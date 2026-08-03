package com.panda.merge.dto.advertise.v2;

import com.panda.merge.dto.advertise.AbstructAdvertiseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 保存热键请求 DTO，适用于所有球种的报球板热键存储
 */
@Data
public class HotkeysSaveDto extends AbstructAdvertiseDto {

    @ApiModelProperty(value = "赛种(球种)ID")
    private Long sportId;

    @ApiModelProperty(value = "操作人ID")
    private String userId;

    @ApiModelProperty(value = "用户名")
    private String userName;

    /**
     * 热键配置 JSON，各球种自定义结构，建议包含 version 字段以支持未来 schema 演进。
     * 示例：{"version":1,"sportId":7,"keys":[{"keyCode":"F1","eventCode":"score_red","label":"红球"}]}
     */
    @ApiModelProperty(value = "键盘热键设置 JSON")
    private String keyboardInfo;
}
