package com.panda.merge.dto;

import com.panda.merge.annotation.ScoresProperty;
import com.panda.merge.constant.TeamTypeConstant;
import com.panda.merge.model.MatchEventInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

@Slf4j
@Data
public class CricketBallScores extends  AbstractSportScores{

    @ScoresProperty(eventName = "投球",eventCode ={"point","delivery","over"})
    private CommonItem delivery ;

    @ScoresProperty(eventName = "轮",eventCode ={"over"})
    private Map<Integer,CommonItem> over = new HashMap<>();

    @ScoresProperty(eventName = "分数")
    private CommonItem point;

    @ScoresProperty(eventName = "三柱门")
    private CommonItem wicet;

    @ScoresProperty(eventName = "赢得掷币")
    private CommonItem toWinTheToss;

    @ScoresProperty(eventName = "大比分")
    private CommonItem matchScore;



    public CricketBallScores() {
        super.init(this);
    }

    public void doCalculation(MatchEventInfo data, Map<Long, CricketBallScores> allPeriodScores) {
        CricketBallScores wholeSores = allPeriodScores.get(WHOLE_MATCH);
        //得分
        if(data.getEventCode().equals("point")|| data.getEventCode().equals("match_status")) {
            wholeSores.matchScore.setHome(data.getT1());
            wholeSores.matchScore.setAway(data.getT2());
            wholeSores.point.setHome(data.getT1());
            wholeSores.point.setAway(data.getT2());
            if(data.getFirstT1()!=null){
                matchScore.setHome(data.getFirstT1());
            }
            if(data.getFirstT2()!=null){
                matchScore.setAway(data.getFirstT2());
            }
            if(data.getFirstT1()!=null){
                point.setHome(data.getFirstT1());
            }
            if(data.getFirstT2()!=null){
                point.setAway(data.getFirstT2());
            }
        }
        //三柱门-出局数
        if(data.getEventCode().equals("wicet")) {
            wicet.setHome(data.getT1());
            wicet.setAway(data.getT2());
            Integer firstHome = 0;
            Integer firstAway = 0;
            if(data.getMatchPeriodId()==9L){
                CricketBallScores firstScores = allPeriodScores.get(8L);
                if(firstScores.getWicet()!=null){
                    firstHome = firstScores.getWicet().getHome();
                    firstAway = firstScores.getWicet().getAway();
                }
            }
            wholeSores.wicet.setHome(firstHome+data.getT1());
            wholeSores.wicet.setAway(firstAway+data.getT2());
        }
        if(data.getEventCode().equals("over")) {
            Map<Integer,CommonItem> overMap = this.getOver();
            if(overMap==null ){
                overMap = new HashMap<>();
                this.setOver(overMap);
            }
            log.info("::{}::板球比分计算：当前轮：{}，旧轮比分:{}",data.getLinkId(),data.getSecondNum(),over);
            if(data.getSecondNum()!=null){
                if(overMap.containsKey(data.getSecondNum())){
                    CommonItem cc = overMap.get(data.getSecondNum());
                    if(cc.getHome()==0){
                        overMap.get(data.getSecondNum()).setHome(data.getSecondT1());
                    }
                    if(cc.getAway()==0){
                        overMap.get(data.getSecondNum()).setAway(data.getSecondT2());
                    }
                }else{
                    CommonItem com = new CommonItem();
                    com.setHome(data.getSecondT1());
                    com.setAway(data.getSecondT2());
                    overMap.put(data.getSecondNum(),com);
                }
                log.info("::{}::板球比分计算：当前轮：{}，计算轮比分:{}",data.getLinkId(),data.getSecondNum(),overMap);
                wholeSores.setOver(null);
                over = overMap;
            }
            log.info("::{}::板球比分计算：当前轮：{}，新轮比分:{}",data.getLinkId(),data.getSecondNum(),over);
        }
        if(data.getEventCode().equals("to_win_the_toss")) {
            toWinTheToss.setHome(data.getT1());
            toWinTheToss.setAway(data.getT2());
            wholeSores.toWinTheToss.setHome(data.getT1());
            wholeSores.toWinTheToss.setAway(data.getT2());
        }
        if(data.getEventCode().equals("delivery")) {
            wholeSores.delivery.setHome(data.getT1());
            wholeSores.delivery.setAway(data.getT2());
            delivery.setHome(data.getFirstT1());
            delivery.setAway(data.getFirstT2());
        }
    }

    public void cancelCalculation(MatchEventInfo data, MatchEventInfo oldMatchInfo, Map<Long, CricketBallScores> allPeriodScores) {
        CricketBallScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        if(data.getEventCode().equals("wicet")) {
            if(data.getHomeAway().equals(TeamTypeConstant.HOME)){
                wicet.setHome(wicet.getHome()-1);
                wholeSores.wicet.setHome(wholeSores.wicet.getHome()-1);
            }else {
                wicet.setAway(wicet.getAway()-1);
                wholeSores.wicet.setAway(wholeSores.wicet.getAway()-1);
            }
        }
        if(data.getEventCode().equals("point")) {
            Integer addHome = data.getT1()-wholeSores.getMatchScore().getHome();
            Integer addAway = data.getT2()-wholeSores.getMatchScore().getAway();
            wholeSores.matchScore.setHome(data.getT1());
            wholeSores.matchScore.setAway(data.getT2());
            this.matchScore.setHome(matchScore.getHome()+addHome);
            this.matchScore.setAway(matchScore.getAway()+addAway);

            Integer pointHome = data.getT1()-wholeSores.getPoint().getHome();
            Integer pointAway = data.getT2()-wholeSores.getPoint().getAway();
            wholeSores.point.setHome(data.getT1());
            wholeSores.point.setAway(data.getT2());
            this.point.setHome(point.getHome()+pointHome);
            this.point.setAway(point.getAway()+pointAway);
        }
    }
}
