package com.panda.merge.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ThirdMatchHistoryExpressionExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ThirdMatchHistoryExpressionExample() {
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

        public Criteria andIdEqualTo(String value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(String value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(String value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(String value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(String value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(String value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLike(String value) {
            addCriterion("id like", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotLike(String value) {
            addCriterion("id not like", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<String> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<String> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(String value1, String value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(String value1, String value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentSourceIdIsNull() {
            addCriterion("third_tournament_source_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentSourceIdIsNotNull() {
            addCriterion("third_tournament_source_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentSourceIdEqualTo(String value) {
            addCriterion("third_tournament_source_id =", value, "thirdTournamentSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentSourceIdNotEqualTo(String value) {
            addCriterion("third_tournament_source_id <>", value, "thirdTournamentSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentSourceIdGreaterThan(String value) {
            addCriterion("third_tournament_source_id >", value, "thirdTournamentSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentSourceIdGreaterThanOrEqualTo(String value) {
            addCriterion("third_tournament_source_id >=", value, "thirdTournamentSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentSourceIdLessThan(String value) {
            addCriterion("third_tournament_source_id <", value, "thirdTournamentSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentSourceIdLessThanOrEqualTo(String value) {
            addCriterion("third_tournament_source_id <=", value, "thirdTournamentSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentSourceIdLike(String value) {
            addCriterion("third_tournament_source_id like", value, "thirdTournamentSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentSourceIdNotLike(String value) {
            addCriterion("third_tournament_source_id not like", value, "thirdTournamentSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentSourceIdIn(List<String> values) {
            addCriterion("third_tournament_source_id in", values, "thirdTournamentSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentSourceIdNotIn(List<String> values) {
            addCriterion("third_tournament_source_id not in", values, "thirdTournamentSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentSourceIdBetween(String value1, String value2) {
            addCriterion("third_tournament_source_id between", value1, value2, "thirdTournamentSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTournamentSourceIdNotBetween(String value1, String value2) {
            addCriterion("third_tournament_source_id not between", value1, value2, "thirdTournamentSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamSourceIdIsNull() {
            addCriterion("third_team_source_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdTeamSourceIdIsNotNull() {
            addCriterion("third_team_source_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdTeamSourceIdEqualTo(String value) {
            addCriterion("third_team_source_id =", value, "thirdTeamSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamSourceIdNotEqualTo(String value) {
            addCriterion("third_team_source_id <>", value, "thirdTeamSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamSourceIdGreaterThan(String value) {
            addCriterion("third_team_source_id >", value, "thirdTeamSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamSourceIdGreaterThanOrEqualTo(String value) {
            addCriterion("third_team_source_id >=", value, "thirdTeamSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamSourceIdLessThan(String value) {
            addCriterion("third_team_source_id <", value, "thirdTeamSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamSourceIdLessThanOrEqualTo(String value) {
            addCriterion("third_team_source_id <=", value, "thirdTeamSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamSourceIdLike(String value) {
            addCriterion("third_team_source_id like", value, "thirdTeamSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamSourceIdNotLike(String value) {
            addCriterion("third_team_source_id not like", value, "thirdTeamSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamSourceIdIn(List<String> values) {
            addCriterion("third_team_source_id in", values, "thirdTeamSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamSourceIdNotIn(List<String> values) {
            addCriterion("third_team_source_id not in", values, "thirdTeamSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamSourceIdBetween(String value1, String value2) {
            addCriterion("third_team_source_id between", value1, value2, "thirdTeamSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamSourceIdNotBetween(String value1, String value2) {
            addCriterion("third_team_source_id not between", value1, value2, "thirdTeamSourceId");
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

        public Criteria andTeamCnNameIsNull() {
            addCriterion("team_cn_name is null");
            return (Criteria) this;
        }

        public Criteria andTeamCnNameIsNotNull() {
            addCriterion("team_cn_name is not null");
            return (Criteria) this;
        }

        public Criteria andTeamCnNameEqualTo(String value) {
            addCriterion("team_cn_name =", value, "teamCnName");
            return (Criteria) this;
        }

        public Criteria andTeamCnNameNotEqualTo(String value) {
            addCriterion("team_cn_name <>", value, "teamCnName");
            return (Criteria) this;
        }

        public Criteria andTeamCnNameGreaterThan(String value) {
            addCriterion("team_cn_name >", value, "teamCnName");
            return (Criteria) this;
        }

        public Criteria andTeamCnNameGreaterThanOrEqualTo(String value) {
            addCriterion("team_cn_name >=", value, "teamCnName");
            return (Criteria) this;
        }

        public Criteria andTeamCnNameLessThan(String value) {
            addCriterion("team_cn_name <", value, "teamCnName");
            return (Criteria) this;
        }

        public Criteria andTeamCnNameLessThanOrEqualTo(String value) {
            addCriterion("team_cn_name <=", value, "teamCnName");
            return (Criteria) this;
        }

        public Criteria andTeamCnNameLike(String value) {
            addCriterion("team_cn_name like", value, "teamCnName");
            return (Criteria) this;
        }

        public Criteria andTeamCnNameNotLike(String value) {
            addCriterion("team_cn_name not like", value, "teamCnName");
            return (Criteria) this;
        }

        public Criteria andTeamCnNameIn(List<String> values) {
            addCriterion("team_cn_name in", values, "teamCnName");
            return (Criteria) this;
        }

        public Criteria andTeamCnNameNotIn(List<String> values) {
            addCriterion("team_cn_name not in", values, "teamCnName");
            return (Criteria) this;
        }

        public Criteria andTeamCnNameBetween(String value1, String value2) {
            addCriterion("team_cn_name between", value1, value2, "teamCnName");
            return (Criteria) this;
        }

        public Criteria andTeamCnNameNotBetween(String value1, String value2) {
            addCriterion("team_cn_name not between", value1, value2, "teamCnName");
            return (Criteria) this;
        }

        public Criteria andTeamEnNameIsNull() {
            addCriterion("team_en_name is null");
            return (Criteria) this;
        }

        public Criteria andTeamEnNameIsNotNull() {
            addCriterion("team_en_name is not null");
            return (Criteria) this;
        }

        public Criteria andTeamEnNameEqualTo(String value) {
            addCriterion("team_en_name =", value, "teamEnName");
            return (Criteria) this;
        }

        public Criteria andTeamEnNameNotEqualTo(String value) {
            addCriterion("team_en_name <>", value, "teamEnName");
            return (Criteria) this;
        }

        public Criteria andTeamEnNameGreaterThan(String value) {
            addCriterion("team_en_name >", value, "teamEnName");
            return (Criteria) this;
        }

        public Criteria andTeamEnNameGreaterThanOrEqualTo(String value) {
            addCriterion("team_en_name >=", value, "teamEnName");
            return (Criteria) this;
        }

        public Criteria andTeamEnNameLessThan(String value) {
            addCriterion("team_en_name <", value, "teamEnName");
            return (Criteria) this;
        }

        public Criteria andTeamEnNameLessThanOrEqualTo(String value) {
            addCriterion("team_en_name <=", value, "teamEnName");
            return (Criteria) this;
        }

        public Criteria andTeamEnNameLike(String value) {
            addCriterion("team_en_name like", value, "teamEnName");
            return (Criteria) this;
        }

        public Criteria andTeamEnNameNotLike(String value) {
            addCriterion("team_en_name not like", value, "teamEnName");
            return (Criteria) this;
        }

        public Criteria andTeamEnNameIn(List<String> values) {
            addCriterion("team_en_name in", values, "teamEnName");
            return (Criteria) this;
        }

        public Criteria andTeamEnNameNotIn(List<String> values) {
            addCriterion("team_en_name not in", values, "teamEnName");
            return (Criteria) this;
        }

        public Criteria andTeamEnNameBetween(String value1, String value2) {
            addCriterion("team_en_name between", value1, value2, "teamEnName");
            return (Criteria) this;
        }

        public Criteria andTeamEnNameNotBetween(String value1, String value2) {
            addCriterion("team_en_name not between", value1, value2, "teamEnName");
            return (Criteria) this;
        }

        public Criteria andEditStatusIsNull() {
            addCriterion("edit_status is null");
            return (Criteria) this;
        }

        public Criteria andEditStatusIsNotNull() {
            addCriterion("edit_status is not null");
            return (Criteria) this;
        }

        public Criteria andEditStatusEqualTo(Integer value) {
            addCriterion("edit_status =", value, "editStatus");
            return (Criteria) this;
        }

        public Criteria andEditStatusNotEqualTo(Integer value) {
            addCriterion("edit_status <>", value, "editStatus");
            return (Criteria) this;
        }

        public Criteria andEditStatusGreaterThan(Integer value) {
            addCriterion("edit_status >", value, "editStatus");
            return (Criteria) this;
        }

        public Criteria andEditStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("edit_status >=", value, "editStatus");
            return (Criteria) this;
        }

        public Criteria andEditStatusLessThan(Integer value) {
            addCriterion("edit_status <", value, "editStatus");
            return (Criteria) this;
        }

        public Criteria andEditStatusLessThanOrEqualTo(Integer value) {
            addCriterion("edit_status <=", value, "editStatus");
            return (Criteria) this;
        }

        public Criteria andEditStatusIn(List<Integer> values) {
            addCriterion("edit_status in", values, "editStatus");
            return (Criteria) this;
        }

        public Criteria andEditStatusNotIn(List<Integer> values) {
            addCriterion("edit_status not in", values, "editStatus");
            return (Criteria) this;
        }

        public Criteria andEditStatusBetween(Integer value1, Integer value2) {
            addCriterion("edit_status between", value1, value2, "editStatus");
            return (Criteria) this;
        }

        public Criteria andEditStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("edit_status not between", value1, value2, "editStatus");
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

        public Criteria andExpressionRankingIsNull() {
            addCriterion("expression_ranking is null");
            return (Criteria) this;
        }

        public Criteria andExpressionRankingIsNotNull() {
            addCriterion("expression_ranking is not null");
            return (Criteria) this;
        }

        public Criteria andExpressionRankingEqualTo(String value) {
            addCriterion("expression_ranking =", value, "expressionRanking");
            return (Criteria) this;
        }

        public Criteria andExpressionRankingNotEqualTo(String value) {
            addCriterion("expression_ranking <>", value, "expressionRanking");
            return (Criteria) this;
        }

        public Criteria andExpressionRankingGreaterThan(String value) {
            addCriterion("expression_ranking >", value, "expressionRanking");
            return (Criteria) this;
        }

        public Criteria andExpressionRankingGreaterThanOrEqualTo(String value) {
            addCriterion("expression_ranking >=", value, "expressionRanking");
            return (Criteria) this;
        }

        public Criteria andExpressionRankingLessThan(String value) {
            addCriterion("expression_ranking <", value, "expressionRanking");
            return (Criteria) this;
        }

        public Criteria andExpressionRankingLessThanOrEqualTo(String value) {
            addCriterion("expression_ranking <=", value, "expressionRanking");
            return (Criteria) this;
        }

        public Criteria andExpressionRankingLike(String value) {
            addCriterion("expression_ranking like", value, "expressionRanking");
            return (Criteria) this;
        }

        public Criteria andExpressionRankingNotLike(String value) {
            addCriterion("expression_ranking not like", value, "expressionRanking");
            return (Criteria) this;
        }

        public Criteria andExpressionRankingIn(List<String> values) {
            addCriterion("expression_ranking in", values, "expressionRanking");
            return (Criteria) this;
        }

        public Criteria andExpressionRankingNotIn(List<String> values) {
            addCriterion("expression_ranking not in", values, "expressionRanking");
            return (Criteria) this;
        }

        public Criteria andExpressionRankingBetween(String value1, String value2) {
            addCriterion("expression_ranking between", value1, value2, "expressionRanking");
            return (Criteria) this;
        }

        public Criteria andExpressionRankingNotBetween(String value1, String value2) {
            addCriterion("expression_ranking not between", value1, value2, "expressionRanking");
            return (Criteria) this;
        }

        public Criteria andExpressingTypeIsNull() {
            addCriterion("expressing_type is null");
            return (Criteria) this;
        }

        public Criteria andExpressingTypeIsNotNull() {
            addCriterion("expressing_type is not null");
            return (Criteria) this;
        }

        public Criteria andExpressingTypeEqualTo(Integer value) {
            addCriterion("expressing_type =", value, "expressingType");
            return (Criteria) this;
        }

        public Criteria andExpressingTypeNotEqualTo(Integer value) {
            addCriterion("expressing_type <>", value, "expressingType");
            return (Criteria) this;
        }

        public Criteria andExpressingTypeGreaterThan(Integer value) {
            addCriterion("expressing_type >", value, "expressingType");
            return (Criteria) this;
        }

        public Criteria andExpressingTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("expressing_type >=", value, "expressingType");
            return (Criteria) this;
        }

        public Criteria andExpressingTypeLessThan(Integer value) {
            addCriterion("expressing_type <", value, "expressingType");
            return (Criteria) this;
        }

        public Criteria andExpressingTypeLessThanOrEqualTo(Integer value) {
            addCriterion("expressing_type <=", value, "expressingType");
            return (Criteria) this;
        }

        public Criteria andExpressingTypeIn(List<Integer> values) {
            addCriterion("expressing_type in", values, "expressingType");
            return (Criteria) this;
        }

        public Criteria andExpressingTypeNotIn(List<Integer> values) {
            addCriterion("expressing_type not in", values, "expressingType");
            return (Criteria) this;
        }

        public Criteria andExpressingTypeBetween(Integer value1, Integer value2) {
            addCriterion("expressing_type between", value1, value2, "expressingType");
            return (Criteria) this;
        }

        public Criteria andExpressingTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("expressing_type not between", value1, value2, "expressingType");
            return (Criteria) this;
        }

        public Criteria andFirstStatusIsNull() {
            addCriterion("first_status is null");
            return (Criteria) this;
        }

        public Criteria andFirstStatusIsNotNull() {
            addCriterion("first_status is not null");
            return (Criteria) this;
        }

        public Criteria andFirstStatusEqualTo(Integer value) {
            addCriterion("first_status =", value, "firstStatus");
            return (Criteria) this;
        }

        public Criteria andFirstStatusNotEqualTo(Integer value) {
            addCriterion("first_status <>", value, "firstStatus");
            return (Criteria) this;
        }

        public Criteria andFirstStatusGreaterThan(Integer value) {
            addCriterion("first_status >", value, "firstStatus");
            return (Criteria) this;
        }

        public Criteria andFirstStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("first_status >=", value, "firstStatus");
            return (Criteria) this;
        }

        public Criteria andFirstStatusLessThan(Integer value) {
            addCriterion("first_status <", value, "firstStatus");
            return (Criteria) this;
        }

        public Criteria andFirstStatusLessThanOrEqualTo(Integer value) {
            addCriterion("first_status <=", value, "firstStatus");
            return (Criteria) this;
        }

        public Criteria andFirstStatusIn(List<Integer> values) {
            addCriterion("first_status in", values, "firstStatus");
            return (Criteria) this;
        }

        public Criteria andFirstStatusNotIn(List<Integer> values) {
            addCriterion("first_status not in", values, "firstStatus");
            return (Criteria) this;
        }

        public Criteria andFirstStatusBetween(Integer value1, Integer value2) {
            addCriterion("first_status between", value1, value2, "firstStatus");
            return (Criteria) this;
        }

        public Criteria andFirstStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("first_status not between", value1, value2, "firstStatus");
            return (Criteria) this;
        }

        public Criteria andSecondStatusIsNull() {
            addCriterion("second_status is null");
            return (Criteria) this;
        }

        public Criteria andSecondStatusIsNotNull() {
            addCriterion("second_status is not null");
            return (Criteria) this;
        }

        public Criteria andSecondStatusEqualTo(Integer value) {
            addCriterion("second_status =", value, "secondStatus");
            return (Criteria) this;
        }

        public Criteria andSecondStatusNotEqualTo(Integer value) {
            addCriterion("second_status <>", value, "secondStatus");
            return (Criteria) this;
        }

        public Criteria andSecondStatusGreaterThan(Integer value) {
            addCriterion("second_status >", value, "secondStatus");
            return (Criteria) this;
        }

        public Criteria andSecondStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("second_status >=", value, "secondStatus");
            return (Criteria) this;
        }

        public Criteria andSecondStatusLessThan(Integer value) {
            addCriterion("second_status <", value, "secondStatus");
            return (Criteria) this;
        }

        public Criteria andSecondStatusLessThanOrEqualTo(Integer value) {
            addCriterion("second_status <=", value, "secondStatus");
            return (Criteria) this;
        }

        public Criteria andSecondStatusIn(List<Integer> values) {
            addCriterion("second_status in", values, "secondStatus");
            return (Criteria) this;
        }

        public Criteria andSecondStatusNotIn(List<Integer> values) {
            addCriterion("second_status not in", values, "secondStatus");
            return (Criteria) this;
        }

        public Criteria andSecondStatusBetween(Integer value1, Integer value2) {
            addCriterion("second_status between", value1, value2, "secondStatus");
            return (Criteria) this;
        }

        public Criteria andSecondStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("second_status not between", value1, value2, "secondStatus");
            return (Criteria) this;
        }

        public Criteria andThirdStatusIsNull() {
            addCriterion("third_status is null");
            return (Criteria) this;
        }

        public Criteria andThirdStatusIsNotNull() {
            addCriterion("third_status is not null");
            return (Criteria) this;
        }

        public Criteria andThirdStatusEqualTo(Integer value) {
            addCriterion("third_status =", value, "thirdStatus");
            return (Criteria) this;
        }

        public Criteria andThirdStatusNotEqualTo(Integer value) {
            addCriterion("third_status <>", value, "thirdStatus");
            return (Criteria) this;
        }

        public Criteria andThirdStatusGreaterThan(Integer value) {
            addCriterion("third_status >", value, "thirdStatus");
            return (Criteria) this;
        }

        public Criteria andThirdStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("third_status >=", value, "thirdStatus");
            return (Criteria) this;
        }

        public Criteria andThirdStatusLessThan(Integer value) {
            addCriterion("third_status <", value, "thirdStatus");
            return (Criteria) this;
        }

        public Criteria andThirdStatusLessThanOrEqualTo(Integer value) {
            addCriterion("third_status <=", value, "thirdStatus");
            return (Criteria) this;
        }

        public Criteria andThirdStatusIn(List<Integer> values) {
            addCriterion("third_status in", values, "thirdStatus");
            return (Criteria) this;
        }

        public Criteria andThirdStatusNotIn(List<Integer> values) {
            addCriterion("third_status not in", values, "thirdStatus");
            return (Criteria) this;
        }

        public Criteria andThirdStatusBetween(Integer value1, Integer value2) {
            addCriterion("third_status between", value1, value2, "thirdStatus");
            return (Criteria) this;
        }

        public Criteria andThirdStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("third_status not between", value1, value2, "thirdStatus");
            return (Criteria) this;
        }

        public Criteria andFourthStatusIsNull() {
            addCriterion("fourth_status is null");
            return (Criteria) this;
        }

        public Criteria andFourthStatusIsNotNull() {
            addCriterion("fourth_status is not null");
            return (Criteria) this;
        }

        public Criteria andFourthStatusEqualTo(Integer value) {
            addCriterion("fourth_status =", value, "fourthStatus");
            return (Criteria) this;
        }

        public Criteria andFourthStatusNotEqualTo(Integer value) {
            addCriterion("fourth_status <>", value, "fourthStatus");
            return (Criteria) this;
        }

        public Criteria andFourthStatusGreaterThan(Integer value) {
            addCriterion("fourth_status >", value, "fourthStatus");
            return (Criteria) this;
        }

        public Criteria andFourthStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("fourth_status >=", value, "fourthStatus");
            return (Criteria) this;
        }

        public Criteria andFourthStatusLessThan(Integer value) {
            addCriterion("fourth_status <", value, "fourthStatus");
            return (Criteria) this;
        }

        public Criteria andFourthStatusLessThanOrEqualTo(Integer value) {
            addCriterion("fourth_status <=", value, "fourthStatus");
            return (Criteria) this;
        }

        public Criteria andFourthStatusIn(List<Integer> values) {
            addCriterion("fourth_status in", values, "fourthStatus");
            return (Criteria) this;
        }

        public Criteria andFourthStatusNotIn(List<Integer> values) {
            addCriterion("fourth_status not in", values, "fourthStatus");
            return (Criteria) this;
        }

        public Criteria andFourthStatusBetween(Integer value1, Integer value2) {
            addCriterion("fourth_status between", value1, value2, "fourthStatus");
            return (Criteria) this;
        }

        public Criteria andFourthStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("fourth_status not between", value1, value2, "fourthStatus");
            return (Criteria) this;
        }

        public Criteria andFifthStatusIsNull() {
            addCriterion("fifth_status is null");
            return (Criteria) this;
        }

        public Criteria andFifthStatusIsNotNull() {
            addCriterion("fifth_status is not null");
            return (Criteria) this;
        }

        public Criteria andFifthStatusEqualTo(Integer value) {
            addCriterion("fifth_status =", value, "fifthStatus");
            return (Criteria) this;
        }

        public Criteria andFifthStatusNotEqualTo(Integer value) {
            addCriterion("fifth_status <>", value, "fifthStatus");
            return (Criteria) this;
        }

        public Criteria andFifthStatusGreaterThan(Integer value) {
            addCriterion("fifth_status >", value, "fifthStatus");
            return (Criteria) this;
        }

        public Criteria andFifthStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("fifth_status >=", value, "fifthStatus");
            return (Criteria) this;
        }

        public Criteria andFifthStatusLessThan(Integer value) {
            addCriterion("fifth_status <", value, "fifthStatus");
            return (Criteria) this;
        }

        public Criteria andFifthStatusLessThanOrEqualTo(Integer value) {
            addCriterion("fifth_status <=", value, "fifthStatus");
            return (Criteria) this;
        }

        public Criteria andFifthStatusIn(List<Integer> values) {
            addCriterion("fifth_status in", values, "fifthStatus");
            return (Criteria) this;
        }

        public Criteria andFifthStatusNotIn(List<Integer> values) {
            addCriterion("fifth_status not in", values, "fifthStatus");
            return (Criteria) this;
        }

        public Criteria andFifthStatusBetween(Integer value1, Integer value2) {
            addCriterion("fifth_status between", value1, value2, "fifthStatus");
            return (Criteria) this;
        }

        public Criteria andFifthStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("fifth_status not between", value1, value2, "fifthStatus");
            return (Criteria) this;
        }

        public Criteria andGoalsForTotalIsNull() {
            addCriterion("goals_for_total is null");
            return (Criteria) this;
        }

        public Criteria andGoalsForTotalIsNotNull() {
            addCriterion("goals_for_total is not null");
            return (Criteria) this;
        }

        public Criteria andGoalsForTotalEqualTo(Integer value) {
            addCriterion("goals_for_total =", value, "goalsForTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsForTotalNotEqualTo(Integer value) {
            addCriterion("goals_for_total <>", value, "goalsForTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsForTotalGreaterThan(Integer value) {
            addCriterion("goals_for_total >", value, "goalsForTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsForTotalGreaterThanOrEqualTo(Integer value) {
            addCriterion("goals_for_total >=", value, "goalsForTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsForTotalLessThan(Integer value) {
            addCriterion("goals_for_total <", value, "goalsForTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsForTotalLessThanOrEqualTo(Integer value) {
            addCriterion("goals_for_total <=", value, "goalsForTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsForTotalIn(List<Integer> values) {
            addCriterion("goals_for_total in", values, "goalsForTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsForTotalNotIn(List<Integer> values) {
            addCriterion("goals_for_total not in", values, "goalsForTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsForTotalBetween(Integer value1, Integer value2) {
            addCriterion("goals_for_total between", value1, value2, "goalsForTotal");
            return (Criteria) this;
        }

        public Criteria andGoalsForTotalNotBetween(Integer value1, Integer value2) {
            addCriterion("goals_for_total not between", value1, value2, "goalsForTotal");
            return (Criteria) this;
        }

        public Criteria andAverageGoalIsNull() {
            addCriterion("average_goal is null");
            return (Criteria) this;
        }

        public Criteria andAverageGoalIsNotNull() {
            addCriterion("average_goal is not null");
            return (Criteria) this;
        }

        public Criteria andAverageGoalEqualTo(BigDecimal value) {
            addCriterion("average_goal =", value, "averageGoal");
            return (Criteria) this;
        }

        public Criteria andAverageGoalNotEqualTo(BigDecimal value) {
            addCriterion("average_goal <>", value, "averageGoal");
            return (Criteria) this;
        }

        public Criteria andAverageGoalGreaterThan(BigDecimal value) {
            addCriterion("average_goal >", value, "averageGoal");
            return (Criteria) this;
        }

        public Criteria andAverageGoalGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("average_goal >=", value, "averageGoal");
            return (Criteria) this;
        }

        public Criteria andAverageGoalLessThan(BigDecimal value) {
            addCriterion("average_goal <", value, "averageGoal");
            return (Criteria) this;
        }

        public Criteria andAverageGoalLessThanOrEqualTo(BigDecimal value) {
            addCriterion("average_goal <=", value, "averageGoal");
            return (Criteria) this;
        }

        public Criteria andAverageGoalIn(List<BigDecimal> values) {
            addCriterion("average_goal in", values, "averageGoal");
            return (Criteria) this;
        }

        public Criteria andAverageGoalNotIn(List<BigDecimal> values) {
            addCriterion("average_goal not in", values, "averageGoal");
            return (Criteria) this;
        }

        public Criteria andAverageGoalBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("average_goal between", value1, value2, "averageGoal");
            return (Criteria) this;
        }

        public Criteria andAverageGoalNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("average_goal not between", value1, value2, "averageGoal");
            return (Criteria) this;
        }

        public Criteria andWinPercentIsNull() {
            addCriterion("win_percent is null");
            return (Criteria) this;
        }

        public Criteria andWinPercentIsNotNull() {
            addCriterion("win_percent is not null");
            return (Criteria) this;
        }

        public Criteria andWinPercentEqualTo(BigDecimal value) {
            addCriterion("win_percent =", value, "winPercent");
            return (Criteria) this;
        }

        public Criteria andWinPercentNotEqualTo(BigDecimal value) {
            addCriterion("win_percent <>", value, "winPercent");
            return (Criteria) this;
        }

        public Criteria andWinPercentGreaterThan(BigDecimal value) {
            addCriterion("win_percent >", value, "winPercent");
            return (Criteria) this;
        }

        public Criteria andWinPercentGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("win_percent >=", value, "winPercent");
            return (Criteria) this;
        }

        public Criteria andWinPercentLessThan(BigDecimal value) {
            addCriterion("win_percent <", value, "winPercent");
            return (Criteria) this;
        }

        public Criteria andWinPercentLessThanOrEqualTo(BigDecimal value) {
            addCriterion("win_percent <=", value, "winPercent");
            return (Criteria) this;
        }

        public Criteria andWinPercentIn(List<BigDecimal> values) {
            addCriterion("win_percent in", values, "winPercent");
            return (Criteria) this;
        }

        public Criteria andWinPercentNotIn(List<BigDecimal> values) {
            addCriterion("win_percent not in", values, "winPercent");
            return (Criteria) this;
        }

        public Criteria andWinPercentBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("win_percent between", value1, value2, "winPercent");
            return (Criteria) this;
        }

        public Criteria andWinPercentNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("win_percent not between", value1, value2, "winPercent");
            return (Criteria) this;
        }

        public Criteria andBothGoalPercentIsNull() {
            addCriterion("both_goal_percent is null");
            return (Criteria) this;
        }

        public Criteria andBothGoalPercentIsNotNull() {
            addCriterion("both_goal_percent is not null");
            return (Criteria) this;
        }

        public Criteria andBothGoalPercentEqualTo(BigDecimal value) {
            addCriterion("both_goal_percent =", value, "bothGoalPercent");
            return (Criteria) this;
        }

        public Criteria andBothGoalPercentNotEqualTo(BigDecimal value) {
            addCriterion("both_goal_percent <>", value, "bothGoalPercent");
            return (Criteria) this;
        }

        public Criteria andBothGoalPercentGreaterThan(BigDecimal value) {
            addCriterion("both_goal_percent >", value, "bothGoalPercent");
            return (Criteria) this;
        }

        public Criteria andBothGoalPercentGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("both_goal_percent >=", value, "bothGoalPercent");
            return (Criteria) this;
        }

        public Criteria andBothGoalPercentLessThan(BigDecimal value) {
            addCriterion("both_goal_percent <", value, "bothGoalPercent");
            return (Criteria) this;
        }

        public Criteria andBothGoalPercentLessThanOrEqualTo(BigDecimal value) {
            addCriterion("both_goal_percent <=", value, "bothGoalPercent");
            return (Criteria) this;
        }

        public Criteria andBothGoalPercentIn(List<BigDecimal> values) {
            addCriterion("both_goal_percent in", values, "bothGoalPercent");
            return (Criteria) this;
        }

        public Criteria andBothGoalPercentNotIn(List<BigDecimal> values) {
            addCriterion("both_goal_percent not in", values, "bothGoalPercent");
            return (Criteria) this;
        }

        public Criteria andBothGoalPercentBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("both_goal_percent between", value1, value2, "bothGoalPercent");
            return (Criteria) this;
        }

        public Criteria andBothGoalPercentNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("both_goal_percent not between", value1, value2, "bothGoalPercent");
            return (Criteria) this;
        }

        public Criteria andNotLostPercentIsNull() {
            addCriterion("not_lost_percent is null");
            return (Criteria) this;
        }

        public Criteria andNotLostPercentIsNotNull() {
            addCriterion("not_lost_percent is not null");
            return (Criteria) this;
        }

        public Criteria andNotLostPercentEqualTo(BigDecimal value) {
            addCriterion("not_lost_percent =", value, "notLostPercent");
            return (Criteria) this;
        }

        public Criteria andNotLostPercentNotEqualTo(BigDecimal value) {
            addCriterion("not_lost_percent <>", value, "notLostPercent");
            return (Criteria) this;
        }

        public Criteria andNotLostPercentGreaterThan(BigDecimal value) {
            addCriterion("not_lost_percent >", value, "notLostPercent");
            return (Criteria) this;
        }

        public Criteria andNotLostPercentGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("not_lost_percent >=", value, "notLostPercent");
            return (Criteria) this;
        }

        public Criteria andNotLostPercentLessThan(BigDecimal value) {
            addCriterion("not_lost_percent <", value, "notLostPercent");
            return (Criteria) this;
        }

        public Criteria andNotLostPercentLessThanOrEqualTo(BigDecimal value) {
            addCriterion("not_lost_percent <=", value, "notLostPercent");
            return (Criteria) this;
        }

        public Criteria andNotLostPercentIn(List<BigDecimal> values) {
            addCriterion("not_lost_percent in", values, "notLostPercent");
            return (Criteria) this;
        }

        public Criteria andNotLostPercentNotIn(List<BigDecimal> values) {
            addCriterion("not_lost_percent not in", values, "notLostPercent");
            return (Criteria) this;
        }

        public Criteria andNotLostPercentBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("not_lost_percent between", value1, value2, "notLostPercent");
            return (Criteria) this;
        }

        public Criteria andNotLostPercentNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("not_lost_percent not between", value1, value2, "notLostPercent");
            return (Criteria) this;
        }

        public Criteria andFirstGoalPercentIsNull() {
            addCriterion("first_goal_percent is null");
            return (Criteria) this;
        }

        public Criteria andFirstGoalPercentIsNotNull() {
            addCriterion("first_goal_percent is not null");
            return (Criteria) this;
        }

        public Criteria andFirstGoalPercentEqualTo(BigDecimal value) {
            addCriterion("first_goal_percent =", value, "firstGoalPercent");
            return (Criteria) this;
        }

        public Criteria andFirstGoalPercentNotEqualTo(BigDecimal value) {
            addCriterion("first_goal_percent <>", value, "firstGoalPercent");
            return (Criteria) this;
        }

        public Criteria andFirstGoalPercentGreaterThan(BigDecimal value) {
            addCriterion("first_goal_percent >", value, "firstGoalPercent");
            return (Criteria) this;
        }

        public Criteria andFirstGoalPercentGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("first_goal_percent >=", value, "firstGoalPercent");
            return (Criteria) this;
        }

        public Criteria andFirstGoalPercentLessThan(BigDecimal value) {
            addCriterion("first_goal_percent <", value, "firstGoalPercent");
            return (Criteria) this;
        }

        public Criteria andFirstGoalPercentLessThanOrEqualTo(BigDecimal value) {
            addCriterion("first_goal_percent <=", value, "firstGoalPercent");
            return (Criteria) this;
        }

        public Criteria andFirstGoalPercentIn(List<BigDecimal> values) {
            addCriterion("first_goal_percent in", values, "firstGoalPercent");
            return (Criteria) this;
        }

        public Criteria andFirstGoalPercentNotIn(List<BigDecimal> values) {
            addCriterion("first_goal_percent not in", values, "firstGoalPercent");
            return (Criteria) this;
        }

        public Criteria andFirstGoalPercentBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("first_goal_percent between", value1, value2, "firstGoalPercent");
            return (Criteria) this;
        }

        public Criteria andFirstGoalPercentNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("first_goal_percent not between", value1, value2, "firstGoalPercent");
            return (Criteria) this;
        }

        public Criteria andAverageGoalPercentIsNull() {
            addCriterion("average_goal_percent is null");
            return (Criteria) this;
        }

        public Criteria andAverageGoalPercentIsNotNull() {
            addCriterion("average_goal_percent is not null");
            return (Criteria) this;
        }

        public Criteria andAverageGoalPercentEqualTo(BigDecimal value) {
            addCriterion("average_goal_percent =", value, "averageGoalPercent");
            return (Criteria) this;
        }

        public Criteria andAverageGoalPercentNotEqualTo(BigDecimal value) {
            addCriterion("average_goal_percent <>", value, "averageGoalPercent");
            return (Criteria) this;
        }

        public Criteria andAverageGoalPercentGreaterThan(BigDecimal value) {
            addCriterion("average_goal_percent >", value, "averageGoalPercent");
            return (Criteria) this;
        }

        public Criteria andAverageGoalPercentGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("average_goal_percent >=", value, "averageGoalPercent");
            return (Criteria) this;
        }

        public Criteria andAverageGoalPercentLessThan(BigDecimal value) {
            addCriterion("average_goal_percent <", value, "averageGoalPercent");
            return (Criteria) this;
        }

        public Criteria andAverageGoalPercentLessThanOrEqualTo(BigDecimal value) {
            addCriterion("average_goal_percent <=", value, "averageGoalPercent");
            return (Criteria) this;
        }

        public Criteria andAverageGoalPercentIn(List<BigDecimal> values) {
            addCriterion("average_goal_percent in", values, "averageGoalPercent");
            return (Criteria) this;
        }

        public Criteria andAverageGoalPercentNotIn(List<BigDecimal> values) {
            addCriterion("average_goal_percent not in", values, "averageGoalPercent");
            return (Criteria) this;
        }

        public Criteria andAverageGoalPercentBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("average_goal_percent between", value1, value2, "averageGoalPercent");
            return (Criteria) this;
        }

        public Criteria andAverageGoalPercentNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("average_goal_percent not between", value1, value2, "averageGoalPercent");
            return (Criteria) this;
        }

        public Criteria andGoalPercentIsNull() {
            addCriterion("goal_percent is null");
            return (Criteria) this;
        }

        public Criteria andGoalPercentIsNotNull() {
            addCriterion("goal_percent is not null");
            return (Criteria) this;
        }

        public Criteria andGoalPercentEqualTo(BigDecimal value) {
            addCriterion("goal_percent =", value, "goalPercent");
            return (Criteria) this;
        }

        public Criteria andGoalPercentNotEqualTo(BigDecimal value) {
            addCriterion("goal_percent <>", value, "goalPercent");
            return (Criteria) this;
        }

        public Criteria andGoalPercentGreaterThan(BigDecimal value) {
            addCriterion("goal_percent >", value, "goalPercent");
            return (Criteria) this;
        }

        public Criteria andGoalPercentGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("goal_percent >=", value, "goalPercent");
            return (Criteria) this;
        }

        public Criteria andGoalPercentLessThan(BigDecimal value) {
            addCriterion("goal_percent <", value, "goalPercent");
            return (Criteria) this;
        }

        public Criteria andGoalPercentLessThanOrEqualTo(BigDecimal value) {
            addCriterion("goal_percent <=", value, "goalPercent");
            return (Criteria) this;
        }

        public Criteria andGoalPercentIn(List<BigDecimal> values) {
            addCriterion("goal_percent in", values, "goalPercent");
            return (Criteria) this;
        }

        public Criteria andGoalPercentNotIn(List<BigDecimal> values) {
            addCriterion("goal_percent not in", values, "goalPercent");
            return (Criteria) this;
        }

        public Criteria andGoalPercentBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("goal_percent between", value1, value2, "goalPercent");
            return (Criteria) this;
        }

        public Criteria andGoalPercentNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("goal_percent not between", value1, value2, "goalPercent");
            return (Criteria) this;
        }

        public Criteria andLostGoalPercentIsNull() {
            addCriterion("lost_goal_percent is null");
            return (Criteria) this;
        }

        public Criteria andLostGoalPercentIsNotNull() {
            addCriterion("lost_goal_percent is not null");
            return (Criteria) this;
        }

        public Criteria andLostGoalPercentEqualTo(BigDecimal value) {
            addCriterion("lost_goal_percent =", value, "lostGoalPercent");
            return (Criteria) this;
        }

        public Criteria andLostGoalPercentNotEqualTo(BigDecimal value) {
            addCriterion("lost_goal_percent <>", value, "lostGoalPercent");
            return (Criteria) this;
        }

        public Criteria andLostGoalPercentGreaterThan(BigDecimal value) {
            addCriterion("lost_goal_percent >", value, "lostGoalPercent");
            return (Criteria) this;
        }

        public Criteria andLostGoalPercentGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("lost_goal_percent >=", value, "lostGoalPercent");
            return (Criteria) this;
        }

        public Criteria andLostGoalPercentLessThan(BigDecimal value) {
            addCriterion("lost_goal_percent <", value, "lostGoalPercent");
            return (Criteria) this;
        }

        public Criteria andLostGoalPercentLessThanOrEqualTo(BigDecimal value) {
            addCriterion("lost_goal_percent <=", value, "lostGoalPercent");
            return (Criteria) this;
        }

        public Criteria andLostGoalPercentIn(List<BigDecimal> values) {
            addCriterion("lost_goal_percent in", values, "lostGoalPercent");
            return (Criteria) this;
        }

        public Criteria andLostGoalPercentNotIn(List<BigDecimal> values) {
            addCriterion("lost_goal_percent not in", values, "lostGoalPercent");
            return (Criteria) this;
        }

        public Criteria andLostGoalPercentBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("lost_goal_percent between", value1, value2, "lostGoalPercent");
            return (Criteria) this;
        }

        public Criteria andLostGoalPercentNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("lost_goal_percent not between", value1, value2, "lostGoalPercent");
            return (Criteria) this;
        }

        public Criteria andGoalXgIsNull() {
            addCriterion("goal_xg is null");
            return (Criteria) this;
        }

        public Criteria andGoalXgIsNotNull() {
            addCriterion("goal_xg is not null");
            return (Criteria) this;
        }

        public Criteria andGoalXgEqualTo(BigDecimal value) {
            addCriterion("goal_xg =", value, "goalXg");
            return (Criteria) this;
        }

        public Criteria andGoalXgNotEqualTo(BigDecimal value) {
            addCriterion("goal_xg <>", value, "goalXg");
            return (Criteria) this;
        }

        public Criteria andGoalXgGreaterThan(BigDecimal value) {
            addCriterion("goal_xg >", value, "goalXg");
            return (Criteria) this;
        }

        public Criteria andGoalXgGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("goal_xg >=", value, "goalXg");
            return (Criteria) this;
        }

        public Criteria andGoalXgLessThan(BigDecimal value) {
            addCriterion("goal_xg <", value, "goalXg");
            return (Criteria) this;
        }

        public Criteria andGoalXgLessThanOrEqualTo(BigDecimal value) {
            addCriterion("goal_xg <=", value, "goalXg");
            return (Criteria) this;
        }

        public Criteria andGoalXgIn(List<BigDecimal> values) {
            addCriterion("goal_xg in", values, "goalXg");
            return (Criteria) this;
        }

        public Criteria andGoalXgNotIn(List<BigDecimal> values) {
            addCriterion("goal_xg not in", values, "goalXg");
            return (Criteria) this;
        }

        public Criteria andGoalXgBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("goal_xg between", value1, value2, "goalXg");
            return (Criteria) this;
        }

        public Criteria andGoalXgNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("goal_xg not between", value1, value2, "goalXg");
            return (Criteria) this;
        }

        public Criteria andGoalXgaIsNull() {
            addCriterion("goal_xga is null");
            return (Criteria) this;
        }

        public Criteria andGoalXgaIsNotNull() {
            addCriterion("goal_xga is not null");
            return (Criteria) this;
        }

        public Criteria andGoalXgaEqualTo(BigDecimal value) {
            addCriterion("goal_xga =", value, "goalXga");
            return (Criteria) this;
        }

        public Criteria andGoalXgaNotEqualTo(BigDecimal value) {
            addCriterion("goal_xga <>", value, "goalXga");
            return (Criteria) this;
        }

        public Criteria andGoalXgaGreaterThan(BigDecimal value) {
            addCriterion("goal_xga >", value, "goalXga");
            return (Criteria) this;
        }

        public Criteria andGoalXgaGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("goal_xga >=", value, "goalXga");
            return (Criteria) this;
        }

        public Criteria andGoalXgaLessThan(BigDecimal value) {
            addCriterion("goal_xga <", value, "goalXga");
            return (Criteria) this;
        }

        public Criteria andGoalXgaLessThanOrEqualTo(BigDecimal value) {
            addCriterion("goal_xga <=", value, "goalXga");
            return (Criteria) this;
        }

        public Criteria andGoalXgaIn(List<BigDecimal> values) {
            addCriterion("goal_xga in", values, "goalXga");
            return (Criteria) this;
        }

        public Criteria andGoalXgaNotIn(List<BigDecimal> values) {
            addCriterion("goal_xga not in", values, "goalXga");
            return (Criteria) this;
        }

        public Criteria andGoalXgaBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("goal_xga between", value1, value2, "goalXga");
            return (Criteria) this;
        }

        public Criteria andGoalXgaNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("goal_xga not between", value1, value2, "goalXga");
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