package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class StandardMatchInfoHisExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public StandardMatchInfoHisExample() {
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

        public Criteria andThirdMatchIdIsNull() {
            addCriterion("third_match_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdIsNotNull() {
            addCriterion("third_match_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdEqualTo(Long value) {
            addCriterion("third_match_id =", value, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdNotEqualTo(Long value) {
            addCriterion("third_match_id <>", value, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdGreaterThan(Long value) {
            addCriterion("third_match_id >", value, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdGreaterThanOrEqualTo(Long value) {
            addCriterion("third_match_id >=", value, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdLessThan(Long value) {
            addCriterion("third_match_id <", value, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdLessThanOrEqualTo(Long value) {
            addCriterion("third_match_id <=", value, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdIn(List<Long> values) {
            addCriterion("third_match_id in", values, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdNotIn(List<Long> values) {
            addCriterion("third_match_id not in", values, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdBetween(Long value1, Long value2) {
            addCriterion("third_match_id between", value1, value2, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdNotBetween(Long value1, Long value2) {
            addCriterion("third_match_id not between", value1, value2, "thirdMatchId");
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

        public Criteria andPreMatchBusinessIsNull() {
            addCriterion("pre_match_business is null");
            return (Criteria) this;
        }

        public Criteria andPreMatchBusinessIsNotNull() {
            addCriterion("pre_match_business is not null");
            return (Criteria) this;
        }

        public Criteria andPreMatchBusinessEqualTo(Integer value) {
            addCriterion("pre_match_business =", value, "preMatchBusiness");
            return (Criteria) this;
        }

        public Criteria andPreMatchBusinessNotEqualTo(Integer value) {
            addCriterion("pre_match_business <>", value, "preMatchBusiness");
            return (Criteria) this;
        }

        public Criteria andPreMatchBusinessGreaterThan(Integer value) {
            addCriterion("pre_match_business >", value, "preMatchBusiness");
            return (Criteria) this;
        }

        public Criteria andPreMatchBusinessGreaterThanOrEqualTo(Integer value) {
            addCriterion("pre_match_business >=", value, "preMatchBusiness");
            return (Criteria) this;
        }

        public Criteria andPreMatchBusinessLessThan(Integer value) {
            addCriterion("pre_match_business <", value, "preMatchBusiness");
            return (Criteria) this;
        }

        public Criteria andPreMatchBusinessLessThanOrEqualTo(Integer value) {
            addCriterion("pre_match_business <=", value, "preMatchBusiness");
            return (Criteria) this;
        }

        public Criteria andPreMatchBusinessIn(List<Integer> values) {
            addCriterion("pre_match_business in", values, "preMatchBusiness");
            return (Criteria) this;
        }

        public Criteria andPreMatchBusinessNotIn(List<Integer> values) {
            addCriterion("pre_match_business not in", values, "preMatchBusiness");
            return (Criteria) this;
        }

        public Criteria andPreMatchBusinessBetween(Integer value1, Integer value2) {
            addCriterion("pre_match_business between", value1, value2, "preMatchBusiness");
            return (Criteria) this;
        }

        public Criteria andPreMatchBusinessNotBetween(Integer value1, Integer value2) {
            addCriterion("pre_match_business not between", value1, value2, "preMatchBusiness");
            return (Criteria) this;
        }

        public Criteria andLiveOddBusinessIsNull() {
            addCriterion("live_odd_business is null");
            return (Criteria) this;
        }

        public Criteria andLiveOddBusinessIsNotNull() {
            addCriterion("live_odd_business is not null");
            return (Criteria) this;
        }

        public Criteria andLiveOddBusinessEqualTo(Integer value) {
            addCriterion("live_odd_business =", value, "liveOddBusiness");
            return (Criteria) this;
        }

        public Criteria andLiveOddBusinessNotEqualTo(Integer value) {
            addCriterion("live_odd_business <>", value, "liveOddBusiness");
            return (Criteria) this;
        }

        public Criteria andLiveOddBusinessGreaterThan(Integer value) {
            addCriterion("live_odd_business >", value, "liveOddBusiness");
            return (Criteria) this;
        }

        public Criteria andLiveOddBusinessGreaterThanOrEqualTo(Integer value) {
            addCriterion("live_odd_business >=", value, "liveOddBusiness");
            return (Criteria) this;
        }

        public Criteria andLiveOddBusinessLessThan(Integer value) {
            addCriterion("live_odd_business <", value, "liveOddBusiness");
            return (Criteria) this;
        }

        public Criteria andLiveOddBusinessLessThanOrEqualTo(Integer value) {
            addCriterion("live_odd_business <=", value, "liveOddBusiness");
            return (Criteria) this;
        }

        public Criteria andLiveOddBusinessIn(List<Integer> values) {
            addCriterion("live_odd_business in", values, "liveOddBusiness");
            return (Criteria) this;
        }

        public Criteria andLiveOddBusinessNotIn(List<Integer> values) {
            addCriterion("live_odd_business not in", values, "liveOddBusiness");
            return (Criteria) this;
        }

        public Criteria andLiveOddBusinessBetween(Integer value1, Integer value2) {
            addCriterion("live_odd_business between", value1, value2, "liveOddBusiness");
            return (Criteria) this;
        }

        public Criteria andLiveOddBusinessNotBetween(Integer value1, Integer value2) {
            addCriterion("live_odd_business not between", value1, value2, "liveOddBusiness");
            return (Criteria) this;
        }

        public Criteria andOperateMatchStatusIsNull() {
            addCriterion("operate_match_status is null");
            return (Criteria) this;
        }

        public Criteria andOperateMatchStatusIsNotNull() {
            addCriterion("operate_match_status is not null");
            return (Criteria) this;
        }

        public Criteria andOperateMatchStatusEqualTo(Integer value) {
            addCriterion("operate_match_status =", value, "operateMatchStatus");
            return (Criteria) this;
        }

        public Criteria andOperateMatchStatusNotEqualTo(Integer value) {
            addCriterion("operate_match_status <>", value, "operateMatchStatus");
            return (Criteria) this;
        }

        public Criteria andOperateMatchStatusGreaterThan(Integer value) {
            addCriterion("operate_match_status >", value, "operateMatchStatus");
            return (Criteria) this;
        }

        public Criteria andOperateMatchStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("operate_match_status >=", value, "operateMatchStatus");
            return (Criteria) this;
        }

        public Criteria andOperateMatchStatusLessThan(Integer value) {
            addCriterion("operate_match_status <", value, "operateMatchStatus");
            return (Criteria) this;
        }

        public Criteria andOperateMatchStatusLessThanOrEqualTo(Integer value) {
            addCriterion("operate_match_status <=", value, "operateMatchStatus");
            return (Criteria) this;
        }

        public Criteria andOperateMatchStatusIn(List<Integer> values) {
            addCriterion("operate_match_status in", values, "operateMatchStatus");
            return (Criteria) this;
        }

        public Criteria andOperateMatchStatusNotIn(List<Integer> values) {
            addCriterion("operate_match_status not in", values, "operateMatchStatus");
            return (Criteria) this;
        }

        public Criteria andOperateMatchStatusBetween(Integer value1, Integer value2) {
            addCriterion("operate_match_status between", value1, value2, "operateMatchStatus");
            return (Criteria) this;
        }

        public Criteria andOperateMatchStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("operate_match_status not between", value1, value2, "operateMatchStatus");
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

        public Criteria andBeginTimeStatusIsNull() {
            addCriterion("begin_time_status is null");
            return (Criteria) this;
        }

        public Criteria andBeginTimeStatusIsNotNull() {
            addCriterion("begin_time_status is not null");
            return (Criteria) this;
        }

        public Criteria andBeginTimeStatusEqualTo(Integer value) {
            addCriterion("begin_time_status =", value, "beginTimeStatus");
            return (Criteria) this;
        }

        public Criteria andBeginTimeStatusNotEqualTo(Integer value) {
            addCriterion("begin_time_status <>", value, "beginTimeStatus");
            return (Criteria) this;
        }

        public Criteria andBeginTimeStatusGreaterThan(Integer value) {
            addCriterion("begin_time_status >", value, "beginTimeStatus");
            return (Criteria) this;
        }

        public Criteria andBeginTimeStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("begin_time_status >=", value, "beginTimeStatus");
            return (Criteria) this;
        }

        public Criteria andBeginTimeStatusLessThan(Integer value) {
            addCriterion("begin_time_status <", value, "beginTimeStatus");
            return (Criteria) this;
        }

        public Criteria andBeginTimeStatusLessThanOrEqualTo(Integer value) {
            addCriterion("begin_time_status <=", value, "beginTimeStatus");
            return (Criteria) this;
        }

        public Criteria andBeginTimeStatusIn(List<Integer> values) {
            addCriterion("begin_time_status in", values, "beginTimeStatus");
            return (Criteria) this;
        }

        public Criteria andBeginTimeStatusNotIn(List<Integer> values) {
            addCriterion("begin_time_status not in", values, "beginTimeStatus");
            return (Criteria) this;
        }

        public Criteria andBeginTimeStatusBetween(Integer value1, Integer value2) {
            addCriterion("begin_time_status between", value1, value2, "beginTimeStatus");
            return (Criteria) this;
        }

        public Criteria andBeginTimeStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("begin_time_status not between", value1, value2, "beginTimeStatus");
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

        public Criteria andNeutralGroundStatusIsNull() {
            addCriterion("neutral_ground_status is null");
            return (Criteria) this;
        }

        public Criteria andNeutralGroundStatusIsNotNull() {
            addCriterion("neutral_ground_status is not null");
            return (Criteria) this;
        }

        public Criteria andNeutralGroundStatusEqualTo(Integer value) {
            addCriterion("neutral_ground_status =", value, "neutralGroundStatus");
            return (Criteria) this;
        }

        public Criteria andNeutralGroundStatusNotEqualTo(Integer value) {
            addCriterion("neutral_ground_status <>", value, "neutralGroundStatus");
            return (Criteria) this;
        }

        public Criteria andNeutralGroundStatusGreaterThan(Integer value) {
            addCriterion("neutral_ground_status >", value, "neutralGroundStatus");
            return (Criteria) this;
        }

        public Criteria andNeutralGroundStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("neutral_ground_status >=", value, "neutralGroundStatus");
            return (Criteria) this;
        }

        public Criteria andNeutralGroundStatusLessThan(Integer value) {
            addCriterion("neutral_ground_status <", value, "neutralGroundStatus");
            return (Criteria) this;
        }

        public Criteria andNeutralGroundStatusLessThanOrEqualTo(Integer value) {
            addCriterion("neutral_ground_status <=", value, "neutralGroundStatus");
            return (Criteria) this;
        }

        public Criteria andNeutralGroundStatusIn(List<Integer> values) {
            addCriterion("neutral_ground_status in", values, "neutralGroundStatus");
            return (Criteria) this;
        }

        public Criteria andNeutralGroundStatusNotIn(List<Integer> values) {
            addCriterion("neutral_ground_status not in", values, "neutralGroundStatus");
            return (Criteria) this;
        }

        public Criteria andNeutralGroundStatusBetween(Integer value1, Integer value2) {
            addCriterion("neutral_ground_status between", value1, value2, "neutralGroundStatus");
            return (Criteria) this;
        }

        public Criteria andNeutralGroundStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("neutral_ground_status not between", value1, value2, "neutralGroundStatus");
            return (Criteria) this;
        }

        public Criteria andMatchManageIdIsNull() {
            addCriterion("match_manage_id is null");
            return (Criteria) this;
        }

        public Criteria andMatchManageIdIsNotNull() {
            addCriterion("match_manage_id is not null");
            return (Criteria) this;
        }

        public Criteria andMatchManageIdEqualTo(String value) {
            addCriterion("match_manage_id =", value, "matchManageId");
            return (Criteria) this;
        }

        public Criteria andMatchManageIdNotEqualTo(String value) {
            addCriterion("match_manage_id <>", value, "matchManageId");
            return (Criteria) this;
        }

        public Criteria andMatchManageIdGreaterThan(String value) {
            addCriterion("match_manage_id >", value, "matchManageId");
            return (Criteria) this;
        }

        public Criteria andMatchManageIdGreaterThanOrEqualTo(String value) {
            addCriterion("match_manage_id >=", value, "matchManageId");
            return (Criteria) this;
        }

        public Criteria andMatchManageIdLessThan(String value) {
            addCriterion("match_manage_id <", value, "matchManageId");
            return (Criteria) this;
        }

        public Criteria andMatchManageIdLessThanOrEqualTo(String value) {
            addCriterion("match_manage_id <=", value, "matchManageId");
            return (Criteria) this;
        }

        public Criteria andMatchManageIdLike(String value) {
            addCriterion("match_manage_id like", value, "matchManageId");
            return (Criteria) this;
        }

        public Criteria andMatchManageIdNotLike(String value) {
            addCriterion("match_manage_id not like", value, "matchManageId");
            return (Criteria) this;
        }

        public Criteria andMatchManageIdIn(List<String> values) {
            addCriterion("match_manage_id in", values, "matchManageId");
            return (Criteria) this;
        }

        public Criteria andMatchManageIdNotIn(List<String> values) {
            addCriterion("match_manage_id not in", values, "matchManageId");
            return (Criteria) this;
        }

        public Criteria andMatchManageIdBetween(String value1, String value2) {
            addCriterion("match_manage_id between", value1, value2, "matchManageId");
            return (Criteria) this;
        }

        public Criteria andMatchManageIdNotBetween(String value1, String value2) {
            addCriterion("match_manage_id not between", value1, value2, "matchManageId");
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

        public Criteria andRiskManagerCodeIsNull() {
            addCriterion("risk_manager_code is null");
            return (Criteria) this;
        }

        public Criteria andRiskManagerCodeIsNotNull() {
            addCriterion("risk_manager_code is not null");
            return (Criteria) this;
        }

        public Criteria andRiskManagerCodeEqualTo(String value) {
            addCriterion("risk_manager_code =", value, "riskManagerCode");
            return (Criteria) this;
        }

        public Criteria andRiskManagerCodeNotEqualTo(String value) {
            addCriterion("risk_manager_code <>", value, "riskManagerCode");
            return (Criteria) this;
        }

        public Criteria andRiskManagerCodeGreaterThan(String value) {
            addCriterion("risk_manager_code >", value, "riskManagerCode");
            return (Criteria) this;
        }

        public Criteria andRiskManagerCodeGreaterThanOrEqualTo(String value) {
            addCriterion("risk_manager_code >=", value, "riskManagerCode");
            return (Criteria) this;
        }

        public Criteria andRiskManagerCodeLessThan(String value) {
            addCriterion("risk_manager_code <", value, "riskManagerCode");
            return (Criteria) this;
        }

        public Criteria andRiskManagerCodeLessThanOrEqualTo(String value) {
            addCriterion("risk_manager_code <=", value, "riskManagerCode");
            return (Criteria) this;
        }

        public Criteria andRiskManagerCodeLike(String value) {
            addCriterion("risk_manager_code like", value, "riskManagerCode");
            return (Criteria) this;
        }

        public Criteria andRiskManagerCodeNotLike(String value) {
            addCriterion("risk_manager_code not like", value, "riskManagerCode");
            return (Criteria) this;
        }

        public Criteria andRiskManagerCodeIn(List<String> values) {
            addCriterion("risk_manager_code in", values, "riskManagerCode");
            return (Criteria) this;
        }

        public Criteria andRiskManagerCodeNotIn(List<String> values) {
            addCriterion("risk_manager_code not in", values, "riskManagerCode");
            return (Criteria) this;
        }

        public Criteria andRiskManagerCodeBetween(String value1, String value2) {
            addCriterion("risk_manager_code between", value1, value2, "riskManagerCode");
            return (Criteria) this;
        }

        public Criteria andRiskManagerCodeNotBetween(String value1, String value2) {
            addCriterion("risk_manager_code not between", value1, value2, "riskManagerCode");
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

        public Criteria andMatchDataProviderCodeIsNull() {
            addCriterion("match_data_provider_code is null");
            return (Criteria) this;
        }

        public Criteria andMatchDataProviderCodeIsNotNull() {
            addCriterion("match_data_provider_code is not null");
            return (Criteria) this;
        }

        public Criteria andMatchDataProviderCodeEqualTo(String value) {
            addCriterion("match_data_provider_code =", value, "matchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andMatchDataProviderCodeNotEqualTo(String value) {
            addCriterion("match_data_provider_code <>", value, "matchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andMatchDataProviderCodeGreaterThan(String value) {
            addCriterion("match_data_provider_code >", value, "matchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andMatchDataProviderCodeGreaterThanOrEqualTo(String value) {
            addCriterion("match_data_provider_code >=", value, "matchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andMatchDataProviderCodeLessThan(String value) {
            addCriterion("match_data_provider_code <", value, "matchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andMatchDataProviderCodeLessThanOrEqualTo(String value) {
            addCriterion("match_data_provider_code <=", value, "matchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andMatchDataProviderCodeLike(String value) {
            addCriterion("match_data_provider_code like", value, "matchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andMatchDataProviderCodeNotLike(String value) {
            addCriterion("match_data_provider_code not like", value, "matchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andMatchDataProviderCodeIn(List<String> values) {
            addCriterion("match_data_provider_code in", values, "matchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andMatchDataProviderCodeNotIn(List<String> values) {
            addCriterion("match_data_provider_code not in", values, "matchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andMatchDataProviderCodeBetween(String value1, String value2) {
            addCriterion("match_data_provider_code between", value1, value2, "matchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andMatchDataProviderCodeNotBetween(String value1, String value2) {
            addCriterion("match_data_provider_code not between", value1, value2, "matchDataProviderCode");
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

        public Criteria andReverseStatusIsNull() {
            addCriterion("reverse_status is null");
            return (Criteria) this;
        }

        public Criteria andReverseStatusIsNotNull() {
            addCriterion("reverse_status is not null");
            return (Criteria) this;
        }

        public Criteria andReverseStatusEqualTo(String value) {
            addCriterion("reverse_status =", value, "reverseStatus");
            return (Criteria) this;
        }

        public Criteria andReverseStatusNotEqualTo(String value) {
            addCriterion("reverse_status <>", value, "reverseStatus");
            return (Criteria) this;
        }

        public Criteria andReverseStatusGreaterThan(String value) {
            addCriterion("reverse_status >", value, "reverseStatus");
            return (Criteria) this;
        }

        public Criteria andReverseStatusGreaterThanOrEqualTo(String value) {
            addCriterion("reverse_status >=", value, "reverseStatus");
            return (Criteria) this;
        }

        public Criteria andReverseStatusLessThan(String value) {
            addCriterion("reverse_status <", value, "reverseStatus");
            return (Criteria) this;
        }

        public Criteria andReverseStatusLessThanOrEqualTo(String value) {
            addCriterion("reverse_status <=", value, "reverseStatus");
            return (Criteria) this;
        }

        public Criteria andReverseStatusLike(String value) {
            addCriterion("reverse_status like", value, "reverseStatus");
            return (Criteria) this;
        }

        public Criteria andReverseStatusNotLike(String value) {
            addCriterion("reverse_status not like", value, "reverseStatus");
            return (Criteria) this;
        }

        public Criteria andReverseStatusIn(List<String> values) {
            addCriterion("reverse_status in", values, "reverseStatus");
            return (Criteria) this;
        }

        public Criteria andReverseStatusNotIn(List<String> values) {
            addCriterion("reverse_status not in", values, "reverseStatus");
            return (Criteria) this;
        }

        public Criteria andReverseStatusBetween(String value1, String value2) {
            addCriterion("reverse_status between", value1, value2, "reverseStatus");
            return (Criteria) this;
        }

        public Criteria andReverseStatusNotBetween(String value1, String value2) {
            addCriterion("reverse_status not between", value1, value2, "reverseStatus");
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

        public Criteria andMatchPeriodIdIsNull() {
            addCriterion("match_period_id is null");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdIsNotNull() {
            addCriterion("match_period_id is not null");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdEqualTo(Long value) {
            addCriterion("match_period_id =", value, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdNotEqualTo(Long value) {
            addCriterion("match_period_id <>", value, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdGreaterThan(Long value) {
            addCriterion("match_period_id >", value, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdGreaterThanOrEqualTo(Long value) {
            addCriterion("match_period_id >=", value, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdLessThan(Long value) {
            addCriterion("match_period_id <", value, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdLessThanOrEqualTo(Long value) {
            addCriterion("match_period_id <=", value, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdIn(List<Long> values) {
            addCriterion("match_period_id in", values, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdNotIn(List<Long> values) {
            addCriterion("match_period_id not in", values, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdBetween(Long value1, Long value2) {
            addCriterion("match_period_id between", value1, value2, "matchPeriodId");
            return (Criteria) this;
        }

        public Criteria andMatchPeriodIdNotBetween(Long value1, Long value2) {
            addCriterion("match_period_id not between", value1, value2, "matchPeriodId");
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

        public Criteria andSoldFlagIsNull() {
            addCriterion("sold_flag is null");
            return (Criteria) this;
        }

        public Criteria andSoldFlagIsNotNull() {
            addCriterion("sold_flag is not null");
            return (Criteria) this;
        }

        public Criteria andSoldFlagEqualTo(String value) {
            addCriterion("sold_flag =", value, "soldFlag");
            return (Criteria) this;
        }

        public Criteria andSoldFlagNotEqualTo(String value) {
            addCriterion("sold_flag <>", value, "soldFlag");
            return (Criteria) this;
        }

        public Criteria andSoldFlagGreaterThan(String value) {
            addCriterion("sold_flag >", value, "soldFlag");
            return (Criteria) this;
        }

        public Criteria andSoldFlagGreaterThanOrEqualTo(String value) {
            addCriterion("sold_flag >=", value, "soldFlag");
            return (Criteria) this;
        }

        public Criteria andSoldFlagLessThan(String value) {
            addCriterion("sold_flag <", value, "soldFlag");
            return (Criteria) this;
        }

        public Criteria andSoldFlagLessThanOrEqualTo(String value) {
            addCriterion("sold_flag <=", value, "soldFlag");
            return (Criteria) this;
        }

        public Criteria andSoldFlagLike(String value) {
            addCriterion("sold_flag like", value, "soldFlag");
            return (Criteria) this;
        }

        public Criteria andSoldFlagNotLike(String value) {
            addCriterion("sold_flag not like", value, "soldFlag");
            return (Criteria) this;
        }

        public Criteria andSoldFlagIn(List<String> values) {
            addCriterion("sold_flag in", values, "soldFlag");
            return (Criteria) this;
        }

        public Criteria andSoldFlagNotIn(List<String> values) {
            addCriterion("sold_flag not in", values, "soldFlag");
            return (Criteria) this;
        }

        public Criteria andSoldFlagBetween(String value1, String value2) {
            addCriterion("sold_flag between", value1, value2, "soldFlag");
            return (Criteria) this;
        }

        public Criteria andSoldFlagNotBetween(String value1, String value2) {
            addCriterion("sold_flag not between", value1, value2, "soldFlag");
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

        public Criteria andHotMatchStatusIsNull() {
            addCriterion("hot_match_status is null");
            return (Criteria) this;
        }

        public Criteria andHotMatchStatusIsNotNull() {
            addCriterion("hot_match_status is not null");
            return (Criteria) this;
        }

        public Criteria andHotMatchStatusEqualTo(Integer value) {
            addCriterion("hot_match_status =", value, "hotMatchStatus");
            return (Criteria) this;
        }

        public Criteria andHotMatchStatusNotEqualTo(Integer value) {
            addCriterion("hot_match_status <>", value, "hotMatchStatus");
            return (Criteria) this;
        }

        public Criteria andHotMatchStatusGreaterThan(Integer value) {
            addCriterion("hot_match_status >", value, "hotMatchStatus");
            return (Criteria) this;
        }

        public Criteria andHotMatchStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("hot_match_status >=", value, "hotMatchStatus");
            return (Criteria) this;
        }

        public Criteria andHotMatchStatusLessThan(Integer value) {
            addCriterion("hot_match_status <", value, "hotMatchStatus");
            return (Criteria) this;
        }

        public Criteria andHotMatchStatusLessThanOrEqualTo(Integer value) {
            addCriterion("hot_match_status <=", value, "hotMatchStatus");
            return (Criteria) this;
        }

        public Criteria andHotMatchStatusIn(List<Integer> values) {
            addCriterion("hot_match_status in", values, "hotMatchStatus");
            return (Criteria) this;
        }

        public Criteria andHotMatchStatusNotIn(List<Integer> values) {
            addCriterion("hot_match_status not in", values, "hotMatchStatus");
            return (Criteria) this;
        }

        public Criteria andHotMatchStatusBetween(Integer value1, Integer value2) {
            addCriterion("hot_match_status between", value1, value2, "hotMatchStatus");
            return (Criteria) this;
        }

        public Criteria andHotMatchStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("hot_match_status not between", value1, value2, "hotMatchStatus");
            return (Criteria) this;
        }

        public Criteria andFinancialTimeIsNull() {
            addCriterion("financial_time is null");
            return (Criteria) this;
        }

        public Criteria andFinancialTimeIsNotNull() {
            addCriterion("financial_time is not null");
            return (Criteria) this;
        }

        public Criteria andFinancialTimeEqualTo(Long value) {
            addCriterion("financial_time =", value, "financialTime");
            return (Criteria) this;
        }

        public Criteria andFinancialTimeNotEqualTo(Long value) {
            addCriterion("financial_time <>", value, "financialTime");
            return (Criteria) this;
        }

        public Criteria andFinancialTimeGreaterThan(Long value) {
            addCriterion("financial_time >", value, "financialTime");
            return (Criteria) this;
        }

        public Criteria andFinancialTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("financial_time >=", value, "financialTime");
            return (Criteria) this;
        }

        public Criteria andFinancialTimeLessThan(Long value) {
            addCriterion("financial_time <", value, "financialTime");
            return (Criteria) this;
        }

        public Criteria andFinancialTimeLessThanOrEqualTo(Long value) {
            addCriterion("financial_time <=", value, "financialTime");
            return (Criteria) this;
        }

        public Criteria andFinancialTimeIn(List<Long> values) {
            addCriterion("financial_time in", values, "financialTime");
            return (Criteria) this;
        }

        public Criteria andFinancialTimeNotIn(List<Long> values) {
            addCriterion("financial_time not in", values, "financialTime");
            return (Criteria) this;
        }

        public Criteria andFinancialTimeBetween(Long value1, Long value2) {
            addCriterion("financial_time between", value1, value2, "financialTime");
            return (Criteria) this;
        }

        public Criteria andFinancialTimeNotBetween(Long value1, Long value2) {
            addCriterion("financial_time not between", value1, value2, "financialTime");
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

        public Criteria andOddsLiveIsNull() {
            addCriterion("odds_live is null");
            return (Criteria) this;
        }

        public Criteria andOddsLiveIsNotNull() {
            addCriterion("odds_live is not null");
            return (Criteria) this;
        }

        public Criteria andOddsLiveEqualTo(Integer value) {
            addCriterion("odds_live =", value, "oddsLive");
            return (Criteria) this;
        }

        public Criteria andOddsLiveNotEqualTo(Integer value) {
            addCriterion("odds_live <>", value, "oddsLive");
            return (Criteria) this;
        }

        public Criteria andOddsLiveGreaterThan(Integer value) {
            addCriterion("odds_live >", value, "oddsLive");
            return (Criteria) this;
        }

        public Criteria andOddsLiveGreaterThanOrEqualTo(Integer value) {
            addCriterion("odds_live >=", value, "oddsLive");
            return (Criteria) this;
        }

        public Criteria andOddsLiveLessThan(Integer value) {
            addCriterion("odds_live <", value, "oddsLive");
            return (Criteria) this;
        }

        public Criteria andOddsLiveLessThanOrEqualTo(Integer value) {
            addCriterion("odds_live <=", value, "oddsLive");
            return (Criteria) this;
        }

        public Criteria andOddsLiveIn(List<Integer> values) {
            addCriterion("odds_live in", values, "oddsLive");
            return (Criteria) this;
        }

        public Criteria andOddsLiveNotIn(List<Integer> values) {
            addCriterion("odds_live not in", values, "oddsLive");
            return (Criteria) this;
        }

        public Criteria andOddsLiveBetween(Integer value1, Integer value2) {
            addCriterion("odds_live between", value1, value2, "oddsLive");
            return (Criteria) this;
        }

        public Criteria andOddsLiveNotBetween(Integer value1, Integer value2) {
            addCriterion("odds_live not between", value1, value2, "oddsLive");
            return (Criteria) this;
        }

        public Criteria andTournamentNameCodeIsNull() {
            addCriterion("tournament_name_code is null");
            return (Criteria) this;
        }

        public Criteria andTournamentNameCodeIsNotNull() {
            addCriterion("tournament_name_code is not null");
            return (Criteria) this;
        }

        public Criteria andTournamentNameCodeEqualTo(Long value) {
            addCriterion("tournament_name_code =", value, "tournamentNameCode");
            return (Criteria) this;
        }

        public Criteria andTournamentNameCodeNotEqualTo(Long value) {
            addCriterion("tournament_name_code <>", value, "tournamentNameCode");
            return (Criteria) this;
        }

        public Criteria andTournamentNameCodeGreaterThan(Long value) {
            addCriterion("tournament_name_code >", value, "tournamentNameCode");
            return (Criteria) this;
        }

        public Criteria andTournamentNameCodeGreaterThanOrEqualTo(Long value) {
            addCriterion("tournament_name_code >=", value, "tournamentNameCode");
            return (Criteria) this;
        }

        public Criteria andTournamentNameCodeLessThan(Long value) {
            addCriterion("tournament_name_code <", value, "tournamentNameCode");
            return (Criteria) this;
        }

        public Criteria andTournamentNameCodeLessThanOrEqualTo(Long value) {
            addCriterion("tournament_name_code <=", value, "tournamentNameCode");
            return (Criteria) this;
        }

        public Criteria andTournamentNameCodeIn(List<Long> values) {
            addCriterion("tournament_name_code in", values, "tournamentNameCode");
            return (Criteria) this;
        }

        public Criteria andTournamentNameCodeNotIn(List<Long> values) {
            addCriterion("tournament_name_code not in", values, "tournamentNameCode");
            return (Criteria) this;
        }

        public Criteria andTournamentNameCodeBetween(Long value1, Long value2) {
            addCriterion("tournament_name_code between", value1, value2, "tournamentNameCode");
            return (Criteria) this;
        }

        public Criteria andTournamentNameCodeNotBetween(Long value1, Long value2) {
            addCriterion("tournament_name_code not between", value1, value2, "tournamentNameCode");
            return (Criteria) this;
        }

        public Criteria andAutoAuditFlagIsNull() {
            addCriterion("auto_audit_flag is null");
            return (Criteria) this;
        }

        public Criteria andAutoAuditFlagIsNotNull() {
            addCriterion("auto_audit_flag is not null");
            return (Criteria) this;
        }

        public Criteria andAutoAuditFlagEqualTo(Integer value) {
            addCriterion("auto_audit_flag =", value, "autoAuditFlag");
            return (Criteria) this;
        }

        public Criteria andAutoAuditFlagNotEqualTo(Integer value) {
            addCriterion("auto_audit_flag <>", value, "autoAuditFlag");
            return (Criteria) this;
        }

        public Criteria andAutoAuditFlagGreaterThan(Integer value) {
            addCriterion("auto_audit_flag >", value, "autoAuditFlag");
            return (Criteria) this;
        }

        public Criteria andAutoAuditFlagGreaterThanOrEqualTo(Integer value) {
            addCriterion("auto_audit_flag >=", value, "autoAuditFlag");
            return (Criteria) this;
        }

        public Criteria andAutoAuditFlagLessThan(Integer value) {
            addCriterion("auto_audit_flag <", value, "autoAuditFlag");
            return (Criteria) this;
        }

        public Criteria andAutoAuditFlagLessThanOrEqualTo(Integer value) {
            addCriterion("auto_audit_flag <=", value, "autoAuditFlag");
            return (Criteria) this;
        }

        public Criteria andAutoAuditFlagIn(List<Integer> values) {
            addCriterion("auto_audit_flag in", values, "autoAuditFlag");
            return (Criteria) this;
        }

        public Criteria andAutoAuditFlagNotIn(List<Integer> values) {
            addCriterion("auto_audit_flag not in", values, "autoAuditFlag");
            return (Criteria) this;
        }

        public Criteria andAutoAuditFlagBetween(Integer value1, Integer value2) {
            addCriterion("auto_audit_flag between", value1, value2, "autoAuditFlag");
            return (Criteria) this;
        }

        public Criteria andAutoAuditFlagNotBetween(Integer value1, Integer value2) {
            addCriterion("auto_audit_flag not between", value1, value2, "autoAuditFlag");
            return (Criteria) this;
        }

        public Criteria andScoreOpflagIsNull() {
            addCriterion("score_opflag is null");
            return (Criteria) this;
        }

        public Criteria andScoreOpflagIsNotNull() {
            addCriterion("score_opflag is not null");
            return (Criteria) this;
        }

        public Criteria andScoreOpflagEqualTo(Integer value) {
            addCriterion("score_opflag =", value, "scoreOpflag");
            return (Criteria) this;
        }

        public Criteria andScoreOpflagNotEqualTo(Integer value) {
            addCriterion("score_opflag <>", value, "scoreOpflag");
            return (Criteria) this;
        }

        public Criteria andScoreOpflagGreaterThan(Integer value) {
            addCriterion("score_opflag >", value, "scoreOpflag");
            return (Criteria) this;
        }

        public Criteria andScoreOpflagGreaterThanOrEqualTo(Integer value) {
            addCriterion("score_opflag >=", value, "scoreOpflag");
            return (Criteria) this;
        }

        public Criteria andScoreOpflagLessThan(Integer value) {
            addCriterion("score_opflag <", value, "scoreOpflag");
            return (Criteria) this;
        }

        public Criteria andScoreOpflagLessThanOrEqualTo(Integer value) {
            addCriterion("score_opflag <=", value, "scoreOpflag");
            return (Criteria) this;
        }

        public Criteria andScoreOpflagIn(List<Integer> values) {
            addCriterion("score_opflag in", values, "scoreOpflag");
            return (Criteria) this;
        }

        public Criteria andScoreOpflagNotIn(List<Integer> values) {
            addCriterion("score_opflag not in", values, "scoreOpflag");
            return (Criteria) this;
        }

        public Criteria andScoreOpflagBetween(Integer value1, Integer value2) {
            addCriterion("score_opflag between", value1, value2, "scoreOpflag");
            return (Criteria) this;
        }

        public Criteria andScoreOpflagNotBetween(Integer value1, Integer value2) {
            addCriterion("score_opflag not between", value1, value2, "scoreOpflag");
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