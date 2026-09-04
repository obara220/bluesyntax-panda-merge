package com.panda.merge.test;

import com.alibaba.fastjson.JSON;
import com.panda.merge.config.RedisService;
import com.panda.merge.config.ThreadPoolConfig;
import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.rocketmq.processor.MatchEventInfoProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.panda.merge.constant.ConstantSystem.HOUR_1;

/**
 * 测试三方赛事事件信息接收处理
 * @author :  tell
 * @since : 2020年9月6日13:48:55
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ThirdMatchEventTest {

    @Autowired
    private MatchEventInfoProcessor matchEventInfoProcessor;


    @Test
    public void testPutMatchEvent() {
        String str = "{\"linkId\":\"ac12b2f620201127201316523fd25d90\",\"data\":{\"canceled\":0,\"sportId\":2,\"dataSourceCode\":\"SR\",\"eventCode\":\"score_change\",\"eventTime\":1606479196393,\"extrainfo\":\"2\",\"homeAway\":\"home\",\"matchPeriodId\":16,\"player1Id\":701517,\"player2Id\":730491,\"secondsFromStart\":105,\"t1\":60,\"thirdEventId\":\"2995976964\",\"thirdMatchSourceId\":\"24627238\",\"thirdTeamId\":\"701517\",\"sourceType\":\"1\",\"t2\":45,\"firstT1\":14,\"firstT2\":6,\"addition1\":\"-1\",\"secondT1\":27,\"secondT2\":26},\"dataSourceTime\":1606479196793}";
        Request<Map<String,Object>> parRequest = JSON.parseObject(str, new Request<Map<String,Object>>().getClass());
        MatchEventInfoDTO matchEventInfoDTO = (JSON.parseObject(JSON.toJSONString(parRequest.getData()), MatchEventInfoDTO.class));
        Request<MatchEventInfoDTO> request = new Request<>();
        request.setLinkId("testPutMatchEvent"+System.currentTimeMillis());
        request.setData(matchEventInfoDTO);
        long beginTime = System.currentTimeMillis();
        matchEventInfoProcessor.putMatchEventInfo(request);
        long endTime = System.currentTimeMillis();
        System.out.println("测试三方赛事事件接收处理结束,共耗时 ："+(endTime - beginTime));
    }

    @Test
    public void testPutMatchEventList() {
        String str = "{\"data\":[{\"canceled\":0,\"dataSourceCode\":\"BC\",\"eventCode\":\"score_change\",\"eventTime\":1606286119503,\"extrainfo\":\"2\",\"firstT1\":3,\"firstT2\":4,\"homeAway\":\"away\",\"matchPeriodId\":13,\"secondT1\":3,\"secondT2\":4,\"secondsFromStart\":475,\"sourceType\":\"1\",\"sportId\":3,\"t1\":3,\"t2\":4,\"thirdEventId\":\"394408389\",\"thirdMatchSourceId\":\"17241037\",\"thirdTeamId\":\"620742\"},{\"canceled\":0,\"dataSourceCode\":\"BC\",\"eventCode\":\"score_change\",\"eventTime\":1606286119503,\"extrainfo\":\"2\",\"firstT1\":3,\"firstT2\":4,\"homeAway\":\"away\",\"matchPeriodId\":13,\"secondT1\":3,\"secondT2\":4,\"secondsFromStart\":475,\"sourceType\":\"1\",\"sportId\":3,\"t1\":3,\"t2\":4,\"thirdEventId\":\"394408389\",\"thirdMatchSourceId\":\"17241037\",\"thirdTeamId\":\"620742\"}],\"dataSourceTime\":1606286140251,\"linkId\":\"ac12b2a32020112514354025174e0c2a\"}";
        Request<List<Map<String,Object>>> parRequest = JSON.parseObject(str, new Request<Map<String,Object>>().getClass());
        List<MatchEventInfoDTO> list = new LinkedList<>();
        for (Map<String,Object> map: parRequest.getData()) {
            list.add(JSON.parseObject(JSON.toJSONString(map), MatchEventInfoDTO.class));
        }
        Request<List<MatchEventInfoDTO>> request = new Request<>();
        request.setLinkId("testPutMatchEvent"+System.currentTimeMillis());
        request.setData(list);
        long beginTime = System.currentTimeMillis();
//        matchEventInfoProcessor.putMatchEventListInfo(request);
        TaskExecutor eventInfoThreadPool = threadPoolConfig.getEventInfoThreadPool();
        for (int i=0;i<5;i++) {
            eventInfoThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        test();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        try{
            Thread.sleep(HOUR_1);
        }catch (Exception e){
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("测试三方赛事事件接收处理结束,共耗时 ："+(endTime - beginTime));
    }

    @Autowired
    private RedisService redisService;

    @Autowired
    private ThreadPoolConfig threadPoolConfig;

    public void test(){
        //赛事级别分布式锁，避免事件下发顺序错乱
        String matchTryLock = "SR-111111";
        try{
            if(redisService.tryLock(matchTryLock, matchTryLock, 5, 3)){
                log.info("000000000000000000000");
                Thread.sleep(100L);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //释放redis锁
            redisService.unLock(matchTryLock, matchTryLock);
        }
    }

}
