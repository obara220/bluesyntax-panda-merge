package com.panda.merge.rocketmq.processor;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ThirdMatchFrontStatisticsDTO;
import com.panda.merge.mapper.ThirdMatchFrontStatisticsMapper;
import com.panda.merge.model.DataSource;
import com.panda.merge.model.ThirdMatchFrontStatistics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.Objects;

import static com.panda.merge.constant.ConstantSystem.PROJECT_ID_REALTIME;
import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_FRONT_STATISTICS_API;

/**
 * 正面交手统计信息
 *
 * @author aldrich
 * @since 2024/10/16
 */
@Slf4j
@Validated
@Component
public class ThirdMatchFrontStatisticsProcessor extends BaseProcessor {

    @Autowired
    private ThirdMatchFrontStatisticsMapper thirdMatchFrontStatisticsMapper;

    public Response processMatchFrontStatisticsData(@Valid Request<ThirdMatchFrontStatisticsDTO> request) {
        ThirdMatchFrontStatisticsDTO data = request.getData();
        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_FRONT_STATISTICS_API+"】【"+request.getDataSourceCode()
                +" ::"+request.getLinkId()+"::】赛事:{}, 正面交手统计信息数据接收开始", data.getThirdMatchSourceId(), data.getDataSourceCode());
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        //校验LinkId和缓存中是否重复
        validateLinkId(THIRD_MATCH_FRONT_STATISTICS_API,request);
        //校验数据源编码是否合法
        DataSource dataSource = simpleValidateDataSourceCode(request, data.getDataSourceCode());
        //校验三方数据源运动类型,返回标准运动类型
        Long sportId = validateSportId(dataSource.getCode(), String.valueOf(data.getSportId()));
        ThirdMatchFrontStatistics thirdMatchFrontStatistics = new ThirdMatchFrontStatistics();
        BeanUtil.copyProperties(data, thirdMatchFrontStatistics);
        thirdMatchFrontStatistics.setId(data.getThirdMatchSourceId() + dataSource.getCode() + sportId);
        thirdMatchFrontStatistics.setDataSourceCode(dataSource.getCode());
        thirdMatchFrontStatistics.setSportId(sportId);
        thirdMatchFrontStatistics.setEditStatus(Constant.INTEGER_FLAG_ZERO);
        thirdMatchFrontStatistics.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());

        ThirdMatchFrontStatistics oldData = thirdMatchFrontStatisticsMapper.selectByPrimaryKey(thirdMatchFrontStatistics.getId());
        if(Objects.isNull(oldData)){
            thirdMatchFrontStatistics.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            thirdMatchFrontStatisticsMapper.insert(thirdMatchFrontStatistics);
        } else {
            thirdMatchFrontStatistics.setCreateTime(oldData.getCreateTime());
            thirdMatchFrontStatisticsMapper.updateByPrimaryKey(thirdMatchFrontStatistics);
        }
        response.setDataSourceTime(System.currentTimeMillis() - beginTime);
        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_FRONT_STATISTICS_API+"】【"+request.getDataSourceCode()
                +" ::"+request.getLinkId()+"::】正面交手统计信息数据接收结束,返回结果：{}" , JSON.toJSONString(response));
        return response;
    }
}
