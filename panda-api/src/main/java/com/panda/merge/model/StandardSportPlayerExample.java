package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class StandardSportPlayerExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public StandardSportPlayerExample() {
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

        public Criteria andRegionIdIsNull() {
            addCriterion("region_id is null");
            return (Criteria) this;
        }

        public Criteria andRegionIdIsNotNull() {
            addCriterion("region_id is not null");
            return (Criteria) this;
        }

        public Criteria andRegionIdEqualTo(Long value) {
            addCriterion("region_id =", value, "regionId");
            return (Criteria) this;
        }

        public Criteria andRegionIdNotEqualTo(Long value) {
            addCriterion("region_id <>", value, "regionId");
            return (Criteria) this;
        }

        public Criteria andRegionIdGreaterThan(Long value) {
            addCriterion("region_id >", value, "regionId");
            return (Criteria) this;
        }

        public Criteria andRegionIdGreaterThanOrEqualTo(Long value) {
            addCriterion("region_id >=", value, "regionId");
            return (Criteria) this;
        }

        public Criteria andRegionIdLessThan(Long value) {
            addCriterion("region_id <", value, "regionId");
            return (Criteria) this;
        }

        public Criteria andRegionIdLessThanOrEqualTo(Long value) {
            addCriterion("region_id <=", value, "regionId");
            return (Criteria) this;
        }

        public Criteria andRegionIdIn(List<Long> values) {
            addCriterion("region_id in", values, "regionId");
            return (Criteria) this;
        }

        public Criteria andRegionIdNotIn(List<Long> values) {
            addCriterion("region_id not in", values, "regionId");
            return (Criteria) this;
        }

        public Criteria andRegionIdBetween(Long value1, Long value2) {
            addCriterion("region_id between", value1, value2, "regionId");
            return (Criteria) this;
        }

        public Criteria andRegionIdNotBetween(Long value1, Long value2) {
            addCriterion("region_id not between", value1, value2, "regionId");
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

        public Criteria andPlayerManagerIdIsNull() {
            addCriterion("player_manager_id is null");
            return (Criteria) this;
        }

        public Criteria andPlayerManagerIdIsNotNull() {
            addCriterion("player_manager_id is not null");
            return (Criteria) this;
        }

        public Criteria andPlayerManagerIdEqualTo(String value) {
            addCriterion("player_manager_id =", value, "playerManagerId");
            return (Criteria) this;
        }

        public Criteria andPlayerManagerIdNotEqualTo(String value) {
            addCriterion("player_manager_id <>", value, "playerManagerId");
            return (Criteria) this;
        }

        public Criteria andPlayerManagerIdGreaterThan(String value) {
            addCriterion("player_manager_id >", value, "playerManagerId");
            return (Criteria) this;
        }

        public Criteria andPlayerManagerIdGreaterThanOrEqualTo(String value) {
            addCriterion("player_manager_id >=", value, "playerManagerId");
            return (Criteria) this;
        }

        public Criteria andPlayerManagerIdLessThan(String value) {
            addCriterion("player_manager_id <", value, "playerManagerId");
            return (Criteria) this;
        }

        public Criteria andPlayerManagerIdLessThanOrEqualTo(String value) {
            addCriterion("player_manager_id <=", value, "playerManagerId");
            return (Criteria) this;
        }

        public Criteria andPlayerManagerIdLike(String value) {
            addCriterion("player_manager_id like", value, "playerManagerId");
            return (Criteria) this;
        }

        public Criteria andPlayerManagerIdNotLike(String value) {
            addCriterion("player_manager_id not like", value, "playerManagerId");
            return (Criteria) this;
        }

        public Criteria andPlayerManagerIdIn(List<String> values) {
            addCriterion("player_manager_id in", values, "playerManagerId");
            return (Criteria) this;
        }

        public Criteria andPlayerManagerIdNotIn(List<String> values) {
            addCriterion("player_manager_id not in", values, "playerManagerId");
            return (Criteria) this;
        }

        public Criteria andPlayerManagerIdBetween(String value1, String value2) {
            addCriterion("player_manager_id between", value1, value2, "playerManagerId");
            return (Criteria) this;
        }

        public Criteria andPlayerManagerIdNotBetween(String value1, String value2) {
            addCriterion("player_manager_id not between", value1, value2, "playerManagerId");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerIdIsNull() {
            addCriterion("third_player_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerIdIsNotNull() {
            addCriterion("third_player_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerIdEqualTo(Long value) {
            addCriterion("third_player_id =", value, "thirdPlayerId");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerIdNotEqualTo(Long value) {
            addCriterion("third_player_id <>", value, "thirdPlayerId");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerIdGreaterThan(Long value) {
            addCriterion("third_player_id >", value, "thirdPlayerId");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerIdGreaterThanOrEqualTo(Long value) {
            addCriterion("third_player_id >=", value, "thirdPlayerId");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerIdLessThan(Long value) {
            addCriterion("third_player_id <", value, "thirdPlayerId");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerIdLessThanOrEqualTo(Long value) {
            addCriterion("third_player_id <=", value, "thirdPlayerId");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerIdIn(List<Long> values) {
            addCriterion("third_player_id in", values, "thirdPlayerId");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerIdNotIn(List<Long> values) {
            addCriterion("third_player_id not in", values, "thirdPlayerId");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerIdBetween(Long value1, Long value2) {
            addCriterion("third_player_id between", value1, value2, "thirdPlayerId");
            return (Criteria) this;
        }

        public Criteria andThirdPlayerIdNotBetween(Long value1, Long value2) {
            addCriterion("third_player_id not between", value1, value2, "thirdPlayerId");
            return (Criteria) this;
        }

        public Criteria andThirdSourcePlayerIdIsNull() {
            addCriterion("third_source_player_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdSourcePlayerIdIsNotNull() {
            addCriterion("third_source_player_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdSourcePlayerIdEqualTo(String value) {
            addCriterion("third_source_player_id =", value, "thirdSourcePlayerId");
            return (Criteria) this;
        }

        public Criteria andThirdSourcePlayerIdNotEqualTo(String value) {
            addCriterion("third_source_player_id <>", value, "thirdSourcePlayerId");
            return (Criteria) this;
        }

        public Criteria andThirdSourcePlayerIdGreaterThan(String value) {
            addCriterion("third_source_player_id >", value, "thirdSourcePlayerId");
            return (Criteria) this;
        }

        public Criteria andThirdSourcePlayerIdGreaterThanOrEqualTo(String value) {
            addCriterion("third_source_player_id >=", value, "thirdSourcePlayerId");
            return (Criteria) this;
        }

        public Criteria andThirdSourcePlayerIdLessThan(String value) {
            addCriterion("third_source_player_id <", value, "thirdSourcePlayerId");
            return (Criteria) this;
        }

        public Criteria andThirdSourcePlayerIdLessThanOrEqualTo(String value) {
            addCriterion("third_source_player_id <=", value, "thirdSourcePlayerId");
            return (Criteria) this;
        }

        public Criteria andThirdSourcePlayerIdLike(String value) {
            addCriterion("third_source_player_id like", value, "thirdSourcePlayerId");
            return (Criteria) this;
        }

        public Criteria andThirdSourcePlayerIdNotLike(String value) {
            addCriterion("third_source_player_id not like", value, "thirdSourcePlayerId");
            return (Criteria) this;
        }

        public Criteria andThirdSourcePlayerIdIn(List<String> values) {
            addCriterion("third_source_player_id in", values, "thirdSourcePlayerId");
            return (Criteria) this;
        }

        public Criteria andThirdSourcePlayerIdNotIn(List<String> values) {
            addCriterion("third_source_player_id not in", values, "thirdSourcePlayerId");
            return (Criteria) this;
        }

        public Criteria andThirdSourcePlayerIdBetween(String value1, String value2) {
            addCriterion("third_source_player_id between", value1, value2, "thirdSourcePlayerId");
            return (Criteria) this;
        }

        public Criteria andThirdSourcePlayerIdNotBetween(String value1, String value2) {
            addCriterion("third_source_player_id not between", value1, value2, "thirdSourcePlayerId");
            return (Criteria) this;
        }

        public Criteria andPictureUrlIsNull() {
            addCriterion("picture_url is null");
            return (Criteria) this;
        }

        public Criteria andPictureUrlIsNotNull() {
            addCriterion("picture_url is not null");
            return (Criteria) this;
        }

        public Criteria andPictureUrlEqualTo(String value) {
            addCriterion("picture_url =", value, "pictureUrl");
            return (Criteria) this;
        }

        public Criteria andPictureUrlNotEqualTo(String value) {
            addCriterion("picture_url <>", value, "pictureUrl");
            return (Criteria) this;
        }

        public Criteria andPictureUrlGreaterThan(String value) {
            addCriterion("picture_url >", value, "pictureUrl");
            return (Criteria) this;
        }

        public Criteria andPictureUrlGreaterThanOrEqualTo(String value) {
            addCriterion("picture_url >=", value, "pictureUrl");
            return (Criteria) this;
        }

        public Criteria andPictureUrlLessThan(String value) {
            addCriterion("picture_url <", value, "pictureUrl");
            return (Criteria) this;
        }

        public Criteria andPictureUrlLessThanOrEqualTo(String value) {
            addCriterion("picture_url <=", value, "pictureUrl");
            return (Criteria) this;
        }

        public Criteria andPictureUrlLike(String value) {
            addCriterion("picture_url like", value, "pictureUrl");
            return (Criteria) this;
        }

        public Criteria andPictureUrlNotLike(String value) {
            addCriterion("picture_url not like", value, "pictureUrl");
            return (Criteria) this;
        }

        public Criteria andPictureUrlIn(List<String> values) {
            addCriterion("picture_url in", values, "pictureUrl");
            return (Criteria) this;
        }

        public Criteria andPictureUrlNotIn(List<String> values) {
            addCriterion("picture_url not in", values, "pictureUrl");
            return (Criteria) this;
        }

        public Criteria andPictureUrlBetween(String value1, String value2) {
            addCriterion("picture_url between", value1, value2, "pictureUrl");
            return (Criteria) this;
        }

        public Criteria andPictureUrlNotBetween(String value1, String value2) {
            addCriterion("picture_url not between", value1, value2, "pictureUrl");
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

        public Criteria andWeightIsNull() {
            addCriterion("weight is null");
            return (Criteria) this;
        }

        public Criteria andWeightIsNotNull() {
            addCriterion("weight is not null");
            return (Criteria) this;
        }

        public Criteria andWeightEqualTo(Integer value) {
            addCriterion("weight =", value, "weight");
            return (Criteria) this;
        }

        public Criteria andWeightNotEqualTo(Integer value) {
            addCriterion("weight <>", value, "weight");
            return (Criteria) this;
        }

        public Criteria andWeightGreaterThan(Integer value) {
            addCriterion("weight >", value, "weight");
            return (Criteria) this;
        }

        public Criteria andWeightGreaterThanOrEqualTo(Integer value) {
            addCriterion("weight >=", value, "weight");
            return (Criteria) this;
        }

        public Criteria andWeightLessThan(Integer value) {
            addCriterion("weight <", value, "weight");
            return (Criteria) this;
        }

        public Criteria andWeightLessThanOrEqualTo(Integer value) {
            addCriterion("weight <=", value, "weight");
            return (Criteria) this;
        }

        public Criteria andWeightIn(List<Integer> values) {
            addCriterion("weight in", values, "weight");
            return (Criteria) this;
        }

        public Criteria andWeightNotIn(List<Integer> values) {
            addCriterion("weight not in", values, "weight");
            return (Criteria) this;
        }

        public Criteria andWeightBetween(Integer value1, Integer value2) {
            addCriterion("weight between", value1, value2, "weight");
            return (Criteria) this;
        }

        public Criteria andWeightNotBetween(Integer value1, Integer value2) {
            addCriterion("weight not between", value1, value2, "weight");
            return (Criteria) this;
        }

        public Criteria andHeightIsNull() {
            addCriterion("height is null");
            return (Criteria) this;
        }

        public Criteria andHeightIsNotNull() {
            addCriterion("height is not null");
            return (Criteria) this;
        }

        public Criteria andHeightEqualTo(Integer value) {
            addCriterion("height =", value, "height");
            return (Criteria) this;
        }

        public Criteria andHeightNotEqualTo(Integer value) {
            addCriterion("height <>", value, "height");
            return (Criteria) this;
        }

        public Criteria andHeightGreaterThan(Integer value) {
            addCriterion("height >", value, "height");
            return (Criteria) this;
        }

        public Criteria andHeightGreaterThanOrEqualTo(Integer value) {
            addCriterion("height >=", value, "height");
            return (Criteria) this;
        }

        public Criteria andHeightLessThan(Integer value) {
            addCriterion("height <", value, "height");
            return (Criteria) this;
        }

        public Criteria andHeightLessThanOrEqualTo(Integer value) {
            addCriterion("height <=", value, "height");
            return (Criteria) this;
        }

        public Criteria andHeightIn(List<Integer> values) {
            addCriterion("height in", values, "height");
            return (Criteria) this;
        }

        public Criteria andHeightNotIn(List<Integer> values) {
            addCriterion("height not in", values, "height");
            return (Criteria) this;
        }

        public Criteria andHeightBetween(Integer value1, Integer value2) {
            addCriterion("height between", value1, value2, "height");
            return (Criteria) this;
        }

        public Criteria andHeightNotBetween(Integer value1, Integer value2) {
            addCriterion("height not between", value1, value2, "height");
            return (Criteria) this;
        }

        public Criteria andGenderIsNull() {
            addCriterion("gender is null");
            return (Criteria) this;
        }

        public Criteria andGenderIsNotNull() {
            addCriterion("gender is not null");
            return (Criteria) this;
        }

        public Criteria andGenderEqualTo(Integer value) {
            addCriterion("gender =", value, "gender");
            return (Criteria) this;
        }

        public Criteria andGenderNotEqualTo(Integer value) {
            addCriterion("gender <>", value, "gender");
            return (Criteria) this;
        }

        public Criteria andGenderGreaterThan(Integer value) {
            addCriterion("gender >", value, "gender");
            return (Criteria) this;
        }

        public Criteria andGenderGreaterThanOrEqualTo(Integer value) {
            addCriterion("gender >=", value, "gender");
            return (Criteria) this;
        }

        public Criteria andGenderLessThan(Integer value) {
            addCriterion("gender <", value, "gender");
            return (Criteria) this;
        }

        public Criteria andGenderLessThanOrEqualTo(Integer value) {
            addCriterion("gender <=", value, "gender");
            return (Criteria) this;
        }

        public Criteria andGenderIn(List<Integer> values) {
            addCriterion("gender in", values, "gender");
            return (Criteria) this;
        }

        public Criteria andGenderNotIn(List<Integer> values) {
            addCriterion("gender not in", values, "gender");
            return (Criteria) this;
        }

        public Criteria andGenderBetween(Integer value1, Integer value2) {
            addCriterion("gender between", value1, value2, "gender");
            return (Criteria) this;
        }

        public Criteria andGenderNotBetween(Integer value1, Integer value2) {
            addCriterion("gender not between", value1, value2, "gender");
            return (Criteria) this;
        }

        public Criteria andBirthdayIsNull() {
            addCriterion("birthday is null");
            return (Criteria) this;
        }

        public Criteria andBirthdayIsNotNull() {
            addCriterion("birthday is not null");
            return (Criteria) this;
        }

        public Criteria andBirthdayEqualTo(String value) {
            addCriterion("birthday =", value, "birthday");
            return (Criteria) this;
        }

        public Criteria andBirthdayNotEqualTo(String value) {
            addCriterion("birthday <>", value, "birthday");
            return (Criteria) this;
        }

        public Criteria andBirthdayGreaterThan(String value) {
            addCriterion("birthday >", value, "birthday");
            return (Criteria) this;
        }

        public Criteria andBirthdayGreaterThanOrEqualTo(String value) {
            addCriterion("birthday >=", value, "birthday");
            return (Criteria) this;
        }

        public Criteria andBirthdayLessThan(String value) {
            addCriterion("birthday <", value, "birthday");
            return (Criteria) this;
        }

        public Criteria andBirthdayLessThanOrEqualTo(String value) {
            addCriterion("birthday <=", value, "birthday");
            return (Criteria) this;
        }

        public Criteria andBirthdayLike(String value) {
            addCriterion("birthday like", value, "birthday");
            return (Criteria) this;
        }

        public Criteria andBirthdayNotLike(String value) {
            addCriterion("birthday not like", value, "birthday");
            return (Criteria) this;
        }

        public Criteria andBirthdayIn(List<String> values) {
            addCriterion("birthday in", values, "birthday");
            return (Criteria) this;
        }

        public Criteria andBirthdayNotIn(List<String> values) {
            addCriterion("birthday not in", values, "birthday");
            return (Criteria) this;
        }

        public Criteria andBirthdayBetween(String value1, String value2) {
            addCriterion("birthday between", value1, value2, "birthday");
            return (Criteria) this;
        }

        public Criteria andBirthdayNotBetween(String value1, String value2) {
            addCriterion("birthday not between", value1, value2, "birthday");
            return (Criteria) this;
        }

        public Criteria andNameSpellIsNull() {
            addCriterion("name_spell is null");
            return (Criteria) this;
        }

        public Criteria andNameSpellIsNotNull() {
            addCriterion("name_spell is not null");
            return (Criteria) this;
        }

        public Criteria andNameSpellEqualTo(String value) {
            addCriterion("name_spell =", value, "nameSpell");
            return (Criteria) this;
        }

        public Criteria andNameSpellNotEqualTo(String value) {
            addCriterion("name_spell <>", value, "nameSpell");
            return (Criteria) this;
        }

        public Criteria andNameSpellGreaterThan(String value) {
            addCriterion("name_spell >", value, "nameSpell");
            return (Criteria) this;
        }

        public Criteria andNameSpellGreaterThanOrEqualTo(String value) {
            addCriterion("name_spell >=", value, "nameSpell");
            return (Criteria) this;
        }

        public Criteria andNameSpellLessThan(String value) {
            addCriterion("name_spell <", value, "nameSpell");
            return (Criteria) this;
        }

        public Criteria andNameSpellLessThanOrEqualTo(String value) {
            addCriterion("name_spell <=", value, "nameSpell");
            return (Criteria) this;
        }

        public Criteria andNameSpellLike(String value) {
            addCriterion("name_spell like", value, "nameSpell");
            return (Criteria) this;
        }

        public Criteria andNameSpellNotLike(String value) {
            addCriterion("name_spell not like", value, "nameSpell");
            return (Criteria) this;
        }

        public Criteria andNameSpellIn(List<String> values) {
            addCriterion("name_spell in", values, "nameSpell");
            return (Criteria) this;
        }

        public Criteria andNameSpellNotIn(List<String> values) {
            addCriterion("name_spell not in", values, "nameSpell");
            return (Criteria) this;
        }

        public Criteria andNameSpellBetween(String value1, String value2) {
            addCriterion("name_spell between", value1, value2, "nameSpell");
            return (Criteria) this;
        }

        public Criteria andNameSpellNotBetween(String value1, String value2) {
            addCriterion("name_spell not between", value1, value2, "nameSpell");
            return (Criteria) this;
        }

        public Criteria andCountryIdIsNull() {
            addCriterion("country_id is null");
            return (Criteria) this;
        }

        public Criteria andCountryIdIsNotNull() {
            addCriterion("country_id is not null");
            return (Criteria) this;
        }

        public Criteria andCountryIdEqualTo(Long value) {
            addCriterion("country_id =", value, "countryId");
            return (Criteria) this;
        }

        public Criteria andCountryIdNotEqualTo(Long value) {
            addCriterion("country_id <>", value, "countryId");
            return (Criteria) this;
        }

        public Criteria andCountryIdGreaterThan(Long value) {
            addCriterion("country_id >", value, "countryId");
            return (Criteria) this;
        }

        public Criteria andCountryIdGreaterThanOrEqualTo(Long value) {
            addCriterion("country_id >=", value, "countryId");
            return (Criteria) this;
        }

        public Criteria andCountryIdLessThan(Long value) {
            addCriterion("country_id <", value, "countryId");
            return (Criteria) this;
        }

        public Criteria andCountryIdLessThanOrEqualTo(Long value) {
            addCriterion("country_id <=", value, "countryId");
            return (Criteria) this;
        }

        public Criteria andCountryIdIn(List<Long> values) {
            addCriterion("country_id in", values, "countryId");
            return (Criteria) this;
        }

        public Criteria andCountryIdNotIn(List<Long> values) {
            addCriterion("country_id not in", values, "countryId");
            return (Criteria) this;
        }

        public Criteria andCountryIdBetween(Long value1, Long value2) {
            addCriterion("country_id between", value1, value2, "countryId");
            return (Criteria) this;
        }

        public Criteria andCountryIdNotBetween(Long value1, Long value2) {
            addCriterion("country_id not between", value1, value2, "countryId");
            return (Criteria) this;
        }

        public Criteria andPersonalFeatureIsNull() {
            addCriterion("personal_feature is null");
            return (Criteria) this;
        }

        public Criteria andPersonalFeatureIsNotNull() {
            addCriterion("personal_feature is not null");
            return (Criteria) this;
        }

        public Criteria andPersonalFeatureEqualTo(String value) {
            addCriterion("personal_feature =", value, "personalFeature");
            return (Criteria) this;
        }

        public Criteria andPersonalFeatureNotEqualTo(String value) {
            addCriterion("personal_feature <>", value, "personalFeature");
            return (Criteria) this;
        }

        public Criteria andPersonalFeatureGreaterThan(String value) {
            addCriterion("personal_feature >", value, "personalFeature");
            return (Criteria) this;
        }

        public Criteria andPersonalFeatureGreaterThanOrEqualTo(String value) {
            addCriterion("personal_feature >=", value, "personalFeature");
            return (Criteria) this;
        }

        public Criteria andPersonalFeatureLessThan(String value) {
            addCriterion("personal_feature <", value, "personalFeature");
            return (Criteria) this;
        }

        public Criteria andPersonalFeatureLessThanOrEqualTo(String value) {
            addCriterion("personal_feature <=", value, "personalFeature");
            return (Criteria) this;
        }

        public Criteria andPersonalFeatureLike(String value) {
            addCriterion("personal_feature like", value, "personalFeature");
            return (Criteria) this;
        }

        public Criteria andPersonalFeatureNotLike(String value) {
            addCriterion("personal_feature not like", value, "personalFeature");
            return (Criteria) this;
        }

        public Criteria andPersonalFeatureIn(List<String> values) {
            addCriterion("personal_feature in", values, "personalFeature");
            return (Criteria) this;
        }

        public Criteria andPersonalFeatureNotIn(List<String> values) {
            addCriterion("personal_feature not in", values, "personalFeature");
            return (Criteria) this;
        }

        public Criteria andPersonalFeatureBetween(String value1, String value2) {
            addCriterion("personal_feature between", value1, value2, "personalFeature");
            return (Criteria) this;
        }

        public Criteria andPersonalFeatureNotBetween(String value1, String value2) {
            addCriterion("personal_feature not between", value1, value2, "personalFeature");
            return (Criteria) this;
        }

        public Criteria andNickNameIsNull() {
            addCriterion("nick_name is null");
            return (Criteria) this;
        }

        public Criteria andNickNameIsNotNull() {
            addCriterion("nick_name is not null");
            return (Criteria) this;
        }

        public Criteria andNickNameEqualTo(String value) {
            addCriterion("nick_name =", value, "nickName");
            return (Criteria) this;
        }

        public Criteria andNickNameNotEqualTo(String value) {
            addCriterion("nick_name <>", value, "nickName");
            return (Criteria) this;
        }

        public Criteria andNickNameGreaterThan(String value) {
            addCriterion("nick_name >", value, "nickName");
            return (Criteria) this;
        }

        public Criteria andNickNameGreaterThanOrEqualTo(String value) {
            addCriterion("nick_name >=", value, "nickName");
            return (Criteria) this;
        }

        public Criteria andNickNameLessThan(String value) {
            addCriterion("nick_name <", value, "nickName");
            return (Criteria) this;
        }

        public Criteria andNickNameLessThanOrEqualTo(String value) {
            addCriterion("nick_name <=", value, "nickName");
            return (Criteria) this;
        }

        public Criteria andNickNameLike(String value) {
            addCriterion("nick_name like", value, "nickName");
            return (Criteria) this;
        }

        public Criteria andNickNameNotLike(String value) {
            addCriterion("nick_name not like", value, "nickName");
            return (Criteria) this;
        }

        public Criteria andNickNameIn(List<String> values) {
            addCriterion("nick_name in", values, "nickName");
            return (Criteria) this;
        }

        public Criteria andNickNameNotIn(List<String> values) {
            addCriterion("nick_name not in", values, "nickName");
            return (Criteria) this;
        }

        public Criteria andNickNameBetween(String value1, String value2) {
            addCriterion("nick_name between", value1, value2, "nickName");
            return (Criteria) this;
        }

        public Criteria andNickNameNotBetween(String value1, String value2) {
            addCriterion("nick_name not between", value1, value2, "nickName");
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

        public Criteria andRelatedDataSourceCoderNumIsNull() {
            addCriterion("related_data_source_coder_num is null");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderNumIsNotNull() {
            addCriterion("related_data_source_coder_num is not null");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderNumEqualTo(Integer value) {
            addCriterion("related_data_source_coder_num =", value, "relatedDataSourceCoderNum");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderNumNotEqualTo(Integer value) {
            addCriterion("related_data_source_coder_num <>", value, "relatedDataSourceCoderNum");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderNumGreaterThan(Integer value) {
            addCriterion("related_data_source_coder_num >", value, "relatedDataSourceCoderNum");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderNumGreaterThanOrEqualTo(Integer value) {
            addCriterion("related_data_source_coder_num >=", value, "relatedDataSourceCoderNum");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderNumLessThan(Integer value) {
            addCriterion("related_data_source_coder_num <", value, "relatedDataSourceCoderNum");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderNumLessThanOrEqualTo(Integer value) {
            addCriterion("related_data_source_coder_num <=", value, "relatedDataSourceCoderNum");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderNumIn(List<Integer> values) {
            addCriterion("related_data_source_coder_num in", values, "relatedDataSourceCoderNum");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderNumNotIn(List<Integer> values) {
            addCriterion("related_data_source_coder_num not in", values, "relatedDataSourceCoderNum");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderNumBetween(Integer value1, Integer value2) {
            addCriterion("related_data_source_coder_num between", value1, value2, "relatedDataSourceCoderNum");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderNumNotBetween(Integer value1, Integer value2) {
            addCriterion("related_data_source_coder_num not between", value1, value2, "relatedDataSourceCoderNum");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderListIsNull() {
            addCriterion("related_data_source_coder_list is null");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderListIsNotNull() {
            addCriterion("related_data_source_coder_list is not null");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderListEqualTo(String value) {
            addCriterion("related_data_source_coder_list =", value, "relatedDataSourceCoderList");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderListNotEqualTo(String value) {
            addCriterion("related_data_source_coder_list <>", value, "relatedDataSourceCoderList");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderListGreaterThan(String value) {
            addCriterion("related_data_source_coder_list >", value, "relatedDataSourceCoderList");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderListGreaterThanOrEqualTo(String value) {
            addCriterion("related_data_source_coder_list >=", value, "relatedDataSourceCoderList");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderListLessThan(String value) {
            addCriterion("related_data_source_coder_list <", value, "relatedDataSourceCoderList");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderListLessThanOrEqualTo(String value) {
            addCriterion("related_data_source_coder_list <=", value, "relatedDataSourceCoderList");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderListLike(String value) {
            addCriterion("related_data_source_coder_list like", value, "relatedDataSourceCoderList");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderListNotLike(String value) {
            addCriterion("related_data_source_coder_list not like", value, "relatedDataSourceCoderList");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderListIn(List<String> values) {
            addCriterion("related_data_source_coder_list in", values, "relatedDataSourceCoderList");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderListNotIn(List<String> values) {
            addCriterion("related_data_source_coder_list not in", values, "relatedDataSourceCoderList");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderListBetween(String value1, String value2) {
            addCriterion("related_data_source_coder_list between", value1, value2, "relatedDataSourceCoderList");
            return (Criteria) this;
        }

        public Criteria andRelatedDataSourceCoderListNotBetween(String value1, String value2) {
            addCriterion("related_data_source_coder_list not between", value1, value2, "relatedDataSourceCoderList");
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