package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author :  myname
 * @Project Name :  data-realtime
 * @Package Name :  com.panda.sport.data.realtime.api.dto
 * @Description :  TODO
 * @Date: 2020-07-15 15:14
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class TradeMarketUiConfigDTO implements Serializable {

    private static final long serialVersionUID = 7140382087626912703L;

    /**
     * 标准赛事ID
     */
    @NotNull(message = "赛事id不能为空")
    private Long standardMatchInfoId;

    /**
     * 标准赛事类型：0.普通赛事、1.冠军赛事
     */
    private String matchType;

    /**
     * 标准玩法ID
     */
//    @NotNull(message = "标准玩法ID不能为空")
    private Long standardCategoryId;

    /**
     * 盘口位置
     */
//    @NotNull(message = "盘口位置不能为空")
    private Integer placeNum;


    /**
     * 盘口类型. 属于赛前盘或者滚球盘. 1: 赛前盘; 0: 滚球盘.
     */
    private Integer marketType;

    /**
     * 联动模式：0(否),1(是)
     */
    private Integer linkageMode;

    /**
     * 盘口位置集合
     */
    private List<MarketPlaceDtlDTO> marketPlaceDtlDTOList;


    /**
     * 最大最小值配置
     */
    private List<TradeMarketConfigItemDTO> marketConfigs;

    /**
     * 投注项margin集合
     */
    private List<MarketMarginDtlDTO> marketMarginDtlDTOList;

    /**
     * 盘口值水差配置,主要用到的球类：足球
     */
    private List<TradeMarketAutoDiffConfigItemDTO> diffConfigs;

    /**
     * 坑位值水差配置,主要用到的球类：篮球
     */
    private List<TradePlaceNumAutoDiffConfigItemDTO> placeNumDiffConfigs;

    /**
     * 玩法值水差配置
     */
    private TradeCategoryAutoDiffConfigItemDTO categoryDiffConfig;

    /**
     * 独赢盘配置
     */
    private List<MarketMarginGapDtlDTO> marginGapDtlDTOList;

    /**
     * 提前结算配置 赛事开关、玩法开关、cashOutMargin
     */
    private ConfigCashOutTradeItemDTO configCashOutTradeItemDTO;

    /**
     * 子玩法ID
     */
    private Long childStandardCategoryId;
}
