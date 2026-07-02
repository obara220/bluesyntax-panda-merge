package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class ConfigStandardThirdOddsTempletRelationExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ConfigStandardThirdOddsTempletRelationExample() {
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

        public Criteria andStandardCategoryIdIsNull() {
            addCriterion("standard_category_id is null");
            return (Criteria) this;
        }

        public Criteria andStandardCategoryIdIsNotNull() {
            addCriterion("standard_category_id is not null");
            return (Criteria) this;
        }

        public Criteria andStandardCategoryIdEqualTo(Integer value) {
            addCriterion("standard_category_id =", value, "standardCategoryId");
            return (Criteria) this;
        }

        public Criteria andStandardCategoryIdNotEqualTo(Integer value) {
            addCriterion("standard_category_id <>", value, "standardCategoryId");
            return (Criteria) this;
        }

        public Criteria andStandardCategoryIdGreaterThan(Integer value) {
            addCriterion("standard_category_id >", value, "standardCategoryId");
            return (Criteria) this;
        }

        public Criteria andStandardCategoryIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("standard_category_id >=", value, "standardCategoryId");
            return (Criteria) this;
        }

        public Criteria andStandardCategoryIdLessThan(Integer value) {
            addCriterion("standard_category_id <", value, "standardCategoryId");
            return (Criteria) this;
        }

        public Criteria andStandardCategoryIdLessThanOrEqualTo(Integer value) {
            addCriterion("standard_category_id <=", value, "standardCategoryId");
            return (Criteria) this;
        }

        public Criteria andStandardCategoryIdIn(List<Integer> values) {
            addCriterion("standard_category_id in", values, "standardCategoryId");
            return (Criteria) this;
        }

        public Criteria andStandardCategoryIdNotIn(List<Integer> values) {
            addCriterion("standard_category_id not in", values, "standardCategoryId");
            return (Criteria) this;
        }

        public Criteria andStandardCategoryIdBetween(Integer value1, Integer value2) {
            addCriterion("standard_category_id between", value1, value2, "standardCategoryId");
            return (Criteria) this;
        }

        public Criteria andStandardCategoryIdNotBetween(Integer value1, Integer value2) {
            addCriterion("standard_category_id not between", value1, value2, "standardCategoryId");
            return (Criteria) this;
        }

        public Criteria andSportTypeIsNull() {
            addCriterion("sport_type is null");
            return (Criteria) this;
        }

        public Criteria andSportTypeIsNotNull() {
            addCriterion("sport_type is not null");
            return (Criteria) this;
        }

        public Criteria andSportTypeEqualTo(String value) {
            addCriterion("sport_type =", value, "sportType");
            return (Criteria) this;
        }

        public Criteria andSportTypeNotEqualTo(String value) {
            addCriterion("sport_type <>", value, "sportType");
            return (Criteria) this;
        }

        public Criteria andSportTypeGreaterThan(String value) {
            addCriterion("sport_type >", value, "sportType");
            return (Criteria) this;
        }

        public Criteria andSportTypeGreaterThanOrEqualTo(String value) {
            addCriterion("sport_type >=", value, "sportType");
            return (Criteria) this;
        }

        public Criteria andSportTypeLessThan(String value) {
            addCriterion("sport_type <", value, "sportType");
            return (Criteria) this;
        }

        public Criteria andSportTypeLessThanOrEqualTo(String value) {
            addCriterion("sport_type <=", value, "sportType");
            return (Criteria) this;
        }

        public Criteria andSportTypeLike(String value) {
            addCriterion("sport_type like", value, "sportType");
            return (Criteria) this;
        }

        public Criteria andSportTypeNotLike(String value) {
            addCriterion("sport_type not like", value, "sportType");
            return (Criteria) this;
        }

        public Criteria andSportTypeIn(List<String> values) {
            addCriterion("sport_type in", values, "sportType");
            return (Criteria) this;
        }

        public Criteria andSportTypeNotIn(List<String> values) {
            addCriterion("sport_type not in", values, "sportType");
            return (Criteria) this;
        }

        public Criteria andSportTypeBetween(String value1, String value2) {
            addCriterion("sport_type between", value1, value2, "sportType");
            return (Criteria) this;
        }

        public Criteria andSportTypeNotBetween(String value1, String value2) {
            addCriterion("sport_type not between", value1, value2, "sportType");
            return (Criteria) this;
        }

        public Criteria andPdNameIsNull() {
            addCriterion("pd_name is null");
            return (Criteria) this;
        }

        public Criteria andPdNameIsNotNull() {
            addCriterion("pd_name is not null");
            return (Criteria) this;
        }

        public Criteria andPdNameEqualTo(String value) {
            addCriterion("pd_name =", value, "pdName");
            return (Criteria) this;
        }

        public Criteria andPdNameNotEqualTo(String value) {
            addCriterion("pd_name <>", value, "pdName");
            return (Criteria) this;
        }

        public Criteria andPdNameGreaterThan(String value) {
            addCriterion("pd_name >", value, "pdName");
            return (Criteria) this;
        }

        public Criteria andPdNameGreaterThanOrEqualTo(String value) {
            addCriterion("pd_name >=", value, "pdName");
            return (Criteria) this;
        }

        public Criteria andPdNameLessThan(String value) {
            addCriterion("pd_name <", value, "pdName");
            return (Criteria) this;
        }

        public Criteria andPdNameLessThanOrEqualTo(String value) {
            addCriterion("pd_name <=", value, "pdName");
            return (Criteria) this;
        }

        public Criteria andPdNameLike(String value) {
            addCriterion("pd_name like", value, "pdName");
            return (Criteria) this;
        }

        public Criteria andPdNameNotLike(String value) {
            addCriterion("pd_name not like", value, "pdName");
            return (Criteria) this;
        }

        public Criteria andPdNameIn(List<String> values) {
            addCriterion("pd_name in", values, "pdName");
            return (Criteria) this;
        }

        public Criteria andPdNameNotIn(List<String> values) {
            addCriterion("pd_name not in", values, "pdName");
            return (Criteria) this;
        }

        public Criteria andPdNameBetween(String value1, String value2) {
            addCriterion("pd_name between", value1, value2, "pdName");
            return (Criteria) this;
        }

        public Criteria andPdNameNotBetween(String value1, String value2) {
            addCriterion("pd_name not between", value1, value2, "pdName");
            return (Criteria) this;
        }

        public Criteria andDataSourceCodeIsNull() {
            addCriterion("data_source_code is null");
            return (Criteria) this;
        }

        public Criteria andDataSourceCodeIsNotNull() {
            addCriterion("data_source_code is not null");
            return (Criteria) this;
        }

        public Criteria andDataSourceCodeEqualTo(String value) {
            addCriterion("data_source_code =", value, "dataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDataSourceCodeNotEqualTo(String value) {
            addCriterion("data_source_code <>", value, "dataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDataSourceCodeGreaterThan(String value) {
            addCriterion("data_source_code >", value, "dataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDataSourceCodeGreaterThanOrEqualTo(String value) {
            addCriterion("data_source_code >=", value, "dataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDataSourceCodeLessThan(String value) {
            addCriterion("data_source_code <", value, "dataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDataSourceCodeLessThanOrEqualTo(String value) {
            addCriterion("data_source_code <=", value, "dataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDataSourceCodeLike(String value) {
            addCriterion("data_source_code like", value, "dataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDataSourceCodeNotLike(String value) {
            addCriterion("data_source_code not like", value, "dataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDataSourceCodeIn(List<String> values) {
            addCriterion("data_source_code in", values, "dataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDataSourceCodeNotIn(List<String> values) {
            addCriterion("data_source_code not in", values, "dataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDataSourceCodeBetween(String value1, String value2) {
            addCriterion("data_source_code between", value1, value2, "dataSourceCode");
            return (Criteria) this;
        }

        public Criteria andDataSourceCodeNotBetween(String value1, String value2) {
            addCriterion("data_source_code not between", value1, value2, "dataSourceCode");
            return (Criteria) this;
        }

        public Criteria andThirdCategoryIdIsNull() {
            addCriterion("third_category_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdCategoryIdIsNotNull() {
            addCriterion("third_category_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdCategoryIdEqualTo(String value) {
            addCriterion("third_category_id =", value, "thirdCategoryId");
            return (Criteria) this;
        }

        public Criteria andThirdCategoryIdNotEqualTo(String value) {
            addCriterion("third_category_id <>", value, "thirdCategoryId");
            return (Criteria) this;
        }

        public Criteria andThirdCategoryIdGreaterThan(String value) {
            addCriterion("third_category_id >", value, "thirdCategoryId");
            return (Criteria) this;
        }

        public Criteria andThirdCategoryIdGreaterThanOrEqualTo(String value) {
            addCriterion("third_category_id >=", value, "thirdCategoryId");
            return (Criteria) this;
        }

        public Criteria andThirdCategoryIdLessThan(String value) {
            addCriterion("third_category_id <", value, "thirdCategoryId");
            return (Criteria) this;
        }

        public Criteria andThirdCategoryIdLessThanOrEqualTo(String value) {
            addCriterion("third_category_id <=", value, "thirdCategoryId");
            return (Criteria) this;
        }

        public Criteria andThirdCategoryIdLike(String value) {
            addCriterion("third_category_id like", value, "thirdCategoryId");
            return (Criteria) this;
        }

        public Criteria andThirdCategoryIdNotLike(String value) {
            addCriterion("third_category_id not like", value, "thirdCategoryId");
            return (Criteria) this;
        }

        public Criteria andThirdCategoryIdIn(List<String> values) {
            addCriterion("third_category_id in", values, "thirdCategoryId");
            return (Criteria) this;
        }

        public Criteria andThirdCategoryIdNotIn(List<String> values) {
            addCriterion("third_category_id not in", values, "thirdCategoryId");
            return (Criteria) this;
        }

        public Criteria andThirdCategoryIdBetween(String value1, String value2) {
            addCriterion("third_category_id between", value1, value2, "thirdCategoryId");
            return (Criteria) this;
        }

        public Criteria andThirdCategoryIdNotBetween(String value1, String value2) {
            addCriterion("third_category_id not between", value1, value2, "thirdCategoryId");
            return (Criteria) this;
        }

        public Criteria andThirdTempletSourceIdIsNull() {
            addCriterion("third_templet_source_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdTempletSourceIdIsNotNull() {
            addCriterion("third_templet_source_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdTempletSourceIdEqualTo(String value) {
            addCriterion("third_templet_source_id =", value, "thirdTempletSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTempletSourceIdNotEqualTo(String value) {
            addCriterion("third_templet_source_id <>", value, "thirdTempletSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTempletSourceIdGreaterThan(String value) {
            addCriterion("third_templet_source_id >", value, "thirdTempletSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTempletSourceIdGreaterThanOrEqualTo(String value) {
            addCriterion("third_templet_source_id >=", value, "thirdTempletSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTempletSourceIdLessThan(String value) {
            addCriterion("third_templet_source_id <", value, "thirdTempletSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTempletSourceIdLessThanOrEqualTo(String value) {
            addCriterion("third_templet_source_id <=", value, "thirdTempletSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTempletSourceIdLike(String value) {
            addCriterion("third_templet_source_id like", value, "thirdTempletSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTempletSourceIdNotLike(String value) {
            addCriterion("third_templet_source_id not like", value, "thirdTempletSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTempletSourceIdIn(List<String> values) {
            addCriterion("third_templet_source_id in", values, "thirdTempletSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTempletSourceIdNotIn(List<String> values) {
            addCriterion("third_templet_source_id not in", values, "thirdTempletSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTempletSourceIdBetween(String value1, String value2) {
            addCriterion("third_templet_source_id between", value1, value2, "thirdTempletSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTempletSourceIdNotBetween(String value1, String value2) {
            addCriterion("third_templet_source_id not between", value1, value2, "thirdTempletSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTempletNameIsNull() {
            addCriterion("third_templet_name is null");
            return (Criteria) this;
        }

        public Criteria andThirdTempletNameIsNotNull() {
            addCriterion("third_templet_name is not null");
            return (Criteria) this;
        }

        public Criteria andThirdTempletNameEqualTo(String value) {
            addCriterion("third_templet_name =", value, "thirdTempletName");
            return (Criteria) this;
        }

        public Criteria andThirdTempletNameNotEqualTo(String value) {
            addCriterion("third_templet_name <>", value, "thirdTempletName");
            return (Criteria) this;
        }

        public Criteria andThirdTempletNameGreaterThan(String value) {
            addCriterion("third_templet_name >", value, "thirdTempletName");
            return (Criteria) this;
        }

        public Criteria andThirdTempletNameGreaterThanOrEqualTo(String value) {
            addCriterion("third_templet_name >=", value, "thirdTempletName");
            return (Criteria) this;
        }

        public Criteria andThirdTempletNameLessThan(String value) {
            addCriterion("third_templet_name <", value, "thirdTempletName");
            return (Criteria) this;
        }

        public Criteria andThirdTempletNameLessThanOrEqualTo(String value) {
            addCriterion("third_templet_name <=", value, "thirdTempletName");
            return (Criteria) this;
        }

        public Criteria andThirdTempletNameLike(String value) {
            addCriterion("third_templet_name like", value, "thirdTempletName");
            return (Criteria) this;
        }

        public Criteria andThirdTempletNameNotLike(String value) {
            addCriterion("third_templet_name not like", value, "thirdTempletName");
            return (Criteria) this;
        }

        public Criteria andThirdTempletNameIn(List<String> values) {
            addCriterion("third_templet_name in", values, "thirdTempletName");
            return (Criteria) this;
        }

        public Criteria andThirdTempletNameNotIn(List<String> values) {
            addCriterion("third_templet_name not in", values, "thirdTempletName");
            return (Criteria) this;
        }

        public Criteria andThirdTempletNameBetween(String value1, String value2) {
            addCriterion("third_templet_name between", value1, value2, "thirdTempletName");
            return (Criteria) this;
        }

        public Criteria andThirdTempletNameNotBetween(String value1, String value2) {
            addCriterion("third_templet_name not between", value1, value2, "thirdTempletName");
            return (Criteria) this;
        }

        public Criteria andStandardTempletIdIsNull() {
            addCriterion("standard_templet_id is null");
            return (Criteria) this;
        }

        public Criteria andStandardTempletIdIsNotNull() {
            addCriterion("standard_templet_id is not null");
            return (Criteria) this;
        }

        public Criteria andStandardTempletIdEqualTo(String value) {
            addCriterion("standard_templet_id =", value, "standardTempletId");
            return (Criteria) this;
        }

        public Criteria andStandardTempletIdNotEqualTo(String value) {
            addCriterion("standard_templet_id <>", value, "standardTempletId");
            return (Criteria) this;
        }

        public Criteria andStandardTempletIdGreaterThan(String value) {
            addCriterion("standard_templet_id >", value, "standardTempletId");
            return (Criteria) this;
        }

        public Criteria andStandardTempletIdGreaterThanOrEqualTo(String value) {
            addCriterion("standard_templet_id >=", value, "standardTempletId");
            return (Criteria) this;
        }

        public Criteria andStandardTempletIdLessThan(String value) {
            addCriterion("standard_templet_id <", value, "standardTempletId");
            return (Criteria) this;
        }

        public Criteria andStandardTempletIdLessThanOrEqualTo(String value) {
            addCriterion("standard_templet_id <=", value, "standardTempletId");
            return (Criteria) this;
        }

        public Criteria andStandardTempletIdLike(String value) {
            addCriterion("standard_templet_id like", value, "standardTempletId");
            return (Criteria) this;
        }

        public Criteria andStandardTempletIdNotLike(String value) {
            addCriterion("standard_templet_id not like", value, "standardTempletId");
            return (Criteria) this;
        }

        public Criteria andStandardTempletIdIn(List<String> values) {
            addCriterion("standard_templet_id in", values, "standardTempletId");
            return (Criteria) this;
        }

        public Criteria andStandardTempletIdNotIn(List<String> values) {
            addCriterion("standard_templet_id not in", values, "standardTempletId");
            return (Criteria) this;
        }

        public Criteria andStandardTempletIdBetween(String value1, String value2) {
            addCriterion("standard_templet_id between", value1, value2, "standardTempletId");
            return (Criteria) this;
        }

        public Criteria andStandardTempletIdNotBetween(String value1, String value2) {
            addCriterion("standard_templet_id not between", value1, value2, "standardTempletId");
            return (Criteria) this;
        }

        public Criteria andEnableFlagIsNull() {
            addCriterion("enable_flag is null");
            return (Criteria) this;
        }

        public Criteria andEnableFlagIsNotNull() {
            addCriterion("enable_flag is not null");
            return (Criteria) this;
        }

        public Criteria andEnableFlagEqualTo(String value) {
            addCriterion("enable_flag =", value, "enableFlag");
            return (Criteria) this;
        }

        public Criteria andEnableFlagNotEqualTo(String value) {
            addCriterion("enable_flag <>", value, "enableFlag");
            return (Criteria) this;
        }

        public Criteria andEnableFlagGreaterThan(String value) {
            addCriterion("enable_flag >", value, "enableFlag");
            return (Criteria) this;
        }

        public Criteria andEnableFlagGreaterThanOrEqualTo(String value) {
            addCriterion("enable_flag >=", value, "enableFlag");
            return (Criteria) this;
        }

        public Criteria andEnableFlagLessThan(String value) {
            addCriterion("enable_flag <", value, "enableFlag");
            return (Criteria) this;
        }

        public Criteria andEnableFlagLessThanOrEqualTo(String value) {
            addCriterion("enable_flag <=", value, "enableFlag");
            return (Criteria) this;
        }

        public Criteria andEnableFlagLike(String value) {
            addCriterion("enable_flag like", value, "enableFlag");
            return (Criteria) this;
        }

        public Criteria andEnableFlagNotLike(String value) {
            addCriterion("enable_flag not like", value, "enableFlag");
            return (Criteria) this;
        }

        public Criteria andEnableFlagIn(List<String> values) {
            addCriterion("enable_flag in", values, "enableFlag");
            return (Criteria) this;
        }

        public Criteria andEnableFlagNotIn(List<String> values) {
            addCriterion("enable_flag not in", values, "enableFlag");
            return (Criteria) this;
        }

        public Criteria andEnableFlagBetween(String value1, String value2) {
            addCriterion("enable_flag between", value1, value2, "enableFlag");
            return (Criteria) this;
        }

        public Criteria andEnableFlagNotBetween(String value1, String value2) {
            addCriterion("enable_flag not between", value1, value2, "enableFlag");
            return (Criteria) this;
        }

        public Criteria andOddsOrderIsNull() {
            addCriterion("odds_order is null");
            return (Criteria) this;
        }

        public Criteria andOddsOrderIsNotNull() {
            addCriterion("odds_order is not null");
            return (Criteria) this;
        }

        public Criteria andOddsOrderEqualTo(String value) {
            addCriterion("odds_order =", value, "oddsOrder");
            return (Criteria) this;
        }

        public Criteria andOddsOrderNotEqualTo(String value) {
            addCriterion("odds_order <>", value, "oddsOrder");
            return (Criteria) this;
        }

        public Criteria andOddsOrderGreaterThan(String value) {
            addCriterion("odds_order >", value, "oddsOrder");
            return (Criteria) this;
        }

        public Criteria andOddsOrderGreaterThanOrEqualTo(String value) {
            addCriterion("odds_order >=", value, "oddsOrder");
            return (Criteria) this;
        }

        public Criteria andOddsOrderLessThan(String value) {
            addCriterion("odds_order <", value, "oddsOrder");
            return (Criteria) this;
        }

        public Criteria andOddsOrderLessThanOrEqualTo(String value) {
            addCriterion("odds_order <=", value, "oddsOrder");
            return (Criteria) this;
        }

        public Criteria andOddsOrderLike(String value) {
            addCriterion("odds_order like", value, "oddsOrder");
            return (Criteria) this;
        }

        public Criteria andOddsOrderNotLike(String value) {
            addCriterion("odds_order not like", value, "oddsOrder");
            return (Criteria) this;
        }

        public Criteria andOddsOrderIn(List<String> values) {
            addCriterion("odds_order in", values, "oddsOrder");
            return (Criteria) this;
        }

        public Criteria andOddsOrderNotIn(List<String> values) {
            addCriterion("odds_order not in", values, "oddsOrder");
            return (Criteria) this;
        }

        public Criteria andOddsOrderBetween(String value1, String value2) {
            addCriterion("odds_order between", value1, value2, "oddsOrder");
            return (Criteria) this;
        }

        public Criteria andOddsOrderNotBetween(String value1, String value2) {
            addCriterion("odds_order not between", value1, value2, "oddsOrder");
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