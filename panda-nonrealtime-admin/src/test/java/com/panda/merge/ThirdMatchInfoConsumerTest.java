package com.panda.merge;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.nonrealttime.put.ThirdMatchInfoDTO;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.rocketmq.processor.ThirdMatchInfoProcessor;
import com.panda.merge.service.ThirdMatchInfoService;
import com.panda.merge.service.VirtualBatchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class ThirdMatchInfoConsumerTest {


    @Autowired
    ThirdMatchInfoProcessor thirdMatchInfoProcessor;
    @Autowired
    VirtualBatchService virtualBatchService;
    @Autowired
    ThirdMatchInfoService thirdMatchInfoService;

    @Test
    void testThirdGlobalStatusss() {
        String requestData ="{\"linkId\":\"GR_21bf83d5820201107141419411424\",\"data\":[{\"thirdMatchSourceId\":\"11101_22146\",\"sourceTournamentId\":\"gr:tournament:11101\",\"sportId\":1001,\"dataSourceCode\":\"GR\",\"thirdRegionId\":\"0\",\"beginTime\":1604745900000,\"matchLength\":0,\"matchStatus\":\"0\",\"matchPeriod\":\"0\",\"matchTeamList\":[{\"thirdTeamId\":\"783\",\"name\":\"georgia\",\"type\":1,\"teamNameList\":[{\"languageType\":\"en\",\"text\":\"georgia\"},{\"languageType\":\"zs\",\"text\":\"georgia\"}],\"matchTeamRelation\":{\"matchPosition\":\"home\",\"teamNameRecord\":\"georgia\"}},{\"thirdTeamId\":\"9\",\"name\":\"croatia\",\"type\":1,\"teamNameList\":[{\"languageType\":\"en\",\"text\":\"croatia\"},{\"languageType\":\"zs\",\"text\":\"croatia\"}],\"matchTeamRelation\":{\"matchPosition\":\"away\",\"teamNameRecord\":\"croatia\"}}],\"homeAwayInfo\":\"georgia VS croatia \",\"neutralGround\":0,\"liveOddSupport\":0,\"active\":1,\"booked\":1,\"createTime\":1604729659411}],\"dataSourceTime\":1604729659411}";
        String data="[{\"thirdMatchSourceId\":\"11101_22146\",\"sourceTournamentId\":\"gr:tournament:11101\",\"sportId\":1001,\"dataSourceCode\":\"GR\",\"thirdRegionId\":\"0\",\"beginTime\":1604745900000,\"matchLength\":0,\"matchStatus\":\"0\",\"matchPeriod\":\"0\",\"matchTeamList\":[{\"thirdTeamId\":\"783\",\"name\":\"georgia\",\"type\":1,\"teamNameList\":[{\"languageType\":\"en\",\"text\":\"georgia\"},{\"languageType\":\"zs\",\"text\":\"georgia\"}],\"matchTeamRelation\":{\"matchPosition\":\"home\",\"teamNameRecord\":\"georgia\"}},{\"thirdTeamId\":\"9\",\"name\":\"croatia\",\"type\":1,\"teamNameList\":[{\"languageType\":\"en\",\"text\":\"croatia\"},{\"languageType\":\"zs\",\"text\":\"croatia\"}],\"matchTeamRelation\":{\"matchPosition\":\"away\",\"teamNameRecord\":\"croatia\"}}],\"homeAwayInfo\":\"georgia VS croatia \",\"neutralGround\":0,\"liveOddSupport\":0,\"active\":1,\"booked\":1,\"createTime\":1604729659411}]";
        Request<List<ThirdMatchInfoDTO>> request = new Request<>();
        request = JSON.parseObject(requestData, request.getClass());

        JSONArray dto = JSONArray.parseArray(data);
        List<ThirdMatchInfoDTO>   list= new ArrayList<>();
        for (Object o : dto.toArray()) {
            ThirdMatchInfoDTO dto1= JSON.parseObject(o.toString(),ThirdMatchInfoDTO.class);
            list.add(dto1);
        }
        request.setData(list);
        thirdMatchInfoProcessor.processMatchData(request);
    }
//
//    @Test
//    void xx(){
//        Long thirdId=  1325033579925819395l;
//        String batchNo="51246";
//        ThirdMatchInfoDTO thirdMatchInfoDTO=new ThirdMatchInfoDTO();
//        ThirdMatchInfo thirdMatchInfo= thirdMatchInfoService.getItem(thirdId);
//        BeanUtils.copyProperties(thirdMatchInfo,thirdMatchInfoDTO);
//        thirdMatchInfoDTO.setBatchNo(batchNo);
//        virtualBatchService.setBatchNo(thirdMatchInfoDTO,thirdMatchInfo.getTournamentId());
//    }
    @Test
    void xx(){

    String data="{\"linkId\":\"GR_ac5ede95020201110200244547248scC1\",\"data\":[{\"thirdMatchSourceId\":\"11101_23014\",\"sourceTournamentId\":\"gr:tournament:11101\",\"sportId\":1001,\"dataSourceCode\":\"GR\",\"thirdRegionId\":\"0\",\"beginTime\":1605006300000,\"matchLength\":0,\"matchStatus\":\"0\",\"matchPeriod\":\"0\",\"matchTeamList\":[{\"thirdTeamId\":\"12\",\"name\":\"russia\",\"type\":1,\"teamNameList\":[{\"languageType\":\"en\",\"text\":\"russia\"},{\"languageType\":\"zs\",\"text\":\"russia\"}],\"matchTeamRelation\":{\"matchPosition\":\"home\",\"teamNameRecord\":\"russia\"}},{\"thirdTeamId\":\"113\",\"name\":\"belgium\",\"type\":1,\"teamNameList\":[{\"languageType\":\"en\",\"text\":\"belgium\"},{\"languageType\":\"zs\",\"text\":\"belgium\"}],\"matchTeamRelation\":{\"matchPosition\":\"away\",\"teamNameRecord\":\"belgium\"}}],\"homeAwayInfo\":\"russia VS belgium \",\"neutralGround\":0,\"liveOddSupport\":0,\"active\":1,\"booked\":1,\"createTime\":1605009764546,\"batchNo\":\"23014\"}],\"dataSourceTime\":1605009764546}";
    Request<JSONArray> request = new Request<>();


        request= JSON.parseObject(data, request.getClass());
        if (request.getData() == null) {
            return;
        }
       List<ThirdMatchInfoDTO> list = JSONArray.parseArray(request.getData().toString(), ThirdMatchInfoDTO.class);
        Request< List<ThirdMatchInfoDTO> > request2 = new Request<>();
        request2.setData(list);
        request2.setDataSourceTime(request.getDataSourceTime());
        request2.setLinkId(request.getLinkId());
        request2.setDataSourceCode(request.getDataSourceCode());
        thirdMatchInfoProcessor.processMatchData(request2);

    }

//    @Autowired
//    ThirdMatchInfoProcessor thirdMatchInfoProcessor;
}
