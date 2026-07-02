package com.panda.merge.service;


import com.panda.merge.model.StandardOutrightMatchCategory;

/**
 * @Author: Kepa
 * @Date: 2020/9/29 20:17
 */
public interface StandardOutrightMatchCategoryService {

    /**
     * 根据标准赛事ID、玩法ID 获取冠军玩法开售信息
     * @param standardMatchId
     * @param categoryId
     * @return
     */
    StandardOutrightMatchCategory getItem(Long standardMatchId, Long categoryId);

    /**
     * 自动开售更新冠军玩法开售状态，同时清空缓存
     * @param standardMatchId
     * @param categoryId
     */
    void updateStandardOutrightMatchCategory(Long standardMatchId, Long categoryId);

    /**
     * 清空指定冠军玩法缓存
     * @param standardMatchId
     * @param categoryId
     */
    void removeCache(Long standardMatchId, Long categoryId);
}
