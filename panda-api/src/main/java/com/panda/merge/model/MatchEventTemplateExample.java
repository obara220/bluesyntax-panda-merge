package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class MatchEventTemplateExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MatchEventTemplateExample() {
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

        public Criteria andSportIdEqualTo(Boolean value) {
            addCriterion("sport_id =", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdNotEqualTo(Boolean value) {
            addCriterion("sport_id <>", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdGreaterThan(Boolean value) {
            addCriterion("sport_id >", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdGreaterThanOrEqualTo(Boolean value) {
            addCriterion("sport_id >=", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdLessThan(Boolean value) {
            addCriterion("sport_id <", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdLessThanOrEqualTo(Boolean value) {
            addCriterion("sport_id <=", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdIn(List<Boolean> values) {
            addCriterion("sport_id in", values, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdNotIn(List<Boolean> values) {
            addCriterion("sport_id not in", values, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdBetween(Boolean value1, Boolean value2) {
            addCriterion("sport_id between", value1, value2, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdNotBetween(Boolean value1, Boolean value2) {
            addCriterion("sport_id not between", value1, value2, "sportId");
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

        public Criteria andTriggerCodeIsNull() {
            addCriterion("trigger_code is null");
            return (Criteria) this;
        }

        public Criteria andTriggerCodeIsNotNull() {
            addCriterion("trigger_code is not null");
            return (Criteria) this;
        }

        public Criteria andTriggerCodeEqualTo(String value) {
            addCriterion("trigger_code =", value, "triggerCode");
            return (Criteria) this;
        }

        public Criteria andTriggerCodeNotEqualTo(String value) {
            addCriterion("trigger_code <>", value, "triggerCode");
            return (Criteria) this;
        }

        public Criteria andTriggerCodeGreaterThan(String value) {
            addCriterion("trigger_code >", value, "triggerCode");
            return (Criteria) this;
        }

        public Criteria andTriggerCodeGreaterThanOrEqualTo(String value) {
            addCriterion("trigger_code >=", value, "triggerCode");
            return (Criteria) this;
        }

        public Criteria andTriggerCodeLessThan(String value) {
            addCriterion("trigger_code <", value, "triggerCode");
            return (Criteria) this;
        }

        public Criteria andTriggerCodeLessThanOrEqualTo(String value) {
            addCriterion("trigger_code <=", value, "triggerCode");
            return (Criteria) this;
        }

        public Criteria andTriggerCodeLike(String value) {
            addCriterion("trigger_code like", value, "triggerCode");
            return (Criteria) this;
        }

        public Criteria andTriggerCodeNotLike(String value) {
            addCriterion("trigger_code not like", value, "triggerCode");
            return (Criteria) this;
        }

        public Criteria andTriggerCodeIn(List<String> values) {
            addCriterion("trigger_code in", values, "triggerCode");
            return (Criteria) this;
        }

        public Criteria andTriggerCodeNotIn(List<String> values) {
            addCriterion("trigger_code not in", values, "triggerCode");
            return (Criteria) this;
        }

        public Criteria andTriggerCodeBetween(String value1, String value2) {
            addCriterion("trigger_code between", value1, value2, "triggerCode");
            return (Criteria) this;
        }

        public Criteria andTriggerCodeNotBetween(String value1, String value2) {
            addCriterion("trigger_code not between", value1, value2, "triggerCode");
            return (Criteria) this;
        }

        public Criteria andTriggerPeriodIdIsNull() {
            addCriterion("trigger_period_id is null");
            return (Criteria) this;
        }

        public Criteria andTriggerPeriodIdIsNotNull() {
            addCriterion("trigger_period_id is not null");
            return (Criteria) this;
        }

        public Criteria andTriggerPeriodIdEqualTo(String value) {
            addCriterion("trigger_period_id =", value, "triggerPeriodId");
            return (Criteria) this;
        }

        public Criteria andTriggerPeriodIdNotEqualTo(String value) {
            addCriterion("trigger_period_id <>", value, "triggerPeriodId");
            return (Criteria) this;
        }

        public Criteria andTriggerPeriodIdGreaterThan(String value) {
            addCriterion("trigger_period_id >", value, "triggerPeriodId");
            return (Criteria) this;
        }

        public Criteria andTriggerPeriodIdGreaterThanOrEqualTo(String value) {
            addCriterion("trigger_period_id >=", value, "triggerPeriodId");
            return (Criteria) this;
        }

        public Criteria andTriggerPeriodIdLessThan(String value) {
            addCriterion("trigger_period_id <", value, "triggerPeriodId");
            return (Criteria) this;
        }

        public Criteria andTriggerPeriodIdLessThanOrEqualTo(String value) {
            addCriterion("trigger_period_id <=", value, "triggerPeriodId");
            return (Criteria) this;
        }

        public Criteria andTriggerPeriodIdLike(String value) {
            addCriterion("trigger_period_id like", value, "triggerPeriodId");
            return (Criteria) this;
        }

        public Criteria andTriggerPeriodIdNotLike(String value) {
            addCriterion("trigger_period_id not like", value, "triggerPeriodId");
            return (Criteria) this;
        }

        public Criteria andTriggerPeriodIdIn(List<String> values) {
            addCriterion("trigger_period_id in", values, "triggerPeriodId");
            return (Criteria) this;
        }

        public Criteria andTriggerPeriodIdNotIn(List<String> values) {
            addCriterion("trigger_period_id not in", values, "triggerPeriodId");
            return (Criteria) this;
        }

        public Criteria andTriggerPeriodIdBetween(String value1, String value2) {
            addCriterion("trigger_period_id between", value1, value2, "triggerPeriodId");
            return (Criteria) this;
        }

        public Criteria andTriggerPeriodIdNotBetween(String value1, String value2) {
            addCriterion("trigger_period_id not between", value1, value2, "triggerPeriodId");
            return (Criteria) this;
        }

        public Criteria andEventCodeIsNull() {
            addCriterion("event_code is null");
            return (Criteria) this;
        }

        public Criteria andEventCodeIsNotNull() {
            addCriterion("event_code is not null");
            return (Criteria) this;
        }

        public Criteria andEventCodeEqualTo(String value) {
            addCriterion("event_code =", value, "eventCode");
            return (Criteria) this;
        }

        public Criteria andEventCodeNotEqualTo(String value) {
            addCriterion("event_code <>", value, "eventCode");
            return (Criteria) this;
        }

        public Criteria andEventCodeGreaterThan(String value) {
            addCriterion("event_code >", value, "eventCode");
            return (Criteria) this;
        }

        public Criteria andEventCodeGreaterThanOrEqualTo(String value) {
            addCriterion("event_code >=", value, "eventCode");
            return (Criteria) this;
        }

        public Criteria andEventCodeLessThan(String value) {
            addCriterion("event_code <", value, "eventCode");
            return (Criteria) this;
        }

        public Criteria andEventCodeLessThanOrEqualTo(String value) {
            addCriterion("event_code <=", value, "eventCode");
            return (Criteria) this;
        }

        public Criteria andEventCodeLike(String value) {
            addCriterion("event_code like", value, "eventCode");
            return (Criteria) this;
        }

        public Criteria andEventCodeNotLike(String value) {
            addCriterion("event_code not like", value, "eventCode");
            return (Criteria) this;
        }

        public Criteria andEventCodeIn(List<String> values) {
            addCriterion("event_code in", values, "eventCode");
            return (Criteria) this;
        }

        public Criteria andEventCodeNotIn(List<String> values) {
            addCriterion("event_code not in", values, "eventCode");
            return (Criteria) this;
        }

        public Criteria andEventCodeBetween(String value1, String value2) {
            addCriterion("event_code between", value1, value2, "eventCode");
            return (Criteria) this;
        }

        public Criteria andEventCodeNotBetween(String value1, String value2) {
            addCriterion("event_code not between", value1, value2, "eventCode");
            return (Criteria) this;
        }

        public Criteria andTemplateTextIsNull() {
            addCriterion("template_text is null");
            return (Criteria) this;
        }

        public Criteria andTemplateTextIsNotNull() {
            addCriterion("template_text is not null");
            return (Criteria) this;
        }

        public Criteria andTemplateTextEqualTo(String value) {
            addCriterion("template_text =", value, "templateText");
            return (Criteria) this;
        }

        public Criteria andTemplateTextNotEqualTo(String value) {
            addCriterion("template_text <>", value, "templateText");
            return (Criteria) this;
        }

        public Criteria andTemplateTextGreaterThan(String value) {
            addCriterion("template_text >", value, "templateText");
            return (Criteria) this;
        }

        public Criteria andTemplateTextGreaterThanOrEqualTo(String value) {
            addCriterion("template_text >=", value, "templateText");
            return (Criteria) this;
        }

        public Criteria andTemplateTextLessThan(String value) {
            addCriterion("template_text <", value, "templateText");
            return (Criteria) this;
        }

        public Criteria andTemplateTextLessThanOrEqualTo(String value) {
            addCriterion("template_text <=", value, "templateText");
            return (Criteria) this;
        }

        public Criteria andTemplateTextLike(String value) {
            addCriterion("template_text like", value, "templateText");
            return (Criteria) this;
        }

        public Criteria andTemplateTextNotLike(String value) {
            addCriterion("template_text not like", value, "templateText");
            return (Criteria) this;
        }

        public Criteria andTemplateTextIn(List<String> values) {
            addCriterion("template_text in", values, "templateText");
            return (Criteria) this;
        }

        public Criteria andTemplateTextNotIn(List<String> values) {
            addCriterion("template_text not in", values, "templateText");
            return (Criteria) this;
        }

        public Criteria andTemplateTextBetween(String value1, String value2) {
            addCriterion("template_text between", value1, value2, "templateText");
            return (Criteria) this;
        }

        public Criteria andTemplateTextNotBetween(String value1, String value2) {
            addCriterion("template_text not between", value1, value2, "templateText");
            return (Criteria) this;
        }

        public Criteria andTemplateFormatIsNull() {
            addCriterion("template_format is null");
            return (Criteria) this;
        }

        public Criteria andTemplateFormatIsNotNull() {
            addCriterion("template_format is not null");
            return (Criteria) this;
        }

        public Criteria andTemplateFormatEqualTo(String value) {
            addCriterion("template_format =", value, "templateFormat");
            return (Criteria) this;
        }

        public Criteria andTemplateFormatNotEqualTo(String value) {
            addCriterion("template_format <>", value, "templateFormat");
            return (Criteria) this;
        }

        public Criteria andTemplateFormatGreaterThan(String value) {
            addCriterion("template_format >", value, "templateFormat");
            return (Criteria) this;
        }

        public Criteria andTemplateFormatGreaterThanOrEqualTo(String value) {
            addCriterion("template_format >=", value, "templateFormat");
            return (Criteria) this;
        }

        public Criteria andTemplateFormatLessThan(String value) {
            addCriterion("template_format <", value, "templateFormat");
            return (Criteria) this;
        }

        public Criteria andTemplateFormatLessThanOrEqualTo(String value) {
            addCriterion("template_format <=", value, "templateFormat");
            return (Criteria) this;
        }

        public Criteria andTemplateFormatLike(String value) {
            addCriterion("template_format like", value, "templateFormat");
            return (Criteria) this;
        }

        public Criteria andTemplateFormatNotLike(String value) {
            addCriterion("template_format not like", value, "templateFormat");
            return (Criteria) this;
        }

        public Criteria andTemplateFormatIn(List<String> values) {
            addCriterion("template_format in", values, "templateFormat");
            return (Criteria) this;
        }

        public Criteria andTemplateFormatNotIn(List<String> values) {
            addCriterion("template_format not in", values, "templateFormat");
            return (Criteria) this;
        }

        public Criteria andTemplateFormatBetween(String value1, String value2) {
            addCriterion("template_format between", value1, value2, "templateFormat");
            return (Criteria) this;
        }

        public Criteria andTemplateFormatNotBetween(String value1, String value2) {
            addCriterion("template_format not between", value1, value2, "templateFormat");
            return (Criteria) this;
        }

        public Criteria andTemplateNoIsNull() {
            addCriterion("template_no is null");
            return (Criteria) this;
        }

        public Criteria andTemplateNoIsNotNull() {
            addCriterion("template_no is not null");
            return (Criteria) this;
        }

        public Criteria andTemplateNoEqualTo(Boolean value) {
            addCriterion("template_no =", value, "templateNo");
            return (Criteria) this;
        }

        public Criteria andTemplateNoNotEqualTo(Boolean value) {
            addCriterion("template_no <>", value, "templateNo");
            return (Criteria) this;
        }

        public Criteria andTemplateNoGreaterThan(Boolean value) {
            addCriterion("template_no >", value, "templateNo");
            return (Criteria) this;
        }

        public Criteria andTemplateNoGreaterThanOrEqualTo(Boolean value) {
            addCriterion("template_no >=", value, "templateNo");
            return (Criteria) this;
        }

        public Criteria andTemplateNoLessThan(Boolean value) {
            addCriterion("template_no <", value, "templateNo");
            return (Criteria) this;
        }

        public Criteria andTemplateNoLessThanOrEqualTo(Boolean value) {
            addCriterion("template_no <=", value, "templateNo");
            return (Criteria) this;
        }

        public Criteria andTemplateNoIn(List<Boolean> values) {
            addCriterion("template_no in", values, "templateNo");
            return (Criteria) this;
        }

        public Criteria andTemplateNoNotIn(List<Boolean> values) {
            addCriterion("template_no not in", values, "templateNo");
            return (Criteria) this;
        }

        public Criteria andTemplateNoBetween(Boolean value1, Boolean value2) {
            addCriterion("template_no between", value1, value2, "templateNo");
            return (Criteria) this;
        }

        public Criteria andTemplateNoNotBetween(Boolean value1, Boolean value2) {
            addCriterion("template_no not between", value1, value2, "templateNo");
            return (Criteria) this;
        }

        public Criteria andAuditTimeIsNull() {
            addCriterion("audit_time is null");
            return (Criteria) this;
        }

        public Criteria andAuditTimeIsNotNull() {
            addCriterion("audit_time is not null");
            return (Criteria) this;
        }

        public Criteria andAuditTimeEqualTo(Integer value) {
            addCriterion("audit_time =", value, "auditTime");
            return (Criteria) this;
        }

        public Criteria andAuditTimeNotEqualTo(Integer value) {
            addCriterion("audit_time <>", value, "auditTime");
            return (Criteria) this;
        }

        public Criteria andAuditTimeGreaterThan(Integer value) {
            addCriterion("audit_time >", value, "auditTime");
            return (Criteria) this;
        }

        public Criteria andAuditTimeGreaterThanOrEqualTo(Integer value) {
            addCriterion("audit_time >=", value, "auditTime");
            return (Criteria) this;
        }

        public Criteria andAuditTimeLessThan(Integer value) {
            addCriterion("audit_time <", value, "auditTime");
            return (Criteria) this;
        }

        public Criteria andAuditTimeLessThanOrEqualTo(Integer value) {
            addCriterion("audit_time <=", value, "auditTime");
            return (Criteria) this;
        }

        public Criteria andAuditTimeIn(List<Integer> values) {
            addCriterion("audit_time in", values, "auditTime");
            return (Criteria) this;
        }

        public Criteria andAuditTimeNotIn(List<Integer> values) {
            addCriterion("audit_time not in", values, "auditTime");
            return (Criteria) this;
        }

        public Criteria andAuditTimeBetween(Integer value1, Integer value2) {
            addCriterion("audit_time between", value1, value2, "auditTime");
            return (Criteria) this;
        }

        public Criteria andAuditTimeNotBetween(Integer value1, Integer value2) {
            addCriterion("audit_time not between", value1, value2, "auditTime");
            return (Criteria) this;
        }

        public Criteria andBillTimeIsNull() {
            addCriterion("bill_time is null");
            return (Criteria) this;
        }

        public Criteria andBillTimeIsNotNull() {
            addCriterion("bill_time is not null");
            return (Criteria) this;
        }

        public Criteria andBillTimeEqualTo(Integer value) {
            addCriterion("bill_time =", value, "billTime");
            return (Criteria) this;
        }

        public Criteria andBillTimeNotEqualTo(Integer value) {
            addCriterion("bill_time <>", value, "billTime");
            return (Criteria) this;
        }

        public Criteria andBillTimeGreaterThan(Integer value) {
            addCriterion("bill_time >", value, "billTime");
            return (Criteria) this;
        }

        public Criteria andBillTimeGreaterThanOrEqualTo(Integer value) {
            addCriterion("bill_time >=", value, "billTime");
            return (Criteria) this;
        }

        public Criteria andBillTimeLessThan(Integer value) {
            addCriterion("bill_time <", value, "billTime");
            return (Criteria) this;
        }

        public Criteria andBillTimeLessThanOrEqualTo(Integer value) {
            addCriterion("bill_time <=", value, "billTime");
            return (Criteria) this;
        }

        public Criteria andBillTimeIn(List<Integer> values) {
            addCriterion("bill_time in", values, "billTime");
            return (Criteria) this;
        }

        public Criteria andBillTimeNotIn(List<Integer> values) {
            addCriterion("bill_time not in", values, "billTime");
            return (Criteria) this;
        }

        public Criteria andBillTimeBetween(Integer value1, Integer value2) {
            addCriterion("bill_time between", value1, value2, "billTime");
            return (Criteria) this;
        }

        public Criteria andBillTimeNotBetween(Integer value1, Integer value2) {
            addCriterion("bill_time not between", value1, value2, "billTime");
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