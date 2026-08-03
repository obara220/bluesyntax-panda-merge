//package com.panda.merge;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.baomidou.dynamic.datasource.annotation.DS;
//import com.panda.merge.advertise.service.TennisAdvertiseService;
//import com.panda.merge.api.IScoresCenterApi;
//import com.panda.merge.dto.MatchStatisticsInfoDTO;
//import com.panda.merge.dto.Request;
//import com.panda.merge.dto.Response;
//import com.panda.merge.dto.advertise.MatchAdvertiseQueryDto;
//import com.panda.merge.dto.scores.MatchScoresRequestDTO;
//import com.panda.merge.mapper.MatchEventInfoMapper;
//import com.panda.merge.mapper.MatchScoresInfoMapper;
//import com.panda.merge.model.MatchEventInfo;
//import com.panda.merge.model.MatchEventInfoExample;
//import com.panda.merge.model.MatchScoresInfo;
//import com.panda.merge.model.MatchScoresInfoExample;
//import com.panda.merge.mq.consumer.LiveDataScoresNewConsumer;
//import com.panda.merge.mq.consumer.UOFScoresConsumer;
//import com.panda.merge.mq.producer.ScoresProducer;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//@Slf4j
//@SpringBootTest
//public class LivedataFootballTest {
//
//        @Autowired
//        LiveDataScoresNewConsumer liveDataScoresConsumer;
//        @Autowired
//         MatchEventInfoMapper matchEventInfoMapper;
//        @Autowired
//        IScoresCenterApi iScoresCenterApi;
//
//        @Autowired
//        TennisAdvertiseService tennisAdvertiseService;
//
//        @DS("slave")
//        @Test
//        void testThirdGlobalStatus() {
//            MatchEventInfo data = new MatchEventInfo();
//            data.setExtraInfo("ed1eb5a5-9810-437b-953a-5ce4044cdb05");
//            data.setDataSourceCode("SR");
//            data.setThirdMatchId(1562036858095489025l);
//            data.setSportId(9l);
//            MatchEventInfoExample matchEventInfoExample=new MatchEventInfoExample();
//            matchEventInfoExample.createCriteria().andThirdMatchIdEqualTo(data.getThirdMatchId()).andThirdEventIdEqualTo(data.getExtraInfo()).andDataSourceCodeEqualTo(data.getDataSourceCode()).andSportIdEqualTo(data.getSportId());
//            List<MatchEventInfo> oldMatchInfos =matchEventInfoMapper.selectByExample(matchEventInfoExample);
//            System.out.println(oldMatchInfos);
//        }
//
////        @Transactional
////        @Test
////        void testSyncSettleScores(){
////            String requestData ="{\"linkId\":\"3338396_1010\",\"data\":{\"id\":7023157473382405,\"eventCode\":\"goal\",\"t1\":1,\"t2\":1,\"firstNum\":null,\"secondNum\":null,\"firstT1\":null,\"firstT2\":null,\"secondT1\":null,\"secondT2\":null,\"extryInfo\":null,\"eventName\":\"FT\",\"operateType\":3,\"operater\":\"cptest\",\"userid\":\"418\",\"settleTimes\":1,\"settleCount\":1,\"settleReason\":null,\"settleReasonDetail\":null,\"settleNum\":\"1019\",\"status\":3,\"sportId\":1,\"dataSourceCode\":\"PA\",\"standardMatchId\":3338396,\"periodId\":100,\"settleFreeze\":null,\"goWaterStatus\":0,\"level\":3,\"playCategory\":null,\"modifyTime\":1659323932836,\"createTime\":1659270231574},\"dataSourceTime\":1659323932840,\"dataSourceCode\":null,\"dataType\":null,\"tag\":null,\"operaterId\":null}";
////            String data="{\"id\":7023157473382405,\"eventCode\":\"goal\",\"t1\":1,\"t2\":1,\"firstNum\":null,\"secondNum\":null,\"firstT1\":null,\"firstT2\":null,\"secondT1\":null,\"secondT2\":null,\"extryInfo\":null,\"eventName\":\"FT\",\"operateType\":3,\"operater\":\"cptest\",\"userid\":\"418\",\"settleTimes\":1,\"settleCount\":1,\"settleReason\":null,\"settleReasonDetail\":null,\"settleNum\":\"1019\",\"status\":3,\"sportId\":1,\"dataSourceCode\":\"PA\",\"standardMatchId\":3338396,\"periodId\":100,\"settleFreeze\":null,\"goWaterStatus\":0,\"level\":3,\"playCategory\":null,\"modifyTime\":1659323932836,\"createTime\":1659270231574}";
////            Request<MatchSettleScore>  request = new Request<>();
////            request = JSON.parseObject(requestData, request.getClass());
////            MatchSettleScore dto = JSON.parseObject(data, MatchSettleScore.class);
////            request.setData(dto);
////            matchSettleScoresConsumer.onMessage(request);
////        }
////
////        @Transactional
////        @Test
////        void testSyncSettleEvent(){
////            String requestData ="{\"linkId\":\"3396898_1030\",\"data\":{\"id\":493934101181597,\"standardMatchId\":3396898,\"periodId\":50,\"thirdEventSourceId\":null,\"eventType\":1,\"eventCode\":\"goal\",\"t1\":1,\"t2\":1,\"settleNum\":\"1030\",\"eventOrder\":2,\"eventName\":null,\"status\":3,\"homeAway\":\"home\",\"playerName\":null,\"playerNameCode\":null,\"dataSourceCode\":\"PA\",\"sportId\":1,\"extryInfo\":\"1\",\"firstNum\":1,\"secondNum\":null,\"firstT1\":0,\"firstT2\":0,\"secondT1\":0,\"secondT2\":0,\"operateType\":1,\"operater\":\"cptest\",\"userid\":\"418\",\"settleTimes\":1,\"settleCount\":1,\"settleReason\":null,\"settleReasonDetail\":null,\"settleFreeze\":null,\"goWaterStatus\":0,\"level\":null,\"playCategory\":null,\"isAbnormal\":0,\"isSequenceSettle\":null,\"isGrey\":null,\"isAutoSettle\":null,\"checkNumber\":1,\"modifyTime\":1664449444858,\"createTime\":1664449393410,\"eventTime\":1664449444008},\"dataSourceTime\":1664449444935,\"dataSourceCode\":null,\"dataType\":null,\"tag\":null,\"operaterId\":null}";
////            String data="{\"id\":493934101181597,\"standardMatchId\":3396898,\"periodId\":50,\"thirdEventSourceId\":null,\"eventType\":1,\"eventCode\":\"goal\",\"t1\":1,\"t2\":1,\"settleNum\":\"1030\",\"eventOrder\":2,\"eventName\":null,\"status\":3,\"homeAway\":\"home\",\"playerName\":null,\"playerNameCode\":null,\"dataSourceCode\":\"PA\",\"sportId\":1,\"extryInfo\":\"1\",\"firstNum\":1,\"secondNum\":null,\"firstT1\":0,\"firstT2\":0,\"secondT1\":0,\"secondT2\":0,\"operateType\":1,\"operater\":\"cptest\",\"userid\":\"418\",\"settleTimes\":1,\"settleCount\":1,\"settleReason\":null,\"settleReasonDetail\":null,\"settleFreeze\":null,\"goWaterStatus\":0,\"level\":null,\"playCategory\":null,\"isAbnormal\":0,\"isSequenceSettle\":null,\"isGrey\":null,\"isAutoSettle\":null,\"checkNumber\":1,\"modifyTime\":1664449444858,\"createTime\":1664449393410,\"eventTime\":1664449444008}";
////            Request<MatchSettleEvent>  request = new Request<>();
////            request = JSON.parseObject(requestData, request.getClass());
////            MatchSettleEvent dto = JSON.parseObject(data, MatchSettleEvent.class);
////            request.setData(dto);
////            matchSettleEventConsumer.onMessage(request);
////        }
//
//        @Test
//        void test(){
//            List<MatchScoresRequestDTO> matchIds =new ArrayList<>();
//            MatchScoresRequestDTO matchScoresRequestDTO =new MatchScoresRequestDTO();
//            matchScoresRequestDTO.setAttention(true);
//            matchScoresRequestDTO.setMatchId(1322807029657587713L);
//            matchScoresRequestDTO.setStandard(false);
//            matchIds.add(matchScoresRequestDTO);
//            List x=iScoresCenterApi.searchListMatchScores(matchIds);
//            System.out.println(x);
//        }
//
//        @Test
//        void wholeEventTest(){
//
////          比分中心处理
//            String requestData="{\"data\":[{\"addition1\":\"FIRST_QUARTER\",\"canceled\":0,\"createTime\":1688265830196,\"dataSourceCode\":\"SR\",\"eventCode\":\"match_status\",\"eventTime\":1688265830054,\"extraInfo\":\"13\",\"homeAway\":\"none\",\"id\":1675334435112480770,\"matchPeriodId\":13,\"modifyTime\":1688265830196,\"periodRemainingSeconds\":900,\"secondsFromStart\":900,\"sourceType\":1,\"sportId\":6,\"standardMatchId\":3494704,\"t1\":0,\"t2\":0,\"thirdEventId\":\"2071906958\",\"thirdMatchId\":1675334172574175234,\"thirdMatchSourceId\":\"2622204424800\"}],\"dataSourceCode\":\"SR\",\"dataSourceTime\":1688265830204,\"linkId\":\"0af40f13202307021043501776407703\"}";
//            String req2="{\"data\":[{\"canceled\":0,\"createTime\":1688265840268,\"dataSourceCode\":\"SR\",\"eventCode\":\"touchdown\",\"eventTime\":1688265840122,\"extraInfo\":\"6\",\"homeAway\":\"home\",\"id\":1675334477357510657,\"matchPeriodId\":13,\"modifyTime\":1688265840268,\"periodRemainingSeconds\":274,\"secondsFromStart\":274,\"sourceType\":1,\"sportId\":6,\"standardMatchId\":3494704,\"standardTeamId\":182275,\"t1\":6,\"t2\":0,\"thirdEventId\":\"2015279640\",\"thirdMatchId\":1675334172574175234,\"thirdMatchSourceId\":\"2622204424800\"}],\"dataSourceCode\":\"SR\",\"dataSourceTime\":1688265840281,\"linkId\":\"0af40f13202307021044002526d4a4de\"}";
//            String req3="{\"data\":[{\"addition1\":\"-1\",\"addition2\":\"-1\",\"addition3\":\"-1\",\"addition4\":\"-1\",\"canceled\":0,\"createTime\":1688265850343,\"dataSourceCode\":\"SR\",\"eventCode\":\"extra_point\",\"eventTime\":1688265850199,\"extraInfo\":\"0\",\"homeAway\":\"home\",\"id\":1675334519615123458,\"matchPeriodId\":13,\"modifyTime\":1688265850343,\"periodRemainingSeconds\":274,\"secondsFromStart\":274,\"sourceType\":1,\"sportId\":6,\"standardMatchId\":3494704,\"standardTeamId\":182275,\"t1\":7,\"t2\":0,\"thirdEventId\":\"2866751690\",\"thirdMatchId\":1675334172574175234,\"thirdMatchSourceId\":\"2622204424800\"}],\"dataSourceCode\":\"SR\",\"dataSourceTime\":1688265850355,\"linkId\":\"0af40f1320230702104410328764d03e\"}";
//            String req4="{\"data\":[{\"addition1\":\"FIRST_PAUSE\",\"canceled\":0,\"createTime\":1688265860449,\"dataSourceCode\":\"SR\",\"eventCode\":\"match_status\",\"eventTime\":1688265860274,\"extraInfo\":\"301\",\"homeAway\":\"none\",\"id\":1675334562002759681,\"matchPeriodId\":301,\"modifyTime\":1688265860449,\"periodRemainingSeconds\":0,\"secondsFromStart\":0,\"sourceType\":1,\"sportId\":6,\"standardMatchId\":3494704,\"t1\":7,\"t2\":0,\"thirdEventId\":\"2767281990\",\"thirdMatchId\":1675334172574175234,\"thirdMatchSourceId\":\"2622204424800\"}],\"dataSourceCode\":\"SR\",\"dataSourceTime\":1688265860457,\"linkId\":\"0af40f13202307021044204018d86641\"}";
//            String req5="{\"data\":[{\"addition1\":\"SECOND_QUARTER\",\"canceled\":0,\"createTime\":1688265910953,\"dataSourceCode\":\"SR\",\"eventCode\":\"match_status\",\"eventTime\":1688265910407,\"extraInfo\":\"14\",\"homeAway\":\"none\",\"id\":1675334773831888897,\"matchPeriodId\":14,\"modifyTime\":1688265910953,\"periodRemainingSeconds\":900,\"secondsFromStart\":900,\"sourceType\":1,\"sportId\":6,\"standardMatchId\":3494704,\"t1\":7,\"t2\":0,\"thirdEventId\":\"2143173696\",\"thirdMatchId\":1675334172574175234,\"thirdMatchSourceId\":\"2622204424800\"}],\"dataSourceCode\":\"SR\",\"dataSourceTime\":1688265910967,\"linkId\":\"0af40f1320230702104510562b8527d9\"}";
//            String req6="{\"data\":[{\"canceled\":0,\"createTime\":1688266301903,\"dataSourceCode\":\"SR\",\"eventCode\":\"touchdown\",\"eventTime\":1688266301450,\"extraInfo\":\"6\",\"homeAway\":\"home\",\"id\":1675336413595037698,\"matchPeriodId\":14,\"modifyTime\":1688266301903,\"periodRemainingSeconds\":548,\"secondsFromStart\":548,\"sourceType\":1,\"sportId\":6,\"standardMatchId\":3494704,\"standardTeamId\":182275,\"t1\":13,\"t2\":0,\"thirdEventId\":\"2379525532\",\"thirdMatchId\":1675334172574175234,\"thirdMatchSourceId\":\"2622204424800\"}],\"dataSourceCode\":\"SR\",\"dataSourceTime\":1688266301913,\"linkId\":\"0af40f1320230702105141741f6d3c3e\"}";
//            String req7 = "{\"data\":[{\"addition1\":\"-1\",\"addition2\":\"-1\",\"addition3\":\"-1\",\"addition4\":\"-1\",\"canceled\":0,\"createTime\":1688266327616,\"dataSourceCode\":\"SR\",\"eventCode\":\"extra_point\",\"eventTime\":1688266327509,\"extraInfo\":\"0\",\"homeAway\":\"home\",\"id\":1675336521443176449,\"matchPeriodId\":14,\"modifyTime\":1688266327616,\"periodRemainingSeconds\":548,\"secondsFromStart\":548,\"sourceType\":1,\"sportId\":6,\"standardMatchId\":3494704,\"standardTeamId\":182275,\"t1\":14,\"t2\":0,\"thirdEventId\":\"2573138404\",\"thirdMatchId\":1675334172574175234,\"thirdMatchSourceId\":\"2622204424800\"}],\"dataSourceCode\":\"SR\",\"dataSourceTime\":1688266327624,\"linkId\":\"0af40f132023070210520760800ecded\"}";
//            String req8 = "{\"data\":[{\"addition1\":\"4\",\"addition2\":\"10\",\"addition3\":\"2\",\"addition4\":\"20\",\"canceled\":0,\"createTime\":1688266382560,\"dataSourceCode\":\"SR\",\"eventCode\":\"field_goal\",\"eventTime\":1688266382360,\"extraInfo\":\"0\",\"homeAway\":\"home\",\"id\":1675336751895015425,\"matchPeriodId\":14,\"modifyTime\":1688266382560,\"periodRemainingSeconds\":270,\"secondsFromStart\":270,\"sourceType\":1,\"sportId\":6,\"standardMatchId\":3494704,\"standardTeamId\":182275,\"t1\":17,\"t2\":0,\"thirdEventId\":\"2591520932\",\"thirdMatchId\":1675334172574175234,\"thirdMatchSourceId\":\"2622204424800\"}],\"dataSourceCode\":\"SR\",\"dataSourceTime\":1688266382568,\"linkId\":\"0af40f1320230702105302468d66a222\"}";
////            String del = "{\"data\":[{\"addition1\":\"-1\",\"addition2\":\"-1\",\"addition3\":\"-1\",\"addition4\":\"-1\",\"canceled\":1,\"createTime\":1688266392593,\"dataSourceCode\":\"SR\",\"eventCode\":\"field_goal\",\"eventTime\":1688266392459,\"extraInfo\":\"2591520932\",\"homeAway\":\"home\",\"id\":1675336793976467457,\"matchPeriodId\":14,\"modifyTime\":1688266392593,\"periodRemainingSeconds\":250,\"secondsFromStart\":250,\"sourceType\":1,\"sportId\":6,\"standardMatchId\":3494704,\"standardTeamId\":182275,\"t1\":14,\"t2\":0,\"thirdEventId\":\"2903185815\",\"thirdMatchId\":1675334172574175234,\"thirdMatchSourceId\":\"2622204424800\"}],\"dataSourceCode\":\"SR\",\"dataSourceTime\":1688266392609,\"linkId\":\"0af40f1320230702105312545a0145b7\"}";
//            String ban = "{\"data\":[{\"id\":1882698682798768129,\"sportId\":37,\"canceled\":0,\"dataSourceCode\":\"BG\",\"sourceType\":1,\"eventCode\":\"delivery\",\"eventTime\":1737705319253,\"extraInfo\":\"2\",\"homeAway\":\"home\",\"matchPeriodId\":8,\"matchType\":1,\"playerIdPrefix\":null,\"player1Id\":null,\"player1Name\":null,\"player2Id\":null,\"player2Name\":null,\"secondsFromStart\":0,\"standardMatchId\":3825839,\"standardTeamId\":246405,\"t1\":115,\"t2\":167,\"secondNum\":17,\"firstT1\":115,\"firstT2\":167,\"secondT1\":6,\"secondT2\":0,\"firstNum\":1,\"thirdEventId\":\"BG:11705448:D8sWf\",\"thirdMatchId\":\"1882062860458221570\",\"thirdMatchSourceId\":\"11705448\",\"aoThirdMatchSourceId\":null,\"thirdTeamId\":null,\"remark\":null,\"periodRemainingSeconds\":null,\"penaltyShootoutRound\":null,\"createTime\":1737705319486,\"modifyTime\":1737705319486,\"addition6\":null,\"addition7\":null,\"addition8\":null,\"addition9\":null,\"addition10\":null,\"addition1\":\"16.2\",\"addition2\":null,\"addition3\":null,\"addition4\":null,\"addition5\":null,\"isErrorEndEvent\":0,\"fragmentId\":null,\"fragmentCode\":null,\"fragmentVideo\":null,\"fragmentPic\":null,\"fragmentLength\":null,\"liveEventSource\":0,\"matchLength\":null,\"isReissue\":false}],\"dataSourceCode\":\"BG\",\"dataSourceTime\":1688265840281,\"linkId\":\"0af40f13202307021044002526d4a4de\"}\n";
//            Request request = JSON.parseObject(ban, Request.class);
//            ArrayList<MatchEventInfo> list = JSON.parseObject( request.getData().toString(), ArrayList.class);
//            MatchEventInfo info = JSON.parseObject(String.valueOf(list.get(0)), MatchEventInfo.class);
//            request.setData(Arrays.asList(info));
//            System.err.println("111111111111111");
//            liveDataScoresConsumer.processMessage(request);
//        }
//
//        @Autowired
//        UOFScoresConsumer uofScoresConsumer;
//
//     @Test
//        void UOFScoresConsumerTest(){
//
////          比分中心处理
//        String requestData="{\"data\":{\"dataSourceCode\":\"BT\",\"info\":\"1-0\",\"matchLength\":0,\"matchStatisticsInfoDetailList\":[{\"code\":\"match_score\",\"t1\":1,\"t2\":0},{\"code\":\"corner_score\",\"t1\":1,\"t2\":2},{\"code\":\"dangerous_attack_score\",\"t1\":2,\"t2\":1},{\"code\":\"yellow_card_score\",\"t1\":0,\"t2\":0},{\"code\":\"red_card_score\",\"t1\":0,\"t2\":0},{\"code\":\"shot_off_target_score\",\"t1\":0,\"t2\":0},{\"code\":\"shot_on_target_score\",\"t1\":1,\"t2\":0},{\"code\":\"attacks_score\",\"t1\":3,\"t2\":5},{\"code\":\"set_score\",\"firstNum\":2,\"t1\":1,\"t2\":0}],\"modifyTime\":1677914864870,\"period\":6,\"secondsMatchStart\":744,\"setCount\":2,\"sportId\":\"1\",\"thirdMatchSourceId\":\"133759779\"},\"dataSourceTime\":1677914864949,\"linkId\":\"BT_ac12b2f620230304152744949f1580ea\"}";
//         Request<MatchStatisticsInfoDTO>  request = JSON.parseObject(requestData, Request.class);
//         String dataSTR="{\"dataSourceCode\":\"BT\",\"info\":\"1-0\",\"matchLength\":0,\"matchStatisticsInfoDetailList\":[{\"code\":\"match_score\",\"t1\":1,\"t2\":0},{\"code\":\"corner_score\",\"t1\":1,\"t2\":2},{\"code\":\"dangerous_attack_score\",\"t1\":2,\"t2\":1},{\"code\":\"yellow_card_score\",\"t1\":0,\"t2\":0},{\"code\":\"red_card_score\",\"t1\":0,\"t2\":0},{\"code\":\"shot_off_target_score\",\"t1\":0,\"t2\":0},{\"code\":\"shot_on_target_score\",\"t1\":1,\"t2\":0},{\"code\":\"attacks_score\",\"t1\":3,\"t2\":5},{\"code\":\"set_score\",\"firstNum\":2,\"t1\":1,\"t2\":0}],\"modifyTime\":1677914864870,\"period\":6,\"secondsMatchStart\":744,\"setCount\":2,\"sportId\":\"1\",\"thirdMatchSourceId\":\"133759779\"}";
//         MatchStatisticsInfoDTO data= JSON.parseObject(dataSTR, MatchStatisticsInfoDTO.class);
//         request.setData(data);
//         uofScoresConsumer.processMessage(request);
//    }
//
//    @Test
//    public void getMatchTimeInfoTest() {
//        String paramStr = "{\"thirdMatchId\":\"1639445069337022468\",\"standardMatchId\":3408474}";
//        MatchAdvertiseQueryDto matchAdvertiseQueryDto = JSONObject.parseObject(paramStr, MatchAdvertiseQueryDto.class);
//        matchAdvertiseQueryDto.setOperatorId("10003");
//        matchAdvertiseQueryDto.setOperatorName("jani");
//        matchAdvertiseQueryDto.setLinkedId("PA_9845U9048UG98UDDFIG");
//        Response response =  tennisAdvertiseService.getMatchAdvertiseInfo(matchAdvertiseQueryDto);
//        log.info("------------------>{}", JSON.toJSONString(response));
//    }
//    @Autowired
//    private MatchScoresInfoMapper matchScoresInfoMapper ;
//    @Autowired
//    ScoresProducer scoresProducer;
//    @Test
//    public void testMatchScoreInfoByDB(){
//         Long thirdMatchId = 12332332L;
//        MatchScoresInfoExample matchScoresInfoExample = new MatchScoresInfoExample();
//        matchScoresInfoExample.createCriteria().andThirdMatchIdEqualTo(thirdMatchId);
//        MatchScoresInfo matchScoresInfo  =matchScoresInfoMapper.selectByExample(matchScoresInfoExample).get(0);
//        scoresProducer.sendToMatchManager(null,matchScoresInfo,"test_link");
//
//    }
//
//}
