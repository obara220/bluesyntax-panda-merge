package com.panda.merge.dto.odds;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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




}
