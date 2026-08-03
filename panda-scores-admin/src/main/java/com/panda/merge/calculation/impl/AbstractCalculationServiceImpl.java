package com.panda.merge.calculation.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.calculation.CalculationService;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.FootballBallPeroidEnum;
import com.panda.merge.common.enums.OperateLogTypeEnum;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.SourceTypeEnum;
import com.panda.merge.constant.SportTypeEnum;
import com.panda.merge.dto.*;
import com.panda.merge.dto.resultScore.MatchResultScoreMsgVo;
import com.panda.merge.dto.scores.*;
import com.panda.merge.dto.sourceSwitch.TennisSwitch;
import com.panda.merge.dubbo.ScoresCenterApiImpl;
import com.panda.merge.mapper.*;
import com.panda.merge.model.*;
import com.panda.merge.mq.message.CommonStandardScoresDto;
import com.panda.merge.mq.producer.ScoresProducer;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.repository.ScoresRedisHelp;
import com.panda.merge.repository.StandardMatchInfoRepository;
import com.panda.merge.repository.StandardSportMarketSellRepository;
import com.panda.merge.service.IMatchScoreSearchService;
import com.panda.merge.service.IScoresService;
import com.panda.merge.service.ThirdMatchInfoService;
import com.panda.merge.utils.JsonMapUtils;
import com.panda.merge.utils.MessageBuilderUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.rocketmq.common.ServiceThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

/**
 * 比分中心
 */
@Slf4j
@Service
public class AbstractCalculationServiceImpl implements CalculationService {

    @Autowired
    RedisService redisService;

    @Autowired
    MessageBuilderUtils messageBuilderUtils;
    @Autowired
    ScoresProducer scoresProducer;

    @Autowired
    private MatchScoresCenterLogMapper matchScoresCenterLogMapper;
    @Autowired
    B02ScoresSourceMapper b02ScoresSourceMapper;
    @Autowired
    IMatchScoreSearchService matchScoreSearchService;
    @Autowired
    ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    MatchScoreInfoRepository matchScoreInfoRepository;
    @Autowired
    ScoresRedisHelp scoresRedisHelp;
    @Autowired
    StandardSportMarketSellRepository standardSportMarketSellRepository;
    @Autowired
    StandardMatchInfoRepository standardMatchInfoRepository;
    @Autowired
    IScoresService scoresService;
    @Autowired
    ScoresCenterApiImpl scoresCenterApiImpl;

    @Autowired
    MatchSettleResultMapper matchSettleResultMapper;
    //网球阶段结束
    static List<Long> setEndPeriod = Arrays.asList(301L, 302L, 303L, 304L, 305L, 100L, 999L,95L,96L);

    //需要计算标准比分的阶段
    static List<Long> scoreCenterPeriod = Arrays.asList(8L, 9L, 10L, 11L, 12L, 441L, 442L);

    List<String> N0123_SOURCE_CODE = Arrays.asList("N01","N02","N03","TS");
    /**
     * 计算赛事比分
     *
     * @param matchScoresInfo
     * @param data
     * @throws Exception
     */
    @Override
    public void calculationMatchScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) throws Exception {

    }

    /**
     * 计算赛事比分
     *
     * @param matchScoresInfo
     * @param standardMatchScores
     * @throws Exception
     */
    @Override
    public void calcStandardMatchScores(MatchScoresInfo matchScoresInfo, StandardMatchScores standardMatchScores, MatchEventInfo data) throws Exception {

    }

    /**
     * 取消事件下发
     *
     * @param matchScoresInfo
     * @param data
     * @param isAgain
     * @throws Exception
     */
    @Override
    public void cancelEvent(MatchScoresInfo matchScoresInfo, MatchEventInfo data, boolean isAgain,Boolean isReissue) throws Exception {

    }

    /**
     * 取消事件逻辑
     *
     * @param data
     * @return
     */
    @Override
    public MatchEventInfo getOldMatchInfoByCancel(MatchEventInfo data) {
        String eventKey = "SCORES_DELETE:" + data.getDataSourceCode() + ":" + data.getStandardMatchId() + data.getExtraInfo();
        Object o = redisService.get(eventKey);
        try {
            if (o != null) {
                return JSONObject.toJavaObject(JSONObject.parseObject(o.toString()), MatchEventInfo.class);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error(":处理数据发生异常:", e);
            return null;
        }
    }

    /**
     * 保存赛事统计比分
     *
     * @param matchScoresInfo
     * @param data
     */
    @Override
    public void saveMatchStatisticsScores(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data,StandardMatchInfo standardMatchInfo) {

    }


    //    @Override
    public StandardScoreCenterDTO queryMatchScores(Long standardMatchId) {
        StandardScoreCenterDTO dto = new StandardScoreCenterDTO();
        StandardSportMarketSell match = standardSportMarketSellRepository.selectThirdMatchInfoPrimaryKey(standardMatchId);

        if (match == null) {
            log.info("开售信息不存在");
            return null;
        }
        StandardMatchInfo matchInfo = standardMatchInfoRepository.selectStandardMatchPrimaryKey(standardMatchId);

        dto.setSportId(match.getSportId());
        dto.setStandardMatchId(standardMatchId);
        dto.setMatchManageId(match.getMatchManageId());
        dto.setMatchLength(matchInfo.getMatchLength());
        dto.setBusinessEvent(match.getBusinessEvent());
        dto.setRelatedDataSourceCoderList(matchInfo.getRelatedDataSourceCoderList());
        dto.setPreId(match.getId());

        //查询标准比分
        StandardMatchScores standardMatchScores = scoresRedisHelp.getCatchStandScoreByMatchId(standardMatchId);
        if (standardMatchScores == null) {
            log.info("标准比分不存在");
            return null;
        }
        dto.setShowStatus(standardMatchScores.getShowStatus());
        //获取标准比分
        StandardScoreCenter centerStand = new StandardScoreCenter();
        centerStand.setDataSourceCode("STAND");
        centerStand.setIndex(0);
        centerStand.setStandardMatchId(standardMatchId);
        centerStand.setSportId(matchInfo.getSportId());
        //组装标准比分
        this.buildScore(centerStand, standardMatchScores.getScoreJson(), matchInfo, standardMatchScores.getDataSourceAccoSwitch());

        if (centerStand.getScores() == null || centerStand.getScores().isEmpty()) {
            scoreIsNullExtract(matchInfo, centerStand);
        }
        List<StandardScoreCenter> list = new ArrayList<>();
        list.add(centerStand);

        //查询比分通道
        List<MatchScoresInfo> listScore = new ArrayList<>();
        //获取其他数据源比分
        ThirdMatchInfoExample thirdMatchInfoExample = new ThirdMatchInfoExample();
        thirdMatchInfoExample.createCriteria().andReferenceIdEqualTo(standardMatchId);
        List<ThirdMatchInfo> thirdMatchInfoList = matchScoreSearchService.searchAllThirdMatchInfoByExample(thirdMatchInfoExample);
//        List<ThirdMatchInfo> thirdMatchInfoList = scoresRedisHelp.getCatchThirdMatchInfoListByReferenceId(standardMatchId);
        if (thirdMatchInfoList != null && !thirdMatchInfoList.isEmpty()) {
            for (ThirdMatchInfo thirdMatchInfo : thirdMatchInfoList) {
                if(N0123_SOURCE_CODE.contains(thirdMatchInfo.getDataSourceCode())){
                    continue;
                }
                //B02取对应通道比分 uof
                if (DataSourceCodeEnum.BC.code.equals(thirdMatchInfo.getDataSourceCode())) {
                    Long dataSourceType = matchScoreInfoRepository.checkB02ScoresSource(matchInfo.getSportId());
                    MatchScoresInfo matchScoresInfo = matchScoreInfoRepository.selectByExample(thirdMatchInfo.getId(), dataSourceType == 1 ? 0 : 1);
                    if (matchScoresInfo != null) {
                        listScore.add(matchScoresInfo);
                    }
                } else {
                    //其他数据源默认取实时事件比分 livedata
                    MatchScoresInfo matchScoresInfo = matchScoreInfoRepository.selectByExample(thirdMatchInfo.getId(), SourceTypeEnum.LIVE_DATA.getCode());
                    if (matchScoresInfo != null) {
                        listScore.add(matchScoresInfo);
                    }else{
                        //无事件比分展示统计比分
                        matchScoresInfo = matchScoreInfoRepository.selectByExample(thirdMatchInfo.getId(), SourceTypeEnum.UOF.getCode());
                        if (matchScoresInfo != null) {
                            listScore.add(matchScoresInfo);
                        }
                    }
                }

            }
            if (!listScore.isEmpty()) {
                Map<String, List<MatchScoresInfo>> scoreMaps =
                        listScore.stream().collect(Collectors.groupingBy(MatchScoresInfo::getDataSourceCode, LinkedHashMap::new, Collectors.toList()));
                int index = 1;
                for (Map.Entry<String, List<MatchScoresInfo>> values : scoreMaps.entrySet()) {
                    String dataSourceCode = values.getKey();
                    String scoresJson = values.getValue().get(0).getScoresJson();

                    StandardScoreCenter dataSourceScores = new StandardScoreCenter();
                    dataSourceScores.setDataSourceCode(dataSourceCode);
                    dataSourceScores.setStandardMatchId(standardMatchId);
                    dataSourceScores.setSportId(matchInfo.getSportId());
                    dataSourceScores.setIndex(index++);
                    if (dataSourceCode.equals(match.getBusinessEvent())) {
                        dataSourceScores.setIsMain(true);
                    } else {
                        dataSourceScores.setIsMain(false);
                    }
                    //组装数据源比分
                    this.buildScore(dataSourceScores, scoresJson, matchInfo, null);
                    list.add(dataSourceScores);
                }
            } else {
                log.info("第三方赛事不存在2");
            }
            if (!list.isEmpty()) {
                //排序，保证0-标准比分一直处于第一个
                list.sort(Comparator.comparing((StandardScoreCenter::getIndex)));
                chechScoreIsDifferent(list);
            }

        } else {
            log.info("第三方赛事不存在3");
        }
        dto.setScores(list);
        return dto;
    }

//    public static void main(String[] args) {
//        String scoresJson = "{\"11\":{\"matchScore\":{\"away\":1,\"home\":2},\"breakSuccessCount\":{\"away\":0,\"home\":0},\"scoreNumber\":{\"away\":0,\"home\":0},\"currentScore\":{\"away\":null,\"home\":null},\"breakSuccessRate\":{\"away\":0,\"home\":0},\"servesFaultCount\":{\"away\":0,\"home\":0},\"breakPointCount\":{\"away\":0,\"home\":0},\"doubleFoolScore\":{\"away\":0,\"home\":0},\"setScore\":{\"away\":13,\"home\":16},\"servesScoredCount\":{\"away\":0,\"home\":0},\"qiangScore\":{\"away\":0,\"home\":0}},\"-1\":{\"matchScore\":{\"away\":1,\"home\":2},\"breakSuccessCount\":{\"away\":0,\"home\":0},\"scoreNumber\":{\"away\":0,\"home\":0},\"currentScore\":{\"away\":null,\"home\":null},\"breakSuccessRate\":{\"away\":0,\"home\":0},\"servesFaultCount\":{\"away\":0,\"home\":0},\"breakPointCount\":{\"away\":0,\"home\":0},\"doubleFoolScore\":{\"away\":0,\"home\":0},\"setScore\":{\"away\":13,\"home\":16},\"servesScoredCount\":{\"away\":0,\"home\":0},\"qiangScore\":{\"away\":0,\"home\":0}},\"303\":{\"matchScore\":{\"away\":1,\"home\":2},\"breakSuccessCount\":{\"away\":0,\"home\":0},\"scoreNumber\":{\"away\":0,\"home\":0},\"currentScore\":{\"away\":0,\"home\":0},\"breakSuccessRate\":{\"away\":0,\"home\":0},\"servesFaultCount\":{\"away\":0,\"home\":0},\"breakPointCount\":{\"away\":0,\"home\":0},\"doubleFoolScore\":{\"away\":0,\"home\":0},\"setScore\":{\"away\":17,\"home\":25},\"servesScoredCount\":{\"away\":0,\"home\":0},\"qiangScore\":{\"away\":0,\"home\":0}},\"10\":{\"matchScore\":{\"away\":1,\"home\":2},\"breakSuccessCount\":{\"away\":0,\"home\":0},\"scoreNumber\":{\"away\":0,\"home\":0},\"currentScore\":{\"away\":0,\"home\":0},\"breakSuccessRate\":{\"away\":0,\"home\":0},\"servesFaultCount\":{\"away\":0,\"home\":0},\"breakPointCount\":{\"away\":0,\"home\":0},\"doubleFoolScore\":{\"away\":0,\"home\":0},\"setScore\":{\"away\":17,\"home\":25},\"servesScoredCount\":{\"away\":0,\"home\":0},\"qiangScore\":{\"away\":0,\"home\":0}}}\t";
//        StandardScoreCenter center = new StandardScoreCenter();
//        StandardSportMarketSell match = new StandardSportMarketSell();
//        match.setRoundType(5);
//        match.setSportId(9L);
//        center.setDataSourceCode("STAND");
//        buildScore(center,scoresJson,match,null);
//        System.out.println(JSONUtil.toJsonStr(center));
//
//    }

    public void scoreIsNullExtract(StandardMatchInfo matchInfo, StandardScoreCenter centerStand) {
        List<StandardScoreDTO> listScore = new ArrayList<>();
        int roundType = matchInfo.getRoundType();
        for (int i = 8; i < roundType + 8; i++) {
            StandardScoreDTO scores = new StandardScoreDTO();
            //补齐阶段比分数据，从8开始，避免前端无数据无法修改比分
            scores.setPeriodId(Long.valueOf(i));
            scores.setHome(null);
            scores.setAway(null);
            listScore.add(scores);
        }
        StandardScoreDTO scores0 = new StandardScoreDTO();
        scores0.setPeriodId(0L);
        scores0.setHome(null);
        scores0.setAway(null);
        listScore.add(scores0);
        StandardScoreDTO scores1 = new StandardScoreDTO();
        scores1.setPeriodId(-1L);
        scores1.setHome(null);
        scores1.setAway(null);
        listScore.add(scores1);
        centerStand.setScores(listScore);
    }

    /**
     * 数据源比分对比标准比分，检查差异比分部分
     *
     * @param list
     */
    public static void chechScoreIsDifferent(List<StandardScoreCenter> list) {
        List<StandardScoreDTO> standScoreList = new ArrayList<>();
        for (StandardScoreCenter standardScoreCenter : list) {
            if (standardScoreCenter.getDataSourceCode().equals("STAND")) {
                //获取到标准比分
                standScoreList = standardScoreCenter.getScores();
                break;
            }
        }
        if (null != standScoreList && !standScoreList.isEmpty()) {
            for (StandardScoreDTO standardScoreDTO : standScoreList) {
                //所有比分
                for (StandardScoreCenter allScores : list) {

                    //标准比分不再对比
                    if ("STAND".equals(allScores.getDataSourceCode())) {
                        continue;
                    }
                    if (allScores.getScores() == null || allScores.getScores().isEmpty()) {
                        log.info("AbstractCalculationServiceImpl-chechScoreIsDifferent error: scores is empty");
                        continue;
                    }
                    for (StandardScoreDTO dataSourceScore : allScores.getScores()) {
                        //总比分 总赛局不对比
                        if (dataSourceScore.getPeriodId() == 0L/* || dataSourceScore.getPeriodId() == -1L*/) {
                            continue;
                        }
                        if (allScores.getSportId() != 1) {
                            standardScoreDTO.setIndex(standardScoreDTO.getPeriodId().intValue());
                            if (standardScoreDTO.getPeriodId() == dataSourceScore.getPeriodId()) {
                                String periodScore = dataSourceScore.getHome() + "-" + dataSourceScore.getAway();
                                String standardPeriodScore = standardScoreDTO.getHome() + "-" + standardScoreDTO.getAway();
                                log.info("index:{},比分差异对比{}:数据源比分:{},标准比分:{}", standardScoreDTO.getIndex(), allScores.getStandardMatchId(), periodScore, standardPeriodScore);
                                if (!periodScore.equals(standardPeriodScore)) {
                                    dataSourceScore.setIsDifference(true);
                                    if (!Objects.equals(dataSourceScore.getHome(), standardScoreDTO.getHome())) {
                                        dataSourceScore.setIsDiffHome(true);
                                    } else {
                                        dataSourceScore.setIsDiffHome(false);
                                    }
                                    if (!Objects.equals(dataSourceScore.getAway(), standardScoreDTO.getAway())) {
                                        dataSourceScore.setIsDiffAway(true);
                                    } else {
                                        dataSourceScore.setIsDiffAway(false);
                                    }
                                } else {
                                    dataSourceScore.setIsDifference(false);
                                    dataSourceScore.setIsDiffHome(false);
                                    dataSourceScore.setIsDiffAway(false);
                                }
                            }
                        } else {
                            if (standardScoreDTO.getIndex() == dataSourceScore.getIndex()) {
                                String periodScore = dataSourceScore.getHome() + "-" + dataSourceScore.getAway();
                                String standardPeriodScore = standardScoreDTO.getHome() + "-" + standardScoreDTO.getAway();
                                log.info("index:{},比分差异对比{}:数据源比分:{},标准比分:{}", standardScoreDTO.getIndex(), allScores.getStandardMatchId(), periodScore, standardPeriodScore);
                                if (!periodScore.equals(standardPeriodScore)) {
                                    dataSourceScore.setIsDifference(true);
                                    if (!Objects.equals(dataSourceScore.getHome(), standardScoreDTO.getHome())) {
                                        dataSourceScore.setIsDiffHome(true);
                                    }
                                    if (!Objects.equals(dataSourceScore.getAway(), standardScoreDTO.getAway())) {
                                        dataSourceScore.setIsDiffAway(true);
                                    }
                                } else {
                                    dataSourceScore.setIsDifference(false);
                                    dataSourceScore.setIsDiffHome(false);
                                    dataSourceScore.setIsDiffAway(false);
                                }
                            }
                        }

                    }
                }
            }
        }
    }


    /**
     * 各球种判定胜的得分
     *
     * @param sportId
     * @return
     */
    public static Integer getSportWinScore(Long sportId) {
        Map<Long, Integer> map = new HashMap<>();
        map.put(5L, 6);
        map.put(8L, 11);
        map.put(9L, 25);
        map.put(10L, 21);
        return map.get(sportId) == null ? 0 : map.get(sportId);
    }

    public  void buildScore(StandardScoreCenter center, String scoresJson, StandardMatchInfo match, String sportSwitchs) {
        if (StringUtils.isBlank(scoresJson)) {
            return;
        }
        int roundType = 3;
        if (null != match.getRoundType() && match.getRoundType() != 0) {
            roundType = match.getRoundType();
        }
        String str = sportSwitchs;
        TennisSwitch switchs = new TennisSwitch();
        if (StrUtil.isNotEmpty(str)) {
            switchs = JSONUtil.toBean(str, TennisSwitch.class);
        }
        //标准比分中心页面内容
        JSONObject periodScores = JSONObject.parseObject(scoresJson);
        Map<Long, TennisScores> allPeriodScores = JsonMapUtils.parseTennisMap(periodScores);
        //比分内容
        List<StandardScoreDTO> listScore = new ArrayList<>();
        Integer tgHome = 0;
        Integer tgAway = 0;
        Integer setHome = 0;
        Integer setAway = 0;
        StandardMatchInfo standardMatchInfo = standardMatchInfoRepository.selectStandardMatchPrimaryKey(center.getStandardMatchId());
        int winScore = getSportWinScore(match.getSportId());
        for (Long periodId : allPeriodScores.keySet()) {
            //查询比分时过滤阶段0 -- 脏数据
            if (!scoreCenterPeriod.contains(periodId)) {
                continue;
            }
            TennisScores cc = allPeriodScores.get(periodId);
            StandardScoreDTO scores = new StandardScoreDTO();
            Long showPeriodId = periodId;
            if (periodId == 441L) {
                showPeriodId = 13L;
            }
            if (periodId == 442L) {
                showPeriodId = 14L;
            }

            //展示对应顺序到前端
            scores.setPeriodId(showPeriodId);
            scores.setHome(cc.getSetScore().getHome());
            scores.setAway(cc.getSetScore().getAway());
            scores.setIndex(showPeriodId.intValue());
            //综合球种的比分联动开关
            setScoresSwitch(periodId, scores, switchs);
            listScore.add(scores);
            tgHome += cc.getSetScore().getHome();
            tgAway += cc.getSetScore().getAway();


            //当前事件是盘结束阶段、赛事结束、异常结束时，以比分大小统计盘比分，适用所有球种
            if(setEndPeriod.contains(standardMatchInfo.getMatchPeriodId())){
                if (cc.getSetScore().getHome() > cc.getSetScore().getAway()) {
                    setHome = setHome + 1;
                } else if (cc.getSetScore().getHome() < cc.getSetScore().getAway()){
                    setAway = setAway + 1;
                }
                continue;
            }
            log.info("比分中心查询比分，获取盘比分：{}，{}，{}",standardMatchInfo.getId(), setHome,setAway);
            //网球有抢7，单独判断
            if (SportTypeEnum.TENNIS.getValue().equals(match.getSportId())){
                if (cc.getSetScore().getHome() >= 7 || cc.getSetScore().getAway() >= 7) {
                    //达到抢7,7分为胜
                    if (cc.getSetScore().getHome() > cc.getSetScore().getAway()) {
                        setHome = setHome + 1;
                    } else {
                        setAway = setAway + 1;
                    }
                } else {
                    //未到7分，相差2分为胜，例：6:4主队+1,6:5继续抢7
                    if (cc.getSetScore().getHome() > cc.getSetScore().getAway()) {
                        if (cc.getSetScore().getHome() >= 6 && cc.getSetScore().getHome() - cc.getSetScore().getAway() >= 2) {
                            setHome = setHome + 1;
                        }
                    } else if (cc.getSetScore().getHome() < cc.getSetScore().getAway()) {
                        if (cc.getSetScore().getAway() >= 6 && cc.getSetScore().getAway() - cc.getSetScore().getHome() >= 2) {
                            setAway = setAway + 1;
                        }
                    }
                }
            }else{
                if (cc.getSetScore().getHome() >= winScore + 1 || cc.getSetScore().getAway() >= winScore + 1) {
                    if (cc.getSetScore().getHome() > cc.getSetScore().getAway()) {
                        if (cc.getSetScore().getHome() - cc.getSetScore().getAway() >= 2) {
                            setHome = setHome + 1;
                        }
                    } else {
                        if (cc.getSetScore().getAway() - cc.getSetScore().getHome() >= 2) {
                            setAway = setAway + 1;
                        }
                    }
                } else {
                    if (match.getSportId() == SportTypeEnum.VOLLEYBALL.getValue() && roundType >= 5 && periodId >= 12L) {
                        //针对排球 排球赛制五局三胜，打到第五局的时候，比分 15分且胜2局，不需要达到25分，盘比分计算逻辑还是有点问题呢
                        if (cc.getSetScore().getHome() > cc.getSetScore().getAway() && cc.getSetScore().getHome() >= 15) {
                            if (cc.getSetScore().getHome() - cc.getSetScore().getAway() >= 2) {
                                setHome = setHome + 1;
                            }
                        } else if (cc.getSetScore().getAway() > cc.getSetScore().getHome() && cc.getSetScore().getAway() >= 15) {
                            if (cc.getSetScore().getAway() - cc.getSetScore().getHome() >= 2) {
                                setAway = setAway + 1;
                            }
                        }
                    } else {
                        if (cc.getSetScore().getHome() > cc.getSetScore().getAway()) {
                            if (cc.getSetScore().getHome() >= winScore && cc.getSetScore().getHome() - cc.getSetScore().getAway() >= 2) {
                                setHome = setHome + 1;
                            }
                        } else if (cc.getSetScore().getHome() < cc.getSetScore().getAway()) {
                            if (cc.getSetScore().getAway() >= winScore && cc.getSetScore().getAway() - cc.getSetScore().getHome() >= 2) {
                                setAway = setAway + 1;
                            }
                        }
                    }
                }
            }
        }
        log.info("比分中心查询比分结束，获取盘比分：{}，{}，{}",standardMatchInfo.getId(), setHome,setAway);
        if (!"STAND".equals(center.getDataSourceCode())) {
            TennisScores wholeSores = allPeriodScores.get(WHOLE_MATCH.longValue());
            setHome = wholeSores.getMatchScore().getHome();
            setAway = wholeSores.getMatchScore().getAway();
        }
        StandardScoreDTO scores_one = new StandardScoreDTO();
        scores_one.setPeriodId(0L);
        scores_one.setHome(setHome);
        scores_one.setAway(setAway);
        listScore.add(scores_one);

        StandardScoreDTO scoresZero = new StandardScoreDTO();
        scoresZero.setPeriodId(-1L);
        scoresZero.setHome(tgHome);
        scoresZero.setAway(tgAway);
        listScore.add(scoresZero);


        listScore.sort(Comparator.comparing((StandardScoreDTO::getPeriodId)));

        List<StandardScoreDTO> tempList = getTempList(roundType, switchs);

        Map<Long, StandardScoreDTO> target = new HashMap<>();
        if (CollectionUtils.isNotEmpty(listScore) && CollectionUtils.isNotEmpty(tempList)) {
            for (StandardScoreDTO tempUser : tempList) {
                target.put(tempUser.getPeriodId(), tempUser);
            }
            for (StandardScoreDTO tempUse2 : listScore) {
                Long userId = tempUse2.getPeriodId();
                if (target.containsKey(userId)) {
                    StandardScoreDTO temp = target.get(userId);
                    // 阶段重复，以listScore中的数据为准
                    temp.setPeriodId(tempUse2.getPeriodId());
                    temp.setHome(tempUse2.getHome());
                    temp.setAway(tempUse2.getAway());
                    temp.setSwitchs(tempUse2.getSwitchs());
                    target.put(userId, temp);
                } else {
                    target.put(userId, tempUse2);
                }
            }
        }
        List<StandardScoreDTO> list = new ArrayList<>(target.values());
        if (list.size() - 2 > roundType) {
            for (int i = 0; i <= list.size() - 2 - roundType; i++) {
                list.remove(list.get(list.size() - 1));
            }
        }
        list.sort(Comparator.comparing((StandardScoreDTO::getPeriodId)));
        center.setScores(list);

    }

    /**
     * 综合球种的比分联动开关
     *
     * @param periodId
     * @param scores
     * @param switchs
     */
    private static void setScoresSwitch(Long periodId, StandardScoreDTO scores, TennisSwitch switchs) {
        //开关
        if (periodId == 8) {
            scores.setSwitchs(switchs.getFirstSwitch());
        } else if (periodId == 9L) {
            scores.setSwitchs(switchs.getSecondSwitch());
        } else if (periodId == 10L) {
            scores.setSwitchs(switchs.getThirdSwitch());
        } else if (periodId == 11L) {
            scores.setSwitchs(switchs.getFourSwitch());
        } else if (periodId == 12L) {
            scores.setSwitchs(switchs.getFifSwitch());
        } else if (periodId == 13L || periodId == 441L) {
            scores.setSwitchs(switchs.getSixSwitch());
        } else if (periodId == 14L || periodId == 442L) {
            scores.setSwitchs(switchs.getSevenSwitch());
        } else {
            scores.setSwitchs(1);
        }
    }


    public static List<StandardScoreDTO> getTempList(int roundType, TennisSwitch switchs) {
        List<StandardScoreDTO> tempList = new ArrayList<>();
        for (int i = 8; i < roundType + 8; i++) {
            StandardScoreDTO dto = new StandardScoreDTO();
            dto.setPeriodId(Long.valueOf(i));
            setScoresSwitch(Long.valueOf(i), dto, switchs);
            tempList.add(dto);
        }
        return tempList;
    }

    @Override
    public Response editStandScores(StandardScoreCenter scores, StandardMatchScores standardMatchScores, StandardMatchInfo standardMatchInfo) {
        //查询标准比分
        return Response.success();

    }

    @Override
    public Boolean editAccoSwitch(StandardMatchSwitchDTO matchSwitchDTO, StandardMatchScores standardMatchScores, MatchScoresInfo matchScoresInfo,StandardMatchInfo standardMatchInfo) {
        return true;
    }



    @Override
    public void canceledGoal(MatchScoresInfo matchScoresInfo, MatchEventInfo data) {

    }


    /**
     * 校验比分信息是否为空
     */
    public boolean isAddScores(MatchEventInfo data) {
        if (data.getT1() == null || data.getT2() == null) {
            return true;
        } else {
            return false;
        }
    }



    /**
     * 编辑比分校验
     *
     * @param scores
     * @param matchLength
     * @return
     */
    public static int checkEditScores(StandardScoreCenter scores, Integer matchLength,Long periodId) {
        // -1 和 0不处理，由后台统计
        for (StandardScoreDTO score : scores.getScores()) {
//            if(score.getHome()==null||score.getAway()==null){
//                return OperateLogTypeEnum.EDIT_TIPS_MSG_09.getCode();
//            }
            if ((score.getHome() == null && score.getAway() != null) || (score.getHome() != null && score.getAway() == null)) {
                return OperateLogTypeEnum.EDIT_TIPS_MSG_09.getCode();
            }
        }
        if (SportTypeEnum.BASKETBALL.getValue().equals(scores.getSportId())) {
            List<StandardScoreDTO> otScores = scores.getScores().stream().filter(s -> s.getPeriodId() == 40L).collect(Collectors.toList());
            List<Long> period0 = new ArrayList<>(Arrays.asList(13L, 14L, 15L, 16L));
            if (matchLength == 17) {
                period0 = Arrays.asList(1L, 2L);
            }else if (matchLength==73){
                period0 = Arrays.asList(21L);
            }
            List<Long> finalBasketballScoresPeriods = period0;
            List<StandardScoreDTO> basketball = scores.getScores().stream().filter(s -> finalBasketballScoresPeriods.contains(s.getPeriodId())).collect(Collectors.toList());
            int home = 0, away = 0;
            int allHome = 0, allAway = 0;
            List<StandardScoreDTO> list = scores.getScores();
            for (StandardScoreDTO s : list) {
                if (finalBasketballScoresPeriods.contains(s.getPeriodId())) {
                    if (s.getHome() == null || s.getAway() == null) {
                        continue;
                    }
                    home += s.getHome();
                    away += s.getAway();
                }
            }

            //主客队总分不相等 不能存在加时比分
            if (home != away) {
//                if (!otScores.isEmpty()) {
//                    if ((otScores.get(0).getHome() != null && otScores.get(0).getAway() != null) &&(otScores.get(0).getHome() != 0 && otScores.get(0).getAway() != 0)) {
//                        return OperateLogTypeEnum.EDIT_TIPS_MSG_01.getCode();
//                    }
//                }
            }else{
                for (StandardScoreDTO s : basketball) {
                    if (s.getHome() == null || s.getAway() == null) {
                        continue;
                    }
                    allHome += s.getHome();
                    allAway += s.getAway();
                }
                for (StandardScoreDTO s : otScores) {
                    if (s.getHome() == null || s.getAway() == null) {
                        continue;
                    }
                    allHome += s.getHome();
                    allAway += s.getAway();
                }
                //总分不可相同，请确认后再次输入
                if(Objects.equals(allHome, allAway) && periodId==999L){
                    return OperateLogTypeEnum.EDIT_TIPS_MSG_15.getCode();
                }
            }
        } else if (SportTypeEnum.FOOTBALL.getValue().equals(scores.getSportId())) {
            List<Integer> index4 = new ArrayList<>(Arrays.asList(4,8,12,16));
            List<StandardScoreDTO> otScores = scores.getScores().stream().filter(s -> index4.contains(s.getIndex())).collect(Collectors.toList());
            List<StandardScoreDTO> goalScore = scores.getScores().stream().filter(s -> s.getIndex() == 13 || s.getIndex() == 14).collect(Collectors.toList());
            List<StandardScoreDTO> penaltyScores = scores.getScores().stream().filter(s -> s.getIndex() == 18).collect(Collectors.toList());
            List<StandardScoreDTO> redCardScores = scores.getScores().stream().filter(s -> s.getIndex() == 5 || s.getIndex() == 6 || s.getIndex() == 8).collect(Collectors.toList());
            List<StandardScoreDTO> yellowCardScores = scores.getScores().stream().filter(s -> s.getIndex() == 1 || s.getIndex() == 2 || s.getIndex() == 4).collect(Collectors.toList());

            Integer home = 0, away = 0;
            Integer othome = 0, otaway = 0;
            if (!goalScore.isEmpty()) {
                for (StandardScoreDTO goal : goalScore) {
                    if (goal.getHome() == null && goal.getAway() == null) {
                        continue;
                    }
                    home += goal.getHome();
                    away += goal.getAway();
                }
            }
            //进球总分不一致
            if (!Objects.equals(home, away)) {
//                for (StandardScoreDTO s : otScores) {
//                    //存在加时赛比分，则不能编辑
//                    if (s.getHome() != null || s.getAway() != null) {
//                        log.info("足球编辑比分校验不通过，id={},s:{}", scores.getStandardMatchId(), s);
//                        return OperateLogTypeEnum.EDIT_TIPS_MSG_01.getCode();
//                    }
//                }
            } else {
                if(otScores.size() == 4){
                    othome = otScores.get(3).getHome();
                    otaway = otScores.get(3).getAway();
                }
                if (othome != null && otaway != null) {
                    //加时比分与全场比分不一致
                    if ((othome + home) != (otaway + away)) {
                        if(!penaltyScores.isEmpty()){
                            //并且存在点球大战比分,则提示错误 编辑校验不通过
                            if (penaltyScores.get(0).getHome() != null || penaltyScores.get(0).getAway() != null) {
                                return OperateLogTypeEnum.EDIT_TIPS_MSG_06.getCode();
                            }
                        }
                    }
                }

            }
            if(!penaltyScores.isEmpty()){
                StandardScoreDTO penalty = penaltyScores.get(0);
                if(penalty!=null && penalty.getHome()!=null && penalty.getAway()!=null){
                    if (penalty.getHome() != 0 || penalty.getAway() != 0) {
                        if(periodId!=null && periodId==999L){
                            //不能相差2分以上
                            if ((penalty.getHome() - penalty.getAway() >= 2) || (penalty.getAway() - penalty.getHome() >= 2)) {
                                return OperateLogTypeEnum.EDIT_TIPS_MSG_11.getCode();
                            }
                            //不能相等
                            if (penalty.getHome().equals(penalty.getAway())) {
                                return OperateLogTypeEnum.EDIT_TIPS_MSG_17.getCode();
                            }
                        }
                    }
                }
            }
//            Integer redHome = 0, redAway = 0;
//            for (StandardScoreDTO redCard : redCardScores) {
//                //红牌一场最多只会有11张
//                if (redCard.getHome() == null && redCard.getAway() == null) {
//                    continue;
//                }
//                redHome += redCard.getHome();
//                redAway += redCard.getAway();
//            }
//            if (redHome > 11 || redAway > 11) {
//                return OperateLogTypeEnum.EDIT_TIPS_MSG_04.getCode();
//            }
//            Integer yellowHome = 0, yellowAway = 0;
//            for (StandardScoreDTO yellowCard : yellowCardScores) {
//                //红牌一场最多只会有11张
//                if (yellowCard.getHome() == null && yellowCard.getAway() == null) {
//                    continue;
//                }
//                yellowHome += yellowCard.getHome();
//                yellowAway += yellowCard.getAway();
//            }
//            if (yellowHome > 27 || yellowAway > 27) {
//                return OperateLogTypeEnum.EDIT_TIPS_MSG_05.getCode();
//            }
        } else if (SportTypeEnum.TABLE_TENNIS.getValue().equals(scores.getSportId())) {
            if (!checkSportMatchPoint(scores, 10)) {
                return OperateLogTypeEnum.EDIT_TIPS_MSG_03.getCode();
            }
        } else if (SportTypeEnum.VOLLEYBALL.getValue().equals(scores.getSportId())) {
            //如前四局比分为24：24（第五局为14：14），则需持续比赛制某一方大于两分，故如果分数除差距为2分以内，编辑大于25分（第五局大于15分），储存时需回报错误
            for (StandardScoreDTO s : scores.getScores()) {
                if (s.getHome() == null || s.getAway() == null) {
                    continue;
                }
                if (s.getPeriodId() == 12) {
                    if (s.getHome() > 14 && s.getAway() > 14) {
                        if (!(s.getHome() - s.getAway() >= 2) && !(s.getAway() - s.getHome() >= 2)) {
                            return OperateLogTypeEnum.EDIT_TIPS_MSG_03.getCode();
                        }
                    }
                } else {
                    if (s.getHome() > 24 && s.getAway() > 24) {
                        if (!(s.getHome() - s.getAway() >= 2) && !(s.getAway() - s.getHome() >= 2)) {
                            return OperateLogTypeEnum.EDIT_TIPS_MSG_03.getCode();
                        }
                    }
                }

            }
        } else if (SportTypeEnum.TENNIS.getValue().equals(scores.getSportId())) {
//            if (!checkSportMatchPoint(scores, 5)) {
//                return OperateLogTypeEnum.EDIT_TIPS_MSG_13.getCode();
//            }
//            List<Long> scoreCenterPeriod = Arrays.asList(8L, 9L, 10L, 11L, 12L, 441L, 442L,999L);
            for (StandardScoreDTO s : scores.getScores()) {
                if(s.getHome()==null && s.getAway()==null){
                    continue;
                }
                if ((s.getHome() == 7 && (s.getAway() !=5 && s.getAway() !=6)) ||
                        s.getAway() == 7 && (s.getHome() !=5 && s.getHome() !=6)) {
                    return OperateLogTypeEnum.EDIT_TIPS_MSG_03.getCode();
                }
            }
        } else if (SportTypeEnum.BADMINTON.getValue().equals(scores.getSportId())) {
            for (StandardScoreDTO s : scores.getScores()) {
                if(s.getHome()==null || s.getAway()==null){
                    continue;
                }
                if(s.getHome()>=30 && s.getAway()>=30){
                    return OperateLogTypeEnum.EDIT_TIPS_MSG_14.getCode();
                }if(s.getHome()>30 || s.getAway()>30){
                    return OperateLogTypeEnum.EDIT_TIPS_MSG_14.getCode();
                } else{
                    if ((s.getHome() >= 20 && s.getAway() >= 20) && (s.getHome() < 30 && s.getAway() < 30)) {
                        if (!(s.getHome() - s.getAway() >= 2) && !(s.getAway() - s.getHome() >= 2)) {
                            return OperateLogTypeEnum.EDIT_TIPS_MSG_03.getCode();
                        }
                    }
                }
                if(periodId!=null && periodId==999L){
                    int ret = s.getHome()>s.getAway()?s.getHome()-s.getAway():s.getAway()-s.getHome();
                    if (s.getHome() < 21 && s.getAway() < 21) {
                        //"局任一队伍比分要≥21";
                        return OperateLogTypeEnum.EDIT_TIPS_MSG_13.getCode();
                    }else {
                        if(s.getHome() >= 20 && s.getAway() >= 20){
                            //分且两队分差不超过2分
                            if(ret!=2){
                                return OperateLogTypeEnum.EDIT_TIPS_MSG_03.getCode();
                            }
                        }

                    }
                }
            }
        }
        return 0;
    }

    private static boolean checkSportMatchPoint(StandardScoreCenter scores, int i) {
        for (StandardScoreDTO s : scores.getScores()) {
            if(s.getHome()==null && s.getAway()==null){
                continue;
            }
            if (s.getHome() > i && s.getAway() > i) {
                if (!(s.getHome() - s.getAway() >= 2) && !(s.getAway() - s.getHome() >= 2)) {
                   return false;
                }
            }
        }
        return true;
    }

    /**
     * 保存和发送标准比分
     *
     * @param standardMatchScores
     * @param standardMatchInfo
     */
    protected void updateEndSendScoresInfo(StandardMatchScores standardMatchScores, StandardMatchInfo standardMatchInfo) {
        standardMatchScores.setUpdateTime(System.currentTimeMillis());
        scoresRedisHelp.saveCatchStandScore(standardMatchScores);
        MatchEventInfo eventInfo = new MatchEventInfo();
        eventInfo.setLinkId(standardMatchScores.getMatchId() + "");
        eventInfo.setStandardMatchId(standardMatchScores.getMatchId());
        eventInfo.setSportId(standardMatchScores.getSportId());
        eventInfo.setMatchPeriodId(standardMatchInfo.getMatchPeriodId());
        MatchScoresInfo matchScoresInfoObj = new MatchScoresInfo();
        CommonStandardScoresDto commonScoresDto = messageBuilderUtils.buildStandardMatchScoreCommonScoresDto(standardMatchScores, eventInfo, matchScoresInfoObj);
        commonScoresDto.setDataFrom("1");
        commonScoresDto.setEventSourceType(1);
        //102430 【生产】【产品】赛事事件停止下发时比分中心编辑比分下发至注单比分
        if(SportTypeEnum.FOOTBALL.getValue().equals(standardMatchInfo.getSportId())
            || SportTypeEnum.BASKETBALL.getValue().equals(standardMatchInfo.getSportId())){
            //足篮正常下发比分
            scoresProducer.sendStandardMatchScores(commonScoresDto);
        }else{
            CalculationService calculationService = scoresService.getCalculationService(standardMatchInfo.getSportId());
            if(calculationService!=null){
                //其他综合球种编辑比分中心下发比分修正，与原事件审核编辑比分格式一致。 TOPIC:MATCH_RESULT_SCORE
                MatchResultScoreMsgVo matchResultScoreVo = calculationService.getSportMatchResultScores(standardMatchInfo,commonScoresDto.getScores());
                log.info("比分修正下发获取到比分信息：{}",matchResultScoreVo);
                scoresProducer.sendMatchResultScores(matchResultScoreVo);
            }

        }
        //发送pls标准比分
        if(standardMatchInfo.getPlsStandardMatchId()!=null && standardMatchInfo.getPlsStandardMatchId()!=0){
            //1.发送 标准比分
            CommonStandardScoresDto plsCommonScoresDto = commonScoresDto;
            plsCommonScoresDto.setPlsStandardTournamentId(standardMatchInfo.getStandardTournamentId());
            plsCommonScoresDto.setPlsStandardMatchId(standardMatchInfo.getPlsStandardMatchId());
            plsCommonScoresDto.setMatchStatus(standardMatchInfo.getMatchStatus());
            scoresProducer.sendPlsScores(plsCommonScoresDto);
        }
    }

    @Override
    public MatchResultScoreMsgVo getSportMatchResultScores(StandardMatchInfo standardMatchInfo, Map scores) {
        // 比分,格式：["S1|0:2","S120|9:11","S121|9:11","S122|5:3"]
        List<String> score = new ArrayList<>();
        if(scores==null){
            log.info("比分修正下发：无比分:{}",standardMatchInfo.getId());
            return null;
        }
        JSONObject periodFootballScores = JSONObject.parseObject(JSON.toJSONString(scores));
        Map<Long, TennisScores> allPeriodScores= JsonMapUtils.parseTennisMap(periodFootballScores);
        for (Long periodId : allPeriodScores.keySet()) {
            if (!scoreCenterPeriod.contains(periodId)) {
                continue;
            }
            if(allPeriodScores.get(periodId)!=null){
                log.info("{}，比分修正下发，组装阶段比分：{}",standardMatchInfo.getId(),allPeriodScores.get(periodId).getMatchScore().doScoreStr());
                score.add(getScoreCode(periodId) + "|" + allPeriodScores.get(periodId).getSetScore().doScoreStr());
            }
        }
        score.add("S1" + "|" + allPeriodScores.get(-1L).getMatchScore().doScoreStr());
        score.add("S115" + "|" + allPeriodScores.get(-1L).getSetScore().doScoreStr());
        MatchResultScoreMsgVo msgVo = new MatchResultScoreMsgVo();
        msgVo.setSportId(standardMatchInfo.getSportId());
        msgVo.setMatchId(standardMatchInfo.getId());
        msgVo.setModifyTime(System.currentTimeMillis());
        msgVo.setScore(score);
        return msgVo;
    }

    /**
     * 综合球种比分编码匹配
     * @param periodId
     * @return
     */
    private String getScoreCode(Long periodId) {
        String code = "";
        if(periodId==8L){
            code = "S120";
        } else if (periodId==9L) {
            code = "S121";
        } else if (periodId==10L) {
            code = "S122";
        } else if (periodId==11L) {
            code = "S123";
        } else if (periodId==12L) {
            code = "S124";
        } else if (periodId==441L) {
            code = "S125";
        } else if (periodId==442L) {
            code = "S126";
        }
        return code;
    }

    public void editScoreCenterSettleLog(String oldScoresJson, StandardMatchScores standardMatchScores, StandardScoreCenter scores, Integer matchLength) {
        String forwText = "";
        if (StringUtils.isNotBlank(oldScoresJson)) {
            JSONObject oldPeriodScores = JSONObject.parseObject(oldScoresJson);
            forwText = getString(forwText, oldPeriodScores, standardMatchScores.getSportId(), matchLength);
        } else {
            forwText += "-";
        }
        String rearText = "";
        JSONObject newAllPeriodScores = JSONObject.parseObject(standardMatchScores.getScoreJson());
        rearText = getString(rearText, newAllPeriodScores, standardMatchScores.getSportId(), matchLength);
        MatchScoresCenterLog matchScoresCenterLog = new MatchScoresCenterLog();
        String matchManageId = standardMatchScores.getMatchManageId();
        if (StringUtils.isBlank(matchManageId)) {
            matchManageId = standardMatchScores.getMatchId() + "";
        }
        matchScoresCenterLog.setMatchManageId(matchManageId);
        matchScoresCenterLog.setOperateId(matchManageId);
        matchScoresCenterLog.setOperateName("");
        matchScoresCenterLog.setOperateParaName(OperateLogTypeEnum.SCORES_CENTER_MANUAL_SCORE.getCode() + "");
        if(SportTypeEnum.FOOTBALL.getValue().equals(standardMatchScores.getSportId()) ||
           SportTypeEnum.BASKETBALL.getValue().equals(standardMatchScores.getSportId())){
            String operateName = getOperateParaName(scores,matchLength);
            matchScoresCenterLog.setOperateParaName(operateName);
        }
        matchScoresCenterLog.setOperateType(OperateLogTypeEnum.SCORES_CENTER_MANUAL_SCORE.getCode() + "");
        matchScoresCenterLog.setOperateForwText(forwText);
        matchScoresCenterLog.setOperateRearText(rearText);
        if(SportTypeEnum.FOOTBALL.getValue().equals(standardMatchScores.getSportId())){
            matchScoresCenterLog.setOperateForwText(getFootballString(oldScoresJson));
            matchScoresCenterLog.setOperateRearText(getFootballString(standardMatchScores.getScoreJson()));
        }
        matchScoresCenterLog.setMatchManageId(matchManageId);
        matchScoresCenterLog.setOperateModule(OperateLogTypeEnum.SCORES_SETTLE_10038.getCode() + "");
        matchScoresCenterLog.setOperateUserName(scores.getUserName());
        matchScoresCenterLog.setIpAddress(scores.getIpAddress());
        matchScoresCenterLog.setOperateMatchId(matchManageId);
        matchScoresCenterLog.setCreateTime(System.currentTimeMillis());
        matchScoresCenterLog.setModifyTime(System.currentTimeMillis());
        matchScoresCenterLogMapper.insert(matchScoresCenterLog);
    }

    private static String getOperateParaName(StandardScoreCenter scores,Integer matchLength) {
        StringBuilder str = new StringBuilder("");
        if(SportTypeEnum.BASKETBALL.getValue().equals(scores.getSportId())){
            List<Long> periodList = Arrays.asList(13L, 14L, 15L, 16L,40L);
            if(matchLength==17){
                periodList = Arrays.asList(1L, 2L,40L);
            }else if (matchLength==73){
                periodList = Arrays.asList(21L);
            }
            for (StandardScoreDTO s : scores.getScores()){
                if(periodList.contains(s.getPeriodId())){
                    if(s.getHome()==null || s.getAway()==null){
                        continue;
                    }
                    str.append(s.getPeriodId());
                    //换行符
                    str.append("|");
                }
            }
        }else if (SportTypeEnum.FOOTBALL.getValue().equals(scores.getSportId())){
            //足球按照栏位展示顺序
            for(int i = 1; i <= 18; i++) {
                int finalI = i;
                //不存全场阶段的日志
                if(finalI==3 || finalI==7 || finalI==11 || finalI==15){
                    continue;
                }
                List<StandardScoreDTO> allScores = scores.getScores().stream().filter(s -> s.getIndex() == finalI).collect(Collectors.toList());
                if(!allScores.isEmpty()){
                    if(allScores.get(0).getHome()==null || allScores.get(0).getAway()==null){
                        continue;
                    }
                }
                str.append(i).append("|");
            }
            if(!scores.getMinute15Scores().isEmpty()){
//                List<Long> allScores = scores.getMinute15Scores().stream().map(StandardScoresDetailDTO::getPeriodId).collect(Collectors.toList());
                //前端传15分钟比分必然会全部传，所以无需再次获取和校验
                List<Long> allScores = Arrays.asList(60899L,61799L,62699L,73599L,74499L,75399L);
                for(Long period : allScores){
                    if(period==60899L){
                        str.append(FootballBallPeroidEnum.MIN_0_15_YELLOW_CARD.getCode()).append("|");
                        str.append(FootballBallPeroidEnum.MIN_0_15_RED_CARD.getCode()).append("|");
                        str.append(FootballBallPeroidEnum.MIN_0_15_CORNER.getCode()).append("|");
                        str.append(FootballBallPeroidEnum.MIN_0_15_GOAL.getCode()).append("|");
                    }else if(period==61799L){
                        str.append(FootballBallPeroidEnum.MIN_15_30_YELLOW_CARD.getCode()).append("|");
                        str.append(FootballBallPeroidEnum.MIN_15_30_RED_CARD.getCode()).append("|");
                        str.append(FootballBallPeroidEnum.MIN_15_30_CORNER.getCode()).append("|");
                        str.append(FootballBallPeroidEnum.MIN_15_30_GOAL.getCode()).append("|");
                    }else if(period==62699L){
                        str.append(FootballBallPeroidEnum.MIN_30_45_YELLOW_CARD.getCode()).append("|");
                        str.append(FootballBallPeroidEnum.MIN_30_45_RED_CARD.getCode()).append("|");
                        str.append(FootballBallPeroidEnum.MIN_30_45_CORNER.getCode()).append("|");
                        str.append(FootballBallPeroidEnum.MIN_30_45_GOAL.getCode()).append("|");
                    }else if(period==73599L){
                        str.append(FootballBallPeroidEnum.MIN_45_60_YELLOW_CARD.getCode()).append("|");
                        str.append(FootballBallPeroidEnum.MIN_45_60_RED_CARD.getCode()).append("|");
                        str.append(FootballBallPeroidEnum.MIN_45_60_CORNER.getCode()).append("|");
                        str.append(FootballBallPeroidEnum.MIN_45_60_GOAL.getCode()).append("|");
                    }else if(period==74499L){
                        str.append(FootballBallPeroidEnum.MIN_60_75_YELLOW_CARD.getCode()).append("|");
                        str.append(FootballBallPeroidEnum.MIN_60_75_RED_CARD.getCode()).append("|");
                        str.append(FootballBallPeroidEnum.MIN_60_75_CORNER.getCode()).append("|");
                        str.append(FootballBallPeroidEnum.MIN_60_75_GOAL.getCode()).append("|");
                    }else if(period==75399L){
                        str.append(FootballBallPeroidEnum.MIN_75_90_YELLOW_CARD.getCode()).append("|");
                        str.append(FootballBallPeroidEnum.MIN_75_90_RED_CARD.getCode()).append("|");
                        str.append(FootballBallPeroidEnum.MIN_75_90_CORNER.getCode()).append("|");
                        str.append(FootballBallPeroidEnum.MIN_75_90_GOAL.getCode()).append("|");
                    }
                }
            }

        }

        return str.toString();
    }

    private String getString(String text, JSONObject scoresJson, Long sportId, Integer matchLength) {
        StringBuilder textBuilder = new StringBuilder(text);
        if (SportTypeEnum.BASKETBALL.getValue().equals(sportId)) {
            Map<Long, BasketballScores> allPeriodScores = JsonMapUtils.parseBasketballMap(scoresJson);
            List<Long> periodList = Arrays.asList(13L, 14L, 15L, 16L,40L);
            if(matchLength==17){
                periodList = Arrays.asList(1L, 2L,40L);
            }else if (matchLength==73){
                periodList = Arrays.asList(21L);
            }
            for(Long periodId : periodList){
                for (Long scorePeriod : allPeriodScores.keySet()) {
                    if(!Objects.equals(periodId, scorePeriod)){
                        continue;
                    }
                    BasketballScores score = allPeriodScores.get(scorePeriod);
//                    textBuilder.append("{").append(score.getMatchScore().doCountScoreStr()).append("}");
                    textBuilder.append(score.getMatchScore().doCountScoreStr()).append(" | ");
                }

            }
        } else {
            Map<Long, TennisScores> allPeriodScores = JsonMapUtils.parseTennisMap(scoresJson);
            for(Long periodId : scoreCenterPeriod){
                for (Long scorePeriod : allPeriodScores.keySet()) {
                    if(!Objects.equals(periodId, scorePeriod)){
                        continue;
                    }
                    TennisScores cc = allPeriodScores.get(periodId);
//                    textBuilder.append("{").append(cc.getSetScore().doCountScoreStr()).append("}");
                    textBuilder.append(cc.getSetScore().doCountScoreStr()).append(" | ");
                }
            }
        }
        text = textBuilder.toString();
        return text;
    }


    private static String getFootballString(String scoresJson) {
        JSONObject periodScores = JSONObject.parseObject(scoresJson);
        StringBuilder textBuilder = new StringBuilder();
        Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodScores);
        Map<Integer,CommonItem> footballScoresMap = new HashMap<>();
        FootballScores hfs = allPeriodScores.get(6L);
        if(hfs!=null){
            footballScoresMap.put(1,hfs.getYellowCard());
            footballScoresMap.put(5,hfs.getRedCard());
            footballScoresMap.put(9,hfs.getCorner());
            footballScoresMap.put(13,hfs.getGoal());
        }
        FootballScores fts = allPeriodScores.get(7L);
        if(fts!=null){
            footballScoresMap.put(2,fts.getYellowCard());
            footballScoresMap.put(6,fts.getRedCard());
            footballScoresMap.put(10,fts.getCorner());
            footballScoresMap.put(14,fts.getGoal());
        }
        FootballScores ots = allPeriodScores.get(110L);
        if(ots!=null){
            footballScoresMap.put(4,ots.getYellowCard());
            footballScoresMap.put(8,ots.getRedCard());
            footballScoresMap.put(12,ots.getCorner());
            footballScoresMap.put(16,ots.getGoal());
        }
        FootballScores pas = allPeriodScores.get(-1L);
        if(pas!=null){
            footballScoresMap.put(17,pas.getPenaltyAwarded());
        }
        FootballScores ps = allPeriodScores.get(50L);
        if(ps!=null){
            footballScoresMap.put(18,ps.getGoal());
        }
        for(int i=1;i<=18;i++){
            for (Integer index : footballScoresMap.keySet()) {
                if(!Objects.equals(i, index)){
                    continue;
                }
                CommonItem cc = footballScoresMap.get(index);
                textBuilder.append(cc.doCountScoreStr()).append(" | ");
            }
        }
        if(allPeriodScores.get(60899L)!=null){
            textBuilder.append(allPeriodScores.get(60899L).getYellowCard().doCountScoreStr()).append("|");
            textBuilder.append(allPeriodScores.get(60899L).getRedCard().doCountScoreStr()).append("|");
            textBuilder.append(allPeriodScores.get(60899L).getCorner().doCountScoreStr()).append("|");
            textBuilder.append(allPeriodScores.get(60899L).getGoal().doCountScoreStr()).append("|");
        }
        if(allPeriodScores.get(61799L)!=null){
            textBuilder.append(allPeriodScores.get(61799L).getYellowCard().doCountScoreStr()).append("|");
            textBuilder.append(allPeriodScores.get(61799L).getRedCard().doCountScoreStr()).append("|");
            textBuilder.append(allPeriodScores.get(61799L).getCorner().doCountScoreStr()).append("|");
            textBuilder.append(allPeriodScores.get(61799L).getGoal().doCountScoreStr()).append("|");
        }
        if(allPeriodScores.get(62699L)!=null){
            textBuilder.append(allPeriodScores.get(62699L).getYellowCard().doCountScoreStr()).append("|");
            textBuilder.append(allPeriodScores.get(62699L).getRedCard().doCountScoreStr()).append("|");
            textBuilder.append(allPeriodScores.get(62699L).getCorner().doCountScoreStr()).append("|");
            textBuilder.append(allPeriodScores.get(62699L).getGoal().doCountScoreStr()).append("|");
        }
        if(allPeriodScores.get(73599L)!=null){
            textBuilder.append(allPeriodScores.get(73599L).getYellowCard().doCountScoreStr()).append("|");
            textBuilder.append(allPeriodScores.get(73599L).getRedCard().doCountScoreStr()).append("|");
            textBuilder.append(allPeriodScores.get(73599L).getCorner().doCountScoreStr()).append("|");
            textBuilder.append(allPeriodScores.get(73599L).getGoal().doCountScoreStr()).append("|");
        }
        if(allPeriodScores.get(74499L)!=null){
            textBuilder.append(allPeriodScores.get(74499L).getYellowCard().doCountScoreStr()).append("|");
            textBuilder.append(allPeriodScores.get(74499L).getRedCard().doCountScoreStr()).append("|");
            textBuilder.append(allPeriodScores.get(74499L).getCorner().doCountScoreStr()).append("|");
            textBuilder.append(allPeriodScores.get(74499L).getGoal().doCountScoreStr()).append("|");
        }
        if(allPeriodScores.get(75399L)!=null){
            textBuilder.append(allPeriodScores.get(75399L).getYellowCard().doCountScoreStr()).append("|");
            textBuilder.append(allPeriodScores.get(75399L).getRedCard().doCountScoreStr()).append("|");
            textBuilder.append(allPeriodScores.get(75399L).getCorner().doCountScoreStr()).append("|");
            textBuilder.append(allPeriodScores.get(75399L).getGoal().doCountScoreStr()).append("|");
        }


        return textBuilder.toString();
    }


    /**
     * 综合球种获取开关字符串
     *
     * @param matchSwitchDTO
     * @param standardMatchScores
     * @return
     */
    protected String getSportSwitchByPeriod(StandardMatchSwitchDTO matchSwitchDTO, StandardMatchScores standardMatchScores) {
        log.info("修改开关联动同步比分 matchSwitchDTO:{}==========standardMatchScores:{} ", matchSwitchDTO, standardMatchScores);
        TennisSwitch switchs = new TennisSwitch();
        if (StrUtil.isNotEmpty(standardMatchScores.getDataSourceAccoSwitch())) {
            switchs = JSON.parseObject(standardMatchScores.getDataSourceAccoSwitch(), TennisSwitch.class);
        }
        int status = matchSwitchDTO.getStatus();
        if (matchSwitchDTO.getIndex() == 8) {
            switchs.setFirstSwitch(status);
        } else if (matchSwitchDTO.getIndex() == 9) {
            switchs.setSecondSwitch(status);
        } else if (matchSwitchDTO.getIndex() == 10) {
            switchs.setThirdSwitch(status);
        } else if (matchSwitchDTO.getIndex() == 11) {
            switchs.setFourSwitch(status);
        } else if (matchSwitchDTO.getIndex() == 12) {
            switchs.setFifSwitch(status);
        } else if (matchSwitchDTO.getIndex() == 441 || matchSwitchDTO.getIndex() == 13) {
            switchs.setSixSwitch(status);
        } else if (matchSwitchDTO.getIndex() == 442 || matchSwitchDTO.getIndex() == 14) {
            switchs.setSevenSwitch(status);
        }
        log.info("修改联动开关:{}", switchs);
        return JSON.toJSONString(switchs);
    }

    protected StandardMatchSwitchDTO setSwitchObj(StandardMatchScores standardMatchScores, StandardScoreCenter scores) {
        StandardMatchSwitchDTO switchDTO = new StandardMatchSwitchDTO();
        switchDTO.setSportId(standardMatchScores.getSportId());
        switchDTO.setMatchId(standardMatchScores.getMatchId());
        switchDTO.setStatus(0);
        switchDTO.setUserId(scores.getUserId());
        switchDTO.setUserName(scores.getUserName());
        switchDTO.setIpAddress(scores.getIpAddress());
        return switchDTO;
    }
}
