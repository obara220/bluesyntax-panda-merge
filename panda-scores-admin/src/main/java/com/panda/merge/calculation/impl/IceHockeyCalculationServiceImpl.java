package com.panda.merge.calculation.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.dto.CommonItem;
import com.panda.merge.dto.IceHockeyScores;
import com.panda.merge.dto.MatchStatisticsInfoDTO;
import com.panda.merge.dto.MatchStatisticsInfoDetailDTO;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

/**
 * 冰球赛事比分中心计算并入库
 */
@Slf4j
@Service
public class IceHockeyCalculationServiceImpl  extends AbstractCalculationServiceImpl {

//    @Autowired
//    MatchScoresInfoDetailCommonMapper matchScoresInfoDetailCommonMapper;
//    @Autowired
//    MatchScoresInfoMapper matchScoresInfoMapper;
////    @Autowired
////    MatchEventInfoMapper matchEventInfoMapper;
//    @Autowired
//    MatchScoresSpecialEventMapper matchScoresSpecialEventMapper;
    @Autowired
    MatchScoreInfoRepository matchScoreInfoRepository;
    /**
     * 计算比分处理
     * @param matchScoresInfo
     * @param data
     * @throws Exception
     */
    @Override
    public void calculationMatchScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) throws Exception {

        String scoreStr=matchScoresInfo.getScoresJson();
        if(StringUtils.isEmpty(scoreStr)){
            createScores(matchScoresInfo,data);
        }else {
            updateScores(matchScoresInfo,data);
        }
    }

    /**
     * 更新比分
     * @param matchScoresInfo
     * @param data
     */
    private void updateScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        if(!SportPeriodConstant.IceHockeyPeriod.contans(data.getMatchPeriodId())){
            return;
        }
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long,IceHockeyScores> allPeriodScores= JsonMapUtils.parseIceHockeyMap(periodFootballScores);
        IceHockeyScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        if(data.getMatchPeriodId() ==50L || data.getMatchPeriodId() ==120L){
            updateMatchScoreByPenalty( data,matchScoresInfo,allPeriodScores);
            return;
        }
        if(data.getMatchPeriodId()==999L){
            return;
        }
        if(wholeSores==null|| data.getMatchPeriodId()==null){
            log.error("updateScores wholeSores==null|| data.getMatchPeriodId()==null ThirdMatchSourceId:"+data.getThirdMatchSourceId()+"matchid:"+matchScoresInfo.getThirdMatchId());
            return;
        }
        IceHockeyScores periodScores= allPeriodScores.get(data.getMatchPeriodId());
        if(periodScores==null){
            periodScores =new IceHockeyScores();
            allPeriodScores.put(data.getMatchPeriodId(),periodScores);
        }
        wholeSores.updateEvent(data,allPeriodScores);
        matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
        matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
        matchScoresInfo.setPeriodT1(periodScores.getMatchScore().getHome());
        matchScoresInfo.setPeriodT2(periodScores.getMatchScore().getAway());
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    /**
     * 更新比分
     * @param data
     * @param matchScoresInfo
     * @param allPeriodScores
     */
    private void updateMatchScoreByPenalty(MatchEventInfo data,MatchScoresInfo matchScoresInfo,Map<Long, IceHockeyScores> allPeriodScores) {
        IceHockeyScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        if(wholeSores==null){
            return;
        }
        IceHockeyScores penaltySores= allPeriodScores.get(50l);
        if(penaltySores==null){
            penaltySores =new IceHockeyScores();
            allPeriodScores.put(50L,penaltySores);
        }
        CommonItem penalty =new CommonItem();
        log.info(" updateMatchScoreByPenalty:{} ,eventCode:{},before: home:{},away:{},linkedId:{}",matchScoresInfo.getThirdMatchId(),"penalty->Sores", wholeSores.getMatchScore().getHome(), wholeSores.getMatchScore().getAway(),data.getLinkId());
        //不是999 则更新点球大战比分
//        if(data.getMatchPeriodId() ==50L){
            Integer home =data.getT1()-wholeSores.getMatchScore().getHome();
            Integer away =data.getT2()-wholeSores.getMatchScore().getAway();
            penaltySores.getMatchScore().setHome(home);
            penaltySores.getMatchScore().setAway(away);
            matchScoresInfo.setPeriodT1(home);
            matchScoresInfo.setPeriodT2(away);
        /*}else if(data.getMatchPeriodId() ==120L){
//            penaltySores.setMatchScore(penalty);
            if(penaltySores.getMatchScore().getHome()>penaltySores.getMatchScore().getAway()){
                penalty.setHome(1);
                wholeSores.getMatchScore().setHome(wholeSores.getMatchScore().getHome()+1);
            }else {
                penalty.setAway(1);
                wholeSores.getMatchScore().setAway(wholeSores.getMatchScore().getAway()+1);
            }
            matchScoresInfo.setPeriodT1(penalty.getHome());
            matchScoresInfo.setPeriodT2(penalty.getAway());
            penaltySores.setMatchScore(penalty);
            //是的话则更新主比分
        }else {
            return;
        }*/
        log.info(" updateMatchScoreByPenalty:{} ,eventCode:{},after: home:{},away:{},linkedId:{}",matchScoresInfo.getThirdMatchId(),"penalty->Sores", wholeSores.getMatchScore().getHome(), wholeSores.getMatchScore().getAway(),data.getLinkId());

        matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
        matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());

        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    /**
     * 事件取消
     * @param matchScoresInfo
     * @param data
     * @param isAgain
     * @throws Exception
     */
    @Override
    public void cancelEvent(MatchScoresInfo matchScoresInfo, MatchEventInfo data , boolean isAgain,Boolean isReissue) throws Exception {
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long,IceHockeyScores> allPeriodScores= JsonMapUtils.parseIceHockeyMap(periodFootballScores);
        IceHockeyScores oldSores= allPeriodScores.get(data.getMatchPeriodId());
        IceHockeyScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        if(data.getMatchPeriodId() ==50L || data.getMatchPeriodId() ==120L){
            updateMatchScoreByPenalty( data,matchScoresInfo,allPeriodScores);
            return;
        }
        oldSores.cancelCalculation( data,data,allPeriodScores);
        wholeSores.cancelCalculation( data,data,allPeriodScores);
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    /**
     * 保存比分
     * @param matchScoresInfo
     * @param data
     */
    private void createScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        Map<Long, IceHockeyScores> allPeriodScores= new HashMap<>();
        IceHockeyScores badmintonScores=new IceHockeyScores();
        allPeriodScores.put(WHOLE_MATCH,badmintonScores);
        allPeriodScores.put(data.getMatchPeriodId(),badmintonScores);
        badmintonScores.updateEvent(data,allPeriodScores);
        //3.更新比分模板
        allPeriodScores.put(data.getMatchPeriodId(),((JSONObject) JSONObject.toJSON(badmintonScores)).toJavaObject(IceHockeyScores.class));
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
        log.info("createScores 成功"+data.getEventCode()+"事件ID:"+data.getThirdEventId());
    }

    /**
     * 保存比分统计
     * @param matchScoresInfo
     * @param data
     */
    @Override
    public void saveMatchStatisticsScores(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data, StandardMatchInfo standardMatchInfo) {
        //1.查询数据库的阶段值是否存在
        //1.1 查询 matchScoresInfo 的 json 是否存在 不存在则新建
        if(StringUtils.isEmpty(matchScoresInfo.getScoresJson())){
            createMatchStatistics(matchScoresInfo,data);
        }else {
            //2.如果存在则覆盖值
            saveMatchStatistics(matchScoresInfo,data);
        }
    }

    /**
     * 更新比分统计
     * @param matchScoresInfo
     * @param data
     */
    private void createMatchStatistics(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data) {
        Map<Long, IceHockeyScores> periodFootballScores= new HashMap<>();
        IceHockeyScores tennisScores=new IceHockeyScores();
        periodFootballScores.put(WHOLE_MATCH,tennisScores);
        IceHockeyScores  periodScores= new IceHockeyScores();
        periodFootballScores.put(data.getPeriod().longValue(),periodScores);
        //更新赛事比分表
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodFootballScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    /**
     * 保存比分统计
     * @param matchScoresInfo
     * @param data
     */
    private void saveMatchStatistics(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data) {
        //保存比分
        if(data.getMatchStatisticsInfoDetailList()==null){
            log.error("createMatchStatistics data:null");
            return;
        }
        //1.得到阶段map 转化的
        JSONObject periodBasketballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long,IceHockeyScores> allPeriodScores= JsonMapUtils.parseIceHockeyMap(periodBasketballScores);
        IceHockeyScores periodScores=allPeriodScores.get(data.getPeriod().longValue());
        //改当前阶段的盘比分以及总的盘比分
        IceHockeyScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        if(periodScores==null){
            periodScores= new IceHockeyScores();
            allPeriodScores.put(data.getPeriod().longValue(),periodScores);
        }

        for (MatchStatisticsInfoDetailDTO dto : data.getMatchStatisticsInfoDetailList()) {
            if(dto.getCode().equals("set_score")){
                //改对应阶段的局比分
                Long   periodId=  SportPeriodConstant.TennisPeriod.getWholePeriodsByMatchLength()[dto.getFirstNum()-1];
                IceHockeyScores setScores=allPeriodScores.get(data.getPeriod().longValue());
                if(setScores==null){
                    setScores= new IceHockeyScores();
                    allPeriodScores.put(periodId,setScores);
                }
                setScores.getMatchScore().setHome(dto.getT1());
                setScores.getMatchScore().setAway(dto.getT2());
            }else if(dto.getCode().equals("match_score")){
                Integer addHome = dto.getT1() -wholeSores.getMatchScore().getHome();
                Integer addAway = dto.getT2() -wholeSores.getMatchScore().getAway();
                wholeSores.getMatchScore().setHome(dto.getT1());
                wholeSores.getMatchScore().setAway(dto.getT2());
                periodScores.getMatchScore().setHome(periodScores.getMatchScore().getHome()+addHome);
                periodScores.getMatchScore().setAway(periodScores.getMatchScore().getAway()+addAway);
                matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
                matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
                matchScoresInfo.setPeriodT1(periodScores.getMatchScore().getHome());
                matchScoresInfo.setPeriodT2(periodScores.getMatchScore().getAway());
            }
        }
        //2.变更入库
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);

    }


}
