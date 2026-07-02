package com.panda.merge.service.impl;

import com.panda.merge.document.MatchTeam;
import com.panda.merge.dto.MatchTeamIDResponseDTO;
import com.panda.merge.dto.MatchTeamRequestDTO;
import com.panda.merge.mapper.StandardSportTeamMapper;
import com.panda.merge.mapper.ThirdSportTeamMapper;
import com.panda.merge.model.StandardSportTeam;
import com.panda.merge.model.StandardSportTeamExample;
import com.panda.merge.model.ThirdSportTeam;
import com.panda.merge.model.ThirdSportTeamExample;
import com.panda.merge.repository.IMatchTeamRespository;
import com.panda.merge.service.IMatchTeamService;
import com.panda.merge.utils.NameUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.*;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.panda.merge.constant.DataSourceConstant.DATA_SOURCE_CODE_MAP;

@Service
@Slf4j
public class MatchTeamServiceImpl implements IMatchTeamService {
    @Autowired
    ThirdSportTeamMapper thirdSportTeamMapper;
    @Autowired
    StandardSportTeamMapper standardSportTeamMapper;
    @Autowired
    IMatchTeamRespository matchTeamRespository;

    @Override
    public List<ThirdSportTeam> getThirdMatchTeamByUpdateTime(Long updateTime) {

        ThirdSportTeamExample example = new ThirdSportTeamExample();
//        example.setOrderByClause("modify_time  limit 2000");
        example.createCriteria().andModifyTimeEqualTo(updateTime);
        List<ThirdSportTeam> list= thirdSportTeamMapper.selectByExample(example);
        if(list.size()>0){
            //查询下es已经有多少，如果漏了一条，则全部插入
            Integer sumNumber =getESTeamNunber(updateTime,0);
            if(sumNumber!=list.size()){
                return list;
            }
        }
        example = new ThirdSportTeamExample();
        example.setOrderByClause("modify_time  limit 5000");
        example.createCriteria().andModifyTimeGreaterThan(updateTime);

         list= thirdSportTeamMapper.selectByExample(example);
        return list;
    }



    @Override
    public List<StandardSportTeam> getStandardMatchTeamByUpdateTime(Long updateTime) {

        StandardSportTeamExample example = new StandardSportTeamExample();
//        example.setOrderByClause("modify_time  limit 2000");
        example.createCriteria().andModifyTimeEqualTo(updateTime);
        List<StandardSportTeam> list= standardSportTeamMapper.selectByExample(example);
        if(list.size()>0){
            Integer sumNumber =getESTeamNunber(updateTime,1);
            if(sumNumber!=list.size()){
                return list;
            }
        }
        example = new StandardSportTeamExample();
        example.setOrderByClause("modify_time  limit 5000");
        example.createCriteria().andModifyTimeGreaterThan(updateTime);
        list= standardSportTeamMapper.selectByExample(example);
        return list;
    }

    private Integer getESTeamNunber(Long updateTime,Integer standard) {
        ValueCountAggregationBuilder mb= AggregationBuilders.count("count_third_team_id").field("third_team_id");
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        MultiMatchQueryBuilder query = QueryBuilders.multiMatchQuery(""+standard,new String[]{"is_standard"});
        MultiMatchQueryBuilder query2 = QueryBuilders.multiMatchQuery(""+updateTime,new String[]{"modify_time"});
        queryBuilder.withQuery(query);
        queryBuilder.withQuery(query2);
        queryBuilder.addAggregation(mb);
        PageRequest p=PageRequest.of(0,10);
        queryBuilder.withPageable(p);
        AggregatedPageImpl<MatchTeam> o= (AggregatedPageImpl) matchTeamRespository.search(queryBuilder.build());
        if(o.getAggregations()!=null&&o.getAggregations().asList().size()>0){
            ParsedValueCount max=(ParsedValueCount) o.getAggregations().asList().get(0);
            return new Double(max.getValue()).intValue();
        }
        return 0;
    }

    @Override
    public MatchTeamIDResponseDTO searchESMatchTeam(MatchTeamRequestDTO requestDTO,List<String> commerceList, boolean queryThirdFlag,  List<String> queryList,Integer queryStandardCount) {
        //1.查询出结果，然后组装成Id
        if(requestDTO.getPage()>30){
            requestDTO.setPage(30+ new Random().nextInt(10));
        }
        List<Long> thirdTeamIdList=new ArrayList<>();
        List<Long> standardTeamIdList=new ArrayList<>();
        MatchTeamIDResponseDTO matchTeamIDResponseDTO =new MatchTeamIDResponseDTO();
        matchTeamIDResponseDTO.setThirdTeamIdList(thirdTeamIdList);
        matchTeamIDResponseDTO.setStandardTeamIdList(standardTeamIdList);
        //根据条件组成查询方法


        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        //builder下有must、should以及mustNot 相当于sql中的and、or以及not

        //1) 分页
        PageRequest p= PageRequest.of(requestDTO.getPage()-1,requestDTO.getSize());
        //2) 必须标准球队或者未被关联的三方球队
        QueryBuilder isStandardOrNotConnect = QueryBuilders.termQuery("reference_id",0);
        builder.must(isStandardOrNotConnect);
        //2.2) 是否查询三方
        if(!queryThirdFlag){
            QueryBuilder isStandard = QueryBuilders.termQuery("is_standard",1);
            builder.must(isStandard);
        }
        //3)名称过滤
        if(StringUtils.isNotEmpty(requestDTO.getName())){
            BoolQueryBuilder nameQuery = QueryBuilders.boolQuery();
            String name = requestDTO.getName();
            List<String>  cnStr =getCnName(requestDTO.getName());

            BoolQueryBuilder cnQuery =QueryBuilders.boolQuery();

            String [] arr = name.split("\\s+");

            if(cnStr.size()>0){
                for (String s : arr) {
                    nameQuery.must( QueryBuilders.matchPhraseQuery("name",s));
                }
//                nameQuery.must( QueryBuilders.matchPhraseQuery("name",name));
//                nameQuery.should( QueryBuilders.matchQuery("name",name));
            }else {
                for (String s : arr) {
                    nameQuery.must( QueryBuilders.matchPhraseQuery("name_spell",s));
                }
            }
//            nameQuery.should( QueryBuilders.termQuery("name",name));


//            nameQuery.should( QueryBuilders.matchQuery("name",name).autoGenerateSynonymsPhraseQuery(false).fuzzyTranspositions(false));
//            nameQuery.should( QueryBuilders.matchQuery("name_spell",name).autoGenerateSynonymsPhraseQuery(false).fuzzyTranspositions(false));
//            builder.should(QueryBuilders.fuzzyQuery("name",requestDTO.getName()))
//                    .should(QueryBuilders.fuzzyQuery("name_spell",requestDTO.getName()));
//            nameQuery=nameQuery.minimumShouldMatch(1);
            builder.must(nameQuery);
        }
        //4)ID过滤
        if(requestDTO.getIds()!=null&&requestDTO.getIds().size()>0){
            QueryBuilder idStandard = QueryBuilders.termsQuery("id",requestDTO.getIds());
            builder.must(idStandard);
        }
        //5)类型过滤
        if(requestDTO.getSportTeamTypes()!=null&&requestDTO.getSportTeamTypes().size()>0){
//            MultiMatchQueryBuilder typeQuery = QueryBuilders.multiMatchQuery(requestDTO.getSportTeamTypes(),new String[]{"type"});
            QueryBuilder typeQuery = QueryBuilders.termsQuery("type",requestDTO.getSportTeamTypes());
            builder.must(typeQuery);
        }
        //6）是否已经关联的标准赛事
        if(requestDTO.getOnlyStandard()!=null&& requestDTO.getOnlyStandard()==1){
            QueryBuilder isStandard = QueryBuilders.termQuery("is_standard",1);
            builder.must(isStandard);
        }
        //7)关联数量判断
        if(queryStandardCount!=null&&queryStandardCount==1){
            QueryBuilder standardCount = QueryBuilders.termQuery("related_data_source_coder_num",1);
            builder.must(standardCount);
        }else {
            QueryBuilder standardCount = QueryBuilders.termQuery("related_data_source_coder_num",1);
            builder.mustNot(standardCount);
        }
        if(queryStandardCount!=null&&queryStandardCount==2){
            QueryBuilder isStandard = QueryBuilders.termQuery("is_standard",1);
            builder.must(isStandard);
            QueryBuilder standardCount = QueryBuilders.termQuery("related_data_source_coder_num",1);
            QueryBuilder standardCount0 = QueryBuilders.termQuery("related_data_source_coder_num",0);
            builder.mustNot(standardCount);
            builder.mustNot(standardCount0);
        }//8) 数据商编码过滤
        if(requestDTO.getDataSourceCode()!=null&&requestDTO.getDataSourceCode().size()!=0){

            BoolQueryBuilder dataSourceQuery = QueryBuilders.boolQuery();
            dataSourceQuery=dataSourceQuery.minimumShouldMatch(1);
            for (String s : requestDTO.getDataSourceCode()) {
                dataSourceQuery.should ( QueryBuilders.termQuery("data_source_code",DATA_SOURCE_CODE_MAP.get(s) ));
            }
            builder.must(dataSourceQuery);
        }
        //9)地区
        if(requestDTO.getRegionId()!=null){
            QueryBuilder dataSourceQuery = QueryBuilders.termQuery("region_id",requestDTO.getRegionId());
            builder.must(dataSourceQuery);
        }
        //10)sportTeamManagerIdStatus 管理ID过滤
        if(requestDTO.getSportTeamManagerIdStatus()!=null&&requestDTO.getSportTeamManagerIdStatus()==1){

            QueryBuilder sportTeamManagerIdStatus = QueryBuilders.termQuery("team_manage_id","");
            builder.mustNot(sportTeamManagerIdStatus);
            builder.mustNot(QueryBuilders.termQuery("team_manage_id","0"));
            builder.must( QueryBuilders.existsQuery("team_manage_id"));
        }
        if(requestDTO.getSportTeamManagerIdStatus()!=null&&requestDTO.getSportTeamManagerIdStatus()==0){
            builder.must(QueryBuilders.termQuery("team_manage_id","0"));
        }
        //11)sportTeamManagerIdStatus 管理ID
        if(StringUtils.isNotEmpty(requestDTO.getTeamManageId())){
            QueryBuilder sportTeamManagerId = QueryBuilders.termQuery("team_manage_id",requestDTO.getTeamManageId());
            builder.must(sportTeamManagerId);
        }
        //12)球种过滤
        if(requestDTO.getSportId()!=null&&requestDTO.getSportId()!=0l){
            QueryBuilder sportId = QueryBuilders.termQuery("sport_id",requestDTO.getSportId());
            builder.must(sportId);
        }
        //13)标准球队数据商编码过滤


        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(builder);
        queryBuilder.withPageable(p);

//        FieldSortBuilder sort = SortBuilders.fieldSort("name_spell").order(SortOrder.DESC);
//        queryBuilder.withSort(sort);
//        .trackTotalHits(true);
        NativeSearchQuery  query= queryBuilder.build();
        query.setTrackTotalHits(true);
        log.info("es查询条件{}",query.getQuery().toString());


        Page<MatchTeam> page = matchTeamRespository.search(query);
        matchTeamIDResponseDTO.setTotal(page.getTotalElements());
        matchTeamIDResponseDTO.setPage(requestDTO.getPage());
        matchTeamIDResponseDTO.setSize(requestDTO.getSize());
        page.get().forEach(it->{
            if(it.getIs_standard()==1){
                standardTeamIdList.add(it.getStandard_team_id());
            }else {
                thirdTeamIdList.add(it.getThird_team_id());
            }
        });
        return matchTeamIDResponseDTO;
    }

    private List<String> getCnName(String name) {
        List<String> list=new ArrayList<>();
        for(int i=0;i < name.length();i++)
        {
            char ch = name.charAt(i);

            if(ch >= '0' && ch<='9') {

            }else if((ch >= 'a' && ch<='z') || (ch>='A' && ch<='Z')){

            }else{

                list.add( ""+ch);

            }

        }
        return list;
    }

}

//    CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria()
//            .and(new Criteria("clusterName").is("app"))
//            .and(new Criteria("ip").is("127.0.0.1"))
//            .and(new Criteria("appType").is("download"))
//            .and(new Criteria("appName").is("appdownload"))
//            .and(new Criteria("fileName").is("appdownload.log"))
//            .and(new Criteria("logLeval").is("info"))
//            .and(new Criteria("produceDateTime").greaterThanEqual(
//                    startDate.getTime()).lessThanEqual(endDate.getTime()))
//            .and(new Criteria("message").contains("haha"))).setPageable(
//            new PageRequest(0, 10)).addSort(
//            new Sort(new Sort.Order(Sort.Direction.DESC, "segEndlineNo")));
//    Page<LogEntity> pages = elasticsearchTemplate.queryForPage(criteriaQuery,
//            LogEntity.class);