package com.panda.merge;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdSportTournamentDTO;
import com.panda.merge.rocketmq.processor.ThirdSportTournamentProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class RealtimeAdminApplicationTests {


    @Autowired
    ThirdSportTournamentProcessor thirdSportTournamentProcessor;



    @Test
    void testThirdGlobalStatusss() {
        String requestData ="{\"linkId\":\"GR_9c4754f1120201030103223057722\",\"data\":\"T\",\"dataSourceTime\":0,\"dataSourceCode\":\"str\",\"entranceName\":\"str\"}";
        String data="[{\"createTime\":1603980433171,\"dataSourceCode\":\"GR\",\"name\":\"Horses 8 Flat\",\"sportId\":1011,\"sportRegionId\":\"0\",\"thirdTournamentSourceId\":\"gr:tournament:24007\",\"tournamentNameList\":[{\"languageType\":\"en\",\"text\":\"Horses 8 Flat\"},{\"languageType\":\"zs\",\"text\":\"Horses 8 Flat\"}]}]";
        Request<List<ThirdSportTournamentDTO>> request = new Request<>();
        request = JSON.parseObject(requestData, request.getClass());

        JSONArray dto = JSONArray.parseArray(data);
        List<ThirdSportTournamentDTO>   list= new ArrayList<>();
        for (Object o : dto.toArray()) {
            ThirdSportTournamentDTO dto1= JSON.parseObject(o.toString(),ThirdSportTournamentDTO.class);
            list.add(dto1);
        }
        request.setData(list);
        thirdSportTournamentProcessor.processTournamentData(request);
    }

}
