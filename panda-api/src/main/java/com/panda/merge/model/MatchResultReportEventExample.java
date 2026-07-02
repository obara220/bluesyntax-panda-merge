package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class MatchResultReportEventExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MatchResultReportEventExample() {
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

        public Criteria andStandardEventIdIsNull() {
            addCriterion("standard_event_id is null");
            return (Criteria) this;
        }

        public Criteria andStandardEventIdIsNotNull() {
            addCriterion("standard_event_id is not null");
            return (Criteria) this;
        }

        public Criteria andStandardEventIdEqualTo(Long value) {
            addCriterion("standard_event_id =", value, "standardEventId");
            return (Criteria) this;
        }

        public Criteria andStandardEventIdNotEqualTo(Long value) {
            addCriterion("standard_event_id <>", value, "standardEventId");
            return (Criteria) this;
        }

        public Criteria andStandardEventIdGreaterThan(Long value) {
            addCriterion("standard_event_id >", value, "standardEventId");
            return (Criteria) this;
        }

        public Criteria andStandardEventIdGreaterThanOrEqualTo(Long value) {
            addCriterion("standard_event_id >=", value, "standardEventId");
            return (Criteria) this;
        }

        public Criteria andStandardEventIdLessThan(Long value) {
            addCriterion("standard_event_id <", value, "standardEventId");
            return (Criteria) this;
        }

        public Criteria andStandardEventIdLessThanOrEqualTo(Long value) {
            addCriterion("standard_event_id <=", value, "standardEventId");
            return (Criteria) this;
        }

        public Criteria andStandardEventIdIn(List<Long> values) {
            addCriterion("standard_event_id in", values, "standardEventId");
            return (Criteria) this;
        }

        public Criteria andStandardEventIdNotIn(List<Long> values) {
            addCriterion("standard_event_id not in", values, "standardEventId");
            return (Criteria) this;
        }

        public Criteria andStandardEventIdBetween(Long value1, Long value2) {
            addCriterion("standard_event_id between", value1, value2, "standardEventId");
            return (Criteria) this;
        }

        public Criteria andStandardEventIdNotBetween(Long value1, Long value2) {
            addCriterion("standard_event_id not between", value1, value2, "standardEventId");
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

        public Criteria andSportIdIsNull() {
            addCriterion("sport_id is null");
            return (Criteria) this;
        }

        public Criteria andSportIdIsNotNull() {
            addCriterion("sport_id is not null");
            return (Criteria) this;
        }

        public Criteria andSportIdEqualTo(Integer value) {
            addCriterion("sport_id =", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdNotEqualTo(Integer value) {
            addCriterion("sport_id <>", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdGreaterThan(Integer value) {
            addCriterion("sport_id >", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("sport_id >=", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdLessThan(Integer value) {
            addCriterion("sport_id <", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdLessThanOrEqualTo(Integer value) {
            addCriterion("sport_id <=", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdIn(List<Integer> values) {
            addCriterion("sport_id in", values, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdNotIn(List<Integer> values) {
            addCriterion("sport_id not in", values, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdBetween(Integer value1, Integer value2) {
            addCriterion("sport_id between", value1, value2, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdNotBetween(Integer value1, Integer value2) {
            addCriterion("sport_id not between", value1, value2, "sportId");
            return (Criteria) this;
        }

        public Criteria andTournamentSportIdIsNull() {
            addCriterion("tournament_sport_id is null");
            return (Criteria) this;
        }

        public Criteria andTournamentSportIdIsNotNull() {
            addCriterion("tournament_sport_id is not null");
            return (Criteria) this;
        }

        public Criteria andTournamentSportIdEqualTo(Long value) {
            addCriterion("tournament_sport_id =", value, "tournamentSportId");
            return (Criteria) this;
        }

        public Criteria andTournamentSportIdNotEqualTo(Long value) {
            addCriterion("tournament_sport_id <>", value, "tournamentSportId");
            return (Criteria) this;
        }

        public Criteria andTournamentSportIdGreaterThan(Long value) {
            addCriterion("tournament_sport_id >", value, "tournamentSportId");
            return (Criteria) this;
        }

        public Criteria andTournamentSportIdGreaterThanOrEqualTo(Long value) {
            addCriterion("tournament_sport_id >=", value, "tournamentSportId");
            return (Criteria) this;
        }

        public Criteria andTournamentSportIdLessThan(Long value) {
            addCriterion("tournament_sport_id <", value, "tournamentSportId");
            return (Criteria) this;
        }

        public Criteria andTournamentSportIdLessThanOrEqualTo(Long value) {
            addCriterion("tournament_sport_id <=", value, "tournamentSportId");
            return (Criteria) this;
        }

        public Criteria andTournamentSportIdIn(List<Long> values) {
            addCriterion("tournament_sport_id in", values, "tournamentSportId");
            return (Criteria) this;
        }

        public Criteria andTournamentSportIdNotIn(List<Long> values) {
            addCriterion("tournament_sport_id not in", values, "tournamentSportId");
            return (Criteria) this;
        }

        public Criteria andTournamentSportIdBetween(Long value1, Long value2) {
            addCriterion("tournament_sport_id between", value1, value2, "tournamentSportId");
            return (Criteria) this;
        }

        public Criteria andTournamentSportIdNotBetween(Long value1, Long value2) {
            addCriterion("tournament_sport_id not between", value1, value2, "tournamentSportId");
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

        public Criteria andEditTimesIsNull() {
            addCriterion("edit_times is null");
            return (Criteria) this;
        }

        public Criteria andEditTimesIsNotNull() {
            addCriterion("edit_times is not null");
            return (Criteria) this;
        }

        public Criteria andEditTimesEqualTo(Integer value) {
            addCriterion("edit_times =", value, "editTimes");
            return (Criteria) this;
        }

        public Criteria andEditTimesNotEqualTo(Integer value) {
            addCriterion("edit_times <>", value, "editTimes");
            return (Criteria) this;
        }

        public Criteria andEditTimesGreaterThan(Integer value) {
            addCriterion("edit_times >", value, "editTimes");
            return (Criteria) this;
        }

        public Criteria andEditTimesGreaterThanOrEqualTo(Integer value) {
            addCriterion("edit_times >=", value, "editTimes");
            return (Criteria) this;
        }

        public Criteria andEditTimesLessThan(Integer value) {
            addCriterion("edit_times <", value, "editTimes");
            return (Criteria) this;
        }

        public Criteria andEditTimesLessThanOrEqualTo(Integer value) {
            addCriterion("edit_times <=", value, "editTimes");
            return (Criteria) this;
        }

        public Criteria andEditTimesIn(List<Integer> values) {
            addCriterion("edit_times in", values, "editTimes");
            return (Criteria) this;
        }

        public Criteria andEditTimesNotIn(List<Integer> values) {
            addCriterion("edit_times not in", values, "editTimes");
            return (Criteria) this;
        }

        public Criteria andEditTimesBetween(Integer value1, Integer value2) {
            addCriterion("edit_times between", value1, value2, "editTimes");
            return (Criteria) this;
        }

        public Criteria andEditTimesNotBetween(Integer value1, Integer value2) {
            addCriterion("edit_times not between", value1, value2, "editTimes");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIdIsNull() {
            addCriterion("confirm_user_id is null");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIdIsNotNull() {
            addCriterion("confirm_user_id is not null");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIdEqualTo(String value) {
            addCriterion("confirm_user_id =", value, "confirmUserId");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIdNotEqualTo(String value) {
            addCriterion("confirm_user_id <>", value, "confirmUserId");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIdGreaterThan(String value) {
            addCriterion("confirm_user_id >", value, "confirmUserId");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIdGreaterThanOrEqualTo(String value) {
            addCriterion("confirm_user_id >=", value, "confirmUserId");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIdLessThan(String value) {
            addCriterion("confirm_user_id <", value, "confirmUserId");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIdLessThanOrEqualTo(String value) {
            addCriterion("confirm_user_id <=", value, "confirmUserId");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIdLike(String value) {
            addCriterion("confirm_user_id like", value, "confirmUserId");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIdNotLike(String value) {
            addCriterion("confirm_user_id not like", value, "confirmUserId");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIdIn(List<String> values) {
            addCriterion("confirm_user_id in", values, "confirmUserId");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIdNotIn(List<String> values) {
            addCriterion("confirm_user_id not in", values, "confirmUserId");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIdBetween(String value1, String value2) {
            addCriterion("confirm_user_id between", value1, value2, "confirmUserId");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIdNotBetween(String value1, String value2) {
            addCriterion("confirm_user_id not between", value1, value2, "confirmUserId");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIsNull() {
            addCriterion("confirm_user is null");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIsNotNull() {
            addCriterion("confirm_user is not null");
            return (Criteria) this;
        }

        public Criteria andConfirmUserEqualTo(String value) {
            addCriterion("confirm_user =", value, "confirmUser");
            return (Criteria) this;
        }

        public Criteria andConfirmUserNotEqualTo(String value) {
            addCriterion("confirm_user <>", value, "confirmUser");
            return (Criteria) this;
        }

        public Criteria andConfirmUserGreaterThan(String value) {
            addCriterion("confirm_user >", value, "confirmUser");
            return (Criteria) this;
        }

        public Criteria andConfirmUserGreaterThanOrEqualTo(String value) {
            addCriterion("confirm_user >=", value, "confirmUser");
            return (Criteria) this;
        }

        public Criteria andConfirmUserLessThan(String value) {
            addCriterion("confirm_user <", value, "confirmUser");
            return (Criteria) this;
        }

        public Criteria andConfirmUserLessThanOrEqualTo(String value) {
            addCriterion("confirm_user <=", value, "confirmUser");
            return (Criteria) this;
        }

        public Criteria andConfirmUserLike(String value) {
            addCriterion("confirm_user like", value, "confirmUser");
            return (Criteria) this;
        }

        public Criteria andConfirmUserNotLike(String value) {
            addCriterion("confirm_user not like", value, "confirmUser");
            return (Criteria) this;
        }

        public Criteria andConfirmUserIn(List<String> values) {
            addCriterion("confirm_user in", values, "confirmUser");
            return (Criteria) this;
        }

        public Criteria andConfirmUserNotIn(List<String> values) {
            addCriterion("confirm_user not in", values, "confirmUser");
            return (Criteria) this;
        }

        public Criteria andConfirmUserBetween(String value1, String value2) {
            addCriterion("confirm_user between", value1, value2, "confirmUser");
            return (Criteria) this;
        }

        public Criteria andConfirmUserNotBetween(String value1, String value2) {
            addCriterion("confirm_user not between", value1, value2, "confirmUser");
            return (Criteria) this;
        }

        public Criteria andConfirmTakeTimeIsNull() {
            addCriterion("confirm_take_time is null");
            return (Criteria) this;
        }

        public Criteria andConfirmTakeTimeIsNotNull() {
            addCriterion("confirm_take_time is not null");
            return (Criteria) this;
        }

        public Criteria andConfirmTakeTimeEqualTo(Long value) {
            addCriterion("confirm_take_time =", value, "confirmTakeTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTakeTimeNotEqualTo(Long value) {
            addCriterion("confirm_take_time <>", value, "confirmTakeTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTakeTimeGreaterThan(Long value) {
            addCriterion("confirm_take_time >", value, "confirmTakeTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTakeTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("confirm_take_time >=", value, "confirmTakeTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTakeTimeLessThan(Long value) {
            addCriterion("confirm_take_time <", value, "confirmTakeTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTakeTimeLessThanOrEqualTo(Long value) {
            addCriterion("confirm_take_time <=", value, "confirmTakeTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTakeTimeIn(List<Long> values) {
            addCriterion("confirm_take_time in", values, "confirmTakeTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTakeTimeNotIn(List<Long> values) {
            addCriterion("confirm_take_time not in", values, "confirmTakeTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTakeTimeBetween(Long value1, Long value2) {
            addCriterion("confirm_take_time between", value1, value2, "confirmTakeTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTakeTimeNotBetween(Long value1, Long value2) {
            addCriterion("confirm_take_time not between", value1, value2, "confirmTakeTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeIsNull() {
            addCriterion("confirm_time is null");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeIsNotNull() {
            addCriterion("confirm_time is not null");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeEqualTo(Long value) {
            addCriterion("confirm_time =", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeNotEqualTo(Long value) {
            addCriterion("confirm_time <>", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeGreaterThan(Long value) {
            addCriterion("confirm_time >", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("confirm_time >=", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeLessThan(Long value) {
            addCriterion("confirm_time <", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeLessThanOrEqualTo(Long value) {
            addCriterion("confirm_time <=", value, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeIn(List<Long> values) {
            addCriterion("confirm_time in", values, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeNotIn(List<Long> values) {
            addCriterion("confirm_time not in", values, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeBetween(Long value1, Long value2) {
            addCriterion("confirm_time between", value1, value2, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andConfirmTimeNotBetween(Long value1, Long value2) {
            addCriterion("confirm_time not between", value1, value2, "confirmTime");
            return (Criteria) this;
        }

        public Criteria andMatchDataSourceCodeListIsNull() {
            addCriterion("match_data_source_code_list is null");
            return (Criteria) this;
        }

        public Criteria andMatchDataSourceCodeListIsNotNull() {
            addCriterion("match_data_source_code_list is not null");
            return (Criteria) this;
        }

        public Criteria andMatchDataSourceCodeListEqualTo(String value) {
            addCriterion("match_data_source_code_list =", value, "matchDataSourceCodeList");
            return (Criteria) this;
        }

        public Criteria andMatchDataSourceCodeListNotEqualTo(String value) {
            addCriterion("match_data_source_code_list <>", value, "matchDataSourceCodeList");
            return (Criteria) this;
        }

        public Criteria andMatchDataSourceCodeListGreaterThan(String value) {
            addCriterion("match_data_source_code_list >", value, "matchDataSourceCodeList");
            return (Criteria) this;
        }

        public Criteria andMatchDataSourceCodeListGreaterThanOrEqualTo(String value) {
            addCriterion("match_data_source_code_list >=", value, "matchDataSourceCodeList");
            return (Criteria) this;
        }

        public Criteria andMatchDataSourceCodeListLessThan(String value) {
            addCriterion("match_data_source_code_list <", value, "matchDataSourceCodeList");
            return (Criteria) this;
        }

        public Criteria andMatchDataSourceCodeListLessThanOrEqualTo(String value) {
            addCriterion("match_data_source_code_list <=", value, "matchDataSourceCodeList");
            return (Criteria) this;
        }

        public Criteria andMatchDataSourceCodeListLike(String value) {
            addCriterion("match_data_source_code_list like", value, "matchDataSourceCodeList");
            return (Criteria) this;
        }

        public Criteria andMatchDataSourceCodeListNotLike(String value) {
            addCriterion("match_data_source_code_list not like", value, "matchDataSourceCodeList");
            return (Criteria) this;
        }

        public Criteria andMatchDataSourceCodeListIn(List<String> values) {
            addCriterion("match_data_source_code_list in", values, "matchDataSourceCodeList");
            return (Criteria) this;
        }

        public Criteria andMatchDataSourceCodeListNotIn(List<String> values) {
            addCriterion("match_data_source_code_list not in", values, "matchDataSourceCodeList");
            return (Criteria) this;
        }

        public Criteria andMatchDataSourceCodeListBetween(String value1, String value2) {
            addCriterion("match_data_source_code_list between", value1, value2, "matchDataSourceCodeList");
            return (Criteria) this;
        }

        public Criteria andMatchDataSourceCodeListNotBetween(String value1, String value2) {
            addCriterion("match_data_source_code_list not between", value1, value2, "matchDataSourceCodeList");
            return (Criteria) this;
        }

        public Criteria andIsAddEventIsNull() {
            addCriterion("is_add_event is null");
            return (Criteria) this;
        }

        public Criteria andIsAddEventIsNotNull() {
            addCriterion("is_add_event is not null");
            return (Criteria) this;
        }

        public Criteria andIsAddEventEqualTo(Boolean value) {
            addCriterion("is_add_event =", value, "isAddEvent");
            return (Criteria) this;
        }

        public Criteria andIsAddEventNotEqualTo(Boolean value) {
            addCriterion("is_add_event <>", value, "isAddEvent");
            return (Criteria) this;
        }

        public Criteria andIsAddEventGreaterThan(Boolean value) {
            addCriterion("is_add_event >", value, "isAddEvent");
            return (Criteria) this;
        }

        public Criteria andIsAddEventGreaterThanOrEqualTo(Boolean value) {
            addCriterion("is_add_event >=", value, "isAddEvent");
            return (Criteria) this;
        }

        public Criteria andIsAddEventLessThan(Boolean value) {
            addCriterion("is_add_event <", value, "isAddEvent");
            return (Criteria) this;
        }

        public Criteria andIsAddEventLessThanOrEqualTo(Boolean value) {
            addCriterion("is_add_event <=", value, "isAddEvent");
            return (Criteria) this;
        }

        public Criteria andIsAddEventIn(List<Boolean> values) {
            addCriterion("is_add_event in", values, "isAddEvent");
            return (Criteria) this;
        }

        public Criteria andIsAddEventNotIn(List<Boolean> values) {
            addCriterion("is_add_event not in", values, "isAddEvent");
            return (Criteria) this;
        }

        public Criteria andIsAddEventBetween(Boolean value1, Boolean value2) {
            addCriterion("is_add_event between", value1, value2, "isAddEvent");
            return (Criteria) this;
        }

        public Criteria andIsAddEventNotBetween(Boolean value1, Boolean value2) {
            addCriterion("is_add_event not between", value1, value2, "isAddEvent");
            return (Criteria) this;
        }

        public Criteria andMatchStartTimeIsNull() {
            addCriterion("match_start_time is null");
            return (Criteria) this;
        }

        public Criteria andMatchStartTimeIsNotNull() {
            addCriterion("match_start_time is not null");
            return (Criteria) this;
        }

        public Criteria andMatchStartTimeEqualTo(Long value) {
            addCriterion("match_start_time =", value, "matchStartTime");
            return (Criteria) this;
        }

        public Criteria andMatchStartTimeNotEqualTo(Long value) {
            addCriterion("match_start_time <>", value, "matchStartTime");
            return (Criteria) this;
        }

        public Criteria andMatchStartTimeGreaterThan(Long value) {
            addCriterion("match_start_time >", value, "matchStartTime");
            return (Criteria) this;
        }

        public Criteria andMatchStartTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("match_start_time >=", value, "matchStartTime");
            return (Criteria) this;
        }

        public Criteria andMatchStartTimeLessThan(Long value) {
            addCriterion("match_start_time <", value, "matchStartTime");
            return (Criteria) this;
        }

        public Criteria andMatchStartTimeLessThanOrEqualTo(Long value) {
            addCriterion("match_start_time <=", value, "matchStartTime");
            return (Criteria) this;
        }

        public Criteria andMatchStartTimeIn(List<Long> values) {
            addCriterion("match_start_time in", values, "matchStartTime");
            return (Criteria) this;
        }

        public Criteria andMatchStartTimeNotIn(List<Long> values) {
            addCriterion("match_start_time not in", values, "matchStartTime");
            return (Criteria) this;
        }

        public Criteria andMatchStartTimeBetween(Long value1, Long value2) {
            addCriterion("match_start_time between", value1, value2, "matchStartTime");
            return (Criteria) this;
        }

        public Criteria andMatchStartTimeNotBetween(Long value1, Long value2) {
            addCriterion("match_start_time not between", value1, value2, "matchStartTime");
            return (Criteria) this;
        }

        public Criteria andEventTimeIsNull() {
            addCriterion("event_time is null");
            return (Criteria) this;
        }

        public Criteria andEventTimeIsNotNull() {
            addCriterion("event_time is not null");
            return (Criteria) this;
        }

        public Criteria andEventTimeEqualTo(Long value) {
            addCriterion("event_time =", value, "eventTime");
            return (Criteria) this;
        }

        public Criteria andEventTimeNotEqualTo(Long value) {
            addCriterion("event_time <>", value, "eventTime");
            return (Criteria) this;
        }

        public Criteria andEventTimeGreaterThan(Long value) {
            addCriterion("event_time >", value, "eventTime");
            return (Criteria) this;
        }

        public Criteria andEventTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("event_time >=", value, "eventTime");
            return (Criteria) this;
        }

        public Criteria andEventTimeLessThan(Long value) {
            addCriterion("event_time <", value, "eventTime");
            return (Criteria) this;
        }

        public Criteria andEventTimeLessThanOrEqualTo(Long value) {
            addCriterion("event_time <=", value, "eventTime");
            return (Criteria) this;
        }

        public Criteria andEventTimeIn(List<Long> values) {
            addCriterion("event_time in", values, "eventTime");
            return (Criteria) this;
        }

        public Criteria andEventTimeNotIn(List<Long> values) {
            addCriterion("event_time not in", values, "eventTime");
            return (Criteria) this;
        }

        public Criteria andEventTimeBetween(Long value1, Long value2) {
            addCriterion("event_time between", value1, value2, "eventTime");
            return (Criteria) this;
        }

        public Criteria andEventTimeNotBetween(Long value1, Long value2) {
            addCriterion("event_time not between", value1, value2, "eventTime");
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