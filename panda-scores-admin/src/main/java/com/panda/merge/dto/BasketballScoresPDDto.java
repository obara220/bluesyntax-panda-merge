package com.panda.merge.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.panda.merge.annotation.ScoresProperty;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.constant.TeamTypeConstant;
import com.panda.merge.dto.advertise.PDBasketBallSendBallDto;
import com.panda.merge.model.MatchEventInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@Data
public class BasketballScoresPDDto extends  AbstractSportScores{

    @ScoresProperty(eventName = "比分",eventCode ={"score_change","match_score","score_correction"})
    private CommonItem matchScore ;

    @ScoresProperty(eventName = "进球数",eventCode ={"score_change"})
    private CommonItem allPointer;


    @ScoresProperty(eventName = "罚球命中次数")
    private CommonItem freeThrowMade;
    @ScoresProperty(eventName = "罚球总数")
    private CommonItem freeThrowCount;


    @ScoresProperty(eventName = "三分球命中次数",eventCode ={"score_change"},extrainInfo="3")
    private CommonItem threePointerMade;

    @ScoresProperty(eventName = "三分球总数",eventCode ={"score_change"},extrainInfo="3")
    private CommonItem threePointer;


    @ScoresProperty(eventName = "两分球命中次数",eventCode ={"score_change"},extrainInfo="2")
    private CommonItem twoPointerMade ;
    @ScoresProperty(eventName = "两分球总数",eventCode ={"score_change"},extrainInfo="2")
    private CommonItem twoPointer ;


    @ScoresProperty(eventName = "篮板",eventCode = "rebound")
    private CommonItem rebound;

    @ScoresProperty(eventName = "防守篮板",eventCode = "reboundDefense")
    private CommonItem reboundDefense;

    @ScoresProperty(eventName = "进攻篮板",eventCode = "reboundAttack")
    private CommonItem reboundAttack;

    @ScoresProperty(eventName = "控球率",eventCode = "possession")
    private CommonItem possession;

    @ScoresProperty(eventName = "助攻",eventCode = "assist")
    private CommonItem assist;

    @ScoresProperty(eventName = "失误",eventCode = "turnover")
    private CommonItem turnover;

    @ScoresProperty(eventName = "犯规",eventCode ={"foul"})
    private CommonItem foul ;

    @ScoresProperty(eventName = "暂停",eventCode ={"timeout"})
    private CommonItem timeout ;

    @ScoresProperty(eventName = "抢断",eventCode ={"steal"})
    private CommonItem steal ;

    @ScoresProperty(eventName = "盖帽",eventCode ={"block"})
    private CommonItem block ;

    @ScoresProperty(eventName = "赢得跳球",eventCode ={"won_jump_ball"})
    private CommonItem wonJumpBall ;


    public void buildAllPointer() {
        allPointer = new CommonItem();
        allPointer.setHome(((freeThrowMade == null ? 0 : freeThrowMade.getHome())
                + (twoPointerMade == null ? 0 : twoPointerMade.getHome())
                + (threePointerMade == null ? 0 : threePointerMade.getHome())));
        allPointer.setAway(((freeThrowMade == null ? 0 : freeThrowMade.getAway())
                + (twoPointerMade == null ? 0 : twoPointerMade.getAway())
                + (threePointerMade == null ? 0 : threePointerMade.getAway())));
    }
    public void changeScoreByPdEventCodeAndHomeAway(String homeAway, String eventCode) {
        if("reboundDefense".equals(eventCode)){
            if(homeAway.equals("home")){
                reboundDefense.setHome(reboundDefense.getHome()+1);
                rebound.setHome(rebound.getHome()+1);
            }else {
                reboundDefense.setAway(reboundDefense.getAway()+1);
                rebound.setAway(rebound.getAway()+1);
            }
            return;
        }
        if("reboundAttack".equals(eventCode)){
            if(homeAway.equals("home")){
                reboundAttack.setHome(reboundAttack.getHome()+1);
                rebound.setHome(rebound.getHome()+1);
            }else {
                reboundAttack.setAway(reboundAttack.getAway()+1);
                rebound.setAway(rebound.getAway()+1);
            }
            return;
        }

        CommonItem commonItem  = this.getCommonItemByEventCode(eventCode);
        if(homeAway.equals("home")){
            commonItem.setHome(commonItem.getHome()+1);
        }else {
            commonItem.setAway(commonItem.getAway()+1);
        }

    }

    public CommonItem getCommonItemByEventCode(String eventCode) {
        if(eventCode.equals("assist")){
            return assist;
        }
        if(eventCode.equals("turnover")){
            return turnover;
        }
        if(eventCode.equals("foul")){
            return foul;
        }
        if(eventCode.equals("timeout")){
            return timeout;
        }
        if(eventCode.equals("steal")){
            return steal;
        }
        if(eventCode.equals("block")){
            return block;
        }
        if(eventCode.equals("won_jump_ball")){
            return wonJumpBall;
        }
        if(eventCode.equals("possession")){
            return possession;
        }
        return null;
    }
}
