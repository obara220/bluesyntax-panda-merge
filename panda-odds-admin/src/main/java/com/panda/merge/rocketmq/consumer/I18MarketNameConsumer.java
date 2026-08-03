package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.model.I18nOutrightMarket;
import com.panda.merge.rocketmq.EnableSecondRocketMQCluster;
import com.panda.merge.service.I18nOutrightMarketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 更新盘口名称国际化<br>
 * @author :  aison
 * @since     2020年11月18日16:51:29
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = MARKET_NAME_I18N_LIST,
        consumerGroup = "odds-group-"+MARKET_NAME_I18N_LIST,
        consumeThreadMax = 20,
        consumeTimeout = 10000L
)
@EnableSecondRocketMQCluster
@DependsOn("oddsAdminApplication")
public class I18MarketNameConsumer implements RocketMQListener<Request<List<I18nOutrightMarket>>> {

    @Autowired
    private I18nOutrightMarketService i18nOutrightMarketService;
    /**
     * 数据中心赔率状态开关 1开 0关
     */
    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    @Override
    public void onMessage(Request<List<I18nOutrightMarket>> request) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", MARKET_NAME_I18N_LIST, request.getData());
            String toTopic = MARKET_NAME_I18N_LIST + DATACENTER;
            String destination = !StringUtils.isEmpty(request.getTag()) ? toTopic + ":" + request.getTag() : toTopic;
            // 发送到 数据中心Topic
            MessageBuilder<Request<List<I18nOutrightMarket>>> builder = MessageBuilder.withPayload(request)
                    .setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
            rocketMqTemplate.send(destination, builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},request:{}", request.getLinkId(), toTopic, JSON.toJSONString(request));
            return;
        }
        String linkId = request.getLinkId();
        List<I18nOutrightMarket> i18nOutrightMarketList = request.getData();
        log.info("::{}::盘口名称国际化数据req={}", linkId, JSON.toJSON(i18nOutrightMarketList));
        if (CollectionUtils.isEmpty(i18nOutrightMarketList)) {
            log.info("::{}::盘口名称国际化数据为空", linkId);
            return;
        }
        String dataSourceCode = i18nOutrightMarketList.get(0).getDataSourceCode();
        List<Long> nameCodeList = i18nOutrightMarketList.stream().map(x -> x.getNameCode()).distinct().collect(Collectors.toList());
        List<I18nOutrightMarket> i18nOutrightMarkets = i18nOutrightMarketService.selectI18nOutrightMarketList(dataSourceCode, nameCodeList);

        Map<String, I18nOutrightMarket> map = new HashMap<>();
        i18nOutrightMarketList.forEach(x -> {
            String key = x.getNameCode() + x.getDataSourceCode() + x.getLanguageType();
            map.put(key, x);
        });

        if (!CollectionUtils.isEmpty(i18nOutrightMarkets)) {
            i18nOutrightMarkets.forEach(x -> {
                String key = x.getNameCode() + x.getDataSourceCode() + x.getLanguageType();
                if (null != map.get(key)) {
                    x.setText(map.get(key).getText());
                }
            });
            i18nOutrightMarketService.updateBatchById(i18nOutrightMarkets);
            log.info("::{}::盘口名称国际化数据更新成功", linkId);
        }
    }

}
