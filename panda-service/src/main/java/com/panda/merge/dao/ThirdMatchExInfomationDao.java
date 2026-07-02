package com.panda.merge.dao;


import com.github.pagehelper.Page;
import com.panda.merge.dto.nonrealttime.query.ThirdMatchInfoDTO;
import com.panda.merge.model.ThirdMatchExInfomation;
import org.springframework.stereotype.Repository;

/**
 * 三方赛事比赛情报综合资讯数据
 * @author tell
 * @since  2021年4月23日13:46:21
 */
@Repository
public interface ThirdMatchExInfomationDao {


    /**
     * 根据修改时间筛选，分页查询
     * @param   thirdMatchInfoDTO
     * @return  Page<ThirdMatchExInfomation>
     */
    Page<ThirdMatchExInfomation> getItemPageByModifyTime(ThirdMatchInfoDTO thirdMatchInfoDTO);


}
