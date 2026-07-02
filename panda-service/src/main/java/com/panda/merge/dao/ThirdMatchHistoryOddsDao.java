package com.panda.merge.dao;


import com.github.pagehelper.Page;
import com.panda.merge.dto.nonrealttime.query.ThirdMatchInfoDTO;
import com.panda.merge.model.ThirdMatchHistoryOdds;
import org.springframework.stereotype.Repository;

/**
 * 三方赛事百家赔信息
 * @author tell
 * @since  2021年4月22日16:20:48
 */
@Repository
public interface ThirdMatchHistoryOddsDao {


    /**
     * 根据修改时间筛选，分页查询
     * @param   thirdMatchInfoDTO
     * @return  Page<ThirdMatchHistoryOdds>
     */
    Page<ThirdMatchHistoryOdds> getItemPageByModifyTime(ThirdMatchInfoDTO thirdMatchInfoDTO);


}
