package com.panda.merge.service;

import com.panda.merge.model.ConfigurationMatchDataSource;

/**
 * @author :  Riben
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.service
 * @Description :  TODO
 * @Date: 2020-09-17 13:42
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface ConfigurationMatchDataSourceService {

    ConfigurationMatchDataSource save(ConfigurationMatchDataSource dataSource);

    ConfigurationMatchDataSource getRecByMatchIdAndMarketType(Long standardMatchId, Integer marketType);

    ConfigurationMatchDataSource update(ConfigurationMatchDataSource existDataSource);
}
