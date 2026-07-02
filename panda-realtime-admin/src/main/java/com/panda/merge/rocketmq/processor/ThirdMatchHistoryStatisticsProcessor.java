package com.panda.merge.rocketmq.processor;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.utils.EntityEqualsUtils;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ThirdMatchHistoryStatisticsDTO;
import com.panda.merge.mapper.ThirdMatchHistoryStatisticsMapper;
import com.panda.merge.model.DataSource;
import com.panda.merge.model.ThirdMatchHistoryStatistics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

import static com.panda.merge.constant.ConstantSystem.PROJECT_ID_REALTIME;
import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_HISTORY_STATISTICS_API;

/**
 * 三方赛事历史统计信息
 * @author  tell
 * @since   2021年2月9日15:52:11
 */
@Slf4j
@Validated
@Component
public class ThirdMatchHistoryStatisticsProcessor extends BaseProcessor {

    @Autowired
    private ThirdMatchHistoryStatisticsMapper thirdMatchHistoryStatisticsMapper;

    public Response processMatchHistoryStatisticsData(@Valid Request<ThirdMatchHistoryStatisticsDTO> request) {
        ThirdMatchHistoryStatisticsDTO dtoItem = request.getData();
        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_HISTORY_STATISTICS_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】赛事:{}历史统计数据接收开始",dtoItem.getThirdMatchSourceId());
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        //校验LinkId和缓存中是否重复
        validateLinkId(THIRD_MATCH_HISTORY_STATISTICS_API,request);
        /** 01 校验dataSourceCode是否合法*/
        DataSource dataSource = simpleValidateDataSourceCode(request, dtoItem.getDataSourceCode());
        /** 02 校验三方数据源运动类型,返回标准运动类型*/
        Long sportId = validateSportId(dataSource.getCode(), String.valueOf(dtoItem.getSportId()));
        //本次操作对象
        ThirdMatchHistoryStatistics upItem = new ThirdMatchHistoryStatistics();
        BeanUtil.copyProperties(dtoItem, upItem);
        upItem.setId(dataSource.getId()+""+dtoItem.getThirdMatchSourceId());
        upItem.setSportId(sportId);
        upItem.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        upItem.setEditStatus(Constant.INTEGER_FLAG_ZERO);
        //获取库中信息
        ThirdMatchHistoryStatistics oldItem = thirdMatchHistoryStatisticsMapper.selectByPrimaryKey(upItem.getId());
        if(null == oldItem){
            upItem.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            thirdMatchHistoryStatisticsMapper.insertSelective(upItem);
        }else{
            upItem.setCreateTime(oldItem.getCreateTime());
            thirdMatchHistoryStatisticsMapper.updateByPrimaryKeySelective(upItem);
        }
        response.setDataSourceTime(System.currentTimeMillis() - beginTime);
        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_HISTORY_STATISTICS_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】赛事历史统计数据接收结束,返回结果 ：{}" , JSON.toJSONString(response));
        return response;
    }


}

