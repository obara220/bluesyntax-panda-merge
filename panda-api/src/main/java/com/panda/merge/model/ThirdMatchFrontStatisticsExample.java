package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class ThirdMatchFrontStatisticsExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ThirdMatchFrontStatisticsExample() {
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

        public Criteria andCountTotalIsNull() {
            addCriterion("count_total is null");
            return (Criteria) this;
        }

        public Criteria andCountTotalIsNotNull() {
            addCriterion("count_total is not null");
            return (Criteria) this;
        }

        public Criteria andCountTotalEqualTo(Integer value) {
            addCriterion("count_total =", value, "countTotal");
            return (Criteria) this;
        }

        public Criteria andCountTotalNotEqualTo(Integer value) {
            addCriterion("count_total <>", value, "countTotal");
            return (Criteria) this;
        }

        public Criteria andCountTotalGreaterThan(Integer value) {
            addCriterion("count_total >", value, "countTotal");
            return (Criteria) this;
        }

        public Criteria andCountTotalGreaterThanOrEqualTo(Integer value) {
            addCriterion("count_total >=", value, "countTotal");
            return (Criteria) this;
        }

        public Criteria andCountTotalLessThan(Integer value) {
            addCriterion("count_total <", value, "countTotal");
            return (Criteria) this;
        }

        public Criteria andCountTotalLessThanOrEqualTo(Integer value) {
            addCriterion("count_total <=", value, "countTotal");
            return (Criteria) this;
        }

        public Criteria andCountTotalIn(List<Integer> values) {
            addCriterion("count_total in", values, "countTotal");
            return (Criteria) this;
        }

        public Criteria andCountTotalNotIn(List<Integer> values) {
            addCriterion("count_total not in", values, "countTotal");
            return (Criteria) this;
        }

        public Criteria andCountTotalBetween(Integer value1, Integer value2) {
            addCriterion("count_total between", value1, value2, "countTotal");
            return (Criteria) this;
        }

        public Criteria andCountTotalNotBetween(Integer value1, Integer value2) {
            addCriterion("count_total not between", value1, value2, "countTotal");
            return (Criteria) this;
        }

        public Criteria andHomeWinIsNull() {
            addCriterion("home_win is null");
            return (Criteria) this;
        }

        public Criteria andHomeWinIsNotNull() {
            addCriterion("home_win is not null");
            return (Criteria) this;
        }

        public Criteria andHomeWinEqualTo(Integer value) {
            addCriterion("home_win =", value, "homeWin");
            return (Criteria) this;
        }

        public Criteria andHomeWinNotEqualTo(Integer value) {
            addCriterion("home_win <>", value, "homeWin");
            return (Criteria) this;
        }

        public Criteria andHomeWinGreaterThan(Integer value) {
            addCriterion("home_win >", value, "homeWin");
            return (Criteria) this;
        }

        public Criteria andHomeWinGreaterThanOrEqualTo(Integer value) {
            addCriterion("home_win >=", value, "homeWin");
            return (Criteria) this;
        }

        public Criteria andHomeWinLessThan(Integer value) {
            addCriterion("home_win <", value, "homeWin");
            return (Criteria) this;
        }

        public Criteria andHomeWinLessThanOrEqualTo(Integer value) {
            addCriterion("home_win <=", value, "homeWin");
            return (Criteria) this;
        }

        public Criteria andHomeWinIn(List<Integer> values) {
            addCriterion("home_win in", values, "homeWin");
            return (Criteria) this;
        }

        public Criteria andHomeWinNotIn(List<Integer> values) {
            addCriterion("home_win not in", values, "homeWin");
            return (Criteria) this;
        }

        public Criteria andHomeWinBetween(Integer value1, Integer value2) {
            addCriterion("home_win between", value1, value2, "homeWin");
            return (Criteria) this;
        }

        public Criteria andHomeWinNotBetween(Integer value1, Integer value2) {
            addCriterion("home_win not between", value1, value2, "homeWin");
            return (Criteria) this;
        }

        public Criteria andAwayWinIsNull() {
            addCriterion("away_win is null");
            return (Criteria) this;
        }

        public Criteria andAwayWinIsNotNull() {
            addCriterion("away_win is not null");
            return (Criteria) this;
        }

        public Criteria andAwayWinEqualTo(Integer value) {
            addCriterion("away_win =", value, "awayWin");
            return (Criteria) this;
        }

        public Criteria andAwayWinNotEqualTo(Integer value) {
            addCriterion("away_win <>", value, "awayWin");
            return (Criteria) this;
        }

        public Criteria andAwayWinGreaterThan(Integer value) {
            addCriterion("away_win >", value, "awayWin");
            return (Criteria) this;
        }

        public Criteria andAwayWinGreaterThanOrEqualTo(Integer value) {
            addCriterion("away_win >=", value, "awayWin");
            return (Criteria) this;
        }

        public Criteria andAwayWinLessThan(Integer value) {
            addCriterion("away_win <", value, "awayWin");
            return (Criteria) this;
        }

        public Criteria andAwayWinLessThanOrEqualTo(Integer value) {
            addCriterion("away_win <=", value, "awayWin");
            return (Criteria) this;
        }

        public Criteria andAwayWinIn(List<Integer> values) {
            addCriterion("away_win in", values, "awayWin");
            return (Criteria) this;
        }

        public Criteria andAwayWinNotIn(List<Integer> values) {
            addCriterion("away_win not in", values, "awayWin");
            return (Criteria) this;
        }

        public Criteria andAwayWinBetween(Integer value1, Integer value2) {
            addCriterion("away_win between", value1, value2, "awayWin");
            return (Criteria) this;
        }

        public Criteria andAwayWinNotBetween(Integer value1, Integer value2) {
            addCriterion("away_win not between", value1, value2, "awayWin");
            return (Criteria) this;
        }

        public Criteria andDogfallTotalIsNull() {
            addCriterion("dogfall_total is null");
            return (Criteria) this;
        }

        public Criteria andDogfallTotalIsNotNull() {
            addCriterion("dogfall_total is not null");
            return (Criteria) this;
        }

        public Criteria andDogfallTotalEqualTo(Integer value) {
            addCriterion("dogfall_total =", value, "dogfallTotal");
            return (Criteria) this;
        }

        public Criteria andDogfallTotalNotEqualTo(Integer value) {
            addCriterion("dogfall_total <>", value, "dogfallTotal");
            return (Criteria) this;
        }

        public Criteria andDogfallTotalGreaterThan(Integer value) {
            addCriterion("dogfall_total >", value, "dogfallTotal");
            return (Criteria) this;
        }

        public Criteria andDogfallTotalGreaterThanOrEqualTo(Integer value) {
            addCriterion("dogfall_total >=", value, "dogfallTotal");
            return (Criteria) this;
        }

        public Criteria andDogfallTotalLessThan(Integer value) {
            addCriterion("dogfall_total <", value, "dogfallTotal");
            return (Criteria) this;
        }

        public Criteria andDogfallTotalLessThanOrEqualTo(Integer value) {
            addCriterion("dogfall_total <=", value, "dogfallTotal");
            return (Criteria) this;
        }

        public Criteria andDogfallTotalIn(List<Integer> values) {
            addCriterion("dogfall_total in", values, "dogfallTotal");
            return (Criteria) this;
        }

        public Criteria andDogfallTotalNotIn(List<Integer> values) {
            addCriterion("dogfall_total not in", values, "dogfallTotal");
            return (Criteria) this;
        }

        public Criteria andDogfallTotalBetween(Integer value1, Integer value2) {
            addCriterion("dogfall_total between", value1, value2, "dogfallTotal");
            return (Criteria) this;
        }

        public Criteria andDogfallTotalNotBetween(Integer value1, Integer value2) {
            addCriterion("dogfall_total not between", value1, value2, "dogfallTotal");
            return (Criteria) this;
        }

        public Criteria andMoreThanOneIsNull() {
            addCriterion("more_than_one is null");
            return (Criteria) this;
        }

        public Criteria andMoreThanOneIsNotNull() {
            addCriterion("more_than_one is not null");
            return (Criteria) this;
        }

        public Criteria andMoreThanOneEqualTo(Integer value) {
            addCriterion("more_than_one =", value, "moreThanOne");
            return (Criteria) this;
        }

        public Criteria andMoreThanOneNotEqualTo(Integer value) {
            addCriterion("more_than_one <>", value, "moreThanOne");
            return (Criteria) this;
        }

        public Criteria andMoreThanOneGreaterThan(Integer value) {
            addCriterion("more_than_one >", value, "moreThanOne");
            return (Criteria) this;
        }

        public Criteria andMoreThanOneGreaterThanOrEqualTo(Integer value) {
            addCriterion("more_than_one >=", value, "moreThanOne");
            return (Criteria) this;
        }

        public Criteria andMoreThanOneLessThan(Integer value) {
            addCriterion("more_than_one <", value, "moreThanOne");
            return (Criteria) this;
        }

        public Criteria andMoreThanOneLessThanOrEqualTo(Integer value) {
            addCriterion("more_than_one <=", value, "moreThanOne");
            return (Criteria) this;
        }

        public Criteria andMoreThanOneIn(List<Integer> values) {
            addCriterion("more_than_one in", values, "moreThanOne");
            return (Criteria) this;
        }

        public Criteria andMoreThanOneNotIn(List<Integer> values) {
            addCriterion("more_than_one not in", values, "moreThanOne");
            return (Criteria) this;
        }

        public Criteria andMoreThanOneBetween(Integer value1, Integer value2) {
            addCriterion("more_than_one between", value1, value2, "moreThanOne");
            return (Criteria) this;
        }

        public Criteria andMoreThanOneNotBetween(Integer value1, Integer value2) {
            addCriterion("more_than_one not between", value1, value2, "moreThanOne");
            return (Criteria) this;
        }

        public Criteria andMoreThanTwoIsNull() {
            addCriterion("more_than_two is null");
            return (Criteria) this;
        }

        public Criteria andMoreThanTwoIsNotNull() {
            addCriterion("more_than_two is not null");
            return (Criteria) this;
        }

        public Criteria andMoreThanTwoEqualTo(Integer value) {
            addCriterion("more_than_two =", value, "moreThanTwo");
            return (Criteria) this;
        }

        public Criteria andMoreThanTwoNotEqualTo(Integer value) {
            addCriterion("more_than_two <>", value, "moreThanTwo");
            return (Criteria) this;
        }

        public Criteria andMoreThanTwoGreaterThan(Integer value) {
            addCriterion("more_than_two >", value, "moreThanTwo");
            return (Criteria) this;
        }

        public Criteria andMoreThanTwoGreaterThanOrEqualTo(Integer value) {
            addCriterion("more_than_two >=", value, "moreThanTwo");
            return (Criteria) this;
        }

        public Criteria andMoreThanTwoLessThan(Integer value) {
            addCriterion("more_than_two <", value, "moreThanTwo");
            return (Criteria) this;
        }

        public Criteria andMoreThanTwoLessThanOrEqualTo(Integer value) {
            addCriterion("more_than_two <=", value, "moreThanTwo");
            return (Criteria) this;
        }

        public Criteria andMoreThanTwoIn(List<Integer> values) {
            addCriterion("more_than_two in", values, "moreThanTwo");
            return (Criteria) this;
        }

        public Criteria andMoreThanTwoNotIn(List<Integer> values) {
            addCriterion("more_than_two not in", values, "moreThanTwo");
            return (Criteria) this;
        }

        public Criteria andMoreThanTwoBetween(Integer value1, Integer value2) {
            addCriterion("more_than_two between", value1, value2, "moreThanTwo");
            return (Criteria) this;
        }

        public Criteria andMoreThanTwoNotBetween(Integer value1, Integer value2) {
            addCriterion("more_than_two not between", value1, value2, "moreThanTwo");
            return (Criteria) this;
        }

        public Criteria andMoreThanThreeIsNull() {
            addCriterion("more_than_three is null");
            return (Criteria) this;
        }

        public Criteria andMoreThanThreeIsNotNull() {
            addCriterion("more_than_three is not null");
            return (Criteria) this;
        }

        public Criteria andMoreThanThreeEqualTo(Integer value) {
            addCriterion("more_than_three =", value, "moreThanThree");
            return (Criteria) this;
        }

        public Criteria andMoreThanThreeNotEqualTo(Integer value) {
            addCriterion("more_than_three <>", value, "moreThanThree");
            return (Criteria) this;
        }

        public Criteria andMoreThanThreeGreaterThan(Integer value) {
            addCriterion("more_than_three >", value, "moreThanThree");
            return (Criteria) this;
        }

        public Criteria andMoreThanThreeGreaterThanOrEqualTo(Integer value) {
            addCriterion("more_than_three >=", value, "moreThanThree");
            return (Criteria) this;
        }

        public Criteria andMoreThanThreeLessThan(Integer value) {
            addCriterion("more_than_three <", value, "moreThanThree");
            return (Criteria) this;
        }

        public Criteria andMoreThanThreeLessThanOrEqualTo(Integer value) {
            addCriterion("more_than_three <=", value, "moreThanThree");
            return (Criteria) this;
        }

        public Criteria andMoreThanThreeIn(List<Integer> values) {
            addCriterion("more_than_three in", values, "moreThanThree");
            return (Criteria) this;
        }

        public Criteria andMoreThanThreeNotIn(List<Integer> values) {
            addCriterion("more_than_three not in", values, "moreThanThree");
            return (Criteria) this;
        }

        public Criteria andMoreThanThreeBetween(Integer value1, Integer value2) {
            addCriterion("more_than_three between", value1, value2, "moreThanThree");
            return (Criteria) this;
        }

        public Criteria andMoreThanThreeNotBetween(Integer value1, Integer value2) {
            addCriterion("more_than_three not between", value1, value2, "moreThanThree");
            return (Criteria) this;
        }

        public Criteria andAllScoresIsNull() {
            addCriterion("all_scores is null");
            return (Criteria) this;
        }

        public Criteria andAllScoresIsNotNull() {
            addCriterion("all_scores is not null");
            return (Criteria) this;
        }

        public Criteria andAllScoresEqualTo(Integer value) {
            addCriterion("all_scores =", value, "allScores");
            return (Criteria) this;
        }

        public Criteria andAllScoresNotEqualTo(Integer value) {
            addCriterion("all_scores <>", value, "allScores");
            return (Criteria) this;
        }

        public Criteria andAllScoresGreaterThan(Integer value) {
            addCriterion("all_scores >", value, "allScores");
            return (Criteria) this;
        }

        public Criteria andAllScoresGreaterThanOrEqualTo(Integer value) {
            addCriterion("all_scores >=", value, "allScores");
            return (Criteria) this;
        }

        public Criteria andAllScoresLessThan(Integer value) {
            addCriterion("all_scores <", value, "allScores");
            return (Criteria) this;
        }

        public Criteria andAllScoresLessThanOrEqualTo(Integer value) {
            addCriterion("all_scores <=", value, "allScores");
            return (Criteria) this;
        }

        public Criteria andAllScoresIn(List<Integer> values) {
            addCriterion("all_scores in", values, "allScores");
            return (Criteria) this;
        }

        public Criteria andAllScoresNotIn(List<Integer> values) {
            addCriterion("all_scores not in", values, "allScores");
            return (Criteria) this;
        }

        public Criteria andAllScoresBetween(Integer value1, Integer value2) {
            addCriterion("all_scores between", value1, value2, "allScores");
            return (Criteria) this;
        }

        public Criteria andAllScoresNotBetween(Integer value1, Integer value2) {
            addCriterion("all_scores not between", value1, value2, "allScores");
            return (Criteria) this;
        }

        public Criteria andHomeNotLostIsNull() {
            addCriterion("home_not_lost is null");
            return (Criteria) this;
        }

        public Criteria andHomeNotLostIsNotNull() {
            addCriterion("home_not_lost is not null");
            return (Criteria) this;
        }

        public Criteria andHomeNotLostEqualTo(Integer value) {
            addCriterion("home_not_lost =", value, "homeNotLost");
            return (Criteria) this;
        }

        public Criteria andHomeNotLostNotEqualTo(Integer value) {
            addCriterion("home_not_lost <>", value, "homeNotLost");
            return (Criteria) this;
        }

        public Criteria andHomeNotLostGreaterThan(Integer value) {
            addCriterion("home_not_lost >", value, "homeNotLost");
            return (Criteria) this;
        }

        public Criteria andHomeNotLostGreaterThanOrEqualTo(Integer value) {
            addCriterion("home_not_lost >=", value, "homeNotLost");
            return (Criteria) this;
        }

        public Criteria andHomeNotLostLessThan(Integer value) {
            addCriterion("home_not_lost <", value, "homeNotLost");
            return (Criteria) this;
        }

        public Criteria andHomeNotLostLessThanOrEqualTo(Integer value) {
            addCriterion("home_not_lost <=", value, "homeNotLost");
            return (Criteria) this;
        }

        public Criteria andHomeNotLostIn(List<Integer> values) {
            addCriterion("home_not_lost in", values, "homeNotLost");
            return (Criteria) this;
        }

        public Criteria andHomeNotLostNotIn(List<Integer> values) {
            addCriterion("home_not_lost not in", values, "homeNotLost");
            return (Criteria) this;
        }

        public Criteria andHomeNotLostBetween(Integer value1, Integer value2) {
            addCriterion("home_not_lost between", value1, value2, "homeNotLost");
            return (Criteria) this;
        }

        public Criteria andHomeNotLostNotBetween(Integer value1, Integer value2) {
            addCriterion("home_not_lost not between", value1, value2, "homeNotLost");
            return (Criteria) this;
        }

        public Criteria andAwayNotLostIsNull() {
            addCriterion("away_not_lost is null");
            return (Criteria) this;
        }

        public Criteria andAwayNotLostIsNotNull() {
            addCriterion("away_not_lost is not null");
            return (Criteria) this;
        }

        public Criteria andAwayNotLostEqualTo(Integer value) {
            addCriterion("away_not_lost =", value, "awayNotLost");
            return (Criteria) this;
        }

        public Criteria andAwayNotLostNotEqualTo(Integer value) {
            addCriterion("away_not_lost <>", value, "awayNotLost");
            return (Criteria) this;
        }

        public Criteria andAwayNotLostGreaterThan(Integer value) {
            addCriterion("away_not_lost >", value, "awayNotLost");
            return (Criteria) this;
        }

        public Criteria andAwayNotLostGreaterThanOrEqualTo(Integer value) {
            addCriterion("away_not_lost >=", value, "awayNotLost");
            return (Criteria) this;
        }

        public Criteria andAwayNotLostLessThan(Integer value) {
            addCriterion("away_not_lost <", value, "awayNotLost");
            return (Criteria) this;
        }

        public Criteria andAwayNotLostLessThanOrEqualTo(Integer value) {
            addCriterion("away_not_lost <=", value, "awayNotLost");
            return (Criteria) this;
        }

        public Criteria andAwayNotLostIn(List<Integer> values) {
            addCriterion("away_not_lost in", values, "awayNotLost");
            return (Criteria) this;
        }

        public Criteria andAwayNotLostNotIn(List<Integer> values) {
            addCriterion("away_not_lost not in", values, "awayNotLost");
            return (Criteria) this;
        }

        public Criteria andAwayNotLostBetween(Integer value1, Integer value2) {
            addCriterion("away_not_lost between", value1, value2, "awayNotLost");
            return (Criteria) this;
        }

        public Criteria andAwayNotLostNotBetween(Integer value1, Integer value2) {
            addCriterion("away_not_lost not between", value1, value2, "awayNotLost");
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