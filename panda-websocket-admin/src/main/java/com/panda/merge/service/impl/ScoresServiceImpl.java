package com.panda.merge.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.constant.SourceTypeEnum;
import com.panda.merge.dto.FootballScores;
import com.panda.merge.dto.scores.MatchScoresBetterDto;
import com.panda.merge.mapper.*;
import com.panda.merge.model.*;
import com.panda.merge.service.IScoresService;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;



@Service
@Slf4j
public class ScoresServiceImpl implements IScoresService {
    @Autowired
    MatchScoresInfoMapper matchScoresInfoMapper;
    @Autowired
    MatchScoresSourceTypeMapper matchScoresSourceTypeMapper;
    @Autowired
    StandardSportMarketSellMapper standardSportMarketSellMapper;
    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    ThirdMatchInfoMapper thirdMatchInfoMapper;
    @Autowired
    MatchTimeInfoMapper matchTimeInfoMapper;
    @Autowired
    IScoresService scoresService;
    @Autowired
    BaseProcessor baseProcessor;







    /**
     * 主客队调换
     * @param standardScore
     */
    public void changeHomeAway(MatchScoresBetterDto standardScore) {
        //只有足球才做主客队相反
        try {
            if (standardScore != null && standardScore.getSportId().equals(1L)) {
                //只有UOF比分才 要主客队互换
                if (standardScore.getDataSourceType().equals(SourceTypeEnum.UOF.getCode().toString())) {
                        if (standardScore.getHomeAwayOpposite() != null && 1 == standardScore.getHomeAwayOpposite()) {
                            Integer t1 = standardScore.getT1();
                            Integer t2 = standardScore.getT2();
                            Integer periodT1 = standardScore.getPeriodT1();
                            Integer periodT2 = standardScore.getPeriodT2();
                            standardScore.setPeriodT1(periodT2);
                            standardScore.setPeriodT2(periodT1);
                            standardScore.setT1(t2);
                            standardScore.setT2(t1);
                            if (StringUtils.isNotEmpty(standardScore.getScoresJson())) {
                                JSONObject periodFootballScores = JSONObject.parseObject(standardScore.getScoresJson());
                                Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
                                for (FootballScores value : allPeriodScores.values()) {
                                    value.changeHomeAwayScore();
                                }
                                standardScore.setScoresJson(JSONObject.toJSONString(allPeriodScores));
                            }
                        }
                }
            }
        }catch (Exception e){
//            log.error("changeHomeAway error:{}",e.getStackTrace());
//            e.printStackTrace();
        }
    }
    /**
     * 主客队调换
     * @param standardScore
     */
    public void changeHomeAway(MatchScoresInfo standardScore) {
        //只有足球才做主客队相反
        try {
            if (standardScore != null && standardScore.getSportId().equals(1L)) {
                //只有UOF比分才需要主客队互换  事件比分不需要
                if (standardScore.getDataSourceType().equals(SourceTypeEnum.UOF.getCode().toString())) {
                    ThirdMatchInfo thirdMatchInfo = thirdMatchInfoMapper.selectByPrimaryKey(standardScore.getThirdMatchId());
                    if (thirdMatchInfo != null && thirdMatchInfo.getHomeAwayOpposite() != null && 1 == thirdMatchInfo.getHomeAwayOpposite()) {
                        Integer t1 = standardScore.getT1();
                        Integer t2 = standardScore.getT2();
                        Integer periodT1 = standardScore.getPeriodT1();
                        Integer periodT2 = standardScore.getPeriodT2();
                        standardScore.setPeriodT1(periodT2);
                        standardScore.setPeriodT2(periodT1);
                        standardScore.setT1(t2);
                        standardScore.setT2(t1);
                        if (StringUtils.isNotEmpty(standardScore.getScoresJson())) {
                            JSONObject periodFootballScores = JSONObject.parseObject(standardScore.getScoresJson());
                            Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
                            for (FootballScores value : allPeriodScores.values()) {
                                value.changeHomeAwayScore();
                            }
                            standardScore.setScoresJson(JSONObject.toJSONString(allPeriodScores));
                        }
                    }
                }
            }
        }catch (Exception e){
            log.error("changeHomeAway error:{}",e.getStackTrace());
//            e.printStackTrace();
        }
    }

    public boolean isLivedataStoped(Long thirdMatchId) {
        MatchScoresInfoExample example = new MatchScoresInfoExample();
        example.createCriteria().andThirdMatchIdEqualTo(thirdMatchId).andDataSourceTypeEqualTo(SourceTypeEnum.LIVE_DATA.getCode().toString());
        List<MatchScoresInfo> scoresInfos = matchScoresInfoMapper.selectByExample(example);
        if(scoresInfos.size()==0||scoresInfos.get(0).getScoresJson()==null){
            log.info("isLivedataStoped 0三方赛事ID：{} 暂无有效事件, 判断可下发UOF比分",thirdMatchId);
            return true;
        }
        MatchScoresSourceType matchScoresSourceType =matchScoresSourceTypeMapper.selectByPrimaryKey(thirdMatchId);
        //1. 当前是 uof 直接下发
        if(matchScoresSourceType==null){
            log.info("isLivedataStoped 1三方赛事ID：{} 暂无事件, 判断可下发UOF比分",thirdMatchId);
            return true;
        }
        if(matchScoresSourceType.getSourceType().equals(SourceTypeEnum.UOF.getCode()+"")) {
            log.info("isLivedataStoped 2三方赛事ID：{} 比分已经切换到UOF, 判断可下发UOF比分", thirdMatchId);
            return true;
        }

        return false;
    }



    @Override
    public Long checkStandardScore(Long standardId) {

        StandardMatchInfo standardMatchInfo =standardMatchInfoMapper.selectByPrimaryKey(standardId);
        if(standardMatchInfo==null){
            return null;
        }
        StandardSportMarketSellExample example= new StandardSportMarketSellExample();
        example.createCriteria().andMatchInfoIdEqualTo(standardId);
        List<StandardSportMarketSell> eventSells = standardSportMarketSellMapper.selectByExample(example);
        if(eventSells.size()!=0) {
            StandardSportMarketSell standardSportMarketSell = eventSells.get(0);
            ThirdMatchInfoExample thirdMatchInfoExample =new ThirdMatchInfoExample();
            thirdMatchInfoExample.createCriteria().andDataSourceCodeEqualTo(standardSportMarketSell.getBusinessEvent()).andReferenceIdEqualTo(standardId);
            List<ThirdMatchInfo> list =thirdMatchInfoMapper.selectByExample(thirdMatchInfoExample);
            if(list.size()==0){
                return standardMatchInfo.getThirdMatchId();
            }else {
                return list.get(0).getId();
            }
        }else {
            return standardMatchInfo.getThirdMatchId();
        }
    }
}
