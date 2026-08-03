package com.panda.merge;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.advertise.dubbo.IMatchIcehockeyAdvertiseApiImpl;
import com.panda.merge.advertise.dubbo.MatchBasketBallAdvertiseApiImpl;
import com.panda.merge.advertise.dubbo.MatchFootballBallAdvertiseApiImpl;
import com.panda.merge.advertise.dubbo.MatchTennisAdvertiseApiImpl;
import com.panda.merge.advertise.utils.MatchPeriodUtils;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.TennisMatchLengthEnum;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.dto.*;
import com.panda.merge.dto.advertise.*;
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
    MatchBasketBallAdvertiseApiImpl matchBasketBallAdvertiseApi;

    @Autowired
    MatchTennisAdvertiseApiImpl matchTennisAdvertiseApi;

    @Autowired
    ScoreUtils scoreUtils;

    @Autowired
    IMatchIcehockeyAdvertiseApiImpl matchIcehockeyAdvertiseApi;

    @Test
    public void changeMatchStatus() {
        String paramStr = "{\"thirdMatchId\":\"1646765760600166403\",\"controlType\":1,\"periodId\":13}";
        ChangeMatchStatusDto changeMatchStatus = JSONObject.parseObject(paramStr, ChangeMatchStatusDto.class);
        changeMatchStatus.setLinkedId("09W45UTY09WERUY09ERT0Y9HER0UYH0ET");
        changeMatchStatus.setOperatorId("419");
        changeMatchStatus.setOperatorName("nonghung");
        matchBasketBallAdvertiseApi.changeMatchStatus(changeMatchStatus);
    }

    @Test
    public void confirmEvent()
    {
        String paramStr = "{\"thirdMatchId\":\"1645780628804292611\",\"confirmEventCode\":\"red_card\",\"homeAway\":\"home\",\"timeFromStartSecond\":1406}";

        ConfirmEventDto confirmEventDto = JSONObject.parseObject(paramStr, ConfirmEventDto.class);
        confirmEventDto.setLinkedId("09W45UTY09WERUY09ERT0Y9HER0UYH0ET");
        confirmEventDto.setOperatorId("419");
        confirmEventDto.setOperatorName("nonghung");
        Response response = matchFootballBallAdvertiseApi.confirmEvent(confirmEventDto);
        System.out.printf("response:{}", response);
    }

    @Test
    public void editYellowCard()
    {
        String paramStr = "{\"thirdMatchId\":\"1654730358938226693\",\"period\":7,\"confirmEventCode\":\"yellow_card\",\"timeFromStartSecond\":10273,\"dataList\":[{\"homeScore\":\"1\",\"awayScore\":\"0\",\"period15Min\":60899},{\"homeScore\":0,\"awayScore\":0,\"period15Min\":61799},{\"homeScore\":0,\"awayScore\":\"1\",\"period15Min\":62699},{\"homeScore\":0,\"awayScore\":0,\"period15Min\":73599},{\"homeScore\":0,\"awayScore\":0,\"period15Min\":74499},{\"homeScore\":0,\"awayScore\":0,\"period15Min\":75399}]}";

        Goal15MinDto confirmEventDto = JSONObject.parseObject(paramStr, Goal15MinDto.class);
        confirmEventDto.setLinkedId("09W45UTY09WERUY09ERT0Y9HER0UYH0ET");
        confirmEventDto.setOperatorId("419");
        confirmEventDto.setOperatorName("nonghung");
        Response response = matchFootballBallAdvertiseApi.edit15MinYellowCard(confirmEventDto);
        System.out.printf("response:{}", response);
    }

    @Test
    public void editCorner()
    {
        String paramStr = "{\"thirdMatchId\":\"1645663314863804419\",\"period\":6,\"confirmEventCode\":\"corner\",\"timeFromStartSecond\":1731,\"dataList\":[{\"homeScore\":\"1\",\"awayScore\":0,\"period15Min\":60899},{\"homeScore\":\"1\",\"awayScore\":0,\"period15Min\":61799}]}";

        Goal15MinDto confirmEventDto = JSONObject.parseObject(paramStr, Goal15MinDto.class);
        confirmEventDto.setLinkedId("09W45UTY09WERUY09ERT0Y9HER0UYH0ET");
        confirmEventDto.setOperatorId("419");
        confirmEventDto.setOperatorName("nonghung");
        Response response = matchFootballBallAdvertiseApi.edit15MinCorner(confirmEventDto);
        System.out.printf("response:{}", response);
    }


    @Test
    public void editRedCard()
    {
        String paramStr = "{\"thirdMatchId\":\"1644589967530872835\",\"period\":7,\"confirmEventCode\":\"red_card\",\"timeFromStartSecond\":79158,\"dataList\":[{\"homeScore\":\"1\",\"awayScore\":0,\"period15Min\":60899},{\"homeScore\":0,\"awayScore\":0,\"period15Min\":61799},{\"homeScore\":1,\"awayScore\":0,\"period15Min\":62699}]}";

        Goal15MinDto confirmEventDto = JSONObject.parseObject(paramStr, Goal15MinDto.class);
        confirmEventDto.setLinkedId("09W45UTY09WERUY09ERT0Y9HER0UYH0ET");
        confirmEventDto.setOperatorId("419");
        confirmEventDto.setOperatorName("nonghung");
        Response response = matchFootballBallAdvertiseApi.edit15MinRedCard(confirmEventDto);
        System.out.printf("response:{}", response);
    }




    @Test
    public void GetFootBallMatchInfo()
    {
        MatchAdvertiseQueryDto matchAdvertiseQueryDto = new MatchAdvertiseQueryDto();
        matchAdvertiseQueryDto.setThirdMatchId(1644589967530872835L);
        matchAdvertiseQueryDto.setLinkedId("09W45UTY09WERUY09ERT0Y9HER0UYH0ET");
        matchAdvertiseQueryDto.setOperatorId("419");
        matchAdvertiseQueryDto.setOperatorName("nonghung");
        Response response = matchFootballBallAdvertiseApi.getMatchAdvertiseInfo(matchAdvertiseQueryDto);
        System.out.printf("response:{}", response);
    }

    @Test
    public void toGetMatchInfo() {
        String paramStr = "{\"thirdMatchId\":\"1642479365126967298\"}";
        MatchAdvertiseQueryDto matchAdvertiseQueryDto = JSONObject.parseObject(paramStr, MatchAdvertiseQueryDto.class);
        matchAdvertiseQueryDto.setLinkedId("09W45UTY09WERUY09ERT0Y9HER0UYH0ET");
        matchAdvertiseQueryDto.setOperatorId("419");
        matchAdvertiseQueryDto.setOperatorName("nonghung");
        Response response = matchIcehockeyAdvertiseApi.getMatchAdvertiseInfo(matchAdvertiseQueryDto);
        System.out.printf("response:{}", response);
    }


    @Test
    public void settingBasketBakMatchType() {
        ChangeMatchLengthDto changeMatchLengthDto = new ChangeMatchLengthDto();
        changeMatchLengthDto.setThirdMatchId(1643898398230269956L);
        changeMatchLengthDto.setMinutes(9);
        changeMatchLengthDto.setLinkedId("09W45UTY09WERUY09ERT0Y9HER0UYH0ET");
        changeMatchLengthDto.setOperatorId("419");
        changeMatchLengthDto.setOperatorName("nonghung");

        matchBasketBallAdvertiseApi.changeMatchLenth(changeMatchLengthDto);
    }

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
        String param = "{\"1\":\"5\",\"2\":2,\"3\":3,\"4\":2,\"5\":1}";
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

                for (int index = 1; index <= standard ; index ++ )
                {
                    if ( commonMap.containsKey(index) ) {
                        scoreDetail.put(index, commonMap.get(index));
                    }
                    else
                    {
                        CommonItem addCommonItem = new CommonItem();
                        addCommonItem.setHome(0);
                        addCommonItem.setAway(0);
                        scoreDetail.put(index, addCommonItem);
                    }
                }

//                for ( Map.Entry<Integer, CommonItem> commonItemEntry : commonMap.entrySet())
//                {
//                    Integer key = commonItemEntry.getKey();
//                    if (key <= standard)
//                    {
//                        scoreDetail.put(key, commonItemEntry.getValue());
//                    }
//                }
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

//    @Test
//    public void jsonTest() {
//        String paramStr = "{-1:{\"breakPointCount\":{\"away\":0,\"home\":0},\"breakSuccessCount\":{\"away\":0,\"home\":0},\"breakSuccessRate\":{\"away\":0,\"home\":0},\"currentScore\":{\"away\":0,\"home\":40},\"doubleFoolScore\":{\"away\":0,\"home\":0},\"matchScore\":{\"away\":0,\"home\":1},\"qiangScore\":{\"away\":0,\"home\":0},\"scoreNumber\":{\"away\":0,\"home\":0},\"servesFaultCount\":{\"away\":0,\"home\":0},\"servesScoredCount\":{\"away\":0,\"home\":0},\"setScore\":{\"away\":0,\"home\":3}},8:{\"breakPointCount\":{\"away\":0,\"home\":0},\"breakSuccessCount\":{\"away\":0,\"home\":0},\"breakSuccessRate\":{\"away\":0,\"home\":0},\"currentScore\":{\"away\":0,\"home\":0},\"doubleFoolScore\":{\"away\":0,\"home\":0},\"matchScore\":{\"away\":0,\"home\":0},\"qiangScore\":{\"away\":0,\"home\":0},\"scoreNumber\":{\"away\":0,\"home\":0},\"servesFaultCount\":{\"away\":0,\"home\":0},\"servesScoredCount\":{\"away\":0,\"home\":0},\"setScore\":{\"away\":0,\"home\":3}}}";
//        Map<Long, TennisScores> allPeriodScores = scoreUtils.periodJson(paramStr, TennisScores.class);
//    }

//    public static void main(String[] args) {
//        Integer firstT1 = 0,firstT2 = 0,secondT1 = 0,secondT2 = 0 ;
//
//        String linkId = "21321321";
//        Long Period = 301L;
//        Integer currentSet = 2;
//        Integer currentRound = 1;
//        String scoresJsonExtra = "{\"currentScoresMap\":{1:{1:{\"away\":0,\"home\":15},2:{\"away\":0,\"home\":50}},8:{1:{\"away\":0,\"home\":0}}}}";
//        String scoresJson = "{-1:{\"breakPointCount\":{\"away\":0,\"home\":0},\"breakSuccessCount\":{\"away\":0,\"home\":0},\"breakSuccessRate\":{\"away\":0,\"home\":0},\"currentScore\":{\"away\":0,\"home\":50},\"doubleFoolScore\":{\"away\":0,\"home\":0},\"matchScore\":{\"away\":0,\"home\":1},\"qiangScore\":{\"away\":0,\"home\":0},\"scoreNumber\":{\"away\":0,\"home\":0},\"servesFaultCount\":{\"away\":0,\"home\":0},\"servesScoredCount\":{\"away\":0,\"home\":0},\"setScore\":{\"away\":0,\"home\":2}},8:{\"breakPointCount\":{\"away\":0,\"home\":0},\"breakSuccessCount\":{\"away\":0,\"home\":0},\"breakSuccessRate\":{\"away\":0,\"home\":0},\"currentScore\":{\"away\":0,\"home\":50},\"doubleFoolScore\":{\"away\":0,\"home\":0},\"matchScore\":{\"away\":0,\"home\":2},\"qiangScore\":{\"away\":0,\"home\":0},\"scoreNumber\":{\"away\":0,\"home\":0},\"servesFaultCount\":{\"away\":0,\"home\":0},\"servesScoredCount\":{\"away\":0,\"home\":0},\"setScore\":{\"away\":0,\"home\":2}}}";
//
//        JSONObject periodFootballScores = JSONObject.parseObject(scoresJson);
//        Map<Long, TennisScores> allPeriodScores = JsonMapUtils.parseTennisMap(periodFootballScores);
//
//        // 计算局比分
//        Map<Integer, CommonItem> setScore = Maps.newConcurrentMap();
//        for (Map.Entry<Long, TennisScores> entry : allPeriodScores.entrySet())
//        {
//            if (entry.getKey().equals(WHOLE_MATCH))
//            {
//                continue;
//            }
//            Integer setNumber = MatchPeriodUtils.getTennisSetByPeriod(entry.getKey());
//            if ( null == setNumber )
//            {
//                continue;
//            }
//            CommonItem commonItemVo = new CommonItem();
//            BeanUtils.copyProperties(entry.getValue().getSetScore(), commonItemVo);
//            setScore.put(setNumber, commonItemVo);
//        }
//
//        // 计算局内比分
//        TennisExtryScores tennisExtryScores;
//        if (StringUtils.isEmpty(scoresJsonExtra))
//        {
//            tennisExtryScores = new TennisExtryScores();
//        }
//        else
//        {
//            tennisExtryScores = JSONObject.toJavaObject((JSONObject.parseObject(scoresJsonExtra)), TennisExtryScores.class);
//        }
//        Map<Integer, Map<Integer, CommonItem>> currentScoresMap = tennisExtryScores.getCurrentScoresMap();
//        Map<String, Integer> secondMap = null;
//        // 根据不同的赛事阶段统计不同的比分
//        if ( null != Period ) {
//            // 第一盘
//            if ( "8".equals(Period.toString()) || "800".equals(Period.toString()) || "301".equals(Period.toString()) )
//            {
//                if ( !Objects.isNull(setScore.get(1)))
//                {
//                    firstT1 = setScore.get(1).getHome();
//                    firstT2 = setScore.get(1).getAway();
//                }
//                secondMap = statisticsRoundScore(linkId, 1, currentScoresMap);
//            }
//            //局盘切换的部分
//            else if ( "302".equals(Period.toString()) || "303".equals(Period.toString()) || "304".equals(Period.toString()) )
//            {
//                Integer tCurrentSet = currentSet -1 ;
//                if ( !Objects.isNull(setScore.get(tCurrentSet)))
//                {
//                    firstT1 = setScore.get(tCurrentSet).getHome();
//                    firstT2 = setScore.get(tCurrentSet).getAway();
//                }
//                secondMap = statisticsRoundScore(linkId, tCurrentSet, currentScoresMap);
//            }
//            else
//            {
//                if ( !Objects.isNull(setScore.get(currentSet)) )
//                {
//                    firstT1 = setScore.get(currentSet).getHome();
//                    firstT2 = setScore.get(currentSet).getAway();
//                }
//                secondMap = statisticsRoundScore(linkId, currentSet, currentScoresMap);
//            }
//            if (null != secondMap  && secondMap.size() > 0 ) {
//                secondT1 = secondMap.get("secondT1");
//                secondT2 = secondMap.get("secondT2");
//            }
//        }
//        System.out.printf("secondT1:" + secondT1);
//        System.out.printf("secondT2:" + secondT2);
//    }

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

    public static void main(String [] xX){
        System.out.println("KB数学实验 3门问题");
        Integer x =0;
        Integer y =10000;
        //z =0 不换盒子
        Integer z= 1;
        for(int J =1;J<=y;J++) {
            //1.随机合子
            List<String> l = new ArrayList<>();
            Random random = new Random();
            Integer r = random.nextInt(3) + 1;
            for (int i = 0; i <= 2; i++) {
                Integer index = i + 1;
                if (index == r) {
                    l.add("保时捷");
                } else {
                    l.add("山羊");
                }
            }
            //2.选择合子
            Random man = new Random();
            Integer xuanze = man.nextInt(3) + 1;
            //3.删除盒子
            Iterator<String> it = l.iterator();
            int i = 1;
            while (it.hasNext()) {
                String ost = it.next();
                if (i != xuanze && ost.equals("山羊")) {
                    //修正选择
                    if (xuanze > i) {
                        xuanze = xuanze - 1;
                    }
                    it.remove();
                    break;
                }
                i++;
            }
            //4.1 不换盒子
            System.out.println("");
            //4.2 换盒子
            if(z==1){
                if (xuanze != 1) {
                    xuanze = 1;
                } else {
                    xuanze = 2;
                }
            }
            if(l.get(xuanze-1).equals("保时捷")){
                x++;
            }
        }
        String xat = z==1?"换门":"不换门";
        System.out.println("三门问题试验"+xat+y+"次,中"+x+"次");
    }
}
