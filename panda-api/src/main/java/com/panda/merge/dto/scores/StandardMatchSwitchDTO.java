package com.panda.merge.dto.scores;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 标准比分中心页面
 */
@Slf4j
@Data
public class StandardMatchSwitchDTO implements Serializable {
    private Long sportId;
    private Long matchId;
//    private Long periodId;
    //开关下标
    private int index;
    //开关状态0关 1开
    private int status;

    private String userId;
    private String userName;
    private String ipAddress;


}
