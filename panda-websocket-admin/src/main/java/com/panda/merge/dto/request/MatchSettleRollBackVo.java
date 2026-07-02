package com.panda.merge.dto.request;

import lombok.Data;

import org.yeauty.pojo.Session;
import java.io.Serializable;

@Data
public class MatchSettleRollBackVo implements Serializable {
    private Integer lostTimes;
    private Long createTime;
    private String sessionId;
    private Session session;
}
