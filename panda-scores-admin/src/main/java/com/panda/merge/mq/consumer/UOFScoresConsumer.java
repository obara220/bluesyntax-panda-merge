package com.panda.merge.mq.consumer;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.PandaErrorCodeEnum;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.config.mq.ConsumerConfigDetail;
import com.panda.merge.config.mq.MqConsumerConfig;
import com.panda.merge.constant.DataSourceConstant;
import com.panda.merge.constant.SourceTypeEnum;
import com.panda.merge.constant.SportTypeEnum;
import com.panda.merge.dto.MatchStatisticsInfoDTO;
import com.panda.merge.dto.MatchStatisticsInfoDetailDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.TennisScores;
import com.panda.merge.dto.scores.StandardScoreCenter;
import com.panda.merge.exception.ApiException;
import com.panda.merge.model.*;
import com.panda.merge.mq.message.CommonStandardScoresDto;
import com.panda.merge.mq.producer.CommonProducer;
import com.panda.merge.mq.producer.ScoresProducer;
import com.panda.merge.repository.*;
import com.panda.merge.service.*;
import com.panda.merge.utils.JsonMapUtils;
import com.panda.merge.utils.MessageBuilderUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;
import static com.panda.merge.constant.DataSourceConstant.VALID_DATA_SOURCES;
import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

/**
 * UOFScoresConsumer事件比分中心处理（统计比分，实时服务下发）
 */
@Slf4j
@Component
@DependsOn("scoresAdminApplication")
public class UOFScoresConsumer  extends AbstractSingleMessageMQConsumer<Request<MatchStatisticsInfoDTO>>{

    private static final String TOPIC="MATCH_STATISTICS_INFO_API";
    private static final String CONSUMER_GROUP="scores-group-"+MATCH_STATISTICS_INFO_API_SCORES;

    @Autowired
    BTMatchScoresService btMatchScoresService;
    @Autowired
    LSMatchScoresService lsMatchScoresService;
    @Autowired
    IUOFScoresService uofScoresService;
    @Autowired
    RedisService redisService;
    @Autowired
    ScoresProducer scoresProducer;
    @Autowired
    MatchScoresSourceTypeRepository matchScoresSourceTypeRepository;
    @Autowired
    IScoresService scoresService;
    @Autowired
    public ThirdSportTypeService thirdSportTypeService;
//    @Autowired
//    IMatchScoreSearchService matchScoreSearchService;
    @Autowired
    MatchScoreInfoRepository matchScoreInfoRepository;
    @Autowired
    StandardMatchInfoService standardMatchInfoService;
    @Autowired
    MessageBuilderUtils messageBuilderUtils;
    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    @Autowired
    StandardSportMarketSellRepository standardSportMarketSellRepository;
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    @Autowired
    ScoresRedisHelp scoresRedisHelp;
    @Autowired
    MatchTimeInfoRepository matchTimeInfoRepository;
    @Autowired
    ILiveDataScoresService liveDataScoresService;

        public static final String NACOS_MQ_CONSUMER_THREAD_NUMBER_KEY ="mq.uof-score.consumer.thread";

    @NacosValue(value = "${"+ NACOS_MQ_CONSUMER_THREAD_NUMBER_KEY +":20}",autoRefreshed = true)
    @Getter
    @Setter
    private Integer consumerThreadNumber;
    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;
    @Autowired
    CommonProducer commonProducer;

        MqConsumerConfig buildConfig() {
        ConsumerConfigDetail consumerConfigDetail = ConsumerConfigDetail.builder()
                .threadNumber(consumerThreadNumber).pullBatchSize(64).build();
        return new MqConsumerConfig(TOPIC, CONSUMER_GROUP
                , new TypeReference<Request<MatchStatisticsInfoDTO>>() {},consumerConfigDetail);
    }
    /**
     * 事件比分中心处理
     * @param request
     */
    @SneakyThrows
    @Override
    public void processMessage(Request<MatchStatisticsInfoDTO> request) {
        log.info("UOFScoresConsumer MQ消费数据开始...{}",datacenterMergeSwitch);
        if (datacenterMergeSwitch && commonProducer.getDatacenterMatchIds(request.getData().getThirdMatchSourceId())) {
            //MQ消息转发给数据中心
            commonProducer.asyncSend(request, "datacenter-MATCH_STATISTICS_INFO_API",request.getLinkId());
            return;
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try{
            log.info("linkId::{}::UOFScoresConsumer 事件比分中心处理开始...",request.getLinkId());
            if(request.getData()==null){
                return;
            }
            if(StringUtils.isEmpty(request.getData().getDataSourceCode()) || StringUtils.isEmpty(request.getData().getThirdMatchSourceId())){
                log.info("linkId::{}::UOFScoresConsumer 查询三方赛事ID 参数错误：{},{}", request.getLinkId(), request.getData().getDataSourceCode(),request.getData().getThirdMatchSourceId());
                return;
            }
            //查询三方赛事
            log.info("linkId::{}::UOFScoresConsumer 查询三方赛事ID：{},{}", request.getLinkId(), request.getData().getDataSourceCode(),request.getData().getThirdMatchSourceId());
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(request.getData().getDataSourceCode(),request.getData().getThirdMatchSourceId());
            if(thirdMatchInfo==null){
                log.info("linkId::{}::UOFScoresConsumer 查询三方赛事 无三方赛事：{},{}", request.getLinkId(), request.getData().getDataSourceCode(), request.getData().getThirdMatchSourceId());
                return ;
            }
            Long sportId = validateSportId(request.getData().getDataSourceCode(), String.valueOf(request.getData().getSportId()));
            log.info("linkId::{}::UOFScoresConsumer 三方赛种ID：{},，标准赛种ID：{}",
                    request.getLinkId(),request.getData().getSportId(),sportId);
            request.getData().setSportId(sportId.toString());
            //校验数据合法性
            if(!checkUofDataFlag(request,thirdMatchInfo,sportId)){
                return;
            }
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(thirdMatchInfo.getReferenceId());
            if(standardMatchInfo==null){
                log.info("::{}::UOFScoresConsumer 标准赛事不存在 ：三方赛事ID：{}，标准赛事ID：{}",request.getLinkId(),thirdMatchInfo.getId(),standardMatchInfo.getId());
                return;
            }
            log.info("linkId::{}::UOFScoresConsumer 事件比分中心获取三方赛事成功,三方赛事ID:{},标准赛事ID:{}",request.getLinkId(),thirdMatchInfo.getId(),thirdMatchInfo.getReferenceId());
            //获取锁
            String key ="MATCH_SCORES:UOFScoresConsumer:"+thirdMatchInfo.getId();
            if(redisService.tryLock(key,request.getLinkId(),2,5)) {
            log.info("UOFScoresConsumer,linkId={},统计事件比分中心获取redis分布式锁结束：{}",request.getLinkId(), stopWatch.getTotalTimeMillis());
                //1.根据赛种获得比分 没有则新增
                try {
                    MatchScoresInfo matchScoresInfo = matchScoreInfoRepository.selectByExample(thirdMatchInfo.getId(),SourceTypeEnum.UOF.getCode());

                    if(matchScoresInfo==null){
                        matchScoresInfo = initUofMatchScoresInfo(thirdMatchInfo, request.getData(),matchScoresInfo);
                    }else{
                        matchScoresInfo = uofScoresService.checkScores(thirdMatchInfo, request.getData(),matchScoresInfo);
                    }
                    //设置临时参数（主方法未传参linkId,设置临时参数，减少改动量）
                    standardMatchInfo.setRemark(request.getLinkId());
                    //各球种统计比分处理
                    uofScoresService.processUofScores(matchScoresInfo, request.getData(),standardMatchInfo);

                    //B02数据特殊校验
                    if(DataSourceCodeEnum.BC.getCode().equals(request.getData().getDataSourceCode())){
                        //B02综合赛种特殊处理
                        if(DataSourceConstant.B02_SPORT_TYPES.contains(sportId)){
                            if (matchScoreInfoRepository.checkB02ScoresSource(Long.parseLong(request.getData().getSportId()))!=1L){
                                log.info("linkId::{}::UOFScoresConsumer 当前赛种:{},B02数据通道为事件比分,无需下发统计比分！",request.getLinkId(),sportId);
                                return;
                            }
                        }
                        //足球、篮球针对B02统计比分做处理：如果存在事件比分，则不下发统计比分
                        if(SportTypeEnum.FOOTBALL.getValue().equals(sportId) || SportTypeEnum.BASKETBALL.getValue().equals(sportId) ){
                            MatchScoresInfo liveScore = matchScoreInfoRepository.selectByExample(thirdMatchInfo.getId(),SourceTypeEnum.LIVE_DATA.getCode());
                            //判断事件比分是否存在
                            if(liveScore != null){
                                log.info("linkId::{}::UOFScoresConsumer 当前赛种:{},B02数据已存在事件比分,无需下发统计比分！",request.getLinkId(),sportId);
                                return;
                            }
                        }
                    }else{
                        //判断 是否有 livedata 没有直接下发
//                        sendMessageAndCheckType(thirdMatchInfo,matchScoresInfo,request);
                        if(!scoresService.isLivedataStoped(thirdMatchInfo.getId())){
                            log.info("::{}::isLivedataStoped 0三方赛事ID：{} 存在事件比分，判断不下发UOF比分",request.getLinkId(),thirdMatchInfo.getId());
                            return;
                        }
                    }
                    //主客队相反
                    scoresService.changePDHomeAwayScores(matchScoresInfo, thirdMatchInfo);
                    //推送三方比分数据 和 标准比分（标准比分不处理 5网，8乒，9排，10羽）
                    //3.判断 livedata 的时间是否 》2分钟 则下发
                    scoresProducer.sendUofScoreToMQ(thirdMatchInfo,matchScoresInfo,request.getLinkId());
                    log.info("linkId::{}::UOFScoresConsumer 统计比分中心处理结束：{}",request.getLinkId(),stopWatch.getTotalTimeMillis());


                    //保存标准比分数据（只处理 5网，8乒，9排，10羽）
                    this.saveStandardScores(matchScoresInfo, thirdMatchInfo,request);
                    //特殊赛种推送到  WS使用的比分数据,SCORE_CENTER_MATCH_SCORES
                    if(DataSourceConstant.STANDARC_SCORE_SPORTIDS.contains(sportId)){
                        pushMatchStandScores(thirdMatchInfo.getReferenceId(), request.getLinkId());
                    }
                }catch (Exception e){
                    log.error("linkId::{}::UOFScoresConsumer 三方赛事ID：{},error：", request.getLinkId(), thirdMatchInfo.getId(), e);
                }finally {
                    redisService.unLock(key,request.getLinkId());
                }
            }
        } finally {
            stopWatch.stop();
        }
        log.info("linkId::{}::UOFScoresConsumer 事件比分中心处理结束耗时：{}", request.getLinkId(), stopWatch.getTotalTimeMillis());
    }

    private void createMatchTimeInfo(ThirdMatchInfo thirdMatchInfo, MatchStatisticsInfoDTO data, MatchScoresInfo matchScoresInfo) {
        MatchTimeInfo matchTimeInfo = new MatchTimeInfo();
        matchTimeInfo.setCreateTime(System.currentTimeMillis());
        matchTimeInfo.setModifyTime(System.currentTimeMillis());
        matchTimeInfo.setDataSourceType(matchScoresInfo.getDataSourceType());
        matchTimeInfo.setTimeGo(1);
        matchTimeInfo.setThirdMatchId(matchScoresInfo.getThirdMatchId());
        if (data.getPeriod() != null)
            matchTimeInfo.setPeriod(data.getPeriod().longValue());
        if (data.getSecondsMatchStart() != null)
            matchTimeInfo.setSecondFromStart(data.getSecondsMatchStart().longValue());
        if (matchScoresInfo.getSportId().equals(2L) && thirdMatchInfo.getMatchType() != null && thirdMatchInfo.getMatchType() == 3) {
            if (data.getRemainingTime() != null)
                matchTimeInfo.setSecondFromStart(data.getRemainingTime().longValue());
            if (data.getSecondsMatchStart() != null)
                matchTimeInfo.setRemainingTime(data.getSecondsMatchStart().longValue());
        }
        matchTimeInfo.setMatchLength(matchScoresInfo.getMatchLength());
        if (data.getRemainingTime() != null)
            matchTimeInfo.setRemainingTime(data.getRemainingTime().longValue());
        matchTimeInfo.setEventTime(System.currentTimeMillis());
        matchTimeInfo.setId(matchScoresInfo.getId());
        //UOF 取  getRemainingTime
        if (matchScoresInfo.getSportId().equals(2L)){
            if (data.getRemainingTime() != null)
                matchTimeInfo.setSecondFromStart(data.getRemainingTime().longValue());
        }
//        matchTimeInfoMapper.insert(matchTimeInfo);
        matchTimeInfoRepository.updateByPrimaryKey(matchTimeInfo);
    }

    private void updateSourceSourceTypeInfo(MatchScoresInfo matchScoresInfo) {
        MatchScoresSourceType matchScoresSourceType = matchScoresSourceTypeRepository.selectSourceSourceTypeByThirdMatchId(matchScoresInfo.getThirdMatchId());
        if (matchScoresSourceType == null) {
            //1.创建比分数据源关联表
            matchScoresSourceType = new MatchScoresSourceType();
            matchScoresSourceType.setId(matchScoresInfo.getThirdMatchId());
            matchScoresSourceType.setThirdMatchId(matchScoresInfo.getThirdMatchId());
            matchScoresSourceType.setModifyTime(matchScoresInfo.getModifyTime());
            matchScoresSourceType.setActiveType(1);
            matchScoresSourceType.setActiveMode(1);
            matchScoresSourceType.setCreateTime(matchScoresInfo.getCreateTime());
            matchScoresSourceType.setSourceType(matchScoresInfo.getDataSourceType());
//            matchScoresSourceTypeRepository.updateScoresSourceType(matchScoresSourceType);
            try {
                matchScoresSourceTypeRepository.insertScoresSourceType(matchScoresSourceType);
                log.info("thirdMatchId::{}::UOFScoresConsumer,比分切换关联表插入成功", matchScoresInfo.getThirdMatchId());
            } catch (Exception e) {
                log.error("thirdMatchId::{}::UOFScoresConsumer,比分切换关联表插入失败,数据库插入异常信息---{}", matchScoresInfo.getThirdMatchId(), e.getMessage(), e);
            }
        }
    }

    /**
     * 初始化uof比分
     * @param thirdMatchInfo
     * @param data
     * @param matchScoresInfo
     * @return
     */
    private MatchScoresInfo initUofMatchScoresInfo(ThirdMatchInfo thirdMatchInfo, MatchStatisticsInfoDTO data,MatchScoresInfo matchScoresInfo ) {
        //1.新建
        matchScoresInfo = new MatchScoresInfo();
        //1. matchScoresInfo 构建入库
        matchScoresInfo.setId(thirdMatchInfo.getId());
        matchScoresInfo.setDataSourceCode(data.getDataSourceCode());
        matchScoresInfo.setDataSourceType(SourceTypeEnum.UOF.getCode().toString());
        matchScoresInfo.setEventTime(System.currentTimeMillis());
        matchScoresInfo.setMatchLength(thirdMatchInfo.getMatchLength());
        if (thirdMatchInfo.getMatchLength() == null) {
            matchScoresInfo.setMatchLength(0);
        }
        if (thirdMatchInfo.getSportId().equals(2L) && thirdMatchInfo.getMatchType() != null && thirdMatchInfo.getMatchType() == 3) {
            matchScoresInfo.setMatchLength(3);
        }
        matchScoresInfo.setPeriod(data.getPeriod().longValue());
        if (data.getRemainingTime() != null)
            matchScoresInfo.setRemainingTime(data.getRemainingTime().longValue());
        if (data.getSecondsMatchStart() != null)
            matchScoresInfo.setSecondsMatchStart(data.getSecondsMatchStart().longValue());
        if (thirdMatchInfo.getSportId().equals(2L) && thirdMatchInfo.getMatchType() != null && thirdMatchInfo.getMatchType() == 3) {
            if (data.getRemainingTime() != null)
                matchScoresInfo.setSecondsMatchStart(data.getRemainingTime().longValue());
            if (data.getSecondsMatchStart() != null)
                matchScoresInfo.setRemainingTime(data.getSecondsMatchStart().longValue());
        }
        matchScoresInfo.setThirdMatchSourceId(data.getThirdMatchSourceId());
        matchScoresInfo.setThirdMatchId(thirdMatchInfo.getId());
        matchScoresInfo.setSportId(Long.parseLong(data.getSportId()));
        matchScoresInfo.setCreateTime(System.currentTimeMillis());
        matchScoresInfo.setModifyTime(matchScoresInfo.getCreateTime());
        //初始化比分时创建数据源关系，后续不再使用这张表，不需要每次都查询
        updateSourceSourceTypeInfo(matchScoresInfo);
        //初始化比分时创建赛事时间表，后续不再使用这张表，不需要每次都查询
        createMatchTimeInfo(thirdMatchInfo, data, matchScoresInfo);
        return matchScoresInfo;
//                matchScoresInfoMapper.insert(matchScoresInfo);
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);

    }

    /**
     * 校验统计数据合法性
     * @param request
     * @param thirdMatchInfo
     * @return
     */
    private boolean checkUofDataFlag(Request<MatchStatisticsInfoDTO> request,ThirdMatchInfo thirdMatchInfo,Long sportId) {
        if(request.getData() == null){
            return false;
        }
        log.info("linkId::{}::UOFScoresConsumer request-dataSourceCode：{},request-data-dataSourceCode: {}",
                request.getLinkId(), request.getDataSourceCode(), request.getData().getDataSourceCode());
        //非商业数据源过滤
        if(StringUtils.isEmpty(request.getData().getDataSourceCode()) || !VALID_DATA_SOURCES.contains(request.getData().getDataSourceCode())){
            log.info("linkId::{}::UOFScoresConsumer 事件比分中心处理,数据源编码不匹配：{}", request.getLinkId(),request.getData().getDataSourceCode());
            return false;
        }
        //0 阶段事件过滤
        if(ZERO.equals(request.getData().getPeriod())){
            log.info("linkId::{}::UOFScoresConsumer 事件比分中心处理,过滤0阶段事件", request.getLinkId());
            return false;
        }
        //非BT数据源统计有效赛种
        if(!DataSourceCodeEnum.BT.code.equals(request.getData().getDataSourceCode()) &&
                !DataSourceConstant.SPORT_TYPES.contains(sportId)) {
            log.info("linkId::{}::UOFScoresConsumer 非BT数据源/统计有效赛种! {} ",request.getLinkId(),sportId);
            return false;
        }
        //校验三方赛事信息
        if(thirdMatchInfo == null){
            log.info("linkId::{}::UOFScoresConsumer thirdMatchInfo 不存在 dataSource:{},thirdMatchSourceId:{}",request.getLinkId(),request.getData().getDataSourceCode(),request.getData().getThirdMatchSourceId());
            return false;
        }
        if(thirdMatchInfo.getReferenceId() == null || thirdMatchInfo.getReferenceId() == 0l){
            log.info("linkId::{}::UOFScoresConsumer 未绑定标准赛事，无需处理!",request.getLinkId());
            return false;
        }
        if(request.getData().getPeriod() == null){
            log.error("linkId::{}::UOFScoresConsumer 传入阶段ID为空!",request.getLinkId());
            return false;
        }
        //UOF_SCORE:2015041:OD  缓存当前赛事当前事件源的时间戳，半小时
        String redisKey = "UOF_SCORE:"+request.getData().getThirdMatchSourceId()+":"+request.getData().getDataSourceCode();
        Object redisValue = redisService.get(redisKey);
        if(redisValue != null){
            Long time = (Long) redisValue;
            if(request.getData().getModifyTime()<time){
                log.info("linkId::{}::UOFScoresConsumer 已消费到更迟的数据，本次不处理! {},{}",request.getLinkId(),request.getData().getModifyTime(),time);
                return false;
            }
        }
        redisService.set(redisKey,request.getData().getModifyTime(), RedisConfig.REDIS_MY_TIME);


        return true;
    }

    /**
     * 保存标准比分并下发（只处理 5网，8乒，9排，10羽）
     * @param matchScoresInfo
     * @param request
     */
    private void saveStandardScores(MatchScoresInfo matchScoresInfo, ThirdMatchInfo thirdMatchInfo,Request<MatchStatisticsInfoDTO> request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        if(!DataSourceConstant.STANDARC_SCORE_SPORTIDS.contains(matchScoresInfo.getSportId())){
            return;
        }
//        if(matchScoresInfo.getSportId()==1L || matchScoresInfo.getSportId() ==2L){
//            return;
//        }
        log.info("linkId::{}::saveStandardScores 保存标准统计比分开始...",request.getLinkId());
        try{
//            scoresService.changeHomeAway(matchScoresInfo, thirdMatchInfo);

            StandardSportMarketSell standardSportMarketSell = standardSportMarketSellRepository.selectThirdMatchInfoPrimaryKey(thirdMatchInfo.getReferenceId());
            if (standardSportMarketSell == null ) {
                log.info("linkId::{}::saveStandardScores 开售数据为空！",request.getLinkId());
                return;
            }
            if(!standardSportMarketSell.getBusinessEvent().equals(matchScoresInfo.getDataSourceCode())){
                log.info("linkId::{}::saveStandardScores DataSourceCode不匹配！{} : {}",request.getLinkId(),request.getData().getDataSourceCode(),standardSportMarketSell.getBusinessEvent());
                return;
            }
            StandardMatchScores score = scoresRedisHelp.getCatchStandScoreByMatchId(thirdMatchInfo.getReferenceId());
            //更新对应数据源的标准比分
            if(null == score){
                log.info("linkId::{}::saveStandardScores 库中现有标准统计比分数据为空！",request.getLinkId());
                return;
            }
            MatchStatisticsInfoDTO data = request.getData();
            String scoresJson = score.getScoreJson();
            Long periodId = Long.parseLong(data.getPeriod().toString());
            Integer firstNum = 0;
            //1.发送 标准比分
            MatchEventInfo scoreDataDto = new MatchEventInfo();
            scoreDataDto.setLinkId(request.getLinkId());
            scoreDataDto.setDataSourceCode(request.getDataSourceCode());
            scoreDataDto.setSourceType(SourceTypeEnum.UOF.getCode());
            scoreDataDto.setSportId(score.getSportId());
            scoreDataDto.setMatchPeriodId(periodId);
            scoreDataDto.setSecondsFromStart(data.getSecondsMatchStart()==null ? 0 :Long.parseLong(data.getSecondsMatchStart().toString()));
            /**网球需要局数*/
            scoreDataDto.setSecondNum(request.getData().getSetCount());
            scoreDataDto.setFirstNum(firstNum);
            scoreDataDto.setStandardMatchId(standardSportMarketSell.getMatchInfoId());
            if(StringUtils.isNotEmpty(scoresJson)){
                //统计比分调用事件统计逻辑 同步比分
                this.processStandardMatchScores(matchScoresInfo,score,scoreDataDto);
            }else{
                scoresJson = matchScoresInfo.getScoresJson();
                score.setScoreJson(scoresJson);
            }

            score.setThirdMatchId(matchScoresInfo.getThirdMatchId());
            score.setUpdateTime(System.currentTimeMillis());
            scoresRedisHelp.saveCatchStandScore(score);
            log.info("linkId::{}::saveStandardScores 标准比分更新完成,发送标准比分开始", request.getLinkId());
            if (thirdMatchInfo.getReferenceId() == null) {
                log.info("检查开售ifMatchSoldByThirdMatchId,standardMatchInfoId:{},standardSportMarketSell.getReferenceId为空,无需处理！",score.getMatchId());
                return;
            }
            if(standardSportMarketSell.getPreMatchSellStatus()==null&&standardSportMarketSell.getLiveMatchSellStatus()==null){
                log.info("检查开售ifMatchSoldByThirdMatchId,standardMatchInfoId:{},standardSportMarketSell sellStatus为空,无需处理！",score.getMatchId());
                return;
            }
            if(standardSportMarketSell.getPreMatchSellStatus()!=null&&standardSportMarketSell.getLiveMatchSellStatus()!=null){
                if(standardSportMarketSell.getPreMatchSellStatus().equals("Unsold")&&standardSportMarketSell.getLiveMatchSellStatus().equals("Unsold")){
                    log.info("检查开售ifMatchSoldByThirdMatchId,standardMatchInfoId:{},standardSportMarketSell sellStatus都为Unsold,无需处理！",score.getMatchId());
                    return;
                }
            }
            //主客队相反
            scoresService.changePDHomeAwayScores(score, thirdMatchInfo);
            CommonStandardScoresDto commonScoresDto = messageBuilderUtils.buildStandardMatchScoreCommonScoresDto(score,scoreDataDto,matchScoresInfo);
//            commonScoresDto.setPeriodId(periodId);
            scoresProducer.sendStandardMatchScores(commonScoresDto);
        }catch (Exception e){
            log.error("linkId::{}::saveStandardScores 更新标准比分统计比分异常,Exception:",request.getLinkId(), e);
        }
        stopWatch.stop();
        log.info("linkId::{}::saveStandardScores 保存标准统计比分结束..耗时：{}",request.getLinkId(),stopWatch.getTotalTimeMillis());
    }

    /**
     * 比分计算逻辑(标准比分不下发的赛种 5网，8乒，9排，10羽赛种)
     * @param matchScoresInfo 比分
     * @param standardMatchScores 事件
     * @throws Exception
     */
    private void processStandardMatchScores(MatchScoresInfo matchScoresInfo, StandardMatchScores standardMatchScores,MatchEventInfo data) throws Exception {
        //1.根据球种计算比分且入库
        liveDataScoresService.calcStandardMatchScores(matchScoresInfo,standardMatchScores,data);
    }

    /**
     * 获取当前阶段对应匹配的setScore里面的firstNum
     * @param currentPeriod
     * @return
     */
    private Integer getSetPeriod(Integer currentPeriod) {
        Map<Integer,Integer> periodMap = new HashMap<>();
        periodMap.put(8,1);
        periodMap.put(301,1);

        periodMap.put(9,2);
        periodMap.put(302,2);

        periodMap.put(10,3);
        periodMap.put(303,3);

        periodMap.put(11,4);
        periodMap.put(304,4);

        periodMap.put(12,5);
        periodMap.put(305,5);

        periodMap.put(441,6);
        periodMap.put(306,6);

        periodMap.put(442,7);
        return periodMap.get(currentPeriod);
    }

    /**
     * 根据标准比分重新计算局比分
     * @param scoresJson 标准比分
     * @return json
     */
    private String calcStandScores(String scoresJson,Long sportId) {
        JSONObject periodScores = JSONObject.parseObject(scoresJson);
        Map<Long, TennisScores> allPeriodScores = JsonMapUtils.parseTennisMap(periodScores);
        //取出原-1比分
        TennisScores whosScore = allPeriodScores.get(WHOLE_MATCH);
        log.info("calcStandScores,-1比分：{}",whosScore);
        Integer tgHome = 0;
        Integer tgAway = 0;
        Integer setHome = 0;
        Integer setAway = 0;
        int winScore = getSportWinScore(sportId);
        for (Long periodId : allPeriodScores.keySet()) {
            List<Long> scoreCenterPeriod = Arrays.asList(8L, 9L, 10L, 11L, 12L,441L,442L);
            if(!scoreCenterPeriod.contains(periodId)){
                continue;
            }
            TennisScores cc = allPeriodScores.get(periodId);
            tgHome+=cc.getSetScore().getHome();
            tgAway+=cc.getSetScore().getAway();

            if(cc.getSetScore().getHome()>= winScore+1 || cc.getSetScore().getAway() >= winScore+1 ){
                if(cc.getSetScore().getHome()>cc.getSetScore().getAway() ){
                    setHome = setHome +1;
                }else{
                    setAway = setAway +1;
                }
            }else{
                if(cc.getSetScore().getHome()>cc.getSetScore().getAway()){
                    if(cc.getSetScore().getHome()>=winScore && cc.getSetScore().getHome() - cc.getSetScore().getAway() >= 2){
                        setHome = setHome +1;
                    }
                }else if (cc.getSetScore().getHome()<cc.getSetScore().getAway()){
                    if(cc.getSetScore().getAway()>=winScore && cc.getSetScore().getAway() - cc.getSetScore().getHome() >= 2){
                        setAway = setAway +1;
                    }
                }
            }
        }
        //计算后放入-1
        whosScore.getMatchScore().setHome(setHome);
        whosScore.getMatchScore().setAway(setAway);
        whosScore.getSetScore().setHome(tgHome);
        whosScore.getSetScore().setAway(tgAway);
        allPeriodScores.put(WHOLE_MATCH,whosScore);
        log.info("calcStandScores,-1比分：{}",whosScore);
        return scoresJson;

    }
    /**
     * 各球种判定胜的得分
     * @param sportId
     * @return
     */
    public Integer getSportWinScore(Long sportId){
        Map<Long, Integer> map = new HashMap<>();
        map.put(5L,6);
        map.put(8L,11);
        map.put(9L,25);
        map.put(10L,21);
        return map.get(sportId) ==null ? 0 : map.get(sportId);
    }


    /**
     * WS 使用的比分数据
     * */
    public void pushMatchStandScores(Long standardMatchId,String linkId){
        Request<String> reqMessage = new Request<>();
        reqMessage.setLinkId(standardMatchId +"");
        StandardScoreCenter centerStand = new StandardScoreCenter();
        centerStand.setStandardMatchId(standardMatchId);
        //推送标识
        centerStand.setIndex(99);
        log.info("linkId::{}::pushMatchStandScores,uof 推送比分中心标准比分到WS服务:{}", linkId, centerStand);
        reqMessage.setData(JSONObject.toJSONString(centerStand, SerializerFeature.DisableCircularReferenceDetect));
        MessageBuilder<Request<String>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, reqMessage.getLinkId());
        rocketMqTemplate.send("SCORE_CENTER_MATCH_SCORES" +":" +reqMessage.getLinkId(), builder.build());
        log.info("linkId::{}::pushMatchStandScores,uof 推送比分中心标准比分到WS服务", linkId);

    }

    private void sendMessageAndCheckType(ThirdMatchInfo thirdMatchInfo, MatchScoresInfo matchScoresInfo,  Request<MatchStatisticsInfoDTO> request) {
        log.info("linkId::{}::sendMessageAndCheckType start", request.getLinkId());
//        //2.判断 是否有 livedata 没有直接下发
//        if(!DataSourceCodeEnum.BC.getCode().equals(matchScoresInfo.getDataSourceCode())){
//            if(!scoresService.isLivedataStoped(thirdMatchInfo.getId())){
//                return;
//            }
//        }
        //3.判断 livedata 的时间是否 》2分钟 则下发
        scoresProducer.sendToMQ(thirdMatchInfo,matchScoresInfo,request.getLinkId());
        log.info("linkId::{}::sendMessageAndCheckType end", request.getLinkId());
    }

    /**
     * 校验三方运动类型是否合法 并返回标准运动类型
     * @param  dataSourceCode       数据来源
     * @param  thirdSportId       运动类型
     * @return Map<String, Long>   三方运动类型和标准运动类型关系
     * */
    public Long validateSportId(String dataSourceCode, String thirdSportId){
        if(StringUtils.isBlank(thirdSportId)){
            throw new ApiException(PandaErrorCodeEnum.SPORT_ID_IS_NOTNULL.getErrorMsg());
        }
        //根据数据源获取数据源下三方数据源运动类型和运动类型对应关系
        Map<String, ThirdSportType> thirdSportId2Item = thirdSportTypeService.getThirdSportId2Item(dataSourceCode);
        if(CollectionUtils.isEmpty(thirdSportId2Item)){
            throw new ApiException(PandaErrorCodeEnum.DATASOURCE_NO_CHECK.getErrorMsg().replace("dataSourceCode",dataSourceCode));
        }
        ThirdSportType thirdSportType = thirdSportId2Item.get(thirdSportId);
        if(Objects.isNull(thirdSportType)){
            throw new ApiException(PandaErrorCodeEnum.SPORT_ID_ILLEGAL.getErrorMsg().replace("sportId",thirdSportId));
        }
        return thirdSportType.getReferenceId();
    }
}
