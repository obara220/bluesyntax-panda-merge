package com.panda.merge.dao;


import com.github.pagehelper.Page;
import com.panda.merge.dto.ThirdMatchInfoDetail;
import com.panda.merge.dto.nonrealttime.query.ThirdMatchInfoDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 三方赛事信息自定义dao
 * @author     tell
 * @since      2020年9月10日09:44:48
 */
@Repository
public interface ThirdMatchInfoDao {

    /**
     * 根据修改时间筛选，分页查询三方赛事信息
     * @param   thirdMatchInfoDTO
     * @return  Page<ThirdMatchInfoDetail>
     */
    Page<ThirdMatchInfoDetail> getItemPageByModifyTime(ThirdMatchInfoDTO thirdMatchInfoDTO);


    /**
     * 根据三方数据源赛事信息获取三方赛事信息（含三方赛事和球队关系）
     * @param   thirdMatchInfoDTO
     * @return  ThirdMatchInfoDetail
     */
    ThirdMatchInfoDetail getItemByThirdMatchSourceId(ThirdMatchInfoDTO thirdMatchInfoDTO);


    /**
     * 查询赛事球队关系脏数据
     * @param   num   本次需要删除的条数
     * @return   List<Long>
     */
    List<ThirdMatchInfoDetail> getThirdRelationByNotInMatchId(Integer num);
}
