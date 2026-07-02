package com.panda.merge.service;

import com.panda.merge.common.OddsWrapper;
import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.dto.odds.StandardMarketModification;
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
public interface StandardSportMarketNewService {
    StandardSportMarket getItem(String dataSourceCode, String thirdMarketSourceId, Long standardMatchId);

    List<StandardSportMarket> getItems(List<OddsWrapper<ThirdMarketDTO>> standardSportMarkets);

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

    <T extends StandardMarketModification> Long createRelationMarketId(String linkId,
                                                                       Long matchId,
                                                                       T standardSportMarket);

    List<Object> createRelationMarketIds(List<StandardSportMarket> markets);

    String txCreateRelationMarketId(String thirdMarketSourceId);

    List<Object> txCreateRelationMarketIds(List<String> thirdMarketSourceId);

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

    /**
     * 根据统一盘口获取数据
     */
    StandardSportMarket getMarketByRelationId(Long relationMarketId);

    /**
     * 根据统一盘口ID列表批量查询标准盘口
     */
    List<StandardSportMarket> getItemsByRelationMarketIds(List<Long> relationMarketIds);
}
