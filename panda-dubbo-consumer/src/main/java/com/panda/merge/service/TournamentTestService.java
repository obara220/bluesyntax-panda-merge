package com.panda.merge.service;

import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdSportTournamentDTO;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
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
public class TournamentTestService {
    public void test() {
        String json = "{\"data\":[{\"dataSourceCode\":\"TS\"," +
                "\"logoUrl\":\"group1/M00/02/13/rBKyv172ttaAAzjTAAEpw-_1Erk058.png\"," +
                "\"name\":\"欧洲足球锦标赛\",\"sportId\":1,\"sportRegionId\":\"1535\"," +
                "\"sportRegionName\":\"Busan\",\"thirdSeasonSourceId\":\"232396\"," +
                "\"thirdTournamentSourceId\":\"1\"," +
                "\"tournamentNameList\":[{\"languageType\":\"zs\",\"text\":\"欧洲足球锦标赛\"}," +
                "{\"languageType\":\"en\",\"text\":\"European Championship\"}," +
                "{\"languageType\":\"jc\",\"text\":\"欧洲杯\"}]}]," +
                "\"dataSourceTime\":1602993188255,\"linkId\":\"TS_ac12b42220201018115308255a3bc84c\"}";
        List<ThirdSportTournamentDTO> data = new LinkedList<>();
        Request<List<Map<String, Object>>> parRequest =
                JSON.parseObject(json, new Request<List<Map<String, Object>>>().getClass());
        for (Map<String, Object> map : parRequest.getData()) {
            data.add(JSON.parseObject(JSON.toJSONString(map), ThirdSportTournamentDTO.class));
        }
        Request<List<ThirdSportTournamentDTO>> request = new Request<>();
        request.setData(data);
        request.setLinkId("tetsTournament" + System.currentTimeMillis());
        long beginTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis();
        System.out.println("测试联赛信息接收处理结束,共耗时 ：" + (endTime - beginTime));

    }

}
