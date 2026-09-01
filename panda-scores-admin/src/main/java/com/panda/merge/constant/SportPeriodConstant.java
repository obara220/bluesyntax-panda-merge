package com.panda.merge.constant;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Executable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class SportPeriodConstant {
    /**
     * 计时事件阶段
     */
    public static final List<Long> TIME_EVENT_PERIOD = Arrays.asList(6L, 7L, 41L, 42L, 31L, 32L, 33L);

    public static final Long matchEnd = 999L;

    public static abstract class SportPeriod  {
        //代表
        public static Long WHOLE_MATCH= -1L;

    }
    public static class FootballPeriod extends SportPeriod{
        public static Long   PENALTY_SHOOTOUT= 50L;
        public static Long[]  WHOLE_PERIODS ;
        static {
            WHOLE_PERIODS = new Long[]{6L, 7L, 41L, 42L, 50L};
        }
        public static Integer getIndexByPeriod(Long periodId) {
            for(int i=0;i<=WHOLE_PERIODS.length-1;i++){
                if(periodId.equals(WHOLE_PERIODS[i])){
                    return i;
                }
            }
            return -1;
        }
        public static boolean contans(Long periodId) {
            for(int i=0;i<=WHOLE_PERIODS.length-1;i++){
                if(periodId.equals(WHOLE_PERIODS[i])){
                    return true;
                }
            }
            return false;
        }
//      abPeriod.add(new MatchPeriodVO(60899L, "开场-14:59"));
//    	abPeriod.add(new MatchPeriodVO(61799L, "15:00-29:59"));
//    	abPeriod.add(new MatchPeriodVO(62699L, "30:00-半场"));
//    	abPeriod.add(new MatchPeriodVO(73599L, "下半场开始-59:59"));
//    	abPeriod.add(new MatchPeriodVO(74499L, "60:00-74:59"));
//    	abPeriod.add(new MatchPeriodVO(75399L, "75:00-全场"));

        public static Long get15MinPeriod(Long period,Long secondStart){
            //开局15分钟
            if(period==6&&secondStart<60*15){
                return 60899L;
            }
            //15分钟-30分钟
            if(period==6&&secondStart>=60*15&&secondStart<60*30){
                return 61799L;
            }
            //30分钟-上半场
            if(period==6&&secondStart>=60*30){
                return 62699L;
            }
            //下半场开始-59:59分钟
            if(period==7&&secondStart<60*60){
                return 73599L;
            }
            //下半场 60分钟-74:59
            if(period==7&&secondStart>=60*60&&secondStart<60*75){
                return 74499L;
            }
            //下半场 75分钟-全场
            if(period==7&&secondStart>=60*75){
                return 75399L;
            }
            return null;
        }

        public static String getAbPeriod(Long period) {
            if(period.equals(60899L)){
                return "1-15";
            }
            if(period.equals(61799L)){
                return "16-30";
            }
            if(period.equals(62699L)){
                return "31-45";
            }
            if(period.equals(73599L)){
                return "46-60";
            }
            if(period.equals(74499L)){
                return "61-75";
            }
            if(period.equals(75399L)){
                return "76-90";
            }
            return null;
        }
//上半场
//      abPeriod.add(new MatchPeriodVO(6005L, "开场-4:59"));
//      abPeriod.add(new MatchPeriodVO(6010L, "5:00 - 9:59"));
//      abPeriod.add(new MatchPeriodVO(6015L, "10:00 - 14:59"));
//      abPeriod.add(new MatchPeriodVO(6020L, "15:00 - 19:59	"));
//      abPeriod.add(new MatchPeriodVO(6025L, "20:00 - 24:59	"));
//      abPeriod.add(new MatchPeriodVO(6030L, "25:00 - 29:59	"));
//      abPeriod.add(new MatchPeriodVO(6035L, "30:00 - 34:59"));
//      abPeriod.add(new MatchPeriodVO(6040L, "35:00 - 39:59"));
//      abPeriod.add(new MatchPeriodVO(6045L, "40:00 - 45:00"));
//      abPeriod.add(new MatchPeriodVO(6050L, "1H Last-minute Goal"));

        //下半场
//      abPeriod.add(new MatchPeriodVO(7050L, "下半场 - 49:59"));
//      abPeriod.add(new MatchPeriodVO(7055L, "50:00 - 54:59"));
//      abPeriod.add(new MatchPeriodVO(7060L, "55:00 - 59:59"));
//      abPeriod.add(new MatchPeriodVO(7065L, "60:00 - 64:59"));
//      abPeriod.add(new MatchPeriodVO(7070L, "65:00 - 69:59"));
//      abPeriod.add(new MatchPeriodVO(7075L, "70:00 - 74:59"));
//      abPeriod.add(new MatchPeriodVO(7080L, "75:00 - 79:59"));
//      abPeriod.add(new MatchPeriodVO(7085L, "80:00 - 84:59"));
//      abPeriod.add(new MatchPeriodVO(7090L, "85:00 - 90:00"));
//      abPeriod.add(new MatchPeriodVO(7095L, "2H Last-minute Goal"));





        //5分钟 阶段计算
        public static Long get5MinPeriod(Long period,Long secondStart){
            //开场-4:59
            if(period==6&&secondStart<60*5){
                return 6005L;
            }
            //5:00 - 9:59
            if(period==6&&secondStart>=60*5&&secondStart<60*10){
                return 6010L;
            }
            //10:00 - 14:59
            if(period==6&&secondStart>=60*10&&secondStart<60*15){
                return 6015L;
            }
            //15:00 - 19:59
            if(period==6&&secondStart>=60*15&&secondStart<60*20){
                return 6020L;
            }
            //20:00 - 24:59
            if(period==6&&secondStart>=60*20&&secondStart<60*25){
                return 6025L;
            }
            //25:00 - 29:59
            if(period==6&&secondStart>=60*25&&secondStart<60*30){
                return 6030L;
            }
            //30:00 - 34:59
            if(period==6&&secondStart>=60*30&&secondStart<60*35){
                return 6035L;
            }
            //35:00 - 39:59
            if(period==6&&secondStart>=60*35&&secondStart<60*40){
                return 6040L;
            }
            //40:00 - 45:00
            if(period==6&&secondStart>=60*40&&secondStart<60*45){
                return 6045L;
            }
            //1H Last-minute Goal
            if(period==6&&secondStart>60*45){
                return 6050L;
            }

            //下半场- 49:59
            if(period==7&&secondStart<60*50){
                return 7050L;
            }
            //50:00 - 54:59
            if(period==7&&secondStart>=60*50&&secondStart<60*55){
                return 7055L;
            }
            //55:00 - 59:59
            if(period==7&&secondStart>=60*55&&secondStart<60*60){
                return 7060L;
            }
            //60:00 - 64:59
            if(period==7&&secondStart>=60*60&&secondStart<60*65){
                return 7065L;
            }
            //65:00 - 69:59
            if(period==7&&secondStart>=60*65&&secondStart<60*70){
                return 7070L;
            }
            //70:00 - 74:59
            if(period==7&&secondStart>=60*70&&secondStart<60*75){
                return 7075L;
            }
            //75:00 - 79:59
            if(period==7&&secondStart>=60*75&&secondStart<60*80){
                return 7080L;
            }
            //80:00 - 84:59
            if(period==7&&secondStart>=60*80&&secondStart<60*85){
                return 7085L;
            }
            //85:00 - 90:00
            if(period==7&&secondStart>=60*85&&secondStart<60*90){
                return 7090L;
            }
            //2H Last-minute Goal
            if(period==7&&secondStart>60*90){
                return 7095L;
            }
            if(period==50){
                return 50L;
            }

            return null;
        }
        public static Long period_6 = 6L;
        public static Long period_7 = 7L;
        public static Long period_31 = 31L;
        public static Long period_41 = 41L;
        public static Long period_42 = 42L;
        public static Long period_110 = 110L;
        public static Long period_50 = 50L;
        public static Long period_100 = 100L;
        public static Long period_60899 = 60899L;
        public static Long period_61799 = 61799L;
        public static Long period_62699 = 62699L;
        public static Long period_73599 = 73599L;
        public static Long period_74499 = 74499L;
        public static Long period_75399 = 75399L;
        public static Long period_6005 = 6005L;
        public static Long period_6010 = 6010L;
        public static Long period_6015 = 6015L;
        public static Long period_6020 = 6020L;
        public static Long period_6025 = 6025L;
        public static Long period_6030 = 6030L;
        public static Long period_6035 = 6035L;
        public static Long period_6040 = 6040L;
        public static Long period_6045 = 6045L;
        public static Long period_6050 = 6050L;
        public static Long period_7050 = 7050L;
        public static Long period_7055 = 7055L;
        public static Long period_7060 = 7060L;
        public static Long period_7065 = 7065L;
        public static Long period_7070 = 7070L;
        public static Long period_7075 = 7075L;
        public static Long period_7080 = 7080L;
        public static Long period_7085 = 7085L;
        public static Long period_7090 = 7090L;
        public static Long period_7095 = 7095L;

    }

    public static class BasketballPeriod extends SportPeriod {
        public static Long[]  period0={13L,14L, 15L,16L,40L};
        public static Long[]  period20={1L, 2L,40L};
        public static Long[]  period73={21L};
        public static Long[] getWholePeriodsByMatchLength(Integer matchLength){
            if(matchLength!=null&&matchLength==17){
                return new Long[]{1L, 2L,40L};
            }else if(matchLength!=null&&matchLength==73){
                return  new Long[]{21L,40L};
            }
            else {
                return  new Long[]{1L, 2L,13L,14L, 15L,16L,40L, 80L};
            }
        }
        public static boolean contans(Long periodId,Integer matchLength) {
            Long[] x=getWholePeriodsByMatchLength(matchLength);
            for(int i=0;i<=x.length-1;i++){
                if(periodId.equals(x[i])){
                    return true;
                }
            }
            return false;
        }

        /**
         * 判断当前篮球赛事的阶段处于上半场还是下半场，用于获取半场比分
         * ps:两节制赛事不在这里获取，外层直接保存了阶段1、2的数据，这里不再重复。
         * @param periodId
         * @return
         */
        public static Long getHalfPeriods(Long periodId) {
            Long index = -1L;
            List<Long> hfPeriods = Arrays.asList(13L,14L);
            List<Long> ftPeriods = Arrays.asList(15L,16L);
            if(hfPeriods.contains(periodId)){
                return 1L;
            }else if(ftPeriods.contains(periodId)){
                return 2L;
            }
            return index;
        }
        public static Integer getIndexByPeriod(Long periodId,Integer matchLength) {
            if(matchLength==17){
                for(int i=0;i<=period20.length-1;i++){
                    if(periodId.equals(period20[i])){
                        return i;
                    }
                }
                return -1;
            }else {
                for(int i=0;i<=period0.length-1;i++){
                    if(periodId.equals(period0[i])){
                        return i;
                    }
                }
                return -1;
            }
        }
        /**
         * 获取当前阶段的下一个开打的阶段的方法
         * */
        public static Long getNextPeriod( Long matchPeriodId,Integer matchLength) {
            Integer index=getIndexByPeriod(matchPeriodId,matchLength);
            if(index==-1){
                return null;
            }
            Integer nextIndex=index+1;
            if(matchLength==17){
                if(period20.length-1<nextIndex){
                    return null;
                }else {
                    return period20[nextIndex];
                }
            }else {
                if(period0.length-1<nextIndex){
                    return null;
                }else {
                    return period0[nextIndex];
                }
            }
        }
        /**
         * 根据阶段和赛事进行时长,获取当前篮球6分钟区间
         * @param period 阶段
         * @param secondStart 时长
         * @return
         */
        public static Long get6MinPeriod(Long period,Long secondStart){
            //12分钟制 720秒
            Long twelveMin = 720L;
            Long sixMin = twelveMin - secondStart;
            //篮球比赛进行时长发的是倒计时，倒叙下发
            if(period==13&&sixMin<60*6){
                return 1312L;
            }else if(period==13&&sixMin<=60*12){
                return 1306L;
            }else if(period==14&&sixMin<60*6){
                return 1412L;
            }else if(period==14&&sixMin<=60*12){
                return 1406L;
            }else if(period==15&&sixMin<60*6){
                return 1512L;
            }else if(period==15&&sixMin<=60*12){
                return 1506L;
            }else if(period==16&&sixMin<60*6){
                return 1612L;
            }else if(period==16&&sixMin<=60*12){
                return 1606L;
            }
            return null;
        }

        public static void main(String[] args) {
            System.out.println(get6MinPeriod(14L,448L));
        }
    }
    public static class TennisPeriod extends SportPeriod {
        public static Long[] getWholePeriodsByMatchLength(){
            return  new Long[]{8L, 9L, 10L, 11L, 12L};
        }
        public static boolean contans(Long periodId) {
            Long[] x=getWholePeriodsByMatchLength();
            for(int i=0;i<=x.length-1;i++){
                if(periodId.equals(x[i])){
                    return true;
                }
            }
            return false;
        }
    }

    public static class TableTennisPeriod extends SportPeriod {
        public static Long[] getWholePeriodsByMatchLength(){
            return  new Long[]{8L, 9L, 10L, 11L, 12L, 441L, 442L};
        }
        public static boolean contans(Long periodId) {
            Long[] x=getWholePeriodsByMatchLength();
            for(int i=0;i<=x.length-1;i++){
                if(periodId.equals(x[i])){
                    return true;
                }
            }
            return false;
        }
    }
    public static class CricketPeriod extends SportPeriod {
        public static Long[] getWholePeriodsByMatchLength(){
            return  new Long[]{8L, 9L};
        }
        public static boolean contans(Long periodId) {
            Long[] x=getWholePeriodsByMatchLength();
            for(int i=0;i<=x.length-1;i++){
                if(periodId.equals(x[i])){
                    return true;
                }
            }
            return false;
        }
    }


    public static class VolleyballPeriod extends SportPeriod {
        public static Long[] getWholePeriodsByMatchLength(){
            return  new Long[]{8L, 9L, 10L, 11L, 12L, 441L, 442L};
        }
        public static boolean contans(Long periodId) {
            Long[] x=getWholePeriodsByMatchLength();
            for(int i=0;i<=x.length-1;i++){
                if(periodId.equals(x[i])){
                    return true;
                }
            }
            return false;
        }
    }

    public static class BadmintonPeriod extends SportPeriod {
        public static Long[] getWholePeriodsByMatchLength(){

            return  new Long[]{8L, 9L, 10L, 11L, 12L};
        }
        public static boolean contans(Long periodId) {
            Long[] x=getWholePeriodsByMatchLength();
            for(int i=0;i<=x.length-1;i++){
                if(periodId.equals(x[i])){
                    return true;
                }
            }
            return false;
        }
    }

    public static class AmericanFootballPeriod extends SportPeriod{
        public static Long[]  WHOLE_PERIODS ;
        static {
            WHOLE_PERIODS = new Long[]{13L, 14L, 15L, 16L,40L};
        }
        public static Integer getIndexByPeriod(Long periodId) {
            for(int i=0;i<=WHOLE_PERIODS.length-1;i++){
                if(periodId.equals(WHOLE_PERIODS[i])){
                    return i;
                }
            }
            return -1;
        }
        public static boolean contans(Long periodId) {
            for(int i=0;i<=WHOLE_PERIODS.length-1;i++){
                if(periodId.equals(WHOLE_PERIODS[i])){
                    return true;
                }
            }
            return false;
        }
    }

    public static class BaseballPeriod extends SportPeriod{
        public static Long[]  WHOLE_PERIODS ;
        static {
            WHOLE_PERIODS = new Long[]{ 401L,  402L, 403L,  404L,  405L, 406L,  407L,  408L, 409L, 410L,  411L, 412L,  413L,
                    414L,  415L, 416L, 417L,  418L,  41910L, 42010L, 41911L,42011L,  41912L,42012L,  41913L,42013L,
                    41914L,42014L,  41915L,42015L,  41916L,42016L,  41917L, 42017L, 41918L,42018L,  41919L,42019L,
                    41920L, 42020L,419L,420L};
        }
        public static Integer getIndexByPeriod(Long periodId) {
            for(int i=0;i<=WHOLE_PERIODS.length-1;i++){
                if(periodId.equals(WHOLE_PERIODS[i])){
                    return i;
                }
            }
            return -1;
        }
        public static boolean contans(Long periodId) {
            for(int i=0;i<=WHOLE_PERIODS.length-1;i++){
                if(periodId.equals(WHOLE_PERIODS[i])){
                    return true;
                }
            }
            return false;
        }
    }
    public static class IceHockeyPeriod extends SportPeriod{
        public static Long[]  WHOLE_PERIODS ;
        static {
            WHOLE_PERIODS = new Long[]{1L,  2L,  3L,40L,50L,120L};
        }
        public static Integer getIndexByPeriod(Long periodId) {
            for(int i=0;i<=WHOLE_PERIODS.length-1;i++){
                if(periodId.equals(WHOLE_PERIODS[i])){
                    return i;
                }
            }
            return -1;
        }
        public static boolean contans(Long periodId) {
            for(int i=0;i<=WHOLE_PERIODS.length-1;i++){
                if(periodId.equals(WHOLE_PERIODS[i])){
                    return true;
                }
            }
            return false;
        }
    }
    public static class HandBallPeriod extends SportPeriod{
        public static Long[]  WHOLE_PERIODS ;
        static {
            WHOLE_PERIODS = new Long[]{6L,7L,41l,42l,50l};
        }
        public static Integer getIndexByPeriod(Long periodId) {
            for(int i=0;i<=WHOLE_PERIODS.length-1;i++){
                if(periodId.equals(WHOLE_PERIODS[i])){
                    return i;
                }
            }
            return -1;
        }
        public static boolean contans(Long periodId) {
            for(int i=0;i<=WHOLE_PERIODS.length-1;i++){
                if(periodId.equals(WHOLE_PERIODS[i])){
                    return true;
                }
            }
            return false;
        }
    }
    public static class BeachVolleyballPeriod extends SportPeriod{
        public static Long[]  WHOLE_PERIODS ;
        static {
            WHOLE_PERIODS = new Long[]{8L,9L,10L,11L,12L};
        }
        public static Integer getIndexByPeriod(Long periodId) {
            for(int i=0;i<=WHOLE_PERIODS.length-1;i++){
                if(periodId.equals(WHOLE_PERIODS[i])){
                    return i;
                }
            }
            return -1;
        }
        public static boolean contans(Long periodId) {
            for(int i=0;i<=WHOLE_PERIODS.length-1;i++){
                if(periodId.equals(WHOLE_PERIODS[i])){
                    return true;
                }
            }
            return false;
        }
    }
    public static class HockeyPeriod {
        public static Long[]  WHOLE_PERIODS ;
        static {
            WHOLE_PERIODS = new Long[]{6L,7L,13L,14L,15L,16L,440L,50L};
        }
        public static Integer getIndexByPeriod(Long periodId) {
            for(int i=0;i<=WHOLE_PERIODS.length-1;i++){
                if(periodId.equals(WHOLE_PERIODS[i])){
                    return i;
                }
            }
            return -1;
        }
        public static boolean contans(Long periodId) {
            for(int i=0;i<=WHOLE_PERIODS.length-1;i++){
                if(periodId.equals(WHOLE_PERIODS[i])){
                    return true;
                }
            }
            return false;
        }
    }

    public static class WaterballPeriod {
        public static Long[]  WHOLE_PERIODS ;
        static {
            WHOLE_PERIODS = new Long[]{13L,14L,15L,16L,50L};
        }
        public static Integer getIndexByPeriod(Long periodId) {
            for(int i=0;i<=WHOLE_PERIODS.length-1;i++){
                if(periodId.equals(WHOLE_PERIODS[i])){
                    return i;
                }
            }
            return -1;
        }
        public static boolean contans(Long periodId) {
            for(int i=0;i<=WHOLE_PERIODS.length-1;i++){
                if(periodId.equals(WHOLE_PERIODS[i])){
                    return true;
                }
            }
            return false;
        }
    }


    public static class UKFootballPeriod {
        public static Long[]  WHOLE_PERIODS ;
        static {
            WHOLE_PERIODS = new Long[]{6L,7L,41L,42L,50L};
        }
        public static Integer getIndexByPeriod(Long periodId) {
            for(int i=0;i<=WHOLE_PERIODS.length-1;i++){
                if(periodId.equals(WHOLE_PERIODS[i])){
                    return i;
                }
            }
            return -1;
        }
        public static boolean contans(Long periodId) {
            for(int i=0;i<=WHOLE_PERIODS.length-1;i++){
                if(periodId.equals(WHOLE_PERIODS[i])){
                    return true;
                }
            }
            return false;
        }
    }

    public static class SnookerPeriod extends SportPeriod {
        public static Map<Long,Long> periodMaps = new HashMap<>();
        static {
            periodMaps.put(301L,8L);
            periodMaps.put(302L,9L);
            periodMaps.put(303L,10L);
            periodMaps.put(304L,11L);
            periodMaps.put(305L,12L);
            periodMaps.put(306L,441L);
            periodMaps.put(307L,442L);
            periodMaps.put(308L,500L);
            periodMaps.put(309L,501L);
            periodMaps.put(310L,502L);
            periodMaps.put(311L,503L);
            periodMaps.put(312L,504L);
            periodMaps.put(313L,505L);
            periodMaps.put(314L,506L);
            periodMaps.put(315L,507L);
            periodMaps.put(316L,508L);
            periodMaps.put(317L,509L);
            periodMaps.put(318L,510L);
            periodMaps.put(319L,511L);
            periodMaps.put(320L,512L);
            periodMaps.put(321L,513L);
            periodMaps.put(322L,514L);
            periodMaps.put(323L,515L);
            periodMaps.put(324L,516L);
            periodMaps.put(325L,517L);
            periodMaps.put(326L,518L);
            periodMaps.put(327L,519L);
            periodMaps.put(328L,520L);
            periodMaps.put(329L,521L);
            periodMaps.put(330L,522L);
            periodMaps.put(331L,523L);
            periodMaps.put(332L,524L);
            periodMaps.put(333L,525L);
            periodMaps.put(334L,526L);
            periodMaps.put(335L,527L);
        }
        public static Long[]  WHOLE_PERIODS ;
        static {
            WHOLE_PERIODS = new Long[]{8L,9L,10L,11L,12L,441L,442L,500L,501L,502L,503L,504L,505L,506L,507L,508L,509L,510L,511L,512L,513L,514L,515L,516L,517L,518L,519L,520L,521L,522L,523L,524L,525L,526L,527L};
        }

        public static class SnookerPeriodScores extends SportPeriod {
            public static Map<Long, Long> periodMaps = new HashMap<>();
            static {
                periodMaps.put(8L, 1L);
                periodMaps.put(9L, 2L);
                periodMaps.put(10L, 3L);
                periodMaps.put(11L, 4L);
                periodMaps.put(12L, 5L);
                periodMaps.put(441L, 6L);
                periodMaps.put(442L, 7L);
                periodMaps.put(500L, 8L);
                periodMaps.put(501L, 9L);
                periodMaps.put(502L, 10L);
                periodMaps.put(503L, 11L);
                periodMaps.put(504L, 12L);
                periodMaps.put(505L, 13L);
                periodMaps.put(506L, 14L);
                periodMaps.put(507L, 15L);
                periodMaps.put(508L, 16L);
                periodMaps.put(509L, 17L);
                periodMaps.put(510L, 18L);
                periodMaps.put(511L, 19L);
                periodMaps.put(512L, 20L);
                periodMaps.put(513L, 21L);
                periodMaps.put(514L, 22L);
                periodMaps.put(515L, 23L);
                periodMaps.put(516L, 24L);
                periodMaps.put(517L, 25L);
                periodMaps.put(518L, 26L);
                periodMaps.put(519L, 27L);
                periodMaps.put(520L, 28L);
                periodMaps.put(521L, 29L);
                periodMaps.put(522L, 30L);
                periodMaps.put(523L, 31L);
                periodMaps.put(524L, 32L);
                periodMaps.put(525L, 33L);
                periodMaps.put(526L, 34L);
                periodMaps.put(527L, 35L);
                periodMaps.put(301L, 1L);
                periodMaps.put(302L, 2L);
                periodMaps.put(303L, 3L);
                periodMaps.put(304L, 4L);
                periodMaps.put(305L, 5L);
                periodMaps.put(306L, 6L);
                periodMaps.put(307L, 7L);
            }
        }
        /**
         * 斯诺克阶段转换
         * 将结束阶段映射到开始阶段，保持比分下发的阶段是一致的
         * @param periodId
         * @return
         */
        public static Long getSnookerPeriod(Long periodId) {
            Object period = periodMaps.get(periodId);
            if(period!=null){
                return Long.valueOf(period+"");
            }
            return periodId;
        }

        /**
         * 获取阶段对应的下标，与原斯诺克比分结构保持一致
         * @param periodId
         * @return
         */
        public static Long getIndexByPeriod(Long periodId) {
            for(int i=0;i<=WHOLE_PERIODS.length-1;i++){
                if(periodId.equals(WHOLE_PERIODS[i])){
                    return i+1L;
                }
            }
            return periodId;
        }

        public static Long getPeriodByIndex(int index) {
            try{
                if(index < 0 || index > 35) {
                    return (long)index;
                }
                return WHOLE_PERIODS[index-1];
            } catch(Exception e) {
                log.info("snooker getPeriodByIndex error: ", e);
            }
            return (long) index;
        }

        public static Long getSnookerScorePeriod(Long periodId) {
            return getIndexByPeriod(getSnookerPeriod(periodId));
        }
    }

}
