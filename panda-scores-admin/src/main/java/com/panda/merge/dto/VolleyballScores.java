package com.panda.merge.dto;

import com.panda.merge.annotation.ScoresProperty;
import com.panda.merge.model.MatchEventInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class VolleyballScores  extends  AbstractSportScores{

    @ScoresProperty(eventName = "全场比分",eventCode ={"volleyball_score_change","match_score"})
    private CommonItem matchScore ;
    @ScoresProperty(eventName = "局比分",eventCode ={"set_scores"})
    private CommonItem setScore ;

    @ScoresProperty(eventName = "发球比分")
    private CommonItem serveScoresCount ;

    @ScoresProperty(eventName = "发球失误次数")
    private CommonItem serveErrorCount;


    @ScoresProperty(eventName = "发球次数", eventCode = {"current_serve_volleyball"})
    private CommonItem serve;

    // eventCode 必须与 VolleyballEventTypeEnum 保持一致 ——
    // doCalculation / getFieldScoreByEventCode 用 type.getEventCode() 直接做字符串相等匹配，
    // 任何前缀偏差都会让该字段永远不被累加。
    @ScoresProperty(eventName = "发球失误", eventCode = {"service_error"})
    private CommonItem serviceError;

    @ScoresProperty(eventName = "出界", eventCode = {"out"})
    private CommonItem out;

    @ScoresProperty(eventName = "发球得分", eventCode = {"ace"})
    private CommonItem ace;

    @ScoresProperty(eventName = "扣杀", eventCode = {"kill"})
    private CommonItem kill;

    @ScoresProperty(eventName = "拦网", eventCode = {"block"})
    private CommonItem block;

    @ScoresProperty(eventName = "驱逐", eventCode = {"expulsion"})
    private CommonItem expulsion;

    @ScoresProperty(eventName = "取消资格", eventCode = {"disqualification"})
    private CommonItem disqualification;

    @ScoresProperty(eventName = "处罚", eventCode = {"penalty"})
    private CommonItem penalty;

    @ScoresProperty(eventName = "失误", eventCode = {"error"})
    private CommonItem error;

    @ScoresProperty(eventName = "先发球", eventCode = {"which_team_serves_first"})
    private CommonItem kickoff;

    public VolleyballScores() {
        super.init(this);
    }

    public void updateEvent( MatchEventInfo data){

        if(data.getEventCode().equals("volleyball_score_change")){
            log.info("linkedId:{}, thirdMatchId:{} ,eventCode:{},before: home:{},away:{}",data.getLinkId(),data.getThirdMatchId(),"matchScore",matchScore.getHome(),matchScore.getAway());
            matchScore.setHome(data.getT1());
            matchScore.setAway(data.getT2());
            setScore.setHome(data.getFirstT1());
            setScore.setAway(data.getFirstT2());
            log.info("linkedId:{},  thirdMatchId:{} ,eventCode:{},after: home:{},away:{}",data.getLinkId(),data.getThirdMatchId(),"matchScore",matchScore.getHome(),matchScore.getAway());
            if(data.getExtraInfo()!=null&&data.getExtraInfo().equals("1")){
                if("home".equals(data.getAddition3())){
                    serveScoresCount.setHome(serveScoresCount.getHome()+1);
                }
                if("away".equals(data.getAddition3())){
                    serveScoresCount.setAway(serveScoresCount.getAway()+1);
                }
            }
            if(data.getExtraInfo()!=null&&data.getExtraInfo().equals("2")){
                if("home".equals(data.getAddition3())){
                    serveErrorCount.setHome(serveErrorCount.getHome()+1);
                }
                if("away".equals(data.getAddition3())){
                    serveErrorCount.setAway(serveErrorCount.getAway()+1);
                }
            }
        }

    }
    public void cancelCalculation(MatchEventInfo data, MatchEventInfo oldMatchInfo) {
        if(oldMatchInfo.getEventCode().equals("volleyball_score_change")){
            matchScore.setHome(data.getT1());
            matchScore.setAway(data.getT2());
            setScore.setHome(data.getFirstT1());
            setScore.setAway(data.getFirstT2());
            if(oldMatchInfo.getExtraInfo()!=null&&oldMatchInfo.getExtraInfo().equals("1")){
                if(oldMatchInfo.getAddition3().equals("home")){
                    serveScoresCount.setHome(serveScoresCount.getHome()-1);
                }
                if(oldMatchInfo.getAddition3().equals("away")){
                    serveScoresCount.setAway(serveScoresCount.getAway()-1);
                }
            }
            if(oldMatchInfo.getExtraInfo()!=null&&oldMatchInfo.getExtraInfo().equals("2")){
                if(oldMatchInfo.getAddition3().equals("home")){
                    serveErrorCount.setHome(serveErrorCount.getHome()-1);
                }
                if(oldMatchInfo.getAddition3().equals("away")){
                    serveErrorCount.setAway(serveErrorCount.getAway()-1);
                }
            }
            if(data.getExtraInfo()!=null&&data.getExtraInfo().equals("1")){
                if("home".equals(data.getAddition3())){
                    serveScoresCount.setHome(serveScoresCount.getHome()+1);
                }
                if("away".equals(data.getAddition3())){
                    serveScoresCount.setAway(serveScoresCount.getAway()+1);
                }
            }
            if(data.getExtraInfo()!=null&&data.getExtraInfo().equals("2")){
                if("home".equals(data.getAddition3())){
                    serveErrorCount.setHome(serveErrorCount.getHome()+1);
                }
                if("away".equals(data.getAddition3())){
                    serveErrorCount.setAway(serveErrorCount.getAway()+1);
                }
            }
        }
    }


}
