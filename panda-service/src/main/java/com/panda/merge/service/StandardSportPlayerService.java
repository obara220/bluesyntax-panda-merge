package com.panda.merge.service;

import com.github.pagehelper.Page;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.StandardSportPlayerDTO;
import com.panda.merge.dto.StandardSportPlayerDetail;
import com.panda.merge.model.StandardSportPlayer;

/**
 * 标准球员信息 <br>
 * @author   tell
 * @since    2020年9月9日12:01:57
 */
public interface StandardSportPlayerService {

     /**
      * 根据修改时间分页查询信息标准球员信息
      * @param page   分页对象信息
      * */
     Page<StandardSportPlayerDetail> getPageItemGreaterThanOrModifyTime(PageModel<StandardSportPlayerDTO> page);

     /**
      * 根据三方球员源id获取标准球员对象
      * @param sportId
      * @param thirdSourcePlayIdId
      * @return
      */
     StandardSportPlayer getItem(Long sportId, String thirdSourcePlayIdId);
    /**
     * 根据标准球员ID获取
     * @param id
     * @return
     */
    StandardSportPlayer getItemById(Long id);
}
