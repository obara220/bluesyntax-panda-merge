package com.panda.merge.constant;

import java.util.Arrays;
import java.util.List;

public class SportPeriodConstant {
    /**
     * 计时事件阶段
     */
    public static final List<Long> TIME_EVENT_PERIOD = Arrays.asList(6L, 7L, 41L, 42L, 31L, 32L, 33L);

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
                    41920L, 42020L};
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
}
