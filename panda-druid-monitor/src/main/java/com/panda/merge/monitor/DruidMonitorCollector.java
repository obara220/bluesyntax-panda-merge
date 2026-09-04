package com.panda.merge.monitor;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.DruidStatManagerFacade;
import com.alibaba.druid.stat.JdbcSqlStat;
import com.alibaba.druid.util.DruidDataSourceUtils;
import com.alibaba.fastjson.JSON;
import com.panda.merge.component.AlertMessage;
import com.panda.merge.component.AlertType;
import com.panda.merge.component.DruidMonitorInfo;
import com.panda.merge.component.StatResult;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

@RefreshScope
@Component
public class DruidMonitorCollector implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger log = LoggerFactory.getLogger(DruidMonitorCollector.class);
    @Resource
    ParameterAutoConfig parameterAutoConfig;
    private RocketMQTemplate rocketMQTemplate;
    private ScheduledExecutorService taskExecutor;
    private long lastExecuteTime;
    private String instanceName;
    private Map<DruidDataSource, Long> slowSqlMillisMap = new HashMap();
    private AtomicBoolean running = new AtomicBoolean(false);

    public DruidMonitorCollector(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    @PostConstruct
    public void postConstruct() {
        this.instanceName = ManagementFactory.getRuntimeMXBean().getName();
        DruidMonitorCollector.log.info(">>>>> ({} - {}) start druid monitor collect - postConstruct >>>>>", parameterAutoConfig.getApplicationName(), DruidMonitorCollector.this.instanceName);
        this.taskExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(Thread.currentThread().getThreadGroup(), r, "Druid-Monitor-Collector");
                t.setDaemon(true);
                return t;
            }
        });
    }

    @PreDestroy
    public void preDestroy() {
        this.taskExecutor.shutdown();

        try {
            this.taskExecutor.awaitTermination(60L, TimeUnit.SECONDS);
        } catch (InterruptedException var2) {
            log.error("task executor await termination exception -> {}", var2.getMessage(), var2);
        }

    }

    public void onApplicationEvent(ApplicationReadyEvent event) {
        this.taskExecutor.scheduleWithFixedDelay(new StatTask(), 30L, 30L, TimeUnit.SECONDS);
    }
    private class StatTask implements Runnable {
        private StatTask() {
        }

        public void run() {
            if (parameterAutoConfig.getEnabled()) {
                if (DruidMonitorCollector.this.lastExecuteTime + parameterAutoConfig.getCollectInterval() * 1000L <= System.currentTimeMillis()) {
                    DruidMonitorCollector.log.info(">>>>> ({} - {}) start druid monitor collect >>>>>", parameterAutoConfig.getApplicationName(), DruidMonitorCollector.this.instanceName);
                    int count = 0;

                    try {
                        count = this.collect();
                    } catch (Exception var6) {
                        DruidMonitorCollector.log.error("stat task execute exception -> {}", var6.getMessage(), var6);
                    } finally {
                        DruidMonitorCollector.log.info("<<<<< ({} - {}) end druid monitor collect (alertCount: {}) <<<<<", new Object[]{parameterAutoConfig.getApplicationName(), DruidMonitorCollector.this.instanceName, count});
                    }

                    DruidMonitorCollector.this.lastExecuteTime = System.currentTimeMillis();
                }
            }
        }

        private int collect() {
            if (!DruidMonitorCollector.this.running.compareAndSet(false, true)) {
                return -1;
            } else {
                int var3;
                try {
                    StatResult rs = this.doCollect();
                    int count = rs.getAlertCount();
                    if (count > 0) {
                        this.reportError(rs);
                    }

                    var3 = count;
                } finally {
                    DruidMonitorCollector.this.running.compareAndSet(true, false);
                }

                return var3;
            }
        }

        private StatResult doCollect() {
            StatResult rs = new StatResult(parameterAutoConfig.getApplicationName(), DruidMonitorCollector.this.instanceName, System.currentTimeMillis());
            DruidDataSourceStatManager.getDruidDataSourceInstances().forEach((dataSource) -> {
                Map<String, Object> stat = DruidDataSourceUtils.getStatData(dataSource);
                List<Map<String, Object>> sqlStats = DruidStatManagerFacade.getInstance().getSqlStatDataList(dataSource);
                stat.put("sqlStats", sqlStats);
                this.checkAlert(rs, stat, dataSource);
            });
            return rs;
        }

        private void checkAlert(StatResult rs, Map<String, Object> stat, DruidDataSource dataSource) {
            boolean hasError = false;
            hasError |= this.checkDataSourceAlert(rs, dataSource, stat);
            List<Map<String, Object>> sqlStats = (List)stat.get("sqlStats");
            if (sqlStats != null) {
                hasError |= this.checkSqlAlert(rs, dataSource, sqlStats);
            }

            if (hasError) {
                rs.addDataSourceStat(stat);
            }

        }

        private boolean checkDataSourceAlert(StatResult rs, DruidDataSource dataSource, Map<String, Object> stat) {
            boolean hasError = false;
            Integer activeCount = (Integer)stat.get("ActiveCount");
            Integer maxActive = (Integer)stat.get("MaxActive");
            if (activeCount != null && maxActive != null && maxActive > 0) {
                double ratio = (double)activeCount / (double)maxActive;
                if (ratio >= parameterAutoConfig.getActiveConnectionAlertRatio()) {
                    hasError = true;
                    AlertMessage alertMessage = AlertMessage.builder()
                            .type(AlertType.TOO_MANY_ACTIVE_CONNECTIONS.getType())
                            .name(AlertType.TOO_MANY_ACTIVE_CONNECTIONS.getName())
                            .dataSource(dataSource.getName())
                            .timestamp(System.currentTimeMillis())
                            .message(String.format("Ratio: %d%%, ActiveCount: %d, MaxActive: %d", Math.round(ratio * 100.0), activeCount, maxActive))
                            .build();
                    rs.addAlertMessage(alertMessage);
                }
            }

            return hasError;
        }

        private boolean checkSqlAlert(StatResult rs, DruidDataSource dataSource, List<Map<String, Object>> sqlStats) {
            boolean hasError = false;
            Iterator<Map<String, Object>> it = sqlStats.iterator();

            while(it.hasNext()) {
                Map<String, Object> sqlStat = (Map)it.next();
                Number id = (Number)sqlStat.get("ID");
                Integer sqlId = id != null ? id.intValue() : null;
                String sql = (String)sqlStat.get("SQL");
                sql = StringUtils.truncate(sql, 2000);
                MutablePair<Boolean, Integer> flag = MutablePair.of(Boolean.FALSE, 0);
                this.checkSqlException(rs, dataSource, sqlStat, sqlId, sql, flag);
                this.checkSlowSql(rs, dataSource, sqlStat, sqlId, sql, flag);
                if ((Boolean)flag.left) {
                    hasError = true;
                    this.doReset((Integer)flag.right, dataSource, sqlId);
                } else {
                    it.remove();
                }
            }

            return hasError;
        }

        private void checkSqlException(StatResult rs, DruidDataSource dataSource, Map<String, Object> sqlStat, Integer sqlId, String sql, MutablePair<Boolean, Integer> flag) {
            String lastErrorClass = (String)sqlStat.get("LastErrorClass");
            if (!StringUtils.isBlank(lastErrorClass)) {
                Date lastErrorTime = (Date)sqlStat.get("LastErrorTime");
                String lastErrorMessage = (String)sqlStat.get("LastErrorMessage");
                lastErrorMessage = StringUtils.truncate(lastErrorMessage, 2000);
                flag.left = Boolean.TRUE;
                if (lastErrorTime == null) {
                    lastErrorTime = new Date();
                    flag.right = (Integer)flag.right | 1;
                } else {
                    flag.right = (Integer)flag.right | (this.checkReset(lastErrorTime) ? 1 : 0);
                }

                AlertType type = this.retriveSqlAlertType(lastErrorMessage, lastErrorClass);
                AlertMessage alertMessage = AlertMessage.builder()
                        .type(type.getType())
                        .name(type.getName())
                        .dataSource(dataSource.getName())
                        .timestamp(lastErrorTime.getTime())
                        .message(lastErrorMessage)
                        .sqlId(sqlId)
                        .sql(sql)
                        .totalTime(Integer.parseInt(String.valueOf(sqlStat.get("MaxTimespan"))))
                        .lastErrorStackTrace(String.valueOf(sqlStat.get("LastErrorStackTrace")))
                        .build();
                rs.addAlertMessage(alertMessage);
            }
        }

        private void checkSlowSql(StatResult rs, DruidDataSource dataSource, Map<String, Object> sqlStat, Integer sqlId, String sql, MutablePair<Boolean, Integer> flag) {
            Long maxTimespan = (Long)sqlStat.get("MaxTimespan");
            Long slowSqlMillis = this.getSlowSqlMillis(dataSource);
            if (slowSqlMillis != null && slowSqlMillis > 0L && maxTimespan != null && maxTimespan >= slowSqlMillis) {
                Date maxTimespanOccurTime = (Date)sqlStat.get("MaxTimespanOccurTime");
                flag.left = Boolean.TRUE;
                if (maxTimespanOccurTime == null) {
                    maxTimespanOccurTime = new Date();
                    flag.right = (Integer)flag.right | 2;
                } else {
                    flag.right = (Integer)flag.right | (this.checkReset(maxTimespanOccurTime) ? 2 : 0);
                }

                String errorMessage = "查询参数：" + sqlStat.get("LastSlowParameters");
                errorMessage = StringUtils.truncate(errorMessage, 2000);
                AlertMessage alertMessage = AlertMessage.builder()
                        .type(AlertType.SQL_SLOW_QUERY.getType())
                        .name(AlertType.SQL_SLOW_QUERY.getName())
                        .dataSource(dataSource.getName())
                        .timestamp(maxTimespanOccurTime.getTime())
                        .message(errorMessage)
                        .sqlId(sqlId)
                        .sql(sql)
                        .totalTime(Integer.parseInt(String.valueOf(sqlStat.get("MaxTimespan"))))
                        .lastErrorStackTrace(String.valueOf(sqlStat.get("LastErrorStackTrace")))
                        .build();
                rs.addAlertMessage(alertMessage);
            }
        }

        private Long getSlowSqlMillis(DruidDataSource dataSource) {
            if (!DruidMonitorCollector.this.slowSqlMillisMap.containsKey(dataSource)) {
                synchronized(DruidMonitorCollector.this.slowSqlMillisMap) {
                    if (!DruidMonitorCollector.this.slowSqlMillisMap.containsKey(dataSource)) {
                        Long slowSqlMillis = null;
                        List<Filter> filters = dataSource.getProxyFilters();
                        Iterator var5 = filters.iterator();

                        while(var5.hasNext()) {
                            Filter filter = (Filter)var5.next();
                            if (filter instanceof StatFilter) {
                                StatFilter statFilter = (StatFilter)filter;
                                slowSqlMillis = statFilter.getSlowSqlMillis();
                                break;
                            }
                        }

                        DruidMonitorCollector.this.slowSqlMillisMap.put(dataSource, slowSqlMillis);
                    }
                }
            }

            return (Long)DruidMonitorCollector.this.slowSqlMillisMap.get(dataSource);
        }

        private AlertType retriveSqlAlertType(String lastErrorMessage, String lastErrorClass) {
            String lowerLastErrorMessage = StringUtils.defaultString(lastErrorMessage).toLowerCase();
            if (!lastErrorClass.contains("CommunicationsException") && !lowerLastErrorMessage.contains("communications ")) {
                return !lastErrorClass.contains("LockWaitTimeoutException") && !lowerLastErrorMessage.contains("lock ") ? AlertType.SQL_EXCEPTION : AlertType.SQL_DEADLOCK;
            } else {
                return AlertType.CONNECTION_FAILED;
            }
        }

        private boolean checkReset(Date lastErrorTime) {
            long resetMillis = parameterAutoConfig.getErrorResetSeconds() * 1000L;
            if (resetMillis < 0L) {
                return false;
            } else if (resetMillis == 0L) {
                return true;
            } else if (lastErrorTime == null) {
                return true;
            } else {
                return System.currentTimeMillis() - lastErrorTime.getTime() > resetMillis;
            }
        }

        private void doReset(int resetFlag, DruidDataSource dataSource, Integer sqlId) {
            if (resetFlag != 0 && sqlId != null) {
                JdbcSqlStat jdbcSqlStat = (JdbcSqlStat)DruidDataSourceUtils.getSqlStat(dataSource, sqlId);
                if (jdbcSqlStat != null) {
                    if ((resetFlag & 1) != 0) {
                        DruidMonitorUtil.setFiledValue(jdbcSqlStat, "executeErrorLastTime", 0L);
                        DruidMonitorUtil.setFiledValue(jdbcSqlStat, "executeErrorLast", (Object)null);
                    }

                    if ((resetFlag & 2) != 0) {
                        DruidMonitorUtil.setFiledValue(jdbcSqlStat, "lastSlowParameters", (Object)null);
                        DruidMonitorUtil.setFiledValue(jdbcSqlStat, "executeNanoSpanMaxOccurTime", 0L);
                        DruidMonitorUtil.invokeFieldMethod(jdbcSqlStat, "executeSpanNanoMaxUpdater", "set", new Pair[]{Pair.of(Object.class, jdbcSqlStat), Pair.of(Long.TYPE, 0L)});
                    }

                }
            }
        }

        private void reportError(StatResult rs) {
            try {
                String key = RandomStringUtils.randomAlphanumeric(32);
                DruidMonitorInfo druidMonitorInfo = doDataBuild(rs, key);
                String str = JSON.toJSONString(druidMonitorInfo);
                String enc = DruidMonitorUtil.encodeMessage(str);
                String data = (new StringBuilder(enc.length() + 20)).append("{\"data\":\"").append(enc).append("\"}").toString();
                String tag = parameterAutoConfig.getApplicationName();

                String topic = String.format("%s:%s", parameterAutoConfig.getSqlStatTopic(), tag);
                MessageBuilder<?> builder = MessageBuilder.withPayload(data);
                builder.setHeader("KEYS", key);
                builder.setHeader("contentType", "text/plain");
                Message<?> message = builder.build();
                DruidMonitorCollector.this.rocketMQTemplate.send(topic, message);
            } catch (Exception var10) {
                DruidMonitorCollector.log.error("send error report exception: {}", var10.getMessage(), var10);
            }

        }

        private DruidMonitorInfo doDataBuild(StatResult rs, String key) {
            Map<String, List<AlertMessage>> groupDataSources = rs.getAlertMessages().stream().collect(Collectors.groupingBy(t->t.getDataSource()));
            List<DruidMonitorInfo.DbMonitorInfo> dbMonitorInfos = new ArrayList<>();
            for(Map<String, Object> ds : rs.getDataSourceStats()) {
                //处理错误日志
                String dataSourceName = String.valueOf(ds.getOrDefault("Name", "defaultName"));
                //连接库名
                String dbName = "";
                String urlString = String.valueOf(ds.getOrDefault("URL", ""));
                if(!StringUtils.isEmpty(urlString)) {
                    String preUrl = urlString.split("\\?")[0];
                    String[] arr = preUrl.split("\\/");
                    dbName = arr[arr.length-1];
                }
                List<AlertMessage> alertMessages = groupDataSources.getOrDefault(dataSourceName, new ArrayList<>());
                List<DruidMonitorInfo.DbMonitorInfo.SqlError> sqlErrors = new ArrayList<>();
                for (AlertMessage alertMessage : alertMessages) {
                    DruidMonitorInfo.DbMonitorInfo.SqlError sqlError = DruidMonitorInfo.DbMonitorInfo.SqlError.builder()
                            .errorType(AlertType.getNameByType(alertMessage.getType()))
                            .errorMessage(alertMessage.getMessage())
                            .sqlId(alertMessage.getSqlId())
                            .sql(alertMessage.getSql())
                            .totalTime(alertMessage.getTotalTime())
                            .lastErrorTime(alertMessage.getTimestamp())
                            .lastErrorStackTrace(alertMessage.getLastErrorStackTrace())
                            .build();
                    sqlErrors.add(sqlError);
                }

                // 处理db
                int activeConnections = Integer.parseInt(String.valueOf(ds.getOrDefault("ActiveCount", "-1")));  // 当前活跃连接数
                int poolingCountConnections = Integer.parseInt(String.valueOf(ds.getOrDefault("PoolingCount", "-1")));  // 当前连接池中的连接数
                DruidMonitorInfo.DbMonitorInfo dbMonitorInfo = DruidMonitorInfo.DbMonitorInfo.builder()
                        .identity(Integer.parseInt(String.valueOf(ds.getOrDefault("Identity", -1))))
                        .name(dataSourceName)
                        .dbType(String.valueOf(ds.getOrDefault("DbType", "defaultType")))
                        .activeCount(activeConnections)
                        .activePeakTime(ds.get("ActivePeakTime")==null?-1:((Date) ds.get("ActivePeakTime")).getTime())
                        .initialSize(Integer.parseInt(String.valueOf(ds.getOrDefault("InitialSize", -1))))
                        .minIdle(Integer.parseInt(String.valueOf(ds.getOrDefault("MinIdle", -1))))
                        .maxActive(Integer.parseInt(String.valueOf(ds.getOrDefault("MaxActive", -1))))
                        .poolingCount(poolingCountConnections)
                        .idleConnections(poolingCountConnections - activeConnections)
                        .physicalCloseCount(Integer.parseInt(String.valueOf(ds.getOrDefault("PhysicalCloseCount", "-1"))))
                        .sqlErrorList(sqlErrors)
                        .dbName(dbName)
                        .build();
                dbMonitorInfos.add(dbMonitorInfo);
            }

            // 处理druid
            DruidMonitorInfo druidMonitorInfo = DruidMonitorInfo.builder()
                    .traceId(key)
                    .appName(rs.getApplicationName())
                    .appNode(rs.getInstanceName())
                    .ip(DruidMonitorUtil.getLinuxLocalIp())
                    .dbMonitorInfoList(dbMonitorInfos)
                    .build();

            return druidMonitorInfo;
        }
    }
}
