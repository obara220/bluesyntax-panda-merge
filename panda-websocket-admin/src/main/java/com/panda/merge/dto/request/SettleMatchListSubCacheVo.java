package com.panda.merge.dto.request;

import lombok.Data;

import org.yeauty.pojo.Session;
import java.io.Serializable;
import java.util.List;

@Data
public class SettleMatchListSubCacheVo implements Serializable {
    private List<Long> standardMatchIdList;
    private List<String> eventCodeList;
    private Integer lostTimes;
    private Long createTime;
    private String sessionId;
    private Session session;
}
