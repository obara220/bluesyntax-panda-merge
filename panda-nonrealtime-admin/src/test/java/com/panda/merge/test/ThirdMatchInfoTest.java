package com.panda.merge.test;


import com.alibaba.fastjson.JSON;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdSportTournamentDTO;
import com.panda.merge.dto.nonrealttime.put.ThirdMatchInfoDTO;
import com.panda.merge.rocketmq.consumer.ThirdMatchInfoConsumer;
import com.panda.merge.rocketmq.processor.ThirdMatchInfoProcessor;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 测试三方赛事信息接收处理
 * @author :  tell
 * @since : 2020年9月6日13:48:55
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class ThirdMatchInfoTest {

    @Autowired
    RedisService redisService;

    @Autowired
    ThirdMatchInfoProcessor thirdMatchInfoProcessor;

    @Autowired
    ThirdMatchInfoConsumer thirdMatchInfoConsumer;

    @Test
    public void testPutThirdMatchInfo() {
        String json = "{\"linkId\":\"PN_16746316512344\",\"data\":[{\"thirdMatchSourceId\":\"1485566044\",\"sourceTournamentId\":\"493\",\"sportId\":4,\"dataSourceCode\":\"PN\",\"thirdRegionId\":\"unknown\",\"beginTime\":1644530400000,\"matchLength\":0,\"matchStatus\":\"0\",\"matchPeriod\":\"0\",\"matchTeamList\":[{\"thirdTeamId\":\"2058243\",\"name\":\"William & Mary\",\"type\":\"Team\",\"statium\":\"home\",\"teamNameList\":[{\"languageType\":\"zs\",\"text\":\"William & Mary\"},{\"languageType\":\"en\",\"text\":\"William & Mary\"}],\"matchTeamRelation\":{\"matchPosition\":\"home\",\"teamNameRecord\":\"William & Mary\"}},{\"thirdTeamId\":\"2058244\",\"name\":\"Towson\",\"type\":\"Team\",\"statium\":\"away\",\"teamNameList\":[{\"languageType\":\"zs\",\"text\":\"Towson\"},{\"languageType\":\"en\",\"text\":\"Towson\"}],\"matchTeamRelation\":{\"matchPosition\":\"away\",\"teamNameRecord\":\"Towson\"}}],\"homeAwayInfo\":\"William & Mary vs Towson\",\"neutralGround\":0,\"liveOddSupport\":1,\"active\":1,\"booked\":0,\"siteType\":0,\"lotteryNumber\":\"\",\"createTime\":1644486997784,\"competitionName\":\"NCAA\",\"status\":0}],\"dataSourceTime\":1644486997784}";
        List<ThirdMatchInfoDTO> data = new ArrayList<>();
        Request<List<Map<String, Object>>> parRequest = JSON.parseObject(json, new Request<List<Map<String, Object>>>().getClass());
        for (Map<String, Object> map : parRequest.getData()) {
            data.add(JSON.parseObject(JSON.toJSONString(map), ThirdMatchInfoDTO.class));
        }
        long beginTime = System.currentTimeMillis();
        Request<List<ThirdMatchInfoDTO>> request = new Request<>();
        request.setData(data);
        request.setLinkId(System.currentTimeMillis()+"");
        thirdMatchInfoProcessor.processMatchData(request);
        System.out.println("执行时间 :  " + (System.currentTimeMillis() - beginTime));
    }

    @Test
    public void testRedis(){
        for (int i=0;i<50;i++) {
            long time = System.currentTimeMillis();
            redisService.get("Ronghe:StandardMarket:RelationMarketId:1500090_39_7.5");
            System.out.println(System.currentTimeMillis()-time);
        }
    }

    public static void main(String[] args) {
        String json = "{\"linkId\":\"BG_ac12b2f420220211110927131b0ebd8e0dd8\",\"data\":[{\"thirdMatchSourceId\":\"8424819\",\"sourceTournamentId\":\"186\",\"sportId\":10,\"dataSourceCode\":\"BG\",\"thirdRegionId\":\"225\",\"beginTime\":1650027600000,\"matchLength\":0,\"matchStatus\":\"0\",\"matchTeamList\":[{\"thirdTeamId\":\"115849\",\"name\":\"Grosseto\",\"type\":7,\"coach\":\"\",\"statium\":\"home\",\"teamNameList\":[{\"languageType\":\"en\",\"text\":\"Grosseto\"},{\"languageType\":\"zs\",\"text\":\"Grosseto\"}],\"matchTeamRelation\":{\"matchPosition\":\"home\",\"teamNameRecord\":\"Grosseto\",\"remark\":\"\"},\"countryId\":\"\",\"countryName\":\"\",\"logoUrl\":\"\",\"remark\":\"\"},{\"thirdTeamId\":\"117933\",\"name\":\"Lucchese\",\"type\":7,\"coach\":\"\",\"statium\":\"away\",\"teamNameList\":[{\"languageType\":\"en\",\"text\":\"Lucchese\"},{\"languageType\":\"zs\",\"text\":\"Lucchese\"}],\"matchTeamRelation\":{\"matchPosition\":\"away\",\"teamNameRecord\":\"Lucchese\",\"remark\":\"\"},\"countryId\":\"\",\"countryName\":\"\",\"logoUrl\":\"\",\"remark\":\"\"}],\"homeAwayInfo\":\"Grosseto v Lucchese\",\"neutralGround\":0,\"liveOddSupport\":0,\"active\":0,\"booked\":0,\"createTime\":1644548967131,\"seasonId\":\"99336\"}],\"dataSourceTime\":1644548967131}";
        List<ThirdMatchInfoDTO> data = new ArrayList<>();
        Request<List<Map<String, Object>>> parRequest = JSON.parseObject(json, new Request<List<Map<String, Object>>>().getClass());
        for (Map<String, Object> map : parRequest.getData()) {
            data.add(JSON.parseObject(JSON.toJSONString(map), ThirdMatchInfoDTO.class));
        }
        Request<List<ThirdMatchInfoDTO>> request = new Request<>();
        request.setData(data);
        request.setLinkId(System.currentTimeMillis()+"");
        System.out.println(request);

        String jsonStr = "{\"booked\":0,\"homeAwayInfo\":\"William & Mary vs Towson\",\"competitionName\":\"NCAA\",\"active\":1,\"dataSourceCode\":\"PN\",\"thirdRegionId\":\"unknown\",\"matchLength\":0,\"matchTeamList\":[{\"matchTeamRelation\":{\"matchPosition\":\"home\",\"teamNameRecord\":\"William & Mary\"},\"statium\":\"home\",\"name\":\"William & Mary\",\"teamNameList\":[{\"languageType\":\"zs\",\"text\":\"William & Mary\"},{\"languageType\":\"en\",\"text\":\"William & Mary\"}],\"type\":\"Team\",\"thirdTeamId\":\"2058243\"},{\"matchTeamRelation\":{\"matchPosition\":\"away\",\"teamNameRecord\":\"Towson\"},\"statium\":\"away\",\"name\":\"Towson\",\"teamNameList\":[{\"languageType\":\"zs\",\"text\":\"Towson\"},{\"languageType\":\"en\",\"text\":\"Towson\"}],\"type\":\"Team\",\"thirdTeamId\":\"2058244\"}],\"neutralGround\":0,\"sportId\":4,\"matchStatus\":\"0\",\"thirdMatchSourceId\":\"1485566044\",\"createTime\":1644486997784,\"lotteryNumber\":\"\",\"sourceTournamentId\":\"493\",\"beginTime\":1644530400000,\"matchPeriod\":\"0\",\"liveOddSupport\":1,\"siteType\":0,\"status\":0}";
        ThirdMatchInfoDTO thirdMatchInfoDTO = JSON.parseObject(jsonStr, ThirdMatchInfoDTO.class);
        System.out.println(thirdMatchInfoDTO);
    }

}
