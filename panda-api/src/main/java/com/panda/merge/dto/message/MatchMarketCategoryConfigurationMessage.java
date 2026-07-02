package com.panda.merge.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author :  Riben
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.dto
 * @Description :  开盘数据服务商及需要开盘玩法的配置信息
 * @Date: 2020-09-17 13:17
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class MatchMarketCategoryConfigurationMessage implements Serializable {

    /**
     * 赛事id
     */
    @NotNull
    private Long standardMatchId;
    /**
     * 联赛等级
     */
    private Integer tournamentLevel;
    /**
     * 盘口类型1：早盘；0：滚球
     */
    @NotNull
    @JsonProperty("matchType")
    private Integer marketType;
    /**
     * SR权重
     */
    private Integer srWeight;
    /**
     * BC权重
     */
    private Integer bcWeight;
    /**
     * BG权重
     */
    private Integer bgWeight;

    /**
     * TX权重
     */
    private Integer txWeight;

    /**
     * RB权重
     */
    private Integer rbWeight;

    /**
     * PD权重
     */
    private Integer pdWeight;

    /**
     * PI权重
     */
    private Integer piWeight;

    /**
     * AO权重
     */
    private Integer aoWeight;

    /**
     * ls权重
     */
    private Integer lsWeight;

    /**
     * ls权重
     */
    private Integer beWeight;
    /**
     * KO权重
     */
    private Integer koWeight;

    /**
     * BT权重
     */
    private Integer btWeight;

    /**
     * od权重
     */
    private Integer odWeight;

    /**
     * N01权重
     */
    private Integer n01Weight;

    /**
     * N02权重
     */
    private Integer n02Weight;

    /**
     * F01权重
     */
    private Integer f01Weight;

    /**
     * N03权重
     */
    private Integer n03Weight;
    /**
     * L02权重
     */
    private Integer l02Weight;

    /**
     * 风控操盘管理数据源
     */
    private String riskManagerCode;

    /**
     * 4405：玩法级操盘模式切换的玩法集合（风控/操盘传入）
     * 说明：当该字段不为空时，走“玩法级写入 playRiskManager”逻辑，而不是赛事级改 sell.pre/liveRiskManagerCode。
     */
    private List<Long> categoryIds4405;

    /**
     * 是否标识开售下发流程（反之为空或者不等于1是其他业务）
     */
    private Integer isDubboSell;

    /**
     * 赛事玩法配置列表
     **/
     List<MatchCategoryConfigurationMessage> categoryList;

}
