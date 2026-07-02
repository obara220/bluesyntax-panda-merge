package com.panda.merge.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 标准玩法表
 * </p>
 *
 * @author CodeGenerator
 * @since 2020-04-19
 */
@Data
public class StandardMarketCategoryBO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标准玩法ID
     */
    private Long id;

    /**
     * 名称编码。国际化信息
     */
    private List<I18nItemBO> nameI18n;

    /**
     * 上下半场标注国际化
     */
    private List<I18nItemBO> detailNameI18n;

    /**
     * 投注项数量
     */
    private Integer fieldsNum;

    /**
     * 是否属于多盘口玩法. 0no; 1yes.  默认no
     */
    private Integer multiMarket;

    /**
     * 支持赔率类型,1：支持欧式、英式、美式、香港、马来、印尼赔率；2：支持欧式、英式、美式赔率
     */
    private String supportOdds;

    /**
     * PC模板展示
     */
    private Integer templatePc;

    /**
     * h5模板展示
     */
    private Integer templateH5;

    /**
     * 客户端PC模板展示（新PC，支持页面编辑）
     */
    private Integer templatePcClient;

    /**
     * 客户端玩法模板H5（新H5，支持页面编辑）
     */
    private Integer templateH5Client;

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
     * 更新时间. UTC时间, 精确到毫秒
     */
    private Long modifyTime;

    /**
     * 投注项类型(自定义玩法)
     */
    private Integer fieldType;
    
    /**
     * 玩法对应投注项
     */
    List<StandardMarketCategoryFieldBO>  marketCategoryFields;
    
    /**
     * 玩法对应赛种信息
     */
    List<StandardSportMarketCategoryBO> sportMarketCategories;

    // "合买开关 0:关闭;1:开启"
    private Integer mrStatus;

}
