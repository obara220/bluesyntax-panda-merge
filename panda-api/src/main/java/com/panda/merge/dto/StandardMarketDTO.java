package com.panda.merge.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 
 * @Description  : 标准盘口与投注项消息
 * @author       :  Vito
 * @Date:  2019年10月7日 下午5:01:27
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class StandardMarketDTO implements Serializable {
	private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 标准玩法id   standard_sport_market_category.id
     */
    @NotNull(message="标准玩法id不能为空")
    private Long marketCategoryId;

    /**
     * 盘口类型. 属于赛前盘或者滚球盘. 1: 赛前盘; 0: 滚球盘. 
     */
    @NotNull(message="盘口类型不能为空")
    private Integer marketType;
    /**
     * 盘口来源。0：数据商，1：融合构建
     */
    private Integer marketSource = 0;
    /**
     * 操盘方式：0自动操盘，1手动操盘
     */
    private Integer tradeType;

    /**
     * 该盘口具体显示的值. 例如: 大小球中, 大小界限是:  3.5
     */
    private String oddsValue;

    /**
     * 盘口名称. 
     */
    private String oddsName;

    /**
     * 排序类型
     */
    private String orderType;

    /**
     * 附加字段1
     */
    private String addition1;

    /**
     * 附加字段2
     */
    private String addition2;

    /**
     * 附加字段3
     */
    private String addition3;

    /**
     * 附加字段4
     */
    private String addition4;

    /**
     * 附加字段5
     */
    private String addition5;

    /**
     * 取值:  PA 代表熊猫操盘
     */
    @NotNull(message="盘口数据源不能为空")
    private String dataSourceCode;

    /**
     * 盘口状态0-5. 0:active, 1:suspended, 2:deactivated, 3:settled, 4:cancelled, 5:handedOver
     */
    @NotNull(message="盘口状态不能为空")
    private Integer status;

    /**
     * 三方盘口源状态,给风控操盘使用
     */
    @NotNull(message="盘口源状态不能为空")
    private Integer thirdMarketSourceStatus;

    /**
     * 操盘后台设置的位置状态，融合侧不做修改，可为空（操盘后台没有设置位置状态为空）
     */
    private Integer placeNumStatus;
    /**
     * 风控防封，累封 需求状态透传给风控
     */
    private Integer placeNumStatusDisplay = 1 ;

    /**
     * 玩法所属时段，
     * 对应字典parent_type_id=7
     */
    private String scopeId;

    /**
     * 该字段用于做风控时，需要替换成风控服务商提供的盘口id
     */
    private String thirdMarketSourceId;
    
    /**
     * 备注
     */
    private String remark;

    /**
     * 盘口位置
     */
    private Integer placeNum;

    /**
     * 盘口差
     */
    private Double marketHeadGap;

    /**
     * 修改时间	
     */
    private Long modifyTime;

    private Long nameCode;
    
    /**
     * 盘口名称编码. 用于多语言
     */
    @Valid
    private List<I18nItemDTO> i18nNames;

    /**
     * 联动模式：0(否),1(是)
     */
    private Integer linkageMode;

	/**
	 * 盘口投注项
	 */
	private List<StandardMarketOddsDTO> marketOddsList;

    /**
     * 子玩法ID
     */
    private Long childStandardCategoryId;
    /**
     * 盘口收盘状态，0（否），1（是）
     */
    private Integer endEdStatus;

    /**
     * 玩法类型 计算
     */
    private String categoryType;
    /**
     * 内部数据源
     */
    private String internalDataSourceCode;

    private Integer paStatus;

    private String paStatusReason;
}
