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
     * 修改数据类型，1无比分自动关闭，2无注单自动关闭 3比分中心手动操作打开/关闭  4棒球赛事取消自动关闭
     */
    private Integer type;

}
