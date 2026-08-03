package com.panda.merge.dto;

import java.util.List;
import java.util.Map;

public class ThirdMatchStatusExtDTO extends ThirdMatchStatusDTO{
    private String linkId;

    private Map<Long, String> replayMatchMap;

    private Long beginTime;

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

    public Long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Long beginTime) {
        this.beginTime = beginTime;
    }
}
