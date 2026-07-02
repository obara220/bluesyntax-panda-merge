package com.panda.merge;


import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.StandardOutrightMatchDTO;
import com.panda.merge.rocketmq.processor.StandardOutRightMatchProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ChampionMarketTest {

    @Autowired
    private StandardOutRightMatchProcessor standardOutRightMatchProcessor;

    @Test
    public void createChampionMarketData() {
        Request<StandardOutrightMatchDTO> request = new Request<>();
        request.setLinkId("e4w8jckxhbvjzdzisuda5p2jb8oeyh8t");
        String paramStr = "{\"thirdOutrightMatchId\":1531712367603830785,\"standardOutrightMatchId\":\"323560280953389058\",\"dataSourceCode\":\"SR\",\"autoSellStatus\":\"No\"}";
        StandardOutrightMatchDTO standardOutrightMatchDTO = JSON.parseObject(paramStr, StandardOutrightMatchDTO.class);
        request.setData(standardOutrightMatchDTO);
        standardOutRightMatchProcessor.processStandardOutRightMatch(request);
    }
}
