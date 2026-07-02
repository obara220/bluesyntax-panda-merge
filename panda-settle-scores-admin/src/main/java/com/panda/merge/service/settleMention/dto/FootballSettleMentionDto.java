package com.panda.merge.service.settleMention.dto;

import com.panda.merge.config.SettleMentionProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: 足球结算事件提示信息
 * @author: Henry Wang
 * @create: 2024-08-27 16:09
 **/

@Data
@Slf4j
public class FootballSettleMentionDto extends AbstractSettleMentionDto<FootballMentionStatus> {
    @SettleMentionProperty(eventCode = {"deleteStatus"})
    private FootballMentionStatus deleteStatus;              // 0:未删除   1: 已删除
    @SettleMentionProperty(eventCode = {"dataMismatchStatus"})
    private FootballMentionStatus dataMismatchStatus;        // 0:匹配     1: 不匹配
}
