package com.panda.merge.dao;

import com.panda.merge.model.SportMarketRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author : bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.dao
 * @description : TODO
 * @date: 2020-10-16 16:26
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
public interface SportMarketRelationDao {


    void insertBatch(@Param("sportMarketRelation") List<SportMarketRelation> sportMarketRelation);

    void delBatch(@Param("relationMarketKeys") Set<String> relationMarketKeys);
}
