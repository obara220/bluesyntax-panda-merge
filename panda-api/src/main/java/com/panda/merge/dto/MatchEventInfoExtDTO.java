package com.panda.merge.dto;

import java.util.Map;

public class MatchEventInfoExtDTO extends MatchEventInfoDTO{
    private String linkId;

    private Map<Long, String> replayMatchMap;

    private Long originalStandardMatchId;

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public Map<Long, String> getReplayMatchMap() {
        return replayMatchMap;
    }

    public void setReplayMatchMap(Map<Long, String> replayMatchMap) {
        this.replayMatchMap = replayMatchMap;
    }

    public Long getOriginalStandardMatchId() {
        return originalStandardMatchId;
    }

    public void setOriginalStandardMatchId(Long originalStandardMatchId) {
        this.originalStandardMatchId = originalStandardMatchId;
    }
}
