package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class ThirdOutrightMatchInfoExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ThirdOutrightMatchInfoExample() {
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

        public Criteria andThirdMatchNameEnIsNull() {
            addCriterion("third_match_name_en is null");
            return (Criteria) this;
        }

        public Criteria andThirdMatchNameEnIsNotNull() {
            addCriterion("third_match_name_en is not null");
            return (Criteria) this;
        }

        public Criteria andThirdMatchNameEnEqualTo(String value) {
            addCriterion("third_match_name_en =", value, "thirdMatchNameEn");
            return (Criteria) this;
        }

        public Criteria andThirdMatchNameEnNotEqualTo(String value) {
            addCriterion("third_match_name_en <>", value, "thirdMatchNameEn");
            return (Criteria) this;
        }

        public Criteria andThirdMatchNameEnGreaterThan(String value) {
            addCriterion("third_match_name_en >", value, "thirdMatchNameEn");
            return (Criteria) this;
        }

        public Criteria andThirdMatchNameEnGreaterThanOrEqualTo(String value) {
            addCriterion("third_match_name_en >=", value, "thirdMatchNameEn");
            return (Criteria) this;
        }

        public Criteria andThirdMatchNameEnLessThan(String value) {
            addCriterion("third_match_name_en <", value, "thirdMatchNameEn");
            return (Criteria) this;
        }

        public Criteria andThirdMatchNameEnLessThanOrEqualTo(String value) {
            addCriterion("third_match_name_en <=", value, "thirdMatchNameEn");
            return (Criteria) this;
        }

        public Criteria andThirdMatchNameEnLike(String value) {
            addCriterion("third_match_name_en like", value, "thirdMatchNameEn");
            return (Criteria) this;
        }

        public Criteria andThirdMatchNameEnNotLike(String value) {
            addCriterion("third_match_name_en not like", value, "thirdMatchNameEn");
            return (Criteria) this;
        }

        public Criteria andThirdMatchNameEnIn(List<String> values) {
            addCriterion("third_match_name_en in", values, "thirdMatchNameEn");
            return (Criteria) this;
        }

        public Criteria andThirdMatchNameEnNotIn(List<String> values) {
            addCriterion("third_match_name_en not in", values, "thirdMatchNameEn");
            return (Criteria) this;
        }

        public Criteria andThirdMatchNameEnBetween(String value1, String value2) {
            addCriterion("third_match_name_en between", value1, value2, "thirdMatchNameEn");
            return (Criteria) this;
        }

        public Criteria andThirdMatchNameEnNotBetween(String value1, String value2) {
            addCriterion("third_match_name_en not between", value1, value2, "thirdMatchNameEn");
            return (Criteria) this;
        }

        public Criteria andThirdMatchNameCnIsNull() {
            addCriterion("third_match_name_cn is null");
            return (Criteria) this;
        }

        public Criteria andThirdMatchNameCnIsNotNull() {
            addCriterion("third_match_name_cn is not null");
            return (Criteria) this;
        }

        public Criteria andThirdMatchNameCnEqualTo(String value) {
            addCriterion("third_match_name_cn =", value, "thirdMatchNameCn");
            return (Criteria) this;
        }

        public Criteria andThirdMatchNameCnNotEqualTo(String value) {
            addCriterion("third_match_name_cn <>", value, "thirdMatchNameCn");
            return (Criteria) this;
        }

        public Criteria andThirdMatchNameCnGreaterThan(String value) {
            addCriterion("third_match_name_cn >", value, "thirdMatchNameCn");
            return (Criteria) this;
        }

        public Criteria andThirdMatchNameCnGreaterThanOrEqualTo(String value) {
            addCriterion("third_match_name_cn >=", value, "thirdMatchNameCn");
            return (Criteria) this;
        }

        public Criteria andThirdMatchNameCnLessThan(String value) {
            addCriterion("third_match_name_cn <", value, "thirdMatchNameCn");
            return (Criteria) this;
        }

        public Criteria andThirdMatchNameCnLessThanOrEqualTo(String value) {
            addCriterion("third_match_name_cn <=", value, "thirdMatchNameCn");
            return (Criteria) this;
        }

        public Criteria andThirdMatchNameCnLike(String value) {
            addCriterion("third_match_name_cn like", value, "thirdMatchNameCn");
            return (Criteria) this;
        }

        public Criteria andThirdMatchNameCnNotLike(String value) {
            addCriterion("third_match_name_cn not like", value, "thirdMatchNameCn");
            return (Criteria) this;
        }

        public Criteria andThirdMatchNameCnIn(List<String> values) {
            addCriterion("third_match_name_cn in", values, "thirdMatchNameCn");
            return (Criteria) this;
        }

        public Criteria andThirdMatchNameCnNotIn(List<String> values) {
            addCriterion("third_match_name_cn not in", values, "thirdMatchNameCn");
            return (Criteria) this;
        }

        public Criteria andThirdMatchNameCnBetween(String value1, String value2) {
            addCriterion("third_match_name_cn between", value1, value2, "thirdMatchNameCn");
            return (Criteria) this;
        }

        public Criteria andThirdMatchNameCnNotBetween(String value1, String value2) {
            addCriterion("third_match_name_cn not between", value1, value2, "thirdMatchNameCn");
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

        public Criteria andThirdOutrightBeginTimeIsNull() {
            addCriterion("third_outright_begin_time is null");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightBeginTimeIsNotNull() {
            addCriterion("third_outright_begin_time is not null");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightBeginTimeEqualTo(Long value) {
            addCriterion("third_outright_begin_time =", value, "thirdOutrightBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightBeginTimeNotEqualTo(Long value) {
            addCriterion("third_outright_begin_time <>", value, "thirdOutrightBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightBeginTimeGreaterThan(Long value) {
            addCriterion("third_outright_begin_time >", value, "thirdOutrightBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightBeginTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("third_outright_begin_time >=", value, "thirdOutrightBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightBeginTimeLessThan(Long value) {
            addCriterion("third_outright_begin_time <", value, "thirdOutrightBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightBeginTimeLessThanOrEqualTo(Long value) {
            addCriterion("third_outright_begin_time <=", value, "thirdOutrightBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightBeginTimeIn(List<Long> values) {
            addCriterion("third_outright_begin_time in", values, "thirdOutrightBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightBeginTimeNotIn(List<Long> values) {
            addCriterion("third_outright_begin_time not in", values, "thirdOutrightBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightBeginTimeBetween(Long value1, Long value2) {
            addCriterion("third_outright_begin_time between", value1, value2, "thirdOutrightBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightBeginTimeNotBetween(Long value1, Long value2) {
            addCriterion("third_outright_begin_time not between", value1, value2, "thirdOutrightBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightEndTimeIsNull() {
            addCriterion("third_outright_end_time is null");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightEndTimeIsNotNull() {
            addCriterion("third_outright_end_time is not null");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightEndTimeEqualTo(Long value) {
            addCriterion("third_outright_end_time =", value, "thirdOutrightEndTime");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightEndTimeNotEqualTo(Long value) {
            addCriterion("third_outright_end_time <>", value, "thirdOutrightEndTime");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightEndTimeGreaterThan(Long value) {
            addCriterion("third_outright_end_time >", value, "thirdOutrightEndTime");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightEndTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("third_outright_end_time >=", value, "thirdOutrightEndTime");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightEndTimeLessThan(Long value) {
            addCriterion("third_outright_end_time <", value, "thirdOutrightEndTime");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightEndTimeLessThanOrEqualTo(Long value) {
            addCriterion("third_outright_end_time <=", value, "thirdOutrightEndTime");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightEndTimeIn(List<Long> values) {
            addCriterion("third_outright_end_time in", values, "thirdOutrightEndTime");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightEndTimeNotIn(List<Long> values) {
            addCriterion("third_outright_end_time not in", values, "thirdOutrightEndTime");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightEndTimeBetween(Long value1, Long value2) {
            addCriterion("third_outright_end_time between", value1, value2, "thirdOutrightEndTime");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightEndTimeNotBetween(Long value1, Long value2) {
            addCriterion("third_outright_end_time not between", value1, value2, "thirdOutrightEndTime");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightSourceIdIsNull() {
            addCriterion("third_outright_source_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightSourceIdIsNotNull() {
            addCriterion("third_outright_source_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightSourceIdEqualTo(String value) {
            addCriterion("third_outright_source_id =", value, "thirdOutrightSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightSourceIdNotEqualTo(String value) {
            addCriterion("third_outright_source_id <>", value, "thirdOutrightSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightSourceIdGreaterThan(String value) {
            addCriterion("third_outright_source_id >", value, "thirdOutrightSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightSourceIdGreaterThanOrEqualTo(String value) {
            addCriterion("third_outright_source_id >=", value, "thirdOutrightSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightSourceIdLessThan(String value) {
            addCriterion("third_outright_source_id <", value, "thirdOutrightSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightSourceIdLessThanOrEqualTo(String value) {
            addCriterion("third_outright_source_id <=", value, "thirdOutrightSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightSourceIdLike(String value) {
            addCriterion("third_outright_source_id like", value, "thirdOutrightSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightSourceIdNotLike(String value) {
            addCriterion("third_outright_source_id not like", value, "thirdOutrightSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightSourceIdIn(List<String> values) {
            addCriterion("third_outright_source_id in", values, "thirdOutrightSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightSourceIdNotIn(List<String> values) {
            addCriterion("third_outright_source_id not in", values, "thirdOutrightSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightSourceIdBetween(String value1, String value2) {
            addCriterion("third_outright_source_id between", value1, value2, "thirdOutrightSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightSourceIdNotBetween(String value1, String value2) {
            addCriterion("third_outright_source_id not between", value1, value2, "thirdOutrightSourceId");
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

        public Criteria andThirdOutrightYearIsNull() {
            addCriterion("third_outright_year is null");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightYearIsNotNull() {
            addCriterion("third_outright_year is not null");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightYearEqualTo(String value) {
            addCriterion("third_outright_year =", value, "thirdOutrightYear");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightYearNotEqualTo(String value) {
            addCriterion("third_outright_year <>", value, "thirdOutrightYear");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightYearGreaterThan(String value) {
            addCriterion("third_outright_year >", value, "thirdOutrightYear");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightYearGreaterThanOrEqualTo(String value) {
            addCriterion("third_outright_year >=", value, "thirdOutrightYear");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightYearLessThan(String value) {
            addCriterion("third_outright_year <", value, "thirdOutrightYear");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightYearLessThanOrEqualTo(String value) {
            addCriterion("third_outright_year <=", value, "thirdOutrightYear");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightYearLike(String value) {
            addCriterion("third_outright_year like", value, "thirdOutrightYear");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightYearNotLike(String value) {
            addCriterion("third_outright_year not like", value, "thirdOutrightYear");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightYearIn(List<String> values) {
            addCriterion("third_outright_year in", values, "thirdOutrightYear");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightYearNotIn(List<String> values) {
            addCriterion("third_outright_year not in", values, "thirdOutrightYear");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightYearBetween(String value1, String value2) {
            addCriterion("third_outright_year between", value1, value2, "thirdOutrightYear");
            return (Criteria) this;
        }

        public Criteria andThirdOutrightYearNotBetween(String value1, String value2) {
            addCriterion("third_outright_year not between", value1, value2, "thirdOutrightYear");
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