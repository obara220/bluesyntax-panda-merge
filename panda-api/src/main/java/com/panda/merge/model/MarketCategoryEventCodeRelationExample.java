package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class MarketCategoryEventCodeRelationExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MarketCategoryEventCodeRelationExample() {
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

        public Criteria andMarketCategoryIdIsNull() {
            addCriterion("market_category_id is null");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdIsNotNull() {
            addCriterion("market_category_id is not null");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdEqualTo(Long value) {
            addCriterion("market_category_id =", value, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdNotEqualTo(Long value) {
            addCriterion("market_category_id <>", value, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdGreaterThan(Long value) {
            addCriterion("market_category_id >", value, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdGreaterThanOrEqualTo(Long value) {
            addCriterion("market_category_id >=", value, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdLessThan(Long value) {
            addCriterion("market_category_id <", value, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdLessThanOrEqualTo(Long value) {
            addCriterion("market_category_id <=", value, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdIn(List<Long> values) {
            addCriterion("market_category_id in", values, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdNotIn(List<Long> values) {
            addCriterion("market_category_id not in", values, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdBetween(Long value1, Long value2) {
            addCriterion("market_category_id between", value1, value2, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdNotBetween(Long value1, Long value2) {
            addCriterion("market_category_id not between", value1, value2, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andEventCodeIdIsNull() {
            addCriterion("event_code_id is null");
            return (Criteria) this;
        }

        public Criteria andEventCodeIdIsNotNull() {
            addCriterion("event_code_id is not null");
            return (Criteria) this;
        }

        public Criteria andEventCodeIdEqualTo(Long value) {
            addCriterion("event_code_id =", value, "eventCodeId");
            return (Criteria) this;
        }

        public Criteria andEventCodeIdNotEqualTo(Long value) {
            addCriterion("event_code_id <>", value, "eventCodeId");
            return (Criteria) this;
        }

        public Criteria andEventCodeIdGreaterThan(Long value) {
            addCriterion("event_code_id >", value, "eventCodeId");
            return (Criteria) this;
        }

        public Criteria andEventCodeIdGreaterThanOrEqualTo(Long value) {
            addCriterion("event_code_id >=", value, "eventCodeId");
            return (Criteria) this;
        }

        public Criteria andEventCodeIdLessThan(Long value) {
            addCriterion("event_code_id <", value, "eventCodeId");
            return (Criteria) this;
        }

        public Criteria andEventCodeIdLessThanOrEqualTo(Long value) {
            addCriterion("event_code_id <=", value, "eventCodeId");
            return (Criteria) this;
        }

        public Criteria andEventCodeIdIn(List<Long> values) {
            addCriterion("event_code_id in", values, "eventCodeId");
            return (Criteria) this;
        }

        public Criteria andEventCodeIdNotIn(List<Long> values) {
            addCriterion("event_code_id not in", values, "eventCodeId");
            return (Criteria) this;
        }

        public Criteria andEventCodeIdBetween(Long value1, Long value2) {
            addCriterion("event_code_id between", value1, value2, "eventCodeId");
            return (Criteria) this;
        }

        public Criteria andEventCodeIdNotBetween(Long value1, Long value2) {
            addCriterion("event_code_id not between", value1, value2, "eventCodeId");
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

        public Criteria andMatchPeriodIdEqualTo(Integer value) {
            addCriterion("match_period_id =", value, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdNotEqualTo(Integer value) {
            addCriterion("match_period_id <>", value, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdGreaterThan(Integer value) {
            addCriterion("match_period_id >", value, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("match_period_id >=", value, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdLessThan(Integer value) {
            addCriterion("match_period_id <", value, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdLessThanOrEqualTo(Integer value) {
            addCriterion("match_period_id <=", value, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdIn(List<Integer> values) {
            addCriterion("match_period_id in", values, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdNotIn(List<Integer> values) {
            addCriterion("match_period_id not in", values, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdBetween(Integer value1, Integer value2) {
            addCriterion("match_period_id between", value1, value2, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdNotBetween(Integer value1, Integer value2) {
            addCriterion("match_period_id not between", value1, value2, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andNormalFlagIsNull() {
            addCriterion("normal_flag is null");
            return (Criteria) this;
        }

        public Criteria andNormalFlagIsNotNull() {
            addCriterion("normal_flag is not null");
            return (Criteria) this;
        }

        public Criteria andNormalFlagEqualTo(Boolean value) {
            addCriterion("normal_flag =", value, "normalFlag");
            return (Criteria) this;
        }

        public Criteria andNormalFlagNotEqualTo(Boolean value) {
            addCriterion("normal_flag <>", value, "normalFlag");
            return (Criteria) this;
        }

        public Criteria andNormalFlagGreaterThan(Boolean value) {
            addCriterion("normal_flag >", value, "normalFlag");
            return (Criteria) this;
        }

        public Criteria andNormalFlagGreaterThanOrEqualTo(Boolean value) {
            addCriterion("normal_flag >=", value, "normalFlag");
            return (Criteria) this;
        }

        public Criteria andNormalFlagLessThan(Boolean value) {
            addCriterion("normal_flag <", value, "normalFlag");
            return (Criteria) this;
        }

        public Criteria andNormalFlagLessThanOrEqualTo(Boolean value) {
            addCriterion("normal_flag <=", value, "normalFlag");
            return (Criteria) this;
        }

        public Criteria andNormalFlagIn(List<Boolean> values) {
            addCriterion("normal_flag in", values, "normalFlag");
            return (Criteria) this;
        }

        public Criteria andNormalFlagNotIn(List<Boolean> values) {
            addCriterion("normal_flag not in", values, "normalFlag");
            return (Criteria) this;
        }

        public Criteria andNormalFlagBetween(Boolean value1, Boolean value2) {
            addCriterion("normal_flag between", value1, value2, "normalFlag");
            return (Criteria) this;
        }

        public Criteria andNormalFlagNotBetween(Boolean value1, Boolean value2) {
            addCriterion("normal_flag not between", value1, value2, "normalFlag");
            return (Criteria) this;
        }

        public Criteria andDynamicFlagIsNull() {
            addCriterion("dynamic_flag is null");
            return (Criteria) this;
        }

        public Criteria andDynamicFlagIsNotNull() {
            addCriterion("dynamic_flag is not null");
            return (Criteria) this;
        }

        public Criteria andDynamicFlagEqualTo(Boolean value) {
            addCriterion("dynamic_flag =", value, "dynamicFlag");
            return (Criteria) this;
        }

        public Criteria andDynamicFlagNotEqualTo(Boolean value) {
            addCriterion("dynamic_flag <>", value, "dynamicFlag");
            return (Criteria) this;
        }

        public Criteria andDynamicFlagGreaterThan(Boolean value) {
            addCriterion("dynamic_flag >", value, "dynamicFlag");
            return (Criteria) this;
        }

        public Criteria andDynamicFlagGreaterThanOrEqualTo(Boolean value) {
            addCriterion("dynamic_flag >=", value, "dynamicFlag");
            return (Criteria) this;
        }

        public Criteria andDynamicFlagLessThan(Boolean value) {
            addCriterion("dynamic_flag <", value, "dynamicFlag");
            return (Criteria) this;
        }

        public Criteria andDynamicFlagLessThanOrEqualTo(Boolean value) {
            addCriterion("dynamic_flag <=", value, "dynamicFlag");
            return (Criteria) this;
        }

        public Criteria andDynamicFlagIn(List<Boolean> values) {
            addCriterion("dynamic_flag in", values, "dynamicFlag");
            return (Criteria) this;
        }

        public Criteria andDynamicFlagNotIn(List<Boolean> values) {
            addCriterion("dynamic_flag not in", values, "dynamicFlag");
            return (Criteria) this;
        }

        public Criteria andDynamicFlagBetween(Boolean value1, Boolean value2) {
            addCriterion("dynamic_flag between", value1, value2, "dynamicFlag");
            return (Criteria) this;
        }

        public Criteria andDynamicFlagNotBetween(Boolean value1, Boolean value2) {
            addCriterion("dynamic_flag not between", value1, value2, "dynamicFlag");
            return (Criteria) this;
        }

        public Criteria andPeriodDynamicFlagIsNull() {
            addCriterion("period_dynamic_flag is null");
            return (Criteria) this;
        }

        public Criteria andPeriodDynamicFlagIsNotNull() {
            addCriterion("period_dynamic_flag is not null");
            return (Criteria) this;
        }

        public Criteria andPeriodDynamicFlagEqualTo(Boolean value) {
            addCriterion("period_dynamic_flag =", value, "periodDynamicFlag");
            return (Criteria) this;
        }

        public Criteria andPeriodDynamicFlagNotEqualTo(Boolean value) {
            addCriterion("period_dynamic_flag <>", value, "periodDynamicFlag");
            return (Criteria) this;
        }

        public Criteria andPeriodDynamicFlagGreaterThan(Boolean value) {
            addCriterion("period_dynamic_flag >", value, "periodDynamicFlag");
            return (Criteria) this;
        }

        public Criteria andPeriodDynamicFlagGreaterThanOrEqualTo(Boolean value) {
            addCriterion("period_dynamic_flag >=", value, "periodDynamicFlag");
            return (Criteria) this;
        }

        public Criteria andPeriodDynamicFlagLessThan(Boolean value) {
            addCriterion("period_dynamic_flag <", value, "periodDynamicFlag");
            return (Criteria) this;
        }

        public Criteria andPeriodDynamicFlagLessThanOrEqualTo(Boolean value) {
            addCriterion("period_dynamic_flag <=", value, "periodDynamicFlag");
            return (Criteria) this;
        }

        public Criteria andPeriodDynamicFlagIn(List<Boolean> values) {
            addCriterion("period_dynamic_flag in", values, "periodDynamicFlag");
            return (Criteria) this;
        }

        public Criteria andPeriodDynamicFlagNotIn(List<Boolean> values) {
            addCriterion("period_dynamic_flag not in", values, "periodDynamicFlag");
            return (Criteria) this;
        }

        public Criteria andPeriodDynamicFlagBetween(Boolean value1, Boolean value2) {
            addCriterion("period_dynamic_flag between", value1, value2, "periodDynamicFlag");
            return (Criteria) this;
        }

        public Criteria andPeriodDynamicFlagNotBetween(Boolean value1, Boolean value2) {
            addCriterion("period_dynamic_flag not between", value1, value2, "periodDynamicFlag");
            return (Criteria) this;
        }

        public Criteria andRangeFlagIsNull() {
            addCriterion("range_flag is null");
            return (Criteria) this;
        }

        public Criteria andRangeFlagIsNotNull() {
            addCriterion("range_flag is not null");
            return (Criteria) this;
        }

        public Criteria andRangeFlagEqualTo(Boolean value) {
            addCriterion("range_flag =", value, "rangeFlag");
            return (Criteria) this;
        }

        public Criteria andRangeFlagNotEqualTo(Boolean value) {
            addCriterion("range_flag <>", value, "rangeFlag");
            return (Criteria) this;
        }

        public Criteria andRangeFlagGreaterThan(Boolean value) {
            addCriterion("range_flag >", value, "rangeFlag");
            return (Criteria) this;
        }

        public Criteria andRangeFlagGreaterThanOrEqualTo(Boolean value) {
            addCriterion("range_flag >=", value, "rangeFlag");
            return (Criteria) this;
        }

        public Criteria andRangeFlagLessThan(Boolean value) {
            addCriterion("range_flag <", value, "rangeFlag");
            return (Criteria) this;
        }

        public Criteria andRangeFlagLessThanOrEqualTo(Boolean value) {
            addCriterion("range_flag <=", value, "rangeFlag");
            return (Criteria) this;
        }

        public Criteria andRangeFlagIn(List<Boolean> values) {
            addCriterion("range_flag in", values, "rangeFlag");
            return (Criteria) this;
        }

        public Criteria andRangeFlagNotIn(List<Boolean> values) {
            addCriterion("range_flag not in", values, "rangeFlag");
            return (Criteria) this;
        }

        public Criteria andRangeFlagBetween(Boolean value1, Boolean value2) {
            addCriterion("range_flag between", value1, value2, "rangeFlag");
            return (Criteria) this;
        }

        public Criteria andRangeFlagNotBetween(Boolean value1, Boolean value2) {
            addCriterion("range_flag not between", value1, value2, "rangeFlag");
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