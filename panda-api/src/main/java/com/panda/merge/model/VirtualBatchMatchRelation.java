package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class VirtualBatchMatchRelation implements Serializable {
    private Long id;

    @ApiModelProperty(value = "虚拟赛事批次ID")
    private Long virtualBatchNoId;

    @ApiModelProperty(value = "三方赛事ID")
    private Long thirdMatchId;

    @ApiModelProperty(value = "标准赛事ID")
    private Long standardMatchId;

    private Long sportId;

    private Long createTime;

    private Long modifyTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVirtualBatchNoId() {
        return virtualBatchNoId;
    }

    public void setVirtualBatchNoId(Long virtualBatchNoId) {
        this.virtualBatchNoId = virtualBatchNoId;
    }

    public Long getThirdMatchId() {
        return thirdMatchId;
    }

    public void setThirdMatchId(Long thirdMatchId) {
        this.thirdMatchId = thirdMatchId;
    }

    public Long getStandardMatchId() {
        return standardMatchId;
    }

    public void setStandardMatchId(Long standardMatchId) {
        this.standardMatchId = standardMatchId;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
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
        sb.append(", virtualBatchNoId=").append(virtualBatchNoId);
        sb.append(", thirdMatchId=").append(thirdMatchId);
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", sportId=").append(sportId);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}