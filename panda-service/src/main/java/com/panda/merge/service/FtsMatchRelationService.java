package com.panda.merge.service;

import com.panda.merge.model.FtsMatchRelation;

import java.util.List;

/**
 * @author aldrich
 * 2023/12/17 18:50
 * 范特西赛事与旧赛事关联关系
 */
public interface FtsMatchRelationService {

    /**
     * 通过主、客队赛事ID查询其与范特西赛事关联信息
     *
     * @param matchId 主、客队赛事ID
     * @return FtsMatchRelation
     */
    List<FtsMatchRelation> getFtsMatchRelation(Long matchId);
}
