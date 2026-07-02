package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class SystemTypeDict implements Serializable {
    private Long id;

    @ApiModelProperty(value = "运动种类id")
    private Long sportId;

    @ApiModelProperty(value = "字典关键字")
    private String code;

    @ApiModelProperty(value = "当前数据所表示的含义.")
    private String value;

    @ApiModelProperty(value = "是否激活.1:激活;0:没有激活.")
    private Integer active;

    @ApiModelProperty(value = "描述信息.")
    private String description;

    @ApiModelProperty(value = "备注.remark")
    private String remark;

    @ApiModelProperty(value = "创建时间.create_time")
    private Long createTime;

    @ApiModelProperty(value = "更新时间.modify_time")
    private Long modifyTime;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", sportId=").append(sportId);
        sb.append(", code=").append(code);
        sb.append(", value=").append(value);
        sb.append(", active=").append(active);
        sb.append(", description=").append(description);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}