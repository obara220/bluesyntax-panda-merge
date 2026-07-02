package com.panda.merge.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ThirdSportPlayerRankingExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ThirdSportPlayerRankingExample() {
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

        public Criteria andThirdSourceSeasonIdIsNull() {
            addCriterion("third_source_season_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonIdIsNotNull() {
            addCriterion("third_source_season_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonIdEqualTo(String value) {
            addCriterion("third_source_season_id =", value, "thirdSourceSeasonId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonIdNotEqualTo(String value) {
            addCriterion("third_source_season_id <>", value, "thirdSourceSeasonId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonIdGreaterThan(String value) {
            addCriterion("third_source_season_id >", value, "thirdSourceSeasonId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonIdGreaterThanOrEqualTo(String value) {
            addCriterion("third_source_season_id >=", value, "thirdSourceSeasonId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonIdLessThan(String value) {
            addCriterion("third_source_season_id <", value, "thirdSourceSeasonId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonIdLessThanOrEqualTo(String value) {
            addCriterion("third_source_season_id <=", value, "thirdSourceSeasonId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonIdLike(String value) {
            addCriterion("third_source_season_id like", value, "thirdSourceSeasonId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonIdNotLike(String value) {
            addCriterion("third_source_season_id not like", value, "thirdSourceSeasonId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonIdIn(List<String> values) {
            addCriterion("third_source_season_id in", values, "thirdSourceSeasonId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonIdNotIn(List<String> values) {
            addCriterion("third_source_season_id not in", values, "thirdSourceSeasonId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonIdBetween(String value1, String value2) {
            addCriterion("third_source_season_id between", value1, value2, "thirdSourceSeasonId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonIdNotBetween(String value1, String value2) {
            addCriterion("third_source_season_id not between", value1, value2, "thirdSourceSeasonId");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonNameIsNull() {
            addCriterion("third_source_season_name is null");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonNameIsNotNull() {
            addCriterion("third_source_season_name is not null");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonNameEqualTo(String value) {
            addCriterion("third_source_season_name =", value, "thirdSourceSeasonName");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonNameNotEqualTo(String value) {
            addCriterion("third_source_season_name <>", value, "thirdSourceSeasonName");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonNameGreaterThan(String value) {
            addCriterion("third_source_season_name >", value, "thirdSourceSeasonName");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonNameGreaterThanOrEqualTo(String value) {
            addCriterion("third_source_season_name >=", value, "thirdSourceSeasonName");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonNameLessThan(String value) {
            addCriterion("third_source_season_name <", value, "thirdSourceSeasonName");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonNameLessThanOrEqualTo(String value) {
            addCriterion("third_source_season_name <=", value, "thirdSourceSeasonName");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonNameLike(String value) {
            addCriterion("third_source_season_name like", value, "thirdSourceSeasonName");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonNameNotLike(String value) {
            addCriterion("third_source_season_name not like", value, "thirdSourceSeasonName");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonNameIn(List<String> values) {
            addCriterion("third_source_season_name in", values, "thirdSourceSeasonName");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonNameNotIn(List<String> values) {
            addCriterion("third_source_season_name not in", values, "thirdSourceSeasonName");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonNameBetween(String value1, String value2) {
            addCriterion("third_source_season_name between", value1, value2, "thirdSourceSeasonName");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonNameNotBetween(String value1, String value2) {
            addCriterion("third_source_season_name not between", value1, value2, "thirdSourceSeasonName");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonBeginTimeIsNull() {
            addCriterion("third_source_season_begin_time is null");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonBeginTimeIsNotNull() {
            addCriterion("third_source_season_begin_time is not null");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonBeginTimeEqualTo(Date value) {
            addCriterion("third_source_season_begin_time =", value, "thirdSourceSeasonBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonBeginTimeNotEqualTo(Date value) {
            addCriterion("third_source_season_begin_time <>", value, "thirdSourceSeasonBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonBeginTimeGreaterThan(Date value) {
            addCriterion("third_source_season_begin_time >", value, "thirdSourceSeasonBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonBeginTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("third_source_season_begin_time >=", value, "thirdSourceSeasonBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonBeginTimeLessThan(Date value) {
            addCriterion("third_source_season_begin_time <", value, "thirdSourceSeasonBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonBeginTimeLessThanOrEqualTo(Date value) {
            addCriterion("third_source_season_begin_time <=", value, "thirdSourceSeasonBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonBeginTimeIn(List<Date> values) {
            addCriterion("third_source_season_begin_time in", values, "thirdSourceSeasonBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonBeginTimeNotIn(List<Date> values) {
            addCriterion("third_source_season_begin_time not in", values, "thirdSourceSeasonBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonBeginTimeBetween(Date value1, Date value2) {
            addCriterion("third_source_season_begin_time between", value1, value2, "thirdSourceSeasonBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonBeginTimeNotBetween(Date value1, Date value2) {
            addCriterion("third_source_season_begin_time not between", value1, value2, "thirdSourceSeasonBeginTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonEndTimeIsNull() {
            addCriterion("third_source_season_end_time is null");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonEndTimeIsNotNull() {
            addCriterion("third_source_season_end_time is not null");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonEndTimeEqualTo(Date value) {
            addCriterion("third_source_season_end_time =", value, "thirdSourceSeasonEndTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonEndTimeNotEqualTo(Date value) {
            addCriterion("third_source_season_end_time <>", value, "thirdSourceSeasonEndTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonEndTimeGreaterThan(Date value) {
            addCriterion("third_source_season_end_time >", value, "thirdSourceSeasonEndTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonEndTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("third_source_season_end_time >=", value, "thirdSourceSeasonEndTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonEndTimeLessThan(Date value) {
            addCriterion("third_source_season_end_time <", value, "thirdSourceSeasonEndTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonEndTimeLessThanOrEqualTo(Date value) {
            addCriterion("third_source_season_end_time <=", value, "thirdSourceSeasonEndTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonEndTimeIn(List<Date> values) {
            addCriterion("third_source_season_end_time in", values, "thirdSourceSeasonEndTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonEndTimeNotIn(List<Date> values) {
            addCriterion("third_source_season_end_time not in", values, "thirdSourceSeasonEndTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonEndTimeBetween(Date value1, Date value2) {
            addCriterion("third_source_season_end_time between", value1, value2, "thirdSourceSeasonEndTime");
            return (Criteria) this;
        }

        public Criteria andThirdSourceSeasonEndTimeNotBetween(Date value1, Date value2) {
            addCriterion("third_source_season_end_time not between", value1, value2, "thirdSourceSeasonEndTime");
            return (Criteria) this;
        }

        public Criteria andMatchCountIsNull() {
            addCriterion("match_count is null");
            return (Criteria) this;
        }

        public Criteria andMatchCountIsNotNull() {
            addCriterion("match_count is not null");
            return (Criteria) this;
        }

        public Criteria andMatchCountEqualTo(Integer value) {
            addCriterion("match_count =", value, "matchCount");
            return (Criteria) this;
        }

        public Criteria andMatchCountNotEqualTo(Integer value) {
            addCriterion("match_count <>", value, "matchCount");
            return (Criteria) this;
        }

        public Criteria andMatchCountGreaterThan(Integer value) {
            addCriterion("match_count >", value, "matchCount");
            return (Criteria) this;
        }

        public Criteria andMatchCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("match_count >=", value, "matchCount");
            return (Criteria) this;
        }

        public Criteria andMatchCountLessThan(Integer value) {
            addCriterion("match_count <", value, "matchCount");
            return (Criteria) this;
        }

        public Criteria andMatchCountLessThanOrEqualTo(Integer value) {
            addCriterion("match_count <=", value, "matchCount");
            return (Criteria) this;
        }

        public Criteria andMatchCountIn(List<Integer> values) {
            addCriterion("match_count in", values, "matchCount");
            return (Criteria) this;
        }

        public Criteria andMatchCountNotIn(List<Integer> values) {
            addCriterion("match_count not in", values, "matchCount");
            return (Criteria) this;
        }

        public Criteria andMatchCountBetween(Integer value1, Integer value2) {
            addCriterion("match_count between", value1, value2, "matchCount");
            return (Criteria) this;
        }

        public Criteria andMatchCountNotBetween(Integer value1, Integer value2) {
            addCriterion("match_count not between", value1, value2, "matchCount");
            return (Criteria) this;
        }

        public Criteria andRankingValueIsNull() {
            addCriterion("ranking_value is null");
            return (Criteria) this;
        }

        public Criteria andRankingValueIsNotNull() {
            addCriterion("ranking_value is not null");
            return (Criteria) this;
        }

        public Criteria andRankingValueEqualTo(Integer value) {
            addCriterion("ranking_value =", value, "rankingValue");
            return (Criteria) this;
        }

        public Criteria andRankingValueNotEqualTo(Integer value) {
            addCriterion("ranking_value <>", value, "rankingValue");
            return (Criteria) this;
        }

        public Criteria andRankingValueGreaterThan(Integer value) {
            addCriterion("ranking_value >", value, "rankingValue");
            return (Criteria) this;
        }

        public Criteria andRankingValueGreaterThanOrEqualTo(Integer value) {
            addCriterion("ranking_value >=", value, "rankingValue");
            return (Criteria) this;
        }

        public Criteria andRankingValueLessThan(Integer value) {
            addCriterion("ranking_value <", value, "rankingValue");
            return (Criteria) this;
        }

        public Criteria andRankingValueLessThanOrEqualTo(Integer value) {
            addCriterion("ranking_value <=", value, "rankingValue");
            return (Criteria) this;
        }

        public Criteria andRankingValueIn(List<Integer> values) {
            addCriterion("ranking_value in", values, "rankingValue");
            return (Criteria) this;
        }

        public Criteria andRankingValueNotIn(List<Integer> values) {
            addCriterion("ranking_value not in", values, "rankingValue");
            return (Criteria) this;
        }

        public Criteria andRankingValueBetween(Integer value1, Integer value2) {
            addCriterion("ranking_value between", value1, value2, "rankingValue");
            return (Criteria) this;
        }

        public Criteria andRankingValueNotBetween(Integer value1, Integer value2) {
            addCriterion("ranking_value not between", value1, value2, "rankingValue");
            return (Criteria) this;
        }

        public Criteria andRankingSortIsNull() {
            addCriterion("ranking_sort is null");
            return (Criteria) this;
        }

        public Criteria andRankingSortIsNotNull() {
            addCriterion("ranking_sort is not null");
            return (Criteria) this;
        }

        public Criteria andRankingSortEqualTo(Integer value) {
            addCriterion("ranking_sort =", value, "rankingSort");
            return (Criteria) this;
        }

        public Criteria andRankingSortNotEqualTo(Integer value) {
            addCriterion("ranking_sort <>", value, "rankingSort");
            return (Criteria) this;
        }

        public Criteria andRankingSortGreaterThan(Integer value) {
            addCriterion("ranking_sort >", value, "rankingSort");
            return (Criteria) this;
        }

        public Criteria andRankingSortGreaterThanOrEqualTo(Integer value) {
            addCriterion("ranking_sort >=", value, "rankingSort");
            return (Criteria) this;
        }

        public Criteria andRankingSortLessThan(Integer value) {
            addCriterion("ranking_sort <", value, "rankingSort");
            return (Criteria) this;
        }

        public Criteria andRankingSortLessThanOrEqualTo(Integer value) {
            addCriterion("ranking_sort <=", value, "rankingSort");
            return (Criteria) this;
        }

        public Criteria andRankingSortIn(List<Integer> values) {
            addCriterion("ranking_sort in", values, "rankingSort");
            return (Criteria) this;
        }

        public Criteria andRankingSortNotIn(List<Integer> values) {
            addCriterion("ranking_sort not in", values, "rankingSort");
            return (Criteria) this;
        }

        public Criteria andRankingSortBetween(Integer value1, Integer value2) {
            addCriterion("ranking_sort between", value1, value2, "rankingSort");
            return (Criteria) this;
        }

        public Criteria andRankingSortNotBetween(Integer value1, Integer value2) {
            addCriterion("ranking_sort not between", value1, value2, "rankingSort");
            return (Criteria) this;
        }

        public Criteria andRankingTypeIsNull() {
            addCriterion("ranking_type is null");
            return (Criteria) this;
        }

        public Criteria andRankingTypeIsNotNull() {
            addCriterion("ranking_type is not null");
            return (Criteria) this;
        }

        public Criteria andRankingTypeEqualTo(Integer value) {
            addCriterion("ranking_type =", value, "rankingType");
            return (Criteria) this;
        }

        public Criteria andRankingTypeNotEqualTo(Integer value) {
            addCriterion("ranking_type <>", value, "rankingType");
            return (Criteria) this;
        }

        public Criteria andRankingTypeGreaterThan(Integer value) {
            addCriterion("ranking_type >", value, "rankingType");
            return (Criteria) this;
        }

        public Criteria andRankingTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("ranking_type >=", value, "rankingType");
            return (Criteria) this;
        }

        public Criteria andRankingTypeLessThan(Integer value) {
            addCriterion("ranking_type <", value, "rankingType");
            return (Criteria) this;
        }

        public Criteria andRankingTypeLessThanOrEqualTo(Integer value) {
            addCriterion("ranking_type <=", value, "rankingType");
            return (Criteria) this;
        }

        public Criteria andRankingTypeIn(List<Integer> values) {
            addCriterion("ranking_type in", values, "rankingType");
            return (Criteria) this;
        }

        public Criteria andRankingTypeNotIn(List<Integer> values) {
            addCriterion("ranking_type not in", values, "rankingType");
            return (Criteria) this;
        }

        public Criteria andRankingTypeBetween(Integer value1, Integer value2) {
            addCriterion("ranking_type between", value1, value2, "rankingType");
            return (Criteria) this;
        }

        public Criteria andRankingTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("ranking_type not between", value1, value2, "rankingType");
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

        public Criteria andTeamLogoIsNull() {
            addCriterion("team_logo is null");
            return (Criteria) this;
        }

        public Criteria andTeamLogoIsNotNull() {
            addCriterion("team_logo is not null");
            return (Criteria) this;
        }

        public Criteria andTeamLogoEqualTo(String value) {
            addCriterion("team_logo =", value, "teamLogo");
            return (Criteria) this;
        }

        public Criteria andTeamLogoNotEqualTo(String value) {
            addCriterion("team_logo <>", value, "teamLogo");
            return (Criteria) this;
        }

        public Criteria andTeamLogoGreaterThan(String value) {
            addCriterion("team_logo >", value, "teamLogo");
            return (Criteria) this;
        }

        public Criteria andTeamLogoGreaterThanOrEqualTo(String value) {
            addCriterion("team_logo >=", value, "teamLogo");
            return (Criteria) this;
        }

        public Criteria andTeamLogoLessThan(String value) {
            addCriterion("team_logo <", value, "teamLogo");
            return (Criteria) this;
        }

        public Criteria andTeamLogoLessThanOrEqualTo(String value) {
            addCriterion("team_logo <=", value, "teamLogo");
            return (Criteria) this;
        }

        public Criteria andTeamLogoLike(String value) {
            addCriterion("team_logo like", value, "teamLogo");
            return (Criteria) this;
        }

        public Criteria andTeamLogoNotLike(String value) {
            addCriterion("team_logo not like", value, "teamLogo");
            return (Criteria) this;
        }

        public Criteria andTeamLogoIn(List<String> values) {
            addCriterion("team_logo in", values, "teamLogo");
            return (Criteria) this;
        }

        public Criteria andTeamLogoNotIn(List<String> values) {
            addCriterion("team_logo not in", values, "teamLogo");
            return (Criteria) this;
        }

        public Criteria andTeamLogoBetween(String value1, String value2) {
            addCriterion("team_logo between", value1, value2, "teamLogo");
            return (Criteria) this;
        }

        public Criteria andTeamLogoNotBetween(String value1, String value2) {
            addCriterion("team_logo not between", value1, value2, "teamLogo");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerSourceIdIsNull() {
            addCriterion("third_player_source_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerSourceIdIsNotNull() {
            addCriterion("third_player_source_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerSourceIdEqualTo(String value) {
            addCriterion("third_player_source_id =", value, "thirdPlayerSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerSourceIdNotEqualTo(String value) {
            addCriterion("third_player_source_id <>", value, "thirdPlayerSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerSourceIdGreaterThan(String value) {
            addCriterion("third_player_source_id >", value, "thirdPlayerSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerSourceIdGreaterThanOrEqualTo(String value) {
            addCriterion("third_player_source_id >=", value, "thirdPlayerSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerSourceIdLessThan(String value) {
            addCriterion("third_player_source_id <", value, "thirdPlayerSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerSourceIdLessThanOrEqualTo(String value) {
            addCriterion("third_player_source_id <=", value, "thirdPlayerSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerSourceIdLike(String value) {
            addCriterion("third_player_source_id like", value, "thirdPlayerSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerSourceIdNotLike(String value) {
            addCriterion("third_player_source_id not like", value, "thirdPlayerSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerSourceIdIn(List<String> values) {
            addCriterion("third_player_source_id in", values, "thirdPlayerSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerSourceIdNotIn(List<String> values) {
            addCriterion("third_player_source_id not in", values, "thirdPlayerSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerSourceIdBetween(String value1, String value2) {
            addCriterion("third_player_source_id between", value1, value2, "thirdPlayerSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerSourceIdNotBetween(String value1, String value2) {
            addCriterion("third_player_source_id not between", value1, value2, "thirdPlayerSourceId");
            return (Criteria) this;
        }

        public Criteria andPlayerCnNameIsNull() {
            addCriterion("player_cn_name is null");
            return (Criteria) this;
        }

        public Criteria andPlayerCnNameIsNotNull() {
            addCriterion("player_cn_name is not null");
            return (Criteria) this;
        }

        public Criteria andPlayerCnNameEqualTo(String value) {
            addCriterion("player_cn_name =", value, "playerCnName");
            return (Criteria) this;
        }

        public Criteria andPlayerCnNameNotEqualTo(String value) {
            addCriterion("player_cn_name <>", value, "playerCnName");
            return (Criteria) this;
        }

        public Criteria andPlayerCnNameGreaterThan(String value) {
            addCriterion("player_cn_name >", value, "playerCnName");
            return (Criteria) this;
        }

        public Criteria andPlayerCnNameGreaterThanOrEqualTo(String value) {
            addCriterion("player_cn_name >=", value, "playerCnName");
            return (Criteria) this;
        }

        public Criteria andPlayerCnNameLessThan(String value) {
            addCriterion("player_cn_name <", value, "playerCnName");
            return (Criteria) this;
        }

        public Criteria andPlayerCnNameLessThanOrEqualTo(String value) {
            addCriterion("player_cn_name <=", value, "playerCnName");
            return (Criteria) this;
        }

        public Criteria andPlayerCnNameLike(String value) {
            addCriterion("player_cn_name like", value, "playerCnName");
            return (Criteria) this;
        }

        public Criteria andPlayerCnNameNotLike(String value) {
            addCriterion("player_cn_name not like", value, "playerCnName");
            return (Criteria) this;
        }

        public Criteria andPlayerCnNameIn(List<String> values) {
            addCriterion("player_cn_name in", values, "playerCnName");
            return (Criteria) this;
        }

        public Criteria andPlayerCnNameNotIn(List<String> values) {
            addCriterion("player_cn_name not in", values, "playerCnName");
            return (Criteria) this;
        }

        public Criteria andPlayerCnNameBetween(String value1, String value2) {
            addCriterion("player_cn_name between", value1, value2, "playerCnName");
            return (Criteria) this;
        }

        public Criteria andPlayerCnNameNotBetween(String value1, String value2) {
            addCriterion("player_cn_name not between", value1, value2, "playerCnName");
            return (Criteria) this;
        }

        public Criteria andPlayerEnNameIsNull() {
            addCriterion("player_en_name is null");
            return (Criteria) this;
        }

        public Criteria andPlayerEnNameIsNotNull() {
            addCriterion("player_en_name is not null");
            return (Criteria) this;
        }

        public Criteria andPlayerEnNameEqualTo(String value) {
            addCriterion("player_en_name =", value, "playerEnName");
            return (Criteria) this;
        }

        public Criteria andPlayerEnNameNotEqualTo(String value) {
            addCriterion("player_en_name <>", value, "playerEnName");
            return (Criteria) this;
        }

        public Criteria andPlayerEnNameGreaterThan(String value) {
            addCriterion("player_en_name >", value, "playerEnName");
            return (Criteria) this;
        }

        public Criteria andPlayerEnNameGreaterThanOrEqualTo(String value) {
            addCriterion("player_en_name >=", value, "playerEnName");
            return (Criteria) this;
        }

        public Criteria andPlayerEnNameLessThan(String value) {
            addCriterion("player_en_name <", value, "playerEnName");
            return (Criteria) this;
        }

        public Criteria andPlayerEnNameLessThanOrEqualTo(String value) {
            addCriterion("player_en_name <=", value, "playerEnName");
            return (Criteria) this;
        }

        public Criteria andPlayerEnNameLike(String value) {
            addCriterion("player_en_name like", value, "playerEnName");
            return (Criteria) this;
        }

        public Criteria andPlayerEnNameNotLike(String value) {
            addCriterion("player_en_name not like", value, "playerEnName");
            return (Criteria) this;
        }

        public Criteria andPlayerEnNameIn(List<String> values) {
            addCriterion("player_en_name in", values, "playerEnName");
            return (Criteria) this;
        }

        public Criteria andPlayerEnNameNotIn(List<String> values) {
            addCriterion("player_en_name not in", values, "playerEnName");
            return (Criteria) this;
        }

        public Criteria andPlayerEnNameBetween(String value1, String value2) {
            addCriterion("player_en_name between", value1, value2, "playerEnName");
            return (Criteria) this;
        }

        public Criteria andPlayerEnNameNotBetween(String value1, String value2) {
            addCriterion("player_en_name not between", value1, value2, "playerEnName");
            return (Criteria) this;
        }

        public Criteria andPlayerLogoIsNull() {
            addCriterion("player_logo is null");
            return (Criteria) this;
        }

        public Criteria andPlayerLogoIsNotNull() {
            addCriterion("player_logo is not null");
            return (Criteria) this;
        }

        public Criteria andPlayerLogoEqualTo(String value) {
            addCriterion("player_logo =", value, "playerLogo");
            return (Criteria) this;
        }

        public Criteria andPlayerLogoNotEqualTo(String value) {
            addCriterion("player_logo <>", value, "playerLogo");
            return (Criteria) this;
        }

        public Criteria andPlayerLogoGreaterThan(String value) {
            addCriterion("player_logo >", value, "playerLogo");
            return (Criteria) this;
        }

        public Criteria andPlayerLogoGreaterThanOrEqualTo(String value) {
            addCriterion("player_logo >=", value, "playerLogo");
            return (Criteria) this;
        }

        public Criteria andPlayerLogoLessThan(String value) {
            addCriterion("player_logo <", value, "playerLogo");
            return (Criteria) this;
        }

        public Criteria andPlayerLogoLessThanOrEqualTo(String value) {
            addCriterion("player_logo <=", value, "playerLogo");
            return (Criteria) this;
        }

        public Criteria andPlayerLogoLike(String value) {
            addCriterion("player_logo like", value, "playerLogo");
            return (Criteria) this;
        }

        public Criteria andPlayerLogoNotLike(String value) {
            addCriterion("player_logo not like", value, "playerLogo");
            return (Criteria) this;
        }

        public Criteria andPlayerLogoIn(List<String> values) {
            addCriterion("player_logo in", values, "playerLogo");
            return (Criteria) this;
        }

        public Criteria andPlayerLogoNotIn(List<String> values) {
            addCriterion("player_logo not in", values, "playerLogo");
            return (Criteria) this;
        }

        public Criteria andPlayerLogoBetween(String value1, String value2) {
            addCriterion("player_logo between", value1, value2, "playerLogo");
            return (Criteria) this;
        }

        public Criteria andPlayerLogoNotBetween(String value1, String value2) {
            addCriterion("player_logo not between", value1, value2, "playerLogo");
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