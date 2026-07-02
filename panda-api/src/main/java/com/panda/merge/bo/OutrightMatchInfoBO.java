package com.panda.merge.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author : nonhung
 * @project Name : panda-merge
 * @package Name : com.panda.merge.bo
 * @description : TODO
 * @date: 2020-10-01 21:15
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Data
public class OutrightMatchInfoBO implements Serializable {
    private static final long serialVersionUID = -6212742985071349389L;
    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "赛种id")
    private Long sportId;

    @ApiModelProperty(value = "区域id")
    private Long regionId;

    @ApiModelProperty(value = "标准联赛id")
    private Long standardTournamentId;

    @ApiModelProperty(value = "数据源")
    private String dataSourceCode;

    @ApiModelProperty(value = "下次封盘时间")
    private Long nextClosingTime;

    @ApiModelProperty(value = "赛事开关封锁Open开Close关Seal封Lock锁")
    private String matchMarketStatus;

    @ApiModelProperty(value = "冠军赛事管理id")
    private String standardOutrightManagerId;

    @ApiModelProperty(value = "三方冠军赛事id")
    private Long thirdOutrightMatchId;

    @ApiModelProperty(value = "三方冠军赛事源id")
    private String thirdOutrightMatchSourceId;

    @ApiModelProperty(value = "标准冠军赛事开始时间")
    private Long standrdOutrightMatchBegionTime;

    @ApiModelProperty(value = "标准冠军赛事结束时间")
    private Long standrdOutrightMatchEndTime;

    @ApiModelProperty(value = "冠军赛事开售状态Sold开售Unsold未售")
    private String sellStatus;

    @ApiModelProperty(value = "是否自动开售新玩法Yes是No否")
    private String autoSellStatus;

    @ApiModelProperty(value = "赛季id")
    private String seasonId;

    @ApiModelProperty(value = "标准冠军赛事赛季名称")
    private String standardOutrightYear;

    @ApiModelProperty(value = "是否订阅0未订阅1已订阅")
    private Integer booked;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "新增时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;
    @ApiModelProperty(value = "联赛logo")
    private String touLogoUrl;

    @ApiModelProperty(value = "赛事国际信息")
    private Map<String, String> map;

}
