package com.panda.merge.model;

import java.util.ArrayList;
import java.util.List;

public class ThirdSportTournamentExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ThirdSportTournamentExample() {
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

        public Criteria andThirdSeasonSourceIdIsNull() {
            addCriterion("third_season_source_id is null");
            return (Criteria) this;
        }

        public Criteria andThirdSeasonSourceIdIsNotNull() {
            addCriterion("third_season_source_id is not null");
            return (Criteria) this;
        }

        public Criteria andThirdSeasonSourceIdEqualTo(String value) {
            addCriterion("third_season_source_id =", value, "thirdSeasonSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdSeasonSourceIdNotEqualTo(String value) {
            addCriterion("third_season_source_id <>", value, "thirdSeasonSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdSeasonSourceIdGreaterThan(String value) {
            addCriterion("third_season_source_id >", value, "thirdSeasonSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdSeasonSourceIdGreaterThanOrEqualTo(String value) {
            addCriterion("third_season_source_id >=", value, "thirdSeasonSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdSeasonSourceIdLessThan(String value) {
            addCriterion("third_season_source_id <", value, "thirdSeasonSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdSeasonSourceIdLessThanOrEqualTo(String value) {
            addCriterion("third_season_source_id <=", value, "thirdSeasonSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdSeasonSourceIdLike(String value) {
            addCriterion("third_season_source_id like", value, "thirdSeasonSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdSeasonSourceIdNotLike(String value) {
            addCriterion("third_season_source_id not like", value, "thirdSeasonSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdSeasonSourceIdIn(List<String> values) {
            addCriterion("third_season_source_id in", values, "thirdSeasonSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdSeasonSourceIdNotIn(List<String> values) {
            addCriterion("third_season_source_id not in", values, "thirdSeasonSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdSeasonSourceIdBetween(String value1, String value2) {
            addCriterion("third_season_source_id between", value1, value2, "thirdSeasonSourceId");
            return (Criteria) this;
        }

        public Criteria andThirdSeasonSourceIdNotBetween(String value1, String value2) {
            addCriterion("third_season_source_id not between", value1, value2, "thirdSeasonSourceId");
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

        public Criteria andSeasonNameCodeIsNull() {
            addCriterion("season_name_code is null");
            return (Criteria) this;
        }

        public Criteria andSeasonNameCodeIsNotNull() {
            addCriterion("season_name_code is not null");
            return (Criteria) this;
        }

        public Criteria andSeasonNameCodeEqualTo(Long value) {
            addCriterion("season_name_code =", value, "seasonNameCode");
            return (Criteria) this;
        }

        public Criteria andSeasonNameCodeNotEqualTo(Long value) {
            addCriterion("season_name_code <>", value, "seasonNameCode");
            return (Criteria) this;
        }

        public Criteria andSeasonNameCodeGreaterThan(Long value) {
            addCriterion("season_name_code >", value, "seasonNameCode");
            return (Criteria) this;
        }

        public Criteria andSeasonNameCodeGreaterThanOrEqualTo(Long value) {
            addCriterion("season_name_code >=", value, "seasonNameCode");
            return (Criteria) this;
        }

        public Criteria andSeasonNameCodeLessThan(Long value) {
            addCriterion("season_name_code <", value, "seasonNameCode");
            return (Criteria) this;
        }

        public Criteria andSeasonNameCodeLessThanOrEqualTo(Long value) {
            addCriterion("season_name_code <=", value, "seasonNameCode");
            return (Criteria) this;
        }

        public Criteria andSeasonNameCodeIn(List<Long> values) {
            addCriterion("season_name_code in", values, "seasonNameCode");
            return (Criteria) this;
        }

        public Criteria andSeasonNameCodeNotIn(List<Long> values) {
            addCriterion("season_name_code not in", values, "seasonNameCode");
            return (Criteria) this;
        }

        public Criteria andSeasonNameCodeBetween(Long value1, Long value2) {
            addCriterion("season_name_code between", value1, value2, "seasonNameCode");
            return (Criteria) this;
        }

        public Criteria andSeasonNameCodeNotBetween(Long value1, Long value2) {
            addCriterion("season_name_code not between", value1, value2, "seasonNameCode");
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

        public Criteria andCurrentSeasonIdIsNull() {
            addCriterion("current_season_id is null");
            return (Criteria) this;
        }

        public Criteria andCurrentSeasonIdIsNotNull() {
            addCriterion("current_season_id is not null");
            return (Criteria) this;
        }

        public Criteria andCurrentSeasonIdEqualTo(String value) {
            addCriterion("current_season_id =", value, "currentSeasonId");
            return (Criteria) this;
        }

        public Criteria andCurrentSeasonIdNotEqualTo(String value) {
            addCriterion("current_season_id <>", value, "currentSeasonId");
            return (Criteria) this;
        }

        public Criteria andCurrentSeasonIdGreaterThan(String value) {
            addCriterion("current_season_id >", value, "currentSeasonId");
            return (Criteria) this;
        }

        public Criteria andCurrentSeasonIdGreaterThanOrEqualTo(String value) {
            addCriterion("current_season_id >=", value, "currentSeasonId");
            return (Criteria) this;
        }

        public Criteria andCurrentSeasonIdLessThan(String value) {
            addCriterion("current_season_id <", value, "currentSeasonId");
            return (Criteria) this;
        }

        public Criteria andCurrentSeasonIdLessThanOrEqualTo(String value) {
            addCriterion("current_season_id <=", value, "currentSeasonId");
            return (Criteria) this;
        }

        public Criteria andCurrentSeasonIdLike(String value) {
            addCriterion("current_season_id like", value, "currentSeasonId");
            return (Criteria) this;
        }

        public Criteria andCurrentSeasonIdNotLike(String value) {
            addCriterion("current_season_id not like", value, "currentSeasonId");
            return (Criteria) this;
        }

        public Criteria andCurrentSeasonIdIn(List<String> values) {
            addCriterion("current_season_id in", values, "currentSeasonId");
            return (Criteria) this;
        }

        public Criteria andCurrentSeasonIdNotIn(List<String> values) {
            addCriterion("current_season_id not in", values, "currentSeasonId");
            return (Criteria) this;
        }

        public Criteria andCurrentSeasonIdBetween(String value1, String value2) {
            addCriterion("current_season_id between", value1, value2, "currentSeasonId");
            return (Criteria) this;
        }

        public Criteria andCurrentSeasonIdNotBetween(String value1, String value2) {
            addCriterion("current_season_id not between", value1, value2, "currentSeasonId");
            return (Criteria) this;
        }

        public Criteria andFatherTournamentIdIsNull() {
            addCriterion("father_tournament_id is null");
            return (Criteria) this;
        }

        public Criteria andFatherTournamentIdIsNotNull() {
            addCriterion("father_tournament_id is not null");
            return (Criteria) this;
        }

        public Criteria andFatherTournamentIdEqualTo(String value) {
            addCriterion("father_tournament_id =", value, "fatherTournamentId");
            return (Criteria) this;
        }

        public Criteria andFatherTournamentIdNotEqualTo(String value) {
            addCriterion("father_tournament_id <>", value, "fatherTournamentId");
            return (Criteria) this;
        }

        public Criteria andFatherTournamentIdGreaterThan(String value) {
            addCriterion("father_tournament_id >", value, "fatherTournamentId");
            return (Criteria) this;
        }

        public Criteria andFatherTournamentIdGreaterThanOrEqualTo(String value) {
            addCriterion("father_tournament_id >=", value, "fatherTournamentId");
            return (Criteria) this;
        }

        public Criteria andFatherTournamentIdLessThan(String value) {
            addCriterion("father_tournament_id <", value, "fatherTournamentId");
            return (Criteria) this;
        }

        public Criteria andFatherTournamentIdLessThanOrEqualTo(String value) {
            addCriterion("father_tournament_id <=", value, "fatherTournamentId");
            return (Criteria) this;
        }

        public Criteria andFatherTournamentIdLike(String value) {
            addCriterion("father_tournament_id like", value, "fatherTournamentId");
            return (Criteria) this;
        }

        public Criteria andFatherTournamentIdNotLike(String value) {
            addCriterion("father_tournament_id not like", value, "fatherTournamentId");
            return (Criteria) this;
        }

        public Criteria andFatherTournamentIdIn(List<String> values) {
            addCriterion("father_tournament_id in", values, "fatherTournamentId");
            return (Criteria) this;
        }

        public Criteria andFatherTournamentIdNotIn(List<String> values) {
            addCriterion("father_tournament_id not in", values, "fatherTournamentId");
            return (Criteria) this;
        }

        public Criteria andFatherTournamentIdBetween(String value1, String value2) {
            addCriterion("father_tournament_id between", value1, value2, "fatherTournamentId");
            return (Criteria) this;
        }

        public Criteria andFatherTournamentIdNotBetween(String value1, String value2) {
            addCriterion("father_tournament_id not between", value1, value2, "fatherTournamentId");
            return (Criteria) this;
        }

        public Criteria andSimpleFlageIsNull() {
            addCriterion("simple_flage is null");
            return (Criteria) this;
        }

        public Criteria andSimpleFlageIsNotNull() {
            addCriterion("simple_flage is not null");
            return (Criteria) this;
        }

        public Criteria andSimpleFlageEqualTo(Integer value) {
            addCriterion("simple_flage =", value, "simpleFlage");
            return (Criteria) this;
        }

        public Criteria andSimpleFlageNotEqualTo(Integer value) {
            addCriterion("simple_flage <>", value, "simpleFlage");
            return (Criteria) this;
        }

        public Criteria andSimpleFlageGreaterThan(Integer value) {
            addCriterion("simple_flage >", value, "simpleFlage");
            return (Criteria) this;
        }

        public Criteria andSimpleFlageGreaterThanOrEqualTo(Integer value) {
            addCriterion("simple_flage >=", value, "simpleFlage");
            return (Criteria) this;
        }

        public Criteria andSimpleFlageLessThan(Integer value) {
            addCriterion("simple_flage <", value, "simpleFlage");
            return (Criteria) this;
        }

        public Criteria andSimpleFlageLessThanOrEqualTo(Integer value) {
            addCriterion("simple_flage <=", value, "simpleFlage");
            return (Criteria) this;
        }

        public Criteria andSimpleFlageIn(List<Integer> values) {
            addCriterion("simple_flage in", values, "simpleFlage");
            return (Criteria) this;
        }

        public Criteria andSimpleFlageNotIn(List<Integer> values) {
            addCriterion("simple_flage not in", values, "simpleFlage");
            return (Criteria) this;
        }

        public Criteria andSimpleFlageBetween(Integer value1, Integer value2) {
            addCriterion("simple_flage between", value1, value2, "simpleFlage");
            return (Criteria) this;
        }

        public Criteria andSimpleFlageNotBetween(Integer value1, Integer value2) {
            addCriterion("simple_flage not between", value1, value2, "simpleFlage");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundTypeIsNull() {
            addCriterion("current_round_type is null");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundTypeIsNotNull() {
            addCriterion("current_round_type is not null");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundTypeEqualTo(String value) {
            addCriterion("current_round_type =", value, "currentRoundType");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundTypeNotEqualTo(String value) {
            addCriterion("current_round_type <>", value, "currentRoundType");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundTypeGreaterThan(String value) {
            addCriterion("current_round_type >", value, "currentRoundType");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundTypeGreaterThanOrEqualTo(String value) {
            addCriterion("current_round_type >=", value, "currentRoundType");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundTypeLessThan(String value) {
            addCriterion("current_round_type <", value, "currentRoundType");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundTypeLessThanOrEqualTo(String value) {
            addCriterion("current_round_type <=", value, "currentRoundType");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundTypeLike(String value) {
            addCriterion("current_round_type like", value, "currentRoundType");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundTypeNotLike(String value) {
            addCriterion("current_round_type not like", value, "currentRoundType");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundTypeIn(List<String> values) {
            addCriterion("current_round_type in", values, "currentRoundType");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundTypeNotIn(List<String> values) {
            addCriterion("current_round_type not in", values, "currentRoundType");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundTypeBetween(String value1, String value2) {
            addCriterion("current_round_type between", value1, value2, "currentRoundType");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundTypeNotBetween(String value1, String value2) {
            addCriterion("current_round_type not between", value1, value2, "currentRoundType");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNumberIsNull() {
            addCriterion("current_round_number is null");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNumberIsNotNull() {
            addCriterion("current_round_number is not null");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNumberEqualTo(Integer value) {
            addCriterion("current_round_number =", value, "currentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNumberNotEqualTo(Integer value) {
            addCriterion("current_round_number <>", value, "currentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNumberGreaterThan(Integer value) {
            addCriterion("current_round_number >", value, "currentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNumberGreaterThanOrEqualTo(Integer value) {
            addCriterion("current_round_number >=", value, "currentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNumberLessThan(Integer value) {
            addCriterion("current_round_number <", value, "currentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNumberLessThanOrEqualTo(Integer value) {
            addCriterion("current_round_number <=", value, "currentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNumberIn(List<Integer> values) {
            addCriterion("current_round_number in", values, "currentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNumberNotIn(List<Integer> values) {
            addCriterion("current_round_number not in", values, "currentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNumberBetween(Integer value1, Integer value2) {
            addCriterion("current_round_number between", value1, value2, "currentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNumberNotBetween(Integer value1, Integer value2) {
            addCriterion("current_round_number not between", value1, value2, "currentRoundNumber");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNameIsNull() {
            addCriterion("current_round_name is null");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNameIsNotNull() {
            addCriterion("current_round_name is not null");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNameEqualTo(String value) {
            addCriterion("current_round_name =", value, "currentRoundName");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNameNotEqualTo(String value) {
            addCriterion("current_round_name <>", value, "currentRoundName");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNameGreaterThan(String value) {
            addCriterion("current_round_name >", value, "currentRoundName");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNameGreaterThanOrEqualTo(String value) {
            addCriterion("current_round_name >=", value, "currentRoundName");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNameLessThan(String value) {
            addCriterion("current_round_name <", value, "currentRoundName");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNameLessThanOrEqualTo(String value) {
            addCriterion("current_round_name <=", value, "currentRoundName");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNameLike(String value) {
            addCriterion("current_round_name like", value, "currentRoundName");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNameNotLike(String value) {
            addCriterion("current_round_name not like", value, "currentRoundName");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNameIn(List<String> values) {
            addCriterion("current_round_name in", values, "currentRoundName");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNameNotIn(List<String> values) {
            addCriterion("current_round_name not in", values, "currentRoundName");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNameBetween(String value1, String value2) {
            addCriterion("current_round_name between", value1, value2, "currentRoundName");
            return (Criteria) this;
        }

        public Criteria andCurrentRoundNameNotBetween(String value1, String value2) {
            addCriterion("current_round_name not between", value1, value2, "currentRoundName");
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