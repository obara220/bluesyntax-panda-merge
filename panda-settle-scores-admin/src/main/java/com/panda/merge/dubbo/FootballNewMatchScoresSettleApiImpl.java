//package com.panda.merge.dubbo;
//
//import com.alibaba.fastjson.JSONArray;
//import com.panda.merge.api.IFootballMatchScoresSettleApi;
//import com.panda.merge.api.IFootballNewMatchScoresSettleApi;
//import com.panda.merge.check.IMatchSettleCheckService;
//import com.panda.merge.common.enums.DataSourceCodeEnum;
//import com.panda.merge.common.enums.MatchPeriodEnum;
//import com.panda.merge.common.enums.OperateLogTypeEnum;
//import com.panda.merge.common.utils.IdWorker;
//import com.panda.merge.config.RedisService;
//import com.panda.merge.constant.*;
//import com.panda.merge.constant.converter.SettleMentionConverter;
//import com.panda.merge.dto.PlayerDeleteStatus;
//import com.panda.merge.dto.Response;
//import com.panda.merge.dto.advertise.MatchSettleSwitcherDto;
//import com.panda.merge.dto.settle.*;
//import com.panda.merge.mapper.MatchSettleCheckInfoMapper;
//import com.panda.merge.mapper.MatchSettleEventMapper;
//import com.panda.merge.mapper.MatchSettleInfoMapper;
//import com.panda.merge.mapper.MatchSettleScoreMapper;
//import com.panda.merge.model.*;
//import com.panda.merge.mq.producer.MatchSettleScoresProducer;
//import com.panda.merge.service.IMatchSettleLogService;
//import com.panda.merge.service.IMatchSettleService;
//import com.panda.merge.service.IWsPushService;
//import com.panda.merge.service.settleMention.dto.AbstractMentionStatus;
//import com.panda.merge.service.settleMention.dto.BasketballMentionStatus;
//import com.panda.merge.service.settleMention.dto.FootballMentionStatus;
//import com.panda.merge.service.syncScore.SyncScoreFactory;
//import com.panda.merge.utils.MatchEventInfoSettleUtils;
//import com.panda.merge.utils.SettleCheckUtils;
//import io.netty.util.internal.StringUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.collections.MapUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.tuple.Pair;
//import org.apache.dubbo.config.annotation.DubboService;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static com.panda.merge.constant.MatchSettleScoreConstant.MatchSettleScoreStatus.*;
//
//
//@Service
//@DubboService
//@Slf4j
//public class FootballNewMatchScoresSettleApiImpl implements IFootballNewMatchScoresSettleApi {
//
//    @Autowired
//    MatchSettleCheckInfoMapper matchSettleCheckInfoMapper;
//
//    @Autowired
//    IMatchSettleService matchSettleService;
//    @Autowired
//    IWsPushService wsPushService;
//
//    @Autowired
//    RedisService redisService;
//
//    @Autowired
//    IMatchSettleCheckService matchSettleCheckService;
//    @Autowired
//    MatchSettleEventMapper matchSettleEventMapper;
//
//    @Autowired
//    IFootballMatchScoresSettleApi footballMatchScoresSettleApi;
//
//    @Autowired
//    MatchSettleScoreMapper matchSettleScoreMapper;
//    @Autowired
//    MatchSettleInfoMapper matchSettleInfoMapper;
//    @Autowired
//    IMatchSettleLogService iMatchSettleLogService;
//    @Autowired
//    MatchSettleScoresProducer matchSettleScoresProducer;
//
//    @Autowired
//    SettleMentionConverter settleMentionConverter;
//    @Autowired
//    SyncScoreFactory syncScoreFactory;
//
//    @Override
//    public Response editMatchSettleScore(UpdateMatchSettleScoreDto matchSettleScoreDto) {
//        log.info("[FootballNewMatchScoresSettleApiImpl] editMatchSettleScore with linkId:{} and param:{} start!",matchSettleScoreDto.getLinkedId(), matchSettleScoreDto);
//        //赛事id
//        Long standardMatchId = matchSettleScoreDto.getStandardMatchId();
//        //赛事比分id
//        Long matchScoreId = matchSettleScoreDto.getMatchScoreId();
//        //审核员姓名
//        String userName = matchSettleScoreDto.getOperatorName();
//
//        //1.判断是否已超过结算时间
//        if(matchSettleService.checkIfOverSettleTime(standardMatchId)){
//            return Response.failed("1031930");
//        }
//        if(redisService.hasKey("SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getMatchScoreId())){
//            return Response.failed("1031960");
//        }
//        String key =CommonConstant.MATCH_PHASE_SCORE_SETTLE+ matchSettleScoreDto.getStandardMatchId();
//        try{
//            if(redisService.tryLock(key,key,2,5)) {
//                MatchSettleCheckInfo matchSettleCheckInfo=  matchSettleCheckService.searchCheckInfoByUser(matchSettleScoreDto.getMatchScoreId(),matchSettleScoreDto.getStandardMatchId(),
//                        matchSettleScoreDto.getOperatorName());
//
//                if(matchSettleCheckInfo!=null && matchSettleCheckInfo.getCheckStatus()== MatchSettleCheckConstant.CheckStatus.CONFIRM){
//                    //还没编辑 该用户已经确认比分
//                    return Response.failed("1031933");
//                }
//
//                MatchSettleScore matchSettleScore =matchSettleScoreMapper.selectByPrimaryKey(matchSettleScoreDto.getMatchScoreId());
//                Integer checkNumber = matchSettleScore.getCheckNumber();
//                if(matchSettleScore==null){
//                    return Response.failed("1031935");
//                }
//                /*if(!matchSettleCheckService.isFiveMinPeriodScoresBeforeSettled(matchSettleScore)){
//                    // 五分钟玩法请确保上一个比分已结算。
//                    return Response.failed("1031946");
//                }*/
//                //阶段比分结算顺序拦截，如果之前的比分没有结算，则不能编辑当前的比分
//                if(!matchSettleCheckService.isAllPeriodScoresBeforeSettled(matchSettleScore)&&
//                        !matchSettleScoreDto.getSettleNum().equals(MatchPeriodEnum.GOAL_5.getCode().toString()) &&
//                        !matchSettleScoreDto.getSettleNum().equals(MatchPeriodEnum.GOAL_9.getCode().toString())) {
//                    // 请确保上一个比分已结算。
//                    return Response.failed("1031946");
//                }
//
//                matchSettleScore.setExtryInfo(matchSettleScoreDto.getExtryInfo());
//                matchSettleScore.setOperater(matchSettleScoreDto.getOperatorName());
//                matchSettleScore.setT1(matchSettleScoreDto.getT1());
//                matchSettleScore.setT2(matchSettleScoreDto.getT2());
//                matchSettleScore.setGoWaterStatus(matchSettleScoreDto.getGoWaterStatus());
//                MatchSettleCheckInfo checkInfo =new  MatchSettleCheckInfo();
//                if(!matchSettleCheckService.checkSettleScoreAndAutoSettleNonEvent(matchSettleScore,null)){
//                    return Response.failed("1031946");
//                }
//                //3.1无比分核对记录则初始化该用户的核对记录
//                if(matchSettleCheckInfo==null){
//                    //得到当前用户的次序
//                    matchSettleCheckInfo=new MatchSettleCheckInfo();
//                    BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
//                    matchSettleCheckInfo =  SettleCheckUtils.initManualMatchSettleScores(matchSettleScore);
//                    SettleCheckUtils.copyManualMatchSettleScore(matchSettleScoreDto,matchSettleCheckInfo);
//                    matchSettleCheckInfo.setCheckNumber( SettleCheckUtils.getCheckNumber(matchSettleScore.getCheckNumber()));
//                    matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
//                    matchSettleCheckInfo.setDataSourceCode("PA");
//                    matchSettleCheckInfoMapper.insert(matchSettleCheckInfo);
//                    log.info("[FootballNewMatchScoresSettleApiImpl] editMatchSettleScore linkId::{}:: 插入比分id为{}，审核员{}比分核对数据:{}",matchSettleScoreDto.getLinkedId(),matchScoreId,userName,matchSettleScoreDto);
//                }else {
//                    //3.2有比分核对记录则更新
//                    BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
//                    SettleCheckUtils.copyManualMatchSettleScore(matchSettleScoreDto,matchSettleCheckInfo);
//                    matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
//                    matchSettleCheckInfoMapper.updateByPrimaryKey(matchSettleCheckInfo);
//                    log.info("[FootballNewMatchScoresSettleApiImpl] editMatchSettleScore linkId::{}:: 更新比分id为{}，审核员{}比分核对数据:{}",matchSettleScoreDto.getLinkedId(),matchScoreId,userName,matchSettleScoreDto);
//                }
//                //TODO 记录日志
//
//                iMatchSettleLogService.matchSettleCheckScoreAddLog(checkInfo,matchSettleCheckInfo,
//                        matchSettleScoreDto,OperateLogTypeEnum.EDIT,matchSettleScore.getSettleNum(), checkNumber);
//                log.info("[FootballNewMatchScoresSettleApiImpl] editMatchSettleScore with linkId:{} end!",matchSettleScoreDto.getLinkedId());
//            }else {
//                return Response.failed("1031933");
//            }
//        }catch (Exception e){
//            log.error("[FootballNewMatchScoresSettleApiImpl] editMatchSettleScore with linkId:{} error",matchSettleScoreDto.getLinkedId(), e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//        return Response.success();
//    }
//
//    @Override
//    public Response confirmMatchSettleScore(ConfirmMatchSettleScoreDto matchSettleScoreDto) {
//        log.info("[FootballNewMatchScoresSettleApiImpl] confirmMatchSettleScore with linkId:{} param: {}", matchSettleScoreDto.getLinkedId(), matchSettleScoreDto);
//        //赛事id
//        Long standardMatchId = matchSettleScoreDto.getStandardMatchId();
//        //赛事比分id
//        Long matchScoreId = matchSettleScoreDto.getMatchScoreId();
//        //审核员姓名
//        String userName = matchSettleScoreDto.getOperatorName();
//
//        //1.判断是否已超过结算时间X
//        if(matchSettleService.checkIfOverSettleTime(standardMatchId)){
//            return Response.failed("1031930");
//        }
//        if(redisService.hasKey("SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getMatchScoreId())){
//            return Response.failed("1031960");
//        }
//        String key =CommonConstant.MATCH_PHASE_SCORE_SETTLE+ matchSettleScoreDto.getStandardMatchId();
//        try{
//            if(redisService.tryLock(key,key,2,5)) {
//                //2.查询人工核对比分
//                MatchSettleCheckInfo matchSettleCheckInfo=  matchSettleCheckService.searchCheckInfoByUser(matchSettleScoreDto.getMatchScoreId(),
//                        matchSettleScoreDto.getStandardMatchId(),matchSettleScoreDto.getOperatorName());
//                MatchSettleCheckInfo checkInfo =new  MatchSettleCheckInfo();
//                BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
//
//                MatchSettleScore matchSettleScore =matchSettleScoreMapper.selectByPrimaryKey(matchSettleScoreDto.getMatchScoreId());
//                Integer checkNumber = matchSettleScore.getCheckNumber();
//                if(matchSettleScore==null){
//                    return Response.failed("1031935");
//                }
//                if(matchSettleCheckInfo==null){
//                    return Response.failed("1031934");
//                }
//                if(matchSettleCheckInfo.getCheckStatus()!= MatchSettleCheckConstant.CheckStatus.EDIT){
//                    log.error("人工核对确认比分状态错误:{}",matchSettleCheckInfo);
//                    return Response.failed("1031934");
//                }
//                //次序校验 如果次序不对返回次序不对
////                if(matchSettleScore.getCheckNumber()==null||matchSettleScore.getCheckNumber()!=matchSettleCheckInfo.getCheckNumber()){
////                    log.error("人工核对确认比分次序错误:{}",matchSettleScore);
////                    return Response.failed("1031934");
////                }
//
//                //TODO 记录日志
//                UpdateMatchSettleScoreDto dto =new UpdateMatchSettleScoreDto();
//                BeanUtils.copyProperties(matchSettleScoreDto,dto);
//                if (!StringUtil.isNullOrEmpty(matchSettleScore.getSettleNum())) {
//                dto.setSettleNum((matchSettleScore.getSettleNum()));
//                }
//                dto.setSportId(matchSettleScore.getSportId());
//                iMatchSettleLogService.matchSettleCheckScoreAddLog(checkInfo, matchSettleCheckInfo,
//                        dto, OperateLogTypeEnum.CONFIRM_SCORE,matchSettleScore.getSettleNum(), checkNumber) ;
//
//                //3.更新状态
//                matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.CONFIRM);
//                matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
//                matchSettleCheckInfoMapper.updateByPrimaryKey(matchSettleCheckInfo);
//                //进入统一核对比分流程
//                Pair<Boolean, Boolean> isScoreOrEventDiff= matchSettleCheckService.checkCommonMatchSettleScoreEvent(matchSettleScore,matchSettleCheckInfo,false);
//
//                matchSettleCheckService.updateMatchGrayStatus(matchSettleCheckInfo.getStandardMatchId());
//                matchSettleCheckService.updateMatchFifteenMinGraySettleFactor(matchSettleScore.getStandardMatchId(),matchSettleScore.getSettleNum());
//                if(isScoreOrEventDiff.getLeft()){
//                    //1031947=比分不一致需要下个审核员审核
//                    return Response.failed("1031947");
//                }
//                if(isScoreOrEventDiff.getRight()) {
//                    // 同步比分
//                    SettleMatchScoreDto scoreSearchDto = new SettleMatchScoreDto();
//                    BeanUtils.copyProperties(matchSettleScoreDto, scoreSearchDto);
//                    scoreSearchDto.setSettleNum(Integer.valueOf(matchSettleScoreDto.getSettleNum()));
//                    scoreSearchDto.setT1(matchSettleCheckInfo.getT1());
//                    scoreSearchDto.setT2(matchSettleCheckInfo.getT2());
//                    scoreSearchDto.setOperatorName(scoreSearchDto.getOperatorName() + ",(第" + checkNumber + "人)");
//                    syncScoreFactory.getProcessor(SettleSyncEnum.FOOTBALL_SYNC_SCORE).syncScore(scoreSearchDto);
//                }
//            }else {
//                log.info("[FootballNewMatchScoresSettleApiImpl] confirmMatchSettleScore with linkId:{} obtain redis fail!",matchSettleScoreDto.getLinkedId());
//                return Response.failed("1031933");
//            }
//        }catch (Exception e){
//            log.error("[FootballNewMatchScoresSettleApiImpl] confirmMatchSettleScore with linkId:{} error",matchSettleScoreDto.getLinkedId(), e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//        log.info("[FootballNewMatchScoresSettleApiImpl] confirmMatchSettleScore with linkId:{} end!",matchSettleScoreDto.getLinkedId());
//        return Response.success();
//    }
//
//    @Override
//    public Response editMatchSettleEvent(EditMatchSettleEventDto editMatchSettleEventDto) {
//        log.info("editMatchSettleEvent New param,editMatchSettleEventDto: {}",editMatchSettleEventDto);
//        String key = CommonConstant.MATCH_SEQUENCE_SCORE_SETTLE + editMatchSettleEventDto.getStandardMatchId();
//        //1.查询是否已经存在人工核对事件
//        try {
//            //3.更新或者新增
//            if(redisService.tryLock(key,key,2,5)) {
//                if(editMatchSettleEventDto.getEventCode().equals("goal")){
//                    return   this.editGoalEvent(editMatchSettleEventDto);
//                }else if(editMatchSettleEventDto.getEventCode().equals("corner")){
//                    return   this.editCornerEvent(editMatchSettleEventDto);
//
//                    //2.阶段比分
//                }else if(editMatchSettleEventDto.getEventCode().equals("fa_card")){
//                    return   this.editFaCardEvent(editMatchSettleEventDto);
//                }
//
//                return Response.success();
//            }else {
//                return Response.failed("1031933");
//            }
//        }catch (Exception e){
//            log.error("FootballNewMatchScoresSettleApiImpl-editMatchSettleEvent:",e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//        //4.返回成功
//    }
//
//    private Response editFaCardEvent(EditMatchSettleEventDto editMatchSettleEventDto) {
//        log.info("editFaCardEvent New editMatchSettleEventDto: {}",editMatchSettleEventDto);
//        //1.根据facard条件设置 主客队和 罚牌类型
//        if(StringUtils.isEmpty(editMatchSettleEventDto.getHomeAway())){
//            return Response.failed("1031939");
//        }
//        MatchSettleCheckInfo matchSettleCheckInfo=  matchSettleCheckService.searchCheckInfoByUser(editMatchSettleEventDto.getEventId(),editMatchSettleEventDto.getStandardMatchId(),
//                editMatchSettleEventDto.getOperatorName());
//
//        MatchSettleCheckInfo checkInfo =new  MatchSettleCheckInfo();
//
//        if(matchSettleCheckInfo!=null){
//            if(matchSettleCheckInfo.getCheckStatus()== MatchSettleCheckConstant.CheckStatus.CONFIRM){
//                //错误编码还没编辑 该用户已经确认比分之类的
//                return Response.failed("1031933");
//            }
//        }
//        MatchSettleEvent matchSettleEvent =matchSettleEventMapper.selectByPrimaryKey(editMatchSettleEventDto.getEventId());
//        Integer checkNumber = matchSettleEvent.getCheckNumber();
//        if(matchSettleEvent==null){
//            return Response.failed("1031935");
//        }
//        if(StringUtils.isEmpty(editMatchSettleEventDto.getHomeAway())){
//            return Response.failed("1031939");
//        }
//        //次序事件控制1917 需求
////        if(!matchSettleService.checkIfEventBeforeAllEdit(matchSettleEvent)){
////            return Response.failed("1031943");
////        }
////        if(!matchSettleService.checkIfEventAfterSettled(matchSettleEvent)){
////            return Response.failed("1031944");
////        }
//        MatchSettleEvent matchSettleEventBefore =new MatchSettleEvent();
//        BeanUtils.copyProperties(matchSettleEvent,matchSettleEventBefore);
//        //1.自动计算进球比分
//        footballMatchScoresSettleApi.updateFaCardEventByInfo(matchSettleEvent,editMatchSettleEventDto.getHomeAway());
//        //比分校验是否相同
//        if(editMatchSettleEventDto.getGoWaterStatus()!=null&&editMatchSettleEventDto.getGoWaterStatus()==1){
//            matchSettleEvent.setGoWaterStatus(1);
//        }else {
//            matchSettleEvent.setGoWaterStatus(0);
//        }
//        matchSettleEvent.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
//        log.info("Titan02:"+matchSettleEvent.getFiveMinSection());
//        if(MatchEventInfoSettleUtils.equileMatchSettleEvent(matchSettleEventBefore,matchSettleEvent)){
//            return Response.failed("1031940");
//        }
//        if(matchSettleCheckInfo==null){
//            matchSettleCheckInfo =new MatchSettleCheckInfo();
//            BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
//            matchSettleCheckInfo.setId(IdWorker.getId());
//            matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
//            SettleCheckUtils.initCheckMatchSettleEvent(matchSettleEvent,matchSettleCheckInfo);
//            //得到当前用户的次序
//            matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
//            matchSettleCheckInfo.setCheckNumber( SettleCheckUtils.getCheckNumber(matchSettleEvent.getCheckNumber()));
//            matchSettleCheckInfo.setUserName(editMatchSettleEventDto.getOperatorName());
//            matchSettleCheckInfo.setDataSourceCode("PA");
//            matchSettleCheckInfo.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
//            matchSettleCheckInfoMapper.insert(matchSettleCheckInfo);
//        }else {
//            BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
//            SettleCheckUtils.initCheckMatchSettleEvent(matchSettleEvent,matchSettleCheckInfo);
//            matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
//            matchSettleCheckInfo.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
//            matchSettleCheckInfoMapper.updateByPrimaryKey(matchSettleCheckInfo);
//        }
//        //日志录入
//        UpdateMatchSettleScoreDto dto =new UpdateMatchSettleScoreDto();
//        BeanUtils.copyProperties(editMatchSettleEventDto,dto);
//        dto.setSportId(matchSettleEvent.getSportId());
//        //罚球传的5分钟字段
//        matchSettleCheckInfo.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
//        matchSettleCheckInfo.setFifteenMinSection(editMatchSettleEventDto.getFifteenMinSection()!=null?editMatchSettleEventDto.getFifteenMinSection():editMatchSettleEventDto.getFiveMinSection());
//        iMatchSettleLogService.matchSettleCheckEventAddLog(checkInfo, matchSettleCheckInfo,
//                dto, OperateLogTypeEnum.EDIT,matchSettleEvent.getSettleNum(),checkNumber) ;
//
//        return Response.success();
//
//    }
//
//    private Response editCornerEvent(EditMatchSettleEventDto editMatchSettleEventDto) {
//        log.info("editCornerEvent New editMatchSettleEventDto: {}",editMatchSettleEventDto);
//        MatchSettleCheckInfo matchSettleCheckInfo=  matchSettleCheckService.searchCheckInfoByUser(editMatchSettleEventDto.getEventId(),editMatchSettleEventDto.getStandardMatchId(),
//                editMatchSettleEventDto.getOperatorName());
//        MatchSettleCheckInfo checkInfo =new  MatchSettleCheckInfo();
//
//        if(matchSettleCheckInfo!=null){
//            if(matchSettleCheckInfo.getCheckStatus()== MatchSettleCheckConstant.CheckStatus.CONFIRM){
//                //错误编码还没编辑 该用户已经确认比分之类的 TODO
//                return Response.failed("1031933");
//            }
//        }
//        //1.事件只编辑比分
//        MatchSettleEvent matchSettleEvent =matchSettleEventMapper.selectByPrimaryKey(editMatchSettleEventDto.getEventId());
//        Integer checkNumber = matchSettleEvent.getCheckNumber();
//        if(matchSettleEvent!=null){
//            //次序事件控制1917 需求
////            if(!matchSettleService.checkIfEventBeforeAllEdit(matchSettleEvent)){
////                return Response.failed("1031943");
////            }
////            if(!matchSettleService.checkIfEventAfterSettled(matchSettleEvent)){
////                return Response.failed("1031944");
////            }
//            MatchSettleEvent matchSettleEventBefore =new MatchSettleEvent();
//            BeanUtils.copyProperties(matchSettleEvent,matchSettleEventBefore);
//            //自动计算角球比分
//            footballMatchScoresSettleApi.updateGoalAndCornerEventByInfo(matchSettleEvent,editMatchSettleEventDto.getHomeAway(),"corner");
//            matchSettleEvent.setExtryInfo(editMatchSettleEventDto.getExtryInfo());
//            matchSettleEvent.setModifyTime(System.currentTimeMillis());
//            matchSettleEvent.setHomeAway(editMatchSettleEventDto.getHomeAway());
//            matchSettleEvent.setStatus(1);
//            matchSettleEvent.setOperater(editMatchSettleEventDto.getOperatorName());
//            if(editMatchSettleEventDto.getGoWaterStatus()!=null&&editMatchSettleEventDto.getGoWaterStatus()==1){
//                matchSettleEvent.setGoWaterStatus(1);
//            }else {
//                matchSettleEvent.setGoWaterStatus(0);
//            }
//            if(MatchEventInfoSettleUtils.equileMatchSettleEvent(matchSettleEventBefore,matchSettleEvent)){
//                return Response.failed("1031940");
//            }
//            matchSettleEvent.setModifyTime(System.currentTimeMillis());
//            if(matchSettleCheckInfo==null){
//                matchSettleCheckInfo =new MatchSettleCheckInfo();
//                BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
//                matchSettleCheckInfo.setId(IdWorker.getId());
//                matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
//                SettleCheckUtils.initCheckMatchSettleEvent(matchSettleEvent,matchSettleCheckInfo);
//                //得到当前用户的次序
//                matchSettleCheckInfo.setCheckNumber( SettleCheckUtils.getCheckNumber(matchSettleEvent.getCheckNumber()));
//                matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
//                matchSettleCheckInfo.setUserName(editMatchSettleEventDto.getOperatorName());
//                matchSettleCheckInfo.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
//                matchSettleCheckInfo.setDataSourceCode("PA");
//                matchSettleCheckInfoMapper.insert(matchSettleCheckInfo);
//            }else {
//                BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
//                SettleCheckUtils.initCheckMatchSettleEvent(matchSettleEvent,matchSettleCheckInfo);
//                matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
//                matchSettleCheckInfo.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
//                matchSettleCheckInfoMapper.updateByPrimaryKey(matchSettleCheckInfo);
//            }
//            //TODO  日志录入
//            UpdateMatchSettleScoreDto dto =new UpdateMatchSettleScoreDto();
//            BeanUtils.copyProperties(editMatchSettleEventDto,dto);
//            dto.setSportId(matchSettleEvent.getSportId());
//            matchSettleCheckInfo.setFifteenMinSection(editMatchSettleEventDto.getFifteenMinSection()!=null?editMatchSettleEventDto.getFifteenMinSection():editMatchSettleEventDto.getFiveMinSection());
//            iMatchSettleLogService.matchSettleCheckEventAddLog(checkInfo, matchSettleCheckInfo,
//                    dto, OperateLogTypeEnum.EDIT,matchSettleEvent.getSettleNum(),checkNumber) ;
//
//            return Response.success();
//        }else {
//            MatchSettleScore matchSettleScore =matchSettleScoreMapper.selectByPrimaryKey(editMatchSettleEventDto.getEventId());
//            MatchSettleScore matchSettleEventBefore =new MatchSettleScore();
//            if(matchSettleScore!=null) {
//                //角球阶段比分由人工录入
//                //比分判断是否相同
//                if(MatchEventInfoSettleUtils.equileMatchSettleScores(matchSettleEventBefore,matchSettleScore)){
//                    return Response.failed("1031940");
//                }
//                if(editMatchSettleEventDto.getGoWaterStatus()!=null&&editMatchSettleEventDto.getGoWaterStatus()==1){
//                    matchSettleScore.setGoWaterStatus(1);
//                }else {
//                    matchSettleScore.setGoWaterStatus(0);
//                }
//                matchSettleScore.setExtryInfo(editMatchSettleEventDto.getExtryInfo());
//                matchSettleScore.setT1(editMatchSettleEventDto.getT1());
//                matchSettleScore.setT2(editMatchSettleEventDto.getT2());
//                matchSettleScore.setModifyTime(System.currentTimeMillis());
//                matchSettleScore.setOperater(editMatchSettleEventDto.getOperatorName());
//                matchSettleScore.setStatus(1);
//                if(matchSettleCheckInfo==null){
//                    matchSettleCheckInfo =new MatchSettleCheckInfo();
//                    BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
//                    matchSettleCheckInfo.setId(IdWorker.getId());
//                    matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
//                    SettleCheckUtils.initCheckMatchSettleScore(matchSettleScore,matchSettleCheckInfo);
//                    //得到当前用户的次序
//                    matchSettleCheckInfo.setCheckNumber( SettleCheckUtils.getCheckNumber(matchSettleEvent.getCheckNumber()));
//                    matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
//                    matchSettleCheckInfo.setUserName(editMatchSettleEventDto.getOperatorName());
//                    matchSettleCheckInfo.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
//                    matchSettleCheckInfoMapper.insert(matchSettleCheckInfo);
//                }else {
//                    BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
//                    SettleCheckUtils.initCheckMatchSettleScore(matchSettleScore,matchSettleCheckInfo);
//                    matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
//                    matchSettleCheckInfo.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
//                    matchSettleCheckInfoMapper.updateByPrimaryKey(matchSettleCheckInfo);
//                }
//                UpdateMatchSettleScoreDto dto =new UpdateMatchSettleScoreDto();
//                BeanUtils.copyProperties(editMatchSettleEventDto,dto);
//                dto.setSportId(matchSettleEvent.getSportId());
//                matchSettleCheckInfo.setFifteenMinSection(editMatchSettleEventDto.getFifteenMinSection()!=null?editMatchSettleEventDto.getFifteenMinSection():editMatchSettleEventDto.getFiveMinSection());
//                iMatchSettleLogService.matchSettleCheckScoreAddLog(checkInfo, matchSettleCheckInfo,
//                        dto, OperateLogTypeEnum.EDIT,matchSettleEvent.getSettleNum(),checkNumber) ;
//            }else {
//                return Response.failed("1031940");
//            }
//            //TODO  日志录入
//            return Response.success();
//        }
//    }
//
//    private Response editGoalEvent(EditMatchSettleEventDto editMatchSettleEventDto) {
//        log.info("editGoalEvent New param,editMatchSettleEventDto :{}",editMatchSettleEventDto);
//        MatchSettleCheckInfo matchSettleCheckInfo=  matchSettleCheckService.searchCheckInfoByUser(editMatchSettleEventDto.getEventId(),editMatchSettleEventDto.getStandardMatchId(),
//                editMatchSettleEventDto.getOperatorName());
//
//        MatchSettleCheckInfo checkInfo =new  MatchSettleCheckInfo();
//
//
//        if(matchSettleCheckInfo!=null){
//            if(matchSettleCheckInfo.getCheckStatus()== MatchSettleCheckConstant.CheckStatus.CONFIRM){
//                //错误编码还没编辑 该用户已经确认比分之类的
//                return Response.failed("1031933");
//            }
//        }
//        MatchSettleEvent matchSettleEvent =matchSettleEventMapper.selectByPrimaryKey(editMatchSettleEventDto.getEventId());
//        Integer checkNumber = matchSettleEvent.getCheckNumber();
//        if(matchSettleEvent==null){
//            return Response.failed("1031935");
//        }
//        if(StringUtils.isEmpty(editMatchSettleEventDto.getHomeAway())){
//            return Response.failed("1031939");
//        }
//        if(!matchSettleCheckService.isPeriodScoresBeforeSettledByEvent(matchSettleEvent)){
//            return Response.failed("10138");
//        }
//        //次序事件控制1917 需求
////        if(!matchSettleService.checkIfEventBeforeAllEdit(matchSettleEvent)){
////            return Response.failed("1031943");
////        }
////        if(!matchSettleService.checkIfEventAfterSettled(matchSettleEvent)){
////            return Response.failed("1031944");
////        }
//        MatchSettleEvent matchSettleEventBefore =new MatchSettleEvent();
//        BeanUtils.copyProperties(matchSettleEvent,matchSettleEventBefore);
//        //1.自动计算进球比分
//        footballMatchScoresSettleApi.updateGoalAndCornerEventByInfo(matchSettleEvent,editMatchSettleEventDto.getHomeAway(),"goal");
//        //比分校验是否相同
//        if(editMatchSettleEventDto.getGoWaterStatus()!=null&&editMatchSettleEventDto.getGoWaterStatus()==1){
//            matchSettleEvent.setGoWaterStatus(1);
//        }else {
//            matchSettleEvent.setGoWaterStatus(0);
//        }
//        matchSettleEvent.setExtryInfo(editMatchSettleEventDto.getExtryInfo());
//        matchSettleEvent.setModifyTime(System.currentTimeMillis());
//        matchSettleEvent.setHomeAway(editMatchSettleEventDto.getHomeAway());
//        matchSettleEvent.setStatus(1);
//        matchSettleEvent.setOperater(editMatchSettleEventDto.getOperatorName());
//        matchSettleEvent.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
//        if(MatchEventInfoSettleUtils.equileMatchSettleEvent(matchSettleEventBefore,matchSettleEvent)){
//            return Response.failed("1031940");
//        }
//        if(matchSettleCheckInfo==null){
//            matchSettleCheckInfo =new MatchSettleCheckInfo();
//            BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
//            matchSettleCheckInfo.setId(IdWorker.getId());
//            matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
//            SettleCheckUtils.initCheckMatchSettleEvent(matchSettleEvent,matchSettleCheckInfo);
//            //得到当前用户的次序
//            matchSettleCheckInfo.setCheckNumber( SettleCheckUtils.getCheckNumber(matchSettleEvent.getCheckNumber()));
//            matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
//            matchSettleCheckInfo.setUserName(editMatchSettleEventDto.getOperatorName());
//            matchSettleCheckInfo.setDataSourceCode("PA");
//            matchSettleCheckInfoMapper.insert(matchSettleCheckInfo);
//        }else {
//            BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
//            SettleCheckUtils.initCheckMatchSettleEvent(matchSettleEvent,matchSettleCheckInfo);
//            matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
//            matchSettleCheckInfoMapper.updateByPrimaryKey(matchSettleCheckInfo);
//        }
//        //TODO  日志录入
//        UpdateMatchSettleScoreDto dto =new UpdateMatchSettleScoreDto();
//        BeanUtils.copyProperties(editMatchSettleEventDto,dto);
//        dto.setSportId(matchSettleEvent.getSportId());
//        matchSettleCheckInfo.setFifteenMinSection(editMatchSettleEventDto.getFifteenMinSection()!=null?editMatchSettleEventDto.getFifteenMinSection():editMatchSettleEventDto.getFiveMinSection());
//        iMatchSettleLogService.matchSettleCheckEventAddLog(checkInfo, matchSettleCheckInfo,
//                dto, OperateLogTypeEnum.EDIT,matchSettleEvent.getSettleNum(),checkNumber) ;
//
//
//        return Response.success();
//    }
//
//    @Override
//    public Response confirmMatchSettleEvent(EditMatchSettleEventDto matchSettleEventDto) {
//        log.info("confirmMatchSettleEvent New param,matchSettleEventDto: {}",matchSettleEventDto);
//        //1.查询人工核对事件
//        //2.检查当前的用户是否满足编辑比分的次序
//        //3.更新状态
//        //4.进入同一核对比分流程
//        MatchSettleCheckInfo matchSettleCheckInfo=  matchSettleCheckService.searchCheckInfoByUser(matchSettleEventDto.getEventId(),
//                matchSettleEventDto.getStandardMatchId(),matchSettleEventDto.getOperatorName());
//
//        String key = CommonConstant.MATCH_SEQUENCE_SCORE_SETTLE + matchSettleEventDto.getStandardMatchId();
//        if(matchSettleService.checkIfOverSettleTime(matchSettleEventDto.getStandardMatchId())){
//            return Response.failed("1031930");
//        }
//        log.info("事件Id::{}:: 当前事件被确认参数:{} ",matchSettleEventDto.getEventId(),matchSettleEventDto);
//        try {
//            if(redisService.tryLock(key,key,2,5)) {
//                MatchSettleEvent matchSettleEvent =matchSettleEventMapper.selectByPrimaryKey(matchSettleEventDto.getEventId());
//                Integer checkNumber = matchSettleEvent.getCheckNumber();
//                if(matchSettleEvent==null){
//                    return Response.failed("1031935");
//                }
//                if(matchSettleCheckInfo==null){
//                    return Response.failed("1031934");
//                }
//                MatchSettleCheckInfo checkInfo =new  MatchSettleCheckInfo();
//                BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
//                if(matchSettleCheckInfo.getCheckStatus()!= MatchSettleCheckConstant.CheckStatus.EDIT){
//                    return Response.failed("1031934");
//                }
//                //次序校验 如果次序不对返回次序不对
////                if(matchSettleEvent.getCheckNumber()==null||matchSettleEvent.getCheckNumber()!=matchSettleCheckInfo.getCheckNumber()){
////                    log.error("人工核对确认比分次序错误:",matchSettleEvent);
////                    return Response.failed("1031934");
////                }
//
//
//                matchSettleCheckInfo.setEventOrder(matchSettleEvent.getEventOrder());
//                matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.CONFIRM);
//                matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
//                matchSettleCheckInfoMapper.updateByPrimaryKey(matchSettleCheckInfo);
//                //3.确认记录日志
//                UpdateMatchSettleScoreDto dto =new UpdateMatchSettleScoreDto();
//                BeanUtils.copyProperties(matchSettleEventDto,dto);
//                dto.setSportId(matchSettleEvent.getSportId());
////                matchSettleCheckInfo.setFifteenMinSection(matchSettleEventDto.getFifteenMinSection());
//                iMatchSettleLogService.matchSettleCheckEventAddLog(checkInfo, matchSettleCheckInfo,
//                        dto, OperateLogTypeEnum.CONFIRM_SCORE,matchSettleEvent.getSettleNum(),checkNumber) ;
//                Pair<Boolean, Boolean> isScoreOrEventDiff=matchSettleCheckService.checkCommonMatchSettleScoreEvent(matchSettleEvent,matchSettleCheckInfo,false);
//                matchSettleCheckService.updateMatchGrayStatus(matchSettleCheckInfo.getStandardMatchId());
//
//
//                if(matchSettleEvent.getStatus()!=3&& isScoreOrEventDiff.getLeft()){
//                    //1031947=比分不一致需要下个审核员审核
//                    return Response.failed("1031947");
//                }
//                return Response.success();
//            }else {
//                return Response.failed("1031933");
//            }
//        }catch (Exception e){
//            log.error("FootballNewMatchScoresSettleApiImpl-confirmMatchSettleEvent:",e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//    /**
//     * 新编辑点球大战比分
//     * 编辑逻辑: 1. 如果有 二次编辑权限直接编辑  要重写 需要重新计算主客队谁先踢球 而且判断是否主客队谁先踢球已经结算 否则无法编辑
//     *     2.如果没有 则走多人编辑  需要重新计算主客队谁先踢球 而且判断是否主客队谁先踢球已经结算 否则无法编辑
//     *     确认逻辑: 1.如果有二次编辑权限直接走确认 结算
//     *     2.如果没有则走多人确认公共事件方法
//     *
//     * 点球大战设置比分
//     * */
//    @Override
//    public Response setPenaltyScores(EditMatchSettleEventDto settleScoreSearchDto) {
//        log.info("setPenaltyScores New param,settleScoreSearchDto: {}",settleScoreSearchDto);
//        String key ="MATCH_SETTLE_INFO:"+ settleScoreSearchDto.getEventId();
//        if(matchSettleService.checkIfOverSettleTime(settleScoreSearchDto.getStandardMatchId())){
//            return Response.failed("1031930");
//        }
//        try {
//            if(redisService.tryLock(key,key,2,5)) {
//                //0.查询当前编辑用户的比分核对记录 如果没有则新增
//                MatchSettleCheckInfo matchSettleCheckInfo=  matchSettleCheckService.searchCheckInfoByUser(settleScoreSearchDto.getEventId(),settleScoreSearchDto.getStandardMatchId(),
//                        settleScoreSearchDto.getOperatorName());
//                //0.01.判断谁先射门是否已经结算如果没结算则直接返回失败
//                if(!footballMatchScoresSettleApi.isTeamFirstSettled(settleScoreSearchDto.getStandardMatchId())){
//                    return Response.failed("1031952");
//                }
//                MatchSettleCheckInfo checkInfo =new  MatchSettleCheckInfo();
//                //0.1 如果已经确认则无法更改
//                if(matchSettleCheckInfo!=null){
//                    BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
//                    if(matchSettleCheckInfo.getCheckStatus()== MatchSettleCheckConstant.CheckStatus.CONFIRM){
//                        //错误编码还没编辑 该用户已经确认比分之类的
//                        return Response.failed("1031933");
//                    }
//                }
//                //0.2 查询当前的事件id是否存在不存在则返回失败
//                MatchSettleEvent matchSettleEvent =matchSettleEventMapper.selectByPrimaryKey(settleScoreSearchDto.getEventId());
//                if(matchSettleEvent==null){
//                    return Response.failed("1031935");
//                }
//                //0.3 查询当前的事件id是否结算如果结算则返回失败
//                if(matchSettleEvent.getStatus()==3){
//                    return Response.failed("1031939");
//                }
//                Integer checkNumber = matchSettleEvent.getCheckNumber();
//                //0.4 如果matchSettleCheckInfo is null 则新增
//                if(matchSettleCheckInfo==null){
//                    matchSettleCheckInfo =new MatchSettleCheckInfo();
//                    BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
//                    matchSettleCheckInfo =  SettleCheckUtils.initCheckPaniltyEvent(matchSettleEvent,matchSettleCheckInfo);
//                    matchSettleCheckInfo.setId(IdWorker.getId());
//                    matchSettleCheckInfo.setCreateTime(System.currentTimeMillis());
//                    matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
//                    matchSettleCheckInfo.setCheckNumber( SettleCheckUtils.getCheckNumber(matchSettleEvent.getCheckNumber()));
//                    matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
//                    matchSettleCheckInfo.setUserName(settleScoreSearchDto.getOperatorName());
//                    matchSettleCheckInfo.setGoWaterStatus(settleScoreSearchDto.getGoWaterStatus());
//                    matchSettleCheckInfo.setDataSourceCode("PA");
//                    matchSettleCheckInfoMapper.insert(matchSettleCheckInfo);
//                }
//                //1.判断是否次序事件 如果是则计算次序事件比分
//                if(matchSettleEvent.getSettleNum().equals("1030")){
//                    boolean isCanCount= footballMatchScoresSettleApi.countPenaltyScores(settleScoreSearchDto,matchSettleEvent);
//                    if(!isCanCount){
//                        return Response.failed("1031937");
//                    }
//                    matchSettleCheckInfo.setT1(matchSettleEvent.getT1());
//                    matchSettleCheckInfo.setT2(matchSettleEvent.getT2());
//                    matchSettleCheckInfo.setExtryInfo(settleScoreSearchDto.getExtryInfo());
//                }else {
//                    //2.如果不是次序事件 则直接编辑比分
//                    matchSettleCheckInfo.setT1(settleScoreSearchDto.getT1());
//                    matchSettleCheckInfo.setT2(settleScoreSearchDto.getT2());
//                    matchSettleCheckInfo.setExtryInfo(settleScoreSearchDto.getExtryInfo());
//                }
//                if(settleScoreSearchDto.getGoWaterStatus()!=null&&settleScoreSearchDto.getGoWaterStatus()==1){
//                    matchSettleCheckInfo.setGoWaterStatus(1);
//                }else {
//                    matchSettleCheckInfo.setGoWaterStatus(0);
//                }
//                matchSettleCheckInfo.setHomeAway(matchSettleEvent.getHomeAway());
//                matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
//                matchSettleCheckInfoMapper.updateByPrimaryKey(matchSettleCheckInfo);
//                //  日志录入
//                matchSettleCheckInfo.setEventOrder(matchSettleEvent.getEventOrder());
//                UpdateMatchSettleScoreDto dto =new UpdateMatchSettleScoreDto();
//                BeanUtils.copyProperties(settleScoreSearchDto,dto);
//                dto.setSportId(matchSettleEvent.getSportId());
//                iMatchSettleLogService.matchSettleCheckEventAddLog(checkInfo, matchSettleCheckInfo,
//                        dto, OperateLogTypeEnum.EDIT,matchSettleEvent.getSettleNum(),checkNumber) ;
//
//                return Response.success();
//            }else {
//                return Response.failed("1031933");
//            }
//        }catch (Exception e){
//            log.error("FootballNewMatchScoresSettleApiImpl-setPenaltyScores:",e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//    /**
//     * 点球大战确认
//     * */
//    @Override
//    public Response confirmPenaltyScores(EditMatchSettleEventDto matchSettleEventDto) {
//        log.info("confirmPenaltyScores New param,matchSettleEventDto: {}",matchSettleEventDto);
//        String key ="MATCH_SETTLE_INFO:"+ matchSettleEventDto.getEventId();
//        if(matchSettleService.checkIfOverSettleTime(matchSettleEventDto.getStandardMatchId())){
//            return Response.failed("1031930");
//        }
//        log.info("事件Id::{}:: 当前事件被确认参数:{} ",matchSettleEventDto.getEventId(),matchSettleEventDto);
//        try {
//            if(redisService.tryLock(key,key,2,5)) {
//                MatchSettleEvent matchSettleEvent =matchSettleEventMapper.selectByPrimaryKey(matchSettleEventDto.getEventId());
//                if(matchSettleEvent==null){
//                    return Response.failed("1031935");
//                }
//                if(matchSettleEvent.getStatus()!=NOT_CONFIRM){
//                    return Response.failed("1031934");
//                }
//                matchSettleEvent.setModifyTime(System.currentTimeMillis());
//                matchSettleEvent.setStatus(CONFIRM);
//                matchSettleEvent.setHomeAway(matchSettleEventDto.getHomeAway());
//                matchSettleEvent.setT1(matchSettleEventDto.getT1());
//                matchSettleEvent.setT2(matchSettleEventDto.getT2());
//                matchSettleEvent.setPlayerNameCode(matchSettleEventDto.getPlayerNameCode());
//                matchSettleEvent.setExtryInfo(matchSettleEventDto.getExtryInfo());
//                matchSettleEventMapper.updateByPrimaryKey(matchSettleEvent);
//                //2.确认记录日志
//                iMatchSettleLogService.matchSettleEventAddLog(matchSettleEvent,matchSettleEventDto.getOperatorName(),
//                        OperateLogTypeEnum.CONFIRM_SCORE.getCode().toString(),"",matchSettleEventDto.getIpAddress());
//
//                //3.返回查询事件列表
////                ThreadUtils.addTaskThreadPool(new Thread(() ->  wsPushService.pushStandardSettleEvent(matchSettleEventDto.getStandardMatchId(),
////                        matchSettleEventDto.getEventCode())), "推送WS标准赛事结算比分" + matchSettleEventDto.getStandardMatchId());
//                wsPushService.pushStandardSettleEvent(matchSettleEventDto.getStandardMatchId(),
//                        matchSettleEventDto.getEventCode());
//                return Response.success();
//            }else {
//                return Response.failed("1031933");
//            }
//        }catch (Exception e){
//            log.error("FootballNewMatchScoresSettleApiImpl-confirmPenaltyScores:",e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//
//
//    @Override
//    public boolean isLockedByMatchSettle(Long standardMatchId,String userName) {
//        MatchSettleInfoExample matchSettleInfoExample = new MatchSettleInfoExample();
//        MatchSettleInfoExample.Criteria criteria = matchSettleInfoExample.createCriteria();
//        criteria.andStandardMatchIdEqualTo(standardMatchId);
//        List<MatchSettleInfo> matchSettleInfos = matchSettleInfoMapper.selectByExample(matchSettleInfoExample);
//        if (matchSettleInfos.size() != 0) {
//            MatchSettleInfo matchSettleInfo =matchSettleInfos.get(0);
//            String arrayStr =matchSettleInfo.getLimitUserArray();
//            if(StringUtils.isEmpty(arrayStr)){
//                return false;
//            }else {
//                JSONArray array =JSONArray.parseArray(arrayStr);
//                for (Object o : array) {
//                    String userOne =o.toString();
//                    if(userName.equals(userOne)){
//                        return true;
//                    }
//                }
//            }
//            return false;
//        }else {
//            return false;
//        }
//    }
//
//    @Override
//    public Response confirmMatchSettlePlayerAndMethod(EditMatchSettleEventDto matchSettleEventDto) {
//        log.info("confirmMatchSettlePlayerAndMethod New param ,matchSettleEventDto: {}",matchSettleEventDto);
//        String key ="MATCH_SETTLE_INFO:"+ matchSettleEventDto.getEventId();
//        if(matchSettleService.checkIfOverSettleTime(matchSettleEventDto.getStandardMatchId())){
//            return Response.failed("1031930");
//        }
//        log.info("事件Id::{}:: 当前事件被确认参数:{} ",matchSettleEventDto.getEventId(),matchSettleEventDto);
//        try {
//            if(redisService.tryLock(key,key,2,5)) {
//                MatchSettleEvent matchSettleEvent =matchSettleEventMapper.selectByPrimaryKey(matchSettleEventDto.getEventId());
//                if(matchSettleEvent==null){
//                    return Response.failed("1031935");
//                }
//                if(matchSettleEvent.getStatus()!=NOT_CONFIRM){
//                    return Response.failed("1031934");
//                }
//                matchSettleEvent.setT2(matchSettleEventDto.getT2());
//                matchSettleEvent.setT1(matchSettleEventDto.getT1());
//                matchSettleEvent.setPlayerNameCode(matchSettleEventDto.getPlayerNameCode());
//                matchSettleEvent.setExtryInfo(matchSettleEventDto.getExtryInfo());
//                matchSettleEvent.setModifyTime(System.currentTimeMillis());
//                matchSettleEvent.setStatus(CONFIRM);
//                matchSettleEvent.setHomeAway(matchSettleEventDto.getHomeAway());
//                matchSettleEventMapper.updateByPrimaryKey(matchSettleEvent);
//                //2.确认记录日志
//                iMatchSettleLogService.matchSettleEventAddLog(matchSettleEvent,matchSettleEventDto.getOperatorName(),
//                        OperateLogTypeEnum.CONFIRM_SCORE.getCode().toString(),"",matchSettleEventDto.getIpAddress());
//
//                //3.返回查询事件列表
////                ThreadUtils.addTaskThreadPool(new Thread(() ->  wsPushService.pushStandardSettleEvent(matchSettleEventDto.getStandardMatchId(),
////                        matchSettleEventDto.getEventCode())), "推送WS标准赛事结算比分" + matchSettleEventDto.getStandardMatchId());
//
//                //由于此时第X进球事件未核对，则结算事件表中无数据，原ws推送的数据缺失
//                //若confirm的事件为进球方式和球员，则推送赛事列表的ws，以便前端能刷新最新数据
//                if(matchSettleEvent.getEventType() == 2l && "goal".equals(matchSettleEventDto.getEventCode())){
//                    wsPushService.pushSettleMatchList(new MatchListSettleDto(matchSettleEventDto.getStandardMatchId(), matchSettleEventDto.getEventCode(),null,null,4));
//                }else {
//                    wsPushService.pushStandardSettleEvent(matchSettleEventDto.getStandardMatchId(),
//                            matchSettleEventDto.getEventCode());
//                }
//                return Response.success();
//            }else {
//                return Response.failed("1031933");
//            }
//        }catch (Exception e){
//            log.error("FootballNewMatchScoresSettleApiImpl-confirmMatchSettlePlayerAndMethod:",e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//
//    @Override
//    public Response settleMatchSettlePlayerAndMethod(EditMatchSettleEventDto matchSettleScoreDto) {
//        log.info("settleMatchSettlePlayerAndMethod New param,matchSettleScoreDto: {}",matchSettleScoreDto);
//        String key ="MATCH_SETTLE_INFO:"+ matchSettleScoreDto.getEventId();
//        if(matchSettleService.checkIfOverSettleTime(matchSettleScoreDto.getStandardMatchId())){
//            return Response.failed("1031930");
//        }
//        try {
//            if(redisService.tryLock(key,key,2,5)) {
//                MatchSettleEvent matchSettleEvent =matchSettleEventMapper.selectByPrimaryKey(matchSettleScoreDto.getEventId());
//                if(matchSettleEvent==null){
//                    return Response.failed("1031935");
//                }
//                if(matchSettleEvent.getStatus()!=CONFIRM){
//                    return Response.failed("1031936");
//                }
//                Integer settleTimes =matchSettleEvent.getSettleTimes();
//                Integer settleCount = matchSettleEvent.getSettleCount();
//                if(settleTimes==null){
//                    settleTimes=0;
//                }
//                if (settleCount == null ) {
//                    matchSettleEvent.setSettleCount(0);
//                }
//                settleTimes++;
//
//                //二次结算,必须给出结算原因
//                if ( matchSettleEvent.getSettleCount() >  0 &&
//                        (matchSettleScoreDto.getSettleReason()==null  ||
//                                matchSettleScoreDto.getSettleReason()== 0) ) {
//                    return Response.failed("1031953");
//                }
//
//                String  before= "-";
//                Integer settleReason = matchSettleEvent.getSettleReason();
//                if (settleReason != null &&  settleReason != 0 ) {
//                    before = settleReason.toString();
//                    if (settleReason == 118) {
//                        before += ": "+matchSettleEvent.getSettleReasonDetail();
//                    }
//                }
//
//                matchSettleEvent.setStatus(SETTLED);
//                matchSettleEvent.setSettleCount(matchSettleEvent.getSettleCount()+1);
//                matchSettleEvent.setModifyTime(System.currentTimeMillis());
//                matchSettleEvent.setSettleTimes(settleTimes);
//                matchSettleEvent.setOperater(matchSettleScoreDto.getOperatorName());
//                matchSettleEvent.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.SETTLE);
//                matchSettleEvent.setUserid(matchSettleScoreDto.getOperatorId());
//                matchSettleEvent.setSettleReason(matchSettleScoreDto.getSettleReason());
//                matchSettleEvent.setSettleReasonDetail(matchSettleScoreDto.getSettleReasonDetail());
//                matchSettleEventMapper.updateByPrimaryKey(matchSettleEvent);
//                log.info("比分Id::{}:: 当前事件被结算参数:{} ",matchSettleScoreDto.getEventId(),matchSettleEvent);
//                //结算时把回滚订单数清零
//                matchSettleService.settleRollBackSetNullOrderCount(matchSettleEvent.getId());
//                //1.日志
//                iMatchSettleLogService.matchSettleEventAddLog(matchSettleEvent,
//                        matchSettleScoreDto.getOperatorName(),OperateLogTypeEnum.SCORE_SETTLE.getCode().toString()
//                        ,before,matchSettleScoreDto.getIpAddress());
//
//                //2.MQ下发
//                matchSettleScoresProducer.sendMatchSettleEvent(matchSettleEvent);
////                ThreadUtils.addTaskThreadPool(new Thread(() ->  wsPushService.pushStandardSettleEvent(matchSettleScoreDto.getStandardMatchId(),
////                        matchSettleScoreDto.getEventCode())), "推送WS标准赛事结算比分" + matchSettleScoreDto.getStandardMatchId());
//                wsPushService.pushStandardSettleEvent(matchSettleScoreDto.getStandardMatchId(),
//                        matchSettleScoreDto.getEventCode());
//                return Response.success();
//            }else {
//                return Response.failed("1031933");
//            }
//        }catch (Exception e){
//            log.error("FootballNewMatchScoresSettleApiImpl-settleMatchSettlePlayerAndMethod:",e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//    /**
//     * 点球大战谁先踢球设置(按操盘手顺序)
//     * */
//    @Override
//    public Response setPenaltyTeamFirst(EditMatchSettleEventDto matchSettleScoreDto) {
//        log.info("setPenaltyTeamFirst New param,matchSettleScoreDto: {}",matchSettleScoreDto);
//        String key ="MATCH_SETTLE_INFO:"+ matchSettleScoreDto.getEventId();
//        //1.查询是否已经存在人工核对事件
//        try {
//            //3.更新或者新增
//            if(redisService.tryLock(key,key,2,5)) {
//                MatchSettleCheckInfo matchSettleCheckInfo=  matchSettleCheckService.searchCheckInfoByUser(matchSettleScoreDto.getEventId(),matchSettleScoreDto.getStandardMatchId(),
//                        matchSettleScoreDto.getOperatorName());
//
//                MatchSettleCheckInfo checkInfo =new  MatchSettleCheckInfo();
//
//                if(matchSettleCheckInfo!=null){
//                    if(matchSettleCheckInfo.getCheckStatus()== MatchSettleCheckConstant.CheckStatus.CONFIRM){
//                        //错误编码还没编辑 该用户已经确认比分之类的
//                        return Response.failed("1031933");
//                    }
//                }
//                MatchSettleEvent matchSettleEvent =matchSettleEventMapper.selectByPrimaryKey(matchSettleScoreDto.getEventId());
//                Integer checkNumber = matchSettleEvent.getCheckNumber();
//                if(matchSettleEvent==null){
//                    return Response.failed("1031935");
//                }
//                if(StringUtils.isEmpty(matchSettleScoreDto.getHomeAway())){
//                    return Response.failed("1031939");
//                }
//                //结算后不能编辑
//                if(matchSettleEvent.getStatus()==3){
//                    return Response.failed("1031939");
//                }
//                if(matchSettleCheckInfo==null){
//                    matchSettleCheckInfo =new MatchSettleCheckInfo();
//                    BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
//                    matchSettleCheckInfo.setId(IdWorker.getId());
//                    matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
//                    SettleCheckUtils.initCheckMatchSettleEvent(matchSettleEvent,matchSettleCheckInfo);
//                    //得到当前用户的次序
//                    matchSettleCheckInfo.setCheckNumber( SettleCheckUtils.getCheckNumber(matchSettleEvent.getCheckNumber()));
//                    matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
//                    if (!StringUtils.isAnyEmpty(matchSettleEvent.getOperater()) && (matchSettleEvent.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || matchSettleEvent.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode()))){
//                        matchSettleCheckInfo.setUserName(matchSettleEvent.getOperater() + "(" + checkInfo.getDataSourceCode() +")");
//                    }else {
//                        matchSettleCheckInfo.setUserName(matchSettleScoreDto.getOperatorName());
//                    }
//                    matchSettleCheckInfo.setHomeAway(matchSettleScoreDto.getHomeAway());
//                    matchSettleCheckInfo.setGoWaterStatus(0);
//                    matchSettleCheckInfo.setDataSourceCode("PA");
//                    matchSettleCheckInfoMapper.insert(matchSettleCheckInfo);
//                }else {
//                    BeanUtils.copyProperties(matchSettleCheckInfo,checkInfo);
//                    SettleCheckUtils.initCheckMatchSettleEvent(matchSettleEvent,matchSettleCheckInfo);
//                    if (!StringUtils.isAnyEmpty(matchSettleEvent.getOperater()) && (matchSettleEvent.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || matchSettleEvent.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode()))){
//                        matchSettleCheckInfo.setUserName(matchSettleEvent.getOperater() + "(" + checkInfo.getDataSourceCode() +")");
//                    }else {
//                        matchSettleCheckInfo.setUserName(matchSettleScoreDto.getOperatorName());
//                    }
//                    matchSettleCheckInfo.setFirstT1(0);
//                    matchSettleCheckInfo.setFirstT2(0);
//                    matchSettleCheckInfo.setSecondT1(0);
//                    matchSettleCheckInfo.setSecondT2(0);
//                    matchSettleCheckInfo.setGoWaterStatus(0);
//                    matchSettleCheckInfo.setCreateTime(System.currentTimeMillis());
//                    matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
//                    matchSettleCheckInfo.setHomeAway(matchSettleScoreDto.getHomeAway());
//                    matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
//                    matchSettleCheckInfoMapper.updateByPrimaryKey(matchSettleCheckInfo);
//                }
//                //  日志录入
//                UpdateMatchSettleScoreDto dto =new UpdateMatchSettleScoreDto();
//                BeanUtils.copyProperties(matchSettleScoreDto,dto);
//                dto.setSportId(matchSettleEvent.getSportId());
//                iMatchSettleLogService.matchSettleCheckEventAddLog(checkInfo, matchSettleCheckInfo,
//                        dto, OperateLogTypeEnum.SCORES_SETTLE_10040,matchSettleEvent.getSettleNum(),checkNumber) ;
//
//                return Response.success();
//            }else {
//                return Response.failed("1031933");
//            }
//        }catch (Exception e){
//            log.error("FootballNewMatchScoresSettleApiImpl-setPenaltyTeamFirst:",e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//    /**
//     * 点球大战谁先踢球单独结算
//     * */
//    @Override
//    public Response settlePenaltyTeamFirst(EditMatchSettleEventDto matchSettleEventDto) {
//        log.info("settlePenaltyTeamFirst New param,matchSettleEventDto: {}",matchSettleEventDto);
//        String key ="MATCH_SETTLE_INFO:"+ matchSettleEventDto.getEventId();
//        //1.查询是否已经存在人工核对事件
//        try {
//            //3.更新或者新增
//            if(redisService.tryLock(key,key,2,5)) {
//                MatchSettleEvent matchSettleEvent =matchSettleEventMapper.selectByPrimaryKey(matchSettleEventDto.getEventId());
//                if(matchSettleEvent==null){
//                    return Response.failed("1031935");
//                }
//                if(StringUtils.isEmpty(matchSettleEventDto.getHomeAway())){
//                    return Response.failed("1031939");
//                }
//                //结算后不能编辑
//                if(matchSettleEvent.getStatus()==3){
//                    return Response.failed("1031939");
//                }
//                String  before= "-";
//                Integer settleReason = matchSettleEvent.getSettleReason();
//                if (settleReason != null &&  settleReason != 0 ) {
//                    before = settleReason.toString();
//                    if (settleReason == 118) {
//                        before += ": "+matchSettleEvent.getSettleReasonDetail();
//                    }
//                }
//
//                matchSettleEvent.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.SETTLE);
//                matchSettleEvent.setSettleCount(matchSettleEvent.getSettleCount()== null ? 1 : matchSettleEvent.getSettleCount()+1);
//                matchSettleEvent.setModifyTime(System.currentTimeMillis());
//                matchSettleEvent.setStatus(SETTLED);
//                matchSettleEventMapper.updateByPrimaryKey(matchSettleEvent);
//                //结算时把回滚订单数清零
//                matchSettleService.settleRollBackSetNullOrderCount(matchSettleEvent.getId());
//                //只有一次结算会走这里
//                matchSettleEvent.setSettleTimes(1);
//               matchSettleCheckService.settlePenaltyTeamFirst(matchSettleEvent);
//                //TODO  日志录入
//                iMatchSettleLogService.matchSettleEventAddLog(matchSettleEvent,matchSettleEventDto.getOperatorName(),
//                        OperateLogTypeEnum.SCORES_SETTLE_10041.getCode().toString(),before,matchSettleEventDto.getIpAddress());
//
//
//                wsPushService.pushStandardSettleScores(matchSettleEvent.getStandardMatchId(),"goal");
//                wsPushService.pushSettleMatchList(new MatchListSettleDto(matchSettleEvent.getStandardMatchId()
//                        , "goal",null,matchSettleEvent.getId(),2));
//                return Response.success();
//            }else {
//                return Response.failed("1031933");
//            }
//        }catch (Exception e){
//            log.error("FootballNewMatchScoresSettleApiImpl-settlePenaltyTeamFirst:",e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//
//    @Override
//    public Response setPenaltyTeamFirstHighL(EditMatchSettleEventDto matchSettleScoreDto) {
//        log.info("setPenaltyTeamFirstHighL New param,matchSettleScoreDto: {}",matchSettleScoreDto);
//        String key ="MATCH_SETTLE_INFO:"+ matchSettleScoreDto.getEventId();
//        //1.查询是否已经存在人工核对事件
//        try {
//            //3.更新或者新增
//            if(redisService.tryLock(key,key,2,5)) {
//                MatchSettleEvent matchSettleEvent =matchSettleEventMapper.selectByPrimaryKey(matchSettleScoreDto.getEventId());
//                MatchSettleEvent eventOid = new MatchSettleEvent();
//                BeanUtils.copyProperties(matchSettleEvent,eventOid);
//                if(matchSettleEvent==null){
//                    return Response.failed("1031935");
//                }
//                if(StringUtils.isEmpty(matchSettleScoreDto.getHomeAway())){
//                    return Response.failed("1031939");
//                }
//                String  before= "-";
//                Integer settleReason = matchSettleEvent.getSettleReason();
//                if (settleReason != null &&  settleReason != 0 ) {
//                    before = settleReason.toString();
//                    if (settleReason == 118) {
//                        before += ": "+matchSettleEvent.getSettleReasonDetail();
//                    }
//                }
//
//
//                matchSettleEvent.setStatus(1);
//                matchSettleEvent.setHomeAway(matchSettleScoreDto.getHomeAway());
//                matchSettleEvent.setModifyTime(System.currentTimeMillis());
//                matchSettleEventMapper.updateByPrimaryKey(matchSettleEvent);
//
//
//                //  日志录入
//                iMatchSettleLogService.matchSettleEventAddLog(eventOid,matchSettleEvent,matchSettleScoreDto.getOperatorName(),
//                        OperateLogTypeEnum.SCORES_SETTLE_10042,matchSettleScoreDto.getIpAddress());
//
//
//                return Response.success();
//            }else {
//                return Response.failed("1031933");
//            }
//        }catch (Exception e){
//            log.error("FootballNewMatchScoresSettleApiImpl-setPenaltyTeamFirstHighL:",e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//
//    @Override
//    public Response cancelDeleteStatus(MatchSettleSwitcherDto matchSettleSwitcherDto) {
//        log.info("cancelDeleteStatus New param,matchSettleSwitcherDto: {}",matchSettleSwitcherDto);
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
//                        MatchListSettleDto matchListSettleDto =new MatchListSettleDto();
//                        matchListSettleDto.setStandardMatchId(matchSettleSwitcherDto.getMatchId());
//                        matchListSettleDto.setEventCode(settleScore.getEventCode());
//                        wsPushService.pushSettleMatchList(matchListSettleDto);
//                        iMatchSettleLogService.deleteSettleAlertLog(settleScore,matchSettleSwitcherDto);
//                    }else {
//                        MatchSettleEvent settleEvent = matchSettleEventMapper.selectByPrimaryKey(matchSettleSwitcherDto.getMatchScoreId());
//                        if(settleEvent!=null){
//                            settleEvent.setHasDeleteEvent(0);
//                            settleEvent.setCurrentEventStatus(settleEvent.getIsGrey());
//                            matchSettleEventMapper.updateByPrimaryKey(settleEvent);
//                            MatchListSettleDto matchListSettleDto =new MatchListSettleDto();
//                            matchListSettleDto.setStandardMatchId(matchSettleSwitcherDto.getMatchId());
//                            matchListSettleDto.setEventCode(settleEvent.getEventCode());
//                            wsPushService.pushSettleMatchList(matchListSettleDto);
//                            iMatchSettleLogService.deleteSettleAlertLog(settleEvent,matchSettleSwitcherDto);
//                        }
//                    }
//                }
//                matchSettleCheckService.updateMatchCurrentEventStatus(matchSettleSwitcherDto.getMatchId());
//                return Response.success();
//            }else {
//                return Response.failed("1031933");
//            }
//        }catch (Exception e){
//            log.error("FootballNewMatchScoresSettleApiImpl-cancelDeleteStatus:",e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//
//    @Override
//    public Response getPlayerCancelDeleteStatus(Long standardMatchId) {
//        log.info("getPlayerCancelDeleteStatus New param,standardMatchId: {}",standardMatchId);
//        List<String> eventCodeFa= new ArrayList<>();
//        eventCodeFa.add("fa_card"); eventCodeFa.add("yellow_card"); eventCodeFa.add("red_card");
//        PlayerDeleteStatus playerDeleteStatus =new PlayerDeleteStatus();
//        playerDeleteStatus.setStandardMatchId(standardMatchId);
//        //进球查询
//        int deleteGoal=0;
//        int grayGoal=0;
//        MatchSettleEventExample goalEvent=new MatchSettleEventExample();
//        goalEvent.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andEventCodeEqualTo("goal").andEventTypeEqualTo(1).andStatusNotEqualTo(SETTLED);
//        List<MatchSettleEvent> goalEventList =matchSettleEventMapper.selectByExample(goalEvent);
//        for (MatchSettleEvent matchSettleEvent : goalEventList) {
//            if(matchSettleEvent.getIsGrey()!=null&&matchSettleEvent.getIsGrey()==1){
//                grayGoal=1;
//            }
//            if(matchSettleEvent.getHasDeleteEvent()!=null&&matchSettleEvent.getHasDeleteEvent()==1){
//                deleteGoal=1;
//            }
//        }
//        MatchSettleScoreExample goalScoreExa=new MatchSettleScoreExample();
//        goalScoreExa.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andEventCodeEqualTo("goal").andStatusNotEqualTo(SETTLED);
//        List< MatchSettleScore> goalScoreList = matchSettleScoreMapper.selectByExample(goalScoreExa);
//        for (MatchSettleScore matchSettleScore : goalScoreList) {
//            if(matchSettleScore.getIsGrey()!=null&&matchSettleScore.getIsGrey()==1){
//                grayGoal=1;
//            }
//            if(matchSettleScore.getHasDeleteEvent()!=null&&matchSettleScore.getHasDeleteEvent()==1){
//                deleteGoal=1;
//            }
//        }
//        if(deleteGoal!=0){
//            //删除是2
//            playerDeleteStatus.setGoalCurrentEventStatus(2);
//        }else if(grayGoal!=0){
//            playerDeleteStatus.setGoalCurrentEventStatus(1);
//        }else {
//            playerDeleteStatus.setGoalCurrentEventStatus(0);
//        }
//        //角球查询
//
//        int grayCorner=0;
//        int deleteCorner=0;
//        MatchSettleEventExample cornerEvent=new MatchSettleEventExample();
//        cornerEvent.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andEventCodeEqualTo("corner").andEventTypeEqualTo(1).andStatusNotEqualTo(SETTLED);
//        List<MatchSettleEvent> cornerEventList =matchSettleEventMapper.selectByExample(cornerEvent);
//        for (MatchSettleEvent matchSettleEvent : cornerEventList) {
//            if(matchSettleEvent.getIsGrey()!=null&&matchSettleEvent.getIsGrey()==1){
//                grayCorner=1;
//            }
//            if(matchSettleEvent.getHasDeleteEvent()!=null&&matchSettleEvent.getHasDeleteEvent()==1){
//                deleteCorner=1;
//            }
//        }
//
//        MatchSettleScoreExample cornerScoreExa=new MatchSettleScoreExample();
//        cornerScoreExa.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andEventCodeEqualTo("corner").andStatusNotEqualTo(SETTLED);
//        List< MatchSettleScore> cornerScoreList = matchSettleScoreMapper.selectByExample(cornerScoreExa);
//
//        for (MatchSettleScore matchSettleScore : cornerScoreList) {
//            if(matchSettleScore.getIsGrey()!=null&&matchSettleScore.getIsGrey()==1){
//                grayCorner=1;
//            }
//            if(matchSettleScore.getHasDeleteEvent()!=null&&matchSettleScore.getHasDeleteEvent()==1){
//                deleteCorner=1;
//            }
//        }
//
//        if(deleteCorner!=0){
//            //删除是2
//            playerDeleteStatus.setCornerCurrentEventStatus(2);
//        }else if(grayCorner!=0){
//            playerDeleteStatus.setCornerCurrentEventStatus(1);
//        }else {
//            playerDeleteStatus.setCornerCurrentEventStatus(0);
//        }
//        //罚牌查询
//        int grayFa=0;
//        int deleteFa=0;
//        MatchSettleEventExample faEvent=new MatchSettleEventExample();
//        faEvent.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andEventCodeIn(eventCodeFa).andEventTypeEqualTo(1).andStatusNotEqualTo(SETTLED);
//        List<MatchSettleEvent> faEventList =matchSettleEventMapper.selectByExample(faEvent);
//        for (MatchSettleEvent matchSettleEvent : faEventList) {
//            if(matchSettleEvent.getIsGrey()!=null&&matchSettleEvent.getIsGrey()==1){
//                grayFa=1;
//            }
//            if(matchSettleEvent.getHasDeleteEvent()!=null&&matchSettleEvent.getHasDeleteEvent()==1){
//                deleteFa=1;
//            }
//        }
//        MatchSettleScoreExample faScoreExa=new MatchSettleScoreExample();
//        faScoreExa.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andEventCodeIn(eventCodeFa).andStatusNotEqualTo(SETTLED);
//        List< MatchSettleScore> faScoreList = matchSettleScoreMapper.selectByExample(faScoreExa);
//
//        for (MatchSettleScore matchSettleScore : faScoreList) {
//            if(matchSettleScore.getIsGrey()!=null&&matchSettleScore.getIsGrey()==1){
//                grayFa=1;
//            }
//            if(matchSettleScore.getHasDeleteEvent()!=null&&matchSettleScore.getHasDeleteEvent()==1){
//                deleteFa=1;
//            }
//        }
//        if(deleteFa!=0){
//            //删除是2
//            playerDeleteStatus.setFacardCurrentEventStatus(2);
//        }else if(grayFa!=0){
//            playerDeleteStatus.setFacardCurrentEventStatus(1);
//        }else {
//            playerDeleteStatus.setFacardCurrentEventStatus(0);
//        }
//        return Response.success(playerDeleteStatus) ;
//    }
//
//    @Override
//    public Response<AbstractMentionQueryDto> getSettleEventMentionStatus(MentionQueryRequest mentionQueryRequest) {
//        try {
//            if(mentionQueryRequest.getMentionType() == 0) {
//                Map<String, AbstractMentionStatus> mentionStatusMap = matchSettleService.getAllMentionStatus(mentionQueryRequest);
//                if (mentionQueryRequest.getSportId() == 1L) {
//                    MentionQueryDto mentionQueryDto = new MentionQueryDto();
//                    if (!MapUtils.isEmpty(mentionStatusMap)) {
//                        for (String v : mentionStatusMap.keySet()) {
//                            MentionQueryDto.FootballMentionStatus subMentionStatus = settleMentionConverter.convertFootballMentionStatus((FootballMentionStatus)mentionStatusMap.get(v));
//                            switch (v){
//                                case "deleteStatus":
//                                    mentionQueryDto.setDeleteStatus(subMentionStatus);
//                                    break;
//                                case "dataMismatchStatus":
//                                    mentionQueryDto.setDataMismatchStatus(subMentionStatus);
//                                    break;
//                            }
//                        }
//                        if (mentionQueryRequest.getMentionDetail() == 0) {
//                            mentionQueryDto.setDetailNull();
//                        }
//                    }
//                    return Response.success(mentionQueryDto);
//                } else {
//                    BasketballMentionQueryDto response = new BasketballMentionQueryDto();
//                    if (!MapUtils.isEmpty(mentionStatusMap)) {
//                        for (String v : mentionStatusMap.keySet()) {
//                            BasketballMentionQueryDto.BasketballMentionStatus mentionStatus = settleMentionConverter.convertBasketballMentionStatus((BasketballMentionStatus)mentionStatusMap.get(v));
//                            switch (v){
//                                case "dataMismatchStatus":
//                                    response.setDataMismatchStatus(mentionStatus);
//                                    break;
//                            }
//                        }
//                        if (mentionQueryRequest.getMentionDetail() == 0) {
//                            response.setDetailNull();
//                        }
//                    }
//                    return Response.success(response);
//                }
//            } else {
//                AbstractMentionStatus mentionDto = matchSettleService.getFootballMentionStatus(mentionQueryRequest);
//                if(mentionQueryRequest.getSportId() == 1L) {
//                    MentionQueryDto response= new MentionQueryDto();
//                    FootballMentionStatus footballMentionStatus = (FootballMentionStatus) mentionDto;
//                    MentionQueryDto.FootballMentionStatus subMentionStatus = settleMentionConverter.convertFootballMentionStatus(footballMentionStatus);
//                    if (mentionQueryRequest.getMentionType() == 1) {
//                        response.setDeleteStatus(subMentionStatus);
//                    } else if (mentionQueryRequest.getMentionType() == 2) {
//                        response.setDataMismatchStatus(subMentionStatus);
//                    }
//                    return Response.success(response);
//                } else {
//                    BasketballMentionQueryDto response= new BasketballMentionQueryDto();
//                    BasketballMentionStatus mentionStatus = (BasketballMentionStatus) mentionDto;
//                    BasketballMentionQueryDto.BasketballMentionStatus subMentionStatus = settleMentionConverter.convertBasketballMentionStatus(mentionStatus);
//                    if (mentionQueryRequest.getMentionType() == 2) {
//                        response.setDataMismatchStatus(subMentionStatus);
//                    }
//                    return Response.success(response);
//                }
//            }
//        } catch (Exception e) {
//            log.error("[FootballNewMatchScoresSettleApiImpl] getSettleEventMentionStatus error: ", e);
//            return Response.failed(e.getMessage());
//        }
//    }
//
//    @Override
//    public Response<Map<Long, AbstractMentionQueryDto>> getSettleEventMentionStatus(List<Long> matchIds, Long sportId) {
//        if(CollectionUtils.isEmpty(matchIds)) {
//            log.info("getSettleEventMentionStatus matchIds: {}", matchIds);
//            return Response.success();
//        }
//        Map<Long, AbstractMentionQueryDto> res = new HashMap<>();
//        MentionQueryRequest request = new MentionQueryRequest();
//        request.setMentionType(0);
//        request.setMentionDetail(0);
//        request.setSportId(sportId);
//        for (Long matchId : matchIds) {
//            request.setMatchId(matchId);
//            Response<AbstractMentionQueryDto> response = getSettleEventMentionStatus(request);
//            res.put(matchId, response.getData());
//        }
//        return Response.success(res);
//    }
//
//    @Override
//    public Response<String> cancelSettleEventMention(SettleEventDeleteRequest settleEventDeleteRequest) {
//        try {
//            log.info("[FootballNewMatchScoresSettleApiImpl] cancelSettleEventMention param with {}", settleEventDeleteRequest);
//            matchSettleService.cancelSettleEventMention(settleEventDeleteRequest);
//            return Response.success("操作成功");
//        } catch (Exception e) {
//            log.error("[FootballNewMatchScoresSettleApiImpl] cancelSettleEventMention error: ", e);
//            return Response.failed(e.getMessage());
//        }
//
//    }
//}
