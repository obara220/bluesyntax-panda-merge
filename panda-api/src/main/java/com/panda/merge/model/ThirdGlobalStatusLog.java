package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ThirdGlobalStatusLog implements Serializable {
    private Long id;

    @ApiModelProperty(value = "数据源编码")
    private String dataSourceCode;

    @ApiModelProperty(value = "服务状态：UP,DOWN，UP：服务正常DOWN：服务异常")
    private String status;

    @ApiModelProperty(value = "数据源事件产生时间")
    private Long sourceTimesTamp;

    @ApiModelProperty(value = "数据接入模块发送消息时间")
    private Long sendTimeStamp;

    @ApiModelProperty(value = "融合下发时间")
    private Long sendTimeStampRonghe;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getSourceTimesTamp() {
        return sourceTimesTamp;
    }

    public void setSourceTimesTamp(Long sourceTimesTamp) {
        this.sourceTimesTamp = sourceTimesTamp;
    }

    public Long getSendTimeStamp() {
        return sendTimeStamp;
    }

    public void setSendTimeStamp(Long sendTimeStamp) {
        this.sendTimeStamp = sendTimeStamp;
    }

    public Long getSendTimeStampRonghe() {
        return sendTimeStampRonghe;
    }

    public void setSendTimeStampRonghe(Long sendTimeStampRonghe) {
        this.sendTimeStampRonghe = sendTimeStampRonghe;
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
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", status=").append(status);
        sb.append(", sourceTimesTamp=").append(sourceTimesTamp);
        sb.append(", sendTimeStamp=").append(sendTimeStamp);
        sb.append(", sendTimeStampRonghe=").append(sendTimeStampRonghe);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}