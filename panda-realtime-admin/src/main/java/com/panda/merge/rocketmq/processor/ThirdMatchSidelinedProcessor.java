package com.panda.merge.rocketmq.processor;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.utils.EntityEqualsUtils;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ThirdMatchSidelinedDTO;
import com.panda.merge.mapper.ThirdMatchSidelinedMapper;
import com.panda.merge.model.DataSource;
import com.panda.merge.model.ThirdMatchSidelined;
import com.panda.merge.model.ThirdMatchSidelinedExample;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 泰森赛事缺阵球员名单（伤停）信息
 * @author  tell
 * @since   2020年9月15日20:23:41
 */
@Slf4j
@Validated
@Component
public class ThirdMatchSidelinedProcessor extends BaseProcessor {

    @Autowired
    private ThirdMatchSidelinedMapper thirdMatchSidelinedMapper;

    public Response processMatchSidelinedData(@Valid Request<List<ThirdMatchSidelinedDTO>> request) {
        List<ThirdMatchSidelinedDTO> dtoItems = request.getData();
        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_SIDELINED_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】赛事球员伤停数据接收开始");
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        //校验LinkId和缓存中是否重复
        validateLinkId(THIRD_MATCH_SIDELINED_API,request);
        if(CollectionUtils.isEmpty(dtoItems)){
            log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_SIDELINED_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】赛事球员伤停数据不能null,无需处理！");
            return response;
        }
        ThirdMatchSidelinedDTO thirdMatchSidelinedDTO = dtoItems.get(0);
        //视频赛事级别分布式锁
        String tryLockKey = String.format(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchSidelinedDTO:%s_%s"
                ,thirdMatchSidelinedDTO.getDataSourceCode(),thirdMatchSidelinedDTO.getThirdMatchSourceId());
        boolean lockFlag = false;
        try {
            //获取分布式锁
            lockFlag = redisService.tryLock(tryLockKey, tryLockKey, 10, 10);
            //查询库中赛事现有伤停数据
            ThirdMatchSidelinedExample matchSidelinedExample = new ThirdMatchSidelinedExample();
            matchSidelinedExample.createCriteria().andThirdMatchSourceIdEqualTo(thirdMatchSidelinedDTO.getThirdMatchSourceId())
                    .andDataSourceCodeEqualTo(thirdMatchSidelinedDTO.getDataSourceCode());
            List<ThirdMatchSidelined> thirdMatchSidelineds = thirdMatchSidelinedMapper.selectByExample(matchSidelinedExample);
            Map<String, ThirdMatchSidelined> id2ItemDb = new HashMap<>();
            if(!CollectionUtils.isEmpty(thirdMatchSidelineds)){
                id2ItemDb = thirdMatchSidelineds.stream().collect(Collectors.toMap(ThirdMatchSidelined::getId, thi -> thi));
            }
            response.setData(id2ItemDb.size());
            for (ThirdMatchSidelinedDTO dtoItem: dtoItems) {
                if(StringUtils.isBlank(dtoItem.getThirdPlayerName()) || StringUtils.isBlank(dtoItem.getThirdPlayerEnName())){
                    log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_SIDELINED_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】" +
                            "赛事球员伤停数据球员名称不能null,无需处理！源球员ID={}",dtoItem.getThirdPlayerSourceId());
                    continue;
                }
                /** 01 校验dataSourceCode是否合法*/
                DataSource dataSource = simpleValidateDataSourceCode(request, dtoItem.getDataSourceCode());
                /** 02 校验三方数据源运动类型,返回标准运动类型*/
                Long sportId = validateSportId(dataSource.getCode(), String.valueOf(dtoItem.getSportId()));
                //库中历史赛事统计信息ID
                String matchHistoryStatisticsId = dataSource.getId() + "" + dtoItem.getThirdMatchSourceId();
                //本次操作对象
                ThirdMatchSidelined upItem = new ThirdMatchSidelined();
                BeanUtil.copyProperties(dtoItem, upItem);
                //数据来源ID+赛事源ID+球队源ID+球员源ID
                upItem.setId(matchHistoryStatisticsId+FIX+dtoItem.getThirdTeamSourceId()+FIX+dtoItem.getThirdPlayerSourceId());
                upItem.setSportId(sportId);
                upItem.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                //获取库中信息
                ThirdMatchSidelined oldItem = id2ItemDb.get(upItem.getId());
                if(null == oldItem){
                    upItem.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    thirdMatchSidelinedMapper.insertSelective(upItem);
                    id2ItemDb.put(upItem.getId(),upItem);
                }else{
                    //比较本次投递信息和数据库中现有信息是否一致，一致则不入库
                    if(!EntityEqualsUtils.equalsIsObjToString(upItem,oldItem)){
                        upItem.setCreateTime(oldItem.getCreateTime());
                        thirdMatchSidelinedMapper.updateByPrimaryKeySelective(upItem);
                    }else{
                        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_SIDELINED_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】本次接收赛事球员伤停数据和库中数据一致，跳过修改，库中数据为：{}" , JSON.toJSONString(oldItem));
                    }
                }
            }
        }finally {
            if (lockFlag) {
                //释放redis锁
                redisService.unLock(tryLockKey, tryLockKey);
            }
            response.setDataSourceTime(System.currentTimeMillis() - beginTime);
            log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_SIDELINED_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】赛事球员伤停数据接收结束,返回结果 ：{}" , JSON.toJSONString(response));
            return response;
        }
    }


}

