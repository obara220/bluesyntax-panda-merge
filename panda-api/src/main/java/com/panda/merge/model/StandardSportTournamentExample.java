package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class StandardSportTournamentExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public StandardSportTournamentExample() {
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

        public Criteria andThirdTournamentIdIsNull() {
            addCriterion("third_tournament_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentIdIsNotNull() {
            addCriterion("third_tournament_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentIdEqualTo(Long value) {
            addCriterion("third_tournament_id =", value, "thirdTournamentId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentIdNotEqualTo(Long value) {
            addCriterion("third_tournament_id <>", value, "thirdTournamentId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentIdGreaterThan(Long value) {
            addCriterion("third_tournament_id >", value, "thirdTournamentId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentIdGreaterThanOrEqualTo(Long value) {
            addCriterion("third_tournament_id >=", value, "thirdTournamentId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentIdLessThan(Long value) {
            addCriterion("third_tournament_id <", value, "thirdTournamentId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentIdLessThanOrEqualTo(Long value) {
            addCriterion("third_tournament_id <=", value, "thirdTournamentId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentIdIn(List<Long> values) {
            addCriterion("third_tournament_id in", values, "thirdTournamentId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentIdNotIn(List<Long> values) {
            addCriterion("third_tournament_id not in", values, "thirdTournamentId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentIdBetween(Long value1, Long value2) {
            addCriterion("third_tournament_id between", value1, value2, "thirdTournamentId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentIdNotBetween(Long value1, Long value2) {
            addCriterion("third_tournament_id not between", value1, value2, "thirdTournamentId");
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

        public Criteria andRegionTypeIsNull() {
            addCriterion("region_type is null");
            return (Criteria) this;
        }

        public Criteria andRegionTypeIsNotNull() {
            addCriterion("region_type is not null");
            return (Criteria) this;
        }

        public Criteria andRegionTypeEqualTo(Integer value) {
            addCriterion("region_type =", value, "regionType");
            return (Criteria) this;
        }

        public Criteria andRegionTypeNotEqualTo(Integer value) {
            addCriterion("region_type <>", value, "regionType");
            return (Criteria) this;
        }

        public Criteria andRegionTypeGreaterThan(Integer value) {
            addCriterion("region_type >", value, "regionType");
            return (Criteria) this;
        }

        public Criteria andRegionTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("region_type >=", value, "regionType");
            return (Criteria) this;
        }

        public Criteria andRegionTypeLessThan(Integer value) {
            addCriterion("region_type <", value, "regionType");
            return (Criteria) this;
        }

        public Criteria andRegionTypeLessThanOrEqualTo(Integer value) {
            addCriterion("region_type <=", value, "regionType");
            return (Criteria) this;
        }

        public Criteria andRegionTypeIn(List<Integer> values) {
            addCriterion("region_type in", values, "regionType");
            return (Criteria) this;
        }

        public Criteria andRegionTypeNotIn(List<Integer> values) {
            addCriterion("region_type not in", values, "regionType");
            return (Criteria) this;
        }

        public Criteria andRegionTypeBetween(Integer value1, Integer value2) {
            addCriterion("region_type between", value1, value2, "regionType");
            return (Criteria) this;
        }

        public Criteria andRegionTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("region_type not between", value1, value2, "regionType");
            return (Criteria) this;
        }

        public Criteria andTournamentLevelIsNull() {
            addCriterion("tournament_level is null");
            return (Criteria) this;
        }

        public Criteria andTournamentLevelIsNotNull() {
            addCriterion("tournament_level is not null");
            return (Criteria) this;
        }

        public Criteria andTournamentLevelEqualTo(Integer value) {
            addCriterion("tournament_level =", value, "tournamentLevel");
            return (Criteria) this;
        }

        public Criteria andTournamentLevelNotEqualTo(Integer value) {
            addCriterion("tournament_level <>", value, "tournamentLevel");
            return (Criteria) this;
        }

        public Criteria andTournamentLevelGreaterThan(Integer value) {
            addCriterion("tournament_level >", value, "tournamentLevel");
            return (Criteria) this;
        }

        public Criteria andTournamentLevelGreaterThanOrEqualTo(Integer value) {
            addCriterion("tournament_level >=", value, "tournamentLevel");
            return (Criteria) this;
        }

        public Criteria andTournamentLevelLessThan(Integer value) {
            addCriterion("tournament_level <", value, "tournamentLevel");
            return (Criteria) this;
        }

        public Criteria andTournamentLevelLessThanOrEqualTo(Integer value) {
            addCriterion("tournament_level <=", value, "tournamentLevel");
            return (Criteria) this;
        }

        public Criteria andTournamentLevelIn(List<Integer> values) {
            addCriterion("tournament_level in", values, "tournamentLevel");
            return (Criteria) this;
        }

        public Criteria andTournamentLevelNotIn(List<Integer> values) {
            addCriterion("tournament_level not in", values, "tournamentLevel");
            return (Criteria) this;
        }

        public Criteria andTournamentLevelBetween(Integer value1, Integer value2) {
            addCriterion("tournament_level between", value1, value2, "tournamentLevel");
            return (Criteria) this;
        }

        public Criteria andTournamentLevelNotBetween(Integer value1, Integer value2) {
            addCriterion("tournament_level not between", value1, value2, "tournamentLevel");
            return (Criteria) this;
        }

        public Criteria andTournamentManagerIdIsNull() {
            addCriterion("tournament_manager_id is null");
            return (Criteria) this;
        }

        public Criteria andTournamentManagerIdIsNotNull() {
            addCriterion("tournament_manager_id is not null");
            return (Criteria) this;
        }

        public Criteria andTournamentManagerIdEqualTo(String value) {
            addCriterion("tournament_manager_id =", value, "tournamentManagerId");
            return (Criteria) this;
        }

        public Criteria andTournamentManagerIdNotEqualTo(String value) {
            addCriterion("tournament_manager_id <>", value, "tournamentManagerId");
            return (Criteria) this;
        }

        public Criteria andTournamentManagerIdGreaterThan(String value) {
            addCriterion("tournament_manager_id >", value, "tournamentManagerId");
            return (Criteria) this;
        }

        public Criteria andTournamentManagerIdGreaterThanOrEqualTo(String value) {
            addCriterion("tournament_manager_id >=", value, "tournamentManagerId");
            return (Criteria) this;
        }

        public Criteria andTournamentManagerIdLessThan(String value) {
            addCriterion("tournament_manager_id <", value, "tournamentManagerId");
            return (Criteria) this;
        }

        public Criteria andTournamentManagerIdLessThanOrEqualTo(String value) {
            addCriterion("tournament_manager_id <=", value, "tournamentManagerId");
            return (Criteria) this;
        }

        public Criteria andTournamentManagerIdLike(String value) {
            addCriterion("tournament_manager_id like", value, "tournamentManagerId");
            return (Criteria) this;
        }

        public Criteria andTournamentManagerIdNotLike(String value) {
            addCriterion("tournament_manager_id not like", value, "tournamentManagerId");
            return (Criteria) this;
        }

        public Criteria andTournamentManagerIdIn(List<String> values) {
            addCriterion("tournament_manager_id in", values, "tournamentManagerId");
            return (Criteria) this;
        }

        public Criteria andTournamentManagerIdNotIn(List<String> values) {
            addCriterion("tournament_manager_id not in", values, "tournamentManagerId");
            return (Criteria) this;
        }

        public Criteria andTournamentManagerIdBetween(String value1, String value2) {
            addCriterion("tournament_manager_id between", value1, value2, "tournamentManagerId");
            return (Criteria) this;
        }

        public Criteria andTournamentManagerIdNotBetween(String value1, String value2) {
            addCriterion("tournament_manager_id not between", value1, value2, "tournamentManagerId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentSourceIdIsNull() {
            addCriterion("third_tournament_source_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentSourceIdIsNotNull() {
            addCriterion("third_tournament_source_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentSourceIdEqualTo(String value) {
            addCriterion("third_tournament_source_id =", value, "thirdTournamentSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentSourceIdNotEqualTo(String value) {
            addCriterion("third_tournament_source_id <>", value, "thirdTournamentSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentSourceIdGreaterThan(String value) {
            addCriterion("third_tournament_source_id >", value, "thirdTournamentSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentSourceIdGreaterThanOrEqualTo(String value) {
            addCriterion("third_tournament_source_id >=", value, "thirdTournamentSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentSourceIdLessThan(String value) {
            addCriterion("third_tournament_source_id <", value, "thirdTournamentSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentSourceIdLessThanOrEqualTo(String value) {
            addCriterion("third_tournament_source_id <=", value, "thirdTournamentSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentSourceIdLike(String value) {
            addCriterion("third_tournament_source_id like", value, "thirdTournamentSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentSourceIdNotLike(String value) {
            addCriterion("third_tournament_source_id not like", value, "thirdTournamentSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentSourceIdIn(List<String> values) {
            addCriterion("third_tournament_source_id in", values, "thirdTournamentSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentSourceIdNotIn(List<String> values) {
            addCriterion("third_tournament_source_id not in", values, "thirdTournamentSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentSourceIdBetween(String value1, String value2) {
            addCriterion("third_tournament_source_id between", value1, value2, "thirdTournamentSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentSourceIdNotBetween(String value1, String value2) {
            addCriterion("third_tournament_source_id not between", value1, value2, "thirdTournamentSourceId");
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

        public Criteria andSeasonNameCodeIsNull() {
            addCriterion("season_name_code is null");
            return (Criteria) this;
        }

        public Criteria andSeasonNameCodeIsNotNull() {
            addCriterion("season_name_code is not null");
            return (Criteria) this;
        }

        public Criteria andSeasonNameCodeEqualTo(Long value) {
            addCriterion("season_name_code =", value, "seasonNameCode");
            return (Criteria) this;
        }

        public Criteria andSeasonNameCodeNotEqualTo(Long value) {
            addCriterion("season_name_code <>", value, "seasonNameCode");
            return (Criteria) this;
        }

        public Criteria andSeasonNameCodeGreaterThan(Long value) {
            addCriterion("season_name_code >", value, "seasonNameCode");
            return (Criteria) this;
        }

        public Criteria andSeasonNameCodeGreaterThanOrEqualTo(Long value) {
            addCriterion("season_name_code >=", value, "seasonNameCode");
            return (Criteria) this;
        }

        public Criteria andSeasonNameCodeLessThan(Long value) {
            addCriterion("season_name_code <", value, "seasonNameCode");
            return (Criteria) this;
        }

        public Criteria andSeasonNameCodeLessThanOrEqualTo(Long value) {
            addCriterion("season_name_code <=", value, "seasonNameCode");
            return (Criteria) this;
        }

        public Criteria andSeasonNameCodeIn(List<Long> values) {
            addCriterion("season_name_code in", values, "seasonNameCode");
            return (Criteria) this;
        }

        public Criteria andSeasonNameCodeNotIn(List<Long> values) {
            addCriterion("season_name_code not in", values, "seasonNameCode");
            return (Criteria) this;
        }

        public Criteria andSeasonNameCodeBetween(Long value1, Long value2) {
            addCriterion("season_name_code between", value1, value2, "seasonNameCode");
            return (Criteria) this;
        }

        public Criteria andSeasonNameCodeNotBetween(Long value1, Long value2) {
            addCriterion("season_name_code not between", value1, value2, "seasonNameCode");
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

        public Criteria andFatherTournamentIdIsNull() {
            addCriterion("father_tournament_id is null");
            return (Criteria) this;
        }

        public Criteria andFatherTournamentIdIsNotNull() {
            addCriterion("father_tournament_id is not null");
            return (Criteria) this;
        }

        public Criteria andFatherTournamentIdEqualTo(String value) {
            addCriterion("father_tournament_id =", value, "fatherTournamentId");
            return (Criteria) this;
        }

        public Criteria andFatherTournamentIdNotEqualTo(String value) {
            addCriterion("father_tournament_id <>", value, "fatherTournamentId");
            return (Criteria) this;
        }

        public Criteria andFatherTournamentIdGreaterThan(String value) {
            addCriterion("father_tournament_id >", value, "fatherTournamentId");
            return (Criteria) this;
        }

        public Criteria andFatherTournamentIdGreaterThanOrEqualTo(String value) {
            addCriterion("father_tournament_id >=", value, "fatherTournamentId");
            return (Criteria) this;
        }

        public Criteria andFatherTournamentIdLessThan(String value) {
            addCriterion("father_tournament_id <", value, "fatherTournamentId");
            return (Criteria) this;
        }

        public Criteria andFatherTournamentIdLessThanOrEqualTo(String value) {
            addCriterion("father_tournament_id <=", value, "fatherTournamentId");
            return (Criteria) this;
        }

        public Criteria andFatherTournamentIdLike(String value) {
            addCriterion("father_tournament_id like", value, "fatherTournamentId");
            return (Criteria) this;
        }

        public Criteria andFatherTournamentIdNotLike(String value) {
            addCriterion("father_tournament_id not like", value, "fatherTournamentId");
            return (Criteria) this;
        }

        public Criteria andFatherTournamentIdIn(List<String> values) {
            addCriterion("father_tournament_id in", values, "fatherTournamentId");
            return (Criteria) this;
        }

        public Criteria andFatherTournamentIdNotIn(List<String> values) {
            addCriterion("father_tournament_id not in", values, "fatherTournamentId");
            return (Criteria) this;
        }

        public Criteria andFatherTournamentIdBetween(String value1, String value2) {
            addCriterion("father_tournament_id between", value1, value2, "fatherTournamentId");
            return (Criteria) this;
        }

        public Criteria andFatherTournamentIdNotBetween(String value1, String value2) {
            addCriterion("father_tournament_id not between", value1, value2, "fatherTournamentId");
            return (Criteria) this;
        }

        public Criteria andSimpleFlageIsNull() {
            addCriterion("simple_flage is null");
            return (Criteria) this;
        }

        public Criteria andSimpleFlageIsNotNull() {
            addCriterion("simple_flage is not null");
            return (Criteria) this;
        }

        public Criteria andSimpleFlageEqualTo(Integer value) {
            addCriterion("simple_flage =", value, "simpleFlage");
            return (Criteria) this;
        }

        public Criteria andSimpleFlageNotEqualTo(Integer value) {
            addCriterion("simple_flage <>", value, "simpleFlage");
            return (Criteria) this;
        }

        public Criteria andSimpleFlageGreaterThan(Integer value) {
            addCriterion("simple_flage >", value, "simpleFlage");
            return (Criteria) this;
        }

        public Criteria andSimpleFlageGreaterThanOrEqualTo(Integer value) {
            addCriterion("simple_flage >=", value, "simpleFlage");
            return (Criteria) this;
        }

        public Criteria andSimpleFlageLessThan(Integer value) {
            addCriterion("simple_flage <", value, "simpleFlage");
            return (Criteria) this;
        }

        public Criteria andSimpleFlageLessThanOrEqualTo(Integer value) {
            addCriterion("simple_flage <=", value, "simpleFlage");
            return (Criteria) this;
        }

        public Criteria andSimpleFlageIn(List<Integer> values) {
            addCriterion("simple_flage in", values, "simpleFlage");
            return (Criteria) this;
        }

        public Criteria andSimpleFlageNotIn(List<Integer> values) {
            addCriterion("simple_flage not in", values, "simpleFlage");
            return (Criteria) this;
        }

        public Criteria andSimpleFlageBetween(Integer value1, Integer value2) {
            addCriterion("simple_flage between", value1, value2, "simpleFlage");
            return (Criteria) this;
        }

        public Criteria andSimpleFlageNotBetween(Integer value1, Integer value2) {
            addCriterion("simple_flage not between", value1, value2, "simpleFlage");
            return (Criteria) this;
        }

        public Criteria andCurrentSeasonIdIsNull() {
            addCriterion("current_season_id is null");
            return (Criteria) this;
        }

        public Criteria andCurrentSeasonIdIsNotNull() {
            addCriterion("current_season_id is not null");
            return (Criteria) this;
        }

        public Criteria andCurrentSeasonIdEqualTo(String value) {
            addCriterion("current_season_id =", value, "currentSeasonId");
            return (Criteria) this;
        }

        public Criteria andCurrentSeasonIdNotEqualTo(String value) {
            addCriterion("current_season_id <>", value, "currentSeasonId");
            return (Criteria) this;
        }

        public Criteria andCurrentSeasonIdGreaterThan(String value) {
            addCriterion("current_season_id >", value, "currentSeasonId");
            return (Criteria) this;
        }

        public Criteria andCurrentSeasonIdGreaterThanOrEqualTo(String value) {
            addCriterion("current_season_id >=", value, "currentSeasonId");
            return (Criteria) this;
        }

        public Criteria andCurrentSeasonIdLessThan(String value) {
            addCriterion("current_season_id <", value, "currentSeasonId");
            return (Criteria) this;
        }

        public Criteria andCurrentSeasonIdLessThanOrEqualTo(String value) {
            addCriterion("current_season_id <=", value, "currentSeasonId");
            return (Criteria) this;
        }

        public Criteria andCurrentSeasonIdLike(String value) {
            addCriterion("current_season_id like", value, "currentSeasonId");
            return (Criteria) this;
        }

        public Criteria andCurrentSeasonIdNotLike(String value) {
            addCriterion("current_season_id not like", value, "currentSeasonId");
            return (Criteria) this;
        }

        public Criteria andCurrentSeasonIdIn(List<String> values) {
            addCriterion("current_season_id in", values, "currentSeasonId");
            return (Criteria) this;
        }

        public Criteria andCurrentSeasonIdNotIn(List<String> values) {
            addCriterion("current_season_id not in", values, "currentSeasonId");
            return (Criteria) this;
        }

        public Criteria andCurrentSeasonIdBetween(String value1, String value2) {
            addCriterion("current_season_id between", value1, value2, "currentSeasonId");
            return (Criteria) this;
        }

        public Criteria andCurrentSeasonIdNotBetween(String value1, String value2) {
            addCriterion("current_season_id not between", value1, value2, "currentSeasonId");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundTypeIsNull() {
            addCriterion("current_round_type is null");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundTypeIsNotNull() {
            addCriterion("current_round_type is not null");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundTypeEqualTo(String value) {
            addCriterion("current_round_type =", value, "currentRoundType");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundTypeNotEqualTo(String value) {
            addCriterion("current_round_type <>", value, "currentRoundType");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundTypeGreaterThan(String value) {
            addCriterion("current_round_type >", value, "currentRoundType");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundTypeGreaterThanOrEqualTo(String value) {
            addCriterion("current_round_type >=", value, "currentRoundType");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundTypeLessThan(String value) {
            addCriterion("current_round_type <", value, "currentRoundType");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundTypeLessThanOrEqualTo(String value) {
            addCriterion("current_round_type <=", value, "currentRoundType");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundTypeLike(String value) {
            addCriterion("current_round_type like", value, "currentRoundType");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundTypeNotLike(String value) {
            addCriterion("current_round_type not like", value, "currentRoundType");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundTypeIn(List<String> values) {
            addCriterion("current_round_type in", values, "currentRoundType");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundTypeNotIn(List<String> values) {
            addCriterion("current_round_type not in", values, "currentRoundType");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundTypeBetween(String value1, String value2) {
            addCriterion("current_round_type between", value1, value2, "currentRoundType");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundTypeNotBetween(String value1, String value2) {
            addCriterion("current_round_type not between", value1, value2, "currentRoundType");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNumberIsNull() {
            addCriterion("current_round_number is null");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNumberIsNotNull() {
            addCriterion("current_round_number is not null");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNumberEqualTo(Integer value) {
            addCriterion("current_round_number =", value, "currentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNumberNotEqualTo(Integer value) {
            addCriterion("current_round_number <>", value, "currentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNumberGreaterThan(Integer value) {
            addCriterion("current_round_number >", value, "currentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNumberGreaterThanOrEqualTo(Integer value) {
            addCriterion("current_round_number >=", value, "currentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNumberLessThan(Integer value) {
            addCriterion("current_round_number <", value, "currentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNumberLessThanOrEqualTo(Integer value) {
            addCriterion("current_round_number <=", value, "currentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNumberIn(List<Integer> values) {
            addCriterion("current_round_number in", values, "currentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNumberNotIn(List<Integer> values) {
            addCriterion("current_round_number not in", values, "currentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNumberBetween(Integer value1, Integer value2) {
            addCriterion("current_round_number between", value1, value2, "currentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNumberNotBetween(Integer value1, Integer value2) {
            addCriterion("current_round_number not between", value1, value2, "currentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNameIsNull() {
            addCriterion("current_round_name is null");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNameIsNotNull() {
            addCriterion("current_round_name is not null");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNameEqualTo(String value) {
            addCriterion("current_round_name =", value, "currentRoundName");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNameNotEqualTo(String value) {
            addCriterion("current_round_name <>", value, "currentRoundName");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNameGreaterThan(String value) {
            addCriterion("current_round_name >", value, "currentRoundName");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNameGreaterThanOrEqualTo(String value) {
            addCriterion("current_round_name >=", value, "currentRoundName");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNameLessThan(String value) {
            addCriterion("current_round_name <", value, "currentRoundName");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNameLessThanOrEqualTo(String value) {
            addCriterion("current_round_name <=", value, "currentRoundName");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNameLike(String value) {
            addCriterion("current_round_name like", value, "currentRoundName");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNameNotLike(String value) {
            addCriterion("current_round_name not like", value, "currentRoundName");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNameIn(List<String> values) {
            addCriterion("current_round_name in", values, "currentRoundName");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNameNotIn(List<String> values) {
            addCriterion("current_round_name not in", values, "currentRoundName");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNameBetween(String value1, String value2) {
            addCriterion("current_round_name between", value1, value2, "currentRoundName");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNameNotBetween(String value1, String value2) {
            addCriterion("current_round_name not between", value1, value2, "currentRoundName");
            return (Criteria) this;
        }

        public Criteria andHotStatusIsNull() {
            addCriterion("hot_status is null");
            return (Criteria) this;
        }

        public Criteria andHotStatusIsNotNull() {
            addCriterion("hot_status is not null");
            return (Criteria) this;
        }

        public Criteria andHotStatusEqualTo(Integer value) {
            addCriterion("hot_status =", value, "hotStatus");
            return (Criteria) this;
        }

        public Criteria andHotStatusNotEqualTo(Integer value) {
            addCriterion("hot_status <>", value, "hotStatus");
            return (Criteria) this;
        }

        public Criteria andHotStatusGreaterThan(Integer value) {
            addCriterion("hot_status >", value, "hotStatus");
            return (Criteria) this;
        }

        public Criteria andHotStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("hot_status >=", value, "hotStatus");
            return (Criteria) this;
        }

        public Criteria andHotStatusLessThan(Integer value) {
            addCriterion("hot_status <", value, "hotStatus");
            return (Criteria) this;
        }

        public Criteria andHotStatusLessThanOrEqualTo(Integer value) {
            addCriterion("hot_status <=", value, "hotStatus");
            return (Criteria) this;
        }

        public Criteria andHotStatusIn(List<Integer> values) {
            addCriterion("hot_status in", values, "hotStatus");
            return (Criteria) this;
        }

        public Criteria andHotStatusNotIn(List<Integer> values) {
            addCriterion("hot_status not in", values, "hotStatus");
            return (Criteria) this;
        }

        public Criteria andHotStatusBetween(Integer value1, Integer value2) {
            addCriterion("hot_status between", value1, value2, "hotStatus");
            return (Criteria) this;
        }

        public Criteria andHotStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("hot_status not between", value1, value2, "hotStatus");
            return (Criteria) this;
        }

        public Criteria andLogoUrlIsNull() {
            addCriterion("logo_url is null");
            return (Criteria) this;
        }

        public Criteria andLogoUrlIsNotNull() {
            addCriterion("logo_url is not null");
            return (Criteria) this;
        }

        public Criteria andLogoUrlEqualTo(String value) {
            addCriterion("logo_url =", value, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlNotEqualTo(String value) {
            addCriterion("logo_url <>", value, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlGreaterThan(String value) {
            addCriterion("logo_url >", value, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlGreaterThanOrEqualTo(String value) {
            addCriterion("logo_url >=", value, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlLessThan(String value) {
            addCriterion("logo_url <", value, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlLessThanOrEqualTo(String value) {
            addCriterion("logo_url <=", value, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlLike(String value) {
            addCriterion("logo_url like", value, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlNotLike(String value) {
            addCriterion("logo_url not like", value, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlIn(List<String> values) {
            addCriterion("logo_url in", values, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlNotIn(List<String> values) {
            addCriterion("logo_url not in", values, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlBetween(String value1, String value2) {
            addCriterion("logo_url between", value1, value2, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlNotBetween(String value1, String value2) {
            addCriterion("logo_url not between", value1, value2, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumbIsNull() {
            addCriterion("logo_url_thumb is null");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumbIsNotNull() {
            addCriterion("logo_url_thumb is not null");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumbEqualTo(String value) {
            addCriterion("logo_url_thumb =", value, "logoUrlThumb");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumbNotEqualTo(String value) {
            addCriterion("logo_url_thumb <>", value, "logoUrlThumb");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumbGreaterThan(String value) {
            addCriterion("logo_url_thumb >", value, "logoUrlThumb");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumbGreaterThanOrEqualTo(String value) {
            addCriterion("logo_url_thumb >=", value, "logoUrlThumb");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumbLessThan(String value) {
            addCriterion("logo_url_thumb <", value, "logoUrlThumb");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumbLessThanOrEqualTo(String value) {
            addCriterion("logo_url_thumb <=", value, "logoUrlThumb");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumbLike(String value) {
            addCriterion("logo_url_thumb like", value, "logoUrlThumb");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumbNotLike(String value) {
            addCriterion("logo_url_thumb not like", value, "logoUrlThumb");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumbIn(List<String> values) {
            addCriterion("logo_url_thumb in", values, "logoUrlThumb");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumbNotIn(List<String> values) {
            addCriterion("logo_url_thumb not in", values, "logoUrlThumb");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumbBetween(String value1, String value2) {
            addCriterion("logo_url_thumb between", value1, value2, "logoUrlThumb");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumbNotBetween(String value1, String value2) {
            addCriterion("logo_url_thumb not between", value1, value2, "logoUrlThumb");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderNumIsNull() {
            addCriterion("related_data_source_coder_num is null");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderNumIsNotNull() {
            addCriterion("related_data_source_coder_num is not null");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderNumEqualTo(Integer value) {
            addCriterion("related_data_source_coder_num =", value, "relatedDataSourceCoderNum");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderNumNotEqualTo(Integer value) {
            addCriterion("related_data_source_coder_num <>", value, "relatedDataSourceCoderNum");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderNumGreaterThan(Integer value) {
            addCriterion("related_data_source_coder_num >", value, "relatedDataSourceCoderNum");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderNumGreaterThanOrEqualTo(Integer value) {
            addCriterion("related_data_source_coder_num >=", value, "relatedDataSourceCoderNum");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderNumLessThan(Integer value) {
            addCriterion("related_data_source_coder_num <", value, "relatedDataSourceCoderNum");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderNumLessThanOrEqualTo(Integer value) {
            addCriterion("related_data_source_coder_num <=", value, "relatedDataSourceCoderNum");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderNumIn(List<Integer> values) {
            addCriterion("related_data_source_coder_num in", values, "relatedDataSourceCoderNum");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderNumNotIn(List<Integer> values) {
            addCriterion("related_data_source_coder_num not in", values, "relatedDataSourceCoderNum");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderNumBetween(Integer value1, Integer value2) {
            addCriterion("related_data_source_coder_num between", value1, value2, "relatedDataSourceCoderNum");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderNumNotBetween(Integer value1, Integer value2) {
            addCriterion("related_data_source_coder_num not between", value1, value2, "relatedDataSourceCoderNum");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderListIsNull() {
            addCriterion("related_data_source_coder_list is null");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderListIsNotNull() {
            addCriterion("related_data_source_coder_list is not null");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderListEqualTo(String value) {
            addCriterion("related_data_source_coder_list =", value, "relatedDataSourceCoderList");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderListNotEqualTo(String value) {
            addCriterion("related_data_source_coder_list <>", value, "relatedDataSourceCoderList");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderListGreaterThan(String value) {
            addCriterion("related_data_source_coder_list >", value, "relatedDataSourceCoderList");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderListGreaterThanOrEqualTo(String value) {
            addCriterion("related_data_source_coder_list >=", value, "relatedDataSourceCoderList");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderListLessThan(String value) {
            addCriterion("related_data_source_coder_list <", value, "relatedDataSourceCoderList");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderListLessThanOrEqualTo(String value) {
            addCriterion("related_data_source_coder_list <=", value, "relatedDataSourceCoderList");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderListLike(String value) {
            addCriterion("related_data_source_coder_list like", value, "relatedDataSourceCoderList");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderListNotLike(String value) {
            addCriterion("related_data_source_coder_list not like", value, "relatedDataSourceCoderList");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderListIn(List<String> values) {
            addCriterion("related_data_source_coder_list in", values, "relatedDataSourceCoderList");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderListNotIn(List<String> values) {
            addCriterion("related_data_source_coder_list not in", values, "relatedDataSourceCoderList");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderListBetween(String value1, String value2) {
            addCriterion("related_data_source_coder_list between", value1, value2, "relatedDataSourceCoderList");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderListNotBetween(String value1, String value2) {
            addCriterion("related_data_source_coder_list not between", value1, value2, "relatedDataSourceCoderList");
            return (Criteria) this;
        }

        public Criteria andIntroductionIsNull() {
            addCriterion("introduction is null");
            return (Criteria) this;
        }

        public Criteria andIntroductionIsNotNull() {
            addCriterion("introduction is not null");
            return (Criteria) this;
        }

        public Criteria andIntroductionEqualTo(String value) {
            addCriterion("introduction =", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionNotEqualTo(String value) {
            addCriterion("introduction <>", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionGreaterThan(String value) {
            addCriterion("introduction >", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionGreaterThanOrEqualTo(String value) {
            addCriterion("introduction >=", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionLessThan(String value) {
            addCriterion("introduction <", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionLessThanOrEqualTo(String value) {
            addCriterion("introduction <=", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionLike(String value) {
            addCriterion("introduction like", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionNotLike(String value) {
            addCriterion("introduction not like", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionIn(List<String> values) {
            addCriterion("introduction in", values, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionNotIn(List<String> values) {
            addCriterion("introduction not in", values, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionBetween(String value1, String value2) {
            addCriterion("introduction between", value1, value2, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionNotBetween(String value1, String value2) {
            addCriterion("introduction not between", value1, value2, "introduction");
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

        public Criteria andIsLockIsNull() {
            addCriterion("is_lock is null");
            return (Criteria) this;
        }

        public Criteria andIsLockIsNotNull() {
            addCriterion("is_lock is not null");
            return (Criteria) this;
        }

        public Criteria andIsLockEqualTo(Integer value) {
            addCriterion("is_lock =", value, "isLock");
            return (Criteria) this;
        }

        public Criteria andIsLockNotEqualTo(Integer value) {
            addCriterion("is_lock <>", value, "isLock");
            return (Criteria) this;
        }

        public Criteria andIsLockGreaterThan(Integer value) {
            addCriterion("is_lock >", value, "isLock");
            return (Criteria) this;
        }

        public Criteria andIsLockGreaterThanOrEqualTo(Integer value) {
            addCriterion("is_lock >=", value, "isLock");
            return (Criteria) this;
        }

        public Criteria andIsLockLessThan(Integer value) {
            addCriterion("is_lock <", value, "isLock");
            return (Criteria) this;
        }

        public Criteria andIsLockLessThanOrEqualTo(Integer value) {
            addCriterion("is_lock <=", value, "isLock");
            return (Criteria) this;
        }

        public Criteria andIsLockIn(List<Integer> values) {
            addCriterion("is_lock in", values, "isLock");
            return (Criteria) this;
        }

        public Criteria andIsLockNotIn(List<Integer> values) {
            addCriterion("is_lock not in", values, "isLock");
            return (Criteria) this;
        }

        public Criteria andIsLockBetween(Integer value1, Integer value2) {
            addCriterion("is_lock between", value1, value2, "isLock");
            return (Criteria) this;
        }

        public Criteria andIsLockNotBetween(Integer value1, Integer value2) {
            addCriterion("is_lock not between", value1, value2, "isLock");
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

        public Criteria andOperatorStatusIsNull() {
            addCriterion("operator_status is null");
            return (Criteria) this;
        }

        public Criteria andOperatorStatusIsNotNull() {
            addCriterion("operator_status is not null");
            return (Criteria) this;
        }

        public Criteria andOperatorStatusEqualTo(Integer value) {
            addCriterion("operator_status =", value, "operatorStatus");
            return (Criteria) this;
        }

        public Criteria andOperatorStatusNotEqualTo(Integer value) {
            addCriterion("operator_status <>", value, "operatorStatus");
            return (Criteria) this;
        }

        public Criteria andOperatorStatusGreaterThan(Integer value) {
            addCriterion("operator_status >", value, "operatorStatus");
            return (Criteria) this;
        }

        public Criteria andOperatorStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("operator_status >=", value, "operatorStatus");
            return (Criteria) this;
        }

        public Criteria andOperatorStatusLessThan(Integer value) {
            addCriterion("operator_status <", value, "operatorStatus");
            return (Criteria) this;
        }

        public Criteria andOperatorStatusLessThanOrEqualTo(Integer value) {
            addCriterion("operator_status <=", value, "operatorStatus");
            return (Criteria) this;
        }

        public Criteria andOperatorStatusIn(List<Integer> values) {
            addCriterion("operator_status in", values, "operatorStatus");
            return (Criteria) this;
        }

        public Criteria andOperatorStatusNotIn(List<Integer> values) {
            addCriterion("operator_status not in", values, "operatorStatus");
            return (Criteria) this;
        }

        public Criteria andOperatorStatusBetween(Integer value1, Integer value2) {
            addCriterion("operator_status between", value1, value2, "operatorStatus");
            return (Criteria) this;
        }

        public Criteria andOperatorStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("operator_status not between", value1, value2, "operatorStatus");
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

        public Criteria andNameSpellIsNull() {
            addCriterion("name_spell is null");
            return (Criteria) this;
        }

        public Criteria andNameSpellIsNotNull() {
            addCriterion("name_spell is not null");
            return (Criteria) this;
        }

        public Criteria andNameSpellEqualTo(String value) {
            addCriterion("name_spell =", value, "nameSpell");
            return (Criteria) this;
        }

        public Criteria andNameSpellNotEqualTo(String value) {
            addCriterion("name_spell <>", value, "nameSpell");
            return (Criteria) this;
        }

        public Criteria andNameSpellGreaterThan(String value) {
            addCriterion("name_spell >", value, "nameSpell");
            return (Criteria) this;
        }

        public Criteria andNameSpellGreaterThanOrEqualTo(String value) {
            addCriterion("name_spell >=", value, "nameSpell");
            return (Criteria) this;
        }

        public Criteria andNameSpellLessThan(String value) {
            addCriterion("name_spell <", value, "nameSpell");
            return (Criteria) this;
        }

        public Criteria andNameSpellLessThanOrEqualTo(String value) {
            addCriterion("name_spell <=", value, "nameSpell");
            return (Criteria) this;
        }

        public Criteria andNameSpellLike(String value) {
            addCriterion("name_spell like", value, "nameSpell");
            return (Criteria) this;
        }

        public Criteria andNameSpellNotLike(String value) {
            addCriterion("name_spell not like", value, "nameSpell");
            return (Criteria) this;
        }

        public Criteria andNameSpellIn(List<String> values) {
            addCriterion("name_spell in", values, "nameSpell");
            return (Criteria) this;
        }

        public Criteria andNameSpellNotIn(List<String> values) {
            addCriterion("name_spell not in", values, "nameSpell");
            return (Criteria) this;
        }

        public Criteria andNameSpellBetween(String value1, String value2) {
            addCriterion("name_spell between", value1, value2, "nameSpell");
            return (Criteria) this;
        }

        public Criteria andNameSpellNotBetween(String value1, String value2) {
            addCriterion("name_spell not between", value1, value2, "nameSpell");
            return (Criteria) this;
        }

        public Criteria andTournamentTypeIsNull() {
            addCriterion("tournament_type is null");
            return (Criteria) this;
        }

        public Criteria andTournamentTypeIsNotNull() {
            addCriterion("tournament_type is not null");
            return (Criteria) this;
        }

        public Criteria andTournamentTypeEqualTo(String value) {
            addCriterion("tournament_type =", value, "tournamentType");
            return (Criteria) this;
        }

        public Criteria andTournamentTypeNotEqualTo(String value) {
            addCriterion("tournament_type <>", value, "tournamentType");
            return (Criteria) this;
        }

        public Criteria andTournamentTypeGreaterThan(String value) {
            addCriterion("tournament_type >", value, "tournamentType");
            return (Criteria) this;
        }

        public Criteria andTournamentTypeGreaterThanOrEqualTo(String value) {
            addCriterion("tournament_type >=", value, "tournamentType");
            return (Criteria) this;
        }

        public Criteria andTournamentTypeLessThan(String value) {
            addCriterion("tournament_type <", value, "tournamentType");
            return (Criteria) this;
        }

        public Criteria andTournamentTypeLessThanOrEqualTo(String value) {
            addCriterion("tournament_type <=", value, "tournamentType");
            return (Criteria) this;
        }

        public Criteria andTournamentTypeLike(String value) {
            addCriterion("tournament_type like", value, "tournamentType");
            return (Criteria) this;
        }

        public Criteria andTournamentTypeNotLike(String value) {
            addCriterion("tournament_type not like", value, "tournamentType");
            return (Criteria) this;
        }

        public Criteria andTournamentTypeIn(List<String> values) {
            addCriterion("tournament_type in", values, "tournamentType");
            return (Criteria) this;
        }

        public Criteria andTournamentTypeNotIn(List<String> values) {
            addCriterion("tournament_type not in", values, "tournamentType");
            return (Criteria) this;
        }

        public Criteria andTournamentTypeBetween(String value1, String value2) {
            addCriterion("tournament_type between", value1, value2, "tournamentType");
            return (Criteria) this;
        }

        public Criteria andTournamentTypeNotBetween(String value1, String value2) {
            addCriterion("tournament_type not between", value1, value2, "tournamentType");
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

        public Criteria andLeagueUrlIsNull() {
            addCriterion("league_url is null");
            return (Criteria) this;
        }

        public Criteria andLeagueUrlIsNotNull() {
            addCriterion("league_url is not null");
            return (Criteria) this;
        }

        public Criteria andLeagueUrlEqualTo(String value) {
            addCriterion("league_url =", value, "leagueUrl");
            return (Criteria) this;
        }

        public Criteria andLeagueUrlNotEqualTo(String value) {
            addCriterion("league_url <>", value, "leagueUrl");
            return (Criteria) this;
        }

        public Criteria andLeagueUrlGreaterThan(String value) {
            addCriterion("league_url >", value, "leagueUrl");
            return (Criteria) this;
        }

        public Criteria andLeagueUrlGreaterThanOrEqualTo(String value) {
            addCriterion("league_url >=", value, "leagueUrl");
            return (Criteria) this;
        }

        public Criteria andLeagueUrlLessThan(String value) {
            addCriterion("league_url <", value, "leagueUrl");
            return (Criteria) this;
        }

        public Criteria andLeagueUrlLessThanOrEqualTo(String value) {
            addCriterion("league_url <=", value, "leagueUrl");
            return (Criteria) this;
        }

        public Criteria andLeagueUrlLike(String value) {
            addCriterion("league_url like", value, "leagueUrl");
            return (Criteria) this;
        }

        public Criteria andLeagueUrlNotLike(String value) {
            addCriterion("league_url not like", value, "leagueUrl");
            return (Criteria) this;
        }

        public Criteria andLeagueUrlIn(List<String> values) {
            addCriterion("league_url in", values, "leagueUrl");
            return (Criteria) this;
        }

        public Criteria andLeagueUrlNotIn(List<String> values) {
            addCriterion("league_url not in", values, "leagueUrl");
            return (Criteria) this;
        }

        public Criteria andLeagueUrlBetween(String value1, String value2) {
            addCriterion("league_url between", value1, value2, "leagueUrl");
            return (Criteria) this;
        }

        public Criteria andLeagueUrlNotBetween(String value1, String value2) {
            addCriterion("league_url not between", value1, value2, "leagueUrl");
            return (Criteria) this;
        }

        public Criteria andHasRelationIsNull() {
            addCriterion("has_relation is null");
            return (Criteria) this;
        }

        public Criteria andHasRelationIsNotNull() {
            addCriterion("has_relation is not null");
            return (Criteria) this;
        }

        public Criteria andHasRelationEqualTo(Integer value) {
            addCriterion("has_relation =", value, "hasRelation");
            return (Criteria) this;
        }

        public Criteria andHasRelationNotEqualTo(Integer value) {
            addCriterion("has_relation <>", value, "hasRelation");
            return (Criteria) this;
        }

        public Criteria andHasRelationGreaterThan(Integer value) {
            addCriterion("has_relation >", value, "hasRelation");
            return (Criteria) this;
        }

        public Criteria andHasRelationGreaterThanOrEqualTo(Integer value) {
            addCriterion("has_relation >=", value, "hasRelation");
            return (Criteria) this;
        }

        public Criteria andHasRelationLessThan(Integer value) {
            addCriterion("has_relation <", value, "hasRelation");
            return (Criteria) this;
        }

        public Criteria andHasRelationLessThanOrEqualTo(Integer value) {
            addCriterion("has_relation <=", value, "hasRelation");
            return (Criteria) this;
        }

        public Criteria andHasRelationIn(List<Integer> values) {
            addCriterion("has_relation in", values, "hasRelation");
            return (Criteria) this;
        }

        public Criteria andHasRelationNotIn(List<Integer> values) {
            addCriterion("has_relation not in", values, "hasRelation");
            return (Criteria) this;
        }

        public Criteria andHasRelationBetween(Integer value1, Integer value2) {
            addCriterion("has_relation between", value1, value2, "hasRelation");
            return (Criteria) this;
        }

        public Criteria andHasRelationNotBetween(Integer value1, Integer value2) {
            addCriterion("has_relation not between", value1, value2, "hasRelation");
            return (Criteria) this;
        }

        public Criteria andInfluenceStatusIsNull() {
            addCriterion("influence_status is null");
            return (Criteria) this;
        }

        public Criteria andInfluenceStatusIsNotNull() {
            addCriterion("influence_status is not null");
            return (Criteria) this;
        }

        public Criteria andInfluenceStatusEqualTo(Integer value) {
            addCriterion("influence_status =", value, "influenceStatus");
            return (Criteria) this;
        }

        public Criteria andInfluenceStatusNotEqualTo(Integer value) {
            addCriterion("influence_status <>", value, "influenceStatus");
            return (Criteria) this;
        }

        public Criteria andInfluenceStatusGreaterThan(Integer value) {
            addCriterion("influence_status >", value, "influenceStatus");
            return (Criteria) this;
        }

        public Criteria andInfluenceStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("influence_status >=", value, "influenceStatus");
            return (Criteria) this;
        }

        public Criteria andInfluenceStatusLessThan(Integer value) {
            addCriterion("influence_status <", value, "influenceStatus");
            return (Criteria) this;
        }

        public Criteria andInfluenceStatusLessThanOrEqualTo(Integer value) {
            addCriterion("influence_status <=", value, "influenceStatus");
            return (Criteria) this;
        }

        public Criteria andInfluenceStatusIn(List<Integer> values) {
            addCriterion("influence_status in", values, "influenceStatus");
            return (Criteria) this;
        }

        public Criteria andInfluenceStatusNotIn(List<Integer> values) {
            addCriterion("influence_status not in", values, "influenceStatus");
            return (Criteria) this;
        }

        public Criteria andInfluenceStatusBetween(Integer value1, Integer value2) {
            addCriterion("influence_status between", value1, value2, "influenceStatus");
            return (Criteria) this;
        }

        public Criteria andInfluenceStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("influence_status not between", value1, value2, "influenceStatus");
            return (Criteria) this;
        }

        public Criteria andNameZhIsNull() {
            addCriterion("name_zh is null");
            return (Criteria) this;
        }

        public Criteria andNameZhIsNotNull() {
            addCriterion("name_zh is not null");
            return (Criteria) this;
        }

        public Criteria andNameZhEqualTo(String value) {
            addCriterion("name_zh =", value, "nameZh");
            return (Criteria) this;
        }

        public Criteria andNameZhNotEqualTo(String value) {
            addCriterion("name_zh <>", value, "nameZh");
            return (Criteria) this;
        }

        public Criteria andNameZhGreaterThan(String value) {
            addCriterion("name_zh >", value, "nameZh");
            return (Criteria) this;
        }

        public Criteria andNameZhGreaterThanOrEqualTo(String value) {
            addCriterion("name_zh >=", value, "nameZh");
            return (Criteria) this;
        }

        public Criteria andNameZhLessThan(String value) {
            addCriterion("name_zh <", value, "nameZh");
            return (Criteria) this;
        }

        public Criteria andNameZhLessThanOrEqualTo(String value) {
            addCriterion("name_zh <=", value, "nameZh");
            return (Criteria) this;
        }

        public Criteria andNameZhLike(String value) {
            addCriterion("name_zh like", value, "nameZh");
            return (Criteria) this;
        }

        public Criteria andNameZhNotLike(String value) {
            addCriterion("name_zh not like", value, "nameZh");
            return (Criteria) this;
        }

        public Criteria andNameZhIn(List<String> values) {
            addCriterion("name_zh in", values, "nameZh");
            return (Criteria) this;
        }

        public Criteria andNameZhNotIn(List<String> values) {
            addCriterion("name_zh not in", values, "nameZh");
            return (Criteria) this;
        }

        public Criteria andNameZhBetween(String value1, String value2) {
            addCriterion("name_zh between", value1, value2, "nameZh");
            return (Criteria) this;
        }

        public Criteria andNameZhNotBetween(String value1, String value2) {
            addCriterion("name_zh not between", value1, value2, "nameZh");
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