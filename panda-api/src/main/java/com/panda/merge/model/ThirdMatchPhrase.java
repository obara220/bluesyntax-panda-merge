package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ThirdMatchPhrase implements Serializable {
    @ApiModelProperty(value = "数据来源ID:文字直播ID")
    private String id;

    @ApiModelProperty(value = "数据源赛事id")
    private String thirdMatchSourceId;

    @ApiModelProperty(value = "运动类型")
    private Long sportId;

    @ApiModelProperty(value = "数据来源")
    private String dataSourceCode;

    @ApiModelProperty(value = "发生时间")
    private String time;

    @ApiModelProperty(value = "中文文字内容")
    private String cnText;

    @ApiModelProperty(value = "中文英字内容")
    private String enText;

    @ApiModelProperty(value = "当前比分")
    private String scores;

    @ApiModelProperty(value = "所属球队(0公共，1主队，2客队)")
    private Integer team;

    @ApiModelProperty(value = "赛事阶段")
    private String matchPeriod;

    @ApiModelProperty(value = "是否已经下发(0:否,1:是)")
    private Integer sendData;

    @ApiModelProperty(value = "线路ID")
    private String linkId;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThirdMatchSourceId() {
        return thirdMatchSourceId;
    }

    public void setThirdMatchSourceId(String thirdMatchSourceId) {
        this.thirdMatchSourceId = thirdMatchSourceId;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCnText() {
        return cnText;
    }

    public void setCnText(String cnText) {
        this.cnText = cnText;
    }

    public String getEnText() {
        return enText;
    }

    public void setEnText(String enText) {
        this.enText = enText;
    }

    public String getScores() {
        return scores;
    }

    public void setScores(String scores) {
        this.scores = scores;
    }

    public Integer getTeam() {
        return team;
    }

    public void setTeam(Integer team) {
        this.team = team;
    }

    public String getMatchPeriod() {
        return matchPeriod;
    }

    public void setMatchPeriod(String matchPeriod) {
        this.matchPeriod = matchPeriod;
    }

    public Integer getSendData() {
        return sendData;
    }

    public void setSendData(Integer sendData) {
        this.sendData = sendData;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", thirdMatchSourceId=").append(thirdMatchSourceId);
        sb.append(", sportId=").append(sportId);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", time=").append(time);
        sb.append(", cnText=").append(cnText);
        sb.append(", enText=").append(enText);
        sb.append(", scores=").append(scores);
        sb.append(", team=").append(team);
        sb.append(", matchPeriod=").append(matchPeriod);
        sb.append(", sendData=").append(sendData);
        sb.append(", linkId=").append(linkId);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}