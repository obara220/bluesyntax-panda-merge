package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class MatchSettleSpMarketExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MatchSettleSpMarketExample() {
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

        public Criteria andChildMarketCategoryIdIsNull() {
            addCriterion("child_market_category_id is null");
            return (Criteria) this;
        }

        public Criteria andChildMarketCategoryIdIsNotNull() {
            addCriterion("child_market_category_id is not null");
            return (Criteria) this;
        }

        public Criteria andChildMarketCategoryIdEqualTo(Long value) {
            addCriterion("child_market_category_id =", value, "childMarketCategoryId");
            return (Criteria) this;
        }

        public Criteria andChildMarketCategoryIdNotEqualTo(Long value) {
            addCriterion("child_market_category_id <>", value, "childMarketCategoryId");
            return (Criteria) this;
        }

        public Criteria andChildMarketCategoryIdGreaterThan(Long value) {
            addCriterion("child_market_category_id >", value, "childMarketCategoryId");
            return (Criteria) this;
        }

        public Criteria andChildMarketCategoryIdGreaterThanOrEqualTo(Long value) {
            addCriterion("child_market_category_id >=", value, "childMarketCategoryId");
            return (Criteria) this;
        }

        public Criteria andChildMarketCategoryIdLessThan(Long value) {
            addCriterion("child_market_category_id <", value, "childMarketCategoryId");
            return (Criteria) this;
        }

        public Criteria andChildMarketCategoryIdLessThanOrEqualTo(Long value) {
            addCriterion("child_market_category_id <=", value, "childMarketCategoryId");
            return (Criteria) this;
        }

        public Criteria andChildMarketCategoryIdIn(List<Long> values) {
            addCriterion("child_market_category_id in", values, "childMarketCategoryId");
            return (Criteria) this;
        }

        public Criteria andChildMarketCategoryIdNotIn(List<Long> values) {
            addCriterion("child_market_category_id not in", values, "childMarketCategoryId");
            return (Criteria) this;
        }

        public Criteria andChildMarketCategoryIdBetween(Long value1, Long value2) {
            addCriterion("child_market_category_id between", value1, value2, "childMarketCategoryId");
            return (Criteria) this;
        }

        public Criteria andChildMarketCategoryIdNotBetween(Long value1, Long value2) {
            addCriterion("child_market_category_id not between", value1, value2, "childMarketCategoryId");
            return (Criteria) this;
        }

        public Criteria andCategoryNameEnIsNull() {
            addCriterion("category_name_en is null");
            return (Criteria) this;
        }

        public Criteria andCategoryNameEnIsNotNull() {
            addCriterion("category_name_en is not null");
            return (Criteria) this;
        }

        public Criteria andCategoryNameEnEqualTo(String value) {
            addCriterion("category_name_en =", value, "categoryNameEn");
            return (Criteria) this;
        }

        public Criteria andCategoryNameEnNotEqualTo(String value) {
            addCriterion("category_name_en <>", value, "categoryNameEn");
            return (Criteria) this;
        }

        public Criteria andCategoryNameEnGreaterThan(String value) {
            addCriterion("category_name_en >", value, "categoryNameEn");
            return (Criteria) this;
        }

        public Criteria andCategoryNameEnGreaterThanOrEqualTo(String value) {
            addCriterion("category_name_en >=", value, "categoryNameEn");
            return (Criteria) this;
        }

        public Criteria andCategoryNameEnLessThan(String value) {
            addCriterion("category_name_en <", value, "categoryNameEn");
            return (Criteria) this;
        }

        public Criteria andCategoryNameEnLessThanOrEqualTo(String value) {
            addCriterion("category_name_en <=", value, "categoryNameEn");
            return (Criteria) this;
        }

        public Criteria andCategoryNameEnLike(String value) {
            addCriterion("category_name_en like", value, "categoryNameEn");
            return (Criteria) this;
        }

        public Criteria andCategoryNameEnNotLike(String value) {
            addCriterion("category_name_en not like", value, "categoryNameEn");
            return (Criteria) this;
        }

        public Criteria andCategoryNameEnIn(List<String> values) {
            addCriterion("category_name_en in", values, "categoryNameEn");
            return (Criteria) this;
        }

        public Criteria andCategoryNameEnNotIn(List<String> values) {
            addCriterion("category_name_en not in", values, "categoryNameEn");
            return (Criteria) this;
        }

        public Criteria andCategoryNameEnBetween(String value1, String value2) {
            addCriterion("category_name_en between", value1, value2, "categoryNameEn");
            return (Criteria) this;
        }

        public Criteria andCategoryNameEnNotBetween(String value1, String value2) {
            addCriterion("category_name_en not between", value1, value2, "categoryNameEn");
            return (Criteria) this;
        }

        public Criteria andCategoryNameCnIsNull() {
            addCriterion("category_name_cn is null");
            return (Criteria) this;
        }

        public Criteria andCategoryNameCnIsNotNull() {
            addCriterion("category_name_cn is not null");
            return (Criteria) this;
        }

        public Criteria andCategoryNameCnEqualTo(String value) {
            addCriterion("category_name_cn =", value, "categoryNameCn");
            return (Criteria) this;
        }

        public Criteria andCategoryNameCnNotEqualTo(String value) {
            addCriterion("category_name_cn <>", value, "categoryNameCn");
            return (Criteria) this;
        }

        public Criteria andCategoryNameCnGreaterThan(String value) {
            addCriterion("category_name_cn >", value, "categoryNameCn");
            return (Criteria) this;
        }

        public Criteria andCategoryNameCnGreaterThanOrEqualTo(String value) {
            addCriterion("category_name_cn >=", value, "categoryNameCn");
            return (Criteria) this;
        }

        public Criteria andCategoryNameCnLessThan(String value) {
            addCriterion("category_name_cn <", value, "categoryNameCn");
            return (Criteria) this;
        }

        public Criteria andCategoryNameCnLessThanOrEqualTo(String value) {
            addCriterion("category_name_cn <=", value, "categoryNameCn");
            return (Criteria) this;
        }

        public Criteria andCategoryNameCnLike(String value) {
            addCriterion("category_name_cn like", value, "categoryNameCn");
            return (Criteria) this;
        }

        public Criteria andCategoryNameCnNotLike(String value) {
            addCriterion("category_name_cn not like", value, "categoryNameCn");
            return (Criteria) this;
        }

        public Criteria andCategoryNameCnIn(List<String> values) {
            addCriterion("category_name_cn in", values, "categoryNameCn");
            return (Criteria) this;
        }

        public Criteria andCategoryNameCnNotIn(List<String> values) {
            addCriterion("category_name_cn not in", values, "categoryNameCn");
            return (Criteria) this;
        }

        public Criteria andCategoryNameCnBetween(String value1, String value2) {
            addCriterion("category_name_cn between", value1, value2, "categoryNameCn");
            return (Criteria) this;
        }

        public Criteria andCategoryNameCnNotBetween(String value1, String value2) {
            addCriterion("category_name_cn not between", value1, value2, "categoryNameCn");
            return (Criteria) this;
        }

        public Criteria andCheckTypeIsNull() {
            addCriterion("check_type is null");
            return (Criteria) this;
        }

        public Criteria andCheckTypeIsNotNull() {
            addCriterion("check_type is not null");
            return (Criteria) this;
        }

        public Criteria andCheckTypeEqualTo(Integer value) {
            addCriterion("check_type =", value, "checkType");
            return (Criteria) this;
        }

        public Criteria andCheckTypeNotEqualTo(Integer value) {
            addCriterion("check_type <>", value, "checkType");
            return (Criteria) this;
        }

        public Criteria andCheckTypeGreaterThan(Integer value) {
            addCriterion("check_type >", value, "checkType");
            return (Criteria) this;
        }

        public Criteria andCheckTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("check_type >=", value, "checkType");
            return (Criteria) this;
        }

        public Criteria andCheckTypeLessThan(Integer value) {
            addCriterion("check_type <", value, "checkType");
            return (Criteria) this;
        }

        public Criteria andCheckTypeLessThanOrEqualTo(Integer value) {
            addCriterion("check_type <=", value, "checkType");
            return (Criteria) this;
        }

        public Criteria andCheckTypeIn(List<Integer> values) {
            addCriterion("check_type in", values, "checkType");
            return (Criteria) this;
        }

        public Criteria andCheckTypeNotIn(List<Integer> values) {
            addCriterion("check_type not in", values, "checkType");
            return (Criteria) this;
        }

        public Criteria andCheckTypeBetween(Integer value1, Integer value2) {
            addCriterion("check_type between", value1, value2, "checkType");
            return (Criteria) this;
        }

        public Criteria andCheckTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("check_type not between", value1, value2, "checkType");
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