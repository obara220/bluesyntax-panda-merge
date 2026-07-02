package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class StandardSportMarketSellExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public StandardSportMarketSellExample() {
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

        public Criteria andMatchInfoIdIsNull() {
            addCriterion("match_info_id is null");
            return (Criteria) this;
        }

        public Criteria andMatchInfoIdIsNotNull() {
            addCriterion("match_info_id is not null");
            return (Criteria) this;
        }

        public Criteria andMatchInfoIdEqualTo(Long value) {
            addCriterion("match_info_id =", value, "matchInfoId");
            return (Criteria) this;
        }

        public Criteria andMatchInfoIdNotEqualTo(Long value) {
            addCriterion("match_info_id <>", value, "matchInfoId");
            return (Criteria) this;
        }

        public Criteria andMatchInfoIdGreaterThan(Long value) {
            addCriterion("match_info_id >", value, "matchInfoId");
            return (Criteria) this;
        }

        public Criteria andMatchInfoIdGreaterThanOrEqualTo(Long value) {
            addCriterion("match_info_id >=", value, "matchInfoId");
            return (Criteria) this;
        }

        public Criteria andMatchInfoIdLessThan(Long value) {
            addCriterion("match_info_id <", value, "matchInfoId");
            return (Criteria) this;
        }

        public Criteria andMatchInfoIdLessThanOrEqualTo(Long value) {
            addCriterion("match_info_id <=", value, "matchInfoId");
            return (Criteria) this;
        }

        public Criteria andMatchInfoIdIn(List<Long> values) {
            addCriterion("match_info_id in", values, "matchInfoId");
            return (Criteria) this;
        }

        public Criteria andMatchInfoIdNotIn(List<Long> values) {
            addCriterion("match_info_id not in", values, "matchInfoId");
            return (Criteria) this;
        }

        public Criteria andMatchInfoIdBetween(Long value1, Long value2) {
            addCriterion("match_info_id between", value1, value2, "matchInfoId");
            return (Criteria) this;
        }

        public Criteria andMatchInfoIdNotBetween(Long value1, Long value2) {
            addCriterion("match_info_id not between", value1, value2, "matchInfoId");
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

        public Criteria andPreMatchDataProviderCodeIsNull() {
            addCriterion("pre_match_data_provider_code is null");
            return (Criteria) this;
        }

        public Criteria andPreMatchDataProviderCodeIsNotNull() {
            addCriterion("pre_match_data_provider_code is not null");
            return (Criteria) this;
        }

        public Criteria andPreMatchDataProviderCodeEqualTo(String value) {
            addCriterion("pre_match_data_provider_code =", value, "preMatchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andPreMatchDataProviderCodeNotEqualTo(String value) {
            addCriterion("pre_match_data_provider_code <>", value, "preMatchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andPreMatchDataProviderCodeGreaterThan(String value) {
            addCriterion("pre_match_data_provider_code >", value, "preMatchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andPreMatchDataProviderCodeGreaterThanOrEqualTo(String value) {
            addCriterion("pre_match_data_provider_code >=", value, "preMatchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andPreMatchDataProviderCodeLessThan(String value) {
            addCriterion("pre_match_data_provider_code <", value, "preMatchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andPreMatchDataProviderCodeLessThanOrEqualTo(String value) {
            addCriterion("pre_match_data_provider_code <=", value, "preMatchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andPreMatchDataProviderCodeLike(String value) {
            addCriterion("pre_match_data_provider_code like", value, "preMatchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andPreMatchDataProviderCodeNotLike(String value) {
            addCriterion("pre_match_data_provider_code not like", value, "preMatchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andPreMatchDataProviderCodeIn(List<String> values) {
            addCriterion("pre_match_data_provider_code in", values, "preMatchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andPreMatchDataProviderCodeNotIn(List<String> values) {
            addCriterion("pre_match_data_provider_code not in", values, "preMatchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andPreMatchDataProviderCodeBetween(String value1, String value2) {
            addCriterion("pre_match_data_provider_code between", value1, value2, "preMatchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andPreMatchDataProviderCodeNotBetween(String value1, String value2) {
            addCriterion("pre_match_data_provider_code not between", value1, value2, "preMatchDataProviderCode");
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

        public Criteria andLiveMatchDataProviderCodeIsNull() {
            addCriterion("live_match_data_provider_code is null");
            return (Criteria) this;
        }

        public Criteria andLiveMatchDataProviderCodeIsNotNull() {
            addCriterion("live_match_data_provider_code is not null");
            return (Criteria) this;
        }

        public Criteria andLiveMatchDataProviderCodeEqualTo(String value) {
            addCriterion("live_match_data_provider_code =", value, "liveMatchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andLiveMatchDataProviderCodeNotEqualTo(String value) {
            addCriterion("live_match_data_provider_code <>", value, "liveMatchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andLiveMatchDataProviderCodeGreaterThan(String value) {
            addCriterion("live_match_data_provider_code >", value, "liveMatchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andLiveMatchDataProviderCodeGreaterThanOrEqualTo(String value) {
            addCriterion("live_match_data_provider_code >=", value, "liveMatchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andLiveMatchDataProviderCodeLessThan(String value) {
            addCriterion("live_match_data_provider_code <", value, "liveMatchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andLiveMatchDataProviderCodeLessThanOrEqualTo(String value) {
            addCriterion("live_match_data_provider_code <=", value, "liveMatchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andLiveMatchDataProviderCodeLike(String value) {
            addCriterion("live_match_data_provider_code like", value, "liveMatchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andLiveMatchDataProviderCodeNotLike(String value) {
            addCriterion("live_match_data_provider_code not like", value, "liveMatchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andLiveMatchDataProviderCodeIn(List<String> values) {
            addCriterion("live_match_data_provider_code in", values, "liveMatchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andLiveMatchDataProviderCodeNotIn(List<String> values) {
            addCriterion("live_match_data_provider_code not in", values, "liveMatchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andLiveMatchDataProviderCodeBetween(String value1, String value2) {
            addCriterion("live_match_data_provider_code between", value1, value2, "liveMatchDataProviderCode");
            return (Criteria) this;
        }

        public Criteria andLiveMatchDataProviderCodeNotBetween(String value1, String value2) {
            addCriterion("live_match_data_provider_code not between", value1, value2, "liveMatchDataProviderCode");
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

        public Criteria andTeamHomeIdIsNull() {
            addCriterion("team_home_id is null");
            return (Criteria) this;
        }

        public Criteria andTeamHomeIdIsNotNull() {
            addCriterion("team_home_id is not null");
            return (Criteria) this;
        }

        public Criteria andTeamHomeIdEqualTo(Long value) {
            addCriterion("team_home_id =", value, "teamHomeId");
            return (Criteria) this;
        }

        public Criteria andTeamHomeIdNotEqualTo(Long value) {
            addCriterion("team_home_id <>", value, "teamHomeId");
            return (Criteria) this;
        }

        public Criteria andTeamHomeIdGreaterThan(Long value) {
            addCriterion("team_home_id >", value, "teamHomeId");
            return (Criteria) this;
        }

        public Criteria andTeamHomeIdGreaterThanOrEqualTo(Long value) {
            addCriterion("team_home_id >=", value, "teamHomeId");
            return (Criteria) this;
        }

        public Criteria andTeamHomeIdLessThan(Long value) {
            addCriterion("team_home_id <", value, "teamHomeId");
            return (Criteria) this;
        }

        public Criteria andTeamHomeIdLessThanOrEqualTo(Long value) {
            addCriterion("team_home_id <=", value, "teamHomeId");
            return (Criteria) this;
        }

        public Criteria andTeamHomeIdIn(List<Long> values) {
            addCriterion("team_home_id in", values, "teamHomeId");
            return (Criteria) this;
        }

        public Criteria andTeamHomeIdNotIn(List<Long> values) {
            addCriterion("team_home_id not in", values, "teamHomeId");
            return (Criteria) this;
        }

        public Criteria andTeamHomeIdBetween(Long value1, Long value2) {
            addCriterion("team_home_id between", value1, value2, "teamHomeId");
            return (Criteria) this;
        }

        public Criteria andTeamHomeIdNotBetween(Long value1, Long value2) {
            addCriterion("team_home_id not between", value1, value2, "teamHomeId");
            return (Criteria) this;
        }

        public Criteria andTeamAwayIdIsNull() {
            addCriterion("team_away_id is null");
            return (Criteria) this;
        }

        public Criteria andTeamAwayIdIsNotNull() {
            addCriterion("team_away_id is not null");
            return (Criteria) this;
        }

        public Criteria andTeamAwayIdEqualTo(Long value) {
            addCriterion("team_away_id =", value, "teamAwayId");
            return (Criteria) this;
        }

        public Criteria andTeamAwayIdNotEqualTo(Long value) {
            addCriterion("team_away_id <>", value, "teamAwayId");
            return (Criteria) this;
        }

        public Criteria andTeamAwayIdGreaterThan(Long value) {
            addCriterion("team_away_id >", value, "teamAwayId");
            return (Criteria) this;
        }

        public Criteria andTeamAwayIdGreaterThanOrEqualTo(Long value) {
            addCriterion("team_away_id >=", value, "teamAwayId");
            return (Criteria) this;
        }

        public Criteria andTeamAwayIdLessThan(Long value) {
            addCriterion("team_away_id <", value, "teamAwayId");
            return (Criteria) this;
        }

        public Criteria andTeamAwayIdLessThanOrEqualTo(Long value) {
            addCriterion("team_away_id <=", value, "teamAwayId");
            return (Criteria) this;
        }

        public Criteria andTeamAwayIdIn(List<Long> values) {
            addCriterion("team_away_id in", values, "teamAwayId");
            return (Criteria) this;
        }

        public Criteria andTeamAwayIdNotIn(List<Long> values) {
            addCriterion("team_away_id not in", values, "teamAwayId");
            return (Criteria) this;
        }

        public Criteria andTeamAwayIdBetween(Long value1, Long value2) {
            addCriterion("team_away_id between", value1, value2, "teamAwayId");
            return (Criteria) this;
        }

        public Criteria andTeamAwayIdNotBetween(Long value1, Long value2) {
            addCriterion("team_away_id not between", value1, value2, "teamAwayId");
            return (Criteria) this;
        }

        public Criteria andPreMatchTimeIsNull() {
            addCriterion("pre_match_time is null");
            return (Criteria) this;
        }

        public Criteria andPreMatchTimeIsNotNull() {
            addCriterion("pre_match_time is not null");
            return (Criteria) this;
        }

        public Criteria andPreMatchTimeEqualTo(Long value) {
            addCriterion("pre_match_time =", value, "preMatchTime");
            return (Criteria) this;
        }

        public Criteria andPreMatchTimeNotEqualTo(Long value) {
            addCriterion("pre_match_time <>", value, "preMatchTime");
            return (Criteria) this;
        }

        public Criteria andPreMatchTimeGreaterThan(Long value) {
            addCriterion("pre_match_time >", value, "preMatchTime");
            return (Criteria) this;
        }

        public Criteria andPreMatchTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("pre_match_time >=", value, "preMatchTime");
            return (Criteria) this;
        }

        public Criteria andPreMatchTimeLessThan(Long value) {
            addCriterion("pre_match_time <", value, "preMatchTime");
            return (Criteria) this;
        }

        public Criteria andPreMatchTimeLessThanOrEqualTo(Long value) {
            addCriterion("pre_match_time <=", value, "preMatchTime");
            return (Criteria) this;
        }

        public Criteria andPreMatchTimeIn(List<Long> values) {
            addCriterion("pre_match_time in", values, "preMatchTime");
            return (Criteria) this;
        }

        public Criteria andPreMatchTimeNotIn(List<Long> values) {
            addCriterion("pre_match_time not in", values, "preMatchTime");
            return (Criteria) this;
        }

        public Criteria andPreMatchTimeBetween(Long value1, Long value2) {
            addCriterion("pre_match_time between", value1, value2, "preMatchTime");
            return (Criteria) this;
        }

        public Criteria andPreMatchTimeNotBetween(Long value1, Long value2) {
            addCriterion("pre_match_time not between", value1, value2, "preMatchTime");
            return (Criteria) this;
        }

        public Criteria andLiveOddTimeIsNull() {
            addCriterion("live_odd_time is null");
            return (Criteria) this;
        }

        public Criteria andLiveOddTimeIsNotNull() {
            addCriterion("live_odd_time is not null");
            return (Criteria) this;
        }

        public Criteria andLiveOddTimeEqualTo(Long value) {
            addCriterion("live_odd_time =", value, "liveOddTime");
            return (Criteria) this;
        }

        public Criteria andLiveOddTimeNotEqualTo(Long value) {
            addCriterion("live_odd_time <>", value, "liveOddTime");
            return (Criteria) this;
        }

        public Criteria andLiveOddTimeGreaterThan(Long value) {
            addCriterion("live_odd_time >", value, "liveOddTime");
            return (Criteria) this;
        }

        public Criteria andLiveOddTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("live_odd_time >=", value, "liveOddTime");
            return (Criteria) this;
        }

        public Criteria andLiveOddTimeLessThan(Long value) {
            addCriterion("live_odd_time <", value, "liveOddTime");
            return (Criteria) this;
        }

        public Criteria andLiveOddTimeLessThanOrEqualTo(Long value) {
            addCriterion("live_odd_time <=", value, "liveOddTime");
            return (Criteria) this;
        }

        public Criteria andLiveOddTimeIn(List<Long> values) {
            addCriterion("live_odd_time in", values, "liveOddTime");
            return (Criteria) this;
        }

        public Criteria andLiveOddTimeNotIn(List<Long> values) {
            addCriterion("live_odd_time not in", values, "liveOddTime");
            return (Criteria) this;
        }

        public Criteria andLiveOddTimeBetween(Long value1, Long value2) {
            addCriterion("live_odd_time between", value1, value2, "liveOddTime");
            return (Criteria) this;
        }

        public Criteria andLiveOddTimeNotBetween(Long value1, Long value2) {
            addCriterion("live_odd_time not between", value1, value2, "liveOddTime");
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

        public Criteria andPreTraderIdIsNull() {
            addCriterion("pre_trader_id is null");
            return (Criteria) this;
        }

        public Criteria andPreTraderIdIsNotNull() {
            addCriterion("pre_trader_id is not null");
            return (Criteria) this;
        }

        public Criteria andPreTraderIdEqualTo(String value) {
            addCriterion("pre_trader_id =", value, "preTraderId");
            return (Criteria) this;
        }

        public Criteria andPreTraderIdNotEqualTo(String value) {
            addCriterion("pre_trader_id <>", value, "preTraderId");
            return (Criteria) this;
        }

        public Criteria andPreTraderIdGreaterThan(String value) {
            addCriterion("pre_trader_id >", value, "preTraderId");
            return (Criteria) this;
        }

        public Criteria andPreTraderIdGreaterThanOrEqualTo(String value) {
            addCriterion("pre_trader_id >=", value, "preTraderId");
            return (Criteria) this;
        }

        public Criteria andPreTraderIdLessThan(String value) {
            addCriterion("pre_trader_id <", value, "preTraderId");
            return (Criteria) this;
        }

        public Criteria andPreTraderIdLessThanOrEqualTo(String value) {
            addCriterion("pre_trader_id <=", value, "preTraderId");
            return (Criteria) this;
        }

        public Criteria andPreTraderIdLike(String value) {
            addCriterion("pre_trader_id like", value, "preTraderId");
            return (Criteria) this;
        }

        public Criteria andPreTraderIdNotLike(String value) {
            addCriterion("pre_trader_id not like", value, "preTraderId");
            return (Criteria) this;
        }

        public Criteria andPreTraderIdIn(List<String> values) {
            addCriterion("pre_trader_id in", values, "preTraderId");
            return (Criteria) this;
        }

        public Criteria andPreTraderIdNotIn(List<String> values) {
            addCriterion("pre_trader_id not in", values, "preTraderId");
            return (Criteria) this;
        }

        public Criteria andPreTraderIdBetween(String value1, String value2) {
            addCriterion("pre_trader_id between", value1, value2, "preTraderId");
            return (Criteria) this;
        }

        public Criteria andPreTraderIdNotBetween(String value1, String value2) {
            addCriterion("pre_trader_id not between", value1, value2, "preTraderId");
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

        public Criteria andPreTraderDepartmentIdIsNull() {
            addCriterion("pre_trader_department_id is null");
            return (Criteria) this;
        }

        public Criteria andPreTraderDepartmentIdIsNotNull() {
            addCriterion("pre_trader_department_id is not null");
            return (Criteria) this;
        }

        public Criteria andPreTraderDepartmentIdEqualTo(String value) {
            addCriterion("pre_trader_department_id =", value, "preTraderDepartmentId");
            return (Criteria) this;
        }

        public Criteria andPreTraderDepartmentIdNotEqualTo(String value) {
            addCriterion("pre_trader_department_id <>", value, "preTraderDepartmentId");
            return (Criteria) this;
        }

        public Criteria andPreTraderDepartmentIdGreaterThan(String value) {
            addCriterion("pre_trader_department_id >", value, "preTraderDepartmentId");
            return (Criteria) this;
        }

        public Criteria andPreTraderDepartmentIdGreaterThanOrEqualTo(String value) {
            addCriterion("pre_trader_department_id >=", value, "preTraderDepartmentId");
            return (Criteria) this;
        }

        public Criteria andPreTraderDepartmentIdLessThan(String value) {
            addCriterion("pre_trader_department_id <", value, "preTraderDepartmentId");
            return (Criteria) this;
        }

        public Criteria andPreTraderDepartmentIdLessThanOrEqualTo(String value) {
            addCriterion("pre_trader_department_id <=", value, "preTraderDepartmentId");
            return (Criteria) this;
        }

        public Criteria andPreTraderDepartmentIdLike(String value) {
            addCriterion("pre_trader_department_id like", value, "preTraderDepartmentId");
            return (Criteria) this;
        }

        public Criteria andPreTraderDepartmentIdNotLike(String value) {
            addCriterion("pre_trader_department_id not like", value, "preTraderDepartmentId");
            return (Criteria) this;
        }

        public Criteria andPreTraderDepartmentIdIn(List<String> values) {
            addCriterion("pre_trader_department_id in", values, "preTraderDepartmentId");
            return (Criteria) this;
        }

        public Criteria andPreTraderDepartmentIdNotIn(List<String> values) {
            addCriterion("pre_trader_department_id not in", values, "preTraderDepartmentId");
            return (Criteria) this;
        }

        public Criteria andPreTraderDepartmentIdBetween(String value1, String value2) {
            addCriterion("pre_trader_department_id between", value1, value2, "preTraderDepartmentId");
            return (Criteria) this;
        }

        public Criteria andPreTraderDepartmentIdNotBetween(String value1, String value2) {
            addCriterion("pre_trader_department_id not between", value1, value2, "preTraderDepartmentId");
            return (Criteria) this;
        }

        public Criteria andPreTraderStatusIsNull() {
            addCriterion("pre_trader_status is null");
            return (Criteria) this;
        }

        public Criteria andPreTraderStatusIsNotNull() {
            addCriterion("pre_trader_status is not null");
            return (Criteria) this;
        }

        public Criteria andPreTraderStatusEqualTo(String value) {
            addCriterion("pre_trader_status =", value, "preTraderStatus");
            return (Criteria) this;
        }

        public Criteria andPreTraderStatusNotEqualTo(String value) {
            addCriterion("pre_trader_status <>", value, "preTraderStatus");
            return (Criteria) this;
        }

        public Criteria andPreTraderStatusGreaterThan(String value) {
            addCriterion("pre_trader_status >", value, "preTraderStatus");
            return (Criteria) this;
        }

        public Criteria andPreTraderStatusGreaterThanOrEqualTo(String value) {
            addCriterion("pre_trader_status >=", value, "preTraderStatus");
            return (Criteria) this;
        }

        public Criteria andPreTraderStatusLessThan(String value) {
            addCriterion("pre_trader_status <", value, "preTraderStatus");
            return (Criteria) this;
        }

        public Criteria andPreTraderStatusLessThanOrEqualTo(String value) {
            addCriterion("pre_trader_status <=", value, "preTraderStatus");
            return (Criteria) this;
        }

        public Criteria andPreTraderStatusLike(String value) {
            addCriterion("pre_trader_status like", value, "preTraderStatus");
            return (Criteria) this;
        }

        public Criteria andPreTraderStatusNotLike(String value) {
            addCriterion("pre_trader_status not like", value, "preTraderStatus");
            return (Criteria) this;
        }

        public Criteria andPreTraderStatusIn(List<String> values) {
            addCriterion("pre_trader_status in", values, "preTraderStatus");
            return (Criteria) this;
        }

        public Criteria andPreTraderStatusNotIn(List<String> values) {
            addCriterion("pre_trader_status not in", values, "preTraderStatus");
            return (Criteria) this;
        }

        public Criteria andPreTraderStatusBetween(String value1, String value2) {
            addCriterion("pre_trader_status between", value1, value2, "preTraderStatus");
            return (Criteria) this;
        }

        public Criteria andPreTraderStatusNotBetween(String value1, String value2) {
            addCriterion("pre_trader_status not between", value1, value2, "preTraderStatus");
            return (Criteria) this;
        }

        public Criteria andLiveTraderIdIsNull() {
            addCriterion("live_trader_id is null");
            return (Criteria) this;
        }

        public Criteria andLiveTraderIdIsNotNull() {
            addCriterion("live_trader_id is not null");
            return (Criteria) this;
        }

        public Criteria andLiveTraderIdEqualTo(String value) {
            addCriterion("live_trader_id =", value, "liveTraderId");
            return (Criteria) this;
        }

        public Criteria andLiveTraderIdNotEqualTo(String value) {
            addCriterion("live_trader_id <>", value, "liveTraderId");
            return (Criteria) this;
        }

        public Criteria andLiveTraderIdGreaterThan(String value) {
            addCriterion("live_trader_id >", value, "liveTraderId");
            return (Criteria) this;
        }

        public Criteria andLiveTraderIdGreaterThanOrEqualTo(String value) {
            addCriterion("live_trader_id >=", value, "liveTraderId");
            return (Criteria) this;
        }

        public Criteria andLiveTraderIdLessThan(String value) {
            addCriterion("live_trader_id <", value, "liveTraderId");
            return (Criteria) this;
        }

        public Criteria andLiveTraderIdLessThanOrEqualTo(String value) {
            addCriterion("live_trader_id <=", value, "liveTraderId");
            return (Criteria) this;
        }

        public Criteria andLiveTraderIdLike(String value) {
            addCriterion("live_trader_id like", value, "liveTraderId");
            return (Criteria) this;
        }

        public Criteria andLiveTraderIdNotLike(String value) {
            addCriterion("live_trader_id not like", value, "liveTraderId");
            return (Criteria) this;
        }

        public Criteria andLiveTraderIdIn(List<String> values) {
            addCriterion("live_trader_id in", values, "liveTraderId");
            return (Criteria) this;
        }

        public Criteria andLiveTraderIdNotIn(List<String> values) {
            addCriterion("live_trader_id not in", values, "liveTraderId");
            return (Criteria) this;
        }

        public Criteria andLiveTraderIdBetween(String value1, String value2) {
            addCriterion("live_trader_id between", value1, value2, "liveTraderId");
            return (Criteria) this;
        }

        public Criteria andLiveTraderIdNotBetween(String value1, String value2) {
            addCriterion("live_trader_id not between", value1, value2, "liveTraderId");
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

        public Criteria andLiveTraderDepartmentIdIsNull() {
            addCriterion("live_trader_department_id is null");
            return (Criteria) this;
        }

        public Criteria andLiveTraderDepartmentIdIsNotNull() {
            addCriterion("live_trader_department_id is not null");
            return (Criteria) this;
        }

        public Criteria andLiveTraderDepartmentIdEqualTo(String value) {
            addCriterion("live_trader_department_id =", value, "liveTraderDepartmentId");
            return (Criteria) this;
        }

        public Criteria andLiveTraderDepartmentIdNotEqualTo(String value) {
            addCriterion("live_trader_department_id <>", value, "liveTraderDepartmentId");
            return (Criteria) this;
        }

        public Criteria andLiveTraderDepartmentIdGreaterThan(String value) {
            addCriterion("live_trader_department_id >", value, "liveTraderDepartmentId");
            return (Criteria) this;
        }

        public Criteria andLiveTraderDepartmentIdGreaterThanOrEqualTo(String value) {
            addCriterion("live_trader_department_id >=", value, "liveTraderDepartmentId");
            return (Criteria) this;
        }

        public Criteria andLiveTraderDepartmentIdLessThan(String value) {
            addCriterion("live_trader_department_id <", value, "liveTraderDepartmentId");
            return (Criteria) this;
        }

        public Criteria andLiveTraderDepartmentIdLessThanOrEqualTo(String value) {
            addCriterion("live_trader_department_id <=", value, "liveTraderDepartmentId");
            return (Criteria) this;
        }

        public Criteria andLiveTraderDepartmentIdLike(String value) {
            addCriterion("live_trader_department_id like", value, "liveTraderDepartmentId");
            return (Criteria) this;
        }

        public Criteria andLiveTraderDepartmentIdNotLike(String value) {
            addCriterion("live_trader_department_id not like", value, "liveTraderDepartmentId");
            return (Criteria) this;
        }

        public Criteria andLiveTraderDepartmentIdIn(List<String> values) {
            addCriterion("live_trader_department_id in", values, "liveTraderDepartmentId");
            return (Criteria) this;
        }

        public Criteria andLiveTraderDepartmentIdNotIn(List<String> values) {
            addCriterion("live_trader_department_id not in", values, "liveTraderDepartmentId");
            return (Criteria) this;
        }

        public Criteria andLiveTraderDepartmentIdBetween(String value1, String value2) {
            addCriterion("live_trader_department_id between", value1, value2, "liveTraderDepartmentId");
            return (Criteria) this;
        }

        public Criteria andLiveTraderDepartmentIdNotBetween(String value1, String value2) {
            addCriterion("live_trader_department_id not between", value1, value2, "liveTraderDepartmentId");
            return (Criteria) this;
        }

        public Criteria andLiveTraderStatusIsNull() {
            addCriterion("live_trader_status is null");
            return (Criteria) this;
        }

        public Criteria andLiveTraderStatusIsNotNull() {
            addCriterion("live_trader_status is not null");
            return (Criteria) this;
        }

        public Criteria andLiveTraderStatusEqualTo(String value) {
            addCriterion("live_trader_status =", value, "liveTraderStatus");
            return (Criteria) this;
        }

        public Criteria andLiveTraderStatusNotEqualTo(String value) {
            addCriterion("live_trader_status <>", value, "liveTraderStatus");
            return (Criteria) this;
        }

        public Criteria andLiveTraderStatusGreaterThan(String value) {
            addCriterion("live_trader_status >", value, "liveTraderStatus");
            return (Criteria) this;
        }

        public Criteria andLiveTraderStatusGreaterThanOrEqualTo(String value) {
            addCriterion("live_trader_status >=", value, "liveTraderStatus");
            return (Criteria) this;
        }

        public Criteria andLiveTraderStatusLessThan(String value) {
            addCriterion("live_trader_status <", value, "liveTraderStatus");
            return (Criteria) this;
        }

        public Criteria andLiveTraderStatusLessThanOrEqualTo(String value) {
            addCriterion("live_trader_status <=", value, "liveTraderStatus");
            return (Criteria) this;
        }

        public Criteria andLiveTraderStatusLike(String value) {
            addCriterion("live_trader_status like", value, "liveTraderStatus");
            return (Criteria) this;
        }

        public Criteria andLiveTraderStatusNotLike(String value) {
            addCriterion("live_trader_status not like", value, "liveTraderStatus");
            return (Criteria) this;
        }

        public Criteria andLiveTraderStatusIn(List<String> values) {
            addCriterion("live_trader_status in", values, "liveTraderStatus");
            return (Criteria) this;
        }

        public Criteria andLiveTraderStatusNotIn(List<String> values) {
            addCriterion("live_trader_status not in", values, "liveTraderStatus");
            return (Criteria) this;
        }

        public Criteria andLiveTraderStatusBetween(String value1, String value2) {
            addCriterion("live_trader_status between", value1, value2, "liveTraderStatus");
            return (Criteria) this;
        }

        public Criteria andLiveTraderStatusNotBetween(String value1, String value2) {
            addCriterion("live_trader_status not between", value1, value2, "liveTraderStatus");
            return (Criteria) this;
        }

        public Criteria andAuditorIdIsNull() {
            addCriterion("auditor_id is null");
            return (Criteria) this;
        }

        public Criteria andAuditorIdIsNotNull() {
            addCriterion("auditor_id is not null");
            return (Criteria) this;
        }

        public Criteria andAuditorIdEqualTo(String value) {
            addCriterion("auditor_id =", value, "auditorId");
            return (Criteria) this;
        }

        public Criteria andAuditorIdNotEqualTo(String value) {
            addCriterion("auditor_id <>", value, "auditorId");
            return (Criteria) this;
        }

        public Criteria andAuditorIdGreaterThan(String value) {
            addCriterion("auditor_id >", value, "auditorId");
            return (Criteria) this;
        }

        public Criteria andAuditorIdGreaterThanOrEqualTo(String value) {
            addCriterion("auditor_id >=", value, "auditorId");
            return (Criteria) this;
        }

        public Criteria andAuditorIdLessThan(String value) {
            addCriterion("auditor_id <", value, "auditorId");
            return (Criteria) this;
        }

        public Criteria andAuditorIdLessThanOrEqualTo(String value) {
            addCriterion("auditor_id <=", value, "auditorId");
            return (Criteria) this;
        }

        public Criteria andAuditorIdLike(String value) {
            addCriterion("auditor_id like", value, "auditorId");
            return (Criteria) this;
        }

        public Criteria andAuditorIdNotLike(String value) {
            addCriterion("auditor_id not like", value, "auditorId");
            return (Criteria) this;
        }

        public Criteria andAuditorIdIn(List<String> values) {
            addCriterion("auditor_id in", values, "auditorId");
            return (Criteria) this;
        }

        public Criteria andAuditorIdNotIn(List<String> values) {
            addCriterion("auditor_id not in", values, "auditorId");
            return (Criteria) this;
        }

        public Criteria andAuditorIdBetween(String value1, String value2) {
            addCriterion("auditor_id between", value1, value2, "auditorId");
            return (Criteria) this;
        }

        public Criteria andAuditorIdNotBetween(String value1, String value2) {
            addCriterion("auditor_id not between", value1, value2, "auditorId");
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

        public Criteria andAuditorDepartmentIdIsNull() {
            addCriterion("auditor_department_id is null");
            return (Criteria) this;
        }

        public Criteria andAuditorDepartmentIdIsNotNull() {
            addCriterion("auditor_department_id is not null");
            return (Criteria) this;
        }

        public Criteria andAuditorDepartmentIdEqualTo(String value) {
            addCriterion("auditor_department_id =", value, "auditorDepartmentId");
            return (Criteria) this;
        }

        public Criteria andAuditorDepartmentIdNotEqualTo(String value) {
            addCriterion("auditor_department_id <>", value, "auditorDepartmentId");
            return (Criteria) this;
        }

        public Criteria andAuditorDepartmentIdGreaterThan(String value) {
            addCriterion("auditor_department_id >", value, "auditorDepartmentId");
            return (Criteria) this;
        }

        public Criteria andAuditorDepartmentIdGreaterThanOrEqualTo(String value) {
            addCriterion("auditor_department_id >=", value, "auditorDepartmentId");
            return (Criteria) this;
        }

        public Criteria andAuditorDepartmentIdLessThan(String value) {
            addCriterion("auditor_department_id <", value, "auditorDepartmentId");
            return (Criteria) this;
        }

        public Criteria andAuditorDepartmentIdLessThanOrEqualTo(String value) {
            addCriterion("auditor_department_id <=", value, "auditorDepartmentId");
            return (Criteria) this;
        }

        public Criteria andAuditorDepartmentIdLike(String value) {
            addCriterion("auditor_department_id like", value, "auditorDepartmentId");
            return (Criteria) this;
        }

        public Criteria andAuditorDepartmentIdNotLike(String value) {
            addCriterion("auditor_department_id not like", value, "auditorDepartmentId");
            return (Criteria) this;
        }

        public Criteria andAuditorDepartmentIdIn(List<String> values) {
            addCriterion("auditor_department_id in", values, "auditorDepartmentId");
            return (Criteria) this;
        }

        public Criteria andAuditorDepartmentIdNotIn(List<String> values) {
            addCriterion("auditor_department_id not in", values, "auditorDepartmentId");
            return (Criteria) this;
        }

        public Criteria andAuditorDepartmentIdBetween(String value1, String value2) {
            addCriterion("auditor_department_id between", value1, value2, "auditorDepartmentId");
            return (Criteria) this;
        }

        public Criteria andAuditorDepartmentIdNotBetween(String value1, String value2) {
            addCriterion("auditor_department_id not between", value1, value2, "auditorDepartmentId");
            return (Criteria) this;
        }

        public Criteria andAuditorStatusIsNull() {
            addCriterion("auditor_status is null");
            return (Criteria) this;
        }

        public Criteria andAuditorStatusIsNotNull() {
            addCriterion("auditor_status is not null");
            return (Criteria) this;
        }

        public Criteria andAuditorStatusEqualTo(String value) {
            addCriterion("auditor_status =", value, "auditorStatus");
            return (Criteria) this;
        }

        public Criteria andAuditorStatusNotEqualTo(String value) {
            addCriterion("auditor_status <>", value, "auditorStatus");
            return (Criteria) this;
        }

        public Criteria andAuditorStatusGreaterThan(String value) {
            addCriterion("auditor_status >", value, "auditorStatus");
            return (Criteria) this;
        }

        public Criteria andAuditorStatusGreaterThanOrEqualTo(String value) {
            addCriterion("auditor_status >=", value, "auditorStatus");
            return (Criteria) this;
        }

        public Criteria andAuditorStatusLessThan(String value) {
            addCriterion("auditor_status <", value, "auditorStatus");
            return (Criteria) this;
        }

        public Criteria andAuditorStatusLessThanOrEqualTo(String value) {
            addCriterion("auditor_status <=", value, "auditorStatus");
            return (Criteria) this;
        }

        public Criteria andAuditorStatusLike(String value) {
            addCriterion("auditor_status like", value, "auditorStatus");
            return (Criteria) this;
        }

        public Criteria andAuditorStatusNotLike(String value) {
            addCriterion("auditor_status not like", value, "auditorStatus");
            return (Criteria) this;
        }

        public Criteria andAuditorStatusIn(List<String> values) {
            addCriterion("auditor_status in", values, "auditorStatus");
            return (Criteria) this;
        }

        public Criteria andAuditorStatusNotIn(List<String> values) {
            addCriterion("auditor_status not in", values, "auditorStatus");
            return (Criteria) this;
        }

        public Criteria andAuditorStatusBetween(String value1, String value2) {
            addCriterion("auditor_status between", value1, value2, "auditorStatus");
            return (Criteria) this;
        }

        public Criteria andAuditorStatusNotBetween(String value1, String value2) {
            addCriterion("auditor_status not between", value1, value2, "auditorStatus");
            return (Criteria) this;
        }

        public Criteria andReporterIsNull() {
            addCriterion("reporter is null");
            return (Criteria) this;
        }

        public Criteria andReporterIsNotNull() {
            addCriterion("reporter is not null");
            return (Criteria) this;
        }

        public Criteria andReporterEqualTo(String value) {
            addCriterion("reporter =", value, "reporter");
            return (Criteria) this;
        }

        public Criteria andReporterNotEqualTo(String value) {
            addCriterion("reporter <>", value, "reporter");
            return (Criteria) this;
        }

        public Criteria andReporterGreaterThan(String value) {
            addCriterion("reporter >", value, "reporter");
            return (Criteria) this;
        }

        public Criteria andReporterGreaterThanOrEqualTo(String value) {
            addCriterion("reporter >=", value, "reporter");
            return (Criteria) this;
        }

        public Criteria andReporterLessThan(String value) {
            addCriterion("reporter <", value, "reporter");
            return (Criteria) this;
        }

        public Criteria andReporterLessThanOrEqualTo(String value) {
            addCriterion("reporter <=", value, "reporter");
            return (Criteria) this;
        }

        public Criteria andReporterLike(String value) {
            addCriterion("reporter like", value, "reporter");
            return (Criteria) this;
        }

        public Criteria andReporterNotLike(String value) {
            addCriterion("reporter not like", value, "reporter");
            return (Criteria) this;
        }

        public Criteria andReporterIn(List<String> values) {
            addCriterion("reporter in", values, "reporter");
            return (Criteria) this;
        }

        public Criteria andReporterNotIn(List<String> values) {
            addCriterion("reporter not in", values, "reporter");
            return (Criteria) this;
        }

        public Criteria andReporterBetween(String value1, String value2) {
            addCriterion("reporter between", value1, value2, "reporter");
            return (Criteria) this;
        }

        public Criteria andReporterNotBetween(String value1, String value2) {
            addCriterion("reporter not between", value1, value2, "reporter");
            return (Criteria) this;
        }

        public Criteria andBusinessEventIsNull() {
            addCriterion("business_event is null");
            return (Criteria) this;
        }

        public Criteria andBusinessEventIsNotNull() {
            addCriterion("business_event is not null");
            return (Criteria) this;
        }

        public Criteria andBusinessEventEqualTo(String value) {
            addCriterion("business_event =", value, "businessEvent");
            return (Criteria) this;
        }

        public Criteria andBusinessEventNotEqualTo(String value) {
            addCriterion("business_event <>", value, "businessEvent");
            return (Criteria) this;
        }

        public Criteria andBusinessEventGreaterThan(String value) {
            addCriterion("business_event >", value, "businessEvent");
            return (Criteria) this;
        }

        public Criteria andBusinessEventGreaterThanOrEqualTo(String value) {
            addCriterion("business_event >=", value, "businessEvent");
            return (Criteria) this;
        }

        public Criteria andBusinessEventLessThan(String value) {
            addCriterion("business_event <", value, "businessEvent");
            return (Criteria) this;
        }

        public Criteria andBusinessEventLessThanOrEqualTo(String value) {
            addCriterion("business_event <=", value, "businessEvent");
            return (Criteria) this;
        }

        public Criteria andBusinessEventLike(String value) {
            addCriterion("business_event like", value, "businessEvent");
            return (Criteria) this;
        }

        public Criteria andBusinessEventNotLike(String value) {
            addCriterion("business_event not like", value, "businessEvent");
            return (Criteria) this;
        }

        public Criteria andBusinessEventIn(List<String> values) {
            addCriterion("business_event in", values, "businessEvent");
            return (Criteria) this;
        }

        public Criteria andBusinessEventNotIn(List<String> values) {
            addCriterion("business_event not in", values, "businessEvent");
            return (Criteria) this;
        }

        public Criteria andBusinessEventBetween(String value1, String value2) {
            addCriterion("business_event between", value1, value2, "businessEvent");
            return (Criteria) this;
        }

        public Criteria andBusinessEventNotBetween(String value1, String value2) {
            addCriterion("business_event not between", value1, value2, "businessEvent");
            return (Criteria) this;
        }

        public Criteria andPreMatchSellStatusIsNull() {
            addCriterion("pre_match_sell_status is null");
            return (Criteria) this;
        }

        public Criteria andPreMatchSellStatusIsNotNull() {
            addCriterion("pre_match_sell_status is not null");
            return (Criteria) this;
        }

        public Criteria andPreMatchSellStatusEqualTo(String value) {
            addCriterion("pre_match_sell_status =", value, "preMatchSellStatus");
            return (Criteria) this;
        }

        public Criteria andPreMatchSellStatusNotEqualTo(String value) {
            addCriterion("pre_match_sell_status <>", value, "preMatchSellStatus");
            return (Criteria) this;
        }

        public Criteria andPreMatchSellStatusGreaterThan(String value) {
            addCriterion("pre_match_sell_status >", value, "preMatchSellStatus");
            return (Criteria) this;
        }

        public Criteria andPreMatchSellStatusGreaterThanOrEqualTo(String value) {
            addCriterion("pre_match_sell_status >=", value, "preMatchSellStatus");
            return (Criteria) this;
        }

        public Criteria andPreMatchSellStatusLessThan(String value) {
            addCriterion("pre_match_sell_status <", value, "preMatchSellStatus");
            return (Criteria) this;
        }

        public Criteria andPreMatchSellStatusLessThanOrEqualTo(String value) {
            addCriterion("pre_match_sell_status <=", value, "preMatchSellStatus");
            return (Criteria) this;
        }

        public Criteria andPreMatchSellStatusLike(String value) {
            addCriterion("pre_match_sell_status like", value, "preMatchSellStatus");
            return (Criteria) this;
        }

        public Criteria andPreMatchSellStatusNotLike(String value) {
            addCriterion("pre_match_sell_status not like", value, "preMatchSellStatus");
            return (Criteria) this;
        }

        public Criteria andPreMatchSellStatusIn(List<String> values) {
            addCriterion("pre_match_sell_status in", values, "preMatchSellStatus");
            return (Criteria) this;
        }

        public Criteria andPreMatchSellStatusNotIn(List<String> values) {
            addCriterion("pre_match_sell_status not in", values, "preMatchSellStatus");
            return (Criteria) this;
        }

        public Criteria andPreMatchSellStatusBetween(String value1, String value2) {
            addCriterion("pre_match_sell_status between", value1, value2, "preMatchSellStatus");
            return (Criteria) this;
        }

        public Criteria andPreMatchSellStatusNotBetween(String value1, String value2) {
            addCriterion("pre_match_sell_status not between", value1, value2, "preMatchSellStatus");
            return (Criteria) this;
        }

        public Criteria andLiveMatchSellStatusIsNull() {
            addCriterion("live_match_sell_status is null");
            return (Criteria) this;
        }

        public Criteria andLiveMatchSellStatusIsNotNull() {
            addCriterion("live_match_sell_status is not null");
            return (Criteria) this;
        }

        public Criteria andLiveMatchSellStatusEqualTo(String value) {
            addCriterion("live_match_sell_status =", value, "liveMatchSellStatus");
            return (Criteria) this;
        }

        public Criteria andLiveMatchSellStatusNotEqualTo(String value) {
            addCriterion("live_match_sell_status <>", value, "liveMatchSellStatus");
            return (Criteria) this;
        }

        public Criteria andLiveMatchSellStatusGreaterThan(String value) {
            addCriterion("live_match_sell_status >", value, "liveMatchSellStatus");
            return (Criteria) this;
        }

        public Criteria andLiveMatchSellStatusGreaterThanOrEqualTo(String value) {
            addCriterion("live_match_sell_status >=", value, "liveMatchSellStatus");
            return (Criteria) this;
        }

        public Criteria andLiveMatchSellStatusLessThan(String value) {
            addCriterion("live_match_sell_status <", value, "liveMatchSellStatus");
            return (Criteria) this;
        }

        public Criteria andLiveMatchSellStatusLessThanOrEqualTo(String value) {
            addCriterion("live_match_sell_status <=", value, "liveMatchSellStatus");
            return (Criteria) this;
        }

        public Criteria andLiveMatchSellStatusLike(String value) {
            addCriterion("live_match_sell_status like", value, "liveMatchSellStatus");
            return (Criteria) this;
        }

        public Criteria andLiveMatchSellStatusNotLike(String value) {
            addCriterion("live_match_sell_status not like", value, "liveMatchSellStatus");
            return (Criteria) this;
        }

        public Criteria andLiveMatchSellStatusIn(List<String> values) {
            addCriterion("live_match_sell_status in", values, "liveMatchSellStatus");
            return (Criteria) this;
        }

        public Criteria andLiveMatchSellStatusNotIn(List<String> values) {
            addCriterion("live_match_sell_status not in", values, "liveMatchSellStatus");
            return (Criteria) this;
        }

        public Criteria andLiveMatchSellStatusBetween(String value1, String value2) {
            addCriterion("live_match_sell_status between", value1, value2, "liveMatchSellStatus");
            return (Criteria) this;
        }

        public Criteria andLiveMatchSellStatusNotBetween(String value1, String value2) {
            addCriterion("live_match_sell_status not between", value1, value2, "liveMatchSellStatus");
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

        public Criteria andStatusIsNull() {
            addCriterion("status is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("status is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(String value) {
            addCriterion("status =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(String value) {
            addCriterion("status <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(String value) {
            addCriterion("status >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(String value) {
            addCriterion("status >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(String value) {
            addCriterion("status <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(String value) {
            addCriterion("status <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLike(String value) {
            addCriterion("status like", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotLike(String value) {
            addCriterion("status not like", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<String> values) {
            addCriterion("status in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<String> values) {
            addCriterion("status not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(String value1, String value2) {
            addCriterion("status between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(String value1, String value2) {
            addCriterion("status not between", value1, value2, "status");
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

        public Criteria andMatchStatusSourceCodeIsNull() {
            addCriterion("match_status_source_code is null");
            return (Criteria) this;
        }

        public Criteria andMatchStatusSourceCodeIsNotNull() {
            addCriterion("match_status_source_code is not null");
            return (Criteria) this;
        }

        public Criteria andMatchStatusSourceCodeEqualTo(String value) {
            addCriterion("match_status_source_code =", value, "matchStatusSourceCode");
            return (Criteria) this;
        }

        public Criteria andMatchStatusSourceCodeNotEqualTo(String value) {
            addCriterion("match_status_source_code <>", value, "matchStatusSourceCode");
            return (Criteria) this;
        }

        public Criteria andMatchStatusSourceCodeGreaterThan(String value) {
            addCriterion("match_status_source_code >", value, "matchStatusSourceCode");
            return (Criteria) this;
        }

        public Criteria andMatchStatusSourceCodeGreaterThanOrEqualTo(String value) {
            addCriterion("match_status_source_code >=", value, "matchStatusSourceCode");
            return (Criteria) this;
        }

        public Criteria andMatchStatusSourceCodeLessThan(String value) {
            addCriterion("match_status_source_code <", value, "matchStatusSourceCode");
            return (Criteria) this;
        }

        public Criteria andMatchStatusSourceCodeLessThanOrEqualTo(String value) {
            addCriterion("match_status_source_code <=", value, "matchStatusSourceCode");
            return (Criteria) this;
        }

        public Criteria andMatchStatusSourceCodeLike(String value) {
            addCriterion("match_status_source_code like", value, "matchStatusSourceCode");
            return (Criteria) this;
        }

        public Criteria andMatchStatusSourceCodeNotLike(String value) {
            addCriterion("match_status_source_code not like", value, "matchStatusSourceCode");
            return (Criteria) this;
        }

        public Criteria andMatchStatusSourceCodeIn(List<String> values) {
            addCriterion("match_status_source_code in", values, "matchStatusSourceCode");
            return (Criteria) this;
        }

        public Criteria andMatchStatusSourceCodeNotIn(List<String> values) {
            addCriterion("match_status_source_code not in", values, "matchStatusSourceCode");
            return (Criteria) this;
        }

        public Criteria andMatchStatusSourceCodeBetween(String value1, String value2) {
            addCriterion("match_status_source_code between", value1, value2, "matchStatusSourceCode");
            return (Criteria) this;
        }

        public Criteria andMatchStatusSourceCodeNotBetween(String value1, String value2) {
            addCriterion("match_status_source_code not between", value1, value2, "matchStatusSourceCode");
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

        public Criteria andLabelEqualTo(Integer value) {
            addCriterion("label =", value, "label");
            return (Criteria) this;
        }

        public Criteria andLabelNotEqualTo(Integer value) {
            addCriterion("label <>", value, "label");
            return (Criteria) this;
        }

        public Criteria andLabelGreaterThan(Integer value) {
            addCriterion("label >", value, "label");
            return (Criteria) this;
        }

        public Criteria andLabelGreaterThanOrEqualTo(Integer value) {
            addCriterion("label >=", value, "label");
            return (Criteria) this;
        }

        public Criteria andLabelLessThan(Integer value) {
            addCriterion("label <", value, "label");
            return (Criteria) this;
        }

        public Criteria andLabelLessThanOrEqualTo(Integer value) {
            addCriterion("label <=", value, "label");
            return (Criteria) this;
        }

        public Criteria andLabelIn(List<Integer> values) {
            addCriterion("label in", values, "label");
            return (Criteria) this;
        }

        public Criteria andLabelNotIn(List<Integer> values) {
            addCriterion("label not in", values, "label");
            return (Criteria) this;
        }

        public Criteria andLabelBetween(Integer value1, Integer value2) {
            addCriterion("label between", value1, value2, "label");
            return (Criteria) this;
        }

        public Criteria andLabelNotBetween(Integer value1, Integer value2) {
            addCriterion("label not between", value1, value2, "label");
            return (Criteria) this;
        }

        public Criteria andSettlementTimeIsNull() {
            addCriterion("settlement_time is null");
            return (Criteria) this;
        }

        public Criteria andSettlementTimeIsNotNull() {
            addCriterion("settlement_time is not null");
            return (Criteria) this;
        }

        public Criteria andSettlementTimeEqualTo(Long value) {
            addCriterion("settlement_time =", value, "settlementTime");
            return (Criteria) this;
        }
        public Criteria andShowResultStatusEqualTo(Integer value) {
            addCriterion("show_result_status =", value, "showResultStatus");
            return (Criteria) this;
        }
        public Criteria andSettlementTimeNotEqualTo(Long value) {
            addCriterion("settlement_time <>", value, "settlementTime");
            return (Criteria) this;
        }

        public Criteria andSettlementTimeGreaterThan(Long value) {
            addCriterion("settlement_time >", value, "settlementTime");
            return (Criteria) this;
        }

        public Criteria andSettlementTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("settlement_time >=", value, "settlementTime");
            return (Criteria) this;
        }

        public Criteria andSettlementTimeLessThan(Long value) {
            addCriterion("settlement_time <", value, "settlementTime");
            return (Criteria) this;
        }

        public Criteria andSettlementTimeLessThanOrEqualTo(Long value) {
            addCriterion("settlement_time <=", value, "settlementTime");
            return (Criteria) this;
        }

        public Criteria andSettlementTimeIn(List<Long> values) {
            addCriterion("settlement_time in", values, "settlementTime");
            return (Criteria) this;
        }

        public Criteria andSettlementTimeNotIn(List<Long> values) {
            addCriterion("settlement_time not in", values, "settlementTime");
            return (Criteria) this;
        }

        public Criteria andSettlementTimeBetween(Long value1, Long value2) {
            addCriterion("settlement_time between", value1, value2, "settlementTime");
            return (Criteria) this;
        }

        public Criteria andSettlementTimeNotBetween(Long value1, Long value2) {
            addCriterion("settlement_time not between", value1, value2, "settlementTime");
            return (Criteria) this;
        }

        public Criteria andPreUsedOddsCodesIsNull() {
            addCriterion("pre_used_odds_codes is null");
            return (Criteria) this;
        }

        public Criteria andPreUsedOddsCodesIsNotNull() {
            addCriterion("pre_used_odds_codes is not null");
            return (Criteria) this;
        }

        public Criteria andPreUsedOddsCodesEqualTo(String value) {
            addCriterion("pre_used_odds_codes =", value, "preUsedOddsCodes");
            return (Criteria) this;
        }

        public Criteria andPreUsedOddsCodesNotEqualTo(String value) {
            addCriterion("pre_used_odds_codes <>", value, "preUsedOddsCodes");
            return (Criteria) this;
        }

        public Criteria andPreUsedOddsCodesGreaterThan(String value) {
            addCriterion("pre_used_odds_codes >", value, "preUsedOddsCodes");
            return (Criteria) this;
        }

        public Criteria andPreUsedOddsCodesGreaterThanOrEqualTo(String value) {
            addCriterion("pre_used_odds_codes >=", value, "preUsedOddsCodes");
            return (Criteria) this;
        }

        public Criteria andPreUsedOddsCodesLessThan(String value) {
            addCriterion("pre_used_odds_codes <", value, "preUsedOddsCodes");
            return (Criteria) this;
        }

        public Criteria andPreUsedOddsCodesLessThanOrEqualTo(String value) {
            addCriterion("pre_used_odds_codes <=", value, "preUsedOddsCodes");
            return (Criteria) this;
        }

        public Criteria andPreUsedOddsCodesLike(String value) {
            addCriterion("pre_used_odds_codes like", value, "preUsedOddsCodes");
            return (Criteria) this;
        }

        public Criteria andPreUsedOddsCodesNotLike(String value) {
            addCriterion("pre_used_odds_codes not like", value, "preUsedOddsCodes");
            return (Criteria) this;
        }

        public Criteria andPreUsedOddsCodesIn(List<String> values) {
            addCriterion("pre_used_odds_codes in", values, "preUsedOddsCodes");
            return (Criteria) this;
        }

        public Criteria andPreUsedOddsCodesNotIn(List<String> values) {
            addCriterion("pre_used_odds_codes not in", values, "preUsedOddsCodes");
            return (Criteria) this;
        }

        public Criteria andPreUsedOddsCodesBetween(String value1, String value2) {
            addCriterion("pre_used_odds_codes between", value1, value2, "preUsedOddsCodes");
            return (Criteria) this;
        }

        public Criteria andPreUsedOddsCodesNotBetween(String value1, String value2) {
            addCriterion("pre_used_odds_codes not between", value1, value2, "preUsedOddsCodes");
            return (Criteria) this;
        }

        public Criteria andLiveUsedOddsCodesIsNull() {
            addCriterion("live_used_odds_codes is null");
            return (Criteria) this;
        }

        public Criteria andLiveUsedOddsCodesIsNotNull() {
            addCriterion("live_used_odds_codes is not null");
            return (Criteria) this;
        }

        public Criteria andLiveUsedOddsCodesEqualTo(String value) {
            addCriterion("live_used_odds_codes =", value, "liveUsedOddsCodes");
            return (Criteria) this;
        }

        public Criteria andLiveUsedOddsCodesNotEqualTo(String value) {
            addCriterion("live_used_odds_codes <>", value, "liveUsedOddsCodes");
            return (Criteria) this;
        }

        public Criteria andLiveUsedOddsCodesGreaterThan(String value) {
            addCriterion("live_used_odds_codes >", value, "liveUsedOddsCodes");
            return (Criteria) this;
        }

        public Criteria andLiveUsedOddsCodesGreaterThanOrEqualTo(String value) {
            addCriterion("live_used_odds_codes >=", value, "liveUsedOddsCodes");
            return (Criteria) this;
        }

        public Criteria andLiveUsedOddsCodesLessThan(String value) {
            addCriterion("live_used_odds_codes <", value, "liveUsedOddsCodes");
            return (Criteria) this;
        }

        public Criteria andLiveUsedOddsCodesLessThanOrEqualTo(String value) {
            addCriterion("live_used_odds_codes <=", value, "liveUsedOddsCodes");
            return (Criteria) this;
        }

        public Criteria andLiveUsedOddsCodesLike(String value) {
            addCriterion("live_used_odds_codes like", value, "liveUsedOddsCodes");
            return (Criteria) this;
        }

        public Criteria andLiveUsedOddsCodesNotLike(String value) {
            addCriterion("live_used_odds_codes not like", value, "liveUsedOddsCodes");
            return (Criteria) this;
        }

        public Criteria andLiveUsedOddsCodesIn(List<String> values) {
            addCriterion("live_used_odds_codes in", values, "liveUsedOddsCodes");
            return (Criteria) this;
        }

        public Criteria andLiveUsedOddsCodesNotIn(List<String> values) {
            addCriterion("live_used_odds_codes not in", values, "liveUsedOddsCodes");
            return (Criteria) this;
        }

        public Criteria andLiveUsedOddsCodesBetween(String value1, String value2) {
            addCriterion("live_used_odds_codes between", value1, value2, "liveUsedOddsCodes");
            return (Criteria) this;
        }

        public Criteria andLiveUsedOddsCodesNotBetween(String value1, String value2) {
            addCriterion("live_used_odds_codes not between", value1, value2, "liveUsedOddsCodes");
            return (Criteria) this;
        }

        public Criteria andSellBeginDifferIsNull() {
            addCriterion("sell_begin_differ is null");
            return (Criteria) this;
        }

        public Criteria andSellBeginDifferIsNotNull() {
            addCriterion("sell_begin_differ is not null");
            return (Criteria) this;
        }

        public Criteria andSellBeginDifferEqualTo(Long value) {
            addCriterion("sell_begin_differ =", value, "sellBeginDiffer");
            return (Criteria) this;
        }

        public Criteria andSellBeginDifferNotEqualTo(Long value) {
            addCriterion("sell_begin_differ <>", value, "sellBeginDiffer");
            return (Criteria) this;
        }

        public Criteria andSellBeginDifferGreaterThan(Long value) {
            addCriterion("sell_begin_differ >", value, "sellBeginDiffer");
            return (Criteria) this;
        }

        public Criteria andSellBeginDifferGreaterThanOrEqualTo(Long value) {
            addCriterion("sell_begin_differ >=", value, "sellBeginDiffer");
            return (Criteria) this;
        }

        public Criteria andSellBeginDifferLessThan(Long value) {
            addCriterion("sell_begin_differ <", value, "sellBeginDiffer");
            return (Criteria) this;
        }

        public Criteria andSellBeginDifferLessThanOrEqualTo(Long value) {
            addCriterion("sell_begin_differ <=", value, "sellBeginDiffer");
            return (Criteria) this;
        }

        public Criteria andSellBeginDifferIn(List<Long> values) {
            addCriterion("sell_begin_differ in", values, "sellBeginDiffer");
            return (Criteria) this;
        }

        public Criteria andSellBeginDifferNotIn(List<Long> values) {
            addCriterion("sell_begin_differ not in", values, "sellBeginDiffer");
            return (Criteria) this;
        }

        public Criteria andSellBeginDifferBetween(Long value1, Long value2) {
            addCriterion("sell_begin_differ between", value1, value2, "sellBeginDiffer");
            return (Criteria) this;
        }

        public Criteria andSellBeginDifferNotBetween(Long value1, Long value2) {
            addCriterion("sell_begin_differ not between", value1, value2, "sellBeginDiffer");
            return (Criteria) this;
        }

        public Criteria andVideoCodeIsNull() {
            addCriterion("video_code is null");
            return (Criteria) this;
        }

        public Criteria andVideoCodeIsNotNull() {
            addCriterion("video_code is not null");
            return (Criteria) this;
        }

        public Criteria andVideoCodeEqualTo(String value) {
            addCriterion("video_code =", value, "videoCode");
            return (Criteria) this;
        }

        public Criteria andVideoCodeNotEqualTo(String value) {
            addCriterion("video_code <>", value, "videoCode");
            return (Criteria) this;
        }

        public Criteria andVideoCodeGreaterThan(String value) {
            addCriterion("video_code >", value, "videoCode");
            return (Criteria) this;
        }

        public Criteria andVideoCodeGreaterThanOrEqualTo(String value) {
            addCriterion("video_code >=", value, "videoCode");
            return (Criteria) this;
        }

        public Criteria andVideoCodeLessThan(String value) {
            addCriterion("video_code <", value, "videoCode");
            return (Criteria) this;
        }

        public Criteria andVideoCodeLessThanOrEqualTo(String value) {
            addCriterion("video_code <=", value, "videoCode");
            return (Criteria) this;
        }

        public Criteria andVideoCodeLike(String value) {
            addCriterion("video_code like", value, "videoCode");
            return (Criteria) this;
        }

        public Criteria andVideoCodeNotLike(String value) {
            addCriterion("video_code not like", value, "videoCode");
            return (Criteria) this;
        }

        public Criteria andVideoCodeIn(List<String> values) {
            addCriterion("video_code in", values, "videoCode");
            return (Criteria) this;
        }

        public Criteria andVideoCodeNotIn(List<String> values) {
            addCriterion("video_code not in", values, "videoCode");
            return (Criteria) this;
        }

        public Criteria andVideoCodeBetween(String value1, String value2) {
            addCriterion("video_code between", value1, value2, "videoCode");
            return (Criteria) this;
        }

        public Criteria andVideoCodeNotBetween(String value1, String value2) {
            addCriterion("video_code not between", value1, value2, "videoCode");
            return (Criteria) this;
        }

        public Criteria andAnimationCodeIsNull() {
            addCriterion("animation_code is null");
            return (Criteria) this;
        }

        public Criteria andAnimationCodeIsNotNull() {
            addCriterion("animation_code is not null");
            return (Criteria) this;
        }

        public Criteria andAnimationCodeEqualTo(String value) {
            addCriterion("animation_code =", value, "animationCode");
            return (Criteria) this;
        }

        public Criteria andAnimationCodeNotEqualTo(String value) {
            addCriterion("animation_code <>", value, "animationCode");
            return (Criteria) this;
        }

        public Criteria andAnimationCodeGreaterThan(String value) {
            addCriterion("animation_code >", value, "animationCode");
            return (Criteria) this;
        }

        public Criteria andAnimationCodeGreaterThanOrEqualTo(String value) {
            addCriterion("animation_code >=", value, "animationCode");
            return (Criteria) this;
        }

        public Criteria andAnimationCodeLessThan(String value) {
            addCriterion("animation_code <", value, "animationCode");
            return (Criteria) this;
        }

        public Criteria andAnimationCodeLessThanOrEqualTo(String value) {
            addCriterion("animation_code <=", value, "animationCode");
            return (Criteria) this;
        }

        public Criteria andAnimationCodeLike(String value) {
            addCriterion("animation_code like", value, "animationCode");
            return (Criteria) this;
        }

        public Criteria andAnimationCodeNotLike(String value) {
            addCriterion("animation_code not like", value, "animationCode");
            return (Criteria) this;
        }

        public Criteria andAnimationCodeIn(List<String> values) {
            addCriterion("animation_code in", values, "animationCode");
            return (Criteria) this;
        }

        public Criteria andAnimationCodeNotIn(List<String> values) {
            addCriterion("animation_code not in", values, "animationCode");
            return (Criteria) this;
        }

        public Criteria andAnimationCodeBetween(String value1, String value2) {
            addCriterion("animation_code between", value1, value2, "animationCode");
            return (Criteria) this;
        }

        public Criteria andAnimationCodeNotBetween(String value1, String value2) {
            addCriterion("animation_code not between", value1, value2, "animationCode");
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