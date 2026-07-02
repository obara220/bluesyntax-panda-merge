package com.panda.merge.service;

import com.panda.merge.common.OddsWrapper;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchMarketDTO;
import com.panda.merge.model.ThirdOutrightMatchInfo;

import java.util.List;
import java.util.Map;

/**
 * <Description> 三方冠军赛事信息
 * @author      tell
 * @since       2020年9月10日10:35:50
 */
public interface ThirdOutrightMatchInfoService {


    /**
     * 根据 数据源+三方数据源赛事ID 获取三方赛事信息
     * @param dataSourceCode
     * @param thirdMatchSourceId
     * @return
     */
    ThirdOutrightMatchInfo getItem(String dataSourceCode, String thirdOutrightSourceId);

    /**
     * 根据 数据源+三方数据源赛事ID 批量获取三方赛事信息
     * @param matchMarketDto
     * @return
     */
    List<ThirdOutrightMatchInfo> getItems(List<OddsWrapper<ThirdMatchMarketDTO>> matchMarketDto);

    /**
     * 根据 标准赛事ID 获取三方赛事信息
     * @param standardMatchId
     * @param dataSourcecode
     * @return
     */
    ThirdOutrightMatchInfo getItemByMatchId(Long standardMatchId, String dataSourcecode);

    /**
     * 新增或修改
     * @param item  对象信息
     * @return ThirdOutrightMatchInfo
     * */
    ThirdOutrightMatchInfo saveOrupdate(ThirdOutrightMatchInfo item);

    /**
     * 根据 三方赛事ID 获取三方赛事信息
     * @param thirdMatchId
     * @param dataSourceCode
     * @return
     */
    ThirdOutrightMatchInfo getItem(Long thirdMatchId, String dataSourceCode);

}
