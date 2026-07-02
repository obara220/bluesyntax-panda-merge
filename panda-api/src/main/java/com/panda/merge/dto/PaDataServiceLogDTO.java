package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * PA数据服务日志
 * @Author tell
 * @since  2021年3月12日12:24:58
 */
@Data
public class PaDataServiceLogDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 线路ID **/
    private String linkId;
    /** 当前日期(格式：yyyyMMdd) **/
    private Long date;
    /** 服务类型 : SR，BG，odds(赔率服务)，realtime(实时服务)，nonrealtime(非实时服务) **/
    private String serviceType;
    /** 接口编码（例：THIRD_TOURNAMENT_API） **/
    private String apiCode;
    /** 接口名称（例：联赛信息） */
    private String apiName;
    /** 耗时（毫秒） **/
    private Long consumeTime;
    /** 错误编码（200：处理成功，非200：处理失败...） **/
    private Integer errorCode;
    /** 描述 **/
    private String message;

}
