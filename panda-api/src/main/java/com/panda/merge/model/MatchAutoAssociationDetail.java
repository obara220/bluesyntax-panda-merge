package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchAutoAssociationDetail implements Serializable {
    @ApiModelProperty(value = "数据表id,自增")
    private Long id;

    @ApiModelProperty(value = "运动种类id.对应sport.id")
    private Long sportId;

    @ApiModelProperty(value = "分组id,math_auto_association.id")
    private Long matchAutoAssociationId;

    @ApiModelProperty(value = "第三方赛事ID")
    private Long thirdMatchId;

    @ApiModelProperty(value = "数据来源编码.data_source.code")
    private String dataSourceCode;

    @ApiModelProperty(value = "第三方联赛管理ID列表,仅标准数据源,json格式")
    private String tournamentManagerId;

    @ApiModelProperty(value = "比赛开始时间.UTC时间")
    private Long beginTime;

    @ApiModelProperty(value = "主客队是否相反.与标准球队相比,主客队是否相反.0:否;1:是")
    private Integer homeAwayOpposite;

    @ApiModelProperty(value = "是否为中立场.取值为0和1.1:是中立场,0:非中立场.操盘人员可手动处理")
    private Integer neutralGround;

    @ApiModelProperty(value = "机器计算评分(满分100分),需要除以100，例如9745，代表97.45分")
    private Integer score;

    @ApiModelProperty(value = "是否匹配,0初始化;1已匹配;2未匹配")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建时间.UTC时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间.UTC时间")
    private Long modifyTime;

    @ApiModelProperty(value = "第三方联赛多语言信息,json格式,根据配置保留N种语言信息")
    private String tournamentName;

    @ApiModelProperty(value = "第三方球队多语言信息,json格式,根据配置保留N种语言信息")
    private String teamName;

    @ApiModelProperty(value = "第三方球队管理ID列表,仅标准数据源,json格式")
    private String teamManagerId;

    @ApiModelProperty(value = "机器计算评分详情")
    private String scoreDetail;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public Long getMatchAutoAssociationId() {
        return matchAutoAssociationId;
    }

    public void setMatchAutoAssociationId(Long matchAutoAssociationId) {
        this.matchAutoAssociationId = matchAutoAssociationId;
    }

    public Long getThirdMatchId() {
        return thirdMatchId;
    }

    public void setThirdMatchId(Long thirdMatchId) {
        this.thirdMatchId = thirdMatchId;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public String getTournamentManagerId() {
        return tournamentManagerId;
    }

    public void setTournamentManagerId(String tournamentManagerId) {
        this.tournamentManagerId = tournamentManagerId;
    }

    public Long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Long beginTime) {
        this.beginTime = beginTime;
    }

    public Integer getHomeAwayOpposite() {
        return homeAwayOpposite;
    }

    public void setHomeAwayOpposite(Integer homeAwayOpposite) {
        this.homeAwayOpposite = homeAwayOpposite;
    }

    public Integer getNeutralGround() {
        return neutralGround;
    }

    public void setNeutralGround(Integer neutralGround) {
        this.neutralGround = neutralGround;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamManagerId() {
        return teamManagerId;
    }

    public void setTeamManagerId(String teamManagerId) {
        this.teamManagerId = teamManagerId;
    }

    public String getScoreDetail() {
        return scoreDetail;
    }

    public void setScoreDetail(String scoreDetail) {
        this.scoreDetail = scoreDetail;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", sportId=").append(sportId);
        sb.append(", matchAutoAssociationId=").append(matchAutoAssociationId);
        sb.append(", thirdMatchId=").append(thirdMatchId);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", tournamentManagerId=").append(tournamentManagerId);
        sb.append(", beginTime=").append(beginTime);
        sb.append(", homeAwayOpposite=").append(homeAwayOpposite);
        sb.append(", neutralGround=").append(neutralGround);
        sb.append(", score=").append(score);
        sb.append(", status=").append(status);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", tournamentName=").append(tournamentName);
        sb.append(", teamName=").append(teamName);
        sb.append(", teamManagerId=").append(teamManagerId);
        sb.append(", scoreDetail=").append(scoreDetail);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}