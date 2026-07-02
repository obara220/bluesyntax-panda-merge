package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author : nonhung
 * @project Name : panda-merge
 * @package Name : com.panda.merge.dto
 * @description : TODO
 * @date: 2020-10-01 20:59
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Data
public class OutrightMatchInfoDTO implements Serializable {

    private static final long serialVersionUID = -2951045180023421548L;
    /**
     * 开始时间 ，utc时间戳
     */
    @NotNull(message = "开始时间不能为null!")
    private Long beginTime;
    /**
     * 结束时间 ，utc时间戳
     */
    private Long endTime;

}
