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
import com.panda.merge.service.StandardMatchInfoService;
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
 * 排球比分计算
 */
@Slf4j
@Service
public class VolleyballCalculationServiceImpl extends AbstractCalculationServiceImpl {

    @Autowired
    MatchScoreInfoRepository matchScoreInfoRepository;
    @Autowired
    StandardMatchInfoService standardMatchInfoService;
    /**
     * 比分计算并入库
     * @param matchScoresInfo
     * @param data
     * @throws Exception
     */
    @Override
    public void calculationMatchScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) throws Exception {
        if(!SportPeriodConstant.VolleyballPeriod.contans(data.getMatchPeriodId())){
            return;
        }
        String scoreStr=matchScoresInfo.getScoresJson();
        if(StringUtils.isEmpty(scoreStr)){
            createScores(matchScoresInfo,data);
        }else {
            updateScores(matchScoresInfo,data);
        }
    }

    /**
     * 保存比分
     * @param matchScoresInfo
     * @param data
     */
    private void updateScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, VolleyballScores> allPeriodScores= JsonMapUtils.parseVolleyballMap(periodFootballScores);
        VolleyballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);

        if(wholeSores==null|| data.getMatchPeriodId()==null){
            log.error("updateScores wholeSores==null|| data.getMatchPeriodId()==null ThirdMatchSourceId:"+data.getThirdMatchSourceId()+"matchid:"+matchScoresInfo.getThirdMatchId());
            return;
        }
        VolleyballScores periodScores= allPeriodScores.get(data.getMatchPeriodId());
        if(periodScores==null){
            periodScores= new VolleyballScores();
            allPeriodScores.put(data.getMatchPeriodId(),periodScores);
        }
        periodScores.updateEvent(data);
        wholeSores.updateEvent(data);

        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
        matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
        matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
        matchScoresInfo.setPeriodT1(periodScores.getSetScore().getHome());
        matchScoresInfo.setPeriodT2(periodScores.getSetScore().getAway());
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
        Map<Long, VolleyballScores> allPeriodScores= JsonMapUtils.parseVolleyballMap(periodFootballScores);
        VolleyballScores oldSores= allPeriodScores.get(data.getMatchPeriodId());
        VolleyballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        if(oldSores==null || wholeSores==null){
            return;
        }
        oldSores.cancelCalculation( data,data);
        wholeSores.cancelCalculation( data,data);
        matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
        matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
        matchScoresInfo.setPeriodT1(wholeSores.getSetScore().getHome());
        matchScoresInfo.setPeriodT2(wholeSores.getSetScore().getAway());
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
        Map<Long, VolleyballScores> allPeriodScores= new HashMap<>();
        VolleyballScores badmintonScores=new VolleyballScores();
        allPeriodScores.put(WHOLE_MATCH,badmintonScores);
        allPeriodScores.put(data.getMatchPeriodId(),badmintonScores);
        badmintonScores.updateEvent(data);
        //3.更新比分模板
        allPeriodScores.put(data.getMatchPeriodId(),((JSONObject) JSONObject.toJSON(badmintonScores)).toJavaObject(VolleyballScores.class));
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
        matchScoresInfo.setT1(badmintonScores.getMatchScore().getHome());
        matchScoresInfo.setT2(badmintonScores.getMatchScore().getAway());
        matchScoresInfo.setPeriodT1(badmintonScores.getSetScore().getHome());
        matchScoresInfo.setPeriodT2(badmintonScores.getSetScore().getAway());
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
     * 初始化比分
     * @param matchScoresInfo
     * @param data
     */
    private void createMatchStatistics(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data) {
        if(!SportPeriodConstant.VolleyballPeriod.contans(data.getPeriod()+0l)){
            return;
        }
        Map<Long, VolleyballScores> periodFootballScores= new HashMap<>();
        VolleyballScores tennisScores=new VolleyballScores();
        periodFootballScores.put(WHOLE_MATCH,tennisScores);
        VolleyballScores  periodScores= new VolleyballScores();
        periodFootballScores.put(data.getPeriod().longValue(),periodScores);
        //更新赛事比分表
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodFootballScores));
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    /**
     * 保存比分
     * @param matchScoresInfo
     * @param data
     */
    private void saveMatchStatistics(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data) {
        //保存比分
        //保存比分
        if(data.getMatchStatisticsInfoDetailList()==null){
            log.error("createMatchStatistics data:null");
            return;
        }
        //1.得到阶段map 转化的
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, VolleyballScores> allPeriodScores= JsonMapUtils.parseVolleyballMap(periodFootballScores);
//        VolleyballScores periodScores=allPeriodScores.get(data.getPeriod()+0l);
        //改当前阶段的盘比分以及总的盘比分
        VolleyballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
//        if(periodScores==null){
//            periodScores= new VolleyballScores();
//            allPeriodScores.put(data.getPeriod()+0l,periodScores);
//        }
        Long maxPeriodId =0L;
        for (MatchStatisticsInfoDetailDTO dto : data.getMatchStatisticsInfoDetailList()) {
            if(dto.getCode().equals("set_score")){
                //改对应阶段的局比分
                Long   periodId=  SportPeriodConstant.VolleyballPeriod.getWholePeriodsByMatchLength()[dto.getFirstNum()-1];
                VolleyballScores setScores=allPeriodScores.get(periodId);
                if(setScores==null){
                    setScores= new VolleyballScores();
                    allPeriodScores.put(periodId,setScores);
                }
                setScores.getSetScore().setHome(dto.getT1());
                setScores.getSetScore().setAway(dto.getT2());
                if(maxPeriodId<periodId){
                    maxPeriodId=periodId;
                    matchScoresInfo.setPeriodT1(setScores.getSetScore().getHome());
                    matchScoresInfo.setPeriodT2(setScores.getSetScore().getAway());
                }
            }else if(dto.getCode().equals("match_score")){
                wholeSores.getMatchScore().setHome(dto.getT1());
                wholeSores.getMatchScore().setAway(dto.getT2());
                matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
                matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
            }
        }
        VolleyballScores setScores=allPeriodScores.get(maxPeriodId);
        wholeSores.getSetScore().setHome(setScores.getSetScore().getHome());
        wholeSores.getSetScore().setAway(setScores.getSetScore().getAway());
        //2.变更入库
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);


    }

    @Override
    public void calcStandardMatchScores(MatchScoresInfo matchScoresInfo, StandardMatchScores score, MatchEventInfo data){

        Integer rountType = 0;
        String scoresJson = matchScoresInfo.getScoresJson();
        Map<Long, VolleyballScores> allPeriodScores = com.alibaba.fastjson.JSON.parseObject(scoresJson, new TypeReference<Map<Long, VolleyballScores>>() {
        });
        VolleyballScores thirdWholeSores= allPeriodScores.get(WHOLE_MATCH);
        Map<Long, VolleyballScores> standardScores = new HashMap<>();
        //标准比分为空，直接复制三方比分
        if (!StringUtils.isEmpty(score.getScoreJson())) {
            standardScores = JSON.parseObject(score.getScoreJson(), new TypeReference<Map<Long, VolleyballScores>>() {
            });
        }
        try{
            String sourceSwitchJson = score.getDataSourceAccoSwitch();
            TennisSwitch tennisSwitch = new TennisSwitch();
            if (StringUtils.isNotEmpty(sourceSwitchJson)) {
                tennisSwitch = JSONObject.parseObject(sourceSwitchJson, TennisSwitch.class);
            }
            //每次执行当前阶段的比分同步
            this.setPeriodScores(standardScores, allPeriodScores, tennisSwitch,data);
            log.info("::{}::计算标准比分,三方阶段::{}",data.getLinkId(),standardScores);
            StandardMatchInfo match = standardMatchInfoService.getItem(score.getMatchId());

            if(match!=null && match.getRoundType() != null){
                rountType = match.getRoundType();
            }
            calcWholeScores(standardScores,rountType,data.getLinkId(), data.getMatchPeriodId());
        }catch (Exception e){
            log.error("计算标准比分错误:{}",data.getLinkId(),e);
        }
        scoresJson = JSONUtil.toJsonStr(standardScores);
        //保存
        log.info("::{}::排球标准比分同步完成,roundType={},scores={}",data.getLinkId(),rountType,scoresJson);
        score.setScoreJson(scoresJson);
    }
    private void setPeriodScores(Map<Long, VolleyballScores> standardScores, Map<Long, VolleyballScores> allPeriodScores, TennisSwitch tennisSwitch,MatchEventInfo data) {
        Long periodId  = data.getMatchPeriodId();
        VolleyballScores soresSource= allPeriodScores.get(periodId);
        if(soresSource==null) {
            log.info("复制排球阶段比分,三方阶段比分为空 {}",periodId);
            return;
        }
        if(standardScores.get(periodId)==null){
            standardScores.put(periodId,new VolleyballScores());
        }
        for(Map.Entry<Long, VolleyballScores> entry : allPeriodScores.entrySet()){
            log.info("::{}::排球同步标准比分：{}，{}",data.getLinkId(),entry.getKey(),entry.getValue());
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
    public Response editStandScores(StandardScoreCenter scores,StandardMatchScores standardMatchScores,StandardMatchInfo standardMatchInfo){
        if(scores.getScores()==null || scores.getScores().isEmpty()){
            return Response.failed("getScores().isEmpty()");
        }
        String scoresJson = standardMatchScores.getScoreJson();
        Map<Long, VolleyballScores> allPeriodScores = new HashMap<>();
        if(StringUtils.isNotBlank(scoresJson)) {
            JSONObject periodFootballScores = JSONObject.parseObject(scoresJson);
            allPeriodScores = JsonMapUtils.parseVolleyballMap(periodFootballScores);
        }
        Integer rtnFlag = checkEditScores(scores,standardMatchInfo.getMatchLength(),standardMatchInfo.getMatchPeriodId());
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
            if(standardMatchInfo.getSportId() == SportTypeEnum.VOLLEYBALL.getValue() && standardMatchInfo.getRoundType()==5 && score.getPeriodId()==12L){
                //针对排球 排球赛制五局三胜，打到第五局的时候，比分 15分且胜2局，不需要达到25分，盘比分计算逻辑还是有点问题呢
                if(score.getHome()> score.getAway() && score.getHome() >= 15){
                    if(score.getHome() - score.getAway()>=2){
                        home = home +1;
                    }
                }else if(score.getAway()> score.getHome() && score.getAway() >= 15){
                    if(score.getAway() - score.getHome()>=2){
                        away = away +1;
                    }
                }
            }else{
                if(score.getHome()>score.getAway()){
                    if(score.getHome()>=25 && score.getHome() - score.getAway() >= 2){
                        home = home +1;
                    }
                }else if (score.getHome()<score.getAway()){
                    if(score.getAway()>=25 && score.getAway() - score.getHome() >= 2){
                        away = away +1;
                    }
                }
            }
//            if(score.getHome()>score.getAway()){
//                home = home +1;
//            }else if (score.getAway()>score.getHome()){
//                away = away +1;
//            }
            tgHome+=score.getHome();
            tgAway+=score.getAway();
            CommonItem scoreItem = new CommonItem();
            scoreItem.setHome(score.getHome());
            scoreItem.setAway(score.getAway());
            VolleyballScores ts = new VolleyballScores();
            ts.setSetScore(scoreItem);
            //页面按顺序传的8 9 10 11 12 13 14，转换对应的赛事标准比分阶段
            if(score.getPeriodId()==13)score.setPeriodId(441L);
            if(score.getPeriodId()==14)score.setPeriodId(442L);
            allPeriodScores.put(score.getPeriodId(),ts);
        }
        //总比分
        CommonItem scoreItem = new CommonItem();
        scoreItem.setHome(home);
        scoreItem.setAway(away);
        VolleyballScores tts = new VolleyballScores();
        tts.setMatchScore(scoreItem);
        CommonItem setScore = new CommonItem();
        //总局数
        setScore.setHome(tgHome);
        setScore.setAway(tgAway);
        tts.setSetScore(setScore);
        allPeriodScores.put(-1L,tts);
        standardMatchScores.setScoreJson(JSONUtil.toJsonStr(allPeriodScores));
        //数据源开关联动
        this.setSwitch(standardMatchScores,scoresJson,scores);
        super.updateEndSendScoresInfo(standardMatchScores,standardMatchInfo);
        //添加日志
        super.editScoreCenterSettleLog(scoresJson,standardMatchScores,scores,null);
        return Response.success();
    }

    private void setSwitch(StandardMatchScores standardMatchScores, String scoresJson,StandardScoreCenter scores) {
        //获取修改后前端传的比分
        JSONObject periodScores = JSONObject.parseObject(standardMatchScores.getScoreJson());
        Map<Long,VolleyballScores> newScores = JsonMapUtils.parseVolleyballMap(periodScores);
        if(StrUtil.isEmpty(scoresJson)){
            Map<Long, VolleyballScores> wholePeriodScores= new HashMap<>();
            VolleyballScores badScores=new VolleyballScores();
            wholePeriodScores.put(WHOLE_MATCH,badScores);
            //无比分时初始化比分,用于做开关自动关闭的校验
            scoresJson = JSONObject.toJSONString(wholePeriodScores);
        }
        //获取修改前数据库的比分
        JSONObject oldScores = JSONObject.parseObject(scoresJson);
        Map<Long,VolleyballScores> allPeriodScores2 = JsonMapUtils.parseVolleyballMap(oldScores);
        if(allPeriodScores2.isEmpty()){
            return;
        }
        TennisSwitch accoSwitchs = getSportSwitchsConfig(standardMatchScores, newScores, allPeriodScores2,scores);
        standardMatchScores.setDataSourceAccoSwitch(JSONUtil.toJsonStr(accoSwitchs));
    }

    private  TennisSwitch getSportSwitchsConfig(StandardMatchScores standardMatchScores, Map<Long, VolleyballScores> newScores,
                                                Map<Long, VolleyballScores> oldScores,StandardScoreCenter scores) {
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
            VolleyballScores scoresfor = newScores.get(period);
            VolleyballScores scoresRea = oldScores.get(period);
            if(scoresfor==null && scoresRea==null){
                continue;
            }
            if (scoresfor == null) {
                scoresfor = new VolleyballScores();
            }
            if (scoresRea == null) {
                scoresRea = new VolleyballScores();
            }
            log.info("matchManageId::{}::period={},scoresfor={},scoresRea={}",matchManageId,period,scoresfor.getSetScore().doCountScoreStr(), scoresRea.getSetScore().doCountScoreStr());
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
                } else if (period == 13 || period == 441 ) {
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getSixSwitch(),matchManageId);
                    accoSwitchs.setSixSwitch(0);
                } else if (period == 14 || period == 442) {
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getSevenSwitch(),matchManageId);
                    accoSwitchs.setSevenSwitch(0);
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
            standardMatchScores.setScoreJson(this.getScoresAsSwitch(standardMatchScores,matchSwitchDTO,matchScoresInfo,standardMatchInfo.getRoundType(),standardMatchInfo.getMatchPeriodId()));
            super.updateEndSendScoresInfo(standardMatchScores,standardMatchInfo);
        }catch(Exception e){
            log.error("修改数据源联动开关异常:{}",e);
        }
        return true;

    }

    protected String getScoresAsSwitch(StandardMatchScores standardMatchScores, StandardMatchSwitchDTO matchSwitchDTO, MatchScoresInfo matchScoresInfo,Integer roundType,Long matchPeriodId) {
        log.info("修改开关联动同步比分 matchSwitchDTO:{}==========standardMatchScores:{} " +
                "======================matchScoresInfo:{}",matchSwitchDTO,standardMatchScores,matchScoresInfo);
        Map<Long, VolleyballScores> newStandardScores = new HashMap<>();
        if(StrUtil.isNotEmpty(standardMatchScores.getScoreJson())){
            newStandardScores = JSON.parseObject(standardMatchScores.getScoreJson(), new TypeReference<Map<Long, VolleyballScores>>() {
            });
        }
        Map<Long, VolleyballScores> thirdMatchScores = new HashMap<>();
        if(matchScoresInfo!=null && StrUtil.isNotEmpty(matchScoresInfo.getScoresJson())){
            JSONObject periodtableballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
            thirdMatchScores= JsonMapUtils.parseVolleyballMap(periodtableballScores);
        }
        //足球外的其他球种 index字段传阶段值
        int index = matchSwitchDTO.getIndex();
        log.info("修改开关联动同步比分:index:{}",index);
        if(matchSwitchDTO.getStatus()==1){
            Long period = new Long(index);
            VolleyballScores thirdScores = thirdMatchScores.get(period);
            if(thirdScores==null){
                return standardMatchScores.getScoreJson();
            }
            VolleyballScores standScores = newStandardScores.get(period);
            if(standScores==null){
                standScores = new VolleyballScores();
            }
            standScores.setSetScore(thirdScores.getSetScore());
            newStandardScores.put(period,standScores);
        }
        calcWholeScores(newStandardScores,roundType,standardMatchScores.getMatchId()+"",matchPeriodId);
    log.info("修改开关联动同步比分 newStandardScores:{}",newStandardScores);
    return JSON.toJSONString(newStandardScores);
}

    public void calcWholeScores(Map<Long, VolleyballScores> newStandardScores,Integer roundType,String linkId,Long matchPeriodId){
        Integer tgHome = 0;
        Integer tgAway = 0;
        Integer setHome = 0;
        Integer setAway = 0;
        log.info("::{}::排球标准比分同步,roundType={},同步前比分：{}",linkId,roundType,newStandardScores.get(WHOLE_MATCH));
        for (Long periodId : newStandardScores.keySet()) {
            //查询比分时过滤阶段0 -- 脏数据
            if (!scoreCenterPeriod.contains(periodId)) {
                continue;
            }
            VolleyballScores cc = newStandardScores.get(periodId);
            tgHome += cc.getSetScore().getHome();
            tgAway += cc.getSetScore().getAway();
            log.info("::{}::排球标准比分同步,roundType={}",linkId,roundType);
            if(setEndPeriod.contains(matchPeriodId)){
                if (cc.getSetScore().getHome() > cc.getSetScore().getAway()) {
                    setHome = setHome + 1;
                } else if (cc.getSetScore().getHome() < cc.getSetScore().getAway()){
                    setAway = setAway + 1;
                }
                continue;
            }
            if (cc.getSetScore().getHome() >= 25 + 1 || cc.getSetScore().getAway() >= 25 + 1) {
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
                if (roundType >= 5 && periodId >= 12L) {
                    log.info("::{}::排球标准比分同步1,roundType={},periodId={}，{}:{}",linkId,roundType,periodId,cc.getSetScore().getHome(),cc.getSetScore().getAway());
                    //针对排球 排球赛制五局三胜，打到第五局的时候，比分 15分且胜2局，不需要达到25分，盘比分计算逻辑还是有点问题呢
                    if (cc.getSetScore().getHome() > cc.getSetScore().getAway() && cc.getSetScore().getHome() >= 15) {
                        if (cc.getSetScore().getHome() - cc.getSetScore().getAway() >= 2) {
                            setHome = setHome + 1;
                        }
                    } else if (cc.getSetScore().getAway() > cc.getSetScore().getHome() && cc.getSetScore().getAway() >= 15) {
                        if (cc.getSetScore().getAway() - cc.getSetScore().getHome() >= 2) {
                            setAway = setAway + 1;
                        }
                    }
                } else {
                    log.info("::{}::排球标准比分同步2,roundType={},periodId={}，{}:{}",linkId,roundType,periodId,cc.getSetScore().getHome(),cc.getSetScore().getAway());
                    if (cc.getSetScore().getHome() > cc.getSetScore().getAway()) {
                        if (cc.getSetScore().getHome() >= 25 && cc.getSetScore().getHome() - cc.getSetScore().getAway() >= 2) {
                            setHome = setHome + 1;
                        }
                    } else if (cc.getSetScore().getHome() < cc.getSetScore().getAway()) {
                        if (cc.getSetScore().getAway() >= 25 && cc.getSetScore().getAway() - cc.getSetScore().getHome() >= 2) {
                            setAway = setAway + 1;
                        }
                    }
                }
            }
        }
        if(newStandardScores.get(WHOLE_MATCH)==null){
            newStandardScores.put(WHOLE_MATCH,new VolleyballScores());
        }
        log.info("::{}::排球标准比分同步,roundType={},setHome={},setAway={},tgHome={},setAway={}",linkId,roundType,setHome,setAway,tgHome,setAway);
        newStandardScores.get(WHOLE_MATCH).setMatchScore(new CommonItem(setHome, setAway));
        newStandardScores.get(WHOLE_MATCH).setSetScore(new CommonItem(tgHome, tgAway));
        log.info("::{}::排球标准比分同步,roundType={},同步后比分：{}",linkId,roundType,newStandardScores.get(WHOLE_MATCH));

    }
}
