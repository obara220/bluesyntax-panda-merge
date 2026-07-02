package com.panda.merge.dto.request;

import lombok.Data;

@Data
public class MatchEventRequestVo {
    Long matchId;
    boolean isStandard;
    String dataSourceCode;
}
