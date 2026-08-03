package com.panda.merge.dubbo;

import static com.panda.merge.constant.ConstantSystem.PROJECT_ID_NOREALTIME;

import java.util.Map;

import com.panda.merge.constant.ConstantSystem;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.panda.merge.api.IStandardMarketOddsApi;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.StandardMarketOddsDTO;

import lombok.extern.slf4j.Slf4j;

/**
 * 查询投注项赔率接口
 * @author  damian
 * @since   2022年6月05日16:58:13
 * */
@Slf4j
@Component
@DubboService(timeout = 1000000)
public class StandardMarketOddsApiImpl extends BaseProcessor implements IStandardMarketOddsApi {
	@Autowired
    private RedisService redisService;

	@Override
	public Response<Integer> getAoOriginalOddsById(Request<StandardMarketOddsDTO> reqDto) {
		log.info("【getAoOriginalOddsById】【::"+reqDto.getLinkId()+"::】根据投注项ID 查询AO原始赔率,入参：{}",reqDto.getData());
		
		StandardMarketOddsDTO odds = reqDto.getData();
		if(odds == null || odds.getMatchId() == null || odds.getId()==null) {
			return Response.failed("标准赛事ID 和 投注项ID 不能为空");
		}
		String key = Constant.REDIS_KEY.RONGHE_AO_MARKET_ORIGINAL_ODDS+odds.getMatchId();
		Map<String,Integer> data = redisService.hGetAllBasedBucket(key, ConstantSystem.BUCKET_QUANTITY_EIGHT);
		Response<Integer> response = Response.success();
		if(data != null) {
			response.setData(data.get(odds.getId()));
		}
		response.setDataSourceTime(System.currentTimeMillis());
		log.info("【getAoOriginalOddsById】【::"+reqDto.getLinkId()+"::】查询标准赛果信息结束,返回结果 ：{}" ,response);
		return response;
	}

}
