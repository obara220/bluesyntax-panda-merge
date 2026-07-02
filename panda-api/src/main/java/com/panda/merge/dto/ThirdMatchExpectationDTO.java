package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 三方赛事预期信息更新
 *
 * @author aldrich
 * @since 2024/11/6
 */
@Data
public class ThirdMatchExpectationDTO implements Serializable {
    private static final long serialVersionUID = -4703883348844248151L;

    /** 数据源赛事ID*/
    @NotNull(message = "三方数据源赛事ID不能为null!")
    private String thirdMatchSourceId;


    /** 数据源运动种类ID*/
    @NotNull(message = "三方数据源赛事运动类型不能为null!")
    private Long sportId;

    /** 数据来源编码code（取值： SR BC分别代表：SportRadar、FeedConstruc。详情见data_source）*/
    @NotNull(message = "三方数据源赛事数据来源不能为null!")
    private String dataSourceCode;

    /** 主队预期进球xG */
    private BigDecimal homeExpectationXg;

    /** 主队预期失球 */
    private BigDecimal homeExpectationLoss;

    /** 客队预期进球xG */
    private BigDecimal awayExpectationXg;

    /** 客队预期失球 */
    private BigDecimal awayExpectationLoss;
}
