package com.panda.merge.dto;

import com.panda.merge.model.StandardSportMarketOdds;
import lombok.Data;

/**
 * @author bevan
 */
@Data
public class StandardSportMarketOddsDetail extends StandardSportMarketOdds {
    /**
     * margin值
     */
    private Double margin;

    /**
     * 概率差
     */
    private Double probability;

    /**
     * 描点 ：0(否),1(是)
     */
    private Integer anchor;

    /**
     * 概率赔率
     */
    private Integer probabilityOdds;
}
