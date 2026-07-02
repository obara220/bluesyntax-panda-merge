package com.panda.merge.dto.request;

import lombok.Data;

import org.yeauty.pojo.Session;
import java.io.Serializable;

@Data
public class SettleMatchSubCacheVo implements Serializable {
    private Long standardMatchId;
    private String eventCode;
    private Integer lostTimes;
    private Long createTime;
    private String sessionId;
    private Session session;
}
