package com.panda.merge.dubbo;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.google.common.collect.Maps;
import com.panda.merge.api.IStandardMatchInfoQueryApi;
import com.panda.merge.bo.StandardMatchInfoBO;
import com.panda.merge.bo.StandardMatchOverTimeBO;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dto.*;
import com.panda.merge.mapper.StandardSportMarketSellMapper;
import com.panda.merge.model.DataSource;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.model.StandardSportMarketSellExample;
import com.panda.merge.service.StandardMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.collect;

/**
 * 标准赛事相关查询
 * @author  tell
 * @since   2020年9月10日11:24:27
 * */
@Deprecated
@Slf4j
@Component
@DubboService
public class StandardMatchInoQueryApiImpl  extends BaseProcessor implements IStandardMatchInfoQueryApi {

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    @Autowired
    private StandardSportMarketSellMapper standardSportMarketSellMapper;


    /**
     * 分页查询标准赛事数据，目前是写死的条件 【比赛是否结束为（0:未结束，2:临时状态）,比赛开盘标识为（1:开盘，2:关盘，3:封盘）】
     * @param request
     * @return
     */
    @Override
    public Response<PageModel<List<StandardMatchInfoBO>>> queryStandardMatchInfoPage(Request<PageModel<StandardMatchInfoDTO>> request) {
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_STANDARD_MATCH_INFO_PAGE+"】【::"+request.getLinkId()+"::】分页查询标准赛事数据开始,入参：{}",JSON.toJSONString(request.getData()));
        Page<StandardMatchInfoDetail> resPage = standardMatchInfoService.getStandardMatchInfoPage(request.getData());
        Map<Long, StandardSportMarketSell> standardSportMarketSellMap = Maps.newConcurrentMap();
        if (CollectionUtils.isNotEmpty(resPage)) {
            Set<Long> standardIds = resPage.stream().map(StandardMatchInfoDetail::getId).collect(Collectors.toSet());
            StandardSportMarketSellExample ssmsExample = new StandardSportMarketSellExample();
            ssmsExample.createCriteria().andMatchInfoIdIn(new LinkedList<>(standardIds));
            List<StandardSportMarketSell> standardSportMarketSells = standardSportMarketSellMapper.selectByExample(ssmsExample);
            if (CollectionUtils.isNotEmpty(standardSportMarketSells)) {
                standardSportMarketSellMap = standardSportMarketSells.stream().collect(Collectors.toMap(StandardSportMarketSell::getMatchInfoId, Function.identity()));
            }
        }
        List<StandardMatchInfoBO> resList = new LinkedList<>();
        for (StandardMatchInfoDetail standardMatchInfo: resPage) {
            StandardMatchInfoBO standardMatchInfoBO = new StandardMatchInfoBO();
            BeanUtils.copyProperties(standardMatchInfo, standardMatchInfoBO);
            standardMatchInfoBO.setAuditor(standardSportMarketSellMap.containsKey(standardMatchInfo.getId())?standardSportMarketSellMap.get(standardMatchInfo.getId()).getAuditor():"");
            standardMatchInfoBO.setAuditorId(standardSportMarketSellMap.containsKey(standardMatchInfo.getId())?standardSportMarketSellMap.get(standardMatchInfo.getId()).getAuditorId():"");
            resList.add(standardMatchInfoBO);
        }
        //转换后的分页对象
        PageModel<List<StandardMatchInfoBO>> pageModel = new PageModel(resPage.getPageSize(),resPage.getPageNum());
        pageModel.setTotal(resPage.getTotal());
        pageModel.setData(resList);
        response.setData(pageModel);
        response.setDataSourceTime(System.currentTimeMillis() - beginTime);
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_STANDARD_MATCH_INFO_PAGE+"】【::"+request.getLinkId()+"::】分页查询标准赛事数据结束,返回结果 ：{}" ,JSON.toJSONString(response));
        return response;
    }

    @Override
    public Response<StandardMatchInfoBO> queryStandardMatchInfoByThirdSourceId(Request<StandardMatchInfoDTO> request) {
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        StandardMatchInfoDTO standardMatchInfoDTO = request.getData();
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_STANDARD_MATCH_INFO_BY_THIRD_SOURCE_ID+"】【::"+request.getLinkId()+"::】据三方数据源赛事信息查询标准赛事开始,入参：{}",JSON.toJSONString(request.getData()));
        /** 01 校验dataSourceCode是否合法*/
        DataSource dataSource = simpleValidateDataSourceCode(request, standardMatchInfoDTO.getDataSourceCode());
        /** 02 校验三方运动类型是否合法并返回标准运动类型*/
        Long sportId = validateSportId(dataSource.getCode(), String.valueOf(standardMatchInfoDTO.getThirdSportId()));
        StandardMatchInfoDetail standardMatchInfo = standardMatchInfoService.getStandardMatchInfoByThirdSourceId(dataSource.getCode(), sportId, standardMatchInfoDTO.getThirdMatchSourceId());
        if(null != standardMatchInfo){
            StandardMatchInfoBO standardMatchInfoBO = new StandardMatchInfoBO();
            BeanUtils.copyProperties(standardMatchInfo, standardMatchInfoBO);
            response.setData(standardMatchInfoBO);
        }
        response.setDataSourceTime(System.currentTimeMillis() - beginTime);
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_STANDARD_MATCH_INFO_BY_THIRD_SOURCE_ID+"】【::"+request.getLinkId()+"::】据三方数据源赛事信息查询标准赛事结束,返回结果 ：{}" ,JSON.toJSONString(response));
        return response;
    }

    @Override
    public Response<StandardMatchInfoBO> queryStandardMatchInfoById(Request<Long> request) {
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_STANDARD_MATCH_INFO_BY_ID+"】【::"+request.getLinkId()+"::】根据标准赛事ID查询标准赛事开始,入参：{}",JSON.toJSONString(request.getData()));
        StandardMatchInfoDetail standardMatchInfo = standardMatchInfoService.getDetailItem(request.getData());
        if(null != standardMatchInfo){
            StandardMatchInfoBO standardMatchInfoBO = new StandardMatchInfoBO();
            BeanUtils.copyProperties(standardMatchInfo,standardMatchInfoBO);
            response.setData(standardMatchInfoBO);
        }
        response.setDataSourceTime(System.currentTimeMillis() - beginTime);
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_STANDARD_MATCH_INFO_BY_ID+"】【::"+request.getLinkId()+"::】根据标准赛事ID查询标准赛事结束,返回结果 ：{}" , JSON.toJSONString(response));
        return response;
    }

    @Override
    public Response<StandardMatchOverTimeBO> queryStandardMatchInfoOverTimeById(@Valid Request<Long> request) {
        long beginTime = TimeUtils.millsSecondsEast8ZoneGmt();
        Response response = Response.success();
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_STANDARD_MATCH_INFO_OVER_TIME_BY_ID+"】【::"+request.getLinkId()+"::】根据标准赛事ID查询标准赛事结束时间开始,入参：{}",JSON.toJSONString(request.getData()));
        //查询赛事是否存在
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(request.getData());
        StandardMatchOverTimeBO standardMatchOverTimeBO = new StandardMatchOverTimeBO();
        standardMatchOverTimeBO.setSendTime(beginTime);
        if(null != standardMatchInfo){
            BeanUtils.copyProperties(standardMatchInfo,standardMatchOverTimeBO);
        }
        //redis缓存查询赛事结束时间
        Long overTime = (Long)redisService.get(String.format(MATCH_OVER_TIME, request.getData()));
        standardMatchOverTimeBO.setMatchOverTime(overTime);
        response.setData(standardMatchOverTimeBO);
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_STANDARD_MATCH_INFO_OVER_TIME_BY_ID+"】【::"+request.getLinkId()+"::】根据标准赛事ID查询标准赛事结束时间结束,返回结果 ：{}" , JSON.toJSONString(response));
        return response;
    }
}
