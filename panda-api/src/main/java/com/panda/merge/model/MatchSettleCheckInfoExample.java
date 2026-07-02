package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class MatchSettleCheckInfoExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MatchSettleCheckInfoExample() {
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

        public Criteria andSettleScoreEventIdIsNull() {
            addCriterion("settle_score_event_id is null");
            return (Criteria) this;
        }

        public Criteria andSettleScoreEventIdIsNotNull() {
            addCriterion("settle_score_event_id is not null");
            return (Criteria) this;
        }

        public Criteria andSettleScoreEventIdEqualTo(Long value) {
            addCriterion("settle_score_event_id =", value, "settleScoreEventId");
            return (Criteria) this;
        }

        public Criteria andSettleScoreEventIdNotEqualTo(Long value) {
            addCriterion("settle_score_event_id <>", value, "settleScoreEventId");
            return (Criteria) this;
        }

        public Criteria andSettleScoreEventIdGreaterThan(Long value) {
            addCriterion("settle_score_event_id >", value, "settleScoreEventId");
            return (Criteria) this;
        }

        public Criteria andSettleScoreEventIdGreaterThanOrEqualTo(Long value) {
            addCriterion("settle_score_event_id >=", value, "settleScoreEventId");
            return (Criteria) this;
        }

        public Criteria andSettleScoreEventIdLessThan(Long value) {
            addCriterion("settle_score_event_id <", value, "settleScoreEventId");
            return (Criteria) this;
        }

        public Criteria andSettleScoreEventIdLessThanOrEqualTo(Long value) {
            addCriterion("settle_score_event_id <=", value, "settleScoreEventId");
            return (Criteria) this;
        }

        public Criteria andSettleScoreEventIdIn(List<Long> values) {
            addCriterion("settle_score_event_id in", values, "settleScoreEventId");
            return (Criteria) this;
        }

        public Criteria andSettleScoreEventIdNotIn(List<Long> values) {
            addCriterion("settle_score_event_id not in", values, "settleScoreEventId");
            return (Criteria) this;
        }

        public Criteria andSettleScoreEventIdBetween(Long value1, Long value2) {
            addCriterion("settle_score_event_id between", value1, value2, "settleScoreEventId");
            return (Criteria) this;
        }

        public Criteria andSettleScoreEventIdNotBetween(Long value1, Long value2) {
            addCriterion("settle_score_event_id not between", value1, value2, "settleScoreEventId");
            return (Criteria) this;
        }

        public Criteria andCheckStatusIsNull() {
            addCriterion("check_status is null");
            return (Criteria) this;
        }

        public Criteria andCheckStatusIsNotNull() {
            addCriterion("check_status is not null");
            return (Criteria) this;
        }

        public Criteria andCheckStatusEqualTo(Integer value) {
            addCriterion("check_status =", value, "checkStatus");
            return (Criteria) this;
        }

        public Criteria andCheckStatusNotEqualTo(Integer value) {
            addCriterion("check_status <>", value, "checkStatus");
            return (Criteria) this;
        }

        public Criteria andCheckStatusGreaterThan(Integer value) {
            addCriterion("check_status >", value, "checkStatus");
            return (Criteria) this;
        }

        public Criteria andCheckStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("check_status >=", value, "checkStatus");
            return (Criteria) this;
        }

        public Criteria andCheckStatusLessThan(Integer value) {
            addCriterion("check_status <", value, "checkStatus");
            return (Criteria) this;
        }

        public Criteria andCheckStatusLessThanOrEqualTo(Integer value) {
            addCriterion("check_status <=", value, "checkStatus");
            return (Criteria) this;
        }

        public Criteria andCheckStatusIn(List<Integer> values) {
            addCriterion("check_status in", values, "checkStatus");
            return (Criteria) this;
        }

        public Criteria andCheckStatusNotIn(List<Integer> values) {
            addCriterion("check_status not in", values, "checkStatus");
            return (Criteria) this;
        }

        public Criteria andCheckStatusBetween(Integer value1, Integer value2) {
            addCriterion("check_status between", value1, value2, "checkStatus");
            return (Criteria) this;
        }

        public Criteria andCheckStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("check_status not between", value1, value2, "checkStatus");
            return (Criteria) this;
        }

        public Criteria andUserNameIsNull() {
            addCriterion("user_name is null");
            return (Criteria) this;
        }

        public Criteria andUserNameIsNotNull() {
            addCriterion("user_name is not null");
            return (Criteria) this;
        }

        public Criteria andUserNameEqualTo(String value) {
            addCriterion("user_name =", value, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameNotEqualTo(String value) {
            addCriterion("user_name <>", value, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameGreaterThan(String value) {
            addCriterion("user_name >", value, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameGreaterThanOrEqualTo(String value) {
            addCriterion("user_name >=", value, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameLessThan(String value) {
            addCriterion("user_name <", value, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameLessThanOrEqualTo(String value) {
            addCriterion("user_name <=", value, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameLike(String value) {
            addCriterion("user_name like", value, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameNotLike(String value) {
            addCriterion("user_name not like", value, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameIn(List<String> values) {
            addCriterion("user_name in", values, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameNotIn(List<String> values) {
            addCriterion("user_name not in", values, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameBetween(String value1, String value2) {
            addCriterion("user_name between", value1, value2, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameNotBetween(String value1, String value2) {
            addCriterion("user_name not between", value1, value2, "userName");
            return (Criteria) this;
        }

        public Criteria andCheckDataTypeIsNull() {
            addCriterion("check_data_type is null");
            return (Criteria) this;
        }

        public Criteria andCheckDataTypeIsNotNull() {
            addCriterion("check_data_type is not null");
            return (Criteria) this;
        }

        public Criteria andCheckDataTypeEqualTo(Integer value) {
            addCriterion("check_data_type =", value, "checkDataType");
            return (Criteria) this;
        }

        public Criteria andCheckDataTypeNotEqualTo(Integer value) {
            addCriterion("check_data_type <>", value, "checkDataType");
            return (Criteria) this;
        }

        public Criteria andCheckDataTypeGreaterThan(Integer value) {
            addCriterion("check_data_type >", value, "checkDataType");
            return (Criteria) this;
        }

        public Criteria andCheckDataTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("check_data_type >=", value, "checkDataType");
            return (Criteria) this;
        }

        public Criteria andCheckDataTypeLessThan(Integer value) {
            addCriterion("check_data_type <", value, "checkDataType");
            return (Criteria) this;
        }

        public Criteria andCheckDataTypeLessThanOrEqualTo(Integer value) {
            addCriterion("check_data_type <=", value, "checkDataType");
            return (Criteria) this;
        }

        public Criteria andCheckDataTypeIn(List<Integer> values) {
            addCriterion("check_data_type in", values, "checkDataType");
            return (Criteria) this;
        }

        public Criteria andCheckDataTypeNotIn(List<Integer> values) {
            addCriterion("check_data_type not in", values, "checkDataType");
            return (Criteria) this;
        }

        public Criteria andCheckDataTypeBetween(Integer value1, Integer value2) {
            addCriterion("check_data_type between", value1, value2, "checkDataType");
            return (Criteria) this;
        }

        public Criteria andCheckDataTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("check_data_type not between", value1, value2, "checkDataType");
            return (Criteria) this;
        }

        public Criteria andThirdSettleScoreEventIdIsNull() {
            addCriterion("third_settle_score_event_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdSettleScoreEventIdIsNotNull() {
            addCriterion("third_settle_score_event_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdSettleScoreEventIdEqualTo(Long value) {
            addCriterion("third_settle_score_event_id =", value, "thirdSettleScoreEventId");
            return (Criteria) this;
        }

        public Criteria andThirdSettleScoreEventIdNotEqualTo(Long value) {
            addCriterion("third_settle_score_event_id <>", value, "thirdSettleScoreEventId");
            return (Criteria) this;
        }

        public Criteria andThirdSettleScoreEventIdGreaterThan(Long value) {
            addCriterion("third_settle_score_event_id >", value, "thirdSettleScoreEventId");
            return (Criteria) this;
        }

        public Criteria andThirdSettleScoreEventIdGreaterThanOrEqualTo(Long value) {
            addCriterion("third_settle_score_event_id >=", value, "thirdSettleScoreEventId");
            return (Criteria) this;
        }

        public Criteria andThirdSettleScoreEventIdLessThan(Long value) {
            addCriterion("third_settle_score_event_id <", value, "thirdSettleScoreEventId");
            return (Criteria) this;
        }

        public Criteria andThirdSettleScoreEventIdLessThanOrEqualTo(Long value) {
            addCriterion("third_settle_score_event_id <=", value, "thirdSettleScoreEventId");
            return (Criteria) this;
        }

        public Criteria andThirdSettleScoreEventIdIn(List<Long> values) {
            addCriterion("third_settle_score_event_id in", values, "thirdSettleScoreEventId");
            return (Criteria) this;
        }

        public Criteria andThirdSettleScoreEventIdNotIn(List<Long> values) {
            addCriterion("third_settle_score_event_id not in", values, "thirdSettleScoreEventId");
            return (Criteria) this;
        }

        public Criteria andThirdSettleScoreEventIdBetween(Long value1, Long value2) {
            addCriterion("third_settle_score_event_id between", value1, value2, "thirdSettleScoreEventId");
            return (Criteria) this;
        }

        public Criteria andThirdSettleScoreEventIdNotBetween(Long value1, Long value2) {
            addCriterion("third_settle_score_event_id not between", value1, value2, "thirdSettleScoreEventId");
            return (Criteria) this;
        }

        public Criteria andCheckTypeIsNull() {
            addCriterion("check_type is null");
            return (Criteria) this;
        }

        public Criteria andCheckTypeIsNotNull() {
            addCriterion("check_type is not null");
            return (Criteria) this;
        }

        public Criteria andCheckTypeEqualTo(Integer value) {
            addCriterion("check_type =", value, "checkType");
            return (Criteria) this;
        }

        public Criteria andCheckTypeNotEqualTo(Integer value) {
            addCriterion("check_type <>", value, "checkType");
            return (Criteria) this;
        }

        public Criteria andCheckTypeGreaterThan(Integer value) {
            addCriterion("check_type >", value, "checkType");
            return (Criteria) this;
        }

        public Criteria andCheckTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("check_type >=", value, "checkType");
            return (Criteria) this;
        }

        public Criteria andCheckTypeLessThan(Integer value) {
            addCriterion("check_type <", value, "checkType");
            return (Criteria) this;
        }

        public Criteria andCheckTypeLessThanOrEqualTo(Integer value) {
            addCriterion("check_type <=", value, "checkType");
            return (Criteria) this;
        }

        public Criteria andCheckTypeIn(List<Integer> values) {
            addCriterion("check_type in", values, "checkType");
            return (Criteria) this;
        }

        public Criteria andCheckTypeNotIn(List<Integer> values) {
            addCriterion("check_type not in", values, "checkType");
            return (Criteria) this;
        }

        public Criteria andCheckTypeBetween(Integer value1, Integer value2) {
            addCriterion("check_type between", value1, value2, "checkType");
            return (Criteria) this;
        }

        public Criteria andCheckTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("check_type not between", value1, value2, "checkType");
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

        public Criteria andIsGreyIsNull() {
            addCriterion("is_grey is null");
            return (Criteria) this;
        }

        public Criteria andIsGreyIsNotNull() {
            addCriterion("is_grey is not null");
            return (Criteria) this;
        }

        public Criteria andIsGreyEqualTo(Integer value) {
            addCriterion("is_grey =", value, "isGrey");
            return (Criteria) this;
        }

        public Criteria andIsGreyNotEqualTo(Integer value) {
            addCriterion("is_grey <>", value, "isGrey");
            return (Criteria) this;
        }

        public Criteria andIsGreyGreaterThan(Integer value) {
            addCriterion("is_grey >", value, "isGrey");
            return (Criteria) this;
        }

        public Criteria andIsGreyGreaterThanOrEqualTo(Integer value) {
            addCriterion("is_grey >=", value, "isGrey");
            return (Criteria) this;
        }

        public Criteria andIsGreyLessThan(Integer value) {
            addCriterion("is_grey <", value, "isGrey");
            return (Criteria) this;
        }

        public Criteria andIsGreyLessThanOrEqualTo(Integer value) {
            addCriterion("is_grey <=", value, "isGrey");
            return (Criteria) this;
        }

        public Criteria andIsGreyIn(List<Integer> values) {
            addCriterion("is_grey in", values, "isGrey");
            return (Criteria) this;
        }

        public Criteria andIsGreyNotIn(List<Integer> values) {
            addCriterion("is_grey not in", values, "isGrey");
            return (Criteria) this;
        }

        public Criteria andIsGreyBetween(Integer value1, Integer value2) {
            addCriterion("is_grey between", value1, value2, "isGrey");
            return (Criteria) this;
        }

        public Criteria andIsGreyNotBetween(Integer value1, Integer value2) {
            addCriterion("is_grey not between", value1, value2, "isGrey");
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

        public Criteria andExtryInfoIsNull() {
            addCriterion("extry_info is null");
            return (Criteria) this;
        }

        public Criteria andExtryInfoIsNotNull() {
            addCriterion("extry_info is not null");
            return (Criteria) this;
        }

        public Criteria andExtryInfoEqualTo(String value) {
            addCriterion("extry_info =", value, "extryInfo");
            return (Criteria) this;
        }

        public Criteria andExtryInfoNotEqualTo(String value) {
            addCriterion("extry_info <>", value, "extryInfo");
            return (Criteria) this;
        }

        public Criteria andExtryInfoGreaterThan(String value) {
            addCriterion("extry_info >", value, "extryInfo");
            return (Criteria) this;
        }

        public Criteria andExtryInfoGreaterThanOrEqualTo(String value) {
            addCriterion("extry_info >=", value, "extryInfo");
            return (Criteria) this;
        }

        public Criteria andExtryInfoLessThan(String value) {
            addCriterion("extry_info <", value, "extryInfo");
            return (Criteria) this;
        }

        public Criteria andExtryInfoLessThanOrEqualTo(String value) {
            addCriterion("extry_info <=", value, "extryInfo");
            return (Criteria) this;
        }

        public Criteria andExtryInfoLike(String value) {
            addCriterion("extry_info like", value, "extryInfo");
            return (Criteria) this;
        }

        public Criteria andExtryInfoNotLike(String value) {
            addCriterion("extry_info not like", value, "extryInfo");
            return (Criteria) this;
        }

        public Criteria andExtryInfoIn(List<String> values) {
            addCriterion("extry_info in", values, "extryInfo");
            return (Criteria) this;
        }

        public Criteria andExtryInfoNotIn(List<String> values) {
            addCriterion("extry_info not in", values, "extryInfo");
            return (Criteria) this;
        }

        public Criteria andExtryInfoBetween(String value1, String value2) {
            addCriterion("extry_info between", value1, value2, "extryInfo");
            return (Criteria) this;
        }

        public Criteria andExtryInfoNotBetween(String value1, String value2) {
            addCriterion("extry_info not between", value1, value2, "extryInfo");
            return (Criteria) this;
        }

        public Criteria andCheckNumberIsNull() {
            addCriterion("check_number is null");
            return (Criteria) this;
        }

        public Criteria andCheckNumberIsNotNull() {
            addCriterion("check_number is not null");
            return (Criteria) this;
        }

        public Criteria andCheckNumberEqualTo(Integer value) {
            addCriterion("check_number =", value, "checkNumber");
            return (Criteria) this;
        }

        public Criteria andCheckNumberNotEqualTo(Integer value) {
            addCriterion("check_number <>", value, "checkNumber");
            return (Criteria) this;
        }

        public Criteria andCheckNumberGreaterThan(Integer value) {
            addCriterion("check_number >", value, "checkNumber");
            return (Criteria) this;
        }

        public Criteria andCheckNumberGreaterThanOrEqualTo(Integer value) {
            addCriterion("check_number >=", value, "checkNumber");
            return (Criteria) this;
        }

        public Criteria andCheckNumberLessThan(Integer value) {
            addCriterion("check_number <", value, "checkNumber");
            return (Criteria) this;
        }

        public Criteria andCheckNumberLessThanOrEqualTo(Integer value) {
            addCriterion("check_number <=", value, "checkNumber");
            return (Criteria) this;
        }

        public Criteria andCheckNumberIn(List<Integer> values) {
            addCriterion("check_number in", values, "checkNumber");
            return (Criteria) this;
        }

        public Criteria andCheckNumberNotIn(List<Integer> values) {
            addCriterion("check_number not in", values, "checkNumber");
            return (Criteria) this;
        }

        public Criteria andCheckNumberBetween(Integer value1, Integer value2) {
            addCriterion("check_number between", value1, value2, "checkNumber");
            return (Criteria) this;
        }

        public Criteria andCheckNumberNotBetween(Integer value1, Integer value2) {
            addCriterion("check_number not between", value1, value2, "checkNumber");
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

        public Criteria andGoWaterStatusIsNull() {
            addCriterion("go_water_status is null");
            return (Criteria) this;
        }

        public Criteria andGoWaterStatusIsNotNull() {
            addCriterion("go_water_status is not null");
            return (Criteria) this;
        }

        public Criteria andGoWaterStatusEqualTo(Integer value) {
            addCriterion("go_water_status =", value, "goWaterStatus");
            return (Criteria) this;
        }

        public Criteria andGoWaterStatusNotEqualTo(Integer value) {
            addCriterion("go_water_status <>", value, "goWaterStatus");
            return (Criteria) this;
        }

        public Criteria andGoWaterStatusGreaterThan(Integer value) {
            addCriterion("go_water_status >", value, "goWaterStatus");
            return (Criteria) this;
        }

        public Criteria andGoWaterStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("go_water_status >=", value, "goWaterStatus");
            return (Criteria) this;
        }

        public Criteria andGoWaterStatusLessThan(Integer value) {
            addCriterion("go_water_status <", value, "goWaterStatus");
            return (Criteria) this;
        }

        public Criteria andGoWaterStatusLessThanOrEqualTo(Integer value) {
            addCriterion("go_water_status <=", value, "goWaterStatus");
            return (Criteria) this;
        }

        public Criteria andGoWaterStatusIn(List<Integer> values) {
            addCriterion("go_water_status in", values, "goWaterStatus");
            return (Criteria) this;
        }

        public Criteria andGoWaterStatusNotIn(List<Integer> values) {
            addCriterion("go_water_status not in", values, "goWaterStatus");
            return (Criteria) this;
        }

        public Criteria andGoWaterStatusBetween(Integer value1, Integer value2) {
            addCriterion("go_water_status between", value1, value2, "goWaterStatus");
            return (Criteria) this;
        }

        public Criteria andGoWaterStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("go_water_status not between", value1, value2, "goWaterStatus");
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

        public Criteria andFiveMinSectionIsNull() {
            addCriterion("five_min_section is null");
            return (Criteria) this;
        }

        public Criteria andFiveMinSectionIsNotNull() {
            addCriterion("five_min_section is not null");
            return (Criteria) this;
        }

        public Criteria andFiveMinSectionEqualTo(String value) {
            addCriterion("five_min_section =", value, "fiveMinSection");
            return (Criteria) this;
        }

        public Criteria andFiveMinSectionNotEqualTo(String value) {
            addCriterion("five_min_section <>", value, "fiveMinSection");
            return (Criteria) this;
        }

        public Criteria andFiveMinSectionGreaterThan(String value) {
            addCriterion("five_min_section >", value, "fiveMinSection");
            return (Criteria) this;
        }

        public Criteria andFiveMinSectionGreaterThanOrEqualTo(String value) {
            addCriterion("five_min_section >=", value, "fiveMinSection");
            return (Criteria) this;
        }

        public Criteria andFiveMinSectionLessThan(String value) {
            addCriterion("five_min_section <", value, "fiveMinSection");
            return (Criteria) this;
        }

        public Criteria andFiveMinSectionLessThanOrEqualTo(String value) {
            addCriterion("five_min_section <=", value, "fiveMinSection");
            return (Criteria) this;
        }

        public Criteria andFiveMinSectionLike(String value) {
            addCriterion("five_min_section like", value, "fiveMinSection");
            return (Criteria) this;
        }

        public Criteria andFiveMinSectionNotLike(String value) {
            addCriterion("five_min_section not like", value, "fiveMinSection");
            return (Criteria) this;
        }

        public Criteria andFiveMinSectionIn(List<String> values) {
            addCriterion("five_min_section in", values, "fiveMinSection");
            return (Criteria) this;
        }

        public Criteria andFiveMinSectionNotIn(List<String> values) {
            addCriterion("five_min_section not in", values, "fiveMinSection");
            return (Criteria) this;
        }

        public Criteria andFiveMinSectionBetween(String value1, String value2) {
            addCriterion("five_min_section between", value1, value2, "fiveMinSection");
            return (Criteria) this;
        }

        public Criteria andFiveMinSectionNotBetween(String value1, String value2) {
            addCriterion("five_min_section not between", value1, value2, "fiveMinSection");
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