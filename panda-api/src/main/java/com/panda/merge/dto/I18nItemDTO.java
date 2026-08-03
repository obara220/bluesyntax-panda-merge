package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 多语言DTO
 * @author    tell
 * @since    2020年11月6日15:41:37
 */
@Data
public class I18nItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 语言类型。 zh jp en 等
     * languageType 必须下发en语种，en语种text为空的可以填充中文text值
     */
    @NotNull(message = "三方数据源国际化语言类型不能为null")
    private String languageType;

    @NotNull(message = "三方数据源国际化文字内容不能为null")
    private String text;

    /**
     * 对应的多语言的Id
     */
    private Long i18nId;

    /**
     * 对应的多语言的nameCode
     */
    private Long nameCode;

    /**
     * 备注
     * */
    private String remark;
}
