package com.panda.merge.calculation.impl;


import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.enums.*;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.common.enums.EventCodeEnum;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.*;
import com.panda.merge.dto.*;
import com.panda.merge.dto.cache.FootballCacheScores;
import com.panda.merge.dto.scores.*;
import com.panda.merge.dto.sourceSwitch.FootballSwitch;
import com.panda.merge.model.*;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.repository.ThirdMatchInfoRepository;
import com.panda.merge.service.IScoresService;
import com.panda.merge.service.ThirdMatchInfoService;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.EffectScoresCode.RED_CARD;
import static com.panda.merge.constant.EffectScoresCode.YELLOW_RED_CARD;
import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;
import static com.panda.merge.utils.SettleNumToScoreCodeUtils.getFootballSettleScoreIndex;

/**
 * 足球 比分计算并入库
 *
 * @author idol
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2022-2-26 17:06:27
 * @see com.panda.merge.calculation.impl
 */

@Slf4j
@Service
public class FootballCalculationServiceImpl extends AbstractCalculationServiceImpl {
    @Autowired
    RedisService redisService;
    @Autowired
    IScoresService scoresService;
    @Autowired
    MatchScoreInfoRepository matchScoreInfoRepository;
    @Autowired
    ThirdMatchInfoRepository thirdMatchInfoRepository;
    @Autowired
    ThirdMatchInfoService thirdMatchInfoService;
    List<String> PenaltyEventCode = new ArrayList<>(Arrays.asList("penalty_awarded","retake_pen","penalty_missed","canceled_penalty","canceled_goal","canceled_var_penalty"));

    //进球角球红牌黄牌事件
    List<String> SCORES_EVENT = new ArrayList<>(Arrays.asList(EventCodeEnum.CORNER.code,EventCodeEnum.GOAL.code,EventCodeEnum.RED_CARD.code,EventCodeEnum.YELLOW_CARD.code));
    //修改时间事件4243需求
    List<String> MODIFY_TIME_EVENT = new ArrayList<>(Arrays.asList("goal_time_modified","redcard_time_modified","yellowcard_time_modified","corner_time_modified"));

    @Override
    public void calculationMatchScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) throws Exception {
        log.info("linkId::{}::processLivedataScores football scores start...",data.getLinkId());
        //是否是赛事比赛阶段
        //1.根据event_code 计算 当前事件
        String scoreStr = matchScoresInfo.getScoresJson();
        //比分事件上半场结束(31L) 统一改为 上半场(6L)(进球,角球,红牌,黄牌)
        //缓存比分事件，调整阶段
        fixPeriod(data);
        String deleteEventKey = "MATCH_DELETE_EVENT_ID:"+data.getThirdMatchId()+"_"+data.getThirdEventId();
        if(redisService.hasKey(deleteEventKey)){
            log.info("检测到先消费删除事件，比分事件linkId:{}，比分事件不处理：删除事件key:{}",data.getLinkId(),deleteEventKey);
            return;
        }
        //没数据的情况
        if(StringUtils.isEmpty(scoreStr)){
            log.info("linkId::{}::processLivedataScores football scores create start...",data.getLinkId());
            //创建ScoresJson信息  + 更新比分信息
            createScores(matchScoresInfo,data);
            log.info("linkId::{}::processLivedataScores football scores create end...",data.getLinkId());
            if(data.getEventCode().equals("goal")
                    ||data.getEventCode().equals("corner")
                    ||data.getEventCode().equals("yellow_card")
                    ||data.getEventCode().equals("red_card")){
                //第一个事件是进球角球红牌黄牌的,同步处理区间比分
                //5分钟比分计算
                createMinScores(matchScoresInfo,data,5);
                log.info("linkId::{}::processLivedataScores football create5MinsScores scores end...",data.getLinkId());
                //15分钟比分计算
                createMinScores(matchScoresInfo,data,15);
                log.info("linkId::{}::processLivedataScores football create15MinsScores scores end...",data.getLinkId());
                matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
            }
            log.info("createScores成功" + data.getEventCode() + "事件ID:" + data.getId()+",linkId="+data.getLinkId());
            return;
        }else {
            log.info("linkId::{}::processLivedataScores update football scores start...",data.getLinkId());

            //5分钟比分计算
            update5MinsScores(matchScoresInfo,data);
            log.info("linkId::{}::processLivedataScores football update5MinsScores scores end...",data.getLinkId());

            //15分钟比分计算
            update15MinsScores(matchScoresInfo,data);
            log.info("linkId::{}::processLivedataScores football update15MinsScores scores end...",data.getLinkId());

            //1.全阶段比分计算 判断该阶段数据是否存在，不存在则提供数据
            updateScores(matchScoresInfo,data);
            log.info("linkId::{}::processLivedataScores football updateScores scores end...",data.getLinkId());
        }
        if(SCORES_EVENT.contains(data.getEventCode())){
            String scoreEventKey = "MATCH_SCORES_EVENT_ID:"+data.getThirdMatchId()+"_"+data.getThirdEventId();
            //比分事件缓存3小时
            redisService.set(scoreEventKey,data,ConstantSystem.HOUR_1*3);
        }
       try {
           //足球控球率计算
           calcFootballEventTime(data,matchScoresInfo);
       } catch (Exception e) {
           log.error("更新控球率信息失败，linkId:{}",data.getLinkId(),e);
       }
//       finally {
//           matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
//       }

    }


    /**
     * 计算足球控球率
     * @param data
     * @param matchScoresInfo
     */
    private void calcFootballEventTime(MatchEventInfo data, MatchScoresInfo matchScoresInfo) {
        //获取当前赛事全部比分数据
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        //全场(-1)比分
        FootballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        FootballScores periodSores= allPeriodScores.get(data.getMatchPeriodId());
        if(periodSores==null){
            periodSores = new FootballScores(data.getMatchPeriodId());
        }
        // 公共事件时间统计
        String eventCode = data.getEventCode();
        String homeAway = data.getHomeAway();
        //控球率阶段过滤
        if(!EffectScoresCode.FOOTBALL_POSSESSION_PERIOD.contains(data.getMatchPeriodId())){
            log.info("::{}::当前事件不在控球率统计范围内，阶段不处理",data.getLinkId());
            return;
        }
        //控球事件过滤
        if(!EffectScoresCode.FOOTBALL_POSSESSION_EVENT.contains(eventCode)){
            log.info("::{}::当前事件不在控球率统计范围内，事件不处理",data.getLinkId());
            return;
        }
        String homeEventKey = "POSSESSION:HOME:EVENT:MATCHID:"+data.getThirdMatchId();
        String awayEventKey = "POSSESSION:AWAY:EVENT:MATCHID:"+data.getThirdMatchId();
        String publicEventKey = "POSSESSION:PUB:EVENT:MATCHID:"+data.getThirdMatchId();

        //获取此刻上一次的事件
        MatchEventInfo lastEvent = getLastEventTime(data,homeEventKey,awayEventKey,publicEventKey);
        if(lastEvent==null){
            log.info("{},更新控球率信息：lastEvent为空,HOME_KEY={},AWAY_KEY={},PUB_KEY={}",data.getLinkId(),homeEventKey,awayEventKey,publicEventKey);
            return;
        }
        //断开/间隔时间
        Long brokenTime = System.currentTimeMillis() - lastEvent.getCreateTime();
        //超出5分钟未有事件下发
        if(brokenTime>(5*1000*60)){
            log.info("::{}::更新控球率信息：上一个事件间隔到此时超过5分钟，不计算主客队时间：currentTime={},lastEvent={}",data.getLinkId(),System.currentTimeMillis(),lastEvent);
            //缓存事件
            if(TeamTypeEnum.HOME.getCode().equals(homeAway)){
                redisService.set(homeEventKey,data,7200);
            }else if(TeamTypeEnum.AWAY.getCode().equals(homeAway)){
                redisService.set(awayEventKey,data,7200);
            }else{
                redisService.set(publicEventKey,data,7200);
            }
            return;
        }
        log.info("::{}::更新控球率信息：上一个事件：homeAway={},createTime={}",data.getLinkId(),lastEvent.getHomeAway(),lastEvent.getCreateTime());

        //距离上一个home事件的时间差，汇总home总控球时间
        Integer possessionTime = (int)(System.currentTimeMillis() - lastEvent.getCreateTime());
        log.info("::{}::更新控球率信息：控球事件时间差：{}，上一个事件：{}",data.getLinkId(),possessionTime,lastEvent.getHomeAway());
        if(TeamTypeEnum.HOME.getCode().equals(lastEvent.getHomeAway())){
            buildPossessionData(wholeSores,possessionTime,TeamTypeEnum.HOME.getCode(), data.getLinkId());
        }else if(TeamTypeEnum.AWAY.getCode().equals(lastEvent.getHomeAway())){
            buildPossessionData(wholeSores,possessionTime,TeamTypeEnum.AWAY.getCode(), data.getLinkId());
        }else{
            buildPossessionData(wholeSores,possessionTime,"PUBLIC", data.getLinkId());
        }
        log.info("::{}::更新控球率信息：{} 计算结果==时间：{}，控球率：{}",data.getLinkId(),lastEvent.getHomeAway(),
                wholeSores.getPossessionTime(),wholeSores.getBallPossessionPercentage());
        if(TeamTypeEnum.HOME.getCode().equals(homeAway)){
            redisService.set(homeEventKey,data,7200);
        }else if(TeamTypeEnum.AWAY.getCode().equals(homeAway)){
            redisService.set(awayEventKey,data,7200);
        }else{
            redisService.set(publicEventKey,data,7200);
        }
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    private void buildPossessionData(FootballScores wholeSores,Integer possessionTime,String homeAway,String linkId){
        Integer awayAllTime = wholeSores.getPossessionTime().getAway();
        Integer homeAllTime = wholeSores.getPossessionTime().getHome();
        if(TeamTypeEnum.HOME.getCode().equals(homeAway)){
            homeAllTime += possessionTime;
        }else if(TeamTypeEnum.AWAY.getCode().equals(homeAway)){
            awayAllTime += possessionTime;
        }else{
            //上一个公共事件，不计算主客队控球时长
        }
        log.info("{},更新控球率信息1：控球时长：home={},away={}，控球率：home={}，away={}",linkId,homeAllTime,awayAllTime,
                wholeSores.getBallPossessionPercentage().getHome(),wholeSores.getBallPossessionPercentage().getAway());
        //添加控球时间，保存入库
        wholeSores.getPossessionTime().setHome(homeAllTime);
        wholeSores.getPossessionTime().setAway(awayAllTime);

        //控球率客A% =  事件持续时间A /  (事件持续时间H + 事件持续时间A )* 100 //控球率主H% = 事件持续时间H / (事件持续时间H + 事件持续时间A )* 100
        BigDecimal homeTime = new BigDecimal(homeAllTime+"");
        BigDecimal awayTime = new BigDecimal(awayAllTime+"");
        BigDecimal homeAndAway = homeTime.add(awayTime);
        if(homeAndAway.compareTo(BigDecimal.ZERO)==0){
            log.info("{},更新控球率信息：控球时长0",linkId);
            return;
        }
        BigDecimal onehundred = new BigDecimal("100");
        BigDecimal possessionHome = homeTime.divide(homeAndAway,2,4).multiply(onehundred);
        BigDecimal possessionAway = awayTime.divide(homeAndAway,2,4).multiply(onehundred);
//        int possessionHome = homeAllTime / (homeAllTime + awayAllTime) * 100;
//        int possessionAway = awayAllTime / (homeAllTime + awayAllTime) * 100;
        wholeSores.getBallPossessionPercentage().setHome(possessionHome.intValue());
        wholeSores.getBallPossessionPercentage().setAway(possessionAway.intValue());
        log.info("{},更新控球率信息2：控球时长：{}-{}，控球率：{}-{}",linkId,homeAllTime,awayAllTime,
                wholeSores.getBallPossessionPercentage().getHome(),wholeSores.getBallPossessionPercentage().getAway());
    }

    /**
     * 此前最后一个持球事件
     * @param data
     */
    private MatchEventInfo getLastEventTime(MatchEventInfo data,String homeEventKey,String awayEventKey,String publicEventKey) {
        //上一个持球事件 //保存事件的key
        Long homeTime = 0L;
        Long awayTime = 0L;
        Long pubTime = 0L;
        Object hasHomeEvent = redisService.get(homeEventKey);
        Object hasAwayEvent = redisService.get(awayEventKey);
        Object hasPublicEvent = redisService.get(publicEventKey);
        if (null != hasHomeEvent) {
            MatchEventInfo dh = JSONUtil.toBean(JSONUtil.toJsonStr(hasHomeEvent), MatchEventInfo.class);
//            MatchEventInfo dh = (MatchEventInfo) hasHomeEvent;
            homeTime = dh.getCreateTime();
        } else {
            if (TeamTypeEnum.HOME.getCode().equals(data.getHomeAway())) {
                redisService.set(homeEventKey, data, 7200);
            }
        }
        if (null != hasAwayEvent) {
            MatchEventInfo da = JSONUtil.toBean(JSONUtil.toJsonStr(hasAwayEvent), MatchEventInfo.class);
//            MatchEventInfo da = (MatchEventInfo) hasAwayEvent;
            awayTime = da.getCreateTime();
        } else {
            if (TeamTypeEnum.AWAY.getCode().equals(data.getHomeAway())) {
                redisService.set(homeEventKey, data, 7200);
            }
        }
        if (null != hasPublicEvent) {
            MatchEventInfo dp = JSONUtil.toBean(JSONUtil.toJsonStr(hasPublicEvent), MatchEventInfo.class);
//            MatchEventInfo dp = (MatchEventInfo) hasPublicEvent;
            pubTime = dp.getCreateTime();
        } else {
            if (StrUtil.isEmpty(data.getHomeAway()) || "none".equals(data.getHomeAway())) {
                redisService.set(publicEventKey, data, 7200);
            }
        }
        log.info("{}:更新控球率信息，已有事件:home:{},away:{},public:{}", data.getLinkId(), homeTime, awayTime, pubTime);
        //获取最后一个事件的时间和主客队
        MatchEventInfo lastEvent = new MatchEventInfo();
        if (homeTime >= awayTime && homeTime >= pubTime) {
//            lastEvent = (MatchEventInfo) hasHomeEvent;
            lastEvent = JSONUtil.toBean(JSONUtil.toJsonStr(hasHomeEvent), MatchEventInfo.class);
            log.info("{}:更新控球率信息，获取上一个事件:home:{},{}", data.getLinkId(), homeTime, lastEvent);
        } else if (awayTime >= homeTime && awayTime >= pubTime) {
//            lastEvent = (MatchEventInfo) hasAwayEvent;
            lastEvent = JSONUtil.toBean(JSONUtil.toJsonStr(hasAwayEvent), MatchEventInfo.class);
            log.info("{}:更新控球率信息，获取上一个事件:away:{},{}", data.getLinkId(), awayTime, lastEvent);
        } else {
//            lastEvent = (MatchEventInfo) hasPublicEvent;
            lastEvent = JSONUtil.toBean(JSONUtil.toJsonStr(hasPublicEvent), MatchEventInfo.class);
            log.info("{}:更新控球率信息，获取上一个事件:away:{},{}", data.getLinkId(), hasPublicEvent, lastEvent);
        }
        return lastEvent;

    }
    /**
     * 调整,适配阶段ID
     * @param data
     */
    private void changePeriodByExtryPeriodEvent(MatchEventInfo data) {
        if(data.getMatchPeriodId().equals(31L)){
            data.setMatchPeriodId(6L);
        }
        if(data.getMatchPeriodId().equals(8L) || data.getMatchPeriodId().equals(100L)){
            data.setMatchPeriodId(7L);
        }
        if(data.getMatchPeriodId().equals(33L)){
            data.setMatchPeriodId(41L);
        }
        if(data.getMatchPeriodId().equals(43L) || data.getMatchPeriodId().equals(110L)){
            data.setMatchPeriodId(42L);
        }
    }
    /**
     * 比分事件上半场结束(31L) 统一改为 上半场(6L)(进球,角球,红牌,黄牌)
     * 和接入有约定，中场休息进球 角球罚牌 ，统一记为上半场。如果是场外的话，让数据商不要下发
     * 或者由接入识别不要下发给我们
     * */
    //进球,角球,红牌,黄牌赛事事件都改为上半场 6L
    private MatchEventInfo fixPeriod(MatchEventInfo data){
        //中场休息期间下发上半场事件，补充为上半场的比分 82754
        if(data.getEventCode().equals("goal") || data.getEventCode().equals("corner") || data.getEventCode().equals("red_card") || data.getEventCode().equals("yellow_card")
                || data.getEventCode().equals("penalty_missed")) {
            //上半场结束(31L)改为 上半场(6L)
            if(data.getMatchPeriodId().equals(31L)){
                data.setMatchPeriodId(6L);
            }
        }
        //删除事件--被删除事件未消费到，所以不处理
        //进球事件--查询是否存在删除事件，如果有，进球事件也不处理。
        String scoresEventKey = "MATCH_SCORES_EVENT_ID:"+data.getThirdMatchId()+"_"+data.getThirdEventId();
        redisService.set(scoresEventKey,data,7200);
        return data;
    }
    private void update15MinsScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) {

        //1.阶段过滤 6L, 7L, 41L, 42L, 50L
        if(!SportPeriodConstant.FootballPeriod.contans(data.getMatchPeriodId())){
            return;
        }
        //3.计算15分钟阶段 编码
        Long period15 =SportPeriodConstant.FootballPeriod.get15MinPeriod(data.getMatchPeriodId(),data.getSecondsFromStart());
        if(period15==null){
            return;
        }
        log.info("{}:更新15分钟比分,{},{}", data.getLinkId(), period15, data.getMatchPeriodId());
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        FootballScores periodScores= allPeriodScores.get(period15);
        //新建该阶段值
        if(periodScores==null) {
            periodScores = FootballScores.createMinFootballScores();
            allPeriodScores.put(period15, periodScores);
        }
        //2.事件过滤
        if(!(data.getEventCode().equals("goal")||data.getEventCode().equals("corner")||data.getEventCode().equals("yellow_card")||data.getEventCode().equals("red_card"))){
            matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
            matchScoresInfo.setModifyTime(System.currentTimeMillis());
            matchScoresInfo.setEventTime(data.getEventTime());
//            matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
//            matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
            save15MinScores(matchScoresInfo,matchScoresInfo.getThirdMatchId());
            return;
        }
        boolean isAddScores = isAddScores(data);
        if (isAddScores) {
            periodScores.addEventScores(data.getEventCode(), data.getHomeAway());
        } else {
            periodScores.set15MinuteFieldByEventCode(data,allPeriodScores);
        }
        periodScores.countFaCard();
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
        matchScoresInfo.setEventTime(data.getEventTime());
//        matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
        save15MinScores(matchScoresInfo,matchScoresInfo.getThirdMatchId());
    }

    /**
     * 删除15分钟比分
     * @param matchScoresInfo
     * @param data
     */
    private void delete15MinsScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        try {
            MatchEventInfo oldMatchInfo = super.getOldMatchInfoByCancel(data);
            if (oldMatchInfo == null) {
                //原有事件不存在的不取消事件
                oldMatchInfo = data;
            }
            String scoresEventKey = "MATCH_SCORES_EVENT_ID:"+data.getThirdMatchId()+"_"+data.getThirdEventId();
            Object obj = redisService.get(scoresEventKey);
            if(obj!=null){
                MatchEventInfo scoreEvent = (MatchEventInfo) obj;
                if(scoreEvent.getSecondsFromStart()!=null && scoreEvent.getSecondsFromStart()>0){
                    oldMatchInfo.setSecondsFromStart(scoreEvent.getSecondsFromStart());
                }
            }
            //3.计算15分钟阶段
            Long period15 = SportPeriodConstant.FootballPeriod.get15MinPeriod(oldMatchInfo.getMatchPeriodId(), oldMatchInfo.getSecondsFromStart());
            if (period15 == null) {
                return;
            }
            log.info("{}:删除15分钟比分,{},{}", data.getLinkId(), period15, data.getMatchPeriodId());
            JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
            //存入所有阶段比分
            Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
            FootballScores periodScores = allPeriodScores.get(period15);
            //新建该阶段值
            if (periodScores == null) {
                return;
            }
            //2.事件过滤
            if (!(oldMatchInfo.getEventCode().equals("goal") || oldMatchInfo.getEventCode().equals("corner")
                    ||data.getEventCode().equals("yellow_card")||data.getEventCode().equals("red_card"))) {
                return;
            }
            periodScores.set15MinuteFieldByEventCode(data, allPeriodScores);
            //计算发牌
            periodScores.countFaCard();
            matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
            matchScoresInfo.setModifyTime(System.currentTimeMillis());
            matchScoresInfo.setEventTime(data.getEventTime());
//            matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
//            matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
            save15MinScores(matchScoresInfo, matchScoresInfo.getThirdMatchId());
        }catch (Exception e){
            
            log.error(":处理数据发生异常:", e);
        }
    }

    /**
     * 删除5分钟比分
     * @param matchScoresInfo
     * @param data
     */
    private void delete5MinsScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        try {
            MatchEventInfo oldMatchInfo = super.getOldMatchInfoByCancel(data);
            if (oldMatchInfo == null) {
                //原有事件不存在的不取消事件
                oldMatchInfo = data;
            }
            //3.计算5分钟阶段
            Long period5 = SportPeriodConstant.FootballPeriod.get5MinPeriod(oldMatchInfo.getMatchPeriodId(), oldMatchInfo.getSecondsFromStart());
            if (period5 == null) {
                return;
            }
            log.info("{}:删除5分钟比分,{},{}", data.getLinkId(), period5, data.getMatchPeriodId());

            JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
            //存入所有阶段比分
            Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
            FootballScores periodScores = allPeriodScores.get(period5);
            //新建该阶段值
            if (periodScores == null) {
                return;
            }
            //2.事件过滤
            if (!(oldMatchInfo.getEventCode().equals("goal") || oldMatchInfo.getEventCode().equals("corner"))) {
                return;
            }
            periodScores.set5MinuteFieldByEventCode(data, allPeriodScores);
            matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
            matchScoresInfo.setModifyTime(System.currentTimeMillis());
            matchScoresInfo.setEventTime(data.getEventTime());
//            matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
//            matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
            //15分钟已经下发过
            //save15MinScores(matchScoresInfo, matchScoresInfo.getThirdMatchId());
        }catch (Exception e){
            
            log.error(":处理数据发生异常:", e);
        }
    }


    private void update5MinsScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        log.info("update5MinsScores 分钟比分计算:"+data.getEventCode()+"事件ID:"+data.getThirdEventId());
        //1.阶段过滤 6L, 7L, 41L, 42L, 50L
        if(!SportPeriodConstant.FootballPeriod.contans(data.getMatchPeriodId())){
            return;
        }
        //3.计算15分钟阶段 编码
        Long period5 =SportPeriodConstant.FootballPeriod.get5MinPeriod(data.getMatchPeriodId(),data.getSecondsFromStart());
        if(period5==null){
            return;
        }
        log.info("{}:更新5分钟比分,{},{}", data.getLinkId(), period5, data.getMatchPeriodId());

        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        FootballScores periodScores= allPeriodScores.get(period5);
        //新建该阶段值
        if(periodScores==null) {
            periodScores = FootballScores.createMinFootballScores();
            allPeriodScores.put(period5, periodScores);
        }
        //2.事件过滤
        if(!(data.getEventCode().equals("goal")||data.getEventCode().equals("corner"))){
            matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
            matchScoresInfo.setModifyTime(System.currentTimeMillis());
            matchScoresInfo.setEventTime(data.getEventTime());
//            matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
//            matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
            //save15MinScores(matchScoresInfo,matchScoresInfo.getThirdMatchId());
            return;
        }
        boolean isAddScores = isAddScores(data);
        if (isAddScores) {
            periodScores.addEventScores(data.getEventCode(), data.getHomeAway());
        } else {
            periodScores.set5MinuteFieldByEventCode(data,allPeriodScores);
        }
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
        matchScoresInfo.setEventTime(data.getEventTime());
//        matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
      //  save15MinScores(matchScoresInfo,matchScoresInfo.getThirdMatchId());
    }


    /**
     * 保存redis缓存15分钟玩法比分;给赔率服务调用获取赔率基准分
     * @param matchScoreInfo
     * @param thirdMatchId
     */
    public void save15MinScores( MatchScoresInfo matchScoreInfo ,Long thirdMatchId) {
        //主客队相反标识 通过复制对象 绕开变更
        MatchScoresInfo matchScoreInfoCP=new MatchScoresInfo();
        BeanUtils.copyProperties(matchScoreInfo,matchScoreInfoCP);
        scoresService.changeHomeAway(matchScoreInfoCP,null);
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoreInfoCP.getScoresJson());
        if (ObjectUtils.isEmpty(periodFootballScores)) {
            return;
        }
        Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
//        ThirdMatchInfo thirdMatchInfo=thirdMatchInfoMapper.selectByPrimaryKey(thirdMatchId);
//        ThirdMatchInfo thirdMatchInfo= scoresRedisHelp.getCatchThirdMatchInfoByPrimaryKey(thirdMatchId);
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoRepository.selectThirdMatchInfoByPrimaryKey(thirdMatchId);

        if(thirdMatchInfo.getReferenceId()==null||thirdMatchInfo.getReferenceId().equals(0l)){
            return;
        }
        /**只有标准赛事事件源比分才写入*/
        //验证标准赛事和开售信息
        if(!scoresService.checkStandardScore(thirdMatchInfo)){
            log.info("save15MinScores退出，无赛事或数据源编码/三方赛事ID不匹配:"+thirdMatchId+"::-----:"+thirdMatchInfo.getDataSourceCode());
            return;
        }
        Map<String,FootballCacheScores> matchAbScores=new HashMap<>();
        for (Map.Entry<Long, FootballScores> entry : allPeriodScores.entrySet()) {
            Long period= entry.getKey();
            if(period<60899){
                continue;
            }
            FootballCacheScores footballCacheScores=new FootballCacheScores();
            footballCacheScores.setGoal(entry.getValue().getGoal());
            footballCacheScores.setCorner(entry.getValue().getCorner());
            footballCacheScores.setFaCard(entry.getValue().getFaCard());
            footballCacheScores.setRedCard(entry.getValue().getRedCard());
            footballCacheScores.setYellowCard(entry.getValue().getYellowCard());
            //根据比分阶段编号 返回阶段时间范围 如60899L  -> "1-15"
            String abPeriod=SportPeriodConstant.FootballPeriod.getAbPeriod(period);
            if(abPeriod==null){
                continue;
            }
            matchAbScores.put(abPeriod,footballCacheScores);
        }
        //缺乏开售逻辑判断 KB-1390
        String key ="ABSCORES:"+thirdMatchInfo.getReferenceId();
        key = DigestUtil.md5Hex(key);
        //缓存比分信息
        redisService.set(key,JSONObject.toJSONString(matchAbScores),259200);
//        redisService.expire(key,259200);
    }


    /**
     * 更新比分信息
     * @param matchScoresInfo  库中比分信息
     * @param data             本次传入事件信息
     * @throws Exception
     */
    private void updateScores( MatchScoresInfo matchScoresInfo, MatchEventInfo data) throws Exception {
        //下半场开球事件屏蔽
        if("kick_off_team".equals(data.getEventCode()) && data.getMatchPeriodId().equals(7l)){
            log.info("updateScores,单条事件处理逻辑,linkId={},下半场开球事件无需处理！",data.getLinkId());
            return;
        }
        //需要处理的阶段,6L, 7L, 41L, 42L, 50L
        if(!SportPeriodConstant.FootballPeriod.contans(data.getMatchPeriodId())){
            log.info("updateScores,单条事件处理逻辑,linkId={},阶段={}无需处理！",data.getLinkId(),data.getMatchPeriodId());
            return;
        }
        if(PenaltyEventCode.contains(data.getEventCode()) && !data.getMatchPeriodId().equals(50L) ){
            //点球事件不处理比分(点球大战阶段除外)
            log.info("updateScores,单条事件处理逻辑,linkId={},点球事件无需处理！",data.getLinkId());
            return;
        }
        //获取当前赛事全部比分数据
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        //全场(-1)比分
        FootballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        //2黄一红 事件特别处理
        if(YELLOW_RED_CARD.equals(data.getEventCode())){
            data.setEventCode(RED_CARD);
        }
        //获取当前阶段的比分
        FootballScores periodScores = allPeriodScores.get(data.getMatchPeriodId());
        // 如果是goal,判断前面是否存在点球事件，从而决定当前是进球还是点球进球
        if("goal".equals(data.getEventCode())){
            String key = "PENALTY_EVENT_" + data.getThirdMatchId()+"_"+data.getDataSourceCode();
            Object redisPenaltyCodeObj = redisService.get(key);
            if(redisPenaltyCodeObj != null){
                String redisPenaltyCode = redisPenaltyCodeObj.toString();
                if("penalty_awarded".equals(redisPenaltyCode) || "retake_pen".equals(redisPenaltyCode)){
//                    data.setEventCode("penalty_score");
                     //0528
                    log.info("点球进球事件+++++++++++，{},====,{}",data.getLinkId(),wholeSores.getPenaltyAwarded());
                    if(ConstantSystem.HOME.equals(data.getHomeAway())){
                        wholeSores.getPenaltyAwarded().setHome(wholeSores.getPenaltyAwarded().getHome()+1);
                    }else if(ConstantSystem.AWAY.equals(data.getHomeAway())){
                        wholeSores.getPenaltyAwarded().setAway(wholeSores.getPenaltyAwarded().getAway()+1);
                    }
                    //缓存当前点球进球事件，以防下次下发扣回进球取消
                    redisService.set("GOAL_" + data.getThirdMatchId()+"_"+data.getDataSourceCode(), 1, 300);
                }else if("penalty_missed".equals(redisPenaltyCode) ||
                        "canceled_penalty".equals(redisPenaltyCode)||
                        "canceled_var_penalty".equals(redisPenaltyCode)){
                    data.setEventCode("goal");
                }
                redisService.del(key);
            }
        }
        if(wholeSores == null || data.getMatchPeriodId() == null){
            log.info("updateScores,单条事件处理逻辑,linkId={},源赛事ID={},三方赛事ID:{},全场(-1)比分为空或者赛事阶段值为空！",data.getLinkId(),data.getThirdMatchSourceId(),matchScoresInfo.getThirdMatchId());
            return;
        }

        //新建该阶段值
        if(periodScores==null) {
            periodScores = new FootballScores(data.getMatchPeriodId());
            allPeriodScores.put(data.getMatchPeriodId(), periodScores);
        }

        //1.如果是点球事件直接赋值返回
        if(data.getMatchPeriodId().equals(50L)){
            calulationPenaltyScores(matchScoresInfo,data);
            //进球事件处理
            if("goal".equals(data.getEventCode())){
                periodScores.getGoal().setHome(data.getT1());
                periodScores.getGoal().setAway(data.getT2());
                matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
            }
            log.info("updateScores,单条事件逻辑处理,linkId="+data.getLinkId()+",点球大战处理结束");
            return;
        }

        //校验比分信息是否为空
        boolean isAddScores = isAddScores(data);
        if (isAddScores) {
            //当前阶段比分处理
            periodScores.addEventScores(data.getEventCode(), data.getHomeAway());
            //全场(-1)比分处理
            wholeSores.addEventScores(data.getEventCode(), data.getHomeAway());
        } else {
            //当前阶段的比分处理
            periodScores.setFieldByEventCode(data,allPeriodScores);
        }
        //全场射正比分
        wholeSores.doShot();
        //当前阶段射正比分
        periodScores.doShot();
        //全场KickOff事件
        wholeSores.countKickOff(data);
        //4243需求，下发XX_time_modify事件修改比分区间和时间
        if(MODIFY_TIME_EVENT.contains(data.getEventCode())){
            String lastScoreEventId = data.getRemark();
            String scoreEventKey = "MATCH_SCORES_EVENT_ID:"+data.getThirdMatchId()+"_"+lastScoreEventId;
            log.info("doModifyTimeScores,linkId={}，{} ",data.getLinkId(),scoreEventKey);
            MatchEventInfo lastEvent = null;
            Object obj = redisService.get(scoreEventKey);
            if(obj!=null){
                lastEvent = (MatchEventInfo) obj;
            }
            if(lastEvent==null){
                log.info("获取修改时间的目标事件失败，数据异常或缓存已过期,linkId={}，{} ",data.getLinkId(),scoreEventKey);
                return;
            }
            periodScores.doModifyTimeScores(data,allPeriodScores,15,lastEvent);
            periodScores.doModifyTimeScores(data,allPeriodScores,5,lastEvent);
            //如目标事件与删除事件所属阶段不一致，也需处理对应阶段的比分，比如当前事件为下半场， 目标事件是上半场
            periodScores.doModifyTimeScores(data,allPeriodScores,0,lastEvent);

        }
        //全场&当前阶段比分赋值
        matchScoresInfo.setT1(wholeSores.getGoal().getHome());
        matchScoresInfo.setT2(wholeSores.getGoal().getAway());
        matchScoresInfo.setPeriodT1(periodScores.getGoal().getHome());
        matchScoresInfo.setPeriodT2(periodScores.getGoal().getAway());
        //当前阶段新增事件值 或者设置当前事件值
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    /**
     * 统计点球信息
     * @param matchScoresInfo
     * @param data
     */
    private void calulationPenaltyScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        log.info("calulationPenaltyScores,单条事件逻辑处理,linkId="+data.getLinkId()+",点球大战处理");
        try{
            String key ="PenaltyEvent:"+data.getStandardMatchId()+":"+data.getDataSourceCode()+":"+data.getThirdEventId();
            if(redisService.get(key) != null){
                log.info("calulationPenaltyScores,单条事件逻辑处理,linkId="+data.getLinkId()+",点球大战缓存中已存在,跳过处理！");
                return;
            }else {
                redisService.set(key,key,100);
            }
            //scoresJsonExtra 点球信息
            String extraScores = matchScoresInfo.getScoresJsonExtra();
            FootballPenaltyScores footballPenaltyScores;
            if(StringUtils.isEmpty(extraScores)){
                //初始化点球大战轮次比分
                footballPenaltyScores =new FootballPenaltyScores();
            }else {
                footballPenaltyScores= JSONObject.toJavaObject((JSONObject.parseObject(extraScores)) , FootballPenaltyScores.class);
            }
            //主要逻辑
            footballPenaltyScores.calutionPenaltyScores(data);
            matchScoresInfo.setScoresJsonExtra(JSONObject.toJSONString(footballPenaltyScores));
        }catch (Exception e){
            log.error("calulationPenaltyScores,单条事件逻辑处理,linkId="+data.getLinkId()+",点球大战事件处理异常,Exception:", e);
        }
    }

    /**
     * 比分json转map, 方便数据组装
     * @param sjon
     * @return
     */
    public   Map<String,Object> buildMatchScoreByMap(String sjon){
        if(StringUtils.isEmpty(sjon)){
            return new HashMap<>();
        }
        JSONObject periodFootballScores = JSONObject.parseObject(sjon);
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        //.定义要求结果
        Map<String,Object> matchScore =new HashMap<>();
        //1.求全场数据
        CommonItem whole =new CommonItem();
        //2.求当前半场数据
        CommonItem period =new CommonItem();
        //3.求加时赛数据
        CommonItem overtime =new CommonItem();
        //4.求点球大战数据
        CommonItem penalty =new CommonItem();
        //5.组装返回string
        Long periodKey= 0l;
        for (Map.Entry<Long, FootballScores> entry : allPeriodScores.entrySet()) {
            if(entry.getKey().equals(6l)||entry.getKey().equals(7l)){
                whole.setHome(whole.getHome()+entry.getValue().getGoal().getHome());
                whole.setAway(whole.getAway()+entry.getValue().getGoal().getAway());
                if(periodKey<entry.getKey()){
                    periodKey=entry.getKey();
                    period.setHome(entry.getValue().getGoal().getHome());
                    period.setAway(entry.getValue().getGoal().getAway());
                }
            }
            if(entry.getKey().equals(41L)||entry.getKey().equals(42L)){
                overtime.setHome(overtime.getHome()+entry.getValue().getGoal().getHome());
                overtime.setAway(overtime.getAway()+entry.getValue().getGoal().getAway());
                matchScore.put("overtimeScore",overtime);
            }
        }
        matchScore.put("wholeScore",whole);
        matchScore.put("periodScore",period);
        FootballScores wholeSores= allPeriodScores.get(WHOLE_MATCH.longValue());
        if(wholeSores!=null&&allPeriodScores.get(50L)!=null){
            matchScore.put("penaltyShootout",allPeriodScores.get(50L).getGoal());
        }
        matchScore.put("minutesGoalScore",build15MinuteMatchScores(allPeriodScores,"goal"));
        matchScore.put("minutesCornerScore",build15MinuteMatchScores(allPeriodScores,"corner"));
        return matchScore;
    }
    /**
     * 比分json转map, 方便数据组装
     * @param type
     * @return
     */
    private Object build15MinuteMatchScores(Map<Long, FootballScores> allPeriodScores,String type) {
        Map<String,Object> map=new HashMap<>();
        if(type.equals("goal")){
            for (Map.Entry<Long, FootballScores> entry : allPeriodScores.entrySet()) {
                if(entry.getKey()!=null&&entry.getKey()>60000){
                    map.put(entry.getKey().toString(),entry.getValue().getGoal());
                }
            }
            return map;
        }
        if(type.equals("corner")){
            for (Map.Entry<Long, FootballScores> entry : allPeriodScores.entrySet()) {
                if(entry.getKey()!=null&&entry.getKey()>60000){
                    map.put(entry.getKey().toString(),entry.getValue().getCorner());
                }
            }
            return map;
        }
        return map;
    }

    public   Map<String,CommonItem> buildMatchScore2ByMap(String sjon){
        if(StringUtils.isEmpty(sjon)){
            return new HashMap<>();
        }
        JSONObject periodFootballScores = JSONObject.parseObject(sjon);
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        //.定义要求结果
        Map<String,CommonItem> matchScore =new HashMap<>();
        //1.求全场数据
        CommonItem whole =new CommonItem();
        //2.求当前半场数据
        CommonItem period =new CommonItem();
        //3.求加时赛数据
        CommonItem overtime =new CommonItem();
        //4.求点球大战数据
        CommonItem penalty =new CommonItem();

        for (Map.Entry<Long, FootballScores> entry : allPeriodScores.entrySet()) {
            if(entry.getKey().equals(6l)||entry.getKey().equals(7l)){
                whole.setHome(whole.getHome()+entry.getValue().getGoal().getHome());
                whole.setAway(whole.getAway()+entry.getValue().getGoal().getAway());
                if(entry.getKey().equals(6L)){
                    period.setHome(entry.getValue().getGoal().getHome());
                    period.setAway(entry.getValue().getGoal().getAway());
                }
            }
            if(entry.getKey().equals(41L)||entry.getKey().equals(42L)){
                overtime.setHome(overtime.getHome()+entry.getValue().getGoal().getHome());
                overtime.setAway(overtime.getAway()+entry.getValue().getGoal().getAway());
                matchScore.put("overtimeScore",overtime);
            }
        }
        matchScore.put("wholeScore",whole);
        matchScore.put("periodScore",period);
        FootballScores wholeSores= allPeriodScores.get(WHOLE_MATCH.longValue());
        if(wholeSores!=null&&allPeriodScores.get(50L)!=null){
            matchScore.put("penaltyShootout",allPeriodScores.get(50L).getGoal());
        }
        return matchScore;
    }

    /**
     * 创建比分对象
     * @param matchScoresInfo
     * @param data
     * @throws Exception
     */
    private void createScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) throws Exception {
        if(data.getEventCode().equals(YELLOW_RED_CARD)){
            data.setEventCode(RED_CARD);
        }
        //破发事件计算
        //比分计算只有上半场,下半场,上半场加时赛,下半场加时赛,点球大战
        if(!SportPeriodConstant.FootballPeriod.contans(data.getMatchPeriodId())){
            log.info("linkId::{}::processLivedataScores football scores create :阶段不匹配...",data.getLinkId());
            return;
        }
        Map<Long, FootballScores> periodFootballScores= new HashMap<>();
        FootballScores footballScores=new FootballScores(data.getMatchPeriodId());
        periodFootballScores.put(WHOLE_MATCH,footballScores);
        periodFootballScores.put(data.getMatchPeriodId(),footballScores);
        //校验比分是否为空
        boolean isAddScores= isAddScores(data);
        if(isAddScores){
            footballScores.addEventScores(data.getEventCode(),data.getHomeAway());
        }else {
            footballScores.setFieldByEventCode(data.getThirdMatchId(),data.getEventCode(),data.getT1(),data.getT2());
        }
        //谁先开球
        footballScores.countKickOff(data);
        matchScoresInfo.setT1(footballScores.getGoal().getHome());
        matchScoresInfo.setT2(footballScores.getGoal().getAway());
        matchScoresInfo.setPeriodT1(footballScores.getGoal().getHome());
        matchScoresInfo.setPeriodT2(footballScores.getGoal().getAway());
        //3.更新比分模板
        periodFootballScores.put(data.getMatchPeriodId(),((JSONObject) JSONObject.toJSON(footballScores)).toJavaObject(FootballScores.class));
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodFootballScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
        log.info("createScores成功" + data.getEventCode() + "事件ID:" + data.getId()+",linkId="+data.getLinkId());
        //4.返回成功
//        footballScores.setFieldByEventCode(data.getEventCode(),data.)  计算方式通过配置实现
    }


    /**
     * 创建区间比分对象
     * @param matchScoresInfo
     * @param data
     * @throws Exception
     */
    private void createMinScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data,Integer min) {
        if(data.getEventCode().equals(YELLOW_RED_CARD)){
            data.setEventCode(RED_CARD);
        }
        Long peroid = null;
        if(min==5){
            peroid =SportPeriodConstant.FootballPeriod.get5MinPeriod(data.getMatchPeriodId(),data.getSecondsFromStart());
        }else if (min==15){
            peroid =SportPeriodConstant.FootballPeriod.get15MinPeriod(data.getMatchPeriodId(),data.getSecondsFromStart());
        }
        //3.计算15分钟阶段 编码
        if(peroid==null){
            log.info("linkId::{}::processLivedataScores football scores create :阶段不匹配...",data.getLinkId());
            return;
        }
        if(!matchScoresInfo.getScoresJson().isEmpty()){
            //获取当前赛事全部比分数据
            JSONObject footballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
            Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(footballScores);
            FootballScores footballMinScores=allPeriodScores.get(peroid);
            if(footballMinScores==null){
                footballMinScores = new FootballScores(peroid);
                allPeriodScores.put(peroid,footballMinScores);
            }
            footballMinScores.setFieldByEventCode(data.getThirdMatchId(),data.getEventCode(),data.getT1(),data.getT2());
             //3.更新比分模板
            allPeriodScores.put(peroid,footballMinScores);
            matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
            matchScoresInfo.setModifyTime(System.currentTimeMillis());
            log.info("::{}::{},初始化比分createMinScores:{}",data.getLinkId(),peroid,JSONObject.toJSON(footballMinScores));
        }
    }


    /**
     * 足球取消事件下发逻辑
     * @param matchScoresInfo
     * @param data
     * @param isAgain
     * @throws Exception
     */
    @Override
    public void cancelEvent(MatchScoresInfo matchScoresInfo, MatchEventInfo data , boolean isAgain,Boolean isReissue) throws Exception {
        //比分事件上半场结束(31L) 统一改为 上半场(6L)(进球,角球,红牌,黄牌)
//        fixPeriod(data);
        Long periodId=data.getMatchPeriodId();
        if(data.getEventCode().equals("goal") || data.getEventCode().equals("corner") || data.getEventCode().equals("red_card") || data.getEventCode().equals("yellow_card")){
            //上半场结束(31L)改为 上半场(6L)
            if(data.getMatchPeriodId().equals(31L)){
                data.setMatchPeriodId(6L);
            }
            //84170 针对下半场删除上半场的事件，上游添加特殊标志
            //比分处理上半场阶段
//            if(data.getMatchPeriodId().equals(7L) && "1".equals(data.getAddition5())){
//                data.setMatchPeriodId(6L);
//            }
        }
        //缓存删除事件，如果比分事件比删除事件晚消费 则不处理
        String deleteEventKey = "MATCH_DELETE_EVENT_ID:"+data.getThirdMatchId()+"_"+data.getExtraInfo();
        redisService.set(deleteEventKey,data,7200);

        String scoresEventKey = "MATCH_SCORES_EVENT_ID:"+data.getThirdMatchId()+"_"+data.getExtraInfo();
        if(!redisService.hasKey(scoresEventKey)){
            log.info("检测到未消费被删除事件，删除事件不处理,linkId:{}，被删除事件id:{}",data.getLinkId(),scoresEventKey);
            return;
        }
        //删除点球事件，删除判定为点球的redis数据
        String key = "PENALTY_EVENT_" + data.getThirdMatchId()+"_"+data.getDataSourceCode();
        if("penalty_awarded".equals(data.getEventCode()) ){
            redisService.del(key);
            return;
        }
        delete15MinsScores(matchScoresInfo,data);
        delete5MinsScores(matchScoresInfo,data);
        deleteScores(matchScoresInfo,data,isReissue);
        //上面84170转换了阶段，处理了上半场比分，变更回原来的阶段，用来下发
        //不变更阶段，只修改比分
//        if("1".equals(data.getAddition5())){
//            data.setMatchPeriodId(periodId);
//        }

    }

    private void deleteScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data,Boolean isReissue)throws Exception  {
        log.info("删除事件canleEvent，score-center:linkId::{}::",data.getLinkId());

        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        FootballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        FootballScores oldSores= allPeriodScores.get(data.getMatchPeriodId());
        if(wholeSores==null||oldSores==null){
            log.error("canleEvent 当前取消事件还未被统计:wholeSores==null||oldSores==null EventCode:"+data.getEventCode()+"事件ID:"+data.getThirdEventId());
            throw new Exception("canleEvent 当前取消事件还未被统计:wholeSores==null||oldSores==null |EventCode："+data.getEventCode()+",oldSores="+oldSores);
        }
        if(data.getMatchPeriodId() == null){
            return;
        }
//        //1.先取消全局
//        CommonItem commonItem= wholeSores.getEventScores(data.getEventCode());
//        CommonItem oldItem= oldSores.getEventScores(data.getEventCode());
//        if(commonItem==null||oldItem==null){
//            log.error("canleEvent 当前取消事件还未被统计:commonItem==null||oldItem==null EventCode:"+data.getEventCode()+"事件ID:"+data.getThirdEventId());
//            throw new Exception("当前取消事件还未被统计:commonItem==null||oldItem==null |EventCode："+data.getEventCode());
//        }
        log.info("{},删除事件前阶段比分：{}",data.getLinkId(),oldSores);
        log.info("{},删除事件前全局比分：{}",data.getLinkId(),wholeSores);
        boolean isAddScores =isAddScores(data);
        if(isAddScores){
            wholeSores.deleteEventScores(data.getEventCode(),data.getHomeAway());
            oldSores.deleteEventScores(data.getEventCode(),data.getHomeAway());
        }else {
            wholeSores.setFieldByCancelEventCode( data.getThirdMatchId(),data.getEventCode(), data.getT1(), data.getT2());
            //计算阶段值 下半场=全场-上半场 等
            wholeSores.doCalculation( data.getThirdMatchId(),data, allPeriodScores,isReissue);
        }

        if(EventCodeEnum.GOAL.code.equals(data.getEventCode())){
            String key = "GOAL_" + data.getThirdMatchId()+"_"+data.getDataSourceCode();
//            String penaltyKey = "PENALTY_EVENT_" + data.getThirdMatchId()+"_"+data.getDataSourceCode();
//            if(redisService.get(penaltyKey)!=null){
                //存在点球进球
                if(redisService.get(key)!=null){
                    //扣回点球进球比分
                    if(TeamTypeEnum.HOME.getCode().equals(data.getHomeAway())){
                        wholeSores.getPenaltyAwarded().setHome(wholeSores.getPenaltyAwarded().getHome()-1);
                    }else if(TeamTypeEnum.AWAY.getCode().equals(data.getHomeAway())){
                        wholeSores.getPenaltyAwarded().setAway(wholeSores.getPenaltyAwarded().getAway()-1);
                    }else{
                        log.info("删除点球进球：无主客队：{}",data.getLinkId());
                    }
                }
                redisService.del(key);
//                redisService.del(penaltyKey);
//            }
        }
        //入库保存
        if((data.getEventCode().equals("goal")|| data.getEventCode().equals("penalty_missed") )&&data.getMatchPeriodId().equals(50L)){
            log.info("删除点球canleEvent start，linkId::{}::",data.getLinkId());
            cancelCalulationPenaltyScores(matchScoresInfo, data,oldSores);
            allPeriodScores.put(50L,oldSores);
            log.info("删除点球canleEvent end，linkId::{}::",data.getLinkId());
        }
        wholeSores.countFaCard();
        oldSores.countFaCard(); //罚牌
        matchScoresInfo.setT1(wholeSores.getGoal().getHome());
        matchScoresInfo.setT2(wholeSores.getGoal().getAway());
        matchScoresInfo.setPeriodT1(oldSores.getGoal().getHome());
        matchScoresInfo.setPeriodT2(oldSores.getGoal().getAway());
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    /**
     * 删除事件金秋比分
     * @param matchScoresInfo
     * @param data
     */
    private void cancelCalulationPenaltyScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data,FootballScores oldSores) {
        try{
            String extraScores =matchScoresInfo.getScoresJsonExtra();
            FootballPenaltyScores footballPenaltyScores=null;
            if(StringUtils.isEmpty(extraScores)){
               return;
            }else {
                footballPenaltyScores= JSONObject.toJavaObject((JSONObject.parseObject(extraScores)) , FootballPenaltyScores.class);
            }
            footballPenaltyScores.cancelCalutionPenaltyScores(data);
            matchScoresInfo.setScoresJsonExtra(JSONObject.toJSONString(footballPenaltyScores));
            if(data.getMatchPeriodId()==50L){
                oldSores.setGoal(new CommonItem(data.getT1(),data.getT2()));
            }
        }catch (Exception e){

            log.error(":处理数据发生异常:", e);
        }
    }

    /**
     * 保存赛事比分统计
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
     * 保存赛事比分统计
     * @param matchScoresInfo
     * @param data
     */
    private void saveMatchStatistics(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data) {
        //保存比分
        if(data.getMatchStatisticsInfoDetailList()==null || data.getPeriod()==null){
            log.error("createMatchStatistics data:null");
            return;
        }
        //1.得到阶段map 转化的X
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        FootballScores wholeScore=allPeriodScores.get(SportPeriodConstant.SportPeriod.WHOLE_MATCH);

        FootballScores period=allPeriodScores.get(data.getPeriod().longValue());
        // 6 7 41 42
        if(data.getPeriod().equals(6)||data.getPeriod().equals(7)||data.getPeriod().equals(41)||data.getPeriod().equals(42)){
            if(period==null){
                period=new FootballScores(data.getPeriod().longValue());
                allPeriodScores.put(data.getPeriod().longValue(),period);
            }
        }
        for (MatchStatisticsInfoDetailDTO matchStatisticsInfoDetailDTO : data.getMatchStatisticsInfoDetailList()) {
            if(matchStatisticsInfoDetailDTO.getCode().equals("set_score")){
                if( 1 == matchStatisticsInfoDetailDTO.getFirstNum() &&
                        null != matchStatisticsInfoDetailDTO.getT1() && null != matchStatisticsInfoDetailDTO.getT2() ){
                    FootballScores secondScores=allPeriodScores.get(6L);
                    if(allPeriodScores.get(6L)==null){
                        secondScores=new FootballScores(6L);
                        allPeriodScores.put(6L,secondScores);
                    }
                    secondScores.getGoal().setHome(matchStatisticsInfoDetailDTO.getT1());
                    secondScores.getGoal().setAway(matchStatisticsInfoDetailDTO.getT2());
                }
                if( 2 == matchStatisticsInfoDetailDTO.getFirstNum() &&
                        null != matchStatisticsInfoDetailDTO.getT1() && null != matchStatisticsInfoDetailDTO.getT2() ){
                    FootballScores secondScores=allPeriodScores.get(7L);
                    if(allPeriodScores.get(7L)==null){
                        secondScores=new FootballScores(7L);
                        allPeriodScores.put(7L,secondScores);
                    }
                    secondScores.getGoal().setHome(matchStatisticsInfoDetailDTO.getT1());
                    secondScores.getGoal().setAway(matchStatisticsInfoDetailDTO.getT2());
                }
            }
            if(matchStatisticsInfoDetailDTO.getCode().equals("match_score")){
                wholeScore.getGoal().setHome(matchStatisticsInfoDetailDTO.getT1());
                wholeScore.getGoal().setAway(matchStatisticsInfoDetailDTO.getT2());
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
            //保存比分数据记录
        }
        if(period!=null){
            period.countFaCard();
        }
        wholeScore.countFaCard();
        matchScoresInfo.setT1(wholeScore.getGoal().getHome());
        matchScoresInfo.setT2(wholeScore.getGoal().getAway());
//        matchScoresInfo.setPeriodT1(period.getGoal().getHome());
//        matchScoresInfo.setPeriodT2(period.getGoal().getAway());
        //2.变更入库
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);

    }
    /**
     * 保存赛事比分统计
     * @param matchScoresInfo
     * @param data
     */
    private void createMatchStatistics(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data) {
        FootballScores wholeScore=new FootballScores(WHOLE_MATCH);
        Map<Long, FootballScores> footballScoresHashMap= new HashMap<>();
        footballScoresHashMap.put(WHOLE_MATCH,wholeScore);

        //保存比分
        if(data.getMatchStatisticsInfoDetailList()==null){
            log.error("createMatchStatistics data:null");
            return;
        }
        for (MatchStatisticsInfoDetailDTO matchStatisticsInfoDetailDTO : data.getMatchStatisticsInfoDetailList()) {
            if(matchStatisticsInfoDetailDTO.getCode().equals("set_score")){
                if( 1 == matchStatisticsInfoDetailDTO.getFirstNum() &&
                        null != matchStatisticsInfoDetailDTO.getT1() && null != matchStatisticsInfoDetailDTO.getT2() ){
                    FootballScores secondScores=footballScoresHashMap.get(6l);
                    if(footballScoresHashMap.get(6l)==null){
                        secondScores=new FootballScores(6l);
                        footballScoresHashMap.put(6l,secondScores);
                    }
                    secondScores.getGoal().setHome(matchStatisticsInfoDetailDTO.getT1());
                    secondScores.getGoal().setAway(matchStatisticsInfoDetailDTO.getT2());
                }else if( 2 == matchStatisticsInfoDetailDTO.getFirstNum() &&
                        null != matchStatisticsInfoDetailDTO.getT1() && null != matchStatisticsInfoDetailDTO.getT2() ){
                    FootballScores secondScores=footballScoresHashMap.get(7l);
                    if(footballScoresHashMap.get(7l)==null){
                         secondScores=new FootballScores(7l);
                        footballScoresHashMap.put(7l,secondScores);
                    }
                    secondScores.getGoal().setHome(matchStatisticsInfoDetailDTO.getT1());
                    secondScores.getGoal().setAway(matchStatisticsInfoDetailDTO.getT2());
                }
                if(matchStatisticsInfoDetailDTO.getCode().equals("match_score")){
                    wholeScore.getGoal().setHome(matchStatisticsInfoDetailDTO.getT1());
                    wholeScore.getGoal().setAway(matchStatisticsInfoDetailDTO.getT2());
                }
                if(matchStatisticsInfoDetailDTO.getCode().equals("yellow_card_score")){
                    wholeScore.getYellowCard().setHome(matchStatisticsInfoDetailDTO.getT1());
                    wholeScore.getYellowCard().setAway(matchStatisticsInfoDetailDTO.getT2());
                }
                if(matchStatisticsInfoDetailDTO.getCode().equals("red_card_score")){
                    wholeScore.getRedCard().setHome(matchStatisticsInfoDetailDTO.getT1());
                    wholeScore.getRedCard().setAway(matchStatisticsInfoDetailDTO.getT2());
                }
                if(matchStatisticsInfoDetailDTO.getCode().equals("corner_score")){
                    wholeScore.getCorner().setHome(matchStatisticsInfoDetailDTO.getT1());
                    wholeScore.getCorner().setAway(matchStatisticsInfoDetailDTO.getT2());
                }
            }
            //保存比分数据记录
        }
        //更新赛事比分表
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(footballScoresHashMap));
        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    /**
     * 保存15分钟玩法比分
     * @param matchId
     */
    public void save15MinToCacheByStandardId(Long matchId) {
        MatchScoresInfo matchScoresInfo =scoresService.searchMatchScoreByStandardId(matchId);
        if(matchScoresInfo==null){
            return;
        }
        log.info("save15MinToCacheByStandardId 当数据源切换的时候切换存储15分钟比分标准赛事ID:{}",matchId);
        save15MinScores(matchScoresInfo,matchScoresInfo.getThirdMatchId());
    }

    /**
     * 足球取消进球事件下发逻辑
     * @param matchScoresInfo
     * @param data
     */
    @Override
    public void canceledGoal(MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        String key = "GOAL_" + data.getThirdMatchId()+"_"+data.getDataSourceCode();
        String penaltyKey = "PENALTY_EVENT_" + data.getThirdMatchId()+"_"+data.getDataSourceCode();
        if(redisService.get(penaltyKey)!=null){
            //存在点球事件
            if(redisService.get(key)!=null){
               try{
                   //并且点球进球了
                   //扣回点球进球比分
                   //@TODO 不确定进球取消事件报文，暂此处理
                   JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
                   Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
                   FootballScores score = allPeriodScores.get(data.getMatchPeriodId());
                   if(score!=null){
                       score.getPenaltyAwarded().setHome(data.getT1());
                       score.getPenaltyAwarded().setAway(data.getT2());
                       allPeriodScores.put(data.getMatchPeriodId(),score);
                       matchScoresInfo.setModifyTime(System.currentTimeMillis());
                       matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
//                       matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
                   }
              }catch (Exception e){
                   log.error("canceledGoal error:",e);
              }finally {
                   redisService.del(penaltyKey);
               }
            }
        }
    }

    @Override
    public void calcStandardMatchScores(MatchScoresInfo matchScoresInfo, StandardMatchScores score, MatchEventInfo data)
            throws Exception{
        log.info("calcStandardMatchScores linkId={},计算标准比分",data.getLinkId());
        String scoresJson = matchScoresInfo.getScoresJson();
        //阶段转换
        changePeriodByExtryPeriodEvent(data);
        Long periodId = data.getMatchPeriodId();
        if(!SportPeriodConstant.FootballPeriod.contans(periodId)){
            log.info(",linkId={},计算标准比分 足球 阶段={}无需处理！",data.getLinkId(),periodId);
            return;
        }
        try{
            Map<Long, FootballScores> allPeriodScores = JSON.parseObject(scoresJson, new TypeReference<Map<Long, FootballScores>>() {
            });

            FootballScores thirdWholeScores= allPeriodScores.get(WHOLE_MATCH);
            if(thirdWholeScores==null){
                thirdWholeScores = new FootballScores(WHOLE_MATCH);
            }
            CommonItem penaltyAwarded = thirdWholeScores.getPenaltyAwarded();
            //清除需要汇总计算的比分，保留其他比分如进攻危险进攻等比分
            initWholeScores(thirdWholeScores);
            Map<Long, FootballScores> standardScores = new HashMap<>();
            //标准比分为空，直接复制三方比分
            if (!StringUtils.isEmpty(score.getScoreJson())) {
                standardScores = JSON.parseObject(score.getScoreJson(), new TypeReference<Map<Long, FootballScores>>() {
                });
            }else{
                standardScores = JSON.parseObject(scoresJson, new TypeReference<Map<Long, FootballScores>>() {
                });
            }
            String sourceSwitchJson = score.getDataSourceAccoSwitch();
            FootballSwitch footballSwitch = new FootballSwitch();
            if (StringUtils.isNotEmpty(sourceSwitchJson)) {
                footballSwitch = JSONObject.parseObject(sourceSwitchJson, FootballSwitch.class);
            }
            setPeriodScores(data,standardScores, allPeriodScores, footballSwitch);

            //标准比分里面的-1比分
            FootballScores wholeStands = standardScores.get(WHOLE_MATCH);
            for(Map.Entry<Long, FootballScores> entry : standardScores.entrySet()){
                if(entry.getKey()==6 || entry.getKey()==7 || entry.getKey()==110 ){
                    //累计-1比分
                    calcWholeScore(thirdWholeScores,standardScores.get(entry.getKey()));
                }
                //计算每个阶段的罚牌比分
                entry.getValue().countFaCard();
            }
            log.info("::{}::计算点球比分,获取开关：{},{}",data.getLinkId(),data.getStandardMatchId(),footballSwitch.getPenaltyAwarded());
            if(footballSwitch.getPenaltyAwarded()==1){
                thirdWholeScores.setPenaltyAwarded(penaltyAwarded);
                log.info("::{}::计算点球比分：{},三方点球比分：{}",data.getLinkId(),data.getStandardMatchId(),thirdWholeScores.getPenaltyAwarded());
            }else{
                log.info("::{}::计算点球比分：{},标准点球比分：{}",data.getLinkId(),data.getStandardMatchId(),thirdWholeScores.getPenaltyAwarded());
                thirdWholeScores.setPenaltyAwarded(wholeStands.getPenaltyAwarded());
            }
            log.info("::{}::计算点球比分：{},最终比分：{}",data.getLinkId(),data.getStandardMatchId(),thirdWholeScores);
            standardScores.put(WHOLE_MATCH,thirdWholeScores);
            //保存
            scoresJson = JSONUtil.toJsonStr(standardScores);
            score.setScoreJson(scoresJson);

        }catch (Exception e){
            log.error("计算标准比分错误:{}",data.getLinkId(),e);
        }

    }

    /**
     * 三方的进攻危险进攻射正射偏控球率数据同步
     * @param standScores
     * @param soresSource
     */
    public void setOther(FootballScores standScores,FootballScores soresSource){
        standScores.setAttack(soresSource.getAttack());
        standScores.setDangerousAttack(soresSource.getDangerousAttack());
        standScores.setBallPossessionPercentage(soresSource.getBallPossessionPercentage());
        standScores.setShotOn(soresSource.getShotOn());
        standScores.setShotOff(soresSource.getShotOff());
        standScores.setShot(soresSource.getShot());

    }
    private void setPeriodScores(MatchEventInfo data,
                                 Map<Long, FootballScores> standardScores,
                                 Map<Long, FootballScores> allPeriodScores,
                                 FootballSwitch footballSwitch) {
        Long periodId = data.getMatchPeriodId();
        FootballScores soresSource= allPeriodScores.get(periodId);
        if(soresSource==null) {
            log.info("::{}::复制足球阶段比分,三方阶段比分为空 {}",data.getLinkId(),periodId);
            return;
        }
        //同步5分钟比分
        copyMinuteScores(periodId,standardScores,allPeriodScores,footballSwitch);
        if(standardScores.get(periodId)==null){
            standardScores.put(periodId,new FootballScores(periodId));
        }

        //检索历史比分，根据开关同步历史比分
        for(Map.Entry<Long, FootballScores> entry : allPeriodScores.entrySet()){

            //获取标准比分当前阶段的比分
            FootballScores standScores = standardScores.get(entry.getKey());
            if(standScores==null){
                standScores = new FootballScores(entry.getKey());
            }
            if(entry.getKey()==6L){
                //获取三方比分当前阶段的比分
                FootballScores thirdScores = entry.getValue();
                if(thirdScores==null){
                    thirdScores = new FootballScores(entry.getKey());
                }
                FootballScores footballMinScores1 = standardScores.get(60899L);
                FootballScores footballMinScores2 = standardScores.get(61799L);
                FootballScores footballMinScores3 = standardScores.get(62699L);
                if(footballMinScores1==null){
                    footballMinScores1 = new FootballScores(60899L);
                    standardScores.put(60899L,footballMinScores1);
                }
                if(footballMinScores2==null){
                    footballMinScores2 = new FootballScores(61799L);
                    standardScores.put(61799L,footballMinScores2);
                }
                if(footballMinScores3==null){
                    footballMinScores3 = new FootballScores(62699L);
                    standardScores.put(62699L,footballMinScores3);
                }
                //上半场进球
                if(footballSwitch.getGoalHf()==1){
                    standScores.setGoal(thirdScores.getGoal());
                }
                //0-15分钟进球
                if(footballSwitch.getGoal60899()==1){
                    if(allPeriodScores.get(60899L)!=null){
                        footballMinScores1.setGoal(allPeriodScores.get(60899L).getGoal());
                    }
                }
                //15-30分钟进球
                if(footballSwitch.getGoal61799()==1){
                    if(allPeriodScores.get(61799L)!=null){
                        footballMinScores2.setGoal(allPeriodScores.get(61799L).getGoal());
                    }
                }
                //30-45分钟进球
                if(footballSwitch.getGoal62699()==1){
                    if(allPeriodScores.get(62699L)!=null){
                        footballMinScores3.setGoal(allPeriodScores.get(62699L).getGoal());
                    }
                }
                //上半场角球
                if(footballSwitch.getCornerHf()==1){
                    standScores.setCorner(thirdScores.getCorner());
                }
                //0-15分钟角球
                if(footballSwitch.getCorner60899()==1){
                    if(allPeriodScores.get(60899L)!=null){
                        footballMinScores1.setCorner(allPeriodScores.get(60899L).getCorner());
                    }
                }
                //15-30分钟角球
                if(footballSwitch.getCorner61799()==1){
                    if(allPeriodScores.get(61799L)!=null){
                        footballMinScores2.setCorner(allPeriodScores.get(61799L).getCorner());
                    }
                }
                //30-45分钟角球
                if(footballSwitch.getCorner62699()==1){
                    if(allPeriodScores.get(62699L)!=null){
                        footballMinScores3.setCorner(allPeriodScores.get(62699L).getCorner());
                    }
                }
                //上半场黄牌
                if(footballSwitch.getYellowHf()==1){
                    standScores.setYellowCard(thirdScores.getYellowCard());
                }
                //0-15分钟黄牌
                if(footballSwitch.getYellowCard60899()==1){
                    if(allPeriodScores.get(60899L)!=null){
                        footballMinScores1.setYellowCard(allPeriodScores.get(60899L).getYellowCard());
                    }
                }
                //15-30分钟黄牌
                if(footballSwitch.getYellowCard61799()==1){
                    if(allPeriodScores.get(61799L)!=null){
                        footballMinScores2.setYellowCard(allPeriodScores.get(61799L).getYellowCard());
                    }
                }
                //30-45分钟黄牌
                if(footballSwitch.getYellowCard62699()==1){
                    if(allPeriodScores.get(62699L)!=null){
                        footballMinScores3.setYellowCard(allPeriodScores.get(62699L).getYellowCard());
                    }
                }
                //上半场红牌
                if(footballSwitch.getRedHf()==1){
                    standScores.setRedCard(thirdScores.getRedCard());
                }
                //0-15分钟红牌
                if(footballSwitch.getYellowCard60899()==1){
                    if(allPeriodScores.get(60899L)!=null){
                        footballMinScores1.setRedCard(allPeriodScores.get(60899L).getRedCard());
                    }
                }
                //15-30分钟红牌
                if(footballSwitch.getYellowCard61799()==1){
                    if(allPeriodScores.get(61799L)!=null){
                        footballMinScores2.setRedCard(allPeriodScores.get(61799L).getRedCard());
                    }
                }
                //30-45分钟红牌
                if(footballSwitch.getYellowCard62699()==1){
                    if(allPeriodScores.get(62699L)!=null){
                        footballMinScores3.setRedCard(allPeriodScores.get(62699L).getRedCard());
                    }
                }
                //三方的进攻危险进攻射正射偏控球率数据同步
                this.setOther(standScores,thirdScores);
                standardScores.put(6L,standScores);
                standardScores.put(60899L,footballMinScores1);
                standardScores.put(61799L,footballMinScores2);
                standardScores.put(62699L,footballMinScores3);
            }

            if(entry.getKey()==7L){
                //获取三方比分当前阶段的比分
                FootballScores thirdScores = entry.getValue();
                if(thirdScores==null){
                    thirdScores = new FootballScores(entry.getKey());
                }
                FootballScores footballMinScores1 = standardScores.get(73599L);
                FootballScores footballMinScores2 = standardScores.get(74499L);
                FootballScores footballMinScores3 = standardScores.get(75399L);
                if(footballMinScores1==null){
                    footballMinScores1 = new FootballScores(73599L);
                    standardScores.put(73599L,footballMinScores1);
                }
                if(footballMinScores2==null){
                    footballMinScores2 = new FootballScores(74499L);
                    standardScores.put(74499L,footballMinScores2);
                }
                if(footballMinScores3==null){
                    footballMinScores3 = new FootballScores(75399L);
                    standardScores.put(75399L,footballMinScores3);
                }
                if(footballSwitch.getGoalFt()==1){
                    standScores.setGoal(thirdScores.getGoal());
                }
                //45-60钟进球
                if(footballSwitch.getGoal73599()==1){
                    if(allPeriodScores.get(73599L)!=null){
                        footballMinScores1.setGoal(allPeriodScores.get(73599L).getGoal());
                    }
                }
                //60-75分钟进球
                if(footballSwitch.getGoal74499()==1){
                    if(allPeriodScores.get(74499L)!=null){
                        footballMinScores2.setGoal(allPeriodScores.get(74499L).getGoal());
                    }
                }
                //75-90分钟进球
                if(footballSwitch.getGoal75399()==1){
                    if(allPeriodScores.get(75399L)!=null){
                        footballMinScores3.setGoal(allPeriodScores.get(75399L).getGoal());
                    }
                }
                //下半场角球
                if(footballSwitch.getCornerFt()==1){
                    standScores.setCorner(thirdScores.getCorner());
                }
                //45-60钟角球
                if(footballSwitch.getCorner73599()==1){
                    if(allPeriodScores.get(73599L)!=null){
                        footballMinScores1.setCorner(allPeriodScores.get(73599L).getCorner());
                    }
                }
                //60-75分钟角球
                if(footballSwitch.getCorner74499()==1){
                    if(allPeriodScores.get(74499L)!=null){
                        footballMinScores2.setCorner(allPeriodScores.get(74499L).getCorner());
                    }
                }
                //75-90分钟角球
                if(footballSwitch.getCorner75399()==1){
                    if(allPeriodScores.get(75399L)!=null){
                        footballMinScores3.setCorner(allPeriodScores.get(75399L).getCorner());
                    }
                }
                if(footballSwitch.getYellowFt()==1){
                    standScores.setYellowCard(thirdScores.getYellowCard());
                }
                //45-60钟黄牌
                if(footballSwitch.getYellowCard73599()==1){
                    if(allPeriodScores.get(73599L)!=null){
                        footballMinScores1.setYellowCard(allPeriodScores.get(73599L).getYellowCard());
                    }
                }
                //60-75分钟黄牌
                if(footballSwitch.getYellowCard74499()==1){
                    if(allPeriodScores.get(74499L)!=null){
                        footballMinScores2.setYellowCard(allPeriodScores.get(74499L).getYellowCard());
                    }
                }
                //75-90分钟黄牌
                if(footballSwitch.getYellowCard75399()==1){
                    if(allPeriodScores.get(75399L)!=null){
                        footballMinScores3.setYellowCard(allPeriodScores.get(75399L).getYellowCard());
                    }
                }
                //下半场红牌
                if(footballSwitch.getRedFt()==1){
                    standScores.setRedCard(thirdScores.getRedCard());
                }
                //45-60钟红牌
                if(footballSwitch.getRedCard73599()==1){
                    if(allPeriodScores.get(73599L)!=null){
                        footballMinScores1.setRedCard(allPeriodScores.get(73599L).getRedCard());
                    }
                }
                //60-75分钟红牌
                if(footballSwitch.getRedCard74499()==1){
                    if(allPeriodScores.get(74499L)!=null){
                        footballMinScores2.setRedCard(allPeriodScores.get(74499L).getRedCard());
                    }
                }
                //75-90分钟红牌
                if(footballSwitch.getRedCard75399()==1){
                    if(allPeriodScores.get(75399L)!=null){
                        footballMinScores3.setRedCard(allPeriodScores.get(75399L).getRedCard());
                    }
                }
                //三方的进攻危险进攻射正射偏控球率数据同步
                this.setOther(standScores,thirdScores);
                standardScores.put(7L,standScores);
                standardScores.put(73599L,footballMinScores1);
                standardScores.put(74499L,footballMinScores2);
                standardScores.put(75399L,footballMinScores3);
            }
            if(entry.getKey()==41L) {
                //获取三方比分当前阶段的比分
                FootballScores thirdScores = entry.getValue();
                if(thirdScores==null){
                    thirdScores = new FootballScores(entry.getKey());
                }
                if(footballSwitch.getGoalOt()==1){
                    standScores.setGoal(thirdScores.getGoal());
                }
                if(footballSwitch.getCornerOt()==1){
                    standScores.setCorner(thirdScores.getCorner());
                }
                if(footballSwitch.getYellowOt()==1){
                    standScores.setYellowCard(thirdScores.getYellowCard());
                }
                if(footballSwitch.getRedOt()==1){
                    standScores.setRedCard(thirdScores.getRedCard());
                }
                //三方的进攻危险进攻射正射偏控球率数据同步
                this.setOther(standScores,thirdScores);
                standardScores.put(41L,standScores);
            }
            if(entry.getKey()==42L) {
                //获取三方比分当前阶段的比分
                FootballScores thirdScores = entry.getValue();
                if(thirdScores==null){
                    thirdScores = new FootballScores(entry.getKey());
                }
                if(footballSwitch.getGoalOt()==1){
                    standScores.setGoal(thirdScores.getGoal());
                }
                if(footballSwitch.getCornerOt()==1){
                    standScores.setCorner(thirdScores.getCorner());
                }
                if(footballSwitch.getYellowOt()==1){
                    standScores.setYellowCard(thirdScores.getYellowCard());
                }
                if(footballSwitch.getRedOt()==1){
                    standScores.setRedCard(thirdScores.getRedCard());
                }
                //三方的进攻危险进攻射正射偏控球率数据同步
                this.setOther(standScores,thirdScores);
                standardScores.put(42L,standScores);
            }
            if(entry.getKey()==50L){
                //获取三方比分当前阶段的比分
                FootballScores thirdScores = entry.getValue();
                if(thirdScores==null){
                    thirdScores = new FootballScores(entry.getKey());
                }
                log.info("{}，同步点球大战比分1：{}",footballSwitch.getPenalty(),thirdScores);
                if(footballSwitch.getPenalty()==1){
                    standScores.setGoal(thirdScores.getGoal());
                }
                log.info("{}，同步点球大战比分2：{}",footballSwitch.getPenalty(),standScores);
                standardScores.put(50L,standScores);
            }
        }
        int rate = 0;
        int otrate = 0;
        //汇总加时赛全场比分
        if(periodId==41L || periodId==42L){
            Integer otHomeGoal =0, otAwayGoal = 0;
            Integer otHomeCorner = 0, otAwayCorner = 0;
            Integer otHomeYellowCard = 0, otAwayYellowCard = 0;
            Integer otHomeRedCard = 0, otAwayRedCard = 0;
            Integer otHomeAttack = 0, otAwayAttack = 0;
            Integer otHomeDangerousAttack = 0, otAwayDangerousAttack = 0;
            Integer otHomePossession = 0, otAwayPossession = 0;
            Integer otShotOnHome = 0, otShotOnAway = 0;
            Integer otShotOffHome = 0, otShotOffAway = 0;
            Integer otShotHome = 0, otShotAway = 0;

            FootballScores standScoresOts = standardScores.get(110L);
            if(standScoresOts==null){
                standScoresOts = new FootballScores(110L);
                standardScores.put(110L,standScoresOts);
            }
            FootballScores ot1= allPeriodScores.get(41L);
            if(ot1!=null){
                otHomeGoal += ot1.getGoal().getHome();
                otAwayGoal += ot1.getGoal().getAway();
                otHomeCorner += ot1.getCorner().getHome();
                otAwayCorner += ot1.getCorner().getAway();
                otHomeYellowCard += ot1.getYellowCard().getHome();
                otAwayYellowCard += ot1.getYellowCard().getAway();
                otHomeRedCard += ot1.getRedCard().getHome();
                otAwayRedCard += ot1.getRedCard().getAway();
                otHomeAttack += ot1.getAttack().getHome();
                otAwayAttack += ot1.getAttack().getAway();
                otHomeDangerousAttack += ot1.getDangerousAttack().getHome();
                otAwayDangerousAttack += ot1.getDangerousAttack().getAway();
                otHomePossession += ot1.getBallPossessionPercentage().getHome();
                otAwayPossession += ot1.getBallPossessionPercentage().getAway();
                otrate += 1;
                otShotOnHome += ot1.getShotOn().getHome();
                otShotOnAway += ot1.getShotOn().getAway();
                otShotOffHome += ot1.getShotOff().getHome();
                otShotOffAway += ot1.getShotOff().getAway();
                otShotHome += ot1.getShot().getHome();
                otShotAway += ot1.getShot().getAway();
            }
            FootballScores ot2= allPeriodScores.get(42L);
            if(ot2!=null){
                otHomeGoal += ot2.getGoal().getHome();
                otAwayGoal += ot2.getGoal().getAway();
                otHomeCorner += ot2.getCorner().getHome();
                otAwayCorner += ot2.getCorner().getAway();
                otHomeYellowCard += ot2.getYellowCard().getHome();
                otAwayYellowCard += ot2.getYellowCard().getAway();
                otHomeRedCard += ot2.getRedCard().getHome();
                otAwayRedCard += ot2.getRedCard().getAway();
                otHomeAttack += ot2.getAttack().getHome();
                otAwayAttack += ot2.getAttack().getAway();
                otHomeDangerousAttack += ot2.getDangerousAttack().getHome();
                otAwayDangerousAttack += ot2.getDangerousAttack().getAway();
                otHomePossession += ot2.getBallPossessionPercentage().getHome();
                otAwayPossession += ot2.getBallPossessionPercentage().getAway();
                otrate += 1;
                otShotOnHome += ot2.getShotOn().getHome();
                otShotOnAway += ot2.getShotOn().getAway();
                otShotOffHome += ot2.getShotOff().getHome();
                otShotOffAway += ot2.getShotOff().getAway();
                otShotHome += ot2.getShot().getHome();
                otShotAway += ot2.getShot().getAway();

            }
            if(footballSwitch.getGoalOt()==1){
                standScoresOts.setGoal(new CommonItem(otHomeGoal,otAwayGoal));
            }
            if(footballSwitch.getCornerOt()==1){
                standScoresOts.setCorner(new CommonItem(otHomeCorner,otAwayCorner));
            }
            if(footballSwitch.getYellowOt()==1){
                standScoresOts.setYellowCard(new CommonItem(otHomeYellowCard,otAwayYellowCard));
            }
            if(footballSwitch.getRedOt()==1){
                standScoresOts.setRedCard(new CommonItem(otHomeRedCard,otAwayRedCard));
            }
            standScoresOts.countFaCard();
            standScoresOts.setAttack(new CommonItem(otHomeAttack,otAwayAttack));
            standScoresOts.setDangerousAttack(new CommonItem(otHomeDangerousAttack,otAwayDangerousAttack));
            if(otrate!=0){
                standScoresOts.setBallPossessionPercentage(new CommonItem(otHomePossession/otrate,otAwayPossession/otrate));
            }else{
                standScoresOts.setBallPossessionPercentage(new CommonItem(otHomePossession,otAwayPossession));
            }
            standScoresOts.setShotOn(new CommonItem(otShotOnHome,otShotOnAway));
            standScoresOts.setShotOff(new CommonItem(otShotOffHome,otShotOffAway));
            standScoresOts.setShot(new CommonItem(otShotHome,otShotAway));
            //阶段41|| 42
            standardScores.put(110L,standScoresOts);
        }
        //拼阶段100的比分-常规赛不含加时
        Integer homeGoal =0, awayGoal = 0;
        Integer homeCorner = 0, awayCorner = 0;
        Integer homeYellowCard = 0, awayYellowCard = 0;
        Integer homeRedCard = 0, awayRedCard = 0;
        Integer homeAttack = 0, awayAttack = 0;
        Integer homeDangerousAttack = 0, awayDangerousAttack = 0;
        Integer homePossession = 0, awayPossession = 0;
        Integer shotOnHome = 0, shotOnAway = 0;
        Integer shotOffHome = 0, shotOffAway = 0;
        Integer shotHome = 0, shotAway = 0;
        FootballScores standScoresEnd = standardScores.get(100L);
        if(standScoresEnd==null){
            standScoresEnd = new FootballScores(100L);
            standardScores.put(100L,standScoresEnd);
        }
        FootballScores hfScore= allPeriodScores.get(6L);
        if(hfScore!=null){
            homeGoal += hfScore.getGoal().getHome();
            awayGoal += hfScore.getGoal().getAway();
            homeCorner += hfScore.getCorner().getHome();
            awayCorner += hfScore.getCorner().getAway();
            homeYellowCard += hfScore.getYellowCard().getHome();
            awayYellowCard += hfScore.getYellowCard().getAway();
            homeRedCard += hfScore.getRedCard().getHome();
            awayRedCard += hfScore.getRedCard().getAway();
            homeAttack += hfScore.getAttack().getHome();
            awayAttack += hfScore.getAttack().getAway();
            homeDangerousAttack += hfScore.getDangerousAttack().getHome();
            awayDangerousAttack += hfScore.getDangerousAttack().getAway();
            homePossession += hfScore.getBallPossessionPercentage().getHome();
            awayPossession += hfScore.getBallPossessionPercentage().getAway();
            rate += 1;
            shotOnHome += hfScore.getShotOn().getHome();
            shotOnAway += hfScore.getShotOn().getAway();
            shotOffHome += hfScore.getShotOff().getHome();
            shotOffAway += hfScore.getShotOff().getAway();
            shotHome += hfScore.getShot().getHome();
            shotAway += hfScore.getShot().getAway();
        }
        FootballScores ftScore= allPeriodScores.get(7L);
        if(ftScore!=null){
            homeGoal += ftScore.getGoal().getHome();
            awayGoal += ftScore.getGoal().getAway();
            homeCorner += ftScore.getCorner().getHome();
            awayCorner += ftScore.getCorner().getAway();
            homeYellowCard += ftScore.getYellowCard().getHome();
            awayYellowCard += ftScore.getYellowCard().getAway();
            homeRedCard += ftScore.getRedCard().getHome();
            awayRedCard += ftScore.getRedCard().getAway();
            homeAttack += ftScore.getAttack().getHome();
            awayAttack += ftScore.getAttack().getAway();
            homeDangerousAttack += ftScore.getDangerousAttack().getHome();
            awayDangerousAttack += ftScore.getDangerousAttack().getAway();
            homePossession += ftScore.getBallPossessionPercentage().getHome();
            awayPossession += ftScore.getBallPossessionPercentage().getAway();
            rate += 1;
            shotOnHome += ftScore.getShotOn().getHome();
            shotOnAway += ftScore.getShotOn().getAway();
            shotOffHome += ftScore.getShotOff().getHome();
            shotOffAway += ftScore.getShotOff().getAway();
            shotHome += ftScore.getShot().getHome();
            shotAway += ftScore.getShot().getAway();
        }
        standScoresEnd.setGoal(new CommonItem(homeGoal,awayGoal));
        standScoresEnd.setCorner(new CommonItem(homeCorner,awayCorner));
        standScoresEnd.setYellowCard(new CommonItem(homeYellowCard,awayYellowCard));
        standScoresEnd.setRedCard(new CommonItem(homeRedCard,awayRedCard));
        standScoresEnd.setAttack(new CommonItem(homeAttack,awayAttack));
        standScoresEnd.setDangerousAttack(new CommonItem(homeDangerousAttack,awayDangerousAttack));
        if(rate!=0){
            standScoresEnd.setBallPossessionPercentage(new CommonItem(homePossession/rate,awayPossession/rate));
        }else{
            standScoresEnd.setBallPossessionPercentage(new CommonItem(homePossession,awayPossession));
        }
        standScoresEnd.setShotOn(new CommonItem(shotOnHome,shotOnAway));
        standScoresEnd.setShotOff(new CommonItem(shotOffHome,shotOffAway));
        standScoresEnd.setShot(new CommonItem(shotHome,shotAway));
        standardScores.put(100L,standScoresEnd);
    }

    /**
     * 同步事件源的区间比分
     * @param periodId
     * @param standardScores
     * @param allPeriodScores
     * @param footballSwitch
     */
    private void copyMinuteScores(Long periodId, Map<Long, FootballScores> standardScores, Map<Long, FootballScores> allPeriodScores, FootballSwitch footballSwitch) {
        if(allPeriodScores!=null){
            for (Long period : allPeriodScores.keySet()) {
                if(period>999L && period<60899L){
                    standardScores.put(period,allPeriodScores.get(period));
                }
            }
        }
    }


    private static void calcWholeScore(FootballScores wholeSores,FootballScores standScores) {
        wholeSores.setGoal(new CommonItem(wholeSores.getGoal().getHome()+standScores.getGoal().getHome(),
                wholeSores.getGoal().getAway()+standScores.getGoal().getAway()));
        wholeSores.setCorner(new CommonItem(wholeSores.getCorner().getHome()+standScores.getCorner().getHome(),
                wholeSores.getCorner().getAway()+standScores.getCorner().getAway()));
        wholeSores.setYellowCard(new CommonItem(wholeSores.getYellowCard().getHome()+standScores.getYellowCard().getHome(),
                wholeSores.getYellowCard().getAway()+standScores.getYellowCard().getAway()));
        wholeSores.setRedCard(new CommonItem(wholeSores.getRedCard().getHome()+standScores.getRedCard().getHome(),
                wholeSores.getRedCard().getAway()+standScores.getRedCard().getAway()));
//        wholeSores.setDangerousAttack(new CommonItem(wholeSores.getDangerousAttack().getHome()+standScores.getDangerousAttack().getHome(),
//                wholeSores.getDangerousAttack().getAway()+standScores.getDangerousAttack().getAway()));
//        wholeSores.setAttack(new CommonItem(wholeSores.getAttack().getHome()+standScores.getAttack().getHome(),
//                wholeSores.getAttack().getAway()+standScores.getAttack().getAway()));

        wholeSores.countFaCard();
    }

    public StandardScoreCenterDTO queryMatchScores(Long standardMatchId) {
        StandardScoreCenterDTO dto = new StandardScoreCenterDTO();
        StandardSportMarketSell match = standardSportMarketSellRepository.selectThirdMatchInfoPrimaryKey(standardMatchId);
        if (match==null) {
            log.info("查询标准比分:开售信息不存在:{}",standardMatchId);
            return null;
        }
        StandardMatchInfo matchInfo = standardMatchInfoRepository.selectStandardMatchPrimaryKey(standardMatchId);
        dto.setSportId(match.getSportId());
        dto.setStandardMatchId(standardMatchId);
        dto.setMatchManageId(match.getMatchManageId());
        dto.setBusinessEvent(match.getBusinessEvent());
        dto.setRelatedDataSourceCoderList(matchInfo.getRelatedDataSourceCoderList());
        dto.setPreId(match.getId());
        //查询标准比分
        StandardMatchScores standardMatchScores = scoresRedisHelp.getCatchStandScoreByMatchId(standardMatchId);

        if (standardMatchScores == null) {
            log.info("linkId::{}::saveStandardScores 标准比分数据为空！", standardMatchId);
            return null;
        }
        dto.setShowStatus(standardMatchScores.getShowStatus());
        dto.setSendSettleCount(standardMatchScores.getSendSettleCount()==null?0:standardMatchScores.getSendSettleCount());
        //获取标准比分
        StandardScoreCenter centerStand = new StandardScoreCenter();
        centerStand.setDataSourceCode("STAND");
        centerStand.setIndex(0);//排序保证标准比分放最前面
        centerStand.setStandardMatchId(standardMatchId);
        centerStand.setSportId(matchInfo.getSportId());
        //组装标准比分
        this.buildFootballScore(centerStand, standardMatchScores.getScoreJson(), standardMatchScores.getDataSourceAccoSwitch(),matchInfo.getMatchPeriodId());
        if (centerStand.getScores() == null || centerStand.getScores().isEmpty()) {
            this.scoreIsNullExtractFootball(centerStand,standardMatchScores.getDataSourceAccoSwitch());
        }
        List<StandardScoreCenter> list = new ArrayList<>();
        list.add(centerStand);


        //获取其他数据源比分
        List<ThirdMatchInfo> thirdMatchInfoList = thirdMatchInfoService.getItems(standardMatchId);
        if (thirdMatchInfoList == null || thirdMatchInfoList.isEmpty()) {
            log.info("查询标准比分，无三方赛事,直接返回：{}",standardMatchId);
            //无三方赛事，直接返回
            dto.setScores(list);
            return dto;
        }
        List<MatchScoresInfo> listScore = new ArrayList<>();
        for (ThirdMatchInfo thirdMatchInfo : thirdMatchInfoList) {
            if(N0123_SOURCE_CODE.contains(thirdMatchInfo.getDataSourceCode())){
                continue;
            }
            //其他数据源默认取实时事件比分 livedata
            MatchScoresInfo matchScoresInfo = matchScoreInfoRepository.selectByExample(thirdMatchInfo.getId(), SourceTypeEnum.LIVE_DATA.getCode());
            if (matchScoresInfo != null) {
                listScore.add(matchScoresInfo);
            }else{
                //无事件比分则查询是否存在UOF统计比分
                matchScoresInfo = matchScoreInfoRepository.selectByExample(thirdMatchInfo.getId(), SourceTypeEnum.UOF.getCode());
                if (matchScoresInfo != null) {
                    listScore.add(matchScoresInfo);
                }
            }
        }
        if (listScore.isEmpty()) {
            log.info("查询标准比分，无三方比分,直接返回：{}",standardMatchId);
            dto.setScores(list);
            return dto;
        }
        //三方事件源比分转MAP返回前端
        Map<String, List<MatchScoresInfo>> scoreMaps =
                listScore.stream().collect(Collectors.groupingBy(MatchScoresInfo::getDataSourceCode, LinkedHashMap::new, Collectors.toList()));
        int index = 1;//排序
        for (Map.Entry<String, List<MatchScoresInfo>> values : scoreMaps.entrySet()) {
            String dataSourceCode = values.getKey();
            String scoresJson = values.getValue().get(0).getScoresJson();
            StandardScoreCenter dataSourceScores = new StandardScoreCenter();
            dataSourceScores.setDataSourceCode(dataSourceCode);
            dataSourceScores.setStandardMatchId(standardMatchId);
            dataSourceScores.setSportId(matchInfo.getSportId());
            dataSourceScores.setIndex(index++);
            Long period = values.getValue().get(0).getPeriod();
            //是否主数据源
            if (dataSourceCode.equals(match.getBusinessEvent())) {
                dataSourceScores.setIsMain(true);
            } else {
                dataSourceScores.setIsMain(false);
            }
            //组装数据源比分
            this.buildFootballScore(dataSourceScores, scoresJson,null,period);
            list.add(dataSourceScores);
        }
        if (!list.isEmpty()) {
            //排序，保证0-标准比分一直处于第一个
            list.sort(Comparator.comparing((StandardScoreCenter::getIndex)));
            this.chechScoreIsDifferent(list);
        }
        dto.setScores(list);
        return dto;
    }

    /**
     * 比分为空的情况下 补齐数据结构
     * @param centerStand
     */
    public static void scoreIsNullExtractFootball(StandardScoreCenter centerStand,String dataSourceAccoSwitch) {
        List<StandardScoreDTO> listScore = new ArrayList<>();
        List<Integer> index1 = new ArrayList<>(Arrays.asList(1,5,9,13));
        List<Integer> index2 = new ArrayList<>(Arrays.asList(2,6,10,14));
        List<Integer> index3 = new ArrayList<>(Arrays.asList(3,7,11,15));
        List<Integer> index4 = new ArrayList<>(Arrays.asList(4,8,12,16));
        FootballSwitch switchs = new FootballSwitch();
        if(StrUtil.isNotEmpty(dataSourceAccoSwitch)){
            switchs = JSONUtil.toBean(dataSourceAccoSwitch, FootballSwitch.class);
        }
        //18个下标设置空比分 回应页面可以编辑
        for (int i = 1; i <= 18; i++) {
            StandardScoreDTO scores = new StandardScoreDTO();
            if(index1.contains(i)){
                scores.setPeriodId(6L);
                if(i==1){
                    scores.setSwitchs(switchs.getYellowHf());
                }else if(i==5){
                    scores.setSwitchs(switchs.getRedHf());
                }else if(i==9){
                    scores.setSwitchs(switchs.getCornerHf());
                }else{
                    scores.setSwitchs(switchs.getGoalHf());
                }
            }else if(index2.contains(i)){
                scores.setPeriodId(7L);
                if(i==2){
                    scores.setSwitchs(switchs.getYellowFt());
                }else if(i==6){
                    scores.setSwitchs(switchs.getRedFt());
                }else if(i==10){
                    scores.setSwitchs(switchs.getCornerFt());
                }else{
                    scores.setSwitchs(switchs.getGoalFt());
                }
            }else if(index3.contains(i)){
                scores.setPeriodId(100L);
            }else if(index4.contains(i)){
                scores.setPeriodId(110L);
                if(i==4){
                    scores.setSwitchs(switchs.getYellowOt());
                }else if(i==8){
                    scores.setSwitchs(switchs.getRedOt());
                }else if(i==12){
                    scores.setSwitchs(switchs.getCornerOt());
                }else{
                    scores.setSwitchs(switchs.getGoalOt());
                }
            }else{
                if(i==17){
                    scores.setPeriodId(10L);
                    scores.setSwitchs(switchs.getPenaltyAwarded());
                } else if(i==18){
                    scores.setPeriodId(50L);
                    scores.setSwitchs(switchs.getPenalty());
                }
            }
            scores.setHome(null);
            scores.setAway(null);
            scores.setIndex(i);
            listScore.add(scores);
        }

        centerStand.setScores(listScore);


        List<StandardScoresDetailDTO> minScore = new ArrayList<>();

        Long[] indexfif = new Long[]{60899L,61799L,62699L,73599L,74499L,75399L};
        for(int i=0;i<indexfif.length;i++){
            Long fifteenCode = indexfif[i];
            StandardScoresDetailDTO fifteenMinScores = new StandardScoresDetailDTO();
            fifteenMinScores.setPeriodId(fifteenCode);
            fifteenMinScores.setHomeGoal(null);
            fifteenMinScores.setAwayGoal(null);
            fifteenMinScores.setHomeCorner(null);
            fifteenMinScores.setAwayCorner(null);
            fifteenMinScores.setHomeYellowCard(null);
            fifteenMinScores.setAwayYellowCard(null);
            fifteenMinScores.setHomeRedCard(null);
            fifteenMinScores.setAwayRedCard(null);
            minScore.add(fifteenMinScores);
        }
        centerStand.setMinute15Scores(minScore);
    }


    public static Integer getScores(Integer scores){
        if(scores==null){
            return 0;
        }
        return  scores;
    }

//    public static void main(String[] args) {
        //获取标准比分
//        StandardScoreCenter centerStand = new StandardScoreCenter();
//        centerStand.setDataSourceCode("STAND");
//        centerStand.setIndex(0);//排序保证标准比分放最前面
//        centerStand.setStandardMatchId(123456L);
//        centerStand.setSportId(2L);
//        centerStand.setSwitchStatus("");
//        //组装标准比分
//        buildFootballScore(centerStand, "", "");
//        if (centerStand.getScores() == null || centerStand.getScores().isEmpty()) {
//            scoreIsNullExtractFootball(centerStand);
//        }
//        List<StandardScoreCenter> list = new ArrayList<>();
//        list.add(centerStand);
//        System.out.println(DigestUtil.md5Hex("FOOTBALL_STANDARD_MATCH_SCORES:4505117"));

//    }

    /**
     * 清除需要汇总计算的比分，保留其他比分如进攻危险进攻等比分
     * @param wholeScores
     */
    private static void initWholeScores(FootballScores wholeScores) {
        wholeScores.setGoal(new CommonItem());
        wholeScores.setCorner(new CommonItem());
        wholeScores.setYellowCard(new  CommonItem());
        wholeScores.setRedCard(new CommonItem());
//        wholeScores.setDangerousAttack(new CommonItem());
//        wholeScores.setAttack(new CommonItem());
        wholeScores.setPenaltyAwarded(new CommonItem());
    }

    public void  buildFootballScore(StandardScoreCenter center, String scoresJson,String switchStr,Long period) {
        //开关
        String str = switchStr;
        FootballSwitch switchs = new FootballSwitch();
        if(StrUtil.isNotEmpty(str)){
            switchs = JSONUtil.toBean(str, FootballSwitch.class);
        }
        //每个数据对应的下标
        int[] index1 = new int[]{1,5,9,13};
        int[] index2 = new int[]{2,6,10,14};
        int[] index3 = new int[]{3,7,11,15};
        int[] index4 = new int[]{4,8,12,16};
        int[] index5 = new int[]{17};
        int[] index6 = new int[]{18};
        //比分内容
        List<StandardScoreDTO> listScore = new ArrayList<>();
        if (StringUtils.isEmpty(scoresJson)) {
            log.info("{} 查询标准比分,比分为空:{}",center.getStandardMatchId(),listScore);
            return;
        }
        //标准比分中心页面内容
        JSONObject periodScores = JSONObject.parseObject(scoresJson);
        Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodScores);
        try{
            FootballScores sc6 = allPeriodScores.get(6L);
            if(sc6==null){
                addNullScores(index1, listScore,6L,switchs);
            }else{
                addScores6(sc6, listScore,switchs);
            }
            FootballScores sc7 = allPeriodScores.get(7L);
            if(sc7==null){
                addNullScores(index2, listScore,7L,switchs);
            }else{
                addScores7(sc7, listScore,switchs);
            }
            if(sc6==null && sc7==null){
                addNullScores(index3, listScore,100L,switchs);
            }else{
                addScore100(sc6, sc7, listScore);
            }
            //加时赛比分
            FootballScores sc110 = allPeriodScores.get(110L);
            if(sc110!=null){
                addScores110(sc110, switchs, listScore);
            }else{
                //标准比分没数据就从三方比分里面取,41+42
                FootballScores sc41 = allPeriodScores.get(41L);
                FootballScores sc42 = allPeriodScores.get(42L);
                if(sc41==null && sc42==null){
                    addNullScores(index4, listScore,110L,switchs);
                }else{
                    addScores42(sc41, sc42, switchs, listScore);
                }
            }
            //点球大战
            FootballScores sc50 = allPeriodScores.get(50L);
            if(sc50==null){
                addNullScores(index6, listScore,50L,switchs);
            }else{
                StandardScoreDTO scores50 = new StandardScoreDTO();
                scores50.setPeriodId(50L);
                scores50.setIndex(18);
                scores50.setHome(sc50.getGoal().getHome());
                scores50.setAway(sc50.getGoal().getAway());
                scores50.setSwitchs(switchs.getPenalty());
                listScore.add(scores50);
            }
            //常规点球
            FootballScores wholdScore = allPeriodScores.get(WHOLE_MATCH);
            log.info("查询点球比分：{}",wholdScore);
            if(wholdScore==null){
                addNullScores(index5, listScore,WHOLE_MATCH,switchs);
            }else{
                StandardScoreDTO scoresPenalty = new StandardScoreDTO();
                //常规赛点球 阶段ID定义为10
                scoresPenalty.setPeriodId(10L);
                scoresPenalty.setIndex(17);
                scoresPenalty.setHome(wholdScore.getPenaltyAwarded().getHome());
                scoresPenalty.setAway(wholdScore.getPenaltyAwarded().getAway());
                if(period==0){
                    scoresPenalty.setHome(null);
                    scoresPenalty.setAway(null);
                }
                scoresPenalty.setSwitchs(switchs.getPenaltyAwarded());
                listScore.add(scoresPenalty);
            }
        }catch(Exception e){
            log.error("查询比分/组装比分异常 赛事ID:{} ，",center.getStandardMatchId(),e);
        }
        //足球根据下标排序 对应具体位置
        listScore.sort(Comparator.comparing((StandardScoreDTO::getIndex)));
        center.setScores(listScore);
        //查询、组装15分钟比分
        center.setMinute15Scores(build15Mines(allPeriodScores,switchs));
        //只查询一次结算比分
        if("STAND".equals(center.getDataSourceCode())) {
            //组装结算比分
            center.setSettleScores(buildSettleScores(center.getStandardMatchId(), listScore));
        }
    }


    /**
     * 结算比分
     * @param standardMatchId
     * @param listScore
     * @return
     */
    public List<StandardScoreDTO> buildSettleScores(Long standardMatchId, List<StandardScoreDTO> listScore) {
        log.info("::{}::校验结算比分是否不同：{}",standardMatchId,JSON.toJSONString(listScore));
        List<StandardScoreDTO> settleResult = new ArrayList<>();
        MatchSettleResultExample example =  new MatchSettleResultExample();
        example.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
        List<MatchSettleResult> list = matchSettleResultMapper.selectByExample(example);
        if (list.isEmpty()) {
            return null;
        }
        for (MatchSettleResult result: list){
            Integer index = getFootballSettleScoreIndex(result.getScoreCode());
            if(index==null){
                continue;
            }

            if(!listScore.isEmpty()){
                for(StandardScoreDTO score: listScore ){
                    if(score.getIndex()==index){
                        StandardScoreDTO scores = new StandardScoreDTO();
                        scores.setHome(result.getT1());
                        scores.setAway(result.getT2());
                        scores.setIndex(index);
                        scores.setPeriodId(Long.valueOf(index));
                        //添加结算比分与标准比分不一致的标识
                        if(!Objects.equals(score.getHome(), result.getT1()) || !Objects.equals(score.getAway(), result.getT2())){
                            scores.setIsDifference(true);
                        }else{
                            scores.setIsDifference(false);
                        }
                        settleResult.add(scores);
                    }
                }
            }
//            settleResult.add(scores);
        }
        return settleResult;
    }

    /**
     * 查询、组装15分钟比分
     * @param allPeriodScores
     * @return
     */
    private static List<StandardScoresDetailDTO> build15Mines(Map<Long, FootballScores> allPeriodScores,FootballSwitch switchs) {
        List<StandardScoresDetailDTO> listScore = new ArrayList<>();
        Long[] indexfif = new Long[]{60899L,61799L,62699L,73599L,74499L,75399L};
        for(int i=0;i<indexfif.length;i++){
            Long fifteenCode = indexfif[i];
            FootballScores fifteenScores = allPeriodScores.get(fifteenCode);
            if(fifteenScores==null){
                fifteenScores = new FootballScores(fifteenCode);
            }
            StandardScoresDetailDTO fifteenMinScores = new StandardScoresDetailDTO();
            fifteenMinScores.setPeriodId(fifteenCode);
            fifteenMinScores.setHomeGoal(fifteenScores.getGoal()!=null?fifteenScores.getGoal().getHome():0);
            fifteenMinScores.setAwayGoal(fifteenScores.getGoal()!=null?fifteenScores.getGoal().getAway():0);
            fifteenMinScores.setHomeCorner(fifteenScores.getCorner()!=null?fifteenScores.getCorner().getHome():0);
            fifteenMinScores.setAwayCorner(fifteenScores.getCorner()!=null?fifteenScores.getCorner().getAway():0);
            fifteenMinScores.setHomeYellowCard(fifteenScores.getYellowCard()!=null?fifteenScores.getYellowCard().getHome():0);
            fifteenMinScores.setAwayYellowCard(fifteenScores.getYellowCard()!=null?fifteenScores.getYellowCard().getAway():0);
            fifteenMinScores.setHomeRedCard(fifteenScores.getRedCard()!=null?fifteenScores.getRedCard().getHome():0);
            fifteenMinScores.setAwayRedCard(fifteenScores.getRedCard()!=null?fifteenScores.getRedCard().getAway():0);
            set15minSwitch(fifteenMinScores,fifteenCode,switchs);
            listScore.add(fifteenMinScores);
        }
        return listScore;
    }

    private static void set15minSwitch(StandardScoresDetailDTO fifteenMinScores, Long fifteenCode,FootballSwitch switchs) {
        List<Long> indexfif = new ArrayList<>(Arrays.asList(60899L,61799L,62699L,73599L,74499L,75399L));
        if(fifteenCode==null  || !indexfif.contains(fifteenCode)){
            return;
        }
        if(fifteenCode.equals(60899L)){
            fifteenMinScores.setGoalSwitch(switchs.getGoal60899());
            fifteenMinScores.setCornerSwitch(switchs.getCorner60899());
            fifteenMinScores.setYellowSwitch(switchs.getYellowCard60899());
            fifteenMinScores.setRedSwitch(switchs.getRedCard60899());
            fifteenMinScores.setGoalIndex(21);
            fifteenMinScores.setCornerIndex(22);
            fifteenMinScores.setRedIndex(23);
            fifteenMinScores.setYellowIndex(24);

        } else if (fifteenCode.equals(61799L)) {
            fifteenMinScores.setGoalSwitch(switchs.getGoal61799());
            fifteenMinScores.setCornerSwitch(switchs.getCorner61799());
            fifteenMinScores.setYellowSwitch(switchs.getYellowCard61799());
            fifteenMinScores.setRedSwitch(switchs.getRedCard61799());
            fifteenMinScores.setGoalIndex(25);
            fifteenMinScores.setCornerIndex(26);
            fifteenMinScores.setRedIndex(27);
            fifteenMinScores.setYellowIndex(28);
        } else if (fifteenCode.equals(62699L)) {
            fifteenMinScores.setGoalSwitch(switchs.getGoal62699());
            fifteenMinScores.setCornerSwitch(switchs.getCorner62699());
            fifteenMinScores.setYellowSwitch(switchs.getYellowCard62699());
            fifteenMinScores.setRedSwitch(switchs.getRedCard62699());
            fifteenMinScores.setGoalIndex(29);
            fifteenMinScores.setCornerIndex(30);
            fifteenMinScores.setRedIndex(31);
            fifteenMinScores.setYellowIndex(32);
        } else if (fifteenCode.equals(73599L)) {
            fifteenMinScores.setGoalSwitch(switchs.getGoal73599());
            fifteenMinScores.setCornerSwitch(switchs.getCorner73599());
            fifteenMinScores.setYellowSwitch(switchs.getYellowCard73599());
            fifteenMinScores.setRedSwitch(switchs.getRedCard73599());
            fifteenMinScores.setGoalIndex(33);
            fifteenMinScores.setCornerIndex(34);
            fifteenMinScores.setRedIndex(35);
            fifteenMinScores.setYellowIndex(36);
        }else if (fifteenCode.equals(74499L)){
            fifteenMinScores.setGoalSwitch(switchs.getGoal74499());
            fifteenMinScores.setCornerSwitch(switchs.getCorner74499());
            fifteenMinScores.setYellowSwitch(switchs.getYellowCard74499());
            fifteenMinScores.setRedSwitch(switchs.getRedCard74499());
            fifteenMinScores.setGoalIndex(37);
            fifteenMinScores.setCornerIndex(38);
            fifteenMinScores.setRedIndex(39);
            fifteenMinScores.setYellowIndex(40);
        } else if (fifteenCode.equals(75399L)){
            fifteenMinScores.setGoalSwitch(switchs.getGoal75399());
            fifteenMinScores.setCornerSwitch(switchs.getCorner75399());
            fifteenMinScores.setYellowSwitch(switchs.getYellowCard75399());
            fifteenMinScores.setRedSwitch(switchs.getRedCard75399());
            fifteenMinScores.setGoalIndex(41);
            fifteenMinScores.setCornerIndex(42);
            fifteenMinScores.setRedIndex(43);
            fifteenMinScores.setYellowIndex(44);
        }
    }

    private static void addScores42(FootballScores sc41, FootballScores sc42, FootballSwitch switchs, List<StandardScoreDTO> listScore) {
        int homeGoalOt110=0, homeCornerOt110=0, homeYellowCardsOt110=0,homeRedCardsOt110=0;
        int awayGoalOt110=0, awayCornerOt110=0, awayYellowCardsOt110=0,awayRedCardsOt110=0;
        if(sc41 !=null){
            homeGoalOt110+= sc41.getGoal().getHome();
            homeCornerOt110+= sc41.getCorner().getHome();
            homeYellowCardsOt110+= sc41.getYellowCard().getHome();
            homeRedCardsOt110+= sc41.getRedCard().getHome();
            awayGoalOt110+= sc41.getGoal().getAway();
            awayCornerOt110+= sc41.getCorner().getAway();
            awayYellowCardsOt110+= sc41.getYellowCard().getAway();
            awayRedCardsOt110+= sc41.getRedCard().getAway();
        }
        if(sc42 !=null){
            homeGoalOt110+= sc42.getGoal().getHome();
            homeCornerOt110+= sc42.getCorner().getHome();
            homeYellowCardsOt110+= sc42.getYellowCard().getHome();
            homeRedCardsOt110+= sc42.getRedCard().getHome();
            awayGoalOt110+= sc42.getGoal().getAway();
            awayCornerOt110+= sc42.getCorner().getAway();
            awayYellowCardsOt110+= sc42.getYellowCard().getAway();
            awayRedCardsOt110+= sc42.getRedCard().getAway();
        }
        //标准比分有数据就从标准比分里面取
        StandardScoreDTO scores110y = new StandardScoreDTO();
        scores110y.setPeriodId(110L);
        scores110y.setIndex(4);
        scores110y.setHome(getScores(homeYellowCardsOt110));
        scores110y.setAway(getScores(awayYellowCardsOt110));
        scores110y.setSwitchs(switchs.getYellowOt());
        listScore.add(scores110y);
        StandardScoreDTO scores110r = new StandardScoreDTO();
        scores110r.setPeriodId(110L);
        scores110r.setIndex(8);
        scores110r.setHome(getScores(homeRedCardsOt110));
        scores110r.setAway(getScores(awayRedCardsOt110));
        scores110r.setSwitchs(switchs.getRedOt());
        listScore.add(scores110r);
        StandardScoreDTO scores110c = new StandardScoreDTO();
        scores110c.setPeriodId(110L);
        scores110c.setIndex(12);
        scores110c.setHome(getScores(homeCornerOt110));
        scores110c.setAway(getScores(awayCornerOt110));
        scores110c.setSwitchs(switchs.getGoalOt());
        listScore.add(scores110c);
        StandardScoreDTO scores110g = new StandardScoreDTO();
        scores110g.setPeriodId(110L);
        scores110g.setIndex(16);
        scores110g.setHome(getScores(homeGoalOt110));
        scores110g.setAway(getScores(awayGoalOt110));
        scores110g.setSwitchs(switchs.getGoalOt());
        listScore.add(scores110g);
    }

    private static void addScores110(FootballScores sc110, FootballSwitch switchs, List<StandardScoreDTO> listScore) {
//        if(sc7==null){
//            sc7  = new FootballScores(7L);
//        }
        //标准比分有数据就从标准比分里面取
        StandardScoreDTO scores110y = new StandardScoreDTO();
        scores110y.setPeriodId(110L);
        scores110y.setIndex(4);
        scores110y.setHome(sc110.getYellowCard().getHome());
        scores110y.setAway(sc110.getYellowCard().getAway());
        scores110y.setSwitchs(switchs.getYellowOt());
        listScore.add(scores110y);
        StandardScoreDTO scores110r = new StandardScoreDTO();
        scores110r.setPeriodId(110L);
        scores110r.setIndex(8);
        scores110r.setHome(sc110.getRedCard().getHome());
        scores110r.setAway(sc110.getRedCard().getAway());
        scores110r.setSwitchs(switchs.getRedOt());
        listScore.add(scores110r);
        StandardScoreDTO scores110c = new StandardScoreDTO();
        scores110c.setPeriodId(110L);
        scores110c.setIndex(12);
        scores110c.setHome(sc110.getCorner().getHome());
        scores110c.setAway(sc110.getCorner().getAway());
        scores110c.setSwitchs(switchs.getCornerOt());
        listScore.add(scores110c);
        StandardScoreDTO scores110g = new StandardScoreDTO();
        scores110g.setPeriodId(110L);
        scores110g.setIndex(16);
        scores110g.setHome(sc110.getGoal().getHome());
        scores110g.setAway(sc110.getGoal().getAway());
        scores110g.setSwitchs(switchs.getGoalOt());
        listScore.add(scores110g);
    }

    private static void addNullScores(int[] index, List<StandardScoreDTO> listScore,Long periodId,FootballSwitch switchs) {
        //无数据赋值null
        for (int i = 0; i< index.length; i++){
            StandardScoreDTO scoresNull = new StandardScoreDTO();
            scoresNull.setPeriodId(periodId);
            scoresNull.setIndex(index[i]);
            scoresNull.setHome(null);
            scoresNull.setAway(null);
            if(periodId==6L){
                if(index[i]==1){
                    scoresNull.setSwitchs(switchs.getYellowHf());
                }else if(index[i]==5){
                    scoresNull.setSwitchs(switchs.getRedHf());
                }else if(index[i]==9){
                    scoresNull.setSwitchs(switchs.getCornerHf());
                }else{
                    scoresNull.setSwitchs(switchs.getGoalHf());
                }
            } else if(periodId==7L){
                if(index[i]==2){
                    scoresNull.setSwitchs(switchs.getYellowFt());
                }else if(index[i]==6){
                    scoresNull.setSwitchs(switchs.getRedFt());
                }else if(index[i]==10){
                    scoresNull.setSwitchs(switchs.getCornerFt());
                }else{
                    scoresNull.setSwitchs(switchs.getGoalFt());
                }
            } else if(periodId==110L){
                if(index[i]==4){
                    scoresNull.setSwitchs(switchs.getYellowOt());
                }else if(index[i]==8){
                    scoresNull.setSwitchs(switchs.getRedOt());
                }else if(index[i]==12){
                    scoresNull.setSwitchs(switchs.getCornerOt());
                }else{
                    scoresNull.setSwitchs(switchs.getGoalOt());
                }
            } else if (periodId==50L){
                scoresNull.setSwitchs(switchs.getPenalty());
            } else if (periodId==10L){
                scoresNull.setSwitchs(switchs.getPenaltyAwarded());
            } else if (periodId==100L){
                scoresNull.setSwitchs(null);
            }else if (periodId==60899L){
                //开始组装15分钟数据的开关 ps:页面格式与阶段比分展示不一致,所以这里index递加
                if(index[i]==21){
                    scoresNull.setSwitchs(switchs.getGoal60899());
                }else if(index[i]==22){
                    scoresNull.setSwitchs(switchs.getCorner60899());
                }else if(index[i]==23){
                    scoresNull.setSwitchs(switchs.getRedCard60899());
                }else if(index[i]==24){
                    scoresNull.setSwitchs(switchs.getYellowCard60899());
                }
            }else if (periodId==61799L){
                if(index[i]==25){
                    scoresNull.setSwitchs(switchs.getGoal61799());
                }else if(index[i]==26){
                    scoresNull.setSwitchs(switchs.getCorner61799());
                }else if(index[i]==27){
                    scoresNull.setSwitchs(switchs.getRedCard61799());
                }else if(index[i]==28){
                    scoresNull.setSwitchs(switchs.getYellowCard61799());
                }
            }else if (periodId==62699L){
                if(index[i]==29){
                    scoresNull.setSwitchs(switchs.getGoal62699());
                }else if(index[i]==30){
                    scoresNull.setSwitchs(switchs.getCorner62699());
                }else if(index[i]==31){
                    scoresNull.setSwitchs(switchs.getRedCard62699());
                }else if(index[i]==32){
                    scoresNull.setSwitchs(switchs.getYellowCard62699());
                }
            }else if (periodId==73599L){
                if(index[i]==33){
                    scoresNull.setSwitchs(switchs.getGoal73599());
                }else if(index[i]==34){
                    scoresNull.setSwitchs(switchs.getCorner73599());
                }else if(index[i]==35){
                    scoresNull.setSwitchs(switchs.getRedCard73599());
                }else if(index[i]==36){
                    scoresNull.setSwitchs(switchs.getYellowCard73599());
                }
            }else if (periodId==74499L){
                if(index[i]==37){
                    scoresNull.setSwitchs(switchs.getGoal74499());
                }else if(index[i]==38){
                    scoresNull.setSwitchs(switchs.getCorner74499());
                }else if(index[i]==39){
                    scoresNull.setSwitchs(switchs.getRedCard74499());
                }else if(index[i]==40){
                    scoresNull.setSwitchs(switchs.getYellowCard74499());
                }
            }else if (periodId==75399L){
                if(index[i]==41){
                    scoresNull.setSwitchs(switchs.getGoal75399());
                }else if(index[i]==42){
                    scoresNull.setSwitchs(switchs.getCorner75399());
                }else if(index[i]==43){
                    scoresNull.setSwitchs(switchs.getRedCard75399());
                }else if(index[i]==44){
                    scoresNull.setSwitchs(switchs.getYellowCard75399());
                }
            }else{
                scoresNull.setSwitchs(1);
            }
            listScore.add(scoresNull);
        }
    }

    private static void addScore100(FootballScores sc6, FootballScores sc7, List<StandardScoreDTO> listScore) {
        if(sc6==null){
            sc6 = new FootballScores(6L);
        }
        if(sc7==null){
            sc7 = new FootballScores(7L);
        }
        StandardScoreDTO scores100y = new StandardScoreDTO();
        scores100y.setPeriodId(100L);
        scores100y.setIndex(3);
        scores100y.setHome(getScores(sc6.getYellowCard().getHome())+getScores(sc7.getYellowCard().getHome()));
        scores100y.setAway(getScores(sc6.getYellowCard().getAway())+getScores(sc7.getYellowCard().getAway()));
        listScore.add(scores100y);
        StandardScoreDTO scores100r = new StandardScoreDTO();
        scores100r.setPeriodId(100L);
        scores100r.setIndex(7);
        scores100r.setHome(getScores(sc6.getRedCard().getHome())+getScores(sc7.getRedCard().getHome()));
        scores100r.setAway(getScores(sc6.getRedCard().getAway())+getScores(sc7.getRedCard().getAway()));
        listScore.add(scores100r);
        StandardScoreDTO scores100c = new StandardScoreDTO();
        scores100c.setPeriodId(100L);
        scores100c.setIndex(11);
        scores100c.setHome(getScores(sc6.getCorner().getHome())+getScores(sc7.getCorner().getHome()));
        scores100c.setAway(getScores(sc6.getCorner().getAway())+getScores(sc7.getCorner().getAway()));
        listScore.add(scores100c);
        StandardScoreDTO scores100g = new StandardScoreDTO();
        scores100g.setPeriodId(100L);
        scores100g.setIndex(15);
        scores100g.setHome(getScores(sc6.getGoal().getHome())+getScores(sc7.getGoal().getHome()));
        scores100g.setAway(getScores(sc6.getGoal().getAway())+getScores(sc7.getGoal().getAway()));
        listScore.add(scores100g);
    }

    private static void addScores7(FootballScores sc7, List<StandardScoreDTO> listScore,FootballSwitch switchs) {
        StandardScoreDTO scores7y = new StandardScoreDTO();
        scores7y.setPeriodId(7L);
        scores7y.setIndex(2);
        scores7y.setHome(sc7.getYellowCard().getHome());
        scores7y.setAway(sc7.getYellowCard().getAway());
        scores7y.setSwitchs(switchs.getYellowFt());
        listScore.add(scores7y);
        StandardScoreDTO scores7r = new StandardScoreDTO();
        scores7r.setPeriodId(7L);
        scores7r.setIndex(6);
        scores7r.setHome(sc7.getRedCard().getHome());
        scores7r.setAway(sc7.getRedCard().getAway());
        scores7r.setSwitchs(switchs.getRedFt());
        listScore.add(scores7r);
        StandardScoreDTO scores7c = new StandardScoreDTO();
        scores7c.setPeriodId(7L);
        scores7c.setIndex(10);
        scores7c.setHome(sc7.getCorner().getHome());
        scores7c.setAway(sc7.getCorner().getAway());
        scores7c.setSwitchs(switchs.getCornerFt());
        listScore.add(scores7c);
        StandardScoreDTO scores7g = new StandardScoreDTO();
        scores7g.setPeriodId(7L);
        scores7g.setIndex(14);
        scores7g.setHome(sc7.getGoal().getHome());
        scores7g.setAway(sc7.getGoal().getAway());
        scores7g.setSwitchs(switchs.getGoalFt());
        listScore.add(scores7g);
    }

    private static void addScores6(FootballScores sc6, List<StandardScoreDTO> listScore,FootballSwitch switchs) {
        StandardScoreDTO scores6y = new StandardScoreDTO();
        scores6y.setPeriodId(6L);
        scores6y.setIndex(1);
        scores6y.setHome(sc6.getYellowCard().getHome());
        scores6y.setAway(sc6.getYellowCard().getAway());
        scores6y.setSwitchs(switchs.getYellowHf());
        listScore.add(scores6y);
        StandardScoreDTO scores6r = new StandardScoreDTO();
        scores6r.setPeriodId(6L);
        scores6r.setIndex(5);
        scores6r.setHome(sc6.getRedCard().getHome());
        scores6r.setAway(sc6.getRedCard().getAway());
        scores6r.setSwitchs(switchs.getRedHf());
        listScore.add(scores6r);
        StandardScoreDTO scores6c = new StandardScoreDTO();
        scores6c.setPeriodId(6L);
        scores6c.setIndex(9);
        scores6c.setHome(sc6.getCorner().getHome());
        scores6c.setAway(sc6.getCorner().getAway());
        scores6c.setSwitchs(switchs.getCornerHf());
        listScore.add(scores6c);
        StandardScoreDTO scores6g = new StandardScoreDTO();
        scores6g.setPeriodId(6L);
        scores6g.setIndex(13);
        scores6g.setHome(sc6.getGoal().getHome());
        scores6g.setAway(sc6.getGoal().getAway());
        scores6g.setSwitchs(switchs.getGoalHf());
        listScore.add(scores6g);
    }


    /**
     * 修改标准比分
     * @param scores
     * @return
     */
    public Response editStandScores(StandardScoreCenter scores,StandardMatchScores standardMatchScores,StandardMatchInfo standardMatchInfo){
        log.info("足球修改标准比分:{},{}",scores.getStandardMatchId(),JSON.toJSONString(scores));
        String scoresJson = standardMatchScores.getScoreJson();
        Map<Long, FootballScores> allPeriodScores = new HashMap<>();
        if(StrUtil.isNotEmpty(scoresJson)) {
            JSONObject periodFootballScores = JSONObject.parseObject(scoresJson);
            allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
        }
        FootballScores wholeScores = allPeriodScores.get(-1L)!=null?allPeriodScores.get(-1L):new FootballScores(-1L);
        initWholeScores(wholeScores);
        //要修改的比分
        List<StandardScoreDTO> editScores = scores.getScores();
        //编辑校验
        Integer rtnFlag = checkEditScores(scores,standardMatchInfo.getMatchLength(),standardMatchInfo.getMatchPeriodId());
        if(rtnFlag!=0){
            return Response.failed(rtnFlag.toString());
        }
        //编辑校验
        Integer editFlag = checkEditMinScores(scores);
        if(editFlag!=0){
            return Response.failed(editFlag.toString());
        }
        //常规赛比分
        for(StandardScoreDTO score :editScores){
            if(score.getHome()==null && score.getAway()==null){
                continue;
            }
            if(score.getPeriodId()==6L){
                FootballScores score6 = getScore6(score,allPeriodScores);
                allPeriodScores.put(6L,score6);
            }else if(score.getPeriodId()==7L){
                FootballScores score7 = getScore7(score,allPeriodScores);
                allPeriodScores.put(7L,score7);
            }else if(score.getPeriodId()==100L){
                FootballScores score100 = getScores100(score,allPeriodScores);
                allPeriodScores.put(100L,score100);
            }else if(score.getPeriodId()==110L){
                FootballScores score110 = getScores110(score,allPeriodScores);
                allPeriodScores.put(110L,score110);
            }else if(score.getPeriodId()==50L){
                //点球大战
                FootballScores score50 = new FootballScores(50L);
                score50.setGoal(new CommonItem(score.getHome(),score.getAway()));
                allPeriodScores.put(50L,score50);
            }else{
                if(score.getIndex()==17){
                  //常规点球
                  wholeScores.setPenaltyAwarded(new CommonItem(score.getHome(),score.getAway()));
                  log.info("修改点球比分：{}",wholeScores);
                }
            }
        }

        //要修改的比分 - 15分钟区间比分
        List<StandardScoresDetailDTO> minute15Scores = scores.getMinute15Scores();
        if(minute15Scores !=null && !minute15Scores.isEmpty()){
            for(StandardScoresDetailDTO score :minute15Scores){
                FootballScores fifminScores = new FootballScores(score.getPeriodId());
                fifminScores.setGoal(new CommonItem(score.getHomeGoal(),score.getAwayGoal()));
                fifminScores.setCorner(new CommonItem(score.getHomeCorner(),score.getAwayCorner()));
                fifminScores.setYellowCard(new CommonItem(score.getHomeYellowCard(),score.getAwayYellowCard()));
                fifminScores.setRedCard(new CommonItem(score.getHomeRedCard(),score.getAwayRedCard()));
                log.info("足球修改标准比分,15分钟阶段:{},比分：{}",score.getPeriodId(),fifminScores);
                allPeriodScores.put(score.getPeriodId(),fifminScores);
            }
        }
        for(Map.Entry<Long, FootballScores> entry : allPeriodScores.entrySet()){
            if(entry.getKey()==6 || entry.getKey()==7 || entry.getKey()==110 ){
                //累计-1比分
                calcWholeScore(wholeScores,allPeriodScores.get(entry.getKey()));
                log.info("足球修改标准比分 -1 :matchId={},peroid={},allPeriodScores={}",scores.getStandardMatchId(),entry.getKey(),JSONUtil.toJsonStr(wholeScores));
            }
            //计算每个阶段的罚牌比分
            entry.getValue().countFaCard();
        }
        allPeriodScores.put(WHOLE_MATCH,wholeScores);
//        calcWholdScores(allPeriodScores,wholeScores);
        log.info("足球修改标准比分:matchId={},allPeriodScores={}",scores.getStandardMatchId(),JSONUtil.toJsonStr(allPeriodScores));
        standardMatchScores.setScoreJson(JSONUtil.toJsonStr(allPeriodScores));
        //数据源开关联动
        setSwitch(standardMatchScores,scoresJson,scores);
        super.updateEndSendScoresInfo(standardMatchScores,standardMatchInfo);
        //添加日志
        super.editScoreCenterSettleLog(scoresJson,standardMatchScores,scores,null);
        return Response.success();
    }

    private static Integer checkEditMinScores(StandardScoreCenter scores) {
        Integer flag = 0;
        List<StandardScoreDTO> hfYellow = scores.getScores().stream().filter(s -> s.getIndex() == 1).collect(Collectors.toList());
        List<StandardScoreDTO> ftYellow = scores.getScores().stream().filter(s -> s.getIndex() == 2).collect(Collectors.toList());

        List<StandardScoreDTO> hfRed = scores.getScores().stream().filter(s -> s.getIndex() == 5).collect(Collectors.toList());
        List<StandardScoreDTO> ftRed = scores.getScores().stream().filter(s -> s.getIndex() == 6).collect(Collectors.toList());

        List<StandardScoreDTO> hfCorner = scores.getScores().stream().filter(s -> s.getIndex() == 9).collect(Collectors.toList());
        List<StandardScoreDTO> ftCorner = scores.getScores().stream().filter(s -> s.getIndex() == 10).collect(Collectors.toList());

        List<StandardScoreDTO> hfGoal = scores.getScores().stream().filter(s -> s.getIndex() == 13).collect(Collectors.toList());
        List<StandardScoreDTO> ftGoal = scores.getScores().stream().filter(s -> s.getIndex() == 14).collect(Collectors.toList());
        List<StandardScoresDetailDTO> minutes = scores.getMinute15Scores();
        if(minutes==null || minutes.isEmpty()){
            return 0;
        }
        List<Long> indexfif6 = new ArrayList<>(Arrays.asList(60899L,61799L,62699L));
        List<Long> indexfif7 = new ArrayList<>(Arrays.asList(73599L,74499L,75399L));
        Integer hfYellowHome = 0,hfYellowAway = 0;
        Integer hfRedHome = 0,hfRedAway = 0;
        Integer hfCornerHome = 0,hfCornerAway = 0;
        Integer hfGoalHome = 0,hfGoalAway = 0;

        Integer ftYellowHome = 0,ftYellowAway = 0;
        Integer ftRedHome = 0,ftRedAway = 0;
        Integer ftCornerHome = 0,ftCornerAway = 0;
        Integer ftGoalHome = 0,ftGoalAway = 0;
        for (StandardScoresDetailDTO minute : minutes) {
            if(indexfif6.contains(minute.getPeriodId())){
                //上半场15分钟区间校验
                hfYellowHome+= minute.getHomeYellowCard()!=null?minute.getHomeYellowCard():0;
                hfYellowAway+= minute.getAwayYellowCard()!=null?minute.getAwayYellowCard():0;
                hfRedHome+= minute.getHomeRedCard()!=null?minute.getHomeRedCard():0;
                hfRedAway+= minute.getAwayRedCard()!=null?minute.getAwayRedCard():0;
                hfCornerHome+= minute.getHomeCorner()!=null?minute.getHomeCorner():0;
                hfCornerAway+= minute.getAwayCorner()!=null?minute.getAwayCorner():0;
                hfGoalHome+= minute.getHomeGoal()!=null?minute.getHomeGoal():0;
                hfGoalAway+= minute.getAwayGoal()!=null?minute.getAwayGoal():0;
            }

            if(indexfif7.contains(minute.getPeriodId())){
                //下半场15分钟区间校验
                ftYellowHome+= minute.getHomeYellowCard()!=null?minute.getHomeYellowCard():0;
                ftYellowAway+= minute.getAwayYellowCard()!=null?minute.getAwayYellowCard():0;
                ftRedHome+= minute.getHomeRedCard()!=null?minute.getHomeRedCard():0;
                ftRedAway+= minute.getAwayRedCard()!=null?minute.getAwayRedCard():0;
                ftCornerHome+= minute.getHomeCorner()!=null?minute.getHomeCorner():0;
                ftCornerAway+= minute.getAwayCorner()!=null?minute.getAwayCorner():0;
                ftGoalHome+= minute.getHomeGoal()!=null?minute.getHomeGoal():0;
                ftGoalAway+= minute.getAwayGoal()!=null?minute.getAwayGoal():0;
            }
        }
        if(hfYellow.get(0).getHome()!=null && hfYellow.get(0).getAway()!=null){
            Boolean ckHfYellow = !Objects.equals(hfYellow.get(0).getHome(), hfYellowHome) || !Objects.equals(hfYellow.get(0).getAway(), hfYellowAway);
            if(ckHfYellow){
                log.info("足球修改标准比分,阶段15分钟区间校验 上半场黄牌:{}:{},{}:{}", hfYellow.get(0).getHome(),hfYellow.get(0).getAway(),hfYellowHome,hfYellowAway);
                return OperateLogTypeEnum.EDIT_TIPS_MSG_18.getCode();
            }
        }
        if(hfRed.get(0).getHome()!=null && hfRed.get(0).getAway()!=null) {
            Boolean ckHfRed = !Objects.equals(hfRed.get(0).getHome(), hfRedHome) || !Objects.equals(hfRed.get(0).getAway(), hfRedAway);
            if(ckHfRed){
                log.info("足球修改标准比分,阶段15分钟区间校验 上半场黄牌:{}:{},{}:{}", hfRed.get(0).getHome(),hfRed.get(0).getAway(),hfRedHome,hfRedAway);
                return OperateLogTypeEnum.EDIT_TIPS_MSG_18.getCode();
            }
        }
        if(hfCorner.get(0).getHome()!=null && hfCorner.get(0).getAway()!=null) {
            Boolean ckHfCorner = !Objects.equals(hfCorner.get(0).getHome(), hfCornerHome) || !Objects.equals(hfCorner.get(0).getAway(), hfCornerAway);
            if(ckHfCorner){
                log.info("足球修改标准比分,阶段15分钟区间校验 上半场进球:{}:{},{}:{}", hfCorner.get(0).getHome(),hfCorner.get(0).getAway(),hfCornerHome,hfCornerAway);
                return OperateLogTypeEnum.EDIT_TIPS_MSG_18.getCode();
            }
        }
        if(hfGoal.get(0).getHome()!=null && hfGoal.get(0).getAway()!=null) {
            Boolean ckHfGoal = !Objects.equals(hfGoal.get(0).getHome(), hfGoalHome) || !Objects.equals(hfGoal.get(0).getAway(), hfGoalAway);
            if(ckHfGoal){
                log.info("足球修改标准比分,阶段15分钟区间校验 上半场进球:{}:{},{}:{}", hfGoal.get(0).getHome(),hfGoal.get(0).getAway(),hfGoalHome,hfGoalAway);
                return OperateLogTypeEnum.EDIT_TIPS_MSG_18.getCode();
            }
        }

        log.info("下半场比分校验开始");

        if(ftYellow.get(0).getHome()!=null && ftYellow.get(0).getAway()!=null) {
            Boolean ckFtYellow = !Objects.equals(ftYellow.get(0).getHome(), ftYellowHome) || !Objects.equals(ftYellow.get(0).getAway(), ftYellowAway);
            if(ckFtYellow){
                log.info("足球修改标准比分,阶段15分钟区间校验 下半场黄牌:{}:{},{}:{}", ftYellow.get(0).getHome(),ftYellow.get(0).getAway(),ftYellowHome,ftYellowAway);
                return OperateLogTypeEnum.EDIT_TIPS_MSG_18.getCode();
            }
        }
        if(ftRed.get(0).getHome()!=null && ftRed.get(0).getAway()!=null) {
            Boolean ckFtRed = !Objects.equals(ftRed.get(0).getHome(), ftRedHome) || !Objects.equals(ftRed.get(0).getAway(), ftRedAway);
            if(ckFtRed){
                log.info("足球修改标准比分,阶段15分钟区间校验 下半场红牌:{}:{},{}:{}", ftRed.get(0).getHome(),ftRed.get(0).getAway(),ftRedHome,ftRedAway);
                return OperateLogTypeEnum.EDIT_TIPS_MSG_18.getCode();
            }
        }
        if(ftCorner.get(0).getHome()!=null && ftCorner.get(0).getAway()!=null) {
            Boolean ckFtCorner = !Objects.equals(ftCorner.get(0).getHome(), ftCornerHome) || !Objects.equals(ftCorner.get(0).getAway(), ftCornerAway);
            if(ckFtCorner){
                log.info("足球修改标准比分,阶段15分钟区间校验 下半场角球:{}:{},{}:{}", ftCorner.get(0).getHome(),ftCorner.get(0).getAway(),ftCornerHome,ftCornerAway);
                return OperateLogTypeEnum.EDIT_TIPS_MSG_18.getCode();
            }
        }
        if(ftGoal.get(0).getHome()!=null && ftGoal.get(0).getAway()!=null) {
            Boolean ckFtGoal = !Objects.equals(ftGoal.get(0).getHome(), ftGoalHome) || !Objects.equals(ftGoal.get(0).getAway(), ftGoalAway);
            if(ckFtGoal){
                log.info("足球修改标准比分,阶段15分钟区间校验 下半场进球:{}:{},{}:{}", ftGoal.get(0).getHome(),ftGoal.get(0).getAway(),ftGoalHome,ftGoalAway);
                return OperateLogTypeEnum.EDIT_TIPS_MSG_18.getCode();
            }
        }
        return flag;
    }

    private static FootballScores getScore6(StandardScoreDTO score,Map<Long, FootballScores> allPeriodScores) {
//        FootballScores score6 = new FootballScores(6L);
        FootballScores score6 = allPeriodScores.get(6L)==null?new FootballScores(6L):allPeriodScores.get(6L);
        if(score.getIndex()==1){
            score6.setYellowCard(new CommonItem(score.getHome(), score.getAway()));
        }else if (score.getIndex()==5){
            score6.setRedCard(new CommonItem(score.getHome(), score.getAway()));
        }else if (score.getIndex()==9){
            score6.setCorner(new CommonItem(score.getHome(), score.getAway()));
        }else if (score.getIndex()==13){
            score6.setGoal(new CommonItem(score.getHome(), score.getAway()));
        }
        log.info("足球修改标准比分,阶段6比分:{}",score6);
        return score6;
    }

    private static FootballScores getScore7(StandardScoreDTO score,Map<Long, FootballScores> allPeriodScores) {
        FootballScores score7 = allPeriodScores.get(7L)!=null?allPeriodScores.get(7L):new FootballScores(7L);
        if(score.getIndex()==2){
            score7.setYellowCard(new CommonItem(score.getHome(), score.getAway()));
        }else if (score.getIndex()==6){
            score7.setRedCard(new CommonItem(score.getHome(), score.getAway()));
        }else if (score.getIndex()==10){
            score7.setCorner(new CommonItem(score.getHome(), score.getAway()));
        }else if (score.getIndex()==14){
            score7.setGoal(new CommonItem(score.getHome(), score.getAway()));
        }
        log.info("足球修改标准比分,阶段7比分:{}",score7);
        return score7;
    }

    private static FootballScores getScores100(StandardScoreDTO score,Map<Long, FootballScores> allPeriodScores) {
        FootballScores score100 = allPeriodScores.get(100L)!=null?allPeriodScores.get(100L):new FootballScores(100L);
        if(score.getIndex()==3){
            score100.setYellowCard(new CommonItem(score.getHome(), score.getAway()));
        }else if (score.getIndex()==7){
            score100.setRedCard(new CommonItem(score.getHome(), score.getAway()));
        }else if (score.getIndex()==11){
            score100.setCorner(new CommonItem(score.getHome(), score.getAway()));
        }else if (score.getIndex()==15){
            score100.setGoal(new CommonItem(score.getHome(), score.getAway()));
        }
        log.info("足球修改标准比分,阶段100比分:{}",score100);
        return score100;
    }

    private static FootballScores getScores110(StandardScoreDTO score,Map<Long, FootballScores> allPeriodScores) {
        FootballScores score110 = allPeriodScores.get(110L)!=null?allPeriodScores.get(110L):new FootballScores(110L);
        if(score.getIndex()==4){
            score110.setYellowCard(new CommonItem(score.getHome(), score.getAway()));
        }else if (score.getIndex()==8){
            score110.setRedCard(new CommonItem(score.getHome(), score.getAway()));
        }else if (score.getIndex()==12){
            score110.setCorner(new CommonItem(score.getHome(), score.getAway()));
        }else if (score.getIndex()==16){
            score110.setGoal(new CommonItem(score.getHome(), score.getAway()));
        }
        log.info("足球修改标准比分,阶段110比分:{}",score110);
        return score110;
    }


    private void setSwitch(StandardMatchScores standardMatchScores, String oldScores,StandardScoreCenter scores) {
        if(StrUtil.isEmpty(oldScores)){
//            return;
            Map<Long, FootballScores> periodFootballScores= new HashMap<>();
            FootballScores footballScores=new FootballScores(WHOLE_MATCH);
            periodFootballScores.put(WHOLE_MATCH,footballScores);
            //无比分时初始化比分,用于做开关自动关闭的校验
            oldScores = JSONObject.toJSONString(periodFootballScores);
        }
        //修改数据源联动开关 入库
        StandardMatchSwitchDTO matchSwitchDTO = new StandardMatchSwitchDTO();
        matchSwitchDTO.setMatchId(standardMatchScores.getMatchId());
        matchSwitchDTO.setSportId(1L);
        //获取修改后的比分
        JSONObject periodFootballScores = JSONObject.parseObject(standardMatchScores.getScoreJson());
        Map<Long,FootballScores> newScoresMap = JsonMapUtils.parseFootballMap(periodFootballScores);
        //获取修改前的比分
        JSONObject periodFootballScores2 = JSONObject.parseObject(oldScores);
        Map<Long,FootballScores> oldScoresMap = JsonMapUtils.parseFootballMap(periodFootballScores2);
        if(oldScoresMap.isEmpty()){
            oldScoresMap = new HashMap<>();
        }
        //修改前的联动开关串
        FootballSwitch accoSwitchs = JSONObject.parseObject(standardMatchScores.getDataSourceAccoSwitch(),FootballSwitch.class);
        String matchManageId = standardMatchScores.getMatchManageId();
        StandardMatchSwitchDTO switchDTO = super.setSwitchObj(standardMatchScores,scores);
        //修改前的联动开关串
        List<Long> footballScoreCenterPeriod = Arrays.asList(6L,7L,110L,50L,60899L,61799L,62699L,73599L,74499L,75399L);
        for(int i=0;i<footballScoreCenterPeriod.size();i++){
            Long period = footballScoreCenterPeriod.get(i);
            //对比修改前后比分,变更开关-修改后-前端传比分
            FootballScores scoresfor = newScoresMap.get(period);
            if(scoresfor==null){
                scoresfor = new FootballScores(period);
            }
            //对比修改前后比分,变更开关-修改前-数据库比分
            FootballScores scoresRea = oldScoresMap.get(period);
            if(scoresRea==null){
                scoresRea = new FootballScores(period);
            }
            if(!StrUtil.equals(scoresfor.getGoal().doCountScoreStr(),scoresRea.getGoal().doCountScoreStr())){
                if(period==6L){
                    switchDTO.setIndex(13);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getGoalHf(),matchManageId);
                    accoSwitchs.setGoalHf(0);
                }else if (period==7L){
                    switchDTO.setIndex(14);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getGoalFt(),matchManageId);
                    accoSwitchs.setGoalFt(0);
                }else if (period==110L){
                    switchDTO.setIndex(16);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getGoalOt(),matchManageId);
                    accoSwitchs.setGoalOt(0);
                }else if (period==50L){
                    switchDTO.setIndex(18);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getPenalty(),matchManageId);
                    //点球阶段有点球,其余阶段无点球
                    accoSwitchs.setPenalty(0);
                }else if(period==60899L){
                    switchDTO.setIndex(21);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getGoal60899(),matchManageId);
                    accoSwitchs.setGoal60899(0);
                }else if(period==61799L){
                    switchDTO.setIndex(25);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getGoal61799(),matchManageId);
                    accoSwitchs.setGoal61799(0);
                }else if(period==62699L){
                    switchDTO.setIndex(29);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getGoal62699(),matchManageId);
                    accoSwitchs.setGoal62699(0);
                }else if(period==73599L){
                    switchDTO.setIndex(33);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getGoal73599(),matchManageId);
                    accoSwitchs.setGoal73599(0);
                }else if(period==74499L){
                    switchDTO.setIndex(37);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getGoal74499(),matchManageId);
                    accoSwitchs.setGoal74499(0);
                }else if(period==75399L){
                    switchDTO.setIndex(41);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getGoal75399(),matchManageId);
                    accoSwitchs.setGoal75399(0);
                }
            }
            if(!StrUtil.equals(scoresfor.getCorner().doCountScoreStr(),scoresRea.getCorner().doCountScoreStr())){
                if(period==6L){
                    switchDTO.setIndex(9);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getCornerHf(),matchManageId);
                    accoSwitchs.setCornerHf(0);
                }else if (period==7L){
                    switchDTO.setIndex(10);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getCornerFt(),matchManageId);
                    accoSwitchs.setCornerFt(0);
                }else if (period==110L){
                    switchDTO.setIndex(12);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getCornerOt(),matchManageId);
                    accoSwitchs.setCornerOt(0);
                }else if(period==60899L){
                    switchDTO.setIndex(22);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getCorner60899(),matchManageId);
                    accoSwitchs.setCorner60899(0);
                }else if(period==61799L){
                    switchDTO.setIndex(26);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getCorner61799(),matchManageId);
                    accoSwitchs.setCorner61799(0);
                }else if(period==62699L){
                    switchDTO.setIndex(30);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getCorner62699(),matchManageId);
                    accoSwitchs.setCorner62699(0);
                }else if(period==73599L){
                    switchDTO.setIndex(34);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getCorner73599(),matchManageId);
                    accoSwitchs.setCorner73599(0);
                }else if(period==74499L){
                    switchDTO.setIndex(38);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getCorner74499(),matchManageId);
                    accoSwitchs.setCorner74499(0);
                }else if(period==75399L){
                    switchDTO.setIndex(42);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getCorner75399(),matchManageId);
                    accoSwitchs.setCorner75399(0);
                }
            }
            if(!StrUtil.equals(scoresfor.getYellowCard().doCountScoreStr(),scoresRea.getYellowCard().doCountScoreStr())){
                if(period==6L){
                    switchDTO.setIndex(1);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getYellowHf(),matchManageId);
                    accoSwitchs.setYellowHf(0);
                }else if (period==7L){
                    switchDTO.setIndex(2);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getYellowFt(),matchManageId);
                    accoSwitchs.setYellowFt(0);
                }else if (period==110L){
                    switchDTO.setIndex(4);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getYellowOt(),matchManageId);
                    accoSwitchs.setYellowOt(0);
                }else if(period==60899L){
                    switchDTO.setIndex(23);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getYellowCard60899(),matchManageId);
                    accoSwitchs.setYellowCard60899(0);
                }else if(period==61799L){
                    switchDTO.setIndex(27);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getYellowCard61799(),matchManageId);
                    accoSwitchs.setYellowCard61799(0);
                }else if(period==62699L){
                    switchDTO.setIndex(31);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getYellowCard62699(),matchManageId);
                    accoSwitchs.setYellowCard62699(0);
                }else if(period==73599L){
                    switchDTO.setIndex(35);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getYellowCard73599(),matchManageId);
                    accoSwitchs.setYellowCard73599(0);
                }else if(period==74499L){
                    switchDTO.setIndex(39);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getYellowCard74499(),matchManageId);
                    accoSwitchs.setYellowCard74499(0);
                }else if(period==75399L){
                    switchDTO.setIndex(43);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getYellowCard75399(),matchManageId);
                    accoSwitchs.setYellowCard75399(0);
                }
            }
            if(!StrUtil.equals(scoresfor.getRedCard().doCountScoreStr(),scoresRea.getRedCard().doCountScoreStr())){
                if(period==6L){
                    switchDTO.setIndex(5);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getRedHf(),matchManageId);
                    accoSwitchs.setRedHf(0);
                }else if (period==7L){
                    switchDTO.setIndex(6);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getRedFt(),matchManageId);
                    accoSwitchs.setRedFt(0);
                }else if (period==110L){
                    switchDTO.setIndex(8);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getRedOt(),matchManageId);
                    accoSwitchs.setRedOt(0);
                }else if(period==60899L){
                    switchDTO.setIndex(24);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getRedCard60899(),matchManageId);
                    accoSwitchs.setRedCard60899(0);
                }else if(period==61799L){
                    switchDTO.setIndex(28);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getRedCard61799(),matchManageId);
                    accoSwitchs.setRedCard61799(0);
                }else if(period==62699L){
                    switchDTO.setIndex(32);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getRedCard62699(),matchManageId);
                    accoSwitchs.setRedCard62699(0);
                }else if(period==73599L){
                    switchDTO.setIndex(36);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getRedCard73599(),matchManageId);
                    accoSwitchs.setRedCard73599(0);
                }else if(period==74499L){
                    switchDTO.setIndex(40);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getRedCard74499(),matchManageId);
                    accoSwitchs.setRedCard74499(0);
                }else if(period==75399L){
                    switchDTO.setIndex(44);
                    scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getRedCard75399(),matchManageId);
                    accoSwitchs.setRedCard75399(0);
                }
            }

            FootballScores wholdFor = newScoresMap.get(-1L);
            if(wholdFor==null){
                wholdFor = new FootballScores(-1L);
            }
            //对比修改前后比分,变更开关-修改前-数据库比分
            FootballScores wholdRea = oldScoresMap.get(-1L);
            if(wholdRea==null){
                wholdRea = new FootballScores(-1L);
            }
            if(!StrUtil.equals(wholdFor.getPenaltyAwarded().doCountScoreStr(),wholdRea.getPenaltyAwarded().doCountScoreStr())){
                switchDTO.setIndex(17);
                scoresCenterApiImpl.autoClose(switchDTO,accoSwitchs.getPenaltyAwarded(),matchManageId);
                accoSwitchs.setPenaltyAwarded(0);

            }
        }
        log.info("足球修改标准比分,获取修改后的开关配置 :{}",JSONUtil.toJsonStr(accoSwitchs));
        standardMatchScores.setDataSourceAccoSwitch(JSONUtil.toJsonStr(accoSwitchs));

    }

    /**
     * 修改数据源联动开关,同步比分
     * @param matchSwitchDTO
     * @param standardMatchScores
     * @param matchScoresInfo
     */
    @Override
    public Boolean editAccoSwitch(StandardMatchSwitchDTO matchSwitchDTO,StandardMatchScores standardMatchScores,MatchScoresInfo matchScoresInfo,StandardMatchInfo standardMatchInfo) {
        try{

            standardMatchScores.setDataSourceAccoSwitch(getStrByIndex(matchSwitchDTO,standardMatchScores));
            standardMatchScores.setScoreJson(getScoresAsSwitch(standardMatchScores,matchSwitchDTO,matchScoresInfo));
            //校验区间比分是否和阶段比分一致,不一致则操作拦截
            if(!checkSwitchMinScores(standardMatchScores)){
                log.info("区间比分不一致,请检查阶段比分和15分钟比分的一致性:{}",standardMatchScores.getMatchId());
                return false;
            }
            super.updateEndSendScoresInfo(standardMatchScores,standardMatchInfo);
        }catch(Exception e){
           log.error("修改数据源联动开关异常:",e);
        }
        return true;

    }

    private boolean checkSwitchMinScores(StandardMatchScores standardMatchScores) {
        String redisKey = "scores:min:check:switch:"+standardMatchScores.getMatchId();
        Object obj = redisService.get(redisKey);
        if(obj!=null && !(Boolean)obj ){
            log.info("比分编辑过滤区间比分校验：{},{}",standardMatchScores.getMatchId(),obj);
            return true ;
        }
        Map<Long, FootballScores> newStandardScores = new HashMap<>();
        if(!StrUtil.isEmpty(standardMatchScores.getScoreJson())){
            newStandardScores = JSON.parseObject(standardMatchScores.getScoreJson(), new TypeReference<Map<Long, FootballScores>>() {
            });
        }
        FootballScores scores6 = newStandardScores.get(6L);
        if(scores6!=null){
            FootballScores scores60899 = newStandardScores.get(60899L);
            FootballScores scores61799 = newStandardScores.get(61799L);
            FootballScores scores62699 = newStandardScores.get(62699L);
            if (scores60899==null){
                scores60899 = new FootballScores(60866L);
            }
            if (scores61799==null){
                scores61799 = new FootballScores(61799L);
            }
            if (scores62699==null){
                scores62699 = new FootballScores(62699L);
            }
            int homeGoal = 0, awayGoal = 0,homeCorner=0, awayCorner=0,homeYellow=0, awayYellow=0, homeRed=0, awayRed=0;
            homeGoal = scores60899.getGoal().getHome()+scores61799.getGoal().getHome()+scores62699.getGoal().getHome();
            awayGoal = scores60899.getGoal().getAway()+scores61799.getGoal().getAway()+scores62699.getGoal().getAway();
            if(homeGoal!=scores6.getGoal().getHome()||awayGoal!=scores6.getGoal().getAway()){
                log.info("区间比分不一致,请检查阶段比分和15分钟比分的一致性:{},上半场进球:{}:{},{}:{}",standardMatchScores.getMatchId(),
                        scores6.getGoal().getHome(),scores6.getGoal().getAway(),homeGoal,awayGoal);
                return false;
            }
            homeCorner = scores60899.getCorner().getHome()+scores61799.getCorner().getHome()+scores62699.getCorner().getHome();
            awayCorner = scores60899.getCorner().getAway()+scores61799.getCorner().getAway()+scores62699.getCorner().getAway();
            if(homeCorner!=scores6.getCorner().getHome()||awayCorner!=scores6.getCorner().getAway()){
                log.info("区间比分不一致,请检查阶段比分和15分钟比分的一致性:{},上半场角球:{}:{},{}:{}",standardMatchScores.getMatchId(),
                        scores6.getCorner().getHome(),scores6.getCorner().getAway(),homeCorner,awayCorner);
                return false;
            }
            homeYellow = scores60899.getYellowCard().getHome()+scores61799.getYellowCard().getHome()+scores62699.getYellowCard().getHome();
            awayYellow = scores60899.getYellowCard().getAway()+scores61799.getYellowCard().getAway()+scores62699.getYellowCard().getAway();
            if(homeYellow!=scores6.getYellowCard().getHome()||awayYellow!=scores6.getYellowCard().getAway()){
                log.info("区间比分不一致,请检查阶段比分和15分钟比分的一致性:{},上半场黄牌:{}:{},{}:{}",standardMatchScores.getMatchId(),
                        scores6.getYellowCard().getHome(),scores6.getYellowCard().getAway(),homeYellow,awayYellow);
                return false;
            }
            homeRed = scores60899.getRedCard().getHome()+scores61799.getRedCard().getHome()+scores62699.getRedCard().getHome();
            awayRed = scores60899.getRedCard().getAway()+scores61799.getRedCard().getAway()+scores62699.getRedCard().getAway();
            if(homeRed!=scores6.getRedCard().getHome()||awayRed!=scores6.getRedCard().getAway()){
                log.info("区间比分不一致,请检查阶段比分和15分钟比分的一致性:{},上半场红牌:{}:{},{}:{}",standardMatchScores.getMatchId(),
                        scores6.getRedCard().getHome(),scores6.getRedCard().getAway(),homeRed,awayRed);
                return false;
            }
        }
        FootballScores scores7 = newStandardScores.get(7L);
        if (scores7!=null){
            int homeGoal = 0, awayGoal = 0,homeCorner=0, awayCorner=0,homeYellow=0, awayYellow=0, homeRed=0, awayRed=0;
            FootballScores scores73599 = newStandardScores.get(73599L);
            FootballScores scores74499 = newStandardScores.get(74499L);
            FootballScores scores75399 = newStandardScores.get(75399L);
            if (scores73599==null){
                scores73599 = new FootballScores(73599L);
            }
            if (scores74499==null){
                scores74499 = new FootballScores(74499L);
            }
            if (scores75399==null){
                scores75399 = new FootballScores(75399L);
            }
            homeGoal = scores73599.getGoal().getHome()+scores74499.getGoal().getHome()+scores75399.getGoal().getHome();
            awayGoal = scores73599.getGoal().getAway()+scores74499.getGoal().getAway()+scores75399.getGoal().getAway();
            if(homeGoal!=scores7.getGoal().getHome()||awayGoal!=scores7.getGoal().getAway()){
                log.info("区间比分不一致,请检查阶段比分和15分钟比分的一致性:{},下半场进球:{}:{},{}:{}",standardMatchScores.getMatchId(),
                        scores7.getGoal().getHome(),scores7.getGoal().getAway(),homeGoal,awayGoal);
                return false;
            }
            homeCorner = scores73599.getCorner().getHome()+scores74499.getCorner().getHome()+scores75399.getCorner().getHome();
            awayCorner = scores73599.getCorner().getAway()+scores74499.getCorner().getAway()+scores75399.getCorner().getAway();
            if(homeCorner!=scores7.getCorner().getHome()||awayCorner!=scores7.getCorner().getAway()){
                log.info("区间比分不一致,请检查阶段比分和15分钟比分的一致性:{},下半场角球:{}:{},{}:{}",standardMatchScores.getMatchId(),
                        scores7.getCorner().getHome(),scores7.getCorner().getAway(),homeCorner,awayCorner);
                return false;
            }
            homeYellow = scores73599.getYellowCard().getHome()+scores74499.getYellowCard().getHome()+scores75399.getYellowCard().getHome();
            awayYellow = scores73599.getYellowCard().getAway()+scores74499.getYellowCard().getAway()+scores75399.getYellowCard().getAway();
            if(homeYellow!=scores7.getYellowCard().getHome()||awayYellow!=scores7.getYellowCard().getAway()){
                log.info("区间比分不一致,请检查阶段比分和15分钟比分的一致性:{},下半场黄牌:{}:{},{}:{}",standardMatchScores.getMatchId(),
                        scores7.getYellowCard().getHome(),scores7.getYellowCard().getAway(),homeYellow,awayYellow);
                return false;
            }
            homeRed = scores73599.getRedCard().getHome()+scores74499.getRedCard().getHome()+scores75399.getRedCard().getHome();
            awayRed = scores73599.getRedCard().getAway()+scores74499.getRedCard().getAway()+scores75399.getRedCard().getAway();
            if(homeRed!=scores7.getRedCard().getHome()||awayRed!=scores7.getRedCard().getAway()){
                log.info("区间比分不一致,请检查阶段比分和15分钟比分的一致性:{},下半场红牌:{}:{},{}:{}",standardMatchScores.getMatchId(),
                        scores7.getRedCard().getHome(),scores7.getRedCard().getAway(),homeRed,awayRed);
                return false;
            }
        }
        return true;
    }

    /**
     * 传过来的开关,获取下标 重新组织到开关对象里面
     * @param matchSwitchDTO
     * @param standardMatchScores
     * @return
     */
    private String getStrByIndex(StandardMatchSwitchDTO matchSwitchDTO, StandardMatchScores standardMatchScores) {
        FootballSwitch switchs = new FootballSwitch();
        if(StrUtil.isNotEmpty(standardMatchScores.getDataSourceAccoSwitch())){
            switchs = JSON.parseObject(standardMatchScores.getDataSourceAccoSwitch(), FootballSwitch.class);
        }
        log.info("修改联动开关:{}",matchSwitchDTO);
        int status = matchSwitchDTO.getStatus();
        switch (matchSwitchDTO.getIndex()){
            case 1:
                switchs.setYellowHf(status);
                break;
            case 2:
                switchs.setYellowFt(status);
                break;
            case 4:
                switchs.setYellowOt(status);
                break;
            case 5:
                switchs.setRedHf(status);
                break;
            case 6:
                switchs.setRedFt(status);
                break;
            case 8:
                switchs.setRedOt(status);
                break;
            case 9:
                switchs.setCornerHf(status);
                break;
            case 10:
                switchs.setCornerFt(status);
                break;
            case 12:
                switchs.setCornerOt(status);
                break;

            case 13:
                switchs.setGoalHf(status);
                break;
            case 14:
                switchs.setGoalFt(status);
                break;
            case 16:
                switchs.setGoalOt(status);
                break;
            case 17:
                switchs.setPenaltyAwarded(status);
                break;
            case 18:
                switchs.setPenalty(status);
                break;
            case 21:
                switchs.setGoal60899(status);
                break;
            case 22:
                switchs.setCorner60899(status);
                break;
            case 23:
                switchs.setRedCard60899(status);
                break;
            case 24:
                switchs.setYellowCard60899(status);
                break;
            case 25:
                switchs.setGoal61799(status);
                break;
            case 26:
                switchs.setCorner61799(status);
                break;
            case 27:
                switchs.setRedCard61799(status);
                break;
            case 28:
                switchs.setYellowCard61799(status);
                break;
            case 29:
                switchs.setGoal62699(status);
                break;
            case 30:
                switchs.setCorner62699(status);
                break;
            case 31:
                switchs.setRedCard62699(status);
                break;
            case 32:
                switchs.setYellowCard62699(status);
                break;
            case 33:
                switchs.setGoal73599(status);
                break;
            case 34:
                switchs.setCorner73599(status);
                break;
            case 35:
                switchs.setRedCard73599(status);
                break;
            case 36:
                switchs.setYellowCard73599(status);
                break;
            case 37:
                switchs.setGoal74499(status);
                break;
            case 38:
                switchs.setCorner74499(status);
                break;
            case 39:
                switchs.setRedCard74499(status);
                break;
            case 40:
                switchs.setYellowCard74499(status);
                break;
            case 41:
                switchs.setGoal75399(status);
                break;
            case 42:
                switchs.setCorner75399(status);
                break;
            case 43:
                switchs.setRedCard75399(status);
                break;
            case 44:
                switchs.setYellowCard75399(status);
                break;
        }
        log.info("修改联动开关:{}",switchs);
        return JSON.toJSONString(switchs);
    }

    /**
     * 调整开关后 同步对应的三方比分到标准比分
     * @param standardMatchScores
     * @param matchSwitchDTO
     * @param matchScoresInfo
     */
    private static String getScoresAsSwitch(StandardMatchScores standardMatchScores, StandardMatchSwitchDTO matchSwitchDTO, MatchScoresInfo matchScoresInfo) {
        log.info("修改开关联动同步比分 matchSwitchDTO:{}==========standardMatchScores:{} " +
                "======================matchScoresInfo:{}",matchSwitchDTO,standardMatchScores,matchScoresInfo);
        Map<Long, FootballScores> newStandardScores = new HashMap<>();
        if(!StrUtil.isEmpty(standardMatchScores.getScoreJson())){
            newStandardScores = JSON.parseObject(standardMatchScores.getScoreJson(), new TypeReference<Map<Long, FootballScores>>() {
            });
        }
        Map<Long, FootballScores> thirdMatchScores = new HashMap<>();
        if(matchScoresInfo!=null && StrUtil.isNotEmpty(matchScoresInfo.getScoresJson())){
            JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
            thirdMatchScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        }
        FootballScores wholeScores = newStandardScores.get(-1L);
        if(wholeScores==null){
            wholeScores = new FootballScores(-1L);
        }
        int index = matchSwitchDTO.getIndex();
        int status = matchSwitchDTO.getStatus();
        List<Integer> index1 = new ArrayList<>(Arrays.asList(1,5,9,13));
        List<Integer> index2 = new ArrayList<>(Arrays.asList(2,6,10,14));
        List<Integer> index4 = new ArrayList<>(Arrays.asList(4,8,12,16));
        List<Integer> index_60899 = new ArrayList<>(Arrays.asList(21,22,23,24));
        List<Integer> index_61799 = new ArrayList<>(Arrays.asList(25,26,27,28));
        List<Integer> index_62699 = new ArrayList<>(Arrays.asList(29,30,31,32));
        List<Integer> index_73599 = new ArrayList<>(Arrays.asList(33,34,35,36));
        List<Integer> index_74499 = new ArrayList<>(Arrays.asList(37,38,39,40));
        List<Integer> index_75399 = new ArrayList<>(Arrays.asList(41,42,43,44));

        log.info("修改开关联动同步比分:index:{}",index);
        if(index1.contains(index)){
            FootballScores thirdScores = thirdMatchScores.get(6L);
            log.info("修改开关联动同步比分:thirdScores:{}",thirdScores);
            if(thirdScores==null){
                return standardMatchScores.getScoreJson();
            }
            FootballScores standScores = newStandardScores.get(6L);
            if(standScores==null){
                standScores = new FootballScores(6L);
            }
            if(index == 1 && status == 1){
                standScores.setYellowCard(thirdScores.getYellowCard());
            }
            if(index == 5 && status == 1){
                standScores.setRedCard(thirdScores.getRedCard());
            }
            if(index == 9 && status == 1){
                standScores.setCorner(thirdScores.getCorner());
            }
            if(index == 13 && status == 1){
                standScores.setGoal(thirdScores.getGoal());
            }
            newStandardScores.put(6L,standScores);

        }else if(index2.contains(index)){
            FootballScores thirdScores = thirdMatchScores.get(7L);
            log.info("修改开关联动同步比分:thirdScores:{}",thirdScores);
            if(thirdScores==null){
                return standardMatchScores.getScoreJson();
            }
            FootballScores standScores = newStandardScores.get(7L);
            if(standScores==null){
                standScores = new FootballScores(7L);
            }
            if(index == 2 && status == 1){
                standScores.setYellowCard(thirdScores.getYellowCard());
            }
            if(index == 6 && status == 1){
                standScores.setRedCard(thirdScores.getRedCard());
            }
            if(index == 10 && status == 1){
                standScores.setCorner(thirdScores.getCorner());
            }
            if(index == 14 && status == 1){
                standScores.setGoal(thirdScores.getGoal());
            }
            newStandardScores.put(7L,standScores);
        }else if(index4.contains(index)){
            FootballScores standScores = newStandardScores.get(110L);
            if(standScores==null){
                standScores = new FootballScores(110L);
            }

            FootballScores thirdScores = thirdMatchScores.get(110L);
            log.info("修改开关联动同步比分:thirdScores:{}",thirdScores);
            Integer yellowHome = 0,yellowAway = 0;
            Integer redHome = 0,redAway = 0;
            Integer cornerHome = 0,cornerAway = 0;
            Integer goalHome = 0,goalAway = 0;
            if(thirdScores==null){
                FootballScores thirdScores41 = thirdMatchScores.get(41L);
                FootballScores thirdScores42 = thirdMatchScores.get(42L);
                if(thirdScores41!=null){
                    yellowHome+=thirdScores41.getYellowCard().getHome();
                    yellowAway+=thirdScores41.getYellowCard().getAway();
                    redHome+=thirdScores41.getRedCard().getHome();
                    redAway+=thirdScores41.getRedCard().getAway();
                    cornerHome+=thirdScores41.getCorner().getHome();
                    cornerAway+=thirdScores41.getCorner().getAway();
                    goalHome+=thirdScores41.getGoal().getHome();
                    goalAway+=thirdScores41.getGoal().getAway();
                }
                if(thirdScores42!=null){
                    yellowHome+=thirdScores42.getYellowCard().getHome();
                    yellowAway+=thirdScores42.getYellowCard().getAway();
                    redHome+=thirdScores42.getRedCard().getHome();
                    redAway+=thirdScores42.getRedCard().getAway();
                    cornerHome+=thirdScores42.getCorner().getHome();
                    cornerAway+=thirdScores42.getCorner().getAway();
                    goalHome+=thirdScores42.getGoal().getHome();
                    goalAway+=thirdScores42.getGoal().getAway();
                }
                if(index == 4 && status == 1){
                    standScores.setYellowCard(new CommonItem(yellowHome,yellowAway));
                }
                if(index == 8 && status == 1){
                    standScores.setRedCard(new CommonItem(redHome,redAway));
                }
                if(index == 12 && status == 1){
                    standScores.setCorner(new CommonItem(cornerHome,cornerAway));
                }
                if(index == 16 && status == 1){
                    standScores.setGoal(new CommonItem(goalHome,goalAway));
                }
            }else{
                if(index == 4 && status == 1){
                    standScores.setYellowCard(thirdScores.getYellowCard());
                }
                if(index == 8 && status == 1){
                    standScores.setRedCard(thirdScores.getRedCard());
                }
                if(index == 12 && status == 1){
                    standScores.setCorner(thirdScores.getCorner());
                }
                if(index == 16 && status == 1){
                    standScores.setGoal(thirdScores.getGoal());
                }
            }
            newStandardScores.put(110L,standScores);
        }else if(index == 17 && status == 1){
            FootballScores thirdScores = thirdMatchScores.get(-1L);
            log.info("修改开关联动同步比分:thirdScores:{}",thirdScores);
            if(thirdScores==null){
                return standardMatchScores.getScoreJson();
            }
            wholeScores.setPenaltyAwarded(thirdScores.getPenaltyAwarded());
        }else if(index == 18 && status == 1){
            FootballScores thirdScores = thirdMatchScores.get(50L);
            log.info("修改开关联动同步比分:thirdScores:{}",thirdScores);
            if(thirdScores==null){
                return standardMatchScores.getScoreJson();
            }
            FootballScores standScores = newStandardScores.get(50L);
            newStandardScores.put(50L,standScores);
        }else if(index_60899.contains(index)){
            FootballScores thirdScores = thirdMatchScores.get(60899L);
            log.info("修改开关联动同步比分:thirdScores:{}",60899L);
            if(thirdScores==null){
                return standardMatchScores.getScoreJson();
            }
            FootballScores footballMinScores1 = newStandardScores.get(60899L);
            if(footballMinScores1==null){
                footballMinScores1 = new FootballScores(60899L);
            }
            if(index == 21 && status == 1){
                footballMinScores1.setGoal(thirdMatchScores.get(60899L).getGoal());
            }else if(index == 22 && status == 1){
                footballMinScores1.setCorner(thirdMatchScores.get(60899L).getCorner());
            }else if(index == 23 && status == 1){
                footballMinScores1.setRedCard(thirdMatchScores.get(60899L).getRedCard());
            }else if(index == 24 && status == 1){
                footballMinScores1.setYellowCard(thirdMatchScores.get(60899L).getYellowCard());
            }
            newStandardScores.put(60899L,footballMinScores1);
        }else if(index_61799.contains(index)){
            log.info("修改开关联动同步比分:thirdScores:{}",61799L);
            FootballScores thirdScores = thirdMatchScores.get(61799L);
            if(thirdScores==null){
                return standardMatchScores.getScoreJson();
            }
            FootballScores footballMinScores1 = newStandardScores.get(61799L);
            if(footballMinScores1==null){
                footballMinScores1 = new FootballScores(61799L);
            }
            if(index == 25 && status == 1){
                footballMinScores1.setGoal(thirdMatchScores.get(61799L).getGoal());
            }else if(index == 26 && status == 1){
                footballMinScores1.setCorner(thirdMatchScores.get(61799L).getCorner());
            }else if(index == 27 && status == 1){
                footballMinScores1.setRedCard(thirdMatchScores.get(61799L).getRedCard());
            }else if(index == 28 && status == 1){
                footballMinScores1.setYellowCard(thirdMatchScores.get(61799L).getYellowCard());
            }
            newStandardScores.put(61799L,footballMinScores1);
        }else if(index_62699.contains(index)){
            log.info("修改开关联动同步比分:thirdScores:{}",62699L);
            FootballScores thirdScores = thirdMatchScores.get(62699L);
            if(thirdScores==null){
                return standardMatchScores.getScoreJson();
            }
            FootballScores footballMinScores1 = newStandardScores.get(62699L);
            if(footballMinScores1==null){
                footballMinScores1 = new FootballScores(62699L);
            }
            if(index == 29 && status == 1){
                footballMinScores1.setGoal(thirdMatchScores.get(62699L).getGoal());
            }else if(index == 30 && status == 1){
                footballMinScores1.setCorner(thirdMatchScores.get(62699L).getCorner());
            }else if(index == 31 && status == 1){
                footballMinScores1.setRedCard(thirdMatchScores.get(62699L).getRedCard());
            }else if(index == 32 && status == 1){
                footballMinScores1.setYellowCard(thirdMatchScores.get(62699L).getYellowCard());
            }
            newStandardScores.put(62699L,footballMinScores1);
        }else if(index_73599.contains(index)){
            FootballScores thirdScores = thirdMatchScores.get(73599L);
            if(thirdScores==null){
                return standardMatchScores.getScoreJson();
            }
            FootballScores footballMinScores1 = newStandardScores.get(73599L);
            if(footballMinScores1==null){
                footballMinScores1 = new FootballScores(73599L);
            }
            if(index == 33 && status == 1){
                footballMinScores1.setGoal(thirdMatchScores.get(73599L).getGoal());
            }else if(index == 34 && status == 1){
                footballMinScores1.setCorner(thirdMatchScores.get(73599L).getCorner());
            }else if(index == 35 && status == 1){
                footballMinScores1.setRedCard(thirdMatchScores.get(73599L).getRedCard());
            }else if(index == 36 && status == 1){
                footballMinScores1.setYellowCard(thirdMatchScores.get(73599L).getYellowCard());
            }
            newStandardScores.put(73599L,footballMinScores1);
        }else if(index_74499.contains(index)){
            FootballScores thirdScores = thirdMatchScores.get(74499L);
            if(thirdScores==null){
                return standardMatchScores.getScoreJson();
            }
            FootballScores footballMinScores1 = newStandardScores.get(74499L);
            if(footballMinScores1==null){
                footballMinScores1 = new FootballScores(74499L);
            }
            if(index == 37 && status == 1){
                footballMinScores1.setGoal(thirdMatchScores.get(74499L).getGoal());
            }else if(index == 38 && status == 1){
                footballMinScores1.setCorner(thirdMatchScores.get(74499L).getCorner());
            }else if(index == 39 && status == 1){
                footballMinScores1.setRedCard(thirdMatchScores.get(74499L).getRedCard());
            }else if(index == 40 && status == 1){
                footballMinScores1.setYellowCard(thirdMatchScores.get(74499L).getYellowCard());
            }
            newStandardScores.put(74499L,footballMinScores1);
        }else if(index_75399.contains(index)){
            FootballScores thirdScores = thirdMatchScores.get(75399L);
            if(thirdScores==null){
                return standardMatchScores.getScoreJson();
            }
            FootballScores footballMinScores1 = newStandardScores.get(75399L);
            if(footballMinScores1==null){
                footballMinScores1 = new FootballScores(75399L);
            }
            if(index == 37 && status == 1){
                footballMinScores1.setGoal(thirdMatchScores.get(75399L).getGoal());
            }else if(index == 38 && status == 1){
                footballMinScores1.setCorner(thirdMatchScores.get(75399L).getCorner());
            }else if(index == 39 && status == 1){
                footballMinScores1.setRedCard(thirdMatchScores.get(75399L).getRedCard());
            }else if(index == 40 && status == 1){
                footballMinScores1.setYellowCard(thirdMatchScores.get(75399L).getYellowCard());
            }
            newStandardScores.put(75399L,footballMinScores1);
        }


//        calcWholdScores(newStandardScores,wholeScores);

        Integer homeGoal = 0,awayGoal = 0;
        Integer homeCorner = 0,awayCorner = 0;
        Integer homeYellowCard = 0, awayYellowCard = 0;
        Integer homeRedCard = 0, awayRedCard = 0;
        for(Map.Entry<Long, FootballScores> entry : newStandardScores.entrySet()){
            if(entry.getKey()==6 || entry.getKey()==7 || entry.getKey()==110 ){
                homeGoal +=entry.getValue().getGoal().getHome();
                awayGoal +=entry.getValue().getGoal().getAway();
                homeCorner+=entry.getValue().getCorner().getHome();
                awayCorner+=entry.getValue().getCorner().getAway();
                homeYellowCard+=entry.getValue().getYellowCard().getHome();
                awayYellowCard+=entry.getValue().getYellowCard().getAway();
                homeRedCard+=entry.getValue().getRedCard().getHome();
                awayRedCard+=entry.getValue().getRedCard().getAway();
            }
            //计算每个阶段的罚牌比分
            entry.getValue().countFaCard();
        }
        wholeScores.setGoal(new CommonItem(homeGoal,awayGoal));
        wholeScores.setCorner(new CommonItem(homeCorner,awayCorner));
        wholeScores.setYellowCard(new CommonItem(homeYellowCard,awayYellowCard));
        wholeScores.setRedCard(new CommonItem(homeRedCard,awayRedCard));
        newStandardScores.put(-1L,wholeScores);
        log.info("修改开关联动同步比分 newStandardScores:{}",newStandardScores);
        return JSON.toJSONString(newStandardScores);
    }

}
