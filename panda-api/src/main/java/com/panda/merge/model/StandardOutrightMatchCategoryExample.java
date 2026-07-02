package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class StandardOutrightMatchCategoryExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public StandardOutrightMatchCategoryExample() {
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

        public Criteria andCategorySellStatusIsNull() {
            addCriterion("category_sell_status is null");
            return (Criteria) this;
        }

        public Criteria andCategorySellStatusIsNotNull() {
            addCriterion("category_sell_status is not null");
            return (Criteria) this;
        }

        public Criteria andCategorySellStatusEqualTo(String value) {
            addCriterion("category_sell_status =", value, "categorySellStatus");
            return (Criteria) this;
        }

        public Criteria andCategorySellStatusNotEqualTo(String value) {
            addCriterion("category_sell_status <>", value, "categorySellStatus");
            return (Criteria) this;
        }

        public Criteria andCategorySellStatusGreaterThan(String value) {
            addCriterion("category_sell_status >", value, "categorySellStatus");
            return (Criteria) this;
        }

        public Criteria andCategorySellStatusGreaterThanOrEqualTo(String value) {
            addCriterion("category_sell_status >=", value, "categorySellStatus");
            return (Criteria) this;
        }

        public Criteria andCategorySellStatusLessThan(String value) {
            addCriterion("category_sell_status <", value, "categorySellStatus");
            return (Criteria) this;
        }

        public Criteria andCategorySellStatusLessThanOrEqualTo(String value) {
            addCriterion("category_sell_status <=", value, "categorySellStatus");
            return (Criteria) this;
        }

        public Criteria andCategorySellStatusLike(String value) {
            addCriterion("category_sell_status like", value, "categorySellStatus");
            return (Criteria) this;
        }

        public Criteria andCategorySellStatusNotLike(String value) {
            addCriterion("category_sell_status not like", value, "categorySellStatus");
            return (Criteria) this;
        }

        public Criteria andCategorySellStatusIn(List<String> values) {
            addCriterion("category_sell_status in", values, "categorySellStatus");
            return (Criteria) this;
        }

        public Criteria andCategorySellStatusNotIn(List<String> values) {
            addCriterion("category_sell_status not in", values, "categorySellStatus");
            return (Criteria) this;
        }

        public Criteria andCategorySellStatusBetween(String value1, String value2) {
            addCriterion("category_sell_status between", value1, value2, "categorySellStatus");
            return (Criteria) this;
        }

        public Criteria andCategorySellStatusNotBetween(String value1, String value2) {
            addCriterion("category_sell_status not between", value1, value2, "categorySellStatus");
            return (Criteria) this;
        }

        public Criteria andMatchResultStatusIsNull() {
            addCriterion("match_result_status is null");
            return (Criteria) this;
        }

        public Criteria andMatchResultStatusIsNotNull() {
            addCriterion("match_result_status is not null");
            return (Criteria) this;
        }

        public Criteria andMatchResultStatusEqualTo(Integer value) {
            addCriterion("match_result_status =", value, "matchResultStatus");
            return (Criteria) this;
        }

        public Criteria andMatchResultStatusNotEqualTo(Integer value) {
            addCriterion("match_result_status <>", value, "matchResultStatus");
            return (Criteria) this;
        }

        public Criteria andMatchResultStatusGreaterThan(Integer value) {
            addCriterion("match_result_status >", value, "matchResultStatus");
            return (Criteria) this;
        }

        public Criteria andMatchResultStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("match_result_status >=", value, "matchResultStatus");
            return (Criteria) this;
        }

        public Criteria andMatchResultStatusLessThan(Integer value) {
            addCriterion("match_result_status <", value, "matchResultStatus");
            return (Criteria) this;
        }

        public Criteria andMatchResultStatusLessThanOrEqualTo(Integer value) {
            addCriterion("match_result_status <=", value, "matchResultStatus");
            return (Criteria) this;
        }

        public Criteria andMatchResultStatusIn(List<Integer> values) {
            addCriterion("match_result_status in", values, "matchResultStatus");
            return (Criteria) this;
        }

        public Criteria andMatchResultStatusNotIn(List<Integer> values) {
            addCriterion("match_result_status not in", values, "matchResultStatus");
            return (Criteria) this;
        }

        public Criteria andMatchResultStatusBetween(Integer value1, Integer value2) {
            addCriterion("match_result_status between", value1, value2, "matchResultStatus");
            return (Criteria) this;
        }

        public Criteria andMatchResultStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("match_result_status not between", value1, value2, "matchResultStatus");
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

        public Criteria andModfiyTimeIsNull() {
            addCriterion("modfiy_time is null");
            return (Criteria) this;
        }

        public Criteria andModfiyTimeIsNotNull() {
            addCriterion("modfiy_time is not null");
            return (Criteria) this;
        }

        public Criteria andModfiyTimeEqualTo(Long value) {
            addCriterion("modfiy_time =", value, "modfiyTime");
            return (Criteria) this;
        }

        public Criteria andModfiyTimeNotEqualTo(Long value) {
            addCriterion("modfiy_time <>", value, "modfiyTime");
            return (Criteria) this;
        }

        public Criteria andModfiyTimeGreaterThan(Long value) {
            addCriterion("modfiy_time >", value, "modfiyTime");
            return (Criteria) this;
        }

        public Criteria andModfiyTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("modfiy_time >=", value, "modfiyTime");
            return (Criteria) this;
        }

        public Criteria andModfiyTimeLessThan(Long value) {
            addCriterion("modfiy_time <", value, "modfiyTime");
            return (Criteria) this;
        }

        public Criteria andModfiyTimeLessThanOrEqualTo(Long value) {
            addCriterion("modfiy_time <=", value, "modfiyTime");
            return (Criteria) this;
        }

        public Criteria andModfiyTimeIn(List<Long> values) {
            addCriterion("modfiy_time in", values, "modfiyTime");
            return (Criteria) this;
        }

        public Criteria andModfiyTimeNotIn(List<Long> values) {
            addCriterion("modfiy_time not in", values, "modfiyTime");
            return (Criteria) this;
        }

        public Criteria andModfiyTimeBetween(Long value1, Long value2) {
            addCriterion("modfiy_time between", value1, value2, "modfiyTime");
            return (Criteria) this;
        }

        public Criteria andModfiyTimeNotBetween(Long value1, Long value2) {
            addCriterion("modfiy_time not between", value1, value2, "modfiyTime");
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