package com.panda.merge.model;

import com.panda.merge.dto.advertise.AbstructAdvertiseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 用户热键设置实体类
 *
 * @author warren
 * @since 2024/03/17 18:10:10
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserKeyboardSet extends AbstructAdvertiseDto {
    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "赛种(球种)ID")
    private Long sportId;

    @ApiModelProperty(value = "操作人")
    private String userId;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "键盘设置信息")
    private String keyboardInfo;

    @ApiModelProperty(value = "创建人")
    private String createUser;

    @ApiModelProperty(value = "修改人")
    private String modifyUser;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;
}
