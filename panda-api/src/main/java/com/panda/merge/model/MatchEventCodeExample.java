package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class MatchEventCodeExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MatchEventCodeExample() {
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

        public Criteria andSportIdEqualTo(Integer value) {
            addCriterion("sport_id =", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdNotEqualTo(Integer value) {
            addCriterion("sport_id <>", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdGreaterThan(Integer value) {
            addCriterion("sport_id >", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("sport_id >=", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdLessThan(Integer value) {
            addCriterion("sport_id <", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdLessThanOrEqualTo(Integer value) {
            addCriterion("sport_id <=", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdIn(List<Integer> values) {
            addCriterion("sport_id in", values, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdNotIn(List<Integer> values) {
            addCriterion("sport_id not in", values, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdBetween(Integer value1, Integer value2) {
            addCriterion("sport_id between", value1, value2, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdNotBetween(Integer value1, Integer value2) {
            addCriterion("sport_id not between", value1, value2, "sportId");
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

        public Criteria andEventNameIsNull() {
            addCriterion("event_name is null");
            return (Criteria) this;
        }

        public Criteria andEventNameIsNotNull() {
            addCriterion("event_name is not null");
            return (Criteria) this;
        }

        public Criteria andEventNameEqualTo(String value) {
            addCriterion("event_name =", value, "eventName");
            return (Criteria) this;
        }

        public Criteria andEventNameNotEqualTo(String value) {
            addCriterion("event_name <>", value, "eventName");
            return (Criteria) this;
        }

        public Criteria andEventNameGreaterThan(String value) {
            addCriterion("event_name >", value, "eventName");
            return (Criteria) this;
        }

        public Criteria andEventNameGreaterThanOrEqualTo(String value) {
            addCriterion("event_name >=", value, "eventName");
            return (Criteria) this;
        }

        public Criteria andEventNameLessThan(String value) {
            addCriterion("event_name <", value, "eventName");
            return (Criteria) this;
        }

        public Criteria andEventNameLessThanOrEqualTo(String value) {
            addCriterion("event_name <=", value, "eventName");
            return (Criteria) this;
        }

        public Criteria andEventNameLike(String value) {
            addCriterion("event_name like", value, "eventName");
            return (Criteria) this;
        }

        public Criteria andEventNameNotLike(String value) {
            addCriterion("event_name not like", value, "eventName");
            return (Criteria) this;
        }

        public Criteria andEventNameIn(List<String> values) {
            addCriterion("event_name in", values, "eventName");
            return (Criteria) this;
        }

        public Criteria andEventNameNotIn(List<String> values) {
            addCriterion("event_name not in", values, "eventName");
            return (Criteria) this;
        }

        public Criteria andEventNameBetween(String value1, String value2) {
            addCriterion("event_name between", value1, value2, "eventName");
            return (Criteria) this;
        }

        public Criteria andEventNameNotBetween(String value1, String value2) {
            addCriterion("event_name not between", value1, value2, "eventName");
            return (Criteria) this;
        }

        public Criteria andConfirmEventIsNull() {
            addCriterion("confirm_event is null");
            return (Criteria) this;
        }

        public Criteria andConfirmEventIsNotNull() {
            addCriterion("confirm_event is not null");
            return (Criteria) this;
        }

        public Criteria andConfirmEventEqualTo(Boolean value) {
            addCriterion("confirm_event =", value, "confirmEvent");
            return (Criteria) this;
        }

        public Criteria andConfirmEventNotEqualTo(Boolean value) {
            addCriterion("confirm_event <>", value, "confirmEvent");
            return (Criteria) this;
        }

        public Criteria andConfirmEventGreaterThan(Boolean value) {
            addCriterion("confirm_event >", value, "confirmEvent");
            return (Criteria) this;
        }

        public Criteria andConfirmEventGreaterThanOrEqualTo(Boolean value) {
            addCriterion("confirm_event >=", value, "confirmEvent");
            return (Criteria) this;
        }

        public Criteria andConfirmEventLessThan(Boolean value) {
            addCriterion("confirm_event <", value, "confirmEvent");
            return (Criteria) this;
        }

        public Criteria andConfirmEventLessThanOrEqualTo(Boolean value) {
            addCriterion("confirm_event <=", value, "confirmEvent");
            return (Criteria) this;
        }

        public Criteria andConfirmEventIn(List<Boolean> values) {
            addCriterion("confirm_event in", values, "confirmEvent");
            return (Criteria) this;
        }

        public Criteria andConfirmEventNotIn(List<Boolean> values) {
            addCriterion("confirm_event not in", values, "confirmEvent");
            return (Criteria) this;
        }

        public Criteria andConfirmEventBetween(Boolean value1, Boolean value2) {
            addCriterion("confirm_event between", value1, value2, "confirmEvent");
            return (Criteria) this;
        }

        public Criteria andConfirmEventNotBetween(Boolean value1, Boolean value2) {
            addCriterion("confirm_event not between", value1, value2, "confirmEvent");
            return (Criteria) this;
        }

        public Criteria andStandardEventIsNull() {
            addCriterion("standard_event is null");
            return (Criteria) this;
        }

        public Criteria andStandardEventIsNotNull() {
            addCriterion("standard_event is not null");
            return (Criteria) this;
        }

        public Criteria andStandardEventEqualTo(Boolean value) {
            addCriterion("standard_event =", value, "standardEvent");
            return (Criteria) this;
        }

        public Criteria andStandardEventNotEqualTo(Boolean value) {
            addCriterion("standard_event <>", value, "standardEvent");
            return (Criteria) this;
        }

        public Criteria andStandardEventGreaterThan(Boolean value) {
            addCriterion("standard_event >", value, "standardEvent");
            return (Criteria) this;
        }

        public Criteria andStandardEventGreaterThanOrEqualTo(Boolean value) {
            addCriterion("standard_event >=", value, "standardEvent");
            return (Criteria) this;
        }

        public Criteria andStandardEventLessThan(Boolean value) {
            addCriterion("standard_event <", value, "standardEvent");
            return (Criteria) this;
        }

        public Criteria andStandardEventLessThanOrEqualTo(Boolean value) {
            addCriterion("standard_event <=", value, "standardEvent");
            return (Criteria) this;
        }

        public Criteria andStandardEventIn(List<Boolean> values) {
            addCriterion("standard_event in", values, "standardEvent");
            return (Criteria) this;
        }

        public Criteria andStandardEventNotIn(List<Boolean> values) {
            addCriterion("standard_event not in", values, "standardEvent");
            return (Criteria) this;
        }

        public Criteria andStandardEventBetween(Boolean value1, Boolean value2) {
            addCriterion("standard_event between", value1, value2, "standardEvent");
            return (Criteria) this;
        }

        public Criteria andStandardEventNotBetween(Boolean value1, Boolean value2) {
            addCriterion("standard_event not between", value1, value2, "standardEvent");
            return (Criteria) this;
        }

        public Criteria andPushEventIsNull() {
            addCriterion("push_event is null");
            return (Criteria) this;
        }

        public Criteria andPushEventIsNotNull() {
            addCriterion("push_event is not null");
            return (Criteria) this;
        }

        public Criteria andPushEventEqualTo(Boolean value) {
            addCriterion("push_event =", value, "pushEvent");
            return (Criteria) this;
        }

        public Criteria andPushEventNotEqualTo(Boolean value) {
            addCriterion("push_event <>", value, "pushEvent");
            return (Criteria) this;
        }

        public Criteria andPushEventGreaterThan(Boolean value) {
            addCriterion("push_event >", value, "pushEvent");
            return (Criteria) this;
        }

        public Criteria andPushEventGreaterThanOrEqualTo(Boolean value) {
            addCriterion("push_event >=", value, "pushEvent");
            return (Criteria) this;
        }

        public Criteria andPushEventLessThan(Boolean value) {
            addCriterion("push_event <", value, "pushEvent");
            return (Criteria) this;
        }

        public Criteria andPushEventLessThanOrEqualTo(Boolean value) {
            addCriterion("push_event <=", value, "pushEvent");
            return (Criteria) this;
        }

        public Criteria andPushEventIn(List<Boolean> values) {
            addCriterion("push_event in", values, "pushEvent");
            return (Criteria) this;
        }

        public Criteria andPushEventNotIn(List<Boolean> values) {
            addCriterion("push_event not in", values, "pushEvent");
            return (Criteria) this;
        }

        public Criteria andPushEventBetween(Boolean value1, Boolean value2) {
            addCriterion("push_event between", value1, value2, "pushEvent");
            return (Criteria) this;
        }

        public Criteria andPushEventNotBetween(Boolean value1, Boolean value2) {
            addCriterion("push_event not between", value1, value2, "pushEvent");
            return (Criteria) this;
        }

        public Criteria andErrorEventIsNull() {
            addCriterion("error_event is null");
            return (Criteria) this;
        }

        public Criteria andErrorEventIsNotNull() {
            addCriterion("error_event is not null");
            return (Criteria) this;
        }

        public Criteria andErrorEventEqualTo(Boolean value) {
            addCriterion("error_event =", value, "errorEvent");
            return (Criteria) this;
        }

        public Criteria andErrorEventNotEqualTo(Boolean value) {
            addCriterion("error_event <>", value, "errorEvent");
            return (Criteria) this;
        }

        public Criteria andErrorEventGreaterThan(Boolean value) {
            addCriterion("error_event >", value, "errorEvent");
            return (Criteria) this;
        }

        public Criteria andErrorEventGreaterThanOrEqualTo(Boolean value) {
            addCriterion("error_event >=", value, "errorEvent");
            return (Criteria) this;
        }

        public Criteria andErrorEventLessThan(Boolean value) {
            addCriterion("error_event <", value, "errorEvent");
            return (Criteria) this;
        }

        public Criteria andErrorEventLessThanOrEqualTo(Boolean value) {
            addCriterion("error_event <=", value, "errorEvent");
            return (Criteria) this;
        }

        public Criteria andErrorEventIn(List<Boolean> values) {
            addCriterion("error_event in", values, "errorEvent");
            return (Criteria) this;
        }

        public Criteria andErrorEventNotIn(List<Boolean> values) {
            addCriterion("error_event not in", values, "errorEvent");
            return (Criteria) this;
        }

        public Criteria andErrorEventBetween(Boolean value1, Boolean value2) {
            addCriterion("error_event between", value1, value2, "errorEvent");
            return (Criteria) this;
        }

        public Criteria andErrorEventNotBetween(Boolean value1, Boolean value2) {
            addCriterion("error_event not between", value1, value2, "errorEvent");
            return (Criteria) this;
        }

        public Criteria andSpecialEventIsNull() {
            addCriterion("special_event is null");
            return (Criteria) this;
        }

        public Criteria andSpecialEventIsNotNull() {
            addCriterion("special_event is not null");
            return (Criteria) this;
        }

        public Criteria andSpecialEventEqualTo(Boolean value) {
            addCriterion("special_event =", value, "specialEvent");
            return (Criteria) this;
        }

        public Criteria andSpecialEventNotEqualTo(Boolean value) {
            addCriterion("special_event <>", value, "specialEvent");
            return (Criteria) this;
        }

        public Criteria andSpecialEventGreaterThan(Boolean value) {
            addCriterion("special_event >", value, "specialEvent");
            return (Criteria) this;
        }

        public Criteria andSpecialEventGreaterThanOrEqualTo(Boolean value) {
            addCriterion("special_event >=", value, "specialEvent");
            return (Criteria) this;
        }

        public Criteria andSpecialEventLessThan(Boolean value) {
            addCriterion("special_event <", value, "specialEvent");
            return (Criteria) this;
        }

        public Criteria andSpecialEventLessThanOrEqualTo(Boolean value) {
            addCriterion("special_event <=", value, "specialEvent");
            return (Criteria) this;
        }

        public Criteria andSpecialEventIn(List<Boolean> values) {
            addCriterion("special_event in", values, "specialEvent");
            return (Criteria) this;
        }

        public Criteria andSpecialEventNotIn(List<Boolean> values) {
            addCriterion("special_event not in", values, "specialEvent");
            return (Criteria) this;
        }

        public Criteria andSpecialEventBetween(Boolean value1, Boolean value2) {
            addCriterion("special_event between", value1, value2, "specialEvent");
            return (Criteria) this;
        }

        public Criteria andSpecialEventNotBetween(Boolean value1, Boolean value2) {
            addCriterion("special_event not between", value1, value2, "specialEvent");
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

        public Criteria andTemplateNoEqualTo(Integer value) {
            addCriterion("template_no =", value, "templateNo");
            return (Criteria) this;
        }

        public Criteria andTemplateNoNotEqualTo(Integer value) {
            addCriterion("template_no <>", value, "templateNo");
            return (Criteria) this;
        }

        public Criteria andTemplateNoGreaterThan(Integer value) {
            addCriterion("template_no >", value, "templateNo");
            return (Criteria) this;
        }

        public Criteria andTemplateNoGreaterThanOrEqualTo(Integer value) {
            addCriterion("template_no >=", value, "templateNo");
            return (Criteria) this;
        }

        public Criteria andTemplateNoLessThan(Integer value) {
            addCriterion("template_no <", value, "templateNo");
            return (Criteria) this;
        }

        public Criteria andTemplateNoLessThanOrEqualTo(Integer value) {
            addCriterion("template_no <=", value, "templateNo");
            return (Criteria) this;
        }

        public Criteria andTemplateNoIn(List<Integer> values) {
            addCriterion("template_no in", values, "templateNo");
            return (Criteria) this;
        }

        public Criteria andTemplateNoNotIn(List<Integer> values) {
            addCriterion("template_no not in", values, "templateNo");
            return (Criteria) this;
        }

        public Criteria andTemplateNoBetween(Integer value1, Integer value2) {
            addCriterion("template_no between", value1, value2, "templateNo");
            return (Criteria) this;
        }

        public Criteria andTemplateNoNotBetween(Integer value1, Integer value2) {
            addCriterion("template_no not between", value1, value2, "templateNo");
            return (Criteria) this;
        }

        public Criteria andAddEventIsNull() {
            addCriterion("add_event is null");
            return (Criteria) this;
        }

        public Criteria andAddEventIsNotNull() {
            addCriterion("add_event is not null");
            return (Criteria) this;
        }

        public Criteria andAddEventEqualTo(Boolean value) {
            addCriterion("add_event =", value, "addEvent");
            return (Criteria) this;
        }

        public Criteria andAddEventNotEqualTo(Boolean value) {
            addCriterion("add_event <>", value, "addEvent");
            return (Criteria) this;
        }

        public Criteria andAddEventGreaterThan(Boolean value) {
            addCriterion("add_event >", value, "addEvent");
            return (Criteria) this;
        }

        public Criteria andAddEventGreaterThanOrEqualTo(Boolean value) {
            addCriterion("add_event >=", value, "addEvent");
            return (Criteria) this;
        }

        public Criteria andAddEventLessThan(Boolean value) {
            addCriterion("add_event <", value, "addEvent");
            return (Criteria) this;
        }

        public Criteria andAddEventLessThanOrEqualTo(Boolean value) {
            addCriterion("add_event <=", value, "addEvent");
            return (Criteria) this;
        }

        public Criteria andAddEventIn(List<Boolean> values) {
            addCriterion("add_event in", values, "addEvent");
            return (Criteria) this;
        }

        public Criteria andAddEventNotIn(List<Boolean> values) {
            addCriterion("add_event not in", values, "addEvent");
            return (Criteria) this;
        }

        public Criteria andAddEventBetween(Boolean value1, Boolean value2) {
            addCriterion("add_event between", value1, value2, "addEvent");
            return (Criteria) this;
        }

        public Criteria andAddEventNotBetween(Boolean value1, Boolean value2) {
            addCriterion("add_event not between", value1, value2, "addEvent");
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