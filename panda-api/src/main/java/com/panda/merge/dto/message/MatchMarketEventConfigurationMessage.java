package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;

/**
 * @author :  Riben
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.dto
 * @Description :  TODO
 * @Date: 2020-09-17 13:09
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class MatchMarketEventConfigurationMessage implements Serializable {
    /**
     * 事件code
     */
    private String eventCode;

    /**
     * 事件审核时间
     */
    private Integer eventHandleTime;

    /**
     * 结算审核时间
     */
    private Integer settleHandleTime;
}
