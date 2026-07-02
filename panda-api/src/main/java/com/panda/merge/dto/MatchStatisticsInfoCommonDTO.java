package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @description    虚拟赛事统计信息
 * @Param No such property: code for class: Script1
 * @Author  Crazy
 * @Date  2020/10/31 13:40
 * @return
 **/

@Data
public class MatchStatisticsInfoCommonDTO implements Serializable{
	private static final long serialVersionUID = 1L;
    /**
     * 统计事件编码：字典表system_item_dict parent_type_id=17
     */
	private String code;
	/**
	 * 球队信息
	 */
	private String value;
}
