package com.panda.merge.utils;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.dto.BaseballScores;
import com.panda.merge.dto.CommonItem;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static com.panda.merge.constant.DataSourceConstant.WHOLE_MATCH;

/**
 * 棒球比分处理
 */
public class BaseBallScoresUtils {
    /**
     * 棒球比分处理
     * @param scores
     * @return
     */
    public static Map<String, CommonItem>  getBaseBallAllScores(String scores){
        Map<String, CommonItem> map=new HashMap<>();
        if(StringUtils.isEmpty(scores)){
            return map;
        }
        JSONObject periodFootballScores = JSONObject.parseObject(scores);
        Map<Long, BaseballScores> allPeriodScores= JsonMapUtils.parseBaseballMap(periodFootballScores);
        for (Map.Entry<Long, BaseballScores> entry : allPeriodScores.entrySet()) {
            if(entry.getKey().equals(WHOLE_MATCH)){
                continue;
            }
            if(SportPeriodConstant.BaseballPeriod.getIndexByPeriod(entry.getKey())%2==0){
                continue;
            }
            //1. 计算当前局
          Integer index=  SportPeriodConstant.BaseballPeriod.getIndexByPeriod(entry.getKey()); //0,1,2,3,4
            index=index/2+1;
            //2. 根据局数计算局比分\
            CommonItem commonItem =map.get(index.intValue()+"");
            if(commonItem==null){
                map.put(index.intValue()+"",entry.getValue().getSetScore());
            }else {
                commonItem.setHome(commonItem.getHome()+entry.getValue().getSetScore().getHome());
                commonItem.setAway(commonItem.getAway()+entry.getValue().getSetScore().getAway());
            }
        }
        return map;
    }
}
