package com.panda.merge.service;

import com.panda.merge.dto.UpdateMarketCategoryDataSourceCodeDTO;
import com.panda.merge.dto.odds.CategoryDataSource;
import com.panda.merge.model.StandardSportMarketSell;

import java.util.List;

/**
 * <Description> <br>
 *
 * @author Top<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2021/2/6 <br>
 * @see com.panda.merge.service <br>
 */
public interface StandardSportMarketSellLogService {

    /**
     * 组装并新增日志记录
     *
     * @param matchId                   标准赛事id
     * @param standardSportMarketSellId 赛事开售表主键id
     * @param logInfo                   日志信息
     * @param logInfoEn                 英文日志信息
     * @param operateType               操作类型
     * @param userId                    用户id
     * @param userName                  用户名称
     * @return num
     * @Author: Top
     * @Date: 2021/2/6 10:37
     */
    Integer AssemblyAndInsertStandardSportMarketSellLog(Long matchId, Long standardSportMarketSellId, String logInfo, String logInfoEn,
                                                        String operateType, String userId, String userName);

    void log(List<UpdateMarketCategoryDataSourceCodeDTO> marketCategoryDataSourceCodeList,
             StandardSportMarketSell standardSportMarketSell,
             String linkId,
             Integer marketType,
             Long operaterId,
             String operaterName);

    void log(CategoryDataSource cds);

}
