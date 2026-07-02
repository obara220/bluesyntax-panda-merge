package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class ConfigTradeMarketExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ConfigTradeMarketExample() {
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
            addCriterion("ID is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("ID is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("ID =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("ID <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("ID >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("ID >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("ID <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("ID <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("ID in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("ID not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("ID between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("ID not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andConfigIdIsNull() {
            addCriterion("CONFIG_ID is null");
            return (Criteria) this;
        }

        public Criteria andConfigIdIsNotNull() {
            addCriterion("CONFIG_ID is not null");
            return (Criteria) this;
        }

        public Criteria andConfigIdEqualTo(String value) {
            addCriterion("CONFIG_ID =", value, "configId");
            return (Criteria) this;
        }

        public Criteria andConfigIdNotEqualTo(String value) {
            addCriterion("CONFIG_ID <>", value, "configId");
            return (Criteria) this;
        }

        public Criteria andConfigIdGreaterThan(String value) {
            addCriterion("CONFIG_ID >", value, "configId");
            return (Criteria) this;
        }

        public Criteria andConfigIdGreaterThanOrEqualTo(String value) {
            addCriterion("CONFIG_ID >=", value, "configId");
            return (Criteria) this;
        }

        public Criteria andConfigIdLessThan(String value) {
            addCriterion("CONFIG_ID <", value, "configId");
            return (Criteria) this;
        }

        public Criteria andConfigIdLessThanOrEqualTo(String value) {
            addCriterion("CONFIG_ID <=", value, "configId");
            return (Criteria) this;
        }

        public Criteria andConfigIdLike(String value) {
            addCriterion("CONFIG_ID like", value, "configId");
            return (Criteria) this;
        }

        public Criteria andConfigIdNotLike(String value) {
            addCriterion("CONFIG_ID not like", value, "configId");
            return (Criteria) this;
        }

        public Criteria andConfigIdIn(List<String> values) {
            addCriterion("CONFIG_ID in", values, "configId");
            return (Criteria) this;
        }

        public Criteria andConfigIdNotIn(List<String> values) {
            addCriterion("CONFIG_ID not in", values, "configId");
            return (Criteria) this;
        }

        public Criteria andConfigIdBetween(String value1, String value2) {
            addCriterion("CONFIG_ID between", value1, value2, "configId");
            return (Criteria) this;
        }

        public Criteria andConfigIdNotBetween(String value1, String value2) {
            addCriterion("CONFIG_ID not between", value1, value2, "configId");
            return (Criteria) this;
        }

        public Criteria andLevelIsNull() {
            addCriterion("LEVEL is null");
            return (Criteria) this;
        }

        public Criteria andLevelIsNotNull() {
            addCriterion("LEVEL is not null");
            return (Criteria) this;
        }

        public Criteria andLevelEqualTo(Integer value) {
            addCriterion("LEVEL =", value, "level");
            return (Criteria) this;
        }

        public Criteria andLevelNotEqualTo(Integer value) {
            addCriterion("LEVEL <>", value, "level");
            return (Criteria) this;
        }

        public Criteria andLevelGreaterThan(Integer value) {
            addCriterion("LEVEL >", value, "level");
            return (Criteria) this;
        }

        public Criteria andLevelGreaterThanOrEqualTo(Integer value) {
            addCriterion("LEVEL >=", value, "level");
            return (Criteria) this;
        }

        public Criteria andLevelLessThan(Integer value) {
            addCriterion("LEVEL <", value, "level");
            return (Criteria) this;
        }

        public Criteria andLevelLessThanOrEqualTo(Integer value) {
            addCriterion("LEVEL <=", value, "level");
            return (Criteria) this;
        }

        public Criteria andLevelIn(List<Integer> values) {
            addCriterion("LEVEL in", values, "level");
            return (Criteria) this;
        }

        public Criteria andLevelNotIn(List<Integer> values) {
            addCriterion("LEVEL not in", values, "level");
            return (Criteria) this;
        }

        public Criteria andLevelBetween(Integer value1, Integer value2) {
            addCriterion("LEVEL between", value1, value2, "level");
            return (Criteria) this;
        }

        public Criteria andLevelNotBetween(Integer value1, Integer value2) {
            addCriterion("LEVEL not between", value1, value2, "level");
            return (Criteria) this;
        }

        public Criteria andTargetIdIsNull() {
            addCriterion("TARGET_ID is null");
            return (Criteria) this;
        }

        public Criteria andTargetIdIsNotNull() {
            addCriterion("TARGET_ID is not null");
            return (Criteria) this;
        }

        public Criteria andTargetIdEqualTo(String value) {
            addCriterion("TARGET_ID =", value, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdNotEqualTo(String value) {
            addCriterion("TARGET_ID <>", value, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdGreaterThan(String value) {
            addCriterion("TARGET_ID >", value, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdGreaterThanOrEqualTo(String value) {
            addCriterion("TARGET_ID >=", value, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdLessThan(String value) {
            addCriterion("TARGET_ID <", value, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdLessThanOrEqualTo(String value) {
            addCriterion("TARGET_ID <=", value, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdLike(String value) {
            addCriterion("TARGET_ID like", value, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdNotLike(String value) {
            addCriterion("TARGET_ID not like", value, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdIn(List<String> values) {
            addCriterion("TARGET_ID in", values, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdNotIn(List<String> values) {
            addCriterion("TARGET_ID not in", values, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdBetween(String value1, String value2) {
            addCriterion("TARGET_ID between", value1, value2, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdNotBetween(String value1, String value2) {
            addCriterion("TARGET_ID not between", value1, value2, "targetId");
            return (Criteria) this;
        }

        public Criteria andMatchTypeIsNull() {
            addCriterion("MATCH_TYPE is null");
            return (Criteria) this;
        }

        public Criteria andMatchTypeIsNotNull() {
            addCriterion("MATCH_TYPE is not null");
            return (Criteria) this;
        }

        public Criteria andMatchTypeEqualTo(String value) {
            addCriterion("MATCH_TYPE =", value, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeNotEqualTo(String value) {
            addCriterion("MATCH_TYPE <>", value, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeGreaterThan(String value) {
            addCriterion("MATCH_TYPE >", value, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeGreaterThanOrEqualTo(String value) {
            addCriterion("MATCH_TYPE >=", value, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeLessThan(String value) {
            addCriterion("MATCH_TYPE <", value, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeLessThanOrEqualTo(String value) {
            addCriterion("MATCH_TYPE <=", value, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeLike(String value) {
            addCriterion("MATCH_TYPE like", value, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeNotLike(String value) {
            addCriterion("MATCH_TYPE not like", value, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeIn(List<String> values) {
            addCriterion("MATCH_TYPE in", values, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeNotIn(List<String> values) {
            addCriterion("MATCH_TYPE not in", values, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeBetween(String value1, String value2) {
            addCriterion("MATCH_TYPE between", value1, value2, "matchType");
            return (Criteria) this;
        }

        public Criteria andMatchTypeNotBetween(String value1, String value2) {
            addCriterion("MATCH_TYPE not between", value1, value2, "matchType");
            return (Criteria) this;
        }

        public Criteria andTradeTypeIsNull() {
            addCriterion("Trade_type is null");
            return (Criteria) this;
        }

        public Criteria andTradeTypeIsNotNull() {
            addCriterion("Trade_type is not null");
            return (Criteria) this;
        }

        public Criteria andTradeTypeEqualTo(Integer value) {
            addCriterion("Trade_type =", value, "tradeType");
            return (Criteria) this;
        }

        public Criteria andTradeTypeNotEqualTo(Integer value) {
            addCriterion("Trade_type <>", value, "tradeType");
            return (Criteria) this;
        }

        public Criteria andTradeTypeGreaterThan(Integer value) {
            addCriterion("Trade_type >", value, "tradeType");
            return (Criteria) this;
        }

        public Criteria andTradeTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("Trade_type >=", value, "tradeType");
            return (Criteria) this;
        }

        public Criteria andTradeTypeLessThan(Integer value) {
            addCriterion("Trade_type <", value, "tradeType");
            return (Criteria) this;
        }

        public Criteria andTradeTypeLessThanOrEqualTo(Integer value) {
            addCriterion("Trade_type <=", value, "tradeType");
            return (Criteria) this;
        }

        public Criteria andTradeTypeIn(List<Integer> values) {
            addCriterion("Trade_type in", values, "tradeType");
            return (Criteria) this;
        }

        public Criteria andTradeTypeNotIn(List<Integer> values) {
            addCriterion("Trade_type not in", values, "tradeType");
            return (Criteria) this;
        }

        public Criteria andTradeTypeBetween(Integer value1, Integer value2) {
            addCriterion("Trade_type between", value1, value2, "tradeType");
            return (Criteria) this;
        }

        public Criteria andTradeTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("Trade_type not between", value1, value2, "tradeType");
            return (Criteria) this;
        }

        public Criteria andMarketStatusIsNull() {
            addCriterion("MARKET_STATUS is null");
            return (Criteria) this;
        }

        public Criteria andMarketStatusIsNotNull() {
            addCriterion("MARKET_STATUS is not null");
            return (Criteria) this;
        }

        public Criteria andMarketStatusEqualTo(Integer value) {
            addCriterion("MARKET_STATUS =", value, "marketStatus");
            return (Criteria) this;
        }

        public Criteria andMarketStatusNotEqualTo(Integer value) {
            addCriterion("MARKET_STATUS <>", value, "marketStatus");
            return (Criteria) this;
        }

        public Criteria andMarketStatusGreaterThan(Integer value) {
            addCriterion("MARKET_STATUS >", value, "marketStatus");
            return (Criteria) this;
        }

        public Criteria andMarketStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("MARKET_STATUS >=", value, "marketStatus");
            return (Criteria) this;
        }

        public Criteria andMarketStatusLessThan(Integer value) {
            addCriterion("MARKET_STATUS <", value, "marketStatus");
            return (Criteria) this;
        }

        public Criteria andMarketStatusLessThanOrEqualTo(Integer value) {
            addCriterion("MARKET_STATUS <=", value, "marketStatus");
            return (Criteria) this;
        }

        public Criteria andMarketStatusIn(List<Integer> values) {
            addCriterion("MARKET_STATUS in", values, "marketStatus");
            return (Criteria) this;
        }

        public Criteria andMarketStatusNotIn(List<Integer> values) {
            addCriterion("MARKET_STATUS not in", values, "marketStatus");
            return (Criteria) this;
        }

        public Criteria andMarketStatusBetween(Integer value1, Integer value2) {
            addCriterion("MARKET_STATUS between", value1, value2, "marketStatus");
            return (Criteria) this;
        }

        public Criteria andMarketStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("MARKET_STATUS not between", value1, value2, "marketStatus");
            return (Criteria) this;
        }

        public Criteria andSourceSystemIsNull() {
            addCriterion("SOURCE_SYSTEM is null");
            return (Criteria) this;
        }

        public Criteria andSourceSystemIsNotNull() {
            addCriterion("SOURCE_SYSTEM is not null");
            return (Criteria) this;
        }

        public Criteria andSourceSystemEqualTo(Integer value) {
            addCriterion("SOURCE_SYSTEM =", value, "sourceSystem");
            return (Criteria) this;
        }

        public Criteria andSourceSystemNotEqualTo(Integer value) {
            addCriterion("SOURCE_SYSTEM <>", value, "sourceSystem");
            return (Criteria) this;
        }

        public Criteria andSourceSystemGreaterThan(Integer value) {
            addCriterion("SOURCE_SYSTEM >", value, "sourceSystem");
            return (Criteria) this;
        }

        public Criteria andSourceSystemGreaterThanOrEqualTo(Integer value) {
            addCriterion("SOURCE_SYSTEM >=", value, "sourceSystem");
            return (Criteria) this;
        }

        public Criteria andSourceSystemLessThan(Integer value) {
            addCriterion("SOURCE_SYSTEM <", value, "sourceSystem");
            return (Criteria) this;
        }

        public Criteria andSourceSystemLessThanOrEqualTo(Integer value) {
            addCriterion("SOURCE_SYSTEM <=", value, "sourceSystem");
            return (Criteria) this;
        }

        public Criteria andSourceSystemIn(List<Integer> values) {
            addCriterion("SOURCE_SYSTEM in", values, "sourceSystem");
            return (Criteria) this;
        }

        public Criteria andSourceSystemNotIn(List<Integer> values) {
            addCriterion("SOURCE_SYSTEM not in", values, "sourceSystem");
            return (Criteria) this;
        }

        public Criteria andSourceSystemBetween(Integer value1, Integer value2) {
            addCriterion("SOURCE_SYSTEM between", value1, value2, "sourceSystem");
            return (Criteria) this;
        }

        public Criteria andSourceSystemNotBetween(Integer value1, Integer value2) {
            addCriterion("SOURCE_SYSTEM not between", value1, value2, "sourceSystem");
            return (Criteria) this;
        }

        public Criteria andActiveIsNull() {
            addCriterion("ACTIVE is null");
            return (Criteria) this;
        }

        public Criteria andActiveIsNotNull() {
            addCriterion("ACTIVE is not null");
            return (Criteria) this;
        }

        public Criteria andActiveEqualTo(Integer value) {
            addCriterion("ACTIVE =", value, "active");
            return (Criteria) this;
        }

        public Criteria andActiveNotEqualTo(Integer value) {
            addCriterion("ACTIVE <>", value, "active");
            return (Criteria) this;
        }

        public Criteria andActiveGreaterThan(Integer value) {
            addCriterion("ACTIVE >", value, "active");
            return (Criteria) this;
        }

        public Criteria andActiveGreaterThanOrEqualTo(Integer value) {
            addCriterion("ACTIVE >=", value, "active");
            return (Criteria) this;
        }

        public Criteria andActiveLessThan(Integer value) {
            addCriterion("ACTIVE <", value, "active");
            return (Criteria) this;
        }

        public Criteria andActiveLessThanOrEqualTo(Integer value) {
            addCriterion("ACTIVE <=", value, "active");
            return (Criteria) this;
        }

        public Criteria andActiveIn(List<Integer> values) {
            addCriterion("ACTIVE in", values, "active");
            return (Criteria) this;
        }

        public Criteria andActiveNotIn(List<Integer> values) {
            addCriterion("ACTIVE not in", values, "active");
            return (Criteria) this;
        }

        public Criteria andActiveBetween(Integer value1, Integer value2) {
            addCriterion("ACTIVE between", value1, value2, "active");
            return (Criteria) this;
        }

        public Criteria andActiveNotBetween(Integer value1, Integer value2) {
            addCriterion("ACTIVE not between", value1, value2, "active");
            return (Criteria) this;
        }

        public Criteria andOperaterIdIsNull() {
            addCriterion("OPERATER_ID is null");
            return (Criteria) this;
        }

        public Criteria andOperaterIdIsNotNull() {
            addCriterion("OPERATER_ID is not null");
            return (Criteria) this;
        }

        public Criteria andOperaterIdEqualTo(Long value) {
            addCriterion("OPERATER_ID =", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdNotEqualTo(Long value) {
            addCriterion("OPERATER_ID <>", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdGreaterThan(Long value) {
            addCriterion("OPERATER_ID >", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdGreaterThanOrEqualTo(Long value) {
            addCriterion("OPERATER_ID >=", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdLessThan(Long value) {
            addCriterion("OPERATER_ID <", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdLessThanOrEqualTo(Long value) {
            addCriterion("OPERATER_ID <=", value, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdIn(List<Long> values) {
            addCriterion("OPERATER_ID in", values, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdNotIn(List<Long> values) {
            addCriterion("OPERATER_ID not in", values, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdBetween(Long value1, Long value2) {
            addCriterion("OPERATER_ID between", value1, value2, "operaterId");
            return (Criteria) this;
        }

        public Criteria andOperaterIdNotBetween(Long value1, Long value2) {
            addCriterion("OPERATER_ID not between", value1, value2, "operaterId");
            return (Criteria) this;
        }

        public Criteria andConfigModifyTimeIsNull() {
            addCriterion("CONFIG_MODIFY_TIME is null");
            return (Criteria) this;
        }

        public Criteria andConfigModifyTimeIsNotNull() {
            addCriterion("CONFIG_MODIFY_TIME is not null");
            return (Criteria) this;
        }

        public Criteria andConfigModifyTimeEqualTo(Long value) {
            addCriterion("CONFIG_MODIFY_TIME =", value, "configModifyTime");
            return (Criteria) this;
        }

        public Criteria andConfigModifyTimeNotEqualTo(Long value) {
            addCriterion("CONFIG_MODIFY_TIME <>", value, "configModifyTime");
            return (Criteria) this;
        }

        public Criteria andConfigModifyTimeGreaterThan(Long value) {
            addCriterion("CONFIG_MODIFY_TIME >", value, "configModifyTime");
            return (Criteria) this;
        }

        public Criteria andConfigModifyTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("CONFIG_MODIFY_TIME >=", value, "configModifyTime");
            return (Criteria) this;
        }

        public Criteria andConfigModifyTimeLessThan(Long value) {
            addCriterion("CONFIG_MODIFY_TIME <", value, "configModifyTime");
            return (Criteria) this;
        }

        public Criteria andConfigModifyTimeLessThanOrEqualTo(Long value) {
            addCriterion("CONFIG_MODIFY_TIME <=", value, "configModifyTime");
            return (Criteria) this;
        }

        public Criteria andConfigModifyTimeIn(List<Long> values) {
            addCriterion("CONFIG_MODIFY_TIME in", values, "configModifyTime");
            return (Criteria) this;
        }

        public Criteria andConfigModifyTimeNotIn(List<Long> values) {
            addCriterion("CONFIG_MODIFY_TIME not in", values, "configModifyTime");
            return (Criteria) this;
        }

        public Criteria andConfigModifyTimeBetween(Long value1, Long value2) {
            addCriterion("CONFIG_MODIFY_TIME between", value1, value2, "configModifyTime");
            return (Criteria) this;
        }

        public Criteria andConfigModifyTimeNotBetween(Long value1, Long value2) {
            addCriterion("CONFIG_MODIFY_TIME not between", value1, value2, "configModifyTime");
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

        public Criteria andCreateTimeIsNull() {
            addCriterion("CREATE_TIME is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("CREATE_TIME is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Long value) {
            addCriterion("CREATE_TIME =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Long value) {
            addCriterion("CREATE_TIME <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Long value) {
            addCriterion("CREATE_TIME >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("CREATE_TIME >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Long value) {
            addCriterion("CREATE_TIME <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Long value) {
            addCriterion("CREATE_TIME <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Long> values) {
            addCriterion("CREATE_TIME in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Long> values) {
            addCriterion("CREATE_TIME not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Long value1, Long value2) {
            addCriterion("CREATE_TIME between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Long value1, Long value2) {
            addCriterion("CREATE_TIME not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeIsNull() {
            addCriterion("MODIFY_TIME is null");
            return (Criteria) this;
        }

        public Criteria andModifyTimeIsNotNull() {
            addCriterion("MODIFY_TIME is not null");
            return (Criteria) this;
        }

        public Criteria andModifyTimeEqualTo(Long value) {
            addCriterion("MODIFY_TIME =", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeNotEqualTo(Long value) {
            addCriterion("MODIFY_TIME <>", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeGreaterThan(Long value) {
            addCriterion("MODIFY_TIME >", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("MODIFY_TIME >=", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeLessThan(Long value) {
            addCriterion("MODIFY_TIME <", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeLessThanOrEqualTo(Long value) {
            addCriterion("MODIFY_TIME <=", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeIn(List<Long> values) {
            addCriterion("MODIFY_TIME in", values, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeNotIn(List<Long> values) {
            addCriterion("MODIFY_TIME not in", values, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeBetween(Long value1, Long value2) {
            addCriterion("MODIFY_TIME between", value1, value2, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeNotBetween(Long value1, Long value2) {
            addCriterion("MODIFY_TIME not between", value1, value2, "modifyTime");
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