package com.panda.merge.dto.odds;

import com.panda.merge.common.enums.MarketTypeEnum;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * DataSourceAutoSwitchConfig
 *
 * @description:
 * @date: 4/28/2025
 **/
@Data
public class DataSourceAutoSwitchConfig {

    public static final DataSourceAutoSwitchConfig EMPTY = new DataSourceAutoSwitchConfig();

    private Integer validSecond;

    @NotNull(message = "matchId can not be null")
    private Long matchId;

    @NotNull(message = "marketCategoryId can not be null")
    private Integer marketType;

    private List<String> dataSourceList;

    transient  private Map<String, Integer> dsPriorityMap;

    private Integer changeStatus;
    /**
     * 0-风控，1-A01
     */
    private int source;

    public boolean isEnabled() {
        return changeStatus!=null && 1 == changeStatus;
    }

    public void setDataSourceList(List<String> dataSourceList) {
        this.dataSourceList = dataSourceList;
        if (CollectionUtils.isEmpty(dataSourceList)) {
            dsPriorityMap = Collections.emptyMap();
        } else {
            if (dsPriorityMap == null) {
                dsPriorityMap = new HashMap<>();
            } else {
                dsPriorityMap.clear();
            }
            for (int i = 0; i < dataSourceList.size(); i++) {
                dsPriorityMap.put(dataSourceList.get(i), i);
            }
        }
    }

    /*public DataSourceAutoSwitchConfig(Long matchId,int marketType){
        this.dataSourceList = Arrays.asList("SR", "BC", "BG", "L01-1XBet");
        this.matchId = matchId;
        this.marketType = marketType;
        if (marketType == MarketTypeEnum.PREMATCH.getCode()) {
            this.validSecond = 24 * 60 * 60 * 1000;
        } else
            this.validSecond = 120 * 60 * 60 * 1000;
        this.changeStatus = 1;
    }*/


}
