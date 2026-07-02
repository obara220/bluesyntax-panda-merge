package com.panda.merge;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdGlobalStatusDTO;
import com.panda.merge.dto.ThirdSportTournamentDTO;
import com.panda.merge.dto.TradeMarketConfigDTO;
import com.panda.merge.rocketmq.processor.ThirdGlobalStatusProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class RealtimeAdminApplicationTests {

    @Autowired
    ThirdGlobalStatusProcessor thirdGlobalStatusProcessor;


    @Test
    void testThirdGlobalStatus() {
        String requestData ="{\"linkId\":\"test12345\",\"data\":\"T\",\"dataSourceTime\":0,\"dataSourceCode\":\"str\",\"entranceName\":\"str\"}";
        String data="{\"serialVersionUID\":0,\"dataSourceCode\":\"SRTEST\",\"status\":\"down\",\"sourceTimestamp\":0,\"sendTimestamp\":0}";
        Request<ThirdGlobalStatusDTO> request = new Request<>();
        request = JSON.parseObject(requestData, request.getClass());
        ThirdGlobalStatusDTO dto = JSON.parseObject(data, ThirdGlobalStatusDTO.class);
        request.setData(dto);
        thirdGlobalStatusProcessor.putGlobalStatus(request);
    }


}
