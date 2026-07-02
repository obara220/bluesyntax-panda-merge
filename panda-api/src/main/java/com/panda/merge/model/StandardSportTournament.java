package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class StandardSportTournament implements Serializable {
    @ApiModelProperty(value = "表ID,自增.id")
    private Long id;

    @ApiModelProperty(value = "运动种类ID.联赛所属体育种类id,对应sport.id")
    private Long sportId;

    @ApiModelProperty(value = "第三方联赛id.第三方联赛在表third_sport_tournament中的id")
    private Long thirdTournamentId;

    @ApiModelProperty(value = "所属标准区域id.对应standard_sport_region.id")
    private Long regionId;

    @ApiModelProperty(value = "数据来源类型.0:系统设置;1:人工设置")
    private Integer regionType;

    @ApiModelProperty(value = "联赛分级.1:一级联赛;2:二级联赛;3:三级联赛;以此类推;0:未分级")
    private Integer tournamentLevel;

    @ApiModelProperty(value = "后台管理使用的联赛id.")
    private String tournamentManagerId;

    @ApiModelProperty(value = "第三方联赛原始id.第三方提供的联赛的id")
    private String thirdTournamentSourceId;

    @ApiModelProperty(value = "联赛名称编码.联赛名称编码.用于多语言")
    private Long nameCode;

    @ApiModelProperty(value = "赛季国际化Code")
    private Long seasonNameCode;

    @ApiModelProperty(value = "数据来源编码.取值:SRBC分别代表:SportRadar、FeedConstruc.详情见data_source")
    private String dataSourceCode;

    @ApiModelProperty(value = "当为子联赛时取父联赛的id")
    private String fatherTournamentId;

    @ApiModelProperty(value = "0：否1：是默认0，标识是否是子联赛")
    private Integer simpleFlage;

    @ApiModelProperty(value = "当前赛季id")
    private String currentSeasonId;

    @ApiModelProperty(value = "当前轮类型：Group，Cup，Qualification")
    private String currentRoundType;

    @ApiModelProperty(value = "当类型Group时存在值，原始示例：number='1'")
    private Integer currentRoundNumber;

    @ApiModelProperty(value = "如果存在值就传，原始示例：name='semifinal'")
    private String currentRoundName;

    @ApiModelProperty(value = "是否热门联赛0:false1:true")
    private Integer hotStatus;

    @ApiModelProperty(value = "联赛logo.图标的url地址")
    private String logoUrl;

    @ApiModelProperty(value = "联赛logo.缩略图的url地址")
    private String logoUrlThumb;

    @ApiModelProperty(value = "关联数据源数量")
    private Integer relatedDataSourceCoderNum;

    @ApiModelProperty(value = "关联数据源编码列表.数据样例:SR,BC,188;SR,188;BC,188(冗余字段,用于查询)")
    private String relatedDataSourceCoderList;

    @ApiModelProperty(value = "简介.")
    private String introduction;

    @ApiModelProperty(value = "备注.")
    private String remark;

    @ApiModelProperty(value = "编辑多语言锁状态")
    private Integer isLock;

    @ApiModelProperty(value = "创建时间.")
    private Long createTime;

    @ApiModelProperty(value = "使用状态0:Disable;1:Enable")
    private Integer operatorStatus;

    @ApiModelProperty(value = "修改时间.")
    private Long modifyTime;

    @ApiModelProperty(value = "英文名称(冗余字段,用于排序)")
    private String nameSpell;

    @ApiModelProperty(value = "联赛归属,1:无2:东京奥运会")
    private String tournamentType;

    @ApiModelProperty(value = "中文简体(冗余字段,用于查询,修改是需要维护)")
    private String name;

    @ApiModelProperty(value = "联赛官网")
    private String leagueUrl;

    @ApiModelProperty(value = "是否有对应的三方联赛（默认1）0：没有、1：有")
    private Integer hasRelation;

    @ApiModelProperty(value = "是否受赛事关联影响0不是1是")
    private Integer influenceStatus;

    @ApiModelProperty(value = "联赛名繁体")
    private String nameZh;

    @ApiModelProperty(value = "赛季")
    private String season;

    @ApiModelProperty(value = "PLS标准联赛ID")
    private Long plsStandardTournamentId;

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

    public Long getThirdTournamentId() {
        return thirdTournamentId;
    }

    public void setThirdTournamentId(Long thirdTournamentId) {
        this.thirdTournamentId = thirdTournamentId;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public Integer getRegionType() {
        return regionType;
    }

    public void setRegionType(Integer regionType) {
        this.regionType = regionType;
    }

    public Integer getTournamentLevel() {
        return tournamentLevel;
    }

    public void setTournamentLevel(Integer tournamentLevel) {
        this.tournamentLevel = tournamentLevel;
    }

    public String getTournamentManagerId() {
        return tournamentManagerId;
    }

    public void setTournamentManagerId(String tournamentManagerId) {
        this.tournamentManagerId = tournamentManagerId;
    }

    public String getThirdTournamentSourceId() {
        return thirdTournamentSourceId;
    }

    public void setThirdTournamentSourceId(String thirdTournamentSourceId) {
        this.thirdTournamentSourceId = thirdTournamentSourceId;
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

    public String getCurrentSeasonId() {
        return currentSeasonId;
    }

    public void setCurrentSeasonId(String currentSeasonId) {
        this.currentSeasonId = currentSeasonId;
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

    public Integer getHotStatus() {
        return hotStatus;
    }

    public void setHotStatus(Integer hotStatus) {
        this.hotStatus = hotStatus;
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

    public Integer getIsLock() {
        return isLock;
    }

    public void setIsLock(Integer isLock) {
        this.isLock = isLock;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getOperatorStatus() {
        return operatorStatus;
    }

    public void setOperatorStatus(Integer operatorStatus) {
        this.operatorStatus = operatorStatus;
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

    public String getTournamentType() {
        return tournamentType;
    }

    public void setTournamentType(String tournamentType) {
        this.tournamentType = tournamentType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLeagueUrl() {
        return leagueUrl;
    }

    public void setLeagueUrl(String leagueUrl) {
        this.leagueUrl = leagueUrl;
    }

    public Integer getHasRelation() {
        return hasRelation;
    }

    public void setHasRelation(Integer hasRelation) {
        this.hasRelation = hasRelation;
    }

    public Integer getInfluenceStatus() {
        return influenceStatus;
    }

    public void setInfluenceStatus(Integer influenceStatus) {
        this.influenceStatus = influenceStatus;
    }

    public String getNameZh() {
        return nameZh;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public Long getPlsStandardTournamentId() {
        return plsStandardTournamentId;
    }

    public void setPlsStandardTournamentId(Long plsStandardTournamentId) {
        this.plsStandardTournamentId = plsStandardTournamentId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", sportId=").append(sportId);
        sb.append(", thirdTournamentId=").append(thirdTournamentId);
        sb.append(", regionId=").append(regionId);
        sb.append(", regionType=").append(regionType);
        sb.append(", tournamentLevel=").append(tournamentLevel);
        sb.append(", tournamentManagerId=").append(tournamentManagerId);
        sb.append(", thirdTournamentSourceId=").append(thirdTournamentSourceId);
        sb.append(", nameCode=").append(nameCode);
        sb.append(", seasonNameCode=").append(seasonNameCode);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", fatherTournamentId=").append(fatherTournamentId);
        sb.append(", simpleFlage=").append(simpleFlage);
        sb.append(", currentSeasonId=").append(currentSeasonId);
        sb.append(", currentRoundType=").append(currentRoundType);
        sb.append(", currentRoundNumber=").append(currentRoundNumber);
        sb.append(", currentRoundName=").append(currentRoundName);
        sb.append(", hotStatus=").append(hotStatus);
        sb.append(", logoUrl=").append(logoUrl);
        sb.append(", logoUrlThumb=").append(logoUrlThumb);
        sb.append(", relatedDataSourceCoderNum=").append(relatedDataSourceCoderNum);
        sb.append(", relatedDataSourceCoderList=").append(relatedDataSourceCoderList);
        sb.append(", introduction=").append(introduction);
        sb.append(", remark=").append(remark);
        sb.append(", isLock=").append(isLock);
        sb.append(", createTime=").append(createTime);
        sb.append(", operatorStatus=").append(operatorStatus);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", nameSpell=").append(nameSpell);
        sb.append(", tournamentType=").append(tournamentType);
        sb.append(", name=").append(name);
        sb.append(", leagueUrl=").append(leagueUrl);
        sb.append(", hasRelation=").append(hasRelation);
        sb.append(", influenceStatus=").append(influenceStatus);
        sb.append(", nameZh=").append(nameZh);
        sb.append(", season=").append(season);
        sb.append(", plsStandardTournamentId=").append(plsStandardTournamentId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}