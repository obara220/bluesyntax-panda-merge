package com.panda.merge.constant;

/**
 * 推送类型
 */
public enum SubscriptionTypeEnum {

    SCORE_PAGE(30000,"页面关注比分"),
    SCORE_EVENT(30001,"页面关注的时间以及事件"),
    HEART(3,"心跳"),
    HEART_SUCCESS(20000,"心跳成功"),
    PD_MATCH_SCORE(30002,"PD足球比分版推送"),
    PD_MATCH_EVENT(30003,"PD足球事件推送"),
    PD_MATCH_SUB(30004,"PD赛事订阅"),
    PD_MATCH_CANCEL(30005,"PD赛事取消订阅"),
    SETTLE_MATCH_SCORES(30006,"结算比分推送"),
    SETTLE_MATCH_EVENT(30007,"结算事件推送"),
    SETTLE_MATCH_THIRD_SCORES(30008,"三方结算比分推送"),
    SETTLE_MATCH_THIRD_EVENT(30009,"三方结算事件推送"),
    SETTLE_MATCH_SUB(30010,"结算赛事订阅"),
    SETTLE_MATCH_CANCEL(30011,"结算赛事取消订阅"),
    SETTLE_MATCH_LIST_SUB(30012,"结算赛事列表订阅"),
    SETTLE_MATCH_LIST_CANCEL(30013,"结算赛事列表取消订阅"),
    SETTLE_MATCH_LIST_PUSH(30014,"结算赛事列表推送"),
    AUTO_SETTLE_DATA_SOURCE_SUB(30015,"数据商自动结算开关状态订阅"),
    AUTO_SETTLE_DATA_SOURCE_PUSH(30016,"数据商自动结算开关状态推送"),
    MATCH_SETTLE_ROLL_BACK_STATUS_PUSH(30017,"赛事回滚状态推送"),
    BASKETBALL_PERIOD_SCORES_PUSH(30018,"篮球阶段比分推送"),
    MATCH_STANDARD_SCORES_PUSH(30019,"标准比分推送"),
    SP_SETTLE_MATCH_PUSH(30020,"SP赛事级推送"),

    SCORE_NET_PUSH(30021,"比分网比分推送"),
    SCORE_NET_CANCEL(30022,"比分网比分取消推送"),
    CAOPAN_ONLINE_PUSH(30024,"操盘手在线状态推送"),
    DATASOURCE_CONNECTION_STATUS_PUSH(30025,"数据商连接状态推送")
    ;

    private Integer code;

    private String val;

    SubscriptionTypeEnum(Integer code, String val) {
        this.code = code;
        this.val = val;
    }

    public Integer getCode() {
        return code;
    }

    public String getVal() {
        return val;
    }
}
