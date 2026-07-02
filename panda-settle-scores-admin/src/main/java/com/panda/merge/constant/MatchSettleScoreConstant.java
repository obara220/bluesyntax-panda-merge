package com.panda.merge.constant;


public class MatchSettleScoreConstant {
    public static class MatchSettleScoreStatus{
        /**
         *
         * 未编辑 0
         * */
        public static Integer NOT_EDIT=0;
        /**
         *
         * 未确认 1
         * */
        public static Integer NOT_CONFIRM=1;
        /**
         * 已确认
         * */
        public static Integer CONFIRM=2;
        /**
         * 已结算
         * */
        public static Integer SETTLED=3;
    }

    public static class MatchSettleOperateType{
        /**
         *
         * 1 结算
         * */
        public static Integer SETTLE=1;
        /**
         *
         * 2 回滚结算
         * */
        public static Integer ROLL_BACK=2;
        /**
         * 3 重新结算
         * */
        public static Integer RE_SETTLE=3;

    }
    public static class CacheKey{
        /**
         * 对比比分
         * */
        public static String COMPARE_SCORES_KEY="COMPARE_SCORES_KEY:";
        /**
         * 对比事件
         * */
        public static String COMPARE_EVENT_KEY="COMPARE_EVENT_KEY:";
    }

}
