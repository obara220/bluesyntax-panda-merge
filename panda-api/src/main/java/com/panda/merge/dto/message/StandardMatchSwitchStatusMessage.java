package com.panda.merge.dto.message;

import lombok.Data;

/**
 * @author : Bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.dto.message
 * @description : TODO
 * @date: 2020-12-02 15:01
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Data
public class StandardMatchSwitchStatusMessage {

    /**
     * 标准赛事ID
     */
    private Long standardMatchId;
    /**
     * 三方赛事
     */
    private String thirdMatchId;
    /**
     * 数据源
     */
    private String dataSourceCode;

    /**
     * 状态 1 即将开赛 （已经接受到滚球赔率）
     */
    private Integer oddsLive;

    /** 是否提前开赛 0否，1是*/
    private Integer advance = 0;
}
