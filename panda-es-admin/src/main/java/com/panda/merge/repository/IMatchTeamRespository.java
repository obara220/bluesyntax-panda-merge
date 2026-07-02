package com.panda.merge.repository;


import com.panda.merge.document.MatchTeam;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface IMatchTeamRespository extends ElasticsearchRepository<MatchTeam,String> {

    }

