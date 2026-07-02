package com.panda.merge.advertise.utils;

import com.panda.merge.advertise.common.Constant;
import com.panda.merge.common.enums.BasketballSixPeriodEnum;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.dto.CommonItem;
import com.panda.merge.dto.advertise.PDBasketBallEditSixScoreDto;
import com.panda.merge.dto.advertise.PDTennisRoundStatusDto;
import com.panda.merge.model.MatchTimeInfo;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MatchPeriodUtils {
    public static Long getMatchTime(MatchTimeInfo matchTimeInfo) {
        //1.非开赛阶段计算
        Long startTimeSecond =0l;
        if(matchTimeInfo.getPeriod().equals(999l)){
                return startTimeSecond;
        }
        if(matchTimeInfo.getPeriod().equals(100l)){
            return 5*60l;
        }
        if(matchTimeInfo.getPeriod().equals(32L)){
            return 5*60l;
        }
        if(matchTimeInfo.getPeriod()!=null&&matchTimeInfo.getPeriod().equals(21L)){
            startTimeSecond =  matchTimeInfo.getSecondFromStart()-(System.currentTimeMillis()- matchTimeInfo.getEventTime())/1000;
            if(matchTimeInfo.getTimeGo()==0){
                startTimeSecond= matchTimeInfo.getSecondFromStart();
            }
            return startTimeSecond;
        }
        //判断比赛是否进行中
        if(SportPeriodConstant.BasketballPeriod.contans( matchTimeInfo.getPeriod(),0)){
            //比赛进行中的话没暂停 篮球的比赛时长 =  数据库记录的比赛时长- (系统时间戳-上次事件发生时间)/1000
             startTimeSecond =  matchTimeInfo.getSecondFromStart()-(System.currentTimeMillis()- matchTimeInfo.getEventTime())/1000;
            //比赛暂停 篮球的比赛时长 =  数据库记录的比赛时长
            if(matchTimeInfo.getTimeGo()==0){
                startTimeSecond= matchTimeInfo.getSecondFromStart();
            }
            return startTimeSecond;
        }else {
            if(matchTimeInfo.getMatchLength()==null){
                return 10*60l;
            }
            //else 比赛没有开打 或者不在开打阶段
            if(matchTimeInfo.getMatchLength()==17){
                return 20*60l;
            }else if(matchTimeInfo.getMatchLength()==0){
                return 10*60l;
            }else if(matchTimeInfo.getMatchLength()==68){
                return 5*60l;
            }else if(matchTimeInfo.getMatchLength()==73){
                return 10*60l;
            }
            else{
                return 12*60l;
            }
        }

        //2.正常滚球开赛中计算

    }

    public static Long getBreakAndRestartMatchTime(MatchTimeInfo matchTimeInfo) {
        //1.非开赛阶段计算
        Long startTimeSecond =0l;
        if(matchTimeInfo.getPeriod().equals(999l)){
            return startTimeSecond;
        }
        if(matchTimeInfo.getPeriod().equals(100l)){
            return 5*60l;
        }
        if(matchTimeInfo.getPeriod().equals(32L)){
            return 5*60l;
        }
        if(matchTimeInfo.getPeriod()!=null&&matchTimeInfo.getPeriod().equals(21L)){
            startTimeSecond =  matchTimeInfo.getSecondFromStart()-(System.currentTimeMillis()- matchTimeInfo.getEventTime())/1000;
            if(matchTimeInfo.getTimeGo()==0){
                startTimeSecond= matchTimeInfo.getSecondFromStart();
            }
            return startTimeSecond;
        }
        //判断比赛是否进行中
        if(SportPeriodConstant.BasketballPeriod.contans( matchTimeInfo.getPeriod(),0)){
            //比赛进行中的话没暂停 篮球的比赛时长 =  数据库记录的比赛时长- (系统时间戳-上次事件发生时间)/1000
            startTimeSecond =  matchTimeInfo.getSecondFromStart()-(System.currentTimeMillis()- matchTimeInfo.getEventTime())/1000;
            //比赛暂停 篮球的比赛时长 =  数据库记录的比赛时长
//            if(matchTimeInfo.getTimeGo()==0){
//                startTimeSecond= matchTimeInfo.getSecondFromStart();
//            }
            return startTimeSecond;
        }else {
            if(matchTimeInfo.getMatchLength()==null){
                return 10*60l;
            }
            //else 比赛没有开打 或者不在开打阶段
            if(matchTimeInfo.getMatchLength()==17){
                return 20*60l;
            }else if(matchTimeInfo.getMatchLength()==0){
                return 10*60l;
            }else if(matchTimeInfo.getMatchLength()==68){
                return 5*60l;
            }else if(matchTimeInfo.getMatchLength()==73){
                return 10*60l;
            }
            else{
                return 12*60l;
            }
        }

        //2.正常滚球开赛中计算

    }

    public static Long getFootBallPeriodTime(Integer matchLenth,Long period){
        //根据当前阶段判断时间

        if(period.equals(7L)){
            if(matchLenth==55){
                return 3*60L;
            }
            if(matchLenth==62){
                return 6*60L;
            }
            if(matchLenth==63){
                return 5*60L;
            }
            if(matchLenth==69){
                return 10*60L;
            }
            if(matchLenth==71){
                return 4*60L;
            }
            if(matchLenth==72){
                return 15*60L;
            }
            return 45*60L;
        }
        if(period.equals(41L)){
            if(matchLenth==55){
                return 6*60L;
            }
            if(matchLenth==62){
                return 12*60L;
            }
            if(matchLenth==63){
                return 10*60L;
            }
            if(matchLenth==69){
                return 20*60L;
            }
            if(matchLenth==71){
                return 8*60L;
            }
            if(matchLenth==72){
                return 30*60L;
            }
            return  90*60L;
        }
        if(period.equals(42L)){
            if(matchLenth==55){
                return 9*60L;
            }
            if(matchLenth==62){
                return 18*60L;
            }
            if(matchLenth==63){
                return 15*60L;
            }
            if(matchLenth==69){
                return 30*60L;
            }
            if(matchLenth==71){
                return 12*60L;
            }
            if(matchLenth==72){
                return 40*60L;
            }
           return  105*60L;
        }

        return 0l;
    }

    public static Long getTennisPeriodByRoundStatus(PDTennisRoundStatusDto pdTennisRoundStatusDto) {
        //结束RoundStatus = 1
        if(pdTennisRoundStatusDto.getRoundStatus()==1){
            if(pdTennisRoundStatusDto.getCurrentSet()==1){
                return 800L;
            }else if(pdTennisRoundStatusDto.getCurrentSet()==2){
                return 900L;
            }else if(pdTennisRoundStatusDto.getCurrentSet()==3){
                return 1000L;
            }else if(pdTennisRoundStatusDto.getCurrentSet()==4){
                return 1100L;
            }else if(pdTennisRoundStatusDto.getCurrentSet()==5){
                return 1200L;
            }
        } else if(pdTennisRoundStatusDto.getRoundStatus()==0){       //结束RoundStatus = 0
            if(pdTennisRoundStatusDto.getCurrentSet()==1){
                return 8L;
            }else if(pdTennisRoundStatusDto.getCurrentSet()==2){
                return 9L;
            }else if(pdTennisRoundStatusDto.getCurrentSet()==3){
                return 10L;
            }else if(pdTennisRoundStatusDto.getCurrentSet()==4){
                return 11L;
            }else if(pdTennisRoundStatusDto.getCurrentSet()==5){
                return 12L;
            }
        }
        return null;
    }

    public static Long getBasketSixPeriod(Long matchPeriodId, Long secondsFromStart,Integer matchLength) {
        if(matchLength==null){
            matchLength = 0;
        }
        if (matchLength != 7) {
            return null;
        }
        if(matchPeriodId.equals(13L)){
            if(secondsFromStart<=360){
                return BasketballSixPeriodEnum.BASKETBALL_1306.getCode();
            }else {
                return BasketballSixPeriodEnum.BASKETBALL_1312.getCode();
            }
        }
        if(matchPeriodId.equals(14L)){
            if(secondsFromStart<=360){
                return BasketballSixPeriodEnum.BASKETBALL_1406.getCode();
            }else {
                return BasketballSixPeriodEnum.BASKETBALL_1412.getCode();
            }
        }
        if(matchPeriodId.equals(15L)){
            if(secondsFromStart<=360){
                return BasketballSixPeriodEnum.BASKETBALL_1506.getCode();
            }else {
                return BasketballSixPeriodEnum.BASKETBALL_1512.getCode();
            }
        }
        if(matchPeriodId.equals(16L)){
            if(secondsFromStart<=360){
                return BasketballSixPeriodEnum.BASKETBALL_1606.getCode();
            }else {
                return BasketballSixPeriodEnum.BASKETBALL_1612.getCode();
            }
        }
        return null;
    }

    public static class BascketBallPeriod{
        public static  Long getNextPeriod(Long period, Integer matchLength){
            Long[] periods= Constant.BasketBallConstant.getWholePeriodsByMatchLength(matchLength);
            int x =0;
            for (int i=0;i<=periods.length-1;i++) {
                if(periods[i].equals(period)){
                    x=i;
                }
            }
            return periods[x+1];
        }
        public static  Long getBeforePeriod(Long period, Integer matchLength){
            Long[] periods= Constant.BasketBallConstant.getWholePeriodsByMatchLength(matchLength);
            int x =0;
            for (int i=0;i<=periods.length-1;i++) {
                if(periods[i].equals(period)){
                    x=i;
                }
            }
            if(x-1<0){
                return -1l;
            }
            return periods[x-1];
        }
        public static boolean comparePeriodIndex(Long period1,Long period2, Integer matchLength){
            Long[] periods= Constant.BasketBallConstant.getWholePeriodsByMatchLength(matchLength);
            int index1=0;
            int index2=0;
            for (int i=0;i<=periods.length-1;i++) {
                if(periods[i].equals(period1)){
                    index1=i;
                }
            }
            for (int i=0;i<=periods.length-1;i++) {
                if(periods[i].equals(period2)){
                    index2=i;
                }
            }
            return index1>=index2;
        }
    }
    public static Map<Integer ,Long> TENNIS_PERIOD_MAP=new HashMap<>();
    static {
        TENNIS_PERIOD_MAP.put(1,8L);
        TENNIS_PERIOD_MAP.put(2,9L);
        TENNIS_PERIOD_MAP.put(3,10L);
        TENNIS_PERIOD_MAP.put(4,11L);
        TENNIS_PERIOD_MAP.put(5,12L);
    }
    public static Long getTennisPeriodBySet(Integer currentSet){
        return TENNIS_PERIOD_MAP.get(currentSet);
    }
    public static Map<Long ,Integer> TENNIS_SET_MAP=new HashMap<>();
    static {
        TENNIS_SET_MAP.put(8L,1);
        TENNIS_SET_MAP.put(9L,2);
        TENNIS_SET_MAP.put(10L,3);
        TENNIS_SET_MAP.put(11L,4);
        TENNIS_SET_MAP.put(12L,5);
    }
    public static Integer getTennisSetByPeriod(Long currentSet){
        return TENNIS_SET_MAP.get(currentSet);
    }
    /**
     * 结束阶段
     * */
    public static Map<Integer ,Long> TENNIS_PERIOD_END_MAP=new HashMap<>();
    static {
        TENNIS_PERIOD_END_MAP.put(1,301L);
        TENNIS_PERIOD_END_MAP.put(2,302L);
        TENNIS_PERIOD_END_MAP.put(3,303L);
        TENNIS_PERIOD_END_MAP.put(4,304L);
        TENNIS_PERIOD_END_MAP.put(5,100L);
    }
    public static Long getTennisPeriodEndBySet(Integer currentSet){
        return TENNIS_PERIOD_END_MAP.get(currentSet);
    }

    /**
     * 结束阶段获取当前盘数
     * */
    public static Map<Long,Integer > TENNIS_END_MAP=new HashMap<>();
    static {
        TENNIS_END_MAP.put(301L,1);
        TENNIS_END_MAP.put(302L,2);
        TENNIS_END_MAP.put(303L,3);
        TENNIS_END_MAP.put(304L,4);
        TENNIS_END_MAP.put(100L,5);
    }
    public static Integer getTennisEndBySet(Long currentSet){
        return TENNIS_END_MAP.get(currentSet);
    }

    /**
     * 获取6分钟阶段ID
     *
     * @param editSixScoreDto 阶段信息
     * @return 阶段Id
     */
    public static Map<Long, CommonItem> getSixPeriodMap(PDBasketBallEditSixScoreDto editSixScoreDto) {
        Map<Long, CommonItem> periodMap = new HashMap<>(16);
        try {
            Field[] fields = editSixScoreDto.getClass().getDeclaredFields();
            for (Field field : fields) {
                String fieldName = field.getName();
                String firstLetter = fieldName.substring(0, 1).toUpperCase();
                String getter = "get" + firstLetter + fieldName.substring(1);
                Method method = editSixScoreDto.getClass().getMethod(getter);
                Object value = method.invoke(editSixScoreDto);
                if (fieldName.length() <= 4 || value == null) {
                    continue;
                }
                String substring = fieldName.substring(fieldName.length() - 4);
                if (!substring.matches("\\d+")) {
                    continue;
                }
                Long periodId = Long.valueOf(substring);
                if (BasketballSixPeriodEnum.getSixPeriodId(periodId)) {
                    com.panda.merge.cache.CommonItem value1 = (com.panda.merge.cache.CommonItem) value;
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(value1.getHome());
                    commonItem.setAway(value1.getAway());
                    periodMap.put(periodId, commonItem);
                    return periodMap;
                }
            }
        } catch (Exception e) {
            log.error("篮球报球板，编辑6分钟异常，thirdMatchId={}", editSixScoreDto.getThirdMatchId(), e);
        }
        return periodMap;
    }
}
