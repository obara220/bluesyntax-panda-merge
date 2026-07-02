package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class ThirdMatchLineupExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ThirdMatchLineupExample() {
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

        public Criteria andThirdPlayerNameIsNull() {
            addCriterion("third_player_name is null");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerNameIsNotNull() {
            addCriterion("third_player_name is not null");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerNameEqualTo(String value) {
            addCriterion("third_player_name =", value, "thirdPlayerName");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerNameNotEqualTo(String value) {
            addCriterion("third_player_name <>", value, "thirdPlayerName");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerNameGreaterThan(String value) {
            addCriterion("third_player_name >", value, "thirdPlayerName");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerNameGreaterThanOrEqualTo(String value) {
            addCriterion("third_player_name >=", value, "thirdPlayerName");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerNameLessThan(String value) {
            addCriterion("third_player_name <", value, "thirdPlayerName");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerNameLessThanOrEqualTo(String value) {
            addCriterion("third_player_name <=", value, "thirdPlayerName");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerNameLike(String value) {
            addCriterion("third_player_name like", value, "thirdPlayerName");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerNameNotLike(String value) {
            addCriterion("third_player_name not like", value, "thirdPlayerName");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerNameIn(List<String> values) {
            addCriterion("third_player_name in", values, "thirdPlayerName");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerNameNotIn(List<String> values) {
            addCriterion("third_player_name not in", values, "thirdPlayerName");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerNameBetween(String value1, String value2) {
            addCriterion("third_player_name between", value1, value2, "thirdPlayerName");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerNameNotBetween(String value1, String value2) {
            addCriterion("third_player_name not between", value1, value2, "thirdPlayerName");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerPicUrlIsNull() {
            addCriterion("third_player_pic_url is null");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerPicUrlIsNotNull() {
            addCriterion("third_player_pic_url is not null");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerPicUrlEqualTo(String value) {
            addCriterion("third_player_pic_url =", value, "thirdPlayerPicUrl");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerPicUrlNotEqualTo(String value) {
            addCriterion("third_player_pic_url <>", value, "thirdPlayerPicUrl");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerPicUrlGreaterThan(String value) {
            addCriterion("third_player_pic_url >", value, "thirdPlayerPicUrl");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerPicUrlGreaterThanOrEqualTo(String value) {
            addCriterion("third_player_pic_url >=", value, "thirdPlayerPicUrl");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerPicUrlLessThan(String value) {
            addCriterion("third_player_pic_url <", value, "thirdPlayerPicUrl");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerPicUrlLessThanOrEqualTo(String value) {
            addCriterion("third_player_pic_url <=", value, "thirdPlayerPicUrl");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerPicUrlLike(String value) {
            addCriterion("third_player_pic_url like", value, "thirdPlayerPicUrl");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerPicUrlNotLike(String value) {
            addCriterion("third_player_pic_url not like", value, "thirdPlayerPicUrl");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerPicUrlIn(List<String> values) {
            addCriterion("third_player_pic_url in", values, "thirdPlayerPicUrl");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerPicUrlNotIn(List<String> values) {
            addCriterion("third_player_pic_url not in", values, "thirdPlayerPicUrl");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerPicUrlBetween(String value1, String value2) {
            addCriterion("third_player_pic_url between", value1, value2, "thirdPlayerPicUrl");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerPicUrlNotBetween(String value1, String value2) {
            addCriterion("third_player_pic_url not between", value1, value2, "thirdPlayerPicUrl");
            return (Criteria) this;
        }

        public Criteria andPositionIsNull() {
            addCriterion("position is null");
            return (Criteria) this;
        }

        public Criteria andPositionIsNotNull() {
            addCriterion("position is not null");
            return (Criteria) this;
        }

        public Criteria andPositionEqualTo(Integer value) {
            addCriterion("position =", value, "position");
            return (Criteria) this;
        }

        public Criteria andPositionNotEqualTo(Integer value) {
            addCriterion("position <>", value, "position");
            return (Criteria) this;
        }

        public Criteria andPositionGreaterThan(Integer value) {
            addCriterion("position >", value, "position");
            return (Criteria) this;
        }

        public Criteria andPositionGreaterThanOrEqualTo(Integer value) {
            addCriterion("position >=", value, "position");
            return (Criteria) this;
        }

        public Criteria andPositionLessThan(Integer value) {
            addCriterion("position <", value, "position");
            return (Criteria) this;
        }

        public Criteria andPositionLessThanOrEqualTo(Integer value) {
            addCriterion("position <=", value, "position");
            return (Criteria) this;
        }

        public Criteria andPositionIn(List<Integer> values) {
            addCriterion("position in", values, "position");
            return (Criteria) this;
        }

        public Criteria andPositionNotIn(List<Integer> values) {
            addCriterion("position not in", values, "position");
            return (Criteria) this;
        }

        public Criteria andPositionBetween(Integer value1, Integer value2) {
            addCriterion("position between", value1, value2, "position");
            return (Criteria) this;
        }

        public Criteria andPositionNotBetween(Integer value1, Integer value2) {
            addCriterion("position not between", value1, value2, "position");
            return (Criteria) this;
        }

        public Criteria andShirtNumberIsNull() {
            addCriterion("shirt_number is null");
            return (Criteria) this;
        }

        public Criteria andShirtNumberIsNotNull() {
            addCriterion("shirt_number is not null");
            return (Criteria) this;
        }

        public Criteria andShirtNumberEqualTo(Integer value) {
            addCriterion("shirt_number =", value, "shirtNumber");
            return (Criteria) this;
        }

        public Criteria andShirtNumberNotEqualTo(Integer value) {
            addCriterion("shirt_number <>", value, "shirtNumber");
            return (Criteria) this;
        }

        public Criteria andShirtNumberGreaterThan(Integer value) {
            addCriterion("shirt_number >", value, "shirtNumber");
            return (Criteria) this;
        }

        public Criteria andShirtNumberGreaterThanOrEqualTo(Integer value) {
            addCriterion("shirt_number >=", value, "shirtNumber");
            return (Criteria) this;
        }

        public Criteria andShirtNumberLessThan(Integer value) {
            addCriterion("shirt_number <", value, "shirtNumber");
            return (Criteria) this;
        }

        public Criteria andShirtNumberLessThanOrEqualTo(Integer value) {
            addCriterion("shirt_number <=", value, "shirtNumber");
            return (Criteria) this;
        }

        public Criteria andShirtNumberIn(List<Integer> values) {
            addCriterion("shirt_number in", values, "shirtNumber");
            return (Criteria) this;
        }

        public Criteria andShirtNumberNotIn(List<Integer> values) {
            addCriterion("shirt_number not in", values, "shirtNumber");
            return (Criteria) this;
        }

        public Criteria andShirtNumberBetween(Integer value1, Integer value2) {
            addCriterion("shirt_number between", value1, value2, "shirtNumber");
            return (Criteria) this;
        }

        public Criteria andShirtNumberNotBetween(Integer value1, Integer value2) {
            addCriterion("shirt_number not between", value1, value2, "shirtNumber");
            return (Criteria) this;
        }

        public Criteria andSubstituteIsNull() {
            addCriterion("substitute is null");
            return (Criteria) this;
        }

        public Criteria andSubstituteIsNotNull() {
            addCriterion("substitute is not null");
            return (Criteria) this;
        }

        public Criteria andSubstituteEqualTo(Integer value) {
            addCriterion("substitute =", value, "substitute");
            return (Criteria) this;
        }

        public Criteria andSubstituteNotEqualTo(Integer value) {
            addCriterion("substitute <>", value, "substitute");
            return (Criteria) this;
        }

        public Criteria andSubstituteGreaterThan(Integer value) {
            addCriterion("substitute >", value, "substitute");
            return (Criteria) this;
        }

        public Criteria andSubstituteGreaterThanOrEqualTo(Integer value) {
            addCriterion("substitute >=", value, "substitute");
            return (Criteria) this;
        }

        public Criteria andSubstituteLessThan(Integer value) {
            addCriterion("substitute <", value, "substitute");
            return (Criteria) this;
        }

        public Criteria andSubstituteLessThanOrEqualTo(Integer value) {
            addCriterion("substitute <=", value, "substitute");
            return (Criteria) this;
        }

        public Criteria andSubstituteIn(List<Integer> values) {
            addCriterion("substitute in", values, "substitute");
            return (Criteria) this;
        }

        public Criteria andSubstituteNotIn(List<Integer> values) {
            addCriterion("substitute not in", values, "substitute");
            return (Criteria) this;
        }

        public Criteria andSubstituteBetween(Integer value1, Integer value2) {
            addCriterion("substitute between", value1, value2, "substitute");
            return (Criteria) this;
        }

        public Criteria andSubstituteNotBetween(Integer value1, Integer value2) {
            addCriterion("substitute not between", value1, value2, "substitute");
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

        public Criteria andHomeAwayIsNull() {
            addCriterion("home_away is null");
            return (Criteria) this;
        }

        public Criteria andHomeAwayIsNotNull() {
            addCriterion("home_away is not null");
            return (Criteria) this;
        }

        public Criteria andHomeAwayEqualTo(Integer value) {
            addCriterion("home_away =", value, "homeAway");
            return (Criteria) this;
        }

        public Criteria andHomeAwayNotEqualTo(Integer value) {
            addCriterion("home_away <>", value, "homeAway");
            return (Criteria) this;
        }

        public Criteria andHomeAwayGreaterThan(Integer value) {
            addCriterion("home_away >", value, "homeAway");
            return (Criteria) this;
        }

        public Criteria andHomeAwayGreaterThanOrEqualTo(Integer value) {
            addCriterion("home_away >=", value, "homeAway");
            return (Criteria) this;
        }

        public Criteria andHomeAwayLessThan(Integer value) {
            addCriterion("home_away <", value, "homeAway");
            return (Criteria) this;
        }

        public Criteria andHomeAwayLessThanOrEqualTo(Integer value) {
            addCriterion("home_away <=", value, "homeAway");
            return (Criteria) this;
        }

        public Criteria andHomeAwayIn(List<Integer> values) {
            addCriterion("home_away in", values, "homeAway");
            return (Criteria) this;
        }

        public Criteria andHomeAwayNotIn(List<Integer> values) {
            addCriterion("home_away not in", values, "homeAway");
            return (Criteria) this;
        }

        public Criteria andHomeAwayBetween(Integer value1, Integer value2) {
            addCriterion("home_away between", value1, value2, "homeAway");
            return (Criteria) this;
        }

        public Criteria andHomeAwayNotBetween(Integer value1, Integer value2) {
            addCriterion("home_away not between", value1, value2, "homeAway");
            return (Criteria) this;
        }

        public Criteria andPositionNameIsNull() {
            addCriterion("position_name is null");
            return (Criteria) this;
        }

        public Criteria andPositionNameIsNotNull() {
            addCriterion("position_name is not null");
            return (Criteria) this;
        }

        public Criteria andPositionNameEqualTo(String value) {
            addCriterion("position_name =", value, "positionName");
            return (Criteria) this;
        }

        public Criteria andPositionNameNotEqualTo(String value) {
            addCriterion("position_name <>", value, "positionName");
            return (Criteria) this;
        }

        public Criteria andPositionNameGreaterThan(String value) {
            addCriterion("position_name >", value, "positionName");
            return (Criteria) this;
        }

        public Criteria andPositionNameGreaterThanOrEqualTo(String value) {
            addCriterion("position_name >=", value, "positionName");
            return (Criteria) this;
        }

        public Criteria andPositionNameLessThan(String value) {
            addCriterion("position_name <", value, "positionName");
            return (Criteria) this;
        }

        public Criteria andPositionNameLessThanOrEqualTo(String value) {
            addCriterion("position_name <=", value, "positionName");
            return (Criteria) this;
        }

        public Criteria andPositionNameLike(String value) {
            addCriterion("position_name like", value, "positionName");
            return (Criteria) this;
        }

        public Criteria andPositionNameNotLike(String value) {
            addCriterion("position_name not like", value, "positionName");
            return (Criteria) this;
        }

        public Criteria andPositionNameIn(List<String> values) {
            addCriterion("position_name in", values, "positionName");
            return (Criteria) this;
        }

        public Criteria andPositionNameNotIn(List<String> values) {
            addCriterion("position_name not in", values, "positionName");
            return (Criteria) this;
        }

        public Criteria andPositionNameBetween(String value1, String value2) {
            addCriterion("position_name between", value1, value2, "positionName");
            return (Criteria) this;
        }

        public Criteria andPositionNameNotBetween(String value1, String value2) {
            addCriterion("position_name not between", value1, value2, "positionName");
            return (Criteria) this;
        }

        public Criteria andInvalidIsNull() {
            addCriterion("invalid is null");
            return (Criteria) this;
        }

        public Criteria andInvalidIsNotNull() {
            addCriterion("invalid is not null");
            return (Criteria) this;
        }

        public Criteria andInvalidEqualTo(Integer value) {
            addCriterion("invalid =", value, "invalid");
            return (Criteria) this;
        }

        public Criteria andInvalidNotEqualTo(Integer value) {
            addCriterion("invalid <>", value, "invalid");
            return (Criteria) this;
        }

        public Criteria andInvalidGreaterThan(Integer value) {
            addCriterion("invalid >", value, "invalid");
            return (Criteria) this;
        }

        public Criteria andInvalidGreaterThanOrEqualTo(Integer value) {
            addCriterion("invalid >=", value, "invalid");
            return (Criteria) this;
        }

        public Criteria andInvalidLessThan(Integer value) {
            addCriterion("invalid <", value, "invalid");
            return (Criteria) this;
        }

        public Criteria andInvalidLessThanOrEqualTo(Integer value) {
            addCriterion("invalid <=", value, "invalid");
            return (Criteria) this;
        }

        public Criteria andInvalidIn(List<Integer> values) {
            addCriterion("invalid in", values, "invalid");
            return (Criteria) this;
        }

        public Criteria andInvalidNotIn(List<Integer> values) {
            addCriterion("invalid not in", values, "invalid");
            return (Criteria) this;
        }

        public Criteria andInvalidBetween(Integer value1, Integer value2) {
            addCriterion("invalid between", value1, value2, "invalid");
            return (Criteria) this;
        }

        public Criteria andInvalidNotBetween(Integer value1, Integer value2) {
            addCriterion("invalid not between", value1, value2, "invalid");
            return (Criteria) this;
        }

        public Criteria andOverallRatingsIsNull() {
            addCriterion("overall_ratings is null");
            return (Criteria) this;
        }

        public Criteria andOverallRatingsIsNotNull() {
            addCriterion("overall_ratings is not null");
            return (Criteria) this;
        }

        public Criteria andOverallRatingsEqualTo(String value) {
            addCriterion("overall_ratings =", value, "overallRatings");
            return (Criteria) this;
        }

        public Criteria andOverallRatingsNotEqualTo(String value) {
            addCriterion("overall_ratings <>", value, "overallRatings");
            return (Criteria) this;
        }

        public Criteria andOverallRatingsGreaterThan(String value) {
            addCriterion("overall_ratings >", value, "overallRatings");
            return (Criteria) this;
        }

        public Criteria andOverallRatingsGreaterThanOrEqualTo(String value) {
            addCriterion("overall_ratings >=", value, "overallRatings");
            return (Criteria) this;
        }

        public Criteria andOverallRatingsLessThan(String value) {
            addCriterion("overall_ratings <", value, "overallRatings");
            return (Criteria) this;
        }

        public Criteria andOverallRatingsLessThanOrEqualTo(String value) {
            addCriterion("overall_ratings <=", value, "overallRatings");
            return (Criteria) this;
        }

        public Criteria andOverallRatingsLike(String value) {
            addCriterion("overall_ratings like", value, "overallRatings");
            return (Criteria) this;
        }

        public Criteria andOverallRatingsNotLike(String value) {
            addCriterion("overall_ratings not like", value, "overallRatings");
            return (Criteria) this;
        }

        public Criteria andOverallRatingsIn(List<String> values) {
            addCriterion("overall_ratings in", values, "overallRatings");
            return (Criteria) this;
        }

        public Criteria andOverallRatingsNotIn(List<String> values) {
            addCriterion("overall_ratings not in", values, "overallRatings");
            return (Criteria) this;
        }

        public Criteria andOverallRatingsBetween(String value1, String value2) {
            addCriterion("overall_ratings between", value1, value2, "overallRatings");
            return (Criteria) this;
        }

        public Criteria andOverallRatingsNotBetween(String value1, String value2) {
            addCriterion("overall_ratings not between", value1, value2, "overallRatings");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerEnNameIsNull() {
            addCriterion("third_player_en_name is null");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerEnNameIsNotNull() {
            addCriterion("third_player_en_name is not null");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerEnNameEqualTo(String value) {
            addCriterion("third_player_en_name =", value, "thirdPlayerEnName");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerEnNameNotEqualTo(String value) {
            addCriterion("third_player_en_name <>", value, "thirdPlayerEnName");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerEnNameGreaterThan(String value) {
            addCriterion("third_player_en_name >", value, "thirdPlayerEnName");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerEnNameGreaterThanOrEqualTo(String value) {
            addCriterion("third_player_en_name >=", value, "thirdPlayerEnName");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerEnNameLessThan(String value) {
            addCriterion("third_player_en_name <", value, "thirdPlayerEnName");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerEnNameLessThanOrEqualTo(String value) {
            addCriterion("third_player_en_name <=", value, "thirdPlayerEnName");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerEnNameLike(String value) {
            addCriterion("third_player_en_name like", value, "thirdPlayerEnName");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerEnNameNotLike(String value) {
            addCriterion("third_player_en_name not like", value, "thirdPlayerEnName");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerEnNameIn(List<String> values) {
            addCriterion("third_player_en_name in", values, "thirdPlayerEnName");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerEnNameNotIn(List<String> values) {
            addCriterion("third_player_en_name not in", values, "thirdPlayerEnName");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerEnNameBetween(String value1, String value2) {
            addCriterion("third_player_en_name between", value1, value2, "thirdPlayerEnName");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerEnNameNotBetween(String value1, String value2) {
            addCriterion("third_player_en_name not between", value1, value2, "thirdPlayerEnName");
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

        public Criteria andHomeFormationIsNull() {
            addCriterion("home_formation is null");
            return (Criteria) this;
        }

        public Criteria andHomeFormationIsNotNull() {
            addCriterion("home_formation is not null");
            return (Criteria) this;
        }

        public Criteria andHomeFormationEqualTo(String value) {
            addCriterion("home_formation =", value, "homeFormation");
            return (Criteria) this;
        }

        public Criteria andHomeFormationNotEqualTo(String value) {
            addCriterion("home_formation <>", value, "homeFormation");
            return (Criteria) this;
        }

        public Criteria andHomeFormationGreaterThan(String value) {
            addCriterion("home_formation >", value, "homeFormation");
            return (Criteria) this;
        }

        public Criteria andHomeFormationGreaterThanOrEqualTo(String value) {
            addCriterion("home_formation >=", value, "homeFormation");
            return (Criteria) this;
        }

        public Criteria andHomeFormationLessThan(String value) {
            addCriterion("home_formation <", value, "homeFormation");
            return (Criteria) this;
        }

        public Criteria andHomeFormationLessThanOrEqualTo(String value) {
            addCriterion("home_formation <=", value, "homeFormation");
            return (Criteria) this;
        }

        public Criteria andHomeFormationLike(String value) {
            addCriterion("home_formation like", value, "homeFormation");
            return (Criteria) this;
        }

        public Criteria andHomeFormationNotLike(String value) {
            addCriterion("home_formation not like", value, "homeFormation");
            return (Criteria) this;
        }

        public Criteria andHomeFormationIn(List<String> values) {
            addCriterion("home_formation in", values, "homeFormation");
            return (Criteria) this;
        }

        public Criteria andHomeFormationNotIn(List<String> values) {
            addCriterion("home_formation not in", values, "homeFormation");
            return (Criteria) this;
        }

        public Criteria andHomeFormationBetween(String value1, String value2) {
            addCriterion("home_formation between", value1, value2, "homeFormation");
            return (Criteria) this;
        }

        public Criteria andHomeFormationNotBetween(String value1, String value2) {
            addCriterion("home_formation not between", value1, value2, "homeFormation");
            return (Criteria) this;
        }

        public Criteria andAwayFormationIsNull() {
            addCriterion("away_formation is null");
            return (Criteria) this;
        }

        public Criteria andAwayFormationIsNotNull() {
            addCriterion("away_formation is not null");
            return (Criteria) this;
        }

        public Criteria andAwayFormationEqualTo(String value) {
            addCriterion("away_formation =", value, "awayFormation");
            return (Criteria) this;
        }

        public Criteria andAwayFormationNotEqualTo(String value) {
            addCriterion("away_formation <>", value, "awayFormation");
            return (Criteria) this;
        }

        public Criteria andAwayFormationGreaterThan(String value) {
            addCriterion("away_formation >", value, "awayFormation");
            return (Criteria) this;
        }

        public Criteria andAwayFormationGreaterThanOrEqualTo(String value) {
            addCriterion("away_formation >=", value, "awayFormation");
            return (Criteria) this;
        }

        public Criteria andAwayFormationLessThan(String value) {
            addCriterion("away_formation <", value, "awayFormation");
            return (Criteria) this;
        }

        public Criteria andAwayFormationLessThanOrEqualTo(String value) {
            addCriterion("away_formation <=", value, "awayFormation");
            return (Criteria) this;
        }

        public Criteria andAwayFormationLike(String value) {
            addCriterion("away_formation like", value, "awayFormation");
            return (Criteria) this;
        }

        public Criteria andAwayFormationNotLike(String value) {
            addCriterion("away_formation not like", value, "awayFormation");
            return (Criteria) this;
        }

        public Criteria andAwayFormationIn(List<String> values) {
            addCriterion("away_formation in", values, "awayFormation");
            return (Criteria) this;
        }

        public Criteria andAwayFormationNotIn(List<String> values) {
            addCriterion("away_formation not in", values, "awayFormation");
            return (Criteria) this;
        }

        public Criteria andAwayFormationBetween(String value1, String value2) {
            addCriterion("away_formation between", value1, value2, "awayFormation");
            return (Criteria) this;
        }

        public Criteria andAwayFormationNotBetween(String value1, String value2) {
            addCriterion("away_formation not between", value1, value2, "awayFormation");
            return (Criteria) this;
        }

        public Criteria andPlayTimeIsNull() {
            addCriterion("play_time is null");
            return (Criteria) this;
        }

        public Criteria andPlayTimeIsNotNull() {
            addCriterion("play_time is not null");
            return (Criteria) this;
        }

        public Criteria andPlayTimeEqualTo(String value) {
            addCriterion("play_time =", value, "playTime");
            return (Criteria) this;
        }

        public Criteria andPlayTimeNotEqualTo(String value) {
            addCriterion("play_time <>", value, "playTime");
            return (Criteria) this;
        }

        public Criteria andPlayTimeGreaterThan(String value) {
            addCriterion("play_time >", value, "playTime");
            return (Criteria) this;
        }

        public Criteria andPlayTimeGreaterThanOrEqualTo(String value) {
            addCriterion("play_time >=", value, "playTime");
            return (Criteria) this;
        }

        public Criteria andPlayTimeLessThan(String value) {
            addCriterion("play_time <", value, "playTime");
            return (Criteria) this;
        }

        public Criteria andPlayTimeLessThanOrEqualTo(String value) {
            addCriterion("play_time <=", value, "playTime");
            return (Criteria) this;
        }

        public Criteria andPlayTimeLike(String value) {
            addCriterion("play_time like", value, "playTime");
            return (Criteria) this;
        }

        public Criteria andPlayTimeNotLike(String value) {
            addCriterion("play_time not like", value, "playTime");
            return (Criteria) this;
        }

        public Criteria andPlayTimeIn(List<String> values) {
            addCriterion("play_time in", values, "playTime");
            return (Criteria) this;
        }

        public Criteria andPlayTimeNotIn(List<String> values) {
            addCriterion("play_time not in", values, "playTime");
            return (Criteria) this;
        }

        public Criteria andPlayTimeBetween(String value1, String value2) {
            addCriterion("play_time between", value1, value2, "playTime");
            return (Criteria) this;
        }

        public Criteria andPlayTimeNotBetween(String value1, String value2) {
            addCriterion("play_time not between", value1, value2, "playTime");
            return (Criteria) this;
        }

        public Criteria andAssistIsNull() {
            addCriterion("assist is null");
            return (Criteria) this;
        }

        public Criteria andAssistIsNotNull() {
            addCriterion("assist is not null");
            return (Criteria) this;
        }

        public Criteria andAssistEqualTo(String value) {
            addCriterion("assist =", value, "assist");
            return (Criteria) this;
        }

        public Criteria andAssistNotEqualTo(String value) {
            addCriterion("assist <>", value, "assist");
            return (Criteria) this;
        }

        public Criteria andAssistGreaterThan(String value) {
            addCriterion("assist >", value, "assist");
            return (Criteria) this;
        }

        public Criteria andAssistGreaterThanOrEqualTo(String value) {
            addCriterion("assist >=", value, "assist");
            return (Criteria) this;
        }

        public Criteria andAssistLessThan(String value) {
            addCriterion("assist <", value, "assist");
            return (Criteria) this;
        }

        public Criteria andAssistLessThanOrEqualTo(String value) {
            addCriterion("assist <=", value, "assist");
            return (Criteria) this;
        }

        public Criteria andAssistLike(String value) {
            addCriterion("assist like", value, "assist");
            return (Criteria) this;
        }

        public Criteria andAssistNotLike(String value) {
            addCriterion("assist not like", value, "assist");
            return (Criteria) this;
        }

        public Criteria andAssistIn(List<String> values) {
            addCriterion("assist in", values, "assist");
            return (Criteria) this;
        }

        public Criteria andAssistNotIn(List<String> values) {
            addCriterion("assist not in", values, "assist");
            return (Criteria) this;
        }

        public Criteria andAssistBetween(String value1, String value2) {
            addCriterion("assist between", value1, value2, "assist");
            return (Criteria) this;
        }

        public Criteria andAssistNotBetween(String value1, String value2) {
            addCriterion("assist not between", value1, value2, "assist");
            return (Criteria) this;
        }

        public Criteria andReboundIsNull() {
            addCriterion("rebound is null");
            return (Criteria) this;
        }

        public Criteria andReboundIsNotNull() {
            addCriterion("rebound is not null");
            return (Criteria) this;
        }

        public Criteria andReboundEqualTo(String value) {
            addCriterion("rebound =", value, "rebound");
            return (Criteria) this;
        }

        public Criteria andReboundNotEqualTo(String value) {
            addCriterion("rebound <>", value, "rebound");
            return (Criteria) this;
        }

        public Criteria andReboundGreaterThan(String value) {
            addCriterion("rebound >", value, "rebound");
            return (Criteria) this;
        }

        public Criteria andReboundGreaterThanOrEqualTo(String value) {
            addCriterion("rebound >=", value, "rebound");
            return (Criteria) this;
        }

        public Criteria andReboundLessThan(String value) {
            addCriterion("rebound <", value, "rebound");
            return (Criteria) this;
        }

        public Criteria andReboundLessThanOrEqualTo(String value) {
            addCriterion("rebound <=", value, "rebound");
            return (Criteria) this;
        }

        public Criteria andReboundLike(String value) {
            addCriterion("rebound like", value, "rebound");
            return (Criteria) this;
        }

        public Criteria andReboundNotLike(String value) {
            addCriterion("rebound not like", value, "rebound");
            return (Criteria) this;
        }

        public Criteria andReboundIn(List<String> values) {
            addCriterion("rebound in", values, "rebound");
            return (Criteria) this;
        }

        public Criteria andReboundNotIn(List<String> values) {
            addCriterion("rebound not in", values, "rebound");
            return (Criteria) this;
        }

        public Criteria andReboundBetween(String value1, String value2) {
            addCriterion("rebound between", value1, value2, "rebound");
            return (Criteria) this;
        }

        public Criteria andReboundNotBetween(String value1, String value2) {
            addCriterion("rebound not between", value1, value2, "rebound");
            return (Criteria) this;
        }

        public Criteria andPointIsNull() {
            addCriterion("point is null");
            return (Criteria) this;
        }

        public Criteria andPointIsNotNull() {
            addCriterion("point is not null");
            return (Criteria) this;
        }

        public Criteria andPointEqualTo(String value) {
            addCriterion("point =", value, "point");
            return (Criteria) this;
        }

        public Criteria andPointNotEqualTo(String value) {
            addCriterion("point <>", value, "point");
            return (Criteria) this;
        }

        public Criteria andPointGreaterThan(String value) {
            addCriterion("point >", value, "point");
            return (Criteria) this;
        }

        public Criteria andPointGreaterThanOrEqualTo(String value) {
            addCriterion("point >=", value, "point");
            return (Criteria) this;
        }

        public Criteria andPointLessThan(String value) {
            addCriterion("point <", value, "point");
            return (Criteria) this;
        }

        public Criteria andPointLessThanOrEqualTo(String value) {
            addCriterion("point <=", value, "point");
            return (Criteria) this;
        }

        public Criteria andPointLike(String value) {
            addCriterion("point like", value, "point");
            return (Criteria) this;
        }

        public Criteria andPointNotLike(String value) {
            addCriterion("point not like", value, "point");
            return (Criteria) this;
        }

        public Criteria andPointIn(List<String> values) {
            addCriterion("point in", values, "point");
            return (Criteria) this;
        }

        public Criteria andPointNotIn(List<String> values) {
            addCriterion("point not in", values, "point");
            return (Criteria) this;
        }

        public Criteria andPointBetween(String value1, String value2) {
            addCriterion("point between", value1, value2, "point");
            return (Criteria) this;
        }

        public Criteria andPointNotBetween(String value1, String value2) {
            addCriterion("point not between", value1, value2, "point");
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