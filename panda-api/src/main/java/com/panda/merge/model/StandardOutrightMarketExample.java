package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class StandardOutrightMarketExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public StandardOutrightMarketExample() {
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

        public Criteria andMarketCategoryIdIsNull() {
            addCriterion("market_category_id is null");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdIsNotNull() {
            addCriterion("market_category_id is not null");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdEqualTo(Long value) {
            addCriterion("market_category_id =", value, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdNotEqualTo(Long value) {
            addCriterion("market_category_id <>", value, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdGreaterThan(Long value) {
            addCriterion("market_category_id >", value, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdGreaterThanOrEqualTo(Long value) {
            addCriterion("market_category_id >=", value, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdLessThan(Long value) {
            addCriterion("market_category_id <", value, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdLessThanOrEqualTo(Long value) {
            addCriterion("market_category_id <=", value, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdIn(List<Long> values) {
            addCriterion("market_category_id in", values, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdNotIn(List<Long> values) {
            addCriterion("market_category_id not in", values, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdBetween(Long value1, Long value2) {
            addCriterion("market_category_id between", value1, value2, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdNotBetween(Long value1, Long value2) {
            addCriterion("market_category_id not between", value1, value2, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketSellStatusIsNull() {
            addCriterion("market_sell_status is null");
            return (Criteria) this;
        }

        public Criteria andMarketSellStatusIsNotNull() {
            addCriterion("market_sell_status is not null");
            return (Criteria) this;
        }

        public Criteria andMarketSellStatusEqualTo(String value) {
            addCriterion("market_sell_status =", value, "marketSellStatus");
            return (Criteria) this;
        }

        public Criteria andMarketSellStatusNotEqualTo(String value) {
            addCriterion("market_sell_status <>", value, "marketSellStatus");
            return (Criteria) this;
        }

        public Criteria andMarketSellStatusGreaterThan(String value) {
            addCriterion("market_sell_status >", value, "marketSellStatus");
            return (Criteria) this;
        }

        public Criteria andMarketSellStatusGreaterThanOrEqualTo(String value) {
            addCriterion("market_sell_status >=", value, "marketSellStatus");
            return (Criteria) this;
        }

        public Criteria andMarketSellStatusLessThan(String value) {
            addCriterion("market_sell_status <", value, "marketSellStatus");
            return (Criteria) this;
        }

        public Criteria andMarketSellStatusLessThanOrEqualTo(String value) {
            addCriterion("market_sell_status <=", value, "marketSellStatus");
            return (Criteria) this;
        }

        public Criteria andMarketSellStatusLike(String value) {
            addCriterion("market_sell_status like", value, "marketSellStatus");
            return (Criteria) this;
        }

        public Criteria andMarketSellStatusNotLike(String value) {
            addCriterion("market_sell_status not like", value, "marketSellStatus");
            return (Criteria) this;
        }

        public Criteria andMarketSellStatusIn(List<String> values) {
            addCriterion("market_sell_status in", values, "marketSellStatus");
            return (Criteria) this;
        }

        public Criteria andMarketSellStatusNotIn(List<String> values) {
            addCriterion("market_sell_status not in", values, "marketSellStatus");
            return (Criteria) this;
        }

        public Criteria andMarketSellStatusBetween(String value1, String value2) {
            addCriterion("market_sell_status between", value1, value2, "marketSellStatus");
            return (Criteria) this;
        }

        public Criteria andMarketSellStatusNotBetween(String value1, String value2) {
            addCriterion("market_sell_status not between", value1, value2, "marketSellStatus");
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

        public Criteria andMarketStatusIsNull() {
            addCriterion("market_status is null");
            return (Criteria) this;
        }

        public Criteria andMarketStatusIsNotNull() {
            addCriterion("market_status is not null");
            return (Criteria) this;
        }

        public Criteria andMarketStatusEqualTo(Integer value) {
            addCriterion("market_status =", value, "marketStatus");
            return (Criteria) this;
        }

        public Criteria andMarketStatusNotEqualTo(Integer value) {
            addCriterion("market_status <>", value, "marketStatus");
            return (Criteria) this;
        }

        public Criteria andMarketStatusGreaterThan(Integer value) {
            addCriterion("market_status >", value, "marketStatus");
            return (Criteria) this;
        }

        public Criteria andMarketStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("market_status >=", value, "marketStatus");
            return (Criteria) this;
        }

        public Criteria andMarketStatusLessThan(Integer value) {
            addCriterion("market_status <", value, "marketStatus");
            return (Criteria) this;
        }

        public Criteria andMarketStatusLessThanOrEqualTo(Integer value) {
            addCriterion("market_status <=", value, "marketStatus");
            return (Criteria) this;
        }

        public Criteria andMarketStatusIn(List<Integer> values) {
            addCriterion("market_status in", values, "marketStatus");
            return (Criteria) this;
        }

        public Criteria andMarketStatusNotIn(List<Integer> values) {
            addCriterion("market_status not in", values, "marketStatus");
            return (Criteria) this;
        }

        public Criteria andMarketStatusBetween(Integer value1, Integer value2) {
            addCriterion("market_status between", value1, value2, "marketStatus");
            return (Criteria) this;
        }

        public Criteria andMarketStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("market_status not between", value1, value2, "marketStatus");
            return (Criteria) this;
        }

        public Criteria andMarketOrderNumberIsNull() {
            addCriterion("market_order_number is null");
            return (Criteria) this;
        }

        public Criteria andMarketOrderNumberIsNotNull() {
            addCriterion("market_order_number is not null");
            return (Criteria) this;
        }

        public Criteria andMarketOrderNumberEqualTo(Integer value) {
            addCriterion("market_order_number =", value, "marketOrderNumber");
            return (Criteria) this;
        }

        public Criteria andMarketOrderNumberNotEqualTo(Integer value) {
            addCriterion("market_order_number <>", value, "marketOrderNumber");
            return (Criteria) this;
        }

        public Criteria andMarketOrderNumberGreaterThan(Integer value) {
            addCriterion("market_order_number >", value, "marketOrderNumber");
            return (Criteria) this;
        }

        public Criteria andMarketOrderNumberGreaterThanOrEqualTo(Integer value) {
            addCriterion("market_order_number >=", value, "marketOrderNumber");
            return (Criteria) this;
        }

        public Criteria andMarketOrderNumberLessThan(Integer value) {
            addCriterion("market_order_number <", value, "marketOrderNumber");
            return (Criteria) this;
        }

        public Criteria andMarketOrderNumberLessThanOrEqualTo(Integer value) {
            addCriterion("market_order_number <=", value, "marketOrderNumber");
            return (Criteria) this;
        }

        public Criteria andMarketOrderNumberIn(List<Integer> values) {
            addCriterion("market_order_number in", values, "marketOrderNumber");
            return (Criteria) this;
        }

        public Criteria andMarketOrderNumberNotIn(List<Integer> values) {
            addCriterion("market_order_number not in", values, "marketOrderNumber");
            return (Criteria) this;
        }

        public Criteria andMarketOrderNumberBetween(Integer value1, Integer value2) {
            addCriterion("market_order_number between", value1, value2, "marketOrderNumber");
            return (Criteria) this;
        }

        public Criteria andMarketOrderNumberNotBetween(Integer value1, Integer value2) {
            addCriterion("market_order_number not between", value1, value2, "marketOrderNumber");
            return (Criteria) this;
        }

        public Criteria andNextClosingTimeIsNull() {
            addCriterion("next_closing_time is null");
            return (Criteria) this;
        }

        public Criteria andNextClosingTimeIsNotNull() {
            addCriterion("next_closing_time is not null");
            return (Criteria) this;
        }

        public Criteria andNextClosingTimeEqualTo(Long value) {
            addCriterion("next_closing_time =", value, "nextClosingTime");
            return (Criteria) this;
        }

        public Criteria andNextClosingTimeNotEqualTo(Long value) {
            addCriterion("next_closing_time <>", value, "nextClosingTime");
            return (Criteria) this;
        }

        public Criteria andNextClosingTimeGreaterThan(Long value) {
            addCriterion("next_closing_time >", value, "nextClosingTime");
            return (Criteria) this;
        }

        public Criteria andNextClosingTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("next_closing_time >=", value, "nextClosingTime");
            return (Criteria) this;
        }

        public Criteria andNextClosingTimeLessThan(Long value) {
            addCriterion("next_closing_time <", value, "nextClosingTime");
            return (Criteria) this;
        }

        public Criteria andNextClosingTimeLessThanOrEqualTo(Long value) {
            addCriterion("next_closing_time <=", value, "nextClosingTime");
            return (Criteria) this;
        }

        public Criteria andNextClosingTimeIn(List<Long> values) {
            addCriterion("next_closing_time in", values, "nextClosingTime");
            return (Criteria) this;
        }

        public Criteria andNextClosingTimeNotIn(List<Long> values) {
            addCriterion("next_closing_time not in", values, "nextClosingTime");
            return (Criteria) this;
        }

        public Criteria andNextClosingTimeBetween(Long value1, Long value2) {
            addCriterion("next_closing_time between", value1, value2, "nextClosingTime");
            return (Criteria) this;
        }

        public Criteria andNextClosingTimeNotBetween(Long value1, Long value2) {
            addCriterion("next_closing_time not between", value1, value2, "nextClosingTime");
            return (Criteria) this;
        }

        public Criteria andLinkIdIsNull() {
            addCriterion("link_id is null");
            return (Criteria) this;
        }

        public Criteria andLinkIdIsNotNull() {
            addCriterion("link_id is not null");
            return (Criteria) this;
        }

        public Criteria andLinkIdEqualTo(String value) {
            addCriterion("link_id =", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdNotEqualTo(String value) {
            addCriterion("link_id <>", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdGreaterThan(String value) {
            addCriterion("link_id >", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdGreaterThanOrEqualTo(String value) {
            addCriterion("link_id >=", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdLessThan(String value) {
            addCriterion("link_id <", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdLessThanOrEqualTo(String value) {
            addCriterion("link_id <=", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdLike(String value) {
            addCriterion("link_id like", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdNotLike(String value) {
            addCriterion("link_id not like", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdIn(List<String> values) {
            addCriterion("link_id in", values, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdNotIn(List<String> values) {
            addCriterion("link_id not in", values, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdBetween(String value1, String value2) {
            addCriterion("link_id between", value1, value2, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdNotBetween(String value1, String value2) {
            addCriterion("link_id not between", value1, value2, "linkId");
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