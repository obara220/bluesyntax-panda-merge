//package com.panda.merge.dubbo;
//
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.panda.merge.api.IBasketballNewMatchScoresSettleApi;
//import com.panda.merge.api.IFootballMatchScoresSettleApi;
//import com.panda.merge.check.IMatchSettleCheckService;
//import com.panda.merge.common.enums.OperateLogTypeEnum;
//import com.panda.merge.config.RedisService;
//import com.panda.merge.constant.MatchSettleCheckConstant;
//import com.panda.merge.constant.MatchSettleScoreConstant;
//import com.panda.merge.dto.*;
//import com.panda.merge.dto.advertise.MatchFreezeDto;
//import com.panda.merge.dto.message.MatchFreezeMessage;
//import com.panda.merge.dto.settle.*;
//import com.panda.merge.mapper.IMatchSettleScoreEventMapper;
//import com.panda.merge.mapper.MatchSettleCheckInfoMapper;
//import com.panda.merge.mapper.MatchSettleEventMapper;
//import com.panda.merge.mapper.MatchSettleInfoMapper;
//import com.panda.merge.mapper.MatchSettleScoreMapper;
//import com.panda.merge.mapper.StandardMatchInfoMapper;
//import com.panda.merge.model.*;
//import com.panda.merge.mq.producer.MatchSettleCenterProducer;
//import com.panda.merge.mq.producer.MatchSettleScoresProducer;
//import com.panda.merge.respository.MatchSettleInfoRepository;
//import com.panda.merge.respository.StandardMatchInfoRepository;
//import com.panda.merge.service.IMatchSettleLogService;
//import com.panda.merge.service.IMatchSettleService;
//import com.panda.merge.service.IWsPushService;
//import com.panda.merge.service.StandardMatchInfoService;
//import com.panda.merge.utils.SettleCheckUtils;
//import io.netty.util.internal.StringUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.tuple.Pair;
//import org.apache.dubbo.config.annotation.DubboService;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//
//@Service
//@DubboService
//@Slf4j
//public class BasketballNewMatchScoresSettleApiImpl implements IBasketballNewMatchScoresSettleApi {
//
//    @Autowired
//    MatchSettleCheckInfoMapper matchSettleCheckInfoMapper;
//
//    @Autowired
//    IMatchSettleService matchSettleService;
//
//    @Autowired
//    IWsPushService wsPushService;
//
//    @Autowired
//    RedisService redisService;
//
//    @Autowired
//    IMatchSettleCheckService matchSettleCheckService;
//
//    @Autowired
//    MatchSettleEventMapper matchSettleEventMapper;
//
//    @Autowired
//    IFootballMatchScoresSettleApi footballMatchScoresSettleApi;
//
//    @Autowired
//    MatchSettleScoreMapper matchSettleScoreMapper;
//
//    @Autowired
//    MatchSettleInfoMapper matchSettleInfoMapper;
//
//    @Autowired
//    IMatchSettleLogService iMatchSettleLogService;
//
//    @Autowired
//    MatchSettleScoresProducer matchSettleScoresProducer;
//
//    @Autowired
//    IMatchSettleScoreEventMapper matchSettleScoreEventMapper;
//
//    @Autowired
//    MatchSettleCenterProducer matchSettleCenterProducer;
//
//    @Autowired
//    StandardMatchInfoMapper standardMatchInfoMapper;
//    @Autowired
//    MatchSettleInfoRepository matchSettleInfoRepository;
//    @Autowired
//    StandardMatchInfoService standardMatchInfoService;
//
//
//    @Override
//    public Response editMatchSettleScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto) {
//        log.info("basketball new editMatchSettleScore,matchSettleScoreDto: {}",matchSettleScoreDto);
//        //赛事id
//        Long standardMatchId = matchSettleScoreDto.getStandardMatchId();
//        //赛事比分id
//        Long matchScoreId = matchSettleScoreDto.getMatchScoreId();
//        //审核员姓名
//        String userName = matchSettleScoreDto.getOperatorName();
//
//        //1.判断是否已超过结算时间
//        if (matchSettleService.checkIfOverSettleTime(standardMatchId)) {
//            return Response.failed("1031930");
//        }
//        if(redisService.hasKey("SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getMatchScoreId())){
//            return Response.failed("1031960");
//        }
//        String key = "NEW_MATCH_SETTLE_INFO:" + matchScoreId + "_" + userName;
//        try {
//            if (redisService.tryLock(key, key, 2, 5)) {
//                MatchSettleCheckInfo matchSettleCheckInfo = matchSettleCheckService.searchCheckInfoByUser(matchSettleScoreDto.getMatchScoreId(), matchSettleScoreDto.getStandardMatchId(), matchSettleScoreDto.getOperatorName());
//
//                if (matchSettleCheckInfo != null && matchSettleCheckInfo.getCheckStatus() == MatchSettleCheckConstant.CheckStatus.CONFIRM) {
//                    //还没编辑 该用户已经确认比分
//                    return Response.failed("1031933");
//                }
//
//                MatchSettleScore matchSettleScore = matchSettleScoreMapper.selectByPrimaryKey(matchSettleScoreDto.getMatchScoreId());
//                Integer checkNumber = matchSettleScore.getCheckNumber();
//                if (matchSettleScore == null) {
//                    return Response.failed("1031935");
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
//                matchSettleScore.setOperater(matchSettleScoreDto.getOperatorName());
//                matchSettleScore.setT2(matchSettleScoreDto.getT2());
//                matchSettleScore.setT1(matchSettleScoreDto.getT1());
//                matchSettleScore.setGoWaterStatus(matchSettleScoreDto.getGoWaterStatus());
//                MatchSettleCheckInfo checkInfo = new MatchSettleCheckInfo();
//
//                //3.1无比分核对记录则初始化该用户的核对记录
//                if (matchSettleCheckInfo == null) {
//                    //得到当前用户的次序
//                    matchSettleCheckInfo = new MatchSettleCheckInfo();
//                    BeanUtils.copyProperties(matchSettleCheckInfo, checkInfo);
//                    matchSettleCheckInfo = SettleCheckUtils.initManualMatchSettleScores(matchSettleScore);
//                    SettleCheckUtils.copyManualMatchSettleScore(matchSettleScoreDto, matchSettleCheckInfo);
//                    matchSettleCheckInfo.setCheckNumber(SettleCheckUtils.getCheckNumber(matchSettleScore.getCheckNumber()));
//                    matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
//                    matchSettleCheckInfo.setDataSourceCode("PA");
//                    matchSettleCheckInfo.setExtryInfo(matchSettleScore.getExtryInfo());
//                    matchSettleCheckInfoMapper.insert(matchSettleCheckInfo);
//                    log.info("::{}:: 插入比分id为{}，审核员{}比分核对数据:{}", standardMatchId, matchScoreId, userName, matchSettleScoreDto);
//                } else {
//                    //3.2有比分核对记录则更新
//                    BeanUtils.copyProperties(matchSettleCheckInfo, checkInfo);
//                    SettleCheckUtils.copyManualMatchSettleScore(matchSettleScoreDto, matchSettleCheckInfo);
//                    matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
//                    matchSettleCheckInfo.setExtryInfo(matchSettleScore.getExtryInfo());
//                    matchSettleCheckInfoMapper.updateByPrimaryKey(matchSettleCheckInfo);
//                    log.info("::{}:: 更新比分id为{}，审核员{}比分核对数据:{}", standardMatchId, matchScoreId, userName, matchSettleScoreDto);
//                }
//
//                String settleScoreJson = JSON.toJSONString(matchSettleScoreDto);
//                UpdateMatchSettleScoreDto updateMatchSettleScoreDto = JSON.parseObject(settleScoreJson,UpdateMatchSettleScoreDto.class);
//                updateMatchSettleScoreDto.setBasketBallSettleNum(matchSettleScoreDto.getSettleNum());
//                iMatchSettleLogService.matchSettleCheckScoreAddLog(checkInfo,matchSettleCheckInfo,
//                        updateMatchSettleScoreDto,OperateLogTypeEnum.EDIT,matchSettleScore.getSettleNum(), checkNumber);
//            } else {
//                return Response.failed("1031933");
//            }
//        } catch (Exception e) {
//            log.error("BasketballNew-editMatchSettleScore", e);
//            return Response.failed();
//        } finally {
//            redisService.unLock(key, key);
//        }
//        return Response.success();
//    }
//
//    @Override
//    public Response confirmMatchSettleScore(ConfirmMatchSettleScoreDto matchSettleScoreDto) {
//        log.info("basketball new confirmMatchSettleScore,matchSettleScoreDto: {}",matchSettleScoreDto);
//        //赛事id
//        Long standardMatchId = matchSettleScoreDto.getStandardMatchId();
//        //赛事比分id
//        Long matchScoreId = matchSettleScoreDto.getMatchScoreId();
//        //审核员姓名
//        String userName = matchSettleScoreDto.getOperatorName();
//
//        //1.判断是否已超过结算时间X
//        if (matchSettleService.checkIfOverSettleTime(standardMatchId)) {
//            return Response.failed("1031930");
//        }
//        if(redisService.hasKey("SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getMatchScoreId())){
//            return Response.failed("1031960");
//        }
//        String key = "NEW_MATCH_SETTLE_INFO:" + matchScoreId + "_" + userName;
//        try {
//            if (redisService.tryLock(key, key, 2, 5)) {
//                //2.查询人工核对比分
//                MatchSettleCheckInfo matchSettleCheckInfo = matchSettleCheckService.searchCheckInfoByUser(matchSettleScoreDto.getMatchScoreId(), matchSettleScoreDto.getStandardMatchId(), matchSettleScoreDto.getOperatorName());
//                MatchSettleCheckInfo checkInfo = new MatchSettleCheckInfo();
//                BeanUtils.copyProperties(matchSettleCheckInfo, checkInfo);
//
//                MatchSettleScore matchSettleScore = matchSettleScoreMapper.selectByPrimaryKey(matchSettleScoreDto.getMatchScoreId());
//                Integer checkNumber = matchSettleScore.getCheckNumber();
//                if (matchSettleScore == null) {
//                    return Response.failed("1031935");
//                }
//                if (matchSettleCheckInfo == null) {
//                    return Response.failed("1031934");
//                }
//                if (matchSettleCheckInfo.getCheckStatus() != MatchSettleCheckConstant.CheckStatus.EDIT) {
//                    log.error("人工核对确认比分状态错误:{}", matchSettleCheckInfo);
//                    return Response.failed("1031934");
//                }
//
//                UpdateMatchSettleScoreDto dto = new UpdateMatchSettleScoreDto();
//                BeanUtils.copyProperties(matchSettleScoreDto, dto);
//                if (!StringUtil.isNullOrEmpty(matchSettleScore.getSettleNum())) {
//                    dto.setSettleNum(matchSettleScore.getSettleNum());
//                }
//                dto.setSportId(matchSettleScore.getSportId());
//                iMatchSettleLogService.matchSettleCheckScoreAddLog(checkInfo, matchSettleCheckInfo, dto, OperateLogTypeEnum.CONFIRM_SCORE, matchSettleScore.getSettleNum(), checkNumber);
//
//                //3.更新状态
//                matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.CONFIRM);
//                matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
//                matchSettleCheckInfoMapper.updateByPrimaryKey(matchSettleCheckInfo);
//                //进入统一核对比分流程
//                Pair<Boolean, Boolean> isScoreOrEventDiff = matchSettleCheckService.checkCommonMatchSettleScoreEvent(matchSettleScore, matchSettleCheckInfo, false);
//
//                if (isScoreOrEventDiff.getLeft()) {
//                    //1031947=比分不一致需要下个审核员审核
//                    return Response.success("1031947");
//                }
//            } else {
//                return Response.failed("1031933");
//            }
//        } catch (Exception e) {
//            log.error("BasketballNew-confirmMatchSettleScore:", e);
//            return Response.failed();
//        } finally {
//            redisService.unLock(key, key);
//        }
//        return Response.success();
//
//    }
//
//    @Override
//    public Response matchReplayAndFreeze(SettleQueryDTO settleQueryDTO) {
//        log.info("basketball new matchReplayAndFreeze,settleQueryDTO: {}",settleQueryDTO);
//        Response response = null;
//        try {
//            if (settleQueryDTO.getLevel().equals(1)) {
//                switch (settleQueryDTO.getExInfo()) {
//                    case 0:
//                    case 1://赛事级的冻结、解冻
//                        response = basketBallMatchAndPlayFreeze(settleQueryDTO);
//                        break;
//                    case 2: //赛事级的重跑
//                        response = basketBallMatchReSettle(settleQueryDTO);
//                        break;
//                    default:
//                        break;
//                }
//            }
//        } catch (Exception e) {
//            log.error("BasketballNew-matchReplayAndFreeze:", e);
//            return Response.failed();
//        }
//        return response;
//    }
//
//    @Override
//    public Response playReplayAndFreeze(SettleQueryDTO settleQueryDTO) {
//        log.info("basketball new playReplayAndFreeze,settleQueryDTO: {}",settleQueryDTO);
//        //玩法级重跑、冻结
//        Response response = null;
//        try {
//            if (settleQueryDTO.getLevel().equals(2)) {
//                switch (settleQueryDTO.getExInfo()) {
//                    case 0:
//                    case 1://玩法级的冻结、解冻
//                        response = basketBallMatchAndPlayFreeze(settleQueryDTO);
//                        break;
//                    case 2: //玩法级的重跑
//                        response = basketBallPlayReSettle(settleQueryDTO);
//                        break;
//                    default:
//                        break;
//                }
//            }
//        } catch (Exception e) {
//            log.error("BasketballNew-playReplayAndFreeze:", e);
//            return Response.failed();
//        }
//        return response;
//    }
//
//
//
//
//    /**
//     * 篮球赛事级、玩法级的冻结、解冻
//     *
//     * @param settleQueryDTO
//     * @return
//     */
//    public Response basketBallMatchAndPlayFreeze(SettleQueryDTO settleQueryDTO) {
//        log.info("basketball new basketBallMatchAndPlayFreeze,settleQueryDTO: {}",settleQueryDTO);
//        //查询比分
//        try {
//            List<String> settleNumList = new LinkedList<>();
//            MatchSettleScoreExample matchSettleScoreExample = new MatchSettleScoreExample();
//            if (settleQueryDTO.getLevel().equals(2) && (settleQueryDTO.getExInfo() == 0 || settleQueryDTO.getExInfo() == 1)) {
//                settleNumList = MatchSettleCheckConstant.getBasketBallSettleNumEnumList(settleQueryDTO);
//                if (settleNumList.isEmpty()) {
//                    return Response.failed();
//                }
//                matchSettleScoreExample.createCriteria().andStandardMatchIdEqualTo(settleQueryDTO.getMatchId()).andSettleNumIn(settleNumList);
//            } else {
//                matchSettleScoreExample.createCriteria().andStandardMatchIdEqualTo(settleQueryDTO.getMatchId());
//            }
//
//            matchSettleScoreExample.setOrderByClause("settle_num desc");
//            List<MatchSettleScore> settleScoreList = matchSettleScoreMapper.selectByExample(matchSettleScoreExample);
//            if (!settleScoreList.isEmpty()) {
//                List<MatchSettleScore> listSettleScore = new ArrayList<>();
//                for (MatchSettleScore dto : settleScoreList) {
//                    MatchSettleScore matchSettleScore = new MatchSettleScore();
//                    BeanUtils.copyProperties(dto, matchSettleScore);
//                    matchSettleScore.setModifyTime(System.currentTimeMillis());
//                    matchSettleScore.setId(dto.getId());
//                    matchSettleScore.setSettleFreeze(settleQueryDTO.getExInfo());
//                    listSettleScore.add(matchSettleScore);
//                }
//                //更新比分解冻、冻结
//                matchSettleScoreEventMapper.updateScoreByList(listSettleScore);
//            }
//
//            //查询事件
//            MatchSettleEventExample matchSettleEventExample = new MatchSettleEventExample();
//            if (settleQueryDTO.getLevel().equals(2) && (settleQueryDTO.getExInfo() == 0 || settleQueryDTO.getExInfo() == 1) && !settleNumList.isEmpty()) {
//                matchSettleEventExample.createCriteria().andStandardMatchIdEqualTo(settleQueryDTO.getMatchId()).andSettleNumIn(settleNumList);
//            } else {
//                matchSettleEventExample.createCriteria().andStandardMatchIdEqualTo(settleQueryDTO.getMatchId());
//            }
//            matchSettleEventExample.setOrderByClause("settle_num desc,event_order desc");
//            List<MatchSettleEvent> settleEventList = matchSettleEventMapper.selectByExample(matchSettleEventExample);
//            if (!settleEventList.isEmpty()) {
//                List<MatchSettleEvent> listEvent = new ArrayList<>();
//                for (MatchSettleEvent dto : settleEventList) {
//                    MatchSettleEvent matchSettleEvent = new MatchSettleEvent();
//                    BeanUtils.copyProperties(dto, matchSettleEvent);
//                    matchSettleEvent.setModifyTime(System.currentTimeMillis());
//                    matchSettleEvent.setId(dto.getId());
//                    matchSettleEvent.setSettleFreeze(settleQueryDTO.getExInfo());
//                    listEvent.add(matchSettleEvent);
//                }
//                //更新事件解冻、冻结
//                matchSettleScoreEventMapper.updateEventByList(listEvent);
//            }
//
//            String freezeText = null;
//            MatchFreezeMessage matchFreezeMessage = new MatchFreezeMessage();
//            matchFreezeMessage.setMatchId(settleQueryDTO.getMatchId());
//            if (settleQueryDTO.getLevel().equals(2) && (settleQueryDTO.getExInfo() == 0 || settleQueryDTO.getExInfo() == 1)) {
//                freezeText = "玩法级冻结/解冻";
//                matchFreezeMessage.setLevel(settleQueryDTO.getLevelNum());
//                if (settleQueryDTO.getLevelNum() == 3) {
//                    matchFreezeMessage.setSettleNum(settleNumList.get(0));
//                }
//                if (settleQueryDTO.getPlayCategoryNum() != null) {
//                    matchFreezeMessage.setPlayCategory(settleQueryDTO.getPlayCategoryNum());
//                }
//            } else {
//                freezeText = "赛事级冻结/解冻";
//                matchFreezeMessage.setLevel(settleQueryDTO.getLevel());
//            }
//            matchFreezeMessage.setSportId(settleQueryDTO.getSportId());
//            matchFreezeMessage.setFreezeSettleStatus(settleQueryDTO.getExInfo());
//            matchSettleCenterProducer.MatchFreeze(matchFreezeMessage, freezeText);
//
//            //7.日志
//            String forwText = "-";
//            if (settleQueryDTO.getExInfo() == 0) {
//                forwText = OperateLogTypeEnum.type_1.getCode().toString();
//            }
//            if (settleQueryDTO.getExInfo() == 1) {
//                forwText = OperateLogTypeEnum.type_2.getCode().toString();
//            }
//            if (settleQueryDTO.getLevel().equals(2) && (settleQueryDTO.getExInfo() == 0 || settleQueryDTO.getExInfo() == 1)) {
//
//                // 玩法级的冻结、解冻
//                MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(settleQueryDTO.getMatchId());
//                if (matchSettleInfo != null && !StringUtils.isAnyEmpty(matchSettleInfo.getCategoryFreezeStatus())) {
//
//                    CategoryBasketballDto categoryBasketballDto = JSONObject.parseObject(matchSettleInfo.getCategoryFreezeStatus(), CategoryBasketballDto.class);
//                    switch (settleQueryDTO.getSettleNum()) {
//                        case "100":
//                            categoryBasketballDto.setBkQ104(settleQueryDTO.getExInfo());
//                            break;
//                        case "200":
//                            categoryBasketballDto.setBkQ204(settleQueryDTO.getExInfo());
//                            break;
//                        case "300":
//                            categoryBasketballDto.setBkQ304(settleQueryDTO.getExInfo());
//                            break;
//                        case "400":
//                            categoryBasketballDto.setBkQ404(settleQueryDTO.getExInfo());
//                            break;
//                        case "end":
//                            categoryBasketballDto.setEnd(settleQueryDTO.getExInfo());
//                            break;
//                        case "s001":
//                            categoryBasketballDto.setFirstNPoint(settleQueryDTO.getExInfo());
//                            break;
//                        case "1ht":
//                            categoryBasketballDto.setBk1ht(settleQueryDTO.getExInfo());
//                            break;
//                        case "2ht":
//                            categoryBasketballDto.setBk2ht(settleQueryDTO.getExInfo());
//                            break;
//                        case "et":
//                            categoryBasketballDto.setBkEt(settleQueryDTO.getExInfo());
//                            break;
//                        case "rg":
//                            categoryBasketballDto.setBkFtRg(settleQueryDTO.getExInfo());
//                            break;
//                        case "point":
//                            categoryBasketballDto.setPoint(settleQueryDTO.getExInfo());
//                            break;
//                        case "3pt":
//                            categoryBasketballDto.setBk3pt(settleQueryDTO.getExInfo());
//                            break;
//                        case "ast":
//                            categoryBasketballDto.setBkAst(settleQueryDTO.getExInfo());
//                            break;
//                        case "rbd":
//                            categoryBasketballDto.setBkRbd(settleQueryDTO.getExInfo());
//                            break;
//                    }
//                    matchSettleInfo.setCategoryFreezeStatus(JSON.toJSONString(categoryBasketballDto));
//                    matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
//                }
//                iMatchSettleLogService.categoryReSettleAddLog(settleQueryDTO, forwText);
//                StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(settleQueryDTO.getMatchId());
//                matchSettleCenterProducer.doSendLogToRiskByTypeBasketball(standardMatchInfo,settleQueryDTO,forwText);
//
//            } else {
//                //赛事级别的冻结、解冻
//                MatchFreezeDto matchFreezeDto = new MatchFreezeDto();
//                matchFreezeDto.setMatchId(settleQueryDTO.getMatchId());
//                matchFreezeDto.setIpAddress(settleQueryDTO.getIpAddress());
//                matchFreezeDto.setOperatorName(settleQueryDTO.getOperatorName());
//                matchFreezeDto.setSportId(2l);
//
//                MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(settleQueryDTO.getMatchId());
//                matchSettleInfo.setSportId(2L);
//                matchSettleInfo.setId(settleQueryDTO.getMatchId());
//                matchSettleInfo.setFreezeStatus(settleQueryDTO.getExInfo());
//                matchSettleInfo.setStandardMatchId(settleQueryDTO.getMatchId());
//                String categoryFreezeStatus = null;
//                if (settleQueryDTO.getExInfo().equals(0)) {
//                    categoryFreezeStatus = JSON.toJSONString(new CategoryBasketballDto().unFreeze());
//                } else {
//                    categoryFreezeStatus = JSON.toJSONString(new CategoryBasketballDto().builderFreeze());
//                }
//                matchSettleInfo.setCategoryFreezeStatus(categoryFreezeStatus);
//                matchSettleInfo.setModifyTime(System.currentTimeMillis());
//                matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
//                StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchFreezeDto.getMatchId());
//                iMatchSettleLogService.matchFreezeAddLog(standardMatchInfo,matchSettleInfo, forwText, matchFreezeDto);
//
//                MatchFreezeMessage freezeMessage = new MatchFreezeMessage();
//                BeanUtils.copyProperties(matchFreezeDto, freezeMessage);
//                freezeMessage.setLevel(1);
//                matchFreezeDto.setFreezeSettleStatus(settleQueryDTO.getExInfo());
//                matchSettleCenterProducer.doSendLogToRisk(standardMatchInfo,matchSettleInfo, freezeMessage, matchFreezeDto);
//            }
//        } catch (Exception e) {
//            log.error("BasketballNew-basketBallMatchAndPlayFreeze:", e);
//            return Response.failed();
//        }
//        return Response.success();
//    }
//
//    /**
//     * 篮球玩法级的重跑
//     *
//     * @param settleQueryDTO
//     * @return
//     */
//    private Response basketBallPlayReSettle(SettleQueryDTO settleQueryDTO) {
//        log.info("basketball new basketBallPlayReSettle,settleQueryDTO: {}",settleQueryDTO);
//        try {
//            List<String> settleNumList = MatchSettleCheckConstant.getBasketBallSettleNumEnumList(settleQueryDTO);
//            if (settleNumList.isEmpty()) {
//                return Response.failed();
//            }
//            MatchSettleScoreExample matchSettleScoreExample = new MatchSettleScoreExample();
//            matchSettleScoreExample.createCriteria().andStandardMatchIdEqualTo(settleQueryDTO.getMatchId()).andSportIdEqualTo(2L).andSettleNumIn(settleNumList);
//            List<MatchSettleScore> matchSettleScoreList = matchSettleScoreMapper.selectByExample(matchSettleScoreExample);
//            if (!matchSettleScoreList.isEmpty()) {
//                for (MatchSettleScore matchSettleScore : matchSettleScoreList) {
//                    matchSettleScore.setModifyTime(System.currentTimeMillis());
//                    matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
//                    matchSettleScore.setOperater(settleQueryDTO.getOperatorName());
//                    matchSettleScore.setUserid(settleQueryDTO.getOperatorId());
//                }
//                matchSettleScoreEventMapper.updateScoreByList(matchSettleScoreList);
//            }
//
//            //3.查询事件
//            MatchSettleEventExample matchSettleEventExample = new MatchSettleEventExample();
//            matchSettleEventExample.createCriteria().andStandardMatchIdEqualTo(settleQueryDTO.getMatchId()).andSportIdEqualTo(2L).andSettleNumIn(settleNumList);
//            matchSettleEventExample.setOrderByClause("settle_num desc,event_order desc");
//            List<MatchSettleEvent> eventList = matchSettleEventMapper.selectByExample(matchSettleEventExample);
//            if (!eventList.isEmpty()) {
//                //4.批量更新事件
//                for (MatchSettleEvent event : eventList) {
//                    event.setModifyTime(System.currentTimeMillis());
//                    event.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
//                    event.setOperater(settleQueryDTO.getOperatorName());
//                    event.setUserid(settleQueryDTO.getOperatorId());
//                }
//                matchSettleScoreEventMapper.updateEventByList(eventList);
//            }
//
//            //5.结算比分下发
//            MatchSettleScoreMessage matchSettleScore = new MatchSettleScoreMessage();
//            matchSettleScore.setOperateType(3);
//            matchSettleScore.setSportId(settleQueryDTO.getSportId());
//            matchSettleScore.setStandardMatchId(settleQueryDTO.getMatchId());
//            matchSettleScore.setLevel(settleQueryDTO.getLevelNum());
//            if (settleQueryDTO.getLevelNum() == 3) {
//                matchSettleScore.setSettleNum(settleNumList.get(0));
//            }
//            if (settleQueryDTO.getPlayCategoryNum() != null) {
//                matchSettleScore.setPlayCategory(settleQueryDTO.getPlayCategoryNum());
//            }
//            matchSettleScoresProducer.sendMatchSettleScores(matchSettleScore);
//
//            //6.结算事件下发
//            MatchSettleEventMessage matchSettleEvent = new MatchSettleEventMessage();
//            matchSettleEvent.setOperateType(3);
//            matchSettleEvent.setLevel(settleQueryDTO.getLevelNum());
//            matchSettleEvent.setSportId(settleQueryDTO.getSportId());
//            matchSettleEvent.setStandardMatchId(settleQueryDTO.getMatchId());
//            if (settleQueryDTO.getLevelNum() == 3) {
//                matchSettleScore.setSettleNum(settleNumList.get(0));
//            }
//            if (settleQueryDTO.getPlayCategoryNum() != null) {
//                matchSettleEvent.setPlayCategory(settleQueryDTO.getPlayCategoryNum());
//            }
//            matchSettleScoresProducer.sendMatchSettleEvent(matchSettleEvent);
//
//            //7.日志
//            iMatchSettleLogService.categoryReSettleAddLog(settleQueryDTO, "-");
//        } catch (Exception e) {
//            log.error("BasketballNew-basketBallPlayReSettle:", e);
//            return Response.failed();
//        }
//        return Response.success();
//    }
//
//    /**
//     * 篮球赛事级的重跑
//     * 查询已结算的比分、事件更新后，重新下发到下游，下游重新结算
//     *
//     * @param settleQueryDTO
//     * @return
//     */
//    private Response basketBallMatchReSettle(SettleQueryDTO settleQueryDTO) {
//        log.info("basketball new basketBallMatchReSettle,settleQueryDTO: {}",settleQueryDTO);
//        try {
//            MatchSettleScoreExample example = new MatchSettleScoreExample();
//            example.createCriteria().andStandardMatchIdEqualTo(settleQueryDTO.getMatchId()).andStatusEqualTo(MatchSettleScoreConstant.MatchSettleScoreStatus.CONFIRM);
//            example.setOrderByClause("settle_num desc");
//            List<MatchSettleScore> settleScoreList = matchSettleScoreMapper.selectByExample(example);
//            if (!settleScoreList.isEmpty()) {
//                //1.查询比分
//                for (MatchSettleScore matchSettleScore : settleScoreList) {
//                    matchSettleScore.setModifyTime(System.currentTimeMillis());
//                    matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
//                    matchSettleScore.setSettleTimes(matchSettleScore.getSettleTimes());
//                    matchSettleScore.setOperater(settleQueryDTO.getOperatorName());
//                    matchSettleScore.setUserid(settleQueryDTO.getOperatorId());
//                }
//                //2.更新比分
//                matchSettleScoreEventMapper.updateScoreByList(settleScoreList);
//            }
//
//            //3.查询事件
//            MatchSettleEventExample matchSettleEventExample = new MatchSettleEventExample();
//            matchSettleEventExample.createCriteria().andStandardMatchIdEqualTo(settleQueryDTO.getMatchId()).andStatusEqualTo(MatchSettleScoreConstant.MatchSettleScoreStatus.CONFIRM);
//            matchSettleEventExample.setOrderByClause("settle_num desc,event_order desc");
//            List<MatchSettleEvent> eventList = matchSettleEventMapper.selectByExample(matchSettleEventExample);
//            if (!eventList.isEmpty()) {
//                //4.批量更新事件
//                for (MatchSettleEvent event : eventList) {
//                    event.setModifyTime(System.currentTimeMillis());
//                    event.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
//                    event.setOperater(settleQueryDTO.getOperatorName());
//                    event.setUserid(settleQueryDTO.getOperatorId());
//                }
//                matchSettleScoreEventMapper.updateEventByList(eventList);
//            }
//
//            //5.结算比分下发
//            MatchSettleScoreMessage matchSettleScore = new MatchSettleScoreMessage();
//            matchSettleScore.setStandardMatchId(settleQueryDTO.getMatchId());
//            matchSettleScore.setSportId(settleQueryDTO.getSportId());
//            matchSettleScore.setLevel(settleQueryDTO.getLevel());
//            matchSettleScore.setSettleNum("0");
//            matchSettleScore.setOperateType(3);
//            matchSettleScoresProducer.sendMatchSettleScores(matchSettleScore);
//
//            //6.结算事件下发
//            MatchSettleEventMessage matchSettleEvent = new MatchSettleEventMessage();
//            matchSettleEvent.setStandardMatchId(settleQueryDTO.getMatchId());
//            matchSettleEvent.setSportId(settleQueryDTO.getSportId());
//            matchSettleEvent.setLevel(settleQueryDTO.getLevel());
//            matchSettleEvent.setSettleNum("0");
//            matchSettleEvent.setOperateType(3);
//            matchSettleScoresProducer.sendMatchSettleEvent(matchSettleEvent);
//
//            //7.日志
//            iMatchSettleLogService.matchReSettleAddLog(settleQueryDTO);
//        } catch (Exception e) {
//            log.error("BasketballNew-basketBallMatchReSettle:", e);
//            return Response.failed();
//        }
//        return Response.success();
//
//    }
//
//    @Override
//    public Response confirmBringInScore(BasketBallPutInJsonDto basketBallPutInJsonDto) {
//        String key ="StandardMatchScoreConsumer:"+basketBallPutInJsonDto.getStandardMatchId();
//
//        if (redisService.tryLock(key, key, 2, 5)) {
//            log.info("审核员confirmBringInScore入参: {}",basketBallPutInJsonDto);
//            Long standardMatchId = basketBallPutInJsonDto.getStandardMatchId();
//            //解析需要带入的比分
//            List<UpdateBasketBallSettleScoreDto> updateBasketBallSettleScores =new ArrayList<>();
//            try {
//                JSONArray array = JSONArray.parseArray(basketBallPutInJsonDto.getPutInJson());
//                for (Object o : array) {
//                    UpdateBasketBallSettleScoreDto updateBasketBallSettleScoreDto = JSONObject.toJavaObject((JSONObject)o,UpdateBasketBallSettleScoreDto.class);
//                    updateBasketBallSettleScores.add(updateBasketBallSettleScoreDto);
//                }
//            }catch (Exception e){
//                log.error("confirmBringInScore:解析参数异常",e);
//                return Response.failed("解析参数异常");
//            }
//
//            //判断带入的比分是否已经结算,假如已经结算则不带入
//            List<String> settleNums = updateBasketBallSettleScores.stream().map(UpdateBasketBallSettleScoreDto::getSettleNum).collect(Collectors.toList());
//            MatchSettleScoreExample example = new MatchSettleScoreExample();
//            example.createCriteria().andSettleNumIn(settleNums).andStandardMatchIdEqualTo(standardMatchId).andStatusNotEqualTo(3);
//            List<MatchSettleScore> matchSettleScores = matchSettleScoreMapper.selectByExample(example);
//            if (!CollectionUtils.isEmpty(matchSettleScores)){
//                matchSettleScores.forEach(matchSettleScore -> {
//                    updateBasketBallSettleScores.forEach(updateBasketBallSettleScore ->{
//                        if (matchSettleScore.getSettleNum().equals(updateBasketBallSettleScore.getSettleNum())){
//                            //赛事比分id
//                            Long matchScoreId = updateBasketBallSettleScore.getMatchScoreId();
//                            //审核员姓名
//                            String userName = basketBallPutInJsonDto.getOperatorName();
//                            //2.查询人工核对比分
//                            MatchSettleCheckInfo matchSettleCheckInfo = matchSettleCheckService.searchCheckInfoByUser(matchScoreId, standardMatchId, userName);
//                            MatchSettleCheckInfo checkInfo = new MatchSettleCheckInfo();
//                            Integer checkNumber = matchSettleScore.getCheckNumber();
//                            UpdateMatchSettleScoreDto dto = new UpdateMatchSettleScoreDto();
//                            updateBasketBallSettleScore.setOperatorName(userName);
//                            BeanUtils.copyProperties(updateBasketBallSettleScore, dto);
//                            if (matchSettleCheckInfo == null) {
//                                //得到当前用户的次序
//                                matchSettleCheckInfo = new MatchSettleCheckInfo();
//                                BeanUtils.copyProperties(matchSettleCheckInfo, checkInfo);
//
//                                matchSettleCheckInfo = SettleCheckUtils.initManualMatchSettleScores(matchSettleScore);
//                                SettleCheckUtils.copyManualMatchSettleScore(updateBasketBallSettleScore, matchSettleCheckInfo);
//                                matchSettleCheckInfo.setCheckNumber(SettleCheckUtils.getCheckNumber(checkNumber));
//                                matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
//                                matchSettleCheckInfo.setDataSourceCode("PA");
//                                matchSettleCheckInfo.setGoWaterStatus(0);
//                                matchSettleCheckInfo.setExtryInfo(matchSettleScore.getExtryInfo());
//                                matchSettleCheckInfoMapper.insert(matchSettleCheckInfo);
//
//
//                                if (!StringUtil.isNullOrEmpty(matchSettleScore.getSettleNum())) {
//                                    dto.setSettleNum(matchSettleScore.getSettleNum());
//                                }
//                                dto.setSportId(matchSettleScore.getSportId());
//
//                                iMatchSettleLogService.matchSettleCheckScoreAddLog(checkInfo, matchSettleCheckInfo, dto, OperateLogTypeEnum.EDIT, matchSettleScore.getSettleNum(), checkNumber);
//                            }
//                            //3.2有比分核对记录则更新
//                            //3.更新状态
//                            matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.CONFIRM);
//                            SettleCheckUtils.copyManualMatchSettleScore(updateBasketBallSettleScore, matchSettleCheckInfo);
//                            matchSettleCheckInfo.setUserName(userName);
//                            matchSettleCheckInfo.setGoWaterStatus(0);
//                            matchSettleCheckInfoMapper.updateByPrimaryKey(matchSettleCheckInfo);
//                            iMatchSettleLogService.matchSettleCheckScoreAddLog(checkInfo, matchSettleCheckInfo, dto, OperateLogTypeEnum.CONFIRM_SCORE, matchSettleScore.getSettleNum(), checkNumber);
//                            //进入统一核对比分流程
//                            matchSettleCheckService.checkCommonMatchSettleScoreEvent(matchSettleScore, matchSettleCheckInfo, false);
//
//                        }
//
//                    });
//                });
//            }
////        //全部結束后去掉同步按鈕
////        MatchSettleScore settleScore = matchSettleScoreMapper.selectByPrimaryKey(basketBallPutInJsonDto.getMatchScoreId());
////        List<PopupUserDto> popUsers = new ArrayList<>();
////        JSONArray array = JSONArray.parseArray(settleScore.getPopupUsers());
////        if (null!=array){
////            for (Object o : array) {
////                PopupUserDto dto = JSONObject.toJavaObject((JSONObject)o,PopupUserDto.class);
////                if (!dto.getPopupUser().equals(basketBallPutInJsonDto.getOperatorName())){
////                    popUsers.add(dto);
////                }
////            }
////        }
////        settleScore.setPopupUsers(JSONArray.toJSONString(popUsers));
////        matchSettleScoreMapper.updateByPrimaryKey(settleScore);
//
//            return Response.success();
//        } else {
//            log.error("confirmBringInScore standardMatchId::{}::比分无法获取redis锁",basketBallPutInJsonDto.getStandardMatchId());
//        }
//        return Response.failed("比分带入时，比分无法获取redis锁,请重试!");
//    }
//
//}
