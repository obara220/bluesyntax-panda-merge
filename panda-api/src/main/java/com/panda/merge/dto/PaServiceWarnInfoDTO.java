package com.panda.merge.dto;

import com.panda.merge.common.utils.TimeUtils;
import lombok.Data;

import java.io.Serializable;

/**
 * PA统一异常监听报警
 * @Author tell
 * @since  2021年10月2日12:18:24
 */
@Data
public class PaServiceWarnInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 线路ID **/
    private String linkId;

    /** 数据来源**/
    private String dataSourceCode;

    /** 1:非常严重，2:严重 ，3:普通，99:恢复 ，可以参考 com.panda.merge.common.enums.WarnLevelEnum 枚举**/
    private Integer level;

    /** 描述 **/
    private String message;

    /** 触发时间 **/
    private Long modifyTime = TimeUtils.millsSecondsEast8ZoneGmt();

}
