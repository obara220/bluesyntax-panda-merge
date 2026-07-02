package com.panda.merge.service;

import com.panda.merge.dto.MatchStatisticsInfoDetailDTO;
import com.panda.merge.model.MatchStatisticsInfoDetail;

import java.util.List;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/9/10 <br>
 * @see com.panda.merge.service <br>
 */
public interface MatchStatisticsInfoDetailService {

    List<MatchStatisticsInfoDetail> getItemList(Long matchStatisticsInfoId);

    MatchStatisticsInfoDetail create(MatchStatisticsInfoDetailDTO matchStatisticsInfoDetailDTO,Long matchStatisticsInfoId);

    MatchStatisticsInfoDetail update(MatchStatisticsInfoDetail matchStatisticsInfoDetail);
}
