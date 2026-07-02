package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class MatchSettleThirdBasketScoreExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MatchSettleThirdBasketScoreExample() {
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

        public Criteria andSumScoreIsNull() {
            addCriterion("sum_score is null");
            return (Criteria) this;
        }

        public Criteria andSumScoreIsNotNull() {
            addCriterion("sum_score is not null");
            return (Criteria) this;
        }

        public Criteria andSumScoreEqualTo(Integer value) {
            addCriterion("sum_score =", value, "sumScore");
            return (Criteria) this;
        }

        public Criteria andSumScoreNotEqualTo(Integer value) {
            addCriterion("sum_score <>", value, "sumScore");
            return (Criteria) this;
        }

        public Criteria andSumScoreGreaterThan(Integer value) {
            addCriterion("sum_score >", value, "sumScore");
            return (Criteria) this;
        }

        public Criteria andSumScoreGreaterThanOrEqualTo(Integer value) {
            addCriterion("sum_score >=", value, "sumScore");
            return (Criteria) this;
        }

        public Criteria andSumScoreLessThan(Integer value) {
            addCriterion("sum_score <", value, "sumScore");
            return (Criteria) this;
        }

        public Criteria andSumScoreLessThanOrEqualTo(Integer value) {
            addCriterion("sum_score <=", value, "sumScore");
            return (Criteria) this;
        }

        public Criteria andSumScoreIn(List<Integer> values) {
            addCriterion("sum_score in", values, "sumScore");
            return (Criteria) this;
        }

        public Criteria andSumScoreNotIn(List<Integer> values) {
            addCriterion("sum_score not in", values, "sumScore");
            return (Criteria) this;
        }

        public Criteria andSumScoreBetween(Integer value1, Integer value2) {
            addCriterion("sum_score between", value1, value2, "sumScore");
            return (Criteria) this;
        }

        public Criteria andSumScoreNotBetween(Integer value1, Integer value2) {
            addCriterion("sum_score not between", value1, value2, "sumScore");
            return (Criteria) this;
        }

        public Criteria andSettleSumScoreIsNull() {
            addCriterion("settle_sum_score is null");
            return (Criteria) this;
        }

        public Criteria andSettleSumScoreIsNotNull() {
            addCriterion("settle_sum_score is not null");
            return (Criteria) this;
        }

        public Criteria andSettleSumScoreEqualTo(Integer value) {
            addCriterion("settle_sum_score =", value, "settleSumScore");
            return (Criteria) this;
        }

        public Criteria andSettleSumScoreNotEqualTo(Integer value) {
            addCriterion("settle_sum_score <>", value, "settleSumScore");
            return (Criteria) this;
        }

        public Criteria andSettleSumScoreGreaterThan(Integer value) {
            addCriterion("settle_sum_score >", value, "settleSumScore");
            return (Criteria) this;
        }

        public Criteria andSettleSumScoreGreaterThanOrEqualTo(Integer value) {
            addCriterion("settle_sum_score >=", value, "settleSumScore");
            return (Criteria) this;
        }

        public Criteria andSettleSumScoreLessThan(Integer value) {
            addCriterion("settle_sum_score <", value, "settleSumScore");
            return (Criteria) this;
        }

        public Criteria andSettleSumScoreLessThanOrEqualTo(Integer value) {
            addCriterion("settle_sum_score <=", value, "settleSumScore");
            return (Criteria) this;
        }

        public Criteria andSettleSumScoreIn(List<Integer> values) {
            addCriterion("settle_sum_score in", values, "settleSumScore");
            return (Criteria) this;
        }

        public Criteria andSettleSumScoreNotIn(List<Integer> values) {
            addCriterion("settle_sum_score not in", values, "settleSumScore");
            return (Criteria) this;
        }

        public Criteria andSettleSumScoreBetween(Integer value1, Integer value2) {
            addCriterion("settle_sum_score between", value1, value2, "settleSumScore");
            return (Criteria) this;
        }

        public Criteria andSettleSumScoreNotBetween(Integer value1, Integer value2) {
            addCriterion("settle_sum_score not between", value1, value2, "settleSumScore");
            return (Criteria) this;
        }

        public Criteria andPeriodIdIsNull() {
            addCriterion("period_id is null");
            return (Criteria) this;
        }

        public Criteria andPeriodIdIsNotNull() {
            addCriterion("period_id is not null");
            return (Criteria) this;
        }

        public Criteria andPeriodIdEqualTo(Long value) {
            addCriterion("period_id =", value, "periodId");
            return (Criteria) this;
        }

        public Criteria andPeriodIdNotEqualTo(Long value) {
            addCriterion("period_id <>", value, "periodId");
            return (Criteria) this;
        }

        public Criteria andPeriodIdGreaterThan(Long value) {
            addCriterion("period_id >", value, "periodId");
            return (Criteria) this;
        }

        public Criteria andPeriodIdGreaterThanOrEqualTo(Long value) {
            addCriterion("period_id >=", value, "periodId");
            return (Criteria) this;
        }

        public Criteria andPeriodIdLessThan(Long value) {
            addCriterion("period_id <", value, "periodId");
            return (Criteria) this;
        }

        public Criteria andPeriodIdLessThanOrEqualTo(Long value) {
            addCriterion("period_id <=", value, "periodId");
            return (Criteria) this;
        }

        public Criteria andPeriodIdIn(List<Long> values) {
            addCriterion("period_id in", values, "periodId");
            return (Criteria) this;
        }

        public Criteria andPeriodIdNotIn(List<Long> values) {
            addCriterion("period_id not in", values, "periodId");
            return (Criteria) this;
        }

        public Criteria andPeriodIdBetween(Long value1, Long value2) {
            addCriterion("period_id between", value1, value2, "periodId");
            return (Criteria) this;
        }

        public Criteria andPeriodIdNotBetween(Long value1, Long value2) {
            addCriterion("period_id not between", value1, value2, "periodId");
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

        public Criteria andSecondFromStartEqualTo(Integer value) {
            addCriterion("second_from_start =", value, "secondFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondFromStartNotEqualTo(Integer value) {
            addCriterion("second_from_start <>", value, "secondFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondFromStartGreaterThan(Integer value) {
            addCriterion("second_from_start >", value, "secondFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondFromStartGreaterThanOrEqualTo(Integer value) {
            addCriterion("second_from_start >=", value, "secondFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondFromStartLessThan(Integer value) {
            addCriterion("second_from_start <", value, "secondFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondFromStartLessThanOrEqualTo(Integer value) {
            addCriterion("second_from_start <=", value, "secondFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondFromStartIn(List<Integer> values) {
            addCriterion("second_from_start in", values, "secondFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondFromStartNotIn(List<Integer> values) {
            addCriterion("second_from_start not in", values, "secondFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondFromStartBetween(Integer value1, Integer value2) {
            addCriterion("second_from_start between", value1, value2, "secondFromStart");
            return (Criteria) this;
        }

        public Criteria andSecondFromStartNotBetween(Integer value1, Integer value2) {
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