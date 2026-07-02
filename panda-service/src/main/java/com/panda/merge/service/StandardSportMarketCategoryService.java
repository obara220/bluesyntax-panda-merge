package com.panda.merge.service;

import com.panda.merge.dto.StandardSportMarketCategoryDetail;
import com.panda.merge.model.StandardSportMarketCategory;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * 运动种类对应标准玩法玩数据
 * @author Aison<br>
 * @createDate 2020/8/14 <br>
 */
public interface StandardSportMarketCategoryService {

    StandardSportMarketCategory getItem(Long marketCategoryId, Long sportId);

    List<StandardSportMarketCategory> getItemsByStandardCategories(List<Pair<Long, Long>> standardCategories);

    List<StandardSportMarketCategory> getItemsByMarketCategoryIds(List<Long> marketCategoryIds);

    /**
     * 根据标准玩法id查询标准玩法投注项
     * @param  marketCategoryId  标准玩法Id
     * @return List<StandardSportMarketCategoryDetail>
     * */
    List<StandardSportMarketCategoryDetail> getItems(Long marketCategoryId);

    /**
     * 根据标准玩法id列表查询标准玩法投注项
     * @param  marketCategoryIds  标准玩法id
     * @return List<StandardSportMarketCategoryDetail>
     * */
    List<StandardSportMarketCategoryDetail> getItems(List<Long> marketCategoryIds);
    
    /**
     * 根据标准玩法id列表查询数据
     * @param marketCategoryId
     * @return
     */
    public List<StandardSportMarketCategory> selectByCategoryId(Long marketCategoryId);
    
    /**
     * 根据标准玩法id查询标准玩法投注项(不差缓存)
     * @param  marketCategoryId  标准玩法Id
     * @return List<StandardSportMarketCategoryDetail>
     * */
    public StandardSportMarketCategory getByCategoryIdAndSportId(Long marketCategoryId, Long sportId);

    /**
     * 清理全量缓存
     * */
    int delRedisByAll();
}
