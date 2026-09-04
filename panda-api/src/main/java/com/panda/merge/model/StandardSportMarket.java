package com.panda.merge.model;

import com.panda.merge.dto.odds.StandardMarketModification;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class StandardSportMarket implements Serializable, StandardMarketModification {
    @ApiModelProperty(value = "数据库id,自增")
    private Long id;

    private Long relationMarketId;

    @ApiModelProperty(value = "所属联赛IDstandard_sport_tournament.id")
    private Long standardTournamentId;

    @ApiModelProperty(value = "标准比赛IDstandard_match_info.id")
    private Long standardMatchInfoId;

    @ApiModelProperty(value = "标准玩法idstandard_sport_market_category.id")
    private Long marketCategoryId;

    @ApiModelProperty(value = "盘口类型.属于赛前盘或者滚球盘.1:赛前盘;0:滚球盘.")
    private Integer marketType;

    @ApiModelProperty(value = "操盘方式：0自动操盘，1手动操盘")
    private Integer tradeType;

    @ApiModelProperty(value = "盘口名称编码.用于多语言")
    private Long nameCode;

    @ApiModelProperty(value = "该盘口具体显示的值.例如:大小球中,大小界限是:3.5")
    private String oddsValue;

    @ApiModelProperty(value = "盘口名称,V1.2统一命名规则.")
    private String oddsName;

    @ApiModelProperty(value = "排序类型")
    private String orderType;

    @ApiModelProperty(value = "盘口级别，数字越小优先级越高")
    private Long oddsMetric;

    @ApiModelProperty(value = "附加字段1")
    private String addition1;

    @ApiModelProperty(value = "附加字段2")
    private String addition2;

    @ApiModelProperty(value = "附加字段3")
    private String addition3;

    @ApiModelProperty(value = "附加字段4")
    private String addition4;

    @ApiModelProperty(value = "附加字段5")
    private String addition5;

    @ApiModelProperty(value = "取值:SRBC分别代表:SportRadar、FeedConstruc.详情见data_source")
    private String dataSourceCode;

    @ApiModelProperty(value = "盘口状态0-5.0:active,1:suspended,2:deactivated,3:settled,4:cancelled,5:handedOver")
    private Integer status;

    @ApiModelProperty(value = "三方盘口源状态")
    private Integer thirdMarketSourceStatus;

    @ApiModelProperty(value = "盘口阶段id.对应对应system_item_dict.value")
    private String scopeId;

    @ApiModelProperty(value = "接收到第三方数据后,可以通过该字段快速定位到当前的盘口.通过玩法和具体内容确认盘口的唯一性.SR提供的盘口数据id生成算法:Type_Typeid_Subtypeid_Specialoddsvalue")
    private String thirdOddsType;

    @ApiModelProperty(value = "该字段用于做风控时，需要替换成风控服务商提供的盘口id。如果数据源发生切换，当前字段需要更新。")
    private String thirdMarketSourceId;

    @ApiModelProperty(value = "是否下发数据：Y是N否")
    private String sendData;

    @ApiModelProperty(value = "最近一次下发数据的Linkid")
    private String linkId;

    private String extraInfo;

    @ApiModelProperty(value = "盘口位置，1：表示主盘，2：表示第一副盘")
    private Integer placeNum;

    private String remark;

    private Double marketHeadGap;

    private Long createTime;

    private Long modifyTime;

    @ApiModelProperty(value = "并列-胜出数")
    private Integer numberOfWinners;

    /**
     * 足球增加开盘时间-封、关盘/接拒  记录被修改的第三方状态
     */
    @ApiModelProperty(value = "足球增加开盘时间-封、关盘/接拒  记录被修改的第三方状态")
    private Integer oldThirdMarketSourceStatus;

    /**
     * 内部数据源 不入库
     */
    private String internalDataSourceCode;

    /**
     * 玩法类型 计算
     */
    private String categoryType;

    private Integer paStatus;

    private String paStatusReason;

    @Override
    public Integer getMarketSource() {
        return 0;
    }

    @Override
    public Integer getMergeMarketStatus() {
        return 0;
    }

    @Override
    public void setMergeMarketStatus(Integer status) {

    }

    @Override
    public int getControlStatus() {
        return 0;
    }

    @Override
    public void setControlStatus(int controlStatus) {}

}
