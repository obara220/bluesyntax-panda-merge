package com.panda.merge.snooker.converter;

import com.panda.merge.constant.SnookerConstant;
import com.panda.merge.dto.advertise.v2.*;
import com.panda.merge.snooker.dto.MatchCommonLogDto;
import com.panda.merge.util.CategoryUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring",imports = {SnookerConstant.class, CategoryUtils.class})
@Component
public interface PDOperationLogConverter {

    MatchCommonLogDto convertChangeMatchStatusToLog(ChangeMatchStatusV2Dto changeMatchStatusV2Dto);

    MatchCommonLogDto convertChangeScoreToLog(ChangeMatchScoreV2Dto changeMatchScoreV2Dto, String beforeVal, String afterVal);
    @Mapping(target = "eventCode", constant = "changeMatchLength")
    MatchCommonLogDto convertChangeMatchLengthToLog(ChangeMatchLengthV2Dto changeMatchLengthDto, String beforeVal, String afterVal);

    @Mapping(target = "beforeVal", expression = "java(PDOperationLogConverter.getChangePeriodLog(changeMatchPeriodV2Dto.getPeriodId(), true))")
    @Mapping(target = "afterVal", expression = "java(PDOperationLogConverter.getChangePeriodLog(changeMatchPeriodV2Dto.getPeriodId(), false))")
    MatchCommonLogDto convertChangePeriodToLog(ChangeMatchPeriodV2Dto changeMatchPeriodV2Dto);

    @Mapping(target = "howAway", source = "whoKickOff")
    @Mapping(target = "beforeVal", expression = "java(CategoryUtils.SPLIT_LINE)")
    @Mapping(target = "afterVal", expression = "java(PDOperationLogConverter.kickOffOrRerackAfterVal(kickOffV2Dto.getPeriodId(), true))")
    @Mapping(target = "eventCode", constant = "which_team_serves_first")
    MatchCommonLogDto convertKickOffToLog(KickOffV2Dto kickOffV2Dto);

    @Mapping(target = "beforeVal", expression = "java(CategoryUtils.SPLIT_LINE)")
    @Mapping(target = "afterVal", expression = "java(PDOperationLogConverter.kickOffOrRerackAfterVal(rerackV2Dto.getPeriodId(), false))")
    @Mapping(target = "eventCode", constant = "rerack")
    MatchCommonLogDto convertRerackToLog(RerackV2Dto rerackV2Dto);

    @Mapping(target = "eventCode", constant = "deleteEvent")
    MatchCommonLogDto convertDeleteEventToLog(DeleteEventV2Dto deleteEventDto, String beforeVal, String afterVal);

    /**
     * 开球/重排日志文案：periodId 为业务阶段号（如 8=第一局、442=第七局），展示须用 SNOOKER_SET_BEGIN 映射为 SET1、SET7…
     * 不能直接用 periodId 拼接为 Set8。
     */
    static String kickOffOrRerackAfterVal(Long periodId, boolean kickOff) {
        String cn = kickOff ? " 开球" : " 重新排球";
        String en = kickOff ? " Kick Off" : " Rerack";
        if (periodId == null) {
            return cn.trim() + "&&" + en.trim();
        }
        if (SnookerConstant.SNOOKER_SET_BEGIN.containsKey(periodId)) {
            int setNum = SnookerConstant.SNOOKER_SET_BEGIN.get(periodId);
            return "SET" + setNum + cn + "&&" + "SET" + setNum + en;
        }
        return "Set" + periodId + cn + "&&" + "Set" + periodId + en;
    }

    /** 阶段变更日志的 修改前/修改后，与需求表一致：SET1、SET1开始、SET1结束、小局休息 */
    static String getChangePeriodLog(Long periodId, boolean isBefore) {
        String beforeVal = CategoryUtils.SPLIT_LINE;
        String afterVal = CategoryUtils.SPLIT_LINE;
        if (periodId == null) return isBefore ? beforeVal : afterVal;
        if (SnookerConstant.SNOOKER_SET_BEGIN.containsKey(periodId)) {
            int setNum = SnookerConstant.SNOOKER_SET_BEGIN.get(periodId);
            beforeVal = CategoryUtils.SPLIT_LINE;
            afterVal = "SET" + setNum + "开始" + "&&" + "SET" + setNum + " Begin";
        } else if (SnookerConstant.SNOOKER_SET_END.containsKey(periodId)) {
            int setNum = SnookerConstant.SNOOKER_SET_END.get(periodId);
            beforeVal = "SET" + setNum;
            afterVal = "SET" + setNum + "结束" + "&&" + "SET" + setNum + " END";
        } else if (Long.valueOf(445L).equals(periodId)) {
            afterVal = "小局休息" + "&&" + "Small Break";
        } else {
            beforeVal = "Set" + periodId;
            afterVal = "Set" + periodId + "结束" + "&&" + "Set" + periodId + " END";
        }
        return isBefore ? beforeVal : afterVal;
    }
}
