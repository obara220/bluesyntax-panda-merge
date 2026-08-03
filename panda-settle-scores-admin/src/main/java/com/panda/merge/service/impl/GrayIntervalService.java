package com.panda.merge.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.dto.CheckIsGreyDto;
import com.panda.merge.mapper.MatchGrayIntervalMapper;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchGrayInterval;
import com.panda.merge.model.MatchGrayIntervalExample;
import com.panda.merge.model.MatchSettleTemplate;
import com.panda.merge.service.ISettleTemplateService;
import com.panda.merge.utils.SettleNumUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 灰色区间
 */
@Slf4j
@Component
public class GrayIntervalService {

    @Autowired
    MatchGrayIntervalMapper matchGrayIntervalMapper;
    @Autowired
    ISettleTemplateService settleTemplateService;

    static Map<Integer, List<Integer>> footballGrayIntervalMap = Maps.newConcurrentMap();

    // 上半场
    public static List<Integer> HTMinutes = Arrays.asList(5, 10, 15, 20, 25, 30, 35, 40, 45);

    // 下半场
    public static List<Integer> FTMinutes = Arrays.asList(50, 55, 60, 65, 70, 75, 80, 85, 90);

    //15分钟区间
    public static Map<String,String> fifteenMinSettleNumMap = Maps.newConcurrentMap();


    static {
        footballGrayIntervalMap.put(6, HTMinutes);
        footballGrayIntervalMap.put(7, FTMinutes);
        fifteenMinSettleNumMap.put("102","105");fifteenMinSettleNumMap.put("103","105");fifteenMinSettleNumMap.put("104","105");
        fifteenMinSettleNumMap.put("106","109");fifteenMinSettleNumMap.put("107","109");fifteenMinSettleNumMap.put("108","109");

//        fifteenMinSettleNumMap.put("301","304");fifteenMinSettleNumMap.put("302","304");fifteenMinSettleNumMap.put("303","304");
//        fifteenMinSettleNumMap.put("305","308");fifteenMinSettleNumMap.put("306","308");fifteenMinSettleNumMap.put("307","308");
    }

    public static Map<Integer, List<String>> fiveSettleNumMap = new HashMap<Integer, List<String>>() {
        {
            put(5, Arrays.asList("1034", "1035"));
            put(10, Arrays.asList("1035", "1036"));
            put(15, Arrays.asList("1036", "1037","102", "103"));
            put(20, Arrays.asList("1037", "1038"));
            put(25, Arrays.asList("1038", "1039"));
            put(30, Arrays.asList("1039", "1040","103", "104"));
            put(35, Arrays.asList("1040", "1041"));
            put(40, Arrays.asList("1041", "1042"));
            put(45, Arrays.asList("1042"));
            put(50, Arrays.asList("1044", "1045"));
            put(55, Arrays.asList("1045", "1046"));
            put(60, Arrays.asList("1046", "1047","106", "107"));
            put(65, Arrays.asList("1047", "1048"));
            put(70, Arrays.asList("1048", "1049"));
            put(75, Arrays.asList("1049", "1050","107", "108"));
            put(80, Arrays.asList("1050", "1051"));
            put(85, Arrays.asList("1051", "1052"));
            put(90, Arrays.asList("1052"));
        }
    };

    public static Map<Integer, List<String>> fifteenSettleNumMap = new HashMap<Integer, List<String>>() {
        {
            put(15, Arrays.asList("102", "103"));
            put(30, Arrays.asList("103", "104"));
            put(60, Arrays.asList("106", "107"));
            put(75, Arrays.asList("107", "108"));
        }
    };

    public static Map<Integer, List<String>> fifteenCornerSettleNumMap = new HashMap<Integer, List<String>>() {
        {
            put(15, Arrays.asList("2011", "2012"));
            put(30, Arrays.asList("2012", "2013"));
            put(60, Arrays.asList("2014", "2015"));
            put(75, Arrays.asList("2015", "2016"));
        }
    };

    public static Map<Integer, List<String>> fifteenBookingSettleNumMap = new HashMap<Integer, List<String>>() {
        {
            put(15, Arrays.asList("301", "302"));
            put(30, Arrays.asList("302", "303"));
            put(60, Arrays.asList("305", "306"));
            put(75, Arrays.asList("306", "307"));
        }
    };

    public static Map<Long, List<String>> basketball6MInSettleNumMap = new HashMap<Long, List<String>>() {
        {
            put(13L, Arrays.asList("bk_q1041", "bk_q1042"));
            put(14L, Arrays.asList("bk_q2041", "bk_q2042"));
            put(15L, Arrays.asList("bk_q3041", "bk_q3042"));
            put(16L, Arrays.asList("bk_q4041", "bk_q4042"));
        }
    };


    static List<Integer> fifteenMinutesStatistics =  Arrays.asList(15, 30, 60, 75);

    static Map<String, DataSourceGrayIntervalEnum> enumMap =getEnumMap();

    // 数据商对应的灰色区间时间
    enum DataSourceGrayIntervalEnum {
        RB(10, 15),
        BG(10, 15),
        BT(40, 40),
        PD(40, 40),
        PD2(40, 40),
        TS(25, 25);
        public Integer fiveMinutes;

        public Integer fifteenMinutes;

        DataSourceGrayIntervalEnum(Integer fiveMinutes, Integer fifteenMinutes) {
            this.fiveMinutes = fiveMinutes;
            this.fifteenMinutes = fifteenMinutes;
        }

        public static Integer getFiveMinutes(DataSourceGrayIntervalEnum intervalEnum) {
            return intervalEnum.fiveMinutes;
        }

        public static Integer getFifteenMinutes(DataSourceGrayIntervalEnum intervalEnum) {
            return intervalEnum.fifteenMinutes;
        }
    }

//    @PostConstruct
//    public void initDataSourceGrayInterval() {
//        enumMap = getEnumMap();
//    }

    public static Map<String, DataSourceGrayIntervalEnum> getEnumMap() {
        Map<String, DataSourceGrayIntervalEnum> map = new HashMap<>();
        for (DataSourceGrayIntervalEnum intervalEnum : DataSourceGrayIntervalEnum.values()) {
            map.put(intervalEnum.name(), intervalEnum);
        }
        return map;
    }


    public static DataSourceGrayIntervalEnum getEnumByDataSource(String dataSourceCode) {
//        if (CollectionUtils.isEmpty(enumMap)) {
//            enumMap = getEnumMap();
//        }
        if (enumMap.containsKey(dataSourceCode)) {
            return enumMap.get(dataSourceCode);
        } else {
            return null;
        }
    }

    public CheckIsGreyDto checkDataSourceFiveGray(MatchSettleTemplate grayTemplate, MatchSettleTemplate weithtTemplate, MatchEventInfo matchEventInfo, Integer tournamentLevel) {
        CheckIsGreyDto checkIsGreyDto = new CheckIsGreyDto();
        //角球不结算5分钟
        if(matchEventInfo.getEventCode().equals("corner")){
            return checkIsGreyDto;
        }
        boolean isFiveGray = false;
        Integer fiveSeconds = 5;
//        fiveSeconds = graySecondsByDataSource("min5Goal" , dataSourceCode, tournamentLevel, fiveSeconds);
        fiveSeconds = settleTemplateService.getTemplateGraySeconds("min5Goal",matchEventInfo.getDataSourceCode(),grayTemplate);
        log.info("Template:matchId:{},min5Goal:{},event:{}",matchEventInfo.getStandardMatchId(), fiveSeconds,matchEventInfo);
        if(fiveSeconds==null){
            fiveSeconds=5;
        }
        List<Integer> list = Lists.newArrayList();
        list.addAll(HTMinutes);
        list.addAll(FTMinutes);

        for (Integer m : list) {
            Long start = m * 60L - fiveSeconds;
            Long end = m * 60L + fiveSeconds;
            if ( matchEventInfo.getSecondsFromStart() >= start && matchEventInfo.getSecondsFromStart() <= end  ) {
//                isFiveGray = true;
                List<String> settleNums= fiveSettleNumMap.get(m);
                for (String settleNum : settleNums) {
                    String grayType= "min5Goal";
                    Integer min= SettleNumUtils.GOAL_5_Min_Map.get(settleNum);
                    if(min==null){
                         min= SettleNumUtils.GOAL_15_Min_Map.get(settleNum);
                        grayType="min15Goal";
                    }
                    //当前数据商的灰色区间为真 调用权重校验接口
                    isFiveGray =  settleTemplateService.judgeGrayStatus(matchEventInfo, weithtTemplate,matchEventInfo.getDataSourceCode(),grayType,min, matchEventInfo.getSportId());
                    if(isFiveGray){
//                     checkIsGreyDto.getSettleNum().addAll( fiveSettleNumMap.get(m));
                        checkIsGreyDto.setThisDataSourceIsGray(1);
                        checkIsGreyDto.getSettleNum().add(settleNum);
                    }
                }
            }
        }
        if (checkIsGreyDto.getSettleNum().size()!=0) {
            checkIsGreyDto.setIsGrey(2);
        }
//        if (isFiveGray) {
//            checkIsGreyDto.setIsGrey(2);
//        }
        return checkIsGreyDto;
    }

    public static CheckIsGreyDto checkFiveGray(String dataSourceCode, Long secondsFromStart) {
        CheckIsGreyDto checkIsGreyDto = new CheckIsGreyDto();
        boolean isFiveGray = false;
        Integer fiveSeconds = 5;
        DataSourceGrayIntervalEnum dataSourceEnum = getEnumByDataSource(dataSourceCode);
        if ( null != dataSourceEnum ) {
            fiveSeconds = dataSourceEnum.fiveMinutes;
        }
        List<Integer> list = Lists.newArrayList();
        list.addAll(HTMinutes);
        list.addAll(FTMinutes);

        for (Integer m : list) {
            Long start = m * 60L - fiveSeconds;
            Long end = m * 60L + fiveSeconds;;
            if ( secondsFromStart >= start && secondsFromStart <= end  ) {
                isFiveGray = true;
                checkIsGreyDto.getSettleNum().addAll( fiveSettleNumMap.get(m));
            }
        }
        if (isFiveGray) {
            checkIsGreyDto.setIsGrey(2);
        }
        return checkIsGreyDto;
    }

    public CheckIsGreyDto checkIsGreyCorner(MatchSettleTemplate grayTemplate, MatchSettleTemplate weithtTemplate, MatchEventInfo matchEventInfo, Integer tournamentLevel) {
        CheckIsGreyDto checkIsGreyDto = new CheckIsGreyDto();

        Integer cornerSeconds = 120;
        boolean isCornerGray = false;
        Long secondsFromStart = matchEventInfo.getSecondsFromStart();

//        cornerSeconds = graySecondsByDataSource("min15Corner", matchEventInfo.getDataSourceCode(), tournamentLevel, cornerSeconds);
        cornerSeconds = settleTemplateService.getTemplateGraySeconds("min15Corner",matchEventInfo.getDataSourceCode(),grayTemplate);
        if(cornerSeconds==null){
            cornerSeconds=120;
        }
        log.info("Template:matchId:{},min15Corner:{},event:{}",matchEventInfo.getStandardMatchId(), cornerSeconds,matchEventInfo);
        // 上半场结束
        if ( matchEventInfo.getMatchPeriodId().equals(6L) && secondsFromStart >= 45*60 )
        {
            checkIsGreyDto.getSettleNum().add("2013");
//            isCornerGray = true;
        }
        else
            // 下半场结束
            if ( matchEventInfo.getMatchPeriodId().equals(7L) && secondsFromStart >= 90*60 )
            {
                checkIsGreyDto.getSettleNum().add("2016");
//                isCornerGray = true;
            }
            else
            {
                for (Map.Entry<Integer, List<String>> cornerMap : fifteenCornerSettleNumMap.entrySet() )
                {
                    Long start = cornerMap.getKey() * 60L - cornerSeconds;
                    Long end = cornerMap.getKey() * 60L + cornerSeconds;
                    if ( secondsFromStart >= start && secondsFromStart <= end  ) {
//                        isCornerGray = true;
//                        isCornerGray =  settleTemplateService.judgeGrayStatus(matchEventInfo, weithtTemplate,matchEventInfo.getDataSourceCode(),"min15Corner",cornerMap.getKey());
//                        if(isCornerGray){
//                            checkIsGreyDto.getSettleNum().addAll( cornerMap.getValue() );
//                            checkIsGreyDto.setThisDataSourceIsGray(1);
//                        }
                        List<String> settleNums= cornerMap.getValue();
                        for (String settleNum : settleNums) {
                            Integer min= SettleNumUtils.CORNER_15_Min_Map.get(settleNum);
                            //当前数据商的灰色区间为真 调用权重校验接口
                            isCornerGray =  settleTemplateService.judgeGrayStatus(matchEventInfo, weithtTemplate,matchEventInfo.getDataSourceCode(),"min15Corner",min, matchEventInfo.getSportId());
                            if(isCornerGray){
                                checkIsGreyDto.setThisDataSourceIsGray(1);
                                checkIsGreyDto.getSettleNum().add(settleNum);
                            }
                        }
                    }
                }
            }

        if (isCornerGray) {
            checkIsGreyDto.setIsGrey(1);
        }
        return checkIsGreyDto;
    }


    public CheckIsGreyDto checkIsGreyFaCard(MatchSettleTemplate grayTemplate, MatchSettleTemplate weithtTemplate, MatchEventInfo matchEventInfo, Integer tournamentLevel) {
        CheckIsGreyDto checkIsGreyDto = new CheckIsGreyDto();

        Integer bookingSeconds = 120;
        boolean isBookingGray = false;
        Long secondsFromStart = matchEventInfo.getSecondsFromStart();

        bookingSeconds = settleTemplateService.getTemplateGraySeconds("booking15Min",matchEventInfo.getDataSourceCode(),grayTemplate);
        if(bookingSeconds==null){
            bookingSeconds=120;
        }
        log.info("Template:matchId:{},booking15Min:{},event:{}",matchEventInfo.getStandardMatchId(), bookingSeconds,matchEventInfo);
        // 上半场结束
        if ( matchEventInfo.getMatchPeriodId().equals(6L) && secondsFromStart >= 45*60 )
        {

            checkIsGreyDto.getSettleNum().add("303");
//            isBookingGray = true;
        }
        else
            // 下半场结束
            if ( matchEventInfo.getMatchPeriodId().equals(7L) && secondsFromStart >= 90*60 )
            {
                checkIsGreyDto.getSettleNum().add("307");
//                isBookingGray = true;
            }
            else
            {
                for (Map.Entry<Integer, List<String>> cornerMap : fifteenBookingSettleNumMap.entrySet() )
                {
                    Long start = cornerMap.getKey() * 60L - bookingSeconds;
                    Long end = cornerMap.getKey() * 60L + bookingSeconds;;
                    if ( secondsFromStart >= start && secondsFromStart <= end  ) {
                        List<String> settleNums= cornerMap.getValue();
                        for (String settleNum : settleNums) {
                            Integer min= SettleNumUtils.BOOKING_15_Min_Map.get(settleNum);
                            //当前数据商的灰色区间为真 调用权重校验接口
                            isBookingGray =  settleTemplateService.judgeGrayStatus(matchEventInfo, weithtTemplate,matchEventInfo.getDataSourceCode(),"booking15Min",min, matchEventInfo.getSportId());
                            if(isBookingGray){
                                checkIsGreyDto.setThisDataSourceIsGray(1);
                                checkIsGreyDto.getSettleNum().add(settleNum);
                            }
                        }
                    }
                }
            }

        if (isBookingGray) {
            checkIsGreyDto.setIsGrey(1);
        }
        return checkIsGreyDto;
    }

    public CheckIsGreyDto checkIsGreyBasketball(MatchSettleTemplate grayTemplate, MatchSettleTemplate weithtTemplate, MatchEventInfo matchEventInfo, Integer tournamentLevel) {
        CheckIsGreyDto checkIsGreyDto = new CheckIsGreyDto();
        boolean isGoalGray = false;
        Integer goalSeconds = settleTemplateService.getTemplateGraySeconds(CommonConstant.BASKETBALL_GRAY_GAOL_6MIN,matchEventInfo.getDataSourceCode(),grayTemplate);
        if(goalSeconds == null) {
            goalSeconds=30;
        }


        if ((matchEventInfo.getSecondsFromStart() >=  360L - goalSeconds) && (matchEventInfo.getSecondsFromStart() <= 360L + goalSeconds)) {
            isGoalGray = true;
            List<String> settleNums= basketball6MInSettleNumMap.get(matchEventInfo.getMatchPeriodId());
            for (String settleNum : settleNums) {
                Integer min= SettleNumUtils.BASKETBALL_GOAL_6_Min_Map.get(settleNum);
                if(min==null){
                    continue;
                }
                //当前数据商的灰色区间为真 调用权重校验接口
                isGoalGray =  settleTemplateService.judgeGrayStatus(matchEventInfo, weithtTemplate,matchEventInfo.getDataSourceCode(),CommonConstant.BASKETBALL_GRAY_GAOL_6MIN,min, matchEventInfo.getSportId());
                if(isGoalGray){
                    checkIsGreyDto.setThisDataSourceIsGray(1);
                    checkIsGreyDto.getSettleNum().add(settleNum);
                }
            }
        }
        if (isGoalGray) {
            checkIsGreyDto.setIsGrey(1);
        }
        return checkIsGreyDto;
    }


    public CheckIsGreyDto checkDataSourceFifteenGray(MatchSettleTemplate grayTemplate, MatchSettleTemplate weithtTemplate, MatchEventInfo matchEventInfo, Integer tournamentLevel ) {
        CheckIsGreyDto checkIsGreyDto = new CheckIsGreyDto();
        boolean isFifteenGray = false;
        Integer fifteenSeconds = 30;
//        fifteenSeconds = graySecondsByDataSource("min15Goal" , dataSourceCode, tournamentLevel, fifteenSeconds);
        fifteenSeconds = settleTemplateService.getTemplateGraySeconds("min15Goal",matchEventInfo.getDataSourceCode(),grayTemplate);
        log.info("Template:matchId:{},min15Goal:{},event:{}",matchEventInfo.getStandardMatchId(), fifteenSeconds,matchEventInfo);
        if(fifteenSeconds==null){
            fifteenSeconds=30;
        }
        for (Integer m : fifteenMinutesStatistics) {
            Long start = m * 60L - fifteenSeconds;
            Long end = m * 60L + fifteenSeconds;
            if ( matchEventInfo.getSecondsFromStart() >= start && matchEventInfo.getSecondsFromStart() <= end  ) {
                isFifteenGray = true;
                //根据settleNum 得到分钟数
//                isFifteenGray =  settleTemplateService.judgeGrayStatus(matchEventInfo, weithtTemplate,matchEventInfo.getDataSourceCode(),"min15Goal",m);
//                if(isFifteenGray){
//                    checkIsGreyDto.getSettleNum().addAll( fifteenSettleNumMap.get(m));
//                    checkIsGreyDto.setThisDataSourceIsGray(1);
//                }
                List<String> settleNums= fifteenSettleNumMap.get(m);
                for (String settleNum : settleNums) {
                    Integer min= SettleNumUtils.GOAL_15_Min_Map.get(settleNum);
                    if(min==null){
                        continue;
                    }
                    //当前数据商的灰色区间为真 调用权重校验接口
                    isFifteenGray =  settleTemplateService.judgeGrayStatus(matchEventInfo, weithtTemplate,matchEventInfo.getDataSourceCode(),"min15Goal",min, matchEventInfo.getSportId());
                    if(isFifteenGray){
                        checkIsGreyDto.setThisDataSourceIsGray(1);
                        checkIsGreyDto.getSettleNum().add(settleNum);
                    }
                }
            }
        }
        if (isFifteenGray) {
            checkIsGreyDto.setIsGrey(1);
        }
        return checkIsGreyDto;
    }

    public static CheckIsGreyDto checkFifteenGray(String dataSourceCode, Long secondsFromStart) {
        CheckIsGreyDto checkIsGreyDto = new CheckIsGreyDto();
        boolean isFifteenGray = false;
        Integer fifteenSeconds = 30;
        DataSourceGrayIntervalEnum dataSourceEnum = getEnumByDataSource(dataSourceCode);
        if ( null != dataSourceEnum ) {
            fifteenSeconds = dataSourceEnum.fifteenMinutes;
        }
        for (Integer m : fifteenMinutesStatistics) {
            Long start = m * 60L - fifteenSeconds;
            Long end = m * 60L + fifteenSeconds;;
            if ( secondsFromStart >= start && secondsFromStart <= end  ) {
                isFifteenGray = true;
                checkIsGreyDto.getSettleNum().addAll( fifteenSettleNumMap.get(m));
            }
        }
        if (isFifteenGray) {
            checkIsGreyDto.setIsGrey(1);
        }
        return checkIsGreyDto;
    }

    /**
     *
     * @param timeType  灰色区间的类型
     * @param dataSourceCode
     * @param tournamentLevel
     * @param graySecond 默认灰色区间时间
     * @return
     */
    public Integer graySecondsByDataSource(String timeType , String dataSourceCode, Integer tournamentLevel, Integer graySecond) {
        Integer targetSeconds = graySecond;
        MatchGrayIntervalExample grayIntervalExample = new MatchGrayIntervalExample();
        grayIntervalExample.createCriteria().andTournamentLevelEqualTo(tournamentLevel);
        List<MatchGrayInterval> dbGrayIntervals = matchGrayIntervalMapper.selectByExample(grayIntervalExample);
        Map<String, MatchGrayInterval> dsgMap = Maps.newConcurrentMap();
        if ( !CollectionUtils.isEmpty(dbGrayIntervals) ) {
            dsgMap = dbGrayIntervals.stream().collect(Collectors.toMap(MatchGrayInterval::getDataSourceCode, Function.identity()));
        }

        if ( null != dsgMap && dsgMap.size() > 0 && dsgMap.containsKey(dataSourceCode)) {
            if ( "min15Goal".equals(timeType) ) {
                targetSeconds = dsgMap.get(dataSourceCode).getMin15Goal();
            } else if ( "min5Goal".equals(timeType) ) {
                targetSeconds = dsgMap.get(dataSourceCode).getMin5Goal();
            } else if ( "min15Corner".equals(timeType) ) {
                targetSeconds = dsgMap.get(dataSourceCode).getMin15Corner();
            }
        }
        return targetSeconds;
    }

}
