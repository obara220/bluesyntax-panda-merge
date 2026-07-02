package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ImpMatchPresellMain implements Serializable {
    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "导入总记录数")
    private Integer impCounts;

    @ApiModelProperty(value = "导入成功数")
    private Integer impSuccessCounts;

    @ApiModelProperty(value = "导入失败数")
    private Integer impFailureCounts;

    @ApiModelProperty(value = "操作人")
    private String operator;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "操作时间")
    private Long operatTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getImpCounts() {
        return impCounts;
    }

    public void setImpCounts(Integer impCounts) {
        this.impCounts = impCounts;
    }

    public Integer getImpSuccessCounts() {
        return impSuccessCounts;
    }

    public void setImpSuccessCounts(Integer impSuccessCounts) {
        this.impSuccessCounts = impSuccessCounts;
    }

    public Integer getImpFailureCounts() {
        return impFailureCounts;
    }

    public void setImpFailureCounts(Integer impFailureCounts) {
        this.impFailureCounts = impFailureCounts;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getOperatTime() {
        return operatTime;
    }

    public void setOperatTime(Long operatTime) {
        this.operatTime = operatTime;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", impCounts=").append(impCounts);
        sb.append(", impSuccessCounts=").append(impSuccessCounts);
        sb.append(", impFailureCounts=").append(impFailureCounts);
        sb.append(", operator=").append(operator);
        sb.append(", createTime=").append(createTime);
        sb.append(", operatTime=").append(operatTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}