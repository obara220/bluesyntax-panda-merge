package com.panda.merge.component;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlertMessage {
    private Integer type;
    private String name;
    private String dataSource;
    private Integer sqlId;
    private String sql;
    private String message;
    private Long timestamp;
    private Integer totalTime;
    private String lastErrorStackTrace;
}
