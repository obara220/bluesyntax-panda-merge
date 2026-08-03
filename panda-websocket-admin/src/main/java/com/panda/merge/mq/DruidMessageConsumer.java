//package com.panda.merge.mq;
//
//import com.alibaba.druid.stat.DruidStatManagerFacade;
//import com.alibaba.fastjson.JSONObject;
//import com.google.common.collect.Lists;
//import com.panda.merge.common.utils.Sequence;
//import lombok.extern.slf4j.Slf4j;
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
//import java.util.List;
//import java.util.Map;
//
//import static com.panda.merge.constant.ConstantSystem.CONSUME_NONREALTIME_GROUP;
//import static com.panda.merge.constant.ConstantSystem.CONSUME_PANDA_WEBSOCKET_ADMIN_GROUP;
//import static com.panda.merge.constant.ConstantSystem.DATA_DRUID_MESSAGE_HANDLER;
//import static com.panda.merge.constant.ConstantSystem.DATA_PUSH_DRUID_MESSAGE;
//
///**
// * 监听数据库信息
// *
// * @author warren
// * @since 2025/01/05 14:12:28
// */
//@Slf4j
//@Component
//@RocketMQMessageListener(
//        topic = DATA_PUSH_DRUID_MESSAGE,
//        consumerGroup = CONSUME_PANDA_WEBSOCKET_ADMIN_GROUP + DATA_PUSH_DRUID_MESSAGE,
//        messageModel = MessageModel.BROADCASTING,
//        consumeMode = ConsumeMode.CONCURRENTLY)
//public class DruidMessageConsumer implements RocketMQListener<JSONObject> {
//
//    @Autowired
//    private RocketMQTemplate rocketMqTemplate;
//
//    /**
//     * DruidStatManagerFacade
//     */
//    private final DruidStatManagerFacade druidStatManagerFacade = DruidStatManagerFacade.getInstance();
//
//    @Value("${spring.application.name}")
//    private String applicationName;
//
//    /**
//     * json数据格式
//     * {traceId:根据跟踪id，需要在投送的监控消息体返回,timeStamp:时间戳,scope:default 默认(目前只有这种模式),为空也是默认}
//     */
//    @Override
//    public void onMessage(JSONObject json) {
//        try {
//            String linuxLocalIp = Sequence.getLinuxLocalIp();
//            String traceId = json.getString("traceId");
//            String linkId = traceId + "_" + linuxLocalIp;
//            String data = getData(linuxLocalIp, traceId);
//            MessageBuilder<String> builder = MessageBuilder.withPayload(data).setHeader(MessageConst.PROPERTY_KEYS, linkId);
//            rocketMqTemplate.send(DATA_DRUID_MESSAGE_HANDLER + ":" + linuxLocalIp, builder.build());
//            log.info("::{}::topic={}WS服务数据库信息处理结束,data={}", linkId, DATA_DRUID_MESSAGE_HANDLER, data);
//        } catch (Exception e) {
//            log.info("WS服务监听数据库信息异常");
//        }
//    }
//
//    public String getData(String linuxLocalIp, String traceId) {
//        List<Map<String, Object>> lists = druidStatManagerFacade.getDataSourceStatDataList(true);
//        if (CollectionUtils.isEmpty(lists)) {
//            lists = Lists.newArrayList();
//        }
//        JSONObject obj = new JSONObject();
//        obj.put("lists", lists);
//        obj.put("ip", linuxLocalIp);
//        //DruidMonitorInfoCollectRequest 获取到traceId
//        obj.put("traceId", traceId);
//        //spring.application.name 配置值
//        obj.put("appName", applicationName);
//        InetAddress address = null;
//        try {
//            address = InetAddress.getLocalHost();
//        } catch (UnknownHostException e) {
//            log.info("get appNode error.");
//        }
//        obj.put("appNode", address == null ? "" : address.getHostName());
//        return JSONObject.toJSONString(obj);
//    }
//}
