package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

/**操盘配置-事件审核
 * @author :  myname
 * @Project Name :  data-realtime
 * @Package Name :  com.panda.sport.data.realtime.api.dto
 * @Description :  TODO
 * @Date: 2020-07-09 15:38
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class ConfigTemplateEventDTO implements Serializable {

    private static final long serialVersionUID = 8699969998094354205L;
    /**
     * 事件code
     */
    private String eventCode;

    /**
     * 事件内容
     */
    private String eventName;

    /**
     * 自动审核时间，单位为秒
     */
    private Long eventAuditTime;

}

