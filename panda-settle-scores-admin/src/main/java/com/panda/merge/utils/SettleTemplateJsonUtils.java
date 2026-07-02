package com.panda.merge.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.*;
import com.panda.merge.dto.settle.MatchSettleDataSourceAllWeightDto;
import com.panda.merge.dto.settle.MatchSettleDataSourceWeightConfigDto;
import com.panda.merge.dto.settle.TemplateListSearchDto;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Data
public class SettleTemplateJsonUtils {
    public static Map<Integer,List<String>> CN_LEVEL =new HashMap<>();
    static {
        CN_LEVEL.put(1, Arrays.asList(new String[]{"一级","1st"}));
        CN_LEVEL.put(2, Arrays.asList(new String[]{"二级","2st"}));
        CN_LEVEL.put(3, Arrays.asList(new String[]{"三级","3st"}));
        CN_LEVEL.put(4, Arrays.asList(new String[]{"四级","4st"}));
        CN_LEVEL.put(5, Arrays.asList(new String[]{"五级","5st"}));
        CN_LEVEL.put(6, Arrays.asList(new String[]{"六级","6st"}));
        CN_LEVEL.put(7, Arrays.asList(new String[]{"七级","7st"}));
        CN_LEVEL.put(8, Arrays.asList(new String[]{"八级","8st"}));
        CN_LEVEL.put(9, Arrays.asList(new String[]{"九级","9st"}));
        CN_LEVEL.put(10, Arrays.asList(new String[]{"十级","10st"}));
        CN_LEVEL.put(11, Arrays.asList(new String[]{"十一级","11st"}));
        CN_LEVEL.put(12, Arrays.asList(new String[]{"十二级","12st"}));
        CN_LEVEL.put(13, Arrays.asList(new String[]{"十三级","13st"}));
        CN_LEVEL.put(14, Arrays.asList(new String[]{"十四级","14st"}));
        CN_LEVEL.put(15, Arrays.asList(new String[]{"十五级","15st"}));
    }

    public static List<GrayAreaSettleDto> tansferGrayAreaList(String jsonArray){
        JSONArray array = JSONArray.parseArray(jsonArray);
        List<GrayAreaSettleDto> list =new ArrayList<>();
        for (Object o : array) {
            GrayAreaSettleDto grayAreaSettleDto = JSONObject.toJavaObject((JSONObject)o,GrayAreaSettleDto.class);
            list.add(grayAreaSettleDto);
        }
        return list;
    }
    public static List<DownSettleDto> tansferDownList(String jsonArray){
        JSONArray array = JSONArray.parseArray(jsonArray);
        List<DownSettleDto> list =new ArrayList<>();
        for (Object o : array) {
            DownSettleDto sownSettleDto = JSONObject.toJavaObject((JSONObject)o,DownSettleDto.class);
            list.add(sownSettleDto);
        }
        return list;
    }

    public static List<DataSourceSettleWeightDto> tansferDataSourceSettleWeightDtoList(String jsonArray){
        JSONArray array = JSONArray.parseArray(jsonArray);
        List<DataSourceSettleWeightDto> list =new ArrayList<>();
        for (Object o : array) {
            DataSourceSettleWeightDto dataSourceSettleWeightDto = JSONObject.toJavaObject((JSONObject)o,DataSourceSettleWeightDto.class);
            list.add(dataSourceSettleWeightDto);
        }
        return list;
    }

    public static List<DataSourceSettleWeightAndConfigDto> dataSourceSettleWeightAndConfigDto(String jsonArray){
        JSONArray array = JSONArray.parseArray(jsonArray);
        List<DataSourceSettleWeightAndConfigDto> list =new ArrayList<>();
        for (Object o : array) {
            DataSourceSettleWeightAndConfigDto dataSourceSettleWeightDto = JSONObject.toJavaObject((JSONObject)o,DataSourceSettleWeightAndConfigDto.class);
            list.add(dataSourceSettleWeightDto);
        }
        return list;
    }
    public static List<GrayAreaSettleAndConfigDto> dataSourceSettleGrayAndConfigDto(String jsonArray){
        JSONArray array = JSONArray.parseArray(jsonArray);
        List<GrayAreaSettleAndConfigDto> list =new ArrayList<>();
        for (Object o : array) {
            GrayAreaSettleAndConfigDto grayAreaSettleAndConfigDto = JSONObject.toJavaObject((JSONObject)o,GrayAreaSettleAndConfigDto.class);
            list.add(grayAreaSettleAndConfigDto);
        }
        return list;
    }
    public static List<MatchSettleDataSourceAllWeightDto> tansferMatchSettleDataSourceAllWeightDtoList(String jsonArray){
        JSONArray array = JSONArray.parseArray(jsonArray);
        List<MatchSettleDataSourceAllWeightDto> list =new ArrayList<>();
        for (Object o : array) {
            MatchSettleDataSourceAllWeightDto matchSettleDataSourceAllWeightDto = JSONObject.toJavaObject((JSONObject)o,MatchSettleDataSourceAllWeightDto.class);
            list.add(matchSettleDataSourceAllWeightDto);
        }
        return list;
    }
    public static List<MatchSettleDataSourceWeightConfigDto> tansferMatchSettleDataSourceWeightConfigDtoList(String jsonArray){
        JSONArray array = JSONArray.parseArray(jsonArray);
        List<MatchSettleDataSourceWeightConfigDto> list =new ArrayList<>();
        for (Object o : array) {
            MatchSettleDataSourceWeightConfigDto matchSettleDataSourceWeightConfigDto = JSONObject.toJavaObject((JSONObject)o,MatchSettleDataSourceWeightConfigDto.class);
            list.add(matchSettleDataSourceWeightConfigDto);
        }
        return list;
    }

    public static void main(String[] arr){
     String json= "[{\"dataSourceCode\":\"BG\",\"goal15Min\":10,\"corner15Min\":20,\"booking15Min\":20,\"goal5Min\":10},{\"dataSourceCode\":\"KO\",\"goal15Min\":10,\"corner15Min\":20,\"booking15Min\":20,\"goal5Min\":10},{\"dataSourceCode\":\"RB\",\"goal15Min\":10,\"corner15Min\":20,\"booking15Min\":20,\"goal5Min\":10},{\"dataSourceCode\":\"TS\",\"goal15Min\":15,\"corner15Min\":30,\"booking15Min\":30,\"goal5Min\":15},{\"dataSourceCode\":\"LS\",\"goal15Min\":15,\"corner15Min\":30,\"booking15Min\":30,\"goal5Min\":15},{\"dataSourceCode\":\"BT\",\"goal15Min\":15,\"corner15Min\":30,\"booking15Min\":30,\"goal5Min\":15},{\"dataSourceCode\":\"BC\",\"goal15Min\":15,\"corner15Min\":30,\"booking15Min\":30,\"goal5Min\":15},{\"dataSourceCode\":\"PD\",\"goal15Min\":15,\"corner15Min\":30,\"booking15Min\":30,\"goal5Min\":15},{\"dataSourceCode\":\"PD2\",\"goal15Min\":15,\"corner15Min\":30,\"booking15Min\":30,\"goal5Min\":15}]";
        List<GrayAreaSettleDto> list=tansferGrayAreaList(json);
        System.out.println(list);

    }

    public static void transforListKeyWord(TemplateListSearchDto templateListSearchDto) {
        //"一级" 改为联赛等级为 1 "二级改为" 联赛等级是2 依次类推         //繁体 转化
        if(StringUtils.isNotEmpty(templateListSearchDto.getDataSourceWeight())){
            for (Map.Entry<Integer, List<String>> e : CN_LEVEL.entrySet()) {
                if(templateListSearchDto.getDataSourceWeight().contains(e.getValue().get(0))||templateListSearchDto.getDataSourceWeight().contains(e.getValue().get(1))){
                    templateListSearchDto.setDataSourceWeightLevel(e.getKey());
                }
            }
        }
        if(StringUtils.isNotEmpty(templateListSearchDto.getGrayAreaSet())){
            for (Map.Entry<Integer, List<String>> e : CN_LEVEL.entrySet()) {
                if(templateListSearchDto.getGrayAreaSet().contains(e.getValue().get(0))||templateListSearchDto.getGrayAreaSet().contains(e.getValue().get(1))){
                    templateListSearchDto.setGrayAreaSetLevel(e.getKey());
                }
            }
        }
        if(StringUtils.isNotEmpty(templateListSearchDto.getCountDown())){
            for (Map.Entry<Integer, List<String>> e : CN_LEVEL.entrySet()) {
                if(templateListSearchDto.getCountDown().contains(e.getValue().get(0))||templateListSearchDto.getCountDown().contains(e.getValue().get(1))){
                    templateListSearchDto.setCountDownLevel(e.getKey());
                }
            }
        }

    }

}
