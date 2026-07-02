package com.panda.merge.service;

import java.util.List;

import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.model.ConfigMarketOddsStatus;
import com.panda.merge.model.StandardMatchInfo;

/**
 * <Description> <br>
 *
 * @author damian<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2022/5/14 <br>
 * @see com.panda.merge.service <br>
 */
public interface ConfigMarketOddsStatusService {

	ConfigMarketOddsStatus getItemOne(Long matchId, Long categoryId, String oddsType);

	ConfigMarketOddsStatus create(ConfigMarketOddsStatus configMarketOddsStatus);

	ConfigMarketOddsStatus update(ConfigMarketOddsStatus configMarketOddsStatus);

	List<ConfigMarketOddsStatus> getItemListByMatchId(Long standardMatchInfoId);
	
	void delete(Long matchId, Long categoryId, String oddsType);
	
	void processConfigOddsValue(String linkId, List<StandardMarketMessage> StandardMarketMessageList, StandardMatchInfo standardMatchInfo);

	void updateStatusByMatchId(String linkId, Long standardMatchId, Integer status);
	
	void updateStatusByMatchIdAndCategoryIds(String linkId, Long standardMatchId, List<Long> categoryIds, Integer status);
}
