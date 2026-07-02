package com.panda.merge.dao;

import com.panda.merge.model.ConfigurationMatchDataSource;

/**
 * @author :  Riben
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.dao
 * @Description :  TODO
 * @Date: 2020-09-17 15:13
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface ConfigurationMatchDataSourceDao {
    void updateByMatchIdAndMarketType(ConfigurationMatchDataSource existDataSource);
}
