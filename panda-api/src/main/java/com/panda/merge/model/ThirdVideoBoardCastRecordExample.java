package com.panda.merge.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ThirdVideoBoardCastRecordExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ThirdVideoBoardCastRecordExample() {
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

        public Criteria andMatchIdIsNull() {
            addCriterion("match_id is null");
            return (Criteria) this;
        }

        public Criteria andMatchIdIsNotNull() {
            addCriterion("match_id is not null");
            return (Criteria) this;
        }

        public Criteria andMatchIdEqualTo(String value) {
            addCriterion("match_id =", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdNotEqualTo(String value) {
            addCriterion("match_id <>", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdGreaterThan(String value) {
            addCriterion("match_id >", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdGreaterThanOrEqualTo(String value) {
            addCriterion("match_id >=", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdLessThan(String value) {
            addCriterion("match_id <", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdLessThanOrEqualTo(String value) {
            addCriterion("match_id <=", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdLike(String value) {
            addCriterion("match_id like", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdNotLike(String value) {
            addCriterion("match_id not like", value, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdIn(List<String> values) {
            addCriterion("match_id in", values, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdNotIn(List<String> values) {
            addCriterion("match_id not in", values, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdBetween(String value1, String value2) {
            addCriterion("match_id between", value1, value2, "matchId");
            return (Criteria) this;
        }

        public Criteria andMatchIdNotBetween(String value1, String value2) {
            addCriterion("match_id not between", value1, value2, "matchId");
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

        public Criteria andCateIsNull() {
            addCriterion("cate is null");
            return (Criteria) this;
        }

        public Criteria andCateIsNotNull() {
            addCriterion("cate is not null");
            return (Criteria) this;
        }

        public Criteria andCateEqualTo(String value) {
            addCriterion("cate =", value, "cate");
            return (Criteria) this;
        }

        public Criteria andCateNotEqualTo(String value) {
            addCriterion("cate <>", value, "cate");
            return (Criteria) this;
        }

        public Criteria andCateGreaterThan(String value) {
            addCriterion("cate >", value, "cate");
            return (Criteria) this;
        }

        public Criteria andCateGreaterThanOrEqualTo(String value) {
            addCriterion("cate >=", value, "cate");
            return (Criteria) this;
        }

        public Criteria andCateLessThan(String value) {
            addCriterion("cate <", value, "cate");
            return (Criteria) this;
        }

        public Criteria andCateLessThanOrEqualTo(String value) {
            addCriterion("cate <=", value, "cate");
            return (Criteria) this;
        }

        public Criteria andCateLike(String value) {
            addCriterion("cate like", value, "cate");
            return (Criteria) this;
        }

        public Criteria andCateNotLike(String value) {
            addCriterion("cate not like", value, "cate");
            return (Criteria) this;
        }

        public Criteria andCateIn(List<String> values) {
            addCriterion("cate in", values, "cate");
            return (Criteria) this;
        }

        public Criteria andCateNotIn(List<String> values) {
            addCriterion("cate not in", values, "cate");
            return (Criteria) this;
        }

        public Criteria andCateBetween(String value1, String value2) {
            addCriterion("cate between", value1, value2, "cate");
            return (Criteria) this;
        }

        public Criteria andCateNotBetween(String value1, String value2) {
            addCriterion("cate not between", value1, value2, "cate");
            return (Criteria) this;
        }

        public Criteria andLeagueIsNull() {
            addCriterion("league is null");
            return (Criteria) this;
        }

        public Criteria andLeagueIsNotNull() {
            addCriterion("league is not null");
            return (Criteria) this;
        }

        public Criteria andLeagueEqualTo(String value) {
            addCriterion("league =", value, "league");
            return (Criteria) this;
        }

        public Criteria andLeagueNotEqualTo(String value) {
            addCriterion("league <>", value, "league");
            return (Criteria) this;
        }

        public Criteria andLeagueGreaterThan(String value) {
            addCriterion("league >", value, "league");
            return (Criteria) this;
        }

        public Criteria andLeagueGreaterThanOrEqualTo(String value) {
            addCriterion("league >=", value, "league");
            return (Criteria) this;
        }

        public Criteria andLeagueLessThan(String value) {
            addCriterion("league <", value, "league");
            return (Criteria) this;
        }

        public Criteria andLeagueLessThanOrEqualTo(String value) {
            addCriterion("league <=", value, "league");
            return (Criteria) this;
        }

        public Criteria andLeagueLike(String value) {
            addCriterion("league like", value, "league");
            return (Criteria) this;
        }

        public Criteria andLeagueNotLike(String value) {
            addCriterion("league not like", value, "league");
            return (Criteria) this;
        }

        public Criteria andLeagueIn(List<String> values) {
            addCriterion("league in", values, "league");
            return (Criteria) this;
        }

        public Criteria andLeagueNotIn(List<String> values) {
            addCriterion("league not in", values, "league");
            return (Criteria) this;
        }

        public Criteria andLeagueBetween(String value1, String value2) {
            addCriterion("league between", value1, value2, "league");
            return (Criteria) this;
        }

        public Criteria andLeagueNotBetween(String value1, String value2) {
            addCriterion("league not between", value1, value2, "league");
            return (Criteria) this;
        }

        public Criteria andStartDateIsNull() {
            addCriterion("start_date is null");
            return (Criteria) this;
        }

        public Criteria andStartDateIsNotNull() {
            addCriterion("start_date is not null");
            return (Criteria) this;
        }

        public Criteria andStartDateEqualTo(Date value) {
            addCriterion("start_date =", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateNotEqualTo(Date value) {
            addCriterion("start_date <>", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateGreaterThan(Date value) {
            addCriterion("start_date >", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateGreaterThanOrEqualTo(Date value) {
            addCriterion("start_date >=", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateLessThan(Date value) {
            addCriterion("start_date <", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateLessThanOrEqualTo(Date value) {
            addCriterion("start_date <=", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateIn(List<Date> values) {
            addCriterion("start_date in", values, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateNotIn(List<Date> values) {
            addCriterion("start_date not in", values, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateBetween(Date value1, Date value2) {
            addCriterion("start_date between", value1, value2, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateNotBetween(Date value1, Date value2) {
            addCriterion("start_date not between", value1, value2, "startDate");
            return (Criteria) this;
        }

        public Criteria andHomeZnIsNull() {
            addCriterion("home_zn is null");
            return (Criteria) this;
        }

        public Criteria andHomeZnIsNotNull() {
            addCriterion("home_zn is not null");
            return (Criteria) this;
        }

        public Criteria andHomeZnEqualTo(String value) {
            addCriterion("home_zn =", value, "homeZn");
            return (Criteria) this;
        }

        public Criteria andHomeZnNotEqualTo(String value) {
            addCriterion("home_zn <>", value, "homeZn");
            return (Criteria) this;
        }

        public Criteria andHomeZnGreaterThan(String value) {
            addCriterion("home_zn >", value, "homeZn");
            return (Criteria) this;
        }

        public Criteria andHomeZnGreaterThanOrEqualTo(String value) {
            addCriterion("home_zn >=", value, "homeZn");
            return (Criteria) this;
        }

        public Criteria andHomeZnLessThan(String value) {
            addCriterion("home_zn <", value, "homeZn");
            return (Criteria) this;
        }

        public Criteria andHomeZnLessThanOrEqualTo(String value) {
            addCriterion("home_zn <=", value, "homeZn");
            return (Criteria) this;
        }

        public Criteria andHomeZnLike(String value) {
            addCriterion("home_zn like", value, "homeZn");
            return (Criteria) this;
        }

        public Criteria andHomeZnNotLike(String value) {
            addCriterion("home_zn not like", value, "homeZn");
            return (Criteria) this;
        }

        public Criteria andHomeZnIn(List<String> values) {
            addCriterion("home_zn in", values, "homeZn");
            return (Criteria) this;
        }

        public Criteria andHomeZnNotIn(List<String> values) {
            addCriterion("home_zn not in", values, "homeZn");
            return (Criteria) this;
        }

        public Criteria andHomeZnBetween(String value1, String value2) {
            addCriterion("home_zn between", value1, value2, "homeZn");
            return (Criteria) this;
        }

        public Criteria andHomeZnNotBetween(String value1, String value2) {
            addCriterion("home_zn not between", value1, value2, "homeZn");
            return (Criteria) this;
        }

        public Criteria andAwayZnIsNull() {
            addCriterion("away_zn is null");
            return (Criteria) this;
        }

        public Criteria andAwayZnIsNotNull() {
            addCriterion("away_zn is not null");
            return (Criteria) this;
        }

        public Criteria andAwayZnEqualTo(String value) {
            addCriterion("away_zn =", value, "awayZn");
            return (Criteria) this;
        }

        public Criteria andAwayZnNotEqualTo(String value) {
            addCriterion("away_zn <>", value, "awayZn");
            return (Criteria) this;
        }

        public Criteria andAwayZnGreaterThan(String value) {
            addCriterion("away_zn >", value, "awayZn");
            return (Criteria) this;
        }

        public Criteria andAwayZnGreaterThanOrEqualTo(String value) {
            addCriterion("away_zn >=", value, "awayZn");
            return (Criteria) this;
        }

        public Criteria andAwayZnLessThan(String value) {
            addCriterion("away_zn <", value, "awayZn");
            return (Criteria) this;
        }

        public Criteria andAwayZnLessThanOrEqualTo(String value) {
            addCriterion("away_zn <=", value, "awayZn");
            return (Criteria) this;
        }

        public Criteria andAwayZnLike(String value) {
            addCriterion("away_zn like", value, "awayZn");
            return (Criteria) this;
        }

        public Criteria andAwayZnNotLike(String value) {
            addCriterion("away_zn not like", value, "awayZn");
            return (Criteria) this;
        }

        public Criteria andAwayZnIn(List<String> values) {
            addCriterion("away_zn in", values, "awayZn");
            return (Criteria) this;
        }

        public Criteria andAwayZnNotIn(List<String> values) {
            addCriterion("away_zn not in", values, "awayZn");
            return (Criteria) this;
        }

        public Criteria andAwayZnBetween(String value1, String value2) {
            addCriterion("away_zn between", value1, value2, "awayZn");
            return (Criteria) this;
        }

        public Criteria andAwayZnNotBetween(String value1, String value2) {
            addCriterion("away_zn not between", value1, value2, "awayZn");
            return (Criteria) this;
        }

        public Criteria andHomeEnIsNull() {
            addCriterion("home_en is null");
            return (Criteria) this;
        }

        public Criteria andHomeEnIsNotNull() {
            addCriterion("home_en is not null");
            return (Criteria) this;
        }

        public Criteria andHomeEnEqualTo(String value) {
            addCriterion("home_en =", value, "homeEn");
            return (Criteria) this;
        }

        public Criteria andHomeEnNotEqualTo(String value) {
            addCriterion("home_en <>", value, "homeEn");
            return (Criteria) this;
        }

        public Criteria andHomeEnGreaterThan(String value) {
            addCriterion("home_en >", value, "homeEn");
            return (Criteria) this;
        }

        public Criteria andHomeEnGreaterThanOrEqualTo(String value) {
            addCriterion("home_en >=", value, "homeEn");
            return (Criteria) this;
        }

        public Criteria andHomeEnLessThan(String value) {
            addCriterion("home_en <", value, "homeEn");
            return (Criteria) this;
        }

        public Criteria andHomeEnLessThanOrEqualTo(String value) {
            addCriterion("home_en <=", value, "homeEn");
            return (Criteria) this;
        }

        public Criteria andHomeEnLike(String value) {
            addCriterion("home_en like", value, "homeEn");
            return (Criteria) this;
        }

        public Criteria andHomeEnNotLike(String value) {
            addCriterion("home_en not like", value, "homeEn");
            return (Criteria) this;
        }

        public Criteria andHomeEnIn(List<String> values) {
            addCriterion("home_en in", values, "homeEn");
            return (Criteria) this;
        }

        public Criteria andHomeEnNotIn(List<String> values) {
            addCriterion("home_en not in", values, "homeEn");
            return (Criteria) this;
        }

        public Criteria andHomeEnBetween(String value1, String value2) {
            addCriterion("home_en between", value1, value2, "homeEn");
            return (Criteria) this;
        }

        public Criteria andHomeEnNotBetween(String value1, String value2) {
            addCriterion("home_en not between", value1, value2, "homeEn");
            return (Criteria) this;
        }

        public Criteria andAwayEnIsNull() {
            addCriterion("away_en is null");
            return (Criteria) this;
        }

        public Criteria andAwayEnIsNotNull() {
            addCriterion("away_en is not null");
            return (Criteria) this;
        }

        public Criteria andAwayEnEqualTo(String value) {
            addCriterion("away_en =", value, "awayEn");
            return (Criteria) this;
        }

        public Criteria andAwayEnNotEqualTo(String value) {
            addCriterion("away_en <>", value, "awayEn");
            return (Criteria) this;
        }

        public Criteria andAwayEnGreaterThan(String value) {
            addCriterion("away_en >", value, "awayEn");
            return (Criteria) this;
        }

        public Criteria andAwayEnGreaterThanOrEqualTo(String value) {
            addCriterion("away_en >=", value, "awayEn");
            return (Criteria) this;
        }

        public Criteria andAwayEnLessThan(String value) {
            addCriterion("away_en <", value, "awayEn");
            return (Criteria) this;
        }

        public Criteria andAwayEnLessThanOrEqualTo(String value) {
            addCriterion("away_en <=", value, "awayEn");
            return (Criteria) this;
        }

        public Criteria andAwayEnLike(String value) {
            addCriterion("away_en like", value, "awayEn");
            return (Criteria) this;
        }

        public Criteria andAwayEnNotLike(String value) {
            addCriterion("away_en not like", value, "awayEn");
            return (Criteria) this;
        }

        public Criteria andAwayEnIn(List<String> values) {
            addCriterion("away_en in", values, "awayEn");
            return (Criteria) this;
        }

        public Criteria andAwayEnNotIn(List<String> values) {
            addCriterion("away_en not in", values, "awayEn");
            return (Criteria) this;
        }

        public Criteria andAwayEnBetween(String value1, String value2) {
            addCriterion("away_en between", value1, value2, "awayEn");
            return (Criteria) this;
        }

        public Criteria andAwayEnNotBetween(String value1, String value2) {
            addCriterion("away_en not between", value1, value2, "awayEn");
            return (Criteria) this;
        }

        public Criteria andLiveVideoPathStatusIsNull() {
            addCriterion("live_video_path_status is null");
            return (Criteria) this;
        }

        public Criteria andLiveVideoPathStatusIsNotNull() {
            addCriterion("live_video_path_status is not null");
            return (Criteria) this;
        }

        public Criteria andLiveVideoPathStatusEqualTo(Long value) {
            addCriterion("live_video_path_status =", value, "liveVideoPathStatus");
            return (Criteria) this;
        }

        public Criteria andLiveVideoPathStatusNotEqualTo(Long value) {
            addCriterion("live_video_path_status <>", value, "liveVideoPathStatus");
            return (Criteria) this;
        }

        public Criteria andLiveVideoPathStatusGreaterThan(Long value) {
            addCriterion("live_video_path_status >", value, "liveVideoPathStatus");
            return (Criteria) this;
        }

        public Criteria andLiveVideoPathStatusGreaterThanOrEqualTo(Long value) {
            addCriterion("live_video_path_status >=", value, "liveVideoPathStatus");
            return (Criteria) this;
        }

        public Criteria andLiveVideoPathStatusLessThan(Long value) {
            addCriterion("live_video_path_status <", value, "liveVideoPathStatus");
            return (Criteria) this;
        }

        public Criteria andLiveVideoPathStatusLessThanOrEqualTo(Long value) {
            addCriterion("live_video_path_status <=", value, "liveVideoPathStatus");
            return (Criteria) this;
        }

        public Criteria andLiveVideoPathStatusIn(List<Long> values) {
            addCriterion("live_video_path_status in", values, "liveVideoPathStatus");
            return (Criteria) this;
        }

        public Criteria andLiveVideoPathStatusNotIn(List<Long> values) {
            addCriterion("live_video_path_status not in", values, "liveVideoPathStatus");
            return (Criteria) this;
        }

        public Criteria andLiveVideoPathStatusBetween(Long value1, Long value2) {
            addCriterion("live_video_path_status between", value1, value2, "liveVideoPathStatus");
            return (Criteria) this;
        }

        public Criteria andLiveVideoPathStatusNotBetween(Long value1, Long value2) {
            addCriterion("live_video_path_status not between", value1, value2, "liveVideoPathStatus");
            return (Criteria) this;
        }

        public Criteria andLiveVideoOnlineIsNull() {
            addCriterion("live_video_online is null");
            return (Criteria) this;
        }

        public Criteria andLiveVideoOnlineIsNotNull() {
            addCriterion("live_video_online is not null");
            return (Criteria) this;
        }

        public Criteria andLiveVideoOnlineEqualTo(Long value) {
            addCriterion("live_video_online =", value, "liveVideoOnline");
            return (Criteria) this;
        }

        public Criteria andLiveVideoOnlineNotEqualTo(Long value) {
            addCriterion("live_video_online <>", value, "liveVideoOnline");
            return (Criteria) this;
        }

        public Criteria andLiveVideoOnlineGreaterThan(Long value) {
            addCriterion("live_video_online >", value, "liveVideoOnline");
            return (Criteria) this;
        }

        public Criteria andLiveVideoOnlineGreaterThanOrEqualTo(Long value) {
            addCriterion("live_video_online >=", value, "liveVideoOnline");
            return (Criteria) this;
        }

        public Criteria andLiveVideoOnlineLessThan(Long value) {
            addCriterion("live_video_online <", value, "liveVideoOnline");
            return (Criteria) this;
        }

        public Criteria andLiveVideoOnlineLessThanOrEqualTo(Long value) {
            addCriterion("live_video_online <=", value, "liveVideoOnline");
            return (Criteria) this;
        }

        public Criteria andLiveVideoOnlineIn(List<Long> values) {
            addCriterion("live_video_online in", values, "liveVideoOnline");
            return (Criteria) this;
        }

        public Criteria andLiveVideoOnlineNotIn(List<Long> values) {
            addCriterion("live_video_online not in", values, "liveVideoOnline");
            return (Criteria) this;
        }

        public Criteria andLiveVideoOnlineBetween(Long value1, Long value2) {
            addCriterion("live_video_online between", value1, value2, "liveVideoOnline");
            return (Criteria) this;
        }

        public Criteria andLiveVideoOnlineNotBetween(Long value1, Long value2) {
            addCriterion("live_video_online not between", value1, value2, "liveVideoOnline");
            return (Criteria) this;
        }

        public Criteria andLiveVideoHdIsNull() {
            addCriterion("live_video_hd is null");
            return (Criteria) this;
        }

        public Criteria andLiveVideoHdIsNotNull() {
            addCriterion("live_video_hd is not null");
            return (Criteria) this;
        }

        public Criteria andLiveVideoHdEqualTo(String value) {
            addCriterion("live_video_hd =", value, "liveVideoHd");
            return (Criteria) this;
        }

        public Criteria andLiveVideoHdNotEqualTo(String value) {
            addCriterion("live_video_hd <>", value, "liveVideoHd");
            return (Criteria) this;
        }

        public Criteria andLiveVideoHdGreaterThan(String value) {
            addCriterion("live_video_hd >", value, "liveVideoHd");
            return (Criteria) this;
        }

        public Criteria andLiveVideoHdGreaterThanOrEqualTo(String value) {
            addCriterion("live_video_hd >=", value, "liveVideoHd");
            return (Criteria) this;
        }

        public Criteria andLiveVideoHdLessThan(String value) {
            addCriterion("live_video_hd <", value, "liveVideoHd");
            return (Criteria) this;
        }

        public Criteria andLiveVideoHdLessThanOrEqualTo(String value) {
            addCriterion("live_video_hd <=", value, "liveVideoHd");
            return (Criteria) this;
        }

        public Criteria andLiveVideoHdLike(String value) {
            addCriterion("live_video_hd like", value, "liveVideoHd");
            return (Criteria) this;
        }

        public Criteria andLiveVideoHdNotLike(String value) {
            addCriterion("live_video_hd not like", value, "liveVideoHd");
            return (Criteria) this;
        }

        public Criteria andLiveVideoHdIn(List<String> values) {
            addCriterion("live_video_hd in", values, "liveVideoHd");
            return (Criteria) this;
        }

        public Criteria andLiveVideoHdNotIn(List<String> values) {
            addCriterion("live_video_hd not in", values, "liveVideoHd");
            return (Criteria) this;
        }

        public Criteria andLiveVideoHdBetween(String value1, String value2) {
            addCriterion("live_video_hd between", value1, value2, "liveVideoHd");
            return (Criteria) this;
        }

        public Criteria andLiveVideoHdNotBetween(String value1, String value2) {
            addCriterion("live_video_hd not between", value1, value2, "liveVideoHd");
            return (Criteria) this;
        }

        public Criteria andAniIdIsNull() {
            addCriterion("ani_id is null");
            return (Criteria) this;
        }

        public Criteria andAniIdIsNotNull() {
            addCriterion("ani_id is not null");
            return (Criteria) this;
        }

        public Criteria andAniIdEqualTo(String value) {
            addCriterion("ani_id =", value, "aniId");
            return (Criteria) this;
        }

        public Criteria andAniIdNotEqualTo(String value) {
            addCriterion("ani_id <>", value, "aniId");
            return (Criteria) this;
        }

        public Criteria andAniIdGreaterThan(String value) {
            addCriterion("ani_id >", value, "aniId");
            return (Criteria) this;
        }

        public Criteria andAniIdGreaterThanOrEqualTo(String value) {
            addCriterion("ani_id >=", value, "aniId");
            return (Criteria) this;
        }

        public Criteria andAniIdLessThan(String value) {
            addCriterion("ani_id <", value, "aniId");
            return (Criteria) this;
        }

        public Criteria andAniIdLessThanOrEqualTo(String value) {
            addCriterion("ani_id <=", value, "aniId");
            return (Criteria) this;
        }

        public Criteria andAniIdLike(String value) {
            addCriterion("ani_id like", value, "aniId");
            return (Criteria) this;
        }

        public Criteria andAniIdNotLike(String value) {
            addCriterion("ani_id not like", value, "aniId");
            return (Criteria) this;
        }

        public Criteria andAniIdIn(List<String> values) {
            addCriterion("ani_id in", values, "aniId");
            return (Criteria) this;
        }

        public Criteria andAniIdNotIn(List<String> values) {
            addCriterion("ani_id not in", values, "aniId");
            return (Criteria) this;
        }

        public Criteria andAniIdBetween(String value1, String value2) {
            addCriterion("ani_id between", value1, value2, "aniId");
            return (Criteria) this;
        }

        public Criteria andAniIdNotBetween(String value1, String value2) {
            addCriterion("ani_id not between", value1, value2, "aniId");
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

        public Criteria andPlayerUrlIsNull() {
            addCriterion("player_url is null");
            return (Criteria) this;
        }

        public Criteria andPlayerUrlIsNotNull() {
            addCriterion("player_url is not null");
            return (Criteria) this;
        }

        public Criteria andPlayerUrlEqualTo(String value) {
            addCriterion("player_url =", value, "playerUrl");
            return (Criteria) this;
        }

        public Criteria andPlayerUrlNotEqualTo(String value) {
            addCriterion("player_url <>", value, "playerUrl");
            return (Criteria) this;
        }

        public Criteria andPlayerUrlGreaterThan(String value) {
            addCriterion("player_url >", value, "playerUrl");
            return (Criteria) this;
        }

        public Criteria andPlayerUrlGreaterThanOrEqualTo(String value) {
            addCriterion("player_url >=", value, "playerUrl");
            return (Criteria) this;
        }

        public Criteria andPlayerUrlLessThan(String value) {
            addCriterion("player_url <", value, "playerUrl");
            return (Criteria) this;
        }

        public Criteria andPlayerUrlLessThanOrEqualTo(String value) {
            addCriterion("player_url <=", value, "playerUrl");
            return (Criteria) this;
        }

        public Criteria andPlayerUrlLike(String value) {
            addCriterion("player_url like", value, "playerUrl");
            return (Criteria) this;
        }

        public Criteria andPlayerUrlNotLike(String value) {
            addCriterion("player_url not like", value, "playerUrl");
            return (Criteria) this;
        }

        public Criteria andPlayerUrlIn(List<String> values) {
            addCriterion("player_url in", values, "playerUrl");
            return (Criteria) this;
        }

        public Criteria andPlayerUrlNotIn(List<String> values) {
            addCriterion("player_url not in", values, "playerUrl");
            return (Criteria) this;
        }

        public Criteria andPlayerUrlBetween(String value1, String value2) {
            addCriterion("player_url between", value1, value2, "playerUrl");
            return (Criteria) this;
        }

        public Criteria andPlayerUrlNotBetween(String value1, String value2) {
            addCriterion("player_url not between", value1, value2, "playerUrl");
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