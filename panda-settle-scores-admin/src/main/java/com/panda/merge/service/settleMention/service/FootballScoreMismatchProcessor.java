package com.panda.merge.service.settleMention.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.panda.merge.constant.*;
import com.panda.merge.dto.DataSourceSettleWeightDto;
import com.panda.merge.filter.football.impl.MatchScoresSettleInitChainFilter;
import com.panda.merge.mapper.MatchSettleCheckInfoMapper;
import com.panda.merge.model.*;
import com.panda.merge.service.ISettleTemplateService;
import com.panda.merge.service.settleMention.dto.FootballMentionStatus;
import com.panda.merge.utils.FootBallMatchSettleScoreUtils;
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
public class FootballScoreMismatchProcessor extends AbstractFootballProcessor<FootballMentionStatus>{

    @Resource
    protected ISettleTemplateService settleTemplateService;

    @Resource
    protected MatchSettleCheckInfoMapper matchSettleCheckInfoMapper;

    @Resource
    protected MatchScoresSettleInitChainFilter matchScoresSettleInitChainFilter;

    /** 5/15分钟次序数据不匹配不考虑的数据源（不参与可用数据源、不触发等待） */
    private static final Set<String> DATA_SOURCE_5_15_IGNORE = new HashSet<>(Arrays.asList("N01", "N02", "N03", "LS"));

    @Override
    protected Object obtainData(Map<String, Object> parameters) {
        MatchSettleEvent matchSettleEvent = (MatchSettleEvent) parameters.get("matchSettleEvent");
        MatchEventInfo matchEventInfo = (MatchEventInfo) parameters.get("matchEventInfo");
        // 5/15分钟：N01、N02、N03、LS 不参与次序数据不匹配校验
        if (matchSettleEvent != null && DATA_SOURCE_5_15_IGNORE.contains(matchEventInfo.getDataSourceCode())) {
            log.info("[FootballScoreMismatchProcessor] addSettleMention ignore data source {} for score mismatch", matchSettleEvent.getDataSourceCode());
            return null;
        }
        log.info("[FootballScoreMismatchProcessor] addSettleMention obtainData with settleEventId:{} start!", matchSettleEvent.getId());
        SettleEventCodeEnum settleEventCodeEnum = SettleEventCodeEnum.getEventCodeEnum(matchSettleEvent.getEventCode());
        if (settleEventCodeEnum == null) {
            log.info("[FootballScoreMismatchProcessor] addSettleMention obtainData with settleEventId:{} settleEventCodeEnum is null!", matchSettleEvent.getId());
            return null;
        }
        // 获取不同数据源但同次序数据
        List<String> dataSourceCodes = obtainVaildDataSourceCode(matchSettleEvent, settleEventCodeEnum);
        if (CollectionUtils.isEmpty(dataSourceCodes)) {
            log.info("[FootballScoreMismatchProcessor] addSettleMention obtainData with settleEventId:{} dataSourceCodes is null!", matchSettleEvent.getId());
            return null;
        }
        MatchSettleCheckInfoExample checkInfoExample = new MatchSettleCheckInfoExample();
        checkInfoExample.createCriteria().andSettleScoreEventIdEqualTo(matchSettleEvent.getId()).andDataSourceCodeIn(dataSourceCodes);
        List<MatchSettleCheckInfo> list = matchSettleCheckInfoMapper.selectByExample(checkInfoExample);
        //判断比分或5分钟是否不同
        String fiveMinSection;
        if (matchEventInfo.getSecondsFromStart() != null && matchEventInfo.getMatchPeriodId() != null) {
            Long period5 = FootBallMatchSettleScoreUtils.get5MinPeriod(matchEventInfo.getMatchPeriodId(), matchEventInfo.getSecondsFromStart());
            fiveMinSection = period5 == null ? null : String.valueOf(period5);
            if(matchSettleEvent.getEventCode().equals("corner")||matchSettleEvent.getEventCode().equals("fa_card")||matchSettleEvent.getEventCode().equals("yellow_card")||matchSettleEvent.getEventCode().equals("red_card")){
                //1.计算出角球15分钟区间
                //2.设置15分钟区间
                Long period15 = SportPeriodConstant.FootballPeriod.get15MinPeriod(matchEventInfo.getMatchPeriodId(), matchEventInfo.getSecondsFromStart());
                fiveMinSection = period15 == null ? null : String.valueOf(period15);
            }
        } else {
            fiveMinSection = null;
        }
        log.info("[FootballScoreMismatchProcessor] addSettleMention obtainData with settleEventId:{} fiveMinSection:{}", matchSettleEvent.getId(), fiveMinSection);
        // 如果任何一个checkInfo的比分（t1, t2）或5分钟（FiveMinSection）与matchSettleEvent不同，则认为是数据不匹配
        if(!CollectionUtils.isEmpty(list)) {
            String finalFiveMinSection = fiveMinSection;
            list = list.stream().filter(t->{
                // 检查比分t1是否不同
                boolean t1Different = (matchSettleEvent.getT1() == null && t.getT1() != null) ||
                        (matchSettleEvent.getT1() != null && !matchSettleEvent.getT1().equals(t.getT1()));

                // 检查比分t2是否不同
                boolean t2Different = (matchSettleEvent.getT2() == null && t.getT2() != null) ||
                        (matchSettleEvent.getT2() != null && !matchSettleEvent.getT2().equals(t.getT2()));

                // 检查5分钟是否不同
                boolean fiveMinDifferent = false;
                if (matchSettleEvent.getEventType() == 3) {
                    fiveMinDifferent = finalFiveMinSection != null && t.getFiveMinSection() != null && !finalFiveMinSection.equals(t.getFiveMinSection());
                }
                // 如果比分或5分钟有任何不同，返回true（表示数据不匹配）
                return t1Different || t2Different || fiveMinDifferent;
            }).collect(Collectors.toList());
        }


        if(CollectionUtils.isEmpty(list)) {
            log.info("[FootballScoreMismatchProcessor] addSettleMention obtainData with settleEventId:{} data with different scores is null!", matchSettleEvent.getId());
            return null;
        }

        // 查找阶段比分
        List<String> deleteEventMap = new ArrayList<>();
        //赛果不匹配事件标记阶段比分
        matchScoresSettleInitChainFilter.deleteEventPeriodScorefilter(matchEventInfo, deleteEventMap);
        Map<String, Integer> misMatchSettleNums = getScoreIdsBySettleNums(matchSettleEvent.getStandardMatchId(), deleteEventMap);
        misMatchSettleNums.put(String.valueOf(matchSettleEvent.getId()), CommonConstant.COMMON_TRUE_FLAG);
//
//        // 计算下一个阶段的 matchSettleScoreId，并直接加入到 detailStatus 中
//        // 获取所有当前阶段的 matchSettleScoreId（排除 matchSettleEvent.getId()）
//        List<String> currentScoreIds = new ArrayList<>();
//        for (String key : misMatchSettleNums.keySet()) {
//            try {
//                Long.parseLong(key); // 验证是否为数字ID
//                currentScoreIds.add(key);
//            } catch (NumberFormatException e) {
//                // 跳过非数字ID（如 matchSettleEvent.getId() 可能是字符串格式）
//            }
//        }
//
//        // 为每个当前阶段计算下一个阶段，并直接加入到 detailStatus 中
//        for (String currentScoreId : currentScoreIds) {
//            List<String> nextPhaseScoreIds = getNextPhaseScoreIds(matchSettleEvent.getStandardMatchId(), currentScoreId);
//            if (!CollectionUtils.isEmpty(nextPhaseScoreIds)) {
//                // 将下一个阶段直接加入到 misMatchSettleNums 中，确保前端能正确显示
//                for (String nextScoreId : nextPhaseScoreIds) {
//                    misMatchSettleNums.put(nextScoreId, CommonConstant.COMMON_TRUE_FLAG);
//                }
//            }
//        }
        
        // 组装数据返回
        Map<String, Object> result = new HashMap<>();
        result.put("redisKey", redisKey(matchSettleEvent.getStandardMatchId()));
        result.put("settleEventCodeEnum", settleEventCodeEnum);
        result.put("redisValue", misMatchSettleNums);
        log.info("[FootballScoreMismatchProcessor] addSettleMention obtainData with settleEventId:{} result:{} end!", matchSettleEvent.getId(), result);
        return result;
    }

    // 根据数据权重, 获取其他有效数据源
    private List<String> obtainVaildDataSourceCode(MatchSettleEvent matchSettleEvent, SettleEventCodeEnum settleEventCodeEnum) {
        // 获取数据源权重
        MatchSettleTemplate matchSettleTemplate = settleTemplateService.getTemplateByStandardMatchId(matchSettleEvent.getStandardMatchId(), SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code);
        if (matchSettleTemplate == null) {
            return null;
        }
        List<DataSourceSettleWeightDto> dataSourceSettleWeightDtos = SettleTemplateJsonUtils.tansferDataSourceSettleWeightDtoList(matchSettleTemplate.getTemplateJson());

        List<String> dataSourceCodes = dataSourceSettleWeightDtos.stream().map(t->{
            if (t.getDataSourceCode().equals(matchSettleEvent.getDataSourceCode())) {
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
        if (CollectionUtils.isEmpty(dataSourceCodes)) {
            return null;
        }
        // 5/15分钟：次序数据不匹配不考虑 N01、N02、N03、LS
        dataSourceCodes = dataSourceCodes.stream()
                .filter(code -> !DATA_SOURCE_5_15_IGNORE.contains(code))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(dataSourceCodes)) {
            return null;
        }
        log.info("[FootballScoreMismatchProcessor] addSettleMention obtainVaildDataSourceCode with settleEventId:{} dataSourceCodes:{} end!", matchSettleEvent.getId(), dataSourceCodes);
        return dataSourceCodes;
    }

    @Override
    protected SettleMentionEnum settleMention(){
        return SettleMentionEnum.FOOTBALL_SCORE_MISMATCH;
    }

    @Override
    protected TypeReference<FootballMentionStatus> typeReference() {
        return new TypeReference<FootballMentionStatus>(){};
    }

 }
