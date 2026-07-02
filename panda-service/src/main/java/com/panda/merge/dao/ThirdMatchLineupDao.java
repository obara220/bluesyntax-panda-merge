package com.panda.merge.dao;


import com.github.pagehelper.Page;
import com.panda.merge.dto.nonrealttime.query.ThirdMatchInfoDTO;
import com.panda.merge.model.ThirdMatchLineup;
import org.springframework.stereotype.Repository;

/**
 * 三方赛事阵容数据
 * @author tell
 * @since  2021年3月13日14:33:24
 */
@Repository
public interface ThirdMatchLineupDao {


    /**
     * 根据修改时间筛选，分页查询
     * @param   thirdMatchInfoDTO
     * @return  Page<ThirdMatchLineup>
     */
    Page<ThirdMatchLineup> getItemPageByModifyTime(ThirdMatchInfoDTO thirdMatchInfoDTO);


}
