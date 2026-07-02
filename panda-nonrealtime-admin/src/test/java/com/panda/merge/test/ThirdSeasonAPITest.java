package com.panda.merge.test;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdSeasonInfoDTO;
import com.panda.merge.rocketmq.processor.ThirdSeasonInfoProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author Kepa
 * @Date 2021/3/3 17:17
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ThirdSeasonAPITest {

    @Autowired
    ThirdSeasonInfoProcessor thirdSeasonInfoProcessor;


    @Test
    public void toTestSeason () {
        System.out.println("+++++++++++++++++++");
        Request<ThirdSeasonInfoDTO> request = new Request<>();
        request.setLinkId("ac12b2f6202103021131398837d84589");
        request.setDataSourceTime(1614655900051L);

        String paramStr = "{\"dataSourceCode\":\"SR\",\"endDate\":1622419200000,\"seasonNameList\":[{\"languageType\":\"zs\",\"text\":\"全国篮球联赛 20/21\"},{\"languageType\":\"en\",\"text\":\"LNB 20/21\"}],\"sportId\":2,\"startDate\":1604448000000,\"thirdSeasonId\":\"sr:season:79613\",\"thirdSeasonName\":\"全国篮球联赛 20/21\",\"thirdTournamentId\":\"sr:tournament:1680\",\"year\":\"20/21\"}";

        ThirdSeasonInfoDTO thirdSeasonInfoDTO = JSONObject.parseObject(paramStr,ThirdSeasonInfoDTO.class);
        request.setData(thirdSeasonInfoDTO);
        thirdSeasonInfoProcessor.processSeasonData(request);
    }
}
