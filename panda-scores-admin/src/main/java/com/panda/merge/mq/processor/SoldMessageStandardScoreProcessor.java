package com.panda.merge.mq.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.SellStatusEnum;
import com.panda.merge.constant.SourceTypeEnum;
import com.panda.merge.constant.SportTypeEnum;
import com.panda.merge.dto.CommonItem;
import com.panda.merge.dto.FootballScores;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.SoldMessage;
import com.panda.merge.dto.scores.MatchScoresBetterDto;
import com.panda.merge.dto.sourceSwitch.FootballSwitch;
import com.panda.merge.mapper.StandardSportMarketSellMapper;
import com.panda.merge.model.*;
import com.panda.merge.mq.message.CommonStandardScoresDto;
import com.panda.merge.mq.producer.ScoresProducer;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.repository.ScoresRedisHelp;
import com.panda.merge.repository.StandardMatchInfoRepository;
import com.panda.merge.repository.ThirdMatchInfoRepository;
import com.panda.merge.service.StandardSportMarketSellService;
import com.panda.merge.utils.MessageBuilderUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.panda.merge.constant.ConstantSystem.SOLD_MESSAGE;
import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

/**
 * @author warren
 * @since 2025/01/03 16:29:52
 */
@Slf4j
@Validated
@Component
public class SoldMessageStandardScoreProcessor {
    @Autowired
    private StandardSportMarketSellMapper standardSportMarketSellMapper;

    @Autowired
    private MessageBuilderUtils messageBuilderUtils;

    @Autowired
    private ScoresProducer scoresProducer;

    @Autowired
    private ThirdMatchInfoRepository thirdMatchInfoRepository;

    @Autowired
    private MatchScoreInfoRepository matchScoreInfoRepository;

    @Autowired
    private StandardMatchInfoRepository standardMatchInfoRepository;
    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    ScoresRedisHelp scoresRedisHelp;

//    @Async("SoldMessageStandardScoreThreadPool")
    public void execute(Request<SoldMessage> soldMessageRequest) {
        SoldMessage soldMessage = soldMessageRequest.getData();
        String linkId = soldMessageRequest.getLinkId();
//        log.info("::{}::比分中心:soldMessage比分下发，逻辑处理开始，request={}", linkId, JSON.toJSONString(soldMessageRequest));
        Long matchId = soldMessage.getMatchId();
        log.info("::{}::比分中心-赛事开售补发比分,标准赛事id:{}", soldMessageRequest.getLinkId(), linkId);
//        StandardSportMarketSellExample example = new StandardSportMarketSellExample();
//        example.createCriteria().andMatchInfoIdEqualTo(matchId);
//        example.setOrderByClause("id desc limit 1");
//        List<StandardSportMarketSell> standardSportMarketSellList = standardSportMarketSellMapper.selectByExample(example);
//        if (CollectionUtils.isEmpty(standardSportMarketSellList)) {
//            log.info("::{}::比分中心-开售赛事不存在,标准赛事id:{}", soldMessageRequest.getLinkId(), matchId);
//            return;
//        }
//        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellList.get(0);

        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(matchId);
        if (standardSportMarketSell==null) {
            log.info("::{}::比分中心-开售赛事不存在,标准赛事id:{}", soldMessageRequest.getLinkId(), matchId);
            return;
        }
        StandardMatchInfo standardMatchInfo = standardMatchInfoRepository.selectStandardMatchPrimaryKey(matchId);
        if (standardMatchInfo.getMatchPeriodId() != null && standardMatchInfo.getMatchPeriodId() == 0L) {
            log.info("::{}::比分中心-赛事未开赛-阶段为0,标准赛事id:{}", soldMessageRequest.getLinkId(), matchId);
            return;
        }
        if (SellStatusEnum.UNSOLD.getValue().equals(standardSportMarketSell.getLiveMatchSellStatus()) && SellStatusEnum.UNSOLD.getValue().equals(standardSportMarketSell.getPreMatchSellStatus())) {
            log.info("::{}::比分中心-赛事未开售,标准赛事id:{}", soldMessageRequest.getLinkId(), matchId);
            return;
        }
        String businessEvent = standardSportMarketSell.getBusinessEvent();
        Long sportId = standardSportMarketSell.getSportId();

        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoRepository.selectByStandardIdAndDataSourceCode(matchId, businessEvent);
        if (thirdMatchInfo == null) {
            log.info("::{}::比分中心-开售:三方赛事不存在,标准赛事id:{}", soldMessageRequest.getLinkId(), matchId);
            return;
        }
        Integer dataSourceType = SourceTypeEnum.LIVE_DATA.getCode();
        if (DataSourceCodeEnum.BC.code.equals(soldMessageRequest.getDataSourceCode())) {
            Long sourceType = matchScoreInfoRepository.checkB02ScoresSource(sportId);
            dataSourceType = sourceType == 1 ? 0 : 1;
        }
//        log.info("::{}::比分中心:thirdMatchInfo-soldMessage比分下发，逻辑处理开始，request={}", linkId, JSON.toJSONString(thirdMatchInfo));
        MatchScoresInfo matchScoresInfo = matchScoreInfoRepository.selectByExample(thirdMatchInfo.getId(), dataSourceType);
        if(matchScoresInfo==null || matchScoresInfo.getScoresJson()==null){
            log.info("::{}::比分中心-开售:比分不存在,标准赛事id:{}", soldMessageRequest.getLinkId(), matchId);
            return;
        }
//        log.info("::{}::比分中心:matchScoresInfo-soldMessage比分下发，逻辑处理开始，request={}", linkId, JSON.toJSONString(matchScoresInfo));
        // 数据组装
        CommonStandardScoresDto commonScoresDto = messageBuilderUtils.buildCommonScoresDto(thirdMatchInfo, matchScoresInfo);
//        log.info("::{}::比分中心:commonScoresDto-soldMessage比分下发，逻辑处理开始，request={}", linkId, JSON.toJSONString(commonScoresDto));
        changeMatchPeriod(commonScoresDto);
        commonScoresDto.setLinkedId(linkId);
        scoresProducer.sendStandardMatchScores(commonScoresDto);

        try{
            MatchScoresBetterDto scores = new MatchScoresBetterDto();
            scores.setSportId(standardMatchInfo.getSportId());
            scores.setDataSourceCode(businessEvent);
            scores.setMatchId(matchId+"");
            scores.setScoresJson(matchScoresInfo.getScoresJson());
            copyStandardScores(scores, soldMessageRequest.getLinkId());
        }catch (Exception e){
            log.error("{}开售复制比分异常：",soldMessageRequest.getLinkId(),e);
        }
    }
    private void changeMatchPeriod(CommonStandardScoresDto commonScoresDto) {
        if(commonScoresDto.getSportId()!=2){
            return;
        }
        if(301L == commonScoresDto.getPeriodId()){
            commonScoresDto.setPeriodId(13L);
        }else if(302L == commonScoresDto.getPeriodId()){
            commonScoresDto.setPeriodId(14L);
        }if(303L == commonScoresDto.getPeriodId()){
            commonScoresDto.setPeriodId(15L);
        }if(304L == commonScoresDto.getPeriodId()){
            commonScoresDto.setPeriodId(16L);
        }if(31L == commonScoresDto.getPeriodId()){
            commonScoresDto.setPeriodId(1L);
        }
    }



    /**
     * 复制标准比分
     *
     * @param scores
     */
    private void copyStandardScores(MatchScoresBetterDto scores, String linkId) {
        if(!scores.getSportId().equals(1L) && !scores.getSportId().equals(2L)){
            return;
        }
        StandardMatchScores standardMatchScores = scoresRedisHelp.getCatchStandScoreByMatchId(new Long(scores.getMatchId()));
        if (standardMatchScores == null) {
            standardMatchScores = new StandardMatchScores();
        }
        standardMatchScores.setDataSourceCode(scores.getDataSourceCode());
        standardMatchScores.setScoreJson(scores.getScoresJson());
        standardMatchScores.setUpdateTime(System.currentTimeMillis());
        if (SportTypeEnum.FOOTBALL.getValue().equals(scores.getSportId())) {
            Map<Long, FootballScores> footballScores = copyFootballScores(scores, standardMatchScores);
            standardMatchScores.setScoreJson(JSONObject.toJSONString(footballScores));
        }else if(SportTypeEnum.BASKETBALL.getValue().equals(scores.getSportId())){
            standardMatchScores.setScoreJson(scores.getScoresJson());
        }
        scoresRedisHelp.saveCatchStandScore(standardMatchScores);
        log.info("【SoldMessageScoresConsumer:" + SOLD_MESSAGE + "】【::" + linkId + "::】开售处理后补发比分,复制标准比分完成");
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
    private Map<Long, FootballScores> copyFootballScores(MatchScoresBetterDto scores, StandardMatchScores standardMatchScores) {
        FootballSwitch footballSwitch = new FootballSwitch();
        if (StringUtils.isNotEmpty(standardMatchScores.getDataSourceAccoSwitch())) {
            footballSwitch = JSONObject.parseObject(standardMatchScores.getDataSourceAccoSwitch(), FootballSwitch.class);
        }
        Map<Long, FootballScores> standardScores = new HashMap<>();
        //标准比分为空，直接复制三方比分
        if (!StringUtils.isEmpty(standardMatchScores.getScoreJson())) {
            standardScores = JSON.parseObject(standardMatchScores.getScoreJson(), new TypeReference<Map<Long, FootballScores>>() {
            });
        } else {
            standardScores = JSON.parseObject(scores.getScoresJson(), new TypeReference<Map<Long, FootballScores>>() {
            });
        }
        Map<Long, FootballScores> allPeriodScores = JSON.parseObject(scores.getScoresJson(), new TypeReference<Map<Long, FootballScores>>() {
        });
        FootballScores thirdWholeScores= allPeriodScores.get(WHOLE_MATCH);
        if(thirdWholeScores==null){
            thirdWholeScores = new FootballScores(WHOLE_MATCH);
        }

        Boolean hasOt = false;
        Integer otHomeGoal = 0, otAwayGoal = 0;
        Integer otHomeCorner = 0, otAwayCorner = 0;
        Integer otHomeYellowCard = 0, otAwayYellowCard = 0;
        Integer otHomeRedCard = 0, otAwayRedCard = 0;
        Integer otHomeAttack = 0, otAwayAttack = 0;
        Integer otHomeDangerousAttack = 0, otAwayDangerousAttack = 0;
        Integer otHomePossession = 0, otAwayPossession = 0;
        Integer otShotOnHome = 0, otShotOnAway = 0;
        Integer otShotOffHome = 0, otShotOffAway = 0;
        Integer otShotHome = 0, otShotAway = 0;

        //拼阶段100的比分-常规赛不含加时
        Integer homeGoal = 0, awayGoal = 0;
        Integer homeCorner = 0, awayCorner = 0;
        Integer homeYellowCard = 0, awayYellowCard = 0;
        Integer homeRedCard = 0, awayRedCard = 0;
        Integer homeAttack = 0, awayAttack = 0;
        Integer homeDangerousAttack = 0, awayDangerousAttack = 0;
        Integer homePossession = 0, awayPossession = 0;
        Integer shotOnHome = 0, shotOnAway = 0;
        Integer shotOffHome = 0, shotOffAway = 0;
        Integer shotHome = 0, shotAway = 0;
        int rate = 0;
        int otrate = 0;
        //检索历史比分，根据开关同步历史比分
        for (Map.Entry<Long, FootballScores> entry : allPeriodScores.entrySet()) {
            //获取标准比分当前阶段的比分
            FootballScores standScores = standardScores.get(entry.getKey());
            if (standScores == null) {
                standScores = new FootballScores(entry.getKey());
            }
            if (entry.getKey() == 6L) {
                //获取三方比分当前阶段的比分
                FootballScores thirdScores = entry.getValue();
                if(thirdScores==null){
                    thirdScores = new FootballScores(entry.getKey());
                }
                FootballScores footballMinScores1 = standardScores.get(60899L);
                FootballScores footballMinScores2 = standardScores.get(61799L);
                FootballScores footballMinScores3 = standardScores.get(62699L);
                if (footballMinScores1 == null) {
                    footballMinScores1 = new FootballScores(60899L);
                    standardScores.put(60899L, footballMinScores1);
                }
                if (footballMinScores2 == null) {
                    footballMinScores2 = new FootballScores(61799L);
                    standardScores.put(61799L, footballMinScores2);
                }
                if (footballMinScores3 == null) {
                    footballMinScores3 = new FootballScores(62699L);
                    standardScores.put(62699L, footballMinScores3);
                }
                if (footballSwitch.getGoalHf() == 1) {
                    standScores.setGoal(thirdScores.getGoal());
                    if (allPeriodScores.get(60899L) != null) {
                        footballMinScores1.setGoal(allPeriodScores.get(60899L).getGoal());
                    }
                    if (allPeriodScores.get(61799L) != null) {
                        footballMinScores2.setGoal(allPeriodScores.get(61799L).getGoal());
                    }
                    if (allPeriodScores.get(62699L) != null) {
                        footballMinScores3.setGoal(allPeriodScores.get(62699L).getGoal());
                    }
                }
                if (footballSwitch.getCornerHf() == 1) {
                    standScores.setCorner(thirdScores.getCorner());
                    if (allPeriodScores.get(60899L) != null) {
                        footballMinScores1.setCorner(allPeriodScores.get(60899L).getCorner());
                    }
                    if (allPeriodScores.get(61799L) != null) {
                        footballMinScores2.setCorner(allPeriodScores.get(61799L).getCorner());
                    }
                    if (allPeriodScores.get(62699L) != null) {
                        footballMinScores3.setCorner(allPeriodScores.get(62699L).getCorner());
                    }
                }
                if (footballSwitch.getYellowHf() == 1) {
                    standScores.setYellowCard(thirdScores.getYellowCard());
                    if (allPeriodScores.get(60899L) != null) {
                        footballMinScores1.setYellowCard(allPeriodScores.get(60899L).getYellowCard());
                    }
                    if (allPeriodScores.get(61799L) != null) {
                        footballMinScores2.setYellowCard(allPeriodScores.get(61799L).getYellowCard());
                    }
                    if (allPeriodScores.get(62699L) != null) {
                        footballMinScores3.setYellowCard(allPeriodScores.get(62699L).getYellowCard());
                    }
                }
                if (footballSwitch.getRedHf() == 1) {
                    standScores.setRedCard(thirdScores.getRedCard());
                    if (allPeriodScores.get(60899L) != null) {
                        footballMinScores1.setRedCard(allPeriodScores.get(60899L).getRedCard());
                    }
                    if (allPeriodScores.get(61799L) != null) {
                        footballMinScores2.setRedCard(allPeriodScores.get(61799L).getRedCard());
                    }
                    if (allPeriodScores.get(62699L) != null) {
                        footballMinScores3.setRedCard(allPeriodScores.get(62699L).getRedCard());
                    }
                }
                //三方的进攻危险进攻射正射偏控球率数据同步
                this.setOther(standScores, thirdScores);
                standardScores.put(6L, standScores);
                standardScores.put(60899L, footballMinScores1);
                standardScores.put(61799L, footballMinScores2);
                standardScores.put(62699L, footballMinScores3);
            }

            if (entry.getKey() == 7L) {
                //获取三方比分当前阶段的比分
                FootballScores thirdScores = entry.getValue();
                if(thirdScores==null){
                    thirdScores = new FootballScores(entry.getKey());
                }
                FootballScores footballMinScores1 = standardScores.get(73599L);
                FootballScores footballMinScores2 = standardScores.get(74499L);
                FootballScores footballMinScores3 = standardScores.get(75399L);
                if (footballMinScores1 == null) {
                    footballMinScores1 = new FootballScores(73599L);
                    standardScores.put(73599L, footballMinScores1);
                }
                if (footballMinScores2 == null) {
                    footballMinScores2 = new FootballScores(74499L);
                    standardScores.put(74499L, footballMinScores2);
                }
                if (footballMinScores3 == null) {
                    footballMinScores3 = new FootballScores(75399L);
                    standardScores.put(75399L, footballMinScores3);
                }
                if (footballSwitch.getGoalFt() == 1) {
                    standScores.setGoal(thirdScores.getGoal());
                    if (allPeriodScores.get(73599L) != null) {
                        footballMinScores1.setGoal(allPeriodScores.get(73599L).getGoal());
                    }
                    if (allPeriodScores.get(74499L) != null) {
                        footballMinScores2.setGoal(allPeriodScores.get(74499L).getGoal());
                    }
                    if (allPeriodScores.get(75399L) != null) {
                        footballMinScores3.setGoal(allPeriodScores.get(75399L).getGoal());
                    }
                }
                if (footballSwitch.getCornerFt() == 1) {
                    standScores.setCorner(thirdScores.getCorner());
                    if (allPeriodScores.get(73599L) != null) {
                        footballMinScores1.setCorner(allPeriodScores.get(73599L).getCorner());
                    }
                    if (allPeriodScores.get(74499L) != null) {
                        footballMinScores2.setCorner(allPeriodScores.get(74499L).getCorner());
                    }
                    if (allPeriodScores.get(75399L) != null) {
                        footballMinScores3.setCorner(allPeriodScores.get(75399L).getCorner());
                    }
                }
                if (footballSwitch.getYellowFt() == 1) {
                    standScores.setYellowCard(thirdScores.getYellowCard());
                    if (allPeriodScores.get(73599L) != null) {
                        footballMinScores1.setYellowCard(allPeriodScores.get(73599L).getYellowCard());
                    }
                    if (allPeriodScores.get(74499L) != null) {
                        footballMinScores2.setYellowCard(allPeriodScores.get(74499L).getYellowCard());
                    }
                    if (allPeriodScores.get(75399L) != null) {
                        footballMinScores3.setYellowCard(allPeriodScores.get(75399L).getYellowCard());
                    }
                }
                if (footballSwitch.getRedFt() == 1) {
                    standScores.setRedCard(thirdScores.getRedCard());
                    if (allPeriodScores.get(73599L) != null) {
                        footballMinScores1.setRedCard(allPeriodScores.get(73599L).getRedCard());
                    }
                    if (allPeriodScores.get(74499L) != null) {
                        footballMinScores2.setRedCard(allPeriodScores.get(74499L).getRedCard());
                    }
                    if (allPeriodScores.get(75399L) != null) {
                        footballMinScores3.setRedCard(allPeriodScores.get(75399L).getRedCard());
                    }
                }
                //三方的进攻危险进攻射正射偏控球率数据同步
                this.setOther(standScores, thirdScores);
                standardScores.put(7L, standScores);
                standardScores.put(73599L, footballMinScores1);
                standardScores.put(74499L, footballMinScores2);
                standardScores.put(75399L, footballMinScores3);
            }
            if (entry.getKey() == 41L) {
                //获取三方比分当前阶段的比分
                FootballScores thirdScores = entry.getValue();
                if(thirdScores==null){
                    thirdScores = new FootballScores(entry.getKey());
                }
                if (footballSwitch.getGoalOt() == 1) {
                    standScores.setGoal(thirdScores.getGoal());
                }
                if (footballSwitch.getCornerOt() == 1) {
                    standScores.setCorner(thirdScores.getCorner());
                }
                if (footballSwitch.getYellowOt() == 1) {
                    standScores.setYellowCard(thirdScores.getYellowCard());
                }
                if (footballSwitch.getRedOt() == 1) {
                    standScores.setRedCard(thirdScores.getRedCard());
                }
                //三方的进攻危险进攻射正射偏控球率数据同步
                this.setOther(standScores, thirdScores);
                standardScores.put(41L, standScores);
            }
            if (entry.getKey() == 42L) {
                //获取三方比分当前阶段的比分
                FootballScores thirdScores = entry.getValue();
                if(thirdScores==null){
                    thirdScores = new FootballScores(entry.getKey());
                }
                if (footballSwitch.getGoalOt() == 1) {
                    standScores.setGoal(thirdScores.getGoal());
                }
                if (footballSwitch.getCornerOt() == 1) {
                    standScores.setCorner(thirdScores.getCorner());
                }
                if (footballSwitch.getYellowOt() == 1) {
                    standScores.setYellowCard(thirdScores.getYellowCard());
                }
                if (footballSwitch.getRedOt() == 1) {
                    standScores.setRedCard(thirdScores.getRedCard());
                }
                //三方的进攻危险进攻射正射偏控球率数据同步
                this.setOther(standScores, thirdScores);
                standardScores.put(42L, standScores);
            }
            if (entry.getKey() == 41L || entry.getKey() == 42L) {
                hasOt = true;
                FootballScores ot1 = allPeriodScores.get(41L);
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
            if (entry.getKey() == 6L || entry.getKey() == 7L) {
                FootballScores ftScore = entry.getValue();
                if (ftScore != null) {
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
            }
            if (entry.getKey() == 50L) {
                //获取三方比分当前阶段的比分
                FootballScores thirdScores = entry.getValue();
                if(thirdScores==null){
                    thirdScores = new FootballScores(entry.getKey());
                }
                log.info("{}，同步点球大战比分1：{}", footballSwitch.getPenalty(), thirdScores);
                if (footballSwitch.getPenalty() == 1) {
                    standScores.setGoal(thirdScores.getGoal());
                }
                log.info("{}，同步点球大战比分2：{}", footballSwitch.getPenalty(), standScores);
                standardScores.put(50L, standScores);
            }
        }
        //存在加时赛
        if(hasOt){
            FootballScores standScoresOts = standardScores.get(110L);
            if (standScoresOts == null) {
                standScoresOts = new FootballScores(110L);
                standardScores.put(110L, standScoresOts);
            }
            if (footballSwitch.getGoalOt() == 1) {
                standScoresOts.setGoal(new CommonItem(otHomeGoal, otAwayGoal));
            }
            if (footballSwitch.getCornerOt() == 1) {
                standScoresOts.setCorner(new CommonItem(otHomeCorner, otAwayCorner));
            }
            if (footballSwitch.getYellowOt() == 1) {
                standScoresOts.setYellowCard(new CommonItem(otHomeYellowCard, otAwayYellowCard));
            }
            if (footballSwitch.getRedOt() == 1) {
                standScoresOts.setRedCard(new CommonItem(otHomeRedCard, otAwayRedCard));
            }
            standScoresOts.countFaCard();
            standScoresOts.setAttack(new CommonItem(otHomeAttack, otAwayAttack));
            standScoresOts.setDangerousAttack(new CommonItem(otHomeDangerousAttack, otAwayDangerousAttack));
            if (otrate != 0) {
                standScoresOts.setBallPossessionPercentage(new CommonItem(otHomePossession / otrate, otAwayPossession / otrate));
            } else {
                standScoresOts.setBallPossessionPercentage(new CommonItem(otHomePossession, otAwayPossession));
            }
            standScoresOts.setShotOn(new CommonItem(otShotOnHome, otShotOnAway));
            standScoresOts.setShotOff(new CommonItem(otShotOffHome, otShotOffAway));
            standScoresOts.setShot(new CommonItem(otShotHome, otShotAway));
            //阶段41|| 42
            standardScores.put(110L, standScoresOts);
        }


        FootballScores standScoresEnd = standardScores.get(100L);
        if (standScoresEnd == null) {
            standScoresEnd = new FootballScores(100L);
            standardScores.put(100L, standScoresEnd);
        }
        standScoresEnd.setGoal(new CommonItem(homeGoal, awayGoal));
        standScoresEnd.setCorner(new CommonItem(homeCorner, awayCorner));
        standScoresEnd.setYellowCard(new CommonItem(homeYellowCard, awayYellowCard));
        standScoresEnd.setRedCard(new CommonItem(homeRedCard, awayRedCard));
        standScoresEnd.setAttack(new CommonItem(homeAttack, awayAttack));
        standScoresEnd.setDangerousAttack(new CommonItem(homeDangerousAttack, awayDangerousAttack));
        if (otrate != 0) {
            standScoresEnd.setBallPossessionPercentage(new CommonItem(homePossession / rate, awayPossession / rate));
        } else {
            standScoresEnd.setBallPossessionPercentage(new CommonItem(homePossession, awayPossession));
        }
        standScoresEnd.setShotOn(new CommonItem(shotOnHome, shotOnAway));
        standScoresEnd.setShotOff(new CommonItem(shotOffHome, shotOffAway));
        standScoresEnd.setShot(new CommonItem(shotHome, shotAway));
        standardScores.put(100L, standScoresEnd);


//        FootballScores wholeStands = standardScores.get(WHOLE_MATCH);
        for(Map.Entry<Long, FootballScores> entry : standardScores.entrySet()){
            if(entry.getKey()==6 || entry.getKey()==7 || entry.getKey()==110 ){
                //累计-1比分
                calcWholeScore(thirdWholeScores,standardScores.get(entry.getKey()));
            }
            //计算每个阶段的罚牌比分
            entry.getValue().countFaCard();
        }

        return standardScores;
    }
    private void calcWholeScore(FootballScores wholeSores,FootballScores standScores) {
        wholeSores.setGoal(new CommonItem(wholeSores.getGoal().getHome()+standScores.getGoal().getHome(),
                wholeSores.getGoal().getAway()+standScores.getGoal().getAway()));
        wholeSores.setCorner(new CommonItem(wholeSores.getCorner().getHome()+standScores.getCorner().getHome(),
                wholeSores.getCorner().getAway()+standScores.getCorner().getAway()));
        wholeSores.setYellowCard(new CommonItem(wholeSores.getYellowCard().getHome()+standScores.getYellowCard().getHome(),
                wholeSores.getYellowCard().getAway()+standScores.getYellowCard().getAway()));
        wholeSores.setRedCard(new CommonItem(wholeSores.getRedCard().getHome()+standScores.getRedCard().getHome(),
                wholeSores.getRedCard().getAway()+standScores.getRedCard().getAway()));
        wholeSores.countFaCard();
    }


}
