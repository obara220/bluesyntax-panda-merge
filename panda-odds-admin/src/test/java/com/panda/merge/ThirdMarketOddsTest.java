package com.panda.merge;

import com.panda.merge.common.enums.Constant;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisService;
import com.panda.merge.model.StandardOutrightMarket;
import com.panda.merge.model.ThirdSportMarket;
import com.panda.merge.service.StandardOutrightMarketService;
import com.panda.merge.service.ThirdSportMarketService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ThirdMarketOddsTest {

    @Autowired
    private RedisService redisService;
    @Autowired
    private StandardOutrightMarketService standardOutrightMarketService;
    @Autowired
    private ThirdSportMarketService thirdSportMarketService;


    @Test
    public void processCreateReplenish(){
        ThirdSportMarket thirdSportMarket = new ThirdSportMarket();
        thirdSportMarket.setMatchId(6565656l);
        thirdSportMarket.setDataSourceCode("SR");
        thirdSportMarket.setThirdMarketSourceId("29209418_209_1_611_10_");
        thirdSportMarket.setRemark("123");
        //thirdSportMarketService.createReplenish("123",thirdSportMarket);
    }

    @Test
    public void processStandardOutRightMatch(){
        List<StandardOutrightMarket> standardOutrightMarketList = new ArrayList<>();
        StandardOutrightMarket standardOutrightMarket = new StandardOutrightMarket();
        standardOutrightMarket.setId(1307882416232378370L);
        standardOutrightMarket.setStandardMatchId(46506L);
        standardOutrightMarket.setMarketStatus(Constant.SPORT_MARKET.STATUS.ACTIVE);
        standardOutrightMarket.setNameCode(UUIdUtils.getId());
        standardOutrightMarket.setLinkId("dad231ss5556");
        standardOutrightMarket.setMarketSellStatus("0");
        standardOutrightMarketList.add(standardOutrightMarket);
        standardOutrightMarketService.saveBatch(standardOutrightMarketList);
    }

    @Test
    public void testRedis() {
        int count = 0;
        while (count < 10) {
            StopWatch sw = new StopWatch();
            sw.start("" + count);
            //redisService.get("")
            sw.stop();
            count++;
        }
    }
}
