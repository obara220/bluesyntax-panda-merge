package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.model.I18nOutrightMarket;
import com.panda.merge.rocketmq.EnableSecondRocketMQCluster;
import com.panda.merge.service.I18nOutrightMarketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.MARKET_NAME_I18N_LIST;

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
        consumeThreadMax = 256,
        consumeTimeout = 10000L
)
@EnableSecondRocketMQCluster
@DependsOn("oddsAdminApplication")
public class I18MarketNameConsumer implements RocketMQListener<Request<List<I18nOutrightMarket>>> {

    @Autowired
    private I18nOutrightMarketService i18nOutrightMarketService;

    @Override
    public void onMessage(Request<List<I18nOutrightMarket>> listRequest) {
        String linkId = listRequest.getLinkId();
        List<I18nOutrightMarket> i18nOutrightMarketList = listRequest.getData();
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
