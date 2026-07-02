package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Description  :  操盘配置-投注项配置DTO
 * @author       :  damian
 * @Date:  2022年05月14日 下午16:34:06
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class ConfigMarketOddsStatusDTO implements Serializable {
	 
	private static final long serialVersionUID = 5188624964566856388L;

	//投注项id
    private Long id;

    //标准赛事id
    private Long standardMatchInfoId;

    //标准玩法id
    private Long standardCategoryId;

    //投注项
    private String oddsType;

    //投注项赔率值
    private Long oddsValue;

    //投注项操盘状态，0-关闭，1-开启
    private Integer status;

    //配置修改时间
    private Long modifyTime;

    //创建时间
    private Long createTime;

    //操作人ID
    private Long operaterId;

	@Override
	public String toString() {
		return "ConfigMarketOddsStatusDTO [id=" + id + ", standardMatchInfoId=" + standardMatchInfoId
				+ ", standardCategoryId=" + standardCategoryId + ", oddsType=" + oddsType + ", oddsValue=" + oddsValue
				+ ", status=" + status + ", modifyTime=" + modifyTime + ", createTime=" + createTime + ", operaterId="
				+ operaterId + "]";
	}
    

}
