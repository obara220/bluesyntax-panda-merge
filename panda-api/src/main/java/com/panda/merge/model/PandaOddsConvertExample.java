package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class PandaOddsConvertExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public PandaOddsConvertExample() {
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

        public Criteria andEuropeStartIsNull() {
            addCriterion("europe_start is null");
            return (Criteria) this;
        }

        public Criteria andEuropeStartIsNotNull() {
            addCriterion("europe_start is not null");
            return (Criteria) this;
        }

        public Criteria andEuropeStartEqualTo(Double value) {
            addCriterion("europe_start =", value, "europeStart");
            return (Criteria) this;
        }

        public Criteria andEuropeStartNotEqualTo(Double value) {
            addCriterion("europe_start <>", value, "europeStart");
            return (Criteria) this;
        }

        public Criteria andEuropeStartGreaterThan(Double value) {
            addCriterion("europe_start >", value, "europeStart");
            return (Criteria) this;
        }

        public Criteria andEuropeStartGreaterThanOrEqualTo(Double value) {
            addCriterion("europe_start >=", value, "europeStart");
            return (Criteria) this;
        }

        public Criteria andEuropeStartLessThan(Double value) {
            addCriterion("europe_start <", value, "europeStart");
            return (Criteria) this;
        }

        public Criteria andEuropeStartLessThanOrEqualTo(Double value) {
            addCriterion("europe_start <=", value, "europeStart");
            return (Criteria) this;
        }

        public Criteria andEuropeStartIn(List<Double> values) {
            addCriterion("europe_start in", values, "europeStart");
            return (Criteria) this;
        }

        public Criteria andEuropeStartNotIn(List<Double> values) {
            addCriterion("europe_start not in", values, "europeStart");
            return (Criteria) this;
        }

        public Criteria andEuropeStartBetween(Double value1, Double value2) {
            addCriterion("europe_start between", value1, value2, "europeStart");
            return (Criteria) this;
        }

        public Criteria andEuropeStartNotBetween(Double value1, Double value2) {
            addCriterion("europe_start not between", value1, value2, "europeStart");
            return (Criteria) this;
        }

        public Criteria andEuropeEndIsNull() {
            addCriterion("europe_end is null");
            return (Criteria) this;
        }

        public Criteria andEuropeEndIsNotNull() {
            addCriterion("europe_end is not null");
            return (Criteria) this;
        }

        public Criteria andEuropeEndEqualTo(Double value) {
            addCriterion("europe_end =", value, "europeEnd");
            return (Criteria) this;
        }

        public Criteria andEuropeEndNotEqualTo(Double value) {
            addCriterion("europe_end <>", value, "europeEnd");
            return (Criteria) this;
        }

        public Criteria andEuropeEndGreaterThan(Double value) {
            addCriterion("europe_end >", value, "europeEnd");
            return (Criteria) this;
        }

        public Criteria andEuropeEndGreaterThanOrEqualTo(Double value) {
            addCriterion("europe_end >=", value, "europeEnd");
            return (Criteria) this;
        }

        public Criteria andEuropeEndLessThan(Double value) {
            addCriterion("europe_end <", value, "europeEnd");
            return (Criteria) this;
        }

        public Criteria andEuropeEndLessThanOrEqualTo(Double value) {
            addCriterion("europe_end <=", value, "europeEnd");
            return (Criteria) this;
        }

        public Criteria andEuropeEndIn(List<Double> values) {
            addCriterion("europe_end in", values, "europeEnd");
            return (Criteria) this;
        }

        public Criteria andEuropeEndNotIn(List<Double> values) {
            addCriterion("europe_end not in", values, "europeEnd");
            return (Criteria) this;
        }

        public Criteria andEuropeEndBetween(Double value1, Double value2) {
            addCriterion("europe_end between", value1, value2, "europeEnd");
            return (Criteria) this;
        }

        public Criteria andEuropeEndNotBetween(Double value1, Double value2) {
            addCriterion("europe_end not between", value1, value2, "europeEnd");
            return (Criteria) this;
        }

        public Criteria andMalaysiaIsNull() {
            addCriterion("malaysia is null");
            return (Criteria) this;
        }

        public Criteria andMalaysiaIsNotNull() {
            addCriterion("malaysia is not null");
            return (Criteria) this;
        }

        public Criteria andMalaysiaEqualTo(Double value) {
            addCriterion("malaysia =", value, "malaysia");
            return (Criteria) this;
        }

        public Criteria andMalaysiaNotEqualTo(Double value) {
            addCriterion("malaysia <>", value, "malaysia");
            return (Criteria) this;
        }

        public Criteria andMalaysiaGreaterThan(Double value) {
            addCriterion("malaysia >", value, "malaysia");
            return (Criteria) this;
        }

        public Criteria andMalaysiaGreaterThanOrEqualTo(Double value) {
            addCriterion("malaysia >=", value, "malaysia");
            return (Criteria) this;
        }

        public Criteria andMalaysiaLessThan(Double value) {
            addCriterion("malaysia <", value, "malaysia");
            return (Criteria) this;
        }

        public Criteria andMalaysiaLessThanOrEqualTo(Double value) {
            addCriterion("malaysia <=", value, "malaysia");
            return (Criteria) this;
        }

        public Criteria andMalaysiaIn(List<Double> values) {
            addCriterion("malaysia in", values, "malaysia");
            return (Criteria) this;
        }

        public Criteria andMalaysiaNotIn(List<Double> values) {
            addCriterion("malaysia not in", values, "malaysia");
            return (Criteria) this;
        }

        public Criteria andMalaysiaBetween(Double value1, Double value2) {
            addCriterion("malaysia between", value1, value2, "malaysia");
            return (Criteria) this;
        }

        public Criteria andMalaysiaNotBetween(Double value1, Double value2) {
            addCriterion("malaysia not between", value1, value2, "malaysia");
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