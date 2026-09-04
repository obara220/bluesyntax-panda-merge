package com.panda.merge.rocketmq.processor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Lists;
import com.panda.merge.api.ITradeMarketConfigApi;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.*;
import com.panda.merge.common.utils.MyHashUtil;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.InitializeComponent;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dto.*;
import com.panda.merge.dto.message.MatchStatisticsInfoDetailMessage;
import com.panda.merge.dto.message.MatchStatisticsInfoMessage;
import com.panda.merge.exception.ExceptionHelper;
import com.panda.merge.mapper.StandardRelationNewStandardMapper;
import com.panda.merge.mapper.ThirdRelationNewThirdMapper;
import com.panda.merge.model.*;
import com.panda.merge.rocketmq.producer.MatchSaleOverProducer;
import com.panda.merge.rocketmq.producer.MatchStatisticsInfoProducer;
import com.panda.merge.rocketmq.producer.PaDataServiceLogProducer;
import com.panda.merge.rocketmq.producer.RealtimeBaseProduecr;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.*;

import static com.panda.merge.config.RedisConfig.REDIS_HOUR_TIME;
import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 第三方赛事统计信息接入
 *
 * @author Aison
 * @since 2020年10月22日11:01:53
 */
@Slf4j
@Component
@Validated
public class MatchStatisticsInfoProcessor extends BaseProcessor {

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private MatchStatisticsInfoService matchStatisticsInfoService;
//    @Autowired
//    private MatchStatisticsInfoDetailService matchStatisticsInfoDetailService;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    private MatchStatisticsInfoProducer matchStatisticsInfoProducer;
    @Autowired
    private PaDataServiceLogProducer paDataServiceLogProducer;
    @Autowired
    private InitializeComponent initializeComponent;
    @Autowired
    private MatchSaleOverProducer matchSaleOverProducer;

    @Autowired
    private MarketCategorySellService marketCategorySellService;
    @DubboReference
    private ITradeMarketConfigApi iTradeMarketConfigApi;

    @Autowired
    public RealtimeBaseProduecr realtimeBaseProduecr;


    /**
     *  拷贝赛事开关（false:关，true：开）
     * */
    @NacosValue(value = "${copy.match.switch:false}", autoRefreshed = true)
    private boolean copyMatchSwitch;

    @ExceptionHelper
    public Response putMatchStatisticsInfo(@Valid Request<MatchStatisticsInfoDTO> request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Response response = Response.success();
        try {
            log.info("linkId=【{}】putMatchStatisticsInfo, 统计信息处理开始，request={}", request.getLinkId(), JSON.toJSONString(request));
            //校验LinkId和缓存中是否重复
            validateLinkId(MATCH_STATISTICS_INFO_API, request);
            //拷贝参数对象，不对传入参数对象进行修改
            MatchStatisticsInfoDTO matchStatisticsInfoDTO = new MatchStatisticsInfoDTO();
            BeanUtils.copyProperties(request.getData(), matchStatisticsInfoDTO);
            //获取标准运动类型
            final Long sportId = validateSportId(matchStatisticsInfoDTO.getDataSourceCode(), String.valueOf(matchStatisticsInfoDTO.getSportId()));
            //查找库中三方赛事
            ThirdMatchInfo oldThirdMatchInfo = thirdMatchInfoService.getItem(matchStatisticsInfoDTO.getDataSourceCode(), matchStatisticsInfoDTO.getThirdMatchSourceId());
            if (null == oldThirdMatchInfo) {
                log.error("linkId=【{}】putMatchStatisticsInfo, error:三方赛事不存在，三方赛事id：{}", request.getLinkId(), matchStatisticsInfoDTO.getThirdMatchSourceId());
                response.setCode(ResultCode.VALIDATE_FAILED.getCode());
                response.setMsg("三方赛事信息不存在！");
                return response;
            }
            //如果对于标准赛事不存在，重新查询一次
            if (null == oldThirdMatchInfo.getReferenceId() || oldThirdMatchInfo.getReferenceId() == 0) {
                oldThirdMatchInfo = thirdMatchInfoService.getItem(oldThirdMatchInfo.getId());
            }
            //如果上游推送的阶段为0(未开始)，则沿用原来的阶段
            if (null == matchStatisticsInfoDTO.getPeriod() || MatchStatusEnum.Not_Started.value.equals(matchStatisticsInfoDTO.getPeriod())) {
                if (StringUtils.isNotBlank(oldThirdMatchInfo.getMatchPeriod())) {
                    matchStatisticsInfoDTO.setPeriod(Integer.valueOf(oldThirdMatchInfo.getMatchPeriod()));
                }
            } else {
                //实时事件入口处在存入该缓存，为空则需要在赛事统计入口修改赛事阶段
                if (Objects.isNull(redisService.get(RedisConfig.REDIS_KEY_DATABASE + "::" + MATCH_STATISTICS_INFO_API + ":" + oldThirdMatchInfo.getId()))) {
                    Long matchPeriodId = Long.valueOf(matchStatisticsInfoDTO.getPeriod());
                    //检查阶段是否符合要求
                    Map<Long, SystemItemDict> matchPeriodMap = initializeComponent.getMatchPeriodData().get(sportId);
                    if (CollectionUtils.isEmpty(matchPeriodMap) || null == matchPeriodMap.get(matchPeriodId)) {
                        log.error("linkId=【{}】putMatchStatisticsInfo，error:修改三方赛事阶段，赛事阶段数据不匹配,阶段id={}", request.getLinkId(), matchPeriodId);
                    } else {
                        ThirdMatchInfo upThirdMatchInfo = new ThirdMatchInfo();
                        upThirdMatchInfo.setId(oldThirdMatchInfo.getId());
                        //更新当前赛事阶段值
                        upThirdMatchInfo.setMatchPeriod(String.valueOf(matchStatisticsInfoDTO.getPeriod()));
                        //如果是B03篮球，需要使用赛事倒计时
                        if (DataSourceCodeEnum.BE.getCode().equals(oldThirdMatchInfo.getDataSourceCode())) {
                            if (StandardSportTypeEnum.Basketball.code.equals(oldThirdMatchInfo.getSportId())) {
                                upThirdMatchInfo.setSecondsMatchStart(matchStatisticsInfoDTO.getRemainingTime());
                            }
                        }
                        //当前赛事阶段为999，则不更新赛事阶段，避免完赛影响事件
                        if (MatchPeriodForMatchOverEnum.Ended999.value.equals(Long.valueOf(upThirdMatchInfo.getMatchPeriod()))) {
                            upThirdMatchInfo.setMatchOver(YesNoEnum.Y.value);
                            log.info("linkId=【{}】putMatchStatisticsInfo，赛事阶段999，三方赛事完赛状态,三方赛事id={}", request.getLinkId(), upThirdMatchInfo.getId());
                        }
                        //赛事阶段发生改变才需要修改
                        if(!upThirdMatchInfo.getMatchPeriod().equals(oldThirdMatchInfo.getMatchPeriod())){
                            BeanUtil.copyProperties(upThirdMatchInfo,oldThirdMatchInfo, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
                            realtimeBaseProduecr.send(upThirdMatchInfo,request.getLinkId(),DATA_THIRD_MATCH_INFO_DB,oldThirdMatchInfo.getThirdMatchSourceId(),oldThirdMatchInfo.getDataSourceCode());
                        }
                    }
                }
            }
            //统计信息业务逻辑处理
            process2MatchStatisticsInfo(request.getLinkId(), matchStatisticsInfoDTO, oldThirdMatchInfo);

            //拷贝的赛事向MQ投递赛事统计消息
            if(copyMatchSwitch){
                copyStandardMatchInfoStatistics2Mq(request, oldThirdMatchInfo);
            }
        } catch (Exception e) {
            response.setCode(ResultCode.FAILED.getCode());
            response.setMsg(e.getMessage());
            throw e;
        } finally {
            stopWatch.stop();
            response.setDataSourceTime(stopWatch.getTotalTimeMillis());
            //统计处理耗时
            paDataServiceLogProducer.sendPaDataServiceLog(
                    getPaDataServiceLogDTO(request.getLinkId(), realtime, MATCH_STATISTICS_INFO_API, "三方赛事统计信息接入",
                            stopWatch.getTotalTimeMillis(), Integer.parseInt(String.valueOf(response.getCode())), response.getMsg())
            );
            log.info("linkId=【{}】putMatchStatisticsInfo, 统计信息处理结束，共耗时={}", request.getLinkId(), JSON.toJSONString(response));
            return response;
        }
    }

    /**
     * 统计信息业务逻辑处理
     */
    private void process2MatchStatisticsInfo(String linkId, MatchStatisticsInfoDTO matchStatisticsInfoDTO, ThirdMatchInfo oldThirdMatchInfo) {
        //转换为通知下游的实体类
        List<MatchStatisticsInfoDetailMessage> matchStatisticsInfoDetailMessageList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(matchStatisticsInfoDTO.getMatchStatisticsInfoDetailList())){
            for (MatchStatisticsInfoDetailDTO detailDTO : matchStatisticsInfoDTO.getMatchStatisticsInfoDetailList()) {
                MatchStatisticsInfoDetailMessage matchStatisticsInfoDetailMessage = new MatchStatisticsInfoDetailMessage();
                BeanUtils.copyProperties(detailDTO, matchStatisticsInfoDetailMessage);
                matchStatisticsInfoDetailMessageList.add(matchStatisticsInfoDetailMessage);
            }
        }
        // 缓存统计ID和创建时间，方便后面直接取缓存
        String matchStatisticsInfoKey = String.format(ConstantSystem.getMatchStatisticsInfoKey(), oldThirdMatchInfo.getDataSourceCode(),oldThirdMatchInfo.getThirdMatchSourceId());
        //获取统计信息
        MatchStatisticsInfo matchStatisticsInfo = getRedisItem(matchStatisticsInfoDTO.getDataSourceCode(),matchStatisticsInfoDTO.getThirdMatchSourceId(),matchStatisticsInfoKey,linkId);
        if (matchStatisticsInfo == null) {
            matchStatisticsInfo = new MatchStatisticsInfo();
            matchStatisticsInfo.setId(MyHashUtil.fnv1aHash64(matchStatisticsInfoDTO.getDataSourceCode()+matchStatisticsInfoDTO.getThirdMatchSourceId()));

            MatchStatisticsInfo redisItem = new MatchStatisticsInfo();
            redisItem.setId(matchStatisticsInfo.getId());
            redisItem.setCreateTime(System.currentTimeMillis());
            redisService.set(matchStatisticsInfoKey, redisItem, TWO * REDIS_HOUR_TIME);
        }
        BeanUtils.copyProperties(matchStatisticsInfoDTO, matchStatisticsInfo);
        //统计详情，不需要单独存表，直接放到统计信息里面，避免高并发下重复操作数据库
        String remark = JSON.toJSONString(matchStatisticsInfoDetailMessageList);
        matchStatisticsInfo.setRemark(remark);
        matchStatisticsInfo.setThirdMatchId(oldThirdMatchInfo.getId());
        matchStatisticsInfo.setStandardMatchId(oldThirdMatchInfo.getReferenceId());
        matchStatisticsInfo.setThirdSourceMatchId(oldThirdMatchInfo.getThirdMatchSourceId());
        matchStatisticsInfo.setDataSourceCode(oldThirdMatchInfo.getDataSourceCode());
        matchStatisticsInfo.setSportId(oldThirdMatchInfo.getSportId());
        //需要入库的赛事阶段
        Map<Long, Long> periodId2Time = MatchPeriodForMatchOverEnum.getSleepMatchPeriodId2Time();
        Long period = Long.valueOf(matchStatisticsInfoDTO.getPeriod());
        //阶段未发生改变和已经完赛的不入库
        if(periodId2Time.get(period) != null && !ONE.equals(oldThirdMatchInfo.getMatchOver())){
            realtimeBaseProduecr.send(matchStatisticsInfo,linkId,DATA_MATCHS_TATISTICS_INFO_DB,matchStatisticsInfo.getThirdSourceMatchId(),matchStatisticsInfo.getDataSourceCode());
        }
        //------同步比分数据------
        //查询比分数据
//        List<MatchStatisticsInfoDetail> matchStatisticsInfoDetailList = matchStatisticsInfoDetailService.getItemList(matchStatisticsInfo.getId());
//        Map<String, MatchStatisticsInfoDetail> detailMap = new HashMap<>(16);
//        if (!CollectionUtils.isEmpty(matchStatisticsInfoDetailList)) {
//            for (MatchStatisticsInfoDetail detail : matchStatisticsInfoDetailList) {
//                String key = matchStatisticsInfo.getId() + "_" + detail.getCode() + "_" + detail.getFirstNum() + "_" + detail.getSecondNum();
//                detailMap.put(key, detail);
//            }
//        }
//
//        //循环比分数据(其他地方没有人用到该统计详情信息，所以不需要入库，避免频繁操作数据库)
//        List<MatchStatisticsInfoDetailMessage> matchStatisticsInfoDetailMessageList = new ArrayList<>();
//        if (!CollectionUtils.isEmpty(matchStatisticsInfoDTO.getMatchStatisticsInfoDetailList())) {
//            //加redis锁处理赛事统计信息过滤，排斥重复插入问题
//            String matchLockKey = oldThirdMatchInfo.getDataSourceCode() + sportId + matchStatisticsInfo.getId();
//            try {
//                /**redis锁*/
//                if (redisService.tryLock(matchLockKey, matchLockKey, 5, 3)) {
//                    for (MatchStatisticsInfoDetailDTO detailDTO : matchStatisticsInfoDTO.getMatchStatisticsInfoDetailList()) {
//                        String key = matchStatisticsInfo.getId() + "_" + detailDTO.getCode() + "_" + detailDTO.getFirstNum() + "_" + detailDTO.getSecondNum();
//                        MatchStatisticsInfoDetail msDetail = detailMap.get(key);
//                        if (msDetail == null) {
//                            msDetail = matchStatisticsInfoDetailService.create(detailDTO, matchStatisticsInfo.getId());
//                        } else {
//                            BeanUtils.copyProperties(detailDTO, msDetail);
//                            matchStatisticsInfoDetailService.update(msDetail);
//                        }
//                        MatchStatisticsInfoDetailMessage matchStatisticsInfoDetailMessage = new MatchStatisticsInfoDetailMessage();
//                        BeanUtils.copyProperties(msDetail, matchStatisticsInfoDetailMessage);
//                        matchStatisticsInfoDetailMessageList.add(matchStatisticsInfoDetailMessage);
//                    }
//                }
//            } catch (Exception e) {
//                log.error(e.getMessage(), e);
//            } finally {
//                redisService.unLock(matchLockKey, matchLockKey);
//            }
//        }

        //-------MQ推送赛事统计信息到下游------
        //查询标准赛事是否存在
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(oldThirdMatchInfo.getReferenceId());
        if (null == standardMatchInfo) {
            log.info("linkId=【{}】 standardMatchInfo 不存在，统计信息不推送，标准赛事id={}", linkId, oldThirdMatchInfo.getReferenceId());
            return;
        }
        //查询赛事开售是否存在
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(oldThirdMatchInfo.getReferenceId());
        if (null == standardSportMarketSell) {
            log.info("linkId=【{}】 standardSportMarketSell 不存在，统计信息不推送,标准赛事id={}", linkId, oldThirdMatchInfo.getReferenceId());
            return;
        }
        //判断是否为开售事件源
        if (!oldThirdMatchInfo.getDataSourceCode().equalsIgnoreCase(standardSportMarketSell.getBusinessEvent())) {
            log.info("linkId=【{}】 不是商业事件源，统计信息不推送,三方数据源={},开售事件源={}  ", linkId, oldThirdMatchInfo.getDataSourceCode(), standardSportMarketSell.getBusinessEvent());
            return;
        }
        //实时事件入口处在存入该缓存，为空则需要在赛事统计入口修改赛事阶段
        if (Objects.isNull(redisService.get(RedisConfig.REDIS_KEY_DATABASE + "::" + MATCH_STATISTICS_INFO_API + ":" + oldThirdMatchInfo.getId()))) {
            Long matchPeriodId = Long.valueOf(matchStatisticsInfoDTO.getPeriod());
            if (null != matchPeriodId) {
                //检查阶段是否符合要求
                Map<Long, SystemItemDict> matchPeriodMap = initializeComponent.getMatchPeriodData().get(oldThirdMatchInfo.getSportId());
                if (CollectionUtils.isEmpty(matchPeriodMap) || null == matchPeriodMap.get(matchPeriodId)) {
                    log.error("linkId=【{}】putMatchStatisticsInfo，error:修改标准赛事阶段，赛事阶段数据不匹配,阶段id={}", linkId, matchPeriodId);
                } else {
                    //本次需要修改的标准赛事字段
                    StandardMatchInfo upStandardMatchInfo = new StandardMatchInfo();
                    upStandardMatchInfo.setId(standardMatchInfo.getId());
                    upStandardMatchInfo.setMatchPeriodId(Long.valueOf(String.valueOf(matchStatisticsInfo.getPeriod())));
                    //如果是B03篮球，需要使用赛事倒计时
                    if (DataSourceCodeEnum.BE.getCode().equals(oldThirdMatchInfo.getDataSourceCode())) {
                        if (StandardSportTypeEnum.Basketball.code.equals(oldThirdMatchInfo.getSportId())) {
                            upStandardMatchInfo.setSecondsMatchStart(matchStatisticsInfoDTO.getRemainingTime());
                        }
                    }
                    //当前赛事阶段为999，则设值完赛状态字段
                    if (MatchPeriodForMatchOverEnum.Ended999.value.equals(upStandardMatchInfo.getMatchPeriodId())) {
                        upStandardMatchInfo.setMatchOver(YesNoEnum.Y.value);
                        if (null != standardSportMarketSell) {
                            //通知预售开售 赛事完赛消息
                            matchSaleOverProducer.sendMatchSaleOverMessage(linkId, standardMatchInfo);
                        }
                        log.info("linkId=【{}】process2MatchStatisticsInfo，赛事阶段999，标准赛事完赛状态,标准赛事id={}", linkId, upStandardMatchInfo.getId());
                    }
//                    try{
//                        standardMatchInfo = standardMatchInfoService.updateByPrimaryKeySelective(upStandardMatchInfo);
//                        log.info("linkId=【{}】process2MatchStatisticsInfo，修改标准赛事信息完成={}", linkId, JSON.toJSONString(standardMatchInfo));
//                    } catch (Exception e) {
//                        log.error("linkId=【" + linkId + "】process2MatchStatisticsInfo，error:修改标准赛事异常，标准赛事ID:" + upStandardMatchInfo.getId() + "，Exception:", e);
//                    }
                    //赛事阶段发生改变才需要修改
                    if(!upStandardMatchInfo.getMatchPeriodId().equals(standardMatchInfo.getMatchPeriodId())){
                        BeanUtil.copyProperties(upStandardMatchInfo,standardMatchInfo, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
                        realtimeBaseProduecr.send(upStandardMatchInfo,linkId,DATA_STANDARD_MATCH_INFO_DB,standardMatchInfo.getId()+"",oldThirdMatchInfo.getDataSourceCode());
                    }
                }
            }
        }
        MatchStatisticsInfoMessage matchStatisticsInfoMessage = new MatchStatisticsInfoMessage();
        BeanUtils.copyProperties(matchStatisticsInfo, matchStatisticsInfoMessage);
        matchStatisticsInfoMessage.setThirdSourceMatchId(matchStatisticsInfo.getThirdSourceMatchId());
        matchStatisticsInfoMessage.setMatchStatisticsInfoDetailList(matchStatisticsInfoDetailMessageList);
        matchStatisticsInfoMessage.setMatchType(standardMatchInfo.getMatchType());
        //BC统计特殊处理
        Boolean eventFlag = bcEventProcessor(linkId, standardMatchInfo, oldThirdMatchInfo);
        if (eventFlag) {
            //推送mq
            matchStatisticsInfoProducer.sendMatchStatisticsInfo(linkId, matchStatisticsInfoMessage, oldThirdMatchInfo);
        }
        //根据比赛进行时间，确认是否有需要关盘的玩法
        autoCloseMarketDispose(standardMatchInfo, linkId, matchStatisticsInfoDTO);
    }

    /**
     * 获取缓存中的统计信息
     * */
    public MatchStatisticsInfo getRedisItem(String dataSourceCode, String thirdSourceMatchId,String matchStatisticsInfoKey, String linkId) {
        // 尝试从Redis中获取比赛统计信息
        MatchStatisticsInfo resItem = (MatchStatisticsInfo) redisService.get(matchStatisticsInfoKey);
//        if (resItem != null) {
//            return resItem;
//        }
//        log.info("linkId=【{}】统计信息处理，获取到缓存中统计数据为空！直接查询数据库", linkId);
//        resItem = matchStatisticsInfoService.getItem(thirdSourceMatchId, dataSourceCode,linkId);
//        //重新缓存统计数据
//        if (resItem != null) {
//            MatchStatisticsInfo redisItem = new MatchStatisticsInfo();
//            redisItem.setId(resItem.getId());
//            redisItem.setCreateTime(resItem.getCreateTime());
//            redisService.set(matchStatisticsInfoKey, redisItem, TWO * REDIS_HOUR_TIME);
//        }
        return resItem;
    }


    /**
     * 自动关盘玩法数据处理
     * 考虑到存在部分赛事是没有实时事件只有统计信息的
     *
     * @param standardMatchInfo
     * @param linkId
     * @param matchStatisticsInfoDTO
     */
    public void autoCloseMarketDispose(StandardMatchInfo standardMatchInfo, String linkId, MatchStatisticsInfoDTO matchStatisticsInfoDTO) {
        // 支持足球、篮球 自动关盘处理
        List<Long> closeMarketSportIds = Lists.newArrayList(StandardSportTypeEnum.FootBall.code, StandardSportTypeEnum.Basketball.code);
        if (!closeMarketSportIds.contains(standardMatchInfo.getSportId())) {
            log.info("linkId=【{}】autoCloseMarketDispose，标准比赛id={},阶段id={},运动id={},不支持自动关盘!", linkId, standardMatchInfo.getId(), matchStatisticsInfoDTO.getPeriod(), standardMatchInfo.getSportId());
            return;
        }
        //如果阶段是0，不自动关盘
        if (ZERO.equals(matchStatisticsInfoDTO.getPeriod())) {
            log.info("linkId=【{}】autoCloseMarketDispose，error:标准比赛id={},阶段id:0,不自动关盘!", linkId, standardMatchInfo.getId());
            return;
        }
        //事件比赛 已进行时长
        Integer secondsFromStart = StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId()) ? matchStatisticsInfoDTO.getSecondsMatchStart() : matchStatisticsInfoDTO.getRemainingTime();
        //如果当前的赛事进行阶段是赛事进行中，且赛事进行时间为空则退出自动关盘
        if (null == secondsFromStart && SportPeriodWholeEnum.getSprotPeriodBySportId(standardMatchInfo.getSportId()).getPeriods().indexOf(Long.valueOf(matchStatisticsInfoDTO.getPeriod())) >= ZERO) {
            log.info("linkId=【{}】autoCloseMarketDispose，标准比赛id={},阶段id={},比赛进行时间为空!", linkId, standardMatchInfo.getId(), matchStatisticsInfoDTO.getPeriod());
            return;
        }
        //获取需要自动关盘的标准玩法
        Set<Long> marketCategoryIds = getAutoCloseMarketDisposeBySportId(linkId, Long.valueOf(secondsFromStart == null ? 0 : secondsFromStart), standardMatchInfo, matchStatisticsInfoDTO.getPeriod().longValue(), "0");
        if (marketCategoryIds == null) {
            marketCategoryIds = new HashSet<Long>();
        }
        Pair<Set<Long>, Map<String, JSONObject>> childCloseMarketCategory = getAutoCloseChildMarketCategoryDisposeBySportId(linkId, Long.valueOf(secondsFromStart == null ? 0 : secondsFromStart), standardMatchInfo, matchStatisticsInfoDTO.getPeriod().longValue(), "0");
        if (null != childCloseMarketCategory) {
            iTradeMarketConfigApi.autoCloseChildMarketCategory(linkId+"_childCloseMarket", standardMatchInfo.getId(), childCloseMarketCategory, TimeUtils.millsSecondsEast8ZoneGmt());
        }
        //1852 兜底功能，进入下一个阶段时，关闭上一个阶段的玩法盘口
        Set<Long> marketCategoryIds1 = getAutoCloseBeforePeriodCategory(linkId, standardMatchInfo, matchStatisticsInfoDTO.getPeriod().longValue());
        if (!CollectionUtils.isEmpty(marketCategoryIds1)) {
            marketCategoryIds.addAll(marketCategoryIds1);
        }
        //下发标准玩法关盘
        if (CollectionUtils.isEmpty(marketCategoryIds)) {
            log.info("linkId=【{}】autoCloseMarketDispose，标准比赛id={},阶段id={},下发自动关盘数据为空!", linkId, standardMatchInfo.getId(), matchStatisticsInfoDTO.getPeriod());
            return;
        }
        iTradeMarketConfigApi.autoCloseMarket(linkId, standardMatchInfo.getId(), marketCategoryIds, TimeUtils.millsSecondsEast8ZoneGmt());
    }



    @Autowired
    private StandardRelationNewStandardMapper standardRelationNewStandardMapper;

    @Autowired
    private ThirdRelationNewThirdMapper thirdRelationNewThirdMapper;

    /**
     * 拷贝的赛事向MQ投递统计消息,拷贝赛事不上生产
     *
     * @param request           请求参数
     * @param oldThirdMatchInfo 原始三方赛事信息
     */
    public void copyStandardMatchInfoStatistics2Mq(Request<MatchStatisticsInfoDTO> request, ThirdMatchInfo oldThirdMatchInfo) {
        if (null != oldThirdMatchInfo.getReferenceId() && !Long.valueOf(ZERO).equals(oldThirdMatchInfo.getReferenceId())) {
            String copyLinkId = StringUtils.join(request.getLinkId(), "_copy");
            //重新赋值三方赛事来源ID
            MatchStatisticsInfoDTO data = request.getData();
            log.info("linkId=【{}】copyStandardMatchInfoStatistics2Mq,dataSourceCode={},thirdMatchSourceId={},copy赛事统计信息开始", copyLinkId, data.getDataSourceCode(), data.getThirdMatchSourceId());
            //判断是否有拷贝的赛事
            StandardRelationNewStandardExample relationExample = new StandardRelationNewStandardExample();
            relationExample.createCriteria().andSourceStandardIdEqualTo(oldThirdMatchInfo.getReferenceId());
            List<StandardRelationNewStandard> copyStandards = standardRelationNewStandardMapper.selectByExample(relationExample);
            if (!CollectionUtils.isEmpty(copyStandards)) {
                int i = 1;
                for (StandardRelationNewStandard copyStandard : copyStandards) {
                    log.info("linkId=【{}】 copyStandardMatchInfoStatistics2Mq,拷贝标准赛事关系={}", copyLinkId, JSON.toJSONString(copyStandard));
                    //查询copy的标准赛事是否存在
                    StandardMatchInfo copyStandardMatchInfo = standardMatchInfoService.getItem(copyStandard.getNewStandardId());
                    if (null == copyStandardMatchInfo) {
                        log.info("linkId=【{}】 copyStandardMatchInfoStatistics2Mq，标准赛事id={}，copy标准赛事不存在!", copyLinkId, copyStandard.getNewStandardId());
                        return;
                    }
                    //拷贝的三方赛事关系
                    ThirdRelationNewThirdExample thirdRelationExample = new ThirdRelationNewThirdExample();
                    thirdRelationExample.createCriteria().andSourceThirdIdEqualTo(oldThirdMatchInfo.getId());
                    List<ThirdRelationNewThird> copyThirdList = thirdRelationNewThirdMapper.selectByExample(thirdRelationExample);
                    if (CollectionUtils.isEmpty(copyThirdList)) {
                        log.info("linkId=【{}】 copyStandardMatchInfoStatistics2Mq，三方赛事id={}，copy三方赛事关系不存在!", copyLinkId, oldThirdMatchInfo.getId());
                        return;
                    }
                    for (ThirdRelationNewThird copyThird : copyThirdList) {
                        ThirdMatchInfo copyThirdMatchInfo = thirdMatchInfoService.getItem(copyThird.getNewThirdId());
                        if (null == copyThirdMatchInfo) {
                            log.info("linkId=【{}】 copyStandardMatchInfoStatistics2Mq，copy三方赛事id={}，copy三方赛事不存在!", copyLinkId, copyThird.getNewThirdId());
                            return;
                        }
                        //重新创建参数对象
                        Request<MatchStatisticsInfoDTO> newRequest = new Request<>();
                        data.setThirdMatchSourceId(copyThirdMatchInfo.getThirdMatchSourceId());
                        newRequest.setData(data);
                        newRequest.setLinkId(copyLinkId + "_" + i++);
                        newRequest.setDataSourceCode(copyThirdMatchInfo.getDataSourceCode());
                        //重新执行copy后的赛事统计逻辑
                        putMatchStatisticsInfo(newRequest);
                    }
                }
            } else {
                log.info("linkId=【{}】copyStandardMatchInfoStatistics2Mq,根据原始标准赛事ID：{}未找到拷贝赛事信息！", copyLinkId, oldThirdMatchInfo.getReferenceId());
            }
            log.info("linkId=【{}】copyStandardMatchInfoStatistics2Mq,copy赛事统计信息结束", copyLinkId);
        }
    }
}
