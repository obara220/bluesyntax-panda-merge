package com.panda.merge.api;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.StandardMarketOddsDTO;


/**
 * 查询投注项赔率接口
 * @author :  damian
 * @Date:     2022年06月05日17:10:45
 */
public interface IStandardMarketOddsApi {

	/**
	 * 根据投注项ID 查询AO原始赔率
	 * @param reqDto
	 * @return
	 */
	public Response<Integer> getAoOriginalOddsById(Request<StandardMarketOddsDTO> reqDto);
}
