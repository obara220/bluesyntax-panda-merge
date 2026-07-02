package com.panda.merge.api;

import com.panda.merge.bo.StandardMatchInfoBO;
import com.panda.merge.bo.StandardMatchOverTimeBO;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.StandardMatchInfoDTO;

import javax.validation.Valid;
import java.util.List;

/**
 * 标准赛事相关查询
 * @author  tell
 * @since   2020年9月10日09:15:50
 * */
public interface IStandardMatchInfoQueryApi {
    /**
     * 分页查询标准赛事数据，目前是写死的条件 【比赛是否结束为（0:未结束，2:临时状态）,比赛开盘标识为（1:开盘，2:关盘，3:封盘）】
     * @param request
     * @return
     */
    Response<PageModel<List<StandardMatchInfoBO>>> queryStandardMatchInfoPage(Request<PageModel<StandardMatchInfoDTO>> request);

    /**
     *根据三方数据源赛事信息查询标准赛事
     * @param request
     * @return
     */
    Response<StandardMatchInfoBO> queryStandardMatchInfoByThirdSourceId(@Valid Request<StandardMatchInfoDTO> request) ;

    /**
     *根据标准赛事ID查询标准赛事
     * @param request
     * @return
     */
    Response<StandardMatchInfoBO> queryStandardMatchInfoById(@Valid Request<Long> request) ;

    /**
     *根据标准赛事ID查询标准赛事结束时间,没有结束则返回0
     * @param request 标准赛事id
     * @return
     */
    Response<StandardMatchOverTimeBO> queryStandardMatchInfoOverTimeById(@Valid Request<Long> request) ;
}
