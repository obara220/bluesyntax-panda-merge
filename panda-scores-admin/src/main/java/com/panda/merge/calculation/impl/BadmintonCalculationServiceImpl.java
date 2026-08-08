package com.panda.merge.calculation.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.constant.SportTypeEnum;
import com.panda.merge.dto.*;
import com.panda.merge.dto.scores.StandardMatchSwitchDTO;
import com.panda.merge.dto.scores.StandardScoreCenter;
import com.panda.merge.dto.scores.StandardScoreDTO;
import com.panda.merge.dto.sourceSwitch.TennisSwitch;
import com.panda.merge.model.*;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

/**
 * 羽毛球事件比分中心
 */
@Slf4j
@Service
public class BadmintonCalculationServiceImpl  extends AbstractCalculationServiceImpl {
    @Autowired
    MatchScoreInfoRepository matchScoreInfoRepository;
    @Override
    public void calculationMatchScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) throws Exception {
        if(!SportPeriodConstant.BadmintonPeriod.contans(data.getMatchPeriodId())){
            return;
        }
        String scoreStr=matchScoresInfo.getScoresJson();
        if(StringUtils.isEmpty(scoreStr)){
            createScores(matchScoresInfo,data);
        }else {
            updateScores(matchScoresInfo,data);
        }
    }

    private void updateScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        log.info("开始更新羽毛球比分:"+data.getThirdMatchSourceId()+"linkId:"+data.getLinkId());
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, BadmintonScores> allPeriodScores= JsonMapUtils.parseBadmintonMap(periodFootballScores);
        BadmintonScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        if(wholeSores==null|| data.getMatchPeriodId()==null){
            log.error("updateScores wholeSores==null|| data.getMatchPeriodId()==null ThirdMatchSourceId:"+data.getThirdMatchSourceId()+"matchid:"+matchScoresInfo.getThirdMatchId());
            return;
        }
        BadmintonScores periodScores= allPeriodScores.get(data.getMatchPeriodId());
        if(periodScores==null){
            periodScores= new BadmintonScores();
            allPeriodScores.put(data.getMatchPeriodId(),periodScores);
        }
        periodScores.doCalculation(data);
        wholeSores.doCalculation(data);
        wholeSores.getSetScore().setHome(0);
        wholeSores.getSetScore().setAway(0);
        for (Map.Entry<Long, BadmintonScores> longTableTennisScoresEntry : allPeriodScores.entrySet()) {
            if(longTableTennisScoresEntry.getKey().equals(WHOLE_MATCH)){
                continue;
            }
            wholeSores.getSetScore().setHome( wholeSores.getSetScore().getHome()+longTableTennisScoresEntry.getValue().getSetScore().getHome());
            wholeSores.getSetScore().setAway( wholeSores.getSetScore().getAway()+longTableTennisScoresEntry.getValue().getSetScore().getAway());
        }
        matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
        matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
        matchScoresInfo.setPeriodT1(periodScores.getSetScore().getHome());
        matchScoresInfo.setPeriodT2(periodScores.getSetScore().getAway());
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
        log.info("结束更新羽毛球比分:"+data.getThirdMatchSourceId()+"linkId:"+data.getLinkId());
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
        Map<Long, BadmintonScores> allPeriodScores= JsonMapUtils.parseBadmintonMap(periodFootballScores);
        BadmintonScores oldSores= allPeriodScores.get(data.getMatchPeriodId());
        oldSores.cancelCalculation( data,data,allPeriodScores);
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    /**
     * 初始化比分
     * @param matchScoresInfo
     * @param data
     */
    private void createScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        Map<Long, BadmintonScores> scoresHashMap= new HashMap<>();
        BadmintonScores badmintonScores=new BadmintonScores();

        scoresHashMap.put(WHOLE_MATCH,badmintonScores);
        scoresHashMap.put(data.getMatchPeriodId(),badmintonScores);

        badmintonScores.doCalculation(data);
        //3.更新比分模板
        scoresHashMap.put(data.getMatchPeriodId(),((JSONObject) JSONObject.toJSON(badmintonScores)).toJavaObject(BadmintonScores.class));

        matchScoresInfo.setScoresJson(JSONObject.toJSONString(scoresHashMap));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
        log.info("createScores 创建羽毛球比分成功"+data.getEventCode()+"事件ID:"+data.getThirdEventId()+",linkId:",data.getLinkId());
    }

    /**
     * 保存赛事统计比分
     * @param matchScoresInfo
     * @param data
     */
    @Override
    public void saveMatchStatisticsScores(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data,StandardMatchInfo standardMatchInfo) {
        if(!SportPeriodConstant.BadmintonPeriod.contans(data.getPeriod().longValue())){
            return;
        }
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
     * 初始化比分统计
     * @param matchScoresInfo
     * @param data
     */
    private void createMatchStatistics(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data) {
        Map<Long, BadmintonScores> periodFootballScores= new HashMap<>();
        BadmintonScores tennisScores=new BadmintonScores();
        periodFootballScores.put(WHOLE_MATCH,tennisScores);
        BadmintonScores  periodScores= new BadmintonScores();
        periodFootballScores.put(data.getPeriod().longValue(),periodScores);
        //更新赛事比分表
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodFootballScores));
        //matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    /**
     * 保存赛事统计详细
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
        Map<Long, BadmintonScores> allPeriodScores= JsonMapUtils.parseBadmintonMap(periodBasketballScores);
        BadmintonScores periodScores=allPeriodScores.get(data.getPeriod().longValue());
        //改当前阶段的盘比分以及总的盘比分
        BadmintonScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        if(periodScores==null){
            periodScores= new BadmintonScores();
            allPeriodScores.put(data.getPeriod().longValue(),periodScores);
        }
        Long maxPeriod =0L;
        for (MatchStatisticsInfoDetailDTO dto : data.getMatchStatisticsInfoDetailList()) {
            if(dto.getCode().equals("set_score")){
                //改对应阶段的局比分
                Long   periodId=  SportPeriodConstant.BadmintonPeriod.getWholePeriodsByMatchLength()[dto.getFirstNum()-1];
                BadmintonScores setScores=allPeriodScores.get(periodId);
                if(setScores==null){
                    setScores= new BadmintonScores();
                    allPeriodScores.put(periodId,setScores);
                }
                setScores.getSetScore().setHome(dto.getT1());
                setScores.getSetScore().setAway(dto.getT2());
                if(maxPeriod<periodId){
                    maxPeriod=periodId;
                    matchScoresInfo.setPeriodT1(setScores.getSetScore().getHome());
                    matchScoresInfo.setPeriodT2(setScores.getSetScore().getAway());
                }

            }else if(dto.getCode().equals("match_score")){
                Integer addHome = dto.getT1() -wholeSores.getMatchScore().getHome();
                Integer addAway = dto.getT2() -wholeSores.getMatchScore().getAway();
                wholeSores.getMatchScore().setHome(dto.getT1());
                wholeSores.getMatchScore().setAway(dto.getT2());
                periodScores.getMatchScore().setHome(periodScores.getMatchScore().getHome()+addHome);
                periodScores.getMatchScore().setAway(periodScores.getMatchScore().getAway()+addAway);
                matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
                matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
            }
        }
//        wholeSores.getSetScore().setHome(periodScores.getSetScore().getHome());
//        wholeSores.getSetScore().setAway(periodScores.getSetScore().getAway());
        wholeSores.getSetScore().setHome(0);
        wholeSores.getSetScore().setAway(0);
        for (Map.Entry<Long, BadmintonScores> longTableTennisScoresEntry : allPeriodScores.entrySet()) {
            if(longTableTennisScoresEntry.getKey().equals(WHOLE_MATCH)){
                continue;
            }
            wholeSores.getSetScore().setHome( wholeSores.getSetScore().getHome()+longTableTennisScoresEntry.getValue().getSetScore().getHome());
            wholeSores.getSetScore().setAway( wholeSores.getSetScore().getAway()+longTableTennisScoresEntry.getValue().getSetScore().getAway());
        }
        //2.变更入库
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);

    }
    @Override
    public void calcStandardMatchScores(MatchScoresInfo matchScoresInfo, StandardMatchScores score, MatchEventInfo data){
        String scoresJson = matchScoresInfo.getScoresJson();
        Map<Long, BadmintonScores> allPeriodScores = JSON.parseObject(scoresJson, new TypeReference<Map<Long, BadmintonScores>>() {
        });
        BadmintonScores thirdWholeSores= allPeriodScores.get(WHOLE_MATCH);
        Map<Long, BadmintonScores> standardScores = new HashMap<>();
        //标准比分为空，直接复制三方比分
        if (!StringUtils.isEmpty(score.getScoreJson())) {
            standardScores = JSON.parseObject(score.getScoreJson(), new TypeReference<Map<Long, BadmintonScores>>() {
            });
        }
        try{
            String sourceSwitchJson = score.getDataSourceAccoSwitch();
            TennisSwitch tennisSwitch = new TennisSwitch();
            if (StringUtils.isNotEmpty(sourceSwitchJson)) {
                tennisSwitch = JSONObject.parseObject(sourceSwitchJson, TennisSwitch.class);
            }
            //每次执行当前阶段的比分同步
            this.setPeriodScores(standardScores, allPeriodScores, tennisSwitch,data.getMatchPeriodId());
            StandardMatchInfo match = standardMatchInfoRepository.selectStandardMatchPrimaryKey(score.getMatchId());
            calcWholeScores(standardScores,match.getMatchPeriodId());
//            standardScores.put(WHOLE_MATCH,standardScores);
        }catch (Exception e){
            log.error("计算标准比分错误:{}",data.getLinkId(),e);
        }
        //保存
        scoresJson = JSONUtil.toJsonStr(standardScores);
        score.setScoreJson(scoresJson);
    }

    private void setPeriodScores(Map<Long, BadmintonScores> standardScores, Map<Long, BadmintonScores> allPeriodScores, TennisSwitch tennisSwitch, Long periodId) {
        BadmintonScores soresSource= allPeriodScores.get(periodId);
        if(soresSource==null) {
            log.info("复制羽毛球阶段比分,三方阶段比分为空 {}",periodId);
            return;
        }
        if(standardScores.get(periodId)==null){
            standardScores.put(periodId,new BadmintonScores());
        }
        for(Map.Entry<Long, BadmintonScores> entry : allPeriodScores.entrySet()){
            Long scoresPperiod=changePeriodByExtryPeriodEvent(entry.getKey());
            if(scoresPperiod==8L && tennisSwitch.getFirstSwitch()==1){
                standardScores.put(8L,allPeriodScores.get(scoresPperiod));
            }else if(scoresPperiod==9L && tennisSwitch.getSecondSwitch()==1){
                standardScores.put(9L,allPeriodScores.get(scoresPperiod));
            }else if(scoresPperiod==10L && tennisSwitch.getThirdSwitch()==1){
                standardScores.put(10L,allPeriodScores.get(scoresPperiod));
            }else if(scoresPperiod==11L && tennisSwitch.getFourSwitch()==1){
                standardScores.put(11L,allPeriodScores.get(scoresPperiod));
            }else if(scoresPperiod==12L && tennisSwitch.getFifSwitch()==1){
                standardScores.put(12L,allPeriodScores.get(scoresPperiod));
            }
        }

    }

    private Long changePeriodByExtryPeriodEvent(Long periodId) {
        if(periodId==301L){
            return 8L;
        }else if(periodId==302L){
            return 9L;
        }else if(periodId==303L){
            return 10L;
        }else if(periodId==304L){
            return 11L;
        }else if(periodId==305L){
            return 12L;
        }else if(periodId==306L){
            return 441L;
        }else if(periodId==307L){
            return 442L;
        }else{
            return periodId;
        }
    }

    /**
     * 修改标准比分
     * @param scores
     * @return
     */
    public Response editStandScores(StandardScoreCenter scores,StandardMatchScores standardMatchScores,StandardMatchInfo match){

        if(scores.getScores()==null || scores.getScores().isEmpty()){
            return Response.failed("比分为空");
        }
        String scoresJson = standardMatchScores.getScoreJson();
        Map<Long, BadmintonScores> allPeriodScores = new HashMap<>();
        if(StringUtils.isNotBlank(scoresJson)) {
            JSONObject periodFootballScores = JSONObject.parseObject(scoresJson);
            allPeriodScores = JsonMapUtils.parseBadmintonMap(periodFootballScores);
        }
        Integer rtnFlag = checkEditScores(scores,match.getMatchLength(),match.getMatchPeriodId());
        if(rtnFlag!=0){
            return Response.failed(rtnFlag.toString());
        }
        //要修改的比分
        List<StandardScoreDTO> editScores = scores.getScores();
        //赛盘
        int home = 0;
        int away = 0;
        //总局数
        int tgHome = 0;
        int tgAway = 0;
        for (StandardScoreDTO score : editScores) {
            //当主队和客队都为空时，不处理当前阶段
            if(score.getHome()==null && score.getAway()==null){
                continue;
            }
            //-1或者0不处理，由后台统计
            if(score.getPeriodId() == -1 || score.getPeriodId() == 0){
                continue;
            }
            //当主队或者客队为空时，另一个队比分为0
            if(score.getHome()==null || score.getAway()==null){
                if(score.getHome()==null){
                    score.setHome(0);
                }else{
                    score.setAway(0);
                }
            }
            if(score.getHome()>= 21+1 || score.getAway() >= 21+1 ){
                if(score.getHome()>score.getAway() ){
                    home = home +1;
                }else{
                    away = away +1;
                }
            }else{
                if(score.getHome()>score.getAway()){
                    if(score.getHome()>=21 && score.getHome() - score.getAway() >= 2){
                        home = home +1;
                    }
                }else if (score.getHome()<score.getAway()){
                    if(score.getAway()>=21 && score.getAway() - score.getHome() >= 2){
                        away = away +1;
                    }
                }
            }
            tgHome+=score.getHome();
            tgAway+=score.getAway();
            CommonItem scoreItem = new CommonItem();
            scoreItem.setHome(score.getHome());
            scoreItem.setAway(score.getAway());
            BadmintonScores ts = new BadmintonScores();
            ts.setSetScore(scoreItem);
            allPeriodScores.put(score.getPeriodId(),ts);
        }
        //总比分
        CommonItem scoreItem = new CommonItem();
        scoreItem.setHome(home);
        scoreItem.setAway(away);
        BadmintonScores bs = new BadmintonScores();
        bs.setMatchScore(scoreItem);
        CommonItem setScore = new CommonItem();
        //总局数
        setScore.setHome(tgHome);
        setScore.setAway(tgAway);
        bs.setSetScore(setScore);
        allPeriodScores.put(-1L,bs);
        standardMatchScores.setScoreJson(JSONUtil.toJsonStr(allPeriodScores));

        //数据源开关联动
        this.setSwitch(standardMatchScores,scoresJson,scores);
        super.updateEndSendScoresInfo(standardMatchScores,match);
        //添加日志
        super.editScoreCenterSettleLog(scoresJson,standardMatchScores,scores,null);
        return Response.success();
    }

    private void setSwitch(StandardMatchScores standardMatchScores, String scoresJson,StandardScoreCenter scores) {
        //获取修改后前端传的比分
        JSONObject periodScores = JSONObject.parseObject(standardMatchScores.getScoreJson());
        Map<Long,BadmintonScores> newScores = JsonMapUtils.parseBadmintonMap(periodScores);
        if(StrUtil.isEmpty(scoresJson)){
            Map<Long, BadmintonScores> wholePeriodScores= new HashMap<>();
            BadmintonScores badScores=new BadmintonScores();
            wholePeriodScores.put(WHOLE_MATCH,badScores);
            //无比分时初始化比分,用于做开关自动关闭的校验
            scoresJson = JSONObject.toJSONString(wholePeriodScores);
        }
        //获取修改前数据库的比分
        JSONObject oldScores = JSONObject.parseObject(scoresJson);
        Map<Long,BadmintonScores> allPeriodScores2 = JsonMapUtils.parseBadmintonMap(oldScores);
        if(allPeriodScores2.isEmpty()){
            allPeriodScores2 = new HashMap<>();
        }
        TennisSwitch accoSwitchs = getSportSwitchsConfig(standardMatchScores, newScores, allPeriodScores2,scores);
        standardMatchScores.setDataSourceAccoSwitch(JSONUtil.toJsonStr(accoSwitchs));
    }

    private TennisSwitch getSportSwitchsConfig(StandardMatchScores standardMatchScores, Map<Long, BadmintonScores> newScores,
                                               Map<Long, BadmintonScores> oldScores,StandardScoreCenter scores) {
        TennisSwitch accoSwitchs = new TennisSwitch();
        //修改前的联动开关串
        if (!StrUtil.isEmpty(standardMatchScores.getDataSourceAccoSwitch())) {
            accoSwitchs = JSONObject.parseObject(standardMatchScores.getDataSourceAccoSwitch(), TennisSwitch.class);
        }
        String matchManageId = standardMatchScores.getMatchManageId();
        StandardMatchSwitchDTO switchDTO = super.setSwitchObj(standardMatchScores,scores);
        for (int i = 0; i < scoreCenterPeriod.size(); i++) {
            Long period = scoreCenterPeriod.get(i);
            //对比修改前后比分,变更开关
            BadmintonScores scoresfor = newScores.get(scoreCenterPeriod.get(i));
            if (scoresfor == null) {
                scoresfor = new BadmintonScores();
            }
            BadmintonScores scoresRea = oldScores.get(scoreCenterPeriod.get(i));
            if(scoresRea==null){
                scoresRea = new BadmintonScores();
            }
            if (!StrUtil.equals(scoresfor.getSetScore().doCountScoreStr(), scoresRea.getSetScore().doCountScoreStr())) {
                switchDTO.setIndex(period.intValue());
                if (period == 8) {
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getFirstSwitch(),matchManageId);
                    accoSwitchs.setFirstSwitch(0);
                } else if (period == 9) {
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getSecondSwitch(),matchManageId);
                    accoSwitchs.setSecondSwitch(0);
                } else if (period == 10) {
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getThirdSwitch(),matchManageId);
                    accoSwitchs.setThirdSwitch(0);
                } else if (period == 11) {
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getFourSwitch(),matchManageId);
                    accoSwitchs.setFourSwitch(0);
                } else if (period == 12) {
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getFifSwitch(),matchManageId);
                    accoSwitchs.setFifSwitch(0);
                }
                log.info("综合球种修改比分联动开关:{}", accoSwitchs);
            }
        }
        return accoSwitchs;
    }


    /**
     * 修改数据源联动开关,同步比分
     * @param matchSwitchDTO
     * @param standardMatchScores
     * @param matchScoresInfo
     */
    @Override
    public Boolean editAccoSwitch(StandardMatchSwitchDTO matchSwitchDTO, StandardMatchScores standardMatchScores, MatchScoresInfo matchScoresInfo,StandardMatchInfo standardMatchInfo) {
        try{
            standardMatchScores.setDataSourceAccoSwitch(super.getSportSwitchByPeriod(matchSwitchDTO,standardMatchScores));
            standardMatchScores.setScoreJson(this.getScoresAsSwitch(standardMatchScores,matchSwitchDTO,matchScoresInfo,standardMatchInfo.getMatchPeriodId()));
            super.updateEndSendScoresInfo(standardMatchScores,standardMatchInfo);
        }catch(Exception e){
            log.error("修改数据源联动开关异常:{}",e);
        }
        return true;

    }

    protected String getScoresAsSwitch(StandardMatchScores standardMatchScores, StandardMatchSwitchDTO matchSwitchDTO, MatchScoresInfo matchScoresInfo,Long matchPeriodId) {
        log.info("修改开关联动同步比分 matchSwitchDTO:{}==========standardMatchScores:{} " +
                "======================matchScoresInfo:{}",matchSwitchDTO,standardMatchScores,matchScoresInfo);
        Map<Long, BadmintonScores> newStandardScores = new HashMap<>();
        if(StrUtil.isNotEmpty(standardMatchScores.getScoreJson())){
            newStandardScores = JSON.parseObject(standardMatchScores.getScoreJson(), new TypeReference<Map<Long, BadmintonScores>>() {
            });
        }
        Map<Long, BadmintonScores> thirdMatchScores = new HashMap<>();
        if(matchScoresInfo!=null && StrUtil.isNotEmpty(matchScoresInfo.getScoresJson())){
            JSONObject periodtableballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
            thirdMatchScores= JsonMapUtils.parseBadmintonMap(periodtableballScores);
        }
        //足球外的其他球种 index字段传阶段值
        int index = matchSwitchDTO.getIndex();
        log.info("修改开关联动同步比分:index:{}",index);
        if(matchSwitchDTO.getStatus()==1){
            Long period = new Long(index);
            BadmintonScores thirdScores = thirdMatchScores.get(period);
            if(thirdScores==null){
                return standardMatchScores.getScoreJson();
            }
            BadmintonScores standScores = newStandardScores.get(period);
            if(standScores==null){
                standScores = new BadmintonScores();
            }
            standScores.setSetScore(thirdScores.getSetScore());
            newStandardScores.put(period,standScores);
        }
        calcWholeScores(newStandardScores,matchPeriodId);
        log.info("修改开关联动同步比分 newStandardScores:{}",newStandardScores);
        return JSON.toJSONString(newStandardScores);
    }

    public void calcWholeScores(Map<Long, BadmintonScores> newStandardScores,Long matchPeriodId){
        Integer tgHome = 0;
        Integer tgAway = 0;
        Integer setHome = 0;
        Integer setAway = 0;
        for (Long periodId : newStandardScores.keySet()) {
            //查询比分时过滤阶段0 -- 脏数据
            if (!scoreCenterPeriod.contains(periodId)) {
                continue;
            }
            BadmintonScores cc = newStandardScores.get(periodId);
            tgHome += cc.getSetScore().getHome();
            tgAway += cc.getSetScore().getAway();
            //当前事件是盘结束阶段
            if(setEndPeriod.contains(matchPeriodId)){
                if (cc.getSetScore().getHome() > cc.getSetScore().getAway()) {
                    setHome = setHome + 1;
                } else if (cc.getSetScore().getHome() < cc.getSetScore().getAway()){
                    setAway = setAway + 1;
                }
                continue;
            }
            if (cc.getSetScore().getHome() >= 21 + 1 || cc.getSetScore().getAway() >= 21 + 1) {
                if (cc.getSetScore().getHome() > cc.getSetScore().getAway()) {
                    if (cc.getSetScore().getHome() - cc.getSetScore().getAway() >= 2) {
                        setHome = setHome + 1;
                    }
                } else {
                    if (cc.getSetScore().getAway() - cc.getSetScore().getHome() >= 2) {
                        setAway = setAway + 1;
                    }
                }
            } else {
                if (cc.getSetScore().getHome() > cc.getSetScore().getAway()) {
                    if (cc.getSetScore().getHome() >= 21 && cc.getSetScore().getHome() - cc.getSetScore().getAway() >= 2) {
                        setHome = setHome + 1;
                    }
                } else if (cc.getSetScore().getHome() < cc.getSetScore().getAway()) {
                    if (cc.getSetScore().getAway() >= 21 && cc.getSetScore().getAway() - cc.getSetScore().getHome() >= 2) {
                        setAway = setAway + 1;
                    }
                }
            }
        }
        if(newStandardScores.get(WHOLE_MATCH)==null){
            newStandardScores.put(WHOLE_MATCH,new BadmintonScores());
        }
        newStandardScores.get(WHOLE_MATCH).setMatchScore(new CommonItem(setHome, setAway));
        newStandardScores.get(WHOLE_MATCH).setSetScore(new CommonItem(tgHome, tgAway));
    }
}
