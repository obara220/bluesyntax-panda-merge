package com.panda.merge.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ThirdMatchSeasonStatisticsExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ThirdMatchSeasonStatisticsExample() {
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

        public Criteria andThirdSourceSeasonIdIsNull() {
            addCriterion("third_source_season_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonIdIsNotNull() {
            addCriterion("third_source_season_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonIdEqualTo(String value) {
            addCriterion("third_source_season_id =", value, "thirdSourceSeasonId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonIdNotEqualTo(String value) {
            addCriterion("third_source_season_id <>", value, "thirdSourceSeasonId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonIdGreaterThan(String value) {
            addCriterion("third_source_season_id >", value, "thirdSourceSeasonId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonIdGreaterThanOrEqualTo(String value) {
            addCriterion("third_source_season_id >=", value, "thirdSourceSeasonId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonIdLessThan(String value) {
            addCriterion("third_source_season_id <", value, "thirdSourceSeasonId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonIdLessThanOrEqualTo(String value) {
            addCriterion("third_source_season_id <=", value, "thirdSourceSeasonId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonIdLike(String value) {
            addCriterion("third_source_season_id like", value, "thirdSourceSeasonId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonIdNotLike(String value) {
            addCriterion("third_source_season_id not like", value, "thirdSourceSeasonId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonIdIn(List<String> values) {
            addCriterion("third_source_season_id in", values, "thirdSourceSeasonId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonIdNotIn(List<String> values) {
            addCriterion("third_source_season_id not in", values, "thirdSourceSeasonId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonIdBetween(String value1, String value2) {
            addCriterion("third_source_season_id between", value1, value2, "thirdSourceSeasonId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonIdNotBetween(String value1, String value2) {
            addCriterion("third_source_season_id not between", value1, value2, "thirdSourceSeasonId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonNameIsNull() {
            addCriterion("third_source_season_name is null");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonNameIsNotNull() {
            addCriterion("third_source_season_name is not null");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonNameEqualTo(String value) {
            addCriterion("third_source_season_name =", value, "thirdSourceSeasonName");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonNameNotEqualTo(String value) {
            addCriterion("third_source_season_name <>", value, "thirdSourceSeasonName");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonNameGreaterThan(String value) {
            addCriterion("third_source_season_name >", value, "thirdSourceSeasonName");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonNameGreaterThanOrEqualTo(String value) {
            addCriterion("third_source_season_name >=", value, "thirdSourceSeasonName");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonNameLessThan(String value) {
            addCriterion("third_source_season_name <", value, "thirdSourceSeasonName");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonNameLessThanOrEqualTo(String value) {
            addCriterion("third_source_season_name <=", value, "thirdSourceSeasonName");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonNameLike(String value) {
            addCriterion("third_source_season_name like", value, "thirdSourceSeasonName");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonNameNotLike(String value) {
            addCriterion("third_source_season_name not like", value, "thirdSourceSeasonName");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonNameIn(List<String> values) {
            addCriterion("third_source_season_name in", values, "thirdSourceSeasonName");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonNameNotIn(List<String> values) {
            addCriterion("third_source_season_name not in", values, "thirdSourceSeasonName");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonNameBetween(String value1, String value2) {
            addCriterion("third_source_season_name between", value1, value2, "thirdSourceSeasonName");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonNameNotBetween(String value1, String value2) {
            addCriterion("third_source_season_name not between", value1, value2, "thirdSourceSeasonName");
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

        public Criteria andPercentThanOneIsNull() {
            addCriterion("percent_than_one is null");
            return (Criteria) this;
        }

        public Criteria andPercentThanOneIsNotNull() {
            addCriterion("percent_than_one is not null");
            return (Criteria) this;
        }

        public Criteria andPercentThanOneEqualTo(BigDecimal value) {
            addCriterion("percent_than_one =", value, "percentThanOne");
            return (Criteria) this;
        }

        public Criteria andPercentThanOneNotEqualTo(BigDecimal value) {
            addCriterion("percent_than_one <>", value, "percentThanOne");
            return (Criteria) this;
        }

        public Criteria andPercentThanOneGreaterThan(BigDecimal value) {
            addCriterion("percent_than_one >", value, "percentThanOne");
            return (Criteria) this;
        }

        public Criteria andPercentThanOneGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("percent_than_one >=", value, "percentThanOne");
            return (Criteria) this;
        }

        public Criteria andPercentThanOneLessThan(BigDecimal value) {
            addCriterion("percent_than_one <", value, "percentThanOne");
            return (Criteria) this;
        }

        public Criteria andPercentThanOneLessThanOrEqualTo(BigDecimal value) {
            addCriterion("percent_than_one <=", value, "percentThanOne");
            return (Criteria) this;
        }

        public Criteria andPercentThanOneIn(List<BigDecimal> values) {
            addCriterion("percent_than_one in", values, "percentThanOne");
            return (Criteria) this;
        }

        public Criteria andPercentThanOneNotIn(List<BigDecimal> values) {
            addCriterion("percent_than_one not in", values, "percentThanOne");
            return (Criteria) this;
        }

        public Criteria andPercentThanOneBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("percent_than_one between", value1, value2, "percentThanOne");
            return (Criteria) this;
        }

        public Criteria andPercentThanOneNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("percent_than_one not between", value1, value2, "percentThanOne");
            return (Criteria) this;
        }

        public Criteria andPercentThanTwoIsNull() {
            addCriterion("percent_than_two is null");
            return (Criteria) this;
        }

        public Criteria andPercentThanTwoIsNotNull() {
            addCriterion("percent_than_two is not null");
            return (Criteria) this;
        }

        public Criteria andPercentThanTwoEqualTo(BigDecimal value) {
            addCriterion("percent_than_two =", value, "percentThanTwo");
            return (Criteria) this;
        }

        public Criteria andPercentThanTwoNotEqualTo(BigDecimal value) {
            addCriterion("percent_than_two <>", value, "percentThanTwo");
            return (Criteria) this;
        }

        public Criteria andPercentThanTwoGreaterThan(BigDecimal value) {
            addCriterion("percent_than_two >", value, "percentThanTwo");
            return (Criteria) this;
        }

        public Criteria andPercentThanTwoGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("percent_than_two >=", value, "percentThanTwo");
            return (Criteria) this;
        }

        public Criteria andPercentThanTwoLessThan(BigDecimal value) {
            addCriterion("percent_than_two <", value, "percentThanTwo");
            return (Criteria) this;
        }

        public Criteria andPercentThanTwoLessThanOrEqualTo(BigDecimal value) {
            addCriterion("percent_than_two <=", value, "percentThanTwo");
            return (Criteria) this;
        }

        public Criteria andPercentThanTwoIn(List<BigDecimal> values) {
            addCriterion("percent_than_two in", values, "percentThanTwo");
            return (Criteria) this;
        }

        public Criteria andPercentThanTwoNotIn(List<BigDecimal> values) {
            addCriterion("percent_than_two not in", values, "percentThanTwo");
            return (Criteria) this;
        }

        public Criteria andPercentThanTwoBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("percent_than_two between", value1, value2, "percentThanTwo");
            return (Criteria) this;
        }

        public Criteria andPercentThanTwoNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("percent_than_two not between", value1, value2, "percentThanTwo");
            return (Criteria) this;
        }

        public Criteria andPercentThanThreeIsNull() {
            addCriterion("percent_than_three is null");
            return (Criteria) this;
        }

        public Criteria andPercentThanThreeIsNotNull() {
            addCriterion("percent_than_three is not null");
            return (Criteria) this;
        }

        public Criteria andPercentThanThreeEqualTo(BigDecimal value) {
            addCriterion("percent_than_three =", value, "percentThanThree");
            return (Criteria) this;
        }

        public Criteria andPercentThanThreeNotEqualTo(BigDecimal value) {
            addCriterion("percent_than_three <>", value, "percentThanThree");
            return (Criteria) this;
        }

        public Criteria andPercentThanThreeGreaterThan(BigDecimal value) {
            addCriterion("percent_than_three >", value, "percentThanThree");
            return (Criteria) this;
        }

        public Criteria andPercentThanThreeGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("percent_than_three >=", value, "percentThanThree");
            return (Criteria) this;
        }

        public Criteria andPercentThanThreeLessThan(BigDecimal value) {
            addCriterion("percent_than_three <", value, "percentThanThree");
            return (Criteria) this;
        }

        public Criteria andPercentThanThreeLessThanOrEqualTo(BigDecimal value) {
            addCriterion("percent_than_three <=", value, "percentThanThree");
            return (Criteria) this;
        }

        public Criteria andPercentThanThreeIn(List<BigDecimal> values) {
            addCriterion("percent_than_three in", values, "percentThanThree");
            return (Criteria) this;
        }

        public Criteria andPercentThanThreeNotIn(List<BigDecimal> values) {
            addCriterion("percent_than_three not in", values, "percentThanThree");
            return (Criteria) this;
        }

        public Criteria andPercentThanThreeBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("percent_than_three between", value1, value2, "percentThanThree");
            return (Criteria) this;
        }

        public Criteria andPercentThanThreeNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("percent_than_three not between", value1, value2, "percentThanThree");
            return (Criteria) this;
        }

        public Criteria andAverageGoalIsNull() {
            addCriterion("average_goal is null");
            return (Criteria) this;
        }

        public Criteria andAverageGoalIsNotNull() {
            addCriterion("average_goal is not null");
            return (Criteria) this;
        }

        public Criteria andAverageGoalEqualTo(BigDecimal value) {
            addCriterion("average_goal =", value, "averageGoal");
            return (Criteria) this;
        }

        public Criteria andAverageGoalNotEqualTo(BigDecimal value) {
            addCriterion("average_goal <>", value, "averageGoal");
            return (Criteria) this;
        }

        public Criteria andAverageGoalGreaterThan(BigDecimal value) {
            addCriterion("average_goal >", value, "averageGoal");
            return (Criteria) this;
        }

        public Criteria andAverageGoalGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("average_goal >=", value, "averageGoal");
            return (Criteria) this;
        }

        public Criteria andAverageGoalLessThan(BigDecimal value) {
            addCriterion("average_goal <", value, "averageGoal");
            return (Criteria) this;
        }

        public Criteria andAverageGoalLessThanOrEqualTo(BigDecimal value) {
            addCriterion("average_goal <=", value, "averageGoal");
            return (Criteria) this;
        }

        public Criteria andAverageGoalIn(List<BigDecimal> values) {
            addCriterion("average_goal in", values, "averageGoal");
            return (Criteria) this;
        }

        public Criteria andAverageGoalNotIn(List<BigDecimal> values) {
            addCriterion("average_goal not in", values, "averageGoal");
            return (Criteria) this;
        }

        public Criteria andAverageGoalBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("average_goal between", value1, value2, "averageGoal");
            return (Criteria) this;
        }

        public Criteria andAverageGoalNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("average_goal not between", value1, value2, "averageGoal");
            return (Criteria) this;
        }

        public Criteria andAverageCardIsNull() {
            addCriterion("average_card is null");
            return (Criteria) this;
        }

        public Criteria andAverageCardIsNotNull() {
            addCriterion("average_card is not null");
            return (Criteria) this;
        }

        public Criteria andAverageCardEqualTo(BigDecimal value) {
            addCriterion("average_card =", value, "averageCard");
            return (Criteria) this;
        }

        public Criteria andAverageCardNotEqualTo(BigDecimal value) {
            addCriterion("average_card <>", value, "averageCard");
            return (Criteria) this;
        }

        public Criteria andAverageCardGreaterThan(BigDecimal value) {
            addCriterion("average_card >", value, "averageCard");
            return (Criteria) this;
        }

        public Criteria andAverageCardGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("average_card >=", value, "averageCard");
            return (Criteria) this;
        }

        public Criteria andAverageCardLessThan(BigDecimal value) {
            addCriterion("average_card <", value, "averageCard");
            return (Criteria) this;
        }

        public Criteria andAverageCardLessThanOrEqualTo(BigDecimal value) {
            addCriterion("average_card <=", value, "averageCard");
            return (Criteria) this;
        }

        public Criteria andAverageCardIn(List<BigDecimal> values) {
            addCriterion("average_card in", values, "averageCard");
            return (Criteria) this;
        }

        public Criteria andAverageCardNotIn(List<BigDecimal> values) {
            addCriterion("average_card not in", values, "averageCard");
            return (Criteria) this;
        }

        public Criteria andAverageCardBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("average_card between", value1, value2, "averageCard");
            return (Criteria) this;
        }

        public Criteria andAverageCardNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("average_card not between", value1, value2, "averageCard");
            return (Criteria) this;
        }

        public Criteria andAverageCornerIsNull() {
            addCriterion("average_corner is null");
            return (Criteria) this;
        }

        public Criteria andAverageCornerIsNotNull() {
            addCriterion("average_corner is not null");
            return (Criteria) this;
        }

        public Criteria andAverageCornerEqualTo(BigDecimal value) {
            addCriterion("average_corner =", value, "averageCorner");
            return (Criteria) this;
        }

        public Criteria andAverageCornerNotEqualTo(BigDecimal value) {
            addCriterion("average_corner <>", value, "averageCorner");
            return (Criteria) this;
        }

        public Criteria andAverageCornerGreaterThan(BigDecimal value) {
            addCriterion("average_corner >", value, "averageCorner");
            return (Criteria) this;
        }

        public Criteria andAverageCornerGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("average_corner >=", value, "averageCorner");
            return (Criteria) this;
        }

        public Criteria andAverageCornerLessThan(BigDecimal value) {
            addCriterion("average_corner <", value, "averageCorner");
            return (Criteria) this;
        }

        public Criteria andAverageCornerLessThanOrEqualTo(BigDecimal value) {
            addCriterion("average_corner <=", value, "averageCorner");
            return (Criteria) this;
        }

        public Criteria andAverageCornerIn(List<BigDecimal> values) {
            addCriterion("average_corner in", values, "averageCorner");
            return (Criteria) this;
        }

        public Criteria andAverageCornerNotIn(List<BigDecimal> values) {
            addCriterion("average_corner not in", values, "averageCorner");
            return (Criteria) this;
        }

        public Criteria andAverageCornerBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("average_corner between", value1, value2, "averageCorner");
            return (Criteria) this;
        }

        public Criteria andAverageCornerNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("average_corner not between", value1, value2, "averageCorner");
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