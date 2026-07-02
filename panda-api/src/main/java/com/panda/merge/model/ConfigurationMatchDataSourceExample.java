package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationMatchDataSourceExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ConfigurationMatchDataSourceExample() {
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

        public Criteria andSrWeightIsNull() {
            addCriterion("sr_weight is null");
            return (Criteria) this;
        }

        public Criteria andSrWeightIsNotNull() {
            addCriterion("sr_weight is not null");
            return (Criteria) this;
        }

        public Criteria andSrWeightEqualTo(Integer value) {
            addCriterion("sr_weight =", value, "srWeight");
            return (Criteria) this;
        }

        public Criteria andSrWeightNotEqualTo(Integer value) {
            addCriterion("sr_weight <>", value, "srWeight");
            return (Criteria) this;
        }

        public Criteria andSrWeightGreaterThan(Integer value) {
            addCriterion("sr_weight >", value, "srWeight");
            return (Criteria) this;
        }

        public Criteria andSrWeightGreaterThanOrEqualTo(Integer value) {
            addCriterion("sr_weight >=", value, "srWeight");
            return (Criteria) this;
        }

        public Criteria andSrWeightLessThan(Integer value) {
            addCriterion("sr_weight <", value, "srWeight");
            return (Criteria) this;
        }

        public Criteria andSrWeightLessThanOrEqualTo(Integer value) {
            addCriterion("sr_weight <=", value, "srWeight");
            return (Criteria) this;
        }

        public Criteria andSrWeightIn(List<Integer> values) {
            addCriterion("sr_weight in", values, "srWeight");
            return (Criteria) this;
        }

        public Criteria andSrWeightNotIn(List<Integer> values) {
            addCriterion("sr_weight not in", values, "srWeight");
            return (Criteria) this;
        }

        public Criteria andSrWeightBetween(Integer value1, Integer value2) {
            addCriterion("sr_weight between", value1, value2, "srWeight");
            return (Criteria) this;
        }

        public Criteria andSrWeightNotBetween(Integer value1, Integer value2) {
            addCriterion("sr_weight not between", value1, value2, "srWeight");
            return (Criteria) this;
        }

        public Criteria andBcWeightIsNull() {
            addCriterion("bc_weight is null");
            return (Criteria) this;
        }

        public Criteria andBcWeightIsNotNull() {
            addCriterion("bc_weight is not null");
            return (Criteria) this;
        }

        public Criteria andBcWeightEqualTo(Integer value) {
            addCriterion("bc_weight =", value, "bcWeight");
            return (Criteria) this;
        }

        public Criteria andBcWeightNotEqualTo(Integer value) {
            addCriterion("bc_weight <>", value, "bcWeight");
            return (Criteria) this;
        }

        public Criteria andBcWeightGreaterThan(Integer value) {
            addCriterion("bc_weight >", value, "bcWeight");
            return (Criteria) this;
        }

        public Criteria andBcWeightGreaterThanOrEqualTo(Integer value) {
            addCriterion("bc_weight >=", value, "bcWeight");
            return (Criteria) this;
        }

        public Criteria andBcWeightLessThan(Integer value) {
            addCriterion("bc_weight <", value, "bcWeight");
            return (Criteria) this;
        }

        public Criteria andBcWeightLessThanOrEqualTo(Integer value) {
            addCriterion("bc_weight <=", value, "bcWeight");
            return (Criteria) this;
        }

        public Criteria andBcWeightIn(List<Integer> values) {
            addCriterion("bc_weight in", values, "bcWeight");
            return (Criteria) this;
        }

        public Criteria andBcWeightNotIn(List<Integer> values) {
            addCriterion("bc_weight not in", values, "bcWeight");
            return (Criteria) this;
        }

        public Criteria andBcWeightBetween(Integer value1, Integer value2) {
            addCriterion("bc_weight between", value1, value2, "bcWeight");
            return (Criteria) this;
        }

        public Criteria andBcWeightNotBetween(Integer value1, Integer value2) {
            addCriterion("bc_weight not between", value1, value2, "bcWeight");
            return (Criteria) this;
        }

        public Criteria andBgWeightIsNull() {
            addCriterion("bg_weight is null");
            return (Criteria) this;
        }

        public Criteria andBgWeightIsNotNull() {
            addCriterion("bg_weight is not null");
            return (Criteria) this;
        }

        public Criteria andBgWeightEqualTo(Integer value) {
            addCriterion("bg_weight =", value, "bgWeight");
            return (Criteria) this;
        }

        public Criteria andBgWeightNotEqualTo(Integer value) {
            addCriterion("bg_weight <>", value, "bgWeight");
            return (Criteria) this;
        }

        public Criteria andBgWeightGreaterThan(Integer value) {
            addCriterion("bg_weight >", value, "bgWeight");
            return (Criteria) this;
        }

        public Criteria andBgWeightGreaterThanOrEqualTo(Integer value) {
            addCriterion("bg_weight >=", value, "bgWeight");
            return (Criteria) this;
        }

        public Criteria andBgWeightLessThan(Integer value) {
            addCriterion("bg_weight <", value, "bgWeight");
            return (Criteria) this;
        }

        public Criteria andBgWeightLessThanOrEqualTo(Integer value) {
            addCriterion("bg_weight <=", value, "bgWeight");
            return (Criteria) this;
        }

        public Criteria andBgWeightIn(List<Integer> values) {
            addCriterion("bg_weight in", values, "bgWeight");
            return (Criteria) this;
        }

        public Criteria andBgWeightNotIn(List<Integer> values) {
            addCriterion("bg_weight not in", values, "bgWeight");
            return (Criteria) this;
        }

        public Criteria andBgWeightBetween(Integer value1, Integer value2) {
            addCriterion("bg_weight between", value1, value2, "bgWeight");
            return (Criteria) this;
        }

        public Criteria andBgWeightNotBetween(Integer value1, Integer value2) {
            addCriterion("bg_weight not between", value1, value2, "bgWeight");
            return (Criteria) this;
        }

        public Criteria andScoreSourceIsNull() {
            addCriterion("score_source is null");
            return (Criteria) this;
        }

        public Criteria andScoreSourceIsNotNull() {
            addCriterion("score_source is not null");
            return (Criteria) this;
        }

        public Criteria andScoreSourceEqualTo(Integer value) {
            addCriterion("score_source =", value, "scoreSource");
            return (Criteria) this;
        }

        public Criteria andScoreSourceNotEqualTo(Integer value) {
            addCriterion("score_source <>", value, "scoreSource");
            return (Criteria) this;
        }

        public Criteria andScoreSourceGreaterThan(Integer value) {
            addCriterion("score_source >", value, "scoreSource");
            return (Criteria) this;
        }

        public Criteria andScoreSourceGreaterThanOrEqualTo(Integer value) {
            addCriterion("score_source >=", value, "scoreSource");
            return (Criteria) this;
        }

        public Criteria andScoreSourceLessThan(Integer value) {
            addCriterion("score_source <", value, "scoreSource");
            return (Criteria) this;
        }

        public Criteria andScoreSourceLessThanOrEqualTo(Integer value) {
            addCriterion("score_source <=", value, "scoreSource");
            return (Criteria) this;
        }

        public Criteria andScoreSourceIn(List<Integer> values) {
            addCriterion("score_source in", values, "scoreSource");
            return (Criteria) this;
        }

        public Criteria andScoreSourceNotIn(List<Integer> values) {
            addCriterion("score_source not in", values, "scoreSource");
            return (Criteria) this;
        }

        public Criteria andScoreSourceBetween(Integer value1, Integer value2) {
            addCriterion("score_source between", value1, value2, "scoreSource");
            return (Criteria) this;
        }

        public Criteria andScoreSourceNotBetween(Integer value1, Integer value2) {
            addCriterion("score_source not between", value1, value2, "scoreSource");
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

        public Criteria andTxWeightIsNull() {
            addCriterion("tx_weight is null");
            return (Criteria) this;
        }

        public Criteria andTxWeightIsNotNull() {
            addCriterion("tx_weight is not null");
            return (Criteria) this;
        }

        public Criteria andTxWeightEqualTo(Integer value) {
            addCriterion("tx_weight =", value, "txWeight");
            return (Criteria) this;
        }

        public Criteria andTxWeightNotEqualTo(Integer value) {
            addCriterion("tx_weight <>", value, "txWeight");
            return (Criteria) this;
        }

        public Criteria andTxWeightGreaterThan(Integer value) {
            addCriterion("tx_weight >", value, "txWeight");
            return (Criteria) this;
        }

        public Criteria andTxWeightGreaterThanOrEqualTo(Integer value) {
            addCriterion("tx_weight >=", value, "txWeight");
            return (Criteria) this;
        }

        public Criteria andTxWeightLessThan(Integer value) {
            addCriterion("tx_weight <", value, "txWeight");
            return (Criteria) this;
        }

        public Criteria andTxWeightLessThanOrEqualTo(Integer value) {
            addCriterion("tx_weight <=", value, "txWeight");
            return (Criteria) this;
        }

        public Criteria andTxWeightIn(List<Integer> values) {
            addCriterion("tx_weight in", values, "txWeight");
            return (Criteria) this;
        }

        public Criteria andTxWeightNotIn(List<Integer> values) {
            addCriterion("tx_weight not in", values, "txWeight");
            return (Criteria) this;
        }

        public Criteria andTxWeightBetween(Integer value1, Integer value2) {
            addCriterion("tx_weight between", value1, value2, "txWeight");
            return (Criteria) this;
        }

        public Criteria andTxWeightNotBetween(Integer value1, Integer value2) {
            addCriterion("tx_weight not between", value1, value2, "txWeight");
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