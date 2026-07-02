package com.panda.merge.dto;

import lombok.Data;

import java.util.List;

@Data
public class RateLimiterDTO {
    /**  0 开 1 关 */
    public Integer flowControlNotificationStatus;
    /** 总分批次 */
    public Integer flowControlNotificationTotal;
    /** 当前批次 */
    public Integer flowControlNotificationCurrent;
    public Integer flowControlNotificationStage;
    /** 不需要下发赛事赛事id集合 */
    public List<Long> flowControlNotificationMatchNotInIds;
    public List<RateLimiterThirdMatchDTO> flowControlNotificationMatchNotIns;
    public String flowControlNotificationUnique;
    /** link 改变需要清空原缓存,重新添加了*/
    public boolean changeLink;

}
