package com.panda.merge.tabletennis.dto;

import com.panda.merge.annotation.ScoresProperty;
import com.panda.merge.constant.TeamTypeConstant;
import com.panda.merge.constant.TableTennisEventTypeEnum;
import com.panda.merge.dto.AbstractSportScores;
import com.panda.merge.dto.CommonItem;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * 乒乓球报球板（PA）比分模型：以局粒度存到 MatchScoresInfo.scoresJson 内。
 *
 * 全场聚合记录在 -1 这个 key 上，单局比分记录在该局的 periodId 下。
 *
 * 本类仅服务于报球板（PA）相关的 MatchTableTennisServiceImpl。
 * 已有的 TableTennisScores（dto 包）仍被计算链路引用，不要混淆。
 */
@Data
@Slf4j
public class TableTennisV2Scores extends AbstractSportScores {

    @ScoresProperty(eventName = "盘比分", eventCode = {"match_score"})
    private CommonItem matchScore;

    @ScoresProperty(eventName = "局比分", eventCode = {"set_score"})
    private CommonItem setScore;

    @ScoresProperty(eventName = "发球次数", eventCode = {"current_serve_tabletennis"})
    private CommonItem serve;

    @ScoresProperty(eventName = "先发球", eventCode = {"which_team_serves_first"})
    private CommonItem kickoff;

    @ScoresProperty(eventName = "重新发球", eventCode = {"re_serve"})
    private CommonItem reServe;

    @ScoresProperty(eventName = "黄牌", eventCode = {"yellow_card"})
    private CommonItem yellowCard;

    @ScoresProperty(eventName = "红牌", eventCode = {"red_card"})
    private CommonItem redCard;

    @ScoresProperty(eventName = "加速模式", eventCode = {"expedite_mode"})
    private CommonItem expediteMode;

    @ScoresProperty(eventName = "红黄牌同手", eventCode = {"yellowred_card_same_hand"})
    private CommonItem yellowRedCardSameHand;

    public TableTennisV2Scores() {
        super.init(this);
    }

    /**
     * 一次事件 → 比分变更：
     * 1) 统计字段计在「事件持有人」homeAway（不论 opposite）；
     * 2) 局比分（setScore）按 opposite 决定加在哪一方：opposite=false → homeAway 本方加分；
     *    opposite=true → 对方加分（如红牌 red_card）。
     */
    public void doCalculation(TableTennisEventTypeEnum type, String homeAway, Boolean isDelete) {
        if (type == null || homeAway == null) {
            return;
        }
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(ScoresProperty.class)) {
                continue;
            }
            ScoresProperty meta = field.getAnnotation(ScoresProperty.class);
            try {
                for (String code : meta.eventCode()) {
                    if (!code.equals(type.getEventCode())) {
                        continue;
                    }
                    Integer pure = type.getScore() != null ? type.getScore() : 1;
                    Integer delta = Boolean.TRUE.equals(isDelete) ? -pure : pure;
                    CommonItem item = (CommonItem) field.get(this);
                    if (item == null) {
                        item = new CommonItem();
                        field.set(this, item);
                    }
                    if (TeamTypeConstant.AWAY.equals(homeAway)) {
                        item.setAway((item.getAway() == null ? 0 : item.getAway()) + delta);
                    } else {
                        item.setHome((item.getHome() == null ? 0 : item.getHome()) + delta);
                    }
                }
            } catch (IllegalAccessException e) {
                log.error("[TableTennisV2Scores]doCalculation error", e);
            }
        }

        if (type.getScore() != null && !"set_score".equals(type.getEventCode()) && setScore != null) {
            Integer delta = Boolean.TRUE.equals(isDelete) ? -type.getScore() : type.getScore();
            String pointSide = Boolean.TRUE.equals(type.getOpposite())
                    ? (TeamTypeConstant.HOME.equals(homeAway) ? TeamTypeConstant.AWAY : TeamTypeConstant.HOME)
                    : homeAway;
            if (TeamTypeConstant.AWAY.equals(pointSide)) {
                setScore.setAway((setScore.getAway() == null ? 0 : setScore.getAway()) + delta);
            } else {
                setScore.setHome((setScore.getHome() == null ? 0 : setScore.getHome()) + delta);
            }
        }
    }

    /**
     * 找到 eventCode 对应的统计字段。配合 {@link #doCalculation} 用于读取删除前的旧值做兜底校验。
     */
    public CommonItem getFieldScoreByEventCode(String eventCode) {
        if (eventCode == null) {
            return null;
        }
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(ScoresProperty.class)) {
                continue;
            }
            ScoresProperty meta = field.getAnnotation(ScoresProperty.class);
            try {
                for (String code : meta.eventCode()) {
                    if (eventCode.equals(code)) {
                        return (CommonItem) field.get(this);
                    }
                }
            } catch (IllegalAccessException e) {
                log.error("[TableTennisV2Scores]getFieldScoreByEventCode error", e);
            }
        }
        return null;
    }
}
