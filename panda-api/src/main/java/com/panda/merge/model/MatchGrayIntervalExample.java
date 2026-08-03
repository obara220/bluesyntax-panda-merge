package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class MatchGrayIntervalExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MatchGrayIntervalExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andDataSourceCodeIsNull() {
            addCriterion("data_source_code is null");
            return (Criteria) this;
        }

        public Criteria andDataSourceCodeIsNotNull() {
            addCriterion("data_source_code is not null");
            return (Criteria) this;
        }

        public Criteria andDataSourceCodeEqualTo(String value) {
            addCriterion("data_source_code =", value, "dataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDataSourceCodeNotEqualTo(String value) {
            addCriterion("data_source_code <>", value, "dataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDataSourceCodeGreaterThan(String value) {
            addCriterion("data_source_code >", value, "dataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDataSourceCodeGreaterThanOrEqualTo(String value) {
            addCriterion("data_source_code >=", value, "dataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDataSourceCodeLessThan(String value) {
            addCriterion("data_source_code <", value, "dataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDataSourceCodeLessThanOrEqualTo(String value) {
            addCriterion("data_source_code <=", value, "dataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDataSourceCodeLike(String value) {
            addCriterion("data_source_code like", value, "dataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDataSourceCodeNotLike(String value) {
            addCriterion("data_source_code not like", value, "dataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDataSourceCodeIn(List<String> values) {
            addCriterion("data_source_code in", values, "dataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDataSourceCodeNotIn(List<String> values) {
            addCriterion("data_source_code not in", values, "dataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDataSourceCodeBetween(String value1, String value2) {
            addCriterion("data_source_code between", value1, value2, "dataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDataSourceCodeNotBetween(String value1, String value2) {
            addCriterion("data_source_code not between", value1, value2, "dataSourceCode");
            return (Criteria) this;
        }

        public Criteria andTournamentLevelIsNull() {
            addCriterion("tournament_level is null");
            return (Criteria) this;
        }

        public Criteria andTournamentLevelIsNotNull() {
            addCriterion("tournament_level is not null");
            return (Criteria) this;
        }

        public Criteria andTournamentLevelEqualTo(Integer value) {
            addCriterion("tournament_level =", value, "tournamentLevel");
            return (Criteria) this;
        }

        public Criteria andTournamentLevelNotEqualTo(Integer value) {
            addCriterion("tournament_level <>", value, "tournamentLevel");
            return (Criteria) this;
        }

        public Criteria andTournamentLevelGreaterThan(Integer value) {
            addCriterion("tournament_level >", value, "tournamentLevel");
            return (Criteria) this;
        }

        public Criteria andTournamentLevelGreaterThanOrEqualTo(Integer value) {
            addCriterion("tournament_level >=", value, "tournamentLevel");
            return (Criteria) this;
        }

        public Criteria andTournamentLevelLessThan(Integer value) {
            addCriterion("tournament_level <", value, "tournamentLevel");
            return (Criteria) this;
        }

        public Criteria andTournamentLevelLessThanOrEqualTo(Integer value) {
            addCriterion("tournament_level <=", value, "tournamentLevel");
            return (Criteria) this;
        }

        public Criteria andTournamentLevelIn(List<Integer> values) {
            addCriterion("tournament_level in", values, "tournamentLevel");
            return (Criteria) this;
        }

        public Criteria andTournamentLevelNotIn(List<Integer> values) {
            addCriterion("tournament_level not in", values, "tournamentLevel");
            return (Criteria) this;
        }

        public Criteria andTournamentLevelBetween(Integer value1, Integer value2) {
            addCriterion("tournament_level between", value1, value2, "tournamentLevel");
            return (Criteria) this;
        }

        public Criteria andTournamentLevelNotBetween(Integer value1, Integer value2) {
            addCriterion("tournament_level not between", value1, value2, "tournamentLevel");
            return (Criteria) this;
        }

        public Criteria andMin15GoalIsNull() {
            addCriterion("min15_goal is null");
            return (Criteria) this;
        }

        public Criteria andMin15GoalIsNotNull() {
            addCriterion("min15_goal is not null");
            return (Criteria) this;
        }

        public Criteria andMin15GoalEqualTo(Integer value) {
            addCriterion("min15_goal =", value, "min15Goal");
            return (Criteria) this;
        }

        public Criteria andMin15GoalNotEqualTo(Integer value) {
            addCriterion("min15_goal <>", value, "min15Goal");
            return (Criteria) this;
        }

        public Criteria andMin15GoalGreaterThan(Integer value) {
            addCriterion("min15_goal >", value, "min15Goal");
            return (Criteria) this;
        }

        public Criteria andMin15GoalGreaterThanOrEqualTo(Integer value) {
            addCriterion("min15_goal >=", value, "min15Goal");
            return (Criteria) this;
        }

        public Criteria andMin15GoalLessThan(Integer value) {
            addCriterion("min15_goal <", value, "min15Goal");
            return (Criteria) this;
        }

        public Criteria andMin15GoalLessThanOrEqualTo(Integer value) {
            addCriterion("min15_goal <=", value, "min15Goal");
            return (Criteria) this;
        }

        public Criteria andMin15GoalIn(List<Integer> values) {
            addCriterion("min15_goal in", values, "min15Goal");
            return (Criteria) this;
        }

        public Criteria andMin15GoalNotIn(List<Integer> values) {
            addCriterion("min15_goal not in", values, "min15Goal");
            return (Criteria) this;
        }

        public Criteria andMin15GoalBetween(Integer value1, Integer value2) {
            addCriterion("min15_goal between", value1, value2, "min15Goal");
            return (Criteria) this;
        }

        public Criteria andMin15GoalNotBetween(Integer value1, Integer value2) {
            addCriterion("min15_goal not between", value1, value2, "min15Goal");
            return (Criteria) this;
        }

        public Criteria andMin15CornerIsNull() {
            addCriterion("min15_corner is null");
            return (Criteria) this;
        }

        public Criteria andMin15CornerIsNotNull() {
            addCriterion("min15_corner is not null");
            return (Criteria) this;
        }

        public Criteria andMin15CornerEqualTo(Integer value) {
            addCriterion("min15_corner =", value, "min15Corner");
            return (Criteria) this;
        }

        public Criteria andMin15CornerNotEqualTo(Integer value) {
            addCriterion("min15_corner <>", value, "min15Corner");
            return (Criteria) this;
        }

        public Criteria andMin15CornerGreaterThan(Integer value) {
            addCriterion("min15_corner >", value, "min15Corner");
            return (Criteria) this;
        }

        public Criteria andMin15CornerGreaterThanOrEqualTo(Integer value) {
            addCriterion("min15_corner >=", value, "min15Corner");
            return (Criteria) this;
        }

        public Criteria andMin15CornerLessThan(Integer value) {
            addCriterion("min15_corner <", value, "min15Corner");
            return (Criteria) this;
        }

        public Criteria andMin15CornerLessThanOrEqualTo(Integer value) {
            addCriterion("min15_corner <=", value, "min15Corner");
            return (Criteria) this;
        }

        public Criteria andMin15CornerIn(List<Integer> values) {
            addCriterion("min15_corner in", values, "min15Corner");
            return (Criteria) this;
        }

        public Criteria andMin15CornerNotIn(List<Integer> values) {
            addCriterion("min15_corner not in", values, "min15Corner");
            return (Criteria) this;
        }

        public Criteria andMin15CornerBetween(Integer value1, Integer value2) {
            addCriterion("min15_corner between", value1, value2, "min15Corner");
            return (Criteria) this;
        }

        public Criteria andMin15CornerNotBetween(Integer value1, Integer value2) {
            addCriterion("min15_corner not between", value1, value2, "min15Corner");
            return (Criteria) this;
        }

        public Criteria andMin15BookingsIsNull() {
            addCriterion("min15_bookings is null");
            return (Criteria) this;
        }

        public Criteria andMin15BookingsIsNotNull() {
            addCriterion("min15_bookings is not null");
            return (Criteria) this;
        }

        public Criteria andMin15BookingsEqualTo(Integer value) {
            addCriterion("min15_bookings =", value, "min15Bookings");
            return (Criteria) this;
        }

        public Criteria andMin15BookingsNotEqualTo(Integer value) {
            addCriterion("min15_bookings <>", value, "min15Bookings");
            return (Criteria) this;
        }

        public Criteria andMin15BookingsGreaterThan(Integer value) {
            addCriterion("min15_bookings >", value, "min15Bookings");
            return (Criteria) this;
        }

        public Criteria andMin15BookingsGreaterThanOrEqualTo(Integer value) {
            addCriterion("min15_bookings >=", value, "min15Bookings");
            return (Criteria) this;
        }

        public Criteria andMin15BookingsLessThan(Integer value) {
            addCriterion("min15_bookings <", value, "min15Bookings");
            return (Criteria) this;
        }

        public Criteria andMin15BookingsLessThanOrEqualTo(Integer value) {
            addCriterion("min15_bookings <=", value, "min15Bookings");
            return (Criteria) this;
        }

        public Criteria andMin15BookingsIn(List<Integer> values) {
            addCriterion("min15_bookings in", values, "min15Bookings");
            return (Criteria) this;
        }

        public Criteria andMin15BookingsNotIn(List<Integer> values) {
            addCriterion("min15_bookings not in", values, "min15Bookings");
            return (Criteria) this;
        }

        public Criteria andMin15BookingsBetween(Integer value1, Integer value2) {
            addCriterion("min15_bookings between", value1, value2, "min15Bookings");
            return (Criteria) this;
        }

        public Criteria andMin15BookingsNotBetween(Integer value1, Integer value2) {
            addCriterion("min15_bookings not between", value1, value2, "min15Bookings");
            return (Criteria) this;
        }

        public Criteria andMin5GoalIsNull() {
            addCriterion("min5_goal is null");
            return (Criteria) this;
        }

        public Criteria andMin5GoalIsNotNull() {
            addCriterion("min5_goal is not null");
            return (Criteria) this;
        }

        public Criteria andMin5GoalEqualTo(Integer value) {
            addCriterion("min5_goal =", value, "min5Goal");
            return (Criteria) this;
        }

        public Criteria andMin5GoalNotEqualTo(Integer value) {
            addCriterion("min5_goal <>", value, "min5Goal");
            return (Criteria) this;
        }

        public Criteria andMin5GoalGreaterThan(Integer value) {
            addCriterion("min5_goal >", value, "min5Goal");
            return (Criteria) this;
        }

        public Criteria andMin5GoalGreaterThanOrEqualTo(Integer value) {
            addCriterion("min5_goal >=", value, "min5Goal");
            return (Criteria) this;
        }

        public Criteria andMin5GoalLessThan(Integer value) {
            addCriterion("min5_goal <", value, "min5Goal");
            return (Criteria) this;
        }

        public Criteria andMin5GoalLessThanOrEqualTo(Integer value) {
            addCriterion("min5_goal <=", value, "min5Goal");
            return (Criteria) this;
        }

        public Criteria andMin5GoalIn(List<Integer> values) {
            addCriterion("min5_goal in", values, "min5Goal");
            return (Criteria) this;
        }

        public Criteria andMin5GoalNotIn(List<Integer> values) {
            addCriterion("min5_goal not in", values, "min5Goal");
            return (Criteria) this;
        }

        public Criteria andMin5GoalBetween(Integer value1, Integer value2) {
            addCriterion("min5_goal between", value1, value2, "min5Goal");
            return (Criteria) this;
        }

        public Criteria andMin5GoalNotBetween(Integer value1, Integer value2) {
            addCriterion("min5_goal not between", value1, value2, "min5Goal");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Long value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Long value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Long value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Long value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Long value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Long> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Long> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Long value1, Long value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Long value1, Long value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeIsNull() {
            addCriterion("modify_time is null");
            return (Criteria) this;
        }

        public Criteria andModifyTimeIsNotNull() {
            addCriterion("modify_time is not null");
            return (Criteria) this;
        }

        public Criteria andModifyTimeEqualTo(Long value) {
            addCriterion("modify_time =", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeNotEqualTo(Long value) {
            addCriterion("modify_time <>", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeGreaterThan(Long value) {
            addCriterion("modify_time >", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("modify_time >=", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeLessThan(Long value) {
            addCriterion("modify_time <", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeLessThanOrEqualTo(Long value) {
            addCriterion("modify_time <=", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeIn(List<Long> values) {
            addCriterion("modify_time in", values, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeNotIn(List<Long> values) {
            addCriterion("modify_time not in", values, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeBetween(Long value1, Long value2) {
            addCriterion("modify_time between", value1, value2, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeNotBetween(Long value1, Long value2) {
            addCriterion("modify_time not between", value1, value2, "modifyTime");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}