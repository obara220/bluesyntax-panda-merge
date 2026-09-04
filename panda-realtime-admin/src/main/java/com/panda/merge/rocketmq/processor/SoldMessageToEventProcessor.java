package com.panda.merge.rocketmq.processor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Lists;
import com.panda.merge.common.enums.*;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.SoldMessage;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.rocketmq.producer.MatchEventInfoProducer;
import com.panda.merge.rocketmq.producer.StandardMatchStatusProducer;
import com.panda.merge.service.MatchEventInfoService;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportMarketSellService;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 开售处理下发事件
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/28 <br>
 * @see com.panda.merge.rocketmq.processor <br>
 */
@Component
@Slf4j
@Validated
public class SoldMessageToEventProcessor{

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;

    @Autowired
    private MatchEventInfoService matchEventInfoService;

    @Autowired
    private MatchEventInfoProducer matchEventInfoProducer;

    @Autowired
    private StandardMatchStatusProducer standardMatchStatusProducer;

    @Autowired
    private ThirdMatchStatusProcessor thirdMatchStatusProcessor;

    @Autowired
    public MatchEventInfoProcessor matchEventInfoProcessor;

    @Autowired
    public RedisService redisService;

    /**
     *  需要被忽略的事件源
     * */
    @NacosValue(value = "${suspend.event.code:OD}", autoRefreshed = true)
    private String suspendEventCodes;

    /**
     *  事件入库方式(1:根据赛事ID修改，2:单条批量修改)
     * */
    @NacosValue(value = "${match.event.db:1}", autoRefreshed = true)
    private Integer matchEvent2Db;

    /** linkId 最大长度*/
    @NacosValue(value = "${panda.link.max.size:50}", autoRefreshed = true)
    private Integer linIdMaxSize;

    /**
     *  4248 【赛程】赛事中断场景优化开关（false:关，true：开）
     * */
    @NacosValue(value = "${panda.interrupted.event.switch:true}", autoRefreshed = true)
    private boolean interruptedEventSwitch;


    public void soldMessageToEvent(@Valid Request<SoldMessage> request) {
        //下发全量历史事件
        Integer num = reissueEventInfo(request.getLinkId(), request.getData().getMatchId(),false);
        log.info("【"+ PROJECT_ID_REALTIME+" ：soldMessageToEvent】【{}】开售后补发事件结束,补发条数:{}",request.getLinkId(),num);
    }

    /**
     *  全量事件下发，下发未下发事件
     * @param linkId
     * @param standardMatchId
     * @param isReissue  是否补发事件(是:true，否:false) 注：滚球切换事件源属于补发事件 , 正常开售 & 滚球开售 触发的下发事件不属于补发
     * @return   补发事件条数
     */
    public Integer reissueEventInfo(String linkId, Long standardMatchId,boolean isReissue) {
        //刷新开售缓存并返回最新开售信息
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.refreshCache(standardMatchId);
        if (null == standardSportMarketSell) {
            log.info("linkId=【{}】reissueEventInfo 补发事件下发,未找到预开售信息,标准赛事id={}", linkId,standardMatchId);
            return ZERO;
        }
        //商业事件源编码
        String dataSource = standardSportMarketSell.getBusinessEvent();
        log.info("linkId=【{}】reissueEventInfo 补发事件下发,下发未下发事件,isReissue={},开售信息={}", linkId,isReissue, JSON.toJSONString(standardSportMarketSell));
        //查询三方赛事信息
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItemNoCache(standardMatchId, dataSource);
        if (null == thirdMatchInfo) {
            log.info("linkId=【{}】reissueEventInfo 补发事件下发,查询三方赛事为空,三方赛事id={}，商业事件源编码={}",linkId, standardMatchId,dataSource);
            return ZERO;
        }
        //获取标准赛事信息
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItemByPrimaryKey(standardMatchId);
        if (null == standardMatchInfo) {
            log.info("linkId=【{}】reissueEventInfo 补发事件下发,查询标准赛事为空,标准赛事id={}，商业事件源编码={}",linkId, standardMatchId,dataSource);
            return ZERO;
        }
//        //======================开售数据源补发一次赛事状态源服务商的赛事状态开始======================
//        if(!isReissue){
//            try {
//                log.info("linkId=【{}】reissueEventInfo开售补发赛事状态,需要重新下发的赛事状态信息,standardMatchInfo={}", linkId,JSON.toJSONString(standardMatchInfo));
//                //如果是未开赛无需补发
//                if(!MatchStatusEnum.Not_Started.value.equals(standardMatchInfo.getMatchStatus())){
//                    String matchStatusSourceCode = StringUtils.isNotBlank(standardSportMarketSell.getMatchStatusSourceCode()) ? standardSportMarketSell.getMatchStatusSourceCode():standardMatchInfo.getDataSourceCode();
//                    thirdMatchStatusProcessor.pushMatchStatusInfo(linkId+"_Status", standardMatchInfo,standardSportMarketSell,matchStatusSourceCode,System.currentTimeMillis());
//                }
//            }catch (Exception e){
//                log.error("linkId=【"+linkId+"】reissueEventInfo开售补发赛事状态下发异常,标准赛事id="+standardMatchId+"，Exception:", e);
//            }
//            try {
//                //赛事开售通知下游（业务）
//                standardMatchStatusProducer.sendStandardMatchSold(linkId, standardMatchInfo, standardSportMarketSell);
//            }catch (Exception e){
//                log.error("linkId=【"+linkId+"】reissueEventInfo开售通知下游（业务）下发异常,标准赛事id="+standardMatchId+"，Exception:", e);
//            }
//        }
//        //======================开售数据源补发一次赛事状态源服务商的赛事状态结束======================
//        if(suspendEventCodes.contains(thirdMatchInfo.getDataSourceCode())){
//            log.info("linkId=【{}】reissueEventInfo 补发事件下发,该数据源事件无需处理,标准赛事id={}，数据源源编码={}",linkId, standardMatchId,thirdMatchInfo.getDataSourceCode());
//            return ZERO;
//        }
        if(!DataSourceCodeEnum.getEventCodeList().contains(thirdMatchInfo.getDataSourceCode())){
            log.info("linkId=【{}】reissueEventInfo 补发事件下发,该数据源不正常事件,无需处理,标准赛事id={}，数据源源编码={}",linkId, standardMatchId,thirdMatchInfo.getDataSourceCode());
            return ZERO;
        }
        //赛事级别分布式锁，避免事件重复下发
        String matchSoldTryLock = "Sold::MatchEventInfoDTO_"+dataSource+"_"+thirdMatchInfo.getThirdMatchSourceId();
        boolean flag = false;
        try{
            //开售或者切换事件源清理告警事件历史缓存，方便查库中最新数据
            redisService.del(String.format(ConstantSystem.getAlertsEventsKey(), dataSource,standardMatchId));
            //批量下发数据不可控，可能比较多，需要锁60秒
            flag = redisService.tryLock(matchSoldTryLock, matchSoldTryLock, SIXTY, 10);
            //当前事件源的全部事件信息
            List<MatchEventInfo> oldEventInfoAllList;
            //未下发过的标准事件信息
            List<MatchEventInfo> oldEventInfoList;
            try{
                //根据三方赛事ID获取赛事全量事件信息
                oldEventInfoAllList = matchEventInfoService.getItemByThirdMatchIdAndDataSoureCode(
                        thirdMatchInfo.getId(), thirdMatchInfo.getDataSourceCode()
                );
                List<Long> sportIds = Lists.newArrayList(StandardSportTypeEnum.FootBall.getCode(), StandardSportTypeEnum.Basketball.getCode());
                if (sportIds.contains(thirdMatchInfo.getSportId())) {
                    for (MatchEventInfo matchEventInfo : oldEventInfoAllList) {
                        //自动关盘
                        if (MATCH_CLOSE_PERIOD_ID.get(standardMatchInfo.getSportId()).contains(matchEventInfo.getMatchPeriodId())) {
                            matchEventInfoProcessor.autoCloseMarketDispose(standardMatchInfo, linkId, matchEventInfo);
                        }
                    }
                }

                //根据三方赛事ID获取库中未下发的事件列表
//                oldEventInfoList = matchEventInfoService.getItemByThirdMatchIdAndSendData(
//                        thirdMatchInfo.getId(),thirdMatchInfo.getDataSourceCode(),YesNoEnum.N.name()
//                );
                //获取未下发的事件列表
                oldEventInfoList = oldEventInfoAllList.stream().filter(obj->YesNoEnum.N.name().equals(obj.getSendData())).collect(Collectors.toList());
            }catch (Exception e){
                log.error("linkId=【"+linkId+"】reissueEventInfo 补发事件下发异常,标准赛事id:"+standardMatchId+",查询数据库异常,请确认数据源:"+thirdMatchInfo.getDataSourceCode()+"是否存在事件信息,Exception:", e);
                return ZERO;
            }
            if (CollectionUtils.isEmpty(oldEventInfoList)) {
                log.info("linkId=【{}】reissueEventInfo 补发事件下发,下发当前最新事件:未下发的事件列表为空 ,三方赛事id={},数据源编码={}", linkId, thirdMatchInfo.getId(), dataSource);
                return ZERO;
            }
            //开售或者切换事件源都需要，先清理缓存在重新缓存当前事件源的事件比分，用于赔率服务基准分计算
            matchEventInfoProducer.standardMatchScores2Redis(linkId,oldEventInfoAllList,thirdMatchInfo);
            log.info("linkId=【{}】reissueEventInfo 补发事件下发,全部事件条数：{},需要补发事件条数：{}", linkId,oldEventInfoAllList.size(),oldEventInfoList.size());
            //需要更新的事件列表 MATCH_EVENT_INFO
            List<MatchEventInfo> matchEventInfoList = new LinkedList<>();
            //需要更新的事件列表 MATCH_EVENT_INFO_TO_RISK
            List<MatchEventInfo> matchEventInfoRiskList = new LinkedList<>();
            //更新对应标准赛事消息
            StandardMatchInfo upStandardMatchInfo = new StandardMatchInfo();
            upStandardMatchInfo.setId(standardMatchInfo.getId());
            //linkId长度兜底，因为数据库最大长度是60
            String newLinkId;
            if(linkId.length() > linIdMaxSize){
                newLinkId = linkId.substring(0,linIdMaxSize);
                log.info("linkId=【{}】reissueEventInfo 补发事件下发,linkId超长,截取后linkId：{}", linkId,newLinkId);
            }else{
                newLinkId = linkId;
            }

            //事件中断标识
            boolean interruptedFlag = false;
            //循环推送事件给下游
            for (int i = 0; i < oldEventInfoList.size(); i++) {
                //库中事件信息
                MatchEventInfo matchEventInfo = oldEventInfoList.get(i);
                if(null == matchEventInfo.getStandardMatchId() || 0 == matchEventInfo.getStandardMatchId()){
                    matchEventInfoRiskList.add(matchEventInfo);
                }
                matchEventInfo.setStandardMatchId(standardMatchId);
                //添加下发标识
                matchEventInfo.setSendData(YesNoEnum.Y.name());
                matchEventInfo.setLinkId(newLinkId);
                matchEventInfo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                matchEventInfoList.add(matchEventInfo);

                upStandardMatchInfo.setMatchPeriodId(matchEventInfo.getMatchPeriodId());
                upStandardMatchInfo.setSecondsMatchStart(Math.toIntExact(matchEventInfo.getSecondsFromStart()));
                upStandardMatchInfo.setSecondsMatchModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());

                //4248 【赛程】赛事中断场景优化: 事件编码=match_status & 赛事阶段=80 ，属于停表中断，如标准赛事状态不是中断则需要生成标准赛事中断状态下发
                if(StringUtils.equals(EventCodeEnum.MATCH_STATUS.code, matchEventInfo.getEventCode()) &&
                        Objects.equals(MatchPeriodForMatchOverEnum.Interrupted.value, matchEventInfo.getMatchPeriodId())){
                    interruptedFlag = true;
                }
            }

            //4248 【赛程】赛事中断场景优化: 事件编码=match_status & 赛事阶段=80 ，属于停表中断，如标准赛事状态不是中断则需要生成标准赛事中断状态下发
            if(interruptedEventSwitch){
                if(interruptedFlag){
                    //标准赛事状态不是中断
                    if(!Objects.equals(MatchStatusEnum.Interrupted.value,standardMatchInfo.getMatchStatus())){
                        //补发中断状态
                        standardMatchInfo.setMatchStatus(MatchStatusEnum.Interrupted.value);
                        //下发赛事状态给业务
                        thirdMatchStatusProcessor.pushMatchStatusInfo(linkId, standardMatchInfo, standardSportMarketSell, standardMatchInfo.getDataSourceCode(), System.currentTimeMillis());

                        //事件这里直接修改标准赛事状态
                        upStandardMatchInfo.setMatchStatus(MatchStatusEnum.Interrupted.value);
                        log.info("linkId=【{}】reissueEventInfo_4248，赛事中断,需要更新的标准赛事信息={}", linkId, JSON.toJSONString(upStandardMatchInfo));

                        //缓存标识5小时
                        String interruptedKey = String.format(ConstantSystem.getInterruptedKey(), standardMatchInfo.getId());
                        redisService.set(interruptedKey,ONE,FIVES * RedisConfig.REDIS_HOUR_TIME);
                    }
                }else{
                    //如果缓存中key存在
                    String interruptedKey = String.format(ConstantSystem.getInterruptedKey(), standardMatchInfo.getId());
                    if(redisService.hasKey(interruptedKey)){
                        redisService.del(interruptedKey);

                        //补发滚球状态
                        standardMatchInfo.setMatchStatus(MatchStatusEnum.Live.value);
                        //下发赛事状态给业务
                        thirdMatchStatusProcessor.pushMatchStatusInfo(linkId, standardMatchInfo, standardSportMarketSell, standardMatchInfo.getDataSourceCode(), System.currentTimeMillis());

                        //事件这里直接修改标准赛事状态
                        upStandardMatchInfo.setMatchStatus(MatchStatusEnum.Live.value);
                        log.info("linkId=【{}】reissueEventInfo_4248，中断恢复,需要更新的标准赛事信息={}", linkId, JSON.toJSONString(upStandardMatchInfo));
                    }
                }
            }


            //BUG：45387  补发事件到MATCH_EVENT_INFO_TO_RISK, 该topic下在此处，都属于历史事件
            if(!CollectionUtils.isEmpty(matchEventInfoRiskList)){
                matchEventInfoProducer.pushMatchEventDataToRisk(linkId, matchEventInfoRiskList,thirdMatchInfo,true);
            }
            //下发事件到MATCH_EVENT_INFO (isReissue字段解释 true:切换事件源，或者延迟消费的事件，false:开售事件，或者正常通道下发事件)
            matchEventInfoProducer.pushMatchEventData(linkId, matchEventInfoList, thirdMatchInfo, isReissue,false);

            //更新标准赛事信息
            standardMatchInfoService.updateByPrimaryKeySelective(upStandardMatchInfo);

            //根据赛事信息批量修改事件
            if(ONE.equals(matchEvent2Db)){
                matchEventInfoService.matchEvent2StandardEvent(newLinkId,thirdMatchInfo);
            }else{
                matchEventInfoService.updateBatch(matchEventInfoList);
            }

            //三方事件关联到标准赛事下(开售时也校验下是否还有事件未绑定到标准赛事下面)
            matchEvent2StandardMatch(linkId,standardMatchId,thirdMatchInfo.getId());

            return matchEventInfoList.size();
        }finally {
            if(flag){
                //释放redis锁
                redisService.unLock(matchSoldTryLock, matchSoldTryLock);
            }
        }
    }

    /**
     * 三方事件关联到标准赛事下
     * @param linkId 线路ID
     * @param standardMatchId 当前标准赛事ID
     * @param thirdMatchInfoId 需要排除的三方赛事ID
     * */
    public void matchEvent2StandardMatch(String linkId,Long standardMatchId,Long thirdMatchInfoId){
        //补发标准赛事下其他事件源未关联标准赛事的事件
        List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoService.getItems(standardMatchId);
        for(ThirdMatchInfo item : thirdMatchInfos){
            if(thirdMatchInfoId != null && item.getId().equals(thirdMatchInfoId)){
                log.info("linkId=【{}】matchEventRelationStandardMatch 数据源编码={},源赛事ID={}需要过滤,无需补发事件到TO_RISK", linkId,item.getDataSourceCode(),item.getThirdMatchSourceId());
                continue;
            }
            matchEvent2StandardMatch(linkId,item);
        }
    }

    public void matchEvent2StandardMatch(String linkId,ThirdMatchInfo item){
        if(DataSourceCodeEnum.getEventCodeList().contains(item.getDataSourceCode())){
            List<MatchEventInfo> list = matchEventInfoService.getItemByThirdMatchIdAndDataSoureCode(item.getId(), item.getDataSourceCode());
            if(!CollectionUtils.isEmpty(list)){
                //需要更新的事件列表 MATCH_EVENT_INFO_TO_RISK
                List<MatchEventInfo> riskList = new LinkedList<>();
                for (int i = 0; i < list.size(); i++) {
                    //库中事件信息
                    MatchEventInfo matchEventInfo = list.get(i);
                    if(null == matchEventInfo.getStandardMatchId() || 0 == matchEventInfo.getStandardMatchId()){
                        matchEventInfo.setStandardMatchId(item.getReferenceId());
                        matchEventInfo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                        riskList.add(matchEventInfo);
                    }
                }
                if(!CollectionUtils.isEmpty(riskList)){
                    //投递到MQ
                    matchEventInfoProducer.pushMatchEventDataToRisk(linkId+item.getDataSourceCode(), riskList,item,true);
                    //根据赛事信息批量修改事件
                    matchEventInfoService.matchEvent2StandardMatch(linkId,item);
                    log.info("linkId=【{}】matchEventRelationStandardMatch 补发事件下发TO_RISK,linkId={},riskList条数：{}", linkId,riskList.size());
                }
            }
        }else{
            log.info("linkId=【" + linkId + "】matchEventRelationStandardMatch 当前数据源无事件，无需处理,数据源编码={},源赛事ID={}", linkId,item.getDataSourceCode(), item.getThirdMatchSourceId());
        }
    }



    /**
     *全量事件监听字自动关盘阶段
     */
    public static Map<Long, List<Long>> MATCH_CLOSE_PERIOD_ID = new HashMap<Long, List<Long>>() {{
        put(StandardSportTypeEnum.FootBall.getCode(), Arrays.asList(31L,100L,33L,110L,120L));
        put(StandardSportTypeEnum.Basketball.getCode(), Arrays.asList(301L,302L,31L,303L,100L,110L));
    }};

}

