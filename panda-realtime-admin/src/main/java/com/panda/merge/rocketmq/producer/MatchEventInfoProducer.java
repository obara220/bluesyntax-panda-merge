package com.panda.merge.rocketmq.producer;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Lists;
import com.panda.merge.cache.CommonItem;
import com.panda.merge.cache.FootballCacheScores;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.*;
import com.panda.merge.common.utils.CommUtils;
import com.panda.merge.component.InitializeComponent;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.config.ThreadPoolConfig;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dto.MatchEventInfoDetail;
import com.panda.merge.dto.MatchEventInfoWarnNoticeDto;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.DataMerchantMessage;
import com.panda.merge.dto.message.MarketSuspendMessage;
import com.panda.merge.dto.message.MatchEventInfoMessage;
import com.panda.merge.mapper.MatchEventInfoScoresMapper;
import com.panda.merge.model.*;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.config.RedisConfig.REDIS_HOUR_TIME;
import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 下发三方赛事事件信息到下游( 赛程统计,业务，风控)
 *
 * @author tell
 * @since 2020年11月26日20:11:49
 */
@Slf4j
@Component
public class MatchEventInfoProducer {

//    @Autowired
//    private RocketMQTemplate rocketMqTemplate;

    @Autowired
    public RedisService redisService;

    @Autowired
    public BaseProcessor baseProcessor;

    @Autowired
    private FtsMatchRelationService ftsMatchRelationService;

    @Autowired
    private MatchEventInfoService matchEventInfoService;

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    @Autowired
    private MatchSaleOverProducer matchSaleOverProducer;

    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;

    @Autowired
    private StandardMatchTeamRelationService standardMatchTeamRelationService;

    @Autowired
    private ThreadPoolConfig threadPoolConfig;

    @Autowired
    public RealtimeBaseProduecr realtimeBaseProduecr;

    /**
     * panda数据库状态是否异常（false:否，true:是）
     * */
    @NacosValue(value = "${panda.db.error.realtime:false}", autoRefreshed = true)
    private Boolean pandaDbIsError;

    /**
     * FTS赛事完赛状态有效时长(小时)
     */
    @NacosValue(value = "${fts.matchOverStatus.durationTime:24}", autoRefreshed = true)
    private Integer ftsDurationTime;

    /**
     * 往S02数据源投递事件,需要验证的赛事信息数据源
     * 因为测试环境无S02赛事，需要用其它数据源赛事来验证
     */
    @NacosValue(value = "${s02.source.code:SK}", autoRefreshed = true)
    private String s02MatchSourceCode;

    /**
     * 对S02开放的赛种，1:足球，2:篮球
     * 网球/羽毛球/乒乓球/棒球/美式足球/排球/斯诺克
     *  优化单:94733   橄榄球14/手球11/曲棍球15
     */
    @NacosValue(value = "${s02.sport.id:1,2,3,5,6,7,8,9,10,11,14,15}", autoRefreshed = true)
    private String s02SportId;

    /**
     * A01需要消费的标准事件赛种类型
     * 获取A01赛事信息,需要单独获取A01赛事ID,
     * 目前只需要1 足球,2 篮球aoThirdMatchSourceId
     * 8 乒乓球(需求:3393)
     */
    @NacosValue(value = "${a01.sport.id:1,2,8}", autoRefreshed = true)
    private String a02SportId;

    /** fts事件开关  false:关，true：开*/
    @NacosValue(value = "${fts.event.switch:true}", autoRefreshed = true)
    private boolean ftsEventSwitch;

    /**自研动画Z01事件是否下发开关*/
    @NacosValue(value = "${animation.event.switch:true}", autoRefreshed = true)
    private boolean animationEventSwitch;

    /**
     *  自研动画Z01不支持数据源
     *  BUG：102859 【生产】【产品】Z01动画不使用事件质量较差的数据源事件
     * */
    @NacosValue(value = "${animation.event.not.data.code:BC,LS,TS,N01,N02,N03}", autoRefreshed = true)
    private String animationEventNotDataCode;

    /**自研动画Z01支持赛种*/
    @NacosValue(value = "${animation.event.sport:1,2}", autoRefreshed = true)
    private String animationEventSport;

    /**自研动画Z01关键事件编码(足球：需求3795)*/
    @NacosValue(value = "${animation.event.1.code:goal}", autoRefreshed = true)
    private String animationEventCode1;

    @NacosValue(value = "${animation.event.1.code.change:{possible_var:possible_video_assistant_referee}}", autoRefreshed = true)
    private String animationEventCodeChange1;

    /**自研动画Z01关键事件编码(篮球：需求3833)*/
    @NacosValue(value = "${animation.event.2.code:goal}", autoRefreshed = true)
    private String animationEventCode2;

    @NacosValue(value = "${animation.event.2.code.change:{possession_arrow:possession,personal_foul:foul}}", autoRefreshed = true)
    private String animationEventCodeChange2;


    /**
     * 推送三方赛事事件到队列 THIRD_MATCH_EVENT_INFO
     */
    public void pushThirdMatchEvent(String linkId, MatchEventInfo matchEventInfo, ThirdMatchInfo thirdMatchInfo) {
        //如果是V02的UOF事件则是视频集锦，此处无需下发,需求：2409
        if (DataSourceCodeEnum.TS.code.equalsIgnoreCase(thirdMatchInfo.getDataSourceCode()) && ZERO.equals(matchEventInfo.getSourceType())) {
//            log.info("linkId=【{}】 topic=THIRD_MATCH_EVENT_INFO赛事{}V02集锦事件无需下发到三方事件队列！", linkId,matchEventInfo.getThirdMatchId());
            return;
        }
        //单号：87828 赛事阶段100，999延迟处理
        if(EventCodeEnum.MATCH_STATUS.code.equalsIgnoreCase(matchEventInfo.getEventCode()) &&
                (MatchPeriodForMatchOverEnum.Ended.value.equals(matchEventInfo.getMatchPeriodId())) || MatchPeriodForMatchOverEnum.Ended999.value.equals(matchEventInfo.getMatchPeriodId())){
            realtimeBaseProduecr.syncSend(matchEventInfo,linkId,THIRD_MATCH_EVENT_INFO,matchEventInfo.getThirdMatchSourceId()+"",matchEventInfo.getDataSourceCode(),TWO);
        }else{
            realtimeBaseProduecr.send(matchEventInfo,linkId,THIRD_MATCH_EVENT_INFO,matchEventInfo.getThirdMatchSourceId()+"",matchEventInfo.getDataSourceCode());
        }
        log.info("linkId=【{}】组装数据源赛事{}事件并下发完成,topic={},", linkId, matchEventInfo.getThirdMatchId(),THIRD_MATCH_EVENT_INFO);
    }


    /**
     * 推送生成标准赛事事件到队列 MATCH_EVENT_INFO_TO_RISK
     *
     * @param isReissue 是否补发事件(true:切换事件源，或者延迟消费的事件，false:开售事件，或者正常通道下发事件）
     */
    public void pushMatchEventDataToRisk(String linkId, List<MatchEventInfo> originalMatchEventInfos, ThirdMatchInfo thirdMatchInfo, boolean isReissue) {
        if (CollectionUtils.isEmpty(originalMatchEventInfos)){
            log.info("linkId=【{}】pushMatchEventDataToRisk, matchEventInfoListPush为空, 三方赛事原始id={}, isReissue={}",
                    linkId, thirdMatchInfo.getThirdMatchSourceId(),isReissue);
            return;
        }
        //需要过滤掉uof事件.0:UOF;1:liveData
        List<MatchEventInfo> resList = originalMatchEventInfos.stream().filter(obj -> !ZERO.equals(obj.getSourceType())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(resList)) {
            //转换后的事件
            List<MatchEventInfo> matchEventInfos = baseProcessor.matchHomeAwayExchange(originalMatchEventInfos, thirdMatchInfo);
            pushMatchEventList2Mq(linkId, matchEventInfos, thirdMatchInfo, MATCH_EVENT_INFO_TO_RISK, isReissue);
            log.info("linkId=【{}】pushMatchEventDataToRisk，推送事件到队列 MATCH_EVENT_INFO_TO_RISK结束，三方赛事原始id={},isReissue={},自研动画Z01事件是否下发开关={}"
                    , linkId,thirdMatchInfo.getThirdMatchSourceId(),isReissue,animationEventSwitch);

            /**
             * 自研动画Z01相关处理逻辑
             * 足球：需求3795
             * BUG：102859 【生产】【产品】Z01动画不使用事件质量较差的数据源事件
             * isReissue = true:切换事件源，或者延迟消费的事件 无需处理
             */
            if(!isReissue && animationEventSwitch){
                //需要处理的赛种
                List<String> animationSportIds = Arrays.asList(animationEventSport.split(","));
                //不支持数据源
                List<String> animationEventNotDataCodes = Arrays.asList(animationEventNotDataCode.split(","));
                if(animationSportIds.contains(thirdMatchInfo.getSportId().toString()) && !animationEventNotDataCodes.contains(thirdMatchInfo.getDataSourceCode())){
                    TaskExecutor taskExecutor = threadPoolConfig.getMatchThreadPool();
                    taskExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            matchEventList3795ToMq(linkId,matchEventInfos,thirdMatchInfo);
                        }
                    });
                }else{
                    log.info("linkId=【{}】pushMatchEventDataToRisk，无需推送Z01动画事件,单号-102859,三方赛事原始id={},赛种={},数据源编码={},需要处理赛种={},不支持数据源={}"
                            , linkId,thirdMatchInfo.getThirdMatchSourceId(),thirdMatchInfo.getSportId(),thirdMatchInfo.getDataSourceCode(),animationEventSport,animationEventNotDataCode);
                }
            }
        }

        //如果是TS赛事事件则是视频集锦，特殊下发,需求：2409
        if (DataSourceCodeEnum.TS.code.equalsIgnoreCase(thirdMatchInfo.getDataSourceCode())) {
            //如果是V02的UOF事件则是视频集锦，需要单独下发,需求：2409
            List<MatchEventInfo> resList_0 = originalMatchEventInfos.stream().filter(obj -> ZERO.equals(obj.getSourceType())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(resList_0)) {
                //转换后的事件
                List<MatchEventInfo> matchEventInfos = baseProcessor.matchHomeAwayExchange(originalMatchEventInfos, thirdMatchInfo);
                pushMatchEventList2Mq(linkId, matchEventInfos, thirdMatchInfo, MATCH_EVENT_INFO_VIDEO, isReissue);
            }
        }
    }

    /**
     * 推送生成标准赛事并事件源编码一致的事件到 MATCH_EVENT_INFO
     *
     * @param isReissue 是否补发事件(true:切换事件源，或者延迟消费的事件，false:开售事件，或者正常通道下发事件）
     * @param isNormalChannel 是否正常标准事件投递通道(true:正常标准事件通道,false:切换事件源或者中途开售)
     */
    public void pushMatchEventData(String linkId, List<MatchEventInfo> originalMatchEventInfos,
                                   ThirdMatchInfo thirdMatchInfo, boolean isReissue, boolean isNormalChannel) {
        if (CollectionUtils.isEmpty(originalMatchEventInfos)){
            log.info("linkId=【{}】pushMatchEventData, matchEventInfoListPush为空, s02MatchSourceCode={}, s02SportId={}",
                    linkId, s02MatchSourceCode, s02SportId);
            return;
        }
        //转换后的事件
        List<MatchEventInfo> matchEventInfos = baseProcessor.matchHomeAwayExchange(originalMatchEventInfos, thirdMatchInfo);

        //开售后足球标准事件需要特殊处理的事件逻辑
        standardMatchEvent1Processor(linkId, matchEventInfos, thirdMatchInfo, isNormalChannel);

        //根据 SourceType数据来源类型.0:UOF;1:liveData,来分组事件，
        Map<Integer, List<MatchEventInfo>> sourceType2List = matchEventInfos.stream().collect(Collectors.groupingBy(obj -> obj.getSourceType()));
        if (sourceType2List.size() > 1) {
            for (Integer sourceType : sourceType2List.keySet()) {
                pushMatchEventList2Mq(linkId + "_" + sourceType, sourceType2List.get(sourceType), thirdMatchInfo, MATCH_EVENT_INFO, isReissue);
            }
        } else {
            pushMatchEventList2Mq(linkId, matchEventInfos, thirdMatchInfo, MATCH_EVENT_INFO, isReissue);
        }
        log.info("linkId=【{}】组装标准事件并下发完成,topic=MATCH_EVENT_INFO，下发条数={},s02MatchSourceCode={},s02SportId={}", linkId, matchEventInfos.size(), s02MatchSourceCode, s02SportId);

        try {
            if(ftsEventSwitch){
                //2550 【A01】【操盘风控】足球-范特西联赛
                if(StandardSportTypeEnum.FootBall.getCode().equals(thirdMatchInfo.getSportId())){
                    //FTS事件相关处理逻辑，切换事件源无需补发事件到FTS
                    if (!isReissue || (isReissue && isNormalChannel)) {
                        log.info("linkId=【{}】组装FTS事件开始,标准赛事ID={}", linkId,thirdMatchInfo.getReferenceId());
                        realtimeBaseProduecr.send(matchEventInfos,linkId,MATCH_EVENT_INFO_FTS,thirdMatchInfo.getReferenceId()+"",DataSourceCodeEnum.FTS.getCode());
                    }
                }
            }
        } catch (Exception e) {
            log.error("linkId=【"+linkId+"】组装FTS事件异常,Exception:", e);
        } finally {
            log.info("linkId=【{}】组装FTS事件结束", linkId);
        }

//        try {
//            //对S02开发的赛种
//            List<String> s02SportIds = Arrays.asList(s02SportId.split(","));
//            if (s02SportIds.contains(String.valueOf(thirdMatchInfo.getSportId()))) {
//                //SK相关事件特殊下发
//                ThirdMatchInfo skItem = thirdMatchInfoService.getItem(thirdMatchInfo.getReferenceId(), s02MatchSourceCode);
//                if (null != skItem) {
//                    log.info("linkId=【{}】组装SK事件下发开始,topic=" + MATCH_EVENT_INFO_SK + "，下发条数={}", linkId, matchEventInfos.size());
//                    try {
//                        if (sourceType2List.size() > 1) {
//                            for (Integer sourceType : sourceType2List.keySet()) {
//                                pushMatchEventList2Mq(linkId + "_" + sourceType, sourceType2List.get(sourceType), thirdMatchInfo, MATCH_EVENT_INFO_SK, isReissue);
//                            }
//                        } else {
//                            pushMatchEventList2Mq(linkId, matchEventInfos, thirdMatchInfo, MATCH_EVENT_INFO_SK, isReissue);
//                        }
//                    } catch (Exception e) {
//                        log.error("::" + linkId + "::组装SK事件异常,Exception:", e);
//                    }
//                    log.info("linkId=【{}】组装SK事件下发完成,topic=" + MATCH_EVENT_INFO_SK + "，下发条数={}", linkId, matchEventInfos.size());
//                }
//            }
//        } catch (Exception e) {
//            log.error("::" + linkId + "::组装SK事件事件异常,Exception:", e);
//        } finally {
//            log.info("linkId=【{}】组装SK事件事件结束", linkId);
//        }
    }


    /*
     * 需求2550 FTS范特西赛事事件处理
     *  筛选原始赛事相同球队事件和公共事件
     * @param standardMatchId  原始标准赛事ID
     */
    public void handleFtsMatchEventInfo(String linkId, List<MatchEventInfo> matchEventInfos, Long standardMatchId) {
        //范特西赛事事件转换
        List<FtsMatchRelation> ftsMatchRelations = ftsMatchRelationService.getFtsMatchRelation(standardMatchId);
        if (CollectionUtils.isEmpty(ftsMatchRelations)) {
            log.info("linkId=【{}】FTS事件,原始赛事ID={}下事件所属标准赛事不存在对应的FTS赛事,不需要下发！", linkId, standardMatchId);
            return;
        }
        //范特西赛事需要处理比分的事件类型集合
        List<String> eventCodeList = EventCodeEnum.getFtsScoresEventCodes();
        for (FtsMatchRelation ftsMatchRelation : ftsMatchRelations) {
            //FTS标准赛事ID
            Long ftsStandardMatchId = ftsMatchRelation.getNewMatchId();
            StandardMatchInfo ftsStandardMatchInfo = standardMatchInfoService.getItem(ftsStandardMatchId);
            //需要修改的数据
            StandardMatchInfo upStandardMatchInfo = new StandardMatchInfo();
            upStandardMatchInfo.setId(ftsStandardMatchInfo.getId());
            //获取FTS赛事信息,需要单独获取FTS赛事ID,FTS三方赛事默认使用FTS赛事
            ThirdMatchInfo ftsItem = thirdMatchInfoService.getItem(ftsStandardMatchInfo.getDataSourceCode(), ftsStandardMatchInfo.getThirdMatchSourceId());
            if (null == ftsItem) {
                log.info("linkId=【{}】FTS事件,赛事ID={}下没有找到对应FTS三方赛事,不需要下发！", linkId, ftsStandardMatchId);
                return;
            }
            //原始赛事在FTS赛事中时主队还是客队
            String ftsHomeAway = standardMatchId.equals(ftsMatchRelation.getNewHomeMatchId()) ? TeamTypeEnum.HOME.code : TeamTypeEnum.AWAY.code;
            //标准球队id
            Long standardTeamId;
            if (TeamTypeEnum.HOME.code.equals(ftsHomeAway)) {
                standardTeamId = ftsMatchRelation.getNewHomeTeamId();
            } else {
                standardTeamId = ftsMatchRelation.getNewAwayTeamId();
            }
            //原始赛事球队关系
            Map<String, StandardMatchTeamRelation> position2ItemByStandardMatchId = standardMatchTeamRelationService.getPosition2ItemByStandardMatchId(standardMatchId);
//            log.info("::"+linkId+"::范特西母赛事与球队对应关系={}", JSON.toJSONString(position2ItemByStandardMatchId));
            List<MatchEventInfo> newMatchEventList = new LinkedList<>();
            for (MatchEventInfo item : matchEventInfos) {
                //全场比分需要两场母赛事都结束后才下发最终比分。（999,100）
                Long matchPeriodId = item.getMatchPeriodId();
                if (EventCodeEnum.MATCH_STATUS.code.equals(item.getEventCode())) {
                    //999,100 FTS赛事阶段特殊处理
                    if (MatchPeriodForMatchOverEnum.Ended.value.equals(matchPeriodId) || MatchPeriodForMatchOverEnum.Ended999.value.equals(matchPeriodId)) {
                        String redisKey = "FTS_MATCH_PERIOD:" + ftsStandardMatchInfo.getId() + ":" + item.getEventCode() + ":" + matchPeriodId + ":";
                        MatchEventInfo cacheMatchEventInfo = null;
                        if (TeamTypeEnum.HOME.code.equals(ftsHomeAway)) {
                            cacheMatchEventInfo = (MatchEventInfo) redisService.get(redisKey + TeamTypeEnum.AWAY.code);
                        }
                        if (TeamTypeEnum.AWAY.code.equals(ftsHomeAway)) {
                            cacheMatchEventInfo = (MatchEventInfo) redisService.get(redisKey + TeamTypeEnum.HOME.code);
                        }
                        log.info("::" + linkId + "::FTS事件,redisKey={},ftsHomeAway={},cacheMatchEventInfo是否为空={}", redisKey, ftsHomeAway, null == cacheMatchEventInfo);
                        //如果另一个母赛事赛事阶段为空，表示当前FTS赛事是第一次收到赛事阶段事件，第一次不需要下发
                        if (null == cacheMatchEventInfo) {
                            redisService.set(redisKey + ftsHomeAway, item, (long) ftsDurationTime * REDIS_HOUR_TIME);
                            continue;
                        }
                    }
                }
                //封装FTS事件
                MatchEventInfo matchEventInfo = new MatchEventInfo();
                BeanUtils.copyProperties(item, matchEventInfo);
                //如果原始赛事事件主客队标识 和 FTS赛事事件主客队标识不一致，则需要特殊处理
                if (!ftsHomeAway.equals(matchEventInfo.getHomeAway())) {
                    //如果当前主客队标识是主队，表示在FTS中是客队，需要对调比分
                    if (TeamTypeEnum.HOME.code.equals(matchEventInfo.getHomeAway())) {
                        matchEventInfo.setT2(matchEventInfo.getT1());
                    }
                    if (TeamTypeEnum.AWAY.code.equals(matchEventInfo.getHomeAway())) {
                        matchEventInfo.setT1(matchEventInfo.getT2());
                    }
                }

                //母赛事标准赛事球队关系
                StandardMatchTeamRelation standardMatchTeamRelation = position2ItemByStandardMatchId.get(item.getHomeAway());
                log.info("::" + linkId + "::FTS事件,原始事件主客场={},fts事件主客场={},母赛事标准赛事球队关系是否为空={}", item.getHomeAway(), ftsHomeAway, null == standardMatchTeamRelation);
                if (null != standardMatchTeamRelation) {
                    matchEventInfo.setStandardTeamId(standardMatchTeamRelation.getStandardTeamId());
                    matchEventInfo.setHomeAway(ftsHomeAway);
                }
                newMatchEventList.add(matchEventInfo);
            }
            //筛选出FTS相关的事件,筛选原始赛事相同球队事件和公共事件，kick_off事件必须下发
            List<MatchEventInfo> ftsMatchEventList = newMatchEventList.stream().filter(
                    obj -> null == obj.getStandardTeamId() || obj.getStandardTeamId().equals(standardTeamId)
                            || EventCodeEnum.MATCH_STATUS.code.equals(obj.getEventCode()) || EventCodeEnum.KICK_OFF.code.equals(obj.getEventCode())
            ).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(ftsMatchEventList)) {
                log.info("linkId=【{}】FTS事件,当前无FTS事件下发,handleFtsMatchEventInfo", linkId);
                return;
            }
            //94032
            //过滤阶段未点球大赛和加时上半场，加时下半场的进球事件
            int eventCount = ftsMatchEventList.size();
            ftsMatchEventList = ftsMatchEventList.stream().filter(v ->!(
                    (MatchPeriodForMatchOverEnum.PENALTY_SHOOTOUT.value.equals(v.getMatchPeriodId())
                    || MatchPeriodForMatchOverEnum.OverTime_1H.value.equals(v.getMatchPeriodId())
                    || MatchPeriodForMatchOverEnum.OverTime_2H.value.equals(v.getMatchPeriodId()))
                    && "goal".equals(v.getEventCode())
                    )
            ).collect(Collectors.toList());
            log.info("linkId=【{}】FTS事件,过滤点球大战和加时赛的进球{}条,handleFtsMatchEventInfo",linkId,eventCount-ftsMatchEventList.size());
            if (CollectionUtils.isEmpty(ftsMatchEventList)) {
                log.info("linkId=【{}】FTS事件,当前无FTS事件下发,handleFtsMatchEventInfo,2", linkId);
                return;
            }
            log.info("linkId=【{}】FTS事件,组装FTS事件并下发开始,topic=MATCH_EVENT_INFO，下发条数={}", linkId, ftsMatchEventList.size());
            //判断当前赛事是否是完赛阶段
            StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(ftsStandardMatchId);
            //FTS事件列表处理
            for (MatchEventInfo matchEventInfo : ftsMatchEventList) {
                Long matchPeriodId = matchEventInfo.getMatchPeriodId();
                if (MatchPeriodForMatchOverEnum.Ended999.value.equals(matchPeriodId)) {
                    if (null != standardSportMarketSell) {
                        upStandardMatchInfo.setMatchOver(YesNoEnum.Y.value);
                        //通知预售开售 赛事完赛消息
                        matchSaleOverProducer.sendMatchSaleOverMessage(linkId, ftsStandardMatchInfo);
                    }
                }
                //范特西赛事信息处理
                upStandardMatchInfo.setMatchPeriodId(matchEventInfo.getMatchPeriodId());
                upStandardMatchInfo.setSecondsMatchStart(Math.toIntExact(matchEventInfo.getSecondsFromStart()));
                upStandardMatchInfo.setSecondsMatchModifyTime(matchEventInfo.getModifyTime());
                //范特西事件转换
                matchEventInfo.setId(UUIdUtils.getId());
                matchEventInfo.setLinkId(linkId);
                matchEventInfo.setThirdMatchId(ftsItem.getId());
                matchEventInfo.setThirdMatchSourceId(ftsItem.getThirdMatchSourceId());
                matchEventInfo.setStandardMatchId(ftsStandardMatchId);
                matchEventInfo.setDataSourceCode(ftsItem.getDataSourceCode());
                //范特西比分处理
                if (eventCodeList.contains(matchEventInfo.getEventCode())) {
                    extractedEventT1T2Value(matchEventInfo, linkId);
                    log.info("linkId=【{}】FTS事件,homeAway={},缓存当前事件信息,eventCode={},matchPeriodId={},t1={},t2={}", linkId, ftsHomeAway, matchEventInfo.getEventCode(), matchEventInfo.getMatchPeriodId(), matchEventInfo.getT1(), matchEventInfo.getT2());
                    if (EventCodeEnum.GOAL.code.equals(matchEventInfo.getEventCode())) {
                        //记录进球总比分
                        redisService.set(matchEventInfo.getStandardMatchId() + ":" + ftsHomeAway + ":" + matchEventInfo.getEventCode(), matchEventInfo, (long) ftsDurationTime * REDIS_HOUR_TIME);
                    }
                    redisService.set(matchEventInfo.getStandardMatchId() + ":" + ftsHomeAway + ":" + matchEventInfo.getEventCode() + ":" + matchEventInfo.getMatchPeriodId(), matchEventInfo, (long) ftsDurationTime * REDIS_HOUR_TIME);
                }
            }
            //事件下发(业务 风控)
            pushMatchEventList2Mq(linkId, ftsMatchEventList, ftsItem, MATCH_EVENT_INFO, false);
            //事件下发(比分)
            pushMatchEventList2Mq(linkId, ftsMatchEventList, ftsItem, MATCH_EVENT_INFO_TO_RISK, false);
            //范特西事件保存
            saveMatchEventInfoList(linkId,ftsMatchEventList,ftsItem);
            //范特西赛事信息更新，和库中赛事阶段不一致才更新
            if (!ftsStandardMatchInfo.getMatchPeriodId().equals(upStandardMatchInfo.getMatchPeriodId())){
                BeanUtil.copyProperties(upStandardMatchInfo,ftsStandardMatchInfo, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
                realtimeBaseProduecr.send(upStandardMatchInfo,linkId,DATA_STANDARD_MATCH_INFO_DB,ftsStandardMatchInfo.getId()+"",ftsStandardMatchInfo.getDataSourceCode());
            }
            log.info("linkId=【{}】FTS事件,组装FTS事件并下发完成,topic=MATCH_EVENT_INFO，下发条数={}", linkId, ftsMatchEventList.size());
        }
    }

    /**
     * 获取范特西事件类型最近一次比分值
     * 当前为范特西主队事件，则取出事件类型对应客队最近一次比分值
     * 当前为范特西客队事件，则取出事件类型对应主队最近一次比分值
     *
     * @param matchEventInfo 范特西事件
     */
    private void extractedEventT1T2Value(MatchEventInfo matchEventInfo, String linkId) {
        log.info("{}::FTS赛事信息需要处理比分，赛事编码为: {},原始比分t1={},t2={}", linkId, matchEventInfo.getEventCode(), matchEventInfo.getT1(), matchEventInfo.getT2());
        //match_status，kick_off 单独处理，取进球的主、客队缓存信息中得分值
        if (EventCodeEnum.MATCH_STATUS.code.equals(matchEventInfo.getEventCode()) || EventCodeEnum.KICK_OFF.code.equals(matchEventInfo.getEventCode())) {
            if (EventCodeEnum.MATCH_STATUS.code.equals(matchEventInfo.getEventCode())) {
                MatchEventInfo matchEventInfoHome = (MatchEventInfo) redisService.get(matchEventInfo.getStandardMatchId() + ":home:goal");
                MatchEventInfo matchEventInfoAway = (MatchEventInfo) redisService.get(matchEventInfo.getStandardMatchId() + ":away:goal");
                matchEventInfo.setT1(null != matchEventInfoHome ? matchEventInfoHome.getT1() : 0);
                matchEventInfo.setT2(null != matchEventInfoAway ? matchEventInfoAway.getT2() : 0);
            }
            if (EventCodeEnum.KICK_OFF.code.equals(matchEventInfo.getEventCode())) {
                MatchEventInfo matchEventInfoHome = (MatchEventInfo) redisService.get(matchEventInfo.getStandardMatchId() + ":home:goal:" + matchEventInfo.getMatchPeriodId());
                MatchEventInfo matchEventInfoAway = (MatchEventInfo) redisService.get(matchEventInfo.getStandardMatchId() + ":away:goal:" + matchEventInfo.getMatchPeriodId());
                matchEventInfo.setT1(null != matchEventInfoHome ? matchEventInfoHome.getT1() : 0);
                matchEventInfo.setT2(null != matchEventInfoAway ? matchEventInfoAway.getT2() : 0);
            }
        } else {
            String newHomeAway;
            if (TeamTypeEnum.HOME.code.equals(matchEventInfo.getHomeAway())) {
                newHomeAway = TeamTypeEnum.AWAY.code;
            } else {
                newHomeAway = TeamTypeEnum.HOME.code;
            }
            String key = matchEventInfo.getStandardMatchId() + ":" + newHomeAway + ":" + matchEventInfo.getEventCode() + ":" + matchEventInfo.getMatchPeriodId();
            MatchEventInfo result = (MatchEventInfo) redisService.get(key);
            if (null == result) {
                MatchEventInfo searchMatchEvent = new MatchEventInfo();
                BeanUtils.copyProperties(matchEventInfo, searchMatchEvent);
                searchMatchEvent.setHomeAway(newHomeAway);
                result = matchEventInfoService.getMatchEventInfo(searchMatchEvent);
                log.info("{}::FTS事件,newHomeAway={}, FTS赛事事件查询数据库中最近的一条{}事件,是否存在={}", linkId, newHomeAway, matchEventInfo.getEventCode(), null != result);
            }
            if (null != result) {
                log.info("{}::FTS事件,newHomeAway={},FTS赛事上一次事件信息,t1={},t2={}", linkId, newHomeAway, result.getT1(), result.getT2());
                if (TeamTypeEnum.HOME.code.equals(newHomeAway)) {
                    matchEventInfo.setT1(result.getT1());
                } else {
                    matchEventInfo.setT2(result.getT2());
                }
            } else {
                log.info("{}::FTS事件,newHomeAway={},FTS赛事上一次事件信息为空！", linkId, newHomeAway);
                if (TeamTypeEnum.HOME.code.equals(newHomeAway)) {
                    matchEventInfo.setT1(ZERO);
                } else {
                    matchEventInfo.setT2(ZERO);
                }
            }
        }
    }

    @Autowired
    private MatchEventInfoScoresMapper matchEventInfoScoresMapper;

    /**
     * 开售后足球标准事件需要特殊处理的事件逻辑
     * @param isNormalChannel 是否正常标准事件投递通道
     */
    public void standardMatchEvent1Processor(String linkId, List<MatchEventInfo> matchEventInfos, ThirdMatchInfo thirdMatchInfo, boolean isNormalChannel) {
        //目前只处理足球
        if (StandardSportTypeEnum.FootBall.getCode().equals(thirdMatchInfo.getSportId())) {
            /**
             * 103497 【生产】【产品】【操盘风控】足球-常规时间状态源-异常下发结束优化
             * 缓存最近一条足球标准事件
             * */
            MatchEventInfo lastEvent = matchEventInfos.get(matchEventInfos.size() - 1);
            String standardEventLastKey = String.format(ConstantSystem.getStandardEventLastKey(), lastEvent.getStandardMatchId());
            redisService.set(standardEventLastKey,lastEvent,RedisConfig.REDIS_HOUR_TIME);

            //比分相关事件编码
            List<String> scoresEventCodes = EventCodeEnum.getScoresEventCodes();
            for (MatchEventInfo matchEventInfo : matchEventInfos) {
                try {
                    //此处必须是正常下发事件才需要执行，因为开售或者切换事件源在reissueEventInfo方法中已经提前缓存了
                    if (isNormalChannel) {
                        //如果是进球，角球，红牌，黄牌事件
                        if (scoresEventCodes.contains(matchEventInfo.getEventCode())) {
                            matchEventScores2Redis(linkId, matchEventInfo, thirdMatchInfo);
                        }
                    }

                    //需求编号：1549,单独存储标准事件比分事件（目前只需要足球）
                    if (EventCodeEnum.GOAL.code.equals(matchEventInfo.getEventCode())) {
                        MatchEventInfoScores matchEventScores = new MatchEventInfoScores();
                        //两个对象字段完全一致，直接拷贝熟悉
                        BeanUtils.copyProperties(matchEventInfo, matchEventScores);
                        int matchEventScoresNum = matchEventInfoScoresMapper.insert(matchEventScores);
                        log.info("linkId=【{}】MatchEventInfoScores入库是否成功(0:否,1：是)={}，三方赛事原始id={}", matchEventScores.getLinkId(), matchEventScoresNum, matchEventScores.getThirdMatchSourceId());
                    }

                    //需求：2070 如果是赛事阶段事件(上半场，下半场阶段事件需要缓存)
                    if (EventCodeEnum.MATCH_STATUS.code.equalsIgnoreCase(matchEventInfo.getEventCode())) {
                        if (NUM6.equals(matchEventInfo.getMatchPeriodId().intValue()) ||
                                NUM7.equals(matchEventInfo.getMatchPeriodId().intValue())) {
                            String cacheStatusKey = "STANDARD_MATCH_STATUS_2070:" + thirdMatchInfo.getReferenceId();
                            Map<String, Long> matchPeriodId2Time = redisService.hGetAll(cacheStatusKey);
                            if (null == matchPeriodId2Time) {
                                matchPeriodId2Time = new HashMap<>();
                            }
                            matchPeriodId2Time.put(matchEventInfo.getMatchPeriodId().toString(), matchEventInfo.getEventTime());
                            redisService.hSetAll(cacheStatusKey, matchPeriodId2Time, FIVES * REDIS_HOUR_TIME);
                        }
                    }
                } catch (Exception e) {
                    log.info("::" + linkId + "::standardMatchEvent1Processor，缓存" + matchEventInfo.getEventCode() + "比分异常,key = STANDARD_MATCH_SCORES:" + thirdMatchInfo.getReferenceId() + ",Exception={}", e);
                }
            }
        }
    }


    /**
     * 开售或者切换事件源都需要，先清理历史缓存，然后重新缓存当前事件源的事件比分，用于赔率服务基准分计算
     */
    public void standardMatchScores2Redis(String linkId, List<MatchEventInfo> matchEventInfos, ThirdMatchInfo thirdMatchInfo) {
        //目前只处理足球
        if (StandardSportTypeEnum.FootBall.getCode().equals(thirdMatchInfo.getSportId())) {
            //先清理历史缓存
            redisService.del(DigestUtil.md5Hex(Constant.REDIS_KEY.STANDARD_MATCH_SCORES + thirdMatchInfo.getReferenceId()));
            List<String> scoresEventCodes = EventCodeEnum.getScoresEventCodes();
            //然后重新缓存
            for (MatchEventInfo matchEventInfo : matchEventInfos) {
                //2.如果是进球，角球，红牌，黄牌事件
                if (scoresEventCodes.contains(matchEventInfo.getEventCode())) {
                    matchEventScores2Redis(linkId, matchEventInfo, thirdMatchInfo);
                }
            }
        }
    }

    /**
     * 比分类型数据缓存到redis,赔率服务需要用于计算基准分
     */
    public void matchEventScores2Redis(String linkId, MatchEventInfo matchEventInfo, ThirdMatchInfo thirdMatchInfo) {
        try {
            //缓存key
            String cacheScoresKey = DigestUtil.md5Hex(Constant.REDIS_KEY.STANDARD_MATCH_SCORES + thirdMatchInfo.getReferenceId());
            FootballCacheScores footballCacheScores = new FootballCacheScores();
            Object scores = redisService.get(cacheScoresKey);
            if (!Objects.isNull(scores)) {
                try {
                    if (scores instanceof String) {
                        footballCacheScores = JSON.parseObject(scores.toString(),FootballCacheScores.class);
                    } else {
                        footballCacheScores = (FootballCacheScores) scores;
                    }
                    log.info("linkId=【{}】matchEventGoalAndCorner2Redis，获取缓存中比分,key = {},footballCacheScores={}", linkId, cacheScoresKey, JSON.toJSONString(footballCacheScores));
                } catch (Exception e) {
                    log.error("linkId=【"+linkId+"】standardMatchScores2Redis，获取缓存" + matchEventInfo.getEventCode() + "比分异常,key = STANDARD_MATCH_SCORES:" + thirdMatchInfo.getReferenceId() + ",Exception:", e);
                }
            }
            //当前主客队比分
            Integer t1 = matchEventInfo.getT1();
            Integer t2 = matchEventInfo.getT2();
            if (EventCodeEnum.GOAL.code.equals(matchEventInfo.getEventCode())) {
                //获取常规赛比分
                CommonItem goalItem = footballCacheScores.getGoal();
                //是否常规赛标识
                Boolean flag = true;
                //表示加时赛的阶段（需要重新计算，本身含常规赛事比分）
                List<Long> overTimePeriods = Lists.newArrayList(MatchPeriodForMatchOverEnum.OverTime_1H.value, MatchPeriodForMatchOverEnum.OverTime_HT.value, MatchPeriodForMatchOverEnum.OverTime_2H.value);
                if (overTimePeriods.contains(matchEventInfo.getMatchPeriodId())) {
                    if (null != goalItem) {
                        footballCacheScores.setGoalOverTime(new CommonItem(t1 - goalItem.getHome(), t2 - goalItem.getAway()));
                    } else {
                        footballCacheScores.setGoalOverTime(new CommonItem(t1, t2));
                    }
                    flag = false;
                }
                //点球大战（不需要重新计算，本身不含常规赛事比分）
                if (MatchPeriodForMatchOverEnum.PENALTY_SHOOTOUT.value.equals(matchEventInfo.getMatchPeriodId())) {
                    footballCacheScores.setGoalPenalty(new CommonItem(t1, t2));
                    flag = false;
                }
                //常规赛事比分
                if (flag) {
                    footballCacheScores.setGoal(new CommonItem(t1, t2));
                }
            }
            if (EventCodeEnum.CORNER.code.equals(matchEventInfo.getEventCode())) {
                footballCacheScores.setCorner(new CommonItem(t1, t2));
            }
            if (EventCodeEnum.RED_CARD.code.equals(matchEventInfo.getEventCode())) {
                footballCacheScores.setRedCard(new CommonItem(t1, t2));
            }
            if (EventCodeEnum.YELLOW_CARD.code.equals(matchEventInfo.getEventCode())) {
                footballCacheScores.setYellowCard(new CommonItem(t1, t2));
            }
            redisService.set(cacheScoresKey, JSON.toJSONString(footballCacheScores), FIVES * REDIS_HOUR_TIME);
            log.info("linkId=【{}】matchEventGoalAndCorner2Redis，缓存{}比分 ,key = {},footballCacheScores={}", linkId, matchEventInfo.getEventCode(), cacheScoresKey, JSON.toJSONString(footballCacheScores));
        } catch (Exception e) {
            log.info("linkId=【"+linkId+"】standardMatchScores2Redis，缓存" + matchEventInfo.getEventCode() + "比分异常,key = STANDARD_MATCH_SCORES:" + thirdMatchInfo.getReferenceId() + ",Exception={}", e);
        }
    }

    /**
     * 推送事件列表到MQ，统一特殊处理
     *
     * @param isReissue 是否补发事件(true:切换事件源，或者延迟消费的事件，false:开售事件，或者正常通道下发事件）
     */
    public void pushMatchEventList2Mq(String linkId, List<MatchEventInfo> matchEventInfos, ThirdMatchInfo thirdMatchInfo, String topic, boolean isReissue) {
        //事件集合太大，特殊处理
        if (matchEventInfos.size() > TWO * HUNDRED) {
            //分割集合，避免集合太大，投递MQ失败
            List<List<MatchEventInfo>> lists = CommUtils.groupList(matchEventInfos, TWO * HUNDRED);
            for (int i = 0; i < lists.size(); i++) {
                List<MatchEventInfo> list = lists.get(i);
                Request<List<MatchEventInfoMessage>> request = getMessageListBuilder(linkId + "_" + i, list, thirdMatchInfo, isReissue, topic);
                request.setDataType(topic);
                request.setTag(thirdMatchInfo.getReferenceId()+"");
                realtimeBaseProduecr.sendAdminOrSpare(request,thirdMatchInfo,ZERO);

                Long delayTime = SECOND_1;
                if (ConstantSystem.MATCH_EVENT_INFO_TO_RISK.equals(topic)) {
                    delayTime = SECOND_1 * 3;
                }

                try{
                    Thread.sleep(delayTime);
                }catch (Exception e){
                    log.info("linkId=【"+linkId+"】pushMatchEventList2Mq,sleep异常,Exception={}", e);
                }
            }
            log.info("linkId=【{}】组装事件并下发完成,topic={},下发条数={},分批总条数={}", linkId, topic, matchEventInfos.size(), lists.size());
        } else {
            Request<List<MatchEventInfoMessage>> request = getMessageListBuilder(linkId, matchEventInfos, thirdMatchInfo, isReissue, topic);
            request.setDataType(topic);
            request.setTag(thirdMatchInfo.getReferenceId()+"");
            realtimeBaseProduecr.sendAdminOrSpare(request,thirdMatchInfo,ZERO);
            log.info("linkId=【{}】组装事件并下发完成,topic={},下发条数={}", linkId, topic, matchEventInfos.size());
        }
    }

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    /**
     * 组装下游需要事件信息格式
     *
     * @param isReissue 是否补发事件(切换事件源触发的补发事件) true:切换事件源，false:正常事件，开售事件 ，需求 3531，切换事件源标识历史事件，避免前端赛事进行时间在切换过程中乱跳
     * @param topic
     */
    public Request<List<MatchEventInfoMessage>> getMessageListBuilder(String linkId, List<MatchEventInfo> matchEventInfos, ThirdMatchInfo thirdMatchInfo, boolean isReissue, String topic) {
        List<MatchEventInfoMessage> matchEventInfoMessages = new LinkedList<>();
        /** 球员ID前缀*/
        String dataSourceCode = matchEventInfos.get(0).getDataSourceCode();
        String playerIdPrefix = DataSourceCodeEnum.getDataSourceCodeEnumByCode(dataSourceCode).getPlayerIdPrefix();
        /**
         * 获取A01赛事信息,需要单独获取A01赛事ID,
         * 目前只需要足球,篮球aoThirdMatchSourceId
         * 乒乓球(需求:3393)
         * */
        String aoThirdMatchSourceId = null;
        List<String> sportIds = Arrays.asList(a02SportId.split(","));
        if (sportIds.contains(String.valueOf(thirdMatchInfo.getSportId()))) {
            ThirdMatchInfo aoItem = thirdMatchInfoService.getItem(thirdMatchInfo.getReferenceId(), DataSourceCodeEnum.AO.getCode());
            if (null != aoItem) {
                aoThirdMatchSourceId = aoItem.getThirdMatchSourceId();
            }
            log.info("linkId=【{}】【标准事件信息】aoThirdMatchSourceId={},a02SportId={}", linkId, aoThirdMatchSourceId, a02SportId);
        }
        for (MatchEventInfo matchEventInfo : matchEventInfos) {
            MatchEventInfoMessage matchEventInfoMessage = new MatchEventInfoMessage();
            BeanUtils.copyProperties(matchEventInfo, matchEventInfoMessage);
            matchEventInfoMessage.setThirdMatchId(String.valueOf(matchEventInfo.getThirdMatchId()));
            matchEventInfoMessage.setPlayerIdPrefix(playerIdPrefix);
            if (null != thirdMatchInfo) {
                matchEventInfoMessage.setMatchType(thirdMatchInfo.getMatchType());
                if (null == matchEventInfo.getStandardMatchId() || matchEventInfo.getStandardMatchId() == 0) {
                    matchEventInfoMessage.setStandardMatchId(thirdMatchInfo.getReferenceId());
                }
            }
            matchEventInfoMessage.setAoThirdMatchSourceId(aoThirdMatchSourceId);
            //如果是TS赛事事件则是视频集锦，特殊下发
            if (DataSourceCodeEnum.TS.code.equals(thirdMatchInfo.getDataSourceCode())) {
                String remark = matchEventInfo.getRemark();
                if (StringUtils.isNotBlank(remark)) {
                    JSONObject obj = JSON.parseObject(remark);
                    matchEventInfoMessage.setFragmentId(obj.getString("fragmentId"));
                    matchEventInfoMessage.setFragmentCode(obj.getString("fragmentCode"));
                    matchEventInfoMessage.setFragmentVideo(obj.getString("fragmentVideo"));
                    matchEventInfoMessage.setFragmentLength(obj.getString("fragmentLength"));
                    matchEventInfoMessage.setFragmentPic(obj.getString("fragmentPic"));
                }

            }
            //优化单 66013 【产品】【生产】操盘后台新增比赛相关信息
            matchEventInfoMessage.setLiveEventSource(thirdMatchInfo.getLiveEventSource());
            matchEventInfoMessage.setMatchLength(thirdMatchInfo.getMatchLength());
            //需求 3531，切换事件源标识历史事件，避免前端赛事进行时间在切换过程中乱
            matchEventInfoMessage.setIsReissue(isReissue);
//            109522 【产品】【生产】injury_time历史事件不标示补发
            if (isReissue
                    && StandardSportTypeEnum.FootBall.code.equals(matchEventInfo.getSportId())
                    && (ConstantSystem.MATCH_EVENT_INFO.equals(topic) || ConstantSystem.MATCH_EVENT_INFO_TO_RISK.equals(topic))
                    && EventCodeEnum.INJURY_TIME.code.equals(matchEventInfo.getEventCode())
            ) {
                matchEventInfoMessage.setIsReissue(false);
                log.info("补发伤停事件取消补发标记, linkId={}, thirdMatchSourceId={}, thirdEventId={}", linkId, matchEventInfo.getThirdMatchSourceId(), matchEventInfo.getThirdEventId());
            }

            matchEventInfoMessages.add(matchEventInfoMessage);
        }
        Request<List<MatchEventInfoMessage>> request = new Request<>(matchEventInfoMessages,linkId,isReissue,dataSourceCode);
        return request;
    }



    /**
     * 下发异常完赛事件到风控，等待风控确认是否完赛(MATCH_EVENT_INFO_ERROR_END ，使用方：风控)
     */
    public void pushMatchEventErrorEndData(String linkId, MatchEventInfo matchEventInfo, ThirdMatchInfo thirdMatchInfo) {
        MatchEventInfoMessage matchEventInfoMessage = new MatchEventInfoMessage();
        BeanUtils.copyProperties(matchEventInfo, matchEventInfoMessage);
        matchEventInfoMessage.setThirdMatchId(String.valueOf(matchEventInfo.getThirdMatchId()));
        if (null != thirdMatchInfo) {
            matchEventInfoMessage.setMatchType(thirdMatchInfo.getMatchType());
        }
        //IsErrorEndEven=“1”表示是否错误完赛事件（普通足球阶段为999才会使用该字段，0:否，1:是）
        matchEventInfoMessage.setIsErrorEndEvent(ONE);
        realtimeBaseProduecr.send(matchEventInfoMessage,linkId,"MATCH_EVENT_INFO_ERROR_END", thirdMatchInfo.getReferenceId()+"",matchEventInfoMessage.getDataSourceCode());
        log.info("linkId=【{}】组装异常完赛赛事事件并下发完成,topic=MATCH_EVENT_INFO_ERROR_END", linkId);
        //通知风控赛事级别封盘
        switchDataSourceSendRiskMQ(linkId, thirdMatchInfo);
    }

    /**
     * 连续5次比分校验失败，通知封盘
     */
    public void pushScoreValidationError(String linkId, ThirdMatchInfo thirdMatchInfo, Boolean isReissue, Boolean spareMq) {
        MarketSuspendMessage.MessageData messageData = new MarketSuspendMessage.MessageData();
        messageData.setMatchId(thirdMatchInfo.getReferenceId());
        messageData.setSportId(thirdMatchInfo.getSportId());

        MarketSuspendMessage message = new MarketSuspendMessage();
        message.setLinkId(linkId);
        message.setData(messageData);
        message.setDataSourceTime(System.currentTimeMillis());
        message.setDataSourceCode(thirdMatchInfo.getDataSourceCode());
        message.setIsReissue(isReissue);
        message.setSpareMq(spareMq);

        String topic = "RCS_SCORE_INCONSISTENCY_WARNING";
        realtimeBaseProduecr.send(message, linkId,topic,
                thirdMatchInfo.getReferenceId() + "", thirdMatchInfo.getDataSourceCode());

        log.info("linkId=【{}】连续5次比分校验失败，通知封盘,topic={},request={}",
                linkId, topic, JSON.toJSONString(message));
    }

    /**
     * 异常完赛事件下发风控赛事级别封盘（赔率服务处拷贝过来的）
     *
     * @param linkId
     * @param thirdMatchInfo
     */
    public void switchDataSourceSendRiskMQ(String linkId, ThirdMatchInfo thirdMatchInfo) {
        linkId = linkId + "_SWITCH";
        DataMerchantMessage dataMerchantMessage = new DataMerchantMessage();
        dataMerchantMessage.setMatchId(thirdMatchInfo.getReferenceId());
        if (thirdMatchInfo.getSportId().equals(StandardSportTypeEnum.Boxing.code) || thirdMatchInfo.getSportId().equals(StandardSportTypeEnum.WaterPolo.code)) {
            dataMerchantMessage.setStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
        } else {
            dataMerchantMessage.setStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.SUSPENDED);
        }
        //风控定义字段
        dataMerchantMessage.setTradeLevel(1);
        dataMerchantMessage.setLinkedType(21);
        realtimeBaseProduecr.send(dataMerchantMessage,linkId,"RCS_TRADE_UPDATE_MARKET_STATUS", thirdMatchInfo.getReferenceId()+"",thirdMatchInfo.getDataSourceCode());
        log.info("linkId=【{}】组装赛事封盘数据并下发完成,topic=RCS_TRADE_UPDATE_MARKET_STATUS,request={}", linkId, JSON.toJSONString(dataMerchantMessage));
    }


    /**
     * 切换数据源时，下发三方赛事事件信息到下游(MATCH_EVENT_INFO_LIST ，使用方：风控，业务)已废弃，统一在 MATCH_EVENT_INFO下发
     */
    @Deprecated
    public void sendMatchEventInfo(String linkId, Long standardMatchId, List<MatchEventInfoMessage> matchEventInfoMessages) {
        realtimeBaseProduecr.send(matchEventInfoMessages,linkId,"MATCH_EVENT_INFO_LIST", standardMatchId+"",matchEventInfoMessages.get(0).getDataSourceCode());
        log.info("linkId=【{}】组装三方赛事事件并下发完成,topic=MATCH_EVENT_INFO_LIST", linkId);
    }


    /**
     * 需求：3795 （异步处理绑定过足球标准赛事的事件，自研动画需要）
     * 1:主数据源关键事件无需校验直接下发
     * 2:同一标准赛事下发X秒内相同事件只需要下发一次（区分主客队）
     * */
    public void matchEventList3795ToMq(String linkId,List<MatchEventInfo> matchEventInfos,ThirdMatchInfo thirdMatchInfo){
        try {
            log.info("linkId=【{}】matchEventList3795ToMq，推送事件到队列 MATCH_EVENT_INFO_TO_3795 开始，三方赛事原始id={},初始事件条数={}", linkId,thirdMatchInfo.getThirdMatchSourceId(),matchEventInfos.size());
            StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.refreshCache(thirdMatchInfo.getReferenceId());
            if (null == standardSportMarketSell) {
                log.info("linkId=【{}】matchEventList3795ToMq,未找到预开售信息,标准赛事id={}", linkId,thirdMatchInfo.getReferenceId());
                return;
            }
            //商业事件源编码
            String businessEventCode = standardSportMarketSell.getBusinessEvent();
            List<String> eventCode3795List = Arrays.asList(animationEventCode1.split(","));
            JSONObject animationEventCodeChange = JSON.parseObject(animationEventCodeChange1);
            //兼容篮球：需求3833
            if (StandardSportTypeEnum.Basketball.getCode().equals(thirdMatchInfo.getSportId())) {
                eventCode3795List = Arrays.asList(animationEventCode2.split(","));
                animationEventCodeChange = JSON.parseObject(animationEventCodeChange2);
            }
            //需要下发的事件列表
            List<MatchEventInfoMessage> sendList = new ArrayList<>();
            for (MatchEventInfo matchEventInfo: matchEventInfos) {
                MatchEventInfoMessage item = new MatchEventInfoMessage();
                BeanUtil.copyProperties(matchEventInfo, item);
                String eventCode = item.getEventCode();
                //需要转换的code
                if(animationEventCodeChange.containsKey(eventCode)){
                    item.setEventCode(animationEventCodeChange.getString(eventCode));
                    log.info("linkId=【{}】matchEventList3795ToMq,需要转换的code,转换前={},转换后={}", linkId,eventCode,item.getEventCode());
                }
                //是否主事件源
                if(thirdMatchInfo.getDataSourceCode().equalsIgnoreCase(businessEventCode)){
                    item.setAddition10(ONE.toString());
                }else{
                    item.setAddition10(ZERO.toString());
                }

                if (StandardSportTypeEnum.Basketball.getCode().equals(thirdMatchInfo.getSportId())) {
                    if(DataSourceCodeEnum.getPdCodeList().contains(item.getDataSourceCode())){
                        if(EventCodeEnum.MISS_2P.code.equals(eventCode)){
                            item.setExtraInfo(String.valueOf(TWO));
                        }
                        if(EventCodeEnum.MISS_3P.code.equals(eventCode)){
                            item.setExtraInfo(String.valueOf(THREE));
                        }
                        if(EventCodeEnum.SCORE_MISS.code.equals(eventCode) && String.valueOf(ONE).equals(item.getAddition5())){
                            item.setExtraInfo(item.getAddition5());
                        }
                        if(EventCodeEnum.SCORE_MISS.code.equals(eventCode) && "0".equals(item.getExtraInfo())){
                            item.setExtraInfo("1");
                        }
                    }
                }

                //是否关键事件
                boolean standardMatchEventFlag1 = eventCode3795List.contains(eventCode);
                //104504 【生产】【产品】【pc＆h5】自研动画var事件常驻展示
                //var后续事件标记,
                boolean standardMatchEventFlag2 = StandardSportTypeEnum.FootBall.getCode().equals(thirdMatchInfo.getSportId()) && EventCodeEnum.getZ01VarSubsequentEvents().contains(eventCode);
                //可能点球后续事件标记
                boolean standardMatchEventFlag3 = StandardSportTypeEnum.FootBall.getCode().equals(thirdMatchInfo.getSportId()) && EventCodeEnum.getZ01PossiblePenaltySubsequentEvents().contains(eventCode);
                String z01VarFlagKey = String.format(ConstantSystem.getZ01VarFlagKey(), thirdMatchInfo.getReferenceId());
                String z01PossiblePenaltyFlagKey = String.format(ConstantSystem.getZ01PossiblePenaltyFlagKey(), thirdMatchInfo.getReferenceId());
                // 后续事件在有常驻事件下发过的标记才使用主事件源
                if (standardMatchEventFlag2) {
                    Object flagObj = redisService.get(z01VarFlagKey);
                    standardMatchEventFlag2 = flagObj != null;
                }
                if (standardMatchEventFlag3) {
                    Object flagObj = redisService.get(z01PossiblePenaltyFlagKey);
                    standardMatchEventFlag3 = flagObj != null;
                }
                log.info("linkId=【{}】matchEventList3795ToMq, 是否使用主事件源来下发事件,标准赛事id={}, dataSourceCode={}, eventCode={}, standardMatchEventFlag1={}, standardMatchEventFlag2={}, standardMatchEventFlag3={}", linkId,thirdMatchInfo.getReferenceId(), thirdMatchInfo.getDataSourceCode(), eventCode, standardMatchEventFlag1, standardMatchEventFlag2, standardMatchEventFlag3);

                if(standardMatchEventFlag1 || standardMatchEventFlag2 || standardMatchEventFlag3){
                    //主事件源关键事件直接下发
                    if(thirdMatchInfo.getDataSourceCode().equalsIgnoreCase(businessEventCode)){
                        if (StandardSportTypeEnum.FootBall.getCode().equals(thirdMatchInfo.getSportId())) {
                            /**
                             * 删除事件特殊处理  goal，corner，red_card，yellow_card
                             * 转换为          canceled_goal,canceled_corner,canceled_red_card,canceled_yellow_card
                             * */
                            if(item.getCanceled() == 1 && EventCodeEnum.getScoresEventCodes().contains(eventCode)){
                                item.setEventCode("canceled_"+eventCode);
                            }

                            //104504 【生产】【产品】【pc＆h5】自研动画var事件常驻展示
                            //下发了后续事件就移除标记
                            if (standardMatchEventFlag2) {
                                redisService.del(z01VarFlagKey);
                                log.info("linkId=【{}】matchEventList3795ToMq, 常驻var事件满足条件移除标记,标准赛事id={},eventCode={}", linkId,thirdMatchInfo.getReferenceId(),eventCode);
                            }
                            if (standardMatchEventFlag3) {
                                redisService.del(z01PossiblePenaltyFlagKey);
                                log.info("linkId=【{}】matchEventList3795ToMq, 常驻PossiblePenalty事件满足条件移除标记,标准赛事id={},eventCode={}", linkId,thirdMatchInfo.getReferenceId(),eventCode);
                            }

                            //走标准事件,设置标记
                            if (EventCodeEnum.POSSIBLE_VIDEO_ASSISTANT_REFEREE.code.equals(eventCode) ||
                                    EventCodeEnum.VIDEO_ASSISTANT_REFEREE.code.equals(eventCode)
                            ) {
                                redisService.set(z01VarFlagKey,ONE, RedisConfig.REDIS_HOUR_TIME);
                                log.info("linkId=【{}】matchEventList3795ToMq, 常驻var事件满足条件设置标记,标准赛事id={},eventCode={}", linkId,thirdMatchInfo.getReferenceId(),eventCode);
                            } else if (EventCodeEnum.POSSIBLE_PENALTY.code.equals(eventCode)) {
                                redisService.set(z01PossiblePenaltyFlagKey,ONE, RedisConfig.REDIS_HOUR_TIME);
                                log.info("linkId=【{}】matchEventList3795ToMq, 常驻PossiblePenalty事件满足条件设置标记,标准赛事id={},eventCode={}", linkId,thirdMatchInfo.getReferenceId(),eventCode);
                            }
                        }
                        sendList.add(item);
                    } else {
                        log.info("linkId=【{}】matchEventList3795ToMq, 非主事件源不下发,标准赛事id={}, dataSourceCode={}, eventCode={}", linkId,thirdMatchInfo.getReferenceId(), thirdMatchInfo.getDataSourceCode(), eventCode);
                    }
                }else{
                    //单号：87646 非主事件源无需下发的事件
                    if(EventCodeEnum.getZ01NotProcessEventCodes().contains(eventCode)){
                        log.info("linkId=【{}】matchEventList3795ToMq,非主事件源无需下发的事件,标准赛事id={},eventCode={}", linkId,thirdMatchInfo.getReferenceId(),eventCode);
                        continue;
                    }
                    //单号：87646 当前事件阶段和标准阶段不一致，无需下发 , BUG: 97015
                    StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(thirdMatchInfo.getReferenceId());
                    if(!standardMatchInfo.getMatchPeriodId().equals(matchEventInfo.getMatchPeriodId())){
                        log.info("linkId=【{}】matchEventList3795ToMq,当前事件阶段和标准阶段不一致，无需下发,标准赛事id={}", linkId,thirdMatchInfo.getReferenceId());
                        continue;
                    }
                    //同一标准赛事下发X秒内相同事件只需要下发一次（区分主客队）
                    String standardMatch3795Key = String.format(ConstantSystem.getStandardMatch3795Key(), thirdMatchInfo.getReferenceId(),matchEventInfo.getHomeAway(),matchEventInfo.getEventCode());
                    if(validateStandardMatchEventCode(linkId,standardMatch3795Key,thirdMatchInfo.getSportId())){
                        sendList.add(item);
                    }
                }
            }
            if(!CollectionUtils.isEmpty(sendList)){
                realtimeBaseProduecr.send(sendList,linkId,MATCH_EVENT_INFO_TO_3795,thirdMatchInfo.getReferenceId()+"",thirdMatchInfo.getDataSourceCode());
            }
            log.info("linkId=【{}】matchEventList3795ToMq，推送事件到队列 MATCH_EVENT_INFO_TO_3795 结束，三方赛事原始id={},下发事件条数={}", linkId,thirdMatchInfo.getThirdMatchSourceId(),sendList.size());
        }catch (Exception e){
            log.error("linkId=【"+linkId+"】matchEventList3795ToMq，推送事件到队列 MATCH_EVENT_INFO_TO_3795 异常，三方赛事原始id:"+thirdMatchInfo.getThirdMatchSourceId()+",Exception:",e);
        }
    }


    @Autowired
    private SystemTypeDictService systemTypeDictService;

    /**
     * 判断当前事件编码多少秒内（默认5S）是否已经下发
     * @param linkId 当前线路ID
     * @param standardMatch3795Key    标准赛事下事件编码标识
     * @param sportId  赛种ID（不同赛种缓存时间不一样）
     */
    public boolean validateStandardMatchEventCode(String linkId,String standardMatch3795Key,Long sportId) {
        SystemTypeDict systemTypeDict = systemTypeDictService.getItemByCodeAndActive("Z01AnimationEventCacheSeconds", 1);
        int seconds = 5;
        if(systemTypeDict != null){
            try{
                //兼容篮球：需求3833兼容调整后数据格式为 	{"1":5,"seconds":5,"2":15}
                JSONObject jsonObject = JSON.parseObject(systemTypeDict.getDescription());
//                seconds = jsonObject.getInteger("seconds");
                seconds = jsonObject.getInteger(sportId.toString());
            }catch (Exception e){
                log.error("linkId=【"+linkId+"】matchEventList3795ToMq,key={},oldLinkId={},获取配置中事件缓存时间异常,Exception:", e);
            }
        }
        if(!redisService.tryLockOnce(standardMatch3795Key,linkId,seconds)){
            Object oldLinkId = redisService.get(standardMatch3795Key);
            log.info("linkId=【{}】matchEventList3795ToMq,key={},oldLinkId={},当前标准赛事下该事件{}S内已经下发过，无需重复下发！", linkId,standardMatch3795Key,oldLinkId,seconds);
            return false;
        }else{
            log.info("linkId=【{}】matchEventList3795ToMq,key={},当前标准赛事下该事件{}S内未下发过，直接下发！", linkId,standardMatch3795Key,seconds);
        }
        return true;
    }

    /**
     * 事件异步批量入库逻辑
     * */
    public void saveMatchEventInfoList(String linkId,List<MatchEventInfo> matchEventInfoList,ThirdMatchInfo oldThirdMatchInfo) {
        log.info("linkId=【"+linkId+"】saveMatchEventInfoList,事件异步批量入库开始,事件条数={}", matchEventInfoList.size());
        //当前集合大于100直接批量入库
        if(matchEventInfoList.size() > 100 && !pandaDbIsError){
            matchEventInfoService.saveBatch(matchEventInfoList,linkId);
        }else{
            //事件数据MQ异步批量入库
            realtimeBaseProduecr.send(matchEventInfoList,linkId,DATA_MATCHS_EVENT_INFO_DB,oldThirdMatchInfo.getThirdMatchSourceId(),oldThirdMatchInfo.getDataSourceCode());
        }
    }


    @Autowired
    private StandardSportTournamentService standardSportTournamentService;

    @Autowired
    private InitializeComponent initializeComponent;

    /**
     * 优化单79713，需要告警的足球事件模板
     */
    @NacosValue(value = "${alerts.event.code.template}", autoRefreshed = true)
    private String alertsEventCodeTemplate;
    /**
     * 优化单79713，forward event事件条数
     */
    @NacosValue(value = "${alerts.event.forward.size:10}", autoRefreshed = true)
    private String alertsEventForwardSize;

    /**
     * 优化单79713，需要查询该事件最近7条事件，触发告警时需要一起发送,
     * 一次性数据大于7条的不处理（历史事件不处理），正常情况不会有多条事件一起下发
     * 模板：
     * Match ID ：45645114561165
     * League ：英超 England primary
     * Event ：14:12:16 取消进球 Cancel Goal 进行时长（20‘12’‘）
     * Time ：2024-12-15 14:12:16
     * Trader ：Strawberry
     * DataSource：G01
     * <p>
     * 14:12:16 event 1 ：危险进攻code homeAway 进行时长（20‘12’‘）
     * 14:12:16 event 2 ：安全code homeAway 进行时长
     * 14:12:16 event 3 ：半场控球code homeAway 进行时长
     * 14:12:16 event 4 ：角球code homeAway 进行时长
     * 14:12:16 event 5 ：进攻code homeAway 进行时长
     * 14:12:16 event 6 ：任意球code
     * 14:12:16 event 7 ：危险球code
     * 14:12:16 event 8 ：危险球code
     * 14:12:16 event 9 ：危险球code
     * 14:12:16 event 10 ：危险球code
     *
     * @param flag  是否需要告警
     */
    public void alertsEventInfo2MQ(MatchEventInfoDetail alertsEventInfo, StandardMatchInfo standardMatchInfo, String liveTrader, List<MatchEventInfo> matchEventInfoList,boolean flag) {
        try{
            //赛事结束后，手工完赛结束后不在 mango 79713通知 单号：82010
            if(YesNoEnum.Y.value.equals(standardMatchInfo.getMatchOver())){
                log.info("linkId=【{}】当前事件对应标准赛事已经完赛,无需触发事件告警,标准赛事ID={}", alertsEventInfo.getLinkId(),standardMatchInfo.getId());
                return;
            }
            //无需告警只需要存缓存的数据
            if(!flag){
                getAlertsEventInfoListForward10(alertsEventInfo,matchEventInfoList);
                return;
            }
            //4123-操盘风控-紧急事件告警
            List<MatchEventInfo> alertsEventInfoList = getAlertsEventInfoListForward10(alertsEventInfo,matchEventInfoList);
            alertsEventInfoList.add(0,alertsEventInfo);
            //需要告警的集合
            List<MatchEventInfoWarnNoticeDto> warnList = new ArrayList<>();
            for (int i = 0; i < alertsEventInfoList.size(); i++) {
                MatchEventInfo eventItem = alertsEventInfoList.get(i);
                //需要告警的数据
                MatchEventInfoWarnNoticeDto itemDto = new MatchEventInfoWarnNoticeDto();
                itemDto.setMatchId(standardMatchInfo.getId());
                itemDto.setSportId(standardMatchInfo.getSportId());
                itemDto.setDataSourceCode(standardMatchInfo.getDataSourceCode());
                itemDto.setMatchBeginTime(standardMatchInfo.getBeginTime());
                itemDto.setEventCode(eventItem.getEventCode());
                itemDto.setSecondsFromStart(eventItem.getSecondsFromStart());
                itemDto.setReportTime(eventItem.getEventTime());
                itemDto.setTrader(liveTrader);
                itemDto.setEventType(2);
                warnList.add(itemDto);
            }
            realtimeBaseProduecr.send(warnList,alertsEventInfo.getLinkId(),"RCS_MATCH_EVENT_INFO_WARN_NOTICE",alertsEventInfo.getStandardMatchId()+"",standardMatchInfo.getDataSourceCode());
            log.info("linkId=【{}】4123-操盘风控-紧急事件告警数据投递MQ完成,topic=RCS_MATCH_EVENT_INFO_WARN_NOTICE,标准赛事ID={}", alertsEventInfo.getLinkId(), alertsEventInfo.getStandardMatchId());


//            Map<String, MatchEventType> matchEventTypeMap = initializeComponent.getMatchEventTypeData().get(alertsEventInfo.getSportId());
//            MatchEventType alertsMatchEventType = matchEventTypeMap.get(alertsEventInfo.getEventCode());
//            //标准联赛信息
//            StandardSportTournament standardTournament = standardSportTournamentService.getItem(standardMatchInfo.getStandardTournamentId());
//            //告警信息
//            String sendMsg = alertsEventCodeTemplate.replace("{1}", standardMatchInfo.getMatchManageId())
//                    .replace("{2}", null != standardTournament ? (standardTournament.getName() + " " + standardTournament.getNameSpell()) : "")
//                    .replace("{3}", TimeUtils.timestamp2Str(alertsEventInfo.getEventTime(), "HH:mm:ss")
//                                    + " " + (null != alertsMatchEventType
//                                    ? (alertsEventInfo.getCanceled() == 1 ? "取消" : "") + alertsMatchEventType.getEventName() + " " + (alertsEventInfo.getCanceled() == 1 ? "cancel_" : "") + alertsEventInfo.getEventCode()
//                                    : (alertsEventInfo.getCanceled() == 1 ? "cancel_" : "") + alertsEventInfo.getEventCode()
//                            ) + " " + TimeUtils.convertSecondsToMMSS(alertsEventInfo.getSecondsFromStart())
//                    )
//                    .replace("{4}", TimeUtils.timestamp2Str(alertsEventInfo.getEventTime(), "yyyy-MM-dd HH:mm:ss"))
//                    .replace("{5}", liveTrader)
//                    .replace("{6}", alertsEventInfo.getDataSourceCode());
////            List<MatchEventInfo> alertsEventInfoList = matchEventInfoService.getEventHistoryByEventTime(alertsEventInfo);
//            List<MatchEventInfo> alertsEventInfoList = getAlertsEventInfoListForward10(alertsEventInfo,matchEventInfoList);
//
//            for (int i = 0; i < alertsEventInfoList.size(); i++) {
//                MatchEventInfo item = alertsEventInfoList.get(i);
//                MatchEventType matchEventType = matchEventTypeMap.get(item.getEventCode());
//                if (null != matchEventType) {
//                    String str = "forward event " + (i + 1) + " ：";
//                    if (sendMsg.contains(str)) {
//                        sendMsg = sendMsg.replace(str + "MSG_79713",
//                                str.replace("forward", TimeUtils.timestamp2Str(item.getEventTime(), "HH:mm:ss") + " ：")
//                                        + (item.getCanceled() == 1 ? "取消" : "") + matchEventType.getEventName() + " " + (item.getCanceled() == 1 ? "cancel_" : "") + matchEventType.getEventCode()
////                                     +" "+TimeUtils.timestamp2Str(item.getEventTime(),"HH:mm:ss")
//                                        + " " + (StringUtils.isNotBlank(item.getHomeAway()) ? item.getHomeAway() : "none")
//                                        + " " + TimeUtils.convertSecondsToMMSS(item.getSecondsFromStart())
//                        );
//                    }
//                }
//            }
//            if (sendMsg.contains(",")) {
//                sendMsg = sendMsg.replaceAll(",", System.lineSeparator());
//            }
//            if (sendMsg.contains("MSG_79713")) {
//                sendMsg = sendMsg.replaceAll("MSG_79713", "none");
//            }
//            realtimeBaseProduecr.send(Lists.newArrayList(sendMsg),alertsEventInfo.getLinkId(),"PA_COMMON_WARN_INFO",alertsEventInfo.getStandardMatchId()+"","79713");
//            log.info("linkId=【{}】开始组装优化单79713事件告警数据,topic=PA_COMMON_WARN_INFO,标准赛事ID={}", alertsEventInfo.getLinkId(), alertsEventInfo.getStandardMatchId());
        }catch (Exception e){
            log.error("linkId=【"+alertsEventInfo.getLinkId()+"】组装优化单79713事件告警数据异常,topic=PA_COMMON_WARN_INFO,标准赛事ID："+alertsEventInfo.getStandardMatchId()+",Exception:",e);
        }
    }


    /**
     * 获取告警事件前N条事件（默认10条）
     */
    private List<MatchEventInfo> getAlertsEventInfoListForward10(MatchEventInfoDetail alertsEventInfo,List<MatchEventInfo> matchEventInfoList) {
        //向前事件条数
        alertsEventInfo.setSize(Integer.valueOf(alertsEventForwardSize));
        //记录告警事件前10条数据
        String alertsEventsKey = String.format(ConstantSystem.getAlertsEventsKey(), alertsEventInfo.getDataSourceCode(),alertsEventInfo.getStandardMatchId());
        //本次需要缓存的集合
        List<MatchEventInfo> matchEventInfos = new LinkedList<>();
        //之前未被缓存的最近10条事件
        Map<String, MatchEventInfo> thirdEventId2Map = redisService.hGetAll(alertsEventsKey);
        if (CollectionUtils.isEmpty(thirdEventId2Map) && !pandaDbIsError) {
            try{
                alertsEventInfo.setTableName("match_event_info_"+alertsEventInfo.getDataSourceCode().toLowerCase(Locale.ROOT));
                List<MatchEventInfo> alertsEventInfoList = matchEventInfoService.getEventHistoryByEventTime(alertsEventInfo);
                matchEventInfos.addAll(alertsEventInfoList);
            }catch (Exception e){
                log.info("【linkId="+alertsEventInfo.getLinkId()+"】getAlertsEventInfoListForward10，事件信息缓存清理异常,Exception:",e);
            }
        }else{
            matchEventInfos.addAll(thirdEventId2Map.values());
        }
        matchEventInfos.addAll(matchEventInfoList);
        log.info("【linkId="+alertsEventInfo.getLinkId()+"】getAlertsEventInfoListForward10,刷新告警事件缓存处理,数据源赛事ID={},当前事件条数={},缓存中事件条数={},只需要缓存最近10条事件"
                ,alertsEventInfo.getThirdMatchSourceId(), matchEventInfos.size(),thirdEventId2Map.size());
        matchEventInfos = matchEventInfos.stream().filter(obj->obj.getEventTime() <= alertsEventInfo.getEventTime()).sorted(Comparator.comparingLong(MatchEventInfo::getEventTime).reversed()).collect(Collectors.toList());
        //最多缓存11条数据
        if(matchEventInfos.size() > alertsEventInfo.getSize()+1){
            matchEventInfos = matchEventInfos.subList(0,alertsEventInfo.getSize()+1);
        }
        thirdEventId2Map = matchEventInfos.stream().collect(Collectors.toMap(obj -> obj.getThirdEventId(), thi -> thi,(oldValue, newValue) -> newValue));
        redisService.hSetAll(alertsEventsKey,thirdEventId2Map);
        redisService.expire(alertsEventsKey,RedisConfig.REDIS_HOUR_TIME);
        //返回的集合不能包含当前告警事件
        return matchEventInfos.stream().filter(obj->!obj.getThirdEventId().equals(alertsEventInfo.getThirdEventId())).collect(Collectors.toList());
    }

}
