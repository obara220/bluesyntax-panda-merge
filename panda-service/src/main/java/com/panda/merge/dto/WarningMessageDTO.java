package com.panda.merge.dto;

import java.util.List;
import java.util.Map;

public class WarningMessageDTO {

    private String code;

    private List<String> contents;

    private List<Map<String, Object>> contexts;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getContents() {
        return contents;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }

    public List<Map<String, Object>> getContexts() {
        return contexts;
    }

    public void setContexts(List<Map<String, Object>> contexts) {
        this.contexts = contexts;
    }
}
