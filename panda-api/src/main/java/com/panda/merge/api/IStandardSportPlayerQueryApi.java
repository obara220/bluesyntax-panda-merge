package com.panda.merge.api;

import com.panda.merge.bo.StandardSportPlayerBO;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.StandardSportPlayerDTO;

import java.util.List;

/**
 * 查询标准球员信息相关
 * @author :  tell
 * @Date:     2020年9月9日16:10:45
 */
public interface IStandardSportPlayerQueryApi {

    /**
     *  根据修改时间分页查询标准球员信息
     * @param request
     * @return
     */
    Response<PageModel<List<StandardSportPlayerBO>>> queryStandardSportPlayerByUpdateTime(Request<PageModel<StandardSportPlayerDTO>> request) ;

}
