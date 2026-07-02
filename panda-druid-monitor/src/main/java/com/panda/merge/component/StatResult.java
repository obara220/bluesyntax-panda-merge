package com.panda.merge.component;

import com.alibaba.fastjson.JSON;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StatResult {
    private String applicationName;
    private String instanceName;
    private long collectTime;
    private List<AlertMessage> alertMessages = new LinkedList();
    private List<Map<String, Object>> dataSourceStats = new LinkedList();

    public StatResult(String applicationName, String instanceName, long collectTime) {
        this.applicationName = applicationName;
        this.instanceName = instanceName;
        this.collectTime = collectTime;
    }

    public String toString() {
        return JSON.toJSONString(this);
    }

    public StatResult addAlertMessage(AlertMessage alertMessage) {
        this.alertMessages.add(alertMessage);
        return this;
    }

    public boolean hasAlert() {
        return !this.alertMessages.isEmpty();
    }

    public int getAlertCount() {
        return this.alertMessages.size();
    }

    public StatResult addDataSourceStat(Map<String, Object> dataSourceStat) {
        this.dataSourceStats.add(dataSourceStat);
        return this;
    }

    public String getApplicationName() {
        return this.applicationName;
    }

    public String getInstanceName() {
        return this.instanceName;
    }

    public long getCollectTime() {
        return this.collectTime;
    }

    public List<AlertMessage> getAlertMessages() {
        return this.alertMessages;
    }

    public List<Map<String, Object>> getDataSourceStats() {
        return this.dataSourceStats;
    }

    public StatResult setApplicationName(final String applicationName) {
        this.applicationName = applicationName;
        return this;
    }

    public StatResult setInstanceName(final String instanceName) {
        this.instanceName = instanceName;
        return this;
    }

    public StatResult setCollectTime(final long collectTime) {
        this.collectTime = collectTime;
        return this;
    }

    public StatResult setAlertMessages(final List<AlertMessage> alertMessages) {
        this.alertMessages = alertMessages;
        return this;
    }

    public StatResult setDataSourceStats(final List<Map<String, Object>> dataSourceStats) {
        this.dataSourceStats = dataSourceStats;
        return this;
    }

    public StatResult() {
    }
}
