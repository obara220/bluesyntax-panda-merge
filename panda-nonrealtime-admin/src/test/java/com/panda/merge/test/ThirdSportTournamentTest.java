package com.panda.merge.test;


import com.alibaba.fastjson.JSON;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ThirdSportTournamentDTO;
import com.panda.merge.mapper.ThirdSportTournamentMapper;
import com.panda.merge.model.ThirdSportTournament;
import com.panda.merge.model.ThirdSportTournamentExample;
import com.panda.merge.rocketmq.processor.ThirdSportTournamentProcessor;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 测试三方联赛信息接收处理
 * @author :  tell
 * @since : 2020年9月6日13:48:55
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ThirdSportTournamentTest {

    @Autowired
    RedisService redisService;

    @Autowired
    ThirdSportTournamentMapper thirdSportTournamentMapper;

    @Autowired
    ThirdSportTournamentProcessor thirdSportTournamentProcessor;

    @Test
    public void testPutThirdSportTournament() {
        redisService.del(RedisConfig.REDIS_KEY_DATABASE);
        String json = "{\"data\":[{\"dataSourceCode\":\"TS\",\"logoUrl\":\"group1/M00/02/13/rBKyv172ttaAAzjTAAEpw-_1Erk058.png\",\"name\":\"欧洲足球锦标赛\",\"sportId\":1,\"sportRegionId\":\"1535\",\"sportRegionName\":\"Busan\",\"thirdSeasonSourceId\":\"232396\",\"thirdTournamentSourceId\":\"1\",\"tournamentNameList\":[{\"languageType\":\"zs\",\"text\":\"欧洲足球锦标赛\"},{\"languageType\":\"en\",\"text\":\"European Championship\"},{\"languageType\":\"jc\",\"text\":\"欧洲杯\"}]}],\"dataSourceTime\":1602993188255,\"linkId\":\"TS_ac12b42220201018115308255a3bc84c\"}";
        List<ThirdSportTournamentDTO> data = new LinkedList<>();
        Request<List<Map<String, Object>>> parRequest = JSON.parseObject(json, new Request<List<Map<String, Object>>>().getClass());
        for (Map<String, Object> map : parRequest.getData()) {
            data.add(JSON.parseObject(JSON.toJSONString(map), ThirdSportTournamentDTO.class));
        }
        Request<List<ThirdSportTournamentDTO>> request = new Request<>();
        request.setData(data);
        request.setLinkId("tetsTournament"+System.currentTimeMillis());
        long beginTime = System.currentTimeMillis();
        Response response = thirdSportTournamentProcessor.processTournamentData(request);
        long endTime = System.currentTimeMillis();
        System.out.println("测试联赛信息接收处理结束,共耗时 ：" + (endTime - beginTime) + ", response: " + response);
    }

    @Test
    public void testGetThirdSportTournament(){
        ThirdSportTournamentExample example = new ThirdSportTournamentExample();
        example.createCriteria().andDataSourceCodeEqualTo("SR").andSportIdEqualTo(1L).andThirdTournamentSourceIdEqualTo("sr:tournament:1");
        List<ThirdSportTournament> resList = thirdSportTournamentMapper.selectByExampleWithBLOBs(example);
        System.out.println("resList : "+resList);
    }

}
