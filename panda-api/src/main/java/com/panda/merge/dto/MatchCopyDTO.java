package com.panda.merge.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

/**
 * 拷贝赛事相关信息（拷贝赛事事件，统计信息）
 * @author   tell
 * @since    2022年1月30日13:47:52
 */
@Data
public class MatchCopyDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "原始标准赛事ID不能为空！")
    private Long oldStandardMatchId;

    @NotEmpty(message = "拷贝标准赛事ID不能为空！")
    private Long newStandardMatchId;

}
