package com.panda.merge.dto.request;

import lombok.Data;

import java.io.Serializable;
import org.yeauty.pojo.Session;
@Data
public class PdSubCacheVo implements Serializable {
    private Long thirdMatchId;
    private Integer lostTimes;
    private Long createTime;
    private String sessionId;
    private Session session;
}
