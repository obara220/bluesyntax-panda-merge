package com.panda.merge.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author : nonhung
 * @project Name : panda-merge
 * @package Name : com.panda.merge.bo
 * @description : TODO
 * @date: 2020-10-01 21:44
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Data
public class OutrightMatchCateGoryInfoBO implements Serializable {
    private static final long serialVersionUID = -3545989748555217584L;
    @ApiModelProperty(value = "标准玩法id")
    private Long id;

    @ApiModelProperty(value = "标准赛事id")
    private Long standardMatchId;

    @ApiModelProperty(value = "玩法开售状态Sold开售Unsold未售")
    private String categorySellStatus;


    @ApiModelProperty(value = "支持赔率类型,1：支持欧式、英式、美式、香港、马来、印尼赔率；2：支持欧式、英式、美式赔率")
    private String supportOdds;

    @ApiModelProperty(value = "PC模板展示")
    private Integer templatePc;

    @ApiModelProperty(value = "h5模板展示")
    private Integer templateH5;

    @ApiModelProperty(value = "玩法是否有效")
    private Integer status;

    @ApiModelProperty(value = "排序值")
    private Integer orderNo;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modfiyTime;

    @ApiModelProperty(value = "玩法国际信息")
    private Map<String, String> map;

}
