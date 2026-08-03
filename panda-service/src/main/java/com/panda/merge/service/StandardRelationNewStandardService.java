package com.panda.merge.service;

import com.panda.merge.model.StandardRelationNewStandard;

import java.util.List;

/**
 * 获取测试联赛赛事绑定关系
 */
public interface StandardRelationNewStandardService {
    StandardRelationNewStandard getItem(Long matchId);

    StandardRelationNewStandard getItemByNewId(Long matchId);

    List<StandardRelationNewStandard> listStandardRelationNewStandard(Long sourceStandardId);
}
