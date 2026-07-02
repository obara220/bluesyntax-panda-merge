package com.panda.merge.service;


import java.util.Map;

/**
 * @name: ConfigSportCategoryGroupService
 * @description: 玩法对应赔率计算分组配置
 * @date: 1/13/2025
 **/
public interface ConfigSportCategoryGroupService {

    public Map<Long,Integer> getBySportId(Long sportId);
}
