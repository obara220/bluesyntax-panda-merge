package com.panda.merge.dto.message;

import com.panda.merge.dto.odds.StandardMarketScoreModification;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

/**
 *
 * @Description  : 标准盘口与投注项消息
 * @author       :  Vito
 * @Date:  2019年10月7日 下午5:01:27
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class StandardMarketDataMessage implements Serializable, StandardMarketScoreModification {
	private static final long serialVersionUID = 1L;

    /**
     * 数据库id, 自增
     */
    private Long id;


    private Long relationMarketId;
    /**
     * 所属联赛ID    standard_sport_tournament.id
     */
    private Long standardTournamentId;

    /**
     * 标准比赛ID   standard_match_info.id
     */
    private Long standardMatchInfoId;

    /**
     * 标准玩法id   standard_sport_market_category.id
     */
    private Long marketCategoryId;

    /**
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
     * 盘口名称编码. 用于多语言
     */
    private Long nameCode;

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
     * 旧的附加字段1
     */
    private String oldAddition1;

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

    /** 并列胜出数 **/
    private Integer numberOfWinners;

    /**
     * 取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source
     */
    private String dataSourceCode;

    /**
     * 盘口状态0-5. 0:active, 1:suspended, 2:deactivated, 3:settled, 4:cancelled, 5:handedOver
     */
    private Integer status;

    /**
     * 三方盘口源状态
     */
    private Integer thirdMarketSourceStatus;

    /**
     * 盘口阶段id. 对应 对应 system_item_dict.value
     */
    private String scopeId;

    /**
     * 接收到第三方数据后, 可以通过该字段快速定位到当前的盘口. 通过玩法和具体内容确认盘口的唯一性.  SR提供的盘口数据id 生成算法: Type_Typeid_Subtypeid_Specialoddsvalue
     */
    private String thirdOddsType;

    /**
     * 该字段用于做风控时，需要替换成风控服务商提供的盘口id。 如果数据源发生切换，当前字段需要更新。
     */
    private String thirdMarketSourceId;

    /**
     * 是否下发数据：Y是N否 改为 TX 统一盘口值ID
     */
    private String sendData;

    /**
     * 最近一次下发数据的Linkid
     */
    private String linkId;

    private String extraInfo;

    /**
     * 投注项中的最小赔率值 （用作盘口位置排序参数）
     */
    private Integer paOddsValue;

    /**
     * 盘口值的绝对值（用作盘口位置排序参数）
     */
    private Double marketOddsValue;

    /**
     * 盘口位置
     */
    private Integer placeNum;
    /**
     * TX盘口位置
     */
    private Integer txPlaceNum;

    /**
     * 盘口位置开关，开关封锁状态
     */
    private String placeNumStatus ;
    /**
     * 自动关盘状态 0 开 ，1 关
     */
    private Integer autoCloseStatus;
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
    private Integer showMarketResult = 0;

    /**
     * 冠军盘口排序字段
     */
    private Integer orderNo;

    /**
     * 联动模式：0(否),1(是)
     */
    private Integer linkageMode;

    /**
     * 自动关盘最终状态 autoClose
     */
    private String remark;

    private Long createTime;

    private Long modifyTime;
    //格式化后的盘口修改时间，yyyy-MM-dd HH:mm
    private Long modifyTimeFormat;

	/**
	 * 盘口投注项
	 */
	private List<StandardMarketOddsDataMessage> marketOddsList;

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
     * 初盘PA赋值
     */
    private String dataSourceCodePA;

    /**
     * 内部数据源 不入库
     */
    private String internalDataSourceCode;
    /**
     * AO特殊事件 不入库
     */
    private Integer eventType = 0;
    /**
     * 玩法类型 计算
     */
    private String categoryType;
    /**
     * 标准不走关转封 1关转封 ，2不走
     */
    private int colseMarket = 1;

    private Integer mergeMarketStatus;

    transient private int controlStatus;

    private BigDecimal obh;

    public void saveStandardMarketOddsMessageList(List<StandardMarketOddsMessage> list) {
    	if(CollectionUtils.isEmpty(list)) {
    		return;
    	}
    	marketOddsList = new ArrayList<StandardMarketOddsDataMessage>();
    	list.forEach(e->{
    		StandardMarketOddsDataMessage s = new StandardMarketOddsDataMessage();
    		BeanUtils.copyProperties(e, s);
    		marketOddsList.add(s);
    	});
    }

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
		StandardMarketDataMessage other = (StandardMarketDataMessage) obj;
		return Objects.equals(dataSourceCode, other.dataSourceCode)
				&& Objects.equals(relationMarketId, other.relationMarketId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(dataSourceCode, relationMarketId);
	}

}
