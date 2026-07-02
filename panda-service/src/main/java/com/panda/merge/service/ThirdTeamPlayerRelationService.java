package com.panda.merge.service;

import com.panda.merge.model.ThirdTeamPlayerRelation;

/**
 * <Description> 球队球员关系信息
 * @author      tell
 * @since       2020年9月6日11:02:59
 */
public interface ThirdTeamPlayerRelationService {

    /**
     * 新增或修改列表
     * @param item  对象信息
     * */
    ThirdTeamPlayerRelation saveOrupdate(ThirdTeamPlayerRelation item);

}
