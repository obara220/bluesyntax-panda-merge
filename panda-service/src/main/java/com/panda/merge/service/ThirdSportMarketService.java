package com.panda.merge.service;

import com.github.pagehelper.Page;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.dto.ThirdSportMarketDTO;
import com.panda.merge.model.StandardSportMarketCategory;
import com.panda.merge.model.ThirdSportMarket;

import java.util.List;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/15 <br>
 * @see com.panda.merge.service <br>
 */
public interface ThirdSportMarketService {

    ThirdSportMarket getItem(String thirdMarketSourceId) ;

    ThirdSportMarket getItem(String dataSourceCode, String thirdMarketSourceId, Long thirdMatchId) ;

    List<ThirdSportMarket> getItemList(Long thirdMatchSourceId, String dataSourceWeight, Integer marketType, List<Long> marketCategoryIds);

    List<ThirdSportMarket> getItemList(Long thirdMatchSourceId);

    List<ThirdSportMarket> getItemList(Long matchId,int marketType);

    List<ThirdSportMarket> getItemList(Long thirdMatchId, Long standardMatchId);

    ThirdSportMarket create(String linkId, ThirdMarketDTO thirdMarketDTO, Long thirdMatchInfoId, StandardSportMarketCategory standardSportMarketCategory);

    ThirdSportMarket updateByPrimaryKeySelective(ThirdSportMarket thirdSportMarket);

    int updateByExampleSelective(Integer status, String dataSource, Long thirdMatchInfoId, List<Integer> statusList,List<Integer>  marketTypeList ) ;


    /**
     * 根据修改时间筛选，分页查询三方盘口信息
     * @param  page  分页对象信息
     * @return Page<ThirdSportMarket>
     * */
    Page<ThirdSportMarket> getItemPageByModifyTime(PageModel<ThirdSportMarketDTO> page);

    List<ThirdSportMarket> getItemListByStatus(Long thirdMatchSourceId, String dataSourceWeight, Integer marketType, List<Long> marketCategoryIds,Integer status);

    List<ThirdSportMarket> getItem(Long thirdMatchId,String dataSourceCode,Long categoryId,String addtion1);

    List<ThirdSportMarket> getItemList(Long thirdMatchId,String dataSourceCode,Long marketCategoryId);

    Long getRelationMarketId(String linkId, Long standardMatchId, Long categoryId,
                             String addition1,String addition2,String addition3,String addition4,String addition5,
                             Integer marketType,String thirdMarketSourceId);

    List<ThirdSportMarket> getItemList(Long thirdMatchId,String dataSourceCode,Long marketCategoryId,Integer marketType);

    public ThirdSportMarket createReplenish(String linkId,ThirdSportMarket thirdSportMarket);
}
