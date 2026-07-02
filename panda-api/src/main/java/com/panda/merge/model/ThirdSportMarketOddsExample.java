package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class ThirdSportMarketOddsExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ThirdSportMarketOddsExample() {
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

        public Criteria andSettlementResultTextIsNull() {
            addCriterion("settlement_result_text is null");
            return (Criteria) this;
        }

        public Criteria andSettlementResultTextIsNotNull() {
            addCriterion("settlement_result_text is not null");
            return (Criteria) this;
        }

        public Criteria andSettlementResultTextEqualTo(String value) {
            addCriterion("settlement_result_text =", value, "settlementResultText");
            return (Criteria) this;
        }

        public Criteria andSettlementResultTextNotEqualTo(String value) {
            addCriterion("settlement_result_text <>", value, "settlementResultText");
            return (Criteria) this;
        }

        public Criteria andSettlementResultTextGreaterThan(String value) {
            addCriterion("settlement_result_text >", value, "settlementResultText");
            return (Criteria) this;
        }

        public Criteria andSettlementResultTextGreaterThanOrEqualTo(String value) {
            addCriterion("settlement_result_text >=", value, "settlementResultText");
            return (Criteria) this;
        }

        public Criteria andSettlementResultTextLessThan(String value) {
            addCriterion("settlement_result_text <", value, "settlementResultText");
            return (Criteria) this;
        }

        public Criteria andSettlementResultTextLessThanOrEqualTo(String value) {
            addCriterion("settlement_result_text <=", value, "settlementResultText");
            return (Criteria) this;
        }

        public Criteria andSettlementResultTextLike(String value) {
            addCriterion("settlement_result_text like", value, "settlementResultText");
            return (Criteria) this;
        }

        public Criteria andSettlementResultTextNotLike(String value) {
            addCriterion("settlement_result_text not like", value, "settlementResultText");
            return (Criteria) this;
        }

        public Criteria andSettlementResultTextIn(List<String> values) {
            addCriterion("settlement_result_text in", values, "settlementResultText");
            return (Criteria) this;
        }

        public Criteria andSettlementResultTextNotIn(List<String> values) {
            addCriterion("settlement_result_text not in", values, "settlementResultText");
            return (Criteria) this;
        }

        public Criteria andSettlementResultTextBetween(String value1, String value2) {
            addCriterion("settlement_result_text between", value1, value2, "settlementResultText");
            return (Criteria) this;
        }

        public Criteria andSettlementResultTextNotBetween(String value1, String value2) {
            addCriterion("settlement_result_text not between", value1, value2, "settlementResultText");
            return (Criteria) this;
        }

        public Criteria andSettlementResultIsNull() {
            addCriterion("settlement_result is null");
            return (Criteria) this;
        }

        public Criteria andSettlementResultIsNotNull() {
            addCriterion("settlement_result is not null");
            return (Criteria) this;
        }

        public Criteria andSettlementResultEqualTo(String value) {
            addCriterion("settlement_result =", value, "settlementResult");
            return (Criteria) this;
        }

        public Criteria andSettlementResultNotEqualTo(String value) {
            addCriterion("settlement_result <>", value, "settlementResult");
            return (Criteria) this;
        }

        public Criteria andSettlementResultGreaterThan(String value) {
            addCriterion("settlement_result >", value, "settlementResult");
            return (Criteria) this;
        }

        public Criteria andSettlementResultGreaterThanOrEqualTo(String value) {
            addCriterion("settlement_result >=", value, "settlementResult");
            return (Criteria) this;
        }

        public Criteria andSettlementResultLessThan(String value) {
            addCriterion("settlement_result <", value, "settlementResult");
            return (Criteria) this;
        }

        public Criteria andSettlementResultLessThanOrEqualTo(String value) {
            addCriterion("settlement_result <=", value, "settlementResult");
            return (Criteria) this;
        }

        public Criteria andSettlementResultLike(String value) {
            addCriterion("settlement_result like", value, "settlementResult");
            return (Criteria) this;
        }

        public Criteria andSettlementResultNotLike(String value) {
            addCriterion("settlement_result not like", value, "settlementResult");
            return (Criteria) this;
        }

        public Criteria andSettlementResultIn(List<String> values) {
            addCriterion("settlement_result in", values, "settlementResult");
            return (Criteria) this;
        }

        public Criteria andSettlementResultNotIn(List<String> values) {
            addCriterion("settlement_result not in", values, "settlementResult");
            return (Criteria) this;
        }

        public Criteria andSettlementResultBetween(String value1, String value2) {
            addCriterion("settlement_result between", value1, value2, "settlementResult");
            return (Criteria) this;
        }

        public Criteria andSettlementResultNotBetween(String value1, String value2) {
            addCriterion("settlement_result not between", value1, value2, "settlementResult");
            return (Criteria) this;
        }

        public Criteria andBetSettlementCertaintyIsNull() {
            addCriterion("bet_settlement_certainty is null");
            return (Criteria) this;
        }

        public Criteria andBetSettlementCertaintyIsNotNull() {
            addCriterion("bet_settlement_certainty is not null");
            return (Criteria) this;
        }

        public Criteria andBetSettlementCertaintyEqualTo(String value) {
            addCriterion("bet_settlement_certainty =", value, "betSettlementCertainty");
            return (Criteria) this;
        }

        public Criteria andBetSettlementCertaintyNotEqualTo(String value) {
            addCriterion("bet_settlement_certainty <>", value, "betSettlementCertainty");
            return (Criteria) this;
        }

        public Criteria andBetSettlementCertaintyGreaterThan(String value) {
            addCriterion("bet_settlement_certainty >", value, "betSettlementCertainty");
            return (Criteria) this;
        }

        public Criteria andBetSettlementCertaintyGreaterThanOrEqualTo(String value) {
            addCriterion("bet_settlement_certainty >=", value, "betSettlementCertainty");
            return (Criteria) this;
        }

        public Criteria andBetSettlementCertaintyLessThan(String value) {
            addCriterion("bet_settlement_certainty <", value, "betSettlementCertainty");
            return (Criteria) this;
        }

        public Criteria andBetSettlementCertaintyLessThanOrEqualTo(String value) {
            addCriterion("bet_settlement_certainty <=", value, "betSettlementCertainty");
            return (Criteria) this;
        }

        public Criteria andBetSettlementCertaintyLike(String value) {
            addCriterion("bet_settlement_certainty like", value, "betSettlementCertainty");
            return (Criteria) this;
        }

        public Criteria andBetSettlementCertaintyNotLike(String value) {
            addCriterion("bet_settlement_certainty not like", value, "betSettlementCertainty");
            return (Criteria) this;
        }

        public Criteria andBetSettlementCertaintyIn(List<String> values) {
            addCriterion("bet_settlement_certainty in", values, "betSettlementCertainty");
            return (Criteria) this;
        }

        public Criteria andBetSettlementCertaintyNotIn(List<String> values) {
            addCriterion("bet_settlement_certainty not in", values, "betSettlementCertainty");
            return (Criteria) this;
        }

        public Criteria andBetSettlementCertaintyBetween(String value1, String value2) {
            addCriterion("bet_settlement_certainty between", value1, value2, "betSettlementCertainty");
            return (Criteria) this;
        }

        public Criteria andBetSettlementCertaintyNotBetween(String value1, String value2) {
            addCriterion("bet_settlement_certainty not between", value1, value2, "betSettlementCertainty");
            return (Criteria) this;
        }

        public Criteria andOddsTypeIsNull() {
            addCriterion("odds_type is null");
            return (Criteria) this;
        }

        public Criteria andOddsTypeIsNotNull() {
            addCriterion("odds_type is not null");
            return (Criteria) this;
        }

        public Criteria andOddsTypeEqualTo(String value) {
            addCriterion("odds_type =", value, "oddsType");
            return (Criteria) this;
        }

        public Criteria andOddsTypeNotEqualTo(String value) {
            addCriterion("odds_type <>", value, "oddsType");
            return (Criteria) this;
        }

        public Criteria andOddsTypeGreaterThan(String value) {
            addCriterion("odds_type >", value, "oddsType");
            return (Criteria) this;
        }

        public Criteria andOddsTypeGreaterThanOrEqualTo(String value) {
            addCriterion("odds_type >=", value, "oddsType");
            return (Criteria) this;
        }

        public Criteria andOddsTypeLessThan(String value) {
            addCriterion("odds_type <", value, "oddsType");
            return (Criteria) this;
        }

        public Criteria andOddsTypeLessThanOrEqualTo(String value) {
            addCriterion("odds_type <=", value, "oddsType");
            return (Criteria) this;
        }

        public Criteria andOddsTypeLike(String value) {
            addCriterion("odds_type like", value, "oddsType");
            return (Criteria) this;
        }

        public Criteria andOddsTypeNotLike(String value) {
            addCriterion("odds_type not like", value, "oddsType");
            return (Criteria) this;
        }

        public Criteria andOddsTypeIn(List<String> values) {
            addCriterion("odds_type in", values, "oddsType");
            return (Criteria) this;
        }

        public Criteria andOddsTypeNotIn(List<String> values) {
            addCriterion("odds_type not in", values, "oddsType");
            return (Criteria) this;
        }

        public Criteria andOddsTypeBetween(String value1, String value2) {
            addCriterion("odds_type between", value1, value2, "oddsType");
            return (Criteria) this;
        }

        public Criteria andOddsTypeNotBetween(String value1, String value2) {
            addCriterion("odds_type not between", value1, value2, "oddsType");
            return (Criteria) this;
        }

        public Criteria andAddition1IsNull() {
            addCriterion("addition1 is null");
            return (Criteria) this;
        }

        public Criteria andAddition1IsNotNull() {
            addCriterion("addition1 is not null");
            return (Criteria) this;
        }

        public Criteria andAddition1EqualTo(String value) {
            addCriterion("addition1 =", value, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1NotEqualTo(String value) {
            addCriterion("addition1 <>", value, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1GreaterThan(String value) {
            addCriterion("addition1 >", value, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1GreaterThanOrEqualTo(String value) {
            addCriterion("addition1 >=", value, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1LessThan(String value) {
            addCriterion("addition1 <", value, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1LessThanOrEqualTo(String value) {
            addCriterion("addition1 <=", value, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1Like(String value) {
            addCriterion("addition1 like", value, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1NotLike(String value) {
            addCriterion("addition1 not like", value, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1In(List<String> values) {
            addCriterion("addition1 in", values, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1NotIn(List<String> values) {
            addCriterion("addition1 not in", values, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1Between(String value1, String value2) {
            addCriterion("addition1 between", value1, value2, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition1NotBetween(String value1, String value2) {
            addCriterion("addition1 not between", value1, value2, "addition1");
            return (Criteria) this;
        }

        public Criteria andAddition2IsNull() {
            addCriterion("addition2 is null");
            return (Criteria) this;
        }

        public Criteria andAddition2IsNotNull() {
            addCriterion("addition2 is not null");
            return (Criteria) this;
        }

        public Criteria andAddition2EqualTo(String value) {
            addCriterion("addition2 =", value, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2NotEqualTo(String value) {
            addCriterion("addition2 <>", value, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2GreaterThan(String value) {
            addCriterion("addition2 >", value, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2GreaterThanOrEqualTo(String value) {
            addCriterion("addition2 >=", value, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2LessThan(String value) {
            addCriterion("addition2 <", value, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2LessThanOrEqualTo(String value) {
            addCriterion("addition2 <=", value, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2Like(String value) {
            addCriterion("addition2 like", value, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2NotLike(String value) {
            addCriterion("addition2 not like", value, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2In(List<String> values) {
            addCriterion("addition2 in", values, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2NotIn(List<String> values) {
            addCriterion("addition2 not in", values, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2Between(String value1, String value2) {
            addCriterion("addition2 between", value1, value2, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition2NotBetween(String value1, String value2) {
            addCriterion("addition2 not between", value1, value2, "addition2");
            return (Criteria) this;
        }

        public Criteria andAddition3IsNull() {
            addCriterion("addition3 is null");
            return (Criteria) this;
        }

        public Criteria andAddition3IsNotNull() {
            addCriterion("addition3 is not null");
            return (Criteria) this;
        }

        public Criteria andAddition3EqualTo(String value) {
            addCriterion("addition3 =", value, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3NotEqualTo(String value) {
            addCriterion("addition3 <>", value, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3GreaterThan(String value) {
            addCriterion("addition3 >", value, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3GreaterThanOrEqualTo(String value) {
            addCriterion("addition3 >=", value, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3LessThan(String value) {
            addCriterion("addition3 <", value, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3LessThanOrEqualTo(String value) {
            addCriterion("addition3 <=", value, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3Like(String value) {
            addCriterion("addition3 like", value, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3NotLike(String value) {
            addCriterion("addition3 not like", value, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3In(List<String> values) {
            addCriterion("addition3 in", values, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3NotIn(List<String> values) {
            addCriterion("addition3 not in", values, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3Between(String value1, String value2) {
            addCriterion("addition3 between", value1, value2, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition3NotBetween(String value1, String value2) {
            addCriterion("addition3 not between", value1, value2, "addition3");
            return (Criteria) this;
        }

        public Criteria andAddition4IsNull() {
            addCriterion("addition4 is null");
            return (Criteria) this;
        }

        public Criteria andAddition4IsNotNull() {
            addCriterion("addition4 is not null");
            return (Criteria) this;
        }

        public Criteria andAddition4EqualTo(String value) {
            addCriterion("addition4 =", value, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4NotEqualTo(String value) {
            addCriterion("addition4 <>", value, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4GreaterThan(String value) {
            addCriterion("addition4 >", value, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4GreaterThanOrEqualTo(String value) {
            addCriterion("addition4 >=", value, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4LessThan(String value) {
            addCriterion("addition4 <", value, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4LessThanOrEqualTo(String value) {
            addCriterion("addition4 <=", value, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4Like(String value) {
            addCriterion("addition4 like", value, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4NotLike(String value) {
            addCriterion("addition4 not like", value, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4In(List<String> values) {
            addCriterion("addition4 in", values, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4NotIn(List<String> values) {
            addCriterion("addition4 not in", values, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4Between(String value1, String value2) {
            addCriterion("addition4 between", value1, value2, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition4NotBetween(String value1, String value2) {
            addCriterion("addition4 not between", value1, value2, "addition4");
            return (Criteria) this;
        }

        public Criteria andAddition5IsNull() {
            addCriterion("addition5 is null");
            return (Criteria) this;
        }

        public Criteria andAddition5IsNotNull() {
            addCriterion("addition5 is not null");
            return (Criteria) this;
        }

        public Criteria andAddition5EqualTo(String value) {
            addCriterion("addition5 =", value, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5NotEqualTo(String value) {
            addCriterion("addition5 <>", value, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5GreaterThan(String value) {
            addCriterion("addition5 >", value, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5GreaterThanOrEqualTo(String value) {
            addCriterion("addition5 >=", value, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5LessThan(String value) {
            addCriterion("addition5 <", value, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5LessThanOrEqualTo(String value) {
            addCriterion("addition5 <=", value, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5Like(String value) {
            addCriterion("addition5 like", value, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5NotLike(String value) {
            addCriterion("addition5 not like", value, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5In(List<String> values) {
            addCriterion("addition5 in", values, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5NotIn(List<String> values) {
            addCriterion("addition5 not in", values, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5Between(String value1, String value2) {
            addCriterion("addition5 between", value1, value2, "addition5");
            return (Criteria) this;
        }

        public Criteria andAddition5NotBetween(String value1, String value2) {
            addCriterion("addition5 not between", value1, value2, "addition5");
            return (Criteria) this;
        }

        public Criteria andThirdOddsFieldSourceIdIsNull() {
            addCriterion("third_odds_field_source_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdOddsFieldSourceIdIsNotNull() {
            addCriterion("third_odds_field_source_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdOddsFieldSourceIdEqualTo(String value) {
            addCriterion("third_odds_field_source_id =", value, "thirdOddsFieldSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOddsFieldSourceIdNotEqualTo(String value) {
            addCriterion("third_odds_field_source_id <>", value, "thirdOddsFieldSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOddsFieldSourceIdGreaterThan(String value) {
            addCriterion("third_odds_field_source_id >", value, "thirdOddsFieldSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOddsFieldSourceIdGreaterThanOrEqualTo(String value) {
            addCriterion("third_odds_field_source_id >=", value, "thirdOddsFieldSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOddsFieldSourceIdLessThan(String value) {
            addCriterion("third_odds_field_source_id <", value, "thirdOddsFieldSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOddsFieldSourceIdLessThanOrEqualTo(String value) {
            addCriterion("third_odds_field_source_id <=", value, "thirdOddsFieldSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOddsFieldSourceIdLike(String value) {
            addCriterion("third_odds_field_source_id like", value, "thirdOddsFieldSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOddsFieldSourceIdNotLike(String value) {
            addCriterion("third_odds_field_source_id not like", value, "thirdOddsFieldSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOddsFieldSourceIdIn(List<String> values) {
            addCriterion("third_odds_field_source_id in", values, "thirdOddsFieldSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOddsFieldSourceIdNotIn(List<String> values) {
            addCriterion("third_odds_field_source_id not in", values, "thirdOddsFieldSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOddsFieldSourceIdBetween(String value1, String value2) {
            addCriterion("third_odds_field_source_id between", value1, value2, "thirdOddsFieldSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdOddsFieldSourceIdNotBetween(String value1, String value2) {
            addCriterion("third_odds_field_source_id not between", value1, value2, "thirdOddsFieldSourceId");
            return (Criteria) this;
        }

        public Criteria andOrderOddsIsNull() {
            addCriterion("order_odds is null");
            return (Criteria) this;
        }

        public Criteria andOrderOddsIsNotNull() {
            addCriterion("order_odds is not null");
            return (Criteria) this;
        }

        public Criteria andOrderOddsEqualTo(Integer value) {
            addCriterion("order_odds =", value, "orderOdds");
            return (Criteria) this;
        }

        public Criteria andOrderOddsNotEqualTo(Integer value) {
            addCriterion("order_odds <>", value, "orderOdds");
            return (Criteria) this;
        }

        public Criteria andOrderOddsGreaterThan(Integer value) {
            addCriterion("order_odds >", value, "orderOdds");
            return (Criteria) this;
        }

        public Criteria andOrderOddsGreaterThanOrEqualTo(Integer value) {
            addCriterion("order_odds >=", value, "orderOdds");
            return (Criteria) this;
        }

        public Criteria andOrderOddsLessThan(Integer value) {
            addCriterion("order_odds <", value, "orderOdds");
            return (Criteria) this;
        }

        public Criteria andOrderOddsLessThanOrEqualTo(Integer value) {
            addCriterion("order_odds <=", value, "orderOdds");
            return (Criteria) this;
        }

        public Criteria andOrderOddsIn(List<Integer> values) {
            addCriterion("order_odds in", values, "orderOdds");
            return (Criteria) this;
        }

        public Criteria andOrderOddsNotIn(List<Integer> values) {
            addCriterion("order_odds not in", values, "orderOdds");
            return (Criteria) this;
        }

        public Criteria andOrderOddsBetween(Integer value1, Integer value2) {
            addCriterion("order_odds between", value1, value2, "orderOdds");
            return (Criteria) this;
        }

        public Criteria andOrderOddsNotBetween(Integer value1, Integer value2) {
            addCriterion("order_odds not between", value1, value2, "orderOdds");
            return (Criteria) this;
        }

        public Criteria andNameCodeIsNull() {
            addCriterion("name_code is null");
            return (Criteria) this;
        }

        public Criteria andNameCodeIsNotNull() {
            addCriterion("name_code is not null");
            return (Criteria) this;
        }

        public Criteria andNameCodeEqualTo(Long value) {
            addCriterion("name_code =", value, "nameCode");
            return (Criteria) this;
        }

        public Criteria andNameCodeNotEqualTo(Long value) {
            addCriterion("name_code <>", value, "nameCode");
            return (Criteria) this;
        }

        public Criteria andNameCodeGreaterThan(Long value) {
            addCriterion("name_code >", value, "nameCode");
            return (Criteria) this;
        }

        public Criteria andNameCodeGreaterThanOrEqualTo(Long value) {
            addCriterion("name_code >=", value, "nameCode");
            return (Criteria) this;
        }

        public Criteria andNameCodeLessThan(Long value) {
            addCriterion("name_code <", value, "nameCode");
            return (Criteria) this;
        }

        public Criteria andNameCodeLessThanOrEqualTo(Long value) {
            addCriterion("name_code <=", value, "nameCode");
            return (Criteria) this;
        }

        public Criteria andNameCodeIn(List<Long> values) {
            addCriterion("name_code in", values, "nameCode");
            return (Criteria) this;
        }

        public Criteria andNameCodeNotIn(List<Long> values) {
            addCriterion("name_code not in", values, "nameCode");
            return (Criteria) this;
        }

        public Criteria andNameCodeBetween(Long value1, Long value2) {
            addCriterion("name_code between", value1, value2, "nameCode");
            return (Criteria) this;
        }

        public Criteria andNameCodeNotBetween(Long value1, Long value2) {
            addCriterion("name_code not between", value1, value2, "nameCode");
            return (Criteria) this;
        }

        public Criteria andNameExpressionValueIsNull() {
            addCriterion("name_expression_value is null");
            return (Criteria) this;
        }

        public Criteria andNameExpressionValueIsNotNull() {
            addCriterion("name_expression_value is not null");
            return (Criteria) this;
        }

        public Criteria andNameExpressionValueEqualTo(String value) {
            addCriterion("name_expression_value =", value, "nameExpressionValue");
            return (Criteria) this;
        }

        public Criteria andNameExpressionValueNotEqualTo(String value) {
            addCriterion("name_expression_value <>", value, "nameExpressionValue");
            return (Criteria) this;
        }

        public Criteria andNameExpressionValueGreaterThan(String value) {
            addCriterion("name_expression_value >", value, "nameExpressionValue");
            return (Criteria) this;
        }

        public Criteria andNameExpressionValueGreaterThanOrEqualTo(String value) {
            addCriterion("name_expression_value >=", value, "nameExpressionValue");
            return (Criteria) this;
        }

        public Criteria andNameExpressionValueLessThan(String value) {
            addCriterion("name_expression_value <", value, "nameExpressionValue");
            return (Criteria) this;
        }

        public Criteria andNameExpressionValueLessThanOrEqualTo(String value) {
            addCriterion("name_expression_value <=", value, "nameExpressionValue");
            return (Criteria) this;
        }

        public Criteria andNameExpressionValueLike(String value) {
            addCriterion("name_expression_value like", value, "nameExpressionValue");
            return (Criteria) this;
        }

        public Criteria andNameExpressionValueNotLike(String value) {
            addCriterion("name_expression_value not like", value, "nameExpressionValue");
            return (Criteria) this;
        }

        public Criteria andNameExpressionValueIn(List<String> values) {
            addCriterion("name_expression_value in", values, "nameExpressionValue");
            return (Criteria) this;
        }

        public Criteria andNameExpressionValueNotIn(List<String> values) {
            addCriterion("name_expression_value not in", values, "nameExpressionValue");
            return (Criteria) this;
        }

        public Criteria andNameExpressionValueBetween(String value1, String value2) {
            addCriterion("name_expression_value between", value1, value2, "nameExpressionValue");
            return (Criteria) this;
        }

        public Criteria andNameExpressionValueNotBetween(String value1, String value2) {
            addCriterion("name_expression_value not between", value1, value2, "nameExpressionValue");
            return (Criteria) this;
        }

        public Criteria andOddsValueIsNull() {
            addCriterion("odds_value is null");
            return (Criteria) this;
        }

        public Criteria andOddsValueIsNotNull() {
            addCriterion("odds_value is not null");
            return (Criteria) this;
        }

        public Criteria andOddsValueEqualTo(Integer value) {
            addCriterion("odds_value =", value, "oddsValue");
            return (Criteria) this;
        }

        public Criteria andOddsValueNotEqualTo(Integer value) {
            addCriterion("odds_value <>", value, "oddsValue");
            return (Criteria) this;
        }

        public Criteria andOddsValueGreaterThan(Integer value) {
            addCriterion("odds_value >", value, "oddsValue");
            return (Criteria) this;
        }

        public Criteria andOddsValueGreaterThanOrEqualTo(Integer value) {
            addCriterion("odds_value >=", value, "oddsValue");
            return (Criteria) this;
        }

        public Criteria andOddsValueLessThan(Integer value) {
            addCriterion("odds_value <", value, "oddsValue");
            return (Criteria) this;
        }

        public Criteria andOddsValueLessThanOrEqualTo(Integer value) {
            addCriterion("odds_value <=", value, "oddsValue");
            return (Criteria) this;
        }

        public Criteria andOddsValueIn(List<Integer> values) {
            addCriterion("odds_value in", values, "oddsValue");
            return (Criteria) this;
        }

        public Criteria andOddsValueNotIn(List<Integer> values) {
            addCriterion("odds_value not in", values, "oddsValue");
            return (Criteria) this;
        }

        public Criteria andOddsValueBetween(Integer value1, Integer value2) {
            addCriterion("odds_value between", value1, value2, "oddsValue");
            return (Criteria) this;
        }

        public Criteria andOddsValueNotBetween(Integer value1, Integer value2) {
            addCriterion("odds_value not between", value1, value2, "oddsValue");
            return (Criteria) this;
        }

        public Criteria andPaOddsValueIsNull() {
            addCriterion("pa_odds_value is null");
            return (Criteria) this;
        }

        public Criteria andPaOddsValueIsNotNull() {
            addCriterion("pa_odds_value is not null");
            return (Criteria) this;
        }

        public Criteria andPaOddsValueEqualTo(Integer value) {
            addCriterion("pa_odds_value =", value, "paOddsValue");
            return (Criteria) this;
        }

        public Criteria andPaOddsValueNotEqualTo(Integer value) {
            addCriterion("pa_odds_value <>", value, "paOddsValue");
            return (Criteria) this;
        }

        public Criteria andPaOddsValueGreaterThan(Integer value) {
            addCriterion("pa_odds_value >", value, "paOddsValue");
            return (Criteria) this;
        }

        public Criteria andPaOddsValueGreaterThanOrEqualTo(Integer value) {
            addCriterion("pa_odds_value >=", value, "paOddsValue");
            return (Criteria) this;
        }

        public Criteria andPaOddsValueLessThan(Integer value) {
            addCriterion("pa_odds_value <", value, "paOddsValue");
            return (Criteria) this;
        }

        public Criteria andPaOddsValueLessThanOrEqualTo(Integer value) {
            addCriterion("pa_odds_value <=", value, "paOddsValue");
            return (Criteria) this;
        }

        public Criteria andPaOddsValueIn(List<Integer> values) {
            addCriterion("pa_odds_value in", values, "paOddsValue");
            return (Criteria) this;
        }

        public Criteria andPaOddsValueNotIn(List<Integer> values) {
            addCriterion("pa_odds_value not in", values, "paOddsValue");
            return (Criteria) this;
        }

        public Criteria andPaOddsValueBetween(Integer value1, Integer value2) {
            addCriterion("pa_odds_value between", value1, value2, "paOddsValue");
            return (Criteria) this;
        }

        public Criteria andPaOddsValueNotBetween(Integer value1, Integer value2) {
            addCriterion("pa_odds_value not between", value1, value2, "paOddsValue");
            return (Criteria) this;
        }

        public Criteria andOriginalOddsValueIsNull() {
            addCriterion("original_odds_value is null");
            return (Criteria) this;
        }

        public Criteria andOriginalOddsValueIsNotNull() {
            addCriterion("original_odds_value is not null");
            return (Criteria) this;
        }

        public Criteria andOriginalOddsValueEqualTo(Integer value) {
            addCriterion("original_odds_value =", value, "originalOddsValue");
            return (Criteria) this;
        }

        public Criteria andOriginalOddsValueNotEqualTo(Integer value) {
            addCriterion("original_odds_value <>", value, "originalOddsValue");
            return (Criteria) this;
        }

        public Criteria andOriginalOddsValueGreaterThan(Integer value) {
            addCriterion("original_odds_value >", value, "originalOddsValue");
            return (Criteria) this;
        }

        public Criteria andOriginalOddsValueGreaterThanOrEqualTo(Integer value) {
            addCriterion("original_odds_value >=", value, "originalOddsValue");
            return (Criteria) this;
        }

        public Criteria andOriginalOddsValueLessThan(Integer value) {
            addCriterion("original_odds_value <", value, "originalOddsValue");
            return (Criteria) this;
        }

        public Criteria andOriginalOddsValueLessThanOrEqualTo(Integer value) {
            addCriterion("original_odds_value <=", value, "originalOddsValue");
            return (Criteria) this;
        }

        public Criteria andOriginalOddsValueIn(List<Integer> values) {
            addCriterion("original_odds_value in", values, "originalOddsValue");
            return (Criteria) this;
        }

        public Criteria andOriginalOddsValueNotIn(List<Integer> values) {
            addCriterion("original_odds_value not in", values, "originalOddsValue");
            return (Criteria) this;
        }

        public Criteria andOriginalOddsValueBetween(Integer value1, Integer value2) {
            addCriterion("original_odds_value between", value1, value2, "originalOddsValue");
            return (Criteria) this;
        }

        public Criteria andOriginalOddsValueNotBetween(Integer value1, Integer value2) {
            addCriterion("original_odds_value not between", value1, value2, "originalOddsValue");
            return (Criteria) this;
        }

        public Criteria andOddsFieldsTemplateIdIsNull() {
            addCriterion("odds_fields_template_id is null");
            return (Criteria) this;
        }

        public Criteria andOddsFieldsTemplateIdIsNotNull() {
            addCriterion("odds_fields_template_id is not null");
            return (Criteria) this;
        }

        public Criteria andOddsFieldsTemplateIdEqualTo(Long value) {
            addCriterion("odds_fields_template_id =", value, "oddsFieldsTemplateId");
            return (Criteria) this;
        }

        public Criteria andOddsFieldsTemplateIdNotEqualTo(Long value) {
            addCriterion("odds_fields_template_id <>", value, "oddsFieldsTemplateId");
            return (Criteria) this;
        }

        public Criteria andOddsFieldsTemplateIdGreaterThan(Long value) {
            addCriterion("odds_fields_template_id >", value, "oddsFieldsTemplateId");
            return (Criteria) this;
        }

        public Criteria andOddsFieldsTemplateIdGreaterThanOrEqualTo(Long value) {
            addCriterion("odds_fields_template_id >=", value, "oddsFieldsTemplateId");
            return (Criteria) this;
        }

        public Criteria andOddsFieldsTemplateIdLessThan(Long value) {
            addCriterion("odds_fields_template_id <", value, "oddsFieldsTemplateId");
            return (Criteria) this;
        }

        public Criteria andOddsFieldsTemplateIdLessThanOrEqualTo(Long value) {
            addCriterion("odds_fields_template_id <=", value, "oddsFieldsTemplateId");
            return (Criteria) this;
        }

        public Criteria andOddsFieldsTemplateIdIn(List<Long> values) {
            addCriterion("odds_fields_template_id in", values, "oddsFieldsTemplateId");
            return (Criteria) this;
        }

        public Criteria andOddsFieldsTemplateIdNotIn(List<Long> values) {
            addCriterion("odds_fields_template_id not in", values, "oddsFieldsTemplateId");
            return (Criteria) this;
        }

        public Criteria andOddsFieldsTemplateIdBetween(Long value1, Long value2) {
            addCriterion("odds_fields_template_id between", value1, value2, "oddsFieldsTemplateId");
            return (Criteria) this;
        }

        public Criteria andOddsFieldsTemplateIdNotBetween(Long value1, Long value2) {
            addCriterion("odds_fields_template_id not between", value1, value2, "oddsFieldsTemplateId");
            return (Criteria) this;
        }

        public Criteria andThirdTemplateSourceIdIsNull() {
            addCriterion("third_template_source_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdTemplateSourceIdIsNotNull() {
            addCriterion("third_template_source_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdTemplateSourceIdEqualTo(String value) {
            addCriterion("third_template_source_id =", value, "thirdTemplateSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTemplateSourceIdNotEqualTo(String value) {
            addCriterion("third_template_source_id <>", value, "thirdTemplateSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTemplateSourceIdGreaterThan(String value) {
            addCriterion("third_template_source_id >", value, "thirdTemplateSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTemplateSourceIdGreaterThanOrEqualTo(String value) {
            addCriterion("third_template_source_id >=", value, "thirdTemplateSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTemplateSourceIdLessThan(String value) {
            addCriterion("third_template_source_id <", value, "thirdTemplateSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTemplateSourceIdLessThanOrEqualTo(String value) {
            addCriterion("third_template_source_id <=", value, "thirdTemplateSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTemplateSourceIdLike(String value) {
            addCriterion("third_template_source_id like", value, "thirdTemplateSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTemplateSourceIdNotLike(String value) {
            addCriterion("third_template_source_id not like", value, "thirdTemplateSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTemplateSourceIdIn(List<String> values) {
            addCriterion("third_template_source_id in", values, "thirdTemplateSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTemplateSourceIdNotIn(List<String> values) {
            addCriterion("third_template_source_id not in", values, "thirdTemplateSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTemplateSourceIdBetween(String value1, String value2) {
            addCriterion("third_template_source_id between", value1, value2, "thirdTemplateSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTemplateSourceIdNotBetween(String value1, String value2) {
            addCriterion("third_template_source_id not between", value1, value2, "thirdTemplateSourceId");
            return (Criteria) this;
        }

        public Criteria andTargetSideIsNull() {
            addCriterion("target_side is null");
            return (Criteria) this;
        }

        public Criteria andTargetSideIsNotNull() {
            addCriterion("target_side is not null");
            return (Criteria) this;
        }

        public Criteria andTargetSideEqualTo(String value) {
            addCriterion("target_side =", value, "targetSide");
            return (Criteria) this;
        }

        public Criteria andTargetSideNotEqualTo(String value) {
            addCriterion("target_side <>", value, "targetSide");
            return (Criteria) this;
        }

        public Criteria andTargetSideGreaterThan(String value) {
            addCriterion("target_side >", value, "targetSide");
            return (Criteria) this;
        }

        public Criteria andTargetSideGreaterThanOrEqualTo(String value) {
            addCriterion("target_side >=", value, "targetSide");
            return (Criteria) this;
        }

        public Criteria andTargetSideLessThan(String value) {
            addCriterion("target_side <", value, "targetSide");
            return (Criteria) this;
        }

        public Criteria andTargetSideLessThanOrEqualTo(String value) {
            addCriterion("target_side <=", value, "targetSide");
            return (Criteria) this;
        }

        public Criteria andTargetSideLike(String value) {
            addCriterion("target_side like", value, "targetSide");
            return (Criteria) this;
        }

        public Criteria andTargetSideNotLike(String value) {
            addCriterion("target_side not like", value, "targetSide");
            return (Criteria) this;
        }

        public Criteria andTargetSideIn(List<String> values) {
            addCriterion("target_side in", values, "targetSide");
            return (Criteria) this;
        }

        public Criteria andTargetSideNotIn(List<String> values) {
            addCriterion("target_side not in", values, "targetSide");
            return (Criteria) this;
        }

        public Criteria andTargetSideBetween(String value1, String value2) {
            addCriterion("target_side between", value1, value2, "targetSide");
            return (Criteria) this;
        }

        public Criteria andTargetSideNotBetween(String value1, String value2) {
            addCriterion("target_side not between", value1, value2, "targetSide");
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

        public Criteria andExtraInfoIsNull() {
            addCriterion("extra_info is null");
            return (Criteria) this;
        }

        public Criteria andExtraInfoIsNotNull() {
            addCriterion("extra_info is not null");
            return (Criteria) this;
        }

        public Criteria andExtraInfoEqualTo(String value) {
            addCriterion("extra_info =", value, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoNotEqualTo(String value) {
            addCriterion("extra_info <>", value, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoGreaterThan(String value) {
            addCriterion("extra_info >", value, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoGreaterThanOrEqualTo(String value) {
            addCriterion("extra_info >=", value, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoLessThan(String value) {
            addCriterion("extra_info <", value, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoLessThanOrEqualTo(String value) {
            addCriterion("extra_info <=", value, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoLike(String value) {
            addCriterion("extra_info like", value, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoNotLike(String value) {
            addCriterion("extra_info not like", value, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoIn(List<String> values) {
            addCriterion("extra_info in", values, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoNotIn(List<String> values) {
            addCriterion("extra_info not in", values, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoBetween(String value1, String value2) {
            addCriterion("extra_info between", value1, value2, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andExtraInfoNotBetween(String value1, String value2) {
            addCriterion("extra_info not between", value1, value2, "extraInfo");
            return (Criteria) this;
        }

        public Criteria andNameIsNull() {
            addCriterion("name is null");
            return (Criteria) this;
        }

        public Criteria andNameIsNotNull() {
            addCriterion("name is not null");
            return (Criteria) this;
        }

        public Criteria andNameEqualTo(String value) {
            addCriterion("name =", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotEqualTo(String value) {
            addCriterion("name <>", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThan(String value) {
            addCriterion("name >", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThanOrEqualTo(String value) {
            addCriterion("name >=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThan(String value) {
            addCriterion("name <", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThanOrEqualTo(String value) {
            addCriterion("name <=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLike(String value) {
            addCriterion("name like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotLike(String value) {
            addCriterion("name not like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameIn(List<String> values) {
            addCriterion("name in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotIn(List<String> values) {
            addCriterion("name not in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameBetween(String value1, String value2) {
            addCriterion("name between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotBetween(String value1, String value2) {
            addCriterion("name not between", value1, value2, "name");
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