//package com.panda.merge.rocketmq.consumer;
//
//import com.alibaba.druid.stat.DruidStatManagerFacade;
//import com.alibaba.fastjson.JSONObject;
//import com.panda.merge.common.utils.Sequence;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.rocketmq.common.message.MessageConst;
//import org.apache.rocketmq.spring.annotation.ConsumeMode;
//import org.apache.rocketmq.spring.annotation.MessageModel;
//import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
//import org.apache.rocketmq.spring.core.RocketMQListener;
//import org.apache.rocketmq.spring.core.RocketMQTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.messaging.support.MessageBuilder;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import static com.panda.merge.constant.ConstantSystem.*;
//
///**
// * 监听数据库信息（广播消费模式，每个服务节点都会消费）
// * */
//@Slf4j
//@Component
//@RocketMQMessageListener(
//        topic = DATA_PUSH_DRUID_MESSAGE,
//        consumerGroup = CONSUME_REALTIME_GROUP + DATA_PUSH_DRUID_MESSAGE,
//        messageModel = MessageModel.BROADCASTING,
//        consumeMode = ConsumeMode.CONCURRENTLY)
//public class DruidMessageConsumer implements RocketMQListener<JSONObject> {
//
//    @Autowired
//    private RocketMQTemplate rocketMqTemplate;
//
//    //DruidStatManagerFacade
//    private final DruidStatManagerFacade druidStatManagerFacade = DruidStatManagerFacade.getInstance();
//
//    @Value("${spring.application.name}")
//    private String applicationName;
//
//    /**
//     * json数据格式
//     *  {traceId:根据跟踪id，需要在投送的监控消息体返回,timeStamp:时间戳,scope:default 默认(目前只有这种模式),为空也是默认}
//     * */
//    @Override
//    public void onMessage(JSONObject json) {
//        String linkId = null;
//        try {
//            String linuxLocalIp = Sequence.getLinuxLocalIp();
//            String traceId = StringUtils.defaultIfBlank(json.getString("traceId"), System.currentTimeMillis()+"");
//            linkId = traceId +"_"+linuxLocalIp;
//            String data = getData(linuxLocalIp, traceId);
//            MessageBuilder<String> builder = MessageBuilder.withPayload(data).setHeader(MessageConst.PROPERTY_KEYS, linkId);
//            rocketMqTemplate.send(DATA_DRUID_MESSAGE_HANDLER+":realtime"+linuxLocalIp, builder.build());
//            log.info("linkId=【{}】,topic={},实时服务数据库监听信息处理结束,data={}", linkId, DATA_DRUID_MESSAGE_HANDLER, data);
//        } catch (Exception e) {
//            log.error("linkId=【"+linkId+"】,实时服务数据库监听信息异常,Exception:",e);
//        }
//    }
//
//    public String getData(String linuxLocalIp,String traceId) {
//        JSONObject obj = new JSONObject(new LinkedHashMap<>(8));
//        List<Map<String, Object>> lists = null;
//        try {
//            // 关闭reset统计开关（保留历史数据）
//            lists = druidStatManagerFacade.getDataSourceStatDataList(false);
//            obj.put("lists", CollectionUtils.isEmpty(lists) ? new ArrayList<>() : lists);
//        } catch (Exception e) {
//            obj.put("lists", Collections.emptyList());
//            log.error("traceId=【"+traceId+"】,实时服务数据库监听Druid数据采集异常,Exception:",e);
//        }
//
//        try {
//            //本地调试校验
//            if(!CollectionUtils.isEmpty(lists)){
//                checkData(lists,traceId);
//            }
//        } catch (Exception e) {
//            log.error("traceId=【"+traceId+"】,实时服务数据库监听Druid数据采集本地调试校验异常,Exception:",e);
//        }
//        obj.put("ip", linuxLocalIp);
//        //DruidMonitorInfoCollectRequest 获取到traceId
//        obj.put("traceId", traceId);
//        //spring.application.name 配置值
//        obj.put("appName", applicationName);
//        InetAddress address = null;
//        try {
//            address = InetAddress.getLocalHost();
//        } catch (UnknownHostException e) {
//            log.error("traceId=【"+traceId+"】,实时服务数据库监听获取节点名称异常,Exception:",e);
//        }
//        obj.put("appNode", address == null ? "" : address.getHostName());
//        return JSONObject.toJSONString(obj);
//    }
//
//
//    // 配置参数
//    final long SLOW_QUERY_THRESHOLD = 2000; // 慢查询阈值(ms)
//    final int ERROR_COUNT_THRESHOLD = 1;    // 错误次数阈值
//    final String DEADLOCK_KEYWORD = "Deadlock"; // 死锁标识
//
//    public void checkData(List<Map<String, Object>> stats,String traceId) {
//        List<Map<String, Object>> result = new ArrayList<>();
//        stats.stream()
//                .filter(ds -> "mysql".equalsIgnoreCase((String) ds.get("DbType")))
//                .forEach(ds -> {
//                    Map<String, Object> report = new LinkedHashMap<>();
//                    report.put("数据源", ds.get("Name"));
//
//                    Map<String, Object> sqlStats = (Map<String, Object>) ds.get("SqlStatMap");
//                    List<Map<String, String>> slowSQL = new ArrayList<>();
//                    List<Map<String, String>> errorSQL = new ArrayList<>();
//                    List<Map<String, String>> deadlockSQL = new ArrayList<>();
//                    if(CollectionUtils.isEmpty(sqlStats)){
//                        sqlStats.forEach((id, stat) -> {
//                            Map<String, Object> sqlStat = (Map<String, Object>) stat;
//                            String sql = (String) sqlStat.get("Sql");
//
//                            // 慢查询检测
//                            long maxTime = ((Number) sqlStat.get("ExecuteMillisMax")).longValue();
//                            if (maxTime > SLOW_QUERY_THRESHOLD) {
//                                Map<String, String> errInfo = new LinkedHashMap<>();
//                                errInfo.put("SQL", abbreviateSQL(sql));
//                                errInfo.put("最大耗时", maxTime + "ms");
//                                errInfo.put("最后执行", formatTime(sqlStat.get("LastSlowParameters")));
//                                slowSQL.add(errInfo);
//                            }
//
//                            // 异常检测
//                            int errors = ((Number) sqlStat.get("ErrorCount")).intValue();
//                            String lastError = (String) sqlStat.getOrDefault("LastErrorMessage", "");
//                            if (errors >= ERROR_COUNT_THRESHOLD) {
//                                Map<String, String> errInfo = new LinkedHashMap<>();
//                                errInfo.put("SQL", abbreviateSQL(sql));
//                                errInfo.put("错误次数", errors + "次");
//                                errInfo.put("最后错误", lastError);
//
//                                // 死锁专项识别
//                                if (lastError.contains(DEADLOCK_KEYWORD)) {
//                                    deadlockSQL.add(errInfo);
//                                } else {
//                                    errorSQL.add(errInfo);
//                                }
//                            }
//                        });
//                    }
//                    // 聚合结果
//                    report.put("慢查询统计", slowSQL.size());
//                    report.put("异常SQL统计", errorSQL.size());
//                    report.put("死锁SQL统计", deadlockSQL.size());
//                    report.put("全部异常SQL", Stream.of(slowSQL, errorSQL, deadlockSQL)
//                            .flatMap(List::stream)
//                            .collect(Collectors.toList()));
//
//                    result.add(report);
//                });
//
//        log.info("traceId=【{}】,实时服务数据库监听本地调试校验结果,result={}", traceId, JSONObject.toJSONString(result));
//    }
//    // 辅助方法
//    private String abbreviateSQL(String sql) {
//        return sql.length() > 100 ? sql.substring(0, 97) + "..." : sql;
//    }
//    private String formatTime(Object timestamp) {
//        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
//    }
//
//}
