package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class StandardMarketCategoryExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public StandardMarketCategoryExample() {
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

        public Criteria andFieldsNumIsNull() {
            addCriterion("fields_num is null");
            return (Criteria) this;
        }

        public Criteria andFieldsNumIsNotNull() {
            addCriterion("fields_num is not null");
            return (Criteria) this;
        }

        public Criteria andFieldsNumEqualTo(Integer value) {
            addCriterion("fields_num =", value, "fieldsNum");
            return (Criteria) this;
        }

        public Criteria andFieldsNumNotEqualTo(Integer value) {
            addCriterion("fields_num <>", value, "fieldsNum");
            return (Criteria) this;
        }

        public Criteria andFieldsNumGreaterThan(Integer value) {
            addCriterion("fields_num >", value, "fieldsNum");
            return (Criteria) this;
        }

        public Criteria andFieldsNumGreaterThanOrEqualTo(Integer value) {
            addCriterion("fields_num >=", value, "fieldsNum");
            return (Criteria) this;
        }

        public Criteria andFieldsNumLessThan(Integer value) {
            addCriterion("fields_num <", value, "fieldsNum");
            return (Criteria) this;
        }

        public Criteria andFieldsNumLessThanOrEqualTo(Integer value) {
            addCriterion("fields_num <=", value, "fieldsNum");
            return (Criteria) this;
        }

        public Criteria andFieldsNumIn(List<Integer> values) {
            addCriterion("fields_num in", values, "fieldsNum");
            return (Criteria) this;
        }

        public Criteria andFieldsNumNotIn(List<Integer> values) {
            addCriterion("fields_num not in", values, "fieldsNum");
            return (Criteria) this;
        }

        public Criteria andFieldsNumBetween(Integer value1, Integer value2) {
            addCriterion("fields_num between", value1, value2, "fieldsNum");
            return (Criteria) this;
        }

        public Criteria andFieldsNumNotBetween(Integer value1, Integer value2) {
            addCriterion("fields_num not between", value1, value2, "fieldsNum");
            return (Criteria) this;
        }

        public Criteria andMultiMarketIsNull() {
            addCriterion("multi_market is null");
            return (Criteria) this;
        }

        public Criteria andMultiMarketIsNotNull() {
            addCriterion("multi_market is not null");
            return (Criteria) this;
        }

        public Criteria andMultiMarketEqualTo(Integer value) {
            addCriterion("multi_market =", value, "multiMarket");
            return (Criteria) this;
        }

        public Criteria andMultiMarketNotEqualTo(Integer value) {
            addCriterion("multi_market <>", value, "multiMarket");
            return (Criteria) this;
        }

        public Criteria andMultiMarketGreaterThan(Integer value) {
            addCriterion("multi_market >", value, "multiMarket");
            return (Criteria) this;
        }

        public Criteria andMultiMarketGreaterThanOrEqualTo(Integer value) {
            addCriterion("multi_market >=", value, "multiMarket");
            return (Criteria) this;
        }

        public Criteria andMultiMarketLessThan(Integer value) {
            addCriterion("multi_market <", value, "multiMarket");
            return (Criteria) this;
        }

        public Criteria andMultiMarketLessThanOrEqualTo(Integer value) {
            addCriterion("multi_market <=", value, "multiMarket");
            return (Criteria) this;
        }

        public Criteria andMultiMarketIn(List<Integer> values) {
            addCriterion("multi_market in", values, "multiMarket");
            return (Criteria) this;
        }

        public Criteria andMultiMarketNotIn(List<Integer> values) {
            addCriterion("multi_market not in", values, "multiMarket");
            return (Criteria) this;
        }

        public Criteria andMultiMarketBetween(Integer value1, Integer value2) {
            addCriterion("multi_market between", value1, value2, "multiMarket");
            return (Criteria) this;
        }

        public Criteria andMultiMarketNotBetween(Integer value1, Integer value2) {
            addCriterion("multi_market not between", value1, value2, "multiMarket");
            return (Criteria) this;
        }

        public Criteria andSupportOddsIsNull() {
            addCriterion("support_odds is null");
            return (Criteria) this;
        }

        public Criteria andSupportOddsIsNotNull() {
            addCriterion("support_odds is not null");
            return (Criteria) this;
        }

        public Criteria andSupportOddsEqualTo(String value) {
            addCriterion("support_odds =", value, "supportOdds");
            return (Criteria) this;
        }

        public Criteria andSupportOddsNotEqualTo(String value) {
            addCriterion("support_odds <>", value, "supportOdds");
            return (Criteria) this;
        }

        public Criteria andSupportOddsGreaterThan(String value) {
            addCriterion("support_odds >", value, "supportOdds");
            return (Criteria) this;
        }

        public Criteria andSupportOddsGreaterThanOrEqualTo(String value) {
            addCriterion("support_odds >=", value, "supportOdds");
            return (Criteria) this;
        }

        public Criteria andSupportOddsLessThan(String value) {
            addCriterion("support_odds <", value, "supportOdds");
            return (Criteria) this;
        }

        public Criteria andSupportOddsLessThanOrEqualTo(String value) {
            addCriterion("support_odds <=", value, "supportOdds");
            return (Criteria) this;
        }

        public Criteria andSupportOddsLike(String value) {
            addCriterion("support_odds like", value, "supportOdds");
            return (Criteria) this;
        }

        public Criteria andSupportOddsNotLike(String value) {
            addCriterion("support_odds not like", value, "supportOdds");
            return (Criteria) this;
        }

        public Criteria andSupportOddsIn(List<String> values) {
            addCriterion("support_odds in", values, "supportOdds");
            return (Criteria) this;
        }

        public Criteria andSupportOddsNotIn(List<String> values) {
            addCriterion("support_odds not in", values, "supportOdds");
            return (Criteria) this;
        }

        public Criteria andSupportOddsBetween(String value1, String value2) {
            addCriterion("support_odds between", value1, value2, "supportOdds");
            return (Criteria) this;
        }

        public Criteria andSupportOddsNotBetween(String value1, String value2) {
            addCriterion("support_odds not between", value1, value2, "supportOdds");
            return (Criteria) this;
        }

        public Criteria andTemplatePcIsNull() {
            addCriterion("template_pc is null");
            return (Criteria) this;
        }

        public Criteria andTemplatePcIsNotNull() {
            addCriterion("template_pc is not null");
            return (Criteria) this;
        }

        public Criteria andTemplatePcEqualTo(Integer value) {
            addCriterion("template_pc =", value, "templatePc");
            return (Criteria) this;
        }

        public Criteria andTemplatePcNotEqualTo(Integer value) {
            addCriterion("template_pc <>", value, "templatePc");
            return (Criteria) this;
        }

        public Criteria andTemplatePcGreaterThan(Integer value) {
            addCriterion("template_pc >", value, "templatePc");
            return (Criteria) this;
        }

        public Criteria andTemplatePcGreaterThanOrEqualTo(Integer value) {
            addCriterion("template_pc >=", value, "templatePc");
            return (Criteria) this;
        }

        public Criteria andTemplatePcLessThan(Integer value) {
            addCriterion("template_pc <", value, "templatePc");
            return (Criteria) this;
        }

        public Criteria andTemplatePcLessThanOrEqualTo(Integer value) {
            addCriterion("template_pc <=", value, "templatePc");
            return (Criteria) this;
        }

        public Criteria andTemplatePcIn(List<Integer> values) {
            addCriterion("template_pc in", values, "templatePc");
            return (Criteria) this;
        }

        public Criteria andTemplatePcNotIn(List<Integer> values) {
            addCriterion("template_pc not in", values, "templatePc");
            return (Criteria) this;
        }

        public Criteria andTemplatePcBetween(Integer value1, Integer value2) {
            addCriterion("template_pc between", value1, value2, "templatePc");
            return (Criteria) this;
        }

        public Criteria andTemplatePcNotBetween(Integer value1, Integer value2) {
            addCriterion("template_pc not between", value1, value2, "templatePc");
            return (Criteria) this;
        }

        public Criteria andTemplateH5IsNull() {
            addCriterion("template_h5 is null");
            return (Criteria) this;
        }

        public Criteria andTemplateH5IsNotNull() {
            addCriterion("template_h5 is not null");
            return (Criteria) this;
        }

        public Criteria andTemplateH5EqualTo(Integer value) {
            addCriterion("template_h5 =", value, "templateH5");
            return (Criteria) this;
        }

        public Criteria andTemplateH5NotEqualTo(Integer value) {
            addCriterion("template_h5 <>", value, "templateH5");
            return (Criteria) this;
        }

        public Criteria andTemplateH5GreaterThan(Integer value) {
            addCriterion("template_h5 >", value, "templateH5");
            return (Criteria) this;
        }

        public Criteria andTemplateH5GreaterThanOrEqualTo(Integer value) {
            addCriterion("template_h5 >=", value, "templateH5");
            return (Criteria) this;
        }

        public Criteria andTemplateH5LessThan(Integer value) {
            addCriterion("template_h5 <", value, "templateH5");
            return (Criteria) this;
        }

        public Criteria andTemplateH5LessThanOrEqualTo(Integer value) {
            addCriterion("template_h5 <=", value, "templateH5");
            return (Criteria) this;
        }

        public Criteria andTemplateH5In(List<Integer> values) {
            addCriterion("template_h5 in", values, "templateH5");
            return (Criteria) this;
        }

        public Criteria andTemplateH5NotIn(List<Integer> values) {
            addCriterion("template_h5 not in", values, "templateH5");
            return (Criteria) this;
        }

        public Criteria andTemplateH5Between(Integer value1, Integer value2) {
            addCriterion("template_h5 between", value1, value2, "templateH5");
            return (Criteria) this;
        }

        public Criteria andTemplateH5NotBetween(Integer value1, Integer value2) {
            addCriterion("template_h5 not between", value1, value2, "templateH5");
            return (Criteria) this;
        }

        public Criteria andStatusIsNull() {
            addCriterion("status is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("status is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(Integer value) {
            addCriterion("status =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(Integer value) {
            addCriterion("status <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(Integer value) {
            addCriterion("status >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("status >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(Integer value) {
            addCriterion("status <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(Integer value) {
            addCriterion("status <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<Integer> values) {
            addCriterion("status in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<Integer> values) {
            addCriterion("status not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(Integer value1, Integer value2) {
            addCriterion("status between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("status not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andMerchantStatusIsNull() {
            addCriterion("merchant_status is null");
            return (Criteria) this;
        }

        public Criteria andMerchantStatusIsNotNull() {
            addCriterion("merchant_status is not null");
            return (Criteria) this;
        }

        public Criteria andMerchantStatusEqualTo(Integer value) {
            addCriterion("merchant_status =", value, "merchantStatus");
            return (Criteria) this;
        }

        public Criteria andMerchantStatusNotEqualTo(Integer value) {
            addCriterion("merchant_status <>", value, "merchantStatus");
            return (Criteria) this;
        }

        public Criteria andMerchantStatusGreaterThan(Integer value) {
            addCriterion("merchant_status >", value, "merchantStatus");
            return (Criteria) this;
        }

        public Criteria andMerchantStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("merchant_status >=", value, "merchantStatus");
            return (Criteria) this;
        }

        public Criteria andMerchantStatusLessThan(Integer value) {
            addCriterion("merchant_status <", value, "merchantStatus");
            return (Criteria) this;
        }

        public Criteria andMerchantStatusLessThanOrEqualTo(Integer value) {
            addCriterion("merchant_status <=", value, "merchantStatus");
            return (Criteria) this;
        }

        public Criteria andMerchantStatusIn(List<Integer> values) {
            addCriterion("merchant_status in", values, "merchantStatus");
            return (Criteria) this;
        }

        public Criteria andMerchantStatusNotIn(List<Integer> values) {
            addCriterion("merchant_status not in", values, "merchantStatus");
            return (Criteria) this;
        }

        public Criteria andMerchantStatusBetween(Integer value1, Integer value2) {
            addCriterion("merchant_status between", value1, value2, "merchantStatus");
            return (Criteria) this;
        }

        public Criteria andMerchantStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("merchant_status not between", value1, value2, "merchantStatus");
            return (Criteria) this;
        }

        public Criteria andOrderNoIsNull() {
            addCriterion("order_no is null");
            return (Criteria) this;
        }

        public Criteria andOrderNoIsNotNull() {
            addCriterion("order_no is not null");
            return (Criteria) this;
        }

        public Criteria andOrderNoEqualTo(Integer value) {
            addCriterion("order_no =", value, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoNotEqualTo(Integer value) {
            addCriterion("order_no <>", value, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoGreaterThan(Integer value) {
            addCriterion("order_no >", value, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoGreaterThanOrEqualTo(Integer value) {
            addCriterion("order_no >=", value, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoLessThan(Integer value) {
            addCriterion("order_no <", value, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoLessThanOrEqualTo(Integer value) {
            addCriterion("order_no <=", value, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoIn(List<Integer> values) {
            addCriterion("order_no in", values, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoNotIn(List<Integer> values) {
            addCriterion("order_no not in", values, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoBetween(Integer value1, Integer value2) {
            addCriterion("order_no between", value1, value2, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoNotBetween(Integer value1, Integer value2) {
            addCriterion("order_no not between", value1, value2, "orderNo");
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

        public Criteria andTemplatePcClientIsNull() {
            addCriterion("template_pc_client is null");
            return (Criteria) this;
        }

        public Criteria andTemplatePcClientIsNotNull() {
            addCriterion("template_pc_client is not null");
            return (Criteria) this;
        }

        public Criteria andTemplatePcClientEqualTo(Integer value) {
            addCriterion("template_pc_client =", value, "templatePcClient");
            return (Criteria) this;
        }

        public Criteria andTemplatePcClientNotEqualTo(Integer value) {
            addCriterion("template_pc_client <>", value, "templatePcClient");
            return (Criteria) this;
        }

        public Criteria andTemplatePcClientGreaterThan(Integer value) {
            addCriterion("template_pc_client >", value, "templatePcClient");
            return (Criteria) this;
        }

        public Criteria andTemplatePcClientGreaterThanOrEqualTo(Integer value) {
            addCriterion("template_pc_client >=", value, "templatePcClient");
            return (Criteria) this;
        }

        public Criteria andTemplatePcClientLessThan(Integer value) {
            addCriterion("template_pc_client <", value, "templatePcClient");
            return (Criteria) this;
        }

        public Criteria andTemplatePcClientLessThanOrEqualTo(Integer value) {
            addCriterion("template_pc_client <=", value, "templatePcClient");
            return (Criteria) this;
        }

        public Criteria andTemplatePcClientIn(List<Integer> values) {
            addCriterion("template_pc_client in", values, "templatePcClient");
            return (Criteria) this;
        }

        public Criteria andTemplatePcClientNotIn(List<Integer> values) {
            addCriterion("template_pc_client not in", values, "templatePcClient");
            return (Criteria) this;
        }

        public Criteria andTemplatePcClientBetween(Integer value1, Integer value2) {
            addCriterion("template_pc_client between", value1, value2, "templatePcClient");
            return (Criteria) this;
        }

        public Criteria andTemplatePcClientNotBetween(Integer value1, Integer value2) {
            addCriterion("template_pc_client not between", value1, value2, "templatePcClient");
            return (Criteria) this;
        }

        public Criteria andTemplateH5ClientIsNull() {
            addCriterion("template_h5_client is null");
            return (Criteria) this;
        }

        public Criteria andTemplateH5ClientIsNotNull() {
            addCriterion("template_h5_client is not null");
            return (Criteria) this;
        }

        public Criteria andTemplateH5ClientEqualTo(Integer value) {
            addCriterion("template_h5_client =", value, "templateH5Client");
            return (Criteria) this;
        }

        public Criteria andTemplateH5ClientNotEqualTo(Integer value) {
            addCriterion("template_h5_client <>", value, "templateH5Client");
            return (Criteria) this;
        }

        public Criteria andTemplateH5ClientGreaterThan(Integer value) {
            addCriterion("template_h5_client >", value, "templateH5Client");
            return (Criteria) this;
        }

        public Criteria andTemplateH5ClientGreaterThanOrEqualTo(Integer value) {
            addCriterion("template_h5_client >=", value, "templateH5Client");
            return (Criteria) this;
        }

        public Criteria andTemplateH5ClientLessThan(Integer value) {
            addCriterion("template_h5_client <", value, "templateH5Client");
            return (Criteria) this;
        }

        public Criteria andTemplateH5ClientLessThanOrEqualTo(Integer value) {
            addCriterion("template_h5_client <=", value, "templateH5Client");
            return (Criteria) this;
        }

        public Criteria andTemplateH5ClientIn(List<Integer> values) {
            addCriterion("template_h5_client in", values, "templateH5Client");
            return (Criteria) this;
        }

        public Criteria andTemplateH5ClientNotIn(List<Integer> values) {
            addCriterion("template_h5_client not in", values, "templateH5Client");
            return (Criteria) this;
        }

        public Criteria andTemplateH5ClientBetween(Integer value1, Integer value2) {
            addCriterion("template_h5_client between", value1, value2, "templateH5Client");
            return (Criteria) this;
        }

        public Criteria andTemplateH5ClientNotBetween(Integer value1, Integer value2) {
            addCriterion("template_h5_client not between", value1, value2, "templateH5Client");
            return (Criteria) this;
        }

        public Criteria andAoStatusIsNull() {
            addCriterion("ao_status is null");
            return (Criteria) this;
        }

        public Criteria andAoStatusIsNotNull() {
            addCriterion("ao_status is not null");
            return (Criteria) this;
        }

        public Criteria andAoStatusEqualTo(Integer value) {
            addCriterion("ao_status =", value, "aoStatus");
            return (Criteria) this;
        }

        public Criteria andAoStatusNotEqualTo(Integer value) {
            addCriterion("ao_status <>", value, "aoStatus");
            return (Criteria) this;
        }

        public Criteria andAoStatusGreaterThan(Integer value) {
            addCriterion("ao_status >", value, "aoStatus");
            return (Criteria) this;
        }

        public Criteria andAoStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("ao_status >=", value, "aoStatus");
            return (Criteria) this;
        }

        public Criteria andAoStatusLessThan(Integer value) {
            addCriterion("ao_status <", value, "aoStatus");
            return (Criteria) this;
        }

        public Criteria andAoStatusLessThanOrEqualTo(Integer value) {
            addCriterion("ao_status <=", value, "aoStatus");
            return (Criteria) this;
        }

        public Criteria andAoStatusIn(List<Integer> values) {
            addCriterion("ao_status in", values, "aoStatus");
            return (Criteria) this;
        }

        public Criteria andAoStatusNotIn(List<Integer> values) {
            addCriterion("ao_status not in", values, "aoStatus");
            return (Criteria) this;
        }

        public Criteria andAoStatusBetween(Integer value1, Integer value2) {
            addCriterion("ao_status between", value1, value2, "aoStatus");
            return (Criteria) this;
        }

        public Criteria andAoStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("ao_status not between", value1, value2, "aoStatus");
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