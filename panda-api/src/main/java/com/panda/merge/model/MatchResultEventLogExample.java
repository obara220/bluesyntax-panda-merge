package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class MatchResultEventLogExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MatchResultEventLogExample() {
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

        public Criteria andEventCodeIsNull() {
            addCriterion("event_code is null");
            return (Criteria) this;
        }

        public Criteria andEventCodeIsNotNull() {
            addCriterion("event_code is not null");
            return (Criteria) this;
        }

        public Criteria andEventCodeEqualTo(String value) {
            addCriterion("event_code =", value, "eventCode");
            return (Criteria) this;
        }

        public Criteria andEventCodeNotEqualTo(String value) {
            addCriterion("event_code <>", value, "eventCode");
            return (Criteria) this;
        }

        public Criteria andEventCodeGreaterThan(String value) {
            addCriterion("event_code >", value, "eventCode");
            return (Criteria) this;
        }

        public Criteria andEventCodeGreaterThanOrEqualTo(String value) {
            addCriterion("event_code >=", value, "eventCode");
            return (Criteria) this;
        }

        public Criteria andEventCodeLessThan(String value) {
            addCriterion("event_code <", value, "eventCode");
            return (Criteria) this;
        }

        public Criteria andEventCodeLessThanOrEqualTo(String value) {
            addCriterion("event_code <=", value, "eventCode");
            return (Criteria) this;
        }

        public Criteria andEventCodeLike(String value) {
            addCriterion("event_code like", value, "eventCode");
            return (Criteria) this;
        }

        public Criteria andEventCodeNotLike(String value) {
            addCriterion("event_code not like", value, "eventCode");
            return (Criteria) this;
        }

        public Criteria andEventCodeIn(List<String> values) {
            addCriterion("event_code in", values, "eventCode");
            return (Criteria) this;
        }

        public Criteria andEventCodeNotIn(List<String> values) {
            addCriterion("event_code not in", values, "eventCode");
            return (Criteria) this;
        }

        public Criteria andEventCodeBetween(String value1, String value2) {
            addCriterion("event_code between", value1, value2, "eventCode");
            return (Criteria) this;
        }

        public Criteria andEventCodeNotBetween(String value1, String value2) {
            addCriterion("event_code not between", value1, value2, "eventCode");
            return (Criteria) this;
        }

        public Criteria andEventNameIsNull() {
            addCriterion("event_name is null");
            return (Criteria) this;
        }

        public Criteria andEventNameIsNotNull() {
            addCriterion("event_name is not null");
            return (Criteria) this;
        }

        public Criteria andEventNameEqualTo(String value) {
            addCriterion("event_name =", value, "eventName");
            return (Criteria) this;
        }

        public Criteria andEventNameNotEqualTo(String value) {
            addCriterion("event_name <>", value, "eventName");
            return (Criteria) this;
        }

        public Criteria andEventNameGreaterThan(String value) {
            addCriterion("event_name >", value, "eventName");
            return (Criteria) this;
        }

        public Criteria andEventNameGreaterThanOrEqualTo(String value) {
            addCriterion("event_name >=", value, "eventName");
            return (Criteria) this;
        }

        public Criteria andEventNameLessThan(String value) {
            addCriterion("event_name <", value, "eventName");
            return (Criteria) this;
        }

        public Criteria andEventNameLessThanOrEqualTo(String value) {
            addCriterion("event_name <=", value, "eventName");
            return (Criteria) this;
        }

        public Criteria andEventNameLike(String value) {
            addCriterion("event_name like", value, "eventName");
            return (Criteria) this;
        }

        public Criteria andEventNameNotLike(String value) {
            addCriterion("event_name not like", value, "eventName");
            return (Criteria) this;
        }

        public Criteria andEventNameIn(List<String> values) {
            addCriterion("event_name in", values, "eventName");
            return (Criteria) this;
        }

        public Criteria andEventNameNotIn(List<String> values) {
            addCriterion("event_name not in", values, "eventName");
            return (Criteria) this;
        }

        public Criteria andEventNameBetween(String value1, String value2) {
            addCriterion("event_name between", value1, value2, "eventName");
            return (Criteria) this;
        }

        public Criteria andEventNameNotBetween(String value1, String value2) {
            addCriterion("event_name not between", value1, value2, "eventName");
            return (Criteria) this;
        }

        public Criteria andExtraInfoIsNull() {
            addCriterion("extra_info is null");
            return (Criteria) this;
        }

        public Criteria andExtraInfoIsNotNull() {
            addCriterion("extra_info is not null");
            return (Criteria) this;
        }

        public Criteria andExtraInfoEqualTo(String value) {
            addCriterion("extra_info =", value, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoNotEqualTo(String value) {
            addCriterion("extra_info <>", value, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoGreaterThan(String value) {
            addCriterion("extra_info >", value, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoGreaterThanOrEqualTo(String value) {
            addCriterion("extra_info >=", value, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoLessThan(String value) {
            addCriterion("extra_info <", value, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoLessThanOrEqualTo(String value) {
            addCriterion("extra_info <=", value, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoLike(String value) {
            addCriterion("extra_info like", value, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoNotLike(String value) {
            addCriterion("extra_info not like", value, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoIn(List<String> values) {
            addCriterion("extra_info in", values, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoNotIn(List<String> values) {
            addCriterion("extra_info not in", values, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoBetween(String value1, String value2) {
            addCriterion("extra_info between", value1, value2, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoNotBetween(String value1, String value2) {
            addCriterion("extra_info not between", value1, value2, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeIsNull() {
            addCriterion("confirm_time is null");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeIsNotNull() {
            addCriterion("confirm_time is not null");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeEqualTo(Integer value) {
            addCriterion("confirm_time =", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeNotEqualTo(Integer value) {
            addCriterion("confirm_time <>", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeGreaterThan(Integer value) {
            addCriterion("confirm_time >", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeGreaterThanOrEqualTo(Integer value) {
            addCriterion("confirm_time >=", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeLessThan(Integer value) {
            addCriterion("confirm_time <", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeLessThanOrEqualTo(Integer value) {
            addCriterion("confirm_time <=", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeIn(List<Integer> values) {
            addCriterion("confirm_time in", values, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeNotIn(List<Integer> values) {
            addCriterion("confirm_time not in", values, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeBetween(Integer value1, Integer value2) {
            addCriterion("confirm_time between", value1, value2, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeNotBetween(Integer value1, Integer value2) {
            addCriterion("confirm_time not between", value1, value2, "confirmTime");
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

        public Criteria andMatchPeriodIdIsNull() {
            addCriterion("match_period_id is null");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdIsNotNull() {
            addCriterion("match_period_id is not null");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdEqualTo(Long value) {
            addCriterion("match_period_id =", value, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdNotEqualTo(Long value) {
            addCriterion("match_period_id <>", value, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdGreaterThan(Long value) {
            addCriterion("match_period_id >", value, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdGreaterThanOrEqualTo(Long value) {
            addCriterion("match_period_id >=", value, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdLessThan(Long value) {
            addCriterion("match_period_id <", value, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdLessThanOrEqualTo(Long value) {
            addCriterion("match_period_id <=", value, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdIn(List<Long> values) {
            addCriterion("match_period_id in", values, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdNotIn(List<Long> values) {
            addCriterion("match_period_id not in", values, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdBetween(Long value1, Long value2) {
            addCriterion("match_period_id between", value1, value2, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdNotBetween(Long value1, Long value2) {
            addCriterion("match_period_id not between", value1, value2, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchScoreIsNull() {
            addCriterion("match_score is null");
            return (Criteria) this;
        }

        public Criteria andMatchScoreIsNotNull() {
            addCriterion("match_score is not null");
            return (Criteria) this;
        }

        public Criteria andMatchScoreEqualTo(String value) {
            addCriterion("match_score =", value, "matchScore");
            return (Criteria) this;
        }

        public Criteria andMatchScoreNotEqualTo(String value) {
            addCriterion("match_score <>", value, "matchScore");
            return (Criteria) this;
        }

        public Criteria andMatchScoreGreaterThan(String value) {
            addCriterion("match_score >", value, "matchScore");
            return (Criteria) this;
        }

        public Criteria andMatchScoreGreaterThanOrEqualTo(String value) {
            addCriterion("match_score >=", value, "matchScore");
            return (Criteria) this;
        }

        public Criteria andMatchScoreLessThan(String value) {
            addCriterion("match_score <", value, "matchScore");
            return (Criteria) this;
        }

        public Criteria andMatchScoreLessThanOrEqualTo(String value) {
            addCriterion("match_score <=", value, "matchScore");
            return (Criteria) this;
        }

        public Criteria andMatchScoreLike(String value) {
            addCriterion("match_score like", value, "matchScore");
            return (Criteria) this;
        }

        public Criteria andMatchScoreNotLike(String value) {
            addCriterion("match_score not like", value, "matchScore");
            return (Criteria) this;
        }

        public Criteria andMatchScoreIn(List<String> values) {
            addCriterion("match_score in", values, "matchScore");
            return (Criteria) this;
        }

        public Criteria andMatchScoreNotIn(List<String> values) {
            addCriterion("match_score not in", values, "matchScore");
            return (Criteria) this;
        }

        public Criteria andMatchScoreBetween(String value1, String value2) {
            addCriterion("match_score between", value1, value2, "matchScore");
            return (Criteria) this;
        }

        public Criteria andMatchScoreNotBetween(String value1, String value2) {
            addCriterion("match_score not between", value1, value2, "matchScore");
            return (Criteria) this;
        }

        public Criteria andReferenceIdIsNull() {
            addCriterion("reference_id is null");
            return (Criteria) this;
        }

        public Criteria andReferenceIdIsNotNull() {
            addCriterion("reference_id is not null");
            return (Criteria) this;
        }

        public Criteria andReferenceIdEqualTo(Long value) {
            addCriterion("reference_id =", value, "referenceId");
            return (Criteria) this;
        }

        public Criteria andReferenceIdNotEqualTo(Long value) {
            addCriterion("reference_id <>", value, "referenceId");
            return (Criteria) this;
        }

        public Criteria andReferenceIdGreaterThan(Long value) {
            addCriterion("reference_id >", value, "referenceId");
            return (Criteria) this;
        }

        public Criteria andReferenceIdGreaterThanOrEqualTo(Long value) {
            addCriterion("reference_id >=", value, "referenceId");
            return (Criteria) this;
        }

        public Criteria andReferenceIdLessThan(Long value) {
            addCriterion("reference_id <", value, "referenceId");
            return (Criteria) this;
        }

        public Criteria andReferenceIdLessThanOrEqualTo(Long value) {
            addCriterion("reference_id <=", value, "referenceId");
            return (Criteria) this;
        }

        public Criteria andReferenceIdIn(List<Long> values) {
            addCriterion("reference_id in", values, "referenceId");
            return (Criteria) this;
        }

        public Criteria andReferenceIdNotIn(List<Long> values) {
            addCriterion("reference_id not in", values, "referenceId");
            return (Criteria) this;
        }

        public Criteria andReferenceIdBetween(Long value1, Long value2) {
            addCriterion("reference_id between", value1, value2, "referenceId");
            return (Criteria) this;
        }

        public Criteria andReferenceIdNotBetween(Long value1, Long value2) {
            addCriterion("reference_id not between", value1, value2, "referenceId");
            return (Criteria) this;
        }

        public Criteria andPlayer1IdIsNull() {
            addCriterion("player1_id is null");
            return (Criteria) this;
        }

        public Criteria andPlayer1IdIsNotNull() {
            addCriterion("player1_id is not null");
            return (Criteria) this;
        }

        public Criteria andPlayer1IdEqualTo(Long value) {
            addCriterion("player1_id =", value, "player1Id");
            return (Criteria) this;
        }

        public Criteria andPlayer1IdNotEqualTo(Long value) {
            addCriterion("player1_id <>", value, "player1Id");
            return (Criteria) this;
        }

        public Criteria andPlayer1IdGreaterThan(Long value) {
            addCriterion("player1_id >", value, "player1Id");
            return (Criteria) this;
        }

        public Criteria andPlayer1IdGreaterThanOrEqualTo(Long value) {
            addCriterion("player1_id >=", value, "player1Id");
            return (Criteria) this;
        }

        public Criteria andPlayer1IdLessThan(Long value) {
            addCriterion("player1_id <", value, "player1Id");
            return (Criteria) this;
        }

        public Criteria andPlayer1IdLessThanOrEqualTo(Long value) {
            addCriterion("player1_id <=", value, "player1Id");
            return (Criteria) this;
        }

        public Criteria andPlayer1IdIn(List<Long> values) {
            addCriterion("player1_id in", values, "player1Id");
            return (Criteria) this;
        }

        public Criteria andPlayer1IdNotIn(List<Long> values) {
            addCriterion("player1_id not in", values, "player1Id");
            return (Criteria) this;
        }

        public Criteria andPlayer1IdBetween(Long value1, Long value2) {
            addCriterion("player1_id between", value1, value2, "player1Id");
            return (Criteria) this;
        }

        public Criteria andPlayer1IdNotBetween(Long value1, Long value2) {
            addCriterion("player1_id not between", value1, value2, "player1Id");
            return (Criteria) this;
        }

        public Criteria andPlayer1NameIsNull() {
            addCriterion("player1_name is null");
            return (Criteria) this;
        }

        public Criteria andPlayer1NameIsNotNull() {
            addCriterion("player1_name is not null");
            return (Criteria) this;
        }

        public Criteria andPlayer1NameEqualTo(String value) {
            addCriterion("player1_name =", value, "player1Name");
            return (Criteria) this;
        }

        public Criteria andPlayer1NameNotEqualTo(String value) {
            addCriterion("player1_name <>", value, "player1Name");
            return (Criteria) this;
        }

        public Criteria andPlayer1NameGreaterThan(String value) {
            addCriterion("player1_name >", value, "player1Name");
            return (Criteria) this;
        }

        public Criteria andPlayer1NameGreaterThanOrEqualTo(String value) {
            addCriterion("player1_name >=", value, "player1Name");
            return (Criteria) this;
        }

        public Criteria andPlayer1NameLessThan(String value) {
            addCriterion("player1_name <", value, "player1Name");
            return (Criteria) this;
        }

        public Criteria andPlayer1NameLessThanOrEqualTo(String value) {
            addCriterion("player1_name <=", value, "player1Name");
            return (Criteria) this;
        }

        public Criteria andPlayer1NameLike(String value) {
            addCriterion("player1_name like", value, "player1Name");
            return (Criteria) this;
        }

        public Criteria andPlayer1NameNotLike(String value) {
            addCriterion("player1_name not like", value, "player1Name");
            return (Criteria) this;
        }

        public Criteria andPlayer1NameIn(List<String> values) {
            addCriterion("player1_name in", values, "player1Name");
            return (Criteria) this;
        }

        public Criteria andPlayer1NameNotIn(List<String> values) {
            addCriterion("player1_name not in", values, "player1Name");
            return (Criteria) this;
        }

        public Criteria andPlayer1NameBetween(String value1, String value2) {
            addCriterion("player1_name between", value1, value2, "player1Name");
            return (Criteria) this;
        }

        public Criteria andPlayer1NameNotBetween(String value1, String value2) {
            addCriterion("player1_name not between", value1, value2, "player1Name");
            return (Criteria) this;
        }

        public Criteria andPlayer2IdIsNull() {
            addCriterion("player2_id is null");
            return (Criteria) this;
        }

        public Criteria andPlayer2IdIsNotNull() {
            addCriterion("player2_id is not null");
            return (Criteria) this;
        }

        public Criteria andPlayer2IdEqualTo(Long value) {
            addCriterion("player2_id =", value, "player2Id");
            return (Criteria) this;
        }

        public Criteria andPlayer2IdNotEqualTo(Long value) {
            addCriterion("player2_id <>", value, "player2Id");
            return (Criteria) this;
        }

        public Criteria andPlayer2IdGreaterThan(Long value) {
            addCriterion("player2_id >", value, "player2Id");
            return (Criteria) this;
        }

        public Criteria andPlayer2IdGreaterThanOrEqualTo(Long value) {
            addCriterion("player2_id >=", value, "player2Id");
            return (Criteria) this;
        }

        public Criteria andPlayer2IdLessThan(Long value) {
            addCriterion("player2_id <", value, "player2Id");
            return (Criteria) this;
        }

        public Criteria andPlayer2IdLessThanOrEqualTo(Long value) {
            addCriterion("player2_id <=", value, "player2Id");
            return (Criteria) this;
        }

        public Criteria andPlayer2IdIn(List<Long> values) {
            addCriterion("player2_id in", values, "player2Id");
            return (Criteria) this;
        }

        public Criteria andPlayer2IdNotIn(List<Long> values) {
            addCriterion("player2_id not in", values, "player2Id");
            return (Criteria) this;
        }

        public Criteria andPlayer2IdBetween(Long value1, Long value2) {
            addCriterion("player2_id between", value1, value2, "player2Id");
            return (Criteria) this;
        }

        public Criteria andPlayer2IdNotBetween(Long value1, Long value2) {
            addCriterion("player2_id not between", value1, value2, "player2Id");
            return (Criteria) this;
        }

        public Criteria andPlayer2NameIsNull() {
            addCriterion("player2_name is null");
            return (Criteria) this;
        }

        public Criteria andPlayer2NameIsNotNull() {
            addCriterion("player2_name is not null");
            return (Criteria) this;
        }

        public Criteria andPlayer2NameEqualTo(String value) {
            addCriterion("player2_name =", value, "player2Name");
            return (Criteria) this;
        }

        public Criteria andPlayer2NameNotEqualTo(String value) {
            addCriterion("player2_name <>", value, "player2Name");
            return (Criteria) this;
        }

        public Criteria andPlayer2NameGreaterThan(String value) {
            addCriterion("player2_name >", value, "player2Name");
            return (Criteria) this;
        }

        public Criteria andPlayer2NameGreaterThanOrEqualTo(String value) {
            addCriterion("player2_name >=", value, "player2Name");
            return (Criteria) this;
        }

        public Criteria andPlayer2NameLessThan(String value) {
            addCriterion("player2_name <", value, "player2Name");
            return (Criteria) this;
        }

        public Criteria andPlayer2NameLessThanOrEqualTo(String value) {
            addCriterion("player2_name <=", value, "player2Name");
            return (Criteria) this;
        }

        public Criteria andPlayer2NameLike(String value) {
            addCriterion("player2_name like", value, "player2Name");
            return (Criteria) this;
        }

        public Criteria andPlayer2NameNotLike(String value) {
            addCriterion("player2_name not like", value, "player2Name");
            return (Criteria) this;
        }

        public Criteria andPlayer2NameIn(List<String> values) {
            addCriterion("player2_name in", values, "player2Name");
            return (Criteria) this;
        }

        public Criteria andPlayer2NameNotIn(List<String> values) {
            addCriterion("player2_name not in", values, "player2Name");
            return (Criteria) this;
        }

        public Criteria andPlayer2NameBetween(String value1, String value2) {
            addCriterion("player2_name between", value1, value2, "player2Name");
            return (Criteria) this;
        }

        public Criteria andPlayer2NameNotBetween(String value1, String value2) {
            addCriterion("player2_name not between", value1, value2, "player2Name");
            return (Criteria) this;
        }

        public Criteria andSecondsFromStartIsNull() {
            addCriterion("seconds_from_start is null");
            return (Criteria) this;
        }

        public Criteria andSecondsFromStartIsNotNull() {
            addCriterion("seconds_from_start is not null");
            return (Criteria) this;
        }

        public Criteria andSecondsFromStartEqualTo(Integer value) {
            addCriterion("seconds_from_start =", value, "secondsFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondsFromStartNotEqualTo(Integer value) {
            addCriterion("seconds_from_start <>", value, "secondsFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondsFromStartGreaterThan(Integer value) {
            addCriterion("seconds_from_start >", value, "secondsFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondsFromStartGreaterThanOrEqualTo(Integer value) {
            addCriterion("seconds_from_start >=", value, "secondsFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondsFromStartLessThan(Integer value) {
            addCriterion("seconds_from_start <", value, "secondsFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondsFromStartLessThanOrEqualTo(Integer value) {
            addCriterion("seconds_from_start <=", value, "secondsFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondsFromStartIn(List<Integer> values) {
            addCriterion("seconds_from_start in", values, "secondsFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondsFromStartNotIn(List<Integer> values) {
            addCriterion("seconds_from_start not in", values, "secondsFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondsFromStartBetween(Integer value1, Integer value2) {
            addCriterion("seconds_from_start between", value1, value2, "secondsFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondsFromStartNotBetween(Integer value1, Integer value2) {
            addCriterion("seconds_from_start not between", value1, value2, "secondsFromStart");
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

        public Criteria andStandardTeamIdIsNull() {
            addCriterion("standard_team_id is null");
            return (Criteria) this;
        }

        public Criteria andStandardTeamIdIsNotNull() {
            addCriterion("standard_team_id is not null");
            return (Criteria) this;
        }

        public Criteria andStandardTeamIdEqualTo(Long value) {
            addCriterion("standard_team_id =", value, "standardTeamId");
            return (Criteria) this;
        }

        public Criteria andStandardTeamIdNotEqualTo(Long value) {
            addCriterion("standard_team_id <>", value, "standardTeamId");
            return (Criteria) this;
        }

        public Criteria andStandardTeamIdGreaterThan(Long value) {
            addCriterion("standard_team_id >", value, "standardTeamId");
            return (Criteria) this;
        }

        public Criteria andStandardTeamIdGreaterThanOrEqualTo(Long value) {
            addCriterion("standard_team_id >=", value, "standardTeamId");
            return (Criteria) this;
        }

        public Criteria andStandardTeamIdLessThan(Long value) {
            addCriterion("standard_team_id <", value, "standardTeamId");
            return (Criteria) this;
        }

        public Criteria andStandardTeamIdLessThanOrEqualTo(Long value) {
            addCriterion("standard_team_id <=", value, "standardTeamId");
            return (Criteria) this;
        }

        public Criteria andStandardTeamIdIn(List<Long> values) {
            addCriterion("standard_team_id in", values, "standardTeamId");
            return (Criteria) this;
        }

        public Criteria andStandardTeamIdNotIn(List<Long> values) {
            addCriterion("standard_team_id not in", values, "standardTeamId");
            return (Criteria) this;
        }

        public Criteria andStandardTeamIdBetween(Long value1, Long value2) {
            addCriterion("standard_team_id between", value1, value2, "standardTeamId");
            return (Criteria) this;
        }

        public Criteria andStandardTeamIdNotBetween(Long value1, Long value2) {
            addCriterion("standard_team_id not between", value1, value2, "standardTeamId");
            return (Criteria) this;
        }

        public Criteria andEventHomeScoreIsNull() {
            addCriterion("event_home_score is null");
            return (Criteria) this;
        }

        public Criteria andEventHomeScoreIsNotNull() {
            addCriterion("event_home_score is not null");
            return (Criteria) this;
        }

        public Criteria andEventHomeScoreEqualTo(Integer value) {
            addCriterion("event_home_score =", value, "eventHomeScore");
            return (Criteria) this;
        }

        public Criteria andEventHomeScoreNotEqualTo(Integer value) {
            addCriterion("event_home_score <>", value, "eventHomeScore");
            return (Criteria) this;
        }

        public Criteria andEventHomeScoreGreaterThan(Integer value) {
            addCriterion("event_home_score >", value, "eventHomeScore");
            return (Criteria) this;
        }

        public Criteria andEventHomeScoreGreaterThanOrEqualTo(Integer value) {
            addCriterion("event_home_score >=", value, "eventHomeScore");
            return (Criteria) this;
        }

        public Criteria andEventHomeScoreLessThan(Integer value) {
            addCriterion("event_home_score <", value, "eventHomeScore");
            return (Criteria) this;
        }

        public Criteria andEventHomeScoreLessThanOrEqualTo(Integer value) {
            addCriterion("event_home_score <=", value, "eventHomeScore");
            return (Criteria) this;
        }

        public Criteria andEventHomeScoreIn(List<Integer> values) {
            addCriterion("event_home_score in", values, "eventHomeScore");
            return (Criteria) this;
        }

        public Criteria andEventHomeScoreNotIn(List<Integer> values) {
            addCriterion("event_home_score not in", values, "eventHomeScore");
            return (Criteria) this;
        }

        public Criteria andEventHomeScoreBetween(Integer value1, Integer value2) {
            addCriterion("event_home_score between", value1, value2, "eventHomeScore");
            return (Criteria) this;
        }

        public Criteria andEventHomeScoreNotBetween(Integer value1, Integer value2) {
            addCriterion("event_home_score not between", value1, value2, "eventHomeScore");
            return (Criteria) this;
        }

        public Criteria andEventAwayScoreIsNull() {
            addCriterion("event_away_score is null");
            return (Criteria) this;
        }

        public Criteria andEventAwayScoreIsNotNull() {
            addCriterion("event_away_score is not null");
            return (Criteria) this;
        }

        public Criteria andEventAwayScoreEqualTo(Integer value) {
            addCriterion("event_away_score =", value, "eventAwayScore");
            return (Criteria) this;
        }

        public Criteria andEventAwayScoreNotEqualTo(Integer value) {
            addCriterion("event_away_score <>", value, "eventAwayScore");
            return (Criteria) this;
        }

        public Criteria andEventAwayScoreGreaterThan(Integer value) {
            addCriterion("event_away_score >", value, "eventAwayScore");
            return (Criteria) this;
        }

        public Criteria andEventAwayScoreGreaterThanOrEqualTo(Integer value) {
            addCriterion("event_away_score >=", value, "eventAwayScore");
            return (Criteria) this;
        }

        public Criteria andEventAwayScoreLessThan(Integer value) {
            addCriterion("event_away_score <", value, "eventAwayScore");
            return (Criteria) this;
        }

        public Criteria andEventAwayScoreLessThanOrEqualTo(Integer value) {
            addCriterion("event_away_score <=", value, "eventAwayScore");
            return (Criteria) this;
        }

        public Criteria andEventAwayScoreIn(List<Integer> values) {
            addCriterion("event_away_score in", values, "eventAwayScore");
            return (Criteria) this;
        }

        public Criteria andEventAwayScoreNotIn(List<Integer> values) {
            addCriterion("event_away_score not in", values, "eventAwayScore");
            return (Criteria) this;
        }

        public Criteria andEventAwayScoreBetween(Integer value1, Integer value2) {
            addCriterion("event_away_score between", value1, value2, "eventAwayScore");
            return (Criteria) this;
        }

        public Criteria andEventAwayScoreNotBetween(Integer value1, Integer value2) {
            addCriterion("event_away_score not between", value1, value2, "eventAwayScore");
            return (Criteria) this;
        }

        public Criteria andThirdEventIdIsNull() {
            addCriterion("third_event_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdEventIdIsNotNull() {
            addCriterion("third_event_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdEventIdEqualTo(String value) {
            addCriterion("third_event_id =", value, "thirdEventId");
            return (Criteria) this;
        }

        public Criteria andThirdEventIdNotEqualTo(String value) {
            addCriterion("third_event_id <>", value, "thirdEventId");
            return (Criteria) this;
        }

        public Criteria andThirdEventIdGreaterThan(String value) {
            addCriterion("third_event_id >", value, "thirdEventId");
            return (Criteria) this;
        }

        public Criteria andThirdEventIdGreaterThanOrEqualTo(String value) {
            addCriterion("third_event_id >=", value, "thirdEventId");
            return (Criteria) this;
        }

        public Criteria andThirdEventIdLessThan(String value) {
            addCriterion("third_event_id <", value, "thirdEventId");
            return (Criteria) this;
        }

        public Criteria andThirdEventIdLessThanOrEqualTo(String value) {
            addCriterion("third_event_id <=", value, "thirdEventId");
            return (Criteria) this;
        }

        public Criteria andThirdEventIdLike(String value) {
            addCriterion("third_event_id like", value, "thirdEventId");
            return (Criteria) this;
        }

        public Criteria andThirdEventIdNotLike(String value) {
            addCriterion("third_event_id not like", value, "thirdEventId");
            return (Criteria) this;
        }

        public Criteria andThirdEventIdIn(List<String> values) {
            addCriterion("third_event_id in", values, "thirdEventId");
            return (Criteria) this;
        }

        public Criteria andThirdEventIdNotIn(List<String> values) {
            addCriterion("third_event_id not in", values, "thirdEventId");
            return (Criteria) this;
        }

        public Criteria andThirdEventIdBetween(String value1, String value2) {
            addCriterion("third_event_id between", value1, value2, "thirdEventId");
            return (Criteria) this;
        }

        public Criteria andThirdEventIdNotBetween(String value1, String value2) {
            addCriterion("third_event_id not between", value1, value2, "thirdEventId");
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

        public Criteria andThirdTeamIdIsNull() {
            addCriterion("third_team_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdTeamIdIsNotNull() {
            addCriterion("third_team_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdTeamIdEqualTo(Long value) {
            addCriterion("third_team_id =", value, "thirdTeamId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamIdNotEqualTo(Long value) {
            addCriterion("third_team_id <>", value, "thirdTeamId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamIdGreaterThan(Long value) {
            addCriterion("third_team_id >", value, "thirdTeamId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamIdGreaterThanOrEqualTo(Long value) {
            addCriterion("third_team_id >=", value, "thirdTeamId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamIdLessThan(Long value) {
            addCriterion("third_team_id <", value, "thirdTeamId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamIdLessThanOrEqualTo(Long value) {
            addCriterion("third_team_id <=", value, "thirdTeamId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamIdIn(List<Long> values) {
            addCriterion("third_team_id in", values, "thirdTeamId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamIdNotIn(List<Long> values) {
            addCriterion("third_team_id not in", values, "thirdTeamId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamIdBetween(Long value1, Long value2) {
            addCriterion("third_team_id between", value1, value2, "thirdTeamId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamIdNotBetween(Long value1, Long value2) {
            addCriterion("third_team_id not between", value1, value2, "thirdTeamId");
            return (Criteria) this;
        }

        public Criteria andStatusIsNull() {
            addCriterion("status is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("status is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(Integer value) {
            addCriterion("status =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(Integer value) {
            addCriterion("status <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(Integer value) {
            addCriterion("status >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("status >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(Integer value) {
            addCriterion("status <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(Integer value) {
            addCriterion("status <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<Integer> values) {
            addCriterion("status in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<Integer> values) {
            addCriterion("status not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(Integer value1, Integer value2) {
            addCriterion("status between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("status not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andOperateTypeIsNull() {
            addCriterion("operate_type is null");
            return (Criteria) this;
        }

        public Criteria andOperateTypeIsNotNull() {
            addCriterion("operate_type is not null");
            return (Criteria) this;
        }

        public Criteria andOperateTypeEqualTo(Integer value) {
            addCriterion("operate_type =", value, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeNotEqualTo(Integer value) {
            addCriterion("operate_type <>", value, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeGreaterThan(Integer value) {
            addCriterion("operate_type >", value, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("operate_type >=", value, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeLessThan(Integer value) {
            addCriterion("operate_type <", value, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeLessThanOrEqualTo(Integer value) {
            addCriterion("operate_type <=", value, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeIn(List<Integer> values) {
            addCriterion("operate_type in", values, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeNotIn(List<Integer> values) {
            addCriterion("operate_type not in", values, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeBetween(Integer value1, Integer value2) {
            addCriterion("operate_type between", value1, value2, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("operate_type not between", value1, value2, "operateType");
            return (Criteria) this;
        }

        public Criteria andConfirmTimesIsNull() {
            addCriterion("confirm_times is null");
            return (Criteria) this;
        }

        public Criteria andConfirmTimesIsNotNull() {
            addCriterion("confirm_times is not null");
            return (Criteria) this;
        }

        public Criteria andConfirmTimesEqualTo(Integer value) {
            addCriterion("confirm_times =", value, "confirmTimes");
            return (Criteria) this;
        }

        public Criteria andConfirmTimesNotEqualTo(Integer value) {
            addCriterion("confirm_times <>", value, "confirmTimes");
            return (Criteria) this;
        }

        public Criteria andConfirmTimesGreaterThan(Integer value) {
            addCriterion("confirm_times >", value, "confirmTimes");
            return (Criteria) this;
        }

        public Criteria andConfirmTimesGreaterThanOrEqualTo(Integer value) {
            addCriterion("confirm_times >=", value, "confirmTimes");
            return (Criteria) this;
        }

        public Criteria andConfirmTimesLessThan(Integer value) {
            addCriterion("confirm_times <", value, "confirmTimes");
            return (Criteria) this;
        }

        public Criteria andConfirmTimesLessThanOrEqualTo(Integer value) {
            addCriterion("confirm_times <=", value, "confirmTimes");
            return (Criteria) this;
        }

        public Criteria andConfirmTimesIn(List<Integer> values) {
            addCriterion("confirm_times in", values, "confirmTimes");
            return (Criteria) this;
        }

        public Criteria andConfirmTimesNotIn(List<Integer> values) {
            addCriterion("confirm_times not in", values, "confirmTimes");
            return (Criteria) this;
        }

        public Criteria andConfirmTimesBetween(Integer value1, Integer value2) {
            addCriterion("confirm_times between", value1, value2, "confirmTimes");
            return (Criteria) this;
        }

        public Criteria andConfirmTimesNotBetween(Integer value1, Integer value2) {
            addCriterion("confirm_times not between", value1, value2, "confirmTimes");
            return (Criteria) this;
        }

        public Criteria andSuspendTimeIsNull() {
            addCriterion("suspend_time is null");
            return (Criteria) this;
        }

        public Criteria andSuspendTimeIsNotNull() {
            addCriterion("suspend_time is not null");
            return (Criteria) this;
        }

        public Criteria andSuspendTimeEqualTo(Integer value) {
            addCriterion("suspend_time =", value, "suspendTime");
            return (Criteria) this;
        }

        public Criteria andSuspendTimeNotEqualTo(Integer value) {
            addCriterion("suspend_time <>", value, "suspendTime");
            return (Criteria) this;
        }

        public Criteria andSuspendTimeGreaterThan(Integer value) {
            addCriterion("suspend_time >", value, "suspendTime");
            return (Criteria) this;
        }

        public Criteria andSuspendTimeGreaterThanOrEqualTo(Integer value) {
            addCriterion("suspend_time >=", value, "suspendTime");
            return (Criteria) this;
        }

        public Criteria andSuspendTimeLessThan(Integer value) {
            addCriterion("suspend_time <", value, "suspendTime");
            return (Criteria) this;
        }

        public Criteria andSuspendTimeLessThanOrEqualTo(Integer value) {
            addCriterion("suspend_time <=", value, "suspendTime");
            return (Criteria) this;
        }

        public Criteria andSuspendTimeIn(List<Integer> values) {
            addCriterion("suspend_time in", values, "suspendTime");
            return (Criteria) this;
        }

        public Criteria andSuspendTimeNotIn(List<Integer> values) {
            addCriterion("suspend_time not in", values, "suspendTime");
            return (Criteria) this;
        }

        public Criteria andSuspendTimeBetween(Integer value1, Integer value2) {
            addCriterion("suspend_time between", value1, value2, "suspendTime");
            return (Criteria) this;
        }

        public Criteria andSuspendTimeNotBetween(Integer value1, Integer value2) {
            addCriterion("suspend_time not between", value1, value2, "suspendTime");
            return (Criteria) this;
        }

        public Criteria andAlertTypeIsNull() {
            addCriterion("alert_type is null");
            return (Criteria) this;
        }

        public Criteria andAlertTypeIsNotNull() {
            addCriterion("alert_type is not null");
            return (Criteria) this;
        }

        public Criteria andAlertTypeEqualTo(Integer value) {
            addCriterion("alert_type =", value, "alertType");
            return (Criteria) this;
        }

        public Criteria andAlertTypeNotEqualTo(Integer value) {
            addCriterion("alert_type <>", value, "alertType");
            return (Criteria) this;
        }

        public Criteria andAlertTypeGreaterThan(Integer value) {
            addCriterion("alert_type >", value, "alertType");
            return (Criteria) this;
        }

        public Criteria andAlertTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("alert_type >=", value, "alertType");
            return (Criteria) this;
        }

        public Criteria andAlertTypeLessThan(Integer value) {
            addCriterion("alert_type <", value, "alertType");
            return (Criteria) this;
        }

        public Criteria andAlertTypeLessThanOrEqualTo(Integer value) {
            addCriterion("alert_type <=", value, "alertType");
            return (Criteria) this;
        }

        public Criteria andAlertTypeIn(List<Integer> values) {
            addCriterion("alert_type in", values, "alertType");
            return (Criteria) this;
        }

        public Criteria andAlertTypeNotIn(List<Integer> values) {
            addCriterion("alert_type not in", values, "alertType");
            return (Criteria) this;
        }

        public Criteria andAlertTypeBetween(Integer value1, Integer value2) {
            addCriterion("alert_type between", value1, value2, "alertType");
            return (Criteria) this;
        }

        public Criteria andAlertTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("alert_type not between", value1, value2, "alertType");
            return (Criteria) this;
        }

        public Criteria andOperateIdIsNull() {
            addCriterion("operate_id is null");
            return (Criteria) this;
        }

        public Criteria andOperateIdIsNotNull() {
            addCriterion("operate_id is not null");
            return (Criteria) this;
        }

        public Criteria andOperateIdEqualTo(String value) {
            addCriterion("operate_id =", value, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdNotEqualTo(String value) {
            addCriterion("operate_id <>", value, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdGreaterThan(String value) {
            addCriterion("operate_id >", value, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdGreaterThanOrEqualTo(String value) {
            addCriterion("operate_id >=", value, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdLessThan(String value) {
            addCriterion("operate_id <", value, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdLessThanOrEqualTo(String value) {
            addCriterion("operate_id <=", value, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdLike(String value) {
            addCriterion("operate_id like", value, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdNotLike(String value) {
            addCriterion("operate_id not like", value, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdIn(List<String> values) {
            addCriterion("operate_id in", values, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdNotIn(List<String> values) {
            addCriterion("operate_id not in", values, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdBetween(String value1, String value2) {
            addCriterion("operate_id between", value1, value2, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdNotBetween(String value1, String value2) {
            addCriterion("operate_id not between", value1, value2, "operateId");
            return (Criteria) this;
        }

        public Criteria andSourceTypeIsNull() {
            addCriterion("source_type is null");
            return (Criteria) this;
        }

        public Criteria andSourceTypeIsNotNull() {
            addCriterion("source_type is not null");
            return (Criteria) this;
        }

        public Criteria andSourceTypeEqualTo(Integer value) {
            addCriterion("source_type =", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeNotEqualTo(Integer value) {
            addCriterion("source_type <>", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeGreaterThan(Integer value) {
            addCriterion("source_type >", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("source_type >=", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeLessThan(Integer value) {
            addCriterion("source_type <", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeLessThanOrEqualTo(Integer value) {
            addCriterion("source_type <=", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeIn(List<Integer> values) {
            addCriterion("source_type in", values, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeNotIn(List<Integer> values) {
            addCriterion("source_type not in", values, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeBetween(Integer value1, Integer value2) {
            addCriterion("source_type between", value1, value2, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("source_type not between", value1, value2, "sourceType");
            return (Criteria) this;
        }

        public Criteria andCommercialDatasourcesIsNull() {
            addCriterion("commercial_datasources is null");
            return (Criteria) this;
        }

        public Criteria andCommercialDatasourcesIsNotNull() {
            addCriterion("commercial_datasources is not null");
            return (Criteria) this;
        }

        public Criteria andCommercialDatasourcesEqualTo(String value) {
            addCriterion("commercial_datasources =", value, "commercialDatasources");
            return (Criteria) this;
        }

        public Criteria andCommercialDatasourcesNotEqualTo(String value) {
            addCriterion("commercial_datasources <>", value, "commercialDatasources");
            return (Criteria) this;
        }

        public Criteria andCommercialDatasourcesGreaterThan(String value) {
            addCriterion("commercial_datasources >", value, "commercialDatasources");
            return (Criteria) this;
        }

        public Criteria andCommercialDatasourcesGreaterThanOrEqualTo(String value) {
            addCriterion("commercial_datasources >=", value, "commercialDatasources");
            return (Criteria) this;
        }

        public Criteria andCommercialDatasourcesLessThan(String value) {
            addCriterion("commercial_datasources <", value, "commercialDatasources");
            return (Criteria) this;
        }

        public Criteria andCommercialDatasourcesLessThanOrEqualTo(String value) {
            addCriterion("commercial_datasources <=", value, "commercialDatasources");
            return (Criteria) this;
        }

        public Criteria andCommercialDatasourcesLike(String value) {
            addCriterion("commercial_datasources like", value, "commercialDatasources");
            return (Criteria) this;
        }

        public Criteria andCommercialDatasourcesNotLike(String value) {
            addCriterion("commercial_datasources not like", value, "commercialDatasources");
            return (Criteria) this;
        }

        public Criteria andCommercialDatasourcesIn(List<String> values) {
            addCriterion("commercial_datasources in", values, "commercialDatasources");
            return (Criteria) this;
        }

        public Criteria andCommercialDatasourcesNotIn(List<String> values) {
            addCriterion("commercial_datasources not in", values, "commercialDatasources");
            return (Criteria) this;
        }

        public Criteria andCommercialDatasourcesBetween(String value1, String value2) {
            addCriterion("commercial_datasources between", value1, value2, "commercialDatasources");
            return (Criteria) this;
        }

        public Criteria andCommercialDatasourcesNotBetween(String value1, String value2) {
            addCriterion("commercial_datasources not between", value1, value2, "commercialDatasources");
            return (Criteria) this;
        }

        public Criteria andCompetitionDatasourcesIsNull() {
            addCriterion("competition_datasources is null");
            return (Criteria) this;
        }

        public Criteria andCompetitionDatasourcesIsNotNull() {
            addCriterion("competition_datasources is not null");
            return (Criteria) this;
        }

        public Criteria andCompetitionDatasourcesEqualTo(String value) {
            addCriterion("competition_datasources =", value, "competitionDatasources");
            return (Criteria) this;
        }

        public Criteria andCompetitionDatasourcesNotEqualTo(String value) {
            addCriterion("competition_datasources <>", value, "competitionDatasources");
            return (Criteria) this;
        }

        public Criteria andCompetitionDatasourcesGreaterThan(String value) {
            addCriterion("competition_datasources >", value, "competitionDatasources");
            return (Criteria) this;
        }

        public Criteria andCompetitionDatasourcesGreaterThanOrEqualTo(String value) {
            addCriterion("competition_datasources >=", value, "competitionDatasources");
            return (Criteria) this;
        }

        public Criteria andCompetitionDatasourcesLessThan(String value) {
            addCriterion("competition_datasources <", value, "competitionDatasources");
            return (Criteria) this;
        }

        public Criteria andCompetitionDatasourcesLessThanOrEqualTo(String value) {
            addCriterion("competition_datasources <=", value, "competitionDatasources");
            return (Criteria) this;
        }

        public Criteria andCompetitionDatasourcesLike(String value) {
            addCriterion("competition_datasources like", value, "competitionDatasources");
            return (Criteria) this;
        }

        public Criteria andCompetitionDatasourcesNotLike(String value) {
            addCriterion("competition_datasources not like", value, "competitionDatasources");
            return (Criteria) this;
        }

        public Criteria andCompetitionDatasourcesIn(List<String> values) {
            addCriterion("competition_datasources in", values, "competitionDatasources");
            return (Criteria) this;
        }

        public Criteria andCompetitionDatasourcesNotIn(List<String> values) {
            addCriterion("competition_datasources not in", values, "competitionDatasources");
            return (Criteria) this;
        }

        public Criteria andCompetitionDatasourcesBetween(String value1, String value2) {
            addCriterion("competition_datasources between", value1, value2, "competitionDatasources");
            return (Criteria) this;
        }

        public Criteria andCompetitionDatasourcesNotBetween(String value1, String value2) {
            addCriterion("competition_datasources not between", value1, value2, "competitionDatasources");
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