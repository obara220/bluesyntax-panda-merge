package com.panda.merge.advertise.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.advertise.service.CommonScoreEventService;
import com.panda.merge.common.enums.TeamTypeEnum;
import com.panda.merge.constant.SourceTypeEnum;
import com.panda.merge.dto.CommonItem;
import com.panda.merge.dto.FootballScores;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.service.ThirdMatchInfoService;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

/**
 * @author Kepa
 */

@Slf4j
@Service
public class CommonScoreEventServiceImpl implements CommonScoreEventService {

    @Autowired
    private MatchScoreInfoRepository matchScoreInfoRepository;

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    /**
     * 查询当前点球大战阶段的实时比分
     * @param linkId
     * @param matchScoresInfo
     * @param homeAway
     * @return
     */
    @Override
    public Integer getCurrentPenaltyScore(String linkId, MatchScoresInfo matchScoresInfo, String homeAway) {
        if ( Objects.isNull(matchScoresInfo) || StringUtils.isEmpty(homeAway)) {
            log.info("::{}::matchScoresInfo或homeAway为空", linkId);
            return null;
        }
        if ( null == matchScoresInfo.getPeriod()  || !"50".equals(matchScoresInfo.getPeriod().toString()) ) {
            log.info("::{}::getCurrentPenaltyScore赛事阶段不属于点球大战", linkId);
            return null;
        }

        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
        FootballScores footballScores50 = allPeriodScores.get(50L);
        if( null == footballScores50 ){
            log.info("::{}::getCurrentPenaltyScore点球大战信息不存在", linkId);
            return null;
        }
        CommonItem commonItem = footballScores50.getGoal();
        if( null == commonItem ){
            log.info("::{}::getCurrentPenaltyScore点球大战计分不存在", linkId);
            return null;
        }
        if ( TeamTypeEnum.HOME.code.equals( homeAway )) {
            return commonItem.getHome();
        } else if ( TeamTypeEnum.AWAY.code.equals( homeAway ) ) {
            return commonItem.getAway();
        }
        return null;
    }

    @Override
    public Integer getCurrentPenaltyScore(String linkId, String dataSourceCode, String thirdMatchSourceId, String homeAway) {
        if ( StringUtils.isEmpty(dataSourceCode) || StringUtils.isEmpty(thirdMatchSourceId) || StringUtils.isEmpty(homeAway) ) {
            log.info("::{}::getCurrentPenaltyScore的入参; dataSourceCode:{}, thirdMatchSourceId:{}, homeAway:{}", linkId, dataSourceCode, thirdMatchSourceId, homeAway);
            return null;
        }
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem( dataSourceCode, thirdMatchSourceId);
        if ( null == thirdMatchInfo ) {
            log.info("::{}::getCurrentPenaltyScore三方赛事查询为空", linkId);
            return null;
        }
        MatchScoresInfo matchScoresInfo = matchScoreInfoRepository.selectByExample( thirdMatchInfo.getId(), SourceTypeEnum.LIVE_DATA.getCode());
        if ( null == thirdMatchInfo ) {
            log.info("::{}::getCurrentPenaltyScore三方赛事比分数据查询为空", linkId);
            return null;
        }
        return getCurrentPenaltyScore( linkId, matchScoresInfo, homeAway);
    }

}
