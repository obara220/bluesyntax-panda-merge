package com.panda.merge.constant;

import org.apache.commons.lang3.StringUtils;

public class SportPeriodConstant {

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
            //60分钟-74:59
            if(period==7&&secondStart>=60*60&&secondStart<60*75){
                return 74499L;
            }
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

        //5分钟 阶段计算
        public static String get5MinCode(Long period, Long secondStart){
            //开场-4:59
            if(period==6&&secondStart<60*5){
                return "5";
            }
            //5:00 - 9:59
            if(period==6&&secondStart>=60*5&&secondStart<60*10){
                return "10";
            }
            //10:00 - 14:59
            if(period==6&&secondStart>=60*10&&secondStart<60*15){
                return "15";
            }
            //15:00 - 19:59
            if(period==6&&secondStart>=60*15&&secondStart<60*20){
                return "20";
            }
            //20:00 - 24:59
            if(period==6&&secondStart>=60*20&&secondStart<60*25){
                return "25";
            }
            //25:00 - 29:59
            if(period==6&&secondStart>=60*25&&secondStart<60*30){
                return "30";
            }
            //30:00 - 34:59
            if(period==6&&secondStart>=60*30&&secondStart<60*35){
                return "35";
            }
            //35:00 - 39:59
            if(period==6&&secondStart>=60*35&&secondStart<60*40){
                return "40";
            }
            //40:00 - 45:00
            if(period==6&&secondStart>=60*40&&secondStart<60*45){
                return "45";
            }
            //1H Last-minute Goal
            if(period==6&&secondStart>60*45){
                return "49";
            }

            //下半场- 49:59
            if(period==7&&secondStart<60*50){
                return "50";
            }
            //50:00 - 54:59
            if(period==7&&secondStart>=60*50&&secondStart<60*55){
                return "55";
            }
            //55:00 - 59:59
            if(period==7&&secondStart>=60*55&&secondStart<60*60){
                return "60";
            }
            //60:00 - 64:59
            if(period==7&&secondStart>=60*60&&secondStart<60*65){
                return "65";
            }
            //65:00 - 69:59
            if(period==7&&secondStart>=60*65&&secondStart<60*70){
                return "70";
            }
            //70:00 - 74:59
            if(period==7&&secondStart>=60*70&&secondStart<60*75){
                return "75";
            }
            //75:00 - 79:59
            if(period==7&&secondStart>=60*75&&secondStart<60*80){
                return "80";
            }
            //80:00 - 84:59
            if(period==7&&secondStart>=60*80&&secondStart<60*85){
                return "85";
            }
            //85:00 - 90:00
            if(period==7&&secondStart>=60*85&&secondStart<60*90){
                return "90";
            }
            //2H Last-minute Goal
            if(period==7&&secondStart>60*90){
                return "99";
            }
            return null;
        }
        //5分钟 阶段计算
        public static Long get15MinPeriodBy5Min(String fiveMinSection) {
            if (StringUtils.isEmpty(fiveMinSection)) {
                return null;
            }
            Integer min5 = Integer.parseInt(fiveMinSection);
            if (min5 <= 15) {
                if(min5 > 0){
                    return 60899L;
                }else {
                    return null;
                }
            } else if (min5 <= 30) {
                return 61799L;
                //49 包括上半场的补时
            } else if (min5 <= 49) {
                return 62699L;
            } else if (min5 <= 60) {
                return 73599L;
            } else if (min5 <= 75) {
                return 74499L;
            } else if (min5 <= 99) {
                //99包括下半场补时
                return 75399L;
            } else {
                return null;
            }

        }

    }


}
