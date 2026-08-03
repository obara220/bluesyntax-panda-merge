package com.panda.merge.v2.check.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.SettleTemplateTypeEnum;
import com.panda.merge.dto.CheckIsGreyDto;
import com.panda.merge.dto.DataSourceSettleWeightDto;
import com.panda.merge.filter.football.impl.MatchPenaltyEventSettleInitFilter;
import com.panda.merge.filter.football.impl.MatchScoresSettleInitChainFilter;
import com.panda.merge.mapper.MatchSettleGrayWeightMapper;
import com.panda.merge.model.*;
import com.panda.merge.respository.MatchEventInfoRepository;
import com.panda.merge.service.*;
import com.panda.merge.service.impl.GrayIntervalService;
import com.panda.merge.service.settleMention.service.SettleMentionFactory;
import com.panda.merge.utils.SettleNumUtils;
import com.panda.merge.utils.SettleTemplateJsonUtils;
import com.panda.merge.v2.check.IMatchSettleBatchCheckService;
import com.panda.merge.v2.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class GrayPhaseEventHelper {

    @Autowired
    MatchScoresSettleInitChainFilter matchScoresSettleInitChainFilter;
    @Autowired
    MatchPenaltyEventSettleInitFilter matchPenaltyEventSettleInitFilter;
    @Autowired
    RedisService redisService;
    @Autowired
    StandardSportPlayerService standardSportPlayerService;
    @Autowired
    MatchSettleGrayWeightMapper matchSettleGrayWeightMapper;
    @Autowired
    IMatchSettleService matchSettleService;

    @Autowired
    IMatchSettleBatchCheckService matchSettleBatchCheckService;
    @Autowired
    MatchSettleDataSourceConfigService matchSettleDataSourceConfigService;
    @Autowired
    GrayIntervalService grayIntervalService;
    @Autowired
    ISettleTemplateService settleTemplateService;
    @Autowired
    MatchSettleInfoRepository matchSettleInfoRepository;
    @Autowired
    SettleMentionFactory settleMentionFactory;
    @Autowired
    StandardMatchInfoService standardMatchInfoService;
    @Autowired
    StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    StandardSportTournamentService standardSportTournamentService;
    @Autowired
    MatchEventInfoRepository matchEventInfoRepository;
    @Autowired
    MatchSettleFactorCheckInfoRepository matchSettleFactoryCheckInfoRepository;
    @Autowired
    com.panda.merge.v2.service.IMatchSettleCheckInfoService matchSettleCheckInfoService;
    @Autowired
    private MatchSettleDataSourceSwitchRepository matchSettleDataSourceSwitchRepository;

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


    public CheckIsGreyDto checkIsGreyPhaseEvent(MatchEventInfo matchEventInfo, String linkId) {
        CheckIsGreyDto checkIsGreyDto = new CheckIsGreyDto();
        checkIsGreyDto.setStandardMatchId(matchEventInfo.getStandardMatchId());
        checkIsGreyDto.setMatchEventInfo(matchEventInfo);

        //模版查询
        MatchSettleTemplate grayTemplate = settleTemplateService.getTemplateByStandardMatchId(matchEventInfo.getStandardMatchId(), SettleTemplateTypeEnum.GRAY_AREA.code);
        log.info("linkId::{}::Template:matchId:{},grayTemplate:{}",linkId, matchEventInfo.getStandardMatchId(), grayTemplate);
        MatchSettleTemplate weithtTemplate = settleTemplateService.getTemplateByStandardMatchId(matchEventInfo.getStandardMatchId(), SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code);
        log.info("linkId::{}::Template:matchId:{},weithtTemplate:{}",linkId, matchEventInfo.getStandardMatchId(), weithtTemplate);

        try {
            log.info("linkId::{}::checkIsGreyEvent的入参:{}", linkId, JSON.toJSONString(matchEventInfo));
            // 赛事与联赛的判断
            Long standardMatchId = matchEventInfo.getStandardMatchId();
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
            if ( Objects.isNull(standardMatchInfo) ) {
                log.info("linkId::{}::matchId:{} checkIsGreyEvent的赛事不存在", linkId, matchEventInfo.getStandardMatchId());
                return checkIsGreyDto;
            }
            StandardSportTournament standardSportTournament = standardSportTournamentService.getItem(standardMatchInfo.getStandardTournamentId());
            if ( Objects.isNull(standardSportTournament) ) {
                log.info("linkId::{}::matchId:{} checkIsGreyEvent的联赛不存在", linkId, matchEventInfo.getStandardMatchId());
                return checkIsGreyDto;
            }
            Integer tournamentLevel = standardSportTournament.getTournamentLevel();

            checkIsGreyDto.setStandardMatchId(matchEventInfo.getStandardMatchId());
            if (null == matchEventInfo) {
                log.info("linkId::{}::matchId:{} checkIsGreyEvent事件无法获取", linkId, matchEventInfo.getStandardMatchId());
                return checkIsGreyDto;
            }

            //1.只处理进球角球类型
            if (matchEventInfo.getEventCode().equals("corner")) {
                return checkIsGreyCorner(linkId, grayTemplate,weithtTemplate,matchEventInfo, tournamentLevel);
            }else if (matchEventInfo.getEventCode().equals("yellow_card")||matchEventInfo.getEventCode().equals("red_card")||matchEventInfo.getEventCode().equals("fa_card")){
                return checkIsGreyFaCard(linkId,grayTemplate,weithtTemplate,matchEventInfo, tournamentLevel);
            }else if(matchEventInfo.getEventCode().equals("score_change")) {
                return grayIntervalService.checkIsGreyBasketball(grayTemplate,weithtTemplate,matchEventInfo, tournamentLevel);
            } else if (!matchEventInfo.getEventCode().equals("goal")) {
                return checkIsGreyDto;
            }

            checkIsGreyDto = checkDataSourceFiveGray(linkId,grayTemplate,weithtTemplate,matchEventInfo, tournamentLevel);
            if (2 == checkIsGreyDto.getIsGrey()) {
                log.info("linkId::{}::matchId:{} checkIsGreyDto:{} ", linkId, matchEventInfo.getStandardMatchId(), checkIsGreyDto);
                return checkIsGreyDto;
            }

            checkIsGreyDto = checkDataSourceFifteenGray(linkId, grayTemplate,weithtTemplate,matchEventInfo, tournamentLevel);
            log.info("linkId::{}::matchId:{} checkIsGreyDto:{} ", linkId, matchEventInfo.getStandardMatchId(), checkIsGreyDto);
        } catch (Exception   e){
            log.error("linkId::{}::matchId:{} MatchScoresTransSettleServiceImpl-checkIsGreyEvent:",linkId, matchEventInfo.getStandardMatchId(), e);
            return checkIsGreyDto;
        }
        return checkIsGreyDto;
    }


    public CheckIsGreyDto checkIsGreyCorner(String linkId, MatchSettleTemplate grayTemplate, MatchSettleTemplate weithtTemplate, MatchEventInfo matchEventInfo, Integer tournamentLevel) {
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
                    if ( matchEventInfo.getSecondsFromStart() > cornerMap.getKey()*60 || matchEventInfo.getSecondsFromStart() <= (cornerMap.getKey()-15)* 60L) {
                        continue;
                    }
                    Long start = cornerMap.getKey() * 60L - cornerSeconds;
                    Long end = cornerMap.getKey() * 60L + cornerSeconds;
                    boolean isGray = false;
                    if ( secondsFromStart >= start && secondsFromStart <= end  ) {
                        isGray = true;
                    }
                    List<String> settleNums= cornerMap.getValue();
                    for (String settleNum : settleNums) {
                        Integer min= SettleNumUtils.CORNER_15_Min_Map.get(settleNum);
                        //当前数据商的灰色区间为真 调用权重校验接口
                        isCornerGray =  judgeGrayStatus(linkId, matchEventInfo, weithtTemplate,matchEventInfo.getDataSourceCode(),"min15Corner",min, matchEventInfo.getSportId(),isGray);
                        if(isCornerGray){
                            checkIsGreyDto.setThisDataSourceIsGray(1);
                            checkIsGreyDto.getSettleNum().add(settleNum);
                        }
                    }
                }
            }

        if (isCornerGray) {
            checkIsGreyDto.setIsGrey(1);
        }
        return checkIsGreyDto;
    }


    public CheckIsGreyDto checkIsGreyFaCard(String linkId, MatchSettleTemplate grayTemplate, MatchSettleTemplate weithtTemplate, MatchEventInfo matchEventInfo, Integer tournamentLevel) {
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
                    if ( matchEventInfo.getSecondsFromStart() > cornerMap.getKey()*60 || matchEventInfo.getSecondsFromStart() <= (cornerMap.getKey()-15)* 60L) {
                        continue;
                    }
                    Long start = cornerMap.getKey() * 60L - bookingSeconds;
                    Long end = cornerMap.getKey() * 60L + bookingSeconds;
                    boolean isGray = false;
                    if ( secondsFromStart >= start && secondsFromStart <= end  ) {
                        isGray= true;
                    }
                    List<String> settleNums= cornerMap.getValue();
                    for (String settleNum : settleNums) {
                        Integer min= SettleNumUtils.BOOKING_15_Min_Map.get(settleNum);
                        //当前数据商的灰色区间为真 调用权重校验接口
                        isBookingGray =  judgeGrayStatus(linkId, matchEventInfo, weithtTemplate,matchEventInfo.getDataSourceCode(),"booking15Min",min, matchEventInfo.getSportId(), isGray);
                        if(isBookingGray){
                            checkIsGreyDto.setThisDataSourceIsGray(1);
                            checkIsGreyDto.getSettleNum().add(settleNum);
                        }
                    }
                }
            }

        if (isBookingGray) {
            checkIsGreyDto.setIsGrey(1);
        }
        return checkIsGreyDto;
    }

    public CheckIsGreyDto checkDataSourceFiveGray(String linkId, MatchSettleTemplate grayTemplate, MatchSettleTemplate weithtTemplate, MatchEventInfo matchEventInfo, Integer tournamentLevel) {
        CheckIsGreyDto checkIsGreyDto = new CheckIsGreyDto();
        //角球不结算5分钟
        if(matchEventInfo.getEventCode().equals("corner")){
            return checkIsGreyDto;
        }
        boolean isFiveGray = false;
        Integer fiveSeconds = 5;
//        fiveSeconds = graySecondsByDataSource("min5Goal" , dataSourceCode, tournamentLevel, fiveSeconds);
        fiveSeconds = settleTemplateService.getTemplateGraySeconds("min5Goal",matchEventInfo.getDataSourceCode(),grayTemplate);
        log.info("linkId::{}::Template:matchId:{},min5Goal:{},event:{}",linkId, matchEventInfo.getStandardMatchId(), fiveSeconds,matchEventInfo);
        if(fiveSeconds==null){
            fiveSeconds=5;
        }
        List<Integer> list = Lists.newArrayList();
        list.addAll(HTMinutes);
        list.addAll(FTMinutes);

        for (Integer m : list) {
            if ( matchEventInfo.getSecondsFromStart() > m*60 || matchEventInfo.getSecondsFromStart() <= (m-5)* 60L) {
                continue;
            }

            Long start = m * 60L - fiveSeconds;
            Long end = m * 60L + fiveSeconds;

            boolean isGray = false;
            if ( matchEventInfo.getSecondsFromStart() >= start && matchEventInfo.getSecondsFromStart() <= end  ) {
                isGray = true;
            }
            List<String> settleNums= fiveSettleNumMap.get(m);
            for (String settleNum : settleNums) {
                String grayType= "min5Goal";
                Integer min= SettleNumUtils.GOAL_5_Min_Map.get(settleNum);
                if(min==null){
                    min= SettleNumUtils.GOAL_15_Min_Map.get(settleNum);
                    grayType="min15Goal";
                }
                //当前数据商的灰色区间为真 调用权重校验接口
                isFiveGray =  judgeGrayStatus(linkId, matchEventInfo, weithtTemplate,matchEventInfo.getDataSourceCode(),grayType,min, matchEventInfo.getSportId(),isGray);
                if(isFiveGray){
                    checkIsGreyDto.setThisDataSourceIsGray(1);
                    checkIsGreyDto.getSettleNum().add(settleNum);
                }
            }
        }
        if (checkIsGreyDto.getSettleNum().size()!=0) {
            checkIsGreyDto.setIsGrey(2);
        }
        return checkIsGreyDto;
    }


    public CheckIsGreyDto checkDataSourceFifteenGray(String linkId, MatchSettleTemplate grayTemplate, MatchSettleTemplate weithtTemplate, MatchEventInfo matchEventInfo, Integer tournamentLevel ) {
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
            if ( matchEventInfo.getSecondsFromStart() > m*60 || matchEventInfo.getSecondsFromStart() <= (m-15)* 60L) {
                continue;
            }
            Long start = m * 60L - fifteenSeconds;
            Long end = m * 60L + fifteenSeconds;
            boolean isGray = false;
            if ( matchEventInfo.getSecondsFromStart() >= start && matchEventInfo.getSecondsFromStart() <= end  ) {
                isGray = true;
            }
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
                isFifteenGray =  judgeGrayStatus(linkId, matchEventInfo, weithtTemplate,matchEventInfo.getDataSourceCode(),"min15Goal",min, matchEventInfo.getSportId(), isGray);
                if(isFifteenGray){
                    checkIsGreyDto.setThisDataSourceIsGray(1);
                    checkIsGreyDto.getSettleNum().add(settleNum);
                }
            }
        }
        if (isFifteenGray) {
            checkIsGreyDto.setIsGrey(1);
        }
        return checkIsGreyDto;
    }

    public Boolean judgeGrayStatus(String linkId, MatchEventInfo matchEventInfo, MatchSettleTemplate template, String dataSourceCode, String grayType, Integer min, Long sportId, boolean isGray) {
        log.info("linkId::{}::dataSourceCode:{},grayType:{},min:{},isGray:{}",linkId, dataSourceCode,grayType,min,isGray);
        //1.插入当前数据商的灰色区间 去重方式:  id =  standardMatchId+grayType(1 2 3  15进球 5进球 15角球)+min(2位)
        Long grayId =  settleTemplateService.getGrayId(matchEventInfo.getStandardMatchId(),dataSourceCode,grayType,min);
        //查询是否有，如果没有则新增
        if(grayId==null){
            return false;
        }

        List<MatchSettleDataSourceSwitch> switches = matchSettleDataSourceSwitchRepository.getModelBySportIdAndDataSource(matchEventInfo.getSportId(),matchEventInfo.getDataSourceCode(),"1");
        if(!switches.isEmpty()){
            MatchSettleGrayWeight matchSettleGrayWeight = matchSettleGrayWeightMapper.selectByPrimaryKey(grayId);
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
                matchSettleGrayWeight.setGrayStatus(isGray?1:0);
                matchSettleGrayWeightMapper.insert(matchSettleGrayWeight);
            }else {
                if (isGray) {
                    matchSettleGrayWeight.setGrayStatus(1);
                    matchSettleGrayWeightMapper.updateByPrimaryKey(matchSettleGrayWeight);
                }
            }
        }


        //2.根据赛事id,min grayType 查询
        MatchSettleGrayWeightExample example =new MatchSettleGrayWeightExample();
        example.createCriteria()
                .andStandardMatchIdEqualTo(matchEventInfo.getStandardMatchId())
                .andSportIdEqualTo(sportId)
                .andGrayCodeEqualTo(grayType)
                .andGrayAreaMinEqualTo(min)
                .andGrayStatusEqualTo(1);
        List<MatchSettleGrayWeight> list =matchSettleGrayWeightMapper.selectByExample(example);
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
        MatchSettleGrayWeightExample example2 =new MatchSettleGrayWeightExample();
        example2.createCriteria()
                .andGrayAreaMinEqualTo(min)
                .andSportIdEqualTo(sportId)
                .andStandardMatchIdEqualTo(matchEventInfo.getStandardMatchId())
                .andGrayCodeEqualTo(grayType)
                .andGrayStatusEqualTo(0);
        List<MatchSettleGrayWeight> list2 =matchSettleGrayWeightMapper.selectByExample(example2);
        Integer weight2 = 0;
        for (MatchSettleGrayWeight settleGrayWeight : list2) {
            Integer w= map.getOrDefault(settleGrayWeight.getDataSourceCode(), 0);
            weight2+=w;
        }

        log.info("linkId::{}::Template:matchId:{},matchEventId:{},GrayWeightSum:{},NoGrayWeightSum:{}",linkId, matchEventInfo.getStandardMatchId(),matchEventInfo.getId(), weight,weight2);
        return weight>=50&&weight>weight2;
    }

}
