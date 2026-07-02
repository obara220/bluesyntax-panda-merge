package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class MatchEventInfoExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MatchEventInfoExample() {
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

        public Criteria andCanceledIsNull() {
            addCriterion("canceled is null");
            return (Criteria) this;
        }

        public Criteria andCanceledIsNotNull() {
            addCriterion("canceled is not null");
            return (Criteria) this;
        }

        public Criteria andCanceledEqualTo(Integer value) {
            addCriterion("canceled =", value, "canceled");
            return (Criteria) this;
        }

        public Criteria andCanceledNotEqualTo(Integer value) {
            addCriterion("canceled <>", value, "canceled");
            return (Criteria) this;
        }

        public Criteria andCanceledGreaterThan(Integer value) {
            addCriterion("canceled >", value, "canceled");
            return (Criteria) this;
        }

        public Criteria andCanceledGreaterThanOrEqualTo(Integer value) {
            addCriterion("canceled >=", value, "canceled");
            return (Criteria) this;
        }

        public Criteria andCanceledLessThan(Integer value) {
            addCriterion("canceled <", value, "canceled");
            return (Criteria) this;
        }

        public Criteria andCanceledLessThanOrEqualTo(Integer value) {
            addCriterion("canceled <=", value, "canceled");
            return (Criteria) this;
        }

        public Criteria andCanceledIn(List<Integer> values) {
            addCriterion("canceled in", values, "canceled");
            return (Criteria) this;
        }

        public Criteria andCanceledNotIn(List<Integer> values) {
            addCriterion("canceled not in", values, "canceled");
            return (Criteria) this;
        }

        public Criteria andCanceledBetween(Integer value1, Integer value2) {
            addCriterion("canceled between", value1, value2, "canceled");
            return (Criteria) this;
        }

        public Criteria andCanceledNotBetween(Integer value1, Integer value2) {
            addCriterion("canceled not between", value1, value2, "canceled");
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

        public Criteria andAddition1IsNull() {
            addCriterion("addition1 is null");
            return (Criteria) this;
        }

        public Criteria andAddition1IsNotNull() {
            addCriterion("addition1 is not null");
            return (Criteria) this;
        }

        public Criteria andAddition1EqualTo(String value) {
            addCriterion("addition1 =", value, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1NotEqualTo(String value) {
            addCriterion("addition1 <>", value, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1GreaterThan(String value) {
            addCriterion("addition1 >", value, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1GreaterThanOrEqualTo(String value) {
            addCriterion("addition1 >=", value, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1LessThan(String value) {
            addCriterion("addition1 <", value, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1LessThanOrEqualTo(String value) {
            addCriterion("addition1 <=", value, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1Like(String value) {
            addCriterion("addition1 like", value, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1NotLike(String value) {
            addCriterion("addition1 not like", value, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1In(List<String> values) {
            addCriterion("addition1 in", values, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1NotIn(List<String> values) {
            addCriterion("addition1 not in", values, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1Between(String value1, String value2) {
            addCriterion("addition1 between", value1, value2, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1NotBetween(String value1, String value2) {
            addCriterion("addition1 not between", value1, value2, "addition1");
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

        public Criteria andSecondNumIsNull() {
            addCriterion("second_num is null");
            return (Criteria) this;
        }

        public Criteria andSecondNumIsNotNull() {
            addCriterion("second_num is not null");
            return (Criteria) this;
        }

        public Criteria andSecondNumEqualTo(Integer value) {
            addCriterion("second_num =", value, "secondNum");
            return (Criteria) this;
        }

        public Criteria andSecondNumNotEqualTo(Integer value) {
            addCriterion("second_num <>", value, "secondNum");
            return (Criteria) this;
        }

        public Criteria andSecondNumGreaterThan(Integer value) {
            addCriterion("second_num >", value, "secondNum");
            return (Criteria) this;
        }

        public Criteria andSecondNumGreaterThanOrEqualTo(Integer value) {
            addCriterion("second_num >=", value, "secondNum");
            return (Criteria) this;
        }

        public Criteria andSecondNumLessThan(Integer value) {
            addCriterion("second_num <", value, "secondNum");
            return (Criteria) this;
        }

        public Criteria andSecondNumLessThanOrEqualTo(Integer value) {
            addCriterion("second_num <=", value, "secondNum");
            return (Criteria) this;
        }

        public Criteria andSecondNumIn(List<Integer> values) {
            addCriterion("second_num in", values, "secondNum");
            return (Criteria) this;
        }

        public Criteria andSecondNumNotIn(List<Integer> values) {
            addCriterion("second_num not in", values, "secondNum");
            return (Criteria) this;
        }

        public Criteria andSecondNumBetween(Integer value1, Integer value2) {
            addCriterion("second_num between", value1, value2, "secondNum");
            return (Criteria) this;
        }

        public Criteria andSecondNumNotBetween(Integer value1, Integer value2) {
            addCriterion("second_num not between", value1, value2, "secondNum");
            return (Criteria) this;
        }

        public Criteria andFirstT1IsNull() {
            addCriterion("first_t1 is null");
            return (Criteria) this;
        }

        public Criteria andFirstT1IsNotNull() {
            addCriterion("first_t1 is not null");
            return (Criteria) this;
        }

        public Criteria andFirstT1EqualTo(Integer value) {
            addCriterion("first_t1 =", value, "firstT1");
            return (Criteria) this;
        }

        public Criteria andFirstT1NotEqualTo(Integer value) {
            addCriterion("first_t1 <>", value, "firstT1");
            return (Criteria) this;
        }

        public Criteria andFirstT1GreaterThan(Integer value) {
            addCriterion("first_t1 >", value, "firstT1");
            return (Criteria) this;
        }

        public Criteria andFirstT1GreaterThanOrEqualTo(Integer value) {
            addCriterion("first_t1 >=", value, "firstT1");
            return (Criteria) this;
        }

        public Criteria andFirstT1LessThan(Integer value) {
            addCriterion("first_t1 <", value, "firstT1");
            return (Criteria) this;
        }

        public Criteria andFirstT1LessThanOrEqualTo(Integer value) {
            addCriterion("first_t1 <=", value, "firstT1");
            return (Criteria) this;
        }

        public Criteria andFirstT1In(List<Integer> values) {
            addCriterion("first_t1 in", values, "firstT1");
            return (Criteria) this;
        }

        public Criteria andFirstT1NotIn(List<Integer> values) {
            addCriterion("first_t1 not in", values, "firstT1");
            return (Criteria) this;
        }

        public Criteria andFirstT1Between(Integer value1, Integer value2) {
            addCriterion("first_t1 between", value1, value2, "firstT1");
            return (Criteria) this;
        }

        public Criteria andFirstT1NotBetween(Integer value1, Integer value2) {
            addCriterion("first_t1 not between", value1, value2, "firstT1");
            return (Criteria) this;
        }

        public Criteria andFirstT2IsNull() {
            addCriterion("first_t2 is null");
            return (Criteria) this;
        }

        public Criteria andFirstT2IsNotNull() {
            addCriterion("first_t2 is not null");
            return (Criteria) this;
        }

        public Criteria andFirstT2EqualTo(Integer value) {
            addCriterion("first_t2 =", value, "firstT2");
            return (Criteria) this;
        }

        public Criteria andFirstT2NotEqualTo(Integer value) {
            addCriterion("first_t2 <>", value, "firstT2");
            return (Criteria) this;
        }

        public Criteria andFirstT2GreaterThan(Integer value) {
            addCriterion("first_t2 >", value, "firstT2");
            return (Criteria) this;
        }

        public Criteria andFirstT2GreaterThanOrEqualTo(Integer value) {
            addCriterion("first_t2 >=", value, "firstT2");
            return (Criteria) this;
        }

        public Criteria andFirstT2LessThan(Integer value) {
            addCriterion("first_t2 <", value, "firstT2");
            return (Criteria) this;
        }

        public Criteria andFirstT2LessThanOrEqualTo(Integer value) {
            addCriterion("first_t2 <=", value, "firstT2");
            return (Criteria) this;
        }

        public Criteria andFirstT2In(List<Integer> values) {
            addCriterion("first_t2 in", values, "firstT2");
            return (Criteria) this;
        }

        public Criteria andFirstT2NotIn(List<Integer> values) {
            addCriterion("first_t2 not in", values, "firstT2");
            return (Criteria) this;
        }

        public Criteria andFirstT2Between(Integer value1, Integer value2) {
            addCriterion("first_t2 between", value1, value2, "firstT2");
            return (Criteria) this;
        }

        public Criteria andFirstT2NotBetween(Integer value1, Integer value2) {
            addCriterion("first_t2 not between", value1, value2, "firstT2");
            return (Criteria) this;
        }

        public Criteria andSecondT1IsNull() {
            addCriterion("second_t1 is null");
            return (Criteria) this;
        }

        public Criteria andSecondT1IsNotNull() {
            addCriterion("second_t1 is not null");
            return (Criteria) this;
        }

        public Criteria andSecondT1EqualTo(Integer value) {
            addCriterion("second_t1 =", value, "secondT1");
            return (Criteria) this;
        }

        public Criteria andSecondT1NotEqualTo(Integer value) {
            addCriterion("second_t1 <>", value, "secondT1");
            return (Criteria) this;
        }

        public Criteria andSecondT1GreaterThan(Integer value) {
            addCriterion("second_t1 >", value, "secondT1");
            return (Criteria) this;
        }

        public Criteria andSecondT1GreaterThanOrEqualTo(Integer value) {
            addCriterion("second_t1 >=", value, "secondT1");
            return (Criteria) this;
        }

        public Criteria andSecondT1LessThan(Integer value) {
            addCriterion("second_t1 <", value, "secondT1");
            return (Criteria) this;
        }

        public Criteria andSecondT1LessThanOrEqualTo(Integer value) {
            addCriterion("second_t1 <=", value, "secondT1");
            return (Criteria) this;
        }

        public Criteria andSecondT1In(List<Integer> values) {
            addCriterion("second_t1 in", values, "secondT1");
            return (Criteria) this;
        }

        public Criteria andSecondT1NotIn(List<Integer> values) {
            addCriterion("second_t1 not in", values, "secondT1");
            return (Criteria) this;
        }

        public Criteria andSecondT1Between(Integer value1, Integer value2) {
            addCriterion("second_t1 between", value1, value2, "secondT1");
            return (Criteria) this;
        }

        public Criteria andSecondT1NotBetween(Integer value1, Integer value2) {
            addCriterion("second_t1 not between", value1, value2, "secondT1");
            return (Criteria) this;
        }

        public Criteria andSecondT2IsNull() {
            addCriterion("second_t2 is null");
            return (Criteria) this;
        }

        public Criteria andSecondT2IsNotNull() {
            addCriterion("second_t2 is not null");
            return (Criteria) this;
        }

        public Criteria andSecondT2EqualTo(Integer value) {
            addCriterion("second_t2 =", value, "secondT2");
            return (Criteria) this;
        }

        public Criteria andSecondT2NotEqualTo(Integer value) {
            addCriterion("second_t2 <>", value, "secondT2");
            return (Criteria) this;
        }

        public Criteria andSecondT2GreaterThan(Integer value) {
            addCriterion("second_t2 >", value, "secondT2");
            return (Criteria) this;
        }

        public Criteria andSecondT2GreaterThanOrEqualTo(Integer value) {
            addCriterion("second_t2 >=", value, "secondT2");
            return (Criteria) this;
        }

        public Criteria andSecondT2LessThan(Integer value) {
            addCriterion("second_t2 <", value, "secondT2");
            return (Criteria) this;
        }

        public Criteria andSecondT2LessThanOrEqualTo(Integer value) {
            addCriterion("second_t2 <=", value, "secondT2");
            return (Criteria) this;
        }

        public Criteria andSecondT2In(List<Integer> values) {
            addCriterion("second_t2 in", values, "secondT2");
            return (Criteria) this;
        }

        public Criteria andSecondT2NotIn(List<Integer> values) {
            addCriterion("second_t2 not in", values, "secondT2");
            return (Criteria) this;
        }

        public Criteria andSecondT2Between(Integer value1, Integer value2) {
            addCriterion("second_t2 between", value1, value2, "secondT2");
            return (Criteria) this;
        }

        public Criteria andSecondT2NotBetween(Integer value1, Integer value2) {
            addCriterion("second_t2 not between", value1, value2, "secondT2");
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

        public Criteria andSecondsFromStartEqualTo(Long value) {
            addCriterion("seconds_from_start =", value, "secondsFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondsFromStartNotEqualTo(Long value) {
            addCriterion("seconds_from_start <>", value, "secondsFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondsFromStartGreaterThan(Long value) {
            addCriterion("seconds_from_start >", value, "secondsFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondsFromStartGreaterThanOrEqualTo(Long value) {
            addCriterion("seconds_from_start >=", value, "secondsFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondsFromStartLessThan(Long value) {
            addCriterion("seconds_from_start <", value, "secondsFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondsFromStartLessThanOrEqualTo(Long value) {
            addCriterion("seconds_from_start <=", value, "secondsFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondsFromStartIn(List<Long> values) {
            addCriterion("seconds_from_start in", values, "secondsFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondsFromStartNotIn(List<Long> values) {
            addCriterion("seconds_from_start not in", values, "secondsFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondsFromStartBetween(Long value1, Long value2) {
            addCriterion("seconds_from_start between", value1, value2, "secondsFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondsFromStartNotBetween(Long value1, Long value2) {
            addCriterion("seconds_from_start not between", value1, value2, "secondsFromStart");
            return (Criteria) this;
        }

        public Criteria andPeriodRemainingSecondsIsNull() {
            addCriterion("period_remaining_seconds is null");
            return (Criteria) this;
        }

        public Criteria andPeriodRemainingSecondsIsNotNull() {
            addCriterion("period_remaining_seconds is not null");
            return (Criteria) this;
        }

        public Criteria andPeriodRemainingSecondsEqualTo(Long value) {
            addCriterion("period_remaining_seconds =", value, "periodRemainingSeconds");
            return (Criteria) this;
        }

        public Criteria andPeriodRemainingSecondsNotEqualTo(Long value) {
            addCriterion("period_remaining_seconds <>", value, "periodRemainingSeconds");
            return (Criteria) this;
        }

        public Criteria andPeriodRemainingSecondsGreaterThan(Long value) {
            addCriterion("period_remaining_seconds >", value, "periodRemainingSeconds");
            return (Criteria) this;
        }

        public Criteria andPeriodRemainingSecondsGreaterThanOrEqualTo(Long value) {
            addCriterion("period_remaining_seconds >=", value, "periodRemainingSeconds");
            return (Criteria) this;
        }

        public Criteria andPeriodRemainingSecondsLessThan(Long value) {
            addCriterion("period_remaining_seconds <", value, "periodRemainingSeconds");
            return (Criteria) this;
        }

        public Criteria andPeriodRemainingSecondsLessThanOrEqualTo(Long value) {
            addCriterion("period_remaining_seconds <=", value, "periodRemainingSeconds");
            return (Criteria) this;
        }

        public Criteria andPeriodRemainingSecondsIn(List<Long> values) {
            addCriterion("period_remaining_seconds in", values, "periodRemainingSeconds");
            return (Criteria) this;
        }

        public Criteria andPeriodRemainingSecondsNotIn(List<Long> values) {
            addCriterion("period_remaining_seconds not in", values, "periodRemainingSeconds");
            return (Criteria) this;
        }

        public Criteria andPeriodRemainingSecondsBetween(Long value1, Long value2) {
            addCriterion("period_remaining_seconds between", value1, value2, "periodRemainingSeconds");
            return (Criteria) this;
        }

        public Criteria andPeriodRemainingSecondsNotBetween(Long value1, Long value2) {
            addCriterion("period_remaining_seconds not between", value1, value2, "periodRemainingSeconds");
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

        public Criteria andAddition3IsNull() {
            addCriterion("addition3 is null");
            return (Criteria) this;
        }

        public Criteria andAddition3IsNotNull() {
            addCriterion("addition3 is not null");
            return (Criteria) this;
        }

        public Criteria andAddition3EqualTo(String value) {
            addCriterion("addition3 =", value, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3NotEqualTo(String value) {
            addCriterion("addition3 <>", value, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3GreaterThan(String value) {
            addCriterion("addition3 >", value, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3GreaterThanOrEqualTo(String value) {
            addCriterion("addition3 >=", value, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3LessThan(String value) {
            addCriterion("addition3 <", value, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3LessThanOrEqualTo(String value) {
            addCriterion("addition3 <=", value, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3Like(String value) {
            addCriterion("addition3 like", value, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3NotLike(String value) {
            addCriterion("addition3 not like", value, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3In(List<String> values) {
            addCriterion("addition3 in", values, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3NotIn(List<String> values) {
            addCriterion("addition3 not in", values, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3Between(String value1, String value2) {
            addCriterion("addition3 between", value1, value2, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3NotBetween(String value1, String value2) {
            addCriterion("addition3 not between", value1, value2, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition4IsNull() {
            addCriterion("addition4 is null");
            return (Criteria) this;
        }

        public Criteria andAddition4IsNotNull() {
            addCriterion("addition4 is not null");
            return (Criteria) this;
        }

        public Criteria andAddition4EqualTo(String value) {
            addCriterion("addition4 =", value, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4NotEqualTo(String value) {
            addCriterion("addition4 <>", value, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4GreaterThan(String value) {
            addCriterion("addition4 >", value, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4GreaterThanOrEqualTo(String value) {
            addCriterion("addition4 >=", value, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4LessThan(String value) {
            addCriterion("addition4 <", value, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4LessThanOrEqualTo(String value) {
            addCriterion("addition4 <=", value, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4Like(String value) {
            addCriterion("addition4 like", value, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4NotLike(String value) {
            addCriterion("addition4 not like", value, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4In(List<String> values) {
            addCriterion("addition4 in", values, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4NotIn(List<String> values) {
            addCriterion("addition4 not in", values, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4Between(String value1, String value2) {
            addCriterion("addition4 between", value1, value2, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4NotBetween(String value1, String value2) {
            addCriterion("addition4 not between", value1, value2, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition5IsNull() {
            addCriterion("addition5 is null");
            return (Criteria) this;
        }

        public Criteria andAddition5IsNotNull() {
            addCriterion("addition5 is not null");
            return (Criteria) this;
        }

        public Criteria andAddition5EqualTo(String value) {
            addCriterion("addition5 =", value, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5NotEqualTo(String value) {
            addCriterion("addition5 <>", value, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5GreaterThan(String value) {
            addCriterion("addition5 >", value, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5GreaterThanOrEqualTo(String value) {
            addCriterion("addition5 >=", value, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5LessThan(String value) {
            addCriterion("addition5 <", value, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5LessThanOrEqualTo(String value) {
            addCriterion("addition5 <=", value, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5Like(String value) {
            addCriterion("addition5 like", value, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5NotLike(String value) {
            addCriterion("addition5 not like", value, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5In(List<String> values) {
            addCriterion("addition5 in", values, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5NotIn(List<String> values) {
            addCriterion("addition5 not in", values, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5Between(String value1, String value2) {
            addCriterion("addition5 between", value1, value2, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5NotBetween(String value1, String value2) {
            addCriterion("addition5 not between", value1, value2, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition6IsNull() {
            addCriterion("addition6 is null");
            return (Criteria) this;
        }

        public Criteria andAddition6IsNotNull() {
            addCriterion("addition6 is not null");
            return (Criteria) this;
        }

        public Criteria andAddition6EqualTo(String value) {
            addCriterion("addition6 =", value, "addition6");
            return (Criteria) this;
        }

        public Criteria andAddition6NotEqualTo(String value) {
            addCriterion("addition6 <>", value, "addition6");
            return (Criteria) this;
        }

        public Criteria andAddition6GreaterThan(String value) {
            addCriterion("addition6 >", value, "addition6");
            return (Criteria) this;
        }

        public Criteria andAddition6GreaterThanOrEqualTo(String value) {
            addCriterion("addition6 >=", value, "addition6");
            return (Criteria) this;
        }

        public Criteria andAddition6LessThan(String value) {
            addCriterion("addition6 <", value, "addition6");
            return (Criteria) this;
        }

        public Criteria andAddition6LessThanOrEqualTo(String value) {
            addCriterion("addition6 <=", value, "addition6");
            return (Criteria) this;
        }

        public Criteria andAddition6Like(String value) {
            addCriterion("addition6 like", value, "addition6");
            return (Criteria) this;
        }

        public Criteria andAddition6NotLike(String value) {
            addCriterion("addition6 not like", value, "addition6");
            return (Criteria) this;
        }

        public Criteria andAddition6In(List<String> values) {
            addCriterion("addition6 in", values, "addition6");
            return (Criteria) this;
        }

        public Criteria andAddition6NotIn(List<String> values) {
            addCriterion("addition6 not in", values, "addition6");
            return (Criteria) this;
        }

        public Criteria andAddition6Between(String value1, String value2) {
            addCriterion("addition6 between", value1, value2, "addition6");
            return (Criteria) this;
        }

        public Criteria andAddition6NotBetween(String value1, String value2) {
            addCriterion("addition6 not between", value1, value2, "addition6");
            return (Criteria) this;
        }

        public Criteria andAddition7IsNull() {
            addCriterion("addition7 is null");
            return (Criteria) this;
        }

        public Criteria andAddition7IsNotNull() {
            addCriterion("addition7 is not null");
            return (Criteria) this;
        }

        public Criteria andAddition7EqualTo(String value) {
            addCriterion("addition7 =", value, "addition7");
            return (Criteria) this;
        }

        public Criteria andAddition7NotEqualTo(String value) {
            addCriterion("addition7 <>", value, "addition7");
            return (Criteria) this;
        }

        public Criteria andAddition7GreaterThan(String value) {
            addCriterion("addition7 >", value, "addition7");
            return (Criteria) this;
        }

        public Criteria andAddition7GreaterThanOrEqualTo(String value) {
            addCriterion("addition7 >=", value, "addition7");
            return (Criteria) this;
        }

        public Criteria andAddition7LessThan(String value) {
            addCriterion("addition7 <", value, "addition7");
            return (Criteria) this;
        }

        public Criteria andAddition7LessThanOrEqualTo(String value) {
            addCriterion("addition7 <=", value, "addition7");
            return (Criteria) this;
        }

        public Criteria andAddition7Like(String value) {
            addCriterion("addition7 like", value, "addition7");
            return (Criteria) this;
        }

        public Criteria andAddition7NotLike(String value) {
            addCriterion("addition7 not like", value, "addition7");
            return (Criteria) this;
        }

        public Criteria andAddition7In(List<String> values) {
            addCriterion("addition7 in", values, "addition7");
            return (Criteria) this;
        }

        public Criteria andAddition7NotIn(List<String> values) {
            addCriterion("addition7 not in", values, "addition7");
            return (Criteria) this;
        }

        public Criteria andAddition7Between(String value1, String value2) {
            addCriterion("addition7 between", value1, value2, "addition7");
            return (Criteria) this;
        }

        public Criteria andAddition7NotBetween(String value1, String value2) {
            addCriterion("addition7 not between", value1, value2, "addition7");
            return (Criteria) this;
        }

        public Criteria andAddition8IsNull() {
            addCriterion("addition8 is null");
            return (Criteria) this;
        }

        public Criteria andAddition8IsNotNull() {
            addCriterion("addition8 is not null");
            return (Criteria) this;
        }

        public Criteria andAddition8EqualTo(String value) {
            addCriterion("addition8 =", value, "addition8");
            return (Criteria) this;
        }

        public Criteria andAddition8NotEqualTo(String value) {
            addCriterion("addition8 <>", value, "addition8");
            return (Criteria) this;
        }

        public Criteria andAddition8GreaterThan(String value) {
            addCriterion("addition8 >", value, "addition8");
            return (Criteria) this;
        }

        public Criteria andAddition8GreaterThanOrEqualTo(String value) {
            addCriterion("addition8 >=", value, "addition8");
            return (Criteria) this;
        }

        public Criteria andAddition8LessThan(String value) {
            addCriterion("addition8 <", value, "addition8");
            return (Criteria) this;
        }

        public Criteria andAddition8LessThanOrEqualTo(String value) {
            addCriterion("addition8 <=", value, "addition8");
            return (Criteria) this;
        }

        public Criteria andAddition8Like(String value) {
            addCriterion("addition8 like", value, "addition8");
            return (Criteria) this;
        }

        public Criteria andAddition8NotLike(String value) {
            addCriterion("addition8 not like", value, "addition8");
            return (Criteria) this;
        }

        public Criteria andAddition8In(List<String> values) {
            addCriterion("addition8 in", values, "addition8");
            return (Criteria) this;
        }

        public Criteria andAddition8NotIn(List<String> values) {
            addCriterion("addition8 not in", values, "addition8");
            return (Criteria) this;
        }

        public Criteria andAddition8Between(String value1, String value2) {
            addCriterion("addition8 between", value1, value2, "addition8");
            return (Criteria) this;
        }

        public Criteria andAddition8NotBetween(String value1, String value2) {
            addCriterion("addition8 not between", value1, value2, "addition8");
            return (Criteria) this;
        }

        public Criteria andAddition9IsNull() {
            addCriterion("addition9 is null");
            return (Criteria) this;
        }

        public Criteria andAddition9IsNotNull() {
            addCriterion("addition9 is not null");
            return (Criteria) this;
        }

        public Criteria andAddition9EqualTo(String value) {
            addCriterion("addition9 =", value, "addition9");
            return (Criteria) this;
        }

        public Criteria andAddition9NotEqualTo(String value) {
            addCriterion("addition9 <>", value, "addition9");
            return (Criteria) this;
        }

        public Criteria andAddition9GreaterThan(String value) {
            addCriterion("addition9 >", value, "addition9");
            return (Criteria) this;
        }

        public Criteria andAddition9GreaterThanOrEqualTo(String value) {
            addCriterion("addition9 >=", value, "addition9");
            return (Criteria) this;
        }

        public Criteria andAddition9LessThan(String value) {
            addCriterion("addition9 <", value, "addition9");
            return (Criteria) this;
        }

        public Criteria andAddition9LessThanOrEqualTo(String value) {
            addCriterion("addition9 <=", value, "addition9");
            return (Criteria) this;
        }

        public Criteria andAddition9Like(String value) {
            addCriterion("addition9 like", value, "addition9");
            return (Criteria) this;
        }

        public Criteria andAddition9NotLike(String value) {
            addCriterion("addition9 not like", value, "addition9");
            return (Criteria) this;
        }

        public Criteria andAddition9In(List<String> values) {
            addCriterion("addition9 in", values, "addition9");
            return (Criteria) this;
        }

        public Criteria andAddition9NotIn(List<String> values) {
            addCriterion("addition9 not in", values, "addition9");
            return (Criteria) this;
        }

        public Criteria andAddition9Between(String value1, String value2) {
            addCriterion("addition9 between", value1, value2, "addition9");
            return (Criteria) this;
        }

        public Criteria andAddition9NotBetween(String value1, String value2) {
            addCriterion("addition9 not between", value1, value2, "addition9");
            return (Criteria) this;
        }

        public Criteria andAddition10IsNull() {
            addCriterion("addition10 is null");
            return (Criteria) this;
        }

        public Criteria andAddition10IsNotNull() {
            addCriterion("addition10 is not null");
            return (Criteria) this;
        }

        public Criteria andAddition10EqualTo(String value) {
            addCriterion("addition10 =", value, "addition10");
            return (Criteria) this;
        }

        public Criteria andAddition10NotEqualTo(String value) {
            addCriterion("addition10 <>", value, "addition10");
            return (Criteria) this;
        }

        public Criteria andAddition10GreaterThan(String value) {
            addCriterion("addition10 >", value, "addition10");
            return (Criteria) this;
        }

        public Criteria andAddition10GreaterThanOrEqualTo(String value) {
            addCriterion("addition10 >=", value, "addition10");
            return (Criteria) this;
        }

        public Criteria andAddition10LessThan(String value) {
            addCriterion("addition10 <", value, "addition10");
            return (Criteria) this;
        }

        public Criteria andAddition10LessThanOrEqualTo(String value) {
            addCriterion("addition10 <=", value, "addition10");
            return (Criteria) this;
        }

        public Criteria andAddition10Like(String value) {
            addCriterion("addition10 like", value, "addition10");
            return (Criteria) this;
        }

        public Criteria andAddition10NotLike(String value) {
            addCriterion("addition10 not like", value, "addition10");
            return (Criteria) this;
        }

        public Criteria andAddition10In(List<String> values) {
            addCriterion("addition10 in", values, "addition10");
            return (Criteria) this;
        }

        public Criteria andAddition10NotIn(List<String> values) {
            addCriterion("addition10 not in", values, "addition10");
            return (Criteria) this;
        }

        public Criteria andAddition10Between(String value1, String value2) {
            addCriterion("addition10 between", value1, value2, "addition10");
            return (Criteria) this;
        }

        public Criteria andAddition10NotBetween(String value1, String value2) {
            addCriterion("addition10 not between", value1, value2, "addition10");
            return (Criteria) this;
        }

        public Criteria andAddition2IsNull() {
            addCriterion("addition2 is null");
            return (Criteria) this;
        }

        public Criteria andAddition2IsNotNull() {
            addCriterion("addition2 is not null");
            return (Criteria) this;
        }

        public Criteria andAddition2EqualTo(String value) {
            addCriterion("addition2 =", value, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2NotEqualTo(String value) {
            addCriterion("addition2 <>", value, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2GreaterThan(String value) {
            addCriterion("addition2 >", value, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2GreaterThanOrEqualTo(String value) {
            addCriterion("addition2 >=", value, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2LessThan(String value) {
            addCriterion("addition2 <", value, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2LessThanOrEqualTo(String value) {
            addCriterion("addition2 <=", value, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2Like(String value) {
            addCriterion("addition2 like", value, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2NotLike(String value) {
            addCriterion("addition2 not like", value, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2In(List<String> values) {
            addCriterion("addition2 in", values, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2NotIn(List<String> values) {
            addCriterion("addition2 not in", values, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2Between(String value1, String value2) {
            addCriterion("addition2 between", value1, value2, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2NotBetween(String value1, String value2) {
            addCriterion("addition2 not between", value1, value2, "addition2");
            return (Criteria) this;
        }

        public Criteria andSendDataIsNull() {
            addCriterion("send_data is null");
            return (Criteria) this;
        }

        public Criteria andSendDataIsNotNull() {
            addCriterion("send_data is not null");
            return (Criteria) this;
        }

        public Criteria andSendDataEqualTo(String value) {
            addCriterion("send_data =", value, "sendData");
            return (Criteria) this;
        }

        public Criteria andSendDataNotEqualTo(String value) {
            addCriterion("send_data <>", value, "sendData");
            return (Criteria) this;
        }

        public Criteria andSendDataGreaterThan(String value) {
            addCriterion("send_data >", value, "sendData");
            return (Criteria) this;
        }

        public Criteria andSendDataGreaterThanOrEqualTo(String value) {
            addCriterion("send_data >=", value, "sendData");
            return (Criteria) this;
        }

        public Criteria andSendDataLessThan(String value) {
            addCriterion("send_data <", value, "sendData");
            return (Criteria) this;
        }

        public Criteria andSendDataLessThanOrEqualTo(String value) {
            addCriterion("send_data <=", value, "sendData");
            return (Criteria) this;
        }

        public Criteria andSendDataLike(String value) {
            addCriterion("send_data like", value, "sendData");
            return (Criteria) this;
        }

        public Criteria andSendDataNotLike(String value) {
            addCriterion("send_data not like", value, "sendData");
            return (Criteria) this;
        }

        public Criteria andSendDataIn(List<String> values) {
            addCriterion("send_data in", values, "sendData");
            return (Criteria) this;
        }

        public Criteria andSendDataNotIn(List<String> values) {
            addCriterion("send_data not in", values, "sendData");
            return (Criteria) this;
        }

        public Criteria andSendDataBetween(String value1, String value2) {
            addCriterion("send_data between", value1, value2, "sendData");
            return (Criteria) this;
        }

        public Criteria andSendDataNotBetween(String value1, String value2) {
            addCriterion("send_data not between", value1, value2, "sendData");
            return (Criteria) this;
        }

        public Criteria andLinkIdIsNull() {
            addCriterion("link_id is null");
            return (Criteria) this;
        }

        public Criteria andLinkIdIsNotNull() {
            addCriterion("link_id is not null");
            return (Criteria) this;
        }

        public Criteria andLinkIdEqualTo(String value) {
            addCriterion("link_id =", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdNotEqualTo(String value) {
            addCriterion("link_id <>", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdGreaterThan(String value) {
            addCriterion("link_id >", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdGreaterThanOrEqualTo(String value) {
            addCriterion("link_id >=", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdLessThan(String value) {
            addCriterion("link_id <", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdLessThanOrEqualTo(String value) {
            addCriterion("link_id <=", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdLike(String value) {
            addCriterion("link_id like", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdNotLike(String value) {
            addCriterion("link_id not like", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdIn(List<String> values) {
            addCriterion("link_id in", values, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdNotIn(List<String> values) {
            addCriterion("link_id not in", values, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdBetween(String value1, String value2) {
            addCriterion("link_id between", value1, value2, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdNotBetween(String value1, String value2) {
            addCriterion("link_id not between", value1, value2, "linkId");
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