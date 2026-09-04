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
