//package com.panda.merge.dubbo;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.panda.merge.api.IBasketballMatchScoresSettleApi;
//import com.panda.merge.check.IMatchSettleCheckService;
//import com.panda.merge.common.enums.BasketBallSettleNumEnum;
//import com.panda.merge.common.enums.OperateLogTypeEnum;
//import com.panda.merge.common.utils.TimeUtils;
//import com.panda.merge.config.RedisService;
//import com.panda.merge.constant.MatchSettleCheckConstant;
//import com.panda.merge.constant.MatchSettleScoreConstant;
//import com.panda.merge.dto.*;
//import com.panda.merge.dto.advertise.MatchSettleSwitcherDto;
//import com.panda.merge.dto.settle.*;
//import com.panda.merge.mapper.*;
//import com.panda.merge.model.*;
//import com.panda.merge.mq.producer.MatchSettleCenterProducer;
//import com.panda.merge.mq.producer.MatchSettleScoresProducer;
//import com.panda.merge.respository.MatchSettleInfoRepository;
//import com.panda.merge.respository.MatchSettleRollBackInfoRepository;
//import com.panda.merge.respository.StandardMatchInfoRepository;
//import com.panda.merge.service.*;
//import com.panda.merge.utils.BasketBallSettleScoreUtils;
//import com.panda.merge.utils.MatchEventInfoSettleUtils;
//import com.panda.merge.utils.SettleCheckUtils;
//import com.panda.merge.utils.SettleNumUtils;
//import io.netty.util.internal.StringUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.dubbo.config.annotation.DubboService;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//import static com.panda.merge.constant.MatchSettleScoreConstant.MatchSettleScoreStatus.CONFIRM;
//import static com.panda.merge.constant.MatchSettleScoreConstant.MatchSettleScoreStatus.NOT_CONFIRM;
//import static com.panda.merge.constant.MatchSettleScoreConstant.MatchSettleScoreStatus.NOT_EDIT;
//import static com.panda.merge.constant.MatchSettleScoreConstant.MatchSettleScoreStatus.SETTLED;
//
//@Service
//@DubboService
//@Slf4j
//public class BasketballMatchScoresSettleApiImpl implements IBasketballMatchScoresSettleApi {
//    @Autowired
//    MatchSettleScoreMapper matchSettleScoreMapper;
//    @Autowired
//    MatchSettleEventMapper matchSettleEventMapper;
//    @Autowired
//    StandardMatchInfoMapper standardMatchInfoMapper;
////    @Autowired
////    StandardSportTournamentMapper standardSportTournamentMapper;
//    @Autowired
//    MatchSettleScoresProducer matchSettleScoresProducer;
//    @Autowired
//    MatchSettleInfoMapper matchSettleInfoMapper;
//    @Autowired
//    MatchSettleOperateLogMapper matchSettleOperateLogMapper;
//    @Autowired
//    IMatchSettleService matchSettleService;
//    @Autowired
//    IMatchSettleLogService iMatchSettleLogService;
//    @Autowired
//    RedisService redisService;
//    @Autowired
//    MatchSettleThirdScoreMapper matchSettleThirdScoreMapper;
//    @Autowired
//    MatchSettleThirdEventMapper matchSettleThirdEventMapper;
//    @Autowired
//    IWsPushService wsPushService;
//    @Autowired
//    MatchSettleCenterProducer matchSettleCenterProducer;
//    @Autowired
//    IMatchSettleScoreEventMapper iMatchSettleScoreEventMapper;
//    @Autowired
//    IMatchSettleCheckService matchSettleCheckService;
//    @Autowired
//    MatchSettleRollBackInfoMapper matchSettleRollBackInfoMapper;
//    @Autowired
//    MatchSettleCheckInfoMapper matchSettleCheckInfoMapper;
//    @Autowired
//    IBasketballInSettleService basketballInSettleService;
//    @Autowired
//    MatchSettleInfoRepository matchSettleInfoRepository;
//    @Autowired
//    IMatchSettleScoreService matchSettleScoreService;
//    @Autowired
//    MatchSettleRollBackInfoRepository matchSettleRollBackInfoRepository;
//    @Autowired
//    StandardSportMarketSellService standardSportMarketSellService;
//    @Autowired
//    StandardSportTournamentService standardSportTournamentService;
//    @Autowired
//    StandardMatchInfoService standardMatchInfoService;
//    @Override
//    public List<MatchSettleScoreDto> searchMatchSettleScores(MatchSettleScoreSearchDto settleScoreSearchDto) {
//        MatchSettleScoreExample example =new MatchSettleScoreExample();
//        example.createCriteria().andStandardMatchIdEqualTo(settleScoreSearchDto.getStandardMatchId());
//        List<MatchSettleScore> list =matchSettleScoreMapper.selectByExample(example);
//        List<MatchSettleScoreDto> matchSettleScoreDtos=new ArrayList<>();
//        //查询是否有篮球即时比分，如果没有则新增，而且加redis锁
//        boolean hasInScore = false;
//        boolean has2HTET = false;
//        for (MatchSettleScore matchSettleScore : list) {
//            if(matchSettleScore.getSettleNum().equals("bk_in_rg")){
//                hasInScore=true;
//            }
//            if(matchSettleScore.getSettleNum().equals("bk_in_2htet")){
//                has2HTET=true;
//            }
//        }
//        //如果没有初始化则重查一遍，先初始化
//        if(!hasInScore){
//            addInitInSettleScore(settleScoreSearchDto.getStandardMatchId());
//            list =matchSettleScoreMapper.selectByExample(example);
//        }
//        if(!has2HTET){
//            add2HTETSettleScore(settleScoreSearchDto.getStandardMatchId());
//            list =matchSettleScoreMapper.selectByExample(example);
//        }
//        for (MatchSettleScore matchSettleScore : list) {
//            MatchSettleScoreDto matchSettleScoreDto =new MatchSettleScoreDto();
//            BeanUtils.copyProperties(matchSettleScore,matchSettleScoreDto);
//            matchSettleScoreDto.setSettleNum(matchSettleScore.getSettleNum());
//            matchSettleScoreDto.setId(matchSettleScore.getId().toString());
//            matchSettleScoreDto.setScoresPeriodFreeze(matchSettleScore.getSettleFreeze());
//            MatchEventInfoSettleUtils.checkInfoKey(matchSettleScoreDto);
//            matchSettleScoreDtos.add(matchSettleScoreDto);
//        }
//        list.sort(new Comparator<MatchSettleScore>() {
//            @Override
//            public int compare(MatchSettleScore o1, MatchSettleScore o2) {
//                BasketBallSearchScoreCompareDto compareDto1=new BasketBallSearchScoreCompareDto();
//                BeanUtils.copyProperties(o1,compareDto1);
//                BasketBallSearchScoreCompareDto compareDto2=new BasketBallSearchScoreCompareDto();
//                BeanUtils.copyProperties(o2,compareDto2);
//                return compareDto1.compareTo(compareDto2);
//            }
//        });
//        //查询 当前用户的 阶段比分的明细的审核状态
//        matchSettleCheckService.searchCheckStatusByScoresList(matchSettleScoreDtos,settleScoreSearchDto.getOperatorName());
//        setRollBackStatusScores(matchSettleScoreDtos,settleScoreSearchDto.getStandardMatchId());
//        setBkInScoreTag(matchSettleScoreDtos,settleScoreSearchDto.getStandardMatchId());
//        //查询回滚状态
//        return matchSettleScoreDtos;
//    }
//
//    private void setBkInScoreTag(List<MatchSettleScoreDto> matchSettleScoreDtos,Long standardMatchId){
//        MatchSettleThirdScoreExample example =new MatchSettleThirdScoreExample();
//        example.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
//        List<MatchSettleThirdScore> list =matchSettleThirdScoreMapper.selectByExample(example);
//        matchSettleScoreDtos.forEach(matchSettleScoreDto ->{
//        if (BasketBallSettleScoreUtils.IN_SETTLE_NUM_LIST.contains(matchSettleScoreDto.getSettleNum())&&null!=matchSettleScoreDto.getT1()&&null!=matchSettleScoreDto.getT2()){
//            list.forEach(matchSettleThirdScore->{
//                boolean tag = false;
//                if (matchSettleThirdScore.getSettleNum().equals(matchSettleScoreDto.getSettleNum())){
//                    if (null==matchSettleThirdScore.getT1()||null==matchSettleThirdScore.getT2()){
//                        tag = true;
//                    }else  {
//                        if (matchSettleThirdScore.getT1()!=matchSettleScoreDto.getT1()||matchSettleThirdScore.getT2() != matchSettleScoreDto.getT2()){
//                            tag = true;
//                        }
//                    }
//                }
//                if (tag){
//                    matchSettleScoreDto.setScoreCheckTag(3);
//                }
//            });
//        }
//        });
//
//
//    }
//
//
//    private void add2HTETSettleScore(Long standardMatchId) {
//        String key = "add2HTETSettleScore:"+standardMatchId;
//        try{
//            if(redisService.tryLock(key,key,2,3)){
//                //重查一遍
//                List<MatchSettleScore> list= BasketBallSettleScoreUtils.createBasket2HTETSettleScore(standardMatchId);
//                for (MatchSettleScore matchSettleScore : list) {
//                    matchSettleScoreMapper.insert(matchSettleScore);
//                }
//            }
//        }catch (Exception e){
//            log.error("BasketballMatchScoresSettleApiImpl-add2HTETSettleScore:",e);
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//
//    private void addInitInSettleScore(Long standardMatchId) {
//        String key = "addInitInSettleScore:"+standardMatchId;
//        try{
//            if(redisService.tryLock(key,key,2,3)){
//                //重查一遍
//                List<MatchSettleScore> list= BasketBallSettleScoreUtils.createBasketInSettleScore(standardMatchId);
//                for (MatchSettleScore matchSettleScore : list) {
//                    matchSettleScoreMapper.insert(matchSettleScore);
//                }
//            }
//        }catch (Exception e){
//            log.error("BasketballMatchScoresSettleApiImpl-addInitInSettleScore:",e);
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//
//    @Override
//    public ThirdMatchSettleScoresDto searchThirdMatchSettleScores(MatchSettleScoreSearchDto settleScoreSearchDto) {
//        MatchSettleThirdScoreExample example =new MatchSettleThirdScoreExample();
//        example.createCriteria().andStandardMatchIdEqualTo(settleScoreSearchDto.getStandardMatchId());
//        List<MatchSettleThirdScore> list =matchSettleThirdScoreMapper.selectByExample(example);
//        List<MatchSettleScoreDto> matchSettleScoreDtos=new ArrayList<>();
//        for (MatchSettleThirdScore matchSettleScore : list) {
//            MatchSettleScoreDto matchSettleScoreDto =new MatchSettleScoreDto();
//            BeanUtils.copyProperties(matchSettleScore,matchSettleScoreDto);
//            matchSettleScoreDto.setSettleNum(matchSettleScore.getSettleNum());
//            matchSettleScoreDto.setId(matchSettleScore.getId().toString());
//            matchSettleScoreDto.setScoresPeriodFreeze(matchSettleScore.getSettleFreeze());
//            MatchEventInfoSettleUtils.checkInfoKey(matchSettleScoreDto);
//            matchSettleScoreDtos.add(matchSettleScoreDto);
//        }
//        //2.根据数据商比分组
//        Map<String,List<MatchSettleScoreDto>> map = matchSettleScoreDtos.stream().collect(Collectors.groupingBy(MatchSettleScoreDto::getDataSourceCode));
//        //3.组装数据返回前端
//        ThirdMatchSettleScoresDto thirdMatchSettleScoresDto =new ThirdMatchSettleScoresDto();
//        thirdMatchSettleScoresDto.setEventCode(settleScoreSearchDto.getEventCode());
//        thirdMatchSettleScoresDto.setStandardMatchId(settleScoreSearchDto.getStandardMatchId());
//        thirdMatchSettleScoresDto.setThirdMatchScoresMap(map);
//        //4.log日志记录异常报错以及耗时
//        return thirdMatchSettleScoresDto;
//    }
//    /**
//     * 查询比分回滚状态
//     * @param scores
//     * @param stndardMatchId
//     */
//    private void setRollBackStatusScores(List<MatchSettleScoreDto> scores,Long stndardMatchId){
//        if(scores != null && scores.size() > 0){
//            List<MatchSettleRollBackInfo> list =matchSettleRollBackInfoRepository.getMatchSettleRollBackInfoByStandardMatchId(stndardMatchId);
//            Map<String,MatchSettleRollBackInfo> map =new HashMap<>();
//            for (MatchSettleRollBackInfo matchSettleRollBackInfo : list) {
//                map.put(matchSettleRollBackInfo.getId().toString(),matchSettleRollBackInfo);
//            }
//            for (MatchSettleScoreDto score : scores) {
//                MatchSettleRollBackInfo info =map.get(score.getId());
//                if(info!=null){
//                    score.setRollBackStatus(info.getRollBackStatus());
//                    score.setRollBackOrderCount(info.getRollBackOrderCount());
//                }
//            }
//        }
//    }
//
//
//    @Override
//    public Response updateMatchSettleScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto) {
//        log.info("basketball updateMatchSettleScore,matchSettleScoreDto: {}",matchSettleScoreDto);
//        String key ="MATCH_SETTLE_INFO:"+ matchSettleScoreDto.getMatchScoreId();
//        if(matchSettleService.checkIfOverSettleTime(matchSettleScoreDto.getStandardMatchId())){
//            return Response.failed("1031930");
//        }
//        try {
//            if(redisService.tryLock(key,key,2,5)) {
//                //0.加redis锁
//                StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchSettleScoreDto.getStandardMatchId());
//                if (standardMatchInfo == null) {
//                    return Response.failed("1031931");
//                }
//                String forwScore ="" ;
//                MatchSettleScore matchSettleBefore = new MatchSettleScore();
//                MatchSettleScore matchSettleScore  = matchSettleScoreMapper.selectByPrimaryKey(matchSettleScoreDto.getMatchScoreId());
//                if (matchSettleScore == null) {
//                    return Response.failed("1031931");
//                }
//                //删除事件不能编辑
//                if (null!=matchSettleScore.getHasDeleteEvent()&&matchSettleScore.getHasDeleteEvent()==1){
//                    return Response.failed("1031961");
//                }
//                //篮球结算顺序拦截
//                if(matchSettleScore.getSportId().equals(2L)){
//                    if(!matchSettleCheckService.checkBasketPeriodScoreOrder(matchSettleScore)){
//                        return Response.failed("10138");
//                    }
//                }
//                //修改前比分
//                forwScore= matchSettleScore.getT1()+"-"+ matchSettleScore.getT2();
//                String t1 =matchSettleScore.getT1()==null ?"":matchSettleScore.getT1().toString();
//                String t2 =matchSettleScore.getT2()==null ?"":matchSettleScore.getT2().toString();
//                forwScore= t1+"-"+t2;
//
//                BeanUtils.copyProperties(matchSettleScore,matchSettleBefore);
//                matchSettleScore.setT1(matchSettleScoreDto.getT1());
//                matchSettleScore.setT2(matchSettleScoreDto.getT2());
//                matchSettleScore.setStatus(NOT_CONFIRM);
//                matchSettleScore.setOperater(matchSettleScoreDto.getOperatorName());
//                if(matchSettleScoreDto.getGoWaterStatus()!=null&&matchSettleScoreDto.getGoWaterStatus()==1){
//                    matchSettleScore.setGoWaterStatus(1);
//                }else {
//                    matchSettleScore.setGoWaterStatus(0);
//                }
//                matchSettleScore.setExtryInfo(matchSettleScoreDto.getExtryInfo());
//                matchSettleScore.setModifyTime(System.currentTimeMillis());
//                //比分判断是否相同
//                if(MatchEventInfoSettleUtils.equileMatchSettleScores(matchSettleBefore,matchSettleScore)){
//                    return Response.failed("1031940");
//                }
//                matchSettleScoreMapper.updateByPrimaryKey(matchSettleScore);
//                //2.判断更新上半场(5)和全场比分(10) 更新结算信息
//                if (matchSettleScore.getSettleNum().equals("bk_1ht") || matchSettleScore.getSettleNum().equals("bk_ft_rg")) {
//                    recordScore(matchSettleScoreDto);
//                }
//                wsPushService.pushBasketballStandardSettleScores(matchSettleScoreDto.getStandardMatchId(),
//                        matchSettleScoreDto.getEventCode());
//                //3.操作日志记录
//                String settleScoreJson = JSON.toJSONString(matchSettleScoreDto);
//                UpdateMatchSettleScoreDto updateMatchSettleScoreDto = JSON.parseObject(settleScoreJson,UpdateMatchSettleScoreDto.class);
//                updateMatchSettleScoreDto.setBasketBallSettleNum(matchSettleScoreDto.getSettleNum());
//                iMatchSettleLogService.updateMatchSettleScoreAddLog(updateMatchSettleScoreDto,forwScore,matchSettleScore,standardMatchInfo,OperateLogTypeEnum.EDIT.getCode().toString());
//                return Response.success();
//            }else {
//                return Response.failed("1031933");
//            }
//        }catch (Exception e){
//            log.error("BasketballMatchScoresSettleApiImpl-updateMatchSettleScore:",e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//
//
//
//
//
//    //更新结算表中比分
//    private void recordScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto){
//        MatchSettleInfo   matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(matchSettleScoreDto.getStandardMatchId());
//        if (matchSettleInfo!=null) {
//
//            matchSettleInfo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
//            if (matchSettleScoreDto.getSettleNum().equals("bk_1ht")) {
//                matchSettleInfo.setH1T1(matchSettleScoreDto.getT1());
//                matchSettleInfo.setH1T2(matchSettleScoreDto.getT2());
//            }else if(matchSettleScoreDto.getSettleNum().equals("bk_ft_rg")){
//                matchSettleInfo.setFtT1(matchSettleScoreDto.getT1());
//                matchSettleInfo.setFtT2(matchSettleScoreDto.getT2());
//            }
//            //更新结算信息
//            matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
//        }else {
//            log.error("参数异常【matchSettleInfos为空! 】");
//        }
//
//    }
//
//    @Override
//    public Response confirmMatchSettleScore(ConfirmMatchSettleScoreDto matchSettleScoreDto) {
//        log.info("比分Id::{}:: 当前比分被确认参数:{} ",matchSettleScoreDto.getMatchScoreId(),matchSettleScoreDto);
//        //0.加redis锁
//        String key ="MATCH_SETTLE_INFO:"+ matchSettleScoreDto.getMatchScoreId();
//        if(matchSettleService.checkIfOverSettleTime(matchSettleScoreDto.getStandardMatchId())){
//            return Response.failed("1031930");
//        }
//        try {
//            if(redisService.tryLock(key,key,2,5)) {
//                MatchSettleScore matchSettleScore =null;
//                matchSettleScore=matchSettleScoreMapper.selectByPrimaryKey(matchSettleScoreDto.getMatchScoreId());
//                if(matchSettleScore==null){
//                    return Response.failed("1031931");
//                }
//                if(matchSettleScore.getStatus()>=CONFIRM){
//                    return Response.failed("1031934");
//                }
//                Integer status = matchSettleScore.getStatus();
//                matchSettleScore.setStatus(CONFIRM);
//                matchSettleScore.setT1(matchSettleScoreDto.getT1());
//                matchSettleScore.setT2(matchSettleScoreDto.getT2());
//                matchSettleScore.setModifyTime(System.currentTimeMillis());
//                matchSettleScoreMapper.updateByPrimaryKey(matchSettleScore);
//                //2.记录日志
//                //走水  将编码设置为8
//                if (matchSettleScore.getGoWaterStatus()!=null&&matchSettleScore.getGoWaterStatus().equals(1)){
//                    matchSettleScore.setExtryInfo("8");
//                }
//                iMatchSettleLogService.matchSettleScoreAddLog(matchSettleScore,matchSettleScoreDto.getOperatorName(),
//                        OperateLogTypeEnum.CONFIRM_SCORE,"",matchSettleScoreDto.getIpAddress());
//                wsPushService.pushBasketballStandardSettleScores(matchSettleScoreDto.getStandardMatchId(),
//                        matchSettleScoreDto.getEventCode());
//                return Response.success();
//            }else {
//                return Response.failed("1031933");
//            }
//        }catch (Exception e){
//            log.error("BasketballMatchScoresSettleApiImpl-confirmMatchSettleScore:",e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//
//
//    @Override
//    public Response settleMatchScore(SettleMatchScoreDto matchSettleScoreDto) {
//        log.info("basketball settleMatchScore,matchSettleScoreDto: {}",matchSettleScoreDto);
//        String key ="MATCH_SETTLE_INFO:"+ matchSettleScoreDto.getMatchScoreId();
//        if(matchSettleService.checkIfOverSettleTime(matchSettleScoreDto.getStandardMatchId())){
//            return Response.failed("1031930");
//        }
//        if(redisService.hasKey("SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getMatchScoreId())){
//            return Response.failed("1031960");
//        }
//        try {
//            if(redisService.tryLock(key,key,2,5)) {
//                MatchSettleScore matchSettleScore =null;
//                matchSettleScore=matchSettleScoreMapper.selectByPrimaryKey(matchSettleScoreDto.getMatchScoreId());
//                if(matchSettleScore==null){
//                    return Response.failed("1031931");
//                }
//                if(matchSettleScore.getStatus()!=CONFIRM){
//                    return Response.failed("1031932");
//                }
//                Integer settleTimes =matchSettleScore.getSettleTimes();
//                if(settleTimes==null){
//                    settleTimes=0;
//                }
//                if (matchSettleScore.getSettleCount()== null ) {
//                    matchSettleScore.setSettleCount(0);
//                }
//                settleTimes++;
//
//                //二次结算,必须给出结算原因
//                if (matchSettleScore.getSettleCount() >  0 &&
//                        (matchSettleScoreDto.getSettleReason()==null  ||
//                                matchSettleScoreDto.getSettleReason()== 0) ) {
//                    return Response.failed("1031953");
//                }
//
//                //比较缓存是否相同比分
////                if(compareDiffScoresByCache(matchSettleScore)){
////                    settleTimes++;
////                }
//                String  before= "-";
//                Integer settleReason = matchSettleScore.getSettleReason();
//                if (settleReason != null &&  settleReason != 0 ) {
//                    before = settleReason.toString();
//                    if (settleReason == 118) {
//                        before += ": "+matchSettleScore.getSettleReasonDetail();
//                    }
//                }
//                //这是理论时间不对 应该先查数据商，如果没数据商再赋值当前
//                if(matchSettleScore.getEventTime()==null||matchSettleScore.getEventTime().equals(0l)){
//                    Long eventTime =matchSettleCheckService.searchEventTimeByScores(matchSettleScore);
//                    if(eventTime==0l){
//                        eventTime=matchSettleScore.getModifyTime();
//                    }
//                    matchSettleScore.setEventTime(eventTime);
//                }
//                matchSettleScore.setStatus(SETTLED);
//                matchSettleScore.setOperater(matchSettleScoreDto.getOperatorName());
//                matchSettleScore.setSettleTimes(settleTimes);
//                matchSettleScore.setSettleCount(matchSettleScore.getSettleCount()+1);
//                matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.SETTLE);
//                matchSettleScore.setUserid(matchSettleScoreDto.getOperatorId());
//                matchSettleScore.setModifyTime(System.currentTimeMillis());
//                matchSettleScore.setSettleReason(matchSettleScoreDto.getSettleReason());
//                matchSettleScore.setSettleReasonDetail(matchSettleScoreDto.getSettleReasonDetail());
//                matchSettleScore.setIsGrey(0);
//                matchSettleScore.setHasDeleteEvent(0);
//                matchSettleScore.setCurrentEventStatus(0);
//
//                MatchSettleThirdScoreExample example = new MatchSettleThirdScoreExample();
//                List<String> l = new ArrayList();
//                l.add(matchSettleScore.getSettleNum());
//                example.createCriteria().andSettleNumIn(l).andStandardMatchIdEqualTo(matchSettleScore.getStandardMatchId()) ;
//                List<MatchSettleThirdScore> matchSettleThirdScores =matchSettleThirdScoreMapper.selectByExample(example);
//                boolean tag =false;
//                if (!matchSettleThirdScores.isEmpty()){
//                    for (int i =0;i<matchSettleThirdScores.size();i++ ){
//                        if (!matchSettleThirdScores.get(i).getT1().equals(matchSettleScore.getT1())||!matchSettleThirdScores.get(i).getT2().equals(matchSettleScore.getT2())){
//                            tag = true;
//                        }
//                    }
//                }
//                if (tag){
//                    matchSettleScore.setCurrentEventTag(1);
//                    MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(matchSettleScore.getStandardMatchId());
//                    matchSettleInfo.setCurrentEventTag(1);
//                    matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
//                }
//                //篮球结算后去掉比分带入弹框
//                matchSettleScore.setPopupUsers(null);
//                matchSettleScoreMapper.updateByPrimaryKey(matchSettleScore);
//                matchSettleCheckService.updateMatchGrayStatus(matchSettleScore.getStandardMatchId());
//                matchSettleCheckService.updateMatchCurrentEventStatus(matchSettleScore.getStandardMatchId());
//                //校驗比分更新標記
//                verifyScoresIsSame(matchSettleScore);
//                //结算时把回滚订单数清零
//                matchSettleService.settleRollBackSetNullOrderCount(matchSettleScore.getId());
//
////                //3486 新增篮球即时校验
////                boolean result = basketballInSettleService.rollBackSettleInScore(matchSettleScore);
////                if (result){
////                    //延迟下发
////                    log.info("sendSyncMQMessage: {}",matchSettleScore);
////                    matchSettleScoresProducer.sendSyncMQMessage(matchSettleScore);
////                }else {
////                    //即时下发
////                    matchSettleScoresProducer.sendMatchSettleScores(matchSettleScore);
////                }
//                matchSettleScoresProducer.sendMatchSettleScores(matchSettleScore);
//                wsPushService.pushBasketballStandardSettleScores(matchSettleScoreDto.getStandardMatchId(),
//                        matchSettleScoreDto.getEventCode());
//
//                //1.比分结算增加操作日志
//                if (matchSettleScore.getGoWaterStatus()!=null&&matchSettleScore.getGoWaterStatus().equals(1)) {
//                    matchSettleScore.setExtryInfo("8");
//                }
//
//
//                iMatchSettleLogService.matchSettleScoreAddLog(matchSettleScore,matchSettleScoreDto.getOperatorName(),OperateLogTypeEnum.SCORE_SETTLE,before,matchSettleScoreDto.getIpAddress());
//
//
//                return Response.success();
//            }else {
//                return Response.failed("1031933");
//            }
//        }catch (Exception e){
//            log.error("BasketballMatchScoresSettleApiImpl-settleMatchScore:",e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//
//    @Override
//    public Response reSettleMatchScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto) {
//        log.info("basketball reSettleMatchScore,matchSettleScoreDto: {}",matchSettleScoreDto);
//        String key ="MATCH_SETTLE_INFO:"+ matchSettleScoreDto.getMatchScoreId();
//        try {
//            if(redisService.tryLock(key,key,2,5)) {
//                MatchSettleScore matchSettleScore = matchSettleScoreMapper.selectByPrimaryKey(matchSettleScoreDto.getMatchScoreId());
//                if(matchSettleScore==null){
//                    return Response.failed("1031935");
//                }
//                Integer settleTimes =matchSettleScore.getSettleTimes();
//                if(settleTimes!=null&&settleTimes>0){
//                }else {
//                    return Response.failed("1031938");
//                }
//                matchSettleScore.setModifyTime(System.currentTimeMillis());
//                matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
//                matchSettleScore.setSettleTimes(settleTimes);
//                matchSettleScore.setOperater(matchSettleScoreDto.getOperatorName());
//                matchSettleScore.setUserid(matchSettleScoreDto.getOperatorId());
//                matchSettleScoreMapper.updateByPrimaryKey(matchSettleScore);
//
//                //2.MQ下发
//                MatchSettleScoreMessage Score = new MatchSettleScoreMessage();
//                BeanUtils.copyProperties(matchSettleScore,Score);
//                Score.setLevel(3);
//                matchSettleScoresProducer.sendMatchSettleScores(Score);
//
//
//                //1.比分结算增加操作日志
//                //走水设置编码为8
//                if (matchSettleScore.getGoWaterStatus()!=null&&matchSettleScore.getGoWaterStatus().equals(1))  matchSettleScore.setExtryInfo("8");
//                iMatchSettleLogService.matchSettleScoreAddLog(matchSettleScore,matchSettleScoreDto.getOperatorName(),
//                        OperateLogTypeEnum.ROLLBACK_EXECUTE,"",matchSettleScoreDto.getIpAddress());
//
//                return Response.success();
//            }else {
//                return Response.failed("1031933");
//            }
//        }catch (Exception e){
//            log.error("BasketballMatchScoresSettleApiImpl-reSettleMatchScore:",e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//
//    /**
//     * 回滚结算
//     * */
//    @Override
//    public Response rollBackSettleMatchScores(UpdateBasketBallSettleScoreDto matchSettleScoreDto) {
//        log.info("basketball rollBackSettleMatchScores,matchSettleScoreDto: {}",matchSettleScoreDto);
//        String key ="MATCH_SETTLE_INFO:"+ matchSettleScoreDto.getMatchScoreId();
//        if(matchSettleService.checkIfOverSettleTime(matchSettleScoreDto.getStandardMatchId())){
//            return Response.failed("1031930");
//        }
//        try {
//            /*if(redisService.tryLock(key,key,2,5)) {*/
//            MatchSettleScore matchSettleScore = matchSettleScoreMapper.selectByPrimaryKey(matchSettleScoreDto.getMatchScoreId());
//            if(matchSettleScore==null){
//                return Response.failed("1031935");
//            }
//            MatchSettleScore oIdMatchSettleScore = new   MatchSettleScore();
//            BeanUtils.copyProperties(matchSettleScore,oIdMatchSettleScore);
//            matchSettleScore.setGoWaterStatus(0);
//            matchSettleScore.setStatus(NOT_EDIT);
//            matchSettleScore.setT1(null);
//            matchSettleScore.setT2(null);
//            matchSettleScore.setExtryInfo(null);
//            matchSettleScore.setFirstT1(null);
//            matchSettleScore.setFirstT2(null);
//            matchSettleScore.setSecondT1(null);
//            matchSettleScore.setSecondT2(null);
//            matchSettleScore.setSettleTimes(0);
//            matchSettleScore.setModifyTime(System.currentTimeMillis());
//            matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.ROLL_BACK);
//            matchSettleScore.setOperater(matchSettleScoreDto.getOperatorName());
//            matchSettleScore.setUserid(matchSettleScoreDto.getOperatorId());
//            matchSettleScore.setSettleReasonDetail(null);
//            matchSettleScore.setSettleReason(null);
//            matchSettleScore.setPopupUsers(null);
//            matchSettleScoreMapper.updateByPrimaryKey(matchSettleScore);
//            //将核对信息进行无效处理
//            matchSettleCheckService.rollbackScores(matchSettleScore);
//            //2.MQ下发
//            matchSettleScoresProducer.sendMatchSettleScores(matchSettleScore);
//            wsPushService.pushBasketballStandardSettleScores(matchSettleScoreDto.getStandardMatchId(),
//                    matchSettleScoreDto.getEventCode());
//
//            //1.记录日志
//            iMatchSettleLogService.matchSettleScoreAddLog(oIdMatchSettleScore,matchSettleScore,matchSettleScoreDto.getOperatorName(),
//                    OperateLogTypeEnum.ROLLBACK_SCORES_SETTLE.getCode().toString(),matchSettleScoreDto.getLinkedId(),matchSettleScoreDto.getIpAddress());
//            //回滚新增记录
//            insertRollbackData(matchSettleScoreDto.getStandardMatchId(),matchSettleScoreDto.getMatchScoreId(),1,matchSettleScoreDto.getEventCode(),matchSettleScore.getSettleNum());
//            //回滚保存赛事ID一分钟
//            redisService.set("SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getMatchScoreId(),matchSettleScoreDto.getMatchScoreId(),60);
//            return Response.success();
//            /*}else {
//                return Response.failed("1031933");
//            }*/
//        }catch (Exception e){
//            log.error("BasketballMatchScoresSettleApiImpl-rollBackSettleMatchScores:",e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//
//
//
//    @Override
//    public Response querySettleType(Long StandardMatchId) {
//
//        //1.查询结算信息
//        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(StandardMatchId);
//        if (matchSettleInfo == null || matchSettleInfo.getSettleType()==1) {
//            return   Response.success(1);
//        }else {
//            return   Response.success(2);
//        }
//    }
//
//
//    /**
//     * 新增回滚数据
//     * @param standardMatchId
//     * @param scoreEventId
//     * @param type
//     */
//    private void insertRollbackData(Long standardMatchId,Long scoreEventId,Integer type,String eventCode,String settleNum){
//        MatchSettleRollBackInfo oldInfo = matchSettleRollBackInfoRepository.getMatchSettleRollBackInfo(scoreEventId);
//        Integer isPenalty =0;
//        if(settleNum.equals("1030")||settleNum.equals("1029")||settleNum.equals("1028")){
//            isPenalty=1;
//        }
//        //多次回滚，存在更新，不存在新增
//        if(oldInfo != null){
//            oldInfo.setRollBackStatus(1);
//            oldInfo.setRollBackOrderCount(0l);
//            oldInfo.setOrderCount(0l);
//            oldInfo.setRollBackTime(System.currentTimeMillis());
//            matchSettleRollBackInfoRepository.updateMatchSettleRollBackInfoToRedis(oldInfo,false);
//        } else {
//            MatchSettleRollBackInfo info = new MatchSettleRollBackInfo();
//            info.setId(scoreEventId);
//            info.setSettleScoreEventId(scoreEventId);
//            info.setDataType(type);
//            info.setRollBackStatus(1);
//            info.setRollBackTime(System.currentTimeMillis());
//            info.setStandardMatchId(standardMatchId);
//            info.setCreateTime(System.currentTimeMillis());
//            info.setModifyTime(System.currentTimeMillis());
//            info.setEventCode(eventCode);
//            info.setIsDianQiu(isPenalty);
//            info.setOrderCount(0L);
//            info.setRollBackOrderCount(0L);
//            matchSettleRollBackInfoRepository.updateMatchSettleRollBackInfoToRedis(info,true);
//        }
//    }
//
//    public Response cancelSettleEventTag(MatchSettleSwitcherDto matchSettleSwitcherDto) {
//        log.info("basketball cancelSettleEventTag,matchSettleSwitcherDto: {}",matchSettleSwitcherDto);
//        try {
//            if(matchSettleSwitcherDto.getMatchScoreId()!=null && matchSettleSwitcherDto.getMatchScoreId()>0L){
//                MatchSettleScore settleScore = matchSettleScoreMapper.selectByPrimaryKey(matchSettleSwitcherDto.getMatchScoreId());
//                if(settleScore!=null){
//                    settleScore.setCurrentEventTag(0);
//                    matchSettleScoreMapper.updateByPrimaryKey(settleScore);
//                }
//                boolean tag = true;
//                MatchSettleScoreExample example = new MatchSettleScoreExample();
//                example.createCriteria().andStandardMatchIdEqualTo(matchSettleSwitcherDto.getMatchId());
//                //重新查一遍结算表,判断还有没有提示标记,假如没有则更新结算赛事表信息
//                List<MatchSettleScore> settleScores = matchSettleScoreMapper.selectByExample(example);
//                for (int i = 0;i<settleScores.size();i++){
//                    if (null!=settleScores.get(i).getCurrentEventTag()&&settleScores.get(i).getCurrentEventTag()==1){
//                        tag = false;
//                        break;
//                    }
//                }
//                if (tag){
//                    MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(matchSettleSwitcherDto.getMatchId());
//                    matchSettleInfo.setCurrentEventTag(0);
//                    matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
//                }
//
//            }
//        }catch (Exception e){
//            log.error("BasketballMatchScoresSettleApiImpl-cancelSettleEventTag:",e);
//            return Response.failed();
//        }
//        return Response.success();
//    }
//
//    @Override
//    public Response showPopupScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto) {
//        log.info("showPopupScore传入参数: {}",matchSettleScoreDto);
//        //获取当前需要结算的阶段之前的阶段
//        List<String> settleNumList = SettleNumUtils.countBasketballScoreSettleNumBefore(matchSettleScoreDto.getSettleNum(),matchSettleScoreDto.getMatchLength());
//        MatchSettleScoreExample example = new MatchSettleScoreExample();
//        example.createCriteria().andSettleNumIn(settleNumList).andStandardMatchIdEqualTo(matchSettleScoreDto.getStandardMatchId());
//        List<MatchSettleScore> settleScores = matchSettleScoreMapper.selectByExample(example);
//        if (!settleScores.isEmpty()){
//           for (int i = 0;i<settleScores.size();i++){
//               if (!settleScores.get(i).getStatus().equals(3)){
//                   return Response.failed("该阶段前有未结算的比分，无法同步比分");
//               }
//           }
//
//        }
//        //假如是审核员,则去取核对记录表中的确认比分填充进去
//        if(matchSettleScoreDto.getRoleCode()==2){
//            MatchSettleCheckInfoExample exampleCheck = new MatchSettleCheckInfoExample();
//            log.info("showPopupScore_check: {},{},{}",matchSettleScoreDto.getMatchScoreId(),matchSettleScoreDto.getOperatorName(),matchSettleScoreDto.getStandardMatchId());
//            exampleCheck.createCriteria().andCheckStatusEqualTo(2).andSettleScoreEventIdEqualTo(matchSettleScoreDto.getMatchScoreId()).andUserNameEqualTo(matchSettleScoreDto.getOperatorName()).andStandardMatchIdEqualTo(matchSettleScoreDto.getStandardMatchId());
//            List<MatchSettleCheckInfo> checkInfos = matchSettleCheckInfoMapper.selectByExample(exampleCheck);
//            if (CollectionUtils.isEmpty(checkInfos)){
//                return Response.failed("审核员核对数据,弹框数据异常");
//            }else {
//                MatchSettleCheckInfo checkInfo = checkInfos.get(0);
//                matchSettleScoreDto.setT1(checkInfo.getT1());
//                matchSettleScoreDto.setT2(checkInfo.getT2());
//            }
//
//        }
//
//        List<MatchSettleScore> needSettleScores = new ArrayList<>();
//        List<String> needSettleNum = new ArrayList<>();
//        List<BasketBallPopupSettleScoreDto> popupSettleScores = new ArrayList<>();
//        //case 1: 确认的是第二节比分 settle_num:bk_q204,则带出 上半场bk_1ht比分:Q1+Q2
//        if (matchSettleScoreDto.getSettleNum().equals("bk_q204")){
//            needSettleNum.add("bk_1ht");
//            int home1ht =matchSettleScoreDto.getT1();
//            int away1ht =matchSettleScoreDto.getT2();
//            for (int i =0;i<settleScores.size();i++){
//                if (settleScores.get(i).getSettleNum().equals("bk_q104")){
//                    home1ht = home1ht+settleScores.get(i).getT1();
//                    away1ht = away1ht+settleScores.get(i).getT2();
//                }
//            }
//            MatchSettleScoreExample example1= new MatchSettleScoreExample();
//            example1.createCriteria().andSettleNumIn(needSettleNum).andStandardMatchIdEqualTo(matchSettleScoreDto.getStandardMatchId());
//            List<MatchSettleScore> caseOne = matchSettleScoreMapper.selectByExample(example1);
//            if(caseOne.get(0).getStatus()!=3){
//                BasketBallPopupSettleScoreDto dto = new BasketBallPopupSettleScoreDto();
//                dto.setMatchScoreId(caseOne.get(0).getId());
//                dto.setStandardMatchId(caseOne.get(0).getStandardMatchId());
//                dto.setEventCode(caseOne.get(0).getEventCode());
//                dto.setSettleNum(caseOne.get(0).getSettleNum());
//                dto.setT1(home1ht);
//                dto.setT2(away1ht);
//                dto.setSort(1);
//                popupSettleScores.add(dto);
//            }
//        }
//        /*case 2:确认的是第四节比分  settle_num:bk_q404,则带出
//         1  下半场比分 bk_2ht:Q3+Q4
//         2  全场不含加时比分 bk_ft_rg:Q1+Q2+Q3+Q4
//         3  假如 全场不含加时比分 bk_ft_rg 不相等,这此赛事无加时赛,需要带出  全场比分(含加时):bk_ft_et比分:Q1+Q2+Q3+Q4
//         */
//        if (matchSettleScoreDto.getSettleNum().equals("bk_q404")){
//            needSettleNum.add("bk_2ht");
//            needSettleNum.add("bk_ft_rg");
//            int home2ht =matchSettleScoreDto.getT1();
//            int away2ht =matchSettleScoreDto.getT2();
//            int homeRgEt =matchSettleScoreDto.getT1();
//            int awayRgEt =matchSettleScoreDto.getT2();
//
//            for (int i =0;i<settleScores.size();i++){
//                if (settleScores.get(i).getSettleNum().equals("bk_q304")){
//                    home2ht = home2ht+settleScores.get(i).getT1();
//                    away2ht = away2ht+settleScores.get(i).getT2();
//                    homeRgEt = homeRgEt+settleScores.get(i).getT1();
//                    awayRgEt = awayRgEt+settleScores.get(i).getT2();
//                }
//                if (settleScores.get(i).getSettleNum().equals("bk_q104")||settleScores.get(i).getSettleNum().equals("bk_q204")){
//                    homeRgEt = homeRgEt+settleScores.get(i).getT1();
//                    awayRgEt = awayRgEt+settleScores.get(i).getT2();
//                }
//
//            }
//            if (homeRgEt!=awayRgEt) {
//                needSettleNum.add("bk_ft_et");
//                needSettleNum.add("bk_2htet");
//            }
//            MatchSettleScoreExample example1= new MatchSettleScoreExample();
//            example1.createCriteria().andSettleNumIn(needSettleNum).andStandardMatchIdEqualTo(matchSettleScoreDto.getStandardMatchId());
//            List<MatchSettleScore> caseTwo = matchSettleScoreMapper.selectByExample(example1);
//            for (int i =0;i<caseTwo.size();i++){
//                if (caseTwo.get(i).getSettleNum().equals("bk_2ht")&&caseTwo.get(i).getStatus()!=3){
//                    BasketBallPopupSettleScoreDto dto = new BasketBallPopupSettleScoreDto();
//                    dto.setMatchScoreId(caseTwo.get(i).getId());
//                    dto.setStandardMatchId(caseTwo.get(i).getStandardMatchId());
//                    dto.setEventCode(caseTwo.get(i).getEventCode());
//                    dto.setSettleNum(caseTwo.get(i).getSettleNum());
//                    dto.setT1(home2ht);
//                    dto.setT2(away2ht);
//                    dto.setSort(2);
//                    popupSettleScores.add(dto);
//                }
//                if (caseTwo.get(i).getSettleNum().equals("bk_ft_rg")&&caseTwo.get(i).getStatus()!=3){
//                    BasketBallPopupSettleScoreDto dto = new BasketBallPopupSettleScoreDto();
//                    dto.setMatchScoreId(caseTwo.get(i).getId());
//                    dto.setStandardMatchId(caseTwo.get(i).getStandardMatchId());
//                    dto.setEventCode(caseTwo.get(i).getEventCode());
//                    dto.setSettleNum(caseTwo.get(i).getSettleNum());
//                    dto.setT1(homeRgEt);
//                    dto.setT2(awayRgEt);
//                    dto.setSort(4);
//                    popupSettleScores.add(dto);
//                }
//                if (caseTwo.get(i).getSettleNum().equals("bk_ft_et")&&caseTwo.get(i).getStatus()!=3){
//                    BasketBallPopupSettleScoreDto dto = new BasketBallPopupSettleScoreDto();
//                    dto.setMatchScoreId(caseTwo.get(i).getId());
//                    dto.setStandardMatchId(caseTwo.get(i).getStandardMatchId());
//                    dto.setEventCode(caseTwo.get(i).getEventCode());
//                    dto.setSettleNum(caseTwo.get(i).getSettleNum());
//                    dto.setT1(homeRgEt);
//                    dto.setT2(awayRgEt);
//                    dto.setSort(5);
//                    popupSettleScores.add(dto);
//                }
//                if (caseTwo.get(i).getSettleNum().equals("bk_2htet")&&caseTwo.get(i).getStatus()!=3){
//                    BasketBallPopupSettleScoreDto dto = new BasketBallPopupSettleScoreDto();
//                    dto.setMatchScoreId(caseTwo.get(i).getId());
//                    dto.setStandardMatchId(caseTwo.get(i).getStandardMatchId());
//                    dto.setEventCode(caseTwo.get(i).getEventCode());
//                    dto.setSettleNum(caseTwo.get(i).getSettleNum());
//                    dto.setT1(home2ht);
//                    dto.setT2(away2ht);
//                    dto.setSort(3);
//                    popupSettleScores.add(dto);
//                }
//            }
//        }
//        //case 3:确认的比分是 下半场阶段OT bk_et,则需要带出 全场比分(含加时):bk_ft_et比分:Q1+Q2+Q3+Q4+OT
//        if (matchSettleScoreDto.getSettleNum().equals("bk_et")){
//            needSettleNum.add("bk_ft_et");
//            needSettleNum.add("bk_2htet");
//            int homeFtEt =matchSettleScoreDto.getT1();
//            int awayFtEt =matchSettleScoreDto.getT2();
//            int homeHtEt =matchSettleScoreDto.getT1();
//            int awayHtEt =matchSettleScoreDto.getT2();
//            for (int i =0;i<settleScores.size();i++){
//                if (settleScores.get(i).getSettleNum().equals("bk_ft_rg")){
//                    homeFtEt = homeFtEt+settleScores.get(i).getT1();
//                    awayFtEt = awayFtEt+settleScores.get(i).getT2();
//                }
//                if (settleScores.get(i).getSettleNum().equals("bk_2ht")){
//                    homeHtEt = homeHtEt+settleScores.get(i).getT1();
//                    awayHtEt = awayHtEt+settleScores.get(i).getT2();
//
//                }
//            }
//            if (matchSettleScoreDto.getMatchLength()==73){ //3*3没有下半场含加时
//                needSettleNum.remove("bk_2htet");
//            }
//            MatchSettleScoreExample example1= new MatchSettleScoreExample();
//            example1.createCriteria().andSettleNumIn(needSettleNum).andStandardMatchIdEqualTo(matchSettleScoreDto.getStandardMatchId());
//            List<MatchSettleScore> caseOne = matchSettleScoreMapper.selectByExample(example1);
//            if (!CollectionUtils.isEmpty(caseOne)){
//                for (int i =0;i<caseOne.size();i++){
//                    if (caseOne.get(i).getStatus()!=3&&caseOne.get(i).getSettleNum().equals("bk_ft_et")){
//                        BasketBallPopupSettleScoreDto dto = new BasketBallPopupSettleScoreDto();
//                        dto.setMatchScoreId(caseOne.get(i).getId());
//                        dto.setStandardMatchId(caseOne.get(i).getStandardMatchId());
//                        dto.setEventCode(caseOne.get(i).getEventCode());
//                        dto.setSettleNum(caseOne.get(i).getSettleNum());
//                        dto.setT1(homeFtEt);
//                        dto.setT2(awayFtEt);
//                        dto.setSort(5);
//                        popupSettleScores.add(dto);
//                    }
//                    if (caseOne.get(i).getStatus()!=3&&caseOne.get(i).getSettleNum().equals("bk_2htet")){
//                        BasketBallPopupSettleScoreDto dto = new BasketBallPopupSettleScoreDto();
//                        dto.setMatchScoreId(caseOne.get(i).getId());
//                        dto.setStandardMatchId(caseOne.get(i).getStandardMatchId());
//                        dto.setEventCode(caseOne.get(i).getEventCode());
//                        dto.setSettleNum(caseOne.get(i).getSettleNum());
//                        dto.setT1(homeHtEt);
//                        dto.setT2(awayHtEt);
//                        dto.setSort(3);
//                        popupSettleScores.add(dto);
//                    }
//
//                }
//            }
//
//        }
//        //假如不是高级权限并且非第一审核员,则需要校验他前面的审核员是否有带入比分
//        if (null!=matchSettleScoreDto.getRoleCode()&&matchSettleScoreDto.getRoleCode()==2&&!CollectionUtils.isEmpty(popupSettleScores)){
//            MatchSettleInfo info = matchSettleInfoRepository.getMatchSettleInfo(matchSettleScoreDto.getStandardMatchId());
//            if(StringUtils.isNotEmpty(info.getAuditorJson())){
//                ArrayList<String> list = JSON.parseObject(info.getAuditorJson(), ArrayList.class);
//                if (!CollectionUtils.isEmpty(list)){
//                    int index = list.indexOf(matchSettleScoreDto.getOperatorName());
//                    //不是第一审核员
//                    if (index>0){
//                        List<String> users = list.subList(0,list.indexOf(matchSettleScoreDto.getOperatorName()));
//                        List<String> settleNums = popupSettleScores.stream().map(BasketBallPopupSettleScoreDto::getSettleNum).collect(Collectors.toList());
//                        boolean tag = true;
//                        for (int i =0;i<users.size();i++){
//                            //需要带入的阶段是否已经被前面的审核员全部带入过
//                            int num = matchSettleCheckInfoMapper.countBySettleNumAndUser(users.get(i),2,settleNums,matchSettleScoreDto.getStandardMatchId());
//                            if (num==settleNums.size()){
//                                tag = false;
//                                break;
//                            }
//                        }
//                        //假如前面审核员带入的比分条数不等于需要带入的比分条数,则说明前面审核员没有带入完整,此时后续核员不能带入比分
//                        if (tag){
//                            return Response.failed("该审核员之前的审核员没有带入比分");
//                        }
//                    }
//                }
//            }
//        }
//
//        //假如此阶段需要弹出比分,则将当前登录用户存入,用来展示弹框
//        if (!popupSettleScores.isEmpty()){
//            MatchSettleScore matchSettleScore = matchSettleScoreMapper.selectByPrimaryKey(matchSettleScoreDto.getMatchScoreId());
//            //由于两个审核员操作确认的时候,后台会先结算比分 此时就不应该有弹出标记
//            if (matchSettleScore.getStatus()!=3){
//                List<PopupUserDto> popUsers = new ArrayList<>();
//                if (null!=matchSettleScore.getPopupUsers()){
//                    JSONArray array = JSONArray.parseArray(matchSettleScore.getPopupUsers());
//                    if (null!=array){
//                        for (Object o : array) {
//                            PopupUserDto dto = JSONObject.toJavaObject((JSONObject)o,PopupUserDto.class);
//                            popUsers.add(dto);
//                        }
//                    }
//                }
//                boolean tag = true;
//                for (PopupUserDto p:popUsers){
//                    if (null!=p.getPopupUser()&&p.getPopupUser().equals(matchSettleScoreDto.getOperatorName())){
//                        tag =false;
//                    }
//                }
//                if (tag){
//                    PopupUserDto popupUser = new PopupUserDto();
//                    popupUser.setPopupUser(matchSettleScoreDto.getOperatorName());
//                    popUsers.add(popupUser);
//                    matchSettleScore.setPopupUsers(JSONArray.toJSONString(popUsers));
//                    matchSettleScoreMapper.updateByPrimaryKey(matchSettleScore);
//                }
//            }
//
//
//        }
//        return Response.success(popupSettleScores);
//    }
//
//    @Override
//    public Response confirmBringInScore(BasketBallPutInJsonDto basketBallPutInJsonDto) {
//        String key ="StandardMatchScoreConsumer:"+basketBallPutInJsonDto.getStandardMatchId();
//
//        if (redisService.tryLock(key, key, 2, 5)) {
//            log.info("超级管理员confirmBringInScore入参: {}",basketBallPutInJsonDto);
//            Long standardMatchId = basketBallPutInJsonDto.getStandardMatchId();
//            List<UpdateBasketBallSettleScoreDto> updateBasketBallSettleScores =new ArrayList<>();
//            //解析需要带入的比分
//            try {
//                JSONArray array = JSONArray.parseArray(basketBallPutInJsonDto.getPutInJson());
//                for (Object o : array) {
//                    UpdateBasketBallSettleScoreDto updateBasketBallSettleScoreDto = JSONObject.toJavaObject((JSONObject)o,UpdateBasketBallSettleScoreDto.class);
//                    updateBasketBallSettleScores.add(updateBasketBallSettleScoreDto);
//                }
//            }catch (Exception e){
//                log.error("confirmBringInScore解析参数异常:",e);
//                return Response.failed("解析参数异常");
//            }
//            log.info("解析后updateBasketBallSettleScores: {}",updateBasketBallSettleScores);
//            //判断带入的比分是否已经结算,假如已经结算则不带入
//            List<String> settleNums = updateBasketBallSettleScores.stream().map(UpdateBasketBallSettleScoreDto::getSettleNum).collect(Collectors.toList());
//            MatchSettleScoreExample example = new MatchSettleScoreExample();
//            example.createCriteria().andSettleNumIn(settleNums).andStandardMatchIdEqualTo(standardMatchId).andStatusNotEqualTo(3);
//            List<MatchSettleScore> matchSettleScores = matchSettleScoreMapper.selectByExample(example);
//            if (!CollectionUtils.isEmpty(matchSettleScores)){
//                matchSettleScores.forEach(matchSettleScore -> {
//                    updateBasketBallSettleScores.forEach(updateBasketBallSettleScore ->{
//                        if (matchSettleScore.getSettleNum().equals(updateBasketBallSettleScore.getSettleNum())){
//                            matchSettleScore.setStatus(CONFIRM);
//                            matchSettleScore.setT1(updateBasketBallSettleScore.getT1());
//                            matchSettleScore.setT2(updateBasketBallSettleScore.getT2());
//                            matchSettleScore.setModifyTime(System.currentTimeMillis());
//                            matchSettleScoreMapper.updateByPrimaryKey(matchSettleScore);
//                            //2.记录日志
//                            //走水  将编码设置为8
//                            if (matchSettleScore.getGoWaterStatus()!=null&&matchSettleScore.getGoWaterStatus().equals(1)){
//                                matchSettleScore.setExtryInfo("8");
//                            }
//                            iMatchSettleLogService.matchSettleScoreAddLog(matchSettleScore,updateBasketBallSettleScore.getOperatorName(),
//                                    OperateLogTypeEnum.CONFIRM_SCORE,"",updateBasketBallSettleScore.getIpAddress());
//                            log.info("updateBasketBallSettleScore_StandardMatchId: {},EventCode: {}",updateBasketBallSettleScore.getStandardMatchId(),updateBasketBallSettleScore.getEventCode());
//                            wsPushService.pushBasketballStandardSettleScores(updateBasketBallSettleScore.getStandardMatchId(),
//                                    updateBasketBallSettleScore.getEventCode());
//
//                        }
//
//                    });
//                });
//            }
//    //        //全部結束后去掉同步按鈕
//    //        MatchSettleScore settleScore = matchSettleScoreMapper.selectByPrimaryKey(basketBallPutInJsonDto.getMatchScoreId());
//    //        List<PopupUserDto> popUsers = new ArrayList<>();
//    //        JSONArray array = JSONArray.parseArray(settleScore.getPopupUsers());
//    //        if (null!=array){
//    //            for (Object o : array) {
//    //                PopupUserDto dto = JSONObject.toJavaObject((JSONObject)o,PopupUserDto.class);
//    //                if (!dto.getPopupUser().equals(basketBallPutInJsonDto.getOperatorName())){
//    //                    popUsers.add(dto);
//    //                }
//    //
//    //            }
//    //        }
//    //        log.info("removePopUsers: {}",basketBallPutInJsonDto.getOperatorName());
//    //        settleScore.setPopupUsers(JSONArray.toJSONString(popUsers));
//    //        matchSettleScoreMapper.updateByPrimaryKey(settleScore);
//            return Response.success();
//        } else {
//            log.error("confirmBringInScore standardMatchId::{}::比分无法获取redis锁",basketBallPutInJsonDto.getStandardMatchId());
//        }
//        return Response.failed("比分带入时，比分无法获取redis锁,请重试!");
//    }
//
//    @Override
//    public  void verifyScoresIsSame(MatchSettleScore matchSettleScore){
//        //查出這場所有已結算比分
//        MatchSettleScoreExample example= new MatchSettleScoreExample();
//        example.createCriteria().andStandardMatchIdEqualTo(matchSettleScore.getStandardMatchId()).andStatusEqualTo(3);
//        List<MatchSettleScore> matchSettleScores =  matchSettleScoreMapper.selectByExample(example);
//
//        MatchSettleScore scoreQ104 = null; //第一節
//        MatchSettleScore scoreQ204 = null;//第二節
//        MatchSettleScore score1Ht = null; //上半場
//        MatchSettleScore scoreQ304 = null;//第三節
//        MatchSettleScore scoreQ404 = null;//第四節
//        MatchSettleScore score2Ht = null; //下半場
//        MatchSettleScore scoreEt = null; //加時賽
//        MatchSettleScore scoreHtEt = null; //下半場含加時賽
//        MatchSettleScore scoreFtRg = null; //全場常規不含加時賽
//        MatchSettleScore scoreFtEt = null; //全場比分含加時賽
//        if (!CollectionUtils.isEmpty(matchSettleScores)){
//            for (MatchSettleScore score :matchSettleScores){
//                if (score.getSettleNum().equals("bk_q104")){
//                    scoreQ104=score;
//                }
//                if (score.getSettleNum().equals("bk_q204")){
//                    scoreQ204=score;
//                }
//                if (score.getSettleNum().equals("bk_1ht")){
//                    score1Ht=score;
//                }
//                if (score.getSettleNum().equals("bk_q304")){
//                    scoreQ304=score;
//                }
//                if (score.getSettleNum().equals("bk_q404")){
//                    scoreQ404=score;
//                }
//                if (score.getSettleNum().equals("bk_2ht")){
//                    score2Ht=score;
//                }if (score.getSettleNum().equals("bk_et")){
//                    scoreEt=score;
//                }
//                if (score.getSettleNum().equals("bk_2htet")){
//                    scoreHtEt=score;
//                }
//                if (score.getSettleNum().equals("bk_ft_rg")){
//                    scoreFtRg=score;
//                }
//                if (score.getSettleNum().equals("bk_ft_et")){
//                    scoreFtEt=score;
//                }
//
//            }
//        }
//
//        if (null!=score1Ht){
//            int home1Ht = 0;
//            int away1Ht = 0;
//            if (null==scoreQ104||null==scoreQ204){ //第一節,第二節其中有沒有結算
//                score1Ht.setScoreCheckTag(0);
//            }else {
//                home1Ht = scoreQ104.getT1()+scoreQ204.getT1();
//                away1Ht = scoreQ104.getT2()+scoreQ204.getT2();
//                if (home1Ht!=score1Ht.getT1()||away1Ht!=score1Ht.getT2()){
//                    score1Ht.setScoreCheckTag(1);
//                }else {
//                    score1Ht.setScoreCheckTag(0);
//                }
//            }
//            matchSettleScoreMapper.updateByPrimaryKey(score1Ht);
//        }
//
//        if (null!=score2Ht){
//            int home2Ht = 0;
//            int away2Ht = 0;
//            if (null==scoreQ304||null==scoreQ404){ //第3節,第4節其中有沒有結算
//                score2Ht.setScoreCheckTag(0);
//            }else {
//                home2Ht = scoreQ304.getT1()+scoreQ404.getT1();
//                away2Ht = scoreQ304.getT2()+scoreQ404.getT2();
//                if (home2Ht!=score2Ht.getT1()||away2Ht!=score2Ht.getT2()){
//                    score2Ht.setScoreCheckTag(1);
//                }else {
//                    score2Ht.setScoreCheckTag(0);
//                }
//            }
//            matchSettleScoreMapper.updateByPrimaryKey(score2Ht);
//        }
//
//        if (null!=scoreHtEt){
//            int homeHtEt = 0;
//            int awayHtEt = 0;
//            if (null==score2Ht){ //第3節,第4節,加時賽其中有沒有結算
//                scoreHtEt.setScoreCheckTag(0);
//            }else {
//                if (null==scoreEt){
//                    homeHtEt = score2Ht.getT1();
//                    awayHtEt =score2Ht.getT2();
//                }else {
//                    homeHtEt = score2Ht.getT1()+scoreEt.getT1();
//                    awayHtEt =score2Ht.getT2()+scoreEt.getT2();
//                }
//
//                if (homeHtEt!=scoreHtEt.getT1()||awayHtEt!=scoreHtEt.getT2()){
//                    scoreHtEt.setScoreCheckTag(1);
//                }else {
//                    scoreHtEt.setScoreCheckTag(0);
//                }
//            }
//            matchSettleScoreMapper.updateByPrimaryKey(scoreHtEt);
//        }
//
//        if (null!=scoreFtRg){
//            int homeFtRg = 0;
//            int awayFtRg = 0;
//            if (null==score1Ht||null==score2Ht){ //第一節,第二節,第3節,第4節其中有沒有結算
//                scoreFtRg.setScoreCheckTag(0);
//            }else {
//                homeFtRg = score1Ht.getT1()+score2Ht.getT1();
//                awayFtRg = score1Ht.getT2()+score2Ht.getT2();
//                if (homeFtRg!=scoreFtRg.getT1()||awayFtRg!=scoreFtRg.getT2()){
//                    scoreFtRg.setScoreCheckTag(1);
//                }else {
//                    scoreFtRg.setScoreCheckTag(0);
//                }
//            }
//            matchSettleScoreMapper.updateByPrimaryKey(scoreFtRg);
//        }
//
//        if (null!=scoreFtEt){
//            int homeFtEt = 0;
//            int awayFtEt = 0;
//            if (null==score1Ht||null==score2Ht||null==scoreEt){ //第一節,第二節,第3節,第4節,加時賽其中有沒有結算
//                scoreFtEt.setScoreCheckTag(0);
//            }else {
//                homeFtEt = score1Ht.getT1()+score2Ht.getT1()+scoreEt.getT1();
//                awayFtEt = score1Ht.getT2()+score2Ht.getT2()+scoreEt.getT2();
//                if (homeFtEt!=scoreFtEt.getT1()||awayFtEt!=scoreFtEt.getT2()){
//                    scoreFtEt.setScoreCheckTag(1);
//                }else {
//                    scoreFtEt.setScoreCheckTag(0);
//                }
//            }
//            matchSettleScoreMapper.updateByPrimaryKey(scoreFtEt);
//        }
//
//    }
//
//    @Override
//    public void verifyScoresIsSame(Long standardMatchId){
//        //查出這場所有已結算比分
//        MatchSettleScoreExample example= new MatchSettleScoreExample();
//        example.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andStatusEqualTo(3);
//        List<MatchSettleScore> matchSettleScores =  matchSettleScoreMapper.selectByExample(example);
//
//        MatchSettleScore scoreQ104 = null; //第一節
//        MatchSettleScore scoreQ204 = null;//第二節
//        MatchSettleScore score1Ht = null; //上半場
//        MatchSettleScore scoreQ304 = null;//第三節
//        MatchSettleScore scoreQ404 = null;//第四節
//        MatchSettleScore score2Ht = null; //下半場
//        MatchSettleScore scoreEt = null; //加時賽
//        MatchSettleScore scoreHtEt = null; //下半場含加時賽
//        MatchSettleScore scoreFtRg = null; //全場常規不含加時賽
//        MatchSettleScore scoreFtEt = null; //全場比分含加時賽
//        List<MatchSettleScore> batchUpdate = new ArrayList<>();
//        if (!CollectionUtils.isEmpty(matchSettleScores)){
//            for (MatchSettleScore score :matchSettleScores){
//                if (score.getSettleNum().equals("bk_q104")){
//                    scoreQ104=score;
//                }
//                if (score.getSettleNum().equals("bk_q204")){
//                    scoreQ204=score;
//                }
//                if (score.getSettleNum().equals("bk_1ht")){
//                    score1Ht=score;
//                }
//                if (score.getSettleNum().equals("bk_q304")){
//                    scoreQ304=score;
//                }
//                if (score.getSettleNum().equals("bk_q404")){
//                    scoreQ404=score;
//                }
//                if (score.getSettleNum().equals("bk_2ht")){
//                    score2Ht=score;
//                }if (score.getSettleNum().equals("bk_et")){
//                    scoreEt=score;
//                }
//                if (score.getSettleNum().equals("bk_2htet")){
//                    scoreHtEt=score;
//                }
//                if (score.getSettleNum().equals("bk_ft_rg")){
//                    scoreFtRg=score;
//                }
//                if (score.getSettleNum().equals("bk_ft_et")){
//                    scoreFtEt=score;
//                }
//
//            }
//        }
//
//        if (null!=score1Ht){
//            int home1Ht = 0;
//            int away1Ht = 0;
//            if (null==scoreQ104||null==scoreQ204){ //第一節,第二節其中有沒有結算
//                score1Ht.setScoreCheckTag(0);
//            }else {
//                home1Ht = scoreQ104.getT1()+scoreQ204.getT1();
//                away1Ht = scoreQ104.getT2()+scoreQ204.getT2();
//                if (home1Ht!=score1Ht.getT1()||away1Ht!=score1Ht.getT2()){
//                    score1Ht.setScoreCheckTag(1);
//                }else {
//                    score1Ht.setScoreCheckTag(0);
//                }
//            }
//            batchUpdate.add(score1Ht);
//        }
//
//        if (null!=score2Ht){
//            int home2Ht = 0;
//            int away2Ht = 0;
//            if (null==scoreQ304||null==scoreQ404){ //第3節,第4節其中有沒有結算
//                score2Ht.setScoreCheckTag(0);
//            }else {
//                home2Ht = scoreQ304.getT1()+scoreQ404.getT1();
//                away2Ht = scoreQ304.getT2()+scoreQ404.getT2();
//                if (home2Ht!=score2Ht.getT1()||away2Ht!=score2Ht.getT2()){
//                    score2Ht.setScoreCheckTag(1);
//                }else {
//                    score2Ht.setScoreCheckTag(0);
//                }
//            }
//            batchUpdate.add(score2Ht);
//        }
//
//        if (null!=scoreHtEt){
//            int homeHtEt = 0;
//            int awayHtEt = 0;
//            if (null==score2Ht){ //第3節,第4節,加時賽其中有沒有結算
//                scoreHtEt.setScoreCheckTag(0);
//            }else {
//                if (null==scoreEt){
//                    homeHtEt = score2Ht.getT1();
//                    awayHtEt =score2Ht.getT2();
//                }else {
//                    homeHtEt = score2Ht.getT1()+scoreEt.getT1();
//                    awayHtEt =score2Ht.getT2()+scoreEt.getT2();
//                }
//
//                if (homeHtEt!=scoreHtEt.getT1()||awayHtEt!=scoreHtEt.getT2()){
//                    scoreHtEt.setScoreCheckTag(1);
//                }else {
//                    scoreHtEt.setScoreCheckTag(0);
//                }
//            }
//            batchUpdate.add(scoreHtEt);
//        }
//
//        if (null!=scoreFtRg){
//            int homeFtRg = 0;
//            int awayFtRg = 0;
//            if (null==score1Ht||null==score2Ht){ //第一節,第二節,第3節,第4節其中有沒有結算
//                scoreFtRg.setScoreCheckTag(0);
//            }else {
//                homeFtRg = score1Ht.getT1()+score2Ht.getT1();
//                awayFtRg = score1Ht.getT2()+score2Ht.getT2();
//                if (homeFtRg!=scoreFtRg.getT1()||awayFtRg!=scoreFtRg.getT2()){
//                    scoreFtRg.setScoreCheckTag(1);
//                }else {
//                    scoreFtRg.setScoreCheckTag(0);
//                }
//            }
//            batchUpdate.add(scoreFtRg);
//        }
//
//        if (null!=scoreFtEt){
//            int homeFtEt = 0;
//            int awayFtEt = 0;
//            if (null==score1Ht||null==score2Ht||null==scoreEt){ //第一節,第二節,第3節,第4節,加時賽其中有沒有結算
//                scoreFtEt.setScoreCheckTag(0);
//            }else {
//                homeFtEt = score1Ht.getT1()+score2Ht.getT1()+scoreEt.getT1();
//                awayFtEt = score1Ht.getT2()+score2Ht.getT2()+scoreEt.getT2();
//                if (homeFtEt!=scoreFtEt.getT1()||awayFtEt!=scoreFtEt.getT2()){
//                    scoreFtEt.setScoreCheckTag(1);
//                }else {
//                    scoreFtEt.setScoreCheckTag(0);
//                }
//            }
//            batchUpdate.add(scoreFtEt);
//        }
//        matchSettleScoreService.saveOrUpdateBatch(batchUpdate);
//    }
//
//    @Override
//    public Response editShowScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto) {
//        log.info("basketball editShowScore,matchSettleScoreDto: {}",matchSettleScoreDto);
//        List<LimitSwitchDto> l = basketballInSettleService.getBasketInSettleTimeLimit(2l);
//        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchSettleScoreDto.getStandardMatchId());
//        StandardSportTournament standardSportTournament = standardSportTournamentService.getItem(standardMatchInfo.getStandardTournamentId());
//        int level = standardSportTournament.getTournamentLevel();
//        boolean tag = false;
//        for (int i =0;i<l.size();i++){
//            if (level==l.get(i).getLevel()){
//                tag = l.get(i).getOnOff();
//            }
//        }
//        if (!tag){
//            return Response.success();
//        }
//        List<Long> standardMatchIds = new ArrayList<>();
//        standardMatchIds.add(matchSettleScoreDto.getStandardMatchId());
//        List<StandardSportMarketSell> sells = standardSportMarketSellService.getItems(standardMatchIds);
//        if (sells.size() == 0) {
//            return Response.failed();
//        }
//        String businessEvent = sells.get(0).getBusinessEvent();
//        BasketBallPopupSettleScoreDto dto = new BasketBallPopupSettleScoreDto();
//
//        MatchSettleThirdScoreExample matchSettleThirdScoreExample = new MatchSettleThirdScoreExample();
//        matchSettleThirdScoreExample.createCriteria().andStandardMatchIdEqualTo(matchSettleScoreDto.getStandardMatchId()).andDataSourceCodeEqualTo(businessEvent).andSettleNumEqualTo(matchSettleScoreDto.getSettleNum());
//        List<MatchSettleThirdScore> thirdScores = matchSettleThirdScoreMapper.selectByExample(matchSettleThirdScoreExample);
//        if (!CollectionUtils.isEmpty(thirdScores)){
//            MatchSettleThirdScore thirdScore = thirdScores.get(0);
//            if (null!=thirdScore.getT1()&&null!=thirdScore.getT2()){
//                dto.setT1(thirdScore.getT1());
//                dto.setT2(thirdScore.getT2());
//                dto.setSettleNum(thirdScore.getSettleNum());
//                return Response.success(dto);
//            }
//        }
//
//        return Response.success();
//
//    }
//
//    @Override
//    public Response cancelDeleteStatus(MatchSettleSwitcherDto matchSettleSwitcherDto) {
//        log.info("basketball cancelDeleteStatus,matchSettleSwitcherDto: {}",matchSettleSwitcherDto);
//        String key ="MATCH_SETTLE_INFO:"+ matchSettleSwitcherDto.getMatchId();
//        //1.查询是否已经存在人工核对事件
//        try {
//            //3.更新或者新增
//            if(redisService.tryLock(key,key,2,5)) {
//                if(matchSettleSwitcherDto.getMatchScoreId()!=null && matchSettleSwitcherDto.getMatchScoreId()>0L){
//                    MatchSettleScore settleScore = matchSettleScoreMapper.selectByPrimaryKey(matchSettleSwitcherDto.getMatchScoreId());
//                    if(settleScore!=null){
//                        settleScore.setHasDeleteEvent(0);
//                        settleScore.setCurrentEventStatus(settleScore.getIsGrey());
//                        matchSettleScoreMapper.updateByPrimaryKey(settleScore);
//                        iMatchSettleLogService.deleteSettleAlertLog(settleScore,matchSettleSwitcherDto);
//                    }
//                }
//                MatchSettleScoreExample example = new MatchSettleScoreExample();
//                example.createCriteria().andStandardMatchIdEqualTo(matchSettleSwitcherDto.getMatchId());
//                List<MatchSettleScore> settleScores = matchSettleScoreMapper.selectByExample(example);
//                int deleteGoal=0;
//                int grayGoal=0;
//                for (MatchSettleScore matchSettleScore : settleScores) {
//                    if(matchSettleScore.getIsGrey()!=null&&matchSettleScore.getIsGrey()==1){
//                        grayGoal=1;
//                    }
//                    if(matchSettleScore.getHasDeleteEvent()!=null&&matchSettleScore.getHasDeleteEvent()==1){
//                        deleteGoal=1;
//                    }
//                }
//                MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(matchSettleSwitcherDto.getMatchId());
//                if(deleteGoal==1 && grayGoal==1){
//                    matchSettleInfo.setIsGray(1);
//                    matchSettleInfo.setHasDeleteEvent(1);
//                    matchSettleInfo.setCurrentEventStatus(1);
//                }else if(deleteGoal==1 && grayGoal==0){
//                    matchSettleInfo.setIsGray(0);
//                    matchSettleInfo.setHasDeleteEvent(1);
//                    matchSettleInfo.setCurrentEventStatus(2);
//                }else if(deleteGoal==0 && grayGoal==1){
//                    matchSettleInfo.setIsGray(1);
//                    matchSettleInfo.setHasDeleteEvent(0);
//                    matchSettleInfo.setCurrentEventStatus(1);
//                }else{
//                    matchSettleInfo.setIsGray(0);
//                    matchSettleInfo.setHasDeleteEvent(0);
//                    matchSettleInfo.setCurrentEventStatus(0);
//                }
//                matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
//                return Response.success();
//            }else {
//                return Response.failed("1031933");
//            }
//        }catch (Exception e){
//            log.error("BasketballMatchScoresSettleApiImpl-cancelDeleteStatus:",e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//
//    @Override
//    public MatchSettleInfo searchMatchDelStatus(MatchSettleScoreSearchDto settleScoreSearchDto) {
//        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(settleScoreSearchDto.getStandardMatchId());
//        if (null!=matchSettleInfo){
//            return matchSettleInfo;
//        }
//        return null;
//    }
//
//
//}
