package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class MatchSettleRollBackInfoExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MatchSettleRollBackInfoExample() {
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

        public Criteria andDataTypeIsNull() {
            addCriterion("data_type is null");
            return (Criteria) this;
        }

        public Criteria andDataTypeIsNotNull() {
            addCriterion("data_type is not null");
            return (Criteria) this;
        }

        public Criteria andDataTypeEqualTo(Integer value) {
            addCriterion("data_type =", value, "dataType");
            return (Criteria) this;
        }

        public Criteria andDataTypeNotEqualTo(Integer value) {
            addCriterion("data_type <>", value, "dataType");
            return (Criteria) this;
        }

        public Criteria andDataTypeGreaterThan(Integer value) {
            addCriterion("data_type >", value, "dataType");
            return (Criteria) this;
        }

        public Criteria andDataTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("data_type >=", value, "dataType");
            return (Criteria) this;
        }

        public Criteria andDataTypeLessThan(Integer value) {
            addCriterion("data_type <", value, "dataType");
            return (Criteria) this;
        }

        public Criteria andDataTypeLessThanOrEqualTo(Integer value) {
            addCriterion("data_type <=", value, "dataType");
            return (Criteria) this;
        }

        public Criteria andDataTypeIn(List<Integer> values) {
            addCriterion("data_type in", values, "dataType");
            return (Criteria) this;
        }

        public Criteria andDataTypeNotIn(List<Integer> values) {
            addCriterion("data_type not in", values, "dataType");
            return (Criteria) this;
        }

        public Criteria andDataTypeBetween(Integer value1, Integer value2) {
            addCriterion("data_type between", value1, value2, "dataType");
            return (Criteria) this;
        }

        public Criteria andDataTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("data_type not between", value1, value2, "dataType");
            return (Criteria) this;
        }

        public Criteria andRollBackStatusIsNull() {
            addCriterion("roll_back_status is null");
            return (Criteria) this;
        }

        public Criteria andRollBackStatusIsNotNull() {
            addCriterion("roll_back_status is not null");
            return (Criteria) this;
        }

        public Criteria andRollBackStatusEqualTo(Integer value) {
            addCriterion("roll_back_status =", value, "rollBackStatus");
            return (Criteria) this;
        }

        public Criteria andRollBackStatusNotEqualTo(Integer value) {
            addCriterion("roll_back_status <>", value, "rollBackStatus");
            return (Criteria) this;
        }

        public Criteria andRollBackStatusGreaterThan(Integer value) {
            addCriterion("roll_back_status >", value, "rollBackStatus");
            return (Criteria) this;
        }

        public Criteria andRollBackStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("roll_back_status >=", value, "rollBackStatus");
            return (Criteria) this;
        }

        public Criteria andRollBackStatusLessThan(Integer value) {
            addCriterion("roll_back_status <", value, "rollBackStatus");
            return (Criteria) this;
        }

        public Criteria andRollBackStatusLessThanOrEqualTo(Integer value) {
            addCriterion("roll_back_status <=", value, "rollBackStatus");
            return (Criteria) this;
        }

        public Criteria andRollBackStatusIn(List<Integer> values) {
            addCriterion("roll_back_status in", values, "rollBackStatus");
            return (Criteria) this;
        }

        public Criteria andRollBackStatusNotIn(List<Integer> values) {
            addCriterion("roll_back_status not in", values, "rollBackStatus");
            return (Criteria) this;
        }

        public Criteria andRollBackStatusBetween(Integer value1, Integer value2) {
            addCriterion("roll_back_status between", value1, value2, "rollBackStatus");
            return (Criteria) this;
        }

        public Criteria andRollBackStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("roll_back_status not between", value1, value2, "rollBackStatus");
            return (Criteria) this;
        }

        public Criteria andRollBackTimeIsNull() {
            addCriterion("roll_back_time is null");
            return (Criteria) this;
        }

        public Criteria andRollBackTimeIsNotNull() {
            addCriterion("roll_back_time is not null");
            return (Criteria) this;
        }

        public Criteria andRollBackTimeEqualTo(Long value) {
            addCriterion("roll_back_time =", value, "rollBackTime");
            return (Criteria) this;
        }

        public Criteria andRollBackTimeNotEqualTo(Long value) {
            addCriterion("roll_back_time <>", value, "rollBackTime");
            return (Criteria) this;
        }

        public Criteria andRollBackTimeGreaterThan(Long value) {
            addCriterion("roll_back_time >", value, "rollBackTime");
            return (Criteria) this;
        }

        public Criteria andRollBackTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("roll_back_time >=", value, "rollBackTime");
            return (Criteria) this;
        }

        public Criteria andRollBackTimeLessThan(Long value) {
            addCriterion("roll_back_time <", value, "rollBackTime");
            return (Criteria) this;
        }

        public Criteria andRollBackTimeLessThanOrEqualTo(Long value) {
            addCriterion("roll_back_time <=", value, "rollBackTime");
            return (Criteria) this;
        }

        public Criteria andRollBackTimeIn(List<Long> values) {
            addCriterion("roll_back_time in", values, "rollBackTime");
            return (Criteria) this;
        }

        public Criteria andRollBackTimeNotIn(List<Long> values) {
            addCriterion("roll_back_time not in", values, "rollBackTime");
            return (Criteria) this;
        }

        public Criteria andRollBackTimeBetween(Long value1, Long value2) {
            addCriterion("roll_back_time between", value1, value2, "rollBackTime");
            return (Criteria) this;
        }

        public Criteria andRollBackTimeNotBetween(Long value1, Long value2) {
            addCriterion("roll_back_time not between", value1, value2, "rollBackTime");
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

        public Criteria andRollBackOrderCountIsNull() {
            addCriterion("roll_back_order_count is null");
            return (Criteria) this;
        }

        public Criteria andRollBackOrderCountIsNotNull() {
            addCriterion("roll_back_order_count is not null");
            return (Criteria) this;
        }

        public Criteria andRollBackOrderCountEqualTo(Long value) {
            addCriterion("roll_back_order_count =", value, "rollBackOrderCount");
            return (Criteria) this;
        }

        public Criteria andRollBackOrderCountNotEqualTo(Long value) {
            addCriterion("roll_back_order_count <>", value, "rollBackOrderCount");
            return (Criteria) this;
        }

        public Criteria andRollBackOrderCountGreaterThan(Long value) {
            addCriterion("roll_back_order_count >", value, "rollBackOrderCount");
            return (Criteria) this;
        }

        public Criteria andRollBackOrderCountGreaterThanOrEqualTo(Long value) {
            addCriterion("roll_back_order_count >=", value, "rollBackOrderCount");
            return (Criteria) this;
        }

        public Criteria andRollBackOrderCountLessThan(Long value) {
            addCriterion("roll_back_order_count <", value, "rollBackOrderCount");
            return (Criteria) this;
        }

        public Criteria andRollBackOrderCountLessThanOrEqualTo(Long value) {
            addCriterion("roll_back_order_count <=", value, "rollBackOrderCount");
            return (Criteria) this;
        }

        public Criteria andRollBackOrderCountIn(List<Long> values) {
            addCriterion("roll_back_order_count in", values, "rollBackOrderCount");
            return (Criteria) this;
        }

        public Criteria andRollBackOrderCountNotIn(List<Long> values) {
            addCriterion("roll_back_order_count not in", values, "rollBackOrderCount");
            return (Criteria) this;
        }

        public Criteria andRollBackOrderCountBetween(Long value1, Long value2) {
            addCriterion("roll_back_order_count between", value1, value2, "rollBackOrderCount");
            return (Criteria) this;
        }

        public Criteria andRollBackOrderCountNotBetween(Long value1, Long value2) {
            addCriterion("roll_back_order_count not between", value1, value2, "rollBackOrderCount");
            return (Criteria) this;
        }

        public Criteria andOrderCountIsNull() {
            addCriterion("order_count is null");
            return (Criteria) this;
        }

        public Criteria andOrderCountIsNotNull() {
            addCriterion("order_count is not null");
            return (Criteria) this;
        }

        public Criteria andOrderCountEqualTo(Long value) {
            addCriterion("order_count =", value, "orderCount");
            return (Criteria) this;
        }

        public Criteria andOrderCountNotEqualTo(Long value) {
            addCriterion("order_count <>", value, "orderCount");
            return (Criteria) this;
        }

        public Criteria andOrderCountGreaterThan(Long value) {
            addCriterion("order_count >", value, "orderCount");
            return (Criteria) this;
        }

        public Criteria andOrderCountGreaterThanOrEqualTo(Long value) {
            addCriterion("order_count >=", value, "orderCount");
            return (Criteria) this;
        }

        public Criteria andOrderCountLessThan(Long value) {
            addCriterion("order_count <", value, "orderCount");
            return (Criteria) this;
        }

        public Criteria andOrderCountLessThanOrEqualTo(Long value) {
            addCriterion("order_count <=", value, "orderCount");
            return (Criteria) this;
        }

        public Criteria andOrderCountIn(List<Long> values) {
            addCriterion("order_count in", values, "orderCount");
            return (Criteria) this;
        }

        public Criteria andOrderCountNotIn(List<Long> values) {
            addCriterion("order_count not in", values, "orderCount");
            return (Criteria) this;
        }

        public Criteria andOrderCountBetween(Long value1, Long value2) {
            addCriterion("order_count between", value1, value2, "orderCount");
            return (Criteria) this;
        }

        public Criteria andOrderCountNotBetween(Long value1, Long value2) {
            addCriterion("order_count not between", value1, value2, "orderCount");
            return (Criteria) this;
        }

        public Criteria andRollBackSuccessTimeIsNull() {
            addCriterion("roll_back_success_time is null");
            return (Criteria) this;
        }

        public Criteria andRollBackSuccessTimeIsNotNull() {
            addCriterion("roll_back_success_time is not null");
            return (Criteria) this;
        }

        public Criteria andRollBackSuccessTimeEqualTo(Long value) {
            addCriterion("roll_back_success_time =", value, "rollBackSuccessTime");
            return (Criteria) this;
        }

        public Criteria andRollBackSuccessTimeNotEqualTo(Long value) {
            addCriterion("roll_back_success_time <>", value, "rollBackSuccessTime");
            return (Criteria) this;
        }

        public Criteria andRollBackSuccessTimeGreaterThan(Long value) {
            addCriterion("roll_back_success_time >", value, "rollBackSuccessTime");
            return (Criteria) this;
        }

        public Criteria andRollBackSuccessTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("roll_back_success_time >=", value, "rollBackSuccessTime");
            return (Criteria) this;
        }

        public Criteria andRollBackSuccessTimeLessThan(Long value) {
            addCriterion("roll_back_success_time <", value, "rollBackSuccessTime");
            return (Criteria) this;
        }

        public Criteria andRollBackSuccessTimeLessThanOrEqualTo(Long value) {
            addCriterion("roll_back_success_time <=", value, "rollBackSuccessTime");
            return (Criteria) this;
        }

        public Criteria andRollBackSuccessTimeIn(List<Long> values) {
            addCriterion("roll_back_success_time in", values, "rollBackSuccessTime");
            return (Criteria) this;
        }

        public Criteria andRollBackSuccessTimeNotIn(List<Long> values) {
            addCriterion("roll_back_success_time not in", values, "rollBackSuccessTime");
            return (Criteria) this;
        }

        public Criteria andRollBackSuccessTimeBetween(Long value1, Long value2) {
            addCriterion("roll_back_success_time between", value1, value2, "rollBackSuccessTime");
            return (Criteria) this;
        }

        public Criteria andRollBackSuccessTimeNotBetween(Long value1, Long value2) {
            addCriterion("roll_back_success_time not between", value1, value2, "rollBackSuccessTime");
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

        public Criteria andIsDianQiuIsNull() {
            addCriterion("is_dian_qiu is null");
            return (Criteria) this;
        }

        public Criteria andIsDianQiuIsNotNull() {
            addCriterion("is_dian_qiu is not null");
            return (Criteria) this;
        }

        public Criteria andIsDianQiuEqualTo(Integer value) {
            addCriterion("is_dian_qiu =", value, "isDianQiu");
            return (Criteria) this;
        }

        public Criteria andIsDianQiuNotEqualTo(Integer value) {
            addCriterion("is_dian_qiu <>", value, "isDianQiu");
            return (Criteria) this;
        }

        public Criteria andIsDianQiuGreaterThan(Integer value) {
            addCriterion("is_dian_qiu >", value, "isDianQiu");
            return (Criteria) this;
        }

        public Criteria andIsDianQiuGreaterThanOrEqualTo(Integer value) {
            addCriterion("is_dian_qiu >=", value, "isDianQiu");
            return (Criteria) this;
        }

        public Criteria andIsDianQiuLessThan(Integer value) {
            addCriterion("is_dian_qiu <", value, "isDianQiu");
            return (Criteria) this;
        }

        public Criteria andIsDianQiuLessThanOrEqualTo(Integer value) {
            addCriterion("is_dian_qiu <=", value, "isDianQiu");
            return (Criteria) this;
        }

        public Criteria andIsDianQiuIn(List<Integer> values) {
            addCriterion("is_dian_qiu in", values, "isDianQiu");
            return (Criteria) this;
        }

        public Criteria andIsDianQiuNotIn(List<Integer> values) {
            addCriterion("is_dian_qiu not in", values, "isDianQiu");
            return (Criteria) this;
        }

        public Criteria andIsDianQiuBetween(Integer value1, Integer value2) {
            addCriterion("is_dian_qiu between", value1, value2, "isDianQiu");
            return (Criteria) this;
        }

        public Criteria andIsDianQiuNotBetween(Integer value1, Integer value2) {
            addCriterion("is_dian_qiu not between", value1, value2, "isDianQiu");
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