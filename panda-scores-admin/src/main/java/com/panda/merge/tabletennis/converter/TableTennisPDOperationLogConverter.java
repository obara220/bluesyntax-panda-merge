package com.panda.merge.tabletennis.converter;

import com.panda.merge.constant.TableTennisConstant;
import com.panda.merge.dto.advertise.v2.ChangeMatchPeriodV2Dto;
import com.panda.merge.dto.advertise.v2.DeleteEventV2Dto;
import com.panda.merge.snooker.dto.MatchCommonLogDto;
import com.panda.merge.util.CategoryUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

/**
 * 乒乓球操作日志转换器：changePeriod 的中英文标签走 {@link TableTennisConstant} 的局映射
 * （TABLE_TENNIS_SET_BEGIN / TABLE_TENNIS_SET_END），而非斯诺克/排球的映射。
 */
@Mapper(componentModel = "spring", imports = {TableTennisConstant.class, CategoryUtils.class})
@Component
public interface TableTennisPDOperationLogConverter {

    @Mapping(target = "eventCode", constant = "deleteEvent")
    MatchCommonLogDto convertDeleteEventToLog(DeleteEventV2Dto deleteEventDto, String beforeVal, String afterVal);

    /**
     * 阶段变更日志的 修改前/修改后：
     * - TABLE_TENNIS_SET_BEGIN：before=SPLIT_LINE，after="SET<n>开始 && SET<n> Begin"
     * - TABLE_TENNIS_SET_END：before="SET<n>"，after="SET<n>结束 && SET<n> END"
     * - 80L（中断）：before=SPLIT_LINE，after="比赛中断 && Match Suspension"
     * - 999L（结束）：before=SPLIT_LINE，after="比赛结束 && Match Ended"
     * - 其它：before="Set<id>"，after="Set<id>结束 && Set<id> END"
     */
    static String getChangePeriodLog(Long periodId, boolean isBefore) {
        String beforeVal = CategoryUtils.SPLIT_LINE;
        String afterVal = CategoryUtils.SPLIT_LINE;
        if (periodId == null) return isBefore ? beforeVal : afterVal;
        if (TableTennisConstant.TABLE_TENNIS_SET_BEGIN.containsKey(periodId)) {
            int setNum = TableTennisConstant.TABLE_TENNIS_SET_BEGIN.get(periodId);
            afterVal = "SET" + setNum + "开始" + "&&" + "SET" + setNum + " Begin";
        } else if (TableTennisConstant.TABLE_TENNIS_SET_END.containsKey(periodId)) {
            int setNum = TableTennisConstant.TABLE_TENNIS_SET_END.get(periodId);
            beforeVal = "SET" + setNum;
            afterVal = "SET" + setNum + "结束" + "&&" + "SET" + setNum + " END";
        } else if (TableTennisConstant.PERIOD_SUSPENDED.equals(periodId)) {
            afterVal = "比赛中断" + "&&" + "Match Suspension";
        } else if (TableTennisConstant.PERIOD_MATCH_END.equals(periodId)) {
            afterVal = "比赛结束" + "&&" + "Match Ended";
        } else {
            beforeVal = "Set" + periodId;
            afterVal = "Set" + periodId + "结束" + "&&" + "Set" + periodId + " END";
        }
        return isBefore ? beforeVal : afterVal;
    }

    @Mapping(target = "beforeVal",
            expression = "java(TableTennisPDOperationLogConverter.getChangePeriodLog(changeMatchPeriodV2Dto.getPeriodId(), true))")
    @Mapping(target = "afterVal",
            expression = "java(TableTennisPDOperationLogConverter.getChangePeriodLog(changeMatchPeriodV2Dto.getPeriodId(), false))")
    MatchCommonLogDto convertChangePeriodToLog(ChangeMatchPeriodV2Dto changeMatchPeriodV2Dto);
}
