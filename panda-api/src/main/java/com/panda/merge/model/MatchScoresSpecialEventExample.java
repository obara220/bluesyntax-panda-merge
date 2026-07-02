package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class MatchScoresSpecialEventExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MatchScoresSpecialEventExample() {
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

        public Criteria andSourceEventIdIsNull() {
            addCriterion("source_event_id is null");
            return (Criteria) this;
        }

        public Criteria andSourceEventIdIsNotNull() {
            addCriterion("source_event_id is not null");
            return (Criteria) this;
        }

        public Criteria andSourceEventIdEqualTo(String value) {
            addCriterion("source_event_id =", value, "sourceEventId");
            return (Criteria) this;
        }

        public Criteria andSourceEventIdNotEqualTo(String value) {
            addCriterion("source_event_id <>", value, "sourceEventId");
            return (Criteria) this;
        }

        public Criteria andSourceEventIdGreaterThan(String value) {
            addCriterion("source_event_id >", value, "sourceEventId");
            return (Criteria) this;
        }

        public Criteria andSourceEventIdGreaterThanOrEqualTo(String value) {
            addCriterion("source_event_id >=", value, "sourceEventId");
            return (Criteria) this;
        }

        public Criteria andSourceEventIdLessThan(String value) {
            addCriterion("source_event_id <", value, "sourceEventId");
            return (Criteria) this;
        }

        public Criteria andSourceEventIdLessThanOrEqualTo(String value) {
            addCriterion("source_event_id <=", value, "sourceEventId");
            return (Criteria) this;
        }

        public Criteria andSourceEventIdLike(String value) {
            addCriterion("source_event_id like", value, "sourceEventId");
            return (Criteria) this;
        }

        public Criteria andSourceEventIdNotLike(String value) {
            addCriterion("source_event_id not like", value, "sourceEventId");
            return (Criteria) this;
        }

        public Criteria andSourceEventIdIn(List<String> values) {
            addCriterion("source_event_id in", values, "sourceEventId");
            return (Criteria) this;
        }

        public Criteria andSourceEventIdNotIn(List<String> values) {
            addCriterion("source_event_id not in", values, "sourceEventId");
            return (Criteria) this;
        }

        public Criteria andSourceEventIdBetween(String value1, String value2) {
            addCriterion("source_event_id between", value1, value2, "sourceEventId");
            return (Criteria) this;
        }

        public Criteria andSourceEventIdNotBetween(String value1, String value2) {
            addCriterion("source_event_id not between", value1, value2, "sourceEventId");
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

        public Criteria andDataSourceEventIdIsNull() {
            addCriterion("data_source_event_id is null");
            return (Criteria) this;
        }

        public Criteria andDataSourceEventIdIsNotNull() {
            addCriterion("data_source_event_id is not null");
            return (Criteria) this;
        }

        public Criteria andDataSourceEventIdEqualTo(String value) {
            addCriterion("data_source_event_id =", value, "dataSourceEventId");
            return (Criteria) this;
        }

        public Criteria andDataSourceEventIdNotEqualTo(String value) {
            addCriterion("data_source_event_id <>", value, "dataSourceEventId");
            return (Criteria) this;
        }

        public Criteria andDataSourceEventIdGreaterThan(String value) {
            addCriterion("data_source_event_id >", value, "dataSourceEventId");
            return (Criteria) this;
        }

        public Criteria andDataSourceEventIdGreaterThanOrEqualTo(String value) {
            addCriterion("data_source_event_id >=", value, "dataSourceEventId");
            return (Criteria) this;
        }

        public Criteria andDataSourceEventIdLessThan(String value) {
            addCriterion("data_source_event_id <", value, "dataSourceEventId");
            return (Criteria) this;
        }

        public Criteria andDataSourceEventIdLessThanOrEqualTo(String value) {
            addCriterion("data_source_event_id <=", value, "dataSourceEventId");
            return (Criteria) this;
        }

        public Criteria andDataSourceEventIdLike(String value) {
            addCriterion("data_source_event_id like", value, "dataSourceEventId");
            return (Criteria) this;
        }

        public Criteria andDataSourceEventIdNotLike(String value) {
            addCriterion("data_source_event_id not like", value, "dataSourceEventId");
            return (Criteria) this;
        }

        public Criteria andDataSourceEventIdIn(List<String> values) {
            addCriterion("data_source_event_id in", values, "dataSourceEventId");
            return (Criteria) this;
        }

        public Criteria andDataSourceEventIdNotIn(List<String> values) {
            addCriterion("data_source_event_id not in", values, "dataSourceEventId");
            return (Criteria) this;
        }

        public Criteria andDataSourceEventIdBetween(String value1, String value2) {
            addCriterion("data_source_event_id between", value1, value2, "dataSourceEventId");
            return (Criteria) this;
        }

        public Criteria andDataSourceEventIdNotBetween(String value1, String value2) {
            addCriterion("data_source_event_id not between", value1, value2, "dataSourceEventId");
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

        public Criteria andPandaEventCodeIsNull() {
            addCriterion("panda_event_code is null");
            return (Criteria) this;
        }

        public Criteria andPandaEventCodeIsNotNull() {
            addCriterion("panda_event_code is not null");
            return (Criteria) this;
        }

        public Criteria andPandaEventCodeEqualTo(String value) {
            addCriterion("panda_event_code =", value, "pandaEventCode");
            return (Criteria) this;
        }

        public Criteria andPandaEventCodeNotEqualTo(String value) {
            addCriterion("panda_event_code <>", value, "pandaEventCode");
            return (Criteria) this;
        }

        public Criteria andPandaEventCodeGreaterThan(String value) {
            addCriterion("panda_event_code >", value, "pandaEventCode");
            return (Criteria) this;
        }

        public Criteria andPandaEventCodeGreaterThanOrEqualTo(String value) {
            addCriterion("panda_event_code >=", value, "pandaEventCode");
            return (Criteria) this;
        }

        public Criteria andPandaEventCodeLessThan(String value) {
            addCriterion("panda_event_code <", value, "pandaEventCode");
            return (Criteria) this;
        }

        public Criteria andPandaEventCodeLessThanOrEqualTo(String value) {
            addCriterion("panda_event_code <=", value, "pandaEventCode");
            return (Criteria) this;
        }

        public Criteria andPandaEventCodeLike(String value) {
            addCriterion("panda_event_code like", value, "pandaEventCode");
            return (Criteria) this;
        }

        public Criteria andPandaEventCodeNotLike(String value) {
            addCriterion("panda_event_code not like", value, "pandaEventCode");
            return (Criteria) this;
        }

        public Criteria andPandaEventCodeIn(List<String> values) {
            addCriterion("panda_event_code in", values, "pandaEventCode");
            return (Criteria) this;
        }

        public Criteria andPandaEventCodeNotIn(List<String> values) {
            addCriterion("panda_event_code not in", values, "pandaEventCode");
            return (Criteria) this;
        }

        public Criteria andPandaEventCodeBetween(String value1, String value2) {
            addCriterion("panda_event_code between", value1, value2, "pandaEventCode");
            return (Criteria) this;
        }

        public Criteria andPandaEventCodeNotBetween(String value1, String value2) {
            addCriterion("panda_event_code not between", value1, value2, "pandaEventCode");
            return (Criteria) this;
        }

        public Criteria andHomeawayIsNull() {
            addCriterion("homeaway is null");
            return (Criteria) this;
        }

        public Criteria andHomeawayIsNotNull() {
            addCriterion("homeaway is not null");
            return (Criteria) this;
        }

        public Criteria andHomeawayEqualTo(String value) {
            addCriterion("homeaway =", value, "homeaway");
            return (Criteria) this;
        }

        public Criteria andHomeawayNotEqualTo(String value) {
            addCriterion("homeaway <>", value, "homeaway");
            return (Criteria) this;
        }

        public Criteria andHomeawayGreaterThan(String value) {
            addCriterion("homeaway >", value, "homeaway");
            return (Criteria) this;
        }

        public Criteria andHomeawayGreaterThanOrEqualTo(String value) {
            addCriterion("homeaway >=", value, "homeaway");
            return (Criteria) this;
        }

        public Criteria andHomeawayLessThan(String value) {
            addCriterion("homeaway <", value, "homeaway");
            return (Criteria) this;
        }

        public Criteria andHomeawayLessThanOrEqualTo(String value) {
            addCriterion("homeaway <=", value, "homeaway");
            return (Criteria) this;
        }

        public Criteria andHomeawayLike(String value) {
            addCriterion("homeaway like", value, "homeaway");
            return (Criteria) this;
        }

        public Criteria andHomeawayNotLike(String value) {
            addCriterion("homeaway not like", value, "homeaway");
            return (Criteria) this;
        }

        public Criteria andHomeawayIn(List<String> values) {
            addCriterion("homeaway in", values, "homeaway");
            return (Criteria) this;
        }

        public Criteria andHomeawayNotIn(List<String> values) {
            addCriterion("homeaway not in", values, "homeaway");
            return (Criteria) this;
        }

        public Criteria andHomeawayBetween(String value1, String value2) {
            addCriterion("homeaway between", value1, value2, "homeaway");
            return (Criteria) this;
        }

        public Criteria andHomeawayNotBetween(String value1, String value2) {
            addCriterion("homeaway not between", value1, value2, "homeaway");
            return (Criteria) this;
        }

        public Criteria andT1IsNull() {
            addCriterion("T1 is null");
            return (Criteria) this;
        }

        public Criteria andT1IsNotNull() {
            addCriterion("T1 is not null");
            return (Criteria) this;
        }

        public Criteria andT1EqualTo(Integer value) {
            addCriterion("T1 =", value, "t1");
            return (Criteria) this;
        }

        public Criteria andT1NotEqualTo(Integer value) {
            addCriterion("T1 <>", value, "t1");
            return (Criteria) this;
        }

        public Criteria andT1GreaterThan(Integer value) {
            addCriterion("T1 >", value, "t1");
            return (Criteria) this;
        }

        public Criteria andT1GreaterThanOrEqualTo(Integer value) {
            addCriterion("T1 >=", value, "t1");
            return (Criteria) this;
        }

        public Criteria andT1LessThan(Integer value) {
            addCriterion("T1 <", value, "t1");
            return (Criteria) this;
        }

        public Criteria andT1LessThanOrEqualTo(Integer value) {
            addCriterion("T1 <=", value, "t1");
            return (Criteria) this;
        }

        public Criteria andT1In(List<Integer> values) {
            addCriterion("T1 in", values, "t1");
            return (Criteria) this;
        }

        public Criteria andT1NotIn(List<Integer> values) {
            addCriterion("T1 not in", values, "t1");
            return (Criteria) this;
        }

        public Criteria andT1Between(Integer value1, Integer value2) {
            addCriterion("T1 between", value1, value2, "t1");
            return (Criteria) this;
        }

        public Criteria andT1NotBetween(Integer value1, Integer value2) {
            addCriterion("T1 not between", value1, value2, "t1");
            return (Criteria) this;
        }

        public Criteria andT2IsNull() {
            addCriterion("T2 is null");
            return (Criteria) this;
        }

        public Criteria andT2IsNotNull() {
            addCriterion("T2 is not null");
            return (Criteria) this;
        }

        public Criteria andT2EqualTo(Integer value) {
            addCriterion("T2 =", value, "t2");
            return (Criteria) this;
        }

        public Criteria andT2NotEqualTo(Integer value) {
            addCriterion("T2 <>", value, "t2");
            return (Criteria) this;
        }

        public Criteria andT2GreaterThan(Integer value) {
            addCriterion("T2 >", value, "t2");
            return (Criteria) this;
        }

        public Criteria andT2GreaterThanOrEqualTo(Integer value) {
            addCriterion("T2 >=", value, "t2");
            return (Criteria) this;
        }

        public Criteria andT2LessThan(Integer value) {
            addCriterion("T2 <", value, "t2");
            return (Criteria) this;
        }

        public Criteria andT2LessThanOrEqualTo(Integer value) {
            addCriterion("T2 <=", value, "t2");
            return (Criteria) this;
        }

        public Criteria andT2In(List<Integer> values) {
            addCriterion("T2 in", values, "t2");
            return (Criteria) this;
        }

        public Criteria andT2NotIn(List<Integer> values) {
            addCriterion("T2 not in", values, "t2");
            return (Criteria) this;
        }

        public Criteria andT2Between(Integer value1, Integer value2) {
            addCriterion("T2 between", value1, value2, "t2");
            return (Criteria) this;
        }

        public Criteria andT2NotBetween(Integer value1, Integer value2) {
            addCriterion("T2 not between", value1, value2, "t2");
            return (Criteria) this;
        }

        public Criteria andSecondT1IsNull() {
            addCriterion("second_T1 is null");
            return (Criteria) this;
        }

        public Criteria andSecondT1IsNotNull() {
            addCriterion("second_T1 is not null");
            return (Criteria) this;
        }

        public Criteria andSecondT1EqualTo(Integer value) {
            addCriterion("second_T1 =", value, "secondT1");
            return (Criteria) this;
        }

        public Criteria andSecondT1NotEqualTo(Integer value) {
            addCriterion("second_T1 <>", value, "secondT1");
            return (Criteria) this;
        }

        public Criteria andSecondT1GreaterThan(Integer value) {
            addCriterion("second_T1 >", value, "secondT1");
            return (Criteria) this;
        }

        public Criteria andSecondT1GreaterThanOrEqualTo(Integer value) {
            addCriterion("second_T1 >=", value, "secondT1");
            return (Criteria) this;
        }

        public Criteria andSecondT1LessThan(Integer value) {
            addCriterion("second_T1 <", value, "secondT1");
            return (Criteria) this;
        }

        public Criteria andSecondT1LessThanOrEqualTo(Integer value) {
            addCriterion("second_T1 <=", value, "secondT1");
            return (Criteria) this;
        }

        public Criteria andSecondT1In(List<Integer> values) {
            addCriterion("second_T1 in", values, "secondT1");
            return (Criteria) this;
        }

        public Criteria andSecondT1NotIn(List<Integer> values) {
            addCriterion("second_T1 not in", values, "secondT1");
            return (Criteria) this;
        }

        public Criteria andSecondT1Between(Integer value1, Integer value2) {
            addCriterion("second_T1 between", value1, value2, "secondT1");
            return (Criteria) this;
        }

        public Criteria andSecondT1NotBetween(Integer value1, Integer value2) {
            addCriterion("second_T1 not between", value1, value2, "secondT1");
            return (Criteria) this;
        }

        public Criteria andSecondT2IsNull() {
            addCriterion("second_T2 is null");
            return (Criteria) this;
        }

        public Criteria andSecondT2IsNotNull() {
            addCriterion("second_T2 is not null");
            return (Criteria) this;
        }

        public Criteria andSecondT2EqualTo(Integer value) {
            addCriterion("second_T2 =", value, "secondT2");
            return (Criteria) this;
        }

        public Criteria andSecondT2NotEqualTo(Integer value) {
            addCriterion("second_T2 <>", value, "secondT2");
            return (Criteria) this;
        }

        public Criteria andSecondT2GreaterThan(Integer value) {
            addCriterion("second_T2 >", value, "secondT2");
            return (Criteria) this;
        }

        public Criteria andSecondT2GreaterThanOrEqualTo(Integer value) {
            addCriterion("second_T2 >=", value, "secondT2");
            return (Criteria) this;
        }

        public Criteria andSecondT2LessThan(Integer value) {
            addCriterion("second_T2 <", value, "secondT2");
            return (Criteria) this;
        }

        public Criteria andSecondT2LessThanOrEqualTo(Integer value) {
            addCriterion("second_T2 <=", value, "secondT2");
            return (Criteria) this;
        }

        public Criteria andSecondT2In(List<Integer> values) {
            addCriterion("second_T2 in", values, "secondT2");
            return (Criteria) this;
        }

        public Criteria andSecondT2NotIn(List<Integer> values) {
            addCriterion("second_T2 not in", values, "secondT2");
            return (Criteria) this;
        }

        public Criteria andSecondT2Between(Integer value1, Integer value2) {
            addCriterion("second_T2 between", value1, value2, "secondT2");
            return (Criteria) this;
        }

        public Criteria andSecondT2NotBetween(Integer value1, Integer value2) {
            addCriterion("second_T2 not between", value1, value2, "secondT2");
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

        public Criteria andExtrainfoIsNull() {
            addCriterion("extrainfo is null");
            return (Criteria) this;
        }

        public Criteria andExtrainfoIsNotNull() {
            addCriterion("extrainfo is not null");
            return (Criteria) this;
        }

        public Criteria andExtrainfoEqualTo(String value) {
            addCriterion("extrainfo =", value, "extrainfo");
            return (Criteria) this;
        }

        public Criteria andExtrainfoNotEqualTo(String value) {
            addCriterion("extrainfo <>", value, "extrainfo");
            return (Criteria) this;
        }

        public Criteria andExtrainfoGreaterThan(String value) {
            addCriterion("extrainfo >", value, "extrainfo");
            return (Criteria) this;
        }

        public Criteria andExtrainfoGreaterThanOrEqualTo(String value) {
            addCriterion("extrainfo >=", value, "extrainfo");
            return (Criteria) this;
        }

        public Criteria andExtrainfoLessThan(String value) {
            addCriterion("extrainfo <", value, "extrainfo");
            return (Criteria) this;
        }

        public Criteria andExtrainfoLessThanOrEqualTo(String value) {
            addCriterion("extrainfo <=", value, "extrainfo");
            return (Criteria) this;
        }

        public Criteria andExtrainfoLike(String value) {
            addCriterion("extrainfo like", value, "extrainfo");
            return (Criteria) this;
        }

        public Criteria andExtrainfoNotLike(String value) {
            addCriterion("extrainfo not like", value, "extrainfo");
            return (Criteria) this;
        }

        public Criteria andExtrainfoIn(List<String> values) {
            addCriterion("extrainfo in", values, "extrainfo");
            return (Criteria) this;
        }

        public Criteria andExtrainfoNotIn(List<String> values) {
            addCriterion("extrainfo not in", values, "extrainfo");
            return (Criteria) this;
        }

        public Criteria andExtrainfoBetween(String value1, String value2) {
            addCriterion("extrainfo between", value1, value2, "extrainfo");
            return (Criteria) this;
        }

        public Criteria andExtrainfoNotBetween(String value1, String value2) {
            addCriterion("extrainfo not between", value1, value2, "extrainfo");
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