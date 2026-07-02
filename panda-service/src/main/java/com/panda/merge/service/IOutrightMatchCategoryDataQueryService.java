package com.panda.merge.service;

import com.panda.merge.bo.OutrightMatchCateGoryInfoBO;
import com.panda.merge.dto.OutrightMatchInfoDTO;
import com.panda.merge.model.StandardOutrightMatchCategory;

import java.util.List;

/**
 * @author : nonhung
 * @project Name : panda-merge
 * @package Name : com.panda.merge.service
 * @description : TODO
 * @date: 2020-10-02 12:06
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
public interface IOutrightMatchCategoryDataQueryService {
    /**
     * @param outrightMatchInfoDTO
     * @return OutrightMatchCateGoryInfoBO
     * @description 冠军玩法下发
     * @author nonhung
     * @date 2020/10/2 11:59
     **/
    List<StandardOutrightMatchCategory> queryOutrihtMatchCategory(OutrightMatchInfoDTO outrightMatchInfoDTO);
}
