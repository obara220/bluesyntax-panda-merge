package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class ThirdSportMarketExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ThirdSportMarketExample() {
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

        public Criteria andTournamentIdIsNull() {
            addCriterion("tournament_id is null");
            return (Criteria) this;
        }

        public Criteria andTournamentIdIsNotNull() {
            addCriterion("tournament_id is not null");
            return (Criteria) this;
        }

        public Criteria andTournamentIdEqualTo(Long value) {
            addCriterion("tournament_id =", value, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdNotEqualTo(Long value) {
            addCriterion("tournament_id <>", value, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdGreaterThan(Long value) {
            addCriterion("tournament_id >", value, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdGreaterThanOrEqualTo(Long value) {
            addCriterion("tournament_id >=", value, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdLessThan(Long value) {
            addCriterion("tournament_id <", value, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdLessThanOrEqualTo(Long value) {
            addCriterion("tournament_id <=", value, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdIn(List<Long> values) {
            addCriterion("tournament_id in", values, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdNotIn(List<Long> values) {
            addCriterion("tournament_id not in", values, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdBetween(Long value1, Long value2) {
            addCriterion("tournament_id between", value1, value2, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdNotBetween(Long value1, Long value2) {
            addCriterion("tournament_id not between", value1, value2, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andMatchIdIsNull() {
            addCriterion("match_id is null");
            return (Criteria) this;
        }

        public Criteria andMatchIdIsNotNull() {
            addCriterion("match_id is not null");
            return (Criteria) this;
        }

        public Criteria andMatchIdEqualTo(Long value) {
            addCriterion("match_id =", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdNotEqualTo(Long value) {
            addCriterion("match_id <>", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdGreaterThan(Long value) {
            addCriterion("match_id >", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdGreaterThanOrEqualTo(Long value) {
            addCriterion("match_id >=", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdLessThan(Long value) {
            addCriterion("match_id <", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdLessThanOrEqualTo(Long value) {
            addCriterion("match_id <=", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdIn(List<Long> values) {
            addCriterion("match_id in", values, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdNotIn(List<Long> values) {
            addCriterion("match_id not in", values, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdBetween(Long value1, Long value2) {
            addCriterion("match_id between", value1, value2, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdNotBetween(Long value1, Long value2) {
            addCriterion("match_id not between", value1, value2, "matchId");
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

        public Criteria andThirdMarketSourceIdIsNull() {
            addCriterion("third_market_source_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdMarketSourceIdIsNotNull() {
            addCriterion("third_market_source_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdMarketSourceIdEqualTo(String value) {
            addCriterion("third_market_source_id =", value, "thirdMarketSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMarketSourceIdNotEqualTo(String value) {
            addCriterion("third_market_source_id <>", value, "thirdMarketSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMarketSourceIdGreaterThan(String value) {
            addCriterion("third_market_source_id >", value, "thirdMarketSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMarketSourceIdGreaterThanOrEqualTo(String value) {
            addCriterion("third_market_source_id >=", value, "thirdMarketSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMarketSourceIdLessThan(String value) {
            addCriterion("third_market_source_id <", value, "thirdMarketSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMarketSourceIdLessThanOrEqualTo(String value) {
            addCriterion("third_market_source_id <=", value, "thirdMarketSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMarketSourceIdLike(String value) {
            addCriterion("third_market_source_id like", value, "thirdMarketSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMarketSourceIdNotLike(String value) {
            addCriterion("third_market_source_id not like", value, "thirdMarketSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMarketSourceIdIn(List<String> values) {
            addCriterion("third_market_source_id in", values, "thirdMarketSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMarketSourceIdNotIn(List<String> values) {
            addCriterion("third_market_source_id not in", values, "thirdMarketSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMarketSourceIdBetween(String value1, String value2) {
            addCriterion("third_market_source_id between", value1, value2, "thirdMarketSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMarketSourceIdNotBetween(String value1, String value2) {
            addCriterion("third_market_source_id not between", value1, value2, "thirdMarketSourceId");
            return (Criteria) this;
        }

        public Criteria andReferenceIdIsNull() {
            addCriterion("reference_id is null");
            return (Criteria) this;
        }

        public Criteria andReferenceIdIsNotNull() {
            addCriterion("reference_id is not null");
            return (Criteria) this;
        }

        public Criteria andReferenceIdEqualTo(Long value) {
            addCriterion("reference_id =", value, "referenceId");
            return (Criteria) this;
        }

        public Criteria andReferenceIdNotEqualTo(Long value) {
            addCriterion("reference_id <>", value, "referenceId");
            return (Criteria) this;
        }

        public Criteria andReferenceIdGreaterThan(Long value) {
            addCriterion("reference_id >", value, "referenceId");
            return (Criteria) this;
        }

        public Criteria andReferenceIdGreaterThanOrEqualTo(Long value) {
            addCriterion("reference_id >=", value, "referenceId");
            return (Criteria) this;
        }

        public Criteria andReferenceIdLessThan(Long value) {
            addCriterion("reference_id <", value, "referenceId");
            return (Criteria) this;
        }

        public Criteria andReferenceIdLessThanOrEqualTo(Long value) {
            addCriterion("reference_id <=", value, "referenceId");
            return (Criteria) this;
        }

        public Criteria andReferenceIdIn(List<Long> values) {
            addCriterion("reference_id in", values, "referenceId");
            return (Criteria) this;
        }

        public Criteria andReferenceIdNotIn(List<Long> values) {
            addCriterion("reference_id not in", values, "referenceId");
            return (Criteria) this;
        }

        public Criteria andReferenceIdBetween(Long value1, Long value2) {
            addCriterion("reference_id between", value1, value2, "referenceId");
            return (Criteria) this;
        }

        public Criteria andReferenceIdNotBetween(Long value1, Long value2) {
            addCriterion("reference_id not between", value1, value2, "referenceId");
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

        public Criteria andScopeIdIsNull() {
            addCriterion("scope_id is null");
            return (Criteria) this;
        }

        public Criteria andScopeIdIsNotNull() {
            addCriterion("scope_id is not null");
            return (Criteria) this;
        }

        public Criteria andScopeIdEqualTo(String value) {
            addCriterion("scope_id =", value, "scopeId");
            return (Criteria) this;
        }

        public Criteria andScopeIdNotEqualTo(String value) {
            addCriterion("scope_id <>", value, "scopeId");
            return (Criteria) this;
        }

        public Criteria andScopeIdGreaterThan(String value) {
            addCriterion("scope_id >", value, "scopeId");
            return (Criteria) this;
        }

        public Criteria andScopeIdGreaterThanOrEqualTo(String value) {
            addCriterion("scope_id >=", value, "scopeId");
            return (Criteria) this;
        }

        public Criteria andScopeIdLessThan(String value) {
            addCriterion("scope_id <", value, "scopeId");
            return (Criteria) this;
        }

        public Criteria andScopeIdLessThanOrEqualTo(String value) {
            addCriterion("scope_id <=", value, "scopeId");
            return (Criteria) this;
        }

        public Criteria andScopeIdLike(String value) {
            addCriterion("scope_id like", value, "scopeId");
            return (Criteria) this;
        }

        public Criteria andScopeIdNotLike(String value) {
            addCriterion("scope_id not like", value, "scopeId");
            return (Criteria) this;
        }

        public Criteria andScopeIdIn(List<String> values) {
            addCriterion("scope_id in", values, "scopeId");
            return (Criteria) this;
        }

        public Criteria andScopeIdNotIn(List<String> values) {
            addCriterion("scope_id not in", values, "scopeId");
            return (Criteria) this;
        }

        public Criteria andScopeIdBetween(String value1, String value2) {
            addCriterion("scope_id between", value1, value2, "scopeId");
            return (Criteria) this;
        }

        public Criteria andScopeIdNotBetween(String value1, String value2) {
            addCriterion("scope_id not between", value1, value2, "scopeId");
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

        public Criteria andOddsTypeNameIsNull() {
            addCriterion("odds_type_name is null");
            return (Criteria) this;
        }

        public Criteria andOddsTypeNameIsNotNull() {
            addCriterion("odds_type_name is not null");
            return (Criteria) this;
        }

        public Criteria andOddsTypeNameEqualTo(String value) {
            addCriterion("odds_type_name =", value, "oddsTypeName");
            return (Criteria) this;
        }

        public Criteria andOddsTypeNameNotEqualTo(String value) {
            addCriterion("odds_type_name <>", value, "oddsTypeName");
            return (Criteria) this;
        }

        public Criteria andOddsTypeNameGreaterThan(String value) {
            addCriterion("odds_type_name >", value, "oddsTypeName");
            return (Criteria) this;
        }

        public Criteria andOddsTypeNameGreaterThanOrEqualTo(String value) {
            addCriterion("odds_type_name >=", value, "oddsTypeName");
            return (Criteria) this;
        }

        public Criteria andOddsTypeNameLessThan(String value) {
            addCriterion("odds_type_name <", value, "oddsTypeName");
            return (Criteria) this;
        }

        public Criteria andOddsTypeNameLessThanOrEqualTo(String value) {
            addCriterion("odds_type_name <=", value, "oddsTypeName");
            return (Criteria) this;
        }

        public Criteria andOddsTypeNameLike(String value) {
            addCriterion("odds_type_name like", value, "oddsTypeName");
            return (Criteria) this;
        }

        public Criteria andOddsTypeNameNotLike(String value) {
            addCriterion("odds_type_name not like", value, "oddsTypeName");
            return (Criteria) this;
        }

        public Criteria andOddsTypeNameIn(List<String> values) {
            addCriterion("odds_type_name in", values, "oddsTypeName");
            return (Criteria) this;
        }

        public Criteria andOddsTypeNameNotIn(List<String> values) {
            addCriterion("odds_type_name not in", values, "oddsTypeName");
            return (Criteria) this;
        }

        public Criteria andOddsTypeNameBetween(String value1, String value2) {
            addCriterion("odds_type_name between", value1, value2, "oddsTypeName");
            return (Criteria) this;
        }

        public Criteria andOddsTypeNameNotBetween(String value1, String value2) {
            addCriterion("odds_type_name not between", value1, value2, "oddsTypeName");
            return (Criteria) this;
        }

        public Criteria andThirdOddsTypeIsNull() {
            addCriterion("third_odds_type is null");
            return (Criteria) this;
        }

        public Criteria andThirdOddsTypeIsNotNull() {
            addCriterion("third_odds_type is not null");
            return (Criteria) this;
        }

        public Criteria andThirdOddsTypeEqualTo(String value) {
            addCriterion("third_odds_type =", value, "thirdOddsType");
            return (Criteria) this;
        }

        public Criteria andThirdOddsTypeNotEqualTo(String value) {
            addCriterion("third_odds_type <>", value, "thirdOddsType");
            return (Criteria) this;
        }

        public Criteria andThirdOddsTypeGreaterThan(String value) {
            addCriterion("third_odds_type >", value, "thirdOddsType");
            return (Criteria) this;
        }

        public Criteria andThirdOddsTypeGreaterThanOrEqualTo(String value) {
            addCriterion("third_odds_type >=", value, "thirdOddsType");
            return (Criteria) this;
        }

        public Criteria andThirdOddsTypeLessThan(String value) {
            addCriterion("third_odds_type <", value, "thirdOddsType");
            return (Criteria) this;
        }

        public Criteria andThirdOddsTypeLessThanOrEqualTo(String value) {
            addCriterion("third_odds_type <=", value, "thirdOddsType");
            return (Criteria) this;
        }

        public Criteria andThirdOddsTypeLike(String value) {
            addCriterion("third_odds_type like", value, "thirdOddsType");
            return (Criteria) this;
        }

        public Criteria andThirdOddsTypeNotLike(String value) {
            addCriterion("third_odds_type not like", value, "thirdOddsType");
            return (Criteria) this;
        }

        public Criteria andThirdOddsTypeIn(List<String> values) {
            addCriterion("third_odds_type in", values, "thirdOddsType");
            return (Criteria) this;
        }

        public Criteria andThirdOddsTypeNotIn(List<String> values) {
            addCriterion("third_odds_type not in", values, "thirdOddsType");
            return (Criteria) this;
        }

        public Criteria andThirdOddsTypeBetween(String value1, String value2) {
            addCriterion("third_odds_type between", value1, value2, "thirdOddsType");
            return (Criteria) this;
        }

        public Criteria andThirdOddsTypeNotBetween(String value1, String value2) {
            addCriterion("third_odds_type not between", value1, value2, "thirdOddsType");
            return (Criteria) this;
        }

        public Criteria andOddsValueIsNull() {
            addCriterion("odds_value is null");
            return (Criteria) this;
        }

        public Criteria andOddsValueIsNotNull() {
            addCriterion("odds_value is not null");
            return (Criteria) this;
        }

        public Criteria andOddsValueEqualTo(String value) {
            addCriterion("odds_value =", value, "oddsValue");
            return (Criteria) this;
        }

        public Criteria andOddsValueNotEqualTo(String value) {
            addCriterion("odds_value <>", value, "oddsValue");
            return (Criteria) this;
        }

        public Criteria andOddsValueGreaterThan(String value) {
            addCriterion("odds_value >", value, "oddsValue");
            return (Criteria) this;
        }

        public Criteria andOddsValueGreaterThanOrEqualTo(String value) {
            addCriterion("odds_value >=", value, "oddsValue");
            return (Criteria) this;
        }

        public Criteria andOddsValueLessThan(String value) {
            addCriterion("odds_value <", value, "oddsValue");
            return (Criteria) this;
        }

        public Criteria andOddsValueLessThanOrEqualTo(String value) {
            addCriterion("odds_value <=", value, "oddsValue");
            return (Criteria) this;
        }

        public Criteria andOddsValueLike(String value) {
            addCriterion("odds_value like", value, "oddsValue");
            return (Criteria) this;
        }

        public Criteria andOddsValueNotLike(String value) {
            addCriterion("odds_value not like", value, "oddsValue");
            return (Criteria) this;
        }

        public Criteria andOddsValueIn(List<String> values) {
            addCriterion("odds_value in", values, "oddsValue");
            return (Criteria) this;
        }

        public Criteria andOddsValueNotIn(List<String> values) {
            addCriterion("odds_value not in", values, "oddsValue");
            return (Criteria) this;
        }

        public Criteria andOddsValueBetween(String value1, String value2) {
            addCriterion("odds_value between", value1, value2, "oddsValue");
            return (Criteria) this;
        }

        public Criteria andOddsValueNotBetween(String value1, String value2) {
            addCriterion("odds_value not between", value1, value2, "oddsValue");
            return (Criteria) this;
        }

        public Criteria andOrderTypeIsNull() {
            addCriterion("order_type is null");
            return (Criteria) this;
        }

        public Criteria andOrderTypeIsNotNull() {
            addCriterion("order_type is not null");
            return (Criteria) this;
        }

        public Criteria andOrderTypeEqualTo(String value) {
            addCriterion("order_type =", value, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeNotEqualTo(String value) {
            addCriterion("order_type <>", value, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeGreaterThan(String value) {
            addCriterion("order_type >", value, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeGreaterThanOrEqualTo(String value) {
            addCriterion("order_type >=", value, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeLessThan(String value) {
            addCriterion("order_type <", value, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeLessThanOrEqualTo(String value) {
            addCriterion("order_type <=", value, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeLike(String value) {
            addCriterion("order_type like", value, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeNotLike(String value) {
            addCriterion("order_type not like", value, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeIn(List<String> values) {
            addCriterion("order_type in", values, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeNotIn(List<String> values) {
            addCriterion("order_type not in", values, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeBetween(String value1, String value2) {
            addCriterion("order_type between", value1, value2, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeNotBetween(String value1, String value2) {
            addCriterion("order_type not between", value1, value2, "orderType");
            return (Criteria) this;
        }

        public Criteria andOddsNameIsNull() {
            addCriterion("odds_name is null");
            return (Criteria) this;
        }

        public Criteria andOddsNameIsNotNull() {
            addCriterion("odds_name is not null");
            return (Criteria) this;
        }

        public Criteria andOddsNameEqualTo(String value) {
            addCriterion("odds_name =", value, "oddsName");
            return (Criteria) this;
        }

        public Criteria andOddsNameNotEqualTo(String value) {
            addCriterion("odds_name <>", value, "oddsName");
            return (Criteria) this;
        }

        public Criteria andOddsNameGreaterThan(String value) {
            addCriterion("odds_name >", value, "oddsName");
            return (Criteria) this;
        }

        public Criteria andOddsNameGreaterThanOrEqualTo(String value) {
            addCriterion("odds_name >=", value, "oddsName");
            return (Criteria) this;
        }

        public Criteria andOddsNameLessThan(String value) {
            addCriterion("odds_name <", value, "oddsName");
            return (Criteria) this;
        }

        public Criteria andOddsNameLessThanOrEqualTo(String value) {
            addCriterion("odds_name <=", value, "oddsName");
            return (Criteria) this;
        }

        public Criteria andOddsNameLike(String value) {
            addCriterion("odds_name like", value, "oddsName");
            return (Criteria) this;
        }

        public Criteria andOddsNameNotLike(String value) {
            addCriterion("odds_name not like", value, "oddsName");
            return (Criteria) this;
        }

        public Criteria andOddsNameIn(List<String> values) {
            addCriterion("odds_name in", values, "oddsName");
            return (Criteria) this;
        }

        public Criteria andOddsNameNotIn(List<String> values) {
            addCriterion("odds_name not in", values, "oddsName");
            return (Criteria) this;
        }

        public Criteria andOddsNameBetween(String value1, String value2) {
            addCriterion("odds_name between", value1, value2, "oddsName");
            return (Criteria) this;
        }

        public Criteria andOddsNameNotBetween(String value1, String value2) {
            addCriterion("odds_name not between", value1, value2, "oddsName");
            return (Criteria) this;
        }

        public Criteria andOddsMetricIsNull() {
            addCriterion("odds_metric is null");
            return (Criteria) this;
        }

        public Criteria andOddsMetricIsNotNull() {
            addCriterion("odds_metric is not null");
            return (Criteria) this;
        }

        public Criteria andOddsMetricEqualTo(Long value) {
            addCriterion("odds_metric =", value, "oddsMetric");
            return (Criteria) this;
        }

        public Criteria andOddsMetricNotEqualTo(Long value) {
            addCriterion("odds_metric <>", value, "oddsMetric");
            return (Criteria) this;
        }

        public Criteria andOddsMetricGreaterThan(Long value) {
            addCriterion("odds_metric >", value, "oddsMetric");
            return (Criteria) this;
        }

        public Criteria andOddsMetricGreaterThanOrEqualTo(Long value) {
            addCriterion("odds_metric >=", value, "oddsMetric");
            return (Criteria) this;
        }

        public Criteria andOddsMetricLessThan(Long value) {
            addCriterion("odds_metric <", value, "oddsMetric");
            return (Criteria) this;
        }

        public Criteria andOddsMetricLessThanOrEqualTo(Long value) {
            addCriterion("odds_metric <=", value, "oddsMetric");
            return (Criteria) this;
        }

        public Criteria andOddsMetricIn(List<Long> values) {
            addCriterion("odds_metric in", values, "oddsMetric");
            return (Criteria) this;
        }

        public Criteria andOddsMetricNotIn(List<Long> values) {
            addCriterion("odds_metric not in", values, "oddsMetric");
            return (Criteria) this;
        }

        public Criteria andOddsMetricBetween(Long value1, Long value2) {
            addCriterion("odds_metric between", value1, value2, "oddsMetric");
            return (Criteria) this;
        }

        public Criteria andOddsMetricNotBetween(Long value1, Long value2) {
            addCriterion("odds_metric not between", value1, value2, "oddsMetric");
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

        public Criteria andRemarkIsNull() {
            addCriterion("remark is null");
            return (Criteria) this;
        }

        public Criteria andRemarkIsNotNull() {
            addCriterion("remark is not null");
            return (Criteria) this;
        }

        public Criteria andRemarkEqualTo(String value) {
            addCriterion("remark =", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotEqualTo(String value) {
            addCriterion("remark <>", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkGreaterThan(String value) {
            addCriterion("remark >", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkGreaterThanOrEqualTo(String value) {
            addCriterion("remark >=", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLessThan(String value) {
            addCriterion("remark <", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLessThanOrEqualTo(String value) {
            addCriterion("remark <=", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLike(String value) {
            addCriterion("remark like", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotLike(String value) {
            addCriterion("remark not like", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkIn(List<String> values) {
            addCriterion("remark in", values, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotIn(List<String> values) {
            addCriterion("remark not in", values, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkBetween(String value1, String value2) {
            addCriterion("remark between", value1, value2, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotBetween(String value1, String value2) {
            addCriterion("remark not between", value1, value2, "remark");
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

        public Criteria andExtraInfoIsNull() {
            addCriterion("extra_info is null");
            return (Criteria) this;
        }

        public Criteria andExtraInfoIsNotNull() {
            addCriterion("extra_info is not null");
            return (Criteria) this;
        }

        public Criteria andExtraInfoEqualTo(String value) {
            addCriterion("extra_info =", value, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoNotEqualTo(String value) {
            addCriterion("extra_info <>", value, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoGreaterThan(String value) {
            addCriterion("extra_info >", value, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoGreaterThanOrEqualTo(String value) {
            addCriterion("extra_info >=", value, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoLessThan(String value) {
            addCriterion("extra_info <", value, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoLessThanOrEqualTo(String value) {
            addCriterion("extra_info <=", value, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoLike(String value) {
            addCriterion("extra_info like", value, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoNotLike(String value) {
            addCriterion("extra_info not like", value, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoIn(List<String> values) {
            addCriterion("extra_info in", values, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoNotIn(List<String> values) {
            addCriterion("extra_info not in", values, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoBetween(String value1, String value2) {
            addCriterion("extra_info between", value1, value2, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoNotBetween(String value1, String value2) {
            addCriterion("extra_info not between", value1, value2, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andThirdMarketSourceStatusIsNull() {
            addCriterion("third_market_source_status is null");
            return (Criteria) this;
        }

        public Criteria andThirdMarketSourceStatusIsNotNull() {
            addCriterion("third_market_source_status is not null");
            return (Criteria) this;
        }

        public Criteria andThirdMarketSourceStatusEqualTo(Integer value) {
            addCriterion("third_market_source_status =", value, "thirdMarketSourceStatus");
            return (Criteria) this;
        }

        public Criteria andThirdMarketSourceStatusNotEqualTo(Integer value) {
            addCriterion("third_market_source_status <>", value, "thirdMarketSourceStatus");
            return (Criteria) this;
        }

        public Criteria andThirdMarketSourceStatusGreaterThan(Integer value) {
            addCriterion("third_market_source_status >", value, "thirdMarketSourceStatus");
            return (Criteria) this;
        }

        public Criteria andThirdMarketSourceStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("third_market_source_status >=", value, "thirdMarketSourceStatus");
            return (Criteria) this;
        }

        public Criteria andThirdMarketSourceStatusLessThan(Integer value) {
            addCriterion("third_market_source_status <", value, "thirdMarketSourceStatus");
            return (Criteria) this;
        }

        public Criteria andThirdMarketSourceStatusLessThanOrEqualTo(Integer value) {
            addCriterion("third_market_source_status <=", value, "thirdMarketSourceStatus");
            return (Criteria) this;
        }

        public Criteria andThirdMarketSourceStatusIn(List<Integer> values) {
            addCriterion("third_market_source_status in", values, "thirdMarketSourceStatus");
            return (Criteria) this;
        }

        public Criteria andThirdMarketSourceStatusNotIn(List<Integer> values) {
            addCriterion("third_market_source_status not in", values, "thirdMarketSourceStatus");
            return (Criteria) this;
        }

        public Criteria andThirdMarketSourceStatusBetween(Integer value1, Integer value2) {
            addCriterion("third_market_source_status between", value1, value2, "thirdMarketSourceStatus");
            return (Criteria) this;
        }

        public Criteria andThirdMarketSourceStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("third_market_source_status not between", value1, value2, "thirdMarketSourceStatus");
            return (Criteria) this;
        }

        public Criteria andOfferLineIdIsNull() {
            addCriterion("offer_line_id is null");
            return (Criteria) this;
        }

        public Criteria andOfferLineIdIsNotNull() {
            addCriterion("offer_line_id is not null");
            return (Criteria) this;
        }

        public Criteria andOfferLineIdEqualTo(Integer value) {
            addCriterion("offer_line_id =", value, "offerLineId");
            return (Criteria) this;
        }

        public Criteria andOfferLineIdNotEqualTo(Integer value) {
            addCriterion("offer_line_id <>", value, "offerLineId");
            return (Criteria) this;
        }

        public Criteria andOfferLineIdGreaterThan(Integer value) {
            addCriterion("offer_line_id >", value, "offerLineId");
            return (Criteria) this;
        }

        public Criteria andOfferLineIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("offer_line_id >=", value, "offerLineId");
            return (Criteria) this;
        }

        public Criteria andOfferLineIdLessThan(Integer value) {
            addCriterion("offer_line_id <", value, "offerLineId");
            return (Criteria) this;
        }

        public Criteria andOfferLineIdLessThanOrEqualTo(Integer value) {
            addCriterion("offer_line_id <=", value, "offerLineId");
            return (Criteria) this;
        }

        public Criteria andOfferLineIdIn(List<Integer> values) {
            addCriterion("offer_line_id in", values, "offerLineId");
            return (Criteria) this;
        }

        public Criteria andOfferLineIdNotIn(List<Integer> values) {
            addCriterion("offer_line_id not in", values, "offerLineId");
            return (Criteria) this;
        }

        public Criteria andOfferLineIdBetween(Integer value1, Integer value2) {
            addCriterion("offer_line_id between", value1, value2, "offerLineId");
            return (Criteria) this;
        }

        public Criteria andOfferLineIdNotBetween(Integer value1, Integer value2) {
            addCriterion("offer_line_id not between", value1, value2, "offerLineId");
            return (Criteria) this;
        }

        public Criteria andNumberOfWinnersIsNull() {
            addCriterion("number_of_winners is null");
            return (Criteria) this;
        }

        public Criteria andNumberOfWinnersIsNotNull() {
            addCriterion("number_of_winners is not null");
            return (Criteria) this;
        }

        public Criteria andNumberOfWinnersEqualTo(Integer value) {
            addCriterion("number_of_winners =", value, "numberOfWinners");
            return (Criteria) this;
        }

        public Criteria andNumberOfWinnersNotEqualTo(Integer value) {
            addCriterion("number_of_winners <>", value, "numberOfWinners");
            return (Criteria) this;
        }

        public Criteria andNumberOfWinnersGreaterThan(Integer value) {
            addCriterion("number_of_winners >", value, "numberOfWinners");
            return (Criteria) this;
        }

        public Criteria andNumberOfWinnersGreaterThanOrEqualTo(Integer value) {
            addCriterion("number_of_winners >=", value, "numberOfWinners");
            return (Criteria) this;
        }

        public Criteria andNumberOfWinnersLessThan(Integer value) {
            addCriterion("number_of_winners <", value, "numberOfWinners");
            return (Criteria) this;
        }

        public Criteria andNumberOfWinnersLessThanOrEqualTo(Integer value) {
            addCriterion("number_of_winners <=", value, "numberOfWinners");
            return (Criteria) this;
        }

        public Criteria andNumberOfWinnersIn(List<Integer> values) {
            addCriterion("number_of_winners in", values, "numberOfWinners");
            return (Criteria) this;
        }

        public Criteria andNumberOfWinnersNotIn(List<Integer> values) {
            addCriterion("number_of_winners not in", values, "numberOfWinners");
            return (Criteria) this;
        }

        public Criteria andNumberOfWinnersBetween(Integer value1, Integer value2) {
            addCriterion("number_of_winners between", value1, value2, "numberOfWinners");
            return (Criteria) this;
        }

        public Criteria andNumberOfWinnersNotBetween(Integer value1, Integer value2) {
            addCriterion("number_of_winners not between", value1, value2, "numberOfWinners");
            return (Criteria) this;
        }

        public Criteria andInternalDataSourceCodeIsNull() {
            addCriterion("internal_data_source_code is null");
            return (Criteria) this;
        }

        public Criteria andInternalDataSourceCodeIsNotNull() {
            addCriterion("internal_data_source_code is not null");
            return (Criteria) this;
        }

        public Criteria andInternalDataSourceCodeEqualTo(String value) {
            addCriterion("internal_data_source_code =", value, "internalDataSourceCode");
            return (Criteria) this;
        }

        public Criteria andInternalDataSourceCodeNotEqualTo(String value) {
            addCriterion("internal_data_source_code <>", value, "internalDataSourceCode");
            return (Criteria) this;
        }

        public Criteria andInternalDataSourceCodeGreaterThan(String value) {
            addCriterion("internal_data_source_code >", value, "internalDataSourceCode");
            return (Criteria) this;
        }

        public Criteria andInternalDataSourceCodeGreaterThanOrEqualTo(String value) {
            addCriterion("internal_data_source_code >=", value, "internalDataSourceCode");
            return (Criteria) this;
        }

        public Criteria andInternalDataSourceCodeLessThan(String value) {
            addCriterion("internal_data_source_code <", value, "internalDataSourceCode");
            return (Criteria) this;
        }

        public Criteria andInternalDataSourceCodeLessThanOrEqualTo(String value) {
            addCriterion("internal_data_source_code <=", value, "internalDataSourceCode");
            return (Criteria) this;
        }

        public Criteria andInternalDataSourceCodeLike(String value) {
            addCriterion("internal_data_source_code like", value, "internalDataSourceCode");
            return (Criteria) this;
        }

        public Criteria andInternalDataSourceCodeNotLike(String value) {
            addCriterion("internal_data_source_code not like", value, "internalDataSourceCode");
            return (Criteria) this;
        }

        public Criteria andInternalDataSourceCodeIn(List<String> values) {
            addCriterion("internal_data_source_code in", values, "internalDataSourceCode");
            return (Criteria) this;
        }

        public Criteria andInternalDataSourceCodeNotIn(List<String> values) {
            addCriterion("internal_data_source_code not in", values, "internalDataSourceCode");
            return (Criteria) this;
        }

        public Criteria andInternalDataSourceCodeBetween(String value1, String value2) {
            addCriterion("internal_data_source_code between", value1, value2, "internalDataSourceCode");
            return (Criteria) this;
        }

        public Criteria andInternalDataSourceCodeNotBetween(String value1, String value2) {
            addCriterion("internal_data_source_code not between", value1, value2, "internalDataSourceCode");
            return (Criteria) this;
        }

        public Criteria andEventTypeIsNull() {
            addCriterion("event_type is null");
            return (Criteria) this;
        }

        public Criteria andEventTypeIsNotNull() {
            addCriterion("event_type is not null");
            return (Criteria) this;
        }

        public Criteria andEventTypeEqualTo(Integer value) {
            addCriterion("event_type =", value, "eventType");
            return (Criteria) this;
        }

        public Criteria andEventTypeNotEqualTo(Integer value) {
            addCriterion("event_type <>", value, "eventType");
            return (Criteria) this;
        }

        public Criteria andEventTypeGreaterThan(Integer value) {
            addCriterion("event_type >", value, "eventType");
            return (Criteria) this;
        }

        public Criteria andEventTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("event_type >=", value, "eventType");
            return (Criteria) this;
        }

        public Criteria andEventTypeLessThan(Integer value) {
            addCriterion("event_type <", value, "eventType");
            return (Criteria) this;
        }

        public Criteria andEventTypeLessThanOrEqualTo(Integer value) {
            addCriterion("event_type <=", value, "eventType");
            return (Criteria) this;
        }

        public Criteria andEventTypeIn(List<Integer> values) {
            addCriterion("event_type in", values, "eventType");
            return (Criteria) this;
        }

        public Criteria andEventTypeNotIn(List<Integer> values) {
            addCriterion("event_type not in", values, "eventType");
            return (Criteria) this;
        }

        public Criteria andEventTypeBetween(Integer value1, Integer value2) {
            addCriterion("event_type between", value1, value2, "eventType");
            return (Criteria) this;
        }

        public Criteria andEventTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("event_type not between", value1, value2, "eventType");
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