package com.panda.merge.calculation.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.advertise.service.CommonAdvertiseService;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.dto.*;
import com.panda.merge.dto.scores.StandardMatchSwitchDTO;
import com.panda.merge.dto.scores.StandardScoreCenter;
import com.panda.merge.dto.scores.StandardScoreDTO;
import com.panda.merge.dto.sourceSwitch.TennisSwitch;
import com.panda.merge.model.*;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.repository.StandardMatchInfoRepository;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

    /** 兵乓球比分计算并入库
     *
     * @author idol
     * @version 1.0<br>
     * @taskId: <br>
     * @createDate 2022-2-26 17:06:27
     * @see com.panda.merge.calculation.impl
        */

@Slf4j
@Service
public class TableTennisCalculationServiceImpl extends AbstractCalculationServiceImpl {
    @Autowired
    MatchScoreInfoRepository matchScoreInfoRepository;
    @Autowired
    CommonAdvertiseService commonAdvertiseService;
    /**
     * 比分计算入库
     * @param matchScoresInfo
     * @param data
     * @throws Exception
    */
    @Override
    public void calculationMatchScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) throws Exception {
        if(!SportPeriodConstant.TableTennisPeriod.contans(data.getMatchPeriodId())){
            return;
        }
        String scoreStr = matchScoresInfo.getScoresJson();
        if (StringUtils.isEmpty(scoreStr)) {
            createScores(matchScoresInfo, data);
        } else {
            updateScores(matchScoresInfo, data);
        }
        //添加乒乓球进球比分事件的缓存，用来校验顺序，处理延迟问题
        if("table_tennis_score_change".equals(data.getEventCode())){
            String tableTennisScoreKey = "table_tennis_"+data.getThirdMatchId()+"_"+data.getEventCode();
            redisService.set(tableTennisScoreKey,data, RedisConfig.REDIS_HOUR_TIME);
        }
    }

    /**
     * 更新比分
     * @param matchScoresInfo
     * @param data
     */
    private void updateScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        log.info(data.getLinkId()+"  updateScores wholeSores==null|| data.getMatchPeriodId()==null ThirdMatchSourceId:"+data.getThirdMatchSourceId()+"matchid:"+matchScoresInfo.getThirdMatchId());
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, TableTennisScores> allPeriodScores= JsonMapUtils.parseTableTennisMap(periodFootballScores);
        TableTennisScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        if(wholeSores==null|| data.getMatchPeriodId()==null){
            log.error("updateScores wholeSores==null|| data.getMatchPeriodId()==null ThirdMatchSourceId:"+data.getThirdMatchSourceId()+"matchid:"+matchScoresInfo.getThirdMatchId());
            return;
        }
        //添加乒乓球进球比分事件的缓存，用来校验顺序，处理延迟问题
        if("table_tennis_score_change".equals(data.getEventCode())){
            String tableTennisScoreKey = "table_tennis_"+data.getThirdMatchId()+"_"+data.getEventCode();
            Object obj = redisService.get(tableTennisScoreKey);
            if(obj!=null){
                //获取上一个进球事件
                MatchEventInfo lastEvent  = JSONUtil.toBean(JSONUtil.toJsonStr(obj), MatchEventInfo.class);
                if(lastEvent.getEventTime()>data.getEventTime()){
                    log.info("linkId:{}  已消费到更新的比分，本次不处理,已消费到事件：{}",lastEvent.getLinkId(),lastEvent.getLinkId());
                    return;
                }
            }
        }
        changeMatchStatus(data);
        TableTennisScores periodScores= allPeriodScores.get(data.getMatchPeriodId());
        if(periodScores==null){
            periodScores =new TableTennisScores();
            allPeriodScores.put(data.getMatchPeriodId(),periodScores);
        }
        periodScores.doCalculation(data,allPeriodScores);
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
        matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
        matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
        matchScoresInfo.setPeriodT1(periodScores.getSetScore().getHome());
        matchScoresInfo.setPeriodT2(periodScores.getSetScore().getAway());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

        /**
         * 装配事件编码和阶段
         * @param data
         */
        private void changeMatchStatus(MatchEventInfo data) {
            if(data.getEventCode().equals("match_status")){
                if(data.getMatchPeriodId().equals(301l)){
                    data.setMatchPeriodId(8l);
                    data.setEventCode("table_tennis_score_change");
                }
                if(data.getMatchPeriodId().equals(302l)){
                    data.setMatchPeriodId(9l);
                    data.setEventCode("table_tennis_score_change");
                }
                if(data.getMatchPeriodId().equals(303L)){
                    data.setMatchPeriodId(10l);
                    data.setEventCode("table_tennis_score_change");
                }
                if(data.getMatchPeriodId().equals(304L)){
                    data.setMatchPeriodId(11l);
                    data.setEventCode("table_tennis_score_change");
                }
                if(data.getMatchPeriodId().equals(305l)){
                    data.setMatchPeriodId(12l);
                    data.setEventCode("table_tennis_score_change");
                }
            }
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
        Map<Long, TableTennisScores> allPeriodScores= JsonMapUtils.parseTableTennisMap(periodFootballScores);
        TableTennisScores oldSores= allPeriodScores.get(data.getMatchPeriodId());
        TableTennisScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        if(oldSores==null || wholeSores==null){
           return;
        }
        oldSores.cancelCalculation( data,data,allPeriodScores);
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
        matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
        matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
        matchScoresInfo.setPeriodT1(wholeSores.getSetScore().getHome());
        matchScoresInfo.setPeriodT2(wholeSores.getSetScore().getAway());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
        //添加乒乓球进球比分事件的缓存，用来校验顺序，处理延迟问题
        if("table_tennis_score_change".equals(data.getEventCode())){
            String tableTennisScoreKey = "table_tennis_"+data.getThirdMatchId()+"_"+data.getEventCode();
            redisService.set(tableTennisScoreKey,data, RedisConfig.REDIS_HOUR_TIME);
        }
    }

        /**
         * 更新比分
         * @param matchScoresInfo
         * @param data
         */
    private void createScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        Map<Long, TableTennisScores> scoresHashMap= new HashMap<>();
        TableTennisScores TableTennisScores=new TableTennisScores();
        scoresHashMap.put(WHOLE_MATCH,TableTennisScores);
        scoresHashMap.put(data.getMatchPeriodId(),TableTennisScores);

        TableTennisScores.doCalculation(data,scoresHashMap);
        //3.更新比分模板
        scoresHashMap.put(data.getMatchPeriodId(),((JSONObject) JSONObject.toJSON(TableTennisScores)).toJavaObject(TableTennisScores.class));

        matchScoresInfo.setScoresJson(JSONObject.toJSONString(scoresHashMap));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
        matchScoresInfo.setT1(TableTennisScores.getMatchScore().getHome());
        matchScoresInfo.setT2(TableTennisScores.getMatchScore().getAway());
        matchScoresInfo.setPeriodT1(TableTennisScores.getSetScore().getHome());
        matchScoresInfo.setPeriodT2(TableTennisScores.getSetScore().getAway());
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
        //1.1 查询 matchScoresInfo 的 json 是否存在 不存在则新建
        if(StringUtils.isEmpty(matchScoresInfo.getScoresJson())){
            createMatchStatistics(matchScoresInfo,data);
        }else {
            //2.如果存在则覆盖值
            saveMatchStatistics(matchScoresInfo,data);
        }
    }

        /**
         * 初始化统计比分
         * @param matchScoresInfo
         * @param data
         */
    private void createMatchStatistics(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data) {
        Map<Long, TableTennisScores> periodFootballScores= new HashMap<>();
        TableTennisScores tennisScores=new TableTennisScores();
        periodFootballScores.put(WHOLE_MATCH,tennisScores);

        TableTennisScores  periodScores= new TableTennisScores();
        periodFootballScores.put(data.getPeriod().longValue(),periodScores);
        //更新赛事比分表
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodFootballScores));
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
        Map<Long, TableTennisScores> allPeriodScores= JsonMapUtils.parseTableTennisMap(periodBasketballScores);
        Integer oldPeriodId = data.getPeriod();
        Long newPeriodId=changePeriodByExtryPeriodEvent(data.getPeriod().longValue());
        data.setPeriod(newPeriodId.intValue());
        TableTennisScores periodScores=allPeriodScores.get(newPeriodId);
//        TableTennisScores periodScores=allPeriodScores.get(data.getPeriod().longValue());
        //改当前阶段的盘比分以及总的盘比分
        TableTennisScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        Long maxPeriod =0l;
        for (MatchStatisticsInfoDetailDTO dto : data.getMatchStatisticsInfoDetailList()) {
            if(dto.getCode().equals("set_score")){
                //改对应阶段的局比分
                Long   periodId=  SportPeriodConstant.TableTennisPeriod.getWholePeriodsByMatchLength()[dto.getFirstNum()-1];
                TableTennisScores setScores=allPeriodScores.get(periodId);
                if(setScores==null){
                    setScores= new TableTennisScores();
                    allPeriodScores.put(periodId,setScores);
                }
                setScores.getSetScore().setHome(dto.getT1());
                setScores.getSetScore().setAway(dto.getT2());
                if(maxPeriod<periodId){
                    maxPeriod=periodId;
                    periodScores=setScores;
                    matchScoresInfo.setPeriodT1(setScores.getSetScore().getHome());
                    matchScoresInfo.setPeriodT2(setScores.getSetScore().getAway());
                }
            }else if(dto.getCode().equals("match_score")){
                Integer addHome = dto.getT1() -wholeSores.getMatchScore().getHome();
                Integer addAway = dto.getT2() -wholeSores.getMatchScore().getAway();
                wholeSores.getMatchScore().setHome(dto.getT1());
                wholeSores.getMatchScore().setAway(dto.getT2());
                if(periodScores!=null){
                    periodScores.getMatchScore().setHome(periodScores.getMatchScore().getHome()+addHome);
                    periodScores.getMatchScore().setAway(periodScores.getMatchScore().getAway()+addAway);
                }
                matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
                matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());

            }
        }
        //2.变更入库
        wholeSores.getSetScore().setHome(0);
        wholeSores.getSetScore().setAway(0);
        for (Map.Entry<Long, TableTennisScores> longTableTennisScoresEntry : allPeriodScores.entrySet()) {
            if(longTableTennisScoresEntry.getKey().equals(WHOLE_MATCH)){
                continue;
            }
            wholeSores.getSetScore().setHome( wholeSores.getSetScore().getHome()+longTableTennisScoresEntry.getValue().getSetScore().getHome());
            wholeSores.getSetScore().setAway( wholeSores.getSetScore().getAway()+longTableTennisScoresEntry.getValue().getSetScore().getAway());
        }
        data.setPeriod(oldPeriodId);
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());

    }
        /**
         * 转换阶段
         * @param periodId
         * @return
         */
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
        @Override
        public void calcStandardMatchScores(MatchScoresInfo matchScoresInfo, StandardMatchScores score, MatchEventInfo data){
            String scoresJson = matchScoresInfo.getScoresJson();
            Map<Long, TableTennisScores> allPeriodScores = com.alibaba.fastjson.JSON.parseObject(scoresJson, new TypeReference<Map<Long, TableTennisScores>>() {
            });
            TableTennisScores thirdWholeSores= allPeriodScores.get(WHOLE_MATCH);
            Map<Long, TableTennisScores> standardScores = new HashMap<>();
            //标准比分为空，直接复制三方比分
            if (!StringUtils.isEmpty(score.getScoreJson())) {
                standardScores = JSON.parseObject(score.getScoreJson(), new TypeReference<Map<Long, TableTennisScores>>() {
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
//                StandardMatchInfo match = standardMatchInfoRepository.selectStandardMatchPrimaryKey(score.getMatchId());
                calcWholeScores(standardScores,data.getMatchPeriodId());
//                standardScores.put(WHOLE_MATCH,thirdWholeSores);
            }catch (Exception e){
                log.error("计算标准比分错误:{}",data.getLinkId(),e);
            }
            //保存
            scoresJson = JSONUtil.toJsonStr(standardScores);
            score.setScoreJson(scoresJson);
        }
        private void setPeriodScores(Map<Long, TableTennisScores> standardScores, Map<Long, TableTennisScores> allPeriodScores, TennisSwitch tennisSwitch, Long periodId) {
            TableTennisScores soresSource= allPeriodScores.get(periodId);
            if(soresSource==null) {
                log.info("复制羽毛球阶段比分,三方阶段比分为空 {}",periodId);
                return;
            }
            if(standardScores.get(periodId)==null){
                standardScores.put(periodId,new TableTennisScores());
            }
            for(Map.Entry<Long, TableTennisScores> entry : allPeriodScores.entrySet()){
                Long scoresPperiod=changePeriodByExtryPeriodEvent(entry.getKey());
                if(scoresPperiod==8L && tennisSwitch.getFirstSwitch()==1){
                    standardScores.put(8L,entry.getValue());
                }else if(scoresPperiod==9L && tennisSwitch.getSecondSwitch()==1){
                    standardScores.put(9L,entry.getValue());
                }else if(scoresPperiod==10L && tennisSwitch.getThirdSwitch()==1){
                    standardScores.put(10L,entry.getValue());
                }else if(scoresPperiod==11L && tennisSwitch.getFourSwitch()==1){
                    standardScores.put(11L,entry.getValue());
                }else if(scoresPperiod==12L && tennisSwitch.getFifSwitch()==1){
                    standardScores.put(12L,entry.getValue());
                }else if(scoresPperiod==441L && tennisSwitch.getSixSwitch()==1){
                    standardScores.put(441L,entry.getValue());
                }else if(scoresPperiod==442L && tennisSwitch.getSevenSwitch()==1){
                    standardScores.put(442L,entry.getValue());
                }
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
            Map<Long, TableTennisScores> allPeriodScores = new HashMap<>();
            if(StringUtils.isNotBlank(scoresJson)) {
                JSONObject periodFootballScores = JSONObject.parseObject(scoresJson);
                allPeriodScores = JsonMapUtils.parseTableTennisMap(periodFootballScores);
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
                if(score.getHome()>= 11+1 || score.getAway() >= 11+1 ){
                    if(score.getHome()>score.getAway() ){
                        home = home +1;
                    }else{
                        away = away +1;
                    }
                }else{
                    if(score.getHome()>score.getAway()){
                        if(score.getHome()>=11 && score.getHome() - score.getAway() >= 2){
                            home = home +1;
                        }
                    }else if (score.getHome()<score.getAway()){
                        if(score.getAway()>= 11 && score.getAway() - score.getHome() >= 2){
                            away = away +1;
                        }
                    }
                }
                tgHome+=score.getHome();
                tgAway+=score.getAway();
                CommonItem scoreItem = new CommonItem();
                scoreItem.setHome(score.getHome());
                scoreItem.setAway(score.getAway());
                TableTennisScores ts = new TableTennisScores();
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
            TableTennisScores tts = new TableTennisScores();
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
            Map<Long,TableTennisScores> newScores = JsonMapUtils.parseTableTennisMap(periodScores);
            if(StrUtil.isEmpty(scoresJson)){
                Map<Long, TableTennisScores> wholePeriodScores= new HashMap<>();
                TableTennisScores tableTennisScores=new TableTennisScores();
                wholePeriodScores.put(WHOLE_MATCH,tableTennisScores);
                //无比分时初始化比分,用于做开关自动关闭的校验
                scoresJson = JSONObject.toJSONString(wholePeriodScores);
            }
            //获取修改前数据库的比分
            JSONObject oldScores = JSONObject.parseObject(scoresJson);
            Map<Long,TableTennisScores> allPeriodScores2 = JsonMapUtils.parseTableTennisMap(oldScores);
            if(allPeriodScores2.isEmpty()){
                allPeriodScores2 = new HashMap<>();
            }
            TennisSwitch accoSwitchs = getSportSwitchsConfig(standardMatchScores, newScores, allPeriodScores2,scores);
            standardMatchScores.setDataSourceAccoSwitch(JSONUtil.toJsonStr(accoSwitchs));
        }

        private  TennisSwitch getSportSwitchsConfig(StandardMatchScores standardMatchScores, Map<Long, TableTennisScores> newScores,
                                                    Map<Long, TableTennisScores> oldScores,StandardScoreCenter scores) {
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
                TableTennisScores scoresfor = newScores.get(period);
                TableTennisScores scoresRea = oldScores.get(period);
                if(scoresfor==null && scoresRea==null){
                    continue;
                }
                if (scoresfor == null) {
                    scoresfor = new TableTennisScores();
                }

                if(scoresRea==null){
                    scoresRea = new TableTennisScores();
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
                    } else if (period == 13 || period == 441 ) {
                        switchDTO.setIndex(13);
                        scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getSixSwitch(),matchManageId);
                        accoSwitchs.setSixSwitch(0);
                    } else if (period == 14 || period == 442) {
                        switchDTO.setIndex(14);
                        scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getSevenSwitch(),matchManageId);
                        accoSwitchs.setSevenSwitch(0);
                    }
                    log.info("综合球种修改比分联动开关:{}", accoSwitchs);
                }
            }
            return accoSwitchs;
        }
        public JSONObject buildStandardMatchScoreHisByMap(Long matchId,String scoreJson,String extraScoreJson,Long matchPeriodId,String linkId) {
            matchPeriodId = changePeriodByExtryPeriodEvent(matchId,matchPeriodId);
            JSONObject periodFootballScores = JSONObject.parseObject(scoreJson);
            Map<Long, TableTennisScores> allPeriodScores= JsonMapUtils.parseTableTennisMap(periodFootballScores);
            TableTennisScores wholeSores= allPeriodScores.get(matchPeriodId);
            if(wholeSores==null){
                log.info("buildStandardMatchScoreHisByMap 阶段错误/无阶段比分。。。。。。。。。{}=={}",matchPeriodId,linkId);
                return null;
            }
            //获取当前阶段的最新局比分
            Integer setHome = wholeSores.getSetScore().getHome();
            Integer setAway = wholeSores.getSetScore().getAway();
            //.定义要求结果
            Map<Long,Map<Integer,Object>> currentScores =new HashMap<>();
            Integer setNum = setHome+setAway;
            log.info("buildStandardMatchScoreHisByMap 开始组装历史比分：{},setNum={}",linkId,setNum);

            if(setNum>=1){
                //局内比分 11-7
                Map<Integer,Object> currentSetScoreMap = new HashMap<>();
                CommonItem currentPeriodScore = new CommonItem();
                currentPeriodScore.setHome(setHome);
                currentPeriodScore.setAway(setAway);
                //当前阶段-8
                currentSetScoreMap.put(setNum,currentPeriodScore);

                if(extraScoreJson==null){
                    currentScores.put(matchPeriodId,currentSetScoreMap);
                    JSONObject jsonObj = JSONObject.parseObject(JSONUtil.toJsonStr(currentScores));
                    return jsonObj;
                }else{
                    JSONObject jsonObj = JSONObject.parseObject(extraScoreJson);
                    //先获取当前阶段的局比分
                    JSONObject currentPeriod = jsonObj.getJSONObject(matchPeriodId+"");
                    if(currentPeriod==null){
                        JSONObject currentPeriodNew = new JSONObject();
                        //当前阶段无局历史比分，直接赋值当前比分， key为1，值通常为0-1或者1-0
                        currentPeriodNew.put(setNum+"",currentPeriodScore);
                        //创建当前阶段map
                        jsonObj.put(matchPeriodId+"",currentPeriodNew);
                        return jsonObj;
                    }else{
                        //当前阶段存在历史局比分
                        currentPeriod.put(setNum+"",currentPeriodScore);
                        jsonObj.put(matchPeriodId+"",currentPeriod);
                        return jsonObj;
                    }
                }
            }

            return null;
        }

        /**
         * 根据阶段获取当前局
         * @param matchPeriodId
         * @return
         */
        private Long changePeriodByExtryPeriodEvent(Long matchId,Long matchPeriodId) {

            if(301L == matchPeriodId){
                return 8L;
            }
            if(302L == matchPeriodId){
                return 9L;
            }
            if(303L == matchPeriodId){
                return 10L;
            }
            if(304L == matchPeriodId){
                return 11L;
            }
            if(305L == matchPeriodId){
                return 12L;
            }
            if(306L == matchPeriodId){
                return 441L;
            }
            if(100L == matchPeriodId || 999L == matchPeriodId){
                StandardMatchInfo match = standardMatchInfoRepository.selectStandardMatchPrimaryKey(matchId);
                if(match==null || match.getRoundType()==null){
                    return 5L;
                }
                switch (match.getRoundType()){
                    case 3:
                        return 10L;
                    case 4:
                        return 11L;
                    case 5:
                        return 12L;
                    case 7:
                        return 442L;
                    }
            }
            return matchPeriodId;
        }

        public  Map<Long, TableTennisScores> calcStandardSetScore(String scoreJson,String linkId) {
            List<Long> periodList = new ArrayList<>(Arrays.asList(8L,9L,10L,11L,12L,441L,442L));

            JSONObject periodFootballScores = JSONObject.parseObject(scoreJson);
            Map<Long, TableTennisScores> allPeriodScores= JsonMapUtils.parseTableTennisMap(periodFootballScores);
            TableTennisScores tableTennisScores = allPeriodScores.get(WHOLE_MATCH);

            Integer home = 0;
            Integer away = 0;
            for (Long peroId : allPeriodScores.keySet()) {
                if(!periodList.contains(peroId)){
                    continue;
                }

                TableTennisScores periodScores = allPeriodScores.get(peroId);
                home = home+periodScores.getSetScore().getHome();
                away = away+periodScores.getSetScore().getAway();
                log.info("计算setScore总分calcStandardSetScore,peroid:{},home:{},away:{},linkId:{}",peroId,home,away,linkId);
            }
            CommonItem setScore = new CommonItem();
            setScore.setHome(home);
            setScore.setAway(away);
            tableTennisScores.setSetScore(setScore);
            log.info("计算setScore总分calcStandardSetScore:{}",JSONUtil.toJsonStr(tableTennisScores));
            return allPeriodScores;
        }


        /**
         * 修改数据源联动开关,同步比分
         * @param matchSwitchDTO
         * @param standardMatchScores
         * @param matchScoresInfo
         */
        @Override
        public Boolean editAccoSwitch(StandardMatchSwitchDTO matchSwitchDTO,
                                      StandardMatchScores standardMatchScores,
                                      MatchScoresInfo matchScoresInfo,
                                      StandardMatchInfo standardMatchInfo) {
            try{
                standardMatchScores.setDataSourceAccoSwitch(super.getSportSwitchByPeriod(matchSwitchDTO,standardMatchScores));
                standardMatchScores.setScoreJson(this.getScoresAsSwitch(standardMatchScores,matchSwitchDTO,matchScoresInfo,standardMatchInfo));
                super.updateEndSendScoresInfo(standardMatchScores,standardMatchInfo);
            }catch(Exception e){
                log.error("修改数据源联动开关异常:{}",e);
            }
            return true;
        }

        protected String getScoresAsSwitch(StandardMatchScores standardMatchScores, StandardMatchSwitchDTO matchSwitchDTO, MatchScoresInfo matchScoresInfo,StandardMatchInfo matchInfo) {
            log.info("修改开关联动同步比分 matchSwitchDTO:{}==========standardMatchScores:{} " +
                    "======================matchScoresInfo:{}",matchSwitchDTO,standardMatchScores,matchScoresInfo);
            Map<Long, TableTennisScores> newStandardScores = new HashMap<>();
            if(!StrUtil.isEmpty(standardMatchScores.getScoreJson())){
                newStandardScores = JSON.parseObject(standardMatchScores.getScoreJson(), new TypeReference<Map<Long, TableTennisScores>>() {
                });
            }
            Map<Long, TableTennisScores> thirdMatchScores = new HashMap<>();
            if(matchScoresInfo!=null && StrUtil.isNotEmpty(matchScoresInfo.getScoresJson())){
                JSONObject periodtableballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
                thirdMatchScores= JsonMapUtils.parseTableTennisMap(periodtableballScores);
            }
            log.info("乒乓球当前阶段比分:thirdMatchScores={}",thirdMatchScores);
            //足球外的其他球种 index字段传阶段值
            int index = matchSwitchDTO.getIndex();
            log.info("修改开关联动同步比分:index:{}",index);
            if(matchSwitchDTO.getStatus()==1){
                Long period = new Long(index);
                TableTennisScores thirdScores = thirdMatchScores.get(period);
                log.info("乒乓球当前阶段比分:{},{}",period,thirdScores);
                if(thirdScores==null){
                    return standardMatchScores.getScoreJson();
                }
                TableTennisScores standScores = newStandardScores.get(period);
                if(standScores==null){
                    standScores = new TableTennisScores();
                }
                standScores.setSetScore(thirdScores.getSetScore());
                log.info("乒乓球当前阶段比分:{},{}",period,standScores);
                newStandardScores.put(period,standScores);
            }
            calcWholeScores(newStandardScores,matchInfo.getMatchPeriodId());
            log.info("修改开关联动同步比分 newStandardScores:{}",newStandardScores);
            return JSON.toJSONString(newStandardScores);
        }

        public void calcWholeScores(Map<Long, TableTennisScores> newStandardScores,Long matchPeriodId){
            Integer tgHome = 0;
            Integer tgAway = 0;
            Integer setHome = 0;
            Integer setAway = 0;
            for (Long periodId : newStandardScores.keySet()) {
                //查询比分时过滤阶段0 -- 脏数据
                if (!scoreCenterPeriod.contains(periodId)) {
                    continue;
                }
                TableTennisScores cc = newStandardScores.get(periodId);
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
                if (cc.getSetScore().getHome() >= 12 || cc.getSetScore().getAway() >= 12) {
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
                        if (cc.getSetScore().getHome() >= 11 && cc.getSetScore().getHome() - cc.getSetScore().getAway() >= 2) {
                            setHome = setHome + 1;
                        }
                    } else if (cc.getSetScore().getHome() < cc.getSetScore().getAway()) {
                        if (cc.getSetScore().getAway() >= 11 && cc.getSetScore().getAway() - cc.getSetScore().getHome() >= 2) {
                            setAway = setAway + 1;
                        }
                    }
                }
            }
            if(newStandardScores.get(WHOLE_MATCH)==null){
                newStandardScores.put(WHOLE_MATCH,new TableTennisScores());
            }
            newStandardScores.get(WHOLE_MATCH).setMatchScore(new CommonItem(setHome, setAway));
            newStandardScores.get(WHOLE_MATCH).setSetScore(new CommonItem(tgHome, tgAway));
        }
    }
