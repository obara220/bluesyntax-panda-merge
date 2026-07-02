package com.panda.merge.service;

import com.panda.merge.model.StandardMatchTeamRelation;

import java.util.Map;

/**
 * <Description> 标准赛事球队关联关系信息
 * @author      tell
 * @since       2021年1月31日16:08:45
 */
public interface StandardMatchTeamRelationService {
    /**
     * 根据标准赛事ID 获取 球队位置 和 标准赛事球队关联关系的对应信息
     * @param  standardMatchId          标准赛事ID
     * @return Map<String,StandardMatchTeamRelation>
     * */
    Map<String, StandardMatchTeamRelation> getPosition2ItemByStandardMatchId(Long standardMatchId);

    /**
     * 修改标准赛事球队关系
     * @param item            对象信息
     * @return StandardMatchTeamRelation
     * */
    StandardMatchTeamRelation updateItem(StandardMatchTeamRelation item);


}
