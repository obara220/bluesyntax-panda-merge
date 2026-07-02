package com.panda.merge.rocketmq.processor;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ThirdMatchHistoryExpressionDTO;
import com.panda.merge.mapper.ThirdMatchHistoryExpressionMapper;
import com.panda.merge.model.DataSource;
import com.panda.merge.model.ThirdMatchHistoryExpression;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import static com.panda.merge.constant.ConstantSystem.PROJECT_ID_REALTIME;
import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_HISTORY_EXPRESSION_API;

/**
 * 联赛球队历史表现
 *
 * @author aldrich
 * @since 2024/10/14
 */
@Slf4j
@Validated
@Component
public class ThirdMatchHistoryExpressionProcessor extends BaseProcessor {

    @Autowired
    ThirdMatchHistoryExpressionMapper thirdMatchHistoryExpressionMapper;

    public Response processMatchHistoryExpressionData(@Valid Request<ThirdMatchHistoryExpressionDTO> request) {
        ThirdMatchHistoryExpressionDTO data = request.getData();
        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_HISTORY_EXPRESSION_API+"】【"+request.getDataSourceCode()
                +" ::"+request.getLinkId()+"::】联赛:{}, 联赛球队历史表现数据接收开始", data.getThirdTournamentSourceId(), data.getThirdTeamSourceId());
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        //校验LinkId和缓存中是否重复
        validateLinkId(THIRD_MATCH_HISTORY_EXPRESSION_API,request);
        //校验数据源编码是否合法
        DataSource dataSource = simpleValidateDataSourceCode(request, data.getDataSourceCode());
        //校验三方数据源运动类型,返回标准运动类型
        Long sportId = validateSportId(dataSource.getCode(), String.valueOf(data.getSportId()));
        ThirdMatchHistoryExpression thirdMatchHistoryExpression = new ThirdMatchHistoryExpression();
        BeanUtil.copyProperties(data, thirdMatchHistoryExpression);
        thirdMatchHistoryExpression.setId(data.getThirdTournamentSourceId() + data.getThirdTeamSourceId()
                + dataSource.getCode() + data.getExpressingType() + sportId);
        thirdMatchHistoryExpression.setSportId(sportId);
        thirdMatchHistoryExpression.setEditStatus(Constant.INTEGER_FLAG_ZERO);
        thirdMatchHistoryExpression.setDataSourceCode(dataSource.getCode());
        thirdMatchHistoryExpression.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        //场均进球数计算
        BigDecimal bigDecimalGoal = new BigDecimal(String.valueOf(data.getGoalsForTotal()));
        BigDecimal bigDecimalMatch = new BigDecimal(5);
        BigDecimal winPercent = bigDecimalGoal.divide(bigDecimalMatch, 2, RoundingMode.CEILING);
        thirdMatchHistoryExpression.setAverageGoal(winPercent);
        thirdMatchHistoryExpression.setEditStatus(Constant.INTEGER_FLAG_ZERO);

        ThirdMatchHistoryExpression oldData = thirdMatchHistoryExpressionMapper.selectByPrimaryKey(thirdMatchHistoryExpression.getId());
        if(Objects.isNull(oldData)){
            thirdMatchHistoryExpression.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            thirdMatchHistoryExpressionMapper.insert(thirdMatchHistoryExpression);
        } else {
            thirdMatchHistoryExpression.setCreateTime(oldData.getCreateTime());
            thirdMatchHistoryExpressionMapper.updateByPrimaryKeySelective(thirdMatchHistoryExpression);
        }
        response.setDataSourceTime(System.currentTimeMillis() - beginTime);
        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_HISTORY_EXPRESSION_API+"】【"+request.getDataSourceCode()
                +" ::"+request.getLinkId()+"::】联赛球队历史表现数据接收结束,返回结果：{}" , JSON.toJSONString(response));
        return response;
    }
}
