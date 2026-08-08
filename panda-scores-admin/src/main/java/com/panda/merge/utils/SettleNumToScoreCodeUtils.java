package com.panda.merge.utils;

import com.panda.merge.dto.ScoreCodeDto;
import com.panda.merge.model.MatchSettleEvent;
import com.panda.merge.model.MatchSettleScore;

import java.util.HashMap;
import java.util.Map;

/**
 * 结算中心比分类
 */
public class SettleNumToScoreCodeUtils {
    /**
     * 获取事件编码
     * @param matchSettleEvent
     * @return
     */
    public static String getScoreCodeBySettleEvent(MatchSettleEvent matchSettleEvent){
        if(matchSettleEvent.getSportId().equals(1L)){
            return getFootballScoreCodeByEvent(matchSettleEvent);
        }
        return null;
    }
    /**
     * 足球结算事件转化为业务的比分
     * */
    private static String getFootballScoreCodeByEvent(MatchSettleEvent matchSettleEvent) {
        //次序事件 第几轮点球比分 罚牌类 的无罚牌玩法 下发结算 黄牌 红牌比分

        //点球 1~9轮  S1701 ~S1709
        if(matchSettleEvent.getSettleNum().equals("1030")){
            return "S170"+matchSettleEvent.getFirstNum();
        }
        //点球总比分
        if(matchSettleEvent.getSettleNum().equals("1028")){
            return "S170";
        }
        //点球前5轮比分
        if(matchSettleEvent.getSettleNum().equals("1029")){
            return "S17005";
        }
        return null;
    }

    /**
     * 红牌事件总次数
     * @param matchSettleEvent
     * @return
     */
    public static ScoreCodeDto countSettleRedCardEvent(MatchSettleEvent matchSettleEvent){
        if(("fa_card".equals(matchSettleEvent.getEventCode()) || "red_card".equals(matchSettleEvent.getEventCode()) )
                && !matchSettleEvent.getHomeAway().equals("none")){
            //红牌 second
            Integer secondT1 = matchSettleEvent.getSecondT1();
            Integer secondT2 = matchSettleEvent.getSecondT2();

            ScoreCodeDto scoreCodeDto =new ScoreCodeDto();
            scoreCodeDto.setT1(secondT1);
            scoreCodeDto.setT2(secondT2);
            //上半场红牌
            if(matchSettleEvent.getPeriodId().equals(6L)){
                scoreCodeDto.setScoreCode("S13");
            }
            //下半场是 全场常规赛黄牌
            if(matchSettleEvent.getPeriodId().equals(7L)){
                scoreCodeDto.setScoreCode("S11001");
            }
//            //加时赛上半场
//            if(matchSettleEvent.getPeriodId().equals(41L)){
//                scoreCodeDto.setScoreCode("S14");
//            }
//            //加时赛下半场= 加时赛全场黄牌
//            if(matchSettleEvent.getPeriodId().equals(7L)){
//                scoreCodeDto.setScoreCode("S12001");
//            }
            if(scoreCodeDto.getScoreCode()==null){
                return null;
            }
            return scoreCodeDto;
        }else {
            return null;
        }
    }

    /**
     * 黄牌事件总次数
     * @param matchSettleEvent
     * @return
     */
    public static ScoreCodeDto countSettleYellowCardEvent(MatchSettleEvent matchSettleEvent){
        if(("fa_card".equals(matchSettleEvent.getEventCode()) || "yellow_card".equals(matchSettleEvent.getEventCode()) )
                && !matchSettleEvent.getHomeAway().equals("none")){
            //黄牌
            Integer xFirstT1 = matchSettleEvent.getFirstT1();
            Integer xFirstT2 = matchSettleEvent.getFirstT2();

            ScoreCodeDto scoreCodeDto =new ScoreCodeDto();
            scoreCodeDto.setT1(xFirstT1);
            scoreCodeDto.setT2(xFirstT2);
            //上半场黄牌
            if(matchSettleEvent.getPeriodId().equals(6L)){
                scoreCodeDto.setScoreCode("S14");
            }
            //下半场是 全场常规赛黄牌
            if(matchSettleEvent.getPeriodId().equals(7L)){
                scoreCodeDto.setScoreCode("S12001");
            }
//            //加时赛上半场
//            if(matchSettleEvent.getPeriodId().equals(41L)){
//                scoreCodeDto.setScoreCode("S14");
//            }
//            //加时赛下半场= 加时赛全场黄牌
//            if(matchSettleEvent.getPeriodId().equals(7L)){
//                scoreCodeDto.setScoreCode("S12001");
//            }
            if(scoreCodeDto.getScoreCode()==null){
                return null;
            }
            return scoreCodeDto;
        }else {
            return null;
        }
    }



    public static ScoreCodeDto getBasketballFirstScore(MatchSettleEvent matchSettleEvent){
        if(matchSettleEvent.getEventCode().equals("score_change") && matchSettleEvent.getHomeAway().equals("none")) {
            //红牌 second
            Integer secondT1 = matchSettleEvent.getSecondT1();
            Integer secondT2 = matchSettleEvent.getSecondT2();

            ScoreCodeDto scoreCodeDto =new ScoreCodeDto();
            scoreCodeDto.setT1(secondT1);
            scoreCodeDto.setT2(secondT2);
            //上半场红牌
            if(matchSettleEvent.getPeriodId().equals(6L)){
                scoreCodeDto.setScoreCode("S13");
            }

            if(scoreCodeDto.getScoreCode()==null){
                return null;
            }
            return scoreCodeDto;
        }else {
            return null;
        }
    }


    /**
     * 获取比分编码
     * @param matchSettleScore
     * @return
     */
    public static String getScoreCodeBySettleScore(MatchSettleScore matchSettleScore){
            if(matchSettleScore.getSportId().equals(1L)){
                return getFootballScoreCodebyScore(matchSettleScore);
            }else if(matchSettleScore.getSportId().equals(2L)){
                return getBasketballScoreCodebyScore(matchSettleScore);
            }else {
                return null;
            }
    }

    private static String getBasketballScoreCodebyScore(MatchSettleScore matchSettleScore) {
        return basketballScoreNumToScoreMap.get(matchSettleScore.getSettleNum());
    }


    /**
     * 足球结算比分转化为业务的比分
     * */
    private static String getFootballScoreCodebyScore(MatchSettleScore ms) {
        //根据 结算编码 下发 罚牌比分 进球比分 角球比分 点球比分
        //15分钟比分
        return scoreNumToScoreMap.get(ms.getSettleNum());
    }

    /**
     * 足球结算编码匹配业务比分编码
     */
    static Map<String,String> scoreNumToScoreMap =new HashMap<>();
    static {
        //15分钟进球 上半场
        scoreNumToScoreMap.put("102","S1001");
        scoreNumToScoreMap.put("103","S1002");
        scoreNumToScoreMap.put("104","S1003");
        //15分钟进球 下半场
        scoreNumToScoreMap.put("106","S1004");
        scoreNumToScoreMap.put("107","S1005");
        scoreNumToScoreMap.put("108","S1006");
        //5 分钟 上半场 进球
        scoreNumToScoreMap.put("1034","S10011");
        scoreNumToScoreMap.put("1035","S10012");
        scoreNumToScoreMap.put("1036","S10013");
        scoreNumToScoreMap.put("1037","S10021");
        scoreNumToScoreMap.put("1038","S10022");
        scoreNumToScoreMap.put("1039","S10023");
        scoreNumToScoreMap.put("1040","S10031");
        scoreNumToScoreMap.put("1041","S10032");
        scoreNumToScoreMap.put("1042","S10033");
        scoreNumToScoreMap.put("1043","S10034");
        //5分钟下半场进球
        scoreNumToScoreMap.put("1044","S10041");
        scoreNumToScoreMap.put("1045","S10042");
        scoreNumToScoreMap.put("1046","S10043");
        scoreNumToScoreMap.put("1047","S10051");
        scoreNumToScoreMap.put("1048","S10052");
        scoreNumToScoreMap.put("1049","S10053");
        scoreNumToScoreMap.put("1050","S10061");
        scoreNumToScoreMap.put("1051","S10062");
        scoreNumToScoreMap.put("1052","S10063");
        scoreNumToScoreMap.put("1053","S10064");
        //全场
        scoreNumToScoreMap.put("1010","S1");
        //上半场 进球
        scoreNumToScoreMap.put("105","S2");
        //下半场进球
        scoreNumToScoreMap.put("109","S3");
        //角球全场
        scoreNumToScoreMap.put("203","S555");


        //角球15分钟
        scoreNumToScoreMap.put("2011","S5001");
        scoreNumToScoreMap.put("2012","S5002");
        scoreNumToScoreMap.put("2013","S5003");
        scoreNumToScoreMap.put("2014","S5004");
        scoreNumToScoreMap.put("2015","S5005");
        scoreNumToScoreMap.put("2016","S5006");
        //S15 S16 角球上半场 下半场
        scoreNumToScoreMap.put("201","S15");
        scoreNumToScoreMap.put("202","S16");
        //角球加时
        scoreNumToScoreMap.put("208","S500");
        //角球加时 上下半场
        scoreNumToScoreMap.put("206","S501");
        scoreNumToScoreMap.put("207","S502");
        //加时赛比分
        scoreNumToScoreMap.put("1019","S7");
        //加时赛上下半场
        scoreNumToScoreMap.put("1014","S701");
        scoreNumToScoreMap.put("1018","S702");
        //罚牌 比分
//        scoreNumToScoreMap.put("1018","S10101");
        //罚牌 常规赛比分
        scoreNumToScoreMap.put("309","S10102");
        scoreNumToScoreMap.put("3041","S13");
        //罚牌上半场
        scoreNumToScoreMap.put("304","S10103");
        //罚牌下半场
        scoreNumToScoreMap.put("308","S10104");
        //15分钟罚牌
        scoreNumToScoreMap.put("301","S50011");
        scoreNumToScoreMap.put("302","S50012");
        scoreNumToScoreMap.put("303","S50013");
        scoreNumToScoreMap.put("305","S50014");
        scoreNumToScoreMap.put("306","S50015");
        scoreNumToScoreMap.put("307","S50016");

        //加时赛罚牌比分
        scoreNumToScoreMap.put("3018","S10105");
        scoreNumToScoreMap.put("3022","S10106");
        scoreNumToScoreMap.put("3023","S10107");
        //避免差错 新加文档结算编码对应
        scoreNumToScoreMap.put("3013","S10106");
        scoreNumToScoreMap.put("3017","S10107");
        scoreNumToScoreMap.put("3014","S11001");

    }
    static Map<String,String> basketballScoreNumToScoreMap =new HashMap<>();
    static {
        // 第一节阶段
        basketballScoreNumToScoreMap.put("bk_q104", "S19");
        // 第二节阶段
        basketballScoreNumToScoreMap.put("bk_q204", "S20");
        // 上半场阶段(Q1+Q2)
        basketballScoreNumToScoreMap.put("bk_1ht", "S2");
        // 第三节阶段
        basketballScoreNumToScoreMap.put("bk_q304", "S21");
        // 第四节阶段
        basketballScoreNumToScoreMap.put("bk_q404", "S22");
        // 下半场阶段(Q3+Q4)
        basketballScoreNumToScoreMap.put("bk_2ht", "S3");
        // OT阶段
        basketballScoreNumToScoreMap.put("bk_et", "S7");
        // 下半场阶段(Q3+Q4+OT)
        basketballScoreNumToScoreMap.put("bk_2htet", "S307");
        // 全场(常规赛)
        basketballScoreNumToScoreMap.put("bk_ft_rg", "S1111");
        // 全场含加时
        basketballScoreNumToScoreMap.put("bk_ft_et", "S1");
    }
    public static Integer getFootballSettleScoreIndex(String settleNum) {
        return footballSettleResultIndex.get(settleNum);
    }
    static Map<String,Integer> footballSettleResultIndex =new HashMap<>();
    static {
        //全场进球 //上半场 进球//下半场进球//加时赛进球
        footballSettleResultIndex.put("S1",15);
        footballSettleResultIndex.put("S2",13);
        footballSettleResultIndex.put("S3",14);
        footballSettleResultIndex.put("S7",16);
        //S15 S16 角球上半场、下半场、全场//角球加时
        footballSettleResultIndex.put("S15",9);
        footballSettleResultIndex.put("S16",10);
        footballSettleResultIndex.put("S555",11);
        footballSettleResultIndex.put("S500",12);
        footballSettleResultIndex.put("S170",18);

//        footballSettleResultIndex.put("S14",1);
//        footballSettleResultIndex.put("S1402",2);
//        footballSettleResultIndex.put("S12001",3);
//        footballSettleResultIndex.put("S506",4);
//        footballSettleResultIndex.put("S13",5);
//        footballSettleResultIndex.put("S1302",6);
//        footballSettleResultIndex.put("S11001",7);
//        footballSettleResultIndex.put("S503",8);

    }
    public static Integer getBasketSettleScoreIndex(String settleNum) {
        return basketSettleScoreIndex.get(settleNum);
    }
    static Map<String,Integer> basketSettleScoreIndex =new HashMap<>();
    static {
        basketSettleScoreIndex.put("S1",-1);
        basketSettleScoreIndex.put("S2",1);
        basketSettleScoreIndex.put("S3",2);
        basketSettleScoreIndex.put("S7",40);
        basketSettleScoreIndex.put("S19",13);
        basketSettleScoreIndex.put("S20",14);
        basketSettleScoreIndex.put("S21",15);
        basketSettleScoreIndex.put("S22",16);
        basketSettleScoreIndex.put("S307",307);
        basketSettleScoreIndex.put("S1111",100);
    }
}
