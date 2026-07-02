package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class MatchDataSourceWeightExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MatchDataSourceWeightExample() {
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

        public Criteria andMarketTypeIsNull() {
            addCriterion("market_type is null");
            return (Criteria) this;
        }

        public Criteria andMarketTypeIsNotNull() {
            addCriterion("market_type is not null");
            return (Criteria) this;
        }

        public Criteria andMarketTypeEqualTo(String value) {
            addCriterion("market_type =", value, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeNotEqualTo(String value) {
            addCriterion("market_type <>", value, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeGreaterThan(String value) {
            addCriterion("market_type >", value, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeGreaterThanOrEqualTo(String value) {
            addCriterion("market_type >=", value, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeLessThan(String value) {
            addCriterion("market_type <", value, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeLessThanOrEqualTo(String value) {
            addCriterion("market_type <=", value, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeLike(String value) {
            addCriterion("market_type like", value, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeNotLike(String value) {
            addCriterion("market_type not like", value, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeIn(List<String> values) {
            addCriterion("market_type in", values, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeNotIn(List<String> values) {
            addCriterion("market_type not in", values, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeBetween(String value1, String value2) {
            addCriterion("market_type between", value1, value2, "marketType");
            return (Criteria) this;
        }

        public Criteria andMarketTypeNotBetween(String value1, String value2) {
            addCriterion("market_type not between", value1, value2, "marketType");
            return (Criteria) this;
        }

        public Criteria andSrWeightIsNull() {
            addCriterion("sr_weight is null");
            return (Criteria) this;
        }

        public Criteria andSrWeightIsNotNull() {
            addCriterion("sr_weight is not null");
            return (Criteria) this;
        }

        public Criteria andSrWeightEqualTo(Integer value) {
            addCriterion("sr_weight =", value, "srWeight");
            return (Criteria) this;
        }

        public Criteria andSrWeightNotEqualTo(Integer value) {
            addCriterion("sr_weight <>", value, "srWeight");
            return (Criteria) this;
        }

        public Criteria andSrWeightGreaterThan(Integer value) {
            addCriterion("sr_weight >", value, "srWeight");
            return (Criteria) this;
        }

        public Criteria andSrWeightGreaterThanOrEqualTo(Integer value) {
            addCriterion("sr_weight >=", value, "srWeight");
            return (Criteria) this;
        }

        public Criteria andSrWeightLessThan(Integer value) {
            addCriterion("sr_weight <", value, "srWeight");
            return (Criteria) this;
        }

        public Criteria andSrWeightLessThanOrEqualTo(Integer value) {
            addCriterion("sr_weight <=", value, "srWeight");
            return (Criteria) this;
        }

        public Criteria andSrWeightIn(List<Integer> values) {
            addCriterion("sr_weight in", values, "srWeight");
            return (Criteria) this;
        }

        public Criteria andSrWeightNotIn(List<Integer> values) {
            addCriterion("sr_weight not in", values, "srWeight");
            return (Criteria) this;
        }

        public Criteria andSrWeightBetween(Integer value1, Integer value2) {
            addCriterion("sr_weight between", value1, value2, "srWeight");
            return (Criteria) this;
        }

        public Criteria andSrWeightNotBetween(Integer value1, Integer value2) {
            addCriterion("sr_weight not between", value1, value2, "srWeight");
            return (Criteria) this;
        }

        public Criteria andBcWeightIsNull() {
            addCriterion("bc_weight is null");
            return (Criteria) this;
        }

        public Criteria andBcWeightIsNotNull() {
            addCriterion("bc_weight is not null");
            return (Criteria) this;
        }

        public Criteria andBcWeightEqualTo(Integer value) {
            addCriterion("bc_weight =", value, "bcWeight");
            return (Criteria) this;
        }

        public Criteria andBcWeightNotEqualTo(Integer value) {
            addCriterion("bc_weight <>", value, "bcWeight");
            return (Criteria) this;
        }

        public Criteria andBcWeightGreaterThan(Integer value) {
            addCriterion("bc_weight >", value, "bcWeight");
            return (Criteria) this;
        }

        public Criteria andBcWeightGreaterThanOrEqualTo(Integer value) {
            addCriterion("bc_weight >=", value, "bcWeight");
            return (Criteria) this;
        }

        public Criteria andBcWeightLessThan(Integer value) {
            addCriterion("bc_weight <", value, "bcWeight");
            return (Criteria) this;
        }

        public Criteria andBcWeightLessThanOrEqualTo(Integer value) {
            addCriterion("bc_weight <=", value, "bcWeight");
            return (Criteria) this;
        }

        public Criteria andBcWeightIn(List<Integer> values) {
            addCriterion("bc_weight in", values, "bcWeight");
            return (Criteria) this;
        }

        public Criteria andBcWeightNotIn(List<Integer> values) {
            addCriterion("bc_weight not in", values, "bcWeight");
            return (Criteria) this;
        }

        public Criteria andBcWeightBetween(Integer value1, Integer value2) {
            addCriterion("bc_weight between", value1, value2, "bcWeight");
            return (Criteria) this;
        }

        public Criteria andBcWeightNotBetween(Integer value1, Integer value2) {
            addCriterion("bc_weight not between", value1, value2, "bcWeight");
            return (Criteria) this;
        }

        public Criteria andBgWeightIsNull() {
            addCriterion("bg_weight is null");
            return (Criteria) this;
        }

        public Criteria andBgWeightIsNotNull() {
            addCriterion("bg_weight is not null");
            return (Criteria) this;
        }

        public Criteria andBgWeightEqualTo(Integer value) {
            addCriterion("bg_weight =", value, "bgWeight");
            return (Criteria) this;
        }

        public Criteria andBgWeightNotEqualTo(Integer value) {
            addCriterion("bg_weight <>", value, "bgWeight");
            return (Criteria) this;
        }

        public Criteria andBgWeightGreaterThan(Integer value) {
            addCriterion("bg_weight >", value, "bgWeight");
            return (Criteria) this;
        }

        public Criteria andBgWeightGreaterThanOrEqualTo(Integer value) {
            addCriterion("bg_weight >=", value, "bgWeight");
            return (Criteria) this;
        }

        public Criteria andBgWeightLessThan(Integer value) {
            addCriterion("bg_weight <", value, "bgWeight");
            return (Criteria) this;
        }

        public Criteria andBgWeightLessThanOrEqualTo(Integer value) {
            addCriterion("bg_weight <=", value, "bgWeight");
            return (Criteria) this;
        }

        public Criteria andBgWeightIn(List<Integer> values) {
            addCriterion("bg_weight in", values, "bgWeight");
            return (Criteria) this;
        }

        public Criteria andBgWeightNotIn(List<Integer> values) {
            addCriterion("bg_weight not in", values, "bgWeight");
            return (Criteria) this;
        }

        public Criteria andBgWeightBetween(Integer value1, Integer value2) {
            addCriterion("bg_weight between", value1, value2, "bgWeight");
            return (Criteria) this;
        }

        public Criteria andBgWeightNotBetween(Integer value1, Integer value2) {
            addCriterion("bg_weight not between", value1, value2, "bgWeight");
            return (Criteria) this;
        }

        public Criteria andPdWeightIsNull() {
            addCriterion("pd_weight is null");
            return (Criteria) this;
        }

        public Criteria andPdWeightIsNotNull() {
            addCriterion("pd_weight is not null");
            return (Criteria) this;
        }

        public Criteria andPdWeightEqualTo(Integer value) {
            addCriterion("pd_weight =", value, "pdWeight");
            return (Criteria) this;
        }

        public Criteria andPdWeightNotEqualTo(Integer value) {
            addCriterion("pd_weight <>", value, "pdWeight");
            return (Criteria) this;
        }

        public Criteria andPdWeightGreaterThan(Integer value) {
            addCriterion("pd_weight >", value, "pdWeight");
            return (Criteria) this;
        }

        public Criteria andPdWeightGreaterThanOrEqualTo(Integer value) {
            addCriterion("pd_weight >=", value, "pdWeight");
            return (Criteria) this;
        }

        public Criteria andPdWeightLessThan(Integer value) {
            addCriterion("pd_weight <", value, "pdWeight");
            return (Criteria) this;
        }

        public Criteria andPdWeightLessThanOrEqualTo(Integer value) {
            addCriterion("pd_weight <=", value, "pdWeight");
            return (Criteria) this;
        }

        public Criteria andPdWeightIn(List<Integer> values) {
            addCriterion("pd_weight in", values, "pdWeight");
            return (Criteria) this;
        }

        public Criteria andPdWeightNotIn(List<Integer> values) {
            addCriterion("pd_weight not in", values, "pdWeight");
            return (Criteria) this;
        }

        public Criteria andPdWeightBetween(Integer value1, Integer value2) {
            addCriterion("pd_weight between", value1, value2, "pdWeight");
            return (Criteria) this;
        }

        public Criteria andPdWeightNotBetween(Integer value1, Integer value2) {
            addCriterion("pd_weight not between", value1, value2, "pdWeight");
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

        public Criteria andOperaterIdIsNull() {
            addCriterion("operater_id is null");
            return (Criteria) this;
        }

        public Criteria andOperaterIdIsNotNull() {
            addCriterion("operater_id is not null");
            return (Criteria) this;
        }

        public Criteria andOperaterIdEqualTo(Long value) {
            addCriterion("operater_id =", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdNotEqualTo(Long value) {
            addCriterion("operater_id <>", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdGreaterThan(Long value) {
            addCriterion("operater_id >", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdGreaterThanOrEqualTo(Long value) {
            addCriterion("operater_id >=", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdLessThan(Long value) {
            addCriterion("operater_id <", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdLessThanOrEqualTo(Long value) {
            addCriterion("operater_id <=", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdIn(List<Long> values) {
            addCriterion("operater_id in", values, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdNotIn(List<Long> values) {
            addCriterion("operater_id not in", values, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdBetween(Long value1, Long value2) {
            addCriterion("operater_id between", value1, value2, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdNotBetween(Long value1, Long value2) {
            addCriterion("operater_id not between", value1, value2, "operaterId");
            return (Criteria) this;
        }

        public Criteria andTxWeightIsNull() {
            addCriterion("tx_weight is null");
            return (Criteria) this;
        }

        public Criteria andTxWeightIsNotNull() {
            addCriterion("tx_weight is not null");
            return (Criteria) this;
        }

        public Criteria andTxWeightEqualTo(Integer value) {
            addCriterion("tx_weight =", value, "txWeight");
            return (Criteria) this;
        }

        public Criteria andTxWeightNotEqualTo(Integer value) {
            addCriterion("tx_weight <>", value, "txWeight");
            return (Criteria) this;
        }

        public Criteria andTxWeightGreaterThan(Integer value) {
            addCriterion("tx_weight >", value, "txWeight");
            return (Criteria) this;
        }

        public Criteria andTxWeightGreaterThanOrEqualTo(Integer value) {
            addCriterion("tx_weight >=", value, "txWeight");
            return (Criteria) this;
        }

        public Criteria andTxWeightLessThan(Integer value) {
            addCriterion("tx_weight <", value, "txWeight");
            return (Criteria) this;
        }

        public Criteria andTxWeightLessThanOrEqualTo(Integer value) {
            addCriterion("tx_weight <=", value, "txWeight");
            return (Criteria) this;
        }

        public Criteria andTxWeightIn(List<Integer> values) {
            addCriterion("tx_weight in", values, "txWeight");
            return (Criteria) this;
        }

        public Criteria andTxWeightNotIn(List<Integer> values) {
            addCriterion("tx_weight not in", values, "txWeight");
            return (Criteria) this;
        }

        public Criteria andTxWeightBetween(Integer value1, Integer value2) {
            addCriterion("tx_weight between", value1, value2, "txWeight");
            return (Criteria) this;
        }

        public Criteria andTxWeightNotBetween(Integer value1, Integer value2) {
            addCriterion("tx_weight not between", value1, value2, "txWeight");
            return (Criteria) this;
        }

        public Criteria andRbWeightIsNull() {
            addCriterion("rb_weight is null");
            return (Criteria) this;
        }

        public Criteria andRbWeightIsNotNull() {
            addCriterion("rb_weight is not null");
            return (Criteria) this;
        }

        public Criteria andRbWeightEqualTo(Integer value) {
            addCriterion("rb_weight =", value, "rbWeight");
            return (Criteria) this;
        }

        public Criteria andRbWeightNotEqualTo(Integer value) {
            addCriterion("rb_weight <>", value, "rbWeight");
            return (Criteria) this;
        }

        public Criteria andRbWeightGreaterThan(Integer value) {
            addCriterion("rb_weight >", value, "rbWeight");
            return (Criteria) this;
        }

        public Criteria andRbWeightGreaterThanOrEqualTo(Integer value) {
            addCriterion("rb_weight >=", value, "rbWeight");
            return (Criteria) this;
        }

        public Criteria andRbWeightLessThan(Integer value) {
            addCriterion("rb_weight <", value, "rbWeight");
            return (Criteria) this;
        }

        public Criteria andRbWeightLessThanOrEqualTo(Integer value) {
            addCriterion("rb_weight <=", value, "rbWeight");
            return (Criteria) this;
        }

        public Criteria andRbWeightIn(List<Integer> values) {
            addCriterion("rb_weight in", values, "rbWeight");
            return (Criteria) this;
        }

        public Criteria andRbWeightNotIn(List<Integer> values) {
            addCriterion("rb_weight not in", values, "rbWeight");
            return (Criteria) this;
        }

        public Criteria andRbWeightBetween(Integer value1, Integer value2) {
            addCriterion("rb_weight between", value1, value2, "rbWeight");
            return (Criteria) this;
        }

        public Criteria andRbWeightNotBetween(Integer value1, Integer value2) {
            addCriterion("rb_weight not between", value1, value2, "rbWeight");
            return (Criteria) this;
        }

        public Criteria andAoWeightIsNull() {
            addCriterion("ao_weight is null");
            return (Criteria) this;
        }

        public Criteria andAoWeightIsNotNull() {
            addCriterion("ao_weight is not null");
            return (Criteria) this;
        }

        public Criteria andAoWeightEqualTo(Integer value) {
            addCriterion("ao_weight =", value, "aoWeight");
            return (Criteria) this;
        }

        public Criteria andAoWeightNotEqualTo(Integer value) {
            addCriterion("ao_weight <>", value, "aoWeight");
            return (Criteria) this;
        }

        public Criteria andAoWeightGreaterThan(Integer value) {
            addCriterion("ao_weight >", value, "aoWeight");
            return (Criteria) this;
        }

        public Criteria andAoWeightGreaterThanOrEqualTo(Integer value) {
            addCriterion("ao_weight >=", value, "aoWeight");
            return (Criteria) this;
        }

        public Criteria andAoWeightLessThan(Integer value) {
            addCriterion("ao_weight <", value, "aoWeight");
            return (Criteria) this;
        }

        public Criteria andAoWeightLessThanOrEqualTo(Integer value) {
            addCriterion("ao_weight <=", value, "aoWeight");
            return (Criteria) this;
        }

        public Criteria andAoWeightIn(List<Integer> values) {
            addCriterion("ao_weight in", values, "aoWeight");
            return (Criteria) this;
        }

        public Criteria andAoWeightNotIn(List<Integer> values) {
            addCriterion("ao_weight not in", values, "aoWeight");
            return (Criteria) this;
        }

        public Criteria andAoWeightBetween(Integer value1, Integer value2) {
            addCriterion("ao_weight between", value1, value2, "aoWeight");
            return (Criteria) this;
        }

        public Criteria andAoWeightNotBetween(Integer value1, Integer value2) {
            addCriterion("ao_weight not between", value1, value2, "aoWeight");
            return (Criteria) this;
        }

        public Criteria andPiWeightIsNull() {
            addCriterion("pi_weight is null");
            return (Criteria) this;
        }

        public Criteria andPiWeightIsNotNull() {
            addCriterion("pi_weight is not null");
            return (Criteria) this;
        }

        public Criteria andPiWeightEqualTo(Integer value) {
            addCriterion("pi_weight =", value, "piWeight");
            return (Criteria) this;
        }

        public Criteria andPiWeightNotEqualTo(Integer value) {
            addCriterion("pi_weight <>", value, "piWeight");
            return (Criteria) this;
        }

        public Criteria andPiWeightGreaterThan(Integer value) {
            addCriterion("pi_weight >", value, "piWeight");
            return (Criteria) this;
        }

        public Criteria andPiWeightGreaterThanOrEqualTo(Integer value) {
            addCriterion("pi_weight >=", value, "piWeight");
            return (Criteria) this;
        }

        public Criteria andPiWeightLessThan(Integer value) {
            addCriterion("pi_weight <", value, "piWeight");
            return (Criteria) this;
        }

        public Criteria andPiWeightLessThanOrEqualTo(Integer value) {
            addCriterion("pi_weight <=", value, "piWeight");
            return (Criteria) this;
        }

        public Criteria andPiWeightIn(List<Integer> values) {
            addCriterion("pi_weight in", values, "piWeight");
            return (Criteria) this;
        }

        public Criteria andPiWeightNotIn(List<Integer> values) {
            addCriterion("pi_weight not in", values, "piWeight");
            return (Criteria) this;
        }

        public Criteria andPiWeightBetween(Integer value1, Integer value2) {
            addCriterion("pi_weight between", value1, value2, "piWeight");
            return (Criteria) this;
        }

        public Criteria andPiWeightNotBetween(Integer value1, Integer value2) {
            addCriterion("pi_weight not between", value1, value2, "piWeight");
            return (Criteria) this;
        }

        public Criteria andLsWeightIsNull() {
            addCriterion("ls_weight is null");
            return (Criteria) this;
        }

        public Criteria andLsWeightIsNotNull() {
            addCriterion("ls_weight is not null");
            return (Criteria) this;
        }

        public Criteria andLsWeightEqualTo(Integer value) {
            addCriterion("ls_weight =", value, "lsWeight");
            return (Criteria) this;
        }

        public Criteria andLsWeightNotEqualTo(Integer value) {
            addCriterion("ls_weight <>", value, "lsWeight");
            return (Criteria) this;
        }

        public Criteria andLsWeightGreaterThan(Integer value) {
            addCriterion("ls_weight >", value, "lsWeight");
            return (Criteria) this;
        }

        public Criteria andLsWeightGreaterThanOrEqualTo(Integer value) {
            addCriterion("ls_weight >=", value, "lsWeight");
            return (Criteria) this;
        }

        public Criteria andLsWeightLessThan(Integer value) {
            addCriterion("ls_weight <", value, "lsWeight");
            return (Criteria) this;
        }

        public Criteria andLsWeightLessThanOrEqualTo(Integer value) {
            addCriterion("ls_weight <=", value, "lsWeight");
            return (Criteria) this;
        }

        public Criteria andLsWeightIn(List<Integer> values) {
            addCriterion("ls_weight in", values, "lsWeight");
            return (Criteria) this;
        }

        public Criteria andLsWeightNotIn(List<Integer> values) {
            addCriterion("ls_weight not in", values, "lsWeight");
            return (Criteria) this;
        }

        public Criteria andLsWeightBetween(Integer value1, Integer value2) {
            addCriterion("ls_weight between", value1, value2, "lsWeight");
            return (Criteria) this;
        }

        public Criteria andLsWeightNotBetween(Integer value1, Integer value2) {
            addCriterion("ls_weight not between", value1, value2, "lsWeight");
            return (Criteria) this;
        }

        public Criteria andBtWeightIsNull() {
            addCriterion("bt_weight is null");
            return (Criteria) this;
        }

        public Criteria andBtWeightIsNotNull() {
            addCriterion("bt_weight is not null");
            return (Criteria) this;
        }

        public Criteria andBtWeightEqualTo(Integer value) {
            addCriterion("bt_weight =", value, "btWeight");
            return (Criteria) this;
        }

        public Criteria andBtWeightNotEqualTo(Integer value) {
            addCriterion("bt_weight <>", value, "btWeight");
            return (Criteria) this;
        }

        public Criteria andBtWeightGreaterThan(Integer value) {
            addCriterion("bt_weight >", value, "btWeight");
            return (Criteria) this;
        }

        public Criteria andBtWeightGreaterThanOrEqualTo(Integer value) {
            addCriterion("bt_weight >=", value, "btWeight");
            return (Criteria) this;
        }

        public Criteria andBtWeightLessThan(Integer value) {
            addCriterion("bt_weight <", value, "btWeight");
            return (Criteria) this;
        }

        public Criteria andBtWeightLessThanOrEqualTo(Integer value) {
            addCriterion("bt_weight <=", value, "btWeight");
            return (Criteria) this;
        }

        public Criteria andBtWeightIn(List<Integer> values) {
            addCriterion("bt_weight in", values, "btWeight");
            return (Criteria) this;
        }

        public Criteria andBtWeightNotIn(List<Integer> values) {
            addCriterion("bt_weight not in", values, "btWeight");
            return (Criteria) this;
        }

        public Criteria andBtWeightBetween(Integer value1, Integer value2) {
            addCriterion("bt_weight between", value1, value2, "btWeight");
            return (Criteria) this;
        }

        public Criteria andBtWeightNotBetween(Integer value1, Integer value2) {
            addCriterion("bt_weight not between", value1, value2, "btWeight");
            return (Criteria) this;
        }





        public Criteria andBeWeightIsNull() {
            addCriterion("be_weight is null");
            return (Criteria) this;
        }

        public Criteria andBeWeightIsNotNull() {
            addCriterion("be_weight is not null");
            return (Criteria) this;
        }

        public Criteria andBeWeightEqualTo(Integer value) {
            addCriterion("be_weight =", value, "beWeight");
            return (Criteria) this;
        }

        public Criteria andBeWeightNotEqualTo(Integer value) {
            addCriterion("be_weight <>", value, "beWeight");
            return (Criteria) this;
        }

        public Criteria andBeWeightGreaterThan(Integer value) {
            addCriterion("be_weight >", value, "beWeight");
            return (Criteria) this;
        }

        public Criteria andBeWeightGreaterThanOrEqualTo(Integer value) {
            addCriterion("be_weight >=", value, "beWeight");
            return (Criteria) this;
        }

        public Criteria andBeWeightLessThan(Integer value) {
            addCriterion("be_weight <", value, "beWeight");
            return (Criteria) this;
        }

        public Criteria andBeWeightLessThanOrEqualTo(Integer value) {
            addCriterion("be_weight <=", value, "beWeight");
            return (Criteria) this;
        }

        public Criteria andBeWeightIn(List<Integer> values) {
            addCriterion("be_weight in", values, "beWeight");
            return (Criteria) this;
        }

        public Criteria andBeWeightNotIn(List<Integer> values) {
            addCriterion("be_weight not in", values, "beWeight");
            return (Criteria) this;
        }

        public Criteria andBeWeightBetween(Integer value1, Integer value2) {
            addCriterion("be_weight between", value1, value2, "beWeight");
            return (Criteria) this;
        }

        public Criteria andBeWeightNotBetween(Integer value1, Integer value2) {
            addCriterion("be_weight not between", value1, value2, "beWeight");
            return (Criteria) this;
        }

        public Criteria andKoWeightIsNull() {
            addCriterion("ko_weight is null");
            return (Criteria) this;
        }

        public Criteria andKoWeightIsNotNull() {
            addCriterion("ko_weight is not null");
            return (Criteria) this;
        }

        public Criteria andKoWeightEqualTo(Integer value) {
            addCriterion("ko_weight =", value, "koWeight");
            return (Criteria) this;
        }

        public Criteria andKoWeightNotEqualTo(Integer value) {
            addCriterion("ko_weight <>", value, "koWeight");
            return (Criteria) this;
        }

        public Criteria andKoWeightGreaterThan(Integer value) {
            addCriterion("ko_weight >", value, "koWeight");
            return (Criteria) this;
        }

        public Criteria andKoWeightGreaterThanOrEqualTo(Integer value) {
            addCriterion("ko_weight >=", value, "koWeight");
            return (Criteria) this;
        }

        public Criteria andKoWeightLessThan(Integer value) {
            addCriterion("ko_weight <", value, "koWeight");
            return (Criteria) this;
        }

        public Criteria andKoWeightLessThanOrEqualTo(Integer value) {
            addCriterion("ko_weight <=", value, "koWeight");
            return (Criteria) this;
        }

        public Criteria andKoWeightIn(List<Integer> values) {
            addCriterion("ko_weight in", values, "koWeight");
            return (Criteria) this;
        }

        public Criteria andKoWeightNotIn(List<Integer> values) {
            addCriterion("ko_weight not in", values, "koWeight");
            return (Criteria) this;
        }

        public Criteria andKoWeightBetween(Integer value1, Integer value2) {
            addCriterion("ko_weight between", value1, value2, "koWeight");
            return (Criteria) this;
        }

        public Criteria andKoWeightNotBetween(Integer value1, Integer value2) {
            addCriterion("ko_weight not between", value1, value2, "koWeight");
            return (Criteria) this;
        }

        public Criteria andOdWeightIsNull() {
            addCriterion("od_weight is null");
            return (Criteria) this;
        }

        public Criteria andOdWeightIsNotNull() {
            addCriterion("od_weight is not null");
            return (Criteria) this;
        }

        public Criteria andOdWeightEqualTo(Integer value) {
            addCriterion("od_weight =", value, "odWeight");
            return (Criteria) this;
        }

        public Criteria andOdWeightNotEqualTo(Integer value) {
            addCriterion("od_weight <>", value, "odWeight");
            return (Criteria) this;
        }

        public Criteria andOdWeightGreaterThan(Integer value) {
            addCriterion("od_weight >", value, "odWeight");
            return (Criteria) this;
        }

        public Criteria andOdWeightGreaterThanOrEqualTo(Integer value) {
            addCriterion("od_weight >=", value, "odWeight");
            return (Criteria) this;
        }

        public Criteria andOdWeightLessThan(Integer value) {
            addCriterion("od_weight <", value, "odWeight");
            return (Criteria) this;
        }

        public Criteria andOdWeightLessThanOrEqualTo(Integer value) {
            addCriterion("od_weight <=", value, "odWeight");
            return (Criteria) this;
        }

        public Criteria andOdWeightIn(List<Integer> values) {
            addCriterion("od_weight in", values, "odWeight");
            return (Criteria) this;
        }

        public Criteria andOdWeightNotIn(List<Integer> values) {
            addCriterion("od_weight not in", values, "odWeight");
            return (Criteria) this;
        }

        public Criteria andOdWeightBetween(Integer value1, Integer value2) {
            addCriterion("od_weight between", value1, value2, "odWeight");
            return (Criteria) this;
        }

        public Criteria andOdWeightNotBetween(Integer value1, Integer value2) {
            addCriterion("od_weight not between", value1, value2, "odWeight");
            return (Criteria) this;
        }

        public Criteria andN01WeightIsNull() {
            addCriterion("n01_weight is null");
            return (Criteria) this;
        }

        public Criteria andN01WeightIsNotNull() {
            addCriterion("n01_weight is not null");
            return (Criteria) this;
        }

        public Criteria andN01WeightEqualTo(Integer value) {
            addCriterion("n01_weight =", value, "n01Weight");
            return (Criteria) this;
        }

        public Criteria andN01WeightNotEqualTo(Integer value) {
            addCriterion("n01_weight <>", value, "n01Weight");
            return (Criteria) this;
        }

        public Criteria andN01WeightGreaterThan(Integer value) {
            addCriterion("n01_weight >", value, "n01Weight");
            return (Criteria) this;
        }

        public Criteria andN01WeightGreaterThanOrEqualTo(Integer value) {
            addCriterion("n01_weight >=", value, "n01Weight");
            return (Criteria) this;
        }

        public Criteria andN01WeightLessThan(Integer value) {
            addCriterion("n01_weight <", value, "n01Weight");
            return (Criteria) this;
        }

        public Criteria andN01WeightLessThanOrEqualTo(Integer value) {
            addCriterion("n01_weight <=", value, "n01Weight");
            return (Criteria) this;
        }

        public Criteria andN01WeightIn(List<Integer> values) {
            addCriterion("n01_weight in", values, "n01Weight");
            return (Criteria) this;
        }

        public Criteria andN01WeightNotIn(List<Integer> values) {
            addCriterion("n01_weight not in", values, "n01Weight");
            return (Criteria) this;
        }

        public Criteria andN01WeightBetween(Integer value1, Integer value2) {
            addCriterion("n01_weight between", value1, value2, "n01Weight");
            return (Criteria) this;
        }

        public Criteria andN01WeightNotBetween(Integer value1, Integer value2) {
            addCriterion("n01_weight not between", value1, value2, "n01Weight");
            return (Criteria) this;
        }

        public Criteria andN02WeightIsNull() {
            addCriterion("n02_weight is null");
            return (Criteria) this;
        }

        public Criteria andN02WeightIsNotNull() {
            addCriterion("n02_weight is not null");
            return (Criteria) this;
        }

        public Criteria andN02WeightEqualTo(Integer value) {
            addCriterion("n02_weight =", value, "n02Weight");
            return (Criteria) this;
        }

        public Criteria andN02WeightNotEqualTo(Integer value) {
            addCriterion("n02_weight <>", value, "n02Weight");
            return (Criteria) this;
        }

        public Criteria andN02WeightGreaterThan(Integer value) {
            addCriterion("n02_weight >", value, "n02Weight");
            return (Criteria) this;
        }

        public Criteria andN02WeightGreaterThanOrEqualTo(Integer value) {
            addCriterion("n02_weight >=", value, "n02Weight");
            return (Criteria) this;
        }

        public Criteria andN02WeightLessThan(Integer value) {
            addCriterion("n02_weight <", value, "n02Weight");
            return (Criteria) this;
        }

        public Criteria andN02WeightLessThanOrEqualTo(Integer value) {
            addCriterion("n02_weight <=", value, "n02Weight");
            return (Criteria) this;
        }

        public Criteria andN02WeightIn(List<Integer> values) {
            addCriterion("n02_weight in", values, "n02Weight");
            return (Criteria) this;
        }

        public Criteria andN02WeightNotIn(List<Integer> values) {
            addCriterion("n02_weight not in", values, "n02Weight");
            return (Criteria) this;
        }

        public Criteria andN02WeightBetween(Integer value1, Integer value2) {
            addCriterion("n02_weight between", value1, value2, "n02Weight");
            return (Criteria) this;
        }

        public Criteria andN02WeightNotBetween(Integer value1, Integer value2) {
            addCriterion("n02_weight not between", value1, value2, "n02Weight");
            return (Criteria) this;
        }

        public Criteria andF01WeightIsNull() {
            addCriterion("f01_weight is null");
            return (Criteria) this;
        }

        public Criteria andF01WeightIsNotNull() {
            addCriterion("f01_weight is not null");
            return (Criteria) this;
        }

        public Criteria andF01WeightEqualTo(Integer value) {
            addCriterion("f01_weight =", value, "f01Weight");
            return (Criteria) this;
        }

        public Criteria andF01WeightNotEqualTo(Integer value) {
            addCriterion("f01_weight <>", value, "f01Weight");
            return (Criteria) this;
        }

        public Criteria andF01WeightGreaterThan(Integer value) {
            addCriterion("f01_weight >", value, "f01Weight");
            return (Criteria) this;
        }

        public Criteria andF01WeightGreaterThanOrEqualTo(Integer value) {
            addCriterion("f01_weight >=", value, "f01Weight");
            return (Criteria) this;
        }

        public Criteria andF01WeightLessThan(Integer value) {
            addCriterion("f01_weight <", value, "f01Weight");
            return (Criteria) this;
        }

        public Criteria andF01WeightLessThanOrEqualTo(Integer value) {
            addCriterion("f01_weight <=", value, "f01Weight");
            return (Criteria) this;
        }

        public Criteria andF01WeightIn(List<Integer> values) {
            addCriterion("f01_weight in", values, "f01Weight");
            return (Criteria) this;
        }

        public Criteria andF01WeightNotIn(List<Integer> values) {
            addCriterion("f01_weight not in", values, "f01Weight");
            return (Criteria) this;
        }

        public Criteria andF01WeightBetween(Integer value1, Integer value2) {
            addCriterion("f01_weight between", value1, value2, "f01Weight");
            return (Criteria) this;
        }

        public Criteria andF01WeightNotBetween(Integer value1, Integer value2) {
            addCriterion("f01_weight not between", value1, value2, "f01Weight");
            return (Criteria) this;
        }

        public Criteria andN03WeightIsNull() {
            addCriterion("n03_weight is null");
            return (Criteria) this;
        }

        public Criteria andN03WeightIsNotNull() {
            addCriterion("n03_weight is not null");
            return (Criteria) this;
        }

        public Criteria andN03WeightEqualTo(Integer value) {
            addCriterion("n03_weight =", value, "n03Weight");
            return (Criteria) this;
        }

        public Criteria andN03WeightNotEqualTo(Integer value) {
            addCriterion("n03_weight <>", value, "n03Weight");
            return (Criteria) this;
        }

        public Criteria andN03WeightGreaterThan(Integer value) {
            addCriterion("n03_weight >", value, "n03Weight");
            return (Criteria) this;
        }

        public Criteria andN03WeightGreaterThanOrEqualTo(Integer value) {
            addCriterion("n03_weight >=", value, "n03Weight");
            return (Criteria) this;
        }

        public Criteria andN03WeightLessThan(Integer value) {
            addCriterion("n03_weight <", value, "n03Weight");
            return (Criteria) this;
        }

        public Criteria andN03WeightLessThanOrEqualTo(Integer value) {
            addCriterion("n03_weight <=", value, "n03Weight");
            return (Criteria) this;
        }

        public Criteria andN03WeightIn(List<Integer> values) {
            addCriterion("n03_weight in", values, "n03Weight");
            return (Criteria) this;
        }

        public Criteria andN03WeightNotIn(List<Integer> values) {
            addCriterion("n03_weight not in", values, "n03Weight");
            return (Criteria) this;
        }

        public Criteria andN03WeightBetween(Integer value1, Integer value2) {
            addCriterion("n03_weight between", value1, value2, "n03Weight");
            return (Criteria) this;
        }

        public Criteria andN03WeightNotBetween(Integer value1, Integer value2) {
            addCriterion("n03_weight not between", value1, value2, "n03Weight");
            return (Criteria) this;
        }

        public Criteria andL02WeightIsNull() {
            addCriterion("l02_weight is null");
            return (Criteria) this;
        }

        public Criteria andL02WeightIsNotNull() {
            addCriterion("l02_weight is not null");
            return (Criteria) this;
        }

        public Criteria andL02WeightEqualTo(Integer value) {
            addCriterion("l02_weight =", value, "l02Weight");
            return (Criteria) this;
        }

        public Criteria andL02WeightNotEqualTo(Integer value) {
            addCriterion("l02_weight <>", value, "l02Weight");
            return (Criteria) this;
        }

        public Criteria andL02WeightGreaterThan(Integer value) {
            addCriterion("l02_weight >", value, "l02Weight");
            return (Criteria) this;
        }

        public Criteria andL02WeightGreaterThanOrEqualTo(Integer value) {
            addCriterion("l02_weight >=", value, "l02Weight");
            return (Criteria) this;
        }

        public Criteria andL02WeightLessThan(Integer value) {
            addCriterion("l02_weight <", value, "l02Weight");
            return (Criteria) this;
        }

        public Criteria andL02WeightLessThanOrEqualTo(Integer value) {
            addCriterion("l02_weight <=", value, "l02Weight");
            return (Criteria) this;
        }

        public Criteria andL02WeightIn(List<Integer> values) {
            addCriterion("l02_weight in", values, "l02Weight");
            return (Criteria) this;
        }

        public Criteria andL02WeightNotIn(List<Integer> values) {
            addCriterion("l02_weight not in", values, "l02Weight");
            return (Criteria) this;
        }

        public Criteria andL02WeightBetween(Integer value1, Integer value2) {
            addCriterion("l02_weight between", value1, value2, "l02Weight");
            return (Criteria) this;
        }

        public Criteria andL02WeightNotBetween(Integer value1, Integer value2) {
            addCriterion("l02_weight not between", value1, value2, "l02Weight");
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