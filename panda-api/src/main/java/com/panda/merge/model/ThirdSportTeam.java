package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ThirdSportTeam implements Serializable {
    @ApiModelProperty(value = "id.id")
    private Long id;

    @ApiModelProperty(value = "体育种类id.体育种类id")
    private Long sportId;

    @ApiModelProperty(value = "第三方提供的id.第三方球队id")
    private String thirdTeamSourceId;

    @ApiModelProperty(value = "国籍.国籍所属国家id.对应third_sport_region.id")
    private Long countryId;

    @ApiModelProperty(value = "第三方区域id")
    private Long regionId;

    @ApiModelProperty(value = "标准球队id.例如:AB2个记录,融合过程中生成了C记录且C记录对外体现,则AB记录的该字段是C记录的id")
    private Long referenceId;

    @ApiModelProperty(value = "球队logo缩略图的url地址.")
    private String logoUrlThumb;

    @ApiModelProperty(value = "球队logo.图标的url地址")
    private String logoUrl;

    @ApiModelProperty(value = "数据来源编码.取值:SRBC分别代表:SportRadar、FeedConstruc.详情见data_source")
    private String dataSourceCode;

    @ApiModelProperty(value = "主教练.如果第三不提供,则删除该字段")
    private String coach;

    @ApiModelProperty(value = "主场.主场信息,比如:所在地和名称")
    private String statium;

    @ApiModelProperty(value = "球队名称编码.用于多语言")
    private Long nameCode;

    @ApiModelProperty(value = "球队类型.1:团体;2:男单;3:女单;4:男双;5:女双;6:混双;7:未知")
    private Integer type;

    @ApiModelProperty(value = "备注.")
    private String remark;

    @ApiModelProperty(value = "bet_radar_id球队关联id")
    private Integer betRadarId;

    @ApiModelProperty(value = "创建时间.")
    private Long createTime;

    @ApiModelProperty(value = "更新时间.")
    private Long modifyTime;

    @ApiModelProperty(value = "英文名称(冗余字段,用于排序)")
    private String nameSpell;

    @ApiModelProperty(value = "中文简体(冗余字段,用于查询,修改是需要维护)")
    private String name;

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

    public String getThirdTeamSourceId() {
        return thirdTeamSourceId;
    }

    public void setThirdTeamSourceId(String thirdTeamSourceId) {
        this.thirdTeamSourceId = thirdTeamSourceId;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public String getLogoUrlThumb() {
        return logoUrlThumb;
    }

    public void setLogoUrlThumb(String logoUrlThumb) {
        this.logoUrlThumb = logoUrlThumb;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public String getCoach() {
        return coach;
    }

    public void setCoach(String coach) {
        this.coach = coach;
    }

    public String getStatium() {
        return statium;
    }

    public void setStatium(String statium) {
        this.statium = statium;
    }

    public Long getNameCode() {
        return nameCode;
    }

    public void setNameCode(Long nameCode) {
        this.nameCode = nameCode;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getBetRadarId() {
        return betRadarId;
    }

    public void setBetRadarId(Integer betRadarId) {
        this.betRadarId = betRadarId;
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

    public String getNameSpell() {
        return nameSpell;
    }

    public void setNameSpell(String nameSpell) {
        this.nameSpell = nameSpell;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", sportId=").append(sportId);
        sb.append(", thirdTeamSourceId=").append(thirdTeamSourceId);
        sb.append(", countryId=").append(countryId);
        sb.append(", regionId=").append(regionId);
        sb.append(", referenceId=").append(referenceId);
        sb.append(", logoUrlThumb=").append(logoUrlThumb);
        sb.append(", logoUrl=").append(logoUrl);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", coach=").append(coach);
        sb.append(", statium=").append(statium);
        sb.append(", nameCode=").append(nameCode);
        sb.append(", type=").append(type);
        sb.append(", remark=").append(remark);
        sb.append(", betRadarId=").append(betRadarId);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", nameSpell=").append(nameSpell);
        sb.append(", name=").append(name);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}