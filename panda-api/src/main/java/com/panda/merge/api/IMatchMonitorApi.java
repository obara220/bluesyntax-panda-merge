package com.panda.merge.api;

import com.panda.merge.dto.MatchMonitorDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;

/**
 * @author damian<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2022/6/12 <br>
 * @see com.panda.merge.api <br>
 */
public interface IMatchMonitorApi {
	
	/**
     * 获取赛事第三方玩法赔率最新更新时间
     * @param request
     * @return
     */
    Response<Long> getMatchCategoryOfUpdateTime(Request<MatchMonitorDTO> request);

}
