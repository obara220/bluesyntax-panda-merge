package com.panda.merge.dao;

import com.panda.merge.dto.ThirdMatchTeamRelationDetail;
import com.panda.merge.model.ThirdMatchTeamRelation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 三方赛事球队关联关系自定义dao
 * @author     tell
 * @since      2020年9月4日12:07:19
 */
@Repository
public interface ThirdMatchTeamRelationDao {
    /**
     * 批量创建
     */
    int insertList(@Param("list") List<ThirdMatchTeamRelation> list);

    /**
     * 批量更新
     */
    int updateList(@Param("list") List<ThirdMatchTeamRelation> list);

    /**
     * 根据赛事i获取赛事、球队、球员关系
     * @param matchId
     * @return
     */
    List<ThirdMatchTeamRelationDetail> getItemsByMatchId(@Param("matchId") Long matchId);
}
