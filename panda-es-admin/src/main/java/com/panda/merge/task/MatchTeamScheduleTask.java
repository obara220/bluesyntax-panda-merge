package com.panda.merge.task;


import com.panda.merge.config.RedisService;
import com.panda.merge.document.MatchTeam;
import com.panda.merge.model.StandardSportTeam;
import com.panda.merge.model.ThirdSportTeam;
import com.panda.merge.repository.IMatchTeamRespository;
import com.panda.merge.service.IMatchTeamService;
import com.panda.merge.utils.NameUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedMax;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;

import static com.panda.merge.constant.DataSourceConstant.DATA_SOURCE_CODE_MAP;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
@Slf4j
public class MatchTeamScheduleTask {

    @Autowired
    RedisService redisService;
    static Boolean isGo=false;

    @Autowired
    IMatchTeamRespository matchTeamRespository;
    @Autowired
    IMatchTeamService matchTeamService;
        //3.添加定时任务 1秒
        @Scheduled(cron = "0/1 * * * * ?")
        //@Scheduled(fixedRate=5000)
        private void configureTasks() {
            synchronized (this){
                if(!isGo){
                    isGo=true;
                }else {
                    return;
                }
            }
            Long updateTime =0l;
            MaxAggregationBuilder mb= AggregationBuilders.max("max_modify_time").field("modify_time");
            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
            MultiMatchQueryBuilder query = QueryBuilders.multiMatchQuery("0",new String[]{"is_standard"});
            queryBuilder.withQuery(query);
            queryBuilder.addAggregation(mb);
            PageRequest p=PageRequest.of(0,10);
            queryBuilder.withPageable(p);
            AggregatedPageImpl<MatchTeam> o= (AggregatedPageImpl) matchTeamRespository.search(queryBuilder.build());
            if(o.getAggregations()!=null&&o.getAggregations().asList().size()>0){
                ParsedMax max=(ParsedMax) o.getAggregations().asList().get(0);
                updateTime= new Double(max.getValue()).longValue();
            }
            //1.查询30个标准球队未更新的 根据时间条件排序 分页
            List<ThirdSportTeam> thirdSportTeamList =matchTeamService.getThirdMatchTeamByUpdateTime(updateTime);
            log.info("需要更新ES三方球队："+thirdSportTeamList.size());
            updateTime=0l;
            //3.查询30个三方球队未更新的 根据时间条件排序 分页
            query = QueryBuilders.multiMatchQuery("1",new String[]{"is_standard"});
            queryBuilder= new NativeSearchQueryBuilder();
            queryBuilder.withQuery(query);
            queryBuilder.addAggregation(mb);
            queryBuilder.withPageable(p);
            o= (AggregatedPageImpl) matchTeamRespository.search(queryBuilder.build());
            if(o.getAggregations()!=null&&o.getAggregations().asList().size()>0){
                ParsedMax max=(ParsedMax) o.getAggregations().asList().get(0);
                updateTime= new Double(max.getValue()).longValue();
            }
            List<StandardSportTeam> standardSportTeamList =matchTeamService.getStandardMatchTeamByUpdateTime(updateTime);
            log.info("需要更新ES标准球队："+standardSportTeamList.size());
            //2.每个球队for循环入库更新ES索引
            List<MatchTeam> list= new ArrayList<>();
            for (ThirdSportTeam thirdSportTeam : thirdSportTeamList) {
                list.add(saveThirdMatchTeam(thirdSportTeam));
            }
            //4.每个球队for循环入库更新ES索引
            for (StandardSportTeam standardSportTeam : standardSportTeamList) {
                list.add( saveStandardMatchTeam(standardSportTeam));
            }
            matchTeamRespository.saveAll(list);
            isGo=false;
            log.info("更新ES球队索引成功!");
        }

    private MatchTeam saveThirdMatchTeam(ThirdSportTeam thirdSportTeam) {
        MatchTeam matchTeam =new MatchTeam();
        matchTeam.setId(thirdSportTeam.getId()+"_0");
        matchTeam.setCreate_time(thirdSportTeam.getCreateTime());
        matchTeam.setModify_time(thirdSportTeam.getModifyTime());

        matchTeam.setCountry_id(thirdSportTeam.getCountryId());
        matchTeam.setData_source_code(DATA_SOURCE_CODE_MAP.get( thirdSportTeam.getDataSourceCode()));
        matchTeam.setReference_id(thirdSportTeam.getReferenceId());
        matchTeam.setRegion_id(thirdSportTeam.getRegionId());

        matchTeam.setName(NameUtils.transfer(thirdSportTeam.getName()));

        matchTeam.setTeam_manage_id("0");

        matchTeam.setName_spell(NameUtils.transfer(thirdSportTeam.getNameSpell()));

        matchTeam.setSport_id(thirdSportTeam.getSportId());
        matchTeam.setIs_standard(0);
        matchTeam.setThird_team_id(thirdSportTeam.getId());
        matchTeam.setType(thirdSportTeam.getType());
        matchTeam.setRelated_data_source_coder_num(0);
        matchTeam.setStandard_team_id(0l);
        return matchTeam;
//        log.info("ES保存索引 matchteam:"+matchTeam);
    }

    private MatchTeam saveStandardMatchTeam(StandardSportTeam standardSportTeam) {
        MatchTeam matchTeam =new MatchTeam();
        matchTeam.setId(standardSportTeam.getId()+"_1");
        matchTeam.setCreate_time(standardSportTeam.getCreateTime());
        matchTeam.setModify_time(standardSportTeam.getModifyTime());

        matchTeam.setCountry_id(standardSportTeam.getCountryId());
        matchTeam.setData_source_code(DATA_SOURCE_CODE_MAP.get( standardSportTeam.getDataSourceCode()));
        matchTeam.setReference_id(0L);
        matchTeam.setRegion_id(standardSportTeam.getRegionId());
        matchTeam.setName(NameUtils.transfer(standardSportTeam.getName()));
        matchTeam.setTeam_manage_id(standardSportTeam.getTeamManageId());
        matchTeam.setName_spell(NameUtils.transfer(standardSportTeam.getNameSpell()));
        matchTeam.setSport_id(standardSportTeam.getSportId());
        matchTeam.setIs_standard(1);
        matchTeam.setThird_team_id(standardSportTeam.getThirdTeamId());
        matchTeam.setType(standardSportTeam.getType());
        matchTeam.setRelated_data_source_coder_num(standardSportTeam.getRelatedDataSourceCoderNum());
        matchTeam.setStandard_team_id(standardSportTeam.getId());

        return matchTeam;
//        log.info("ES保存索引 matchteam:"+matchTeam);
    }

}
