package com.panda.merge.service;

import com.panda.merge.dto.ThirdMatchTeamRelationDetail;
import com.panda.merge.model.ThirdMatchTeamRelation;

import java.util.List;
import java.util.Map;

/**
 * <Description> 三方赛事球队关联关系信息
 * @author      tell
 * @since       2020年9月3日15:24:52
 */
public interface ThirdMatchTeamRelationService {
    /**
     * 根据三方赛事ID 获取 三方赛事ID+球队位置和三方赛事球队关联关系的对应信息
     * @param matchId          三方赛事ID
     * @return Map<String,ThirdMatchTeamRelation>
     * */
    Map<String,ThirdMatchTeamRelation> getMatchIdAndPosition2ItemByMatchId(Long matchId);


    /**
     * 根据三方赛事ID列表 获取三方赛事球队关联关系的对应信息
     * @param matchIds          三方赛事ID列表
     * @return List<ThirdMatchTeamRelation>
     * */
    List<ThirdMatchTeamRelation> getItemsByMatchIds(List<Long> matchIds);

    /**
     * 根据三方赛事ID,三方球队ID获取 三方赛事球队关联关系的对应信息
     * @param matchId          三方赛事ID
     * @param matchId          三方球队ID
     * @return ThirdMatchTeamRelation
     * */
    ThirdMatchTeamRelation getItemByMatchIdAndTeamId(Long matchId,Long teamId);

    /**
     * 新增或修改
     * @param item            对象信息
     * @return ThirdMatchTeamRelation
     * */
    ThirdMatchTeamRelation saveOrupdate(ThirdMatchTeamRelation item);

    /**
     * 新增或修改
     * @param list            对象列表信息
     * @return List<ThirdMatchTeamRelation>
     * */
    List<ThirdMatchTeamRelation> saveOrupdateList(List<ThirdMatchTeamRelation> list,String linkId);

    /**
     * 根据赛事i获取赛事、球队、球员关系
     * @param matchId
     * @return
     */
    List<ThirdMatchTeamRelationDetail> getItemsByMatchId(Long matchId);

    /**
     * 根据球队获取赛事、球队、球员关系
     * @param teamId
     * @return
     */
    List<ThirdMatchTeamRelation> listByTeamId(Long teamId);
}
