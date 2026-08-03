package com.panda.merge.dto;

import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchSettleEvent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.net.Inet4Address;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
public class FootballPenaltyScores implements Serializable {

    /**
     * 当前点球大战局数
     * */
    private Integer firstNum;
    /**
     * 主客队射门次数
     * */
    private Integer pointNum;
    /**
     *每局比分
     **/
    private Map<String,CommonItem> roundScores;
    /**
     * 前五轮比分
     * */
    private CommonItem round5Scores;
    /**
     * 谁先射门
     * */
    private String shootFirst;
    /**
     * 点球射失个数
     * */
    private CommonItem penaltyMiss;

    /**
     * 当前点球开始
     */
    private List<Map<String, Object>> roundPenaltyKick;

    public FootballPenaltyScores(){
        firstNum =0;
        pointNum=0;
        penaltyMiss = new CommonItem();
        roundScores = new HashMap<>();
        round5Scores = new CommonItem(-1,-1,true);
    }

    public FootballPenaltyScores(boolean init){
        firstNum = 1;
        pointNum = 1;
        roundScores = new HashMap<>();
        round5Scores = new CommonItem();
        penaltyMiss = new CommonItem();
        roundScores.put("1",new CommonItem(null,null,true));
        roundScores.put("2",new CommonItem(null,null,true));
        roundScores.put("3",new CommonItem(null,null,true));
        roundScores.put("4",new CommonItem(null,null,true));
        roundScores.put("5",new CommonItem(null,null,true));
    }

    public FootballPenaltyScores(String eventCode){
        firstNum =0;
        pointNum = 0;
        CommonItem item=new CommonItem();
        item.setHome(-1);
        item.setAway(-1);
        roundScores = new HashMap<>();
        round5Scores = new CommonItem();
        roundScores.put("1",item);
        roundScores.put("2",item);
        roundScores.put("3",item);
        roundScores.put("4",item);
        roundScores.put("5",item);
    }


    public FootballPenaltyScores(Integer firstNum, Integer currentRound){
        this.firstNum = firstNum;
        pointNum=1;
        roundScores =new HashMap<>();
        round5Scores=new CommonItem();
        penaltyMiss = new CommonItem();
        for(int i=1;i<=currentRound;i++){
            roundScores.put(i+"",new CommonItem(null,null,true));
        }
    }

    /**
     * 进球或者没进
     * */
    public void calutionPenaltyScores(MatchEventInfo data) {
        log.info("calulationPenaltyScores,单条事件逻辑处理,linkId="+data.getLinkId()+",点球大战处理2,pointNum="+pointNum+",firstNum="+firstNum);
        //点球进球或射失
        boolean isPenalty=data.getEventCode().equals("goal")||data.getEventCode().equals("penalty_missed");
        if(!isPenalty){
            //点球大战非射进射失事件,不处理
            return;
        }
        //0.计数加1
        if("home".equals(data.getHomeAway())||"away".equals(data.getHomeAway())){
            pointNum++;
        }else {
            return;
        }
        if(pointNum==1){
            shootFirst=data.getHomeAway();
        }
        //1.计算局数
        firstNum= pointNum/2+pointNum%2;
        //2.根据局数得到当前局数比分
        CommonItem commonItem=roundScores.get(firstNum.toString().trim());
        if(commonItem==null){
            commonItem=new CommonItem(-1,-1,true);
            roundScores.put(firstNum.toString().trim(),commonItem);
        }
        log.info("::{}::点球大战比分：eventCode={}",data.getLinkId(),data.getEventCode()+",item="+commonItem+"firstNum:"+firstNum+",roundScores="+roundScores+",pointNum="+pointNum);
        if("goal".equals(data.getEventCode())){
            if(round5Scores==null){
                round5Scores = new CommonItem();
            }
            if("home".equals(data.getHomeAway())){
                commonItem.setHome(1);
                if(firstNum<=5){
                    if(round5Scores.getHome()==null){
                        round5Scores.setHome(0);
                    }
                    round5Scores.setHome(round5Scores.getHome()+1);
                }
            }else if("away".equals(data.getHomeAway())){
                commonItem.setAway(1);
                if(firstNum<=5){
                    if(round5Scores.getAway()==null){
                        round5Scores.setAway(0);
                    }
                    round5Scores.setAway(round5Scores.getAway()+1);
                }
            }
        }else if ("penalty_missed".equals(data.getEventCode())){
            log.info("::{}::点球大战比分3：eventCode={}",data.getLinkId(),data.getEventCode()+",item="+commonItem+"firstNum:"+firstNum+",roundScores="+roundScores);
            if(penaltyMiss == null){
                penaltyMiss = new CommonItem();
            }
            if("home".equals(data.getHomeAway())){
                commonItem.setHome(0);
                penaltyMiss.setHome(penaltyMiss.getHome()+1);
            }else if("away".equals(data.getHomeAway())){
                commonItem.setAway(0);
                penaltyMiss.setAway(penaltyMiss.getAway()+1);
            }
            log.info("::{}::点球大战比分4：eventCode={}",data.getLinkId(),data.getEventCode()+",item="+commonItem+"firstNum:"+firstNum+",roundScores="+roundScores);
        }
        roundScores.put(firstNum.toString().trim(),commonItem);
        log.info("::{}::点球大战比分5：eventCode={}",data.getLinkId(),data.getEventCode()+",item="+commonItem+"firstNum:"+firstNum+",roundScores="+roundScores);


    }
    /**
     * 删除事件进球比分
     * */
    public void cancelCalutionPenaltyScores(MatchEventInfo data) {
        if(data.getHomeAway().equals("home")||data.getHomeAway().equals("away")){
        }else {
            return;
        }
        log.info("删除点球进球或者点球未进，score-center:linkId：{}，firstNum={},pointNum:{},前5轮比分：{},每局比分：{}",data.getLinkId(),firstNum,pointNum,round5Scores,roundScores);
        pointNum--;
        //1.计算局数
        //2.根据局数得到当前局数比分
        CommonItem commonItem=roundScores.get(firstNum.toString().trim());
        if(commonItem==null){
            commonItem=new CommonItem();
            roundScores.put(firstNum.toString().trim(),commonItem);
        }
        //3.根据 home away 设置当前比分
        //4.设置前五句比分
        if(data.getHomeAway().equals("home")){
            if(data.getEventCode().equals("goal") || data.getEventCode().equals("penalty_missed")){
                commonItem.setHome(-1);
                if(firstNum<=5){
                    round5Scores.setHome(round5Scores.getHome()-1);
                    if(round5Scores.getHome()<0){
                        round5Scores.setHome(0);
                    }
                }
            }
        }else{
            if(data.getEventCode().equals("goal") || data.getEventCode().equals("penalty_missed")){
                commonItem.setAway(-1);
                round5Scores.setAway(round5Scores.getAway()-1);
                if(round5Scores.getAway()<0){
                    round5Scores.setAway(0);
                }
            }
        }
        log.info("2删除点球cancelCalutionPenaltyScores ，linkId::{},前5轮比分：{},每局比分：{}",data.getLinkId(),round5Scores,roundScores);
        pointNum--;
        firstNum--;
        log.info("::{}::删除事件点球大战比分：eventCode={}",data.getLinkId(),data.getEventCode()+",item="+commonItem+"firstNum:"+firstNum+"，pointNum="+pointNum+",roundScores="+roundScores);

    }
    /**
     * 进球或者没进
     * */
    public void calutionPenaltyScores(MatchSettleEvent data) {
        if(!("goal".equals(data.getEventCode())||"penalty_missed".equals(data.getEventCode()))){
            return;
        }
        if(!("home".equals(data.getHomeAway())||"away".equals(data.getHomeAway()))){
            return;
        }
        //1、当前局数
        Integer firstNum = data.getFirstNum();
        //2.根据局数得到当前局数比分
        CommonItem commonItem=roundScores.get(firstNum.toString());
        if(commonItem==null){
            return;
        }
        //3.根据 home away 设置当前比分
        //4.设置前五局比分
        if("home".equals(data.getHomeAway())){
            if("1".equals(data.getExtryInfo())){
                commonItem.setHome(1);
            }else if("0".equals(data.getExtryInfo())){
                commonItem.setHome(0);
            }
        }else{
            if("1".equals(data.getExtryInfo())){
                commonItem.setAway(1);
            }else if("0".equals(data.getExtryInfo())){
                commonItem.setAway(0);
            }
        }
    }

    /**
     *  根据每轮比分计算前5轮，根据前5轮计算总比分
     *  返回总比分
     * */
    public CommonItem editRoundsScoreByRounds() {
        CommonItem allRoundsScore = new CommonItem();
        CommonItem rounds5Score = new CommonItem();
        Integer homePenaltyMiss = 0;
        Integer awayPenaltyMiss = 0;
        try {
            for ( Map.Entry<String, CommonItem> entry : roundScores.entrySet()) {
                Integer round = Integer.parseInt(entry.getKey());
                Integer homeVal = entry.getValue().getHome() == null ? 0 : entry.getValue().getHome();
                Integer awayVal = entry.getValue().getAway() == null ? 0 : entry.getValue().getAway();
                if( round <= 5 ){
                    rounds5Score.setHome( rounds5Score.getHome() + homeVal);
                    rounds5Score.setAway( rounds5Score.getAway() + awayVal);
                }
                allRoundsScore.setHome( allRoundsScore.getHome() + homeVal);
                allRoundsScore.setAway( allRoundsScore.getAway() + awayVal);
                log.info("计算点球射失：当前比分：{},{}，历史比分：{}，{}", entry.getValue().getHome(), entry.getValue().getAway(), homePenaltyMiss,awayPenaltyMiss);
                if( entry.getValue().getHome() != null && entry.getValue().getHome() == 0 ){
                    homePenaltyMiss += 1;
                }
                if ( entry.getValue().getAway() != null && entry.getValue().getAway() == 0 ) {
                    awayPenaltyMiss += 1;
                }
                log.info("计算点球射失：当前比分：{},{}，历史比分：{}，{}", entry.getValue().getHome(), entry.getValue().getAway(), homePenaltyMiss,awayPenaltyMiss);
            }
        } catch (Exception e){
            log.error(":处理数据发生异常:", e);
        }
        log.info("计算点球射失：当前比分：this.setPenaltyMiss={}", this.getPenaltyMiss());
        this.setPenaltyMiss(new CommonItem( homePenaltyMiss, awayPenaltyMiss,true));
        log.info("计算点球射失：计算后比分：this.setPenaltyMiss={}", this.getPenaltyMiss());
        this.setRound5Scores(rounds5Score);
        return allRoundsScore;
    }

    /**
     * 新增轮次
     */
    public void addPenaltyRounds() {
        Integer size = roundScores.size();
        size ++;
        roundScores.put( size.toString(), new CommonItem(null,null,true));
    }
}
