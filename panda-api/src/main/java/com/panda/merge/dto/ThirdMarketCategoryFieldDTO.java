package com.panda.merge.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 三方玩法投注项DTO
 * @author    tell
 * @since    2020年11月6日15:41:37
 */
@Data
public class ThirdMarketCategoryFieldDTO implements Serializable {

    private static final long serialVersionUID = 3258258210500944507L;

    /**
     * 第三方投注项原始ID。
     */
    @NotNull(message = "三方玩法投注项原始id不能为空")
    private String thirdSourceId;

    /**
     * 第三方玩法原始id。
     */
    @NotNull(message = "三方玩法原始id不能为空")
    private String thirdCategorySourceId;


    @NotNull(message = "排序字段不能为空，请确认数据是否正确.")
    private Integer orderNo;

    /**
     * 取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source
     */
    @NotNull(message = "数据来源编码[dataSourceCode]不能为空")
    private String dataSourceCode;

    /**
     * 上游下发消息戳
     */
    private Long modifyTime;

    /**
     * 投注项名称国际化信息
     */
    @NotEmpty(message = "投注项名称国际化信息不能为空")
    @Valid
    private List<I18nItemDTO> nameI18n;
}
