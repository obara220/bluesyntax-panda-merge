package com.panda.merge.dto.scores;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 板球轮数推送
 */
@Slf4j
@Data
public class CricketOverPushDTO implements Serializable{
    private Long sportId;
    private Long standardMatchId;
    private String over;

}
