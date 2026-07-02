package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class OutrightMatchLogExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public OutrightMatchLogExample() {
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

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andOperateTargetIdIsNull() {
            addCriterion("operate_target_id is null");
            return (Criteria) this;
        }

        public Criteria andOperateTargetIdIsNotNull() {
            addCriterion("operate_target_id is not null");
            return (Criteria) this;
        }

        public Criteria andOperateTargetIdEqualTo(Long value) {
            addCriterion("operate_target_id =", value, "operateTargetId");
            return (Criteria) this;
        }

        public Criteria andOperateTargetIdNotEqualTo(Long value) {
            addCriterion("operate_target_id <>", value, "operateTargetId");
            return (Criteria) this;
        }

        public Criteria andOperateTargetIdGreaterThan(Long value) {
            addCriterion("operate_target_id >", value, "operateTargetId");
            return (Criteria) this;
        }

        public Criteria andOperateTargetIdGreaterThanOrEqualTo(Long value) {
            addCriterion("operate_target_id >=", value, "operateTargetId");
            return (Criteria) this;
        }

        public Criteria andOperateTargetIdLessThan(Long value) {
            addCriterion("operate_target_id <", value, "operateTargetId");
            return (Criteria) this;
        }

        public Criteria andOperateTargetIdLessThanOrEqualTo(Long value) {
            addCriterion("operate_target_id <=", value, "operateTargetId");
            return (Criteria) this;
        }

        public Criteria andOperateTargetIdIn(List<Long> values) {
            addCriterion("operate_target_id in", values, "operateTargetId");
            return (Criteria) this;
        }

        public Criteria andOperateTargetIdNotIn(List<Long> values) {
            addCriterion("operate_target_id not in", values, "operateTargetId");
            return (Criteria) this;
        }

        public Criteria andOperateTargetIdBetween(Long value1, Long value2) {
            addCriterion("operate_target_id between", value1, value2, "operateTargetId");
            return (Criteria) this;
        }

        public Criteria andOperateTargetIdNotBetween(Long value1, Long value2) {
            addCriterion("operate_target_id not between", value1, value2, "operateTargetId");
            return (Criteria) this;
        }

        public Criteria andOperatorIdIsNull() {
            addCriterion("operator_id is null");
            return (Criteria) this;
        }

        public Criteria andOperatorIdIsNotNull() {
            addCriterion("operator_id is not null");
            return (Criteria) this;
        }

        public Criteria andOperatorIdEqualTo(Long value) {
            addCriterion("operator_id =", value, "operatorId");
            return (Criteria) this;
        }

        public Criteria andOperatorIdNotEqualTo(Long value) {
            addCriterion("operator_id <>", value, "operatorId");
            return (Criteria) this;
        }

        public Criteria andOperatorIdGreaterThan(Long value) {
            addCriterion("operator_id >", value, "operatorId");
            return (Criteria) this;
        }

        public Criteria andOperatorIdGreaterThanOrEqualTo(Long value) {
            addCriterion("operator_id >=", value, "operatorId");
            return (Criteria) this;
        }

        public Criteria andOperatorIdLessThan(Long value) {
            addCriterion("operator_id <", value, "operatorId");
            return (Criteria) this;
        }

        public Criteria andOperatorIdLessThanOrEqualTo(Long value) {
            addCriterion("operator_id <=", value, "operatorId");
            return (Criteria) this;
        }

        public Criteria andOperatorIdIn(List<Long> values) {
            addCriterion("operator_id in", values, "operatorId");
            return (Criteria) this;
        }

        public Criteria andOperatorIdNotIn(List<Long> values) {
            addCriterion("operator_id not in", values, "operatorId");
            return (Criteria) this;
        }

        public Criteria andOperatorIdBetween(Long value1, Long value2) {
            addCriterion("operator_id between", value1, value2, "operatorId");
            return (Criteria) this;
        }

        public Criteria andOperatorIdNotBetween(Long value1, Long value2) {
            addCriterion("operator_id not between", value1, value2, "operatorId");
            return (Criteria) this;
        }

        public Criteria andOperatorNameIsNull() {
            addCriterion("operator_name is null");
            return (Criteria) this;
        }

        public Criteria andOperatorNameIsNotNull() {
            addCriterion("operator_name is not null");
            return (Criteria) this;
        }

        public Criteria andOperatorNameEqualTo(String value) {
            addCriterion("operator_name =", value, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameNotEqualTo(String value) {
            addCriterion("operator_name <>", value, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameGreaterThan(String value) {
            addCriterion("operator_name >", value, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameGreaterThanOrEqualTo(String value) {
            addCriterion("operator_name >=", value, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameLessThan(String value) {
            addCriterion("operator_name <", value, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameLessThanOrEqualTo(String value) {
            addCriterion("operator_name <=", value, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameLike(String value) {
            addCriterion("operator_name like", value, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameNotLike(String value) {
            addCriterion("operator_name not like", value, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameIn(List<String> values) {
            addCriterion("operator_name in", values, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameNotIn(List<String> values) {
            addCriterion("operator_name not in", values, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameBetween(String value1, String value2) {
            addCriterion("operator_name between", value1, value2, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameNotBetween(String value1, String value2) {
            addCriterion("operator_name not between", value1, value2, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorModleIsNull() {
            addCriterion("operator_modle is null");
            return (Criteria) this;
        }

        public Criteria andOperatorModleIsNotNull() {
            addCriterion("operator_modle is not null");
            return (Criteria) this;
        }

        public Criteria andOperatorModleEqualTo(String value) {
            addCriterion("operator_modle =", value, "operatorModle");
            return (Criteria) this;
        }

        public Criteria andOperatorModleNotEqualTo(String value) {
            addCriterion("operator_modle <>", value, "operatorModle");
            return (Criteria) this;
        }

        public Criteria andOperatorModleGreaterThan(String value) {
            addCriterion("operator_modle >", value, "operatorModle");
            return (Criteria) this;
        }

        public Criteria andOperatorModleGreaterThanOrEqualTo(String value) {
            addCriterion("operator_modle >=", value, "operatorModle");
            return (Criteria) this;
        }

        public Criteria andOperatorModleLessThan(String value) {
            addCriterion("operator_modle <", value, "operatorModle");
            return (Criteria) this;
        }

        public Criteria andOperatorModleLessThanOrEqualTo(String value) {
            addCriterion("operator_modle <=", value, "operatorModle");
            return (Criteria) this;
        }

        public Criteria andOperatorModleLike(String value) {
            addCriterion("operator_modle like", value, "operatorModle");
            return (Criteria) this;
        }

        public Criteria andOperatorModleNotLike(String value) {
            addCriterion("operator_modle not like", value, "operatorModle");
            return (Criteria) this;
        }

        public Criteria andOperatorModleIn(List<String> values) {
            addCriterion("operator_modle in", values, "operatorModle");
            return (Criteria) this;
        }

        public Criteria andOperatorModleNotIn(List<String> values) {
            addCriterion("operator_modle not in", values, "operatorModle");
            return (Criteria) this;
        }

        public Criteria andOperatorModleBetween(String value1, String value2) {
            addCriterion("operator_modle between", value1, value2, "operatorModle");
            return (Criteria) this;
        }

        public Criteria andOperatorModleNotBetween(String value1, String value2) {
            addCriterion("operator_modle not between", value1, value2, "operatorModle");
            return (Criteria) this;
        }

        public Criteria andOperatorNumberIsNull() {
            addCriterion("operator_number is null");
            return (Criteria) this;
        }

        public Criteria andOperatorNumberIsNotNull() {
            addCriterion("operator_number is not null");
            return (Criteria) this;
        }

        public Criteria andOperatorNumberEqualTo(String value) {
            addCriterion("operator_number =", value, "operatorNumber");
            return (Criteria) this;
        }

        public Criteria andOperatorNumberNotEqualTo(String value) {
            addCriterion("operator_number <>", value, "operatorNumber");
            return (Criteria) this;
        }

        public Criteria andOperatorNumberGreaterThan(String value) {
            addCriterion("operator_number >", value, "operatorNumber");
            return (Criteria) this;
        }

        public Criteria andOperatorNumberGreaterThanOrEqualTo(String value) {
            addCriterion("operator_number >=", value, "operatorNumber");
            return (Criteria) this;
        }

        public Criteria andOperatorNumberLessThan(String value) {
            addCriterion("operator_number <", value, "operatorNumber");
            return (Criteria) this;
        }

        public Criteria andOperatorNumberLessThanOrEqualTo(String value) {
            addCriterion("operator_number <=", value, "operatorNumber");
            return (Criteria) this;
        }

        public Criteria andOperatorNumberLike(String value) {
            addCriterion("operator_number like", value, "operatorNumber");
            return (Criteria) this;
        }

        public Criteria andOperatorNumberNotLike(String value) {
            addCriterion("operator_number not like", value, "operatorNumber");
            return (Criteria) this;
        }

        public Criteria andOperatorNumberIn(List<String> values) {
            addCriterion("operator_number in", values, "operatorNumber");
            return (Criteria) this;
        }

        public Criteria andOperatorNumberNotIn(List<String> values) {
            addCriterion("operator_number not in", values, "operatorNumber");
            return (Criteria) this;
        }

        public Criteria andOperatorNumberBetween(String value1, String value2) {
            addCriterion("operator_number between", value1, value2, "operatorNumber");
            return (Criteria) this;
        }

        public Criteria andOperatorNumberNotBetween(String value1, String value2) {
            addCriterion("operator_number not between", value1, value2, "operatorNumber");
            return (Criteria) this;
        }

        public Criteria andOperatorTextIsNull() {
            addCriterion("operator_text is null");
            return (Criteria) this;
        }

        public Criteria andOperatorTextIsNotNull() {
            addCriterion("operator_text is not null");
            return (Criteria) this;
        }

        public Criteria andOperatorTextEqualTo(String value) {
            addCriterion("operator_text =", value, "operatorText");
            return (Criteria) this;
        }

        public Criteria andOperatorTextNotEqualTo(String value) {
            addCriterion("operator_text <>", value, "operatorText");
            return (Criteria) this;
        }

        public Criteria andOperatorTextGreaterThan(String value) {
            addCriterion("operator_text >", value, "operatorText");
            return (Criteria) this;
        }

        public Criteria andOperatorTextGreaterThanOrEqualTo(String value) {
            addCriterion("operator_text >=", value, "operatorText");
            return (Criteria) this;
        }

        public Criteria andOperatorTextLessThan(String value) {
            addCriterion("operator_text <", value, "operatorText");
            return (Criteria) this;
        }

        public Criteria andOperatorTextLessThanOrEqualTo(String value) {
            addCriterion("operator_text <=", value, "operatorText");
            return (Criteria) this;
        }

        public Criteria andOperatorTextLike(String value) {
            addCriterion("operator_text like", value, "operatorText");
            return (Criteria) this;
        }

        public Criteria andOperatorTextNotLike(String value) {
            addCriterion("operator_text not like", value, "operatorText");
            return (Criteria) this;
        }

        public Criteria andOperatorTextIn(List<String> values) {
            addCriterion("operator_text in", values, "operatorText");
            return (Criteria) this;
        }

        public Criteria andOperatorTextNotIn(List<String> values) {
            addCriterion("operator_text not in", values, "operatorText");
            return (Criteria) this;
        }

        public Criteria andOperatorTextBetween(String value1, String value2) {
            addCriterion("operator_text between", value1, value2, "operatorText");
            return (Criteria) this;
        }

        public Criteria andOperatorTextNotBetween(String value1, String value2) {
            addCriterion("operator_text not between", value1, value2, "operatorText");
            return (Criteria) this;
        }

        public Criteria andOperatorTimeIsNull() {
            addCriterion("operator_time is null");
            return (Criteria) this;
        }

        public Criteria andOperatorTimeIsNotNull() {
            addCriterion("operator_time is not null");
            return (Criteria) this;
        }

        public Criteria andOperatorTimeEqualTo(Long value) {
            addCriterion("operator_time =", value, "operatorTime");
            return (Criteria) this;
        }

        public Criteria andOperatorTimeNotEqualTo(Long value) {
            addCriterion("operator_time <>", value, "operatorTime");
            return (Criteria) this;
        }

        public Criteria andOperatorTimeGreaterThan(Long value) {
            addCriterion("operator_time >", value, "operatorTime");
            return (Criteria) this;
        }

        public Criteria andOperatorTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("operator_time >=", value, "operatorTime");
            return (Criteria) this;
        }

        public Criteria andOperatorTimeLessThan(Long value) {
            addCriterion("operator_time <", value, "operatorTime");
            return (Criteria) this;
        }

        public Criteria andOperatorTimeLessThanOrEqualTo(Long value) {
            addCriterion("operator_time <=", value, "operatorTime");
            return (Criteria) this;
        }

        public Criteria andOperatorTimeIn(List<Long> values) {
            addCriterion("operator_time in", values, "operatorTime");
            return (Criteria) this;
        }

        public Criteria andOperatorTimeNotIn(List<Long> values) {
            addCriterion("operator_time not in", values, "operatorTime");
            return (Criteria) this;
        }

        public Criteria andOperatorTimeBetween(Long value1, Long value2) {
            addCriterion("operator_time between", value1, value2, "operatorTime");
            return (Criteria) this;
        }

        public Criteria andOperatorTimeNotBetween(Long value1, Long value2) {
            addCriterion("operator_time not between", value1, value2, "operatorTime");
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