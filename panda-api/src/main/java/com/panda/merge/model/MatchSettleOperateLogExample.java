package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class MatchSettleOperateLogExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MatchSettleOperateLogExample() {
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

        public Criteria andOperateIdIsNull() {
            addCriterion("operate_id is null");
            return (Criteria) this;
        }

        public Criteria andOperateIdIsNotNull() {
            addCriterion("operate_id is not null");
            return (Criteria) this;
        }

        public Criteria andOperateIdEqualTo(String value) {
            addCriterion("operate_id =", value, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdNotEqualTo(String value) {
            addCriterion("operate_id <>", value, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdGreaterThan(String value) {
            addCriterion("operate_id >", value, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdGreaterThanOrEqualTo(String value) {
            addCriterion("operate_id >=", value, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdLessThan(String value) {
            addCriterion("operate_id <", value, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdLessThanOrEqualTo(String value) {
            addCriterion("operate_id <=", value, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdLike(String value) {
            addCriterion("operate_id like", value, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdNotLike(String value) {
            addCriterion("operate_id not like", value, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdIn(List<String> values) {
            addCriterion("operate_id in", values, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdNotIn(List<String> values) {
            addCriterion("operate_id not in", values, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdBetween(String value1, String value2) {
            addCriterion("operate_id between", value1, value2, "operateId");
            return (Criteria) this;
        }

        public Criteria andOperateIdNotBetween(String value1, String value2) {
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

        public Criteria andOperateMatchIdIsNull() {
            addCriterion("operate_match_id is null");
            return (Criteria) this;
        }

        public Criteria andOperateMatchIdIsNotNull() {
            addCriterion("operate_match_id is not null");
            return (Criteria) this;
        }

        public Criteria andOperateMatchIdEqualTo(String value) {
            addCriterion("operate_match_id =", value, "operateMatchId");
            return (Criteria) this;
        }

        public Criteria andOperateMatchIdNotEqualTo(String value) {
            addCriterion("operate_match_id <>", value, "operateMatchId");
            return (Criteria) this;
        }

        public Criteria andOperateMatchIdGreaterThan(String value) {
            addCriterion("operate_match_id >", value, "operateMatchId");
            return (Criteria) this;
        }

        public Criteria andOperateMatchIdGreaterThanOrEqualTo(String value) {
            addCriterion("operate_match_id >=", value, "operateMatchId");
            return (Criteria) this;
        }

        public Criteria andOperateMatchIdLessThan(String value) {
            addCriterion("operate_match_id <", value, "operateMatchId");
            return (Criteria) this;
        }

        public Criteria andOperateMatchIdLessThanOrEqualTo(String value) {
            addCriterion("operate_match_id <=", value, "operateMatchId");
            return (Criteria) this;
        }

        public Criteria andOperateMatchIdLike(String value) {
            addCriterion("operate_match_id like", value, "operateMatchId");
            return (Criteria) this;
        }

        public Criteria andOperateMatchIdNotLike(String value) {
            addCriterion("operate_match_id not like", value, "operateMatchId");
            return (Criteria) this;
        }

        public Criteria andOperateMatchIdIn(List<String> values) {
            addCriterion("operate_match_id in", values, "operateMatchId");
            return (Criteria) this;
        }

        public Criteria andOperateMatchIdNotIn(List<String> values) {
            addCriterion("operate_match_id not in", values, "operateMatchId");
            return (Criteria) this;
        }

        public Criteria andOperateMatchIdBetween(String value1, String value2) {
            addCriterion("operate_match_id between", value1, value2, "operateMatchId");
            return (Criteria) this;
        }

        public Criteria andOperateMatchIdNotBetween(String value1, String value2) {
            addCriterion("operate_match_id not between", value1, value2, "operateMatchId");
            return (Criteria) this;
        }

        public Criteria andOperateMatchNameIsNull() {
            addCriterion("operate_match_name is null");
            return (Criteria) this;
        }

        public Criteria andOperateMatchNameIsNotNull() {
            addCriterion("operate_match_name is not null");
            return (Criteria) this;
        }

        public Criteria andOperateMatchNameEqualTo(String value) {
            addCriterion("operate_match_name =", value, "operateMatchName");
            return (Criteria) this;
        }

        public Criteria andOperateMatchNameNotEqualTo(String value) {
            addCriterion("operate_match_name <>", value, "operateMatchName");
            return (Criteria) this;
        }

        public Criteria andOperateMatchNameGreaterThan(String value) {
            addCriterion("operate_match_name >", value, "operateMatchName");
            return (Criteria) this;
        }

        public Criteria andOperateMatchNameGreaterThanOrEqualTo(String value) {
            addCriterion("operate_match_name >=", value, "operateMatchName");
            return (Criteria) this;
        }

        public Criteria andOperateMatchNameLessThan(String value) {
            addCriterion("operate_match_name <", value, "operateMatchName");
            return (Criteria) this;
        }

        public Criteria andOperateMatchNameLessThanOrEqualTo(String value) {
            addCriterion("operate_match_name <=", value, "operateMatchName");
            return (Criteria) this;
        }

        public Criteria andOperateMatchNameLike(String value) {
            addCriterion("operate_match_name like", value, "operateMatchName");
            return (Criteria) this;
        }

        public Criteria andOperateMatchNameNotLike(String value) {
            addCriterion("operate_match_name not like", value, "operateMatchName");
            return (Criteria) this;
        }

        public Criteria andOperateMatchNameIn(List<String> values) {
            addCriterion("operate_match_name in", values, "operateMatchName");
            return (Criteria) this;
        }

        public Criteria andOperateMatchNameNotIn(List<String> values) {
            addCriterion("operate_match_name not in", values, "operateMatchName");
            return (Criteria) this;
        }

        public Criteria andOperateMatchNameBetween(String value1, String value2) {
            addCriterion("operate_match_name between", value1, value2, "operateMatchName");
            return (Criteria) this;
        }

        public Criteria andOperateMatchNameNotBetween(String value1, String value2) {
            addCriterion("operate_match_name not between", value1, value2, "operateMatchName");
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

        public Criteria andOperateParaNameIsNull() {
            addCriterion("operate_para_name is null");
            return (Criteria) this;
        }

        public Criteria andOperateParaNameIsNotNull() {
            addCriterion("operate_para_name is not null");
            return (Criteria) this;
        }

        public Criteria andOperateParaNameEqualTo(String value) {
            addCriterion("operate_para_name =", value, "operateParaName");
            return (Criteria) this;
        }

        public Criteria andOperateParaNameNotEqualTo(String value) {
            addCriterion("operate_para_name <>", value, "operateParaName");
            return (Criteria) this;
        }

        public Criteria andOperateParaNameGreaterThan(String value) {
            addCriterion("operate_para_name >", value, "operateParaName");
            return (Criteria) this;
        }

        public Criteria andOperateParaNameGreaterThanOrEqualTo(String value) {
            addCriterion("operate_para_name >=", value, "operateParaName");
            return (Criteria) this;
        }

        public Criteria andOperateParaNameLessThan(String value) {
            addCriterion("operate_para_name <", value, "operateParaName");
            return (Criteria) this;
        }

        public Criteria andOperateParaNameLessThanOrEqualTo(String value) {
            addCriterion("operate_para_name <=", value, "operateParaName");
            return (Criteria) this;
        }

        public Criteria andOperateParaNameLike(String value) {
            addCriterion("operate_para_name like", value, "operateParaName");
            return (Criteria) this;
        }

        public Criteria andOperateParaNameNotLike(String value) {
            addCriterion("operate_para_name not like", value, "operateParaName");
            return (Criteria) this;
        }

        public Criteria andOperateParaNameIn(List<String> values) {
            addCriterion("operate_para_name in", values, "operateParaName");
            return (Criteria) this;
        }

        public Criteria andOperateParaNameNotIn(List<String> values) {
            addCriterion("operate_para_name not in", values, "operateParaName");
            return (Criteria) this;
        }

        public Criteria andOperateParaNameBetween(String value1, String value2) {
            addCriterion("operate_para_name between", value1, value2, "operateParaName");
            return (Criteria) this;
        }

        public Criteria andOperateParaNameNotBetween(String value1, String value2) {
            addCriterion("operate_para_name not between", value1, value2, "operateParaName");
            return (Criteria) this;
        }

        public Criteria andOperateForwTextIsNull() {
            addCriterion("operate_forw_text is null");
            return (Criteria) this;
        }

        public Criteria andOperateForwTextIsNotNull() {
            addCriterion("operate_forw_text is not null");
            return (Criteria) this;
        }

        public Criteria andOperateForwTextEqualTo(String value) {
            addCriterion("operate_forw_text =", value, "operateForwText");
            return (Criteria) this;
        }

        public Criteria andOperateForwTextNotEqualTo(String value) {
            addCriterion("operate_forw_text <>", value, "operateForwText");
            return (Criteria) this;
        }

        public Criteria andOperateForwTextGreaterThan(String value) {
            addCriterion("operate_forw_text >", value, "operateForwText");
            return (Criteria) this;
        }

        public Criteria andOperateForwTextGreaterThanOrEqualTo(String value) {
            addCriterion("operate_forw_text >=", value, "operateForwText");
            return (Criteria) this;
        }

        public Criteria andOperateForwTextLessThan(String value) {
            addCriterion("operate_forw_text <", value, "operateForwText");
            return (Criteria) this;
        }

        public Criteria andOperateForwTextLessThanOrEqualTo(String value) {
            addCriterion("operate_forw_text <=", value, "operateForwText");
            return (Criteria) this;
        }

        public Criteria andOperateForwTextLike(String value) {
            addCriterion("operate_forw_text like", value, "operateForwText");
            return (Criteria) this;
        }

        public Criteria andOperateForwTextNotLike(String value) {
            addCriterion("operate_forw_text not like", value, "operateForwText");
            return (Criteria) this;
        }

        public Criteria andOperateForwTextIn(List<String> values) {
            addCriterion("operate_forw_text in", values, "operateForwText");
            return (Criteria) this;
        }

        public Criteria andOperateForwTextNotIn(List<String> values) {
            addCriterion("operate_forw_text not in", values, "operateForwText");
            return (Criteria) this;
        }

        public Criteria andOperateForwTextBetween(String value1, String value2) {
            addCriterion("operate_forw_text between", value1, value2, "operateForwText");
            return (Criteria) this;
        }

        public Criteria andOperateForwTextNotBetween(String value1, String value2) {
            addCriterion("operate_forw_text not between", value1, value2, "operateForwText");
            return (Criteria) this;
        }

        public Criteria andOperateRearTextIsNull() {
            addCriterion("operate_rear_text is null");
            return (Criteria) this;
        }

        public Criteria andOperateRearTextIsNotNull() {
            addCriterion("operate_rear_text is not null");
            return (Criteria) this;
        }

        public Criteria andOperateRearTextEqualTo(String value) {
            addCriterion("operate_rear_text =", value, "operateRearText");
            return (Criteria) this;
        }

        public Criteria andOperateRearTextNotEqualTo(String value) {
            addCriterion("operate_rear_text <>", value, "operateRearText");
            return (Criteria) this;
        }

        public Criteria andOperateRearTextGreaterThan(String value) {
            addCriterion("operate_rear_text >", value, "operateRearText");
            return (Criteria) this;
        }

        public Criteria andOperateRearTextGreaterThanOrEqualTo(String value) {
            addCriterion("operate_rear_text >=", value, "operateRearText");
            return (Criteria) this;
        }

        public Criteria andOperateRearTextLessThan(String value) {
            addCriterion("operate_rear_text <", value, "operateRearText");
            return (Criteria) this;
        }

        public Criteria andOperateRearTextLessThanOrEqualTo(String value) {
            addCriterion("operate_rear_text <=", value, "operateRearText");
            return (Criteria) this;
        }

        public Criteria andOperateRearTextLike(String value) {
            addCriterion("operate_rear_text like", value, "operateRearText");
            return (Criteria) this;
        }

        public Criteria andOperateRearTextNotLike(String value) {
            addCriterion("operate_rear_text not like", value, "operateRearText");
            return (Criteria) this;
        }

        public Criteria andOperateRearTextIn(List<String> values) {
            addCriterion("operate_rear_text in", values, "operateRearText");
            return (Criteria) this;
        }

        public Criteria andOperateRearTextNotIn(List<String> values) {
            addCriterion("operate_rear_text not in", values, "operateRearText");
            return (Criteria) this;
        }

        public Criteria andOperateRearTextBetween(String value1, String value2) {
            addCriterion("operate_rear_text between", value1, value2, "operateRearText");
            return (Criteria) this;
        }

        public Criteria andOperateRearTextNotBetween(String value1, String value2) {
            addCriterion("operate_rear_text not between", value1, value2, "operateRearText");
            return (Criteria) this;
        }

        public Criteria andEventOrderIsNull() {
            addCriterion("event_order is null");
            return (Criteria) this;
        }

        public Criteria andEventOrderIsNotNull() {
            addCriterion("event_order is not null");
            return (Criteria) this;
        }

        public Criteria andEventOrderEqualTo(String value) {
            addCriterion("event_order =", value, "eventOrder");
            return (Criteria) this;
        }

        public Criteria andEventOrderNotEqualTo(String value) {
            addCriterion("event_order <>", value, "eventOrder");
            return (Criteria) this;
        }

        public Criteria andEventOrderGreaterThan(String value) {
            addCriterion("event_order >", value, "eventOrder");
            return (Criteria) this;
        }

        public Criteria andEventOrderGreaterThanOrEqualTo(String value) {
            addCriterion("event_order >=", value, "eventOrder");
            return (Criteria) this;
        }

        public Criteria andEventOrderLessThan(String value) {
            addCriterion("event_order <", value, "eventOrder");
            return (Criteria) this;
        }

        public Criteria andEventOrderLessThanOrEqualTo(String value) {
            addCriterion("event_order <=", value, "eventOrder");
            return (Criteria) this;
        }

        public Criteria andEventOrderLike(String value) {
            addCriterion("event_order like", value, "eventOrder");
            return (Criteria) this;
        }

        public Criteria andEventOrderNotLike(String value) {
            addCriterion("event_order not like", value, "eventOrder");
            return (Criteria) this;
        }

        public Criteria andEventOrderIn(List<String> values) {
            addCriterion("event_order in", values, "eventOrder");
            return (Criteria) this;
        }

        public Criteria andEventOrderNotIn(List<String> values) {
            addCriterion("event_order not in", values, "eventOrder");
            return (Criteria) this;
        }

        public Criteria andEventOrderBetween(String value1, String value2) {
            addCriterion("event_order between", value1, value2, "eventOrder");
            return (Criteria) this;
        }

        public Criteria andEventOrderNotBetween(String value1, String value2) {
            addCriterion("event_order not between", value1, value2, "eventOrder");
            return (Criteria) this;
        }

        public Criteria andPeriodIdIsNull() {
            addCriterion("period_id is null");
            return (Criteria) this;
        }

        public Criteria andPeriodIdIsNotNull() {
            addCriterion("period_id is not null");
            return (Criteria) this;
        }

        public Criteria andPeriodIdEqualTo(Long value) {
            addCriterion("period_id =", value, "periodId");
            return (Criteria) this;
        }

        public Criteria andPeriodIdNotEqualTo(Long value) {
            addCriterion("period_id <>", value, "periodId");
            return (Criteria) this;
        }

        public Criteria andPeriodIdGreaterThan(Long value) {
            addCriterion("period_id >", value, "periodId");
            return (Criteria) this;
        }

        public Criteria andPeriodIdGreaterThanOrEqualTo(Long value) {
            addCriterion("period_id >=", value, "periodId");
            return (Criteria) this;
        }

        public Criteria andPeriodIdLessThan(Long value) {
            addCriterion("period_id <", value, "periodId");
            return (Criteria) this;
        }

        public Criteria andPeriodIdLessThanOrEqualTo(Long value) {
            addCriterion("period_id <=", value, "periodId");
            return (Criteria) this;
        }

        public Criteria andPeriodIdIn(List<Long> values) {
            addCriterion("period_id in", values, "periodId");
            return (Criteria) this;
        }

        public Criteria andPeriodIdNotIn(List<Long> values) {
            addCriterion("period_id not in", values, "periodId");
            return (Criteria) this;
        }

        public Criteria andPeriodIdBetween(Long value1, Long value2) {
            addCriterion("period_id between", value1, value2, "periodId");
            return (Criteria) this;
        }

        public Criteria andPeriodIdNotBetween(Long value1, Long value2) {
            addCriterion("period_id not between", value1, value2, "periodId");
            return (Criteria) this;
        }

        public Criteria andOperateUserNameIsNull() {
            addCriterion("operate_user_name is null");
            return (Criteria) this;
        }

        public Criteria andOperateUserNameIsNotNull() {
            addCriterion("operate_user_name is not null");
            return (Criteria) this;
        }

        public Criteria andOperateUserNameEqualTo(String value) {
            addCriterion("operate_user_name =", value, "operateUserName");
            return (Criteria) this;
        }

        public Criteria andOperateUserNameNotEqualTo(String value) {
            addCriterion("operate_user_name <>", value, "operateUserName");
            return (Criteria) this;
        }

        public Criteria andOperateUserNameGreaterThan(String value) {
            addCriterion("operate_user_name >", value, "operateUserName");
            return (Criteria) this;
        }

        public Criteria andOperateUserNameGreaterThanOrEqualTo(String value) {
            addCriterion("operate_user_name >=", value, "operateUserName");
            return (Criteria) this;
        }

        public Criteria andOperateUserNameLessThan(String value) {
            addCriterion("operate_user_name <", value, "operateUserName");
            return (Criteria) this;
        }

        public Criteria andOperateUserNameLessThanOrEqualTo(String value) {
            addCriterion("operate_user_name <=", value, "operateUserName");
            return (Criteria) this;
        }

        public Criteria andOperateUserNameLike(String value) {
            addCriterion("operate_user_name like", value, "operateUserName");
            return (Criteria) this;
        }

        public Criteria andOperateUserNameNotLike(String value) {
            addCriterion("operate_user_name not like", value, "operateUserName");
            return (Criteria) this;
        }

        public Criteria andOperateUserNameIn(List<String> values) {
            addCriterion("operate_user_name in", values, "operateUserName");
            return (Criteria) this;
        }

        public Criteria andOperateUserNameNotIn(List<String> values) {
            addCriterion("operate_user_name not in", values, "operateUserName");
            return (Criteria) this;
        }

        public Criteria andOperateUserNameBetween(String value1, String value2) {
            addCriterion("operate_user_name between", value1, value2, "operateUserName");
            return (Criteria) this;
        }

        public Criteria andOperateUserNameNotBetween(String value1, String value2) {
            addCriterion("operate_user_name not between", value1, value2, "operateUserName");
            return (Criteria) this;
        }

        public Criteria andIpAddressIsNull() {
            addCriterion("ip_address is null");
            return (Criteria) this;
        }

        public Criteria andIpAddressIsNotNull() {
            addCriterion("ip_address is not null");
            return (Criteria) this;
        }

        public Criteria andIpAddressEqualTo(String value) {
            addCriterion("ip_address =", value, "ipAddress");
            return (Criteria) this;
        }

        public Criteria andIpAddressNotEqualTo(String value) {
            addCriterion("ip_address <>", value, "ipAddress");
            return (Criteria) this;
        }

        public Criteria andIpAddressGreaterThan(String value) {
            addCriterion("ip_address >", value, "ipAddress");
            return (Criteria) this;
        }

        public Criteria andIpAddressGreaterThanOrEqualTo(String value) {
            addCriterion("ip_address >=", value, "ipAddress");
            return (Criteria) this;
        }

        public Criteria andIpAddressLessThan(String value) {
            addCriterion("ip_address <", value, "ipAddress");
            return (Criteria) this;
        }

        public Criteria andIpAddressLessThanOrEqualTo(String value) {
            addCriterion("ip_address <=", value, "ipAddress");
            return (Criteria) this;
        }

        public Criteria andIpAddressLike(String value) {
            addCriterion("ip_address like", value, "ipAddress");
            return (Criteria) this;
        }

        public Criteria andIpAddressNotLike(String value) {
            addCriterion("ip_address not like", value, "ipAddress");
            return (Criteria) this;
        }

        public Criteria andIpAddressIn(List<String> values) {
            addCriterion("ip_address in", values, "ipAddress");
            return (Criteria) this;
        }

        public Criteria andIpAddressNotIn(List<String> values) {
            addCriterion("ip_address not in", values, "ipAddress");
            return (Criteria) this;
        }

        public Criteria andIpAddressBetween(String value1, String value2) {
            addCriterion("ip_address between", value1, value2, "ipAddress");
            return (Criteria) this;
        }

        public Criteria andIpAddressNotBetween(String value1, String value2) {
            addCriterion("ip_address not between", value1, value2, "ipAddress");
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