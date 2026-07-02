package com.panda.merge.constant.converter;

import com.panda.merge.dto.settle.BasketballMentionQueryDto;
import com.panda.merge.dto.settle.MentionQueryDto;
import com.panda.merge.service.settleMention.dto.BasketballMentionStatus;
import com.panda.merge.service.settleMention.dto.FootballMentionStatus;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


//@Mapper(componentModel = "spring", imports = {ThirdSportMarketOdds.class, OddsWrapper.class})
@Mapper(componentModel = "spring")
@Component
public interface SettleMentionConverter {

    default MentionQueryDto.FootballMentionStatus convertFootballMentionStatus(FootballMentionStatus footballMentionStatus) {
        if (footballMentionStatus == null) {
            return null;
        }
        MentionQueryDto.FootballMentionStatus result = new MentionQueryDto.FootballMentionStatus();
        if (footballMentionStatus.getGoalStatus() != null) {
            result.setGoalStatus(convertEventStatus(footballMentionStatus.getGoalStatus()));
        }
        if (footballMentionStatus.getFacardStatus() != null) {
            result.setFacardStatus(convertEventStatus(footballMentionStatus.getFacardStatus()));
        }
        if (footballMentionStatus.getCornerStatus() != null) {
            result.setCornerStatus(convertEventStatus(footballMentionStatus.getCornerStatus()));
        }
        return result;
    }
    
    default MentionQueryDto.EventStatus convertEventStatus(com.panda.merge.service.settleMention.dto.AbstractMentionStatus.EventStatus eventStatus) {
        if (eventStatus == null) {
            return null;
        }
        MentionQueryDto.EventStatus result = new MentionQueryDto.EventStatus();
        result.setStatus(eventStatus.getStatus());
        result.setDetailStatus(eventStatus.getDetailStatus());
        return result;
    }
    
    default BasketballMentionQueryDto.BasketballMentionStatus convertBasketballMentionStatus(BasketballMentionStatus basketballMentionStatus) {
        if (basketballMentionStatus == null) {
            return null;
        }
        BasketballMentionQueryDto.BasketballMentionStatus result = new BasketballMentionQueryDto.BasketballMentionStatus();
        if (basketballMentionStatus.getGoalStatus() != null) {
            result.setGoalStatus(convertBasketballEventStatus(basketballMentionStatus.getGoalStatus()));
        }
        return result;
    }
    
    default BasketballMentionQueryDto.EventStatus convertBasketballEventStatus(com.panda.merge.service.settleMention.dto.AbstractMentionStatus.EventStatus eventStatus) {
        if (eventStatus == null) {
            return null;
        }
        BasketballMentionQueryDto.EventStatus result = new BasketballMentionQueryDto.EventStatus();
        result.setStatus(eventStatus.getStatus());
        // Basketball 的 detailStatus 仍然是 Map<String, Integer>，需要转换
        if (eventStatus.getDetailStatus() != null) {
            Map<String, Integer> detailStatus = new HashMap<>();
            for (Map.Entry<String, Object> entry : eventStatus.getDetailStatus().entrySet()) {
                Object value = entry.getValue();
                if (value instanceof Integer) {
                    detailStatus.put(entry.getKey(), (Integer) value);
                } else if (value instanceof Map) {
                    // 如果是 Map 对象，提取 status 值
                    Map<String, Object> valueMap = (Map<String, Object>) value;
                    Object statusValue = valueMap.get("status");
                    if (statusValue instanceof Integer) {
                        detailStatus.put(entry.getKey(), (Integer) statusValue);
                    }
                }
            }
            result.setDetailStatus(detailStatus);
        }
        return result;
    }
//
//    @Mappings({
//            @Mapping(source = "matchMarketStatus", target = "operateMatchStatus"),
//            @Mapping(source = "standrdOutrightMatchEndTime", target = "beginTime"),
//            @Mapping(expression = "java(1)", target = "matchType")
//    })
//    List<StandardMatchInfoDetail> convertOutrightToStandardDetails(List<StandardOutrightMatchInfo> standardOutrightMatchInfos);
//
//    @Mapping(expression = "java(0)", target = "matchType")
//    StandardMatchInfoDetail convertstandardToStandardDetails(StandardMatchInfo standardMatchInfos);
//
//    @Mapping(expression = "java(0)", target = "matchType")
//    List<StandardMatchInfoDetail> convertstandardToStandardDetails(List<StandardMatchInfo> standardMatchInfos);
//
//
//    @Mappings({
//            @Mapping(source = "id", target = "matchInfoId"),
//            @Mapping(source = "dataSourceCode", target = "preMatchDataProviderCode"),
//            @Mapping(source = "sellStatus", target = "preMatchSellStatus")
//    })
//    StandardSportMarketSell convertOutrightToStandardSportMarketSell(StandardOutrightMatchInfo standardOutrightMatchInfo);
}
