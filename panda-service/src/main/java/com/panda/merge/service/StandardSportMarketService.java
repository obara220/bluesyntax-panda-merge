package com.panda.merge.service;

import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarket;
import com.panda.merge.model.StandardSportMarketCategory;
import com.panda.merge.model.ThirdSportMarket;

import java.util.List;
import java.util.Set;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/14 <br>
 * @see com.panda.merge.service <br>
 */
public interface StandardSportMarketService {
    StandardSportMarket getItem(String dataSourceCode, String thirdMarketSourceId, Long standardMatchId);
    StandardSportMarket getItemNoCache(String dataSourceCode, String thirdMarketSourceId, Long standardMatchId);

    StandardSportMarket create(String linkId,StandardMatchInfo standardMatchInfo, ThirdMarketDTO thirdMarketDTO, StandardSportMarketCategory standardSportMarketCategory);

    StandardSportMarket create(String linkId,StandardMatchInfo standardMatchInfo, ThirdSportMarket thirdSportMarket, String scopeId);

    StandardSportMarket updateByPrimaryKeySelective(StandardSportMarket standardSportMarket);

    /**
     * 获取RelationId
     *
     * @param linkId
     * @param standardSportMarket
     * @return
     */
    Long getRelationMarketId(String linkId, StandardSportMarket standardSportMarket);

    /**
     * 保存RelationId
     *
     * @param linkId
     * @param standardSportMarket
     * @return
     */
    Long createRelationMarketId(String linkId, StandardSportMarket standardSportMarket);

    String txCreateRelationMarketId(String thirdMarketSourceId);

    int updateByExampleSelective(Integer status,String dataSource,Long standardMatchInfoId,  List<Integer> statusList,List<Integer> marketTypeList);

    List<StandardSportMarket> getItemList(Long standardMatchInfoId);

    List<StandardSportMarket> getItemByThirdMarketSourceIdsAndDataSourceCode(List<String> strList, String dataSourceCode, Long standardMatchId);

    /**
     * 根据标准赛事  + 数据源 + 类型  查询标准赛事
     * @param standardMatchId
     * @param dataSourceCode
     * @return
     */
    List<StandardSportMarket> getItemByMatchIdAndDataSourceCode(Long standardMatchId, String dataSourceCode, List<Integer> marketTypeList);

    /**
     * 修改玩法状态
     *
     * @param marketCategoryIds 标准玩法ID
     * @param status
     */
    int updateBySelective(Long standardMatchId,Set<Long> marketCategoryIds, Integer status);

    List<StandardSportMarket> getMarketByMatchIdList(List<Long> standardMatchIdList);

    /**
     * 根据统一盘口获取数据
     */
    StandardSportMarket getMarketByRelationId(Long relationMarketId);
}
