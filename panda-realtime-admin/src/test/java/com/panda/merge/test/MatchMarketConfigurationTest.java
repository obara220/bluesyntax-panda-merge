package com.panda.merge.test;

import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MatchMarketCategoryConfigurationMessage;
import com.panda.merge.rocketmq.processor.MatchCategoryConfigruationProcessor;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

/**
 * 测试开盘玩法的配置处理
 * @author :  tell
 * @since : 2020年9月6日13:48:55
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MatchMarketConfigurationTest {

    @Autowired
    private MatchCategoryConfigruationProcessor matchCategoryConfigruationProcessor;

    @Test
    public void testHandleCategoryConfigrations() {
        String str = "{\"data\":{\"srWeight\":1,\"bgWeight\":3,\"marketType\":0,\"categoryList\":[{\"marketAdjustRange\":1.0,\"playId\":17,\"autoCloseMarket\":14,\"isSell\":1,\"marketCount\":1,\"oddsAdjustRange\":0.02,\"marketNearDiff\":1.0,\"matchProgressTime\":180,\"marketNearOddsDiff\":0.16,\"isSeries\":1}],\"riskManagerCode\":\"MTS\",\"standardMatchId\":1708654,\"bcWeight\":2},\"globalId\":\"bef36185b00246a483fe8f08838df36d_template_play_update\"}";
        Request<Map<String,Object>> parRequest = JSON.parseObject(str, new Request<Map<String,Object>>().getClass());
        MatchMarketCategoryConfigurationMessage data = JSON.parseObject(JSON.toJSONString(parRequest.getData()), MatchMarketCategoryConfigurationMessage.class);
        Request<MatchMarketCategoryConfigurationMessage> request = new Request<>();
        request.setLinkId("test"+System.currentTimeMillis());
        request.setData(data);
        long beginTime = System.currentTimeMillis();
        matchCategoryConfigruationProcessor.handleCategoryConfigrations(request);
        long endTime = System.currentTimeMillis();
        System.out.println("测试开盘数据服务商及需要开盘玩法的配置接收处理结束,共耗时 ："+(endTime - beginTime));

    }

    @Test
    public void testPutMarketOddsFields() {
//        String str = "{\"data\":[{\"dataSourceCode\":\"188\",\"modifyTime\":1601361021028,\"nameI18n\":[{\"languageType\":\"zs\",\"text\":\"2 - 3\"},{\"languageType\":\"en\",\"text\":\"2 - 3\"}],\"orderNo\":0,\"thirdCategorySourceId\":\"188:1:109\",\"thirdSourceId\":\"188:1:109:26\"},{\"dataSourceCode\":\"188\",\"modifyTime\":1601361021028,\"nameI18n\":[{\"languageType\":\"zs\",\"text\":\"0 - 3\"},{\"languageType\":\"en\",\"text\":\"0 - 3\"}],\"orderNo\":0,\"thirdCategorySourceId\":\"188:1:109\",\"thirdSourceId\":\"188:1:109:22\"},{\"dataSourceCode\":\"188\",\"modifyTime\":1601361021028,\"nameI18n\":[{\"languageType\":\"zs\",\"text\":\"小\"},{\"languageType\":\"en\",\"text\":\"Under\"}],\"orderNo\":0,\"thirdCategorySourceId\":\"188:1:104\",\"thirdSourceId\":\"188:1:104:8\"}],\"dataSourceTime\":1601361021366,\"linkId\":\"188_1310829246398070784\"}";
//        List<ThirdMarketCategoryFieldDTO> data = new LinkedList<>();
//        Request<List<Map<String,Object>>> parRequest = JSON.parseObject(str, new Request<List<Map<String,Object>>>().getClass());
//        for (Map<String,Object> map : parRequest.getData()) {
//            data.add(JSON.parseObject(JSON.toJSONString(map), ThirdMarketCategoryFieldDTO.class));
//        }
//        Request<List<ThirdMarketCategoryFieldDTO>> request = new Request<>();
//        request.setLinkId("testMarketOddsFields"+System.currentTimeMillis());
//        request.setData(data);
//        long beginTime = System.currentTimeMillis();
//        thirdMarketCategoryProcessor.putMarketOddsFields(request);
//        long endTime = System.currentTimeMillis();
//        System.out.println("测试玩法投注项信息接收处理结束,共耗时 ："+(endTime - beginTime));

    }

}
