package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchSettleGrayWeight implements Serializable {
    private Long id;

    @ApiModelProperty(value = "运动ID")
    private Long sportId;

    @ApiModelProperty(value = "赛事id")
    private Long standardMatchId;

    @ApiModelProperty(value = "灰色类型编码:15进球min15Goal5进球min5Goal15角球min15Corner")
    private String grayCode;

    @ApiModelProperty(value = "灰色区间分钟数5~90")
    private Integer grayAreaMin;

    @ApiModelProperty(value = "灰色区间设置模版id")
    private String dataSourceCode;

    @ApiModelProperty(value = "灰色区间状态0待确认1已确认")
    private Integer grayStatus;

    @ApiModelProperty(value = "更新时间")
    private Long modifyTime;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

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

    public Long getStandardMatchId() {
        return standardMatchId;
    }

    public void setStandardMatchId(Long standardMatchId) {
        this.standardMatchId = standardMatchId;
    }

    public String getGrayCode() {
        return grayCode;
    }

    public void setGrayCode(String grayCode) {
        this.grayCode = grayCode;
    }

    public Integer getGrayAreaMin() {
        return grayAreaMin;
    }

    public void setGrayAreaMin(Integer grayAreaMin) {
        this.grayAreaMin = grayAreaMin;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public Integer getGrayStatus() {
        return grayStatus;
    }

    public void setGrayStatus(Integer grayStatus) {
        this.grayStatus = grayStatus;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", sportId=").append(sportId);
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", grayCode=").append(grayCode);
        sb.append(", grayAreaMin=").append(grayAreaMin);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", grayStatus=").append(grayStatus);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}