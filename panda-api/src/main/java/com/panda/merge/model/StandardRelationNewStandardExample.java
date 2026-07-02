package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class StandardRelationNewStandardExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public StandardRelationNewStandardExample() {
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

        public Criteria andSourceStandardIdIsNull() {
            addCriterion("source_standard_id is null");
            return (Criteria) this;
        }

        public Criteria andSourceStandardIdIsNotNull() {
            addCriterion("source_standard_id is not null");
            return (Criteria) this;
        }

        public Criteria andSourceStandardIdEqualTo(Long value) {
            addCriterion("source_standard_id =", value, "sourceStandardId");
            return (Criteria) this;
        }

        public Criteria andSourceStandardIdNotEqualTo(Long value) {
            addCriterion("source_standard_id <>", value, "sourceStandardId");
            return (Criteria) this;
        }

        public Criteria andSourceStandardIdGreaterThan(Long value) {
            addCriterion("source_standard_id >", value, "sourceStandardId");
            return (Criteria) this;
        }

        public Criteria andSourceStandardIdGreaterThanOrEqualTo(Long value) {
            addCriterion("source_standard_id >=", value, "sourceStandardId");
            return (Criteria) this;
        }

        public Criteria andSourceStandardIdLessThan(Long value) {
            addCriterion("source_standard_id <", value, "sourceStandardId");
            return (Criteria) this;
        }

        public Criteria andSourceStandardIdLessThanOrEqualTo(Long value) {
            addCriterion("source_standard_id <=", value, "sourceStandardId");
            return (Criteria) this;
        }

        public Criteria andSourceStandardIdIn(List<Long> values) {
            addCriterion("source_standard_id in", values, "sourceStandardId");
            return (Criteria) this;
        }

        public Criteria andSourceStandardIdNotIn(List<Long> values) {
            addCriterion("source_standard_id not in", values, "sourceStandardId");
            return (Criteria) this;
        }

        public Criteria andSourceStandardIdBetween(Long value1, Long value2) {
            addCriterion("source_standard_id between", value1, value2, "sourceStandardId");
            return (Criteria) this;
        }

        public Criteria andSourceStandardIdNotBetween(Long value1, Long value2) {
            addCriterion("source_standard_id not between", value1, value2, "sourceStandardId");
            return (Criteria) this;
        }

        public Criteria andNewStandardIdIsNull() {
            addCriterion("new_standard_id is null");
            return (Criteria) this;
        }

        public Criteria andNewStandardIdIsNotNull() {
            addCriterion("new_standard_id is not null");
            return (Criteria) this;
        }

        public Criteria andNewStandardIdEqualTo(Long value) {
            addCriterion("new_standard_id =", value, "newStandardId");
            return (Criteria) this;
        }

        public Criteria andNewStandardIdNotEqualTo(Long value) {
            addCriterion("new_standard_id <>", value, "newStandardId");
            return (Criteria) this;
        }

        public Criteria andNewStandardIdGreaterThan(Long value) {
            addCriterion("new_standard_id >", value, "newStandardId");
            return (Criteria) this;
        }

        public Criteria andNewStandardIdGreaterThanOrEqualTo(Long value) {
            addCriterion("new_standard_id >=", value, "newStandardId");
            return (Criteria) this;
        }

        public Criteria andNewStandardIdLessThan(Long value) {
            addCriterion("new_standard_id <", value, "newStandardId");
            return (Criteria) this;
        }

        public Criteria andNewStandardIdLessThanOrEqualTo(Long value) {
            addCriterion("new_standard_id <=", value, "newStandardId");
            return (Criteria) this;
        }

        public Criteria andNewStandardIdIn(List<Long> values) {
            addCriterion("new_standard_id in", values, "newStandardId");
            return (Criteria) this;
        }

        public Criteria andNewStandardIdNotIn(List<Long> values) {
            addCriterion("new_standard_id not in", values, "newStandardId");
            return (Criteria) this;
        }

        public Criteria andNewStandardIdBetween(Long value1, Long value2) {
            addCriterion("new_standard_id between", value1, value2, "newStandardId");
            return (Criteria) this;
        }

        public Criteria andNewStandardIdNotBetween(Long value1, Long value2) {
            addCriterion("new_standard_id not between", value1, value2, "newStandardId");
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