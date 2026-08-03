package com.panda.merge.dto.message;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RcsClearDiffMessage implements Serializable {

    /**
     * 赛事ID
     */
    private Long matchId;
    /**
     * 融合发送清理标识
     */
    private Integer clearType;
    /**
     * 赛事类型,0:普通赛事、1冠军赛事
     */
    private Integer type;
    /**
     * 清理类型
     */
    private List<Long> playIds;
    /**
     * 体育种类
     */
    private Long sportId;
    /**
     * 比赛开始时间. 比赛开始时间 UTC时间
     */
    private Long beginTime;
    /**
     * LINKID
     */
    private String globalId;

    /**
     * 自動切換賠率源 手動/自動:true,其他：false
     */
    private boolean changeDataSource = false;
}