package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class MatchTimeInfoExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MatchTimeInfoExample() {
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

        public Criteria andDataSourceTypeIsNull() {
            addCriterion("data_source_type is null");
            return (Criteria) this;
        }

        public Criteria andDataSourceTypeIsNotNull() {
            addCriterion("data_source_type is not null");
            return (Criteria) this;
        }

        public Criteria andDataSourceTypeEqualTo(String value) {
            addCriterion("data_source_type =", value, "dataSourceType");
            return (Criteria) this;
        }

        public Criteria andDataSourceTypeNotEqualTo(String value) {
            addCriterion("data_source_type <>", value, "dataSourceType");
            return (Criteria) this;
        }

        public Criteria andDataSourceTypeGreaterThan(String value) {
            addCriterion("data_source_type >", value, "dataSourceType");
            return (Criteria) this;
        }

        public Criteria andDataSourceTypeGreaterThanOrEqualTo(String value) {
            addCriterion("data_source_type >=", value, "dataSourceType");
            return (Criteria) this;
        }

        public Criteria andDataSourceTypeLessThan(String value) {
            addCriterion("data_source_type <", value, "dataSourceType");
            return (Criteria) this;
        }

        public Criteria andDataSourceTypeLessThanOrEqualTo(String value) {
            addCriterion("data_source_type <=", value, "dataSourceType");
            return (Criteria) this;
        }

        public Criteria andDataSourceTypeLike(String value) {
            addCriterion("data_source_type like", value, "dataSourceType");
            return (Criteria) this;
        }

        public Criteria andDataSourceTypeNotLike(String value) {
            addCriterion("data_source_type not like", value, "dataSourceType");
            return (Criteria) this;
        }

        public Criteria andDataSourceTypeIn(List<String> values) {
            addCriterion("data_source_type in", values, "dataSourceType");
            return (Criteria) this;
        }

        public Criteria andDataSourceTypeNotIn(List<String> values) {
            addCriterion("data_source_type not in", values, "dataSourceType");
            return (Criteria) this;
        }

        public Criteria andDataSourceTypeBetween(String value1, String value2) {
            addCriterion("data_source_type between", value1, value2, "dataSourceType");
            return (Criteria) this;
        }

        public Criteria andDataSourceTypeNotBetween(String value1, String value2) {
            addCriterion("data_source_type not between", value1, value2, "dataSourceType");
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

        public Criteria andPeriodEqualTo(Long value) {
            addCriterion("period =", value, "period");
            return (Criteria) this;
        }

        public Criteria andPeriodNotEqualTo(Long value) {
            addCriterion("period <>", value, "period");
            return (Criteria) this;
        }

        public Criteria andPeriodGreaterThan(Long value) {
            addCriterion("period >", value, "period");
            return (Criteria) this;
        }

        public Criteria andPeriodGreaterThanOrEqualTo(Long value) {
            addCriterion("period >=", value, "period");
            return (Criteria) this;
        }

        public Criteria andPeriodLessThan(Long value) {
            addCriterion("period <", value, "period");
            return (Criteria) this;
        }

        public Criteria andPeriodLessThanOrEqualTo(Long value) {
            addCriterion("period <=", value, "period");
            return (Criteria) this;
        }

        public Criteria andPeriodIn(List<Long> values) {
            addCriterion("period in", values, "period");
            return (Criteria) this;
        }

        public Criteria andPeriodNotIn(List<Long> values) {
            addCriterion("period not in", values, "period");
            return (Criteria) this;
        }

        public Criteria andPeriodBetween(Long value1, Long value2) {
            addCriterion("period between", value1, value2, "period");
            return (Criteria) this;
        }

        public Criteria andPeriodNotBetween(Long value1, Long value2) {
            addCriterion("period not between", value1, value2, "period");
            return (Criteria) this;
        }

        public Criteria andTimeGoIsNull() {
            addCriterion("time_go is null");
            return (Criteria) this;
        }

        public Criteria andTimeGoIsNotNull() {
            addCriterion("time_go is not null");
            return (Criteria) this;
        }

        public Criteria andTimeGoEqualTo(Integer value) {
            addCriterion("time_go =", value, "timeGo");
            return (Criteria) this;
        }

        public Criteria andTimeGoNotEqualTo(Integer value) {
            addCriterion("time_go <>", value, "timeGo");
            return (Criteria) this;
        }

        public Criteria andTimeGoGreaterThan(Integer value) {
            addCriterion("time_go >", value, "timeGo");
            return (Criteria) this;
        }

        public Criteria andTimeGoGreaterThanOrEqualTo(Integer value) {
            addCriterion("time_go >=", value, "timeGo");
            return (Criteria) this;
        }

        public Criteria andTimeGoLessThan(Integer value) {
            addCriterion("time_go <", value, "timeGo");
            return (Criteria) this;
        }

        public Criteria andTimeGoLessThanOrEqualTo(Integer value) {
            addCriterion("time_go <=", value, "timeGo");
            return (Criteria) this;
        }

        public Criteria andTimeGoIn(List<Integer> values) {
            addCriterion("time_go in", values, "timeGo");
            return (Criteria) this;
        }

        public Criteria andTimeGoNotIn(List<Integer> values) {
            addCriterion("time_go not in", values, "timeGo");
            return (Criteria) this;
        }

        public Criteria andTimeGoBetween(Integer value1, Integer value2) {
            addCriterion("time_go between", value1, value2, "timeGo");
            return (Criteria) this;
        }

        public Criteria andTimeGoNotBetween(Integer value1, Integer value2) {
            addCriterion("time_go not between", value1, value2, "timeGo");
            return (Criteria) this;
        }

        public Criteria andSecondFromStartIsNull() {
            addCriterion("second_from_start is null");
            return (Criteria) this;
        }

        public Criteria andSecondFromStartIsNotNull() {
            addCriterion("second_from_start is not null");
            return (Criteria) this;
        }

        public Criteria andSecondFromStartEqualTo(Long value) {
            addCriterion("second_from_start =", value, "secondFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondFromStartNotEqualTo(Long value) {
            addCriterion("second_from_start <>", value, "secondFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondFromStartGreaterThan(Long value) {
            addCriterion("second_from_start >", value, "secondFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondFromStartGreaterThanOrEqualTo(Long value) {
            addCriterion("second_from_start >=", value, "secondFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondFromStartLessThan(Long value) {
            addCriterion("second_from_start <", value, "secondFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondFromStartLessThanOrEqualTo(Long value) {
            addCriterion("second_from_start <=", value, "secondFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondFromStartIn(List<Long> values) {
            addCriterion("second_from_start in", values, "secondFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondFromStartNotIn(List<Long> values) {
            addCriterion("second_from_start not in", values, "secondFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondFromStartBetween(Long value1, Long value2) {
            addCriterion("second_from_start between", value1, value2, "secondFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondFromStartNotBetween(Long value1, Long value2) {
            addCriterion("second_from_start not between", value1, value2, "secondFromStart");
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

        public Criteria andRemainingTimeIsNull() {
            addCriterion("remaining_time is null");
            return (Criteria) this;
        }

        public Criteria andRemainingTimeIsNotNull() {
            addCriterion("remaining_time is not null");
            return (Criteria) this;
        }

        public Criteria andRemainingTimeEqualTo(Long value) {
            addCriterion("remaining_time =", value, "remainingTime");
            return (Criteria) this;
        }

        public Criteria andRemainingTimeNotEqualTo(Long value) {
            addCriterion("remaining_time <>", value, "remainingTime");
            return (Criteria) this;
        }

        public Criteria andRemainingTimeGreaterThan(Long value) {
            addCriterion("remaining_time >", value, "remainingTime");
            return (Criteria) this;
        }

        public Criteria andRemainingTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("remaining_time >=", value, "remainingTime");
            return (Criteria) this;
        }

        public Criteria andRemainingTimeLessThan(Long value) {
            addCriterion("remaining_time <", value, "remainingTime");
            return (Criteria) this;
        }

        public Criteria andRemainingTimeLessThanOrEqualTo(Long value) {
            addCriterion("remaining_time <=", value, "remainingTime");
            return (Criteria) this;
        }

        public Criteria andRemainingTimeIn(List<Long> values) {
            addCriterion("remaining_time in", values, "remainingTime");
            return (Criteria) this;
        }

        public Criteria andRemainingTimeNotIn(List<Long> values) {
            addCriterion("remaining_time not in", values, "remainingTime");
            return (Criteria) this;
        }

        public Criteria andRemainingTimeBetween(Long value1, Long value2) {
            addCriterion("remaining_time between", value1, value2, "remainingTime");
            return (Criteria) this;
        }

        public Criteria andRemainingTimeNotBetween(Long value1, Long value2) {
            addCriterion("remaining_time not between", value1, value2, "remainingTime");
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

        public Criteria andFirstNumIsNull() {
            addCriterion("first_num is null");
            return (Criteria) this;
        }

        public Criteria andFirstNumIsNotNull() {
            addCriterion("first_num is not null");
            return (Criteria) this;
        }

        public Criteria andFirstNumEqualTo(Integer value) {
            addCriterion("first_num =", value, "firstNum");
            return (Criteria) this;
        }

        public Criteria andFirstNumNotEqualTo(Integer value) {
            addCriterion("first_num <>", value, "firstNum");
            return (Criteria) this;
        }

        public Criteria andFirstNumGreaterThan(Integer value) {
            addCriterion("first_num >", value, "firstNum");
            return (Criteria) this;
        }

        public Criteria andFirstNumGreaterThanOrEqualTo(Integer value) {
            addCriterion("first_num >=", value, "firstNum");
            return (Criteria) this;
        }

        public Criteria andFirstNumLessThan(Integer value) {
            addCriterion("first_num <", value, "firstNum");
            return (Criteria) this;
        }

        public Criteria andFirstNumLessThanOrEqualTo(Integer value) {
            addCriterion("first_num <=", value, "firstNum");
            return (Criteria) this;
        }

        public Criteria andFirstNumIn(List<Integer> values) {
            addCriterion("first_num in", values, "firstNum");
            return (Criteria) this;
        }

        public Criteria andFirstNumNotIn(List<Integer> values) {
            addCriterion("first_num not in", values, "firstNum");
            return (Criteria) this;
        }

        public Criteria andFirstNumBetween(Integer value1, Integer value2) {
            addCriterion("first_num between", value1, value2, "firstNum");
            return (Criteria) this;
        }

        public Criteria andFirstNumNotBetween(Integer value1, Integer value2) {
            addCriterion("first_num not between", value1, value2, "firstNum");
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

        public Criteria andPeriodLengthEqualTo(Long value) {
            addCriterion("period_length =", value, "periodLength");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthNotEqualTo(Long value) {
            addCriterion("period_length <>", value, "periodLength");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthGreaterThan(Long value) {
            addCriterion("period_length >", value, "periodLength");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthGreaterThanOrEqualTo(Long value) {
            addCriterion("period_length >=", value, "periodLength");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthLessThan(Long value) {
            addCriterion("period_length <", value, "periodLength");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthLessThanOrEqualTo(Long value) {
            addCriterion("period_length <=", value, "periodLength");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthIn(List<Long> values) {
            addCriterion("period_length in", values, "periodLength");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthNotIn(List<Long> values) {
            addCriterion("period_length not in", values, "periodLength");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthBetween(Long value1, Long value2) {
            addCriterion("period_length between", value1, value2, "periodLength");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthNotBetween(Long value1, Long value2) {
            addCriterion("period_length not between", value1, value2, "periodLength");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthJsonIsNull() {
            addCriterion("period_length_json is null");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthJsonIsNotNull() {
            addCriterion("period_length_json is not null");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthJsonEqualTo(String value) {
            addCriterion("period_length_json =", value, "periodLengthJson");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthJsonNotEqualTo(String value) {
            addCriterion("period_length_json <>", value, "periodLengthJson");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthJsonGreaterThan(String value) {
            addCriterion("period_length_json >", value, "periodLengthJson");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthJsonGreaterThanOrEqualTo(String value) {
            addCriterion("period_length_json >=", value, "periodLengthJson");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthJsonLessThan(String value) {
            addCriterion("period_length_json <", value, "periodLengthJson");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthJsonLessThanOrEqualTo(String value) {
            addCriterion("period_length_json <=", value, "periodLengthJson");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthJsonLike(String value) {
            addCriterion("period_length_json like", value, "periodLengthJson");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthJsonNotLike(String value) {
            addCriterion("period_length_json not like", value, "periodLengthJson");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthJsonIn(List<String> values) {
            addCriterion("period_length_json in", values, "periodLengthJson");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthJsonNotIn(List<String> values) {
            addCriterion("period_length_json not in", values, "periodLengthJson");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthJsonBetween(String value1, String value2) {
            addCriterion("period_length_json between", value1, value2, "periodLengthJson");
            return (Criteria) this;
        }

        public Criteria andPeriodLengthJsonNotBetween(String value1, String value2) {
            addCriterion("period_length_json not between", value1, value2, "periodLengthJson");
            return (Criteria) this;
        }

        public Criteria andMatchLengthJsonIsNull() {
            addCriterion("match_length_json is null");
            return (Criteria) this;
        }

        public Criteria andMatchLengthJsonIsNotNull() {
            addCriterion("match_length_json is not null");
            return (Criteria) this;
        }

        public Criteria andMatchLengthJsonEqualTo(String value) {
            addCriterion("match_length_json =", value, "matchLengthJson");
            return (Criteria) this;
        }

        public Criteria andMatchLengthJsonNotEqualTo(String value) {
            addCriterion("match_length_json <>", value, "matchLengthJson");
            return (Criteria) this;
        }

        public Criteria andMatchLengthJsonGreaterThan(String value) {
            addCriterion("match_length_json >", value, "matchLengthJson");
            return (Criteria) this;
        }

        public Criteria andMatchLengthJsonGreaterThanOrEqualTo(String value) {
            addCriterion("match_length_json >=", value, "matchLengthJson");
            return (Criteria) this;
        }

        public Criteria andMatchLengthJsonLessThan(String value) {
            addCriterion("match_length_json <", value, "matchLengthJson");
            return (Criteria) this;
        }

        public Criteria andMatchLengthJsonLessThanOrEqualTo(String value) {
            addCriterion("match_length_json <=", value, "matchLengthJson");
            return (Criteria) this;
        }

        public Criteria andMatchLengthJsonLike(String value) {
            addCriterion("match_length_json like", value, "matchLengthJson");
            return (Criteria) this;
        }

        public Criteria andMatchLengthJsonNotLike(String value) {
            addCriterion("match_length_json not like", value, "matchLengthJson");
            return (Criteria) this;
        }

        public Criteria andMatchLengthJsonIn(List<String> values) {
            addCriterion("match_length_json in", values, "matchLengthJson");
            return (Criteria) this;
        }

        public Criteria andMatchLengthJsonNotIn(List<String> values) {
            addCriterion("match_length_json not in", values, "matchLengthJson");
            return (Criteria) this;
        }

        public Criteria andMatchLengthJsonBetween(String value1, String value2) {
            addCriterion("match_length_json between", value1, value2, "matchLengthJson");
            return (Criteria) this;
        }

        public Criteria andMatchLengthJsonNotBetween(String value1, String value2) {
            addCriterion("match_length_json not between", value1, value2, "matchLengthJson");
            return (Criteria) this;
        }

        public Criteria andRoundTypeIsNull() {
            addCriterion("round_type is null");
            return (Criteria) this;
        }

        public Criteria andRoundTypeIsNotNull() {
            addCriterion("round_type is not null");
            return (Criteria) this;
        }

        public Criteria andRoundTypeEqualTo(Integer value) {
            addCriterion("round_type =", value, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeNotEqualTo(Integer value) {
            addCriterion("round_type <>", value, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeGreaterThan(Integer value) {
            addCriterion("round_type >", value, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("round_type >=", value, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeLessThan(Integer value) {
            addCriterion("round_type <", value, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeLessThanOrEqualTo(Integer value) {
            addCriterion("round_type <=", value, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeIn(List<Integer> values) {
            addCriterion("round_type in", values, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeNotIn(List<Integer> values) {
            addCriterion("round_type not in", values, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeBetween(Integer value1, Integer value2) {
            addCriterion("round_type between", value1, value2, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("round_type not between", value1, value2, "roundType");
            return (Criteria) this;
        }

        public Criteria andCurrentSetIsNull() {
            addCriterion("current_set is null");
            return (Criteria) this;
        }

        public Criteria andCurrentSetIsNotNull() {
            addCriterion("current_set is not null");
            return (Criteria) this;
        }

        public Criteria andCurrentSetEqualTo(Integer value) {
            addCriterion("current_set =", value, "currentSet");
            return (Criteria) this;
        }

        public Criteria andCurrentSetNotEqualTo(Integer value) {
            addCriterion("current_set <>", value, "currentSet");
            return (Criteria) this;
        }

        public Criteria andCurrentSetGreaterThan(Integer value) {
            addCriterion("current_set >", value, "currentSet");
            return (Criteria) this;
        }

        public Criteria andCurrentSetGreaterThanOrEqualTo(Integer value) {
            addCriterion("current_set >=", value, "currentSet");
            return (Criteria) this;
        }

        public Criteria andCurrentSetLessThan(Integer value) {
            addCriterion("current_set <", value, "currentSet");
            return (Criteria) this;
        }

        public Criteria andCurrentSetLessThanOrEqualTo(Integer value) {
            addCriterion("current_set <=", value, "currentSet");
            return (Criteria) this;
        }

        public Criteria andCurrentSetIn(List<Integer> values) {
            addCriterion("current_set in", values, "currentSet");
            return (Criteria) this;
        }

        public Criteria andCurrentSetNotIn(List<Integer> values) {
            addCriterion("current_set not in", values, "currentSet");
            return (Criteria) this;
        }

        public Criteria andCurrentSetBetween(Integer value1, Integer value2) {
            addCriterion("current_set between", value1, value2, "currentSet");
            return (Criteria) this;
        }

        public Criteria andCurrentSetNotBetween(Integer value1, Integer value2) {
            addCriterion("current_set not between", value1, value2, "currentSet");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundIsNull() {
            addCriterion("current_round is null");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundIsNotNull() {
            addCriterion("current_round is not null");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundEqualTo(Integer value) {
            addCriterion("current_round =", value, "currentRound");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNotEqualTo(Integer value) {
            addCriterion("current_round <>", value, "currentRound");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundGreaterThan(Integer value) {
            addCriterion("current_round >", value, "currentRound");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundGreaterThanOrEqualTo(Integer value) {
            addCriterion("current_round >=", value, "currentRound");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundLessThan(Integer value) {
            addCriterion("current_round <", value, "currentRound");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundLessThanOrEqualTo(Integer value) {
            addCriterion("current_round <=", value, "currentRound");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundIn(List<Integer> values) {
            addCriterion("current_round in", values, "currentRound");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNotIn(List<Integer> values) {
            addCriterion("current_round not in", values, "currentRound");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundBetween(Integer value1, Integer value2) {
            addCriterion("current_round between", value1, value2, "currentRound");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNotBetween(Integer value1, Integer value2) {
            addCriterion("current_round not between", value1, value2, "currentRound");
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