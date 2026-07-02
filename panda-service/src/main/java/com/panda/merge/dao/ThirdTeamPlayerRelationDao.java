package com.panda.merge.dao;

import com.panda.merge.model.ThirdTeamPlayerRelation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 球队球员关联信息自定义dao
 * @author     tell
 * @since      2020年9月4日12:07:19
 */
@Repository
public interface ThirdTeamPlayerRelationDao {

    /**
     * 批量创建
     * @param   list
     * @return  int
     */
    int insertList(@Param("list") List<ThirdTeamPlayerRelation> list);

    /**
     * 批量更新
     * @param   list
     * @return  int
     */
    int updateList(@Param("list") List<ThirdTeamPlayerRelation> list);
}
