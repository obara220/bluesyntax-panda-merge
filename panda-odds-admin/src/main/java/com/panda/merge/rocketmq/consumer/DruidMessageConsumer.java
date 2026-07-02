package com.panda.merge.rocketmq.consumer;

/**
 * 监听数据库信息
 */
/*
@Slf4j
@Component
@RocketMQMessageListener(
        topic = DATA_PUSH_DRUID_MESSAGE,
        consumerGroup = PAND_ODDS_GROUP+DATA_PUSH_DRUID_MESSAGE,
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class DruidMessageConsumer implements RocketMQListener<JSONObject> {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Value("${spring.application.name}")
    private String applicationName;

    //DruidStatManagerFacade
    private final DruidStatManagerFacade druidStatManagerFacade = DruidStatManagerFacade.getInstance();

    *//**
     * json数据格式
     *  {traceId:根据跟踪id，需要在投送的监控消息体返回,timeStamp:时间戳,scope:default 默认(目前只有这种模式),为空也是默认}
     * *//*
    @Override
    public void onMessage(JSONObject json) {
        try {
            String linuxLocalIp = Sequence.getLinuxLocalIp();
            String traceId = json.getString("traceId");
            String linkId = traceId +"_"+linuxLocalIp;
            String data = getData(linuxLocalIp, traceId);
            MessageBuilder<String> builder = MessageBuilder.withPayload(data).setHeader(MessageConst.PROPERTY_KEYS, linkId);
            rocketMqTemplate.send(DATA_DRUID_MESSAGE_HANDLER+":panda-odds_"+linuxLocalIp, builder.build());
            log.info("::{}::topic={}融合赔率服务数据库信息处理结束,data={}", linkId, DATA_DRUID_MESSAGE_HANDLER, data);
        } catch (Exception e) {
            log.error("融合赔率服务数据库信息处理异常");
        }
    }

    public String getData(String linuxLocalIp,String traceId) {
        List<Map<String, Object>> lists = druidStatManagerFacade.getDataSourceStatDataList(true);
        if (CollectionUtils.isEmpty(lists)) {
            lists = Lists.newArrayList();
        }
        JSONObject obj = new JSONObject();
        obj.put("lists", lists);
        obj.put("ip", linuxLocalIp);
        //DruidMonitorInfoCollectRequest 获取到traceId
        obj.put("traceId", traceId);
        //spring.application.name 配置值
        obj.put("appName", applicationName);
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            log.info("get appNode error.");
        }
        obj.put("appNode", address == null ? "" : address.getHostName());
        return JSONObject.toJSONString(obj);
    }
}*/
