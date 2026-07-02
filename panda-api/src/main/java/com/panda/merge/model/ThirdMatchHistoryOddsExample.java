package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class ThirdMatchHistoryOddsExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ThirdMatchHistoryOddsExample() {
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

        public Criteria andIdEqualTo(String value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(String value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(String value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(String value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(String value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(String value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLike(String value) {
            addCriterion("id like", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotLike(String value) {
            addCriterion("id not like", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<String> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<String> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(String value1, String value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(String value1, String value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdIsNull() {
            addCriterion("third_match_source_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdIsNotNull() {
            addCriterion("third_match_source_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdEqualTo(String value) {
            addCriterion("third_match_source_id =", value, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdNotEqualTo(String value) {
            addCriterion("third_match_source_id <>", value, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdGreaterThan(String value) {
            addCriterion("third_match_source_id >", value, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdGreaterThanOrEqualTo(String value) {
            addCriterion("third_match_source_id >=", value, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdLessThan(String value) {
            addCriterion("third_match_source_id <", value, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdLessThanOrEqualTo(String value) {
            addCriterion("third_match_source_id <=", value, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdLike(String value) {
            addCriterion("third_match_source_id like", value, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdNotLike(String value) {
            addCriterion("third_match_source_id not like", value, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdIn(List<String> values) {
            addCriterion("third_match_source_id in", values, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdNotIn(List<String> values) {
            addCriterion("third_match_source_id not in", values, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdBetween(String value1, String value2) {
            addCriterion("third_match_source_id between", value1, value2, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdNotBetween(String value1, String value2) {
            addCriterion("third_match_source_id not between", value1, value2, "thirdMatchSourceId");
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

        public Criteria andSportIdEqualTo(Long value) {
            addCriterion("sport_id =", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdNotEqualTo(Long value) {
            addCriterion("sport_id <>", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdGreaterThan(Long value) {
            addCriterion("sport_id >", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdGreaterThanOrEqualTo(Long value) {
            addCriterion("sport_id >=", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdLessThan(Long value) {
            addCriterion("sport_id <", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdLessThanOrEqualTo(Long value) {
            addCriterion("sport_id <=", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdIn(List<Long> values) {
            addCriterion("sport_id in", values, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdNotIn(List<Long> values) {
            addCriterion("sport_id not in", values, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdBetween(Long value1, Long value2) {
            addCriterion("sport_id between", value1, value2, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdNotBetween(Long value1, Long value2) {
            addCriterion("sport_id not between", value1, value2, "sportId");
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

        public Criteria andBookIdIsNull() {
            addCriterion("book_id is null");
            return (Criteria) this;
        }

        public Criteria andBookIdIsNotNull() {
            addCriterion("book_id is not null");
            return (Criteria) this;
        }

        public Criteria andBookIdEqualTo(Integer value) {
            addCriterion("book_id =", value, "bookId");
            return (Criteria) this;
        }

        public Criteria andBookIdNotEqualTo(Integer value) {
            addCriterion("book_id <>", value, "bookId");
            return (Criteria) this;
        }

        public Criteria andBookIdGreaterThan(Integer value) {
            addCriterion("book_id >", value, "bookId");
            return (Criteria) this;
        }

        public Criteria andBookIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("book_id >=", value, "bookId");
            return (Criteria) this;
        }

        public Criteria andBookIdLessThan(Integer value) {
            addCriterion("book_id <", value, "bookId");
            return (Criteria) this;
        }

        public Criteria andBookIdLessThanOrEqualTo(Integer value) {
            addCriterion("book_id <=", value, "bookId");
            return (Criteria) this;
        }

        public Criteria andBookIdIn(List<Integer> values) {
            addCriterion("book_id in", values, "bookId");
            return (Criteria) this;
        }

        public Criteria andBookIdNotIn(List<Integer> values) {
            addCriterion("book_id not in", values, "bookId");
            return (Criteria) this;
        }

        public Criteria andBookIdBetween(Integer value1, Integer value2) {
            addCriterion("book_id between", value1, value2, "bookId");
            return (Criteria) this;
        }

        public Criteria andBookIdNotBetween(Integer value1, Integer value2) {
            addCriterion("book_id not between", value1, value2, "bookId");
            return (Criteria) this;
        }

        public Criteria andBookCnNameIsNull() {
            addCriterion("book_cn_name is null");
            return (Criteria) this;
        }

        public Criteria andBookCnNameIsNotNull() {
            addCriterion("book_cn_name is not null");
            return (Criteria) this;
        }

        public Criteria andBookCnNameEqualTo(String value) {
            addCriterion("book_cn_name =", value, "bookCnName");
            return (Criteria) this;
        }

        public Criteria andBookCnNameNotEqualTo(String value) {
            addCriterion("book_cn_name <>", value, "bookCnName");
            return (Criteria) this;
        }

        public Criteria andBookCnNameGreaterThan(String value) {
            addCriterion("book_cn_name >", value, "bookCnName");
            return (Criteria) this;
        }

        public Criteria andBookCnNameGreaterThanOrEqualTo(String value) {
            addCriterion("book_cn_name >=", value, "bookCnName");
            return (Criteria) this;
        }

        public Criteria andBookCnNameLessThan(String value) {
            addCriterion("book_cn_name <", value, "bookCnName");
            return (Criteria) this;
        }

        public Criteria andBookCnNameLessThanOrEqualTo(String value) {
            addCriterion("book_cn_name <=", value, "bookCnName");
            return (Criteria) this;
        }

        public Criteria andBookCnNameLike(String value) {
            addCriterion("book_cn_name like", value, "bookCnName");
            return (Criteria) this;
        }

        public Criteria andBookCnNameNotLike(String value) {
            addCriterion("book_cn_name not like", value, "bookCnName");
            return (Criteria) this;
        }

        public Criteria andBookCnNameIn(List<String> values) {
            addCriterion("book_cn_name in", values, "bookCnName");
            return (Criteria) this;
        }

        public Criteria andBookCnNameNotIn(List<String> values) {
            addCriterion("book_cn_name not in", values, "bookCnName");
            return (Criteria) this;
        }

        public Criteria andBookCnNameBetween(String value1, String value2) {
            addCriterion("book_cn_name between", value1, value2, "bookCnName");
            return (Criteria) this;
        }

        public Criteria andBookCnNameNotBetween(String value1, String value2) {
            addCriterion("book_cn_name not between", value1, value2, "bookCnName");
            return (Criteria) this;
        }

        public Criteria andBookEnNameIsNull() {
            addCriterion("book_en_name is null");
            return (Criteria) this;
        }

        public Criteria andBookEnNameIsNotNull() {
            addCriterion("book_en_name is not null");
            return (Criteria) this;
        }

        public Criteria andBookEnNameEqualTo(String value) {
            addCriterion("book_en_name =", value, "bookEnName");
            return (Criteria) this;
        }

        public Criteria andBookEnNameNotEqualTo(String value) {
            addCriterion("book_en_name <>", value, "bookEnName");
            return (Criteria) this;
        }

        public Criteria andBookEnNameGreaterThan(String value) {
            addCriterion("book_en_name >", value, "bookEnName");
            return (Criteria) this;
        }

        public Criteria andBookEnNameGreaterThanOrEqualTo(String value) {
            addCriterion("book_en_name >=", value, "bookEnName");
            return (Criteria) this;
        }

        public Criteria andBookEnNameLessThan(String value) {
            addCriterion("book_en_name <", value, "bookEnName");
            return (Criteria) this;
        }

        public Criteria andBookEnNameLessThanOrEqualTo(String value) {
            addCriterion("book_en_name <=", value, "bookEnName");
            return (Criteria) this;
        }

        public Criteria andBookEnNameLike(String value) {
            addCriterion("book_en_name like", value, "bookEnName");
            return (Criteria) this;
        }

        public Criteria andBookEnNameNotLike(String value) {
            addCriterion("book_en_name not like", value, "bookEnName");
            return (Criteria) this;
        }

        public Criteria andBookEnNameIn(List<String> values) {
            addCriterion("book_en_name in", values, "bookEnName");
            return (Criteria) this;
        }

        public Criteria andBookEnNameNotIn(List<String> values) {
            addCriterion("book_en_name not in", values, "bookEnName");
            return (Criteria) this;
        }

        public Criteria andBookEnNameBetween(String value1, String value2) {
            addCriterion("book_en_name between", value1, value2, "bookEnName");
            return (Criteria) this;
        }

        public Criteria andBookEnNameNotBetween(String value1, String value2) {
            addCriterion("book_en_name not between", value1, value2, "bookEnName");
            return (Criteria) this;
        }

        public Criteria andTypeIdIsNull() {
            addCriterion("type_id is null");
            return (Criteria) this;
        }

        public Criteria andTypeIdIsNotNull() {
            addCriterion("type_id is not null");
            return (Criteria) this;
        }

        public Criteria andTypeIdEqualTo(Integer value) {
            addCriterion("type_id =", value, "typeId");
            return (Criteria) this;
        }

        public Criteria andTypeIdNotEqualTo(Integer value) {
            addCriterion("type_id <>", value, "typeId");
            return (Criteria) this;
        }

        public Criteria andTypeIdGreaterThan(Integer value) {
            addCriterion("type_id >", value, "typeId");
            return (Criteria) this;
        }

        public Criteria andTypeIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("type_id >=", value, "typeId");
            return (Criteria) this;
        }

        public Criteria andTypeIdLessThan(Integer value) {
            addCriterion("type_id <", value, "typeId");
            return (Criteria) this;
        }

        public Criteria andTypeIdLessThanOrEqualTo(Integer value) {
            addCriterion("type_id <=", value, "typeId");
            return (Criteria) this;
        }

        public Criteria andTypeIdIn(List<Integer> values) {
            addCriterion("type_id in", values, "typeId");
            return (Criteria) this;
        }

        public Criteria andTypeIdNotIn(List<Integer> values) {
            addCriterion("type_id not in", values, "typeId");
            return (Criteria) this;
        }

        public Criteria andTypeIdBetween(Integer value1, Integer value2) {
            addCriterion("type_id between", value1, value2, "typeId");
            return (Criteria) this;
        }

        public Criteria andTypeIdNotBetween(Integer value1, Integer value2) {
            addCriterion("type_id not between", value1, value2, "typeId");
            return (Criteria) this;
        }

        public Criteria andTypeNameIsNull() {
            addCriterion("type_name is null");
            return (Criteria) this;
        }

        public Criteria andTypeNameIsNotNull() {
            addCriterion("type_name is not null");
            return (Criteria) this;
        }

        public Criteria andTypeNameEqualTo(String value) {
            addCriterion("type_name =", value, "typeName");
            return (Criteria) this;
        }

        public Criteria andTypeNameNotEqualTo(String value) {
            addCriterion("type_name <>", value, "typeName");
            return (Criteria) this;
        }

        public Criteria andTypeNameGreaterThan(String value) {
            addCriterion("type_name >", value, "typeName");
            return (Criteria) this;
        }

        public Criteria andTypeNameGreaterThanOrEqualTo(String value) {
            addCriterion("type_name >=", value, "typeName");
            return (Criteria) this;
        }

        public Criteria andTypeNameLessThan(String value) {
            addCriterion("type_name <", value, "typeName");
            return (Criteria) this;
        }

        public Criteria andTypeNameLessThanOrEqualTo(String value) {
            addCriterion("type_name <=", value, "typeName");
            return (Criteria) this;
        }

        public Criteria andTypeNameLike(String value) {
            addCriterion("type_name like", value, "typeName");
            return (Criteria) this;
        }

        public Criteria andTypeNameNotLike(String value) {
            addCriterion("type_name not like", value, "typeName");
            return (Criteria) this;
        }

        public Criteria andTypeNameIn(List<String> values) {
            addCriterion("type_name in", values, "typeName");
            return (Criteria) this;
        }

        public Criteria andTypeNameNotIn(List<String> values) {
            addCriterion("type_name not in", values, "typeName");
            return (Criteria) this;
        }

        public Criteria andTypeNameBetween(String value1, String value2) {
            addCriterion("type_name between", value1, value2, "typeName");
            return (Criteria) this;
        }

        public Criteria andTypeNameNotBetween(String value1, String value2) {
            addCriterion("type_name not between", value1, value2, "typeName");
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

        public Criteria andValue0IsNull() {
            addCriterion("value0 is null");
            return (Criteria) this;
        }

        public Criteria andValue0IsNotNull() {
            addCriterion("value0 is not null");
            return (Criteria) this;
        }

        public Criteria andValue0EqualTo(String value) {
            addCriterion("value0 =", value, "value0");
            return (Criteria) this;
        }

        public Criteria andValue0NotEqualTo(String value) {
            addCriterion("value0 <>", value, "value0");
            return (Criteria) this;
        }

        public Criteria andValue0GreaterThan(String value) {
            addCriterion("value0 >", value, "value0");
            return (Criteria) this;
        }

        public Criteria andValue0GreaterThanOrEqualTo(String value) {
            addCriterion("value0 >=", value, "value0");
            return (Criteria) this;
        }

        public Criteria andValue0LessThan(String value) {
            addCriterion("value0 <", value, "value0");
            return (Criteria) this;
        }

        public Criteria andValue0LessThanOrEqualTo(String value) {
            addCriterion("value0 <=", value, "value0");
            return (Criteria) this;
        }

        public Criteria andValue0Like(String value) {
            addCriterion("value0 like", value, "value0");
            return (Criteria) this;
        }

        public Criteria andValue0NotLike(String value) {
            addCriterion("value0 not like", value, "value0");
            return (Criteria) this;
        }

        public Criteria andValue0In(List<String> values) {
            addCriterion("value0 in", values, "value0");
            return (Criteria) this;
        }

        public Criteria andValue0NotIn(List<String> values) {
            addCriterion("value0 not in", values, "value0");
            return (Criteria) this;
        }

        public Criteria andValue0Between(String value1, String value2) {
            addCriterion("value0 between", value1, value2, "value0");
            return (Criteria) this;
        }

        public Criteria andValue0NotBetween(String value1, String value2) {
            addCriterion("value0 not between", value1, value2, "value0");
            return (Criteria) this;
        }

        public Criteria andValueIsNull() {
            addCriterion("value is null");
            return (Criteria) this;
        }

        public Criteria andValueIsNotNull() {
            addCriterion("value is not null");
            return (Criteria) this;
        }

        public Criteria andValueEqualTo(String value) {
            addCriterion("value =", value, "value");
            return (Criteria) this;
        }

        public Criteria andValueNotEqualTo(String value) {
            addCriterion("value <>", value, "value");
            return (Criteria) this;
        }

        public Criteria andValueGreaterThan(String value) {
            addCriterion("value >", value, "value");
            return (Criteria) this;
        }

        public Criteria andValueGreaterThanOrEqualTo(String value) {
            addCriterion("value >=", value, "value");
            return (Criteria) this;
        }

        public Criteria andValueLessThan(String value) {
            addCriterion("value <", value, "value");
            return (Criteria) this;
        }

        public Criteria andValueLessThanOrEqualTo(String value) {
            addCriterion("value <=", value, "value");
            return (Criteria) this;
        }

        public Criteria andValueLike(String value) {
            addCriterion("value like", value, "value");
            return (Criteria) this;
        }

        public Criteria andValueNotLike(String value) {
            addCriterion("value not like", value, "value");
            return (Criteria) this;
        }

        public Criteria andValueIn(List<String> values) {
            addCriterion("value in", values, "value");
            return (Criteria) this;
        }

        public Criteria andValueNotIn(List<String> values) {
            addCriterion("value not in", values, "value");
            return (Criteria) this;
        }

        public Criteria andValueBetween(String value1, String value2) {
            addCriterion("value between", value1, value2, "value");
            return (Criteria) this;
        }

        public Criteria andValueNotBetween(String value1, String value2) {
            addCriterion("value not between", value1, value2, "value");
            return (Criteria) this;
        }

        public Criteria andOddsJsonIsNull() {
            addCriterion("odds_json is null");
            return (Criteria) this;
        }

        public Criteria andOddsJsonIsNotNull() {
            addCriterion("odds_json is not null");
            return (Criteria) this;
        }

        public Criteria andOddsJsonEqualTo(String value) {
            addCriterion("odds_json =", value, "oddsJson");
            return (Criteria) this;
        }

        public Criteria andOddsJsonNotEqualTo(String value) {
            addCriterion("odds_json <>", value, "oddsJson");
            return (Criteria) this;
        }

        public Criteria andOddsJsonGreaterThan(String value) {
            addCriterion("odds_json >", value, "oddsJson");
            return (Criteria) this;
        }

        public Criteria andOddsJsonGreaterThanOrEqualTo(String value) {
            addCriterion("odds_json >=", value, "oddsJson");
            return (Criteria) this;
        }

        public Criteria andOddsJsonLessThan(String value) {
            addCriterion("odds_json <", value, "oddsJson");
            return (Criteria) this;
        }

        public Criteria andOddsJsonLessThanOrEqualTo(String value) {
            addCriterion("odds_json <=", value, "oddsJson");
            return (Criteria) this;
        }

        public Criteria andOddsJsonLike(String value) {
            addCriterion("odds_json like", value, "oddsJson");
            return (Criteria) this;
        }

        public Criteria andOddsJsonNotLike(String value) {
            addCriterion("odds_json not like", value, "oddsJson");
            return (Criteria) this;
        }

        public Criteria andOddsJsonIn(List<String> values) {
            addCriterion("odds_json in", values, "oddsJson");
            return (Criteria) this;
        }

        public Criteria andOddsJsonNotIn(List<String> values) {
            addCriterion("odds_json not in", values, "oddsJson");
            return (Criteria) this;
        }

        public Criteria andOddsJsonBetween(String value1, String value2) {
            addCriterion("odds_json between", value1, value2, "oddsJson");
            return (Criteria) this;
        }

        public Criteria andOddsJsonNotBetween(String value1, String value2) {
            addCriterion("odds_json not between", value1, value2, "oddsJson");
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