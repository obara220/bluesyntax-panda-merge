package com.panda.merge.component;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DruidMonitorInfo {
    private String traceId;
    private String appName;
    private String appNode;
    private String ip;
    private List<DbMonitorInfo> dbMonitorInfoList;

    @Data
    @Builder
    public static class DbMonitorInfo{
        @JsonProperty("Identity")
        private Integer identity;
        @JsonProperty("Name")
        private String name;
        @JsonProperty("DbType")
        private String dbType;
        @JsonProperty("ActiveCount")
        private Integer activeCount;
        @JsonProperty("ActivePeak")
        private Integer activePeak;
        @JsonProperty("ActivePeakTime")
        private Long activePeakTime;
        @JsonProperty("InitialSize")
        private Integer initialSize;
        @JsonProperty("MinIdle")
        private Integer minIdle;
        @JsonProperty("MaxActive")
        private Integer maxActive;

        @JsonProperty("PoolingCount")
        private Integer poolingCount;
        private Integer idleConnections;
        @JsonProperty("PhysicalCloseCount")
        private Integer physicalCloseCount;
        @JsonProperty("SQL")
        private List<SqlError> sqlErrorList;
        private String dbName;
        @Builder
        @Data
        public static class SqlError {
            private String errorType;
            private String errorMessage;
            private Integer sqlId;
            @JsonProperty("SQL")
            private String sql;
            @JsonProperty("TotalTime")
            private Integer totalTime;
            @JsonProperty("LastErrorTime")
            private Long lastErrorTime;
            @JsonProperty("LastErrorStackTrace")
            private String lastErrorStackTrace;
        }
    }


}
