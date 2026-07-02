package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

public class MachineMatchTest implements Serializable {
    private Long id;

    @ApiModelProperty(value = "匹配id")
    private String relationId;

    @ApiModelProperty(value = "三方赛事id")
    private String thirdMatchId;

    @ApiModelProperty(value = "运动种类")
    private String sportId;

    @ApiModelProperty(value = "三方赛事源id")
    private String thirdMatchSourceId;

    @ApiModelProperty(value = "数据源编码")
    private String dataSourceCode;

    @ApiModelProperty(value = "赛制")
    private String format;

    @ApiModelProperty(value = "联赛id")
    private String tournamentId;

    @ApiModelProperty(value = "联赛中文名")
    private String tournamentNameCn;

    @ApiModelProperty(value = "联赛英文名")
    private String tournamentNameEn;

    @ApiModelProperty(value = "1队中文名")
    private String t1NameCn;

    @ApiModelProperty(value = "1队英文名")
    private String t1NameEn;

    @ApiModelProperty(value = "2队中文名")
    private String t2NameCn;

    @ApiModelProperty(value = "2队英文名")
    private String t2NameEn;

    @ApiModelProperty(value = "开赛时间")
    private Date beginTime;

    @ApiModelProperty(value = "得分")
    private String score;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date modifyTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRelationId() {
        return relationId;
    }

    public void setRelationId(String relationId) {
        this.relationId = relationId;
    }

    public String getThirdMatchId() {
        return thirdMatchId;
    }

    public void setThirdMatchId(String thirdMatchId) {
        this.thirdMatchId = thirdMatchId;
    }

    public String getSportId() {
        return sportId;
    }

    public void setSportId(String sportId) {
        this.sportId = sportId;
    }

    public String getThirdMatchSourceId() {
        return thirdMatchSourceId;
    }

    public void setThirdMatchSourceId(String thirdMatchSourceId) {
        this.thirdMatchSourceId = thirdMatchSourceId;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(String tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getTournamentNameCn() {
        return tournamentNameCn;
    }

    public void setTournamentNameCn(String tournamentNameCn) {
        this.tournamentNameCn = tournamentNameCn;
    }

    public String getTournamentNameEn() {
        return tournamentNameEn;
    }

    public void setTournamentNameEn(String tournamentNameEn) {
        this.tournamentNameEn = tournamentNameEn;
    }

    public String getT1NameCn() {
        return t1NameCn;
    }

    public void setT1NameCn(String t1NameCn) {
        this.t1NameCn = t1NameCn;
    }

    public String getT1NameEn() {
        return t1NameEn;
    }

    public void setT1NameEn(String t1NameEn) {
        this.t1NameEn = t1NameEn;
    }

    public String getT2NameCn() {
        return t2NameCn;
    }

    public void setT2NameCn(String t2NameCn) {
        this.t2NameCn = t2NameCn;
    }

    public String getT2NameEn() {
        return t2NameEn;
    }

    public void setT2NameEn(String t2NameEn) {
        this.t2NameEn = t2NameEn;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", relationId=").append(relationId);
        sb.append(", thirdMatchId=").append(thirdMatchId);
        sb.append(", sportId=").append(sportId);
        sb.append(", thirdMatchSourceId=").append(thirdMatchSourceId);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", format=").append(format);
        sb.append(", tournamentId=").append(tournamentId);
        sb.append(", tournamentNameCn=").append(tournamentNameCn);
        sb.append(", tournamentNameEn=").append(tournamentNameEn);
        sb.append(", t1NameCn=").append(t1NameCn);
        sb.append(", t1NameEn=").append(t1NameEn);
        sb.append(", t2NameCn=").append(t2NameCn);
        sb.append(", t2NameEn=").append(t2NameEn);
        sb.append(", beginTime=").append(beginTime);
        sb.append(", score=").append(score);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}