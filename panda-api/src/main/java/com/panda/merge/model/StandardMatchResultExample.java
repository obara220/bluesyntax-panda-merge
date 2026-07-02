package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class StandardMatchResultExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public StandardMatchResultExample() {
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

        public Criteria andPeriodRemainingSecondsIsNull() {
            addCriterion("period_remaining_seconds is null");
            return (Criteria) this;
        }

        public Criteria andPeriodRemainingSecondsIsNotNull() {
            addCriterion("period_remaining_seconds is not null");
            return (Criteria) this;
        }

        public Criteria andPeriodRemainingSecondsEqualTo(Integer value) {
            addCriterion("period_remaining_seconds =", value, "periodRemainingSeconds");
            return (Criteria) this;
        }

        public Criteria andPeriodRemainingSecondsNotEqualTo(Integer value) {
            addCriterion("period_remaining_seconds <>", value, "periodRemainingSeconds");
            return (Criteria) this;
        }

        public Criteria andPeriodRemainingSecondsGreaterThan(Integer value) {
            addCriterion("period_remaining_seconds >", value, "periodRemainingSeconds");
            return (Criteria) this;
        }

        public Criteria andPeriodRemainingSecondsGreaterThanOrEqualTo(Integer value) {
            addCriterion("period_remaining_seconds >=", value, "periodRemainingSeconds");
            return (Criteria) this;
        }

        public Criteria andPeriodRemainingSecondsLessThan(Integer value) {
            addCriterion("period_remaining_seconds <", value, "periodRemainingSeconds");
            return (Criteria) this;
        }

        public Criteria andPeriodRemainingSecondsLessThanOrEqualTo(Integer value) {
            addCriterion("period_remaining_seconds <=", value, "periodRemainingSeconds");
            return (Criteria) this;
        }

        public Criteria andPeriodRemainingSecondsIn(List<Integer> values) {
            addCriterion("period_remaining_seconds in", values, "periodRemainingSeconds");
            return (Criteria) this;
        }

        public Criteria andPeriodRemainingSecondsNotIn(List<Integer> values) {
            addCriterion("period_remaining_seconds not in", values, "periodRemainingSeconds");
            return (Criteria) this;
        }

        public Criteria andPeriodRemainingSecondsBetween(Integer value1, Integer value2) {
            addCriterion("period_remaining_seconds between", value1, value2, "periodRemainingSeconds");
            return (Criteria) this;
        }

        public Criteria andPeriodRemainingSecondsNotBetween(Integer value1, Integer value2) {
            addCriterion("period_remaining_seconds not between", value1, value2, "periodRemainingSeconds");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmTimeIsNull() {
            addCriterion("auto_confirm_time is null");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmTimeIsNotNull() {
            addCriterion("auto_confirm_time is not null");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmTimeEqualTo(Integer value) {
            addCriterion("auto_confirm_time =", value, "autoConfirmTime");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmTimeNotEqualTo(Integer value) {
            addCriterion("auto_confirm_time <>", value, "autoConfirmTime");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmTimeGreaterThan(Integer value) {
            addCriterion("auto_confirm_time >", value, "autoConfirmTime");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmTimeGreaterThanOrEqualTo(Integer value) {
            addCriterion("auto_confirm_time >=", value, "autoConfirmTime");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmTimeLessThan(Integer value) {
            addCriterion("auto_confirm_time <", value, "autoConfirmTime");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmTimeLessThanOrEqualTo(Integer value) {
            addCriterion("auto_confirm_time <=", value, "autoConfirmTime");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmTimeIn(List<Integer> values) {
            addCriterion("auto_confirm_time in", values, "autoConfirmTime");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmTimeNotIn(List<Integer> values) {
            addCriterion("auto_confirm_time not in", values, "autoConfirmTime");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmTimeBetween(Integer value1, Integer value2) {
            addCriterion("auto_confirm_time between", value1, value2, "autoConfirmTime");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmTimeNotBetween(Integer value1, Integer value2) {
            addCriterion("auto_confirm_time not between", value1, value2, "autoConfirmTime");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmUsedTimeIsNull() {
            addCriterion("auto_confirm_used_time is null");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmUsedTimeIsNotNull() {
            addCriterion("auto_confirm_used_time is not null");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmUsedTimeEqualTo(Integer value) {
            addCriterion("auto_confirm_used_time =", value, "autoConfirmUsedTime");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmUsedTimeNotEqualTo(Integer value) {
            addCriterion("auto_confirm_used_time <>", value, "autoConfirmUsedTime");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmUsedTimeGreaterThan(Integer value) {
            addCriterion("auto_confirm_used_time >", value, "autoConfirmUsedTime");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmUsedTimeGreaterThanOrEqualTo(Integer value) {
            addCriterion("auto_confirm_used_time >=", value, "autoConfirmUsedTime");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmUsedTimeLessThan(Integer value) {
            addCriterion("auto_confirm_used_time <", value, "autoConfirmUsedTime");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmUsedTimeLessThanOrEqualTo(Integer value) {
            addCriterion("auto_confirm_used_time <=", value, "autoConfirmUsedTime");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmUsedTimeIn(List<Integer> values) {
            addCriterion("auto_confirm_used_time in", values, "autoConfirmUsedTime");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmUsedTimeNotIn(List<Integer> values) {
            addCriterion("auto_confirm_used_time not in", values, "autoConfirmUsedTime");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmUsedTimeBetween(Integer value1, Integer value2) {
            addCriterion("auto_confirm_used_time between", value1, value2, "autoConfirmUsedTime");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmUsedTimeNotBetween(Integer value1, Integer value2) {
            addCriterion("auto_confirm_used_time not between", value1, value2, "autoConfirmUsedTime");
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

        public Criteria andSecondNumberIsNull() {
            addCriterion("second_number is null");
            return (Criteria) this;
        }

        public Criteria andSecondNumberIsNotNull() {
            addCriterion("second_number is not null");
            return (Criteria) this;
        }

        public Criteria andSecondNumberEqualTo(Integer value) {
            addCriterion("second_number =", value, "secondNumber");
            return (Criteria) this;
        }

        public Criteria andSecondNumberNotEqualTo(Integer value) {
            addCriterion("second_number <>", value, "secondNumber");
            return (Criteria) this;
        }

        public Criteria andSecondNumberGreaterThan(Integer value) {
            addCriterion("second_number >", value, "secondNumber");
            return (Criteria) this;
        }

        public Criteria andSecondNumberGreaterThanOrEqualTo(Integer value) {
            addCriterion("second_number >=", value, "secondNumber");
            return (Criteria) this;
        }

        public Criteria andSecondNumberLessThan(Integer value) {
            addCriterion("second_number <", value, "secondNumber");
            return (Criteria) this;
        }

        public Criteria andSecondNumberLessThanOrEqualTo(Integer value) {
            addCriterion("second_number <=", value, "secondNumber");
            return (Criteria) this;
        }

        public Criteria andSecondNumberIn(List<Integer> values) {
            addCriterion("second_number in", values, "secondNumber");
            return (Criteria) this;
        }

        public Criteria andSecondNumberNotIn(List<Integer> values) {
            addCriterion("second_number not in", values, "secondNumber");
            return (Criteria) this;
        }

        public Criteria andSecondNumberBetween(Integer value1, Integer value2) {
            addCriterion("second_number between", value1, value2, "secondNumber");
            return (Criteria) this;
        }

        public Criteria andSecondNumberNotBetween(Integer value1, Integer value2) {
            addCriterion("second_number not between", value1, value2, "secondNumber");
            return (Criteria) this;
        }

        public Criteria andFirstNumberIsNull() {
            addCriterion("first_number is null");
            return (Criteria) this;
        }

        public Criteria andFirstNumberIsNotNull() {
            addCriterion("first_number is not null");
            return (Criteria) this;
        }

        public Criteria andFirstNumberEqualTo(Integer value) {
            addCriterion("first_number =", value, "firstNumber");
            return (Criteria) this;
        }

        public Criteria andFirstNumberNotEqualTo(Integer value) {
            addCriterion("first_number <>", value, "firstNumber");
            return (Criteria) this;
        }

        public Criteria andFirstNumberGreaterThan(Integer value) {
            addCriterion("first_number >", value, "firstNumber");
            return (Criteria) this;
        }

        public Criteria andFirstNumberGreaterThanOrEqualTo(Integer value) {
            addCriterion("first_number >=", value, "firstNumber");
            return (Criteria) this;
        }

        public Criteria andFirstNumberLessThan(Integer value) {
            addCriterion("first_number <", value, "firstNumber");
            return (Criteria) this;
        }

        public Criteria andFirstNumberLessThanOrEqualTo(Integer value) {
            addCriterion("first_number <=", value, "firstNumber");
            return (Criteria) this;
        }

        public Criteria andFirstNumberIn(List<Integer> values) {
            addCriterion("first_number in", values, "firstNumber");
            return (Criteria) this;
        }

        public Criteria andFirstNumberNotIn(List<Integer> values) {
            addCriterion("first_number not in", values, "firstNumber");
            return (Criteria) this;
        }

        public Criteria andFirstNumberBetween(Integer value1, Integer value2) {
            addCriterion("first_number between", value1, value2, "firstNumber");
            return (Criteria) this;
        }

        public Criteria andFirstNumberNotBetween(Integer value1, Integer value2) {
            addCriterion("first_number not between", value1, value2, "firstNumber");
            return (Criteria) this;
        }

        public Criteria andHomeFirstNumberIsNull() {
            addCriterion("home_first_number is null");
            return (Criteria) this;
        }

        public Criteria andHomeFirstNumberIsNotNull() {
            addCriterion("home_first_number is not null");
            return (Criteria) this;
        }

        public Criteria andHomeFirstNumberEqualTo(Integer value) {
            addCriterion("home_first_number =", value, "homeFirstNumber");
            return (Criteria) this;
        }

        public Criteria andHomeFirstNumberNotEqualTo(Integer value) {
            addCriterion("home_first_number <>", value, "homeFirstNumber");
            return (Criteria) this;
        }

        public Criteria andHomeFirstNumberGreaterThan(Integer value) {
            addCriterion("home_first_number >", value, "homeFirstNumber");
            return (Criteria) this;
        }

        public Criteria andHomeFirstNumberGreaterThanOrEqualTo(Integer value) {
            addCriterion("home_first_number >=", value, "homeFirstNumber");
            return (Criteria) this;
        }

        public Criteria andHomeFirstNumberLessThan(Integer value) {
            addCriterion("home_first_number <", value, "homeFirstNumber");
            return (Criteria) this;
        }

        public Criteria andHomeFirstNumberLessThanOrEqualTo(Integer value) {
            addCriterion("home_first_number <=", value, "homeFirstNumber");
            return (Criteria) this;
        }

        public Criteria andHomeFirstNumberIn(List<Integer> values) {
            addCriterion("home_first_number in", values, "homeFirstNumber");
            return (Criteria) this;
        }

        public Criteria andHomeFirstNumberNotIn(List<Integer> values) {
            addCriterion("home_first_number not in", values, "homeFirstNumber");
            return (Criteria) this;
        }

        public Criteria andHomeFirstNumberBetween(Integer value1, Integer value2) {
            addCriterion("home_first_number between", value1, value2, "homeFirstNumber");
            return (Criteria) this;
        }

        public Criteria andHomeFirstNumberNotBetween(Integer value1, Integer value2) {
            addCriterion("home_first_number not between", value1, value2, "homeFirstNumber");
            return (Criteria) this;
        }

        public Criteria andAwayFirstNumberIsNull() {
            addCriterion("away_first_number is null");
            return (Criteria) this;
        }

        public Criteria andAwayFirstNumberIsNotNull() {
            addCriterion("away_first_number is not null");
            return (Criteria) this;
        }

        public Criteria andAwayFirstNumberEqualTo(Integer value) {
            addCriterion("away_first_number =", value, "awayFirstNumber");
            return (Criteria) this;
        }

        public Criteria andAwayFirstNumberNotEqualTo(Integer value) {
            addCriterion("away_first_number <>", value, "awayFirstNumber");
            return (Criteria) this;
        }

        public Criteria andAwayFirstNumberGreaterThan(Integer value) {
            addCriterion("away_first_number >", value, "awayFirstNumber");
            return (Criteria) this;
        }

        public Criteria andAwayFirstNumberGreaterThanOrEqualTo(Integer value) {
            addCriterion("away_first_number >=", value, "awayFirstNumber");
            return (Criteria) this;
        }

        public Criteria andAwayFirstNumberLessThan(Integer value) {
            addCriterion("away_first_number <", value, "awayFirstNumber");
            return (Criteria) this;
        }

        public Criteria andAwayFirstNumberLessThanOrEqualTo(Integer value) {
            addCriterion("away_first_number <=", value, "awayFirstNumber");
            return (Criteria) this;
        }

        public Criteria andAwayFirstNumberIn(List<Integer> values) {
            addCriterion("away_first_number in", values, "awayFirstNumber");
            return (Criteria) this;
        }

        public Criteria andAwayFirstNumberNotIn(List<Integer> values) {
            addCriterion("away_first_number not in", values, "awayFirstNumber");
            return (Criteria) this;
        }

        public Criteria andAwayFirstNumberBetween(Integer value1, Integer value2) {
            addCriterion("away_first_number between", value1, value2, "awayFirstNumber");
            return (Criteria) this;
        }

        public Criteria andAwayFirstNumberNotBetween(Integer value1, Integer value2) {
            addCriterion("away_first_number not between", value1, value2, "awayFirstNumber");
            return (Criteria) this;
        }

        public Criteria andHomeSecondNumberIsNull() {
            addCriterion("home_second_number is null");
            return (Criteria) this;
        }

        public Criteria andHomeSecondNumberIsNotNull() {
            addCriterion("home_second_number is not null");
            return (Criteria) this;
        }

        public Criteria andHomeSecondNumberEqualTo(Integer value) {
            addCriterion("home_second_number =", value, "homeSecondNumber");
            return (Criteria) this;
        }

        public Criteria andHomeSecondNumberNotEqualTo(Integer value) {
            addCriterion("home_second_number <>", value, "homeSecondNumber");
            return (Criteria) this;
        }

        public Criteria andHomeSecondNumberGreaterThan(Integer value) {
            addCriterion("home_second_number >", value, "homeSecondNumber");
            return (Criteria) this;
        }

        public Criteria andHomeSecondNumberGreaterThanOrEqualTo(Integer value) {
            addCriterion("home_second_number >=", value, "homeSecondNumber");
            return (Criteria) this;
        }

        public Criteria andHomeSecondNumberLessThan(Integer value) {
            addCriterion("home_second_number <", value, "homeSecondNumber");
            return (Criteria) this;
        }

        public Criteria andHomeSecondNumberLessThanOrEqualTo(Integer value) {
            addCriterion("home_second_number <=", value, "homeSecondNumber");
            return (Criteria) this;
        }

        public Criteria andHomeSecondNumberIn(List<Integer> values) {
            addCriterion("home_second_number in", values, "homeSecondNumber");
            return (Criteria) this;
        }

        public Criteria andHomeSecondNumberNotIn(List<Integer> values) {
            addCriterion("home_second_number not in", values, "homeSecondNumber");
            return (Criteria) this;
        }

        public Criteria andHomeSecondNumberBetween(Integer value1, Integer value2) {
            addCriterion("home_second_number between", value1, value2, "homeSecondNumber");
            return (Criteria) this;
        }

        public Criteria andHomeSecondNumberNotBetween(Integer value1, Integer value2) {
            addCriterion("home_second_number not between", value1, value2, "homeSecondNumber");
            return (Criteria) this;
        }

        public Criteria andAwaySecondNumberIsNull() {
            addCriterion("away_second_number is null");
            return (Criteria) this;
        }

        public Criteria andAwaySecondNumberIsNotNull() {
            addCriterion("away_second_number is not null");
            return (Criteria) this;
        }

        public Criteria andAwaySecondNumberEqualTo(Integer value) {
            addCriterion("away_second_number =", value, "awaySecondNumber");
            return (Criteria) this;
        }

        public Criteria andAwaySecondNumberNotEqualTo(Integer value) {
            addCriterion("away_second_number <>", value, "awaySecondNumber");
            return (Criteria) this;
        }

        public Criteria andAwaySecondNumberGreaterThan(Integer value) {
            addCriterion("away_second_number >", value, "awaySecondNumber");
            return (Criteria) this;
        }

        public Criteria andAwaySecondNumberGreaterThanOrEqualTo(Integer value) {
            addCriterion("away_second_number >=", value, "awaySecondNumber");
            return (Criteria) this;
        }

        public Criteria andAwaySecondNumberLessThan(Integer value) {
            addCriterion("away_second_number <", value, "awaySecondNumber");
            return (Criteria) this;
        }

        public Criteria andAwaySecondNumberLessThanOrEqualTo(Integer value) {
            addCriterion("away_second_number <=", value, "awaySecondNumber");
            return (Criteria) this;
        }

        public Criteria andAwaySecondNumberIn(List<Integer> values) {
            addCriterion("away_second_number in", values, "awaySecondNumber");
            return (Criteria) this;
        }

        public Criteria andAwaySecondNumberNotIn(List<Integer> values) {
            addCriterion("away_second_number not in", values, "awaySecondNumber");
            return (Criteria) this;
        }

        public Criteria andAwaySecondNumberBetween(Integer value1, Integer value2) {
            addCriterion("away_second_number between", value1, value2, "awaySecondNumber");
            return (Criteria) this;
        }

        public Criteria andAwaySecondNumberNotBetween(Integer value1, Integer value2) {
            addCriterion("away_second_number not between", value1, value2, "awaySecondNumber");
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

        public Criteria andTeamNameZsIsNull() {
            addCriterion("team_name_zs is null");
            return (Criteria) this;
        }

        public Criteria andTeamNameZsIsNotNull() {
            addCriterion("team_name_zs is not null");
            return (Criteria) this;
        }

        public Criteria andTeamNameZsEqualTo(String value) {
            addCriterion("team_name_zs =", value, "teamNameZs");
            return (Criteria) this;
        }

        public Criteria andTeamNameZsNotEqualTo(String value) {
            addCriterion("team_name_zs <>", value, "teamNameZs");
            return (Criteria) this;
        }

        public Criteria andTeamNameZsGreaterThan(String value) {
            addCriterion("team_name_zs >", value, "teamNameZs");
            return (Criteria) this;
        }

        public Criteria andTeamNameZsGreaterThanOrEqualTo(String value) {
            addCriterion("team_name_zs >=", value, "teamNameZs");
            return (Criteria) this;
        }

        public Criteria andTeamNameZsLessThan(String value) {
            addCriterion("team_name_zs <", value, "teamNameZs");
            return (Criteria) this;
        }

        public Criteria andTeamNameZsLessThanOrEqualTo(String value) {
            addCriterion("team_name_zs <=", value, "teamNameZs");
            return (Criteria) this;
        }

        public Criteria andTeamNameZsLike(String value) {
            addCriterion("team_name_zs like", value, "teamNameZs");
            return (Criteria) this;
        }

        public Criteria andTeamNameZsNotLike(String value) {
            addCriterion("team_name_zs not like", value, "teamNameZs");
            return (Criteria) this;
        }

        public Criteria andTeamNameZsIn(List<String> values) {
            addCriterion("team_name_zs in", values, "teamNameZs");
            return (Criteria) this;
        }

        public Criteria andTeamNameZsNotIn(List<String> values) {
            addCriterion("team_name_zs not in", values, "teamNameZs");
            return (Criteria) this;
        }

        public Criteria andTeamNameZsBetween(String value1, String value2) {
            addCriterion("team_name_zs between", value1, value2, "teamNameZs");
            return (Criteria) this;
        }

        public Criteria andTeamNameZsNotBetween(String value1, String value2) {
            addCriterion("team_name_zs not between", value1, value2, "teamNameZs");
            return (Criteria) this;
        }

        public Criteria andEventHomeNumberIsNull() {
            addCriterion("event_home_number is null");
            return (Criteria) this;
        }

        public Criteria andEventHomeNumberIsNotNull() {
            addCriterion("event_home_number is not null");
            return (Criteria) this;
        }

        public Criteria andEventHomeNumberEqualTo(Integer value) {
            addCriterion("event_home_number =", value, "eventHomeNumber");
            return (Criteria) this;
        }

        public Criteria andEventHomeNumberNotEqualTo(Integer value) {
            addCriterion("event_home_number <>", value, "eventHomeNumber");
            return (Criteria) this;
        }

        public Criteria andEventHomeNumberGreaterThan(Integer value) {
            addCriterion("event_home_number >", value, "eventHomeNumber");
            return (Criteria) this;
        }

        public Criteria andEventHomeNumberGreaterThanOrEqualTo(Integer value) {
            addCriterion("event_home_number >=", value, "eventHomeNumber");
            return (Criteria) this;
        }

        public Criteria andEventHomeNumberLessThan(Integer value) {
            addCriterion("event_home_number <", value, "eventHomeNumber");
            return (Criteria) this;
        }

        public Criteria andEventHomeNumberLessThanOrEqualTo(Integer value) {
            addCriterion("event_home_number <=", value, "eventHomeNumber");
            return (Criteria) this;
        }

        public Criteria andEventHomeNumberIn(List<Integer> values) {
            addCriterion("event_home_number in", values, "eventHomeNumber");
            return (Criteria) this;
        }

        public Criteria andEventHomeNumberNotIn(List<Integer> values) {
            addCriterion("event_home_number not in", values, "eventHomeNumber");
            return (Criteria) this;
        }

        public Criteria andEventHomeNumberBetween(Integer value1, Integer value2) {
            addCriterion("event_home_number between", value1, value2, "eventHomeNumber");
            return (Criteria) this;
        }

        public Criteria andEventHomeNumberNotBetween(Integer value1, Integer value2) {
            addCriterion("event_home_number not between", value1, value2, "eventHomeNumber");
            return (Criteria) this;
        }

        public Criteria andEventAwayNumberIsNull() {
            addCriterion("event_away_number is null");
            return (Criteria) this;
        }

        public Criteria andEventAwayNumberIsNotNull() {
            addCriterion("event_away_number is not null");
            return (Criteria) this;
        }

        public Criteria andEventAwayNumberEqualTo(Integer value) {
            addCriterion("event_away_number =", value, "eventAwayNumber");
            return (Criteria) this;
        }

        public Criteria andEventAwayNumberNotEqualTo(Integer value) {
            addCriterion("event_away_number <>", value, "eventAwayNumber");
            return (Criteria) this;
        }

        public Criteria andEventAwayNumberGreaterThan(Integer value) {
            addCriterion("event_away_number >", value, "eventAwayNumber");
            return (Criteria) this;
        }

        public Criteria andEventAwayNumberGreaterThanOrEqualTo(Integer value) {
            addCriterion("event_away_number >=", value, "eventAwayNumber");
            return (Criteria) this;
        }

        public Criteria andEventAwayNumberLessThan(Integer value) {
            addCriterion("event_away_number <", value, "eventAwayNumber");
            return (Criteria) this;
        }

        public Criteria andEventAwayNumberLessThanOrEqualTo(Integer value) {
            addCriterion("event_away_number <=", value, "eventAwayNumber");
            return (Criteria) this;
        }

        public Criteria andEventAwayNumberIn(List<Integer> values) {
            addCriterion("event_away_number in", values, "eventAwayNumber");
            return (Criteria) this;
        }

        public Criteria andEventAwayNumberNotIn(List<Integer> values) {
            addCriterion("event_away_number not in", values, "eventAwayNumber");
            return (Criteria) this;
        }

        public Criteria andEventAwayNumberBetween(Integer value1, Integer value2) {
            addCriterion("event_away_number between", value1, value2, "eventAwayNumber");
            return (Criteria) this;
        }

        public Criteria andEventAwayNumberNotBetween(Integer value1, Integer value2) {
            addCriterion("event_away_number not between", value1, value2, "eventAwayNumber");
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

        public Criteria andConfirmOperationTimesIsNull() {
            addCriterion("confirm_operation_times is null");
            return (Criteria) this;
        }

        public Criteria andConfirmOperationTimesIsNotNull() {
            addCriterion("confirm_operation_times is not null");
            return (Criteria) this;
        }

        public Criteria andConfirmOperationTimesEqualTo(Integer value) {
            addCriterion("confirm_operation_times =", value, "confirmOperationTimes");
            return (Criteria) this;
        }

        public Criteria andConfirmOperationTimesNotEqualTo(Integer value) {
            addCriterion("confirm_operation_times <>", value, "confirmOperationTimes");
            return (Criteria) this;
        }

        public Criteria andConfirmOperationTimesGreaterThan(Integer value) {
            addCriterion("confirm_operation_times >", value, "confirmOperationTimes");
            return (Criteria) this;
        }

        public Criteria andConfirmOperationTimesGreaterThanOrEqualTo(Integer value) {
            addCriterion("confirm_operation_times >=", value, "confirmOperationTimes");
            return (Criteria) this;
        }

        public Criteria andConfirmOperationTimesLessThan(Integer value) {
            addCriterion("confirm_operation_times <", value, "confirmOperationTimes");
            return (Criteria) this;
        }

        public Criteria andConfirmOperationTimesLessThanOrEqualTo(Integer value) {
            addCriterion("confirm_operation_times <=", value, "confirmOperationTimes");
            return (Criteria) this;
        }

        public Criteria andConfirmOperationTimesIn(List<Integer> values) {
            addCriterion("confirm_operation_times in", values, "confirmOperationTimes");
            return (Criteria) this;
        }

        public Criteria andConfirmOperationTimesNotIn(List<Integer> values) {
            addCriterion("confirm_operation_times not in", values, "confirmOperationTimes");
            return (Criteria) this;
        }

        public Criteria andConfirmOperationTimesBetween(Integer value1, Integer value2) {
            addCriterion("confirm_operation_times between", value1, value2, "confirmOperationTimes");
            return (Criteria) this;
        }

        public Criteria andConfirmOperationTimesNotBetween(Integer value1, Integer value2) {
            addCriterion("confirm_operation_times not between", value1, value2, "confirmOperationTimes");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmStartTimestampIsNull() {
            addCriterion("auto_confirm_start_timestamp is null");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmStartTimestampIsNotNull() {
            addCriterion("auto_confirm_start_timestamp is not null");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmStartTimestampEqualTo(Long value) {
            addCriterion("auto_confirm_start_timestamp =", value, "autoConfirmStartTimestamp");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmStartTimestampNotEqualTo(Long value) {
            addCriterion("auto_confirm_start_timestamp <>", value, "autoConfirmStartTimestamp");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmStartTimestampGreaterThan(Long value) {
            addCriterion("auto_confirm_start_timestamp >", value, "autoConfirmStartTimestamp");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmStartTimestampGreaterThanOrEqualTo(Long value) {
            addCriterion("auto_confirm_start_timestamp >=", value, "autoConfirmStartTimestamp");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmStartTimestampLessThan(Long value) {
            addCriterion("auto_confirm_start_timestamp <", value, "autoConfirmStartTimestamp");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmStartTimestampLessThanOrEqualTo(Long value) {
            addCriterion("auto_confirm_start_timestamp <=", value, "autoConfirmStartTimestamp");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmStartTimestampIn(List<Long> values) {
            addCriterion("auto_confirm_start_timestamp in", values, "autoConfirmStartTimestamp");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmStartTimestampNotIn(List<Long> values) {
            addCriterion("auto_confirm_start_timestamp not in", values, "autoConfirmStartTimestamp");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmStartTimestampBetween(Long value1, Long value2) {
            addCriterion("auto_confirm_start_timestamp between", value1, value2, "autoConfirmStartTimestamp");
            return (Criteria) this;
        }

        public Criteria andAutoConfirmStartTimestampNotBetween(Long value1, Long value2) {
            addCriterion("auto_confirm_start_timestamp not between", value1, value2, "autoConfirmStartTimestamp");
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

        public Criteria andEventWeightIsNull() {
            addCriterion("event_weight is null");
            return (Criteria) this;
        }

        public Criteria andEventWeightIsNotNull() {
            addCriterion("event_weight is not null");
            return (Criteria) this;
        }

        public Criteria andEventWeightEqualTo(Integer value) {
            addCriterion("event_weight =", value, "eventWeight");
            return (Criteria) this;
        }

        public Criteria andEventWeightNotEqualTo(Integer value) {
            addCriterion("event_weight <>", value, "eventWeight");
            return (Criteria) this;
        }

        public Criteria andEventWeightGreaterThan(Integer value) {
            addCriterion("event_weight >", value, "eventWeight");
            return (Criteria) this;
        }

        public Criteria andEventWeightGreaterThanOrEqualTo(Integer value) {
            addCriterion("event_weight >=", value, "eventWeight");
            return (Criteria) this;
        }

        public Criteria andEventWeightLessThan(Integer value) {
            addCriterion("event_weight <", value, "eventWeight");
            return (Criteria) this;
        }

        public Criteria andEventWeightLessThanOrEqualTo(Integer value) {
            addCriterion("event_weight <=", value, "eventWeight");
            return (Criteria) this;
        }

        public Criteria andEventWeightIn(List<Integer> values) {
            addCriterion("event_weight in", values, "eventWeight");
            return (Criteria) this;
        }

        public Criteria andEventWeightNotIn(List<Integer> values) {
            addCriterion("event_weight not in", values, "eventWeight");
            return (Criteria) this;
        }

        public Criteria andEventWeightBetween(Integer value1, Integer value2) {
            addCriterion("event_weight between", value1, value2, "eventWeight");
            return (Criteria) this;
        }

        public Criteria andEventWeightNotBetween(Integer value1, Integer value2) {
            addCriterion("event_weight not between", value1, value2, "eventWeight");
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

        public Criteria andScoreDatasourcesIsNull() {
            addCriterion("score_datasources is null");
            return (Criteria) this;
        }

        public Criteria andScoreDatasourcesIsNotNull() {
            addCriterion("score_datasources is not null");
            return (Criteria) this;
        }

        public Criteria andScoreDatasourcesEqualTo(String value) {
            addCriterion("score_datasources =", value, "scoreDatasources");
            return (Criteria) this;
        }

        public Criteria andScoreDatasourcesNotEqualTo(String value) {
            addCriterion("score_datasources <>", value, "scoreDatasources");
            return (Criteria) this;
        }

        public Criteria andScoreDatasourcesGreaterThan(String value) {
            addCriterion("score_datasources >", value, "scoreDatasources");
            return (Criteria) this;
        }

        public Criteria andScoreDatasourcesGreaterThanOrEqualTo(String value) {
            addCriterion("score_datasources >=", value, "scoreDatasources");
            return (Criteria) this;
        }

        public Criteria andScoreDatasourcesLessThan(String value) {
            addCriterion("score_datasources <", value, "scoreDatasources");
            return (Criteria) this;
        }

        public Criteria andScoreDatasourcesLessThanOrEqualTo(String value) {
            addCriterion("score_datasources <=", value, "scoreDatasources");
            return (Criteria) this;
        }

        public Criteria andScoreDatasourcesLike(String value) {
            addCriterion("score_datasources like", value, "scoreDatasources");
            return (Criteria) this;
        }

        public Criteria andScoreDatasourcesNotLike(String value) {
            addCriterion("score_datasources not like", value, "scoreDatasources");
            return (Criteria) this;
        }

        public Criteria andScoreDatasourcesIn(List<String> values) {
            addCriterion("score_datasources in", values, "scoreDatasources");
            return (Criteria) this;
        }

        public Criteria andScoreDatasourcesNotIn(List<String> values) {
            addCriterion("score_datasources not in", values, "scoreDatasources");
            return (Criteria) this;
        }

        public Criteria andScoreDatasourcesBetween(String value1, String value2) {
            addCriterion("score_datasources between", value1, value2, "scoreDatasources");
            return (Criteria) this;
        }

        public Criteria andScoreDatasourcesNotBetween(String value1, String value2) {
            addCriterion("score_datasources not between", value1, value2, "scoreDatasources");
            return (Criteria) this;
        }

        public Criteria andDeletedDataSourceCodeIsNull() {
            addCriterion("deleted_data_source_code is null");
            return (Criteria) this;
        }

        public Criteria andDeletedDataSourceCodeIsNotNull() {
            addCriterion("deleted_data_source_code is not null");
            return (Criteria) this;
        }

        public Criteria andDeletedDataSourceCodeEqualTo(String value) {
            addCriterion("deleted_data_source_code =", value, "deletedDataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDeletedDataSourceCodeNotEqualTo(String value) {
            addCriterion("deleted_data_source_code <>", value, "deletedDataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDeletedDataSourceCodeGreaterThan(String value) {
            addCriterion("deleted_data_source_code >", value, "deletedDataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDeletedDataSourceCodeGreaterThanOrEqualTo(String value) {
            addCriterion("deleted_data_source_code >=", value, "deletedDataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDeletedDataSourceCodeLessThan(String value) {
            addCriterion("deleted_data_source_code <", value, "deletedDataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDeletedDataSourceCodeLessThanOrEqualTo(String value) {
            addCriterion("deleted_data_source_code <=", value, "deletedDataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDeletedDataSourceCodeLike(String value) {
            addCriterion("deleted_data_source_code like", value, "deletedDataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDeletedDataSourceCodeNotLike(String value) {
            addCriterion("deleted_data_source_code not like", value, "deletedDataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDeletedDataSourceCodeIn(List<String> values) {
            addCriterion("deleted_data_source_code in", values, "deletedDataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDeletedDataSourceCodeNotIn(List<String> values) {
            addCriterion("deleted_data_source_code not in", values, "deletedDataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDeletedDataSourceCodeBetween(String value1, String value2) {
            addCriterion("deleted_data_source_code between", value1, value2, "deletedDataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDeletedDataSourceCodeNotBetween(String value1, String value2) {
            addCriterion("deleted_data_source_code not between", value1, value2, "deletedDataSourceCode");
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

        public Criteria andScoreTypeIsNull() {
            addCriterion("score_type is null");
            return (Criteria) this;
        }

        public Criteria andScoreTypeIsNotNull() {
            addCriterion("score_type is not null");
            return (Criteria) this;
        }

        public Criteria andScoreTypeEqualTo(Boolean value) {
            addCriterion("score_type =", value, "scoreType");
            return (Criteria) this;
        }

        public Criteria andScoreTypeNotEqualTo(Boolean value) {
            addCriterion("score_type <>", value, "scoreType");
            return (Criteria) this;
        }

        public Criteria andScoreTypeGreaterThan(Boolean value) {
            addCriterion("score_type >", value, "scoreType");
            return (Criteria) this;
        }

        public Criteria andScoreTypeGreaterThanOrEqualTo(Boolean value) {
            addCriterion("score_type >=", value, "scoreType");
            return (Criteria) this;
        }

        public Criteria andScoreTypeLessThan(Boolean value) {
            addCriterion("score_type <", value, "scoreType");
            return (Criteria) this;
        }

        public Criteria andScoreTypeLessThanOrEqualTo(Boolean value) {
            addCriterion("score_type <=", value, "scoreType");
            return (Criteria) this;
        }

        public Criteria andScoreTypeIn(List<Boolean> values) {
            addCriterion("score_type in", values, "scoreType");
            return (Criteria) this;
        }

        public Criteria andScoreTypeNotIn(List<Boolean> values) {
            addCriterion("score_type not in", values, "scoreType");
            return (Criteria) this;
        }

        public Criteria andScoreTypeBetween(Boolean value1, Boolean value2) {
            addCriterion("score_type between", value1, value2, "scoreType");
            return (Criteria) this;
        }

        public Criteria andScoreTypeNotBetween(Boolean value1, Boolean value2) {
            addCriterion("score_type not between", value1, value2, "scoreType");
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

        public Criteria andConfirmTimeEqualTo(Long value) {
            addCriterion("confirm_time =", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeNotEqualTo(Long value) {
            addCriterion("confirm_time <>", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeGreaterThan(Long value) {
            addCriterion("confirm_time >", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("confirm_time >=", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeLessThan(Long value) {
            addCriterion("confirm_time <", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeLessThanOrEqualTo(Long value) {
            addCriterion("confirm_time <=", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeIn(List<Long> values) {
            addCriterion("confirm_time in", values, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeNotIn(List<Long> values) {
            addCriterion("confirm_time not in", values, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeBetween(Long value1, Long value2) {
            addCriterion("confirm_time between", value1, value2, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeNotBetween(Long value1, Long value2) {
            addCriterion("confirm_time not between", value1, value2, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andEventOrderIsNull() {
            addCriterion("event_order is null");
            return (Criteria) this;
        }

        public Criteria andEventOrderIsNotNull() {
            addCriterion("event_order is not null");
            return (Criteria) this;
        }

        public Criteria andEventOrderEqualTo(Integer value) {
            addCriterion("event_order =", value, "eventOrder");
            return (Criteria) this;
        }

        public Criteria andEventOrderNotEqualTo(Integer value) {
            addCriterion("event_order <>", value, "eventOrder");
            return (Criteria) this;
        }

        public Criteria andEventOrderGreaterThan(Integer value) {
            addCriterion("event_order >", value, "eventOrder");
            return (Criteria) this;
        }

        public Criteria andEventOrderGreaterThanOrEqualTo(Integer value) {
            addCriterion("event_order >=", value, "eventOrder");
            return (Criteria) this;
        }

        public Criteria andEventOrderLessThan(Integer value) {
            addCriterion("event_order <", value, "eventOrder");
            return (Criteria) this;
        }

        public Criteria andEventOrderLessThanOrEqualTo(Integer value) {
            addCriterion("event_order <=", value, "eventOrder");
            return (Criteria) this;
        }

        public Criteria andEventOrderIn(List<Integer> values) {
            addCriterion("event_order in", values, "eventOrder");
            return (Criteria) this;
        }

        public Criteria andEventOrderNotIn(List<Integer> values) {
            addCriterion("event_order not in", values, "eventOrder");
            return (Criteria) this;
        }

        public Criteria andEventOrderBetween(Integer value1, Integer value2) {
            addCriterion("event_order between", value1, value2, "eventOrder");
            return (Criteria) this;
        }

        public Criteria andEventOrderNotBetween(Integer value1, Integer value2) {
            addCriterion("event_order not between", value1, value2, "eventOrder");
            return (Criteria) this;
        }

        public Criteria andAuditOrderNoIsNull() {
            addCriterion("audit_order_no is null");
            return (Criteria) this;
        }

        public Criteria andAuditOrderNoIsNotNull() {
            addCriterion("audit_order_no is not null");
            return (Criteria) this;
        }

        public Criteria andAuditOrderNoEqualTo(Integer value) {
            addCriterion("audit_order_no =", value, "auditOrderNo");
            return (Criteria) this;
        }

        public Criteria andAuditOrderNoNotEqualTo(Integer value) {
            addCriterion("audit_order_no <>", value, "auditOrderNo");
            return (Criteria) this;
        }

        public Criteria andAuditOrderNoGreaterThan(Integer value) {
            addCriterion("audit_order_no >", value, "auditOrderNo");
            return (Criteria) this;
        }

        public Criteria andAuditOrderNoGreaterThanOrEqualTo(Integer value) {
            addCriterion("audit_order_no >=", value, "auditOrderNo");
            return (Criteria) this;
        }

        public Criteria andAuditOrderNoLessThan(Integer value) {
            addCriterion("audit_order_no <", value, "auditOrderNo");
            return (Criteria) this;
        }

        public Criteria andAuditOrderNoLessThanOrEqualTo(Integer value) {
            addCriterion("audit_order_no <=", value, "auditOrderNo");
            return (Criteria) this;
        }

        public Criteria andAuditOrderNoIn(List<Integer> values) {
            addCriterion("audit_order_no in", values, "auditOrderNo");
            return (Criteria) this;
        }

        public Criteria andAuditOrderNoNotIn(List<Integer> values) {
            addCriterion("audit_order_no not in", values, "auditOrderNo");
            return (Criteria) this;
        }

        public Criteria andAuditOrderNoBetween(Integer value1, Integer value2) {
            addCriterion("audit_order_no between", value1, value2, "auditOrderNo");
            return (Criteria) this;
        }

        public Criteria andAuditOrderNoNotBetween(Integer value1, Integer value2) {
            addCriterion("audit_order_no not between", value1, value2, "auditOrderNo");
            return (Criteria) this;
        }

        public Criteria andTemplateNoIsNull() {
            addCriterion("template_no is null");
            return (Criteria) this;
        }

        public Criteria andTemplateNoIsNotNull() {
            addCriterion("template_no is not null");
            return (Criteria) this;
        }

        public Criteria andTemplateNoEqualTo(Integer value) {
            addCriterion("template_no =", value, "templateNo");
            return (Criteria) this;
        }

        public Criteria andTemplateNoNotEqualTo(Integer value) {
            addCriterion("template_no <>", value, "templateNo");
            return (Criteria) this;
        }

        public Criteria andTemplateNoGreaterThan(Integer value) {
            addCriterion("template_no >", value, "templateNo");
            return (Criteria) this;
        }

        public Criteria andTemplateNoGreaterThanOrEqualTo(Integer value) {
            addCriterion("template_no >=", value, "templateNo");
            return (Criteria) this;
        }

        public Criteria andTemplateNoLessThan(Integer value) {
            addCriterion("template_no <", value, "templateNo");
            return (Criteria) this;
        }

        public Criteria andTemplateNoLessThanOrEqualTo(Integer value) {
            addCriterion("template_no <=", value, "templateNo");
            return (Criteria) this;
        }

        public Criteria andTemplateNoIn(List<Integer> values) {
            addCriterion("template_no in", values, "templateNo");
            return (Criteria) this;
        }

        public Criteria andTemplateNoNotIn(List<Integer> values) {
            addCriterion("template_no not in", values, "templateNo");
            return (Criteria) this;
        }

        public Criteria andTemplateNoBetween(Integer value1, Integer value2) {
            addCriterion("template_no between", value1, value2, "templateNo");
            return (Criteria) this;
        }

        public Criteria andTemplateNoNotBetween(Integer value1, Integer value2) {
            addCriterion("template_no not between", value1, value2, "templateNo");
            return (Criteria) this;
        }

        public Criteria andAuditTypeIsNull() {
            addCriterion("audit_type is null");
            return (Criteria) this;
        }

        public Criteria andAuditTypeIsNotNull() {
            addCriterion("audit_type is not null");
            return (Criteria) this;
        }

        public Criteria andAuditTypeEqualTo(Boolean value) {
            addCriterion("audit_type =", value, "auditType");
            return (Criteria) this;
        }

        public Criteria andAuditTypeNotEqualTo(Boolean value) {
            addCriterion("audit_type <>", value, "auditType");
            return (Criteria) this;
        }

        public Criteria andAuditTypeGreaterThan(Boolean value) {
            addCriterion("audit_type >", value, "auditType");
            return (Criteria) this;
        }

        public Criteria andAuditTypeGreaterThanOrEqualTo(Boolean value) {
            addCriterion("audit_type >=", value, "auditType");
            return (Criteria) this;
        }

        public Criteria andAuditTypeLessThan(Boolean value) {
            addCriterion("audit_type <", value, "auditType");
            return (Criteria) this;
        }

        public Criteria andAuditTypeLessThanOrEqualTo(Boolean value) {
            addCriterion("audit_type <=", value, "auditType");
            return (Criteria) this;
        }

        public Criteria andAuditTypeIn(List<Boolean> values) {
            addCriterion("audit_type in", values, "auditType");
            return (Criteria) this;
        }

        public Criteria andAuditTypeNotIn(List<Boolean> values) {
            addCriterion("audit_type not in", values, "auditType");
            return (Criteria) this;
        }

        public Criteria andAuditTypeBetween(Boolean value1, Boolean value2) {
            addCriterion("audit_type between", value1, value2, "auditType");
            return (Criteria) this;
        }

        public Criteria andAuditTypeNotBetween(Boolean value1, Boolean value2) {
            addCriterion("audit_type not between", value1, value2, "auditType");
            return (Criteria) this;
        }

        public Criteria andTemplateIdIsNull() {
            addCriterion("template_id is null");
            return (Criteria) this;
        }

        public Criteria andTemplateIdIsNotNull() {
            addCriterion("template_id is not null");
            return (Criteria) this;
        }

        public Criteria andTemplateIdEqualTo(String value) {
            addCriterion("template_id =", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdNotEqualTo(String value) {
            addCriterion("template_id <>", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdGreaterThan(String value) {
            addCriterion("template_id >", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdGreaterThanOrEqualTo(String value) {
            addCriterion("template_id >=", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdLessThan(String value) {
            addCriterion("template_id <", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdLessThanOrEqualTo(String value) {
            addCriterion("template_id <=", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdLike(String value) {
            addCriterion("template_id like", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdNotLike(String value) {
            addCriterion("template_id not like", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdIn(List<String> values) {
            addCriterion("template_id in", values, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdNotIn(List<String> values) {
            addCriterion("template_id not in", values, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdBetween(String value1, String value2) {
            addCriterion("template_id between", value1, value2, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdNotBetween(String value1, String value2) {
            addCriterion("template_id not between", value1, value2, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateTextIsNull() {
            addCriterion("template_text is null");
            return (Criteria) this;
        }

        public Criteria andTemplateTextIsNotNull() {
            addCriterion("template_text is not null");
            return (Criteria) this;
        }

        public Criteria andTemplateTextEqualTo(String value) {
            addCriterion("template_text =", value, "templateText");
            return (Criteria) this;
        }

        public Criteria andTemplateTextNotEqualTo(String value) {
            addCriterion("template_text <>", value, "templateText");
            return (Criteria) this;
        }

        public Criteria andTemplateTextGreaterThan(String value) {
            addCriterion("template_text >", value, "templateText");
            return (Criteria) this;
        }

        public Criteria andTemplateTextGreaterThanOrEqualTo(String value) {
            addCriterion("template_text >=", value, "templateText");
            return (Criteria) this;
        }

        public Criteria andTemplateTextLessThan(String value) {
            addCriterion("template_text <", value, "templateText");
            return (Criteria) this;
        }

        public Criteria andTemplateTextLessThanOrEqualTo(String value) {
            addCriterion("template_text <=", value, "templateText");
            return (Criteria) this;
        }

        public Criteria andTemplateTextLike(String value) {
            addCriterion("template_text like", value, "templateText");
            return (Criteria) this;
        }

        public Criteria andTemplateTextNotLike(String value) {
            addCriterion("template_text not like", value, "templateText");
            return (Criteria) this;
        }

        public Criteria andTemplateTextIn(List<String> values) {
            addCriterion("template_text in", values, "templateText");
            return (Criteria) this;
        }

        public Criteria andTemplateTextNotIn(List<String> values) {
            addCriterion("template_text not in", values, "templateText");
            return (Criteria) this;
        }

        public Criteria andTemplateTextBetween(String value1, String value2) {
            addCriterion("template_text between", value1, value2, "templateText");
            return (Criteria) this;
        }

        public Criteria andTemplateTextNotBetween(String value1, String value2) {
            addCriterion("template_text not between", value1, value2, "templateText");
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