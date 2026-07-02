package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ThirdSportTournament implements Serializable {
    @ApiModelProperty(value = "表ID,自增.id")
    private Long id;

    @ApiModelProperty(value = "运动种类id.联赛所属体育种类id,对应sport.id")
    private Long sportId;

    @ApiModelProperty(value = "运动区域id.当前联赛所属体育区域id.对应sport_region.id")
    private Long regionId;

    @ApiModelProperty(value = "关联联赛id.例如:AB2个记录,融合过程中生成了C记录且C记录对外体现,则AB记录的该字段是C记录的id")
    private Long referenceId;

    @ApiModelProperty(value = "第三方联赛原始id.第三方提供的联赛的id")
    private String thirdTournamentSourceId;

    @ApiModelProperty(value = "三方数据源当前赛季ID")
    private String thirdSeasonSourceId;

    @ApiModelProperty(value = "联赛logo.图标的url地址")
    private String logoUrl;

    @ApiModelProperty(value = "联赛logo缩略图的url地址.")
    private String logoUrlThumb;

    @ApiModelProperty(value = "联赛名称编码.用于多语言")
    private Long nameCode;

    @ApiModelProperty(value = "赛季国际化Code")
    private Long seasonNameCode;

    @ApiModelProperty(value = "数据来源编码.取值:SRBC分别代表:SportRadar、FeedConstruc.详情见data_source")
    private String dataSourceCode;

    @ApiModelProperty(value = "当前赛季id")
    private String currentSeasonId;

    @ApiModelProperty(value = "当为子联赛时取父联赛的id")
    private String fatherTournamentId;

    @ApiModelProperty(value = "0：否1：是默认0，标识是否是子联赛")
    private Integer simpleFlage;

    @ApiModelProperty(value = "当前轮类型：Group，Cup，Qualification")
    private String currentRoundType;

    @ApiModelProperty(value = "当类型Group时存在值，原始示例：number='1'")
    private Integer currentRoundNumber;

    @ApiModelProperty(value = "如果存在值就传，原始示例：name='semifinal'")
    private String currentRoundName;

    @ApiModelProperty(value = "备注.")
    private String remark;

    @ApiModelProperty(value = "英文名称(冗余字段,用于排序)")
    private String nameSpell;

    @ApiModelProperty(value = "中文简体(冗余字段,用于查询,修改是需要维护)")
    private String name;

    @ApiModelProperty(value = "创建时间.")
    private Long createTime;

    @ApiModelProperty(value = "修改时间.")
    private Long modifyTime;

    @ApiModelProperty(value = "赛季")
    private String season;

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

    public String getThirdTournamentSourceId() {
        return thirdTournamentSourceId;
    }

    public void setThirdTournamentSourceId(String thirdTournamentSourceId) {
        this.thirdTournamentSourceId = thirdTournamentSourceId;
    }

    public String getThirdSeasonSourceId() {
        return thirdSeasonSourceId;
    }

    public void setThirdSeasonSourceId(String thirdSeasonSourceId) {
        this.thirdSeasonSourceId = thirdSeasonSourceId;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getLogoUrlThumb() {
        return logoUrlThumb;
    }

    public void setLogoUrlThumb(String logoUrlThumb) {
        this.logoUrlThumb = logoUrlThumb;
    }

    public Long getNameCode() {
        return nameCode;
    }

    public void setNameCode(Long nameCode) {
        this.nameCode = nameCode;
    }

    public Long getSeasonNameCode() {
        return seasonNameCode;
    }

    public void setSeasonNameCode(Long seasonNameCode) {
        this.seasonNameCode = seasonNameCode;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public String getCurrentSeasonId() {
        return currentSeasonId;
    }

    public void setCurrentSeasonId(String currentSeasonId) {
        this.currentSeasonId = currentSeasonId;
    }

    public String getFatherTournamentId() {
        return fatherTournamentId;
    }

    public void setFatherTournamentId(String fatherTournamentId) {
        this.fatherTournamentId = fatherTournamentId;
    }

    public Integer getSimpleFlage() {
        return simpleFlage;
    }

    public void setSimpleFlage(Integer simpleFlage) {
        this.simpleFlage = simpleFlage;
    }

    public String getCurrentRoundType() {
        return currentRoundType;
    }

    public void setCurrentRoundType(String currentRoundType) {
        this.currentRoundType = currentRoundType;
    }

    public Integer getCurrentRoundNumber() {
        return currentRoundNumber;
    }

    public void setCurrentRoundNumber(Integer currentRoundNumber) {
        this.currentRoundNumber = currentRoundNumber;
    }

    public String getCurrentRoundName() {
        return currentRoundName;
    }

    public void setCurrentRoundName(String currentRoundName) {
        this.currentRoundName = currentRoundName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public Integer getMatchType() {
        return matchType;
    }

    public void setMatchType(Integer matchType) {
        this.matchType = matchType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", sportId=").append(sportId);
        sb.append(", regionId=").append(regionId);
        sb.append(", referenceId=").append(referenceId);
        sb.append(", thirdTournamentSourceId=").append(thirdTournamentSourceId);
        sb.append(", thirdSeasonSourceId=").append(thirdSeasonSourceId);
        sb.append(", logoUrl=").append(logoUrl);
        sb.append(", logoUrlThumb=").append(logoUrlThumb);
        sb.append(", nameCode=").append(nameCode);
        sb.append(", seasonNameCode=").append(seasonNameCode);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", currentSeasonId=").append(currentSeasonId);
        sb.append(", fatherTournamentId=").append(fatherTournamentId);
        sb.append(", simpleFlage=").append(simpleFlage);
        sb.append(", currentRoundType=").append(currentRoundType);
        sb.append(", currentRoundNumber=").append(currentRoundNumber);
        sb.append(", currentRoundName=").append(currentRoundName);
        sb.append(", remark=").append(remark);
        sb.append(", nameSpell=").append(nameSpell);
        sb.append(", name=").append(name);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", season=").append(season);
        sb.append(", matchType=").append(matchType);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}