package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class PlsThirdMatchRelationExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public PlsThirdMatchRelationExample() {
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

        public Criteria andPlsStandardMatchIdIsNull() {
            addCriterion("pls_standard_match_id is null");
            return (Criteria) this;
        }

        public Criteria andPlsStandardMatchIdIsNotNull() {
            addCriterion("pls_standard_match_id is not null");
            return (Criteria) this;
        }

        public Criteria andPlsStandardMatchIdEqualTo(Long value) {
            addCriterion("pls_standard_match_id =", value, "plsStandardMatchId");
            return (Criteria) this;
        }

        public Criteria andPlsStandardMatchIdNotEqualTo(Long value) {
            addCriterion("pls_standard_match_id <>", value, "plsStandardMatchId");
            return (Criteria) this;
        }

        public Criteria andPlsStandardMatchIdGreaterThan(Long value) {
            addCriterion("pls_standard_match_id >", value, "plsStandardMatchId");
            return (Criteria) this;
        }

        public Criteria andPlsStandardMatchIdGreaterThanOrEqualTo(Long value) {
            addCriterion("pls_standard_match_id >=", value, "plsStandardMatchId");
            return (Criteria) this;
        }

        public Criteria andPlsStandardMatchIdLessThan(Long value) {
            addCriterion("pls_standard_match_id <", value, "plsStandardMatchId");
            return (Criteria) this;
        }

        public Criteria andPlsStandardMatchIdLessThanOrEqualTo(Long value) {
            addCriterion("pls_standard_match_id <=", value, "plsStandardMatchId");
            return (Criteria) this;
        }

        public Criteria andPlsStandardMatchIdIn(List<Long> values) {
            addCriterion("pls_standard_match_id in", values, "plsStandardMatchId");
            return (Criteria) this;
        }

        public Criteria andPlsStandardMatchIdNotIn(List<Long> values) {
            addCriterion("pls_standard_match_id not in", values, "plsStandardMatchId");
            return (Criteria) this;
        }

        public Criteria andPlsStandardMatchIdBetween(Long value1, Long value2) {
            addCriterion("pls_standard_match_id between", value1, value2, "plsStandardMatchId");
            return (Criteria) this;
        }

        public Criteria andPlsStandardMatchIdNotBetween(Long value1, Long value2) {
            addCriterion("pls_standard_match_id not between", value1, value2, "plsStandardMatchId");
            return (Criteria) this;
        }

        public Criteria andPlsMatchManageIdIsNull() {
            addCriterion("pls_match_manage_id is null");
            return (Criteria) this;
        }

        public Criteria andPlsMatchManageIdIsNotNull() {
            addCriterion("pls_match_manage_id is not null");
            return (Criteria) this;
        }

        public Criteria andPlsMatchManageIdEqualTo(String value) {
            addCriterion("pls_match_manage_id =", value, "plsMatchManageId");
            return (Criteria) this;
        }

        public Criteria andPlsMatchManageIdNotEqualTo(String value) {
            addCriterion("pls_match_manage_id <>", value, "plsMatchManageId");
            return (Criteria) this;
        }

        public Criteria andPlsMatchManageIdGreaterThan(String value) {
            addCriterion("pls_match_manage_id >", value, "plsMatchManageId");
            return (Criteria) this;
        }

        public Criteria andPlsMatchManageIdGreaterThanOrEqualTo(String value) {
            addCriterion("pls_match_manage_id >=", value, "plsMatchManageId");
            return (Criteria) this;
        }

        public Criteria andPlsMatchManageIdLessThan(String value) {
            addCriterion("pls_match_manage_id <", value, "plsMatchManageId");
            return (Criteria) this;
        }

        public Criteria andPlsMatchManageIdLessThanOrEqualTo(String value) {
            addCriterion("pls_match_manage_id <=", value, "plsMatchManageId");
            return (Criteria) this;
        }

        public Criteria andPlsMatchManageIdLike(String value) {
            addCriterion("pls_match_manage_id like", value, "plsMatchManageId");
            return (Criteria) this;
        }

        public Criteria andPlsMatchManageIdNotLike(String value) {
            addCriterion("pls_match_manage_id not like", value, "plsMatchManageId");
            return (Criteria) this;
        }

        public Criteria andPlsMatchManageIdIn(List<String> values) {
            addCriterion("pls_match_manage_id in", values, "plsMatchManageId");
            return (Criteria) this;
        }

        public Criteria andPlsMatchManageIdNotIn(List<String> values) {
            addCriterion("pls_match_manage_id not in", values, "plsMatchManageId");
            return (Criteria) this;
        }

        public Criteria andPlsMatchManageIdBetween(String value1, String value2) {
            addCriterion("pls_match_manage_id between", value1, value2, "plsMatchManageId");
            return (Criteria) this;
        }

        public Criteria andPlsMatchManageIdNotBetween(String value1, String value2) {
            addCriterion("pls_match_manage_id not between", value1, value2, "plsMatchManageId");
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

        public Criteria andThirdMatchSourceIdIsNull() {
            addCriterion("third_match_source_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdIsNotNull() {
            addCriterion("third_match_source_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdEqualTo(String value) {
            addCriterion("third_match_source_id =", value, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdNotEqualTo(String value) {
            addCriterion("third_match_source_id <>", value, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdGreaterThan(String value) {
            addCriterion("third_match_source_id >", value, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdGreaterThanOrEqualTo(String value) {
            addCriterion("third_match_source_id >=", value, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdLessThan(String value) {
            addCriterion("third_match_source_id <", value, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdLessThanOrEqualTo(String value) {
            addCriterion("third_match_source_id <=", value, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdLike(String value) {
            addCriterion("third_match_source_id like", value, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdNotLike(String value) {
            addCriterion("third_match_source_id not like", value, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdIn(List<String> values) {
            addCriterion("third_match_source_id in", values, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdNotIn(List<String> values) {
            addCriterion("third_match_source_id not in", values, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdBetween(String value1, String value2) {
            addCriterion("third_match_source_id between", value1, value2, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchSourceIdNotBetween(String value1, String value2) {
            addCriterion("third_match_source_id not between", value1, value2, "thirdMatchSourceId");
            return (Criteria) this;
        }

        public Criteria andIsStandardFlagIsNull() {
            addCriterion("is_standard_flag is null");
            return (Criteria) this;
        }

        public Criteria andIsStandardFlagIsNotNull() {
            addCriterion("is_standard_flag is not null");
            return (Criteria) this;
        }

        public Criteria andIsStandardFlagEqualTo(Integer value) {
            addCriterion("is_standard_flag =", value, "isStandardFlag");
            return (Criteria) this;
        }

        public Criteria andIsStandardFlagNotEqualTo(Integer value) {
            addCriterion("is_standard_flag <>", value, "isStandardFlag");
            return (Criteria) this;
        }

        public Criteria andIsStandardFlagGreaterThan(Integer value) {
            addCriterion("is_standard_flag >", value, "isStandardFlag");
            return (Criteria) this;
        }

        public Criteria andIsStandardFlagGreaterThanOrEqualTo(Integer value) {
            addCriterion("is_standard_flag >=", value, "isStandardFlag");
            return (Criteria) this;
        }

        public Criteria andIsStandardFlagLessThan(Integer value) {
            addCriterion("is_standard_flag <", value, "isStandardFlag");
            return (Criteria) this;
        }

        public Criteria andIsStandardFlagLessThanOrEqualTo(Integer value) {
            addCriterion("is_standard_flag <=", value, "isStandardFlag");
            return (Criteria) this;
        }

        public Criteria andIsStandardFlagIn(List<Integer> values) {
            addCriterion("is_standard_flag in", values, "isStandardFlag");
            return (Criteria) this;
        }

        public Criteria andIsStandardFlagNotIn(List<Integer> values) {
            addCriterion("is_standard_flag not in", values, "isStandardFlag");
            return (Criteria) this;
        }

        public Criteria andIsStandardFlagBetween(Integer value1, Integer value2) {
            addCriterion("is_standard_flag between", value1, value2, "isStandardFlag");
            return (Criteria) this;
        }

        public Criteria andIsStandardFlagNotBetween(Integer value1, Integer value2) {
            addCriterion("is_standard_flag not between", value1, value2, "isStandardFlag");
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