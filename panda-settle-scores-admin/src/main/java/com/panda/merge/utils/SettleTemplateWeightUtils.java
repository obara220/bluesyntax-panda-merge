package com.panda.merge.utils;

import com.panda.merge.dto.DataSourceSettleWeightDto;
import com.panda.merge.model.MatchSettleCheckInfo;
import com.panda.merge.model.MatchSettleTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class SettleTemplateWeightUtils {

    public static Integer countFootballWeightDataSourceCheck(MatchSettleTemplate matchSettleTemplate, List<MatchSettleCheckInfo> value) {
        String eventCode = value.get(0).getEventCode();
        List<DataSourceSettleWeightDto> dataSourceSettleWeightDtos = SettleTemplateJsonUtils.tansferDataSourceSettleWeightDtoList(matchSettleTemplate.getTemplateJson());
        Integer sum =0;
        Map<String,Integer>  map= null;
        if(eventCode.equals("corner")) {
            map = dataSourceSettleWeightDtos.stream().collect(Collectors.toMap(DataSourceSettleWeightDto::getDataSourceCode, DataSourceSettleWeightDto::getCornerWeight));
        }else if(eventCode.equals("goal")||eventCode.equals("kick_off")){
            map = dataSourceSettleWeightDtos.stream().collect(Collectors.toMap(DataSourceSettleWeightDto::getDataSourceCode, DataSourceSettleWeightDto::getGoalWeight));
        }else {
            map = dataSourceSettleWeightDtos.stream().collect(Collectors.toMap(DataSourceSettleWeightDto::getDataSourceCode, DataSourceSettleWeightDto::getBookingWeight));
        }
        for (MatchSettleCheckInfo matchSettleCheckInfo : value) {
            Integer weight = map.get(matchSettleCheckInfo.getDataSourceCode());
            if(weight!=null){
                sum+=weight;
                //人工录入为PA
            }else if(matchSettleCheckInfo.getDataSourceCode().equals("PA")){
                sum+=90;
            }
        }
        return sum;
    }
    public static Integer countBasketballWeightDataSourceCheck(MatchSettleTemplate matchSettleTemplate, List<MatchSettleCheckInfo> value) {
        List<DataSourceSettleWeightDto> dataSourceSettleWeightDtos = SettleTemplateJsonUtils.tansferDataSourceSettleWeightDtoList(matchSettleTemplate.getTemplateJson());
        Integer sum =0;
        Map<String,Integer> map = dataSourceSettleWeightDtos.stream().collect(Collectors.toMap(DataSourceSettleWeightDto::getDataSourceCode, DataSourceSettleWeightDto::getGoalWeight));
        for (MatchSettleCheckInfo matchSettleCheckInfo : value) {
            Integer weight = map.get(matchSettleCheckInfo.getDataSourceCode());
            if(weight!=null){
                sum+=weight;
                //人工录入为PA
            }else if(matchSettleCheckInfo.getDataSourceCode().equals("PA")){
                sum+=90;
            }
        }
        return sum;
    }
}
