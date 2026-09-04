package com.panda.merge.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MarketCategorySellExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MarketCategorySellExample() {
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

        public Criteria andMatchIdIsNull() {
            addCriterion("match_id is null");
            return (Criteria) this;
        }

        public Criteria andMatchIdIsNotNull() {
            addCriterion("match_id is not null");
            return (Criteria) this;
        }

        public Criteria andMatchIdEqualTo(Long value) {
            addCriterion("match_id =", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdNotEqualTo(Long value) {
            addCriterion("match_id <>", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdGreaterThan(Long value) {
            addCriterion("match_id >", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdGreaterThanOrEqualTo(Long value) {
            addCriterion("match_id >=", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdLessThan(Long value) {
            addCriterion("match_id <", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdLessThanOrEqualTo(Long value) {
            addCriterion("match_id <=", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdIn(List<Long> values) {
            addCriterion("match_id in", values, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdNotIn(List<Long> values) {
            addCriterion("match_id not in", values, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdBetween(Long value1, Long value2) {
            addCriterion("match_id between", value1, value2, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdNotBetween(Long value1, Long value2) {
            addCriterion("match_id not between", value1, value2, "matchId");
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

        public Criteria andMarketCategoryIdIsNull() {
            addCriterion("market_category_id is null");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdIsNotNull() {
            addCriterion("market_category_id is not null");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdEqualTo(Long value) {
            addCriterion("market_category_id =", value, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdNotEqualTo(Long value) {
            addCriterion("market_category_id <>", value, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdGreaterThan(Long value) {
            addCriterion("market_category_id >", value, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdGreaterThanOrEqualTo(Long value) {
            addCriterion("market_category_id >=", value, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdLessThan(Long value) {
            addCriterion("market_category_id <", value, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdLessThanOrEqualTo(Long value) {
            addCriterion("market_category_id <=", value, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdIn(List<Long> values) {
            addCriterion("market_category_id in", values, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdNotIn(List<Long> values) {
            addCriterion("market_category_id not in", values, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdBetween(Long value1, Long value2) {
            addCriterion("market_category_id between", value1, value2, "marketCategoryId");
            return (Criteria) this;
        }

        public Criteria andMarketCategoryIdNotBetween(Long value1, Long value2) {
            addCriterion("market_category_id not between", value1, value2, "marketCategoryId");
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

        public Criteria andSellTimeIsNull() {
            addCriterion("sell_time is null");
            return (Criteria) this;
        }

        public Criteria andSellTimeIsNotNull() {
            addCriterion("sell_time is not null");
            return (Criteria) this;
        }

        public Criteria andSellTimeEqualTo(Long value) {
            addCriterion("sell_time =", value, "sellTime");
            return (Criteria) this;
        }

        public Criteria andSellTimeNotEqualTo(Long value) {
            addCriterion("sell_time <>", value, "sellTime");
            return (Criteria) this;
        }

        public Criteria andSellTimeGreaterThan(Long value) {
            addCriterion("sell_time >", value, "sellTime");
            return (Criteria) this;
        }

        public Criteria andSellTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("sell_time >=", value, "sellTime");
            return (Criteria) this;
        }

        public Criteria andSellTimeLessThan(Long value) {
            addCriterion("sell_time <", value, "sellTime");
            return (Criteria) this;
        }

        public Criteria andSellTimeLessThanOrEqualTo(Long value) {
            addCriterion("sell_time <=", value, "sellTime");
            return (Criteria) this;
        }

        public Criteria andSellTimeIn(List<Long> values) {
            addCriterion("sell_time in", values, "sellTime");
            return (Criteria) this;
        }

        public Criteria andSellTimeNotIn(List<Long> values) {
            addCriterion("sell_time not in", values, "sellTime");
            return (Criteria) this;
        }

        public Criteria andSellTimeBetween(Long value1, Long value2) {
            addCriterion("sell_time between", value1, value2, "sellTime");
            return (Criteria) this;
        }

        public Criteria andSellTimeNotBetween(Long value1, Long value2) {
            addCriterion("sell_time not between", value1, value2, "sellTime");
            return (Criteria) this;
        }

        public Criteria andIsSendIsNull() {
            addCriterion("is_send is null");
            return (Criteria) this;
        }

        public Criteria andIsSendIsNotNull() {
            addCriterion("is_send is not null");
            return (Criteria) this;
        }

        public Criteria andIsSendEqualTo(Integer value) {
            addCriterion("is_send =", value, "isSend");
            return (Criteria) this;
        }

        public Criteria andIsSendNotEqualTo(Integer value) {
            addCriterion("is_send <>", value, "isSend");
            return (Criteria) this;
        }

        public Criteria andIsSendGreaterThan(Integer value) {
            addCriterion("is_send >", value, "isSend");
            return (Criteria) this;
        }

        public Criteria andIsSendGreaterThanOrEqualTo(Integer value) {
            addCriterion("is_send >=", value, "isSend");
            return (Criteria) this;
        }

        public Criteria andIsSendLessThan(Integer value) {
            addCriterion("is_send <", value, "isSend");
            return (Criteria) this;
        }

        public Criteria andIsSendLessThanOrEqualTo(Integer value) {
            addCriterion("is_send <=", value, "isSend");
            return (Criteria) this;
        }

        public Criteria andIsSendIn(List<Integer> values) {
            addCriterion("is_send in", values, "isSend");
            return (Criteria) this;
        }

        public Criteria andIsSendNotIn(List<Integer> values) {
            addCriterion("is_send not in", values, "isSend");
            return (Criteria) this;
        }

        public Criteria andIsSendBetween(Integer value1, Integer value2) {
            addCriterion("is_send between", value1, value2, "isSend");
            return (Criteria) this;
        }

        public Criteria andIsSendNotBetween(Integer value1, Integer value2) {
            addCriterion("is_send not between", value1, value2, "isSend");
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

        public Criteria andMarketCountIsNull() {
            addCriterion("market_count is null");
            return (Criteria) this;
        }

        public Criteria andMarketCountIsNotNull() {
            addCriterion("market_count is not null");
            return (Criteria) this;
        }

        public Criteria andMarketCountEqualTo(Integer value) {
            addCriterion("market_count =", value, "marketCount");
            return (Criteria) this;
        }

        public Criteria andMarketCountNotEqualTo(Integer value) {
            addCriterion("market_count <>", value, "marketCount");
            return (Criteria) this;
        }

        public Criteria andMarketCountGreaterThan(Integer value) {
            addCriterion("market_count >", value, "marketCount");
            return (Criteria) this;
        }

        public Criteria andMarketCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("market_count >=", value, "marketCount");
            return (Criteria) this;
        }

        public Criteria andMarketCountLessThan(Integer value) {
            addCriterion("market_count <", value, "marketCount");
            return (Criteria) this;
        }

        public Criteria andMarketCountLessThanOrEqualTo(Integer value) {
            addCriterion("market_count <=", value, "marketCount");
            return (Criteria) this;
        }

        public Criteria andMarketCountIn(List<Integer> values) {
            addCriterion("market_count in", values, "marketCount");
            return (Criteria) this;
        }

        public Criteria andMarketCountNotIn(List<Integer> values) {
            addCriterion("market_count not in", values, "marketCount");
            return (Criteria) this;
        }

        public Criteria andMarketCountBetween(Integer value1, Integer value2) {
            addCriterion("market_count between", value1, value2, "marketCount");
            return (Criteria) this;
        }

        public Criteria andMarketCountNotBetween(Integer value1, Integer value2) {
            addCriterion("market_count not between", value1, value2, "marketCount");
            return (Criteria) this;
        }

        public Criteria andIsSeriesIsNull() {
            addCriterion("is_series is null");
            return (Criteria) this;
        }

        public Criteria andIsSeriesIsNotNull() {
            addCriterion("is_series is not null");
            return (Criteria) this;
        }

        public Criteria andIsSeriesEqualTo(Integer value) {
            addCriterion("is_series =", value, "isSeries");
            return (Criteria) this;
        }

        public Criteria andIsSeriesNotEqualTo(Integer value) {
            addCriterion("is_series <>", value, "isSeries");
            return (Criteria) this;
        }

        public Criteria andIsSeriesGreaterThan(Integer value) {
            addCriterion("is_series >", value, "isSeries");
            return (Criteria) this;
        }

        public Criteria andIsSeriesGreaterThanOrEqualTo(Integer value) {
            addCriterion("is_series >=", value, "isSeries");
            return (Criteria) this;
        }

        public Criteria andIsSeriesLessThan(Integer value) {
            addCriterion("is_series <", value, "isSeries");
            return (Criteria) this;
        }

        public Criteria andIsSeriesLessThanOrEqualTo(Integer value) {
            addCriterion("is_series <=", value, "isSeries");
            return (Criteria) this;
        }

        public Criteria andIsSeriesIn(List<Integer> values) {
            addCriterion("is_series in", values, "isSeries");
            return (Criteria) this;
        }

        public Criteria andIsSeriesNotIn(List<Integer> values) {
            addCriterion("is_series not in", values, "isSeries");
            return (Criteria) this;
        }

        public Criteria andIsSeriesBetween(Integer value1, Integer value2) {
            addCriterion("is_series between", value1, value2, "isSeries");
            return (Criteria) this;
        }

        public Criteria andIsSeriesNotBetween(Integer value1, Integer value2) {
            addCriterion("is_series not between", value1, value2, "isSeries");
            return (Criteria) this;
        }

        public Criteria andAutoCloseMarketIsNull() {
            addCriterion("auto_close_Market is null");
            return (Criteria) this;
        }

        public Criteria andAutoCloseMarketIsNotNull() {
            addCriterion("auto_close_Market is not null");
            return (Criteria) this;
        }

        public Criteria andAutoCloseMarketEqualTo(Integer value) {
            addCriterion("auto_close_Market =", value, "autoCloseMarket");
            return (Criteria) this;
        }

        public Criteria andAutoCloseMarketNotEqualTo(Integer value) {
            addCriterion("auto_close_Market <>", value, "autoCloseMarket");
            return (Criteria) this;
        }

        public Criteria andAutoCloseMarketGreaterThan(Integer value) {
            addCriterion("auto_close_Market >", value, "autoCloseMarket");
            return (Criteria) this;
        }

        public Criteria andAutoCloseMarketGreaterThanOrEqualTo(Integer value) {
            addCriterion("auto_close_Market >=", value, "autoCloseMarket");
            return (Criteria) this;
        }

        public Criteria andAutoCloseMarketLessThan(Integer value) {
            addCriterion("auto_close_Market <", value, "autoCloseMarket");
            return (Criteria) this;
        }

        public Criteria andAutoCloseMarketLessThanOrEqualTo(Integer value) {
            addCriterion("auto_close_Market <=", value, "autoCloseMarket");
            return (Criteria) this;
        }

        public Criteria andAutoCloseMarketIn(List<Integer> values) {
            addCriterion("auto_close_Market in", values, "autoCloseMarket");
            return (Criteria) this;
        }

        public Criteria andAutoCloseMarketNotIn(List<Integer> values) {
            addCriterion("auto_close_Market not in", values, "autoCloseMarket");
            return (Criteria) this;
        }

        public Criteria andAutoCloseMarketBetween(Integer value1, Integer value2) {
            addCriterion("auto_close_Market between", value1, value2, "autoCloseMarket");
            return (Criteria) this;
        }

        public Criteria andAutoCloseMarketNotBetween(Integer value1, Integer value2) {
            addCriterion("auto_close_Market not between", value1, value2, "autoCloseMarket");
            return (Criteria) this;
        }

        public Criteria andMatchProgressTimeIsNull() {
            addCriterion("match_progress_time is null");
            return (Criteria) this;
        }

        public Criteria andMatchProgressTimeIsNotNull() {
            addCriterion("match_progress_time is not null");
            return (Criteria) this;
        }

        public Criteria andMatchProgressTimeEqualTo(Integer value) {
            addCriterion("match_progress_time =", value, "matchProgressTime");
            return (Criteria) this;
        }

        public Criteria andMatchProgressTimeNotEqualTo(Integer value) {
            addCriterion("match_progress_time <>", value, "matchProgressTime");
            return (Criteria) this;
        }

        public Criteria andMatchProgressTimeGreaterThan(Integer value) {
            addCriterion("match_progress_time >", value, "matchProgressTime");
            return (Criteria) this;
        }

        public Criteria andMatchProgressTimeGreaterThanOrEqualTo(Integer value) {
            addCriterion("match_progress_time >=", value, "matchProgressTime");
            return (Criteria) this;
        }

        public Criteria andMatchProgressTimeLessThan(Integer value) {
            addCriterion("match_progress_time <", value, "matchProgressTime");
            return (Criteria) this;
        }

        public Criteria andMatchProgressTimeLessThanOrEqualTo(Integer value) {
            addCriterion("match_progress_time <=", value, "matchProgressTime");
            return (Criteria) this;
        }

        public Criteria andMatchProgressTimeIn(List<Integer> values) {
            addCriterion("match_progress_time in", values, "matchProgressTime");
            return (Criteria) this;
        }

        public Criteria andMatchProgressTimeNotIn(List<Integer> values) {
            addCriterion("match_progress_time not in", values, "matchProgressTime");
            return (Criteria) this;
        }

        public Criteria andMatchProgressTimeBetween(Integer value1, Integer value2) {
            addCriterion("match_progress_time between", value1, value2, "matchProgressTime");
            return (Criteria) this;
        }

        public Criteria andMatchProgressTimeNotBetween(Integer value1, Integer value2) {
            addCriterion("match_progress_time not between", value1, value2, "matchProgressTime");
            return (Criteria) this;
        }

        public Criteria andInjuryTimeIsNull() {
            addCriterion("injury_time is null");
            return (Criteria) this;
        }

        public Criteria andInjuryTimeIsNotNull() {
            addCriterion("injury_time is not null");
            return (Criteria) this;
        }

        public Criteria andInjuryTimeEqualTo(Integer value) {
            addCriterion("injury_time =", value, "injuryTime");
            return (Criteria) this;
        }

        public Criteria andInjuryTimeNotEqualTo(Integer value) {
            addCriterion("injury_time <>", value, "injuryTime");
            return (Criteria) this;
        }

        public Criteria andInjuryTimeGreaterThan(Integer value) {
            addCriterion("injury_time >", value, "injuryTime");
            return (Criteria) this;
        }

        public Criteria andInjuryTimeGreaterThanOrEqualTo(Integer value) {
            addCriterion("injury_time >=", value, "injuryTime");
            return (Criteria) this;
        }

        public Criteria andInjuryTimeLessThan(Integer value) {
            addCriterion("injury_time <", value, "injuryTime");
            return (Criteria) this;
        }

        public Criteria andInjuryTimeLessThanOrEqualTo(Integer value) {
            addCriterion("injury_time <=", value, "injuryTime");
            return (Criteria) this;
        }

        public Criteria andInjuryTimeIn(List<Integer> values) {
            addCriterion("injury_time in", values, "injuryTime");
            return (Criteria) this;
        }

        public Criteria andInjuryTimeNotIn(List<Integer> values) {
            addCriterion("injury_time not in", values, "injuryTime");
            return (Criteria) this;
        }

        public Criteria andInjuryTimeBetween(Integer value1, Integer value2) {
            addCriterion("injury_time between", value1, value2, "injuryTime");
            return (Criteria) this;
        }

        public Criteria andInjuryTimeNotBetween(Integer value1, Integer value2) {
            addCriterion("injury_time not between", value1, value2, "injuryTime");
            return (Criteria) this;
        }

        public Criteria andMarketNearDiffIsNull() {
            addCriterion("market_near_diff is null");
            return (Criteria) this;
        }

        public Criteria andMarketNearDiffIsNotNull() {
            addCriterion("market_near_diff is not null");
            return (Criteria) this;
        }

        public Criteria andMarketNearDiffEqualTo(BigDecimal value) {
            addCriterion("market_near_diff =", value, "marketNearDiff");
            return (Criteria) this;
        }

        public Criteria andMarketNearDiffNotEqualTo(BigDecimal value) {
            addCriterion("market_near_diff <>", value, "marketNearDiff");
            return (Criteria) this;
        }

        public Criteria andMarketNearDiffGreaterThan(BigDecimal value) {
            addCriterion("market_near_diff >", value, "marketNearDiff");
            return (Criteria) this;
        }

        public Criteria andMarketNearDiffGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("market_near_diff >=", value, "marketNearDiff");
            return (Criteria) this;
        }

        public Criteria andMarketNearDiffLessThan(BigDecimal value) {
            addCriterion("market_near_diff <", value, "marketNearDiff");
            return (Criteria) this;
        }

        public Criteria andMarketNearDiffLessThanOrEqualTo(BigDecimal value) {
            addCriterion("market_near_diff <=", value, "marketNearDiff");
            return (Criteria) this;
        }

        public Criteria andMarketNearDiffIn(List<BigDecimal> values) {
            addCriterion("market_near_diff in", values, "marketNearDiff");
            return (Criteria) this;
        }

        public Criteria andMarketNearDiffNotIn(List<BigDecimal> values) {
            addCriterion("market_near_diff not in", values, "marketNearDiff");
            return (Criteria) this;
        }

        public Criteria andMarketNearDiffBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("market_near_diff between", value1, value2, "marketNearDiff");
            return (Criteria) this;
        }

        public Criteria andMarketNearDiffNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("market_near_diff not between", value1, value2, "marketNearDiff");
            return (Criteria) this;
        }

        public Criteria andMarketNearOddsDiffIsNull() {
            addCriterion("market_near_odds_diff is null");
            return (Criteria) this;
        }

        public Criteria andMarketNearOddsDiffIsNotNull() {
            addCriterion("market_near_odds_diff is not null");
            return (Criteria) this;
        }

        public Criteria andMarketNearOddsDiffEqualTo(BigDecimal value) {
            addCriterion("market_near_odds_diff =", value, "marketNearOddsDiff");
            return (Criteria) this;
        }

        public Criteria andMarketNearOddsDiffNotEqualTo(BigDecimal value) {
            addCriterion("market_near_odds_diff <>", value, "marketNearOddsDiff");
            return (Criteria) this;
        }

        public Criteria andMarketNearOddsDiffGreaterThan(BigDecimal value) {
            addCriterion("market_near_odds_diff >", value, "marketNearOddsDiff");
            return (Criteria) this;
        }

        public Criteria andMarketNearOddsDiffGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("market_near_odds_diff >=", value, "marketNearOddsDiff");
            return (Criteria) this;
        }

        public Criteria andMarketNearOddsDiffLessThan(BigDecimal value) {
            addCriterion("market_near_odds_diff <", value, "marketNearOddsDiff");
            return (Criteria) this;
        }

        public Criteria andMarketNearOddsDiffLessThanOrEqualTo(BigDecimal value) {
            addCriterion("market_near_odds_diff <=", value, "marketNearOddsDiff");
            return (Criteria) this;
        }

        public Criteria andMarketNearOddsDiffIn(List<BigDecimal> values) {
            addCriterion("market_near_odds_diff in", values, "marketNearOddsDiff");
            return (Criteria) this;
        }

        public Criteria andMarketNearOddsDiffNotIn(List<BigDecimal> values) {
            addCriterion("market_near_odds_diff not in", values, "marketNearOddsDiff");
            return (Criteria) this;
        }

        public Criteria andMarketNearOddsDiffBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("market_near_odds_diff between", value1, value2, "marketNearOddsDiff");
            return (Criteria) this;
        }

        public Criteria andMarketNearOddsDiffNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("market_near_odds_diff not between", value1, value2, "marketNearOddsDiff");
            return (Criteria) this;
        }

        public Criteria andIsSellIsNull() {
            addCriterion("is_sell is null");
            return (Criteria) this;
        }

        public Criteria andIsSellIsNotNull() {
            addCriterion("is_sell is not null");
            return (Criteria) this;
        }

        public Criteria andIsSellEqualTo(Integer value) {
            addCriterion("is_sell =", value, "isSell");
            return (Criteria) this;
        }

        public Criteria andIsSellNotEqualTo(Integer value) {
            addCriterion("is_sell <>", value, "isSell");
            return (Criteria) this;
        }

        public Criteria andIsSellGreaterThan(Integer value) {
            addCriterion("is_sell >", value, "isSell");
            return (Criteria) this;
        }

        public Criteria andIsSellGreaterThanOrEqualTo(Integer value) {
            addCriterion("is_sell >=", value, "isSell");
            return (Criteria) this;
        }

        public Criteria andIsSellLessThan(Integer value) {
            addCriterion("is_sell <", value, "isSell");
            return (Criteria) this;
        }

        public Criteria andIsSellLessThanOrEqualTo(Integer value) {
            addCriterion("is_sell <=", value, "isSell");
            return (Criteria) this;
        }

        public Criteria andIsSellIn(List<Integer> values) {
            addCriterion("is_sell in", values, "isSell");
            return (Criteria) this;
        }

        public Criteria andIsSellNotIn(List<Integer> values) {
            addCriterion("is_sell not in", values, "isSell");
            return (Criteria) this;
        }

        public Criteria andIsSellBetween(Integer value1, Integer value2) {
            addCriterion("is_sell between", value1, value2, "isSell");
            return (Criteria) this;
        }

        public Criteria andIsSellNotBetween(Integer value1, Integer value2) {
            addCriterion("is_sell not between", value1, value2, "isSell");
            return (Criteria) this;
        }

        public Criteria andLinkIdIsNull() {
            addCriterion("link_id is null");
            return (Criteria) this;
        }

        public Criteria andLinkIdIsNotNull() {
            addCriterion("link_id is not null");
            return (Criteria) this;
        }

        public Criteria andLinkIdEqualTo(String value) {
            addCriterion("link_id =", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdNotEqualTo(String value) {
            addCriterion("link_id <>", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdGreaterThan(String value) {
            addCriterion("link_id >", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdGreaterThanOrEqualTo(String value) {
            addCriterion("link_id >=", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdLessThan(String value) {
            addCriterion("link_id <", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdLessThanOrEqualTo(String value) {
            addCriterion("link_id <=", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdLike(String value) {
            addCriterion("link_id like", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdNotLike(String value) {
            addCriterion("link_id not like", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdIn(List<String> values) {
            addCriterion("link_id in", values, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdNotIn(List<String> values) {
            addCriterion("link_id not in", values, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdBetween(String value1, String value2) {
            addCriterion("link_id between", value1, value2, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdNotBetween(String value1, String value2) {
            addCriterion("link_id not between", value1, value2, "linkId");
            return (Criteria) this;
        }

        public Criteria andIsSpecialPumpingIsNull() {
            addCriterion("is_special_pumping is null");
            return (Criteria) this;
        }

        public Criteria andIsSpecialPumpingIsNotNull() {
            addCriterion("is_special_pumping is not null");
            return (Criteria) this;
        }

        public Criteria andIsSpecialPumpingEqualTo(Integer value) {
            addCriterion("is_special_pumping =", value, "isSpecialPumping");
            return (Criteria) this;
        }

        public Criteria andIsSpecialPumpingNotEqualTo(Integer value) {
            addCriterion("is_special_pumping <>", value, "isSpecialPumping");
            return (Criteria) this;
        }

        public Criteria andIsSpecialPumpingGreaterThan(Integer value) {
            addCriterion("is_special_pumping >", value, "isSpecialPumping");
            return (Criteria) this;
        }

        public Criteria andIsSpecialPumpingGreaterThanOrEqualTo(Integer value) {
            addCriterion("is_special_pumping >=", value, "isSpecialPumping");
            return (Criteria) this;
        }

        public Criteria andIsSpecialPumpingLessThan(Integer value) {
            addCriterion("is_special_pumping <", value, "isSpecialPumping");
            return (Criteria) this;
        }

        public Criteria andIsSpecialPumpingLessThanOrEqualTo(Integer value) {
            addCriterion("is_special_pumping <=", value, "isSpecialPumping");
            return (Criteria) this;
        }

        public Criteria andIsSpecialPumpingIn(List<Integer> values) {
            addCriterion("is_special_pumping in", values, "isSpecialPumping");
            return (Criteria) this;
        }

        public Criteria andIsSpecialPumpingNotIn(List<Integer> values) {
            addCriterion("is_special_pumping not in", values, "isSpecialPumping");
            return (Criteria) this;
        }

        public Criteria andIsSpecialPumpingBetween(Integer value1, Integer value2) {
            addCriterion("is_special_pumping between", value1, value2, "isSpecialPumping");
            return (Criteria) this;
        }

        public Criteria andIsSpecialPumpingNotBetween(Integer value1, Integer value2) {
            addCriterion("is_special_pumping not between", value1, value2, "isSpecialPumping");
            return (Criteria) this;
        }

        public Criteria andSpecialOddsIntervalIsNull() {
            addCriterion("special_odds_interval is null");
            return (Criteria) this;
        }

        public Criteria andSpecialOddsIntervalIsNotNull() {
            addCriterion("special_odds_interval is not null");
            return (Criteria) this;
        }

        public Criteria andSpecialOddsIntervalEqualTo(String value) {
            addCriterion("special_odds_interval =", value, "specialOddsInterval");
            return (Criteria) this;
        }

        public Criteria andSpecialOddsIntervalNotEqualTo(String value) {
            addCriterion("special_odds_interval <>", value, "specialOddsInterval");
            return (Criteria) this;
        }

        public Criteria andSpecialOddsIntervalGreaterThan(String value) {
            addCriterion("special_odds_interval >", value, "specialOddsInterval");
            return (Criteria) this;
        }

        public Criteria andSpecialOddsIntervalGreaterThanOrEqualTo(String value) {
            addCriterion("special_odds_interval >=", value, "specialOddsInterval");
            return (Criteria) this;
        }

        public Criteria andSpecialOddsIntervalLessThan(String value) {
            addCriterion("special_odds_interval <", value, "specialOddsInterval");
            return (Criteria) this;
        }

        public Criteria andSpecialOddsIntervalLessThanOrEqualTo(String value) {
            addCriterion("special_odds_interval <=", value, "specialOddsInterval");
            return (Criteria) this;
        }

        public Criteria andSpecialOddsIntervalLike(String value) {
            addCriterion("special_odds_interval like", value, "specialOddsInterval");
            return (Criteria) this;
        }

        public Criteria andSpecialOddsIntervalNotLike(String value) {
            addCriterion("special_odds_interval not like", value, "specialOddsInterval");
            return (Criteria) this;
        }

        public Criteria andSpecialOddsIntervalIn(List<String> values) {
            addCriterion("special_odds_interval in", values, "specialOddsInterval");
            return (Criteria) this;
        }

        public Criteria andSpecialOddsIntervalNotIn(List<String> values) {
            addCriterion("special_odds_interval not in", values, "specialOddsInterval");
            return (Criteria) this;
        }

        public Criteria andSpecialOddsIntervalBetween(String value1, String value2) {
            addCriterion("special_odds_interval between", value1, value2, "specialOddsInterval");
            return (Criteria) this;
        }

        public Criteria andSpecialOddsIntervalNotBetween(String value1, String value2) {
            addCriterion("special_odds_interval not between", value1, value2, "specialOddsInterval");
            return (Criteria) this;
        }

        public Criteria andMinBallHeadIsNull() {
            addCriterion("min_ball_head is null");
            return (Criteria) this;
        }

        public Criteria andMinBallHeadIsNotNull() {
            addCriterion("min_ball_head is not null");
            return (Criteria) this;
        }

        public Criteria andMinBallHeadEqualTo(BigDecimal value) {
            addCriterion("min_ball_head =", value, "minBallHead");
            return (Criteria) this;
        }

        public Criteria andMinBallHeadNotEqualTo(BigDecimal value) {
            addCriterion("min_ball_head <>", value, "minBallHead");
            return (Criteria) this;
        }

        public Criteria andMinBallHeadGreaterThan(BigDecimal value) {
            addCriterion("min_ball_head >", value, "minBallHead");
            return (Criteria) this;
        }

        public Criteria andMinBallHeadGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("min_ball_head >=", value, "minBallHead");
            return (Criteria) this;
        }

        public Criteria andMinBallHeadLessThan(BigDecimal value) {
            addCriterion("min_ball_head <", value, "minBallHead");
            return (Criteria) this;
        }

        public Criteria andMinBallHeadLessThanOrEqualTo(BigDecimal value) {
            addCriterion("min_ball_head <=", value, "minBallHead");
            return (Criteria) this;
        }

        public Criteria andMinBallHeadIn(List<BigDecimal> values) {
            addCriterion("min_ball_head in", values, "minBallHead");
            return (Criteria) this;
        }

        public Criteria andMinBallHeadNotIn(List<BigDecimal> values) {
            addCriterion("min_ball_head not in", values, "minBallHead");
            return (Criteria) this;
        }

        public Criteria andMinBallHeadBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("min_ball_head between", value1, value2, "minBallHead");
            return (Criteria) this;
        }

        public Criteria andMinBallHeadNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("min_ball_head not between", value1, value2, "minBallHead");
            return (Criteria) this;
        }

        public Criteria andMaxBallHeadIsNull() {
            addCriterion("max_ball_head is null");
            return (Criteria) this;
        }

        public Criteria andMaxBallHeadIsNotNull() {
            addCriterion("max_ball_head is not null");
            return (Criteria) this;
        }

        public Criteria andMaxBallHeadEqualTo(BigDecimal value) {
            addCriterion("max_ball_head =", value, "maxBallHead");
            return (Criteria) this;
        }

        public Criteria andMaxBallHeadNotEqualTo(BigDecimal value) {
            addCriterion("max_ball_head <>", value, "maxBallHead");
            return (Criteria) this;
        }

        public Criteria andMaxBallHeadGreaterThan(BigDecimal value) {
            addCriterion("max_ball_head >", value, "maxBallHead");
            return (Criteria) this;
        }

        public Criteria andMaxBallHeadGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("max_ball_head >=", value, "maxBallHead");
            return (Criteria) this;
        }

        public Criteria andMaxBallHeadLessThan(BigDecimal value) {
            addCriterion("max_ball_head <", value, "maxBallHead");
            return (Criteria) this;
        }

        public Criteria andMaxBallHeadLessThanOrEqualTo(BigDecimal value) {
            addCriterion("max_ball_head <=", value, "maxBallHead");
            return (Criteria) this;
        }

        public Criteria andMaxBallHeadIn(List<BigDecimal> values) {
            addCriterion("max_ball_head in", values, "maxBallHead");
            return (Criteria) this;
        }

        public Criteria andMaxBallHeadNotIn(List<BigDecimal> values) {
            addCriterion("max_ball_head not in", values, "maxBallHead");
            return (Criteria) this;
        }

        public Criteria andMaxBallHeadBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("max_ball_head between", value1, value2, "maxBallHead");
            return (Criteria) this;
        }

        public Criteria andMaxBallHeadNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("max_ball_head not between", value1, value2, "maxBallHead");
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

        public Criteria andAutoOpenMarketIsNull() {
            addCriterion("auto_open_market is null");
            return (Criteria) this;
        }

        public Criteria andAutoOpenMarketIsNotNull() {
            addCriterion("auto_open_market is not null");
            return (Criteria) this;
        }

        public Criteria andAutoOpenMarketEqualTo(Integer value) {
            addCriterion("auto_open_market =", value, "autoOpenMarket");
            return (Criteria) this;
        }

        public Criteria andAutoOpenMarketNotEqualTo(Integer value) {
            addCriterion("auto_open_market <>", value, "autoOpenMarket");
            return (Criteria) this;
        }

        public Criteria andAutoOpenMarketGreaterThan(Integer value) {
            addCriterion("auto_open_market >", value, "autoOpenMarket");
            return (Criteria) this;
        }

        public Criteria andAutoOpenMarketGreaterThanOrEqualTo(Integer value) {
            addCriterion("auto_open_market >=", value, "autoOpenMarket");
            return (Criteria) this;
        }

        public Criteria andAutoOpenMarketLessThan(Integer value) {
            addCriterion("auto_open_market <", value, "autoOpenMarket");
            return (Criteria) this;
        }

        public Criteria andAutoOpenMarketLessThanOrEqualTo(Integer value) {
            addCriterion("auto_open_market <=", value, "autoOpenMarket");
            return (Criteria) this;
        }

        public Criteria andAutoOpenMarketIn(List<Integer> values) {
            addCriterion("auto_open_market in", values, "autoOpenMarket");
            return (Criteria) this;
        }

        public Criteria andAutoOpenMarketNotIn(List<Integer> values) {
            addCriterion("auto_open_market not in", values, "autoOpenMarket");
            return (Criteria) this;
        }

        public Criteria andAutoOpenMarketBetween(Integer value1, Integer value2) {
            addCriterion("auto_open_market between", value1, value2, "autoOpenMarket");
            return (Criteria) this;
        }

        public Criteria andAutoOpenMarketNotBetween(Integer value1, Integer value2) {
            addCriterion("auto_open_market not between", value1, value2, "autoOpenMarket");
            return (Criteria) this;
        }

        public Criteria andAutoOpenTimeIsNull() {
            addCriterion("auto_open_time is null");
            return (Criteria) this;
        }

        public Criteria andAutoOpenTimeIsNotNull() {
            addCriterion("auto_open_time is not null");
            return (Criteria) this;
        }

        public Criteria andAutoOpenTimeEqualTo(Integer value) {
            addCriterion("auto_open_time =", value, "autoOpenTime");
            return (Criteria) this;
        }

        public Criteria andAutoOpenTimeNotEqualTo(Integer value) {
            addCriterion("auto_open_time <>", value, "autoOpenTime");
            return (Criteria) this;
        }

        public Criteria andAutoOpenTimeGreaterThan(Integer value) {
            addCriterion("auto_open_time >", value, "autoOpenTime");
            return (Criteria) this;
        }

        public Criteria andAutoOpenTimeGreaterThanOrEqualTo(Integer value) {
            addCriterion("auto_open_time >=", value, "autoOpenTime");
            return (Criteria) this;
        }

        public Criteria andAutoOpenTimeLessThan(Integer value) {
            addCriterion("auto_open_time <", value, "autoOpenTime");
            return (Criteria) this;
        }

        public Criteria andAutoOpenTimeLessThanOrEqualTo(Integer value) {
            addCriterion("auto_open_time <=", value, "autoOpenTime");
            return (Criteria) this;
        }

        public Criteria andAutoOpenTimeIn(List<Integer> values) {
            addCriterion("auto_open_time in", values, "autoOpenTime");
            return (Criteria) this;
        }

        public Criteria andAutoOpenTimeNotIn(List<Integer> values) {
            addCriterion("auto_open_time not in", values, "autoOpenTime");
            return (Criteria) this;
        }

        public Criteria andAutoOpenTimeBetween(Integer value1, Integer value2) {
            addCriterion("auto_open_time between", value1, value2, "autoOpenTime");
            return (Criteria) this;
        }

        public Criteria andAutoOpenTimeNotBetween(Integer value1, Integer value2) {
            addCriterion("auto_open_time not between", value1, value2, "autoOpenTime");
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