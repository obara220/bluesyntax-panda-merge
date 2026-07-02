package com.panda.merge.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * mango预警请求信息 <br>
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2022/6/12 <br>
 */
@ApiModel(value = "用户请求的实体")
@Data
public class MangoRequest<T> implements Serializable {

    @ApiModelProperty(name = "data",value = "数据实体",required = true)
    @Valid
    @NotNull(message = "data不能为null")
    private T data;

    @ApiModelProperty(name = "dataSourceCode",value = "数据来源")
    private String dataSourceCode;

}
