package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class ConfigCashOutTradeItemExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ConfigCashOutTradeItemExample() {
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

        public Criteria andMarketTypeIsNull() {
            addCriterion("market_type is null");
            return (Criteria) this;
        }

        public Criteria andMarketTypeIsNotNull() {
            addCriterion("market_type is not null");
            return (Criteria) this;
        }

        public Criteria andMarketTypeEqualTo(Integer value) {
            addCriterion("market_type =", value, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeNotEqualTo(Integer value) {
            addCriterion("market_type <>", value, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeGreaterThan(Integer value) {
            addCriterion("market_type >", value, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("market_type >=", value, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeLessThan(Integer value) {
            addCriterion("market_type <", value, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeLessThanOrEqualTo(Integer value) {
            addCriterion("market_type <=", value, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeIn(List<Integer> values) {
            addCriterion("market_type in", values, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeNotIn(List<Integer> values) {
            addCriterion("market_type not in", values, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeBetween(Integer value1, Integer value2) {
            addCriterion("market_type between", value1, value2, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("market_type not between", value1, value2, "marketType");
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

        public Criteria andMatchPreStatusIsNull() {
            addCriterion("match_pre_status is null");
            return (Criteria) this;
        }

        public Criteria andMatchPreStatusIsNotNull() {
            addCriterion("match_pre_status is not null");
            return (Criteria) this;
        }

        public Criteria andMatchPreStatusEqualTo(Integer value) {
            addCriterion("match_pre_status =", value, "matchPreStatus");
            return (Criteria) this;
        }

        public Criteria andMatchPreStatusNotEqualTo(Integer value) {
            addCriterion("match_pre_status <>", value, "matchPreStatus");
            return (Criteria) this;
        }

        public Criteria andMatchPreStatusGreaterThan(Integer value) {
            addCriterion("match_pre_status >", value, "matchPreStatus");
            return (Criteria) this;
        }

        public Criteria andMatchPreStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("match_pre_status >=", value, "matchPreStatus");
            return (Criteria) this;
        }

        public Criteria andMatchPreStatusLessThan(Integer value) {
            addCriterion("match_pre_status <", value, "matchPreStatus");
            return (Criteria) this;
        }

        public Criteria andMatchPreStatusLessThanOrEqualTo(Integer value) {
            addCriterion("match_pre_status <=", value, "matchPreStatus");
            return (Criteria) this;
        }

        public Criteria andMatchPreStatusIn(List<Integer> values) {
            addCriterion("match_pre_status in", values, "matchPreStatus");
            return (Criteria) this;
        }

        public Criteria andMatchPreStatusNotIn(List<Integer> values) {
            addCriterion("match_pre_status not in", values, "matchPreStatus");
            return (Criteria) this;
        }

        public Criteria andMatchPreStatusBetween(Integer value1, Integer value2) {
            addCriterion("match_pre_status between", value1, value2, "matchPreStatus");
            return (Criteria) this;
        }

        public Criteria andMatchPreStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("match_pre_status not between", value1, value2, "matchPreStatus");
            return (Criteria) this;
        }

        public Criteria andCategoryPreStatusIsNull() {
            addCriterion("category_pre_status is null");
            return (Criteria) this;
        }

        public Criteria andCategoryPreStatusIsNotNull() {
            addCriterion("category_pre_status is not null");
            return (Criteria) this;
        }

        public Criteria andCategoryPreStatusEqualTo(Integer value) {
            addCriterion("category_pre_status =", value, "categoryPreStatus");
            return (Criteria) this;
        }

        public Criteria andCategoryPreStatusNotEqualTo(Integer value) {
            addCriterion("category_pre_status <>", value, "categoryPreStatus");
            return (Criteria) this;
        }

        public Criteria andCategoryPreStatusGreaterThan(Integer value) {
            addCriterion("category_pre_status >", value, "categoryPreStatus");
            return (Criteria) this;
        }

        public Criteria andCategoryPreStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("category_pre_status >=", value, "categoryPreStatus");
            return (Criteria) this;
        }

        public Criteria andCategoryPreStatusLessThan(Integer value) {
            addCriterion("category_pre_status <", value, "categoryPreStatus");
            return (Criteria) this;
        }

        public Criteria andCategoryPreStatusLessThanOrEqualTo(Integer value) {
            addCriterion("category_pre_status <=", value, "categoryPreStatus");
            return (Criteria) this;
        }

        public Criteria andCategoryPreStatusIn(List<Integer> values) {
            addCriterion("category_pre_status in", values, "categoryPreStatus");
            return (Criteria) this;
        }

        public Criteria andCategoryPreStatusNotIn(List<Integer> values) {
            addCriterion("category_pre_status not in", values, "categoryPreStatus");
            return (Criteria) this;
        }

        public Criteria andCategoryPreStatusBetween(Integer value1, Integer value2) {
            addCriterion("category_pre_status between", value1, value2, "categoryPreStatus");
            return (Criteria) this;
        }

        public Criteria andCategoryPreStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("category_pre_status not between", value1, value2, "categoryPreStatus");
            return (Criteria) this;
        }

        public Criteria andCashOutMarginIsNull() {
            addCriterion("cash_out_margin is null");
            return (Criteria) this;
        }

        public Criteria andCashOutMarginIsNotNull() {
            addCriterion("cash_out_margin is not null");
            return (Criteria) this;
        }

        public Criteria andCashOutMarginEqualTo(Long value) {
            addCriterion("cash_out_margin =", value, "cashOutMargin");
            return (Criteria) this;
        }

        public Criteria andCashOutMarginNotEqualTo(Long value) {
            addCriterion("cash_out_margin <>", value, "cashOutMargin");
            return (Criteria) this;
        }

        public Criteria andCashOutMarginGreaterThan(Long value) {
            addCriterion("cash_out_margin >", value, "cashOutMargin");
            return (Criteria) this;
        }

        public Criteria andCashOutMarginGreaterThanOrEqualTo(Long value) {
            addCriterion("cash_out_margin >=", value, "cashOutMargin");
            return (Criteria) this;
        }

        public Criteria andCashOutMarginLessThan(Long value) {
            addCriterion("cash_out_margin <", value, "cashOutMargin");
            return (Criteria) this;
        }

        public Criteria andCashOutMarginLessThanOrEqualTo(Long value) {
            addCriterion("cash_out_margin <=", value, "cashOutMargin");
            return (Criteria) this;
        }

        public Criteria andCashOutMarginIn(List<Long> values) {
            addCriterion("cash_out_margin in", values, "cashOutMargin");
            return (Criteria) this;
        }

        public Criteria andCashOutMarginNotIn(List<Long> values) {
            addCriterion("cash_out_margin not in", values, "cashOutMargin");
            return (Criteria) this;
        }

        public Criteria andCashOutMarginBetween(Long value1, Long value2) {
            addCriterion("cash_out_margin between", value1, value2, "cashOutMargin");
            return (Criteria) this;
        }

        public Criteria andCashOutMarginNotBetween(Long value1, Long value2) {
            addCriterion("cash_out_margin not between", value1, value2, "cashOutMargin");
            return (Criteria) this;
        }

        public Criteria andLeveIsNull() {
            addCriterion("leve is null");
            return (Criteria) this;
        }

        public Criteria andLeveIsNotNull() {
            addCriterion("leve is not null");
            return (Criteria) this;
        }

        public Criteria andLeveEqualTo(Integer value) {
            addCriterion("leve =", value, "leve");
            return (Criteria) this;
        }

        public Criteria andLeveNotEqualTo(Integer value) {
            addCriterion("leve <>", value, "leve");
            return (Criteria) this;
        }

        public Criteria andLeveGreaterThan(Integer value) {
            addCriterion("leve >", value, "leve");
            return (Criteria) this;
        }

        public Criteria andLeveGreaterThanOrEqualTo(Integer value) {
            addCriterion("leve >=", value, "leve");
            return (Criteria) this;
        }

        public Criteria andLeveLessThan(Integer value) {
            addCriterion("leve <", value, "leve");
            return (Criteria) this;
        }

        public Criteria andLeveLessThanOrEqualTo(Integer value) {
            addCriterion("leve <=", value, "leve");
            return (Criteria) this;
        }

        public Criteria andLeveIn(List<Integer> values) {
            addCriterion("leve in", values, "leve");
            return (Criteria) this;
        }

        public Criteria andLeveNotIn(List<Integer> values) {
            addCriterion("leve not in", values, "leve");
            return (Criteria) this;
        }

        public Criteria andLeveBetween(Integer value1, Integer value2) {
            addCriterion("leve between", value1, value2, "leve");
            return (Criteria) this;
        }

        public Criteria andLeveNotBetween(Integer value1, Integer value2) {
            addCriterion("leve not between", value1, value2, "leve");
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