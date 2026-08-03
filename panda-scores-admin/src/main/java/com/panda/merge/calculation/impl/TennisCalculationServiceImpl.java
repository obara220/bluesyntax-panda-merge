package com.panda.merge.calculation.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.constant.SourceTypeEnum;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.constant.TeamTypeConstant;
import com.panda.merge.dto.*;
import com.panda.merge.dto.resultScore.MatchResultScoreMsgVo;
import com.panda.merge.dto.scores.StandardMatchSwitchDTO;
import com.panda.merge.dto.scores.StandardScoreCenter;
import com.panda.merge.dto.scores.StandardScoreCenterDTO;
import com.panda.merge.dto.scores.StandardScoreDTO;
import com.panda.merge.dto.sourceSwitch.TennisSwitch;
import com.panda.merge.mapper.MatchScoresSpecialEventMapper;
import com.panda.merge.model.*;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.EffectScoresCode.*;
import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

/**
 * 网球比分逻辑处理
 */
@Slf4j
@Service
public class TennisCalculationServiceImpl extends AbstractCalculationServiceImpl {

    @Autowired
    MatchScoresSpecialEventMapper matchScoresSpecialEventMapper;
    /**
     * 保存比分
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
     * @throws Exception
     */
    private void updateScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) throws Exception {
        TennisMatchEventInfoDTO tennisMatchEventInfo =getTennisMatchEventInfo(matchScoresInfo,data);
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, TennisScores> allPeriodScores= JsonMapUtils.parseTennisMap(periodFootballScores);
        TennisScores wholeSores= allPeriodScores.get(WHOLE_MATCH);

        if(wholeSores==null|| data.getMatchPeriodId()==null){
            log.error("updateScores wholeSores==null|| data.getMatchPeriodId()==null ThirdMatchSourceId:"+data.getThirdMatchSourceId()+"matchid:"+matchScoresInfo.getThirdMatchId());
            return;
        }
        //如果非正常开打阶段直接清零当前局比分
        if(!SportPeriodConstant.TennisPeriod.contans(data.getMatchPeriodId())){
            if(data.getEventCode().equals("tennis_score_change")){
                for (TennisScores value : allPeriodScores.values()) {
                    value.getCurrentScore().setHome(0);
                    value.getCurrentScore().setAway(0);
                }
            }
            matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
            matchScoresInfo.setModifyTime(System.currentTimeMillis());
//            matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
            //继续执行局比分处理，不中断
//            return;
        }
        TennisScores periodScores= allPeriodScores.get(data.getMatchPeriodId());

        //新建该阶段值
        if(periodScores==null) {
            periodScores = new TennisScores();
        }
        /**当前局内比分清理*/
        if(data.getEventCode().equals("tennis_score_change")){
            for (TennisScores value : allPeriodScores.values()) {
                value.getCurrentScore().setHome(0);
                value.getCurrentScore().setAway(0);
            }
        }
        allPeriodScores.put(data.getMatchPeriodId(), periodScores);
        //总阶段新增事件值
        wholeSores.setFieldByEvent(data);
        //计算阶段值 下半场=全场-上半场 等
        periodScores.setFieldByEvent(data);
        wholeSores.getSetScore().setHome(0);
        wholeSores.getSetScore().setAway(0);
        for (Map.Entry<Long, TennisScores> longTableTennisScoresEntry : allPeriodScores.entrySet()) {
            if(longTableTennisScoresEntry.getKey().equals(WHOLE_MATCH)){
                continue;
            }
            wholeSores.getSetScore().setHome( wholeSores.getSetScore().getHome()+longTableTennisScoresEntry.getValue().getSetScore().getHome());
            wholeSores.getSetScore().setAway( wholeSores.getSetScore().getAway()+longTableTennisScoresEntry.getValue().getSetScore().getAway());
        }
        //每局比分
        updateExtryScores(data,matchScoresInfo);
        periodScores.doCalculation(tennisMatchEventInfo);
        wholeSores.doCalculation(tennisMatchEventInfo);
        matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
        matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
        matchScoresInfo.setPeriodT1(periodScores.getSetScore().getHome());
        matchScoresInfo.setPeriodT2(periodScores.getSetScore().getAway());
        //当前阶段新增事件值 或者设置当前事件值
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    /**
     * 更新局比分
     * @param data
     * @param matchScoresInfo
     */
    private void updateExtryScores(MatchEventInfo data, MatchScoresInfo matchScoresInfo) {
        try {
            TennisExtryScores tennisExtryScores;
            if (StringUtils.isEmpty(matchScoresInfo.getScoresJsonExtra())) {
                tennisExtryScores = new TennisExtryScores();
            } else {
                tennisExtryScores = JSONObject.toJavaObject((JSONObject.parseObject(matchScoresInfo.getScoresJsonExtra())), TennisExtryScores.class);
            }
            tennisExtryScores.doCalculation(data);
            matchScoresInfo.setScoresJsonExtra(JSONObject.toJSONString(tennisExtryScores));
        }catch (Exception e){
            log.error("{}:error:",data.getLinkId(),e);
        }
    }

    /**
     * 初始化比分
     * @param matchScoresInfo
     * @param data
     * @throws Exception
     */
    private void createScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) throws Exception {
        if(!SportPeriodConstant.TennisPeriod.contans(data.getMatchPeriodId())){
            return;
        }
        TennisMatchEventInfoDTO tennisMatchEventInfo =new TennisMatchEventInfoDTO();
        tennisMatchEventInfo.setMatchEventInfo(data);
        Map<Long, TennisScores> periodFootballScores= new HashMap<>();
        TennisScores tennisScores=new TennisScores();
        periodFootballScores.put(WHOLE_MATCH,tennisScores);
        periodFootballScores.put(data.getMatchPeriodId(),tennisScores);

        tennisScores.setFieldByEvent(data);

        tennisScores.doCalculation(tennisMatchEventInfo);
        //3.更新比分模板
        periodFootballScores.put(data.getMatchPeriodId(),((JSONObject) JSONObject.toJSON(tennisScores)).toJavaObject(TennisScores.class));

        matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodFootballScores));
        matchScoresInfo.setT1(tennisScores.getMatchScore().getHome());
        matchScoresInfo.setT2(tennisScores.getMatchScore().getAway());
        matchScoresInfo.setPeriodT1(tennisScores.getSetScore().getHome());
        matchScoresInfo.setPeriodT2(tennisScores.getSetScore().getAway());
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
        log.info("createScores 成功"+data.getEventCode()+"事件ID:"+data.getThirdEventId());
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
        if(!data.getEventCode().equals("tennis_score_change")){
            return;
        }
//        TennisMatchEventInfoDTO tennisMatchEventInfo =getCancelEventInfo(matchScoresInfo,data);
//        tennisMatchEventInfo.setMatchEventInfo(data);
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, TennisScores> allPeriodScores= JsonMapUtils.parseTennisMap(periodFootballScores);
        TennisScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        if(wholeSores==null|| data.getMatchPeriodId()==null){
            return;
        }
        TennisScores oldSores= allPeriodScores.get(data.getMatchPeriodId());
        if(oldSores==null){
            log.error("canleEvent 当前取消事件还未被统计:wholeSores==null||oldSores==null EventCode:"+data.getEventCode()+"事件ID:"+data.getThirdEventId());
            throw new Exception("canleEvent 当前取消事件还未被统计:wholeSores==null||oldSores==null |EventCode："+data.getEventCode());
        }
        //1.先取消全局
//        oldSores.reSetEvent(allPeriodScores,tennisMatchEventInfo);
//        }

        wholeSores.getSetScore().setHome(0);
        wholeSores.getSetScore().setAway(0);
        for (Map.Entry<Long, TennisScores> longTableTennisScoresEntry : allPeriodScores.entrySet()) {
            if(longTableTennisScoresEntry.getKey().equals(WHOLE_MATCH)){
                continue;
            }
            wholeSores.getSetScore().setHome( wholeSores.getSetScore().getHome()+longTableTennisScoresEntry.getValue().getSetScore().getHome());
            wholeSores.getSetScore().setAway( wholeSores.getSetScore().getAway()+longTableTennisScoresEntry.getValue().getSetScore().getAway());
        }
        matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
        matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
        matchScoresInfo.setPeriodT1(wholeSores.getSetScore().getHome());
        matchScoresInfo.setPeriodT2(wholeSores.getSetScore().getAway());
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    /**
     * 网球事件取消逻辑处理
     * @param matchScoresInfo
     * @param oldMatchInfo
     * @return
     */
    private TennisMatchEventInfoDTO getCancelEventInfo(MatchScoresInfo matchScoresInfo, MatchEventInfo oldMatchInfo) {
        TennisMatchEventInfoDTO tennisMatchEventInfo =new TennisMatchEventInfoDTO();
        //1.查询原事件ID的 特殊事件
        MatchScoresSpecialEventExample matchScoresSpecialEventExample =new MatchScoresSpecialEventExample();
        matchScoresSpecialEventExample.createCriteria().andThirdMatchIdEqualTo(matchScoresInfo.getThirdMatchId()).andSourceEventIdEqualTo(oldMatchInfo.getThirdEventId());
        List<MatchScoresSpecialEvent> eventList= matchScoresSpecialEventMapper.selectByExample(matchScoresSpecialEventExample);
        //2.循环特殊事件
        //2.1 是否有破发点 2.2 是否有破发成功事件
        for (MatchScoresSpecialEvent matchScoresSpecialEvent : eventList) {
            if(matchScoresSpecialEvent.getPandaEventCode().equals(BREAK_POINT_EVENT)){
                tennisMatchEventInfo.setBreakPoint(true);
                tennisMatchEventInfo.setHomeAwayBreakPoint(matchScoresSpecialEvent.getHomeaway());
            }
            if(matchScoresSpecialEvent.getPandaEventCode().equals(BREAK_SUCCESS_EVENT)){
                tennisMatchEventInfo.setBreakSuccess(true);
                tennisMatchEventInfo.setHomeAwayBreakSuccess(matchScoresSpecialEvent.getHomeaway());
            }
        }
        //3. 返回
        return tennisMatchEventInfo;
    }

    /**
     * 获取网球事件信息
     * @param matchScoresInfo
     * @param data
     * @return
     */
    private TennisMatchEventInfoDTO getTennisMatchEventInfo(MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        TennisMatchEventInfoDTO tennisMatchEventInfo =new TennisMatchEventInfoDTO();
        tennisMatchEventInfo.setMatchEventInfo(data);
        if(!data.getEventCode().equals("tennis_score_change")){
            return tennisMatchEventInfo;
        }
        if(!data.getSourceType().equals(SourceTypeEnum.LIVE_DATA.getCode())){
            return tennisMatchEventInfo;
        }

        MatchScoresSpecialEventExample matchScoresSpecialEventExample =new MatchScoresSpecialEventExample();
        matchScoresSpecialEventExample.createCriteria().andThirdMatchIdEqualTo(matchScoresInfo.getThirdMatchId()).andPandaEventCodeEqualTo(UNKNOW_EVENT);
        List<MatchScoresSpecialEvent> list= matchScoresSpecialEventMapper.selectByExample(matchScoresSpecialEventExample);
        //0.未知事件入库  40-40
        if(checkScores(data)&&list.size()==0){
            MatchScoresSpecialEvent matchScoresSpecialEvent= createMatchScoresSpecialEvent(matchScoresInfo,data);
            matchScoresSpecialEventMapper.insert(matchScoresSpecialEvent);
            return tennisMatchEventInfo;
        }
        //0.1 未知事件计算
        if(list.size()>0){
            MatchScoresSpecialEvent matchScoresSpecialEvent =list.get(0);
            if(matchScoresSpecialEvent.getSecondT1()==null){
                matchScoresSpecialEvent.setSecondT1(0);
            }
            if(matchScoresSpecialEvent.getSecondT2()==null){
                matchScoresSpecialEvent.setSecondT2(0);
            }
            if(data.getSecondT1()==null){
                data.setSecondT1(0);
            }
            if(data.getSecondT2()==null){
                data.setSecondT2(0);
            }
            if(matchScoresSpecialEvent.getAddition3()==null||matchScoresSpecialEvent.getAddition3()==null){
                return tennisMatchEventInfo;
            }
            //1.判断当前是不是破发点
            if(data.getSecondT1()<=1&&data.getSecondT2()<=1){
                //1.2判断球队 发球方
                if(matchScoresSpecialEvent.getSecondT1()>=matchScoresSpecialEvent.getSecondT2()&& matchScoresSpecialEvent.getAddition3().equals(TeamTypeConstant.AWAY)){
                    // matchScoresSpecialEvent 就是 主队的 破发点
                    matchScoresSpecialEvent.setPandaEventCode(BREAK_POINT_EVENT);
                    matchScoresSpecialEvent.setHomeaway(TeamTypeConstant.HOME);
                    matchScoresSpecialEvent.setModifyTime(System.currentTimeMillis());
                    matchScoresSpecialEventMapper.updateByPrimaryKey(matchScoresSpecialEvent);
                    tennisMatchEventInfo.setHomeAwayBreakPoint(TeamTypeConstant.HOME);
                    tennisMatchEventInfo.setBreakPoint(true);
                    //1.3 计算是否是破发成功
                    if(data.getHomeAway().equals(TeamTypeConstant.HOME)){
                        //1.主队破发成功
                        tennisMatchEventInfo.setBreakSuccess(true);
                        tennisMatchEventInfo.setBreakPoint(true);
                        //2.破发成功事件入库
                        MatchScoresSpecialEvent event= createMatchScoresSpecialEvent(matchScoresInfo,data);
                        event.setPandaEventCode(BREAK_SUCCESS_EVENT);
                        event.setHomeaway(TeamTypeConstant.HOME);
                        matchScoresSpecialEventMapper.insert(event);
                        tennisMatchEventInfo.setHomeAwayBreakSuccess(TeamTypeConstant.HOME);
                    }
                }else if(matchScoresSpecialEvent.getSecondT1()<=matchScoresSpecialEvent.getSecondT2()&& matchScoresSpecialEvent.getAddition3().equals(TeamTypeConstant.HOME)){
                    //matchScoresSpecialEvent 就是客队的破发点
                    matchScoresSpecialEvent.setPandaEventCode(BREAK_POINT_EVENT);
                    matchScoresSpecialEvent.setHomeaway(TeamTypeConstant.AWAY);
                    matchScoresSpecialEvent.setModifyTime(System.currentTimeMillis());
                    matchScoresSpecialEventMapper.updateByPrimaryKey(matchScoresSpecialEvent);
                    tennisMatchEventInfo.setHomeAwayBreakPoint(TeamTypeConstant.AWAY);
                    tennisMatchEventInfo.setBreakPoint(true);
                    if(data.getHomeAway().equals(TeamTypeConstant.AWAY)){
                        //1.客队破发成功
                        tennisMatchEventInfo.setBreakSuccess(true);
                        tennisMatchEventInfo.setBreakPoint(true);
                        //2.破发成功事件入库
                        MatchScoresSpecialEvent event= createMatchScoresSpecialEvent(matchScoresInfo,data);
                        event.setPandaEventCode(BREAK_SUCCESS_EVENT);
                        event.setHomeaway(TeamTypeConstant.AWAY);
                        matchScoresSpecialEventMapper.insert(event);
                        tennisMatchEventInfo.setHomeAwayBreakSuccess(TeamTypeConstant.AWAY);
                    }
                }else {
                    matchScoresSpecialEvent.setPandaEventCode(NONE_EVENT);
                    matchScoresSpecialEvent.setModifyTime(System.currentTimeMillis());
                    matchScoresSpecialEventMapper.updateByPrimaryKey(matchScoresSpecialEvent);
                }
            }else {
                // 不是 0 比 0 则 如果不是 40-40 则是 破发点
                if(isSimpleBreakPoint(matchScoresSpecialEvent)){
                    if(matchScoresSpecialEvent.getSecondT1()>=matchScoresSpecialEvent.getSecondT2()&&matchScoresSpecialEvent.getAddition3().equals(TeamTypeConstant.AWAY)){
                        tennisMatchEventInfo.setBreakPoint(true);
                        tennisMatchEventInfo.setBreakSuccess(false);
                        matchScoresSpecialEvent.setPandaEventCode(BREAK_POINT_EVENT);
                        matchScoresSpecialEvent.setHomeaway(TeamTypeConstant.HOME);
                        matchScoresSpecialEvent.setModifyTime(System.currentTimeMillis());
                        matchScoresSpecialEventMapper.updateByPrimaryKey(matchScoresSpecialEvent);
                        tennisMatchEventInfo.setHomeAwayBreakPoint(TeamTypeConstant.HOME);
                    }else if(matchScoresSpecialEvent.getSecondT1()<=matchScoresSpecialEvent.getSecondT2()&& matchScoresSpecialEvent.getAddition3().equals(TeamTypeConstant.HOME)){
                        tennisMatchEventInfo.setBreakPoint(true);
                        tennisMatchEventInfo.setBreakSuccess(false);
                        matchScoresSpecialEvent.setPandaEventCode(BREAK_POINT_EVENT);
                        matchScoresSpecialEvent.setHomeaway(TeamTypeConstant.AWAY);
                        matchScoresSpecialEvent.setModifyTime(System.currentTimeMillis());
                        matchScoresSpecialEventMapper.updateByPrimaryKey(matchScoresSpecialEvent);
                        tennisMatchEventInfo.setHomeAwayBreakPoint(TeamTypeConstant.AWAY);
                    }
                }else {
                    matchScoresSpecialEvent.setPandaEventCode(NONE_EVENT);
                    matchScoresSpecialEvent.setModifyTime(System.currentTimeMillis());
                    matchScoresSpecialEventMapper.updateByPrimaryKey(matchScoresSpecialEvent);
                }
            }
            return tennisMatchEventInfo;
        }else {

        }
        return  tennisMatchEventInfo;
    }

    /**
     * 校验比分
     * @param data
     * @return
     */
    private boolean checkScores(MatchEventInfo data) {
        if(data.getSecondT1()==null||data.getSecondT2()==null){
            return false;
        }
        if(data.getSecondT1()==40||data.getSecondT2()==40){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 是否破发点
     * @param data
     * @return
     */
    private boolean isSimpleBreakPoint(MatchScoresSpecialEvent data) {
        if(data.getSecondT1()==null||data.getSecondT2()==null){
            return false;
        }
        if(data.getSecondT1()==40&&data.getSecondT2()==40){
            return false;
        }else {
            return true;
        }
    }

    /**
     * 破发事件入库
     * @param matchScoresInfo
     * @param data
     * @return
     */
    private MatchScoresSpecialEvent createMatchScoresSpecialEvent(MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        MatchScoresSpecialEvent matchScoresSpecialEvent=  new MatchScoresSpecialEvent();
        matchScoresSpecialEvent.setId(IdWorker.getId());
        matchScoresSpecialEvent.setCreateTime(System.currentTimeMillis());
        matchScoresSpecialEvent.setModifyTime(System.currentTimeMillis());
        matchScoresSpecialEvent.setDataSourceEventId(data.getThirdEventId());
        matchScoresSpecialEvent.setSourceEventId(data.getThirdEventId());
        matchScoresSpecialEvent.setHomeaway(data.getHomeAway());
        matchScoresSpecialEvent.setT1(data.getT1());
        matchScoresSpecialEvent.setT2(data.getT2());
        matchScoresSpecialEvent.setAddition3(data.getAddition3());
        matchScoresSpecialEvent.setAddition1(data.getAddition1());
        matchScoresSpecialEvent.setAddition2(data.getAddition2());
        matchScoresSpecialEvent.setFirstNum(data.getFirstNum());
        matchScoresSpecialEvent.setSecondNum(data.getSecondNum());
        matchScoresSpecialEvent.setSportId(data.getSportId());
        matchScoresSpecialEvent.setDataSourceCode(data.getDataSourceCode());
        matchScoresSpecialEvent.setThirdMatchId(matchScoresInfo.getThirdMatchId());
        matchScoresSpecialEvent.setPandaEventCode(UNKNOW_EVENT);
        matchScoresSpecialEvent.setExtrainfo(data.getExtraInfo());
        return matchScoresSpecialEvent;
    }

    /**
     * 保存比分统计
     * @param matchScoresInfo
     * @param data
     */
    @Override
    public void saveMatchStatisticsScores(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data, StandardMatchInfo standardMatchInfo) {
//        if(!SportPeriodConstant.TennisPeriod.contans(data.getPeriod()+0l)){
//            return;
//        }
        //1.查询数据库的阶段值是否存在
        //1.1 查询 matchScoresInfo 的 json 是否存在 不存在则新建
        if(StringUtils.isEmpty(matchScoresInfo.getScoresJson())){
            createMatchStatistics(matchScoresInfo,data);
        }else {
            //2.如果存在则覆盖值
            saveMatchStatistics(matchScoresInfo,data,standardMatchInfo);
        }
    }

    /**
     * 初始化比分统计
     * @param matchScoresInfo
     * @param data
     */
    private void createMatchStatistics(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data) {
        Map<Long, TennisScores> periodFootballScores= new HashMap<>();
        TennisScores tennisScores=new TennisScores();
        periodFootballScores.put(WHOLE_MATCH,tennisScores);
//        TennisScores  periodScores= new TennisScores();
//        periodFootballScores.put(data.getPeriod()+0l,periodScores);
        //更新赛事比分表
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodFootballScores));
        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    /**
     * 更新比分统计
     * @param matchScoresInfo
     * @param data
     */
    private void saveMatchStatistics(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data, StandardMatchInfo standardMatchInfo) {
        if(standardMatchInfo==null){
            log.info("saveMatchStatistics 标准赛事不存在，standardMatchInfo:null");
            return;
        }
        Integer roundType = standardMatchInfo.getRoundType();
        //保存比分
        if(data.getMatchStatisticsInfoDetailList()==null){
            log.error("createMatchStatistics data:null");
            return;
        }
        //1.得到阶段map 转化的
        JSONObject periodBasketballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, TennisScores> allPeriodScores= JsonMapUtils.parseTennisMap(periodBasketballScores);
//        TennisScores periodScores=allPeriodScores.get(data.getPeriod()+0l);
        //改当前阶段的盘比分以及总的盘比分
        TennisScores wholeSores= allPeriodScores.get(WHOLE_MATCH.longValue());
//        if(periodScores==null){
//            periodScores= new TennisScores();
//            allPeriodScores.put(data.getPeriod()+0l,periodScores);
//        }
        Long maxPeriodId =0l;
        /**
         * 局比分清理
         * */
        TennisScores periodScores =null ;
        int x=0;
        for (MatchStatisticsInfoDetailDTO dto : data.getMatchStatisticsInfoDetailList()) {
            if(dto.getCode().equals("set_score")){
                if(dto.getFirstNum()!=null && roundType<dto.getFirstNum()){
                    log.info("{} ,thirdMatchId={}  saveMatchStatistics 网球赛制为{}局，不获取第{}局的比分",
                            data.getRemark(),matchScoresInfo.getThirdMatchId(), roundType,dto.getFirstNum());
                    return;
                }
                //改对应阶段的局比分
                Long   periodId=  SportPeriodConstant.TennisPeriod.getWholePeriodsByMatchLength()[dto.getFirstNum()-1];
                TennisScores setScores=allPeriodScores.get(periodId);
                if(setScores==null){
                    setScores= new TennisScores();
                    allPeriodScores.put(periodId,setScores);
                }
                setScores.getCurrentScore().setHome(0);
                setScores.getCurrentScore().setAway(0);
                if(x<dto.getFirstNum()){
                    x=dto.getFirstNum();
                    periodScores=setScores;
                }
            }
        }
        if(periodScores==null){
            periodScores = new TennisScores();
        }
        wholeSores.getCurrentScore().setHome(0);
        wholeSores.getCurrentScore().setAway(0);
        for (MatchStatisticsInfoDetailDTO dto : data.getMatchStatisticsInfoDetailList()) {
            if(dto.getCode().equals("set_score")){
                if(dto.getFirstNum()!=null && roundType<dto.getFirstNum()){
                    log.info("{} ,thirdMatchId={}  saveMatchStatistics 网球赛制为{}局，不获取第{}局的比分",
                            data.getRemark(),matchScoresInfo.getThirdMatchId(), roundType,dto.getFirstNum());
                    return;
                }
                //改对应阶段的局比分
                Long   periodId=  SportPeriodConstant.TennisPeriod.getWholePeriodsByMatchLength()[dto.getFirstNum()-1];
                TennisScores setScores=allPeriodScores.get(periodId);
                if(setScores==null){
                    setScores= new TennisScores();
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
                Integer addHome = dto.getT1() -wholeSores.getMatchScore().getHome();
                Integer addAway = dto.getT2() -wholeSores.getMatchScore().getAway();
                wholeSores.getMatchScore().setHome(dto.getT1());
                wholeSores.getMatchScore().setAway(dto.getT2());

                periodScores.getMatchScore().setHome(periodScores.getMatchScore().getHome()+addHome);
                periodScores.getMatchScore().setAway(periodScores.getMatchScore().getAway()+addAway);
                matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
                matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
            }else if(dto.getCode().equals("game_score")){
                wholeSores.getCurrentScore().setHome(dto.getT1());
                wholeSores.getCurrentScore().setAway(dto.getT2());
                periodScores.getCurrentScore().setHome(dto.getT1());
                periodScores.getCurrentScore().setAway(dto.getT2());
            }
        }
        wholeSores.getSetScore().setHome(0);
        wholeSores.getSetScore().setAway(0);
        for (Map.Entry<Long, TennisScores> longTableTennisScoresEntry : allPeriodScores.entrySet()) {
            if(longTableTennisScoresEntry.getKey().equals(WHOLE_MATCH)){
                continue;
            }
            wholeSores.getSetScore().setHome( wholeSores.getSetScore().getHome()+longTableTennisScoresEntry.getValue().getSetScore().getHome());
            wholeSores.getSetScore().setAway( wholeSores.getSetScore().getAway()+longTableTennisScoresEntry.getValue().getSetScore().getAway());
        }
        //2.变更入库
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);

    }

    @Override
    public void calcStandardMatchScores(MatchScoresInfo matchScoresInfo, StandardMatchScores score, MatchEventInfo data){

        String scoresJson = matchScoresInfo.getScoresJson();
        Map<Long, TennisScores> allPeriodScores = JSON.parseObject(scoresJson, new TypeReference<Map<Long, TennisScores>>() {
        });
        TennisScores thirdWholeSores= allPeriodScores.get(WHOLE_MATCH);
        Map<Long, TennisScores> standardScores = new HashMap<>();
        //标准比分为空，直接复制三方比分
        if (!StringUtils.isEmpty(score.getScoreJson())) {
            standardScores = JSON.parseObject(score.getScoreJson(), new TypeReference<Map<Long, TennisScores>>() {
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
            calcWholeScores(standardScores,data.getMatchPeriodId());
            standardScores.get(WHOLE_MATCH).setCurrentScore(thirdWholeSores.getCurrentScore());
//            standardScores.put(WHOLE_MATCH,thirdWholeSores);
        }catch (Exception e){
            log.error("计算标准比分错误:{}",data.getLinkId(),e);
        }
        //保存
        scoresJson = JSONUtil.toJsonStr(standardScores);
        score.setScoreJson(scoresJson);
    }

    private void setPeriodScores(Map<Long, TennisScores> standardScores, Map<Long, TennisScores> allPeriodScores, TennisSwitch tennisSwitch, Long periodId) {
        TennisScores soresSource= allPeriodScores.get(periodId);
        if(soresSource==null) {
            log.info("复制网球阶段比分,三方阶段比分为空 {}",periodId);
            return;
        }
        if(standardScores.get(periodId)==null){
            standardScores.put(periodId,new TennisScores(periodId));
        }
        for(Map.Entry<Long, TennisScores> entry : allPeriodScores.entrySet()){
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

    private Long changePeriodByExtryPeriodEvent(Long periodId) {
        if(periodId==301L || periodId==800){
            return 8L;
        }else if(periodId==302L || periodId==900){
            return 9L;
        }else if(periodId==303L || periodId==1000){
            return 10L;
        }else if(periodId==304L || periodId==1100){
            return 11L;
        }else if(periodId==305L || periodId==1200){
            return 12L;
        }else{
            return periodId;
        }
    }

    @Override
    public StandardScoreCenterDTO queryMatchScores(Long standardMatchId) {
        StandardSportMarketSell match = standardSportMarketSellRepository.selectThirdMatchInfoPrimaryKey(standardMatchId);

        if(match==null){
            log.info("开售信息不存在");
            return null;
        }
        StandardScoreCenterDTO dto = new StandardScoreCenterDTO();
        StandardMatchInfo matchInfo = standardMatchInfoRepository.selectStandardMatchPrimaryKey(standardMatchId);

        dto.setSportId(match.getSportId());
        dto.setStandardMatchId(standardMatchId);
        dto.setMatchManageId(match.getMatchManageId());

        dto.setBusinessEvent(match.getBusinessEvent());
        dto.setRelatedDataSourceCoderList(matchInfo.getRelatedDataSourceCoderList());
        dto.setPreId(match.getId());
        //查询标准比分
        StandardMatchScores standardMatchScores = scoresRedisHelp.getCatchStandScoreByMatchId(standardMatchId);
        if(standardMatchScores == null){
            log.info("标准比分不存在");
            return null;
        }
        dto.setShowStatus(standardMatchScores.getShowStatus());
        //获取标准比分
        StandardScoreCenter centerStand = new StandardScoreCenter();
        centerStand.setDataSourceCode("STAND");
        centerStand.setIndex(0);
        centerStand.setStandardMatchId(standardMatchId);
        centerStand.setSportId(matchInfo.getSportId());
        //组装标准比分
        super.buildScore(centerStand,standardMatchScores.getScoreJson(),matchInfo,standardMatchScores.getDataSourceAccoSwitch());

        if(centerStand.getScores()==null || centerStand.getScores().isEmpty()){
            //比分为空时，补充阶段比分为null 用于前端编辑
            super.scoreIsNullExtract(matchInfo,centerStand);
        }
        List<StandardScoreCenter> list = new ArrayList<>();
        list.add(centerStand);

        //TODO
        //查询所有 的三方赛事
        //for 循环获取 redis 的 比分中心比分
        //区分 sourceType
        //根据 matchScoreInfoRepository 查询
        //组装反数据
        //假如list
        //获取其他数据源比分
        List<MatchScoresInfo>  listScore = new ArrayList<>();

        ThirdMatchInfoExample thirdMatchInfoExample = new ThirdMatchInfoExample();
        thirdMatchInfoExample.createCriteria().andReferenceIdEqualTo(standardMatchId);
        List<ThirdMatchInfo> thirdMatchInfoList =matchScoreSearchService.searchAllThirdMatchInfoByExample(thirdMatchInfoExample);
        if(thirdMatchInfoList!=null && !thirdMatchInfoList.isEmpty()) {
            for (ThirdMatchInfo thirdMatchInfo : thirdMatchInfoList) {
                if(N0123_SOURCE_CODE.contains(thirdMatchInfo.getDataSourceCode())){
                    continue;
                }
                //B02取对应通道比分 uof
                if(DataSourceCodeEnum.BC.code.equals(thirdMatchInfo.getDataSourceCode())){
                   Long dataSourceType = matchScoreInfoRepository.checkB02ScoresSource(matchInfo.getSportId());
                    MatchScoresInfo matchScoresInfo = matchScoreInfoRepository.selectByExample(thirdMatchInfo.getId(), dataSourceType == 1 ? 0 : 1);
                    if (matchScoresInfo != null) {
                        listScore.add(matchScoresInfo);
                    }
                }else{
                    //其他数据源默认取实时事件比分 livedata
                    MatchScoresInfo matchScoresInfo = matchScoreInfoRepository.selectByExample(thirdMatchInfo.getId(), SourceTypeEnum.LIVE_DATA.getCode());
                    if (matchScoresInfo != null) {
                        listScore.add(matchScoresInfo);
                    }else{
                        //无事件比分展示统计比分
                        matchScoresInfo = matchScoreInfoRepository.selectByExample(thirdMatchInfo.getId(), SourceTypeEnum.UOF.getCode());
                        if (matchScoresInfo != null) {
                            listScore.add(matchScoresInfo);
                        }
                    }
                }
            }
            if (!listScore.isEmpty()) {
                Map<String, List<MatchScoresInfo>> scoreMaps =
                        listScore.stream().collect(Collectors.groupingBy(MatchScoresInfo::getDataSourceCode, LinkedHashMap::new, Collectors.toList()));
                int index = 1;
                for (Map.Entry<String, List<MatchScoresInfo>> values : scoreMaps.entrySet()) {
                    String dataSourceCode = values.getKey();
                    String scoresJson = values.getValue().get(0).getScoresJson();

                    StandardScoreCenter dataSourceScores = new StandardScoreCenter();
                    dataSourceScores.setDataSourceCode(dataSourceCode);
                    dataSourceScores.setStandardMatchId(standardMatchId);
                    dataSourceScores.setSportId(matchInfo.getSportId());
                    dataSourceScores.setIndex(index++);
                    if (dataSourceCode.equals(match.getBusinessEvent())) {
                        dataSourceScores.setIsMain(true);
                    } else {
                        dataSourceScores.setIsMain(false);
                    }
                    //组装数据源比分
                    super.buildScore(dataSourceScores, scoresJson, matchInfo, null);
                    list.add(dataSourceScores);
                }
            }else{
                log.info("第三方赛事不存在1");
            }
        }else{
            log.info("第三方赛事不存在2");
        }
        if(!list.isEmpty()){
            //排序，保证0-标准比分一直处于第一个
            list.sort(Comparator.comparing((StandardScoreCenter::getIndex)));
            super.chechScoreIsDifferent(list);
        }
        dto.setScores(list);
        return dto;
    }




    /**
     * 修改标准比分
     * @param scores
     * @return
     */
    public Response editStandScores(StandardScoreCenter scores,StandardMatchScores standardMatchScores,StandardMatchInfo standardMatchInfo){
        String scoresJson = standardMatchScores.getScoreJson();
        Map<Long, TennisScores> allPeriodScores = new HashMap<>();
        if(StringUtils.isNotBlank(scoresJson)) {
            JSONObject periodFootballScores = JSONObject.parseObject(scoresJson);
            allPeriodScores = JsonMapUtils.parseTennisMap(periodFootballScores);
        }
        //编辑校验
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
            // -1 和 0不处理，由后台统计
            if(score.getPeriodId() == 0 || score.getPeriodId() == -1){
                continue;
            }
            //当主队和客队都为空时，不处理当前阶段
            if(score.getHome()==null && score.getAway()==null){
                continue;
            }
            //当主队或者客队为空时，比分为0
            if(score.getHome()==null){
                score.setHome(0);
            }
            if(score.getAway()==null){
                score.setAway(0);
            }
            //当前事件是盘结束阶段
            if(setEndPeriod.contains(standardMatchInfo.getMatchPeriodId())){
                if (score.getHome() > score.getAway()) {
                    home = home +1;
                } else {
                    away = away +1;
                }
                log.info("赛事：{}，赛事阶段：{}，比分阶段{},网球比分编辑下发，" +
                        "盘比分：{}-{}",standardMatchInfo.getId(),standardMatchInfo.getMatchPeriodId(),score,home,away);
            }else{
                if(score.getHome()>= 6+1 || score.getAway() >= 6+1 ){
                    if(score.getHome()>score.getAway() ){
                        home = home +1;
                    }else{
                        away = away +1;
                    }
                }else{
                    if(score.getHome()>score.getAway()){
                        if(score.getHome()>=6 && score.getHome() - score.getAway() >= 2){
                            home = home +1;
                        }
                    }else if (score.getHome()<score.getAway()){
                        if(score.getAway()>=6 && score.getAway() - score.getHome() >= 2){
                            away = away +1;
                        }
                    }
                }
            }

            tgHome+=score.getHome();
            tgAway+=score.getAway();
            CommonItem scoreItem = new CommonItem();
            scoreItem.setHome(score.getHome());
            scoreItem.setAway(score.getAway());
            TennisScores ts = new TennisScores();
            ts.setSetScore(scoreItem);
            allPeriodScores.put(score.getPeriodId(),ts);
        }
        //总比分-赛盘
        TennisScores ts = new TennisScores();
        ts.setMatchScore(new CommonItem(home,away));
        //总局数
        ts.setSetScore(new CommonItem(tgHome,tgAway));
        allPeriodScores.put(-1L,ts);

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
        Map<Long,TennisScores> newScores = JsonMapUtils.parseTennisMap(periodScores);
        if(StrUtil.isEmpty(scoresJson)){
            Map<Long, TennisScores> wholePeriodScores= new HashMap<>();
            TennisScores badScores=new TennisScores();
            wholePeriodScores.put(WHOLE_MATCH,badScores);
            //无比分时初始化比分,用于做开关自动关闭的校验
            scoresJson = JSONObject.toJSONString(wholePeriodScores);
        }
        //获取修改前数据库的比分
        JSONObject oldScores = JSONObject.parseObject(scoresJson);
        Map<Long,TennisScores> allPeriodScores2 = JsonMapUtils.parseTennisMap(oldScores);
        if(allPeriodScores2.isEmpty()){
            allPeriodScores2 = new HashMap<>();
        }
        TennisSwitch accoSwitchs = getSportSwitchsConfig(standardMatchScores, newScores, allPeriodScores2,scores);
        standardMatchScores.setDataSourceAccoSwitch(JSONUtil.toJsonStr(accoSwitchs));
    }
    private  TennisSwitch getSportSwitchsConfig(StandardMatchScores standardMatchScores, Map<Long, TennisScores> newScores,
                                                Map<Long, TennisScores> oldScores,StandardScoreCenter scores) {
        TennisSwitch accoSwitchs = new TennisSwitch();
        //修改前的联动开关串
        if (!StrUtil.isEmpty(standardMatchScores.getDataSourceAccoSwitch())) {
            accoSwitchs = JSONObject.parseObject(standardMatchScores.getDataSourceAccoSwitch(), TennisSwitch.class);
        }
        String matchManageId = standardMatchScores.getMatchManageId();
        StandardMatchSwitchDTO switchDTO = super.setSwitchObj(standardMatchScores,scores);
        log.info("修改开关联动同步比分日志 switchDTO:{}==========s ", switchDTO);
        for (int i = 0; i < scoreCenterPeriod.size(); i++) {
            Long period = scoreCenterPeriod.get(i);
            //对比修改前后比分,变更开关
            TennisScores scoresfor = newScores.get(period);
            if (scoresfor == null) {
                scoresfor = new TennisScores(period);
            }
            TennisScores scoresRea = oldScores.get(period);
            if(scoresRea==null){
                scoresRea = new TennisScores();
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
                } else if (period == 13 || period == 441) {
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
            standardMatchScores.setScoreJson(this.getScoresAsSwitch(standardMatchScores,matchSwitchDTO,matchScoresInfo));
            super.updateEndSendScoresInfo(standardMatchScores,standardMatchInfo);
        }catch(Exception e){
            log.error("修改数据源联动开关异常:{}",e);
        }
        return true;

    }

    protected String getScoresAsSwitch(StandardMatchScores standardMatchScores, StandardMatchSwitchDTO matchSwitchDTO, MatchScoresInfo matchScoresInfo) {
        log.info("修改开关联动同步比分 matchSwitchDTO:{}==========standardMatchScores:{} " +
                "======================matchScoresInfo:{}",matchSwitchDTO,standardMatchScores,matchScoresInfo);
        Map<Long, TennisScores> newStandardScores = new HashMap<>();
        if(StrUtil.isNotEmpty(standardMatchScores.getScoreJson())){
            newStandardScores = JSON.parseObject(standardMatchScores.getScoreJson(), new TypeReference<Map<Long, TennisScores>>() {
            });
        }
        Map<Long, TennisScores> thirdMatchScores = new HashMap<>();
        if(matchScoresInfo!=null && StrUtil.isNotEmpty(matchScoresInfo.getScoresJson())){
            JSONObject periodBasketballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
            thirdMatchScores= JsonMapUtils.parseTennisMap(periodBasketballScores);
        }
        //足球外的其他球种 index字段传阶段值
        int index = matchSwitchDTO.getIndex();
        log.info("修改开关联动同步比分:index:{}",index);
        if(matchSwitchDTO.getStatus()==1){
            Long period = new Long(index);
            TennisScores thirdScores = thirdMatchScores.get(period);
            if(thirdScores==null){
                return standardMatchScores.getScoreJson();
            }
            TennisScores standScores = newStandardScores.get(period);
            if(standScores==null){
                standScores = new TennisScores(period);
            }
            standScores.setSetScore(thirdScores.getSetScore());
            newStandardScores.put(period,standScores);
        }
        calcWholeScores(newStandardScores,matchScoresInfo.getPeriod());
        log.info("修改开关联动同步比分 newStandardScores:{}",newStandardScores);
        return JSON.toJSONString(newStandardScores);
    }

    public void calcWholeScores(Map<Long, TennisScores> newStandardScores,Long period){
        Integer tgHome = 0;
        Integer tgAway = 0;
        Integer setHome = 0;
        Integer setAway = 0;
        for (Long periodId : newStandardScores.keySet()) {
            //查询比分时过滤阶段0 -- 脏数据
            if (!scoreCenterPeriod.contains(periodId)) {
                continue;
            }
            if(periodId==0L || periodId==-1L){
                continue;
            }
            TennisScores cc = newStandardScores.get(periodId);
            tgHome += cc.getSetScore().getHome();
            tgAway += cc.getSetScore().getAway();
            //当前事件是盘结束阶段
            if(setEndPeriod.contains(period)){
                if (cc.getSetScore().getHome() > cc.getSetScore().getAway()) {
                    setHome = setHome + 1;
                } else if (cc.getSetScore().getHome() < cc.getSetScore().getAway()){
                    setAway = setAway + 1;
                }
                continue;
            }
            if (cc.getSetScore().getHome() >= 7 || cc.getSetScore().getAway() >= 7) {
                if (cc.getSetScore().getHome() > cc.getSetScore().getAway()) {
                    setHome = setHome + 1;
                } else {
                    setAway = setAway + 1;
                }
            } else {
                //未到7分，相差2分为胜，例：6:4主队+1,6:5继续抢7
                if (cc.getSetScore().getHome() > cc.getSetScore().getAway()) {
                    if (cc.getSetScore().getHome() >= 6 && cc.getSetScore().getHome() - cc.getSetScore().getAway() >= 2) {
                        setHome = setHome + 1;
                    }
                } else if (cc.getSetScore().getHome() < cc.getSetScore().getAway()) {
                    if (cc.getSetScore().getAway() >= 6 && cc.getSetScore().getAway() - cc.getSetScore().getHome() >= 2) {
                        setAway = setAway + 1;
                    }
                }
            }

        }
        if(newStandardScores.get(WHOLE_MATCH)==null){
            newStandardScores.put(WHOLE_MATCH,new TennisScores());
        }
        newStandardScores.get(WHOLE_MATCH).setMatchScore(new CommonItem(setHome, setAway));
        newStandardScores.get(WHOLE_MATCH).setSetScore(new CommonItem(tgHome, tgAway));
    }

    /**
     * 如果非正常开打阶段直接清零当前局比分
     * @param matchScoresInfo
     * @param matchPeriodId
     * @param eventCode
     */
    public void buildStandardMatchScoreByMap(StandardMatchScores matchScoresInfo, Long matchPeriodId,String eventCode) {
        String scoresJson = matchScoresInfo.getScoreJson();
        JSONObject periodFootballScores = JSONObject.parseObject(scoresJson);
        Map<Long, TennisScores> allPeriodScores= JsonMapUtils.parseTennisMap(periodFootballScores);
        //如果非正常开打阶段直接清零当前局比分
        if(!SportPeriodConstant.TennisPeriod.contans(matchPeriodId)){
            if("tennis_score_change".equals(eventCode) || "match_status".equals(eventCode)){
                for (TennisScores value : allPeriodScores.values()) {
                    value.getCurrentScore().setHome(0);
                    value.getCurrentScore().setAway(0);
                }
            }
            matchScoresInfo.setScoreJson(JSONObject.toJSONString(allPeriodScores));
        }
    }
    @Override
    public MatchResultScoreMsgVo getSportMatchResultScores(StandardMatchInfo standardMatchInfo, Map scores) {
        // 比分,格式：["S1|0:2","S120|9:11","S121|9:11","S122|5:3"]
        List<String> score = new ArrayList<>();
        if(scores==null){
            log.info("比分修正下发：无比分:{}",standardMatchInfo.getId());
            return null;
        }
        JSONObject periodFootballScores = JSONObject.parseObject(JSON.toJSONString(scores));
        Map<Long, TennisScores> allPeriodScores= JsonMapUtils.parseTennisMap(periodFootballScores);
        for (Long periodId : allPeriodScores.keySet()) {
            if (!scoreCenterPeriod.contains(periodId)) {
                log.info("{}，比分修正下发，过滤阶段比分：{}",standardMatchInfo.getId(),periodId);
                continue;
            }
            log.info("{}，比分修正下发，组装阶段比分：{}",standardMatchInfo.getId(),periodId);
            if(allPeriodScores.get(periodId)!=null){
                log.info("{}，比分修正下发，组装阶段比分：{}",standardMatchInfo.getId(),allPeriodScores.get(periodId).getMatchScore().doScoreStr());
                score.add(getScoreCode(periodId) + "|" + allPeriodScores.get(periodId).getSetScore().doScoreStr());
            }
        }
        score.add("S1" + "|" + allPeriodScores.get(-1L).getMatchScore().doScoreStr());
        score.add("S115" + "|" + allPeriodScores.get(-1L).getSetScore().doScoreStr());
        MatchResultScoreMsgVo msgVo = new MatchResultScoreMsgVo();
        msgVo.setSportId(standardMatchInfo.getSportId());
        msgVo.setMatchId(standardMatchInfo.getId());
        msgVo.setModifyTime(System.currentTimeMillis());
        msgVo.setScore(score);
        return msgVo;
    }

    /**
     * 比分修正阶段匹配比分编码
     * @param periodId
     * @return
     */
    private String getScoreCode(Long periodId) {
        String code = "";
        if(periodId==8L){
            code = "S23";
        } else if (periodId==9L) {
            code = "S39";
        } else if (periodId==10L) {
            code = "S55";
        } else if (periodId==11L) {
            code = "S71";
        } else if (periodId==12L) {
            code = "S87";
        }
        return code;
    }

}
