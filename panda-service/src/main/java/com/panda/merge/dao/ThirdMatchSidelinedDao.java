package com.panda.merge.dao;


import com.github.pagehelper.Page;
import com.panda.merge.dto.nonrealttime.query.ThirdMatchInfoDTO;
import com.panda.merge.model.ThirdMatchSidelined;
import org.springframework.stereotype.Repository;

/**
 * 三方赛事伤停球员信息
 * @author tell
 * @since  2021年4月22日16:20:48
 */
@Repository
public interface ThirdMatchSidelinedDao {


    /**
     * 根据修改时间筛选，分页查询
     * @param   thirdMatchInfoDTO
     * @return  Page<ThirdMatchSidelined>
     */
    Page<ThirdMatchSidelined> getItemPageByModifyTime(ThirdMatchInfoDTO thirdMatchInfoDTO);


}
