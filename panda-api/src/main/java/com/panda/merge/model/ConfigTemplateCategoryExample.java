package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class ConfigTemplateCategoryExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ConfigTemplateCategoryExample() {
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

        public Criteria andTemplateIdIsNull() {
            addCriterion("template_id is null");
            return (Criteria) this;
        }

        public Criteria andTemplateIdIsNotNull() {
            addCriterion("template_id is not null");
            return (Criteria) this;
        }

        public Criteria andTemplateIdEqualTo(Long value) {
            addCriterion("template_id =", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdNotEqualTo(Long value) {
            addCriterion("template_id <>", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdGreaterThan(Long value) {
            addCriterion("template_id >", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdGreaterThanOrEqualTo(Long value) {
            addCriterion("template_id >=", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdLessThan(Long value) {
            addCriterion("template_id <", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdLessThanOrEqualTo(Long value) {
            addCriterion("template_id <=", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdIn(List<Long> values) {
            addCriterion("template_id in", values, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdNotIn(List<Long> values) {
            addCriterion("template_id not in", values, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdBetween(Long value1, Long value2) {
            addCriterion("template_id between", value1, value2, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdNotBetween(Long value1, Long value2) {
            addCriterion("template_id not between", value1, value2, "templateId");
            return (Criteria) this;
        }

        public Criteria andStandardCategoryIdIsNull() {
            addCriterion("standard_category_id is null");
            return (Criteria) this;
        }

        public Criteria andStandardCategoryIdIsNotNull() {
            addCriterion("standard_category_id is not null");
            return (Criteria) this;
        }

        public Criteria andStandardCategoryIdEqualTo(Long value) {
            addCriterion("standard_category_id =", value, "standardCategoryId");
            return (Criteria) this;
        }

        public Criteria andStandardCategoryIdNotEqualTo(Long value) {
            addCriterion("standard_category_id <>", value, "standardCategoryId");
            return (Criteria) this;
        }

        public Criteria andStandardCategoryIdGreaterThan(Long value) {
            addCriterion("standard_category_id >", value, "standardCategoryId");
            return (Criteria) this;
        }

        public Criteria andStandardCategoryIdGreaterThanOrEqualTo(Long value) {
            addCriterion("standard_category_id >=", value, "standardCategoryId");
            return (Criteria) this;
        }

        public Criteria andStandardCategoryIdLessThan(Long value) {
            addCriterion("standard_category_id <", value, "standardCategoryId");
            return (Criteria) this;
        }

        public Criteria andStandardCategoryIdLessThanOrEqualTo(Long value) {
            addCriterion("standard_category_id <=", value, "standardCategoryId");
            return (Criteria) this;
        }

        public Criteria andStandardCategoryIdIn(List<Long> values) {
            addCriterion("standard_category_id in", values, "standardCategoryId");
            return (Criteria) this;
        }

        public Criteria andStandardCategoryIdNotIn(List<Long> values) {
            addCriterion("standard_category_id not in", values, "standardCategoryId");
            return (Criteria) this;
        }

        public Criteria andStandardCategoryIdBetween(Long value1, Long value2) {
            addCriterion("standard_category_id between", value1, value2, "standardCategoryId");
            return (Criteria) this;
        }

        public Criteria andStandardCategoryIdNotBetween(Long value1, Long value2) {
            addCriterion("standard_category_id not between", value1, value2, "standardCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketOddsTypeIsNull() {
            addCriterion("market_odds_type is null");
            return (Criteria) this;
        }

        public Criteria andMarketOddsTypeIsNotNull() {
            addCriterion("market_odds_type is not null");
            return (Criteria) this;
        }

        public Criteria andMarketOddsTypeEqualTo(String value) {
            addCriterion("market_odds_type =", value, "marketOddsType");
            return (Criteria) this;
        }

        public Criteria andMarketOddsTypeNotEqualTo(String value) {
            addCriterion("market_odds_type <>", value, "marketOddsType");
            return (Criteria) this;
        }

        public Criteria andMarketOddsTypeGreaterThan(String value) {
            addCriterion("market_odds_type >", value, "marketOddsType");
            return (Criteria) this;
        }

        public Criteria andMarketOddsTypeGreaterThanOrEqualTo(String value) {
            addCriterion("market_odds_type >=", value, "marketOddsType");
            return (Criteria) this;
        }

        public Criteria andMarketOddsTypeLessThan(String value) {
            addCriterion("market_odds_type <", value, "marketOddsType");
            return (Criteria) this;
        }

        public Criteria andMarketOddsTypeLessThanOrEqualTo(String value) {
            addCriterion("market_odds_type <=", value, "marketOddsType");
            return (Criteria) this;
        }

        public Criteria andMarketOddsTypeLike(String value) {
            addCriterion("market_odds_type like", value, "marketOddsType");
            return (Criteria) this;
        }

        public Criteria andMarketOddsTypeNotLike(String value) {
            addCriterion("market_odds_type not like", value, "marketOddsType");
            return (Criteria) this;
        }

        public Criteria andMarketOddsTypeIn(List<String> values) {
            addCriterion("market_odds_type in", values, "marketOddsType");
            return (Criteria) this;
        }

        public Criteria andMarketOddsTypeNotIn(List<String> values) {
            addCriterion("market_odds_type not in", values, "marketOddsType");
            return (Criteria) this;
        }

        public Criteria andMarketOddsTypeBetween(String value1, String value2) {
            addCriterion("market_odds_type between", value1, value2, "marketOddsType");
            return (Criteria) this;
        }

        public Criteria andMarketOddsTypeNotBetween(String value1, String value2) {
            addCriterion("market_odds_type not between", value1, value2, "marketOddsType");
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

        public Criteria andMatchPeriodIdEqualTo(Long value) {
            addCriterion("match_period_id =", value, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdNotEqualTo(Long value) {
            addCriterion("match_period_id <>", value, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdGreaterThan(Long value) {
            addCriterion("match_period_id >", value, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdGreaterThanOrEqualTo(Long value) {
            addCriterion("match_period_id >=", value, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdLessThan(Long value) {
            addCriterion("match_period_id <", value, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdLessThanOrEqualTo(Long value) {
            addCriterion("match_period_id <=", value, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdIn(List<Long> values) {
            addCriterion("match_period_id in", values, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdNotIn(List<Long> values) {
            addCriterion("match_period_id not in", values, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdBetween(Long value1, Long value2) {
            addCriterion("match_period_id between", value1, value2, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdNotBetween(Long value1, Long value2) {
            addCriterion("match_period_id not between", value1, value2, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchProgressTimeIsNull() {
            addCriterion("match_progress_time is null");
            return (Criteria) this;
        }

        public Criteria andMatchProgressTimeIsNotNull() {
            addCriterion("match_progress_time is not null");
            return (Criteria) this;
        }

        public Criteria andMatchProgressTimeEqualTo(Long value) {
            addCriterion("match_progress_time =", value, "matchProgressTime");
            return (Criteria) this;
        }

        public Criteria andMatchProgressTimeNotEqualTo(Long value) {
            addCriterion("match_progress_time <>", value, "matchProgressTime");
            return (Criteria) this;
        }

        public Criteria andMatchProgressTimeGreaterThan(Long value) {
            addCriterion("match_progress_time >", value, "matchProgressTime");
            return (Criteria) this;
        }

        public Criteria andMatchProgressTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("match_progress_time >=", value, "matchProgressTime");
            return (Criteria) this;
        }

        public Criteria andMatchProgressTimeLessThan(Long value) {
            addCriterion("match_progress_time <", value, "matchProgressTime");
            return (Criteria) this;
        }

        public Criteria andMatchProgressTimeLessThanOrEqualTo(Long value) {
            addCriterion("match_progress_time <=", value, "matchProgressTime");
            return (Criteria) this;
        }

        public Criteria andMatchProgressTimeIn(List<Long> values) {
            addCriterion("match_progress_time in", values, "matchProgressTime");
            return (Criteria) this;
        }

        public Criteria andMatchProgressTimeNotIn(List<Long> values) {
            addCriterion("match_progress_time not in", values, "matchProgressTime");
            return (Criteria) this;
        }

        public Criteria andMatchProgressTimeBetween(Long value1, Long value2) {
            addCriterion("match_progress_time between", value1, value2, "matchProgressTime");
            return (Criteria) this;
        }

        public Criteria andMatchProgressTimeNotBetween(Long value1, Long value2) {
            addCriterion("match_progress_time not between", value1, value2, "matchProgressTime");
            return (Criteria) this;
        }

        public Criteria andInjuryTimeIsNull() {
            addCriterion("injury_time is null");
            return (Criteria) this;
        }

        public Criteria andInjuryTimeIsNotNull() {
            addCriterion("injury_time is not null");
            return (Criteria) this;
        }

        public Criteria andInjuryTimeEqualTo(Long value) {
            addCriterion("injury_time =", value, "injuryTime");
            return (Criteria) this;
        }

        public Criteria andInjuryTimeNotEqualTo(Long value) {
            addCriterion("injury_time <>", value, "injuryTime");
            return (Criteria) this;
        }

        public Criteria andInjuryTimeGreaterThan(Long value) {
            addCriterion("injury_time >", value, "injuryTime");
            return (Criteria) this;
        }

        public Criteria andInjuryTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("injury_time >=", value, "injuryTime");
            return (Criteria) this;
        }

        public Criteria andInjuryTimeLessThan(Long value) {
            addCriterion("injury_time <", value, "injuryTime");
            return (Criteria) this;
        }

        public Criteria andInjuryTimeLessThanOrEqualTo(Long value) {
            addCriterion("injury_time <=", value, "injuryTime");
            return (Criteria) this;
        }

        public Criteria andInjuryTimeIn(List<Long> values) {
            addCriterion("injury_time in", values, "injuryTime");
            return (Criteria) this;
        }

        public Criteria andInjuryTimeNotIn(List<Long> values) {
            addCriterion("injury_time not in", values, "injuryTime");
            return (Criteria) this;
        }

        public Criteria andInjuryTimeBetween(Long value1, Long value2) {
            addCriterion("injury_time between", value1, value2, "injuryTime");
            return (Criteria) this;
        }

        public Criteria andInjuryTimeNotBetween(Long value1, Long value2) {
            addCriterion("injury_time not between", value1, value2, "injuryTime");
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

        public Criteria andCanceledIsNull() {
            addCriterion("canceled is null");
            return (Criteria) this;
        }

        public Criteria andCanceledIsNotNull() {
            addCriterion("canceled is not null");
            return (Criteria) this;
        }

        public Criteria andCanceledEqualTo(Integer value) {
            addCriterion("canceled =", value, "canceled");
            return (Criteria) this;
        }

        public Criteria andCanceledNotEqualTo(Integer value) {
            addCriterion("canceled <>", value, "canceled");
            return (Criteria) this;
        }

        public Criteria andCanceledGreaterThan(Integer value) {
            addCriterion("canceled >", value, "canceled");
            return (Criteria) this;
        }

        public Criteria andCanceledGreaterThanOrEqualTo(Integer value) {
            addCriterion("canceled >=", value, "canceled");
            return (Criteria) this;
        }

        public Criteria andCanceledLessThan(Integer value) {
            addCriterion("canceled <", value, "canceled");
            return (Criteria) this;
        }

        public Criteria andCanceledLessThanOrEqualTo(Integer value) {
            addCriterion("canceled <=", value, "canceled");
            return (Criteria) this;
        }

        public Criteria andCanceledIn(List<Integer> values) {
            addCriterion("canceled in", values, "canceled");
            return (Criteria) this;
        }

        public Criteria andCanceledNotIn(List<Integer> values) {
            addCriterion("canceled not in", values, "canceled");
            return (Criteria) this;
        }

        public Criteria andCanceledBetween(Integer value1, Integer value2) {
            addCriterion("canceled between", value1, value2, "canceled");
            return (Criteria) this;
        }

        public Criteria andCanceledNotBetween(Integer value1, Integer value2) {
            addCriterion("canceled not between", value1, value2, "canceled");
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