package com.panda.merge;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.advertise.dubbo.FootballDashboardAdvertiseApiImpl;
import com.panda.merge.advertise.dubbo.FootballDashboardHotKeyApiImpl;
import com.panda.merge.advertise.dubbo.MatchFootballBallAdvertiseApiImpl;
import com.panda.merge.api.IScoresCenterApi;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.ConfirmEventDto;
import com.panda.merge.dto.advertise.InjuryTimeEventDto;
import com.panda.merge.dto.advertise.PossibleEventDto;
import com.panda.merge.dto.advertise.TimeStatusEventDto;
import com.panda.merge.dto.scores.StandardScoreCenter;
import com.panda.merge.dubbo.ScoresCenterApiImpl;
import com.panda.merge.model.FootballKeyboardSet;
import com.panda.merge.service.IScoresService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author warren
 * @since 2023/12/15 11:35:11
 */
@Slf4j
@SpringBootTest
public class PADashboardTest {
    @Autowired
    FootballDashboardAdvertiseApiImpl footballDashboardAdvertiseApi;

    @Autowired
    FootballDashboardHotKeyApiImpl footballDashboardHotKeyApi;

    @Autowired
    MatchFootballBallAdvertiseApiImpl matchFootballBallAdvertiseApi;

    @Autowired
    private RedisService redisService;
    @Autowired
    IScoresCenterApi scoresCenterApi;

    @Test
    public void injuryTimeEventTest() {
        String paramStr = "{\"thirdMatchId\":\"1711581937470230529\",\"timeOut\":10,\"currentTime\":1702607210033}";
        InjuryTimeEventDto injuryTimeEventDto = JSONObject.parseObject(paramStr, InjuryTimeEventDto.class);
//        injuryTimeEventDto.setLinkedId("09W45UTY09WERUY09ERT0Y9HER0UYH0ET");
//        injuryTimeEventDto.setOperatorId("419");
//        injuryTimeEventDto.setOperatorName("nonghung");
        Response response = footballDashboardAdvertiseApi.injuryTimeEvent(injuryTimeEventDto);
        System.out.println("response: " + response);
    }
    @Test
    public void query() {
        Response response = scoresCenterApi.queryMatchScores(3956843L,1L);
        System.out.println("response: " + response);
    }
    @Test
    public void edit() {
        String paramStr = "{\"sportId\":1,\"scores\":[{\"periodId\":6,\"home\":\"1\",\"away\":\"1\",\"isDifference\":null,\"scores\":{\"6\":{\"redCard\":{\"away\":8,\"home\":8},\"yellowCard\":{\"away\":8,\"home\":8},\"corner\":{\"away\":8,\"home\":8},\"goal\":{\"away\":8,\"home\":8}},\"7\":{\"redCard\":{\"away\":1,\"home\":1},\"yellowCard\":{\"away\":1,\"home\":3},\"corner\":{\"away\":2,\"home\":1},\"goal\":{\"away\":5,\"home\":2}}}}],\"standardMatchId\":3967609}";
        StandardScoreCenter cc = JSONObject.parseObject(paramStr, StandardScoreCenter.class);

        scoresCenterApi.editStandScores(cc);
    }
    @Test
    public void timeStatusEventTest() {
        String paramStr = "{\"thirdMatchId\":\"1711581937470230529\",\"resetTime\":0}";
        TimeStatusEventDto timeStatusEventDto = JSONObject.parseObject(paramStr, TimeStatusEventDto.class);
//        injuryTimeEventDto.setLinkedId("09W45UTY09WERUY09ERT0Y9HER0UYH0ET");
//        injuryTimeEventDto.setOperatorId("419");
//        injuryTimeEventDto.setOperatorName("nonghung");
        Response response = footballDashboardAdvertiseApi.timeStatusEvent(timeStatusEventDto);
        System.out.println("response: " + response);
    }

    @Test
    public void addKeyboardInfoTest() {
        String paramStr = "{\"sportId\":\"1\",\"userId\":1234,\"userName\":\"test01\",\"keyboardInfo\":\"fdsafasfdsa\"}";
        FootballKeyboardSet footballKeyboardSet = JSONObject.parseObject(paramStr, FootballKeyboardSet.class);
        footballKeyboardSet.setCreateUser("123456");
        footballKeyboardSet.setModifyUser("123456");
        footballKeyboardSet.setCreateTime(System.currentTimeMillis());
        footballKeyboardSet.setModifyTime(System.currentTimeMillis());
        footballDashboardHotKeyApi.addKeyboardInfo(footballKeyboardSet);
    }

    @Test
    public void getKeyboardByUserNameTest() {
        Long thirdMatchId=123465L;
        Response response = footballDashboardHotKeyApi.getKeyboardByUserNameAndThirdMatchId("123",thirdMatchId);
        System.out.println(response);
    }

    @Test
    public void updateKeyboardByUserNameTest() {
        String paramStr = "{\"sportId\":\"1\",\"userId\":123,\"keyboardInfo\":\"tttt\"}";
        FootballKeyboardSet footballKeyboardSet = JSONObject.parseObject(paramStr, FootballKeyboardSet.class);
        footballKeyboardSet.setCreateUser("123456L");
        footballKeyboardSet.setModifyUser("123456L");
        footballKeyboardSet.setCreateTime(System.currentTimeMillis());
        footballKeyboardSet.setModifyTime(System.currentTimeMillis());
        footballDashboardHotKeyApi.updateKeyboardByUserName(footballKeyboardSet);
    }

    @Test
    public void testPossibleEvent(){
        String paramStr = "{\"thirdMatchId\":\"1711637838747095042\",\"possibleEventCode\":\"possible_red_card\",\"homeAway\":\"home\",\"timeFromStartSecond\":1234}";

        PossibleEventDto possibleEventDto = JSONObject.parseObject(paramStr, PossibleEventDto.class);
        possibleEventDto.setLinkedId("09W45UTY09WERUY09ERT0Y9HER0UYH0ET");
        possibleEventDto.setOperatorId("418");
        possibleEventDto.setOperatorName("warren");
        Response response = matchFootballBallAdvertiseApi.possibleEvent(possibleEventDto);
        System.out.printf("response:{}", response);
    }

    @Test
    public void testMget(){
//        List<String> list = Arrays.asList("awaypossible_yellow_card1711637838747095042","homepossible_free_kick1711637838747095042",
//                "awaypossible_free_kick1711637838747095042","awaypossible_goal1711637838747095042","homepossible_penalty1711637838747095042",
//                "awaypossible_red_card1711637838747095042","homepossible_corner1711637838747095042","homepossible_goal1711637838747095042",
//                "homepossible_red_card1711637838747095042","homepossible_yellow_card1711637838747095042","awaypossible_penalty1711637838747095042",
//                "awaypossible_corner1711637838747095042");
        List<String> eventCodeList = Arrays.asList("possible_red_card", "possible_yellow_card", "possible_goal",
                "possible_penalty", "possible_free_kick", "possible_corner");
        Set<String> keys = new HashSet<>();
        Long id = 1711637838747095042L;
        for (String item : eventCodeList) {
            keys.add("home" + item + id);
            keys.add("away" + item + id);
        }
        List<Object> list1 = redisService.mGet(new ArrayList<>(keys));
        System.out.println(list1);
    }
}
