package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class ConfigMarketTradeItemLogExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ConfigMarketTradeItemLogExample() {
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

        public Criteria andMarketIdIsNull() {
            addCriterion("market_id is null");
            return (Criteria) this;
        }

        public Criteria andMarketIdIsNotNull() {
            addCriterion("market_id is not null");
            return (Criteria) this;
        }

        public Criteria andMarketIdEqualTo(Long value) {
            addCriterion("market_id =", value, "marketId");
            return (Criteria) this;
        }

        public Criteria andMarketIdNotEqualTo(Long value) {
            addCriterion("market_id <>", value, "marketId");
            return (Criteria) this;
        }

        public Criteria andMarketIdGreaterThan(Long value) {
            addCriterion("market_id >", value, "marketId");
            return (Criteria) this;
        }

        public Criteria andMarketIdGreaterThanOrEqualTo(Long value) {
            addCriterion("market_id >=", value, "marketId");
            return (Criteria) this;
        }

        public Criteria andMarketIdLessThan(Long value) {
            addCriterion("market_id <", value, "marketId");
            return (Criteria) this;
        }

        public Criteria andMarketIdLessThanOrEqualTo(Long value) {
            addCriterion("market_id <=", value, "marketId");
            return (Criteria) this;
        }

        public Criteria andMarketIdIn(List<Long> values) {
            addCriterion("market_id in", values, "marketId");
            return (Criteria) this;
        }

        public Criteria andMarketIdNotIn(List<Long> values) {
            addCriterion("market_id not in", values, "marketId");
            return (Criteria) this;
        }

        public Criteria andMarketIdBetween(Long value1, Long value2) {
            addCriterion("market_id between", value1, value2, "marketId");
            return (Criteria) this;
        }

        public Criteria andMarketIdNotBetween(Long value1, Long value2) {
            addCriterion("market_id not between", value1, value2, "marketId");
            return (Criteria) this;
        }

        public Criteria andMatchIdIsNull() {
            addCriterion("match_id is null");
            return (Criteria) this;
        }

        public Criteria andMatchIdIsNotNull() {
            addCriterion("match_id is not null");
            return (Criteria) this;
        }

        public Criteria andMatchIdEqualTo(Long value) {
            addCriterion("match_id =", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdNotEqualTo(Long value) {
            addCriterion("match_id <>", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdGreaterThan(Long value) {
            addCriterion("match_id >", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdGreaterThanOrEqualTo(Long value) {
            addCriterion("match_id >=", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdLessThan(Long value) {
            addCriterion("match_id <", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdLessThanOrEqualTo(Long value) {
            addCriterion("match_id <=", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdIn(List<Long> values) {
            addCriterion("match_id in", values, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdNotIn(List<Long> values) {
            addCriterion("match_id not in", values, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdBetween(Long value1, Long value2) {
            addCriterion("match_id between", value1, value2, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdNotBetween(Long value1, Long value2) {
            addCriterion("match_id not between", value1, value2, "matchId");
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

        public Criteria andPlaceNumIsNull() {
            addCriterion("place_num is null");
            return (Criteria) this;
        }

        public Criteria andPlaceNumIsNotNull() {
            addCriterion("place_num is not null");
            return (Criteria) this;
        }

        public Criteria andPlaceNumEqualTo(Integer value) {
            addCriterion("place_num =", value, "placeNum");
            return (Criteria) this;
        }

        public Criteria andPlaceNumNotEqualTo(Integer value) {
            addCriterion("place_num <>", value, "placeNum");
            return (Criteria) this;
        }

        public Criteria andPlaceNumGreaterThan(Integer value) {
            addCriterion("place_num >", value, "placeNum");
            return (Criteria) this;
        }

        public Criteria andPlaceNumGreaterThanOrEqualTo(Integer value) {
            addCriterion("place_num >=", value, "placeNum");
            return (Criteria) this;
        }

        public Criteria andPlaceNumLessThan(Integer value) {
            addCriterion("place_num <", value, "placeNum");
            return (Criteria) this;
        }

        public Criteria andPlaceNumLessThanOrEqualTo(Integer value) {
            addCriterion("place_num <=", value, "placeNum");
            return (Criteria) this;
        }

        public Criteria andPlaceNumIn(List<Integer> values) {
            addCriterion("place_num in", values, "placeNum");
            return (Criteria) this;
        }

        public Criteria andPlaceNumNotIn(List<Integer> values) {
            addCriterion("place_num not in", values, "placeNum");
            return (Criteria) this;
        }

        public Criteria andPlaceNumBetween(Integer value1, Integer value2) {
            addCriterion("place_num between", value1, value2, "placeNum");
            return (Criteria) this;
        }

        public Criteria andPlaceNumNotBetween(Integer value1, Integer value2) {
            addCriterion("place_num not between", value1, value2, "placeNum");
            return (Criteria) this;
        }

        public Criteria andMaxOddsValueIsNull() {
            addCriterion("max_odds_value is null");
            return (Criteria) this;
        }

        public Criteria andMaxOddsValueIsNotNull() {
            addCriterion("max_odds_value is not null");
            return (Criteria) this;
        }

        public Criteria andMaxOddsValueEqualTo(Double value) {
            addCriterion("max_odds_value =", value, "maxOddsValue");
            return (Criteria) this;
        }

        public Criteria andMaxOddsValueNotEqualTo(Double value) {
            addCriterion("max_odds_value <>", value, "maxOddsValue");
            return (Criteria) this;
        }

        public Criteria andMaxOddsValueGreaterThan(Double value) {
            addCriterion("max_odds_value >", value, "maxOddsValue");
            return (Criteria) this;
        }

        public Criteria andMaxOddsValueGreaterThanOrEqualTo(Double value) {
            addCriterion("max_odds_value >=", value, "maxOddsValue");
            return (Criteria) this;
        }

        public Criteria andMaxOddsValueLessThan(Double value) {
            addCriterion("max_odds_value <", value, "maxOddsValue");
            return (Criteria) this;
        }

        public Criteria andMaxOddsValueLessThanOrEqualTo(Double value) {
            addCriterion("max_odds_value <=", value, "maxOddsValue");
            return (Criteria) this;
        }

        public Criteria andMaxOddsValueIn(List<Double> values) {
            addCriterion("max_odds_value in", values, "maxOddsValue");
            return (Criteria) this;
        }

        public Criteria andMaxOddsValueNotIn(List<Double> values) {
            addCriterion("max_odds_value not in", values, "maxOddsValue");
            return (Criteria) this;
        }

        public Criteria andMaxOddsValueBetween(Double value1, Double value2) {
            addCriterion("max_odds_value between", value1, value2, "maxOddsValue");
            return (Criteria) this;
        }

        public Criteria andMaxOddsValueNotBetween(Double value1, Double value2) {
            addCriterion("max_odds_value not between", value1, value2, "maxOddsValue");
            return (Criteria) this;
        }

        public Criteria andMinOddsValueIsNull() {
            addCriterion("min_odds_value is null");
            return (Criteria) this;
        }

        public Criteria andMinOddsValueIsNotNull() {
            addCriterion("min_odds_value is not null");
            return (Criteria) this;
        }

        public Criteria andMinOddsValueEqualTo(Double value) {
            addCriterion("min_odds_value =", value, "minOddsValue");
            return (Criteria) this;
        }

        public Criteria andMinOddsValueNotEqualTo(Double value) {
            addCriterion("min_odds_value <>", value, "minOddsValue");
            return (Criteria) this;
        }

        public Criteria andMinOddsValueGreaterThan(Double value) {
            addCriterion("min_odds_value >", value, "minOddsValue");
            return (Criteria) this;
        }

        public Criteria andMinOddsValueGreaterThanOrEqualTo(Double value) {
            addCriterion("min_odds_value >=", value, "minOddsValue");
            return (Criteria) this;
        }

        public Criteria andMinOddsValueLessThan(Double value) {
            addCriterion("min_odds_value <", value, "minOddsValue");
            return (Criteria) this;
        }

        public Criteria andMinOddsValueLessThanOrEqualTo(Double value) {
            addCriterion("min_odds_value <=", value, "minOddsValue");
            return (Criteria) this;
        }

        public Criteria andMinOddsValueIn(List<Double> values) {
            addCriterion("min_odds_value in", values, "minOddsValue");
            return (Criteria) this;
        }

        public Criteria andMinOddsValueNotIn(List<Double> values) {
            addCriterion("min_odds_value not in", values, "minOddsValue");
            return (Criteria) this;
        }

        public Criteria andMinOddsValueBetween(Double value1, Double value2) {
            addCriterion("min_odds_value between", value1, value2, "minOddsValue");
            return (Criteria) this;
        }

        public Criteria andMinOddsValueNotBetween(Double value1, Double value2) {
            addCriterion("min_odds_value not between", value1, value2, "minOddsValue");
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

        public Criteria andOperaterIdIsNull() {
            addCriterion("operater_id is null");
            return (Criteria) this;
        }

        public Criteria andOperaterIdIsNotNull() {
            addCriterion("operater_id is not null");
            return (Criteria) this;
        }

        public Criteria andOperaterIdEqualTo(Long value) {
            addCriterion("operater_id =", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdNotEqualTo(Long value) {
            addCriterion("operater_id <>", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdGreaterThan(Long value) {
            addCriterion("operater_id >", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdGreaterThanOrEqualTo(Long value) {
            addCriterion("operater_id >=", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdLessThan(Long value) {
            addCriterion("operater_id <", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdLessThanOrEqualTo(Long value) {
            addCriterion("operater_id <=", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdIn(List<Long> values) {
            addCriterion("operater_id in", values, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdNotIn(List<Long> values) {
            addCriterion("operater_id not in", values, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdBetween(Long value1, Long value2) {
            addCriterion("operater_id between", value1, value2, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdNotBetween(Long value1, Long value2) {
            addCriterion("operater_id not between", value1, value2, "operaterId");
            return (Criteria) this;
        }

        public Criteria andChildStandardCategoryIdIsNull() {
            addCriterion("child_standard_category_id is null");
            return (Criteria) this;
        }

        public Criteria andChildStandardCategoryIdIsNotNull() {
            addCriterion("child_standard_category_id is not null");
            return (Criteria) this;
        }

        public Criteria andChildStandardCategoryIdEqualTo(Long value) {
            addCriterion("child_standard_category_id =", value, "childStandardCategoryId");
            return (Criteria) this;
        }

        public Criteria andChildStandardCategoryIdNotEqualTo(Long value) {
            addCriterion("child_standard_category_id <>", value, "childStandardCategoryId");
            return (Criteria) this;
        }

        public Criteria andChildStandardCategoryIdGreaterThan(Long value) {
            addCriterion("child_standard_category_id >", value, "childStandardCategoryId");
            return (Criteria) this;
        }

        public Criteria andChildStandardCategoryIdGreaterThanOrEqualTo(Long value) {
            addCriterion("child_standard_category_id >=", value, "childStandardCategoryId");
            return (Criteria) this;
        }

        public Criteria andChildStandardCategoryIdLessThan(Long value) {
            addCriterion("child_standard_category_id <", value, "childStandardCategoryId");
            return (Criteria) this;
        }

        public Criteria andChildStandardCategoryIdLessThanOrEqualTo(Long value) {
            addCriterion("child_standard_category_id <=", value, "childStandardCategoryId");
            return (Criteria) this;
        }

        public Criteria andChildStandardCategoryIdIn(List<Long> values) {
            addCriterion("child_standard_category_id in", values, "childStandardCategoryId");
            return (Criteria) this;
        }

        public Criteria andChildStandardCategoryIdNotIn(List<Long> values) {
            addCriterion("child_standard_category_id not in", values, "childStandardCategoryId");
            return (Criteria) this;
        }

        public Criteria andChildStandardCategoryIdBetween(Long value1, Long value2) {
            addCriterion("child_standard_category_id between", value1, value2, "childStandardCategoryId");
            return (Criteria) this;
        }

        public Criteria andChildStandardCategoryIdNotBetween(Long value1, Long value2) {
            addCriterion("child_standard_category_id not between", value1, value2, "childStandardCategoryId");
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