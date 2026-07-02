package com.panda.merge.service;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.ThirdSportPlayerDetail;
import com.panda.merge.model.ThirdSportTeam;

import java.util.Map;

/**
 * <Description> 球队球员信息
 * @author      tell
 * @since       2020年9月6日11:02:59
 */
public interface ThirdSportPlayerService {

    /**
     * 根据球队信息查询球队下球员集合
     *   @param team      球队参数、
     *   teamId           三方库球队ID（必填）
     *   dataSourceCode   数据来源（非必填）
     *   sportId          运动类型（非必填）
     * */
    Map<String, ThirdSportPlayerDetail> getUnique2ItemByTeamId(ThirdSportTeam team);

    /**
     * 根据数据来源，运动类型，三方数据源球员ID 获取 三方库人员信息
     *   @param dataSourceCode   数据来源
     *   @param sportId          运动类型
     *   @param thirdSourcePlayerId   三方数据源球员ID
     * */
    ThirdSportPlayerDetail getItem(String dataSourceCode, Long sportId, String thirdSourcePlayerId);

    /**
     * 根据三方球员ID获取球员信息
     *   @param  id   三方球员库ID
     * */
    ThirdSportPlayerDetail getItemByPrimaryKey(Long id);

    /**
     * 新增或修改列表
     * @param item          对象信息
     * */
    ThirdSportPlayerDetail saveOrupdate(ThirdSportPlayerDetail item, JSONObject affectedObject);

}
