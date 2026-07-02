package com.panda.merge.api;

import com.panda.merge.dto.ConfigMarketOddsStatusDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;

/**
 * @author damian<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2022/5/14 <br>
 * @see com.panda.merge.api <br>
 */
public interface ConfigMarketOddsStatusApi {
	
	/**
	 * 查询单个投注项配置内容
	 * @param request
	 * @return
	 */
	Response<ConfigMarketOddsStatusDTO> getItemOne(Request<ConfigMarketOddsStatusDTO> request);

	/**
	 * 创建投注项配置内容
	 * @param request
	 * @return
	 */
	Response<ConfigMarketOddsStatusDTO> create(Request<ConfigMarketOddsStatusDTO> request);

	/**
	 * 修改投注项配置内容
	 * @param request
	 * @return
	 */
	Response<ConfigMarketOddsStatusDTO> update(Request<ConfigMarketOddsStatusDTO> request);
	
	/**
	 * 删除投注项配置内容
	 * @param request
	 * @return
	 */
	Response<ConfigMarketOddsStatusDTO> delete(Request<ConfigMarketOddsStatusDTO> request);
}
