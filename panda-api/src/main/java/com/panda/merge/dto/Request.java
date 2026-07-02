package com.panda.merge.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 请求信息 <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/13 <br>
 */
@ApiModel(value = "用户请求的实体")
@Data
public class Request<T> implements Serializable {

    @ApiModelProperty(name = "linkId", value = "链路id,最大长度建议不要超过50", required = true)
    @NotNull(message = "linkId不能为空")
    private String linkId;

    @ApiModelProperty(name = "data", value = "数据实体", required = true)
    @Valid
    @NotNull(message = "data不能为null")
    private T data;

    @ApiModelProperty(name = "dataSourceTime", value = "请求时间戳")
    private Long dataSourceTime = System.currentTimeMillis();

    @ApiModelProperty(name = "dataSourceCode", value = "数据来源")
    private String dataSourceCode;

    @ApiModelProperty(name = "dataType", value = "数据类型,可以是mq的topic")
    private String dataType;

    @ApiModelProperty(name = "tag", value = " mq消息界面上显示的tag")
    private String tag;

    @ApiModelProperty(name = "operaterId", value = "操作人ID")
    private Long operaterId;

    /**
     * 需求 3531，切换事件源标识历史事件，避免前端赛事进行时间在切换过程中乱
     * 是否补发事件(切换事件源触发的补发事件) true:是，其它（false或者null）:否
     */
    private Boolean isReissue = false;

    /**
     * 需求 3963，赔率、事件、统计、比分、投注流程MQ发送及消费数据支持动态切换MQ集群地址
     * 是否备用MQ（true:是，false:否）
     */
    private Boolean spareMq = false;

    /**
     * 4269 【数据报表】
     */
    private int oddsSource = 0;
    /**
     * 由于风控模块通用globalId，这里增加个setter用于风控系统未传linkId是，从globalId属性中取值赋给linkId。 add_by Riben 20200923
     *
     * @param globalId
     */
    public void setGlobalId(String globalId) {
        this.linkId = globalId;
    }

    public Request() {
        super();
    }

    public Request(T data, String linkId) {
        this.data = data;
        this.linkId = linkId;
    }

    public Request(T data, String linkId, Boolean isReissue, String dataSourceCode) {
        this.data = data;
        this.linkId = linkId;
        this.isReissue = isReissue;
        this.dataSourceCode = dataSourceCode;
    }

    public Request(T data, String linkId, String dataType, String tag, String dataSourceCode) {
        this.data = data;
        this.linkId = linkId;
        this.dataType = dataType;
        this.tag = tag;
        this.dataSourceCode = dataSourceCode;
    }

}
