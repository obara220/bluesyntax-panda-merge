package com.panda.merge.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 标准赛种玩法表
 * </p>
 *
 * @author CodeGenerator
 * @since 2020-04-19
 */
@Data
public class StandardSportMarketCategoryBO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 运动种类id.  对应表 sport.id
     */
    private Long sportId;

    /**
     * 玩法名称国际化信息
     */
    private List<I18nItemBO> nameI18n;

    /**
     * 玩法描述国际化编码
     */
    private List<I18nItemBO> descI18n;

    private List<I18nItemBO> detailNameI18n;

    /**
     * PC模板展示
     */
    private Integer templatePc;

    /**
     * h5模板展示
     */
    private Integer templateH5;

    /**
     * 客户端PC模板展示
     */
    private Integer templatePcClient;

    /**
     * 客户端玩法模板H5
     */
    private Integer templateH5Client;

    /**
     * 上下半场标注多语言
     */
    private Long detailNameCode;

    /**
     * 是否展开，1：“是” 代表默认展开，0：“否” 代表默认收起
     */
    private Integer isCollapse;

    /**
     * 所属时段,对应融合字典parent_type_id=7
     */
    private String scopeId;

    /**
     * 玩法状态. 0无效; 1有效
     */
    private Integer status;

    /**
     * 外部商户玩法状态
     */
    private Integer merchantStatus;

    /**
     * 商户编码集合
     */
    private String merchantApiCodeList;

    /**
     * 排序值.
     */
    private Integer orderNo;

    /**
     * 投注项类型(自定义玩法)
     */
    private Integer fieldType;

    private Long modifyTime;

    // "合买开关 0:关闭;1:开启"
    private Integer mrStatus;

    /**
     * 主玩法多语言
     */
    private List<I18nItemBO> mainNameI18n;

    private Long mainNameCode;

}
