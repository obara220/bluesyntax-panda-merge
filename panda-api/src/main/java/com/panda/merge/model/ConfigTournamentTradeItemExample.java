package com.panda.merge.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ConfigTournamentTradeItemExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ConfigTournamentTradeItemExample() {
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

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andSportIdIsNull() {
            addCriterion("sport_id is null");
            return (Criteria) this;
        }

        public Criteria andSportIdIsNotNull() {
            addCriterion("sport_id is not null");
            return (Criteria) this;
        }

        public Criteria andSportIdEqualTo(Long value) {
            addCriterion("sport_id =", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdNotEqualTo(Long value) {
            addCriterion("sport_id <>", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdGreaterThan(Long value) {
            addCriterion("sport_id >", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdGreaterThanOrEqualTo(Long value) {
            addCriterion("sport_id >=", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdLessThan(Long value) {
            addCriterion("sport_id <", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdLessThanOrEqualTo(Long value) {
            addCriterion("sport_id <=", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdIn(List<Long> values) {
            addCriterion("sport_id in", values, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdNotIn(List<Long> values) {
            addCriterion("sport_id not in", values, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdBetween(Long value1, Long value2) {
            addCriterion("sport_id between", value1, value2, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdNotBetween(Long value1, Long value2) {
            addCriterion("sport_id not between", value1, value2, "sportId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdIsNull() {
            addCriterion("tournament_id is null");
            return (Criteria) this;
        }

        public Criteria andTournamentIdIsNotNull() {
            addCriterion("tournament_id is not null");
            return (Criteria) this;
        }

        public Criteria andTournamentIdEqualTo(Long value) {
            addCriterion("tournament_id =", value, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdNotEqualTo(Long value) {
            addCriterion("tournament_id <>", value, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdGreaterThan(Long value) {
            addCriterion("tournament_id >", value, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdGreaterThanOrEqualTo(Long value) {
            addCriterion("tournament_id >=", value, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdLessThan(Long value) {
            addCriterion("tournament_id <", value, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdLessThanOrEqualTo(Long value) {
            addCriterion("tournament_id <=", value, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdIn(List<Long> values) {
            addCriterion("tournament_id in", values, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdNotIn(List<Long> values) {
            addCriterion("tournament_id not in", values, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdBetween(Long value1, Long value2) {
            addCriterion("tournament_id between", value1, value2, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdNotBetween(Long value1, Long value2) {
            addCriterion("tournament_id not between", value1, value2, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andMatchTypeIsNull() {
            addCriterion("match_type is null");
            return (Criteria) this;
        }

        public Criteria andMatchTypeIsNotNull() {
            addCriterion("match_type is not null");
            return (Criteria) this;
        }

        public Criteria andMatchTypeEqualTo(Integer value) {
            addCriterion("match_type =", value, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeNotEqualTo(Integer value) {
            addCriterion("match_type <>", value, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeGreaterThan(Integer value) {
            addCriterion("match_type >", value, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("match_type >=", value, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeLessThan(Integer value) {
            addCriterion("match_type <", value, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeLessThanOrEqualTo(Integer value) {
            addCriterion("match_type <=", value, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeIn(List<Integer> values) {
            addCriterion("match_type in", values, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeNotIn(List<Integer> values) {
            addCriterion("match_type not in", values, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeBetween(Integer value1, Integer value2) {
            addCriterion("match_type between", value1, value2, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("match_type not between", value1, value2, "matchType");
            return (Criteria) this;
        }

        public Criteria andSpreadMaxOddsIsNull() {
            addCriterion("spread_max_odds is null");
            return (Criteria) this;
        }

        public Criteria andSpreadMaxOddsIsNotNull() {
            addCriterion("spread_max_odds is not null");
            return (Criteria) this;
        }

        public Criteria andSpreadMaxOddsEqualTo(BigDecimal value) {
            addCriterion("spread_max_odds =", value, "spreadMaxOdds");
            return (Criteria) this;
        }

        public Criteria andSpreadMaxOddsNotEqualTo(BigDecimal value) {
            addCriterion("spread_max_odds <>", value, "spreadMaxOdds");
            return (Criteria) this;
        }

        public Criteria andSpreadMaxOddsGreaterThan(BigDecimal value) {
            addCriterion("spread_max_odds >", value, "spreadMaxOdds");
            return (Criteria) this;
        }

        public Criteria andSpreadMaxOddsGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("spread_max_odds >=", value, "spreadMaxOdds");
            return (Criteria) this;
        }

        public Criteria andSpreadMaxOddsLessThan(BigDecimal value) {
            addCriterion("spread_max_odds <", value, "spreadMaxOdds");
            return (Criteria) this;
        }

        public Criteria andSpreadMaxOddsLessThanOrEqualTo(BigDecimal value) {
            addCriterion("spread_max_odds <=", value, "spreadMaxOdds");
            return (Criteria) this;
        }

        public Criteria andSpreadMaxOddsIn(List<BigDecimal> values) {
            addCriterion("spread_max_odds in", values, "spreadMaxOdds");
            return (Criteria) this;
        }

        public Criteria andSpreadMaxOddsNotIn(List<BigDecimal> values) {
            addCriterion("spread_max_odds not in", values, "spreadMaxOdds");
            return (Criteria) this;
        }

        public Criteria andSpreadMaxOddsBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("spread_max_odds between", value1, value2, "spreadMaxOdds");
            return (Criteria) this;
        }

        public Criteria andSpreadMaxOddsNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("spread_max_odds not between", value1, value2, "spreadMaxOdds");
            return (Criteria) this;
        }

        public Criteria andSpreadMinOddsIsNull() {
            addCriterion("spread_min_odds is null");
            return (Criteria) this;
        }

        public Criteria andSpreadMinOddsIsNotNull() {
            addCriterion("spread_min_odds is not null");
            return (Criteria) this;
        }

        public Criteria andSpreadMinOddsEqualTo(BigDecimal value) {
            addCriterion("spread_min_odds =", value, "spreadMinOdds");
            return (Criteria) this;
        }

        public Criteria andSpreadMinOddsNotEqualTo(BigDecimal value) {
            addCriterion("spread_min_odds <>", value, "spreadMinOdds");
            return (Criteria) this;
        }

        public Criteria andSpreadMinOddsGreaterThan(BigDecimal value) {
            addCriterion("spread_min_odds >", value, "spreadMinOdds");
            return (Criteria) this;
        }

        public Criteria andSpreadMinOddsGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("spread_min_odds >=", value, "spreadMinOdds");
            return (Criteria) this;
        }

        public Criteria andSpreadMinOddsLessThan(BigDecimal value) {
            addCriterion("spread_min_odds <", value, "spreadMinOdds");
            return (Criteria) this;
        }

        public Criteria andSpreadMinOddsLessThanOrEqualTo(BigDecimal value) {
            addCriterion("spread_min_odds <=", value, "spreadMinOdds");
            return (Criteria) this;
        }

        public Criteria andSpreadMinOddsIn(List<BigDecimal> values) {
            addCriterion("spread_min_odds in", values, "spreadMinOdds");
            return (Criteria) this;
        }

        public Criteria andSpreadMinOddsNotIn(List<BigDecimal> values) {
            addCriterion("spread_min_odds not in", values, "spreadMinOdds");
            return (Criteria) this;
        }

        public Criteria andSpreadMinOddsBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("spread_min_odds between", value1, value2, "spreadMinOdds");
            return (Criteria) this;
        }

        public Criteria andSpreadMinOddsNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("spread_min_odds not between", value1, value2, "spreadMinOdds");
            return (Criteria) this;
        }

        public Criteria andMarginMaxOddsIsNull() {
            addCriterion("margin_max_odds is null");
            return (Criteria) this;
        }

        public Criteria andMarginMaxOddsIsNotNull() {
            addCriterion("margin_max_odds is not null");
            return (Criteria) this;
        }

        public Criteria andMarginMaxOddsEqualTo(BigDecimal value) {
            addCriterion("margin_max_odds =", value, "marginMaxOdds");
            return (Criteria) this;
        }

        public Criteria andMarginMaxOddsNotEqualTo(BigDecimal value) {
            addCriterion("margin_max_odds <>", value, "marginMaxOdds");
            return (Criteria) this;
        }

        public Criteria andMarginMaxOddsGreaterThan(BigDecimal value) {
            addCriterion("margin_max_odds >", value, "marginMaxOdds");
            return (Criteria) this;
        }

        public Criteria andMarginMaxOddsGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("margin_max_odds >=", value, "marginMaxOdds");
            return (Criteria) this;
        }

        public Criteria andMarginMaxOddsLessThan(BigDecimal value) {
            addCriterion("margin_max_odds <", value, "marginMaxOdds");
            return (Criteria) this;
        }

        public Criteria andMarginMaxOddsLessThanOrEqualTo(BigDecimal value) {
            addCriterion("margin_max_odds <=", value, "marginMaxOdds");
            return (Criteria) this;
        }

        public Criteria andMarginMaxOddsIn(List<BigDecimal> values) {
            addCriterion("margin_max_odds in", values, "marginMaxOdds");
            return (Criteria) this;
        }

        public Criteria andMarginMaxOddsNotIn(List<BigDecimal> values) {
            addCriterion("margin_max_odds not in", values, "marginMaxOdds");
            return (Criteria) this;
        }

        public Criteria andMarginMaxOddsBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("margin_max_odds between", value1, value2, "marginMaxOdds");
            return (Criteria) this;
        }

        public Criteria andMarginMaxOddsNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("margin_max_odds not between", value1, value2, "marginMaxOdds");
            return (Criteria) this;
        }

        public Criteria andMarginMinOddsIsNull() {
            addCriterion("margin_min_odds is null");
            return (Criteria) this;
        }

        public Criteria andMarginMinOddsIsNotNull() {
            addCriterion("margin_min_odds is not null");
            return (Criteria) this;
        }

        public Criteria andMarginMinOddsEqualTo(BigDecimal value) {
            addCriterion("margin_min_odds =", value, "marginMinOdds");
            return (Criteria) this;
        }

        public Criteria andMarginMinOddsNotEqualTo(BigDecimal value) {
            addCriterion("margin_min_odds <>", value, "marginMinOdds");
            return (Criteria) this;
        }

        public Criteria andMarginMinOddsGreaterThan(BigDecimal value) {
            addCriterion("margin_min_odds >", value, "marginMinOdds");
            return (Criteria) this;
        }

        public Criteria andMarginMinOddsGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("margin_min_odds >=", value, "marginMinOdds");
            return (Criteria) this;
        }

        public Criteria andMarginMinOddsLessThan(BigDecimal value) {
            addCriterion("margin_min_odds <", value, "marginMinOdds");
            return (Criteria) this;
        }

        public Criteria andMarginMinOddsLessThanOrEqualTo(BigDecimal value) {
            addCriterion("margin_min_odds <=", value, "marginMinOdds");
            return (Criteria) this;
        }

        public Criteria andMarginMinOddsIn(List<BigDecimal> values) {
            addCriterion("margin_min_odds in", values, "marginMinOdds");
            return (Criteria) this;
        }

        public Criteria andMarginMinOddsNotIn(List<BigDecimal> values) {
            addCriterion("margin_min_odds not in", values, "marginMinOdds");
            return (Criteria) this;
        }

        public Criteria andMarginMinOddsBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("margin_min_odds between", value1, value2, "marginMinOdds");
            return (Criteria) this;
        }

        public Criteria andMarginMinOddsNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("margin_min_odds not between", value1, value2, "marginMinOdds");
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

        public Criteria andOperaterIdIsNull() {
            addCriterion("operater_id is null");
            return (Criteria) this;
        }

        public Criteria andOperaterIdIsNotNull() {
            addCriterion("operater_id is not null");
            return (Criteria) this;
        }

        public Criteria andOperaterIdEqualTo(Long value) {
            addCriterion("operater_id =", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdNotEqualTo(Long value) {
            addCriterion("operater_id <>", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdGreaterThan(Long value) {
            addCriterion("operater_id >", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdGreaterThanOrEqualTo(Long value) {
            addCriterion("operater_id >=", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdLessThan(Long value) {
            addCriterion("operater_id <", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdLessThanOrEqualTo(Long value) {
            addCriterion("operater_id <=", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdIn(List<Long> values) {
            addCriterion("operater_id in", values, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdNotIn(List<Long> values) {
            addCriterion("operater_id not in", values, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdBetween(Long value1, Long value2) {
            addCriterion("operater_id between", value1, value2, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdNotBetween(Long value1, Long value2) {
            addCriterion("operater_id not between", value1, value2, "operaterId");
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