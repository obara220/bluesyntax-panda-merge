package com.panda.merge.filter.basketball;

import com.panda.merge.dto.BasketballScores;
import com.panda.merge.dto.CommonThirdScoresDto;
import com.panda.merge.dto.Request;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.model.*;
import com.panda.merge.service.IWsPushService;
import com.panda.merge.utils.BasketBallSettleScoreUtils;
import com.panda.merge.utils.EndEventUtils;
import com.panda.merge.utils.IdGenerator;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class BasketballInstantSettleFilter {
    private final static Long HT13=13L;
    private final static Long HT14=14L;
    private final static Long HT15=15L;
    private final static Long HT16=16L;
    private final static Long ET=40L;
    private final static Long HT=1L;
    private final static Long HT2=2L;
    private final static Long WHOLE_PERIOD=-1L;

    /**
     * 即时比分转化结算
     * */
   public static MatchSettleThirdBasketScore filter(Request<CommonThirdScoresDto> request, MatchSettleInfo matchSettleInfo,
                                                    MatchEventInfo eventInfo, List<MatchSettleScore> list,StandardMatchInfo match){
       List<Long>  period0= Arrays.asList(13L,14L, 15L,16L,1L,2L);
       try {
           String link = request.getLinkId();
           //根据阶段获取不同的比分
           log.info(":{}: InstantSettleBasketBallScore 1 BasketballInstantSettleFilter start",link);
           MatchSettleThirdBasketScore matchSettleThirdBasketScore= initMatchSettleThirdBasketScore(request.getData(),eventInfo);
           log.info(":{}: InstantSettleBasketBallScore 1 BasketballInstantSettleFilter 1 matchSettleThirdBasketScore:{}",link,matchSettleThirdBasketScore);
           Long period = request.getData().getPeriodId();
           Map<String, BasketballScores> basketballScoresMap = JsonMapUtils.transferBasketballMap(request.getData().getScores());
           //1.获取当前节比分
           BasketballScores periodScore = basketballScoresMap.get(period.toString());
           log.info(":{}: InstantSettleBasketBallScore 1 BasketballInstantSettleFilter 2 periodScore:{},periodId:{}",link,periodScore,period);
           if (periodScore == null) {
               return null;
           }
           //51079 篮球即时结算跨阶段修正比分不做处理
           if(period0.contains(period) && match.getMatchLength()!=17){
               int idx = period0.indexOf(period);
               if(period0.size() >idx){
                   Long nextPeroid = period0.get(idx + 1);
                   if(nextPeroid!=null){
                       BasketballScores nextPeriodScore = basketballScoresMap.get(nextPeroid.toString());
                        //校验 假定已经有 了第三节比分而且不是 0-0 的情况 下 如果阶段是第二节，则不予处理直接return
                       if (nextPeriodScore!=null &&
                               (nextPeriodScore.getMatchScore().getHome()!= 0 ||
                                       nextPeriodScore.getMatchScore().getAway()!= 0)) {
                            return null;
                       }
                   }
               }
           }
           matchSettleThirdBasketScore.setSecondT1(periodScore.getMatchScore().getHome());
           matchSettleThirdBasketScore.setSecondT2(periodScore.getMatchScore().getAway());
           //1.2根据阶段获取编码
           String periodCode = EndEventUtils.getBasketballInSettleCodeByPeriod(period);
           log.info(":{}: InstantSettleBasketBallScore 1 BasketballInstantSettleFilter 3 periodCode:{}",link,periodCode);
           if (periodCode != null && periodScore != null) {
               //不为空则获取节比分
               MatchSettleScore matchSettleScore = BasketBallSettleScoreUtils.initMatchSettleScore(matchSettleInfo.getStandardMatchId());
               matchSettleScore.setSettleNum(periodCode);
               matchSettleScore.setT1(periodScore.getMatchScore().getHome());
               matchSettleScore.setT2(periodScore.getMatchScore().getAway());
               list.add(matchSettleScore);
               log.info(":{}: InstantSettleBasketBallScore 1 BasketballInstantSettleFilter 4 period_matchSettleScore:{}",link,matchSettleScore);
           }else {
               //如果是2节制的
           }
           //2.获取半场比分
           Integer htT1 = 0;
           Integer htT2 = 0;
           //2.1 3x3 上下半场 则放弃半场
           if ( (!(period.equals(21L) || period.equals(1L) || period.equals(2L)))&&(basketballScoresMap.get("21")==null)) {
               String htCode = "bk_in_1ht";

               BasketballScores period1Score =null;
               BasketballScores period2Score =null;
               BasketballScores etScore =null;
               //1.如果是上半场
               if (period.equals(13L) || period.equals(14L)) {
                   htCode = "bk_in_1ht";
                    period1Score = basketballScoresMap.get( HT13.toString());
                    period2Score = basketballScoresMap.get(HT14.toString());

               } else if (period.equals(15L) || period.equals(16L)) {
                   //2.如果是下半场
                   htCode = "bk_in_2htet";
                   period1Score = basketballScoresMap.get(HT15.toString());
                   period2Score = basketballScoresMap.get(HT16.toString());

               }else if(period.equals(40L)){
                   htCode = "bk_in_2htet";

                   period1Score = basketballScoresMap.get(HT15.toString());
                   period2Score = basketballScoresMap.get(HT16.toString());
                   //如果没有 15 16阶段 那 取 2的阶段 得到下半场比分
                   if(period2Score==null&&period1Score==null){
                       period2Score =  basketballScoresMap.get(HT2.toString());
                   }
                   etScore = basketballScoresMap.get(ET.toString());
                   //加入加时赛比分
                   htT1+=etScore.getMatchScore().getHome();
                   htT2+=etScore.getMatchScore().getAway();
               }
               if(period1Score!=null){
                   htT1+=period1Score.getMatchScore().getHome();
                   htT2+=period1Score.getMatchScore().getAway();
               }
               if(period2Score!=null){
                   htT1+=period2Score.getMatchScore().getHome();
                   htT2+=period2Score.getMatchScore().getAway();
               }
               MatchSettleScore matchSettleScore = BasketBallSettleScoreUtils.initMatchSettleScore(matchSettleInfo.getStandardMatchId());
               matchSettleScore.setSettleNum(htCode);
               matchSettleScore.setT1(htT1);
               matchSettleScore.setT2(htT2);
               list.add(matchSettleScore);

               log.info(":{}: InstantSettleBasketBallScore 1 BasketballInstantSettleFilter 5 ht_matchSettleScore:{}",link,matchSettleScore);
               matchSettleThirdBasketScore.setFirstT1(matchSettleScore.getT1());
               matchSettleThirdBasketScore.setFirstT2(matchSettleScore.getT2());
           }
           //如果是 2 15 16 阶段 则新增下半场的比分(不含加时的)
//           if(period.equals(2L) || period.equals(15L) || period.equals(16L)){
//               MatchSettleScore matchSettleScore = BasketBallSettleScoreUtils.initMatchSettleScore(matchSettleInfo.getStandardMatchId());
//               matchSettleScore.setSettleNum("bk_in_2ht");
//               matchSettleScore.setT1(htT1);
//               matchSettleScore.setT2(htT2);
//               list.add(matchSettleScore);
//           }
           //3.获取总分
           BasketballScores wholeScore = basketballScoresMap.get(WHOLE_PERIOD.toString());
//           if (wholeScore != null&&!period.equals(40L)) {
//               //3.1 如果不是加时赛则 获取 总分不含加时X
//               String  ftCode = "bk_in_rg";
//               MatchSettleScore matchSettleScore = BasketBallSettleScoreUtils.initMatchSettleScore(matchSettleInfo.getStandardMatchId());
//               matchSettleScore.setSettleNum(ftCode);
//               matchSettleScore.setT1(wholeScore.getMatchScore().getHome());
//               matchSettleScore.setT2(wholeScore.getMatchScore().getAway());
//               list.add(matchSettleScore);
//               matchSettleThirdBasketScore.setT1(matchSettleScore.getT1());
//               matchSettleThirdBasketScore.setT2(matchSettleScore.getT2());
//           }
           if (wholeScore != null) {
               //3.1 如果不是加时赛则 获取 总分不含加时X
               String ftCode = "bk_in_et";
               MatchSettleScore matchSettleScore = BasketBallSettleScoreUtils.initMatchSettleScore(matchSettleInfo.getStandardMatchId());
               matchSettleScore.setSettleNum(ftCode);
               matchSettleScore.setT1(wholeScore.getMatchScore().getHome());
               matchSettleScore.setT2(wholeScore.getMatchScore().getAway());
               list.add(matchSettleScore);
               log.info(":{}: InstantSettleBasketBallScore 1 BasketballInstantSettleFilter 6 ft_matchSettleScore:{}",link,matchSettleScore);
               matchSettleThirdBasketScore.setT1(matchSettleScore.getT1());
               matchSettleThirdBasketScore.setT2(matchSettleScore.getT2());
               matchSettleThirdBasketScore.setSumScore(matchSettleThirdBasketScore.getT1()+matchSettleThirdBasketScore.getT2());
               matchSettleThirdBasketScore.setSettleSumScore(matchSettleThirdBasketScore.getT1()+matchSettleThirdBasketScore.getT2()-0);
           }
           return matchSettleThirdBasketScore;
       }catch (Exception e){
           log.error("BasketballInstantSettleFilter-filter-link:"+request.getData().getLinkedId(),e.getMessage());
           return null;
       }

   }

    /**
     * 即时比分转化结算
     * */
    public static List<MatchSettleScore> transforSettleInScore(MatchSettleThirdBasketScore matchSettleThirdBasketScore, MatchSettleInfo matchSettleInfo,String link ){
        List<MatchSettleScore> list =new ArrayList<>();
        try {
            //结算编码获取
            Long period = matchSettleThirdBasketScore.getPeriodId();
            //比分赋值
            String periodCode = EndEventUtils.getBasketballInSettleCodeByPeriod(period);
            String htCode ="";
            String ftCode = "";
            if (period.equals(13L) || period.equals(14L)) {
                htCode = "bk_in_1ht";
            } else if (period.equals(15L) || period.equals(16L)||period.equals(40L)) {
                //2.如果是下半场
                htCode = "bk_in_2htet";
            }
                //3.2 如果是加时赛则 获取总分 含加时
            ftCode = "bk_in_et";

            //当前节比分
            if(StringUtils.isNotEmpty(periodCode)){
                MatchSettleScore periodScore= BasketBallSettleScoreUtils.initMatchSettleScore(matchSettleInfo.getStandardMatchId());
                periodScore.setSettleNum(periodCode);
                periodScore.setT1(matchSettleThirdBasketScore.getSecondT1());
                periodScore.setT2(matchSettleThirdBasketScore.getSecondT2());
                list.add(periodScore);
            }
            //当前半场含加时
            if(StringUtils.isNotEmpty(htCode)){
                MatchSettleScore htScore= BasketBallSettleScoreUtils.initMatchSettleScore(matchSettleInfo.getStandardMatchId());
                htScore.setSettleNum(htCode);
                htScore.setT1(matchSettleThirdBasketScore.getFirstT1());
                htScore.setT2(matchSettleThirdBasketScore.getFirstT2());
                list.add(htScore);
            }
            //当前半场不含加时
            if( period.equals(15L) || period.equals(16L)){
                MatchSettleScore htScore= BasketBallSettleScoreUtils.initMatchSettleScore(matchSettleInfo.getStandardMatchId());
                htScore.setSettleNum("bk_in_2ht");
                htScore.setT1(matchSettleThirdBasketScore.getFirstT1());
                htScore.setT2(matchSettleThirdBasketScore.getFirstT2());
                list.add(htScore);
            }
            //全场
            MatchSettleScore ftScore= BasketBallSettleScoreUtils.initMatchSettleScore(matchSettleInfo.getStandardMatchId());
            ftScore.setSettleNum(ftCode);
            ftScore.setT1(matchSettleThirdBasketScore.getT1());
            ftScore.setT2(matchSettleThirdBasketScore.getT2());
            list.add(ftScore);
                return list;
        }catch (Exception e){
            log.error("BasketballInstantSettleFilter-transforSettleInScore-link:"+link,e.getMessage());
         return list;
        }
    }

    /**
     * 初始化篮球比分
     * */
    private static MatchSettleThirdBasketScore initMatchSettleThirdBasketScore( CommonThirdScoresDto data, MatchEventInfo eventInfo) {
        MatchSettleThirdBasketScore matchSettleThirdBasketScore =new MatchSettleThirdBasketScore();
        //赛事信息
        matchSettleThirdBasketScore.setStandardMatchId(data.getStandardMatchId());
        matchSettleThirdBasketScore.setDataSourceCode(data.getDataSourceCode());
        matchSettleThirdBasketScore.setSportId(data.getSportId());
        matchSettleThirdBasketScore.setThirdMatchId(data.getThirdMatchId());
        //事件信息
        matchSettleThirdBasketScore.setEventTime(data.getScoreTime());
        matchSettleThirdBasketScore.setSecondFromStart(data.getSecondFromStart().intValue());
        matchSettleThirdBasketScore.setLinkId(data.getLinkedId());
        matchSettleThirdBasketScore.setPeriodId(data.getPeriodId());
        if(eventInfo!=null){
            matchSettleThirdBasketScore.setThirdEventId(eventInfo.getThirdEventId());
        }
        matchSettleThirdBasketScore.setId(IdGenerator.nextId());
        matchSettleThirdBasketScore.setCreateTime(System.currentTimeMillis());
        matchSettleThirdBasketScore.setModifyTime(System.currentTimeMillis());
        //比分信息后续补充
        return matchSettleThirdBasketScore;
    }

}
