package com.panda.merge.service.settleMention.dto;

import com.panda.merge.config.SettleMentionProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: 篮球结算事件提示信息
 * @author: Henry Wang
 * @create: 2024-10-15 16:09
 **/

@Data
@Slf4j
public class BasketballSettleMentionDto extends AbstractSettleMentionDto<BasketballMentionStatus> {
    @SettleMentionProperty(eventCode = {"dataMismatchStatus"})
    private BasketballMentionStatus dataMismatchStatus;        // 0:匹配     1: 不匹配

    @SettleMentionProperty(eventCode = {"grayAreaStatus"})
    private BasketballMentionStatus grayAreaStatus;        // 0:匹配     1: 不匹配
}
