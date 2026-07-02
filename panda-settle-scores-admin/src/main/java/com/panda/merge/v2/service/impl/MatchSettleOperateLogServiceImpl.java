package com.panda.merge.v2.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.enums.*;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.MatchSettleCheckConstant;
import com.panda.merge.constant.SettleMentionEnum;
import com.panda.merge.dto.SettleQueryDTO;
import com.panda.merge.dto.advertise.MatchFreezeDto;
import com.panda.merge.dto.advertise.MatchSettleSwitcherDto;
import com.panda.merge.dto.settle.SettleEventDeleteRequest;
import com.panda.merge.dto.settle.UpdateMatchSettleScoreDto;
import com.panda.merge.mapper.SettleTemplateExtMappper;
import com.panda.merge.mapper.StandardSportMarketSellMapper;
import com.panda.merge.mapper.StandardSportTeamMapper;
import com.panda.merge.model.*;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.v2.entity.MatchSettleCheckInfoEntity;
import com.panda.merge.v2.entity.MatchSettleOperateLogEntity;
import com.panda.merge.v2.entity.MatchSettleScoreEntity;
import com.panda.merge.v2.entity.MatchSettleTemplateRelationEntity;
import com.panda.merge.v2.mapper.MatchSettleTemplateRelationV2Mapper;
import com.panda.merge.v2.repository.MatchSettleOperateLogV2Repository;
import com.panda.merge.v2.service.IMatchSettleOperateLogService;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static com.panda.merge.common.enums.FaCardEnum.Method_6;
import static com.panda.merge.constant.RepositoryConstant.REDIS_THREE_TIME;
import static com.panda.merge.constant.RepositoryConstant.TEMPLATE_RELATION;

@Slf4j
@Service("MatchSettleOperateLogServiceImpl")
public class MatchSettleOperateLogServiceImpl implements IMatchSettleOperateLogService {

    @Autowired
    RedisService redisService;
    @Autowired
    MatchSettleTemplateRelationV2Mapper matchSettleTemplateRelationV2Mapper;
    @Autowired
    SettleTemplateExtMappper settleTemplateExtMappper;
    @Autowired
    private MatchSettleOperateLogV2Repository matchSettleOperateLogRepository;
    @Autowired
    StandardMatchInfoService standardMatchInfoService;

    @Autowired
    private StandardSportMarketSellMapper standardSportMarketSellMapper;
    @Autowired
    StandardSportTeamMapper standardSportTeamMapper;


    public void delTemplateRelationByExample(MatchSettleTemplateRelationExample example) {
        List<MatchSettleTemplateRelationEntity> list = matchSettleTemplateRelationV2Mapper.selectByExample(example);
        if (!list.isEmpty()) {
            list.forEach(l -> {
                String key = TEMPLATE_RELATION + l.getId();
                try {
                    redisService.del(key);
                } catch (Exception e) {
                    log.error("deleteTemplate:redis删除异常：key=[{}]TemplateRelation[{}]", key, JSONObject.toJSON(l), e);
                }

            });
        }
    }


    public void batchInsertTemplateRelationToRedis(Integer level) {
        List<MatchSettleTemplateRelation> list = settleTemplateExtMappper.selectTemplateRelationByLevel(level);
        if (!list.isEmpty()) {
            list.forEach(l -> {
                String key = TEMPLATE_RELATION + l.getId();
                try {
                    redisService.set(key, JSONObject.toJSON(l), REDIS_THREE_TIME);
                } catch (Exception e) {
                    log.error("batchInsertTemplateRelationToRedis:redis插入异常：key=[{}]TemplateRelation[{}]TemplateRelation[{}]", key, JSONObject.toJSON(l), e);
                }
            });
        }
    }

    @Override
    public void updateMatchSettleScoreAddLog(UpdateMatchSettleScoreDto matchSettleOperateLogDto,
                                             String forwScore, MatchSettleScore matchSettleScore,
                                             StandardMatchInfo standardMatchInfo, String OperateType) {


        String linkId = matchSettleOperateLogDto.getLinkedId();
        try {
            if (standardMatchInfo == null) {
                standardMatchInfo = standardMatchInfoService.getItem(matchSettleScore.getStandardMatchId());
                if (standardMatchInfo == null) {
                    return;
                }
            }
            String eventCode = matchSettleScore.getEventCode();

            String rearT1 = "";
            if (matchSettleScore.getT1() != null) rearT1 = matchSettleScore.getT1().toString();
            String rearT2 = "";
            if (matchSettleScore.getT2() != null) rearT2 = matchSettleScore.getT2().toString();

            MatchSettleOperateLog matchSettleOperateLog = new MatchSettleOperateLog();
            BeanUtils.copyProperties(matchSettleOperateLogDto, matchSettleOperateLog);
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchSettleOperateLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());
            matchSettleOperateLog.setOperateType(OperateType);
            matchSettleOperateLog.setIpAddress(matchSettleOperateLogDto.getIpAddress());
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.type_7.getCode() + "-" + StandardSportTypeEnum.getEnum(standardMatchInfo.getSportId()).getCode());
            matchSettleOperateLog.setOperateId(matchSettleScore.getId().toString());
            if (standardMatchInfo.getSportId().intValue() == 2) {
                //篮球结算事件
                String settleNum = matchSettleOperateLogDto.getBasketBallSettleNum();
                BasketBallSettleNumEnum ballSettleNumEnum = BasketBallSettleNumEnum.getEnum(settleNum);
                if (ballSettleNumEnum != null) {
                    matchSettleOperateLog.setOperateParaName(ballSettleNumEnum.getCode());
                }
                String periodName = MatchSettleCheckConstant.getPeriodBySettleNum(settleNum, standardMatchInfo.getMatchLength());
                if (!StringUtils.isAnyEmpty(periodName)) {
                    if (periodName.equals(BasketBallSettleNumEnum.BK_SN.getCode())) {
                        return;
                    }
                    matchSettleOperateLog.setOperateName(periodName);
                }
            } else {
                matchSettleOperateLog.setOperateName(eventCode);
                String settleNum = matchSettleOperateLogDto.getSettleNum().toString();
                matchSettleOperateLog.setOperateParaName(MatchPeriodEnum.getEnum(settleNum).getCode().toString());
            }
            if (StringUtils.isEmpty(eventCode) || "kick_off".equals(eventCode)) {
                matchSettleOperateLog.setOperateName("goal");
            }
            matchSettleOperateLog.setOperateUserName(matchSettleOperateLogDto.getOperatorName());
            matchSettleOperateLog.setOperateForwText(forwScore);
            matchSettleOperateLog.setOperateRearText(rearT1 + "-" + rearT2);
            //结算方式特殊处理
            List<Integer> integers = Arrays.asList(1021, 1031, 1032, 1033);
            List<String> str = Arrays.asList("206", "207", "208");
            if (integers.contains(matchSettleOperateLogDto.getSettleNum())) {
                if (matchSettleScore.getGoWaterStatus() != null && matchSettleScore.getGoWaterStatus().equals(1)) {
                    matchSettleScore.setExtryInfo(WinningMethodEnum.Method_8.getCode().toString());
                }
                String rearScore = processedScore(matchSettleOperateLogDto.getSettleNum(), Integer.parseInt(matchSettleScore.getExtryInfo()));
                matchSettleOperateLog.setOperateRearText(rearScore);
            }

            //角球加时赛 判断走水
            if (str.contains(matchSettleScore.getSettleNum())) {

                if (matchSettleScore.getGoWaterStatus() != null && matchSettleScore.getGoWaterStatus().equals(1)) {
                    matchSettleOperateLog.setOperateRearText(OperateLogTypeEnum.SCORES_SETTLE_10031.getCode().toString());
                }
            }
            MatchSettleOperateLogEntity entity = new MatchSettleOperateLogEntity();
            BeanUtils.copyProperties(matchSettleOperateLog,entity);
            matchSettleOperateLogRepository.save(entity);
        } catch (Exception e) {
            log.error("::"+linkId+":: updateMatchSettleScoreAddLog-v2,标准赛事ID:"+ JSON.toJSONString(standardMatchInfo)+", error:", e);

        }
    }

    @Override
    @Async("MatchScoreLogThreadPool")
    public void matchSettleScoreAddLog(MatchSettleScore matchSettleScore, String operatorName, OperateLogTypeEnum type, String beforeText, String ipAddress) {
        try {
            String eventCode = matchSettleScore.getEventCode();

            String forwT1 = "";
            if (matchSettleScore.getT1() != null) forwT1 = matchSettleScore.getT1().toString();
            String forwT2 = "";
            if (matchSettleScore.getT2() != null) forwT2 = matchSettleScore.getT2().toString();
            String rearT1 = "";
            if (matchSettleScore.getT1() != null) rearT1 = matchSettleScore.getT1().toString();
            String rearT2 = "";
            if (matchSettleScore.getT2() != null) rearT2 = matchSettleScore.getT2().toString();


            MatchSettleOperateLog matchSettleOperateLog = new MatchSettleOperateLog();
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchSettleScore.getStandardMatchId());
            if (standardMatchInfo == null) {
                return;
            }
            matchSettleOperateLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchSettleOperateLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());
            matchSettleOperateLog.setOperateForwText(forwT1 + "-" + forwT2);
            matchSettleOperateLog.setOperateRearText(rearT1 + "-" + rearT2);
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.type_7.getCode() + "-" + StandardSportTypeEnum.getEnum(matchSettleScore.getSportId()).getCode());
            matchSettleOperateLog.setOperateType(type.getCode().toString());
            matchSettleOperateLog.setOperateUserName(operatorName);
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setIpAddress(ipAddress);
            //操作参数名称
            String settleNum = matchSettleScore.getSettleNum();
            //操作对象id
            if (type.getCode().toString().equals(OperateLogTypeEnum.SCORES_SETTLE_10039.getCode().toString())) {
                matchSettleOperateLog.setOperateId(standardMatchInfo.getMatchManageId());
                matchSettleOperateLog.setOperateName(standardMatchInfo.getHomeAwayInfo());
            } else {
                matchSettleOperateLog.setOperateId(matchSettleScore.getId().toString());
                if (matchSettleScore.getSportId().intValue() == 2) {
                    //篮球结算事件
                    BasketBallSettleNumEnum basketBallSettleNumEnum = BasketBallSettleNumEnum.getEnum(settleNum);
                    if (basketBallSettleNumEnum != null) {
                        matchSettleOperateLog.setOperateParaName(basketBallSettleNumEnum.getCode());
                    }
                    String periodNum = MatchSettleCheckConstant.getPeriodBySettleNum(settleNum, standardMatchInfo.getMatchLength());
                    if (!StringUtils.isAnyEmpty(periodNum)) {
                        if (periodNum.equals(BasketBallSettleNumEnum.BK_SN.getCode())) {
                            //比如：第1节首先获得10分，会下发 首先获得 N 分 事件日志过滤掉,
                            return;
                        }
                        matchSettleOperateLog.setOperateName(periodNum);
                    }
                } else {
                    matchSettleOperateLog.setOperateName(matchSettleScore.getEventCode());
                    if (MatchPeriodEnum.getEnum(settleNum) != null) {
                        matchSettleOperateLog.setOperateParaName(MatchPeriodEnum.getEnum(settleNum).getCode().toString());
                    }
                }
            }

            //开球也属于进球类
            if (StringUtils.isEmpty(eventCode) || "kick_off".equals(eventCode)) {
                matchSettleOperateLog.setOperateName("goal");
            }
            //进球特殊处理
            //获胜方式, 点球大战, 是否加时赛, 点球大战走水
            List<String> list = Arrays.asList("1021", "1031", "1032", "1033");

            if (list.contains(settleNum)) {
                String homeAway = matchSettleScore.getExtryInfo();
                homeAway = processedScore(matchSettleScore.getSettleNum(), Integer.parseInt(homeAway));
                matchSettleOperateLog.setOperateForwText(homeAway);
                matchSettleOperateLog.setOperateRearText(homeAway);
            }

            List<String> str = Arrays.asList("206", "207", "208");
            //角球加时赛 判断走水
            if (str.contains(matchSettleScore.getSettleNum())) {
                if (matchSettleScore.getGoWaterStatus() != null && matchSettleScore.getGoWaterStatus().equals(1)) {
                    matchSettleOperateLog.setOperateForwText(OperateLogTypeEnum.SCORES_SETTLE_10031.getCode().toString());
                    matchSettleOperateLog.setOperateRearText(OperateLogTypeEnum.SCORES_SETTLE_10031.getCode().toString());
                }
            }
            MatchSettleOperateLogEntity entity = new MatchSettleOperateLogEntity();
            BeanUtils.copyProperties(matchSettleOperateLog,entity);
            matchSettleOperateLogRepository.save(entity);

            //二次结算再次记录日志
            if (matchSettleScore.getSettleCount() != null && matchSettleScore.getSettleCount() > 1 && type.getCode() == 10017) {
                matchSettleOperateLog.setOperateType("10026");
                if (beforeText.equals("80")){
                    beforeText = OperateLogTypeEnum.SETTLE_REASON_80.getCode().toString();
                }
                if (beforeText.equals("81")){
                    beforeText = OperateLogTypeEnum.SETTLE_REASON_81.getCode().toString();
                }
                if (beforeText.equals("82")){
                    beforeText = OperateLogTypeEnum.SETTLE_REASON_82.getCode().toString();
                }
                if (beforeText.equals("83")){
                    beforeText = OperateLogTypeEnum.SETTLE_REASON_83.getCode().toString();
                }
                if (beforeText.equals("84")){
                    beforeText = OperateLogTypeEnum.SETTLE_REASON_84.getCode().toString();
                }

                matchSettleOperateLog.setOperateForwText(beforeText);

                Integer settleReason =matchSettleScore.getSettleReason();
                if (settleReason == 80){
                    settleReason = OperateLogTypeEnum.SETTLE_REASON_80.getCode();
                }
                if (settleReason == 81){
                    settleReason = OperateLogTypeEnum.SETTLE_REASON_81.getCode();
                }
                if (settleReason == 82){
                    settleReason = OperateLogTypeEnum.SETTLE_REASON_82.getCode();
                }
                if (settleReason == 83){
                    settleReason = OperateLogTypeEnum.SETTLE_REASON_83.getCode();
                }
                if (settleReason == 84){
                    settleReason = OperateLogTypeEnum.SETTLE_REASON_84.getCode();
                }

                matchSettleOperateLog.setOperateRearText(settleReason.toString());
//                //二次结算原因为"其他" 将拼接详细原因
//                if (matchSettleScore.getSettleReason() == 118) {
//                    matchSettleOperateLog.setOperateRearText(matchSettleScore.getSettleReason().toString() + ": " + matchSettleScore.getSettleReasonDetail());
//                }
                MatchSettleOperateLogEntity entity2 = new MatchSettleOperateLogEntity();
                BeanUtils.copyProperties(matchSettleOperateLog,entity2);
                matchSettleOperateLogRepository.save(entity2);
            }

        } catch (Exception e) {
            log.error("matchSettleScoreAddLog :"+type+",标准赛事ID:"+matchSettleScore.getStandardMatchId()+", error:" , e);

        }
    }

    @Override
    public void matchSettleScoreAddLog(MatchSettleScoreEntity matchSettleScore, MatchSettleScore newMatchSettleScore, String operatorName, String type, String linkId, String ipAddress) {
        String eventCode = newMatchSettleScore.getEventCode();
        try {
            MatchSettleOperateLog matchSettleOperateLog = new MatchSettleOperateLog();
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(newMatchSettleScore.getStandardMatchId());
            if (standardMatchInfo != null) {
                matchSettleOperateLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
                matchSettleOperateLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());
            }

            String forwT1 = "";
            if (matchSettleScore.getT1() != null) forwT1 = matchSettleScore.getT1().toString();
            String forwT2 = "";
            if (matchSettleScore.getT2() != null) forwT2 = matchSettleScore.getT2().toString();
            String rearT1 = "";
            if (newMatchSettleScore.getT1() != null) rearT1 = newMatchSettleScore.getT1().toString();
            String rearT2 = "";
            if (newMatchSettleScore.getT2() != null) rearT2 = newMatchSettleScore.getT2().toString();


            matchSettleOperateLog.setOperateForwText(forwT1 + "-" + forwT2);
            matchSettleOperateLog.setOperateRearText(rearT1 + "-" + rearT2);
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.type_7.getCode() + "-" + StandardSportTypeEnum.getEnum(newMatchSettleScore.getSportId()).getCode());
            matchSettleOperateLog.setOperateType(type);
            matchSettleOperateLog.setOperateUserName(operatorName);
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setIpAddress(ipAddress);
            //操作对象id
            if (type.equals(OperateLogTypeEnum.SCORES_SETTLE_10039.getCode().toString())) {
                matchSettleOperateLog.setOperateId(standardMatchInfo.getMatchManageId());
                matchSettleOperateLog.setOperateName(standardMatchInfo.getHomeAwayInfo());
            } else {
                matchSettleOperateLog.setOperateId(matchSettleScore.getId().toString());
                if (matchSettleScore.getSportId().intValue() == 2) {
                    String periodNum = MatchSettleCheckConstant.getPeriodBySettleNum(matchSettleScore.getSettleNum(), standardMatchInfo.getMatchLength());
                    if (!StringUtils.isAnyEmpty(periodNum)) {
                        if (periodNum.equals(BasketBallSettleNumEnum.BK_SN.getCode())) {
                            //比如：第1节首先获得10分，会下发 首先获得 N 分 事件日志过滤掉,
                            return;
                        }
                        matchSettleOperateLog.setOperateName(periodNum);
                    }
                } else {
                    matchSettleOperateLog.setOperateName(matchSettleScore.getEventCode());
                }
            }

            if (StringUtils.isEmpty(eventCode) || "kick_off".equals(eventCode)) {
                matchSettleOperateLog.setOperateName("goal");
            }
            //操作参数名称
            String settleNum = matchSettleScore.getSettleNum();
            if (standardMatchInfo.getSportId().intValue() == 2) {
                //篮球结算事件
                BasketBallSettleNumEnum basketBallSettleNumEnum = BasketBallSettleNumEnum.getEnum(settleNum);
                if (basketBallSettleNumEnum != null) {
                    matchSettleOperateLog.setOperateParaName(basketBallSettleNumEnum.getCode());
                }
            } else {
                if (MatchPeriodEnum.getEnum(settleNum) != null) {
                    matchSettleOperateLog.setOperateParaName(MatchPeriodEnum.getEnum(settleNum).getCode().toString());
                }
            }
            //进球特殊处理
            //获胜方式, 点球大战, 是否加时赛, 点球大战走水
            List<String> list = Arrays.asList("1021", "1031", "1032", "1033");
            if (list.contains(settleNum)) {
                String homeAway = matchSettleScore.getExtryInfo();
                if (homeAway == null) homeAway = "-";
                //8 表示走水
                if (("1032".equals(homeAway) || "1033".equals(homeAway)) && matchSettleScore.getExtryInfo() == "1")
                    homeAway = "8";
                matchSettleOperateLog.setOperateForwText(homeAway);
                matchSettleOperateLog.setOperateRearText("-");
                //走水处理
                if (matchSettleScore.getGoWaterStatus() != null && matchSettleScore.getGoWaterStatus().equals(1))
                    matchSettleOperateLog.setOperateForwText("8");
            }

            MatchSettleOperateLogEntity entity = new MatchSettleOperateLogEntity();
            BeanUtils.copyProperties(matchSettleOperateLog,entity);
            matchSettleOperateLogRepository.save(entity);
        } catch (Exception e) {
            log.error("::"+linkId+"::matchSettleScoreAddLog :"+type+",标准赛事管理ID:"+matchSettleScore.getStandardMatchId()+", error:", e);

        }
    }

    @Override
    public void deleteSettleAlertLog(Object matchSettleScoreEventInfo, MatchSettleSwitcherDto matchSettleSwitcherDto) {

        try {
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchSettleSwitcherDto.getMatchId());
            if (standardMatchInfo == null) {
                return;
            }
            String settleNum = null;
            String eventCode = null;
            Integer eventOrder = null;
            if (matchSettleScoreEventInfo instanceof MatchSettleScore) {
                settleNum = ((MatchSettleScore) matchSettleScoreEventInfo).getSettleNum();
                eventCode = ((MatchSettleScore) matchSettleScoreEventInfo).getEventCode();

            } else if (matchSettleScoreEventInfo instanceof MatchSettleEvent) {
                settleNum = ((MatchSettleEvent) matchSettleScoreEventInfo).getSettleNum();
                eventCode = ((MatchSettleEvent) matchSettleScoreEventInfo).getEventCode();
                eventOrder = ((MatchSettleEvent) matchSettleScoreEventInfo).getEventOrder();
            } else {
                log.error("deleteSettleAlertLog matchSettleScoreEventInfo:{}:传入类型错误", matchSettleScoreEventInfo);
                return;
            }
            MatchSettleOperateLog matchSettleOperateLog = new MatchSettleOperateLog();
            if (!StringUtils.isAnyEmpty(eventCode)) {
                switch (eventCode) {
                    case "goal":
                        matchSettleOperateLog.setOperateName(OperateLogTypeEnum.type_12.getCode().toString());
                        break;
                    case "corner":
                        matchSettleOperateLog.setOperateName(OperateLogTypeEnum.type_13.getCode().toString());
                        break;
                    case "fa_card":
                        matchSettleOperateLog.setOperateName(OperateLogTypeEnum.type_14.getCode().toString());
                        break;
                    case "score_change":
                        matchSettleOperateLog.setOperateName(OperateLogTypeEnum.type_17.getCode().toString());
                        break;
                    default:

                        return;
                }
            }
            if (!StringUtils.isAnyEmpty(settleNum)) {
                if (eventCode.equals("score_change")){
                    BasketBallSettleNumEnum basketBallSettleNumEnum = BasketBallSettleNumEnum.getEnum(settleNum);
                    if (basketBallSettleNumEnum != null && basketBallSettleNumEnum.getCode() != null) {
                        matchSettleOperateLog.setOperateParaName(basketBallSettleNumEnum.getCode());
                    }
                }else {
                    MatchPeriodEnum matchPeriodEnum = MatchPeriodEnum.getEnum(settleNum);
                    if (matchPeriodEnum != null && matchPeriodEnum.getCode() != null) {
                        matchSettleOperateLog.setOperateParaName(matchPeriodEnum.getCode().toString());
                    }
                }

            }
            matchSettleOperateLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchSettleOperateLog.setOperateMatchName(getMatchSportTeamNameCode(standardMatchInfo.getId()));
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.type_7.getCode() + "-" + StandardSportTypeEnum.getEnum(standardMatchInfo.getSportId()).getCode());
            matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100154.getCode().toString());
            matchSettleOperateLog.setOperateForwText("-");
            matchSettleOperateLog.setOperateRearText("-");
            matchSettleOperateLog.setOperateUserName(matchSettleSwitcherDto.getOperatorName());
            matchSettleOperateLog.setIpAddress(matchSettleSwitcherDto.getIpAddress());
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            if (eventOrder != null && eventOrder > 0) {
                matchSettleOperateLog.setRemark(eventOrder.toString());
            }
            MatchSettleOperateLogEntity entity = new MatchSettleOperateLogEntity();
            BeanUtils.copyProperties(matchSettleOperateLog,entity);
            matchSettleOperateLogRepository.save(entity);
        } catch (Exception e) {
            log.error("删除阶段报警日志 标准赛事ID:"+matchSettleSwitcherDto.getMatchId()+", error:", e);
        }
    }

    @Override
    public void matchSettleCheckScoreAddLog(MatchSettleCheckInfoEntity oIdInfo, MatchSettleCheckInfoEntity newInfo, UpdateMatchSettleScoreDto dto, OperateLogTypeEnum enums, String settleNum, Integer checkNumber) {
        try {
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(newInfo.getStandardMatchId());
            if (standardMatchInfo == null) {
                return;
            }

            //操作对象转换
            String eventCode = newInfo.getEventCode();

            String forT1 = "";
            if (oIdInfo.getT1() != null) forT1 = oIdInfo.getT1().toString();
            String forT2 = "";
            if (oIdInfo.getT2() != null) forT2 = oIdInfo.getT2().toString();

            String rearT1 = "";
            if (newInfo.getT1() != null) rearT1 = newInfo.getT1().toString();
            String rearT2 = "";
            if (newInfo.getT2() != null) rearT2 = newInfo.getT2().toString();

            matchSettleOperateLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchSettleOperateLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());
            matchSettleOperateLog.setOperateId(dto.getMatchScoreId().toString());
            matchSettleOperateLog.setOperateForwText(forT1 + "-" + forT2);
            matchSettleOperateLog.setOperateRearText(rearT1 + "-" + rearT2);
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.type_7.getCode() + "-" + StandardSportTypeEnum.getEnum(standardMatchInfo.getSportId()).getCode());
            matchSettleOperateLog.setOperateUserName(dto.getOperatorName() + ",(第" + checkNumber + "人)");
            matchSettleOperateLog.setOperateType(enums.getCode().toString());
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setIpAddress(dto.getIpAddress());
            if (standardMatchInfo.getSportId().intValue() == 2) {
                //篮球结算事件
                if (BasketBallSettleNumEnum.getEnum(settleNum) != null) {
                    matchSettleOperateLog.setOperateParaName(BasketBallSettleNumEnum.getEnum(settleNum).getCode());
                }
                String periodName = MatchSettleCheckConstant.getPeriodBySettleNum(settleNum, standardMatchInfo.getMatchLength());
                if (!StringUtils.isAnyEmpty(periodName)) {
                    if (periodName.equals(BasketBallSettleNumEnum.BK_SN.getCode())) {
                        return;
                    }
                    matchSettleOperateLog.setOperateName(periodName);
                }
            } else {
                if (MatchPeriodEnum.getEnum(settleNum) != null) {
                    matchSettleOperateLog.setOperateParaName(MatchPeriodEnum.getEnum(settleNum).getCode().toString());
                }
                matchSettleOperateLog.setOperateName(eventCode);
            }
            if (StringUtils.isEmpty(eventCode) || "kick_off".equals(eventCode)) {
                matchSettleOperateLog.setOperateName("goal");
            }

            //进球比分特殊处理
            List<String> integers = Arrays.asList("1021", "1031", "1032", "1033");
            //角球比分
            List<String> str = Arrays.asList("206", "207", "208");


            if (integers.contains(dto.getSettleNum().toString())) {
                if (dto.getGoWaterStatus() != null && dto.getGoWaterStatus().equals(1)) {
                    dto.setExtryInfo(WinningMethodEnum.Method_8.getCode().toString());
                }

                if (!StringUtil.isNullOrEmpty(oIdInfo.getExtryInfo())) {
                    String forw = processedScore(dto.getSettleNum(), Integer.parseInt(oIdInfo.getExtryInfo()));
                    //修改后
                    matchSettleOperateLog.setOperateForwText(forw);
                }
                String rearScore = processedScore(dto.getSettleNum(), Integer.parseInt(newInfo.getExtryInfo()));
                //修改后
                matchSettleOperateLog.setOperateRearText(rearScore);

                //确认比分   将修改前改为一致
                if (enums.getCode().equals(OperateLogTypeEnum.CONFIRM_SCORE.getCode())) {
                    //修改前
                    matchSettleOperateLog.setOperateForwText(rearScore);
                }

            }

            //角球特殊处理
            if (str.contains(dto.getSettleNum())) {
                CheckInfoCornerProcessRest(oIdInfo, newInfo, matchSettleOperateLog);
            }
            MatchSettleOperateLogEntity entity = new MatchSettleOperateLogEntity();
            BeanUtils.copyProperties(matchSettleOperateLog,entity);
            matchSettleOperateLogRepository.save(entity);

            if (standardMatchInfo.getSportId() == 1 && newInfo.getCheckType() == 1) {
                return;
            }
            //5分钟编辑再次记录日志
            if (newInfo.getFiveMinSection() != null && !"".equals(newInfo.getFiveMinSection())) {
                if (oIdInfo.getFiveMinSection() != null && !"".equals(oIdInfo.getFiveMinSection()))
                    matchSettleOperateLog.setOperateForwText(oIdInfo.getFiveMinSection());
                else
                    matchSettleOperateLog.setOperateForwText("-");
                matchSettleOperateLog.setOperateRearText(newInfo.getFiveMinSection());
                MatchSettleOperateLogEntity entity2 = new MatchSettleOperateLogEntity();
                BeanUtils.copyProperties(matchSettleOperateLog,entity2);
                matchSettleOperateLogRepository.save(entity2);
            }
            //15分钟编辑再次记录日志
            if (newInfo.getFiveMinSection() != null ) {
                newInfo.setFifteenMinSection(calcFifteenMinSection(newInfo.getFiveMinSection()));
                if (oIdInfo.getFiveMinSection()  != null ) {
                    matchSettleOperateLog.setOperateForwText(newInfo.getFifteenMinSection());
                }else{
                    matchSettleOperateLog.setOperateForwText("-");
                }
                matchSettleOperateLog.setOperateRearText(newInfo.getFifteenMinSection());
                MatchSettleOperateLogEntity entity3 = new MatchSettleOperateLogEntity();
                BeanUtils.copyProperties(matchSettleOperateLog,entity3);
                matchSettleOperateLogRepository.save(entity3);
            }
        } catch (Exception e) {
            log.error("matchSettleCheckInfoAddLog,标准赛事ID:"+dto.getStandardMatchId()+" , error:", e);

        }
    }

    @Override
    public void categoryReSettleAddLog(SettleQueryDTO settleQueryDTO, String forwText) {

            try {
                MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
                StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(settleQueryDTO.getMatchId());
                if (standardMatchInfo == null) {
                    return;
                }
                //操作对象转换
                String operateName = "-";
                if (settleQueryDTO.getSportId().intValue() == 2) {
                    //操作参数名称
                    if (settleQueryDTO.getLevel().equals(2) && (settleQueryDTO.getExInfo() == 0 || settleQueryDTO.getExInfo() == 1 || settleQueryDTO.getExInfo() == 2)) {
                        List<String> basketBallSettleNums = MatchSettleCheckConstant.getBasketBallSettleNumEnumList(settleQueryDTO);
                        if (!basketBallSettleNums.isEmpty()) {
                            if (!settleQueryDTO.getSettleNum().equals("100") && !settleQueryDTO.getSettleNum().equals("200") && !settleQueryDTO.getSettleNum().equals("300") && !settleQueryDTO.getSettleNum().equals("400") && !settleQueryDTO.getSettleNum().equals("end")) {
                                matchSettleOperateLog.setOperateParaName(basketBallSettleNums.get(0));
                            }
                            if (settleQueryDTO.getPlayCategoryNum() != null && settleQueryDTO.getPlayCategoryNum() == 1 && (standardMatchInfo.getMatchLength() == 17 || standardMatchInfo.getMatchLength() == 73)) {
                                matchSettleOperateLog.setOperateParaName("-");
                            }
                            String periodName = MatchSettleCheckConstant.getPeriodBySettleNum(basketBallSettleNums.get(0), standardMatchInfo.getMatchLength());
                            if (!StringUtils.isAnyEmpty(periodName)) {
                                if (periodName.equals(BasketBallSettleNumEnum.BK_SN.getCode())) {
                                    return;
                                }
                                operateName = periodName;
                            }
                        }
                    }
                } else {
                    //操作参数名称
                    matchSettleOperateLog.setOperateParaName("-");
                    if (settleQueryDTO.getPlayCategory() != null) {
                        switch (settleQueryDTO.getPlayCategory()) {
                            case 1:
                                operateName = "goal";
                                break;
                            case 2:
                                operateName = "corner";
                                break;
                            case 3:
                                operateName = "fa_card";
                                break;
                        }
                    }
                }
                String realText = "-";
                String operateType = "-";
                switch (settleQueryDTO.getExInfo()) {
                    case 0: //取消冻结
                        operateType = OperateLogTypeEnum.type_2.getCode().toString();
                        realText = OperateLogTypeEnum.type_2.getCode().toString(); //操作后
                        break;
                    case 1:
                        //玩法按分钟冻结
                        if (settleQueryDTO.getMins() != null && settleQueryDTO.getMins() != 0) {
                            forwText = "-";
                            realText = OperateLogTypeEnum.type_1.getCode().toString();
                            operateType = OperateLogTypeEnum.SCORES_SETTLE_10037.getCode().toString();
                            matchSettleOperateLog.setRemark(settleQueryDTO.getMins().toString());
                        } else {
                            realText = OperateLogTypeEnum.type_1.getCode().toString();
                            operateType = OperateLogTypeEnum.SCORES_SETTLE_10032.getCode().toString();
                        }
                        break;
                    case 2:  //玩法级程序重跑
                        realText = "-";
                        operateType = OperateLogTypeEnum.ROLLBACK_EXECUTE.getCode().toString();
                        break;
                    default:
                        break;
                }
                matchSettleOperateLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
                matchSettleOperateLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());
                matchSettleOperateLog.setOperateForwText(forwText);
                matchSettleOperateLog.setOperateRearText(realText);
                matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.type_7.getCode() + "-" + StandardSportTypeEnum.getEnum(standardMatchInfo.getSportId()).getCode());
                matchSettleOperateLog.setOperateUserName(settleQueryDTO.getOperatorName());
                matchSettleOperateLog.setOperateType(operateType);
                matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                matchSettleOperateLog.setIpAddress(settleQueryDTO.getIpAddress());
                //操作对象id
                matchSettleOperateLog.setOperateId("-");
                matchSettleOperateLog.setOperateName(operateName);
                matchSettleOperateLogRepository.save(matchSettleOperateLog);
            } catch (Exception e) {
                log.error("categoryReSettleAddLog,标准赛事ID:"+settleQueryDTO.getMatchId()+" , error:", e);

            }

    }

    void CheckInfoCornerProcessRest(MatchSettleCheckInfoEntity matchSettleEvent,
                                    MatchSettleCheckInfoEntity newMatchSettleEvent,
                                    MatchSettleOperateLogEntity matchSettleOperateLog) {
        if ("none".equals(matchSettleEvent.getHomeAway()) && matchSettleEvent.getCheckStatus() > 0) {
            matchSettleOperateLog.setOperateForwText(OperateLogTypeEnum.SCORES_SETTLE_10029.getCode().toString());
        } else if ("home".equals(matchSettleEvent.getHomeAway()) && matchSettleEvent.getCheckStatus() > 0) {
            matchSettleOperateLog.setOperateForwText("1-0");
        } else if ("away".equals(matchSettleEvent.getHomeAway()) && matchSettleEvent.getCheckStatus() > 0) {
            matchSettleOperateLog.setOperateForwText("0-1");
        } else if ("none".equals(matchSettleEvent.getHomeAway()) && matchSettleEvent.getCheckStatus() == 0) {
            matchSettleOperateLog.setOperateForwText("-");
        }


        if ("none".equals(newMatchSettleEvent.getHomeAway()) && newMatchSettleEvent.getCheckStatus() > 0) {
            matchSettleOperateLog.setOperateRearText(OperateLogTypeEnum.SCORES_SETTLE_10029.getCode().toString());
        } else if ("home".equals(newMatchSettleEvent.getHomeAway()) && newMatchSettleEvent.getCheckStatus() > 0) {
            matchSettleOperateLog.setOperateRearText("1-0");
        } else if ("away".equals(newMatchSettleEvent.getHomeAway()) && newMatchSettleEvent.getCheckStatus() > 0) {
            matchSettleOperateLog.setOperateRearText("0-1");
        } else if ("none".equals(newMatchSettleEvent.getHomeAway()) && newMatchSettleEvent.getCheckStatus() == 0) {
            matchSettleOperateLog.setOperateRearText("-");
        }
        //设置走水
        if (matchSettleEvent.getGoWaterStatus() != null && matchSettleEvent.getGoWaterStatus().equals(1))
            matchSettleOperateLog.setOperateForwText("10031");
        if (newMatchSettleEvent.getGoWaterStatus() != null && newMatchSettleEvent.getGoWaterStatus().equals(1))
            matchSettleOperateLog.setOperateRearText("10031");
    }
    private static String calcFifteenMinSection(String fiveMinSection) {
        Integer val = Integer.parseInt(fiveMinSection);
        switch (val) {
            case 5:
            case 10:
            case 15:
                return "60899";
            case 20:
            case 25:
            case 30:
                return "61799";
            case 35:
            case 40:
            case 45:
            case 49:
                return "62699";
            case 50:
            case 55:
            case 60:
                return "73599";
            case 65:
            case 70:
            case 75:
                return "74499";
            case 80:
            case 85:
            case 90:
            case 99:
                return "75399";
            case 60899:
                return "60899";
            case 61799:
                return "61799";
            case 62699:
                return "62699";
            case 73599:
                return "73599";
            case 74499:
                return "74499";
            case 75399:
                return "75399";
            default:
                return "0";
        }
    }

    //角球特殊处理
    void CornerProcessRest(MatchSettleEvent matchSettleEvent,
                           MatchSettleEvent newMatchSettleEvent,
                           MatchSettleOperateLogEntity matchSettleOperateLog) {
        if ("none".equals(matchSettleEvent.getHomeAway()) && matchSettleEvent.getStatus() > 0) {
            matchSettleOperateLog.setOperateForwText(OperateLogTypeEnum.SCORES_SETTLE_10029.getCode().toString());
        } else if ("home".equals(matchSettleEvent.getHomeAway()) && matchSettleEvent.getStatus() > 0) {
            matchSettleOperateLog.setOperateForwText("1-0");
        } else if ("away".equals(matchSettleEvent.getHomeAway()) && matchSettleEvent.getStatus() > 0) {
            matchSettleOperateLog.setOperateForwText("0-1");
        } else if ("none".equals(matchSettleEvent.getHomeAway()) && matchSettleEvent.getStatus() == 0) {
            matchSettleOperateLog.setOperateForwText("-");
        }


        if ("none".equals(newMatchSettleEvent.getHomeAway()) && newMatchSettleEvent.getStatus() > 0) {
            matchSettleOperateLog.setOperateRearText(OperateLogTypeEnum.SCORES_SETTLE_10029.getCode().toString());
        } else if ("home".equals(newMatchSettleEvent.getHomeAway()) && newMatchSettleEvent.getStatus() > 0) {
            matchSettleOperateLog.setOperateRearText("1-0");
        } else if ("away".equals(newMatchSettleEvent.getHomeAway()) && newMatchSettleEvent.getStatus() > 0) {
            matchSettleOperateLog.setOperateRearText("0-1");
        } else if ("none".equals(newMatchSettleEvent.getHomeAway()) && newMatchSettleEvent.getStatus() == 0) {
            matchSettleOperateLog.setOperateRearText("-");
        }
        //设置走水
        if (matchSettleEvent.getGoWaterStatus() != null && matchSettleEvent.getGoWaterStatus().equals(1))
            matchSettleOperateLog.setOperateForwText("10031");
        if (newMatchSettleEvent.getGoWaterStatus() != null && newMatchSettleEvent.getGoWaterStatus().equals(1))
            matchSettleOperateLog.setOperateRearText("10031");

    }
//    @Override
//    public void deleteSettleAlertLog(Object matchSettleScoreEventInfo, MatchSettleSwitcherDto matchSettleSwitcherDto) {
//
//        try {
//            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchSettleSwitcherDto.getMatchId());
//            if (standardMatchInfo == null) {
//                return;
//            }
//            String settleNum = null;
//            String eventCode = null;
//            Integer eventOrder = null;
//            if (matchSettleScoreEventInfo instanceof MatchSettleScore) {
//                settleNum = ((MatchSettleScore) matchSettleScoreEventInfo).getSettleNum();
//                eventCode = ((MatchSettleScore) matchSettleScoreEventInfo).getEventCode();
//
//            } else if (matchSettleScoreEventInfo instanceof MatchSettleEvent) {
//                settleNum = ((MatchSettleEvent) matchSettleScoreEventInfo).getSettleNum();
//                eventCode = ((MatchSettleEvent) matchSettleScoreEventInfo).getEventCode();
//                eventOrder = ((MatchSettleEvent) matchSettleScoreEventInfo).getEventOrder();
//            } else {
//                log.error("deleteSettleAlertLog matchSettleScoreEventInfo:{}:传入类型错误", matchSettleScoreEventInfo);
//                return;
//            }
//            MatchSettleOperateLog matchSettleOperateLog = new MatchSettleOperateLog();
//            if (!StringUtils.isAnyEmpty(eventCode)) {
//                switch (eventCode) {
//                    case "goal":
//                        matchSettleOperateLog.setOperateName(OperateLogTypeEnum.type_12.getCode().toString());
//                        break;
//                    case "corner":
//                        matchSettleOperateLog.setOperateName(OperateLogTypeEnum.type_13.getCode().toString());
//                        break;
//                    case "fa_card":
//                        matchSettleOperateLog.setOperateName(OperateLogTypeEnum.type_14.getCode().toString());
//                        break;
//                    case "score_change":
//                        matchSettleOperateLog.setOperateName(OperateLogTypeEnum.type_17.getCode().toString());
//                        break;
//                    default:
//
//                        return;
//                }
//            }
//            if (!StringUtils.isAnyEmpty(settleNum)) {
//                if (eventCode.equals("score_change")){
//                    BasketBallSettleNumEnum basketBallSettleNumEnum = BasketBallSettleNumEnum.getEnum(settleNum);
//                    if (basketBallSettleNumEnum != null && basketBallSettleNumEnum.getCode() != null) {
//                        matchSettleOperateLog.setOperateParaName(basketBallSettleNumEnum.getCode());
//                    }
//                }else {
//                    MatchPeriodEnum matchPeriodEnum = MatchPeriodEnum.getEnum(settleNum);
//                    if (matchPeriodEnum != null && matchPeriodEnum.getCode() != null) {
//                        matchSettleOperateLog.setOperateParaName(matchPeriodEnum.getCode().toString());
//                    }
//                }
//
//            }
//            matchSettleOperateLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
//            matchSettleOperateLog.setOperateMatchName(getMatchSportTeamNameCode(standardMatchInfo.getId()));
//            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.type_7.getCode() + "-" + StandardSportTypeEnum.getEnum(standardMatchInfo.getSportId()).getCode());
//            matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100154.getCode().toString());
//            matchSettleOperateLog.setOperateForwText("-");
//            matchSettleOperateLog.setOperateRearText("-");
//            matchSettleOperateLog.setOperateUserName(matchSettleSwitcherDto.getOperatorName());
//            matchSettleOperateLog.setIpAddress(matchSettleSwitcherDto.getIpAddress());
//            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
//            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
//            if (eventOrder != null && eventOrder > 0) {
//                matchSettleOperateLog.setRemark(eventOrder.toString());
//            }
//            MatchSettleOperateLogEntity entity = new MatchSettleOperateLogEntity();
//            BeanUtils.copyProperties(matchSettleOperateLog,entity);
//            matchSettleOperateLogRepository.save(entity);
//        } catch (Exception e) {
//            log.error("删除阶段报警日志 标准赛事ID:"+matchSettleSwitcherDto.getMatchId()+", error:", e);
//        }
//    }

    public String processedScore(String settleNum, Integer extryInfo) {
        String rearText = "-";
        if (settleNum.equals(1021)) {
            rearText = WinningMethodEnum.getWinningMethodByCode(extryInfo).getCode().toString();
            return rearText;
        }

        if (settleNum.equals(1031)) {
            rearText = YesNoEnum.getEnum(extryInfo).value + "";
            return rearText;
        }
        if (settleNum.equals(1032) || settleNum.equals(1033)) {
            //1表示走水
            if (extryInfo.equals(1)) {
                rearText = WinningMethodEnum.Method_8.getCode().toString();
                return rearText;
            }
        }
        return rearText;
    }
    private String getMatchSportTeamNameCode(Long matchInfoId) {

        StringBuffer result = new StringBuffer();
        StandardSportMarketSell standardSportMarketSell = new StandardSportMarketSell();
        StandardSportMarketSellExample standardSportMarketSellExample = new StandardSportMarketSellExample();
        standardSportMarketSellExample.createCriteria().andMatchInfoIdEqualTo(matchInfoId);
        List<StandardSportMarketSell> standardSportMarketSellList = standardSportMarketSellMapper.selectByExample(standardSportMarketSellExample);
        if (standardSportMarketSellList.size() > 0) {
            standardSportMarketSell = standardSportMarketSellList.get(0);
        }
        StandardSportTeam homeSportTeam = null;
        StandardSportTeam awaySportTeam = null;
        if (standardSportMarketSell.getTeamHomeId() != null && standardSportMarketSell.getTeamHomeId() > 0) {
            homeSportTeam = standardSportTeamMapper.selectByPrimaryKey(standardSportMarketSell.getTeamHomeId());
        }
        if (standardSportMarketSell.getTeamAwayId() != null && standardSportMarketSell.getTeamAwayId() > 0) {
            awaySportTeam = standardSportTeamMapper.selectByPrimaryKey(standardSportMarketSell.getTeamAwayId());
        }
        if (homeSportTeam != null) {
            if (StringUtils.isAnyEmpty(homeSportTeam.getName()) && homeSportTeam.getNameSpell() != null) {
                homeSportTeam.setName(homeSportTeam.getNameSpell());
            }
        }
        if (awaySportTeam != null) {
            if (StringUtils.isAnyEmpty(awaySportTeam.getName()) && awaySportTeam.getNameSpell() != null) {
                awaySportTeam.setName(awaySportTeam.getNameSpell());
            }
        }
        if (homeSportTeam != null && StringUtils.isNotEmpty(homeSportTeam.getName()) && awaySportTeam != null && StringUtils.isNotEmpty(awaySportTeam.getName())) {
            result.append(homeSportTeam.getName() + " vs " + awaySportTeam.getName());
            result.append("&&");
        }
        if (homeSportTeam != null && StringUtils.isNotEmpty(homeSportTeam.getNameSpell()) && awaySportTeam != null && StringUtils.isNotEmpty(awaySportTeam.getNameSpell())) {
            result.append(homeSportTeam.getNameSpell() + " vs " + awaySportTeam.getNameSpell());
        }
        return result.toString();
    }
@Override
    public void matchFreezeAddLog(StandardMatchInfo standardMatchInfo ,MatchSettleInfo matchSettleInfo, String forwText, MatchFreezeDto matchFreezeDto) {
        try {
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            if (standardMatchInfo == null) {
                return;
            }
            matchSettleOperateLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchSettleOperateLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());
            matchSettleOperateLog.setOperateForwText(forwText);
            matchSettleOperateLog.setOperateRearText(matchSettleInfo.getFreezeStatus() == 0 ? OperateLogTypeEnum.type_2.getCode().toString() : OperateLogTypeEnum.type_1.getCode().toString());
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.type_7.getCode() + "-" + StandardSportTypeEnum.getEnum(standardMatchInfo.getSportId()).getCode());
            matchSettleOperateLog.setOperateUserName(matchFreezeDto.getOperatorName());
            //操作参数名称
            matchSettleOperateLog.setOperateParaName("-");
            if (matchSettleInfo.getFreezeStatus() == 1) {
                if (matchFreezeDto.getMins() != null && matchFreezeDto.getMins() > 0) {
                    matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_SETTLE_10037.getCode().toString());
                    matchSettleOperateLog.setRemark(matchFreezeDto.getMins().toString());
                    matchSettleOperateLog.setOperateForwText("-");
                } else if (matchFreezeDto.getFreezeTime() != null && matchFreezeDto.getFreezeTime() != 0) {
                    SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
                    matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100139.getCode().toString());
                    matchSettleOperateLog.setOperateForwText(sdf.format(TimeUtils.millsSecondsEast8ZoneGmt()));
                    matchSettleOperateLog.setOperateRearText(sdf.format(matchFreezeDto.getFreezeTime()));
                } else {
                    matchSettleOperateLog.setOperateType(matchSettleInfo.getFreezeStatus() == 0 ?
                            OperateLogTypeEnum.type_2.getCode().toString() : OperateLogTypeEnum.type_5.getCode().toString());
                }
            } else {
                matchSettleOperateLog.setOperateType(OperateLogTypeEnum.type_2.getCode().toString());
                matchSettleOperateLog.setOperateForwText(OperateLogTypeEnum.type_1.getCode().toString());
                if (matchFreezeDto.getSettleNum() != null && !matchFreezeDto.getSettleNum().equals("0")) {
                    matchSettleOperateLog.setOperateParaName(matchFreezeDto.getSettleNum());
                }
            }
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            //操作对象id
            matchSettleOperateLog.setOperateId(standardMatchInfo.getMatchManageId());
            matchSettleOperateLog.setOperateName(standardMatchInfo.getHomeAwayInfo());
            matchSettleOperateLog.setIpAddress(matchFreezeDto.getIpAddress());


            matchSettleOperateLogRepository.save(matchSettleOperateLog);
        } catch (Exception e) {
            log.error("::"+matchFreezeDto.getLinkId()+"::matchFreezeAddLog,标准赛事ID:"+matchSettleInfo.getStandardMatchId()+" , error:",  e);

        }
    }
    @Override
    public void matchReSettleAddLog(SettleQueryDTO settleQueryDTO) {
        try {
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(settleQueryDTO.getMatchId());
            if (standardMatchInfo == null) {
                return;
            }
            matchSettleOperateLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchSettleOperateLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());
            matchSettleOperateLog.setOperateForwText("-");
            matchSettleOperateLog.setOperateRearText("-");
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.type_7.getCode() + "-" + StandardSportTypeEnum.getEnum(standardMatchInfo.getSportId()).getCode());
            matchSettleOperateLog.setOperateUserName(settleQueryDTO.getOperatorName());
            matchSettleOperateLog.setOperateType(OperateLogTypeEnum.ROLLBACK_EXECUTE.getCode().toString());
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            //操作对象id
            matchSettleOperateLog.setOperateId(standardMatchInfo.getMatchManageId());
            matchSettleOperateLog.setOperateName(standardMatchInfo.getHomeAwayInfo());
            matchSettleOperateLog.setIpAddress(settleQueryDTO.getIpAddress());
            //操作参数名称
            matchSettleOperateLog.setOperateParaName("-");

            matchSettleOperateLogRepository.save(matchSettleOperateLog);
        } catch (Exception e) {
            log.error("matchReSettleAddLog,标准赛事ID:"+settleQueryDTO.getMatchId()+" , error:", e);

        }
    }
    @Override
    public void matchSettleEventAddLog(MatchSettleEvent matchSettleEvent, String operatorName, String code, String before, String ipAddress) {
        log.info("记录结算事件日志，matchSettleEvent：{}，eventType：{}", matchSettleEvent, matchSettleEvent != null ? matchSettleEvent.getEventType() : null);
        try {
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchSettleEvent.getStandardMatchId());
            if (standardMatchInfo == null) {
                return;
            }
            Long periodId = matchSettleEvent.getPeriodId();
            SettlePeriodEnum PeriodEnum = SettlePeriodEnum.getEnum(periodId);
            if (PeriodEnum != null) matchSettleOperateLog.setPeriodId(PeriodEnum.value.longValue());
            String eventCode = matchSettleEvent.getEventCode();
            String forwT1 = "";
            if (matchSettleEvent.getT1() != null) forwT1 = matchSettleEvent.getT1().toString();
            String forwT2 = "";
            if (matchSettleEvent.getT2() != null) forwT2 = matchSettleEvent.getT2().toString();
            String rearT1 = "";
            if (matchSettleEvent.getT1() != null) rearT1 = matchSettleEvent.getT1().toString();
            String rearT2 = "";
            if (matchSettleEvent.getT2() != null) rearT2 = matchSettleEvent.getT2().toString();
            if (matchSettleEvent.getEventOrder() != null && matchSettleEvent.getEventOrder() != 0) {
                matchSettleOperateLog.setEventOrder(matchSettleEvent.getEventOrder().toString());
            }
            matchSettleOperateLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchSettleOperateLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());
            matchSettleOperateLog.setOperateForwText(forwT1 + "-" + forwT2);
            matchSettleOperateLog.setOperateRearText(rearT1 + "-" + rearT2);
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.type_7.getCode() + "-" + StandardSportTypeEnum.getEnum(matchSettleEvent.getSportId()).getCode());
            matchSettleOperateLog.setOperateUserName(operatorName);
            matchSettleOperateLog.setOperateType(code);
            matchSettleOperateLog.setIpAddress(ipAddress);
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            //创建时间加一秒  防止结算日志在确认比分之前展示
            if (code.equals(OperateLogTypeEnum.SCORE_SETTLE.getCode().toString())) {
                time += 1000L;
            }
            matchSettleOperateLog.setModifyTime(time);
            matchSettleOperateLog.setCreateTime(time);
            //操作对象id
            matchSettleOperateLog.setOperateId(matchSettleEvent.getId().toString());
            //操作参数名称
            String settleNum = matchSettleEvent.getSettleNum();
            if (standardMatchInfo.getSportId().intValue() == 2) {
                //篮球结算事件
                BasketBallSettleNumEnum ballSettleNumEnum = BasketBallSettleNumEnum.getEnum(settleNum);
                if (ballSettleNumEnum != null) {
                    matchSettleOperateLog.setOperateParaName(ballSettleNumEnum.getCode());
                }
                String periodName = MatchSettleCheckConstant.getPeriodBySettleNum(settleNum, standardMatchInfo.getMatchLength());
                if (!StringUtils.isAnyEmpty(periodName)) {
                    if (periodName.equals(BasketBallSettleNumEnum.BK_SN.getCode())) {
                        return;
                    }
                    matchSettleOperateLog.setOperateName(periodName);
                }
            } else {
                matchSettleOperateLog.setOperateName(matchSettleEvent.getEventCode());
                if ("red_card".equals(eventCode) || "yellow_card".equals(eventCode)) {
                    matchSettleOperateLog.setOperateName("fa_card");
                }
                if (MatchPeriodEnum.getEnum(settleNum) != null) {
                    matchSettleOperateLog.setOperateParaName(MatchPeriodEnum.getEnum(settleNum).getCode().toString());
                }
            }
            //角球事件处理
            if (matchSettleEvent.getEventCode().equals("corner")
                    && matchSettleEvent.getHomeAway() != null) {

                //走水為1  修改前后设为10031
                if (matchSettleEvent.getGoWaterStatus() != null && matchSettleEvent.getGoWaterStatus().equals(1)) {
                    matchSettleOperateLog.setOperateForwText(OperateLogTypeEnum.SCORES_SETTLE_10031.getCode().toString());
                    matchSettleOperateLog.setOperateRearText(OperateLogTypeEnum.SCORES_SETTLE_10031.getCode().toString());
                } else if ("home".equals(matchSettleEvent.getHomeAway()) && matchSettleEvent.getStatus() > 0) {
                    matchSettleOperateLog.setOperateForwText("1-0");
                    matchSettleOperateLog.setOperateRearText("1-0");
                } else if ("away".equals(matchSettleEvent.getHomeAway()) && matchSettleEvent.getStatus() > 0) {
                    matchSettleOperateLog.setOperateForwText("0-1");
                    matchSettleOperateLog.setOperateRearText("0-1");
                } else if ("none".equals(matchSettleEvent.getHomeAway()) && matchSettleEvent.getStatus() > 0) {
                    matchSettleOperateLog.setOperateForwText(OperateLogTypeEnum.SCORES_SETTLE_10029.getCode().toString());
                    matchSettleOperateLog.setOperateRearText(OperateLogTypeEnum.SCORES_SETTLE_10029.getCode().toString());
                } else if ("none".equals(matchSettleEvent.getHomeAway()) && matchSettleEvent.getStatus() == 0) {
                    matchSettleOperateLog.setOperateForwText("-");
                    matchSettleOperateLog.setOperateRearText("-");
                }
            }


            //进球/罚牌次序特殊处理
            List<String> list = Arrays.asList(
                    //进球次序
                    "1022", "1023", "1025", "1026",
                    //罚牌次序 上下半场,和加时赛
                    "3019", "3020", "3022", "3023");

            if (list.contains(settleNum)) {
                String homeAway = "-";
                if ("goal".equals(matchSettleOperateLog.getOperateName())) {
                    homeAway = goalProcessRest(matchSettleEvent.getHomeAway(), matchSettleEvent.getStatus());
                    //进球走水设置编码为10031
                    if (matchSettleEvent.getGoWaterStatus() != null && matchSettleEvent.getGoWaterStatus().equals(1))
                        homeAway = "10031";
                }
                if ("fa_card".equals(matchSettleOperateLog.getOperateName())) {
                    homeAway = faCardProcessRest(matchSettleEvent.getEventCode(), matchSettleEvent.getHomeAway(), matchSettleEvent.getStatus());
                    //罚牌走水罚牌编码为6
                    if (matchSettleEvent.getGoWaterStatus() != null && matchSettleEvent.getGoWaterStatus().equals(1))
                        homeAway = FaCardEnum.Method_6.getCode().toString();
                }
                matchSettleOperateLog.setOperateForwText(homeAway);
                matchSettleOperateLog.setOperateRearText(homeAway);
            }

            //进球方式和球员的比分确认和结算处理
            if (Arrays.asList("1024", "1027").contains(settleNum) &&
                    (OperateLogTypeEnum.CONFIRM_SCORE.getCode().toString().equals(code) ||
                            OperateLogTypeEnum.SCORE_SETTLE.getCode().toString().equals(code) ||
                            OperateLogTypeEnum.ROLLBACK_EXECUTE.getCode().toString().equals(code))) {
                String playerNameCode = matchSettleEvent.getPlayerNameCode() == null ? "1001" : matchSettleEvent.getPlayerNameCode();
                if ("".equals(matchSettleEvent.getPlayerNameCode())) playerNameCode = "none";
                String ExtryInfo = StringUtils.isEmpty(matchSettleEvent.getExtryInfo()) ? "1001" : matchSettleEvent.getExtryInfo();
                String info = playerNameCode + " - " + ExtryInfo;
                matchSettleOperateLog.setOperateForwText(info);
                matchSettleOperateLog.setOperateRearText(info);
            }

            //点球大战 走水处理
            if (Arrays.asList("1028", "1029").contains(settleNum) &&
                    (OperateLogTypeEnum.CONFIRM_SCORE.getCode().toString().equals(code) ||
                            OperateLogTypeEnum.SCORE_SETTLE.getCode().toString().equals(code) ||
                            OperateLogTypeEnum.ROLLBACK_EXECUTE.getCode().toString().equals(code))) {
                Integer goWaterStatus = matchSettleEvent.getGoWaterStatus();
                if (goWaterStatus != null && goWaterStatus.equals(1)) {
                    matchSettleOperateLog.setOperateForwText(OperateLogTypeEnum.SCORES_SETTLE_10031.getCode().toString());
                    matchSettleOperateLog.setOperateRearText(OperateLogTypeEnum.SCORES_SETTLE_10031.getCode().toString());
                }
            }
            //点球大战谁先踢球设置特殊处理
            if (code.equals(OperateLogTypeEnum.SCORES_SETTLE_10041.getCode().toString()) ||
                    code.equals(OperateLogTypeEnum.SCORES_SETTLE_10042.getCode().toString()) ||
                    MatchPeriodEnum.GOAL_PENALTY_33.getCode().toString().equals(matchSettleEvent.getSettleNum())) {
                String forw = "-";
                String rear = "-";
                if ("home".equals(matchSettleEvent.getHomeAway()))
                    forw = OperateLogTypeEnum.SCORES_SETTLE_10027.getCode().toString();
                if ("away".equals(matchSettleEvent.getHomeAway()))
                    forw = OperateLogTypeEnum.SCORES_SETTLE_10028.getCode().toString();
                if ("home".equals(matchSettleEvent.getHomeAway()))
                    rear = OperateLogTypeEnum.SCORES_SETTLE_10027.getCode().toString();
                if ("away".equals(matchSettleEvent.getHomeAway()))
                    rear = OperateLogTypeEnum.SCORES_SETTLE_10028.getCode().toString();
                matchSettleOperateLog.setOperateForwText(forw);
                matchSettleOperateLog.setOperateRearText(rear);
            }
            // 对于时段类型（eventType=3），不应该展示比分日志
            Integer eventType = matchSettleEvent.getEventType();
            if (eventType == null || eventType != 3) {
                // 只有非时段类型才保存比分日志
                matchSettleOperateLogRepository.save(matchSettleOperateLog);
            }
            // 无论是否保存比分日志，都需要重置ID以便后续使用
            matchSettleOperateLog.setId(null);
            log.info("确认15分钟日志==========:{}", JSONUtil.toJsonStr(matchSettleEvent));
            //5分钟再次记录日志
            // 对于次序结算（eventType=1），不应该展示5/15分钟日志
            if ((eventType == null || eventType != 1) && StrUtil.isNotEmpty(matchSettleEvent.getFiveMinSection())) {
                if("0".equals(matchSettleEvent.getFiveMinSection())){
                    extracted(matchSettleEvent, matchSettleOperateLog);
                    matchSettleOperateLogRepository.save(matchSettleOperateLog);
                }else{
                    if(FifteenMinSectionEnum.isExist(matchSettleEvent.getFiveMinSection())){
                        //传的15分钟区间，则只保存15分钟日志
                        //15分钟编辑再次记录日志
                        matchSettleOperateLog.setOperateForwText(matchSettleEvent.getFiveMinSection());
                        matchSettleOperateLog.setOperateRearText(matchSettleEvent.getFiveMinSection());
                        matchSettleOperateLogRepository.save(matchSettleOperateLog);
                    }else{
                        if("goal".equals(matchSettleEvent.getEventCode())){
                            if(StrUtil.isNotEmpty(matchSettleEvent.getFiveMinSection())){
                                matchSettleOperateLog.setOperateForwText(matchSettleEvent.getFiveMinSection());
                            }else{
                                matchSettleOperateLog.setOperateForwText("-");
                            }
                            matchSettleOperateLog.setOperateRearText(matchSettleEvent.getFiveMinSection());
                            if("0".equals(matchSettleOperateLog.getOperateForwText())){
                                extracted(matchSettleEvent, matchSettleOperateLog);
                            }
                            matchSettleOperateLogRepository.save(matchSettleOperateLog);
                        }
                        //保存15分钟日志
                        matchSettleEvent.setFifteenMinSection(calcFifteenMinSection(matchSettleEvent.getFiveMinSection()));
                        if("0".equals(matchSettleEvent.getFifteenMinSection())){
                            matchSettleOperateLog.setOperateForwText("-");
                        }else{
                            matchSettleOperateLog.setOperateForwText(matchSettleEvent.getFifteenMinSection());
                        }
                        matchSettleOperateLog.setOperateRearText(matchSettleEvent.getFifteenMinSection());
                        if("0".equals(matchSettleOperateLog.getOperateForwText())){
                            extracted(matchSettleEvent, matchSettleOperateLog);
                        }
                        if("0".equals(matchSettleOperateLog.getOperateRearText())){
                            extracted(matchSettleEvent, matchSettleOperateLog);
                        }
                        matchSettleOperateLog.setId(null);
                        matchSettleOperateLogRepository.save(matchSettleOperateLog);
                    }
                }
            }


            //二次结算再次记录日志
            if (matchSettleEvent.getSettleCount() != null && matchSettleEvent.getSettleCount() > 1 && "10017".equals(code)) {
                matchSettleOperateLog.setOperateType("10026");
                if (before.equals("80")){
                    before = OperateLogTypeEnum.SETTLE_REASON_80.getCode().toString();
                }
                if (before.equals("81")){
                    before = OperateLogTypeEnum.SETTLE_REASON_81.getCode().toString();
                }
                if (before.equals("82")){
                    before = OperateLogTypeEnum.SETTLE_REASON_82.getCode().toString();
                }
                if (before.equals("83")){
                    before = OperateLogTypeEnum.SETTLE_REASON_83.getCode().toString();
                }
                if (before.equals("84")){
                    before = OperateLogTypeEnum.SETTLE_REASON_84.getCode().toString();
                }


                matchSettleOperateLog.setOperateForwText(before);

                Integer settleReason =matchSettleEvent.getSettleReason();
                if (settleReason == 80){
                    settleReason = OperateLogTypeEnum.SETTLE_REASON_80.getCode();
                }
                if (settleReason == 81){
                    settleReason = OperateLogTypeEnum.SETTLE_REASON_81.getCode();
                }
                if (settleReason == 82){
                    settleReason = OperateLogTypeEnum.SETTLE_REASON_82.getCode();
                }
                if (settleReason == 83){
                    settleReason = OperateLogTypeEnum.SETTLE_REASON_83.getCode();
                }
                if (settleReason == 84){
                    settleReason = OperateLogTypeEnum.SETTLE_REASON_84.getCode();
                }

                matchSettleOperateLog.setOperateRearText(settleReason.toString());
//                //二次结算原因为"其他" 将拼接详细原因
//                if (matchSettleEvent.getSettleReason() == 118) {
//                    matchSettleOperateLog.setOperateRearText(matchSettleEvent.getSettleReason().toString() + ": " + matchSettleEvent.getSettleReasonDetail());
//                }
                matchSettleOperateLog.setId(null);
                matchSettleOperateLogRepository.save(matchSettleOperateLog);
            }


        } catch (Exception e) {
            log.error("matchSettleEventAddLog :"+code+",标准赛事ID:"+matchSettleEvent.getStandardMatchId()+", error:",  e);

        }
    }
    String goalProcessRest(String homeAway, Integer status) {
        if ("no goal".equals(homeAway) || "none".equals(homeAway)) {
            if (status == 0) {
                return "-";
            } else {
                return OperateLogTypeEnum.type_6.getCode().toString();
            }
        }
        if ("home".equals(homeAway)) return OperateLogTypeEnum.SCORES_SETTLE_10027.getCode().toString();
        if ("away".equals(homeAway)) return OperateLogTypeEnum.SCORES_SETTLE_10028.getCode().toString();

        return homeAway;
    }
    String faCardProcessRest(String eventCode, String homeAway, Integer status) {
        if (FaCardEnum.Method_5.getMsg().equals(homeAway)) {
            if (status == 0) {
                return "-";
            } else {
                return FaCardEnum.Method_5.getCode().toString();
            }
        }
        if ("home".equals(homeAway) && "yellow_card".equals(eventCode)) return FaCardEnum.Method_1.getCode().toString();
        if ("away".equals(homeAway) && "yellow_card".equals(eventCode)) return FaCardEnum.Method_2.getCode().toString();
        if ("home".equals(homeAway) && "red_card".equals(eventCode)) return FaCardEnum.Method_3.getCode().toString();
        if ("away".equals(homeAway) && "red_card".equals(eventCode)) return FaCardEnum.Method_4.getCode().toString();
        return homeAway;
    }
    private void extracted(MatchSettleEvent matchSettleEvent, MatchSettleOperateLogEntity matchSettleOperateLog) {
        if("goal".equals(matchSettleEvent.getEventCode())){
            matchSettleOperateLog.setOperateForwText(OperateLogTypeEnum.type_6.getCode().toString());
            matchSettleOperateLog.setOperateRearText(OperateLogTypeEnum.type_6.getCode().toString());
        }else if("corner".equals(matchSettleEvent.getEventCode())){
            matchSettleOperateLog.setOperateForwText(OperateLogTypeEnum.SCORES_SETTLE_10029.getCode().toString());
            matchSettleOperateLog.setOperateRearText(OperateLogTypeEnum.SCORES_SETTLE_10029.getCode().toString());
        }else if("fa_card".equals(matchSettleEvent.getEventCode())){
            matchSettleOperateLog.setOperateForwText(FaCardEnum.Method_5.getCode().toString());
            matchSettleOperateLog.setOperateRearText(FaCardEnum.Method_5.getCode().toString());
        }else{
            matchSettleOperateLog.setOperateForwText("None");
            matchSettleOperateLog.setOperateRearText("None");
        }
    }
@Override
    public void matchSettleEventAddLog(MatchSettleEvent matchSettleEvent, MatchSettleEvent newMatchSettleEvent, String operatorName, OperateLogTypeEnum type, String ipAddress) {
        log.info("记录结算事件日志（编辑），matchSettleEvent：{}，newMatchSettleEvent：{}，eventType：{}", matchSettleEvent, newMatchSettleEvent, newMatchSettleEvent != null ? newMatchSettleEvent.getEventType() : null);
        String code = type.getCode().toString();
        Long periodId = newMatchSettleEvent.getPeriodId();
        try {
            String forwT1 = "";
            if (matchSettleEvent.getT1() != null) forwT1 = matchSettleEvent.getT1().toString();
            String forwT2 = "";
            if (matchSettleEvent.getT2() != null) forwT2 = matchSettleEvent.getT2().toString();
            String rearT1 = "";
            if (newMatchSettleEvent.getT1() != null) rearT1 = newMatchSettleEvent.getT1().toString();
            String rearT2 = "";
            if (newMatchSettleEvent.getT2() != null) rearT2 = newMatchSettleEvent.getT2().toString();

            String eventCode = newMatchSettleEvent.getEventCode();
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchSettleEvent.getStandardMatchId());
            if (standardMatchInfo == null) {
                return;
            }

            if (newMatchSettleEvent.getEventOrder() != null && newMatchSettleEvent.getEventOrder() != 0) {
                matchSettleOperateLog.setEventOrder(newMatchSettleEvent.getEventOrder().toString());
            }
            SettlePeriodEnum PeriodEnum = SettlePeriodEnum.getEnum(periodId);
            if (PeriodEnum != null) matchSettleOperateLog.setPeriodId(PeriodEnum.value);
            matchSettleOperateLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchSettleOperateLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());
            matchSettleOperateLog.setOperateForwText(forwT1 + "-" + forwT2);

            matchSettleOperateLog.setOperateRearText(rearT1 + "-" + rearT2);
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.type_7.getCode() + "-" + StandardSportTypeEnum.getEnum(matchSettleEvent.getSportId()).getCode());
            matchSettleOperateLog.setOperateUserName(operatorName);
            matchSettleOperateLog.setOperateType(code);
            matchSettleOperateLog.setIpAddress(ipAddress);
            if (code.equals("10003"))
                matchSettleOperateLog.setOperateType(OperateLogTypeEnum.EDIT.getCode().toString());


            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            //操作对象id
            matchSettleOperateLog.setOperateId(newMatchSettleEvent.getId().toString());
            matchSettleOperateLog.setOperateName(eventCode);

            //设置球员和进球方式特殊处理
            if (type.getCode().equals(10003)) {
                String PlayerBefor = "";
                if (!StringUtils.isEmpty(matchSettleEvent.getPlayerNameCode())) {
                    PlayerBefor = matchSettleEvent.getPlayerNameCode();
                } else if ("".equals(matchSettleEvent.getPlayerNameCode())) {
                    PlayerBefor = "none";
                } else if (matchSettleEvent.getPlayerNameCode() == null) {
                    PlayerBefor = GoalTypeEnum.UNSELECTED.getCode().toString();
                }

                String goalBefor = "";
                if (matchSettleEvent.getExtryInfo() != null) {
                    goalBefor = matchSettleEvent.getExtryInfo();
                } else {
                    if (StringUtils.isEmpty(goalBefor)) goalBefor = GoalTypeEnum.UNSELECTED.getCode().toString();
                }
                String PlayerRear = "";
                if (!StringUtils.isEmpty(newMatchSettleEvent.getPlayerNameCode())) {
                    PlayerRear = newMatchSettleEvent.getPlayerNameCode();
                } else if ("".equals(newMatchSettleEvent.getPlayerNameCode())) {
                    PlayerRear = "none";
                } else if (newMatchSettleEvent.getPlayerNameCode() == null) {
                    PlayerRear = GoalTypeEnum.UNSELECTED.getCode().toString();
                }


                String goalRear = "";
                if (newMatchSettleEvent.getExtryInfo() != null) {
                    goalRear = newMatchSettleEvent.getExtryInfo();
                    if (StringUtils.isEmpty(goalRear)) goalRear = GoalTypeEnum.UNSELECTED.getCode().toString();
                }

                matchSettleOperateLog.setOperateForwText(PlayerBefor + " - " + goalBefor);
                matchSettleOperateLog.setOperateRearText(PlayerRear + " - " + goalRear);
                //两个修改前都是未选择  展示 "-"
                if ("1001".equals(PlayerBefor) && "1001".equals(goalBefor)) {
                    matchSettleOperateLog.setOperateForwText("-");
                }
            }

            //角球事件处理
            if (newMatchSettleEvent.getEventCode().equals("corner")
                    && newMatchSettleEvent.getHomeAway() != null) {
                CornerProcessRest(matchSettleEvent, newMatchSettleEvent, matchSettleOperateLog);
            }

            //罚牌事件处理
            if (newMatchSettleEvent.getEventCode().equals("fa_card")
                    && newMatchSettleEvent.getHomeAway() != null
                    && newMatchSettleEvent.getStatus() > 0) {
                matchSettleOperateLog.setOperateForwText(matchSettleEvent.getHomeAway());
                matchSettleOperateLog.setOperateRearText(newMatchSettleEvent.getHomeAway());
                //设置走水
                if (matchSettleEvent.getGoWaterStatus() != null && matchSettleEvent.getGoWaterStatus().equals(1))
                    matchSettleOperateLog.setOperateForwText("6");
                if (newMatchSettleEvent.getGoWaterStatus() != null && newMatchSettleEvent.getGoWaterStatus().equals(1))
                    matchSettleOperateLog.setOperateRearText("6");

            }

            //进球事件特殊处理
            List<String> list = Arrays.asList("1022", "1023", "1025", "1026");
            //罚牌事件特殊处理
            List<String> faList = Arrays.asList("3019", "3020", "3022", "3023");
            //角球事件特殊处理
            List<String> coList = Arrays.asList("204", "205", "209", "2010");
            //点球大战事件特殊处理
            List<String> penaltyList = Arrays.asList("1028", "1029", "1030", "1054");


            if ((newMatchSettleEvent.getEventCode().equals("goal")
                    && newMatchSettleEvent.getStatus() > 0 && !type.getCode().equals(10003))
                    || (type.getCode() == 10019)) {

                //不是点球大战
                if (!penaltyList.contains(newMatchSettleEvent.getSettleNum())) {
                    matchSettleOperateLog.setOperateForwText(matchSettleEvent.getHomeAway());
                    matchSettleOperateLog.setOperateRearText(newMatchSettleEvent.getHomeAway());
                } else {
                    Integer goWaterStatus = matchSettleEvent.getGoWaterStatus();
                    Integer newGoWaterStatus = newMatchSettleEvent.getGoWaterStatus();
                    if (goWaterStatus != null && goWaterStatus.equals(1))
                        matchSettleOperateLog.setOperateForwText(OperateLogTypeEnum.SCORES_SETTLE_10031.getCode().toString());
                    if (newGoWaterStatus != null && newGoWaterStatus.equals(1))
                        matchSettleOperateLog.setOperateRearText(OperateLogTypeEnum.SCORES_SETTLE_10031.getCode().toString());
                }

                //进球次序
                if (list.contains(newMatchSettleEvent.getSettleNum())) {
                    String ForwText = goalProcessRest(matchSettleEvent.getHomeAway(), matchSettleEvent.getStatus());
                    String RearText = goalProcessRest(newMatchSettleEvent.getHomeAway(), newMatchSettleEvent.getStatus());
                    if (matchSettleEvent.getGoWaterStatus() != null && matchSettleEvent.getGoWaterStatus().equals(1))
                        ForwText = "10031";
                    if (newMatchSettleEvent.getGoWaterStatus() != null && newMatchSettleEvent.getGoWaterStatus().equals(1))
                        RearText = "10031";
                    matchSettleOperateLog.setOperateForwText(ForwText);
                    matchSettleOperateLog.setOperateRearText(RearText);
                }

                //角球次序
                if (coList.contains(newMatchSettleEvent.getSettleNum())) {
                    CornerProcessRest(matchSettleEvent, newMatchSettleEvent, matchSettleOperateLog);
                }
                //罚牌次序
                if (faList.contains(newMatchSettleEvent.getSettleNum())) {
                    String ForwText = faCardProcessRest(matchSettleEvent.getEventCode(), matchSettleEvent.getHomeAway(), matchSettleEvent.getStatus());
                    String RearText = faCardProcessRest(newMatchSettleEvent.getEventCode(), newMatchSettleEvent.getHomeAway(), newMatchSettleEvent.getStatus());
                    if (matchSettleEvent.getGoWaterStatus() != null && matchSettleEvent.getGoWaterStatus().equals(1))
                        ForwText = "6";
                    if (newMatchSettleEvent.getGoWaterStatus() != null && newMatchSettleEvent.getGoWaterStatus().equals(1))
                        RearText = "6";
                    matchSettleOperateLog.setOperateForwText(ForwText);
                    matchSettleOperateLog.setOperateRearText(RearText);
                }
                //球员和进球方式
                if (Arrays.asList("1024", "1027").contains(newMatchSettleEvent.getSettleNum())) {
                    matchSettleOperateLog.setOperateForwText(matchSettleEvent.getPlayerNameCode() + " - " + matchSettleEvent.getExtryInfo());
                    String playerNameCode = matchSettleEvent.getPlayerNameCode() == null ? GoalTypeEnum.UNSELECTED.getCode().toString() : matchSettleEvent.getPlayerNameCode();
                    if ("".equals(matchSettleEvent.getPlayerNameCode())) {
                        playerNameCode = "none";
                    }
                    String ExtryInfo = StringUtils.isEmpty(matchSettleEvent.getExtryInfo()) ?
                            GoalTypeEnum.UNSELECTED.getCode().toString() : matchSettleEvent.getExtryInfo();
                    String playerNameCodeNew = newMatchSettleEvent.getPlayerNameCode() == null ? GoalTypeEnum.UNSELECTED.getCode().toString() : newMatchSettleEvent.getPlayerNameCode();
                    if ("".equals(newMatchSettleEvent.getPlayerNameCode())) {
                        playerNameCodeNew = "none";
                    }
                    String ExtryInfoNew = StringUtils.isEmpty(newMatchSettleEvent.getExtryInfo()) ?
                            GoalTypeEnum.UNSELECTED.getCode().toString() : newMatchSettleEvent.getExtryInfo();

                    matchSettleOperateLog.setOperateForwText(playerNameCode + " - " + ExtryInfo);
                    matchSettleOperateLog.setOperateRearText(playerNameCodeNew + " - " + ExtryInfoNew);
                    //进球方式和球员都是未选择的 只显示 "-"
                    if ("1001".equals(playerNameCodeNew) && "1001".equals(ExtryInfoNew)) {
                        matchSettleOperateLog.setOperateRearText("-");
                    }
                }

                //点球大战谁先踢球设置特殊处理
                if (code.equals(OperateLogTypeEnum.SCORES_SETTLE_10041.getCode().toString()) ||
                        code.equals(OperateLogTypeEnum.SCORES_SETTLE_10042.getCode().toString()) ||
                        //点球大战事件回滚
                        (MatchPeriodEnum.GOAL_PENALTY_33.getCode().toString().equals(newMatchSettleEvent.getSettleNum()) &&
                                OperateLogTypeEnum.ROLLBACK_SCORES_SETTLE.getCode() == type.getCode())) {
                    String forw = "-";
                    String rear = "-";
                    if ("home".equals(matchSettleEvent.getHomeAway()))
                        forw = OperateLogTypeEnum.SCORES_SETTLE_10027.getCode().toString();
                    if ("away".equals(matchSettleEvent.getHomeAway()))
                        forw = OperateLogTypeEnum.SCORES_SETTLE_10028.getCode().toString();
                    if ("home".equals(newMatchSettleEvent.getHomeAway()))
                        rear = OperateLogTypeEnum.SCORES_SETTLE_10027.getCode().toString();
                    if ("away".equals(newMatchSettleEvent.getHomeAway()))
                        rear = OperateLogTypeEnum.SCORES_SETTLE_10028.getCode().toString();
                    matchSettleOperateLog.setOperateForwText(forw);
                    matchSettleOperateLog.setOperateRearText(rear);
                }


            }


            //开球进入
            if (StringUtils.isEmpty(eventCode) || "kick_off".equals(eventCode))
                matchSettleOperateLog.setOperateName("goal");
            if ("red_card".equals(eventCode) || "yellow_card".equals(eventCode))
                matchSettleOperateLog.setOperateName("fa_card");

            //操作参数名称
            String settleNum = newMatchSettleEvent.getSettleNum();
            if (standardMatchInfo.getSportId().intValue() == 2) {
                //篮球结算事件
                if (BasketBallSettleNumEnum.getEnum(settleNum) != null) {
                    matchSettleOperateLog.setOperateParaName(BasketBallSettleNumEnum.getEnum(settleNum).getCode());
                }
            } else {
                if (MatchPeriodEnum.getEnum(settleNum) != null) {
                    matchSettleOperateLog.setOperateParaName(MatchPeriodEnum.getEnum(settleNum).getCode().toString());
                }
            }
            log.info("开始保存15分钟日志==========1:{}", JSONUtil.toJsonStr(matchSettleOperateLog));
            // 对于时段类型（eventType=3），不应该展示比分日志
            Integer eventType = newMatchSettleEvent.getEventType();
            if (eventType == null || eventType != 3) {
                // 只有非时段类型才保存比分日志
                matchSettleOperateLog.setId(null);
                matchSettleOperateLogRepository.save(matchSettleOperateLog);
            }
            // 无论是否保存比分日志，都需要重置ID以便后续使用
            matchSettleOperateLog.setId(null);

            //5分钟编辑再次记录日志//角球罚牌只有15分
            // 对于次序结算（eventType=1），不应该展示5/15分钟日志
            if ((eventType == null || eventType != 1) && "goal".equals(newMatchSettleEvent.getEventCode())){
                if  (StrUtil.isNotEmpty(newMatchSettleEvent.getFiveMinSection())) {
                    //15分钟区间值，不保存5分钟日志
                    if(!FifteenMinSectionEnum.isExist(newMatchSettleEvent.getFiveMinSection())){
                        if (matchSettleEvent.getFiveMinSection() != null && !"".equals(matchSettleEvent.getFiveMinSection())) {
                            matchSettleOperateLog.setOperateForwText(matchSettleEvent.getFiveMinSection());
                        }else {
                            matchSettleOperateLog.setOperateForwText("-");
                        }
                        matchSettleOperateLog.setOperateRearText(newMatchSettleEvent.getFiveMinSection());
                        log.info("开始保存15分钟日志1.8::{}",JSONObject.toJSON(matchSettleOperateLog));
                        if("0".equals(matchSettleOperateLog.getOperateForwText())){
                            matchSettleOperateLog.setOperateForwText("-");
                        }
                        if("0".equals(matchSettleOperateLog.getOperateRearText())){
                            matchSettleOperateLog.setOperateRearText(OperateLogTypeEnum.type_6.getCode().toString());
                        }
                        log.info("开始保存15分钟日志==========2:{}", JSONUtil.toJsonStr(matchSettleOperateLog));
                        matchSettleOperateLog.setId(null);
                        matchSettleOperateLogRepository.save(matchSettleOperateLog);
                    }
                }
            }
            log.info("开始保存15分钟日志==========3:{}", JSONUtil.toJsonStr(matchSettleOperateLog));
            //15分钟编辑再次记录日志
            // 对于次序结算（eventType=1），不应该展示15分钟日志
            // 对于回滚操作，即使回滚后的时段信息为空，如果回滚前有值，也应该记录日志
            boolean isRollback = OperateLogTypeEnum.ROLLBACK_SCORES_SETTLE.getCode() == type.getCode();
            boolean hasNewPeriodInfo = StrUtil.isNotEmpty(newMatchSettleEvent.getFifteenMinSection()) || StrUtil.isNotEmpty(newMatchSettleEvent.getFiveMinSection());
            boolean hasOldPeriodInfo = StrUtil.isNotEmpty(matchSettleEvent.getFifteenMinSection()) || StrUtil.isNotEmpty(matchSettleEvent.getFiveMinSection());
            // 对于时段事件（eventType=3）的回滚，即使回滚后为空，只要回滚前有值，也应该记录
            if ((eventType == null || eventType != 1) && (hasNewPeriodInfo || hasOldPeriodInfo || (isRollback && eventType != null && eventType == 3 && hasOldPeriodInfo))) {
                // 设置修改前的时段信息
                if (StrUtil.isNotEmpty(matchSettleEvent.getFifteenMinSection())) {
                    matchSettleOperateLog.setOperateForwText(matchSettleEvent.getFifteenMinSection());
                } else if (StrUtil.isNotEmpty(matchSettleEvent.getFiveMinSection())) {
                    //因为15分钟区间未入库，根据5分钟区间计算15分钟区间
                    matchSettleOperateLog.setOperateForwText(calcFifteenMinSection(matchSettleEvent.getFiveMinSection()));
                }else {
                    matchSettleOperateLog.setOperateForwText("-");
                }
                
                // 设置修改后的时段信息
                if (StrUtil.isNotEmpty(newMatchSettleEvent.getFifteenMinSection())) {
                    matchSettleOperateLog.setOperateRearText(newMatchSettleEvent.getFifteenMinSection());
                } else if (StrUtil.isNotEmpty(newMatchSettleEvent.getFiveMinSection())) {
                    //因为15分钟区间未入库，根据5分钟区间计算15分钟区间
                    matchSettleOperateLog.setOperateRearText(calcFifteenMinSection(newMatchSettleEvent.getFiveMinSection()));
                }else {
                    matchSettleOperateLog.setOperateRearText("-");
                }
                
                //编辑 前 -
                if("0".equals(matchSettleOperateLog.getOperateForwText())){
                    matchSettleOperateLog.setOperateForwText("-");
                }
                // 对于时段事件（eventType=3），当时段信息为"0"时，应该设置为"-"，而不是使用比分相关的逻辑
                if("0".equals(matchSettleOperateLog.getOperateRearText())){
                    // 对于时段事件，只使用时段信息，不使用比分信息
                    if (eventType != null && eventType == 3) {
                        matchSettleOperateLog.setOperateRearText("-");
                    } else {
                        // 对于非时段事件，使用原有的比分逻辑
                        if("goal".equals(matchSettleEvent.getEventCode())){
                            matchSettleOperateLog.setOperateRearText(OperateLogTypeEnum.type_6.getCode().toString());
                        }else if("corner".equals(matchSettleEvent.getEventCode())){
                            matchSettleOperateLog.setOperateRearText(OperateLogTypeEnum.SCORES_SETTLE_10029.getCode().toString());
                        }else if("fa_card".equals(matchSettleEvent.getEventCode())){
                            matchSettleOperateLog.setOperateRearText(FaCardEnum.Method_5.getCode().toString());
                        }
                    }
                }
                log.info("开始保存15分钟日志==========3.5:{}", JSONUtil.toJsonStr(matchSettleOperateLog));
                matchSettleOperateLog.setId(null);
                matchSettleOperateLogRepository.save(matchSettleOperateLog);
            }

        } catch (Exception e) {
            log.error("matchSettleEventAddLog :"+type+",标准赛事ID:"+matchSettleEvent.getStandardMatchId()+", error:", e);

        }
    }
    @Override
    public void matchSettleCheckEventAddLog(MatchSettleCheckInfoEntity oIdInfo, MatchSettleCheckInfoEntity newInfo,
                                            UpdateMatchSettleScoreDto dto, OperateLogTypeEnum enums,
                                            String settleNum, Integer checkNumber, Integer eventType) {
        log.info("记录结算核对事件日志，settleNum：{}，eventType：{}，oIdInfo：{}，newInfo：{}", settleNum, eventType, oIdInfo, newInfo);
        try {
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(newInfo.getStandardMatchId());
            if (standardMatchInfo == null) {
                return;
            }

            //操作对象转换
            String eventCode = newInfo.getEventCode();

            String forT1 = "";
            if (oIdInfo.getT1() != null) forT1 = oIdInfo.getT1().toString();
            String forT2 = "";
            if (oIdInfo.getT2() != null) forT2 = oIdInfo.getT2().toString();

            String rearT1 = "";
            if (newInfo.getT1() != null) rearT1 = newInfo.getT1().toString();
            String rearT2 = "";
            if (newInfo.getT2() != null) rearT2 = newInfo.getT2().toString();


            matchSettleOperateLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchSettleOperateLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());

            //点球大战谁先踢球设置特殊处理
            if (enums.getCode().equals(OperateLogTypeEnum.SCORES_SETTLE_10040.getCode())
                    || (settleNum.equals(MatchPeriodEnum.GOAL_PENALTY_33.getCode().toString()))) {
                String forw = "-";
                String rear = "-";
                if ("home".equals(oIdInfo.getHomeAway()))
                    forw = OperateLogTypeEnum.SCORES_SETTLE_10027.getCode().toString();
                if ("away".equals(oIdInfo.getHomeAway()))
                    forw = OperateLogTypeEnum.SCORES_SETTLE_10028.getCode().toString();
                if ("home".equals(newInfo.getHomeAway()))
                    rear = OperateLogTypeEnum.SCORES_SETTLE_10027.getCode().toString();
                if ("away".equals(newInfo.getHomeAway()))
                    rear = OperateLogTypeEnum.SCORES_SETTLE_10028.getCode().toString();
                matchSettleOperateLog.setOperateForwText(forw);
                matchSettleOperateLog.setOperateRearText(rear);
            } else {
                matchSettleOperateLog.setOperateForwText(forT1 + "-" + forT2);
                matchSettleOperateLog.setOperateRearText(rearT1 + "-" + rearT2);
            }
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.type_7.getCode() + "-" + StandardSportTypeEnum.getEnum(dto.getSportId()).getCode());
            matchSettleOperateLog.setOperateUserName(dto.getOperatorName() + ",(第" + checkNumber + "人)");
            matchSettleOperateLog.setOperateType(enums.getCode().toString());
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setIpAddress(dto.getIpAddress());
            if (newInfo.getEventOrder() != null && newInfo.getEventOrder() != 0) {
                matchSettleOperateLog.setEventOrder(newInfo.getEventOrder().toString());
            }
            matchSettleOperateLog.setOperateId(newInfo.getId().toString());
            //操作对象id

            matchSettleOperateLog.setOperateName(eventCode);
            //操作参数名称
            if (standardMatchInfo.getSportId().intValue() == 2) {
                //篮球结算事件
                if (BasketBallSettleNumEnum.getEnum(settleNum) != null) {
                    matchSettleOperateLog.setOperateParaName(BasketBallSettleNumEnum.getEnum(settleNum).getCode());
                }
            } else {
                if (MatchPeriodEnum.getEnum(settleNum) != null) {
                    matchSettleOperateLog.setOperateParaName(MatchPeriodEnum.getEnum(settleNum).getCode().toString());
                }
            }
            if (StringUtils.isEmpty(eventCode) || "kick_off".equals(eventCode)) {
                matchSettleOperateLog.setOperateName("goal");
            }
            if ("red_card".equals(eventCode) || "yellow_card".equals(eventCode))
                matchSettleOperateLog.setOperateName("fa_card");

            //角球比分特殊处理
            List<String> coList = Arrays.asList("204", "205", "209", "2010");

            //进球/罚牌次序特殊处理
            List<String> list = Arrays.asList(
                    //进球次序
                    "1022", "1023", "1025", "1026",
                    //罚牌次序 上下半场,和加时赛
                    "3019", "3020", "3022", "3023");
            //点球大战事件特殊处理
            List<String> penaltyList = Arrays.asList("1028", "1029", "1030");


            //角球次序
            if (coList.contains(settleNum)) {
                CheckInfoCornerProcessRest(oIdInfo, newInfo, matchSettleOperateLog);
            }

            //进球,罚牌次序事件特殊处理
            if (list.contains(settleNum)) {
                String homeAwayForw = "-";
                String homeAwayRest = "-";
                if ("goal".equals(dto.getEventCode())) {
                    homeAwayForw = goalProcessRest(oIdInfo.getHomeAway(), oIdInfo.getCheckStatus());
                    homeAwayRest = goalProcessRest(newInfo.getHomeAway(), newInfo.getCheckStatus());
                    //进球走水设置编码为10031
                    if (oIdInfo.getGoWaterStatus() != null && oIdInfo.getGoWaterStatus().equals(1))
                        homeAwayForw = OperateLogTypeEnum.SCORES_SETTLE_10031.getCode().toString();
                    if (newInfo.getGoWaterStatus() != null && newInfo.getGoWaterStatus().equals(1))
                        homeAwayRest = OperateLogTypeEnum.SCORES_SETTLE_10031.getCode().toString();
                }
                if ("fa_card".equals(matchSettleOperateLog.getOperateName())) {
                    homeAwayForw = faCardProcessRest(oIdInfo.getEventCode(), oIdInfo.getHomeAway(), oIdInfo.getCheckStatus());
                    homeAwayRest = faCardProcessRest(newInfo.getEventCode(), newInfo.getHomeAway(), newInfo.getCheckStatus());
                    //罚牌走水罚牌编码为6
                    if (oIdInfo.getGoWaterStatus() != null && oIdInfo.getGoWaterStatus().equals(1))
                        homeAwayForw = Method_6.getCode().toString();
                    if (newInfo.getGoWaterStatus() != null && newInfo.getGoWaterStatus().equals(1))
                        homeAwayRest = Method_6.getCode().toString();
                }
                matchSettleOperateLog.setOperateForwText(homeAwayForw);
                matchSettleOperateLog.setOperateRearText(homeAwayRest);
            }

            //点球大战特殊处理
            if (penaltyList.contains(settleNum)) {
                Integer goWaterStatus = oIdInfo.getGoWaterStatus();
                Integer newGoWaterStatus = newInfo.getGoWaterStatus();
                if (goWaterStatus != null && goWaterStatus.equals(1))
                    matchSettleOperateLog.setOperateForwText(OperateLogTypeEnum.SCORES_SETTLE_10031.getCode().toString());
                if (newGoWaterStatus != null && newGoWaterStatus.equals(1))
                    matchSettleOperateLog.setOperateRearText(OperateLogTypeEnum.SCORES_SETTLE_10031.getCode().toString());
            }

            // eventType=3 时段事件：只记录 5/15 分钟，不记录比分
            if (eventType != null && eventType == 1) {
                matchSettleOperateLogRepository.save(matchSettleOperateLog);
            }


            //5分钟编辑再次记录日志
            if (newInfo.getFiveMinSection() != null && !"".equals(newInfo.getFiveMinSection())) {
                // 5分钟区间
                if(!FifteenMinSectionEnum.isExist(newInfo.getFiveMinSection())){
                    if (oIdInfo.getFiveMinSection() != null && !"".equals(oIdInfo.getFiveMinSection())) {
                        matchSettleOperateLog.setOperateForwText(oIdInfo.getFiveMinSection());
                    }else {
                        matchSettleOperateLog.setOperateForwText("-");
                    }
                    matchSettleOperateLog.setOperateRearText(newInfo.getFiveMinSection());
                    matchSettleOperateLog.setId(null);
                    log.info("15min1:"+matchSettleOperateLog);
                    matchSettleOperateLogRepository.save(matchSettleOperateLog);
                }
            }

            //15分钟编辑再次记录日志
            if (newInfo.getFiveMinSection() != null && !"".equals(newInfo.getFiveMinSection())) {
                //15分钟区间的值
                if(FifteenMinSectionEnum.isExist(newInfo.getFiveMinSection())){
                    matchSettleOperateLog.setOperateForwText(newInfo.getFiveMinSection());
                    matchSettleOperateLog.setOperateRearText(newInfo.getFiveMinSection());
                }else{
                    newInfo.setFifteenMinSection(calcFifteenMinSection(newInfo.getFiveMinSection()));
                    matchSettleOperateLog.setOperateForwText(newInfo.getFifteenMinSection());
                    matchSettleOperateLog.setOperateRearText(newInfo.getFifteenMinSection());
                }
                matchSettleOperateLog.setId(null);
                log.info("15min2:"+matchSettleOperateLog);
                matchSettleOperateLogRepository.save(matchSettleOperateLog);
            }

        } catch (Exception e) {
            log.error("matchSettleCheckEventAddLog,标准赛事ID:"+dto.getStandardMatchId()+" , error:", e);

        }
    }
    @Override
    public void settleMentionLog(Object object, SettleEventDeleteRequest settleEventDeleteRequest) {
        try {
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(settleEventDeleteRequest.getMatchId());
            if (standardMatchInfo == null) {
                return;
            }
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            matchSettleOperateLog.setOperateId(String.valueOf(settleEventDeleteRequest.getMatchScoreId()));
            matchSettleOperateLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.type_7.getCode() + "-" + StandardSportTypeEnum.getEnum(standardMatchInfo.getSportId()).getCode());
            SettleMentionEnum mentionType = SettleMentionEnum.getEnumByMentionCode(settleEventDeleteRequest.getMentionType());
            matchSettleOperateLog.setOperateType(mentionType == null ? null : mentionType.getName());
            matchSettleOperateLog.setOperateName("-");
            matchSettleOperateLog.setOperateForwText("on");
            matchSettleOperateLog.setOperateRearText("off");
            matchSettleOperateLog.setOperateParaName("-");
            matchSettleOperateLog.setOperateUserName(settleEventDeleteRequest.getOperatorName());
            matchSettleOperateLog.setIpAddress(settleEventDeleteRequest.getIpAddress());
            Long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchSettleOperateLog.setModifyTime(time);
            matchSettleOperateLog.setCreateTime(time);
            matchSettleOperateLogRepository.save(matchSettleOperateLog);
        } catch (Exception e) {
            log.error("[MatchSettleLogServiceImpl] settleMentionLog with parameter {} error: ", settleEventDeleteRequest, e);
        }
    }
}
