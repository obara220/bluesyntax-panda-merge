package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class StandardSportRegionExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public StandardSportRegionExample() {
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

        public Criteria andVisibleIsNull() {
            addCriterion("visible is null");
            return (Criteria) this;
        }

        public Criteria andVisibleIsNotNull() {
            addCriterion("visible is not null");
            return (Criteria) this;
        }

        public Criteria andVisibleEqualTo(Integer value) {
            addCriterion("visible =", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleNotEqualTo(Integer value) {
            addCriterion("visible <>", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleGreaterThan(Integer value) {
            addCriterion("visible >", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleGreaterThanOrEqualTo(Integer value) {
            addCriterion("visible >=", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleLessThan(Integer value) {
            addCriterion("visible <", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleLessThanOrEqualTo(Integer value) {
            addCriterion("visible <=", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleIn(List<Integer> values) {
            addCriterion("visible in", values, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleNotIn(List<Integer> values) {
            addCriterion("visible not in", values, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleBetween(Integer value1, Integer value2) {
            addCriterion("visible between", value1, value2, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleNotBetween(Integer value1, Integer value2) {
            addCriterion("visible not between", value1, value2, "visible");
            return (Criteria) this;
        }

        public Criteria andNameCodeIsNull() {
            addCriterion("name_code is null");
            return (Criteria) this;
        }

        public Criteria andNameCodeIsNotNull() {
            addCriterion("name_code is not null");
            return (Criteria) this;
        }

        public Criteria andNameCodeEqualTo(Long value) {
            addCriterion("name_code =", value, "nameCode");
            return (Criteria) this;
        }

        public Criteria andNameCodeNotEqualTo(Long value) {
            addCriterion("name_code <>", value, "nameCode");
            return (Criteria) this;
        }

        public Criteria andNameCodeGreaterThan(Long value) {
            addCriterion("name_code >", value, "nameCode");
            return (Criteria) this;
        }

        public Criteria andNameCodeGreaterThanOrEqualTo(Long value) {
            addCriterion("name_code >=", value, "nameCode");
            return (Criteria) this;
        }

        public Criteria andNameCodeLessThan(Long value) {
            addCriterion("name_code <", value, "nameCode");
            return (Criteria) this;
        }

        public Criteria andNameCodeLessThanOrEqualTo(Long value) {
            addCriterion("name_code <=", value, "nameCode");
            return (Criteria) this;
        }

        public Criteria andNameCodeIn(List<Long> values) {
            addCriterion("name_code in", values, "nameCode");
            return (Criteria) this;
        }

        public Criteria andNameCodeNotIn(List<Long> values) {
            addCriterion("name_code not in", values, "nameCode");
            return (Criteria) this;
        }

        public Criteria andNameCodeBetween(Long value1, Long value2) {
            addCriterion("name_code between", value1, value2, "nameCode");
            return (Criteria) this;
        }

        public Criteria andNameCodeNotBetween(Long value1, Long value2) {
            addCriterion("name_code not between", value1, value2, "nameCode");
            return (Criteria) this;
        }

        public Criteria andIntroductionEnIsNull() {
            addCriterion("introduction_en is null");
            return (Criteria) this;
        }

        public Criteria andIntroductionEnIsNotNull() {
            addCriterion("introduction_en is not null");
            return (Criteria) this;
        }

        public Criteria andIntroductionEnEqualTo(String value) {
            addCriterion("introduction_en =", value, "introductionEn");
            return (Criteria) this;
        }

        public Criteria andIntroductionEnNotEqualTo(String value) {
            addCriterion("introduction_en <>", value, "introductionEn");
            return (Criteria) this;
        }

        public Criteria andIntroductionEnGreaterThan(String value) {
            addCriterion("introduction_en >", value, "introductionEn");
            return (Criteria) this;
        }

        public Criteria andIntroductionEnGreaterThanOrEqualTo(String value) {
            addCriterion("introduction_en >=", value, "introductionEn");
            return (Criteria) this;
        }

        public Criteria andIntroductionEnLessThan(String value) {
            addCriterion("introduction_en <", value, "introductionEn");
            return (Criteria) this;
        }

        public Criteria andIntroductionEnLessThanOrEqualTo(String value) {
            addCriterion("introduction_en <=", value, "introductionEn");
            return (Criteria) this;
        }

        public Criteria andIntroductionEnLike(String value) {
            addCriterion("introduction_en like", value, "introductionEn");
            return (Criteria) this;
        }

        public Criteria andIntroductionEnNotLike(String value) {
            addCriterion("introduction_en not like", value, "introductionEn");
            return (Criteria) this;
        }

        public Criteria andIntroductionEnIn(List<String> values) {
            addCriterion("introduction_en in", values, "introductionEn");
            return (Criteria) this;
        }

        public Criteria andIntroductionEnNotIn(List<String> values) {
            addCriterion("introduction_en not in", values, "introductionEn");
            return (Criteria) this;
        }

        public Criteria andIntroductionEnBetween(String value1, String value2) {
            addCriterion("introduction_en between", value1, value2, "introductionEn");
            return (Criteria) this;
        }

        public Criteria andIntroductionEnNotBetween(String value1, String value2) {
            addCriterion("introduction_en not between", value1, value2, "introductionEn");
            return (Criteria) this;
        }

        public Criteria andIntroductionIsNull() {
            addCriterion("introduction is null");
            return (Criteria) this;
        }

        public Criteria andIntroductionIsNotNull() {
            addCriterion("introduction is not null");
            return (Criteria) this;
        }

        public Criteria andIntroductionEqualTo(String value) {
            addCriterion("introduction =", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionNotEqualTo(String value) {
            addCriterion("introduction <>", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionGreaterThan(String value) {
            addCriterion("introduction >", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionGreaterThanOrEqualTo(String value) {
            addCriterion("introduction >=", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionLessThan(String value) {
            addCriterion("introduction <", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionLessThanOrEqualTo(String value) {
            addCriterion("introduction <=", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionLike(String value) {
            addCriterion("introduction like", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionNotLike(String value) {
            addCriterion("introduction not like", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionIn(List<String> values) {
            addCriterion("introduction in", values, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionNotIn(List<String> values) {
            addCriterion("introduction not in", values, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionBetween(String value1, String value2) {
            addCriterion("introduction between", value1, value2, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionNotBetween(String value1, String value2) {
            addCriterion("introduction not between", value1, value2, "introduction");
            return (Criteria) this;
        }

        public Criteria andOrderNumberIsNull() {
            addCriterion("order_number is null");
            return (Criteria) this;
        }

        public Criteria andOrderNumberIsNotNull() {
            addCriterion("order_number is not null");
            return (Criteria) this;
        }

        public Criteria andOrderNumberEqualTo(Integer value) {
            addCriterion("order_number =", value, "orderNumber");
            return (Criteria) this;
        }

        public Criteria andOrderNumberNotEqualTo(Integer value) {
            addCriterion("order_number <>", value, "orderNumber");
            return (Criteria) this;
        }

        public Criteria andOrderNumberGreaterThan(Integer value) {
            addCriterion("order_number >", value, "orderNumber");
            return (Criteria) this;
        }

        public Criteria andOrderNumberGreaterThanOrEqualTo(Integer value) {
            addCriterion("order_number >=", value, "orderNumber");
            return (Criteria) this;
        }

        public Criteria andOrderNumberLessThan(Integer value) {
            addCriterion("order_number <", value, "orderNumber");
            return (Criteria) this;
        }

        public Criteria andOrderNumberLessThanOrEqualTo(Integer value) {
            addCriterion("order_number <=", value, "orderNumber");
            return (Criteria) this;
        }

        public Criteria andOrderNumberIn(List<Integer> values) {
            addCriterion("order_number in", values, "orderNumber");
            return (Criteria) this;
        }

        public Criteria andOrderNumberNotIn(List<Integer> values) {
            addCriterion("order_number not in", values, "orderNumber");
            return (Criteria) this;
        }

        public Criteria andOrderNumberBetween(Integer value1, Integer value2) {
            addCriterion("order_number between", value1, value2, "orderNumber");
            return (Criteria) this;
        }

        public Criteria andOrderNumberNotBetween(Integer value1, Integer value2) {
            addCriterion("order_number not between", value1, value2, "orderNumber");
            return (Criteria) this;
        }

        public Criteria andNationalFlagUrlIsNull() {
            addCriterion("national_flag_url is null");
            return (Criteria) this;
        }

        public Criteria andNationalFlagUrlIsNotNull() {
            addCriterion("national_flag_url is not null");
            return (Criteria) this;
        }

        public Criteria andNationalFlagUrlEqualTo(String value) {
            addCriterion("national_flag_url =", value, "nationalFlagUrl");
            return (Criteria) this;
        }

        public Criteria andNationalFlagUrlNotEqualTo(String value) {
            addCriterion("national_flag_url <>", value, "nationalFlagUrl");
            return (Criteria) this;
        }

        public Criteria andNationalFlagUrlGreaterThan(String value) {
            addCriterion("national_flag_url >", value, "nationalFlagUrl");
            return (Criteria) this;
        }

        public Criteria andNationalFlagUrlGreaterThanOrEqualTo(String value) {
            addCriterion("national_flag_url >=", value, "nationalFlagUrl");
            return (Criteria) this;
        }

        public Criteria andNationalFlagUrlLessThan(String value) {
            addCriterion("national_flag_url <", value, "nationalFlagUrl");
            return (Criteria) this;
        }

        public Criteria andNationalFlagUrlLessThanOrEqualTo(String value) {
            addCriterion("national_flag_url <=", value, "nationalFlagUrl");
            return (Criteria) this;
        }

        public Criteria andNationalFlagUrlLike(String value) {
            addCriterion("national_flag_url like", value, "nationalFlagUrl");
            return (Criteria) this;
        }

        public Criteria andNationalFlagUrlNotLike(String value) {
            addCriterion("national_flag_url not like", value, "nationalFlagUrl");
            return (Criteria) this;
        }

        public Criteria andNationalFlagUrlIn(List<String> values) {
            addCriterion("national_flag_url in", values, "nationalFlagUrl");
            return (Criteria) this;
        }

        public Criteria andNationalFlagUrlNotIn(List<String> values) {
            addCriterion("national_flag_url not in", values, "nationalFlagUrl");
            return (Criteria) this;
        }

        public Criteria andNationalFlagUrlBetween(String value1, String value2) {
            addCriterion("national_flag_url between", value1, value2, "nationalFlagUrl");
            return (Criteria) this;
        }

        public Criteria andNationalFlagUrlNotBetween(String value1, String value2) {
            addCriterion("national_flag_url not between", value1, value2, "nationalFlagUrl");
            return (Criteria) this;
        }

        public Criteria andSpellIsNull() {
            addCriterion("spell is null");
            return (Criteria) this;
        }

        public Criteria andSpellIsNotNull() {
            addCriterion("spell is not null");
            return (Criteria) this;
        }

        public Criteria andSpellEqualTo(String value) {
            addCriterion("spell =", value, "spell");
            return (Criteria) this;
        }

        public Criteria andSpellNotEqualTo(String value) {
            addCriterion("spell <>", value, "spell");
            return (Criteria) this;
        }

        public Criteria andSpellGreaterThan(String value) {
            addCriterion("spell >", value, "spell");
            return (Criteria) this;
        }

        public Criteria andSpellGreaterThanOrEqualTo(String value) {
            addCriterion("spell >=", value, "spell");
            return (Criteria) this;
        }

        public Criteria andSpellLessThan(String value) {
            addCriterion("spell <", value, "spell");
            return (Criteria) this;
        }

        public Criteria andSpellLessThanOrEqualTo(String value) {
            addCriterion("spell <=", value, "spell");
            return (Criteria) this;
        }

        public Criteria andSpellLike(String value) {
            addCriterion("spell like", value, "spell");
            return (Criteria) this;
        }

        public Criteria andSpellNotLike(String value) {
            addCriterion("spell not like", value, "spell");
            return (Criteria) this;
        }

        public Criteria andSpellIn(List<String> values) {
            addCriterion("spell in", values, "spell");
            return (Criteria) this;
        }

        public Criteria andSpellNotIn(List<String> values) {
            addCriterion("spell not in", values, "spell");
            return (Criteria) this;
        }

        public Criteria andSpellBetween(String value1, String value2) {
            addCriterion("spell between", value1, value2, "spell");
            return (Criteria) this;
        }

        public Criteria andSpellNotBetween(String value1, String value2) {
            addCriterion("spell not between", value1, value2, "spell");
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

        public Criteria andIntroductionZhIsNull() {
            addCriterion("introduction_zh is null");
            return (Criteria) this;
        }

        public Criteria andIntroductionZhIsNotNull() {
            addCriterion("introduction_zh is not null");
            return (Criteria) this;
        }

        public Criteria andIntroductionZhEqualTo(String value) {
            addCriterion("introduction_zh =", value, "introductionZh");
            return (Criteria) this;
        }

        public Criteria andIntroductionZhNotEqualTo(String value) {
            addCriterion("introduction_zh <>", value, "introductionZh");
            return (Criteria) this;
        }

        public Criteria andIntroductionZhGreaterThan(String value) {
            addCriterion("introduction_zh >", value, "introductionZh");
            return (Criteria) this;
        }

        public Criteria andIntroductionZhGreaterThanOrEqualTo(String value) {
            addCriterion("introduction_zh >=", value, "introductionZh");
            return (Criteria) this;
        }

        public Criteria andIntroductionZhLessThan(String value) {
            addCriterion("introduction_zh <", value, "introductionZh");
            return (Criteria) this;
        }

        public Criteria andIntroductionZhLessThanOrEqualTo(String value) {
            addCriterion("introduction_zh <=", value, "introductionZh");
            return (Criteria) this;
        }

        public Criteria andIntroductionZhLike(String value) {
            addCriterion("introduction_zh like", value, "introductionZh");
            return (Criteria) this;
        }

        public Criteria andIntroductionZhNotLike(String value) {
            addCriterion("introduction_zh not like", value, "introductionZh");
            return (Criteria) this;
        }

        public Criteria andIntroductionZhIn(List<String> values) {
            addCriterion("introduction_zh in", values, "introductionZh");
            return (Criteria) this;
        }

        public Criteria andIntroductionZhNotIn(List<String> values) {
            addCriterion("introduction_zh not in", values, "introductionZh");
            return (Criteria) this;
        }

        public Criteria andIntroductionZhBetween(String value1, String value2) {
            addCriterion("introduction_zh between", value1, value2, "introductionZh");
            return (Criteria) this;
        }

        public Criteria andIntroductionZhNotBetween(String value1, String value2) {
            addCriterion("introduction_zh not between", value1, value2, "introductionZh");
            return (Criteria) this;
        }

        public Criteria andIntroductionViIsNull() {
            addCriterion("introduction_vi is null");
            return (Criteria) this;
        }

        public Criteria andIntroductionViIsNotNull() {
            addCriterion("introduction_vi is not null");
            return (Criteria) this;
        }

        public Criteria andIntroductionViEqualTo(String value) {
            addCriterion("introduction_vi =", value, "introductionVi");
            return (Criteria) this;
        }

        public Criteria andIntroductionViNotEqualTo(String value) {
            addCriterion("introduction_vi <>", value, "introductionVi");
            return (Criteria) this;
        }

        public Criteria andIntroductionViGreaterThan(String value) {
            addCriterion("introduction_vi >", value, "introductionVi");
            return (Criteria) this;
        }

        public Criteria andIntroductionViGreaterThanOrEqualTo(String value) {
            addCriterion("introduction_vi >=", value, "introductionVi");
            return (Criteria) this;
        }

        public Criteria andIntroductionViLessThan(String value) {
            addCriterion("introduction_vi <", value, "introductionVi");
            return (Criteria) this;
        }

        public Criteria andIntroductionViLessThanOrEqualTo(String value) {
            addCriterion("introduction_vi <=", value, "introductionVi");
            return (Criteria) this;
        }

        public Criteria andIntroductionViLike(String value) {
            addCriterion("introduction_vi like", value, "introductionVi");
            return (Criteria) this;
        }

        public Criteria andIntroductionViNotLike(String value) {
            addCriterion("introduction_vi not like", value, "introductionVi");
            return (Criteria) this;
        }

        public Criteria andIntroductionViIn(List<String> values) {
            addCriterion("introduction_vi in", values, "introductionVi");
            return (Criteria) this;
        }

        public Criteria andIntroductionViNotIn(List<String> values) {
            addCriterion("introduction_vi not in", values, "introductionVi");
            return (Criteria) this;
        }

        public Criteria andIntroductionViBetween(String value1, String value2) {
            addCriterion("introduction_vi between", value1, value2, "introductionVi");
            return (Criteria) this;
        }

        public Criteria andIntroductionViNotBetween(String value1, String value2) {
            addCriterion("introduction_vi not between", value1, value2, "introductionVi");
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