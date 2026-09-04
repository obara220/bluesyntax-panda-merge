package com.panda.merge.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.advertise.dto.FootBallScoreVo;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.event.FootBallEventService;
import com.panda.merge.advertise.mq.EventProducer;
import com.panda.merge.advertise.service.BasketBallScoreService;
import com.panda.merge.advertise.service.FootBallScoreService;
import com.panda.merge.advertise.service.IceHockeyScoreService;
import com.panda.merge.advertise.utils.MatchEventUtils;
import com.panda.merge.advertise.utils.MatchPeriodUtils;
import com.panda.merge.common.enums.BasketballSixPeriodEnum;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.EventCodeEnum;
import com.panda.merge.common.enums.OperateLogTypeEnum;
import com.panda.merge.common.enums.PDOperateLogEnum;
import com.panda.merge.common.enums.TeamTypeEnum;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.constant.PDEventCodeEnum;
import com.panda.merge.constant.SportTypeEnum;
import com.panda.merge.dto.BasketballScores;
import com.panda.merge.dto.BasketballScoresPDDto;
import com.panda.merge.dto.CommonItem;
import com.panda.merge.dto.FootballPenaltyScores;
import com.panda.merge.dto.FootballScores;
import com.panda.merge.dto.IceHockeyScores;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.TennisExtryScores;
import com.panda.merge.dto.TennisScores;
import com.panda.merge.dto.advertise.*;
import com.panda.merge.mapper.MatchScoresEventInfoMapper;
import com.panda.merge.mapper.MatchScoresInfoMapper;
import com.panda.merge.mapper.MatchScoresPdLogMapper;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.mapper.StandardSportMarketSellMapper;
import com.panda.merge.mapper.StandardSportTeamMapper;
import com.panda.merge.mapper.SystemItemDictMapper;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.model.MatchFreezeSettlePdLogEvent;
import com.panda.merge.model.MatchScoresEventInfo;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchScoresPdLog;
import com.panda.merge.model.MatchTimeInfo;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.model.StandardSportMarketSellExample;
import com.panda.merge.model.StandardSportTeam;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.model.ThirdMatchInfoExample;
import com.panda.merge.service.IMatchScorePdLogService;
import com.panda.merge.service.IScoresService;
import com.panda.merge.util.CategoryUtils;
import com.panda.merge.utils.JsonMapUtils;
import com.panda.merge.utils.ScoreUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;

import java.lang.reflect.Field;
import java.util.*;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

/**
 * PD赛事比分服务
 */
@Service
@Slf4j
public class MatchScorePdLogServiceImpl implements IMatchScorePdLogService {
    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    MatchScoresEventInfoMapper matchScoresEventInfoMapper;
    @Autowired
    MatchScoresInfoMapper matchScoresInfoMapper;
    @Autowired
    ThirdMatchInfoMapper thirdMatchInfoMapper;
    @Autowired
    RedisService redisService;
    @Autowired
    IScoresService scoresService;
    @Autowired
    private ScoreUtils scoreUtils;
    @Autowired
    BasketBallScoreService basketBallScoreService;
    @Autowired
    IceHockeyScoreService iceHockeyScoreService;
    @Autowired
    FootBallScoreService footBallScoreService;
    @Autowired
    FootBallEventService footBallEventService;
    @Autowired
    SystemItemDictMapper systemItemDictMapper;
    @Autowired
    MatchScoresPdLogMapper matchScoresPdLogMapper;
    @Autowired
    StandardSportTeamMapper standardSportTeamMapper;
    @Autowired
    StandardSportMarketSellMapper standardSportMarketSellMapper;
    @Autowired
    EventProducer eventProducer;

    @Value("${spring.profiles.active}")
    private String env;

    /**
     * 比赛开始操作日志
     * @param matchDto
     * @param matchTimeInfo
     */
    @Override
    public void matchBeginLog(TennisAdvertiseDto matchDto, MatchTimeInfo matchTimeInfo) {
        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        StandardMatchInfo standardMatchInfo = null;
        try {
            standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(matchDto.getStandardMatchId());
            if (standardMatchInfo == null) {
                return;
            }
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoMapper.selectByPrimaryKey(matchDto.getThirdMatchId());
            if (thirdMatchInfo == null) {
                return;
            }
            HashMap<String, Object> mapMatchLength = JSON.parseObject(matchTimeInfo.getMatchLengthJson(), HashMap.class);
            Object matchLength = mapMatchLength.get("1");

            matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_10048.getCode().toString());
            matchScoresPdLog.setOperateName(OperateLogTypeEnum.SCORES_PD_10049.getCode().toString());

            matchScoresPdLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10049.getCode().toString());
            matchScoresPdLog.setOperateParaName("-");
            matchScoresPdLog.setOperateForwText("-");
            matchScoresPdLog.setOperateRearText("-");
            if (thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(matchDto.getOperatorName() + " (" + thirdMatchInfo.getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(matchDto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(matchDto.getIpAddress());
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);

            String Rear = "-";
            if (matchLength != null) {
                Rear = String.valueOf(matchLength);
            }
            matchScoresPdLog.setOperateRearText(Rear);
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10049.getCode().toString());
            matchScoresPdLog.setRemark("1-1");
            matchScoresPdLogMapper.insert(matchScoresPdLog);

        } catch (Exception e) {

            log.error("matchBeginLog,标准赛事ID:{} , error:{}", standardMatchInfo.getId(), e);
        }

    }
    /**
     * 比赛结束操作日志
     * @param matchDto
     * @param matchTimeInfo
     */
    @Override
    public void matchEndLog(TennisAdvertiseDto matchDto, MatchTimeInfo matchTimeInfo) {
        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        StandardMatchInfo standardMatchInfo = null;

        try {
            standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(matchDto.getStandardMatchId());
            if (standardMatchInfo == null) {
                return;
            }
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoMapper.selectByPrimaryKey(matchDto.getThirdMatchId());
            if (thirdMatchInfo == null) {
                return;
            }
            matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_10048.getCode().toString());
            matchScoresPdLog.setOperateName(OperateLogTypeEnum.SCORES_PD_10052.getCode().toString());
            matchScoresPdLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10052.getCode().toString());

            String para = "-";
            if (matchTimeInfo.getPeriod().equals(61L)) {
                para = OperateLogTypeEnum.SCORES_PD_10054.getCode().toString();
            } else if (matchTimeInfo.getPeriod().equals(80L)) {
                para = OperateLogTypeEnum.SCORES_PD_10053.getCode().toString();
            } else if (matchTimeInfo.getPeriod().equals(90L)) {
                para = OperateLogTypeEnum.SCORES_PD_10055.getCode().toString();
            } else if (matchTimeInfo.getPeriod().equals(999L)) {
                para = OperateLogTypeEnum.SCORES_PD_10052.getCode().toString();
            }
            matchScoresPdLog.setOperateParaName(para);
            matchScoresPdLog.setOperateForwText("-");
            matchScoresPdLog.setOperateRearText("-");
            if (thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(matchDto.getOperatorName() + " (" + thirdMatchInfo.getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(matchDto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(matchDto.getIpAddress());
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);

        } catch (Exception e) {

            log.error("matchEndLog,标准赛事ID:{} , error:{}", standardMatchInfo.getId(), e);
        }

    }

    /**
     * 比赛恢复操作日志
     * @param matchDto
     */
    @Override
    public void matchStatusReSetLog(TennisAdvertiseDto matchDto) {
        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        StandardMatchInfo standardMatchInfo = null;
        try {
            standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(matchDto.getStandardMatchId());
            if (standardMatchInfo == null) {
                return;
            }
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoMapper.selectByPrimaryKey(matchDto.getThirdMatchId());
            if (thirdMatchInfo == null) {
                return;
            }
            matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_10048.getCode().toString());
            matchScoresPdLog.setOperateName("-");
            matchScoresPdLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10056.getCode().toString());

            matchScoresPdLog.setOperateParaName("-");
            matchScoresPdLog.setOperateForwText("-");
            matchScoresPdLog.setOperateRearText("-");
            if (thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(matchDto.getOperatorName() + " (" + thirdMatchInfo.getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(matchDto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(matchDto.getIpAddress());
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);

        } catch (Exception e) {

            log.error("matchStatusReSetLog,标准赛事ID:{} , error:{}", standardMatchInfo.getId(), e);
        }

    }

    /**
     * 比赛赛制设置
     * @param matchTimeInfoOid
     * @param matchTimeInfo
     * @param matchDto
     */
    @Override
    public void setRoundTypeLog(MatchTimeInfo matchTimeInfoOid, MatchTimeInfo matchTimeInfo, PDRoundTypeEditDto matchDto) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        StandardMatchInfo standardMatchInfo = null;

        try {

            standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(matchDto.getStandardMatchId());
            if (standardMatchInfo == null) {
                return;
            }
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoMapper.selectByPrimaryKey(matchDto.getThirdMatchId());
            if (thirdMatchInfo == null) {
                return;
            }
            matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_10048.getCode().toString());
            matchScoresPdLog.setOperateName("-");
            matchScoresPdLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10057.getCode().toString());

            matchScoresPdLog.setOperateParaName("-");
            matchScoresPdLog.setOperateForwText(matchTimeInfoOid.getRoundType().toString());
            matchScoresPdLog.setOperateRearText(matchTimeInfo.getRoundType().toString());
            if (thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(matchDto.getOperatorName() + " (" + thirdMatchInfo.getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(matchDto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(matchDto.getIpAddress());
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);

        } catch (Exception e) {

            log.error("setRoundTypeLog,标准赛事ID:{} , error:{}", standardMatchInfo.getId(), e);
        }
    }
    /**
     * 比赛局制设置
     * @param matchTimeInfoOid
     * @param matchTimeInfo
     * @param matchDto
     */
    @Override
    public void setMatchLengthLog(MatchTimeInfo matchTimeInfoOid, MatchTimeInfo matchTimeInfo, TennisAdvertiseDto matchDto) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        StandardMatchInfo standardMatchInfo = null;
        try {
            HashMap<String, Object> mapMatchLength = JSON.parseObject(matchTimeInfoOid.getMatchLengthJson(), HashMap.class);
            Object matchLength = mapMatchLength.get(matchDto.getCurrentSet().toString());
            standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(matchDto.getStandardMatchId());
            if (standardMatchInfo == null) {
                return;
            }
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoMapper.selectByPrimaryKey(matchDto.getThirdMatchId());
            if (thirdMatchInfo == null) {
                return;
            }
            matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_10048.getCode().toString());
            matchScoresPdLog.setOperateName(OperateLogTypeEnum.SCORES_PD_10058.getCode().toString());
            matchScoresPdLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10058.getCode().toString());
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateParaName("-");
            String str = "-";
            if (matchLength != null) {
                str = String.valueOf(matchLength);
            }

            matchScoresPdLog.setOperateForwText(str);
            matchScoresPdLog.setOperateRearText(matchTimeInfo.getMatchLength().toString());
            if (thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(matchDto.getOperatorName() + " (" + thirdMatchInfo.getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(matchDto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(matchDto.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);

        } catch (Exception e) {

            log.error("setMatchLengthLog,标准赛事ID:{} , error:{}", standardMatchInfo.getId(), e);
        }
    }

    /**
     * 局内比分设置
     * @param matchScoresInfoOid
     * @param matchScoresInfo
     * @param dto
     */
    @Override
    public void setMatchSecondScoreLog(MatchScoresInfo matchScoresInfoOid,
                                       MatchScoresInfo matchScoresInfo, TennisEditSecondScoreDto dto) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        StandardMatchInfo standardMatchInfo = null;
        try {
            TennisExtryScores tennisExtryScores = JSONObject.toJavaObject((JSONObject.parseObject(matchScoresInfo.getScoresJsonExtra())), TennisExtryScores.class);
            TennisExtryScores tennisExtryScoresOid = JSONObject.toJavaObject((JSONObject.parseObject(matchScoresInfoOid.getScoresJsonExtra())), TennisExtryScores.class);
            CommonItem commonItem = tennisExtryScores.getCurrentScoresMap().get(dto.getCurrentSet()).get(dto.getCurrentRound());
            CommonItem commonItemOid = tennisExtryScoresOid.getCurrentScoresMap().get(dto.getCurrentSet()).get(dto.getCurrentRound());

            standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(dto.getStandardMatchId());
            if (standardMatchInfo == null) {
                return;
            }
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoMapper.selectByPrimaryKey(dto.getThirdMatchId());
            if (thirdMatchInfo == null) {
                return;
            }
            matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_10048.getCode().toString());
            matchScoresPdLog.setOperateName(OperateLogTypeEnum.SCORES_PD_10051.getCode().toString());
            matchScoresPdLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10059.getCode().toString());
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_10051.getCode().toString());
            matchScoresPdLog.setOperateForwText(commonItemOid.getHome() + "-" + commonItemOid.getAway());
            matchScoresPdLog.setOperateRearText(commonItem.getHome() + "-" + commonItem.getAway());
            if (thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(dto.getOperatorName() + " (" + thirdMatchInfo.getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(dto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(dto.getIpAddress());
            matchScoresPdLog.setRemark(dto.getCurrentSet() + "-" + dto.getCurrentRound());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);

        } catch (Exception e) {

            log.error("setMatchSecondScoreLog,标准赛事ID:{} , error:{}", standardMatchInfo.getId(), e);
        }
    }
    /**
     * 更新局内比分
     * @param matchScoresInfoOid
     * @param matchScoresInfo
     * @param dto
     */
    @Override
    public void updateSecondScoreLog(MatchScoresInfo matchScoresInfoOid, MatchScoresInfo matchScoresInfo, TennisEditSecondScoreDto dto) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        StandardMatchInfo standardMatchInfo = null;
        try {
            TennisExtryScores tennisExtryScores = JSONObject.toJavaObject((JSONObject.parseObject(matchScoresInfo.getScoresJsonExtra())), TennisExtryScores.class);
            TennisExtryScores tennisExtryScoresOid = JSONObject.toJavaObject((JSONObject.parseObject(matchScoresInfoOid.getScoresJsonExtra())), TennisExtryScores.class);
            CommonItem commonItem = tennisExtryScores.getCurrentScoresMap().get(dto.getCurrentSet()).get(dto.getCurrentRound());
            CommonItem commonItemOid = tennisExtryScoresOid.getCurrentScoresMap().get(dto.getCurrentSet()).get(dto.getCurrentRound());

            standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(dto.getStandardMatchId());
            if (standardMatchInfo == null) {
                return;
            }
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoMapper.selectByPrimaryKey(dto.getThirdMatchId());
            if (thirdMatchInfo == null) {
                return;
            }
            matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_10048.getCode().toString());
            matchScoresPdLog.setOperateName(OperateLogTypeEnum.SCORES_PD_10051.getCode().toString());
            matchScoresPdLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10059.getCode().toString());
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_10051.getCode().toString());
            matchScoresPdLog.setOperateForwText(commonItemOid.getHome() + "-" + commonItemOid.getAway());
            matchScoresPdLog.setOperateRearText(commonItem.getHome() + "-" + commonItem.getAway());
            if (thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(dto.getOperatorName() + " (" + thirdMatchInfo.getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(dto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(dto.getIpAddress());
            matchScoresPdLog.setRemark(dto.getCurrentSet() + "-" + dto.getCurrentRound());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);

        } catch (Exception e) {

            log.error("updateSecondScoreLog,标准赛事ID:{} , error:{}", standardMatchInfo.getId(), e);
        }
    }

    /**
     * 盘开始  盘结束
     * @param matchTimeInfoOid
     * @param matchTimeInfo
     * @param dto
     */
    @Override
    public void changeSetStatusLog(MatchTimeInfo matchTimeInfoOid, MatchTimeInfo matchTimeInfo, PDTennisSetStatusDto dto) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        StandardMatchInfo standardMatchInfo = null;
        try {
            standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(dto.getStandardMatchId());
            if (standardMatchInfo == null) {
                return;
            }
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoMapper.selectByPrimaryKey(dto.getThirdMatchId());
            if (thirdMatchInfo == null) {
                return;
            }
            matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_10048.getCode().toString());
            matchScoresPdLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());
            if (dto.getSetStatus().equals(0)) {
                matchScoresPdLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_100123.getCode().toString());
            } else {
                matchScoresPdLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_100124.getCode().toString());
            }

            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateName(matchScoresPdLog.getOperateParaName());
            matchScoresPdLog.setOperateType(matchScoresPdLog.getOperateParaName());
            matchScoresPdLog.setOperateForwText("-");
            matchScoresPdLog.setOperateRearText("-");
            if (thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(dto.getOperatorName() + " (" + thirdMatchInfo.getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(dto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(dto.getIpAddress());
            matchScoresPdLog.setRemark(dto.getCurrentSet().toString());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);

        } catch (Exception e) {
            log.error("changeSetStatusLog,标准赛事ID:{} , error:{}", standardMatchInfo.getId(), e);
        }
    }

    /**
     * 局开始  局结束
     * @param matchTimeInfoOid
     * @param matchTimeInfo
     * @param dto
     */
    @Override
    public void changeRoundStatusLog(MatchTimeInfo matchTimeInfoOid, MatchTimeInfo matchTimeInfo, PDTennisRoundStatusDto dto) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        StandardMatchInfo standardMatchInfo = null;
        try {

            standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(dto.getStandardMatchId());
            if (standardMatchInfo == null) {
                return;
            }
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoMapper.selectByPrimaryKey(dto.getThirdMatchId());
            if (thirdMatchInfo == null) {
                return;
            }
            matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_10048.getCode().toString());
            matchScoresPdLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());
            if (dto.getRoundStatus() == 0) {
                matchScoresPdLog.setOperateName(OperateLogTypeEnum.SCORES_PD_10060.getCode().toString());
                matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10060.getCode().toString());
            } else if (dto.getRoundStatus() == 1) {
                matchScoresPdLog.setOperateName(OperateLogTypeEnum.SCORES_PD_10061.getCode().toString());
                matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10061.getCode().toString());
            }
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_10051.getCode().toString());
            matchScoresPdLog.setOperateForwText("-");
            matchScoresPdLog.setOperateRearText("-");
            if (thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(dto.getOperatorName() + " (" + thirdMatchInfo.getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(dto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(dto.getIpAddress());
            matchScoresPdLog.setRemark(dto.getCurrentSet() + "-" + dto.getCurrentRound());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);

        } catch (Exception e) {

            log.error("changeRoundStatusLog,标准赛事ID:{} , error:{}", standardMatchInfo.getId(), e);
        }
    }

    /**
     * 设置最大局数
     * @param matchTimeInfoOid
     * @param matchTimeInfo
     * @param dto
     * @param id
     */
    @Override
    public void setMaxRoundLog(MatchTimeInfo matchTimeInfoOid, MatchTimeInfo matchTimeInfo, MatchTennisEditMaxRoundDto dto, Long id) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        StandardMatchInfo standardMatchInfo = null;
        try {
            JSONObject jsonObject = JSONObject.parseObject(matchTimeInfoOid.getPeriodLengthJson());
            Object obj = jsonObject.get(dto.getCurrentSet().toString());

            JSONObject jsonObject2 = JSONObject.parseObject(matchTimeInfo.getPeriodLengthJson());
            Object obj2 = jsonObject2.get(dto.getCurrentSet().toString());
            standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(id);
            if (standardMatchInfo == null) {
                return;
            }
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoMapper.selectByPrimaryKey(dto.getThirdMatchId());
            if (thirdMatchInfo == null) {
                return;
            }
            matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_10048.getCode().toString());
            matchScoresPdLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());
            matchScoresPdLog.setOperateName(OperateLogTypeEnum.SCORES_PD_10062.getCode().toString());
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10062.getCode().toString());
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateParaName("-");
            matchScoresPdLog.setOperateForwText(obj.toString());
            matchScoresPdLog.setOperateRearText(obj2.toString());
            if (thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(dto.getOperatorName() + " (" + thirdMatchInfo.getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(dto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(dto.getIpAddress());

            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);

        } catch (Exception e) {

            log.error("setMaxRoundLog,标准赛事ID:{} , error:{}", standardMatchInfo.getId(), e);
        }
    }

    /**
     * 直接编辑盘比分
     * @param matchScoresInfoOid
     * @param matchScoresInfo
     * @param dto
     * @param id
     */
    @Override
    public void setSetScoreLog(MatchScoresInfo matchScoresInfoOid, MatchScoresInfo matchScoresInfo,
                               MatchTennisEditSetScoreDto dto, Long id) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        StandardMatchInfo standardMatchInfo = null;
        try {

            standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(id);
            if (standardMatchInfo == null) {
                return;
            }
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoMapper.selectByPrimaryKey(dto.getThirdMatchId());
            if (thirdMatchInfo == null) {
                return;
            }
            matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_10048.getName());
            matchScoresPdLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());
            matchScoresPdLog.setOperateName(OperateLogTypeEnum.SCORES_PD_10062.getCode().toString());
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10062.getCode().toString());
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateParaName("-");
            matchScoresPdLog.setOperateForwText(matchScoresInfoOid.getT1() + "-" + matchScoresInfoOid.getT2());
            matchScoresPdLog.setOperateRearText(matchScoresInfo.getT1() + CategoryUtils.SPLIT_LINE + matchScoresInfo.getT2());
            if (thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(dto.getOperatorName() + " (" + thirdMatchInfo.getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(dto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(dto.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);

        } catch (Exception e) {

            log.error("setSetScoreLog,标准赛事ID:{} , error:{}", standardMatchInfo.getId(), e);
        }
    }


    static String getTheScoreString(MatchScoresInfo matchScoresInfo) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(matchScoresInfo.getT1()).append("-").append(matchScoresInfo.getT2());
        stringBuffer.append("\n");
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, TennisScores> allPeriodScores = JsonMapUtils.parseTennisMap(periodFootballScores);

        for (Map.Entry<Long, TennisScores> entry : allPeriodScores.entrySet()) {
            if (entry.getKey().equals(-1L)) {
                continue;
            }
            Integer setNumber = MatchPeriodUtils.getTennisSetByPeriod(entry.getKey());
            if (null == setNumber) {
                continue;
            }
            CommonItem commonItemVo = new CommonItem();
            BeanUtils.copyProperties(entry.getValue().getSetScore(), commonItemVo);
            //拼接局比分 (1-2)格式
            stringBuffer.append("(").append(commonItemVo.getHome()).append("-").append(commonItemVo.getAway()).append(")");
        }

        return stringBuffer.toString();
    }

    /**
     * 重新计算盘比分
     * @param matchScoresInfoOid
     * @param matchScoresInfo
     * @param dto
     * @param id
     */
    @Override
    public void reCountSetScoreLog(MatchScoresInfo matchScoresInfoOid, MatchScoresInfo matchScoresInfo,
                                   MatchTennisReSetScoreDto dto, Long id) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        StandardMatchInfo standardMatchInfo = null;
        try {
            String theScoreStringOid = getTheScoreString(matchScoresInfoOid);
            String theScoreStringNew = getTheScoreString(matchScoresInfo);


            standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(id);
            if (standardMatchInfo == null) {
                return;
            }
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoMapper.selectByPrimaryKey(dto.getThirdMatchId());
            if (thirdMatchInfo == null) {
                return;
            }
            matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_10048.getCode().toString());
            matchScoresPdLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());
            matchScoresPdLog.setOperateName(OperateLogTypeEnum.SCORES_PD_10063.getCode().toString());
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10063.getCode().toString());
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateParaName("-");
            matchScoresPdLog.setOperateForwText(theScoreStringOid);

            if (matchScoresInfoOid.getT1() == null || matchScoresInfoOid.getT2() == null) {
                matchScoresPdLog.setOperateForwText("-");
            }
            matchScoresPdLog.setOperateRearText(theScoreStringNew);
            if (thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(dto.getOperatorName() + " (" + thirdMatchInfo.getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(dto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(dto.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);

        } catch (Exception e) {

            log.error("reCountSetScore,标准赛事ID:{} , error:{}", standardMatchInfo.getId(), e);
        }
    }

    /**
     * 修改开赛时间操作日志
     * @param changeMatchStartTimeDto 修改后传参数
     * @param oldBeginTime 修改前传参数 1
     */
    @Override
    public void changeMatchStartTimeLog(ChangeMatchStartTimeDto changeMatchStartTimeDto, Long oldBeginTime, StandardMatchInfo standardMatchInfo) {

        if (standardMatchInfo == null) {
            return;
        }
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoMapper.selectByPrimaryKey(changeMatchStartTimeDto.getThirdMatchId());
        if (thirdMatchInfo == null) {
            return;
        }
        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            switch (standardMatchInfo.getSportId().intValue()) {
                case 1:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
                    break;
                case 2:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100101.getCode().toString());
                    break;
                case 4:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100102.getCode().toString());
                    break;
            }
            matchScoresPdLog.setOperateId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateName(getMatchSportTeamNameCode(standardMatchInfo.getId()));
            matchScoresPdLog.setOperateMatchId(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateMatchName(CategoryUtils.SPLIT_LINE);
            if (DateUtils.isSameDay(new Date(changeMatchStartTimeDto.getStartTime()), new Date(oldBeginTime))) {
                matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100103.getCode().toString());
                matchScoresPdLog.setOperateForwText(DateFormatUtils.format(oldBeginTime, "HH:mm"));
                matchScoresPdLog.setOperateRearText(DateFormatUtils.format(changeMatchStartTimeDto.getStartTime(), "HH:mm"));
            } else {
                matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100104.getCode().toString());
                matchScoresPdLog.setOperateForwText(DateFormatUtils.format(oldBeginTime, "yyyy-MM-dd"));
                matchScoresPdLog.setOperateRearText(DateFormatUtils.format(changeMatchStartTimeDto.getStartTime(), "yyyy-MM-dd"));
            }
            matchScoresPdLog.setOperateParaName(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            if (thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(changeMatchStartTimeDto.getOperatorName() + " (" + thirdMatchInfo.getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(changeMatchStartTimeDto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(changeMatchStartTimeDto.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);
        } catch (Exception e) {

            log.error("changeMatchStartTimeLog,标准赛事ID:{} , error:{}", standardMatchInfo.getId(), e);
        }
    }
    /**
     * 开球
     * @param kickOff
     * @param matchScoreAndTimeVo
     */
    @Override
    public void kickOffLog(KickOffDto kickOff, MatchScoreAndTimeVo matchScoreAndTimeVo) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            StandardMatchInfo standardMatchInfo = matchScoreAndTimeVo.getStandardMatchInfo();
            switch (matchScoreAndTimeVo.getThirdMatchInfo().getSportId().intValue()) {
                case 1:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
                    break;
                case 2:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100101.getCode().toString());
                    break;
                case 4:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100102.getCode().toString());
                    break;
            }
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateName(getMatchSportTeamNameCode(matchScoreAndTimeVo.getThirdMatchInfo().getReferenceId()));
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100106.getCode().toString());
            matchScoresPdLog.setOperateMatchId(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateMatchName(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateParaName(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
            switch (kickOff.getWhoKickOff()) {
                case "home":
                    matchScoresPdLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_100107.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100107.getValue());
                    break;
                case "away":
                    matchScoresPdLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_100111.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100111.getValue());
                    break;
                default:
                    matchScoresPdLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_100106.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100106.getValue());
                    break;
            }
            if (matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(kickOff.getOperatorName() + " (" + matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(kickOff.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(kickOff.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);//操作时间
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);

            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100112.getCode().toString());
            matchScoresPdLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_100126.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100126.getValue());//开始
            matchScoresPdLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_10066.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10066.getValue());
            matchScoresPdLogMapper.insert(matchScoresPdLog);

        } catch (Exception e) {

            log.error("kickOffLog,标准赛事ID:{} , error:{}", matchScoreAndTimeVo.getStandardMatchInfo().getId(), e);
        }

    }

    @Override
    public void kickOffAfterGoalLog(KickOffDto kickOff, MatchScoreAndTimeVo matchScoreAndTimeVo) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            StandardMatchInfo standardMatchInfo = matchScoreAndTimeVo.getStandardMatchInfo();
            switch (matchScoreAndTimeVo.getThirdMatchInfo().getSportId().intValue()) {
                case 1:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
                    break;
                case 2:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100101.getCode().toString());
                    break;
                case 4:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100102.getCode().toString());
                    break;
                default:
            }
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateName(getMatchSportTeamNameCode(matchScoreAndTimeVo.getThirdMatchInfo().getReferenceId()));
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100106.getCode().toString());
            matchScoresPdLog.setOperateMatchId(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateMatchName(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateParaName(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
            switch (kickOff.getWhoKickOff()) {
                case "home":
                    matchScoresPdLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_100107.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100107.getValue());
                    break;
                case "away":
                    matchScoresPdLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_100111.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100111.getValue());
                    break;
                default:
                    matchScoresPdLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_100106.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100106.getValue());
                    break;
            }
            if (matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())) {
                matchScoresPdLog.setOperateUserName(kickOff.getOperatorName() + " (" + matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(kickOff.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(kickOff.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);
        } catch (Exception e) {
            log.error("kickOffAfterGoalLog,标准赛事ID:{} , error:{}", matchScoreAndTimeVo.getStandardMatchInfo().getId(), e);
        }
    }
    /**
     * 赛事 危险安全设置日志
     * @param isDangerDto
     * @param matchScoreAndTimeVo
     */
    @Override
    public void isDangerLog(IsDangerDto isDangerDto, MatchScoreAndTimeVo matchScoreAndTimeVo) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            StandardMatchInfo standardMatchInfo = matchScoreAndTimeVo.getStandardMatchInfo();
            switch (standardMatchInfo.getSportId().intValue()) {
                case 1:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
                    break;
                case 2:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100101.getCode().toString());
                    break;
                case 4:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100102.getCode().toString());
                    break;
            }
            matchScoresPdLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateMatchName(getMatchSportTeamNameCode(matchScoreAndTimeVo.getThirdMatchInfo().getReferenceId()));
            matchScoresPdLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateRearText(CategoryUtils.SPLIT_LINE);
            switch (isDangerDto.getIsDanger()) {
                case 0:
                    matchScoresPdLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_100117.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100117.getValue());
                    break;
                case 1:
                    matchScoresPdLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_100118.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100118.getValue());
                    break;
            }
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateId(standardMatchInfo.getMatchPeriodId().toString());
            matchScoresPdLog.setOperateName(getPeriodNameByPeriodId(standardMatchInfo.getSportId(), standardMatchInfo.getMatchPeriodId()));
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10065.getCode().toString());
            if (matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(isDangerDto.getOperatorName() + " (" + matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(isDangerDto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(isDangerDto.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);//操作时间
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);
        } catch (Exception e) {

            log.error("isDangerLog,标准赛事ID:{} , error:{}", matchScoreAndTimeVo.getStandardMatchInfo().getId(), e);
        }

    }

    /***
     * 增加 periodId 字段
     * @param matchScoreAndTimeVo
     * @param changeMatchStatus
     */
    @Override
    public void changeMatchStatusLog(MatchScoreAndTimeVo matchScoreAndTimeVo, ChangeMatchStatusDto changeMatchStatus) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            StandardMatchInfo standardMatchInfo = matchScoreAndTimeVo.getStandardMatchInfo();
            switch (matchScoreAndTimeVo.getThirdMatchInfo().getSportId().intValue()) {
                case 1:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
                    break;
                case 2:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100101.getCode().toString());
                    break;
                case 4:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100102.getCode().toString());
                    break;
            }
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateName(getMatchSportTeamNameCode(standardMatchInfo.getId()));
            switch (changeMatchStatus.getControlType()) {
                case 1:
                    matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100112.getCode().toString());
                    matchScoresPdLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_100126.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100126.getValue());//开始
                    break;
                case 2:
                    matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100113.getCode().toString());
                    matchScoresPdLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_100127.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100127.getValue());//暂停
                    break;
                case 3:
                    matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100114.getCode().toString());
                    matchScoresPdLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_100128.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100128.getValue());//继续
                    break;
                case 4:
                    matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100115.getCode().toString());
                    matchScoresPdLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_10052.getName() + CategoryUtils.SPLIT_AND + "End");//结束
                    break;
            }
            String paramName;
            if (null != changeMatchStatus.getIsJump() && changeMatchStatus.getIsJump() == 0) {
                Long period = matchScoreAndTimeVo.getMatchTimeInfo().getPeriod();
                paramName = getPeriodNameByPeriodId(standardMatchInfo.getSportId(), period);
            } else {
                paramName = getPeriodNameByPeriodId(standardMatchInfo.getSportId(), changeMatchStatus.getPeriodId());
            }
            matchScoresPdLog.setOperateParaName(paramName);
            if (matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(changeMatchStatus.getOperatorName() + " (" + matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(changeMatchStatus.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(changeMatchStatus.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);//操作时间
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);
        } catch (Exception e) {

            log.error("changeMatchStatusLog,标准赛事ID:{} , error:{}", matchScoreAndTimeVo.getStandardMatchInfo().getId(), e);
        }

    }

    /**
     *  点击结束赛事事件
     * @param matchScoreAndTimeVo
     * @param changeMatchStatus
     */
    @Override
    public void setMatchEndLog(MatchScoreAndTimeVo matchScoreAndTimeVo, ChangeMatchStatusDto changeMatchStatus) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            StandardMatchInfo standardMatchInfo = matchScoreAndTimeVo.getStandardMatchInfo();
            switch (standardMatchInfo.getSportId().intValue()) {
                case 1:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
                    break;
                case 2:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100101.getCode().toString());
                    break;
                case 4:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100102.getCode().toString());
                    break;
            }
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateName(getMatchSportTeamNameCode(standardMatchInfo.getId()));
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10052.getCode().toString());
            matchScoresPdLog.setOperateMatchId(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateMatchName(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_10052.getName() + CategoryUtils.SPLIT_AND + "End");
            matchScoresPdLog.setOperateParaName(CategoryUtils.SPLIT_LINE);
            if (matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(changeMatchStatus.getOperatorName() + " (" + matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(changeMatchStatus.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(changeMatchStatus.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);
        } catch (Exception e) {

            log.error("setMatchEndLog,标准赛事ID:{} , error:{}", matchScoreAndTimeVo.getStandardMatchInfo().getId(), e);
        }

    }
    /**
     * 记录删除赛事事件，区分事件类型：进球、黄牌、角球等
     * @param matchScoreAndTimeVo
     * @param deleteEventDto
     */
    @Override
    public void deleteEventLog(MatchScoreAndTimeVo matchScoreAndTimeVo, DeleteEventDto deleteEventDto,CommonItem commonOldItem,CommonItem commonNewItem,MatchScoresEventInfo oldEvent) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            StandardMatchInfo standardMatchInfo = matchScoreAndTimeVo.getStandardMatchInfo();
            switch (standardMatchInfo.getSportId().intValue()) {
                case 1:
                    eventProducer.deleteEventPDManGoEarlyWarning(matchScoreAndTimeVo,commonOldItem,oldEvent,env);
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
                    break;
                case 2:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100101.getCode().toString());
                    break;
                case 4:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100102.getCode().toString());
                    break;
            }
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            String periodName = getPeriodNameByPeriodId(standardMatchInfo.getSportId(), standardMatchInfo.getMatchPeriodId());
            matchScoresPdLog.setOperateName(periodName);
            matchScoresPdLog.setOperateId(standardMatchInfo.getMatchPeriodId().toString());
            matchScoresPdLog.setOperateMatchName(getMatchSportTeamNameCode(standardMatchInfo.getId()));
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10064.getCode().toString());

            String homeAwayName = null;
            if(oldEvent.getHomeAway().equals(TeamTypeEnum.HOME.code)){
                homeAwayName = CategoryUtils.HOME_PARAM;
            } else {
                homeAwayName = CategoryUtils.AWAY_PARAM;
            }
            switch (oldEvent.getEventCode()) {
                case "goal":
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_10084.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10084.getValue());
                    break;
                case "corner":
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_10085.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10085.getValue());
                    break;
                case "yellow_card":
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_10086.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10086.getValue());
                    break;
                case "red_card":
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_10087.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10087.getValue());
                    break;
                case "Penalty":
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_10069.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10069.getValue());
                    break;
                case "throw_in":
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_100075.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_100075.getValue());
                    break;
                case "attack":
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_100076.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_100076.getValue());
                    break;
                case "yellow_red_card":
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_100085.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_100085.getValue());
                    break;
                case "goal_kick":
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_100077.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_100077.getValue());
                    break;
                case "free_kick":
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_100078.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_100078.getValue());
                    break;
                case "offside":
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_100082.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_100082.getValue());
                    break;
                case "shot_on_target":
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_100083.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_100083.getValue());
                    break;
                case "shot_off_target":
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_100084.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_100084.getValue());
                    break;
                case "score_delete":
                    int score = Integer.parseInt(oldEvent.getExtraInfo());
                    String addition5 = oldEvent.getAddition5();
                    if(!StringUtils.isEmpty(addition5)){
                        matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                                + OperateLogTypeEnum.SCORES_PD_20330101.getName() + CategoryUtils.SPLIT_AND
                                + OperateLogTypeEnum.SCORES_PD_20330101.getValue());
                    }
                    if (1 == score && StringUtils.isEmpty(addition5)) {
                        matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                                + OperateLogTypeEnum.SCORES_PD_203301.getName() + CategoryUtils.SPLIT_AND
                                + OperateLogTypeEnum.SCORES_PD_203301.getValue());
                    }
                    if (2 == score && StringUtils.isEmpty(addition5)) {
                        matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                                + OperateLogTypeEnum.SCORES_PD_203302.getName() + CategoryUtils.SPLIT_AND
                                + OperateLogTypeEnum.SCORES_PD_203302.getValue());
                    }
                    if (3 == score && StringUtils.isEmpty(addition5)) {
                        matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                                + OperateLogTypeEnum.SCORES_PD_203303.getName() + CategoryUtils.SPLIT_AND
                                + OperateLogTypeEnum.SCORES_PD_203303.getValue());
                    }
                    break;
            }
            matchScoresPdLog.setOperateForwText(commonOldItem.getHome() + CategoryUtils.SPLIT_LINE + commonOldItem.getAway());
            matchScoresPdLog.setOperateRearText(commonNewItem.getHome() + CategoryUtils.SPLIT_LINE + commonNewItem.getAway());
            if (matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(deleteEventDto.getOperatorName() + " (" + matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(deleteEventDto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(deleteEventDto.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);

        } catch (Exception e) {

            log.error("deleteEventLog,三方赛事ID:{} , error:{}", matchScoreAndTimeVo.getMatchTimeInfo().getThirdMatchId(), e);
        }

    }

    @Override
    public void retakePenLog(MatchScoreAndTimeVo matchScoreAndTimeVo, MatchScoresEventInfo oldEvent, RetakePenDto retakePenDto) {
        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            StandardMatchInfo standardMatchInfo = matchScoreAndTimeVo.getStandardMatchInfo();
            switch (standardMatchInfo.getSportId().intValue()) {
                case 1:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
                    break;
                case 2:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100101.getCode().toString());
                    break;
                case 4:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100102.getCode().toString());
                    break;
                default:
            }
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            String periodName = getPeriodNameByPeriodId(standardMatchInfo.getSportId(), standardMatchInfo.getMatchPeriodId());
            matchScoresPdLog.setOperateName(periodName);
            matchScoresPdLog.setOperateId(standardMatchInfo.getMatchPeriodId().toString());
            matchScoresPdLog.setOperateMatchName(getMatchSportTeamNameCode(standardMatchInfo.getId()));
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100641.getCode().toString());

            String homeAway;
            if (oldEvent.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                homeAway = CategoryUtils.HOME_PARAM;
            } else {
                homeAway = CategoryUtils.AWAY_PARAM;
            }
            switch (oldEvent.getEventCode()) {
                case "penalty_goal":
                    matchScoresPdLog.setOperateParaName(homeAway + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_10095.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10095.getValue());
                    matchScoresPdLog.setOperateForwText(OperateLogTypeEnum.SCORES_PD_10096.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10096.getValue());
                    matchScoresPdLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_10095.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10095.getValue());
                    break;
                case "penalty_missed":
                    matchScoresPdLog.setOperateParaName(homeAway + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_100132.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100132.getValue());
                    matchScoresPdLog.setOperateForwText(OperateLogTypeEnum.SCORES_PD_100132.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100132.getValue());
                    matchScoresPdLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_10095.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10095.getValue());
                    break;
                default:
            }
            if (matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())) {
                matchScoresPdLog.setOperateUserName(retakePenDto.getOperatorName() + " (" + matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(retakePenDto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(retakePenDto.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);

        } catch (Exception e) {
            log.error("retakePenLog,三方赛事ID:{} , error:{}", matchScoreAndTimeVo.getMatchTimeInfo().getThirdMatchId(), e);
        }
    }

    @Override
    public void noRetakePenLog(MatchScoreAndTimeVo matchScoreAndTimeVo, MatchScoresEventInfo oldEvent, NoRetakePenDto noRetakePenDto) {
        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            StandardMatchInfo standardMatchInfo = matchScoreAndTimeVo.getStandardMatchInfo();
            switch (standardMatchInfo.getSportId().intValue()) {
                case 1:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
                    break;
                case 2:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100101.getCode().toString());
                    break;
                case 4:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100102.getCode().toString());
                    break;
                default:
            }
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            String periodName = getPeriodNameByPeriodId(standardMatchInfo.getSportId(), standardMatchInfo.getMatchPeriodId());
            matchScoresPdLog.setOperateName(periodName);
            matchScoresPdLog.setOperateId(standardMatchInfo.getMatchPeriodId().toString());
            matchScoresPdLog.setOperateMatchName(getMatchSportTeamNameCode(standardMatchInfo.getId()));
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100951.getCode().toString());

            String homeAway;
            if (oldEvent.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                homeAway = CategoryUtils.HOME_PARAM;
            } else {
                homeAway = CategoryUtils.AWAY_PARAM;
            }
            switch (oldEvent.getEventCode()) {
                case "penalty_goal":
                    matchScoresPdLog.setOperateParaName(homeAway + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_10095.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10095.getValue());
                    matchScoresPdLog.setOperateForwText(OperateLogTypeEnum.SCORES_PD_10096.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10096.getValue());
                    matchScoresPdLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_100951.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100951.getValue());
                    break;
                case "penalty_missed":
                    matchScoresPdLog.setOperateParaName(homeAway + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_100132.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100132.getValue());
                    matchScoresPdLog.setOperateForwText(OperateLogTypeEnum.SCORES_PD_100132.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100132.getValue());
                    matchScoresPdLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_100951.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100951.getValue());
                    break;
                default:
            }
            if (matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())) {
                matchScoresPdLog.setOperateUserName(noRetakePenDto.getOperatorName() + " (" + matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(noRetakePenDto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(noRetakePenDto.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);

        } catch (Exception e) {
            log.error("retakePenLog,三方赛事ID:{} , error:{}", matchScoreAndTimeVo.getMatchTimeInfo().getThirdMatchId(), e);
        }
    }

    @Override
    public void editEventLog(MatchScoreAndTimeVo matchScoreAndTimeVo, PDBasketBallEditEventDto editEventDto, CommonItem commonOldItem, CommonItem commonNewItem, MatchScoresEventInfo oldEvent) {
        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            StandardMatchInfo standardMatchInfo = matchScoreAndTimeVo.getStandardMatchInfo();
            if (standardMatchInfo.getSportId().intValue() == 2) {
                matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100101.getCode().toString());
            }
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            String periodName = getPeriodNameByPeriodId(standardMatchInfo.getSportId(), standardMatchInfo.getMatchPeriodId());
            matchScoresPdLog.setOperateName(periodName);
            matchScoresPdLog.setOperateId(standardMatchInfo.getMatchPeriodId().toString());
            matchScoresPdLog.setOperateMatchName(getMatchSportTeamNameCode(standardMatchInfo.getId()));
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100073.getCode().toString());
            String homeAwayName = null;
            if (oldEvent.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                homeAwayName = CategoryUtils.HOME_PARAM;
            } else {
                homeAwayName = CategoryUtils.AWAY_PARAM;
            }
            if ("score_change".equals(oldEvent.getEventCode())) {
                int score = Integer.parseInt(oldEvent.getExtraInfo());
                String addition5 = oldEvent.getAddition5();
                if (!StringUtils.isEmpty(addition5)) {
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2033012.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2033012.getValue());
                }
                if (1 == score && StringUtils.isEmpty(addition5)) {
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2033011.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2033011.getValue());
                }
                if (2 == score && StringUtils.isEmpty(addition5)) {
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2033021.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2033021.getValue());
                }
                if (3 == score && StringUtils.isEmpty(addition5)) {
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2033031.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2033031.getValue());
                }
            }
            matchScoresPdLog.setOperateForwText(commonOldItem.getHome() + CategoryUtils.SPLIT_LINE + commonOldItem.getAway());
            matchScoresPdLog.setOperateRearText(commonNewItem.getHome() + CategoryUtils.SPLIT_LINE + commonNewItem.getAway());
            if (matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())) {
                matchScoresPdLog.setOperateUserName(editEventDto.getOperatorName() + " (" + matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(editEventDto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(editEventDto.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);
        } catch (Exception e) {
            log.error("editEventLog,三方赛事ID:{} , error:{}", matchScoreAndTimeVo.getMatchTimeInfo().getThirdMatchId(), e);
        }
    }

    /**
     * 可能比分事件
     * @param possibleEventDto
     */
    @Override
    public void possibleEventLog(PossibleEventDto possibleEventDto) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String homeAwayName = null;
        String homeAway = possibleEventDto.getHomeAway();
        if (homeAway.equals(TeamTypeEnum.HOME.code)) {
            homeAwayName = CategoryUtils.HOME_PARAM;
        } else {
            homeAwayName = CategoryUtils.AWAY_PARAM;
        }
        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoMapper.selectByPrimaryKey(possibleEventDto.getThirdMatchId());
            switch (thirdMatchInfo.getSportId().intValue()) {
                case 1:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
                    break;
                case 2:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100101.getCode().toString());
                    break;
                case 4:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100102.getCode().toString());
                    break;
            }
            MatchScoresInfo matchScoresInfo = scoresService.selectLiveMatchScoreInfo(possibleEventDto.getThirdMatchId());
            if (matchScoresInfo != null) {
                FootBallScoreVo footBallScoreVo = footBallScoreService.transforScore(matchScoresInfo);
                //可能进球
                if (possibleEventDto.getPossibleEventCode().equals("possible_goal")) {
                    CommonItem goalItem = footBallScoreVo.getGoal();
                    if (homeAway.equals(TeamTypeEnum.HOME.code)) {
                        goalItem.setHome(goalItem.getHome() + 1);
                    } else {
                        goalItem.setAway(goalItem.getAway() + 1);
                    }
                    matchScoresPdLog.setOperateRearText(goalItem.getHome() + CategoryUtils.SPLIT_LINE + goalItem.getAway());
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_10079.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10079.getValue());
                }
                //可能点球
                if (possibleEventDto.getPossibleEventCode().equals("possible_penalty")) {
                    CommonItem goalItem = footBallScoreVo.getPeriodGoal();
                    if (homeAway.equals(TeamTypeEnum.HOME.code)) {
                        goalItem.setHome(goalItem.getHome() + 1);
                    } else {
                        goalItem.setAway(goalItem.getAway() + 1);
                    }
                    matchScoresPdLog.setOperateRearText(goalItem.getHome() + CategoryUtils.SPLIT_LINE + goalItem.getAway());
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_100125.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100125.getValue());
                }
                //可能黄牌
                if (possibleEventDto.getPossibleEventCode().equals("possible_yellow_card")) {
                    CommonItem yellowCardItem = footBallScoreVo.getYellowCard();
                    if (homeAway.equals(TeamTypeEnum.HOME.code)) {
                        yellowCardItem.setHome(yellowCardItem.getHome() + 1);
                    } else {
                        yellowCardItem.setAway(yellowCardItem.getAway() + 1);
                    }
                    matchScoresPdLog.setOperateRearText(yellowCardItem.getHome() + CategoryUtils.SPLIT_LINE + yellowCardItem.getAway());
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_10070.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10070.getValue());
                }
                //可能红牌
                if (possibleEventDto.getPossibleEventCode().equals("possible_red_card")) {
                    CommonItem redCardItem = footBallScoreVo.getRedCard();
                    if (homeAway.equals(TeamTypeEnum.HOME.code)) {
                        redCardItem.setHome(redCardItem.getHome() + 1);
                    } else {
                        redCardItem.setAway(redCardItem.getAway() + 1);
                    }
                    matchScoresPdLog.setOperateRearText(redCardItem.getHome() + CategoryUtils.SPLIT_LINE + redCardItem.getAway());
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_10073.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10073.getValue());
                }
                //可能角球
                if (possibleEventDto.getPossibleEventCode().equals("possible_corner")) {
                    CommonItem cornerItem = footBallScoreVo.getPeriodCorner();
                    if (homeAway.equals(TeamTypeEnum.HOME.code)) {
                        cornerItem.setHome(cornerItem.getHome() + 1);
                    } else {
                        cornerItem.setAway(cornerItem.getAway() + 1);
                    }
                    matchScoresPdLog.setOperateRearText(cornerItem.getHome() + CategoryUtils.SPLIT_LINE + cornerItem.getAway());
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_10076.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10076.getValue());
                }
                // 可能任意球
                if ("possible_free_kick".equals(possibleEventDto.getPossibleEventCode())) {
                    CommonItem freeKick = footBallScoreVo.getFreeKick();
                    if (homeAway.equals(TeamTypeEnum.HOME.code)) {
                        freeKick.setHome(freeKick.getHome() + 1);
                    } else {
                        freeKick.setAway(freeKick.getAway() + 1);
                    }
                    matchScoresPdLog.setOperateRearText(freeKick.getHome()
                            + CategoryUtils.SPLIT_LINE + freeKick.getAway());
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_100079.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_100079.getValue());
                }

                // 可能var
                if(PDEventCodeEnum.containVAREvent(possibleEventDto.getPossibleEventCode(), CommonConstant.PD_EVENT_TYPE_POSSIBLE)){
                    matchScoresPdLog.setOperateRearText(CategoryUtils.SPLIT_LINE);
                    OperateLogTypeEnum logTypeEnum = OperateLogTypeEnum.getEnumByValue(possibleEventDto.getPossibleEventCode());
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + logTypeEnum.getName() + CategoryUtils.SPLIT_AND + logTypeEnum.getValue());
                }

                StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(thirdMatchInfo.getReferenceId());
                if (standardMatchInfo == null) {
                    return;
                }
                matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
                matchScoresPdLog.setOperateId(standardMatchInfo.getMatchPeriodId().toString());
                matchScoresPdLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
                matchScoresPdLog.setOperateMatchName(getMatchSportTeamNameCode(thirdMatchInfo.getReferenceId()));
                matchScoresPdLog.setOperateName(getPeriodNameByPeriodId(standardMatchInfo.getSportId(), standardMatchInfo.getMatchPeriodId()));
                matchScoresPdLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10065.getCode().toString());
                if (thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                    matchScoresPdLog.setOperateUserName(possibleEventDto.getOperatorName() + " (" + thirdMatchInfo.getDataSourceCode() + ")");
                } else {
                    matchScoresPdLog.setOperateUserName(possibleEventDto.getOperatorName());
                }
                matchScoresPdLog.setIpAddress(possibleEventDto.getIpAddress());
                long time = TimeUtils.millsSecondsEast8ZoneGmt();
                matchScoresPdLog.setCreateTime(time);
                matchScoresPdLog.setModifyTime(time);
                matchScoresPdLogMapper.insert(matchScoresPdLog);
            }
            stopWatch.stop();
            log.info("MatchScorePdLogServiceImpl-possibleEventLog-耗时={}, thirdMatchId={}",stopWatch.getTotalTimeMillis(),possibleEventDto.getThirdMatchId());
        } catch (Exception e) {

            log.error("possibleEventLog,标准赛事ID:{} , error:{}", possibleEventDto.getThirdMatchId(), e);
        }

    }
    /**
     * 确认比分事件
     * @param confirmEventDto
     */
    @Override
    public void confirmEventLog(ConfirmEventDto confirmEventDto) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String homeAwayName = null;
        if (confirmEventDto.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
            homeAwayName = CategoryUtils.HOME_PARAM;
        } else {
            homeAwayName = CategoryUtils.AWAY_PARAM;
        }
        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoMapper.selectByPrimaryKey(confirmEventDto.getThirdMatchId());
            switch (thirdMatchInfo.getSportId().intValue()) {
                case 1:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
                    break;
                case 2:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100101.getCode().toString());
                    break;
                case 4:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100102.getCode().toString());
                    break;
            }
            MatchScoresInfo matchScoresInfo = scoresService.selectLiveMatchScoreInfo(confirmEventDto.getThirdMatchId());
            if (matchScoresInfo != null) {
                FootBallScoreVo footBallScoreVo = footBallScoreService.transforScore(matchScoresInfo);
                //确认进球
                if (confirmEventDto.getConfirmEventCode().equals("goal")) {
                    CommonItem goalItem = footBallScoreVo.getGoal();
                    matchScoresPdLog.setOperateRearText(goalItem.getHome() + CategoryUtils.SPLIT_LINE + goalItem.getAway());
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_10082.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10082.getValue());
                }
                //确认点球
                if (confirmEventDto.getConfirmEventCode().equals("penalty")) {
                    CommonItem goalItem = footBallScoreVo.getPeriodGoal();
                    matchScoresPdLog.setOperateRearText(goalItem.getHome() + CategoryUtils.SPLIT_LINE + goalItem.getAway());
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_100130.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100130.getValue());
                }
                //确认黄牌
                if (confirmEventDto.getConfirmEventCode().equals("yellow_card")) {
                    CommonItem yellowCardItem = footBallScoreVo.getYellowCard();
                    matchScoresPdLog.setOperateRearText(yellowCardItem.getHome() + CategoryUtils.SPLIT_LINE + yellowCardItem.getAway());
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_10072.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10072.getValue());
                }
                //确认红牌
                if (confirmEventDto.getConfirmEventCode().equals("red_card")) {
                    CommonItem redCardItem = footBallScoreVo.getRedCard();
                    matchScoresPdLog.setOperateRearText(redCardItem.getHome() + CategoryUtils.SPLIT_LINE + redCardItem.getAway());
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_10075.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10075.getValue());
                }
                //确认角球
                if (confirmEventDto.getConfirmEventCode().equals("corner")) {
                    CommonItem cornerItem = footBallScoreVo.getCorner();
                    matchScoresPdLog.setOperateRearText(cornerItem.getHome() + CategoryUtils.SPLIT_LINE + cornerItem.getAway());
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_10078.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10078.getValue());
                }
                // 确认界外球
                if ("throw_in".equals(confirmEventDto.getConfirmEventCode())) {
                    CommonItem throwIn = footBallScoreVo.getThrowIn();
                    matchScoresPdLog.setOperateRearText((throwIn.getHome()
                            + CategoryUtils.SPLIT_LINE + throwIn.getAway()));
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_100075.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_100075.getValue());
                }
                // 确认持球
                if ("possession".equals(confirmEventDto.getConfirmEventCode())) {
                    CommonItem possession = footBallScoreVo.getPossession();
                    matchScoresPdLog.setOperateRearText((possession.getHome()
                            + CategoryUtils.SPLIT_LINE + possession.getAway()));
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2008.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2008.getValue());
                }
                // 进攻
                if ("attack".equals(confirmEventDto.getConfirmEventCode())) {
                    CommonItem attack = footBallScoreVo.getAttack();
                    matchScoresPdLog.setOperateRearText((attack.getHome()
                            + CategoryUtils.SPLIT_LINE + attack.getAway()));
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_100076.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_100076.getValue());
                }
                // 红黄牌
                if ("yellow_red_card".equals(confirmEventDto.getConfirmEventCode())) {
                    CommonItem yellowRedCard = footBallScoreVo.getYellowRedCard();
                    matchScoresPdLog.setOperateRearText((yellowRedCard.getHome()
                            + CategoryUtils.SPLIT_LINE + yellowRedCard.getAway()));
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_100085.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_100085.getValue());
                }
                // 球门球
                if ("goal_kick".equals(confirmEventDto.getConfirmEventCode())) {
                    CommonItem goalKick = footBallScoreVo.getGoalKick();
                    matchScoresPdLog.setOperateRearText((goalKick.getHome()
                            + CategoryUtils.SPLIT_LINE + goalKick.getAway()));
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_100077.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_100077.getValue());
                }
                // 任意球确认
                if ("free_kick".equals(confirmEventDto.getConfirmEventCode())) {
                    CommonItem freeKick = footBallScoreVo.getFreeKick();
                    matchScoresPdLog.setOperateRearText((freeKick.getHome()
                            + CategoryUtils.SPLIT_LINE + freeKick.getAway()));
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_100078.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_100078.getValue());
                }
                // 越位确认
                if ("offside".equals(confirmEventDto.getConfirmEventCode())) {
                    CommonItem offside = footBallScoreVo.getOffside();
                    matchScoresPdLog.setOperateRearText((offside.getHome()
                            + CategoryUtils.SPLIT_LINE + offside.getAway()));
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_100082.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_100082.getValue());
                }
                // 射正确认
                if ("shot_on_target".equals(confirmEventDto.getConfirmEventCode())) {
                    CommonItem shotOnTarget = footBallScoreVo.getShotOnTarget();
                    matchScoresPdLog.setOperateRearText((shotOnTarget.getHome()
                            + CategoryUtils.SPLIT_LINE + shotOnTarget.getAway()));
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_100083.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_100083.getValue());
                }
                // 射偏确认
                if ("shot_off_target".equals(confirmEventDto.getConfirmEventCode())) {
                    CommonItem shotOffTarget = footBallScoreVo.getShotOffTarget();
                    matchScoresPdLog.setOperateRearText((shotOffTarget.getHome()
                            + CategoryUtils.SPLIT_LINE + shotOffTarget.getAway()));
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_100084.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_100084.getValue());
                }
                // 危险进攻
                if ("dangerous_attack".equals(confirmEventDto.getConfirmEventCode())) {
                    CommonItem dangerousAttack = footBallScoreVo.getDangerousAttack();
                    matchScoresPdLog.setOperateRearText((dangerousAttack.getHome()
                            + CategoryUtils.SPLIT_LINE + dangerousAttack.getAway()));
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_100089.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_100089.getValue());
                }
                //确认VAR进球/点球/罚牌
                if(PDEventCodeEnum.containVAREvent(confirmEventDto.getConfirmEventCode(), CommonConstant.PD_EVENT_TYPE_CONFIRM)) {
                    matchScoresPdLog.setOperateRearText(CategoryUtils.SPLIT_LINE);
                    OperateLogTypeEnum logTypeEnum = OperateLogTypeEnum.getEnumByValue(confirmEventDto.getConfirmEventCode());
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + logTypeEnum.getName() + CategoryUtils.SPLIT_AND + logTypeEnum.getValue());
                }
                StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(thirdMatchInfo.getReferenceId());
                if (standardMatchInfo == null) {
                    return;
                }
                matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
                matchScoresPdLog.setOperateId(standardMatchInfo.getMatchPeriodId().toString());
                matchScoresPdLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
                matchScoresPdLog.setOperateMatchName(getMatchSportTeamNameCode(thirdMatchInfo.getReferenceId()));
                matchScoresPdLog.setOperateName(getPeriodNameByPeriodId(standardMatchInfo.getSportId(), standardMatchInfo.getMatchPeriodId()));
                matchScoresPdLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10065.getCode().toString());
                if (thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                    matchScoresPdLog.setOperateUserName(confirmEventDto.getOperatorName() + " (" + thirdMatchInfo.getDataSourceCode() + ")");
                } else {
                    matchScoresPdLog.setOperateUserName(confirmEventDto.getOperatorName());
                }
                matchScoresPdLog.setIpAddress(confirmEventDto.getIpAddress());
                long time = TimeUtils.millsSecondsEast8ZoneGmt();
                matchScoresPdLog.setCreateTime(time);
                matchScoresPdLog.setModifyTime(time);
                matchScoresPdLogMapper.insert(matchScoresPdLog);
            }
            stopWatch.stop();
            log.info("MatchScorePdLogServiceImpl-confirmEventLog-耗时={}, thirdMatchId={}",stopWatch.getTotalTimeMillis(),thirdMatchInfo.getId());
        } catch (Exception e) {

            log.error("possibleEventLog,标准赛事ID:{} , error:{}", confirmEventDto.getThirdMatchId(), e);
        }

    }
    /**
     * 取消比分事件
     * @param cancelEventDto
     */
    @Override
    public void cancelEventLog(CancelEventDto cancelEventDto) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String homeAwayName = null;
        if (cancelEventDto.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
            homeAwayName = CategoryUtils.HOME_PARAM;
        } else {
            homeAwayName = CategoryUtils.AWAY_PARAM;
        }
        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoMapper.selectByPrimaryKey(cancelEventDto.getThirdMatchId());
            switch (thirdMatchInfo.getSportId().intValue()) {
                case 1:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
                    break;
                case 2:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100101.getCode().toString());
                    break;
                case 4:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100102.getCode().toString());
                    break;
            }
            MatchScoresInfo matchScoresInfo = scoresService.selectLiveMatchScoreInfo(cancelEventDto.getThirdMatchId());
            if (matchScoresInfo != null) {
                FootBallScoreVo footBallScoreVo = footBallScoreService.transforScore(matchScoresInfo);
                //没有进球
                if (cancelEventDto.getCancelEventCode().equals("canceled_goal")) {
                    CommonItem canceledGoalItem = footBallScoreVo.getGoal();
                    matchScoresPdLog.setOperateRearText(canceledGoalItem.getHome() + CategoryUtils.SPLIT_LINE + canceledGoalItem.getAway());
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_10080.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10080.getValue());
                }
                //没有点球
                if (cancelEventDto.getCancelEventCode().equals("canceled_penalty")) {
                    CommonItem canceledGoalItem = footBallScoreVo.getPeriodGoal();
                    matchScoresPdLog.setOperateRearText(canceledGoalItem.getHome() + CategoryUtils.SPLIT_LINE + canceledGoalItem.getAway());
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_100131.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100131.getValue());
                }
                //点球未中
                if (cancelEventDto.getCancelEventCode().equals("penalty_missed")) {
                    CommonItem canceledGoalItem = footBallScoreVo.getPeriodGoal();
                    matchScoresPdLog.setOperateRearText(canceledGoalItem.getHome() + CategoryUtils.SPLIT_LINE + canceledGoalItem.getAway());
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_100132.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100132.getValue());
                }
                //没有黄牌
                if (cancelEventDto.getCancelEventCode().equals("canceled_yellow_card")) {
                    CommonItem yellowCardItem = footBallScoreVo.getYellowCard();
                    matchScoresPdLog.setOperateRearText(yellowCardItem.getHome() + CategoryUtils.SPLIT_LINE + yellowCardItem.getAway());
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_10071.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10071.getValue());
                }
                //没有红牌
                if (cancelEventDto.getCancelEventCode().equals("canceled_red_card")) {
                    CommonItem redCardItem = footBallScoreVo.getRedCard();
                    matchScoresPdLog.setOperateRearText(redCardItem.getHome() + CategoryUtils.SPLIT_LINE + redCardItem.getAway());
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_10074.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10074.getValue());
                }
                //没有角球
                if (cancelEventDto.getCancelEventCode().equals("canceled_corner")) {
                    CommonItem canceledCornerItem = footBallScoreVo.getPeriodCorner();
                    matchScoresPdLog.setOperateRearText(canceledCornerItem.getHome() + CategoryUtils.SPLIT_LINE + canceledCornerItem.getAway());
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_10077.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10077.getValue());
                }
                // 没有任意球
                if ("canceled_free_kick".equals(cancelEventDto.getCancelEventCode())) {
                    CommonItem freeKick = footBallScoreVo.getFreeKick();
                    matchScoresPdLog.setOperateRearText(freeKick.getHome()
                            + CategoryUtils.SPLIT_LINE + freeKick.getAway());
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_100080.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_100080.getValue());
                }
                //取消var进球/点球/罚牌
                if(PDEventCodeEnum.containVAREvent(cancelEventDto.getCancelEventCode(), CommonConstant.PD_EVENT_TYPE_CANCEL)) {
                    matchScoresPdLog.setOperateRearText(CategoryUtils.SPLIT_LINE);
                    OperateLogTypeEnum logTypeEnum = OperateLogTypeEnum.getEnumByValue(cancelEventDto.getCancelEventCode());
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + logTypeEnum.getName() + CategoryUtils.SPLIT_AND + logTypeEnum.getValue());
                }
                StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(thirdMatchInfo.getReferenceId());
                if (standardMatchInfo == null) {
                    return;
                }
                matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
                matchScoresPdLog.setOperateId(standardMatchInfo.getMatchPeriodId().toString());
                matchScoresPdLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
                matchScoresPdLog.setOperateMatchName(getMatchSportTeamNameCode(thirdMatchInfo.getReferenceId()));
                matchScoresPdLog.setOperateName(getPeriodNameByPeriodId(thirdMatchInfo.getSportId(), Long.valueOf(thirdMatchInfo.getMatchPeriod())));
                matchScoresPdLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10065.getCode().toString());
                if (thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                    matchScoresPdLog.setOperateUserName(cancelEventDto.getOperatorName() + " (" + thirdMatchInfo.getDataSourceCode() + ")");
                } else {
                    matchScoresPdLog.setOperateUserName(cancelEventDto.getOperatorName());
                }
                matchScoresPdLog.setIpAddress(cancelEventDto.getIpAddress());
                long time = TimeUtils.millsSecondsEast8ZoneGmt();
                matchScoresPdLog.setCreateTime(time);
                matchScoresPdLog.setModifyTime(time);
                matchScoresPdLogMapper.insert(matchScoresPdLog);
            }
            stopWatch.stop();
            log.info("MatchScorePdLogServiceImpl-cancelEventLog-耗时={}, thirdMatchId={}",stopWatch.getTotalTimeMillis(),cancelEventDto.getThirdMatchId());
        } catch (Exception e) {

            log.error("possibleEventLog,标准赛事ID:{} , error:{}", cancelEventDto.getThirdMatchId(), e);
        }

    }

    @Override
    public void confirmPenaltyEventLog(MatchScoreAndTimeVo data, ConfirmPenaltyEventDTO confirmPenaltyEventDTO) {
        String linkId = confirmPenaltyEventDTO.getLinkedId();
        Long thirdMatchId = confirmPenaltyEventDTO.getThirdMatchId();
        MatchScoresInfo matchScoresInfo = data.getMatchScoresInfo();
        StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        String homeAwayName = null;
        if ( confirmPenaltyEventDTO.getHomeAway().equals(TeamTypeEnum.HOME.code) ) {
            homeAwayName = CategoryUtils.HOME_PARAM;
        } else {
            homeAwayName = CategoryUtils.AWAY_PARAM;
        }
        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoMapper.selectByPrimaryKey( thirdMatchId);
            matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
            if (matchScoresInfo != null) {
                FootBallScoreVo footBallScoreVo = footBallScoreService.transforScore(matchScoresInfo);
                //点球未中
                if ( confirmPenaltyEventDTO.getConfirmEventCode().equals("penalty_missed") ) {
                    CommonItem canceledGoalItem = footBallScoreVo.getPeriodGoal();
                    matchScoresPdLog.setOperateRearText(canceledGoalItem.getHome() + CategoryUtils.SPLIT_LINE + canceledGoalItem.getAway());
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_100132.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100132.getValue());
                }
                //确认点球
                if ( confirmPenaltyEventDTO.getConfirmEventCode().equals("penalty") ) {
                    CommonItem goalItem = footBallScoreVo.getPeriodGoal();
                    matchScoresPdLog.setOperateRearText(goalItem.getHome() + CategoryUtils.SPLIT_LINE + goalItem.getAway());
                    matchScoresPdLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_100130.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100130.getValue());
                }
                StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(thirdMatchInfo.getReferenceId());
                if (standardMatchInfo == null) {
                    return;
                }
                matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
                matchScoresPdLog.setOperateId(standardMatchInfo.getMatchPeriodId().toString());
                matchScoresPdLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
                matchScoresPdLog.setOperateMatchName(getMatchSportTeamNameCode(thirdMatchInfo.getReferenceId()));
                matchScoresPdLog.setOperateName(getPeriodNameByPeriodId(thirdMatchInfo.getSportId(), Long.valueOf(thirdMatchInfo.getMatchPeriod())));
                matchScoresPdLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10065.getCode().toString());
                if (thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                    matchScoresPdLog.setOperateUserName(confirmPenaltyEventDTO.getOperatorName() + " (" + thirdMatchInfo.getDataSourceCode() + ")");
                } else {
                    matchScoresPdLog.setOperateUserName(confirmPenaltyEventDTO.getOperatorName());
                }
                matchScoresPdLog.setIpAddress(confirmPenaltyEventDTO.getIpAddress());
                long time = TimeUtils.millsSecondsEast8ZoneGmt();
                matchScoresPdLog.setCreateTime(time);
                matchScoresPdLog.setModifyTime(time);
                matchScoresPdLogMapper.insert(matchScoresPdLog);
            }
            stopWatch.stop();
            log.info("::{}::MatchScorePdLogServiceImpl-confirmPenaltyEventLog-耗时:{}, thirdMatchId:{}",
                    linkId, stopWatch.getTotalTimeMillis(), thirdMatchId);
        } catch (Exception e) {
            log.error("::{}::possibleEventLog,标准赛事ID:{}, confirmPenaltyEventDTO:{}, error:",
                    linkId, JSONObject.toJSONString(confirmPenaltyEventDTO), thirdMatchId, e);
        }
    }

    /**
     * 人工下发的var事件日志
     * @param eventOperationDto
     * @param matchScoreAndTimeVo 三方赛事ID
     */
    @Override
    public void addVarEventLog(EventOperationDto eventOperationDto, MatchScoreAndTimeVo matchScoreAndTimeVo) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        String key = String.valueOf(eventOperationDto.getThirdMatchId());
        try {
            if (redisService.tryLock(key, key, 2, 3)) {
                StandardMatchInfo standardMatchInfo = matchScoreAndTimeVo.getStandardMatchInfo();
                switch (standardMatchInfo.getSportId().intValue()) {
                    case 1:
                        matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
                        break;
                    case 2:
                        matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100101.getCode().toString());
                        break;
                    case 4:
                        matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100102.getCode().toString());
                        break;
                }
                matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
                matchScoresPdLog.setOperateId(standardMatchInfo.getMatchPeriodId().toString());
                matchScoresPdLog.setOperateName(getPeriodNameByPeriodId(standardMatchInfo.getSportId(), standardMatchInfo.getMatchPeriodId()));
                matchScoresPdLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
                matchScoresPdLog.setOperateMatchName(getMatchSportTeamNameCode(standardMatchInfo.getId()));
                matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10065.getCode().toString());
                matchScoresPdLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                matchScoresPdLog.setOperateRearText(CategoryUtils.SPLIT_LINE);
                if ("water_break".equals(eventOperationDto.getEventCode())) {
                    matchScoresPdLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_100090.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100090.getValue());
                } else if ("possible_video_assistant_referee".equals(eventOperationDto.getEventCode())) {
                    // 进球
                    if ("0".equals(eventOperationDto.getExtraInfo())) {
                        matchScoresPdLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_100091.getName() + "-" + OperateLogTypeEnum.SCORES_PD_1000911.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100091.getValue() + "-" + OperateLogTypeEnum.SCORES_PD_1000911.getValue());
                    }
                    // 点球
                    if ("1".equals(eventOperationDto.getExtraInfo())) {
                        matchScoresPdLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_100091.getName() + "-" + OperateLogTypeEnum.SCORES_PD_1000921.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100091.getValue() + "-" + OperateLogTypeEnum.SCORES_PD_1000921.getValue());
                    }
                    // 红牌
                    if ("2".equals(eventOperationDto.getExtraInfo())) {
                        matchScoresPdLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_100091.getName() + "-" + OperateLogTypeEnum.SCORES_PD_1000931.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100091.getValue() + "-" + OperateLogTypeEnum.SCORES_PD_1000931.getValue());
                    }
                } else if ("video_assistant_referee_over".equals(eventOperationDto.getEventCode())) {
                    // 进球
                    if ("0".equals(eventOperationDto.getExtraInfo())) {
                        matchScoresPdLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_100092.getName() + "-" + OperateLogTypeEnum.SCORES_PD_1000911.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100092.getValue() + "-" + OperateLogTypeEnum.SCORES_PD_1000911.getValue());
                    }
                    // 点球
                    if ("1".equals(eventOperationDto.getExtraInfo())) {
                        matchScoresPdLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_100092.getName() + "-" + OperateLogTypeEnum.SCORES_PD_1000921.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100092.getValue() + "-" + OperateLogTypeEnum.SCORES_PD_1000921.getValue());
                    }
                    // 红牌
                    if ("2".equals(eventOperationDto.getExtraInfo())) {
                        matchScoresPdLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_100092.getName() + "-" + OperateLogTypeEnum.SCORES_PD_1000931.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100092.getValue() + "-" + OperateLogTypeEnum.SCORES_PD_1000931.getValue());
                    }
                } else if ("canceled_video_assistant_referee".equals(eventOperationDto.getEventCode())) {
                    // 进球
                    if ("0".equals(eventOperationDto.getExtraInfo())) {
                        matchScoresPdLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_100093.getName() + "-" + OperateLogTypeEnum.SCORES_PD_1000911.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100093.getValue() + "-" + OperateLogTypeEnum.SCORES_PD_1000911.getValue());
                    }
                    // 点球
                    if ("1".equals(eventOperationDto.getExtraInfo())) {
                        matchScoresPdLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_100093.getName() + "-" + OperateLogTypeEnum.SCORES_PD_1000921.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100093.getValue() + "-" + OperateLogTypeEnum.SCORES_PD_1000921.getValue());
                    }
                    // 红牌
                    if ("2".equals(eventOperationDto.getExtraInfo())) {
                        matchScoresPdLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_100093.getName() + "-" + OperateLogTypeEnum.SCORES_PD_1000931.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100093.getValue() + "-" + OperateLogTypeEnum.SCORES_PD_1000931.getValue());
                    }
                } else {
                    matchScoresPdLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_10083.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10083.getValue());
                }
                if (matchScoreAndTimeVo.getMatchScoresInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || matchScoreAndTimeVo.getMatchScoresInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())) {
                    matchScoresPdLog.setOperateUserName(eventOperationDto.getOperatorName() + " (" + matchScoreAndTimeVo.getMatchScoresInfo().getDataSourceCode() + ")");
                } else {
                    matchScoresPdLog.setOperateUserName(eventOperationDto.getOperatorName());
                }
                matchScoresPdLog.setIpAddress(eventOperationDto.getIpAddress());
                long time = TimeUtils.millsSecondsEast8ZoneGmt();
                matchScoresPdLog.setCreateTime(time);
                matchScoresPdLog.setModifyTime(time);
                matchScoresPdLogMapper.insert(matchScoresPdLog);
            } else {
                throw new RuntimeException("redis锁失败:");
            }
        } catch (Exception e) {

            log.error("addVarEventLog,标准赛事ID:{} , error:{}", matchScoreAndTimeVo.getStandardMatchInfo().getId(), e);
        } finally {
            redisService.unLock(key, key);
        }

    }
    /**
     * 更改赛事时间
     * @param matchScoreAndTimeVo
     * @param secondFromStart
     * @param changeMatchTimeDto
     */
    @Override
    public void changeMatchTimeLog(MatchScoreAndTimeVo matchScoreAndTimeVo, Long secondFromStart, ChangeMatchTimeDto changeMatchTimeDto) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            matchScoreAndTimeVo.getMatchTimeInfo().setSecondFromStart(secondFromStart);
            StandardMatchInfo standardMatchInfo = matchScoreAndTimeVo.getStandardMatchInfo();
            ThirdMatchInfo thirdMatchInfo = matchScoreAndTimeVo.getThirdMatchInfo();
            switch (thirdMatchInfo.getSportId().intValue()) {
                case 1:// 足球
                    Long startTimeSecond = matchScoreAndTimeVo.getMatchTimeInfo().getSecondFromStart() + (System.currentTimeMillis() - matchScoreAndTimeVo.getMatchTimeInfo().getEventTime()) / 1000;
                    if (startTimeSecond < 0L) {
                        startTimeSecond = 0L;
                    }
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
                    matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10088.getCode().toString());
                    matchScoresPdLog.setOperateForwText(getPeriodNameByPeriodId(standardMatchInfo.getSportId(), standardMatchInfo.getMatchPeriodId()) + getMinuteTime(startTimeSecond));//操作前
                    matchScoresPdLog.setOperateRearText(getPeriodNameByPeriodId(standardMatchInfo.getSportId(), standardMatchInfo.getMatchPeriodId()) + getMinuteTime(changeMatchTimeDto.getMatchTime()));//操作后
                    break;
                case 2:// 篮球
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100101.getCode().toString());
                    if (changeMatchTimeDto.getType() == null || changeMatchTimeDto.getType() == 0) {
                        matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10089.getCode().toString());
                        matchScoresPdLog.setOperateForwText(changeMatchTimeDto.getBeforeTime());//操作前
                        matchScoresPdLog.setOperateRearText(changeMatchTimeDto.getAfterTime());//操作后
                    } else {
                        matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10088.getCode().toString());
                        matchScoresPdLog.setOperateForwText(getPeriodNameByPeriodId(standardMatchInfo.getSportId(), standardMatchInfo.getMatchPeriodId()) + CategoryUtils.SPLIT_LINE + changeMatchTimeDto.getBeforeTime());//操作前
                        matchScoresPdLog.setOperateRearText(getPeriodNameByPeriodId(standardMatchInfo.getSportId(), standardMatchInfo.getMatchPeriodId()) + CategoryUtils.SPLIT_LINE + changeMatchTimeDto.getAfterTime());//操作后
                    }
                    break;
                case 4:// 冰球
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100102.getCode().toString());
                    if (changeMatchTimeDto.getType() == null || changeMatchTimeDto.getType() == 0) {
                        matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10089.getCode().toString());
                        matchScoresPdLog.setOperateForwText(changeMatchTimeDto.getBeforeTime());//操作前
                        matchScoresPdLog.setOperateRearText(changeMatchTimeDto.getAfterTime());//操作后
                    } else {
                        matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10088.getCode().toString());
                        matchScoresPdLog.setOperateForwText(getPeriodNameByPeriodId(standardMatchInfo.getSportId(), standardMatchInfo.getMatchPeriodId()) + CategoryUtils.SPLIT_LINE + changeMatchTimeDto.getBeforeTime());//操作前
                        matchScoresPdLog.setOperateRearText(getPeriodNameByPeriodId(standardMatchInfo.getSportId(), standardMatchInfo.getMatchPeriodId()) + CategoryUtils.SPLIT_LINE + changeMatchTimeDto.getAfterTime());//操作后
                    }
                    break;
            }
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateName(getMatchSportTeamNameCode(thirdMatchInfo.getReferenceId()));
            matchScoresPdLog.setOperateMatchId(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateMatchName(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateParaName(CategoryUtils.SPLIT_LINE);
            if (matchScoreAndTimeVo.getMatchScoresInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || matchScoreAndTimeVo.getMatchScoresInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(changeMatchTimeDto.getOperatorName() + " (" + matchScoreAndTimeVo.getMatchScoresInfo().getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(changeMatchTimeDto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(changeMatchTimeDto.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);//操作时间
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);
        } catch (Exception e) {

            log.error("changeMatchTimeLog,标准赛事ID:{} , error:{}", matchScoreAndTimeVo.getThirdMatchInfo().getReferenceId(), e);
        }

    }

    /**
     * 打印PD报球版结算冻结日志
     * @param matchFreezeSettleDto
     */
    @Override
    public void matchFreezeSettleLog(MatchFreezeSettlePdLogEvent matchFreezeSettleDto) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            ThirdMatchInfoExample thirdMatchInfoExample = new ThirdMatchInfoExample();
            thirdMatchInfoExample.createCriteria().andReferenceIdEqualTo(Long.valueOf(matchFreezeSettleDto.getMatchId())).andDataSourceCodeEqualTo("PD");
            List<ThirdMatchInfo> thirdMatchInfoList = thirdMatchInfoMapper.selectByExample(thirdMatchInfoExample);
            if (thirdMatchInfoList.isEmpty()) {
                return;
            }
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoList.get(0);
            switch (thirdMatchInfo.getSportId().intValue()) {
                case 1:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
                    break;
                case 2:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100101.getCode().toString());
                    break;
                case 4:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100102.getCode().toString());
                    break;
            }
            StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(thirdMatchInfo.getReferenceId());
            if (standardMatchInfo == null) {
                return;
            }
            matchScoresPdLog.setOperateId(standardMatchInfo.getMatchManageId());//操作对象ID
            matchScoresPdLog.setOperateName(getMatchSportTeamNameCode(thirdMatchInfo.getReferenceId()));
            if (StringUtils.isNotEmpty(matchFreezeSettleDto.getCategory()) && matchFreezeSettleDto.getCategory().equals("Y")) {
                matchScoresPdLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_10092.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10092.getValue());//操作后
                matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10090.getCode().toString());
            } else {
                matchScoresPdLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_10093.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10093.getValue());//操作后
                matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10091.getCode().toString());
            }
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateForwText(matchFreezeSettleDto.getOperateForw());//操作前
            matchScoresPdLog.setOperateParaName(CategoryUtils.SPLIT_LINE);
            if (thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(matchFreezeSettleDto.getOperatorName() + " (" + thirdMatchInfo.getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(matchFreezeSettleDto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(matchFreezeSettleDto.getIp());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);//操作时间
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);
        } catch (Exception e) {

            log.error("matchFreezeSettleLog,标准赛事ID:{} , error:{}", matchFreezeSettleDto.getMatchId(), e);
        }

    }

    /**
     * 修改比分日志
     * @param oldScoreMap
     * @param data
     * @param changeMatchScoreDto
     */
    @Override
    public void changeMatchScoreLog(Map<String, String> oldScoreMap, MatchScoreAndTimeVo data, ChangeMatchScoreDto changeMatchScoreDto) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            Integer t1 = null;
            Integer t2 = null;
            switch (data.getStandardMatchInfo().getSportId().intValue()) {
                case 2:
                    JSONObject periodBasketballScores = JSONObject.parseObject(oldScoreMap.get("scoresJson"));
                    Map<Long, BasketballScores> basketballAllPeriodScores = JsonMapUtils.parseBasketballMap(periodBasketballScores);
                    for (Map.Entry<Long, BasketballScores> entry : basketballAllPeriodScores.entrySet()) {
                        if (entry.getKey().equals(changeMatchScoreDto.getPeriod())) {
                            t1 = entry.getValue().getMatchScore().getHome();
                            t2 = entry.getValue().getMatchScore().getAway();
                            break;
                        }
                    }
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100101.getCode().toString());
                    break;
                case 4:
                    Map<Long, IceHockeyScores> iceHockeyAllPeriodScores = scoreUtils.periodJson(oldScoreMap.get("scoresJson"), IceHockeyScores.class);
                    for (Map.Entry<Long, IceHockeyScores> entry : iceHockeyAllPeriodScores.entrySet()) {
                        if (entry.getKey().equals(changeMatchScoreDto.getPeriod())) {
                            t1 = entry.getValue().getMatchScore().getHome();
                            t2 = entry.getValue().getMatchScore().getAway();
                            break;
                        }
                    }
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100102.getCode().toString());
                    break;
                default:
                    return;
            }
            matchScoresPdLog.setMatchManageId(data.getStandardMatchInfo().getMatchManageId());
            if (data.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || data.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(changeMatchScoreDto.getOperatorName() + " (" + data.getThirdMatchInfo().getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(changeMatchScoreDto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(changeMatchScoreDto.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);//操作时间
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLog.setOperateId(changeMatchScoreDto.getPeriod().toString());
            switch (changeMatchScoreDto.getType()) { // type=0 左边， type=1    右边，
                case 0:
                    matchScoresPdLog.setOperateName(getPeriodNameByPeriodId(data.getStandardMatchInfo().getSportId(), changeMatchScoreDto.getPeriod()));
                    matchScoresPdLog.setOperateMatchId(data.getStandardMatchInfo().getMatchManageId());
                    matchScoresPdLog.setOperateMatchName(getMatchSportTeamNameCode(data.getThirdMatchInfo().getReferenceId()));
                    matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100121.getCode().toString());
                    if (t1 == null && t2 == null) {
                        matchScoresPdLog.setOperateForwText(0 + CategoryUtils.SPLIT_LINE + 0);
                    } else {
                        matchScoresPdLog.setOperateForwText(t1 + CategoryUtils.SPLIT_LINE + t2);
                    }
                    matchScoresPdLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_10094.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10094.getValue());
                    matchScoresPdLog.setOperateRearText(changeMatchScoreDto.getPeriodT1() + CategoryUtils.SPLIT_LINE + changeMatchScoreDto.getPeriodT2());//操作后
                    matchScoresPdLogMapper.insert(matchScoresPdLog);
                    break;
                case 1:
                    matchScoresPdLog.setOperateForwText(CategoryUtils.SPLIT_LINE);//操作前
                    matchScoresPdLog.setOperateId(data.getStandardMatchInfo().getMatchManageId());
                    matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10094.getCode().toString());
                    matchScoresPdLog.setOperateName(getMatchSportTeamNameCode(data.getStandardMatchInfo().getId()));
                    matchScoresPdLog.setOperateMatchId(CategoryUtils.SPLIT_LINE);
                    matchScoresPdLog.setOperateMatchName(CategoryUtils.SPLIT_LINE);
                    Integer periodT1Old = oldScoreMap.get("periodT1Old") != null ? Integer.valueOf(oldScoreMap.get("periodT1Old")) : 0;
                    Integer periodT2Old = oldScoreMap.get("periodT2Old") != null ? Integer.valueOf(oldScoreMap.get("periodT2Old")) : 0;
                    Integer periodT1New = changeMatchScoreDto.getPeriodT1();
                    Integer periodT2New = changeMatchScoreDto.getPeriodT2();
                    if (!periodT1Old.equals(periodT1New)) { //主队
                        int homeAfterScore = 0;
                        if (periodT1New > periodT1Old) {
                            homeAfterScore = periodT1New - periodT1Old;
                            matchScoresPdLog.setOperateRearText(Integer.toString(homeAfterScore));//操作后
                        } else {
                            homeAfterScore = periodT1Old - periodT1New;
                            matchScoresPdLog.setOperateRearText("-" + homeAfterScore);//操作后
                        }
                        matchScoresPdLog.setOperateParaName(CategoryUtils.HOME_PARAM + CategoryUtils.SPLIT_LINE + getPeriodNameByPeriodId(data.getStandardMatchInfo().getSportId(), changeMatchScoreDto.getPeriod()));
                        matchScoresPdLogMapper.insert(matchScoresPdLog);
                    }
                    if (!periodT2Old.equals(periodT2New)) { //客队
                        int awayAfterScore = 0;
                        if (periodT2New > periodT2Old) {
                            awayAfterScore = periodT2New - periodT2Old;
                            matchScoresPdLog.setOperateRearText(Integer.toString(awayAfterScore));//操作后
                        } else {
                            awayAfterScore = periodT2Old - periodT2New;
                            matchScoresPdLog.setOperateRearText("-" + awayAfterScore);//操作后
                        }
                        matchScoresPdLog.setOperateParaName(CategoryUtils.AWAY_PARAM + CategoryUtils.SPLIT_LINE + getPeriodNameByPeriodId(data.getStandardMatchInfo().getSportId(), changeMatchScoreDto.getPeriod()));
                        matchScoresPdLogMapper.insert(matchScoresPdLog);
                    }
                    break;
            }

        } catch (Exception e) {

            log.error("changeMatchScoreLog,标准赛事ID:{} , error:{}", data.getStandardMatchInfo().getId(), e);
        }
    }

    /**
     * 修改篮球、冰球的赛事阶段
     * @param data
     * @param periodId
     * @param changeMatchPeriodDto
     */
    @Override
    public void changeMatchPeriodLog(MatchScoreAndTimeVo data, Long periodId, ChangeMatchPeriodDto changeMatchPeriodDto) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            matchScoresPdLog.setOperateForwText(getPeriodNameByPeriodId(data.getThirdMatchInfo().getSportId(), periodId));
            matchScoresPdLog.setOperateRearText(getPeriodNameByPeriodId(data.getThirdMatchInfo().getSportId(), changeMatchPeriodDto.getPeriodId()));
            switch (data.getThirdMatchInfo().getSportId().intValue()) {
                case 1:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
                    break;
                case 2:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100101.getCode().toString());
                    break;
                case 4:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100102.getCode().toString());
                    break;
            }
            matchScoresPdLog.setMatchManageId(data.getStandardMatchInfo().getMatchManageId());
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10097.getCode().toString());
            matchScoresPdLog.setOperateId(data.getStandardMatchInfo().getMatchManageId());
            matchScoresPdLog.setOperateName(getMatchSportTeamNameCode(data.getStandardMatchInfo().getId()));
            matchScoresPdLog.setOperateParaName(CategoryUtils.SPLIT_LINE);
            if (data.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || data.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(changeMatchPeriodDto.getOperatorName() + " (" + data.getThirdMatchInfo().getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(changeMatchPeriodDto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(changeMatchPeriodDto.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);
        } catch (Exception e) {

            log.error("changeMatchPeriodLog,标准赛事ID:{} , error:{}", data.getStandardMatchInfo().getId(), e);
        }
    }

    /**
     * 报球板足球5分钟进球编辑
     * @param scoresJson
     * @param data
     * @param confirmEventDto
     */
    @Override
    public void modify5MinScoreLog(String scoresJson, MatchScoreAndTimeVo data, Goal5MinDto confirmEventDto) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            ThirdMatchInfo thirdMatchInfo = data.getThirdMatchInfo();
            matchScoresPdLog.setOperateId(data.getStandardMatchInfo().getMatchPeriodId().toString());
            matchScoresPdLog.setOperateName(getPeriodNameByPeriodId(data.getStandardMatchInfo().getSportId(), data.getStandardMatchInfo().getMatchPeriodId()));
            matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
            matchScoresPdLog.setOperateMatchId(data.getStandardMatchInfo().getMatchManageId());//操作对象ID
            matchScoresPdLog.setOperateMatchName(getMatchSportTeamNameCode(thirdMatchInfo.getReferenceId()));
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100121.getCode().toString());
            if (data.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || data.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(confirmEventDto.getOperatorName() + " (" + data.getThirdMatchInfo().getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(confirmEventDto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(confirmEventDto.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);//操作时间
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLog.setMatchManageId(data.getStandardMatchInfo().getMatchManageId());
            JSONObject periodFootballScores = JSONObject.parseObject(scoresJson);
            Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
            List<Goal5MinDataDto> dataList = confirmEventDto.getDataList();
            if (!dataList.isEmpty()) {
                for (Goal5MinDataDto editFiveData : dataList) {
                    //获取比赛阶段
                    Long period5Min = editFiveData.getPeriod5Min();//编辑后的5分钟阶段id
                    FootballScores periodSores5 = allPeriodScores.get(period5Min);
                    if (periodSores5 == null) {
                        periodSores5 = FootballScores.createMinFootballScores();
                    }
                    if (!periodSores5.getGoal().getHome().equals(editFiveData.getHomeScore()) || !periodSores5.getGoal().getAway().equals(editFiveData.getAwayScore())) {
                        matchScoresPdLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_100140.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100140.getValue() + CategoryUtils.SPLIT_LINE + CategoryUtils.time5MinMap.get(period5Min));
                        matchScoresPdLog.setOperateForwText(periodSores5.getGoal().getHome() + CategoryUtils.SPLIT_LINE + periodSores5.getGoal().getAway());
                        matchScoresPdLog.setOperateRearText(editFiveData.getHomeScore() + CategoryUtils.SPLIT_LINE + editFiveData.getAwayScore());
                        matchScoresPdLogMapper.insert(matchScoresPdLog);
                    }
                }
            }
        } catch (Exception e) {

            log.error("modify5MinScoreLog,标准赛事ID:{} , error:{}", data.getThirdMatchInfo().getReferenceId(), e);
        }
    }

    /**
     * 报球板足球15分钟进球编辑
     * @param scoresJson
     * @param data
     * @param confirmEventDto
     */
    @Override
    public void modify15MinScoreLog(String scoresJson, MatchScoreAndTimeVo data, Goal15MinDto confirmEventDto) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            ThirdMatchInfo thirdMatchInfo = data.getThirdMatchInfo();
            matchScoresPdLog.setOperateId(thirdMatchInfo.getMatchPeriod());
            matchScoresPdLog.setOperateName(getPeriodNameByPeriodId(data.getThirdMatchInfo().getSportId(), data.getStandardMatchInfo().getMatchPeriodId()));
            matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
            matchScoresPdLog.setOperateMatchId(data.getStandardMatchInfo().getMatchManageId());
            matchScoresPdLog.setOperateMatchName(getMatchSportTeamNameCode(thirdMatchInfo.getReferenceId()));
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100121.getCode().toString());
            if (data.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || data.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(confirmEventDto.getOperatorName() + " (" + data.getThirdMatchInfo().getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(confirmEventDto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(confirmEventDto.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);//操作时间
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLog.setMatchManageId(data.getStandardMatchInfo().getMatchManageId());
            JSONObject periodFootballScores = JSONObject.parseObject(scoresJson);
            Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
            List<Goal15MinDataDto> dataList = confirmEventDto.getDataList();
            if (!dataList.isEmpty()) {
                for (Goal15MinDataDto editFiveData : dataList) {
                    //获取比赛阶段
                    Long period15Min = editFiveData.getPeriod15Min();//编辑后的5分钟阶段id
                    FootballScores periodSores15 = allPeriodScores.get(period15Min);
                    if (periodSores15 == null) {
                        periodSores15 = FootballScores.createMinFootballScores();
                    }
                    if (!periodSores15.getGoal().getHome().equals(editFiveData.getHomeScore()) || !periodSores15.getGoal().getAway().equals(editFiveData.getAwayScore())) {
                        matchScoresPdLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_100140.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100140.getValue() + CategoryUtils.SPLIT_LINE + CategoryUtils.time15MinMap.get(period15Min));
                        matchScoresPdLog.setOperateForwText(periodSores15.getGoal().getHome() + CategoryUtils.SPLIT_LINE + periodSores15.getGoal().getAway());
                        matchScoresPdLog.setOperateRearText(editFiveData.getHomeScore() + CategoryUtils.SPLIT_LINE + editFiveData.getAwayScore());
                        matchScoresPdLogMapper.insert(matchScoresPdLog);
                    }

                }
            }

        } catch (Exception e) {

            log.error("modify15MinScoreLog,标准赛事ID:{} , error:{}", data.getThirdMatchInfo().getReferenceId(), e);
        }

    }

    /**
     * 报球板足球5分钟角球编辑
     * @param linkId
     * @param scoresJson
     * @param data
     * @param confirmEventDto
     */
    @Override
    public void modify15MinCornerLog(String linkId, String scoresJson, MatchScoreAndTimeVo data, Goal15MinDto confirmEventDto) {
        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            ThirdMatchInfo thirdMatchInfo = data.getThirdMatchInfo();
            matchScoresPdLog.setOperateId(thirdMatchInfo.getMatchPeriod());
            matchScoresPdLog.setOperateName(getPeriodNameByPeriodId(data.getThirdMatchInfo().getSportId(), data.getStandardMatchInfo().getMatchPeriodId()));
            matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
            matchScoresPdLog.setOperateMatchId(data.getStandardMatchInfo().getMatchManageId());
            matchScoresPdLog.setOperateMatchName(getMatchSportTeamNameCode(thirdMatchInfo.getReferenceId()));
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100121.getCode().toString());
            if (data.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || data.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(confirmEventDto.getOperatorName() + " (" + data.getThirdMatchInfo().getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(confirmEventDto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(confirmEventDto.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLog.setMatchManageId(data.getStandardMatchInfo().getMatchManageId());
            JSONObject periodFootballScores = JSONObject.parseObject(scoresJson);
            Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
            List<Goal15MinDataDto> dataList = confirmEventDto.getDataList();
            if (!dataList.isEmpty()) {
                for (Goal15MinDataDto editFiveData : dataList) {
                    //获取比赛阶段
                    Long period15Min = editFiveData.getPeriod15Min();
                    FootballScores periodSores15 = allPeriodScores.get(period15Min);
                    if (periodSores15 == null) {
                        periodSores15 = FootballScores.createMinFootballScores();
                    }
                    if (null == periodSores15.getCorner()) {
                        CommonItem commonItem = new CommonItem();
                        commonItem.setAway(0);
                        commonItem.setHome(0);
                        periodSores15.setCorner(commonItem);
                    }
                    Integer homeScore = periodSores15.getCorner().getHome();
                    Integer awayScore = periodSores15.getCorner().getAway();
                    log.info("::{}::modify15MinCornerLog数据保存, homeScore:{}, awayScore:{}, editHome:{}, editAway:{}, 是否修改:{}",
                            linkId, homeScore, awayScore, editFiveData.getHomeScore(), editFiveData.getAwayScore(),
                            !homeScore.equals(editFiveData.getHomeScore()) || !awayScore.equals(editFiveData.getAwayScore()));
                    if (!homeScore.equals(editFiveData.getHomeScore()) || !awayScore.equals(editFiveData.getAwayScore())) {
                        matchScoresPdLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_100141.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100141.getValue() + CategoryUtils.SPLIT_LINE + CategoryUtils.time15MinMap.get(period15Min));
                        matchScoresPdLog.setOperateForwText(homeScore + CategoryUtils.SPLIT_LINE + awayScore);
                        matchScoresPdLog.setOperateRearText(editFiveData.getHomeScore() + CategoryUtils.SPLIT_LINE + editFiveData.getAwayScore());
                        matchScoresPdLogMapper.insert(matchScoresPdLog);
                    }

                }
            }

        } catch (Exception e) {

            log.error("::{}::modify15MinCornerLog,标准赛事ID:{} , error:{}", linkId, data.getThirdMatchInfo().getReferenceId(), e);
        }
    }


    /**
     * 罚牌15分钟编辑日志
     * @param linkId
     * @param scoresJson
     * @param data
     * @param confirmEventDto
     */
    @Override
    public void modify15MinCardLog(String linkId, String scoresJson, MatchScoreAndTimeVo data, Goal15MinDto confirmEventDto) {
        String eventCode = confirmEventDto.getConfirmEventCode();
        // 默认黄牌
        log.info("::{}::modify15MinCardLog的入参", linkId);
        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            ThirdMatchInfo thirdMatchInfo = data.getThirdMatchInfo();
            matchScoresPdLog.setOperateId(thirdMatchInfo.getMatchPeriod());
            matchScoresPdLog.setOperateName(getPeriodNameByPeriodId(data.getThirdMatchInfo().getSportId(), data.getStandardMatchInfo().getMatchPeriodId()));
            matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
            matchScoresPdLog.setOperateMatchId(data.getStandardMatchInfo().getMatchManageId());
            matchScoresPdLog.setOperateMatchName(getMatchSportTeamNameCode(thirdMatchInfo.getReferenceId()));
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100121.getCode().toString());
            if (data.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || data.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(confirmEventDto.getOperatorName() + " (" + data.getThirdMatchInfo().getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(confirmEventDto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(confirmEventDto.getIpAddress());
            matchScoresPdLog.setMatchManageId(data.getStandardMatchInfo().getMatchManageId());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            JSONObject periodFootballScores = JSONObject.parseObject(scoresJson);
            Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
            List<Goal15MinDataDto> dataList = confirmEventDto.getDataList();
            if (!dataList.isEmpty()) {
                for (Goal15MinDataDto editFiveData : dataList) {
                    //获取比赛阶段
                    Long period15Min = editFiveData.getPeriod15Min();
                    FootballScores periodSores15 = allPeriodScores.get(period15Min);
                    if (periodSores15 == null) {
                        periodSores15 = FootballScores.createMinFootballScores();
                    }
                    Integer homeScore = 0;
                    Integer awayScore = 0;
                    String carName = null;
                    if (EventCodeEnum.YELLOW_CARD.code.toLowerCase(Locale.ROOT).equals(eventCode.toLowerCase(Locale.ROOT))) {
                        if (null == periodSores15.getYellowCard()) {
                            CommonItem commonItem = new CommonItem();
                            commonItem.setAway(0);
                            commonItem.setHome(0);
                            periodSores15.setYellowCard(commonItem);
                        }
                        homeScore = periodSores15.getYellowCard().getHome();
                        awayScore = periodSores15.getYellowCard().getAway();
                        carName = OperateLogTypeEnum.SCORES_PD_100142.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100142.getValue();
                    }
                    if (EventCodeEnum.RED_CARD.code.toLowerCase(Locale.ROOT).equals(eventCode.toLowerCase(Locale.ROOT))) {
                        if (null == periodSores15.getRedCard()) {
                            CommonItem commonItem = new CommonItem();
                            commonItem.setAway(0);
                            commonItem.setHome(0);
                            periodSores15.setRedCard(commonItem);
                        }
                        homeScore = periodSores15.getRedCard().getHome();
                        awayScore = periodSores15.getRedCard().getAway();
                        carName = OperateLogTypeEnum.SCORES_PD_100143.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100143.getValue();
                    }
                    log.info("::{}::modify15MinCardLog数据保存, homeScore:{}, awayScore:{}, editHome:{}, editAway:{}, 是否修改:{}",
                            linkId, homeScore, awayScore, editFiveData.getHomeScore(), editFiveData.getAwayScore(),
                            !homeScore.equals(editFiveData.getHomeScore()) || !awayScore.equals(editFiveData.getAwayScore()));
                    if (!homeScore.equals(editFiveData.getHomeScore()) || !awayScore.equals(editFiveData.getAwayScore())) {
                        if(!StringUtils.isAnyEmpty(carName)){
                            matchScoresPdLog.setOperateParaName(carName + CategoryUtils.SPLIT_LINE + CategoryUtils.time15MinMap.get(period15Min));
                        }else{
                            matchScoresPdLog.setOperateParaName(CategoryUtils.time15MinMap.get(period15Min));
                        }
                        matchScoresPdLog.setOperateForwText(homeScore + CategoryUtils.SPLIT_LINE + awayScore);
                        matchScoresPdLog.setOperateRearText(editFiveData.getHomeScore() + CategoryUtils.SPLIT_LINE + editFiveData.getAwayScore());
                        log.info("::{}::modify15MinCardLog数据保存:{}", linkId, JSON.toJSONString(matchScoresPdLog));
                        matchScoresPdLogMapper.insert(matchScoresPdLog);
                    }

                }
            }
        } catch (Exception e) {

            log.error("::{}::modify15MinCardLog,标准赛事ID:{} , error:{}", linkId, data.getThirdMatchInfo().getReferenceId(), e);
        }
    }

    /**
     * 点球大战编辑比分
     * @param extryScore
     * @param afterData
     * @param penaltyScoresEditDto
     */
    @Override
    public void editPenaltyScoreLog(String extryScore, MatchScoreAndTimeVo afterData, PenaltyScoresEditDto penaltyScoresEditDto) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            FootballPenaltyScores footballPenaltyScores = null;
            StandardMatchInfo standardMatchInfo = afterData.getStandardMatchInfo();
            matchScoresPdLog.setOperateId(standardMatchInfo.getMatchPeriodId().toString());
            matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
            matchScoresPdLog.setOperateName(getPeriodNameByPeriodId(standardMatchInfo.getSportId(), standardMatchInfo.getMatchPeriodId()));
            matchScoresPdLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateMatchName(getMatchSportTeamNameCode(standardMatchInfo.getId()));
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100121.getCode().toString());
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            //第X轮-H，第X轮-A
            if (StringUtils.isEmpty(extryScore)) {
                return;
            } else {
                footballPenaltyScores = JSONObject.toJavaObject((JSONObject.parseObject(extryScore)), FootballPenaltyScores.class);
            }
            CommonItem commonItem = footballPenaltyScores.getRoundScores().get(penaltyScoresEditDto.getTargetRound().toString());
            if (commonItem == null) {
                return;
            }
            String editTeam = null;
            if (!(commonItem.getHome()==null?Integer.valueOf(0):commonItem.getHome()).equals(penaltyScoresEditDto.getHome())) {
                editTeam = CategoryUtils.HOME_PARAM;
            } else {
                editTeam = CategoryUtils.AWAY_PARAM;
            }
            String paramName = "第 X 轮".replace("X", penaltyScoresEditDto.getTargetRound().toString()) + CategoryUtils.SPLIT_AND + "Round " + penaltyScoresEditDto.getTargetRound();
            matchScoresPdLog.setOperateParaName(editTeam + CategoryUtils.SPLIT_LINE + paramName);
            matchScoresPdLog.setOperateForwText((commonItem.getHome()==null?0:commonItem.getHome()) + CategoryUtils.SPLIT_LINE + (commonItem.getAway()==null?0:commonItem.getAway()));
            matchScoresPdLog.setOperateRearText((penaltyScoresEditDto.getHome()==null?0:penaltyScoresEditDto.getHome()) + CategoryUtils.SPLIT_LINE + (penaltyScoresEditDto.getAway()==null?0:penaltyScoresEditDto.getAway()));//操作后
            if (afterData.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || afterData.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(penaltyScoresEditDto.getOperatorName() + " (" + afterData.getThirdMatchInfo().getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(penaltyScoresEditDto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(penaltyScoresEditDto.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);//操作时间
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);
        } catch (Exception e) {

            log.error("editPenaltyScoreLog,标准赛事ID:{} , error:{}", afterData.getStandardMatchInfo().getId(), e);
        }

    }

    /**
     * 点球大战阶段切换
     * @param data
     * @param penaltyChangeRoundsDto
     */
    @Override
    public void changePenaltyRoundsLog(MatchScoreAndTimeVo data, PenaltyChangeRoundsDto penaltyChangeRoundsDto) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            switch (data.getThirdMatchInfo().getSportId().intValue()) {
                case 1:
                    matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
                    break;
            }
            matchScoresPdLog.setMatchManageId(data.getStandardMatchInfo().getMatchManageId());
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10097.getCode().toString());
            matchScoresPdLog.setOperateId(data.getStandardMatchInfo().getMatchManageId());
            matchScoresPdLog.setOperateName(getMatchSportTeamNameCode(data.getStandardMatchInfo().getId()));
            matchScoresPdLog.setOperateParaName(getPeriodNameByPeriodId(data.getStandardMatchInfo().getSportId(), data.getStandardMatchInfo().getMatchPeriodId()));
            if (data.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || data.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(penaltyChangeRoundsDto.getOperatorName() + " (" + data.getThirdMatchInfo().getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(penaltyChangeRoundsDto.getOperatorName());
            }
            matchScoresPdLog.setOperateForwText(Integer.toString(penaltyChangeRoundsDto.getTargetRound() - 1));
            matchScoresPdLog.setOperateRearText(penaltyChangeRoundsDto.getTargetRound().toString());
            matchScoresPdLog.setIpAddress(penaltyChangeRoundsDto.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);
        } catch (Exception e) {

            log.error("changeMatchPeriodLog,标准赛事ID:{} , error:{}", data.getStandardMatchInfo().getId(), e);
        }
    }

    /**
     * 新增点球大战阶段
     * @param data
     * @param penaltyAddRoundsDto
     */
    @Override
    public void addPenaltyRoundsLog(MatchScoreAndTimeVo data, PenaltyAddRoundsDto penaltyAddRoundsDto) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            String extraScores = data.getMatchScoresInfo().getScoresJsonExtra();
            FootballPenaltyScores footballPenaltyScores = null;
            if (StringUtils.isEmpty(extraScores)) {
                footballPenaltyScores = new FootballPenaltyScores();
            } else {
                footballPenaltyScores = JSONObject.toJavaObject((JSONObject.parseObject(extraScores)), FootballPenaltyScores.class);
            }
            Map<String, CommonItem> penaltyRound = footballPenaltyScores.getRoundScores();
            int round = penaltyRound.size();
            matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100133.getCode().toString());
            matchScoresPdLog.setOperateId(Integer.toString(round));
            matchScoresPdLog.setOperateName(OperateLogTypeEnum.SCORES_PD_100135.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100135.getValue());
            matchScoresPdLog.setOperateMatchId(data.getStandardMatchInfo().getMatchManageId());
            matchScoresPdLog.setOperateMatchName(getMatchSportTeamNameCode(data.getStandardMatchInfo().getId()));
            String periodId = Integer.toString(round);
            String paramName = "第 X 轮".replace("X", periodId) + CategoryUtils.SPLIT_AND + "Round " + periodId;
            matchScoresPdLog.setOperateParaName(paramName);
            matchScoresPdLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateRearText(CategoryUtils.SPLIT_LINE);
            if (data.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || data.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(penaltyAddRoundsDto.getOperatorName() + " (" + data.getThirdMatchInfo().getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(penaltyAddRoundsDto.getOperatorName());
            }
            matchScoresPdLog.setMatchManageId(data.getStandardMatchInfo().getMatchManageId());
            matchScoresPdLog.setIpAddress(penaltyAddRoundsDto.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);
        } catch (Exception e) {

            log.error("changeMatchPeriodLog,标准赛事ID:{} , error:{}", data.getStandardMatchInfo().getId(), e);
        }

    }

    @Override
    public void changePenaltyFirstLog(MatchScoreAndTimeVo data, PenaltyFirstDto penaltyFirstDto) {
        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            String extraScores = data.getMatchScoresInfo().getScoresJsonExtra();
            FootballPenaltyScores footballPenaltyScores = null;
            if (StringUtils.isEmpty(extraScores)) {
                footballPenaltyScores = new FootballPenaltyScores();
            } else {
                footballPenaltyScores = JSONObject.toJavaObject((JSONObject.parseObject(extraScores)), FootballPenaltyScores.class);
            }
            Map<String, CommonItem> penaltyRound = footballPenaltyScores.getRoundScores();
            int round = penaltyRound.size();
            matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100500.getCode().toString());
            matchScoresPdLog.setOperateId(Integer.toString(round));
            matchScoresPdLog.setOperateName(OperateLogTypeEnum.SCORES_PD_100500.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100500.getValue());
            matchScoresPdLog.setOperateMatchId(data.getStandardMatchInfo().getMatchManageId());
            matchScoresPdLog.setOperateMatchName(getMatchSportTeamNameCode(data.getStandardMatchInfo().getId()));
            String periodId = Integer.toString(round);
            String paramName = "第 " + periodId+ " 轮 " + penaltyFirstDto.getHomeAway() + "先罚";
            matchScoresPdLog.setOperateParaName(paramName);
            matchScoresPdLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateRearText(CategoryUtils.SPLIT_LINE);
            if (data.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || data.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(penaltyFirstDto.getOperatorName() + " (" + data.getThirdMatchInfo().getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(penaltyFirstDto.getOperatorName());
            }
            matchScoresPdLog.setMatchManageId(data.getStandardMatchInfo().getMatchManageId());
            matchScoresPdLog.setIpAddress(penaltyFirstDto.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);
        } catch (Exception e) {
            log.error("[MatchScorePdLogServiceImpl] changePenaltyFirstLog thirdMatchId:{} penalty first: {}, error: ",
                    penaltyFirstDto.getThirdMatchId(), penaltyFirstDto.getHomeAway(), e);
        }
    }

    @Override
    public void takePenaltyLog(MatchScoreAndTimeVo data, TakePenaltyDTO takePenaltyDto) {
        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            String extraScores = data.getMatchScoresInfo().getScoresJsonExtra();
            FootballPenaltyScores footballPenaltyScores = null;
            if (StringUtils.isEmpty(extraScores)) {
                footballPenaltyScores = new FootballPenaltyScores();
            } else {
                footballPenaltyScores = JSONObject.toJavaObject((JSONObject.parseObject(extraScores)), FootballPenaltyScores.class);
            }
            Map<String, CommonItem> penaltyRound = footballPenaltyScores.getRoundScores();
            int round = penaltyRound.size();
            matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100500.getCode().toString());
            matchScoresPdLog.setOperateId(Integer.toString(round));
            matchScoresPdLog.setOperateName(OperateLogTypeEnum.SCORES_PD_100500.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100500.getValue());
            matchScoresPdLog.setOperateMatchId(data.getStandardMatchInfo().getMatchManageId());
            matchScoresPdLog.setOperateMatchName(getMatchSportTeamNameCode(data.getStandardMatchInfo().getId()));
            String periodId = Integer.toString(round);
            String directions = PDEventCodeEnum.getEventCodeEnum(takePenaltyDto.getEventCode()).getEventName();
            String paramName = "第 " + periodId+ " 轮 " + takePenaltyDto.getHomeAway() + directions;
            matchScoresPdLog.setOperateParaName(paramName);
            matchScoresPdLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateRearText(CategoryUtils.SPLIT_LINE);
            if (data.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || data.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(takePenaltyDto.getOperatorName() + " (" + data.getThirdMatchInfo().getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(takePenaltyDto.getOperatorName());
            }
            matchScoresPdLog.setMatchManageId(data.getStandardMatchInfo().getMatchManageId());
            matchScoresPdLog.setIpAddress(takePenaltyDto.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);
        } catch (Exception e) {
            log.error("::{}::[MatchScorePdLogServiceImpl] takePenaltyLog thirdMatchId:{} take_penalty error: ",
                    takePenaltyDto.getLinkedId(), takePenaltyDto.getThirdMatchId(), e);
        }
    }

    @Override
    public void penaltyAboutToBeTakenLog(MatchScoreAndTimeVo data, PenaltyAboutToBeTakenDto penaltyAboutToBeTakenDTO) {
        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            String extraScores = data.getMatchScoresInfo().getScoresJsonExtra();
            FootballPenaltyScores footballPenaltyScores = null;
            if (StringUtils.isEmpty(extraScores)) {
                footballPenaltyScores = new FootballPenaltyScores();
            } else {
                footballPenaltyScores = JSONObject.toJavaObject((JSONObject.parseObject(extraScores)), FootballPenaltyScores.class);
            }
            Map<String, CommonItem> penaltyRound = footballPenaltyScores.getRoundScores();
            int round = penaltyRound.size();
            matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100500.getCode().toString());
            matchScoresPdLog.setOperateId(Integer.toString(round));
            matchScoresPdLog.setOperateName(OperateLogTypeEnum.SCORES_PD_100500.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100500.getValue());
            matchScoresPdLog.setOperateMatchId(data.getStandardMatchInfo().getMatchManageId());
            matchScoresPdLog.setOperateMatchName(getMatchSportTeamNameCode(data.getStandardMatchInfo().getId()));
            String periodId = Integer.toString(round);
            String directions = PDEventCodeEnum.getEventCodeEnum(penaltyAboutToBeTakenDTO.getEventCode()).getEventName();
            String paramName = "第 " + periodId+ " 轮 " + penaltyAboutToBeTakenDTO.getHomeAway() + directions;
            matchScoresPdLog.setOperateParaName(paramName);
            matchScoresPdLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateRearText(CategoryUtils.SPLIT_LINE);
            if (data.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || data.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(penaltyAboutToBeTakenDTO.getOperatorName() + " (" + data.getThirdMatchInfo().getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(penaltyAboutToBeTakenDTO.getOperatorName());
            }
            matchScoresPdLog.setMatchManageId(data.getStandardMatchInfo().getMatchManageId());
            matchScoresPdLog.setIpAddress(penaltyAboutToBeTakenDTO.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);
        } catch (Exception e) {
            log.error("::{}::[MatchScorePdLogServiceImpl] penaltyAboutToBeTakenLog thirdMatchId:{} penalty_about_to_be_taken error: ",
                    penaltyAboutToBeTakenDTO.getLinkedId(), penaltyAboutToBeTakenDTO.getThirdMatchId(), e);
        }
    }

    /**
     * 修改赛制时间
     * @param matchScoreAndTimeVo
     * @param changeMatchLengthDto
     */
    @Override
    public void changeMatchLenthLog(MatchScoreAndTimeVo matchScoreAndTimeVo, ChangeMatchLengthDto changeMatchLengthDto) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            StandardMatchInfo standardMatchInfo = matchScoreAndTimeVo.getStandardMatchInfo();
            matchScoresPdLog.setOperateId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100101.getCode().toString());
            matchScoresPdLog.setOperateName(getMatchSportTeamNameCode(matchScoreAndTimeVo.getThirdMatchInfo().getReferenceId()));
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100134.getCode().toString());
            matchScoresPdLog.setOperateMatchId(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateMatchName(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateParaName(CategoryUtils.SPLIT_LINE);
            if (matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(changeMatchLengthDto.getOperatorName() + " (" + matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(changeMatchLengthDto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(changeMatchLengthDto.getIpAddress());

            int length = matchScoreAndTimeVo.getStandardMatchInfo().getMatchLength();
            if(length==0){
                matchScoresPdLog.setOperateForwText("10m");
            } else if (length==7) {
                matchScoresPdLog.setOperateForwText("12m");
            }else if (length==17) {
                matchScoresPdLog.setOperateForwText("20m");
            }else if (length==73) {
                matchScoresPdLog.setOperateForwText("3x3");
            }
            matchScoresPdLog.setOperateRearText(changeMatchLengthDto.getMinutes() + "m");
            if(changeMatchLengthDto.getMinutes()==9){
                matchScoresPdLog.setOperateRearText("3x3");
            }

            matchScoresPdLog.setOperateRearText(changeMatchLengthDto.getMinutes() + "m");
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);//操作时间
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);
        } catch (Exception e) {

            log.error("changeMatchLenthLog,标准赛事ID:{} , error:{}", matchScoreAndTimeVo.getThirdMatchInfo().getReferenceId(), e);
        }
    }

    /**
     * 冰球大、小罚比分记录
     * @param matchScoreAndTimeVo
     * @param scoresJson
     * @param editFaScoreDto
     */
    @Override
    public void editFaScoreLog(MatchScoreAndTimeVo matchScoreAndTimeVo, String scoresJson, EditFaScoreDto editFaScoreDto) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            StandardMatchInfo standardMatchInfo = matchScoreAndTimeVo.getStandardMatchInfo();
            if (standardMatchInfo.getSportId() == 4L) {
                matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100102.getCode().toString());
            } else {
                return;
            }
            Map<Long, IceHockeyScores> allPeriodScores = scoreUtils.periodJson(scoresJson, IceHockeyScores.class);
            IceHockeyScores wholeSores = allPeriodScores.get(WHOLE_MATCH);
            if (wholeSores == null) {
                return;
            }
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateName(getMatchSportTeamNameCode(standardMatchInfo.getId()));
            matchScoresPdLog.setOperateMatchId(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateMatchName(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
            if (matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(editFaScoreDto.getOperatorName() + " (" + matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(editFaScoreDto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(editFaScoreDto.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            String homeWay = null;
            if (editFaScoreDto.getType() == 0) {
                //大罚
                matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100137.getCode().toString());
                if (!editFaScoreDto.getBigFaT1().equals(wholeSores.getSuspensionBig().getHome())) {
                    homeWay = CategoryUtils.HOME_PARAM;
                    matchScoresPdLog.setOperateParaName(homeWay + CategoryUtils.SPLIT_LINE + getPeriodNameByPeriodId(standardMatchInfo.getSportId(), standardMatchInfo.getMatchPeriodId()));
                    if (editFaScoreDto.getBigFaT1() > wholeSores.getSuspensionBig().getHome()) {
                        matchScoresPdLog.setOperateRearText(String.valueOf(editFaScoreDto.getBigFaT1() - wholeSores.getSuspensionBig().getHome()));
                    } else {
                        matchScoresPdLog.setOperateRearText("-" + (wholeSores.getSuspensionBig().getHome() - editFaScoreDto.getBigFaT1()));
                    }
                    matchScoresPdLogMapper.insert(matchScoresPdLog);
                }
                if (!editFaScoreDto.getBigFaT2().equals(wholeSores.getSuspensionBig().getAway())) {
                    homeWay = CategoryUtils.AWAY_PARAM;
                    matchScoresPdLog.setOperateParaName(homeWay + CategoryUtils.SPLIT_LINE + getPeriodNameByPeriodId(standardMatchInfo.getSportId(), standardMatchInfo.getMatchPeriodId()));
                    if (editFaScoreDto.getBigFaT2() > wholeSores.getSuspensionBig().getAway()) {
                        matchScoresPdLog.setOperateRearText(String.valueOf(editFaScoreDto.getBigFaT2() - wholeSores.getSuspensionBig().getAway()));
                    } else {
                        matchScoresPdLog.setOperateRearText("-" + (wholeSores.getSuspensionBig().getAway() - editFaScoreDto.getBigFaT2()));
                    }
                    matchScoresPdLogMapper.insert(matchScoresPdLog);
                }
            } else {
                //小罚
                matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100138.getCode().toString());
                if (!editFaScoreDto.getSmallFaT1().equals(wholeSores.getSuspensionSmall().getHome())) {
                    homeWay = CategoryUtils.HOME_PARAM;
                    matchScoresPdLog.setOperateParaName(homeWay + CategoryUtils.SPLIT_LINE + getPeriodNameByPeriodId(standardMatchInfo.getSportId(), standardMatchInfo.getMatchPeriodId()));
                    if (editFaScoreDto.getSmallFaT1() > wholeSores.getSuspensionSmall().getHome()) {
                        matchScoresPdLog.setOperateRearText(String.valueOf(editFaScoreDto.getSmallFaT1() - wholeSores.getSuspensionSmall().getHome()));
                    } else {
                        matchScoresPdLog.setOperateRearText("-" + (wholeSores.getSuspensionSmall().getHome() - editFaScoreDto.getSmallFaT1()));
                    }
                    matchScoresPdLogMapper.insert(matchScoresPdLog);
                }
                if (!editFaScoreDto.getSmallFaT2().equals(wholeSores.getSuspensionSmall().getAway())) {
                    homeWay = CategoryUtils.AWAY_PARAM;
                    matchScoresPdLog.setOperateParaName(homeWay + CategoryUtils.SPLIT_LINE + getPeriodNameByPeriodId(standardMatchInfo.getSportId(), standardMatchInfo.getMatchPeriodId()));
                    if (editFaScoreDto.getSmallFaT2() > wholeSores.getSuspensionSmall().getAway()) {
                        matchScoresPdLog.setOperateRearText(String.valueOf(editFaScoreDto.getSmallFaT2() - wholeSores.getSuspensionSmall().getAway()));
                    } else {
                        matchScoresPdLog.setOperateRearText("-" + (wholeSores.getSuspensionSmall().getAway() - editFaScoreDto.getSmallFaT2()));
                    }
                    matchScoresPdLogMapper.insert(matchScoresPdLog);
                }
            }
        } catch (Exception e) {

            log.error("editFaScoreLog,三方赛事ID:{} , error:{}", matchScoreAndTimeVo.getMatchTimeInfo().getThirdMatchId(), e);
        }

    }

    /**
     * 网球球权日志
     * @param data
     * @param dto
     */
    @Override
    public void setMatchOpenBallLog(MatchScoreAndTimeVo data, TennisEditSecondScoreDto dto) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        StandardMatchInfo standardMatchInfo = null;
        try {
            standardMatchInfo = data.getStandardMatchInfo();
            if (standardMatchInfo == null) {
                return;
            }
            matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_10048.getCode().toString());
            matchScoresPdLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateMatchName(standardMatchInfo.getHomeAwayInfo());
            matchScoresPdLog.setOperateName(OperateLogTypeEnum.SCORES_PD_10060.getCode().toString());
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10060.getCode().toString());
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_10060.getCode().toString());
            if (dto.getHomeAway().equals("home")){
                matchScoresPdLog.setRemark(dto.getCurrentSet() + "-" + dto.getCurrentRound() + "-" + OperateLogTypeEnum.SCORES_PD_100146.getCode().toString());
            }else{
                matchScoresPdLog.setRemark(dto.getCurrentSet() + "-" + dto.getCurrentRound() + "-" + OperateLogTypeEnum.SCORES_PD_100147.getCode().toString());
            }
            matchScoresPdLog.setOperateForwText("-");
            matchScoresPdLog.setOperateRearText("-");
            if (data.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || data.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(dto.getOperatorName() + " (" + data.getThirdMatchInfo().getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(dto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(dto.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);

        } catch (Exception e) {

            log.error("setMatchOpenBallLog,标准赛事ID:{} , error:{}", standardMatchInfo.getId(), e);
        }
    }



    /**
     * 滚球中途切PD,参与结算的开关更新
     * @param updateSettleStatusDto
     */
    @Override
    public void updateSettleStatusLog(UpdateSettleStatusDto updateSettleStatusDto) {

        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        StandardMatchInfo standardMatchInfo = null;
        try {
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoMapper.selectByPrimaryKey(updateSettleStatusDto.getThirdMatchId());
            if(thirdMatchInfo==null){
                return;
            }
            if(thirdMatchInfo.getReferenceId()!=null&&thirdMatchInfo.getReferenceId()!=0L){
                standardMatchInfo =standardMatchInfoMapper.selectByPrimaryKey(thirdMatchInfo.getReferenceId());
            }
            matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100071.getCode().toString());
            matchScoresPdLog.setOperateId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateName(getMatchSportTeamNameCode(standardMatchInfo.getId()));
            matchScoresPdLog.setOperateMatchId(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateMatchName(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateParaName(CategoryUtils.SPLIT_LINE);
            if (updateSettleStatusDto.getSettleStatus()!=null && updateSettleStatusDto.getSettleStatus().equals(SportTypeEnum.FOOTBALL.getValue().intValue())){
                matchScoresPdLog.setOperateForwText(CategoryUtils.OFF);
                matchScoresPdLog.setOperateRearText(CategoryUtils.ON);
            }else{
                matchScoresPdLog.setOperateForwText(CategoryUtils.ON);
                matchScoresPdLog.setOperateRearText(CategoryUtils.OFF);
            }
            matchScoresPdLog.setIpAddress(updateSettleStatusDto.getIpAddress());
            matchScoresPdLog.setOperateUserName(updateSettleStatusDto.getOperatorName());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);
        } catch (Exception e) {

            log.error("updateSettleStatusLog, error:{}", JSONObject.toJSONString(updateSettleStatusDto), e);
        }
    }

    @Override
    public void modifyCancelMatchEndLog(Integer secondFromStart, Long periodId, String userName, String address, String userId, MatchScoreAndTimeVo data) {
        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        StandardMatchInfo standardMatchInfo = null;
        try {
            ThirdMatchInfo thirdMatchInfo = data.getThirdMatchInfo();
            if (thirdMatchInfo == null) {
                return;
            }
            if (thirdMatchInfo.getReferenceId() != null && thirdMatchInfo.getReferenceId() != 0L) {
                standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(thirdMatchInfo.getReferenceId());
            }
            matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100072.getCode().toString());
            matchScoresPdLog.setOperateId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateName(getMatchSportTeamNameCode(standardMatchInfo.getId()));
            matchScoresPdLog.setOperateMatchId(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateMatchName(CategoryUtils.SPLIT_LINE);

            if (data.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || data.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                matchScoresPdLog.setOperateUserName(userName + " (" + data.getThirdMatchInfo().getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(userName);
            }
            //比赛拉回-取消结束
            matchScoresPdLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_100074.getName()+ CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100074.getValue());
            //操作前 比赛结束
            matchScoresPdLog.setOperateForwText(getPeriodNameByPeriodId(standardMatchInfo.getSportId(), 999L));
            //操作后 取消结束
            matchScoresPdLog.setOperateRearText(getPeriodNameByPeriodId(standardMatchInfo.getSportId(), periodId)+ CategoryUtils.SPLIT_LINE + secondFromStart);


            matchScoresPdLog.setIpAddress(address);
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);
        }catch (Exception e){
            log.error("{}",e.getMessage());
        }
    }

    @Override
    public void modifyEditAllScoreLog(AllFootballScoreEditDto allFootballScoreEditDto, MatchScoreAndTimeVo data) {
        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        StandardMatchInfo standardMatchInfo = null;
        try {
            ThirdMatchInfo thirdMatchInfo = data.getThirdMatchInfo();
            if (thirdMatchInfo == null) {
                return;
            }
            if (thirdMatchInfo.getReferenceId() != null && thirdMatchInfo.getReferenceId() != 0L) {
                standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(thirdMatchInfo.getReferenceId());
            }
            if (standardMatchInfo == null) {
                return;
            }
            matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100073.getCode().toString());
            matchScoresPdLog.setOperateId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateName(getMatchSportTeamNameCode(standardMatchInfo.getId()));
            matchScoresPdLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateMatchName(getMatchSportTeamNameCode(thirdMatchInfo.getReferenceId()));
            matchScoresPdLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_100073.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100073.getValue());

            matchScoresPdLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_100073.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100073.getValue());
            matchScoresPdLog.setIpAddress(allFootballScoreEditDto.getIpAddress());
            matchScoresPdLog.setOperateUserName(allFootballScoreEditDto.getOperatorName());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);
        }catch (Exception e){
            log.error("{}",e.getMessage());
        }
    }

    @Override
    public void modifyEditAllScoreLog(AllFootballScoreEditDto allFootballScoreEditDto, MatchScoreAndTimeVo data,Map<Long, FootballScores> oldScores,Map<Long, FootballScores> newScores) {

        StandardMatchInfo standardMatchInfo = null;
        try {
            ThirdMatchInfo thirdMatchInfo = data.getThirdMatchInfo();
            if (thirdMatchInfo == null) {
                return;
            }
            if (thirdMatchInfo.getReferenceId() != null && thirdMatchInfo.getReferenceId() != 0L) {
                standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(thirdMatchInfo.getReferenceId());
            }
            if (standardMatchInfo == null) {
                return;
            }

            List<MatchScoresPdLog> list = new ArrayList<>();
            list =  setOperateText(list,standardMatchInfo,oldScores,newScores,thirdMatchInfo,allFootballScoreEditDto,data);
            if(!list.isEmpty()){
                for (MatchScoresPdLog pdLogs:list){
                    matchScoresPdLogMapper.insert(pdLogs);
                }
            }else{
                log.info("::{}::modifyEditAllScoreLog无比分编辑,不保存操作日志 ",allFootballScoreEditDto.getLinkedId());
            }

        }catch (Exception e){
            log.error("{}",e.getMessage());
        }
    }

    @Override
    public void injuryTimeEventLog(MatchScoreAndTimeVo data, InjuryTimeEventDto dto) {
        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            ThirdMatchInfo matchInfo = data.getThirdMatchInfo();
            if (ObjectUtil.isNotEmpty(matchInfo) && matchInfo.getSportId().intValue() == 1) {
                matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
            }
            StandardMatchInfo standardMatchInfo = data.getStandardMatchInfo();
            matchScoresPdLog.setOperateId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateName(getPeriodNameByPeriodId(standardMatchInfo.getSportId(), standardMatchInfo.getMatchPeriodId()));
            matchScoresPdLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateMatchName(getMatchSportTeamNameCode(standardMatchInfo.getId()));
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10065.getCode().toString());
            getOldTimeout(data, matchScoresPdLog);
            matchScoresPdLog.setOperateRearText(String.valueOf(dto.getTimeOut()));
            matchScoresPdLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_100086.getName()
                    + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100086.getValue());
            boolean flag = DataSourceCodeEnum.PD.getCode().equals(matchInfo.getDataSourceCode())
                    || DataSourceCodeEnum.PD2.getCode().equals(matchInfo.getDataSourceCode());
            if (flag) {
                matchScoresPdLog.setOperateUserName(dto.getOperatorName() + "(" + matchInfo.getDataSourceCode() + ")");
            } else {
                matchScoresPdLog.setOperateUserName(dto.getOperatorName());
            }
            matchScoresPdLog.setIpAddress(dto.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);
        } catch (Exception e) {
            log.error("injuryTimeEventLog,标准赛事ID:{} , error:{}", data.getStandardMatchInfo().getId(), e);
        }
    }

    /**
     * 获取操作前时间
     *
     * @param data             赛事相关数据
     * @param matchScoresPdLog 赛事比分日志记录
     */
    private void getOldTimeout(MatchScoreAndTimeVo data, MatchScoresPdLog matchScoresPdLog) {
        MatchTimeInfo matchTimeInfo = data.getMatchTimeInfo();
        if (ObjectUtil.isNotEmpty(matchTimeInfo)) {
            String timeOutList = matchTimeInfo.getTimeOutList();
            Long period = matchTimeInfo.getPeriod();
            if (StrUtil.isNotEmpty(timeOutList)) {
                Set<Map<String, Long>> set = JSONObject.parseObject(timeOutList, new TypeReference<Set<Map<String, Long>>>() {
                });
                if (CollectionUtil.isNotEmpty(set)) {
                    Set<Object> setEle = new HashSet<>();
                    for (Map<String, Long> element : set) {
                        setEle.add(element.get("period"));
                    }
                    if (setEle.contains(period)) {
                        for (Map<String, Long> strMap : set) {
                            if (Objects.equals(strMap.get("period"), period)) {
                                matchScoresPdLog.setOperateForwText(strMap.get("timeOut") + "");
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void timeStatusEventLog(MatchScoreAndTimeVo data, TimeStatusEventDto dto) {
        MatchScoresPdLog matchLog = new MatchScoresPdLog();
        try {
            ThirdMatchInfo thirdMatchInfo = data.getThirdMatchInfo();
            if (ObjectUtil.isNotEmpty(thirdMatchInfo) && thirdMatchInfo.getSportId().intValue() == 1) {
                matchLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
            }
            StandardMatchInfo standardMatchInfo = data.getStandardMatchInfo();
            matchLog.setOperateId(standardMatchInfo.getMatchManageId());
            matchLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            matchLog.setOperateName(getPeriodNameByPeriodId(standardMatchInfo.getSportId(), standardMatchInfo.getMatchPeriodId()));
            matchLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
            matchLog.setOperateMatchName(getMatchSportTeamNameCode(standardMatchInfo.getId()));
            matchLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10065.getCode().toString());
            if (dto.getTimeGo() == 0) {
                matchLog.setOperateForwText(OperateLogTypeEnum.SCORES_PD_100128.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100128.getValue());
                matchLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_100127.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100127.getValue());
            }
            if (dto.getTimeGo() == 1) {
                matchLog.setOperateForwText(OperateLogTypeEnum.SCORES_PD_100127.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100127.getValue());
                matchLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_100128.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100128.getValue());
            }
            matchLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_100087.getName()
                    + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100087.getValue());
            boolean flag = DataSourceCodeEnum.PD.getCode().equals(thirdMatchInfo.getDataSourceCode())
                    || DataSourceCodeEnum.PD2.getCode().equals(thirdMatchInfo.getDataSourceCode());
            if (flag) {
                matchLog.setOperateUserName(dto.getOperatorName() + "(" + thirdMatchInfo.getDataSourceCode() + ")");
            } else {
                matchLog.setOperateUserName(dto.getOperatorName());
            }
            matchLog.setIpAddress(dto.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchLog.setCreateTime(time);
            matchLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchLog);
        } catch (Exception e) {
            log.error("timeStatusEventLog,标准赛事ID:{} , error:{}", data.getStandardMatchInfo().getId(), e);
        }
    }

    @Override
    public void sendEventLog(BasketballScoresPDDto oldScore, BasketballScoresPDDto newScore, Response<MatchScoreAndTimeVo> response, PDBasketBallSendEventDto sendEventDto) {
        String homeAwayName;
        if (sendEventDto.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
            homeAwayName = CategoryUtils.HOME_PARAM;
        } else {
            homeAwayName = CategoryUtils.AWAY_PARAM;
        }
        MatchScoresPdLog matchLog = new MatchScoresPdLog();
        try {
            ThirdMatchInfo thirdMatchInfo = response.getData().getThirdMatchInfo();
            if (ObjectUtil.isNotEmpty(thirdMatchInfo) && thirdMatchInfo.getSportId().intValue() == 2) {
                matchLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100101.getCode().toString());
            }
            // 根据事件编码返回前端事件状态
            String eventCode = MatchEventUtils.getEventCodeByType(sendEventDto.getEventType());
            MatchScoresInfo matchScoresInfo = scoresService.selectLiveMatchScoreInfo(sendEventDto.getThirdMatchId());
            if (matchScoresInfo != null) {
                BasketballScores periodScore = basketBallScoreService.getPeriodScore(response);
                // 助攻
                if ("assist".equals(eventCode)) {
                    CommonItem oldAssist = oldScore.getAssist();
                    CommonItem newAssist = newScore.getAssist();
                    if (ObjectUtil.isEmpty(oldAssist)) {
                        matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                    } else {
                        matchLog.setOperateForwText(oldAssist.getHome() + CategoryUtils.SPLIT_LINE + oldAssist.getAway());
                    }
                    matchLog.setOperateRearText(newAssist.getHome() + CategoryUtils.SPLIT_LINE + newAssist.getAway());
                    matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2001.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2001.getValue());
                }
                // 失误
                if ("turnover".equals(eventCode)) {
                    CommonItem oldTurnover = oldScore.getTurnover();
                    CommonItem newTurnover = newScore.getTurnover();
                    if (ObjectUtil.isEmpty(oldTurnover)) {
                        matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                    } else {
                        matchLog.setOperateForwText(oldTurnover.getHome() + CategoryUtils.SPLIT_LINE + oldTurnover.getAway());
                    }
                    matchLog.setOperateRearText(newTurnover.getHome() + CategoryUtils.SPLIT_LINE + newTurnover.getAway());
                    matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2002.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2002.getValue());
                }
                // 抢断
                if ("steal".equals(eventCode)) {
                    CommonItem oldSteal = oldScore.getSteal();
                    CommonItem newSteal = newScore.getSteal();
                    if (ObjectUtil.isEmpty(oldSteal)) {
                        matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                    } else {
                        matchLog.setOperateForwText(oldSteal.getHome() + CategoryUtils.SPLIT_LINE + oldSteal.getAway());
                    }
                    matchLog.setOperateRearText(newSteal.getHome() + CategoryUtils.SPLIT_LINE + newSteal.getAway());
                    matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2003.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2003.getValue());
                }
                // 盖帽
                if ("block".equals(eventCode)) {
                    CommonItem oldBlock = oldScore.getBlock();
                    CommonItem newBlock = newScore.getBlock();
                    if (ObjectUtil.isEmpty(oldBlock)) {
                        matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                    } else {
                        matchLog.setOperateForwText(oldBlock.getHome() + CategoryUtils.SPLIT_LINE + oldBlock.getAway());
                    }
                    matchLog.setOperateRearText(newBlock.getHome() + CategoryUtils.SPLIT_LINE + newBlock.getAway());
                    matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2004.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2004.getValue());
                }
                // 犯规
                if ("foul".equals(eventCode)) {
                    CommonItem oldFoul = oldScore.getFoul();
                    CommonItem newFoul = newScore.getFoul();
                    if (ObjectUtil.isEmpty(oldFoul)) {
                        matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                    } else {
                        matchLog.setOperateForwText(oldFoul.getHome() + CategoryUtils.SPLIT_LINE + oldFoul.getAway());
                    }
                    matchLog.setOperateRearText(newFoul.getHome() + CategoryUtils.SPLIT_LINE + newFoul.getAway());
                    matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2005.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2005.getValue());
                }
                // 进攻篮板
                if ("6".equals(sendEventDto.getEventType())) {
                    CommonItem oldReboundAttack = oldScore.getReboundAttack();
                    CommonItem newReboundAttack = newScore.getReboundAttack();
                    if (ObjectUtil.isEmpty(oldReboundAttack)) {
                        matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                    } else {
                        matchLog.setOperateForwText(oldReboundAttack.getHome() + CategoryUtils.SPLIT_LINE + oldReboundAttack.getAway());
                    }
                    matchLog.setOperateRearText(newReboundAttack.getHome() + CategoryUtils.SPLIT_LINE + newReboundAttack.getAway());
                    matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2006.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2006.getValue());
                }
                // 防守篮板
                if ("7".equals(sendEventDto.getEventType())) {
                    CommonItem oldReboundDefense = oldScore.getReboundDefense();
                    CommonItem newReboundDefense = newScore.getReboundDefense();
                    if (ObjectUtil.isEmpty(oldReboundDefense)) {
                        matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                    } else {
                        matchLog.setOperateForwText(oldReboundDefense.getHome() + CategoryUtils.SPLIT_LINE + oldReboundDefense.getAway());
                    }
                    matchLog.setOperateRearText(newReboundDefense.getHome() + CategoryUtils.SPLIT_LINE + newReboundDefense.getAway());
                    matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2007.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2007.getValue());
                }
                // 控球权
                if ("8".equals(sendEventDto.getEventType())) {
                    CommonItem oldPossession = oldScore.getPossession();
                    CommonItem newPossession = newScore.getPossession();
                    if (ObjectUtil.isEmpty(oldPossession)) {
                        matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                    } else {
                        matchLog.setOperateForwText(oldPossession.getHome() + CategoryUtils.SPLIT_LINE + oldPossession.getAway());
                    }
                    matchLog.setOperateRearText(newPossession.getHome() + CategoryUtils.SPLIT_LINE + newPossession.getAway());
                    matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2008.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2008.getValue());
                }

                StandardMatchInfo standardMatchInfo = response.getData().getStandardMatchInfo();
                if (standardMatchInfo == null) {
                    return;
                }
                matchLog.setOperateId(standardMatchInfo.getMatchManageId());
                matchLog.setMatchManageId(standardMatchInfo.getMatchManageId());
                matchLog.setOperateName(getPeriodNameByPeriodId(standardMatchInfo.getSportId(), standardMatchInfo.getMatchPeriodId()));
                matchLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
                matchLog.setOperateMatchName(getMatchSportTeamNameCode(standardMatchInfo.getId()));
                matchLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10065.getCode().toString());
                matchLog.setOperateUserName(sendEventDto.getOperatorName());
                if (thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                    matchLog.setOperateUserName(sendEventDto.getOperatorName() + " (" + thirdMatchInfo.getDataSourceCode() + ")");
                } else {
                    matchLog.setOperateUserName(sendEventDto.getOperatorName());
                }
                matchLog.setIpAddress(sendEventDto.getIpAddress());
                long time = System.currentTimeMillis();
                matchLog.setCreateTime(time);
                matchLog.setModifyTime(time);
                matchScoresPdLogMapper.insert(matchLog);
            }
        } catch (Exception e) {
            log.error("MatchScorePdLogServiceImpl::sendEventLog, error: " + e);
        }
    }

    @Override
    public void changeJumpWonScoreLog(Response<MatchScoreAndTimeVo> response, PDBaskectBallMatchStartDto dto) {
        String homeAwayName;
        if (dto.getJumpWonHomeAway().equals(TeamTypeEnum.HOME.code)) {
            homeAwayName = CategoryUtils.HOME_PARAM;
        } else {
            homeAwayName = CategoryUtils.AWAY_PARAM;
        }
        MatchScoresPdLog matchLog = new MatchScoresPdLog();
        try {
            ThirdMatchInfo thirdMatchInfo = response.getData().getThirdMatchInfo();
            if (ObjectUtil.isNotEmpty(thirdMatchInfo) && thirdMatchInfo.getSportId().intValue() == 2) {
                matchLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100101.getCode().toString());
            }
            MatchScoresInfo matchScoresInfo = scoresService.selectLiveMatchScoreInfo(dto.getThirdMatchId());
            if (matchScoresInfo != null) {
                StandardMatchInfo standardMatchInfo = response.getData().getStandardMatchInfo();
                if (standardMatchInfo == null) {
                    return;
                }
                if (1 == dto.getIsJump()) {
                    matchLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_100126.getName()
                            + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100126.getValue());//开始
                    matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2040.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2040.getValue());
                }
                if (0 == dto.getIsJump()) {
                    matchLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_100126.getName()
                            + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100126.getValue());//开始
                    matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2041.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2041.getValue());
                }
                matchLog.setOperateId(standardMatchInfo.getMatchManageId());
                matchLog.setMatchManageId(standardMatchInfo.getMatchManageId());
                matchLog.setOperateName(getPeriodNameByPeriodId(standardMatchInfo.getSportId(), matchScoresInfo.getPeriod()));
                matchLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
                matchLog.setOperateMatchName(getMatchSportTeamNameCode(standardMatchInfo.getId()));
                matchLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10065.getCode().toString());
                matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                if (thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                    matchLog.setOperateUserName(dto.getOperatorName() + " (" + thirdMatchInfo.getDataSourceCode() + ")");
                } else {
                    matchLog.setOperateUserName(dto.getOperatorName());
                }
                matchLog.setIpAddress(dto.getIpAddress());
                long time = System.currentTimeMillis();
                matchLog.setCreateTime(time);
                matchLog.setModifyTime(time);
                matchScoresPdLogMapper.insert(matchLog);
            }
        } catch (Exception e) {
            log.error("MatchScorePdLogServiceImpl::changeJumpWonScoreLog, error: " + e);
        }
    }

    @Override
    public void sendBallLog(BasketballScores oldScore, BasketballScores newScore, Response<MatchScoreAndTimeVo> response, PDBasketBallSendBallDto sendBallDto) {
        MatchScoresPdLog matchLog = new MatchScoresPdLog();
        try {
            String homeAwayName;
            if (sendBallDto.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                homeAwayName = CategoryUtils.HOME_PARAM;
            } else {
                homeAwayName = CategoryUtils.AWAY_PARAM;
            }
            ThirdMatchInfo thirdMatchInfo = response.getData().getThirdMatchInfo();
            if (ObjectUtil.isNotEmpty(thirdMatchInfo) && thirdMatchInfo.getSportId().intValue() == 2) {
                matchLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100101.getCode().toString());
            } else {
                return;
            }
            MatchScoresInfo matchScoresInfo = scoresService.selectLiveMatchScoreInfo(sendBallDto.getThirdMatchId());
            if (matchScoresInfo != null) {
                StandardMatchInfo standardMatchInfo = response.getData().getStandardMatchInfo();
                if (standardMatchInfo == null) {
                    return;
                }

                CommonItem matchScore = oldScore.getMatchScore();
                CommonItem matchScoreUpdated = newScore.getMatchScore();
                if (1 == sendBallDto.getScore() && 1 == sendBallDto.getBallEventType()) {
                    if (ObjectUtil.isEmpty(matchScore)) {
                        matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                        matchLog.setOperateRearText(CategoryUtils.SPLIT_LINE);
                    } else {
                        matchLog.setOperateForwText(matchScore.getHome() + CategoryUtils.SPLIT_LINE + matchScore.getAway());
                        matchLog.setOperateRearText(matchScoreUpdated.getHome() + CategoryUtils.SPLIT_LINE + matchScoreUpdated.getAway());
                    }
                    matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2031.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2031.getValue());
                }
                if (1 == sendBallDto.getScore() && 2 == sendBallDto.getBallEventType()) {
                    if (ObjectUtil.isEmpty(matchScore)) {
                        matchScore = new CommonItem();
                        matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                    } else {
                        matchLog.setOperateForwText(matchScore.getHome() + CategoryUtils.SPLIT_LINE + matchScore.getAway());
                    }
                    matchLog.setOperateRearText(matchScoreUpdated.getHome() + CategoryUtils.SPLIT_LINE + matchScoreUpdated.getAway());
                    matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2032.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2032.getValue());
                }
                if (1 == sendBallDto.getScore() && 3 == sendBallDto.getBallEventType()) {
                    if (ObjectUtil.isEmpty(matchScore)) {
                        matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                        matchLog.setOperateRearText(CategoryUtils.SPLIT_LINE);
                    } else {
                        matchLog.setOperateForwText(matchScore.getHome() + CategoryUtils.SPLIT_LINE + matchScore.getAway());
                        matchLog.setOperateRearText(matchScoreUpdated.getHome() + CategoryUtils.SPLIT_LINE + matchScoreUpdated.getAway());
                    }
                    matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2033.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2033.getValue());
                }
                if (2 == sendBallDto.getScore() && 1 == sendBallDto.getBallEventType()) {
                    if (ObjectUtil.isEmpty(matchScore)) {
                        matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                        matchLog.setOperateRearText(CategoryUtils.SPLIT_LINE);
                    } else {
                        matchLog.setOperateForwText(matchScore.getHome() + CategoryUtils.SPLIT_LINE + matchScore.getAway());
                        matchLog.setOperateRearText(matchScoreUpdated.getHome() + CategoryUtils.SPLIT_LINE + matchScoreUpdated.getAway());
                    }
                    matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2034.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2034.getValue());
                }
                if (2 == sendBallDto.getScore() && 2 == sendBallDto.getBallEventType()) {
                    if (ObjectUtil.isEmpty(matchScore)) {
                        matchScore = new CommonItem();
                        matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                    } else {
                        matchLog.setOperateForwText(matchScore.getHome() + CategoryUtils.SPLIT_LINE + matchScore.getAway());
                    }
                    matchLog.setOperateRearText(matchScoreUpdated.getHome() + CategoryUtils.SPLIT_LINE + matchScoreUpdated.getAway());
                    matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2035.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2035.getValue());
                }
                if (2 == sendBallDto.getScore() && 3 == sendBallDto.getBallEventType()) {
                    if (ObjectUtil.isEmpty(matchScore)) {
                        matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                        matchLog.setOperateRearText(CategoryUtils.SPLIT_LINE);
                    } else {
                        matchLog.setOperateForwText(matchScore.getHome() + CategoryUtils.SPLIT_LINE + matchScore.getAway());
                        matchLog.setOperateRearText(matchScoreUpdated.getHome() + CategoryUtils.SPLIT_LINE + matchScoreUpdated.getAway());
                    }
                    matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2036.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2036.getValue());
                }
                if (3 == sendBallDto.getScore() && 1 == sendBallDto.getBallEventType()) {
                    if (ObjectUtil.isEmpty(matchScore)) {
                        matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                        matchLog.setOperateRearText(CategoryUtils.SPLIT_LINE);
                    } else {
                        matchLog.setOperateForwText(matchScore.getHome() + CategoryUtils.SPLIT_LINE + matchScore.getAway());
                        matchLog.setOperateRearText(matchScoreUpdated.getHome() + CategoryUtils.SPLIT_LINE + matchScoreUpdated.getAway());
                    }
                    matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2037.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2037.getValue());
                }
                if (3 == sendBallDto.getScore() && 2 == sendBallDto.getBallEventType()) {
                    if (ObjectUtil.isEmpty(matchScore)) {
                        matchScore = new CommonItem();
                        matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                    } else {
                        matchLog.setOperateForwText(matchScore.getHome() + CategoryUtils.SPLIT_LINE + matchScore.getAway());
                    }
                    matchLog.setOperateRearText(matchScoreUpdated.getHome() + CategoryUtils.SPLIT_LINE + matchScoreUpdated.getAway());
                    matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2038.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2038.getValue());
                }
                if (3 == sendBallDto.getScore() && 3 == sendBallDto.getBallEventType()) {
                    if (ObjectUtil.isEmpty(matchScore)) {
                        matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                        matchLog.setOperateRearText(CategoryUtils.SPLIT_LINE);
                    } else {
                        matchLog.setOperateForwText(matchScore.getHome() + CategoryUtils.SPLIT_LINE + matchScore.getAway());
                        matchLog.setOperateRearText(matchScoreUpdated.getHome() + CategoryUtils.SPLIT_LINE + matchScoreUpdated.getAway());
                    }
                    matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2039.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2039.getValue());
                }

                matchLog.setOperateId(standardMatchInfo.getMatchManageId());
                matchLog.setMatchManageId(standardMatchInfo.getMatchManageId());
                matchLog.setOperateName(getPeriodNameByPeriodId(standardMatchInfo.getSportId(), standardMatchInfo.getMatchPeriodId()));
                matchLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
                matchLog.setOperateMatchName(getMatchSportTeamNameCode(standardMatchInfo.getId()));
                matchLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10065.getCode().toString());
                if (thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                    matchLog.setOperateUserName(sendBallDto.getOperatorName() + " (" + thirdMatchInfo.getDataSourceCode() + ")");
                } else {
                    matchLog.setOperateUserName(sendBallDto.getOperatorName());
                }
                matchLog.setIpAddress(sendBallDto.getIpAddress());
                long time = System.currentTimeMillis();
                matchLog.setCreateTime(time);
                matchLog.setModifyTime(time);
                matchScoresPdLogMapper.insert(matchLog);
            }
        } catch (Exception e) {
            log.error("MatchScorePdLogServiceImpl::sendBallLog, error: " + e);
        }
    }

    @Override
    public void sendFreeThrowLog(SetFreeThrowDto setFreeThrowDto, Response<MatchScoreAndTimeVo> response) {
        MatchScoresPdLog matchLog = new MatchScoresPdLog();
        try {
            String homeAwayName;
            if (TeamTypeEnum.HOME.code.equals(setFreeThrowDto.getHomeAway())) {
                homeAwayName = CategoryUtils.HOME_PARAM;
            } else {
                homeAwayName = CategoryUtils.AWAY_PARAM;
            }
            ThirdMatchInfo thirdMatchInfo = response.getData().getThirdMatchInfo();
            if (ObjectUtil.isNotEmpty(thirdMatchInfo) && thirdMatchInfo.getSportId().intValue() == 2) {
                matchLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100101.getCode().toString());
            } else {
                return;
            }
            MatchScoresInfo matchScoresInfo = scoresService.selectLiveMatchScoreInfo(setFreeThrowDto.getThirdMatchId());
            if (matchScoresInfo != null) {
                StandardMatchInfo standardMatchInfo = response.getData().getStandardMatchInfo();
                if (standardMatchInfo == null) {
                    return;
                }
                // 初始化罚球
                boolean flag = ObjectUtils.isEmpty(setFreeThrowDto.getOldFreeThrowNumber());
                // 取消罚球 cancel=true, 未取消罚球 cancel=false
                boolean cancel = setFreeThrowDto.isCancel();
                if (cancel) {
                    Integer freeThrowNumber = setFreeThrowDto.getFreeThrowNumber();
                    matchLog.setOperateForwText(freeThrowNumber + "");
                    matchLog.setOperateRearText(CategoryUtils.SPLIT_LINE);
                    switch (freeThrowNumber){
                        case 1:
                            matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_203114.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_203114.getValue());
                            break;
                        case 2:
                            matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_203115.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_203115.getValue());
                            break;
                        case 3:
                            matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_203116.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_203116.getValue());
                            break;
                    }
                } else {
                    if (flag) {
                        switch (setFreeThrowDto.getFreeThrowNumber()) {
                            case 1:
                                matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                                matchLog.setOperateRearText(setFreeThrowDto.getFreeThrowNumber() + "");
                                matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_203111.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_203111.getValue());
                                break;
                            case 2:
                                matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                                matchLog.setOperateRearText(setFreeThrowDto.getFreeThrowNumber() + "");
                                matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_203112.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_203112.getValue());
                                break;
                            case 3:
                                matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                                matchLog.setOperateRearText(setFreeThrowDto.getFreeThrowNumber() + "");
                                matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_203113.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_203113.getValue());
                                break;
                        }
                    }
                    // 罚球增加
                    if (!flag && setFreeThrowDto.getOldFreeThrowNumber() < setFreeThrowDto.getFreeThrowNumber()) {
                        matchLog.setOperateForwText(setFreeThrowDto.getOldFreeThrowNumber() + "");
                        matchLog.setOperateRearText(setFreeThrowDto.getFreeThrowNumber() + "");
                        matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_20312.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_20312.getValue());
                    }
                    // 罚球减少
                    if (!flag && setFreeThrowDto.getOldFreeThrowNumber() > setFreeThrowDto.getFreeThrowNumber()) {
                        matchLog.setOperateForwText(setFreeThrowDto.getOldFreeThrowNumber() + "");
                        matchLog.setOperateRearText(setFreeThrowDto.getFreeThrowNumber() + "");
                        matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE + OperateLogTypeEnum.SCORES_PD_20313.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_20313.getValue());
                    }
                }
                matchLog.setOperateId(standardMatchInfo.getMatchManageId());
                matchLog.setMatchManageId(standardMatchInfo.getMatchManageId());
                matchLog.setOperateName(getPeriodNameByPeriodId(standardMatchInfo.getSportId(), standardMatchInfo.getMatchPeriodId()));
                matchLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
                matchLog.setOperateMatchName(getMatchSportTeamNameCode(standardMatchInfo.getId()));
                matchLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10065.getCode().toString());
                if (thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())) {
                    matchLog.setOperateUserName(setFreeThrowDto.getOperatorName() + " (" + thirdMatchInfo.getDataSourceCode() + ")");
                } else {
                    matchLog.setOperateUserName(setFreeThrowDto.getOperatorName());
                }
                matchLog.setIpAddress(setFreeThrowDto.getIpAddress());
                long time = System.currentTimeMillis();
                matchLog.setCreateTime(time);
                matchLog.setModifyTime(time);
                matchScoresPdLogMapper.insert(matchLog);
            }
        } catch (Exception e) {
            log.error("MatchScorePdLogServiceImpl::sendFreeThrow, error: " + e);
        }
    }

    @Override
    public void sendBallLog(Response<MatchScoreAndTimeVo> responseBuffer, Response<MatchScoreAndTimeVo> response, PDBasketBallSendBallDto sendBallDto) {
        MatchScoresPdLog matchLog = new MatchScoresPdLog();
        try {
            String homeAwayName;
            if (sendBallDto.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                homeAwayName = CategoryUtils.HOME_PARAM;
            } else {
                homeAwayName = CategoryUtils.AWAY_PARAM;
            }
            ThirdMatchInfo thirdMatchInfo = responseBuffer.getData().getThirdMatchInfo();
            if (ObjectUtil.isNotEmpty(thirdMatchInfo) && thirdMatchInfo.getSportId().intValue() == 2) {
                matchLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100101.getCode().toString());
            } else {
                return;
            }
            MatchScoresInfo matchScoresInfo = scoresService.selectLiveMatchScoreInfo(sendBallDto.getThirdMatchId());
            if (matchScoresInfo != null) {
                JSONObject periodFootballScores = JSONObject.parseObject(responseBuffer.getData().getMatchScoresInfo().getScoresJson());
                Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodFootballScores);
                BasketballScores sendBallByScore = allPeriodScores.get(WHOLE_MATCH);
                StandardMatchInfo standardMatchInfo = responseBuffer.getData().getStandardMatchInfo();
                if (standardMatchInfo == null) {
                    return;
                }
                periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
                allPeriodScores= JsonMapUtils.parseBasketballMap(periodFootballScores);
                BasketballScores sendBallByScoreUpdated = allPeriodScores.get(WHOLE_MATCH);
                CommonItem matchScore = sendBallByScore.getMatchScore();
                CommonItem matchScoreUpdated = sendBallByScoreUpdated.getMatchScore();
                // 罚球并且是输入框输入
                boolean inputFlag = sendBallDto.isFreeThrow() && !sendBallDto.isInput();
                // 罚球并且不是输入框输入
                boolean clickFlag = sendBallDto.isFreeThrow() && sendBallDto.isInput();
                if(inputFlag){
                    if (ObjectUtil.isEmpty(matchScore)) {
                        matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                        matchLog.setOperateRearText(CategoryUtils.SPLIT_LINE);
                    } else {
                        matchLog.setOperateForwText(matchScore.getHome() + CategoryUtils.SPLIT_LINE + matchScore.getAway());
                        matchLog.setOperateRearText(matchScoreUpdated.getHome() + CategoryUtils.SPLIT_LINE + matchScoreUpdated.getAway());
                    }
                    matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_203201.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_203201.getValue());
                }
                // 非输入框，点击罚球未命中
                if (1 == sendBallDto.getScore() && 1 == sendBallDto.getBallEventType() && clickFlag) {
                    if (ObjectUtil.isEmpty(matchScore)) {
                        matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                        matchLog.setOperateRearText(CategoryUtils.SPLIT_LINE);
                    } else {
                        matchLog.setOperateForwText(matchScore.getHome() + CategoryUtils.SPLIT_LINE + matchScore.getAway());
                        matchLog.setOperateRearText(matchScoreUpdated.getHome() + CategoryUtils.SPLIT_LINE + matchScoreUpdated.getAway());
                    }
                    matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2031.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2031.getValue());
                }
                // 非输入框，点击罚球命中
                if (1 == sendBallDto.getScore() && 2 == sendBallDto.getBallEventType() && clickFlag) {
                    if (ObjectUtil.isEmpty(matchScore)) {
                        matchScore = new CommonItem();
                        matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                    } else {
                        matchLog.setOperateForwText(matchScore.getHome() + CategoryUtils.SPLIT_LINE + matchScore.getAway());
                    }
                    matchLog.setOperateRearText(matchScoreUpdated.getHome() + CategoryUtils.SPLIT_LINE + matchScoreUpdated.getAway());
                    matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2032.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2032.getValue());
                }
                // 非输入框，点击罚球取消
                if (1 == sendBallDto.getScore() && 3 == sendBallDto.getBallEventType() && clickFlag) {
                    if (ObjectUtil.isEmpty(matchScore)) {
                        matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                        matchLog.setOperateRearText(CategoryUtils.SPLIT_LINE);
                    } else {
                        matchLog.setOperateForwText(matchScore.getHome() + CategoryUtils.SPLIT_LINE + matchScore.getAway());
                        matchLog.setOperateRearText(matchScoreUpdated.getHome() + CategoryUtils.SPLIT_LINE + matchScoreUpdated.getAway());
                    }
                    matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2033.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2033.getValue());
                }
                // 2分球未命中，非罚球
                if (2 == sendBallDto.getScore() && 1 == sendBallDto.getBallEventType() && !sendBallDto.isFreeThrow()) {
                    if (ObjectUtil.isEmpty(matchScore)) {
                        matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                        matchLog.setOperateRearText(CategoryUtils.SPLIT_LINE);
                    } else {
                        matchLog.setOperateForwText(matchScore.getHome() + CategoryUtils.SPLIT_LINE + matchScore.getAway());
                        matchLog.setOperateRearText(matchScoreUpdated.getHome() + CategoryUtils.SPLIT_LINE + matchScoreUpdated.getAway());
                    }
                    if (sendBallDto.isFreeThrow()) {
                        matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                                + OperateLogTypeEnum.SCORES_PD_203102.getName() + CategoryUtils.SPLIT_AND
                                + OperateLogTypeEnum.SCORES_PD_203102.getValue());
                    } else {
                        matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                                + OperateLogTypeEnum.SCORES_PD_2034.getName() + CategoryUtils.SPLIT_AND
                                + OperateLogTypeEnum.SCORES_PD_2034.getValue());
                    }
                }
                // 2分球命中，非罚球
                if (2 == sendBallDto.getScore() && 2 == sendBallDto.getBallEventType() && !sendBallDto.isFreeThrow()) {
                    if (ObjectUtil.isEmpty(matchScore)) {
                        matchScore = new CommonItem();
                        matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                    } else {
                        matchLog.setOperateForwText(matchScore.getHome() + CategoryUtils.SPLIT_LINE + matchScore.getAway());
                    }
                    matchLog.setOperateRearText(matchScoreUpdated.getHome() + CategoryUtils.SPLIT_LINE + matchScoreUpdated.getAway());
                    if (sendBallDto.isFreeThrow()) {
                        matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                                + OperateLogTypeEnum.SCORES_PD_203202.getName() + CategoryUtils.SPLIT_AND
                                + OperateLogTypeEnum.SCORES_PD_203202.getValue());
                    } else {
                        matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                                + OperateLogTypeEnum.SCORES_PD_2035.getName() + CategoryUtils.SPLIT_AND
                                + OperateLogTypeEnum.SCORES_PD_2035.getValue());
                    }
                }
                // 2分球取消，非罚球
                if (2 == sendBallDto.getScore() && 3 == sendBallDto.getBallEventType() && !sendBallDto.isFreeThrow()) {
                    if (ObjectUtil.isEmpty(matchScore)) {
                        matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                        matchLog.setOperateRearText(CategoryUtils.SPLIT_LINE);
                    } else {
                        matchLog.setOperateForwText(matchScore.getHome() + CategoryUtils.SPLIT_LINE + matchScore.getAway());
                        matchLog.setOperateRearText(matchScoreUpdated.getHome() + CategoryUtils.SPLIT_LINE + matchScoreUpdated.getAway());
                    }
                    matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2036.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2036.getValue());
                }
                // 3分球未命中，非罚球
                if (3 == sendBallDto.getScore() && 1 == sendBallDto.getBallEventType() && !sendBallDto.isFreeThrow()) {
                    if (ObjectUtil.isEmpty(matchScore)) {
                        matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                        matchLog.setOperateRearText(CategoryUtils.SPLIT_LINE);
                    } else {
                        matchLog.setOperateForwText(matchScore.getHome() + CategoryUtils.SPLIT_LINE + matchScore.getAway());
                        matchLog.setOperateRearText(matchScoreUpdated.getHome() + CategoryUtils.SPLIT_LINE + matchScoreUpdated.getAway());
                    }
                    if (sendBallDto.isFreeThrow()) {
                        matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                                + OperateLogTypeEnum.SCORES_PD_203103.getName() + CategoryUtils.SPLIT_AND
                                + OperateLogTypeEnum.SCORES_PD_203103.getValue());
                    } else {
                        matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                                + OperateLogTypeEnum.SCORES_PD_2037.getName() + CategoryUtils.SPLIT_AND
                                + OperateLogTypeEnum.SCORES_PD_2037.getValue());
                    }
                }
                // 3分球命中，非罚球
                if (3 == sendBallDto.getScore() && 2 == sendBallDto.getBallEventType() && !sendBallDto.isFreeThrow()) {
                    if (ObjectUtil.isEmpty(matchScore)) {
                        matchScore = new CommonItem();
                        matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                    } else {
                        matchLog.setOperateForwText(matchScore.getHome() + CategoryUtils.SPLIT_LINE + matchScore.getAway());
                    }
                    matchLog.setOperateRearText(matchScoreUpdated.getHome() + CategoryUtils.SPLIT_LINE + matchScoreUpdated.getAway());
                    if (sendBallDto.isFreeThrow()) {
                        matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                                + OperateLogTypeEnum.SCORES_PD_203203.getName() + CategoryUtils.SPLIT_AND
                                + OperateLogTypeEnum.SCORES_PD_203203.getValue());
                    } else {
                        matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                                + OperateLogTypeEnum.SCORES_PD_2038.getName() + CategoryUtils.SPLIT_AND
                                + OperateLogTypeEnum.SCORES_PD_2038.getValue());
                    }
                }
                // 3分球取消，非罚球
                if (3 == sendBallDto.getScore() && 3 == sendBallDto.getBallEventType() && !sendBallDto.isFreeThrow()) {
                    if (ObjectUtil.isEmpty(matchScore)) {
                        matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                        matchLog.setOperateRearText(CategoryUtils.SPLIT_LINE);
                    } else {
                        matchLog.setOperateForwText(matchScore.getHome() + CategoryUtils.SPLIT_LINE + matchScore.getAway());
                        matchLog.setOperateRearText(matchScoreUpdated.getHome() + CategoryUtils.SPLIT_LINE + matchScoreUpdated.getAway());
                    }
                    matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_2039.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_2039.getValue());
                }
                // 罚球未命中，非输入框
                if (0 == sendBallDto.getScore() && 1 == sendBallDto.getBallEventType() && clickFlag) {
                    if (ObjectUtil.isEmpty(matchScore)) {
                        matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                        matchLog.setOperateRearText(CategoryUtils.SPLIT_LINE);
                    } else {
                        matchLog.setOperateForwText(matchScore.getHome() + CategoryUtils.SPLIT_LINE + matchScore.getAway());
                        matchLog.setOperateRearText(matchScoreUpdated.getHome() + CategoryUtils.SPLIT_LINE + matchScoreUpdated.getAway());
                    }
                    if (sendBallDto.getFreeThrowNumber() == 1) {
                        matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                                + OperateLogTypeEnum.SCORES_PD_2031.getName() + CategoryUtils.SPLIT_AND
                                + OperateLogTypeEnum.SCORES_PD_2031.getValue());
                    }
                    if (sendBallDto.getFreeThrowNumber() == 2) {
                        matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                                + OperateLogTypeEnum.SCORES_PD_203102.getName() + CategoryUtils.SPLIT_AND
                                + OperateLogTypeEnum.SCORES_PD_203102.getValue());
                    }
                    if (sendBallDto.getFreeThrowNumber() == 3) {
                        matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                                + OperateLogTypeEnum.SCORES_PD_203103.getName() + CategoryUtils.SPLIT_AND
                                + OperateLogTypeEnum.SCORES_PD_203103.getValue());
                    }
                }
                matchLog.setOperateId(standardMatchInfo.getMatchManageId());
                matchLog.setMatchManageId(standardMatchInfo.getMatchManageId());
                matchLog.setOperateName(getPeriodNameByPeriodId(standardMatchInfo.getSportId(), standardMatchInfo.getMatchPeriodId()));
                matchLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
                matchLog.setOperateMatchName(getMatchSportTeamNameCode(standardMatchInfo.getId()));
                matchLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10065.getCode().toString());
                if (thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                    matchLog.setOperateUserName(sendBallDto.getOperatorName() + " (" + thirdMatchInfo.getDataSourceCode() + ")");
                } else {
                    matchLog.setOperateUserName(sendBallDto.getOperatorName());
                }
                matchLog.setIpAddress(sendBallDto.getIpAddress());
                long time = System.currentTimeMillis();
                matchLog.setCreateTime(time);
                matchLog.setModifyTime(time);
                matchScoresPdLogMapper.insert(matchLog);
            }
        } catch (Exception e) {
            log.error("MatchScorePdLogServiceImpl::sendBallLog, error: " + e);
        }
    }

    @Override
    public void wonJumpBallLog(Long thirdMatchId, String homeAway) {
        MatchScoresPdLog matchLog = new MatchScoresPdLog();
        try {

        } catch (Exception e) {
            log.error("MatchScorePdLogServiceImpl::sendEventLog, error: " + e);
        }
    }

    @Override
    public void gameStartLog(Long thirdMatchId) {
        MatchScoresPdLog matchLog = new MatchScoresPdLog();
        try {

        } catch (Exception e) {
            log.error("MatchScorePdLogServiceImpl::sendEventLog, error: " + e);
        }
    }

    @Override
    public void gameEndLog(Long thirdMatchId) {
        MatchScoresPdLog matchLog = new MatchScoresPdLog();
        try {

        } catch (Exception e) {
            log.error("MatchScorePdLogServiceImpl::sendEventLog, error: " + e);
        }
    }

    @Override
    public void nextPeriodLog(PDBasketBallNextPeriodDto periodDto) {
        MatchScoresPdLog matchLog = new MatchScoresPdLog();
        try {

        } catch (Exception e) {
            log.error("MatchScorePdLogServiceImpl::sendEventLog, error: " + e);
        }
    }

    @Override
    public void takeRestLog(PDBasketBallTakeRestDto takeRestDto) {
        MatchScoresPdLog matchLog = new MatchScoresPdLog();
        try {

        } catch (Exception e) {
            log.error("MatchScorePdLogServiceImpl::sendEventLog, error: " + e);
        }
    }

    @Override
    public void parseContinueLog(Response<MatchScoreAndTimeVo> response, PDBasketBallPauseDto dto) {
        MatchScoresPdLog matchLog = new MatchScoresPdLog();
        try {
            String homeAwayName;
            if (dto.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                homeAwayName = CategoryUtils.HOME_PARAM;
            } else {
                homeAwayName = CategoryUtils.AWAY_PARAM;
            }
            ThirdMatchInfo thirdMatchInfo = response.getData().getThirdMatchInfo();
            if (ObjectUtil.isNotEmpty(thirdMatchInfo) && thirdMatchInfo.getSportId().intValue() == 2) {
                matchLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100101.getCode().toString());
            } else {
                return;
            }
            MatchScoresInfo matchScoresInfo = scoresService.selectLiveMatchScoreInfo(dto.getThirdMatchId());
            if (matchScoresInfo != null) {
                StandardMatchInfo standardMatchInfo = response.getData().getStandardMatchInfo();
                if (standardMatchInfo == null) {
                    return;
                }
                if (2 == dto.getType()) {
                    matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_100128.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_100128.getValue());
                }
                if (1 == dto.getType()) {
                    matchLog.setOperateParaName(homeAwayName + CategoryUtils.SPLIT_LINE
                            + OperateLogTypeEnum.SCORES_PD_100127.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_100127.getValue());
                }
                matchLog.setOperateId(standardMatchInfo.getMatchManageId());
                matchLog.setMatchManageId(standardMatchInfo.getMatchManageId());
                matchLog.setOperateName(getPeriodNameByPeriodId(standardMatchInfo.getSportId(), standardMatchInfo.getMatchPeriodId()));
                matchLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
                matchLog.setOperateMatchName(getMatchSportTeamNameCode(standardMatchInfo.getId()));
                matchLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10065.getCode().toString());
                matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                if (thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                    matchLog.setOperateUserName(dto.getOperatorName() + " (" + thirdMatchInfo.getDataSourceCode() + ")");
                } else {
                    matchLog.setOperateUserName(dto.getOperatorName());
                }
                matchLog.setIpAddress(dto.getIpAddress());
                long time = System.currentTimeMillis();
                matchLog.setCreateTime(time);
                matchLog.setModifyTime(time);
                matchScoresPdLogMapper.insert(matchLog);
            }
        } catch (Exception e) {
            log.error("MatchScorePdLogServiceImpl::parseContinueLog, error: " + e);
        }
    }

    @Override
    public void breakOrReStartLog(Response<MatchScoreAndTimeVo> response, PDBasketBallParseContinueDto dto) {
        MatchScoresPdLog matchLog = new MatchScoresPdLog();
        try {
            ThirdMatchInfo thirdMatchInfo = response.getData().getThirdMatchInfo();
            if (ObjectUtil.isNotEmpty(thirdMatchInfo) && thirdMatchInfo.getSportId().intValue() == 2) {
                matchLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100101.getCode().toString());
            } else {
                return;
            }
            MatchScoresInfo matchScoresInfo = scoresService.selectLiveMatchScoreInfo(dto.getThirdMatchId());
            if (matchScoresInfo != null) {
                StandardMatchInfo standardMatchInfo = response.getData().getStandardMatchInfo();
                if (standardMatchInfo == null) {
                    return;
                }
                if (1 == dto.getMatchGoStatus()) {
                    matchLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_100128.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_100114.getValue());
                } else {
                    matchLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_100127.getName() + CategoryUtils.SPLIT_AND
                            + OperateLogTypeEnum.SCORES_PD_100113.getValue());
                }
                matchLog.setOperateId(standardMatchInfo.getMatchManageId());
                matchLog.setMatchManageId(standardMatchInfo.getMatchManageId());
                matchLog.setOperateName(getPeriodNameByPeriodId(standardMatchInfo.getSportId(), standardMatchInfo.getMatchPeriodId()));
                matchLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
                matchLog.setOperateMatchName(getMatchSportTeamNameCode(standardMatchInfo.getId()));
                matchLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10065.getCode().toString());
//                Integer timeGo = response.getData().getMatchTimeInfo().getTimeGo();
                if (dto.getMatchGoStatus() == 1) {
                    matchLog.setOperateForwText(OperateLogTypeEnum.SCORES_PD_100128.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100128.getValue());
                    matchLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_10053.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10053.getValue());
                } else {
                    matchLog.setOperateForwText(OperateLogTypeEnum.SCORES_PD_10053.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_10053.getValue());
                    matchLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_100128.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100128.getValue());
                }
                if (thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                    matchLog.setOperateUserName(dto.getOperatorName() + " (" + thirdMatchInfo.getDataSourceCode() + ")");
                } else {
                    matchLog.setOperateUserName(dto.getOperatorName());
                }
                matchLog.setIpAddress(dto.getIpAddress());
                long time = System.currentTimeMillis();
                matchLog.setCreateTime(time);
                matchLog.setModifyTime(time);
                matchScoresPdLogMapper.insert(matchLog);
            }
        } catch (Exception e) {
            log.error("MatchScorePdLogServiceImpl::breakOrReStartLog, error: " + e);
        }
    }

    @Override
    public void editSixScoreLog(Map<Long, BasketballScores> oldAllScores, Response<MatchScoreAndTimeVo> response, PDBasketBallEditSixScoreDto dto) {
        MatchScoresPdLog matchLog = new MatchScoresPdLog();
        try {
            ThirdMatchInfo thirdMatchInfo = response.getData().getThirdMatchInfo();
            if (ObjectUtil.isNotEmpty(thirdMatchInfo) && thirdMatchInfo.getSportId().intValue() == 2) {
                matchLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100101.getCode().toString());
            } else {
                return;
            }
            MatchScoresInfo matchScoresInfo = scoresService.selectLiveMatchScoreInfo(dto.getThirdMatchId());
            if (matchScoresInfo != null) {
                StandardMatchInfo standardMatchInfo = response.getData().getStandardMatchInfo();
                if (standardMatchInfo == null) {
                    return;
                }
                JSONObject periodFootballScores = JSONObject.parseObject(response.getData().getMatchScoresInfo().getScoresJson());
                Map<Long, BasketballScores> newAllScores = JsonMapUtils.parseBasketballMap(periodFootballScores);
                if (dto.getPeriodId() == 13L) {
                    BasketballScores oldScore1306 = oldAllScores.get(BasketballSixPeriodEnum.BASKETBALL_1306.getCode());
                    com.panda.merge.cache.CommonItem newComm1306 = dto.getPeriod1306();
                    boolean flag1306 = newComm1306 != null && (newComm1306.getHome() != null || newComm1306.getAway() != null);
                    BasketballScores oldScore1312 = oldAllScores.get(BasketballSixPeriodEnum.BASKETBALL_1312.getCode());
                    com.panda.merge.cache.CommonItem newComm1312 = dto.getPeriod1312();
                    boolean flag1312 = newComm1312 != null && (newComm1312.getHome() != null || newComm1312.getAway() != null);
                    BasketballScores oldScore13 = oldAllScores.get(13L);
                    CommonItem newComm13 = newAllScores.get(13L).getMatchScore();
                    boolean status = true;
                    if (flag1306 && flag1312) {
                        matchLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_2013.getName() + CategoryUtils.SPLIT_AND
                                + OperateLogTypeEnum.SCORES_PD_2013.getValue());

                        if (ObjectUtil.isEmpty(oldScore13)) {
                            matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                        } else {
                            matchLog.setOperateForwText(oldScore13.getMatchScore().getHome() + CategoryUtils.SPLIT_LINE + oldScore13.getMatchScore().getAway());
                        }
                        Integer home = newComm13.getHome();
                        Integer away = newComm13.getAway();
                        matchLog.setOperateRearText((home == null ? oldScore13.getMatchScore().getHome() : home) + CategoryUtils.SPLIT_LINE + (away == null ? oldScore13.getMatchScore().getAway() : away));
                        status = false;
                    }
                    if (status && flag1306) {
                        matchLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_201306.getName() + CategoryUtils.SPLIT_AND
                                + OperateLogTypeEnum.SCORES_PD_201306.getValue());
                        if (ObjectUtil.isEmpty(oldScore1306)) {
                            matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                        } else {
                            matchLog.setOperateForwText(oldScore1306.getMatchScore().getHome() + CategoryUtils.SPLIT_LINE + oldScore1306.getMatchScore().getAway());
                        }
                        Integer home = newComm1306.getHome();
                        Integer away = newComm1306.getAway();
                        matchLog.setOperateRearText((home == null ? oldScore1306.getMatchScore().getHome() : home) + CategoryUtils.SPLIT_LINE + (away == null ? oldScore1306.getMatchScore().getAway() : away));
                    }
                    if (status && flag1312) {
                        matchLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_201312.getName() + CategoryUtils.SPLIT_AND
                                + OperateLogTypeEnum.SCORES_PD_201312.getValue());
                        if (ObjectUtil.isEmpty(oldScore1312)) {
                            matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                        } else {
                            matchLog.setOperateForwText(oldScore1312.getMatchScore().getHome() + CategoryUtils.SPLIT_LINE + oldScore1312.getMatchScore().getAway());
                        }
                        Integer home = newComm1312.getHome();
                        Integer away = newComm1312.getAway();
                        matchLog.setOperateRearText((home == null ? oldScore1312.getMatchScore().getHome() : home) + CategoryUtils.SPLIT_LINE + (away == null ? oldScore1312.getMatchScore().getAway() : away));
                    }
                }

                if (dto.getPeriodId() == 14L) {
                    BasketballScores oldScore1406 = oldAllScores.get(BasketballSixPeriodEnum.BASKETBALL_1406.getCode());
                    com.panda.merge.cache.CommonItem newComm1406 = dto.getPeriod1406();
                    boolean flag1406 = newComm1406 != null && (newComm1406.getHome() != null || newComm1406.getAway() != null);
                    BasketballScores oldScore1412 = oldAllScores.get(BasketballSixPeriodEnum.BASKETBALL_1412.getCode());
                    com.panda.merge.cache.CommonItem newComm1412 = dto.getPeriod1412();
                    boolean flag1412 = newComm1412 != null && (newComm1412.getHome() != null || newComm1412.getAway() != null);
                    BasketballScores oldScore14 = oldAllScores.get(14L);
                    CommonItem newComm14 = newAllScores.get(14L).getMatchScore();
                    boolean status = true;
                    if (flag1406 && flag1412) {
                        matchLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_2014.getName() + CategoryUtils.SPLIT_AND
                                + OperateLogTypeEnum.SCORES_PD_2014.getValue());

                        if (ObjectUtil.isEmpty(oldScore14)) {
                            matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                        } else {
                            matchLog.setOperateForwText(oldScore14.getMatchScore().getHome() + CategoryUtils.SPLIT_LINE + oldScore14.getMatchScore().getAway());
                        }
                        Integer home = newComm14.getHome();
                        Integer away = newComm14.getAway();
                        matchLog.setOperateRearText((home == null ? oldScore14.getMatchScore().getHome() : home) + CategoryUtils.SPLIT_LINE + (away == null ? oldScore14.getMatchScore().getAway() : away));
                        status = false;
                    }
                    if (status && flag1406) {
                        matchLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_201406.getName() + CategoryUtils.SPLIT_AND
                                + OperateLogTypeEnum.SCORES_PD_201406.getValue());
                        if (ObjectUtil.isEmpty(oldScore1406)) {
                            matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                        } else {
                            matchLog.setOperateForwText(oldScore1406.getMatchScore().getHome() + CategoryUtils.SPLIT_LINE + oldScore1406.getMatchScore().getAway());
                        }
                        Integer home = newComm1406.getHome();
                        Integer away = newComm1406.getAway();
                        matchLog.setOperateRearText((home == null ? oldScore1406.getMatchScore().getHome() : home) + CategoryUtils.SPLIT_LINE + (away == null ? oldScore1406.getMatchScore().getAway() : away));
                    }
                    if (status && flag1412) {
                        matchLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_201412.getName() + CategoryUtils.SPLIT_AND
                                + OperateLogTypeEnum.SCORES_PD_201412.getValue());
                        if (ObjectUtil.isEmpty(oldScore1412)) {
                            matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                        } else {
                            matchLog.setOperateForwText(oldScore1412.getMatchScore().getHome() + CategoryUtils.SPLIT_LINE + oldScore1412.getMatchScore().getAway());
                        }
                        Integer home = newComm1412.getHome();
                        Integer away = newComm1412.getAway();
                        matchLog.setOperateRearText((home == null ? oldScore1412.getMatchScore().getHome() : home) + CategoryUtils.SPLIT_LINE + (away == null ? oldScore1412.getMatchScore().getAway() : away));
                    }
                }

                if (dto.getPeriodId() == 15L) {
                    BasketballScores oldScore1506 = oldAllScores.get(BasketballSixPeriodEnum.BASKETBALL_1506.getCode());
                    com.panda.merge.cache.CommonItem newComm1506 = dto.getPeriod1506();
                    boolean flag1506 = newComm1506 != null && (newComm1506.getHome() != null || newComm1506.getAway() != null);
                    BasketballScores oldScore1512 = oldAllScores.get(BasketballSixPeriodEnum.BASKETBALL_1512.getCode());
                    com.panda.merge.cache.CommonItem newComm1512 = dto.getPeriod1512();
                    boolean flag1512 = newComm1512 != null && (newComm1512.getHome() != null || newComm1512.getAway() != null);
                    BasketballScores oldScore15 = oldAllScores.get(15L);
                    CommonItem newComm15 = newAllScores.get(15L).getMatchScore();
                    boolean status = true;
                    if (flag1506 && flag1512) {
                        matchLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_2015.getName() + CategoryUtils.SPLIT_AND
                                + OperateLogTypeEnum.SCORES_PD_2015.getValue());

                        if (ObjectUtil.isEmpty(oldScore15)) {
                            matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                        } else {
                            matchLog.setOperateForwText(oldScore15.getMatchScore().getHome() + CategoryUtils.SPLIT_LINE + oldScore15.getMatchScore().getAway());
                        }
                        Integer home = newComm15.getHome();
                        Integer away = newComm15.getAway();
                        matchLog.setOperateRearText((home == null ? oldScore15.getMatchScore().getHome() : home) + CategoryUtils.SPLIT_LINE + (away == null ? oldScore15.getMatchScore().getAway() : away));
                        status = false;
                    }
                    if (status && flag1506) {
                        matchLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_201506.getName() + CategoryUtils.SPLIT_AND
                                + OperateLogTypeEnum.SCORES_PD_201506.getValue());
                        if (ObjectUtil.isEmpty(oldScore1506)) {
                            matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                        } else {
                            matchLog.setOperateForwText(oldScore1506.getMatchScore().getHome() + CategoryUtils.SPLIT_LINE + oldScore1506.getMatchScore().getAway());
                        }
                        Integer home = newComm1506.getHome();
                        Integer away = newComm1506.getAway();
                        matchLog.setOperateRearText((home == null ? oldScore1506.getMatchScore().getHome() : home) + CategoryUtils.SPLIT_LINE + (away == null ? oldScore1506.getMatchScore().getAway() : away));
                    }
                    if (status && flag1512) {
                        matchLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_201512.getName() + CategoryUtils.SPLIT_AND
                                + OperateLogTypeEnum.SCORES_PD_201512.getValue());
                        if (ObjectUtil.isEmpty(oldScore1512)) {
                            matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                        } else {
                            matchLog.setOperateForwText(oldScore1512.getMatchScore().getHome() + CategoryUtils.SPLIT_LINE + oldScore1512.getMatchScore().getAway());
                        }
                        Integer home = newComm1512.getHome();
                        Integer away = newComm1512.getAway();
                        matchLog.setOperateRearText((home == null ? oldScore1512.getMatchScore().getHome() : home) + CategoryUtils.SPLIT_LINE + (away == null ? oldScore1512.getMatchScore().getAway() : away));
                    }
                }

                if (dto.getPeriodId() == 16L) {
                    BasketballScores oldScore1606 = oldAllScores.get(BasketballSixPeriodEnum.BASKETBALL_1606.getCode());
                    com.panda.merge.cache.CommonItem newComm1606 = dto.getPeriod1606();
                    boolean flag1606 = newComm1606 != null && (newComm1606.getHome() != null || newComm1606.getAway() != null);
                    BasketballScores oldScore1612 = oldAllScores.get(BasketballSixPeriodEnum.BASKETBALL_1612.getCode());
                    com.panda.merge.cache.CommonItem newComm1612 = dto.getPeriod1612();
                    boolean flag1612 = newComm1612 != null && (newComm1612.getHome() != null || newComm1612.getAway() != null);
                    BasketballScores oldScore16 = oldAllScores.get(16L);
                    CommonItem newComm16 = newAllScores.get(16L).getMatchScore();
                    boolean status = true;
                    if (flag1606 && flag1612) {
                        matchLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_2016.getName() + CategoryUtils.SPLIT_AND
                                + OperateLogTypeEnum.SCORES_PD_2016.getValue());

                        if (ObjectUtil.isEmpty(oldScore16)) {
                            matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                        } else {
                            matchLog.setOperateForwText(oldScore16.getMatchScore().getHome() + CategoryUtils.SPLIT_LINE + oldScore16.getMatchScore().getAway());
                        }
                        Integer home = newComm16.getHome();
                        Integer away = newComm16.getAway();
                        matchLog.setOperateRearText((home == null ? oldScore16.getMatchScore().getHome() : home) + CategoryUtils.SPLIT_LINE + (away == null ? oldScore16.getMatchScore().getAway() : away));
                        status = false;
                    }
                    if (status && flag1606) {
                        matchLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_201606.getName() + CategoryUtils.SPLIT_AND
                                + OperateLogTypeEnum.SCORES_PD_201606.getValue());
                        if (ObjectUtil.isEmpty(oldScore1606)) {
                            matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                        } else {
                            matchLog.setOperateForwText(oldScore1606.getMatchScore().getHome() + CategoryUtils.SPLIT_LINE + oldScore1606.getMatchScore().getAway());
                        }
                        Integer home = newComm1606.getHome();
                        Integer away = newComm1606.getAway();
                        matchLog.setOperateRearText((home == null ? oldScore1606.getMatchScore().getHome() : home) + CategoryUtils.SPLIT_LINE + (away == null ? oldScore1606.getMatchScore().getAway() : away));
                    }
                    if (status && flag1612) {
                        matchLog.setOperateParaName(OperateLogTypeEnum.SCORES_PD_201612.getName() + CategoryUtils.SPLIT_AND
                                + OperateLogTypeEnum.SCORES_PD_201612.getValue());
                        if (ObjectUtil.isEmpty(oldScore1612)) {
                            matchLog.setOperateForwText(CategoryUtils.SPLIT_LINE);
                        } else {
                            matchLog.setOperateForwText(oldScore1612.getMatchScore().getHome() + CategoryUtils.SPLIT_LINE + oldScore1612.getMatchScore().getAway());
                        }
                        Integer home = newComm1612.getHome();
                        Integer away = newComm1612.getAway();
                        matchLog.setOperateRearText((home == null ? oldScore1612.getMatchScore().getHome() : home) + CategoryUtils.SPLIT_LINE + (away == null ? oldScore1612.getMatchScore().getAway() : away));
                    }
                }

                matchLog.setOperateId(standardMatchInfo.getMatchManageId());
                matchLog.setMatchManageId(standardMatchInfo.getMatchManageId());
                matchLog.setOperateName(getPeriodNameByPeriodId(standardMatchInfo.getSportId(), standardMatchInfo.getMatchPeriodId()));
                matchLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
                matchLog.setOperateMatchName(getMatchSportTeamNameCode(standardMatchInfo.getId()));
                matchLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100073.getCode().toString());
                if (thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                    matchLog.setOperateUserName(dto.getOperatorName() + " (" + thirdMatchInfo.getDataSourceCode() + ")");
                } else {
                    matchLog.setOperateUserName(dto.getOperatorName());
                }
                matchLog.setIpAddress(dto.getIpAddress());
                long time = System.currentTimeMillis();
                matchLog.setCreateTime(time);
                matchLog.setModifyTime(time);
                matchScoresPdLogMapper.insert(matchLog);
            }
        } catch (Exception e) {
            log.error("MatchScorePdLogServiceImpl::editSixScoreLog, error: " + e);
        }
    }

    /**
     * 组装修改前修改后的比分细项
     * @param list
     * @param standardMatchInfo
     * @param oldScores
     * @param newScores
     * @param thirdMatchInfo
     * @param allFootballScoreEditDto
     * @param data
     * @return
     */
    public List<MatchScoresPdLog> setOperateText(List<MatchScoresPdLog> list ,StandardMatchInfo standardMatchInfo,
                                                 Map<Long, FootballScores> oldScores,Map<Long, FootballScores> newScores,
                                                 ThirdMatchInfo thirdMatchInfo,AllFootballScoreEditDto allFootballScoreEditDto,MatchScoreAndTimeVo data){

        for(Map.Entry<Long, FootballScores> entry1:oldScores.entrySet()){
            if(entry1.getKey()==-1){
                continue;
            }
            FootballScores m1value = entry1.getValue();
            FootballScores m2value = newScores.get(entry1.getKey());
            if (!m1value.equals(m2value)) {//若两个map中相同key对应的value不相等
                HashMap<String,Object> obj = classIsEqual(m1value,m2value);
                if(obj==null){
                    continue;
                }
                FootballScores oldScoreObj=oldScores.get(entry1.getKey());
                FootballScores newScoreObj=newScores.get(entry1.getKey());
                for(Map.Entry<String, Object> o:obj.entrySet()){
                    String key = o.getKey();

                    Class<?> clazz1 = oldScoreObj.getClass();
                    Field[] fields = clazz1.getDeclaredFields();
                    for (Field field : fields) {
                        if(!key.equals(field.getName())){
                            continue;
                        }
                        if(o.getKey().equals("faCard")){
                            continue;
                        }
                        // 避免 can not access a member of class com.java.test.Person with modifiers "private"
                        field.setAccessible(true);
                        try {
                            CommonItem item = (CommonItem) field.get(oldScoreObj);
                            CommonItem item2 = (CommonItem) field.get(newScoreObj);

                            MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
                            buildMatchSciresPdLogs(standardMatchInfo, thirdMatchInfo, allFootballScoreEditDto, data, entry1, o, matchScoresPdLog, item, item2);
                            list.add(matchScoresPdLog);


                        } catch (IllegalAccessException e) {

                        }
                    }

                }
            }
        }
        return list;
    }

    /**
     * 组装比分编辑日志消息
     * @param standardMatchInfo
     * @param thirdMatchInfo
     * @param allFootballScoreEditDto
     * @param data
     * @param entry1
     * @param o
     * @param matchScoresPdLog
     * @param item
     * @param item2
     */
    private void buildMatchSciresPdLogs(StandardMatchInfo standardMatchInfo, ThirdMatchInfo thirdMatchInfo, AllFootballScoreEditDto allFootballScoreEditDto, MatchScoreAndTimeVo data, Map.Entry<Long, FootballScores> entry1, Map.Entry<String, Object> o, MatchScoresPdLog matchScoresPdLog, CommonItem item, CommonItem item2) {
        matchScoresPdLog.setOperateModule(OperateLogTypeEnum.SCORES_PD_100100.getCode().toString());
        matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
        matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100073.getCode().toString());
        matchScoresPdLog.setOperateId(standardMatchInfo.getMatchManageId());
        matchScoresPdLog.setOperateName(getMatchSportTeamNameCode(standardMatchInfo.getId()));
        matchScoresPdLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
        matchScoresPdLog.setOperateMatchName(getMatchSportTeamNameCode(thirdMatchInfo.getReferenceId()));
        matchScoresPdLog.setIpAddress(allFootballScoreEditDto.getIpAddress());

        if (data.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || data.getThirdMatchInfo().getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
            matchScoresPdLog.setOperateUserName(allFootballScoreEditDto.getOperatorName() + " (" + data.getThirdMatchInfo().getDataSourceCode() + ")");
        } else {
            matchScoresPdLog.setOperateUserName(allFootballScoreEditDto.getOperatorName());
        }
        long time = TimeUtils.millsSecondsEast8ZoneGmt();
        matchScoresPdLog.setCreateTime(time);
        matchScoresPdLog.setModifyTime(time);
        matchScoresPdLog.setOperateForwText(item.doCountScoreStr());
        matchScoresPdLog.setOperateRearText(item2.doCountScoreStr());
        matchScoresPdLog.setOperateParaName(getPeriodNameByPeriodId(standardMatchInfo.getSportId(), entry1.getKey())+ CategoryUtils.SPLIT_LINE + o.getKey());
    }

    /**
     * 返回时间，格式分钟:秒
     * startTimeSecond / 60 + ":" + startTimeSecond % 60
     *
     * @param startTime
     * @return
     */
    private String getMinuteTime(Long startTime) {
        StringBuffer timeBuffer = new StringBuffer();
        timeBuffer.append(CategoryUtils.SPLIT_LINE);
        Long minuteTime = startTime / 60;
        Long secondTime = startTime % 60;
        if (minuteTime < 10) {
            timeBuffer.append("0" + minuteTime);
        } else {
            timeBuffer.append(minuteTime);
        }
        timeBuffer.append(":");
        if (secondTime < 10) {
            timeBuffer.append("0" + secondTime);
        } else {
            timeBuffer.append(secondTime);
        }
        return timeBuffer.toString();
    }


    /**
     * 获取namecode
     * @param matchInfoId
     * @return
     */
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
            result.append(CategoryUtils.SPLIT_AND);
        }
        if (homeSportTeam != null && StringUtils.isNotEmpty(homeSportTeam.getNameSpell()) && awaySportTeam != null && StringUtils.isNotEmpty(awaySportTeam.getNameSpell())) {
            result.append(homeSportTeam.getNameSpell() + " vs " + awaySportTeam.getNameSpell());
        }
        return result.toString();
    }

    /**
     * 获取阶段namecode
     * @param sportId
     * @param periodId
     * @return
     */
    private  String getPeriodNameByPeriodId(Long sportId, Long periodId) {

        String codeName = PDOperateLogEnum.getCnNameByCode(sportId.toString() + periodId.toString()) + CategoryUtils.SPLIT_AND + PDOperateLogEnum.getEnNameByCode(sportId.toString() + periodId.toString());
        return codeName;
    }


//    public static void main(String[] args) throws NoSuchFieldException {
//
////        System.out.println(getPeriodNameByPeriodId(1L,6L));
//        String json = "{-1:{\"attack\":{\"away\":18,\"home\":16},\"corner\":{\"away\":4,\"home\":4},\"dangerousAttack\":{\"away\":7,\"home\":12},\"faCard\":{\"away\":6,\"home\":11},\"freeKickScore\":{\"away\":4,\"home\":5},\"goal\":{\"away\":2,\"home\":4},\"kickOff\":{\"away\":1,\"home\":0},\"offside\":{\"away\":1,\"home\":2},\"penaltyAwarded\":{\"away\":0,\"home\":0},\"possession\":{\"away\":0,\"home\":0},\"redCard\":{\"away\":2,\"home\":5},\"shot\":{\"away\":1,\"home\":3},\"shotOff\":{\"away\":1,\"home\":3},\"shotOn\":{\"away\":0,\"home\":0},\"substitution\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":2,\"home\":1}},60899:{\"corner\":{\"away\":0,\"home\":1},\"faCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"redCard\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0}},6020:{\"corner\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"redCard\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0}},6:{\"attack\":{\"away\":18,\"home\":16},\"corner\":{\"away\":2,\"home\":2},\"dangerousAttack\":{\"away\":7,\"home\":12},\"faCard\":{\"away\":2,\"home\":5},\"freeKickScore\":{\"away\":4,\"home\":5},\"goal\":{\"away\":1,\"home\":2},\"kickOff\":{\"away\":1,\"home\":0},\"offside\":{\"away\":1,\"home\":2},\"penaltyAwarded\":{\"away\":0,\"home\":0},\"possession\":{\"away\":0,\"home\":0},\"redCard\":{\"away\":0,\"home\":2},\"shot\":{\"away\":1,\"home\":3},\"shotOff\":{\"away\":1,\"home\":3},\"shotOn\":{\"away\":0,\"home\":0},\"substitution\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":2,\"home\":1}},61799:{\"corner\":{\"away\":0,\"home\":1},\"faCard\":{\"away\":2,\"home\":1},\"goal\":{\"away\":0,\"home\":1},\"redCard\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":2,\"home\":1}},7:{\"attack\":{\"away\":0,\"home\":0},\"corner\":{\"away\":2,\"home\":2},\"dangerousAttack\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":4,\"home\":4},\"freeKickScore\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":2},\"kickOff\":{\"away\":0,\"home\":0},\"offside\":{\"away\":0,\"home\":0},\"penaltyAwarded\":{\"away\":0,\"home\":0},\"possession\":{\"away\":0,\"home\":0},\"redCard\":{\"away\":2,\"home\":2},\"shot\":{\"away\":0,\"home\":0},\"shotOff\":{\"away\":0,\"home\":0},\"shotOn\":{\"away\":0,\"home\":0},\"substitution\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0}},6025:{\"corner\":{\"away\":0,\"home\":1},\"faCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"redCard\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0}},41:{\"attack\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":0},\"dangerousAttack\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":2},\"freeKickScore\":{\"away\":0,\"home\":0},\"goal\":{\"away\":1,\"home\":0},\"kickOff\":{\"away\":0,\"home\":0},\"offside\":{\"away\":0,\"home\":0},\"penaltyAwarded\":{\"away\":0,\"home\":0},\"possession\":{\"away\":0,\"home\":0},\"redCard\":{\"away\":0,\"home\":1},\"shot\":{\"away\":0,\"home\":0},\"shotOff\":{\"away\":0,\"home\":0},\"shotOn\":{\"away\":0,\"home\":0},\"substitution\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0}},7050:{\"corner\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"redCard\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0}},62699:{\"corner\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":1,\"home\":0},\"redCard\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0}},6030:{\"corner\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":1},\"redCard\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0}},6005:{\"corner\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"redCard\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0}},6040:{\"corner\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":1,\"home\":0},\"redCard\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0}},6010:{\"corner\":{\"away\":0,\"home\":1},\"faCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"redCard\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0}},73599:{\"corner\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"redCard\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0}},6015:{\"corner\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"redCard\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0}}}\t";
//        String json2= "{-1:{\"attack\":{\"away\":18,\"home\":16},\"corner\":{\"away\":4,\"home\":4},\"dangerousAttack\":{\"away\":7,\"home\":12},\"faCard\":{\"away\":6,\"home\":11},\"freeKickScore\":{\"away\":4,\"home\":5},\"goal\":{\"away\":5,\"home\":3},\"kickOff\":{\"away\":1,\"home\":0},\"offside\":{\"away\":1,\"home\":2},\"penaltyAwarded\":{\"away\":0,\"home\":0},\"possession\":{\"away\":0,\"home\":0},\"redCard\":{\"away\":2,\"home\":5},\"shot\":{\"away\":1,\"home\":3},\"shotOff\":{\"away\":1,\"home\":3},\"shotOn\":{\"away\":0,\"home\":0},\"substitution\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":2,\"home\":1}},60899:{\"corner\":{\"away\":0,\"home\":1},\"faCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"redCard\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0}},6020:{\"corner\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"redCard\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0}},6:{\"attack\":{\"away\":18,\"home\":16},\"corner\":{\"away\":2,\"home\":2},\"dangerousAttack\":{\"away\":7,\"home\":12},\"faCard\":{\"away\":2,\"home\":5},\"freeKickScore\":{\"away\":4,\"home\":5},\"goal\":{\"away\":2,\"home\":1},\"kickOff\":{\"away\":1,\"home\":0},\"offside\":{\"away\":1,\"home\":2},\"penaltyAwarded\":{\"away\":0,\"home\":0},\"possession\":{\"away\":0,\"home\":0},\"redCard\":{\"away\":0,\"home\":2},\"shot\":{\"away\":1,\"home\":3},\"shotOff\":{\"away\":1,\"home\":3},\"shotOn\":{\"away\":0,\"home\":0},\"substitution\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":2,\"home\":1}},61799:{\"corner\":{\"away\":0,\"home\":1},\"faCard\":{\"away\":2,\"home\":1},\"goal\":{\"away\":0,\"home\":1},\"redCard\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":2,\"home\":1}},7:{\"attack\":{\"away\":0,\"home\":0},\"corner\":{\"away\":2,\"home\":2},\"dangerousAttack\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":4,\"home\":4},\"freeKickScore\":{\"away\":0,\"home\":0},\"goal\":{\"away\":1,\"home\":1},\"kickOff\":{\"away\":0,\"home\":0},\"offside\":{\"away\":0,\"home\":0},\"penaltyAwarded\":{\"away\":0,\"home\":0},\"possession\":{\"away\":0,\"home\":0},\"redCard\":{\"away\":2,\"home\":2},\"shot\":{\"away\":0,\"home\":0},\"shotOff\":{\"away\":0,\"home\":0},\"shotOn\":{\"away\":0,\"home\":0},\"substitution\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0}},6025:{\"corner\":{\"away\":0,\"home\":1},\"faCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"redCard\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0}},41:{\"attack\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":0},\"dangerousAttack\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":2},\"freeKickScore\":{\"away\":0,\"home\":0},\"goal\":{\"away\":2,\"home\":1},\"kickOff\":{\"away\":0,\"home\":0},\"offside\":{\"away\":0,\"home\":0},\"penaltyAwarded\":{\"away\":0,\"home\":0},\"possession\":{\"away\":0,\"home\":0},\"redCard\":{\"away\":0,\"home\":1},\"shot\":{\"away\":0,\"home\":0},\"shotOff\":{\"away\":0,\"home\":0},\"shotOn\":{\"away\":0,\"home\":0},\"substitution\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0}},7050:{\"corner\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"redCard\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0}},62699:{\"corner\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":1,\"home\":0},\"redCard\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0}},6030:{\"corner\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":1},\"redCard\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0}},6005:{\"corner\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"redCard\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0}},6040:{\"corner\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":1,\"home\":0},\"redCard\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0}},6010:{\"corner\":{\"away\":0,\"home\":1},\"faCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"redCard\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0}},73599:{\"corner\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"redCard\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0}},6015:{\"corner\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"redCard\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0}}}\t";
//        JSONObject oldScoresJson = JSONObject.parseObject(json);
//        JSONObject newScoresJson = JSONObject.parseObject(json2);
//        Map<Long, FootballScores> oldScores= JsonMapUtils.parseFootballMap(oldScoresJson);
//        Map<Long, FootballScores> newScores= JsonMapUtils.parseFootballMap(newScoresJson);
//
//        for(Map.Entry<Long, FootballScores> entry1:oldScores.entrySet()){
//            if(entry1.getKey()==-1){
//                continue;
//            }
//            FootballScores m1value = entry1.getValue();
//            FootballScores m2value = newScores.get(entry1.getKey());
//            if (!m1value.equals(m2value)) {//若两个map中相同key对应的value不相等
//                HashMap<String,Object> obj = classIsEqual(m1value,m2value);
//                if(obj==null){
//                    continue;
//                }
//                FootballScores oldScoreObj=oldScores.get(entry1.getKey());
//                FootballScores newScoreObj=newScores.get(entry1.getKey());
//                for(Map.Entry<String, Object> o:obj.entrySet()){
//                    String key = o.getKey();
//
//                    Class<?> clazz1 = oldScoreObj.getClass();
////                    Class<?> clazz2 = oldScoreObj.getClass();
//                    Field[] fields = clazz1.getDeclaredFields();
////                    Field[] fields2 = clazz2.getDeclaredFields();
//                    for (Field field : fields) {
//                        if(!key.equals(field.getName())){
//                            continue;
//                        }
//                        if(o.getKey().equals("faCard")){
//                            continue;
//                        }
//                        // 避免 can not access a member of class com.java.test.Person with modifiers "private"
//                        field.setAccessible(true);
////                        fields2[0].setAccessible(true);
//                        try {
//                            System.out.println(entry1.getKey()+"--"+o.getKey());
//                            CommonItem item = (CommonItem) field.get(oldScoreObj);
//                            System.out.println("old:"+item.doCountScoreStr());
//                            CommonItem item2 = (CommonItem) field.get(newScoreObj);
//                            System.out.println("new:"+item2.doCountScoreStr());
//                            System.out.println("-------------------------------");
//                        } catch (IllegalAccessException e) {
//
//                        }
//                    }
//
//                }
//            }
//        }
//
//    }

    /**
     * 判断两个对象中的值哪些修改了
     * @param object1
     * @param object2
     * @param <T>
     * @return
     */
    public  static <T> HashMap<String,Object> classIsEqual(T object1, T object2){
        if(object1==null || object2==null){
            return null;
        }
        HashMap<String,Object> equalAttributeMap = new HashMap<>();
        //获取对象的class
        Class c1 = object1.getClass();
        Class c2 = object2.getClass();
        //获取该类中的全部属性
        Field[] fields1 = c1.getDeclaredFields();
        Object tempValue1,tempValue2;
        int i;
        for(i = 0;i < fields1.length;i++){
            fields1[i].setAccessible(true);
            try {
                //获取两个对象该属性的值
                tempValue1 = fields1[i].get(object1);
                tempValue2 = fields1[i].get(object2);
                //tempValue1为null，tempValue2不为null表明数据原来是空现在修改了
                //tempValue1不为null，tempValue2不为null表明数据修改了
                if(tempValue2 != null && !tempValue1.equals(tempValue2)){
                    equalAttributeMap.put(fields1[i].getName(),tempValue2);
                }
            } catch (IllegalAccessException e) {

            }
        }
        return equalAttributeMap;
    }
}
