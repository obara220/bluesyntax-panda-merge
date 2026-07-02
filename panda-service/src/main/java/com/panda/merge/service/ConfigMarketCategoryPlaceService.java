package com.panda.merge.service;

import com.panda.merge.dto.MarketPlaceDtlDTO;
import com.panda.merge.model.ConfigMarketCategoryPlace;

import java.util.List;
import java.util.Set;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/19 <br>
 * @see com.panda.merge.service <br>
 */
public interface ConfigMarketCategoryPlaceService {

    ConfigMarketCategoryPlace getItem(Long standardMatchInfoId, Long StandardCategoryId,Long childStandardCategoryId, Integer placeNum);

    ConfigMarketCategoryPlace update(ConfigMarketCategoryPlace configMarketCategoryPlace);

    ConfigMarketCategoryPlace create(String linkId, MarketPlaceDtlDTO marketPlaceDtlDTO, Long standardMatchInfoId);

    int delete(Long standardMatchInfoId, Long standardCategoryId);

    int delBatch(Long standardMatchInfoId, Set<Long> delMarketCategoryIdSet, String linkId);

    int delByStandardMatchIds(List<Long> standardMatchIds);

    void insertList(List<ConfigMarketCategoryPlace> configMarketCategoryPlaces,String linkId);

    void updateList(List<ConfigMarketCategoryPlace> configMarketCategoryPlaces,String linkId);

    List<ConfigMarketCategoryPlace> getItemListCache(Long standardMatchInfoId, Long standardCategoryId);

    void cacheConfigMarketPlace(List<ConfigMarketCategoryPlace> configMarketCategoryPlaces,String linkId,Long standardMatchInfoId);

    ConfigMarketCategoryPlace getConfigMarketPlaceCache(Long standardMatchInfoId, Long StandardCategoryId,Long childStandardCategoryId, Integer placeNum);

    String genNewHashKey(Long standardMatchInfoId, Long StandardCategoryId,Long childStandardCategoryId, Integer placeNum);

}
