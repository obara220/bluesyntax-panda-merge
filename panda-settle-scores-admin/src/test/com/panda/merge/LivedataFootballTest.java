package com.panda.merge;

import com.alibaba.fastjson.JSON;
import com.panda.merge.api.IFootballMatchScoresSettleApi;
import com.panda.merge.api.IFootballNewMatchScoresSettleApi;
import com.panda.merge.api.ISettleCenterApi;
import com.panda.merge.dto.CommonThirdScoresDto;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.MatchSettleSwitcherDto;
import com.panda.merge.dto.message.StandardMarketResultMessage;
import com.panda.merge.dto.settle.*;
import com.panda.merge.mapper.MatchEventInfoMapper;
import com.panda.merge.mapper.MatchSettleEventMapper;
import com.panda.merge.mapper.MatchSettleScoreMapper;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.mq.consumer.MatchSettleRollBackSuccessConsumer;
import com.panda.merge.mq.consumer.StandardMarketResultConsumer;
import com.panda.merge.mq.consumer.StandardMatchEventConsumer;
import com.panda.merge.mq.consumer.StandardMatchScoreConsumer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@SpringBootTest
@Slf4j
public class LivedataFootballTest {

        @Autowired
        IFootballMatchScoresSettleApi footballMatchScoresSettleApi;
        @Autowired
        ISettleCenterApi settleCenterApi;
        @Autowired
        MatchSettleScoreMapper matchSettleScoreMapper;
        @Autowired
        MatchSettleEventMapper matchSettleEventMapper;

        @Autowired
        MatchSettleRollBackSuccessConsumer matchSettleRollBackSuccessConsumer;


        /**
         * 切换生成初始化数据x
         * */
        @Test
        void testThirdGlobalStatus() {
                MatchSettleSwitcherDto matchSettleSwitcherDto =new MatchSettleSwitcherDto();
                matchSettleSwitcherDto.setSportId(1l);
                matchSettleSwitcherDto.setLinkId(UUID.randomUUID().toString());
                matchSettleSwitcherDto.setMatchId(3135511L);
                matchSettleSwitcherDto.setSettleType(2);
                settleCenterApi.settleSwitcher(matchSettleSwitcherDto);
        }
        /**
         * 查询比分
         * */
        @Test
        void testThirdGlobalStatus2() {

                MatchSettleScoreSearchDto matchSettleScoreSearchDto =new MatchSettleScoreSearchDto();
                matchSettleScoreSearchDto.setEventCode("goal");
                matchSettleScoreSearchDto.setStandardMatchId(3094535L);
                matchSettleScoreSearchDto.setSportId(1l);
                List l =footballMatchScoresSettleApi.searchMatchSettleScores(matchSettleScoreSearchDto);
                log.info("{}",l);
        }

        /**
         * 查询比分
         * */
        @Test
        void testThirdGlobalStatus222() {

                AddMatchSettleEventDto matchSettleScoreSearchDto =new AddMatchSettleEventDto();
                matchSettleScoreSearchDto.setEventCode("goal");
                matchSettleScoreSearchDto.setStandardMatchId(3094535l);
                matchSettleScoreSearchDto.setSettleNum("1022");
                matchSettleScoreSearchDto.setPeriodId(6l);
                matchSettleScoreSearchDto.setSportId(1l);
                Response l =footballMatchScoresSettleApi.addMatchSettleEvent(matchSettleScoreSearchDto);
                log.info("{}",l);
        }

        /**
         * 查询比分
         * */
        @Test
        void testThirdGlobalStatus22() {

                MatchSettleScoreSearchDto matchSettleScoreSearchDto =new MatchSettleScoreSearchDto();
                matchSettleScoreSearchDto.setEventCode("goal");
                matchSettleScoreSearchDto.setStandardMatchId(3094535L);
                matchSettleScoreSearchDto.setSportId(1l);
                Response l =footballMatchScoresSettleApi.searchPenaltyScores(matchSettleScoreSearchDto);
                log.info("{}",l);
        }
        /**
         * 查询事件
         * */
        @Test
        void testThirdGlobalStatus3() {
                MatchSettleScoreSearchDto matchSettleScoreSearchDto =new MatchSettleScoreSearchDto();
                matchSettleScoreSearchDto.setEventCode("goal");
                matchSettleScoreSearchDto.setStandardMatchId(3094535L);
                matchSettleScoreSearchDto.setSportId(1l);
                List l =footballMatchScoresSettleApi.searchMatchSettleEvent(matchSettleScoreSearchDto);
                log.info("{}",l);
        }

        /**
         * 结算比分
         * */
        @Test
        void testThirdGlobalStatus4() {
//                MatchSettleScoreExample example =new MatchSettleScoreExample();
//                example.createCriteria().andStandardMatchIdEqualTo(3090275L);
//                List<MatchSettleScore> list =matchSettleScoreMapper.selectByExample(example);
//                for (MatchSettleScore matchSettleScore : list) {
                        SettleMatchScoreDto settleMatchScoreDto =new SettleMatchScoreDto();
                        settleMatchScoreDto.setEventCode("corner");
                        settleMatchScoreDto.setMatchScoreId(1499624463525220366L);
                        settleMatchScoreDto.setStandardMatchId(3110317L);
                        settleMatchScoreDto.setSportId(1l);
                        footballMatchScoresSettleApi.settleMatchScore(settleMatchScoreDto);


//                SettleMatchScoreDto settleMatchScoreDto2 =new SettleMatchScoreDto();
//                settleMatchScoreDto2.setEventCode("corner");
//                settleMatchScoreDto2.setMatchScoreId(1499624463525220362L);
//                settleMatchScoreDto2.setStandardMatchId(3110317L);
//                settleMatchScoreDto2.setSportId(1l);
//                footballMatchScoresSettleApi.settleMatchScore(settleMatchScoreDto2);


//                1499624463525220370
//                }

//                MatchSettleEventExample example2 =new MatchSettleEventExample();
//                example2.createCriteria().andStandardMatchIdEqualTo(3094535L);
//                List<MatchSettleEvent> list2 =matchSettleEventMapper.selectByExample(example2);
//                for (MatchSettleEvent matchSettleScore : list2) {
//                        EditMatchSettleEventDto settleMatchScoreDto =new EditMatchSettleEventDto();
//                        settleMatchScoreDto.setEventCode("goal");
//                        settleMatchScoreDto.setEventId(1499624718429851649l);
//                        settleMatchScoreDto.setStandardMatchId(3110317L);
//                        settleMatchScoreDto.setSportId(1l);
//                        footballMatchScoresSettleApi.settleMatchSettleEvent(settleMatchScoreDto);
//
//                EditMatchSettleEventDto settleMatchScoreDto2 =new EditMatchSettleEventDto();
//                settleMatchScoreDto2.setEventCode("goal");
//                settleMatchScoreDto2.setEventId(1499624718429851651l);
//                settleMatchScoreDto2.setStandardMatchId(3110317L);
//                settleMatchScoreDto2.setSportId(1l);
//                footballMatchScoresSettleApi.settleMatchSettleEvent(settleMatchScoreDto2);
//                }
        }
        /**
         * 结算比分
         * */
        @Test
        void testThirdGlobalStatus41() {
                ConfirmMatchSettleScoreDto settleMatchScoreDto =new ConfirmMatchSettleScoreDto();
                settleMatchScoreDto.setEventCode("goal");
                settleMatchScoreDto.setMatchScoreId(1495396029265612803l);
                settleMatchScoreDto.setStandardMatchId(3090275L);
                settleMatchScoreDto.setSportId(1l);
                footballMatchScoresSettleApi.confirmMatchSettleScore(settleMatchScoreDto);
        }

        /**
         * 比分测试
         * */
        @Test
        void testThirdGlobalStatus5() {
//                JSONObject jsonObject =JSONObject.parseObject("{\"linkId\":\"ac12b2f620220318185214886b540d2a\",\"data\":{\"linkedId\":\"ac12b2f620220318185214886b540d2a\",\"standardMatchId\":3135511,\"periodId\":999,\"secondNum\":null,\"sportId\":1,\"dataSourceCode\":\"SR\",\"scores\":{\"60899\":{\"goal\":{\"away\":0,\"home\":1},\"corner\":{\"away\":1,\"home\":2}},\"-1\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":1,\"home\":2},\"shotOff\":{\"away\":2,\"home\":2},\"substitution\":{\"away\":0,\"home\":0},\"kickOff\":{\"away\":0,\"home\":0},\"penaltyAwarded\":{\"away\":0,\"home\":0},\"possession\":{\"away\":49,\"home\":50},\"dangerousAttack\":{\"away\":63,\"home\":48},\"yellowCard\":{\"away\":1,\"home\":2},\"freeKickScore\":{\"away\":21,\"home\":17},\"faCard\":{\"away\":1,\"home\":2},\"shot\":{\"away\":5,\"home\":4},\"shotOn\":{\"away\":3,\"home\":2},\"offside\":{\"away\":2,\"home\":0},\"corner\":{\"away\":7,\"home\":4},\"attack\":{\"away\":130,\"home\":114}},\"6\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":1},\"shotOff\":{\"away\":2,\"home\":1},\"substitution\":{\"away\":0,\"home\":0},\"kickOff\":{\"away\":0,\"home\":0},\"penaltyAwarded\":{\"away\":0,\"home\":0},\"possession\":{\"away\":50,\"home\":50},\"dangerousAttack\":{\"away\":33,\"home\":14},\"yellowCard\":{\"away\":0,\"home\":0},\"freeKickScore\":{\"away\":9,\"home\":11},\"faCard\":{\"away\":0,\"home\":0},\"shot\":{\"away\":3,\"home\":2},\"shotOn\":{\"away\":1,\"home\":1},\"offside\":{\"away\":1,\"home\":0},\"corner\":{\"away\":2,\"home\":2},\"attack\":{\"away\":70,\"home\":49}},\"73599\":{\"goal\":{\"away\":1,\"home\":1},\"corner\":{\"away\":2,\"home\":0}},\"7\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":1,\"home\":1},\"shotOff\":{\"away\":0,\"home\":1},\"substitution\":{\"away\":0,\"home\":0},\"kickOff\":{\"away\":0,\"home\":0},\"penaltyAwarded\":{\"away\":0,\"home\":0},\"possession\":{\"away\":49,\"home\":50},\"dangerousAttack\":{\"away\":30,\"home\":34},\"yellowCard\":{\"away\":1,\"home\":2},\"freeKickScore\":{\"away\":12,\"home\":6},\"faCard\":{\"away\":1,\"home\":2},\"shot\":{\"away\":2,\"home\":2},\"shotOn\":{\"away\":2,\"home\":1},\"offside\":{\"away\":1,\"home\":0},\"corner\":{\"away\":5,\"home\":2},\"attack\":{\"away\":60,\"home\":65}},\"74499\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":2,\"home\":0}},\"75399\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":2}},\"62699\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":0}},\"61799\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":0}}},\"eventSourceType\":1,\"scoreTime\":1647600735068,\"allScores\":{\"periodScore\":{\"away\":1,\"home\":1},\"minutesGoalScore\":{\"60899\":{\"away\":0,\"home\":1},\"73599\":{\"away\":1,\"home\":1},\"74499\":{\"away\":0,\"home\":0},\"75399\":{\"away\":0,\"home\":0},\"62699\":{\"away\":0,\"home\":0},\"61799\":{\"away\":0,\"home\":0}},\"wholeScore\":{\"away\":1,\"home\":2},\"minutesCornerScore\":{\"60899\":{\"away\":1,\"home\":2},\"73599\":{\"away\":2,\"home\":0},\"74499\":{\"away\":2,\"home\":0},\"75399\":{\"away\":1,\"home\":2},\"62699\":{\"away\":1,\"home\":0},\"61799\":{\"away\":0,\"home\":0}}},\"minuteScores\":{\"60899\":{\"goal\":{\"away\":0,\"home\":1},\"corner\":{\"away\":1,\"home\":2}},\"73599\":{\"goal\":{\"away\":1,\"home\":1},\"corner\":{\"away\":2,\"home\":0}},\"74499\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":2,\"home\":0}},\"75399\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":2}},\"62699\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":0}},\"61799\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":0}}},\"extraScores\":null,\"aoMatchId\":172824484868341762,\"secondFromStart\":5400},\"dataSourceTime\":1647600735068,\"dataSourceCode\":null,\"dataType\":null,\"tag\":null,\"operaterId\":null}");
//                Request<CommonStandardScoresDto> request =JSONObject.toJavaObject(jsonObject,Request.class);
//                CommonStandardScoresDto commonStandardScoresDto =JSONObject.toJavaObject(JSONObject.parseObject("{\"linkedId\":\"ac12b2f620220318185214886b540d2a\",\"standardMatchId\":3135511,\"periodId\":999,\"secondNum\":null,\"sportId\":1,\"dataSourceCode\":\"SR\",\"scores\":{\"60899\":{\"goal\":{\"away\":0,\"home\":1},\"corner\":{\"away\":1,\"home\":2}},\"-1\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":1,\"home\":2},\"shotOff\":{\"away\":2,\"home\":2},\"substitution\":{\"away\":0,\"home\":0},\"kickOff\":{\"away\":0,\"home\":0},\"penaltyAwarded\":{\"away\":0,\"home\":0},\"possession\":{\"away\":49,\"home\":50},\"dangerousAttack\":{\"away\":63,\"home\":48},\"yellowCard\":{\"away\":1,\"home\":2},\"freeKickScore\":{\"away\":21,\"home\":17},\"faCard\":{\"away\":1,\"home\":2},\"shot\":{\"away\":5,\"home\":4},\"shotOn\":{\"away\":3,\"home\":2},\"offside\":{\"away\":2,\"home\":0},\"corner\":{\"away\":7,\"home\":4},\"attack\":{\"away\":130,\"home\":114}},\"6\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":1},\"shotOff\":{\"away\":2,\"home\":1},\"substitution\":{\"away\":0,\"home\":0},\"kickOff\":{\"away\":0,\"home\":0},\"penaltyAwarded\":{\"away\":0,\"home\":0},\"possession\":{\"away\":50,\"home\":50},\"dangerousAttack\":{\"away\":33,\"home\":14},\"yellowCard\":{\"away\":0,\"home\":0},\"freeKickScore\":{\"away\":9,\"home\":11},\"faCard\":{\"away\":0,\"home\":0},\"shot\":{\"away\":3,\"home\":2},\"shotOn\":{\"away\":1,\"home\":1},\"offside\":{\"away\":1,\"home\":0},\"corner\":{\"away\":2,\"home\":2},\"attack\":{\"away\":70,\"home\":49}},\"73599\":{\"goal\":{\"away\":1,\"home\":1},\"corner\":{\"away\":2,\"home\":0}},\"7\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":1,\"home\":1},\"shotOff\":{\"away\":0,\"home\":1},\"substitution\":{\"away\":0,\"home\":0},\"kickOff\":{\"away\":0,\"home\":0},\"penaltyAwarded\":{\"away\":0,\"home\":0},\"possession\":{\"away\":49,\"home\":50},\"dangerousAttack\":{\"away\":30,\"home\":34},\"yellowCard\":{\"away\":1,\"home\":2},\"freeKickScore\":{\"away\":12,\"home\":6},\"faCard\":{\"away\":1,\"home\":2},\"shot\":{\"away\":2,\"home\":2},\"shotOn\":{\"away\":2,\"home\":1},\"offside\":{\"away\":1,\"home\":0},\"corner\":{\"away\":5,\"home\":2},\"attack\":{\"away\":60,\"home\":65}},\"74499\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":2,\"home\":0}},\"75399\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":2}},\"62699\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":0}},\"61799\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":0}}},\"eventSourceType\":1,\"scoreTime\":1647600735068,\"allScores\":{\"periodScore\":{\"away\":1,\"home\":1},\"minutesGoalScore\":{\"60899\":{\"away\":0,\"home\":1},\"73599\":{\"away\":1,\"home\":1},\"74499\":{\"away\":0,\"home\":0},\"75399\":{\"away\":0,\"home\":0},\"62699\":{\"away\":0,\"home\":0},\"61799\":{\"away\":0,\"home\":0}},\"wholeScore\":{\"away\":1,\"home\":2},\"minutesCornerScore\":{\"60899\":{\"away\":1,\"home\":2},\"73599\":{\"away\":2,\"home\":0},\"74499\":{\"away\":2,\"home\":0},\"75399\":{\"away\":1,\"home\":2},\"62699\":{\"away\":1,\"home\":0},\"61799\":{\"away\":0,\"home\":0}}},\"minuteScores\":{\"60899\":{\"goal\":{\"away\":0,\"home\":1},\"corner\":{\"away\":1,\"home\":2}},\"73599\":{\"goal\":{\"away\":1,\"home\":1},\"corner\":{\"away\":2,\"home\":0}},\"74499\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":2,\"home\":0}},\"75399\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":2}},\"62699\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":0}},\"61799\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":0}}},\"extraScores\":null,\"aoMatchId\":172824484868341762,\"secondFromStart\":5400}"),CommonStandardScoresDto.class);
//                request.setData(commonStandardScoresDto);
//                standardMatchScoreConsumer.onMessage(request);
        }
        /**
         * 重新结算比分
         * */
        @Test
        void testThirdGlobalStatus6() {

        }

        /**
         * 编辑比分
         * */
        @Test
        void testThirdGlobalStatus7() {

        }

        /**
         * 点球大战查询
         * */
        @Test
        void testThirdGlobalStatus8() {

        }

        /**
         * 点球大战编辑事件
         * */
        @Test
        void testThirdGlobalStatus9() {

        }

        @Autowired
        MatchEventInfoMapper matchEventInfoMapper;
        @Autowired
        StandardMatchEventConsumer standardMatchEventConsumer;
        @Test
        void wholeEventTest(){


                //goal
                //String data="{\"linkId\":\"BG_ac12b2f42023011620523915244a1c7c3367\",\"data\":[{\"id\":1614968874285527041,\"sportId\":1,\"canceled\":0,\"dataSourceCode\":\"BG\",\"sourceType\":1,\"eventCode\":\"goal\",\"eventTime\":1673873559148,\"extraInfo\":\"\",\"homeAway\":\"away\",\"matchPeriodId\":6,\"matchType\":1,\"playerIdPrefix\":null,\"player1Id\":null,\"player1Name\":null,\"player2Id\":null,\"player2Name\":null,\"secondsFromStart\":698,\"standardMatchId\":3401549,\"standardTeamId\":107820,\"t1\":0,\"t2\":1,\"secondNum\":null,\"firstT1\":null,\"firstT2\":null,\"secondT1\":null,\"secondT2\":null,\"firstNum\":null,\"thirdEventId\":\"173\",\"thirdMatchId\":\"1614676526804586497\",\"thirdMatchSourceId\":\"9616422\",\"aoThirdMatchSourceId\":\"282901410272854017\",\"thirdTeamId\":null,\"remark\":null,\"periodRemainingSeconds\":0,\"penaltyShootoutRound\":null,\"createTime\":1673873558957,\"modifyTime\":1673873558957,\"addition6\":null,\"addition7\":null,\"addition8\":null,\"addition9\":null,\"addition10\":null,\"addition1\":null,\"addition2\":null,\"addition3\":null,\"addition4\":null,\"addition5\":null,\"isErrorEndEvent\":0}],\"dataSourceTime\":1673873558972,\"dataSourceCode\":\"BG\",\"dataType\":null,\"tag\":null,\"operaterId\":null}";
        String data="{\"linkId\":\"BG_ac12b2f420230117235917333ef3f6d45a63\",\"data\":[{\"id\":1615378230722293762,\"sportId\":1,\"canceled\":0,\"dataSourceCode\":\"BG\",\"sourceType\":1,\"eventCode\":\"goal\",\"eventTime\":1673971157326,\"extraInfo\":\"\",\"homeAway\":\"home\",\"matchPeriodId\":7,\"matchType\":1,\"playerIdPrefix\":null,\"player1Id\":null,\"player1Name\":null,\"player2Id\":null,\"player2Name\":null,\"secondsFromStart\":5676,\"standardMatchId\":3401605,\"standardTeamId\":94544,\"t1\":3,\"t2\":1,\"secondNum\":null,\"firstT1\":null,\"firstT2\":null,\"secondT1\":null,\"secondT2\":null,\"firstNum\":null,\"thirdEventId\":\"874\",\"thirdMatchId\":\"1615194671277494273\",\"thirdMatchSourceId\":\"9926797\",\"aoThirdMatchSourceId\":\"283277346319187969\",\"thirdTeamId\":null,\"remark\":null,\"periodRemainingSeconds\":0,\"penaltyShootoutRound\":null,\"createTime\":1673971157137,\"modifyTime\":1673971157137,\"addition6\":null,\"addition7\":null,\"addition8\":null,\"addition9\":null,\"addition10\":null,\"addition1\":null,\"addition2\":null,\"addition3\":null,\"addition4\":null,\"addition5\":null,\"isErrorEndEvent\":0}],\"dataSourceTime\":1673971157151,\"dataSourceCode\":\"BG\",\"dataType\":null,\"tag\":null,\"operaterId\":null}";
        //kick_off(进球确认)
        Request request = JSON.parseObject(data, Request.class);
        ArrayList<MatchEventInfo> list = JSON.parseObject( request.getData().toString(), ArrayList.class);
        MatchEventInfo info = JSON.parseObject(String.valueOf(list.get(0)), MatchEventInfo.class);
        request.setData(Arrays.asList(info));
        standardMatchEventConsumer.onMessage(request);



        }

        /**
         * 模拟回滚回调
         */
        @Test
        void wholeEventTest2(){

                String data="{\"linkId\":\"67764398229247-144736003882322095-1\",\"matchId\":\"3400579\",\"sportId\":1,\"optId\":\"418\",\"optUser\":\"cptest\",\"evenRollBackId\":67764398229247,\"betTotal\":0,\"rollBackBetTotal\":0}";
                MatchSettleRollBackSuccessDto request = JSON.parseObject(data, MatchSettleRollBackSuccessDto.class);
                matchSettleRollBackSuccessConsumer.onMessage(request);

        }


        @Autowired
        StandardMarketResultConsumer standardMarketResultConsumer;
        @Test
        void resultTest(){


                String requestData ="{\"linkId\":\"RB_ac12b2f4202206241307463872ea7ae8127b\",\"data\":{\"linkedId\":\"RB_ac12b2f4202206241307463872ea7ae8127b\",\"thirdMatchId\":1538795195948683267,\"standardMatchId\":3297733,\"periodId\":999,\"sportId\":1,\"dataSourceCode\":\"RB\",\"scores\":{\"60899\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":1}},\"-1\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":3,\"home\":1},\"shotOff\":{\"away\":3,\"home\":2},\"substitution\":{\"away\":0,\"home\":0},\"kickOff\":{\"away\":0,\"home\":1},\"penaltyAwarded\":{\"away\":0,\"home\":0},\"possession\":{\"away\":0,\"home\":0},\"dangerousAttack\":{\"away\":47,\"home\":29},\"yellowCard\":{\"away\":1,\"home\":0},\"freeKickScore\":{\"away\":14,\"home\":11},\"faCard\":{\"away\":1,\"home\":0},\"shot\":{\"away\":8,\"home\":4},\"shotOn\":{\"away\":5,\"home\":2},\"offside\":{\"away\":0,\"home\":3},\"corner\":{\"away\":5,\"home\":5},\"attack\":{\"away\":101,\"home\":82}},\"6\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":1,\"home\":0},\"shotOff\":{\"away\":2,\"home\":1},\"substitution\":{\"away\":0,\"home\":0},\"kickOff\":{\"away\":0,\"home\":1},\"penaltyAwarded\":{\"away\":0,\"home\":0},\"possession\":{\"away\":0,\"home\":0},\"dangerousAttack\":{\"away\":18,\"home\":17},\"yellowCard\":{\"away\":0,\"home\":0},\"freeKickScore\":{\"away\":7,\"home\":7},\"faCard\":{\"away\":0,\"home\":0},\"shot\":{\"away\":6,\"home\":3},\"shotOn\":{\"away\":4,\"home\":2},\"offside\":{\"away\":0,\"home\":1},\"corner\":{\"away\":2,\"home\":4},\"attack\":{\"away\":42,\"home\":43}},\"73599\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":0}},\"7\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":2,\"home\":1},\"shotOff\":{\"away\":1,\"home\":1},\"substitution\":{\"away\":0,\"home\":0},\"kickOff\":{\"away\":0,\"home\":0},\"penaltyAwarded\":{\"away\":0,\"home\":0},\"possession\":{\"away\":0,\"home\":0},\"dangerousAttack\":{\"away\":29,\"home\":12},\"yellowCard\":{\"away\":1,\"home\":0},\"freeKickScore\":{\"away\":7,\"home\":4},\"faCard\":{\"away\":1,\"home\":0},\"shot\":{\"away\":2,\"home\":1},\"shotOn\":{\"away\":1,\"home\":0},\"offside\":{\"away\":0,\"home\":2},\"corner\":{\"away\":3,\"home\":1},\"attack\":{\"away\":59,\"home\":39}},\"74499\":{\"goal\":{\"away\":1,\"home\":0},\"corner\":{\"away\":0,\"home\":0}},\"75399\":{\"goal\":{\"away\":1,\"home\":1},\"corner\":{\"away\":2,\"home\":1}},\"62699\":{\"goal\":{\"away\":1,\"home\":0},\"corner\":{\"away\":0,\"home\":3}},\"61799\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":2,\"home\":0}}},\"eventSourceType\":1,\"scoreTime\":1656047266387,\"allScores\":{\"periodScore\":{\"away\":2,\"home\":1},\"minutesGoalScore\":{\"60899\":{\"away\":0,\"home\":0},\"73599\":{\"away\":0,\"home\":0},\"74499\":{\"away\":1,\"home\":0},\"75399\":{\"away\":1,\"home\":1},\"62699\":{\"away\":1,\"home\":0},\"61799\":{\"away\":0,\"home\":0}},\"wholeScore\":{\"away\":3,\"home\":1},\"minutesCornerScore\":{\"60899\":{\"away\":0,\"home\":1},\"73599\":{\"away\":1,\"home\":0},\"74499\":{\"away\":0,\"home\":0},\"75399\":{\"away\":2,\"home\":1},\"62699\":{\"away\":0,\"home\":3},\"61799\":{\"away\":2,\"home\":0}}},\"minuteScores\":{\"60899\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":1}},\"73599\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":0}},\"74499\":{\"goal\":{\"away\":1,\"home\":0},\"corner\":{\"away\":0,\"home\":0}},\"75399\":{\"goal\":{\"away\":1,\"home\":1},\"corner\":{\"away\":2,\"home\":1}},\"62699\":{\"goal\":{\"away\":1,\"home\":0},\"corner\":{\"away\":0,\"home\":3}},\"61799\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":2,\"home\":0}}},\"secondFromStart\":5698,\"eventId\":1540200005524647938,\"extraScores\":null},\"dataSourceTime\":1656047266387,\"dataSourceCode\":null,\"dataType\":null,\"tag\":null,\"operaterId\":null}";

                String data="{\"linkedId\":\"RB_ac12b2f4202206241307463872ea7ae8127b\",\"thirdMatchId\":1538795195948683267,\"standardMatchId\":3297733,\"periodId\":999,\"sportId\":1,\"dataSourceCode\":\"RB\",\"scores\":{\"60899\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":1}},\"-1\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":3,\"home\":1},\"shotOff\":{\"away\":3,\"home\":2},\"substitution\":{\"away\":0,\"home\":0},\"kickOff\":{\"away\":0,\"home\":1},\"penaltyAwarded\":{\"away\":0,\"home\":0},\"possession\":{\"away\":0,\"home\":0},\"dangerousAttack\":{\"away\":47,\"home\":29},\"yellowCard\":{\"away\":1,\"home\":0},\"freeKickScore\":{\"away\":14,\"home\":11},\"faCard\":{\"away\":1,\"home\":0},\"shot\":{\"away\":8,\"home\":4},\"shotOn\":{\"away\":5,\"home\":2},\"offside\":{\"away\":0,\"home\":3},\"corner\":{\"away\":5,\"home\":5},\"attack\":{\"away\":101,\"home\":82}},\"6\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":1,\"home\":0},\"shotOff\":{\"away\":2,\"home\":1},\"substitution\":{\"away\":0,\"home\":0},\"kickOff\":{\"away\":0,\"home\":1},\"penaltyAwarded\":{\"away\":0,\"home\":0},\"possession\":{\"away\":0,\"home\":0},\"dangerousAttack\":{\"away\":18,\"home\":17},\"yellowCard\":{\"away\":0,\"home\":0},\"freeKickScore\":{\"away\":7,\"home\":7},\"faCard\":{\"away\":0,\"home\":0},\"shot\":{\"away\":6,\"home\":3},\"shotOn\":{\"away\":4,\"home\":2},\"offside\":{\"away\":0,\"home\":1},\"corner\":{\"away\":2,\"home\":4},\"attack\":{\"away\":42,\"home\":43}},\"73599\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":0}},\"7\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":2,\"home\":1},\"shotOff\":{\"away\":1,\"home\":1},\"substitution\":{\"away\":0,\"home\":0},\"kickOff\":{\"away\":0,\"home\":0},\"penaltyAwarded\":{\"away\":0,\"home\":0},\"possession\":{\"away\":0,\"home\":0},\"dangerousAttack\":{\"away\":29,\"home\":12},\"yellowCard\":{\"away\":1,\"home\":0},\"freeKickScore\":{\"away\":7,\"home\":4},\"faCard\":{\"away\":1,\"home\":0},\"shot\":{\"away\":2,\"home\":1},\"shotOn\":{\"away\":1,\"home\":0},\"offside\":{\"away\":0,\"home\":2},\"corner\":{\"away\":3,\"home\":1},\"attack\":{\"away\":59,\"home\":39}},\"74499\":{\"goal\":{\"away\":1,\"home\":0},\"corner\":{\"away\":0,\"home\":0}},\"75399\":{\"goal\":{\"away\":1,\"home\":1},\"corner\":{\"away\":2,\"home\":1}},\"62699\":{\"goal\":{\"away\":1,\"home\":0},\"corner\":{\"away\":0,\"home\":3}},\"61799\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":2,\"home\":0}}},\"eventSourceType\":1,\"scoreTime\":1656047266387,\"allScores\":{\"periodScore\":{\"away\":2,\"home\":1},\"minutesGoalScore\":{\"60899\":{\"away\":0,\"home\":0},\"73599\":{\"away\":0,\"home\":0},\"74499\":{\"away\":1,\"home\":0},\"75399\":{\"away\":1,\"home\":1},\"62699\":{\"away\":1,\"home\":0},\"61799\":{\"away\":0,\"home\":0}},\"wholeScore\":{\"away\":3,\"home\":1},\"minutesCornerScore\":{\"60899\":{\"away\":0,\"home\":1},\"73599\":{\"away\":1,\"home\":0},\"74499\":{\"away\":0,\"home\":0},\"75399\":{\"away\":2,\"home\":1},\"62699\":{\"away\":0,\"home\":3},\"61799\":{\"away\":2,\"home\":0}}},\"minuteScores\":{\"60899\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":1}},\"73599\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":0}},\"74499\":{\"goal\":{\"away\":1,\"home\":0},\"corner\":{\"away\":0,\"home\":0}},\"75399\":{\"goal\":{\"away\":1,\"home\":1},\"corner\":{\"away\":2,\"home\":1}},\"62699\":{\"goal\":{\"away\":1,\"home\":0},\"corner\":{\"away\":0,\"home\":3}},\"61799\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":2,\"home\":0}}},\"secondFromStart\":5698,\"eventId\":1540200005524647938,\"extraScores\":null}";
                Request<StandardMarketResultMessage>  request = new Request<>();
                request = JSON.parseObject(requestData, request.getClass());
                StandardMarketResultMessage eventInfos;
                eventInfos =JSON.parseObject(data, StandardMarketResultMessage.class);
                request.setData(eventInfos);
                        standardMarketResultConsumer.onMessage(request);

        }

        @Autowired
        StandardMatchScoreConsumer standardMatchScoreConsumer;
      //接收比分中心信息X
        @Test
        void scorexTest(){


                String requestData ="{\"linkId\":\"BG_ac12b2f420230202120849498635746e6db4\",\"data\":{\"linkedId\":\"BG_ac12b2f420230202120849498635746e6db4\",\"thirdMatchId\":1620624341523320834,\"standardMatchId\":3402439,\"periodId\":100,\"sportId\":1,\"dataSourceCode\":\"BG\",\"scores\":{\"6010\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":0}},\"7065\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":0}},\"6035\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":0}},\"6015\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":0}},\"74499\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":2}},\"7080\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":0}},\"7060\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":1}},\"62699\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":2,\"home\":0}},\"6050\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":0}},\"6030\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":1}},\"7085\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":0}},\"60899\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":2,\"home\":0}},\"7055\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":0}},\"6020\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":0}},\"6045\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":0}},\"-1\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"shotOff\":{\"away\":3,\"home\":4},\"substitution\":{\"away\":2,\"home\":4},\"kickOff\":{\"away\":1,\"home\":0},\"penaltyAwarded\":{\"away\":0,\"home\":0},\"possession\":{\"away\":0,\"home\":0},\"dangerousAttack\":{\"away\":63,\"home\":49},\"yellowCard\":{\"away\":2,\"home\":1},\"freeKickScore\":{\"away\":11,\"home\":18},\"faCard\":{\"away\":2,\"home\":1},\"shot\":{\"away\":6,\"home\":7},\"shotOn\":{\"away\":3,\"home\":3},\"offside\":{\"away\":2,\"home\":2},\"corner\":{\"away\":7,\"home\":5},\"attack\":{\"away\":80,\"home\":76}},\"6025\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":0}},\"6005\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":0}},\"73599\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":1}},\"7090\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":0}},\"75399\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":1}},\"7070\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":2}},\"61799\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":1}},\"7095\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":1}},\"7050\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":0}},\"7075\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":0}},\"6040\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":0}},\"6\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"shotOff\":{\"away\":2,\"home\":1},\"substitution\":{\"away\":0,\"home\":0},\"kickOff\":{\"away\":1,\"home\":0},\"penaltyAwarded\":{\"away\":0,\"home\":0},\"possession\":{\"away\":0,\"home\":0},\"dangerousAttack\":{\"away\":39,\"home\":21},\"yellowCard\":{\"away\":0,\"home\":1},\"freeKickScore\":{\"away\":6,\"home\":10},\"faCard\":{\"away\":0,\"home\":1},\"shot\":{\"away\":5,\"home\":4},\"shotOn\":{\"away\":3,\"home\":3},\"offside\":{\"away\":1,\"home\":0},\"corner\":{\"away\":5,\"home\":1},\"attack\":{\"away\":47,\"home\":37}},\"7\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"shotOff\":{\"away\":1,\"home\":3},\"substitution\":{\"away\":2,\"home\":4},\"kickOff\":{\"away\":0,\"home\":0},\"penaltyAwarded\":{\"away\":0,\"home\":0},\"possession\":{\"away\":0,\"home\":0},\"dangerousAttack\":{\"away\":24,\"home\":28},\"yellowCard\":{\"away\":2,\"home\":0},\"freeKickScore\":{\"away\":5,\"home\":8},\"faCard\":{\"away\":2,\"home\":0},\"shot\":{\"away\":1,\"home\":3},\"shotOn\":{\"away\":0,\"home\":0},\"offside\":{\"away\":1,\"home\":2},\"corner\":{\"away\":2,\"home\":4},\"attack\":{\"away\":33,\"home\":39}}},\"eventSourceType\":1,\"scoreTime\":1675310929447,\"allScores\":{\"periodScore\":{\"away\":0,\"home\":0},\"minutesGoalScore\":{\"60899\":{\"away\":0,\"home\":0},\"73599\":{\"away\":0,\"home\":0},\"74499\":{\"away\":0,\"home\":0},\"75399\":{\"away\":0,\"home\":0},\"62699\":{\"away\":0,\"home\":0},\"61799\":{\"away\":0,\"home\":0}},\"wholeScore\":{\"away\":0,\"home\":0},\"minutesCornerScore\":{\"60899\":{\"away\":2,\"home\":0},\"73599\":{\"away\":1,\"home\":1},\"74499\":{\"away\":1,\"home\":2},\"75399\":{\"away\":0,\"home\":1},\"62699\":{\"away\":2,\"home\":0},\"61799\":{\"away\":1,\"home\":1}}},\"minuteScores\":{\"60899\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":2,\"home\":0}},\"73599\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":1}},\"74499\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":2}},\"62699\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":2,\"home\":0}},\"75399\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":1}},\"61799\":{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":1}}},\"secondFromStart\":6070,\"eventId\":1620997651864088577,\"extraScores\":null},\"dataSourceTime\":1675310929447,\"dataSourceCode\":null,\"dataType\":null,\"tag\":null,\"operaterId\":null}";
                Request request = JSON.parseObject(requestData, Request.class);
                CommonThirdScoresDto info = JSON.parseObject(String.valueOf(request.getData()), CommonThirdScoresDto.class);
                request.setData(info);
                standardMatchScoreConsumer.onMessage(request);

        }

        @Autowired
        IFootballNewMatchScoresSettleApi footballNewMatchScoresSettleApi;

        @Test
        void testNewEditSettleScore(){
                UpdateMatchSettleScoreDto dto = new UpdateMatchSettleScoreDto();
                dto.setMatchScoreId(97488831334197l);
                dto.setEventCode("goal");
                dto.setOperatorName("co");
                dto.setStandardMatchId(3239880l);
                footballNewMatchScoresSettleApi.editMatchSettleScore(dto);
        }

        @Test
        void testNewConfirmSettleScore(){
                ConfirmMatchSettleScoreDto dto = new ConfirmMatchSettleScoreDto();
                dto.setMatchScoreId(4267421768516009L);
                dto.setEventCode("goal");
                dto.setOperatorName("pi");
                dto.setStandardMatchId(3299587L);
                footballNewMatchScoresSettleApi.confirmMatchSettleScore(dto);
        }
}
