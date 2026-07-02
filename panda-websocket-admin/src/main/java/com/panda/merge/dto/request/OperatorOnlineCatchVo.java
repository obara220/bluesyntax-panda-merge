package com.panda.merge.dto.request;

import lombok.Data;
import org.yeauty.pojo.Session;

import java.io.Serializable;
import java.util.List;

@Data
public class OperatorOnlineCatchVo implements Serializable {
    private Integer lostTimes;
    private Long createTime;
    private String sessionId;
    private Session session;
    private List userIds;
}
