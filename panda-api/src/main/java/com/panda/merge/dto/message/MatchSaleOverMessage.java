package com.panda.merge.dto.message;


import lombok.Data;

import java.io.Serializable;

/**
 * 赛事预售开售记录结束消息体
 *
 * @author :  Franz
 * @Project Name :  data-nonrealtime
 * @Package Name :  com.panda.sport.data.nonrealtime.support.mqhandler
 * @Description :  TODO
 * @Date: 2019-11-30 18:16
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class MatchSaleOverMessage implements Serializable {
    private String linkId;
    private Long standardMatchId;
    private String matchManageId;
    /** 操作人*/
    private String updateUser;

    public MatchSaleOverMessage() {
    }

    public MatchSaleOverMessage(String linkId, Long standardMatchId, String matchManageId) {
        this.linkId = linkId;
        this.standardMatchId = standardMatchId;
        this.matchManageId = matchManageId;
    }

    public MatchSaleOverMessage(String linkId, Long standardMatchId, String matchManageId,String updateUser) {
        this.linkId = linkId;
        this.standardMatchId = standardMatchId;
        this.matchManageId = matchManageId;
        this.updateUser = updateUser;
    }
}
