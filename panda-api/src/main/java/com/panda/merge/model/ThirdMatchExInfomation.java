package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ThirdMatchExInfomation implements Serializable {
    @ApiModelProperty(value = "数据来源ID:赛事源ID")
    private String id;

    @ApiModelProperty(value = "赛事源ID")
    private String thirdMatchSourceId;

    @ApiModelProperty(value = "运动类型")
    private Long sportId;

    @ApiModelProperty(value = "数据来源")
    private String dataSourceCode;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "主队教练信息json")
    private String homeCoach;

    @ApiModelProperty(value = "客队教练信息json")
    private String awayCoach;

    @ApiModelProperty(value = "情报信息列表json")
    private String informations;

    @ApiModelProperty(value = "赔率情况分析json")
    private String winningOdds;

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

    public String getHomeCoach() {
        return homeCoach;
    }

    public void setHomeCoach(String homeCoach) {
        this.homeCoach = homeCoach;
    }

    public String getAwayCoach() {
        return awayCoach;
    }

    public void setAwayCoach(String awayCoach) {
        this.awayCoach = awayCoach;
    }

    public String getInformations() {
        return informations;
    }

    public void setInformations(String informations) {
        this.informations = informations;
    }

    public String getWinningOdds() {
        return winningOdds;
    }

    public void setWinningOdds(String winningOdds) {
        this.winningOdds = winningOdds;
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
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", homeCoach=").append(homeCoach);
        sb.append(", awayCoach=").append(awayCoach);
        sb.append(", informations=").append(informations);
        sb.append(", winningOdds=").append(winningOdds);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}