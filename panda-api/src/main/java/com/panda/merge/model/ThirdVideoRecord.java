package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ThirdVideoRecord implements Serializable {
    private Long id;

    @ApiModelProperty(value = "数据源编码TS")
    private String dataSourceCode;

    @ApiModelProperty(value = "三方赛事原id")
    private String thirdMatchSourceId;

    @ApiModelProperty(value = "多媒体流id")
    private String streamId;

    @ApiModelProperty(value = "流媒体状态0:不可用1:可用，暂未播放2：可用，播放中")
    private Short streamStatus;

    @ApiModelProperty(value = "小编编辑状态OPEN:开CLOSE:关闭")
    private String sellStatus;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public String getThirdMatchSourceId() {
        return thirdMatchSourceId;
    }

    public void setThirdMatchSourceId(String thirdMatchSourceId) {
        this.thirdMatchSourceId = thirdMatchSourceId;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public Short getStreamStatus() {
        return streamStatus;
    }

    public void setStreamStatus(Short streamStatus) {
        this.streamStatus = streamStatus;
    }

    public String getSellStatus() {
        return sellStatus;
    }

    public void setSellStatus(String sellStatus) {
        this.sellStatus = sellStatus;
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
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", thirdMatchSourceId=").append(thirdMatchSourceId);
        sb.append(", streamId=").append(streamId);
        sb.append(", streamStatus=").append(streamStatus);
        sb.append(", sellStatus=").append(sellStatus);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}