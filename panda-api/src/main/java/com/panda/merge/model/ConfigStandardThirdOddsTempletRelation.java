package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ConfigStandardThirdOddsTempletRelation implements Serializable {
    private Long id;

    private Integer standardCategoryId;

    private String sportType;

    private String pdName;

    private String dataSourceCode;

    private String thirdCategoryId;

    private String thirdTempletSourceId;

    private String thirdTempletName;

    private String standardTempletId;

    private String enableFlag;

    private String oddsOrder;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStandardCategoryId() {
        return standardCategoryId;
    }

    public void setStandardCategoryId(Integer standardCategoryId) {
        this.standardCategoryId = standardCategoryId;
    }

    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public String getPdName() {
        return pdName;
    }

    public void setPdName(String pdName) {
        this.pdName = pdName;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public String getThirdCategoryId() {
        return thirdCategoryId;
    }

    public void setThirdCategoryId(String thirdCategoryId) {
        this.thirdCategoryId = thirdCategoryId;
    }

    public String getThirdTempletSourceId() {
        return thirdTempletSourceId;
    }

    public void setThirdTempletSourceId(String thirdTempletSourceId) {
        this.thirdTempletSourceId = thirdTempletSourceId;
    }

    public String getThirdTempletName() {
        return thirdTempletName;
    }

    public void setThirdTempletName(String thirdTempletName) {
        this.thirdTempletName = thirdTempletName;
    }

    public String getStandardTempletId() {
        return standardTempletId;
    }

    public void setStandardTempletId(String standardTempletId) {
        this.standardTempletId = standardTempletId;
    }

    public String getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(String enableFlag) {
        this.enableFlag = enableFlag;
    }

    public String getOddsOrder() {
        return oddsOrder;
    }

    public void setOddsOrder(String oddsOrder) {
        this.oddsOrder = oddsOrder;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", standardCategoryId=").append(standardCategoryId);
        sb.append(", sportType=").append(sportType);
        sb.append(", pdName=").append(pdName);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", thirdCategoryId=").append(thirdCategoryId);
        sb.append(", thirdTempletSourceId=").append(thirdTempletSourceId);
        sb.append(", thirdTempletName=").append(thirdTempletName);
        sb.append(", standardTempletId=").append(standardTempletId);
        sb.append(", enableFlag=").append(enableFlag);
        sb.append(", oddsOrder=").append(oddsOrder);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}