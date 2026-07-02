package com.panda.merge.dao;

import com.panda.merge.model.FtsMatchRelation;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author aldrich
 * 2023/12/17 19:15
 * 范特西与标准赛事关联关系
 */
@Repository
public interface FtsMatchRelationDao {

    /**
     * 通过主、客队赛事ID查询其与范特西赛事关联信息
     *
     * @param ftsMatchRelation 关联对象
     * @return FtsMatchRelation
     */
    List<FtsMatchRelation> getFtsMatchRelation(FtsMatchRelation ftsMatchRelation);

    /**
     *
     * @param ftsMatchRelation
     * @return
     */
    List<FtsMatchRelation> getFtsMatchByFtsId(FtsMatchRelation ftsMatchRelation);
}
