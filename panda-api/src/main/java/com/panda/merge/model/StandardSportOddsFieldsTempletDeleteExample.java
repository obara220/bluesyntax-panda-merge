package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class StandardSportOddsFieldsTempletDeleteExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public StandardSportOddsFieldsTempletDeleteExample() {
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

        public Criteria andNameIsNull() {
            addCriterion("name is null");
            return (Criteria) this;
        }

        public Criteria andNameIsNotNull() {
            addCriterion("name is not null");
            return (Criteria) this;
        }

        public Criteria andNameEqualTo(String value) {
            addCriterion("name =", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotEqualTo(String value) {
            addCriterion("name <>", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThan(String value) {
            addCriterion("name >", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThanOrEqualTo(String value) {
            addCriterion("name >=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThan(String value) {
            addCriterion("name <", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThanOrEqualTo(String value) {
            addCriterion("name <=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLike(String value) {
            addCriterion("name like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotLike(String value) {
            addCriterion("name not like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameIn(List<String> values) {
            addCriterion("name in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotIn(List<String> values) {
            addCriterion("name not in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameBetween(String value1, String value2) {
            addCriterion("name between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotBetween(String value1, String value2) {
            addCriterion("name not between", value1, value2, "name");
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

        public Criteria andAddition1IsNull() {
            addCriterion("addition1 is null");
            return (Criteria) this;
        }

        public Criteria andAddition1IsNotNull() {
            addCriterion("addition1 is not null");
            return (Criteria) this;
        }

        public Criteria andAddition1EqualTo(String value) {
            addCriterion("addition1 =", value, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1NotEqualTo(String value) {
            addCriterion("addition1 <>", value, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1GreaterThan(String value) {
            addCriterion("addition1 >", value, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1GreaterThanOrEqualTo(String value) {
            addCriterion("addition1 >=", value, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1LessThan(String value) {
            addCriterion("addition1 <", value, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1LessThanOrEqualTo(String value) {
            addCriterion("addition1 <=", value, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1Like(String value) {
            addCriterion("addition1 like", value, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1NotLike(String value) {
            addCriterion("addition1 not like", value, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1In(List<String> values) {
            addCriterion("addition1 in", values, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1NotIn(List<String> values) {
            addCriterion("addition1 not in", values, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1Between(String value1, String value2) {
            addCriterion("addition1 between", value1, value2, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1NotBetween(String value1, String value2) {
            addCriterion("addition1 not between", value1, value2, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition2IsNull() {
            addCriterion("addition2 is null");
            return (Criteria) this;
        }

        public Criteria andAddition2IsNotNull() {
            addCriterion("addition2 is not null");
            return (Criteria) this;
        }

        public Criteria andAddition2EqualTo(String value) {
            addCriterion("addition2 =", value, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2NotEqualTo(String value) {
            addCriterion("addition2 <>", value, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2GreaterThan(String value) {
            addCriterion("addition2 >", value, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2GreaterThanOrEqualTo(String value) {
            addCriterion("addition2 >=", value, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2LessThan(String value) {
            addCriterion("addition2 <", value, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2LessThanOrEqualTo(String value) {
            addCriterion("addition2 <=", value, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2Like(String value) {
            addCriterion("addition2 like", value, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2NotLike(String value) {
            addCriterion("addition2 not like", value, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2In(List<String> values) {
            addCriterion("addition2 in", values, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2NotIn(List<String> values) {
            addCriterion("addition2 not in", values, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2Between(String value1, String value2) {
            addCriterion("addition2 between", value1, value2, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2NotBetween(String value1, String value2) {
            addCriterion("addition2 not between", value1, value2, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition3IsNull() {
            addCriterion("addition3 is null");
            return (Criteria) this;
        }

        public Criteria andAddition3IsNotNull() {
            addCriterion("addition3 is not null");
            return (Criteria) this;
        }

        public Criteria andAddition3EqualTo(String value) {
            addCriterion("addition3 =", value, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3NotEqualTo(String value) {
            addCriterion("addition3 <>", value, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3GreaterThan(String value) {
            addCriterion("addition3 >", value, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3GreaterThanOrEqualTo(String value) {
            addCriterion("addition3 >=", value, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3LessThan(String value) {
            addCriterion("addition3 <", value, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3LessThanOrEqualTo(String value) {
            addCriterion("addition3 <=", value, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3Like(String value) {
            addCriterion("addition3 like", value, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3NotLike(String value) {
            addCriterion("addition3 not like", value, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3In(List<String> values) {
            addCriterion("addition3 in", values, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3NotIn(List<String> values) {
            addCriterion("addition3 not in", values, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3Between(String value1, String value2) {
            addCriterion("addition3 between", value1, value2, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3NotBetween(String value1, String value2) {
            addCriterion("addition3 not between", value1, value2, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition4IsNull() {
            addCriterion("addition4 is null");
            return (Criteria) this;
        }

        public Criteria andAddition4IsNotNull() {
            addCriterion("addition4 is not null");
            return (Criteria) this;
        }

        public Criteria andAddition4EqualTo(String value) {
            addCriterion("addition4 =", value, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4NotEqualTo(String value) {
            addCriterion("addition4 <>", value, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4GreaterThan(String value) {
            addCriterion("addition4 >", value, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4GreaterThanOrEqualTo(String value) {
            addCriterion("addition4 >=", value, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4LessThan(String value) {
            addCriterion("addition4 <", value, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4LessThanOrEqualTo(String value) {
            addCriterion("addition4 <=", value, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4Like(String value) {
            addCriterion("addition4 like", value, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4NotLike(String value) {
            addCriterion("addition4 not like", value, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4In(List<String> values) {
            addCriterion("addition4 in", values, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4NotIn(List<String> values) {
            addCriterion("addition4 not in", values, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4Between(String value1, String value2) {
            addCriterion("addition4 between", value1, value2, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4NotBetween(String value1, String value2) {
            addCriterion("addition4 not between", value1, value2, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition5IsNull() {
            addCriterion("addition5 is null");
            return (Criteria) this;
        }

        public Criteria andAddition5IsNotNull() {
            addCriterion("addition5 is not null");
            return (Criteria) this;
        }

        public Criteria andAddition5EqualTo(String value) {
            addCriterion("addition5 =", value, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5NotEqualTo(String value) {
            addCriterion("addition5 <>", value, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5GreaterThan(String value) {
            addCriterion("addition5 >", value, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5GreaterThanOrEqualTo(String value) {
            addCriterion("addition5 >=", value, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5LessThan(String value) {
            addCriterion("addition5 <", value, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5LessThanOrEqualTo(String value) {
            addCriterion("addition5 <=", value, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5Like(String value) {
            addCriterion("addition5 like", value, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5NotLike(String value) {
            addCriterion("addition5 not like", value, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5In(List<String> values) {
            addCriterion("addition5 in", values, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5NotIn(List<String> values) {
            addCriterion("addition5 not in", values, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5Between(String value1, String value2) {
            addCriterion("addition5 between", value1, value2, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5NotBetween(String value1, String value2) {
            addCriterion("addition5 not between", value1, value2, "addition5");
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