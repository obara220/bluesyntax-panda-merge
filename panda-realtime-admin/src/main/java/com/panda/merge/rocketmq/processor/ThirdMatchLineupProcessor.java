package com.panda.merge.rocketmq.processor;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.utils.EntityEqualsUtils;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ThirdMatchLineupDTO;
import com.panda.merge.dto.ThirdMatchLineupSimpleDTO;
import com.panda.merge.model.DataSource;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.model.ThirdMatchLineup;
import com.panda.merge.service.ThirdMatchInfoService;
import com.panda.merge.service.ThirdMatchLineupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 泰森赛事首发阵容信息
 * @author  tell
 * @since   2020年9月15日20:23:41
 */
@Slf4j
@Validated
@Component
public class ThirdMatchLineupProcessor extends BaseProcessor {

    @Autowired
    private ThirdMatchLineupService thirdMatchLineupService;

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    public Response processMatchLineupData(@Valid Request<ThirdMatchLineupSimpleDTO> request) {
        ThirdMatchLineupSimpleDTO simpleItem = request.getData();
        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_LINEUP_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】赛事:{}首发阵容数据接收开始",simpleItem.getThirdMatchSourceId());
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        //校验LinkId和缓存中是否重复
        validateLinkId(THIRD_MATCH_LINEUP_API,request);
        /** 01 校验dataSourceCode是否合法*/
        DataSource dataSource = simpleValidateDataSourceCode(request, simpleItem.getDataSourceCode());
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_MATCH_LINEUP_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】赛事首发阵容数据接收开始");
        /** 02 校验三方数据源运动类型,返回标准运动类型*/
        Long sportId = validateSportId(dataSource.getCode(), String.valueOf(simpleItem.getSportId()));
        //获取赛事下阵容列表
        List<ThirdMatchLineupDTO> lineupList = simpleItem.getLineupList();
        //获取库中现有阵容
        Map<String, ThirdMatchLineup> id2Lineup = thirdMatchLineupService.getItemList(simpleItem.getThirdMatchSourceId(), simpleItem.getDataSourceCode())
                .stream().collect(Collectors.toMap(ThirdMatchLineup::getId, thi -> thi));
        for (ThirdMatchLineupDTO thirdMatchLineupDto: lineupList) {
            //本次操作对象
            String id = dataSource.getId() + FIX + simpleItem.getThirdMatchSourceId() + FIX + thirdMatchLineupDto.getThirdTeamSourceId() + FIX + thirdMatchLineupDto.getThirdPlayerSourceId();
            ThirdMatchLineup item = new ThirdMatchLineup();
            BeanUtil.copyProperties(thirdMatchLineupDto, item);
            item.setId(id);
            item.setThirdMatchSourceId(simpleItem.getThirdMatchSourceId());
            item.setSportId(sportId);
            item.setDataSourceCode(dataSource.getCode());
            item.setEditStatus(Constant.INTEGER_FLAG_ZERO);
            item.setHomeFormation(simpleItem.getHomeFormation());
            item.setAwayFormation(simpleItem.getAwayFormation());
            //获取库中信息
            ThirdMatchLineup oldItem = id2Lineup.get(item.getId());
            if(null == oldItem){
                thirdMatchLineupService.saveItem(item,request.getLinkId());
            }else{
//                //比较本次投递信息和数据库中现有信息是否一致，一致则不入库
//                if(!EntityEqualsUtils.equalsIsObjToString(item,oldItem)){
                    item.setCreateTime(oldItem.getCreateTime());
                    thirdMatchLineupService.updateItem(item);
//                }else{
//                    log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_LINEUP_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】本次接收赛事首发阵容数据和库中数据一致，跳过修改，库中数据为：{}" , JSON.toJSONString(oldItem));
//                }
            }
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_MATCH_LINEUP_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】赛事首发阵容数据处理，item={}",JSON.toJSONString(item));
        }
        //如果是赛事并关联了标准赛事，需要更新标准赛事修改时间（因为业务是根据标准赛事更新查询）
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(dataSource.getCode(), simpleItem.getThirdMatchSourceId());
        if (null != thirdMatchInfo) {
            //当前主客队阵型和库中主客队阵型有差距则需要更新
            if(!StringUtils.equals(simpleItem.getHomeFormation(),thirdMatchInfo.getHomeFormation()) || !StringUtils.equals(simpleItem.getAwayFormation(),thirdMatchInfo.getAwayFormation())){
                log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_LINEUP_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】阵容数据发生变更,主={},old主={},客={},old客={}"
                        ,simpleItem.getHomeFormation(),thirdMatchInfo.getHomeFormation(),simpleItem.getHomeFormation(),thirdMatchInfo.getAwayFormation());
                //设置三方赛事阵型
                ThirdMatchInfo upThirdMatchInfo = new ThirdMatchInfo();
                upThirdMatchInfo.setId(thirdMatchInfo.getId());
                upThirdMatchInfo.setHomeFormation(simpleItem.getHomeFormation());
                upThirdMatchInfo.setAwayFormation(simpleItem.getAwayFormation());
                thirdMatchInfoService.updateByPrimaryKeySelective(upThirdMatchInfo,request.getLinkId());
            }
        }
        response.setDataSourceTime(System.currentTimeMillis() - beginTime);
        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_LINEUP_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】赛事首发阵容数据接收结束,返回结果 ：{}" , JSON.toJSONString(response));
        return response;
    }


}

