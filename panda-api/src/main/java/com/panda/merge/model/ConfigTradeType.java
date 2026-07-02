package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ConfigTradeType implements Serializable {
    private Long id;

    @ApiModelProperty(value = "生效级别1:玩法3:赛事")
    private Integer level;

    @ApiModelProperty(value = "标准赛事id")
    private String standardMatchId;

    @ApiModelProperty(value = "标准玩法id")
    private String standardCategoryId;

    @ApiModelProperty(value = "操盘类型0:自动操盘1:手动操盘")
    private Integer tradeType;

    @ApiModelProperty(value = "配置修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "操作人ID")
    private Long operaterId;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getStandardMatchId() {
        return standardMatchId;
    }

    public void setStandardMatchId(String standardMatchId) {
        this.standardMatchId = standardMatchId;
    }

    public String getStandardCategoryId() {
        return standardCategoryId;
    }

    public void setStandardCategoryId(String standardCategoryId) {
        this.standardCategoryId = standardCategoryId;
    }

    public Integer getTradeType() {
        return tradeType;
    }

    public void setTradeType(Integer tradeType) {
        this.tradeType = tradeType;
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

    public Long getOperaterId() {
        return operaterId;
    }

    public void setOperaterId(Long operaterId) {
        this.operaterId = operaterId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", level=").append(level);
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", standardCategoryId=").append(standardCategoryId);
        sb.append(", tradeType=").append(tradeType);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", operaterId=").append(operaterId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}