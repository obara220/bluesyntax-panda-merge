package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class ConfigMarketStatusTradeExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ConfigMarketStatusTradeExample() {
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

        public Criteria andRelationMarketIdIsNull() {
            addCriterion("relation_market_id is null");
            return (Criteria) this;
        }

        public Criteria andRelationMarketIdIsNotNull() {
            addCriterion("relation_market_id is not null");
            return (Criteria) this;
        }

        public Criteria andRelationMarketIdEqualTo(Long value) {
            addCriterion("relation_market_id =", value, "relationMarketId");
            return (Criteria) this;
        }

        public Criteria andRelationMarketIdNotEqualTo(Long value) {
            addCriterion("relation_market_id <>", value, "relationMarketId");
            return (Criteria) this;
        }

        public Criteria andRelationMarketIdGreaterThan(Long value) {
            addCriterion("relation_market_id >", value, "relationMarketId");
            return (Criteria) this;
        }

        public Criteria andRelationMarketIdGreaterThanOrEqualTo(Long value) {
            addCriterion("relation_market_id >=", value, "relationMarketId");
            return (Criteria) this;
        }

        public Criteria andRelationMarketIdLessThan(Long value) {
            addCriterion("relation_market_id <", value, "relationMarketId");
            return (Criteria) this;
        }

        public Criteria andRelationMarketIdLessThanOrEqualTo(Long value) {
            addCriterion("relation_market_id <=", value, "relationMarketId");
            return (Criteria) this;
        }

        public Criteria andRelationMarketIdIn(List<Long> values) {
            addCriterion("relation_market_id in", values, "relationMarketId");
            return (Criteria) this;
        }

        public Criteria andRelationMarketIdNotIn(List<Long> values) {
            addCriterion("relation_market_id not in", values, "relationMarketId");
            return (Criteria) this;
        }

        public Criteria andRelationMarketIdBetween(Long value1, Long value2) {
            addCriterion("relation_market_id between", value1, value2, "relationMarketId");
            return (Criteria) this;
        }

        public Criteria andRelationMarketIdNotBetween(Long value1, Long value2) {
            addCriterion("relation_market_id not between", value1, value2, "relationMarketId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchInfoIdIsNull() {
            addCriterion("standard_match_info_id is null");
            return (Criteria) this;
        }

        public Criteria andStandardMatchInfoIdIsNotNull() {
            addCriterion("standard_match_info_id is not null");
            return (Criteria) this;
        }

        public Criteria andStandardMatchInfoIdEqualTo(Long value) {
            addCriterion("standard_match_info_id =", value, "standardMatchInfoId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchInfoIdNotEqualTo(Long value) {
            addCriterion("standard_match_info_id <>", value, "standardMatchInfoId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchInfoIdGreaterThan(Long value) {
            addCriterion("standard_match_info_id >", value, "standardMatchInfoId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchInfoIdGreaterThanOrEqualTo(Long value) {
            addCriterion("standard_match_info_id >=", value, "standardMatchInfoId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchInfoIdLessThan(Long value) {
            addCriterion("standard_match_info_id <", value, "standardMatchInfoId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchInfoIdLessThanOrEqualTo(Long value) {
            addCriterion("standard_match_info_id <=", value, "standardMatchInfoId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchInfoIdIn(List<Long> values) {
            addCriterion("standard_match_info_id in", values, "standardMatchInfoId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchInfoIdNotIn(List<Long> values) {
            addCriterion("standard_match_info_id not in", values, "standardMatchInfoId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchInfoIdBetween(Long value1, Long value2) {
            addCriterion("standard_match_info_id between", value1, value2, "standardMatchInfoId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchInfoIdNotBetween(Long value1, Long value2) {
            addCriterion("standard_match_info_id not between", value1, value2, "standardMatchInfoId");
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

        public Criteria andMarketTypeIsNull() {
            addCriterion("market_type is null");
            return (Criteria) this;
        }

        public Criteria andMarketTypeIsNotNull() {
            addCriterion("market_type is not null");
            return (Criteria) this;
        }

        public Criteria andMarketTypeEqualTo(Integer value) {
            addCriterion("market_type =", value, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeNotEqualTo(Integer value) {
            addCriterion("market_type <>", value, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeGreaterThan(Integer value) {
            addCriterion("market_type >", value, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("market_type >=", value, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeLessThan(Integer value) {
            addCriterion("market_type <", value, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeLessThanOrEqualTo(Integer value) {
            addCriterion("market_type <=", value, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeIn(List<Integer> values) {
            addCriterion("market_type in", values, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeNotIn(List<Integer> values) {
            addCriterion("market_type not in", values, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeBetween(Integer value1, Integer value2) {
            addCriterion("market_type between", value1, value2, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("market_type not between", value1, value2, "marketType");
            return (Criteria) this;
        }

        public Criteria andAddtionIsNull() {
            addCriterion("addtion is null");
            return (Criteria) this;
        }

        public Criteria andAddtionIsNotNull() {
            addCriterion("addtion is not null");
            return (Criteria) this;
        }

        public Criteria andAddtionEqualTo(String value) {
            addCriterion("addtion =", value, "addtion");
            return (Criteria) this;
        }

        public Criteria andAddtionNotEqualTo(String value) {
            addCriterion("addtion <>", value, "addtion");
            return (Criteria) this;
        }

        public Criteria andAddtionGreaterThan(String value) {
            addCriterion("addtion >", value, "addtion");
            return (Criteria) this;
        }

        public Criteria andAddtionGreaterThanOrEqualTo(String value) {
            addCriterion("addtion >=", value, "addtion");
            return (Criteria) this;
        }

        public Criteria andAddtionLessThan(String value) {
            addCriterion("addtion <", value, "addtion");
            return (Criteria) this;
        }

        public Criteria andAddtionLessThanOrEqualTo(String value) {
            addCriterion("addtion <=", value, "addtion");
            return (Criteria) this;
        }

        public Criteria andAddtionLike(String value) {
            addCriterion("addtion like", value, "addtion");
            return (Criteria) this;
        }

        public Criteria andAddtionNotLike(String value) {
            addCriterion("addtion not like", value, "addtion");
            return (Criteria) this;
        }

        public Criteria andAddtionIn(List<String> values) {
            addCriterion("addtion in", values, "addtion");
            return (Criteria) this;
        }

        public Criteria andAddtionNotIn(List<String> values) {
            addCriterion("addtion not in", values, "addtion");
            return (Criteria) this;
        }

        public Criteria andAddtionBetween(String value1, String value2) {
            addCriterion("addtion between", value1, value2, "addtion");
            return (Criteria) this;
        }

        public Criteria andAddtionNotBetween(String value1, String value2) {
            addCriterion("addtion not between", value1, value2, "addtion");
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

        public Criteria andOperaterIdIsNull() {
            addCriterion("operater_id is null");
            return (Criteria) this;
        }

        public Criteria andOperaterIdIsNotNull() {
            addCriterion("operater_id is not null");
            return (Criteria) this;
        }

        public Criteria andOperaterIdEqualTo(Long value) {
            addCriterion("operater_id =", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdNotEqualTo(Long value) {
            addCriterion("operater_id <>", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdGreaterThan(Long value) {
            addCriterion("operater_id >", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdGreaterThanOrEqualTo(Long value) {
            addCriterion("operater_id >=", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdLessThan(Long value) {
            addCriterion("operater_id <", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdLessThanOrEqualTo(Long value) {
            addCriterion("operater_id <=", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdIn(List<Long> values) {
            addCriterion("operater_id in", values, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdNotIn(List<Long> values) {
            addCriterion("operater_id not in", values, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdBetween(Long value1, Long value2) {
            addCriterion("operater_id between", value1, value2, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdNotBetween(Long value1, Long value2) {
            addCriterion("operater_id not between", value1, value2, "operaterId");
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