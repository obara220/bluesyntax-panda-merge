package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class ThirdMatchHistoryStatisticsExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ThirdMatchHistoryStatisticsExample() {
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

        public Criteria andThirdSeasonSourceIdIsNull() {
            addCriterion("third_season_source_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdSeasonSourceIdIsNotNull() {
            addCriterion("third_season_source_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdSeasonSourceIdEqualTo(String value) {
            addCriterion("third_season_source_id =", value, "thirdSeasonSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdSeasonSourceIdNotEqualTo(String value) {
            addCriterion("third_season_source_id <>", value, "thirdSeasonSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdSeasonSourceIdGreaterThan(String value) {
            addCriterion("third_season_source_id >", value, "thirdSeasonSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdSeasonSourceIdGreaterThanOrEqualTo(String value) {
            addCriterion("third_season_source_id >=", value, "thirdSeasonSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdSeasonSourceIdLessThan(String value) {
            addCriterion("third_season_source_id <", value, "thirdSeasonSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdSeasonSourceIdLessThanOrEqualTo(String value) {
            addCriterion("third_season_source_id <=", value, "thirdSeasonSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdSeasonSourceIdLike(String value) {
            addCriterion("third_season_source_id like", value, "thirdSeasonSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdSeasonSourceIdNotLike(String value) {
            addCriterion("third_season_source_id not like", value, "thirdSeasonSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdSeasonSourceIdIn(List<String> values) {
            addCriterion("third_season_source_id in", values, "thirdSeasonSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdSeasonSourceIdNotIn(List<String> values) {
            addCriterion("third_season_source_id not in", values, "thirdSeasonSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdSeasonSourceIdBetween(String value1, String value2) {
            addCriterion("third_season_source_id between", value1, value2, "thirdSeasonSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdSeasonSourceIdNotBetween(String value1, String value2) {
            addCriterion("third_season_source_id not between", value1, value2, "thirdSeasonSourceId");
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

        public Criteria andBeginTimeIsNull() {
            addCriterion("begin_time is null");
            return (Criteria) this;
        }

        public Criteria andBeginTimeIsNotNull() {
            addCriterion("begin_time is not null");
            return (Criteria) this;
        }

        public Criteria andBeginTimeEqualTo(Long value) {
            addCriterion("begin_time =", value, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeNotEqualTo(Long value) {
            addCriterion("begin_time <>", value, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeGreaterThan(Long value) {
            addCriterion("begin_time >", value, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("begin_time >=", value, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeLessThan(Long value) {
            addCriterion("begin_time <", value, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeLessThanOrEqualTo(Long value) {
            addCriterion("begin_time <=", value, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeIn(List<Long> values) {
            addCriterion("begin_time in", values, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeNotIn(List<Long> values) {
            addCriterion("begin_time not in", values, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeBetween(Long value1, Long value2) {
            addCriterion("begin_time between", value1, value2, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeNotBetween(Long value1, Long value2) {
            addCriterion("begin_time not between", value1, value2, "beginTime");
            return (Criteria) this;
        }

        public Criteria andMatchStatusIsNull() {
            addCriterion("match_status is null");
            return (Criteria) this;
        }

        public Criteria andMatchStatusIsNotNull() {
            addCriterion("match_status is not null");
            return (Criteria) this;
        }

        public Criteria andMatchStatusEqualTo(String value) {
            addCriterion("match_status =", value, "matchStatus");
            return (Criteria) this;
        }

        public Criteria andMatchStatusNotEqualTo(String value) {
            addCriterion("match_status <>", value, "matchStatus");
            return (Criteria) this;
        }

        public Criteria andMatchStatusGreaterThan(String value) {
            addCriterion("match_status >", value, "matchStatus");
            return (Criteria) this;
        }

        public Criteria andMatchStatusGreaterThanOrEqualTo(String value) {
            addCriterion("match_status >=", value, "matchStatus");
            return (Criteria) this;
        }

        public Criteria andMatchStatusLessThan(String value) {
            addCriterion("match_status <", value, "matchStatus");
            return (Criteria) this;
        }

        public Criteria andMatchStatusLessThanOrEqualTo(String value) {
            addCriterion("match_status <=", value, "matchStatus");
            return (Criteria) this;
        }

        public Criteria andMatchStatusLike(String value) {
            addCriterion("match_status like", value, "matchStatus");
            return (Criteria) this;
        }

        public Criteria andMatchStatusNotLike(String value) {
            addCriterion("match_status not like", value, "matchStatus");
            return (Criteria) this;
        }

        public Criteria andMatchStatusIn(List<String> values) {
            addCriterion("match_status in", values, "matchStatus");
            return (Criteria) this;
        }

        public Criteria andMatchStatusNotIn(List<String> values) {
            addCriterion("match_status not in", values, "matchStatus");
            return (Criteria) this;
        }

        public Criteria andMatchStatusBetween(String value1, String value2) {
            addCriterion("match_status between", value1, value2, "matchStatus");
            return (Criteria) this;
        }

        public Criteria andMatchStatusNotBetween(String value1, String value2) {
            addCriterion("match_status not between", value1, value2, "matchStatus");
            return (Criteria) this;
        }

        public Criteria andHomeTeamIdIsNull() {
            addCriterion("home_team_id is null");
            return (Criteria) this;
        }

        public Criteria andHomeTeamIdIsNotNull() {
            addCriterion("home_team_id is not null");
            return (Criteria) this;
        }

        public Criteria andHomeTeamIdEqualTo(String value) {
            addCriterion("home_team_id =", value, "homeTeamId");
            return (Criteria) this;
        }

        public Criteria andHomeTeamIdNotEqualTo(String value) {
            addCriterion("home_team_id <>", value, "homeTeamId");
            return (Criteria) this;
        }

        public Criteria andHomeTeamIdGreaterThan(String value) {
            addCriterion("home_team_id >", value, "homeTeamId");
            return (Criteria) this;
        }

        public Criteria andHomeTeamIdGreaterThanOrEqualTo(String value) {
            addCriterion("home_team_id >=", value, "homeTeamId");
            return (Criteria) this;
        }

        public Criteria andHomeTeamIdLessThan(String value) {
            addCriterion("home_team_id <", value, "homeTeamId");
            return (Criteria) this;
        }

        public Criteria andHomeTeamIdLessThanOrEqualTo(String value) {
            addCriterion("home_team_id <=", value, "homeTeamId");
            return (Criteria) this;
        }

        public Criteria andHomeTeamIdLike(String value) {
            addCriterion("home_team_id like", value, "homeTeamId");
            return (Criteria) this;
        }

        public Criteria andHomeTeamIdNotLike(String value) {
            addCriterion("home_team_id not like", value, "homeTeamId");
            return (Criteria) this;
        }

        public Criteria andHomeTeamIdIn(List<String> values) {
            addCriterion("home_team_id in", values, "homeTeamId");
            return (Criteria) this;
        }

        public Criteria andHomeTeamIdNotIn(List<String> values) {
            addCriterion("home_team_id not in", values, "homeTeamId");
            return (Criteria) this;
        }

        public Criteria andHomeTeamIdBetween(String value1, String value2) {
            addCriterion("home_team_id between", value1, value2, "homeTeamId");
            return (Criteria) this;
        }

        public Criteria andHomeTeamIdNotBetween(String value1, String value2) {
            addCriterion("home_team_id not between", value1, value2, "homeTeamId");
            return (Criteria) this;
        }

        public Criteria andAwayTeamIdIsNull() {
            addCriterion("away_team_id is null");
            return (Criteria) this;
        }

        public Criteria andAwayTeamIdIsNotNull() {
            addCriterion("away_team_id is not null");
            return (Criteria) this;
        }

        public Criteria andAwayTeamIdEqualTo(String value) {
            addCriterion("away_team_id =", value, "awayTeamId");
            return (Criteria) this;
        }

        public Criteria andAwayTeamIdNotEqualTo(String value) {
            addCriterion("away_team_id <>", value, "awayTeamId");
            return (Criteria) this;
        }

        public Criteria andAwayTeamIdGreaterThan(String value) {
            addCriterion("away_team_id >", value, "awayTeamId");
            return (Criteria) this;
        }

        public Criteria andAwayTeamIdGreaterThanOrEqualTo(String value) {
            addCriterion("away_team_id >=", value, "awayTeamId");
            return (Criteria) this;
        }

        public Criteria andAwayTeamIdLessThan(String value) {
            addCriterion("away_team_id <", value, "awayTeamId");
            return (Criteria) this;
        }

        public Criteria andAwayTeamIdLessThanOrEqualTo(String value) {
            addCriterion("away_team_id <=", value, "awayTeamId");
            return (Criteria) this;
        }

        public Criteria andAwayTeamIdLike(String value) {
            addCriterion("away_team_id like", value, "awayTeamId");
            return (Criteria) this;
        }

        public Criteria andAwayTeamIdNotLike(String value) {
            addCriterion("away_team_id not like", value, "awayTeamId");
            return (Criteria) this;
        }

        public Criteria andAwayTeamIdIn(List<String> values) {
            addCriterion("away_team_id in", values, "awayTeamId");
            return (Criteria) this;
        }

        public Criteria andAwayTeamIdNotIn(List<String> values) {
            addCriterion("away_team_id not in", values, "awayTeamId");
            return (Criteria) this;
        }

        public Criteria andAwayTeamIdBetween(String value1, String value2) {
            addCriterion("away_team_id between", value1, value2, "awayTeamId");
            return (Criteria) this;
        }

        public Criteria andAwayTeamIdNotBetween(String value1, String value2) {
            addCriterion("away_team_id not between", value1, value2, "awayTeamId");
            return (Criteria) this;
        }

        public Criteria andHomeTeamNameIsNull() {
            addCriterion("home_team_name is null");
            return (Criteria) this;
        }

        public Criteria andHomeTeamNameIsNotNull() {
            addCriterion("home_team_name is not null");
            return (Criteria) this;
        }

        public Criteria andHomeTeamNameEqualTo(String value) {
            addCriterion("home_team_name =", value, "homeTeamName");
            return (Criteria) this;
        }

        public Criteria andHomeTeamNameNotEqualTo(String value) {
            addCriterion("home_team_name <>", value, "homeTeamName");
            return (Criteria) this;
        }

        public Criteria andHomeTeamNameGreaterThan(String value) {
            addCriterion("home_team_name >", value, "homeTeamName");
            return (Criteria) this;
        }

        public Criteria andHomeTeamNameGreaterThanOrEqualTo(String value) {
            addCriterion("home_team_name >=", value, "homeTeamName");
            return (Criteria) this;
        }

        public Criteria andHomeTeamNameLessThan(String value) {
            addCriterion("home_team_name <", value, "homeTeamName");
            return (Criteria) this;
        }

        public Criteria andHomeTeamNameLessThanOrEqualTo(String value) {
            addCriterion("home_team_name <=", value, "homeTeamName");
            return (Criteria) this;
        }

        public Criteria andHomeTeamNameLike(String value) {
            addCriterion("home_team_name like", value, "homeTeamName");
            return (Criteria) this;
        }

        public Criteria andHomeTeamNameNotLike(String value) {
            addCriterion("home_team_name not like", value, "homeTeamName");
            return (Criteria) this;
        }

        public Criteria andHomeTeamNameIn(List<String> values) {
            addCriterion("home_team_name in", values, "homeTeamName");
            return (Criteria) this;
        }

        public Criteria andHomeTeamNameNotIn(List<String> values) {
            addCriterion("home_team_name not in", values, "homeTeamName");
            return (Criteria) this;
        }

        public Criteria andHomeTeamNameBetween(String value1, String value2) {
            addCriterion("home_team_name between", value1, value2, "homeTeamName");
            return (Criteria) this;
        }

        public Criteria andHomeTeamNameNotBetween(String value1, String value2) {
            addCriterion("home_team_name not between", value1, value2, "homeTeamName");
            return (Criteria) this;
        }

        public Criteria andAwayTeamNameIsNull() {
            addCriterion("away_team_name is null");
            return (Criteria) this;
        }

        public Criteria andAwayTeamNameIsNotNull() {
            addCriterion("away_team_name is not null");
            return (Criteria) this;
        }

        public Criteria andAwayTeamNameEqualTo(String value) {
            addCriterion("away_team_name =", value, "awayTeamName");
            return (Criteria) this;
        }

        public Criteria andAwayTeamNameNotEqualTo(String value) {
            addCriterion("away_team_name <>", value, "awayTeamName");
            return (Criteria) this;
        }

        public Criteria andAwayTeamNameGreaterThan(String value) {
            addCriterion("away_team_name >", value, "awayTeamName");
            return (Criteria) this;
        }

        public Criteria andAwayTeamNameGreaterThanOrEqualTo(String value) {
            addCriterion("away_team_name >=", value, "awayTeamName");
            return (Criteria) this;
        }

        public Criteria andAwayTeamNameLessThan(String value) {
            addCriterion("away_team_name <", value, "awayTeamName");
            return (Criteria) this;
        }

        public Criteria andAwayTeamNameLessThanOrEqualTo(String value) {
            addCriterion("away_team_name <=", value, "awayTeamName");
            return (Criteria) this;
        }

        public Criteria andAwayTeamNameLike(String value) {
            addCriterion("away_team_name like", value, "awayTeamName");
            return (Criteria) this;
        }

        public Criteria andAwayTeamNameNotLike(String value) {
            addCriterion("away_team_name not like", value, "awayTeamName");
            return (Criteria) this;
        }

        public Criteria andAwayTeamNameIn(List<String> values) {
            addCriterion("away_team_name in", values, "awayTeamName");
            return (Criteria) this;
        }

        public Criteria andAwayTeamNameNotIn(List<String> values) {
            addCriterion("away_team_name not in", values, "awayTeamName");
            return (Criteria) this;
        }

        public Criteria andAwayTeamNameBetween(String value1, String value2) {
            addCriterion("away_team_name between", value1, value2, "awayTeamName");
            return (Criteria) this;
        }

        public Criteria andAwayTeamNameNotBetween(String value1, String value2) {
            addCriterion("away_team_name not between", value1, value2, "awayTeamName");
            return (Criteria) this;
        }

        public Criteria andHomeTeamScoreIsNull() {
            addCriterion("home_team_score is null");
            return (Criteria) this;
        }

        public Criteria andHomeTeamScoreIsNotNull() {
            addCriterion("home_team_score is not null");
            return (Criteria) this;
        }

        public Criteria andHomeTeamScoreEqualTo(String value) {
            addCriterion("home_team_score =", value, "homeTeamScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamScoreNotEqualTo(String value) {
            addCriterion("home_team_score <>", value, "homeTeamScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamScoreGreaterThan(String value) {
            addCriterion("home_team_score >", value, "homeTeamScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamScoreGreaterThanOrEqualTo(String value) {
            addCriterion("home_team_score >=", value, "homeTeamScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamScoreLessThan(String value) {
            addCriterion("home_team_score <", value, "homeTeamScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamScoreLessThanOrEqualTo(String value) {
            addCriterion("home_team_score <=", value, "homeTeamScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamScoreLike(String value) {
            addCriterion("home_team_score like", value, "homeTeamScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamScoreNotLike(String value) {
            addCriterion("home_team_score not like", value, "homeTeamScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamScoreIn(List<String> values) {
            addCriterion("home_team_score in", values, "homeTeamScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamScoreNotIn(List<String> values) {
            addCriterion("home_team_score not in", values, "homeTeamScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamScoreBetween(String value1, String value2) {
            addCriterion("home_team_score between", value1, value2, "homeTeamScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamScoreNotBetween(String value1, String value2) {
            addCriterion("home_team_score not between", value1, value2, "homeTeamScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamScoreIsNull() {
            addCriterion("away_team_score is null");
            return (Criteria) this;
        }

        public Criteria andAwayTeamScoreIsNotNull() {
            addCriterion("away_team_score is not null");
            return (Criteria) this;
        }

        public Criteria andAwayTeamScoreEqualTo(String value) {
            addCriterion("away_team_score =", value, "awayTeamScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamScoreNotEqualTo(String value) {
            addCriterion("away_team_score <>", value, "awayTeamScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamScoreGreaterThan(String value) {
            addCriterion("away_team_score >", value, "awayTeamScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamScoreGreaterThanOrEqualTo(String value) {
            addCriterion("away_team_score >=", value, "awayTeamScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamScoreLessThan(String value) {
            addCriterion("away_team_score <", value, "awayTeamScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamScoreLessThanOrEqualTo(String value) {
            addCriterion("away_team_score <=", value, "awayTeamScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamScoreLike(String value) {
            addCriterion("away_team_score like", value, "awayTeamScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamScoreNotLike(String value) {
            addCriterion("away_team_score not like", value, "awayTeamScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamScoreIn(List<String> values) {
            addCriterion("away_team_score in", values, "awayTeamScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamScoreNotIn(List<String> values) {
            addCriterion("away_team_score not in", values, "awayTeamScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamScoreBetween(String value1, String value2) {
            addCriterion("away_team_score between", value1, value2, "awayTeamScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamScoreNotBetween(String value1, String value2) {
            addCriterion("away_team_score not between", value1, value2, "awayTeamScore");
            return (Criteria) this;
        }

        public Criteria andHandicapValIsNull() {
            addCriterion("handicap_val is null");
            return (Criteria) this;
        }

        public Criteria andHandicapValIsNotNull() {
            addCriterion("handicap_val is not null");
            return (Criteria) this;
        }

        public Criteria andHandicapValEqualTo(String value) {
            addCriterion("handicap_val =", value, "handicapVal");
            return (Criteria) this;
        }

        public Criteria andHandicapValNotEqualTo(String value) {
            addCriterion("handicap_val <>", value, "handicapVal");
            return (Criteria) this;
        }

        public Criteria andHandicapValGreaterThan(String value) {
            addCriterion("handicap_val >", value, "handicapVal");
            return (Criteria) this;
        }

        public Criteria andHandicapValGreaterThanOrEqualTo(String value) {
            addCriterion("handicap_val >=", value, "handicapVal");
            return (Criteria) this;
        }

        public Criteria andHandicapValLessThan(String value) {
            addCriterion("handicap_val <", value, "handicapVal");
            return (Criteria) this;
        }

        public Criteria andHandicapValLessThanOrEqualTo(String value) {
            addCriterion("handicap_val <=", value, "handicapVal");
            return (Criteria) this;
        }

        public Criteria andHandicapValLike(String value) {
            addCriterion("handicap_val like", value, "handicapVal");
            return (Criteria) this;
        }

        public Criteria andHandicapValNotLike(String value) {
            addCriterion("handicap_val not like", value, "handicapVal");
            return (Criteria) this;
        }

        public Criteria andHandicapValIn(List<String> values) {
            addCriterion("handicap_val in", values, "handicapVal");
            return (Criteria) this;
        }

        public Criteria andHandicapValNotIn(List<String> values) {
            addCriterion("handicap_val not in", values, "handicapVal");
            return (Criteria) this;
        }

        public Criteria andHandicapValBetween(String value1, String value2) {
            addCriterion("handicap_val between", value1, value2, "handicapVal");
            return (Criteria) this;
        }

        public Criteria andHandicapValNotBetween(String value1, String value2) {
            addCriterion("handicap_val not between", value1, value2, "handicapVal");
            return (Criteria) this;
        }

        public Criteria andOverUnderValIsNull() {
            addCriterion("over_under_val is null");
            return (Criteria) this;
        }

        public Criteria andOverUnderValIsNotNull() {
            addCriterion("over_under_val is not null");
            return (Criteria) this;
        }

        public Criteria andOverUnderValEqualTo(String value) {
            addCriterion("over_under_val =", value, "overUnderVal");
            return (Criteria) this;
        }

        public Criteria andOverUnderValNotEqualTo(String value) {
            addCriterion("over_under_val <>", value, "overUnderVal");
            return (Criteria) this;
        }

        public Criteria andOverUnderValGreaterThan(String value) {
            addCriterion("over_under_val >", value, "overUnderVal");
            return (Criteria) this;
        }

        public Criteria andOverUnderValGreaterThanOrEqualTo(String value) {
            addCriterion("over_under_val >=", value, "overUnderVal");
            return (Criteria) this;
        }

        public Criteria andOverUnderValLessThan(String value) {
            addCriterion("over_under_val <", value, "overUnderVal");
            return (Criteria) this;
        }

        public Criteria andOverUnderValLessThanOrEqualTo(String value) {
            addCriterion("over_under_val <=", value, "overUnderVal");
            return (Criteria) this;
        }

        public Criteria andOverUnderValLike(String value) {
            addCriterion("over_under_val like", value, "overUnderVal");
            return (Criteria) this;
        }

        public Criteria andOverUnderValNotLike(String value) {
            addCriterion("over_under_val not like", value, "overUnderVal");
            return (Criteria) this;
        }

        public Criteria andOverUnderValIn(List<String> values) {
            addCriterion("over_under_val in", values, "overUnderVal");
            return (Criteria) this;
        }

        public Criteria andOverUnderValNotIn(List<String> values) {
            addCriterion("over_under_val not in", values, "overUnderVal");
            return (Criteria) this;
        }

        public Criteria andOverUnderValBetween(String value1, String value2) {
            addCriterion("over_under_val between", value1, value2, "overUnderVal");
            return (Criteria) this;
        }

        public Criteria andOverUnderValNotBetween(String value1, String value2) {
            addCriterion("over_under_val not between", value1, value2, "overUnderVal");
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

        public Criteria andMatchGroupIsNull() {
            addCriterion("match_group is null");
            return (Criteria) this;
        }

        public Criteria andMatchGroupIsNotNull() {
            addCriterion("match_group is not null");
            return (Criteria) this;
        }

        public Criteria andMatchGroupEqualTo(Integer value) {
            addCriterion("match_group =", value, "matchGroup");
            return (Criteria) this;
        }

        public Criteria andMatchGroupNotEqualTo(Integer value) {
            addCriterion("match_group <>", value, "matchGroup");
            return (Criteria) this;
        }

        public Criteria andMatchGroupGreaterThan(Integer value) {
            addCriterion("match_group >", value, "matchGroup");
            return (Criteria) this;
        }

        public Criteria andMatchGroupGreaterThanOrEqualTo(Integer value) {
            addCriterion("match_group >=", value, "matchGroup");
            return (Criteria) this;
        }

        public Criteria andMatchGroupLessThan(Integer value) {
            addCriterion("match_group <", value, "matchGroup");
            return (Criteria) this;
        }

        public Criteria andMatchGroupLessThanOrEqualTo(Integer value) {
            addCriterion("match_group <=", value, "matchGroup");
            return (Criteria) this;
        }

        public Criteria andMatchGroupIn(List<Integer> values) {
            addCriterion("match_group in", values, "matchGroup");
            return (Criteria) this;
        }

        public Criteria andMatchGroupNotIn(List<Integer> values) {
            addCriterion("match_group not in", values, "matchGroup");
            return (Criteria) this;
        }

        public Criteria andMatchGroupBetween(Integer value1, Integer value2) {
            addCriterion("match_group between", value1, value2, "matchGroup");
            return (Criteria) this;
        }

        public Criteria andMatchGroupNotBetween(Integer value1, Integer value2) {
            addCriterion("match_group not between", value1, value2, "matchGroup");
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

        public Criteria andTournamentTypeEqualTo(Integer value) {
            addCriterion("tournament_type =", value, "tournamentType");
            return (Criteria) this;
        }

        public Criteria andTournamentTypeNotEqualTo(Integer value) {
            addCriterion("tournament_type <>", value, "tournamentType");
            return (Criteria) this;
        }

        public Criteria andTournamentTypeGreaterThan(Integer value) {
            addCriterion("tournament_type >", value, "tournamentType");
            return (Criteria) this;
        }

        public Criteria andTournamentTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("tournament_type >=", value, "tournamentType");
            return (Criteria) this;
        }

        public Criteria andTournamentTypeLessThan(Integer value) {
            addCriterion("tournament_type <", value, "tournamentType");
            return (Criteria) this;
        }

        public Criteria andTournamentTypeLessThanOrEqualTo(Integer value) {
            addCriterion("tournament_type <=", value, "tournamentType");
            return (Criteria) this;
        }

        public Criteria andTournamentTypeIn(List<Integer> values) {
            addCriterion("tournament_type in", values, "tournamentType");
            return (Criteria) this;
        }

        public Criteria andTournamentTypeNotIn(List<Integer> values) {
            addCriterion("tournament_type not in", values, "tournamentType");
            return (Criteria) this;
        }

        public Criteria andTournamentTypeBetween(Integer value1, Integer value2) {
            addCriterion("tournament_type between", value1, value2, "tournamentType");
            return (Criteria) this;
        }

        public Criteria andTournamentTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("tournament_type not between", value1, value2, "tournamentType");
            return (Criteria) this;
        }

        public Criteria andGroupIdIsNull() {
            addCriterion("group_id is null");
            return (Criteria) this;
        }

        public Criteria andGroupIdIsNotNull() {
            addCriterion("group_id is not null");
            return (Criteria) this;
        }

        public Criteria andGroupIdEqualTo(String value) {
            addCriterion("group_id =", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdNotEqualTo(String value) {
            addCriterion("group_id <>", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdGreaterThan(String value) {
            addCriterion("group_id >", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdGreaterThanOrEqualTo(String value) {
            addCriterion("group_id >=", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdLessThan(String value) {
            addCriterion("group_id <", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdLessThanOrEqualTo(String value) {
            addCriterion("group_id <=", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdLike(String value) {
            addCriterion("group_id like", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdNotLike(String value) {
            addCriterion("group_id not like", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdIn(List<String> values) {
            addCriterion("group_id in", values, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdNotIn(List<String> values) {
            addCriterion("group_id not in", values, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdBetween(String value1, String value2) {
            addCriterion("group_id between", value1, value2, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdNotBetween(String value1, String value2) {
            addCriterion("group_id not between", value1, value2, "groupId");
            return (Criteria) this;
        }

        public Criteria andRoundIsNull() {
            addCriterion("round is null");
            return (Criteria) this;
        }

        public Criteria andRoundIsNotNull() {
            addCriterion("round is not null");
            return (Criteria) this;
        }

        public Criteria andRoundEqualTo(String value) {
            addCriterion("round =", value, "round");
            return (Criteria) this;
        }

        public Criteria andRoundNotEqualTo(String value) {
            addCriterion("round <>", value, "round");
            return (Criteria) this;
        }

        public Criteria andRoundGreaterThan(String value) {
            addCriterion("round >", value, "round");
            return (Criteria) this;
        }

        public Criteria andRoundGreaterThanOrEqualTo(String value) {
            addCriterion("round >=", value, "round");
            return (Criteria) this;
        }

        public Criteria andRoundLessThan(String value) {
            addCriterion("round <", value, "round");
            return (Criteria) this;
        }

        public Criteria andRoundLessThanOrEqualTo(String value) {
            addCriterion("round <=", value, "round");
            return (Criteria) this;
        }

        public Criteria andRoundLike(String value) {
            addCriterion("round like", value, "round");
            return (Criteria) this;
        }

        public Criteria andRoundNotLike(String value) {
            addCriterion("round not like", value, "round");
            return (Criteria) this;
        }

        public Criteria andRoundIn(List<String> values) {
            addCriterion("round in", values, "round");
            return (Criteria) this;
        }

        public Criteria andRoundNotIn(List<String> values) {
            addCriterion("round not in", values, "round");
            return (Criteria) this;
        }

        public Criteria andRoundBetween(String value1, String value2) {
            addCriterion("round between", value1, value2, "round");
            return (Criteria) this;
        }

        public Criteria andRoundNotBetween(String value1, String value2) {
            addCriterion("round not between", value1, value2, "round");
            return (Criteria) this;
        }

        public Criteria andRoundTypeIsNull() {
            addCriterion("round_type is null");
            return (Criteria) this;
        }

        public Criteria andRoundTypeIsNotNull() {
            addCriterion("round_type is not null");
            return (Criteria) this;
        }

        public Criteria andRoundTypeEqualTo(String value) {
            addCriterion("round_type =", value, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeNotEqualTo(String value) {
            addCriterion("round_type <>", value, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeGreaterThan(String value) {
            addCriterion("round_type >", value, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeGreaterThanOrEqualTo(String value) {
            addCriterion("round_type >=", value, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeLessThan(String value) {
            addCriterion("round_type <", value, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeLessThanOrEqualTo(String value) {
            addCriterion("round_type <=", value, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeLike(String value) {
            addCriterion("round_type like", value, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeNotLike(String value) {
            addCriterion("round_type not like", value, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeIn(List<String> values) {
            addCriterion("round_type in", values, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeNotIn(List<String> values) {
            addCriterion("round_type not in", values, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeBetween(String value1, String value2) {
            addCriterion("round_type between", value1, value2, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeNotBetween(String value1, String value2) {
            addCriterion("round_type not between", value1, value2, "roundType");
            return (Criteria) this;
        }

        public Criteria andWeatherDescIsNull() {
            addCriterion("weather_desc is null");
            return (Criteria) this;
        }

        public Criteria andWeatherDescIsNotNull() {
            addCriterion("weather_desc is not null");
            return (Criteria) this;
        }

        public Criteria andWeatherDescEqualTo(String value) {
            addCriterion("weather_desc =", value, "weatherDesc");
            return (Criteria) this;
        }

        public Criteria andWeatherDescNotEqualTo(String value) {
            addCriterion("weather_desc <>", value, "weatherDesc");
            return (Criteria) this;
        }

        public Criteria andWeatherDescGreaterThan(String value) {
            addCriterion("weather_desc >", value, "weatherDesc");
            return (Criteria) this;
        }

        public Criteria andWeatherDescGreaterThanOrEqualTo(String value) {
            addCriterion("weather_desc >=", value, "weatherDesc");
            return (Criteria) this;
        }

        public Criteria andWeatherDescLessThan(String value) {
            addCriterion("weather_desc <", value, "weatherDesc");
            return (Criteria) this;
        }

        public Criteria andWeatherDescLessThanOrEqualTo(String value) {
            addCriterion("weather_desc <=", value, "weatherDesc");
            return (Criteria) this;
        }

        public Criteria andWeatherDescLike(String value) {
            addCriterion("weather_desc like", value, "weatherDesc");
            return (Criteria) this;
        }

        public Criteria andWeatherDescNotLike(String value) {
            addCriterion("weather_desc not like", value, "weatherDesc");
            return (Criteria) this;
        }

        public Criteria andWeatherDescIn(List<String> values) {
            addCriterion("weather_desc in", values, "weatherDesc");
            return (Criteria) this;
        }

        public Criteria andWeatherDescNotIn(List<String> values) {
            addCriterion("weather_desc not in", values, "weatherDesc");
            return (Criteria) this;
        }

        public Criteria andWeatherDescBetween(String value1, String value2) {
            addCriterion("weather_desc between", value1, value2, "weatherDesc");
            return (Criteria) this;
        }

        public Criteria andWeatherDescNotBetween(String value1, String value2) {
            addCriterion("weather_desc not between", value1, value2, "weatherDesc");
            return (Criteria) this;
        }

        public Criteria andGoogleMapsCoordinatesIsNull() {
            addCriterion("google_maps_coordinates is null");
            return (Criteria) this;
        }

        public Criteria andGoogleMapsCoordinatesIsNotNull() {
            addCriterion("google_maps_coordinates is not null");
            return (Criteria) this;
        }

        public Criteria andGoogleMapsCoordinatesEqualTo(String value) {
            addCriterion("google_maps_coordinates =", value, "googleMapsCoordinates");
            return (Criteria) this;
        }

        public Criteria andGoogleMapsCoordinatesNotEqualTo(String value) {
            addCriterion("google_maps_coordinates <>", value, "googleMapsCoordinates");
            return (Criteria) this;
        }

        public Criteria andGoogleMapsCoordinatesGreaterThan(String value) {
            addCriterion("google_maps_coordinates >", value, "googleMapsCoordinates");
            return (Criteria) this;
        }

        public Criteria andGoogleMapsCoordinatesGreaterThanOrEqualTo(String value) {
            addCriterion("google_maps_coordinates >=", value, "googleMapsCoordinates");
            return (Criteria) this;
        }

        public Criteria andGoogleMapsCoordinatesLessThan(String value) {
            addCriterion("google_maps_coordinates <", value, "googleMapsCoordinates");
            return (Criteria) this;
        }

        public Criteria andGoogleMapsCoordinatesLessThanOrEqualTo(String value) {
            addCriterion("google_maps_coordinates <=", value, "googleMapsCoordinates");
            return (Criteria) this;
        }

        public Criteria andGoogleMapsCoordinatesLike(String value) {
            addCriterion("google_maps_coordinates like", value, "googleMapsCoordinates");
            return (Criteria) this;
        }

        public Criteria andGoogleMapsCoordinatesNotLike(String value) {
            addCriterion("google_maps_coordinates not like", value, "googleMapsCoordinates");
            return (Criteria) this;
        }

        public Criteria andGoogleMapsCoordinatesIn(List<String> values) {
            addCriterion("google_maps_coordinates in", values, "googleMapsCoordinates");
            return (Criteria) this;
        }

        public Criteria andGoogleMapsCoordinatesNotIn(List<String> values) {
            addCriterion("google_maps_coordinates not in", values, "googleMapsCoordinates");
            return (Criteria) this;
        }

        public Criteria andGoogleMapsCoordinatesBetween(String value1, String value2) {
            addCriterion("google_maps_coordinates between", value1, value2, "googleMapsCoordinates");
            return (Criteria) this;
        }

        public Criteria andGoogleMapsCoordinatesNotBetween(String value1, String value2) {
            addCriterion("google_maps_coordinates not between", value1, value2, "googleMapsCoordinates");
            return (Criteria) this;
        }

        public Criteria andStadiumNamesIsNull() {
            addCriterion("stadium_names is null");
            return (Criteria) this;
        }

        public Criteria andStadiumNamesIsNotNull() {
            addCriterion("stadium_names is not null");
            return (Criteria) this;
        }

        public Criteria andStadiumNamesEqualTo(String value) {
            addCriterion("stadium_names =", value, "stadiumNames");
            return (Criteria) this;
        }

        public Criteria andStadiumNamesNotEqualTo(String value) {
            addCriterion("stadium_names <>", value, "stadiumNames");
            return (Criteria) this;
        }

        public Criteria andStadiumNamesGreaterThan(String value) {
            addCriterion("stadium_names >", value, "stadiumNames");
            return (Criteria) this;
        }

        public Criteria andStadiumNamesGreaterThanOrEqualTo(String value) {
            addCriterion("stadium_names >=", value, "stadiumNames");
            return (Criteria) this;
        }

        public Criteria andStadiumNamesLessThan(String value) {
            addCriterion("stadium_names <", value, "stadiumNames");
            return (Criteria) this;
        }

        public Criteria andStadiumNamesLessThanOrEqualTo(String value) {
            addCriterion("stadium_names <=", value, "stadiumNames");
            return (Criteria) this;
        }

        public Criteria andStadiumNamesLike(String value) {
            addCriterion("stadium_names like", value, "stadiumNames");
            return (Criteria) this;
        }

        public Criteria andStadiumNamesNotLike(String value) {
            addCriterion("stadium_names not like", value, "stadiumNames");
            return (Criteria) this;
        }

        public Criteria andStadiumNamesIn(List<String> values) {
            addCriterion("stadium_names in", values, "stadiumNames");
            return (Criteria) this;
        }

        public Criteria andStadiumNamesNotIn(List<String> values) {
            addCriterion("stadium_names not in", values, "stadiumNames");
            return (Criteria) this;
        }

        public Criteria andStadiumNamesBetween(String value1, String value2) {
            addCriterion("stadium_names between", value1, value2, "stadiumNames");
            return (Criteria) this;
        }

        public Criteria andStadiumNamesNotBetween(String value1, String value2) {
            addCriterion("stadium_names not between", value1, value2, "stadiumNames");
            return (Criteria) this;
        }

        public Criteria andEditStatusIsNull() {
            addCriterion("edit_status is null");
            return (Criteria) this;
        }

        public Criteria andEditStatusIsNotNull() {
            addCriterion("edit_status is not null");
            return (Criteria) this;
        }

        public Criteria andEditStatusEqualTo(Integer value) {
            addCriterion("edit_status =", value, "editStatus");
            return (Criteria) this;
        }

        public Criteria andEditStatusNotEqualTo(Integer value) {
            addCriterion("edit_status <>", value, "editStatus");
            return (Criteria) this;
        }

        public Criteria andEditStatusGreaterThan(Integer value) {
            addCriterion("edit_status >", value, "editStatus");
            return (Criteria) this;
        }

        public Criteria andEditStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("edit_status >=", value, "editStatus");
            return (Criteria) this;
        }

        public Criteria andEditStatusLessThan(Integer value) {
            addCriterion("edit_status <", value, "editStatus");
            return (Criteria) this;
        }

        public Criteria andEditStatusLessThanOrEqualTo(Integer value) {
            addCriterion("edit_status <=", value, "editStatus");
            return (Criteria) this;
        }

        public Criteria andEditStatusIn(List<Integer> values) {
            addCriterion("edit_status in", values, "editStatus");
            return (Criteria) this;
        }

        public Criteria andEditStatusNotIn(List<Integer> values) {
            addCriterion("edit_status not in", values, "editStatus");
            return (Criteria) this;
        }

        public Criteria andEditStatusBetween(Integer value1, Integer value2) {
            addCriterion("edit_status between", value1, value2, "editStatus");
            return (Criteria) this;
        }

        public Criteria andEditStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("edit_status not between", value1, value2, "editStatus");
            return (Criteria) this;
        }

        public Criteria andHomeTeamScoreD01IsNull() {
            addCriterion("home_team_score_d01 is null");
            return (Criteria) this;
        }

        public Criteria andHomeTeamScoreD01IsNotNull() {
            addCriterion("home_team_score_d01 is not null");
            return (Criteria) this;
        }

        public Criteria andHomeTeamScoreD01EqualTo(String value) {
            addCriterion("home_team_score_d01 =", value, "homeTeamScoreD01");
            return (Criteria) this;
        }

        public Criteria andHomeTeamScoreD01NotEqualTo(String value) {
            addCriterion("home_team_score_d01 <>", value, "homeTeamScoreD01");
            return (Criteria) this;
        }

        public Criteria andHomeTeamScoreD01GreaterThan(String value) {
            addCriterion("home_team_score_d01 >", value, "homeTeamScoreD01");
            return (Criteria) this;
        }

        public Criteria andHomeTeamScoreD01GreaterThanOrEqualTo(String value) {
            addCriterion("home_team_score_d01 >=", value, "homeTeamScoreD01");
            return (Criteria) this;
        }

        public Criteria andHomeTeamScoreD01LessThan(String value) {
            addCriterion("home_team_score_d01 <", value, "homeTeamScoreD01");
            return (Criteria) this;
        }

        public Criteria andHomeTeamScoreD01LessThanOrEqualTo(String value) {
            addCriterion("home_team_score_d01 <=", value, "homeTeamScoreD01");
            return (Criteria) this;
        }

        public Criteria andHomeTeamScoreD01Like(String value) {
            addCriterion("home_team_score_d01 like", value, "homeTeamScoreD01");
            return (Criteria) this;
        }

        public Criteria andHomeTeamScoreD01NotLike(String value) {
            addCriterion("home_team_score_d01 not like", value, "homeTeamScoreD01");
            return (Criteria) this;
        }

        public Criteria andHomeTeamScoreD01In(List<String> values) {
            addCriterion("home_team_score_d01 in", values, "homeTeamScoreD01");
            return (Criteria) this;
        }

        public Criteria andHomeTeamScoreD01NotIn(List<String> values) {
            addCriterion("home_team_score_d01 not in", values, "homeTeamScoreD01");
            return (Criteria) this;
        }

        public Criteria andHomeTeamScoreD01Between(String value1, String value2) {
            addCriterion("home_team_score_d01 between", value1, value2, "homeTeamScoreD01");
            return (Criteria) this;
        }

        public Criteria andHomeTeamScoreD01NotBetween(String value1, String value2) {
            addCriterion("home_team_score_d01 not between", value1, value2, "homeTeamScoreD01");
            return (Criteria) this;
        }

        public Criteria andAwayTeamScoreD01IsNull() {
            addCriterion("away_team_score_d01 is null");
            return (Criteria) this;
        }

        public Criteria andAwayTeamScoreD01IsNotNull() {
            addCriterion("away_team_score_d01 is not null");
            return (Criteria) this;
        }

        public Criteria andAwayTeamScoreD01EqualTo(String value) {
            addCriterion("away_team_score_d01 =", value, "awayTeamScoreD01");
            return (Criteria) this;
        }

        public Criteria andAwayTeamScoreD01NotEqualTo(String value) {
            addCriterion("away_team_score_d01 <>", value, "awayTeamScoreD01");
            return (Criteria) this;
        }

        public Criteria andAwayTeamScoreD01GreaterThan(String value) {
            addCriterion("away_team_score_d01 >", value, "awayTeamScoreD01");
            return (Criteria) this;
        }

        public Criteria andAwayTeamScoreD01GreaterThanOrEqualTo(String value) {
            addCriterion("away_team_score_d01 >=", value, "awayTeamScoreD01");
            return (Criteria) this;
        }

        public Criteria andAwayTeamScoreD01LessThan(String value) {
            addCriterion("away_team_score_d01 <", value, "awayTeamScoreD01");
            return (Criteria) this;
        }

        public Criteria andAwayTeamScoreD01LessThanOrEqualTo(String value) {
            addCriterion("away_team_score_d01 <=", value, "awayTeamScoreD01");
            return (Criteria) this;
        }

        public Criteria andAwayTeamScoreD01Like(String value) {
            addCriterion("away_team_score_d01 like", value, "awayTeamScoreD01");
            return (Criteria) this;
        }

        public Criteria andAwayTeamScoreD01NotLike(String value) {
            addCriterion("away_team_score_d01 not like", value, "awayTeamScoreD01");
            return (Criteria) this;
        }

        public Criteria andAwayTeamScoreD01In(List<String> values) {
            addCriterion("away_team_score_d01 in", values, "awayTeamScoreD01");
            return (Criteria) this;
        }

        public Criteria andAwayTeamScoreD01NotIn(List<String> values) {
            addCriterion("away_team_score_d01 not in", values, "awayTeamScoreD01");
            return (Criteria) this;
        }

        public Criteria andAwayTeamScoreD01Between(String value1, String value2) {
            addCriterion("away_team_score_d01 between", value1, value2, "awayTeamScoreD01");
            return (Criteria) this;
        }

        public Criteria andAwayTeamScoreD01NotBetween(String value1, String value2) {
            addCriterion("away_team_score_d01 not between", value1, value2, "awayTeamScoreD01");
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