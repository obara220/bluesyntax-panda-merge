package com.panda.merge.mq.producer;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.aocollect.model.MatchScoresHistory;
import com.panda.aocollect.model.MatchScoresHistoryBasketball;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.EventCodeEnum;
import com.panda.merge.common.enums.MatchPeriodForMatchOverEnum;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.CacheConstant;
import com.panda.merge.constant.DataSourceConstant;
import com.panda.merge.dao.FtsMatchRelationDao;
import com.panda.merge.dto.*;
import com.panda.merge.dto.cache.BasketballCacheScores;
import com.panda.merge.dto.cache.FootballCacheScores;
import com.panda.merge.dto.resultScore.MatchResultScoreMsgVo;
import com.panda.merge.dto.scores.EditScoreResultStatusRequest;
import com.panda.merge.dto.scores.MatchScoresBetterDto;
import com.panda.merge.dto.scores.SportResultShowStatusDTO;
import com.panda.merge.mapper.MatchScoresInfoMapper;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.mapper.StandardSportMarketSellMapper;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.model.*;
import com.panda.merge.mq.message.CommonStandardScoresDto;
import com.panda.merge.mq.message.CommonThirdScoresDto;
import com.panda.merge.mq.message.DelayEventMessageDto;
import com.panda.merge.mq.message.MatchEventInfoMessage;
import com.panda.merge.mq.spare.SpareBaseProducer;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.repository.ScoresRedisHelp;
import com.panda.merge.repository.StandardMatchInfoRepository;
import com.panda.merge.repository.StandardSportMarketSellRepository;
import com.panda.merge.service.IScoresService;
import com.panda.merge.service.StandardSportMarketSellService;
import com.panda.merge.service.StandardSportTournamentService;
import com.panda.merge.service.ThirdSportTypeService;
import com.panda.merge.utils.JsonMapUtils;
import com.panda.merge.utils.MessageBuilderUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.time.LocalTime;
import java.util.*;

import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_EVENT_INFO_API;


/**
 * 比分消息类
 */
@Service
@Slf4j
public class ScoresProducer {
    @NacosValue(value = "${panda.data.mq.gateway.event:1}", autoRefreshed = true)
    private int pandaDataMqGatewayevent;
    @NacosValue(value = "${panda.data.mq.gateway.matchId:1,}", autoRefreshed = true)
    private String pandaDataMqGatewayMatchId;
    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    StandardSportMarketSellMapper standardSportMarketSellMapper;
    @Autowired
    IScoresService scoresService;
    @Autowired
    MessageBuilderUtils messageBuilderUtils;
    @Autowired
    RedisService redisService;
    @Autowired
    MatchScoresInfoMapper matchScoresInfoMapper;
    @Autowired
    ThirdMatchInfoMapper thirdMatchInfoMapper;
    @Autowired
    FtsMatchRelationDao ftsMatchRelationDao;
    @Autowired
    MatchScoreInfoRepository matchScoresInfoRepository;
    @Autowired
    StandardMatchInfoRepository standardMatchInfoRepository;
    @Autowired
    StandardSportMarketSellRepository standardSportMarketSellRepository;
    @Autowired
    private ScoresRedisHelp scoresRedisHelp;
    @Autowired
    SpareBaseProducer spareBaseProducer;
    @Autowired
    StandardSportTournamentService standardSportTournamentServiceImpl;
    @Autowired
    public ThirdSportTypeService thirdSportTypeService;
    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;
    /**
     * 推送三方比分和标准比分到下游
     * 标准比分不下发的赛种 5网，8乒，9排，10羽赛种
     * livedata
     */
    public void sendToMQ(ThirdMatchInfo thirdMatchInfo, MatchScoresInfo matchScoresInfo, MatchEventInfo data, StandardMatchInfo standardMatchInfo) {
        log.info("linkId::{}::sendToMQ start", data.getLinkId());
        //处理实时服务下发100，比分中心转换成999的问题
        if (data.getSportId().equals(2L)) {
            data.setMatchPeriodId(data.getMatchPeriodId()==100?data.getMatchPeriodId():matchScoresInfo.getPeriod());
        }
        if (data.getSportId().equals(1L)) {
            List<String> scoresEventCodes = EventCodeEnum.getScoresEventCodes();
            if (scoresEventCodes.contains(data.getEventCode())) {
                String eventKey = "SCORES_DELETE:" + data.getDataSourceCode() + ":" + data.getStandardMatchId() + data.getThirdEventId();
                redisService.set(eventKey, JSONObject.toJSON(data).toString(), 3600);
                log.info("linkId::{}::sendToMQ 缓存足球下发的比分事件信息,key:{}", data.getLinkId(), eventKey);
            }
        }
        if (data.getSportId().equals(1L) || data.getSportId().equals(2L)) {
            String eventKey = "SCORES:" + data.getDataSourceCode() + ":" + data.getId();
            redisService.set(eventKey, JSONObject.toJSON(data).toString(), 3600);
            log.info("linkId::{}::sendToMQ 缓存足蓝下发的比分事件信息,key:{}", data.getLinkId(), eventKey);
        }
        if ((!scoresService.ifMatchSoldByThirdMatchId(thirdMatchInfo, standardMatchInfo))) {
            log.info("linkId::{}::sendToMQ 该赛事未开售！scores:{}", data.getLinkId(),matchScoresInfo.getScoresJson());
            return;
        }
        //标准比分不处理 5网，8乒，9排，10羽赛种
        if (!DataSourceConstant.STANDARC_SCORE_SPORTIDS.contains(matchScoresInfo.getSportId())
               /* || data.getSportId()==1 || data.getSportId()==2L*/) {
            sendToBussiness(thirdMatchInfo, matchScoresInfo, data);
        }
        //1.发送 标准比分 (使用方:业务kerr) 3001 网乒羽排不下发标准比分topic
//            sendToBussiness(thirdMatchInfo,matchScoresInfo,data);
        //2.发送 三方比分 (使用方:操盘,结算2.0)
        sendToMatchManager(thirdMatchInfo, matchScoresInfo, data);
        //3.如果是999 则发给AO初盘
        if (MatchPeriodForMatchOverEnum.Ended999.value.equals(matchScoresInfo.getPeriod())) {
            sendAoMatchScores(matchScoresInfo, data.getLinkId(), data.getStandardMatchId());
            if (!DataSourceConstant.STANDARC_SCORE_SPORTIDS.contains(matchScoresInfo.getSportId())) {
                //4117校验阶段比分是否完整
                checkScores(matchScoresInfo.getScoresJson(),data.getLinkId(),999L,standardMatchInfo,data.getDataSourceCode());
            }
        }
        log.info("linkId::{}::sendToMQ end", data.getLinkId());
    }


    /**
     * 4117校验阶段比分是否完整
     * @param scoresJson
     * @param linkId
     */
    public void checkScores(String scoresJson, String linkId, Long matchPeriod, StandardMatchInfo standardMatchInfo,String dataSourceCode) {
        if(matchPeriod!=999L){
            log.info("linkId::{}::checkScores "+matchPeriod+",非999未结束，不校验比分！", linkId);
            return;
        }
        Long matchId = standardMatchInfo.getId();
        Boolean isFlag = checkSportScores(standardMatchInfo,scoresJson,linkId);
        log.info("{} 校验比分是否完整：{}",linkId,isFlag);
        if(!isFlag){
            //下发自动关闭
            StandardSportMarketSell standardSportMarketSell = standardSportMarketSellRepository.selectThirdMatchInfoPrimaryKey(matchId);
            if(!standardSportMarketSell.getBusinessEvent().equals(dataSourceCode)){
                log.info("linkId:{},数据源不匹配，不下发赛果关：主事件源：{}，{}",linkId,standardSportMarketSell.getBusinessEvent(),dataSourceCode);
                return;
            }
            standardSportMarketSell.setShowResultStatus(0);
            //刷新缓存
            standardSportMarketSellService.refreshCache(standardSportMarketSell);
            //入库
            StandardSportMarketSell sell = new StandardSportMarketSell();
            sell.setId(standardSportMarketSell.getId());
            sell.setShowResultStatus(0);
            standardSportMarketSellService.update(sell);
            //下发MQ
            EditScoreResultStatusRequest showStatus = new EditScoreResultStatusRequest();
            showStatus.setSportId(standardSportMarketSell.getSportId());
            showStatus.setStatus(0);
            showStatus.setStandardMatchId(matchId);
            //定义类型为1 比分不完整自动关闭赛果展示
            showStatus.setType(1);
            this.sendMatchShowStatus(showStatus,linkId);
        }
    }

    private Boolean checkSportScores(StandardMatchInfo standardMatchInfo, String scoresJson, String linkId) {
        if(standardMatchInfo.getMatchLength()==null){
            standardMatchInfo.setMatchLength(0);
        }
        if(standardMatchInfo.getRoundType()==null){
            standardMatchInfo.setRoundType(0);
        }
        int sport = 0;
        if(standardMatchInfo.getSportId()!=null){
            sport = standardMatchInfo.getSportId().intValue();
        }
        Integer matchLength = standardMatchInfo.getMatchLength();
        Integer rountType = standardMatchInfo.getRoundType();
        List<Long> sportPeriod = new ArrayList<>();
        switch (sport){
            case 1:
                sportPeriod = Arrays.asList(6L,7L);
                break;
            case 2:
                sportPeriod = Arrays.asList(13L,14L,15L,16L);
                if(matchLength==17){
                    sportPeriod = Arrays.asList(1L,2L);
                }else if(matchLength==73){
                    sportPeriod = Arrays.asList(21L);
                }
                break;
            case 3:
//                9 局  第5局结束 下发完赛判断为比分完整
//                7 局  第3局结束 下发完赛判断为比分完整
                //棒球默认9
                sportPeriod = Arrays.asList(401L,402L,403L,404L,405L,406L,407L,408L,409L,410L/*,411L,412L,413L,414L,415L,416L,417L,418L*/);
                if(rountType==7){
                    sportPeriod = Arrays.asList(401L,402L,403L,404L,405L,406L/*,407L,408L,409L,410L,411L,412L,413L,414L*/);
                }
                break;
            case 4:
                sportPeriod = Arrays.asList(1L,2L,3L);
                break;
            case 6:
                break;
            case 7:
                //斯洛克比分结构并非阶段ID为比分KEY，而是12345排列，所以用循环获取比分key
                //斯洛克赛制 ： 3,4,5,7,9,11,13,17,19,25,33,35
                if(rountType==null || rountType == 0){
                    rountType = 3;
                }
                if(rountType==3){
                    //三局两胜
                    sportPeriod = Arrays.asList(1L,2L);
                }else if(rountType==4){
                    //四局三胜
                    sportPeriod = Arrays.asList(1L,2L,3L);
                }else{
                    List<Long> list = new ArrayList<>();
                    for (int i = 1;i<=rountType/2+1;i++){
                        list.add((long) i);
                    }
                    sportPeriod = new ArrayList<>(list);
                }
                break;
            case 5:
            case 8:
            case 9:
            case 10:
            case 13:
                //羽毛球、乒乓球、排球、沙滩排球、网球 一样的赛制&阶段模式
                sportPeriod = Arrays.asList(8L,9L/*,10L*/);
                if(rountType==4){
                    sportPeriod = Arrays.asList(8L,9L,10L/*,11L*/);
                }else if(rountType==5){
                    sportPeriod = Arrays.asList(8L,9L,10L/*,11L,12L*/);
                }else if(rountType==7){
                    sportPeriod = Arrays.asList(8L,9L,10L,11L/*,12L,441L,442L*/);
                }
                break;
            case 11:
                break;
            case 12:
                break;
            case 14:
                break;
            case 37:
                //板球直接展示
                /*sportPeriod = Arrays.asList(8L);
                if(rountType==5){
                    sportPeriod = Arrays.asList(8L,9L);
                }*/
                break;
            default:
                break;
        }

        JSONObject periodScores = JSONObject.parseObject(scoresJson);
        Map<Long, Object> allPeriodScores= JsonMapUtils.parseObjectMap(periodScores);
        if(periodScores==null || allPeriodScores.isEmpty()){
            log.info("：：{}：：赛事结束无比分,{}",standardMatchInfo.getId(),linkId);
            return false;
        }
        if(!sportPeriod.isEmpty()){
             for(Long period:sportPeriod){
                 //获取到的阶段比分为空，则代表存在阶段无比分
                 Object cc = allPeriodScores.get(period);
                 if(cc==null){
                     log.info("：：{}：：赛事结束无比分：{},{}",standardMatchInfo.getId(),period,linkId);
                     return false;
                 }
             }
        }
        return true;
    }


    /**
     * 推送AO赛事比分
     *
     * @param matchScoresInfo
     * @param link
     * @param standardMatchId
     */
    private void sendAoMatchScores(MatchScoresInfo matchScoresInfo, String link, Long standardMatchId) {
        log.info("linkId::{}::sendAoMatchScores start 开始构建初盘比分下发standardMatchId:{}", link, standardMatchId);
        try {
            MatchScoresHistory matchScoresHistory = null;
            MatchScoresHistoryBasketball basketballMatchAoScores = null;
            if (matchScoresInfo.getSportId().equals(1L)) {
                matchScoresHistory = messageBuilderUtils.buildAoMatchScoresHistory(matchScoresInfo, standardMatchId);
            } else if (matchScoresInfo.getSportId().equals(2L)) {
                basketballMatchAoScores = messageBuilderUtils.buildBasketballAoMatchScoresHistory(matchScoresInfo, standardMatchId);
            } else {
                return;
            }
            if (null == matchScoresHistory && null == basketballMatchAoScores) {
                return;
            }
            com.panda.aocollect.dto.Request reqMessage = new com.panda.aocollect.dto.Request<>();
            reqMessage.setLinkId(link);
            boolean spareMqFlag = getSpareMqFlag(standardMatchId+"");
            if (matchScoresInfo.getSportId().equals(1L)) {
                reqMessage.setData(matchScoresHistory);
                reqMessage.setSportId(1L);
                MessageBuilder<com.panda.aocollect.dto.Request> builder = MessageBuilder.withPayload(reqMessage)
                        .setHeader(MessageConst.PROPERTY_KEYS, link);
                if (pandaDataMqGatewayevent == 2 && spareMqFlag) {
                    Request<MatchScoresHistory> request = new Request<>(matchScoresHistory, reqMessage.getLinkId(),
                            "MATCH_SCORES_HISTORY", reqMessage.getLinkId(), matchScoresInfo.getDataSourceCode());
                    spareBaseProducer.send(request);
                } else {
                    rocketMqTemplate.send("MATCH_SCORES_HISTORY:" + link, builder.build());
                }
            } else {
                reqMessage.setData(basketballMatchAoScores);
                reqMessage.setSportId(2L);
                MessageBuilder<com.panda.aocollect.dto.Request> builder = MessageBuilder.withPayload(reqMessage)
                        .setHeader(MessageConst.PROPERTY_KEYS, link);
                Request<MatchScoresHistoryBasketball> request = new Request<>(basketballMatchAoScores, reqMessage.getLinkId(),
                        "MATCH_SCORES_HISTORY", reqMessage.getLinkId(), matchScoresInfo.getDataSourceCode());
                if (pandaDataMqGatewayevent == 2 && spareMqFlag) {
                    spareBaseProducer.send(request);
                } else {
                    rocketMqTemplate.send("MATCH_SCORES_HISTORY:" + link, builder.build());
                }
            }
            log.info("linkId::{}::开始组装赛事比分信息并下发,topic:MATCH_SCORES_HISTORY,request={}", link, JSON.toJSONString(reqMessage));
        } catch (Exception e) {
            log.error("linkId::{}::处理数据发生异常:", link, e);
        }
    }

    private void sendToMatchManager(ThirdMatchInfo thirdMatchInfo, MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        log.info("linkId::{}::sendToMatchManager start", data.getLinkId());
        CommonThirdScoresDto commonScoresDto = messageBuilderUtils.buildThirdScoresDto(thirdMatchInfo, matchScoresInfo, data);
        //发布操作
        sendThirdMatchScores(commonScoresDto);
        log.info("linkId::{}::sendToMatchManager end", data.getLinkId());
    }

    private void sendToBussiness(ThirdMatchInfo thirdMatchInfo, MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        String linkId = data.getLinkId();
        log.info("linkId::{}::sendToBussiness start",linkId);
        //1.标准赛事绑定逻辑 判断当前比分是否为标准赛事的开售事件的比分
        if ((thirdMatchInfo.getReferenceId() == null || thirdMatchInfo.getReferenceId().equals(0l))) {
            log.info("linkId::{}::sendToBussiness 三方赛事标准比分有误 标准赛事id:{} 三方赛事id:{}", linkId, thirdMatchInfo.getReferenceId(), thirdMatchInfo.getId());
            return;
        }
        boolean isStandardScore = scoresService.checkStandardScore(thirdMatchInfo, matchScoresInfo);
        //2.获取标准赛事的开售的事件源
        if (!isStandardScore) {
            log.info("linkId::{}::sendToBussiness 标准比分有误 标准赛事id:{} 三方赛事id:{}", linkId, thirdMatchInfo.getReferenceId(), thirdMatchInfo.getId());
            return;
        }
        this.updateMatchScoreKey(thirdMatchInfo, matchScoresInfo);
        //数据组装
        CommonStandardScoresDto commonScoresDto = messageBuilderUtils.buildCommonScoresDto(thirdMatchInfo, matchScoresInfo, data);
//        log.info("{},下发三方比分的关联关系为： 三方赛事ID：{},标准赛事ID：{}",data.getLinkId(),thirdMatchInfo.getId(),thirdMatchInfo.getReferenceId());
        //发布操作
        sendStandardMatchScores(commonScoresDto);
        log.info("linkId::{}::sendToBussiness end",linkId);
        //发送比分MQ到V02
        sendV02Scores(commonScoresDto);
    }

    private void updateMatchScoreKey(ThirdMatchInfo thirdMatchInfo, MatchScoresInfo matchScoresInfo) {
        String matchScoreKey = "MATCH_INFO_SCORE_ID:" + thirdMatchInfo.getReferenceId();
        Long scoreId = matchScoresInfo.getId();
        redisService.set(matchScoreKey, scoreId, 7200);
    }


    @Autowired
    private RocketMQTemplate rocketMqTemplate;


    /**
     * 发送赛事标准比分到MQ
     *
     * @param commonScoresDto
     */
    public void sendStandardMatchScores(CommonStandardScoresDto commonScoresDto) {
        //足蓝 需要赋值AoMatchId字段
        if (1L == commonScoresDto.getSportId() || 2L == commonScoresDto.getSportId() || 8L == commonScoresDto.getSportId()) {
            commonScoresDto.setAoMatchId(scoresService.selectAoMatchId(commonScoresDto.getStandardMatchId()));
        }
//        //保存比分到redis
        saveFTScores(commonScoresDto);
        Request<CommonStandardScoresDto> reqMessage = new Request<>();
        reqMessage.setLinkId(commonScoresDto.getLinkedId());
        reqMessage.setData(commonScoresDto);
        reqMessage.setDataSourceCode(commonScoresDto.getDataSourceCode());
        //比分计算的时间
        if (commonScoresDto.getScoreTime() != null && commonScoresDto.getScoreTime() != 0) {
            reqMessage.setDataSourceTime(commonScoresDto.getScoreTime());
        }
        MessageBuilder<Request<CommonStandardScoresDto>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, commonScoresDto.getLinkedId());
        Request<CommonStandardScoresDto> request = new Request<>(commonScoresDto, reqMessage.getLinkId(), "STANDARD_MATCH_SCORES", reqMessage.getLinkId(), commonScoresDto.getDataSourceCode());
        boolean spareMqFlag = getSpareMqFlag(commonScoresDto.getStandardMatchId()+"");
        if (pandaDataMqGatewayevent == 2 && spareMqFlag) {
            spareBaseProducer.send(reqMessage);
        }else{
            rocketMqTemplate.asyncSend("STANDARD_MATCH_SCORES:" + commonScoresDto.getLinkedId(), builder.build(),new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("::{}::,STANDARD_MATCH_SCORES send successful", commonScoresDto.getLinkedId());
                }
                @Override
                public void onException(Throwable throwable) {
                    log.error("::{}::TOPIC={}，send fail; ", commonScoresDto.getLinkedId(), "STANDARD_MATCH_SCORES", throwable);
                }
            });
        }
        log.info("linkId::{}::开始组装赛事标准比分信息并下发,topic:STANDARD_MATCH_SCORES,request={}", commonScoresDto.getLinkedId(), JSON.toJSONString(reqMessage));
    }

    /**
     * 发送比分到比分网服务
     *
     * @param commonScoresDto
     */
    public void sendPlsScores(CommonStandardScoresDto commonScoresDto) {
        log.info("EVENT下发STANDARD_MATCH_SCORES_PLS：{}",commonScoresDto.getLinkedId());
        if(commonScoresDto.getPlsStandardTournamentId()!=null){
            StandardSportTournament tournment = standardSportTournamentServiceImpl.getItem(commonScoresDto.getPlsStandardTournamentId());
            if(tournment!=null){
                commonScoresDto.setPlsStandardTournamentId(tournment.getPlsStandardTournamentId());
            }
        }
        Request<CommonStandardScoresDto> reqMessage = new Request<>();
        reqMessage.setLinkId(commonScoresDto.getLinkedId());
        reqMessage.setData(commonScoresDto);
        reqMessage.setDataSourceCode(commonScoresDto.getDataSourceCode());
        //比分计算的时间
        if (commonScoresDto.getScoreTime() != null && commonScoresDto.getScoreTime() != 0) {
            reqMessage.setDataSourceTime(commonScoresDto.getScoreTime());
        }
        MessageBuilder<Request<CommonStandardScoresDto>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, commonScoresDto.getLinkedId());
        Request<CommonStandardScoresDto> request = new Request<>(commonScoresDto, reqMessage.getLinkId(), "STANDARD_MATCH_SCORES_PLS", reqMessage.getLinkId(), commonScoresDto.getDataSourceCode());
        request.setTag(commonScoresDto.getStandardMatchId()+"");
        boolean spareMqFlag = getSpareMqFlag(commonScoresDto.getStandardMatchId()+"");
        if (pandaDataMqGatewayevent == 2 && spareMqFlag) {
            spareBaseProducer.send(request);
        }else{
            rocketMqTemplate.asyncSend("STANDARD_MATCH_SCORES_PLS:" + commonScoresDto.getLinkedId(), builder.build(),new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("::{}::,STANDARD_MATCH_SCORES_PLS send successful", commonScoresDto.getLinkedId());
                }
                @Override
                public void onException(Throwable throwable) {
                    log.error("::{}::TOPIC={}，send fail; ", commonScoresDto.getLinkedId(), "STANDARD_MATCH_SCORES_PLS", throwable);
                }
            });
        }
        log.info("linkId::{}::开始组装赛事标准比分信息并下发到比分网,topic:STANDARD_MATCH_SCORES_PLS,request={}", commonScoresDto.getLinkedId(), JSON.toJSONString(reqMessage));
    }

    /**
     * 爆球板事件触发发送比分到比分网服务
     * @param event
     * @param score
     * @param thirdMatchInfo
     * @param standardMatchInfo
     */
    public void sendPlsScoresPD(MatchEventInfo event,MatchScoresInfo score,ThirdMatchInfo thirdMatchInfo,StandardMatchInfo standardMatchInfo) {
        log.info("PD下发STANDARD_MATCH_SCORES_PLS：{}",event.getLinkId());
        //数据组装
        CommonStandardScoresDto commonScoresDto = messageBuilderUtils.buildCommonScoresDto(thirdMatchInfo, score, event);
        if(standardMatchInfo.getStandardTournamentId()!=null){
            StandardSportTournament tournment = standardSportTournamentServiceImpl.getItem(standardMatchInfo.getStandardTournamentId());
            if(tournment!=null){
                commonScoresDto.setPlsStandardTournamentId(tournment.getPlsStandardTournamentId());
            }
        }
        commonScoresDto.setMatchStatus(standardMatchInfo.getMatchStatus());
        commonScoresDto.setPlsStandardMatchId(standardMatchInfo.getPlsStandardMatchId());
        Request<CommonStandardScoresDto> reqMessage = new Request<>();
        reqMessage.setLinkId(commonScoresDto.getLinkedId());
        reqMessage.setData(commonScoresDto);
        reqMessage.setDataSourceCode(commonScoresDto.getDataSourceCode());
        //比分计算的时间
        if (commonScoresDto.getScoreTime() != null && commonScoresDto.getScoreTime() != 0) {
            reqMessage.setDataSourceTime(commonScoresDto.getScoreTime());
        }
        MessageBuilder<Request<CommonStandardScoresDto>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, commonScoresDto.getLinkedId());
        Request<CommonStandardScoresDto> request = new Request<>(commonScoresDto, reqMessage.getLinkId(), "STANDARD_MATCH_SCORES_PLS", reqMessage.getLinkId(), commonScoresDto.getDataSourceCode());
        boolean spareMqFlag = getSpareMqFlag(commonScoresDto.getStandardMatchId()+"");
        if (pandaDataMqGatewayevent == 2 && spareMqFlag) {
            spareBaseProducer.send(request);
        }else{
            rocketMqTemplate.asyncSend("STANDARD_MATCH_SCORES_PLS:" + commonScoresDto.getLinkedId(), builder.build(),new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("::{}::,STANDARD_MATCH_SCORES_PLS send successful", commonScoresDto.getLinkedId());
                }
                @Override
                public void onException(Throwable throwable) {
                    log.error("::{}::TOPIC={}，send fail; ", commonScoresDto.getLinkedId(), "STANDARD_MATCH_SCORES_PLS", throwable);
                }
            });
        }
        log.info("linkId::{}::开始组装赛事标准比分信息并下发到比分网,topic:STANDARD_MATCH_SCORES_PLS,request={}", commonScoresDto.getLinkedId(), JSON.toJSONString(reqMessage));
    }

    /**
     * 保存redis缓存比分;给赔率服务调用获取赔率基准分
     *
     * @param commonScoresDto
     */
    public void saveFTScores(CommonStandardScoresDto commonScoresDto) {
        if(commonScoresDto.getScores()==null){
            return;
        }
        String jsonScore = JSONUtil.toJsonStr(commonScoresDto.getScores());
        if (commonScoresDto.getSportId().equals(1L)) {
            //加时赛上半场
            Map<Long, FootballScores> scoresMap = JSONObject.parseObject(jsonScore, new TypeReference<Map<Long, FootballScores>>() {});
            if(scoresMap==null){
               log.info("比分为空,缓存失败:{}",commonScoresDto.getLinkedId());
               return;
            }
            FootballCacheScores footballCacheScores = null;
            try{
                footballCacheScores = cacheFootballScores(commonScoresDto, scoresMap);
            }catch(Exception e){
                log.error("缓存比分异常：{}",commonScoresDto.getLinkedId(),e);
            }
            String key = CacheConstant.FOOTBALL_STANDARD_MATCH_SCORES + commonScoresDto.getStandardMatchId();
            Object jsonObject = JSONObject.toJSON(footballCacheScores);
            key = DigestUtil.md5Hex(key);
            //缓存比分信息
            if(jsonObject==null){
                log.info("比分为空:{}",commonScoresDto.getLinkedId());
                return;
            }
            redisService.set(key, jsonObject.toString(), 3600*12);
            log.info("linkId::{}::缓存标准比分：足球比分,redisKey:FOOTBALL_STANDARD_MATCH_SCORES:"+commonScoresDto.getStandardMatchId()+",Scores={}", commonScoresDto.getLinkedId(), JSON.toJSONString(jsonObject));


        }else if(commonScoresDto.getSportId().equals(2L)) {
            BasketballCacheScores scores = new BasketballCacheScores();
            try {
                Map<String, Object> basketballAscore = commonScoresDto.getAllScores();
                if(basketballAscore!=null){
                    if(basketballAscore.get("wholeScore")!=null){
                        scores.setWholeScores((CommonItem) basketballAscore.get("wholeScore"));
                    }
                    if(basketballAscore.get("periodOneScore")!=null){
                        scores.setPeriodOneScore((CommonItem) basketballAscore.get("periodOneScore"));
                    }
                    if(basketballAscore.get("periodTwoScore")!=null){
                        scores.setPeriodTwoScore((CommonItem) basketballAscore.get("periodTwoScore"));
                    }
                }
                Map<Long, BasketballScores> scoresMap = JSONObject.parseObject(jsonScore, new TypeReference<Map<Long, BasketballScores>>() {});
                BasketballScores firstScores  = scoresMap.get(13L);
                if(firstScores!=null){
                    scores.setFirstScores(firstScores.getMatchScore());
                }
                BasketballScores secondScores = scoresMap.get(14L);
                if(secondScores!=null){
                    scores.setSecondScores(secondScores.getMatchScore());
                }
                BasketballScores thirdScores  = scoresMap.get(15L);
                if(thirdScores!=null){
                    scores.setThirdScores(thirdScores.getMatchScore());
                }
                BasketballScores fourthScores = scoresMap.get(16L);
                if(fourthScores!=null){
                    scores.setFourthScores(fourthScores.getMatchScore());
                }
            }catch(Exception e){
                log.error("缓存比分异常：{}",commonScoresDto.getLinkedId(),e);
            }
            String key = CacheConstant.BASKETBALL_STANDARD_MATCH_SCORES + commonScoresDto.getStandardMatchId();
            Object jsonObject = JSONObject.toJSON(scores);
            key = DigestUtil.md5Hex(key);
            //缓存比分信息
            redisService.set(key, jsonObject.toString(), 3600*12);
            log.info("linkId::{}::缓存标准比分：篮球比分,redisKey:BASKETBALL_STANDARD_MATCH_SCORES:"+commonScoresDto.getStandardMatchId()+",Scores={}", commonScoresDto.getLinkedId(), JSON.toJSONString(jsonObject));

        }


    }

    /**
     * 组装足球缓存比分 80074
     * @param commonScoresDto
     * @param scoresMap
     * @return
     */
    private FootballCacheScores cacheFootballScores(CommonStandardScoresDto commonScoresDto, Map<Long, FootballScores> scoresMap) {
        FootballCacheScores footballCacheScores = new FootballCacheScores();
        FootballScores firstScore = scoresMap.get(6L);
        if(firstScore==null){
            firstScore = new FootballScores(6L);
            //上半场比分
            footballCacheScores.setHfCorner(new CommonItem(0,0,true));
            footballCacheScores.setHfGoal(new CommonItem(0,0,true));
            footballCacheScores.setHfFaCard(new CommonItem(0,0,true));
            footballCacheScores.setHfRedCard(new CommonItem(0,0,true));
            footballCacheScores.setHfYellowCard(new CommonItem(0,0,true));
        }else{
            footballCacheScores.setHfCorner(firstScore.getCorner());
            footballCacheScores.setHfGoal(firstScore.getGoal());
            footballCacheScores.setHfFaCard(firstScore.getFaCard());
            footballCacheScores.setHfRedCard(firstScore.getRedCard());
            footballCacheScores.setHfYellowCard(firstScore.getYellowCard());
        }
        FootballScores secondScore = scoresMap.get(7L);
        if(secondScore == null){
            secondScore = new FootballScores(7L);
            //下半场比分
            footballCacheScores.setHtCorner(new CommonItem(0,0,true));
            footballCacheScores.setHtGoal(new CommonItem(0,0,true));
            footballCacheScores.setHtFaCard(new CommonItem(0,0,true));
            footballCacheScores.setHtRedCard(new CommonItem(0,0,true));
            footballCacheScores.setHtYellowCard(new CommonItem(0,0,true));
        }else{
            footballCacheScores.setHtCorner(secondScore.getCorner());
            footballCacheScores.setHtGoal(secondScore.getGoal());
            footballCacheScores.setHtFaCard(secondScore.getFaCard());
            footballCacheScores.setHtRedCard(secondScore.getRedCard());
            footballCacheScores.setHtYellowCard(secondScore.getYellowCard());
        }
        //全场比分,常规赛
        footballCacheScores.setGoal(new CommonItem(firstScore.getGoal().getHome()+secondScore.getGoal().getHome(),firstScore.getGoal().getAway()+secondScore.getGoal().getAway(),true));
        footballCacheScores.setFaCard(new CommonItem(firstScore.getFaCard().getHome()+secondScore.getFaCard().getHome(), firstScore.getFaCard().getAway()+secondScore.getFaCard().getAway(),true));
        footballCacheScores.setYellowCard(new CommonItem(firstScore.getYellowCard().getHome() + secondScore.getYellowCard().getHome(), firstScore.getYellowCard().getAway()+secondScore.getYellowCard().getAway(),true));
        footballCacheScores.setRedCard(new CommonItem(firstScore.getRedCard().getHome()+secondScore.getRedCard().getHome(), firstScore.getRedCard().getAway()+secondScore.getRedCard().getAway(), true));
        footballCacheScores.setCorner(new CommonItem(firstScore.getCorner().getHome()+secondScore.getCorner().getHome(),firstScore.getCorner().getAway() + secondScore.getCorner().getAway(),true));

        //加时赛上半场
        FootballScores ftOverTimeScore = scoresMap.get(41L);
        if(ftOverTimeScore==null) {
            ftOverTimeScore = new FootballScores(41L);
            //加时上半场比分
            footballCacheScores.setOverTimeHfGoal(new CommonItem(0,0,true));
            footballCacheScores.setOverTimeHfFaCard(new CommonItem(0,0,true));
            footballCacheScores.setOverTimeHfCorner(new CommonItem(0,0,true));
            footballCacheScores.setOverTimeHfRedCard(new CommonItem(0,0,true));
            footballCacheScores.setOverTimeHfYellowCard(new CommonItem(0,0,true));
        }else {
            //加时上半场比分
            footballCacheScores.setOverTimeHfGoal(ftOverTimeScore.getGoal());
            footballCacheScores.setOverTimeHfFaCard(ftOverTimeScore.getFaCard());
            footballCacheScores.setOverTimeHfCorner(ftOverTimeScore.getCorner());
            footballCacheScores.setOverTimeHfRedCard(ftOverTimeScore.getRedCard());
            footballCacheScores.setOverTimeHfYellowCard(ftOverTimeScore.getYellowCard());
        }
        FootballScores lastOverTimeScore = scoresMap.get(42L);
        if(lastOverTimeScore==null) {
            lastOverTimeScore = new FootballScores(42L);
            //加时下半场比分
            footballCacheScores.setOverTimeHtGoal(new CommonItem(0,0,true));
            footballCacheScores.setOverTimeHtFaCard(new CommonItem(0,0,true));
            footballCacheScores.setOverTimeHtCorner(new CommonItem(0,0,true));
            footballCacheScores.setOverTimeHtRedCard(new CommonItem(0,0,true));
            footballCacheScores.setOverTimeHtYellowCard(new CommonItem(0,0,true));
        }else{
            //加时下半场比分
            footballCacheScores.setOverTimeHtGoal(lastOverTimeScore.getGoal());
            footballCacheScores.setOverTimeHtFaCard(lastOverTimeScore.getFaCard());
            footballCacheScores.setOverTimeHtCorner(lastOverTimeScore.getCorner());
            footballCacheScores.setOverTimeHtRedCard(lastOverTimeScore.getRedCard());
            footballCacheScores.setOverTimeHtYellowCard(lastOverTimeScore.getYellowCard());
        }
        footballCacheScores.setOverTimeGoal(new CommonItem(ftOverTimeScore.getGoal().getHome()+lastOverTimeScore.getGoal().getHome(),ftOverTimeScore.getGoal().getAway()+lastOverTimeScore.getGoal().getAway(),true));
        footballCacheScores.setOverTimeFaCard(new CommonItem(ftOverTimeScore.getFaCard().getHome()+lastOverTimeScore.getFaCard().getHome(), ftOverTimeScore.getFaCard().getAway()+lastOverTimeScore.getFaCard().getAway(),true));
        footballCacheScores.setOverTimeYellowCard(new CommonItem(ftOverTimeScore.getYellowCard().getHome()+lastOverTimeScore.getYellowCard().getHome(),  ftOverTimeScore.getYellowCard().getAway()+lastOverTimeScore.getYellowCard().getAway(),true));
        footballCacheScores.setOverTimeRedCard(new CommonItem(ftOverTimeScore.getRedCard().getHome()+lastOverTimeScore.getRedCard().getHome(), ftOverTimeScore.getRedCard().getAway()+lastOverTimeScore.getRedCard().getAway(), true));
        footballCacheScores.setOverTimeCorner(new CommonItem(ftOverTimeScore.getCorner().getHome()+lastOverTimeScore.getCorner().getHome(),ftOverTimeScore.getCorner().getAway()+lastOverTimeScore.getCorner().getAway(),true));
        Map<String, Object> allScores = commonScoresDto.getAllScores();
        if(allScores!=null && allScores.get("penaltyShootout")!=null){
            footballCacheScores.setPenaltyScores((CommonItem) allScores.get("penaltyShootout"));
        }
        return footballCacheScores;
    }

    /**
     * 发送给赛程,结算服务
     */
    public void sendThirdMatchScores(CommonThirdScoresDto commonScoresDto) {
        Request<CommonThirdScoresDto> reqMessage = new Request<>();
        reqMessage.setLinkId(commonScoresDto.getLinkedId());
        //过滤篮球6分钟比分
//        extraBasketballSixScores(commonScoresDto);
        //87260 【生产】【产品】V02/N02不做结算数据源，不下发至结算
        if(DataSourceCodeEnum.N02.code.equals(commonScoresDto.getDataSourceCode())|| DataSourceCodeEnum.TS.code.equals(commonScoresDto.getDataSourceCode())){
            return;
        }
        reqMessage.setData(commonScoresDto);
        if (commonScoresDto.getScoreTime() != null && commonScoresDto.getScoreTime() != 0)
            reqMessage.setDataSourceTime(commonScoresDto.getScoreTime());
        MessageBuilder<Request<CommonThirdScoresDto>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, commonScoresDto.getLinkedId());
        Request<CommonThirdScoresDto> request = new Request<>(commonScoresDto, reqMessage.getLinkId(), "THIRD_MATCH_SCORES", reqMessage.getLinkId(), commonScoresDto.getDataSourceCode());
        boolean spareMqFlag = getSpareMqFlag(commonScoresDto.getStandardMatchId()+"");
        if (pandaDataMqGatewayevent == 2 && spareMqFlag) {
            spareBaseProducer.send(reqMessage);
        } else {
            rocketMqTemplate.asyncSend("THIRD_MATCH_SCORES:" + commonScoresDto.getThirdMatchId(), builder.build(),new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("::{}::,THIRD_MATCH_SCORES send successful", commonScoresDto.getLinkedId());
                }
                @Override
                public void onException(Throwable throwable) {
                    log.error("::{}::TOPIC={}，send fail; ", commonScoresDto.getLinkedId(), "THIRD_MATCH_SCORES", throwable);
                }
            });
        }
        log.info("linkId::{}::开始组装赛事比分信息并下发,topic:THIRD_MATCH_SCORES,request={}", commonScoresDto.getLinkedId(), JSON.toJSONString(reqMessage));
    }

//    /**
//     * 过滤篮球6分钟比分
//     * @param commonScoresDto
//     * @return
//     */
//    public  void extraBasketballSixScores(CommonThirdScoresDto commonScoresDto) {
//        if(commonScoresDto.getSportId()!=2L){
//            return;
//        }
//        JSONObject periodFootballScores = JSONObject.parseObject(JSONObject.toJSONString(commonScoresDto.getScores()));
//        Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodFootballScores);
//        List<Long> SIX_SCORES_NUM = new ArrayList<>(Arrays.asList(1312L,1306L,1412L,1406L,1512L,1506L,1612L,1606L));
//        List<Long> removeKey = new ArrayList<>();
//        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellRepository.selectThirdMatchInfoPrimaryKey(commonScoresDto.getStandardMatchId());
//        if(standardSportMarketSell != null && standardSportMarketSell.getBusinessEvent() != null){
//            if(!standardSportMarketSell.getBusinessEvent().equals(commonScoresDto.getDataSourceCode())) {
//                for (Map.Entry<Long, BasketballScores> entry : allPeriodScores.entrySet()) {
//                    Long key = entry.getKey();
//                    if (SIX_SCORES_NUM.contains(key)) {
//                        removeKey.add(key);
//                        log.info("::{}::删除6分钟区间比分：{}", commonScoresDto.getLinkedId(), key);
//                    }
//                }
//                if (!removeKey.isEmpty()) {
//                    allPeriodScores.keySet().removeIf(removeKey::contains);
//                }
//            }
//        }
//        commonScoresDto.setScores(JsonMapUtils.transferSimpleJsonMap(JSON.toJSONString(allPeriodScores)));
//        log.info("处理篮球6分钟逻辑结束：{},移除key:{},比分：{}",commonScoresDto.getLinkedId(),removeKey,JSONObject.toJSONString(allPeriodScores));
//    }


    /**
     * 下发三方比 & 标准比分（标准比分不处理 5网，8乒，9排，10羽）
     * PD事件触发
     * */
    public void sendToMQ(ThirdMatchInfo thirdMatchInfo, MatchScoresInfo matchScoresInfo, MatchEventInfo event) {
        String linkedId = event.getLinkId();
        log.info("linkId::{}::sendToMQ in UOF start", linkedId);
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            StandardMatchInfo standardMatchInfo = standardMatchInfoRepository.selectStandardMatchPrimaryKey(thirdMatchInfo.getReferenceId());
            if (event.getSportId().equals(1L) || event.getSportId().equals(2L)) {
                String eventKey = "SCORES:" + event.getDataSourceCode() + ":" + event.getId();
                redisService.set(eventKey, JSONObject.toJSON(event).toString(), 3600);
                log.info("linkId::{}::sendToMQ 缓存足蓝下发的比分事件信息,key:{}", event.getLinkId(), eventKey);
            }
            // 1.发送到业务风控
            if (!scoresService.ifMatchSoldByThirdMatchId(thirdMatchInfo,standardMatchInfo)) {
                log.info("linkId::{}::该赛事未开售", linkedId);
                return;
            }
            //ps:事件已经标反下发，无需重复处理
//            scoresService.changeHomeAway(matchScoresInfo,thirdMatchInfo);
            //非网乒羽排足蓝 才下发
            if (!DataSourceConstant.STANDARC_SCORE_SPORTIDS.contains(matchScoresInfo.getSportId())) {
                sendToBussiness(thirdMatchInfo, matchScoresInfo, event);
            }
            //2.发送 三方比分 THIRD_MATCH_SCORES (使用方:操盘,结算2.0)
            sendToMatchManager(thirdMatchInfo, matchScoresInfo, event);
            if (matchScoresInfo.getPeriod().equals(999l)) {
                sendAoMatchScores(matchScoresInfo, linkedId, thirdMatchInfo.getReferenceId());
            }
            stopWatch.stop();
            log.info("linkId::{}::ScoresProducer-sendToMQ-耗时={}, thirdMatchId={}", linkedId, stopWatch.getTotalTimeMillis(), matchScoresInfo.getThirdMatchId());
        } catch (Exception e) {
            log.error("linkId::{}::sendToMQ 三方赛事ID:{}, error", linkedId, thirdMatchInfo.getId(), e);

        }
    }

    /**
     * 下发三方比 & 标准比分（标准比分不处理 5网，8乒，9排，10羽）
     * 报球板/结算比分触发
     * */
    public void sendToMQ(ThirdMatchInfo thirdMatchInfo, MatchScoresInfo matchScoresInfo, String linkedId) {
        sendToMQ(thirdMatchInfo, matchScoresInfo, linkedId, null);
    }

    /**
     * 与上面的三参重载完全一致，额外允许调用方（如 batchEditScores）显式标注 eventCode，
     * 写入 STANDARD_MATCH_SCORES 消息；其余既有调用方不传该参数，行为不变（eventCode 仍为 null）。
     */
    public void sendToMQ(ThirdMatchInfo thirdMatchInfo, MatchScoresInfo matchScoresInfo, String linkedId, String eventCode) {
        log.info("linkId::{}::sendToMQ in UOF start", linkedId);
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            StandardMatchInfo standardMatchInfo = standardMatchInfoRepository.selectStandardMatchPrimaryKey(thirdMatchInfo.getReferenceId());

            // 1.发送到业务风控
            if (!scoresService.ifMatchSoldByThirdMatchId(thirdMatchInfo,standardMatchInfo)) {
                log.info("linkId::{}::该赛事未开售", linkedId);
                return;
            }
//            scoresService.changeHomeAway(matchScoresInfo,thirdMatchInfo);
            //非网乒羽排足蓝 才下发
            if (!DataSourceConstant.STANDARC_SCORE_SPORTIDS.contains(matchScoresInfo.getSportId())
                    || matchScoresInfo.getSportId()==1 || matchScoresInfo.getSportId()==2L) {
                sendToBussiness(thirdMatchInfo, matchScoresInfo, linkedId, eventCode);
            }
            //2.发送 三方比分 THIRD_MATCH_SCORES (使用方:操盘,结算2.0)
            sendToMatchManager(thirdMatchInfo, matchScoresInfo, linkedId);
            if (matchScoresInfo.getPeriod().equals(999L)) {
                sendAoMatchScores(matchScoresInfo, linkedId, thirdMatchInfo.getReferenceId());
            }
            stopWatch.stop();
            log.info("linkId::{}::ScoresProducer-sendToMQ-耗时={}, thirdMatchId={}", linkedId, stopWatch.getTotalTimeMillis(), matchScoresInfo.getThirdMatchId());
        } catch (Exception e) {
            log.error("linkId::{}::sendToMQ 三方赛事ID:{}, error", linkedId, thirdMatchInfo.getId(), e);

        }
    }
    /**
     * 下发三方比 & 标准比分（标准比分不处理 5网，8乒，9排，10羽）
     * uof
     * */
    public void sendUofScoreToMQ(ThirdMatchInfo thirdMatchInfo, MatchScoresInfo matchScoresInfo, String linkedId) {
        log.info("linkId::{}::sendToMQ in UOF start", linkedId);
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            StandardMatchInfo standardMatchInfo = standardMatchInfoRepository.selectStandardMatchPrimaryKey(thirdMatchInfo.getReferenceId());

            // 1.发送到业务风控
            if (!scoresService.ifMatchSoldByThirdMatchId(thirdMatchInfo,standardMatchInfo)) {
                log.info("linkId::{}::该赛事未开售", linkedId);
                return;
            }
//            scoresService.changeHomeAway(matchScoresInfo,thirdMatchInfo);
            //非网乒羽排足蓝 才下发
            if (!DataSourceConstant.STANDARC_SCORE_SPORTIDS.contains(matchScoresInfo.getSportId())) {
                sendToBussiness(thirdMatchInfo, matchScoresInfo, linkedId);
            }
            //2.发送 三方比分 THIRD_MATCH_SCORES (使用方:操盘,结算2.0)
            sendToMatchManager(thirdMatchInfo, matchScoresInfo, linkedId);
            if (matchScoresInfo.getPeriod().equals(999l)) {
                sendAoMatchScores(matchScoresInfo, linkedId, thirdMatchInfo.getReferenceId());
                if (!DataSourceConstant.STANDARC_SCORE_SPORTIDS.contains(matchScoresInfo.getSportId())) {
                    //4117校验阶段比分是否完整
                    checkScores(matchScoresInfo.getScoresJson(),linkedId,999L,standardMatchInfo,matchScoresInfo.getDataSourceCode());
                }

            }
            stopWatch.stop();
            log.info("linkId::{}::ScoresProducer-sendToMQ-耗时={}, thirdMatchId={}", linkedId, stopWatch.getTotalTimeMillis(), matchScoresInfo.getThirdMatchId());
        } catch (Exception e) {
            log.error("linkId::{}::sendToMQ 三方赛事ID:{}, error", linkedId, thirdMatchInfo.getId(), e);

        }
    }

    public void sendToMatchManager(ThirdMatchInfo thirdMatchInfo, MatchScoresInfo matchScoresInfo, String linkedId) {
        CommonThirdScoresDto commonScoresDto = messageBuilderUtils.buildThirdScoresDto(thirdMatchInfo, matchScoresInfo);
        //发布操作
        commonScoresDto.setLinkedId(linkedId);
        sendThirdMatchScores(commonScoresDto);
    }
    public void sendToSettlement(ThirdMatchInfo thirdMatchInfo, StandardMatchScores standardMatchScores,StandardMatchInfo matchInfo,String userName) {
        log.info("::{}::发送比分到结算,组装对象",standardMatchScores.getMatchId());
        CommonThirdScoresDto commonScoresDto = messageBuilderUtils.buildStandardScoresToThirdScoresDto(thirdMatchInfo, standardMatchScores,matchInfo,userName);
        log.info("::{}::发送比分到结算,组装对象完毕",standardMatchScores.getMatchId());
        //发布操作
        commonScoresDto.setLinkedId("BFZX_"+standardMatchScores.getMatchId()+"_"+System.currentTimeMillis());
        sendThirdMatchScores(commonScoresDto);
        log.info("::{}::发送比分到结算,发送比分完毕：{}",standardMatchScores.getMatchId(),JSON.toJSONString(commonScoresDto));
    }

    private void sendToBussiness(ThirdMatchInfo thirdMatchInfo, MatchScoresInfo matchScoresInfo, String linkedId) {
        sendToBussiness(thirdMatchInfo, matchScoresInfo, linkedId, null);
    }

    private void sendToBussiness(ThirdMatchInfo thirdMatchInfo, MatchScoresInfo matchScoresInfo, String linkedId, String eventCode) {
        //1.标准赛事绑定逻辑 判断当前比分是否为标准赛事的开售事件的比分
        if (thirdMatchInfo.getReferenceId() == null || thirdMatchInfo.getReferenceId().equals(0l)) {
            return;
        }
        boolean isStandardScore = scoresService.checkStandardScore(thirdMatchInfo, matchScoresInfo);
        //2.获取标准赛事的开售的事件源
        if (!isStandardScore) {
            log.info("linkId::{}::该赛事未开售或事件源不匹配", linkedId);
            return;
        }
        //数据组装
        CommonStandardScoresDto commonScoresDto = messageBuilderUtils.buildCommonScoresDto(thirdMatchInfo, matchScoresInfo);
        commonScoresDto.setLinkedId(linkedId);
        commonScoresDto.setEventCode(eventCode);
        //发布操作
        log.info("{},下发三方比分的关联关系为： 三方赛事ID：{},标准赛事ID：{}", linkedId, thirdMatchInfo.getId(), thirdMatchInfo.getReferenceId());

        sendStandardMatchScores(commonScoresDto);

        //发送比分MQ到V02
        sendV02Scores(commonScoresDto);
    }

    /**
     * 推送比分到v02
     * @param commonScoresDto
     */
    private void sendV02Scores(CommonStandardScoresDto commonScoresDto) {
        if (1L != commonScoresDto.getSportId() && 2L != commonScoresDto.getSportId() ) {
            return;
        }
        boolean dataSourceCode = DataSourceCodeEnum.PD.code.equals(commonScoresDto.getDataSourceCode()) ||
                DataSourceCodeEnum.PD2.code.equals(commonScoresDto.getDataSourceCode());
        if(!dataSourceCode){
            log.info("linkId::{}::数据源不匹配,不推送V02:{}，{}", commonScoresDto.getLinkedId(),commonScoresDto.getStandardMatchId(),commonScoresDto.getDataSourceCode());
            return;
        }
        StandardMatchInfo standardMatchInfo =standardMatchInfoRepository.selectStandardMatchPrimaryKey(commonScoresDto.getStandardMatchId());
        if(standardMatchInfo==null){
            log.info("linkId::{}::赛事不存在:{}", commonScoresDto.getLinkedId(),commonScoresDto.getStandardMatchId());
            return;
        }
        Request<CommonStandardScoresDto> reqMessage = new Request<>();
        reqMessage.setLinkId(commonScoresDto.getLinkedId());
        reqMessage.setData(commonScoresDto);
        //比分计算的时间
        if (commonScoresDto.getScoreTime() != null && commonScoresDto.getScoreTime() != 0) {
            reqMessage.setDataSourceTime(commonScoresDto.getScoreTime());
        }
        MessageBuilder<Request<CommonStandardScoresDto>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, commonScoresDto.getLinkedId());
        rocketMqTemplate.send("MATCH_SCORES_INFO_V02:" + commonScoresDto.getLinkedId(), builder.build());
        log.info("linkId::{}::开始组装赛事标准比分信息并下发-v02,topic:MATCH_SCORES_INFO_V02,request={}", commonScoresDto.getLinkedId(), JSON.toJSONString(reqMessage));
    }

    /**
     * 发送比分:切换事件源触发
     * @param matchScoresBetterDto
     * @param linkId
     */
    public void sendToMQ(MatchScoresBetterDto matchScoresBetterDto, String linkId) {
        //开售切换只有UOF的比分才需要变更主客队

//        scoresService.changeHomeAway(matchScoresBetterDto);

        //数据组装
        CommonStandardScoresDto commonScoresDto = messageBuilderUtils.buildCommonScoresDto(matchScoresBetterDto, linkId);
        if(0L==commonScoresDto.getPeriodId()){
            log.info("{},开售切换事件源下发标准比分： 未开赛不下发比分,标准赛事ID：{}", linkId, commonScoresDto.getStandardMatchId());
            return;
        }
        commonScoresDto.setLinkedId(linkId);
        //发布操作
        log.info("{},开售切换事件源下发标准比分： 三方赛事数据源编码：{},标准赛事ID：{}", linkId, matchScoresBetterDto.getDataSourceCode(), matchScoresBetterDto.getMatchId());
        sendStandardMatchScores(commonScoresDto);
    }

    /**
     * 当局结束开始 或者盘结束开始的时候 局内小分要清零
     *
     * @param linkId
     * @param thirdMatchInfo
     * @param matchScoresInfo
     */
    public void sendScore(String linkId, ThirdMatchInfo thirdMatchInfo, MatchScoresInfo matchScoresInfo, boolean roundZero) {

        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, TennisScores> allPeriodScores = JsonMapUtils.parseTennisMap(periodFootballScores);
        for (Map.Entry<Long, TennisScores> scoreEntry : allPeriodScores.entrySet()) {
            CommonItem currentScore = scoreEntry.getValue().getCurrentScore();
            currentScore.setHome(0);
            currentScore.setAway(0);
        }
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
        CommonStandardScoresDto commonScoresDto = messageBuilderUtils.buildCommonScoresDto(thirdMatchInfo, matchScoresInfo);
        commonScoresDto.setLinkedId(thirdMatchInfo.getReferenceId() + "_PD");
//        sendStandardMatchScores(commonScoresDto);
    }


    /**
     * 下发赛果展示状态
     *
     * @param commonScoresDto
     */
    public void sendMatchShowStatus(EditScoreResultStatusRequest commonScoresDto,String linkId) {
        Request<EditScoreResultStatusRequest> reqMessage = new Request<>();
        reqMessage.setLinkId(linkId);
        reqMessage.setData(commonScoresDto);
        MessageBuilder<Request<EditScoreResultStatusRequest>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, reqMessage.getLinkId());
        rocketMqTemplate.send("SHOW_SCORE_STATUS:" + commonScoresDto.getStandardMatchId(), builder.build());
        log.info("{}，下发赛果开关:{}",linkId,reqMessage);

    }


    /**
     * 下发赛种展示状态
     *
     * @param sportResultShowStatusDTO
     */
    public void sendSportMatchShowStatus(SportResultShowStatusDTO sportResultShowStatusDTO) {
        Request<SportResultShowStatusDTO> reqMessage = new Request<>();
        reqMessage.setLinkId(sportResultShowStatusDTO.getSportId() + "");
        reqMessage.setData(sportResultShowStatusDTO);
        MessageBuilder<Request<SportResultShowStatusDTO>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, reqMessage.getLinkId());
        rocketMqTemplate.send("SPORT_SHOW_SCORE_STATUS:" + sportResultShowStatusDTO.getSportId(), builder.build());
    }

    /**
     * 判断是否需要发送消息到备用MQ
     * @param standardMatchId 标准比赛ID
     * @return true表示需要发送到备用MQ
     * 1: pandaDataMqGatewayevent !=2 || 标准赛事ID为空，无需切换
     * 2: pandaDataMqGatewayevent = 2
     *    pandaDataMqGatewayMatchId 为空表示全部切换，
     *    pandaDataMqGatewayMatchId 不为空并且包含 standardMatchId 则切换
     *    pandaDataMqGatewayMatchId 不为空并且不包含 standardMatchId 则不切换
     */
    public boolean getSpareMqFlag(String standardMatchId) {
        // 快速失败：不满足基本条件直接返回
        if (pandaDataMqGatewayevent != 2 || StringUtils.isBlank(standardMatchId)) {
            return false;
        }
        // 处理备用MQ配置
        if (StringUtils.isBlank(pandaDataMqGatewayMatchId)) {
            return true;
        }
        // 转换为set集合
        Set<String> spareMatchIds = new HashSet<>(
                Arrays.asList(pandaDataMqGatewayMatchId.split(","))
        );
        return spareMatchIds.contains(standardMatchId);
    }


    /**
     * 下发延迟的消息
     * @param nowTime
     * @param event
     */
    public void sendOutTimeEvent(Long nowTime,MatchEventInfo event) {
//        LocalTime now = LocalTime.now();
//        int minute = now.getMinute();
//        String redisKey = "DELAY_EVENT:KEY:"+nowTime;
//        Object obj = redisService.get(redisKey);
//        Integer count = 0;
//        if(obj!=null){
//            count = (Integer) obj;
//        }
//        redisService.set(redisKey,count+1);
        DelayEventMessageDto message = new DelayEventMessageDto();
        message.setLinkedId(event.getLinkId());
        message.setSportId(event.getSportId());
        message.setPeriodId(event.getMatchPeriodId());
        message.setStandardMatchId(event.getStandardMatchId());
        message.setDataSourceCode(event.getDataSourceCode());
        message.setEventTime(event.getEventTime());
        message.setCreateTime(event.getCreateTime());
        message.setScoresTime(nowTime);
        message.setDelayTime(nowTime-event.getCreateTime());
        Request<DelayEventMessageDto> reqMessage = new Request<>();
        reqMessage.setLinkId(event.getLinkId());
        reqMessage.setData(message);
        MessageBuilder<Request<DelayEventMessageDto>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, reqMessage.getLinkId());
        rocketMqTemplate.send("DELAY_EVENT_MESSAGE:" + event.getLinkId(), builder.build());

    }

    public void sendMatchResultScores(MatchResultScoreMsgVo matchResultScoreVo) {
        Request<MatchResultScoreMsgVo> reqMessage = new Request<>();
        reqMessage.setLinkId(matchResultScoreVo.getMatchId()+"");
        reqMessage.setData(matchResultScoreVo);
        MessageBuilder<Request<MatchResultScoreMsgVo>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, reqMessage.getLinkId());
        rocketMqTemplate.send("MATCH_RESULT_SCORE:" + matchResultScoreVo.getMatchId(), builder.build());
        log.info("比分中心比分修正比分下发：{}",matchResultScoreVo);
    }
}
