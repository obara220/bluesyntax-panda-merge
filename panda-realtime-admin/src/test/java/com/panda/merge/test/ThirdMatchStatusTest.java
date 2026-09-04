package com.panda.merge.test;

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

/**
 * 测试球队球员信息接收处理
 * @author :  tell
 * @since : 2020年9月6日13:48:55
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class ThirdMatchStatusTest {

    @Autowired
    private ThirdMatchStatusProcessor thirdMatchStatusProcessor;

    @Autowired
    ThirdSportPlayerDao thirdSportPlayerDao;
    @Autowired
    IStandardStatusApi iStandardStatusApi;
    @Test
    public void testPutMatchStatus() {
        String str = "{\"linkId\":\"ac12b2a2202010240208226230d5d0595\",\"data\":{\"sportId\":1,\"thirdMatchSourceId\":\"27536302\",\"dataSourceCode\":\"SR\",\"matchStatus\":5},\"dataSourceTime\":1603476502602}";
        Request<Map<String,Object>> parRequest = JSON.parseObject(str, new Request<Map<String,Object>>().getClass());
        Request<ThirdMatchStatusDTO> request = new Request<>();
        request.setLinkId("testPutMatchStatus"+System.currentTimeMillis());
        request.setData(JSON.parseObject(JSON.toJSONString(parRequest.getData()), ThirdMatchStatusDTO.class));
       // request.getData().setMatchStatus(6);
        long beginTime = System.currentTimeMillis();
        thirdMatchStatusProcessor.putMatchStatus(request);
        long endTime = System.currentTimeMillis();
        System.out.println("测试赛事状态接收处理结束,共耗时 ："+(endTime - beginTime));
    }


  @Test
    public void testMatchStatus() {
      Response response = iStandardStatusApi.updataMatchStatus(0L, "4001202012037900891", 0,null);
      System.out.println("response = " + response);
  }



}
