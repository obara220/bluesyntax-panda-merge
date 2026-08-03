package com.panda.merge.odds.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * FlowControlNotification
 *
 * @description:
 * @date: 7/16/2025
 **/

@Data
public class FlowControlNotificationDto implements Serializable {

    @NotNull(message = "开关不能为空")
    private Integer FlowControlNotificationStatus; // 0 开 1 关 ,

    private int FlowControlNotificationTotal; // 总分批次，

    private int FlowControlNotificationCurrent; // 当前批次，
    @NotNull(message = "告警阶段不能为空")
    private Integer FlowControlNotificationStage; //:0仅告警 1 50%第一阶段 2 70%第二阶段 3 90%第三阶段,

    private List<Long> FlowControlNotificationMatchInIds; //需要下发数据赛事id集合

    private List<Long> FlowControlNotificationMatchNotInIds; // 不需要下发赛事赛事id集合,

}
