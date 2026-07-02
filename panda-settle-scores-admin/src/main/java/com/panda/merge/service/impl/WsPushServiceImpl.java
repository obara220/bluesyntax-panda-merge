package com.panda.merge.service.impl;

import com.panda.merge.dto.settle.*;
import com.panda.merge.model.MatchSettleRollBackInfo;
import com.panda.merge.mq.producer.MatchSettleWsProducer;
import com.panda.merge.service.IWsPushService;
import com.panda.merge.v2.service.assemble.MatchSettleEventAssemble;
import com.panda.merge.v2.service.assemble.MatchSettleScoreAssemble;
import com.panda.merge.v2.service.assemble.MatchSettleThirdEventAssemble;
import com.panda.merge.v2.service.assemble.MatchSettleThirdScoreAssemble;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class WsPushServiceImpl implements IWsPushService {

    @Resource
    private MatchSettleWsProducer matchSettleWsProducer;
    @Resource
    private MatchSettleScoreAssemble matchSettleScoreAssemble;
    @Resource
    private MatchSettleEventAssemble matchSettleEventAssemble;
    @Resource
    private MatchSettleThirdScoreAssemble matchSettleThirdScoreAssemble;
    @Resource
    private MatchSettleThirdEventAssemble matchSettleThirdEventAssemble;

    @Override
    public void pushStandardSettleScores(Long standardMatchId, String eventCode) {
        eventCode = checkEventCode(eventCode);
        MatchSettleScoreSearchDto matchSettleScoreSearchDto =new MatchSettleScoreSearchDto();
        matchSettleScoreSearchDto.setSportId(1l);
        matchSettleScoreSearchDto.setStandardMatchId(standardMatchId);
        matchSettleScoreSearchDto.setEventCode(eventCode);
        List l = matchSettleScoreAssemble.searchFootballMatchSettleScores(matchSettleScoreSearchDto);
        matchSettleWsProducer.pushStandardSettleScores(l,standardMatchId,eventCode);
    }

    @Override
    public void pushBasketballStandardSettleScores(Long standardMatchId, String eventCode) {

        MatchSettleScoreSearchDto matchSettleScoreSearchDto =new MatchSettleScoreSearchDto();
        matchSettleScoreSearchDto.setSportId(2L);
        matchSettleScoreSearchDto.setStandardMatchId(standardMatchId);
        matchSettleScoreSearchDto.setEventCode(eventCode);
        List l = matchSettleScoreAssemble.searchBasketballMatchSettleScores(matchSettleScoreSearchDto);
        matchSettleWsProducer.pushStandardSettleScores(l,standardMatchId,eventCode);
    }


    @Override
    public void pushThirdSettleScores(Long standardMatchId, String eventCode) {
        eventCode = checkEventCode(eventCode);
        MatchSettleScoreSearchDto settleScoreSearchDto =new MatchSettleScoreSearchDto();
        settleScoreSearchDto.setEventCode(eventCode);
        settleScoreSearchDto.setSportId(1l);
        settleScoreSearchDto.setStandardMatchId(standardMatchId);
        ThirdMatchSettleScoresDto thirdMatchSettleScoresDto= matchSettleThirdScoreAssemble.searchFootballThirdMatchSettleScores(settleScoreSearchDto);
        matchSettleWsProducer.pushThirdSettleScores(thirdMatchSettleScoresDto,standardMatchId,eventCode);
    }

    @Override
    public void pushThirdBasketballSettleScores(Long standardMatchId) {
        MatchSettleScoreSearchDto settleScoreSearchDto =new MatchSettleScoreSearchDto();
        settleScoreSearchDto.setSportId(2L);
        settleScoreSearchDto.setStandardMatchId(standardMatchId);
        ThirdMatchSettleScoresDto thirdMatchSettleScoresDto= matchSettleThirdScoreAssemble.searchBasketballThirdMatchSettleScores(settleScoreSearchDto);
        matchSettleWsProducer.pushThirdSettleScores(thirdMatchSettleScoresDto,standardMatchId,"");
    }

    @Override
    public void pushStandardSettleEvent(Long standardMatchId, String eventCode) {
        eventCode = checkEventCode(eventCode);
        MatchSettleScoreSearchDto matchSettleScoreSearchDto =new MatchSettleScoreSearchDto();
        matchSettleScoreSearchDto.setSportId(1l);
        matchSettleScoreSearchDto.setStandardMatchId(standardMatchId);
        matchSettleScoreSearchDto.setEventCode(eventCode);
        List l = matchSettleEventAssemble.searchFootballMatchSettleEvent(matchSettleScoreSearchDto);
        matchSettleWsProducer.pushStandardSettleEvent(l,standardMatchId,eventCode);
    }

    private String checkEventCode(String eventCode) {
        if(eventCode.equals("goal")||eventCode.equals("kick_off")){
            return "goal";
        }else if(eventCode.equals("fa_card")||eventCode.equals("yellow_card")||eventCode.equals("red_card")){
            return "fa_card";
        }else {
            return "corner";
        }
    }

    @Override
    public void pushThirdSettleEvent(Long standardMatchId, String eventCode) {
        eventCode = checkEventCode(eventCode);
        MatchSettleScoreSearchDto settleScoreSearchDto =new MatchSettleScoreSearchDto();
        settleScoreSearchDto.setEventCode(eventCode);
        settleScoreSearchDto.setSportId(1l);
        settleScoreSearchDto.setStandardMatchId(standardMatchId);
        ThirdMatchSettleEventDto thirdMatchSettleScoresDto= matchSettleThirdEventAssemble.searchFootballThirdMatchSettleEvent(settleScoreSearchDto);
        matchSettleWsProducer.pushThirdSettleEvent(thirdMatchSettleScoresDto,standardMatchId,eventCode);
    }

    @Override
    public void pushSettleMatchList(MatchListSettleDto matchListSettleDto) {
        String eventCode = checkEventCode(matchListSettleDto.getEventCode());
        matchListSettleDto.setEventCode(eventCode);
        matchSettleWsProducer.pushSettleMatchList(matchListSettleDto);
    }

    @Override
    public void pushGlobalAutoSettleStatus(AutoSettleDataSourceDto dto) {
        matchSettleWsProducer.pushGlobalAutoSettleStatus(dto);
    }

    @Override
    public void pushMatchSettleRollBackStatus(MatchSettleRollBackInfo info) {
        MatchSettleRollBackDto dto = new MatchSettleRollBackDto();
        BeanUtils.copyProperties(info,dto);
        matchSettleWsProducer.pushMatchSettleRollBackStatus(dto);
    }
}
