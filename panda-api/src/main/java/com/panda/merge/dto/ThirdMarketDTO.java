package com.panda.merge.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Description  :  数据接入格式：盘口
 * @author       :  Vito
 * @Date:  2019年10月7日 下午8:13:18
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class ThirdMarketDTO implements Serializable {
	private static final long serialVersionUID = 1L;

    /**
     * 第三方玩法原始ID.
     */
    @NotNull(message="三方玩法源id不能为空")
    private String thirdMarketCategorySourceId;

    /**
     * 第三提供的id。SR: 报文中有id字段。
     */
    @NotNull(message="三方盘口源id不能为空")
    private String thirdMarketSourceId;

    /**
     * 盘口类型. 属于赛前盘或者滚球盘. 1: 赛前盘; 0: 滚球盘.
     */
    @NotNull(message="盘口类型不能为空")
    private Integer marketType;

    /**
     * 取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source
     */
    @NotNull(message="数据源不能为空")
    private String dataSourceCode;

    /**
     * 盘口状态0-5. 0:active, 1:suspended, 2:deactivated, 3:settled, 4:cancelled, 5:handedOver, 11：锁盘
     */
    @NotNull(message="盘口状态不能为空")
    private Integer status;

    /**
     * 玩法的中文名称. 仅用用于数据库操作人员使用.
     */
    private String oddsTypeName;

    /**
     * 该盘口具体显示的值. 例如: 大小球中, 大小界限是:  3.5
     */
    private String oddsValue;

    /**
     * 排序类型
     */
    private String orderType;

    /**
     * 盘口名称.
     */
    private String oddsName;

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

    private String remark;

    private Long modifyTime;

    /**
     * 并列-胜出数
     */
    private Integer numberOfWinners;

    /**
     * 盘口名称编码. 用于多语言
     */
    @Valid
    private List<I18nItemDTO> i18nNames;

	/**
	 * 盘口投注项
	 */
    @Valid
	private List<ThirdMarketOddsDTO> marketOddsList;

    /**
     * 扩展参数：用于上游需通过融合向下游透传的参数，融合不做任何处理及存储
     */
    private String extraInfo;

    //@NotNull(message="运动类型id不能为空")
    private Long sportId;

    /**
     * 冗余字段盘口名称编码. 用于多语言
     */
    private Long nameCode;

    /**
     * Tx 排序位置
     */
    private Integer offerLineId;
    /**
     * Tx 位置id
     */
    private Long offerId;

    /**
     * 标准玩法
     */
    private Long marketCategoryId;
    /**
     * 盘口级别，数字越小优先级越高
     */
    private Integer oddsMetric;

    /**
     * 内部数据源
     */
    private String internalDataSourceCode;
    /**
     * AO特殊事件
     */
    private Integer eventType = 0;
    /**
     * 三方盘口标识：TRUE 走加锁逻辑，FALSE 不走加锁逻辑直接入库 ,默认TRUE
     */
    private Boolean lock = Boolean.TRUE;
}
