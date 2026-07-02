package com.panda.merge.rocketmq.processor;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ThirdMatchSeasonStatisticsDTO;
import com.panda.merge.mapper.ThirdMatchSeasonStatisticsMapper;
import com.panda.merge.model.DataSource;
import com.panda.merge.model.ThirdMatchSeasonStatistics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.Objects;

import static com.panda.merge.constant.ConstantSystem.PROJECT_ID_REALTIME;
import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_SEASON_STATISTICS_API;

/**
 * 当前赛季统计信息
 *
 * @author aldrich
 * @since 2024/10/14
 */
@Slf4j
@Validated
@Component
public class ThirdMatchSeasonStatisticsProcessor extends BaseProcessor {

    @Autowired
    private ThirdMatchSeasonStatisticsMapper thirdMatchSeasonStatisticsMapper;

    public Response processMatchSeasonStatisticsData(@Valid Request<ThirdMatchSeasonStatisticsDTO> request) {
        ThirdMatchSeasonStatisticsDTO data = request.getData();
        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_SEASON_STATISTICS_API+"】【"+request.getDataSourceCode()
                +" ::"+request.getLinkId()+"::】赛季:{}, 当前赛季统计信息数据接收开始", data.getThirdSourceSeasonId());
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        //校验LinkId和缓存中是否重复
        validateLinkId(THIRD_MATCH_SEASON_STATISTICS_API,request);
        //校验数据源编码是否合法
        DataSource dataSource = simpleValidateDataSourceCode(request, data.getDataSourceCode());
        //校验三方数据源运动类型,返回标准运动类型
        Long sportId = validateSportId(dataSource.getCode(), String.valueOf(data.getSportId()));
        ThirdMatchSeasonStatistics thirdMatchSeasonStatistics = new ThirdMatchSeasonStatistics();
        BeanUtil.copyProperties(data, thirdMatchSeasonStatistics);
        thirdMatchSeasonStatistics.setId(data.getThirdSourceSeasonId() + dataSource.getCode() + sportId + data.getTournamentType());
        thirdMatchSeasonStatistics.setSportId(sportId);
        thirdMatchSeasonStatistics.setDataSourceCode(dataSource.getCode());
        thirdMatchSeasonStatistics.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        thirdMatchSeasonStatistics.setEditStatus(Constant.INTEGER_FLAG_ZERO);
        ThirdMatchSeasonStatistics oldData = thirdMatchSeasonStatisticsMapper.selectByPrimaryKey(thirdMatchSeasonStatistics.getId());
        if(Objects.isNull(oldData)){
            thirdMatchSeasonStatistics.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            thirdMatchSeasonStatisticsMapper.insert(thirdMatchSeasonStatistics);
        } else {
            thirdMatchSeasonStatistics.setCreateTime(oldData.getCreateTime());
            thirdMatchSeasonStatisticsMapper.updateByPrimaryKeySelective(thirdMatchSeasonStatistics);
        }
        response.setDataSourceTime(System.currentTimeMillis() - beginTime);
        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_SEASON_STATISTICS_API+"】【"+request.getDataSourceCode()
                +" ::"+request.getLinkId()+"::】当前赛季统计信息数据接收结束,返回结果：{}" , JSON.toJSONString(response));
        return response;
    }
}
