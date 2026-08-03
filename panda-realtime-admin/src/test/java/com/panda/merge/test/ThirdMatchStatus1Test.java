package com.panda.merge.test;//package com.panda.merge.test;

import com.alibaba.fastjson.JSON;
import com.panda.merge.api.IStandardStatusApi;
import com.panda.merge.common.enums.MatchStatusEnum;
import com.panda.merge.dao.ThirdSportPlayerDao;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ThirdMatchStatusDTO;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.rocketmq.processor.ThirdMatchStatusProcessor;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ThirdMatchStatus1Test {

    @Autowired
    private ThirdMatchStatusProcessor thirdMatchStatusProcessor;

    @Autowired
    ThirdSportPlayerDao thirdSportPlayerDao;
    @Autowired
    IStandardStatusApi iStandardStatusApi;
    @Test
    public void testPutMatchStatus() {
        String str = "{\"linkId\":\"ac12b2a2202010240208226230d5d0595\",\"data\":{\"sportId\":10,\"thirdMatchSourceId\":\"10567899\",\"dataSourceCode\":\"BG\",\"matchStatus\":1},\"dataSourceTime\":1603476502602}";
        Request<Map<String,Object>> parRequest = JSON.parseObject(str, new Request<Map<String,Object>>().getClass());
        Request<ThirdMatchStatusDTO> request = new Request<>();
        request.setLinkId("testPutMatchStatus" + System.currentTimeMillis());
        request.setData(JSON.parseObject(JSON.toJSONString(parRequest.getData()), ThirdMatchStatusDTO.class));
        long beginTime = System.currentTimeMillis();
        thirdMatchStatusProcessor.putMatchStatus(request);
        long endTime = System.currentTimeMillis();
        System.out.println("测试赛事状态接收处理结束,共耗时 ："+(endTime - beginTime));
    }

}
