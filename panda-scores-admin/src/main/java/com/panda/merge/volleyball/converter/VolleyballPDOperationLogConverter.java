package com.panda.merge.volleyball.converter;

import com.panda.merge.constant.VolleyballConstant;
import com.panda.merge.dto.advertise.v2.ChangeMatchPeriodV2Dto;
import com.panda.merge.dto.advertise.v2.DeleteEventV2Dto;
import com.panda.merge.snooker.dto.MatchCommonLogDto;
import com.panda.merge.util.CategoryUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

/**
 * 排球操作日志转换器：与 {@link com.panda.merge.snooker.converter.PDOperationLogConverter}
 * 结构对齐，但 changePeriod 的中英文标签走 {@link VolleyballConstant} 的局映射
 * （VOLLEYBALL_SET_BEGIN / VOLLEYBALL_SET_END），而非斯诺克的 SNOOKER_SET_BEGIN/END。
 * <p>
 * 当前 volleyball 只直接调用 convertChangePeriodToLog 与 convertDeleteEventToLog；
 * 其它 log（changeScore/changeMatchStatus/changeMatchLength）仍由父类
 * AbsMatchCommonProcessor 注入的斯诺克 PDOperationLogConverter 处理 —— 它们与局编号无关，
 * 不存在体育种类差异。
 */
@Mapper(componentModel = "spring", imports = {VolleyballConstant.class, CategoryUtils.class})
@Component
public interface VolleyballPDOperationLogConverter {

    @Mapping(target = "beforeVal",
            expression = "java(VolleyballPDOperationLogConverter.getChangePeriodLog(changeMatchPeriodV2Dto.getPeriodId(), true))")
    @Mapping(target = "afterVal",
            expression = "java(VolleyballPDOperationLogConverter.getChangePeriodLog(changeMatchPeriodV2Dto.getPeriodId(), false))")
    MatchCommonLogDto convertChangePeriodToLog(ChangeMatchPeriodV2Dto changeMatchPeriodV2Dto);

    @Mapping(target = "eventCode", constant = "deleteEvent")
    MatchCommonLogDto convertDeleteEventToLog(DeleteEventV2Dto deleteEventDto, String beforeVal, String afterVal);

    /**
     * 阶段变更日志的 修改前/修改后：
     * - VOLLEYBALL_SET_BEGIN：before=SPLIT_LINE，after="SET<n>开始 && SET<n> Begin"
     * - VOLLEYBALL_SET_END：before="SET<n>"，after="SET<n>结束 && SET<n> END"
     * - 80L（中断）：before=SPLIT_LINE，after="比赛中断 && Match Suspension"
     * - 999L（结束）：before=SPLIT_LINE，after="比赛结束 && Match Ended"
     * - 其它：before="Set<id>"，after="Set<id>结束 && Set<id> END"
     */
    static String getChangePeriodLog(Long periodId, boolean isBefore) {
        String beforeVal = CategoryUtils.SPLIT_LINE;
        String afterVal = CategoryUtils.SPLIT_LINE;
        if (periodId == null) return isBefore ? beforeVal : afterVal;
        if (VolleyballConstant.VOLLEYBALL_SET_BEGIN.containsKey(periodId)) {
            int setNum = VolleyballConstant.VOLLEYBALL_SET_BEGIN.get(periodId);
            afterVal = "SET" + setNum + "开始" + "&&" + "SET" + setNum + " Begin";
        } else if (VolleyballConstant.VOLLEYBALL_SET_END.containsKey(periodId)) {
            int setNum = VolleyballConstant.VOLLEYBALL_SET_END.get(periodId);
            beforeVal = "SET" + setNum;
            afterVal = "SET" + setNum + "结束" + "&&" + "SET" + setNum + " END";
        } else if (VolleyballConstant.PERIOD_SUSPENDED.equals(periodId)) {
            afterVal = "比赛中断" + "&&" + "Match Suspension";
        } else if (VolleyballConstant.PERIOD_MATCH_END.equals(periodId)) {
            afterVal = "比赛结束" + "&&" + "Match Ended";
        } else {
            beforeVal = "Set" + periodId;
            afterVal = "Set" + periodId + "结束" + "&&" + "Set" + periodId + " END";
        }
        return isBefore ? beforeVal : afterVal;
    }
}
