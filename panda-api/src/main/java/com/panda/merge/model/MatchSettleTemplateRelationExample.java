package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class MatchSettleTemplateRelationExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MatchSettleTemplateRelationExample() {
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

        public Criteria andTemplateSettleWeightIdIsNull() {
            addCriterion("template_settle_weight_id is null");
            return (Criteria) this;
        }

        public Criteria andTemplateSettleWeightIdIsNotNull() {
            addCriterion("template_settle_weight_id is not null");
            return (Criteria) this;
        }

        public Criteria andTemplateSettleWeightIdEqualTo(Long value) {
            addCriterion("template_settle_weight_id =", value, "templateSettleWeightId");
            return (Criteria) this;
        }

        public Criteria andTemplateSettleWeightIdNotEqualTo(Long value) {
            addCriterion("template_settle_weight_id <>", value, "templateSettleWeightId");
            return (Criteria) this;
        }

        public Criteria andTemplateSettleWeightIdGreaterThan(Long value) {
            addCriterion("template_settle_weight_id >", value, "templateSettleWeightId");
            return (Criteria) this;
        }

        public Criteria andTemplateSettleWeightIdGreaterThanOrEqualTo(Long value) {
            addCriterion("template_settle_weight_id >=", value, "templateSettleWeightId");
            return (Criteria) this;
        }

        public Criteria andTemplateSettleWeightIdLessThan(Long value) {
            addCriterion("template_settle_weight_id <", value, "templateSettleWeightId");
            return (Criteria) this;
        }

        public Criteria andTemplateSettleWeightIdLessThanOrEqualTo(Long value) {
            addCriterion("template_settle_weight_id <=", value, "templateSettleWeightId");
            return (Criteria) this;
        }

        public Criteria andTemplateSettleWeightIdIn(List<Long> values) {
            addCriterion("template_settle_weight_id in", values, "templateSettleWeightId");
            return (Criteria) this;
        }

        public Criteria andTemplateSettleWeightIdNotIn(List<Long> values) {
            addCriterion("template_settle_weight_id not in", values, "templateSettleWeightId");
            return (Criteria) this;
        }

        public Criteria andTemplateSettleWeightIdBetween(Long value1, Long value2) {
            addCriterion("template_settle_weight_id between", value1, value2, "templateSettleWeightId");
            return (Criteria) this;
        }

        public Criteria andTemplateSettleWeightIdNotBetween(Long value1, Long value2) {
            addCriterion("template_settle_weight_id not between", value1, value2, "templateSettleWeightId");
            return (Criteria) this;
        }

        public Criteria andTemplateCountDowenIdIsNull() {
            addCriterion("template_count_dowen_id is null");
            return (Criteria) this;
        }

        public Criteria andTemplateCountDowenIdIsNotNull() {
            addCriterion("template_count_dowen_id is not null");
            return (Criteria) this;
        }

        public Criteria andTemplateCountDowenIdEqualTo(Long value) {
            addCriterion("template_count_dowen_id =", value, "templateCountDowenId");
            return (Criteria) this;
        }

        public Criteria andTemplateCountDowenIdNotEqualTo(Long value) {
            addCriterion("template_count_dowen_id <>", value, "templateCountDowenId");
            return (Criteria) this;
        }

        public Criteria andTemplateCountDowenIdGreaterThan(Long value) {
            addCriterion("template_count_dowen_id >", value, "templateCountDowenId");
            return (Criteria) this;
        }

        public Criteria andTemplateCountDowenIdGreaterThanOrEqualTo(Long value) {
            addCriterion("template_count_dowen_id >=", value, "templateCountDowenId");
            return (Criteria) this;
        }

        public Criteria andTemplateCountDowenIdLessThan(Long value) {
            addCriterion("template_count_dowen_id <", value, "templateCountDowenId");
            return (Criteria) this;
        }

        public Criteria andTemplateCountDowenIdLessThanOrEqualTo(Long value) {
            addCriterion("template_count_dowen_id <=", value, "templateCountDowenId");
            return (Criteria) this;
        }

        public Criteria andTemplateCountDowenIdIn(List<Long> values) {
            addCriterion("template_count_dowen_id in", values, "templateCountDowenId");
            return (Criteria) this;
        }

        public Criteria andTemplateCountDowenIdNotIn(List<Long> values) {
            addCriterion("template_count_dowen_id not in", values, "templateCountDowenId");
            return (Criteria) this;
        }

        public Criteria andTemplateCountDowenIdBetween(Long value1, Long value2) {
            addCriterion("template_count_dowen_id between", value1, value2, "templateCountDowenId");
            return (Criteria) this;
        }

        public Criteria andTemplateCountDowenIdNotBetween(Long value1, Long value2) {
            addCriterion("template_count_dowen_id not between", value1, value2, "templateCountDowenId");
            return (Criteria) this;
        }

        public Criteria andTemplateGrayAreaIdIsNull() {
            addCriterion("template_gray_area_id is null");
            return (Criteria) this;
        }

        public Criteria andTemplateGrayAreaIdIsNotNull() {
            addCriterion("template_gray_area_id is not null");
            return (Criteria) this;
        }

        public Criteria andTemplateGrayAreaIdEqualTo(Long value) {
            addCriterion("template_gray_area_id =", value, "templateGrayAreaId");
            return (Criteria) this;
        }

        public Criteria andTemplateGrayAreaIdNotEqualTo(Long value) {
            addCriterion("template_gray_area_id <>", value, "templateGrayAreaId");
            return (Criteria) this;
        }

        public Criteria andTemplateGrayAreaIdGreaterThan(Long value) {
            addCriterion("template_gray_area_id >", value, "templateGrayAreaId");
            return (Criteria) this;
        }

        public Criteria andTemplateGrayAreaIdGreaterThanOrEqualTo(Long value) {
            addCriterion("template_gray_area_id >=", value, "templateGrayAreaId");
            return (Criteria) this;
        }

        public Criteria andTemplateGrayAreaIdLessThan(Long value) {
            addCriterion("template_gray_area_id <", value, "templateGrayAreaId");
            return (Criteria) this;
        }

        public Criteria andTemplateGrayAreaIdLessThanOrEqualTo(Long value) {
            addCriterion("template_gray_area_id <=", value, "templateGrayAreaId");
            return (Criteria) this;
        }

        public Criteria andTemplateGrayAreaIdIn(List<Long> values) {
            addCriterion("template_gray_area_id in", values, "templateGrayAreaId");
            return (Criteria) this;
        }

        public Criteria andTemplateGrayAreaIdNotIn(List<Long> values) {
            addCriterion("template_gray_area_id not in", values, "templateGrayAreaId");
            return (Criteria) this;
        }

        public Criteria andTemplateGrayAreaIdBetween(Long value1, Long value2) {
            addCriterion("template_gray_area_id between", value1, value2, "templateGrayAreaId");
            return (Criteria) this;
        }

        public Criteria andTemplateGrayAreaIdNotBetween(Long value1, Long value2) {
            addCriterion("template_gray_area_id not between", value1, value2, "templateGrayAreaId");
            return (Criteria) this;
        }

        public Criteria andStandardTournamentIdIsNull() {
            addCriterion("standard_tournament_id is null");
            return (Criteria) this;
        }

        public Criteria andStandardTournamentIdIsNotNull() {
            addCriterion("standard_tournament_id is not null");
            return (Criteria) this;
        }

        public Criteria andStandardTournamentIdEqualTo(Long value) {
            addCriterion("standard_tournament_id =", value, "standardTournamentId");
            return (Criteria) this;
        }

        public Criteria andStandardTournamentIdNotEqualTo(Long value) {
            addCriterion("standard_tournament_id <>", value, "standardTournamentId");
            return (Criteria) this;
        }

        public Criteria andStandardTournamentIdGreaterThan(Long value) {
            addCriterion("standard_tournament_id >", value, "standardTournamentId");
            return (Criteria) this;
        }

        public Criteria andStandardTournamentIdGreaterThanOrEqualTo(Long value) {
            addCriterion("standard_tournament_id >=", value, "standardTournamentId");
            return (Criteria) this;
        }

        public Criteria andStandardTournamentIdLessThan(Long value) {
            addCriterion("standard_tournament_id <", value, "standardTournamentId");
            return (Criteria) this;
        }

        public Criteria andStandardTournamentIdLessThanOrEqualTo(Long value) {
            addCriterion("standard_tournament_id <=", value, "standardTournamentId");
            return (Criteria) this;
        }

        public Criteria andStandardTournamentIdIn(List<Long> values) {
            addCriterion("standard_tournament_id in", values, "standardTournamentId");
            return (Criteria) this;
        }

        public Criteria andStandardTournamentIdNotIn(List<Long> values) {
            addCriterion("standard_tournament_id not in", values, "standardTournamentId");
            return (Criteria) this;
        }

        public Criteria andStandardTournamentIdBetween(Long value1, Long value2) {
            addCriterion("standard_tournament_id between", value1, value2, "standardTournamentId");
            return (Criteria) this;
        }

        public Criteria andStandardTournamentIdNotBetween(Long value1, Long value2) {
            addCriterion("standard_tournament_id not between", value1, value2, "standardTournamentId");
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