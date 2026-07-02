package com.panda.merge.service;

import com.panda.merge.dto.message.MatchMarketCategoryConfigurationMessage;
import com.panda.merge.model.MatchDataSourceWeight;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/26 <br>
 * @see com.panda.merge.service <br>
 */
public interface MatchDataSourceWeightService {
    MatchDataSourceWeight getItem(Long matchId, Integer marketType);

    MatchDataSourceWeight update(MatchDataSourceWeight matchDataSourceWeight);

    MatchDataSourceWeight save(Integer srWeight, Integer bcWeight, Integer bgWeight, Integer txWeight, Integer rbWeight, Integer pdWeight, Integer piWeight, Integer aoWeight, Integer lsWeight,Integer beWeight,Integer koWeight,Integer btWeight ,Integer oddWeight,Integer n01Weight,Integer n02Weight,Integer f01Weight,Integer n03Weight,Integer l02Weight,Integer tournamentLevel, Long now, MatchMarketCategoryConfigurationMessage categoryConfigutaionInfo, Long operaterId);
}
