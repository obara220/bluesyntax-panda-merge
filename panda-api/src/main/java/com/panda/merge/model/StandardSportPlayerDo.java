package com.panda.merge.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class StandardSportPlayerDo implements Serializable {
    @ApiModelProperty(value = "表id")
    private Long id;

    @ApiModelProperty(value = "运动种类ID")
    private Long sportId;

    @ApiModelProperty(value = "区域ID")
    private Long regionId;

    @ApiModelProperty(value = "数据源编码.对应data_source.code")
    private String dataSourceCode;

    @ApiModelProperty(value = "球员管理ID")
    private String playerManagerId;

    @ApiModelProperty(value = "第三方球员id")
    private Long thirdPlayerId;

    @ApiModelProperty(value = "数据商对该球员的id")
    private String thirdSourcePlayerId;

    @ApiModelProperty(value = "球员照片连接地址.")
    private String pictureUrl;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "球员名称编码.对应language_internation.name_code")
    private Long nameCode;

    @ApiModelProperty(value = "球员体重.单位:0.01KG.")
    private Integer weight;

    @ApiModelProperty(value = "球员身高,单位:毫米(mm)")
    private Integer height;

    @ApiModelProperty(value = "球员性别.0:未知;1:男;2:女")
    private Integer gender;

    @ApiModelProperty(value = "球员出生日期.YYYY-MM-DD")
    private String birthday;

    @ApiModelProperty(value = "英文名称(冗余字段,用于排序)")
    private String nameSpell;

    @ApiModelProperty(value = "国籍.国籍所属国家id.对应standard_sport_region.id")
    private Long countryId;

    @ApiModelProperty(value = "个人特效.比如:握拍方式,进攻特长等.")
    private String personalFeature;

    @ApiModelProperty(value = "球员昵称.例如;C罗")
    private String nickName;

    @ApiModelProperty(value = "球员的中文名称,中文简体(冗余字段,用于查询,修改是需要维护)")
    private String name;

    @ApiModelProperty(value = "0")
    private Integer relatedDataSourceCoderNum;

    @ApiModelProperty(value = "关联数据源编码列表.数据样例:SR,BC,188;SR,188;BC,188")
    private String relatedDataSourceCoderList;

    private String remark;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "全语言名称(冗余字段,用户查询)")
    private String allLanguageName;

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

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public String getPlayerManagerId() {
        return playerManagerId;
    }

    public void setPlayerManagerId(String playerManagerId) {
        this.playerManagerId = playerManagerId;
    }

    public Long getThirdPlayerId() {
        return thirdPlayerId;
    }

    public void setThirdPlayerId(Long thirdPlayerId) {
        this.thirdPlayerId = thirdPlayerId;
    }

    public String getThirdSourcePlayerId() {
        return thirdSourcePlayerId;
    }

    public void setThirdSourcePlayerId(String thirdSourcePlayerId) {
        this.thirdSourcePlayerId = thirdSourcePlayerId;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public Long getNameCode() {
        return nameCode;
    }

    public void setNameCode(Long nameCode) {
        this.nameCode = nameCode;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getNameSpell() {
        return nameSpell;
    }

    public void setNameSpell(String nameSpell) {
        this.nameSpell = nameSpell;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public String getPersonalFeature() {
        return personalFeature;
    }

    public void setPersonalFeature(String personalFeature) {
        this.personalFeature = personalFeature;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getAllLanguageName() {
        return allLanguageName;
    }

    public void setAllLanguageName(String allLanguageName) {
        this.allLanguageName = allLanguageName;
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
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", playerManagerId=").append(playerManagerId);
        sb.append(", thirdPlayerId=").append(thirdPlayerId);
        sb.append(", thirdSourcePlayerId=").append(thirdSourcePlayerId);
        sb.append(", pictureUrl=").append(pictureUrl);
        sb.append(", nameCode=").append(nameCode);
        sb.append(", weight=").append(weight);
        sb.append(", height=").append(height);
        sb.append(", gender=").append(gender);
        sb.append(", birthday=").append(birthday);
        sb.append(", nameSpell=").append(nameSpell);
        sb.append(", countryId=").append(countryId);
        sb.append(", personalFeature=").append(personalFeature);
        sb.append(", nickName=").append(nickName);
        sb.append(", name=").append(name);
        sb.append(", relatedDataSourceCoderNum=").append(relatedDataSourceCoderNum);
        sb.append(", relatedDataSourceCoderList=").append(relatedDataSourceCoderList);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", allLanguageName=").append(allLanguageName);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}