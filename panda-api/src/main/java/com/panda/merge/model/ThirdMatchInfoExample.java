package com.panda.merge.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ThirdMatchInfoExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ThirdMatchInfoExample() {
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

        public Criteria andParentIdIsNull() {
            addCriterion("parent_id is null");
            return (Criteria) this;
        }

        public Criteria andParentIdIsNotNull() {
            addCriterion("parent_id is not null");
            return (Criteria) this;
        }

        public Criteria andParentIdEqualTo(Long value) {
            addCriterion("parent_id =", value, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdNotEqualTo(Long value) {
            addCriterion("parent_id <>", value, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdGreaterThan(Long value) {
            addCriterion("parent_id >", value, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdGreaterThanOrEqualTo(Long value) {
            addCriterion("parent_id >=", value, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdLessThan(Long value) {
            addCriterion("parent_id <", value, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdLessThanOrEqualTo(Long value) {
            addCriterion("parent_id <=", value, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdIn(List<Long> values) {
            addCriterion("parent_id in", values, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdNotIn(List<Long> values) {
            addCriterion("parent_id not in", values, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdBetween(Long value1, Long value2) {
            addCriterion("parent_id between", value1, value2, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdNotBetween(Long value1, Long value2) {
            addCriterion("parent_id not between", value1, value2, "parentId");
            return (Criteria) this;
        }

        public Criteria andWhetherStopIsNull() {
            addCriterion("whether_stop is null");
            return (Criteria) this;
        }

        public Criteria andWhetherStopIsNotNull() {
            addCriterion("whether_stop is not null");
            return (Criteria) this;
        }

        public Criteria andWhetherStopEqualTo(Integer value) {
            addCriterion("whether_stop =", value, "whetherStop");
            return (Criteria) this;
        }

        public Criteria andWhetherStopNotEqualTo(Integer value) {
            addCriterion("whether_stop <>", value, "whetherStop");
            return (Criteria) this;
        }

        public Criteria andWhetherStopGreaterThan(Integer value) {
            addCriterion("whether_stop >", value, "whetherStop");
            return (Criteria) this;
        }

        public Criteria andWhetherStopGreaterThanOrEqualTo(Integer value) {
            addCriterion("whether_stop >=", value, "whetherStop");
            return (Criteria) this;
        }

        public Criteria andWhetherStopLessThan(Integer value) {
            addCriterion("whether_stop <", value, "whetherStop");
            return (Criteria) this;
        }

        public Criteria andWhetherStopLessThanOrEqualTo(Integer value) {
            addCriterion("whether_stop <=", value, "whetherStop");
            return (Criteria) this;
        }

        public Criteria andWhetherStopIn(List<Integer> values) {
            addCriterion("whether_stop in", values, "whetherStop");
            return (Criteria) this;
        }

        public Criteria andWhetherStopNotIn(List<Integer> values) {
            addCriterion("whether_stop not in", values, "whetherStop");
            return (Criteria) this;
        }

        public Criteria andWhetherStopBetween(Integer value1, Integer value2) {
            addCriterion("whether_stop between", value1, value2, "whetherStop");
            return (Criteria) this;
        }

        public Criteria andWhetherStopNotBetween(Integer value1, Integer value2) {
            addCriterion("whether_stop not between", value1, value2, "whetherStop");
            return (Criteria) this;
        }

        public Criteria andActiveIsNull() {
            addCriterion("active is null");
            return (Criteria) this;
        }

        public Criteria andActiveIsNotNull() {
            addCriterion("active is not null");
            return (Criteria) this;
        }

        public Criteria andActiveEqualTo(Integer value) {
            addCriterion("active =", value, "active");
            return (Criteria) this;
        }

        public Criteria andActiveNotEqualTo(Integer value) {
            addCriterion("active <>", value, "active");
            return (Criteria) this;
        }

        public Criteria andActiveGreaterThan(Integer value) {
            addCriterion("active >", value, "active");
            return (Criteria) this;
        }

        public Criteria andActiveGreaterThanOrEqualTo(Integer value) {
            addCriterion("active >=", value, "active");
            return (Criteria) this;
        }

        public Criteria andActiveLessThan(Integer value) {
            addCriterion("active <", value, "active");
            return (Criteria) this;
        }

        public Criteria andActiveLessThanOrEqualTo(Integer value) {
            addCriterion("active <=", value, "active");
            return (Criteria) this;
        }

        public Criteria andActiveIn(List<Integer> values) {
            addCriterion("active in", values, "active");
            return (Criteria) this;
        }

        public Criteria andActiveNotIn(List<Integer> values) {
            addCriterion("active not in", values, "active");
            return (Criteria) this;
        }

        public Criteria andActiveBetween(Integer value1, Integer value2) {
            addCriterion("active between", value1, value2, "active");
            return (Criteria) this;
        }

        public Criteria andActiveNotBetween(Integer value1, Integer value2) {
            addCriterion("active not between", value1, value2, "active");
            return (Criteria) this;
        }

        public Criteria andVisibleIsNull() {
            addCriterion("visible is null");
            return (Criteria) this;
        }

        public Criteria andVisibleIsNotNull() {
            addCriterion("visible is not null");
            return (Criteria) this;
        }

        public Criteria andVisibleEqualTo(Integer value) {
            addCriterion("visible =", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleNotEqualTo(Integer value) {
            addCriterion("visible <>", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleGreaterThan(Integer value) {
            addCriterion("visible >", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleGreaterThanOrEqualTo(Integer value) {
            addCriterion("visible >=", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleLessThan(Integer value) {
            addCriterion("visible <", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleLessThanOrEqualTo(Integer value) {
            addCriterion("visible <=", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleIn(List<Integer> values) {
            addCriterion("visible in", values, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleNotIn(List<Integer> values) {
            addCriterion("visible not in", values, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleBetween(Integer value1, Integer value2) {
            addCriterion("visible between", value1, value2, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleNotBetween(Integer value1, Integer value2) {
            addCriterion("visible not between", value1, value2, "visible");
            return (Criteria) this;
        }

        public Criteria andLiveOddSupportIsNull() {
            addCriterion("live_odd_support is null");
            return (Criteria) this;
        }

        public Criteria andLiveOddSupportIsNotNull() {
            addCriterion("live_odd_support is not null");
            return (Criteria) this;
        }

        public Criteria andLiveOddSupportEqualTo(Integer value) {
            addCriterion("live_odd_support =", value, "liveOddSupport");
            return (Criteria) this;
        }

        public Criteria andLiveOddSupportNotEqualTo(Integer value) {
            addCriterion("live_odd_support <>", value, "liveOddSupport");
            return (Criteria) this;
        }

        public Criteria andLiveOddSupportGreaterThan(Integer value) {
            addCriterion("live_odd_support >", value, "liveOddSupport");
            return (Criteria) this;
        }

        public Criteria andLiveOddSupportGreaterThanOrEqualTo(Integer value) {
            addCriterion("live_odd_support >=", value, "liveOddSupport");
            return (Criteria) this;
        }

        public Criteria andLiveOddSupportLessThan(Integer value) {
            addCriterion("live_odd_support <", value, "liveOddSupport");
            return (Criteria) this;
        }

        public Criteria andLiveOddSupportLessThanOrEqualTo(Integer value) {
            addCriterion("live_odd_support <=", value, "liveOddSupport");
            return (Criteria) this;
        }

        public Criteria andLiveOddSupportIn(List<Integer> values) {
            addCriterion("live_odd_support in", values, "liveOddSupport");
            return (Criteria) this;
        }

        public Criteria andLiveOddSupportNotIn(List<Integer> values) {
            addCriterion("live_odd_support not in", values, "liveOddSupport");
            return (Criteria) this;
        }

        public Criteria andLiveOddSupportBetween(Integer value1, Integer value2) {
            addCriterion("live_odd_support between", value1, value2, "liveOddSupport");
            return (Criteria) this;
        }

        public Criteria andLiveOddSupportNotBetween(Integer value1, Integer value2) {
            addCriterion("live_odd_support not between", value1, value2, "liveOddSupport");
            return (Criteria) this;
        }

        public Criteria andPreMatchBetStatusIsNull() {
            addCriterion("pre_match_bet_status is null");
            return (Criteria) this;
        }

        public Criteria andPreMatchBetStatusIsNotNull() {
            addCriterion("pre_match_bet_status is not null");
            return (Criteria) this;
        }

        public Criteria andPreMatchBetStatusEqualTo(Integer value) {
            addCriterion("pre_match_bet_status =", value, "preMatchBetStatus");
            return (Criteria) this;
        }

        public Criteria andPreMatchBetStatusNotEqualTo(Integer value) {
            addCriterion("pre_match_bet_status <>", value, "preMatchBetStatus");
            return (Criteria) this;
        }

        public Criteria andPreMatchBetStatusGreaterThan(Integer value) {
            addCriterion("pre_match_bet_status >", value, "preMatchBetStatus");
            return (Criteria) this;
        }

        public Criteria andPreMatchBetStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("pre_match_bet_status >=", value, "preMatchBetStatus");
            return (Criteria) this;
        }

        public Criteria andPreMatchBetStatusLessThan(Integer value) {
            addCriterion("pre_match_bet_status <", value, "preMatchBetStatus");
            return (Criteria) this;
        }

        public Criteria andPreMatchBetStatusLessThanOrEqualTo(Integer value) {
            addCriterion("pre_match_bet_status <=", value, "preMatchBetStatus");
            return (Criteria) this;
        }

        public Criteria andPreMatchBetStatusIn(List<Integer> values) {
            addCriterion("pre_match_bet_status in", values, "preMatchBetStatus");
            return (Criteria) this;
        }

        public Criteria andPreMatchBetStatusNotIn(List<Integer> values) {
            addCriterion("pre_match_bet_status not in", values, "preMatchBetStatus");
            return (Criteria) this;
        }

        public Criteria andPreMatchBetStatusBetween(Integer value1, Integer value2) {
            addCriterion("pre_match_bet_status between", value1, value2, "preMatchBetStatus");
            return (Criteria) this;
        }

        public Criteria andPreMatchBetStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("pre_match_bet_status not between", value1, value2, "preMatchBetStatus");
            return (Criteria) this;
        }

        public Criteria andHomeAwayInfoIsNull() {
            addCriterion("home_away_info is null");
            return (Criteria) this;
        }

        public Criteria andHomeAwayInfoIsNotNull() {
            addCriterion("home_away_info is not null");
            return (Criteria) this;
        }

        public Criteria andHomeAwayInfoEqualTo(String value) {
            addCriterion("home_away_info =", value, "homeAwayInfo");
            return (Criteria) this;
        }

        public Criteria andHomeAwayInfoNotEqualTo(String value) {
            addCriterion("home_away_info <>", value, "homeAwayInfo");
            return (Criteria) this;
        }

        public Criteria andHomeAwayInfoGreaterThan(String value) {
            addCriterion("home_away_info >", value, "homeAwayInfo");
            return (Criteria) this;
        }

        public Criteria andHomeAwayInfoGreaterThanOrEqualTo(String value) {
            addCriterion("home_away_info >=", value, "homeAwayInfo");
            return (Criteria) this;
        }

        public Criteria andHomeAwayInfoLessThan(String value) {
            addCriterion("home_away_info <", value, "homeAwayInfo");
            return (Criteria) this;
        }

        public Criteria andHomeAwayInfoLessThanOrEqualTo(String value) {
            addCriterion("home_away_info <=", value, "homeAwayInfo");
            return (Criteria) this;
        }

        public Criteria andHomeAwayInfoLike(String value) {
            addCriterion("home_away_info like", value, "homeAwayInfo");
            return (Criteria) this;
        }

        public Criteria andHomeAwayInfoNotLike(String value) {
            addCriterion("home_away_info not like", value, "homeAwayInfo");
            return (Criteria) this;
        }

        public Criteria andHomeAwayInfoIn(List<String> values) {
            addCriterion("home_away_info in", values, "homeAwayInfo");
            return (Criteria) this;
        }

        public Criteria andHomeAwayInfoNotIn(List<String> values) {
            addCriterion("home_away_info not in", values, "homeAwayInfo");
            return (Criteria) this;
        }

        public Criteria andHomeAwayInfoBetween(String value1, String value2) {
            addCriterion("home_away_info between", value1, value2, "homeAwayInfo");
            return (Criteria) this;
        }

        public Criteria andHomeAwayInfoNotBetween(String value1, String value2) {
            addCriterion("home_away_info not between", value1, value2, "homeAwayInfo");
            return (Criteria) this;
        }

        public Criteria andBetStatusIsNull() {
            addCriterion("bet_status is null");
            return (Criteria) this;
        }

        public Criteria andBetStatusIsNotNull() {
            addCriterion("bet_status is not null");
            return (Criteria) this;
        }

        public Criteria andBetStatusEqualTo(Integer value) {
            addCriterion("bet_status =", value, "betStatus");
            return (Criteria) this;
        }

        public Criteria andBetStatusNotEqualTo(Integer value) {
            addCriterion("bet_status <>", value, "betStatus");
            return (Criteria) this;
        }

        public Criteria andBetStatusGreaterThan(Integer value) {
            addCriterion("bet_status >", value, "betStatus");
            return (Criteria) this;
        }

        public Criteria andBetStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("bet_status >=", value, "betStatus");
            return (Criteria) this;
        }

        public Criteria andBetStatusLessThan(Integer value) {
            addCriterion("bet_status <", value, "betStatus");
            return (Criteria) this;
        }

        public Criteria andBetStatusLessThanOrEqualTo(Integer value) {
            addCriterion("bet_status <=", value, "betStatus");
            return (Criteria) this;
        }

        public Criteria andBetStatusIn(List<Integer> values) {
            addCriterion("bet_status in", values, "betStatus");
            return (Criteria) this;
        }

        public Criteria andBetStatusNotIn(List<Integer> values) {
            addCriterion("bet_status not in", values, "betStatus");
            return (Criteria) this;
        }

        public Criteria andBetStatusBetween(Integer value1, Integer value2) {
            addCriterion("bet_status between", value1, value2, "betStatus");
            return (Criteria) this;
        }

        public Criteria andBetStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("bet_status not between", value1, value2, "betStatus");
            return (Criteria) this;
        }

        public Criteria andLiveOddsBetStatusIsNull() {
            addCriterion("live_odds_bet_status is null");
            return (Criteria) this;
        }

        public Criteria andLiveOddsBetStatusIsNotNull() {
            addCriterion("live_odds_bet_status is not null");
            return (Criteria) this;
        }

        public Criteria andLiveOddsBetStatusEqualTo(Integer value) {
            addCriterion("live_odds_bet_status =", value, "liveOddsBetStatus");
            return (Criteria) this;
        }

        public Criteria andLiveOddsBetStatusNotEqualTo(Integer value) {
            addCriterion("live_odds_bet_status <>", value, "liveOddsBetStatus");
            return (Criteria) this;
        }

        public Criteria andLiveOddsBetStatusGreaterThan(Integer value) {
            addCriterion("live_odds_bet_status >", value, "liveOddsBetStatus");
            return (Criteria) this;
        }

        public Criteria andLiveOddsBetStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("live_odds_bet_status >=", value, "liveOddsBetStatus");
            return (Criteria) this;
        }

        public Criteria andLiveOddsBetStatusLessThan(Integer value) {
            addCriterion("live_odds_bet_status <", value, "liveOddsBetStatus");
            return (Criteria) this;
        }

        public Criteria andLiveOddsBetStatusLessThanOrEqualTo(Integer value) {
            addCriterion("live_odds_bet_status <=", value, "liveOddsBetStatus");
            return (Criteria) this;
        }

        public Criteria andLiveOddsBetStatusIn(List<Integer> values) {
            addCriterion("live_odds_bet_status in", values, "liveOddsBetStatus");
            return (Criteria) this;
        }

        public Criteria andLiveOddsBetStatusNotIn(List<Integer> values) {
            addCriterion("live_odds_bet_status not in", values, "liveOddsBetStatus");
            return (Criteria) this;
        }

        public Criteria andLiveOddsBetStatusBetween(Integer value1, Integer value2) {
            addCriterion("live_odds_bet_status between", value1, value2, "liveOddsBetStatus");
            return (Criteria) this;
        }

        public Criteria andLiveOddsBetStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("live_odds_bet_status not between", value1, value2, "liveOddsBetStatus");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartIsNull() {
            addCriterion("seconds_match_start is null");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartIsNotNull() {
            addCriterion("seconds_match_start is not null");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartEqualTo(Integer value) {
            addCriterion("seconds_match_start =", value, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartNotEqualTo(Integer value) {
            addCriterion("seconds_match_start <>", value, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartGreaterThan(Integer value) {
            addCriterion("seconds_match_start >", value, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartGreaterThanOrEqualTo(Integer value) {
            addCriterion("seconds_match_start >=", value, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartLessThan(Integer value) {
            addCriterion("seconds_match_start <", value, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartLessThanOrEqualTo(Integer value) {
            addCriterion("seconds_match_start <=", value, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartIn(List<Integer> values) {
            addCriterion("seconds_match_start in", values, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartNotIn(List<Integer> values) {
            addCriterion("seconds_match_start not in", values, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartBetween(Integer value1, Integer value2) {
            addCriterion("seconds_match_start between", value1, value2, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchStartNotBetween(Integer value1, Integer value2) {
            addCriterion("seconds_match_start not between", value1, value2, "secondsMatchStart");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchModifyTimeIsNull() {
            addCriterion("seconds_match_modify_time is null");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchModifyTimeIsNotNull() {
            addCriterion("seconds_match_modify_time is not null");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchModifyTimeEqualTo(Long value) {
            addCriterion("seconds_match_modify_time =", value, "secondsMatchModifyTime");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchModifyTimeNotEqualTo(Long value) {
            addCriterion("seconds_match_modify_time <>", value, "secondsMatchModifyTime");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchModifyTimeGreaterThan(Long value) {
            addCriterion("seconds_match_modify_time >", value, "secondsMatchModifyTime");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchModifyTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("seconds_match_modify_time >=", value, "secondsMatchModifyTime");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchModifyTimeLessThan(Long value) {
            addCriterion("seconds_match_modify_time <", value, "secondsMatchModifyTime");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchModifyTimeLessThanOrEqualTo(Long value) {
            addCriterion("seconds_match_modify_time <=", value, "secondsMatchModifyTime");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchModifyTimeIn(List<Long> values) {
            addCriterion("seconds_match_modify_time in", values, "secondsMatchModifyTime");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchModifyTimeNotIn(List<Long> values) {
            addCriterion("seconds_match_modify_time not in", values, "secondsMatchModifyTime");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchModifyTimeBetween(Long value1, Long value2) {
            addCriterion("seconds_match_modify_time between", value1, value2, "secondsMatchModifyTime");
            return (Criteria) this;
        }

        public Criteria andSecondsMatchModifyTimeNotBetween(Long value1, Long value2) {
            addCriterion("seconds_match_modify_time not between", value1, value2, "secondsMatchModifyTime");
            return (Criteria) this;
        }

        public Criteria andHomeAwayOppositeIsNull() {
            addCriterion("home_away_opposite is null");
            return (Criteria) this;
        }

        public Criteria andHomeAwayOppositeIsNotNull() {
            addCriterion("home_away_opposite is not null");
            return (Criteria) this;
        }

        public Criteria andHomeAwayOppositeEqualTo(Integer value) {
            addCriterion("home_away_opposite =", value, "homeAwayOpposite");
            return (Criteria) this;
        }

        public Criteria andHomeAwayOppositeNotEqualTo(Integer value) {
            addCriterion("home_away_opposite <>", value, "homeAwayOpposite");
            return (Criteria) this;
        }

        public Criteria andHomeAwayOppositeGreaterThan(Integer value) {
            addCriterion("home_away_opposite >", value, "homeAwayOpposite");
            return (Criteria) this;
        }

        public Criteria andHomeAwayOppositeGreaterThanOrEqualTo(Integer value) {
            addCriterion("home_away_opposite >=", value, "homeAwayOpposite");
            return (Criteria) this;
        }

        public Criteria andHomeAwayOppositeLessThan(Integer value) {
            addCriterion("home_away_opposite <", value, "homeAwayOpposite");
            return (Criteria) this;
        }

        public Criteria andHomeAwayOppositeLessThanOrEqualTo(Integer value) {
            addCriterion("home_away_opposite <=", value, "homeAwayOpposite");
            return (Criteria) this;
        }

        public Criteria andHomeAwayOppositeIn(List<Integer> values) {
            addCriterion("home_away_opposite in", values, "homeAwayOpposite");
            return (Criteria) this;
        }

        public Criteria andHomeAwayOppositeNotIn(List<Integer> values) {
            addCriterion("home_away_opposite not in", values, "homeAwayOpposite");
            return (Criteria) this;
        }

        public Criteria andHomeAwayOppositeBetween(Integer value1, Integer value2) {
            addCriterion("home_away_opposite between", value1, value2, "homeAwayOpposite");
            return (Criteria) this;
        }

        public Criteria andHomeAwayOppositeNotBetween(Integer value1, Integer value2) {
            addCriterion("home_away_opposite not between", value1, value2, "homeAwayOpposite");
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

        public Criteria andNeutralGroundIsNull() {
            addCriterion("neutral_ground is null");
            return (Criteria) this;
        }

        public Criteria andNeutralGroundIsNotNull() {
            addCriterion("neutral_ground is not null");
            return (Criteria) this;
        }

        public Criteria andNeutralGroundEqualTo(Integer value) {
            addCriterion("neutral_ground =", value, "neutralGround");
            return (Criteria) this;
        }

        public Criteria andNeutralGroundNotEqualTo(Integer value) {
            addCriterion("neutral_ground <>", value, "neutralGround");
            return (Criteria) this;
        }

        public Criteria andNeutralGroundGreaterThan(Integer value) {
            addCriterion("neutral_ground >", value, "neutralGround");
            return (Criteria) this;
        }

        public Criteria andNeutralGroundGreaterThanOrEqualTo(Integer value) {
            addCriterion("neutral_ground >=", value, "neutralGround");
            return (Criteria) this;
        }

        public Criteria andNeutralGroundLessThan(Integer value) {
            addCriterion("neutral_ground <", value, "neutralGround");
            return (Criteria) this;
        }

        public Criteria andNeutralGroundLessThanOrEqualTo(Integer value) {
            addCriterion("neutral_ground <=", value, "neutralGround");
            return (Criteria) this;
        }

        public Criteria andNeutralGroundIn(List<Integer> values) {
            addCriterion("neutral_ground in", values, "neutralGround");
            return (Criteria) this;
        }

        public Criteria andNeutralGroundNotIn(List<Integer> values) {
            addCriterion("neutral_ground not in", values, "neutralGround");
            return (Criteria) this;
        }

        public Criteria andNeutralGroundBetween(Integer value1, Integer value2) {
            addCriterion("neutral_ground between", value1, value2, "neutralGround");
            return (Criteria) this;
        }

        public Criteria andNeutralGroundNotBetween(Integer value1, Integer value2) {
            addCriterion("neutral_ground not between", value1, value2, "neutralGround");
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

        public Criteria andMatchOverIsNull() {
            addCriterion("match_over is null");
            return (Criteria) this;
        }

        public Criteria andMatchOverIsNotNull() {
            addCriterion("match_over is not null");
            return (Criteria) this;
        }

        public Criteria andMatchOverEqualTo(Integer value) {
            addCriterion("match_over =", value, "matchOver");
            return (Criteria) this;
        }

        public Criteria andMatchOverNotEqualTo(Integer value) {
            addCriterion("match_over <>", value, "matchOver");
            return (Criteria) this;
        }

        public Criteria andMatchOverGreaterThan(Integer value) {
            addCriterion("match_over >", value, "matchOver");
            return (Criteria) this;
        }

        public Criteria andMatchOverGreaterThanOrEqualTo(Integer value) {
            addCriterion("match_over >=", value, "matchOver");
            return (Criteria) this;
        }

        public Criteria andMatchOverLessThan(Integer value) {
            addCriterion("match_over <", value, "matchOver");
            return (Criteria) this;
        }

        public Criteria andMatchOverLessThanOrEqualTo(Integer value) {
            addCriterion("match_over <=", value, "matchOver");
            return (Criteria) this;
        }

        public Criteria andMatchOverIn(List<Integer> values) {
            addCriterion("match_over in", values, "matchOver");
            return (Criteria) this;
        }

        public Criteria andMatchOverNotIn(List<Integer> values) {
            addCriterion("match_over not in", values, "matchOver");
            return (Criteria) this;
        }

        public Criteria andMatchOverBetween(Integer value1, Integer value2) {
            addCriterion("match_over between", value1, value2, "matchOver");
            return (Criteria) this;
        }

        public Criteria andMatchOverNotBetween(Integer value1, Integer value2) {
            addCriterion("match_over not between", value1, value2, "matchOver");
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

        public Criteria andRoundTypeEqualTo(Integer value) {
            addCriterion("round_type =", value, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeNotEqualTo(Integer value) {
            addCriterion("round_type <>", value, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeGreaterThan(Integer value) {
            addCriterion("round_type >", value, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("round_type >=", value, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeLessThan(Integer value) {
            addCriterion("round_type <", value, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeLessThanOrEqualTo(Integer value) {
            addCriterion("round_type <=", value, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeIn(List<Integer> values) {
            addCriterion("round_type in", values, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeNotIn(List<Integer> values) {
            addCriterion("round_type not in", values, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeBetween(Integer value1, Integer value2) {
            addCriterion("round_type between", value1, value2, "roundType");
            return (Criteria) this;
        }

        public Criteria andRoundTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("round_type not between", value1, value2, "roundType");
            return (Criteria) this;
        }

        public Criteria andLotteryNumberIsNull() {
            addCriterion("lottery_number is null");
            return (Criteria) this;
        }

        public Criteria andLotteryNumberIsNotNull() {
            addCriterion("lottery_number is not null");
            return (Criteria) this;
        }

        public Criteria andLotteryNumberEqualTo(String value) {
            addCriterion("lottery_number =", value, "lotteryNumber");
            return (Criteria) this;
        }

        public Criteria andLotteryNumberNotEqualTo(String value) {
            addCriterion("lottery_number <>", value, "lotteryNumber");
            return (Criteria) this;
        }

        public Criteria andLotteryNumberGreaterThan(String value) {
            addCriterion("lottery_number >", value, "lotteryNumber");
            return (Criteria) this;
        }

        public Criteria andLotteryNumberGreaterThanOrEqualTo(String value) {
            addCriterion("lottery_number >=", value, "lotteryNumber");
            return (Criteria) this;
        }

        public Criteria andLotteryNumberLessThan(String value) {
            addCriterion("lottery_number <", value, "lotteryNumber");
            return (Criteria) this;
        }

        public Criteria andLotteryNumberLessThanOrEqualTo(String value) {
            addCriterion("lottery_number <=", value, "lotteryNumber");
            return (Criteria) this;
        }

        public Criteria andLotteryNumberLike(String value) {
            addCriterion("lottery_number like", value, "lotteryNumber");
            return (Criteria) this;
        }

        public Criteria andLotteryNumberNotLike(String value) {
            addCriterion("lottery_number not like", value, "lotteryNumber");
            return (Criteria) this;
        }

        public Criteria andLotteryNumberIn(List<String> values) {
            addCriterion("lottery_number in", values, "lotteryNumber");
            return (Criteria) this;
        }

        public Criteria andLotteryNumberNotIn(List<String> values) {
            addCriterion("lottery_number not in", values, "lotteryNumber");
            return (Criteria) this;
        }

        public Criteria andLotteryNumberBetween(String value1, String value2) {
            addCriterion("lottery_number between", value1, value2, "lotteryNumber");
            return (Criteria) this;
        }

        public Criteria andLotteryNumberNotBetween(String value1, String value2) {
            addCriterion("lottery_number not between", value1, value2, "lotteryNumber");
            return (Criteria) this;
        }

        public Criteria andLmtModeIsNull() {
            addCriterion("lmt_mode is null");
            return (Criteria) this;
        }

        public Criteria andLmtModeIsNotNull() {
            addCriterion("lmt_mode is not null");
            return (Criteria) this;
        }

        public Criteria andLmtModeEqualTo(Integer value) {
            addCriterion("lmt_mode =", value, "lmtMode");
            return (Criteria) this;
        }

        public Criteria andLmtModeNotEqualTo(Integer value) {
            addCriterion("lmt_mode <>", value, "lmtMode");
            return (Criteria) this;
        }

        public Criteria andLmtModeGreaterThan(Integer value) {
            addCriterion("lmt_mode >", value, "lmtMode");
            return (Criteria) this;
        }

        public Criteria andLmtModeGreaterThanOrEqualTo(Integer value) {
            addCriterion("lmt_mode >=", value, "lmtMode");
            return (Criteria) this;
        }

        public Criteria andLmtModeLessThan(Integer value) {
            addCriterion("lmt_mode <", value, "lmtMode");
            return (Criteria) this;
        }

        public Criteria andLmtModeLessThanOrEqualTo(Integer value) {
            addCriterion("lmt_mode <=", value, "lmtMode");
            return (Criteria) this;
        }

        public Criteria andLmtModeIn(List<Integer> values) {
            addCriterion("lmt_mode in", values, "lmtMode");
            return (Criteria) this;
        }

        public Criteria andLmtModeNotIn(List<Integer> values) {
            addCriterion("lmt_mode not in", values, "lmtMode");
            return (Criteria) this;
        }

        public Criteria andLmtModeBetween(Integer value1, Integer value2) {
            addCriterion("lmt_mode between", value1, value2, "lmtMode");
            return (Criteria) this;
        }

        public Criteria andLmtModeNotBetween(Integer value1, Integer value2) {
            addCriterion("lmt_mode not between", value1, value2, "lmtMode");
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

        public Criteria andSiteTypeIsNull() {
            addCriterion("site_type is null");
            return (Criteria) this;
        }

        public Criteria andSiteTypeIsNotNull() {
            addCriterion("site_type is not null");
            return (Criteria) this;
        }

        public Criteria andSiteTypeEqualTo(Integer value) {
            addCriterion("site_type =", value, "siteType");
            return (Criteria) this;
        }

        public Criteria andSiteTypeNotEqualTo(Integer value) {
            addCriterion("site_type <>", value, "siteType");
            return (Criteria) this;
        }

        public Criteria andSiteTypeGreaterThan(Integer value) {
            addCriterion("site_type >", value, "siteType");
            return (Criteria) this;
        }

        public Criteria andSiteTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("site_type >=", value, "siteType");
            return (Criteria) this;
        }

        public Criteria andSiteTypeLessThan(Integer value) {
            addCriterion("site_type <", value, "siteType");
            return (Criteria) this;
        }

        public Criteria andSiteTypeLessThanOrEqualTo(Integer value) {
            addCriterion("site_type <=", value, "siteType");
            return (Criteria) this;
        }

        public Criteria andSiteTypeIn(List<Integer> values) {
            addCriterion("site_type in", values, "siteType");
            return (Criteria) this;
        }

        public Criteria andSiteTypeNotIn(List<Integer> values) {
            addCriterion("site_type not in", values, "siteType");
            return (Criteria) this;
        }

        public Criteria andSiteTypeBetween(Integer value1, Integer value2) {
            addCriterion("site_type between", value1, value2, "siteType");
            return (Criteria) this;
        }

        public Criteria andSiteTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("site_type not between", value1, value2, "siteType");
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

        public Criteria andMatchStatusEqualTo(Integer value) {
            addCriterion("match_status =", value, "matchStatus");
            return (Criteria) this;
        }

        public Criteria andMatchStatusNotEqualTo(Integer value) {
            addCriterion("match_status <>", value, "matchStatus");
            return (Criteria) this;
        }

        public Criteria andMatchStatusGreaterThan(Integer value) {
            addCriterion("match_status >", value, "matchStatus");
            return (Criteria) this;
        }

        public Criteria andMatchStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("match_status >=", value, "matchStatus");
            return (Criteria) this;
        }

        public Criteria andMatchStatusLessThan(Integer value) {
            addCriterion("match_status <", value, "matchStatus");
            return (Criteria) this;
        }

        public Criteria andMatchStatusLessThanOrEqualTo(Integer value) {
            addCriterion("match_status <=", value, "matchStatus");
            return (Criteria) this;
        }

        public Criteria andMatchStatusIn(List<Integer> values) {
            addCriterion("match_status in", values, "matchStatus");
            return (Criteria) this;
        }

        public Criteria andMatchStatusNotIn(List<Integer> values) {
            addCriterion("match_status not in", values, "matchStatus");
            return (Criteria) this;
        }

        public Criteria andMatchStatusBetween(Integer value1, Integer value2) {
            addCriterion("match_status between", value1, value2, "matchStatus");
            return (Criteria) this;
        }

        public Criteria andMatchStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("match_status not between", value1, value2, "matchStatus");
            return (Criteria) this;
        }

        public Criteria andMatchTypeIsNull() {
            addCriterion("match_type is null");
            return (Criteria) this;
        }

        public Criteria andMatchTypeIsNotNull() {
            addCriterion("match_type is not null");
            return (Criteria) this;
        }

        public Criteria andMatchTypeEqualTo(Integer value) {
            addCriterion("match_type =", value, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeNotEqualTo(Integer value) {
            addCriterion("match_type <>", value, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeGreaterThan(Integer value) {
            addCriterion("match_type >", value, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("match_type >=", value, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeLessThan(Integer value) {
            addCriterion("match_type <", value, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeLessThanOrEqualTo(Integer value) {
            addCriterion("match_type <=", value, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeIn(List<Integer> values) {
            addCriterion("match_type in", values, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeNotIn(List<Integer> values) {
            addCriterion("match_type not in", values, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeBetween(Integer value1, Integer value2) {
            addCriterion("match_type between", value1, value2, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("match_type not between", value1, value2, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchLengthIsNull() {
            addCriterion("match_length is null");
            return (Criteria) this;
        }

        public Criteria andMatchLengthIsNotNull() {
            addCriterion("match_length is not null");
            return (Criteria) this;
        }

        public Criteria andMatchLengthEqualTo(Integer value) {
            addCriterion("match_length =", value, "matchLength");
            return (Criteria) this;
        }

        public Criteria andMatchLengthNotEqualTo(Integer value) {
            addCriterion("match_length <>", value, "matchLength");
            return (Criteria) this;
        }

        public Criteria andMatchLengthGreaterThan(Integer value) {
            addCriterion("match_length >", value, "matchLength");
            return (Criteria) this;
        }

        public Criteria andMatchLengthGreaterThanOrEqualTo(Integer value) {
            addCriterion("match_length >=", value, "matchLength");
            return (Criteria) this;
        }

        public Criteria andMatchLengthLessThan(Integer value) {
            addCriterion("match_length <", value, "matchLength");
            return (Criteria) this;
        }

        public Criteria andMatchLengthLessThanOrEqualTo(Integer value) {
            addCriterion("match_length <=", value, "matchLength");
            return (Criteria) this;
        }

        public Criteria andMatchLengthIn(List<Integer> values) {
            addCriterion("match_length in", values, "matchLength");
            return (Criteria) this;
        }

        public Criteria andMatchLengthNotIn(List<Integer> values) {
            addCriterion("match_length not in", values, "matchLength");
            return (Criteria) this;
        }

        public Criteria andMatchLengthBetween(Integer value1, Integer value2) {
            addCriterion("match_length between", value1, value2, "matchLength");
            return (Criteria) this;
        }

        public Criteria andMatchLengthNotBetween(Integer value1, Integer value2) {
            addCriterion("match_length not between", value1, value2, "matchLength");
            return (Criteria) this;
        }

        public Criteria andMatchPositionNameCodeIsNull() {
            addCriterion("match_position_name_code is null");
            return (Criteria) this;
        }

        public Criteria andMatchPositionNameCodeIsNotNull() {
            addCriterion("match_position_name_code is not null");
            return (Criteria) this;
        }

        public Criteria andMatchPositionNameCodeEqualTo(Long value) {
            addCriterion("match_position_name_code =", value, "matchPositionNameCode");
            return (Criteria) this;
        }

        public Criteria andMatchPositionNameCodeNotEqualTo(Long value) {
            addCriterion("match_position_name_code <>", value, "matchPositionNameCode");
            return (Criteria) this;
        }

        public Criteria andMatchPositionNameCodeGreaterThan(Long value) {
            addCriterion("match_position_name_code >", value, "matchPositionNameCode");
            return (Criteria) this;
        }

        public Criteria andMatchPositionNameCodeGreaterThanOrEqualTo(Long value) {
            addCriterion("match_position_name_code >=", value, "matchPositionNameCode");
            return (Criteria) this;
        }

        public Criteria andMatchPositionNameCodeLessThan(Long value) {
            addCriterion("match_position_name_code <", value, "matchPositionNameCode");
            return (Criteria) this;
        }

        public Criteria andMatchPositionNameCodeLessThanOrEqualTo(Long value) {
            addCriterion("match_position_name_code <=", value, "matchPositionNameCode");
            return (Criteria) this;
        }

        public Criteria andMatchPositionNameCodeIn(List<Long> values) {
            addCriterion("match_position_name_code in", values, "matchPositionNameCode");
            return (Criteria) this;
        }

        public Criteria andMatchPositionNameCodeNotIn(List<Long> values) {
            addCriterion("match_position_name_code not in", values, "matchPositionNameCode");
            return (Criteria) this;
        }

        public Criteria andMatchPositionNameCodeBetween(Long value1, Long value2) {
            addCriterion("match_position_name_code between", value1, value2, "matchPositionNameCode");
            return (Criteria) this;
        }

        public Criteria andMatchPositionNameCodeNotBetween(Long value1, Long value2) {
            addCriterion("match_position_name_code not between", value1, value2, "matchPositionNameCode");
            return (Criteria) this;
        }

        public Criteria andMatchPositionNameIsNull() {
            addCriterion("match_position_name is null");
            return (Criteria) this;
        }

        public Criteria andMatchPositionNameIsNotNull() {
            addCriterion("match_position_name is not null");
            return (Criteria) this;
        }

        public Criteria andMatchPositionNameEqualTo(String value) {
            addCriterion("match_position_name =", value, "matchPositionName");
            return (Criteria) this;
        }

        public Criteria andMatchPositionNameNotEqualTo(String value) {
            addCriterion("match_position_name <>", value, "matchPositionName");
            return (Criteria) this;
        }

        public Criteria andMatchPositionNameGreaterThan(String value) {
            addCriterion("match_position_name >", value, "matchPositionName");
            return (Criteria) this;
        }

        public Criteria andMatchPositionNameGreaterThanOrEqualTo(String value) {
            addCriterion("match_position_name >=", value, "matchPositionName");
            return (Criteria) this;
        }

        public Criteria andMatchPositionNameLessThan(String value) {
            addCriterion("match_position_name <", value, "matchPositionName");
            return (Criteria) this;
        }

        public Criteria andMatchPositionNameLessThanOrEqualTo(String value) {
            addCriterion("match_position_name <=", value, "matchPositionName");
            return (Criteria) this;
        }

        public Criteria andMatchPositionNameLike(String value) {
            addCriterion("match_position_name like", value, "matchPositionName");
            return (Criteria) this;
        }

        public Criteria andMatchPositionNameNotLike(String value) {
            addCriterion("match_position_name not like", value, "matchPositionName");
            return (Criteria) this;
        }

        public Criteria andMatchPositionNameIn(List<String> values) {
            addCriterion("match_position_name in", values, "matchPositionName");
            return (Criteria) this;
        }

        public Criteria andMatchPositionNameNotIn(List<String> values) {
            addCriterion("match_position_name not in", values, "matchPositionName");
            return (Criteria) this;
        }

        public Criteria andMatchPositionNameBetween(String value1, String value2) {
            addCriterion("match_position_name between", value1, value2, "matchPositionName");
            return (Criteria) this;
        }

        public Criteria andMatchPositionNameNotBetween(String value1, String value2) {
            addCriterion("match_position_name not between", value1, value2, "matchPositionName");
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

        public Criteria andMatchPeriodIsNull() {
            addCriterion("match_period is null");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIsNotNull() {
            addCriterion("match_period is not null");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodEqualTo(String value) {
            addCriterion("match_period =", value, "matchPeriod");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodNotEqualTo(String value) {
            addCriterion("match_period <>", value, "matchPeriod");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodGreaterThan(String value) {
            addCriterion("match_period >", value, "matchPeriod");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodGreaterThanOrEqualTo(String value) {
            addCriterion("match_period >=", value, "matchPeriod");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodLessThan(String value) {
            addCriterion("match_period <", value, "matchPeriod");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodLessThanOrEqualTo(String value) {
            addCriterion("match_period <=", value, "matchPeriod");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodLike(String value) {
            addCriterion("match_period like", value, "matchPeriod");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodNotLike(String value) {
            addCriterion("match_period not like", value, "matchPeriod");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIn(List<String> values) {
            addCriterion("match_period in", values, "matchPeriod");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodNotIn(List<String> values) {
            addCriterion("match_period not in", values, "matchPeriod");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodBetween(String value1, String value2) {
            addCriterion("match_period between", value1, value2, "matchPeriod");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodNotBetween(String value1, String value2) {
            addCriterion("match_period not between", value1, value2, "matchPeriod");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundTypeIsNull() {
            addCriterion("tournament_round_type is null");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundTypeIsNotNull() {
            addCriterion("tournament_round_type is not null");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundTypeEqualTo(String value) {
            addCriterion("tournament_round_type =", value, "tournamentRoundType");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundTypeNotEqualTo(String value) {
            addCriterion("tournament_round_type <>", value, "tournamentRoundType");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundTypeGreaterThan(String value) {
            addCriterion("tournament_round_type >", value, "tournamentRoundType");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundTypeGreaterThanOrEqualTo(String value) {
            addCriterion("tournament_round_type >=", value, "tournamentRoundType");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundTypeLessThan(String value) {
            addCriterion("tournament_round_type <", value, "tournamentRoundType");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundTypeLessThanOrEqualTo(String value) {
            addCriterion("tournament_round_type <=", value, "tournamentRoundType");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundTypeLike(String value) {
            addCriterion("tournament_round_type like", value, "tournamentRoundType");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundTypeNotLike(String value) {
            addCriterion("tournament_round_type not like", value, "tournamentRoundType");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundTypeIn(List<String> values) {
            addCriterion("tournament_round_type in", values, "tournamentRoundType");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundTypeNotIn(List<String> values) {
            addCriterion("tournament_round_type not in", values, "tournamentRoundType");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundTypeBetween(String value1, String value2) {
            addCriterion("tournament_round_type between", value1, value2, "tournamentRoundType");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundTypeNotBetween(String value1, String value2) {
            addCriterion("tournament_round_type not between", value1, value2, "tournamentRoundType");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundNumberIsNull() {
            addCriterion("tournament_round_number is null");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundNumberIsNotNull() {
            addCriterion("tournament_round_number is not null");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundNumberEqualTo(Integer value) {
            addCriterion("tournament_round_number =", value, "tournamentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundNumberNotEqualTo(Integer value) {
            addCriterion("tournament_round_number <>", value, "tournamentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundNumberGreaterThan(Integer value) {
            addCriterion("tournament_round_number >", value, "tournamentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundNumberGreaterThanOrEqualTo(Integer value) {
            addCriterion("tournament_round_number >=", value, "tournamentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundNumberLessThan(Integer value) {
            addCriterion("tournament_round_number <", value, "tournamentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundNumberLessThanOrEqualTo(Integer value) {
            addCriterion("tournament_round_number <=", value, "tournamentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundNumberIn(List<Integer> values) {
            addCriterion("tournament_round_number in", values, "tournamentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundNumberNotIn(List<Integer> values) {
            addCriterion("tournament_round_number not in", values, "tournamentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundNumberBetween(Integer value1, Integer value2) {
            addCriterion("tournament_round_number between", value1, value2, "tournamentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundNumberNotBetween(Integer value1, Integer value2) {
            addCriterion("tournament_round_number not between", value1, value2, "tournamentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundGroupIsNull() {
            addCriterion("tournament_round_group is null");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundGroupIsNotNull() {
            addCriterion("tournament_round_group is not null");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundGroupEqualTo(String value) {
            addCriterion("tournament_round_group =", value, "tournamentRoundGroup");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundGroupNotEqualTo(String value) {
            addCriterion("tournament_round_group <>", value, "tournamentRoundGroup");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundGroupGreaterThan(String value) {
            addCriterion("tournament_round_group >", value, "tournamentRoundGroup");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundGroupGreaterThanOrEqualTo(String value) {
            addCriterion("tournament_round_group >=", value, "tournamentRoundGroup");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundGroupLessThan(String value) {
            addCriterion("tournament_round_group <", value, "tournamentRoundGroup");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundGroupLessThanOrEqualTo(String value) {
            addCriterion("tournament_round_group <=", value, "tournamentRoundGroup");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundGroupLike(String value) {
            addCriterion("tournament_round_group like", value, "tournamentRoundGroup");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundGroupNotLike(String value) {
            addCriterion("tournament_round_group not like", value, "tournamentRoundGroup");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundGroupIn(List<String> values) {
            addCriterion("tournament_round_group in", values, "tournamentRoundGroup");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundGroupNotIn(List<String> values) {
            addCriterion("tournament_round_group not in", values, "tournamentRoundGroup");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundGroupBetween(String value1, String value2) {
            addCriterion("tournament_round_group between", value1, value2, "tournamentRoundGroup");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundGroupNotBetween(String value1, String value2) {
            addCriterion("tournament_round_group not between", value1, value2, "tournamentRoundGroup");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundNameIsNull() {
            addCriterion("tournament_round_name is null");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundNameIsNotNull() {
            addCriterion("tournament_round_name is not null");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundNameEqualTo(String value) {
            addCriterion("tournament_round_name =", value, "tournamentRoundName");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundNameNotEqualTo(String value) {
            addCriterion("tournament_round_name <>", value, "tournamentRoundName");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundNameGreaterThan(String value) {
            addCriterion("tournament_round_name >", value, "tournamentRoundName");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundNameGreaterThanOrEqualTo(String value) {
            addCriterion("tournament_round_name >=", value, "tournamentRoundName");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundNameLessThan(String value) {
            addCriterion("tournament_round_name <", value, "tournamentRoundName");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundNameLessThanOrEqualTo(String value) {
            addCriterion("tournament_round_name <=", value, "tournamentRoundName");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundNameLike(String value) {
            addCriterion("tournament_round_name like", value, "tournamentRoundName");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundNameNotLike(String value) {
            addCriterion("tournament_round_name not like", value, "tournamentRoundName");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundNameIn(List<String> values) {
            addCriterion("tournament_round_name in", values, "tournamentRoundName");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundNameNotIn(List<String> values) {
            addCriterion("tournament_round_name not in", values, "tournamentRoundName");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundNameBetween(String value1, String value2) {
            addCriterion("tournament_round_name between", value1, value2, "tournamentRoundName");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundNameNotBetween(String value1, String value2) {
            addCriterion("tournament_round_name not between", value1, value2, "tournamentRoundName");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundPhaseIsNull() {
            addCriterion("tournament_round_phase is null");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundPhaseIsNotNull() {
            addCriterion("tournament_round_phase is not null");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundPhaseEqualTo(String value) {
            addCriterion("tournament_round_phase =", value, "tournamentRoundPhase");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundPhaseNotEqualTo(String value) {
            addCriterion("tournament_round_phase <>", value, "tournamentRoundPhase");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundPhaseGreaterThan(String value) {
            addCriterion("tournament_round_phase >", value, "tournamentRoundPhase");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundPhaseGreaterThanOrEqualTo(String value) {
            addCriterion("tournament_round_phase >=", value, "tournamentRoundPhase");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundPhaseLessThan(String value) {
            addCriterion("tournament_round_phase <", value, "tournamentRoundPhase");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundPhaseLessThanOrEqualTo(String value) {
            addCriterion("tournament_round_phase <=", value, "tournamentRoundPhase");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundPhaseLike(String value) {
            addCriterion("tournament_round_phase like", value, "tournamentRoundPhase");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundPhaseNotLike(String value) {
            addCriterion("tournament_round_phase not like", value, "tournamentRoundPhase");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundPhaseIn(List<String> values) {
            addCriterion("tournament_round_phase in", values, "tournamentRoundPhase");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundPhaseNotIn(List<String> values) {
            addCriterion("tournament_round_phase not in", values, "tournamentRoundPhase");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundPhaseBetween(String value1, String value2) {
            addCriterion("tournament_round_phase between", value1, value2, "tournamentRoundPhase");
            return (Criteria) this;
        }

        public Criteria andTournamentRoundPhaseNotBetween(String value1, String value2) {
            addCriterion("tournament_round_phase not between", value1, value2, "tournamentRoundPhase");
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

        public Criteria andHomeFormationIsNull() {
            addCriterion("home_formation is null");
            return (Criteria) this;
        }

        public Criteria andHomeFormationIsNotNull() {
            addCriterion("home_formation is not null");
            return (Criteria) this;
        }

        public Criteria andHomeFormationEqualTo(String value) {
            addCriterion("home_formation =", value, "homeFormation");
            return (Criteria) this;
        }

        public Criteria andHomeFormationNotEqualTo(String value) {
            addCriterion("home_formation <>", value, "homeFormation");
            return (Criteria) this;
        }

        public Criteria andHomeFormationGreaterThan(String value) {
            addCriterion("home_formation >", value, "homeFormation");
            return (Criteria) this;
        }

        public Criteria andHomeFormationGreaterThanOrEqualTo(String value) {
            addCriterion("home_formation >=", value, "homeFormation");
            return (Criteria) this;
        }

        public Criteria andHomeFormationLessThan(String value) {
            addCriterion("home_formation <", value, "homeFormation");
            return (Criteria) this;
        }

        public Criteria andHomeFormationLessThanOrEqualTo(String value) {
            addCriterion("home_formation <=", value, "homeFormation");
            return (Criteria) this;
        }

        public Criteria andHomeFormationLike(String value) {
            addCriterion("home_formation like", value, "homeFormation");
            return (Criteria) this;
        }

        public Criteria andHomeFormationNotLike(String value) {
            addCriterion("home_formation not like", value, "homeFormation");
            return (Criteria) this;
        }

        public Criteria andHomeFormationIn(List<String> values) {
            addCriterion("home_formation in", values, "homeFormation");
            return (Criteria) this;
        }

        public Criteria andHomeFormationNotIn(List<String> values) {
            addCriterion("home_formation not in", values, "homeFormation");
            return (Criteria) this;
        }

        public Criteria andHomeFormationBetween(String value1, String value2) {
            addCriterion("home_formation between", value1, value2, "homeFormation");
            return (Criteria) this;
        }

        public Criteria andHomeFormationNotBetween(String value1, String value2) {
            addCriterion("home_formation not between", value1, value2, "homeFormation");
            return (Criteria) this;
        }

        public Criteria andAwayFormationIsNull() {
            addCriterion("away_formation is null");
            return (Criteria) this;
        }

        public Criteria andAwayFormationIsNotNull() {
            addCriterion("away_formation is not null");
            return (Criteria) this;
        }

        public Criteria andAwayFormationEqualTo(String value) {
            addCriterion("away_formation =", value, "awayFormation");
            return (Criteria) this;
        }

        public Criteria andAwayFormationNotEqualTo(String value) {
            addCriterion("away_formation <>", value, "awayFormation");
            return (Criteria) this;
        }

        public Criteria andAwayFormationGreaterThan(String value) {
            addCriterion("away_formation >", value, "awayFormation");
            return (Criteria) this;
        }

        public Criteria andAwayFormationGreaterThanOrEqualTo(String value) {
            addCriterion("away_formation >=", value, "awayFormation");
            return (Criteria) this;
        }

        public Criteria andAwayFormationLessThan(String value) {
            addCriterion("away_formation <", value, "awayFormation");
            return (Criteria) this;
        }

        public Criteria andAwayFormationLessThanOrEqualTo(String value) {
            addCriterion("away_formation <=", value, "awayFormation");
            return (Criteria) this;
        }

        public Criteria andAwayFormationLike(String value) {
            addCriterion("away_formation like", value, "awayFormation");
            return (Criteria) this;
        }

        public Criteria andAwayFormationNotLike(String value) {
            addCriterion("away_formation not like", value, "awayFormation");
            return (Criteria) this;
        }

        public Criteria andAwayFormationIn(List<String> values) {
            addCriterion("away_formation in", values, "awayFormation");
            return (Criteria) this;
        }

        public Criteria andAwayFormationNotIn(List<String> values) {
            addCriterion("away_formation not in", values, "awayFormation");
            return (Criteria) this;
        }

        public Criteria andAwayFormationBetween(String value1, String value2) {
            addCriterion("away_formation between", value1, value2, "awayFormation");
            return (Criteria) this;
        }

        public Criteria andAwayFormationNotBetween(String value1, String value2) {
            addCriterion("away_formation not between", value1, value2, "awayFormation");
            return (Criteria) this;
        }

        public Criteria andTeamChangeStatusIsNull() {
            addCriterion("team_change_status is null");
            return (Criteria) this;
        }

        public Criteria andTeamChangeStatusIsNotNull() {
            addCriterion("team_change_status is not null");
            return (Criteria) this;
        }

        public Criteria andTeamChangeStatusEqualTo(Integer value) {
            addCriterion("team_change_status =", value, "teamChangeStatus");
            return (Criteria) this;
        }

        public Criteria andTeamChangeStatusNotEqualTo(Integer value) {
            addCriterion("team_change_status <>", value, "teamChangeStatus");
            return (Criteria) this;
        }

        public Criteria andTeamChangeStatusGreaterThan(Integer value) {
            addCriterion("team_change_status >", value, "teamChangeStatus");
            return (Criteria) this;
        }

        public Criteria andTeamChangeStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("team_change_status >=", value, "teamChangeStatus");
            return (Criteria) this;
        }

        public Criteria andTeamChangeStatusLessThan(Integer value) {
            addCriterion("team_change_status <", value, "teamChangeStatus");
            return (Criteria) this;
        }

        public Criteria andTeamChangeStatusLessThanOrEqualTo(Integer value) {
            addCriterion("team_change_status <=", value, "teamChangeStatus");
            return (Criteria) this;
        }

        public Criteria andTeamChangeStatusIn(List<Integer> values) {
            addCriterion("team_change_status in", values, "teamChangeStatus");
            return (Criteria) this;
        }

        public Criteria andTeamChangeStatusNotIn(List<Integer> values) {
            addCriterion("team_change_status not in", values, "teamChangeStatus");
            return (Criteria) this;
        }

        public Criteria andTeamChangeStatusBetween(Integer value1, Integer value2) {
            addCriterion("team_change_status between", value1, value2, "teamChangeStatus");
            return (Criteria) this;
        }

        public Criteria andTeamChangeStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("team_change_status not between", value1, value2, "teamChangeStatus");
            return (Criteria) this;
        }

        public Criteria andHomeAwayPlayerNameIsNull() {
            addCriterion("home_away_player_name is null");
            return (Criteria) this;
        }

        public Criteria andHomeAwayPlayerNameIsNotNull() {
            addCriterion("home_away_player_name is not null");
            return (Criteria) this;
        }

        public Criteria andHomeAwayPlayerNameEqualTo(String value) {
            addCriterion("home_away_player_name =", value, "homeAwayPlayerName");
            return (Criteria) this;
        }

        public Criteria andHomeAwayPlayerNameNotEqualTo(String value) {
            addCriterion("home_away_player_name <>", value, "homeAwayPlayerName");
            return (Criteria) this;
        }

        public Criteria andHomeAwayPlayerNameGreaterThan(String value) {
            addCriterion("home_away_player_name >", value, "homeAwayPlayerName");
            return (Criteria) this;
        }

        public Criteria andHomeAwayPlayerNameGreaterThanOrEqualTo(String value) {
            addCriterion("home_away_player_name >=", value, "homeAwayPlayerName");
            return (Criteria) this;
        }

        public Criteria andHomeAwayPlayerNameLessThan(String value) {
            addCriterion("home_away_player_name <", value, "homeAwayPlayerName");
            return (Criteria) this;
        }

        public Criteria andHomeAwayPlayerNameLessThanOrEqualTo(String value) {
            addCriterion("home_away_player_name <=", value, "homeAwayPlayerName");
            return (Criteria) this;
        }

        public Criteria andHomeAwayPlayerNameLike(String value) {
            addCriterion("home_away_player_name like", value, "homeAwayPlayerName");
            return (Criteria) this;
        }

        public Criteria andHomeAwayPlayerNameNotLike(String value) {
            addCriterion("home_away_player_name not like", value, "homeAwayPlayerName");
            return (Criteria) this;
        }

        public Criteria andHomeAwayPlayerNameIn(List<String> values) {
            addCriterion("home_away_player_name in", values, "homeAwayPlayerName");
            return (Criteria) this;
        }

        public Criteria andHomeAwayPlayerNameNotIn(List<String> values) {
            addCriterion("home_away_player_name not in", values, "homeAwayPlayerName");
            return (Criteria) this;
        }

        public Criteria andHomeAwayPlayerNameBetween(String value1, String value2) {
            addCriterion("home_away_player_name between", value1, value2, "homeAwayPlayerName");
            return (Criteria) this;
        }

        public Criteria andHomeAwayPlayerNameNotBetween(String value1, String value2) {
            addCriterion("home_away_player_name not between", value1, value2, "homeAwayPlayerName");
            return (Criteria) this;
        }

        public Criteria andCompetitorTypeIsNull() {
            addCriterion("competitor_type is null");
            return (Criteria) this;
        }

        public Criteria andCompetitorTypeIsNotNull() {
            addCriterion("competitor_type is not null");
            return (Criteria) this;
        }

        public Criteria andCompetitorTypeEqualTo(Integer value) {
            addCriterion("competitor_type =", value, "competitorType");
            return (Criteria) this;
        }

        public Criteria andCompetitorTypeNotEqualTo(Integer value) {
            addCriterion("competitor_type <>", value, "competitorType");
            return (Criteria) this;
        }

        public Criteria andCompetitorTypeGreaterThan(Integer value) {
            addCriterion("competitor_type >", value, "competitorType");
            return (Criteria) this;
        }

        public Criteria andCompetitorTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("competitor_type >=", value, "competitorType");
            return (Criteria) this;
        }

        public Criteria andCompetitorTypeLessThan(Integer value) {
            addCriterion("competitor_type <", value, "competitorType");
            return (Criteria) this;
        }

        public Criteria andCompetitorTypeLessThanOrEqualTo(Integer value) {
            addCriterion("competitor_type <=", value, "competitorType");
            return (Criteria) this;
        }

        public Criteria andCompetitorTypeIn(List<Integer> values) {
            addCriterion("competitor_type in", values, "competitorType");
            return (Criteria) this;
        }

        public Criteria andCompetitorTypeNotIn(List<Integer> values) {
            addCriterion("competitor_type not in", values, "competitorType");
            return (Criteria) this;
        }

        public Criteria andCompetitorTypeBetween(Integer value1, Integer value2) {
            addCriterion("competitor_type between", value1, value2, "competitorType");
            return (Criteria) this;
        }

        public Criteria andCompetitorTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("competitor_type not between", value1, value2, "competitorType");
            return (Criteria) this;
        }

        public Criteria andAccelerationFactorIsNull() {
            addCriterion("acceleration_factor is null");
            return (Criteria) this;
        }

        public Criteria andAccelerationFactorIsNotNull() {
            addCriterion("acceleration_factor is not null");
            return (Criteria) this;
        }

        public Criteria andAccelerationFactorEqualTo(String value) {
            addCriterion("acceleration_factor =", value, "accelerationFactor");
            return (Criteria) this;
        }

        public Criteria andAccelerationFactorNotEqualTo(String value) {
            addCriterion("acceleration_factor <>", value, "accelerationFactor");
            return (Criteria) this;
        }

        public Criteria andAccelerationFactorGreaterThan(String value) {
            addCriterion("acceleration_factor >", value, "accelerationFactor");
            return (Criteria) this;
        }

        public Criteria andAccelerationFactorGreaterThanOrEqualTo(String value) {
            addCriterion("acceleration_factor >=", value, "accelerationFactor");
            return (Criteria) this;
        }

        public Criteria andAccelerationFactorLessThan(String value) {
            addCriterion("acceleration_factor <", value, "accelerationFactor");
            return (Criteria) this;
        }

        public Criteria andAccelerationFactorLessThanOrEqualTo(String value) {
            addCriterion("acceleration_factor <=", value, "accelerationFactor");
            return (Criteria) this;
        }

        public Criteria andAccelerationFactorLike(String value) {
            addCriterion("acceleration_factor like", value, "accelerationFactor");
            return (Criteria) this;
        }

        public Criteria andAccelerationFactorNotLike(String value) {
            addCriterion("acceleration_factor not like", value, "accelerationFactor");
            return (Criteria) this;
        }

        public Criteria andAccelerationFactorIn(List<String> values) {
            addCriterion("acceleration_factor in", values, "accelerationFactor");
            return (Criteria) this;
        }

        public Criteria andAccelerationFactorNotIn(List<String> values) {
            addCriterion("acceleration_factor not in", values, "accelerationFactor");
            return (Criteria) this;
        }

        public Criteria andAccelerationFactorBetween(String value1, String value2) {
            addCriterion("acceleration_factor between", value1, value2, "accelerationFactor");
            return (Criteria) this;
        }

        public Criteria andAccelerationFactorNotBetween(String value1, String value2) {
            addCriterion("acceleration_factor not between", value1, value2, "accelerationFactor");
            return (Criteria) this;
        }

        public Criteria andTournamentChangeStatusIsNull() {
            addCriterion("tournament_change_status is null");
            return (Criteria) this;
        }

        public Criteria andTournamentChangeStatusIsNotNull() {
            addCriterion("tournament_change_status is not null");
            return (Criteria) this;
        }

        public Criteria andTournamentChangeStatusEqualTo(Integer value) {
            addCriterion("tournament_change_status =", value, "tournamentChangeStatus");
            return (Criteria) this;
        }

        public Criteria andTournamentChangeStatusNotEqualTo(Integer value) {
            addCriterion("tournament_change_status <>", value, "tournamentChangeStatus");
            return (Criteria) this;
        }

        public Criteria andTournamentChangeStatusGreaterThan(Integer value) {
            addCriterion("tournament_change_status >", value, "tournamentChangeStatus");
            return (Criteria) this;
        }

        public Criteria andTournamentChangeStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("tournament_change_status >=", value, "tournamentChangeStatus");
            return (Criteria) this;
        }

        public Criteria andTournamentChangeStatusLessThan(Integer value) {
            addCriterion("tournament_change_status <", value, "tournamentChangeStatus");
            return (Criteria) this;
        }

        public Criteria andTournamentChangeStatusLessThanOrEqualTo(Integer value) {
            addCriterion("tournament_change_status <=", value, "tournamentChangeStatus");
            return (Criteria) this;
        }

        public Criteria andTournamentChangeStatusIn(List<Integer> values) {
            addCriterion("tournament_change_status in", values, "tournamentChangeStatus");
            return (Criteria) this;
        }

        public Criteria andTournamentChangeStatusNotIn(List<Integer> values) {
            addCriterion("tournament_change_status not in", values, "tournamentChangeStatus");
            return (Criteria) this;
        }

        public Criteria andTournamentChangeStatusBetween(Integer value1, Integer value2) {
            addCriterion("tournament_change_status between", value1, value2, "tournamentChangeStatus");
            return (Criteria) this;
        }

        public Criteria andTournamentChangeStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("tournament_change_status not between", value1, value2, "tournamentChangeStatus");
            return (Criteria) this;
        }

        public Criteria andInterruptionCancellationStatusIsNull() {
            addCriterion("interruption_cancellation_status is null");
            return (Criteria) this;
        }

        public Criteria andInterruptionCancellationStatusIsNotNull() {
            addCriterion("interruption_cancellation_status is not null");
            return (Criteria) this;
        }

        public Criteria andInterruptionCancellationStatusEqualTo(Integer value) {
            addCriterion("interruption_cancellation_status =", value, "interruptionCancellationStatus");
            return (Criteria) this;
        }

        public Criteria andInterruptionCancellationStatusNotEqualTo(Integer value) {
            addCriterion("interruption_cancellation_status <>", value, "interruptionCancellationStatus");
            return (Criteria) this;
        }

        public Criteria andInterruptionCancellationStatusGreaterThan(Integer value) {
            addCriterion("interruption_cancellation_status >", value, "interruptionCancellationStatus");
            return (Criteria) this;
        }

        public Criteria andInterruptionCancellationStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("interruption_cancellation_status >=", value, "interruptionCancellationStatus");
            return (Criteria) this;
        }

        public Criteria andInterruptionCancellationStatusLessThan(Integer value) {
            addCriterion("interruption_cancellation_status <", value, "interruptionCancellationStatus");
            return (Criteria) this;
        }

        public Criteria andInterruptionCancellationStatusLessThanOrEqualTo(Integer value) {
            addCriterion("interruption_cancellation_status <=", value, "interruptionCancellationStatus");
            return (Criteria) this;
        }

        public Criteria andInterruptionCancellationStatusIn(List<Integer> values) {
            addCriterion("interruption_cancellation_status in", values, "interruptionCancellationStatus");
            return (Criteria) this;
        }

        public Criteria andInterruptionCancellationStatusNotIn(List<Integer> values) {
            addCriterion("interruption_cancellation_status not in", values, "interruptionCancellationStatus");
            return (Criteria) this;
        }

        public Criteria andInterruptionCancellationStatusBetween(Integer value1, Integer value2) {
            addCriterion("interruption_cancellation_status between", value1, value2, "interruptionCancellationStatus");
            return (Criteria) this;
        }

        public Criteria andInterruptionCancellationStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("interruption_cancellation_status not between", value1, value2, "interruptionCancellationStatus");
            return (Criteria) this;
        }

        public Criteria andLiveEventSourceIsNull() {
            addCriterion("live_event_source is null");
            return (Criteria) this;
        }

        public Criteria andLiveEventSourceIsNotNull() {
            addCriterion("live_event_source is not null");
            return (Criteria) this;
        }

        public Criteria andLiveEventSourceEqualTo(Integer value) {
            addCriterion("live_event_source =", value, "liveEventSource");
            return (Criteria) this;
        }

        public Criteria andLiveEventSourceNotEqualTo(Integer value) {
            addCriterion("live_event_source <>", value, "liveEventSource");
            return (Criteria) this;
        }

        public Criteria andLiveEventSourceGreaterThan(Integer value) {
            addCriterion("live_event_source >", value, "liveEventSource");
            return (Criteria) this;
        }

        public Criteria andLiveEventSourceGreaterThanOrEqualTo(Integer value) {
            addCriterion("live_event_source >=", value, "liveEventSource");
            return (Criteria) this;
        }

        public Criteria andLiveEventSourceLessThan(Integer value) {
            addCriterion("live_event_source <", value, "liveEventSource");
            return (Criteria) this;
        }

        public Criteria andLiveEventSourceLessThanOrEqualTo(Integer value) {
            addCriterion("live_event_source <=", value, "liveEventSource");
            return (Criteria) this;
        }

        public Criteria andLiveEventSourceIn(List<Integer> values) {
            addCriterion("live_event_source in", values, "liveEventSource");
            return (Criteria) this;
        }

        public Criteria andLiveEventSourceNotIn(List<Integer> values) {
            addCriterion("live_event_source not in", values, "liveEventSource");
            return (Criteria) this;
        }

        public Criteria andLiveEventSourceBetween(Integer value1, Integer value2) {
            addCriterion("live_event_source between", value1, value2, "liveEventSource");
            return (Criteria) this;
        }

        public Criteria andLiveEventSourceNotBetween(Integer value1, Integer value2) {
            addCriterion("live_event_source not between", value1, value2, "liveEventSource");
            return (Criteria) this;
        }

        public Criteria andHomeExpectationXgIsNull() {
            addCriterion("home_expectation_xg is null");
            return (Criteria) this;
        }

        public Criteria andHomeExpectationXgIsNotNull() {
            addCriterion("home_expectation_xg is not null");
            return (Criteria) this;
        }

        public Criteria andHomeExpectationXgEqualTo(BigDecimal value) {
            addCriterion("home_expectation_xg =", value, "homeExpectationXg");
            return (Criteria) this;
        }

        public Criteria andHomeExpectationXgNotEqualTo(BigDecimal value) {
            addCriterion("home_expectation_xg <>", value, "homeExpectationXg");
            return (Criteria) this;
        }

        public Criteria andHomeExpectationXgGreaterThan(BigDecimal value) {
            addCriterion("home_expectation_xg >", value, "homeExpectationXg");
            return (Criteria) this;
        }

        public Criteria andHomeExpectationXgGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("home_expectation_xg >=", value, "homeExpectationXg");
            return (Criteria) this;
        }

        public Criteria andHomeExpectationXgLessThan(BigDecimal value) {
            addCriterion("home_expectation_xg <", value, "homeExpectationXg");
            return (Criteria) this;
        }

        public Criteria andHomeExpectationXgLessThanOrEqualTo(BigDecimal value) {
            addCriterion("home_expectation_xg <=", value, "homeExpectationXg");
            return (Criteria) this;
        }

        public Criteria andHomeExpectationXgIn(List<BigDecimal> values) {
            addCriterion("home_expectation_xg in", values, "homeExpectationXg");
            return (Criteria) this;
        }

        public Criteria andHomeExpectationXgNotIn(List<BigDecimal> values) {
            addCriterion("home_expectation_xg not in", values, "homeExpectationXg");
            return (Criteria) this;
        }

        public Criteria andHomeExpectationXgBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("home_expectation_xg between", value1, value2, "homeExpectationXg");
            return (Criteria) this;
        }

        public Criteria andHomeExpectationXgNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("home_expectation_xg not between", value1, value2, "homeExpectationXg");
            return (Criteria) this;
        }

        public Criteria andHomeExpectationLossIsNull() {
            addCriterion("home_expectation_loss is null");
            return (Criteria) this;
        }

        public Criteria andHomeExpectationLossIsNotNull() {
            addCriterion("home_expectation_loss is not null");
            return (Criteria) this;
        }

        public Criteria andHomeExpectationLossEqualTo(BigDecimal value) {
            addCriterion("home_expectation_loss =", value, "homeExpectationLoss");
            return (Criteria) this;
        }

        public Criteria andHomeExpectationLossNotEqualTo(BigDecimal value) {
            addCriterion("home_expectation_loss <>", value, "homeExpectationLoss");
            return (Criteria) this;
        }

        public Criteria andHomeExpectationLossGreaterThan(BigDecimal value) {
            addCriterion("home_expectation_loss >", value, "homeExpectationLoss");
            return (Criteria) this;
        }

        public Criteria andHomeExpectationLossGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("home_expectation_loss >=", value, "homeExpectationLoss");
            return (Criteria) this;
        }

        public Criteria andHomeExpectationLossLessThan(BigDecimal value) {
            addCriterion("home_expectation_loss <", value, "homeExpectationLoss");
            return (Criteria) this;
        }

        public Criteria andHomeExpectationLossLessThanOrEqualTo(BigDecimal value) {
            addCriterion("home_expectation_loss <=", value, "homeExpectationLoss");
            return (Criteria) this;
        }

        public Criteria andHomeExpectationLossIn(List<BigDecimal> values) {
            addCriterion("home_expectation_loss in", values, "homeExpectationLoss");
            return (Criteria) this;
        }

        public Criteria andHomeExpectationLossNotIn(List<BigDecimal> values) {
            addCriterion("home_expectation_loss not in", values, "homeExpectationLoss");
            return (Criteria) this;
        }

        public Criteria andHomeExpectationLossBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("home_expectation_loss between", value1, value2, "homeExpectationLoss");
            return (Criteria) this;
        }

        public Criteria andHomeExpectationLossNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("home_expectation_loss not between", value1, value2, "homeExpectationLoss");
            return (Criteria) this;
        }

        public Criteria andAwayExpectationXgIsNull() {
            addCriterion("away_expectation_xg is null");
            return (Criteria) this;
        }

        public Criteria andAwayExpectationXgIsNotNull() {
            addCriterion("away_expectation_xg is not null");
            return (Criteria) this;
        }

        public Criteria andAwayExpectationXgEqualTo(BigDecimal value) {
            addCriterion("away_expectation_xg =", value, "awayExpectationXg");
            return (Criteria) this;
        }

        public Criteria andAwayExpectationXgNotEqualTo(BigDecimal value) {
            addCriterion("away_expectation_xg <>", value, "awayExpectationXg");
            return (Criteria) this;
        }

        public Criteria andAwayExpectationXgGreaterThan(BigDecimal value) {
            addCriterion("away_expectation_xg >", value, "awayExpectationXg");
            return (Criteria) this;
        }

        public Criteria andAwayExpectationXgGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("away_expectation_xg >=", value, "awayExpectationXg");
            return (Criteria) this;
        }

        public Criteria andAwayExpectationXgLessThan(BigDecimal value) {
            addCriterion("away_expectation_xg <", value, "awayExpectationXg");
            return (Criteria) this;
        }

        public Criteria andAwayExpectationXgLessThanOrEqualTo(BigDecimal value) {
            addCriterion("away_expectation_xg <=", value, "awayExpectationXg");
            return (Criteria) this;
        }

        public Criteria andAwayExpectationXgIn(List<BigDecimal> values) {
            addCriterion("away_expectation_xg in", values, "awayExpectationXg");
            return (Criteria) this;
        }

        public Criteria andAwayExpectationXgNotIn(List<BigDecimal> values) {
            addCriterion("away_expectation_xg not in", values, "awayExpectationXg");
            return (Criteria) this;
        }

        public Criteria andAwayExpectationXgBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("away_expectation_xg between", value1, value2, "awayExpectationXg");
            return (Criteria) this;
        }

        public Criteria andAwayExpectationXgNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("away_expectation_xg not between", value1, value2, "awayExpectationXg");
            return (Criteria) this;
        }

        public Criteria andAwayExpectationLossIsNull() {
            addCriterion("away_expectation_loss is null");
            return (Criteria) this;
        }

        public Criteria andAwayExpectationLossIsNotNull() {
            addCriterion("away_expectation_loss is not null");
            return (Criteria) this;
        }

        public Criteria andAwayExpectationLossEqualTo(BigDecimal value) {
            addCriterion("away_expectation_loss =", value, "awayExpectationLoss");
            return (Criteria) this;
        }

        public Criteria andAwayExpectationLossNotEqualTo(BigDecimal value) {
            addCriterion("away_expectation_loss <>", value, "awayExpectationLoss");
            return (Criteria) this;
        }

        public Criteria andAwayExpectationLossGreaterThan(BigDecimal value) {
            addCriterion("away_expectation_loss >", value, "awayExpectationLoss");
            return (Criteria) this;
        }

        public Criteria andAwayExpectationLossGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("away_expectation_loss >=", value, "awayExpectationLoss");
            return (Criteria) this;
        }

        public Criteria andAwayExpectationLossLessThan(BigDecimal value) {
            addCriterion("away_expectation_loss <", value, "awayExpectationLoss");
            return (Criteria) this;
        }

        public Criteria andAwayExpectationLossLessThanOrEqualTo(BigDecimal value) {
            addCriterion("away_expectation_loss <=", value, "awayExpectationLoss");
            return (Criteria) this;
        }

        public Criteria andAwayExpectationLossIn(List<BigDecimal> values) {
            addCriterion("away_expectation_loss in", values, "awayExpectationLoss");
            return (Criteria) this;
        }

        public Criteria andAwayExpectationLossNotIn(List<BigDecimal> values) {
            addCriterion("away_expectation_loss not in", values, "awayExpectationLoss");
            return (Criteria) this;
        }

        public Criteria andAwayExpectationLossBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("away_expectation_loss between", value1, value2, "awayExpectationLoss");
            return (Criteria) this;
        }

        public Criteria andAwayExpectationLossNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("away_expectation_loss not between", value1, value2, "awayExpectationLoss");
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