package com.panda.merge.test;

import com.alibaba.fastjson.JSON;
import com.panda.merge.dao.ThirdSportPlayerDao;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMarketCategoryDTO;
import com.panda.merge.dto.ThirdMarketCategoryFieldDTO;
import com.panda.merge.rocketmq.processor.ThirdMarketCategoryProcessor;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 测试球队球员信息接收处理
 * @author :  tell
 * @since : 2020年9月6日13:48:55
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ThirdMarketCategoryTest {

    @Autowired
    private ThirdMarketCategoryProcessor thirdMarketCategoryProcessor;

    @Autowired
    ThirdSportPlayerDao thirdSportPlayerDao;

    @Test
    public void testPutMarketCategory() {
        String str = "{\"data\":[{\"active\":1,\"dataSourceCode\":\"188\",\"fieldsNum\":3,\"modifyTime\":1601361010965,\"nameI18n\":[{\"languageType\":\"zs\",\"text\":\"独赢-上半场\"},{\"languageType\":\"en\",\"text\":\"1 X 2 - 1st Half\"}],\"supportSports\":[1],\"thirdSourceId\":\"188:1:106\"},{\"active\":1,\"dataSourceCode\":\"188\",\"fieldsNum\":2,\"modifyTime\":1601361010965,\"nameI18n\":[{\"languageType\":\"zs\",\"text\":\"双半场进球\"},{\"languageType\":\"en\",\"text\":\"To Score in Both Halves\"}],\"supportSports\":[1],\"thirdSourceId\":\"188:1:307\"},{\"active\":1,\"dataSourceCode\":\"188\",\"fieldsNum\":6,\"modifyTime\":1601361010965,\"nameI18n\":[{\"languageType\":\"zs\",\"text\":\"双重机会&amp;进球 大/小1.5\"},{\"languageType\":\"en\",\"text\":\"Double Chance &amp; Goals O/U 1.5\"}],\"supportSports\":[1],\"thirdSourceId\":\"188:1:312\"}],\"dataSourceTime\":1601361010967,\"linkId\":\"188_1310829204194983936\"}";
        List<ThirdMarketCategoryDTO> data = new LinkedList<>();
        Request<List<Map<String,Object>>> parRequest = JSON.parseObject(str, new Request<List<Map<String,Object>>>().getClass());
        for (Map<String,Object> map : parRequest.getData()) {
            data.add(JSON.parseObject(JSON.toJSONString(map), ThirdMarketCategoryDTO.class));
        }
        Request<List<ThirdMarketCategoryDTO>> request = new Request<>();
        request.setLinkId("testMarketCategory"+System.currentTimeMillis());
        request.setData(data);
        long beginTime = System.currentTimeMillis();
        thirdMarketCategoryProcessor.putMarketCategory(request);
        long endTime = System.currentTimeMillis();
        System.out.println("测试玩法信息接收处理结束,共耗时 ："+(endTime - beginTime));

    }

    @Test
    public void testPutMarketOddsFields() {
        String str = "{\"data\":[{\"dataSourceCode\":\"188\",\"modifyTime\":1601361021028,\"nameI18n\":[{\"languageType\":\"zs\",\"text\":\"2 - 3\"},{\"languageType\":\"en\",\"text\":\"2 - 3\"}],\"orderNo\":0,\"thirdCategorySourceId\":\"188:1:109\",\"thirdSourceId\":\"188:1:109:26\"},{\"dataSourceCode\":\"188\",\"modifyTime\":1601361021028,\"nameI18n\":[{\"languageType\":\"zs\",\"text\":\"0 - 3\"},{\"languageType\":\"en\",\"text\":\"0 - 3\"}],\"orderNo\":0,\"thirdCategorySourceId\":\"188:1:109\",\"thirdSourceId\":\"188:1:109:22\"},{\"dataSourceCode\":\"188\",\"modifyTime\":1601361021028,\"nameI18n\":[{\"languageType\":\"zs\",\"text\":\"小\"},{\"languageType\":\"en\",\"text\":\"Under\"}],\"orderNo\":0,\"thirdCategorySourceId\":\"188:1:104\",\"thirdSourceId\":\"188:1:104:8\"}],\"dataSourceTime\":1601361021366,\"linkId\":\"188_1310829246398070784\"}";
        List<ThirdMarketCategoryFieldDTO> data = new LinkedList<>();
        Request<List<Map<String,Object>>> parRequest = JSON.parseObject(str, new Request<List<Map<String,Object>>>().getClass());
        for (Map<String,Object> map : parRequest.getData()) {
            data.add(JSON.parseObject(JSON.toJSONString(map), ThirdMarketCategoryFieldDTO.class));
        }
        Request<List<ThirdMarketCategoryFieldDTO>> request = new Request<>();
        request.setLinkId("testMarketOddsFields"+System.currentTimeMillis());
        request.setData(data);
        long beginTime = System.currentTimeMillis();
        thirdMarketCategoryProcessor.putMarketOddsFields(request);
        long endTime = System.currentTimeMillis();
        System.out.println("测试玩法投注项信息接收处理结束,共耗时 ："+(endTime - beginTime));

    }

}
