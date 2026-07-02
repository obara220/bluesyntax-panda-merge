package com.panda.merge.rocketmq.processor;

import com.alibaba.fastjson.JSON;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ThirdMatchExpectationDTO;
import com.panda.merge.model.DataSource;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.rocketmq.producer.RealtimeBaseProduecr;
import com.panda.merge.rocketmq.producer.ThirdMatchExpectationProducer;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 三方赛事预期信息更新
 *
 * @author aldrich
 * @since 2024/11/6
 */
@Slf4j
@Validated
@Component
public class ThirdMatchExpectationProcessor extends BaseProcessor {

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private ThirdMatchExpectationProducer thirdMatchExpectationProducer;

    @Autowired
    public RealtimeBaseProduecr realtimeBaseProduecr;

    public Response processMatchExpectatioData(@Valid Request<ThirdMatchExpectationDTO> request) {
        ThirdMatchExpectationDTO simpleItem = request.getData();
        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_EXPECTATION_API+"】【" +request.getDataSourceCode()+"::" +request.getLinkId()+"::】三方赛事预期数据接收开始,simpleItem={}",JSON.toJSONString(simpleItem));
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        //校验LinkId和缓存中是否重复
        validateLinkId(THIRD_MATCH_EXPECTATION_API,request);
        // 01 校验dataSourceCode是否合法
        DataSource dataSource = simpleValidateDataSourceCode(request, simpleItem.getDataSourceCode());
        // 02 校验三方数据源运动类型,返回标准运动类型
        Long sportId = validateSportId(dataSource.getCode(), String.valueOf(simpleItem.getSportId()));
        //获取三方赛事信息
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItemDetail(dataSource.getCode(), simpleItem.getThirdMatchSourceId());
        if(!Objects.isNull(thirdMatchInfo)){
            ThirdMatchInfo upThirdMatchInfo = new ThirdMatchInfo();
            BeanUtils.copyProperties(simpleItem, upThirdMatchInfo);
            upThirdMatchInfo.setId(thirdMatchInfo.getId());
            upThirdMatchInfo.setSportId(sportId);
//            thirdMatchInfoService.updateByPrimaryKeySelective(upThirdMatchInfo,request.getLinkId());
            realtimeBaseProduecr.send(upThirdMatchInfo,request.getLinkId(),DATA_THIRD_MATCH_INFO_DB,thirdMatchInfo.getThirdMatchSourceId(),thirdMatchInfo.getDataSourceCode());
            log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_EXPECTATION_API+"】【" +request.getDataSourceCode()+"::"+request.getLinkId()+"::】本次接收三方赛事预期数据信息:{}", JSON.toJSONString(upThirdMatchInfo));
            //当三方赛事有标准赛事时，更新标准赛事主数据源对应的三方赛事预期信息
            if(thirdMatchInfo.getReferenceId() != null && thirdMatchInfo.getReferenceId() != 0){
                StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(thirdMatchInfo.getReferenceId());
                if(!Objects.isNull(standardMatchInfo)){
                    List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoService.getItems(standardMatchInfo.getId());
                    List<ThirdMatchInfo> thirdMatchInfoList = thirdMatchInfos.stream().filter(e->e.getDataSourceCode().equals(standardMatchInfo.getDataSourceCode())).collect(Collectors.toList());
                    if(CollectionUtils.isNotEmpty(thirdMatchInfoList)){
                        ThirdMatchInfo thirdMatch = thirdMatchInfoList.get(0);
                        ThirdMatchInfo mainThirdMatch = new ThirdMatchInfo();
                        BeanUtils.copyProperties(simpleItem, mainThirdMatch);
                        mainThirdMatch.setId(thirdMatch.getId());
                        mainThirdMatch.setSportId(sportId);
//                        thirdMatchInfoService.updateByPrimaryKeySelective(standardToThirdMatch,request.getLinkId());
                        realtimeBaseProduecr.send(mainThirdMatch,request.getLinkId(),DATA_THIRD_MATCH_INFO_DB,thirdMatch.getThirdMatchSourceId(),thirdMatch.getDataSourceCode());
                        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_EXPECTATION_API+"】【"+request.getDataSourceCode()+"::"+request.getLinkId()+"::】本次修改标准赛事对应主数据源三方赛事预期数据信息:{}", JSON.toJSONString(mainThirdMatch));
                        //预期信息更新后信息通知赛程
                        thirdMatchExpectationProducer.sendThirdMatchExpectation(simpleItem);
                    }
                } else {
                    log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_EXPECTATION_API+"】【" +request.getDataSourceCode()+" ::"+request.getLinkId()+"::】本次接收三方赛事预期数据无对应标准赛事信息");
                }
            }
        } else {
            log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_EXPECTATION_API+"】【" +request.getDataSourceCode()+" ::"+request.getLinkId()+"::】本次接收三方赛事预期数据无对应三方赛事信息");
        }
        response.setDataSourceTime(System.currentTimeMillis() - beginTime);
        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_EXPECTATION_API+"】【"+request.getDataSourceCode()+" ::" +request.getLinkId()+"::】三方赛事预期数据接收结束,返回结果 ：{}" , JSON.toJSONString(response));
        return response;
    }
}
