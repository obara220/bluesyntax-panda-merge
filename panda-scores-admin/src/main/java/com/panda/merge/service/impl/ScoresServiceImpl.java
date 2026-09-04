package com.panda.merge.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.service.BasketBallAdvertiseService;
import com.panda.merge.advertise.service.CommonAdvertiseService;
import com.panda.merge.advertise.utils.MatchPeriodUtils;
import com.panda.merge.calculation.CalculationService;
import com.panda.merge.calculation.impl.*;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.RepositoryConstant;
import com.panda.merge.constant.SourceTypeEnum;
import com.panda.merge.constant.SportPeriodWholeArrayEnum;
import com.panda.merge.constant.SportTypeEnum;
import com.panda.merge.dto.BasketballScores;
import com.panda.merge.dto.FootballScores;
import com.panda.merge.dto.TennisScores;
import com.panda.merge.dto.scores.MatchScoresBetterDto;
import com.panda.merge.mapper.MatchScoresInfoMapper;
import com.panda.merge.mapper.MatchScoresSourceTypeMapper;
import com.panda.merge.mapper.MatchTimeInfoMapper;
import com.panda.merge.mapper.MatchTimeInfoMapper;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.model.*;
import com.panda.merge.repository.*;
import com.panda.merge.service.IScoresService;
import com.panda.merge.service.ThirdMatchInfoService;
import com.panda.merge.utils.JsonMapUtils;
import com.panda.merge.utils.MessageGZIP;
import com.panda.merge.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.panda.merge.constant.RepositoryConstant.MATCH_SCORES_INFO;
import static com.panda.merge.constant.RepositoryConstant.MATCH_TIME_INFO;
import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;


@Service
@Slf4j
public class ScoresServiceImpl implements IScoresService {
//    @Autowired
//    MatchScoresInfoMapper matchScoresInfoMapper;
//    @Autowired
//    MatchScoresSourceTypeMapper matchScoresSourceTypeMapper;

//    @Autowired
//    StandardMatchInfoMapper standardMatchInfoMapper;
//    @Autowired
//    ThirdMatchInfoMapper thirdMatchInfoMapper;
//    @Autowired
//    MatchTimeInfoMapper matchTimeInfoMapper;
    @Autowired
    BasketBallAdvertiseService basketBallAdvertiseService;
    @Autowired
    BaseProcessor baseProcessor;
    @Autowired
    ThirdMatchInfoRepository thirdMatchInfoRepository;
    @Autowired
    StandardSportMarketSellRepository standardSportMarketSellRepository;
    @Autowired
    StandardMatchInfoRepository standardMatchInfoRepository;
    @Autowired
    MatchScoresSourceTypeRepository matchScoresSourceTypeRepository;
    @Autowired
    MatchScoreInfoRepository matchScoresInfoRepository;
    @Autowired
    MatchTimeInfoRepository matchTimeInfoRepository;
    @Autowired
    RedisService redisService;
    @Autowired
    private ScoresRedisHelp scoresRedisHelp;
    @Autowired
    CommonAdvertiseService commonAdvertiseService;
//    @Autowired
//    PdMatchInfoRepository pdMatchInfoRepository;
    @Autowired
    ThirdMatchInfoService thirdMatchInfoService;

    private static final Map<Long, Class<? extends CalculationService>> calculationServiceMap = new HashMap<>();
    static {
        calculationServiceMap.put(SportTypeEnum.FOOTBALL.getValue(), FootballCalculationServiceImpl.class);
        calculationServiceMap.put(SportTypeEnum.BASKETBALL.getValue(), BasketballCalculationServiceImpl.class);
        calculationServiceMap.put(SportTypeEnum.TENNIS.getValue(), TennisCalculationServiceImpl.class);
        calculationServiceMap.put(SportTypeEnum.AMERICAN_FOOTBALL.getValue(), AmericanFootballCalculationServiceImpl.class);
        calculationServiceMap.put(SportTypeEnum.TABLE_TENNIS.getValue(), TableTennisCalculationServiceImpl.class);
        calculationServiceMap.put(SportTypeEnum.BADMINTON.getValue(), BadmintonCalculationServiceImpl.class);
        calculationServiceMap.put(SportTypeEnum.VOLLEYBALL.getValue(), VolleyballCalculationServiceImpl.class);
        calculationServiceMap.put(SportTypeEnum.SNOOKER.getValue(),SnookerCalculationServiceImpl.class);
        calculationServiceMap.put(SportTypeEnum.BASEBALL.getValue(),BaseballCalculationServiceImpl.class);
        calculationServiceMap.put(SportTypeEnum.ICE_HOCKEY.getValue(),IceHockeyCalculationServiceImpl.class);
        calculationServiceMap.put(SportTypeEnum.HANDBALL.getValue(),HandballCalculationServiceImpl.class);
        calculationServiceMap.put(SportTypeEnum.UK_FOOTBALL.getValue(),UKFootballCalculationServiceImpl.class);
        calculationServiceMap.put(SportTypeEnum.HOCKEY.getValue(),HockeyCalculationServiceImpl.class);
        calculationServiceMap.put(SportTypeEnum.WATER_BALL.getValue(),WaterballCalculationServiceImpl.class);
        calculationServiceMap.put(SportTypeEnum.BEACH_VOLLEYBALL.getValue(),BeachVolleyballCalculationServiceImpl.class);
        calculationServiceMap.put(SportTypeEnum.CRICKET_BALL.getValue(), CricketCalculationServiceImpl.class);

    }

    @Override
    public CalculationService getCalculationService(Long sportId) {
        Class<? extends CalculationService> clazz = calculationServiceMap.get(sportId);
        if (clazz != null) {
            try {
                return SpringUtils.getBean(clazz);
            } catch (Exception e) {
                log.info("getCalculationService,sportId:{},clazz:{},获取CalculationService失败！",sportId,clazz);
//                log.error("getCalculationService,获取CalculationService失败！Exception:",e);
            }
        } else {
            log.info("getCalculationService,sportId:{},clazz为空,无需处理！",sportId);
        }
        return null;
    }


    @Override
    public boolean  ifMatchSoldByThirdMatchId(ThirdMatchInfo thirdMatchInfo,StandardMatchInfo standardMatchInfo){
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellRepository.selectThirdMatchInfoPrimaryKey(standardMatchInfo.getId());
        log.info("检查开售ifMatchSoldByThirdMatchId,standardMatchInfoId:{},standardSportMarketSell:{}",standardMatchInfo.getId(),standardSportMarketSell);
        if (standardSportMarketSell == null || thirdMatchInfo.getReferenceId() == null) {
            log.info("检查开售ifMatchSoldByThirdMatchId,standardMatchInfoId:{},standardSportMarketSell为空,无需处理！",standardMatchInfo.getId());
            return false;
        }
        if(standardSportMarketSell.getPreMatchSellStatus()==null&&standardSportMarketSell.getLiveMatchSellStatus()==null){
            log.info("检查开售ifMatchSoldByThirdMatchId,standardMatchInfoId:{},standardSportMarketSell sellStatus为空,无需处理！",standardMatchInfo.getId());
            return false;
        }
        if(standardSportMarketSell.getPreMatchSellStatus()!=null&&standardSportMarketSell.getLiveMatchSellStatus()!=null){
            if(standardSportMarketSell.getPreMatchSellStatus().equals("Unsold")&&standardSportMarketSell.getLiveMatchSellStatus().equals("Unsold")){
                log.info("检查开售ifMatchSoldByThirdMatchId,standardMatchInfoId:{},standardSportMarketSell sellStatus都为Unsold,无需处理！",standardMatchInfo.getId());
                return false;
            }
        }
        //BC拦截不支持滚球赛事
        if(thirdMatchInfo.getDataSourceCode().equals("BC")){
            return baseProcessor.bcEventProcessor("thirdMatchId:"+thirdMatchInfo.getId(),standardMatchInfo,thirdMatchInfo);        }
        return true;
    }
    //开售事件源当前阶段查询
    @Override
    public Long getSoldEventScoresPeriod(Long thirdMatchId) {
        ThirdMatchInfo thirdMatchInfo =thirdMatchInfoRepository.selectThirdMatchInfoByPrimaryKey(thirdMatchId);
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellRepository.selectThirdMatchInfoPrimaryKey(thirdMatchInfo.getReferenceId());
//        StandardSportMarketSell standardSportMarketSell = scoresRedisHelp.getCatchSportMarketSellByMatchId(thirdMatchInfo.getReferenceId());
        if (standardSportMarketSell == null) {
            return null;
        }
        String businessEvent = standardSportMarketSell.getBusinessEvent();
        ThirdMatchInfo businessMatch =thirdMatchInfoRepository.selectByStandardIdAndDataSourceCode(thirdMatchInfo.getReferenceId(),businessEvent);
//        ThirdMatchInfo businessMatch = scoresRedisHelp.getCatchThirdMatchInfoByReferenceIdAndSourceCode(thirdMatchInfo.getReferenceId(),businessEvent);
        if(businessMatch==null){
            return null;
        }
        Long thirdMatchSoldId =businessMatch.getId();
        MatchScoresInfo matchScoresInfo =matchScoresInfoRepository.selectByExample(thirdMatchSoldId,SourceTypeEnum.LIVE_DATA.getCode());
//        MatchScoresInfo matchScoresInfo = scoresRedisHelp.getCatchScoresByThirdIdAndSourceType(thirdMatchSoldId,SourceTypeEnum.LIVE_DATA.getCode());
        log.info("BT  getSoldEventScoresPeriod matchId:{},period:{}",thirdMatchInfo.getReferenceId(),matchScoresInfo.getPeriod());
        return matchScoresInfo.getPeriod();
    }

    @Override
    public MatchScoresInfo checkBasketPeriodAndSixScore(MatchScoreAndTimeVo matchScoreAndTimeVo, Long period, Long secondFromStart){
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoreAndTimeVo.getMatchScoresInfo().getScoresJson());
        Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodFootballScores);
        if(period.equals(1L)||period.equals(2L)||period.equals(13L)||period.equals(14L)||period.equals(15L)||period.equals(16L)){
            BasketballScores periodScore = allPeriodScores.get(period);
            if(periodScore==null){
                periodScore =new BasketballScores(period);
                allPeriodScores.put(period,periodScore);
            }
        }
        Integer matchLength = matchScoreAndTimeVo.getMatchTimeInfo().getMatchLength();
        Long sixPeriod = MatchPeriodUtils.getBasketSixPeriod(period,secondFromStart,matchLength);
        if(sixPeriod!=null){
            BasketballScores sixPeriodScore = allPeriodScores.get(sixPeriod);
            if(sixPeriodScore==null){
                sixPeriodScore =new BasketballScores(sixPeriod);
                allPeriodScores.put(sixPeriod,sixPeriodScore);
            }
        }
        matchScoreAndTimeVo.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
//        matchScoresInfoMapper.updateByPrimaryKey(matchScoreAndTimeVo.getMatchScoresInfo());
//        matchScoresInfoRepository.updateScoresInfo(matchScoreAndTimeVo.getMatchScoresInfo());
        return matchScoreAndTimeVo.getMatchScoresInfo();
//        matchScoresInfoRepository.updateScoresInfoCache(matchScoreAndTimeVo.getMatchScoresInfo());

    }



    @Override
    public boolean isLivedataStoped(Long thirdMatchId) {

        MatchScoresInfo matchScoresInfo = matchScoresInfoRepository.selectByExample(thirdMatchId,SourceTypeEnum.LIVE_DATA.getCode());
        if(matchScoresInfo==null||matchScoresInfo.getScoresJson()==null){
            log.info("isLivedataStoped 0三方赛事ID：{} 暂无有效事件, 判断可下发UOF比分",thirdMatchId);
            return true;
        }else {
            return false;
        }
    }

    private boolean checkChangeUOF(MatchScoresInfo matchScoresInfo) {
        //斯诺克暂时不切换UOF
        /*if(matchScoresInfo.getSportId().equals(7l)){
            return false;
        }*/
        Long eventTime= matchScoresInfo.getModifyTime()+1000*60*2;
        if (Instant.now().toEpochMilli() > eventTime) {
            if(periodIsNotStop(matchScoresInfo)) {
                return true;
            }
        }
        return false;
    }



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


    /**
     * 校验事件源是否匹配
     * @param thirdMatchInfo
     * @param matchScoresInfo
     * @return
     */
    public boolean checkStandardScore( ThirdMatchInfo thirdMatchInfo, MatchScoresInfo matchScoresInfo) {
        if(thirdMatchInfo.getReferenceId()==null){
            return false;
        }
        StandardMatchInfo standardMatchInfo =standardMatchInfoRepository.selectStandardMatchPrimaryKey(thirdMatchInfo.getReferenceId());
        if(standardMatchInfo==null){
            log.info("checkStandardScore 校验事件源是否匹配::{}::标准赛事不存在",thirdMatchInfo.getReferenceId());
            return false;
        }
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellRepository.selectThirdMatchInfoPrimaryKey(thirdMatchInfo.getReferenceId());
        if(standardSportMarketSell != null && standardSportMarketSell.getBusinessEvent() != null){
            if(standardSportMarketSell.getBusinessEvent().equals(matchScoresInfo.getDataSourceCode())){
                return true;
            }else {
                log.info("{}checkStandardScore 校验事件源不匹配，BusinessEvent={}，matchScoresInfo.getDataSourceCode()={}",
                        thirdMatchInfo.getReferenceId(),standardSportMarketSell.getBusinessEvent(),matchScoresInfo.getDataSourceCode());
                return false;
            }
        }else {
            if(standardMatchInfo.getThirdMatchId().equals(thirdMatchInfo.getId())){
                return true;
            }else {
                log.info("{} checkStandardScore 校验三方赛事ID不匹配，standardMatchInfo.getThirdMatchId()={}，thirdMatchInfo.getId()={}",
                        thirdMatchInfo.getReferenceId(),standardMatchInfo.getThirdMatchId(),thirdMatchInfo.getId());
                return false;
            }
        }
    }

    public boolean checkStandardScore( ThirdMatchInfo thirdMatchInfo) {
        StandardMatchInfo standardMatchInfo = standardMatchInfoRepository.selectStandardMatchPrimaryKey(thirdMatchInfo.getReferenceId());
        if(standardMatchInfo==null){
            return false;
        }
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellRepository.selectThirdMatchInfoPrimaryKey(thirdMatchInfo.getReferenceId());
        if(standardSportMarketSell != null && standardSportMarketSell.getBusinessEvent() != null){
            if(standardSportMarketSell.getBusinessEvent().equals(thirdMatchInfo.getDataSourceCode())){
                return true;
            }else {
                return false;
            }
        }else {
            if(standardMatchInfo.getThirdMatchId().equals(thirdMatchInfo.getId())){
                return true;
            }else {
                return false;
            }
        }
    }

    public MatchScoresInfo searchMatchScoreByStandardId( Long standardId) {
        StandardMatchInfo standardMatchInfo = standardMatchInfoRepository.selectStandardMatchPrimaryKey(standardId);
        if(standardMatchInfo==null){
            return null;
        }
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellRepository.selectThirdMatchInfoPrimaryKey(standardId);
        if (standardSportMarketSell == null){
           return null;
        }
        String dataSourceCode = standardSportMarketSell.getBusinessEvent();
//        ThirdMatchInfoExample thirdMatchInfoExample = new ThirdMatchInfoExample();
//        thirdMatchInfoExample.createCriteria().andReferenceIdEqualTo(standardId).andDataSourceCodeEqualTo(dataSourceCode);
//        List<ThirdMatchInfo> list= thirdMatchInfoMapper.selectByExample(thirdMatchInfoExample);
//        if(list.size()==0){
//            return null;
//        }
//        ThirdMatchInfo thirdMatchInfo =list.get(0);
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoRepository.selectByStandardIdAndDataSourceCode(standardId, dataSourceCode);
        if (ObjectUtils.isEmpty(thirdMatchInfo)) {
            return null;
        }
//        MatchScoresInfoExample scoresInfoExample= new MatchScoresInfoExample();
//        scoresInfoExample.createCriteria().andThirdMatchIdEqualTo(thirdMatchInfo.getId()).andDataSourceTypeEqualTo("1");
//        List<MatchScoresInfo> matchScoresInfos  =matchScoresInfoMapper.selectByExample(scoresInfoExample);
//        if(matchScoresInfos.size()==0){
//            return null;
//        }else {
//            return matchScoresInfos.get(0);
//        }
        return matchScoresInfoRepository.selectByExample(thirdMatchInfo.getId(), SourceTypeEnum.LIVE_DATA.getCode());
    }

    /**
     * 校验赛事比分
     * @param standardId
     * @return
     */
    @Override
    public Long checkStandardScore(Long standardId) {

        StandardMatchInfo standardMatchInfo = standardMatchInfoRepository.selectStandardMatchPrimaryKey(standardId);
        if(standardMatchInfo==null){
            return null;
        }
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellRepository.selectThirdMatchInfoPrimaryKey(standardId);
        if (standardSportMarketSell == null){
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(standardId,standardSportMarketSell.getBusinessEvent());
            if(thirdMatchInfo!=null){
                return thirdMatchInfo.getId();
            }else{
                return standardMatchInfo.getThirdMatchId();
            }
        }else {
            return standardMatchInfo.getThirdMatchId();
        }
    }

    /**
     * 主客队调换
     * @param standardScore
     */
    public void changeHomeAway(MatchScoresBetterDto standardScore) {
        //只有足球才做主客队相反
        try {
            if (standardScore != null && standardScore.getSportId().equals(1L)) {
                //只有UOF比分才 要主客队互换
                if (standardScore.getDataSourceType().equals(SourceTypeEnum.UOF.getCode().toString())) {
                        if (standardScore.getHomeAwayOpposite() != null && 1 == standardScore.getHomeAwayOpposite()) {
                            Integer t1 = standardScore.getT1();
                            Integer t2 = standardScore.getT2();
                            Integer periodT1 = standardScore.getPeriodT1();
                            Integer periodT2 = standardScore.getPeriodT2();
                            standardScore.setPeriodT1(periodT2);
                            standardScore.setPeriodT2(periodT1);
                            standardScore.setT1(t2);
                            standardScore.setT2(t1);
                            if (StringUtils.isNotEmpty(standardScore.getScoresJson())) {
                                JSONObject periodFootballScores = JSONObject.parseObject(standardScore.getScoresJson());
                                Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
                                for (FootballScores value : allPeriodScores.values()) {
                                    value.changeHomeAwayScore();
                                }
                                standardScore.setScoresJson(JSONObject.toJSONString(allPeriodScores));
                            }
                        }
                }
            }
        }catch (Exception e){
            log.error("changeHomeAway error:",e);
        }
    }
    /**
     * 主客队调换
     * @param matchScoresInfo
     */
    public void changeHomeAway(MatchScoresInfo matchScoresInfo,ThirdMatchInfo thirdMatchInfo) {
        //只有足球才做主客队相反
        try {
            if (matchScoresInfo != null && matchScoresInfo.getSportId().equals(1L)) {
                //只有UOF比分才需要主客队互换  事件比分不需要
                if (matchScoresInfo.getDataSourceType().equals(SourceTypeEnum.UOF.getCode().toString())) {
                    if (thirdMatchInfo.getHomeAwayOpposite() != null && 1 == thirdMatchInfo.getHomeAwayOpposite()) {
                        Integer t1 = matchScoresInfo.getT1();
                        Integer t2 = matchScoresInfo.getT2();
                        Integer periodT1 = matchScoresInfo.getPeriodT1();
                        Integer periodT2 = matchScoresInfo.getPeriodT2();
                        matchScoresInfo.setPeriodT1(periodT2);
                        matchScoresInfo.setPeriodT2(periodT1);
                        matchScoresInfo.setT1(t2);
                        matchScoresInfo.setT2(t1);
                        if (StringUtils.isNotEmpty(matchScoresInfo.getScoresJson())) {
                            JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
                            Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
                            for (FootballScores value : allPeriodScores.values()) {
                                value.changeHomeAwayScore();
                            }
                            matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
                        }
                    }
                }
            }
        }catch (Exception e){
            log.error("changeHomeAway error:",e);
//
        }
    }

    /**
     * 创建比分信息-初始化
     * @param data
     * @param thirdMatchInfo
     * @return
     */
    public MatchScoresInfo  createMatchScoresInfo(MatchEventInfo data, ThirdMatchInfo thirdMatchInfo) {
        MatchScoresInfo matchScoresInfo =new  MatchScoresInfo();
        //1. matchScoresInfo 构建入库
        matchScoresInfo.setId(IdWorker.getId());
        matchScoresInfo.setDataSourceCode(data.getDataSourceCode());
        matchScoresInfo.setDataSourceType(data.getSourceType()+"");
        matchScoresInfo.setEventTime(data.getEventTime());
        matchScoresInfo.setMatchLength(thirdMatchInfo.getMatchLength());
        matchScoresInfo.setPeriod(data.getMatchPeriodId());
        matchScoresInfo.setRemainingTime(data.getPeriodRemainingSeconds());
        matchScoresInfo.setSecondsMatchStart(data.getSecondsFromStart());
        matchScoresInfo.setThirdMatchSourceId(data.getThirdMatchSourceId());
        matchScoresInfo.setThirdMatchId(thirdMatchInfo.getId());
        matchScoresInfo.setSportId(data.getSportId());
        matchScoresInfo.setCreateTime(System.currentTimeMillis());
        matchScoresInfo.setModifyTime(matchScoresInfo.getCreateTime());
//        matchScoresInfoMapper.insert(matchScoresInfo);
//        matchScoresInfoRepository.updateScoresInfo(matchScoresInfo);

        return matchScoresInfo;

    }


    public MatchScoresInfo createPDMatchScoresInfo( ThirdMatchInfo thirdMatchInfo)
    {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 根据三方赛事查询对应的标准赛事来确定最后的赛制
        StandardMatchInfo standardMatchInfo = null;
        if (null != thirdMatchInfo && null != thirdMatchInfo.getReferenceId() && thirdMatchInfo.getReferenceId() > 0){
//            standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(thirdMatchInfo.getReferenceId());
            standardMatchInfo = standardMatchInfoRepository.selectStandardMatchPrimaryKey(thirdMatchInfo.getReferenceId());
        }
//        MatchScoresInfoExample matchScoresInfoExample = new MatchScoresInfoExample();
//        matchScoresInfoExample.createCriteria().andThirdMatchIdEqualTo(thirdMatchInfo.getId());
//        List<MatchScoresInfo> virtualRelations = matchScoresInfoMapper.selectByExample(matchScoresInfoExample);
//        if(virtualRelations.size()!=0){
//            return virtualRelations.get(0);
//        }
        MatchScoresInfo virtualRelation = matchScoresInfoRepository.selectByExample(thirdMatchInfo.getId(), SourceTypeEnum.LIVE_DATA.getCode());
        if (!ObjectUtils.isEmpty(virtualRelation)) {
            return virtualRelation;
        }
        if(thirdMatchInfo.getMatchLength()==null){
            thirdMatchInfo.setMatchLength(0);
        }

        Integer matchLength = !Objects.isNull(standardMatchInfo) && null != standardMatchInfo.getMatchLength() ?
                standardMatchInfo.getMatchLength() : thirdMatchInfo.getMatchLength();

        Integer roundType = Objects.isNull(standardMatchInfo) ? thirdMatchInfo.getRoundType() : standardMatchInfo.getRoundType();

        log.info("createPDMatchScoresInfo的初始化, standardMatchInfo是否已创建:{}, matchLength:{}, roundType:{}",
                Objects.isNull(standardMatchInfo), matchLength, roundType);

        MatchScoresInfo matchScoresInfo =new  MatchScoresInfo();
        //1. matchScoresInfo 构建入库
        matchScoresInfo.setId(IdWorker.getId());
        matchScoresInfo.setDataSourceCode(thirdMatchInfo.getDataSourceCode());
        matchScoresInfo.setDataSourceType("1");
        matchScoresInfo.setEventTime(System.currentTimeMillis());
        matchScoresInfo.setMatchLength(matchLength);
        matchScoresInfo.setPeriod(0l);
        matchScoresInfo.setRemainingTime(0l);
        matchScoresInfo.setSecondsMatchStart(0l);
        matchScoresInfo.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
        matchScoresInfo.setThirdMatchId(thirdMatchInfo.getId());
        matchScoresInfo.setSportId(thirdMatchInfo.getSportId());
        matchScoresInfo.setCreateTime(System.currentTimeMillis());
        matchScoresInfo.setModifyTime(matchScoresInfo.getCreateTime());
        matchScoresInfo.setPeriodT1(0);
        matchScoresInfo.setPeriodT2(0);
        matchScoresInfo.setT1(0);
        matchScoresInfo.setT2(0);
        if(thirdMatchInfo.getSportId().equals(2l)){
            Map<Long, BasketballScores> periodFootballScores= new HashMap<>();
            BasketballScores basketballScores=new BasketballScores(0l);
            periodFootballScores.put(WHOLE_MATCH,basketballScores);
            matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodFootballScores));
        } else if(thirdMatchInfo.getSportId().equals(1l)){
            Map<Long, FootballScores> periodFootballScores= new HashMap<>();
            FootballScores basketballScores=new FootballScores(0l);
            periodFootballScores.put(WHOLE_MATCH,basketballScores);
            matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodFootballScores));
        } else if(thirdMatchInfo.getSportId().equals(5l)){
            Map<Long, TennisScores> periodFootballScores = new HashMap<>();
            TennisScores tennisScores = new TennisScores();
            periodFootballScores.put(WHOLE_MATCH,tennisScores);
            matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodFootballScores));
        }
//        matchScoresInfoMapper.insert(matchScoresInfo);
        matchScoresInfoRepository.updateScoresInfo(matchScoresInfo);

//        MatchScoresSourceType matchScoresSourceType = matchScoresSourceTypeMapper.selectByPrimaryKey(matchScoresInfo.getThirdMatchId());
        MatchScoresSourceType matchScoresSourceType = matchScoresSourceTypeRepository.selectSourceSourceTypeByThirdMatchId(matchScoresInfo.getThirdMatchId());
        if(matchScoresSourceType==null){
            //1.创建比分数据源关联表
            matchScoresSourceType =new MatchScoresSourceType();
            matchScoresSourceType.setId(matchScoresInfo.getThirdMatchId());
            matchScoresSourceType.setThirdMatchId(matchScoresInfo.getThirdMatchId());
            matchScoresSourceType.setModifyTime(matchScoresInfo.getModifyTime());
            matchScoresSourceType.setActiveType(1);
            matchScoresSourceType.setActiveMode(1);
            matchScoresSourceType.setCreateTime(matchScoresInfo.getCreateTime());
            matchScoresSourceType.setSourceType(matchScoresInfo.getDataSourceType());
//            matchScoresSourceTypeMapper.insert(matchScoresSourceType);
            matchScoresSourceTypeRepository.insertScoresSourceType(matchScoresSourceType);
        }
        MatchTimeInfo matchTimeInfo = new MatchTimeInfo();
        matchTimeInfo.setCreateTime(System.currentTimeMillis());
        matchTimeInfo.setModifyTime(System.currentTimeMillis());
        matchTimeInfo.setDataSourceType(matchScoresInfo.getDataSourceType());
        matchTimeInfo.setTimeGo(1);
        matchTimeInfo.setThirdMatchId(matchScoresInfo.getThirdMatchId());
        matchTimeInfo.setPeriod(0l);
        matchTimeInfo.setSecondFromStart(0l);
        matchTimeInfo.setId(matchScoresInfo.getId());
        matchTimeInfo.setMatchLength(matchLength);
        matchTimeInfo.setRemainingTime(0l);
        matchTimeInfo.setEventTime(System.currentTimeMillis());
        matchTimeInfo.setRoundType(roundType);
        //如果是 网球 5则需要初始化局制
        if(matchScoresInfo.getSportId().equals(5L)){

            if(matchTimeInfo.getMatchLength()==null|| matchTimeInfo.getMatchLength()==0){
                matchTimeInfo.setMatchLength(1);
            }
            if(matchTimeInfo.getRoundType()==null|| matchTimeInfo.getRoundType()==0){
                matchTimeInfo.setRoundType(3);
            }
            matchTimeInfo.setCurrentRound(1);
            matchTimeInfo.setCurrentSet(1);
            matchTimeInfo.setFirstNum(1);

            JSONObject setRoundJson=new JSONObject();
            JSONObject setMatchLengthJson=new JSONObject();
            for(Integer i=1;i<=matchTimeInfo.getRoundType();i++){
                setRoundJson.put(i.toString(),13);
                setMatchLengthJson.put(i.toString(),2);
            }
            matchTimeInfo.setPeriodLengthJson(setRoundJson.toJSONString());
            matchTimeInfo.setMatchLengthJson(setMatchLengthJson.toJSONString());
        }
//        matchTimeInfoMapper.insert(matchTimeInfo);
        matchTimeInfoRepository.updateByPrimaryKey(matchTimeInfo);
        stopWatch.stop();
        log.info("ScoresServiceImpl-createPDMatchScoresInfo-耗时={}, thirdMatchId={}", stopWatch.getTotalTimeMillis(),thirdMatchInfo.getId());
        return matchScoresInfo;
    }

    @Override
    public MatchScoresInfo selectLiveMatchScoreInfo( Long thirdMatchId) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        MatchScoresInfo matchScoresInfo = matchScoresInfoRepository.selectByExample(thirdMatchId, SourceTypeEnum.LIVE_DATA.getCode());
        // 当存在比分数据时，查询赛事时间表，如该表为空，按比分表数据初始化赛事时间表
        if (!ObjectUtils.isEmpty(matchScoresInfo)) {
            MatchTimeInfo matchTimeInfoOld = matchTimeInfoRepository.selectByPrimaryKey(matchScoresInfo.getId());
            if (ObjectUtils.isEmpty(matchTimeInfoOld)) {
                ThirdMatchInfo thirdMatchInfo = thirdMatchInfoRepository.selectThirdMatchInfoByPrimaryKey(thirdMatchId);
                if (thirdMatchInfo == null) {
                    stopWatch.stop();
                    log.info("ScoresServiceImpl-selectLiveMatchScoreInfo-耗时={},thirdMatchId={}", stopWatch.getTotalTimeMillis(), thirdMatchId);
                    return null;
                }
                StandardMatchInfo standardMatchInfo = null;
                if (null != thirdMatchInfo.getReferenceId() && thirdMatchInfo.getReferenceId() > 0) {
                    standardMatchInfo = standardMatchInfoRepository.selectStandardMatchPrimaryKey(thirdMatchInfo.getReferenceId());
                }
                Integer roundType = Objects.isNull(standardMatchInfo) ? thirdMatchInfo.getRoundType() : standardMatchInfo.getRoundType();
                initMatchTimeInfoByMatchScoresInfo(matchScoresInfo, roundType);
            }
        }
        log.info("::{}::获取到比分：{}", thirdMatchId, JSONObject.toJSONString(matchScoresInfo));
        if (matchScoresInfo == null) {
//            ThirdMatchInfo thirdMatchInfo=scoresRedisHelp.getCatchThirdMatchInfoByPrimaryKey(thirdMatchId);
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoRepository.selectThirdMatchInfoByPrimaryKey(thirdMatchId);

            if(thirdMatchInfo==null){
                stopWatch.stop();
                log.info("ScoresServiceImpl-selectLiveMatchScoreInfo-耗时={},thirdMatchId={}",stopWatch.getTotalTimeMillis(),thirdMatchId);
                return null;
            }
            if(thirdMatchInfo.getSportId().equals(1L)){
                basketBallAdvertiseService.createMatchScoresInfo(thirdMatchInfo,"");
            }
            stopWatch.stop();
            log.info("ScoresServiceImpl-selectLiveMatchScoreInfo-耗时={},thirdMatchId={}",stopWatch.getTotalTimeMillis(),thirdMatchId);
            return createPDMatchScoresInfo(thirdMatchInfo);
        }
        stopWatch.stop();
        log.info("ScoresServiceImpl-selectLiveMatchScoreInfo-耗时={},thirdMatchId={}",stopWatch.getTotalTimeMillis(),thirdMatchId);
        return matchScoresInfo;
    }

    /**
     * 当存在比分数据时，查询赛事时间表，如该表为空，按比分表数据初始化赛事时间表
     *
     * @param matchScoresInfo 比分数据
     * @param roundType       赛制
     */
    @Override
    public void initMatchTimeInfoByMatchScoresInfo(MatchScoresInfo matchScoresInfo, Integer roundType) {
        MatchTimeInfo matchTimeInfo = new MatchTimeInfo();
        matchTimeInfo.setCreateTime(matchScoresInfo.getCreateTime());
        matchTimeInfo.setModifyTime(matchScoresInfo.getModifyTime());
        matchTimeInfo.setDataSourceType(matchScoresInfo.getDataSourceType());
        matchTimeInfo.setTimeGo(1);
        matchTimeInfo.setThirdMatchId(matchScoresInfo.getThirdMatchId());
        matchTimeInfo.setPeriod(matchScoresInfo.getPeriod());
        matchTimeInfo.setSecondFromStart(matchScoresInfo.getSecondsMatchStart());
        matchTimeInfo.setId(matchScoresInfo.getId());
        matchTimeInfo.setMatchLength(matchScoresInfo.getMatchLength());
        matchTimeInfo.setRemainingTime(matchScoresInfo.getRemainingTime());
        matchTimeInfo.setEventTime(matchScoresInfo.getEventTime());
        matchTimeInfo.setRoundType(roundType);
        //如果是 网球 5则需要初始化局制
        if (matchScoresInfo.getSportId().equals(5L)) {

            if (matchTimeInfo.getMatchLength() == null || matchTimeInfo.getMatchLength() == 0) {
                matchTimeInfo.setMatchLength(1);
            }
            if (matchTimeInfo.getRoundType() == null || matchTimeInfo.getRoundType() == 0) {
                matchTimeInfo.setRoundType(3);
            }
            matchTimeInfo.setCurrentRound(1);
            matchTimeInfo.setCurrentSet(1);
            matchTimeInfo.setFirstNum(1);

            JSONObject setRoundJson = new JSONObject();
            JSONObject setMatchLengthJson = new JSONObject();
            for (int i = 1; i <= matchTimeInfo.getRoundType(); i++) {
                setRoundJson.put(Integer.toString(i), 13);
                setMatchLengthJson.put(Integer.toString(i), 2);
            }
            matchTimeInfo.setPeriodLengthJson(setRoundJson.toJSONString());
            matchTimeInfo.setMatchLengthJson(setMatchLengthJson.toJSONString());
        }
        matchTimeInfoRepository.updateByPrimaryKey(matchTimeInfo);
    }

    @Override
    public Long selectAoMatchId(Long standardMatchId) {
        return standardMatchInfoRepository.selectAoMatchId(standardMatchId);
    }


}
