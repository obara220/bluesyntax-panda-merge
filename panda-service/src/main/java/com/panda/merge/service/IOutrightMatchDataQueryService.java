package com.panda.merge.service;

import com.panda.merge.bo.OutrightMatchInfoBO;
import com.panda.merge.dto.OutrightMatchInfoDTO;
import com.panda.merge.model.StandardOutrightMatchInfo;

import java.util.List;

/**
 * @author : nonhung
 * @project Name : panda-merge
 * @package Name : com.panda.merge.service
 * @description : TODO
 * @date: 2020-10-02 11:55
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
public interface IOutrightMatchDataQueryService {


    /**
     * @param outrightMatchInfoDTO
     * @return outrightMatchInfoDTO
     * @description 冠军赛事查询
     * @author nonhung
     * @date 2020/10/2 11:58
     **/
    List<StandardOutrightMatchInfo> queryOutrightMatch(OutrightMatchInfoDTO outrightMatchInfoDTO);


}
