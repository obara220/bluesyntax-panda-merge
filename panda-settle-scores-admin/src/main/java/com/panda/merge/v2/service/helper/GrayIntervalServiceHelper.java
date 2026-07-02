package com.panda.merge.v2.service.helper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.dto.CheckIsGreyDto;
import com.panda.merge.dto.DataSourceSettleWeightDto;
import com.panda.merge.dto.GrayAreaSettleDto;
import com.panda.merge.model.*;
import com.panda.merge.utils.SettleNumUtils;
import com.panda.merge.utils.SettleTemplateJsonUtils;
import com.panda.merge.v2.repository.MatchSettleGrayWeightV2Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 灰色区间
 */
@Slf4j
@Component
public class GrayIntervalServiceHelper {

    @Autowired
    private MatchSettleGrayWeightV2Repository matchSettleGrayWeightRepository;

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
    private static List<String> DATA_SOURCE_CODE_LIST = new ArrayList<>();
    static {
        DATA_SOURCE_CODE_LIST.add("BG");
        DATA_SOURCE_CODE_LIST.add("KO");
        DATA_SOURCE_CODE_LIST.add("RB");
        DATA_SOURCE_CODE_LIST.add("LS");
        DATA_SOURCE_CODE_LIST.add("TS");
        DATA_SOURCE_CODE_LIST.add("BT");
        DATA_SOURCE_CODE_LIST.add("PD");
        DATA_SOURCE_CODE_LIST.add("PD2");
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
        fiveSeconds = getTemplateGraySeconds("min5Goal",matchEventInfo.getDataSourceCode(),grayTemplate);
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
                    isFiveGray =  judgeGrayStatus(matchEventInfo, weithtTemplate,matchEventInfo.getDataSourceCode(),grayType,min, matchEventInfo.getSportId());
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
        cornerSeconds = getTemplateGraySeconds("min15Corner",matchEventInfo.getDataSourceCode(),grayTemplate);
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
                            isCornerGray =  judgeGrayStatus(matchEventInfo, weithtTemplate,matchEventInfo.getDataSourceCode(),"min15Corner",min, matchEventInfo.getSportId());
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

        bookingSeconds = getTemplateGraySeconds("booking15Min",matchEventInfo.getDataSourceCode(),grayTemplate);
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
                            isBookingGray =  judgeGrayStatus(matchEventInfo, weithtTemplate,matchEventInfo.getDataSourceCode(),"booking15Min",min, matchEventInfo.getSportId());
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
        Integer goalSeconds = getTemplateGraySeconds(CommonConstant.BASKETBALL_GRAY_GAOL_6MIN,matchEventInfo.getDataSourceCode(),grayTemplate);
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
                isGoalGray =  judgeGrayStatus(matchEventInfo, weithtTemplate,matchEventInfo.getDataSourceCode(),CommonConstant.BASKETBALL_GRAY_GAOL_6MIN,min, matchEventInfo.getSportId());
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
        fifteenSeconds = getTemplateGraySeconds("min15Goal",matchEventInfo.getDataSourceCode(),grayTemplate);
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
                    isFifteenGray = judgeGrayStatus(matchEventInfo, weithtTemplate,matchEventInfo.getDataSourceCode(),"min15Goal",min, matchEventInfo.getSportId());
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

    public Integer getTemplateGraySeconds(String type,String dataSourceCode, MatchSettleTemplate template) {
        //模版为空 则不用查询
        if(template==null){
            return null;
        }
        List<GrayAreaSettleDto> list  = SettleTemplateJsonUtils.tansferGrayAreaList(template.getTemplateJson());
        GrayAreaSettleDto grayAreaSettleDto =null;
        for (GrayAreaSettleDto dto : list) {
            if(dataSourceCode.equals(dto.getDataSourceCode())){
                grayAreaSettleDto=dto;
                break;
            }
        }
        if(grayAreaSettleDto==null){
            return null;
        }
        if(type==null){
            return null;
        }else if("min15Goal".equals(type)){
            //15分钟进球
            return grayAreaSettleDto.getGoal15Min();
        }else if("min5Goal".equals(type)){
            //5分钟进球
            return grayAreaSettleDto.getGoal5Min();
        }else if("min15Corner".equals(type)){
            return grayAreaSettleDto.getCorner15Min();
        }else if("booking15Min".equals(type)){
            //15分钟罚牌
            return grayAreaSettleDto.getBooking15Min();
        }else if(CommonConstant.BASKETBALL_GRAY_GAOL_6MIN.equals(type)){
            return grayAreaSettleDto.getGoal6Min();
        }
        return null;
    }

    public Boolean judgeGrayStatus(MatchEventInfo matchEventInfo, MatchSettleTemplate template, String dataSourceCode, String grayType, Integer min, Long sportId) {
        //1.插入当前数据商的灰色区间 去重方式:  id =  standardMatchId+grayType(1 2 3  15进球 5进球 15角球)+min(2位)
        Long grayId =  this.getGrayId(matchEventInfo.getStandardMatchId(),dataSourceCode,grayType,min);
        //查询是否有，如果没有则新增
        if(grayId==null){
            return false;
        }
        MatchSettleGrayWeight matchSettleGrayWeight = matchSettleGrayWeightRepository.getById(grayId);
        // 如果有则更新事件list
        if(matchSettleGrayWeight==null){
            matchSettleGrayWeight =new MatchSettleGrayWeight();
            matchSettleGrayWeight.setId(grayId);
            matchSettleGrayWeight.setCreateTime(System.currentTimeMillis());
            matchSettleGrayWeight.setModifyTime(System.currentTimeMillis());
            matchSettleGrayWeight.setSportId(sportId);
            matchSettleGrayWeight.setStandardMatchId(matchEventInfo.getStandardMatchId());
            matchSettleGrayWeight.setDataSourceCode(dataSourceCode);
            matchSettleGrayWeight.setGrayAreaMin(min);
            matchSettleGrayWeight.setGrayCode(grayType);
            matchSettleGrayWeight.setGrayStatus(1);
            matchSettleGrayWeightRepository.save(matchSettleGrayWeight);
        }else {
            matchSettleGrayWeight.setGrayStatus(1);
            matchSettleGrayWeightRepository.updateById(matchSettleGrayWeight);
        }

        //2.根据赛事id,min grayType 查询
        List<MatchSettleGrayWeight> list =matchSettleGrayWeightRepository.getByItems(matchEventInfo.getStandardMatchId(),
                sportId, grayType, min, 1);
        //模版验证上述数据商是否通过权限
        List<DataSourceSettleWeightDto> weightDtos = new ArrayList<>();
        if(template != null) {
            weightDtos = SettleTemplateJsonUtils.tansferDataSourceSettleWeightDtoList(template.getTemplateJson());
        }
        //计算权重是否>=50
        Map<String,Integer>  map = new HashMap<>();
        for (DataSourceSettleWeightDto weightDto : weightDtos) {
            map.put(weightDto.getDataSourceCode(),weightDto.getGrayWeight());
        }
        Integer weight = 0;
        for (MatchSettleGrayWeight settleGrayWeight : list) {
            Integer w= map.getOrDefault(settleGrayWeight.getDataSourceCode(), 0);
            weight+=w;
        }
        //非灰色区间权重计算
        List<MatchSettleGrayWeight> list2 =matchSettleGrayWeightRepository.getByItems(matchEventInfo.getStandardMatchId(),
                sportId, grayType, min, 0);
        Integer weight2 = 0;
        for (MatchSettleGrayWeight settleGrayWeight : list2) {
            Integer w= map.getOrDefault(settleGrayWeight.getDataSourceCode(), 0);
            weight2+=w;
        }

        log.info("Template:matchId:{},matchEventId:{},GrayWeightSum:{},NoGrayWeightSum:{}",matchEventInfo.getStandardMatchId(),matchEventInfo.getId(), weight,weight2);
        return weight>=50&&weight>weight2;
    }

    private Long getGrayId(Long standardMatchId,String dataSourceCode, String grayType, Integer min) {
        Integer i=DATA_SOURCE_CODE_LIST.indexOf(dataSourceCode.trim());
        Integer type=0;
        if("min15Goal".equals(grayType)){
            type=1;
        }
        if("min5Goal".equals(grayType)){
            type=2;
        }
        if("min15Corner".equals(grayType)){
            type=3;
        }
        if("booking15Min".equals(grayType)){
            type=4;
        }
        if(CommonConstant.BASKETBALL_GRAY_GAOL_6MIN.equals(grayType)){
            type=5;
        }
        String minStr ="";
        if(min<10){
            minStr="0"+min;
        }else {
            minStr=min.toString();
        }
        String idStr = ""+standardMatchId+""+type+minStr+i;
        if(i==-1){
            return null;
        }
        return Long.parseLong(idStr);
    }
}
