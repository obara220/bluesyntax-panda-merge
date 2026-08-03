package com.panda.merge.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.cache.CommonItem;
import com.panda.merge.constant.SourceTypeEnum;
import com.panda.merge.dto.FootballScores;
import com.panda.merge.dto.scores.MatchScoresBetterDto;
import com.panda.merge.dto.scores.MatchScoresRequestDTO;
import com.panda.merge.dto.scores.MatchScoresStatusDto;
import com.panda.merge.dto.scores.PdOneInfo;
import com.panda.merge.dto.scores.PdTwoInfo;
import com.panda.merge.mapper.MatchScoresSearchMapper;
import com.panda.merge.mapper.PlsThirdMatchRelationMapper;
import com.panda.merge.mapper.StandardSportMarketSellMapper;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.utils.JsonMapUtils;
import com.panda.sports.auth.rpc.IAuthRequiredPermission;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.DataSourceConstant.WHOLE_MATCH;

@Service
@Slf4j
//@CacheConfig(cacheNames = "GET:SCORE")
public class ScoreSearchServiceImpl {

    @Autowired
    MatchScoresSearchMapper matchScoresSearchMapper;

    @Autowired
    private StandardSportMarketSellMapper standardSportMarketSellMapper;

    @Autowired
    private ThirdMatchInfoMapper thirdMatchInfoMapper;

    @DubboReference(check = false)
    private IAuthRequiredPermission iAuthRequiredPermission;

    @Autowired
    private PlsThirdMatchRelationMapper plsThirdMatchRelationMapper;
    /**
     * 查询赛事比分列表
     * @param matchIds
     * @return
     */
//    @Cacheable(key = "#key",unless = "#result==null", sync = false)
    public List<MatchScoresBetterDto> searchBetterListMatchScores(String key,List<MatchScoresRequestDTO> matchIds) {

        List<Long> standardIdList = new ArrayList<>();
        List<Long> thirdIdList = new ArrayList<>();
        List<Long> standardThirdIdList = new ArrayList<>();
        List<String> standardScoreIds = new ArrayList<>();
        for (MatchScoresRequestDTO matchId : matchIds) {
            if (matchId.isStandard()) {
                standardIdList.add(matchId.getMatchId());
            } else {
                thirdIdList.add(matchId.getMatchId());
            }
        }
        List<MatchScoresStatusDto> matchScoresStatusDtos = null;
        List<MatchScoresBetterDto> scores = new ArrayList<>();
        Map<String,MatchScoresBetterDto> matchScoresBetterDtoMap =new HashMap<>();
        try {
            /**开售的标准赛事查询*/
            if(standardIdList.size()!=0){
                matchScoresStatusDtos = matchScoresSearchMapper.searchMatchStatusByStandardId(standardIdList);
                List<MatchScoresBetterDto> standardScores = matchScoresSearchMapper.searchScoresByStandardId(standardIdList);
                List<Long> matchIdList = new ArrayList<>();
                for (MatchScoresBetterDto standardScore : standardScores) {
                    matchIdList.add(Long.valueOf(standardScore.getMatchId()));
                }
                List<StandardSportMarketSell> standardScoresList = new ArrayList<>();
                List<MatchScoresBetterDto> matchScoresPd1List = new ArrayList<>();
                List<MatchScoresBetterDto> matchScoresPd2List = new ArrayList<>();
                List<ThirdMatchInfo> thirdMatchInfos = new ArrayList<>();
                if (!CollectionUtils.isEmpty(matchIdList)) {
                    standardScoresList = standardSportMarketSellMapper.selectByMatchIds(matchIdList);
                    matchScoresPd1List = matchScoresSearchMapper.selectScoresByMatchIdPDList(matchIdList);
                    matchScoresPd2List = matchScoresSearchMapper.selectScoresByMatchIdPD2List(matchIdList);
                    thirdMatchInfos = thirdMatchInfoMapper.selectByMatchIds(matchIdList);
                }
                Map<String, Integer> userStatusMap = queryReporterLoginStatus(standardScoresList);

                for (MatchScoresBetterDto standardScore : standardScores) {
                    for (StandardSportMarketSell marketSell : standardScoresList) {
                        if (standardScore.getMatchId().equals(String.valueOf(marketSell.getMatchInfoId()))) {
                            if (null != marketSell.getReporter()) {
                                if (CollectionUtils.isEmpty(matchScoresPd1List) && !CollectionUtils.isEmpty(thirdMatchInfos)) {
                                    List<ThirdMatchInfo> matchInfoList = thirdMatchInfos.stream().filter(item -> item.getReferenceId().equals(marketSell.getMatchInfoId()) && "PD".equals(item.getDataSourceCode())).collect(Collectors.toList());
                                    if (!CollectionUtils.isEmpty(matchInfoList)) {
                                        PdOneInfo pdOneInfo = new PdOneInfo();
                                        pdOneInfo.setOperator(marketSell.getReporter());
                                        pdOneInfo.setPeriodNow(0L);
                                        pdOneInfo.setStatus(userStatusMap.getOrDefault(marketSell.getReporter(), null));
                                        standardScore.setPdOneInfo(pdOneInfo);
                                    }
                                }
                                for (MatchScoresBetterDto matchScoresPd1Dto : matchScoresPd1List) {
                                    if (!ObjectUtils.isEmpty(matchScoresPd1Dto) && standardScore.getMatchId().equals(matchScoresPd1Dto.getMatchId())) {
                                        String dataSourceCode = matchScoresPd1Dto.getDataSourceCode();
                                        String reporter = standardScoresList.stream().filter(item -> String.valueOf(item.getMatchInfoId()).equals(standardScore.getMatchId())).map(StandardSportMarketSell::getReporter).collect(Collectors.joining());
                                        Long periodNow = matchScoresPd1Dto.getPeriodNow();
                                        PdOneInfo pdOneInfo = new PdOneInfo();
                                        pdOneInfo.setDataSourceCode(dataSourceCode);
                                        pdOneInfo.setOperator(reporter);
                                        pdOneInfo.setPeriodNow(periodNow);
                                        pdOneInfo.setStatus(userStatusMap.getOrDefault(reporter, null));
                                        String scoresJson = matchScoresPd1Dto.getScoresJson();
                                        Map<Long, FootballScores> scoresMap = JSONObject.parseObject(scoresJson, new TypeReference<Map<Long, FootballScores>>() {
                                        });
                                        FootballScores wholeScores = scoresMap.get(WHOLE_MATCH);
                                        CommonItem pdWholeScore = null;
                                        if (null != wholeScores && null != wholeScores.getGoal()) {
                                            pdWholeScore = new CommonItem(wholeScores.getGoal().getHome(), wholeScores.getGoal().getAway());
                                        }
                                        pdOneInfo.setMatchScores(pdWholeScore);
                                        FootballScores periodScores = scoresMap.get(periodNow);
                                        CommonItem pdPeriodScore = null;
                                        if (null != periodScores && null != periodScores.getGoal()) {
                                            pdPeriodScore = new CommonItem(periodScores.getGoal().getHome(), periodScores.getGoal().getAway());
                                        }
                                        pdOneInfo.setPeriodScores(pdPeriodScore);
                                        Map<String, CommonItem> map = new HashMap<>();
                                        map.put("periodScore", pdPeriodScore);
                                        map.put("wholeScore", pdWholeScore);
                                        pdOneInfo.setAllScore(map);
                                        standardScore.setPdOneInfo(pdOneInfo);
                                    }
                                }
                            }
                            if (null != marketSell.getReporter2()) {
                                if (CollectionUtils.isEmpty(matchScoresPd2List) && !CollectionUtils.isEmpty(thirdMatchInfos)) {
                                    List<ThirdMatchInfo> matchInfoList = thirdMatchInfos.stream().filter(item -> item.getReferenceId().equals(marketSell.getMatchInfoId()) && "PD2".equals(item.getDataSourceCode())).collect(Collectors.toList());
                                    if (!CollectionUtils.isEmpty(matchInfoList)) {
                                        PdTwoInfo pdTwoInfo = new PdTwoInfo();
                                        pdTwoInfo.setOperator(marketSell.getReporter2());
                                        pdTwoInfo.setStatus(userStatusMap.getOrDefault(marketSell.getReporter2(), null));
                                        pdTwoInfo.setPeriodNow(0L);
                                        standardScore.setPdTwoInfo(pdTwoInfo);
                                    }
                                }
                                for (MatchScoresBetterDto matchScoresPd2Dto : matchScoresPd2List) {
                                    if (!ObjectUtils.isEmpty(matchScoresPd2Dto) && standardScore.getMatchId().equals(matchScoresPd2Dto.getMatchId())) {
                                        String dataSourceCode = matchScoresPd2Dto.getDataSourceCode();
                                        String reporter2 = standardScoresList.stream().filter(item -> String.valueOf(item.getMatchInfoId()).equals(standardScore.getMatchId())).map(StandardSportMarketSell::getReporter2).collect(Collectors.joining());
                                        Long periodNow = matchScoresPd2Dto.getPeriodNow();
                                        PdTwoInfo pdTwoInfo = new PdTwoInfo();
                                        pdTwoInfo.setDataSourceCode(dataSourceCode);
                                        pdTwoInfo.setOperator(reporter2);
                                        pdTwoInfo.setStatus(userStatusMap.getOrDefault(reporter2, null));
                                        pdTwoInfo.setPeriodNow(periodNow == null ? 0 : periodNow);
                                        String scoresJson = matchScoresPd2Dto.getScoresJson();
                                        Map<Long, FootballScores> scoresMap = JSONObject.parseObject(scoresJson, new TypeReference<Map<Long, FootballScores>>() {
                                        });
                                        FootballScores wholeScores = scoresMap.get(WHOLE_MATCH);
                                        CommonItem pd2WholeScore = null;
                                        if (null != wholeScores && null != wholeScores.getGoal()) {
                                            pd2WholeScore = new CommonItem(wholeScores.getGoal().getHome(), wholeScores.getGoal().getAway());
                                        }
                                        pdTwoInfo.setMatchScores(pd2WholeScore);
                                        FootballScores periodScores = scoresMap.get(periodNow);
                                        CommonItem pd2PeriodScore = null;
                                        if (null != periodScores && null != periodScores.getGoal()) {
                                            pd2PeriodScore = new CommonItem(periodScores.getGoal().getHome(), periodScores.getGoal().getAway());
                                        }
                                        pdTwoInfo.setPeriodScores(pd2PeriodScore);
                                        Map<String, CommonItem> map = new HashMap<>();
                                        map.put("periodScore", pd2PeriodScore);
                                        map.put("wholeScore", pd2WholeScore);
                                        pdTwoInfo.setAllScore(map);
                                        standardScore.setPdTwoInfo(pdTwoInfo);
                                    }
                                }
                            }
                        }
                    }
                    standardScoreIds.add(standardScore.getMatchId());
                    matchScoresBetterDtoMap.put(standardScore.getMatchId(), standardScore);
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
                List<StandardSportMarketSell> standardScoresList = new ArrayList<>();
                List<MatchScoresBetterDto> matchScoresPd1List = new ArrayList<>();
                List<MatchScoresBetterDto> matchScoresPd2List = new ArrayList<>();
                List<ThirdMatchInfo> thirdMatchInfos = new ArrayList<>();
                if (!CollectionUtils.isEmpty(standardThirdIdList)) {
                    standardScoresList = standardSportMarketSellMapper.selectByMatchIds(standardThirdIdList);
                    matchScoresPd1List = matchScoresSearchMapper.selectScoresByMatchIdPDList(standardThirdIdList);
                    matchScoresPd2List = matchScoresSearchMapper.selectScoresByMatchIdPD2List(standardThirdIdList);
                    thirdMatchInfos = thirdMatchInfoMapper.selectByMatchIds(standardThirdIdList);
                }
                Map<String, Integer> userStatusMap = queryReporterLoginStatus(standardScoresList);
                List<String> standardScoresCollect = standardScores.stream().map(MatchScoresBetterDto::getMatchId).collect(Collectors.toList());
                for (Long matchId : standardThirdIdList) {
                    if (!standardScoresCollect.contains(String.valueOf(matchId))) {
                        MatchScoresBetterDto dto = new MatchScoresBetterDto();
                        dto.setMatchId(String.valueOf(matchId));
                        standardScores.add(dto);
                    }
                }
                for (MatchScoresBetterDto standardScore : standardScores) {
                    for (StandardSportMarketSell marketSell : standardScoresList) {
                        if (standardScore.getMatchId().equals(String.valueOf(marketSell.getMatchInfoId()))) {
                            if (null != marketSell.getReporter()) {
                                if (CollectionUtils.isEmpty(matchScoresPd1List) && !CollectionUtils.isEmpty(thirdMatchInfos)) {
                                    List<ThirdMatchInfo> matchInfoList = thirdMatchInfos.stream().filter(item -> item.getReferenceId().equals(marketSell.getMatchInfoId()) && "PD".equals(item.getDataSourceCode())).collect(Collectors.toList());
                                    if (!CollectionUtils.isEmpty(matchInfoList)) {
                                        PdOneInfo pdOneInfo = new PdOneInfo();
                                        pdOneInfo.setOperator(marketSell.getReporter());
                                        pdOneInfo.setPeriodNow(0L);
                                        pdOneInfo.setStatus(userStatusMap.getOrDefault(marketSell.getReporter(), null));
                                        standardScore.setPdOneInfo(pdOneInfo);
                                    }
                                }
                                for (MatchScoresBetterDto matchScoresPd1Dto : matchScoresPd1List) {
                                    if (!ObjectUtils.isEmpty(matchScoresPd1Dto) && standardScore.getMatchId().equals(matchScoresPd1Dto.getMatchId())) {
                                        String dataSourceCode = matchScoresPd1Dto.getDataSourceCode();
                                        String reporter = standardScoresList.stream().filter(item -> String.valueOf(item.getMatchInfoId()).equals(standardScore.getMatchId())).map(StandardSportMarketSell::getReporter).collect(Collectors.joining());
                                        Long periodNow = matchScoresPd1Dto.getPeriodNow();
                                        PdOneInfo pdOneInfo = new PdOneInfo();
                                        pdOneInfo.setDataSourceCode(dataSourceCode);
                                        pdOneInfo.setOperator(reporter);
                                        pdOneInfo.setStatus(userStatusMap.getOrDefault(reporter, null));
                                        pdOneInfo.setPeriodNow(periodNow);
                                        String scoresJson = matchScoresPd1Dto.getScoresJson();
                                        Map<Long, FootballScores> scoresMap = JSONObject.parseObject(scoresJson, new TypeReference<Map<Long, FootballScores>>() {
                                        });
                                        FootballScores wholeScores = scoresMap.get(WHOLE_MATCH);
                                        CommonItem pdWholeScore = null;
                                        if (null != wholeScores && null != wholeScores.getGoal()) {
                                            pdWholeScore = new CommonItem(wholeScores.getGoal().getHome(), wholeScores.getGoal().getAway());
                                        }
                                        pdOneInfo.setMatchScores(pdWholeScore);
                                        FootballScores periodScores = scoresMap.get(periodNow);
                                        CommonItem pdPeriodScore = null;
                                        if (null != periodScores && null != periodScores.getGoal()) {
                                            pdPeriodScore = new CommonItem(periodScores.getGoal().getHome(), periodScores.getGoal().getAway());
                                        }
                                        pdOneInfo.setPeriodScores(pdPeriodScore);
                                        Map<String, CommonItem> map = new HashMap<>();
                                        map.put("periodScore", pdPeriodScore);
                                        map.put("wholeScore", pdWholeScore);
                                        pdOneInfo.setAllScore(map);
                                        standardScore.setPdOneInfo(pdOneInfo);
                                    }
                                }
                            }
                            if (null != marketSell.getReporter2()) {
                                if (CollectionUtils.isEmpty(matchScoresPd2List) && !CollectionUtils.isEmpty(thirdMatchInfos)) {
                                    List<ThirdMatchInfo> matchInfoList = thirdMatchInfos.stream().filter(item -> item.getReferenceId().equals(marketSell.getMatchInfoId()) && "PD2".equals(item.getDataSourceCode())).collect(Collectors.toList());
                                    if (!CollectionUtils.isEmpty(matchInfoList)) {
                                        PdTwoInfo pdTwoInfo = new PdTwoInfo();
                                        pdTwoInfo.setOperator(marketSell.getReporter2());
                                        pdTwoInfo.setStatus(userStatusMap.getOrDefault(marketSell.getReporter2(), null));
                                        pdTwoInfo.setPeriodNow(0L);
                                        standardScore.setPdTwoInfo(pdTwoInfo);
                                    }
                                }
                                for (MatchScoresBetterDto matchScoresPd2Dto : matchScoresPd2List) {
                                    if (!ObjectUtils.isEmpty(matchScoresPd2Dto) && standardScore.getMatchId().equals(matchScoresPd2Dto.getMatchId())) {
                                        String dataSourceCode = matchScoresPd2Dto.getDataSourceCode();
                                        String reporter2 = standardScoresList.stream().filter(item -> String.valueOf(item.getMatchInfoId()).equals(standardScore.getMatchId())).map(StandardSportMarketSell::getReporter2).collect(Collectors.joining());
                                        Long periodNow = matchScoresPd2Dto.getPeriodNow();
                                        PdTwoInfo pdTwoInfo = new PdTwoInfo();
                                        pdTwoInfo.setDataSourceCode(dataSourceCode);
                                        pdTwoInfo.setOperator(reporter2);
                                        pdTwoInfo.setStatus(userStatusMap.getOrDefault(reporter2, null));
                                        pdTwoInfo.setPeriodNow(periodNow == null ? 0 : periodNow);
                                        String scoresJson = matchScoresPd2Dto.getScoresJson();
                                        Map<Long, FootballScores> scoresMap = JSONObject.parseObject(scoresJson, new TypeReference<Map<Long, FootballScores>>() {
                                        });
                                        FootballScores wholeScores = scoresMap.get(WHOLE_MATCH);
                                        CommonItem pd2WholeScore = null;
                                        if (null != wholeScores && null != wholeScores.getGoal()) {
                                            pd2WholeScore = new CommonItem(wholeScores.getGoal().getHome(), wholeScores.getGoal().getAway());
                                        }
                                        pdTwoInfo.setMatchScores(pd2WholeScore);
                                        FootballScores periodScores = scoresMap.get(periodNow);
                                        CommonItem pd2PeriodScore = null;
                                        if (null != periodScores && null != periodScores.getGoal()) {
                                            pd2PeriodScore = new CommonItem(periodScores.getGoal().getHome(), periodScores.getGoal().getAway());
                                        }
                                        pdTwoInfo.setPeriodScores(pd2PeriodScore);
                                        Map<String, CommonItem> map = new HashMap<>();
                                        map.put("periodScore", pd2PeriodScore);
                                        map.put("wholeScore", pd2WholeScore);
                                        pdTwoInfo.setAllScore(map);
                                        standardScore.setPdTwoInfo(pdTwoInfo);
                                    }
                                }
                            }
                        }
                    }
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
                    changeHomeAway(score);
                }
            }
        }catch (Exception e){
            log.error("searchBetterListMatchScores推送异常:{},-------",e.getMessage(),e);
        }
        return scores;
    }

    public List<MatchScoresBetterDto> searchBetterListScoreNet(String key, List<Long> matchIds) {
//        List<PlsThirdMatchRelationIdDTO> plsList = plsThirdMatchRelationMapper.selectByPlsMatchManageIdList(matchIds);
//        List<Long> standardMatchIdList = plsList.stream().map(PlsThirdMatchRelationIdDTO::getStandardMatchId).collect(Collectors.toList());
//        List<MatchScoresBetterDto> matchScoresBetterDtos = matchScoresSearchMapper.searchScoresByStandardId(standardMatchIdList);
//        for (PlsThirdMatchRelationIdDTO plsThirdMatchRelation : plsList) {
//            for (MatchScoresBetterDto matchScoresBetterDto : matchScoresBetterDtos) {
//                if (plsThirdMatchRelation.getStandardMatchId().equals(Long.valueOf(matchScoresBetterDto.getMatchId()))) {
//                    matchScoresBetterDto.setPlsMatchManageId(plsThirdMatchRelation.getPlsMatchManageId());
//                }
//            }
//        }
//        List<MatchScoresBetterDto> newMatchScoresBetterDtos = new ArrayList<>(matchScoresBetterDtos);
        List<MatchScoresBetterDto> newMatchScoresBetterDtos = new ArrayList<>();
//        List<Long> oldPlsList = matchScoresBetterDtos.stream().map(MatchScoresBetterDto::getPlsMatchManageId).collect(Collectors.toList());
//        for (Long matchId : matchIds) {
//            if (!oldPlsList.contains(matchId)) {
//                MatchScoresBetterDto dto = new MatchScoresBetterDto();
//                dto.setPlsMatchManageId(matchId);
//                newMatchScoresBetterDtos.add(dto);
//            }
//        }
        return newMatchScoresBetterDtos;
    }

    /**
     * 主客队调换
     * @param standardScore
     */
    public void changeHomeAway(MatchScoresBetterDto standardScore) {
        //只有足球才做主客队相反
        try {
            if (standardScore != null && standardScore.getSportId()!=null) {
                if (standardScore.getSportId().equals(1L)){
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

            }
        }catch (Exception e){
            log.error("changeHomeAway error:{}",e);
            e.printStackTrace();
        }
    }

    private Map<String, Integer> queryReporterLoginStatus(List<StandardSportMarketSell> standardScoresList){
        Map<String, Integer> userStatusMap = new HashMap<>();
        if (CollectionUtils.isEmpty(standardScoresList)) {
            return userStatusMap;
        }

        // 查询报球员状态
        List<String> reporters = standardScoresList.stream().map(t-> {
            List<String> sReporter = new ArrayList<>();
            sReporter.add(t.getReporter());
            sReporter.add(t.getReporter2());
            return sReporter;
        }).flatMap(Collection::stream).distinct().collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(reporters)){
            try {
                List<Map<String, Object>> userStatus = iAuthRequiredPermission.getUserStatus(null, reporters);
                log.info("[ScoreSearchServiceImpl] queryReporterLoginStatus userStatus:{}", userStatus);
                for (Map<String, Object> user : userStatus) {
                    String userName = String.valueOf(user.getOrDefault("userName", ""));
                    Integer loginStatus = Integer.valueOf(String.valueOf(user.getOrDefault("loginStatus", "-1")));
                    userStatusMap.put(userName, loginStatus);
                }
            } catch (Exception e) {
                log.error("[ScoreSearchServiceImpl] queryReporterLoginStatus error: ", e);
            }
        }
        return userStatusMap;
    }
}
