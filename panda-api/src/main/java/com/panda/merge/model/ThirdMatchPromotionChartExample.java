package com.panda.merge.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ThirdMatchPromotionChartExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ThirdMatchPromotionChartExample() {
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

        public Criteria andTournamentIdIsNull() {
            addCriterion("tournament_id is null");
            return (Criteria) this;
        }

        public Criteria andTournamentIdIsNotNull() {
            addCriterion("tournament_id is not null");
            return (Criteria) this;
        }

        public Criteria andTournamentIdEqualTo(String value) {
            addCriterion("tournament_id =", value, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdNotEqualTo(String value) {
            addCriterion("tournament_id <>", value, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdGreaterThan(String value) {
            addCriterion("tournament_id >", value, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdGreaterThanOrEqualTo(String value) {
            addCriterion("tournament_id >=", value, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdLessThan(String value) {
            addCriterion("tournament_id <", value, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdLessThanOrEqualTo(String value) {
            addCriterion("tournament_id <=", value, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdLike(String value) {
            addCriterion("tournament_id like", value, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdNotLike(String value) {
            addCriterion("tournament_id not like", value, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdIn(List<String> values) {
            addCriterion("tournament_id in", values, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdNotIn(List<String> values) {
            addCriterion("tournament_id not in", values, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdBetween(String value1, String value2) {
            addCriterion("tournament_id between", value1, value2, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdNotBetween(String value1, String value2) {
            addCriterion("tournament_id not between", value1, value2, "tournamentId");
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

        public Criteria andCnNameIsNull() {
            addCriterion("cn_name is null");
            return (Criteria) this;
        }

        public Criteria andCnNameIsNotNull() {
            addCriterion("cn_name is not null");
            return (Criteria) this;
        }

        public Criteria andCnNameEqualTo(String value) {
            addCriterion("cn_name =", value, "cnName");
            return (Criteria) this;
        }

        public Criteria andCnNameNotEqualTo(String value) {
            addCriterion("cn_name <>", value, "cnName");
            return (Criteria) this;
        }

        public Criteria andCnNameGreaterThan(String value) {
            addCriterion("cn_name >", value, "cnName");
            return (Criteria) this;
        }

        public Criteria andCnNameGreaterThanOrEqualTo(String value) {
            addCriterion("cn_name >=", value, "cnName");
            return (Criteria) this;
        }

        public Criteria andCnNameLessThan(String value) {
            addCriterion("cn_name <", value, "cnName");
            return (Criteria) this;
        }

        public Criteria andCnNameLessThanOrEqualTo(String value) {
            addCriterion("cn_name <=", value, "cnName");
            return (Criteria) this;
        }

        public Criteria andCnNameLike(String value) {
            addCriterion("cn_name like", value, "cnName");
            return (Criteria) this;
        }

        public Criteria andCnNameNotLike(String value) {
            addCriterion("cn_name not like", value, "cnName");
            return (Criteria) this;
        }

        public Criteria andCnNameIn(List<String> values) {
            addCriterion("cn_name in", values, "cnName");
            return (Criteria) this;
        }

        public Criteria andCnNameNotIn(List<String> values) {
            addCriterion("cn_name not in", values, "cnName");
            return (Criteria) this;
        }

        public Criteria andCnNameBetween(String value1, String value2) {
            addCriterion("cn_name between", value1, value2, "cnName");
            return (Criteria) this;
        }

        public Criteria andCnNameNotBetween(String value1, String value2) {
            addCriterion("cn_name not between", value1, value2, "cnName");
            return (Criteria) this;
        }

        public Criteria andEnNameIsNull() {
            addCriterion("en_name is null");
            return (Criteria) this;
        }

        public Criteria andEnNameIsNotNull() {
            addCriterion("en_name is not null");
            return (Criteria) this;
        }

        public Criteria andEnNameEqualTo(String value) {
            addCriterion("en_name =", value, "enName");
            return (Criteria) this;
        }

        public Criteria andEnNameNotEqualTo(String value) {
            addCriterion("en_name <>", value, "enName");
            return (Criteria) this;
        }

        public Criteria andEnNameGreaterThan(String value) {
            addCriterion("en_name >", value, "enName");
            return (Criteria) this;
        }

        public Criteria andEnNameGreaterThanOrEqualTo(String value) {
            addCriterion("en_name >=", value, "enName");
            return (Criteria) this;
        }

        public Criteria andEnNameLessThan(String value) {
            addCriterion("en_name <", value, "enName");
            return (Criteria) this;
        }

        public Criteria andEnNameLessThanOrEqualTo(String value) {
            addCriterion("en_name <=", value, "enName");
            return (Criteria) this;
        }

        public Criteria andEnNameLike(String value) {
            addCriterion("en_name like", value, "enName");
            return (Criteria) this;
        }

        public Criteria andEnNameNotLike(String value) {
            addCriterion("en_name not like", value, "enName");
            return (Criteria) this;
        }

        public Criteria andEnNameIn(List<String> values) {
            addCriterion("en_name in", values, "enName");
            return (Criteria) this;
        }

        public Criteria andEnNameNotIn(List<String> values) {
            addCriterion("en_name not in", values, "enName");
            return (Criteria) this;
        }

        public Criteria andEnNameBetween(String value1, String value2) {
            addCriterion("en_name between", value1, value2, "enName");
            return (Criteria) this;
        }

        public Criteria andEnNameNotBetween(String value1, String value2) {
            addCriterion("en_name not between", value1, value2, "enName");
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

        public Criteria andGroupIdEqualTo(Long value) {
            addCriterion("group_id =", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdNotEqualTo(Long value) {
            addCriterion("group_id <>", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdGreaterThan(Long value) {
            addCriterion("group_id >", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdGreaterThanOrEqualTo(Long value) {
            addCriterion("group_id >=", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdLessThan(Long value) {
            addCriterion("group_id <", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdLessThanOrEqualTo(Long value) {
            addCriterion("group_id <=", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdIn(List<Long> values) {
            addCriterion("group_id in", values, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdNotIn(List<Long> values) {
            addCriterion("group_id not in", values, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdBetween(Long value1, Long value2) {
            addCriterion("group_id between", value1, value2, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdNotBetween(Long value1, Long value2) {
            addCriterion("group_id not between", value1, value2, "groupId");
            return (Criteria) this;
        }

        public Criteria andSeriesIdIsNull() {
            addCriterion("series_id is null");
            return (Criteria) this;
        }

        public Criteria andSeriesIdIsNotNull() {
            addCriterion("series_id is not null");
            return (Criteria) this;
        }

        public Criteria andSeriesIdEqualTo(String value) {
            addCriterion("series_id =", value, "seriesId");
            return (Criteria) this;
        }

        public Criteria andSeriesIdNotEqualTo(String value) {
            addCriterion("series_id <>", value, "seriesId");
            return (Criteria) this;
        }

        public Criteria andSeriesIdGreaterThan(String value) {
            addCriterion("series_id >", value, "seriesId");
            return (Criteria) this;
        }

        public Criteria andSeriesIdGreaterThanOrEqualTo(String value) {
            addCriterion("series_id >=", value, "seriesId");
            return (Criteria) this;
        }

        public Criteria andSeriesIdLessThan(String value) {
            addCriterion("series_id <", value, "seriesId");
            return (Criteria) this;
        }

        public Criteria andSeriesIdLessThanOrEqualTo(String value) {
            addCriterion("series_id <=", value, "seriesId");
            return (Criteria) this;
        }

        public Criteria andSeriesIdLike(String value) {
            addCriterion("series_id like", value, "seriesId");
            return (Criteria) this;
        }

        public Criteria andSeriesIdNotLike(String value) {
            addCriterion("series_id not like", value, "seriesId");
            return (Criteria) this;
        }

        public Criteria andSeriesIdIn(List<String> values) {
            addCriterion("series_id in", values, "seriesId");
            return (Criteria) this;
        }

        public Criteria andSeriesIdNotIn(List<String> values) {
            addCriterion("series_id not in", values, "seriesId");
            return (Criteria) this;
        }

        public Criteria andSeriesIdBetween(String value1, String value2) {
            addCriterion("series_id between", value1, value2, "seriesId");
            return (Criteria) this;
        }

        public Criteria andSeriesIdNotBetween(String value1, String value2) {
            addCriterion("series_id not between", value1, value2, "seriesId");
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

        public Criteria andBeginTimeEqualTo(Date value) {
            addCriterion("begin_time =", value, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeNotEqualTo(Date value) {
            addCriterion("begin_time <>", value, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeGreaterThan(Date value) {
            addCriterion("begin_time >", value, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("begin_time >=", value, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeLessThan(Date value) {
            addCriterion("begin_time <", value, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeLessThanOrEqualTo(Date value) {
            addCriterion("begin_time <=", value, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeIn(List<Date> values) {
            addCriterion("begin_time in", values, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeNotIn(List<Date> values) {
            addCriterion("begin_time not in", values, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeBetween(Date value1, Date value2) {
            addCriterion("begin_time between", value1, value2, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeNotBetween(Date value1, Date value2) {
            addCriterion("begin_time not between", value1, value2, "beginTime");
            return (Criteria) this;
        }

        public Criteria andTeam1IdIsNull() {
            addCriterion("team1_id is null");
            return (Criteria) this;
        }

        public Criteria andTeam1IdIsNotNull() {
            addCriterion("team1_id is not null");
            return (Criteria) this;
        }

        public Criteria andTeam1IdEqualTo(String value) {
            addCriterion("team1_id =", value, "team1Id");
            return (Criteria) this;
        }

        public Criteria andTeam1IdNotEqualTo(String value) {
            addCriterion("team1_id <>", value, "team1Id");
            return (Criteria) this;
        }

        public Criteria andTeam1IdGreaterThan(String value) {
            addCriterion("team1_id >", value, "team1Id");
            return (Criteria) this;
        }

        public Criteria andTeam1IdGreaterThanOrEqualTo(String value) {
            addCriterion("team1_id >=", value, "team1Id");
            return (Criteria) this;
        }

        public Criteria andTeam1IdLessThan(String value) {
            addCriterion("team1_id <", value, "team1Id");
            return (Criteria) this;
        }

        public Criteria andTeam1IdLessThanOrEqualTo(String value) {
            addCriterion("team1_id <=", value, "team1Id");
            return (Criteria) this;
        }

        public Criteria andTeam1IdLike(String value) {
            addCriterion("team1_id like", value, "team1Id");
            return (Criteria) this;
        }

        public Criteria andTeam1IdNotLike(String value) {
            addCriterion("team1_id not like", value, "team1Id");
            return (Criteria) this;
        }

        public Criteria andTeam1IdIn(List<String> values) {
            addCriterion("team1_id in", values, "team1Id");
            return (Criteria) this;
        }

        public Criteria andTeam1IdNotIn(List<String> values) {
            addCriterion("team1_id not in", values, "team1Id");
            return (Criteria) this;
        }

        public Criteria andTeam1IdBetween(String value1, String value2) {
            addCriterion("team1_id between", value1, value2, "team1Id");
            return (Criteria) this;
        }

        public Criteria andTeam1IdNotBetween(String value1, String value2) {
            addCriterion("team1_id not between", value1, value2, "team1Id");
            return (Criteria) this;
        }

        public Criteria andTeam2IdIsNull() {
            addCriterion("team2_id is null");
            return (Criteria) this;
        }

        public Criteria andTeam2IdIsNotNull() {
            addCriterion("team2_id is not null");
            return (Criteria) this;
        }

        public Criteria andTeam2IdEqualTo(String value) {
            addCriterion("team2_id =", value, "team2Id");
            return (Criteria) this;
        }

        public Criteria andTeam2IdNotEqualTo(String value) {
            addCriterion("team2_id <>", value, "team2Id");
            return (Criteria) this;
        }

        public Criteria andTeam2IdGreaterThan(String value) {
            addCriterion("team2_id >", value, "team2Id");
            return (Criteria) this;
        }

        public Criteria andTeam2IdGreaterThanOrEqualTo(String value) {
            addCriterion("team2_id >=", value, "team2Id");
            return (Criteria) this;
        }

        public Criteria andTeam2IdLessThan(String value) {
            addCriterion("team2_id <", value, "team2Id");
            return (Criteria) this;
        }

        public Criteria andTeam2IdLessThanOrEqualTo(String value) {
            addCriterion("team2_id <=", value, "team2Id");
            return (Criteria) this;
        }

        public Criteria andTeam2IdLike(String value) {
            addCriterion("team2_id like", value, "team2Id");
            return (Criteria) this;
        }

        public Criteria andTeam2IdNotLike(String value) {
            addCriterion("team2_id not like", value, "team2Id");
            return (Criteria) this;
        }

        public Criteria andTeam2IdIn(List<String> values) {
            addCriterion("team2_id in", values, "team2Id");
            return (Criteria) this;
        }

        public Criteria andTeam2IdNotIn(List<String> values) {
            addCriterion("team2_id not in", values, "team2Id");
            return (Criteria) this;
        }

        public Criteria andTeam2IdBetween(String value1, String value2) {
            addCriterion("team2_id between", value1, value2, "team2Id");
            return (Criteria) this;
        }

        public Criteria andTeam2IdNotBetween(String value1, String value2) {
            addCriterion("team2_id not between", value1, value2, "team2Id");
            return (Criteria) this;
        }

        public Criteria andTeam1NameIsNull() {
            addCriterion("team1_name is null");
            return (Criteria) this;
        }

        public Criteria andTeam1NameIsNotNull() {
            addCriterion("team1_name is not null");
            return (Criteria) this;
        }

        public Criteria andTeam1NameEqualTo(String value) {
            addCriterion("team1_name =", value, "team1Name");
            return (Criteria) this;
        }

        public Criteria andTeam1NameNotEqualTo(String value) {
            addCriterion("team1_name <>", value, "team1Name");
            return (Criteria) this;
        }

        public Criteria andTeam1NameGreaterThan(String value) {
            addCriterion("team1_name >", value, "team1Name");
            return (Criteria) this;
        }

        public Criteria andTeam1NameGreaterThanOrEqualTo(String value) {
            addCriterion("team1_name >=", value, "team1Name");
            return (Criteria) this;
        }

        public Criteria andTeam1NameLessThan(String value) {
            addCriterion("team1_name <", value, "team1Name");
            return (Criteria) this;
        }

        public Criteria andTeam1NameLessThanOrEqualTo(String value) {
            addCriterion("team1_name <=", value, "team1Name");
            return (Criteria) this;
        }

        public Criteria andTeam1NameLike(String value) {
            addCriterion("team1_name like", value, "team1Name");
            return (Criteria) this;
        }

        public Criteria andTeam1NameNotLike(String value) {
            addCriterion("team1_name not like", value, "team1Name");
            return (Criteria) this;
        }

        public Criteria andTeam1NameIn(List<String> values) {
            addCriterion("team1_name in", values, "team1Name");
            return (Criteria) this;
        }

        public Criteria andTeam1NameNotIn(List<String> values) {
            addCriterion("team1_name not in", values, "team1Name");
            return (Criteria) this;
        }

        public Criteria andTeam1NameBetween(String value1, String value2) {
            addCriterion("team1_name between", value1, value2, "team1Name");
            return (Criteria) this;
        }

        public Criteria andTeam1NameNotBetween(String value1, String value2) {
            addCriterion("team1_name not between", value1, value2, "team1Name");
            return (Criteria) this;
        }

        public Criteria andTeam2NameIsNull() {
            addCriterion("team2_name is null");
            return (Criteria) this;
        }

        public Criteria andTeam2NameIsNotNull() {
            addCriterion("team2_name is not null");
            return (Criteria) this;
        }

        public Criteria andTeam2NameEqualTo(String value) {
            addCriterion("team2_name =", value, "team2Name");
            return (Criteria) this;
        }

        public Criteria andTeam2NameNotEqualTo(String value) {
            addCriterion("team2_name <>", value, "team2Name");
            return (Criteria) this;
        }

        public Criteria andTeam2NameGreaterThan(String value) {
            addCriterion("team2_name >", value, "team2Name");
            return (Criteria) this;
        }

        public Criteria andTeam2NameGreaterThanOrEqualTo(String value) {
            addCriterion("team2_name >=", value, "team2Name");
            return (Criteria) this;
        }

        public Criteria andTeam2NameLessThan(String value) {
            addCriterion("team2_name <", value, "team2Name");
            return (Criteria) this;
        }

        public Criteria andTeam2NameLessThanOrEqualTo(String value) {
            addCriterion("team2_name <=", value, "team2Name");
            return (Criteria) this;
        }

        public Criteria andTeam2NameLike(String value) {
            addCriterion("team2_name like", value, "team2Name");
            return (Criteria) this;
        }

        public Criteria andTeam2NameNotLike(String value) {
            addCriterion("team2_name not like", value, "team2Name");
            return (Criteria) this;
        }

        public Criteria andTeam2NameIn(List<String> values) {
            addCriterion("team2_name in", values, "team2Name");
            return (Criteria) this;
        }

        public Criteria andTeam2NameNotIn(List<String> values) {
            addCriterion("team2_name not in", values, "team2Name");
            return (Criteria) this;
        }

        public Criteria andTeam2NameBetween(String value1, String value2) {
            addCriterion("team2_name between", value1, value2, "team2Name");
            return (Criteria) this;
        }

        public Criteria andTeam2NameNotBetween(String value1, String value2) {
            addCriterion("team2_name not between", value1, value2, "team2Name");
            return (Criteria) this;
        }

        public Criteria andTeam1ScoreIsNull() {
            addCriterion("team1_score is null");
            return (Criteria) this;
        }

        public Criteria andTeam1ScoreIsNotNull() {
            addCriterion("team1_score is not null");
            return (Criteria) this;
        }

        public Criteria andTeam1ScoreEqualTo(String value) {
            addCriterion("team1_score =", value, "team1Score");
            return (Criteria) this;
        }

        public Criteria andTeam1ScoreNotEqualTo(String value) {
            addCriterion("team1_score <>", value, "team1Score");
            return (Criteria) this;
        }

        public Criteria andTeam1ScoreGreaterThan(String value) {
            addCriterion("team1_score >", value, "team1Score");
            return (Criteria) this;
        }

        public Criteria andTeam1ScoreGreaterThanOrEqualTo(String value) {
            addCriterion("team1_score >=", value, "team1Score");
            return (Criteria) this;
        }

        public Criteria andTeam1ScoreLessThan(String value) {
            addCriterion("team1_score <", value, "team1Score");
            return (Criteria) this;
        }

        public Criteria andTeam1ScoreLessThanOrEqualTo(String value) {
            addCriterion("team1_score <=", value, "team1Score");
            return (Criteria) this;
        }

        public Criteria andTeam1ScoreLike(String value) {
            addCriterion("team1_score like", value, "team1Score");
            return (Criteria) this;
        }

        public Criteria andTeam1ScoreNotLike(String value) {
            addCriterion("team1_score not like", value, "team1Score");
            return (Criteria) this;
        }

        public Criteria andTeam1ScoreIn(List<String> values) {
            addCriterion("team1_score in", values, "team1Score");
            return (Criteria) this;
        }

        public Criteria andTeam1ScoreNotIn(List<String> values) {
            addCriterion("team1_score not in", values, "team1Score");
            return (Criteria) this;
        }

        public Criteria andTeam1ScoreBetween(String value1, String value2) {
            addCriterion("team1_score between", value1, value2, "team1Score");
            return (Criteria) this;
        }

        public Criteria andTeam1ScoreNotBetween(String value1, String value2) {
            addCriterion("team1_score not between", value1, value2, "team1Score");
            return (Criteria) this;
        }

        public Criteria andTeam2ScoreIsNull() {
            addCriterion("team2_score is null");
            return (Criteria) this;
        }

        public Criteria andTeam2ScoreIsNotNull() {
            addCriterion("team2_score is not null");
            return (Criteria) this;
        }

        public Criteria andTeam2ScoreEqualTo(String value) {
            addCriterion("team2_score =", value, "team2Score");
            return (Criteria) this;
        }

        public Criteria andTeam2ScoreNotEqualTo(String value) {
            addCriterion("team2_score <>", value, "team2Score");
            return (Criteria) this;
        }

        public Criteria andTeam2ScoreGreaterThan(String value) {
            addCriterion("team2_score >", value, "team2Score");
            return (Criteria) this;
        }

        public Criteria andTeam2ScoreGreaterThanOrEqualTo(String value) {
            addCriterion("team2_score >=", value, "team2Score");
            return (Criteria) this;
        }

        public Criteria andTeam2ScoreLessThan(String value) {
            addCriterion("team2_score <", value, "team2Score");
            return (Criteria) this;
        }

        public Criteria andTeam2ScoreLessThanOrEqualTo(String value) {
            addCriterion("team2_score <=", value, "team2Score");
            return (Criteria) this;
        }

        public Criteria andTeam2ScoreLike(String value) {
            addCriterion("team2_score like", value, "team2Score");
            return (Criteria) this;
        }

        public Criteria andTeam2ScoreNotLike(String value) {
            addCriterion("team2_score not like", value, "team2Score");
            return (Criteria) this;
        }

        public Criteria andTeam2ScoreIn(List<String> values) {
            addCriterion("team2_score in", values, "team2Score");
            return (Criteria) this;
        }

        public Criteria andTeam2ScoreNotIn(List<String> values) {
            addCriterion("team2_score not in", values, "team2Score");
            return (Criteria) this;
        }

        public Criteria andTeam2ScoreBetween(String value1, String value2) {
            addCriterion("team2_score between", value1, value2, "team2Score");
            return (Criteria) this;
        }

        public Criteria andTeam2ScoreNotBetween(String value1, String value2) {
            addCriterion("team2_score not between", value1, value2, "team2Score");
            return (Criteria) this;
        }

        public Criteria andTeam1ComeFromIsNull() {
            addCriterion("team1_come_from is null");
            return (Criteria) this;
        }

        public Criteria andTeam1ComeFromIsNotNull() {
            addCriterion("team1_come_from is not null");
            return (Criteria) this;
        }

        public Criteria andTeam1ComeFromEqualTo(Integer value) {
            addCriterion("team1_come_from =", value, "team1ComeFrom");
            return (Criteria) this;
        }

        public Criteria andTeam1ComeFromNotEqualTo(Integer value) {
            addCriterion("team1_come_from <>", value, "team1ComeFrom");
            return (Criteria) this;
        }

        public Criteria andTeam1ComeFromGreaterThan(Integer value) {
            addCriterion("team1_come_from >", value, "team1ComeFrom");
            return (Criteria) this;
        }

        public Criteria andTeam1ComeFromGreaterThanOrEqualTo(Integer value) {
            addCriterion("team1_come_from >=", value, "team1ComeFrom");
            return (Criteria) this;
        }

        public Criteria andTeam1ComeFromLessThan(Integer value) {
            addCriterion("team1_come_from <", value, "team1ComeFrom");
            return (Criteria) this;
        }

        public Criteria andTeam1ComeFromLessThanOrEqualTo(Integer value) {
            addCriterion("team1_come_from <=", value, "team1ComeFrom");
            return (Criteria) this;
        }

        public Criteria andTeam1ComeFromIn(List<Integer> values) {
            addCriterion("team1_come_from in", values, "team1ComeFrom");
            return (Criteria) this;
        }

        public Criteria andTeam1ComeFromNotIn(List<Integer> values) {
            addCriterion("team1_come_from not in", values, "team1ComeFrom");
            return (Criteria) this;
        }

        public Criteria andTeam1ComeFromBetween(Integer value1, Integer value2) {
            addCriterion("team1_come_from between", value1, value2, "team1ComeFrom");
            return (Criteria) this;
        }

        public Criteria andTeam1ComeFromNotBetween(Integer value1, Integer value2) {
            addCriterion("team1_come_from not between", value1, value2, "team1ComeFrom");
            return (Criteria) this;
        }

        public Criteria andTeam2ComeFromIsNull() {
            addCriterion("team2_come_from is null");
            return (Criteria) this;
        }

        public Criteria andTeam2ComeFromIsNotNull() {
            addCriterion("team2_come_from is not null");
            return (Criteria) this;
        }

        public Criteria andTeam2ComeFromEqualTo(Integer value) {
            addCriterion("team2_come_from =", value, "team2ComeFrom");
            return (Criteria) this;
        }

        public Criteria andTeam2ComeFromNotEqualTo(Integer value) {
            addCriterion("team2_come_from <>", value, "team2ComeFrom");
            return (Criteria) this;
        }

        public Criteria andTeam2ComeFromGreaterThan(Integer value) {
            addCriterion("team2_come_from >", value, "team2ComeFrom");
            return (Criteria) this;
        }

        public Criteria andTeam2ComeFromGreaterThanOrEqualTo(Integer value) {
            addCriterion("team2_come_from >=", value, "team2ComeFrom");
            return (Criteria) this;
        }

        public Criteria andTeam2ComeFromLessThan(Integer value) {
            addCriterion("team2_come_from <", value, "team2ComeFrom");
            return (Criteria) this;
        }

        public Criteria andTeam2ComeFromLessThanOrEqualTo(Integer value) {
            addCriterion("team2_come_from <=", value, "team2ComeFrom");
            return (Criteria) this;
        }

        public Criteria andTeam2ComeFromIn(List<Integer> values) {
            addCriterion("team2_come_from in", values, "team2ComeFrom");
            return (Criteria) this;
        }

        public Criteria andTeam2ComeFromNotIn(List<Integer> values) {
            addCriterion("team2_come_from not in", values, "team2ComeFrom");
            return (Criteria) this;
        }

        public Criteria andTeam2ComeFromBetween(Integer value1, Integer value2) {
            addCriterion("team2_come_from between", value1, value2, "team2ComeFrom");
            return (Criteria) this;
        }

        public Criteria andTeam2ComeFromNotBetween(Integer value1, Integer value2) {
            addCriterion("team2_come_from not between", value1, value2, "team2ComeFrom");
            return (Criteria) this;
        }

        public Criteria andMatchIdsIsNull() {
            addCriterion("match_ids is null");
            return (Criteria) this;
        }

        public Criteria andMatchIdsIsNotNull() {
            addCriterion("match_ids is not null");
            return (Criteria) this;
        }

        public Criteria andMatchIdsEqualTo(String value) {
            addCriterion("match_ids =", value, "matchIds");
            return (Criteria) this;
        }

        public Criteria andMatchIdsNotEqualTo(String value) {
            addCriterion("match_ids <>", value, "matchIds");
            return (Criteria) this;
        }

        public Criteria andMatchIdsGreaterThan(String value) {
            addCriterion("match_ids >", value, "matchIds");
            return (Criteria) this;
        }

        public Criteria andMatchIdsGreaterThanOrEqualTo(String value) {
            addCriterion("match_ids >=", value, "matchIds");
            return (Criteria) this;
        }

        public Criteria andMatchIdsLessThan(String value) {
            addCriterion("match_ids <", value, "matchIds");
            return (Criteria) this;
        }

        public Criteria andMatchIdsLessThanOrEqualTo(String value) {
            addCriterion("match_ids <=", value, "matchIds");
            return (Criteria) this;
        }

        public Criteria andMatchIdsLike(String value) {
            addCriterion("match_ids like", value, "matchIds");
            return (Criteria) this;
        }

        public Criteria andMatchIdsNotLike(String value) {
            addCriterion("match_ids not like", value, "matchIds");
            return (Criteria) this;
        }

        public Criteria andMatchIdsIn(List<String> values) {
            addCriterion("match_ids in", values, "matchIds");
            return (Criteria) this;
        }

        public Criteria andMatchIdsNotIn(List<String> values) {
            addCriterion("match_ids not in", values, "matchIds");
            return (Criteria) this;
        }

        public Criteria andMatchIdsBetween(String value1, String value2) {
            addCriterion("match_ids between", value1, value2, "matchIds");
            return (Criteria) this;
        }

        public Criteria andMatchIdsNotBetween(String value1, String value2) {
            addCriterion("match_ids not between", value1, value2, "matchIds");
            return (Criteria) this;
        }

        public Criteria andRoundOrderIsNull() {
            addCriterion("round_order is null");
            return (Criteria) this;
        }

        public Criteria andRoundOrderIsNotNull() {
            addCriterion("round_order is not null");
            return (Criteria) this;
        }

        public Criteria andRoundOrderEqualTo(Integer value) {
            addCriterion("round_order =", value, "roundOrder");
            return (Criteria) this;
        }

        public Criteria andRoundOrderNotEqualTo(Integer value) {
            addCriterion("round_order <>", value, "roundOrder");
            return (Criteria) this;
        }

        public Criteria andRoundOrderGreaterThan(Integer value) {
            addCriterion("round_order >", value, "roundOrder");
            return (Criteria) this;
        }

        public Criteria andRoundOrderGreaterThanOrEqualTo(Integer value) {
            addCriterion("round_order >=", value, "roundOrder");
            return (Criteria) this;
        }

        public Criteria andRoundOrderLessThan(Integer value) {
            addCriterion("round_order <", value, "roundOrder");
            return (Criteria) this;
        }

        public Criteria andRoundOrderLessThanOrEqualTo(Integer value) {
            addCriterion("round_order <=", value, "roundOrder");
            return (Criteria) this;
        }

        public Criteria andRoundOrderIn(List<Integer> values) {
            addCriterion("round_order in", values, "roundOrder");
            return (Criteria) this;
        }

        public Criteria andRoundOrderNotIn(List<Integer> values) {
            addCriterion("round_order not in", values, "roundOrder");
            return (Criteria) this;
        }

        public Criteria andRoundOrderBetween(Integer value1, Integer value2) {
            addCriterion("round_order between", value1, value2, "roundOrder");
            return (Criteria) this;
        }

        public Criteria andRoundOrderNotBetween(Integer value1, Integer value2) {
            addCriterion("round_order not between", value1, value2, "roundOrder");
            return (Criteria) this;
        }

        public Criteria andLineOrderIsNull() {
            addCriterion("line_order is null");
            return (Criteria) this;
        }

        public Criteria andLineOrderIsNotNull() {
            addCriterion("line_order is not null");
            return (Criteria) this;
        }

        public Criteria andLineOrderEqualTo(Integer value) {
            addCriterion("line_order =", value, "lineOrder");
            return (Criteria) this;
        }

        public Criteria andLineOrderNotEqualTo(Integer value) {
            addCriterion("line_order <>", value, "lineOrder");
            return (Criteria) this;
        }

        public Criteria andLineOrderGreaterThan(Integer value) {
            addCriterion("line_order >", value, "lineOrder");
            return (Criteria) this;
        }

        public Criteria andLineOrderGreaterThanOrEqualTo(Integer value) {
            addCriterion("line_order >=", value, "lineOrder");
            return (Criteria) this;
        }

        public Criteria andLineOrderLessThan(Integer value) {
            addCriterion("line_order <", value, "lineOrder");
            return (Criteria) this;
        }

        public Criteria andLineOrderLessThanOrEqualTo(Integer value) {
            addCriterion("line_order <=", value, "lineOrder");
            return (Criteria) this;
        }

        public Criteria andLineOrderIn(List<Integer> values) {
            addCriterion("line_order in", values, "lineOrder");
            return (Criteria) this;
        }

        public Criteria andLineOrderNotIn(List<Integer> values) {
            addCriterion("line_order not in", values, "lineOrder");
            return (Criteria) this;
        }

        public Criteria andLineOrderBetween(Integer value1, Integer value2) {
            addCriterion("line_order between", value1, value2, "lineOrder");
            return (Criteria) this;
        }

        public Criteria andLineOrderNotBetween(Integer value1, Integer value2) {
            addCriterion("line_order not between", value1, value2, "lineOrder");
            return (Criteria) this;
        }

        public Criteria andDoubleEliminationGroupIsNull() {
            addCriterion("double_elimination_group is null");
            return (Criteria) this;
        }

        public Criteria andDoubleEliminationGroupIsNotNull() {
            addCriterion("double_elimination_group is not null");
            return (Criteria) this;
        }

        public Criteria andDoubleEliminationGroupEqualTo(Integer value) {
            addCriterion("double_elimination_group =", value, "doubleEliminationGroup");
            return (Criteria) this;
        }

        public Criteria andDoubleEliminationGroupNotEqualTo(Integer value) {
            addCriterion("double_elimination_group <>", value, "doubleEliminationGroup");
            return (Criteria) this;
        }

        public Criteria andDoubleEliminationGroupGreaterThan(Integer value) {
            addCriterion("double_elimination_group >", value, "doubleEliminationGroup");
            return (Criteria) this;
        }

        public Criteria andDoubleEliminationGroupGreaterThanOrEqualTo(Integer value) {
            addCriterion("double_elimination_group >=", value, "doubleEliminationGroup");
            return (Criteria) this;
        }

        public Criteria andDoubleEliminationGroupLessThan(Integer value) {
            addCriterion("double_elimination_group <", value, "doubleEliminationGroup");
            return (Criteria) this;
        }

        public Criteria andDoubleEliminationGroupLessThanOrEqualTo(Integer value) {
            addCriterion("double_elimination_group <=", value, "doubleEliminationGroup");
            return (Criteria) this;
        }

        public Criteria andDoubleEliminationGroupIn(List<Integer> values) {
            addCriterion("double_elimination_group in", values, "doubleEliminationGroup");
            return (Criteria) this;
        }

        public Criteria andDoubleEliminationGroupNotIn(List<Integer> values) {
            addCriterion("double_elimination_group not in", values, "doubleEliminationGroup");
            return (Criteria) this;
        }

        public Criteria andDoubleEliminationGroupBetween(Integer value1, Integer value2) {
            addCriterion("double_elimination_group between", value1, value2, "doubleEliminationGroup");
            return (Criteria) this;
        }

        public Criteria andDoubleEliminationGroupNotBetween(Integer value1, Integer value2) {
            addCriterion("double_elimination_group not between", value1, value2, "doubleEliminationGroup");
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

        public Criteria andWinnerIsNull() {
            addCriterion("winner is null");
            return (Criteria) this;
        }

        public Criteria andWinnerIsNotNull() {
            addCriterion("winner is not null");
            return (Criteria) this;
        }

        public Criteria andWinnerEqualTo(Integer value) {
            addCriterion("winner =", value, "winner");
            return (Criteria) this;
        }

        public Criteria andWinnerNotEqualTo(Integer value) {
            addCriterion("winner <>", value, "winner");
            return (Criteria) this;
        }

        public Criteria andWinnerGreaterThan(Integer value) {
            addCriterion("winner >", value, "winner");
            return (Criteria) this;
        }

        public Criteria andWinnerGreaterThanOrEqualTo(Integer value) {
            addCriterion("winner >=", value, "winner");
            return (Criteria) this;
        }

        public Criteria andWinnerLessThan(Integer value) {
            addCriterion("winner <", value, "winner");
            return (Criteria) this;
        }

        public Criteria andWinnerLessThanOrEqualTo(Integer value) {
            addCriterion("winner <=", value, "winner");
            return (Criteria) this;
        }

        public Criteria andWinnerIn(List<Integer> values) {
            addCriterion("winner in", values, "winner");
            return (Criteria) this;
        }

        public Criteria andWinnerNotIn(List<Integer> values) {
            addCriterion("winner not in", values, "winner");
            return (Criteria) this;
        }

        public Criteria andWinnerBetween(Integer value1, Integer value2) {
            addCriterion("winner between", value1, value2, "winner");
            return (Criteria) this;
        }

        public Criteria andWinnerNotBetween(Integer value1, Integer value2) {
            addCriterion("winner not between", value1, value2, "winner");
            return (Criteria) this;
        }

        public Criteria andRoundDescriptionIsNull() {
            addCriterion("round_description is null");
            return (Criteria) this;
        }

        public Criteria andRoundDescriptionIsNotNull() {
            addCriterion("round_description is not null");
            return (Criteria) this;
        }

        public Criteria andRoundDescriptionEqualTo(String value) {
            addCriterion("round_description =", value, "roundDescription");
            return (Criteria) this;
        }

        public Criteria andRoundDescriptionNotEqualTo(String value) {
            addCriterion("round_description <>", value, "roundDescription");
            return (Criteria) this;
        }

        public Criteria andRoundDescriptionGreaterThan(String value) {
            addCriterion("round_description >", value, "roundDescription");
            return (Criteria) this;
        }

        public Criteria andRoundDescriptionGreaterThanOrEqualTo(String value) {
            addCriterion("round_description >=", value, "roundDescription");
            return (Criteria) this;
        }

        public Criteria andRoundDescriptionLessThan(String value) {
            addCriterion("round_description <", value, "roundDescription");
            return (Criteria) this;
        }

        public Criteria andRoundDescriptionLessThanOrEqualTo(String value) {
            addCriterion("round_description <=", value, "roundDescription");
            return (Criteria) this;
        }

        public Criteria andRoundDescriptionLike(String value) {
            addCriterion("round_description like", value, "roundDescription");
            return (Criteria) this;
        }

        public Criteria andRoundDescriptionNotLike(String value) {
            addCriterion("round_description not like", value, "roundDescription");
            return (Criteria) this;
        }

        public Criteria andRoundDescriptionIn(List<String> values) {
            addCriterion("round_description in", values, "roundDescription");
            return (Criteria) this;
        }

        public Criteria andRoundDescriptionNotIn(List<String> values) {
            addCriterion("round_description not in", values, "roundDescription");
            return (Criteria) this;
        }

        public Criteria andRoundDescriptionBetween(String value1, String value2) {
            addCriterion("round_description between", value1, value2, "roundDescription");
            return (Criteria) this;
        }

        public Criteria andRoundDescriptionNotBetween(String value1, String value2) {
            addCriterion("round_description not between", value1, value2, "roundDescription");
            return (Criteria) this;
        }

        public Criteria andParentIdIsNull() {
            addCriterion("parent_id is null");
            return (Criteria) this;
        }

        public Criteria andParentIdIsNotNull() {
            addCriterion("parent_id is not null");
            return (Criteria) this;
        }

        public Criteria andParentIdEqualTo(Integer value) {
            addCriterion("parent_id =", value, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdNotEqualTo(Integer value) {
            addCriterion("parent_id <>", value, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdGreaterThan(Integer value) {
            addCriterion("parent_id >", value, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("parent_id >=", value, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdLessThan(Integer value) {
            addCriterion("parent_id <", value, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdLessThanOrEqualTo(Integer value) {
            addCriterion("parent_id <=", value, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdIn(List<Integer> values) {
            addCriterion("parent_id in", values, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdNotIn(List<Integer> values) {
            addCriterion("parent_id not in", values, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdBetween(Integer value1, Integer value2) {
            addCriterion("parent_id between", value1, value2, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdNotBetween(Integer value1, Integer value2) {
            addCriterion("parent_id not between", value1, value2, "parentId");
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

        public Criteria andMatchIdIsNull() {
            addCriterion("match_id is null");
            return (Criteria) this;
        }

        public Criteria andMatchIdIsNotNull() {
            addCriterion("match_id is not null");
            return (Criteria) this;
        }

        public Criteria andMatchIdEqualTo(String value) {
            addCriterion("match_id =", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdNotEqualTo(String value) {
            addCriterion("match_id <>", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdGreaterThan(String value) {
            addCriterion("match_id >", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdGreaterThanOrEqualTo(String value) {
            addCriterion("match_id >=", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdLessThan(String value) {
            addCriterion("match_id <", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdLessThanOrEqualTo(String value) {
            addCriterion("match_id <=", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdLike(String value) {
            addCriterion("match_id like", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdNotLike(String value) {
            addCriterion("match_id not like", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdIn(List<String> values) {
            addCriterion("match_id in", values, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdNotIn(List<String> values) {
            addCriterion("match_id not in", values, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdBetween(String value1, String value2) {
            addCriterion("match_id between", value1, value2, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdNotBetween(String value1, String value2) {
            addCriterion("match_id not between", value1, value2, "matchId");
            return (Criteria) this;
        }

        public Criteria andInvalidIsNull() {
            addCriterion("invalid is null");
            return (Criteria) this;
        }

        public Criteria andInvalidIsNotNull() {
            addCriterion("invalid is not null");
            return (Criteria) this;
        }

        public Criteria andInvalidEqualTo(Integer value) {
            addCriterion("invalid =", value, "invalid");
            return (Criteria) this;
        }

        public Criteria andInvalidNotEqualTo(Integer value) {
            addCriterion("invalid <>", value, "invalid");
            return (Criteria) this;
        }

        public Criteria andInvalidGreaterThan(Integer value) {
            addCriterion("invalid >", value, "invalid");
            return (Criteria) this;
        }

        public Criteria andInvalidGreaterThanOrEqualTo(Integer value) {
            addCriterion("invalid >=", value, "invalid");
            return (Criteria) this;
        }

        public Criteria andInvalidLessThan(Integer value) {
            addCriterion("invalid <", value, "invalid");
            return (Criteria) this;
        }

        public Criteria andInvalidLessThanOrEqualTo(Integer value) {
            addCriterion("invalid <=", value, "invalid");
            return (Criteria) this;
        }

        public Criteria andInvalidIn(List<Integer> values) {
            addCriterion("invalid in", values, "invalid");
            return (Criteria) this;
        }

        public Criteria andInvalidNotIn(List<Integer> values) {
            addCriterion("invalid not in", values, "invalid");
            return (Criteria) this;
        }

        public Criteria andInvalidBetween(Integer value1, Integer value2) {
            addCriterion("invalid between", value1, value2, "invalid");
            return (Criteria) this;
        }

        public Criteria andInvalidNotBetween(Integer value1, Integer value2) {
            addCriterion("invalid not between", value1, value2, "invalid");
            return (Criteria) this;
        }

        public Criteria andHomeTeamNormalTimeScoreIsNull() {
            addCriterion("home_team_normal_time_score is null");
            return (Criteria) this;
        }

        public Criteria andHomeTeamNormalTimeScoreIsNotNull() {
            addCriterion("home_team_normal_time_score is not null");
            return (Criteria) this;
        }

        public Criteria andHomeTeamNormalTimeScoreEqualTo(String value) {
            addCriterion("home_team_normal_time_score =", value, "homeTeamNormalTimeScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamNormalTimeScoreNotEqualTo(String value) {
            addCriterion("home_team_normal_time_score <>", value, "homeTeamNormalTimeScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamNormalTimeScoreGreaterThan(String value) {
            addCriterion("home_team_normal_time_score >", value, "homeTeamNormalTimeScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamNormalTimeScoreGreaterThanOrEqualTo(String value) {
            addCriterion("home_team_normal_time_score >=", value, "homeTeamNormalTimeScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamNormalTimeScoreLessThan(String value) {
            addCriterion("home_team_normal_time_score <", value, "homeTeamNormalTimeScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamNormalTimeScoreLessThanOrEqualTo(String value) {
            addCriterion("home_team_normal_time_score <=", value, "homeTeamNormalTimeScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamNormalTimeScoreLike(String value) {
            addCriterion("home_team_normal_time_score like", value, "homeTeamNormalTimeScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamNormalTimeScoreNotLike(String value) {
            addCriterion("home_team_normal_time_score not like", value, "homeTeamNormalTimeScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamNormalTimeScoreIn(List<String> values) {
            addCriterion("home_team_normal_time_score in", values, "homeTeamNormalTimeScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamNormalTimeScoreNotIn(List<String> values) {
            addCriterion("home_team_normal_time_score not in", values, "homeTeamNormalTimeScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamNormalTimeScoreBetween(String value1, String value2) {
            addCriterion("home_team_normal_time_score between", value1, value2, "homeTeamNormalTimeScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamNormalTimeScoreNotBetween(String value1, String value2) {
            addCriterion("home_team_normal_time_score not between", value1, value2, "homeTeamNormalTimeScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamNormalTimeScoreIsNull() {
            addCriterion("away_team_normal_time_score is null");
            return (Criteria) this;
        }

        public Criteria andAwayTeamNormalTimeScoreIsNotNull() {
            addCriterion("away_team_normal_time_score is not null");
            return (Criteria) this;
        }

        public Criteria andAwayTeamNormalTimeScoreEqualTo(String value) {
            addCriterion("away_team_normal_time_score =", value, "awayTeamNormalTimeScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamNormalTimeScoreNotEqualTo(String value) {
            addCriterion("away_team_normal_time_score <>", value, "awayTeamNormalTimeScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamNormalTimeScoreGreaterThan(String value) {
            addCriterion("away_team_normal_time_score >", value, "awayTeamNormalTimeScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamNormalTimeScoreGreaterThanOrEqualTo(String value) {
            addCriterion("away_team_normal_time_score >=", value, "awayTeamNormalTimeScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamNormalTimeScoreLessThan(String value) {
            addCriterion("away_team_normal_time_score <", value, "awayTeamNormalTimeScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamNormalTimeScoreLessThanOrEqualTo(String value) {
            addCriterion("away_team_normal_time_score <=", value, "awayTeamNormalTimeScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamNormalTimeScoreLike(String value) {
            addCriterion("away_team_normal_time_score like", value, "awayTeamNormalTimeScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamNormalTimeScoreNotLike(String value) {
            addCriterion("away_team_normal_time_score not like", value, "awayTeamNormalTimeScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamNormalTimeScoreIn(List<String> values) {
            addCriterion("away_team_normal_time_score in", values, "awayTeamNormalTimeScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamNormalTimeScoreNotIn(List<String> values) {
            addCriterion("away_team_normal_time_score not in", values, "awayTeamNormalTimeScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamNormalTimeScoreBetween(String value1, String value2) {
            addCriterion("away_team_normal_time_score between", value1, value2, "awayTeamNormalTimeScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamNormalTimeScoreNotBetween(String value1, String value2) {
            addCriterion("away_team_normal_time_score not between", value1, value2, "awayTeamNormalTimeScore");
            return (Criteria) this;
        }

        public Criteria andHomeExtraTimeFirstHalfScoreIsNull() {
            addCriterion("home_extra_time_first_half_score is null");
            return (Criteria) this;
        }

        public Criteria andHomeExtraTimeFirstHalfScoreIsNotNull() {
            addCriterion("home_extra_time_first_half_score is not null");
            return (Criteria) this;
        }

        public Criteria andHomeExtraTimeFirstHalfScoreEqualTo(String value) {
            addCriterion("home_extra_time_first_half_score =", value, "homeExtraTimeFirstHalfScore");
            return (Criteria) this;
        }

        public Criteria andHomeExtraTimeFirstHalfScoreNotEqualTo(String value) {
            addCriterion("home_extra_time_first_half_score <>", value, "homeExtraTimeFirstHalfScore");
            return (Criteria) this;
        }

        public Criteria andHomeExtraTimeFirstHalfScoreGreaterThan(String value) {
            addCriterion("home_extra_time_first_half_score >", value, "homeExtraTimeFirstHalfScore");
            return (Criteria) this;
        }

        public Criteria andHomeExtraTimeFirstHalfScoreGreaterThanOrEqualTo(String value) {
            addCriterion("home_extra_time_first_half_score >=", value, "homeExtraTimeFirstHalfScore");
            return (Criteria) this;
        }

        public Criteria andHomeExtraTimeFirstHalfScoreLessThan(String value) {
            addCriterion("home_extra_time_first_half_score <", value, "homeExtraTimeFirstHalfScore");
            return (Criteria) this;
        }

        public Criteria andHomeExtraTimeFirstHalfScoreLessThanOrEqualTo(String value) {
            addCriterion("home_extra_time_first_half_score <=", value, "homeExtraTimeFirstHalfScore");
            return (Criteria) this;
        }

        public Criteria andHomeExtraTimeFirstHalfScoreLike(String value) {
            addCriterion("home_extra_time_first_half_score like", value, "homeExtraTimeFirstHalfScore");
            return (Criteria) this;
        }

        public Criteria andHomeExtraTimeFirstHalfScoreNotLike(String value) {
            addCriterion("home_extra_time_first_half_score not like", value, "homeExtraTimeFirstHalfScore");
            return (Criteria) this;
        }

        public Criteria andHomeExtraTimeFirstHalfScoreIn(List<String> values) {
            addCriterion("home_extra_time_first_half_score in", values, "homeExtraTimeFirstHalfScore");
            return (Criteria) this;
        }

        public Criteria andHomeExtraTimeFirstHalfScoreNotIn(List<String> values) {
            addCriterion("home_extra_time_first_half_score not in", values, "homeExtraTimeFirstHalfScore");
            return (Criteria) this;
        }

        public Criteria andHomeExtraTimeFirstHalfScoreBetween(String value1, String value2) {
            addCriterion("home_extra_time_first_half_score between", value1, value2, "homeExtraTimeFirstHalfScore");
            return (Criteria) this;
        }

        public Criteria andHomeExtraTimeFirstHalfScoreNotBetween(String value1, String value2) {
            addCriterion("home_extra_time_first_half_score not between", value1, value2, "homeExtraTimeFirstHalfScore");
            return (Criteria) this;
        }

        public Criteria andAwayExtraTimeFirstHalfScoreIsNull() {
            addCriterion("away_extra_time_first_half_score is null");
            return (Criteria) this;
        }

        public Criteria andAwayExtraTimeFirstHalfScoreIsNotNull() {
            addCriterion("away_extra_time_first_half_score is not null");
            return (Criteria) this;
        }

        public Criteria andAwayExtraTimeFirstHalfScoreEqualTo(String value) {
            addCriterion("away_extra_time_first_half_score =", value, "awayExtraTimeFirstHalfScore");
            return (Criteria) this;
        }

        public Criteria andAwayExtraTimeFirstHalfScoreNotEqualTo(String value) {
            addCriterion("away_extra_time_first_half_score <>", value, "awayExtraTimeFirstHalfScore");
            return (Criteria) this;
        }

        public Criteria andAwayExtraTimeFirstHalfScoreGreaterThan(String value) {
            addCriterion("away_extra_time_first_half_score >", value, "awayExtraTimeFirstHalfScore");
            return (Criteria) this;
        }

        public Criteria andAwayExtraTimeFirstHalfScoreGreaterThanOrEqualTo(String value) {
            addCriterion("away_extra_time_first_half_score >=", value, "awayExtraTimeFirstHalfScore");
            return (Criteria) this;
        }

        public Criteria andAwayExtraTimeFirstHalfScoreLessThan(String value) {
            addCriterion("away_extra_time_first_half_score <", value, "awayExtraTimeFirstHalfScore");
            return (Criteria) this;
        }

        public Criteria andAwayExtraTimeFirstHalfScoreLessThanOrEqualTo(String value) {
            addCriterion("away_extra_time_first_half_score <=", value, "awayExtraTimeFirstHalfScore");
            return (Criteria) this;
        }

        public Criteria andAwayExtraTimeFirstHalfScoreLike(String value) {
            addCriterion("away_extra_time_first_half_score like", value, "awayExtraTimeFirstHalfScore");
            return (Criteria) this;
        }

        public Criteria andAwayExtraTimeFirstHalfScoreNotLike(String value) {
            addCriterion("away_extra_time_first_half_score not like", value, "awayExtraTimeFirstHalfScore");
            return (Criteria) this;
        }

        public Criteria andAwayExtraTimeFirstHalfScoreIn(List<String> values) {
            addCriterion("away_extra_time_first_half_score in", values, "awayExtraTimeFirstHalfScore");
            return (Criteria) this;
        }

        public Criteria andAwayExtraTimeFirstHalfScoreNotIn(List<String> values) {
            addCriterion("away_extra_time_first_half_score not in", values, "awayExtraTimeFirstHalfScore");
            return (Criteria) this;
        }

        public Criteria andAwayExtraTimeFirstHalfScoreBetween(String value1, String value2) {
            addCriterion("away_extra_time_first_half_score between", value1, value2, "awayExtraTimeFirstHalfScore");
            return (Criteria) this;
        }

        public Criteria andAwayExtraTimeFirstHalfScoreNotBetween(String value1, String value2) {
            addCriterion("away_extra_time_first_half_score not between", value1, value2, "awayExtraTimeFirstHalfScore");
            return (Criteria) this;
        }

        public Criteria andHomeExtraTimeSecondHalfScoreIsNull() {
            addCriterion("home_extra_time_second_half_score is null");
            return (Criteria) this;
        }

        public Criteria andHomeExtraTimeSecondHalfScoreIsNotNull() {
            addCriterion("home_extra_time_second_half_score is not null");
            return (Criteria) this;
        }

        public Criteria andHomeExtraTimeSecondHalfScoreEqualTo(String value) {
            addCriterion("home_extra_time_second_half_score =", value, "homeExtraTimeSecondHalfScore");
            return (Criteria) this;
        }

        public Criteria andHomeExtraTimeSecondHalfScoreNotEqualTo(String value) {
            addCriterion("home_extra_time_second_half_score <>", value, "homeExtraTimeSecondHalfScore");
            return (Criteria) this;
        }

        public Criteria andHomeExtraTimeSecondHalfScoreGreaterThan(String value) {
            addCriterion("home_extra_time_second_half_score >", value, "homeExtraTimeSecondHalfScore");
            return (Criteria) this;
        }

        public Criteria andHomeExtraTimeSecondHalfScoreGreaterThanOrEqualTo(String value) {
            addCriterion("home_extra_time_second_half_score >=", value, "homeExtraTimeSecondHalfScore");
            return (Criteria) this;
        }

        public Criteria andHomeExtraTimeSecondHalfScoreLessThan(String value) {
            addCriterion("home_extra_time_second_half_score <", value, "homeExtraTimeSecondHalfScore");
            return (Criteria) this;
        }

        public Criteria andHomeExtraTimeSecondHalfScoreLessThanOrEqualTo(String value) {
            addCriterion("home_extra_time_second_half_score <=", value, "homeExtraTimeSecondHalfScore");
            return (Criteria) this;
        }

        public Criteria andHomeExtraTimeSecondHalfScoreLike(String value) {
            addCriterion("home_extra_time_second_half_score like", value, "homeExtraTimeSecondHalfScore");
            return (Criteria) this;
        }

        public Criteria andHomeExtraTimeSecondHalfScoreNotLike(String value) {
            addCriterion("home_extra_time_second_half_score not like", value, "homeExtraTimeSecondHalfScore");
            return (Criteria) this;
        }

        public Criteria andHomeExtraTimeSecondHalfScoreIn(List<String> values) {
            addCriterion("home_extra_time_second_half_score in", values, "homeExtraTimeSecondHalfScore");
            return (Criteria) this;
        }

        public Criteria andHomeExtraTimeSecondHalfScoreNotIn(List<String> values) {
            addCriterion("home_extra_time_second_half_score not in", values, "homeExtraTimeSecondHalfScore");
            return (Criteria) this;
        }

        public Criteria andHomeExtraTimeSecondHalfScoreBetween(String value1, String value2) {
            addCriterion("home_extra_time_second_half_score between", value1, value2, "homeExtraTimeSecondHalfScore");
            return (Criteria) this;
        }

        public Criteria andHomeExtraTimeSecondHalfScoreNotBetween(String value1, String value2) {
            addCriterion("home_extra_time_second_half_score not between", value1, value2, "homeExtraTimeSecondHalfScore");
            return (Criteria) this;
        }

        public Criteria andAwayExtraTimeSecondHalfScoreIsNull() {
            addCriterion("away_extra_time_second_half_score is null");
            return (Criteria) this;
        }

        public Criteria andAwayExtraTimeSecondHalfScoreIsNotNull() {
            addCriterion("away_extra_time_second_half_score is not null");
            return (Criteria) this;
        }

        public Criteria andAwayExtraTimeSecondHalfScoreEqualTo(String value) {
            addCriterion("away_extra_time_second_half_score =", value, "awayExtraTimeSecondHalfScore");
            return (Criteria) this;
        }

        public Criteria andAwayExtraTimeSecondHalfScoreNotEqualTo(String value) {
            addCriterion("away_extra_time_second_half_score <>", value, "awayExtraTimeSecondHalfScore");
            return (Criteria) this;
        }

        public Criteria andAwayExtraTimeSecondHalfScoreGreaterThan(String value) {
            addCriterion("away_extra_time_second_half_score >", value, "awayExtraTimeSecondHalfScore");
            return (Criteria) this;
        }

        public Criteria andAwayExtraTimeSecondHalfScoreGreaterThanOrEqualTo(String value) {
            addCriterion("away_extra_time_second_half_score >=", value, "awayExtraTimeSecondHalfScore");
            return (Criteria) this;
        }

        public Criteria andAwayExtraTimeSecondHalfScoreLessThan(String value) {
            addCriterion("away_extra_time_second_half_score <", value, "awayExtraTimeSecondHalfScore");
            return (Criteria) this;
        }

        public Criteria andAwayExtraTimeSecondHalfScoreLessThanOrEqualTo(String value) {
            addCriterion("away_extra_time_second_half_score <=", value, "awayExtraTimeSecondHalfScore");
            return (Criteria) this;
        }

        public Criteria andAwayExtraTimeSecondHalfScoreLike(String value) {
            addCriterion("away_extra_time_second_half_score like", value, "awayExtraTimeSecondHalfScore");
            return (Criteria) this;
        }

        public Criteria andAwayExtraTimeSecondHalfScoreNotLike(String value) {
            addCriterion("away_extra_time_second_half_score not like", value, "awayExtraTimeSecondHalfScore");
            return (Criteria) this;
        }

        public Criteria andAwayExtraTimeSecondHalfScoreIn(List<String> values) {
            addCriterion("away_extra_time_second_half_score in", values, "awayExtraTimeSecondHalfScore");
            return (Criteria) this;
        }

        public Criteria andAwayExtraTimeSecondHalfScoreNotIn(List<String> values) {
            addCriterion("away_extra_time_second_half_score not in", values, "awayExtraTimeSecondHalfScore");
            return (Criteria) this;
        }

        public Criteria andAwayExtraTimeSecondHalfScoreBetween(String value1, String value2) {
            addCriterion("away_extra_time_second_half_score between", value1, value2, "awayExtraTimeSecondHalfScore");
            return (Criteria) this;
        }

        public Criteria andAwayExtraTimeSecondHalfScoreNotBetween(String value1, String value2) {
            addCriterion("away_extra_time_second_half_score not between", value1, value2, "awayExtraTimeSecondHalfScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamHalfTimeScoreIsNull() {
            addCriterion("home_team_half_time_score is null");
            return (Criteria) this;
        }

        public Criteria andHomeTeamHalfTimeScoreIsNotNull() {
            addCriterion("home_team_half_time_score is not null");
            return (Criteria) this;
        }

        public Criteria andHomeTeamHalfTimeScoreEqualTo(String value) {
            addCriterion("home_team_half_time_score =", value, "homeTeamHalfTimeScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamHalfTimeScoreNotEqualTo(String value) {
            addCriterion("home_team_half_time_score <>", value, "homeTeamHalfTimeScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamHalfTimeScoreGreaterThan(String value) {
            addCriterion("home_team_half_time_score >", value, "homeTeamHalfTimeScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamHalfTimeScoreGreaterThanOrEqualTo(String value) {
            addCriterion("home_team_half_time_score >=", value, "homeTeamHalfTimeScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamHalfTimeScoreLessThan(String value) {
            addCriterion("home_team_half_time_score <", value, "homeTeamHalfTimeScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamHalfTimeScoreLessThanOrEqualTo(String value) {
            addCriterion("home_team_half_time_score <=", value, "homeTeamHalfTimeScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamHalfTimeScoreLike(String value) {
            addCriterion("home_team_half_time_score like", value, "homeTeamHalfTimeScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamHalfTimeScoreNotLike(String value) {
            addCriterion("home_team_half_time_score not like", value, "homeTeamHalfTimeScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamHalfTimeScoreIn(List<String> values) {
            addCriterion("home_team_half_time_score in", values, "homeTeamHalfTimeScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamHalfTimeScoreNotIn(List<String> values) {
            addCriterion("home_team_half_time_score not in", values, "homeTeamHalfTimeScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamHalfTimeScoreBetween(String value1, String value2) {
            addCriterion("home_team_half_time_score between", value1, value2, "homeTeamHalfTimeScore");
            return (Criteria) this;
        }

        public Criteria andHomeTeamHalfTimeScoreNotBetween(String value1, String value2) {
            addCriterion("home_team_half_time_score not between", value1, value2, "homeTeamHalfTimeScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamHalfTimeScoreIsNull() {
            addCriterion("away_team_half_time_score is null");
            return (Criteria) this;
        }

        public Criteria andAwayTeamHalfTimeScoreIsNotNull() {
            addCriterion("away_team_half_time_score is not null");
            return (Criteria) this;
        }

        public Criteria andAwayTeamHalfTimeScoreEqualTo(String value) {
            addCriterion("away_team_half_time_score =", value, "awayTeamHalfTimeScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamHalfTimeScoreNotEqualTo(String value) {
            addCriterion("away_team_half_time_score <>", value, "awayTeamHalfTimeScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamHalfTimeScoreGreaterThan(String value) {
            addCriterion("away_team_half_time_score >", value, "awayTeamHalfTimeScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamHalfTimeScoreGreaterThanOrEqualTo(String value) {
            addCriterion("away_team_half_time_score >=", value, "awayTeamHalfTimeScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamHalfTimeScoreLessThan(String value) {
            addCriterion("away_team_half_time_score <", value, "awayTeamHalfTimeScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamHalfTimeScoreLessThanOrEqualTo(String value) {
            addCriterion("away_team_half_time_score <=", value, "awayTeamHalfTimeScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamHalfTimeScoreLike(String value) {
            addCriterion("away_team_half_time_score like", value, "awayTeamHalfTimeScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamHalfTimeScoreNotLike(String value) {
            addCriterion("away_team_half_time_score not like", value, "awayTeamHalfTimeScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamHalfTimeScoreIn(List<String> values) {
            addCriterion("away_team_half_time_score in", values, "awayTeamHalfTimeScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamHalfTimeScoreNotIn(List<String> values) {
            addCriterion("away_team_half_time_score not in", values, "awayTeamHalfTimeScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamHalfTimeScoreBetween(String value1, String value2) {
            addCriterion("away_team_half_time_score between", value1, value2, "awayTeamHalfTimeScore");
            return (Criteria) this;
        }

        public Criteria andAwayTeamHalfTimeScoreNotBetween(String value1, String value2) {
            addCriterion("away_team_half_time_score not between", value1, value2, "awayTeamHalfTimeScore");
            return (Criteria) this;
        }

        public Criteria andIsCurrentSeasonIsNull() {
            addCriterion("is_current_season is null");
            return (Criteria) this;
        }

        public Criteria andIsCurrentSeasonIsNotNull() {
            addCriterion("is_current_season is not null");
            return (Criteria) this;
        }

        public Criteria andIsCurrentSeasonEqualTo(Integer value) {
            addCriterion("is_current_season =", value, "isCurrentSeason");
            return (Criteria) this;
        }

        public Criteria andIsCurrentSeasonNotEqualTo(Integer value) {
            addCriterion("is_current_season <>", value, "isCurrentSeason");
            return (Criteria) this;
        }

        public Criteria andIsCurrentSeasonGreaterThan(Integer value) {
            addCriterion("is_current_season >", value, "isCurrentSeason");
            return (Criteria) this;
        }

        public Criteria andIsCurrentSeasonGreaterThanOrEqualTo(Integer value) {
            addCriterion("is_current_season >=", value, "isCurrentSeason");
            return (Criteria) this;
        }

        public Criteria andIsCurrentSeasonLessThan(Integer value) {
            addCriterion("is_current_season <", value, "isCurrentSeason");
            return (Criteria) this;
        }

        public Criteria andIsCurrentSeasonLessThanOrEqualTo(Integer value) {
            addCriterion("is_current_season <=", value, "isCurrentSeason");
            return (Criteria) this;
        }

        public Criteria andIsCurrentSeasonIn(List<Integer> values) {
            addCriterion("is_current_season in", values, "isCurrentSeason");
            return (Criteria) this;
        }

        public Criteria andIsCurrentSeasonNotIn(List<Integer> values) {
            addCriterion("is_current_season not in", values, "isCurrentSeason");
            return (Criteria) this;
        }

        public Criteria andIsCurrentSeasonBetween(Integer value1, Integer value2) {
            addCriterion("is_current_season between", value1, value2, "isCurrentSeason");
            return (Criteria) this;
        }

        public Criteria andIsCurrentSeasonNotBetween(Integer value1, Integer value2) {
            addCriterion("is_current_season not between", value1, value2, "isCurrentSeason");
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