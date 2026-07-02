package com.panda.merge.service;

import com.panda.merge.model.StandardOutrightMatchInfo;

import java.util.List;

/**
 * <Description> 标准冠军赛事信息
 * @author      tell
 * @since       2020年9月10日10:35:50
 */
public interface StandardOutrightMatchInfoService {


    /**
     * 根据 数据源+三方数据源赛事ID 获取三方赛事信息
     * @param dataSourceCode
     * @param thirdMatchSourceId
     * @return
     */
    StandardOutrightMatchInfo getItem(String dataSourceCode, Long thirdMatchSourceId);

    StandardOutrightMatchInfo getItem(Long matchId);

    List<StandardOutrightMatchInfo> getItems(List<Long> matchIds);

    /**
     * 修改标准赛事
     * @param item  对象信息
     * @return StandardOutrightMatchInfo
     * */
    StandardOutrightMatchInfo updateByPrimaryKeySelective(StandardOutrightMatchInfo item);

}
