package com.panda.merge.service.settleMention.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.panda.merge.constant.*;
import com.panda.merge.dto.DataSourceSettleWeightDto;
import com.panda.merge.mapper.MatchSettleCheckInfoMapper;
import com.panda.merge.mapper.MatchSettleScoreMapper;
import com.panda.merge.model.*;
import com.panda.merge.service.ISettleTemplateService;
import com.panda.merge.service.settleMention.dto.FootballMentionStatus;
import com.panda.merge.utils.SettleTemplateJsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @description: settle mention football score mismatch implementation
 * @author: Henry Wang
 * @create: 2024-08-28 13:39
 **/
@Slf4j
@Component
public class FootballPhaseScoreMismatchProcessor extends AbstractFootballProcessor<FootballMentionStatus>{

    @Resource
    protected ISettleTemplateService settleTemplateService;

    @Resource
    protected MatchSettleCheckInfoMapper matchSettleCheckInfoMapper;

    @Resource
    protected MatchSettleScoreMapper matchSettleScoreMapper;

    private static final List<String> allMins15Codes = Arrays.asList(FootballPeriodValidateEnum.GOAL_2.getCode().toString(),
            FootballPeriodValidateEnum.GOAL_3.getCode().toString(),FootballPeriodValidateEnum.GOAL_4.getCode().toString(),FootballPeriodValidateEnum.GOAL_6.getCode().toString(),
            FootballPeriodValidateEnum.GOAL_7.getCode().toString(),FootballPeriodValidateEnum.GOAL_8.getCode().toString());
    /** 5/15分钟数据不匹配校验不考虑的数据源 */
    private static final Set<String> DATA_SOURCE_5_15_IGNORE = new HashSet<>(Arrays.asList("N01", "N02", "N03", "LS"));

    @Override
    protected Object obtainData(Map<String, Object> parameters) {
        MatchSettleCheckInfo matchSettleCheckInfo = (MatchSettleCheckInfo) parameters.get("matchSettleCheckInfo");
        String settleNum = (String) parameters.get("settleNum");
        Long sportId = (Long) parameters.get("sportId");
        // 5/15分钟：N01、N02、N03、LS 不参与数据不匹配校验
        if (sportId != null && sportId == 1 && matchSettleCheckInfo != null && DATA_SOURCE_5_15_IGNORE.contains(matchSettleCheckInfo.getDataSourceCode())) {
            log.info("[PhaseScoreMismatchProcessor] Football addSettleMention ignore data source {} for phase mismatch", matchSettleCheckInfo.getDataSourceCode());
            return null;
        }
        String sport = 1==sportId? "Football" : "Basketball";
        log.info("[PhaseScoreMismatchProcessor] sport:{} addSettleMention obtainData with settleEventId:{} start!", sport, matchSettleCheckInfo.getSettleScoreEventId());
        SettleEventCodeEnum settleEventCodeEnum = SettleEventCodeEnum.getEventCodeEnum(matchSettleCheckInfo.getEventCode());
        if (settleEventCodeEnum == null) {
            log.info("[PhaseScoreMismatchProcessor] sport:{} addSettleMention obtainData with settleEventId:{} settleEventCodeEnum is null!", sport, matchSettleCheckInfo.getSettleScoreEventId());
            return null;
        }
        // 获取不同数据源但同次序数据
        List<String> dataSourceCodes = obtainVaildDataSourceCode(matchSettleCheckInfo, settleEventCodeEnum, sportId);
        if (CollectionUtils.isEmpty(dataSourceCodes)) {
            log.info("[PhaseScoreMismatchProcessor] sport:{} addSettleMention obtainData with settleEventId:{} dataSourceCodes is null!", sport, matchSettleCheckInfo.getSettleScoreEventId());
            return null;
        }
        MatchSettleCheckInfoExample checkInfoExample = new MatchSettleCheckInfoExample();
        checkInfoExample.createCriteria().andSettleScoreEventIdEqualTo(matchSettleCheckInfo.getSettleScoreEventId()).andDataSourceCodeIn(dataSourceCodes);
        List<MatchSettleCheckInfo> list = matchSettleCheckInfoMapper.selectByExample(checkInfoExample);

        //判断比分是否相同
        list = list.stream().filter(t->{
            if(!matchSettleCheckInfo.getT1().equals(t.getT1()) || !matchSettleCheckInfo.getT2().equals(t.getT2())) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());

        if(CollectionUtils.isEmpty(list)) {
            log.info("[PhaseScoreMismatchProcessor] sport:{} addSettleMention obtainData with settleEventId:{} data with different scores is null!", sport, matchSettleCheckInfo.getSettleScoreEventId());
            return null;
        }

        //赛果不匹配事件标记阶段比分
        Map<String, Integer> misMatchSettleNums = new HashMap<>();
        String parentSettleNum = FootballPeriodValidateEnum.getParentSettleNumList(settleNum);
        if (parentSettleNum != null && allMins15Codes.contains(parentSettleNum)) {
            MatchSettleScoreExample settleScoreExample = new MatchSettleScoreExample();
            settleScoreExample.createCriteria().andSettleNumEqualTo(parentSettleNum).andStandardMatchIdEqualTo(matchSettleCheckInfo.getStandardMatchId());
            List<MatchSettleScore> settleScores = matchSettleScoreMapper.selectByExample(settleScoreExample);
            if(!CollectionUtils.isEmpty(settleScores)) {
                misMatchSettleNums.put(String.valueOf(settleScores.get(0).getId()), CommonConstant.COMMON_TRUE_FLAG);
            }
        }
        misMatchSettleNums.put(String.valueOf(matchSettleCheckInfo.getSettleScoreEventId()), CommonConstant.COMMON_TRUE_FLAG);
        // 组装数据返回
        Map<String, Object> result = new HashMap<>();
        result.put("redisKey", redisKey(matchSettleCheckInfo.getStandardMatchId()));
        result.put("settleEventCodeEnum", settleEventCodeEnum);
        result.put("redisValue", misMatchSettleNums);
        log.info("[PhaseScoreMismatchProcessor] sport:{} addSettleMention obtainData with settleEventId:{} result:{} end!", sport, matchSettleCheckInfo.getSettleScoreEventId(), result);
        return result;
    }

    // 根据数据权重, 获取其他有效数据源
    private List<String> obtainVaildDataSourceCode(MatchSettleCheckInfo matchSettleCheckInfo, SettleEventCodeEnum settleEventCodeEnum, Long sportId) {
        // 获取数据源权重
        MatchSettleTemplate matchSettleTemplate = settleTemplateService.getTemplateByStandardMatchId(matchSettleCheckInfo.getStandardMatchId(), SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code);
        if (matchSettleTemplate == null) {
            return null;
        }
        List<DataSourceSettleWeightDto> dataSourceSettleWeightDtos = SettleTemplateJsonUtils.tansferDataSourceSettleWeightDtoList(matchSettleTemplate.getTemplateJson());
        List<String> dataSourceCodes = new ArrayList<>();
        if(1==sportId) {
            dataSourceCodes = dataSourceSettleWeightDtos.stream().map(t->{
                if (t.getDataSourceCode().equals(matchSettleCheckInfo.getDataSourceCode())) {
                    return null;
                }
                if ((settleEventCodeEnum==SettleEventCodeEnum.FOOTBALL_FA_CARD || settleEventCodeEnum==SettleEventCodeEnum.FOOTBALL_RED_CARD
                        || settleEventCodeEnum==SettleEventCodeEnum.FOOTBALL_YELLOW_CARD) && t.getBookingWeight() > 0 ||
                        settleEventCodeEnum==SettleEventCodeEnum.FOOTBALL_GOAL && t.getGoalWeight() > 0
                        || settleEventCodeEnum==SettleEventCodeEnum.FOOTBALL_CORNER && t.getCornerWeight() > 0) {
                    return t.getDataSourceCode();
                }
                return null;
            }).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        } else if (2==sportId) {
            dataSourceCodes = dataSourceSettleWeightDtos.stream().map(t->{
                if (t.getDataSourceCode().equals(matchSettleCheckInfo.getDataSourceCode())) {
                    return null;
                }
                if (settleEventCodeEnum==SettleEventCodeEnum.BASKETBALL_SCORE_CHANGE && t.getGoalWeight() > 0) {
                    return t.getDataSourceCode();
                }
                return null;
            }).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(dataSourceCodes)) {
            return null;
        }
        // 5/15分钟：数据不匹配校验不考虑 N01、N02、N03、LS
        if (sportId != null && sportId == 1) {
            dataSourceCodes = dataSourceCodes.stream()
                    .filter(code -> !DATA_SOURCE_5_15_IGNORE.contains(code))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(dataSourceCodes)) {
                return null;
            }
        }
        String sport = 1==sportId ? "Football" : "Basketball";
        log.info("[PhaseScoreMismatchProcessor] sport:{} addSettleMention obtainVaildDataSourceCode with settleEventId:{} dataSourceCodes:{} end!", sport, matchSettleCheckInfo.getSettleScoreEventId(), dataSourceCodes);
        return dataSourceCodes;
    }

    @Override
    protected SettleMentionEnum settleMention(){
        return SettleMentionEnum.FOOTBALL_PHASE_SCORE_MISMATCH;
    }

    @Override
    protected TypeReference<FootballMentionStatus> typeReference() {
        return new TypeReference<FootballMentionStatus>(){};
    }

 }
