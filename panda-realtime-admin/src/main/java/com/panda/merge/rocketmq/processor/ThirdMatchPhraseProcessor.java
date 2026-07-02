package com.panda.merge.rocketmq.processor;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.utils.EntityEqualsUtils;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ThirdMatchPhraseDTO;
import com.panda.merge.dto.ThirdMatchPhraseDetail;
import com.panda.merge.mapper.ThirdMatchPhraseMapper;
import com.panda.merge.model.DataSource;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.model.ThirdMatchPhrase;
import com.panda.merge.model.ThirdMatchPhraseExample;
import com.panda.merge.rocketmq.producer.ThirdMatchPhraseInfoProducer;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 泰森赛事文字直播信息
 * @author  tell
 * @since   2021年3月6日15:27:09
 */
@Slf4j
@Validated
@Component
public class ThirdMatchPhraseProcessor extends BaseProcessor {

    @Autowired
    private ThirdMatchPhraseMapper thirdMatchPhraseMapper;
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private ThirdMatchPhraseInfoProducer thirdMatchPhraseInfoProducer;

    public Response processMatchPhraseData(@Valid Request<ThirdMatchPhraseDTO> request) {
        ThirdMatchPhraseDTO phraseDTO = request.getData();
        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_PHRASE_INFO_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】赛事:{}文字直播数据接收开始",phraseDTO.getThirdMatchSourceId());
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        //校验LinkId和缓存中是否重复
        validateLinkId(THIRD_MATCH_PHRASE_INFO_API,request);
        /** 01 校验dataSourceCode是否合法*/
        DataSource dataSource = simpleValidateDataSourceCode(request, phraseDTO.getDataSourceCode());
        /** 02 校验三方数据源运动类型,返回标准运动类型*/
        Long sportId = validateSportId(dataSource.getCode(), String.valueOf(phraseDTO.getSportId()));
        //本次操作对象
        String id = dataSource.getId() + FIX + phraseDTO.getPhraseId();
        ThirdMatchPhraseDetail item = new ThirdMatchPhraseDetail();
        BeanUtil.copyProperties(phraseDTO, item);
        item.setId(id);
        item.setSportId(sportId);
        item.setLinkId(request.getLinkId());
        //视频赛事级别分布式锁
        String tryLockKey = RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchPhraseDTO:"+item.getId();
        boolean lockFlag = false;
        try {
            //获取分布式锁
            lockFlag = redisService.tryLock(tryLockKey, tryLockKey, 10, 10);
            //获取库中信息
            ThirdMatchPhrase oldItem = thirdMatchPhraseMapper.selectByPrimaryKey(item.getId());
            if(null == oldItem){
                item.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            }else{
                item.setCreateTime(oldItem.getCreateTime());
            }
            item.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            //获取三方赛事信息
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(dataSource.getCode(), phraseDTO.getThirdMatchSourceId());
            if(null != thirdMatchInfo){
                item.setThirdMatchId(thirdMatchInfo.getId());
                if (null != thirdMatchInfo.getReferenceId() && thirdMatchInfo.getReferenceId() != 0L) {
                    item.setStandardMatchId(thirdMatchInfo.getReferenceId());
                    //推送到下游
                    thirdMatchPhraseInfoProducer.pushThirdMatchPhraseInfo(request.getLinkId(),item);
                    item.setSendData(ONE);
                }
            }
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_MATCH_PHRASE_INFO_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】赛事文字直播数据：{}",JSON.toJSONString(item));
            if(null == oldItem){
                //需要新增的
                thirdMatchPhraseMapper.insertSelective(item);
            }else{
                //比较本次投递信息和数据库中现有信息是否一致，一致则不入库
                if(!EntityEqualsUtils.equalsIsObjToString(item,oldItem)){
                    thirdMatchPhraseMapper.updateByPrimaryKeySelective(item);
                }else{
                    log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_PHRASE_INFO_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】本次接收赛事文字直播数据和库中数据一致，跳过修改，库中数据为：{}" , JSON.toJSONString(oldItem));
                }
            }
        }finally {
            if (lockFlag) {
                //释放redis锁
                redisService.unLock(tryLockKey, tryLockKey);
            }
            response.setDataSourceTime(System.currentTimeMillis() - beginTime);
            log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_PHRASE_INFO_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】赛事文字直播数据接收结束,返回结果 ：{}" , JSON.toJSONString(response));
        }
        return response;
    }


}

