package com.panda.merge.mq.consumer;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.calculation.impl.FootballCalculationServiceImpl;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.SourceTypeEnum;
import com.panda.merge.dto.BasketballScores;
import com.panda.merge.dto.FootballScores;
import com.panda.merge.dto.Request;
import com.panda.merge.mapper.MatchScoresInfoMapper;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.model.*;
import com.panda.merge.mq.producer.CommonProducer;
import com.panda.merge.mq.producer.ScoresProducer;
import com.panda.merge.service.IScoresService;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

/**
 * 同步结算比分到比分中心（结算2.0手动操作下发比分）
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "MATCH_SETTLE_SCORES",
        consumerGroup = "scores-group2-MATCH_SETTLE_SCORES-NEW",
        consumeThreadMax = 20,
        consumeTimeout = 10000L
)
@DependsOn("scoresAdminApplication")
public class MatchEndSettleScoresConsumer implements RocketMQListener<Request<MatchSettleScore>> {

    @Autowired
    MatchScoresInfoMapper matchScoresInfoMapper;

    @Autowired
    ThirdMatchInfoMapper thirdMatchInfoMapper;

    @Autowired
    ScoresProducer scoresProducer;

    @Autowired
    IScoresService scoresService;

    @Autowired
    RedisService redisService;

    @Autowired
    FootballCalculationServiceImpl footballCalculationService;
    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;
    //结算同步比分中心开关key
    private static final String SETTLE_SYNC_SCORES_KEY = "settle_sync_scores";

    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;
    @Autowired
    CommonProducer commonProducer;

    @Override
    public void onMessage(Request<MatchSettleScore> request) {
        log.info("MatchEndSettleScoresConsumer MQ消费数据开始...{}",datacenterMergeSwitch);
        if (datacenterMergeSwitch && commonProducer.getDatacenterMatchIds(request.getData().getStandardMatchId().toString())) {
            //MQ消息转发给数据中心
            commonProducer.asyncSend(request, "datacenter-MATCH_SETTLE_SCORES",request.getLinkId());
            return;
        }
        String linkId = request.getLinkId();
        Long start = System.currentTimeMillis();
        log.info("{} MatchSettleScoresConsumer事件比分中心处理开始：{}", linkId, start);
        if (request == null || request.getData() == null) {
            return;
        }

        //1、过滤不符合的消息
        if (!check(request.getData())) {
            return;
        }
        StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(request.getData().getStandardMatchId());
        if(null == standardMatchInfo || standardMatchInfo.getMatchStatus()==null||standardMatchInfo.getMatchStatus()!=3){
            return;
        }
        //2、处理同步比分中心的逻辑
        String key = "MATCH_SETTLE_SYNC_SCORES:" + request.getData().getStandardMatchId();
        try {
            if (redisService.tryLock(key, linkId, 3, 2)) {
                if(request.getData().getSportId().equals(1L)) {
                    handleData(request);
                }else if(request.getData().getSportId().equals(2L)){
                    handleBasketBallData(request);
                }
            }
        }catch (Exception e){
            log.error("error data:{}",request.getData(),e);

        }finally {
            redisService.unLock(key, linkId);
        }


        log.info("{} MatchSettleScoresConsumer事件比分中心处理结束耗时：{}", linkId, System.currentTimeMillis()-start);
    }

    private void handleBasketBallData(Request<MatchSettleScore> request) {
        MatchSettleScore matchSettleScore = request.getData();
        //获取三方赛事id
        ThirdMatchInfoExample thirdMatchInfoExample = new ThirdMatchInfoExample();
        thirdMatchInfoExample.createCriteria().andReferenceIdEqualTo(matchSettleScore.getStandardMatchId());
        List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoMapper.selectByExample(thirdMatchInfoExample);
        if(CollectionUtil.isEmpty(thirdMatchInfos)){
            log.info("3--------{}",matchSettleScore);
            return;
        }

        //当前阶段  根据settleNum找到对应的period
        Long periodBySettleNum = getBasketBallPeriodBySettleNum(matchSettleScore.getSettleNum());
        if(periodBySettleNum == null){
            log.info("4--------{}",matchSettleScore);
            return;
        }

        //比分中心下多个数据商
        for(ThirdMatchInfo thirdMatchInfo : thirdMatchInfos){
            MatchScoresInfoExample scoresInfoExample = new MatchScoresInfoExample();
            scoresInfoExample.createCriteria().andThirdMatchIdEqualTo(thirdMatchInfo.getId());
            List<MatchScoresInfo> matchScoresInfos = matchScoresInfoMapper.selectByExample(scoresInfoExample);
            if(CollectionUtil.isEmpty(matchScoresInfos)){
                continue;
            }
            try{
                for(MatchScoresInfo matchScoresInfo : matchScoresInfos) {
                    if(StringUtils.isBlank(matchScoresInfo.getScoresJson())){
                        continue;
                    }
                    JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
                    Map<Long, BasketballScores> allPeriodScores = JsonMapUtils.parseBasketballMap(periodFootballScores);
                    BasketballScores wholeSores = allPeriodScores.get(WHOLE_MATCH);

                    BasketballScores periodScores = allPeriodScores.get(periodBySettleNum);
                    if (periodScores == null) {
                        continue;
                    }
                    periodScores.getMatchScore().setHome(matchSettleScore.getT1());
                    periodScores.getMatchScore().setAway(matchSettleScore.getT2());
                    //修改具体分值

                    //上下半场和加时赛上下半场需要同步全场比分
                    matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
                    matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
                    log.info("{}组装json：{}",matchScoresInfo.getId(),JSONObject.toJSONString(allPeriodScores));
                    matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
                    matchScoresInfo.setModifyTime(System.currentTimeMillis());
                    matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
                    //mq通知下游
                    scoresProducer.sendToMQ(thirdMatchInfo, matchScoresInfo, request.getLinkId());
                }
                //修改比分而且下发
            }catch (Exception e){

            }
        }
    }

    private Long getBasketBallPeriodBySettleNum(String settleNum) {
        //第一节 第二节 第三节 第四节比分 上半场比分 常规赛结束比分  全场结束比分(含加时)
//        map.put("bk_q104","S19"); 第一节
//        map.put("bk_q204","S20"); 第二节
//        map.put("bk_q304","S21"); 第三节
//        map.put("bk_q404","S22"); 第四节比分
//        map.put("bk_1ht","S2"); 上半场比分
//        map.put("bk_ft_et","S1"); 常规赛结束比分
//        map.put("bk_ft_rg","S1111"); 全场结束比分(含加时)
//        BK_1HT("bk_1ht", "上半场", "1st Half"),
//                BK_2HT("bk_2ht", "下半场(包含加时)", "2nd Half (included Overtime)"),
//                BK_ET("bk_et", "加时赛", "Extra Time"),
//                BK_FT_ET("bk_ft_et", "全场 (含加时)", "Full Time (included Overtime)"),
//                BK_FT_RG("bk_ft_rg", "全场 (常规时间)", "Full Time (Regular)"),
        if(settleNum.equals("bk_q104")){
            return 13L;
        }else if(settleNum.equals("bk_q204")){
            return 14L;
        }else if(settleNum.equals("bk_q304")){
            return 15L;
        }else if(settleNum.equals("bk_q404")){
            return 16L;
        }else if(settleNum.equals("bk_1ht")){
            return 1L;
        }else if(settleNum.equals("bk_2ht")){
            return 2L;
        }else if(settleNum.equals("bk_et")){
            return 40L;
        }else if(settleNum.equals("bk_ft_rg")){
            return -1L;
        }else if(settleNum.equals("bk_ft_et")){
            return -1L;
        }else {
            return null;
        }
    }

    /**
     * 检查mq消息是否合法X
     * @param data
     * @return
     */
    private boolean check(MatchSettleScore data){
        //非已结算不对接
        if (!data.getStatus().equals(3)) {
            log.info("2--------{}",data);
            return false;
        }
        return true;
    }


    /**
     * 处理同步比分中心的逻辑
     * @param request
     */
    private void handleData(Request<MatchSettleScore> request) {
        MatchSettleScore matchSettleScore = request.getData();
        //获取三方赛事id
        ThirdMatchInfoExample thirdMatchInfoExample = new ThirdMatchInfoExample();
        thirdMatchInfoExample.createCriteria().andReferenceIdEqualTo(matchSettleScore.getStandardMatchId());
        List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoMapper.selectByExample(thirdMatchInfoExample);
        if(CollectionUtil.isEmpty(thirdMatchInfos)){
            log.info("3--------{}",matchSettleScore);
            return;
        }

        //当前阶段  根据settleNum找到对应的period
        Long periodBySettleNum = getPeriodBySettleNum(matchSettleScore.getSettleNum());
        if(periodBySettleNum == null){
            log.info("4--------{}",matchSettleScore);
            return;
        }

        //比分中心下多个数据商
        for(ThirdMatchInfo thirdMatchInfo : thirdMatchInfos){
            MatchScoresInfoExample scoresInfoExample = new MatchScoresInfoExample();
            scoresInfoExample.createCriteria().andThirdMatchIdEqualTo(thirdMatchInfo.getId())
                    .andDataSourceTypeEqualTo(SourceTypeEnum.LIVE_DATA.getCode().toString());
            List<MatchScoresInfo> matchScoresInfos = matchScoresInfoMapper.selectByExample(scoresInfoExample);
            if(CollectionUtil.isEmpty(matchScoresInfos)){
                continue;
            }

            for(MatchScoresInfo matchScoresInfo : matchScoresInfos) {
                if(StringUtils.isBlank(matchScoresInfo.getScoresJson())){
                    continue;
                }
                JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
                Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
                FootballScores wholeSores = allPeriodScores.get(WHOLE_MATCH);

                FootballScores periodScores = allPeriodScores.get(periodBySettleNum);
                if (periodScores == null) {
                    continue;
                }

                //修改具体分值
                updatePeriodScores(periodScores, matchSettleScore, null);
                //上下半场和加时赛上下半场需要同步全场比分
                if (ArrayUtil.contains(new long[]{6L, 7L, 41L, 42L}, periodBySettleNum)) {
                    updatePeriodScores(wholeSores, matchSettleScore, allPeriodScores);
                }

                matchScoresInfo.setT1(wholeSores.getGoal().getHome());
                matchScoresInfo.setT2(wholeSores.getGoal().getAway());
                matchScoresInfo.setPeriodT1(periodScores.getGoal().getHome());
                matchScoresInfo.setPeriodT2(periodScores.getGoal().getAway());
                log.info("{}组装json：{}",matchScoresInfo.getId(),JSONObject.toJSONString(allPeriodScores));
                matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
                matchScoresInfo.setModifyTime(System.currentTimeMillis());
                matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);

                //mq通知下游
                scoresProducer.sendToMQ(thirdMatchInfo, matchScoresInfo, request.getLinkId());
            }
        }
    }

    /**
     * 根据阶段编码获取赛事阶段
     * @param settleNum
     * @return
     */
    private Long getPeriodBySettleNum(String settleNum){
        //上半场
        List<String> group1 = Arrays.asList("101", "105", "201","304");
        //下半场
        List<String> group2 = Arrays.asList("109", "202","308");
        //加时上半场
        List<String> group10 = Arrays.asList("1014","206","3013");
        //加时下半场
        List<String> group11 = Arrays.asList("1018","207","3017");
        //0-15 （15分钟无罚牌）
        List<String> group4 = Arrays.asList("102","2011");
        //15-30 （15分钟无罚牌）
        List<String> group5 = Arrays.asList("103","2012");
        //30-45 （15分钟无罚牌）
        List<String> group6 = Arrays.asList("104","2013");
        //45-60 （15分钟无罚牌）
        List<String> group7 = Arrays.asList("106","2014");
        //60-75 （15分钟无罚牌）
        List<String> group8 = Arrays.asList("107","2015");
        //75-90 （15分钟无罚牌）
        List<String> group9 = Arrays.asList("108","2016");
        if(group1.contains(settleNum)){
            return 6L;
        }else if(group2.contains(settleNum)){
            return 7L;
        } else if(group4.contains(settleNum)){
            return 60899L;
        }else if(group5.contains(settleNum)){
            return 61799L;
        }else if(group6.contains(settleNum)){
            return 62699L;
        }else if(group7.contains(settleNum)){
            return 73599L;
        }else if(group8.contains(settleNum)){
            return 74499L;
        }else if(group9.contains(settleNum)){
            return 75399L;
        }else if(group10.contains(settleNum)){
            return 41L;
        }else if(group11.contains(settleNum)){
            return 42L;
        }else if("1034".equals(settleNum)){
            return 6005L;
        }else if("1035".equals(settleNum)){
            return 6010L;
        }else if("1036".equals(settleNum)){
            return 6015L;
        }else if("1037".equals(settleNum)){
            return 6020L;
        }else if("1038".equals(settleNum)){
            return 6025L;
        }else if("1039".equals(settleNum)){
            return 6030L;
        }else if("1040".equals(settleNum)){
            return 6035L;
        }else if("1041".equals(settleNum)){
            return 6040L;
        }else if("1042".equals(settleNum)){
            return 6045L;
        }else if("1043".equals(settleNum)){
            return 6050L;
        }else if("1044".equals(settleNum)){
            return 7050L;
        }else if("1045".equals(settleNum)){
            return 7055L;
        }else if("1046".equals(settleNum)){
            return 7060L;
        }else if("1047".equals(settleNum)){
            return 7065L;
        }else if("1048".equals(settleNum)){
            return 7070L;
        }else if("1049".equals(settleNum)){
            return 7075L;
        }else if("1050".equals(settleNum)){
            return 7080L;
        }else if("1051".equals(settleNum)){
            return 7085L;
        }else if("1052".equals(settleNum)){
            return 7090L;
        }else if("1053".equals(settleNum)){
            return 7095L;
        }else {
            return null;
        }
    }

    /**
     * 根据阶段编码更新分值
     * @param periodScores
     * @param matchSettleScore
     */
    private void updatePeriodScores(FootballScores periodScores,MatchSettleScore matchSettleScore,Map<Long, FootballScores> allPeriodScores){

        Integer faCardHome = 0;
        Integer faCardAway = 0;
        Integer goalHome = 0;
        Integer goalAway = 0;
        Integer cornerHome = 0;
        Integer cornerAway = 0;

        //需要修改全场比分
        if(allPeriodScores != null) {
            FootballScores footballScores_6L = allPeriodScores.get(6L);
            FootballScores footballScores_7L = allPeriodScores.get(7L);
            FootballScores footballScores_41L = allPeriodScores.get(41L);
            FootballScores footballScores_42L = allPeriodScores.get(42L);
            if (footballScores_6L != null) {
                faCardHome += footballScores_6L.getFaCard().getHome();
                faCardAway += footballScores_6L.getFaCard().getAway();
                goalHome += footballScores_6L.getGoal().getHome();
                goalAway += footballScores_6L.getGoal().getAway();
                cornerHome += footballScores_6L.getCorner().getHome();
                cornerAway += footballScores_6L.getCorner().getAway();
            }
            if (footballScores_7L != null) {
                faCardHome += footballScores_7L.getFaCard().getHome();
                faCardAway += footballScores_7L.getFaCard().getAway();
                goalHome += footballScores_7L.getGoal().getHome();
                goalAway += footballScores_7L.getGoal().getAway();
                cornerHome += footballScores_7L.getCorner().getHome();
                cornerAway += footballScores_7L.getCorner().getAway();
            }
            if (footballScores_41L != null) {
                faCardHome += footballScores_41L.getFaCard().getHome();
                faCardAway += footballScores_41L.getFaCard().getAway();
                goalHome += footballScores_41L.getGoal().getHome();
                goalAway += footballScores_41L.getGoal().getAway();
                cornerHome += footballScores_41L.getCorner().getHome();
                cornerAway += footballScores_41L.getCorner().getAway();
            }
            if (footballScores_42L != null) {
                faCardHome += footballScores_42L.getFaCard().getHome();
                faCardAway += footballScores_42L.getFaCard().getAway();
                goalHome += footballScores_42L.getGoal().getHome();
                goalAway += footballScores_42L.getGoal().getAway();
                cornerHome += footballScores_42L.getCorner().getHome();
                cornerAway += footballScores_42L.getCorner().getAway();
            }
        }

        String settleNum = matchSettleScore.getSettleNum();
        Integer t1 = matchSettleScore.getT1();
        Integer t2 = matchSettleScore.getT2();
        List<String> cornerGroup = Arrays.asList("201", "202","206","207","2011","2012","2013","2014","2015","2016");
        List<String> faCardGroup = Arrays.asList("304","308","3013","3017");
        List<String> goalGroup = Arrays.asList("102","103","104","105","106","107","108","109","1014","1018","1034","1035","1036","1037","1038","1039","1040","1041","1042","1043","1044","1045","1046","1047","1048","1049","1050","1051","1052","1053");
        List<String> kickOffGroup = Arrays.asList("101");
        if(cornerGroup.contains(settleNum)){
            if(allPeriodScores != null) {
                periodScores.getCorner().setHome(cornerHome);
                periodScores.getCorner().setAway(cornerAway);
            }else {
                periodScores.getCorner().setHome(t1);
                periodScores.getCorner().setAway(t2);
            }
        }else if(faCardGroup.contains(settleNum)){
            if(allPeriodScores != null) {
                periodScores.getFaCard().setHome(faCardHome);
                periodScores.getFaCard().setAway(faCardAway);
            }else {
                periodScores.getFaCard().setHome(t1);
                periodScores.getFaCard().setAway(t2);
            }
        }else if(goalGroup.contains(settleNum)){
            if(allPeriodScores != null) {
                periodScores.getGoal().setHome(goalHome);
                periodScores.getGoal().setAway(goalAway);
            }else{
                periodScores.getGoal().setHome(t1);
                periodScores.getGoal().setAway(t2);
            }
        }else if(kickOffGroup.contains(settleNum)){
            periodScores.getKickOff().setHome(t1);
            periodScores.getKickOff().setAway(t2);
        }

    }
}
