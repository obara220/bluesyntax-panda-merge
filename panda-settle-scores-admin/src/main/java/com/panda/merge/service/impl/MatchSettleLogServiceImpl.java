package com.panda.merge.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.panda.merge.common.enums.*;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.constant.MatchSettleCheckConstant;
import com.panda.merge.constant.SettleMentionEnum;
import com.panda.merge.constant.SettleTemplateTypeEnum;
import com.panda.merge.dto.*;
import com.panda.merge.dto.advertise.*;
import com.panda.merge.dto.settle.*;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.mapper.StandardSportMarketSellLogMapper;
import com.panda.merge.mapper.StandardSportTeamMapper;
import com.panda.merge.model.*;
import com.panda.merge.service.IMatchSettleLogService;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportMarketSellService;
import com.panda.merge.service.StandardSportTournamentService;
import com.panda.merge.util.CategoryUtils;
import com.panda.merge.utils.CompareUtils;
import com.panda.merge.utils.SettleTemplateJsonUtils;
import com.panda.merge.v2.entity.*;
import com.panda.merge.v2.mapper.MatchSettleOperateLogV3Mapper;
import com.panda.merge.v2.mapper.MatchSettleTemplateRelationV2Mapper;
import com.panda.merge.v2.mapper.MatchSettleTemplateV2Mapper;
import com.panda.merge.v2.repository.MatchSettleOperateLogV2Repository;
import com.panda.merge.v2.repository.MatchSettleTemplateRepository;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.common.enums.FaCardEnum.Method_6;

@Service
@Slf4j
public class MatchSettleLogServiceImpl implements IMatchSettleLogService {

    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    MatchSettleOperateLogV3Mapper matchSettleOperateLogMapper;
    @Autowired
    MatchSettleOperateLogV2Repository matchSettleOperateLogRepository;

    @Autowired
    StandardSportTeamMapper standardSportTeamMapper;
    @Autowired
    MatchSettleTemplateV2Mapper matchSettleTemplateV2Mapper;
    @Autowired
    MatchSettleTemplateRepository matchSettleTemplateRepository;
    //    @Autowired
//    StandardSportTournamentMapper standardSportTournamentMapper;
    @Autowired
    MatchSettleTemplateRelationV2Mapper matchSettleTemplateRelationV2Mapper;
    @Autowired
    StandardSportMarketSellLogMapper standardSportMarketSellLogMapper;
    @Autowired
    StandardMatchInfoService standardMatchInfoService;
    @Autowired
    StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    StandardSportTournamentService standardSportTournamentService;

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


            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
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

            matchSettleOperateLogRepository.save(matchSettleOperateLog);

            //二次结算再次记录日志
            if (matchSettleScore.getSettleCount() != null && matchSettleScore.getSettleCount() > 1 && type.getCode() == 10017) {
                matchSettleOperateLog.setOperateType("10026");
                matchSettleOperateLog.setOperateForwText(beforeText);
                matchSettleOperateLog.setOperateRearText(matchSettleScore.getSettleReason().toString());
                //二次结算原因为"其他" 将拼接详细原因
                if (matchSettleScore.getSettleReason() == 118) {
                    matchSettleOperateLog.setOperateRearText(matchSettleScore.getSettleReason().toString() + ": " + matchSettleScore.getSettleReasonDetail());
                }
                matchSettleOperateLog.setId(null);
                matchSettleOperateLogRepository.save(matchSettleOperateLog);
            }

        } catch (Exception e) {
            log.error("matchSettleScoreAddLog :" + type + ",标准赛事ID:" + matchSettleScore.getStandardMatchId() + ", error:", e);

        }
    }

    public List<MatchSettleOperateLog> batchMatchSettleScoreAddLog(StandardMatchInfo standardMatchInfo, MatchSettleScore matchSettleScore, String operatorName, OperateLogTypeEnum type, String beforeText, String ipAddress) {
        List<MatchSettleOperateLog> operateLogs = new ArrayList<>();
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
            if (standardMatchInfo == null) {
                return operateLogs;
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
                            return operateLogs;
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
            operateLogs.add(matchSettleOperateLog);
            MatchSettleOperateLog matchSettleOperateLog1 = new MatchSettleOperateLog();
            BeanUtils.copyProperties(matchSettleOperateLog, matchSettleOperateLog1);
            //二次结算再次记录日志
            if (matchSettleScore.getSettleCount() != null && matchSettleScore.getSettleCount() > 1 && type.getCode() == 10017) {
                matchSettleOperateLog1.setOperateType("10026");
                matchSettleOperateLog1.setOperateForwText(beforeText);
                matchSettleOperateLog1.setOperateRearText(matchSettleScore.getSettleReason() != null ? matchSettleScore.getSettleReason().toString() : null);
                //二次结算原因为"其他" 将拼接详细原因
                if (matchSettleScore.getSettleReason() != null && matchSettleScore.getSettleReason() == 118) {
                    matchSettleOperateLog1.setOperateRearText(matchSettleScore.getSettleReason().toString() + ": " + matchSettleScore.getSettleReasonDetail());
                }
                operateLogs.add(matchSettleOperateLog1);
            }
        } catch (Exception e) {
            log.error("matchSettleScoreAddLog :" + type + ",标准赛事ID:" + matchSettleScore.getStandardMatchId() + ", error:", e);
        }
        return operateLogs;
    }

    @Override
    //确认比分增加操作日志
    public void matchSettleScoreAddLog(MatchSettleScore matchSettleScore, MatchSettleScore newMatchSettleScore, String name, String type, String linkId, String ipAddress) {
        String eventCode = newMatchSettleScore.getEventCode();
        try {
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
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
            matchSettleOperateLog.setOperateUserName(name);
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


            matchSettleOperateLogRepository.save(matchSettleOperateLog);
        } catch (Exception e) {
            log.error("::" + linkId + "::matchSettleScoreAddLog :" + type + ",标准赛事管理ID:" + matchSettleScore.getStandardMatchId() + ", error:", e);

        }
    }

    @Override
    @Async("MatchEventLogThreadPool")
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
                if ("0".equals(matchSettleEvent.getFiveMinSection())) {
                    extracted(matchSettleEvent, matchSettleOperateLog);
                    matchSettleOperateLog.setId(null);
                    matchSettleOperateLogRepository.save(matchSettleOperateLog);
                } else {
                    if (FifteenMinSectionEnum.isExist(matchSettleEvent.getFiveMinSection())) {
                        //传的15分钟区间，则只保存15分钟日志
                        //15分钟编辑再次记录日志
                        matchSettleOperateLog.setOperateForwText(matchSettleEvent.getFiveMinSection());
                        matchSettleOperateLog.setOperateRearText(matchSettleEvent.getFiveMinSection());
                        matchSettleOperateLog.setId(null);
                        matchSettleOperateLogRepository.save(matchSettleOperateLog);
                    } else {
                        if ("goal".equals(matchSettleEvent.getEventCode())) {
                            if (StrUtil.isNotEmpty(matchSettleEvent.getFiveMinSection())) {
                                matchSettleOperateLog.setOperateForwText(matchSettleEvent.getFiveMinSection());
                            } else {
                                matchSettleOperateLog.setOperateForwText("-");
                            }
                            matchSettleOperateLog.setOperateRearText(matchSettleEvent.getFiveMinSection());
                            if ("0".equals(matchSettleOperateLog.getOperateForwText())) {
                                extracted(matchSettleEvent, matchSettleOperateLog);
                            }
                            matchSettleOperateLog.setId(null);
                            matchSettleOperateLogRepository.save(matchSettleOperateLog);
                        }
                        //保存15分钟日志
                        matchSettleEvent.setFifteenMinSection(calcFifteenMinSection(matchSettleEvent.getFiveMinSection()));
                        if ("0".equals(matchSettleEvent.getFifteenMinSection())) {
                            matchSettleOperateLog.setOperateForwText("-");
                        } else {
                            matchSettleOperateLog.setOperateForwText(matchSettleEvent.getFifteenMinSection());
                        }
                        matchSettleOperateLog.setOperateRearText(matchSettleEvent.getFifteenMinSection());
                        if ("0".equals(matchSettleOperateLog.getOperateForwText())) {
                            extracted(matchSettleEvent, matchSettleOperateLog);
                        }
                        if ("0".equals(matchSettleOperateLog.getOperateRearText())) {
                            extracted(matchSettleEvent, matchSettleOperateLog);
                        }
                        //罚牌不展示时间问题，特殊处理
//                        if("fa_card".equals(matchSettleOperateLog.getOperateName())){
//                            if(FifteenMinSectionEnum.isExist(matchSettleOperateLog.getOperateForwText())){
//                                matchSettleOperateLog.setOperateForwText(OperateLogTypeEnum.getEnum(matchSettleOperateLog.getOperateForwText()).getValue());
//                            }
//                            if(FifteenMinSectionEnum.isExist(matchSettleOperateLog.getOperateRearText())){
//                                matchSettleOperateLog.setOperateRearText(OperateLogTypeEnum.getEnum(matchSettleOperateLog.getOperateRearText()).getValue());
//                            }
//                        }
                        matchSettleOperateLog.setId(null);
                        matchSettleOperateLogRepository.save(matchSettleOperateLog);
                    }
                }
            }


            //二次结算再次记录日志
            if (matchSettleEvent.getSettleCount() != null && matchSettleEvent.getSettleCount() > 1 && "10017".equals(code)) {
                matchSettleOperateLog.setOperateType("10026");
                matchSettleOperateLog.setOperateForwText(before);
                matchSettleOperateLog.setOperateRearText(matchSettleEvent.getSettleReason().toString());
                //二次结算原因为"其他" 将拼接详细原因
                if (matchSettleEvent.getSettleReason() == 118) {
                    matchSettleOperateLog.setOperateRearText(matchSettleEvent.getSettleReason().toString() + ": " + matchSettleEvent.getSettleReasonDetail());
                }
                matchSettleOperateLog.setId(null);
                matchSettleOperateLogRepository.save(matchSettleOperateLog);
            }


        } catch (Exception e) {
            log.error("matchSettleEventAddLog :" + code + ",标准赛事ID:" + matchSettleEvent.getStandardMatchId() + ", error:", e);

        }
    }

    private void extracted(MatchSettleEvent matchSettleEvent, MatchSettleOperateLogEntity matchSettleOperateLog) {
        if ("goal".equals(matchSettleEvent.getEventCode())) {
            matchSettleOperateLog.setOperateForwText(OperateLogTypeEnum.type_6.getCode().toString());
            matchSettleOperateLog.setOperateRearText(OperateLogTypeEnum.type_6.getCode().toString());
        } else if ("corner".equals(matchSettleEvent.getEventCode())) {
            matchSettleOperateLog.setOperateForwText(OperateLogTypeEnum.SCORES_SETTLE_10029.getCode().toString());
            matchSettleOperateLog.setOperateRearText(OperateLogTypeEnum.SCORES_SETTLE_10029.getCode().toString());
        } else if ("fa_card".equals(matchSettleEvent.getEventCode())) {
            matchSettleOperateLog.setOperateForwText(FaCardEnum.Method_5.getCode().toString());
            matchSettleOperateLog.setOperateRearText(FaCardEnum.Method_5.getCode().toString());
        } else {
            matchSettleOperateLog.setOperateForwText("None");
            matchSettleOperateLog.setOperateRearText("None");
        }
    }

    //进球主客队特殊处理
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

    //角球特殊处理
    void CheckInfoCornerProcessRest(MatchSettleCheckInfo matchSettleEvent,
                                    MatchSettleCheckInfo newMatchSettleEvent,
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
//        //15分钟区间，角球传的5分钟字段
//        if (matchSettleEvent.getFiveMinSection() != null && !"".equals(matchSettleEvent.getFiveMinSection())){
//            matchSettleOperateLog.setOperateForwText(matchSettleEvent.getFiveMinSection());
//            matchSettleOperateLog.setOperateRearText(newMatchSettleEvent.getFiveMinSection());
//        } else {
//            matchSettleOperateLog.setOperateForwText("-");
//            matchSettleOperateLog.setOperateRearText(newMatchSettleEvent.getFiveMinSection());
//        }
        //设置走水
        if (matchSettleEvent.getGoWaterStatus() != null && matchSettleEvent.getGoWaterStatus().equals(1))
            matchSettleOperateLog.setOperateForwText("10031");
        if (newMatchSettleEvent.getGoWaterStatus() != null && newMatchSettleEvent.getGoWaterStatus().equals(1))
            matchSettleOperateLog.setOperateRearText("10031");

    }


    //罚牌红黄牌特殊处理
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

    //事件编辑和回滚
    @Override
    public void matchSettleEventAddLog(MatchSettleEvent matchSettleEvent, MatchSettleEvent newMatchSettleEvent, String operatorName, OperateLogTypeEnum type, String ipAddress) {
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
                    //      if ("1001".equals(playerNameCode) && "1001".equals(ExtryInfo)) {
                    //        matchSettleOperateLog.setOperateForwText("-");
                    //     }
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
            matchSettleOperateLog.setId(null);
            matchSettleOperateLogRepository.save(matchSettleOperateLog);

            //5分钟编辑再次记录日志//角球罚牌只有15分
            if ("goal".equals(newMatchSettleEvent.getEventCode())) {
                if (StrUtil.isNotEmpty(newMatchSettleEvent.getFiveMinSection())) {
                    //15分钟区间值，不保存5分钟日志
                    if (!FifteenMinSectionEnum.isExist(newMatchSettleEvent.getFiveMinSection())) {
                        if (matchSettleEvent.getFiveMinSection() != null && !"".equals(matchSettleEvent.getFiveMinSection())) {
                            matchSettleOperateLog.setOperateForwText(matchSettleEvent.getFiveMinSection());
                        } else {
                            matchSettleOperateLog.setOperateForwText("-");
                        }
                        matchSettleOperateLog.setOperateRearText(newMatchSettleEvent.getFiveMinSection());
                        log.info("开始保存15分钟日志1.8::{}", JSONObject.toJSON(matchSettleOperateLog));
                        if ("0".equals(matchSettleOperateLog.getOperateForwText())) {
                            matchSettleOperateLog.setOperateForwText("-");
                        }
                        if ("0".equals(matchSettleOperateLog.getOperateRearText())) {
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
            if (StrUtil.isNotEmpty(newMatchSettleEvent.getFifteenMinSection())) {
                if (StrUtil.isNotEmpty(matchSettleEvent.getFifteenMinSection())) {
                    matchSettleOperateLog.setOperateForwText(matchSettleEvent.getFifteenMinSection());
                } else if (StrUtil.isNotEmpty(matchSettleEvent.getFiveMinSection())) {
                    //因为15分钟区间未入库，根据5分钟区间计算15分钟区间
                    matchSettleOperateLog.setOperateForwText(calcFifteenMinSection(matchSettleEvent.getFiveMinSection()));
                } else {
                    matchSettleOperateLog.setOperateForwText("-");
                }
                matchSettleOperateLog.setOperateRearText(newMatchSettleEvent.getFifteenMinSection());
                //编辑 前 -
                if ("0".equals(matchSettleOperateLog.getOperateForwText())) {
                    matchSettleOperateLog.setOperateForwText("-");
                }
                if ("0".equals(matchSettleOperateLog.getOperateRearText())) {
                    if ("goal".equals(matchSettleEvent.getEventCode())) {
                        matchSettleOperateLog.setOperateRearText(OperateLogTypeEnum.type_6.getCode().toString());
                    } else if ("corner".equals(matchSettleEvent.getEventCode())) {
                        matchSettleOperateLog.setOperateRearText(OperateLogTypeEnum.SCORES_SETTLE_10029.getCode().toString());
                    } else if ("fa_card".equals(matchSettleEvent.getEventCode())) {
                        matchSettleOperateLog.setOperateRearText(FaCardEnum.Method_5.getCode().toString());
                    }
                }
                log.info("开始保存15分钟日志==========3.5:{}", JSONUtil.toJsonStr(matchSettleOperateLog));
//                //罚牌不展示时间问题，特殊处理
//                if("fa_card".equals(matchSettleOperateLog.getOperateName())){
//                    if(FifteenMinSectionEnum.isExist(matchSettleOperateLog.getOperateForwText())){
//                        matchSettleOperateLog.setOperateForwText(OperateLogTypeEnum.getEnum(matchSettleOperateLog.getOperateForwText()).getValue());
//                    }
//                    if(FifteenMinSectionEnum.isExist(matchSettleOperateLog.getOperateRearText())){
//                        matchSettleOperateLog.setOperateRearText(OperateLogTypeEnum.getEnum(matchSettleOperateLog.getOperateRearText()).getValue());
//                    }
//                }
                matchSettleOperateLog.setId(null);
                matchSettleOperateLogRepository.save(matchSettleOperateLog);
            }/* else if(FifteenMinSectionEnum.isExist(newMatchSettleEvent.getFiveMinSection())){
                if (matchSettleEvent.getFiveMinSection() != null && !"".equals(matchSettleEvent.getFiveMinSection())) {
                    matchSettleOperateLog.setOperateForwText(newMatchSettleEvent.getFiveMinSection());
                }else {
                    matchSettleOperateLog.setOperateForwText("-");
                }
                matchSettleOperateLog.setOperateRearText(newMatchSettleEvent.getFiveMinSection());
                if ("no goal".equals(newMatchSettleEvent.getHomeAway())  || "none".equals(newMatchSettleEvent.getHomeAway()))  {
                    if(StrUtil.isNotEmpty(matchSettleEvent.getFiveMinSection())){
                        matchSettleOperateLog.setOperateForwText(matchSettleEvent.getFiveMinSection());
                    }else{
                        matchSettleOperateLog.setOperateForwText("-");
                    }
                    matchSettleOperateLog.setOperateRearText(OperateLogTypeEnum.type_6.getCode().toString());
                }
                if("0".equals(matchSettleOperateLog.getOperateForwText())){
                    matchSettleOperateLog.setOperateForwText("-");
                }
                if("0".equals(matchSettleOperateLog.getOperateRearText())){
                    matchSettleOperateLog.setOperateRearText(OperateLogTypeEnum.type_6.getCode().toString());
                    if("corner".equals(newMatchSettleEvent.getEventCode())){
                        matchSettleOperateLog.setOperateRearText(OperateLogTypeEnum.SCORES_SETTLE_10029.getCode().toString());
                    }else if("fa_card".equals(newMatchSettleEvent.getEventCode())){
                        matchSettleOperateLog.setOperateRearText(FaCardEnum.Method_5.getCode().toString());
                    }
                }
                log.info("开始保存15分钟日志3::{}",JSONObject.toJSON(matchSettleOperateLog));
                matchSettleOperateLogRepository.save(matchSettleOperateLog);
            }*/

        } catch (Exception e) {
            log.error("matchSettleEventAddLog :" + type + ",标准赛事ID:" + matchSettleEvent.getStandardMatchId() + ", error:", e);

        }
    }

    /**
     * 结算切换操作日志
     *
     * @param matchSettleInfo        结算为空标识
     * @param matchSettleSwitcherDto
     * @param operateForw
     */
    @Override
    public void settleSwitcherAddLog(MatchSettleInfo matchSettleInfo, MatchSettleSwitcherDto matchSettleSwitcherDto, Integer operateForw) {
        String linkId = matchSettleSwitcherDto.getLinkId();
        try {
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchSettleSwitcherDto.getMatchId());
            if (standardMatchInfo == null) {
                return;
            }


            List<Long> standardMatchIds = new ArrayList<>();
            standardMatchIds.add(Long.getLong(standardMatchInfo.getMatchManageId()));
            List<StandardSportMarketSell> standardSportMarketSells = standardSportMarketSellService.getItems(standardMatchIds);
            if (standardSportMarketSells.size() == 0) {
                return;
            }
            StandardSportMarketSell standardSportMarketSell = standardSportMarketSells.get(0);


            BeanUtils.copyProperties(matchSettleSwitcherDto, matchSettleOperateLog);
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchSettleOperateLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());
            matchSettleOperateLog.setOperateType(OperateLogTypeEnum.type_4.getCode().toString());
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.type_8.getCode().toString() + "-" + StandardSportTypeEnum.getEnum(matchSettleSwitcherDto.getSportId()).getCode());
            matchSettleOperateLog.setOperateId(standardMatchInfo.getMatchManageId());
            matchSettleOperateLog.setOperateName(standardMatchInfo.getHomeAwayInfo());
            matchSettleOperateLog.setIpAddress(matchSettleSwitcherDto.getIpAddress());

            matchSettleOperateLog.setOperateUserName(matchSettleSwitcherDto.getOperatorName());
            matchSettleOperateLog.setOperateParaName(OperateLogTypeEnum.type_11.getCode().toString());

            if ("Sold".equals(standardSportMarketSell.getPreMatchSellStatus()) ||
                    "Stop_Sold".equals(standardSportMarketSell.getPreMatchSellStatus())) {
                matchSettleOperateLog.setOperateParaName(OperateLogTypeEnum.type_9.getCode().toString());
            }

            if ("Sold".equals(standardSportMarketSell.getLiveMatchSellStatus()) ||
                    "Stop_Sold".equals(standardSportMarketSell.getLiveMatchSellStatus())) {
                matchSettleOperateLog.setOperateParaName(OperateLogTypeEnum.type_10.getCode().toString());
            }

            matchSettleOperateLog.setOperateForwText(operateForw == 1 ? "1.0" : "2.0");
            matchSettleOperateLog.setOperateRearText(matchSettleSwitcherDto.getSettleType() == 1 ? "1.0" : "2.0");
            matchSettleOperateLogRepository.save(matchSettleOperateLog);

            //结算切换  增加开售日志
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            StandardSportMarketSellLog standardSportMarketSellLog = new StandardSportMarketSellLog();
            standardSportMarketSellLog.setLog("结算切换:【" + matchSettleOperateLog.getOperateUserName() + "】在【" + simpleDateFormat.format(date) + "】将赛事结算方式【" + matchSettleOperateLog.getOperateForwText() + "】切换到【" + matchSettleOperateLog.getOperateRearText() + "】，标准赛事管理id为【" + standardSportMarketSell.getMatchManageId() + "】,link-id为【" + matchSettleSwitcherDto.getLinkId() + "】");
            standardSportMarketSellLog.setLogEn("Settlement Mode Switching: 【" + matchSettleOperateLog.getOperateUserName() + "】【" + simpleDateFormat.format(date) + "】 switching from 【" + matchSettleOperateLog.getOperateForwText() + "】 to 【" + matchSettleOperateLog.getOperateRearText() + "】, Match ID is 【" + standardSportMarketSell.getMatchManageId() + "】, link-id is 【" + matchSettleSwitcherDto.getLinkId() + "】");
            standardSportMarketSellLog.setOperateId(matchSettleSwitcherDto.getOperatorId());
            standardSportMarketSellLog.setOperateName(matchSettleSwitcherDto.getOperatorName());
            standardSportMarketSellLog.setStandardSportMarketSellId(standardSportMarketSell.getId());
            standardSportMarketSellLog.setStandardMatchId(standardMatchInfo.getId());
            standardSportMarketSellLog.setOperateTime(System.currentTimeMillis());
            standardSportMarketSellLog.setOperateType("settle");
            standardSportMarketSellLogMapper.insert(standardSportMarketSellLog);

        } catch (Exception e) {
            log.error("::" + linkId + ":: settleSwitcher,标准赛事ID:" + matchSettleSwitcherDto.getMatchId() + ", error:", e);

        }
    }

    /**
     * 比分录入操作日志
     *
     * @param matchSettleOperateLogDto
     * @param forwScore
     * @param matchSettleScore
     * @param standardMatchInfo
     * @param OperateType
     */
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

            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
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

            matchSettleOperateLogRepository.save(matchSettleOperateLog);
        } catch (Exception e) {
            log.error("::" + linkId + ":: updateMatchSettleScoreAddLog,标准赛事ID:" + JSON.toJSONString(standardMatchInfo) + ", error:", e);

        }
    }


    //1021,1031,1032,1033
    //特殊处理的结算方式
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


    /**
     * 结算事件冻结 记录操作日志
     *
     * @param matchSettleEvent
     * @param forwText
     */
    @Override
    public void scoresPeriodOrderFreeze(StandardMatchInfo standardMatchInfo, MatchSettleEvent matchSettleEvent, String forwText,
                                        ScoresPeriodOrderFreezeDto freezeDto) {
        String operatorName = freezeDto.getOperatorName();
        String linkId = freezeDto.getLinkId();
        String ipAddress = freezeDto.getIpAddress();

        try {
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            if (standardMatchInfo == null) {
                return;
            }
            matchSettleOperateLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchSettleOperateLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());
            matchSettleOperateLog.setOperateForwText(forwText);
            matchSettleOperateLog.setOperateRearText(matchSettleEvent.getSettleFreeze() == 0 ? OperateLogTypeEnum.type_2.getCode().toString() : OperateLogTypeEnum.type_1.getCode().toString());
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.type_7.getCode() + "-" + StandardSportTypeEnum.getEnum(matchSettleEvent.getSportId()).getCode());
            matchSettleOperateLog.setOperateUserName(operatorName);
            matchSettleOperateLog.setOperateType(matchSettleOperateLog.getOperateRearText());

            //全部冻结
            if (freezeDto.getMins() != null && freezeDto.getMins() > 0) {
                matchSettleOperateLog.setRemark(freezeDto.getMins().toString());
                matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_SETTLE_10037.getCode().toString());
                matchSettleOperateLog.setOperateRearText(OperateLogTypeEnum.type_1.getCode().toString());
            } else if (freezeDto.getFreezeTime() != null && freezeDto.getFreezeTime() != 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
                matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100139.getCode().toString());
                matchSettleOperateLog.setOperateForwText(sdf.format(TimeUtils.millsSecondsEast8ZoneGmt()));
                matchSettleOperateLog.setOperateRearText(sdf.format(freezeDto.getFreezeTime()));
            } else {
                matchSettleOperateLog.setOperateType(freezeDto.getFreezeStatus() == 0 ?
                        OperateLogTypeEnum.type_2.getCode().toString() : OperateLogTypeEnum.type_5.getCode().toString());
                matchSettleOperateLog.setOperateForwText(freezeDto.getFreezeStatus() == 0 ? OperateLogTypeEnum.type_1.getCode().toString() : OperateLogTypeEnum.type_2.getCode().toString());
            }

            Integer eventOrder = matchSettleEvent.getEventOrder();
            if (eventOrder != null) matchSettleOperateLog.setEventOrder(matchSettleEvent.getEventOrder().toString());
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setIpAddress(ipAddress);
            //操作对象id
            matchSettleOperateLog.setOperateId(matchSettleEvent.getId().toString());
            matchSettleOperateLog.setOperateName(matchSettleEvent.getEventCode());
            //操作参数名称
            String settleNum = matchSettleEvent.getSettleNum();
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


            matchSettleOperateLogRepository.save(matchSettleOperateLog);
        } catch (Exception e) {
            log.error("::" + linkId + "::scoresPeriodOrderFreeze :标准赛事ID:" + matchSettleEvent.getStandardMatchId() + ", error: 比分阶段冻结", e);

        }
    }

    /**
     * 比分 记录操作日志
     *
     * @param matchSettleScore
     * @param forwText
     * @param scoresPeriodFreezeDto
     * @param forwText
     */
    @Override
    public void scoresPeriodFreezeAddLog(StandardMatchInfo standardMatchInfo, MatchSettleScore matchSettleScore, String forwText, ScoresPeriodFreezeDto scoresPeriodFreezeDto) {

        String operatorName = scoresPeriodFreezeDto.getOperatorName();
        String linkid = scoresPeriodFreezeDto.getLinkId();
        String ipAddress = scoresPeriodFreezeDto.getIpAddress();

        try {
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            String eventCode = matchSettleScore.getEventCode();
            matchSettleOperateLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchSettleOperateLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());
            matchSettleOperateLog.setOperateForwText(forwText);
            matchSettleOperateLog.setOperateRearText(matchSettleScore.getSettleFreeze() == 0 ? OperateLogTypeEnum.type_2.getCode().toString() : OperateLogTypeEnum.type_1.getCode().toString());
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.type_7.getCode() + "-" + StandardSportTypeEnum.getEnum(matchSettleScore.getSportId()).getCode());
            matchSettleOperateLog.setOperateUserName(operatorName);

            if (scoresPeriodFreezeDto.getMins() != null && scoresPeriodFreezeDto.getMins() > 0) {
                matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_SETTLE_10037.getCode().toString());
                matchSettleOperateLog.setRemark(scoresPeriodFreezeDto.getMins().toString());
                matchSettleOperateLog.setOperateForwText("-");
            } else if (scoresPeriodFreezeDto.getFreezeTime() != null && scoresPeriodFreezeDto.getFreezeTime() != 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
                matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100139.getCode().toString());
                matchSettleOperateLog.setOperateForwText(sdf.format(TimeUtils.millsSecondsEast8ZoneGmt()));
                matchSettleOperateLog.setOperateRearText(sdf.format(scoresPeriodFreezeDto.getFreezeTime()));
            } else {
                if (!StringUtils.isAnyEmpty(scoresPeriodFreezeDto.getSettleNum()) && scoresPeriodFreezeDto.getFreezeStatus() == 1) {
                    matchSettleOperateLog.setOperateForwText("-");
                    matchSettleOperateLog.setOperateType(OperateLogTypeEnum.type_1.getCode().toString());
                } else {
                    matchSettleOperateLog.setOperateForwText(scoresPeriodFreezeDto.getFreezeStatus() == 0 ? OperateLogTypeEnum.type_1.getCode().toString() : OperateLogTypeEnum.type_2.getCode().toString());
                    matchSettleOperateLog.setOperateType(scoresPeriodFreezeDto.getFreezeStatus() == 0 ? OperateLogTypeEnum.type_2.getCode().toString() : OperateLogTypeEnum.type_5.getCode().toString());
                }
            }


            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setIpAddress(ipAddress);
            //操作对象id
            matchSettleOperateLog.setOperateId("-");
            matchSettleOperateLog.setOperateName(eventCode);
            //开球进入
            if (StringUtils.isEmpty(eventCode) || "kick_off".equals(matchSettleScore.getEventCode()))
                matchSettleOperateLog.setOperateName("goal");

            //操作参数名称
            String settleNum = matchSettleScore.getSettleNum();
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

            matchSettleOperateLogRepository.save(matchSettleOperateLog);
        } catch (Exception e) {
            log.error("::" + linkid + "::scoresPeriodFreezeAddLog ,标准赛事ID:" + matchSettleScore.getStandardMatchId() + ", error:", e);

        }
    }

    @Override
    public void matchFreezeAddLog(StandardMatchInfo standardMatchInfo, MatchSettleInfo matchSettleInfo, String forwText, MatchFreezeDto matchFreezeDto) {
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
            log.error("::" + matchFreezeDto.getLinkId() + "::matchFreezeAddLog,标准赛事ID:" + matchSettleInfo.getStandardMatchId() + " , error:", e);

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
            log.error("matchReSettleAddLog,标准赛事ID:" + settleQueryDTO.getMatchId() + " , error:", e);

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
            log.error("categoryReSettleAddLog,标准赛事ID:" + settleQueryDTO.getMatchId() + " , error:", e);

        }
    }

    //新表比分编辑
    @Override
    public void matchSettleCheckScoreAddLog(MatchSettleCheckInfo oIdInfo, MatchSettleCheckInfo newInfo,
                                            UpdateMatchSettleScoreDto dto, OperateLogTypeEnum enums,
                                            String settleNum, Integer checkNumber) {
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

            matchSettleOperateLogRepository.save(matchSettleOperateLog);

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
                matchSettleOperateLog.setId(null);
                matchSettleOperateLogRepository.save(matchSettleOperateLog);
            }
            //15分钟编辑再次记录日志
            if (newInfo.getFiveMinSection() != null) {
                newInfo.setFifteenMinSection(calcFifteenMinSection(newInfo.getFiveMinSection()));
                if (oIdInfo.getFiveMinSection() != null) {
                    matchSettleOperateLog.setOperateForwText(newInfo.getFifteenMinSection());
                } else {
                    matchSettleOperateLog.setOperateForwText("-");
                }
                matchSettleOperateLog.setOperateRearText(newInfo.getFifteenMinSection());
                matchSettleOperateLog.setId(null);
                matchSettleOperateLogRepository.save(matchSettleOperateLog);
            }
        } catch (Exception e) {
            log.error("matchSettleCheckInfoAddLog,标准赛事ID:" + dto.getStandardMatchId() + " , error:", e);

        }
    }

    public void matchSettleCheckEventAddLog(MatchSettleCheckInfo oIdInfo, MatchSettleCheckInfo newInfo,
                                            UpdateMatchSettleScoreDto dto, OperateLogTypeEnum enums,
                                            String settleNum, Integer checkNumber) {
        log.info("记录结算核对事件日志，settleNum：{}，oIdInfo：{}，newInfo：{}", settleNum, oIdInfo, newInfo);
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
            matchSettleOperateLogRepository.save(matchSettleOperateLog);


            //5分钟编辑再次记录日志
            if (newInfo.getFiveMinSection() != null && !"".equals(newInfo.getFiveMinSection())) {
                // 5分钟区间
                if (!FifteenMinSectionEnum.isExist(newInfo.getFiveMinSection())) {
                    if (oIdInfo.getFiveMinSection() != null && !"".equals(oIdInfo.getFiveMinSection())) {
                        matchSettleOperateLog.setOperateForwText(oIdInfo.getFiveMinSection());
                    } else {
                        matchSettleOperateLog.setOperateForwText("-");
                    }
                    matchSettleOperateLog.setOperateRearText(newInfo.getFiveMinSection());
                    matchSettleOperateLog.setId(null);
                    log.info("15min1:" + matchSettleOperateLog);
                    matchSettleOperateLogRepository.save(matchSettleOperateLog);
                }
            }

            //15分钟编辑再次记录日志
            if (newInfo.getFiveMinSection() != null && !"".equals(newInfo.getFiveMinSection())) {
                //15分钟区间的值
                if (FifteenMinSectionEnum.isExist(newInfo.getFiveMinSection())) {
                    matchSettleOperateLog.setOperateForwText(newInfo.getFiveMinSection());
                    matchSettleOperateLog.setOperateRearText(newInfo.getFiveMinSection());
                } else {
                    newInfo.setFifteenMinSection(calcFifteenMinSection(newInfo.getFiveMinSection()));
                    matchSettleOperateLog.setOperateForwText(newInfo.getFifteenMinSection());
                    matchSettleOperateLog.setOperateRearText(newInfo.getFifteenMinSection());
                }
                matchSettleOperateLog.setId(null);
                log.info("15min2:" + matchSettleOperateLog);
                matchSettleOperateLogRepository.save(matchSettleOperateLog);
            }

        } catch (Exception e) {
            log.error("matchSettleCheckEventAddLog,标准赛事ID:" + dto.getStandardMatchId() + " , error:", e);

        }
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
            default:
                return "0";
        }
    }


    //普通用户记录编辑操作日志
    @Override
    public void matchSettleCheckEventAddLog(MatchSettleEvent matchSettleEvent, MatchSettleEvent newMatchSettleEvent, UpdateMatchSettleScoreDto dto, OperateLogTypeEnum type, Integer checkNumber) {

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
            if (StringUtils.isEmpty(eventCode) || "kick_off".equals(eventCode)) eventCode = "goal";
            if ("red_card".equals(eventCode) || "yellow_card".equals(eventCode)) eventCode = "fa_card";

            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchSettleEvent.getStandardMatchId());
            if (standardMatchInfo == null) {
                return;
            }

            if (newMatchSettleEvent.getEventOrder() != null) {
                matchSettleOperateLog.setEventOrder(newMatchSettleEvent.getEventOrder().toString());
            }
            SettlePeriodEnum PeriodEnum = SettlePeriodEnum.getEnum(periodId);
            if (PeriodEnum != null) matchSettleOperateLog.setPeriodId(PeriodEnum.value);
            matchSettleOperateLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchSettleOperateLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());
            matchSettleOperateLog.setOperateForwText(forwT1 + "-" + forwT2);

            matchSettleOperateLog.setOperateRearText(rearT1 + "-" + rearT2);
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.type_7.getCode() + "-" + StandardSportTypeEnum.getEnum(matchSettleEvent.getSportId()).getCode());
            matchSettleOperateLog.setOperateUserName(dto.getOperatorName() + ",(第" + checkNumber + "人)");
            matchSettleOperateLog.setOperateType(code);
            matchSettleOperateLog.setIpAddress(dto.getIpAddress());
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


            //进球事件特殊处理
            List<String> list = Arrays.asList("1022", "1023", "1025", "1026");
            //罚球事件特殊处理
            List<String> faList = Arrays.asList("3019", "3020", "3022", "3023");
            //角球事件特殊处理
            List<String> coList = Arrays.asList("204", "205", "209", "2010");
            //点球大战事件特殊处理
            List<String> penaltyList = Arrays.asList("1028", "1029", "1030");

            //角球事件处理
            if (eventCode.equals("corner")
                    && newMatchSettleEvent.getHomeAway() != null) {
                CornerProcessRest(matchSettleEvent, newMatchSettleEvent, matchSettleOperateLog);
            }

            //罚牌事件处理
            if (eventCode.equals("fa_card")
                    && newMatchSettleEvent.getHomeAway() != null
                    && newMatchSettleEvent.getStatus() > 0) {

                matchSettleOperateLog.setOperateForwText(matchSettleEvent.getHomeAway());
                matchSettleOperateLog.setOperateRearText(newMatchSettleEvent.getHomeAway());
                //设置走水
                if (matchSettleEvent.getGoWaterStatus() != null && matchSettleEvent.getGoWaterStatus().equals(1))
                    matchSettleOperateLog.setOperateForwText("6");
                if (newMatchSettleEvent.getGoWaterStatus() != null && newMatchSettleEvent.getGoWaterStatus().equals(1))
                    matchSettleOperateLog.setOperateRearText("6");


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
            }


            if ((eventCode.equals("goal")
                    && newMatchSettleEvent.getStatus() > 0 && !type.getCode().equals(10003))
                    || (type.getCode() == 10016)) {

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

            //5分钟编辑再次记录日志
            if (newMatchSettleEvent.getFiveMinSection() != null && !"".equals(newMatchSettleEvent.getFiveMinSection())) {
                // 5分钟区间
                if (!FifteenMinSectionEnum.isExist(newMatchSettleEvent.getFiveMinSection())) {
                    matchSettleOperateLog.setOperateForwText("-");
                    matchSettleOperateLog.setOperateRearText(newMatchSettleEvent.getFiveMinSection());
                    matchSettleOperateLogRepository.save(matchSettleOperateLog);
                }
//                // 5分钟区间
//                if(!FifteenMinSectionEnum.isExist(newInfo.getFiveMinSection())){
//                    if (oIdInfo.getFiveMinSection() != null && !"".equals(oIdInfo.getFiveMinSection()))
//                        matchSettleOperateLog.setOperateForwText(oIdInfo.getFiveMinSection());
//                    else
//                        matchSettleOperateLog.setOperateForwText("-");
//                    matchSettleOperateLog.setOperateRearText(newInfo.getFiveMinSection());
//                    matchSettleOperateLogRepository.save(matchSettleOperateLog);
//                }
            }

            //15分钟编辑再次记录日志
            if (newMatchSettleEvent.getFiveMinSection() != null && !"".equals(newMatchSettleEvent.getFiveMinSection())) {
                //15分钟区间的值
                if (FifteenMinSectionEnum.isExist(newMatchSettleEvent.getFiveMinSection())) {
                    matchSettleOperateLog.setOperateForwText(newMatchSettleEvent.getFiveMinSection());
                    matchSettleOperateLog.setOperateRearText(newMatchSettleEvent.getFiveMinSection());
                } else {
                    newMatchSettleEvent.setFifteenMinSection(calcFifteenMinSection(newMatchSettleEvent.getFiveMinSection()));
                    matchSettleOperateLog.setOperateForwText(newMatchSettleEvent.getFifteenMinSection());
                    matchSettleOperateLog.setOperateRearText(newMatchSettleEvent.getFiveMinSection());
                }
                matchSettleOperateLog.setId(null);
                matchSettleOperateLogRepository.save(matchSettleOperateLog);
            }
            matchSettleOperateLog.setId(null);
            matchSettleOperateLogRepository.save(matchSettleOperateLog);
        } catch (Exception e) {
            log.error("matchSettleCheckEventAddLog :" + type + ",标准赛事ID:" + matchSettleEvent.getStandardMatchId() + ", error:", e);

        }

    }

    //回滚回调更新操作日志
    @Override
    public void upLog(Long evenRollBackId, String matchId, String info) {
        log.info("回滚回调更新操作日志开始 回滚id:{},标准赛事ID:{}, 回滚数和订单数:{}", evenRollBackId, matchId, info);
        try {
            if (matchId == null || evenRollBackId == null || evenRollBackId == 0) {
                log.error("回滚回调更新操作日志,参数有误 回滚id:{},标准赛事ID: {}", evenRollBackId, matchId);
                return;
            }
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(Long.parseLong(matchId));
            String matchManageId = standardMatchInfo.getMatchManageId();
            MatchSettleOperateLogExample matchSettleOperateLogExample = new MatchSettleOperateLogExample();
            MatchSettleOperateLogExample.Criteria criteria = matchSettleOperateLogExample.createCriteria();
            criteria.andOperateMatchIdEqualTo(matchManageId);
            criteria.andOperateIdEqualTo(evenRollBackId.toString());
            criteria.andOperateTypeEqualTo(OperateLogTypeEnum.ROLLBACK_SCORES_SETTLE.getCode().toString());
            List<MatchSettleOperateLogEntity> matchSettleOperateLogs = matchSettleOperateLogMapper.selectByExample(matchSettleOperateLogExample);
            Optional<MatchSettleOperateLogEntity> max = matchSettleOperateLogs.stream().max((x, y) -> Math.toIntExact(x.getCreateTime() / 1000 - y.getCreateTime() / 1000));
            if (max.isPresent()) {
                MatchSettleOperateLogEntity matchSettleOperateLog = max.get();
                matchSettleOperateLog.setRemark(info);
                matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                matchSettleOperateLogRepository.updateById(matchSettleOperateLog);
            }


        } catch (Exception e) {
            log.error("回滚回调更新操作日志出错 回滚id:" + evenRollBackId + ",标准赛事ID:" + matchId + ", error:", e);
        }


    }

    //结算顺序记录操作日志
    @Override
    public void setSettleOrderClosedAddLog(MatchSettleInfo oIdMatchSettleInfo, MatchSettleInfo matchSettleInfo, MatchSettleOrderClosedDTO dto, OperateLogTypeEnum type) {
        Long matchId = oIdMatchSettleInfo.getStandardMatchId();
        try {
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchId);
            if (standardMatchInfo != null) {
                matchSettleOperateLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
                matchSettleOperateLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());
            }

            Integer settleOrderClosed = oIdMatchSettleInfo.getSettleOrderClosed();
            if (settleOrderClosed == null) {
                settleOrderClosed = 0;
            }
            Integer settleOrderClosedRead = matchSettleInfo.getSettleOrderClosed();
            if (settleOrderClosedRead == null) {
                settleOrderClosedRead = 0;
            }

            if (settleOrderClosed.equals(0))
                settleOrderClosed = OperateLogTypeEnum.SCORES_SETTLE_10035.getCode();
            else
                settleOrderClosed = OperateLogTypeEnum.SCORES_SETTLE_10036.getCode();

            if (settleOrderClosedRead.equals(0))
                settleOrderClosedRead = OperateLogTypeEnum.SCORES_SETTLE_10035.getCode();
            else
                settleOrderClosedRead = OperateLogTypeEnum.SCORES_SETTLE_10036.getCode();


            matchSettleOperateLog.setOperateForwText(settleOrderClosed.toString());
            matchSettleOperateLog.setOperateRearText(settleOrderClosedRead.toString());
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.type_7.getCode() + "-" + StandardSportTypeEnum.getEnum(matchSettleInfo.getSportId()).getCode());
            matchSettleOperateLog.setOperateType(type.getCode().toString());
            matchSettleOperateLog.setOperateUserName(dto.getOperatorName());
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setIpAddress(dto.getIpAddress());
            //操作对象id

            //matchSettleOperateLog.setOperateId(standardMatchInfo.getMatchManageId());
            matchSettleOperateLog.setOperateName("goal");
            //操作参数名称
            matchSettleOperateLog.setOperateParaName("-");
            matchSettleOperateLog.setId(null);
            matchSettleOperateLogRepository.save(matchSettleOperateLog);
        } catch (Exception e) {
            log.error("结算顺序记录操作日志 标准赛事ID:" + matchId + ", error:", e);
        }
    }

    //五分钟玩法开关记录操作日志
    @Override
    public void setFiveMinSwitchLog(MatchSettleInfo oIdMatchSettleInfo, MatchSettleInfo matchSettleInfo, MatchSettleFiveMinSwitchDTO dto, OperateLogTypeEnum type) {
        Long matchId = oIdMatchSettleInfo.getStandardMatchId();
        try {
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchId);
            if (standardMatchInfo != null) {
                matchSettleOperateLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
                matchSettleOperateLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());
            }

            Integer fiveMinSwitch = oIdMatchSettleInfo.getFiveMinSwitch();
            if (fiveMinSwitch == null) {
                fiveMinSwitch = 0;
            }
            Integer fiveMinSwitchRead = matchSettleInfo.getFiveMinSwitch();
            if (fiveMinSwitchRead == null) {
                fiveMinSwitchRead = 0;
            }

            if (fiveMinSwitch.equals(0))
                fiveMinSwitch = OperateLogTypeEnum.SCORES_SETTLE_10036.getCode();
            else
                fiveMinSwitch = OperateLogTypeEnum.SCORES_SETTLE_10035.getCode();

            if (fiveMinSwitchRead.equals(0))
                fiveMinSwitchRead = OperateLogTypeEnum.SCORES_SETTLE_10036.getCode();
            else
                fiveMinSwitchRead = OperateLogTypeEnum.SCORES_SETTLE_10035.getCode();


            matchSettleOperateLog.setOperateForwText(fiveMinSwitch.toString());
            matchSettleOperateLog.setOperateRearText(fiveMinSwitchRead.toString());
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.type_7.getCode() + "-" + StandardSportTypeEnum.getEnum(matchSettleInfo.getSportId()).getCode());
            matchSettleOperateLog.setOperateType(type.getCode().toString());
            matchSettleOperateLog.setOperateUserName(dto.getOperatorName());
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setIpAddress(dto.getIpAddress());
            //操作对象id

            //matchSettleOperateLog.setOperateId(standardMatchInfo.getMatchManageId());
            matchSettleOperateLog.setOperateName("goal");

            //操作参数名称
            matchSettleOperateLog.setOperateParaName("-");
            matchSettleOperateLog.setId(null);
            matchSettleOperateLogRepository.save(matchSettleOperateLog);
        } catch (Exception e) {
            log.error("五分钟玩法记录操作日志 标准赛事ID:" + matchId + ", error:", e);
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
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
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
                if (eventCode.equals("score_change")) {
                    BasketBallSettleNumEnum basketBallSettleNumEnum = BasketBallSettleNumEnum.getEnum(settleNum);
                    if (basketBallSettleNumEnum != null && basketBallSettleNumEnum.getCode() != null) {
                        matchSettleOperateLog.setOperateParaName(basketBallSettleNumEnum.getCode());
                    }
                } else {
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
            matchSettleOperateLog.setId(null);
            matchSettleOperateLogRepository.save(matchSettleOperateLog);
        } catch (Exception e) {
            log.error("删除阶段报警日志 标准赛事ID:" + matchSettleSwitcherDto.getMatchId() + ", error:", e);
        }

    }

    /**
     * @param object                   用于未来对其他参数修改
     * @param settleEventDeleteRequest
     */
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

    private String getMatchSportTeamNameCode(Long matchInfoId) {

        StringBuffer result = new StringBuffer();
        StandardSportMarketSell standardSportMarketSell = new StandardSportMarketSell();
        List<Long> standardMatchIds = new ArrayList<>();
        standardMatchIds.add(matchInfoId);
        List<StandardSportMarketSell> standardSportMarketSellList = standardSportMarketSellService.getItems(standardMatchIds);
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
            result.append(CategoryUtils.SPLIT_AND);
        }
        if (homeSportTeam != null && StringUtils.isNotEmpty(homeSportTeam.getNameSpell()) && awaySportTeam != null && StringUtils.isNotEmpty(awaySportTeam.getNameSpell())) {
            result.append(homeSportTeam.getNameSpell() + " vs " + awaySportTeam.getNameSpell());
        }
        return result.toString();
    }

//    @Override
//    public void updateDataSourceGrayIntervalLog(DataSourceGrayIntervalDto grayIntervalDto, MatchGrayInterval oldDbGray, List<MatchSettleOperateLogEntity> operateLogEntityList) {
//
//        try {
//            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
//            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.type_9.getCode().toString() + "-" + StandardSportTypeEnum.FootBall.getCode());
//            matchSettleOperateLog.setOperateName(grayIntervalDto.getTournamentLevel().toString());
//            matchSettleOperateLog.setOperateMatchId("-");
//            matchSettleOperateLog.setOperateMatchName("-");
//            matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100160.getCode().toString());
//            matchSettleOperateLog.setIpAddress(grayIntervalDto.getIpAddress());
//            matchSettleOperateLog.setOperateUserName(grayIntervalDto.getOperatorName());
//            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
//            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
//            matchSettleOperateLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
//
//            if (grayIntervalDto.getMin5Goal() != null && grayIntervalDto.getMin5Goal() > MatchSettleCheckConstant.CheckType.PERIOD_SCORE) {
//                if (oldDbGray != null && oldDbGray.getMin5Goal() != null) {
//                    matchSettleOperateLog.setOperateForwText(oldDbGray.getMin5Goal().toString());
//                }
//                matchSettleOperateLog.setOperateRearText(grayIntervalDto.getMin5Goal().toString());
//                matchSettleOperateLog.setOperateParaName(DataSourceEncrypEnum.getDataSourceVal(grayIntervalDto.getDataSourceCode()) + CategoryUtils.SPLIT_LINE + CategoryUtils.min5Goal);
//                //matchSettleOperateLogRepository.save(matchSettleOperateLog);
//                operateLogEntityList.add(matchSettleOperateLog);
//            }
//            if (grayIntervalDto.getMin15Goal() != null && grayIntervalDto.getMin15Goal() > MatchSettleCheckConstant.CheckType.PERIOD_SCORE) {
//                if (oldDbGray != null && oldDbGray.getMin15Goal() != null) {
//                    matchSettleOperateLog.setOperateForwText(oldDbGray.getMin15Goal().toString());
//                }
//                matchSettleOperateLog.setOperateRearText(grayIntervalDto.getMin15Goal().toString());
//                matchSettleOperateLog.setOperateParaName(DataSourceEncrypEnum.getDataSourceVal(grayIntervalDto.getDataSourceCode()) + CategoryUtils.SPLIT_LINE + CategoryUtils.min15Goal);
//                //matchSettleOperateLogRepository.save(matchSettleOperateLog);
//                operateLogEntityList.add(matchSettleOperateLog);
//            }
//            if (grayIntervalDto.getMin15Corner() != null && grayIntervalDto.getMin15Corner() > MatchSettleCheckConstant.CheckType.PERIOD_SCORE) {
//                if (oldDbGray != null && oldDbGray.getMin15Corner() != null) {
//                    matchSettleOperateLog.setOperateForwText(oldDbGray.getMin15Corner().toString());
//                }
//                matchSettleOperateLog.setOperateRearText(grayIntervalDto.getMin15Corner().toString());
//                matchSettleOperateLog.setOperateParaName(DataSourceEncrypEnum.getDataSourceVal(grayIntervalDto.getDataSourceCode()) + CategoryUtils.SPLIT_LINE + CategoryUtils.min15Corner);
//                //matchSettleOperateLogRepository.save(matchSettleOperateLog);
//                operateLogEntityList.add(matchSettleOperateLog);
//            }
//            if (grayIntervalDto.getMin15Bookings() != null && grayIntervalDto.getMin15Bookings() > MatchSettleCheckConstant.CheckType.PERIOD_SCORE) {
//                if (oldDbGray != null && oldDbGray.getMin15Bookings() != null) {
//                    matchSettleOperateLog.setOperateForwText(oldDbGray.getMin15Bookings().toString());
//                }
//                matchSettleOperateLog.setOperateRearText(grayIntervalDto.getMin15Bookings().toString());
//                matchSettleOperateLog.setOperateParaName(DataSourceEncrypEnum.getDataSourceVal(grayIntervalDto.getDataSourceCode()) + CategoryUtils.SPLIT_LINE + CategoryUtils.min15Bookings);
//                //matchSettleOperateLogRepository.save(matchSettleOperateLog);
//                operateLogEntityList.add(matchSettleOperateLog);
//            }
//
//        } catch (Exception e) {
//            log.error("修改15分钟&5分钟灰色区间设置日志:" + JSONObject.toJSONString(grayIntervalDto) + ", error:", e);
//        }
//
//    }


    @Override
    public void updateLeagueMatchSettleDataSourceLog(MatchSettleDataSourceDto matchSettleDataSourceDto, Integer oldStatus) {

        try {
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.type_10.getCode().toString() + "-" + StandardSportTypeEnum.FootBall.getCode());
            matchSettleOperateLog.setOperateName("-");
            switch (matchSettleDataSourceDto.getSportId().intValue()) {
                case 1:
                    matchSettleOperateLog.setOperateName(CategoryUtils.SOCCER);
                    break;
                case 2:
                    matchSettleOperateLog.setOperateName(CategoryUtils.BASKETBALL);
                    break;
                default:
                    return;
            }
            matchSettleOperateLog.setOperateMatchId("-");
            matchSettleOperateLog.setOperateMatchName("-");
            matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100159.getCode().toString());
            //全部联赛
            if (matchSettleDataSourceDto.getTournamentLevel().equals(CategoryUtils.UN_LEVEL)) {
                matchSettleOperateLog.setOperateParaName(DataSourceEncrypEnum.getDataSourceVal(matchSettleDataSourceDto.getDataSourceCode()));
                //旧的操作状态
                if (oldStatus.equals(Constant.OUTRIGHT_ZERO)) {
                    matchSettleOperateLog.setOperateForwText(CategoryUtils.OFF_All);
                } else {
                    matchSettleOperateLog.setOperateForwText(CategoryUtils.ON_All);
                }
                //新的操作状态
                if (matchSettleDataSourceDto.getStatus().equals(Constant.OUTRIGHT_ZERO)) {
                    matchSettleOperateLog.setOperateRearText(CategoryUtils.OFF_All);//操作后
                } else {
                    matchSettleOperateLog.setOperateRearText(CategoryUtils.ON_All);//操作后
                }
            } else {
                //单级别的联赛
                matchSettleOperateLog.setOperateParaName(matchSettleDataSourceDto.getTournamentLevel() + CategoryUtils.SPLIT_LINE + DataSourceEncrypEnum.getDataSourceVal(matchSettleDataSourceDto.getDataSourceCode()));
                //旧的操作状态
                if (oldStatus.equals(Constant.OUTRIGHT_ZERO)) {
                    matchSettleOperateLog.setOperateForwText(CategoryUtils.OFF);
                } else {
                    matchSettleOperateLog.setOperateForwText(CategoryUtils.ON);
                }
                //操作后
                if (matchSettleDataSourceDto.getStatus().equals(Constant.OUTRIGHT_ZERO)) {
                    matchSettleOperateLog.setOperateRearText(CategoryUtils.OFF);//操作后
                } else {
                    matchSettleOperateLog.setOperateRearText(CategoryUtils.ON);//操作后
                }
            }
            matchSettleOperateLog.setIpAddress(matchSettleDataSourceDto.getIpAddress());
            matchSettleOperateLog.setOperateUserName(matchSettleDataSourceDto.getOperatorName());
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLogRepository.save(matchSettleOperateLog);
        } catch (Exception e) {
            log.error("linkId：" + matchSettleDataSourceDto.getLinkedId() + ",更新联赛等级对应的结算数据源的开关列表:" + JSONObject.toJSONString(matchSettleDataSourceDto) + ", error:", e);
        }

    }

    @Override
    public void addSettleTemplateLog(MatchSettleTemplateDto matchSettleTemplateDto) {

        try {
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            Long sportType = matchSettleTemplateDto.getSportId() == 1 ? StandardSportTypeEnum.FootBall.getCode() : StandardSportTypeEnum.Basketball.getCode();
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100161.getCode().toString() + "-" + sportType);
            matchSettleOperateLog.setOperateId(matchSettleTemplateDto.getId().toString());
            matchSettleOperateLog.setOperateName(matchSettleTemplateDto.getTemplateName());
            matchSettleOperateLog.setOperateParaName("-");
            matchSettleOperateLog.setOperateMatchId("-");
            matchSettleOperateLog.setOperateForwText("-");
            matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100162.getCode().toString());
            matchSettleOperateLog.setIpAddress(matchSettleTemplateDto.getIpAddress());
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setOperateUserName(matchSettleTemplateDto.getOperatorName());
            //模版类型:
            if (matchSettleTemplateDto.getTemplateType().equals(SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code)) {
                //1.数据商结算权重
                matchSettleOperateLog.setOperateMatchName(OperateLogTypeEnum.SCORES_PD_100067.getCode().toString());
                List<DataSourceSettleWeightDto> dataSourceSettleWeightDtoList = SettleTemplateJsonUtils.tansferDataSourceSettleWeightDtoList(matchSettleTemplateDto.getTemplateJson());
                Map<String, DataSourceSettleWeightDto> dataSourceSettleMap = dataSourceSettleWeightDtoList.stream().collect(Collectors.toMap(DataSourceSettleWeightDto::getDataSourceCode, obj -> obj, (key1, key2) -> key1));
                for (String dataSourceWeightKey : dataSourceSettleMap.keySet()) {
                    StringBuilder dataSourceWeightNew = new StringBuilder();
                    DataSourceSettleWeightDto dataSourceSettleWeightDto = dataSourceSettleMap.get(dataSourceWeightKey);
                    String compareResult = new CompareUtils<DataSourceSettleWeightDto>().compare(new DataSourceSettleWeightDto(), dataSourceSettleWeightDto);
                    if (!StringUtils.isAnyEmpty(compareResult)) {
                        compareResult = DataSourceEncrypEnum.getDataSourceVal(dataSourceWeightKey) + ": " + compareResult;
                        dataSourceWeightNew.append(compareResult);
                        matchSettleOperateLog.setOperateRearText(dataSourceWeightNew.toString());
                        matchSettleOperateLog.setId(null);
                        matchSettleOperateLogRepository.save(matchSettleOperateLog);
                    }
                }
            } else if (matchSettleTemplateDto.getTemplateType().equals(SettleTemplateTypeEnum.GRAY_AREA.code)) {
                //3.灰色区间模版
                matchSettleOperateLog.setOperateMatchName(OperateLogTypeEnum.SCORES_PD_100068.getCode().toString());
                List<GrayAreaSettleDto> grayAreaSettleDtoList = SettleTemplateJsonUtils.tansferGrayAreaList(matchSettleTemplateDto.getTemplateJson());
                Map<String, GrayAreaSettleDto> grayAreaSettleMap = grayAreaSettleDtoList.stream().collect(Collectors.toMap(GrayAreaSettleDto::getDataSourceCode, obj -> obj, (key1, key2) -> key1));
                for (String dataSourceKey : grayAreaSettleMap.keySet()) {
                    StringBuilder dataSourceGrayArea = new StringBuilder();
                    GrayAreaSettleDto dataSourceGrayAreaDto = grayAreaSettleMap.get(dataSourceKey);
                    String compareResult = new CompareUtils<GrayAreaSettleDto>().compare(new GrayAreaSettleDto(), dataSourceGrayAreaDto);
                    if (!StringUtils.isAnyEmpty(compareResult)) {
                        compareResult = DataSourceEncrypEnum.getDataSourceVal(dataSourceKey) + ": " + compareResult;
                        dataSourceGrayArea.append(compareResult);
                        matchSettleOperateLog.setOperateRearText(dataSourceGrayArea.toString());
                        matchSettleOperateLog.setId(null);
                        matchSettleOperateLogRepository.save(matchSettleOperateLog);
                    }
                }
            } else if (matchSettleTemplateDto.getTemplateType().equals(SettleTemplateTypeEnum.COUNT_DOWEN.code)) {
                //3.灰色区间模版
                matchSettleOperateLog.setOperateMatchName(OperateLogTypeEnum.SCORES_PD_100069.getCode().toString());
                List<DownSettleDto> grayAreaSettleDtoList = SettleTemplateJsonUtils.tansferDownList(matchSettleTemplateDto.getTemplateJson());
                DownSettleDto dto = grayAreaSettleDtoList.get(0);
                StringBuilder str = new StringBuilder();
                if (matchSettleTemplateDto.getSportId()==1){
                    str.append("goal15Min:").append(dto.getGoal15Min()).append("corner15Min:").append(dto.getCorner15Min()).append("booking15Min:").append(dto.getBooking15Min());
                }else{
                    str.append("goal:").append(dto.getGoal());
                }

                matchSettleOperateLog.setOperateRearText(str.toString());
                matchSettleOperateLog.setId(null);
                matchSettleOperateLogRepository.save(matchSettleOperateLog);

            }
        } catch (Exception e) {
            log.error("新增联赛等级模板:" + JSONObject.toJSONString(matchSettleTemplateDto) + ", error:", e);
        }
    }


    @Override
    public void deleteTemplateLog(SettleTemplateBatchUpdateDto settleTemplateUpdateDto) {

        try {
            MatchSettleTemplate matchSettleTemplate = matchSettleTemplateRepository.getByIdAndConvert(settleTemplateUpdateDto.getTemplateId());
            if (matchSettleTemplate == null) {
                return;
            }
            Long code = settleTemplateUpdateDto.getSportId() == 1 ? StandardSportTypeEnum.FootBall.getCode() : StandardSportTypeEnum.Basketball.getCode();
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100161.getCode().toString() + "-" + code);
            matchSettleOperateLog.setOperateId(matchSettleTemplate.getId().toString());
            matchSettleOperateLog.setOperateName(matchSettleTemplate.getTemplateName());
            matchSettleOperateLog.setOperateRearText("-");
            matchSettleOperateLog.setOperateParaName("-");
            matchSettleOperateLog.setOperateForwText("-");
            matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100164.getCode().toString());
            if (matchSettleTemplate.getTemplateType().equals(SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code)) {
                //模版类型:1.数据商结算权重
                matchSettleOperateLog.setOperateMatchName(OperateLogTypeEnum.SCORES_PD_100067.getCode().toString());
            } else if (matchSettleTemplate.getTemplateType().equals(SettleTemplateTypeEnum.GRAY_AREA.code)) {
                //3.灰色区间模版
                matchSettleOperateLog.setOperateMatchName(OperateLogTypeEnum.SCORES_PD_100068.getCode().toString());
            }
            matchSettleOperateLog.setIpAddress(settleTemplateUpdateDto.getIpAddress());
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setOperateUserName(settleTemplateUpdateDto.getOperatorName());
            matchSettleOperateLog.setId(null);
            matchSettleOperateLogRepository.save(matchSettleOperateLog);
        } catch (Exception e) {
            log.error("删除联赛等级模板:" + JSONObject.toJSONString(settleTemplateUpdateDto) + ", error:", e);
        }

    }

    @Override
    public void editWeightTemplateLog(MatchSettleTemplateDto matchSettleTemplateOld, SettleWeightTemplateUpdateDto matchSettleTemplateNew) {

        try {
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            Long sportType = matchSettleTemplateNew.getSportId() == 1 ? StandardSportTypeEnum.FootBall.getCode() : StandardSportTypeEnum.Basketball.getCode();
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100161.getCode().toString() + "-" + sportType);
            matchSettleOperateLog.setOperateId(matchSettleTemplateOld.getId().toString());
            matchSettleOperateLog.setOperateName(matchSettleTemplateNew.getTemplateName());
            matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100163.getCode().toString());
            matchSettleOperateLog.setOperateParaName("-");
            matchSettleOperateLog.setIpAddress(matchSettleTemplateNew.getIpAddress());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setOperateUserName(matchSettleTemplateNew.getOperatorName());
            //只修改模板名称增加一行日志
            if (!matchSettleTemplateOld.getTemplateName().equals(matchSettleTemplateNew.getTemplateName())) {
                matchSettleOperateLog.setOperateName(matchSettleTemplateOld.getTemplateName());
                matchSettleOperateLog.setOperateForwText("-");
                matchSettleOperateLog.setOperateRearText(matchSettleTemplateNew.getTemplateName());
                matchSettleOperateLogRepository.save(matchSettleOperateLog);
            }
            //模版类型:
            if (matchSettleTemplateOld.getTemplateType().equals(SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code)) {
                //1.数据商结算权重
                matchSettleOperateLog.setOperateRearText(matchSettleTemplateOld.getTemplateName());
                matchSettleOperateLog.setOperateMatchName(OperateLogTypeEnum.SCORES_PD_100067.getCode().toString());
                //旧的数据对比
                List<DataSourceSettleWeightDto> dataSourceSettleWeightOldList = SettleTemplateJsonUtils.tansferDataSourceSettleWeightDtoList(matchSettleTemplateOld.getTemplateJson());
                Map<String, DataSourceSettleWeightDto> dataSourceSettleWeightOldMap = dataSourceSettleWeightOldList.stream().collect(Collectors.toMap(DataSourceSettleWeightDto::getDataSourceCode, obj -> obj, (key1, key2) -> key1));
                //新的数据对比
                List<DataSourceSettleWeightDto> dataSourceSettleWeightNewList = SettleTemplateJsonUtils.tansferDataSourceSettleWeightDtoList(matchSettleTemplateNew.getWeightJson());
                Map<String, DataSourceSettleWeightDto> dataSourceGraySettleNewMap = dataSourceSettleWeightNewList.stream().collect(Collectors.toMap(DataSourceSettleWeightDto::getDataSourceCode, obj -> obj, (key1, key2) -> key1));
                for (String dataSourceWeightKey : dataSourceGraySettleNewMap.keySet()) {
                    StringBuilder dataSourceSettleWeightNew = new StringBuilder();
                    DataSourceSettleWeightDto dataSourceSettleWeighNewDto = dataSourceGraySettleNewMap.get(dataSourceWeightKey);
                    DataSourceSettleWeightDto dataSourceSettleWeighOldDto = dataSourceSettleWeightOldMap.get(dataSourceWeightKey);
                    String compareResult = new CompareUtils<DataSourceSettleWeightDto>().compare(dataSourceSettleWeighOldDto, dataSourceSettleWeighNewDto);
                    if (!StringUtils.isAnyEmpty(compareResult)) {
                        compareResult = DataSourceEncrypEnum.getDataSourceVal(dataSourceWeightKey) + ": " + compareResult;
                        dataSourceSettleWeightNew.append(compareResult);
                        matchSettleOperateLog.setOperateRearText(dataSourceSettleWeightNew.toString());
                        matchSettleOperateLog.setId(null);
                        matchSettleOperateLogRepository.save(matchSettleOperateLog);
                    }
                }
            }
        } catch (Exception e) {
            log.error("修改联赛数据源权重等级模板:" + JSONObject.toJSONString(matchSettleTemplateNew) + ", error:", e);
        }
    }

    @Override
    public void editGrayAreaTemplateLog(MatchSettleTemplateDto matchSettleTemplateOld, SettleGrayTemplateUpdateDto matchSettleTemplateNew) {

        try {
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            Long sportType = matchSettleTemplateNew.getSportId() == 1 ? StandardSportTypeEnum.FootBall.getCode() : StandardSportTypeEnum.Basketball.getCode();
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100161.getCode().toString() + "-" + sportType);
            matchSettleOperateLog.setOperateId(matchSettleTemplateOld.getId().toString());
            matchSettleOperateLog.setOperateName(matchSettleTemplateNew.getTemplateName());
            matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100163.getCode().toString());
            matchSettleOperateLog.setIpAddress(matchSettleTemplateNew.getIpAddress());
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setOperateUserName(matchSettleTemplateNew.getOperatorName());
            matchSettleOperateLog.setOperateMatchId("-");
            matchSettleOperateLog.setOperateForwText(matchSettleTemplateOld.getTemplateName());
            matchSettleOperateLog.setOperateMatchName(OperateLogTypeEnum.SCORES_PD_100068.getCode().toString());
            //只修改模板名称增加一行日志
            if (!matchSettleTemplateOld.getTemplateName().equals(matchSettleTemplateNew.getTemplateName())) {
                matchSettleOperateLog.setOperateName(matchSettleTemplateOld.getTemplateName());
                matchSettleOperateLog.setOperateForwText("-");
                matchSettleOperateLog.setOperateRearText(matchSettleTemplateNew.getTemplateName());
                matchSettleOperateLog.setId(null);
                matchSettleOperateLogRepository.save(matchSettleOperateLog);
            }
            //模版类型:
            if (matchSettleTemplateOld.getTemplateType().equals(SettleTemplateTypeEnum.GRAY_AREA.code)) {
                //3.灰色区间模版
                List<GrayAreaSettleDto> dataSourceGrayAreaNewList = SettleTemplateJsonUtils.tansferGrayAreaList(matchSettleTemplateNew.getGrayJson());
                Map<String, GrayAreaSettleDto> dataSourceGrayAreaSettleNewMap = dataSourceGrayAreaNewList.stream().collect(Collectors.toMap(GrayAreaSettleDto::getDataSourceCode, obj -> obj, (key1, key2) -> key1));
                List<GrayAreaSettleDto> dataSourceGrayAreaOldList = SettleTemplateJsonUtils.tansferGrayAreaList(matchSettleTemplateOld.getTemplateJson());
                Map<String, GrayAreaSettleDto> dataSourceGrayAreaOldMap = dataSourceGrayAreaOldList.stream().collect(Collectors.toMap(GrayAreaSettleDto::getDataSourceCode, obj -> obj, (key1, key2) -> key1));
                for (String dataSourceGrayAreaKey : dataSourceGrayAreaSettleNewMap.keySet()) {
                    StringBuilder dataSourceGrayAreaNew = new StringBuilder();
                    GrayAreaSettleDto dataSourceSettleGrayAreaNewDto = dataSourceGrayAreaSettleNewMap.get(dataSourceGrayAreaKey);
                    GrayAreaSettleDto dataSourceSettleGrayAreaOldDto = dataSourceGrayAreaOldMap.get(dataSourceGrayAreaKey);
                    String compareResult = new CompareUtils<GrayAreaSettleDto>().compare(dataSourceSettleGrayAreaOldDto, dataSourceSettleGrayAreaNewDto);
                    if (!StringUtils.isAnyEmpty(compareResult)) {
                        compareResult = DataSourceEncrypEnum.getDataSourceVal(dataSourceGrayAreaKey) + ": " + compareResult;
                        dataSourceGrayAreaNew.append(compareResult);
                        matchSettleOperateLog.setOperateRearText(dataSourceGrayAreaNew.toString());
                        matchSettleOperateLog.setId(null);
                        matchSettleOperateLogRepository.save(matchSettleOperateLog);
                    }
                }
            }

        } catch (Exception e) {
            log.error("修改联赛灰色区间等级模板:" + JSONObject.toJSONString(matchSettleTemplateNew) + ", error:", e);
        }
    }

    public void editDownTemplateLog(MatchSettleTemplateDto matchSettleTemplateOld, SettleDownTemplateUpdateDto settleDownTemplateUpdateDto) {

        try {
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            Long sportType = settleDownTemplateUpdateDto.getSportId() == 1 ? StandardSportTypeEnum.FootBall.getCode() : StandardSportTypeEnum.Basketball.getCode();
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100161.getCode().toString() + "-" + sportType);
            matchSettleOperateLog.setOperateId(matchSettleTemplateOld.getId().toString());
            matchSettleOperateLog.setOperateName(settleDownTemplateUpdateDto.getTemplateName());
            matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100163.getCode().toString());
            matchSettleOperateLog.setIpAddress(settleDownTemplateUpdateDto.getIpAddress());
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setOperateUserName(settleDownTemplateUpdateDto.getOperatorName());
            matchSettleOperateLog.setOperateMatchId("-");
            matchSettleOperateLog.setOperateForwText(matchSettleTemplateOld.getTemplateName());
            matchSettleOperateLog.setOperateMatchName(OperateLogTypeEnum.SCORES_PD_100069.getCode().toString());
            //只修改模板名称增加一行日志
            if (!matchSettleTemplateOld.getTemplateName().equals(settleDownTemplateUpdateDto.getTemplateName())) {
                matchSettleOperateLog.setOperateName(matchSettleTemplateOld.getTemplateName());
                matchSettleOperateLog.setOperateForwText("-");
                matchSettleOperateLog.setOperateRearText(settleDownTemplateUpdateDto.getTemplateName());
                matchSettleOperateLogRepository.save(matchSettleOperateLog);
            }
            //模版类型:
            if (matchSettleTemplateOld.getTemplateType().equals(SettleTemplateTypeEnum.COUNT_DOWEN.code)) {
                //3.倒计时模板
                List<DownSettleDto> newList = SettleTemplateJsonUtils.tansferDownList(settleDownTemplateUpdateDto.getDownJson());
                List<DownSettleDto> oldList = SettleTemplateJsonUtils.tansferDownList(matchSettleTemplateOld.getTemplateJson());
                if (!CollectionUtil.isEmpty(newList) && !CollectionUtil.isEmpty(oldList)) {
                    DownSettleDto newDto = newList.get(0);
                    DownSettleDto oldDto = oldList.get(0);
                    StringBuilder dataSourceDownNew = new StringBuilder();

                    String compareResult = new CompareUtils<DownSettleDto>().compare(oldDto, newDto);
                    if (!StringUtils.isAnyEmpty(compareResult)) {
                        dataSourceDownNew.append(compareResult);
                        matchSettleOperateLog.setOperateRearText(dataSourceDownNew.toString());
                        matchSettleOperateLog.setId(null);
                        matchSettleOperateLogRepository.save(matchSettleOperateLog);
                    }
                }
            }

        } catch (Exception e) {
            log.error("修改联赛灰色区间等级模板:" + JSONObject.toJSONString(settleDownTemplateUpdateDto) + ", error:", e);
        }
    }

    @Override
    public void templateBatchUpdateLog(SettleTemplateBatchUpdateDto settleTemplateUpdateDto) {

        try {
            MatchSettleTemplateEntity matchSettleTemplate = matchSettleTemplateRepository.getById(settleTemplateUpdateDto.getTemplateId());
            if (matchSettleTemplate == null) {
                return;
            }
            Long code = settleTemplateUpdateDto.getSportId() == 1 ? StandardSportTypeEnum.FootBall.getCode() : StandardSportTypeEnum.Basketball.getCode();
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100161.getCode().toString() + "-" + code);
            matchSettleOperateLog.setOperateMatchId("-");
            matchSettleOperateLog.setOperateParaName("-");

            switch (settleTemplateUpdateDto.getTemplateType()) {
                case 1://数据商权重模版
                    matchSettleOperateLog.setOperateMatchName(OperateLogTypeEnum.SCORES_PD_100067.getCode().toString());
                    matchSettleOperateLog.setOperateRearText(matchSettleTemplate.getTemplateName());//模板名称
                    break;
                case 3://灰色区间设置
                    matchSettleOperateLog.setOperateMatchName(OperateLogTypeEnum.SCORES_PD_100068.getCode().toString());
                    matchSettleOperateLog.setOperateRearText(settleTemplateUpdateDto.getTournamentLevel().toString() + CategoryUtils.SPLIT_AND + MatchLeagueLevelEnum.getEnumByEn(settleTemplateUpdateDto.getTournamentLevel().toString()));//联赛等级,显示需要中英文切换
                    break;
                case 2://倒计时
                    matchSettleOperateLog.setOperateMatchName(OperateLogTypeEnum.SCORES_PD_100069.getCode().toString());
                    matchSettleOperateLog.setOperateRearText(settleTemplateUpdateDto.getTournamentLevel().toString() + CategoryUtils.SPLIT_AND + MatchLeagueLevelEnum.getEnumByEn(settleTemplateUpdateDto.getTournamentLevel().toString()));//联赛等级,显示需要中英文切换
                    break;
                default:
                    break;
            }
            matchSettleOperateLog.setIpAddress(settleTemplateUpdateDto.getIpAddress());
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setOperateUserName(settleTemplateUpdateDto.getOperatorName());
            //批量修改
            if (settleTemplateUpdateDto.getTournamentManagerId().size() > SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code) {
                //查询联赛的中英文名称
                matchSettleOperateLog.setOperateId(settleTemplateUpdateDto.getTournamentLevel().toString());
                matchSettleOperateLog.setOperateName(MatchLeagueLevelEnum.getEnumByZs(settleTemplateUpdateDto.getTournamentLevel().toString()) + CategoryUtils.SPLIT_AND + MatchLeagueLevelEnum.getEnumByEn(settleTemplateUpdateDto.getTournamentLevel().toString()));
                matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100169.getCode().toString());
                List<String> tournamentIdList = settleTemplateUpdateDto.getTournamentManagerId();
                StringBuilder tournamentIdSb = new StringBuilder();
                for (int i = 0; i < tournamentIdList.size(); i++) {
                    tournamentIdSb.append(tournamentIdList.get(i)).append(",");
                    if ((i > 0 && i % 10 == 0) || ((i + 1) == tournamentIdList.size())) {
                        matchSettleOperateLog.setOperateForwText(tournamentIdSb.toString());
                        matchSettleOperateLog.setOperateRearText(matchSettleTemplate.getTemplateName());
                        matchSettleOperateLog.setId(null);
                        matchSettleOperateLogRepository.save(matchSettleOperateLog);
                        tournamentIdSb = new StringBuilder();
                    }
                }
            }
        } catch (Exception e) {
            log.error("新增联赛等级模板:" + JSONObject.toJSONString(settleTemplateUpdateDto) + ", error:", e);
        }
    }

    @Override
    public void templateBatchSingleUpdateLog(SettleTemplateBatchUpdateDto settleTemplateUpdateDto) {

        try {
            MatchSettleTemplateEntity matchSettleTemplate = matchSettleTemplateRepository.getById(settleTemplateUpdateDto.getTemplateId());
            if (matchSettleTemplate == null) {
                return;
            }
            Long code = settleTemplateUpdateDto.getSportId() == 1 ? StandardSportTypeEnum.FootBall.getCode() : StandardSportTypeEnum.Basketball.getCode();
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100161.getCode().toString() + "-" + code);
            matchSettleOperateLog.setOperateMatchId("-");
            matchSettleOperateLog.setOperateParaName("-");

            switch (settleTemplateUpdateDto.getTemplateType()) {
                case 1://数据商权重模版
                    matchSettleOperateLog.setOperateMatchName(OperateLogTypeEnum.SCORES_PD_100067.getCode().toString());
                    break;
                case 3://灰色区间设置
                    matchSettleOperateLog.setOperateMatchName(OperateLogTypeEnum.SCORES_PD_100068.getCode().toString());
                    break;
                case 2://倒计时
                    matchSettleOperateLog.setOperateMatchName(OperateLogTypeEnum.SCORES_PD_100069.getCode().toString());
                    break;
                default:
                    break;
            }
            if (matchSettleTemplate.getTournamentLevel() == -1) {
                matchSettleOperateLog.setOperateRearText(matchSettleTemplate.getTemplateName());//模板名称
            } else {
                String rearText = MatchLeagueLevelEnum.getEnumByZs(matchSettleTemplate.getTournamentLevel().toString()) + CategoryUtils.SPLIT_AND + MatchLeagueLevelEnum.getEnumByEn(matchSettleTemplate.getTournamentLevel().toString());
                matchSettleOperateLog.setOperateRearText(rearText);//联赛等级,显示需要中英文切换
            }

            matchSettleOperateLog.setIpAddress(settleTemplateUpdateDto.getIpAddress());
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setOperateUserName(settleTemplateUpdateDto.getOperatorName());
            //单条修改
            matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100070.getCode().toString());
            //查询联赛的中英文名称
            StandardSportTournament standardSportTournament = standardSportTournamentService.getItem(settleTemplateUpdateDto.getTournamentIdList().get(0));
            if (standardSportTournament != null && !StringUtils.isAnyEmpty(standardSportTournament.getName()) && !StringUtils.isAnyEmpty(standardSportTournament.getNameSpell())) {
                matchSettleOperateLog.setOperateId(standardSportTournament.getTournamentManagerId());
                matchSettleOperateLog.setOperateName(standardSportTournament.getName() + CategoryUtils.SPLIT_AND + standardSportTournament.getNameSpell());
            }
            //查询修改前的模板名称
            MatchSettleTemplateRelationExample matchSettleTemplateRelationExample = new MatchSettleTemplateRelationExample();
            matchSettleTemplateRelationExample.createCriteria().andStandardTournamentIdEqualTo(settleTemplateUpdateDto.getTournamentIdList().get(0));
            List<MatchSettleTemplateRelationEntity> matchSettleTemplateRelationList = matchSettleTemplateRelationV2Mapper.selectByExample(matchSettleTemplateRelationExample);
            if (!matchSettleTemplateRelationList.isEmpty()) {
                MatchSettleTemplateRelationEntity matchSettleTemplateRelation = matchSettleTemplateRelationList.get(0);
                Long searchId = null;
                switch (settleTemplateUpdateDto.getTemplateType()) {
                    case 1://数据商权重模版
                        searchId = matchSettleTemplateRelation.getTemplateSettleWeightId();
                        break;
                    case 3://灰色区间设置
                        searchId = matchSettleTemplateRelation.getTemplateGrayAreaId();
                        break;
                    case 2://倒计时模板
                        searchId = matchSettleTemplateRelation.getTemplateCountDowenId();
                        break;
                    default:
                        break;
                }
                if (searchId != null) {
                    MatchSettleTemplateEntity matchSettleTemplateOld = matchSettleTemplateRepository.getById(searchId);
                    matchSettleOperateLog.setOperateForwText(matchSettleTemplateOld.getTemplateName());
                } else {
                    matchSettleOperateLog.setOperateForwText(MatchLeagueLevelEnum.getEnumByZs(settleTemplateUpdateDto.getTournamentLevel().toString()) + CategoryUtils.SPLIT_AND + MatchLeagueLevelEnum.getEnumByEn(settleTemplateUpdateDto.getTournamentLevel().toString()));
                }
            }
            matchSettleOperateLogRepository.save(matchSettleOperateLog);
        } catch (Exception e) {
            log.error("新增联赛等级模板:" + JSONObject.toJSONString(settleTemplateUpdateDto) + ", error:", e);
        }

    }


    @Override
    public void editMatchSettleDataSourceGrayAreaLog(MatchSettleTemplateDto matchSettleTemplateOld, SettleGrayTemplateUpdateDto matchSettleTemplateNew, String dataSourceCode, List<MatchSettleOperateLogEntity> willUpdateGrayMatchSettleOperatelogList) {

        try {
            Long code = matchSettleTemplateNew.getSportId() == 1 ? StandardSportTypeEnum.FootBall.getCode() : StandardSportTypeEnum.Basketball.getCode();
            String categoryUtils = matchSettleTemplateNew.getSportId() == 1 ? CategoryUtils.SOCCER : CategoryUtils.BASKETBALL;
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100156.getCode().toString() + "-" + code);
            matchSettleOperateLog.setOperateId("-");
            matchSettleOperateLog.setOperateName(categoryUtils);
            matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100159.getCode().toString());
            matchSettleOperateLog.setIpAddress(matchSettleTemplateNew.getIpAddress());
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setOperateUserName(matchSettleTemplateNew.getOperatorName());
            matchSettleOperateLog.setOperateMatchId("-");
            matchSettleOperateLog.setOperateMatchName(categoryUtils);
            //模版类型:
            if (matchSettleTemplateOld.getTemplateType().equals(SettleTemplateTypeEnum.GRAY_AREA.code)) {
                //3.灰色区间模版
                List<GrayAreaSettleDto> dataSourceGrayAreaNewList = SettleTemplateJsonUtils.tansferGrayAreaList(matchSettleTemplateNew.getGrayJson());
                Map<String, GrayAreaSettleDto> dataSourceGrayAreaSettleNewMap = dataSourceGrayAreaNewList.stream().collect(Collectors.toMap(GrayAreaSettleDto::getDataSourceCode, obj -> obj, (key1, key2) -> key1));
                List<GrayAreaSettleDto> dataSourceGrayAreaOldList = SettleTemplateJsonUtils.tansferGrayAreaList(matchSettleTemplateOld.getTemplateJson());
                Map<String, GrayAreaSettleDto> dataSourceGrayAreaOldMap = dataSourceGrayAreaOldList.stream().collect(Collectors.toMap(GrayAreaSettleDto::getDataSourceCode, obj -> obj, (key1, key2) -> key1));
                for (String dataSourceGrayAreaKey : dataSourceGrayAreaSettleNewMap.keySet()) {
                    StringBuilder dataSourceGrayAreaNew = new StringBuilder();
                    GrayAreaSettleDto dataSourceSettleGrayAreaNewDto = dataSourceGrayAreaSettleNewMap.get(dataSourceGrayAreaKey);
                    GrayAreaSettleDto dataSourceSettleGrayAreaOldDto = dataSourceGrayAreaOldMap.get(dataSourceGrayAreaKey);
                    String compareResult = new CompareUtils<GrayAreaSettleDto>().compare(dataSourceSettleGrayAreaOldDto, dataSourceSettleGrayAreaNewDto);
                    if (!StringUtils.isAnyEmpty(compareResult)) {
                        MatchSettleOperateLogEntity gayLog = new MatchSettleOperateLogEntity();
                        gayLog.setOperateModule(matchSettleOperateLog.getOperateModule());
                        gayLog.setOperateId(matchSettleOperateLog.getOperateId());
                        gayLog.setOperateName(matchSettleOperateLog.getOperateName());
                        gayLog.setOperateType(matchSettleOperateLog.getOperateType()); // 使用默认的 "编辑数据商权重"
                        gayLog.setIpAddress(matchSettleOperateLog.getIpAddress());
                        gayLog.setCreateTime(matchSettleOperateLog.getCreateTime());
                        gayLog.setModifyTime(matchSettleOperateLog.getModifyTime());
                        gayLog.setOperateUserName(matchSettleOperateLog.getOperateUserName());
                        gayLog.setOperateMatchName(matchSettleOperateLog.getOperateMatchName());
                        gayLog.setOperateForwText(matchSettleOperateLog.getOperateForwText());
                        gayLog.setOperateParaName(DataSourceEncrypEnum.getDataSourceVal(dataSourceCode) + " - Level " + matchSettleTemplateOld.getTournamentLevel() + " - " + DataSourceEncrypEnum.getDataSourceVal(dataSourceGrayAreaKey));
                        compareResult = DataSourceEncrypEnum.getDataSourceVal(dataSourceGrayAreaKey) + ": " + compareResult;
                        dataSourceGrayAreaNew.append(compareResult);
                        gayLog.setOperateRearText(dataSourceGrayAreaNew.toString());
                        willUpdateGrayMatchSettleOperatelogList.add(gayLog);
                    }
                }
            }

        } catch (Exception e) {
            log.error("修改联赛灰色区间等级设置:" + JSONObject.toJSONString(matchSettleTemplateNew) + ", error:", e);
        }
    }

    public void editMatchSettleDataSourceWeightLog(MatchSettleTemplateDto matchSettleTemplateOld, SettleWeightTemplateUpdateDto matchSettleTemplateNew, String dataSourceCode, List<MatchSettleOperateLogEntity> willUpdateMatchSettleOperateLogList) {

        try {
            Long code = matchSettleTemplateNew.getSportId() == 1 ? StandardSportTypeEnum.FootBall.getCode() : StandardSportTypeEnum.Basketball.getCode();
            String categoryUtils = matchSettleTemplateNew.getSportId() == 1 ? CategoryUtils.SOCCER : CategoryUtils.BASKETBALL;
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100156.getCode().toString() + "-" + code);
            matchSettleOperateLog.setOperateId("-");
            matchSettleOperateLog.setOperateName(categoryUtils);
            matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100159.getCode().toString());
            matchSettleOperateLog.setIpAddress(matchSettleTemplateNew.getIpAddress());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setOperateUserName(matchSettleTemplateNew.getOperatorName());
            matchSettleOperateLog.setOperateMatchName(categoryUtils);
            matchSettleOperateLog.setOperateForwText("-");
            //模版类型:
            if (matchSettleTemplateOld.getTemplateType().equals(SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code)) {

                //旧的数据对比
                List<DataSourceSettleWeightDto> dataSourceSettleWeightOldList = SettleTemplateJsonUtils.tansferDataSourceSettleWeightDtoList(matchSettleTemplateOld.getTemplateJson());
                Map<String, DataSourceSettleWeightDto> dataSourceSettleWeightOldMap = dataSourceSettleWeightOldList.stream().collect(Collectors.toMap(DataSourceSettleWeightDto::getDataSourceCode, obj -> obj, (key1, key2) -> key1));
                //新的数据对比
                List<DataSourceSettleWeightDto> dataSourceSettleWeightNewList = SettleTemplateJsonUtils.tansferDataSourceSettleWeightDtoList(matchSettleTemplateNew.getWeightJson());
                Map<String, DataSourceSettleWeightDto> dataSourceGraySettleNewMap = dataSourceSettleWeightNewList.stream().collect(Collectors.toMap(DataSourceSettleWeightDto::getDataSourceCode, obj -> obj, (key1, key2) -> key1));
                for (String dataSourceWeightKey : dataSourceGraySettleNewMap.keySet()) {
                    StringBuilder dataSourceSettleWeightNew = new StringBuilder();
                    DataSourceSettleWeightDto dataSourceSettleWeighNewDto = dataSourceGraySettleNewMap.get(dataSourceWeightKey);
                    DataSourceSettleWeightDto dataSourceSettleWeighOldDto = dataSourceSettleWeightOldMap.get(dataSourceWeightKey);
                    
                    // 排除 heartbeatSecond 和 singleDatasourceSettleSwitch，这两个字段需要单独处理
                    List<String> ignoreFields = Arrays.asList("heartbeatSecond", "singleDatasourceSettleSwitch");
                    String compareResult = new CompareUtils<DataSourceSettleWeightDto>().compare(dataSourceSettleWeighOldDto, dataSourceSettleWeighNewDto, ignoreFields);
                    if (!StringUtils.isAnyEmpty(compareResult)) {
                        MatchSettleOperateLogEntity weightLog = new MatchSettleOperateLogEntity();
                        weightLog.setOperateModule(matchSettleOperateLog.getOperateModule());
                        weightLog.setOperateId(matchSettleOperateLog.getOperateId());
                        weightLog.setOperateName(matchSettleOperateLog.getOperateName());
                        weightLog.setOperateType(matchSettleOperateLog.getOperateType()); // 使用默认的 "编辑数据商权重"
                        weightLog.setIpAddress(matchSettleOperateLog.getIpAddress());
                        weightLog.setCreateTime(matchSettleOperateLog.getCreateTime());
                        weightLog.setModifyTime(matchSettleOperateLog.getModifyTime());
                        weightLog.setOperateUserName(matchSettleOperateLog.getOperateUserName());
                        weightLog.setOperateMatchName(matchSettleOperateLog.getOperateMatchName());
                        weightLog.setOperateForwText(matchSettleOperateLog.getOperateForwText());
                        weightLog.setOperateParaName(DataSourceEncrypEnum.getDataSourceVal(dataSourceCode) + " - Level " + matchSettleTemplateOld.getTournamentLevel() + " - " + DataSourceEncrypEnum.getDataSourceVal(dataSourceWeightKey));
                        compareResult = DataSourceEncrypEnum.getDataSourceVal(dataSourceWeightKey) + ": " + compareResult;
                        dataSourceSettleWeightNew.append(compareResult);
                        weightLog.setOperateRearText(dataSourceSettleWeightNew.toString());
                        willUpdateMatchSettleOperateLogList.add(weightLog);
                    }
                    
                    // 单独处理 heartbeatSecond 字段
                    if (dataSourceSettleWeighOldDto != null && dataSourceSettleWeighNewDto != null) {
                        Integer oldHeartbeat = dataSourceSettleWeighOldDto.getHeartbeatSecond();
                        Integer newHeartbeat = dataSourceSettleWeighNewDto.getHeartbeatSecond();
                        if (oldHeartbeat != null && newHeartbeat != null && !oldHeartbeat.equals(newHeartbeat)) {
                            MatchSettleOperateLogEntity heartbeatLog = new MatchSettleOperateLogEntity();
                            heartbeatLog.setOperateModule(matchSettleOperateLog.getOperateModule());
                            heartbeatLog.setOperateId(matchSettleOperateLog.getOperateId());
                            heartbeatLog.setOperateName(matchSettleOperateLog.getOperateName());
                            heartbeatLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100171.getCode().toString()); // "数据商心跳"
                            heartbeatLog.setIpAddress(matchSettleOperateLog.getIpAddress());
                            heartbeatLog.setCreateTime(matchSettleOperateLog.getCreateTime());
                            heartbeatLog.setModifyTime(matchSettleOperateLog.getModifyTime());
                            heartbeatLog.setOperateUserName(matchSettleOperateLog.getOperateUserName());
                            heartbeatLog.setOperateMatchName(matchSettleOperateLog.getOperateMatchName());
                            heartbeatLog.setOperateParaName(DataSourceEncrypEnum.getDataSourceVal(dataSourceWeightKey) + " - Level " + matchSettleTemplateOld.getTournamentLevel() + " - " + DataSourceEncrypEnum.getDataSourceVal(dataSourceWeightKey));
                            heartbeatLog.setOperateForwText(oldHeartbeat == null ? "-" : String.valueOf(oldHeartbeat));
                            heartbeatLog.setOperateRearText(newHeartbeat == null ? "-" : String.valueOf(newHeartbeat));
                            willUpdateMatchSettleOperateLogList.add(heartbeatLog);
                        }
                    }
                    
                    // 单独处理 singleDatasourceSettleSwitch 字段
                    if (dataSourceSettleWeighOldDto != null && dataSourceSettleWeighNewDto != null) {
                        Integer oldSwitch = dataSourceSettleWeighOldDto.getSingleDatasourceSettleSwitch();
                        Integer newSwitch = dataSourceSettleWeighNewDto.getSingleDatasourceSettleSwitch();
                        if (oldSwitch != null && newSwitch != null && !oldSwitch.equals(newSwitch)) {
                            MatchSettleOperateLogEntity switchLog = new MatchSettleOperateLogEntity();
                            switchLog.setOperateModule(matchSettleOperateLog.getOperateModule());
                            switchLog.setOperateId(matchSettleOperateLog.getOperateId());
                            switchLog.setOperateName(matchSettleOperateLog.getOperateName());
                            switchLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100172.getCode().toString()); // "15分钟/5分钟单数据源结算"
                            switchLog.setIpAddress(matchSettleOperateLog.getIpAddress());
                            switchLog.setCreateTime(matchSettleOperateLog.getCreateTime());
                            switchLog.setModifyTime(matchSettleOperateLog.getModifyTime());
                            switchLog.setOperateUserName(matchSettleOperateLog.getOperateUserName());
                            switchLog.setOperateMatchName(matchSettleOperateLog.getOperateMatchName());
                            switchLog.setOperateParaName(DataSourceEncrypEnum.getDataSourceVal(dataSourceWeightKey) + " - singleDataSourceSettle switch");
                            switchLog.setOperateForwText("Level " + matchSettleTemplateOld.getTournamentLevel() + "-" + (oldSwitch > 0 ? CategoryUtils.ON : CategoryUtils.OFF));
                            switchLog.setOperateRearText("Level " + matchSettleTemplateOld.getTournamentLevel() + "-" + (newSwitch > 0 ? CategoryUtils.ON : CategoryUtils.OFF));
                            willUpdateMatchSettleOperateLogList.add(switchLog);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("修改联赛数据源权重等级设置:" + JSONObject.toJSONString(matchSettleTemplateNew) + ", error:", e);
        }
    }

    @Override
    public void editMatchSettleDataSourceSwitchLog(MatchSettleDataSourceSwitch oldSwitch, MatchSettleDataSourceSwitchDto newSwitch) {

        try {
            Long sport = newSwitch.getSportId() == 1 ? StandardSportTypeEnum.FootBall.getCode() : StandardSportTypeEnum.Basketball.getCode();
            String sportName = newSwitch.getSportId() == 1 ? CategoryUtils.SOCCER : CategoryUtils.BASKETBALL;
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100156.getCode().toString() + "-" + sport);
            matchSettleOperateLog.setOperateId("-");
            matchSettleOperateLog.setOperateName(sportName);
            matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100159.getCode().toString());
            matchSettleOperateLog.setIpAddress(newSwitch.getIpAddress());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setOperateUserName(newSwitch.getOperatorName());
            matchSettleOperateLog.setOperateMatchName(sportName);
            if (newSwitch.getSportId() == 1 && !oldSwitch.getBooking().equals(newSwitch.getBooking())) {
                matchSettleOperateLog.setOperateParaName(DataSourceEncrypEnum.getDataSourceVal(oldSwitch.getDataSourceCode()) + " - " + CategoryUtils.bookingSwitch);
                matchSettleOperateLog.setOperateForwText(oldSwitch.getBooking() > 0 ? CategoryUtils.ON : CategoryUtils.OFF);
                matchSettleOperateLog.setOperateRearText(newSwitch.getBooking() > 0 ? CategoryUtils.ON : CategoryUtils.OFF);
                matchSettleOperateLog.setId(null);
                matchSettleOperateLogRepository.save(matchSettleOperateLog);
            }
            if (newSwitch.getSportId() == 1 && !oldSwitch.getCorner().equals(newSwitch.getCorner())) {
                matchSettleOperateLog.setOperateParaName(DataSourceEncrypEnum.getDataSourceVal(oldSwitch.getDataSourceCode()) + " - " + CategoryUtils.cornerSwitch);
                matchSettleOperateLog.setOperateForwText(oldSwitch.getCorner() > 0 ? CategoryUtils.ON : CategoryUtils.OFF);
                matchSettleOperateLog.setOperateRearText(newSwitch.getCorner() > 0 ? CategoryUtils.ON : CategoryUtils.OFF);
                matchSettleOperateLog.setId(null);
                matchSettleOperateLogRepository.save(matchSettleOperateLog);
            }
            if (!oldSwitch.getGoal().equals(newSwitch.getGoal())) {
                matchSettleOperateLog.setOperateParaName(DataSourceEncrypEnum.getDataSourceVal(oldSwitch.getDataSourceCode()) + " - " + CategoryUtils.goalSwitch);
                matchSettleOperateLog.setOperateForwText(oldSwitch.getGoal() > 0 ? CategoryUtils.ON : CategoryUtils.OFF);
                matchSettleOperateLog.setOperateRearText(newSwitch.getGoal() > 0 ? CategoryUtils.ON : CategoryUtils.OFF);
                matchSettleOperateLog.setId(null);
                matchSettleOperateLogRepository.save(matchSettleOperateLog);
            }
            if (!oldSwitch.getTopWeight().equals(newSwitch.getTopWeight())) {
                matchSettleOperateLog.setOperateParaName(DataSourceEncrypEnum.getDataSourceVal(oldSwitch.getDataSourceCode()) + " - " + CategoryUtils.topWeightSwitch);
                matchSettleOperateLog.setOperateForwText(oldSwitch.getTopWeight() > 0 ? CategoryUtils.ON : CategoryUtils.OFF);
                matchSettleOperateLog.setOperateRearText(newSwitch.getTopWeight() > 0 ? CategoryUtils.ON : CategoryUtils.OFF);
                matchSettleOperateLog.setId(null);
                matchSettleOperateLogRepository.save(matchSettleOperateLog);
            }
            if (!oldSwitch.getGray().equals(newSwitch.getGray())) {
                matchSettleOperateLog.setOperateParaName(DataSourceEncrypEnum.getDataSourceVal(oldSwitch.getDataSourceCode()) + " - " + CategoryUtils.graySwitch);
                matchSettleOperateLog.setOperateForwText(oldSwitch.getGray() > 0 ? CategoryUtils.ON : CategoryUtils.OFF);
                matchSettleOperateLog.setOperateRearText(newSwitch.getGray() > 0 ? CategoryUtils.ON : CategoryUtils.OFF);
                matchSettleOperateLog.setId(null);
                matchSettleOperateLogRepository.save(matchSettleOperateLog);
            }

            // 足球：数据商心跳 & 15/5分钟单数据源结算开关日志
            if (newSwitch.getSportId() == 1) {
                // dataSourceHeartbeat：0=开关未开启或维护状态（前端不展示），1=开关开启且连接，2=开关开启且断连
                // 这里是“心跳开关”配置：1开0关（沿用其他开关的ON/OFF展示）
                if (oldSwitch.getDataSourceHeartbeat() != null && newSwitch.getDataSourceHeartbeat() != null
                        && !oldSwitch.getDataSourceHeartbeat().equals(newSwitch.getDataSourceHeartbeat())) {
                    MatchSettleOperateLogEntity heartbeatSwitchLog = new MatchSettleOperateLogEntity();
                    heartbeatSwitchLog.setOperateModule(matchSettleOperateLog.getOperateModule());
                    heartbeatSwitchLog.setOperateId(matchSettleOperateLog.getOperateId());
                    heartbeatSwitchLog.setOperateName(matchSettleOperateLog.getOperateName());
                    heartbeatSwitchLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100171.getCode().toString()); // "数据商心跳"
                    heartbeatSwitchLog.setIpAddress(matchSettleOperateLog.getIpAddress());
                    heartbeatSwitchLog.setCreateTime(matchSettleOperateLog.getCreateTime());
                    heartbeatSwitchLog.setModifyTime(matchSettleOperateLog.getModifyTime());
                    heartbeatSwitchLog.setOperateUserName(matchSettleOperateLog.getOperateUserName());
                    heartbeatSwitchLog.setOperateMatchName(matchSettleOperateLog.getOperateMatchName());
                    heartbeatSwitchLog.setOperateParaName(DataSourceEncrypEnum.getDataSourceVal(oldSwitch.getDataSourceCode()) + " - dataSourceHeartbeat switch");
                    heartbeatSwitchLog.setOperateForwText(oldSwitch.getDataSourceHeartbeat() > 0 ? CategoryUtils.ON : CategoryUtils.OFF);
                    heartbeatSwitchLog.setOperateRearText(newSwitch.getDataSourceHeartbeat() > 0 ? CategoryUtils.ON : CategoryUtils.OFF);
                    heartbeatSwitchLog.setId(null);
                    matchSettleOperateLogRepository.save(heartbeatSwitchLog);
                }

                if (oldSwitch.getSingleDataSourceSettle() != null && newSwitch.getSingleDataSourceSettle() != null
                        && !oldSwitch.getSingleDataSourceSettle().equals(newSwitch.getSingleDataSourceSettle())) {
                    MatchSettleOperateLogEntity singleDataSourceSwitchLog = new MatchSettleOperateLogEntity();
                    singleDataSourceSwitchLog.setOperateModule(matchSettleOperateLog.getOperateModule());
                    singleDataSourceSwitchLog.setOperateId(matchSettleOperateLog.getOperateId());
                    singleDataSourceSwitchLog.setOperateName(matchSettleOperateLog.getOperateName());
                    singleDataSourceSwitchLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100172.getCode().toString()); // "15分钟/5分钟单数据源结算"
                    singleDataSourceSwitchLog.setIpAddress(matchSettleOperateLog.getIpAddress());
                    singleDataSourceSwitchLog.setCreateTime(matchSettleOperateLog.getCreateTime());
                    singleDataSourceSwitchLog.setModifyTime(matchSettleOperateLog.getModifyTime());
                    singleDataSourceSwitchLog.setOperateUserName(matchSettleOperateLog.getOperateUserName());
                    singleDataSourceSwitchLog.setOperateMatchName(matchSettleOperateLog.getOperateMatchName());
                    singleDataSourceSwitchLog.setOperateParaName(DataSourceEncrypEnum.getDataSourceVal(oldSwitch.getDataSourceCode()) + " - singleDataSourceSettle switch");
                    singleDataSourceSwitchLog.setOperateForwText(oldSwitch.getSingleDataSourceSettle() > 0 ? CategoryUtils.ON : CategoryUtils.OFF);
                    singleDataSourceSwitchLog.setOperateRearText(newSwitch.getSingleDataSourceSettle() > 0 ? CategoryUtils.ON : CategoryUtils.OFF);
                    singleDataSourceSwitchLog.setId(null);
                    matchSettleOperateLogRepository.save(singleDataSourceSwitchLog);
                }
            }
            if (!oldSwitch.getDataSourceCode().equals(newSwitch.getDataSourceCode())) {
                matchSettleOperateLog.setOperateParaName(DataSourceEncrypEnum.getDataSourceVal(oldSwitch.getDataSourceCode()) + " - " + CategoryUtils.dataSourceCode);
                matchSettleOperateLog.setOperateForwText(DataSourceEncrypEnum.getDataSourceVal(oldSwitch.getDataSourceCode()));
                matchSettleOperateLog.setOperateRearText(DataSourceEncrypEnum.getDataSourceVal(newSwitch.getDataSourceCode()));
                matchSettleOperateLog.setId(null);
                matchSettleOperateLogRepository.save(matchSettleOperateLog);
            }


        } catch (Exception e) {
            log.error("修改联赛数据源开关or数据源编码设置:" + JSONObject.toJSONString(newSwitch) + ", error:", e);
        }
    }

    @Override
    public void addOrDelDataSourceLog(MatchSettleDataSourceWeightAndSwitchDto matchSettleDataSourceWeightAndSwitchDto, Integer tag) {
        try {
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            Long sport = matchSettleDataSourceWeightAndSwitchDto.getSportId() == 1 ? StandardSportTypeEnum.FootBall.getCode() : StandardSportTypeEnum.Basketball.getCode();
            String sportName = matchSettleDataSourceWeightAndSwitchDto.getSportId() == 1 ? CategoryUtils.SOCCER : CategoryUtils.BASKETBALL;
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100156.getCode().toString() + "-" + sport);
            matchSettleOperateLog.setOperateId("-");
            matchSettleOperateLog.setOperateName(sportName);
            if (tag.equals(0)) {
                matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100157.getCode().toString());
            } else {
                matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100158.getCode().toString());
            }

            matchSettleOperateLog.setIpAddress(matchSettleDataSourceWeightAndSwitchDto.getIpAddress());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setOperateUserName(matchSettleDataSourceWeightAndSwitchDto.getOperatorName());
            matchSettleOperateLog.setOperateMatchName(sportName);
            matchSettleOperateLog.setOperateParaName(DataSourceEncrypEnum.getDataSourceVal(matchSettleDataSourceWeightAndSwitchDto.getDataSourceCode()));
            matchSettleOperateLog.setOperateForwText("-");
            matchSettleOperateLog.setOperateRearText("-");
            matchSettleOperateLogRepository.save(matchSettleOperateLog);


        } catch (Exception e) {
            log.error("新增/删除数据商:" + JSONObject.toJSONString(matchSettleDataSourceWeightAndSwitchDto.getDataSourceCode()) + ", error:", e);
        }
    }

    @Override
    public void updateDataSourceCodeLog(DataSourceWeightUpdateDto dataSourceWeightUpdateDto) {
        try {
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100156.getCode().toString() + "-" + StandardSportTypeEnum.FootBall.getCode());
            matchSettleOperateLog.setOperateId("-");
            matchSettleOperateLog.setOperateName(CategoryUtils.SOCCER);
            matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100159.getCode().toString());


            matchSettleOperateLog.setIpAddress(dataSourceWeightUpdateDto.getIpAddress());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setOperateUserName(dataSourceWeightUpdateDto.getOperatorName());
            matchSettleOperateLog.setOperateMatchName(CategoryUtils.SOCCER);
            matchSettleOperateLog.setOperateParaName(DataSourceEncrypEnum.getDataSourceVal(dataSourceWeightUpdateDto.getDataSourceCode()));
            matchSettleOperateLog.setOperateForwText(DataSourceEncrypEnum.getDataSourceVal(dataSourceWeightUpdateDto.getDataSourceCode()));
            matchSettleOperateLog.setOperateRearText(DataSourceEncrypEnum.getDataSourceVal(dataSourceWeightUpdateDto.getNewDataSourceCode()));
            matchSettleOperateLogRepository.save(matchSettleOperateLog);


        } catch (Exception e) {
            log.error("修改数据商编码:{}, error:{}", JSONObject.toJSONString(dataSourceWeightUpdateDto.getDataSourceCode()), e);
        }
    }

    @Override
    public void editMatchDataSourceWeightConfigLog(MatchSettleDataSourceWeightConfig oldConfig, MatchSettleDataSourceWeightConfigDto newConfig, List<MatchSettleOperateLogEntity> matchSettleOperateLogEntityList) {

        try {
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            Long sport = newConfig.getSportId() == 1 ? StandardSportTypeEnum.FootBall.getCode() : StandardSportTypeEnum.Basketball.getCode();
            String sportName = newConfig.getSportId() == 1 ? CategoryUtils.SOCCER : CategoryUtils.BASKETBALL;
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100156.getCode().toString() + "-" + sport);
            matchSettleOperateLog.setOperateId("-");
            matchSettleOperateLog.setOperateName(sportName);
            matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100170.getCode().toString());
            matchSettleOperateLog.setIpAddress(newConfig.getIpAddress());
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setOperateUserName(newConfig.getOperatorName());
            matchSettleOperateLog.setOperateMatchId("-");
            matchSettleOperateLog.setOperateMatchName(sportName);

            matchSettleOperateLog.setOperateParaName(DataSourceEncrypEnum.getDataSourceVal(oldConfig.getDataSourceCode()) + " - Level " + oldConfig.getTournamentLevel());
            matchSettleOperateLog.setOperateForwText("-");
            matchSettleOperateLog.setOperateRearText(DataSourceEncrypEnum.getDataSourceVal(oldConfig.getDataSourceCode()) + ": [" + oldConfig.getWeightNum().toString() + " -> " + newConfig.getWeightNum().toString() + "]");
            //matchSettleOperateLogRepository.save(matchSettleOperateLog);
            matchSettleOperateLogEntityList.add(matchSettleOperateLog);
        } catch (Exception e) {
            log.error("修改联赛权重上限设置:{}, error:{}", JSONObject.toJSONString(newConfig), e);
        }
    }

    @Override
    public void spOddsResultAddLog(EditMatchSettleSPOddsDto editMatchSettleSPOddsDto, MatchSettleSpOdds oddsBefore, MatchSettleSpOdds odds, StandardMatchInfo standardMatchInfo, String type,List<MatchSettleOperateLogEntity> operateLogEntityList) {
        try {
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.type_7.getCode() + "-" + StandardSportTypeEnum.getEnum(standardMatchInfo.getSportId()).getCode());
            matchSettleOperateLog.setOperateId(standardMatchInfo.getMatchManageId());
            matchSettleOperateLog.setOperateName(OperateLogTypeEnum.type_16.getCode().toString());
            matchSettleOperateLog.setOperateType(type);
            matchSettleOperateLog.setIpAddress(editMatchSettleSPOddsDto.getIpAddress());
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setOperateUserName(editMatchSettleSPOddsDto.getOperatorName());
            matchSettleOperateLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchSettleOperateLog.setOperateMatchName(CategoryUtils.SOCCER);
            String paraName = "{\"zs\":\"" + oddsBefore.getOddsNameCn() + "\",\"en\":\"" + oddsBefore.getOddsNameEn() + "\"}";
            matchSettleOperateLog.setOperateParaName(paraName);

            matchSettleOperateLog.setOperateForwText(oddsBefore.getSettleResult() != null ? oddsBefore.getSettleResult().toString() : "-");

            matchSettleOperateLog.setOperateRearText(odds.getSettleResult() != null ? odds.getSettleResult().toString() : "-");
            //matchSettleOperateLogRepository.save(matchSettleOperateLog);

            operateLogEntityList.add(matchSettleOperateLog);
        } catch (Exception e) {
            log.error("记录结算特殊玩法日志:" + JSONObject.toJSONString(editMatchSettleSPOddsDto) + ", error:", e);
        }

    }

    @Override
    public void spOddsResultAddLog(EditMatchSettleSPOddsDto editMatchSettleSPOddsDto,
                                   MatchSettleSpOddsEntity oddsBefore,
                                   MatchSettleSpOddsEntity odds,
                                   StandardMatchInfo standardMatchInfo,
                                   String type,
                                   List<MatchSettleOperateLogEntity> operateLogEntityList) {
        try {
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.type_7.getCode() + "-" + StandardSportTypeEnum.getEnum(standardMatchInfo.getSportId()).getCode());
            matchSettleOperateLog.setOperateId(odds.getId().toString());
            matchSettleOperateLog.setOperateName(OperateLogTypeEnum.type_16.getCode().toString());
            matchSettleOperateLog.setOperateType(type);
            matchSettleOperateLog.setIpAddress(editMatchSettleSPOddsDto.getIpAddress());
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setOperateUserName(editMatchSettleSPOddsDto.getOperatorName());
            matchSettleOperateLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchSettleOperateLog.setOperateMatchName(CategoryUtils.SOCCER);
            String paraName = "{\"zs\":\"" + oddsBefore.getOddsNameCn() + "\",\"en\":\"" + oddsBefore.getOddsNameEn() + "\"}";
            matchSettleOperateLog.setOperateParaName(paraName);

            matchSettleOperateLog.setOperateForwText(oddsBefore.getSettleResult() != null ? oddsBefore.getSettleResult().toString() : "-");

            matchSettleOperateLog.setOperateRearText(odds.getSettleResult() != null ? odds.getSettleResult().toString() : "-");
            //matchSettleOperateLogRepository.save(matchSettleOperateLog);
            if (operateLogEntityList == null){
                matchSettleOperateLogRepository.save(matchSettleOperateLog);
            } else {
                operateLogEntityList.add(matchSettleOperateLog);
            }

        } catch (Exception e) {
            log.error("记录结算特殊玩法日志:" + JSONObject.toJSONString(editMatchSettleSPOddsDto) + ", error:", e);
        }

    }

    @Override
    public void editBasketBallRealTimeConfigLog(LimitSwitchDto oldConfig, LimitSwitchDto newConfig, SettleTimeLimitDto dto, List<MatchSettleOperateLogEntity> willSaveOperateLogList) {
        try {
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.SCORES_SETTLE_100047.getCode().toString());
            matchSettleOperateLog.setOperateId("-");
            matchSettleOperateLog.setOperateName(CategoryUtils.BASKETBALL);
            matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_SETTLE_100049.getCode().toString());
            matchSettleOperateLog.setIpAddress(dto.getIpAddress());
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setOperateUserName(dto.getOperatorName());
            matchSettleOperateLog.setOperateMatchId("-");
            matchSettleOperateLog.setOperateMatchName(CategoryUtils.BASKETBALL);

            matchSettleOperateLog.setOperateParaName("Level - "+ oldConfig.getLevel());
            matchSettleOperateLog.setOperateForwText(oldConfig.getRealTimeOnOff()?"On":"Off");
            matchSettleOperateLog.setOperateRearText(newConfig.getRealTimeOnOff()?"On":"Off");

            willSaveOperateLogList.add(matchSettleOperateLog);

        } catch (Exception e) {
            log.error("修改篮球结算倒计时限制:{}, error:{}", JSONObject.toJSONString(newConfig), e);
        }
    }

    @Override
    public void editBasketBallTimeLimitConfigLog(LimitSwitchDto oldConfig, LimitSwitchDto newConfig, SettleTimeLimitDto dto, List<MatchSettleOperateLogEntity> matchSettleOperateLogEntityList) {
        try {
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.SCORES_SETTLE_100047.getCode().toString());
            matchSettleOperateLog.setOperateId("-");
            matchSettleOperateLog.setOperateName(CategoryUtils.BASKETBALL);
            matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_SETTLE_100046.getCode().toString());
            matchSettleOperateLog.setIpAddress(dto.getIpAddress());
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setOperateUserName(dto.getOperatorName());
            matchSettleOperateLog.setOperateMatchId("-");
            matchSettleOperateLog.setOperateMatchName(CategoryUtils.BASKETBALL);

            matchSettleOperateLog.setOperateParaName("Level - " + oldConfig.getLevel());
            matchSettleOperateLog.setOperateForwText("[" + oldConfig.getLimitSecond() + "]");
            matchSettleOperateLog.setOperateRearText("[" + newConfig.getLimitSecond() + "]");

            matchSettleOperateLogEntityList.add(matchSettleOperateLog);
        } catch (Exception e) {
            log.error("修改篮球结算倒计时限制:{}, error:{}", JSONObject.toJSONString(newConfig), e);
        }
    }

    @Override
    public void editBasketBallSetUpConfigLog(LimitSwitchDto oldConfig, LimitSwitchDto newConfig, SettleTimeLimitDto dto, List<MatchSettleOperateLogEntity> matchSettleOperateLogEntityList) {
        try {
            MatchSettleOperateLogEntity matchSettleOperateLog = new MatchSettleOperateLogEntity();
            matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.SCORES_SETTLE_100047.getCode().toString());
            matchSettleOperateLog.setOperateId("-");
            matchSettleOperateLog.setOperateName(CategoryUtils.BASKETBALL);
            matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_SETTLE_100045.getCode().toString());
            matchSettleOperateLog.setIpAddress(dto.getIpAddress());
            matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            matchSettleOperateLog.setOperateUserName(dto.getOperatorName());
            matchSettleOperateLog.setOperateMatchId("-");
            matchSettleOperateLog.setOperateMatchName(CategoryUtils.BASKETBALL);

            matchSettleOperateLog.setOperateParaName("Level - " + oldConfig.getLevel());
            matchSettleOperateLog.setOperateForwText(oldConfig.getOnOff() ? "On" : "Off");
            matchSettleOperateLog.setOperateRearText(newConfig.getOnOff() ? "On" : "Off");
            matchSettleOperateLogEntityList.add(matchSettleOperateLog);
        } catch (Exception e) {
            log.error("修改篮球主数据源带入开关:{}, error:{}", JSONObject.toJSONString(newConfig), e);
        }
    }


    private String buildSPText(String result) {
        return null;
    }

}

