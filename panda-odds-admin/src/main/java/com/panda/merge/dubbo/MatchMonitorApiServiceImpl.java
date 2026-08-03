package com.panda.merge.dubbo;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

import com.panda.merge.api.IMatchMonitorApi;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.dto.MatchMonitorDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;

import lombok.extern.slf4j.Slf4j;

/**
 * <Description> <br>
 *
 * @author damian<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2022/5/14 <br>
 * @see com.panda.merge.dubbo <br>
 */
@Slf4j
@Component
@DubboService
public class MatchMonitorApiServiceImpl extends BaseProcessor implements IMatchMonitorApi {
		
	@Override
    public Response<Long> getMatchCategoryOfUpdateTime(Request<MatchMonitorDTO> request) {
    	log.info("::{}::getMatchCategoryOfUpdateTime 获取赛事玩法赔率最新更新时间，入参:{}",request.getLinkId(),request.getData());
    	MatchMonitorDTO cate = request.getData();
		if(cate == null || cate.getMatchId() == null || cate.getCategoryId()==null) {
			return Response.failed("标准赛事ID 和 玩法ID 不能为空");
		}
		String key = Constant.REDIS_KEY.RONGHE_MATCH_CATEGORY_ODDS_UPDATETIME+cate.getMatchId();
		Long updateTime = (Long) redisService.hGet(key,cate.getCategoryId().toString());
		Response<Long> response = Response.success();
		if(updateTime != null) {
			response.setData(updateTime);
		}
		response.setDataSourceTime(System.currentTimeMillis());
		log.info("::{}::getMatchCategoryOfUpdateTime 获取赛事玩法赔率最新更新时间，入参:{}" ,request.getLinkId(),response);
		return response;
    }

  
}
