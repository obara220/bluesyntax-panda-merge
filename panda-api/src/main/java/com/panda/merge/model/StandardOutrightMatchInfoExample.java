package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class StandardOutrightMatchInfoExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public StandardOutrightMatchInfoExample() {
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

        public Criteria andRegionIdIsNull() {
            addCriterion("region_id is null");
            return (Criteria) this;
        }

        public Criteria andRegionIdIsNotNull() {
            addCriterion("region_id is not null");
            return (Criteria) this;
        }

        public Criteria andRegionIdEqualTo(Long value) {
            addCriterion("region_id =", value, "regionId");
            return (Criteria) this;
        }

        public Criteria andRegionIdNotEqualTo(Long value) {
            addCriterion("region_id <>", value, "regionId");
            return (Criteria) this;
        }

        public Criteria andRegionIdGreaterThan(Long value) {
            addCriterion("region_id >", value, "regionId");
            return (Criteria) this;
        }

        public Criteria andRegionIdGreaterThanOrEqualTo(Long value) {
            addCriterion("region_id >=", value, "regionId");
            return (Criteria) this;
        }

        public Criteria andRegionIdLessThan(Long value) {
            addCriterion("region_id <", value, "regionId");
            return (Criteria) this;
        }

        public Criteria andRegionIdLessThanOrEqualTo(Long value) {
            addCriterion("region_id <=", value, "regionId");
            return (Criteria) this;
        }

        public Criteria andRegionIdIn(List<Long> values) {
            addCriterion("region_id in", values, "regionId");
            return (Criteria) this;
        }

        public Criteria andRegionIdNotIn(List<Long> values) {
            addCriterion("region_id not in", values, "regionId");
            return (Criteria) this;
        }

        public Criteria andRegionIdBetween(Long value1, Long value2) {
            addCriterion("region_id between", value1, value2, "regionId");
            return (Criteria) this;
        }

        public Criteria andRegionIdNotBetween(Long value1, Long value2) {
            addCriterion("region_id not between", value1, value2, "regionId");
            return (Criteria) this;
        }

        public Criteria andStandardTournamentIdIsNull() {
            addCriterion("standard_tournament_id is null");
            return (Criteria) this;
        }

        public Criteria andStandardTournamentIdIsNotNull() {
            addCriterion("standard_tournament_id is not null");
            return (Criteria) this;
        }

        public Criteria andStandardTournamentIdEqualTo(Long value) {
            addCriterion("standard_tournament_id =", value, "standardTournamentId");
            return (Criteria) this;
        }

        public Criteria andStandardTournamentIdNotEqualTo(Long value) {
            addCriterion("standard_tournament_id <>", value, "standardTournamentId");
            return (Criteria) this;
        }

        public Criteria andStandardTournamentIdGreaterThan(Long value) {
            addCriterion("standard_tournament_id >", value, "standardTournamentId");
            return (Criteria) this;
        }

        public Criteria andStandardTournamentIdGreaterThanOrEqualTo(Long value) {
            addCriterion("standard_tournament_id >=", value, "standardTournamentId");
            return (Criteria) this;
        }

        public Criteria andStandardTournamentIdLessThan(Long value) {
            addCriterion("standard_tournament_id <", value, "standardTournamentId");
            return (Criteria) this;
        }

        public Criteria andStandardTournamentIdLessThanOrEqualTo(Long value) {
            addCriterion("standard_tournament_id <=", value, "standardTournamentId");
            return (Criteria) this;
        }

        public Criteria andStandardTournamentIdIn(List<Long> values) {
            addCriterion("standard_tournament_id in", values, "standardTournamentId");
            return (Criteria) this;
        }

        public Criteria andStandardTournamentIdNotIn(List<Long> values) {
            addCriterion("standard_tournament_id not in", values, "standardTournamentId");
            return (Criteria) this;
        }

        public Criteria andStandardTournamentIdBetween(Long value1, Long value2) {
            addCriterion("standard_tournament_id between", value1, value2, "standardTournamentId");
            return (Criteria) this;
        }

        public Criteria andStandardTournamentIdNotBetween(Long value1, Long value2) {
            addCriterion("standard_tournament_id not between", value1, value2, "standardTournamentId");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightNameEnIsNull() {
            addCriterion("standard_outright_name_en is null");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightNameEnIsNotNull() {
            addCriterion("standard_outright_name_en is not null");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightNameEnEqualTo(String value) {
            addCriterion("standard_outright_name_en =", value, "standardOutrightNameEn");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightNameEnNotEqualTo(String value) {
            addCriterion("standard_outright_name_en <>", value, "standardOutrightNameEn");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightNameEnGreaterThan(String value) {
            addCriterion("standard_outright_name_en >", value, "standardOutrightNameEn");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightNameEnGreaterThanOrEqualTo(String value) {
            addCriterion("standard_outright_name_en >=", value, "standardOutrightNameEn");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightNameEnLessThan(String value) {
            addCriterion("standard_outright_name_en <", value, "standardOutrightNameEn");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightNameEnLessThanOrEqualTo(String value) {
            addCriterion("standard_outright_name_en <=", value, "standardOutrightNameEn");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightNameEnLike(String value) {
            addCriterion("standard_outright_name_en like", value, "standardOutrightNameEn");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightNameEnNotLike(String value) {
            addCriterion("standard_outright_name_en not like", value, "standardOutrightNameEn");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightNameEnIn(List<String> values) {
            addCriterion("standard_outright_name_en in", values, "standardOutrightNameEn");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightNameEnNotIn(List<String> values) {
            addCriterion("standard_outright_name_en not in", values, "standardOutrightNameEn");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightNameEnBetween(String value1, String value2) {
            addCriterion("standard_outright_name_en between", value1, value2, "standardOutrightNameEn");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightNameEnNotBetween(String value1, String value2) {
            addCriterion("standard_outright_name_en not between", value1, value2, "standardOutrightNameEn");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightNameCnIsNull() {
            addCriterion("standard_outright_name_cn is null");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightNameCnIsNotNull() {
            addCriterion("standard_outright_name_cn is not null");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightNameCnEqualTo(String value) {
            addCriterion("standard_outright_name_cn =", value, "standardOutrightNameCn");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightNameCnNotEqualTo(String value) {
            addCriterion("standard_outright_name_cn <>", value, "standardOutrightNameCn");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightNameCnGreaterThan(String value) {
            addCriterion("standard_outright_name_cn >", value, "standardOutrightNameCn");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightNameCnGreaterThanOrEqualTo(String value) {
            addCriterion("standard_outright_name_cn >=", value, "standardOutrightNameCn");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightNameCnLessThan(String value) {
            addCriterion("standard_outright_name_cn <", value, "standardOutrightNameCn");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightNameCnLessThanOrEqualTo(String value) {
            addCriterion("standard_outright_name_cn <=", value, "standardOutrightNameCn");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightNameCnLike(String value) {
            addCriterion("standard_outright_name_cn like", value, "standardOutrightNameCn");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightNameCnNotLike(String value) {
            addCriterion("standard_outright_name_cn not like", value, "standardOutrightNameCn");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightNameCnIn(List<String> values) {
            addCriterion("standard_outright_name_cn in", values, "standardOutrightNameCn");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightNameCnNotIn(List<String> values) {
            addCriterion("standard_outright_name_cn not in", values, "standardOutrightNameCn");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightNameCnBetween(String value1, String value2) {
            addCriterion("standard_outright_name_cn between", value1, value2, "standardOutrightNameCn");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightNameCnNotBetween(String value1, String value2) {
            addCriterion("standard_outright_name_cn not between", value1, value2, "standardOutrightNameCn");
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

        public Criteria andMatchMarketStatusIsNull() {
            addCriterion("match_market_status is null");
            return (Criteria) this;
        }

        public Criteria andMatchMarketStatusIsNotNull() {
            addCriterion("match_market_status is not null");
            return (Criteria) this;
        }

        public Criteria andMatchMarketStatusEqualTo(Integer value) {
            addCriterion("match_market_status =", value, "matchMarketStatus");
            return (Criteria) this;
        }

        public Criteria andMatchMarketStatusNotEqualTo(Integer value) {
            addCriterion("match_market_status <>", value, "matchMarketStatus");
            return (Criteria) this;
        }

        public Criteria andMatchMarketStatusGreaterThan(Integer value) {
            addCriterion("match_market_status >", value, "matchMarketStatus");
            return (Criteria) this;
        }

        public Criteria andMatchMarketStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("match_market_status >=", value, "matchMarketStatus");
            return (Criteria) this;
        }

        public Criteria andMatchMarketStatusLessThan(Integer value) {
            addCriterion("match_market_status <", value, "matchMarketStatus");
            return (Criteria) this;
        }

        public Criteria andMatchMarketStatusLessThanOrEqualTo(Integer value) {
            addCriterion("match_market_status <=", value, "matchMarketStatus");
            return (Criteria) this;
        }

        public Criteria andMatchMarketStatusIn(List<Integer> values) {
            addCriterion("match_market_status in", values, "matchMarketStatus");
            return (Criteria) this;
        }

        public Criteria andMatchMarketStatusNotIn(List<Integer> values) {
            addCriterion("match_market_status not in", values, "matchMarketStatus");
            return (Criteria) this;
        }

        public Criteria andMatchMarketStatusBetween(Integer value1, Integer value2) {
            addCriterion("match_market_status between", value1, value2, "matchMarketStatus");
            return (Criteria) this;
        }

        public Criteria andMatchMarketStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("match_market_status not between", value1, value2, "matchMarketStatus");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightManagerIdIsNull() {
            addCriterion("standard_outright_manager_id is null");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightManagerIdIsNotNull() {
            addCriterion("standard_outright_manager_id is not null");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightManagerIdEqualTo(String value) {
            addCriterion("standard_outright_manager_id =", value, "standardOutrightManagerId");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightManagerIdNotEqualTo(String value) {
            addCriterion("standard_outright_manager_id <>", value, "standardOutrightManagerId");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightManagerIdGreaterThan(String value) {
            addCriterion("standard_outright_manager_id >", value, "standardOutrightManagerId");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightManagerIdGreaterThanOrEqualTo(String value) {
            addCriterion("standard_outright_manager_id >=", value, "standardOutrightManagerId");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightManagerIdLessThan(String value) {
            addCriterion("standard_outright_manager_id <", value, "standardOutrightManagerId");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightManagerIdLessThanOrEqualTo(String value) {
            addCriterion("standard_outright_manager_id <=", value, "standardOutrightManagerId");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightManagerIdLike(String value) {
            addCriterion("standard_outright_manager_id like", value, "standardOutrightManagerId");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightManagerIdNotLike(String value) {
            addCriterion("standard_outright_manager_id not like", value, "standardOutrightManagerId");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightManagerIdIn(List<String> values) {
            addCriterion("standard_outright_manager_id in", values, "standardOutrightManagerId");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightManagerIdNotIn(List<String> values) {
            addCriterion("standard_outright_manager_id not in", values, "standardOutrightManagerId");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightManagerIdBetween(String value1, String value2) {
            addCriterion("standard_outright_manager_id between", value1, value2, "standardOutrightManagerId");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightManagerIdNotBetween(String value1, String value2) {
            addCriterion("standard_outright_manager_id not between", value1, value2, "standardOutrightManagerId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightMatchIdIsNull() {
            addCriterion("third_outright_match_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightMatchIdIsNotNull() {
            addCriterion("third_outright_match_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightMatchIdEqualTo(Long value) {
            addCriterion("third_outright_match_id =", value, "thirdOutrightMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightMatchIdNotEqualTo(Long value) {
            addCriterion("third_outright_match_id <>", value, "thirdOutrightMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightMatchIdGreaterThan(Long value) {
            addCriterion("third_outright_match_id >", value, "thirdOutrightMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightMatchIdGreaterThanOrEqualTo(Long value) {
            addCriterion("third_outright_match_id >=", value, "thirdOutrightMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightMatchIdLessThan(Long value) {
            addCriterion("third_outright_match_id <", value, "thirdOutrightMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightMatchIdLessThanOrEqualTo(Long value) {
            addCriterion("third_outright_match_id <=", value, "thirdOutrightMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightMatchIdIn(List<Long> values) {
            addCriterion("third_outright_match_id in", values, "thirdOutrightMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightMatchIdNotIn(List<Long> values) {
            addCriterion("third_outright_match_id not in", values, "thirdOutrightMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightMatchIdBetween(Long value1, Long value2) {
            addCriterion("third_outright_match_id between", value1, value2, "thirdOutrightMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightMatchIdNotBetween(Long value1, Long value2) {
            addCriterion("third_outright_match_id not between", value1, value2, "thirdOutrightMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightMatchSourceIdIsNull() {
            addCriterion("third_outright_match_source_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightMatchSourceIdIsNotNull() {
            addCriterion("third_outright_match_source_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightMatchSourceIdEqualTo(String value) {
            addCriterion("third_outright_match_source_id =", value, "thirdOutrightMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightMatchSourceIdNotEqualTo(String value) {
            addCriterion("third_outright_match_source_id <>", value, "thirdOutrightMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightMatchSourceIdGreaterThan(String value) {
            addCriterion("third_outright_match_source_id >", value, "thirdOutrightMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightMatchSourceIdGreaterThanOrEqualTo(String value) {
            addCriterion("third_outright_match_source_id >=", value, "thirdOutrightMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightMatchSourceIdLessThan(String value) {
            addCriterion("third_outright_match_source_id <", value, "thirdOutrightMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightMatchSourceIdLessThanOrEqualTo(String value) {
            addCriterion("third_outright_match_source_id <=", value, "thirdOutrightMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightMatchSourceIdLike(String value) {
            addCriterion("third_outright_match_source_id like", value, "thirdOutrightMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightMatchSourceIdNotLike(String value) {
            addCriterion("third_outright_match_source_id not like", value, "thirdOutrightMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightMatchSourceIdIn(List<String> values) {
            addCriterion("third_outright_match_source_id in", values, "thirdOutrightMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightMatchSourceIdNotIn(List<String> values) {
            addCriterion("third_outright_match_source_id not in", values, "thirdOutrightMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightMatchSourceIdBetween(String value1, String value2) {
            addCriterion("third_outright_match_source_id between", value1, value2, "thirdOutrightMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightMatchSourceIdNotBetween(String value1, String value2) {
            addCriterion("third_outright_match_source_id not between", value1, value2, "thirdOutrightMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andStandrdOutrightMatchBegionTimeIsNull() {
            addCriterion("standrd_outright_match_begion_time is null");
            return (Criteria) this;
        }

        public Criteria andStandrdOutrightMatchBegionTimeIsNotNull() {
            addCriterion("standrd_outright_match_begion_time is not null");
            return (Criteria) this;
        }

        public Criteria andStandrdOutrightMatchBegionTimeEqualTo(Long value) {
            addCriterion("standrd_outright_match_begion_time =", value, "standrdOutrightMatchBegionTime");
            return (Criteria) this;
        }

        public Criteria andStandrdOutrightMatchBegionTimeNotEqualTo(Long value) {
            addCriterion("standrd_outright_match_begion_time <>", value, "standrdOutrightMatchBegionTime");
            return (Criteria) this;
        }

        public Criteria andStandrdOutrightMatchBegionTimeGreaterThan(Long value) {
            addCriterion("standrd_outright_match_begion_time >", value, "standrdOutrightMatchBegionTime");
            return (Criteria) this;
        }

        public Criteria andStandrdOutrightMatchBegionTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("standrd_outright_match_begion_time >=", value, "standrdOutrightMatchBegionTime");
            return (Criteria) this;
        }

        public Criteria andStandrdOutrightMatchBegionTimeLessThan(Long value) {
            addCriterion("standrd_outright_match_begion_time <", value, "standrdOutrightMatchBegionTime");
            return (Criteria) this;
        }

        public Criteria andStandrdOutrightMatchBegionTimeLessThanOrEqualTo(Long value) {
            addCriterion("standrd_outright_match_begion_time <=", value, "standrdOutrightMatchBegionTime");
            return (Criteria) this;
        }

        public Criteria andStandrdOutrightMatchBegionTimeIn(List<Long> values) {
            addCriterion("standrd_outright_match_begion_time in", values, "standrdOutrightMatchBegionTime");
            return (Criteria) this;
        }

        public Criteria andStandrdOutrightMatchBegionTimeNotIn(List<Long> values) {
            addCriterion("standrd_outright_match_begion_time not in", values, "standrdOutrightMatchBegionTime");
            return (Criteria) this;
        }

        public Criteria andStandrdOutrightMatchBegionTimeBetween(Long value1, Long value2) {
            addCriterion("standrd_outright_match_begion_time between", value1, value2, "standrdOutrightMatchBegionTime");
            return (Criteria) this;
        }

        public Criteria andStandrdOutrightMatchBegionTimeNotBetween(Long value1, Long value2) {
            addCriterion("standrd_outright_match_begion_time not between", value1, value2, "standrdOutrightMatchBegionTime");
            return (Criteria) this;
        }

        public Criteria andStandrdOutrightMatchEndTimeIsNull() {
            addCriterion("standrd_outright_match_end_time is null");
            return (Criteria) this;
        }

        public Criteria andStandrdOutrightMatchEndTimeIsNotNull() {
            addCriterion("standrd_outright_match_end_time is not null");
            return (Criteria) this;
        }

        public Criteria andStandrdOutrightMatchEndTimeEqualTo(Long value) {
            addCriterion("standrd_outright_match_end_time =", value, "standrdOutrightMatchEndTime");
            return (Criteria) this;
        }

        public Criteria andStandrdOutrightMatchEndTimeNotEqualTo(Long value) {
            addCriterion("standrd_outright_match_end_time <>", value, "standrdOutrightMatchEndTime");
            return (Criteria) this;
        }

        public Criteria andStandrdOutrightMatchEndTimeGreaterThan(Long value) {
            addCriterion("standrd_outright_match_end_time >", value, "standrdOutrightMatchEndTime");
            return (Criteria) this;
        }

        public Criteria andStandrdOutrightMatchEndTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("standrd_outright_match_end_time >=", value, "standrdOutrightMatchEndTime");
            return (Criteria) this;
        }

        public Criteria andStandrdOutrightMatchEndTimeLessThan(Long value) {
            addCriterion("standrd_outright_match_end_time <", value, "standrdOutrightMatchEndTime");
            return (Criteria) this;
        }

        public Criteria andStandrdOutrightMatchEndTimeLessThanOrEqualTo(Long value) {
            addCriterion("standrd_outright_match_end_time <=", value, "standrdOutrightMatchEndTime");
            return (Criteria) this;
        }

        public Criteria andStandrdOutrightMatchEndTimeIn(List<Long> values) {
            addCriterion("standrd_outright_match_end_time in", values, "standrdOutrightMatchEndTime");
            return (Criteria) this;
        }

        public Criteria andStandrdOutrightMatchEndTimeNotIn(List<Long> values) {
            addCriterion("standrd_outright_match_end_time not in", values, "standrdOutrightMatchEndTime");
            return (Criteria) this;
        }

        public Criteria andStandrdOutrightMatchEndTimeBetween(Long value1, Long value2) {
            addCriterion("standrd_outright_match_end_time between", value1, value2, "standrdOutrightMatchEndTime");
            return (Criteria) this;
        }

        public Criteria andStandrdOutrightMatchEndTimeNotBetween(Long value1, Long value2) {
            addCriterion("standrd_outright_match_end_time not between", value1, value2, "standrdOutrightMatchEndTime");
            return (Criteria) this;
        }

        public Criteria andSellStatusIsNull() {
            addCriterion("sell_status is null");
            return (Criteria) this;
        }

        public Criteria andSellStatusIsNotNull() {
            addCriterion("sell_status is not null");
            return (Criteria) this;
        }

        public Criteria andSellStatusEqualTo(String value) {
            addCriterion("sell_status =", value, "sellStatus");
            return (Criteria) this;
        }

        public Criteria andSellStatusNotEqualTo(String value) {
            addCriterion("sell_status <>", value, "sellStatus");
            return (Criteria) this;
        }

        public Criteria andSellStatusGreaterThan(String value) {
            addCriterion("sell_status >", value, "sellStatus");
            return (Criteria) this;
        }

        public Criteria andSellStatusGreaterThanOrEqualTo(String value) {
            addCriterion("sell_status >=", value, "sellStatus");
            return (Criteria) this;
        }

        public Criteria andSellStatusLessThan(String value) {
            addCriterion("sell_status <", value, "sellStatus");
            return (Criteria) this;
        }

        public Criteria andSellStatusLessThanOrEqualTo(String value) {
            addCriterion("sell_status <=", value, "sellStatus");
            return (Criteria) this;
        }

        public Criteria andSellStatusLike(String value) {
            addCriterion("sell_status like", value, "sellStatus");
            return (Criteria) this;
        }

        public Criteria andSellStatusNotLike(String value) {
            addCriterion("sell_status not like", value, "sellStatus");
            return (Criteria) this;
        }

        public Criteria andSellStatusIn(List<String> values) {
            addCriterion("sell_status in", values, "sellStatus");
            return (Criteria) this;
        }

        public Criteria andSellStatusNotIn(List<String> values) {
            addCriterion("sell_status not in", values, "sellStatus");
            return (Criteria) this;
        }

        public Criteria andSellStatusBetween(String value1, String value2) {
            addCriterion("sell_status between", value1, value2, "sellStatus");
            return (Criteria) this;
        }

        public Criteria andSellStatusNotBetween(String value1, String value2) {
            addCriterion("sell_status not between", value1, value2, "sellStatus");
            return (Criteria) this;
        }

        public Criteria andAutoSellStatusIsNull() {
            addCriterion("auto_sell_status is null");
            return (Criteria) this;
        }

        public Criteria andAutoSellStatusIsNotNull() {
            addCriterion("auto_sell_status is not null");
            return (Criteria) this;
        }

        public Criteria andAutoSellStatusEqualTo(String value) {
            addCriterion("auto_sell_status =", value, "autoSellStatus");
            return (Criteria) this;
        }

        public Criteria andAutoSellStatusNotEqualTo(String value) {
            addCriterion("auto_sell_status <>", value, "autoSellStatus");
            return (Criteria) this;
        }

        public Criteria andAutoSellStatusGreaterThan(String value) {
            addCriterion("auto_sell_status >", value, "autoSellStatus");
            return (Criteria) this;
        }

        public Criteria andAutoSellStatusGreaterThanOrEqualTo(String value) {
            addCriterion("auto_sell_status >=", value, "autoSellStatus");
            return (Criteria) this;
        }

        public Criteria andAutoSellStatusLessThan(String value) {
            addCriterion("auto_sell_status <", value, "autoSellStatus");
            return (Criteria) this;
        }

        public Criteria andAutoSellStatusLessThanOrEqualTo(String value) {
            addCriterion("auto_sell_status <=", value, "autoSellStatus");
            return (Criteria) this;
        }

        public Criteria andAutoSellStatusLike(String value) {
            addCriterion("auto_sell_status like", value, "autoSellStatus");
            return (Criteria) this;
        }

        public Criteria andAutoSellStatusNotLike(String value) {
            addCriterion("auto_sell_status not like", value, "autoSellStatus");
            return (Criteria) this;
        }

        public Criteria andAutoSellStatusIn(List<String> values) {
            addCriterion("auto_sell_status in", values, "autoSellStatus");
            return (Criteria) this;
        }

        public Criteria andAutoSellStatusNotIn(List<String> values) {
            addCriterion("auto_sell_status not in", values, "autoSellStatus");
            return (Criteria) this;
        }

        public Criteria andAutoSellStatusBetween(String value1, String value2) {
            addCriterion("auto_sell_status between", value1, value2, "autoSellStatus");
            return (Criteria) this;
        }

        public Criteria andAutoSellStatusNotBetween(String value1, String value2) {
            addCriterion("auto_sell_status not between", value1, value2, "autoSellStatus");
            return (Criteria) this;
        }

        public Criteria andSeasonIdIsNull() {
            addCriterion("season_id is null");
            return (Criteria) this;
        }

        public Criteria andSeasonIdIsNotNull() {
            addCriterion("season_id is not null");
            return (Criteria) this;
        }

        public Criteria andSeasonIdEqualTo(String value) {
            addCriterion("season_id =", value, "seasonId");
            return (Criteria) this;
        }

        public Criteria andSeasonIdNotEqualTo(String value) {
            addCriterion("season_id <>", value, "seasonId");
            return (Criteria) this;
        }

        public Criteria andSeasonIdGreaterThan(String value) {
            addCriterion("season_id >", value, "seasonId");
            return (Criteria) this;
        }

        public Criteria andSeasonIdGreaterThanOrEqualTo(String value) {
            addCriterion("season_id >=", value, "seasonId");
            return (Criteria) this;
        }

        public Criteria andSeasonIdLessThan(String value) {
            addCriterion("season_id <", value, "seasonId");
            return (Criteria) this;
        }

        public Criteria andSeasonIdLessThanOrEqualTo(String value) {
            addCriterion("season_id <=", value, "seasonId");
            return (Criteria) this;
        }

        public Criteria andSeasonIdLike(String value) {
            addCriterion("season_id like", value, "seasonId");
            return (Criteria) this;
        }

        public Criteria andSeasonIdNotLike(String value) {
            addCriterion("season_id not like", value, "seasonId");
            return (Criteria) this;
        }

        public Criteria andSeasonIdIn(List<String> values) {
            addCriterion("season_id in", values, "seasonId");
            return (Criteria) this;
        }

        public Criteria andSeasonIdNotIn(List<String> values) {
            addCriterion("season_id not in", values, "seasonId");
            return (Criteria) this;
        }

        public Criteria andSeasonIdBetween(String value1, String value2) {
            addCriterion("season_id between", value1, value2, "seasonId");
            return (Criteria) this;
        }

        public Criteria andSeasonIdNotBetween(String value1, String value2) {
            addCriterion("season_id not between", value1, value2, "seasonId");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightYearIsNull() {
            addCriterion("standard_outright_year is null");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightYearIsNotNull() {
            addCriterion("standard_outright_year is not null");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightYearEqualTo(String value) {
            addCriterion("standard_outright_year =", value, "standardOutrightYear");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightYearNotEqualTo(String value) {
            addCriterion("standard_outright_year <>", value, "standardOutrightYear");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightYearGreaterThan(String value) {
            addCriterion("standard_outright_year >", value, "standardOutrightYear");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightYearGreaterThanOrEqualTo(String value) {
            addCriterion("standard_outright_year >=", value, "standardOutrightYear");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightYearLessThan(String value) {
            addCriterion("standard_outright_year <", value, "standardOutrightYear");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightYearLessThanOrEqualTo(String value) {
            addCriterion("standard_outright_year <=", value, "standardOutrightYear");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightYearLike(String value) {
            addCriterion("standard_outright_year like", value, "standardOutrightYear");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightYearNotLike(String value) {
            addCriterion("standard_outright_year not like", value, "standardOutrightYear");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightYearIn(List<String> values) {
            addCriterion("standard_outright_year in", values, "standardOutrightYear");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightYearNotIn(List<String> values) {
            addCriterion("standard_outright_year not in", values, "standardOutrightYear");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightYearBetween(String value1, String value2) {
            addCriterion("standard_outright_year between", value1, value2, "standardOutrightYear");
            return (Criteria) this;
        }

        public Criteria andStandardOutrightYearNotBetween(String value1, String value2) {
            addCriterion("standard_outright_year not between", value1, value2, "standardOutrightYear");
            return (Criteria) this;
        }

        public Criteria andBookedIsNull() {
            addCriterion("booked is null");
            return (Criteria) this;
        }

        public Criteria andBookedIsNotNull() {
            addCriterion("booked is not null");
            return (Criteria) this;
        }

        public Criteria andBookedEqualTo(Integer value) {
            addCriterion("booked =", value, "booked");
            return (Criteria) this;
        }

        public Criteria andBookedNotEqualTo(Integer value) {
            addCriterion("booked <>", value, "booked");
            return (Criteria) this;
        }

        public Criteria andBookedGreaterThan(Integer value) {
            addCriterion("booked >", value, "booked");
            return (Criteria) this;
        }

        public Criteria andBookedGreaterThanOrEqualTo(Integer value) {
            addCriterion("booked >=", value, "booked");
            return (Criteria) this;
        }

        public Criteria andBookedLessThan(Integer value) {
            addCriterion("booked <", value, "booked");
            return (Criteria) this;
        }

        public Criteria andBookedLessThanOrEqualTo(Integer value) {
            addCriterion("booked <=", value, "booked");
            return (Criteria) this;
        }

        public Criteria andBookedIn(List<Integer> values) {
            addCriterion("booked in", values, "booked");
            return (Criteria) this;
        }

        public Criteria andBookedNotIn(List<Integer> values) {
            addCriterion("booked not in", values, "booked");
            return (Criteria) this;
        }

        public Criteria andBookedBetween(Integer value1, Integer value2) {
            addCriterion("booked between", value1, value2, "booked");
            return (Criteria) this;
        }

        public Criteria andBookedNotBetween(Integer value1, Integer value2) {
            addCriterion("booked not between", value1, value2, "booked");
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