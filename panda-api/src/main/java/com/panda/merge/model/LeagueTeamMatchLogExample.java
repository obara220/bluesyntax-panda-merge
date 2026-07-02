package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class LeagueTeamMatchLogExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public LeagueTeamMatchLogExample() {
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

        public Criteria andOperateModuleIsNull() {
            addCriterion("operate_module is null");
            return (Criteria) this;
        }

        public Criteria andOperateModuleIsNotNull() {
            addCriterion("operate_module is not null");
            return (Criteria) this;
        }

        public Criteria andOperateModuleEqualTo(String value) {
            addCriterion("operate_module =", value, "operateModule");
            return (Criteria) this;
        }

        public Criteria andOperateModuleNotEqualTo(String value) {
            addCriterion("operate_module <>", value, "operateModule");
            return (Criteria) this;
        }

        public Criteria andOperateModuleGreaterThan(String value) {
            addCriterion("operate_module >", value, "operateModule");
            return (Criteria) this;
        }

        public Criteria andOperateModuleGreaterThanOrEqualTo(String value) {
            addCriterion("operate_module >=", value, "operateModule");
            return (Criteria) this;
        }

        public Criteria andOperateModuleLessThan(String value) {
            addCriterion("operate_module <", value, "operateModule");
            return (Criteria) this;
        }

        public Criteria andOperateModuleLessThanOrEqualTo(String value) {
            addCriterion("operate_module <=", value, "operateModule");
            return (Criteria) this;
        }

        public Criteria andOperateModuleLike(String value) {
            addCriterion("operate_module like", value, "operateModule");
            return (Criteria) this;
        }

        public Criteria andOperateModuleNotLike(String value) {
            addCriterion("operate_module not like", value, "operateModule");
            return (Criteria) this;
        }

        public Criteria andOperateModuleIn(List<String> values) {
            addCriterion("operate_module in", values, "operateModule");
            return (Criteria) this;
        }

        public Criteria andOperateModuleNotIn(List<String> values) {
            addCriterion("operate_module not in", values, "operateModule");
            return (Criteria) this;
        }

        public Criteria andOperateModuleBetween(String value1, String value2) {
            addCriterion("operate_module between", value1, value2, "operateModule");
            return (Criteria) this;
        }

        public Criteria andOperateModuleNotBetween(String value1, String value2) {
            addCriterion("operate_module not between", value1, value2, "operateModule");
            return (Criteria) this;
        }

        public Criteria andOperateTextIsNull() {
            addCriterion("operate_text is null");
            return (Criteria) this;
        }

        public Criteria andOperateTextIsNotNull() {
            addCriterion("operate_text is not null");
            return (Criteria) this;
        }

        public Criteria andOperateTextEqualTo(String value) {
            addCriterion("operate_text =", value, "operateText");
            return (Criteria) this;
        }

        public Criteria andOperateTextNotEqualTo(String value) {
            addCriterion("operate_text <>", value, "operateText");
            return (Criteria) this;
        }

        public Criteria andOperateTextGreaterThan(String value) {
            addCriterion("operate_text >", value, "operateText");
            return (Criteria) this;
        }

        public Criteria andOperateTextGreaterThanOrEqualTo(String value) {
            addCriterion("operate_text >=", value, "operateText");
            return (Criteria) this;
        }

        public Criteria andOperateTextLessThan(String value) {
            addCriterion("operate_text <", value, "operateText");
            return (Criteria) this;
        }

        public Criteria andOperateTextLessThanOrEqualTo(String value) {
            addCriterion("operate_text <=", value, "operateText");
            return (Criteria) this;
        }

        public Criteria andOperateTextLike(String value) {
            addCriterion("operate_text like", value, "operateText");
            return (Criteria) this;
        }

        public Criteria andOperateTextNotLike(String value) {
            addCriterion("operate_text not like", value, "operateText");
            return (Criteria) this;
        }

        public Criteria andOperateTextIn(List<String> values) {
            addCriterion("operate_text in", values, "operateText");
            return (Criteria) this;
        }

        public Criteria andOperateTextNotIn(List<String> values) {
            addCriterion("operate_text not in", values, "operateText");
            return (Criteria) this;
        }

        public Criteria andOperateTextBetween(String value1, String value2) {
            addCriterion("operate_text between", value1, value2, "operateText");
            return (Criteria) this;
        }

        public Criteria andOperateTextNotBetween(String value1, String value2) {
            addCriterion("operate_text not between", value1, value2, "operateText");
            return (Criteria) this;
        }

        public Criteria andOperateNumberIsNull() {
            addCriterion("operate_number is null");
            return (Criteria) this;
        }

        public Criteria andOperateNumberIsNotNull() {
            addCriterion("operate_number is not null");
            return (Criteria) this;
        }

        public Criteria andOperateNumberEqualTo(String value) {
            addCriterion("operate_number =", value, "operateNumber");
            return (Criteria) this;
        }

        public Criteria andOperateNumberNotEqualTo(String value) {
            addCriterion("operate_number <>", value, "operateNumber");
            return (Criteria) this;
        }

        public Criteria andOperateNumberGreaterThan(String value) {
            addCriterion("operate_number >", value, "operateNumber");
            return (Criteria) this;
        }

        public Criteria andOperateNumberGreaterThanOrEqualTo(String value) {
            addCriterion("operate_number >=", value, "operateNumber");
            return (Criteria) this;
        }

        public Criteria andOperateNumberLessThan(String value) {
            addCriterion("operate_number <", value, "operateNumber");
            return (Criteria) this;
        }

        public Criteria andOperateNumberLessThanOrEqualTo(String value) {
            addCriterion("operate_number <=", value, "operateNumber");
            return (Criteria) this;
        }

        public Criteria andOperateNumberLike(String value) {
            addCriterion("operate_number like", value, "operateNumber");
            return (Criteria) this;
        }

        public Criteria andOperateNumberNotLike(String value) {
            addCriterion("operate_number not like", value, "operateNumber");
            return (Criteria) this;
        }

        public Criteria andOperateNumberIn(List<String> values) {
            addCriterion("operate_number in", values, "operateNumber");
            return (Criteria) this;
        }

        public Criteria andOperateNumberNotIn(List<String> values) {
            addCriterion("operate_number not in", values, "operateNumber");
            return (Criteria) this;
        }

        public Criteria andOperateNumberBetween(String value1, String value2) {
            addCriterion("operate_number between", value1, value2, "operateNumber");
            return (Criteria) this;
        }

        public Criteria andOperateNumberNotBetween(String value1, String value2) {
            addCriterion("operate_number not between", value1, value2, "operateNumber");
            return (Criteria) this;
        }

        public Criteria andOperateIdIsNull() {
            addCriterion("operate_id is null");
            return (Criteria) this;
        }

        public Criteria andOperateIdIsNotNull() {
            addCriterion("operate_id is not null");
            return (Criteria) this;
        }

        public Criteria andOperateIdEqualTo(Long value) {
            addCriterion("operate_id =", value, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdNotEqualTo(Long value) {
            addCriterion("operate_id <>", value, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdGreaterThan(Long value) {
            addCriterion("operate_id >", value, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdGreaterThanOrEqualTo(Long value) {
            addCriterion("operate_id >=", value, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdLessThan(Long value) {
            addCriterion("operate_id <", value, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdLessThanOrEqualTo(Long value) {
            addCriterion("operate_id <=", value, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdIn(List<Long> values) {
            addCriterion("operate_id in", values, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdNotIn(List<Long> values) {
            addCriterion("operate_id not in", values, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdBetween(Long value1, Long value2) {
            addCriterion("operate_id between", value1, value2, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdNotBetween(Long value1, Long value2) {
            addCriterion("operate_id not between", value1, value2, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateNameIsNull() {
            addCriterion("operate_name is null");
            return (Criteria) this;
        }

        public Criteria andOperateNameIsNotNull() {
            addCriterion("operate_name is not null");
            return (Criteria) this;
        }

        public Criteria andOperateNameEqualTo(String value) {
            addCriterion("operate_name =", value, "operateName");
            return (Criteria) this;
        }

        public Criteria andOperateNameNotEqualTo(String value) {
            addCriterion("operate_name <>", value, "operateName");
            return (Criteria) this;
        }

        public Criteria andOperateNameGreaterThan(String value) {
            addCriterion("operate_name >", value, "operateName");
            return (Criteria) this;
        }

        public Criteria andOperateNameGreaterThanOrEqualTo(String value) {
            addCriterion("operate_name >=", value, "operateName");
            return (Criteria) this;
        }

        public Criteria andOperateNameLessThan(String value) {
            addCriterion("operate_name <", value, "operateName");
            return (Criteria) this;
        }

        public Criteria andOperateNameLessThanOrEqualTo(String value) {
            addCriterion("operate_name <=", value, "operateName");
            return (Criteria) this;
        }

        public Criteria andOperateNameLike(String value) {
            addCriterion("operate_name like", value, "operateName");
            return (Criteria) this;
        }

        public Criteria andOperateNameNotLike(String value) {
            addCriterion("operate_name not like", value, "operateName");
            return (Criteria) this;
        }

        public Criteria andOperateNameIn(List<String> values) {
            addCriterion("operate_name in", values, "operateName");
            return (Criteria) this;
        }

        public Criteria andOperateNameNotIn(List<String> values) {
            addCriterion("operate_name not in", values, "operateName");
            return (Criteria) this;
        }

        public Criteria andOperateNameBetween(String value1, String value2) {
            addCriterion("operate_name between", value1, value2, "operateName");
            return (Criteria) this;
        }

        public Criteria andOperateNameNotBetween(String value1, String value2) {
            addCriterion("operate_name not between", value1, value2, "operateName");
            return (Criteria) this;
        }

        public Criteria andOperateTypeIsNull() {
            addCriterion("operate_type is null");
            return (Criteria) this;
        }

        public Criteria andOperateTypeIsNotNull() {
            addCriterion("operate_type is not null");
            return (Criteria) this;
        }

        public Criteria andOperateTypeEqualTo(String value) {
            addCriterion("operate_type =", value, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeNotEqualTo(String value) {
            addCriterion("operate_type <>", value, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeGreaterThan(String value) {
            addCriterion("operate_type >", value, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeGreaterThanOrEqualTo(String value) {
            addCriterion("operate_type >=", value, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeLessThan(String value) {
            addCriterion("operate_type <", value, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeLessThanOrEqualTo(String value) {
            addCriterion("operate_type <=", value, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeLike(String value) {
            addCriterion("operate_type like", value, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeNotLike(String value) {
            addCriterion("operate_type not like", value, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeIn(List<String> values) {
            addCriterion("operate_type in", values, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeNotIn(List<String> values) {
            addCriterion("operate_type not in", values, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeBetween(String value1, String value2) {
            addCriterion("operate_type between", value1, value2, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeNotBetween(String value1, String value2) {
            addCriterion("operate_type not between", value1, value2, "operateType");
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

        public Criteria andRemarkIsNull() {
            addCriterion("remark is null");
            return (Criteria) this;
        }

        public Criteria andRemarkIsNotNull() {
            addCriterion("remark is not null");
            return (Criteria) this;
        }

        public Criteria andRemarkEqualTo(String value) {
            addCriterion("remark =", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotEqualTo(String value) {
            addCriterion("remark <>", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkGreaterThan(String value) {
            addCriterion("remark >", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkGreaterThanOrEqualTo(String value) {
            addCriterion("remark >=", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLessThan(String value) {
            addCriterion("remark <", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLessThanOrEqualTo(String value) {
            addCriterion("remark <=", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLike(String value) {
            addCriterion("remark like", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotLike(String value) {
            addCriterion("remark not like", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkIn(List<String> values) {
            addCriterion("remark in", values, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotIn(List<String> values) {
            addCriterion("remark not in", values, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkBetween(String value1, String value2) {
            addCriterion("remark between", value1, value2, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotBetween(String value1, String value2) {
            addCriterion("remark not between", value1, value2, "remark");
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