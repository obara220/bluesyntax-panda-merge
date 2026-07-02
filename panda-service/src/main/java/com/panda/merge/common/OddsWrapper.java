package com.panda.merge.common;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @description: 批量处理时，赋予每条信息相关关键字段
 * @author: Henry Wang
 * @create: 2024-03-04 11:52
 **/
@Data
@Component
public class OddsWrapper<T> {
    private String linkId;

    private String dataSourceCode;

    private Long dataSourceTime;

    private Long matchBeginTime;

    private Boolean isOutRight;

    private Long thirdMatchId;

    private Long standardSourceId;

    private String thirdMatchSourceId;

    private Integer marketType;

    private Long marketCategoryId;

    private Long thirdMarketCategoryId;

    private Long thirdSportMarketId;

    private Long standardSportMarketId;

    private Boolean isCheckOdds;

    private Boolean hasRecord;

    private Long uuid;

    private Long sportId;

    private T data;
}
