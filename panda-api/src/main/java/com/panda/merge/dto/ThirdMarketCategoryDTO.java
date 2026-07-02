package com.panda.merge.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 基本玩法
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-09-03
 */
@Data
public class ThirdMarketCategoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 投注项数量
     */
    @NotNull(message = "三方玩法投注项数量不能为空")
    private Integer fieldsNum;

    /**
     * 第三方玩法原始ID.
     */
    @NotNull(message = "三方玩法原始id不能为空")
    private String thirdSourceId;


    /**
     * 取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source
     */
    @NotNull(message = "数据来源编码[dataSourceCode]不能为空")
    private String dataSourceCode;

    /**
     * 该玩法是否生效. 1生效; 0 不生效.  默认不生效
     */
    @NotNull(message = "三方玩法玩法是否有效不能为空")
    private Integer active;

    /**
     * 上游下发消息戳
     */
    private Long modifyTime;

    /**
     * 玩法名称国际化信息
     */
    @NotEmpty(message = "推送的三方盘口数据不能为空")
    @Valid
    private List<I18nItemDTO> nameI18n;

    /**
     * 玩法支持的运动种类ID
     */
    private List<Long> supportSports;

}
