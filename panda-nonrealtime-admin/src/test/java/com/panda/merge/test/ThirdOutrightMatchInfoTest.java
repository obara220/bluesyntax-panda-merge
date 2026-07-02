package com.panda.merge.test;


import com.alibaba.fastjson.JSON;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdOutrightMatchInfoDTO;
import com.panda.merge.dto.message.ChangeMatchOverMessage;
import com.panda.merge.dto.nonrealttime.put.ThirdMatchInfoDTO;
import com.panda.merge.rocketmq.consumer.ThirdChangeMatchOverConsumer;
import com.panda.merge.rocketmq.consumer.ThirdMatchInfoConsumer;
import com.panda.merge.rocketmq.consumer.ThirdOutrightMatchInfoConsumer;
import com.panda.merge.rocketmq.processor.ThirdMatchInfoProcessor;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 测试三方冠军赛事信息接收处理
 * @author :  tell
 * @since : 2020年9月6日13:48:55
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ThirdOutrightMatchInfoTest {

    @Autowired
    RedisService redisService;

    @Autowired
    ThirdMatchInfoProcessor thirdMatchInfoProcessor;

    @Autowired
    ThirdOutrightMatchInfoConsumer thirdMatchInfoConsumer;
    @Autowired
    ThirdChangeMatchOverConsumer thirdChangeMatchOverConsumer;

    @Test
    public void testPutThirdMatchInfo() {
//        String json = "{\"linkId\":\"ac12b2f7202008271549468174595e22match1\",\"data\":[{\"active\":1,\"beginTime\":1598515500000,\"booked\":1,\"createTime\":1598514586850,\"dataSourceCode\":\"TS\",\"homeAwayInfo\":\"Michael Geerts vs. Michael Geerts\",\"liveOddSupport\":0,\"MATCH_PERIOD\":\"0\",\"matchStatus\":\"0\",\"matchTeamList\":[{\"matchTeamRelation\":{\"matchPosition\":\"home\",\"teamNameRecord\":\"Michael Geerts\"},\"name\":\"TS测试主队名称\",\"statium\":\"home\",\"teamNameList\":[{\"languageType\":\"en\",\"text\":\"Michael Geerts\"},{\"languageType\":\"ko\",\"text\":\"마이클 게르츠\"},{\"languageType\":\"ru\",\"text\":\"Майкл Гиртс\"},{\"languageType\":\"zh\",\"text\":\"邁克爾•吉爾茨\"},{\"languageType\":\"zs\",\"text\":\"TS测试主队名称\"}],\"thirdTeamId\":\"-3\"},{\"matchTeamRelation\":{\"matchPosition\":\"away\",\"teamNameRecord\":\"Robin Stanek\"},\"name\":\"TS测试客队名称\",\"statium\":\"away\",\"teamNameList\":[{\"languageType\":\"en\",\"text\":\"Robin Stanek\"},{\"languageType\":\"fr\",\"text\":\"Robin Stanek \"},{\"languageType\":\"ru\",\"text\":\"Робин Штанек\"},{\"languageType\":\"zh\",\"text\":\"羅賓•斯坦尼克\"},{\"languageType\":\"zs\",\"text\":\"TS测试客队名称\"}],\"thirdTeamId\":\"-2\"}],\"neutralGround\":0,\"roundType\":3,\"sourceTournamentId\":\"-1\",\"sportId\":1,\"thirdMatchSourceId\":\"-1\",\"thirdRegionId\":\"-1\"}]}";
        String json = "{\"data\":{\"linkId\":\"ZV0g8smXpdM2X6ZGmhy2w6YeWffGZrkM_match_over\",\"matchId\":1823335,\"matchOver\":0,\"sportId\":1},\"linkId\":\"ZV0g8smXpdM2X6ZGmhy2w6YeWffGZrkM_match_over\"}";
        List<ThirdOutrightMatchInfoDTO> data = new ArrayList<>();
        Request<List<Map<String, Object>>> parRequest = JSON.parseObject(json, new Request<List<Map<String, Object>>>().getClass());
        for (Map<String, Object> map : parRequest.getData()) {
            data.add(JSON.parseObject(JSON.toJSONString(map), ThirdOutrightMatchInfoDTO.class));
        }
        Request<List<ThirdOutrightMatchInfoDTO>> request = new Request<>();
        request.setData(data);
        request.setLinkId("tetsMatch"+System.currentTimeMillis());
        long beginTime = System.currentTimeMillis();
//        Response response = thirdMatchInfoProcessor.processMatchData(request);
        thirdMatchInfoConsumer.onMessage(request);
//        //如果是唯一主键冲突错误 则重新推送
//        if(ResultCode.DUPLICATE_KEY.getCode() == response.getCode()){
//            thirdMatchInfoProcessor.delCacheLinkId(THIRD_MATCH_INFO_API,request.getLinkId());
//            System.out.println("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_MATCH_INFO_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】第三方赛事数据重新推送");
//            throw new ApiException("第三方赛事数据唯一主键冲突,重新推送!");
//        }
//        long endTime = System.currentTimeMillis();
//        System.out.println("测试联赛信息接收处理结束,共耗时 ：" + (endTime - beginTime) + ", response: " + response);
    }



    @Test
    public void testThirdChangeMatchOverConsumer() {
//        String json = "{\"linkId\":\"ac12b2f7202008271549468174595e22match1\",\"data\":[{\"active\":1,\"beginTime\":1598515500000,\"booked\":1,\"createTime\":1598514586850,\"dataSourceCode\":\"TS\",\"homeAwayInfo\":\"Michael Geerts vs. Michael Geerts\",\"liveOddSupport\":0,\"MATCH_PERIOD\":\"0\",\"matchStatus\":\"0\",\"matchTeamList\":[{\"matchTeamRelation\":{\"matchPosition\":\"home\",\"teamNameRecord\":\"Michael Geerts\"},\"name\":\"TS测试主队名称\",\"statium\":\"home\",\"teamNameList\":[{\"languageType\":\"en\",\"text\":\"Michael Geerts\"},{\"languageType\":\"ko\",\"text\":\"마이클 게르츠\"},{\"languageType\":\"ru\",\"text\":\"Майкл Гиртс\"},{\"languageType\":\"zh\",\"text\":\"邁克爾•吉爾茨\"},{\"languageType\":\"zs\",\"text\":\"TS测试主队名称\"}],\"thirdTeamId\":\"-3\"},{\"matchTeamRelation\":{\"matchPosition\":\"away\",\"teamNameRecord\":\"Robin Stanek\"},\"name\":\"TS测试客队名称\",\"statium\":\"away\",\"teamNameList\":[{\"languageType\":\"en\",\"text\":\"Robin Stanek\"},{\"languageType\":\"fr\",\"text\":\"Robin Stanek \"},{\"languageType\":\"ru\",\"text\":\"Робин Штанек\"},{\"languageType\":\"zh\",\"text\":\"羅賓•斯坦尼克\"},{\"languageType\":\"zs\",\"text\":\"TS测试客队名称\"}],\"thirdTeamId\":\"-2\"}],\"neutralGround\":0,\"roundType\":3,\"sourceTournamentId\":\"-1\",\"sportId\":1,\"thirdMatchSourceId\":\"-1\",\"thirdRegionId\":\"-1\"}]}";
        String json = "{\"linkId\":\"ZV0g8smXpdM2X6ZGmhy2w6YeWffGZrkM_match_over\",\"matchId\":1823335,\"matchOver\":0,\"sportId\":1}";
        ChangeMatchOverMessage changeMatchOverMessage =new ChangeMatchOverMessage();
        changeMatchOverMessage = JSON.parseObject(json, new ChangeMatchOverMessage().getClass());

        Request<ChangeMatchOverMessage> request = new Request<>();
        request.setData(changeMatchOverMessage);
        request.setLinkId("tetsMatch"+System.currentTimeMillis());
        long beginTime = System.currentTimeMillis();
//        Response response = thirdMatchInfoProcessor.processMatchData(request);
        thirdChangeMatchOverConsumer.onMessage(request);
//        //如果是唯一主键冲突错误 则重新推送
//        if(ResultCode.DUPLICATE_KEY.getCode() == response.getCode()){
//            thirdMatchInfoProcessor.delCacheLinkId(THIRD_MATCH_INFO_API,request.getLinkId());
//            System.out.println("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_MATCH_INFO_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】第三方赛事数据重新推送");
//            throw new ApiException("第三方赛事数据唯一主键冲突,重新推送!");
//        }
//        long endTime = System.currentTimeMillis();
//        System.out.println("测试联赛信息接收处理结束,共耗时 ：" + (endTime - beginTime) + ", response: " + response);
    }

}
