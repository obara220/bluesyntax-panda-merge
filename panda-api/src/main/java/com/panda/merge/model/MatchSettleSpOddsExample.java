package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class MatchSettleSpOddsExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MatchSettleSpOddsExample() {
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

        public Criteria andMarketIdIsNull() {
            addCriterion("market_id is null");
            return (Criteria) this;
        }

        public Criteria andMarketIdIsNotNull() {
            addCriterion("market_id is not null");
            return (Criteria) this;
        }

        public Criteria andMarketIdEqualTo(Long value) {
            addCriterion("market_id =", value, "marketId");
            return (Criteria) this;
        }

        public Criteria andMarketIdNotEqualTo(Long value) {
            addCriterion("market_id <>", value, "marketId");
            return (Criteria) this;
        }

        public Criteria andMarketIdGreaterThan(Long value) {
            addCriterion("market_id >", value, "marketId");
            return (Criteria) this;
        }

        public Criteria andMarketIdGreaterThanOrEqualTo(Long value) {
            addCriterion("market_id >=", value, "marketId");
            return (Criteria) this;
        }

        public Criteria andMarketIdLessThan(Long value) {
            addCriterion("market_id <", value, "marketId");
            return (Criteria) this;
        }

        public Criteria andMarketIdLessThanOrEqualTo(Long value) {
            addCriterion("market_id <=", value, "marketId");
            return (Criteria) this;
        }

        public Criteria andMarketIdIn(List<Long> values) {
            addCriterion("market_id in", values, "marketId");
            return (Criteria) this;
        }

        public Criteria andMarketIdNotIn(List<Long> values) {
            addCriterion("market_id not in", values, "marketId");
            return (Criteria) this;
        }

        public Criteria andMarketIdBetween(Long value1, Long value2) {
            addCriterion("market_id between", value1, value2, "marketId");
            return (Criteria) this;
        }

        public Criteria andMarketIdNotBetween(Long value1, Long value2) {
            addCriterion("market_id not between", value1, value2, "marketId");
            return (Criteria) this;
        }

        public Criteria andOddsNameEnIsNull() {
            addCriterion("odds_name_en is null");
            return (Criteria) this;
        }

        public Criteria andOddsNameEnIsNotNull() {
            addCriterion("odds_name_en is not null");
            return (Criteria) this;
        }

        public Criteria andOddsNameEnEqualTo(String value) {
            addCriterion("odds_name_en =", value, "oddsNameEn");
            return (Criteria) this;
        }

        public Criteria andOddsNameEnNotEqualTo(String value) {
            addCriterion("odds_name_en <>", value, "oddsNameEn");
            return (Criteria) this;
        }

        public Criteria andOddsNameEnGreaterThan(String value) {
            addCriterion("odds_name_en >", value, "oddsNameEn");
            return (Criteria) this;
        }

        public Criteria andOddsNameEnGreaterThanOrEqualTo(String value) {
            addCriterion("odds_name_en >=", value, "oddsNameEn");
            return (Criteria) this;
        }

        public Criteria andOddsNameEnLessThan(String value) {
            addCriterion("odds_name_en <", value, "oddsNameEn");
            return (Criteria) this;
        }

        public Criteria andOddsNameEnLessThanOrEqualTo(String value) {
            addCriterion("odds_name_en <=", value, "oddsNameEn");
            return (Criteria) this;
        }

        public Criteria andOddsNameEnLike(String value) {
            addCriterion("odds_name_en like", value, "oddsNameEn");
            return (Criteria) this;
        }

        public Criteria andOddsNameEnNotLike(String value) {
            addCriterion("odds_name_en not like", value, "oddsNameEn");
            return (Criteria) this;
        }

        public Criteria andOddsNameEnIn(List<String> values) {
            addCriterion("odds_name_en in", values, "oddsNameEn");
            return (Criteria) this;
        }

        public Criteria andOddsNameEnNotIn(List<String> values) {
            addCriterion("odds_name_en not in", values, "oddsNameEn");
            return (Criteria) this;
        }

        public Criteria andOddsNameEnBetween(String value1, String value2) {
            addCriterion("odds_name_en between", value1, value2, "oddsNameEn");
            return (Criteria) this;
        }

        public Criteria andOddsNameEnNotBetween(String value1, String value2) {
            addCriterion("odds_name_en not between", value1, value2, "oddsNameEn");
            return (Criteria) this;
        }

        public Criteria andOddsNameCnIsNull() {
            addCriterion("odds_name_cn is null");
            return (Criteria) this;
        }

        public Criteria andOddsNameCnIsNotNull() {
            addCriterion("odds_name_cn is not null");
            return (Criteria) this;
        }

        public Criteria andOddsNameCnEqualTo(String value) {
            addCriterion("odds_name_cn =", value, "oddsNameCn");
            return (Criteria) this;
        }

        public Criteria andOddsNameCnNotEqualTo(String value) {
            addCriterion("odds_name_cn <>", value, "oddsNameCn");
            return (Criteria) this;
        }

        public Criteria andOddsNameCnGreaterThan(String value) {
            addCriterion("odds_name_cn >", value, "oddsNameCn");
            return (Criteria) this;
        }

        public Criteria andOddsNameCnGreaterThanOrEqualTo(String value) {
            addCriterion("odds_name_cn >=", value, "oddsNameCn");
            return (Criteria) this;
        }

        public Criteria andOddsNameCnLessThan(String value) {
            addCriterion("odds_name_cn <", value, "oddsNameCn");
            return (Criteria) this;
        }

        public Criteria andOddsNameCnLessThanOrEqualTo(String value) {
            addCriterion("odds_name_cn <=", value, "oddsNameCn");
            return (Criteria) this;
        }

        public Criteria andOddsNameCnLike(String value) {
            addCriterion("odds_name_cn like", value, "oddsNameCn");
            return (Criteria) this;
        }

        public Criteria andOddsNameCnNotLike(String value) {
            addCriterion("odds_name_cn not like", value, "oddsNameCn");
            return (Criteria) this;
        }

        public Criteria andOddsNameCnIn(List<String> values) {
            addCriterion("odds_name_cn in", values, "oddsNameCn");
            return (Criteria) this;
        }

        public Criteria andOddsNameCnNotIn(List<String> values) {
            addCriterion("odds_name_cn not in", values, "oddsNameCn");
            return (Criteria) this;
        }

        public Criteria andOddsNameCnBetween(String value1, String value2) {
            addCriterion("odds_name_cn between", value1, value2, "oddsNameCn");
            return (Criteria) this;
        }

        public Criteria andOddsNameCnNotBetween(String value1, String value2) {
            addCriterion("odds_name_cn not between", value1, value2, "oddsNameCn");
            return (Criteria) this;
        }

        public Criteria andSettleStatusIsNull() {
            addCriterion("settle_status is null");
            return (Criteria) this;
        }

        public Criteria andSettleStatusIsNotNull() {
            addCriterion("settle_status is not null");
            return (Criteria) this;
        }

        public Criteria andSettleStatusEqualTo(Integer value) {
            addCriterion("settle_status =", value, "settleStatus");
            return (Criteria) this;
        }

        public Criteria andSettleStatusNotEqualTo(Integer value) {
            addCriterion("settle_status <>", value, "settleStatus");
            return (Criteria) this;
        }

        public Criteria andSettleStatusGreaterThan(Integer value) {
            addCriterion("settle_status >", value, "settleStatus");
            return (Criteria) this;
        }

        public Criteria andSettleStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("settle_status >=", value, "settleStatus");
            return (Criteria) this;
        }

        public Criteria andSettleStatusLessThan(Integer value) {
            addCriterion("settle_status <", value, "settleStatus");
            return (Criteria) this;
        }

        public Criteria andSettleStatusLessThanOrEqualTo(Integer value) {
            addCriterion("settle_status <=", value, "settleStatus");
            return (Criteria) this;
        }

        public Criteria andSettleStatusIn(List<Integer> values) {
            addCriterion("settle_status in", values, "settleStatus");
            return (Criteria) this;
        }

        public Criteria andSettleStatusNotIn(List<Integer> values) {
            addCriterion("settle_status not in", values, "settleStatus");
            return (Criteria) this;
        }

        public Criteria andSettleStatusBetween(Integer value1, Integer value2) {
            addCriterion("settle_status between", value1, value2, "settleStatus");
            return (Criteria) this;
        }

        public Criteria andSettleStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("settle_status not between", value1, value2, "settleStatus");
            return (Criteria) this;
        }

        public Criteria andSettleCountIsNull() {
            addCriterion("settle_count is null");
            return (Criteria) this;
        }

        public Criteria andSettleCountIsNotNull() {
            addCriterion("settle_count is not null");
            return (Criteria) this;
        }

        public Criteria andSettleCountEqualTo(Integer value) {
            addCriterion("settle_count =", value, "settleCount");
            return (Criteria) this;
        }

        public Criteria andSettleCountNotEqualTo(Integer value) {
            addCriterion("settle_count <>", value, "settleCount");
            return (Criteria) this;
        }

        public Criteria andSettleCountGreaterThan(Integer value) {
            addCriterion("settle_count >", value, "settleCount");
            return (Criteria) this;
        }

        public Criteria andSettleCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("settle_count >=", value, "settleCount");
            return (Criteria) this;
        }

        public Criteria andSettleCountLessThan(Integer value) {
            addCriterion("settle_count <", value, "settleCount");
            return (Criteria) this;
        }

        public Criteria andSettleCountLessThanOrEqualTo(Integer value) {
            addCriterion("settle_count <=", value, "settleCount");
            return (Criteria) this;
        }

        public Criteria andSettleCountIn(List<Integer> values) {
            addCriterion("settle_count in", values, "settleCount");
            return (Criteria) this;
        }

        public Criteria andSettleCountNotIn(List<Integer> values) {
            addCriterion("settle_count not in", values, "settleCount");
            return (Criteria) this;
        }

        public Criteria andSettleCountBetween(Integer value1, Integer value2) {
            addCriterion("settle_count between", value1, value2, "settleCount");
            return (Criteria) this;
        }

        public Criteria andSettleCountNotBetween(Integer value1, Integer value2) {
            addCriterion("settle_count not between", value1, value2, "settleCount");
            return (Criteria) this;
        }

        public Criteria andSettleTimesIsNull() {
            addCriterion("settle_times is null");
            return (Criteria) this;
        }

        public Criteria andSettleTimesIsNotNull() {
            addCriterion("settle_times is not null");
            return (Criteria) this;
        }

        public Criteria andSettleTimesEqualTo(Integer value) {
            addCriterion("settle_times =", value, "settleTimes");
            return (Criteria) this;
        }

        public Criteria andSettleTimesNotEqualTo(Integer value) {
            addCriterion("settle_times <>", value, "settleTimes");
            return (Criteria) this;
        }

        public Criteria andSettleTimesGreaterThan(Integer value) {
            addCriterion("settle_times >", value, "settleTimes");
            return (Criteria) this;
        }

        public Criteria andSettleTimesGreaterThanOrEqualTo(Integer value) {
            addCriterion("settle_times >=", value, "settleTimes");
            return (Criteria) this;
        }

        public Criteria andSettleTimesLessThan(Integer value) {
            addCriterion("settle_times <", value, "settleTimes");
            return (Criteria) this;
        }

        public Criteria andSettleTimesLessThanOrEqualTo(Integer value) {
            addCriterion("settle_times <=", value, "settleTimes");
            return (Criteria) this;
        }

        public Criteria andSettleTimesIn(List<Integer> values) {
            addCriterion("settle_times in", values, "settleTimes");
            return (Criteria) this;
        }

        public Criteria andSettleTimesNotIn(List<Integer> values) {
            addCriterion("settle_times not in", values, "settleTimes");
            return (Criteria) this;
        }

        public Criteria andSettleTimesBetween(Integer value1, Integer value2) {
            addCriterion("settle_times between", value1, value2, "settleTimes");
            return (Criteria) this;
        }

        public Criteria andSettleTimesNotBetween(Integer value1, Integer value2) {
            addCriterion("settle_times not between", value1, value2, "settleTimes");
            return (Criteria) this;
        }

        public Criteria andSettleResultIsNull() {
            addCriterion("settle_result is null");
            return (Criteria) this;
        }

        public Criteria andSettleResultIsNotNull() {
            addCriterion("settle_result is not null");
            return (Criteria) this;
        }

        public Criteria andSettleResultEqualTo(Integer value) {
            addCriterion("settle_result =", value, "settleResult");
            return (Criteria) this;
        }

        public Criteria andSettleResultNotEqualTo(Integer value) {
            addCriterion("settle_result <>", value, "settleResult");
            return (Criteria) this;
        }

        public Criteria andSettleResultGreaterThan(Integer value) {
            addCriterion("settle_result >", value, "settleResult");
            return (Criteria) this;
        }

        public Criteria andSettleResultGreaterThanOrEqualTo(Integer value) {
            addCriterion("settle_result >=", value, "settleResult");
            return (Criteria) this;
        }

        public Criteria andSettleResultLessThan(Integer value) {
            addCriterion("settle_result <", value, "settleResult");
            return (Criteria) this;
        }

        public Criteria andSettleResultLessThanOrEqualTo(Integer value) {
            addCriterion("settle_result <=", value, "settleResult");
            return (Criteria) this;
        }

        public Criteria andSettleResultIn(List<Integer> values) {
            addCriterion("settle_result in", values, "settleResult");
            return (Criteria) this;
        }

        public Criteria andSettleResultNotIn(List<Integer> values) {
            addCriterion("settle_result not in", values, "settleResult");
            return (Criteria) this;
        }

        public Criteria andSettleResultBetween(Integer value1, Integer value2) {
            addCriterion("settle_result between", value1, value2, "settleResult");
            return (Criteria) this;
        }

        public Criteria andSettleResultNotBetween(Integer value1, Integer value2) {
            addCriterion("settle_result not between", value1, value2, "settleResult");
            return (Criteria) this;
        }

        public Criteria andCheckNumberIsNull() {
            addCriterion("check_number is null");
            return (Criteria) this;
        }

        public Criteria andCheckNumberIsNotNull() {
            addCriterion("check_number is not null");
            return (Criteria) this;
        }

        public Criteria andCheckNumberEqualTo(Integer value) {
            addCriterion("check_number =", value, "checkNumber");
            return (Criteria) this;
        }

        public Criteria andCheckNumberNotEqualTo(Integer value) {
            addCriterion("check_number <>", value, "checkNumber");
            return (Criteria) this;
        }

        public Criteria andCheckNumberGreaterThan(Integer value) {
            addCriterion("check_number >", value, "checkNumber");
            return (Criteria) this;
        }

        public Criteria andCheckNumberGreaterThanOrEqualTo(Integer value) {
            addCriterion("check_number >=", value, "checkNumber");
            return (Criteria) this;
        }

        public Criteria andCheckNumberLessThan(Integer value) {
            addCriterion("check_number <", value, "checkNumber");
            return (Criteria) this;
        }

        public Criteria andCheckNumberLessThanOrEqualTo(Integer value) {
            addCriterion("check_number <=", value, "checkNumber");
            return (Criteria) this;
        }

        public Criteria andCheckNumberIn(List<Integer> values) {
            addCriterion("check_number in", values, "checkNumber");
            return (Criteria) this;
        }

        public Criteria andCheckNumberNotIn(List<Integer> values) {
            addCriterion("check_number not in", values, "checkNumber");
            return (Criteria) this;
        }

        public Criteria andCheckNumberBetween(Integer value1, Integer value2) {
            addCriterion("check_number between", value1, value2, "checkNumber");
            return (Criteria) this;
        }

        public Criteria andCheckNumberNotBetween(Integer value1, Integer value2) {
            addCriterion("check_number not between", value1, value2, "checkNumber");
            return (Criteria) this;
        }

        public Criteria andIsAutoSettleIsNull() {
            addCriterion("is_auto_settle is null");
            return (Criteria) this;
        }

        public Criteria andIsAutoSettleIsNotNull() {
            addCriterion("is_auto_settle is not null");
            return (Criteria) this;
        }

        public Criteria andIsAutoSettleEqualTo(Integer value) {
            addCriterion("is_auto_settle =", value, "isAutoSettle");
            return (Criteria) this;
        }

        public Criteria andIsAutoSettleNotEqualTo(Integer value) {
            addCriterion("is_auto_settle <>", value, "isAutoSettle");
            return (Criteria) this;
        }

        public Criteria andIsAutoSettleGreaterThan(Integer value) {
            addCriterion("is_auto_settle >", value, "isAutoSettle");
            return (Criteria) this;
        }

        public Criteria andIsAutoSettleGreaterThanOrEqualTo(Integer value) {
            addCriterion("is_auto_settle >=", value, "isAutoSettle");
            return (Criteria) this;
        }

        public Criteria andIsAutoSettleLessThan(Integer value) {
            addCriterion("is_auto_settle <", value, "isAutoSettle");
            return (Criteria) this;
        }

        public Criteria andIsAutoSettleLessThanOrEqualTo(Integer value) {
            addCriterion("is_auto_settle <=", value, "isAutoSettle");
            return (Criteria) this;
        }

        public Criteria andIsAutoSettleIn(List<Integer> values) {
            addCriterion("is_auto_settle in", values, "isAutoSettle");
            return (Criteria) this;
        }

        public Criteria andIsAutoSettleNotIn(List<Integer> values) {
            addCriterion("is_auto_settle not in", values, "isAutoSettle");
            return (Criteria) this;
        }

        public Criteria andIsAutoSettleBetween(Integer value1, Integer value2) {
            addCriterion("is_auto_settle between", value1, value2, "isAutoSettle");
            return (Criteria) this;
        }

        public Criteria andIsAutoSettleNotBetween(Integer value1, Integer value2) {
            addCriterion("is_auto_settle not between", value1, value2, "isAutoSettle");
            return (Criteria) this;
        }

        public Criteria andSettleReasonIsNull() {
            addCriterion("settle_reason is null");
            return (Criteria) this;
        }

        public Criteria andSettleReasonIsNotNull() {
            addCriterion("settle_reason is not null");
            return (Criteria) this;
        }

        public Criteria andSettleReasonEqualTo(Integer value) {
            addCriterion("settle_reason =", value, "settleReason");
            return (Criteria) this;
        }

        public Criteria andSettleReasonNotEqualTo(Integer value) {
            addCriterion("settle_reason <>", value, "settleReason");
            return (Criteria) this;
        }

        public Criteria andSettleReasonGreaterThan(Integer value) {
            addCriterion("settle_reason >", value, "settleReason");
            return (Criteria) this;
        }

        public Criteria andSettleReasonGreaterThanOrEqualTo(Integer value) {
            addCriterion("settle_reason >=", value, "settleReason");
            return (Criteria) this;
        }

        public Criteria andSettleReasonLessThan(Integer value) {
            addCriterion("settle_reason <", value, "settleReason");
            return (Criteria) this;
        }

        public Criteria andSettleReasonLessThanOrEqualTo(Integer value) {
            addCriterion("settle_reason <=", value, "settleReason");
            return (Criteria) this;
        }

        public Criteria andSettleReasonIn(List<Integer> values) {
            addCriterion("settle_reason in", values, "settleReason");
            return (Criteria) this;
        }

        public Criteria andSettleReasonNotIn(List<Integer> values) {
            addCriterion("settle_reason not in", values, "settleReason");
            return (Criteria) this;
        }

        public Criteria andSettleReasonBetween(Integer value1, Integer value2) {
            addCriterion("settle_reason between", value1, value2, "settleReason");
            return (Criteria) this;
        }

        public Criteria andSettleReasonNotBetween(Integer value1, Integer value2) {
            addCriterion("settle_reason not between", value1, value2, "settleReason");
            return (Criteria) this;
        }

        public Criteria andSettleReasonDetailIsNull() {
            addCriterion("settle_reason_detail is null");
            return (Criteria) this;
        }

        public Criteria andSettleReasonDetailIsNotNull() {
            addCriterion("settle_reason_detail is not null");
            return (Criteria) this;
        }

        public Criteria andSettleReasonDetailEqualTo(String value) {
            addCriterion("settle_reason_detail =", value, "settleReasonDetail");
            return (Criteria) this;
        }

        public Criteria andSettleReasonDetailNotEqualTo(String value) {
            addCriterion("settle_reason_detail <>", value, "settleReasonDetail");
            return (Criteria) this;
        }

        public Criteria andSettleReasonDetailGreaterThan(String value) {
            addCriterion("settle_reason_detail >", value, "settleReasonDetail");
            return (Criteria) this;
        }

        public Criteria andSettleReasonDetailGreaterThanOrEqualTo(String value) {
            addCriterion("settle_reason_detail >=", value, "settleReasonDetail");
            return (Criteria) this;
        }

        public Criteria andSettleReasonDetailLessThan(String value) {
            addCriterion("settle_reason_detail <", value, "settleReasonDetail");
            return (Criteria) this;
        }

        public Criteria andSettleReasonDetailLessThanOrEqualTo(String value) {
            addCriterion("settle_reason_detail <=", value, "settleReasonDetail");
            return (Criteria) this;
        }

        public Criteria andSettleReasonDetailLike(String value) {
            addCriterion("settle_reason_detail like", value, "settleReasonDetail");
            return (Criteria) this;
        }

        public Criteria andSettleReasonDetailNotLike(String value) {
            addCriterion("settle_reason_detail not like", value, "settleReasonDetail");
            return (Criteria) this;
        }

        public Criteria andSettleReasonDetailIn(List<String> values) {
            addCriterion("settle_reason_detail in", values, "settleReasonDetail");
            return (Criteria) this;
        }

        public Criteria andSettleReasonDetailNotIn(List<String> values) {
            addCriterion("settle_reason_detail not in", values, "settleReasonDetail");
            return (Criteria) this;
        }

        public Criteria andSettleReasonDetailBetween(String value1, String value2) {
            addCriterion("settle_reason_detail between", value1, value2, "settleReasonDetail");
            return (Criteria) this;
        }

        public Criteria andSettleReasonDetailNotBetween(String value1, String value2) {
            addCriterion("settle_reason_detail not between", value1, value2, "settleReasonDetail");
            return (Criteria) this;
        }

        public Criteria andSettleFreezeIsNull() {
            addCriterion("settle_freeze is null");
            return (Criteria) this;
        }

        public Criteria andSettleFreezeIsNotNull() {
            addCriterion("settle_freeze is not null");
            return (Criteria) this;
        }

        public Criteria andSettleFreezeEqualTo(Integer value) {
            addCriterion("settle_freeze =", value, "settleFreeze");
            return (Criteria) this;
        }

        public Criteria andSettleFreezeNotEqualTo(Integer value) {
            addCriterion("settle_freeze <>", value, "settleFreeze");
            return (Criteria) this;
        }

        public Criteria andSettleFreezeGreaterThan(Integer value) {
            addCriterion("settle_freeze >", value, "settleFreeze");
            return (Criteria) this;
        }

        public Criteria andSettleFreezeGreaterThanOrEqualTo(Integer value) {
            addCriterion("settle_freeze >=", value, "settleFreeze");
            return (Criteria) this;
        }

        public Criteria andSettleFreezeLessThan(Integer value) {
            addCriterion("settle_freeze <", value, "settleFreeze");
            return (Criteria) this;
        }

        public Criteria andSettleFreezeLessThanOrEqualTo(Integer value) {
            addCriterion("settle_freeze <=", value, "settleFreeze");
            return (Criteria) this;
        }

        public Criteria andSettleFreezeIn(List<Integer> values) {
            addCriterion("settle_freeze in", values, "settleFreeze");
            return (Criteria) this;
        }

        public Criteria andSettleFreezeNotIn(List<Integer> values) {
            addCriterion("settle_freeze not in", values, "settleFreeze");
            return (Criteria) this;
        }

        public Criteria andSettleFreezeBetween(Integer value1, Integer value2) {
            addCriterion("settle_freeze between", value1, value2, "settleFreeze");
            return (Criteria) this;
        }

        public Criteria andSettleFreezeNotBetween(Integer value1, Integer value2) {
            addCriterion("settle_freeze not between", value1, value2, "settleFreeze");
            return (Criteria) this;
        }

        public Criteria andOperaterIsNull() {
            addCriterion("operater is null");
            return (Criteria) this;
        }

        public Criteria andOperaterIsNotNull() {
            addCriterion("operater is not null");
            return (Criteria) this;
        }

        public Criteria andOperaterEqualTo(String value) {
            addCriterion("operater =", value, "operater");
            return (Criteria) this;
        }

        public Criteria andOperaterNotEqualTo(String value) {
            addCriterion("operater <>", value, "operater");
            return (Criteria) this;
        }

        public Criteria andOperaterGreaterThan(String value) {
            addCriterion("operater >", value, "operater");
            return (Criteria) this;
        }

        public Criteria andOperaterGreaterThanOrEqualTo(String value) {
            addCriterion("operater >=", value, "operater");
            return (Criteria) this;
        }

        public Criteria andOperaterLessThan(String value) {
            addCriterion("operater <", value, "operater");
            return (Criteria) this;
        }

        public Criteria andOperaterLessThanOrEqualTo(String value) {
            addCriterion("operater <=", value, "operater");
            return (Criteria) this;
        }

        public Criteria andOperaterLike(String value) {
            addCriterion("operater like", value, "operater");
            return (Criteria) this;
        }

        public Criteria andOperaterNotLike(String value) {
            addCriterion("operater not like", value, "operater");
            return (Criteria) this;
        }

        public Criteria andOperaterIn(List<String> values) {
            addCriterion("operater in", values, "operater");
            return (Criteria) this;
        }

        public Criteria andOperaterNotIn(List<String> values) {
            addCriterion("operater not in", values, "operater");
            return (Criteria) this;
        }

        public Criteria andOperaterBetween(String value1, String value2) {
            addCriterion("operater between", value1, value2, "operater");
            return (Criteria) this;
        }

        public Criteria andOperaterNotBetween(String value1, String value2) {
            addCriterion("operater not between", value1, value2, "operater");
            return (Criteria) this;
        }

        public Criteria andUseridIsNull() {
            addCriterion("userid is null");
            return (Criteria) this;
        }

        public Criteria andUseridIsNotNull() {
            addCriterion("userid is not null");
            return (Criteria) this;
        }

        public Criteria andUseridEqualTo(String value) {
            addCriterion("userid =", value, "userid");
            return (Criteria) this;
        }

        public Criteria andUseridNotEqualTo(String value) {
            addCriterion("userid <>", value, "userid");
            return (Criteria) this;
        }

        public Criteria andUseridGreaterThan(String value) {
            addCriterion("userid >", value, "userid");
            return (Criteria) this;
        }

        public Criteria andUseridGreaterThanOrEqualTo(String value) {
            addCriterion("userid >=", value, "userid");
            return (Criteria) this;
        }

        public Criteria andUseridLessThan(String value) {
            addCriterion("userid <", value, "userid");
            return (Criteria) this;
        }

        public Criteria andUseridLessThanOrEqualTo(String value) {
            addCriterion("userid <=", value, "userid");
            return (Criteria) this;
        }

        public Criteria andUseridLike(String value) {
            addCriterion("userid like", value, "userid");
            return (Criteria) this;
        }

        public Criteria andUseridNotLike(String value) {
            addCriterion("userid not like", value, "userid");
            return (Criteria) this;
        }

        public Criteria andUseridIn(List<String> values) {
            addCriterion("userid in", values, "userid");
            return (Criteria) this;
        }

        public Criteria andUseridNotIn(List<String> values) {
            addCriterion("userid not in", values, "userid");
            return (Criteria) this;
        }

        public Criteria andUseridBetween(String value1, String value2) {
            addCriterion("userid between", value1, value2, "userid");
            return (Criteria) this;
        }

        public Criteria andUseridNotBetween(String value1, String value2) {
            addCriterion("userid not between", value1, value2, "userid");
            return (Criteria) this;
        }

        public Criteria andOperateTypeIsNull() {
            addCriterion("operate_type is null");
            return (Criteria) this;
        }

        public Criteria andOperateTypeIsNotNull() {
            addCriterion("operate_type is not null");
            return (Criteria) this;
        }

        public Criteria andOperateTypeEqualTo(Integer value) {
            addCriterion("operate_type =", value, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeNotEqualTo(Integer value) {
            addCriterion("operate_type <>", value, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeGreaterThan(Integer value) {
            addCriterion("operate_type >", value, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("operate_type >=", value, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeLessThan(Integer value) {
            addCriterion("operate_type <", value, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeLessThanOrEqualTo(Integer value) {
            addCriterion("operate_type <=", value, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeIn(List<Integer> values) {
            addCriterion("operate_type in", values, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeNotIn(List<Integer> values) {
            addCriterion("operate_type not in", values, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeBetween(Integer value1, Integer value2) {
            addCriterion("operate_type between", value1, value2, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("operate_type not between", value1, value2, "operateType");
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