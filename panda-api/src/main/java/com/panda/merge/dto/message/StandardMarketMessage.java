package com.panda.merge.dto.message;

import com.panda.merge.dto.I18nItemDTO;
import com.panda.merge.dto.odds.MarketControlStatusEnum;
import com.panda.merge.dto.odds.StandardMarketScoreModification;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 *
 * @Description  : 标准盘口与投注项消息
 * @author       :  Vito
 * @Date:  2019年10月7日 下午5:01:27
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class StandardMarketMessage implements StandardMarketScoreModification, Serializable {
	private static final long serialVersionUID = 1L;

	/**
     * 标准盘口id
     * 非空
     */
    private Long id;

    /**
     * 非空
     * 标准玩法id   standard_sport_market_category.id
     */
    private Long marketCategoryId;

    /**
     * 非空
     * 盘口类型. 属于赛前盘或者滚球盘. 1: 赛前盘; 0: 滚球盘.
     */
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
     * 盘口名称,V1.2统一命名规则.
     */
    private String oddsName;

    /**
     * 排序类型
     */
    private String orderType;

    /**
     * 盘口级别，数字越小优先级越高
     */
    private Integer oddsMetric;
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
     * 取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source
     */
    private String dataSourceCode;

    /**
     * 三方盘口源状态,给风控操盘使用，融合侧不做修改
     */
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
     * pa状态，赔率合法性校验，最大最小值校验设置的状态，可为空(没有经过赔率合法性校验为空)
     */
    private Integer paStatus;
    private String paStatusReason;
    /**
     * 通过以上三种状态加上操盘赛事状态得出的最终状态
     * 盘口状态0-5. 0:active 开盘, 1:suspended 封盘, 2:deactivated 关盘, 3:settled 已结算, 4:cancelled 已取消, 5:handedOver  盘口的中间状态，该状态的盘口后续不会有赔率过来 11:锁盘状态
     */
    private Integer status;

    /**
     * 并列-胜出数
     */
    private Integer numberOfWinners;

    /**
     * 自动关盘状态 0 开 ，1 关
     */
    private Integer autoCloseStatus;

    /**
     * 玩法所属时段，
     * 对应字典parent_type_id=7
     */
    private String scopeId;

    /**
     * 该字段用于做风控时，需要替换成风控服务商提供的盘口id。 如果数据源发生切换，当前字段需要更新。
     */
    private String thirdMarketSourceId;

    private String remark;

    private Long modifyTime;
    /**
     * 用于做时间戳校验
     */
    private Long verifyModifyTime;

    /**
     * 盘口名称编码.
     */
    private Long nameCode;

    /**
     * 盘口名称编码. 用于多语言
     */
    private List<I18nItemDTO> i18nNames;

	/**
	 * 盘口投注项
	 */
	private List<StandardMarketOddsMessage> marketOddsList;

    /**
     * 扩展参数：用于上游需通过融合向下游透传的参数，融合不做任何处理及存储
     */
    private String extraInfo;

    private String sendData;

    /**
     * 盘口位置
     */
    private Integer placeNum;

    /**
     * 盘口位置id
     */
    private String placeNumId;

    /**
     * 盘口差
     */
    private Double marketHeadGap;

    /**
     * 是否展示赛果：0不展示，1展示
     */
    private Integer showMarketResult;

    /**
     * 冠军盘口排序字段
     */
    private Integer orderNo = 0;

    /**
     * 联动模式：0(否),1(是)
     */
    private Integer linkageMode;
    /**
     * 子玩法id
     */
    private Long childMarketCategoryId;
    /**
     * 盘口收盘状态，0（否），1（是）
     */
    private Integer endEdStatus;
    /**
     * A+专用，是否数据商玩法挡板封盘（0-开，1-封）
     */
    private Integer categorySuspended = 0;
    /**
     * 足球增加开盘时间-封、关盘/接拒  记录被修改的第三方状态
     */
    private Integer oldThirdMarketSourceStatus;

    /**
     * 内部数据源
     */
    private String internalDataSourceCode;

    /**
     * 玩法类型 计算
     */
    private String categoryType;

    /**
     * 操盘后台状态
     * @param obj
     * @return
     */
    private Integer riskStatus = 0;

    /**
     * 标准不走关转封 1关转封 ，2不走
     */
    private int colseMarket = 1;

    /**
     * 融合盘口状态  21 关转封特殊状态
     */
    private Integer mergeMarketStatus;

    transient private int controlStatus;

    /**
     * 原始球头, 保存关转封后被覆盖的球头
     */
    private String obh;
    /**
     * 0上架。1下架
     */
    private int isShelves = 0;

    @Override
    public String homeScore() {
        return addition3;
    }

    @Override
    public void setHomeScore(String homeScore) {
        addition3 = homeScore;
    }

    @Override
    public String awayScore() {
        return addition4;
    }

    @Override
    public void setAwayScore(String awayScore) {
        addition4 = awayScore;
    }

    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StandardMarketMessage other = (StandardMarketMessage) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

    @Override
    public Long getRelationMarketId() {
        return id;
    }

    @Override
    public void setRelationMarketId(Long relationMarketId) {
        id = relationMarketId;
    }

}
