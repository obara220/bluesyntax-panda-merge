package com.panda.merge.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ThirdSportTeamRankingExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ThirdSportTeamRankingExample() {
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

        public Criteria andEditStatusIsNull() {
            addCriterion("edit_status is null");
            return (Criteria) this;
        }

        public Criteria andEditStatusIsNotNull() {
            addCriterion("edit_status is not null");
            return (Criteria) this;
        }

        public Criteria andEditStatusEqualTo(Boolean value) {
            addCriterion("edit_status =", value, "editStatus");
            return (Criteria) this;
        }

        public Criteria andEditStatusNotEqualTo(Boolean value) {
            addCriterion("edit_status <>", value, "editStatus");
            return (Criteria) this;
        }

        public Criteria andEditStatusGreaterThan(Boolean value) {
            addCriterion("edit_status >", value, "editStatus");
            return (Criteria) this;
        }

        public Criteria andEditStatusGreaterThanOrEqualTo(Boolean value) {
            addCriterion("edit_status >=", value, "editStatus");
            return (Criteria) this;
        }

        public Criteria andEditStatusLessThan(Boolean value) {
            addCriterion("edit_status <", value, "editStatus");
            return (Criteria) this;
        }

        public Criteria andEditStatusLessThanOrEqualTo(Boolean value) {
            addCriterion("edit_status <=", value, "editStatus");
            return (Criteria) this;
        }

        public Criteria andEditStatusIn(List<Boolean> values) {
            addCriterion("edit_status in", values, "editStatus");
            return (Criteria) this;
        }

        public Criteria andEditStatusNotIn(List<Boolean> values) {
            addCriterion("edit_status not in", values, "editStatus");
            return (Criteria) this;
        }

        public Criteria andEditStatusBetween(Boolean value1, Boolean value2) {
            addCriterion("edit_status between", value1, value2, "editStatus");
            return (Criteria) this;
        }

        public Criteria andEditStatusNotBetween(Boolean value1, Boolean value2) {
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

        public Criteria andThirdSourceSeasonBeginTimeIsNull() {
            addCriterion("third_source_season_begin_time is null");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonBeginTimeIsNotNull() {
            addCriterion("third_source_season_begin_time is not null");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonBeginTimeEqualTo(Date value) {
            addCriterion("third_source_season_begin_time =", value, "thirdSourceSeasonBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonBeginTimeNotEqualTo(Date value) {
            addCriterion("third_source_season_begin_time <>", value, "thirdSourceSeasonBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonBeginTimeGreaterThan(Date value) {
            addCriterion("third_source_season_begin_time >", value, "thirdSourceSeasonBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonBeginTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("third_source_season_begin_time >=", value, "thirdSourceSeasonBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonBeginTimeLessThan(Date value) {
            addCriterion("third_source_season_begin_time <", value, "thirdSourceSeasonBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonBeginTimeLessThanOrEqualTo(Date value) {
            addCriterion("third_source_season_begin_time <=", value, "thirdSourceSeasonBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonBeginTimeIn(List<Date> values) {
            addCriterion("third_source_season_begin_time in", values, "thirdSourceSeasonBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonBeginTimeNotIn(List<Date> values) {
            addCriterion("third_source_season_begin_time not in", values, "thirdSourceSeasonBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonBeginTimeBetween(Date value1, Date value2) {
            addCriterion("third_source_season_begin_time between", value1, value2, "thirdSourceSeasonBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonBeginTimeNotBetween(Date value1, Date value2) {
            addCriterion("third_source_season_begin_time not between", value1, value2, "thirdSourceSeasonBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonEndTimeIsNull() {
            addCriterion("third_source_season_end_time is null");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonEndTimeIsNotNull() {
            addCriterion("third_source_season_end_time is not null");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonEndTimeEqualTo(Date value) {
            addCriterion("third_source_season_end_time =", value, "thirdSourceSeasonEndTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonEndTimeNotEqualTo(Date value) {
            addCriterion("third_source_season_end_time <>", value, "thirdSourceSeasonEndTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonEndTimeGreaterThan(Date value) {
            addCriterion("third_source_season_end_time >", value, "thirdSourceSeasonEndTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonEndTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("third_source_season_end_time >=", value, "thirdSourceSeasonEndTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonEndTimeLessThan(Date value) {
            addCriterion("third_source_season_end_time <", value, "thirdSourceSeasonEndTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonEndTimeLessThanOrEqualTo(Date value) {
            addCriterion("third_source_season_end_time <=", value, "thirdSourceSeasonEndTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonEndTimeIn(List<Date> values) {
            addCriterion("third_source_season_end_time in", values, "thirdSourceSeasonEndTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonEndTimeNotIn(List<Date> values) {
            addCriterion("third_source_season_end_time not in", values, "thirdSourceSeasonEndTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonEndTimeBetween(Date value1, Date value2) {
            addCriterion("third_source_season_end_time between", value1, value2, "thirdSourceSeasonEndTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonEndTimeNotBetween(Date value1, Date value2) {
            addCriterion("third_source_season_end_time not between", value1, value2, "thirdSourceSeasonEndTime");
            return (Criteria) this;
        }

        public Criteria andRankingIdIsNull() {
            addCriterion("ranking_id is null");
            return (Criteria) this;
        }

        public Criteria andRankingIdIsNotNull() {
            addCriterion("ranking_id is not null");
            return (Criteria) this;
        }

        public Criteria andRankingIdEqualTo(String value) {
            addCriterion("ranking_id =", value, "rankingId");
            return (Criteria) this;
        }

        public Criteria andRankingIdNotEqualTo(String value) {
            addCriterion("ranking_id <>", value, "rankingId");
            return (Criteria) this;
        }

        public Criteria andRankingIdGreaterThan(String value) {
            addCriterion("ranking_id >", value, "rankingId");
            return (Criteria) this;
        }

        public Criteria andRankingIdGreaterThanOrEqualTo(String value) {
            addCriterion("ranking_id >=", value, "rankingId");
            return (Criteria) this;
        }

        public Criteria andRankingIdLessThan(String value) {
            addCriterion("ranking_id <", value, "rankingId");
            return (Criteria) this;
        }

        public Criteria andRankingIdLessThanOrEqualTo(String value) {
            addCriterion("ranking_id <=", value, "rankingId");
            return (Criteria) this;
        }

        public Criteria andRankingIdLike(String value) {
            addCriterion("ranking_id like", value, "rankingId");
            return (Criteria) this;
        }

        public Criteria andRankingIdNotLike(String value) {
            addCriterion("ranking_id not like", value, "rankingId");
            return (Criteria) this;
        }

        public Criteria andRankingIdIn(List<String> values) {
            addCriterion("ranking_id in", values, "rankingId");
            return (Criteria) this;
        }

        public Criteria andRankingIdNotIn(List<String> values) {
            addCriterion("ranking_id not in", values, "rankingId");
            return (Criteria) this;
        }

        public Criteria andRankingIdBetween(String value1, String value2) {
            addCriterion("ranking_id between", value1, value2, "rankingId");
            return (Criteria) this;
        }

        public Criteria andRankingIdNotBetween(String value1, String value2) {
            addCriterion("ranking_id not between", value1, value2, "rankingId");
            return (Criteria) this;
        }

        public Criteria andRankingCnNameIsNull() {
            addCriterion("ranking_cn_name is null");
            return (Criteria) this;
        }

        public Criteria andRankingCnNameIsNotNull() {
            addCriterion("ranking_cn_name is not null");
            return (Criteria) this;
        }

        public Criteria andRankingCnNameEqualTo(String value) {
            addCriterion("ranking_cn_name =", value, "rankingCnName");
            return (Criteria) this;
        }

        public Criteria andRankingCnNameNotEqualTo(String value) {
            addCriterion("ranking_cn_name <>", value, "rankingCnName");
            return (Criteria) this;
        }

        public Criteria andRankingCnNameGreaterThan(String value) {
            addCriterion("ranking_cn_name >", value, "rankingCnName");
            return (Criteria) this;
        }

        public Criteria andRankingCnNameGreaterThanOrEqualTo(String value) {
            addCriterion("ranking_cn_name >=", value, "rankingCnName");
            return (Criteria) this;
        }

        public Criteria andRankingCnNameLessThan(String value) {
            addCriterion("ranking_cn_name <", value, "rankingCnName");
            return (Criteria) this;
        }

        public Criteria andRankingCnNameLessThanOrEqualTo(String value) {
            addCriterion("ranking_cn_name <=", value, "rankingCnName");
            return (Criteria) this;
        }

        public Criteria andRankingCnNameLike(String value) {
            addCriterion("ranking_cn_name like", value, "rankingCnName");
            return (Criteria) this;
        }

        public Criteria andRankingCnNameNotLike(String value) {
            addCriterion("ranking_cn_name not like", value, "rankingCnName");
            return (Criteria) this;
        }

        public Criteria andRankingCnNameIn(List<String> values) {
            addCriterion("ranking_cn_name in", values, "rankingCnName");
            return (Criteria) this;
        }

        public Criteria andRankingCnNameNotIn(List<String> values) {
            addCriterion("ranking_cn_name not in", values, "rankingCnName");
            return (Criteria) this;
        }

        public Criteria andRankingCnNameBetween(String value1, String value2) {
            addCriterion("ranking_cn_name between", value1, value2, "rankingCnName");
            return (Criteria) this;
        }

        public Criteria andRankingCnNameNotBetween(String value1, String value2) {
            addCriterion("ranking_cn_name not between", value1, value2, "rankingCnName");
            return (Criteria) this;
        }

        public Criteria andRankingEnNameIsNull() {
            addCriterion("ranking_en_name is null");
            return (Criteria) this;
        }

        public Criteria andRankingEnNameIsNotNull() {
            addCriterion("ranking_en_name is not null");
            return (Criteria) this;
        }

        public Criteria andRankingEnNameEqualTo(String value) {
            addCriterion("ranking_en_name =", value, "rankingEnName");
            return (Criteria) this;
        }

        public Criteria andRankingEnNameNotEqualTo(String value) {
            addCriterion("ranking_en_name <>", value, "rankingEnName");
            return (Criteria) this;
        }

        public Criteria andRankingEnNameGreaterThan(String value) {
            addCriterion("ranking_en_name >", value, "rankingEnName");
            return (Criteria) this;
        }

        public Criteria andRankingEnNameGreaterThanOrEqualTo(String value) {
            addCriterion("ranking_en_name >=", value, "rankingEnName");
            return (Criteria) this;
        }

        public Criteria andRankingEnNameLessThan(String value) {
            addCriterion("ranking_en_name <", value, "rankingEnName");
            return (Criteria) this;
        }

        public Criteria andRankingEnNameLessThanOrEqualTo(String value) {
            addCriterion("ranking_en_name <=", value, "rankingEnName");
            return (Criteria) this;
        }

        public Criteria andRankingEnNameLike(String value) {
            addCriterion("ranking_en_name like", value, "rankingEnName");
            return (Criteria) this;
        }

        public Criteria andRankingEnNameNotLike(String value) {
            addCriterion("ranking_en_name not like", value, "rankingEnName");
            return (Criteria) this;
        }

        public Criteria andRankingEnNameIn(List<String> values) {
            addCriterion("ranking_en_name in", values, "rankingEnName");
            return (Criteria) this;
        }

        public Criteria andRankingEnNameNotIn(List<String> values) {
            addCriterion("ranking_en_name not in", values, "rankingEnName");
            return (Criteria) this;
        }

        public Criteria andRankingEnNameBetween(String value1, String value2) {
            addCriterion("ranking_en_name between", value1, value2, "rankingEnName");
            return (Criteria) this;
        }

        public Criteria andRankingEnNameNotBetween(String value1, String value2) {
            addCriterion("ranking_en_name not between", value1, value2, "rankingEnName");
            return (Criteria) this;
        }

        public Criteria andMatchCountIsNull() {
            addCriterion("match_count is null");
            return (Criteria) this;
        }

        public Criteria andMatchCountIsNotNull() {
            addCriterion("match_count is not null");
            return (Criteria) this;
        }

        public Criteria andMatchCountEqualTo(Integer value) {
            addCriterion("match_count =", value, "matchCount");
            return (Criteria) this;
        }

        public Criteria andMatchCountNotEqualTo(Integer value) {
            addCriterion("match_count <>", value, "matchCount");
            return (Criteria) this;
        }

        public Criteria andMatchCountGreaterThan(Integer value) {
            addCriterion("match_count >", value, "matchCount");
            return (Criteria) this;
        }

        public Criteria andMatchCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("match_count >=", value, "matchCount");
            return (Criteria) this;
        }

        public Criteria andMatchCountLessThan(Integer value) {
            addCriterion("match_count <", value, "matchCount");
            return (Criteria) this;
        }

        public Criteria andMatchCountLessThanOrEqualTo(Integer value) {
            addCriterion("match_count <=", value, "matchCount");
            return (Criteria) this;
        }

        public Criteria andMatchCountIn(List<Integer> values) {
            addCriterion("match_count in", values, "matchCount");
            return (Criteria) this;
        }

        public Criteria andMatchCountNotIn(List<Integer> values) {
            addCriterion("match_count not in", values, "matchCount");
            return (Criteria) this;
        }

        public Criteria andMatchCountBetween(Integer value1, Integer value2) {
            addCriterion("match_count between", value1, value2, "matchCount");
            return (Criteria) this;
        }

        public Criteria andMatchCountNotBetween(Integer value1, Integer value2) {
            addCriterion("match_count not between", value1, value2, "matchCount");
            return (Criteria) this;
        }

        public Criteria andThirdTeamSourceIdIsNull() {
            addCriterion("third_team_source_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdTeamSourceIdIsNotNull() {
            addCriterion("third_team_source_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdTeamSourceIdEqualTo(String value) {
            addCriterion("third_team_source_id =", value, "thirdTeamSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamSourceIdNotEqualTo(String value) {
            addCriterion("third_team_source_id <>", value, "thirdTeamSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamSourceIdGreaterThan(String value) {
            addCriterion("third_team_source_id >", value, "thirdTeamSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamSourceIdGreaterThanOrEqualTo(String value) {
            addCriterion("third_team_source_id >=", value, "thirdTeamSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamSourceIdLessThan(String value) {
            addCriterion("third_team_source_id <", value, "thirdTeamSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamSourceIdLessThanOrEqualTo(String value) {
            addCriterion("third_team_source_id <=", value, "thirdTeamSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamSourceIdLike(String value) {
            addCriterion("third_team_source_id like", value, "thirdTeamSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamSourceIdNotLike(String value) {
            addCriterion("third_team_source_id not like", value, "thirdTeamSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamSourceIdIn(List<String> values) {
            addCriterion("third_team_source_id in", values, "thirdTeamSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamSourceIdNotIn(List<String> values) {
            addCriterion("third_team_source_id not in", values, "thirdTeamSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamSourceIdBetween(String value1, String value2) {
            addCriterion("third_team_source_id between", value1, value2, "thirdTeamSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamSourceIdNotBetween(String value1, String value2) {
            addCriterion("third_team_source_id not between", value1, value2, "thirdTeamSourceId");
            return (Criteria) this;
        }

        public Criteria andTeamCnNameIsNull() {
            addCriterion("team_cn_name is null");
            return (Criteria) this;
        }

        public Criteria andTeamCnNameIsNotNull() {
            addCriterion("team_cn_name is not null");
            return (Criteria) this;
        }

        public Criteria andTeamCnNameEqualTo(String value) {
            addCriterion("team_cn_name =", value, "teamCnName");
            return (Criteria) this;
        }

        public Criteria andTeamCnNameNotEqualTo(String value) {
            addCriterion("team_cn_name <>", value, "teamCnName");
            return (Criteria) this;
        }

        public Criteria andTeamCnNameGreaterThan(String value) {
            addCriterion("team_cn_name >", value, "teamCnName");
            return (Criteria) this;
        }

        public Criteria andTeamCnNameGreaterThanOrEqualTo(String value) {
            addCriterion("team_cn_name >=", value, "teamCnName");
            return (Criteria) this;
        }

        public Criteria andTeamCnNameLessThan(String value) {
            addCriterion("team_cn_name <", value, "teamCnName");
            return (Criteria) this;
        }

        public Criteria andTeamCnNameLessThanOrEqualTo(String value) {
            addCriterion("team_cn_name <=", value, "teamCnName");
            return (Criteria) this;
        }

        public Criteria andTeamCnNameLike(String value) {
            addCriterion("team_cn_name like", value, "teamCnName");
            return (Criteria) this;
        }

        public Criteria andTeamCnNameNotLike(String value) {
            addCriterion("team_cn_name not like", value, "teamCnName");
            return (Criteria) this;
        }

        public Criteria andTeamCnNameIn(List<String> values) {
            addCriterion("team_cn_name in", values, "teamCnName");
            return (Criteria) this;
        }

        public Criteria andTeamCnNameNotIn(List<String> values) {
            addCriterion("team_cn_name not in", values, "teamCnName");
            return (Criteria) this;
        }

        public Criteria andTeamCnNameBetween(String value1, String value2) {
            addCriterion("team_cn_name between", value1, value2, "teamCnName");
            return (Criteria) this;
        }

        public Criteria andTeamCnNameNotBetween(String value1, String value2) {
            addCriterion("team_cn_name not between", value1, value2, "teamCnName");
            return (Criteria) this;
        }

        public Criteria andTeamEnNameIsNull() {
            addCriterion("team_en_name is null");
            return (Criteria) this;
        }

        public Criteria andTeamEnNameIsNotNull() {
            addCriterion("team_en_name is not null");
            return (Criteria) this;
        }

        public Criteria andTeamEnNameEqualTo(String value) {
            addCriterion("team_en_name =", value, "teamEnName");
            return (Criteria) this;
        }

        public Criteria andTeamEnNameNotEqualTo(String value) {
            addCriterion("team_en_name <>", value, "teamEnName");
            return (Criteria) this;
        }

        public Criteria andTeamEnNameGreaterThan(String value) {
            addCriterion("team_en_name >", value, "teamEnName");
            return (Criteria) this;
        }

        public Criteria andTeamEnNameGreaterThanOrEqualTo(String value) {
            addCriterion("team_en_name >=", value, "teamEnName");
            return (Criteria) this;
        }

        public Criteria andTeamEnNameLessThan(String value) {
            addCriterion("team_en_name <", value, "teamEnName");
            return (Criteria) this;
        }

        public Criteria andTeamEnNameLessThanOrEqualTo(String value) {
            addCriterion("team_en_name <=", value, "teamEnName");
            return (Criteria) this;
        }

        public Criteria andTeamEnNameLike(String value) {
            addCriterion("team_en_name like", value, "teamEnName");
            return (Criteria) this;
        }

        public Criteria andTeamEnNameNotLike(String value) {
            addCriterion("team_en_name not like", value, "teamEnName");
            return (Criteria) this;
        }

        public Criteria andTeamEnNameIn(List<String> values) {
            addCriterion("team_en_name in", values, "teamEnName");
            return (Criteria) this;
        }

        public Criteria andTeamEnNameNotIn(List<String> values) {
            addCriterion("team_en_name not in", values, "teamEnName");
            return (Criteria) this;
        }

        public Criteria andTeamEnNameBetween(String value1, String value2) {
            addCriterion("team_en_name between", value1, value2, "teamEnName");
            return (Criteria) this;
        }

        public Criteria andTeamEnNameNotBetween(String value1, String value2) {
            addCriterion("team_en_name not between", value1, value2, "teamEnName");
            return (Criteria) this;
        }

        public Criteria andTeamLogoIsNull() {
            addCriterion("team_logo is null");
            return (Criteria) this;
        }

        public Criteria andTeamLogoIsNotNull() {
            addCriterion("team_logo is not null");
            return (Criteria) this;
        }

        public Criteria andTeamLogoEqualTo(String value) {
            addCriterion("team_logo =", value, "teamLogo");
            return (Criteria) this;
        }

        public Criteria andTeamLogoNotEqualTo(String value) {
            addCriterion("team_logo <>", value, "teamLogo");
            return (Criteria) this;
        }

        public Criteria andTeamLogoGreaterThan(String value) {
            addCriterion("team_logo >", value, "teamLogo");
            return (Criteria) this;
        }

        public Criteria andTeamLogoGreaterThanOrEqualTo(String value) {
            addCriterion("team_logo >=", value, "teamLogo");
            return (Criteria) this;
        }

        public Criteria andTeamLogoLessThan(String value) {
            addCriterion("team_logo <", value, "teamLogo");
            return (Criteria) this;
        }

        public Criteria andTeamLogoLessThanOrEqualTo(String value) {
            addCriterion("team_logo <=", value, "teamLogo");
            return (Criteria) this;
        }

        public Criteria andTeamLogoLike(String value) {
            addCriterion("team_logo like", value, "teamLogo");
            return (Criteria) this;
        }

        public Criteria andTeamLogoNotLike(String value) {
            addCriterion("team_logo not like", value, "teamLogo");
            return (Criteria) this;
        }

        public Criteria andTeamLogoIn(List<String> values) {
            addCriterion("team_logo in", values, "teamLogo");
            return (Criteria) this;
        }

        public Criteria andTeamLogoNotIn(List<String> values) {
            addCriterion("team_logo not in", values, "teamLogo");
            return (Criteria) this;
        }

        public Criteria andTeamLogoBetween(String value1, String value2) {
            addCriterion("team_logo between", value1, value2, "teamLogo");
            return (Criteria) this;
        }

        public Criteria andTeamLogoNotBetween(String value1, String value2) {
            addCriterion("team_logo not between", value1, value2, "teamLogo");
            return (Criteria) this;
        }

        public Criteria andPositionTotalIsNull() {
            addCriterion("position_total is null");
            return (Criteria) this;
        }

        public Criteria andPositionTotalIsNotNull() {
            addCriterion("position_total is not null");
            return (Criteria) this;
        }

        public Criteria andPositionTotalEqualTo(Integer value) {
            addCriterion("position_total =", value, "positionTotal");
            return (Criteria) this;
        }

        public Criteria andPositionTotalNotEqualTo(Integer value) {
            addCriterion("position_total <>", value, "positionTotal");
            return (Criteria) this;
        }

        public Criteria andPositionTotalGreaterThan(Integer value) {
            addCriterion("position_total >", value, "positionTotal");
            return (Criteria) this;
        }

        public Criteria andPositionTotalGreaterThanOrEqualTo(Integer value) {
            addCriterion("position_total >=", value, "positionTotal");
            return (Criteria) this;
        }

        public Criteria andPositionTotalLessThan(Integer value) {
            addCriterion("position_total <", value, "positionTotal");
            return (Criteria) this;
        }

        public Criteria andPositionTotalLessThanOrEqualTo(Integer value) {
            addCriterion("position_total <=", value, "positionTotal");
            return (Criteria) this;
        }

        public Criteria andPositionTotalIn(List<Integer> values) {
            addCriterion("position_total in", values, "positionTotal");
            return (Criteria) this;
        }

        public Criteria andPositionTotalNotIn(List<Integer> values) {
            addCriterion("position_total not in", values, "positionTotal");
            return (Criteria) this;
        }

        public Criteria andPositionTotalBetween(Integer value1, Integer value2) {
            addCriterion("position_total between", value1, value2, "positionTotal");
            return (Criteria) this;
        }

        public Criteria andPositionTotalNotBetween(Integer value1, Integer value2) {
            addCriterion("position_total not between", value1, value2, "positionTotal");
            return (Criteria) this;
        }

        public Criteria andWinTotalIsNull() {
            addCriterion("win_total is null");
            return (Criteria) this;
        }

        public Criteria andWinTotalIsNotNull() {
            addCriterion("win_total is not null");
            return (Criteria) this;
        }

        public Criteria andWinTotalEqualTo(Integer value) {
            addCriterion("win_total =", value, "winTotal");
            return (Criteria) this;
        }

        public Criteria andWinTotalNotEqualTo(Integer value) {
            addCriterion("win_total <>", value, "winTotal");
            return (Criteria) this;
        }

        public Criteria andWinTotalGreaterThan(Integer value) {
            addCriterion("win_total >", value, "winTotal");
            return (Criteria) this;
        }

        public Criteria andWinTotalGreaterThanOrEqualTo(Integer value) {
            addCriterion("win_total >=", value, "winTotal");
            return (Criteria) this;
        }

        public Criteria andWinTotalLessThan(Integer value) {
            addCriterion("win_total <", value, "winTotal");
            return (Criteria) this;
        }

        public Criteria andWinTotalLessThanOrEqualTo(Integer value) {
            addCriterion("win_total <=", value, "winTotal");
            return (Criteria) this;
        }

        public Criteria andWinTotalIn(List<Integer> values) {
            addCriterion("win_total in", values, "winTotal");
            return (Criteria) this;
        }

        public Criteria andWinTotalNotIn(List<Integer> values) {
            addCriterion("win_total not in", values, "winTotal");
            return (Criteria) this;
        }

        public Criteria andWinTotalBetween(Integer value1, Integer value2) {
            addCriterion("win_total between", value1, value2, "winTotal");
            return (Criteria) this;
        }

        public Criteria andWinTotalNotBetween(Integer value1, Integer value2) {
            addCriterion("win_total not between", value1, value2, "winTotal");
            return (Criteria) this;
        }

        public Criteria andDrawTotalIsNull() {
            addCriterion("draw_total is null");
            return (Criteria) this;
        }

        public Criteria andDrawTotalIsNotNull() {
            addCriterion("draw_total is not null");
            return (Criteria) this;
        }

        public Criteria andDrawTotalEqualTo(Integer value) {
            addCriterion("draw_total =", value, "drawTotal");
            return (Criteria) this;
        }

        public Criteria andDrawTotalNotEqualTo(Integer value) {
            addCriterion("draw_total <>", value, "drawTotal");
            return (Criteria) this;
        }

        public Criteria andDrawTotalGreaterThan(Integer value) {
            addCriterion("draw_total >", value, "drawTotal");
            return (Criteria) this;
        }

        public Criteria andDrawTotalGreaterThanOrEqualTo(Integer value) {
            addCriterion("draw_total >=", value, "drawTotal");
            return (Criteria) this;
        }

        public Criteria andDrawTotalLessThan(Integer value) {
            addCriterion("draw_total <", value, "drawTotal");
            return (Criteria) this;
        }

        public Criteria andDrawTotalLessThanOrEqualTo(Integer value) {
            addCriterion("draw_total <=", value, "drawTotal");
            return (Criteria) this;
        }

        public Criteria andDrawTotalIn(List<Integer> values) {
            addCriterion("draw_total in", values, "drawTotal");
            return (Criteria) this;
        }

        public Criteria andDrawTotalNotIn(List<Integer> values) {
            addCriterion("draw_total not in", values, "drawTotal");
            return (Criteria) this;
        }

        public Criteria andDrawTotalBetween(Integer value1, Integer value2) {
            addCriterion("draw_total between", value1, value2, "drawTotal");
            return (Criteria) this;
        }

        public Criteria andDrawTotalNotBetween(Integer value1, Integer value2) {
            addCriterion("draw_total not between", value1, value2, "drawTotal");
            return (Criteria) this;
        }

        public Criteria andLossTotalIsNull() {
            addCriterion("loss_total is null");
            return (Criteria) this;
        }

        public Criteria andLossTotalIsNotNull() {
            addCriterion("loss_total is not null");
            return (Criteria) this;
        }

        public Criteria andLossTotalEqualTo(Integer value) {
            addCriterion("loss_total =", value, "lossTotal");
            return (Criteria) this;
        }

        public Criteria andLossTotalNotEqualTo(Integer value) {
            addCriterion("loss_total <>", value, "lossTotal");
            return (Criteria) this;
        }

        public Criteria andLossTotalGreaterThan(Integer value) {
            addCriterion("loss_total >", value, "lossTotal");
            return (Criteria) this;
        }

        public Criteria andLossTotalGreaterThanOrEqualTo(Integer value) {
            addCriterion("loss_total >=", value, "lossTotal");
            return (Criteria) this;
        }

        public Criteria andLossTotalLessThan(Integer value) {
            addCriterion("loss_total <", value, "lossTotal");
            return (Criteria) this;
        }

        public Criteria andLossTotalLessThanOrEqualTo(Integer value) {
            addCriterion("loss_total <=", value, "lossTotal");
            return (Criteria) this;
        }

        public Criteria andLossTotalIn(List<Integer> values) {
            addCriterion("loss_total in", values, "lossTotal");
            return (Criteria) this;
        }

        public Criteria andLossTotalNotIn(List<Integer> values) {
            addCriterion("loss_total not in", values, "lossTotal");
            return (Criteria) this;
        }

        public Criteria andLossTotalBetween(Integer value1, Integer value2) {
            addCriterion("loss_total between", value1, value2, "lossTotal");
            return (Criteria) this;
        }

        public Criteria andLossTotalNotBetween(Integer value1, Integer value2) {
            addCriterion("loss_total not between", value1, value2, "lossTotal");
            return (Criteria) this;
        }

        public Criteria andPointsTotalIsNull() {
            addCriterion("points_total is null");
            return (Criteria) this;
        }

        public Criteria andPointsTotalIsNotNull() {
            addCriterion("points_total is not null");
            return (Criteria) this;
        }

        public Criteria andPointsTotalEqualTo(Integer value) {
            addCriterion("points_total =", value, "pointsTotal");
            return (Criteria) this;
        }

        public Criteria andPointsTotalNotEqualTo(Integer value) {
            addCriterion("points_total <>", value, "pointsTotal");
            return (Criteria) this;
        }

        public Criteria andPointsTotalGreaterThan(Integer value) {
            addCriterion("points_total >", value, "pointsTotal");
            return (Criteria) this;
        }

        public Criteria andPointsTotalGreaterThanOrEqualTo(Integer value) {
            addCriterion("points_total >=", value, "pointsTotal");
            return (Criteria) this;
        }

        public Criteria andPointsTotalLessThan(Integer value) {
            addCriterion("points_total <", value, "pointsTotal");
            return (Criteria) this;
        }

        public Criteria andPointsTotalLessThanOrEqualTo(Integer value) {
            addCriterion("points_total <=", value, "pointsTotal");
            return (Criteria) this;
        }

        public Criteria andPointsTotalIn(List<Integer> values) {
            addCriterion("points_total in", values, "pointsTotal");
            return (Criteria) this;
        }

        public Criteria andPointsTotalNotIn(List<Integer> values) {
            addCriterion("points_total not in", values, "pointsTotal");
            return (Criteria) this;
        }

        public Criteria andPointsTotalBetween(Integer value1, Integer value2) {
            addCriterion("points_total between", value1, value2, "pointsTotal");
            return (Criteria) this;
        }

        public Criteria andPointsTotalNotBetween(Integer value1, Integer value2) {
            addCriterion("points_total not between", value1, value2, "pointsTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsForTotalIsNull() {
            addCriterion("goals_for_total is null");
            return (Criteria) this;
        }

        public Criteria andGoalsForTotalIsNotNull() {
            addCriterion("goals_for_total is not null");
            return (Criteria) this;
        }

        public Criteria andGoalsForTotalEqualTo(Integer value) {
            addCriterion("goals_for_total =", value, "goalsForTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsForTotalNotEqualTo(Integer value) {
            addCriterion("goals_for_total <>", value, "goalsForTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsForTotalGreaterThan(Integer value) {
            addCriterion("goals_for_total >", value, "goalsForTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsForTotalGreaterThanOrEqualTo(Integer value) {
            addCriterion("goals_for_total >=", value, "goalsForTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsForTotalLessThan(Integer value) {
            addCriterion("goals_for_total <", value, "goalsForTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsForTotalLessThanOrEqualTo(Integer value) {
            addCriterion("goals_for_total <=", value, "goalsForTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsForTotalIn(List<Integer> values) {
            addCriterion("goals_for_total in", values, "goalsForTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsForTotalNotIn(List<Integer> values) {
            addCriterion("goals_for_total not in", values, "goalsForTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsForTotalBetween(Integer value1, Integer value2) {
            addCriterion("goals_for_total between", value1, value2, "goalsForTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsForTotalNotBetween(Integer value1, Integer value2) {
            addCriterion("goals_for_total not between", value1, value2, "goalsForTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsAgainstTotalIsNull() {
            addCriterion("goals_against_total is null");
            return (Criteria) this;
        }

        public Criteria andGoalsAgainstTotalIsNotNull() {
            addCriterion("goals_against_total is not null");
            return (Criteria) this;
        }

        public Criteria andGoalsAgainstTotalEqualTo(Integer value) {
            addCriterion("goals_against_total =", value, "goalsAgainstTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsAgainstTotalNotEqualTo(Integer value) {
            addCriterion("goals_against_total <>", value, "goalsAgainstTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsAgainstTotalGreaterThan(Integer value) {
            addCriterion("goals_against_total >", value, "goalsAgainstTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsAgainstTotalGreaterThanOrEqualTo(Integer value) {
            addCriterion("goals_against_total >=", value, "goalsAgainstTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsAgainstTotalLessThan(Integer value) {
            addCriterion("goals_against_total <", value, "goalsAgainstTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsAgainstTotalLessThanOrEqualTo(Integer value) {
            addCriterion("goals_against_total <=", value, "goalsAgainstTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsAgainstTotalIn(List<Integer> values) {
            addCriterion("goals_against_total in", values, "goalsAgainstTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsAgainstTotalNotIn(List<Integer> values) {
            addCriterion("goals_against_total not in", values, "goalsAgainstTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsAgainstTotalBetween(Integer value1, Integer value2) {
            addCriterion("goals_against_total between", value1, value2, "goalsAgainstTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsAgainstTotalNotBetween(Integer value1, Integer value2) {
            addCriterion("goals_against_total not between", value1, value2, "goalsAgainstTotal");
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

        public Criteria andGoalDiffTotalIsNull() {
            addCriterion("goal_diff_total is null");
            return (Criteria) this;
        }

        public Criteria andGoalDiffTotalIsNotNull() {
            addCriterion("goal_diff_total is not null");
            return (Criteria) this;
        }

        public Criteria andGoalDiffTotalEqualTo(Integer value) {
            addCriterion("goal_diff_total =", value, "goalDiffTotal");
            return (Criteria) this;
        }

        public Criteria andGoalDiffTotalNotEqualTo(Integer value) {
            addCriterion("goal_diff_total <>", value, "goalDiffTotal");
            return (Criteria) this;
        }

        public Criteria andGoalDiffTotalGreaterThan(Integer value) {
            addCriterion("goal_diff_total >", value, "goalDiffTotal");
            return (Criteria) this;
        }

        public Criteria andGoalDiffTotalGreaterThanOrEqualTo(Integer value) {
            addCriterion("goal_diff_total >=", value, "goalDiffTotal");
            return (Criteria) this;
        }

        public Criteria andGoalDiffTotalLessThan(Integer value) {
            addCriterion("goal_diff_total <", value, "goalDiffTotal");
            return (Criteria) this;
        }

        public Criteria andGoalDiffTotalLessThanOrEqualTo(Integer value) {
            addCriterion("goal_diff_total <=", value, "goalDiffTotal");
            return (Criteria) this;
        }

        public Criteria andGoalDiffTotalIn(List<Integer> values) {
            addCriterion("goal_diff_total in", values, "goalDiffTotal");
            return (Criteria) this;
        }

        public Criteria andGoalDiffTotalNotIn(List<Integer> values) {
            addCriterion("goal_diff_total not in", values, "goalDiffTotal");
            return (Criteria) this;
        }

        public Criteria andGoalDiffTotalBetween(Integer value1, Integer value2) {
            addCriterion("goal_diff_total between", value1, value2, "goalDiffTotal");
            return (Criteria) this;
        }

        public Criteria andGoalDiffTotalNotBetween(Integer value1, Integer value2) {
            addCriterion("goal_diff_total not between", value1, value2, "goalDiffTotal");
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

        public Criteria andGroupCnNameIsNull() {
            addCriterion("group_cn_name is null");
            return (Criteria) this;
        }

        public Criteria andGroupCnNameIsNotNull() {
            addCriterion("group_cn_name is not null");
            return (Criteria) this;
        }

        public Criteria andGroupCnNameEqualTo(String value) {
            addCriterion("group_cn_name =", value, "groupCnName");
            return (Criteria) this;
        }

        public Criteria andGroupCnNameNotEqualTo(String value) {
            addCriterion("group_cn_name <>", value, "groupCnName");
            return (Criteria) this;
        }

        public Criteria andGroupCnNameGreaterThan(String value) {
            addCriterion("group_cn_name >", value, "groupCnName");
            return (Criteria) this;
        }

        public Criteria andGroupCnNameGreaterThanOrEqualTo(String value) {
            addCriterion("group_cn_name >=", value, "groupCnName");
            return (Criteria) this;
        }

        public Criteria andGroupCnNameLessThan(String value) {
            addCriterion("group_cn_name <", value, "groupCnName");
            return (Criteria) this;
        }

        public Criteria andGroupCnNameLessThanOrEqualTo(String value) {
            addCriterion("group_cn_name <=", value, "groupCnName");
            return (Criteria) this;
        }

        public Criteria andGroupCnNameLike(String value) {
            addCriterion("group_cn_name like", value, "groupCnName");
            return (Criteria) this;
        }

        public Criteria andGroupCnNameNotLike(String value) {
            addCriterion("group_cn_name not like", value, "groupCnName");
            return (Criteria) this;
        }

        public Criteria andGroupCnNameIn(List<String> values) {
            addCriterion("group_cn_name in", values, "groupCnName");
            return (Criteria) this;
        }

        public Criteria andGroupCnNameNotIn(List<String> values) {
            addCriterion("group_cn_name not in", values, "groupCnName");
            return (Criteria) this;
        }

        public Criteria andGroupCnNameBetween(String value1, String value2) {
            addCriterion("group_cn_name between", value1, value2, "groupCnName");
            return (Criteria) this;
        }

        public Criteria andGroupCnNameNotBetween(String value1, String value2) {
            addCriterion("group_cn_name not between", value1, value2, "groupCnName");
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

        public Criteria andWinnerMarketOddsidIsNull() {
            addCriterion("winner_market_oddsId is null");
            return (Criteria) this;
        }

        public Criteria andWinnerMarketOddsidIsNotNull() {
            addCriterion("winner_market_oddsId is not null");
            return (Criteria) this;
        }

        public Criteria andWinnerMarketOddsidEqualTo(String value) {
            addCriterion("winner_market_oddsId =", value, "winnerMarketOddsid");
            return (Criteria) this;
        }

        public Criteria andWinnerMarketOddsidNotEqualTo(String value) {
            addCriterion("winner_market_oddsId <>", value, "winnerMarketOddsid");
            return (Criteria) this;
        }

        public Criteria andWinnerMarketOddsidGreaterThan(String value) {
            addCriterion("winner_market_oddsId >", value, "winnerMarketOddsid");
            return (Criteria) this;
        }

        public Criteria andWinnerMarketOddsidGreaterThanOrEqualTo(String value) {
            addCriterion("winner_market_oddsId >=", value, "winnerMarketOddsid");
            return (Criteria) this;
        }

        public Criteria andWinnerMarketOddsidLessThan(String value) {
            addCriterion("winner_market_oddsId <", value, "winnerMarketOddsid");
            return (Criteria) this;
        }

        public Criteria andWinnerMarketOddsidLessThanOrEqualTo(String value) {
            addCriterion("winner_market_oddsId <=", value, "winnerMarketOddsid");
            return (Criteria) this;
        }

        public Criteria andWinnerMarketOddsidLike(String value) {
            addCriterion("winner_market_oddsId like", value, "winnerMarketOddsid");
            return (Criteria) this;
        }

        public Criteria andWinnerMarketOddsidNotLike(String value) {
            addCriterion("winner_market_oddsId not like", value, "winnerMarketOddsid");
            return (Criteria) this;
        }

        public Criteria andWinnerMarketOddsidIn(List<String> values) {
            addCriterion("winner_market_oddsId in", values, "winnerMarketOddsid");
            return (Criteria) this;
        }

        public Criteria andWinnerMarketOddsidNotIn(List<String> values) {
            addCriterion("winner_market_oddsId not in", values, "winnerMarketOddsid");
            return (Criteria) this;
        }

        public Criteria andWinnerMarketOddsidBetween(String value1, String value2) {
            addCriterion("winner_market_oddsId between", value1, value2, "winnerMarketOddsid");
            return (Criteria) this;
        }

        public Criteria andWinnerMarketOddsidNotBetween(String value1, String value2) {
            addCriterion("winner_market_oddsId not between", value1, value2, "winnerMarketOddsid");
            return (Criteria) this;
        }

        public Criteria andAdvanceMarketOddsidIsNull() {
            addCriterion("advance_market_oddsId is null");
            return (Criteria) this;
        }

        public Criteria andAdvanceMarketOddsidIsNotNull() {
            addCriterion("advance_market_oddsId is not null");
            return (Criteria) this;
        }

        public Criteria andAdvanceMarketOddsidEqualTo(String value) {
            addCriterion("advance_market_oddsId =", value, "advanceMarketOddsid");
            return (Criteria) this;
        }

        public Criteria andAdvanceMarketOddsidNotEqualTo(String value) {
            addCriterion("advance_market_oddsId <>", value, "advanceMarketOddsid");
            return (Criteria) this;
        }

        public Criteria andAdvanceMarketOddsidGreaterThan(String value) {
            addCriterion("advance_market_oddsId >", value, "advanceMarketOddsid");
            return (Criteria) this;
        }

        public Criteria andAdvanceMarketOddsidGreaterThanOrEqualTo(String value) {
            addCriterion("advance_market_oddsId >=", value, "advanceMarketOddsid");
            return (Criteria) this;
        }

        public Criteria andAdvanceMarketOddsidLessThan(String value) {
            addCriterion("advance_market_oddsId <", value, "advanceMarketOddsid");
            return (Criteria) this;
        }

        public Criteria andAdvanceMarketOddsidLessThanOrEqualTo(String value) {
            addCriterion("advance_market_oddsId <=", value, "advanceMarketOddsid");
            return (Criteria) this;
        }

        public Criteria andAdvanceMarketOddsidLike(String value) {
            addCriterion("advance_market_oddsId like", value, "advanceMarketOddsid");
            return (Criteria) this;
        }

        public Criteria andAdvanceMarketOddsidNotLike(String value) {
            addCriterion("advance_market_oddsId not like", value, "advanceMarketOddsid");
            return (Criteria) this;
        }

        public Criteria andAdvanceMarketOddsidIn(List<String> values) {
            addCriterion("advance_market_oddsId in", values, "advanceMarketOddsid");
            return (Criteria) this;
        }

        public Criteria andAdvanceMarketOddsidNotIn(List<String> values) {
            addCriterion("advance_market_oddsId not in", values, "advanceMarketOddsid");
            return (Criteria) this;
        }

        public Criteria andAdvanceMarketOddsidBetween(String value1, String value2) {
            addCriterion("advance_market_oddsId between", value1, value2, "advanceMarketOddsid");
            return (Criteria) this;
        }

        public Criteria andAdvanceMarketOddsidNotBetween(String value1, String value2) {
            addCriterion("advance_market_oddsId not between", value1, value2, "advanceMarketOddsid");
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

        public Criteria andTotalMatchesIsNull() {
            addCriterion("total_matches is null");
            return (Criteria) this;
        }

        public Criteria andTotalMatchesIsNotNull() {
            addCriterion("total_matches is not null");
            return (Criteria) this;
        }

        public Criteria andTotalMatchesEqualTo(Long value) {
            addCriterion("total_matches =", value, "totalMatches");
            return (Criteria) this;
        }

        public Criteria andTotalMatchesNotEqualTo(Long value) {
            addCriterion("total_matches <>", value, "totalMatches");
            return (Criteria) this;
        }

        public Criteria andTotalMatchesGreaterThan(Long value) {
            addCriterion("total_matches >", value, "totalMatches");
            return (Criteria) this;
        }

        public Criteria andTotalMatchesGreaterThanOrEqualTo(Long value) {
            addCriterion("total_matches >=", value, "totalMatches");
            return (Criteria) this;
        }

        public Criteria andTotalMatchesLessThan(Long value) {
            addCriterion("total_matches <", value, "totalMatches");
            return (Criteria) this;
        }

        public Criteria andTotalMatchesLessThanOrEqualTo(Long value) {
            addCriterion("total_matches <=", value, "totalMatches");
            return (Criteria) this;
        }

        public Criteria andTotalMatchesIn(List<Long> values) {
            addCriterion("total_matches in", values, "totalMatches");
            return (Criteria) this;
        }

        public Criteria andTotalMatchesNotIn(List<Long> values) {
            addCriterion("total_matches not in", values, "totalMatches");
            return (Criteria) this;
        }

        public Criteria andTotalMatchesBetween(Long value1, Long value2) {
            addCriterion("total_matches between", value1, value2, "totalMatches");
            return (Criteria) this;
        }

        public Criteria andTotalMatchesNotBetween(Long value1, Long value2) {
            addCriterion("total_matches not between", value1, value2, "totalMatches");
            return (Criteria) this;
        }

        public Criteria andMatchesCompletedIsNull() {
            addCriterion("matches_completed is null");
            return (Criteria) this;
        }

        public Criteria andMatchesCompletedIsNotNull() {
            addCriterion("matches_completed is not null");
            return (Criteria) this;
        }

        public Criteria andMatchesCompletedEqualTo(Long value) {
            addCriterion("matches_completed =", value, "matchesCompleted");
            return (Criteria) this;
        }

        public Criteria andMatchesCompletedNotEqualTo(Long value) {
            addCriterion("matches_completed <>", value, "matchesCompleted");
            return (Criteria) this;
        }

        public Criteria andMatchesCompletedGreaterThan(Long value) {
            addCriterion("matches_completed >", value, "matchesCompleted");
            return (Criteria) this;
        }

        public Criteria andMatchesCompletedGreaterThanOrEqualTo(Long value) {
            addCriterion("matches_completed >=", value, "matchesCompleted");
            return (Criteria) this;
        }

        public Criteria andMatchesCompletedLessThan(Long value) {
            addCriterion("matches_completed <", value, "matchesCompleted");
            return (Criteria) this;
        }

        public Criteria andMatchesCompletedLessThanOrEqualTo(Long value) {
            addCriterion("matches_completed <=", value, "matchesCompleted");
            return (Criteria) this;
        }

        public Criteria andMatchesCompletedIn(List<Long> values) {
            addCriterion("matches_completed in", values, "matchesCompleted");
            return (Criteria) this;
        }

        public Criteria andMatchesCompletedNotIn(List<Long> values) {
            addCriterion("matches_completed not in", values, "matchesCompleted");
            return (Criteria) this;
        }

        public Criteria andMatchesCompletedBetween(Long value1, Long value2) {
            addCriterion("matches_completed between", value1, value2, "matchesCompleted");
            return (Criteria) this;
        }

        public Criteria andMatchesCompletedNotBetween(Long value1, Long value2) {
            addCriterion("matches_completed not between", value1, value2, "matchesCompleted");
            return (Criteria) this;
        }

        public Criteria andPromotionCnNameIsNull() {
            addCriterion("promotion_cn_name is null");
            return (Criteria) this;
        }

        public Criteria andPromotionCnNameIsNotNull() {
            addCriterion("promotion_cn_name is not null");
            return (Criteria) this;
        }

        public Criteria andPromotionCnNameEqualTo(String value) {
            addCriterion("promotion_cn_name =", value, "promotionCnName");
            return (Criteria) this;
        }

        public Criteria andPromotionCnNameNotEqualTo(String value) {
            addCriterion("promotion_cn_name <>", value, "promotionCnName");
            return (Criteria) this;
        }

        public Criteria andPromotionCnNameGreaterThan(String value) {
            addCriterion("promotion_cn_name >", value, "promotionCnName");
            return (Criteria) this;
        }

        public Criteria andPromotionCnNameGreaterThanOrEqualTo(String value) {
            addCriterion("promotion_cn_name >=", value, "promotionCnName");
            return (Criteria) this;
        }

        public Criteria andPromotionCnNameLessThan(String value) {
            addCriterion("promotion_cn_name <", value, "promotionCnName");
            return (Criteria) this;
        }

        public Criteria andPromotionCnNameLessThanOrEqualTo(String value) {
            addCriterion("promotion_cn_name <=", value, "promotionCnName");
            return (Criteria) this;
        }

        public Criteria andPromotionCnNameLike(String value) {
            addCriterion("promotion_cn_name like", value, "promotionCnName");
            return (Criteria) this;
        }

        public Criteria andPromotionCnNameNotLike(String value) {
            addCriterion("promotion_cn_name not like", value, "promotionCnName");
            return (Criteria) this;
        }

        public Criteria andPromotionCnNameIn(List<String> values) {
            addCriterion("promotion_cn_name in", values, "promotionCnName");
            return (Criteria) this;
        }

        public Criteria andPromotionCnNameNotIn(List<String> values) {
            addCriterion("promotion_cn_name not in", values, "promotionCnName");
            return (Criteria) this;
        }

        public Criteria andPromotionCnNameBetween(String value1, String value2) {
            addCriterion("promotion_cn_name between", value1, value2, "promotionCnName");
            return (Criteria) this;
        }

        public Criteria andPromotionCnNameNotBetween(String value1, String value2) {
            addCriterion("promotion_cn_name not between", value1, value2, "promotionCnName");
            return (Criteria) this;
        }

        public Criteria andPromotionEnNameIsNull() {
            addCriterion("promotion_en_name is null");
            return (Criteria) this;
        }

        public Criteria andPromotionEnNameIsNotNull() {
            addCriterion("promotion_en_name is not null");
            return (Criteria) this;
        }

        public Criteria andPromotionEnNameEqualTo(String value) {
            addCriterion("promotion_en_name =", value, "promotionEnName");
            return (Criteria) this;
        }

        public Criteria andPromotionEnNameNotEqualTo(String value) {
            addCriterion("promotion_en_name <>", value, "promotionEnName");
            return (Criteria) this;
        }

        public Criteria andPromotionEnNameGreaterThan(String value) {
            addCriterion("promotion_en_name >", value, "promotionEnName");
            return (Criteria) this;
        }

        public Criteria andPromotionEnNameGreaterThanOrEqualTo(String value) {
            addCriterion("promotion_en_name >=", value, "promotionEnName");
            return (Criteria) this;
        }

        public Criteria andPromotionEnNameLessThan(String value) {
            addCriterion("promotion_en_name <", value, "promotionEnName");
            return (Criteria) this;
        }

        public Criteria andPromotionEnNameLessThanOrEqualTo(String value) {
            addCriterion("promotion_en_name <=", value, "promotionEnName");
            return (Criteria) this;
        }

        public Criteria andPromotionEnNameLike(String value) {
            addCriterion("promotion_en_name like", value, "promotionEnName");
            return (Criteria) this;
        }

        public Criteria andPromotionEnNameNotLike(String value) {
            addCriterion("promotion_en_name not like", value, "promotionEnName");
            return (Criteria) this;
        }

        public Criteria andPromotionEnNameIn(List<String> values) {
            addCriterion("promotion_en_name in", values, "promotionEnName");
            return (Criteria) this;
        }

        public Criteria andPromotionEnNameNotIn(List<String> values) {
            addCriterion("promotion_en_name not in", values, "promotionEnName");
            return (Criteria) this;
        }

        public Criteria andPromotionEnNameBetween(String value1, String value2) {
            addCriterion("promotion_en_name between", value1, value2, "promotionEnName");
            return (Criteria) this;
        }

        public Criteria andPromotionEnNameNotBetween(String value1, String value2) {
            addCriterion("promotion_en_name not between", value1, value2, "promotionEnName");
            return (Criteria) this;
        }

        public Criteria andPromotionIdIsNull() {
            addCriterion("promotion_id is null");
            return (Criteria) this;
        }

        public Criteria andPromotionIdIsNotNull() {
            addCriterion("promotion_id is not null");
            return (Criteria) this;
        }

        public Criteria andPromotionIdEqualTo(String value) {
            addCriterion("promotion_id =", value, "promotionId");
            return (Criteria) this;
        }

        public Criteria andPromotionIdNotEqualTo(String value) {
            addCriterion("promotion_id <>", value, "promotionId");
            return (Criteria) this;
        }

        public Criteria andPromotionIdGreaterThan(String value) {
            addCriterion("promotion_id >", value, "promotionId");
            return (Criteria) this;
        }

        public Criteria andPromotionIdGreaterThanOrEqualTo(String value) {
            addCriterion("promotion_id >=", value, "promotionId");
            return (Criteria) this;
        }

        public Criteria andPromotionIdLessThan(String value) {
            addCriterion("promotion_id <", value, "promotionId");
            return (Criteria) this;
        }

        public Criteria andPromotionIdLessThanOrEqualTo(String value) {
            addCriterion("promotion_id <=", value, "promotionId");
            return (Criteria) this;
        }

        public Criteria andPromotionIdLike(String value) {
            addCriterion("promotion_id like", value, "promotionId");
            return (Criteria) this;
        }

        public Criteria andPromotionIdNotLike(String value) {
            addCriterion("promotion_id not like", value, "promotionId");
            return (Criteria) this;
        }

        public Criteria andPromotionIdIn(List<String> values) {
            addCriterion("promotion_id in", values, "promotionId");
            return (Criteria) this;
        }

        public Criteria andPromotionIdNotIn(List<String> values) {
            addCriterion("promotion_id not in", values, "promotionId");
            return (Criteria) this;
        }

        public Criteria andPromotionIdBetween(String value1, String value2) {
            addCriterion("promotion_id between", value1, value2, "promotionId");
            return (Criteria) this;
        }

        public Criteria andPromotionIdNotBetween(String value1, String value2) {
            addCriterion("promotion_id not between", value1, value2, "promotionId");
            return (Criteria) this;
        }

        public Criteria andHomeMatchesTotalIsNull() {
            addCriterion("home_matches_total is null");
            return (Criteria) this;
        }

        public Criteria andHomeMatchesTotalIsNotNull() {
            addCriterion("home_matches_total is not null");
            return (Criteria) this;
        }

        public Criteria andHomeMatchesTotalEqualTo(Integer value) {
            addCriterion("home_matches_total =", value, "homeMatchesTotal");
            return (Criteria) this;
        }

        public Criteria andHomeMatchesTotalNotEqualTo(Integer value) {
            addCriterion("home_matches_total <>", value, "homeMatchesTotal");
            return (Criteria) this;
        }

        public Criteria andHomeMatchesTotalGreaterThan(Integer value) {
            addCriterion("home_matches_total >", value, "homeMatchesTotal");
            return (Criteria) this;
        }

        public Criteria andHomeMatchesTotalGreaterThanOrEqualTo(Integer value) {
            addCriterion("home_matches_total >=", value, "homeMatchesTotal");
            return (Criteria) this;
        }

        public Criteria andHomeMatchesTotalLessThan(Integer value) {
            addCriterion("home_matches_total <", value, "homeMatchesTotal");
            return (Criteria) this;
        }

        public Criteria andHomeMatchesTotalLessThanOrEqualTo(Integer value) {
            addCriterion("home_matches_total <=", value, "homeMatchesTotal");
            return (Criteria) this;
        }

        public Criteria andHomeMatchesTotalIn(List<Integer> values) {
            addCriterion("home_matches_total in", values, "homeMatchesTotal");
            return (Criteria) this;
        }

        public Criteria andHomeMatchesTotalNotIn(List<Integer> values) {
            addCriterion("home_matches_total not in", values, "homeMatchesTotal");
            return (Criteria) this;
        }

        public Criteria andHomeMatchesTotalBetween(Integer value1, Integer value2) {
            addCriterion("home_matches_total between", value1, value2, "homeMatchesTotal");
            return (Criteria) this;
        }

        public Criteria andHomeMatchesTotalNotBetween(Integer value1, Integer value2) {
            addCriterion("home_matches_total not between", value1, value2, "homeMatchesTotal");
            return (Criteria) this;
        }

        public Criteria andAwayMatchesTotalIsNull() {
            addCriterion("away_matches_total is null");
            return (Criteria) this;
        }

        public Criteria andAwayMatchesTotalIsNotNull() {
            addCriterion("away_matches_total is not null");
            return (Criteria) this;
        }

        public Criteria andAwayMatchesTotalEqualTo(Integer value) {
            addCriterion("away_matches_total =", value, "awayMatchesTotal");
            return (Criteria) this;
        }

        public Criteria andAwayMatchesTotalNotEqualTo(Integer value) {
            addCriterion("away_matches_total <>", value, "awayMatchesTotal");
            return (Criteria) this;
        }

        public Criteria andAwayMatchesTotalGreaterThan(Integer value) {
            addCriterion("away_matches_total >", value, "awayMatchesTotal");
            return (Criteria) this;
        }

        public Criteria andAwayMatchesTotalGreaterThanOrEqualTo(Integer value) {
            addCriterion("away_matches_total >=", value, "awayMatchesTotal");
            return (Criteria) this;
        }

        public Criteria andAwayMatchesTotalLessThan(Integer value) {
            addCriterion("away_matches_total <", value, "awayMatchesTotal");
            return (Criteria) this;
        }

        public Criteria andAwayMatchesTotalLessThanOrEqualTo(Integer value) {
            addCriterion("away_matches_total <=", value, "awayMatchesTotal");
            return (Criteria) this;
        }

        public Criteria andAwayMatchesTotalIn(List<Integer> values) {
            addCriterion("away_matches_total in", values, "awayMatchesTotal");
            return (Criteria) this;
        }

        public Criteria andAwayMatchesTotalNotIn(List<Integer> values) {
            addCriterion("away_matches_total not in", values, "awayMatchesTotal");
            return (Criteria) this;
        }

        public Criteria andAwayMatchesTotalBetween(Integer value1, Integer value2) {
            addCriterion("away_matches_total between", value1, value2, "awayMatchesTotal");
            return (Criteria) this;
        }

        public Criteria andAwayMatchesTotalNotBetween(Integer value1, Integer value2) {
            addCriterion("away_matches_total not between", value1, value2, "awayMatchesTotal");
            return (Criteria) this;
        }

        public Criteria andWinLast10IsNull() {
            addCriterion("win_last10 is null");
            return (Criteria) this;
        }

        public Criteria andWinLast10IsNotNull() {
            addCriterion("win_last10 is not null");
            return (Criteria) this;
        }

        public Criteria andWinLast10EqualTo(Integer value) {
            addCriterion("win_last10 =", value, "winLast10");
            return (Criteria) this;
        }

        public Criteria andWinLast10NotEqualTo(Integer value) {
            addCriterion("win_last10 <>", value, "winLast10");
            return (Criteria) this;
        }

        public Criteria andWinLast10GreaterThan(Integer value) {
            addCriterion("win_last10 >", value, "winLast10");
            return (Criteria) this;
        }

        public Criteria andWinLast10GreaterThanOrEqualTo(Integer value) {
            addCriterion("win_last10 >=", value, "winLast10");
            return (Criteria) this;
        }

        public Criteria andWinLast10LessThan(Integer value) {
            addCriterion("win_last10 <", value, "winLast10");
            return (Criteria) this;
        }

        public Criteria andWinLast10LessThanOrEqualTo(Integer value) {
            addCriterion("win_last10 <=", value, "winLast10");
            return (Criteria) this;
        }

        public Criteria andWinLast10In(List<Integer> values) {
            addCriterion("win_last10 in", values, "winLast10");
            return (Criteria) this;
        }

        public Criteria andWinLast10NotIn(List<Integer> values) {
            addCriterion("win_last10 not in", values, "winLast10");
            return (Criteria) this;
        }

        public Criteria andWinLast10Between(Integer value1, Integer value2) {
            addCriterion("win_last10 between", value1, value2, "winLast10");
            return (Criteria) this;
        }

        public Criteria andWinLast10NotBetween(Integer value1, Integer value2) {
            addCriterion("win_last10 not between", value1, value2, "winLast10");
            return (Criteria) this;
        }

        public Criteria andLossLast10IsNull() {
            addCriterion("loss_last10 is null");
            return (Criteria) this;
        }

        public Criteria andLossLast10IsNotNull() {
            addCriterion("loss_last10 is not null");
            return (Criteria) this;
        }

        public Criteria andLossLast10EqualTo(Integer value) {
            addCriterion("loss_last10 =", value, "lossLast10");
            return (Criteria) this;
        }

        public Criteria andLossLast10NotEqualTo(Integer value) {
            addCriterion("loss_last10 <>", value, "lossLast10");
            return (Criteria) this;
        }

        public Criteria andLossLast10GreaterThan(Integer value) {
            addCriterion("loss_last10 >", value, "lossLast10");
            return (Criteria) this;
        }

        public Criteria andLossLast10GreaterThanOrEqualTo(Integer value) {
            addCriterion("loss_last10 >=", value, "lossLast10");
            return (Criteria) this;
        }

        public Criteria andLossLast10LessThan(Integer value) {
            addCriterion("loss_last10 <", value, "lossLast10");
            return (Criteria) this;
        }

        public Criteria andLossLast10LessThanOrEqualTo(Integer value) {
            addCriterion("loss_last10 <=", value, "lossLast10");
            return (Criteria) this;
        }

        public Criteria andLossLast10In(List<Integer> values) {
            addCriterion("loss_last10 in", values, "lossLast10");
            return (Criteria) this;
        }

        public Criteria andLossLast10NotIn(List<Integer> values) {
            addCriterion("loss_last10 not in", values, "lossLast10");
            return (Criteria) this;
        }

        public Criteria andLossLast10Between(Integer value1, Integer value2) {
            addCriterion("loss_last10 between", value1, value2, "lossLast10");
            return (Criteria) this;
        }

        public Criteria andLossLast10NotBetween(Integer value1, Integer value2) {
            addCriterion("loss_last10 not between", value1, value2, "lossLast10");
            return (Criteria) this;
        }

        public Criteria andStreakIsNull() {
            addCriterion("streak is null");
            return (Criteria) this;
        }

        public Criteria andStreakIsNotNull() {
            addCriterion("streak is not null");
            return (Criteria) this;
        }

        public Criteria andStreakEqualTo(Integer value) {
            addCriterion("streak =", value, "streak");
            return (Criteria) this;
        }

        public Criteria andStreakNotEqualTo(Integer value) {
            addCriterion("streak <>", value, "streak");
            return (Criteria) this;
        }

        public Criteria andStreakGreaterThan(Integer value) {
            addCriterion("streak >", value, "streak");
            return (Criteria) this;
        }

        public Criteria andStreakGreaterThanOrEqualTo(Integer value) {
            addCriterion("streak >=", value, "streak");
            return (Criteria) this;
        }

        public Criteria andStreakLessThan(Integer value) {
            addCriterion("streak <", value, "streak");
            return (Criteria) this;
        }

        public Criteria andStreakLessThanOrEqualTo(Integer value) {
            addCriterion("streak <=", value, "streak");
            return (Criteria) this;
        }

        public Criteria andStreakIn(List<Integer> values) {
            addCriterion("streak in", values, "streak");
            return (Criteria) this;
        }

        public Criteria andStreakNotIn(List<Integer> values) {
            addCriterion("streak not in", values, "streak");
            return (Criteria) this;
        }

        public Criteria andStreakBetween(Integer value1, Integer value2) {
            addCriterion("streak between", value1, value2, "streak");
            return (Criteria) this;
        }

        public Criteria andStreakNotBetween(Integer value1, Integer value2) {
            addCriterion("streak not between", value1, value2, "streak");
            return (Criteria) this;
        }

        public Criteria andWinPctTotalIsNull() {
            addCriterion("win_pct_total is null");
            return (Criteria) this;
        }

        public Criteria andWinPctTotalIsNotNull() {
            addCriterion("win_pct_total is not null");
            return (Criteria) this;
        }

        public Criteria andWinPctTotalEqualTo(String value) {
            addCriterion("win_pct_total =", value, "winPctTotal");
            return (Criteria) this;
        }

        public Criteria andWinPctTotalNotEqualTo(String value) {
            addCriterion("win_pct_total <>", value, "winPctTotal");
            return (Criteria) this;
        }

        public Criteria andWinPctTotalGreaterThan(String value) {
            addCriterion("win_pct_total >", value, "winPctTotal");
            return (Criteria) this;
        }

        public Criteria andWinPctTotalGreaterThanOrEqualTo(String value) {
            addCriterion("win_pct_total >=", value, "winPctTotal");
            return (Criteria) this;
        }

        public Criteria andWinPctTotalLessThan(String value) {
            addCriterion("win_pct_total <", value, "winPctTotal");
            return (Criteria) this;
        }

        public Criteria andWinPctTotalLessThanOrEqualTo(String value) {
            addCriterion("win_pct_total <=", value, "winPctTotal");
            return (Criteria) this;
        }

        public Criteria andWinPctTotalLike(String value) {
            addCriterion("win_pct_total like", value, "winPctTotal");
            return (Criteria) this;
        }

        public Criteria andWinPctTotalNotLike(String value) {
            addCriterion("win_pct_total not like", value, "winPctTotal");
            return (Criteria) this;
        }

        public Criteria andWinPctTotalIn(List<String> values) {
            addCriterion("win_pct_total in", values, "winPctTotal");
            return (Criteria) this;
        }

        public Criteria andWinPctTotalNotIn(List<String> values) {
            addCriterion("win_pct_total not in", values, "winPctTotal");
            return (Criteria) this;
        }

        public Criteria andWinPctTotalBetween(String value1, String value2) {
            addCriterion("win_pct_total between", value1, value2, "winPctTotal");
            return (Criteria) this;
        }

        public Criteria andWinPctTotalNotBetween(String value1, String value2) {
            addCriterion("win_pct_total not between", value1, value2, "winPctTotal");
            return (Criteria) this;
        }

        public Criteria andGameBehindIsNull() {
            addCriterion("game_behind is null");
            return (Criteria) this;
        }

        public Criteria andGameBehindIsNotNull() {
            addCriterion("game_behind is not null");
            return (Criteria) this;
        }

        public Criteria andGameBehindEqualTo(String value) {
            addCriterion("game_behind =", value, "gameBehind");
            return (Criteria) this;
        }

        public Criteria andGameBehindNotEqualTo(String value) {
            addCriterion("game_behind <>", value, "gameBehind");
            return (Criteria) this;
        }

        public Criteria andGameBehindGreaterThan(String value) {
            addCriterion("game_behind >", value, "gameBehind");
            return (Criteria) this;
        }

        public Criteria andGameBehindGreaterThanOrEqualTo(String value) {
            addCriterion("game_behind >=", value, "gameBehind");
            return (Criteria) this;
        }

        public Criteria andGameBehindLessThan(String value) {
            addCriterion("game_behind <", value, "gameBehind");
            return (Criteria) this;
        }

        public Criteria andGameBehindLessThanOrEqualTo(String value) {
            addCriterion("game_behind <=", value, "gameBehind");
            return (Criteria) this;
        }

        public Criteria andGameBehindLike(String value) {
            addCriterion("game_behind like", value, "gameBehind");
            return (Criteria) this;
        }

        public Criteria andGameBehindNotLike(String value) {
            addCriterion("game_behind not like", value, "gameBehind");
            return (Criteria) this;
        }

        public Criteria andGameBehindIn(List<String> values) {
            addCriterion("game_behind in", values, "gameBehind");
            return (Criteria) this;
        }

        public Criteria andGameBehindNotIn(List<String> values) {
            addCriterion("game_behind not in", values, "gameBehind");
            return (Criteria) this;
        }

        public Criteria andGameBehindBetween(String value1, String value2) {
            addCriterion("game_behind between", value1, value2, "gameBehind");
            return (Criteria) this;
        }

        public Criteria andGameBehindNotBetween(String value1, String value2) {
            addCriterion("game_behind not between", value1, value2, "gameBehind");
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