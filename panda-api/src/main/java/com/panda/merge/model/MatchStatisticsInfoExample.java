package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class MatchStatisticsInfoExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MatchStatisticsInfoExample() {
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

        public Criteria andThirdSourceEventIdIsNull() {
            addCriterion("third_source_event_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdSourceEventIdIsNotNull() {
            addCriterion("third_source_event_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdSourceEventIdEqualTo(String value) {
            addCriterion("third_source_event_id =", value, "thirdSourceEventId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceEventIdNotEqualTo(String value) {
            addCriterion("third_source_event_id <>", value, "thirdSourceEventId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceEventIdGreaterThan(String value) {
            addCriterion("third_source_event_id >", value, "thirdSourceEventId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceEventIdGreaterThanOrEqualTo(String value) {
            addCriterion("third_source_event_id >=", value, "thirdSourceEventId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceEventIdLessThan(String value) {
            addCriterion("third_source_event_id <", value, "thirdSourceEventId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceEventIdLessThanOrEqualTo(String value) {
            addCriterion("third_source_event_id <=", value, "thirdSourceEventId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceEventIdLike(String value) {
            addCriterion("third_source_event_id like", value, "thirdSourceEventId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceEventIdNotLike(String value) {
            addCriterion("third_source_event_id not like", value, "thirdSourceEventId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceEventIdIn(List<String> values) {
            addCriterion("third_source_event_id in", values, "thirdSourceEventId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceEventIdNotIn(List<String> values) {
            addCriterion("third_source_event_id not in", values, "thirdSourceEventId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceEventIdBetween(String value1, String value2) {
            addCriterion("third_source_event_id between", value1, value2, "thirdSourceEventId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceEventIdNotBetween(String value1, String value2) {
            addCriterion("third_source_event_id not between", value1, value2, "thirdSourceEventId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceMatchIdIsNull() {
            addCriterion("third_source_match_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdSourceMatchIdIsNotNull() {
            addCriterion("third_source_match_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdSourceMatchIdEqualTo(String value) {
            addCriterion("third_source_match_id =", value, "thirdSourceMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceMatchIdNotEqualTo(String value) {
            addCriterion("third_source_match_id <>", value, "thirdSourceMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceMatchIdGreaterThan(String value) {
            addCriterion("third_source_match_id >", value, "thirdSourceMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceMatchIdGreaterThanOrEqualTo(String value) {
            addCriterion("third_source_match_id >=", value, "thirdSourceMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceMatchIdLessThan(String value) {
            addCriterion("third_source_match_id <", value, "thirdSourceMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceMatchIdLessThanOrEqualTo(String value) {
            addCriterion("third_source_match_id <=", value, "thirdSourceMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceMatchIdLike(String value) {
            addCriterion("third_source_match_id like", value, "thirdSourceMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceMatchIdNotLike(String value) {
            addCriterion("third_source_match_id not like", value, "thirdSourceMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceMatchIdIn(List<String> values) {
            addCriterion("third_source_match_id in", values, "thirdSourceMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceMatchIdNotIn(List<String> values) {
            addCriterion("third_source_match_id not in", values, "thirdSourceMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceMatchIdBetween(String value1, String value2) {
            addCriterion("third_source_match_id between", value1, value2, "thirdSourceMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceMatchIdNotBetween(String value1, String value2) {
            addCriterion("third_source_match_id not between", value1, value2, "thirdSourceMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdIsNull() {
            addCriterion("third_match_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdIsNotNull() {
            addCriterion("third_match_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdEqualTo(Long value) {
            addCriterion("third_match_id =", value, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdNotEqualTo(Long value) {
            addCriterion("third_match_id <>", value, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdGreaterThan(Long value) {
            addCriterion("third_match_id >", value, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdGreaterThanOrEqualTo(Long value) {
            addCriterion("third_match_id >=", value, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdLessThan(Long value) {
            addCriterion("third_match_id <", value, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdLessThanOrEqualTo(Long value) {
            addCriterion("third_match_id <=", value, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdIn(List<Long> values) {
            addCriterion("third_match_id in", values, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdNotIn(List<Long> values) {
            addCriterion("third_match_id not in", values, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdBetween(Long value1, Long value2) {
            addCriterion("third_match_id between", value1, value2, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdNotBetween(Long value1, Long value2) {
            addCriterion("third_match_id not between", value1, value2, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchIdIsNull() {
            addCriterion("standard_match_id is null");
            return (Criteria) this;
        }

        public Criteria andStandardMatchIdIsNotNull() {
            addCriterion("standard_match_id is not null");
            return (Criteria) this;
        }

        public Criteria andStandardMatchIdEqualTo(Long value) {
            addCriterion("standard_match_id =", value, "standardMatchId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchIdNotEqualTo(Long value) {
            addCriterion("standard_match_id <>", value, "standardMatchId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchIdGreaterThan(Long value) {
            addCriterion("standard_match_id >", value, "standardMatchId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchIdGreaterThanOrEqualTo(Long value) {
            addCriterion("standard_match_id >=", value, "standardMatchId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchIdLessThan(Long value) {
            addCriterion("standard_match_id <", value, "standardMatchId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchIdLessThanOrEqualTo(Long value) {
            addCriterion("standard_match_id <=", value, "standardMatchId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchIdIn(List<Long> values) {
            addCriterion("standard_match_id in", values, "standardMatchId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchIdNotIn(List<Long> values) {
            addCriterion("standard_match_id not in", values, "standardMatchId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchIdBetween(Long value1, Long value2) {
            addCriterion("standard_match_id between", value1, value2, "standardMatchId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchIdNotBetween(Long value1, Long value2) {
            addCriterion("standard_match_id not between", value1, value2, "standardMatchId");
            return (Criteria) this;
        }

        public Criteria andEventTimeIsNull() {
            addCriterion("event_time is null");
            return (Criteria) this;
        }

        public Criteria andEventTimeIsNotNull() {
            addCriterion("event_time is not null");
            return (Criteria) this;
        }

        public Criteria andEventTimeEqualTo(Long value) {
            addCriterion("event_time =", value, "eventTime");
            return (Criteria) this;
        }

        public Criteria andEventTimeNotEqualTo(Long value) {
            addCriterion("event_time <>", value, "eventTime");
            return (Criteria) this;
        }

        public Criteria andEventTimeGreaterThan(Long value) {
            addCriterion("event_time >", value, "eventTime");
            return (Criteria) this;
        }

        public Criteria andEventTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("event_time >=", value, "eventTime");
            return (Criteria) this;
        }

        public Criteria andEventTimeLessThan(Long value) {
            addCriterion("event_time <", value, "eventTime");
            return (Criteria) this;
        }

        public Criteria andEventTimeLessThanOrEqualTo(Long value) {
            addCriterion("event_time <=", value, "eventTime");
            return (Criteria) this;
        }

        public Criteria andEventTimeIn(List<Long> values) {
            addCriterion("event_time in", values, "eventTime");
            return (Criteria) this;
        }

        public Criteria andEventTimeNotIn(List<Long> values) {
            addCriterion("event_time not in", values, "eventTime");
            return (Criteria) this;
        }

        public Criteria andEventTimeBetween(Long value1, Long value2) {
            addCriterion("event_time between", value1, value2, "eventTime");
            return (Criteria) this;
        }

        public Criteria andEventTimeNotBetween(Long value1, Long value2) {
            addCriterion("event_time not between", value1, value2, "eventTime");
            return (Criteria) this;
        }

        public Criteria andThirdEventTypeIdIsNull() {
            addCriterion("third_event_type_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdEventTypeIdIsNotNull() {
            addCriterion("third_event_type_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdEventTypeIdEqualTo(Long value) {
            addCriterion("third_event_type_id =", value, "thirdEventTypeId");
            return (Criteria) this;
        }

        public Criteria andThirdEventTypeIdNotEqualTo(Long value) {
            addCriterion("third_event_type_id <>", value, "thirdEventTypeId");
            return (Criteria) this;
        }

        public Criteria andThirdEventTypeIdGreaterThan(Long value) {
            addCriterion("third_event_type_id >", value, "thirdEventTypeId");
            return (Criteria) this;
        }

        public Criteria andThirdEventTypeIdGreaterThanOrEqualTo(Long value) {
            addCriterion("third_event_type_id >=", value, "thirdEventTypeId");
            return (Criteria) this;
        }

        public Criteria andThirdEventTypeIdLessThan(Long value) {
            addCriterion("third_event_type_id <", value, "thirdEventTypeId");
            return (Criteria) this;
        }

        public Criteria andThirdEventTypeIdLessThanOrEqualTo(Long value) {
            addCriterion("third_event_type_id <=", value, "thirdEventTypeId");
            return (Criteria) this;
        }

        public Criteria andThirdEventTypeIdIn(List<Long> values) {
            addCriterion("third_event_type_id in", values, "thirdEventTypeId");
            return (Criteria) this;
        }

        public Criteria andThirdEventTypeIdNotIn(List<Long> values) {
            addCriterion("third_event_type_id not in", values, "thirdEventTypeId");
            return (Criteria) this;
        }

        public Criteria andThirdEventTypeIdBetween(Long value1, Long value2) {
            addCriterion("third_event_type_id between", value1, value2, "thirdEventTypeId");
            return (Criteria) this;
        }

        public Criteria andThirdEventTypeIdNotBetween(Long value1, Long value2) {
            addCriterion("third_event_type_id not between", value1, value2, "thirdEventTypeId");
            return (Criteria) this;
        }

        public Criteria andThirdEventTypeIsNull() {
            addCriterion("third_event_type is null");
            return (Criteria) this;
        }

        public Criteria andThirdEventTypeIsNotNull() {
            addCriterion("third_event_type is not null");
            return (Criteria) this;
        }

        public Criteria andThirdEventTypeEqualTo(String value) {
            addCriterion("third_event_type =", value, "thirdEventType");
            return (Criteria) this;
        }

        public Criteria andThirdEventTypeNotEqualTo(String value) {
            addCriterion("third_event_type <>", value, "thirdEventType");
            return (Criteria) this;
        }

        public Criteria andThirdEventTypeGreaterThan(String value) {
            addCriterion("third_event_type >", value, "thirdEventType");
            return (Criteria) this;
        }

        public Criteria andThirdEventTypeGreaterThanOrEqualTo(String value) {
            addCriterion("third_event_type >=", value, "thirdEventType");
            return (Criteria) this;
        }

        public Criteria andThirdEventTypeLessThan(String value) {
            addCriterion("third_event_type <", value, "thirdEventType");
            return (Criteria) this;
        }

        public Criteria andThirdEventTypeLessThanOrEqualTo(String value) {
            addCriterion("third_event_type <=", value, "thirdEventType");
            return (Criteria) this;
        }

        public Criteria andThirdEventTypeLike(String value) {
            addCriterion("third_event_type like", value, "thirdEventType");
            return (Criteria) this;
        }

        public Criteria andThirdEventTypeNotLike(String value) {
            addCriterion("third_event_type not like", value, "thirdEventType");
            return (Criteria) this;
        }

        public Criteria andThirdEventTypeIn(List<String> values) {
            addCriterion("third_event_type in", values, "thirdEventType");
            return (Criteria) this;
        }

        public Criteria andThirdEventTypeNotIn(List<String> values) {
            addCriterion("third_event_type not in", values, "thirdEventType");
            return (Criteria) this;
        }

        public Criteria andThirdEventTypeBetween(String value1, String value2) {
            addCriterion("third_event_type between", value1, value2, "thirdEventType");
            return (Criteria) this;
        }

        public Criteria andThirdEventTypeNotBetween(String value1, String value2) {
            addCriterion("third_event_type not between", value1, value2, "thirdEventType");
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

        public Criteria andHomeAwayIsNull() {
            addCriterion("home_away is null");
            return (Criteria) this;
        }

        public Criteria andHomeAwayIsNotNull() {
            addCriterion("home_away is not null");
            return (Criteria) this;
        }

        public Criteria andHomeAwayEqualTo(String value) {
            addCriterion("home_away =", value, "homeAway");
            return (Criteria) this;
        }

        public Criteria andHomeAwayNotEqualTo(String value) {
            addCriterion("home_away <>", value, "homeAway");
            return (Criteria) this;
        }

        public Criteria andHomeAwayGreaterThan(String value) {
            addCriterion("home_away >", value, "homeAway");
            return (Criteria) this;
        }

        public Criteria andHomeAwayGreaterThanOrEqualTo(String value) {
            addCriterion("home_away >=", value, "homeAway");
            return (Criteria) this;
        }

        public Criteria andHomeAwayLessThan(String value) {
            addCriterion("home_away <", value, "homeAway");
            return (Criteria) this;
        }

        public Criteria andHomeAwayLessThanOrEqualTo(String value) {
            addCriterion("home_away <=", value, "homeAway");
            return (Criteria) this;
        }

        public Criteria andHomeAwayLike(String value) {
            addCriterion("home_away like", value, "homeAway");
            return (Criteria) this;
        }

        public Criteria andHomeAwayNotLike(String value) {
            addCriterion("home_away not like", value, "homeAway");
            return (Criteria) this;
        }

        public Criteria andHomeAwayIn(List<String> values) {
            addCriterion("home_away in", values, "homeAway");
            return (Criteria) this;
        }

        public Criteria andHomeAwayNotIn(List<String> values) {
            addCriterion("home_away not in", values, "homeAway");
            return (Criteria) this;
        }

        public Criteria andHomeAwayBetween(String value1, String value2) {
            addCriterion("home_away between", value1, value2, "homeAway");
            return (Criteria) this;
        }

        public Criteria andHomeAwayNotBetween(String value1, String value2) {
            addCriterion("home_away not between", value1, value2, "homeAway");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartIsNull() {
            addCriterion("seconds_match_start is null");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartIsNotNull() {
            addCriterion("seconds_match_start is not null");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartEqualTo(Integer value) {
            addCriterion("seconds_match_start =", value, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartNotEqualTo(Integer value) {
            addCriterion("seconds_match_start <>", value, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartGreaterThan(Integer value) {
            addCriterion("seconds_match_start >", value, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartGreaterThanOrEqualTo(Integer value) {
            addCriterion("seconds_match_start >=", value, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartLessThan(Integer value) {
            addCriterion("seconds_match_start <", value, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartLessThanOrEqualTo(Integer value) {
            addCriterion("seconds_match_start <=", value, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartIn(List<Integer> values) {
            addCriterion("seconds_match_start in", values, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartNotIn(List<Integer> values) {
            addCriterion("seconds_match_start not in", values, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartBetween(Integer value1, Integer value2) {
            addCriterion("seconds_match_start between", value1, value2, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartNotBetween(Integer value1, Integer value2) {
            addCriterion("seconds_match_start not between", value1, value2, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andMatchLengthIsNull() {
            addCriterion("match_length is null");
            return (Criteria) this;
        }

        public Criteria andMatchLengthIsNotNull() {
            addCriterion("match_length is not null");
            return (Criteria) this;
        }

        public Criteria andMatchLengthEqualTo(Integer value) {
            addCriterion("match_length =", value, "matchLength");
            return (Criteria) this;
        }

        public Criteria andMatchLengthNotEqualTo(Integer value) {
            addCriterion("match_length <>", value, "matchLength");
            return (Criteria) this;
        }

        public Criteria andMatchLengthGreaterThan(Integer value) {
            addCriterion("match_length >", value, "matchLength");
            return (Criteria) this;
        }

        public Criteria andMatchLengthGreaterThanOrEqualTo(Integer value) {
            addCriterion("match_length >=", value, "matchLength");
            return (Criteria) this;
        }

        public Criteria andMatchLengthLessThan(Integer value) {
            addCriterion("match_length <", value, "matchLength");
            return (Criteria) this;
        }

        public Criteria andMatchLengthLessThanOrEqualTo(Integer value) {
            addCriterion("match_length <=", value, "matchLength");
            return (Criteria) this;
        }

        public Criteria andMatchLengthIn(List<Integer> values) {
            addCriterion("match_length in", values, "matchLength");
            return (Criteria) this;
        }

        public Criteria andMatchLengthNotIn(List<Integer> values) {
            addCriterion("match_length not in", values, "matchLength");
            return (Criteria) this;
        }

        public Criteria andMatchLengthBetween(Integer value1, Integer value2) {
            addCriterion("match_length between", value1, value2, "matchLength");
            return (Criteria) this;
        }

        public Criteria andMatchLengthNotBetween(Integer value1, Integer value2) {
            addCriterion("match_length not between", value1, value2, "matchLength");
            return (Criteria) this;
        }

        public Criteria andScoreIsNull() {
            addCriterion("score is null");
            return (Criteria) this;
        }

        public Criteria andScoreIsNotNull() {
            addCriterion("score is not null");
            return (Criteria) this;
        }

        public Criteria andScoreEqualTo(String value) {
            addCriterion("score =", value, "score");
            return (Criteria) this;
        }

        public Criteria andScoreNotEqualTo(String value) {
            addCriterion("score <>", value, "score");
            return (Criteria) this;
        }

        public Criteria andScoreGreaterThan(String value) {
            addCriterion("score >", value, "score");
            return (Criteria) this;
        }

        public Criteria andScoreGreaterThanOrEqualTo(String value) {
            addCriterion("score >=", value, "score");
            return (Criteria) this;
        }

        public Criteria andScoreLessThan(String value) {
            addCriterion("score <", value, "score");
            return (Criteria) this;
        }

        public Criteria andScoreLessThanOrEqualTo(String value) {
            addCriterion("score <=", value, "score");
            return (Criteria) this;
        }

        public Criteria andScoreLike(String value) {
            addCriterion("score like", value, "score");
            return (Criteria) this;
        }

        public Criteria andScoreNotLike(String value) {
            addCriterion("score not like", value, "score");
            return (Criteria) this;
        }

        public Criteria andScoreIn(List<String> values) {
            addCriterion("score in", values, "score");
            return (Criteria) this;
        }

        public Criteria andScoreNotIn(List<String> values) {
            addCriterion("score not in", values, "score");
            return (Criteria) this;
        }

        public Criteria andScoreBetween(String value1, String value2) {
            addCriterion("score between", value1, value2, "score");
            return (Criteria) this;
        }

        public Criteria andScoreNotBetween(String value1, String value2) {
            addCriterion("score not between", value1, value2, "score");
            return (Criteria) this;
        }

        public Criteria andCornerScoreIsNull() {
            addCriterion("corner_score is null");
            return (Criteria) this;
        }

        public Criteria andCornerScoreIsNotNull() {
            addCriterion("corner_score is not null");
            return (Criteria) this;
        }

        public Criteria andCornerScoreEqualTo(String value) {
            addCriterion("corner_score =", value, "cornerScore");
            return (Criteria) this;
        }

        public Criteria andCornerScoreNotEqualTo(String value) {
            addCriterion("corner_score <>", value, "cornerScore");
            return (Criteria) this;
        }

        public Criteria andCornerScoreGreaterThan(String value) {
            addCriterion("corner_score >", value, "cornerScore");
            return (Criteria) this;
        }

        public Criteria andCornerScoreGreaterThanOrEqualTo(String value) {
            addCriterion("corner_score >=", value, "cornerScore");
            return (Criteria) this;
        }

        public Criteria andCornerScoreLessThan(String value) {
            addCriterion("corner_score <", value, "cornerScore");
            return (Criteria) this;
        }

        public Criteria andCornerScoreLessThanOrEqualTo(String value) {
            addCriterion("corner_score <=", value, "cornerScore");
            return (Criteria) this;
        }

        public Criteria andCornerScoreLike(String value) {
            addCriterion("corner_score like", value, "cornerScore");
            return (Criteria) this;
        }

        public Criteria andCornerScoreNotLike(String value) {
            addCriterion("corner_score not like", value, "cornerScore");
            return (Criteria) this;
        }

        public Criteria andCornerScoreIn(List<String> values) {
            addCriterion("corner_score in", values, "cornerScore");
            return (Criteria) this;
        }

        public Criteria andCornerScoreNotIn(List<String> values) {
            addCriterion("corner_score not in", values, "cornerScore");
            return (Criteria) this;
        }

        public Criteria andCornerScoreBetween(String value1, String value2) {
            addCriterion("corner_score between", value1, value2, "cornerScore");
            return (Criteria) this;
        }

        public Criteria andCornerScoreNotBetween(String value1, String value2) {
            addCriterion("corner_score not between", value1, value2, "cornerScore");
            return (Criteria) this;
        }

        public Criteria andYellowCardScoreIsNull() {
            addCriterion("yellow_card_score is null");
            return (Criteria) this;
        }

        public Criteria andYellowCardScoreIsNotNull() {
            addCriterion("yellow_card_score is not null");
            return (Criteria) this;
        }

        public Criteria andYellowCardScoreEqualTo(String value) {
            addCriterion("yellow_card_score =", value, "yellowCardScore");
            return (Criteria) this;
        }

        public Criteria andYellowCardScoreNotEqualTo(String value) {
            addCriterion("yellow_card_score <>", value, "yellowCardScore");
            return (Criteria) this;
        }

        public Criteria andYellowCardScoreGreaterThan(String value) {
            addCriterion("yellow_card_score >", value, "yellowCardScore");
            return (Criteria) this;
        }

        public Criteria andYellowCardScoreGreaterThanOrEqualTo(String value) {
            addCriterion("yellow_card_score >=", value, "yellowCardScore");
            return (Criteria) this;
        }

        public Criteria andYellowCardScoreLessThan(String value) {
            addCriterion("yellow_card_score <", value, "yellowCardScore");
            return (Criteria) this;
        }

        public Criteria andYellowCardScoreLessThanOrEqualTo(String value) {
            addCriterion("yellow_card_score <=", value, "yellowCardScore");
            return (Criteria) this;
        }

        public Criteria andYellowCardScoreLike(String value) {
            addCriterion("yellow_card_score like", value, "yellowCardScore");
            return (Criteria) this;
        }

        public Criteria andYellowCardScoreNotLike(String value) {
            addCriterion("yellow_card_score not like", value, "yellowCardScore");
            return (Criteria) this;
        }

        public Criteria andYellowCardScoreIn(List<String> values) {
            addCriterion("yellow_card_score in", values, "yellowCardScore");
            return (Criteria) this;
        }

        public Criteria andYellowCardScoreNotIn(List<String> values) {
            addCriterion("yellow_card_score not in", values, "yellowCardScore");
            return (Criteria) this;
        }

        public Criteria andYellowCardScoreBetween(String value1, String value2) {
            addCriterion("yellow_card_score between", value1, value2, "yellowCardScore");
            return (Criteria) this;
        }

        public Criteria andYellowCardScoreNotBetween(String value1, String value2) {
            addCriterion("yellow_card_score not between", value1, value2, "yellowCardScore");
            return (Criteria) this;
        }

        public Criteria andRedCardScoreIsNull() {
            addCriterion("red_card_score is null");
            return (Criteria) this;
        }

        public Criteria andRedCardScoreIsNotNull() {
            addCriterion("red_card_score is not null");
            return (Criteria) this;
        }

        public Criteria andRedCardScoreEqualTo(String value) {
            addCriterion("red_card_score =", value, "redCardScore");
            return (Criteria) this;
        }

        public Criteria andRedCardScoreNotEqualTo(String value) {
            addCriterion("red_card_score <>", value, "redCardScore");
            return (Criteria) this;
        }

        public Criteria andRedCardScoreGreaterThan(String value) {
            addCriterion("red_card_score >", value, "redCardScore");
            return (Criteria) this;
        }

        public Criteria andRedCardScoreGreaterThanOrEqualTo(String value) {
            addCriterion("red_card_score >=", value, "redCardScore");
            return (Criteria) this;
        }

        public Criteria andRedCardScoreLessThan(String value) {
            addCriterion("red_card_score <", value, "redCardScore");
            return (Criteria) this;
        }

        public Criteria andRedCardScoreLessThanOrEqualTo(String value) {
            addCriterion("red_card_score <=", value, "redCardScore");
            return (Criteria) this;
        }

        public Criteria andRedCardScoreLike(String value) {
            addCriterion("red_card_score like", value, "redCardScore");
            return (Criteria) this;
        }

        public Criteria andRedCardScoreNotLike(String value) {
            addCriterion("red_card_score not like", value, "redCardScore");
            return (Criteria) this;
        }

        public Criteria andRedCardScoreIn(List<String> values) {
            addCriterion("red_card_score in", values, "redCardScore");
            return (Criteria) this;
        }

        public Criteria andRedCardScoreNotIn(List<String> values) {
            addCriterion("red_card_score not in", values, "redCardScore");
            return (Criteria) this;
        }

        public Criteria andRedCardScoreBetween(String value1, String value2) {
            addCriterion("red_card_score between", value1, value2, "redCardScore");
            return (Criteria) this;
        }

        public Criteria andRedCardScoreNotBetween(String value1, String value2) {
            addCriterion("red_card_score not between", value1, value2, "redCardScore");
            return (Criteria) this;
        }

        public Criteria andShotOnTargetScoreIsNull() {
            addCriterion("shot_on_target_score is null");
            return (Criteria) this;
        }

        public Criteria andShotOnTargetScoreIsNotNull() {
            addCriterion("shot_on_target_score is not null");
            return (Criteria) this;
        }

        public Criteria andShotOnTargetScoreEqualTo(String value) {
            addCriterion("shot_on_target_score =", value, "shotOnTargetScore");
            return (Criteria) this;
        }

        public Criteria andShotOnTargetScoreNotEqualTo(String value) {
            addCriterion("shot_on_target_score <>", value, "shotOnTargetScore");
            return (Criteria) this;
        }

        public Criteria andShotOnTargetScoreGreaterThan(String value) {
            addCriterion("shot_on_target_score >", value, "shotOnTargetScore");
            return (Criteria) this;
        }

        public Criteria andShotOnTargetScoreGreaterThanOrEqualTo(String value) {
            addCriterion("shot_on_target_score >=", value, "shotOnTargetScore");
            return (Criteria) this;
        }

        public Criteria andShotOnTargetScoreLessThan(String value) {
            addCriterion("shot_on_target_score <", value, "shotOnTargetScore");
            return (Criteria) this;
        }

        public Criteria andShotOnTargetScoreLessThanOrEqualTo(String value) {
            addCriterion("shot_on_target_score <=", value, "shotOnTargetScore");
            return (Criteria) this;
        }

        public Criteria andShotOnTargetScoreLike(String value) {
            addCriterion("shot_on_target_score like", value, "shotOnTargetScore");
            return (Criteria) this;
        }

        public Criteria andShotOnTargetScoreNotLike(String value) {
            addCriterion("shot_on_target_score not like", value, "shotOnTargetScore");
            return (Criteria) this;
        }

        public Criteria andShotOnTargetScoreIn(List<String> values) {
            addCriterion("shot_on_target_score in", values, "shotOnTargetScore");
            return (Criteria) this;
        }

        public Criteria andShotOnTargetScoreNotIn(List<String> values) {
            addCriterion("shot_on_target_score not in", values, "shotOnTargetScore");
            return (Criteria) this;
        }

        public Criteria andShotOnTargetScoreBetween(String value1, String value2) {
            addCriterion("shot_on_target_score between", value1, value2, "shotOnTargetScore");
            return (Criteria) this;
        }

        public Criteria andShotOnTargetScoreNotBetween(String value1, String value2) {
            addCriterion("shot_on_target_score not between", value1, value2, "shotOnTargetScore");
            return (Criteria) this;
        }

        public Criteria andShotOffTargetScoreIsNull() {
            addCriterion("shot_off_target_score is null");
            return (Criteria) this;
        }

        public Criteria andShotOffTargetScoreIsNotNull() {
            addCriterion("shot_off_target_score is not null");
            return (Criteria) this;
        }

        public Criteria andShotOffTargetScoreEqualTo(String value) {
            addCriterion("shot_off_target_score =", value, "shotOffTargetScore");
            return (Criteria) this;
        }

        public Criteria andShotOffTargetScoreNotEqualTo(String value) {
            addCriterion("shot_off_target_score <>", value, "shotOffTargetScore");
            return (Criteria) this;
        }

        public Criteria andShotOffTargetScoreGreaterThan(String value) {
            addCriterion("shot_off_target_score >", value, "shotOffTargetScore");
            return (Criteria) this;
        }

        public Criteria andShotOffTargetScoreGreaterThanOrEqualTo(String value) {
            addCriterion("shot_off_target_score >=", value, "shotOffTargetScore");
            return (Criteria) this;
        }

        public Criteria andShotOffTargetScoreLessThan(String value) {
            addCriterion("shot_off_target_score <", value, "shotOffTargetScore");
            return (Criteria) this;
        }

        public Criteria andShotOffTargetScoreLessThanOrEqualTo(String value) {
            addCriterion("shot_off_target_score <=", value, "shotOffTargetScore");
            return (Criteria) this;
        }

        public Criteria andShotOffTargetScoreLike(String value) {
            addCriterion("shot_off_target_score like", value, "shotOffTargetScore");
            return (Criteria) this;
        }

        public Criteria andShotOffTargetScoreNotLike(String value) {
            addCriterion("shot_off_target_score not like", value, "shotOffTargetScore");
            return (Criteria) this;
        }

        public Criteria andShotOffTargetScoreIn(List<String> values) {
            addCriterion("shot_off_target_score in", values, "shotOffTargetScore");
            return (Criteria) this;
        }

        public Criteria andShotOffTargetScoreNotIn(List<String> values) {
            addCriterion("shot_off_target_score not in", values, "shotOffTargetScore");
            return (Criteria) this;
        }

        public Criteria andShotOffTargetScoreBetween(String value1, String value2) {
            addCriterion("shot_off_target_score between", value1, value2, "shotOffTargetScore");
            return (Criteria) this;
        }

        public Criteria andShotOffTargetScoreNotBetween(String value1, String value2) {
            addCriterion("shot_off_target_score not between", value1, value2, "shotOffTargetScore");
            return (Criteria) this;
        }

        public Criteria andDangerousAttackScoreIsNull() {
            addCriterion("dangerous_attack_score is null");
            return (Criteria) this;
        }

        public Criteria andDangerousAttackScoreIsNotNull() {
            addCriterion("dangerous_attack_score is not null");
            return (Criteria) this;
        }

        public Criteria andDangerousAttackScoreEqualTo(String value) {
            addCriterion("dangerous_attack_score =", value, "dangerousAttackScore");
            return (Criteria) this;
        }

        public Criteria andDangerousAttackScoreNotEqualTo(String value) {
            addCriterion("dangerous_attack_score <>", value, "dangerousAttackScore");
            return (Criteria) this;
        }

        public Criteria andDangerousAttackScoreGreaterThan(String value) {
            addCriterion("dangerous_attack_score >", value, "dangerousAttackScore");
            return (Criteria) this;
        }

        public Criteria andDangerousAttackScoreGreaterThanOrEqualTo(String value) {
            addCriterion("dangerous_attack_score >=", value, "dangerousAttackScore");
            return (Criteria) this;
        }

        public Criteria andDangerousAttackScoreLessThan(String value) {
            addCriterion("dangerous_attack_score <", value, "dangerousAttackScore");
            return (Criteria) this;
        }

        public Criteria andDangerousAttackScoreLessThanOrEqualTo(String value) {
            addCriterion("dangerous_attack_score <=", value, "dangerousAttackScore");
            return (Criteria) this;
        }

        public Criteria andDangerousAttackScoreLike(String value) {
            addCriterion("dangerous_attack_score like", value, "dangerousAttackScore");
            return (Criteria) this;
        }

        public Criteria andDangerousAttackScoreNotLike(String value) {
            addCriterion("dangerous_attack_score not like", value, "dangerousAttackScore");
            return (Criteria) this;
        }

        public Criteria andDangerousAttackScoreIn(List<String> values) {
            addCriterion("dangerous_attack_score in", values, "dangerousAttackScore");
            return (Criteria) this;
        }

        public Criteria andDangerousAttackScoreNotIn(List<String> values) {
            addCriterion("dangerous_attack_score not in", values, "dangerousAttackScore");
            return (Criteria) this;
        }

        public Criteria andDangerousAttackScoreBetween(String value1, String value2) {
            addCriterion("dangerous_attack_score between", value1, value2, "dangerousAttackScore");
            return (Criteria) this;
        }

        public Criteria andDangerousAttackScoreNotBetween(String value1, String value2) {
            addCriterion("dangerous_attack_score not between", value1, value2, "dangerousAttackScore");
            return (Criteria) this;
        }

        public Criteria andAcesScoreIsNull() {
            addCriterion("aces_score is null");
            return (Criteria) this;
        }

        public Criteria andAcesScoreIsNotNull() {
            addCriterion("aces_score is not null");
            return (Criteria) this;
        }

        public Criteria andAcesScoreEqualTo(String value) {
            addCriterion("aces_score =", value, "acesScore");
            return (Criteria) this;
        }

        public Criteria andAcesScoreNotEqualTo(String value) {
            addCriterion("aces_score <>", value, "acesScore");
            return (Criteria) this;
        }

        public Criteria andAcesScoreGreaterThan(String value) {
            addCriterion("aces_score >", value, "acesScore");
            return (Criteria) this;
        }

        public Criteria andAcesScoreGreaterThanOrEqualTo(String value) {
            addCriterion("aces_score >=", value, "acesScore");
            return (Criteria) this;
        }

        public Criteria andAcesScoreLessThan(String value) {
            addCriterion("aces_score <", value, "acesScore");
            return (Criteria) this;
        }

        public Criteria andAcesScoreLessThanOrEqualTo(String value) {
            addCriterion("aces_score <=", value, "acesScore");
            return (Criteria) this;
        }

        public Criteria andAcesScoreLike(String value) {
            addCriterion("aces_score like", value, "acesScore");
            return (Criteria) this;
        }

        public Criteria andAcesScoreNotLike(String value) {
            addCriterion("aces_score not like", value, "acesScore");
            return (Criteria) this;
        }

        public Criteria andAcesScoreIn(List<String> values) {
            addCriterion("aces_score in", values, "acesScore");
            return (Criteria) this;
        }

        public Criteria andAcesScoreNotIn(List<String> values) {
            addCriterion("aces_score not in", values, "acesScore");
            return (Criteria) this;
        }

        public Criteria andAcesScoreBetween(String value1, String value2) {
            addCriterion("aces_score between", value1, value2, "acesScore");
            return (Criteria) this;
        }

        public Criteria andAcesScoreNotBetween(String value1, String value2) {
            addCriterion("aces_score not between", value1, value2, "acesScore");
            return (Criteria) this;
        }

        public Criteria andDoubleFaultScoreIsNull() {
            addCriterion("double_fault_score is null");
            return (Criteria) this;
        }

        public Criteria andDoubleFaultScoreIsNotNull() {
            addCriterion("double_fault_score is not null");
            return (Criteria) this;
        }

        public Criteria andDoubleFaultScoreEqualTo(String value) {
            addCriterion("double_fault_score =", value, "doubleFaultScore");
            return (Criteria) this;
        }

        public Criteria andDoubleFaultScoreNotEqualTo(String value) {
            addCriterion("double_fault_score <>", value, "doubleFaultScore");
            return (Criteria) this;
        }

        public Criteria andDoubleFaultScoreGreaterThan(String value) {
            addCriterion("double_fault_score >", value, "doubleFaultScore");
            return (Criteria) this;
        }

        public Criteria andDoubleFaultScoreGreaterThanOrEqualTo(String value) {
            addCriterion("double_fault_score >=", value, "doubleFaultScore");
            return (Criteria) this;
        }

        public Criteria andDoubleFaultScoreLessThan(String value) {
            addCriterion("double_fault_score <", value, "doubleFaultScore");
            return (Criteria) this;
        }

        public Criteria andDoubleFaultScoreLessThanOrEqualTo(String value) {
            addCriterion("double_fault_score <=", value, "doubleFaultScore");
            return (Criteria) this;
        }

        public Criteria andDoubleFaultScoreLike(String value) {
            addCriterion("double_fault_score like", value, "doubleFaultScore");
            return (Criteria) this;
        }

        public Criteria andDoubleFaultScoreNotLike(String value) {
            addCriterion("double_fault_score not like", value, "doubleFaultScore");
            return (Criteria) this;
        }

        public Criteria andDoubleFaultScoreIn(List<String> values) {
            addCriterion("double_fault_score in", values, "doubleFaultScore");
            return (Criteria) this;
        }

        public Criteria andDoubleFaultScoreNotIn(List<String> values) {
            addCriterion("double_fault_score not in", values, "doubleFaultScore");
            return (Criteria) this;
        }

        public Criteria andDoubleFaultScoreBetween(String value1, String value2) {
            addCriterion("double_fault_score between", value1, value2, "doubleFaultScore");
            return (Criteria) this;
        }

        public Criteria andDoubleFaultScoreNotBetween(String value1, String value2) {
            addCriterion("double_fault_score not between", value1, value2, "doubleFaultScore");
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

        public Criteria andPeriodScoreIsNull() {
            addCriterion("period_score is null");
            return (Criteria) this;
        }

        public Criteria andPeriodScoreIsNotNull() {
            addCriterion("period_score is not null");
            return (Criteria) this;
        }

        public Criteria andPeriodScoreEqualTo(String value) {
            addCriterion("period_score =", value, "periodScore");
            return (Criteria) this;
        }

        public Criteria andPeriodScoreNotEqualTo(String value) {
            addCriterion("period_score <>", value, "periodScore");
            return (Criteria) this;
        }

        public Criteria andPeriodScoreGreaterThan(String value) {
            addCriterion("period_score >", value, "periodScore");
            return (Criteria) this;
        }

        public Criteria andPeriodScoreGreaterThanOrEqualTo(String value) {
            addCriterion("period_score >=", value, "periodScore");
            return (Criteria) this;
        }

        public Criteria andPeriodScoreLessThan(String value) {
            addCriterion("period_score <", value, "periodScore");
            return (Criteria) this;
        }

        public Criteria andPeriodScoreLessThanOrEqualTo(String value) {
            addCriterion("period_score <=", value, "periodScore");
            return (Criteria) this;
        }

        public Criteria andPeriodScoreLike(String value) {
            addCriterion("period_score like", value, "periodScore");
            return (Criteria) this;
        }

        public Criteria andPeriodScoreNotLike(String value) {
            addCriterion("period_score not like", value, "periodScore");
            return (Criteria) this;
        }

        public Criteria andPeriodScoreIn(List<String> values) {
            addCriterion("period_score in", values, "periodScore");
            return (Criteria) this;
        }

        public Criteria andPeriodScoreNotIn(List<String> values) {
            addCriterion("period_score not in", values, "periodScore");
            return (Criteria) this;
        }

        public Criteria andPeriodScoreBetween(String value1, String value2) {
            addCriterion("period_score between", value1, value2, "periodScore");
            return (Criteria) this;
        }

        public Criteria andPeriodScoreNotBetween(String value1, String value2) {
            addCriterion("period_score not between", value1, value2, "periodScore");
            return (Criteria) this;
        }

        public Criteria andQuarterScoreIsNull() {
            addCriterion("quarter_score is null");
            return (Criteria) this;
        }

        public Criteria andQuarterScoreIsNotNull() {
            addCriterion("quarter_score is not null");
            return (Criteria) this;
        }

        public Criteria andQuarterScoreEqualTo(String value) {
            addCriterion("quarter_score =", value, "quarterScore");
            return (Criteria) this;
        }

        public Criteria andQuarterScoreNotEqualTo(String value) {
            addCriterion("quarter_score <>", value, "quarterScore");
            return (Criteria) this;
        }

        public Criteria andQuarterScoreGreaterThan(String value) {
            addCriterion("quarter_score >", value, "quarterScore");
            return (Criteria) this;
        }

        public Criteria andQuarterScoreGreaterThanOrEqualTo(String value) {
            addCriterion("quarter_score >=", value, "quarterScore");
            return (Criteria) this;
        }

        public Criteria andQuarterScoreLessThan(String value) {
            addCriterion("quarter_score <", value, "quarterScore");
            return (Criteria) this;
        }

        public Criteria andQuarterScoreLessThanOrEqualTo(String value) {
            addCriterion("quarter_score <=", value, "quarterScore");
            return (Criteria) this;
        }

        public Criteria andQuarterScoreLike(String value) {
            addCriterion("quarter_score like", value, "quarterScore");
            return (Criteria) this;
        }

        public Criteria andQuarterScoreNotLike(String value) {
            addCriterion("quarter_score not like", value, "quarterScore");
            return (Criteria) this;
        }

        public Criteria andQuarterScoreIn(List<String> values) {
            addCriterion("quarter_score in", values, "quarterScore");
            return (Criteria) this;
        }

        public Criteria andQuarterScoreNotIn(List<String> values) {
            addCriterion("quarter_score not in", values, "quarterScore");
            return (Criteria) this;
        }

        public Criteria andQuarterScoreBetween(String value1, String value2) {
            addCriterion("quarter_score between", value1, value2, "quarterScore");
            return (Criteria) this;
        }

        public Criteria andQuarterScoreNotBetween(String value1, String value2) {
            addCriterion("quarter_score not between", value1, value2, "quarterScore");
            return (Criteria) this;
        }

        public Criteria andSetScoreIsNull() {
            addCriterion("set_score is null");
            return (Criteria) this;
        }

        public Criteria andSetScoreIsNotNull() {
            addCriterion("set_score is not null");
            return (Criteria) this;
        }

        public Criteria andSetScoreEqualTo(String value) {
            addCriterion("set_score =", value, "setScore");
            return (Criteria) this;
        }

        public Criteria andSetScoreNotEqualTo(String value) {
            addCriterion("set_score <>", value, "setScore");
            return (Criteria) this;
        }

        public Criteria andSetScoreGreaterThan(String value) {
            addCriterion("set_score >", value, "setScore");
            return (Criteria) this;
        }

        public Criteria andSetScoreGreaterThanOrEqualTo(String value) {
            addCriterion("set_score >=", value, "setScore");
            return (Criteria) this;
        }

        public Criteria andSetScoreLessThan(String value) {
            addCriterion("set_score <", value, "setScore");
            return (Criteria) this;
        }

        public Criteria andSetScoreLessThanOrEqualTo(String value) {
            addCriterion("set_score <=", value, "setScore");
            return (Criteria) this;
        }

        public Criteria andSetScoreLike(String value) {
            addCriterion("set_score like", value, "setScore");
            return (Criteria) this;
        }

        public Criteria andSetScoreNotLike(String value) {
            addCriterion("set_score not like", value, "setScore");
            return (Criteria) this;
        }

        public Criteria andSetScoreIn(List<String> values) {
            addCriterion("set_score in", values, "setScore");
            return (Criteria) this;
        }

        public Criteria andSetScoreNotIn(List<String> values) {
            addCriterion("set_score not in", values, "setScore");
            return (Criteria) this;
        }

        public Criteria andSetScoreBetween(String value1, String value2) {
            addCriterion("set_score between", value1, value2, "setScore");
            return (Criteria) this;
        }

        public Criteria andSetScoreNotBetween(String value1, String value2) {
            addCriterion("set_score not between", value1, value2, "setScore");
            return (Criteria) this;
        }

        public Criteria andSet1ScoreIsNull() {
            addCriterion("set1_score is null");
            return (Criteria) this;
        }

        public Criteria andSet1ScoreIsNotNull() {
            addCriterion("set1_score is not null");
            return (Criteria) this;
        }

        public Criteria andSet1ScoreEqualTo(String value) {
            addCriterion("set1_score =", value, "set1Score");
            return (Criteria) this;
        }

        public Criteria andSet1ScoreNotEqualTo(String value) {
            addCriterion("set1_score <>", value, "set1Score");
            return (Criteria) this;
        }

        public Criteria andSet1ScoreGreaterThan(String value) {
            addCriterion("set1_score >", value, "set1Score");
            return (Criteria) this;
        }

        public Criteria andSet1ScoreGreaterThanOrEqualTo(String value) {
            addCriterion("set1_score >=", value, "set1Score");
            return (Criteria) this;
        }

        public Criteria andSet1ScoreLessThan(String value) {
            addCriterion("set1_score <", value, "set1Score");
            return (Criteria) this;
        }

        public Criteria andSet1ScoreLessThanOrEqualTo(String value) {
            addCriterion("set1_score <=", value, "set1Score");
            return (Criteria) this;
        }

        public Criteria andSet1ScoreLike(String value) {
            addCriterion("set1_score like", value, "set1Score");
            return (Criteria) this;
        }

        public Criteria andSet1ScoreNotLike(String value) {
            addCriterion("set1_score not like", value, "set1Score");
            return (Criteria) this;
        }

        public Criteria andSet1ScoreIn(List<String> values) {
            addCriterion("set1_score in", values, "set1Score");
            return (Criteria) this;
        }

        public Criteria andSet1ScoreNotIn(List<String> values) {
            addCriterion("set1_score not in", values, "set1Score");
            return (Criteria) this;
        }

        public Criteria andSet1ScoreBetween(String value1, String value2) {
            addCriterion("set1_score between", value1, value2, "set1Score");
            return (Criteria) this;
        }

        public Criteria andSet1ScoreNotBetween(String value1, String value2) {
            addCriterion("set1_score not between", value1, value2, "set1Score");
            return (Criteria) this;
        }

        public Criteria andSet2ScoreIsNull() {
            addCriterion("set2_score is null");
            return (Criteria) this;
        }

        public Criteria andSet2ScoreIsNotNull() {
            addCriterion("set2_score is not null");
            return (Criteria) this;
        }

        public Criteria andSet2ScoreEqualTo(String value) {
            addCriterion("set2_score =", value, "set2Score");
            return (Criteria) this;
        }

        public Criteria andSet2ScoreNotEqualTo(String value) {
            addCriterion("set2_score <>", value, "set2Score");
            return (Criteria) this;
        }

        public Criteria andSet2ScoreGreaterThan(String value) {
            addCriterion("set2_score >", value, "set2Score");
            return (Criteria) this;
        }

        public Criteria andSet2ScoreGreaterThanOrEqualTo(String value) {
            addCriterion("set2_score >=", value, "set2Score");
            return (Criteria) this;
        }

        public Criteria andSet2ScoreLessThan(String value) {
            addCriterion("set2_score <", value, "set2Score");
            return (Criteria) this;
        }

        public Criteria andSet2ScoreLessThanOrEqualTo(String value) {
            addCriterion("set2_score <=", value, "set2Score");
            return (Criteria) this;
        }

        public Criteria andSet2ScoreLike(String value) {
            addCriterion("set2_score like", value, "set2Score");
            return (Criteria) this;
        }

        public Criteria andSet2ScoreNotLike(String value) {
            addCriterion("set2_score not like", value, "set2Score");
            return (Criteria) this;
        }

        public Criteria andSet2ScoreIn(List<String> values) {
            addCriterion("set2_score in", values, "set2Score");
            return (Criteria) this;
        }

        public Criteria andSet2ScoreNotIn(List<String> values) {
            addCriterion("set2_score not in", values, "set2Score");
            return (Criteria) this;
        }

        public Criteria andSet2ScoreBetween(String value1, String value2) {
            addCriterion("set2_score between", value1, value2, "set2Score");
            return (Criteria) this;
        }

        public Criteria andSet2ScoreNotBetween(String value1, String value2) {
            addCriterion("set2_score not between", value1, value2, "set2Score");
            return (Criteria) this;
        }

        public Criteria andSet3ScoreIsNull() {
            addCriterion("set3_score is null");
            return (Criteria) this;
        }

        public Criteria andSet3ScoreIsNotNull() {
            addCriterion("set3_score is not null");
            return (Criteria) this;
        }

        public Criteria andSet3ScoreEqualTo(String value) {
            addCriterion("set3_score =", value, "set3Score");
            return (Criteria) this;
        }

        public Criteria andSet3ScoreNotEqualTo(String value) {
            addCriterion("set3_score <>", value, "set3Score");
            return (Criteria) this;
        }

        public Criteria andSet3ScoreGreaterThan(String value) {
            addCriterion("set3_score >", value, "set3Score");
            return (Criteria) this;
        }

        public Criteria andSet3ScoreGreaterThanOrEqualTo(String value) {
            addCriterion("set3_score >=", value, "set3Score");
            return (Criteria) this;
        }

        public Criteria andSet3ScoreLessThan(String value) {
            addCriterion("set3_score <", value, "set3Score");
            return (Criteria) this;
        }

        public Criteria andSet3ScoreLessThanOrEqualTo(String value) {
            addCriterion("set3_score <=", value, "set3Score");
            return (Criteria) this;
        }

        public Criteria andSet3ScoreLike(String value) {
            addCriterion("set3_score like", value, "set3Score");
            return (Criteria) this;
        }

        public Criteria andSet3ScoreNotLike(String value) {
            addCriterion("set3_score not like", value, "set3Score");
            return (Criteria) this;
        }

        public Criteria andSet3ScoreIn(List<String> values) {
            addCriterion("set3_score in", values, "set3Score");
            return (Criteria) this;
        }

        public Criteria andSet3ScoreNotIn(List<String> values) {
            addCriterion("set3_score not in", values, "set3Score");
            return (Criteria) this;
        }

        public Criteria andSet3ScoreBetween(String value1, String value2) {
            addCriterion("set3_score between", value1, value2, "set3Score");
            return (Criteria) this;
        }

        public Criteria andSet3ScoreNotBetween(String value1, String value2) {
            addCriterion("set3_score not between", value1, value2, "set3Score");
            return (Criteria) this;
        }

        public Criteria andSet4ScoreIsNull() {
            addCriterion("set4_score is null");
            return (Criteria) this;
        }

        public Criteria andSet4ScoreIsNotNull() {
            addCriterion("set4_score is not null");
            return (Criteria) this;
        }

        public Criteria andSet4ScoreEqualTo(String value) {
            addCriterion("set4_score =", value, "set4Score");
            return (Criteria) this;
        }

        public Criteria andSet4ScoreNotEqualTo(String value) {
            addCriterion("set4_score <>", value, "set4Score");
            return (Criteria) this;
        }

        public Criteria andSet4ScoreGreaterThan(String value) {
            addCriterion("set4_score >", value, "set4Score");
            return (Criteria) this;
        }

        public Criteria andSet4ScoreGreaterThanOrEqualTo(String value) {
            addCriterion("set4_score >=", value, "set4Score");
            return (Criteria) this;
        }

        public Criteria andSet4ScoreLessThan(String value) {
            addCriterion("set4_score <", value, "set4Score");
            return (Criteria) this;
        }

        public Criteria andSet4ScoreLessThanOrEqualTo(String value) {
            addCriterion("set4_score <=", value, "set4Score");
            return (Criteria) this;
        }

        public Criteria andSet4ScoreLike(String value) {
            addCriterion("set4_score like", value, "set4Score");
            return (Criteria) this;
        }

        public Criteria andSet4ScoreNotLike(String value) {
            addCriterion("set4_score not like", value, "set4Score");
            return (Criteria) this;
        }

        public Criteria andSet4ScoreIn(List<String> values) {
            addCriterion("set4_score in", values, "set4Score");
            return (Criteria) this;
        }

        public Criteria andSet4ScoreNotIn(List<String> values) {
            addCriterion("set4_score not in", values, "set4Score");
            return (Criteria) this;
        }

        public Criteria andSet4ScoreBetween(String value1, String value2) {
            addCriterion("set4_score between", value1, value2, "set4Score");
            return (Criteria) this;
        }

        public Criteria andSet4ScoreNotBetween(String value1, String value2) {
            addCriterion("set4_score not between", value1, value2, "set4Score");
            return (Criteria) this;
        }

        public Criteria andSet5ScoreIsNull() {
            addCriterion("set5_score is null");
            return (Criteria) this;
        }

        public Criteria andSet5ScoreIsNotNull() {
            addCriterion("set5_score is not null");
            return (Criteria) this;
        }

        public Criteria andSet5ScoreEqualTo(String value) {
            addCriterion("set5_score =", value, "set5Score");
            return (Criteria) this;
        }

        public Criteria andSet5ScoreNotEqualTo(String value) {
            addCriterion("set5_score <>", value, "set5Score");
            return (Criteria) this;
        }

        public Criteria andSet5ScoreGreaterThan(String value) {
            addCriterion("set5_score >", value, "set5Score");
            return (Criteria) this;
        }

        public Criteria andSet5ScoreGreaterThanOrEqualTo(String value) {
            addCriterion("set5_score >=", value, "set5Score");
            return (Criteria) this;
        }

        public Criteria andSet5ScoreLessThan(String value) {
            addCriterion("set5_score <", value, "set5Score");
            return (Criteria) this;
        }

        public Criteria andSet5ScoreLessThanOrEqualTo(String value) {
            addCriterion("set5_score <=", value, "set5Score");
            return (Criteria) this;
        }

        public Criteria andSet5ScoreLike(String value) {
            addCriterion("set5_score like", value, "set5Score");
            return (Criteria) this;
        }

        public Criteria andSet5ScoreNotLike(String value) {
            addCriterion("set5_score not like", value, "set5Score");
            return (Criteria) this;
        }

        public Criteria andSet5ScoreIn(List<String> values) {
            addCriterion("set5_score in", values, "set5Score");
            return (Criteria) this;
        }

        public Criteria andSet5ScoreNotIn(List<String> values) {
            addCriterion("set5_score not in", values, "set5Score");
            return (Criteria) this;
        }

        public Criteria andSet5ScoreBetween(String value1, String value2) {
            addCriterion("set5_score between", value1, value2, "set5Score");
            return (Criteria) this;
        }

        public Criteria andSet5ScoreNotBetween(String value1, String value2) {
            addCriterion("set5_score not between", value1, value2, "set5Score");
            return (Criteria) this;
        }

        public Criteria andSet6ScoreIsNull() {
            addCriterion("set6_score is null");
            return (Criteria) this;
        }

        public Criteria andSet6ScoreIsNotNull() {
            addCriterion("set6_score is not null");
            return (Criteria) this;
        }

        public Criteria andSet6ScoreEqualTo(String value) {
            addCriterion("set6_score =", value, "set6Score");
            return (Criteria) this;
        }

        public Criteria andSet6ScoreNotEqualTo(String value) {
            addCriterion("set6_score <>", value, "set6Score");
            return (Criteria) this;
        }

        public Criteria andSet6ScoreGreaterThan(String value) {
            addCriterion("set6_score >", value, "set6Score");
            return (Criteria) this;
        }

        public Criteria andSet6ScoreGreaterThanOrEqualTo(String value) {
            addCriterion("set6_score >=", value, "set6Score");
            return (Criteria) this;
        }

        public Criteria andSet6ScoreLessThan(String value) {
            addCriterion("set6_score <", value, "set6Score");
            return (Criteria) this;
        }

        public Criteria andSet6ScoreLessThanOrEqualTo(String value) {
            addCriterion("set6_score <=", value, "set6Score");
            return (Criteria) this;
        }

        public Criteria andSet6ScoreLike(String value) {
            addCriterion("set6_score like", value, "set6Score");
            return (Criteria) this;
        }

        public Criteria andSet6ScoreNotLike(String value) {
            addCriterion("set6_score not like", value, "set6Score");
            return (Criteria) this;
        }

        public Criteria andSet6ScoreIn(List<String> values) {
            addCriterion("set6_score in", values, "set6Score");
            return (Criteria) this;
        }

        public Criteria andSet6ScoreNotIn(List<String> values) {
            addCriterion("set6_score not in", values, "set6Score");
            return (Criteria) this;
        }

        public Criteria andSet6ScoreBetween(String value1, String value2) {
            addCriterion("set6_score between", value1, value2, "set6Score");
            return (Criteria) this;
        }

        public Criteria andSet6ScoreNotBetween(String value1, String value2) {
            addCriterion("set6_score not between", value1, value2, "set6Score");
            return (Criteria) this;
        }

        public Criteria andSet7ScoreIsNull() {
            addCriterion("set7_score is null");
            return (Criteria) this;
        }

        public Criteria andSet7ScoreIsNotNull() {
            addCriterion("set7_score is not null");
            return (Criteria) this;
        }

        public Criteria andSet7ScoreEqualTo(String value) {
            addCriterion("set7_score =", value, "set7Score");
            return (Criteria) this;
        }

        public Criteria andSet7ScoreNotEqualTo(String value) {
            addCriterion("set7_score <>", value, "set7Score");
            return (Criteria) this;
        }

        public Criteria andSet7ScoreGreaterThan(String value) {
            addCriterion("set7_score >", value, "set7Score");
            return (Criteria) this;
        }

        public Criteria andSet7ScoreGreaterThanOrEqualTo(String value) {
            addCriterion("set7_score >=", value, "set7Score");
            return (Criteria) this;
        }

        public Criteria andSet7ScoreLessThan(String value) {
            addCriterion("set7_score <", value, "set7Score");
            return (Criteria) this;
        }

        public Criteria andSet7ScoreLessThanOrEqualTo(String value) {
            addCriterion("set7_score <=", value, "set7Score");
            return (Criteria) this;
        }

        public Criteria andSet7ScoreLike(String value) {
            addCriterion("set7_score like", value, "set7Score");
            return (Criteria) this;
        }

        public Criteria andSet7ScoreNotLike(String value) {
            addCriterion("set7_score not like", value, "set7Score");
            return (Criteria) this;
        }

        public Criteria andSet7ScoreIn(List<String> values) {
            addCriterion("set7_score in", values, "set7Score");
            return (Criteria) this;
        }

        public Criteria andSet7ScoreNotIn(List<String> values) {
            addCriterion("set7_score not in", values, "set7Score");
            return (Criteria) this;
        }

        public Criteria andSet7ScoreBetween(String value1, String value2) {
            addCriterion("set7_score between", value1, value2, "set7Score");
            return (Criteria) this;
        }

        public Criteria andSet7ScoreNotBetween(String value1, String value2) {
            addCriterion("set7_score not between", value1, value2, "set7Score");
            return (Criteria) this;
        }

        public Criteria andSet8ScoreIsNull() {
            addCriterion("set8_score is null");
            return (Criteria) this;
        }

        public Criteria andSet8ScoreIsNotNull() {
            addCriterion("set8_score is not null");
            return (Criteria) this;
        }

        public Criteria andSet8ScoreEqualTo(String value) {
            addCriterion("set8_score =", value, "set8Score");
            return (Criteria) this;
        }

        public Criteria andSet8ScoreNotEqualTo(String value) {
            addCriterion("set8_score <>", value, "set8Score");
            return (Criteria) this;
        }

        public Criteria andSet8ScoreGreaterThan(String value) {
            addCriterion("set8_score >", value, "set8Score");
            return (Criteria) this;
        }

        public Criteria andSet8ScoreGreaterThanOrEqualTo(String value) {
            addCriterion("set8_score >=", value, "set8Score");
            return (Criteria) this;
        }

        public Criteria andSet8ScoreLessThan(String value) {
            addCriterion("set8_score <", value, "set8Score");
            return (Criteria) this;
        }

        public Criteria andSet8ScoreLessThanOrEqualTo(String value) {
            addCriterion("set8_score <=", value, "set8Score");
            return (Criteria) this;
        }

        public Criteria andSet8ScoreLike(String value) {
            addCriterion("set8_score like", value, "set8Score");
            return (Criteria) this;
        }

        public Criteria andSet8ScoreNotLike(String value) {
            addCriterion("set8_score not like", value, "set8Score");
            return (Criteria) this;
        }

        public Criteria andSet8ScoreIn(List<String> values) {
            addCriterion("set8_score in", values, "set8Score");
            return (Criteria) this;
        }

        public Criteria andSet8ScoreNotIn(List<String> values) {
            addCriterion("set8_score not in", values, "set8Score");
            return (Criteria) this;
        }

        public Criteria andSet8ScoreBetween(String value1, String value2) {
            addCriterion("set8_score between", value1, value2, "set8Score");
            return (Criteria) this;
        }

        public Criteria andSet8ScoreNotBetween(String value1, String value2) {
            addCriterion("set8_score not between", value1, value2, "set8Score");
            return (Criteria) this;
        }

        public Criteria andSet9ScoreIsNull() {
            addCriterion("set9_score is null");
            return (Criteria) this;
        }

        public Criteria andSet9ScoreIsNotNull() {
            addCriterion("set9_score is not null");
            return (Criteria) this;
        }

        public Criteria andSet9ScoreEqualTo(String value) {
            addCriterion("set9_score =", value, "set9Score");
            return (Criteria) this;
        }

        public Criteria andSet9ScoreNotEqualTo(String value) {
            addCriterion("set9_score <>", value, "set9Score");
            return (Criteria) this;
        }

        public Criteria andSet9ScoreGreaterThan(String value) {
            addCriterion("set9_score >", value, "set9Score");
            return (Criteria) this;
        }

        public Criteria andSet9ScoreGreaterThanOrEqualTo(String value) {
            addCriterion("set9_score >=", value, "set9Score");
            return (Criteria) this;
        }

        public Criteria andSet9ScoreLessThan(String value) {
            addCriterion("set9_score <", value, "set9Score");
            return (Criteria) this;
        }

        public Criteria andSet9ScoreLessThanOrEqualTo(String value) {
            addCriterion("set9_score <=", value, "set9Score");
            return (Criteria) this;
        }

        public Criteria andSet9ScoreLike(String value) {
            addCriterion("set9_score like", value, "set9Score");
            return (Criteria) this;
        }

        public Criteria andSet9ScoreNotLike(String value) {
            addCriterion("set9_score not like", value, "set9Score");
            return (Criteria) this;
        }

        public Criteria andSet9ScoreIn(List<String> values) {
            addCriterion("set9_score in", values, "set9Score");
            return (Criteria) this;
        }

        public Criteria andSet9ScoreNotIn(List<String> values) {
            addCriterion("set9_score not in", values, "set9Score");
            return (Criteria) this;
        }

        public Criteria andSet9ScoreBetween(String value1, String value2) {
            addCriterion("set9_score between", value1, value2, "set9Score");
            return (Criteria) this;
        }

        public Criteria andSet9ScoreNotBetween(String value1, String value2) {
            addCriterion("set9_score not between", value1, value2, "set9Score");
            return (Criteria) this;
        }

        public Criteria andSet10ScoreIsNull() {
            addCriterion("set10_score is null");
            return (Criteria) this;
        }

        public Criteria andSet10ScoreIsNotNull() {
            addCriterion("set10_score is not null");
            return (Criteria) this;
        }

        public Criteria andSet10ScoreEqualTo(String value) {
            addCriterion("set10_score =", value, "set10Score");
            return (Criteria) this;
        }

        public Criteria andSet10ScoreNotEqualTo(String value) {
            addCriterion("set10_score <>", value, "set10Score");
            return (Criteria) this;
        }

        public Criteria andSet10ScoreGreaterThan(String value) {
            addCriterion("set10_score >", value, "set10Score");
            return (Criteria) this;
        }

        public Criteria andSet10ScoreGreaterThanOrEqualTo(String value) {
            addCriterion("set10_score >=", value, "set10Score");
            return (Criteria) this;
        }

        public Criteria andSet10ScoreLessThan(String value) {
            addCriterion("set10_score <", value, "set10Score");
            return (Criteria) this;
        }

        public Criteria andSet10ScoreLessThanOrEqualTo(String value) {
            addCriterion("set10_score <=", value, "set10Score");
            return (Criteria) this;
        }

        public Criteria andSet10ScoreLike(String value) {
            addCriterion("set10_score like", value, "set10Score");
            return (Criteria) this;
        }

        public Criteria andSet10ScoreNotLike(String value) {
            addCriterion("set10_score not like", value, "set10Score");
            return (Criteria) this;
        }

        public Criteria andSet10ScoreIn(List<String> values) {
            addCriterion("set10_score in", values, "set10Score");
            return (Criteria) this;
        }

        public Criteria andSet10ScoreNotIn(List<String> values) {
            addCriterion("set10_score not in", values, "set10Score");
            return (Criteria) this;
        }

        public Criteria andSet10ScoreBetween(String value1, String value2) {
            addCriterion("set10_score between", value1, value2, "set10Score");
            return (Criteria) this;
        }

        public Criteria andSet10ScoreNotBetween(String value1, String value2) {
            addCriterion("set10_score not between", value1, value2, "set10Score");
            return (Criteria) this;
        }

        public Criteria andGameScoreIsNull() {
            addCriterion("game_score is null");
            return (Criteria) this;
        }

        public Criteria andGameScoreIsNotNull() {
            addCriterion("game_score is not null");
            return (Criteria) this;
        }

        public Criteria andGameScoreEqualTo(String value) {
            addCriterion("game_score =", value, "gameScore");
            return (Criteria) this;
        }

        public Criteria andGameScoreNotEqualTo(String value) {
            addCriterion("game_score <>", value, "gameScore");
            return (Criteria) this;
        }

        public Criteria andGameScoreGreaterThan(String value) {
            addCriterion("game_score >", value, "gameScore");
            return (Criteria) this;
        }

        public Criteria andGameScoreGreaterThanOrEqualTo(String value) {
            addCriterion("game_score >=", value, "gameScore");
            return (Criteria) this;
        }

        public Criteria andGameScoreLessThan(String value) {
            addCriterion("game_score <", value, "gameScore");
            return (Criteria) this;
        }

        public Criteria andGameScoreLessThanOrEqualTo(String value) {
            addCriterion("game_score <=", value, "gameScore");
            return (Criteria) this;
        }

        public Criteria andGameScoreLike(String value) {
            addCriterion("game_score like", value, "gameScore");
            return (Criteria) this;
        }

        public Criteria andGameScoreNotLike(String value) {
            addCriterion("game_score not like", value, "gameScore");
            return (Criteria) this;
        }

        public Criteria andGameScoreIn(List<String> values) {
            addCriterion("game_score in", values, "gameScore");
            return (Criteria) this;
        }

        public Criteria andGameScoreNotIn(List<String> values) {
            addCriterion("game_score not in", values, "gameScore");
            return (Criteria) this;
        }

        public Criteria andGameScoreBetween(String value1, String value2) {
            addCriterion("game_score between", value1, value2, "gameScore");
            return (Criteria) this;
        }

        public Criteria andGameScoreNotBetween(String value1, String value2) {
            addCriterion("game_score not between", value1, value2, "gameScore");
            return (Criteria) this;
        }

        public Criteria andServerIsNull() {
            addCriterion("server is null");
            return (Criteria) this;
        }

        public Criteria andServerIsNotNull() {
            addCriterion("server is not null");
            return (Criteria) this;
        }

        public Criteria andServerEqualTo(Integer value) {
            addCriterion("server =", value, "server");
            return (Criteria) this;
        }

        public Criteria andServerNotEqualTo(Integer value) {
            addCriterion("server <>", value, "server");
            return (Criteria) this;
        }

        public Criteria andServerGreaterThan(Integer value) {
            addCriterion("server >", value, "server");
            return (Criteria) this;
        }

        public Criteria andServerGreaterThanOrEqualTo(Integer value) {
            addCriterion("server >=", value, "server");
            return (Criteria) this;
        }

        public Criteria andServerLessThan(Integer value) {
            addCriterion("server <", value, "server");
            return (Criteria) this;
        }

        public Criteria andServerLessThanOrEqualTo(Integer value) {
            addCriterion("server <=", value, "server");
            return (Criteria) this;
        }

        public Criteria andServerIn(List<Integer> values) {
            addCriterion("server in", values, "server");
            return (Criteria) this;
        }

        public Criteria andServerNotIn(List<Integer> values) {
            addCriterion("server not in", values, "server");
            return (Criteria) this;
        }

        public Criteria andServerBetween(Integer value1, Integer value2) {
            addCriterion("server between", value1, value2, "server");
            return (Criteria) this;
        }

        public Criteria andServerNotBetween(Integer value1, Integer value2) {
            addCriterion("server not between", value1, value2, "server");
            return (Criteria) this;
        }

        public Criteria andInfoIsNull() {
            addCriterion("info is null");
            return (Criteria) this;
        }

        public Criteria andInfoIsNotNull() {
            addCriterion("info is not null");
            return (Criteria) this;
        }

        public Criteria andInfoEqualTo(String value) {
            addCriterion("info =", value, "info");
            return (Criteria) this;
        }

        public Criteria andInfoNotEqualTo(String value) {
            addCriterion("info <>", value, "info");
            return (Criteria) this;
        }

        public Criteria andInfoGreaterThan(String value) {
            addCriterion("info >", value, "info");
            return (Criteria) this;
        }

        public Criteria andInfoGreaterThanOrEqualTo(String value) {
            addCriterion("info >=", value, "info");
            return (Criteria) this;
        }

        public Criteria andInfoLessThan(String value) {
            addCriterion("info <", value, "info");
            return (Criteria) this;
        }

        public Criteria andInfoLessThanOrEqualTo(String value) {
            addCriterion("info <=", value, "info");
            return (Criteria) this;
        }

        public Criteria andInfoLike(String value) {
            addCriterion("info like", value, "info");
            return (Criteria) this;
        }

        public Criteria andInfoNotLike(String value) {
            addCriterion("info not like", value, "info");
            return (Criteria) this;
        }

        public Criteria andInfoIn(List<String> values) {
            addCriterion("info in", values, "info");
            return (Criteria) this;
        }

        public Criteria andInfoNotIn(List<String> values) {
            addCriterion("info not in", values, "info");
            return (Criteria) this;
        }

        public Criteria andInfoBetween(String value1, String value2) {
            addCriterion("info between", value1, value2, "info");
            return (Criteria) this;
        }

        public Criteria andInfoNotBetween(String value1, String value2) {
            addCriterion("info not between", value1, value2, "info");
            return (Criteria) this;
        }

        public Criteria andRemainingTimeIsNull() {
            addCriterion("remaining_time is null");
            return (Criteria) this;
        }

        public Criteria andRemainingTimeIsNotNull() {
            addCriterion("remaining_time is not null");
            return (Criteria) this;
        }

        public Criteria andRemainingTimeEqualTo(Integer value) {
            addCriterion("remaining_time =", value, "remainingTime");
            return (Criteria) this;
        }

        public Criteria andRemainingTimeNotEqualTo(Integer value) {
            addCriterion("remaining_time <>", value, "remainingTime");
            return (Criteria) this;
        }

        public Criteria andRemainingTimeGreaterThan(Integer value) {
            addCriterion("remaining_time >", value, "remainingTime");
            return (Criteria) this;
        }

        public Criteria andRemainingTimeGreaterThanOrEqualTo(Integer value) {
            addCriterion("remaining_time >=", value, "remainingTime");
            return (Criteria) this;
        }

        public Criteria andRemainingTimeLessThan(Integer value) {
            addCriterion("remaining_time <", value, "remainingTime");
            return (Criteria) this;
        }

        public Criteria andRemainingTimeLessThanOrEqualTo(Integer value) {
            addCriterion("remaining_time <=", value, "remainingTime");
            return (Criteria) this;
        }

        public Criteria andRemainingTimeIn(List<Integer> values) {
            addCriterion("remaining_time in", values, "remainingTime");
            return (Criteria) this;
        }

        public Criteria andRemainingTimeNotIn(List<Integer> values) {
            addCriterion("remaining_time not in", values, "remainingTime");
            return (Criteria) this;
        }

        public Criteria andRemainingTimeBetween(Integer value1, Integer value2) {
            addCriterion("remaining_time between", value1, value2, "remainingTime");
            return (Criteria) this;
        }

        public Criteria andRemainingTimeNotBetween(Integer value1, Integer value2) {
            addCriterion("remaining_time not between", value1, value2, "remainingTime");
            return (Criteria) this;
        }

        public Criteria andPeriodIsNull() {
            addCriterion("period is null");
            return (Criteria) this;
        }

        public Criteria andPeriodIsNotNull() {
            addCriterion("period is not null");
            return (Criteria) this;
        }

        public Criteria andPeriodEqualTo(Integer value) {
            addCriterion("period =", value, "period");
            return (Criteria) this;
        }

        public Criteria andPeriodNotEqualTo(Integer value) {
            addCriterion("period <>", value, "period");
            return (Criteria) this;
        }

        public Criteria andPeriodGreaterThan(Integer value) {
            addCriterion("period >", value, "period");
            return (Criteria) this;
        }

        public Criteria andPeriodGreaterThanOrEqualTo(Integer value) {
            addCriterion("period >=", value, "period");
            return (Criteria) this;
        }

        public Criteria andPeriodLessThan(Integer value) {
            addCriterion("period <", value, "period");
            return (Criteria) this;
        }

        public Criteria andPeriodLessThanOrEqualTo(Integer value) {
            addCriterion("period <=", value, "period");
            return (Criteria) this;
        }

        public Criteria andPeriodIn(List<Integer> values) {
            addCriterion("period in", values, "period");
            return (Criteria) this;
        }

        public Criteria andPeriodNotIn(List<Integer> values) {
            addCriterion("period not in", values, "period");
            return (Criteria) this;
        }

        public Criteria andPeriodBetween(Integer value1, Integer value2) {
            addCriterion("period between", value1, value2, "period");
            return (Criteria) this;
        }

        public Criteria andPeriodNotBetween(Integer value1, Integer value2) {
            addCriterion("period not between", value1, value2, "period");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthIsNull() {
            addCriterion("period_length is null");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthIsNotNull() {
            addCriterion("period_length is not null");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthEqualTo(Integer value) {
            addCriterion("period_length =", value, "periodLength");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthNotEqualTo(Integer value) {
            addCriterion("period_length <>", value, "periodLength");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthGreaterThan(Integer value) {
            addCriterion("period_length >", value, "periodLength");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthGreaterThanOrEqualTo(Integer value) {
            addCriterion("period_length >=", value, "periodLength");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthLessThan(Integer value) {
            addCriterion("period_length <", value, "periodLength");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthLessThanOrEqualTo(Integer value) {
            addCriterion("period_length <=", value, "periodLength");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthIn(List<Integer> values) {
            addCriterion("period_length in", values, "periodLength");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthNotIn(List<Integer> values) {
            addCriterion("period_length not in", values, "periodLength");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthBetween(Integer value1, Integer value2) {
            addCriterion("period_length between", value1, value2, "periodLength");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthNotBetween(Integer value1, Integer value2) {
            addCriterion("period_length not between", value1, value2, "periodLength");
            return (Criteria) this;
        }

        public Criteria andSetCountIsNull() {
            addCriterion("set_count is null");
            return (Criteria) this;
        }

        public Criteria andSetCountIsNotNull() {
            addCriterion("set_count is not null");
            return (Criteria) this;
        }

        public Criteria andSetCountEqualTo(Integer value) {
            addCriterion("set_count =", value, "setCount");
            return (Criteria) this;
        }

        public Criteria andSetCountNotEqualTo(Integer value) {
            addCriterion("set_count <>", value, "setCount");
            return (Criteria) this;
        }

        public Criteria andSetCountGreaterThan(Integer value) {
            addCriterion("set_count >", value, "setCount");
            return (Criteria) this;
        }

        public Criteria andSetCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("set_count >=", value, "setCount");
            return (Criteria) this;
        }

        public Criteria andSetCountLessThan(Integer value) {
            addCriterion("set_count <", value, "setCount");
            return (Criteria) this;
        }

        public Criteria andSetCountLessThanOrEqualTo(Integer value) {
            addCriterion("set_count <=", value, "setCount");
            return (Criteria) this;
        }

        public Criteria andSetCountIn(List<Integer> values) {
            addCriterion("set_count in", values, "setCount");
            return (Criteria) this;
        }

        public Criteria andSetCountNotIn(List<Integer> values) {
            addCriterion("set_count not in", values, "setCount");
            return (Criteria) this;
        }

        public Criteria andSetCountBetween(Integer value1, Integer value2) {
            addCriterion("set_count between", value1, value2, "setCount");
            return (Criteria) this;
        }

        public Criteria andSetCountNotBetween(Integer value1, Integer value2) {
            addCriterion("set_count not between", value1, value2, "setCount");
            return (Criteria) this;
        }

        public Criteria andPenaltyScoreIsNull() {
            addCriterion("penalty_score is null");
            return (Criteria) this;
        }

        public Criteria andPenaltyScoreIsNotNull() {
            addCriterion("penalty_score is not null");
            return (Criteria) this;
        }

        public Criteria andPenaltyScoreEqualTo(String value) {
            addCriterion("penalty_score =", value, "penaltyScore");
            return (Criteria) this;
        }

        public Criteria andPenaltyScoreNotEqualTo(String value) {
            addCriterion("penalty_score <>", value, "penaltyScore");
            return (Criteria) this;
        }

        public Criteria andPenaltyScoreGreaterThan(String value) {
            addCriterion("penalty_score >", value, "penaltyScore");
            return (Criteria) this;
        }

        public Criteria andPenaltyScoreGreaterThanOrEqualTo(String value) {
            addCriterion("penalty_score >=", value, "penaltyScore");
            return (Criteria) this;
        }

        public Criteria andPenaltyScoreLessThan(String value) {
            addCriterion("penalty_score <", value, "penaltyScore");
            return (Criteria) this;
        }

        public Criteria andPenaltyScoreLessThanOrEqualTo(String value) {
            addCriterion("penalty_score <=", value, "penaltyScore");
            return (Criteria) this;
        }

        public Criteria andPenaltyScoreLike(String value) {
            addCriterion("penalty_score like", value, "penaltyScore");
            return (Criteria) this;
        }

        public Criteria andPenaltyScoreNotLike(String value) {
            addCriterion("penalty_score not like", value, "penaltyScore");
            return (Criteria) this;
        }

        public Criteria andPenaltyScoreIn(List<String> values) {
            addCriterion("penalty_score in", values, "penaltyScore");
            return (Criteria) this;
        }

        public Criteria andPenaltyScoreNotIn(List<String> values) {
            addCriterion("penalty_score not in", values, "penaltyScore");
            return (Criteria) this;
        }

        public Criteria andPenaltyScoreBetween(String value1, String value2) {
            addCriterion("penalty_score between", value1, value2, "penaltyScore");
            return (Criteria) this;
        }

        public Criteria andPenaltyScoreNotBetween(String value1, String value2) {
            addCriterion("penalty_score not between", value1, value2, "penaltyScore");
            return (Criteria) this;
        }

        public Criteria andFreeKickScoreIsNull() {
            addCriterion("free_kick_score is null");
            return (Criteria) this;
        }

        public Criteria andFreeKickScoreIsNotNull() {
            addCriterion("free_kick_score is not null");
            return (Criteria) this;
        }

        public Criteria andFreeKickScoreEqualTo(String value) {
            addCriterion("free_kick_score =", value, "freeKickScore");
            return (Criteria) this;
        }

        public Criteria andFreeKickScoreNotEqualTo(String value) {
            addCriterion("free_kick_score <>", value, "freeKickScore");
            return (Criteria) this;
        }

        public Criteria andFreeKickScoreGreaterThan(String value) {
            addCriterion("free_kick_score >", value, "freeKickScore");
            return (Criteria) this;
        }

        public Criteria andFreeKickScoreGreaterThanOrEqualTo(String value) {
            addCriterion("free_kick_score >=", value, "freeKickScore");
            return (Criteria) this;
        }

        public Criteria andFreeKickScoreLessThan(String value) {
            addCriterion("free_kick_score <", value, "freeKickScore");
            return (Criteria) this;
        }

        public Criteria andFreeKickScoreLessThanOrEqualTo(String value) {
            addCriterion("free_kick_score <=", value, "freeKickScore");
            return (Criteria) this;
        }

        public Criteria andFreeKickScoreLike(String value) {
            addCriterion("free_kick_score like", value, "freeKickScore");
            return (Criteria) this;
        }

        public Criteria andFreeKickScoreNotLike(String value) {
            addCriterion("free_kick_score not like", value, "freeKickScore");
            return (Criteria) this;
        }

        public Criteria andFreeKickScoreIn(List<String> values) {
            addCriterion("free_kick_score in", values, "freeKickScore");
            return (Criteria) this;
        }

        public Criteria andFreeKickScoreNotIn(List<String> values) {
            addCriterion("free_kick_score not in", values, "freeKickScore");
            return (Criteria) this;
        }

        public Criteria andFreeKickScoreBetween(String value1, String value2) {
            addCriterion("free_kick_score between", value1, value2, "freeKickScore");
            return (Criteria) this;
        }

        public Criteria andFreeKickScoreNotBetween(String value1, String value2) {
            addCriterion("free_kick_score not between", value1, value2, "freeKickScore");
            return (Criteria) this;
        }

        public Criteria andExtraTimeScoreIsNull() {
            addCriterion("extra_time_score is null");
            return (Criteria) this;
        }

        public Criteria andExtraTimeScoreIsNotNull() {
            addCriterion("extra_time_score is not null");
            return (Criteria) this;
        }

        public Criteria andExtraTimeScoreEqualTo(String value) {
            addCriterion("extra_time_score =", value, "extraTimeScore");
            return (Criteria) this;
        }

        public Criteria andExtraTimeScoreNotEqualTo(String value) {
            addCriterion("extra_time_score <>", value, "extraTimeScore");
            return (Criteria) this;
        }

        public Criteria andExtraTimeScoreGreaterThan(String value) {
            addCriterion("extra_time_score >", value, "extraTimeScore");
            return (Criteria) this;
        }

        public Criteria andExtraTimeScoreGreaterThanOrEqualTo(String value) {
            addCriterion("extra_time_score >=", value, "extraTimeScore");
            return (Criteria) this;
        }

        public Criteria andExtraTimeScoreLessThan(String value) {
            addCriterion("extra_time_score <", value, "extraTimeScore");
            return (Criteria) this;
        }

        public Criteria andExtraTimeScoreLessThanOrEqualTo(String value) {
            addCriterion("extra_time_score <=", value, "extraTimeScore");
            return (Criteria) this;
        }

        public Criteria andExtraTimeScoreLike(String value) {
            addCriterion("extra_time_score like", value, "extraTimeScore");
            return (Criteria) this;
        }

        public Criteria andExtraTimeScoreNotLike(String value) {
            addCriterion("extra_time_score not like", value, "extraTimeScore");
            return (Criteria) this;
        }

        public Criteria andExtraTimeScoreIn(List<String> values) {
            addCriterion("extra_time_score in", values, "extraTimeScore");
            return (Criteria) this;
        }

        public Criteria andExtraTimeScoreNotIn(List<String> values) {
            addCriterion("extra_time_score not in", values, "extraTimeScore");
            return (Criteria) this;
        }

        public Criteria andExtraTimeScoreBetween(String value1, String value2) {
            addCriterion("extra_time_score between", value1, value2, "extraTimeScore");
            return (Criteria) this;
        }

        public Criteria andExtraTimeScoreNotBetween(String value1, String value2) {
            addCriterion("extra_time_score not between", value1, value2, "extraTimeScore");
            return (Criteria) this;
        }

        public Criteria andSet1YellowCardScoreIsNull() {
            addCriterion("set1_yellow_card_score is null");
            return (Criteria) this;
        }

        public Criteria andSet1YellowCardScoreIsNotNull() {
            addCriterion("set1_yellow_card_score is not null");
            return (Criteria) this;
        }

        public Criteria andSet1YellowCardScoreEqualTo(String value) {
            addCriterion("set1_yellow_card_score =", value, "set1YellowCardScore");
            return (Criteria) this;
        }

        public Criteria andSet1YellowCardScoreNotEqualTo(String value) {
            addCriterion("set1_yellow_card_score <>", value, "set1YellowCardScore");
            return (Criteria) this;
        }

        public Criteria andSet1YellowCardScoreGreaterThan(String value) {
            addCriterion("set1_yellow_card_score >", value, "set1YellowCardScore");
            return (Criteria) this;
        }

        public Criteria andSet1YellowCardScoreGreaterThanOrEqualTo(String value) {
            addCriterion("set1_yellow_card_score >=", value, "set1YellowCardScore");
            return (Criteria) this;
        }

        public Criteria andSet1YellowCardScoreLessThan(String value) {
            addCriterion("set1_yellow_card_score <", value, "set1YellowCardScore");
            return (Criteria) this;
        }

        public Criteria andSet1YellowCardScoreLessThanOrEqualTo(String value) {
            addCriterion("set1_yellow_card_score <=", value, "set1YellowCardScore");
            return (Criteria) this;
        }

        public Criteria andSet1YellowCardScoreLike(String value) {
            addCriterion("set1_yellow_card_score like", value, "set1YellowCardScore");
            return (Criteria) this;
        }

        public Criteria andSet1YellowCardScoreNotLike(String value) {
            addCriterion("set1_yellow_card_score not like", value, "set1YellowCardScore");
            return (Criteria) this;
        }

        public Criteria andSet1YellowCardScoreIn(List<String> values) {
            addCriterion("set1_yellow_card_score in", values, "set1YellowCardScore");
            return (Criteria) this;
        }

        public Criteria andSet1YellowCardScoreNotIn(List<String> values) {
            addCriterion("set1_yellow_card_score not in", values, "set1YellowCardScore");
            return (Criteria) this;
        }

        public Criteria andSet1YellowCardScoreBetween(String value1, String value2) {
            addCriterion("set1_yellow_card_score between", value1, value2, "set1YellowCardScore");
            return (Criteria) this;
        }

        public Criteria andSet1YellowCardScoreNotBetween(String value1, String value2) {
            addCriterion("set1_yellow_card_score not between", value1, value2, "set1YellowCardScore");
            return (Criteria) this;
        }

        public Criteria andSet1RedCardScoreIsNull() {
            addCriterion("set1_red_card_score is null");
            return (Criteria) this;
        }

        public Criteria andSet1RedCardScoreIsNotNull() {
            addCriterion("set1_red_card_score is not null");
            return (Criteria) this;
        }

        public Criteria andSet1RedCardScoreEqualTo(String value) {
            addCriterion("set1_red_card_score =", value, "set1RedCardScore");
            return (Criteria) this;
        }

        public Criteria andSet1RedCardScoreNotEqualTo(String value) {
            addCriterion("set1_red_card_score <>", value, "set1RedCardScore");
            return (Criteria) this;
        }

        public Criteria andSet1RedCardScoreGreaterThan(String value) {
            addCriterion("set1_red_card_score >", value, "set1RedCardScore");
            return (Criteria) this;
        }

        public Criteria andSet1RedCardScoreGreaterThanOrEqualTo(String value) {
            addCriterion("set1_red_card_score >=", value, "set1RedCardScore");
            return (Criteria) this;
        }

        public Criteria andSet1RedCardScoreLessThan(String value) {
            addCriterion("set1_red_card_score <", value, "set1RedCardScore");
            return (Criteria) this;
        }

        public Criteria andSet1RedCardScoreLessThanOrEqualTo(String value) {
            addCriterion("set1_red_card_score <=", value, "set1RedCardScore");
            return (Criteria) this;
        }

        public Criteria andSet1RedCardScoreLike(String value) {
            addCriterion("set1_red_card_score like", value, "set1RedCardScore");
            return (Criteria) this;
        }

        public Criteria andSet1RedCardScoreNotLike(String value) {
            addCriterion("set1_red_card_score not like", value, "set1RedCardScore");
            return (Criteria) this;
        }

        public Criteria andSet1RedCardScoreIn(List<String> values) {
            addCriterion("set1_red_card_score in", values, "set1RedCardScore");
            return (Criteria) this;
        }

        public Criteria andSet1RedCardScoreNotIn(List<String> values) {
            addCriterion("set1_red_card_score not in", values, "set1RedCardScore");
            return (Criteria) this;
        }

        public Criteria andSet1RedCardScoreBetween(String value1, String value2) {
            addCriterion("set1_red_card_score between", value1, value2, "set1RedCardScore");
            return (Criteria) this;
        }

        public Criteria andSet1RedCardScoreNotBetween(String value1, String value2) {
            addCriterion("set1_red_card_score not between", value1, value2, "set1RedCardScore");
            return (Criteria) this;
        }

        public Criteria andSet1CornerScoreIsNull() {
            addCriterion("set1_corner_score is null");
            return (Criteria) this;
        }

        public Criteria andSet1CornerScoreIsNotNull() {
            addCriterion("set1_corner_score is not null");
            return (Criteria) this;
        }

        public Criteria andSet1CornerScoreEqualTo(String value) {
            addCriterion("set1_corner_score =", value, "set1CornerScore");
            return (Criteria) this;
        }

        public Criteria andSet1CornerScoreNotEqualTo(String value) {
            addCriterion("set1_corner_score <>", value, "set1CornerScore");
            return (Criteria) this;
        }

        public Criteria andSet1CornerScoreGreaterThan(String value) {
            addCriterion("set1_corner_score >", value, "set1CornerScore");
            return (Criteria) this;
        }

        public Criteria andSet1CornerScoreGreaterThanOrEqualTo(String value) {
            addCriterion("set1_corner_score >=", value, "set1CornerScore");
            return (Criteria) this;
        }

        public Criteria andSet1CornerScoreLessThan(String value) {
            addCriterion("set1_corner_score <", value, "set1CornerScore");
            return (Criteria) this;
        }

        public Criteria andSet1CornerScoreLessThanOrEqualTo(String value) {
            addCriterion("set1_corner_score <=", value, "set1CornerScore");
            return (Criteria) this;
        }

        public Criteria andSet1CornerScoreLike(String value) {
            addCriterion("set1_corner_score like", value, "set1CornerScore");
            return (Criteria) this;
        }

        public Criteria andSet1CornerScoreNotLike(String value) {
            addCriterion("set1_corner_score not like", value, "set1CornerScore");
            return (Criteria) this;
        }

        public Criteria andSet1CornerScoreIn(List<String> values) {
            addCriterion("set1_corner_score in", values, "set1CornerScore");
            return (Criteria) this;
        }

        public Criteria andSet1CornerScoreNotIn(List<String> values) {
            addCriterion("set1_corner_score not in", values, "set1CornerScore");
            return (Criteria) this;
        }

        public Criteria andSet1CornerScoreBetween(String value1, String value2) {
            addCriterion("set1_corner_score between", value1, value2, "set1CornerScore");
            return (Criteria) this;
        }

        public Criteria andSet1CornerScoreNotBetween(String value1, String value2) {
            addCriterion("set1_corner_score not between", value1, value2, "set1CornerScore");
            return (Criteria) this;
        }

        public Criteria andSet2YellowCardScoreIsNull() {
            addCriterion("set2_yellow_card_score is null");
            return (Criteria) this;
        }

        public Criteria andSet2YellowCardScoreIsNotNull() {
            addCriterion("set2_yellow_card_score is not null");
            return (Criteria) this;
        }

        public Criteria andSet2YellowCardScoreEqualTo(String value) {
            addCriterion("set2_yellow_card_score =", value, "set2YellowCardScore");
            return (Criteria) this;
        }

        public Criteria andSet2YellowCardScoreNotEqualTo(String value) {
            addCriterion("set2_yellow_card_score <>", value, "set2YellowCardScore");
            return (Criteria) this;
        }

        public Criteria andSet2YellowCardScoreGreaterThan(String value) {
            addCriterion("set2_yellow_card_score >", value, "set2YellowCardScore");
            return (Criteria) this;
        }

        public Criteria andSet2YellowCardScoreGreaterThanOrEqualTo(String value) {
            addCriterion("set2_yellow_card_score >=", value, "set2YellowCardScore");
            return (Criteria) this;
        }

        public Criteria andSet2YellowCardScoreLessThan(String value) {
            addCriterion("set2_yellow_card_score <", value, "set2YellowCardScore");
            return (Criteria) this;
        }

        public Criteria andSet2YellowCardScoreLessThanOrEqualTo(String value) {
            addCriterion("set2_yellow_card_score <=", value, "set2YellowCardScore");
            return (Criteria) this;
        }

        public Criteria andSet2YellowCardScoreLike(String value) {
            addCriterion("set2_yellow_card_score like", value, "set2YellowCardScore");
            return (Criteria) this;
        }

        public Criteria andSet2YellowCardScoreNotLike(String value) {
            addCriterion("set2_yellow_card_score not like", value, "set2YellowCardScore");
            return (Criteria) this;
        }

        public Criteria andSet2YellowCardScoreIn(List<String> values) {
            addCriterion("set2_yellow_card_score in", values, "set2YellowCardScore");
            return (Criteria) this;
        }

        public Criteria andSet2YellowCardScoreNotIn(List<String> values) {
            addCriterion("set2_yellow_card_score not in", values, "set2YellowCardScore");
            return (Criteria) this;
        }

        public Criteria andSet2YellowCardScoreBetween(String value1, String value2) {
            addCriterion("set2_yellow_card_score between", value1, value2, "set2YellowCardScore");
            return (Criteria) this;
        }

        public Criteria andSet2YellowCardScoreNotBetween(String value1, String value2) {
            addCriterion("set2_yellow_card_score not between", value1, value2, "set2YellowCardScore");
            return (Criteria) this;
        }

        public Criteria andSet2RedCardScoreIsNull() {
            addCriterion("set2_red_card_score is null");
            return (Criteria) this;
        }

        public Criteria andSet2RedCardScoreIsNotNull() {
            addCriterion("set2_red_card_score is not null");
            return (Criteria) this;
        }

        public Criteria andSet2RedCardScoreEqualTo(String value) {
            addCriterion("set2_red_card_score =", value, "set2RedCardScore");
            return (Criteria) this;
        }

        public Criteria andSet2RedCardScoreNotEqualTo(String value) {
            addCriterion("set2_red_card_score <>", value, "set2RedCardScore");
            return (Criteria) this;
        }

        public Criteria andSet2RedCardScoreGreaterThan(String value) {
            addCriterion("set2_red_card_score >", value, "set2RedCardScore");
            return (Criteria) this;
        }

        public Criteria andSet2RedCardScoreGreaterThanOrEqualTo(String value) {
            addCriterion("set2_red_card_score >=", value, "set2RedCardScore");
            return (Criteria) this;
        }

        public Criteria andSet2RedCardScoreLessThan(String value) {
            addCriterion("set2_red_card_score <", value, "set2RedCardScore");
            return (Criteria) this;
        }

        public Criteria andSet2RedCardScoreLessThanOrEqualTo(String value) {
            addCriterion("set2_red_card_score <=", value, "set2RedCardScore");
            return (Criteria) this;
        }

        public Criteria andSet2RedCardScoreLike(String value) {
            addCriterion("set2_red_card_score like", value, "set2RedCardScore");
            return (Criteria) this;
        }

        public Criteria andSet2RedCardScoreNotLike(String value) {
            addCriterion("set2_red_card_score not like", value, "set2RedCardScore");
            return (Criteria) this;
        }

        public Criteria andSet2RedCardScoreIn(List<String> values) {
            addCriterion("set2_red_card_score in", values, "set2RedCardScore");
            return (Criteria) this;
        }

        public Criteria andSet2RedCardScoreNotIn(List<String> values) {
            addCriterion("set2_red_card_score not in", values, "set2RedCardScore");
            return (Criteria) this;
        }

        public Criteria andSet2RedCardScoreBetween(String value1, String value2) {
            addCriterion("set2_red_card_score between", value1, value2, "set2RedCardScore");
            return (Criteria) this;
        }

        public Criteria andSet2RedCardScoreNotBetween(String value1, String value2) {
            addCriterion("set2_red_card_score not between", value1, value2, "set2RedCardScore");
            return (Criteria) this;
        }

        public Criteria andSet2CornerScoreIsNull() {
            addCriterion("set2_corner_score is null");
            return (Criteria) this;
        }

        public Criteria andSet2CornerScoreIsNotNull() {
            addCriterion("set2_corner_score is not null");
            return (Criteria) this;
        }

        public Criteria andSet2CornerScoreEqualTo(String value) {
            addCriterion("set2_corner_score =", value, "set2CornerScore");
            return (Criteria) this;
        }

        public Criteria andSet2CornerScoreNotEqualTo(String value) {
            addCriterion("set2_corner_score <>", value, "set2CornerScore");
            return (Criteria) this;
        }

        public Criteria andSet2CornerScoreGreaterThan(String value) {
            addCriterion("set2_corner_score >", value, "set2CornerScore");
            return (Criteria) this;
        }

        public Criteria andSet2CornerScoreGreaterThanOrEqualTo(String value) {
            addCriterion("set2_corner_score >=", value, "set2CornerScore");
            return (Criteria) this;
        }

        public Criteria andSet2CornerScoreLessThan(String value) {
            addCriterion("set2_corner_score <", value, "set2CornerScore");
            return (Criteria) this;
        }

        public Criteria andSet2CornerScoreLessThanOrEqualTo(String value) {
            addCriterion("set2_corner_score <=", value, "set2CornerScore");
            return (Criteria) this;
        }

        public Criteria andSet2CornerScoreLike(String value) {
            addCriterion("set2_corner_score like", value, "set2CornerScore");
            return (Criteria) this;
        }

        public Criteria andSet2CornerScoreNotLike(String value) {
            addCriterion("set2_corner_score not like", value, "set2CornerScore");
            return (Criteria) this;
        }

        public Criteria andSet2CornerScoreIn(List<String> values) {
            addCriterion("set2_corner_score in", values, "set2CornerScore");
            return (Criteria) this;
        }

        public Criteria andSet2CornerScoreNotIn(List<String> values) {
            addCriterion("set2_corner_score not in", values, "set2CornerScore");
            return (Criteria) this;
        }

        public Criteria andSet2CornerScoreBetween(String value1, String value2) {
            addCriterion("set2_corner_score between", value1, value2, "set2CornerScore");
            return (Criteria) this;
        }

        public Criteria andSet2CornerScoreNotBetween(String value1, String value2) {
            addCriterion("set2_corner_score not between", value1, value2, "set2CornerScore");
            return (Criteria) this;
        }

        public Criteria andRemarkIsNull() {
            addCriterion("remark is null");
            return (Criteria) this;
        }

        public Criteria andRemarkIsNotNull() {
            addCriterion("remark is not null");
            return (Criteria) this;
        }

        public Criteria andRemarkEqualTo(String value) {
            addCriterion("remark =", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotEqualTo(String value) {
            addCriterion("remark <>", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkGreaterThan(String value) {
            addCriterion("remark >", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkGreaterThanOrEqualTo(String value) {
            addCriterion("remark >=", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLessThan(String value) {
            addCriterion("remark <", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLessThanOrEqualTo(String value) {
            addCriterion("remark <=", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLike(String value) {
            addCriterion("remark like", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotLike(String value) {
            addCriterion("remark not like", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkIn(List<String> values) {
            addCriterion("remark in", values, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotIn(List<String> values) {
            addCriterion("remark not in", values, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkBetween(String value1, String value2) {
            addCriterion("remark between", value1, value2, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotBetween(String value1, String value2) {
            addCriterion("remark not between", value1, value2, "remark");
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