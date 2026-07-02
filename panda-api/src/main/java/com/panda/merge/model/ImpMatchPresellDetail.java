package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ImpMatchPresellDetail implements Serializable {
    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "导入日志主表id")
    private Long mainId;

    @ApiModelProperty(value = "赛事id")
    private String matchManageId;

    @ApiModelProperty(value = "比赛名称")
    private String tournamentName;

    @ApiModelProperty(value = "开赛时间")
    private Long beginTime;

    @ApiModelProperty(value = "主队名称")
    private String teamHomeName;

    @ApiModelProperty(value = "客队名称")
    private String teamAwayName;

    @ApiModelProperty(value = "赛前操盘手")
    private String preTrader;

    @ApiModelProperty(value = "赛前操盘平台")
    private String preRiskManagerCode;

    @ApiModelProperty(value = "滚球操盘平台")
    private String liveTrader;

    @ApiModelProperty(value = "滚球操盘平台")
    private String liveRiskManagerCode;

    @ApiModelProperty(value = "赛事状态源")
    private String matchStatusCode;

    @ApiModelProperty(value = "赛果审核员")
    private String auditor;

    @ApiModelProperty(value = "赛事标签")
    private String label;

    @ApiModelProperty(value = "导入是否成功")
    private Integer impIsSuccess;

    @ApiModelProperty(value = "导入描述")
    private String impDescription;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMainId() {
        return mainId;
    }

    public void setMainId(Long mainId) {
        this.mainId = mainId;
    }

    public String getMatchManageId() {
        return matchManageId;
    }

    public void setMatchManageId(String matchManageId) {
        this.matchManageId = matchManageId;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public Long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Long beginTime) {
        this.beginTime = beginTime;
    }

    public String getTeamHomeName() {
        return teamHomeName;
    }

    public void setTeamHomeName(String teamHomeName) {
        this.teamHomeName = teamHomeName;
    }

    public String getTeamAwayName() {
        return teamAwayName;
    }

    public void setTeamAwayName(String teamAwayName) {
        this.teamAwayName = teamAwayName;
    }

    public String getPreTrader() {
        return preTrader;
    }

    public void setPreTrader(String preTrader) {
        this.preTrader = preTrader;
    }

    public String getPreRiskManagerCode() {
        return preRiskManagerCode;
    }

    public void setPreRiskManagerCode(String preRiskManagerCode) {
        this.preRiskManagerCode = preRiskManagerCode;
    }

    public String getLiveTrader() {
        return liveTrader;
    }

    public void setLiveTrader(String liveTrader) {
        this.liveTrader = liveTrader;
    }

    public String getLiveRiskManagerCode() {
        return liveRiskManagerCode;
    }

    public void setLiveRiskManagerCode(String liveRiskManagerCode) {
        this.liveRiskManagerCode = liveRiskManagerCode;
    }

    public String getMatchStatusCode() {
        return matchStatusCode;
    }

    public void setMatchStatusCode(String matchStatusCode) {
        this.matchStatusCode = matchStatusCode;
    }

    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getImpIsSuccess() {
        return impIsSuccess;
    }

    public void setImpIsSuccess(Integer impIsSuccess) {
        this.impIsSuccess = impIsSuccess;
    }

    public String getImpDescription() {
        return impDescription;
    }

    public void setImpDescription(String impDescription) {
        this.impDescription = impDescription;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", mainId=").append(mainId);
        sb.append(", matchManageId=").append(matchManageId);
        sb.append(", tournamentName=").append(tournamentName);
        sb.append(", beginTime=").append(beginTime);
        sb.append(", teamHomeName=").append(teamHomeName);
        sb.append(", teamAwayName=").append(teamAwayName);
        sb.append(", preTrader=").append(preTrader);
        sb.append(", preRiskManagerCode=").append(preRiskManagerCode);
        sb.append(", liveTrader=").append(liveTrader);
        sb.append(", liveRiskManagerCode=").append(liveRiskManagerCode);
        sb.append(", matchStatusCode=").append(matchStatusCode);
        sb.append(", auditor=").append(auditor);
        sb.append(", label=").append(label);
        sb.append(", impIsSuccess=").append(impIsSuccess);
        sb.append(", impDescription=").append(impDescription);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}