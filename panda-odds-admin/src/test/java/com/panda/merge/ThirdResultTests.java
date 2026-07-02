package com.panda.merge;

import com.alibaba.fastjson.JSON;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchResultDTO;
import com.panda.merge.dto.ThirdMatchTeamRelationDetail;
import com.panda.merge.rocketmq.processor.ThirdMarketResultProcessor;
import com.panda.merge.service.ThirdMatchTeamRelationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class ThirdResultTests {


    @Autowired
    ThirdMarketResultProcessor thirdMarketResultProcessor;
    @Autowired
    RedisService redisService;
    @Autowired
    private ThirdMatchTeamRelationService thirdMatchTeamRelationService;
    @Test
    void testThirdGlobalStatus() {
//        String requestData ="{\"linkId\":\"test12345\",\"data\":\"T\",\"dataSourceTime\":0,\"dataSourceCode\":\"str\",\"entranceName\":\"str\"}";
        String requestData ="{\"linkId\":\"GR_c3e76aa7a20201027154004422909\",\"data\":[{\"sportId\":1,\"thirdMatchId\":\"14002_558600\",\"dataSourceCode\":\"GR\",\"marketResultList\":[{\"thirdMarketId\":\"14002_558600_Match_Result\",\"marketOddsResultList\":[{\"betSettlementCertainty\":\"Confirmed\",\"marketOddsId\":\"14002_558600_Match_Result_Home\",\"settlementResult\":\"3\"},{\"betSettlementCertainty\":\"Confirmed\",\"marketOddsId\":\"14002_558600_Match_Result_Away\",\"settlementResult\":\"3\"},{\"betSettlementCertainty\":\"Confirmed\",\"marketOddsId\":\"14002_558600_Match_Result_Draw\",\"settlementResult\":\"4\"}";
        Request<ThirdMatchResultDTO>  request = new Request<>();
        request = JSON.parseObject(requestData, request.getClass());
        ThirdMatchResultDTO dto = JSON.parseObject(requestData, ThirdMatchResultDTO.class);
        request.setData(dto);
        thirdMarketResultProcessor.thirdMarketResultApi(request);
    }
    @Test
    void testRedisService()
    {
        String redisKey = "Ronghe:dev:test:matchid";
        redisService.set(redisKey,"7318607");
        Object obj = redisService.get(redisKey);
        System.out.println("==========="+obj);
    }

    @Test
    void testRedisServiceTryLock()
    {
        String redisKey = "ronghelock";
        redisService.tryLock(redisKey,"999",5,3);
        redisService.unLock(redisKey,"999");
        System.out.println("==========="+redisKey);
    }

    @Test
    void testMatchTeamRelation(){
        List<ThirdMatchTeamRelationDetail> list = thirdMatchTeamRelationService.getItemsByMatchId(1312387988304515074L);
        System.out.println(list);
    }
    @Test
    void testRedisHdel()
    {
        String redisKey = "ronghelock_Hdel";
        Map<String,String> value = new HashMap<>();
        value.put("1","1");value.put("2","2");value.put("3","3");value.put("4","4");
        redisService.hSetAll(redisKey,value,100000);
        Map map = redisService.hGetAll(redisKey);
        redisService.hDel(redisKey,"3");
        map = redisService.hGetAll(redisKey);
        System.out.println("==========="+redisKey);
    }


}
