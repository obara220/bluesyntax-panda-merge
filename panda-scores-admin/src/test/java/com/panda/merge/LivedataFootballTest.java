package com.panda.merge;

import com.alibaba.fastjson.JSON;
import com.panda.merge.api.IScoresCenterApi;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.scores.MatchScoresRequestDTO;
import com.panda.merge.mapper.MatchEventInfoMapper;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.mq.consumer.LiveDataScoresNewConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class LivedataFootballTest {

        @Autowired
        LiveDataScoresNewConsumer liveDataScoresConsumer;
        @Autowired
         MatchEventInfoMapper matchEventInfoMapper;
        @Autowired
        IScoresCenterApi iScoresCenterApi;

        @Test
        void testThirdGlobalStatus() {
            String requestData ="{\"linkId\":\"SR_ac12b2f620220301160316906e7a0e9f\",\"data\":{\"canceled\":0,\"sportId\":1,\"dataSourceCode\":\"SR\",\"eventCode\":\"corner\",\"eventTime\":1606896510506,\"extrainfo\":\"0\",\"homeAway\":\"away\",\"matchPeriodId\":7,\"player1Id\":760092,\"player2Id\":760184,\"secondsFromStart\":418,\"t1\":0,\"thirdEventId\":\"3009231876\",\"thirdMatchSourceId\":\"24703278\",\"thirdTeamId\":\"760184\",\"sourceType\":\"1\",\"t2\":2},\"dataSourceTime\":1606896511018}";
            String data="{\"canceled\":0,\"sportId\":1,\"dataSourceCode\":\"SR\",\"eventCode\":\"corner\",\"eventTime\":1606896510506,\"extrainfo\":\"0\",\"homeAway\":\"away\",\"matchPeriodId\":6,\"player1Id\":760092,\"player2Id\":760184,\"secondsFromStart\":418,\"t1\":5,\"thirdEventId\":\"3009231876\",\"thirdMatchSourceId\":\"24703278\",\"thirdTeamId\":\"760184\",\"sourceType\":\"1\",\"t2\":10}";
            Request<MatchEventInfo>  request = new Request<>();
            request = JSON.parseObject(requestData, request.getClass());
            MatchEventInfo dto = JSON.parseObject(data, MatchEventInfo.class);
            request.setData(dto);
            //liveDataScoresConsumer.onMessage(request);
        }

        @Test
        void test(){
            List<MatchScoresRequestDTO> matchIds =new ArrayList<>();
            MatchScoresRequestDTO matchScoresRequestDTO =new MatchScoresRequestDTO();
            matchScoresRequestDTO.setAttention(true);
            matchScoresRequestDTO.setMatchId(1322807029657587713L);
            matchScoresRequestDTO.setStandard(false);
            matchIds.add(matchScoresRequestDTO);
            List x=iScoresCenterApi.searchListMatchScores(matchIds);
            System.out.println(x);
        }

        @Test
        void wholeEventTest(){
//            Long thirdMathId =1368662031617576961L;
//            List<String> list=new ArrayList<>();
//            list.add("yellow_card");
//            list.add("red_card");
//            MatchEventInfoExample matchEventInfoExample =new MatchEventInfoExample();
//            matchEventInfoExample.createCriteria().andSourceTypeEqualTo(1)
//            .andDataSourceCodeEqualTo("SR").andThirdMatchIdEqualTo(1490993104144125954l)
//           .andEventCodeIn(list);
//            matchEventInfoExample.setOrderByClause("'event_time' desc");
//            List<MatchEventInfo> eventInfos = matchEventInfoMapper.selectByExample(matchEventInfoExample);

            MatchEventInfo matchEventInfo  =matchEventInfoMapper.selectByPrimaryKey(1514169360559763458l);

             List<MatchEventInfo> eventInfos =new ArrayList<>();
             eventInfos.add(matchEventInfo);

            for (MatchEventInfo eventInfo : eventInfos) {
                String requestData ="{\"linkId\":\"ac12b2f62020120216083089379701c2\",\"data\":{\"canceled\":0,\"sportId\":1,\"dataSourceCode\":\"SR\",\"eventCode\":\"corner\",\"eventTime\":1606896510506,\"extrainfo\":\"0\",\"homeAway\":\"away\",\"matchPeriodId\":7,\"player1Id\":760092,\"player2Id\":760184,\"secondsFromStart\":418,\"t1\":0,\"thirdEventId\":\"3009231876\",\"thirdMatchSourceId\":\"24703278\",\"thirdTeamId\":\"760184\",\"sourceType\":\"1\",\"t2\":2},\"dataSourceTime\":1606896511018}";
//                String data="{\"canceled\":0,\"sportId\":1,\"dataSourceCode\":\"SR\",\"eventCode\":\"corner\",\"eventTime\":1606896510506,\"extrainfo\":\"0\",\"homeAway\":\"away\",\"matchPeriodId\":6,\"player1Id\":760092,\"player2Id\":760184,\"secondsFromStart\":418,\"t1\":5,\"thirdEventId\":\"3009231876\",\"thirdMatchSourceId\":\"24703278\",\"thirdTeamId\":\"760184\",\"sourceType\":\"1\",\"t2\":10}";
                Request<MatchEventInfo>  request = new Request<>();
                request = JSON.parseObject(requestData, request.getClass());
//                MatchEventInfo dto = new MatchEventInfo();
//                BeanUtils.copyProperties(eventInfo,dto);
                request.setData(eventInfo);
                //liveDataScoresConsumer.onMessage(request);
            }

        }

        public static void main(String[] xx){
            Long x=new Long(50);
            if(x.longValue()==50l){
                System.out.println(111);
            }
        }


}
