package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
@Data
public class MarketCategorySellMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 标准赛事id
     */
    private Long matchId;
    /**
     * 盘口类型 1：早盘；0：滚球
     */
    private Integer matchType;
    /**
     * key  数据源 values  玩法id
     */
    private Map<String, List<Long>> playDataSource;

}
