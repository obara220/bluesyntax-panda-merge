package com.panda.merge.dto.settle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataSourceConnectionStatusDto implements Serializable {

    /**
     * 标准赛事ID
     */
    private Long standardMatchId;

    /**
     * 数据商连接状态映射：key=数据商编码，value=连接状态
     * 状态：0=开关未开启或维护状态（前端不展示），1=开关开启且连接，2=开关开启且断连
     */
    private Map<String, Integer> datasourceStatusMap;

    /**
     * 状态改变时间戳
     */
    private Long timestamp;
}

