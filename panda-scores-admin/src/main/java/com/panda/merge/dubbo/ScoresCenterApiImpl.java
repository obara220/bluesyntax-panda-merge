package com.panda.merge.dubbo;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.advertise.dto.FootballMatchEventStatusVo;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.dubbo.MatchFootballBallAdvertiseApiImpl;
import com.panda.merge.advertise.mq.EventProducer;
import com.panda.merge.advertise.service.CommonAdvertiseService;
import com.panda.merge.advertise.utils.RedisUtils;
import com.panda.merge.api.IScoresCenterApi;
import com.panda.merge.calculation.CalculationService;
import com.panda.merge.calculation.impl.AmericanFootballCalculationServiceImpl;
import com.panda.merge.calculation.impl.BasketballCalculationServiceImpl;
import com.panda.merge.calculation.impl.FootballCalculationServiceImpl;
import com.panda.merge.calculation.impl.HandballCalculationServiceImpl;
import com.panda.merge.common.enums.*;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.*;
import com.panda.merge.dto.*;
import com.panda.merge.dto.advertise.ChangeBusinessEventScoresDto;
import com.panda.merge.dto.advertise.EventOperationDto;
import com.panda.merge.dto.advertise.NewlyMatchEventQuery;
import com.panda.merge.dto.scores.*;
import com.panda.merge.mapper.*;
import com.panda.merge.model.*;
import com.panda.merge.mq.message.CommonStandardScoresDto;
import com.panda.merge.mq.message.MatchEventInfoMessage;
import com.panda.merge.mq.producer.ScoresProducer;
import com.panda.merge.mq.spare.SpareBaseProducer;
import com.panda.merge.repository.*;
import com.panda.merge.service.IMatchScorePdLogService;
import com.panda.merge.service.IScoresService;
import com.panda.merge.service.ThirdMatchInfoService;
import com.panda.merge.util.CategoryUtils;
import com.panda.merge.utils.BaseBallScoresUtils;
import com.panda.merge.utils.JsonMapUtils;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.utils.MessageBuilderUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.advertise.common.Constant.*;
import static com.panda.merge.calculation.impl.FootballCalculationServiceImpl.getScores;
import static com.panda.merge.common.enums.Constant.MATCH_FOOTBALL_TIME_STATUS;
import static com.panda.merge.config.RedisConfig.REDIS_WEEK_TIME;
import static com.panda.merge.constant.ConstantSystem.SCORES_EVENT_OPERATE;
import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_EVENT_INFO_API;
import static com.panda.merge.constant.ConstantSystem.VAR_EVENT_OPERATE;
import static com.panda.merge.dto.Response.failed;

/**
 * 比分数据中心对外接口实现(风控+赛程+数据后台调用)
 * */
@Service
@DubboService
@Slf4j
@RestController
@RequestMapping("/pdMatch/scoreCenter")
public class ScoresCenterApiImpl implements IScoresCenterApi {
    @NacosValue(value = "${panda.data.mq.gateway.event:1}", autoRefreshed = true)
    private int pandaDataMqGatewayevent;
    @NacosValue(value = "${panda.data.mq.gateway.matchId:1,}", autoRefreshed = true)
    private String pandaDataMqGatewayMatchId;
    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    MatchScoresInfoMapper matchScoresInfoMapper;
    @Autowired
    ThirdMatchInfoMapper thirdMatchInfoMapper;
    @Autowired
    MatchTimeInfoMapper matchTimeInfoMapper;
    @Autowired
    StandardSportMarketSellMapper standardSportMarketSellMapper;
    @Autowired
    MatchEventInfoMapper matchEventInfoMapper;

    @Autowired
    FootballCalculationServiceImpl footballCalculationService;
    @Autowired
    HandballCalculationServiceImpl handballCalculationService;
    @Autowired
    BasketballCalculationServiceImpl basketballCalculationService;
    @Autowired
    AmericanFootballCalculationServiceImpl americanFootballCalculationService;

    @Autowired
    MatchScoresSearchMapper matchScoresSearchMapper;

    @Autowired
    IScoresService scoresService;

    @Autowired
    EventProducer eventProduce;
    @Autowired
    MatchScoresEventInfoMapper matchScoresEventInfoMapper;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    @Autowired
    RedisService redisService;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    private CommonAdvertiseService commonAdvertiseService;

    @Autowired
    IMatchScorePdLogService matchScorePdLogService;

    @Autowired
    StandardSportMarketSellRepository standardSportMarketSellRepository;

    @Autowired
    B02ScoresSourceMapper b02ScoresSourceMapper;

    @Autowired
    StandardMatchInfoRepository standardMatchInfoRepository;
    @Autowired
    MatchScoreInfoRepository matchScoreInfoRepository;
    @Autowired
    ThirdMatchInfoRepository thirdMatchInfoRepository;
    @Autowired
    ScoresProducer scoresProducer;
    @Autowired
    SportScoreShowStatusMapper sportScoreShowStatusMapper;
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    @Autowired
    private StandardSportTeamMapper standardSportTeamMapper;

    @Autowired
    private MatchScoresCenterLogMapper matchScoresCenterLogMapper;

    @Autowired
    private MatchFootballBallAdvertiseApiImpl matchFootballBallAdvertiseApi;
    @Autowired
    MatchTimeInfoRepository timeInfoRepository;
    @Autowired
    ScoresRedisHelp scoresRedisHelp;
    @Autowired
    SpareBaseProducer spareBaseProducer;
    @Autowired
    private PdMatchInfoRepository pdMatchInfoRepository;
    @Autowired
    private StandardMatchScoresMapper standardMatchScoresMapper;
    @Autowired
    MessageBuilderUtils messageBuilderUtils;
    /**
     * 根据赛事ID查询比分
     * @param matchIds
     * @return
     */
    @Override
    public List<JSONObject> searchListMatchScores(List<MatchScoresRequestDTO> matchIds) {

//        try {
//            if (matchIds == null || matchIds.size() > 2000) {
//                return null;
//            }
//            List<JSONObject> list = new ArrayList<>();
//
//            List<MatchScoresBetterDto> matchScoresBetterDtos = searchBetterListMatchScores(matchIds);
//            //log.info("searchListMatchScores的查询入参:{}, 返回结果:{}", JSON.toJSONString(matchIds), JSON.toJSONString(matchScoresBetterDtos));
//
//            for (MatchScoresBetterDto matchScoresBetterDto : matchScoresBetterDtos) {
//                if(matchScoresBetterDto.getPeriodNow()==null||matchScoresBetterDto.getPeriodNow().equals(0l)){
//                    matchScoresBetterDto.setT1(null);
//                    matchScoresBetterDto.setT2(null);
//                    matchScoresBetterDto.setPeriodT1(null);
//                    matchScoresBetterDto.setPeriodT2(null);
//                    matchScoresBetterDto.setScoresJson(null);
//                }
//                addScore(list, matchScoresBetterDto);//未展示右边比分版的比分
//            }
//            for (MatchScoresRequestDTO matchId : matchIds) {
//                if (matchId.isAttention()) {
//                    addAttentionScore(list, matchId);//展示右边比分版的比分
//                    break;
//                }
//            }
//            //比分合法性过滤如果阶段=0则不展示比分
//
//            //检查比分版和赛事状态如果有问题则打印日志
////            checkMatchStatus(list);
//            return list;
//        }catch (Exception e){
//            e.printStackTrace();
//            log.error(e.getMessage());
//            log.error(e.getLocalizedMessage());
//        }
        return  null;
    }

    /**
     * 校验赛事状态
     * @param list
     */
    private void checkMatchStatus(List<JSONObject> list) {
        try {
            for (JSONObject jsonObject : list) {
                MatchScoresDto matchScoresDto = JSONObject.toJavaObject(jsonObject, MatchScoresDto.class);
                if(matchScoresDto.getIsStandard()==null||matchScoresDto.getMatchStatus()==null||matchScoresDto.getPeriodNow()==null){
                    continue;
                }
                if (matchScoresDto.getIsStandard() && matchScoresDto.getMatchStatus() == 0 && matchScoresDto.getPeriodNow() > 0) {
                    //1.得到状态源
                    StandardMatchInfo  standardMatchInfo =standardMatchInfoMapper.selectByPrimaryKey(Long.parseLong(matchScoresDto.getMatchId()));
                    if(standardMatchInfo==null){
                        return;
                    }
                    /*StandardSportMarketSellExample example=new StandardSportMarketSellExample();
                    example.createCriteria().andMatchInfoIdEqualTo(standardMatchInfo.getId());
                    List<StandardSportMarketSell> standardSportMarketSell = standardSportMarketSellMapper.selectByExample(example);
                    if(list.size()==0){
                        return;
                    }*/
                    StandardSportMarketSell standardSportMarketSell = standardSportMarketSellRepository.selectThirdMatchInfoPrimaryKey(standardMatchInfo.getId());
                    if(standardSportMarketSell==null){
                        return;
                    }
                    String businessEvent= standardSportMarketSell.getBusinessEvent();
                    ThirdMatchInfoExample thirdMatchInfoExample =new ThirdMatchInfoExample();
                    thirdMatchInfoExample.createCriteria().andReferenceIdEqualTo(standardMatchInfo.getId()).andDataSourceCodeEqualTo(businessEvent);
                    List<ThirdMatchInfo> thirdMatchInfos=thirdMatchInfoMapper.selectByExample(thirdMatchInfoExample);
                    if(thirdMatchInfos.size()==0){
                        return;
                    }
                    Integer thirdStatus= thirdMatchInfos.get(0).getMatchStatus();
                    log.error("checkMatchStatus matchId:{},eventDataSourceCode:{},thirdMatchId:{},standardStatus:{},thirdStatus:{}",
                            matchScoresDto.getMatchId(),businessEvent ,matchScoresDto.getThirdMatchId(),standardMatchInfo.getMatchStatus(),thirdStatus
                    );
                }
            }
        }catch (Exception e){

            log.error("checkMatchStatus error:{}",e.getMessage());
        }
    }

    /**
     * 组装比分
     * @param list
     * @param matchScoresBetterDto
     */
    private void addScore(List<JSONObject> list, MatchScoresBetterDto matchScoresBetterDto)
    {
        MatchScoresDto matchScoresDto = this.buildMatchScoresDto(matchScoresBetterDto);
        JSONObject jsonObject = (JSONObject)JSONObject.toJSON(matchScoresDto);
        list.add(jsonObject);
    }

    /**
     * 组装赛事比分数据
     * @param matchScoresBetterDto
     * @return
     */
    private MatchScoresDto buildMatchScoresDto(MatchScoresBetterDto matchScoresBetterDto) {
        MatchScoresDto matchScoresDto =new MatchScoresDto(matchScoresBetterDto);
        if(StringUtils.isNotEmpty( matchScoresBetterDto.getScoresJson())){
            if(matchScoresBetterDto.getSportId().equals(1l)){
                matchScoresDto.setAllScore(footballCalculationService.buildMatchScore2ByMap(matchScoresBetterDto.getScoresJson()));
            }
            if(matchScoresBetterDto.getSportId().equals(11L)){
                matchScoresDto.setAllScore(handballCalculationService.buildMatchScoreByMap(matchScoresBetterDto.getScoresJson()));
            }
            if(matchScoresBetterDto.getSportId().equals(2L)){
                matchScoresDto.setAllScore(basketballCalculationService.buildMatchScoreByMap(matchScoresBetterDto.getScoresJson()));
                //UOF 篮球 时间字段颠倒
                if(matchScoresDto.getDataSourceType().equals(SourceTypeEnum.UOF.getCode())){
                    matchScoresBetterDto.setSecondsMatchStart(matchScoresBetterDto.getRemainingTime());
                }
            }
            if(matchScoresBetterDto.getSportId().equals(3L)){
                matchScoresDto.setAllScore(BaseBallScoresUtils.getBaseBallAllScores(matchScoresBetterDto.getScoresJson()));
            }
        }

        return matchScoresDto;
    }

//    /**
//     * 组装比分数据
//     * @param list
//     * @param matchId
//     */
//    private void addAttentionScore(List<JSONObject> list, MatchScoresRequestDTO matchId) {
//        if(matchId.isStandard())
//        {
//            Long thirdId = getThirdMatchId(matchId);
//            MatchScoresInfo scoresInfo = getMatchScoresByThirdMatchId(thirdId);
//            //主客队相反
//            scoresService.changeHomeAway(scoresInfo);
//            if(scoresInfo==null){
//                addEmptyMatchStatus(list,matchId);
//                return;
//            }
//            if(scoresInfo.getPeriod()==null||scoresInfo.getPeriod().equals(0l)){
//                scoresInfo.setT1(null);
//                scoresInfo.setT2(null);
//                scoresInfo.setPeriodT1(null);
//                scoresInfo.setPeriodT2(null);
//                scoresInfo.setScoresJson(null);
//            }
//            MatchScoresDto matchScoresDto = buildMatchScoresDto(scoresInfo);
//            matchScoresDto.setThirdMatchId(scoresInfo.getThirdMatchId().toString());
//            MatchTimeInfo matchTimeInfo =matchTimeInfoMapper.selectByPrimaryKey(scoresInfo.getId());
//            if(matchTimeInfo!=null){
//                matchScoresDto.setIsTimeGo(matchTimeInfo.getTimeGo());
//                matchScoresDto.setPeriodNow(matchTimeInfo.getPeriod());
//                matchScoresDto.setSecondsMatchStart(matchTimeInfo.getSecondFromStart());
//                matchScoresDto.setRemainingTime(matchTimeInfo.getRemainingTime());
//                matchScoresDto.setNowSystemTime(System.currentTimeMillis());
//                matchScoresDto.setEventTime(matchTimeInfo.getEventTime());
//            }
//            matchScoresDto.setMatchId(matchId.getMatchId().toString());
//            List<Long> ids= new ArrayList<>();
//            ids.add(matchId.getMatchId());
//            List<MatchScoresStatusDto> scoresStatusDtos= matchScoresSearchMapper.searchMatchStatusByStandardId(ids);
//            if(scoresStatusDtos.size()!=0){
//                matchScoresDto.setMatchStatus(scoresStatusDtos.get(0).getMatchStatus());
//            }
//            matchScoresDto.setIsStandard(Boolean.TRUE);
//            Map<String,Object> map =new HashMap<>();
//            List<String> dataSourceCodes= new ArrayList<>();
//            if(matchId.isAttention()){
//                ThirdMatchInfoExample thirdMatchInfoExample= new ThirdMatchInfoExample();
//                thirdMatchInfoExample.createCriteria().andReferenceIdEqualTo(matchId.getMatchId());
//                List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoMapper.selectByExample(thirdMatchInfoExample);
//                for (ThirdMatchInfo thirdMatchInfo : thirdMatchInfos) {
//                    MatchScoresInfo matchScoresInfo = getMatchScoresByThirdMatchId(thirdMatchInfo.getId());
//                    //主客队相反
//                    if(scoresInfo.getPeriod()==null||scoresInfo.getPeriod().equals(0l)){
//                        scoresInfo.setT1(null);
//                        scoresInfo.setT2(null);
//                        scoresInfo.setPeriodT1(null);
//                        scoresInfo.setPeriodT2(null);
//                        scoresInfo.setScoresJson(null);
//                    }
//                    scoresService.changeHomeAway(matchScoresInfo);
//                    if(matchScoresInfo!=null) {
//                        map.put(matchScoresInfo.getDataSourceCode(), transferJsonMap(matchScoresInfo.getSportId(),matchScoresInfo.getScoresJson()));
//                        dataSourceCodes.add(matchScoresInfo.getDataSourceCode());
//                    }
//                }
//                matchScoresDto.setAllDataSourceCode(dataSourceCodes);
//                matchScoresDto.setScoresJson(map);
//            }
//            JSONObject jsonObject = (JSONObject)JSONObject.toJSON(matchScoresDto);
//            list.add(jsonObject);
//        }
//        else
//        {
//            MatchScoresInfo scoresInfo = getMatchScoresByThirdMatchId(matchId.getMatchId());
//            if(scoresInfo==null){
//                addEmptyMatchStatus(list,matchId);
//                return;
//            }
//            if(scoresInfo.getPeriod()==null||scoresInfo.getPeriod().equals(0l)){
//                scoresInfo.setT1(null);
//                scoresInfo.setT2(null);
//                scoresInfo.setPeriodT1(null);
//                scoresInfo.setPeriodT2(null);
//                scoresInfo.setScoresJson(null);
//            }
//            MatchScoresDto matchScoresDto = buildMatchScoresDto(scoresInfo);
//            matchScoresDto.setMatchId(matchId.getMatchId().toString());
//            matchScoresDto.setThirdMatchId(scoresInfo.getThirdMatchId().toString());
//            MatchTimeInfo matchTimeInfo =matchTimeInfoMapper.selectByPrimaryKey(scoresInfo.getId());
//            if(matchTimeInfo!=null){
//                matchScoresDto.setIsTimeGo(matchTimeInfo.getTimeGo());
//                matchScoresDto.setPeriodNow(matchTimeInfo.getPeriod());
//                matchScoresDto.setSecondsMatchStart(matchTimeInfo.getSecondFromStart());
//                matchScoresDto.setRemainingTime(matchTimeInfo.getRemainingTime());
//                matchScoresDto.setNowSystemTime(System.currentTimeMillis());
//                matchScoresDto.setEventTime(matchTimeInfo.getEventTime());
//            }
//            List<Long> thirdIds= new ArrayList<>();
//            thirdIds.add(matchId.getMatchId());
//            List<MatchScoresStatusDto> scoresStatusDtos= matchScoresSearchMapper.searchMatchStatusByThirdId(thirdIds);
//            if(scoresStatusDtos.size()!=0){
//                matchScoresDto.setMatchStatus(scoresStatusDtos.get(0).getMatchStatus());
//            }
//            if(matchId.isAttention()){
//                Map<String,Object> map =new HashMap<>();
//                List<String> dataSourceCodes= new ArrayList<>();
//                map.put(scoresInfo.getDataSourceCode(), transferJsonMap(scoresInfo.getSportId(),scoresInfo.getScoresJson()));
//                dataSourceCodes.add(scoresInfo.getDataSourceCode());
//                matchScoresDto.setAllDataSourceCode(dataSourceCodes);
//                matchScoresDto.setScoresJson(map);
//            }
//            JSONObject jsonObject = (JSONObject)JSONObject.toJSON(matchScoresDto);
//            list.add(jsonObject);
//        }
//
//    }

    /**
     * 组装赛事状态
     * @param list
     * @param matchId
     */
    private void addEmptyMatchStatus(List<JSONObject> list, MatchScoresRequestDTO matchId) {
        MatchScoresDto matchScoresDto = new MatchScoresDto();
        matchScoresDto.setIsStandard(matchId.isStandard());
        matchScoresDto.setMatchId(matchId.getMatchId().toString());
        //赛事状态查询
        List<Long> ids=new ArrayList<>();
        ids.add(matchId.getMatchId());
        if(matchId.isStandard()){
            List<MatchScoresStatusDto> scoresStatusDtos= matchScoresSearchMapper.searchMatchStatusByStandardId(ids);
            if(scoresStatusDtos.size()!=0){
                matchScoresDto.setMatchStatus(scoresStatusDtos.get(0).getMatchStatus());
            }
        }else {
            List<MatchScoresStatusDto> scoresStatusDtos= matchScoresSearchMapper.searchMatchStatusByThirdId(ids);
            if(scoresStatusDtos.size()!=0){
                matchScoresDto.setMatchStatus(scoresStatusDtos.get(0).getMatchStatus());
            }
        }

        JSONObject  jsonObject = (JSONObject)JSONObject.toJSON(matchScoresDto);
        list.add(jsonObject);
    }

    /**
     * 查询赛事比分列表
     * @param matchIds
     * @return
     */
    private List<MatchScoresBetterDto> searchBetterListMatchScores(List<MatchScoresRequestDTO> matchIds) {

        List<Long> standardIdList = new ArrayList<>();
        List<Long> thirdIdList = new ArrayList<>();
        List<Long> standardThirdIdList = new ArrayList<>();
        List<String> standardScoreIds = new ArrayList<>();
        for (MatchScoresRequestDTO matchId : matchIds) {
            if(matchId.isAttention()){
                continue;
            }
            if (matchId.isStandard()) {
                standardIdList.add(matchId.getMatchId());
            } else {
                thirdIdList.add(matchId.getMatchId());
            }
        }
        List<MatchScoresStatusDto> matchScoresStatusDtos = null;
        List<MatchScoresBetterDto> scores = new ArrayList<>();
        Map<String,MatchScoresBetterDto> matchScoresBetterDtoMap =new HashMap<>();
        /**开售的标准赛事查询*/
        if(standardIdList.size()!=0){
            matchScoresStatusDtos = matchScoresSearchMapper.searchMatchStatusByStandardId(standardIdList);
            List<MatchScoresBetterDto> standardScores = matchScoresSearchMapper.searchScoresByStandardId(standardIdList);
            for (MatchScoresBetterDto standardScore : standardScores) {

                standardScoreIds.add(standardScore.getMatchId());
                matchScoresBetterDtoMap.put(standardScore.getMatchId(),standardScore);
            }
            for (MatchScoresStatusDto matchScoresStatusDto : matchScoresStatusDtos) {
                MatchScoresBetterDto matchScoresBetterDto= matchScoresBetterDtoMap.get(matchScoresStatusDto.getMatchId().trim());
                if(matchScoresBetterDto!=null){
                    matchScoresBetterDto.setMatchStatus(matchScoresStatusDto.getMatchStatus());
                }
            }
            for (Long aLong : standardIdList) {
                if(!standardScoreIds.contains(aLong.toString())){
                    standardThirdIdList.add(aLong);
                }
            }
            scores.addAll(standardScores);
        }
        /**未开售的标准赛事查询*/
        if(standardThirdIdList.size()!=0){
            List<MatchScoresBetterDto> standardScores = matchScoresSearchMapper.searchThirdScoresByStandardId(standardThirdIdList);
            for (MatchScoresBetterDto standardScore : standardScores) {
                matchScoresBetterDtoMap.put(standardScore.getMatchId(),standardScore);
            }
            scores.addAll(standardScores);
        }
        /**三方赛事查询*/
        if(thirdIdList.size()!=0){
            List<MatchScoresBetterDto> thirdScores = matchScoresSearchMapper.searchScoresByThirdId(thirdIdList);
            for (MatchScoresBetterDto thirdScore : thirdScores) {
                matchScoresBetterDtoMap.put(thirdScore.getMatchId(),thirdScore);
            }
            scores.addAll(thirdScores);
            List<MatchScoresStatusDto> thirdStatusList =matchScoresSearchMapper.searchMatchStatusByThirdId(thirdIdList);
            for (MatchScoresStatusDto matchScoresStatusDto : thirdStatusList) {
                MatchScoresBetterDto matchScoresBetterDto = matchScoresBetterDtoMap.get(matchScoresStatusDto.getMatchId().trim());
                if(matchScoresBetterDto!=null){
                    matchScoresBetterDto.setMatchStatus(matchScoresStatusDto.getMatchStatus());
                } else {
                    MatchScoresBetterDto matchScoresBetter =new MatchScoresBetterDto();
                    matchScoresBetter.setMatchId(matchScoresStatusDto.getMatchId());
                    matchScoresBetter.setMatchStatus(matchScoresStatusDto.getMatchStatus());
                    scores.add(matchScoresBetter);
                }
            }
        }
        if(matchScoresStatusDtos!=null&&matchScoresStatusDtos.size()>0){
            for (MatchScoresStatusDto matchScoresStatusDto : matchScoresStatusDtos) {
                MatchScoresBetterDto matchScoresBetterDto = matchScoresBetterDtoMap.get(matchScoresStatusDto.getMatchId().trim());
                if(matchScoresBetterDto==null){
                    matchScoresBetterDto =new MatchScoresBetterDto();
                    matchScoresBetterDto.setMatchId(matchScoresStatusDto.getMatchId());
                    matchScoresBetterDto.setMatchStatus(matchScoresStatusDto.getMatchStatus());
                    scores.add(matchScoresBetterDto);
                }
            }
        }
        for (MatchScoresBetterDto score : scores) {
            if(StringUtils.isNotEmpty(score.getMatchId())&&standardIdList.contains(Long.parseLong(score.getMatchId()))){
                //主客队相反
//                scoresService.changeHomeAway(score);
            }
        }
        return scores;
    }


    /**
     * 查询赛事时间
     * @param matchIds
     * @return
     */
    @Override
    public List<JSONObject> searchListMatchTime(List<MatchScoresRequestDTO> matchIds) {
        List<JSONObject> list= new ArrayList<>();
        Map<MatchScoresRequestDTO,Long> matchScoresRequestDTOLongMap =new HashMap<>();
        for (MatchScoresRequestDTO dto : matchIds) {
            Long thirdMatchId = getThirdMatchId(dto);
            matchScoresRequestDTOLongMap.put(dto,thirdMatchId);
            MatchTimeInfoExample example= new MatchTimeInfoExample();
            if(scoresService.isLivedataStoped(thirdMatchId)){
                example.createCriteria().andThirdMatchIdEqualTo(thirdMatchId).andDataSourceTypeEqualTo("0");
                List<MatchTimeInfo> times= matchTimeInfoMapper.selectByExample(example);
                if(times.size()!=0){
                    MatchTimeInfo time=times.get(0);
                    MatchTimeInfoDTO matchTimeInfoDTO =new MatchTimeInfoDTO();
                    matchTimeInfoDTO.setPeriod(time.getPeriod());
                    matchTimeInfoDTO.setRemainingTime(time.getRemainingTime());
                    matchTimeInfoDTO.setSecondFromStart(time.getSecondFromStart());
                    matchTimeInfoDTO.setTimeIsGo(time.getTimeGo());

                }
            }else {

            }
        }
        return null;
    }

    /**
     * 比分事件操作
     * @param isDangerDto
     * @return
     */
    @Override
    public Response scoresEventOperate(EventOperationDto isDangerDto) {
        log.info("::{}::人工下发的事件,开始处理 request={}", isDangerDto.getLinkedId(), isDangerDto);

        //1.查询当前赛事开售事件源
        Long standerdId = isDangerDto.getMatchId();
        StandardSportMarketSellExample example = new StandardSportMarketSellExample();
        example.createCriteria().andMatchInfoIdEqualTo(standerdId);
        List<StandardSportMarketSell> standardSportMarketSells = standardSportMarketSellMapper.selectByExample(example);
//        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellRepository.selectThirdMatchInfoPrimaryKey(standerdId);
        if (CollectionUtils.isEmpty(standardSportMarketSells) || standerdId == null) {
            return Response.failed("找不到开售信息!");
        }
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSells.get(0);
        String businessEvent = standardSportMarketSell.getBusinessEvent();
        ThirdMatchInfoExample thirdMatchExample  =new ThirdMatchInfoExample();
        thirdMatchExample.createCriteria().andReferenceIdEqualTo(standardSportMarketSell.getMatchInfoId())
                .andDataSourceCodeEqualTo(businessEvent);
        List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoMapper.selectByExample(thirdMatchExample);
        if (thirdMatchInfos == null ) {
            return Response.failed("找不到三方赛事信息!");
        }
        //2.校验三方赛事,赛事比分,赛事事件信息
        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreateApi(thirdMatchInfos.get(0).getId());
        if (!response.isSuccess()) {
            log.info("::{}::【EventOperateConsumer:" + SCORES_EVENT_OPERATE + "】【::" + isDangerDto.getLinkedId() + "::】开售处理后补发比分开始",isDangerDto.getLinkedId());

            return Response.failed("参数有误!");
        }
        log.info("::{}::【EventOperateConsumer:赛事,比分,赛事时间信息" + SCORES_EVENT_OPERATE + "】【::" + response + "::】",isDangerDto.getLinkedId());


        //4.封装事件信息
        MatchEventInfoMessage matchEventInfoMessage = new MatchEventInfoMessage();
        processorMathcEvent(matchEventInfoMessage, response, businessEvent, isDangerDto);

        //5.事件下发到实时服务
        sendMatchEventMessage(matchEventInfoMessage, isDangerDto.getLinkedId(),standerdId);

        return  Response.success();
    }

    /**
     * 添加var事件X
     * @param eventOperationDto
     * @return
     */
    @Override
    @PostMapping("/addVarEvent")
    public Response<com.panda.merge.dto.message.MatchEventInfoMessage> addVarEvent(@RequestBody EventOperationDto eventOperationDto) {
        // VAR可能事件
//        if ("possible_video_assistant_referee".equals(eventOperationDto.getEventCode())) {
//            String cacheKey = eventOperationDto.getExtraInfo() + eventOperationDto.getEventCode() + eventOperationDto.getThirdMatchId();
//            Object obj = redisService.get(cacheKey);
//            if (null != obj) {
//                String varType = Character.toString(obj.toString().charAt(0));
//                if ("2".equals(varType)) {
//                    return Response.failed("zs".equals(eventOperationDto.getLanguage()) ? "请刷新或点击确认或取消VAR红牌按钮" : "please refresh or click affirm or cancel VAR red card button");
//                }
//                if ("1".equals(varType)) {
//                    return Response.failed("zs".equals(eventOperationDto.getLanguage()) ? "请刷新或点击确认或取消VAR点球按钮" : "please refresh or click affirm or cancel VAR penalty button");
//                }
//                if ("0".equals(varType)) {
//                    return Response.failed("zs".equals(eventOperationDto.getLanguage()) ? "请刷新或点击确认或取消VAR进球按钮" : "please refresh or click affirm or cancel VAR goal button");
//                }
//            } else {
//                redisService.set(cacheKey, cacheKey);
//            }
//        }
//        if ("video_assistant_referee_over".equals(eventOperationDto.getEventCode()) || "canceled_video_assistant_referee".equals(eventOperationDto.getEventCode())) {
//            if ("2".equals(eventOperationDto.getExtraInfo())) {
//                String cacheKey = eventOperationDto.getExtraInfo() + "possible_video_assistant_referee" + eventOperationDto.getThirdMatchId();
//                Object obj = redisService.get(cacheKey);
//                if (null != obj) {
//                    redisService.del(cacheKey);
//                } else {
//                    return Response.failed("zs".equals(eventOperationDto.getLanguage()) ? "请刷新或点击VAR可能红牌按钮" : "please refresh or click possible VAR red card button");
//                }
//            }
//            if ("1".equals(eventOperationDto.getExtraInfo())) {
//                String cacheKey = eventOperationDto.getExtraInfo() + "possible_video_assistant_referee" + eventOperationDto.getThirdMatchId();
//                Object obj = redisService.get(cacheKey);
//                if (null != obj) {
//                    redisService.del(cacheKey);
//                } else {
//                    return Response.failed("zs".equals(eventOperationDto.getLanguage()) ? "请刷新或点击VAR可能点球按钮" : "please refresh or click possible VAR penalty button");
//                }
//            }
//            if ("0".equals(eventOperationDto.getExtraInfo())) {
//                String cacheKey = eventOperationDto.getExtraInfo() + "possible_video_assistant_referee" + eventOperationDto.getThirdMatchId();
//                Object obj = redisService.get(cacheKey);
//                if (null != obj) {
//                    redisService.del(cacheKey);
//                } else {
//                    return Response.failed("zs".equals(eventOperationDto.getLanguage()) ? "请刷新或点击VAR可能进球按钮" : "please refresh or click possible VAR goal button");
//                }
//            }
//        }
        if (!PDEventCodeEnum.containVarEvent(eventOperationDto.getEventCode())) {
            Response kickOffEvent = matchFootballBallAdvertiseApi.kickOffEventCheck(eventOperationDto.getThirdMatchId(), eventOperationDto.getLanguage());
            if (kickOffEvent != null) {
                return kickOffEvent;
            }
        }
        String eventCode = eventOperationDto.getEventCode();
        String extraInfo = eventOperationDto.getExtraInfo();
        Long thirdMatchIdForVar = eventOperationDto.getThirdMatchId();
        boolean isZsLanguage = "zs".equals(eventOperationDto.getLanguage());
        final String homeAwayVarAll = "all";
        final String varPossibleCacheKey = homeAwayVarAll + PDEventCodeEnum.POSSIBLE_VIDEO_ASSISTANT_REFEREE.getEventCode() + thirdMatchIdForVar;

        if (PDEventCodeEnum.POSSIBLE_VIDEO_ASSISTANT_REFEREE.getEventCode().equals(eventCode)) {
            if (redisService.get(varPossibleCacheKey) != null) {
                return Response.failed(isZsLanguage ? "请刷新或点击确认或取消按钮" : "please refresh or click affirm or cancel button");
            }
            Response<MatchScoreAndTimeVo> earlyMatchResp = commonAdvertiseService.checkMatchScoreAndTimeCreateApi(thirdMatchIdForVar);
            if (!earlyMatchResp.isSuccess()) {
                log.info("::{}::【addVarEvent:" + VAR_EVENT_OPERATE + "】【::" + eventOperationDto.getLinkedId() + "::】开售处理后补发比分开始", eventOperationDto.getLinkedId());
                return Response.failed("参数有误!");
            }
            if (!SportPeriodConstant.FootballPeriod.contans(earlyMatchResp.getData().getMatchTimeInfo().getPeriod())) {
                return Response.failed("赛事不在开打阶段");
            }
            Long periodIdEarly = earlyMatchResp.getData().getMatchTimeInfo().getPeriod();
            Long periodScoreEarly = earlyMatchResp.getData().getMatchScoresInfo().getPeriod();
            if (50 == periodIdEarly || 50 == periodScoreEarly) {
                return Response.failed(isZsLanguage ? "当前阶段禁止该操作，请刷新或切换赛事" : "Disallow the action at this stage,pls refresh or change match");
            }
            redisService.set(varPossibleCacheKey, varPossibleCacheKey);
        }
        if ("video_assistant_referee_over".equals(eventCode) && StringUtils.isBlank(extraInfo)) {
            if (redisService.get(varPossibleCacheKey) == null) {
                return Response.failed(isZsLanguage ? "请刷新或重新点击VAR按钮" : "please refresh or click VAR button again");
            }
            redisService.del(varPossibleCacheKey);
        }
        if ("canceled_video_assistant_referee".equals(eventCode) && StringUtils.isBlank(extraInfo)) {
            if (redisService.get(varPossibleCacheKey) == null) {
                return Response.failed(isZsLanguage ? "请刷新或重新点击VAR按钮" : "please refresh or click VAR button again");
            }
            // 清除所有可能的VAR缓存key，共4个
            Set<String> deleteKeys = new HashSet<>(Arrays.asList(
                varPossibleCacheKey,
                homeAwayVarAll + PDEventCodeEnum.POSSIBLE_VAR_RED_CARD.getEventCode() + thirdMatchIdForVar,
                homeAwayVarAll + PDEventCodeEnum.POSSIBLE_VAR_PENALTY.getEventCode() + thirdMatchIdForVar,
                homeAwayVarAll + PDEventCodeEnum.POSSIBLE_VAR_GOAL.getEventCode() + thirdMatchIdForVar));
            deleteKeys.removeIf(Objects::isNull);
            if (!deleteKeys.isEmpty()) {
                redisService.delete(deleteKeys);
            }
        }
        if ("video_assistant_referee_over".equals(eventOperationDto.getEventCode()) || "canceled_video_assistant_referee".equals(eventOperationDto.getEventCode())) {
            boolean cancelFlag = true;
            String homeAway = "all";
            if ("video_assistant_referee_over".equals(eventOperationDto.getEventCode())) {
                cancelFlag = false;
            }
            if ("2".equals(eventOperationDto.getExtraInfo())) {
//                String cacheKey = eventOperationDto.getExtraInfo() + "possible_video_assistant_referee" + eventOperationDto.getThirdMatchId();
                String cacheKey = homeAway + PDEventCodeEnum.POSSIBLE_VAR_RED_CARD.getEventCode() + eventOperationDto.getThirdMatchId();
                Object obj = redisService.get(cacheKey);
                if (null != obj) {
                    if (cancelFlag) {
                        redisService.del(cacheKey);
                    }
                } else {
                    return Response.failed("zs".equals(eventOperationDto.getLanguage()) ? "请刷新或点击VAR可能红牌按钮" : "please refresh or click possible VAR red card button");
                }
            }
            if ("1".equals(eventOperationDto.getExtraInfo())) {
//                String cacheKeyey = eventOperationDto.getExtraInfo() + "possible_video_assistant_referee" + eventOperationDto.getThirdMatchId();
                String cacheKey = homeAway + PDEventCodeEnum.POSSIBLE_VAR_PENALTY.getEventCode() + eventOperationDto.getThirdMatchId();
                Object obj = redisService.get(cacheKey);
                if (null != obj) {
                    if (cancelFlag) {
                        redisService.del(cacheKey);
                    }
                } else {
                    return Response.failed("zs".equals(eventOperationDto.getLanguage()) ? "请刷新或点击VAR可能点球按钮" : "please refresh or click possible VAR penalty button");
                }
            }
            if ("0".equals(eventOperationDto.getExtraInfo())) {
//                String cacheKey = eventOperationDto.getExtraInfo() + "possible_video_assistant_referee" + eventOperationDto.getThirdMatchId();
                String cacheKey = homeAway + PDEventCodeEnum.POSSIBLE_VAR_GOAL.getEventCode() + eventOperationDto.getThirdMatchId();
                Object obj = redisService.get(cacheKey);
                if (null != obj) {
                    if (cancelFlag) {
                        redisService.del(cacheKey);
                    }
                } else {
                    return Response.failed("zs".equals(eventOperationDto.getLanguage()) ? "请刷新或点击VAR可能进球按钮" : "please refresh or click possible VAR goal button");
                }
            }
        }
        log.info("::{}::人工下发的var事件,开始处理 request={}", eventOperationDto.getLinkedId(), eventOperationDto);
        String linkId = eventOperationDto.getLinkedId();
        try {
            //1.查询当前赛事开售事件源
            Long standerdId = eventOperationDto.getMatchId();
//            StandardSportMarketSellExample example = new StandardSportMarketSellExample();
//            example.createCriteria().andMatchManageIdEqualTo(standerdId.toString());
//            List<StandardSportMarketSell> standardSportMarketSells = standardSportMarketSellMapper.selectByExample(example);
//            if (standardSportMarketSells.size() == 0 ) {
//                return Response.failed("找不到开售信息!");
//            }
//            StandardSportMarketSell standardSportMarketSell = standardSportMarketSells.get(0);

//            String businessEvent = standardSportMarketSell.getBusinessEvent();
//
//            ThirdMatchInfoExample thirdMatchExample  =new ThirdMatchInfoExample();
//            thirdMatchExample.createCriteria().andReferenceIdEqualTo(standardSportMarketSell.getMatchInfoId())
//                    .andDataSourceCodeEqualTo(businessEvent);
//            List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoMapper.selectByExample(thirdMatchExample);
//            if (thirdMatchInfos == null ) {
//                return Response.failed("找不到三方赛事信息!");
//            }

            //2.校验三方赛事,赛事比分,赛事事件信息
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreateApi(eventOperationDto.getThirdMatchId());
            if (!response.isSuccess()) {
                log.info("::{}::【addVarEvent:" + VAR_EVENT_OPERATE + "】【::" + eventOperationDto.getLinkedId() + "::】开售处理后补发比分开始",linkId);
                return Response.failed("参数有误!");
            }
            log.info("::{}::【addVarEvent:赛事,比分,赛事时间信息" + VAR_EVENT_OPERATE + "】【::" + response + "::】",linkId);


            //4.封装事件信息
            MatchEventInfoMessage matchEventInfoMessage = new MatchEventInfoMessage();
            processorAddVarEvent(matchEventInfoMessage, response, response.getData().getThirdMatchInfo().getDataSourceCode(), eventOperationDto);

            //5.比分事件信息入库
            MatchScoresEventInfo matchScoresEventInfo=new MatchScoresEventInfo();
            Long matchTime = getMatchTime(response.getData());
            eventOperationDto.setEventTime(matchTime);
            // 比赛开始，初始化公共事件存入redis，缓存一周
//            String publicEventKey = "publicEvent:" + eventOperationDto.getThirdMatchId();
//            long l = System.currentTimeMillis();
//            PublicEvent publicEvent = new PublicEvent(0L, 0L, 0L, 0L, l, PublicEventEnum.KICK_OFF.getName());
//            redisService.set(publicEventKey, JSONObject.toJSON(publicEvent).toString(), REDIS_WEEK_TIME);
            // 公共事件时间统计
            Long period = response.getData().getMatchTimeInfo().getPeriod();
            PublicEvent publicEvent = matchFootballBallAdvertiseApi.updateEventTime(response.getData(), period, eventOperationDto.getEventCode(), null);
            // 更新事件时间
            matchFootballBallAdvertiseApi.updateEventTimeByJsonScore(response, period, publicEvent);
            MatchScoresInfo matchScoresInfo = pdMatchInfoRepository.getMatchScoresInfoByPrimaryKey(response.getData().getMatchScoresInfo().getId(), null);
            response.getData().setMatchScoresInfo(matchScoresInfo);
           processorAddVarScoresEvent( matchEventInfoMessage, matchScoresEventInfo,eventOperationDto,
                   response.getData().getStandardMatchInfo().getId(),response.getData().getThirdMatchInfo().getId());

            updateEventStatus(eventOperationDto, matchScoresEventInfo.getEventCode());

            //6.事件下发到实时服务
            sendMatchEventMessage(matchEventInfoMessage, eventOperationDto.getLinkedId(),standerdId);

           //7.Redis缓存
            String key =MATCH_ADVERTIS_EVENT_STATUS +response.getData().getThirdMatchInfo().getId();
            buildCacheMatchStatus(key,matchScoresEventInfo);
            log.info("::{}::人工下发var事件处理完毕 ", linkId);

            //8.ws推送
            redisUtils.pushFootBallScore(response.getData().getThirdMatchInfo().getId());
            redisUtils.pushFootBallEvent(response.getData().getThirdMatchInfo().getId());
            //9.打印日志,人工下发var事件处理
            matchScorePdLogService.addVarEventLog(eventOperationDto,response.getData());
        } catch (Exception e) {

            log.info("::{}::人工下发var事件异常 info: {}", linkId,  e);
            return Response.success(null,"下发var事件异常linkId:"+linkId);
        }
        return Response.success();
    }

    /**
     * 更新事件状态
     *
     * @param eventOperationDto 入参
     */
    private void updateEventStatus(EventOperationDto eventOperationDto, String VarEventCode) {
        String eventCode = eventOperationDto.getEventCode();
        String extraInfo = eventOperationDto.getExtraInfo();
        Long thirdMatchId = eventOperationDto.getThirdMatchId();
        String key = MATCH_ADVERTIS_EVENT_STATUS + thirdMatchId;
        Object cacheEventStatus = redisService.get(key);
        FootballMatchEventStatusVo footballMatchEventStatusVo = FootballMatchEventStatusVo.init();
        if (cacheEventStatus != null) {
            try {
                footballMatchEventStatusVo = JSONObject.toJavaObject(JSONObject.parseObject(cacheEventStatus.toString()), FootballMatchEventStatusVo.class);
            } catch (Exception e) {
                log.error("VAR 错误信息={}, 堆栈信息={}", e.getMessage(), e.getStackTrace());
            }
        } else {
            footballMatchEventStatusVo = FootballMatchEventStatusVo.init();
        }
        // 点击VAR进球，触发VAR进球可能事件，事件类型：video_assistant_referee 0 = 进球
        if("possible_video_assistant_referee".equals(eventCode) && "0".equals(extraInfo) && !"water_break".equals(VarEventCode)){
            footballMatchEventStatusVo.setHasVARGoal(true);
//            footballMatchEventStatusVo.setHasVAREvent(false);
//            footballMatchEventStatusVo.setHasVARPenalty(false);
//            footballMatchEventStatusVo.setHasVARRedCard(false);
        }
        // 点击VAR点球，触发VAR点球可能事件，事件类型：video_assistant_referee 1 = 点球
        if("possible_video_assistant_referee".equals(eventCode) && "1".equals(extraInfo)){
            footballMatchEventStatusVo.setHasVARPenalty(true);
//            footballMatchEventStatusVo.setHasVAREvent(false);
//            footballMatchEventStatusVo.setHasVARGoal(false);
//            footballMatchEventStatusVo.setHasVARRedCard(false);
        }
        // 点击VAR红牌，触发VAR红牌可能事件，事件类型：video_assistant_referee 2 = 红牌
        if("possible_video_assistant_referee".equals(eventCode) && "2".equals(extraInfo)){
            footballMatchEventStatusVo.setHasVARRedCard(true);
//            footballMatchEventStatusVo.setHasVAREvent(false);
//            footballMatchEventStatusVo.setHasVARGoal(false);
//            footballMatchEventStatusVo.setHasVARPenalty(false);
        }
        // 点击 VAR 触发VAR var_reason事件，同时按钮置灰
        if ("var_reason".equals(VarEventCode)) {
            footballMatchEventStatusVo.setHasVAREvent(false);
//            footballMatchEventStatusVo.setHasVARGoal(false);
//            footballMatchEventStatusVo.setHasVARPenalty(false);
//            footballMatchEventStatusVo.setHasVARRedCard(false);
        }
        // 点击确认或取消，按VAR钮均置灰
//        if ("video_assistant_referee_over".equals(eventCode) || "canceled_video_assistant_referee".equals(eventCode)) {
//            footballMatchEventStatusVo.setHasVAREvent(false);
//            footballMatchEventStatusVo.setHasVARGoal(false);
//            footballMatchEventStatusVo.setHasVARPenalty(false);
//            footballMatchEventStatusVo.setHasVARRedCard(false);
//        }
        boolean flag = "video_assistant_referee_over".equals(eventCode) || "canceled_video_assistant_referee".equals(eventCode);
        if (flag && "0".equals(extraInfo) && !"water_break".equals(VarEventCode)) {
            footballMatchEventStatusVo.setHasVARGoal(false);
            if("video_assistant_referee_over".equals(eventCode)) {
                footballMatchEventStatusVo.setHasVARConfirmGoal(true);
            } else if ("canceled_video_assistant_referee".equals(eventCode) && footballMatchEventStatusVo.isHasVARConfirmGoal()) {
                footballMatchEventStatusVo.setHasVARConfirmGoal(false);
            }
        }
        if (flag && "1".equals(extraInfo)) {
            footballMatchEventStatusVo.setHasVARPenalty(false);
            if("video_assistant_referee_over".equals(eventCode)) {
                footballMatchEventStatusVo.setHasVARConfirmPenalty(true);
            } else if ("canceled_video_assistant_referee".equals(eventCode) && footballMatchEventStatusVo.isHasVARConfirmPenalty()) {
                footballMatchEventStatusVo.setHasVARConfirmPenalty(false);
            }
        }
        if (flag && "2".equals(extraInfo)) {
            footballMatchEventStatusVo.setHasVARRedCard(false);
            if("video_assistant_referee_over".equals(eventCode)) {
                footballMatchEventStatusVo.setHasVARConfirmRedCard(true);
            } else if ("canceled_video_assistant_referee".equals(eventCode) && footballMatchEventStatusVo.isHasVARConfirmRedCard()) {
                footballMatchEventStatusVo.setHasVARConfirmRedCard(false);
            }
        }
        redisService.set(key, JSONObject.toJSONString(footballMatchEventStatusVo), 36000);
    }


    /**
     * 从缓存中取赛事事件状态
     * */
    private void buildCacheMatchStatus(String key, MatchScoresEventInfo matchScoresEventInfo) {

        Object cacheEventStatus = redisService.get(key);
        FootballMatchEventStatusVo footballMatchEventStatusVo = null;
        if (cacheEventStatus != null) {
            try {
                footballMatchEventStatusVo = JSONObject.toJavaObject(JSONObject.parseObject(cacheEventStatus.toString()), FootballMatchEventStatusVo.class);
            } catch (Exception e) {
                log.error("buildCacheMatchStatus error:{}:", e);

            }
        }else {
            footballMatchEventStatusVo= FootballMatchEventStatusVo.init();
        }
        //更新事件code
        footballMatchEventStatusVo.setCurrentEventCode(matchScoresEventInfo.getEventCode());
        redisService.set(key,JSONObject.toJSONString(footballMatchEventStatusVo),36000);
    }


    /**
     * 下发事件到实时服务 topic:THIRD_MATCH_EVENT_INFO_API
     */
    public void sendMatchEventMessage(MatchEventInfoMessage matchEventInfoDTO, String linkId,Long matchId) {
        Request<MatchEventInfoMessage> request=new Request();
        request.setData(matchEventInfoDTO);
        request.setLinkId(linkId);
        MessageBuilder<Request<MatchEventInfoMessage> > builder = MessageBuilder.withPayload(request)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
        //通知预售开售赛事完赛
        boolean spareMqFlag = getSpareMqFlag(matchId+"");
        if (pandaDataMqGatewayevent == 2 && spareMqFlag) {
            String dataSourceCode = matchEventInfoDTO.getDataSourceCode();
            request = new Request<>(matchEventInfoDTO, request.getLinkId(), THIRD_MATCH_EVENT_INFO_API, request.getLinkId(), dataSourceCode);
            spareBaseProducer.send(request);
        } else {
            rocketMqTemplate.send("THIRD_MATCH_EVENT_INFO_API:" + matchEventInfoDTO.getThirdMatchSourceId(), builder.build());
        }
        log.info("::{}::通知实时服务处理人工下发的事件 request={}", linkId, matchEventInfoDTO);
    }

    /**
     * 组装var事件下发消息
     * @param matchEventInfoMessage
     * @param matchScoresEventInfo
     * @param eventOperationDto
     * @param matchId
     * @param thirdMatchId
     */
    public void processorAddVarScoresEvent(MatchEventInfoMessage matchEventInfoMessage,
                                           MatchScoresEventInfo matchScoresEventInfo,
                                           EventOperationDto eventOperationDto,
                                          Long matchId, Long thirdMatchId ) {
        BeanUtils.copyProperties(matchEventInfoMessage,matchScoresEventInfo);
        matchScoresEventInfo.setDataSourceCode(matchEventInfoMessage.getDataSourceCode());
        matchScoresEventInfo.setCreateTime(System.currentTimeMillis());
        matchScoresEventInfo.setModifyTime(System.currentTimeMillis());
        matchScoresEventInfo.setLinkId(eventOperationDto.getLinkedId());
        matchScoresEventInfo.setEventTime(System.currentTimeMillis());
        matchScoresEventInfo.setSourceType(1);
        matchScoresEventInfo.setStandardMatchId(matchId);
        matchScoresEventInfo.setSecondsFromStart(eventOperationDto.getEventTime());
        matchScoresEventInfo.setThirdMatchId(thirdMatchId);
        matchScoresEventInfo.setSendData("Y");
        matchScoresEventInfo.setExtraInfo(matchEventInfoMessage.getExtrainfo());
        matchScoresEventInfoMapper.insert(matchScoresEventInfo);
    }


    /**
     * 组装var事件下发消息
     * @param matchEventInfoMessage
     * @param response
     * @param businessEvent
     * @param request
     */
    public void processorAddVarEvent(MatchEventInfoMessage matchEventInfoMessage, Response<MatchScoreAndTimeVo> response, String businessEvent, EventOperationDto request) {
        ThirdMatchInfo thirdMatchInfo = response.getData().getThirdMatchInfo();
        MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
        matchEventInfoMessage.setCanceled(0);//未取消
        matchEventInfoMessage.setDataSourceCode(businessEvent);
        matchEventInfoMessage.setSourceType("1");//常规事件
        matchEventInfoMessage.setEventCode("var_reason");
        matchEventInfoMessage.setEventTime(System.currentTimeMillis());//事件发生事件
        String eventCode = request.getEventCode();
        boolean varFlag = "possible_video_assistant_referee".equals(eventCode)
                || "video_assistant_referee_over".equals(eventCode)
                || "canceled_video_assistant_referee".equals(eventCode);
        matchEventInfoMessage.setHomeAway("none");
        matchEventInfoMessage.setMatchPeriodId(request.getMatchPeriodId());
        matchEventInfoMessage.setSecondsFromStart(request.getEventTime());
        matchEventInfoMessage.setT1(matchScoresInfo.getT1());
        matchEventInfoMessage.setT2(matchScoresInfo.getT2());
        //风控约定1001标识是由人工点击下发的事件
        matchEventInfoMessage.setExtrainfo("1001");
        if (varFlag) {
            matchEventInfoMessage.setEventCode(eventCode);
            matchEventInfoMessage.setExtrainfo(request.getExtraInfo());
        }
        if("water_break".equals(eventCode)){
            // 0安全 1危险
            matchEventInfoMessage.setEventCode("water_break");
        }
        matchEventInfoMessage.setCopyLinkId(request.getLinkedId());
        matchEventInfoMessage.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
        matchEventInfoMessage.setIsErrorEndEvent(0);
        matchEventInfoMessage.setSportId(thirdMatchInfo.getSportId());
        matchEventInfoMessage.setThirdEventId("PA_Event:"+ UUIdUtils.getId());
        matchEventInfoMessage.setRemark(request.getOperatorName());
    }


    /**
     * 组装下发消息
     * @param matchEventInfoMessage
     * @param response
     * @param businessEvent
     * @param request
     */
    public void processorMathcEvent(MatchEventInfoMessage matchEventInfoMessage, Response<MatchScoreAndTimeVo> response, String businessEvent, EventOperationDto request) {
        ThirdMatchInfo thirdMatchInfo = response.getData().getThirdMatchInfo();
        MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
        MatchTimeInfo matchTimeInfo = response.getData().getMatchTimeInfo();
        matchEventInfoMessage.setCanceled(0);//未取消
        matchEventInfoMessage.setDataSourceCode(businessEvent);
        matchEventInfoMessage.setSourceType("1");//常规事件
        matchEventInfoMessage.setEventCode(request.getIsDanger() == 0 ? "ball_safe" : "dangerous_attack");
        matchEventInfoMessage.setEventTime(request.getEventTime());//事件发生事件
        matchEventInfoMessage.setHomeAway("none");
        matchEventInfoMessage.setMatchPeriodId(request.getMatchPeriodId());
        Integer secondsMatchStart =response.getData().getStandardMatchInfo().getSecondsMatchStart();
//        if(secondsMatchStart!=null && secondsMatchStart!=0){
//            Long times  = Long.parseLong(secondsMatchStart.toString());
//            matchEventInfoMessage.setSecondsFromStart(times);
//        }else{
//            matchEventInfoMessage.setSecondsFromStart(matchTimeInfo.getSecondFromStart());
//        }
        if(matchTimeInfo!=null && matchTimeInfo.getSecondFromStart()!=null){
            matchEventInfoMessage.setSecondsFromStart(matchTimeInfo.getSecondFromStart());
        }else{
            Long times  = Long.parseLong(secondsMatchStart.toString());
            matchEventInfoMessage.setSecondsFromStart(times);
        }
        matchEventInfoMessage.setT1(matchScoresInfo.getT1());
        matchEventInfoMessage.setT2(matchScoresInfo.getT2());
//        matchEventInfoMessage.setExtrainfo(request.getIsDanger() == 0 ? "" : "1002");
        matchEventInfoMessage.setExtrainfo("tradeOperate");
        matchEventInfoMessage.setCopyLinkId(request.getLinkedId());
        matchEventInfoMessage.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
        matchEventInfoMessage.setIsErrorEndEvent(0);
        matchEventInfoMessage.setSportId(thirdMatchInfo.getSportId());
        matchEventInfoMessage.setThirdEventId("PA_Event:"+ UUIdUtils.getId());
        matchEventInfoMessage.setRemark(request.getOperatorName());
    }


    /**
     * 查询标准赛事
     * @param dto
     * @return
     */
    private Long getThirdMatchId(MatchScoresRequestDTO dto) {
        if(!dto.isStandard()){
            return dto.getMatchId();
        }
//        StandardMatchInfo standardMatchInfo =standardMatchInfoMapper.selectByPrimaryKey(dto.getMatchId());
//        if(standardMatchInfo==null){
//            return null;
//        }
        return  scoresService.checkStandardScore(dto.getMatchId());
//        return standardMatchInfo.getThirdMatchId();
    }

    /**
     * 构建比分数据
     * @param scoresInfo
     * @return
     */
    private MatchScoresDto buildMatchScoresDto(MatchScoresInfo scoresInfo) {
        MatchScoresDto matchScoresDto =new MatchScoresDto(scoresInfo);
        if(scoresInfo.getSportId().equals(1l)){
            matchScoresDto.setAllScore(footballCalculationService.buildMatchScore2ByMap(scoresInfo.getScoresJson()));
        }
        if(scoresInfo.getSportId().equals(11L)){
            matchScoresDto.setAllScore(handballCalculationService.buildMatchScoreByMap(scoresInfo.getScoresJson()));
        }
        if(scoresInfo.getSportId().equals(2L)){
            if(scoresInfo.getMatchLength()==3){
                matchScoresDto.setAllScore(basketballCalculationService.build3X3MatchScoreByMap(scoresInfo));
                return matchScoresDto;
            }
            matchScoresDto.setAllScore(basketballCalculationService.buildMatchScoreByMap(scoresInfo.getScoresJson()));

        }
        if(scoresInfo.getSportId().equals(3L)){
            matchScoresDto.setAllScore(BaseBallScoresUtils.getBaseBallAllScores(scoresInfo.getScoresJson()));
        }
        return matchScoresDto;
    }

//    /**
//     * 获取第三方赛事比分
//     * @param id
//     * @return
//     */
//    private MatchScoresInfo getMatchScoresByThirdMatchId(Long id) {
//        MatchScoresInfoExample oneScoreExample= new MatchScoresInfoExample();
//        oneScoreExample.createCriteria().andThirdMatchIdEqualTo(id).andDataSourceTypeEqualTo("1");
//        List<MatchScoresInfo> scoresInfos= matchScoresInfoMapper.selectByExample(oneScoreExample);
//        if(scoresInfos.size()!=0&&!checkChangeUOF(scoresInfos.get(0))){
//
//            return scoresInfos.get(0);
//        }else {
//            MatchScoresInfoExample uofScoreExample= new MatchScoresInfoExample();
//            uofScoreExample.createCriteria().andThirdMatchIdEqualTo(id).andDataSourceTypeEqualTo("0");
//            scoresInfos= matchScoresInfoMapper.selectByExample(uofScoreExample);
//
//            if(scoresInfos.size()!=0)
//                return scoresInfos.get(0);
//        }
//        scoresInfos= matchScoresInfoMapper.selectByExample(oneScoreExample);
//        if(scoresInfos.size()!=0){
//            return scoresInfos.get(0);
//        }
//        return null;
//    }

    /**
     * 判断是否UOF
     * @param matchScoresInfo
     * @return
     */
    private boolean checkChangeUOF(MatchScoresInfo matchScoresInfo) {
        Long eventTime= matchScoresInfo.getEventTime()+1000*60*2;
        if (Instant.now().toEpochMilli() > eventTime) {
            if(periodIsNotStop(matchScoresInfo)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 判断阶段  是否停止
     * @param matchScoresInfo
     * @return
     */
    private boolean periodIsNotStop(MatchScoresInfo matchScoresInfo) {

        Long thirdPeriodId= matchScoresInfo.getPeriod();
        //如果 阶段不是
        Long[] arr= SportPeriodWholeArrayEnum.getPeriodsBySportId(matchScoresInfo.getSportId());
        try {
            for (Long aLong : arr) {
                //只要找到一个 对应的值，代表就是滚球阶段里面 的某一节
                if (aLong.equals(thirdPeriodId)) {
                    return true;
                }
            }
        }catch (Exception e){
            //报错说明 赛事阶段异常 默认不在打状态
            return false;
        }
        return false;
    }

    private Object transferJsonMap(Long sportId,String scoresJson) {
        Map<String, Object> map =JsonMapUtils.transferJsonMap(scoresJson);
        if(sportId.equals(3l)){
            if(map.get("-1")!=null){
                JSONObject json= (JSONObject)map.get("-1");
                json.put("allScore",BaseBallScoresUtils.getBaseBallAllScores(scoresJson));
            }
        }
        return map;

    }


    /**
     * 查询是否有新事件推入
     * @param query
     * @return
     */
    @Override
    public Response<Boolean> queryNewlyMatchEvent(NewlyMatchEventQuery query) {
        //生成linkid
        String linkId = DataSourceCodeEnum.PA.code + UUIdUtils.getId();

        log.info("::{}::查询是否有新事件推入 query={}", linkId, query);
        MatchEventInfoExample matchEventInfoExample = new MatchEventInfoExample();
        matchEventInfoExample.createCriteria().
                andStandardMatchIdEqualTo(query.getMatchId()).
                andCreateTimeGreaterThan(query.getCreateTime());

        List<MatchEventInfo> matchEventInfos = matchEventInfoMapper.selectByExample(matchEventInfoExample);
        if (matchEventInfos != null) {
            Response.success(true,linkId+":有新的事件推入!");
            log.info("::{}::查询是否有新事件推入:有新的事件推入", linkId);
        }
        log.info("::{}::查询是否有新事件推入:没有的事件推入", linkId);
        return Response.success(false,linkId+":没有新的事件推入!");
    }

    /**
     * 切换事件源立即下发
     * @param eventSendScoresDto
     * @return
     */
    @Override
    public Response changeBusinessEventScores(ChangeBusinessEventScoresDto eventSendScoresDto) {
        ThirdMatchInfoExample thirdMatchInfoExample =new ThirdMatchInfoExample();
        thirdMatchInfoExample.createCriteria().andReferenceIdEqualTo(eventSendScoresDto.getStandardMatchId()).andDataSourceCodeEqualTo(eventSendScoresDto.getDataSourceCode());
        List<ThirdMatchInfo> list =thirdMatchInfoMapper.selectByExample(thirdMatchInfoExample);
        if(list.size()==0){
            return Response.success();
        }
        Long thirdMatchId =list.get(0).getId();

        MatchScoresInfoExample matchScoresInfoExample = new MatchScoresInfoExample();
        matchScoresInfoExample.createCriteria().andThirdMatchIdEqualTo(thirdMatchId).andDataSourceTypeEqualTo(SourceTypeEnum.LIVE_DATA.getCode()+"");
        List<MatchScoresInfo> scoresInfos = matchScoresInfoMapper.selectByExample(matchScoresInfoExample);
        if(scoresInfos.size()!=0){
            MatchScoresInfo matchScoresInfo =scoresInfos.get(0);

        }
        return null;
    }


    /**
     * 拒单事件 reject_event 人为触发的拒单事件
     * @param eventOperationDto
     * @return
     */
    @Override
    public Response<com.panda.merge.dto.message.MatchEventInfoMessage> addRejectEvent(EventOperationDto eventOperationDto) {
        return addVarManualEvent(eventOperationDto,"reject_event");
    }
    /**
     * 手动触发喝水/拒单事件
     * @param eventOperationDto
     * @param eventCode 事件编码：water_break喝水  reject_event拒单
     * @return
     */
    public Response addVarManualEvent(EventOperationDto eventOperationDto,String eventCode){
        log.info("::{}::人工下发的{}事件,开始处理 request={}", eventOperationDto.getLinkedId(), eventCode,eventOperationDto);
        String linkId = eventOperationDto.getLinkedId();
        try {
            //1.查询当前赛事开售事件源
            Long standerdId = eventOperationDto.getMatchId();
            StandardSportMarketSellExample example = new StandardSportMarketSellExample();
            example.createCriteria().andMatchInfoIdEqualTo(standerdId);
            List<StandardSportMarketSell> standardSportMarketSells = standardSportMarketSellMapper.selectByExample(example);
            if (standardSportMarketSells.size() == 0) {
                return  Response.failed("找不到开售信息!");
            }
            StandardSportMarketSell standardSportMarketSell = standardSportMarketSells.get(0);

            if (standardSportMarketSell == null || standerdId == null) {
                return Response.failed("找不到开售信息!");
            }
            String businessEvent = standardSportMarketSell.getBusinessEvent();
            //检查三方赛事
            ThirdMatchInfoExample thirdMatchExample = new ThirdMatchInfoExample();
            thirdMatchExample.createCriteria().andReferenceIdEqualTo(standardSportMarketSell.getMatchInfoId())
                    .andDataSourceCodeEqualTo(businessEvent);
            List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoMapper.selectByExample(thirdMatchExample);
            if (thirdMatchInfos == null) {
                return Response.failed("找不到三方赛事信息!");
            }
            //2.校验三方赛事,赛事比分,赛事事件信息
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreateApi(thirdMatchInfos.get(0).getId());
            if (!response.isSuccess()) {
                log.info("::{}::【addVarEvent:" + VAR_EVENT_OPERATE + "】【::" + eventOperationDto.getLinkedId() + "::】开售处理后补发比分开始", linkId);
                return Response.failed("参数有误!");
            }
            log.info("::{}::【addVarEvent:赛事,比分,赛事时间信息" + VAR_EVENT_OPERATE + "】【::" + response + "::】", linkId);

            Long thirdMatchId = thirdMatchInfos.get(0).getId();

            //4.封装事件信息
            MatchEventInfoMessage matchEventInfoMessage = new MatchEventInfoMessage();
            processorEventInfo(matchEventInfoMessage, response, businessEvent,eventCode, eventOperationDto);

            //5.比分事件信息入库
            MatchScoresEventInfo matchScoresEventInfo = new MatchScoresEventInfo();
            Long matchTime = getMatchTime(response.getData());
            eventOperationDto.setEventTime(matchTime);
            processorAddVarScoresEvent(matchEventInfoMessage, matchScoresEventInfo, eventOperationDto,
                    standardSportMarketSell.getMatchInfoId(), thirdMatchId);

            //6.事件下发到实时服务
            sendMatchEventMessage(matchEventInfoMessage, eventOperationDto.getLinkedId(),standerdId);

            //7.Redis缓存
            String key = MATCH_ADVERTIS_EVENT_STATUS + thirdMatchId;
            buildCacheMatchStatus(key, matchScoresEventInfo);
            log.info("::{}::人工下发{}事件处理完毕 ", linkId,eventCode);

            //8.ws推送
            redisUtils.pushFootBallScore(thirdMatchId);
            redisUtils.pushFootBallEvent(thirdMatchId);
            //9.打印日志,人工下发var事件处理
            matchScorePdLogService.addVarEventLog(eventOperationDto, response.getData());
        } catch (Exception e) {
            log.info("::{}::手动触发{}事件异常 info: {}", linkId, eventCode,e);
            return Response.success(null, "下发var事件异常linkId:" + linkId);
        }
        return Response.success();
    }
    /**
     * 组装事件下发消息
     * @param matchEventInfoMessage
     * @param response
     * @param businessEvent
     * @param request
     */
    public void processorEventInfo(MatchEventInfoMessage matchEventInfoMessage, Response<MatchScoreAndTimeVo> response, String businessEvent,String eventCode,  EventOperationDto request) {
        matchEventInfoMessage.setDataSourceCode(businessEvent);
        ThirdMatchInfo thirdMatchInfo = response.getData().getThirdMatchInfo();
        MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
        MatchTimeInfo matchTimeInfo = response.getData().getMatchTimeInfo();
        matchEventInfoMessage.setEventCode(eventCode);
        matchEventInfoMessage.setCanceled(0);//未取消
        matchEventInfoMessage.setSourceType("1");//常规事件
        matchEventInfoMessage.setEventTime(System.currentTimeMillis());//事件发生事件
        matchEventInfoMessage.setHomeAway("none");
        matchEventInfoMessage.setMatchPeriodId(request.getMatchPeriodId());
//        matchEventInfoMessage.setSecondsFromStart(matchTimeInfo.getSecondFromStart());
        Integer secondsMatchStart =response.getData().getStandardMatchInfo().getSecondsMatchStart();
//        if(secondsMatchStart!=null && secondsMatchStart!=0){
//            Long times  = Long.parseLong(secondsMatchStart.toString());
//            matchEventInfoMessage.setSecondsFromStart(times);
//        }else{
//            matchEventInfoMessage.setSecondsFromStart(matchTimeInfo.getSecondFromStart());
//        }
        if(matchTimeInfo!=null && matchTimeInfo.getSecondFromStart()!=null){
            matchEventInfoMessage.setSecondsFromStart(matchTimeInfo.getSecondFromStart());
        }else{
            Long times  = Long.parseLong(secondsMatchStart.toString());
            matchEventInfoMessage.setSecondsFromStart(times);
        }
        matchEventInfoMessage.setT1(matchScoresInfo.getT1());
        matchEventInfoMessage.setT2(matchScoresInfo.getT2());
        //风控约定1001标识是由人工点击下发的事件
        matchEventInfoMessage.setExtrainfo("1001");
        matchEventInfoMessage.setCopyLinkId(request.getLinkedId());
        matchEventInfoMessage.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
        matchEventInfoMessage.setIsErrorEndEvent(0);
        matchEventInfoMessage.setSportId(thirdMatchInfo.getSportId());
        matchEventInfoMessage.setThirdEventId("PA_Event:"+ UUIdUtils.getId());
        matchEventInfoMessage.setRemark(request.getOperatorName());
    }


    /**
     * 查询B02比分数据通道
     * @param query
     * @return
     */
    @Override
    public Response queryScoresSource(B02ScoresSourceDTO query) {
        if(query==null){
            return Response.failed("查询参数异常:query=null");
        }
        if(query.getSportId()==null){
            return Response.failed("查询参数异常:query:"+JSONUtil.toJsonStr(query));
        }
        if (!checkSportId(query.getSportId())) {
            return Response.failed("查询参数异常:不支持的sportId类型："+query.getSportId());
        }
        log.info("B02比分通道查询@@:{}",JSONUtil.toJsonStr(query));
        //查询球种B02当前比分数据通道
        List<B02ScoresSource> list = b02ScoresSourceMapper.pageList(query);
        if(list!=null && !list.isEmpty()){
            return Response.success(list.get(0));
        }
        return Response.success();
    }

    /**
     * 校验体种类型，非足篮的综合球种
     * @param sportId
     * @return
     */
    private static boolean checkSportId(Long sportId){
        List<Long> SPORT_TYPES = new ArrayList<>(Arrays.asList(3L,4L,5L,6L,7L,8L,9L,10L,11L,12L,13L,14L,15L,16L));
        return SPORT_TYPES.contains(sportId);
    }

    /**
     * 切换比分数据通道
     * @param data
     * @return
     */
    @Override
    public Response changeScoresSource(B02ScoresSourceDTO data) {
        if(null == data){
            return Response.failed("切换失败：参数异常。");
        }
        if(null==data.getDataSourceType()){
            return Response.failed("切换失败：数据来源类型格式错误。");
        }
        if(!data.getDataSourceType().equals(0L) && !data.getDataSourceType().equals(1L)){
            return Response.failed("切换失败：数据来源类型格式错误。");
        }
        log.info("B02比分通道切换开始@@:{}",JSONUtil.toJsonStr(data));
        data.setUpdateTime(System.currentTimeMillis());
        int count = b02ScoresSourceMapper.update(data);
        if(count>0){
            log.info("B02比分通道切换完成");
//            redisService.setLongTime("B02_SCORES_SOURCE_"+data.getSportId(),data.getDataSourceType());
            redisService.set("B02_SCORES_SOURCE_"+data.getSportId(),data.getDataSourceType(),RedisConfig.REDIS_MONTH_TIME);
            //切换成功后，保存操作记录
            B02ScoresSource entity = new B02ScoresSource();
            BeanUtils.copyProperties(data,entity);
            entity.setDataType(1);
            entity.setDataSourceCode("B02");
            entity.setCreateTime(System.currentTimeMillis());
            entity.setUpdateTime(System.currentTimeMillis());
            entity.setMatchManageId(data.getMatchManageId());
            //切换时指定赛事ID，则
            if(data.getMatchManageId()!=null){
                log.info("B02比分通道切换完成，指定赛事ID：{} ---->下发比分",data.getMatchManageId());
                sendScores(data);
            }
            b02ScoresSourceMapper.insert(entity);
            log.info("B02比分通道切换完成，保存记录完成");
            return Response.success();
        }else{
            return Response.failed("切换失败："+JSONUtil.toJsonStr(data));
        }
    }
    public void sendScores(B02ScoresSourceDTO data){
        try{
            StandardMatchInfoExample matchInfoExample = new StandardMatchInfoExample();
            matchInfoExample.createCriteria().andMatchManageIdEqualTo(data.getMatchManageId().toString()).andSportIdEqualTo(data.getSportId());
            List<StandardMatchInfo> matchInfoList =  standardMatchInfoMapper.selectByExample(matchInfoExample);
            if(matchInfoList==null || matchInfoList.isEmpty()){
                log.info("B02比分通道切换完成，标准赛事不存在：{},{}",data.getMatchManageId(),data.getSportId());
                return ;
            }
            ThirdMatchInfoExample thirdMatchInfoExample = new ThirdMatchInfoExample();
            thirdMatchInfoExample.createCriteria().andReferenceIdEqualTo(matchInfoList.get(0).getId()).andDataSourceCodeEqualTo("BC");
            List<ThirdMatchInfo> thirdMatchList = thirdMatchInfoMapper.selectByExample(thirdMatchInfoExample);
            if(thirdMatchList==null || thirdMatchList.isEmpty()){
                log.info("B02比分通道切换完成，三方赛事不存在，标准赛事ID{}，",matchInfoList.get(0).getId());
                return ;
            }
            log.info("B02比分通道切换完成，thirdMatchId={}",thirdMatchList.get(0).getId());
            Long thirdMatchId = thirdMatchList.get(0).getId();
            String dataType = data.getDataSourceType()==0?"1":"0";
            MatchScoresInfoExample uofScoreExample= new MatchScoresInfoExample();
            uofScoreExample.createCriteria().andThirdMatchIdEqualTo(thirdMatchId)
                    .andDataSourceCodeEqualTo("BC")
                    .andDataSourceTypeEqualTo(dataType);
            List<MatchScoresInfo> scoresInfos= matchScoresInfoMapper.selectByExample(uofScoreExample);
            if(scoresInfos==null ||scoresInfos.isEmpty()){
                log.info("B02比分通道切换完成，对应通道比分查询为空，不下发比分：{}===>{}",matchInfoList.get(0).getId(),data.getDataSourceType());
                return;
            }
            log.info("B02比分通道切换完成，标准比分下发：{}===>{}",matchInfoList.get(0).getId(),scoresInfos.get(0).getScoresJson());
            //停售的历史赛事不影响比分发送
//            if(!scoresService.isLivedataStoped(thirdMatchList.get(0).getId())){
//                return;
//            }
            //3.判断 livedata 的时间是否 》2分钟 则下发
            scoresProducer.sendToMQ(thirdMatchList.get(0),scoresInfos.get(0),"CHANGE_B02_SOURCE_"+matchInfoList.get(0).getId());
            log.info("B02比分通道切换完成，比分下发完成，linkId:：{}","CHANGE_B02_SOURCE_"+matchInfoList.get(0).getId());
        }catch (Exception e){
            log.error("B02比分通道切换，发送赛事比分异常：赛事ID：{},{}",data.getMatchManageId(),e.getMessage());
        }
    }

    @Override
    public Response queryMatchScores(Long standardMatchId, Long sportId) {
        CalculationService calculationService = scoresService.getCalculationService(sportId);
        StandardScoreCenterDTO dto = new StandardScoreCenterDTO();
        if (null != calculationService) {
            //计算阶段比分
            dto = calculationService.queryMatchScores(standardMatchId);
            if(dto==null){
                return Response.failed("无开售信息或无标准比分信息");
            }
        }
        return Response.success(dto);
    }
    @Override
    public Response editStandScores(StandardScoreCenter scores) {
        CalculationService calculationService = scoresService.getCalculationService(scores.getSportId());
        if (null != calculationService) {
            //计算阶段比分
            StandardMatchScores standardMatchScores = scoresRedisHelp.getCatchStandScoreByMatchId(scores.getStandardMatchId());
            if(standardMatchScores == null){
                standardMatchScores = new StandardMatchScores();
//                log.error("标准比分对象不存在");
//                return Response.failed();
            }
            StandardMatchInfo standardMatchInfo = standardMatchInfoRepository.selectStandardMatchPrimaryKey(scores.getStandardMatchId());
            if(standardMatchInfo == null){
                log.error("标准赛事不存在");
                return Response.failed();
            }
            if(scores.getScores()==null || scores.getScores().isEmpty()){
                log.error("修改标准比分:标准比分为空:{}",scores.getStandardMatchId());
                return Response.failed();
            }
//            if(standardMatchInfo.getMatchPeriodId()==null || standardMatchInfo.getMatchPeriodId()==0){
//                return Response.failed(OperateLogTypeEnum.EDIT_TIPS_MSG_16.getCode().toString());
//            }
            return calculationService.editStandScores(scores,standardMatchScores,standardMatchInfo);
        }
        return Response.success();
    }

    /**
     * 修改数据源联动开关
     * @param switchDTO
     * @return
     */
    @Override
    public Response editAccoSwitch(StandardMatchSwitchDTO switchDTO) {
        log.error("修改数据源联动开关:{}",switchDTO);
        if(switchDTO==null){
            log.error("修改数据源联动开关失败,无数据:{}");
            return Response.failed();
        }
        CalculationService calculationService = scoresService.getCalculationService(switchDTO.getSportId());
        if (null != calculationService) {
            //计算阶段比分
            StandardMatchScores standardMatchScores = scoresRedisHelp.getCatchStandScoreByMatchId(switchDTO.getMatchId());
            if (standardMatchScores == null) {
                log.info("修改数据源联动开关失败,标准比分对象不存在:{}",switchDTO);
                return Response.failed("标准比分对象不存在");
            }
            StandardSportMarketSell standardSportMarketSell = standardSportMarketSellRepository.selectThirdMatchInfoPrimaryKey(switchDTO.getMatchId());
            if (standardSportMarketSell == null) {
                log.info("修改数据源联动开关失败,无开售数据:{}",switchDTO);
                return Response.failed("无开售数据");
            }
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(switchDTO.getMatchId(), standardSportMarketSell.getBusinessEvent());
            if (thirdMatchInfo == null) {
                log.info("修改数据源联动开关失败,{} 无三方赛事:{}",standardSportMarketSell.getBusinessEvent(),switchDTO);
                return Response.failed("无三方赛事");
            }
            StandardMatchInfo standardMatchInfo = standardMatchInfoRepository.selectStandardMatchPrimaryKey(switchDTO.getMatchId());
            if (standardMatchInfo == null) {
                log.info("修改数据源联动开关失败,{} 无标准赛事",switchDTO);
                return Response.failed("无标准赛事");
            }
            MatchScoresInfo matchScoresInfo = null;
            //如果是综合球种，需要确认是否B02事件源
            if(DataSourceConstant.B02_SPORT_TYPES.contains(switchDTO.getSportId()) &&
                    DataSourceCodeEnum.BC.code.equals(standardSportMarketSell.getBusinessEvent())){
                //先查询数据通道，再取通道的数据源类型查比分
                Long dataSourceType = matchScoreInfoRepository.checkB02ScoresSource(switchDTO.getSportId());
                matchScoresInfo = matchScoreInfoRepository.selectByExample(thirdMatchInfo.getId(), dataSourceType == 1 ? 0 : 1);
            }else{
                //足蓝或者非B02数据源，优先事件比分
                matchScoresInfo = matchScoreInfoRepository.selectByExample(thirdMatchInfo.getId(), SourceTypeEnum.LIVE_DATA.getCode());
                if(matchScoresInfo==null){
                    matchScoresInfo = matchScoreInfoRepository.selectByExample(thirdMatchInfo.getId(), SourceTypeEnum.UOF.getCode());
                }
            }
//            if (matchScoresInfo == null) {
//                log.info("修改数据源联动开关失败,{} 无数据源比分",switchDTO);
//                return Response.failed("无标准赛事");
//            }
            //无比分,不修改
            if(StrUtil.isEmpty(standardMatchScores.getScoreJson())){
                return Response.failed("无标准比分,切换开关失败");
            }
            //修改数据源联动开关
            Boolean flag = calculationService.editAccoSwitch(switchDTO, standardMatchScores, matchScoresInfo,standardMatchInfo);
            if(!flag){
                return Response.failed(OperateLogTypeEnum.EDIT_TIPS_MSG_18.getCode().toString());
            }
            //切换开关保存日志
            editSwitchLog(switchDTO,standardSportMarketSell.getMatchManageId());

        }
        return Response.success();
    }

    public void autoClose(StandardMatchSwitchDTO switchDTO,int status,String matchManageId) {
        if(status!=0){
            editSwitchLog(switchDTO,matchManageId);
        }
    }
    public void editSwitchLog(StandardMatchSwitchDTO switchDTO,String matchManageId) {
        log.info("jilurizhi:{}",switchDTO);
        Integer showStatus = switchDTO.getStatus();
        MatchScoresCenterLog matchScoresCenterLog = new MatchScoresCenterLog();

        if(StringUtils.isBlank(matchManageId)){
            matchManageId = switchDTO.getMatchId()+"";
        }
        matchScoresCenterLog.setMatchManageId(matchManageId);
        matchScoresCenterLog.setOperateId(matchManageId);
        matchScoresCenterLog.setOperateName("");
        matchScoresCenterLog.setOperateParaName(switchDTO.getIndex()+"");
        matchScoresCenterLog.setOperateType(OperateLogTypeEnum.SCORES_CENTER_SWITCH_EDIT.getCode() + "");
        matchScoresCenterLog.setOperateRearText(String.valueOf(showStatus));
        showStatus++;
        String before = String.valueOf(new StringBuffer(Integer.toBinaryString(showStatus)).reverse().toString().charAt(0));
        matchScoresCenterLog.setOperateForwText(before);
        matchScoresCenterLog.setMatchManageId(matchManageId);
        matchScoresCenterLog.setOperateModule(OperateLogTypeEnum.SCORES_SETTLE_10038.getCode() + "");
        matchScoresCenterLog.setOperateUserName(switchDTO.getUserName());
        matchScoresCenterLog.setIpAddress(switchDTO.getIpAddress());
        matchScoresCenterLog.setOperateMatchId(matchManageId);
        matchScoresCenterLog.setCreateTime(System.currentTimeMillis());
        matchScoresCenterLog.setModifyTime(System.currentTimeMillis());
        matchScoresCenterLogMapper.insert(matchScoresCenterLog);
    }
    @Override
    public Response rcsQueryMatchScoresByMatchIds(Request<QueryMatchScoresParamDTO> request) {

        log.info("风控查询数据中心比分：{}",request);
        if(request==null || request.getData().getMatchIds().isEmpty()){
            return null;
        }
        //支持标准比分中心的赛种
        //风控测传的长ID
        List<Long> matchIds =  request.getData().getMatchIds();
        List<MatchScoresBetterDto> scores = new ArrayList<>();

        Map<String, MatchScoresBetterDto> matchScoresBetterDtoMap = new HashMap<>();
        Map<Long,String> scoresMap =new HashMap<>();
        List<MatchScoresStatusDto> matchScoresStatusDtos = matchScoresSearchMapper.searchMatchStatusByStandardId(matchIds);
        List<MatchScoresBetterDto> standardScores = matchScoresSearchMapper.searchScoresByStandardId(matchIds);
        if(standardScores == null || standardScores.isEmpty()){
            return Response.success();
        }
        for (MatchScoresBetterDto standardScore : standardScores) {
            matchScoresBetterDtoMap.put(standardScore.getMatchId(),standardScore);
        }
        for (MatchScoresStatusDto matchScoresStatusDto : matchScoresStatusDtos) {
            MatchScoresBetterDto matchScoresBetterDto= matchScoresBetterDtoMap.get(matchScoresStatusDto.getMatchId().trim());
            if(matchScoresBetterDto!=null){
                matchScoresBetterDto.setMatchStatus(matchScoresStatusDto.getMatchStatus());
            }
        }
        //支持标准比分中心的赛种，比分保存在standard_match_scores表，其余赛种保存至match_scores_info表
        if(DataSourceConstant.STANDARC_SCORE_SPORTIDS.contains(standardScores.get(0).getSportId())){
            List<StandardMatchScores> scoreList = standardMatchScoresMapper.queryScoresByMatchIds(matchIds);
            if(scoreList==null || scoreList.isEmpty()){
                return Response.success(scores);
            }
            for (StandardMatchScores score : scoreList) {
                scoresMap.put(score.getMatchId(),score.getScoreJson());
            }
            for (MatchScoresBetterDto standardScore : standardScores) {
                if(scoresMap.get(new Long(standardScore.getMatchId()))==null){
                    continue;
                }
                String scoreJson = scoresMap.get(new Long(standardScore.getMatchId()));
                standardScore.setScoresJson(scoreJson);
            }
        }
        log.info("风控查询数据中心比分：{}",standardScores);
        scores.addAll(standardScores);
        if(scores.isEmpty()){
            return Response.success();
        }
        List<CommonStandardScoresDto> list = new ArrayList<>();
        for (MatchScoresBetterDto s: scores) {
            CommonStandardScoresDto commonScoresDto = messageBuilderUtils.buildRcsQueryMatchScoresDto(s,request);
            list.add(commonScoresDto);
        }
        log.info("风控查询数据中心比分：{}",standardScores);
        return Response.success(list);
    }


    /**
     * 下发到结算
     * @param scoreCenter 赛事信息
     * @return
     */
    @Override
    public Response sendToSettlement(StandardScoreCenter scoreCenter) {
        log.info("::::发送比分到结算,开始 request={}", scoreCenter);
        if(scoreCenter==null || scoreCenter.getStandardMatchId()==null){
            log.info("发送比分到结算失败,参数错误:{}",scoreCenter);
            return Response.failed("参数错误");
        }
        Boolean isBreak = false;

        //存在阶段和中断时间则定义为中断结算
        if(scoreCenter.getPeriod()!=null && scoreCenter.getInterruptTime()!=null){
            isBreak = true;
            log.info("发送比分到结算,中断结算 request={},true", scoreCenter);
        }

        StandardMatchScores standardMatchScores = scoresRedisHelp.getCatchStandScoreByMatchId(scoreCenter.getStandardMatchId());
        if(standardMatchScores==null){
            log.info("发送比分到结算失败,无标准比分:{}",scoreCenter.getStandardMatchId());
            return Response.failed("发送比分到结算失败，无标准比分");
        }
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellRepository.selectThirdMatchInfoPrimaryKey(scoreCenter.getStandardMatchId());
        if (standardSportMarketSell == null) {
            log.info("发送比分到结算失败,无开售数据:{}",scoreCenter.getStandardMatchId());
            return Response.failed("无开售数据");
        }
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(scoreCenter.getStandardMatchId(), standardSportMarketSell.getBusinessEvent());
        if (thirdMatchInfo == null) {
            log.info("发送比分到结算失败,{} 无三方赛事:{}",standardSportMarketSell.getBusinessEvent(),scoreCenter.getStandardMatchId());
            return Response.failed("无三方赛事");
        }
        StandardMatchInfo matchInfo = standardMatchInfoRepository.selectStandardMatchPrimaryKey(scoreCenter.getStandardMatchId());
        if (matchInfo == null) {
            log.info("发送比分到结算失败,{} 无标准赛事",scoreCenter);
            return Response.failed("无标准赛事");
        }
        Boolean matchEnd = false;
        //赛事已结束
        if(matchInfo.getMatchPeriodId()==999L || matchInfo.getMatchOver()==1 ||
                matchInfo.getMatchStatus()==3 || matchInfo.getMatchStatus()==4){
            matchEnd = true;
        }
        //赛事未结束并且非中断结算 返回false
        if(!matchEnd && !isBreak){
            log.info("发送比分到结算失败,{} 赛事未结束",matchInfo);
            return Response.failed("赛事未结束");
        }

        //校验阶段比分是否完整
        Boolean checkSettleScores = checkScore(standardMatchScores,matchInfo);
        if(!checkSettleScores && !isBreak){
            return Response.failed("比分不完整");
        }
        log.info("::{}::发送比分到结算,结束 request={}",scoreCenter.getUserName(),scoreCenter);
        standardMatchScores.setSendSettleCount(1);
        scoresRedisHelp.saveCatchStandScore(standardMatchScores);
        standardMatchScoresMapper.update(standardMatchScores);
        //获取比赛比分串
        JSONObject periodScores = JSONObject.parseObject(standardMatchScores.getScoreJson());
        //足球并且中断结算
        log.info("发送比分到结算 {},{}",SportTypeEnum.FOOTBALL.getValue().equals(standardMatchScores.getSportId()),isBreak);
        if (SportTypeEnum.FOOTBALL.getValue().equals(standardMatchScores.getSportId()) && isBreak) {
            log.info("赛事中断结算下发：{}",standardMatchScores.getMatchId());
            Map<Long, FootballScores> footballScoresMap = JsonMapUtils.parseFootballMap(periodScores);
            // 获取中断时间（秒数）
            Long interruptTimeSeconds = scoreCenter.getInterruptTime();
            if(interruptTimeSeconds==null){
                log.info("赛事中断结算下发,结算数据错误：{}",scoreCenter);
                return Response.failed("结算数据错误");
            }
            if(scoreCenter.getPeriod()==null){
                log.info("赛事中断结算下发,结算数据阶段和时间错误：{}",scoreCenter);
                return Response.failed("结算数据阶段和时间错误");
            }
            if(scoreCenter.getPeriod()==6 && interruptTimeSeconds>45*60){
                log.info("赛事中断结算下发,结算数据阶段和时间错误：{}",scoreCenter);
                return Response.failed("结算数据阶段和时间错误");
            }
            if(scoreCenter.getPeriod()==7 && interruptTimeSeconds<45*60){
                log.info("赛事中断结算下发,结算数据阶段和时间错误：{}",scoreCenter);
                return Response.failed("结算数据阶段和时间错误");
            }
            if((scoreCenter.getPeriod()==41 || scoreCenter.getPeriod()==42)
                    && interruptTimeSeconds<90*60){
                log.info("赛事中断结算下发,结算数据阶段和时间错误：{}",scoreCenter);
                return Response.failed("结算数据阶段和时间错误");
            }
            // 获取所有可结算的时段ID（包括基础时段、5分钟时段、15分钟时段）
            List<Long> allPeriods = ScoresCenterApiImpl.getAllTimePeriods(scoreCenter);
            log.info("::{}::赛事中断结算下发,足球中断结算 - 中断时间:{}秒, 所有可结算时段数:{}, 时段列表:{}",
                    scoreCenter.getUserName(), interruptTimeSeconds, allPeriods.size(), allPeriods);
            if(allPeriods.isEmpty()){
                log.info("::{}::赛事中断结算下发,无可结算比分时段:{}",scoreCenter.getStandardMatchId(),scoreCenter);
                return Response.failed("无可结算比分时段");
            }
            // 过滤比分数据：只保留可结算时段的数据
            Map<Long, FootballScores> filteredFootballScores = new HashMap<>();
            for (Long periodId : allPeriods) {
                FootballScores footballSettleScores = footballScoresMap.get(periodId);
                if (footballSettleScores!=null) {
                    filteredFootballScores.put(periodId, footballSettleScores);
                    log.info("::{}::赛事中断结算下发,保留时段数据 - PeriodId:{}", scoreCenter.getStandardMatchId(), periodId);
                }/* else {
                    log.info("::{}::赛事中断结算下发,原始数据中缺少时段 - PeriodId:{}", scoreCenter.getUserName(), periodId);
                }*/
            }
            log.info("::{}::赛事中断结算下发,足球中断数据过滤完成 , 下发结算数据的时段:{}",
                    scoreCenter.getStandardMatchId(), allPeriods);
            // 更新标准比分JSON为过滤后的数据
            String filteredScoreJson = JSONObject.toJSONString(filteredFootballScores);
            standardMatchScores.setScoreJson(filteredScoreJson);
            log.info("::{}::赛事中断结算下发,足球中断数据过滤完成:{}",
                    scoreCenter.getStandardMatchId(),filteredScoreJson);
        }
        scoresProducer.sendToSettlement(thirdMatchInfo,standardMatchScores,matchInfo,scoreCenter.getUserName());

        log.info("::{}::发送比分到结算,更新结算次数结束 request={}",scoreCenter.getStandardMatchId(),scoreCenter);
        saveSendSettleLog(matchInfo.getMatchManageId(),scoreCenter,isBreak,standardMatchScores);
        return Response.success();
    }

    /**
     * 校验阶段比分是否完整
     * @param standardMatchScores
     * @param matchInfo
     * @return
     */
    private Boolean checkScore(StandardMatchScores standardMatchScores, StandardMatchInfo matchInfo) {
        Integer matchLength = matchInfo.getMatchLength();
        if(matchLength==null){
            matchLength = 0;
        }
        //足球校验是否存在上下半场比分
        if(SportTypeEnum.FOOTBALL.getValue().equals(matchInfo.getSportId())){
            JSONObject periodScores = JSONObject.parseObject(standardMatchScores.getScoreJson());
            Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodScores);
            FootballScores hfScores = allPeriodScores.get(6L);
            FootballScores ftScores = allPeriodScores.get(7L);
            if(hfScores==null || ftScores==null){
                log.info("{} 下发结算校验比分完整性：阶段比分不全",matchInfo.getMatchManageId());
                return false;
            }
            return true;
        }else if(SportTypeEnum.BASKETBALL.getValue().equals(matchInfo.getSportId())){
            //篮球根据赛制校验节比分和区间比分
            JSONObject periodScores = JSONObject.parseObject(standardMatchScores.getScoreJson());
            Map<Long, BasketballScores> allPeriodScores = JsonMapUtils.parseBasketballMap(periodScores);
            List<Long> periodList = Arrays.asList(13L, 14L, 15L, 16L);
            if(matchLength==17){
                periodList = Arrays.asList(1L, 2L);
            }else if (matchLength==73){
                periodList = Arrays.asList(21L);
            }
            for(Long periodId : periodList){
                BasketballScores scores = allPeriodScores.get(periodId);
                if(scores==null || scores.getMatchScore()==null){
                    log.info("{} 下发结算校验比分完整性：阶段比分不全",matchInfo.getMatchManageId());
                    return false;
                }
            }
            if(matchLength==7){
                Long[] minPeriodList = new Long[]{1312L,1306L,1412L,1406L,1512L,1506L,1612L,1606L};
                for(Long periodId : minPeriodList){
                    BasketballScores scores = allPeriodScores.get(periodId);
                    if(scores==null || scores.getMatchScore()==null){
                        log.info("{} 下发结算校验比分完整性：15分钟比分不全,{}",matchInfo.getMatchManageId(),periodId);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 保存下发结算的日志
     * @param matchManageId
     * @param center
     * @param isBleak
     * @param standardMatchScores
     */
    private void saveSendSettleLog(String matchManageId,StandardScoreCenter center,Boolean isBleak,StandardMatchScores standardMatchScores) {
        MatchScoresCenterLog matchScoresCenterLog = new MatchScoresCenterLog();
        matchScoresCenterLog.setMatchManageId(matchManageId);
        matchScoresCenterLog.setOperateId(matchManageId);
        matchScoresCenterLog.setOperateName("-");
        matchScoresCenterLog.setOperateModule(OperateLogTypeEnum.SCORES_SETTLE_10038.getCode() + "");
        //是否中断结算
        if(isBleak){
            matchScoresCenterLog.setOperateType(OperateLogTypeEnum.SCORES_CENTER_SETTLE_BREAK.getCode() + "");
//            matchScoresCenterLog.setOperateParaName(OperateLogTypeEnum.SCORES_CENTER_SETTLE_BREAK.getCode()+"");
            matchScoresCenterLog.setOperateParaName(getOpParaname(standardMatchScores));
            matchScoresCenterLog.setOperateRearText(getBreakSettleScores(standardMatchScores));
            matchScoresCenterLog.setOperateForwText("-");
        }else{
            matchScoresCenterLog.setOperateType(OperateLogTypeEnum.SCORES_CENTER_SETTLE.getCode() + "");
            matchScoresCenterLog.setOperateParaName(OperateLogTypeEnum.SCORES_CENTER_SETTLE_CONV.getCode()+"");
            matchScoresCenterLog.setOperateRearText("-");
            matchScoresCenterLog.setOperateForwText("-");
        }
        matchScoresCenterLog.setOperateUserName(center.getUserName());
        matchScoresCenterLog.setIpAddress(center.getIpAddress());
        matchScoresCenterLog.setOperateMatchId(matchManageId);
        matchScoresCenterLog.setCreateTime(System.currentTimeMillis());
        matchScoresCenterLog.setModifyTime(System.currentTimeMillis());
        matchScoresCenterLogMapper.insert(matchScoresCenterLog);
    }

    private String getBreakSettleScores(StandardMatchScores standardMatchScores) {
        StringBuffer sb = new StringBuffer();
        List<Long> list = new ArrayList<>();
        String scores = standardMatchScores.getScoreJson();
        if(StrUtil.isEmpty(scores)) {
            return "";
        }
        JSONObject periodFootballScores = JSONObject.parseObject(scores);
        Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
        for(Map.Entry<Long, FootballScores> entry : allPeriodScores.entrySet()){
            list.add(entry.getKey());
        }
        if(!list.isEmpty()){
            list = list.stream().sorted(Long::compareTo).collect(Collectors.toList());
        }
        for(Long period : list){
            FootballScores footballScores = allPeriodScores.get(period);
            if(footballScores!=null){
                sb.append(footballScores.getYellowCard().doCountScoreStr()).append("|");
                sb.append(footballScores.getRedCard().doCountScoreStr()).append("|");
                sb.append(footballScores.getCorner().doCountScoreStr()).append("|");
                sb.append(footballScores.getGoal().doCountScoreStr()).append("|");
            }
        }
        log.info("保存结算日志比分{}：{}",standardMatchScores.getMatchManageId(),sb.toString());
        return sb.toString();
    }

    /**
     * 赛事中断下发结算，拼接日志消息
     * @param standardMatchScores
     * @return
     */
    private String getOpParaname(StandardMatchScores standardMatchScores) {
        StringBuffer sb = new StringBuffer();
        List<Long> list = new ArrayList<>();
        String scores = standardMatchScores.getScoreJson();
        Map<Long, FootballScores> allPeriodScores = new HashMap<>();
        if(StrUtil.isNotEmpty(scores)) {
            JSONObject periodFootballScores = JSONObject.parseObject(scores);
            allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
        }
        for(Map.Entry<Long, FootballScores> entry : allPeriodScores.entrySet()){
            list.add(entry.getKey());
        }
        if(!list.isEmpty()){
            list = list.stream().sorted(Long::compareTo).collect(Collectors.toList());
        }
        for(Long period : list){
            if(period==6L){
                sb.append(FootballBallPeroidEnum.HF_YELLOW.getCode()).append("|");
                sb.append(FootballBallPeroidEnum.HF_RED.getCode()).append("|");
                sb.append(FootballBallPeroidEnum.HF_CORNER.getCode()).append("|");
                sb.append(FootballBallPeroidEnum.HF_GOAL.getCode()).append("|");
            }else if(period==7L){
                sb.append(FootballBallPeroidEnum.FT_YELLOW.getCode()).append("|");
                sb.append(FootballBallPeroidEnum.FT_RED.getCode()).append("|");
                sb.append(FootballBallPeroidEnum.FT_CORNER.getCode()).append("|");
                sb.append(FootballBallPeroidEnum.FT_GOAL.getCode()).append("|");
            }else if(period==41L || period==42L){
                if(StrUtil.isNotEmpty(sb) && sb.toString().contains("16")){
                    //避免重复添加
                    continue;
                }
                sb.append(FootballBallPeroidEnum.OT_YELLOW.getCode()).append("|");
                sb.append(FootballBallPeroidEnum.OT_RED.getCode()).append("|");
                sb.append(FootballBallPeroidEnum.OT_CORNER.getCode()).append("|");
                sb.append(FootballBallPeroidEnum.OT_GOAL.getCode()).append("|");
            }else if(period==60899L){
                sb.append(FootballBallPeroidEnum.MIN_0_15_YELLOW_CARD.getCode()).append("|");
                sb.append(FootballBallPeroidEnum.MIN_0_15_RED_CARD.getCode()).append("|");
                sb.append(FootballBallPeroidEnum.MIN_0_15_CORNER.getCode()).append("|");
                sb.append(FootballBallPeroidEnum.MIN_0_15_GOAL.getCode()).append("|");
            }else if(period==61799L){
                sb.append(FootballBallPeroidEnum.MIN_15_30_YELLOW_CARD.getCode()).append("|");
                sb.append(FootballBallPeroidEnum.MIN_15_30_RED_CARD.getCode()).append("|");
                sb.append(FootballBallPeroidEnum.MIN_15_30_CORNER.getCode()).append("|");
                sb.append(FootballBallPeroidEnum.MIN_15_30_GOAL.getCode()).append("|");
            }else if(period==62699L){
                sb.append(FootballBallPeroidEnum.MIN_30_45_YELLOW_CARD.getCode()).append("|");
                sb.append(FootballBallPeroidEnum.MIN_30_45_RED_CARD.getCode()).append("|");
                sb.append(FootballBallPeroidEnum.MIN_30_45_CORNER.getCode()).append("|");
                sb.append(FootballBallPeroidEnum.MIN_30_45_GOAL.getCode()).append("|");
            }else if(period==73599L){
                sb.append(FootballBallPeroidEnum.MIN_45_60_YELLOW_CARD.getCode()).append("|");
                sb.append(FootballBallPeroidEnum.MIN_45_60_RED_CARD.getCode()).append("|");
                sb.append(FootballBallPeroidEnum.MIN_45_60_CORNER.getCode()).append("|");
                sb.append(FootballBallPeroidEnum.MIN_45_60_GOAL.getCode()).append("|");
            }else if(period==74499L){
                sb.append(FootballBallPeroidEnum.MIN_60_75_YELLOW_CARD.getCode()).append("|");
                sb.append(FootballBallPeroidEnum.MIN_60_75_RED_CARD.getCode()).append("|");
                sb.append(FootballBallPeroidEnum.MIN_60_75_CORNER.getCode()).append("|");
                sb.append(FootballBallPeroidEnum.MIN_60_75_GOAL.getCode()).append("|");
            }else if(period==75399L){
                sb.append(FootballBallPeroidEnum.MIN_75_90_YELLOW_CARD.getCode()).append("|");
                sb.append(FootballBallPeroidEnum.MIN_75_90_RED_CARD.getCode()).append("|");
                sb.append(FootballBallPeroidEnum.MIN_75_90_CORNER.getCode()).append("|");
                sb.append(FootballBallPeroidEnum.MIN_75_90_GOAL.getCode()).append("|");
            }
        }
        log.info("保存结算日志参数{}：{}",standardMatchScores.getMatchManageId(),sb.toString());
        return sb.toString();
    }

    @Override
    public Response queryScoreCenterPage() {
        List<SportScoreShowStatus> list = sportScoreShowStatusMapper.query();
        if(list.isEmpty()){
            return failed("查询失败：无数据");
        }
        List<SportScoreShowStatusDTO> listDto = new ArrayList<>();
        for (SportScoreShowStatus obj : list) {
            SportScoreShowStatusDTO dto = new SportScoreShowStatusDTO();
            dto.setId(obj.getId());
            dto.setSportId(obj.getSportId());
            dto.setCreateTime(obj.getCreateTime());
            dto.setUpdateTime(obj.getUpdateTime());
            //前端需要true 和 false
            dto.setShowStatus(obj.getShowStatus() == 1);
            dto.setDefaultShowStatus(obj.getDefaultShowStatus() == 1);
            listDto.add(dto);
        }
        return Response.success(listDto);
    }

    @Override
    public Response editScoreCenterSettle(SportScoreShowStatus sportScoreShowStatus) {
        if(sportScoreShowStatus==null){
            log.info("修改赛种赛果状态参数不能为空");
            return failed("修改赛种赛果状态参数不能为空");
        }
        sportScoreShowStatus.setUpdateTime(System.currentTimeMillis());
        int i = sportScoreShowStatusMapper.update(sportScoreShowStatus);

        SportResultShowStatusDTO request = new SportResultShowStatusDTO();
        request.setSportId(sportScoreShowStatus.getSportId());
        request.setCreateTime(System.currentTimeMillis());
        if(sportScoreShowStatus.getShowStatus()!=null){
            request.setType(0);
            request.setResultStatus(sportScoreShowStatus.getShowStatus());
        }else if(sportScoreShowStatus.getDefaultShowStatus()!=null){
            request.setType(1);
            request.setDefaultStatus(sportScoreShowStatus.getDefaultShowStatus());
        }
        scoresProducer.sendSportMatchShowStatus(request);
        // 打印日志
        String operatePage = OperateLogTypeEnum.SCORES_SETTLE_100381.getCode() + "";
        editScoreCenterSettleLog(sportScoreShowStatus, operatePage);
        return Response.success();
    }

    @Override
    public Response setSportResuleShowStatus(ScoresCenterDTO scoresCenter) {
        if (    null == scoresCenter.getStartTimeFrom() ||
                null == scoresCenter.getSportIds() ||
                null == scoresCenter.getShowStatus()) {
            return failed("参数不能为空");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String cc = scoresCenter.getStartTimeFrom();
        try {
            Date date = sdf.parse(cc);
            Calendar cald =  Calendar.getInstance();
            cald.setTime(date);
            cald.add(Calendar.HOUR_OF_DAY, +12);
            scoresCenter.setStartTime(cald.getTime().getTime());
            cald.add(Calendar.HOUR_OF_DAY, +24);
            cald.add(Calendar.SECOND, -1);
            scoresCenter.setEndTime(cald.getTime().getTime());
        } catch (Exception e) {
            log.error("参数异常：{}",e.getMessage());
        }
        log.info("比分中心设置全部显示隐藏参数：{}",JSONUtil.toJsonStr(scoresCenter));
        try{
            Integer status = scoresCenter.getShowStatus() == 1?0:1;
            StandardSportMarketSellExample example = new StandardSportMarketSellExample();
            example.createCriteria().andBeginTimeBetween(scoresCenter.getStartTime(), scoresCenter.getEndTime())
                    .andSportIdIn(scoresCenter.getSportIds()).andShowResultStatusEqualTo(status);
            List<StandardSportMarketSell> matchList = standardSportMarketSellMapper.selectByExample(example);

            if(matchList.isEmpty()){
                return Response.failed("查询比赛为空");
            }

            List<Long> matchIds = matchList.stream().map(StandardSportMarketSell::getMatchInfoId).collect(Collectors.toList());
            standardSportMarketSellMapper.updateShowResultStatusAll(matchIds,System.currentTimeMillis(),scoresCenter.getShowStatus());
            //发送MQ通知业务侧全部显示/隐藏
            for (StandardSportMarketSell match : matchList) {
                EditScoreResultStatusRequest request = new EditScoreResultStatusRequest();
                request.setStatus(scoresCenter.getShowStatus());
                request.setStandardMatchId(match.getMatchInfoId());
                request.setSportId(match.getSportId());
                scoresProducer.sendMatchShowStatus(request,match.getMatchInfoId()+"");
                // 打印日志
                String operatePage = OperateLogTypeEnum.SCORES_SETTLE_100381.getCode() + "";
                sportResultShowStatusLog(match, scoresCenter, operatePage);
            }
        }catch(Exception e) {
            log.error("修改全部显示隐藏失败：{}",e.getMessage());
            return Response.failed("修改失败");
        }
        return Response.success();
    }

    /**
     * 打印日志
     *
     * @param scoresCenter 标准赛事信息
     */
    private void editScoreCenterSettleLog(SportScoreShowStatus scoresCenter, String operatePage) {
        MatchScoresCenterLog matchScoresCenterLog = new MatchScoresCenterLog();
        Integer showStatus = scoresCenter.getShowStatus();
        if (scoresCenter.getShowStatus()!=null) {
            matchScoresCenterLog.setOperateRearText(String.valueOf(showStatus));
            showStatus++;
            String after = String.valueOf(new StringBuffer(Integer.toBinaryString(showStatus)).reverse().toString().charAt(0));
            matchScoresCenterLog.setOperateForwText(after);
            matchScoresCenterLog.setOperateParaName(OperateLogTypeEnum.SCORES_CENTER_MATCH_STATUS.getCode() + "");
            matchScoresCenterLog.setOperateType(OperateLogTypeEnum.SCORES_CENTER_MATCH_SETTING.getCode() + "");
        }
        Integer defaultShowStatus = scoresCenter.getDefaultShowStatus();
        if (ObjectUtil.isNotEmpty(defaultShowStatus)) {
            matchScoresCenterLog.setOperateRearText(String.valueOf(defaultShowStatus));
            defaultShowStatus++;
            String before = String.valueOf(new StringBuffer(Integer.toBinaryString(defaultShowStatus)).reverse().toString().charAt(0));
            matchScoresCenterLog.setOperateForwText(before);
            matchScoresCenterLog.setOperateParaName(OperateLogTypeEnum.SCORES_CENTER_DEFAULT.getCode() + "");
            matchScoresCenterLog.setOperateType(OperateLogTypeEnum.SCORES_CENTER_DEFAULT_SETTING.getCode() + "");
        }
        matchScoresCenterLog.setOperateId("-");
        matchScoresCenterLog.setOperateName(getNameBySportId(Long.valueOf(scoresCenter.getSportId())));
        matchScoresCenterLog.setOperateModule(operatePage);
        matchScoresCenterLog.setOperateUserName(scoresCenter.getUserName());
        matchScoresCenterLog.setIpAddress(scoresCenter.getIpAddress());
        long time = TimeUtils.millsSecondsEast8ZoneGmt();
        matchScoresCenterLog.setCreateTime(time);
        matchScoresCenterLog.setModifyTime(time);
        matchScoresCenterLogMapper.insert(matchScoresCenterLog);
    }

    /**
     * 打印日志
     *
     * @param match 标准赛事信息
     */
    private void sportResultShowStatusLog(StandardSportMarketSell match, ScoresCenterDTO scoresCenter, String operatePage) {
        MatchScoresCenterLog matchScoresCenterLog = new MatchScoresCenterLog();
//        StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(match.getMatchInfoId());
        matchScoresCenterLog.setOperateModule(operatePage);
        String matchManageId = match.getMatchManageId();
        matchScoresCenterLog.setMatchManageId(matchManageId);
        matchScoresCenterLog.setOperateId(matchManageId);
        matchScoresCenterLog.setOperateName(getNameBySportId(match.getSportId()));
//        matchScoresCenterLog.setOperateMatchName(getMatchSportTeamNameCode(standardMatchInfo.getId()));
        Integer showStatus = scoresCenter.getShowStatus();
        matchScoresCenterLog.setOperateRearText(String.valueOf(showStatus));
        showStatus++;
        String before = String.valueOf(new StringBuffer(Integer.toBinaryString(showStatus)).reverse().toString().charAt(0));
        matchScoresCenterLog.setOperateForwText(before);
        matchScoresCenterLog.setOperateParaName(OperateLogTypeEnum.SCORES_CENTER_MATCH_STATUS.getCode() + "");
        matchScoresCenterLog.setOperateType(OperateLogTypeEnum.SCORES_CENTER_MATCH_SETTING.getCode() + "");
        matchScoresCenterLog.setOperateUserName(scoresCenter.getUserName());
        matchScoresCenterLog.setIpAddress(scoresCenter.getIpAddress());
        long time = TimeUtils.millsSecondsEast8ZoneGmt();
        matchScoresCenterLog.setCreateTime(time);
        matchScoresCenterLog.setModifyTime(time);
        matchScoresCenterLogMapper.insert(matchScoresCenterLog);
    }

    /**
     * 获取阶段namecode
     *
     * @param sportId
     * @return
     */
    private static String getNameBySportId(Long sportId) {
        String sportIdStr = null;
        if(sportId == null){
            return "-";
        }
        sportIdStr = sportId.toString();
        if(sportId == 10L){
            sportIdStr = PDOperateLogEnum.BADMINTON.getCode().toString();
        }
        String codeName = PDOperateLogEnum.getCnNameByCode(sportIdStr) + CategoryUtils.SPLIT_AND + PDOperateLogEnum.getEnNameByCode(sportIdStr);

        if (codeName.contains("null")) {
            codeName = codeName.replaceAll("null", CategoryUtils.SPLIT_LINE);
        }
        return codeName;
    }

    /**
     * 比分事件操作-新增补时事件
     * @param matchDto
     * @return
     */
    @Override
    public Response injuryTimeEventOperate(EventOperationDto matchDto) {
        log.info("::{}::人工下发的injuryTimeEventOperate事件,开始处理 request={}", matchDto.getLinkedId(), matchDto);
        try {
        //1.查询当前赛事开售事件源
        Long standerdId = matchDto.getMatchId();
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellRepository.selectThirdMatchInfoPrimaryKey(standerdId);
        if (standardSportMarketSell == null) {
            return Response.failed("找不到开售信息!");
        }
        String businessEvent = standardSportMarketSell.getBusinessEvent();
        ThirdMatchInfoExample thirdMatchExample  =new ThirdMatchInfoExample();
        thirdMatchExample.createCriteria().andReferenceIdEqualTo(standardSportMarketSell.getMatchInfoId())
                .andDataSourceCodeEqualTo(businessEvent);
        List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoMapper.selectByExample(thirdMatchExample);
        if (thirdMatchInfos == null ) {
            return Response.failed("找不到三方赛事信息!");
        }
        //2.校验三方赛事,赛事比分,赛事事件信息
        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreateApi(thirdMatchInfos.get(0).getId());
        if (!response.isSuccess()) {
            return Response.failed("参数有误!");
        }
        log.info("::{}::【injuryTimeEventOperate:赛事,比分,赛事时间信息】【::" + response + "::】",matchDto.getLinkedId());


        Thread.sleep(1000);

        //4.封装事件信息
        MatchEventInfoMessage matchEventInfoMessage = processorInjuryTimeEvent(response, businessEvent, matchDto);
        //5.事件下发到实时服务
        sendMatchEventMessage(matchEventInfoMessage, matchDto.getLinkedId(),standerdId);
        } catch (InterruptedException e) {
            log.info("::{}::【injuryTimeEventOperate:异常】",matchDto.getLinkedId());
        }
        return  Response.success();
    }

    /**
     * 组装下发消息
     * @param response
     * @param businessEvent
     * @param request
     */
    public MatchEventInfoMessage processorInjuryTimeEvent(Response<MatchScoreAndTimeVo> response, String businessEvent, EventOperationDto request) {
        MatchEventInfoMessage matchEventInfoMessage = buildMatchEventInfo(response,businessEvent,request);
        matchEventInfoMessage.setEventCode("injury_time");
        //存放补时时长
        matchEventInfoMessage.setExtrainfo(request.getInjuryTime()+"");
        matchEventInfoMessage.setThirdEventId("PA_Event_input:"+ UUIdUtils.getId());

        return matchEventInfoMessage;
    }

    /**
     * 组装事件
     * @param response
     * @param businessEvent
     * @param request
     * @return
     */

    public MatchEventInfoMessage buildMatchEventInfo(Response<MatchScoreAndTimeVo> response,String businessEvent,EventOperationDto request){
        ThirdMatchInfo thirdMatchInfo = response.getData().getThirdMatchInfo();
        MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
        StandardMatchInfo standardMatchInfo = response.getData().getStandardMatchInfo();
        MatchEventInfoMessage matchEventInfoMessage = new MatchEventInfoMessage();
        matchEventInfoMessage.setCanceled(0);//未取消
        matchEventInfoMessage.setDataSourceCode(businessEvent);
        matchEventInfoMessage.setSourceType("1");//常规事件
        matchEventInfoMessage.setThirdEventId("PA_Event_input:"+ UUIdUtils.getId());

        //事件发生事件
        matchEventInfoMessage.setEventTime(System.currentTimeMillis());
        matchEventInfoMessage.setHomeAway("none");
        matchEventInfoMessage.setMatchPeriodId(standardMatchInfo.getMatchPeriodId());
        Integer secondsMatchStart =response.getData().getStandardMatchInfo().getSecondsMatchStart();
        MatchTimeInfo matchTimeInfo = response.getData().getMatchTimeInfo();
        if(matchTimeInfo.getSecondFromStart()!=null && matchTimeInfo.getSecondFromStart()!=0){
            Long times = matchTimeInfo.getSecondFromStart() + (System.currentTimeMillis() - matchTimeInfo.getEventTime()) / 1000;
            matchEventInfoMessage.setSecondsFromStart(times);
        }else{
            Long times  = Long.parseLong(secondsMatchStart.toString());
            matchEventInfoMessage.setSecondsFromStart(times);
        }
//        if(secondsMatchStart!=null && secondsMatchStart!=0){
//            Long times  = Long.parseLong(secondsMatchStart.toString());
//            matchEventInfoMessage.setSecondsFromStart(times);
//        }else{
//            MatchTimeInfo matchTimeInfo = response.getData().getMatchTimeInfo();
//            matchEventInfoMessage.setSecondsFromStart(matchTimeInfo.getSecondFromStart());
//        }

        matchEventInfoMessage.setT1(matchScoresInfo.getT1());
        matchEventInfoMessage.setT2(matchScoresInfo.getT2());

        matchEventInfoMessage.setCopyLinkId(request.getLinkedId());
        matchEventInfoMessage.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
        matchEventInfoMessage.setIsErrorEndEvent(0);
        matchEventInfoMessage.setSportId(thirdMatchInfo.getSportId());
        matchEventInfoMessage.setRemark(request.getOperatorName());
        //手动补时事件添加ad5=2,下游过滤该事件的走时（A01,客户端）
        matchEventInfoMessage.setAddition5("2");
        return matchEventInfoMessage;
    }

    public MatchEventInfoMessage buildMatchEventInfo(String businessEvent,EventOperationDto request,MatchScoresInfo matchScoresInfo,StandardMatchInfo standardMatchInfo,ThirdMatchInfo thirdMatchInfo){
        MatchEventInfoMessage matchEventInfoMessage = new MatchEventInfoMessage();
        matchEventInfoMessage.setCanceled(0);//未取消
        matchEventInfoMessage.setDataSourceCode(businessEvent);
        matchEventInfoMessage.setSourceType("1");//常规事件
        matchEventInfoMessage.setThirdEventId("PA_Event_input:"+ UUIdUtils.getId());
        //事件发生事件
        matchEventInfoMessage.setEventTime(System.currentTimeMillis());
        matchEventInfoMessage.setHomeAway("none");
        matchEventInfoMessage.setMatchPeriodId(standardMatchInfo.getMatchPeriodId());
        Integer secondsMatchStart =standardMatchInfo.getSecondsMatchStart();
        log.info("接口获取比赛进行时长：{},标准赛事表：{}",request.getMatchId(),secondsMatchStart);
//        if(secondsMatchStart!=null){
//            Long times  = Long.parseLong(secondsMatchStart.toString());
//            matchEventInfoMessage.setSecondsFromStart(times);
//        }/*else{
//            MatchTimeInfo matchTimeInfo = response.getData().getMatchTimeInfo();
//            matchEventInfoMessage.setSecondsFromStart(matchTimeInfo.getSecondFromStart());
//        }*/
        if(matchScoresInfo!=null){
            matchEventInfoMessage.setT1(matchScoresInfo.getT1());
            matchEventInfoMessage.setT2(matchScoresInfo.getT2());
            MatchTimeInfo matchTimeInfo = timeInfoRepository.selectByPrimaryKey(matchScoresInfo.getId());
            if(matchTimeInfo!=null && matchTimeInfo.getSecondFromStart()!=null){
                log.info("接口获取比赛进行时长：{},赛事时间表：{}",request.getMatchId(),matchTimeInfo.getSecondFromStart());
                Long times = matchTimeInfo.getSecondFromStart() + (System.currentTimeMillis() - matchTimeInfo.getEventTime()) / 1000;
                matchEventInfoMessage.setSecondsFromStart(times);
            }else{
                Long times  = Long.parseLong(secondsMatchStart.toString());
                log.info("接口获取比赛进行时长：{},标准赛事表：{}",request.getMatchId(),matchTimeInfo.getSecondFromStart());
                matchEventInfoMessage.setSecondsFromStart(times);
            }
        }else{
            matchEventInfoMessage.setT1(0);
            matchEventInfoMessage.setT2(0);
            matchEventInfoMessage.setSecondsFromStart(0L);
        }
        matchEventInfoMessage.setCopyLinkId(request.getLinkedId());
        matchEventInfoMessage.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
        matchEventInfoMessage.setIsErrorEndEvent(0);
        matchEventInfoMessage.setSportId(thirdMatchInfo.getSportId());
        matchEventInfoMessage.setRemark(request.getOperatorName());
        return matchEventInfoMessage;
    }
    private Long getMatchTime(MatchScoreAndTimeVo data) {
        MatchTimeInfo matchTimeInfo = data.getMatchTimeInfo();
        if(matchTimeInfo==null){
            return 0L;
        }
        Long matchTime;
        if (SportPeriodConstant.FootballPeriod.contans(matchTimeInfo.getPeriod())) {
            matchTime = matchTimeInfo.getSecondFromStart() + (System.currentTimeMillis() - matchTimeInfo.getEventTime()) / 1000;
        } else {
            matchTime = matchTimeInfo.getSecondFromStart();
        }
        return matchTime;
    }

    /**
     * 根据赛事ID查询比分
     * @param matchInfoIds
     * @return
     */
    @Override
    public Response searchMatchScores(List<Long> matchInfoIds) {
        try {
            List<MatchScoreDto> matchScoresInfosList = matchScoresInfoMapper.queryScoresListByMatchIds(matchInfoIds);
            log.info("==========查询赛事比分=========：{}",matchScoresInfosList);
            if(matchScoresInfosList.isEmpty()){
                return Response.success();
            }
            List<MatchScoreDto> reScoreDto = new ArrayList<>();
            for(MatchScoreDto score : matchScoresInfosList){
                if(score==null){
                    continue;
                }
                MatchScoreDto data = new MatchScoreDto();
                data.setMatchInfoId(score.getMatchInfoId());
                if(StringUtils.isNotEmpty(score.getScoresJson())){
                    Map<Long, FootballScores> scoresMap = JSONObject.parseObject(score.getScoresJson(), new TypeReference<Map<Long, FootballScores>>() {});
                    //上半场比分
                    FootballScores halfScore = scoresMap.get(6L);
                    Integer halfHomeGoal = 0;
                    Integer halfAwayGoal = 0;
                    if(halfScore!=null){
                        halfHomeGoal = halfScore.getGoal().getHome();
                        halfAwayGoal = halfScore.getGoal().getAway();
                        data.setHalfTimeScores(new com.panda.merge.cache.CommonItem(halfHomeGoal,halfAwayGoal));
                    }
                    //全场比分=上半场+下半场比分
                    FootballScores lastScore = scoresMap.get(7L);
                    if(lastScore!=null){
                        Integer lastHomeGoal = lastScore.getGoal().getHome()+halfHomeGoal;
                        Integer lastAwayGoal = lastScore.getGoal().getAway()+halfAwayGoal;
                        data.setAllTimeScores(new com.panda.merge.cache.CommonItem(lastHomeGoal,lastAwayGoal));
                    }
                    //加时赛上半场
                    FootballScores overTimeScore = scoresMap.get(41L);
                    Integer halfOverHomeGoal = 0;
                    Integer halfOverAwayGoal = 0;
                    if(overTimeScore!=null){
                        halfOverHomeGoal = overTimeScore.getGoal().getHome();
                        halfOverAwayGoal = overTimeScore.getGoal().getAway();
                        data.setAllOverTimeScores(new com.panda.merge.cache.CommonItem(halfOverHomeGoal,halfOverAwayGoal));
                    }
                    //加时赛全场=加时赛上半场+加时赛下半场
                    FootballScores allOverTimeScore = scoresMap.get(42L);
                    if(allOverTimeScore!=null){
                        Integer allOverHomeGoal = overTimeScore.getGoal().getHome()+halfOverHomeGoal;
                        Integer allOverAwayGoal = overTimeScore.getGoal().getAway()+halfOverAwayGoal;
                        data.setAllOverTimeScores(new com.panda.merge.cache.CommonItem(allOverHomeGoal,allOverAwayGoal));
                    }
                    //-1所有比分
                    FootballScores whoScores = scoresMap.get(-1L);
                    if(whoScores!=null){
                        data.setAttack(new com.panda.merge.cache.CommonItem(whoScores.getAttack().getHome(),whoScores.getAttack().getAway()));
                        data.setDangerousAttack(new com.panda.merge.cache.CommonItem(whoScores.getDangerousAttack().getHome(),whoScores.getDangerousAttack().getAway()));
                        data.setShotOn(new com.panda.merge.cache.CommonItem(whoScores.getShotOn().getHome(),whoScores.getShotOn().getAway()));
                        data.setShotOff(new com.panda.merge.cache.CommonItem(whoScores.getShotOff().getHome(),whoScores.getShotOff().getAway()));
                        data.setCorner(new com.panda.merge.cache.CommonItem(whoScores.getCorner().getHome(),whoScores.getCorner().getAway()));
                        data.setRedCard(new com.panda.merge.cache.CommonItem(whoScores.getRedCard().getHome(),whoScores.getRedCard().getAway()));
                        data.setYellowCard(new com.panda.merge.cache.CommonItem(whoScores.getYellowCard().getHome(),whoScores.getYellowCard().getAway()));
                        data.setBallPossessionPercentage(new com.panda.merge.cache.CommonItem(whoScores.getBallPossessionPercentage().getHome(),whoScores.getBallPossessionPercentage().getAway()));
                        if(whoScores.getExpectationXg()!=null){
                            data.setExpectationXg(new com.panda.merge.cache.CommonItemBigDecimal(whoScores.getExpectationXg().getHome(),whoScores.getExpectationXg().getAway()));
                        }else{
                            data.setExpectationXg(new com.panda.merge.cache.CommonItemBigDecimal(new BigDecimal(0),new BigDecimal(0)));
                        }
                        if(whoScores.getExpectationLoss()!=null){
                            data.setExpectationLoss(new com.panda.merge.cache.CommonItemBigDecimal(whoScores.getExpectationLoss().getHome(),whoScores.getExpectationLoss().getAway()));
                        }else{
                            data.setExpectationLoss(new com.panda.merge.cache.CommonItemBigDecimal(new BigDecimal(0),new BigDecimal(0)));
                        }
                    }
                }

                if(score.getScoresJsonExtra()!=null){
                    JSONObject jsonObj = JSON.parseObject(score.getScoresJsonExtra());
                    FootballPenaltyScoreDto penaltyScore = JSON.toJavaObject(jsonObj,FootballPenaltyScoreDto.class);
                    System.out.println(penaltyScore);
                    List<Map> list= penaltyScore.getRoundScores();
                    Map<String,Object> map = list.get(0);
                    Integer home = 0;
                    Integer away = 0;
                    for (Map.Entry<String,Object> entry : map.entrySet()) {
                        Object value = entry.getValue();
                        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(value);
                        CommonItem com = JSONObject.toJavaObject(jsonObject,CommonItem.class);
                        home += com.getHome();
                        away += com.getAway();
                    }
                    data.setPenaltyScore(new com.panda.merge.cache.CommonItem(home,away));
                }
                Integer homeAll = data.getAllTimeScores().getHome()+data.getAllTimeScores().getHome()+data.getPenaltyScore().getHome();
                Integer awayAll = data.getAllTimeScores().getAway()+data.getAllTimeScores().getAway()+data.getPenaltyScore().getAway();
                data.setAllScores(new com.panda.merge.cache.CommonItem(homeAll,awayAll));
                reScoreDto.add(data);
            }
            return Response.success(reScoreDto);
        }catch (Exception e){
            log.error("查询比分异常matchIds：{}",matchInfoIds,e);
        }
        return  null;
    }

    /**
     * 手动创建tMax事件
     * @param matchDto 操作内容
     * @return
     */
    @Override
    public Response operateTmaxEvent(EventOperationDto matchDto) {
        log.info("::{}::人工下发的operateTmaxEvent事件,开始处理 request={}", matchDto.getLinkedId(), matchDto);
        //1.查询当前赛事开售事件源
        Long standerdId = matchDto.getMatchId();
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellRepository.selectThirdMatchInfoPrimaryKey(standerdId);
        if (standardSportMarketSell == null) {
            return Response.failed("找不到开售信息!");
        }
        String businessEvent = standardSportMarketSell.getBusinessEvent();
        ThirdMatchInfoExample thirdMatchExample  =new ThirdMatchInfoExample();
        thirdMatchExample.createCriteria().andReferenceIdEqualTo(standardSportMarketSell.getMatchInfoId())
                .andDataSourceCodeEqualTo(businessEvent);
        List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoMapper.selectByExample(thirdMatchExample);
        if (thirdMatchInfos == null ) {
            return Response.failed("找不到三方赛事信息!");
        }
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfos.get(0);
        //2.校验三方赛事,赛事比分,赛事事件信息
//        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreateApi(thirdMatchInfos.get(0).getId());
//        if (!response.isSuccess()) {
//            return response;
//        }
        StandardMatchInfo standardMatchInfo = standardMatchInfoRepository.selectStandardMatchPrimaryKey(matchDto.getMatchId());
        if(standardMatchInfo==null){
            log.info("标准赛事不存在");
            return null;
        }
        MatchScoresInfo matchScoresInfo = matchScoreInfoRepository.selectByExample(thirdMatchInfo.getId(), SourceTypeEnum.LIVE_DATA.getCode());
        //4.封装事件信息
        MatchEventInfoMessage matchEventInfoMessage = buildMatchEventInfo(businessEvent,matchDto,matchScoresInfo,standardMatchInfo,thirdMatchInfo);
        String eventCode = matchDto.getEventCode();
        String scoreType = matchDto.getScoresType();
        switch (matchDto.getEventType()){
            case "safe":
                if(EventCodeEnum.GOAL.code.equals(scoreType)){
                    eventCode = "safe_event_goal";
                }else if(EventCodeEnum.CORNER.code.equals(scoreType)){
                    eventCode = "safe_event_corner";
                }else if("faCard".equals(scoreType)){
                    eventCode = "safe_event_booking";
                }
                break;
            case "danger":
                if(EventCodeEnum.GOAL.code.equals(scoreType)){
                    eventCode = "danger_event_goal";
                }else if(EventCodeEnum.CORNER.code.equals(scoreType)){
                    eventCode = "danger_event_corner";
                }else if("faCard".equals(scoreType)){
                    eventCode = "danger_event_booking";
                }
                break;
            case "Tmax":
                if(EventCodeEnum.GOAL.code.equals(scoreType)){
                    eventCode = "Tmax_event_goal";
                }else if(EventCodeEnum.CORNER.code.equals(scoreType)){
                    eventCode = "Tmax_event_corner";
                }else if("faCard".equals(scoreType)){
                    eventCode = "Tmax_event_booking";
                }
                break;
            case "reject":
                if(EventCodeEnum.GOAL.code.equals(scoreType)){
                    eventCode = "reject_event_goal";
                }else if(EventCodeEnum.CORNER.code.equals(scoreType)){
                    eventCode = "reject_event_corner";
                }else if("faCard".equals(scoreType)){
                    eventCode = "reject_event_booking";
                }
                break;
            default:
                eventCode = null;
                break;
        }
        if(StringUtils.isEmpty(eventCode)){
            return Response.failed("事件code为空!");
        }
        matchEventInfoMessage.setEventCode(eventCode);

        //5.事件下发到实时服务
        sendMatchEventMessage(matchEventInfoMessage, matchDto.getLinkedId(), matchDto.getMatchId());
        log.info("::{}::人工下发的operateTmaxEvent事件,结束 request={}", matchDto.getLinkedId(), JSON.toJSON(matchEventInfoMessage));

        return Response.success();
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
        if (pandaDataMqGatewayevent != 2 || org.apache.commons.lang3.StringUtils.isBlank(standardMatchId)) {
            return false;
        }

        // 处理备用MQ配置
        if (org.apache.commons.lang3.StringUtils.isBlank(pandaDataMqGatewayMatchId)) {
            return true;
        }

        // 转换为set集合
        Set<String> spareMatchIds = new HashSet<>(
                Arrays.asList(pandaDataMqGatewayMatchId.split(","))
        );
        return spareMatchIds.contains(standardMatchId);
    }



    private static List<Long> getAllTimePeriods(StandardScoreCenter scoreCenter) {
        List<Long> settlePeriods = new ArrayList<>();
//        settlePeriods.add(-1L);
        Long[] periods = {6L,7L,41L,42L,50L};
        addSettlePeriods(settlePeriods,periods,scoreCenter);
        //102405
//        Long[] minutePeriods5 = {6005L,6010L,6015L,6020L,6025L,6030L,6035L,6040L,6045L,6050L,7050L,7055L,7060L,7065L,7070L,7075L,7080L,7085L,7090L,7095L};
//        addSettlePeriodsMin(settlePeriods,minutePeriods5,scoreCenter,5);
        Long[] minutePeriods15 = {60899L,61799L,62699L,73599L,74499L,75399L};
        addSettlePeriodsMin(settlePeriods,minutePeriods15,scoreCenter,15);
        return settlePeriods;
    }

    private static void addSettlePeriods(List<Long> settlePeriods, Long[] periods, StandardScoreCenter scoreCenter) {
        int index = 0;
        for(int i = 0;i<periods.length;i++){
            if(periods[i]==scoreCenter.getPeriod().intValue()){
                index = i;
                break;
            }
        }
        for(int i = 0;i<index;i++){
            settlePeriods.add(periods[i]);
        }
        log.info("::{}::赛事中断结算下发，添加的阶段比分时段为：{}", scoreCenter.getStandardMatchId(), JSON.toJSON(settlePeriods));
    }
    private static void addSettlePeriodsMin(List<Long> settlePeriods, Long[] periods, StandardScoreCenter scoreCenter,int min) {
//        if(min==5){
//            minPeriod =SportPeriodConstant.FootballPeriod.get5MinPeriod(scoreCenter.getPeriod().longValue(),scoreCenter.getInterruptTime());
//        }else{
        Long minPeriod =SportPeriodConstant.FootballPeriod.get15MinPeriod(scoreCenter.getPeriod().longValue(),scoreCenter.getInterruptTime());
//        }
        if(scoreCenter.getPeriod()>7){
            //下半场后中断，则添加所有区间比分
            List<Long> list = Arrays.asList(60899L,61799L,62699L,73599L,74499L,75399L);
            settlePeriods.addAll(list);
        }else{
            if(minPeriod==null){
                log.info("::{}::赛事中断结算下发，添加的"+min+"分钟时段为：无", scoreCenter.getStandardMatchId());
                return;
            }
            int index = 0;
            for(int i = 0;i<periods.length;i++){
                if(Objects.equals(periods[i], minPeriod)){
                    index = i;
                    break;
                }
            }
            for(int i = 0;i<index;i++){
                settlePeriods.add(periods[i]);
            }
        }
        log.info("::{}::赛事中断结算下发，添加的"+min+"分钟时段为：{}", scoreCenter.getStandardMatchId(), JSON.toJSON(settlePeriods));
    }

    /**
     * 根据比赛开始时间获取所有适用的篮球时段ID（基于倒计时逻辑）
     * @param secondStart 从比赛开始的秒数
     * @return 所有适用的时段ID列表，包括基础时段和6分钟时段
     */
    public static List<Long> getAllBasketballTimeSlotPeriods(Long secondStart) {
        List<Long> periodIds = new ArrayList<>();

        // 添加基础时段
        periodIds.add(-1L); // 全场

        if (secondStart == null || secondStart <= 0) {
            return periodIds;
        }

        // 根据时间判断当前阶段和已完成的阶段
        Long currentPeriod = getCurrentBasketballPeriod(secondStart);
        Long timeInCurrentPeriod = getTimeInCurrentPeriod(secondStart);

        // 添加所有已完成的基础时段
        if (secondStart > 0) periodIds.add(13L);      // 第1节
        if (secondStart > 60 * 12) periodIds.add(14L); // 第2节
        if (secondStart > 60 * 24) periodIds.add(15L); // 第3节
        if (secondStart > 60 * 36) periodIds.add(16L); // 第4节
        if (secondStart > 60 * 48) periodIds.add(40L); // 加时赛

        // 添加所有已完成的6分钟时段
        List<Long> completed6MinPeriods = getAllCompletedBasketball6MinPeriods(secondStart);
        periodIds.addAll(completed6MinPeriods);

        return periodIds;
    }

    /**
     * 获取所有已完成的篮球6分钟时段（基于倒计时逻辑）
     * @param secondStart 从比赛开始的秒数
     * @return 所有已完成的6分钟时段ID列表
     */
    private static List<Long> getAllCompletedBasketball6MinPeriods(Long secondStart) {
        List<Long> periods = new ArrayList<>();
        if (secondStart == null || secondStart <= 0) {
            return periods;
        }

        // 每节12分钟(720秒)，6分钟时段的判断逻辑
        Long twelveMin = 720L;

        // 第1节 (0-12分钟)
        if (secondStart > 0) {
            Long timeInPeriod1 = Math.min(secondStart, twelveMin);
            Long remainingTime1 = twelveMin - timeInPeriod1;

            if (remainingTime1 <= 60 * 6) periods.add(1312L);  // 第1节后6分钟
            if (remainingTime1 <= 60 * 12) periods.add(1306L); // 第1节前6分钟
        }

        // 第2节 (12-24分钟)
        if (secondStart > 60 * 12) {
            Long timeInPeriod2 = Math.min(secondStart - 60 * 12, twelveMin);
            Long remainingTime2 = twelveMin - timeInPeriod2;

            if (remainingTime2 <= 60 * 6) periods.add(1412L);  // 第2节后6分钟
            if (remainingTime2 <= 60 * 12) periods.add(1406L); // 第2节前6分钟
        }

        // 第3节 (24-36分钟)
        if (secondStart > 60 * 24) {
            Long timeInPeriod3 = Math.min(secondStart - 60 * 24, twelveMin);
            Long remainingTime3 = twelveMin - timeInPeriod3;

            if (remainingTime3 <= 60 * 6) periods.add(1512L);  // 第3节后6分钟
            if (remainingTime3 <= 60 * 12) periods.add(1506L); // 第3节前6分钟
        }

        // 第4节 (36-48分钟)
        if (secondStart > 60 * 36) {
            Long timeInPeriod4 = Math.min(secondStart - 60 * 36, twelveMin);
            Long remainingTime4 = twelveMin - timeInPeriod4;

            if (remainingTime4 <= 60 * 6) periods.add(1612L);  // 第4节后6分钟
            if (remainingTime4 <= 60 * 12) periods.add(1606L); // 第4节前6分钟
        }

        return periods;
    }

    /**
     * 获取当前篮球比赛所在的节次
     * @param secondStart 从比赛开始的秒数
     * @return 当前节次 (13-16为1-4节, 40为加时)
     */
    private static Long getCurrentBasketballPeriod(Long secondStart) {
        if (secondStart <= 60 * 12) return 13L;      // 第1节
        else if (secondStart <= 60 * 24) return 14L; // 第2节
        else if (secondStart <= 60 * 36) return 15L; // 第3节
        else if (secondStart <= 60 * 48) return 16L; // 第4节
        else return 40L; // 加时赛
    }

    /**
     * 获取在当前节次中已经进行的时间
     * @param secondStart 从比赛开始的秒数
     * @return 当前节次中的时间（秒数）
     */
    private static Long getTimeInCurrentPeriod(Long secondStart) {
        if (secondStart <= 60 * 12) return secondStart;
        else if (secondStart <= 60 * 24) return secondStart - 60 * 12;
        else if (secondStart <= 60 * 36) return secondStart - 60 * 24;
        else if (secondStart <= 60 * 48) return secondStart - 60 * 36;
        else return secondStart - 60 * 48; // 加时赛
    }

    /**
     * 获取足球方面所有已完成的时段（包括5分钟和15分钟时段）
     * @param secondStart 从比赛开始的秒数
     * @return 所有已完成的时段ID列表
     */
    private static List<Long> getAllCompletedTimePeriods(Long secondStart) {
        List<Long> periods = new ArrayList<>();
        if (secondStart == null) {
            return periods;
        }

        // 上半场5分钟时段
        if (secondStart >= 60 * 5)  periods.add(6005L);   // 0-5分钟
        if (secondStart >= 60 * 10) periods.add(6010L);  // 5-10分钟
        if (secondStart >= 60 * 15) periods.add(6015L);  // 10-15分钟
        if (secondStart >= 60 * 20) periods.add(6020L);  // 15-20分钟
        if (secondStart >= 60 * 25) periods.add(6025L);  // 20-25分钟
        if (secondStart >= 60 * 30) periods.add(6030L);  // 25-30分钟
        if (secondStart >= 60 * 35) periods.add(6035L);  // 30-35分钟
        if (secondStart >= 60 * 40) periods.add(6040L);  // 35-40分钟
        if (secondStart >= 60 * 45) periods.add(6045L);  // 40-45分钟
        if (secondStart > 60 * 45)  periods.add(6050L);   // 上半场补时

        // 下半场5分钟时段
        if (secondStart >= 60 * 50) periods.add(7050L);  // 45-50分钟
        if (secondStart >= 60 * 55) periods.add(7055L);  // 50-55分钟
        if (secondStart >= 60 * 60) periods.add(7060L);  // 55-60分钟
        if (secondStart >= 60 * 65) periods.add(7065L);  // 60-65分钟
        if (secondStart >= 60 * 70) periods.add(7070L);  // 65-70分钟
        if (secondStart >= 60 * 75) periods.add(7075L);  // 70-75分钟
        if (secondStart >= 60 * 80) periods.add(7080L);  // 75-80分钟
        if (secondStart >= 60 * 85) periods.add(7085L);  // 80-85分钟
        if (secondStart >= 60 * 90) periods.add(7090L);  // 85-90分钟
        if (secondStart > 60 * 90) periods.add (7095L);   // 下半场补时

        // 上半场15分钟时段
        if (secondStart >= 60 * 15) periods.add(60899L);  // 0-15分钟
        if (secondStart >= 60 * 30) periods.add(61799L);  // 15-30分钟
        if (secondStart >= 60 * 45) periods.add(62699L);  // 30-45分钟

        // 下半场15分钟时段
        if (secondStart >= 60 * 60) periods.add(73599L);  // 45-60分钟
        if (secondStart >= 60 * 75) periods.add(74499L);  // 60-75分钟
        if (secondStart >= 60 * 90) periods.add(75399L);  // 75-90分钟

        return periods;
    }

    @Override
    public Response checkMinScores(ScoresCenterCheckSwitchDTO checkSwitch) {
        log.info("修改区间比分校验开关:{}",checkSwitch);
        String redisKey = "scores:min:check:switch:"+checkSwitch.getMatchId();
        //缓存一天，24小时后失效
        redisService.set(redisKey,checkSwitch.getMinScoresCheck(),RedisConfig.REDIS_DEFAULT_TIME);

        log.info("修改区间比分校验开关,保存操作日志:{}",checkSwitch);
        Integer showStatus = checkSwitch.getMinScoresCheck()?1:0;
        MatchScoresCenterLog matchScoresCenterLog = new MatchScoresCenterLog();
        String matchManageId = checkSwitch.getMatchManageId();
        matchScoresCenterLog.setMatchManageId(matchManageId);
        matchScoresCenterLog.setOperateId(matchManageId);
        matchScoresCenterLog.setOperateName("");
        matchScoresCenterLog.setOperateParaName(OperateLogTypeEnum.MINUTES_SCORES_CHECK_SWITCH.getCode() + "");
        matchScoresCenterLog.setOperateType(OperateLogTypeEnum.MINUTES_SCORES_CHECK_SWITCH.getCode() + "");
        matchScoresCenterLog.setOperateRearText(String.valueOf(showStatus));
        showStatus++;
        String before = String.valueOf(new StringBuffer(Integer.toBinaryString(showStatus)).reverse().toString().charAt(0));
        matchScoresCenterLog.setOperateForwText(before);
        matchScoresCenterLog.setMatchManageId(matchManageId);
        matchScoresCenterLog.setOperateModule(OperateLogTypeEnum.SCORES_SETTLE_10038.getCode() + "");
        matchScoresCenterLog.setOperateUserName(checkSwitch.getOperatorName());
        matchScoresCenterLog.setIpAddress(checkSwitch.getIpAddress());
        matchScoresCenterLog.setOperateMatchId(matchManageId);
        matchScoresCenterLog.setCreateTime(System.currentTimeMillis());
        matchScoresCenterLog.setModifyTime(System.currentTimeMillis());
        matchScoresCenterLogMapper.insert(matchScoresCenterLog);


        return Response.success();
    }

    @Override
    public Response updateScoreShowStatus(StandardScoreCenterDTO scoreCenterDto) {
        log.info("修改比分展示开关:{}",scoreCenterDto.getStandardMatchId());
        StandardMatchScores scores = scoresRedisHelp.getCatchStandScoreByMatchId(scoreCenterDto.getStandardMatchId());
        if(scores!=null){
            scores.setShowStatus(scoreCenterDto.getShowStatus());
            scores.setUpdateTime(System.currentTimeMillis());
            scoresRedisHelp.saveCatchStandScore(scores);
            log.info("修改比分展示开关，修改比分数据完成:{}",scoreCenterDto.getStandardMatchId());
            EditScoreResultStatusRequest request = new EditScoreResultStatusRequest();
            //定义类型为3 手动下发比分展示开关
            request.setType(3);
            request.setStatus(scoreCenterDto.getShowStatus());
            request.setStandardMatchId(scoreCenterDto.getStandardMatchId());
            request.setSportId(scoreCenterDto.getSportId());
            scoresProducer.sendMatchShowStatus(request,scoreCenterDto.getStandardMatchId()+"_scoreCenter");
            log.info("修改比分展示开关，下发比分展示完成:{}",scoreCenterDto.getStandardMatchId());
            //切换开关保存日志
            editMatchResultShowStatusLog(scores,scoreCenterDto);
        }else{
            log.info("修改比分展示开关：赛事不存在:{}",scoreCenterDto.getStandardMatchId());
        }
        return Response.success();
    }

    public void editMatchResultShowStatusLog(StandardMatchScores scores,StandardScoreCenterDTO centerDto) {
        log.info("保存操作日志:{}",centerDto);
        Integer showStatus = centerDto.getShowStatus();
        MatchScoresCenterLog matchScoresCenterLog = new MatchScoresCenterLog();
        String matchManageId = scores.getMatchManageId();
        matchScoresCenterLog.setMatchManageId(matchManageId);
        matchScoresCenterLog.setOperateId(matchManageId);
        matchScoresCenterLog.setOperateName("");
        matchScoresCenterLog.setOperateParaName(OperateLogTypeEnum.SCORES_CENTER_MATCH_SETTING.getCode() + "");
        matchScoresCenterLog.setOperateType(OperateLogTypeEnum.SCORES_CENTER_MATCH_SETTING.getCode() + "");
//        if(showStatus==1){
//            //开
//            matchScoresCenterLog.setOperateRearText(OperateLogTypeEnum.SCORES_CENTER_OPEN.getCode()+ "");
//            matchScoresCenterLog.setOperateForwText(OperateLogTypeEnum.SCORES_CENTER_CLOSE.getCode()+ "");
//        }else{
//            //关
//            matchScoresCenterLog.setOperateRearText(OperateLogTypeEnum.SCORES_CENTER_CLOSE.getCode()+ "");
//            matchScoresCenterLog.setOperateForwText(OperateLogTypeEnum.SCORES_CENTER_OPEN.getCode()+ "");
//        }
        matchScoresCenterLog.setOperateRearText(String.valueOf(showStatus));
        showStatus++;
        String before = String.valueOf(new StringBuffer(Integer.toBinaryString(showStatus)).reverse().toString().charAt(0));
        matchScoresCenterLog.setOperateForwText(before);
        matchScoresCenterLog.setMatchManageId(matchManageId);
        matchScoresCenterLog.setOperateModule(OperateLogTypeEnum.SCORES_SETTLE_10038.getCode() + "");
        matchScoresCenterLog.setOperateUserName(centerDto.getOperatorName());
        matchScoresCenterLog.setIpAddress(centerDto.getIpAddress());
        matchScoresCenterLog.setOperateMatchId(matchManageId);
        matchScoresCenterLog.setCreateTime(System.currentTimeMillis());
        matchScoresCenterLog.setModifyTime(System.currentTimeMillis());
        matchScoresCenterLogMapper.insert(matchScoresCenterLog);
    }
}
