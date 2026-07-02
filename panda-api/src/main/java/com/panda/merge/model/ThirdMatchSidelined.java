package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ThirdMatchSidelined implements Serializable {
    @ApiModelProperty(value = "数据来源ID+赛事源ID+球队源ID+球员源ID")
    private String id;

    @ApiModelProperty(value = "数据源赛事id")
    private String thirdMatchSourceId;

    @ApiModelProperty(value = "运动类型")
    private Long sportId;

    @ApiModelProperty(value = "数据来源")
    private String dataSourceCode;

    @ApiModelProperty(value = "数据源球队id")
    private String thirdTeamSourceId;

    @ApiModelProperty(value = "数据源球员id")
    private String thirdPlayerSourceId;

    @ApiModelProperty(value = "球员名称")
    private String thirdPlayerName;

    @ApiModelProperty(value = "球员英文名称")
    private String thirdPlayerEnName;

    @ApiModelProperty(value = "球员头像")
    private String thirdPlayerPicUrl;

    @ApiModelProperty(value = "球员位置")
    private String position;

    @ApiModelProperty(value = "球衣号码")
    private Integer shirtNumber;

    @ApiModelProperty(value = "主客队标识(1主队,2客队)")
    private Integer homeAway;

    @ApiModelProperty(value = "缺阵原因")
    private String reason;

    @ApiModelProperty(value = "原因描述id")
    private String descriptionId;

    @ApiModelProperty(value = "原因描述")
    private String description;

    private Long createTime;

    private Long modifyTime;

    @ApiModelProperty(value = "是否失效(0:否,1:是)")
    private Integer invalid;

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

    public String getThirdTeamSourceId() {
        return thirdTeamSourceId;
    }

    public void setThirdTeamSourceId(String thirdTeamSourceId) {
        this.thirdTeamSourceId = thirdTeamSourceId;
    }

    public String getThirdPlayerSourceId() {
        return thirdPlayerSourceId;
    }

    public void setThirdPlayerSourceId(String thirdPlayerSourceId) {
        this.thirdPlayerSourceId = thirdPlayerSourceId;
    }

    public String getThirdPlayerName() {
        return thirdPlayerName;
    }

    public void setThirdPlayerName(String thirdPlayerName) {
        this.thirdPlayerName = thirdPlayerName;
    }

    public String getThirdPlayerEnName() {
        return thirdPlayerEnName;
    }

    public void setThirdPlayerEnName(String thirdPlayerEnName) {
        this.thirdPlayerEnName = thirdPlayerEnName;
    }

    public String getThirdPlayerPicUrl() {
        return thirdPlayerPicUrl;
    }

    public void setThirdPlayerPicUrl(String thirdPlayerPicUrl) {
        this.thirdPlayerPicUrl = thirdPlayerPicUrl;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Integer getShirtNumber() {
        return shirtNumber;
    }

    public void setShirtNumber(Integer shirtNumber) {
        this.shirtNumber = shirtNumber;
    }

    public Integer getHomeAway() {
        return homeAway;
    }

    public void setHomeAway(Integer homeAway) {
        this.homeAway = homeAway;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescriptionId() {
        return descriptionId;
    }

    public void setDescriptionId(String descriptionId) {
        this.descriptionId = descriptionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getInvalid() {
        return invalid;
    }

    public void setInvalid(Integer invalid) {
        this.invalid = invalid;
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
        sb.append(", thirdTeamSourceId=").append(thirdTeamSourceId);
        sb.append(", thirdPlayerSourceId=").append(thirdPlayerSourceId);
        sb.append(", thirdPlayerName=").append(thirdPlayerName);
        sb.append(", thirdPlayerEnName=").append(thirdPlayerEnName);
        sb.append(", thirdPlayerPicUrl=").append(thirdPlayerPicUrl);
        sb.append(", position=").append(position);
        sb.append(", shirtNumber=").append(shirtNumber);
        sb.append(", homeAway=").append(homeAway);
        sb.append(", reason=").append(reason);
        sb.append(", descriptionId=").append(descriptionId);
        sb.append(", description=").append(description);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", invalid=").append(invalid);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}