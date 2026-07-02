package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class MatchSettleInfoExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MatchSettleInfoExample() {
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

        public Criteria andFtT1IsNull() {
            addCriterion("ft_t1 is null");
            return (Criteria) this;
        }

        public Criteria andFtT1IsNotNull() {
            addCriterion("ft_t1 is not null");
            return (Criteria) this;
        }

        public Criteria andFtT1EqualTo(Integer value) {
            addCriterion("ft_t1 =", value, "ftT1");
            return (Criteria) this;
        }

        public Criteria andFtT1NotEqualTo(Integer value) {
            addCriterion("ft_t1 <>", value, "ftT1");
            return (Criteria) this;
        }

        public Criteria andFtT1GreaterThan(Integer value) {
            addCriterion("ft_t1 >", value, "ftT1");
            return (Criteria) this;
        }

        public Criteria andFtT1GreaterThanOrEqualTo(Integer value) {
            addCriterion("ft_t1 >=", value, "ftT1");
            return (Criteria) this;
        }

        public Criteria andFtT1LessThan(Integer value) {
            addCriterion("ft_t1 <", value, "ftT1");
            return (Criteria) this;
        }

        public Criteria andFtT1LessThanOrEqualTo(Integer value) {
            addCriterion("ft_t1 <=", value, "ftT1");
            return (Criteria) this;
        }

        public Criteria andFtT1In(List<Integer> values) {
            addCriterion("ft_t1 in", values, "ftT1");
            return (Criteria) this;
        }

        public Criteria andFtT1NotIn(List<Integer> values) {
            addCriterion("ft_t1 not in", values, "ftT1");
            return (Criteria) this;
        }

        public Criteria andFtT1Between(Integer value1, Integer value2) {
            addCriterion("ft_t1 between", value1, value2, "ftT1");
            return (Criteria) this;
        }

        public Criteria andFtT1NotBetween(Integer value1, Integer value2) {
            addCriterion("ft_t1 not between", value1, value2, "ftT1");
            return (Criteria) this;
        }

        public Criteria andFtT2IsNull() {
            addCriterion("ft_t2 is null");
            return (Criteria) this;
        }

        public Criteria andFtT2IsNotNull() {
            addCriterion("ft_t2 is not null");
            return (Criteria) this;
        }

        public Criteria andFtT2EqualTo(Integer value) {
            addCriterion("ft_t2 =", value, "ftT2");
            return (Criteria) this;
        }

        public Criteria andFtT2NotEqualTo(Integer value) {
            addCriterion("ft_t2 <>", value, "ftT2");
            return (Criteria) this;
        }

        public Criteria andFtT2GreaterThan(Integer value) {
            addCriterion("ft_t2 >", value, "ftT2");
            return (Criteria) this;
        }

        public Criteria andFtT2GreaterThanOrEqualTo(Integer value) {
            addCriterion("ft_t2 >=", value, "ftT2");
            return (Criteria) this;
        }

        public Criteria andFtT2LessThan(Integer value) {
            addCriterion("ft_t2 <", value, "ftT2");
            return (Criteria) this;
        }

        public Criteria andFtT2LessThanOrEqualTo(Integer value) {
            addCriterion("ft_t2 <=", value, "ftT2");
            return (Criteria) this;
        }

        public Criteria andFtT2In(List<Integer> values) {
            addCriterion("ft_t2 in", values, "ftT2");
            return (Criteria) this;
        }

        public Criteria andFtT2NotIn(List<Integer> values) {
            addCriterion("ft_t2 not in", values, "ftT2");
            return (Criteria) this;
        }

        public Criteria andFtT2Between(Integer value1, Integer value2) {
            addCriterion("ft_t2 between", value1, value2, "ftT2");
            return (Criteria) this;
        }

        public Criteria andFtT2NotBetween(Integer value1, Integer value2) {
            addCriterion("ft_t2 not between", value1, value2, "ftT2");
            return (Criteria) this;
        }

        public Criteria andH1T1IsNull() {
            addCriterion("h1_t1 is null");
            return (Criteria) this;
        }

        public Criteria andH1T1IsNotNull() {
            addCriterion("h1_t1 is not null");
            return (Criteria) this;
        }

        public Criteria andH1T1EqualTo(Integer value) {
            addCriterion("h1_t1 =", value, "h1T1");
            return (Criteria) this;
        }

        public Criteria andH1T1NotEqualTo(Integer value) {
            addCriterion("h1_t1 <>", value, "h1T1");
            return (Criteria) this;
        }

        public Criteria andH1T1GreaterThan(Integer value) {
            addCriterion("h1_t1 >", value, "h1T1");
            return (Criteria) this;
        }

        public Criteria andH1T1GreaterThanOrEqualTo(Integer value) {
            addCriterion("h1_t1 >=", value, "h1T1");
            return (Criteria) this;
        }

        public Criteria andH1T1LessThan(Integer value) {
            addCriterion("h1_t1 <", value, "h1T1");
            return (Criteria) this;
        }

        public Criteria andH1T1LessThanOrEqualTo(Integer value) {
            addCriterion("h1_t1 <=", value, "h1T1");
            return (Criteria) this;
        }

        public Criteria andH1T1In(List<Integer> values) {
            addCriterion("h1_t1 in", values, "h1T1");
            return (Criteria) this;
        }

        public Criteria andH1T1NotIn(List<Integer> values) {
            addCriterion("h1_t1 not in", values, "h1T1");
            return (Criteria) this;
        }

        public Criteria andH1T1Between(Integer value1, Integer value2) {
            addCriterion("h1_t1 between", value1, value2, "h1T1");
            return (Criteria) this;
        }

        public Criteria andH1T1NotBetween(Integer value1, Integer value2) {
            addCriterion("h1_t1 not between", value1, value2, "h1T1");
            return (Criteria) this;
        }

        public Criteria andH1T2IsNull() {
            addCriterion("h1_t2 is null");
            return (Criteria) this;
        }

        public Criteria andH1T2IsNotNull() {
            addCriterion("h1_t2 is not null");
            return (Criteria) this;
        }

        public Criteria andH1T2EqualTo(Integer value) {
            addCriterion("h1_t2 =", value, "h1T2");
            return (Criteria) this;
        }

        public Criteria andH1T2NotEqualTo(Integer value) {
            addCriterion("h1_t2 <>", value, "h1T2");
            return (Criteria) this;
        }

        public Criteria andH1T2GreaterThan(Integer value) {
            addCriterion("h1_t2 >", value, "h1T2");
            return (Criteria) this;
        }

        public Criteria andH1T2GreaterThanOrEqualTo(Integer value) {
            addCriterion("h1_t2 >=", value, "h1T2");
            return (Criteria) this;
        }

        public Criteria andH1T2LessThan(Integer value) {
            addCriterion("h1_t2 <", value, "h1T2");
            return (Criteria) this;
        }

        public Criteria andH1T2LessThanOrEqualTo(Integer value) {
            addCriterion("h1_t2 <=", value, "h1T2");
            return (Criteria) this;
        }

        public Criteria andH1T2In(List<Integer> values) {
            addCriterion("h1_t2 in", values, "h1T2");
            return (Criteria) this;
        }

        public Criteria andH1T2NotIn(List<Integer> values) {
            addCriterion("h1_t2 not in", values, "h1T2");
            return (Criteria) this;
        }

        public Criteria andH1T2Between(Integer value1, Integer value2) {
            addCriterion("h1_t2 between", value1, value2, "h1T2");
            return (Criteria) this;
        }

        public Criteria andH1T2NotBetween(Integer value1, Integer value2) {
            addCriterion("h1_t2 not between", value1, value2, "h1T2");
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

        public Criteria andScoresJsonIsNull() {
            addCriterion("scores_json is null");
            return (Criteria) this;
        }

        public Criteria andScoresJsonIsNotNull() {
            addCriterion("scores_json is not null");
            return (Criteria) this;
        }

        public Criteria andScoresJsonEqualTo(String value) {
            addCriterion("scores_json =", value, "scoresJson");
            return (Criteria) this;
        }

        public Criteria andScoresJsonNotEqualTo(String value) {
            addCriterion("scores_json <>", value, "scoresJson");
            return (Criteria) this;
        }

        public Criteria andScoresJsonGreaterThan(String value) {
            addCriterion("scores_json >", value, "scoresJson");
            return (Criteria) this;
        }

        public Criteria andScoresJsonGreaterThanOrEqualTo(String value) {
            addCriterion("scores_json >=", value, "scoresJson");
            return (Criteria) this;
        }

        public Criteria andScoresJsonLessThan(String value) {
            addCriterion("scores_json <", value, "scoresJson");
            return (Criteria) this;
        }

        public Criteria andScoresJsonLessThanOrEqualTo(String value) {
            addCriterion("scores_json <=", value, "scoresJson");
            return (Criteria) this;
        }

        public Criteria andScoresJsonLike(String value) {
            addCriterion("scores_json like", value, "scoresJson");
            return (Criteria) this;
        }

        public Criteria andScoresJsonNotLike(String value) {
            addCriterion("scores_json not like", value, "scoresJson");
            return (Criteria) this;
        }

        public Criteria andScoresJsonIn(List<String> values) {
            addCriterion("scores_json in", values, "scoresJson");
            return (Criteria) this;
        }

        public Criteria andScoresJsonNotIn(List<String> values) {
            addCriterion("scores_json not in", values, "scoresJson");
            return (Criteria) this;
        }

        public Criteria andScoresJsonBetween(String value1, String value2) {
            addCriterion("scores_json between", value1, value2, "scoresJson");
            return (Criteria) this;
        }

        public Criteria andScoresJsonNotBetween(String value1, String value2) {
            addCriterion("scores_json not between", value1, value2, "scoresJson");
            return (Criteria) this;
        }

        public Criteria andScoresJsonExtraIsNull() {
            addCriterion("scores_json_extra is null");
            return (Criteria) this;
        }

        public Criteria andScoresJsonExtraIsNotNull() {
            addCriterion("scores_json_extra is not null");
            return (Criteria) this;
        }

        public Criteria andScoresJsonExtraEqualTo(String value) {
            addCriterion("scores_json_extra =", value, "scoresJsonExtra");
            return (Criteria) this;
        }

        public Criteria andScoresJsonExtraNotEqualTo(String value) {
            addCriterion("scores_json_extra <>", value, "scoresJsonExtra");
            return (Criteria) this;
        }

        public Criteria andScoresJsonExtraGreaterThan(String value) {
            addCriterion("scores_json_extra >", value, "scoresJsonExtra");
            return (Criteria) this;
        }

        public Criteria andScoresJsonExtraGreaterThanOrEqualTo(String value) {
            addCriterion("scores_json_extra >=", value, "scoresJsonExtra");
            return (Criteria) this;
        }

        public Criteria andScoresJsonExtraLessThan(String value) {
            addCriterion("scores_json_extra <", value, "scoresJsonExtra");
            return (Criteria) this;
        }

        public Criteria andScoresJsonExtraLessThanOrEqualTo(String value) {
            addCriterion("scores_json_extra <=", value, "scoresJsonExtra");
            return (Criteria) this;
        }

        public Criteria andScoresJsonExtraLike(String value) {
            addCriterion("scores_json_extra like", value, "scoresJsonExtra");
            return (Criteria) this;
        }

        public Criteria andScoresJsonExtraNotLike(String value) {
            addCriterion("scores_json_extra not like", value, "scoresJsonExtra");
            return (Criteria) this;
        }

        public Criteria andScoresJsonExtraIn(List<String> values) {
            addCriterion("scores_json_extra in", values, "scoresJsonExtra");
            return (Criteria) this;
        }

        public Criteria andScoresJsonExtraNotIn(List<String> values) {
            addCriterion("scores_json_extra not in", values, "scoresJsonExtra");
            return (Criteria) this;
        }

        public Criteria andScoresJsonExtraBetween(String value1, String value2) {
            addCriterion("scores_json_extra between", value1, value2, "scoresJsonExtra");
            return (Criteria) this;
        }

        public Criteria andScoresJsonExtraNotBetween(String value1, String value2) {
            addCriterion("scores_json_extra not between", value1, value2, "scoresJsonExtra");
            return (Criteria) this;
        }

        public Criteria andFreezeStatusIsNull() {
            addCriterion("freeze_status is null");
            return (Criteria) this;
        }

        public Criteria andFreezeStatusIsNotNull() {
            addCriterion("freeze_status is not null");
            return (Criteria) this;
        }

        public Criteria andFreezeStatusEqualTo(Integer value) {
            addCriterion("freeze_status =", value, "freezeStatus");
            return (Criteria) this;
        }

        public Criteria andFreezeStatusNotEqualTo(Integer value) {
            addCriterion("freeze_status <>", value, "freezeStatus");
            return (Criteria) this;
        }

        public Criteria andFreezeStatusGreaterThan(Integer value) {
            addCriterion("freeze_status >", value, "freezeStatus");
            return (Criteria) this;
        }

        public Criteria andFreezeStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("freeze_status >=", value, "freezeStatus");
            return (Criteria) this;
        }

        public Criteria andFreezeStatusLessThan(Integer value) {
            addCriterion("freeze_status <", value, "freezeStatus");
            return (Criteria) this;
        }

        public Criteria andFreezeStatusLessThanOrEqualTo(Integer value) {
            addCriterion("freeze_status <=", value, "freezeStatus");
            return (Criteria) this;
        }

        public Criteria andFreezeStatusIn(List<Integer> values) {
            addCriterion("freeze_status in", values, "freezeStatus");
            return (Criteria) this;
        }

        public Criteria andFreezeStatusNotIn(List<Integer> values) {
            addCriterion("freeze_status not in", values, "freezeStatus");
            return (Criteria) this;
        }

        public Criteria andFreezeStatusBetween(Integer value1, Integer value2) {
            addCriterion("freeze_status between", value1, value2, "freezeStatus");
            return (Criteria) this;
        }

        public Criteria andFreezeStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("freeze_status not between", value1, value2, "freezeStatus");
            return (Criteria) this;
        }

        public Criteria andSettleTypeIsNull() {
            addCriterion("settle_type is null");
            return (Criteria) this;
        }

        public Criteria andSettleTypeIsNotNull() {
            addCriterion("settle_type is not null");
            return (Criteria) this;
        }

        public Criteria andSettleTypeEqualTo(Integer value) {
            addCriterion("settle_type =", value, "settleType");
            return (Criteria) this;
        }

        public Criteria andSettleTypeNotEqualTo(Integer value) {
            addCriterion("settle_type <>", value, "settleType");
            return (Criteria) this;
        }

        public Criteria andSettleTypeGreaterThan(Integer value) {
            addCriterion("settle_type >", value, "settleType");
            return (Criteria) this;
        }

        public Criteria andSettleTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("settle_type >=", value, "settleType");
            return (Criteria) this;
        }

        public Criteria andSettleTypeLessThan(Integer value) {
            addCriterion("settle_type <", value, "settleType");
            return (Criteria) this;
        }

        public Criteria andSettleTypeLessThanOrEqualTo(Integer value) {
            addCriterion("settle_type <=", value, "settleType");
            return (Criteria) this;
        }

        public Criteria andSettleTypeIn(List<Integer> values) {
            addCriterion("settle_type in", values, "settleType");
            return (Criteria) this;
        }

        public Criteria andSettleTypeNotIn(List<Integer> values) {
            addCriterion("settle_type not in", values, "settleType");
            return (Criteria) this;
        }

        public Criteria andSettleTypeBetween(Integer value1, Integer value2) {
            addCriterion("settle_type between", value1, value2, "settleType");
            return (Criteria) this;
        }

        public Criteria andSettleTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("settle_type not between", value1, value2, "settleType");
            return (Criteria) this;
        }

        public Criteria andAllLiveTraderIsNull() {
            addCriterion("all_live_trader is null");
            return (Criteria) this;
        }

        public Criteria andAllLiveTraderIsNotNull() {
            addCriterion("all_live_trader is not null");
            return (Criteria) this;
        }

        public Criteria andAllLiveTraderEqualTo(String value) {
            addCriterion("all_live_trader =", value, "allLiveTrader");
            return (Criteria) this;
        }

        public Criteria andAllLiveTraderNotEqualTo(String value) {
            addCriterion("all_live_trader <>", value, "allLiveTrader");
            return (Criteria) this;
        }

        public Criteria andAllLiveTraderGreaterThan(String value) {
            addCriterion("all_live_trader >", value, "allLiveTrader");
            return (Criteria) this;
        }

        public Criteria andAllLiveTraderGreaterThanOrEqualTo(String value) {
            addCriterion("all_live_trader >=", value, "allLiveTrader");
            return (Criteria) this;
        }

        public Criteria andAllLiveTraderLessThan(String value) {
            addCriterion("all_live_trader <", value, "allLiveTrader");
            return (Criteria) this;
        }

        public Criteria andAllLiveTraderLessThanOrEqualTo(String value) {
            addCriterion("all_live_trader <=", value, "allLiveTrader");
            return (Criteria) this;
        }

        public Criteria andAllLiveTraderLike(String value) {
            addCriterion("all_live_trader like", value, "allLiveTrader");
            return (Criteria) this;
        }

        public Criteria andAllLiveTraderNotLike(String value) {
            addCriterion("all_live_trader not like", value, "allLiveTrader");
            return (Criteria) this;
        }

        public Criteria andAllLiveTraderIn(List<String> values) {
            addCriterion("all_live_trader in", values, "allLiveTrader");
            return (Criteria) this;
        }

        public Criteria andAllLiveTraderNotIn(List<String> values) {
            addCriterion("all_live_trader not in", values, "allLiveTrader");
            return (Criteria) this;
        }

        public Criteria andAllLiveTraderBetween(String value1, String value2) {
            addCriterion("all_live_trader between", value1, value2, "allLiveTrader");
            return (Criteria) this;
        }

        public Criteria andAllLiveTraderNotBetween(String value1, String value2) {
            addCriterion("all_live_trader not between", value1, value2, "allLiveTrader");
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

        public Criteria andLimitUserArrayIsNull() {
            addCriterion("limit_user_array is null");
            return (Criteria) this;
        }

        public Criteria andLimitUserArrayIsNotNull() {
            addCriterion("limit_user_array is not null");
            return (Criteria) this;
        }

        public Criteria andLimitUserArrayEqualTo(String value) {
            addCriterion("limit_user_array =", value, "limitUserArray");
            return (Criteria) this;
        }

        public Criteria andLimitUserArrayNotEqualTo(String value) {
            addCriterion("limit_user_array <>", value, "limitUserArray");
            return (Criteria) this;
        }

        public Criteria andLimitUserArrayGreaterThan(String value) {
            addCriterion("limit_user_array >", value, "limitUserArray");
            return (Criteria) this;
        }

        public Criteria andLimitUserArrayGreaterThanOrEqualTo(String value) {
            addCriterion("limit_user_array >=", value, "limitUserArray");
            return (Criteria) this;
        }

        public Criteria andLimitUserArrayLessThan(String value) {
            addCriterion("limit_user_array <", value, "limitUserArray");
            return (Criteria) this;
        }

        public Criteria andLimitUserArrayLessThanOrEqualTo(String value) {
            addCriterion("limit_user_array <=", value, "limitUserArray");
            return (Criteria) this;
        }

        public Criteria andLimitUserArrayLike(String value) {
            addCriterion("limit_user_array like", value, "limitUserArray");
            return (Criteria) this;
        }

        public Criteria andLimitUserArrayNotLike(String value) {
            addCriterion("limit_user_array not like", value, "limitUserArray");
            return (Criteria) this;
        }

        public Criteria andLimitUserArrayIn(List<String> values) {
            addCriterion("limit_user_array in", values, "limitUserArray");
            return (Criteria) this;
        }

        public Criteria andLimitUserArrayNotIn(List<String> values) {
            addCriterion("limit_user_array not in", values, "limitUserArray");
            return (Criteria) this;
        }

        public Criteria andLimitUserArrayBetween(String value1, String value2) {
            addCriterion("limit_user_array between", value1, value2, "limitUserArray");
            return (Criteria) this;
        }

        public Criteria andLimitUserArrayNotBetween(String value1, String value2) {
            addCriterion("limit_user_array not between", value1, value2, "limitUserArray");
            return (Criteria) this;
        }

        public Criteria andAuditorJsonIsNull() {
            addCriterion("auditor_json is null");
            return (Criteria) this;
        }

        public Criteria andAuditorJsonIsNotNull() {
            addCriterion("auditor_json is not null");
            return (Criteria) this;
        }

        public Criteria andAuditorJsonEqualTo(String value) {
            addCriterion("auditor_json =", value, "auditorJson");
            return (Criteria) this;
        }

        public Criteria andAuditorJsonNotEqualTo(String value) {
            addCriterion("auditor_json <>", value, "auditorJson");
            return (Criteria) this;
        }

        public Criteria andAuditorJsonGreaterThan(String value) {
            addCriterion("auditor_json >", value, "auditorJson");
            return (Criteria) this;
        }

        public Criteria andAuditorJsonGreaterThanOrEqualTo(String value) {
            addCriterion("auditor_json >=", value, "auditorJson");
            return (Criteria) this;
        }

        public Criteria andAuditorJsonLessThan(String value) {
            addCriterion("auditor_json <", value, "auditorJson");
            return (Criteria) this;
        }

        public Criteria andAuditorJsonLessThanOrEqualTo(String value) {
            addCriterion("auditor_json <=", value, "auditorJson");
            return (Criteria) this;
        }

        public Criteria andAuditorJsonLike(String value) {
            addCriterion("auditor_json like", value, "auditorJson");
            return (Criteria) this;
        }

        public Criteria andAuditorJsonNotLike(String value) {
            addCriterion("auditor_json not like", value, "auditorJson");
            return (Criteria) this;
        }

        public Criteria andAuditorJsonIn(List<String> values) {
            addCriterion("auditor_json in", values, "auditorJson");
            return (Criteria) this;
        }

        public Criteria andAuditorJsonNotIn(List<String> values) {
            addCriterion("auditor_json not in", values, "auditorJson");
            return (Criteria) this;
        }

        public Criteria andAuditorJsonBetween(String value1, String value2) {
            addCriterion("auditor_json between", value1, value2, "auditorJson");
            return (Criteria) this;
        }

        public Criteria andAuditorJsonNotBetween(String value1, String value2) {
            addCriterion("auditor_json not between", value1, value2, "auditorJson");
            return (Criteria) this;
        }

        public Criteria andIsAutoSettleDataSourceIsNull() {
            addCriterion("is_auto_settle_data_source is null");
            return (Criteria) this;
        }

        public Criteria andIsAutoSettleDataSourceIsNotNull() {
            addCriterion("is_auto_settle_data_source is not null");
            return (Criteria) this;
        }

        public Criteria andIsAutoSettleDataSourceEqualTo(Integer value) {
            addCriterion("is_auto_settle_data_source =", value, "isAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andIsAutoSettleDataSourceNotEqualTo(Integer value) {
            addCriterion("is_auto_settle_data_source <>", value, "isAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andIsAutoSettleDataSourceGreaterThan(Integer value) {
            addCriterion("is_auto_settle_data_source >", value, "isAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andIsAutoSettleDataSourceGreaterThanOrEqualTo(Integer value) {
            addCriterion("is_auto_settle_data_source >=", value, "isAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andIsAutoSettleDataSourceLessThan(Integer value) {
            addCriterion("is_auto_settle_data_source <", value, "isAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andIsAutoSettleDataSourceLessThanOrEqualTo(Integer value) {
            addCriterion("is_auto_settle_data_source <=", value, "isAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andIsAutoSettleDataSourceIn(List<Integer> values) {
            addCriterion("is_auto_settle_data_source in", values, "isAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andIsAutoSettleDataSourceNotIn(List<Integer> values) {
            addCriterion("is_auto_settle_data_source not in", values, "isAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andIsAutoSettleDataSourceBetween(Integer value1, Integer value2) {
            addCriterion("is_auto_settle_data_source between", value1, value2, "isAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andIsAutoSettleDataSourceNotBetween(Integer value1, Integer value2) {
            addCriterion("is_auto_settle_data_source not between", value1, value2, "isAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andGoalAutoSettleDataSourceIsNull() {
            addCriterion("goal_auto_settle_data_source is null");
            return (Criteria) this;
        }

        public Criteria andGoalAutoSettleDataSourceIsNotNull() {
            addCriterion("goal_auto_settle_data_source is not null");
            return (Criteria) this;
        }

        public Criteria andGoalAutoSettleDataSourceEqualTo(Integer value) {
            addCriterion("goal_auto_settle_data_source =", value, "goalAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andGoalAutoSettleDataSourceNotEqualTo(Integer value) {
            addCriterion("goal_auto_settle_data_source <>", value, "goalAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andGoalAutoSettleDataSourceGreaterThan(Integer value) {
            addCriterion("goal_auto_settle_data_source >", value, "goalAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andGoalAutoSettleDataSourceGreaterThanOrEqualTo(Integer value) {
            addCriterion("goal_auto_settle_data_source >=", value, "goalAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andGoalAutoSettleDataSourceLessThan(Integer value) {
            addCriterion("goal_auto_settle_data_source <", value, "goalAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andGoalAutoSettleDataSourceLessThanOrEqualTo(Integer value) {
            addCriterion("goal_auto_settle_data_source <=", value, "goalAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andGoalAutoSettleDataSourceIn(List<Integer> values) {
            addCriterion("goal_auto_settle_data_source in", values, "goalAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andGoalAutoSettleDataSourceNotIn(List<Integer> values) {
            addCriterion("goal_auto_settle_data_source not in", values, "goalAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andGoalAutoSettleDataSourceBetween(Integer value1, Integer value2) {
            addCriterion("goal_auto_settle_data_source between", value1, value2, "goalAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andGoalAutoSettleDataSourceNotBetween(Integer value1, Integer value2) {
            addCriterion("goal_auto_settle_data_source not between", value1, value2, "goalAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andCornerAutoSettleDataSourceIsNull() {
            addCriterion("corner_auto_settle_data_source is null");
            return (Criteria) this;
        }

        public Criteria andCornerAutoSettleDataSourceIsNotNull() {
            addCriterion("corner_auto_settle_data_source is not null");
            return (Criteria) this;
        }

        public Criteria andCornerAutoSettleDataSourceEqualTo(Integer value) {
            addCriterion("corner_auto_settle_data_source =", value, "cornerAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andCornerAutoSettleDataSourceNotEqualTo(Integer value) {
            addCriterion("corner_auto_settle_data_source <>", value, "cornerAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andCornerAutoSettleDataSourceGreaterThan(Integer value) {
            addCriterion("corner_auto_settle_data_source >", value, "cornerAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andCornerAutoSettleDataSourceGreaterThanOrEqualTo(Integer value) {
            addCriterion("corner_auto_settle_data_source >=", value, "cornerAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andCornerAutoSettleDataSourceLessThan(Integer value) {
            addCriterion("corner_auto_settle_data_source <", value, "cornerAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andCornerAutoSettleDataSourceLessThanOrEqualTo(Integer value) {
            addCriterion("corner_auto_settle_data_source <=", value, "cornerAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andCornerAutoSettleDataSourceIn(List<Integer> values) {
            addCriterion("corner_auto_settle_data_source in", values, "cornerAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andCornerAutoSettleDataSourceNotIn(List<Integer> values) {
            addCriterion("corner_auto_settle_data_source not in", values, "cornerAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andCornerAutoSettleDataSourceBetween(Integer value1, Integer value2) {
            addCriterion("corner_auto_settle_data_source between", value1, value2, "cornerAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andCornerAutoSettleDataSourceNotBetween(Integer value1, Integer value2) {
            addCriterion("corner_auto_settle_data_source not between", value1, value2, "cornerAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andCurrentEventStatusIsNull() {
            addCriterion("current_event_status is null");
            return (Criteria) this;
        }

        public Criteria andCurrentEventStatusIsNotNull() {
            addCriterion("current_event_status is not null");
            return (Criteria) this;
        }

        public Criteria andCurrentEventStatusEqualTo(Integer value) {
            addCriterion("current_event_status =", value, "currentEventStatus");
            return (Criteria) this;
        }

        public Criteria andCurrentEventStatusNotEqualTo(Integer value) {
            addCriterion("current_event_status <>", value, "currentEventStatus");
            return (Criteria) this;
        }

        public Criteria andCurrentEventStatusGreaterThan(Integer value) {
            addCriterion("current_event_status >", value, "currentEventStatus");
            return (Criteria) this;
        }

        public Criteria andCurrentEventStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("current_event_status >=", value, "currentEventStatus");
            return (Criteria) this;
        }

        public Criteria andCurrentEventStatusLessThan(Integer value) {
            addCriterion("current_event_status <", value, "currentEventStatus");
            return (Criteria) this;
        }

        public Criteria andCurrentEventStatusLessThanOrEqualTo(Integer value) {
            addCriterion("current_event_status <=", value, "currentEventStatus");
            return (Criteria) this;
        }

        public Criteria andCurrentEventStatusIn(List<Integer> values) {
            addCriterion("current_event_status in", values, "currentEventStatus");
            return (Criteria) this;
        }

        public Criteria andCurrentEventStatusNotIn(List<Integer> values) {
            addCriterion("current_event_status not in", values, "currentEventStatus");
            return (Criteria) this;
        }

        public Criteria andCurrentEventStatusBetween(Integer value1, Integer value2) {
            addCriterion("current_event_status between", value1, value2, "currentEventStatus");
            return (Criteria) this;
        }

        public Criteria andCurrentEventStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("current_event_status not between", value1, value2, "currentEventStatus");
            return (Criteria) this;
        }

        public Criteria andIsGrayIsNull() {
            addCriterion("is_gray is null");
            return (Criteria) this;
        }

        public Criteria andIsGrayIsNotNull() {
            addCriterion("is_gray is not null");
            return (Criteria) this;
        }

        public Criteria andIsGrayEqualTo(Integer value) {
            addCriterion("is_gray =", value, "isGray");
            return (Criteria) this;
        }

        public Criteria andIsGrayNotEqualTo(Integer value) {
            addCriterion("is_gray <>", value, "isGray");
            return (Criteria) this;
        }

        public Criteria andIsGrayGreaterThan(Integer value) {
            addCriterion("is_gray >", value, "isGray");
            return (Criteria) this;
        }

        public Criteria andIsGrayGreaterThanOrEqualTo(Integer value) {
            addCriterion("is_gray >=", value, "isGray");
            return (Criteria) this;
        }

        public Criteria andIsGrayLessThan(Integer value) {
            addCriterion("is_gray <", value, "isGray");
            return (Criteria) this;
        }

        public Criteria andIsGrayLessThanOrEqualTo(Integer value) {
            addCriterion("is_gray <=", value, "isGray");
            return (Criteria) this;
        }

        public Criteria andIsGrayIn(List<Integer> values) {
            addCriterion("is_gray in", values, "isGray");
            return (Criteria) this;
        }

        public Criteria andIsGrayNotIn(List<Integer> values) {
            addCriterion("is_gray not in", values, "isGray");
            return (Criteria) this;
        }

        public Criteria andIsGrayBetween(Integer value1, Integer value2) {
            addCriterion("is_gray between", value1, value2, "isGray");
            return (Criteria) this;
        }

        public Criteria andIsGrayNotBetween(Integer value1, Integer value2) {
            addCriterion("is_gray not between", value1, value2, "isGray");
            return (Criteria) this;
        }

        public Criteria andHasDeleteEventIsNull() {
            addCriterion("has_delete_event is null");
            return (Criteria) this;
        }

        public Criteria andHasDeleteEventIsNotNull() {
            addCriterion("has_delete_event is not null");
            return (Criteria) this;
        }

        public Criteria andHasDeleteEventEqualTo(Integer value) {
            addCriterion("has_delete_event =", value, "hasDeleteEvent");
            return (Criteria) this;
        }

        public Criteria andHasDeleteEventNotEqualTo(Integer value) {
            addCriterion("has_delete_event <>", value, "hasDeleteEvent");
            return (Criteria) this;
        }

        public Criteria andHasDeleteEventGreaterThan(Integer value) {
            addCriterion("has_delete_event >", value, "hasDeleteEvent");
            return (Criteria) this;
        }

        public Criteria andHasDeleteEventGreaterThanOrEqualTo(Integer value) {
            addCriterion("has_delete_event >=", value, "hasDeleteEvent");
            return (Criteria) this;
        }

        public Criteria andHasDeleteEventLessThan(Integer value) {
            addCriterion("has_delete_event <", value, "hasDeleteEvent");
            return (Criteria) this;
        }

        public Criteria andHasDeleteEventLessThanOrEqualTo(Integer value) {
            addCriterion("has_delete_event <=", value, "hasDeleteEvent");
            return (Criteria) this;
        }

        public Criteria andHasDeleteEventIn(List<Integer> values) {
            addCriterion("has_delete_event in", values, "hasDeleteEvent");
            return (Criteria) this;
        }

        public Criteria andHasDeleteEventNotIn(List<Integer> values) {
            addCriterion("has_delete_event not in", values, "hasDeleteEvent");
            return (Criteria) this;
        }

        public Criteria andHasDeleteEventBetween(Integer value1, Integer value2) {
            addCriterion("has_delete_event between", value1, value2, "hasDeleteEvent");
            return (Criteria) this;
        }

        public Criteria andHasDeleteEventNotBetween(Integer value1, Integer value2) {
            addCriterion("has_delete_event not between", value1, value2, "hasDeleteEvent");
            return (Criteria) this;
        }

        public Criteria andBookingAutoSettleDataSourceIsNull() {
            addCriterion("booking_auto_settle_data_source is null");
            return (Criteria) this;
        }

        public Criteria andBookingAutoSettleDataSourceIsNotNull() {
            addCriterion("booking_auto_settle_data_source is not null");
            return (Criteria) this;
        }

        public Criteria andBookingAutoSettleDataSourceEqualTo(Integer value) {
            addCriterion("booking_auto_settle_data_source =", value, "bookingAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andBookingAutoSettleDataSourceNotEqualTo(Integer value) {
            addCriterion("booking_auto_settle_data_source <>", value, "bookingAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andBookingAutoSettleDataSourceGreaterThan(Integer value) {
            addCriterion("booking_auto_settle_data_source >", value, "bookingAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andBookingAutoSettleDataSourceGreaterThanOrEqualTo(Integer value) {
            addCriterion("booking_auto_settle_data_source >=", value, "bookingAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andBookingAutoSettleDataSourceLessThan(Integer value) {
            addCriterion("booking_auto_settle_data_source <", value, "bookingAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andBookingAutoSettleDataSourceLessThanOrEqualTo(Integer value) {
            addCriterion("booking_auto_settle_data_source <=", value, "bookingAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andBookingAutoSettleDataSourceIn(List<Integer> values) {
            addCriterion("booking_auto_settle_data_source in", values, "bookingAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andBookingAutoSettleDataSourceNotIn(List<Integer> values) {
            addCriterion("booking_auto_settle_data_source not in", values, "bookingAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andBookingAutoSettleDataSourceBetween(Integer value1, Integer value2) {
            addCriterion("booking_auto_settle_data_source between", value1, value2, "bookingAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andBookingAutoSettleDataSourceNotBetween(Integer value1, Integer value2) {
            addCriterion("booking_auto_settle_data_source not between", value1, value2, "bookingAutoSettleDataSource");
            return (Criteria) this;
        }

        public Criteria andIsmemoIsNull() {
            addCriterion("isMemo is null");
            return (Criteria) this;
        }

        public Criteria andIsmemoIsNotNull() {
            addCriterion("isMemo is not null");
            return (Criteria) this;
        }

        public Criteria andIsmemoEqualTo(Integer value) {
            addCriterion("isMemo =", value, "ismemo");
            return (Criteria) this;
        }

        public Criteria andIsmemoNotEqualTo(Integer value) {
            addCriterion("isMemo <>", value, "ismemo");
            return (Criteria) this;
        }

        public Criteria andIsmemoGreaterThan(Integer value) {
            addCriterion("isMemo >", value, "ismemo");
            return (Criteria) this;
        }

        public Criteria andIsmemoGreaterThanOrEqualTo(Integer value) {
            addCriterion("isMemo >=", value, "ismemo");
            return (Criteria) this;
        }

        public Criteria andIsmemoLessThan(Integer value) {
            addCriterion("isMemo <", value, "ismemo");
            return (Criteria) this;
        }

        public Criteria andIsmemoLessThanOrEqualTo(Integer value) {
            addCriterion("isMemo <=", value, "ismemo");
            return (Criteria) this;
        }

        public Criteria andIsmemoIn(List<Integer> values) {
            addCriterion("isMemo in", values, "ismemo");
            return (Criteria) this;
        }

        public Criteria andIsmemoNotIn(List<Integer> values) {
            addCriterion("isMemo not in", values, "ismemo");
            return (Criteria) this;
        }

        public Criteria andIsmemoBetween(Integer value1, Integer value2) {
            addCriterion("isMemo between", value1, value2, "ismemo");
            return (Criteria) this;
        }

        public Criteria andIsmemoNotBetween(Integer value1, Integer value2) {
            addCriterion("isMemo not between", value1, value2, "ismemo");
            return (Criteria) this;
        }

        public Criteria andSettleOrderClosedIsNull() {
            addCriterion("settle_order_closed is null");
            return (Criteria) this;
        }

        public Criteria andSettleOrderClosedIsNotNull() {
            addCriterion("settle_order_closed is not null");
            return (Criteria) this;
        }

        public Criteria andSettleOrderClosedEqualTo(Integer value) {
            addCriterion("settle_order_closed =", value, "settleOrderClosed");
            return (Criteria) this;
        }

        public Criteria andSettleOrderClosedNotEqualTo(Integer value) {
            addCriterion("settle_order_closed <>", value, "settleOrderClosed");
            return (Criteria) this;
        }

        public Criteria andSettleOrderClosedGreaterThan(Integer value) {
            addCriterion("settle_order_closed >", value, "settleOrderClosed");
            return (Criteria) this;
        }

        public Criteria andSettleOrderClosedGreaterThanOrEqualTo(Integer value) {
            addCriterion("settle_order_closed >=", value, "settleOrderClosed");
            return (Criteria) this;
        }

        public Criteria andSettleOrderClosedLessThan(Integer value) {
            addCriterion("settle_order_closed <", value, "settleOrderClosed");
            return (Criteria) this;
        }

        public Criteria andSettleOrderClosedLessThanOrEqualTo(Integer value) {
            addCriterion("settle_order_closed <=", value, "settleOrderClosed");
            return (Criteria) this;
        }

        public Criteria andSettleOrderClosedIn(List<Integer> values) {
            addCriterion("settle_order_closed in", values, "settleOrderClosed");
            return (Criteria) this;
        }

        public Criteria andSettleOrderClosedNotIn(List<Integer> values) {
            addCriterion("settle_order_closed not in", values, "settleOrderClosed");
            return (Criteria) this;
        }

        public Criteria andSettleOrderClosedBetween(Integer value1, Integer value2) {
            addCriterion("settle_order_closed between", value1, value2, "settleOrderClosed");
            return (Criteria) this;
        }

        public Criteria andSettleOrderClosedNotBetween(Integer value1, Integer value2) {
            addCriterion("settle_order_closed not between", value1, value2, "settleOrderClosed");
            return (Criteria) this;
        }

        public Criteria andFiveMinSwitchIsNull() {
            addCriterion("five_min_switch is null");
            return (Criteria) this;
        }

        public Criteria andFiveMinSwitchIsNotNull() {
            addCriterion("five_min_switch is not null");
            return (Criteria) this;
        }

        public Criteria andFiveMinSwitchEqualTo(Integer value) {
            addCriterion("five_min_switch =", value, "fiveMinSwitch");
            return (Criteria) this;
        }

        public Criteria andFiveMinSwitchNotEqualTo(Integer value) {
            addCriterion("five_min_switch <>", value, "fiveMinSwitch");
            return (Criteria) this;
        }

        public Criteria andFiveMinSwitchGreaterThan(Integer value) {
            addCriterion("five_min_switch >", value, "fiveMinSwitch");
            return (Criteria) this;
        }

        public Criteria andFiveMinSwitchGreaterThanOrEqualTo(Integer value) {
            addCriterion("five_min_switch >=", value, "fiveMinSwitch");
            return (Criteria) this;
        }

        public Criteria andFiveMinSwitchLessThan(Integer value) {
            addCriterion("five_min_switch <", value, "fiveMinSwitch");
            return (Criteria) this;
        }

        public Criteria andFiveMinSwitchLessThanOrEqualTo(Integer value) {
            addCriterion("five_min_switch <=", value, "fiveMinSwitch");
            return (Criteria) this;
        }

        public Criteria andFiveMinSwitchIn(List<Integer> values) {
            addCriterion("five_min_switch in", values, "fiveMinSwitch");
            return (Criteria) this;
        }

        public Criteria andFiveMinSwitchNotIn(List<Integer> values) {
            addCriterion("five_min_switch not in", values, "fiveMinSwitch");
            return (Criteria) this;
        }

        public Criteria andFiveMinSwitchBetween(Integer value1, Integer value2) {
            addCriterion("five_min_switch between", value1, value2, "fiveMinSwitch");
            return (Criteria) this;
        }

        public Criteria andFiveMinSwitchNotBetween(Integer value1, Integer value2) {
            addCriterion("five_min_switch not between", value1, value2, "fiveMinSwitch");
            return (Criteria) this;
        }

        public Criteria andAuditorActiveArrayIsNull() {
            addCriterion("auditor_active_array is null");
            return (Criteria) this;
        }

        public Criteria andAuditorActiveArrayIsNotNull() {
            addCriterion("auditor_active_array is not null");
            return (Criteria) this;
        }

        public Criteria andAuditorActiveArrayEqualTo(String value) {
            addCriterion("auditor_active_array =", value, "auditorActiveArray");
            return (Criteria) this;
        }

        public Criteria andAuditorActiveArrayNotEqualTo(String value) {
            addCriterion("auditor_active_array <>", value, "auditorActiveArray");
            return (Criteria) this;
        }

        public Criteria andAuditorActiveArrayGreaterThan(String value) {
            addCriterion("auditor_active_array >", value, "auditorActiveArray");
            return (Criteria) this;
        }

        public Criteria andAuditorActiveArrayGreaterThanOrEqualTo(String value) {
            addCriterion("auditor_active_array >=", value, "auditorActiveArray");
            return (Criteria) this;
        }

        public Criteria andAuditorActiveArrayLessThan(String value) {
            addCriterion("auditor_active_array <", value, "auditorActiveArray");
            return (Criteria) this;
        }

        public Criteria andAuditorActiveArrayLessThanOrEqualTo(String value) {
            addCriterion("auditor_active_array <=", value, "auditorActiveArray");
            return (Criteria) this;
        }

        public Criteria andAuditorActiveArrayLike(String value) {
            addCriterion("auditor_active_array like", value, "auditorActiveArray");
            return (Criteria) this;
        }

        public Criteria andAuditorActiveArrayNotLike(String value) {
            addCriterion("auditor_active_array not like", value, "auditorActiveArray");
            return (Criteria) this;
        }

        public Criteria andAuditorActiveArrayIn(List<String> values) {
            addCriterion("auditor_active_array in", values, "auditorActiveArray");
            return (Criteria) this;
        }

        public Criteria andAuditorActiveArrayNotIn(List<String> values) {
            addCriterion("auditor_active_array not in", values, "auditorActiveArray");
            return (Criteria) this;
        }

        public Criteria andAuditorActiveArrayBetween(String value1, String value2) {
            addCriterion("auditor_active_array between", value1, value2, "auditorActiveArray");
            return (Criteria) this;
        }

        public Criteria andAuditorActiveArrayNotBetween(String value1, String value2) {
            addCriterion("auditor_active_array not between", value1, value2, "auditorActiveArray");
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