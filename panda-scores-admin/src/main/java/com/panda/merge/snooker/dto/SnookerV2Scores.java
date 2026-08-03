package com.panda.merge.snooker.dto;


import com.panda.merge.annotation.ScoresProperty;
import com.panda.merge.constant.SnookerConstant;
import com.panda.merge.constant.SnookerEventTypeEnum;
import com.panda.merge.constant.TeamTypeConstant;
import com.panda.merge.dto.AbstractSportScores;
import com.panda.merge.dto.CommonFItem;
import com.panda.merge.dto.CommonItem;
import com.panda.merge.dto.CommonItemBigDecimal;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

@Data
@Slf4j
public class SnookerV2Scores extends AbstractSportScores {

    @ScoresProperty(eventName = "盘比分",eventCode ={"snooker_score_change","match_score","set_scores"})
    private CommonItem matchScore ;

    @ScoresProperty(eventName = "局比分", eventCode = {"set_score"})
    private CommonItem setScore ;

    @ScoresProperty(eventName = "赢分",eventCode ={"snooker_score_red","snooker_score_yellow","snooker_score_green","snooker_score_brown","snooker_score_blue","snooker_score_pink","snooker_score_black"})
    private CommonItem winingScore;

    @ScoresProperty(eventName = "红球赢分", eventCode ={"snooker_score_red"})
    private CommonItem redWiningScore;

    @ScoresProperty(eventName = "黄球赢分", eventCode ={"snooker_score_yellow"})
    private CommonItem yellowWiningScore;  // 注意：字段名已修正，与PDSnookerEventDto中的yellowWiningScore对应

    @ScoresProperty(eventName = "绿球赢分", eventCode ={"snooker_score_green"})
    private CommonItem greenWiningScore;

    @ScoresProperty(eventName = "棕球赢分", eventCode ={"snooker_score_brown"})
    private CommonItem brownWiningScore;

    @ScoresProperty(eventName = "蓝球赢分", eventCode ={"snooker_score_blue"})
    private CommonItem blueWiningScore;

    @ScoresProperty(eventName = "粉球赢分", eventCode ={"snooker_score_pink"})
    private CommonItem pinkWiningScore;

    @ScoresProperty(eventName = "黑球赢分", eventCode ={"snooker_score_black"})
    private CommonItem blackWiningScore;

    @ScoresProperty(eventName = "罚分",eventCode ={"snooker_foul_red","snooker_foul_yellow","snooker_foul_green","snooker_foul_brown","snooker_foul_blue","snooker_foul_pink","snooker_foul_black"})
    private CommonItem foulScore;
    @ScoresProperty(eventName = "红球罚分", eventCode ={"snooker_foul_red"})
    private CommonItem redFoulScore;

    @ScoresProperty(eventName = "黄球罚分", eventCode ={"snooker_foul_yellow"})
    private CommonItem yellowFoulScore;

    @ScoresProperty(eventName = "绿球罚分", eventCode ={"snooker_foul_green"})
    private CommonItem greenFoulScore;

    @ScoresProperty(eventName = "棕球罚分", eventCode ={"snooker_foul_brown"})
    private CommonItem brownFoulScore;
    @ScoresProperty(eventName = "蓝球罚分", eventCode ={"snooker_foul_blue"})
    private CommonItem blueFoulScore;

    @ScoresProperty(eventName = "粉球罚分", eventCode ={"snooker_foul_pink"})
    private CommonItem pinkFoulScore;
    @ScoresProperty(eventName = "黑球罚分", eventCode ={"snooker_foul_black"})
    private CommonItem blackFoulScore;
    @ScoresProperty(eventName = "持杆", eventCode = {"Striker"})
    private CommonItem strick;
    @ScoresProperty(eventName = "未进球",eventCode ={"nopot"})
    private CommonItem noPot;
    @ScoresProperty(eventName = "自由球得分",eventCode ={"free_ball_pot"})
    private CommonItem freeball;
    @ScoresProperty(eventName = "自由球未得分",eventCode ={"free_ball_nopot"})
    private CommonItem freeballNoPot;
    @ScoresProperty(eventName = "开球",eventCode ={"which_team_serves_first"})
    private CommonItem kickoff;

    @ScoresProperty(eventName = "重排",eventCode ={"rerack"})
    private CommonItem rerack;
    @ScoresProperty(eventName = "犯规罚4",eventCode ={"snooker_foul_4"})
    private CommonItem penaltyFour;

    @ScoresProperty(eventName = "犯规罚7",eventCode ={"snooker_foul_7"})
    private CommonItem penaltySeven;

    @ScoresProperty(eventName = "其他犯规")
    private CommonItem penaltyOther;

    /** 认输（snooker_concede），与 {@link com.panda.merge.dto.advertise.v2.PDSnookerEventDto#getConcede()} 一致 */
    @ScoresProperty(eventName = "认输", eventCode = {"snooker_concede"})
    private CommonItem concede;

    /** 直接判输（snooker_foul_lose），与 {@link com.panda.merge.dto.advertise.v2.PDSnookerEventDto#getDirectLoss()} 一致 */
    @ScoresProperty(eventName = "直接判输", eventCode = {"snooker_foul_lose"})
    private CommonItem directLoss;

    @ScoresProperty(eventName = "犯规次数",eventCode ={"snooker_foul"})
    private CommonItem snookerFoul ;

    @ScoresProperty(eventName = "单杆最高")
    private CommonItem  highestSingleShot ;

    public SnookerV2Scores( ) {
        super.init(this);
    }

    public void doCalculation(SnookerEventTypeEnum snookerEventTypeEnum, String homeAway, Boolean isDelete) {
        Field[] fields = this.getClass().getDeclaredFields();
        boolean needUpdateSetScore = false;
        Integer setScoreValue = null;
        String setScoreHomeAway = null;
        
        for (Field field : fields) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(ScoresProperty.class)) {
                continue;
            }
            ScoresProperty item = field.getAnnotation(ScoresProperty.class);
            try {
                for (String code : item.eventCode()) {
                    if (code.equals(snookerEventTypeEnum.getEventCode())) {
                        // nopot：不累计分项、不改局分
                        // free_ball_nopot：比分为 0（enum score=null）→ pureScore=1 只累加 freeballNoPot 次数，不触发下方 needUpdateSetScore
                        if (SnookerEventTypeEnum.NO_POT.getEventCode().equals(snookerEventTypeEnum.getEventCode())) {
                            continue;
                        }
                        
                        Integer pureScore = snookerEventTypeEnum.getScore() != null ? snookerEventTypeEnum.getScore() : 1;
                        Integer score = isDelete ? -1*pureScore : pureScore;

                        String currentHomeAway = homeAway;
                        if (snookerEventTypeEnum.getOpposite()) {
                            currentHomeAway = TeamTypeConstant.HOME.equals(homeAway) ? TeamTypeConstant.AWAY : TeamTypeConstant.HOME;
                        }
                        CommonItem CommonItem = (com.panda.merge.dto.CommonItem)field.get(this);
                        if(currentHomeAway.equals(TeamTypeConstant.AWAY)){
                            CommonItem.setAway(CommonItem.getAway()+score);
                        }else {
                            CommonItem.setHome(CommonItem.getHome()+score);
                        }
                        // 记录需要更新setScore的信息（只记录一次，避免重复计算）
                        if (snookerEventTypeEnum.getScore() != null 
                                && !"set_score".equals(snookerEventTypeEnum.getEventCode()) 
                                && !needUpdateSetScore) {
                            needUpdateSetScore = true;
                            setScoreValue = score;
                            setScoreHomeAway = currentHomeAway;
                        }
                    }
                }
            }catch (IllegalAccessException e) {
                log.error("doCalculation,处理异常,Exception:", e);
            }
        }

        // 罚4/罚7：分值已在对应 penalty 字段按得分方累计；「犯规次数」单独记在 snookerFoul 的犯规方（homeAway），与罚4/罚7分项解耦
        if (SnookerEventTypeEnum.PENALTY_FOUR.getEventCode().equals(snookerEventTypeEnum.getEventCode())
                || SnookerEventTypeEnum.PENALTY_SEVEN.getEventCode().equals(snookerEventTypeEnum.getEventCode())) {
            String foulingSide = homeAway == null ? TeamTypeConstant.HOME : homeAway;
            CommonItem foulCnt = getFieldScoreByEventCode("snooker_foul");
            if (foulCnt != null) {
                int delta = Boolean.TRUE.equals(isDelete) ? -1 : 1;
                if (TeamTypeConstant.AWAY.equals(foulingSide)) {
                    int a = foulCnt.getAway() != null ? foulCnt.getAway() : 0;
                    foulCnt.setAway(Math.max(0, a + delta));
                } else {
                    int h = foulCnt.getHome() != null ? foulCnt.getHome() : 0;
                    foulCnt.setHome(Math.max(0, h + delta));
                }
            }
        }
        
        // 在循环外统一更新setScore，确保只更新一次
        if (needUpdateSetScore && setScoreValue != null && setScoreHomeAway != null) {
            CommonItem setScore = getFieldScoreByEventCode("set_score");
            if(setScore != null) {
                if(setScoreHomeAway.equals(TeamTypeConstant.AWAY)){
                    setScore.setAway(setScore.getAway()+setScoreValue);
                }else {
                    setScore.setHome(setScore.getHome()+setScoreValue);
                }
            }
        }
    }

    public CommonItem getFieldScoreByEventCode(String eventCode) {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(ScoresProperty.class)) {
                continue;
            }
            ScoresProperty item = field.getAnnotation(ScoresProperty.class);
            try {
                for (String code : item.eventCode()) {
                    if (code.equals(eventCode)) {
                        return (com.panda.merge.dto.CommonItem)field.get(this);
                    }
                }
            }catch (IllegalAccessException e) {
                log.error("doCalculation,处理异常,Exception:", e);
            }
        }
        return null;
    }

    public void updateHighestSingleShot(SnookerEventTypeEnum snookerEventTypeEnum, String homeAway, CommonItem curScoreItem, Boolean updateScore) {
        if(homeAway.equals(TeamTypeConstant.HOME)){
            if (updateScore) {
                curScoreItem.setHome(curScoreItem.getHome()+snookerEventTypeEnum.getScore());
            }
            if(highestSingleShot.getHome()<curScoreItem.getHome()) {
                highestSingleShot.setHome(curScoreItem.getHome());
            }
        }
        if(homeAway.equals(TeamTypeConstant.AWAY)){
            if (updateScore) {
                curScoreItem.setAway(curScoreItem.getAway() + snookerEventTypeEnum.getScore());
            }
            if (highestSingleShot.getAway()<curScoreItem.getAway()) {
                highestSingleShot.setAway(curScoreItem.getAway());
            }
        }
    }

    public void changeWholeScores(SnookerV2Scores periodScores, boolean isRerack) {
        if (periodScores == null) {
            return;
        }
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            // 只处理被 @ScoresProperty 注解的字段
            if (!field.isAnnotationPresent(ScoresProperty.class)) {
                continue;
            }
            try {
                CommonItem wholeObj = (com.panda.merge.dto.CommonItem)field.get(this);
                CommonItem periodObj = (com.panda.merge.dto.CommonItem)field.get(periodScores);
                
                // 安全处理：如果wholeObj或periodObj为null，跳过该字段
                if (wholeObj == null || periodObj == null) {
                    log.warn("changeWholeScores: wholeObj or periodObj is null for field: {}", field.getName());
                    continue;
                }
                
                Integer periodHome = isRerack ? -1*periodObj.getHome() : periodObj.getHome();
                Integer periodAway = isRerack ? -1*periodObj.getAway() : periodObj.getAway();
                wholeObj.setHome(wholeObj.getHome()+periodHome);
                wholeObj.setAway(wholeObj.getAway()+periodAway);
            } catch (Exception e) {
                log.error("changeWholeScores error on field: " + field.getName(), e);
            }
        }
    }
}
