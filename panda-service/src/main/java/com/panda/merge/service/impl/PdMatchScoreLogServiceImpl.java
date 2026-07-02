package com.panda.merge.service.impl;

import com.panda.merge.common.enums.OperateLogTypeEnum;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.dto.TradeMarketConfigDTO;
import com.panda.merge.mapper.MatchScoresPdLogMapper;
import com.panda.merge.mapper.StandardSportMarketSellMapper;
import com.panda.merge.mapper.StandardSportTeamMapper;
import com.panda.merge.model.MatchScoresPdLog;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.model.StandardSportMarketSellExample;
import com.panda.merge.model.StandardSportTeam;
import com.panda.merge.service.PdMatchScoreLogService;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.util.CategoryUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PdMatchScoreLogServiceImpl implements PdMatchScoreLogService {


    @Autowired
    MatchScoresPdLogMapper matchScoresPdLogMapper;

    @Autowired
    StandardSportTeamMapper standardSportTeamMapper;

    @Autowired
    StandardMatchInfoService standardMatchInfoService;

    @Autowired
    StandardSportMarketSellMapper standardSportMarketSellMapper;


    @Override
    public void updateMarketStatusLog(TradeMarketConfigDTO tradeMarketConfigDTO) {

        if (tradeMarketConfigDTO.getMarketStatus() == null) {
            return;
        }
        StandardMatchInfo standardMatchInfo = null;
        MatchScoresPdLog matchScoresPdLog = new MatchScoresPdLog();
        try {
            standardMatchInfo = standardMatchInfoService.getItem(Long.valueOf(tradeMarketConfigDTO.getTargetId()));
            if (standardMatchInfo == null) {
                return;
            }
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
                default:return;
            }
            matchScoresPdLog.setOperateId(standardMatchInfo.getMatchManageId());//操作对象ID
            matchScoresPdLog.setOperateName(getMatchSportTeamNameCode(standardMatchInfo.getId()));
            matchScoresPdLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100120.getCode().toString());
            switch (tradeMarketConfigDTO.getMarketStatus()) {
                case 0:
                    matchScoresPdLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_100108.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100108.getValue());//操作后
                    break;
                case 1:
                    matchScoresPdLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_100110.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100110.getValue());//操作后
                    break;
                case 2:
                    matchScoresPdLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_100109.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100109.getValue());//操作后
                    break;
                case 13:
                    matchScoresPdLog.setOperateRearText(OperateLogTypeEnum.SCORES_PD_100122.getName() + CategoryUtils.SPLIT_AND + OperateLogTypeEnum.SCORES_PD_100122.getValue());//操作后
                    break;
                default:
                    return;
            }
            matchScoresPdLog.setMatchManageId(standardMatchInfo.getMatchManageId());
            matchScoresPdLog.setOperateMatchId(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateForwText(CategoryUtils.SPLIT_LINE);//操作前
            matchScoresPdLog.setOperateParaName(CategoryUtils.SPLIT_LINE);
            matchScoresPdLog.setOperateUserName(tradeMarketConfigDTO.getOperaterName());//操作人
            matchScoresPdLog.setIpAddress(tradeMarketConfigDTO.getIpAddress());
            long time = TimeUtils.millsSecondsEast8ZoneGmt();
            matchScoresPdLog.setCreateTime(time);//操作时间
            matchScoresPdLog.setModifyTime(time);
            matchScoresPdLogMapper.insert(matchScoresPdLog);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("updateMarketStatusLog,标准赛事ID:{} , error:{}", standardMatchInfo.getId(), e);
        }

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

}
