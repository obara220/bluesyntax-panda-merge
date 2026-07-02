package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;

/**
 * @author :  Franz
 * @Project Name :  data-nonrealtime
 * @Package Name :  com.panda.sport.data.nonrealtime.support.mqhandler
 * @Description :  TODO
 * @Date: 2019-11-30 18:16
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class ProcessMatchToMatchOverMessage implements Serializable {
    private String linkId;
    private Long matchStatus;
    private Long thirdMatchId;
    private Long standardMatchId;

    public ProcessMatchToMatchOverMessage() {
    }

    public ProcessMatchToMatchOverMessage(String linkId, Long matchStatus, Long thirdMatchId, Long standardMatchId) {
        this.linkId = linkId;
        this.matchStatus = matchStatus;
        this.thirdMatchId = thirdMatchId;
        this.standardMatchId = standardMatchId;
    }
}
