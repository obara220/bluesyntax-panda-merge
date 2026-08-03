package com.panda.merge.volleyball.dto;

import com.panda.merge.annotation.ScoresProperty;
import com.panda.merge.constant.TeamTypeConstant;
import com.panda.merge.constant.VolleyballEventTypeEnum;
import com.panda.merge.dto.AbstractSportScores;
import com.panda.merge.dto.CommonItem;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * 排球报球板（PA）比分模型：以局粒度存到 MatchScoresInfo.scoresJson 内。
 *
 * 全场聚合记录在 -1 这个 key 上，单局比分记录在该局的 periodId 下。
 *
 * 注意：本类与旧版 {@link com.panda.merge.dto.VolleyballScores} 在不同包下并存。
 * 旧版仍被 VolleyballCalculationServiceImpl / ThirdScoringScoresConsumer 等计算链路引用，
 * 本类仅服务于报球板（PA）相关的 MatchVolleyballServiceImpl。
 */
@Data
@Slf4j
public class VolleyballV2Scores extends AbstractSportScores {

    @ScoresProperty(eventName = "盘比分", eventCode = {"match_score"})
    private CommonItem matchScore;

    @ScoresProperty(eventName = "局比分", eventCode = {"set_score"})
    private CommonItem setScore;

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

    public VolleyballV2Scores() {
        super.init(this);
    }

    /**
     * 一次事件 → 比分变更：
     * 1) 统计字段计在「事件持有人」homeAway（不论 opposite）：例如 home 发球失误，serviceError.home += 1，
     *    记录「谁做了这个动作」；
     * 2) 局比分（setScore）按 opposite 决定加在哪一方：opposite=false → homeAway 本方加分（如 serve_score / winning）；
     *    opposite=true → 对方加分（如 serve_error / out，得分给对手）。
     *
     * 这样像「赢分（volleyball_score_change）」这类没有独立计数字段、但仍要计入局比分的事件也能正确处理。
     *
     * 与斯诺克的语义差异：snooker 的 redFoulScore 等字段存的是「分值」，所以 opposite 时连字段一起转到对手；
     * 排球的 serviceError / out 等字段存的是「次数」，应当永远计在做出动作的那一方。
     */
    public void doCalculation(VolleyballEventTypeEnum type, String homeAway, Boolean isDelete) {
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
                    // 统计字段永远计在事件持有人这一侧（不做 opposite 翻转）
                    if (TeamTypeConstant.AWAY.equals(homeAway)) {
                        item.setAway((item.getAway() == null ? 0 : item.getAway()) + delta);
                    } else {
                        item.setHome((item.getHome() == null ? 0 : item.getHome()) + delta);
                    }
                }
            } catch (IllegalAccessException e) {
                log.error("[VolleyballV2Scores]doCalculation error", e);
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
                log.error("[VolleyballV2Scores]getFieldScoreByEventCode error", e);
            }
        }
        return null;
    }
}
