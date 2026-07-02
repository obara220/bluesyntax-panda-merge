package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class DataSource implements Serializable {
    @ApiModelProperty(value = "数据表id,自增")
    private Long id;

    @ApiModelProperty(value = "该数据源的编码.比如SportRadar的编码是SR")
    private String code;

    @ApiModelProperty(value = "数据源全称.比如SportRadar")
    private String fullName;

    @ApiModelProperty(value = "数据源简称.比如SR,球探")
    private String shortName;

    @ApiModelProperty(value = "数据的优先级.值越大,重要程度越高.")
    private Integer priority;

    @ApiModelProperty(value = "是否是商业来源的数据.1:商业来源;0:非商业")
    private Integer commerce;

    @ApiModelProperty(value = "是否为标准数据源.1:是;0:否")
    private Integer standard;

    @ApiModelProperty(value = "数据源类型.0:竞品数据源;1:比分网")
    private Integer type;

    private String remark;

    private Long createTime;

    private Long modifyTime;

    @ApiModelProperty(value = "是否支持事件(0:否,1:是)")
    private Integer eventSupport;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getCommerce() {
        return commerce;
    }

    public void setCommerce(Integer commerce) {
        this.commerce = commerce;
    }

    public Integer getStandard() {
        return standard;
    }

    public void setStandard(Integer standard) {
        this.standard = standard;
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

    public Integer getEventSupport() {
        return eventSupport;
    }

    public void setEventSupport(Integer eventSupport) {
        this.eventSupport = eventSupport;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", code=").append(code);
        sb.append(", fullName=").append(fullName);
        sb.append(", shortName=").append(shortName);
        sb.append(", priority=").append(priority);
        sb.append(", commerce=").append(commerce);
        sb.append(", standard=").append(standard);
        sb.append(", type=").append(type);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", eventSupport=").append(eventSupport);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}