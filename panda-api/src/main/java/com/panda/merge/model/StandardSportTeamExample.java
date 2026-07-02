package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class StandardSportTeamExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public StandardSportTeamExample() {
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

        public Criteria andThirdTeamIdIsNull() {
            addCriterion("third_team_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdTeamIdIsNotNull() {
            addCriterion("third_team_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdTeamIdEqualTo(Long value) {
            addCriterion("third_team_id =", value, "thirdTeamId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamIdNotEqualTo(Long value) {
            addCriterion("third_team_id <>", value, "thirdTeamId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamIdGreaterThan(Long value) {
            addCriterion("third_team_id >", value, "thirdTeamId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamIdGreaterThanOrEqualTo(Long value) {
            addCriterion("third_team_id >=", value, "thirdTeamId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamIdLessThan(Long value) {
            addCriterion("third_team_id <", value, "thirdTeamId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamIdLessThanOrEqualTo(Long value) {
            addCriterion("third_team_id <=", value, "thirdTeamId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamIdIn(List<Long> values) {
            addCriterion("third_team_id in", values, "thirdTeamId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamIdNotIn(List<Long> values) {
            addCriterion("third_team_id not in", values, "thirdTeamId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamIdBetween(Long value1, Long value2) {
            addCriterion("third_team_id between", value1, value2, "thirdTeamId");
            return (Criteria) this;
        }

        public Criteria andThirdTeamIdNotBetween(Long value1, Long value2) {
            addCriterion("third_team_id not between", value1, value2, "thirdTeamId");
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

        public Criteria andLogoUrlIsNull() {
            addCriterion("logo_url is null");
            return (Criteria) this;
        }

        public Criteria andLogoUrlIsNotNull() {
            addCriterion("logo_url is not null");
            return (Criteria) this;
        }

        public Criteria andLogoUrlEqualTo(String value) {
            addCriterion("logo_url =", value, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlNotEqualTo(String value) {
            addCriterion("logo_url <>", value, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlGreaterThan(String value) {
            addCriterion("logo_url >", value, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlGreaterThanOrEqualTo(String value) {
            addCriterion("logo_url >=", value, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlLessThan(String value) {
            addCriterion("logo_url <", value, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlLessThanOrEqualTo(String value) {
            addCriterion("logo_url <=", value, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlLike(String value) {
            addCriterion("logo_url like", value, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlNotLike(String value) {
            addCriterion("logo_url not like", value, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlIn(List<String> values) {
            addCriterion("logo_url in", values, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlNotIn(List<String> values) {
            addCriterion("logo_url not in", values, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlBetween(String value1, String value2) {
            addCriterion("logo_url between", value1, value2, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrlNotBetween(String value1, String value2) {
            addCriterion("logo_url not between", value1, value2, "logoUrl");
            return (Criteria) this;
        }

        public Criteria andLogoUrl2IsNull() {
            addCriterion("logo_url2 is null");
            return (Criteria) this;
        }

        public Criteria andLogoUrl2IsNotNull() {
            addCriterion("logo_url2 is not null");
            return (Criteria) this;
        }

        public Criteria andLogoUrl2EqualTo(String value) {
            addCriterion("logo_url2 =", value, "logoUrl2");
            return (Criteria) this;
        }

        public Criteria andLogoUrl2NotEqualTo(String value) {
            addCriterion("logo_url2 <>", value, "logoUrl2");
            return (Criteria) this;
        }

        public Criteria andLogoUrl2GreaterThan(String value) {
            addCriterion("logo_url2 >", value, "logoUrl2");
            return (Criteria) this;
        }

        public Criteria andLogoUrl2GreaterThanOrEqualTo(String value) {
            addCriterion("logo_url2 >=", value, "logoUrl2");
            return (Criteria) this;
        }

        public Criteria andLogoUrl2LessThan(String value) {
            addCriterion("logo_url2 <", value, "logoUrl2");
            return (Criteria) this;
        }

        public Criteria andLogoUrl2LessThanOrEqualTo(String value) {
            addCriterion("logo_url2 <=", value, "logoUrl2");
            return (Criteria) this;
        }

        public Criteria andLogoUrl2Like(String value) {
            addCriterion("logo_url2 like", value, "logoUrl2");
            return (Criteria) this;
        }

        public Criteria andLogoUrl2NotLike(String value) {
            addCriterion("logo_url2 not like", value, "logoUrl2");
            return (Criteria) this;
        }

        public Criteria andLogoUrl2In(List<String> values) {
            addCriterion("logo_url2 in", values, "logoUrl2");
            return (Criteria) this;
        }

        public Criteria andLogoUrl2NotIn(List<String> values) {
            addCriterion("logo_url2 not in", values, "logoUrl2");
            return (Criteria) this;
        }

        public Criteria andLogoUrl2Between(String value1, String value2) {
            addCriterion("logo_url2 between", value1, value2, "logoUrl2");
            return (Criteria) this;
        }

        public Criteria andLogoUrl2NotBetween(String value1, String value2) {
            addCriterion("logo_url2 not between", value1, value2, "logoUrl2");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumbIsNull() {
            addCriterion("logo_url_thumb is null");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumbIsNotNull() {
            addCriterion("logo_url_thumb is not null");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumbEqualTo(String value) {
            addCriterion("logo_url_thumb =", value, "logoUrlThumb");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumbNotEqualTo(String value) {
            addCriterion("logo_url_thumb <>", value, "logoUrlThumb");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumbGreaterThan(String value) {
            addCriterion("logo_url_thumb >", value, "logoUrlThumb");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumbGreaterThanOrEqualTo(String value) {
            addCriterion("logo_url_thumb >=", value, "logoUrlThumb");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumbLessThan(String value) {
            addCriterion("logo_url_thumb <", value, "logoUrlThumb");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumbLessThanOrEqualTo(String value) {
            addCriterion("logo_url_thumb <=", value, "logoUrlThumb");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumbLike(String value) {
            addCriterion("logo_url_thumb like", value, "logoUrlThumb");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumbNotLike(String value) {
            addCriterion("logo_url_thumb not like", value, "logoUrlThumb");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumbIn(List<String> values) {
            addCriterion("logo_url_thumb in", values, "logoUrlThumb");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumbNotIn(List<String> values) {
            addCriterion("logo_url_thumb not in", values, "logoUrlThumb");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumbBetween(String value1, String value2) {
            addCriterion("logo_url_thumb between", value1, value2, "logoUrlThumb");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumbNotBetween(String value1, String value2) {
            addCriterion("logo_url_thumb not between", value1, value2, "logoUrlThumb");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumb2IsNull() {
            addCriterion("logo_url_thumb2 is null");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumb2IsNotNull() {
            addCriterion("logo_url_thumb2 is not null");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumb2EqualTo(String value) {
            addCriterion("logo_url_thumb2 =", value, "logoUrlThumb2");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumb2NotEqualTo(String value) {
            addCriterion("logo_url_thumb2 <>", value, "logoUrlThumb2");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumb2GreaterThan(String value) {
            addCriterion("logo_url_thumb2 >", value, "logoUrlThumb2");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumb2GreaterThanOrEqualTo(String value) {
            addCriterion("logo_url_thumb2 >=", value, "logoUrlThumb2");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumb2LessThan(String value) {
            addCriterion("logo_url_thumb2 <", value, "logoUrlThumb2");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumb2LessThanOrEqualTo(String value) {
            addCriterion("logo_url_thumb2 <=", value, "logoUrlThumb2");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumb2Like(String value) {
            addCriterion("logo_url_thumb2 like", value, "logoUrlThumb2");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumb2NotLike(String value) {
            addCriterion("logo_url_thumb2 not like", value, "logoUrlThumb2");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumb2In(List<String> values) {
            addCriterion("logo_url_thumb2 in", values, "logoUrlThumb2");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumb2NotIn(List<String> values) {
            addCriterion("logo_url_thumb2 not in", values, "logoUrlThumb2");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumb2Between(String value1, String value2) {
            addCriterion("logo_url_thumb2 between", value1, value2, "logoUrlThumb2");
            return (Criteria) this;
        }

        public Criteria andLogoUrlThumb2NotBetween(String value1, String value2) {
            addCriterion("logo_url_thumb2 not between", value1, value2, "logoUrlThumb2");
            return (Criteria) this;
        }

        public Criteria andTeamManageIdIsNull() {
            addCriterion("team_manage_id is null");
            return (Criteria) this;
        }

        public Criteria andTeamManageIdIsNotNull() {
            addCriterion("team_manage_id is not null");
            return (Criteria) this;
        }

        public Criteria andTeamManageIdEqualTo(String value) {
            addCriterion("team_manage_id =", value, "teamManageId");
            return (Criteria) this;
        }

        public Criteria andTeamManageIdNotEqualTo(String value) {
            addCriterion("team_manage_id <>", value, "teamManageId");
            return (Criteria) this;
        }

        public Criteria andTeamManageIdGreaterThan(String value) {
            addCriterion("team_manage_id >", value, "teamManageId");
            return (Criteria) this;
        }

        public Criteria andTeamManageIdGreaterThanOrEqualTo(String value) {
            addCriterion("team_manage_id >=", value, "teamManageId");
            return (Criteria) this;
        }

        public Criteria andTeamManageIdLessThan(String value) {
            addCriterion("team_manage_id <", value, "teamManageId");
            return (Criteria) this;
        }

        public Criteria andTeamManageIdLessThanOrEqualTo(String value) {
            addCriterion("team_manage_id <=", value, "teamManageId");
            return (Criteria) this;
        }

        public Criteria andTeamManageIdLike(String value) {
            addCriterion("team_manage_id like", value, "teamManageId");
            return (Criteria) this;
        }

        public Criteria andTeamManageIdNotLike(String value) {
            addCriterion("team_manage_id not like", value, "teamManageId");
            return (Criteria) this;
        }

        public Criteria andTeamManageIdIn(List<String> values) {
            addCriterion("team_manage_id in", values, "teamManageId");
            return (Criteria) this;
        }

        public Criteria andTeamManageIdNotIn(List<String> values) {
            addCriterion("team_manage_id not in", values, "teamManageId");
            return (Criteria) this;
        }

        public Criteria andTeamManageIdBetween(String value1, String value2) {
            addCriterion("team_manage_id between", value1, value2, "teamManageId");
            return (Criteria) this;
        }

        public Criteria andTeamManageIdNotBetween(String value1, String value2) {
            addCriterion("team_manage_id not between", value1, value2, "teamManageId");
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

        public Criteria andTypeIsNull() {
            addCriterion("type is null");
            return (Criteria) this;
        }

        public Criteria andTypeIsNotNull() {
            addCriterion("type is not null");
            return (Criteria) this;
        }

        public Criteria andTypeEqualTo(Integer value) {
            addCriterion("type =", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotEqualTo(Integer value) {
            addCriterion("type <>", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThan(Integer value) {
            addCriterion("type >", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("type >=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThan(Integer value) {
            addCriterion("type <", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThanOrEqualTo(Integer value) {
            addCriterion("type <=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeIn(List<Integer> values) {
            addCriterion("type in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotIn(List<Integer> values) {
            addCriterion("type not in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeBetween(Integer value1, Integer value2) {
            addCriterion("type between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("type not between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andCoachIsNull() {
            addCriterion("coach is null");
            return (Criteria) this;
        }

        public Criteria andCoachIsNotNull() {
            addCriterion("coach is not null");
            return (Criteria) this;
        }

        public Criteria andCoachEqualTo(String value) {
            addCriterion("coach =", value, "coach");
            return (Criteria) this;
        }

        public Criteria andCoachNotEqualTo(String value) {
            addCriterion("coach <>", value, "coach");
            return (Criteria) this;
        }

        public Criteria andCoachGreaterThan(String value) {
            addCriterion("coach >", value, "coach");
            return (Criteria) this;
        }

        public Criteria andCoachGreaterThanOrEqualTo(String value) {
            addCriterion("coach >=", value, "coach");
            return (Criteria) this;
        }

        public Criteria andCoachLessThan(String value) {
            addCriterion("coach <", value, "coach");
            return (Criteria) this;
        }

        public Criteria andCoachLessThanOrEqualTo(String value) {
            addCriterion("coach <=", value, "coach");
            return (Criteria) this;
        }

        public Criteria andCoachLike(String value) {
            addCriterion("coach like", value, "coach");
            return (Criteria) this;
        }

        public Criteria andCoachNotLike(String value) {
            addCriterion("coach not like", value, "coach");
            return (Criteria) this;
        }

        public Criteria andCoachIn(List<String> values) {
            addCriterion("coach in", values, "coach");
            return (Criteria) this;
        }

        public Criteria andCoachNotIn(List<String> values) {
            addCriterion("coach not in", values, "coach");
            return (Criteria) this;
        }

        public Criteria andCoachBetween(String value1, String value2) {
            addCriterion("coach between", value1, value2, "coach");
            return (Criteria) this;
        }

        public Criteria andCoachNotBetween(String value1, String value2) {
            addCriterion("coach not between", value1, value2, "coach");
            return (Criteria) this;
        }

        public Criteria andStatiumIsNull() {
            addCriterion("statium is null");
            return (Criteria) this;
        }

        public Criteria andStatiumIsNotNull() {
            addCriterion("statium is not null");
            return (Criteria) this;
        }

        public Criteria andStatiumEqualTo(String value) {
            addCriterion("statium =", value, "statium");
            return (Criteria) this;
        }

        public Criteria andStatiumNotEqualTo(String value) {
            addCriterion("statium <>", value, "statium");
            return (Criteria) this;
        }

        public Criteria andStatiumGreaterThan(String value) {
            addCriterion("statium >", value, "statium");
            return (Criteria) this;
        }

        public Criteria andStatiumGreaterThanOrEqualTo(String value) {
            addCriterion("statium >=", value, "statium");
            return (Criteria) this;
        }

        public Criteria andStatiumLessThan(String value) {
            addCriterion("statium <", value, "statium");
            return (Criteria) this;
        }

        public Criteria andStatiumLessThanOrEqualTo(String value) {
            addCriterion("statium <=", value, "statium");
            return (Criteria) this;
        }

        public Criteria andStatiumLike(String value) {
            addCriterion("statium like", value, "statium");
            return (Criteria) this;
        }

        public Criteria andStatiumNotLike(String value) {
            addCriterion("statium not like", value, "statium");
            return (Criteria) this;
        }

        public Criteria andStatiumIn(List<String> values) {
            addCriterion("statium in", values, "statium");
            return (Criteria) this;
        }

        public Criteria andStatiumNotIn(List<String> values) {
            addCriterion("statium not in", values, "statium");
            return (Criteria) this;
        }

        public Criteria andStatiumBetween(String value1, String value2) {
            addCriterion("statium between", value1, value2, "statium");
            return (Criteria) this;
        }

        public Criteria andStatiumNotBetween(String value1, String value2) {
            addCriterion("statium not between", value1, value2, "statium");
            return (Criteria) this;
        }

        public Criteria andIntroductionIsNull() {
            addCriterion("introduction is null");
            return (Criteria) this;
        }

        public Criteria andIntroductionIsNotNull() {
            addCriterion("introduction is not null");
            return (Criteria) this;
        }

        public Criteria andIntroductionEqualTo(String value) {
            addCriterion("introduction =", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionNotEqualTo(String value) {
            addCriterion("introduction <>", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionGreaterThan(String value) {
            addCriterion("introduction >", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionGreaterThanOrEqualTo(String value) {
            addCriterion("introduction >=", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionLessThan(String value) {
            addCriterion("introduction <", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionLessThanOrEqualTo(String value) {
            addCriterion("introduction <=", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionLike(String value) {
            addCriterion("introduction like", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionNotLike(String value) {
            addCriterion("introduction not like", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionIn(List<String> values) {
            addCriterion("introduction in", values, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionNotIn(List<String> values) {
            addCriterion("introduction not in", values, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionBetween(String value1, String value2) {
            addCriterion("introduction between", value1, value2, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionNotBetween(String value1, String value2) {
            addCriterion("introduction not between", value1, value2, "introduction");
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

        public Criteria andBetRadarIdIsNull() {
            addCriterion("bet_radar_id is null");
            return (Criteria) this;
        }

        public Criteria andBetRadarIdIsNotNull() {
            addCriterion("bet_radar_id is not null");
            return (Criteria) this;
        }

        public Criteria andBetRadarIdEqualTo(Integer value) {
            addCriterion("bet_radar_id =", value, "betRadarId");
            return (Criteria) this;
        }

        public Criteria andBetRadarIdNotEqualTo(Integer value) {
            addCriterion("bet_radar_id <>", value, "betRadarId");
            return (Criteria) this;
        }

        public Criteria andBetRadarIdGreaterThan(Integer value) {
            addCriterion("bet_radar_id >", value, "betRadarId");
            return (Criteria) this;
        }

        public Criteria andBetRadarIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("bet_radar_id >=", value, "betRadarId");
            return (Criteria) this;
        }

        public Criteria andBetRadarIdLessThan(Integer value) {
            addCriterion("bet_radar_id <", value, "betRadarId");
            return (Criteria) this;
        }

        public Criteria andBetRadarIdLessThanOrEqualTo(Integer value) {
            addCriterion("bet_radar_id <=", value, "betRadarId");
            return (Criteria) this;
        }

        public Criteria andBetRadarIdIn(List<Integer> values) {
            addCriterion("bet_radar_id in", values, "betRadarId");
            return (Criteria) this;
        }

        public Criteria andBetRadarIdNotIn(List<Integer> values) {
            addCriterion("bet_radar_id not in", values, "betRadarId");
            return (Criteria) this;
        }

        public Criteria andBetRadarIdBetween(Integer value1, Integer value2) {
            addCriterion("bet_radar_id between", value1, value2, "betRadarId");
            return (Criteria) this;
        }

        public Criteria andBetRadarIdNotBetween(Integer value1, Integer value2) {
            addCriterion("bet_radar_id not between", value1, value2, "betRadarId");
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