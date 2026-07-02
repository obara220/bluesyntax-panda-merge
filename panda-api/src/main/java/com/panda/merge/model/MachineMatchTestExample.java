package com.panda.merge.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MachineMatchTestExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MachineMatchTestExample() {
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

        public Criteria andRelationIdIsNull() {
            addCriterion("relation_id is null");
            return (Criteria) this;
        }

        public Criteria andRelationIdIsNotNull() {
            addCriterion("relation_id is not null");
            return (Criteria) this;
        }

        public Criteria andRelationIdEqualTo(String value) {
            addCriterion("relation_id =", value, "relationId");
            return (Criteria) this;
        }

        public Criteria andRelationIdNotEqualTo(String value) {
            addCriterion("relation_id <>", value, "relationId");
            return (Criteria) this;
        }

        public Criteria andRelationIdGreaterThan(String value) {
            addCriterion("relation_id >", value, "relationId");
            return (Criteria) this;
        }

        public Criteria andRelationIdGreaterThanOrEqualTo(String value) {
            addCriterion("relation_id >=", value, "relationId");
            return (Criteria) this;
        }

        public Criteria andRelationIdLessThan(String value) {
            addCriterion("relation_id <", value, "relationId");
            return (Criteria) this;
        }

        public Criteria andRelationIdLessThanOrEqualTo(String value) {
            addCriterion("relation_id <=", value, "relationId");
            return (Criteria) this;
        }

        public Criteria andRelationIdLike(String value) {
            addCriterion("relation_id like", value, "relationId");
            return (Criteria) this;
        }

        public Criteria andRelationIdNotLike(String value) {
            addCriterion("relation_id not like", value, "relationId");
            return (Criteria) this;
        }

        public Criteria andRelationIdIn(List<String> values) {
            addCriterion("relation_id in", values, "relationId");
            return (Criteria) this;
        }

        public Criteria andRelationIdNotIn(List<String> values) {
            addCriterion("relation_id not in", values, "relationId");
            return (Criteria) this;
        }

        public Criteria andRelationIdBetween(String value1, String value2) {
            addCriterion("relation_id between", value1, value2, "relationId");
            return (Criteria) this;
        }

        public Criteria andRelationIdNotBetween(String value1, String value2) {
            addCriterion("relation_id not between", value1, value2, "relationId");
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

        public Criteria andThirdMatchIdEqualTo(String value) {
            addCriterion("third_match_id =", value, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdNotEqualTo(String value) {
            addCriterion("third_match_id <>", value, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdGreaterThan(String value) {
            addCriterion("third_match_id >", value, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdGreaterThanOrEqualTo(String value) {
            addCriterion("third_match_id >=", value, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdLessThan(String value) {
            addCriterion("third_match_id <", value, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdLessThanOrEqualTo(String value) {
            addCriterion("third_match_id <=", value, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdLike(String value) {
            addCriterion("third_match_id like", value, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdNotLike(String value) {
            addCriterion("third_match_id not like", value, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdIn(List<String> values) {
            addCriterion("third_match_id in", values, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdNotIn(List<String> values) {
            addCriterion("third_match_id not in", values, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdBetween(String value1, String value2) {
            addCriterion("third_match_id between", value1, value2, "thirdMatchId");
            return (Criteria) this;
        }

        public Criteria andThirdMatchIdNotBetween(String value1, String value2) {
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

        public Criteria andSportIdEqualTo(String value) {
            addCriterion("sport_id =", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdNotEqualTo(String value) {
            addCriterion("sport_id <>", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdGreaterThan(String value) {
            addCriterion("sport_id >", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdGreaterThanOrEqualTo(String value) {
            addCriterion("sport_id >=", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdLessThan(String value) {
            addCriterion("sport_id <", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdLessThanOrEqualTo(String value) {
            addCriterion("sport_id <=", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdLike(String value) {
            addCriterion("sport_id like", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdNotLike(String value) {
            addCriterion("sport_id not like", value, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdIn(List<String> values) {
            addCriterion("sport_id in", values, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdNotIn(List<String> values) {
            addCriterion("sport_id not in", values, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdBetween(String value1, String value2) {
            addCriterion("sport_id between", value1, value2, "sportId");
            return (Criteria) this;
        }

        public Criteria andSportIdNotBetween(String value1, String value2) {
            addCriterion("sport_id not between", value1, value2, "sportId");
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

        public Criteria andFormatIsNull() {
            addCriterion("format is null");
            return (Criteria) this;
        }

        public Criteria andFormatIsNotNull() {
            addCriterion("format is not null");
            return (Criteria) this;
        }

        public Criteria andFormatEqualTo(String value) {
            addCriterion("format =", value, "format");
            return (Criteria) this;
        }

        public Criteria andFormatNotEqualTo(String value) {
            addCriterion("format <>", value, "format");
            return (Criteria) this;
        }

        public Criteria andFormatGreaterThan(String value) {
            addCriterion("format >", value, "format");
            return (Criteria) this;
        }

        public Criteria andFormatGreaterThanOrEqualTo(String value) {
            addCriterion("format >=", value, "format");
            return (Criteria) this;
        }

        public Criteria andFormatLessThan(String value) {
            addCriterion("format <", value, "format");
            return (Criteria) this;
        }

        public Criteria andFormatLessThanOrEqualTo(String value) {
            addCriterion("format <=", value, "format");
            return (Criteria) this;
        }

        public Criteria andFormatLike(String value) {
            addCriterion("format like", value, "format");
            return (Criteria) this;
        }

        public Criteria andFormatNotLike(String value) {
            addCriterion("format not like", value, "format");
            return (Criteria) this;
        }

        public Criteria andFormatIn(List<String> values) {
            addCriterion("format in", values, "format");
            return (Criteria) this;
        }

        public Criteria andFormatNotIn(List<String> values) {
            addCriterion("format not in", values, "format");
            return (Criteria) this;
        }

        public Criteria andFormatBetween(String value1, String value2) {
            addCriterion("format between", value1, value2, "format");
            return (Criteria) this;
        }

        public Criteria andFormatNotBetween(String value1, String value2) {
            addCriterion("format not between", value1, value2, "format");
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

        public Criteria andTournamentIdEqualTo(String value) {
            addCriterion("tournament_id =", value, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdNotEqualTo(String value) {
            addCriterion("tournament_id <>", value, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdGreaterThan(String value) {
            addCriterion("tournament_id >", value, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdGreaterThanOrEqualTo(String value) {
            addCriterion("tournament_id >=", value, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdLessThan(String value) {
            addCriterion("tournament_id <", value, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdLessThanOrEqualTo(String value) {
            addCriterion("tournament_id <=", value, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdLike(String value) {
            addCriterion("tournament_id like", value, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdNotLike(String value) {
            addCriterion("tournament_id not like", value, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdIn(List<String> values) {
            addCriterion("tournament_id in", values, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdNotIn(List<String> values) {
            addCriterion("tournament_id not in", values, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdBetween(String value1, String value2) {
            addCriterion("tournament_id between", value1, value2, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentIdNotBetween(String value1, String value2) {
            addCriterion("tournament_id not between", value1, value2, "tournamentId");
            return (Criteria) this;
        }

        public Criteria andTournamentNameCnIsNull() {
            addCriterion("tournament_name_cn is null");
            return (Criteria) this;
        }

        public Criteria andTournamentNameCnIsNotNull() {
            addCriterion("tournament_name_cn is not null");
            return (Criteria) this;
        }

        public Criteria andTournamentNameCnEqualTo(String value) {
            addCriterion("tournament_name_cn =", value, "tournamentNameCn");
            return (Criteria) this;
        }

        public Criteria andTournamentNameCnNotEqualTo(String value) {
            addCriterion("tournament_name_cn <>", value, "tournamentNameCn");
            return (Criteria) this;
        }

        public Criteria andTournamentNameCnGreaterThan(String value) {
            addCriterion("tournament_name_cn >", value, "tournamentNameCn");
            return (Criteria) this;
        }

        public Criteria andTournamentNameCnGreaterThanOrEqualTo(String value) {
            addCriterion("tournament_name_cn >=", value, "tournamentNameCn");
            return (Criteria) this;
        }

        public Criteria andTournamentNameCnLessThan(String value) {
            addCriterion("tournament_name_cn <", value, "tournamentNameCn");
            return (Criteria) this;
        }

        public Criteria andTournamentNameCnLessThanOrEqualTo(String value) {
            addCriterion("tournament_name_cn <=", value, "tournamentNameCn");
            return (Criteria) this;
        }

        public Criteria andTournamentNameCnLike(String value) {
            addCriterion("tournament_name_cn like", value, "tournamentNameCn");
            return (Criteria) this;
        }

        public Criteria andTournamentNameCnNotLike(String value) {
            addCriterion("tournament_name_cn not like", value, "tournamentNameCn");
            return (Criteria) this;
        }

        public Criteria andTournamentNameCnIn(List<String> values) {
            addCriterion("tournament_name_cn in", values, "tournamentNameCn");
            return (Criteria) this;
        }

        public Criteria andTournamentNameCnNotIn(List<String> values) {
            addCriterion("tournament_name_cn not in", values, "tournamentNameCn");
            return (Criteria) this;
        }

        public Criteria andTournamentNameCnBetween(String value1, String value2) {
            addCriterion("tournament_name_cn between", value1, value2, "tournamentNameCn");
            return (Criteria) this;
        }

        public Criteria andTournamentNameCnNotBetween(String value1, String value2) {
            addCriterion("tournament_name_cn not between", value1, value2, "tournamentNameCn");
            return (Criteria) this;
        }

        public Criteria andTournamentNameEnIsNull() {
            addCriterion("tournament_name_en is null");
            return (Criteria) this;
        }

        public Criteria andTournamentNameEnIsNotNull() {
            addCriterion("tournament_name_en is not null");
            return (Criteria) this;
        }

        public Criteria andTournamentNameEnEqualTo(String value) {
            addCriterion("tournament_name_en =", value, "tournamentNameEn");
            return (Criteria) this;
        }

        public Criteria andTournamentNameEnNotEqualTo(String value) {
            addCriterion("tournament_name_en <>", value, "tournamentNameEn");
            return (Criteria) this;
        }

        public Criteria andTournamentNameEnGreaterThan(String value) {
            addCriterion("tournament_name_en >", value, "tournamentNameEn");
            return (Criteria) this;
        }

        public Criteria andTournamentNameEnGreaterThanOrEqualTo(String value) {
            addCriterion("tournament_name_en >=", value, "tournamentNameEn");
            return (Criteria) this;
        }

        public Criteria andTournamentNameEnLessThan(String value) {
            addCriterion("tournament_name_en <", value, "tournamentNameEn");
            return (Criteria) this;
        }

        public Criteria andTournamentNameEnLessThanOrEqualTo(String value) {
            addCriterion("tournament_name_en <=", value, "tournamentNameEn");
            return (Criteria) this;
        }

        public Criteria andTournamentNameEnLike(String value) {
            addCriterion("tournament_name_en like", value, "tournamentNameEn");
            return (Criteria) this;
        }

        public Criteria andTournamentNameEnNotLike(String value) {
            addCriterion("tournament_name_en not like", value, "tournamentNameEn");
            return (Criteria) this;
        }

        public Criteria andTournamentNameEnIn(List<String> values) {
            addCriterion("tournament_name_en in", values, "tournamentNameEn");
            return (Criteria) this;
        }

        public Criteria andTournamentNameEnNotIn(List<String> values) {
            addCriterion("tournament_name_en not in", values, "tournamentNameEn");
            return (Criteria) this;
        }

        public Criteria andTournamentNameEnBetween(String value1, String value2) {
            addCriterion("tournament_name_en between", value1, value2, "tournamentNameEn");
            return (Criteria) this;
        }

        public Criteria andTournamentNameEnNotBetween(String value1, String value2) {
            addCriterion("tournament_name_en not between", value1, value2, "tournamentNameEn");
            return (Criteria) this;
        }

        public Criteria andT1NameCnIsNull() {
            addCriterion("t1_name_cn is null");
            return (Criteria) this;
        }

        public Criteria andT1NameCnIsNotNull() {
            addCriterion("t1_name_cn is not null");
            return (Criteria) this;
        }

        public Criteria andT1NameCnEqualTo(String value) {
            addCriterion("t1_name_cn =", value, "t1NameCn");
            return (Criteria) this;
        }

        public Criteria andT1NameCnNotEqualTo(String value) {
            addCriterion("t1_name_cn <>", value, "t1NameCn");
            return (Criteria) this;
        }

        public Criteria andT1NameCnGreaterThan(String value) {
            addCriterion("t1_name_cn >", value, "t1NameCn");
            return (Criteria) this;
        }

        public Criteria andT1NameCnGreaterThanOrEqualTo(String value) {
            addCriterion("t1_name_cn >=", value, "t1NameCn");
            return (Criteria) this;
        }

        public Criteria andT1NameCnLessThan(String value) {
            addCriterion("t1_name_cn <", value, "t1NameCn");
            return (Criteria) this;
        }

        public Criteria andT1NameCnLessThanOrEqualTo(String value) {
            addCriterion("t1_name_cn <=", value, "t1NameCn");
            return (Criteria) this;
        }

        public Criteria andT1NameCnLike(String value) {
            addCriterion("t1_name_cn like", value, "t1NameCn");
            return (Criteria) this;
        }

        public Criteria andT1NameCnNotLike(String value) {
            addCriterion("t1_name_cn not like", value, "t1NameCn");
            return (Criteria) this;
        }

        public Criteria andT1NameCnIn(List<String> values) {
            addCriterion("t1_name_cn in", values, "t1NameCn");
            return (Criteria) this;
        }

        public Criteria andT1NameCnNotIn(List<String> values) {
            addCriterion("t1_name_cn not in", values, "t1NameCn");
            return (Criteria) this;
        }

        public Criteria andT1NameCnBetween(String value1, String value2) {
            addCriterion("t1_name_cn between", value1, value2, "t1NameCn");
            return (Criteria) this;
        }

        public Criteria andT1NameCnNotBetween(String value1, String value2) {
            addCriterion("t1_name_cn not between", value1, value2, "t1NameCn");
            return (Criteria) this;
        }

        public Criteria andT1NameEnIsNull() {
            addCriterion("t1_name_en is null");
            return (Criteria) this;
        }

        public Criteria andT1NameEnIsNotNull() {
            addCriterion("t1_name_en is not null");
            return (Criteria) this;
        }

        public Criteria andT1NameEnEqualTo(String value) {
            addCriterion("t1_name_en =", value, "t1NameEn");
            return (Criteria) this;
        }

        public Criteria andT1NameEnNotEqualTo(String value) {
            addCriterion("t1_name_en <>", value, "t1NameEn");
            return (Criteria) this;
        }

        public Criteria andT1NameEnGreaterThan(String value) {
            addCriterion("t1_name_en >", value, "t1NameEn");
            return (Criteria) this;
        }

        public Criteria andT1NameEnGreaterThanOrEqualTo(String value) {
            addCriterion("t1_name_en >=", value, "t1NameEn");
            return (Criteria) this;
        }

        public Criteria andT1NameEnLessThan(String value) {
            addCriterion("t1_name_en <", value, "t1NameEn");
            return (Criteria) this;
        }

        public Criteria andT1NameEnLessThanOrEqualTo(String value) {
            addCriterion("t1_name_en <=", value, "t1NameEn");
            return (Criteria) this;
        }

        public Criteria andT1NameEnLike(String value) {
            addCriterion("t1_name_en like", value, "t1NameEn");
            return (Criteria) this;
        }

        public Criteria andT1NameEnNotLike(String value) {
            addCriterion("t1_name_en not like", value, "t1NameEn");
            return (Criteria) this;
        }

        public Criteria andT1NameEnIn(List<String> values) {
            addCriterion("t1_name_en in", values, "t1NameEn");
            return (Criteria) this;
        }

        public Criteria andT1NameEnNotIn(List<String> values) {
            addCriterion("t1_name_en not in", values, "t1NameEn");
            return (Criteria) this;
        }

        public Criteria andT1NameEnBetween(String value1, String value2) {
            addCriterion("t1_name_en between", value1, value2, "t1NameEn");
            return (Criteria) this;
        }

        public Criteria andT1NameEnNotBetween(String value1, String value2) {
            addCriterion("t1_name_en not between", value1, value2, "t1NameEn");
            return (Criteria) this;
        }

        public Criteria andT2NameCnIsNull() {
            addCriterion("t2_name_cn is null");
            return (Criteria) this;
        }

        public Criteria andT2NameCnIsNotNull() {
            addCriterion("t2_name_cn is not null");
            return (Criteria) this;
        }

        public Criteria andT2NameCnEqualTo(String value) {
            addCriterion("t2_name_cn =", value, "t2NameCn");
            return (Criteria) this;
        }

        public Criteria andT2NameCnNotEqualTo(String value) {
            addCriterion("t2_name_cn <>", value, "t2NameCn");
            return (Criteria) this;
        }

        public Criteria andT2NameCnGreaterThan(String value) {
            addCriterion("t2_name_cn >", value, "t2NameCn");
            return (Criteria) this;
        }

        public Criteria andT2NameCnGreaterThanOrEqualTo(String value) {
            addCriterion("t2_name_cn >=", value, "t2NameCn");
            return (Criteria) this;
        }

        public Criteria andT2NameCnLessThan(String value) {
            addCriterion("t2_name_cn <", value, "t2NameCn");
            return (Criteria) this;
        }

        public Criteria andT2NameCnLessThanOrEqualTo(String value) {
            addCriterion("t2_name_cn <=", value, "t2NameCn");
            return (Criteria) this;
        }

        public Criteria andT2NameCnLike(String value) {
            addCriterion("t2_name_cn like", value, "t2NameCn");
            return (Criteria) this;
        }

        public Criteria andT2NameCnNotLike(String value) {
            addCriterion("t2_name_cn not like", value, "t2NameCn");
            return (Criteria) this;
        }

        public Criteria andT2NameCnIn(List<String> values) {
            addCriterion("t2_name_cn in", values, "t2NameCn");
            return (Criteria) this;
        }

        public Criteria andT2NameCnNotIn(List<String> values) {
            addCriterion("t2_name_cn not in", values, "t2NameCn");
            return (Criteria) this;
        }

        public Criteria andT2NameCnBetween(String value1, String value2) {
            addCriterion("t2_name_cn between", value1, value2, "t2NameCn");
            return (Criteria) this;
        }

        public Criteria andT2NameCnNotBetween(String value1, String value2) {
            addCriterion("t2_name_cn not between", value1, value2, "t2NameCn");
            return (Criteria) this;
        }

        public Criteria andT2NameEnIsNull() {
            addCriterion("t2_name_en is null");
            return (Criteria) this;
        }

        public Criteria andT2NameEnIsNotNull() {
            addCriterion("t2_name_en is not null");
            return (Criteria) this;
        }

        public Criteria andT2NameEnEqualTo(String value) {
            addCriterion("t2_name_en =", value, "t2NameEn");
            return (Criteria) this;
        }

        public Criteria andT2NameEnNotEqualTo(String value) {
            addCriterion("t2_name_en <>", value, "t2NameEn");
            return (Criteria) this;
        }

        public Criteria andT2NameEnGreaterThan(String value) {
            addCriterion("t2_name_en >", value, "t2NameEn");
            return (Criteria) this;
        }

        public Criteria andT2NameEnGreaterThanOrEqualTo(String value) {
            addCriterion("t2_name_en >=", value, "t2NameEn");
            return (Criteria) this;
        }

        public Criteria andT2NameEnLessThan(String value) {
            addCriterion("t2_name_en <", value, "t2NameEn");
            return (Criteria) this;
        }

        public Criteria andT2NameEnLessThanOrEqualTo(String value) {
            addCriterion("t2_name_en <=", value, "t2NameEn");
            return (Criteria) this;
        }

        public Criteria andT2NameEnLike(String value) {
            addCriterion("t2_name_en like", value, "t2NameEn");
            return (Criteria) this;
        }

        public Criteria andT2NameEnNotLike(String value) {
            addCriterion("t2_name_en not like", value, "t2NameEn");
            return (Criteria) this;
        }

        public Criteria andT2NameEnIn(List<String> values) {
            addCriterion("t2_name_en in", values, "t2NameEn");
            return (Criteria) this;
        }

        public Criteria andT2NameEnNotIn(List<String> values) {
            addCriterion("t2_name_en not in", values, "t2NameEn");
            return (Criteria) this;
        }

        public Criteria andT2NameEnBetween(String value1, String value2) {
            addCriterion("t2_name_en between", value1, value2, "t2NameEn");
            return (Criteria) this;
        }

        public Criteria andT2NameEnNotBetween(String value1, String value2) {
            addCriterion("t2_name_en not between", value1, value2, "t2NameEn");
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

        public Criteria andBeginTimeEqualTo(Date value) {
            addCriterion("begin_time =", value, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeNotEqualTo(Date value) {
            addCriterion("begin_time <>", value, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeGreaterThan(Date value) {
            addCriterion("begin_time >", value, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("begin_time >=", value, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeLessThan(Date value) {
            addCriterion("begin_time <", value, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeLessThanOrEqualTo(Date value) {
            addCriterion("begin_time <=", value, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeIn(List<Date> values) {
            addCriterion("begin_time in", values, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeNotIn(List<Date> values) {
            addCriterion("begin_time not in", values, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeBetween(Date value1, Date value2) {
            addCriterion("begin_time between", value1, value2, "beginTime");
            return (Criteria) this;
        }

        public Criteria andBeginTimeNotBetween(Date value1, Date value2) {
            addCriterion("begin_time not between", value1, value2, "beginTime");
            return (Criteria) this;
        }

        public Criteria andScoreIsNull() {
            addCriterion("score is null");
            return (Criteria) this;
        }

        public Criteria andScoreIsNotNull() {
            addCriterion("score is not null");
            return (Criteria) this;
        }

        public Criteria andScoreEqualTo(String value) {
            addCriterion("score =", value, "score");
            return (Criteria) this;
        }

        public Criteria andScoreNotEqualTo(String value) {
            addCriterion("score <>", value, "score");
            return (Criteria) this;
        }

        public Criteria andScoreGreaterThan(String value) {
            addCriterion("score >", value, "score");
            return (Criteria) this;
        }

        public Criteria andScoreGreaterThanOrEqualTo(String value) {
            addCriterion("score >=", value, "score");
            return (Criteria) this;
        }

        public Criteria andScoreLessThan(String value) {
            addCriterion("score <", value, "score");
            return (Criteria) this;
        }

        public Criteria andScoreLessThanOrEqualTo(String value) {
            addCriterion("score <=", value, "score");
            return (Criteria) this;
        }

        public Criteria andScoreLike(String value) {
            addCriterion("score like", value, "score");
            return (Criteria) this;
        }

        public Criteria andScoreNotLike(String value) {
            addCriterion("score not like", value, "score");
            return (Criteria) this;
        }

        public Criteria andScoreIn(List<String> values) {
            addCriterion("score in", values, "score");
            return (Criteria) this;
        }

        public Criteria andScoreNotIn(List<String> values) {
            addCriterion("score not in", values, "score");
            return (Criteria) this;
        }

        public Criteria andScoreBetween(String value1, String value2) {
            addCriterion("score between", value1, value2, "score");
            return (Criteria) this;
        }

        public Criteria andScoreNotBetween(String value1, String value2) {
            addCriterion("score not between", value1, value2, "score");
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

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
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

        public Criteria andModifyTimeEqualTo(Date value) {
            addCriterion("modify_time =", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeNotEqualTo(Date value) {
            addCriterion("modify_time <>", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeGreaterThan(Date value) {
            addCriterion("modify_time >", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("modify_time >=", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeLessThan(Date value) {
            addCriterion("modify_time <", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeLessThanOrEqualTo(Date value) {
            addCriterion("modify_time <=", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeIn(List<Date> values) {
            addCriterion("modify_time in", values, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeNotIn(List<Date> values) {
            addCriterion("modify_time not in", values, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeBetween(Date value1, Date value2) {
            addCriterion("modify_time between", value1, value2, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeNotBetween(Date value1, Date value2) {
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