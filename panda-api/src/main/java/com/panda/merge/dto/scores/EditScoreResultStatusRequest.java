package com.panda.merge.dto.scores;

import lombok.Data;

import java.io.Serializable;

/**
 * @project Name :  panda_data_service
 * @package Name :  com.panda.sports.manager.realtime.query
 * @description :  TODO
 * --------  ---------  --------------------------
 */
@Data
public class EditScoreResultStatusRequest implements Serializable {


    private Long sportId;

    private Long standardMatchId;
    /**
     * 1 开启赛果展示
     * 0 关闭赛果展示
     * */
    private Integer status;


    /**
     * 修改数据类型，0赛果状态，1默认值
     */
    private Integer type;

}
