package com.panda.merge.component;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DruidMonitorSql {

    private String id;
    private String traceId;
    private String appName;
    private String appNode;
    private String appIp;
    private Long createdTime;
    private String errorType;
    private String dbName;

    @JsonProperty("ExecuteAndResultSetHoldTime")
    private Integer executeAndResultSetHoldTime;
    @JsonProperty("EffectedRowCountHistogram")
    private List<Integer> effectedRowCountHistogram;
    @JsonProperty("Histogram")
    private List<Integer> histogram;
    @JsonProperty("InputStreamOpenCount")
    private Integer inputStreamOpenCount;
    @JsonProperty("BatchSizeTotal")
    private Integer batchSizeTotal;
    @JsonProperty("FetchRowCountMax")
    private Integer fetchRowCountMax;
    @JsonProperty("ErrorCount")
    private Integer errorCount;
    @JsonProperty("BatchSizeMax")
    private Integer batchSizeMax;
    @JsonProperty("ReaderOpenCount")
    private Integer readerOpenCount;
    @JsonProperty("EffectedRowCountMax")
    private Integer effectedRowCountMax;
    @JsonProperty("InTransactionCount")
    private Integer inTransactionCount;
    @JsonProperty("ResultSetHoldTime")
    private Integer resultSetHoldTime;
    @JsonProperty("TotalTime")
    private Integer totalTime;

    private Integer sqlId;
    @JsonProperty("ConcurrentMax")
    private Integer concurrentMax;
    @JsonProperty("RunningCount")
    private Integer runningCount;
    @JsonProperty("FetchRowCount")
    private Integer fetchRowCount;
    @JsonProperty("MaxTimespanOccurTime")
    private Long maxTimespanOccurTime;
    @JsonProperty("ReadBytesLength")
    private Integer readBytesLength;
    @JsonProperty("DbType")
    private String dbType;
    @JsonProperty("SQL")
    private String sql;
    @JsonProperty("HASH")
    private String hash;
    @JsonProperty("MaxTimespan")
    private Integer maxTimespan;
    @JsonProperty("BlobOpenCount")
    private Integer blobOpenCount;
    @JsonProperty("ExecuteCount")
    private Integer executeCount;
    @JsonProperty("EffectedRowCount")
    private Integer effectedRowCount;
    @JsonProperty("ReadStringLength")
    private Integer readStringLength;
    @JsonProperty("ExecuteAndResultHoldTimeHistogram")
    private List<Integer> executeAndResultHoldTimeHistogram;
    @JsonProperty("FetchRowCountHistogram")
    private List<Integer> fetchRowCountHistogram;
    @JsonProperty("ClobOpenCount")
    private Integer clobOpenCount;
    @JsonProperty("LastTime")
    private Long lastTime;
    @JsonProperty("PID")
    private Integer pId;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("LastErrorMessage")
    private String lastErrorMessage;
    @JsonProperty("LastErrorTime")
    private Long lastErrorTime;
    @JsonProperty("LastErrorClass")
    private String lastErrorClass;
    @JsonProperty("LastErrorStackTrace")
    private String lastErrorStackTrace;
    @JsonProperty("LastSlowParameters")
    private String lastSlowParameters;
}