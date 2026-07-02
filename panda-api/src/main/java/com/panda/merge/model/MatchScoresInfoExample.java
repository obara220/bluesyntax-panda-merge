package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class MatchScoresInfoExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MatchScoresInfoExample() {
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

        public Criteria andThirdMatchSourceIdIsNull() {
            addCriterion("third_match_source_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdIsNotNull() {
            addCriterion("third_match_source_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdEqualTo(String value) {
            addCriterion("third_match_source_id =", value, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdNotEqualTo(String value) {
            addCriterion("third_match_source_id <>", value, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdGreaterThan(String value) {
            addCriterion("third_match_source_id >", value, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdGreaterThanOrEqualTo(String value) {
            addCriterion("third_match_source_id >=", value, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdLessThan(String value) {
            addCriterion("third_match_source_id <", value, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdLessThanOrEqualTo(String value) {
            addCriterion("third_match_source_id <=", value, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdLike(String value) {
            addCriterion("third_match_source_id like", value, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdNotLike(String value) {
            addCriterion("third_match_source_id not like", value, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdIn(List<String> values) {
            addCriterion("third_match_source_id in", values, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdNotIn(List<String> values) {
            addCriterion("third_match_source_id not in", values, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdBetween(String value1, String value2) {
            addCriterion("third_match_source_id between", value1, value2, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdNotBetween(String value1, String value2) {
            addCriterion("third_match_source_id not between", value1, value2, "thirdMatchSourceId");
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

        public Criteria andSecondsMatchStartIsNull() {
            addCriterion("seconds_match_start is null");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartIsNotNull() {
            addCriterion("seconds_match_start is not null");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartEqualTo(Long value) {
            addCriterion("seconds_match_start =", value, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartNotEqualTo(Long value) {
            addCriterion("seconds_match_start <>", value, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartGreaterThan(Long value) {
            addCriterion("seconds_match_start >", value, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartGreaterThanOrEqualTo(Long value) {
            addCriterion("seconds_match_start >=", value, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartLessThan(Long value) {
            addCriterion("seconds_match_start <", value, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartLessThanOrEqualTo(Long value) {
            addCriterion("seconds_match_start <=", value, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartIn(List<Long> values) {
            addCriterion("seconds_match_start in", values, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartNotIn(List<Long> values) {
            addCriterion("seconds_match_start not in", values, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartBetween(Long value1, Long value2) {
            addCriterion("seconds_match_start between", value1, value2, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartNotBetween(Long value1, Long value2) {
            addCriterion("seconds_match_start not between", value1, value2, "secondsMatchStart");
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

        public Criteria andScoresJsonIsNull() {
            addCriterion("scores_json is null");
            return (Criteria) this;
        }

        public Criteria andScoresJsonIsNotNull() {
            addCriterion("scores_json is not null");
            return (Criteria) this;
        }

        public Criteria andScoresJsonEqualTo(String value) {
            addCriterion("scores_json =", value, "scoresJson");
            return (Criteria) this;
        }

        public Criteria andScoresJsonNotEqualTo(String value) {
            addCriterion("scores_json <>", value, "scoresJson");
            return (Criteria) this;
        }

        public Criteria andScoresJsonGreaterThan(String value) {
            addCriterion("scores_json >", value, "scoresJson");
            return (Criteria) this;
        }

        public Criteria andScoresJsonGreaterThanOrEqualTo(String value) {
            addCriterion("scores_json >=", value, "scoresJson");
            return (Criteria) this;
        }

        public Criteria andScoresJsonLessThan(String value) {
            addCriterion("scores_json <", value, "scoresJson");
            return (Criteria) this;
        }

        public Criteria andScoresJsonLessThanOrEqualTo(String value) {
            addCriterion("scores_json <=", value, "scoresJson");
            return (Criteria) this;
        }

        public Criteria andScoresJsonLike(String value) {
            addCriterion("scores_json like", value, "scoresJson");
            return (Criteria) this;
        }

        public Criteria andScoresJsonNotLike(String value) {
            addCriterion("scores_json not like", value, "scoresJson");
            return (Criteria) this;
        }

        public Criteria andScoresJsonIn(List<String> values) {
            addCriterion("scores_json in", values, "scoresJson");
            return (Criteria) this;
        }

        public Criteria andScoresJsonNotIn(List<String> values) {
            addCriterion("scores_json not in", values, "scoresJson");
            return (Criteria) this;
        }

        public Criteria andScoresJsonBetween(String value1, String value2) {
            addCriterion("scores_json between", value1, value2, "scoresJson");
            return (Criteria) this;
        }

        public Criteria andScoresJsonNotBetween(String value1, String value2) {
            addCriterion("scores_json not between", value1, value2, "scoresJson");
            return (Criteria) this;
        }

        public Criteria andScoresJsonTypeIsNull() {
            addCriterion("scores_json_type is null");
            return (Criteria) this;
        }

        public Criteria andScoresJsonTypeIsNotNull() {
            addCriterion("scores_json_type is not null");
            return (Criteria) this;
        }

        public Criteria andScoresJsonTypeEqualTo(String value) {
            addCriterion("scores_json_type =", value, "scoresJsonType");
            return (Criteria) this;
        }

        public Criteria andScoresJsonTypeNotEqualTo(String value) {
            addCriterion("scores_json_type <>", value, "scoresJsonType");
            return (Criteria) this;
        }

        public Criteria andScoresJsonTypeGreaterThan(String value) {
            addCriterion("scores_json_type >", value, "scoresJsonType");
            return (Criteria) this;
        }

        public Criteria andScoresJsonTypeGreaterThanOrEqualTo(String value) {
            addCriterion("scores_json_type >=", value, "scoresJsonType");
            return (Criteria) this;
        }

        public Criteria andScoresJsonTypeLessThan(String value) {
            addCriterion("scores_json_type <", value, "scoresJsonType");
            return (Criteria) this;
        }

        public Criteria andScoresJsonTypeLessThanOrEqualTo(String value) {
            addCriterion("scores_json_type <=", value, "scoresJsonType");
            return (Criteria) this;
        }

        public Criteria andScoresJsonTypeLike(String value) {
            addCriterion("scores_json_type like", value, "scoresJsonType");
            return (Criteria) this;
        }

        public Criteria andScoresJsonTypeNotLike(String value) {
            addCriterion("scores_json_type not like", value, "scoresJsonType");
            return (Criteria) this;
        }

        public Criteria andScoresJsonTypeIn(List<String> values) {
            addCriterion("scores_json_type in", values, "scoresJsonType");
            return (Criteria) this;
        }

        public Criteria andScoresJsonTypeNotIn(List<String> values) {
            addCriterion("scores_json_type not in", values, "scoresJsonType");
            return (Criteria) this;
        }

        public Criteria andScoresJsonTypeBetween(String value1, String value2) {
            addCriterion("scores_json_type between", value1, value2, "scoresJsonType");
            return (Criteria) this;
        }

        public Criteria andScoresJsonTypeNotBetween(String value1, String value2) {
            addCriterion("scores_json_type not between", value1, value2, "scoresJsonType");
            return (Criteria) this;
        }

        public Criteria andT1IsNull() {
            addCriterion("t1 is null");
            return (Criteria) this;
        }

        public Criteria andT1IsNotNull() {
            addCriterion("t1 is not null");
            return (Criteria) this;
        }

        public Criteria andT1EqualTo(Integer value) {
            addCriterion("t1 =", value, "t1");
            return (Criteria) this;
        }

        public Criteria andT1NotEqualTo(Integer value) {
            addCriterion("t1 <>", value, "t1");
            return (Criteria) this;
        }

        public Criteria andT1GreaterThan(Integer value) {
            addCriterion("t1 >", value, "t1");
            return (Criteria) this;
        }

        public Criteria andT1GreaterThanOrEqualTo(Integer value) {
            addCriterion("t1 >=", value, "t1");
            return (Criteria) this;
        }

        public Criteria andT1LessThan(Integer value) {
            addCriterion("t1 <", value, "t1");
            return (Criteria) this;
        }

        public Criteria andT1LessThanOrEqualTo(Integer value) {
            addCriterion("t1 <=", value, "t1");
            return (Criteria) this;
        }

        public Criteria andT1In(List<Integer> values) {
            addCriterion("t1 in", values, "t1");
            return (Criteria) this;
        }

        public Criteria andT1NotIn(List<Integer> values) {
            addCriterion("t1 not in", values, "t1");
            return (Criteria) this;
        }

        public Criteria andT1Between(Integer value1, Integer value2) {
            addCriterion("t1 between", value1, value2, "t1");
            return (Criteria) this;
        }

        public Criteria andT1NotBetween(Integer value1, Integer value2) {
            addCriterion("t1 not between", value1, value2, "t1");
            return (Criteria) this;
        }

        public Criteria andT2IsNull() {
            addCriterion("t2 is null");
            return (Criteria) this;
        }

        public Criteria andT2IsNotNull() {
            addCriterion("t2 is not null");
            return (Criteria) this;
        }

        public Criteria andT2EqualTo(Integer value) {
            addCriterion("t2 =", value, "t2");
            return (Criteria) this;
        }

        public Criteria andT2NotEqualTo(Integer value) {
            addCriterion("t2 <>", value, "t2");
            return (Criteria) this;
        }

        public Criteria andT2GreaterThan(Integer value) {
            addCriterion("t2 >", value, "t2");
            return (Criteria) this;
        }

        public Criteria andT2GreaterThanOrEqualTo(Integer value) {
            addCriterion("t2 >=", value, "t2");
            return (Criteria) this;
        }

        public Criteria andT2LessThan(Integer value) {
            addCriterion("t2 <", value, "t2");
            return (Criteria) this;
        }

        public Criteria andT2LessThanOrEqualTo(Integer value) {
            addCriterion("t2 <=", value, "t2");
            return (Criteria) this;
        }

        public Criteria andT2In(List<Integer> values) {
            addCriterion("t2 in", values, "t2");
            return (Criteria) this;
        }

        public Criteria andT2NotIn(List<Integer> values) {
            addCriterion("t2 not in", values, "t2");
            return (Criteria) this;
        }

        public Criteria andT2Between(Integer value1, Integer value2) {
            addCriterion("t2 between", value1, value2, "t2");
            return (Criteria) this;
        }

        public Criteria andT2NotBetween(Integer value1, Integer value2) {
            addCriterion("t2 not between", value1, value2, "t2");
            return (Criteria) this;
        }

        public Criteria andPeriodT1IsNull() {
            addCriterion("period_t1 is null");
            return (Criteria) this;
        }

        public Criteria andPeriodT1IsNotNull() {
            addCriterion("period_t1 is not null");
            return (Criteria) this;
        }

        public Criteria andPeriodT1EqualTo(Integer value) {
            addCriterion("period_t1 =", value, "periodT1");
            return (Criteria) this;
        }

        public Criteria andPeriodT1NotEqualTo(Integer value) {
            addCriterion("period_t1 <>", value, "periodT1");
            return (Criteria) this;
        }

        public Criteria andPeriodT1GreaterThan(Integer value) {
            addCriterion("period_t1 >", value, "periodT1");
            return (Criteria) this;
        }

        public Criteria andPeriodT1GreaterThanOrEqualTo(Integer value) {
            addCriterion("period_t1 >=", value, "periodT1");
            return (Criteria) this;
        }

        public Criteria andPeriodT1LessThan(Integer value) {
            addCriterion("period_t1 <", value, "periodT1");
            return (Criteria) this;
        }

        public Criteria andPeriodT1LessThanOrEqualTo(Integer value) {
            addCriterion("period_t1 <=", value, "periodT1");
            return (Criteria) this;
        }

        public Criteria andPeriodT1In(List<Integer> values) {
            addCriterion("period_t1 in", values, "periodT1");
            return (Criteria) this;
        }

        public Criteria andPeriodT1NotIn(List<Integer> values) {
            addCriterion("period_t1 not in", values, "periodT1");
            return (Criteria) this;
        }

        public Criteria andPeriodT1Between(Integer value1, Integer value2) {
            addCriterion("period_t1 between", value1, value2, "periodT1");
            return (Criteria) this;
        }

        public Criteria andPeriodT1NotBetween(Integer value1, Integer value2) {
            addCriterion("period_t1 not between", value1, value2, "periodT1");
            return (Criteria) this;
        }

        public Criteria andPeriodT2IsNull() {
            addCriterion("period_t2 is null");
            return (Criteria) this;
        }

        public Criteria andPeriodT2IsNotNull() {
            addCriterion("period_t2 is not null");
            return (Criteria) this;
        }

        public Criteria andPeriodT2EqualTo(Integer value) {
            addCriterion("period_t2 =", value, "periodT2");
            return (Criteria) this;
        }

        public Criteria andPeriodT2NotEqualTo(Integer value) {
            addCriterion("period_t2 <>", value, "periodT2");
            return (Criteria) this;
        }

        public Criteria andPeriodT2GreaterThan(Integer value) {
            addCriterion("period_t2 >", value, "periodT2");
            return (Criteria) this;
        }

        public Criteria andPeriodT2GreaterThanOrEqualTo(Integer value) {
            addCriterion("period_t2 >=", value, "periodT2");
            return (Criteria) this;
        }

        public Criteria andPeriodT2LessThan(Integer value) {
            addCriterion("period_t2 <", value, "periodT2");
            return (Criteria) this;
        }

        public Criteria andPeriodT2LessThanOrEqualTo(Integer value) {
            addCriterion("period_t2 <=", value, "periodT2");
            return (Criteria) this;
        }

        public Criteria andPeriodT2In(List<Integer> values) {
            addCriterion("period_t2 in", values, "periodT2");
            return (Criteria) this;
        }

        public Criteria andPeriodT2NotIn(List<Integer> values) {
            addCriterion("period_t2 not in", values, "periodT2");
            return (Criteria) this;
        }

        public Criteria andPeriodT2Between(Integer value1, Integer value2) {
            addCriterion("period_t2 between", value1, value2, "periodT2");
            return (Criteria) this;
        }

        public Criteria andPeriodT2NotBetween(Integer value1, Integer value2) {
            addCriterion("period_t2 not between", value1, value2, "periodT2");
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

        public Criteria andScoresJsonExtraIsNull() {
            addCriterion("scores_json_extra is null");
            return (Criteria) this;
        }

        public Criteria andScoresJsonExtraIsNotNull() {
            addCriterion("scores_json_extra is not null");
            return (Criteria) this;
        }

        public Criteria andScoresJsonExtraEqualTo(String value) {
            addCriterion("scores_json_extra =", value, "scoresJsonExtra");
            return (Criteria) this;
        }

        public Criteria andScoresJsonExtraNotEqualTo(String value) {
            addCriterion("scores_json_extra <>", value, "scoresJsonExtra");
            return (Criteria) this;
        }

        public Criteria andScoresJsonExtraGreaterThan(String value) {
            addCriterion("scores_json_extra >", value, "scoresJsonExtra");
            return (Criteria) this;
        }

        public Criteria andScoresJsonExtraGreaterThanOrEqualTo(String value) {
            addCriterion("scores_json_extra >=", value, "scoresJsonExtra");
            return (Criteria) this;
        }

        public Criteria andScoresJsonExtraLessThan(String value) {
            addCriterion("scores_json_extra <", value, "scoresJsonExtra");
            return (Criteria) this;
        }

        public Criteria andScoresJsonExtraLessThanOrEqualTo(String value) {
            addCriterion("scores_json_extra <=", value, "scoresJsonExtra");
            return (Criteria) this;
        }

        public Criteria andScoresJsonExtraLike(String value) {
            addCriterion("scores_json_extra like", value, "scoresJsonExtra");
            return (Criteria) this;
        }

        public Criteria andScoresJsonExtraNotLike(String value) {
            addCriterion("scores_json_extra not like", value, "scoresJsonExtra");
            return (Criteria) this;
        }

        public Criteria andScoresJsonExtraIn(List<String> values) {
            addCriterion("scores_json_extra in", values, "scoresJsonExtra");
            return (Criteria) this;
        }

        public Criteria andScoresJsonExtraNotIn(List<String> values) {
            addCriterion("scores_json_extra not in", values, "scoresJsonExtra");
            return (Criteria) this;
        }

        public Criteria andScoresJsonExtraBetween(String value1, String value2) {
            addCriterion("scores_json_extra between", value1, value2, "scoresJsonExtra");
            return (Criteria) this;
        }

        public Criteria andScoresJsonExtraNotBetween(String value1, String value2) {
            addCriterion("scores_json_extra not between", value1, value2, "scoresJsonExtra");
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