package com.panda.merge.dto;

import lombok.Data;

import java.util.List;


public class PlayerModifyDTO {

    private Long teamId;

    private String thirdTeamSourceId;

    private String dataSourceCode;

    private Long standardMatchId;

    private String matchManageId;

    private List<String> thirdPlayerSourceIds;

    private String message;

    private String messageEn;

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getThirdTeamSourceId() {
        return thirdTeamSourceId;
    }

    public void setThirdTeamSourceId(String thirdTeamSourceId) {
        this.thirdTeamSourceId = thirdTeamSourceId;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public Long getStandardMatchId() {
        return standardMatchId;
    }

    public void setStandardMatchId(Long standardMatchId) {
        this.standardMatchId = standardMatchId;
    }

    public String getMatchManageId() {
        return matchManageId;
    }

    public void setMatchManageId(String matchManageId) {
        this.matchManageId = matchManageId;
    }

    public List<String> getThirdPlayerSourceIds() {
        return thirdPlayerSourceIds;
    }

    public void setThirdPlayerSourceIds(List<String> thirdPlayerSourceIds) {
        this.thirdPlayerSourceIds = thirdPlayerSourceIds;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageEn() {
        return messageEn;
    }

    public void setMessageEn(String messageEn) {
        this.messageEn = messageEn;
    }

    @Override
    public String toString() {
        return "PlayerModifyDTO{" +
                "teamId=" + teamId +
                ", thirdTeamSourceId='" + thirdTeamSourceId + '\'' +
                ", dataSourceCode='" + dataSourceCode + '\'' +
                ", standardMatchId=" + standardMatchId +
                ", matchManageId='" + matchManageId + '\'' +
                ", thirdPlayerSourceIds=" + thirdPlayerSourceIds +
                ", message='" + message + '\'' +
                ", messageEn='" + messageEn + '\'' +
                '}';
    }
}
