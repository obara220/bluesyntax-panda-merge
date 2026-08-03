package com.panda.merge.service;

import com.alibaba.fastjson.JSON;
import com.panda.merge.api.IStandardDataQueryApi;
import com.panda.merge.api.ITradeMarketConfigApi;
import com.panda.merge.dto.*;
import com.panda.merge.dto.nonrealttime.put.ThirdMatchInfoDTO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/9/12 <br>
 * @see com.panda.merge.service <br>
 */
@Service
public class MathcTestService {
    public void test() {
        String json = "{\"linkId\":\"PN_16746316512344\",\"data\":[{\"thirdMatchSourceId\":\"1485566044\"," +
                "\"sourceTournamentId\":\"493\",\"sportId\":4,\"dataSourceCode\":\"PN\",\"thirdRegionId\":\"unknown\"" +
                ",\"beginTime\":1644530400000,\"matchLength\":0,\"matchStatus\":\"0\",\"matchPeriod\":\"0\"," +
                "\"matchTeamList\":[{\"thirdTeamId\":\"2058243\",\"name\":\"William & Mary\",\"type\":\"Team\"," +
                "\"statium\":\"home\",\"teamNameList\":[{\"languageType\":\"zs\",\"text\":\"William & Mary\"}," +
                "{\"languageType\":\"en\",\"text\":\"William & Mary\"}]," +
                "\"matchTeamRelation\":{\"matchPosition\":\"home\",\"teamNameRecord\":\"William & Mary\"}}," +
                "{\"thirdTeamId\":\"2058244\",\"name\":\"Towson\",\"type\":\"Team\",\"statium\":\"away\"," +
                "\"teamNameList\":[{\"languageType\":\"zs\",\"text\":\"Towson\"},{\"languageType\":\"en\"," +
                "\"text\":\"Towson\"}],\"matchTeamRelation\":{\"matchPosition\":\"away\",\"teamNameRecord\":\"Towson\"}}],\"homeAwayInfo\":\"William & Mary vs Towson\",\"neutralGround\":0,\"liveOddSupport\":1,\"active\":1,\"booked\":0,\"siteType\":0,\"lotteryNumber\":\"\",\"createTime\":1644486997784,\"competitionName\":\"NCAA\",\"status\":0}],\"dataSourceTime\":1644486997784}";
        List<ThirdMatchInfoDTO> data = new ArrayList<>();
        Request<List<Map<String, Object>>> parRequest = JSON.parseObject(json,
                new Request<List<Map<String, Object>>>().getClass());
        for (Map<String, Object> map : parRequest.getData()) {
            data.add(JSON.parseObject(JSON.toJSONString(map), ThirdMatchInfoDTO.class));
        }
        long beginTime = System.currentTimeMillis();
        Request<List<ThirdMatchInfoDTO>> request = new Request<>();
        request.setData(data);
        request.setLinkId(System.currentTimeMillis() + "");
        System.out.println("执行时间 :  " + (System.currentTimeMillis() - beginTime));
    }

}
