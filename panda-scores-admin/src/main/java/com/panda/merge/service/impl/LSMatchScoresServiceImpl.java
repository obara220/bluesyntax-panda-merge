package com.panda.merge.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.dto.*;
import com.panda.merge.mapper.MatchScoresInfoMapper;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.mq.producer.ScoreEventProducer;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.service.BTMatchScoresService;
import com.panda.merge.service.IScoresService;
import com.panda.merge.service.LSMatchScoresService;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

/**
 * LS赛事比分服务
 */
@Service
@Slf4j
public class LSMatchScoresServiceImpl implements LSMatchScoresService {
    @Autowired
    IScoresService scoresService;
    @Autowired
    ScoreEventProducer eventProducer;
    @Autowired
    RedisService redisService;
    @Autowired
    MatchScoreInfoRepository matchScoreInfoRepository;

    /**
     * 更新比分
     * matchScoresInfo 三方赛事比分记录
     * request 接入下发统计信息
     * */
    @Override
    public void updateScores(MatchScoresInfo matchScoresInfo, Request<MatchStatisticsInfoDTO> request) {
        if(request.getData()==null||request.getData().getPeriod()==null){
            log.error("::{}::updateScores 阶段不存在",request.getLinkId());
            return ;
        }
        try {
            if(StringUtils.isEmpty(matchScoresInfo.getScoresJson())){
                createScores(matchScoresInfo,request);
            }
        }catch (Exception e){
            log.error(":处理数据发生异常:", e);
        }
    }

    /**
     * 创建比分
     * matchScoresInfo 三方赛事比分记录
     * request 接入下发统计信息
     * */
    private void createScores(MatchScoresInfo matchScoresInfo, Request<MatchStatisticsInfoDTO> request) {
            FootballScores wholeScore=new FootballScores(WHOLE_MATCH);
            Map<Long, FootballScores> footballScoresHashMap= new HashMap<>();
            footballScoresHashMap.put(WHOLE_MATCH,wholeScore);
            //保存比分
            if(request.getData().getMatchStatisticsInfoDetailList()==null){
                log.error("createMatchStatistics data:null");
                return;
            }
            for (MatchStatisticsInfoDetailDTO matchStatisticsInfoDetailDTO : request.getData().getMatchStatisticsInfoDetailList()) {
                if(matchStatisticsInfoDetailDTO.getCode().equals("set_score")){
                    if(matchStatisticsInfoDetailDTO.getFirstNum()==1){
                            FootballScores secondScores;
                            secondScores=footballScoresHashMap.get(6L)==null?new FootballScores(6l):footballScoresHashMap.get(6L);
                            secondScores.getGoal().setHome(matchStatisticsInfoDetailDTO.getT1());
                            secondScores.getGoal().setAway(matchStatisticsInfoDetailDTO.getT2());
                            footballScoresHashMap.put(6l,secondScores);
                    }
                    if(matchStatisticsInfoDetailDTO.getFirstNum()==2){
                        FootballScores secondScores;
                        secondScores=footballScoresHashMap.get(7L)==null?new FootballScores(7l):footballScoresHashMap.get(7L);
                        secondScores.getGoal().setHome(matchStatisticsInfoDetailDTO.getT1());
                        secondScores.getGoal().setAway(matchStatisticsInfoDetailDTO.getT2());
                        footballScoresHashMap.put(7l,secondScores);
                    }
                }
                //上下半场角球数
                if(matchStatisticsInfoDetailDTO.getCode().equals("set_corner_score")){
                    if(matchStatisticsInfoDetailDTO.getFirstNum()==1){
                        FootballScores secondScores;
                        secondScores=footballScoresHashMap.get(6L)==null?new FootballScores(6l):footballScoresHashMap.get(6L);
                        secondScores.getCorner().setHome(matchStatisticsInfoDetailDTO.getT1());
                        secondScores.getCorner().setAway(matchStatisticsInfoDetailDTO.getT2());
                        footballScoresHashMap.put(6l,secondScores);
                    }
                    if(matchStatisticsInfoDetailDTO.getFirstNum()==2){
                        FootballScores secondScores;
                        secondScores=footballScoresHashMap.get(7L)==null?new FootballScores(7l):footballScoresHashMap.get(7L);
                        secondScores.getCorner().setHome(matchStatisticsInfoDetailDTO.getT1());
                        secondScores.getCorner().setAway(matchStatisticsInfoDetailDTO.getT2());
                        footballScoresHashMap.put(7l,secondScores);
                    }
                }
                //全场+加时比分
                if(matchStatisticsInfoDetailDTO.getCode().equals("match_score")){
                    wholeScore.getGoal().setHome(matchStatisticsInfoDetailDTO.getT1());
                    wholeScore.getGoal().setAway(matchStatisticsInfoDetailDTO.getT2());
                }
                //全场+加时黄牌
                if(matchStatisticsInfoDetailDTO.getCode().equals("yellow_card_score")){
                    wholeScore.getYellowCard().setHome(matchStatisticsInfoDetailDTO.getT1());
                    wholeScore.getYellowCard().setAway(matchStatisticsInfoDetailDTO.getT2());
                }
                //全场+加时红牌
                if(matchStatisticsInfoDetailDTO.getCode().equals("red_card_score")){
                    wholeScore.getRedCard().setHome(matchStatisticsInfoDetailDTO.getT1());
                    wholeScore.getRedCard().setAway(matchStatisticsInfoDetailDTO.getT2());
                }
                //全场+加时角球
                if(matchStatisticsInfoDetailDTO.getCode().equals("corner_score")){
                    wholeScore.getCorner().setHome(matchStatisticsInfoDetailDTO.getT1());
                    wholeScore.getCorner().setAway(matchStatisticsInfoDetailDTO.getT2());
                }
                //全场+加时危险进攻
                //dangerous_attack_score shot_on_target_score  shot_off_target_score
                if(matchStatisticsInfoDetailDTO.getCode().equals("dangerous_attack_score")){
                    wholeScore.getDangerousAttack().setHome(matchStatisticsInfoDetailDTO.getT1());
                    wholeScore.getDangerousAttack().setAway(matchStatisticsInfoDetailDTO.getT2());
                }
                //全场+加时射正
                if(matchStatisticsInfoDetailDTO.getCode().equals("shot_on_target_score")){
                    wholeScore.getShotOn().setHome(matchStatisticsInfoDetailDTO.getT1());
                    wholeScore.getShotOn().setAway(matchStatisticsInfoDetailDTO.getT2());
                }
                //全场+加时射偏
                if(matchStatisticsInfoDetailDTO.getCode().equals("shot_off_target_score")){
                    wholeScore.getShotOff().setHome(matchStatisticsInfoDetailDTO.getT1());
                    wholeScore.getShotOff().setAway(matchStatisticsInfoDetailDTO.getT2());
                }
            }
        wholeScore.countFaCard();
            //更新赛事比分表
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(footballScoresHashMap));
        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
            return;

    }


    private void updateFootballScores(MatchScoresInfo matchScoresInfo,Request<MatchStatisticsInfoDTO> request) {
        //3.得到当前阶段比分
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        FootballScores wholeScore=allPeriodScores.get(SportPeriodConstant.SportPeriod.WHOLE_MATCH);
        FootballScores period=allPeriodScores.get(request.getData().getPeriod()+0L);
        // 6 7 41 42
        if(request.getData().getPeriod()==6||request.getData().getPeriod()==7||request.getData().getPeriod()==41||request.getData().getPeriod()==42||request.getData().getPeriod()==50){
            if(period==null){
                period=new FootballScores(request.getData().getPeriod()+0l);
                allPeriodScores.put(request.getData().getPeriod()+0l,period);
            }
        }
        for (MatchStatisticsInfoDetailDTO matchStatisticsInfoDetailDTO : request.getData().getMatchStatisticsInfoDetailList()) {
            if(matchStatisticsInfoDetailDTO.getCode().equals("set_score")){
                if(matchStatisticsInfoDetailDTO.getFirstNum()==1){
                    FootballScores secondScores=allPeriodScores.get(6l);
                    if(allPeriodScores.get(6l)==null){
                        secondScores=new FootballScores(6l);
                        allPeriodScores.put(6l,secondScores);
                    }
                    secondScores.getGoal().setHome(matchStatisticsInfoDetailDTO.getT1());
                    secondScores.getGoal().setAway(matchStatisticsInfoDetailDTO.getT2());
                }
                if(matchStatisticsInfoDetailDTO.getFirstNum()==2){
                    FootballScores secondScores=allPeriodScores.get(7l);
                    if(allPeriodScores.get(7l)==null){
                        secondScores=new FootballScores(7l);
                        allPeriodScores.put(7l,secondScores);
                    }
                    secondScores.getGoal().setHome(matchStatisticsInfoDetailDTO.getT1());
                    secondScores.getGoal().setAway(matchStatisticsInfoDetailDTO.getT2());
                }
            }
            //非加时点球计算
            if(matchStatisticsInfoDetailDTO.getCode().equals("match_score")&&(request.getData().getPeriod()==6||request.getData().getPeriod()==7||request.getData().getPeriod()==100||request.getData().getPeriod()==31)){
                Integer xT1= matchStatisticsInfoDetailDTO.getT1()-wholeScore.getGoal().getHome();
                Integer xT2= matchStatisticsInfoDetailDTO.getT2()-wholeScore.getGoal().getAway();
                wholeScore.getGoal().setHome(matchStatisticsInfoDetailDTO.getT1());
                wholeScore.getGoal().setAway(matchStatisticsInfoDetailDTO.getT2());
                if(period!=null){
                    period.getGoal().setHome( period.getGoal().getHome()+xT1);
                    period.getGoal().setAway( period.getGoal().getAway()+xT2);
                }
            }
            //红黄牌角球 需要阶段性计算 UOF
            //当前总比分 减去 原总比分 即 当前阶段的变化分值，故当前阶段比分= 原阶段比分 + 变化分值
            if(matchStatisticsInfoDetailDTO.getCode().equals("yellow_card_score")){
                Integer xT1= matchStatisticsInfoDetailDTO.getT1()-wholeScore.getYellowCard().getHome();
                Integer xT2= matchStatisticsInfoDetailDTO.getT2()-wholeScore.getYellowCard().getAway();
                wholeScore.getYellowCard().setHome(matchStatisticsInfoDetailDTO.getT1());
                wholeScore.getYellowCard().setAway(matchStatisticsInfoDetailDTO.getT2());
                if(period!=null){
                    period.getYellowCard().setHome( period.getYellowCard().getHome()+xT1);
                    period.getYellowCard().setAway( period.getYellowCard().getAway()+xT2);
                }
            }
            if(matchStatisticsInfoDetailDTO.getCode().equals("red_card_score")){
                Integer xT1= matchStatisticsInfoDetailDTO.getT1()-wholeScore.getRedCard().getHome();
                Integer xT2= matchStatisticsInfoDetailDTO.getT2()-wholeScore.getRedCard().getAway();
                wholeScore.getRedCard().setHome(matchStatisticsInfoDetailDTO.getT1());
                wholeScore.getRedCard().setAway(matchStatisticsInfoDetailDTO.getT2());
                if(period!=null){
                    period.getRedCard().setHome( period.getRedCard().getHome()+xT1);
                    period.getRedCard().setAway( period.getRedCard().getAway()+xT2);
                }
            }
            if(matchStatisticsInfoDetailDTO.getCode().equals("corner_score")){
                Integer xT1= matchStatisticsInfoDetailDTO.getT1()-wholeScore.getCorner().getHome();
                Integer xT2= matchStatisticsInfoDetailDTO.getT2()-wholeScore.getCorner().getAway();
                wholeScore.getCorner().setHome(matchStatisticsInfoDetailDTO.getT1());
                wholeScore.getCorner().setAway(matchStatisticsInfoDetailDTO.getT2());
                if(period!=null){
                    period.getCorner().setHome( period.getCorner().getHome()+xT1);
                    period.getCorner().setAway( period.getCorner().getAway()+xT2);
                }
            }
            //dangerous_attack_score shot_on_target_score  shot_off_target_score
            if(matchStatisticsInfoDetailDTO.getCode().equals("dangerous_attack_score")){
                Integer xT1= matchStatisticsInfoDetailDTO.getT1()-wholeScore.getDangerousAttack().getHome();
                Integer xT2= matchStatisticsInfoDetailDTO.getT2()-wholeScore.getDangerousAttack().getAway();
                wholeScore.getDangerousAttack().setHome(matchStatisticsInfoDetailDTO.getT1());
                wholeScore.getDangerousAttack().setAway(matchStatisticsInfoDetailDTO.getT2());
                if(period!=null){
                    period.getDangerousAttack().setHome( period.getDangerousAttack().getHome()+xT1);
                    period.getDangerousAttack().setAway( period.getDangerousAttack().getAway()+xT2);
                }
            }
            if(matchStatisticsInfoDetailDTO.getCode().equals("shot_on_target_score")){
                Integer xT1= matchStatisticsInfoDetailDTO.getT1()-wholeScore.getShotOn().getHome();
                Integer xT2= matchStatisticsInfoDetailDTO.getT2()-wholeScore.getShotOn().getAway();
                wholeScore.getShotOn().setHome(matchStatisticsInfoDetailDTO.getT1());
                wholeScore.getShotOn().setAway(matchStatisticsInfoDetailDTO.getT2());
                if(period!=null){
                    period.getShotOn().setHome( period.getShotOn().getHome()+xT1);
                    period.getShotOn().setAway( period.getShotOn().getAway()+xT2);
                }
            }
            if(matchStatisticsInfoDetailDTO.getCode().equals("shot_off_target_score")){
                Integer xT1= matchStatisticsInfoDetailDTO.getT1()-wholeScore.getShotOff().getHome();
                Integer xT2= matchStatisticsInfoDetailDTO.getT2()-wholeScore.getShotOff().getAway();
                wholeScore.getShotOff().setHome(matchStatisticsInfoDetailDTO.getT1());
                wholeScore.getShotOff().setAway(matchStatisticsInfoDetailDTO.getT2());
                if(period!=null){
                    period.getShotOff().setHome( period.getShotOff().getHome()+xT1);
                    period.getShotOff().setAway( period.getShotOff().getAway()+xT2);
                }
            }
            //加时  extra_time_score
            if(matchStatisticsInfoDetailDTO.getCode().equals("extra_time_score")){
                if(request.getData().getPeriod()==41){
                    if(period!=null){
                        period.getGoal().setHome(matchStatisticsInfoDetailDTO.getT1());
                        period.getGoal().setAway( matchStatisticsInfoDetailDTO.getT2());
                    }
                }
                if(request.getData().getPeriod()==42){
                    FootballScores period41=allPeriodScores.get(41l);
                    if(period!=null&&period41!=null){
                        period.getGoal().setHome(matchStatisticsInfoDetailDTO.getT1()-period41.getGoal().getHome());
                        period.getGoal().setAway( matchStatisticsInfoDetailDTO.getT2()-period41.getGoal().getAway());
                    }
                }
            }
            //点球  penalty_shootout
            if(matchStatisticsInfoDetailDTO.getCode().equals("penalty_shootout")){
                if(period!=null){
                    period.getGoal().setHome(matchStatisticsInfoDetailDTO.getT1());
                    period.getGoal().setAway(matchStatisticsInfoDetailDTO.getT2());
                }
            }
            //保存比分数据记录
        }

        period.countFaCard();
        wholeScore.countFaCard();
        period.doShot();
        wholeScore.doShot();
        matchScoresInfo.setT1(wholeScore.getGoal().getHome());
        matchScoresInfo.setT2(wholeScore.getGoal().getAway());
        matchScoresInfo.setSecondsMatchStart(request.getData().getSecondsMatchStart().longValue());
//        matchScoresInfo.setPeriodT1(period.getGoal().getHome());
//        matchScoresInfo.setPeriodT2(period.getGoal().getAway());
        //2.变更入库
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }
}
