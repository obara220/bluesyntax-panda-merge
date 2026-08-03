package com.panda.merge.service;

import com.panda.merge.model.ConfigMarketLevel;

import java.util.List;
import java.util.Set;

public interface ConfigMarketLevelService {
    List<ConfigMarketLevel> getItemLevel(Long sportId,Integer level);

    /**
     * 使用主键id 删除缓存
     * @param idList 主键id集合
     */
    void deleteCacheByIdList(List<Long> idList);
}
