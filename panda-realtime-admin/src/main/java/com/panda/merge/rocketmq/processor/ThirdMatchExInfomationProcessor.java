package com.panda.merge.rocketmq.processor;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.utils.EntityEqualsUtils;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.dto.*;
import com.panda.merge.mapper.ThirdMatchExInfomationMapper;
import com.panda.merge.model.DataSource;
import com.panda.merge.model.ThirdMatchExInfomation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 泰森赛事比赛情报综合资讯
 * @author  tell
 * @since   2021年4月23日12:43:59
 */
@Slf4j
@Validated
@Component
public class ThirdMatchExInfomationProcessor extends BaseProcessor {

    @Autowired
    private ThirdMatchExInfomationMapper thirdMatchExInfomationMapper;

    public Response processMatchExInfomationData(@Valid Request<ThirdMatchExInfomationDTO> request) {
        ThirdMatchExInfomationDTO dtoItem = request.getData();
        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_EX_INFOMATION_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】{}:赛事比赛情报综合资讯数据接收开始",dtoItem.getThirdMatchSourceId());
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        //校验LinkId和缓存中是否重复
        validateLinkId(THIRD_MATCH_EX_INFOMATION_API,request);
        /** 01 校验dataSourceCode是否合法*/
        DataSource dataSource = simpleValidateDataSourceCode(request, dtoItem.getDataSourceCode());
        /** 02 校验三方数据源运动类型,返回标准运动类型*/
        Long sportId = validateSportId(dataSource.getCode(), String.valueOf(dtoItem.getSportId()));
        //创建对象信息
        ThirdMatchExInfomation upItem = new ThirdMatchExInfomation();
        BeanUtil.copyProperties(dtoItem, upItem);
        upItem.setId(dataSource.getId()+FIX+dtoItem.getThirdMatchSourceId());
        upItem.setSportId(sportId);
        upItem.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        //主客队信息
        ThirdMatchCoachDTO homeCoach = dtoItem.getHomeCoach();
        ThirdMatchCoachDTO awayCoach = dtoItem.getAwayCoach();
        //指数情报（赔率情况分析）
        ThirdMatchWinningOddsDTO winningOdds = dtoItem.getWinningOdds();
        //新闻情报信息
        List<ThirdMatchInforMatinsDTO> inforMatinsList = dtoItem.getInforMatinsList();
        if(null != homeCoach ){
            upItem.setHomeCoach(JSON.toJSONString(homeCoach));
        }
        if(null != awayCoach){
            upItem.setAwayCoach(JSON.toJSONString(awayCoach));
        }
        if(null != winningOdds){
            upItem.setWinningOdds(JSON.toJSONString(winningOdds));
        }
        if(!CollectionUtils.isEmpty(inforMatinsList)){
            upItem.setInformations(JSON.toJSONString(inforMatinsList));
        }
        //如果全部数据为空则无需处理
        if(StringUtils.isBlank(upItem.getHomeCoach()) && StringUtils.isBlank(upItem.getAwayCoach()) && StringUtils.isBlank(upItem.getWinningOdds()) && StringUtils.isBlank(upItem.getInformations())){
            log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_EX_INFOMATION_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】{}:赛事比赛情报综合资讯数据为空！",dtoItem.getThirdMatchSourceId());
        }else{
            ThirdMatchExInfomation oldItem = thirdMatchExInfomationMapper.selectByPrimaryKey(upItem.getId());
            if(oldItem == null){
                upItem.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                thirdMatchExInfomationMapper.insertSelective(upItem);
            }else{
                //比较本次投递信息和数据库中现有信息是否一致，一致则不入库
                if(!EntityEqualsUtils.equalsIsObjToString(upItem,oldItem)){
                    upItem.setCreateTime(oldItem.getCreateTime());
                    thirdMatchExInfomationMapper.updateByPrimaryKeySelective(upItem);
                }else{
                    log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_EX_INFOMATION_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】本次接收赛事比赛情报综合资讯数据和库中数据一致，跳过修改，库中数据为：{}" , JSON.toJSONString(oldItem));
                }
            }
        }
        response.setDataSourceTime(System.currentTimeMillis() - beginTime);
        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_EX_INFOMATION_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】{}:赛事比赛情报综合资讯数据接收结束,返回结果 ：{}",dtoItem.getThirdMatchSourceId() , JSON.toJSONString(response));
        return response;
    }


}

