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
//import com.panda.merge.model.MatchEventInfo;
//import com.panda.merge.model.MatchEventInfoExample;
//import com.panda.merge.model.MatchSettleEvent;
//import com.panda.merge.model.MatchSettleScore;
//import com.panda.merge.mq.consumer.LiveDataScoresNewConsumer;
//import com.panda.merge.mq.consumer.MatchSettleEventConsumer;
//import com.panda.merge.mq.consumer.MatchSettleScoresConsumer;
//import com.panda.merge.mq.consumer.UOFScoresConsumer;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
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
//        @Autowired
//        MatchSettleScoresConsumer matchSettleScoresConsumer;
//        @Autowired
//        MatchSettleEventConsumer matchSettleEventConsumer;
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
//        @Transactional
//        @Test
//        void testSyncSettleScores(){
//            String requestData ="{\"linkId\":\"3338396_1010\",\"data\":{\"id\":7023157473382405,\"eventCode\":\"goal\",\"t1\":1,\"t2\":1,\"firstNum\":null,\"secondNum\":null,\"firstT1\":null,\"firstT2\":null,\"secondT1\":null,\"secondT2\":null,\"extryInfo\":null,\"eventName\":\"FT\",\"operateType\":3,\"operater\":\"cptest\",\"userid\":\"418\",\"settleTimes\":1,\"settleCount\":1,\"settleReason\":null,\"settleReasonDetail\":null,\"settleNum\":\"1019\",\"status\":3,\"sportId\":1,\"dataSourceCode\":\"PA\",\"standardMatchId\":3338396,\"periodId\":100,\"settleFreeze\":null,\"goWaterStatus\":0,\"level\":3,\"playCategory\":null,\"modifyTime\":1659323932836,\"createTime\":1659270231574},\"dataSourceTime\":1659323932840,\"dataSourceCode\":null,\"dataType\":null,\"tag\":null,\"operaterId\":null}";
//            String data="{\"id\":7023157473382405,\"eventCode\":\"goal\",\"t1\":1,\"t2\":1,\"firstNum\":null,\"secondNum\":null,\"firstT1\":null,\"firstT2\":null,\"secondT1\":null,\"secondT2\":null,\"extryInfo\":null,\"eventName\":\"FT\",\"operateType\":3,\"operater\":\"cptest\",\"userid\":\"418\",\"settleTimes\":1,\"settleCount\":1,\"settleReason\":null,\"settleReasonDetail\":null,\"settleNum\":\"1019\",\"status\":3,\"sportId\":1,\"dataSourceCode\":\"PA\",\"standardMatchId\":3338396,\"periodId\":100,\"settleFreeze\":null,\"goWaterStatus\":0,\"level\":3,\"playCategory\":null,\"modifyTime\":1659323932836,\"createTime\":1659270231574}";
//            Request<MatchSettleScore>  request = new Request<>();
//            request = JSON.parseObject(requestData, request.getClass());
//            MatchSettleScore dto = JSON.parseObject(data, MatchSettleScore.class);
//            request.setData(dto);
//            matchSettleScoresConsumer.onMessage(request);
//        }
//
//        @Transactional
//        @Test
//        void testSyncSettleEvent(){
//            String requestData ="{\"linkId\":\"3396898_1030\",\"data\":{\"id\":493934101181597,\"standardMatchId\":3396898,\"periodId\":50,\"thirdEventSourceId\":null,\"eventType\":1,\"eventCode\":\"goal\",\"t1\":1,\"t2\":1,\"settleNum\":\"1030\",\"eventOrder\":2,\"eventName\":null,\"status\":3,\"homeAway\":\"home\",\"playerName\":null,\"playerNameCode\":null,\"dataSourceCode\":\"PA\",\"sportId\":1,\"extryInfo\":\"1\",\"firstNum\":1,\"secondNum\":null,\"firstT1\":0,\"firstT2\":0,\"secondT1\":0,\"secondT2\":0,\"operateType\":1,\"operater\":\"cptest\",\"userid\":\"418\",\"settleTimes\":1,\"settleCount\":1,\"settleReason\":null,\"settleReasonDetail\":null,\"settleFreeze\":null,\"goWaterStatus\":0,\"level\":null,\"playCategory\":null,\"isAbnormal\":0,\"isSequenceSettle\":null,\"isGrey\":null,\"isAutoSettle\":null,\"checkNumber\":1,\"modifyTime\":1664449444858,\"createTime\":1664449393410,\"eventTime\":1664449444008},\"dataSourceTime\":1664449444935,\"dataSourceCode\":null,\"dataType\":null,\"tag\":null,\"operaterId\":null}";
//            String data="{\"id\":493934101181597,\"standardMatchId\":3396898,\"periodId\":50,\"thirdEventSourceId\":null,\"eventType\":1,\"eventCode\":\"goal\",\"t1\":1,\"t2\":1,\"settleNum\":\"1030\",\"eventOrder\":2,\"eventName\":null,\"status\":3,\"homeAway\":\"home\",\"playerName\":null,\"playerNameCode\":null,\"dataSourceCode\":\"PA\",\"sportId\":1,\"extryInfo\":\"1\",\"firstNum\":1,\"secondNum\":null,\"firstT1\":0,\"firstT2\":0,\"secondT1\":0,\"secondT2\":0,\"operateType\":1,\"operater\":\"cptest\",\"userid\":\"418\",\"settleTimes\":1,\"settleCount\":1,\"settleReason\":null,\"settleReasonDetail\":null,\"settleFreeze\":null,\"goWaterStatus\":0,\"level\":null,\"playCategory\":null,\"isAbnormal\":0,\"isSequenceSettle\":null,\"isGrey\":null,\"isAutoSettle\":null,\"checkNumber\":1,\"modifyTime\":1664449444858,\"createTime\":1664449393410,\"eventTime\":1664449444008}";
//            Request<MatchSettleEvent>  request = new Request<>();
//            request = JSON.parseObject(requestData, request.getClass());
//            MatchSettleEvent dto = JSON.parseObject(data, MatchSettleEvent.class);
//            request.setData(dto);
//            matchSettleEventConsumer.onMessage(request);
//        }
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
//            String requestData="{\"linkId\":\"ac12b2f6202302201417018733a4a85f\",\"data\":[{\"id\":1627552890033401857,\"sportId\":4,\"canceled\":0,\"dataSourceCode\":\"SR\",\"sourceType\":1,\"eventCode\":\"goal\",\"eventTime\":1676873822059,\"extraInfo\":\"7\",\"homeAway\":\"away\",\"matchPeriodId\":1,\"matchType\":1,\"playerIdPrefix\":\"sr:player:\",\"player1Id\":null,\"player1Name\":null,\"player2Id\":null,\"player2Name\":null,\"secondsFromStart\":279,\"standardMatchId\":3404330,\"standardTeamId\":182272,\"t1\":0,\"t2\":1,\"secondNum\":null,\"firstT1\":0,\"firstT2\":1,\"secondT1\":null,\"secondT2\":null,\"firstNum\":1,\"thirdEventId\":\"2510761921\",\"thirdMatchId\":\"1627552024274489346\",\"thirdMatchSourceId\":\"1620571577648\",\"aoThirdMatchSourceId\":null,\"thirdTeamId\":null,\"remark\":null,\"periodRemainingSeconds\":921,\"penaltyShootoutRound\":null,\"createTime\":1676873822111,\"modifyTime\":1676873822111,\"addition6\":null,\"addition7\":null,\"addition8\":null,\"addition9\":null,\"addition10\":null,\"addition1\":null,\"addition2\":null,\"addition3\":null,\"addition4\":null,\"addition5\":null,\"isErrorEndEvent\":0}],\"dataSourceTime\":1676873822121,\"dataSourceCode\":\"SR\",\"dataType\":null,\"tag\":null,\"operaterId\":null}";
//            Request request = JSON.parseObject(requestData, Request.class);
//            ArrayList<MatchEventInfo> list = JSON.parseObject( request.getData().toString(), ArrayList.class);
//            MatchEventInfo info = JSON.parseObject(String.valueOf(list.get(0)), MatchEventInfo.class);
//            request.setData(Arrays.asList(info));
//            liveDataScoresConsumer.onMessage(request);
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
//         uofScoresConsumer.onMessage(request);
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
//
//
//}
