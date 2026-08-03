package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class ReplayStandardMatchInfoExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ReplayStandardMatchInfoExample() {
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

        public Criteria andStandardMatchIdIsNull() {
            addCriterion("standard_match_id is null");
            return (Criteria) this;
        }

        public Criteria andStandardMatchIdIsNotNull() {
            addCriterion("standard_match_id is not null");
            return (Criteria) this;
        }

        public Criteria andStandardMatchIdEqualTo(Long value) {
            addCriterion("standard_match_id =", value, "standardMatchId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchIdNotEqualTo(Long value) {
            addCriterion("standard_match_id <>", value, "standardMatchId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchIdGreaterThan(Long value) {
            addCriterion("standard_match_id >", value, "standardMatchId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchIdGreaterThanOrEqualTo(Long value) {
            addCriterion("standard_match_id >=", value, "standardMatchId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchIdLessThan(Long value) {
            addCriterion("standard_match_id <", value, "standardMatchId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchIdLessThanOrEqualTo(Long value) {
            addCriterion("standard_match_id <=", value, "standardMatchId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchIdIn(List<Long> values) {
            addCriterion("standard_match_id in", values, "standardMatchId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchIdNotIn(List<Long> values) {
            addCriterion("standard_match_id not in", values, "standardMatchId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchIdBetween(Long value1, Long value2) {
            addCriterion("standard_match_id between", value1, value2, "standardMatchId");
            return (Criteria) this;
        }

        public Criteria andStandardMatchIdNotBetween(Long value1, Long value2) {
            addCriterion("standard_match_id not between", value1, value2, "standardMatchId");
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

        public Criteria andSyncStatusIsNull() {
            addCriterion("sync_status is null");
            return (Criteria) this;
        }

        public Criteria andSyncStatusIsNotNull() {
            addCriterion("sync_status is not null");
            return (Criteria) this;
        }

        public Criteria andSyncStatusEqualTo(Integer value) {
            addCriterion("sync_status =", value, "syncStatus");
            return (Criteria) this;
        }

        public Criteria andSyncStatusNotEqualTo(Integer value) {
            addCriterion("sync_status <>", value, "syncStatus");
            return (Criteria) this;
        }

        public Criteria andSyncStatusGreaterThan(Integer value) {
            addCriterion("sync_status >", value, "syncStatus");
            return (Criteria) this;
        }

        public Criteria andSyncStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("sync_status >=", value, "syncStatus");
            return (Criteria) this;
        }

        public Criteria andSyncStatusLessThan(Integer value) {
            addCriterion("sync_status <", value, "syncStatus");
            return (Criteria) this;
        }

        public Criteria andSyncStatusLessThanOrEqualTo(Integer value) {
            addCriterion("sync_status <=", value, "syncStatus");
            return (Criteria) this;
        }

        public Criteria andSyncStatusIn(List<Integer> values) {
            addCriterion("sync_status in", values, "syncStatus");
            return (Criteria) this;
        }

        public Criteria andSyncStatusNotIn(List<Integer> values) {
            addCriterion("sync_status not in", values, "syncStatus");
            return (Criteria) this;
        }

        public Criteria andSyncStatusBetween(Integer value1, Integer value2) {
            addCriterion("sync_status between", value1, value2, "syncStatus");
            return (Criteria) this;
        }

        public Criteria andSyncStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("sync_status not between", value1, value2, "syncStatus");
            return (Criteria) this;
        }

        public Criteria andReplayCountIsNull() {
            addCriterion("replay_count is null");
            return (Criteria) this;
        }

        public Criteria andReplayCountIsNotNull() {
            addCriterion("replay_count is not null");
            return (Criteria) this;
        }

        public Criteria andReplayCountEqualTo(Integer value) {
            addCriterion("replay_count =", value, "replayCount");
            return (Criteria) this;
        }

        public Criteria andReplayCountNotEqualTo(Integer value) {
            addCriterion("replay_count <>", value, "replayCount");
            return (Criteria) this;
        }

        public Criteria andReplayCountGreaterThan(Integer value) {
            addCriterion("replay_count >", value, "replayCount");
            return (Criteria) this;
        }

        public Criteria andReplayCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("replay_count >=", value, "replayCount");
            return (Criteria) this;
        }

        public Criteria andReplayCountLessThan(Integer value) {
            addCriterion("replay_count <", value, "replayCount");
            return (Criteria) this;
        }

        public Criteria andReplayCountLessThanOrEqualTo(Integer value) {
            addCriterion("replay_count <=", value, "replayCount");
            return (Criteria) this;
        }

        public Criteria andReplayCountIn(List<Integer> values) {
            addCriterion("replay_count in", values, "replayCount");
            return (Criteria) this;
        }

        public Criteria andReplayCountNotIn(List<Integer> values) {
            addCriterion("replay_count not in", values, "replayCount");
            return (Criteria) this;
        }

        public Criteria andReplayCountBetween(Integer value1, Integer value2) {
            addCriterion("replay_count between", value1, value2, "replayCount");
            return (Criteria) this;
        }

        public Criteria andReplayCountNotBetween(Integer value1, Integer value2) {
            addCriterion("replay_count not between", value1, value2, "replayCount");
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

        public Criteria andReplayNumberIsNull() {
            addCriterion("replay_number is null");
            return (Criteria) this;
        }

        public Criteria andReplayNumberIsNotNull() {
            addCriterion("replay_number is not null");
            return (Criteria) this;
        }

        public Criteria andReplayNumberEqualTo(Integer value) {
            addCriterion("replay_number =", value, "replayNumber");
            return (Criteria) this;
        }

        public Criteria andReplayNumberNotEqualTo(Integer value) {
            addCriterion("replay_number <>", value, "replayNumber");
            return (Criteria) this;
        }

        public Criteria andReplayNumberGreaterThan(Integer value) {
            addCriterion("replay_number >", value, "replayNumber");
            return (Criteria) this;
        }

        public Criteria andReplayNumberGreaterThanOrEqualTo(Integer value) {
            addCriterion("replay_number >=", value, "replayNumber");
            return (Criteria) this;
        }

        public Criteria andReplayNumberLessThan(Integer value) {
            addCriterion("replay_number <", value, "replayNumber");
            return (Criteria) this;
        }

        public Criteria andReplayNumberLessThanOrEqualTo(Integer value) {
            addCriterion("replay_number <=", value, "replayNumber");
            return (Criteria) this;
        }

        public Criteria andReplayNumberIn(List<Integer> values) {
            addCriterion("replay_number in", values, "replayNumber");
            return (Criteria) this;
        }

        public Criteria andReplayNumberNotIn(List<Integer> values) {
            addCriterion("replay_number not in", values, "replayNumber");
            return (Criteria) this;
        }

        public Criteria andReplayNumberBetween(Integer value1, Integer value2) {
            addCriterion("replay_number between", value1, value2, "replayNumber");
            return (Criteria) this;
        }

        public Criteria andReplayNumberNotBetween(Integer value1, Integer value2) {
            addCriterion("replay_number not between", value1, value2, "replayNumber");
            return (Criteria) this;
        }

        public Criteria andReplayMatchCountIsNull() {
            addCriterion("replay_match_count is null");
            return (Criteria) this;
        }

        public Criteria andReplayMatchCountIsNotNull() {
            addCriterion("replay_match_count is not null");
            return (Criteria) this;
        }

        public Criteria andReplayMatchCountEqualTo(Integer value) {
            addCriterion("replay_match_count =", value, "replayMatchCount");
            return (Criteria) this;
        }

        public Criteria andReplayMatchCountNotEqualTo(Integer value) {
            addCriterion("replay_match_count <>", value, "replayMatchCount");
            return (Criteria) this;
        }

        public Criteria andReplayMatchCountGreaterThan(Integer value) {
            addCriterion("replay_match_count >", value, "replayMatchCount");
            return (Criteria) this;
        }

        public Criteria andReplayMatchCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("replay_match_count >=", value, "replayMatchCount");
            return (Criteria) this;
        }

        public Criteria andReplayMatchCountLessThan(Integer value) {
            addCriterion("replay_match_count <", value, "replayMatchCount");
            return (Criteria) this;
        }

        public Criteria andReplayMatchCountLessThanOrEqualTo(Integer value) {
            addCriterion("replay_match_count <=", value, "replayMatchCount");
            return (Criteria) this;
        }

        public Criteria andReplayMatchCountIn(List<Integer> values) {
            addCriterion("replay_match_count in", values, "replayMatchCount");
            return (Criteria) this;
        }

        public Criteria andReplayMatchCountNotIn(List<Integer> values) {
            addCriterion("replay_match_count not in", values, "replayMatchCount");
            return (Criteria) this;
        }

        public Criteria andReplayMatchCountBetween(Integer value1, Integer value2) {
            addCriterion("replay_match_count between", value1, value2, "replayMatchCount");
            return (Criteria) this;
        }

        public Criteria andReplayMatchCountNotBetween(Integer value1, Integer value2) {
            addCriterion("replay_match_count not between", value1, value2, "replayMatchCount");
            return (Criteria) this;
        }

        public Criteria andMatchEventCountIsNull() {
            addCriterion("match_event_count is null");
            return (Criteria) this;
        }

        public Criteria andMatchEventCountIsNotNull() {
            addCriterion("match_event_count is not null");
            return (Criteria) this;
        }

        public Criteria andMatchEventCountEqualTo(Integer value) {
            addCriterion("match_event_count =", value, "matchEventCount");
            return (Criteria) this;
        }

        public Criteria andMatchEventCountNotEqualTo(Integer value) {
            addCriterion("match_event_count <>", value, "matchEventCount");
            return (Criteria) this;
        }

        public Criteria andMatchEventCountGreaterThan(Integer value) {
            addCriterion("match_event_count >", value, "matchEventCount");
            return (Criteria) this;
        }

        public Criteria andMatchEventCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("match_event_count >=", value, "matchEventCount");
            return (Criteria) this;
        }

        public Criteria andMatchEventCountLessThan(Integer value) {
            addCriterion("match_event_count <", value, "matchEventCount");
            return (Criteria) this;
        }

        public Criteria andMatchEventCountLessThanOrEqualTo(Integer value) {
            addCriterion("match_event_count <=", value, "matchEventCount");
            return (Criteria) this;
        }

        public Criteria andMatchEventCountIn(List<Integer> values) {
            addCriterion("match_event_count in", values, "matchEventCount");
            return (Criteria) this;
        }

        public Criteria andMatchEventCountNotIn(List<Integer> values) {
            addCriterion("match_event_count not in", values, "matchEventCount");
            return (Criteria) this;
        }

        public Criteria andMatchEventCountBetween(Integer value1, Integer value2) {
            addCriterion("match_event_count between", value1, value2, "matchEventCount");
            return (Criteria) this;
        }

        public Criteria andMatchEventCountNotBetween(Integer value1, Integer value2) {
            addCriterion("match_event_count not between", value1, value2, "matchEventCount");
            return (Criteria) this;
        }

        public Criteria andOddsCountIsNull() {
            addCriterion("odds_count is null");
            return (Criteria) this;
        }

        public Criteria andOddsCountIsNotNull() {
            addCriterion("odds_count is not null");
            return (Criteria) this;
        }

        public Criteria andOddsCountEqualTo(Integer value) {
            addCriterion("odds_count =", value, "oddsCount");
            return (Criteria) this;
        }

        public Criteria andOddsCountNotEqualTo(Integer value) {
            addCriterion("odds_count <>", value, "oddsCount");
            return (Criteria) this;
        }

        public Criteria andOddsCountGreaterThan(Integer value) {
            addCriterion("odds_count >", value, "oddsCount");
            return (Criteria) this;
        }

        public Criteria andOddsCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("odds_count >=", value, "oddsCount");
            return (Criteria) this;
        }

        public Criteria andOddsCountLessThan(Integer value) {
            addCriterion("odds_count <", value, "oddsCount");
            return (Criteria) this;
        }

        public Criteria andOddsCountLessThanOrEqualTo(Integer value) {
            addCriterion("odds_count <=", value, "oddsCount");
            return (Criteria) this;
        }

        public Criteria andOddsCountIn(List<Integer> values) {
            addCriterion("odds_count in", values, "oddsCount");
            return (Criteria) this;
        }

        public Criteria andOddsCountNotIn(List<Integer> values) {
            addCriterion("odds_count not in", values, "oddsCount");
            return (Criteria) this;
        }

        public Criteria andOddsCountBetween(Integer value1, Integer value2) {
            addCriterion("odds_count between", value1, value2, "oddsCount");
            return (Criteria) this;
        }

        public Criteria andOddsCountNotBetween(Integer value1, Integer value2) {
            addCriterion("odds_count not between", value1, value2, "oddsCount");
            return (Criteria) this;
        }

        public Criteria andMatchResultCountIsNull() {
            addCriterion("match_result_count is null");
            return (Criteria) this;
        }

        public Criteria andMatchResultCountIsNotNull() {
            addCriterion("match_result_count is not null");
            return (Criteria) this;
        }

        public Criteria andMatchResultCountEqualTo(Integer value) {
            addCriterion("match_result_count =", value, "matchResultCount");
            return (Criteria) this;
        }

        public Criteria andMatchResultCountNotEqualTo(Integer value) {
            addCriterion("match_result_count <>", value, "matchResultCount");
            return (Criteria) this;
        }

        public Criteria andMatchResultCountGreaterThan(Integer value) {
            addCriterion("match_result_count >", value, "matchResultCount");
            return (Criteria) this;
        }

        public Criteria andMatchResultCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("match_result_count >=", value, "matchResultCount");
            return (Criteria) this;
        }

        public Criteria andMatchResultCountLessThan(Integer value) {
            addCriterion("match_result_count <", value, "matchResultCount");
            return (Criteria) this;
        }

        public Criteria andMatchResultCountLessThanOrEqualTo(Integer value) {
            addCriterion("match_result_count <=", value, "matchResultCount");
            return (Criteria) this;
        }

        public Criteria andMatchResultCountIn(List<Integer> values) {
            addCriterion("match_result_count in", values, "matchResultCount");
            return (Criteria) this;
        }

        public Criteria andMatchResultCountNotIn(List<Integer> values) {
            addCriterion("match_result_count not in", values, "matchResultCount");
            return (Criteria) this;
        }

        public Criteria andMatchResultCountBetween(Integer value1, Integer value2) {
            addCriterion("match_result_count between", value1, value2, "matchResultCount");
            return (Criteria) this;
        }

        public Criteria andMatchResultCountNotBetween(Integer value1, Integer value2) {
            addCriterion("match_result_count not between", value1, value2, "matchResultCount");
            return (Criteria) this;
        }

        public Criteria andMatchStatusCountIsNull() {
            addCriterion("match_status_count is null");
            return (Criteria) this;
        }

        public Criteria andMatchStatusCountIsNotNull() {
            addCriterion("match_status_count is not null");
            return (Criteria) this;
        }

        public Criteria andMatchStatusCountEqualTo(Integer value) {
            addCriterion("match_status_count =", value, "matchStatusCount");
            return (Criteria) this;
        }

        public Criteria andMatchStatusCountNotEqualTo(Integer value) {
            addCriterion("match_status_count <>", value, "matchStatusCount");
            return (Criteria) this;
        }

        public Criteria andMatchStatusCountGreaterThan(Integer value) {
            addCriterion("match_status_count >", value, "matchStatusCount");
            return (Criteria) this;
        }

        public Criteria andMatchStatusCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("match_status_count >=", value, "matchStatusCount");
            return (Criteria) this;
        }

        public Criteria andMatchStatusCountLessThan(Integer value) {
            addCriterion("match_status_count <", value, "matchStatusCount");
            return (Criteria) this;
        }

        public Criteria andMatchStatusCountLessThanOrEqualTo(Integer value) {
            addCriterion("match_status_count <=", value, "matchStatusCount");
            return (Criteria) this;
        }

        public Criteria andMatchStatusCountIn(List<Integer> values) {
            addCriterion("match_status_count in", values, "matchStatusCount");
            return (Criteria) this;
        }

        public Criteria andMatchStatusCountNotIn(List<Integer> values) {
            addCriterion("match_status_count not in", values, "matchStatusCount");
            return (Criteria) this;
        }

        public Criteria andMatchStatusCountBetween(Integer value1, Integer value2) {
            addCriterion("match_status_count between", value1, value2, "matchStatusCount");
            return (Criteria) this;
        }

        public Criteria andMatchStatusCountNotBetween(Integer value1, Integer value2) {
            addCriterion("match_status_count not between", value1, value2, "matchStatusCount");
            return (Criteria) this;
        }

        public Criteria andReplayBeginTimeIsNull() {
            addCriterion("replay_begin_time is null");
            return (Criteria) this;
        }

        public Criteria andReplayBeginTimeIsNotNull() {
            addCriterion("replay_begin_time is not null");
            return (Criteria) this;
        }

        public Criteria andReplayBeginTimeEqualTo(Long value) {
            addCriterion("replay_begin_time =", value, "replayBeginTime");
            return (Criteria) this;
        }

        public Criteria andReplayBeginTimeNotEqualTo(Long value) {
            addCriterion("replay_begin_time <>", value, "replayBeginTime");
            return (Criteria) this;
        }

        public Criteria andReplayBeginTimeGreaterThan(Long value) {
            addCriterion("replay_begin_time >", value, "replayBeginTime");
            return (Criteria) this;
        }

        public Criteria andReplayBeginTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("replay_begin_time >=", value, "replayBeginTime");
            return (Criteria) this;
        }

        public Criteria andReplayBeginTimeLessThan(Long value) {
            addCriterion("replay_begin_time <", value, "replayBeginTime");
            return (Criteria) this;
        }

        public Criteria andReplayBeginTimeLessThanOrEqualTo(Long value) {
            addCriterion("replay_begin_time <=", value, "replayBeginTime");
            return (Criteria) this;
        }

        public Criteria andReplayBeginTimeIn(List<Long> values) {
            addCriterion("replay_begin_time in", values, "replayBeginTime");
            return (Criteria) this;
        }

        public Criteria andReplayBeginTimeNotIn(List<Long> values) {
            addCriterion("replay_begin_time not in", values, "replayBeginTime");
            return (Criteria) this;
        }

        public Criteria andReplayBeginTimeBetween(Long value1, Long value2) {
            addCriterion("replay_begin_time between", value1, value2, "replayBeginTime");
            return (Criteria) this;
        }

        public Criteria andReplayBeginTimeNotBetween(Long value1, Long value2) {
            addCriterion("replay_begin_time not between", value1, value2, "replayBeginTime");
            return (Criteria) this;
        }

        public Criteria andLastTimeReplayEndTimeIsNull() {
            addCriterion("last_time_replay_end_time is null");
            return (Criteria) this;
        }

        public Criteria andLastTimeReplayEndTimeIsNotNull() {
            addCriterion("last_time_replay_end_time is not null");
            return (Criteria) this;
        }

        public Criteria andLastTimeReplayEndTimeEqualTo(Long value) {
            addCriterion("last_time_replay_end_time =", value, "lastTimeReplayEndTime");
            return (Criteria) this;
        }

        public Criteria andLastTimeReplayEndTimeNotEqualTo(Long value) {
            addCriterion("last_time_replay_end_time <>", value, "lastTimeReplayEndTime");
            return (Criteria) this;
        }

        public Criteria andLastTimeReplayEndTimeGreaterThan(Long value) {
            addCriterion("last_time_replay_end_time >", value, "lastTimeReplayEndTime");
            return (Criteria) this;
        }

        public Criteria andLastTimeReplayEndTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("last_time_replay_end_time >=", value, "lastTimeReplayEndTime");
            return (Criteria) this;
        }

        public Criteria andLastTimeReplayEndTimeLessThan(Long value) {
            addCriterion("last_time_replay_end_time <", value, "lastTimeReplayEndTime");
            return (Criteria) this;
        }

        public Criteria andLastTimeReplayEndTimeLessThanOrEqualTo(Long value) {
            addCriterion("last_time_replay_end_time <=", value, "lastTimeReplayEndTime");
            return (Criteria) this;
        }

        public Criteria andLastTimeReplayEndTimeIn(List<Long> values) {
            addCriterion("last_time_replay_end_time in", values, "lastTimeReplayEndTime");
            return (Criteria) this;
        }

        public Criteria andLastTimeReplayEndTimeNotIn(List<Long> values) {
            addCriterion("last_time_replay_end_time not in", values, "lastTimeReplayEndTime");
            return (Criteria) this;
        }

        public Criteria andLastTimeReplayEndTimeBetween(Long value1, Long value2) {
            addCriterion("last_time_replay_end_time between", value1, value2, "lastTimeReplayEndTime");
            return (Criteria) this;
        }

        public Criteria andLastTimeReplayEndTimeNotBetween(Long value1, Long value2) {
            addCriterion("last_time_replay_end_time not between", value1, value2, "lastTimeReplayEndTime");
            return (Criteria) this;
        }

        public Criteria andReplayStatusIsNull() {
            addCriterion("replay_status is null");
            return (Criteria) this;
        }

        public Criteria andReplayStatusIsNotNull() {
            addCriterion("replay_status is not null");
            return (Criteria) this;
        }

        public Criteria andReplayStatusEqualTo(Integer value) {
            addCriterion("replay_status =", value, "replayStatus");
            return (Criteria) this;
        }

        public Criteria andReplayStatusNotEqualTo(Integer value) {
            addCriterion("replay_status <>", value, "replayStatus");
            return (Criteria) this;
        }

        public Criteria andReplayStatusGreaterThan(Integer value) {
            addCriterion("replay_status >", value, "replayStatus");
            return (Criteria) this;
        }

        public Criteria andReplayStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("replay_status >=", value, "replayStatus");
            return (Criteria) this;
        }

        public Criteria andReplayStatusLessThan(Integer value) {
            addCriterion("replay_status <", value, "replayStatus");
            return (Criteria) this;
        }

        public Criteria andReplayStatusLessThanOrEqualTo(Integer value) {
            addCriterion("replay_status <=", value, "replayStatus");
            return (Criteria) this;
        }

        public Criteria andReplayStatusIn(List<Integer> values) {
            addCriterion("replay_status in", values, "replayStatus");
            return (Criteria) this;
        }

        public Criteria andReplayStatusNotIn(List<Integer> values) {
            addCriterion("replay_status not in", values, "replayStatus");
            return (Criteria) this;
        }

        public Criteria andReplayStatusBetween(Integer value1, Integer value2) {
            addCriterion("replay_status between", value1, value2, "replayStatus");
            return (Criteria) this;
        }

        public Criteria andReplayStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("replay_status not between", value1, value2, "replayStatus");
            return (Criteria) this;
        }

        public Criteria andReplayDelaySecondsIsNull() {
            addCriterion("replay_delay_seconds is null");
            return (Criteria) this;
        }

        public Criteria andReplayDelaySecondsIsNotNull() {
            addCriterion("replay_delay_seconds is not null");
            return (Criteria) this;
        }

        public Criteria andReplayDelaySecondsEqualTo(Integer value) {
            addCriterion("replay_delay_seconds =", value, "replayDelaySeconds");
            return (Criteria) this;
        }

        public Criteria andReplayDelaySecondsNotEqualTo(Integer value) {
            addCriterion("replay_delay_seconds <>", value, "replayDelaySeconds");
            return (Criteria) this;
        }

        public Criteria andReplayDelaySecondsGreaterThan(Integer value) {
            addCriterion("replay_delay_seconds >", value, "replayDelaySeconds");
            return (Criteria) this;
        }

        public Criteria andReplayDelaySecondsGreaterThanOrEqualTo(Integer value) {
            addCriterion("replay_delay_seconds >=", value, "replayDelaySeconds");
            return (Criteria) this;
        }

        public Criteria andReplayDelaySecondsLessThan(Integer value) {
            addCriterion("replay_delay_seconds <", value, "replayDelaySeconds");
            return (Criteria) this;
        }

        public Criteria andReplayDelaySecondsLessThanOrEqualTo(Integer value) {
            addCriterion("replay_delay_seconds <=", value, "replayDelaySeconds");
            return (Criteria) this;
        }

        public Criteria andReplayDelaySecondsIn(List<Integer> values) {
            addCriterion("replay_delay_seconds in", values, "replayDelaySeconds");
            return (Criteria) this;
        }

        public Criteria andReplayDelaySecondsNotIn(List<Integer> values) {
            addCriterion("replay_delay_seconds not in", values, "replayDelaySeconds");
            return (Criteria) this;
        }

        public Criteria andReplayDelaySecondsBetween(Integer value1, Integer value2) {
            addCriterion("replay_delay_seconds between", value1, value2, "replayDelaySeconds");
            return (Criteria) this;
        }

        public Criteria andReplayDelaySecondsNotBetween(Integer value1, Integer value2) {
            addCriterion("replay_delay_seconds not between", value1, value2, "replayDelaySeconds");
            return (Criteria) this;
        }

        public Criteria andSubSyncStatusIsNull() {
            addCriterion("sub_sync_status is null");
            return (Criteria) this;
        }

        public Criteria andSubSyncStatusIsNotNull() {
            addCriterion("sub_sync_status is not null");
            return (Criteria) this;
        }

        public Criteria andSubSyncStatusEqualTo(String value) {
            addCriterion("sub_sync_status =", value, "subSyncStatus");
            return (Criteria) this;
        }

        public Criteria andSubSyncStatusNotEqualTo(String value) {
            addCriterion("sub_sync_status <>", value, "subSyncStatus");
            return (Criteria) this;
        }

        public Criteria andSubSyncStatusGreaterThan(String value) {
            addCriterion("sub_sync_status >", value, "subSyncStatus");
            return (Criteria) this;
        }

        public Criteria andSubSyncStatusGreaterThanOrEqualTo(String value) {
            addCriterion("sub_sync_status >=", value, "subSyncStatus");
            return (Criteria) this;
        }

        public Criteria andSubSyncStatusLessThan(String value) {
            addCriterion("sub_sync_status <", value, "subSyncStatus");
            return (Criteria) this;
        }

        public Criteria andSubSyncStatusLessThanOrEqualTo(String value) {
            addCriterion("sub_sync_status <=", value, "subSyncStatus");
            return (Criteria) this;
        }

        public Criteria andSubSyncStatusLike(String value) {
            addCriterion("sub_sync_status like", value, "subSyncStatus");
            return (Criteria) this;
        }

        public Criteria andSubSyncStatusNotLike(String value) {
            addCriterion("sub_sync_status not like", value, "subSyncStatus");
            return (Criteria) this;
        }

        public Criteria andSubSyncStatusIn(List<String> values) {
            addCriterion("sub_sync_status in", values, "subSyncStatus");
            return (Criteria) this;
        }

        public Criteria andSubSyncStatusNotIn(List<String> values) {
            addCriterion("sub_sync_status not in", values, "subSyncStatus");
            return (Criteria) this;
        }

        public Criteria andSubSyncStatusBetween(String value1, String value2) {
            addCriterion("sub_sync_status between", value1, value2, "subSyncStatus");
            return (Criteria) this;
        }

        public Criteria andSubSyncStatusNotBetween(String value1, String value2) {
            addCriterion("sub_sync_status not between", value1, value2, "subSyncStatus");
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