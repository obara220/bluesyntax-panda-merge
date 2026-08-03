package com.panda.merge.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.panda.merge.api.IStandardMarketOddsApi;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.StandardMarketOddsDTO;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StandardMarketOddsApiTest {

    @Autowired
    private IStandardMarketOddsApi iStandardMarketOddsApi;
    @Autowired
    private RedisService redisService;

    @Test
    public void testGgetAoOriginalOddsById() {
    	String key = Constant.REDIS_KEY.RONGHE_AO_MARKET_ORIGINAL_ODDS+123456;
    	Map<String,Integer> oddsMap = new HashMap<String,Integer>();
    	oddsMap.put("1",199000);
    	oddsMap.put("2",197000);
    	redisService.hSetAll(key, oddsMap);
        Request<StandardMarketOddsDTO> reqDto = new Request<StandardMarketOddsDTO>();
        StandardMarketOddsDTO t = new StandardMarketOddsDTO();
        t.setMatchId(123456l);
        t.setId("1");
        reqDto.setData(t);
        reqDto.setLinkId("f23948uf20934830945");
    	System.out.println(iStandardMarketOddsApi.getAoOriginalOddsById(reqDto));
    }

}
