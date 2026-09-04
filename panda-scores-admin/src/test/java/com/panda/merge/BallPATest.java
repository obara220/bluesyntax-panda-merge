package com.panda.merge;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.advertise.dubbo.MatchFootballBallAdvertiseApiImpl;
import com.panda.merge.advertise.dubbo.MatchTennisAdvertiseApiImpl;
import com.panda.merge.advertise.utils.MatchPeriodUtils;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.TennisMatchLengthEnum;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.dto.*;
import com.panda.merge.dto.advertise.DeleteEventDto;
import com.panda.merge.dto.advertise.EventListDto;
import com.panda.merge.dto.advertise.MatchAdvertiseQueryDto;
import com.panda.merge.dto.advertise.TennisEditSecondScoreDto;
import com.panda.merge.mq.message.CommonStandardScoresDto;
import com.panda.merge.utils.JsonMapUtils;
import com.panda.merge.utils.ScoreUtils;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

@Slf4j
@SpringBootTest
public class BallPATest {

    @Autowired
    MatchFootballBallAdvertiseApiImpl matchFootballBallAdvertiseApi;

    @Autowired
    MatchTennisAdvertiseApiImpl matchTennisAdvertiseApi;

    @Autowired
    ScoreUtils scoreUtils;



    @Test
    public void currentScore()
    {
        String paramStr = "{-1:{\"breakPointCount\":{\"away\":0,\"home\":0},\"breakSuccessCount\":{\"away\":0,\"home\":0},\"breakSuccessRate\":{\"away\":0,\"home\":0},\"currentScore\":{\"away\":0,\"home\":30},\"doubleFoolScore\":{\"away\":0,\"home\":0},\"matchScore\":{\"away\":0,\"home\":0},\"qiangScore\":{\"away\":0,\"home\":0},\"scoreNumber\":{\"away\":0,\"home\":0},\"servesFaultCount\":{\"away\":0,\"home\":0},\"servesScoredCount\":{\"away\":0,\"home\":0},\"setScore\":{\"away\":0,\"home\":3}},8:{\"breakPointCount\":{\"away\":0,\"home\":0},\"breakSuccessCount\":{\"away\":0,\"home\":0},\"breakSuccessRate\":{\"away\":0,\"home\":0},\"currentScore\":{\"away\":0,\"home\":30},\"doubleFoolScore\":{\"away\":0,\"home\":0},\"matchScore\":{\"away\":0,\"home\":0},\"qiangScore\":{\"away\":0,\"home\":0},\"scoreNumber\":{\"away\":0,\"home\":0},\"servesFaultCount\":{\"away\":0,\"home\":0},\"servesScoredCount\":{\"away\":0,\"home\":0},\"setScore\":{\"away\":0,\"home\":3}}}";
        CommonStandardScoresDto commonScoresDto = new CommonStandardScoresDto();
        commonScoresDto.setScores(JsonMapUtils.transferSimpleJsonMap( paramStr ));
        log.info("-------------------->:{}", JSON.toJSONString(commonScoresDto.getScores()));
        if ( null != commonScoresDto.getScores() )
        {
            JSONObject periodFootballScores = JSONObject.parseObject( JSON.toJSONString(commonScoresDto.getScores()) );
            Map<Long, TennisScores> allPeriodScores = JsonMapUtils.parseTennisMap(periodFootballScores);
            for ( Map.Entry<Long, TennisScores> scoreEntry : allPeriodScores.entrySet() )
            {
                CommonItem currentScore = scoreEntry.getValue().getCurrentScore();
                currentScore.setHome(0);
                currentScore.setAway(0);
                scoreEntry.getValue().setCurrentScore(currentScore);
            }

            commonScoresDto.setScores(JsonMapUtils.transferSimpleJsonMap( JSON.toJSONString(allPeriodScores) ));
        }
        log.info("-------------------->:{}", JSON.toJSONString(commonScoresDto.getScores()));
    }



    @Test
    public void ExterCode()
    {
        String paramStr = "{\"currentScoresMap\":{1:{1:{\"away\":15,\"home\":40},2:{\"away\":0,\"home\":50},3:{\"away\":0,\"home\":50},4:{\"away\":0,\"home\":0}},8:{1:{\"away\":0,\"home\":0}}}}";
        CommonStandardScoresDto commonScoresDto = new CommonStandardScoresDto();
        JSONObject extrayScore=JSONObject.parseObject( paramStr );
        commonScoresDto.setExtraScores(extrayScore);

        if ( null != commonScoresDto.getExtraScores() )
        {
            JSONObject extraScores = commonScoresDto.getExtraScores();
            log.info("-------------------->:{}", extraScores);

            TennisExtryScores tennisExtryScores = JSONObject.toJavaObject( extraScores, TennisExtryScores.class );
            if ( null != tennisExtryScores )
            {
                Map<Integer, Map<Integer,CommonItem>> currentScoresMap = tennisExtryScores.getCurrentScoresMap();
                if ( currentScoresMap.size() > 0 )
                {
                    for ( Map.Entry<Integer, Map<Integer,CommonItem>> scoresEntry : currentScoresMap.entrySet() )
                    {
                        Map<Integer, CommonItem> commonMap = scoresEntry.getValue();

                        for ( Map.Entry<Integer, CommonItem> commonItemEntry : commonMap.entrySet())
                        {
                            commonItemEntry.getValue().setAway(0);
                            commonItemEntry.getValue().setHome(0);
                        }
                    }
                }
            }
            JSONObject scoreObj = (JSONObject)JSONObject.toJSON(tennisExtryScores);
            commonScoresDto.setExtraScores( scoreObj );
            log.info("-------------------->:{}", commonScoresDto.getExtraScores());
        }
    }

    @Test
    public void xjbg()
    {
        String param = "{\"1\":\"5\",\"2\":2,\"3\":3,\"4\":2,\"5\":2}";
        TreeMap<String, Object> treeMap = JSONObject.parseObject(param, TreeMap.class);
        log.info("treeMap:{}" , JSON.toJSONString(treeMap));
        Map<Integer, Integer> checkMap = Maps.newHashMap();
        for (Map.Entry<String, Object> entry : treeMap.entrySet())
        {
            String type = entry.getValue().toString();
            TennisMatchLengthEnum byCode = TennisMatchLengthEnum.getByCode(type);
            entry.setValue(Integer.parseInt(byCode.getValue()));
            checkMap.put( Integer.parseInt(entry.getKey()), Integer.parseInt(byCode.getValue()) );
        }
        log.info("获取具体的赛制后:{}", JSON.toJSONString(treeMap));
        log.info("获取具体的赛制后:{}", JSON.toJSONString(checkMap));

        Map<Integer, Map<Integer, CommonItem>> scoresMap = Maps.newTreeMap();

        Map<Integer, Map<Integer, CommonItem>> currentScoresMap =  getCurrentScoresMap();
        log.info("getCurrentScoresMap:{}", JSON.toJSONString(currentScoresMap));

        for (Map.Entry<Integer, Map<Integer, CommonItem>> scoresEntry : currentScoresMap.entrySet() )
        {
            Integer round = scoresEntry.getKey();
            Integer setKey = scoresEntry.getKey();
            Map<Integer, CommonItem> setValue = scoresEntry.getValue();
            if (checkMap.containsKey(round))
            {
                Integer standard = checkMap.get(round);
                Map<Integer, CommonItem> commonMap = scoresEntry.getValue();
                Map<Integer, CommonItem> scoreDetail = Maps.newTreeMap();
                for ( Map.Entry<Integer, CommonItem> commonItemEntry : commonMap.entrySet())
                {
                    Integer key = commonItemEntry.getKey();
                    if (key <= standard)
                    {
                        scoreDetail.put(key, commonItemEntry.getValue());
                    }
                }
                scoresMap.put( setKey, scoreDetail);
            }
            else
            {
                scoresMap.put( setKey, setValue);
            }
        }
        log.info("scoresMap:{}", JSON.toJSONString(scoresMap));
    }

    private Map<Integer, Map<Integer,CommonItem>> getCurrentScoresMap()
    {
        Map<Integer, Map<Integer,CommonItem>> currentScoresMap = Maps.newTreeMap();
        for (int x = 1; x <= 5 ; x ++ )
        {
            Map<Integer,CommonItem> treeScoresMap = Maps.newTreeMap();
            for (int y = 1; y <= 13 ; y ++ )
            {
                CommonItem commonItem = new CommonItem();
                commonItem.setHome(0);
                commonItem.setAway(0);
                treeScoresMap.put(y, commonItem);
            }
            currentScoresMap.put(x, treeScoresMap);
        }
        return currentScoresMap;
    }

    @Test
    public void toDeleteEventForGoal() {
        log.info("+++++++++++++++++++++");
        String paramStr = "{\"thirdMatchId\":\"1638462276918398979\",\"deleteEventId\":\"1638468713115553793\",\"timeFromStartSecond\":1492}";
        DeleteEventDto deleteEventDto = JSONObject.parseObject(paramStr, DeleteEventDto.class);
        deleteEventDto.setLinkedId("09W45UTY09WERUY09ERT0Y9HER0UYH0ET");
        deleteEventDto.setOperatorId("419");
        deleteEventDto.setOperatorName("nonghung");
        matchFootballBallAdvertiseApi.deleteEvent(deleteEventDto);
    }


    @Test
    public void getEventList() {
        String paramStr = "{\"thirdMatchId\":\"1639887474372726787\"}";
        EventListDto eventListDto = JSONObject.parseObject(paramStr, EventListDto.class);
        eventListDto.setLinkedId("09W45UTY09WERUY09ERT0Y9HER0UYH0ET");
        eventListDto.setOperatorId("419");
        eventListDto.setOperatorName("nonghung");
        Response r = matchTennisAdvertiseApi.eventList(eventListDto);
        log.info(":{}", r);
    }

    @Test
    public void setRoundScores() {
        String paramStr = "{\"thirdMatchId\":\"1641994366130016260\",\"standardMatchId\":3408928,\"firstNum\":1,\"secondNum\":2,\"homeAway\":\"away\",\"t1\":50,\"t2\":30,\"currentSet\":1,\"currentRound\":2}";

        TennisEditSecondScoreDto tennisAdvertiseDto = JSON.parseObject(paramStr, TennisEditSecondScoreDto.class);
        tennisAdvertiseDto.setOperatorId("1100000");
        tennisAdvertiseDto.setOperatorName("champion");
        tennisAdvertiseDto.setLinkedId("n9vw8tu9w8erutn9v8w45ut98w45ut9");
        matchTennisAdvertiseApi.setMatchSecondScore(tennisAdvertiseDto);
    }

    @Test
    public void jsonTest() {
        String paramStr = "{-1:{\"breakPointCount\":{\"away\":0,\"home\":0},\"breakSuccessCount\":{\"away\":0,\"home\":0},\"breakSuccessRate\":{\"away\":0,\"home\":0},\"currentScore\":{\"away\":0,\"home\":40},\"doubleFoolScore\":{\"away\":0,\"home\":0},\"matchScore\":{\"away\":0,\"home\":1},\"qiangScore\":{\"away\":0,\"home\":0},\"scoreNumber\":{\"away\":0,\"home\":0},\"servesFaultCount\":{\"away\":0,\"home\":0},\"servesScoredCount\":{\"away\":0,\"home\":0},\"setScore\":{\"away\":0,\"home\":3}},8:{\"breakPointCount\":{\"away\":0,\"home\":0},\"breakSuccessCount\":{\"away\":0,\"home\":0},\"breakSuccessRate\":{\"away\":0,\"home\":0},\"currentScore\":{\"away\":0,\"home\":0},\"doubleFoolScore\":{\"away\":0,\"home\":0},\"matchScore\":{\"away\":0,\"home\":0},\"qiangScore\":{\"away\":0,\"home\":0},\"scoreNumber\":{\"away\":0,\"home\":0},\"servesFaultCount\":{\"away\":0,\"home\":0},\"servesScoredCount\":{\"away\":0,\"home\":0},\"setScore\":{\"away\":0,\"home\":3}}}";
//        Map<Long, TennisScores> allPeriodScores = scoreUtils.periodJson(paramStr, TennisScores.class);
    }

    public static void main(String[] args) {
        Integer firstT1 = 0,firstT2 = 0,secondT1 = 0,secondT2 = 0 ;

        String linkId = "21321321";
        Long Period = 301L;
        Integer currentSet = 2;
        Integer currentRound = 1;
        String scoresJsonExtra = "{\"currentScoresMap\":{1:{1:{\"away\":0,\"home\":15},2:{\"away\":0,\"home\":50}},8:{1:{\"away\":0,\"home\":0}}}}";
        String scoresJson = "{-1:{\"breakPointCount\":{\"away\":0,\"home\":0},\"breakSuccessCount\":{\"away\":0,\"home\":0},\"breakSuccessRate\":{\"away\":0,\"home\":0},\"currentScore\":{\"away\":0,\"home\":50},\"doubleFoolScore\":{\"away\":0,\"home\":0},\"matchScore\":{\"away\":0,\"home\":1},\"qiangScore\":{\"away\":0,\"home\":0},\"scoreNumber\":{\"away\":0,\"home\":0},\"servesFaultCount\":{\"away\":0,\"home\":0},\"servesScoredCount\":{\"away\":0,\"home\":0},\"setScore\":{\"away\":0,\"home\":2}},8:{\"breakPointCount\":{\"away\":0,\"home\":0},\"breakSuccessCount\":{\"away\":0,\"home\":0},\"breakSuccessRate\":{\"away\":0,\"home\":0},\"currentScore\":{\"away\":0,\"home\":50},\"doubleFoolScore\":{\"away\":0,\"home\":0},\"matchScore\":{\"away\":0,\"home\":2},\"qiangScore\":{\"away\":0,\"home\":0},\"scoreNumber\":{\"away\":0,\"home\":0},\"servesFaultCount\":{\"away\":0,\"home\":0},\"servesScoredCount\":{\"away\":0,\"home\":0},\"setScore\":{\"away\":0,\"home\":2}}}";

        JSONObject periodFootballScores = JSONObject.parseObject(scoresJson);
        Map<Long, TennisScores> allPeriodScores = JsonMapUtils.parseTennisMap(periodFootballScores);

        // 计算局比分
        Map<Integer, CommonItem> setScore = Maps.newConcurrentMap();
        for (Map.Entry<Long, TennisScores> entry : allPeriodScores.entrySet())
        {
            if (entry.getKey().equals(WHOLE_MATCH))
            {
                continue;
            }
            Integer setNumber = MatchPeriodUtils.getTennisSetByPeriod(entry.getKey());
            if ( null == setNumber )
            {
                continue;
            }
            CommonItem commonItemVo = new CommonItem();
            BeanUtils.copyProperties(entry.getValue().getSetScore(), commonItemVo);
            setScore.put(setNumber, commonItemVo);
        }

        // 计算局内比分
        TennisExtryScores tennisExtryScores;
        if (StringUtils.isEmpty(scoresJsonExtra))
        {
            tennisExtryScores = new TennisExtryScores();
        }
        else
        {
            tennisExtryScores = JSONObject.toJavaObject((JSONObject.parseObject(scoresJsonExtra)), TennisExtryScores.class);
        }
        Map<Integer, Map<Integer, CommonItem>> currentScoresMap = tennisExtryScores.getCurrentScoresMap();
        Map<String, Integer> secondMap = null;
        // 根据不同的赛事阶段统计不同的比分
        if ( null != Period ) {
            // 第一盘
            if ( "8".equals(Period.toString()) || "800".equals(Period.toString()) || "301".equals(Period.toString()) )
            {
                if ( !Objects.isNull(setScore.get(1)))
                {
                    firstT1 = setScore.get(1).getHome();
                    firstT2 = setScore.get(1).getAway();
                }
                secondMap = statisticsRoundScore(linkId, 1, currentScoresMap);
            }
            //局盘切换的部分
            else if ( "302".equals(Period.toString()) || "303".equals(Period.toString()) || "304".equals(Period.toString()) )
            {
                Integer tCurrentSet = currentSet -1 ;
                if ( !Objects.isNull(setScore.get(tCurrentSet)))
                {
                    firstT1 = setScore.get(tCurrentSet).getHome();
                    firstT2 = setScore.get(tCurrentSet).getAway();
                }
                secondMap = statisticsRoundScore(linkId, tCurrentSet, currentScoresMap);
            }
            else
            {
                if ( !Objects.isNull(setScore.get(currentSet)) )
                {
                    firstT1 = setScore.get(currentSet).getHome();
                    firstT2 = setScore.get(currentSet).getAway();
                }
                secondMap = statisticsRoundScore(linkId, currentSet, currentScoresMap);
            }
            if (null != secondMap  && secondMap.size() > 0 ) {
                secondT1 = secondMap.get("secondT1");
                secondT2 = secondMap.get("secondT2");
            }
        }
        System.out.printf("secondT1:" + secondT1);
        System.out.printf("secondT2:" + secondT2);
    }

    private static Map<String, Integer> statisticsRoundScore(String linkId, Integer currentSet, Map<Integer, Map<Integer, CommonItem>> currentScoresMap)
    {
        log.info("::{}::currentSet:{}, currentScoresMap:{}", linkId, currentSet, JSON.toJSONString(currentScoresMap));
        Map<String, Integer> secondMap = Maps.newConcurrentMap();
        secondMap.put("secondT1", 0);
        secondMap.put("secondT2", 0);
        Map<Integer, CommonItem> currentRoundMap = currentScoresMap.get(currentSet);
        if ( null == currentScoresMap || 0 == currentRoundMap.size() )
        {
            return secondMap;
        }
        List<Integer> rounds = currentRoundMap.keySet().stream().sorted(Comparator.comparing(Integer::intValue).reversed()).collect(Collectors.toList());
        //Set<Integer> currentSets = currentRoundMap.keySet();
        if ( !CollectionUtils.isEmpty(rounds) )
        {
            for (Integer index : rounds)
            {
                CommonItem item = currentRoundMap.get(index);
                if (item.getAway() > 0 || item.getHome() > 0 )
                {
                    secondMap.put("secondT1", item.getHome());
                    secondMap.put("secondT2", item.getAway());
                    break;
                }
            }
        }
        log.info("::{}::secondMap:{}", linkId, currentSet, JSON.toJSONString(secondMap));
        return secondMap;
    }
}
