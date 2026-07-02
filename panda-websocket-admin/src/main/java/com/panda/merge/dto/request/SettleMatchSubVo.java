package com.panda.merge.dto.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class SettleMatchSubVo implements Serializable {
    private Long standardMatchId;
    private String eventCode;
}
