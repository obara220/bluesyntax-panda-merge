package com.panda.merge.dubbo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.panda.merge.api.ConfigMarketOddsStatusApi;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.dto.ConfigMarketOddsStatusDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.message.StandardMarketOddsDataMessage;
import com.panda.merge.model.ConfigMarketOddsStatus;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.rocketmq.processor.ThirdMatchMarketProcessor;
import com.panda.merge.service.ConfigMarketOddsStatusService;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportMarketSellService;

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
public class ConfigMarketOddsStatusApiServiceImpl extends BaseProcessor implements ConfigMarketOddsStatusApi {
	
	@Autowired
    public ConfigMarketOddsStatusService configMarketOddsStatusService;
    @Lazy
	@Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;
	@Autowired
    private StandardMatchInfoService standardMatchInfoService;
	@Autowired
    private StandardSportMarketSellService standardSportMarketSellService;
	
	@Override
	public Response<ConfigMarketOddsStatusDTO> getItemOne(Request<ConfigMarketOddsStatusDTO> request) {
		ConfigMarketOddsStatusDTO dto = request.getData();
		if(dto == null) {
			log.info("::{}::ConfigMarketOddsStatusApiServiceImpl.getItemOne,输入参数为空", request.getLinkId());
            return Response.failed("输入参数为空!");
		}
		if(dto.getStandardMatchInfoId() == null || dto.getStandardCategoryId() == null || dto.getOddsType() == null) {
			log.info("::{}::ConfigMarketOddsStatusApiServiceImpl.getItemOne,主要参数存在空值. matchId:{}，categoryId:{}，oddsType:{}", request.getLinkId(),
					dto.getStandardMatchInfoId(), dto.getStandardCategoryId(), dto.getOddsType());
            return Response.failed("主要参数存在空值!");
		}
		ConfigMarketOddsStatus configMarketOddsStatus = configMarketOddsStatusService.getItemOne(dto.getStandardMatchInfoId(), dto.getStandardCategoryId(), dto.getOddsType());
		if(configMarketOddsStatus == null) {
			log.info("::{}::ConfigMarketOddsStatusApiServiceImpl.getItemOne,投注项配置不存在，matchId:{}，categoryId:{}，oddsType:{}", request.getLinkId(), 
					dto.getStandardMatchInfoId(), dto.getStandardCategoryId(), dto.getOddsType());
            return Response.failed("投注项配置不存在!");
		}
		BeanUtils.copyProperties(configMarketOddsStatus, dto);
		Response<ConfigMarketOddsStatusDTO> res = Response.success();
		res.setData(dto);
		return res;
	}

	@Override
	public Response<ConfigMarketOddsStatusDTO> create(Request<ConfigMarketOddsStatusDTO> request) {
		ConfigMarketOddsStatusDTO dto = request.getData();
		if(dto == null) {
			log.info("::{}::ConfigMarketOddsStatusApiServiceImpl.create,输入参数为空", request.getLinkId());
            return Response.failed("输入参数为空!");
		}
		//查询标准赛事是否存在
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(dto.getStandardMatchInfoId());
        if (standardMatchInfo == null) {
            log.info("::{}::ConfigMarketOddsStatusApiServiceImpl.create,标准赛事未找到，标准赛事id:{}", request.getLinkId(), dto.getStandardMatchInfoId());
            return Response.failed("标准赛事未找到");
        }
        if(dto.getStandardMatchInfoId() == null || dto.getStandardCategoryId() == null || dto.getOddsType() == null) {
			log.info("::{}::ConfigMarketOddsStatusApiServiceImpl.getItemOne,主要参数存在空值. matchId:{}，categoryId:{}，oddsType:{}", request.getLinkId(),
					dto.getStandardMatchInfoId(), dto.getStandardCategoryId(), dto.getOddsType());
            return Response.failed("主要参数存在空值!");
		}
        //查询赛事的开售信息
        StandardSportMarketSell standardSportMarketSell =
                standardSportMarketSellService.getItem(standardMatchInfo.getId());
        //赛事未开售，赔率不下发
        if (standardSportMarketSell == null) {
            log.info("::{}::ConfigMarketOddsStatusApiServiceImpl.create ,赛事未开售赔率不下发，标准赛事id：{}", request.getLinkId(),
            		dto.getStandardMatchInfoId());
            return Response.failed("赛事未开售赔率不下发");
        }
		log.info("::{}::ConfigMarketOddsStatusApiServiceImpl.create,输入参数内容：{}", request.getLinkId(),dto);
		return save(request, standardMatchInfo, standardSportMarketSell);
	}

	@Override
	public Response<ConfigMarketOddsStatusDTO> update(Request<ConfigMarketOddsStatusDTO> request) {
		ConfigMarketOddsStatusDTO dto = request.getData();
		if(dto == null) {
			log.info("::{}::ConfigMarketOddsStatusApiServiceImpl.update,输入参数为空", request.getLinkId());
            return Response.failed("输入参数为空!");
		}
		//查询标准赛事是否存在
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(dto.getStandardMatchInfoId());
        if (standardMatchInfo == null) {
            log.info("::{}::ConfigMarketOddsStatusApiServiceImpl.create,标准赛事未找到，标准赛事id:{}", request.getLinkId(), dto.getStandardMatchInfoId());
            return Response.failed("标准赛事未找到");
        }
        if(dto.getStandardMatchInfoId() == null || dto.getStandardCategoryId() == null || dto.getOddsType() == null) {
			log.info("::{}::ConfigMarketOddsStatusApiServiceImpl.getItemOne,主要参数存在空值. matchId:{}，categoryId:{}，oddsType:{}", request.getLinkId(),
					dto.getStandardMatchInfoId(), dto.getStandardCategoryId(), dto.getOddsType());
            return Response.failed("主要参数存在空值!");
		}
        //查询赛事的开售信息
        StandardSportMarketSell standardSportMarketSell =
                standardSportMarketSellService.getItem(standardMatchInfo.getId());
        //赛事未开售，赔率不下发
        if (standardSportMarketSell == null) {
            log.info("::{}::ConfigMarketOddsStatusApiServiceImpl.create ,赛事未开售赔率不下发，标准赛事id：{}", request.getLinkId(),
            		dto.getStandardMatchInfoId());
            return Response.failed("赛事未开售赔率不下发");
        }
		log.info("::{}::ConfigMarketOddsStatusApiServiceImpl.update,输入参数内容：{}", request.getLinkId(),dto);
		return save(request, standardMatchInfo, standardSportMarketSell);
	}
	
	private Response<ConfigMarketOddsStatusDTO> save(Request<ConfigMarketOddsStatusDTO> request, StandardMatchInfo standardMatchInfo, StandardSportMarketSell standardSportMarketSell) {
		ConfigMarketOddsStatusDTO dto = request.getData();
		ConfigMarketOddsStatus obj = new ConfigMarketOddsStatus();
		BeanUtils.copyProperties(dto, obj);
		obj.setLinkId(request.getLinkId());
		ConfigMarketOddsStatus configMarketOddsStatus = configMarketOddsStatusService.getItemOne(dto.getStandardMatchInfoId(), dto.getStandardCategoryId(), dto.getOddsType());
		try {
			if(configMarketOddsStatus != null) {
				log.info("::{}::ConfigMarketOddsStatusApiServiceImpl.save,执行update", request.getLinkId());
				configMarketOddsStatusService.update(obj);
			}else {
				log.info("::{}::ConfigMarketOddsStatusApiServiceImpl.save,执行create", request.getLinkId());
				configMarketOddsStatusService.create(obj);
			}
			//更新赔率
			updateOdds(request, standardMatchInfo, standardSportMarketSell);
			return Response.success();
		}catch (Exception e) {
			log.info("::{}::ConfigMarketOddsStatusApiServiceImpl.update,数据修改失败：{}", request.getLinkId(),e);
		}
		return Response.failed("数据修改失败,不执行赔率下发");
	}
	
	/**
	 * 更新赔率
	 * @param request
	 * @param standardMatchInfo
	 * @param standardSportMarketSell
	 */
	private void updateOdds(Request<ConfigMarketOddsStatusDTO> request, StandardMatchInfo standardMatchInfo, StandardSportMarketSell standardSportMarketSell) {
		log.info("::{}::ConfigMarketOddsStatusApiServiceImpl.updateOdds,更新赔率：", request.getLinkId());
		ConfigMarketOddsStatusDTO dto = request.getData();
		Set<Long> marketCategoryIdSet = new HashSet<Long>();
		marketCategoryIdSet.add(dto.getStandardCategoryId());
		//下发最新赔率
        //获取缓存中的所有盘口（赛前数据商和滚球数据商）
        Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap =
        		thirdMatchMarketProcessor.getStringStandardMarketDataMessageMap(marketCategoryIdSet,request.getLinkId(), standardMatchInfo,
                        standardSportMarketSell);
		Iterator<String> keys = stringStandardMarketDataMessageMap.keySet().iterator();
		StandardMarketDataMessage s = null;
		while(keys.hasNext()) {
			s = stringStandardMarketDataMessageMap.get(keys.next());
			List<StandardMarketOddsDataMessage> marketOddsList = s.getMarketOddsList();
			if(!CollectionUtils.isEmpty(marketOddsList)) {
				List<StandardMarketOddsDataMessage> oddsList = marketOddsList.stream().filter(w -> w.getRelationMarketOddsId().equals(dto.getId())).collect(Collectors.toList());
				if(!CollectionUtils.isEmpty(oddsList)) {
					log.info("::{}::ConfigMarketOddsStatusApiServiceImpl.updateOdds,更新投注项状态用于下发：需要调整状态的投注项{}", request.getLinkId(),oddsList);
					oddsList.get(0).setStatus(dto.getStatus());
					break;
				}
			}
		}
		log.info("::{}::ConfigMarketOddsStatusApiServiceImpl.updateOdds,更新赔率：玩法集合{},盘口集合{}", request.getLinkId(),marketCategoryIdSet,stringStandardMarketDataMessageMap);
		thirdMatchMarketProcessor.processOddsByAll(request.getLinkId(),request.getOddsSource(),request.getOperaterId(), standardMatchInfo, marketCategoryIdSet, stringStandardMarketDataMessageMap,
				TimeUtils.millsSecondsEast8ZoneGmt(),standardSportMarketSell, new HashMap<>());
	}

	@Override
	public Response<ConfigMarketOddsStatusDTO> delete(Request<ConfigMarketOddsStatusDTO> request) {
		ConfigMarketOddsStatusDTO dto = request.getData();
		if(dto == null) {
			log.info("::{}::ConfigMarketOddsStatusApiServiceImpl.delete,输入参数为空", request.getLinkId());
            return Response.failed("输入参数为空!");
		}
		if(dto.getStandardMatchInfoId() == null || dto.getStandardCategoryId() == null || dto.getOddsType() == null) {
			log.info("::{}::ConfigMarketOddsStatusApiServiceImpl.delete,主要参数存在空值. matchId:{}，categoryId:{}，oddsType:{}", request.getLinkId(),
					dto.getStandardMatchInfoId(), dto.getStandardCategoryId(), dto.getOddsType());
            return Response.failed("主要参数存在空值!");
		}
		configMarketOddsStatusService.delete(dto.getStandardMatchInfoId(), dto.getStandardCategoryId(), dto.getOddsType());
		return Response.success();
	}

  
}
