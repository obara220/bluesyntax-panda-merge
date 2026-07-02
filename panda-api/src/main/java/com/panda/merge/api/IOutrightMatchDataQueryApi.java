package com.panda.merge.api;

import com.panda.merge.bo.OutrightMatchCateGoryInfoBO;
import com.panda.merge.bo.OutrightMatchInfoBO;
import com.panda.merge.dto.OutrightMatchInfoDTO;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;

import java.util.List;

/**
 * @author : nonhung
 * @project Name : panda-merge
 * @package Name : com.panda.merge.api
 * @description : 冠军赛事相关接口
 * @date: 2020-10-01 20:56
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
public interface IOutrightMatchDataQueryApi {

    /**
     * @param reques
     * @return StandardSportTypeBO
     * @description 冠军赛事下发
     * @author nonhung
     * @date 2020/10/1 21:13
     **/
    Response<PageModel<List<OutrightMatchInfoBO>>> queryOutrihtMatch(Request<PageModel<OutrightMatchInfoDTO>> reques);

    /**
     * @param reques
     * @return StandardSportTypeBO
     * @description 冠军赛事玩法下发
     * @author nonhung
     * @date 2020/10/1 21:13
     **/
    Response<List<OutrightMatchCateGoryInfoBO>> queryOutrihtMatchCategory(Request<PageModel<OutrightMatchInfoDTO>> reques);
}
