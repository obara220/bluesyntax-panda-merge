package com.panda.merge.advertise.utils;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.advertise.dto.TennisInitScoreVo;
import com.panda.merge.dto.CommonItem;
import com.panda.merge.dto.TennisExtryScores;
import com.panda.merge.dto.TennisScores;
import com.panda.merge.dto.advertise.TennisEditSecondScoreDto;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.utils.JsonMapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchScoreUtils {
    public static Map<Integer, List<Integer>> TENNIS_ROUND_TYPE_SCORES =new HashMap<>();
    static {
        //roundType=1 跳分
        Integer[] arr1= new Integer[]{0,15,30,40,50,60};
        List<Integer> list1=Arrays.asList(arr1);
        TENNIS_ROUND_TYPE_SCORES.put(1,list1);
        //roundType=2 跳分 7
        Integer[] arr2= new Integer[]{0,1,2,3,4,5,6,7};
        List<Integer> list2=Arrays.asList(arr2);
        TENNIS_ROUND_TYPE_SCORES.put(2,list2);
        //roundType=3 跳分 10
        Integer[] arr3= new  Integer[]{0,1,2,3,4,5,6,7,8,9,10};
        List<Integer> list3=Arrays.asList(arr3);
        TENNIS_ROUND_TYPE_SCORES.put(3,list3);
        //roundType=4 跳分 10
        Integer[] arr4= new Integer[]{0,1,2,3,4,5,6,7,8,9,10};
        List<Integer> list4=Arrays.asList(arr4);
        TENNIS_ROUND_TYPE_SCORES.put(4,list4);
        //roundType=5 跳分
        Integer[] arr5= new Integer[]{0,1,2,3,4,5,6,7};
        List<Integer> list5=Arrays.asList(arr5);
        TENNIS_ROUND_TYPE_SCORES.put(5,list5);
    }

    public static Map<Integer, List<Integer>> TENNIS_ROUND_MATCH_LENGTH =new HashMap<>();
    static {
        //roundType=1 跳分
        Integer[] arr1= new Integer[]{0,15,30,40,50,60};
        List<Integer> list1=Arrays.asList(arr1);
        TENNIS_ROUND_MATCH_LENGTH.put(1,list1);
        //roundType=2 单分
        Integer[] arr2= new Integer[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25};
        List<Integer> list2=Arrays.asList(arr2);
        TENNIS_ROUND_MATCH_LENGTH.put(2,list2);

    }

    /**
     * 单分 比分计算
     * response : 返回 0 无获胜 返回 1 主队获胜 返回 2 客队获胜
     * */
    public static Integer singleRoundWin(Integer t1,Integer t2,Integer matchLenth){
        //1长盘制, 2抢七制,3单人抢十,4双人抢十,5特
        //根据赛制，判断是否获胜， 当抢七 抢十的 时候先获得7分或者十分则获胜
        //抢七
        if(matchLenth==2||matchLenth==5){
            if(t1>=7 && (t1 -t2) >= 2  ){
               return 1;
            }
            if(t2>=7 && (t2 -t1) >= 2 ){
                return 2;
            }
            return 0;
        }
        //抢十
        if(matchLenth==3||matchLenth==4){
            if(t1>=10&& (t1 -t2) >= 2){
                return 1;
            }
            if(t2>=10 && (t2 -t1) >= 2){
                return 2;
            }
            return 0;
        }

        //长盘
        if(matchLenth.equals(1)){
            if(t1>=6&& (t1 -t2) >= 2){
                return 1;
            }
            if(t2>=6 && (t2 -t1) >= 2){
                return 2;
            }
            return 0;
        }
        return 0;
    }

    /**
     * 网球跳分 规则计算
     * response : 返回 0 无获胜 返回 1 主队获胜 返回 2 客队获胜
     * */
    public static Integer chargeRoundWin(Integer t1,Integer t2){

        //其他情况必须 比分差 20 而且 有球队>=40 分则获胜
        if(t1>=40||t2>=40){
            if(t1-t2>=20){
                return 1;
            }
            if(t2-t1>=20){
                return 2;
            }
        }
        return 0;
    }



    public static void reSetSecondScores(TennisEditSecondScoreDto tennisEditSecondScoreDto) {
        if(tennisEditSecondScoreDto.getT1()==60){
            tennisEditSecondScoreDto.setT1(50);
        }
        if(tennisEditSecondScoreDto.getT2()==60){
            tennisEditSecondScoreDto.setT2(50);
        }
    }

    public static Integer chargeSetWin(Integer currentSet, MatchScoresInfo matchScoresInfo, Integer roundType) {
        //五句三胜
        //
        return 0;
    }


    /**
     * 长盘获胜规则
     * response : 返回 0 无获胜 返回 1 主队获胜 返回 2 客队获胜
     * */
    public static Integer longRoundWin(Integer t1,Integer t2,Integer matchLenth){

        //其他情况必须 比分差 20 而且 有球队>=40 分则获胜
        if(t1>=40||t2>=40){
            if(t1-t2>=20){
                return 1;
            }
            if(t2-t1>=20){
                return 2;
            }
        }
        return 0;
    }

    public  static TennisInitScoreVo initTennisScore(Integer roundType,String  matchLengthJson){
        Map<Long, TennisScores> allPeriodScores =new HashMap<>();
        TennisExtryScores tennisExtryScores =new TennisExtryScores();
        JSONObject matchLengthJ= JSONObject.parseObject(matchLengthJson);
        for(Integer i= 1;i<=roundType;i++){
            Long periodId = MatchPeriodUtils.getTennisPeriodBySet(i);
            allPeriodScores.put(periodId,new TennisScores());
            Integer length = matchLengthJ.getInteger(i.toString());
            if (null == length) {
                continue;
            }
            Map<Integer, CommonItem> map = new HashMap<>();
            tennisExtryScores.getCurrentScoresMap().put(i,map);
            for (int j=1;j<=length;j++){
                CommonItem commonItem =map.get(j);
                if(commonItem==null){
                    commonItem =new CommonItem();
                    map.put(j,commonItem);
                }
            }
        }
        TennisInitScoreVo tennisInitScoreVo =new TennisInitScoreVo();
        tennisInitScoreVo.setAllPeriodScores(allPeriodScores);
        tennisInitScoreVo.setTennisExtryScores(tennisExtryScores);
        return tennisInitScoreVo;
    }

    /**
     * 组装比分 1.初始化 2.将数据组装到初始化
     * */
    public static void buildTennisScore(MatchScoresInfo matchScoresInfo,Integer roundType,String  periodLengthJson){
        TennisInitScoreVo tennisInitScoreVo = initTennisScore(roundType,periodLengthJson);
        TennisExtryScores tennisExtryScores;
        if (StringUtils.isEmpty(matchScoresInfo.getScoresJsonExtra())) {
            tennisExtryScores = new TennisExtryScores();
        } else {
            tennisExtryScores = JSONObject.toJavaObject((JSONObject.parseObject(matchScoresInfo.getScoresJsonExtra())), TennisExtryScores.class);
        }
        //总局比分 盘比分
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, TennisScores> allPeriodScores= JsonMapUtils.parseTennisMap(periodFootballScores);
        for (Map.Entry<Long, TennisScores> entry : allPeriodScores.entrySet()) {
            tennisInitScoreVo.getAllPeriodScores().put(entry.getKey(),entry.getValue());
        }
        for (Map.Entry<Integer, Map<Integer, CommonItem>> integerMapEntry : tennisExtryScores.getCurrentScoresMap().entrySet()) {
            Map<Integer, CommonItem> map=tennisInitScoreVo.getTennisExtryScores().getCurrentScoresMap().get(integerMapEntry.getKey());
            if(map!=null){
                for (Map.Entry<Integer, CommonItem> entry : integerMapEntry.getValue().entrySet()) {
                    map.put(entry.getKey(),entry.getValue());
                }
            }
        }
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(tennisInitScoreVo.getAllPeriodScores()));
        matchScoresInfo.setScoresJsonExtra(JSONObject.toJSONString(tennisInitScoreVo.getTennisExtryScores()));
    }

}
