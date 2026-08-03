package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class StandardSportTeam implements Serializable {
    @ApiModelProperty(value = "id.id")
    private Long id;

    @ApiModelProperty(value = "体育种类id.体育种类id")
    private Long sportId;

    @ApiModelProperty(value = "第三方球队id.third_sport_team.id")
    private Long thirdTeamId;

    @ApiModelProperty(value = "球队区域ID.standard_sport_region.id")
    private Long regionId;

    @ApiModelProperty(value = "国家ID")
    private Long countryId;

    @ApiModelProperty(value = "关联数据源数量")
    private Integer relatedDataSourceCoderNum;

    @ApiModelProperty(value = "关联数据源编码列表.数据样例:SR,BC,188;SR,188;BC,188")
    private String relatedDataSourceCoderList;

    @ApiModelProperty(value = "球队logo.图标的url地址")
    private String logoUrl;

    @ApiModelProperty(value = "球队为双打logo2.图标的url地址")
    private String logoUrl2;

    @ApiModelProperty(value = "球队logo缩略图的url地址")
    private String logoUrlThumb;

    @ApiModelProperty(value = "球队为双打logo2缩略图的url地址")
    private String logoUrlThumb2;

    @ApiModelProperty(value = "球队管理id.该id用于后台管理.")
    private String teamManageId;

    @ApiModelProperty(value = "球队名称编码.用于多语言")
    private Long nameCode;

    @ApiModelProperty(value = "球队类型.1:团体;2:男单;3:女单;4:男双;5:女双;6:混双;7:未知")
    private Integer type;

    @ApiModelProperty(value = "主教练.主教练名称")
    private String coach;

    @ApiModelProperty(value = "主场.比如:所在地和名称")
    private String statium;

    @ApiModelProperty(value = "球队介绍.默认是空")
    private String introduction;

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

    @ApiModelProperty(value = "数据来源编码.取值:SRBC分别代表:SportRadar、FeedConstruc.详情见data_source")
    private String dataSourceCode;
    /**
     * 赛事类别 1:联赛 2:杯赛 3: 其他
     */
    @ApiModelProperty(value = "赛事类型  1:联赛 2:杯赛 3: 其他")
    private Integer matchCategory;
    /**
     * 赛事类型（默认1）1：普通赛事、2：电竞赛事
     */
    @ApiModelProperty(value = "赛事类型（默认1）1：普通赛事、2：电竞赛事")
    private Integer matchType;

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

    public Long getThirdTeamId() {
        return thirdTeamId;
    }

    public void setThirdTeamId(Long thirdTeamId) {
        this.thirdTeamId = thirdTeamId;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Integer getRelatedDataSourceCoderNum() {
        return relatedDataSourceCoderNum;
    }

    public void setRelatedDataSourceCoderNum(Integer relatedDataSourceCoderNum) {
        this.relatedDataSourceCoderNum = relatedDataSourceCoderNum;
    }

    public String getRelatedDataSourceCoderList() {
        return relatedDataSourceCoderList;
    }

    public void setRelatedDataSourceCoderList(String relatedDataSourceCoderList) {
        this.relatedDataSourceCoderList = relatedDataSourceCoderList;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getLogoUrl2() {
        return logoUrl2;
    }

    public void setLogoUrl2(String logoUrl2) {
        this.logoUrl2 = logoUrl2;
    }

    public String getLogoUrlThumb() {
        return logoUrlThumb;
    }

    public void setLogoUrlThumb(String logoUrlThumb) {
        this.logoUrlThumb = logoUrlThumb;
    }

    public String getLogoUrlThumb2() {
        return logoUrlThumb2;
    }

    public void setLogoUrlThumb2(String logoUrlThumb2) {
        this.logoUrlThumb2 = logoUrlThumb2;
    }

    public String getTeamManageId() {
        return teamManageId;
    }

    public void setTeamManageId(String teamManageId) {
        this.teamManageId = teamManageId;
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

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
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

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", sportId=").append(sportId);
        sb.append(", thirdTeamId=").append(thirdTeamId);
        sb.append(", regionId=").append(regionId);
        sb.append(", countryId=").append(countryId);
        sb.append(", relatedDataSourceCoderNum=").append(relatedDataSourceCoderNum);
        sb.append(", relatedDataSourceCoderList=").append(relatedDataSourceCoderList);
        sb.append(", logoUrl=").append(logoUrl);
        sb.append(", logoUrl2=").append(logoUrl2);
        sb.append(", logoUrlThumb=").append(logoUrlThumb);
        sb.append(", logoUrlThumb2=").append(logoUrlThumb2);
        sb.append(", teamManageId=").append(teamManageId);
        sb.append(", nameCode=").append(nameCode);
        sb.append(", type=").append(type);
        sb.append(", coach=").append(coach);
        sb.append(", statium=").append(statium);
        sb.append(", introduction=").append(introduction);
        sb.append(", remark=").append(remark);
        sb.append(", betRadarId=").append(betRadarId);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", nameSpell=").append(nameSpell);
        sb.append(", name=").append(name);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

    public Integer getMatchCategory() {
        return matchCategory;
    }

    public void setMatchCategory(Integer matchCategory) {
        this.matchCategory = matchCategory;
    }

    public Integer getMatchType() {
        return matchType;
    }

    public void setMatchType(Integer matchType) {
        this.matchType = matchType;
    }
}