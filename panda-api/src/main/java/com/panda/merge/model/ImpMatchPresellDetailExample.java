package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class ImpMatchPresellDetailExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ImpMatchPresellDetailExample() {
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

        public Criteria andMainIdIsNull() {
            addCriterion("main_id is null");
            return (Criteria) this;
        }

        public Criteria andMainIdIsNotNull() {
            addCriterion("main_id is not null");
            return (Criteria) this;
        }

        public Criteria andMainIdEqualTo(Long value) {
            addCriterion("main_id =", value, "mainId");
            return (Criteria) this;
        }

        public Criteria andMainIdNotEqualTo(Long value) {
            addCriterion("main_id <>", value, "mainId");
            return (Criteria) this;
        }

        public Criteria andMainIdGreaterThan(Long value) {
            addCriterion("main_id >", value, "mainId");
            return (Criteria) this;
        }

        public Criteria andMainIdGreaterThanOrEqualTo(Long value) {
            addCriterion("main_id >=", value, "mainId");
            return (Criteria) this;
        }

        public Criteria andMainIdLessThan(Long value) {
            addCriterion("main_id <", value, "mainId");
            return (Criteria) this;
        }

        public Criteria andMainIdLessThanOrEqualTo(Long value) {
            addCriterion("main_id <=", value, "mainId");
            return (Criteria) this;
        }

        public Criteria andMainIdIn(List<Long> values) {
            addCriterion("main_id in", values, "mainId");
            return (Criteria) this;
        }

        public Criteria andMainIdNotIn(List<Long> values) {
            addCriterion("main_id not in", values, "mainId");
            return (Criteria) this;
        }

        public Criteria andMainIdBetween(Long value1, Long value2) {
            addCriterion("main_id between", value1, value2, "mainId");
            return (Criteria) this;
        }

        public Criteria andMainIdNotBetween(Long value1, Long value2) {
            addCriterion("main_id not between", value1, value2, "mainId");
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

        public Criteria andTournamentNameIsNull() {
            addCriterion("tournament_name is null");
            return (Criteria) this;
        }

        public Criteria andTournamentNameIsNotNull() {
            addCriterion("tournament_name is not null");
            return (Criteria) this;
        }

        public Criteria andTournamentNameEqualTo(String value) {
            addCriterion("tournament_name =", value, "tournamentName");
            return (Criteria) this;
        }

        public Criteria andTournamentNameNotEqualTo(String value) {
            addCriterion("tournament_name <>", value, "tournamentName");
            return (Criteria) this;
        }

        public Criteria andTournamentNameGreaterThan(String value) {
            addCriterion("tournament_name >", value, "tournamentName");
            return (Criteria) this;
        }

        public Criteria andTournamentNameGreaterThanOrEqualTo(String value) {
            addCriterion("tournament_name >=", value, "tournamentName");
            return (Criteria) this;
        }

        public Criteria andTournamentNameLessThan(String value) {
            addCriterion("tournament_name <", value, "tournamentName");
            return (Criteria) this;
        }

        public Criteria andTournamentNameLessThanOrEqualTo(String value) {
            addCriterion("tournament_name <=", value, "tournamentName");
            return (Criteria) this;
        }

        public Criteria andTournamentNameLike(String value) {
            addCriterion("tournament_name like", value, "tournamentName");
            return (Criteria) this;
        }

        public Criteria andTournamentNameNotLike(String value) {
            addCriterion("tournament_name not like", value, "tournamentName");
            return (Criteria) this;
        }

        public Criteria andTournamentNameIn(List<String> values) {
            addCriterion("tournament_name in", values, "tournamentName");
            return (Criteria) this;
        }

        public Criteria andTournamentNameNotIn(List<String> values) {
            addCriterion("tournament_name not in", values, "tournamentName");
            return (Criteria) this;
        }

        public Criteria andTournamentNameBetween(String value1, String value2) {
            addCriterion("tournament_name between", value1, value2, "tournamentName");
            return (Criteria) this;
        }

        public Criteria andTournamentNameNotBetween(String value1, String value2) {
            addCriterion("tournament_name not between", value1, value2, "tournamentName");
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

        public Criteria andTeamHomeNameIsNull() {
            addCriterion("team_home_name is null");
            return (Criteria) this;
        }

        public Criteria andTeamHomeNameIsNotNull() {
            addCriterion("team_home_name is not null");
            return (Criteria) this;
        }

        public Criteria andTeamHomeNameEqualTo(String value) {
            addCriterion("team_home_name =", value, "teamHomeName");
            return (Criteria) this;
        }

        public Criteria andTeamHomeNameNotEqualTo(String value) {
            addCriterion("team_home_name <>", value, "teamHomeName");
            return (Criteria) this;
        }

        public Criteria andTeamHomeNameGreaterThan(String value) {
            addCriterion("team_home_name >", value, "teamHomeName");
            return (Criteria) this;
        }

        public Criteria andTeamHomeNameGreaterThanOrEqualTo(String value) {
            addCriterion("team_home_name >=", value, "teamHomeName");
            return (Criteria) this;
        }

        public Criteria andTeamHomeNameLessThan(String value) {
            addCriterion("team_home_name <", value, "teamHomeName");
            return (Criteria) this;
        }

        public Criteria andTeamHomeNameLessThanOrEqualTo(String value) {
            addCriterion("team_home_name <=", value, "teamHomeName");
            return (Criteria) this;
        }

        public Criteria andTeamHomeNameLike(String value) {
            addCriterion("team_home_name like", value, "teamHomeName");
            return (Criteria) this;
        }

        public Criteria andTeamHomeNameNotLike(String value) {
            addCriterion("team_home_name not like", value, "teamHomeName");
            return (Criteria) this;
        }

        public Criteria andTeamHomeNameIn(List<String> values) {
            addCriterion("team_home_name in", values, "teamHomeName");
            return (Criteria) this;
        }

        public Criteria andTeamHomeNameNotIn(List<String> values) {
            addCriterion("team_home_name not in", values, "teamHomeName");
            return (Criteria) this;
        }

        public Criteria andTeamHomeNameBetween(String value1, String value2) {
            addCriterion("team_home_name between", value1, value2, "teamHomeName");
            return (Criteria) this;
        }

        public Criteria andTeamHomeNameNotBetween(String value1, String value2) {
            addCriterion("team_home_name not between", value1, value2, "teamHomeName");
            return (Criteria) this;
        }

        public Criteria andTeamAwayNameIsNull() {
            addCriterion("team_away_name is null");
            return (Criteria) this;
        }

        public Criteria andTeamAwayNameIsNotNull() {
            addCriterion("team_away_name is not null");
            return (Criteria) this;
        }

        public Criteria andTeamAwayNameEqualTo(String value) {
            addCriterion("team_away_name =", value, "teamAwayName");
            return (Criteria) this;
        }

        public Criteria andTeamAwayNameNotEqualTo(String value) {
            addCriterion("team_away_name <>", value, "teamAwayName");
            return (Criteria) this;
        }

        public Criteria andTeamAwayNameGreaterThan(String value) {
            addCriterion("team_away_name >", value, "teamAwayName");
            return (Criteria) this;
        }

        public Criteria andTeamAwayNameGreaterThanOrEqualTo(String value) {
            addCriterion("team_away_name >=", value, "teamAwayName");
            return (Criteria) this;
        }

        public Criteria andTeamAwayNameLessThan(String value) {
            addCriterion("team_away_name <", value, "teamAwayName");
            return (Criteria) this;
        }

        public Criteria andTeamAwayNameLessThanOrEqualTo(String value) {
            addCriterion("team_away_name <=", value, "teamAwayName");
            return (Criteria) this;
        }

        public Criteria andTeamAwayNameLike(String value) {
            addCriterion("team_away_name like", value, "teamAwayName");
            return (Criteria) this;
        }

        public Criteria andTeamAwayNameNotLike(String value) {
            addCriterion("team_away_name not like", value, "teamAwayName");
            return (Criteria) this;
        }

        public Criteria andTeamAwayNameIn(List<String> values) {
            addCriterion("team_away_name in", values, "teamAwayName");
            return (Criteria) this;
        }

        public Criteria andTeamAwayNameNotIn(List<String> values) {
            addCriterion("team_away_name not in", values, "teamAwayName");
            return (Criteria) this;
        }

        public Criteria andTeamAwayNameBetween(String value1, String value2) {
            addCriterion("team_away_name between", value1, value2, "teamAwayName");
            return (Criteria) this;
        }

        public Criteria andTeamAwayNameNotBetween(String value1, String value2) {
            addCriterion("team_away_name not between", value1, value2, "teamAwayName");
            return (Criteria) this;
        }

        public Criteria andPreTraderIsNull() {
            addCriterion("pre_trader is null");
            return (Criteria) this;
        }

        public Criteria andPreTraderIsNotNull() {
            addCriterion("pre_trader is not null");
            return (Criteria) this;
        }

        public Criteria andPreTraderEqualTo(String value) {
            addCriterion("pre_trader =", value, "preTrader");
            return (Criteria) this;
        }

        public Criteria andPreTraderNotEqualTo(String value) {
            addCriterion("pre_trader <>", value, "preTrader");
            return (Criteria) this;
        }

        public Criteria andPreTraderGreaterThan(String value) {
            addCriterion("pre_trader >", value, "preTrader");
            return (Criteria) this;
        }

        public Criteria andPreTraderGreaterThanOrEqualTo(String value) {
            addCriterion("pre_trader >=", value, "preTrader");
            return (Criteria) this;
        }

        public Criteria andPreTraderLessThan(String value) {
            addCriterion("pre_trader <", value, "preTrader");
            return (Criteria) this;
        }

        public Criteria andPreTraderLessThanOrEqualTo(String value) {
            addCriterion("pre_trader <=", value, "preTrader");
            return (Criteria) this;
        }

        public Criteria andPreTraderLike(String value) {
            addCriterion("pre_trader like", value, "preTrader");
            return (Criteria) this;
        }

        public Criteria andPreTraderNotLike(String value) {
            addCriterion("pre_trader not like", value, "preTrader");
            return (Criteria) this;
        }

        public Criteria andPreTraderIn(List<String> values) {
            addCriterion("pre_trader in", values, "preTrader");
            return (Criteria) this;
        }

        public Criteria andPreTraderNotIn(List<String> values) {
            addCriterion("pre_trader not in", values, "preTrader");
            return (Criteria) this;
        }

        public Criteria andPreTraderBetween(String value1, String value2) {
            addCriterion("pre_trader between", value1, value2, "preTrader");
            return (Criteria) this;
        }

        public Criteria andPreTraderNotBetween(String value1, String value2) {
            addCriterion("pre_trader not between", value1, value2, "preTrader");
            return (Criteria) this;
        }

        public Criteria andPreRiskManagerCodeIsNull() {
            addCriterion("pre_risk_manager_code is null");
            return (Criteria) this;
        }

        public Criteria andPreRiskManagerCodeIsNotNull() {
            addCriterion("pre_risk_manager_code is not null");
            return (Criteria) this;
        }

        public Criteria andPreRiskManagerCodeEqualTo(String value) {
            addCriterion("pre_risk_manager_code =", value, "preRiskManagerCode");
            return (Criteria) this;
        }

        public Criteria andPreRiskManagerCodeNotEqualTo(String value) {
            addCriterion("pre_risk_manager_code <>", value, "preRiskManagerCode");
            return (Criteria) this;
        }

        public Criteria andPreRiskManagerCodeGreaterThan(String value) {
            addCriterion("pre_risk_manager_code >", value, "preRiskManagerCode");
            return (Criteria) this;
        }

        public Criteria andPreRiskManagerCodeGreaterThanOrEqualTo(String value) {
            addCriterion("pre_risk_manager_code >=", value, "preRiskManagerCode");
            return (Criteria) this;
        }

        public Criteria andPreRiskManagerCodeLessThan(String value) {
            addCriterion("pre_risk_manager_code <", value, "preRiskManagerCode");
            return (Criteria) this;
        }

        public Criteria andPreRiskManagerCodeLessThanOrEqualTo(String value) {
            addCriterion("pre_risk_manager_code <=", value, "preRiskManagerCode");
            return (Criteria) this;
        }

        public Criteria andPreRiskManagerCodeLike(String value) {
            addCriterion("pre_risk_manager_code like", value, "preRiskManagerCode");
            return (Criteria) this;
        }

        public Criteria andPreRiskManagerCodeNotLike(String value) {
            addCriterion("pre_risk_manager_code not like", value, "preRiskManagerCode");
            return (Criteria) this;
        }

        public Criteria andPreRiskManagerCodeIn(List<String> values) {
            addCriterion("pre_risk_manager_code in", values, "preRiskManagerCode");
            return (Criteria) this;
        }

        public Criteria andPreRiskManagerCodeNotIn(List<String> values) {
            addCriterion("pre_risk_manager_code not in", values, "preRiskManagerCode");
            return (Criteria) this;
        }

        public Criteria andPreRiskManagerCodeBetween(String value1, String value2) {
            addCriterion("pre_risk_manager_code between", value1, value2, "preRiskManagerCode");
            return (Criteria) this;
        }

        public Criteria andPreRiskManagerCodeNotBetween(String value1, String value2) {
            addCriterion("pre_risk_manager_code not between", value1, value2, "preRiskManagerCode");
            return (Criteria) this;
        }

        public Criteria andLiveTraderIsNull() {
            addCriterion("live_trader is null");
            return (Criteria) this;
        }

        public Criteria andLiveTraderIsNotNull() {
            addCriterion("live_trader is not null");
            return (Criteria) this;
        }

        public Criteria andLiveTraderEqualTo(String value) {
            addCriterion("live_trader =", value, "liveTrader");
            return (Criteria) this;
        }

        public Criteria andLiveTraderNotEqualTo(String value) {
            addCriterion("live_trader <>", value, "liveTrader");
            return (Criteria) this;
        }

        public Criteria andLiveTraderGreaterThan(String value) {
            addCriterion("live_trader >", value, "liveTrader");
            return (Criteria) this;
        }

        public Criteria andLiveTraderGreaterThanOrEqualTo(String value) {
            addCriterion("live_trader >=", value, "liveTrader");
            return (Criteria) this;
        }

        public Criteria andLiveTraderLessThan(String value) {
            addCriterion("live_trader <", value, "liveTrader");
            return (Criteria) this;
        }

        public Criteria andLiveTraderLessThanOrEqualTo(String value) {
            addCriterion("live_trader <=", value, "liveTrader");
            return (Criteria) this;
        }

        public Criteria andLiveTraderLike(String value) {
            addCriterion("live_trader like", value, "liveTrader");
            return (Criteria) this;
        }

        public Criteria andLiveTraderNotLike(String value) {
            addCriterion("live_trader not like", value, "liveTrader");
            return (Criteria) this;
        }

        public Criteria andLiveTraderIn(List<String> values) {
            addCriterion("live_trader in", values, "liveTrader");
            return (Criteria) this;
        }

        public Criteria andLiveTraderNotIn(List<String> values) {
            addCriterion("live_trader not in", values, "liveTrader");
            return (Criteria) this;
        }

        public Criteria andLiveTraderBetween(String value1, String value2) {
            addCriterion("live_trader between", value1, value2, "liveTrader");
            return (Criteria) this;
        }

        public Criteria andLiveTraderNotBetween(String value1, String value2) {
            addCriterion("live_trader not between", value1, value2, "liveTrader");
            return (Criteria) this;
        }

        public Criteria andLiveRiskManagerCodeIsNull() {
            addCriterion("live_risk_manager_code is null");
            return (Criteria) this;
        }

        public Criteria andLiveRiskManagerCodeIsNotNull() {
            addCriterion("live_risk_manager_code is not null");
            return (Criteria) this;
        }

        public Criteria andLiveRiskManagerCodeEqualTo(String value) {
            addCriterion("live_risk_manager_code =", value, "liveRiskManagerCode");
            return (Criteria) this;
        }

        public Criteria andLiveRiskManagerCodeNotEqualTo(String value) {
            addCriterion("live_risk_manager_code <>", value, "liveRiskManagerCode");
            return (Criteria) this;
        }

        public Criteria andLiveRiskManagerCodeGreaterThan(String value) {
            addCriterion("live_risk_manager_code >", value, "liveRiskManagerCode");
            return (Criteria) this;
        }

        public Criteria andLiveRiskManagerCodeGreaterThanOrEqualTo(String value) {
            addCriterion("live_risk_manager_code >=", value, "liveRiskManagerCode");
            return (Criteria) this;
        }

        public Criteria andLiveRiskManagerCodeLessThan(String value) {
            addCriterion("live_risk_manager_code <", value, "liveRiskManagerCode");
            return (Criteria) this;
        }

        public Criteria andLiveRiskManagerCodeLessThanOrEqualTo(String value) {
            addCriterion("live_risk_manager_code <=", value, "liveRiskManagerCode");
            return (Criteria) this;
        }

        public Criteria andLiveRiskManagerCodeLike(String value) {
            addCriterion("live_risk_manager_code like", value, "liveRiskManagerCode");
            return (Criteria) this;
        }

        public Criteria andLiveRiskManagerCodeNotLike(String value) {
            addCriterion("live_risk_manager_code not like", value, "liveRiskManagerCode");
            return (Criteria) this;
        }

        public Criteria andLiveRiskManagerCodeIn(List<String> values) {
            addCriterion("live_risk_manager_code in", values, "liveRiskManagerCode");
            return (Criteria) this;
        }

        public Criteria andLiveRiskManagerCodeNotIn(List<String> values) {
            addCriterion("live_risk_manager_code not in", values, "liveRiskManagerCode");
            return (Criteria) this;
        }

        public Criteria andLiveRiskManagerCodeBetween(String value1, String value2) {
            addCriterion("live_risk_manager_code between", value1, value2, "liveRiskManagerCode");
            return (Criteria) this;
        }

        public Criteria andLiveRiskManagerCodeNotBetween(String value1, String value2) {
            addCriterion("live_risk_manager_code not between", value1, value2, "liveRiskManagerCode");
            return (Criteria) this;
        }

        public Criteria andMatchStatusCodeIsNull() {
            addCriterion("match_status_code is null");
            return (Criteria) this;
        }

        public Criteria andMatchStatusCodeIsNotNull() {
            addCriterion("match_status_code is not null");
            return (Criteria) this;
        }

        public Criteria andMatchStatusCodeEqualTo(String value) {
            addCriterion("match_status_code =", value, "matchStatusCode");
            return (Criteria) this;
        }

        public Criteria andMatchStatusCodeNotEqualTo(String value) {
            addCriterion("match_status_code <>", value, "matchStatusCode");
            return (Criteria) this;
        }

        public Criteria andMatchStatusCodeGreaterThan(String value) {
            addCriterion("match_status_code >", value, "matchStatusCode");
            return (Criteria) this;
        }

        public Criteria andMatchStatusCodeGreaterThanOrEqualTo(String value) {
            addCriterion("match_status_code >=", value, "matchStatusCode");
            return (Criteria) this;
        }

        public Criteria andMatchStatusCodeLessThan(String value) {
            addCriterion("match_status_code <", value, "matchStatusCode");
            return (Criteria) this;
        }

        public Criteria andMatchStatusCodeLessThanOrEqualTo(String value) {
            addCriterion("match_status_code <=", value, "matchStatusCode");
            return (Criteria) this;
        }

        public Criteria andMatchStatusCodeLike(String value) {
            addCriterion("match_status_code like", value, "matchStatusCode");
            return (Criteria) this;
        }

        public Criteria andMatchStatusCodeNotLike(String value) {
            addCriterion("match_status_code not like", value, "matchStatusCode");
            return (Criteria) this;
        }

        public Criteria andMatchStatusCodeIn(List<String> values) {
            addCriterion("match_status_code in", values, "matchStatusCode");
            return (Criteria) this;
        }

        public Criteria andMatchStatusCodeNotIn(List<String> values) {
            addCriterion("match_status_code not in", values, "matchStatusCode");
            return (Criteria) this;
        }

        public Criteria andMatchStatusCodeBetween(String value1, String value2) {
            addCriterion("match_status_code between", value1, value2, "matchStatusCode");
            return (Criteria) this;
        }

        public Criteria andMatchStatusCodeNotBetween(String value1, String value2) {
            addCriterion("match_status_code not between", value1, value2, "matchStatusCode");
            return (Criteria) this;
        }

        public Criteria andAuditorIsNull() {
            addCriterion("auditor is null");
            return (Criteria) this;
        }

        public Criteria andAuditorIsNotNull() {
            addCriterion("auditor is not null");
            return (Criteria) this;
        }

        public Criteria andAuditorEqualTo(String value) {
            addCriterion("auditor =", value, "auditor");
            return (Criteria) this;
        }

        public Criteria andAuditorNotEqualTo(String value) {
            addCriterion("auditor <>", value, "auditor");
            return (Criteria) this;
        }

        public Criteria andAuditorGreaterThan(String value) {
            addCriterion("auditor >", value, "auditor");
            return (Criteria) this;
        }

        public Criteria andAuditorGreaterThanOrEqualTo(String value) {
            addCriterion("auditor >=", value, "auditor");
            return (Criteria) this;
        }

        public Criteria andAuditorLessThan(String value) {
            addCriterion("auditor <", value, "auditor");
            return (Criteria) this;
        }

        public Criteria andAuditorLessThanOrEqualTo(String value) {
            addCriterion("auditor <=", value, "auditor");
            return (Criteria) this;
        }

        public Criteria andAuditorLike(String value) {
            addCriterion("auditor like", value, "auditor");
            return (Criteria) this;
        }

        public Criteria andAuditorNotLike(String value) {
            addCriterion("auditor not like", value, "auditor");
            return (Criteria) this;
        }

        public Criteria andAuditorIn(List<String> values) {
            addCriterion("auditor in", values, "auditor");
            return (Criteria) this;
        }

        public Criteria andAuditorNotIn(List<String> values) {
            addCriterion("auditor not in", values, "auditor");
            return (Criteria) this;
        }

        public Criteria andAuditorBetween(String value1, String value2) {
            addCriterion("auditor between", value1, value2, "auditor");
            return (Criteria) this;
        }

        public Criteria andAuditorNotBetween(String value1, String value2) {
            addCriterion("auditor not between", value1, value2, "auditor");
            return (Criteria) this;
        }

        public Criteria andLabelIsNull() {
            addCriterion("label is null");
            return (Criteria) this;
        }

        public Criteria andLabelIsNotNull() {
            addCriterion("label is not null");
            return (Criteria) this;
        }

        public Criteria andLabelEqualTo(String value) {
            addCriterion("label =", value, "label");
            return (Criteria) this;
        }

        public Criteria andLabelNotEqualTo(String value) {
            addCriterion("label <>", value, "label");
            return (Criteria) this;
        }

        public Criteria andLabelGreaterThan(String value) {
            addCriterion("label >", value, "label");
            return (Criteria) this;
        }

        public Criteria andLabelGreaterThanOrEqualTo(String value) {
            addCriterion("label >=", value, "label");
            return (Criteria) this;
        }

        public Criteria andLabelLessThan(String value) {
            addCriterion("label <", value, "label");
            return (Criteria) this;
        }

        public Criteria andLabelLessThanOrEqualTo(String value) {
            addCriterion("label <=", value, "label");
            return (Criteria) this;
        }

        public Criteria andLabelLike(String value) {
            addCriterion("label like", value, "label");
            return (Criteria) this;
        }

        public Criteria andLabelNotLike(String value) {
            addCriterion("label not like", value, "label");
            return (Criteria) this;
        }

        public Criteria andLabelIn(List<String> values) {
            addCriterion("label in", values, "label");
            return (Criteria) this;
        }

        public Criteria andLabelNotIn(List<String> values) {
            addCriterion("label not in", values, "label");
            return (Criteria) this;
        }

        public Criteria andLabelBetween(String value1, String value2) {
            addCriterion("label between", value1, value2, "label");
            return (Criteria) this;
        }

        public Criteria andLabelNotBetween(String value1, String value2) {
            addCriterion("label not between", value1, value2, "label");
            return (Criteria) this;
        }

        public Criteria andImpIsSuccessIsNull() {
            addCriterion("imp_is_success is null");
            return (Criteria) this;
        }

        public Criteria andImpIsSuccessIsNotNull() {
            addCriterion("imp_is_success is not null");
            return (Criteria) this;
        }

        public Criteria andImpIsSuccessEqualTo(Integer value) {
            addCriterion("imp_is_success =", value, "impIsSuccess");
            return (Criteria) this;
        }

        public Criteria andImpIsSuccessNotEqualTo(Integer value) {
            addCriterion("imp_is_success <>", value, "impIsSuccess");
            return (Criteria) this;
        }

        public Criteria andImpIsSuccessGreaterThan(Integer value) {
            addCriterion("imp_is_success >", value, "impIsSuccess");
            return (Criteria) this;
        }

        public Criteria andImpIsSuccessGreaterThanOrEqualTo(Integer value) {
            addCriterion("imp_is_success >=", value, "impIsSuccess");
            return (Criteria) this;
        }

        public Criteria andImpIsSuccessLessThan(Integer value) {
            addCriterion("imp_is_success <", value, "impIsSuccess");
            return (Criteria) this;
        }

        public Criteria andImpIsSuccessLessThanOrEqualTo(Integer value) {
            addCriterion("imp_is_success <=", value, "impIsSuccess");
            return (Criteria) this;
        }

        public Criteria andImpIsSuccessIn(List<Integer> values) {
            addCriterion("imp_is_success in", values, "impIsSuccess");
            return (Criteria) this;
        }

        public Criteria andImpIsSuccessNotIn(List<Integer> values) {
            addCriterion("imp_is_success not in", values, "impIsSuccess");
            return (Criteria) this;
        }

        public Criteria andImpIsSuccessBetween(Integer value1, Integer value2) {
            addCriterion("imp_is_success between", value1, value2, "impIsSuccess");
            return (Criteria) this;
        }

        public Criteria andImpIsSuccessNotBetween(Integer value1, Integer value2) {
            addCriterion("imp_is_success not between", value1, value2, "impIsSuccess");
            return (Criteria) this;
        }

        public Criteria andImpDescriptionIsNull() {
            addCriterion("imp_description is null");
            return (Criteria) this;
        }

        public Criteria andImpDescriptionIsNotNull() {
            addCriterion("imp_description is not null");
            return (Criteria) this;
        }

        public Criteria andImpDescriptionEqualTo(String value) {
            addCriterion("imp_description =", value, "impDescription");
            return (Criteria) this;
        }

        public Criteria andImpDescriptionNotEqualTo(String value) {
            addCriterion("imp_description <>", value, "impDescription");
            return (Criteria) this;
        }

        public Criteria andImpDescriptionGreaterThan(String value) {
            addCriterion("imp_description >", value, "impDescription");
            return (Criteria) this;
        }

        public Criteria andImpDescriptionGreaterThanOrEqualTo(String value) {
            addCriterion("imp_description >=", value, "impDescription");
            return (Criteria) this;
        }

        public Criteria andImpDescriptionLessThan(String value) {
            addCriterion("imp_description <", value, "impDescription");
            return (Criteria) this;
        }

        public Criteria andImpDescriptionLessThanOrEqualTo(String value) {
            addCriterion("imp_description <=", value, "impDescription");
            return (Criteria) this;
        }

        public Criteria andImpDescriptionLike(String value) {
            addCriterion("imp_description like", value, "impDescription");
            return (Criteria) this;
        }

        public Criteria andImpDescriptionNotLike(String value) {
            addCriterion("imp_description not like", value, "impDescription");
            return (Criteria) this;
        }

        public Criteria andImpDescriptionIn(List<String> values) {
            addCriterion("imp_description in", values, "impDescription");
            return (Criteria) this;
        }

        public Criteria andImpDescriptionNotIn(List<String> values) {
            addCriterion("imp_description not in", values, "impDescription");
            return (Criteria) this;
        }

        public Criteria andImpDescriptionBetween(String value1, String value2) {
            addCriterion("imp_description between", value1, value2, "impDescription");
            return (Criteria) this;
        }

        public Criteria andImpDescriptionNotBetween(String value1, String value2) {
            addCriterion("imp_description not between", value1, value2, "impDescription");
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