package com.panda.merge.dto;

import com.panda.merge.common.enums.WarningTypeEnum;

import java.util.List;
import java.util.Map;

public class WarningEventDTO {

    private String linkId;

    private WarningTypeEnum warningType;

    private List<Map<String, Object>> contexts;

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public WarningTypeEnum getWarningType() {
        return warningType;
    }

    public void setWarningType(WarningTypeEnum warningType) {
        this.warningType = warningType;
    }

    public List<Map<String, Object>> getContexts() {
        return contexts;
    }

    public void setContexts(List<Map<String, Object>> contexts) {
        this.contexts = contexts;
    }
}
