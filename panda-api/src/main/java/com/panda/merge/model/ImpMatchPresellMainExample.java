package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class ImpMatchPresellMainExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ImpMatchPresellMainExample() {
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

        public Criteria andImpCountsIsNull() {
            addCriterion("imp_counts is null");
            return (Criteria) this;
        }

        public Criteria andImpCountsIsNotNull() {
            addCriterion("imp_counts is not null");
            return (Criteria) this;
        }

        public Criteria andImpCountsEqualTo(Integer value) {
            addCriterion("imp_counts =", value, "impCounts");
            return (Criteria) this;
        }

        public Criteria andImpCountsNotEqualTo(Integer value) {
            addCriterion("imp_counts <>", value, "impCounts");
            return (Criteria) this;
        }

        public Criteria andImpCountsGreaterThan(Integer value) {
            addCriterion("imp_counts >", value, "impCounts");
            return (Criteria) this;
        }

        public Criteria andImpCountsGreaterThanOrEqualTo(Integer value) {
            addCriterion("imp_counts >=", value, "impCounts");
            return (Criteria) this;
        }

        public Criteria andImpCountsLessThan(Integer value) {
            addCriterion("imp_counts <", value, "impCounts");
            return (Criteria) this;
        }

        public Criteria andImpCountsLessThanOrEqualTo(Integer value) {
            addCriterion("imp_counts <=", value, "impCounts");
            return (Criteria) this;
        }

        public Criteria andImpCountsIn(List<Integer> values) {
            addCriterion("imp_counts in", values, "impCounts");
            return (Criteria) this;
        }

        public Criteria andImpCountsNotIn(List<Integer> values) {
            addCriterion("imp_counts not in", values, "impCounts");
            return (Criteria) this;
        }

        public Criteria andImpCountsBetween(Integer value1, Integer value2) {
            addCriterion("imp_counts between", value1, value2, "impCounts");
            return (Criteria) this;
        }

        public Criteria andImpCountsNotBetween(Integer value1, Integer value2) {
            addCriterion("imp_counts not between", value1, value2, "impCounts");
            return (Criteria) this;
        }

        public Criteria andImpSuccessCountsIsNull() {
            addCriterion("imp_success_counts is null");
            return (Criteria) this;
        }

        public Criteria andImpSuccessCountsIsNotNull() {
            addCriterion("imp_success_counts is not null");
            return (Criteria) this;
        }

        public Criteria andImpSuccessCountsEqualTo(Integer value) {
            addCriterion("imp_success_counts =", value, "impSuccessCounts");
            return (Criteria) this;
        }

        public Criteria andImpSuccessCountsNotEqualTo(Integer value) {
            addCriterion("imp_success_counts <>", value, "impSuccessCounts");
            return (Criteria) this;
        }

        public Criteria andImpSuccessCountsGreaterThan(Integer value) {
            addCriterion("imp_success_counts >", value, "impSuccessCounts");
            return (Criteria) this;
        }

        public Criteria andImpSuccessCountsGreaterThanOrEqualTo(Integer value) {
            addCriterion("imp_success_counts >=", value, "impSuccessCounts");
            return (Criteria) this;
        }

        public Criteria andImpSuccessCountsLessThan(Integer value) {
            addCriterion("imp_success_counts <", value, "impSuccessCounts");
            return (Criteria) this;
        }

        public Criteria andImpSuccessCountsLessThanOrEqualTo(Integer value) {
            addCriterion("imp_success_counts <=", value, "impSuccessCounts");
            return (Criteria) this;
        }

        public Criteria andImpSuccessCountsIn(List<Integer> values) {
            addCriterion("imp_success_counts in", values, "impSuccessCounts");
            return (Criteria) this;
        }

        public Criteria andImpSuccessCountsNotIn(List<Integer> values) {
            addCriterion("imp_success_counts not in", values, "impSuccessCounts");
            return (Criteria) this;
        }

        public Criteria andImpSuccessCountsBetween(Integer value1, Integer value2) {
            addCriterion("imp_success_counts between", value1, value2, "impSuccessCounts");
            return (Criteria) this;
        }

        public Criteria andImpSuccessCountsNotBetween(Integer value1, Integer value2) {
            addCriterion("imp_success_counts not between", value1, value2, "impSuccessCounts");
            return (Criteria) this;
        }

        public Criteria andImpFailureCountsIsNull() {
            addCriterion("imp_failure_counts is null");
            return (Criteria) this;
        }

        public Criteria andImpFailureCountsIsNotNull() {
            addCriterion("imp_failure_counts is not null");
            return (Criteria) this;
        }

        public Criteria andImpFailureCountsEqualTo(Integer value) {
            addCriterion("imp_failure_counts =", value, "impFailureCounts");
            return (Criteria) this;
        }

        public Criteria andImpFailureCountsNotEqualTo(Integer value) {
            addCriterion("imp_failure_counts <>", value, "impFailureCounts");
            return (Criteria) this;
        }

        public Criteria andImpFailureCountsGreaterThan(Integer value) {
            addCriterion("imp_failure_counts >", value, "impFailureCounts");
            return (Criteria) this;
        }

        public Criteria andImpFailureCountsGreaterThanOrEqualTo(Integer value) {
            addCriterion("imp_failure_counts >=", value, "impFailureCounts");
            return (Criteria) this;
        }

        public Criteria andImpFailureCountsLessThan(Integer value) {
            addCriterion("imp_failure_counts <", value, "impFailureCounts");
            return (Criteria) this;
        }

        public Criteria andImpFailureCountsLessThanOrEqualTo(Integer value) {
            addCriterion("imp_failure_counts <=", value, "impFailureCounts");
            return (Criteria) this;
        }

        public Criteria andImpFailureCountsIn(List<Integer> values) {
            addCriterion("imp_failure_counts in", values, "impFailureCounts");
            return (Criteria) this;
        }

        public Criteria andImpFailureCountsNotIn(List<Integer> values) {
            addCriterion("imp_failure_counts not in", values, "impFailureCounts");
            return (Criteria) this;
        }

        public Criteria andImpFailureCountsBetween(Integer value1, Integer value2) {
            addCriterion("imp_failure_counts between", value1, value2, "impFailureCounts");
            return (Criteria) this;
        }

        public Criteria andImpFailureCountsNotBetween(Integer value1, Integer value2) {
            addCriterion("imp_failure_counts not between", value1, value2, "impFailureCounts");
            return (Criteria) this;
        }

        public Criteria andOperatorIsNull() {
            addCriterion("operator is null");
            return (Criteria) this;
        }

        public Criteria andOperatorIsNotNull() {
            addCriterion("operator is not null");
            return (Criteria) this;
        }

        public Criteria andOperatorEqualTo(String value) {
            addCriterion("operator =", value, "operator");
            return (Criteria) this;
        }

        public Criteria andOperatorNotEqualTo(String value) {
            addCriterion("operator <>", value, "operator");
            return (Criteria) this;
        }

        public Criteria andOperatorGreaterThan(String value) {
            addCriterion("operator >", value, "operator");
            return (Criteria) this;
        }

        public Criteria andOperatorGreaterThanOrEqualTo(String value) {
            addCriterion("operator >=", value, "operator");
            return (Criteria) this;
        }

        public Criteria andOperatorLessThan(String value) {
            addCriterion("operator <", value, "operator");
            return (Criteria) this;
        }

        public Criteria andOperatorLessThanOrEqualTo(String value) {
            addCriterion("operator <=", value, "operator");
            return (Criteria) this;
        }

        public Criteria andOperatorLike(String value) {
            addCriterion("operator like", value, "operator");
            return (Criteria) this;
        }

        public Criteria andOperatorNotLike(String value) {
            addCriterion("operator not like", value, "operator");
            return (Criteria) this;
        }

        public Criteria andOperatorIn(List<String> values) {
            addCriterion("operator in", values, "operator");
            return (Criteria) this;
        }

        public Criteria andOperatorNotIn(List<String> values) {
            addCriterion("operator not in", values, "operator");
            return (Criteria) this;
        }

        public Criteria andOperatorBetween(String value1, String value2) {
            addCriterion("operator between", value1, value2, "operator");
            return (Criteria) this;
        }

        public Criteria andOperatorNotBetween(String value1, String value2) {
            addCriterion("operator not between", value1, value2, "operator");
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

        public Criteria andOperatTimeIsNull() {
            addCriterion("operat_time is null");
            return (Criteria) this;
        }

        public Criteria andOperatTimeIsNotNull() {
            addCriterion("operat_time is not null");
            return (Criteria) this;
        }

        public Criteria andOperatTimeEqualTo(Long value) {
            addCriterion("operat_time =", value, "operatTime");
            return (Criteria) this;
        }

        public Criteria andOperatTimeNotEqualTo(Long value) {
            addCriterion("operat_time <>", value, "operatTime");
            return (Criteria) this;
        }

        public Criteria andOperatTimeGreaterThan(Long value) {
            addCriterion("operat_time >", value, "operatTime");
            return (Criteria) this;
        }

        public Criteria andOperatTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("operat_time >=", value, "operatTime");
            return (Criteria) this;
        }

        public Criteria andOperatTimeLessThan(Long value) {
            addCriterion("operat_time <", value, "operatTime");
            return (Criteria) this;
        }

        public Criteria andOperatTimeLessThanOrEqualTo(Long value) {
            addCriterion("operat_time <=", value, "operatTime");
            return (Criteria) this;
        }

        public Criteria andOperatTimeIn(List<Long> values) {
            addCriterion("operat_time in", values, "operatTime");
            return (Criteria) this;
        }

        public Criteria andOperatTimeNotIn(List<Long> values) {
            addCriterion("operat_time not in", values, "operatTime");
            return (Criteria) this;
        }

        public Criteria andOperatTimeBetween(Long value1, Long value2) {
            addCriterion("operat_time between", value1, value2, "operatTime");
            return (Criteria) this;
        }

        public Criteria andOperatTimeNotBetween(Long value1, Long value2) {
            addCriterion("operat_time not between", value1, value2, "operatTime");
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