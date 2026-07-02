package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 标准玩法投注项表
 * </p>
 *
 * @author CodeGenerator
 * @since 2020-04-19
 */
@Data
public class StandardMarketCategoryFieldBO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表ID, 自增
     */
    private Long id;

    /**
     * 玩法投注项名称编码。国际化信息
     */
    private List<I18nItemBO> nameI18n;

    /**
     * 排序值.
     */
    private Integer orderNo;

    /**
     * 更新时间. UTC时间, 精确到毫秒
     */
    private Long modifyTime;


}
