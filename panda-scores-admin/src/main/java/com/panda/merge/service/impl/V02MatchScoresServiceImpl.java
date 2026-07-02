package com.panda.merge.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.FootballScores;
import com.panda.merge.mapper.MatchScoresInfoMapper;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.service.V02MatchScoresService;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;


/**
 * BT事件比分服务
 */
@Service
@Slf4j
public class V02MatchScoresServiceImpl implements V02MatchScoresService {

    @Autowired
    MatchScoreInfoRepository matchScoreInfoRepository;

    @Override
    public void processVideoScore(ThirdMatchInfo thirdMatchInfo, MatchScoresInfo matchScoresInfo, MatchEventInfo event) {
        updateScores(matchScoresInfo, event);
    }


    private void updateScores(MatchScoresInfo matchScoresInfo, MatchEventInfo event) {
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
        FootballScores wholeSores = allPeriodScores.get(WHOLE_MATCH);
        if(wholeSores==null){
            wholeSores=new FootballScores(null);
            allPeriodScores.put(WHOLE_MATCH,wholeSores);
        }
        if(event.getEventCode().equals("goal")){
            wholeSores.getGoal().setHome(event.getT1());
            wholeSores.getGoal().setAway(event.getT2());
        }else if(event.getEventCode().equals("corner")) {
            wholeSores.getCorner().setHome(event.getT1());
            wholeSores.getCorner().setAway(event.getT2());
        }else if(event.getEventCode().equals("yellow_card")){
            wholeSores.getYellowCard().setHome(event.getT1());
            wholeSores.getYellowCard().setAway(event.getT2());
        }else if(event.getEventCode().equals("red_card")){
            wholeSores.getRedCard().setHome(event.getT1());
            wholeSores.getRedCard().setAway(event.getT2());
        }
        matchScoresInfo.setT1(wholeSores.getGoal().getHome());
        matchScoresInfo.setT2(wholeSores.getGoal().getAway());

        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);

    }

}
