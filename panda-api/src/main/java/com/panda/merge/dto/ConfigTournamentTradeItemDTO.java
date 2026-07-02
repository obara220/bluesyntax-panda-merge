package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 联赛维度 最大最小赔率
 */
@Data
public class ConfigTournamentTradeItemDTO implements Serializable {
    /**
     * 赛种
     */
    private Long sportId;
    /**
     * 联赛id
     */
    private Long tournamentId;
    /**
     * 1：早盘；0：滚球
     */
    private Integer matchType;
    /**
     * 马来 最大赔率
     */
    private BigDecimal spreadMaxOdds;
    /**
     * 马来 最小赔率
     */
    private BigDecimal spreadMinOdds;
    /**
     * 欧赔 最大赔率
     */
    private BigDecimal marginMaxOdds;
    /**
     * 欧赔 最小赔率
     */
    private BigDecimal marginMinOdds;

    private Long operaterId;
}
