package com.panda.merge;

import com.panda.merge.api.IFootballMatchScoresSettleApi;
import com.panda.merge.api.IFootballNewMatchScoresSettleApi;
import com.panda.merge.check.IMatchSettleCheckService;
import com.panda.merge.dto.settle.EditMatchSettleEventDto;
import com.panda.merge.dto.settle.MatchSettleScoreSearchDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Slf4j
public class MatchCheckTest {
    @Autowired
    IMatchSettleCheckService matchSettleCheckService;
    @Autowired
    IFootballNewMatchScoresSettleApi iFootballNewMatchScoresSettleApi;
    @Autowired
    IFootballMatchScoresSettleApi iFootballMatchScoresSettleApi;
    @Test
    public void testLimitUser(){
        Long standardMatchId =3279380l;
        List<String> userLimit= new ArrayList<>();
        userLimit.add("veigar");
        matchSettleCheckService.lockUserListByCheckPass(standardMatchId,userLimit);

    }

    @Test
    public void editTest(){
        EditMatchSettleEventDto editMatchSettleEventDto =new EditMatchSettleEventDto();
        /*eventCode: "goal"
eventId: "5432609733579146"
goWaterStatus: 0
homeAway: "home"
operatorId: 418
operatorName: "cptest"
settleCount: null
sportId: 1
standardMatchId: 2036302*/
        editMatchSettleEventDto.setEventCode("goal");
        editMatchSettleEventDto.setEventId(56410158602976L);
        editMatchSettleEventDto.setGoWaterStatus(0);
        editMatchSettleEventDto.setHomeAway("home");
        editMatchSettleEventDto.setOperatorId("418");
        editMatchSettleEventDto.setOperatorName("cptest");
        editMatchSettleEventDto.setSettleCount(null);
        editMatchSettleEventDto.setSportId(1L);
        editMatchSettleEventDto.setStandardMatchId(3292910L);
        editMatchSettleEventDto.setIpAddress("123123");
        iFootballNewMatchScoresSettleApi.editMatchSettleEvent(editMatchSettleEventDto);

    }

    @Test
    public void confirmTest(){
        EditMatchSettleEventDto editMatchSettleEventDto =new EditMatchSettleEventDto();
        editMatchSettleEventDto.setEventCode("goal");
        editMatchSettleEventDto.setEventId(845878025112209l);
        editMatchSettleEventDto.setGoWaterStatus(0);
        editMatchSettleEventDto.setHomeAway("home");
        editMatchSettleEventDto.setOperatorId("418");
        editMatchSettleEventDto.setOperatorName("veigar");
        editMatchSettleEventDto.setSettleCount(null);
        editMatchSettleEventDto.setSportId(1L);
        editMatchSettleEventDto.setStandardMatchId(3276114L);
        editMatchSettleEventDto.setIpAddress("123123");
        iFootballNewMatchScoresSettleApi.confirmMatchSettleEvent(editMatchSettleEventDto);

    }
}
