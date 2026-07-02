package com.panda.merge.rocketmq.processor;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.bo.VideoAnimationBO;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.VideoStatusEnum;
import com.panda.merge.common.utils.EntityEqualsUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ThirdVideoBoardCastRecordDTO;
import com.panda.merge.exception.ExceptionHelper;
import com.panda.merge.model.DataSource;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.model.ThirdVideoBoardCastRecord;
import com.panda.merge.rocketmq.producer.ModifyMatchInfoProducer;
import com.panda.merge.rocketmq.producer.RealtimeBaseProduecr;
import com.panda.merge.rocketmq.producer.ThirdVideoImgInfoProducer;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.ThirdMatchInfoService;
import com.panda.merge.service.ThirdVideoBoardCastRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 播控中心接入
 * @author  tell
 * @since   2020年9月15日20:23:41
 */
@Slf4j
@Validated
@Component
public class ThirdVideoBoardCastRecordProcessor extends BaseProcessor {

    @Autowired
    private ThirdVideoBoardCastRecordService thirdVideoBoardCastRecordService;
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private ModifyMatchInfoProducer modifyMatchInfoProducer;

    @Autowired
    public RealtimeBaseProduecr realtimeBaseProduecr;

    /**
     * 视频数据处理
     * */
    @Async("getMatchThreadPool")
    @ExceptionHelper
    public Response processVideoData(@Valid Request<ThirdVideoBoardCastRecordDTO> request) {
        ThirdVideoBoardCastRecordDTO dtoItem = request.getData();
        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_VIDEO_INFO_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】播控中心数据接收开始,源赛事ID：{}",dtoItem.getMatchId());
        long beginTime = System.currentTimeMillis();
        //校验LinkId和缓存中是否重复
        validateLinkId(THIRD_VIDEO_INFO_API,request);
        /** 01 校验dataSourceCode是否合法*/
        DataSource dataSource = simpleValidateDataSourceCode(request, dtoItem.getDataSourceCode());
        Long sportId = null;
        if(null != dtoItem.getSportId()){
            /** 02 校验三方数据源运动类型,返回标准运动类型*/
            sportId = validateSportId(dataSource.getCode(), String.valueOf(dtoItem.getSportId()));
        }
        //如果是赛事并关联了标准赛事，需要更新标准赛事修改时间（因为业务是根据标准赛事更新查询）
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(dataSource.getCode(), dtoItem.getMatchId());
        if (null != thirdMatchInfo) {
            sportId = thirdMatchInfo.getSportId();
        }
        //如果flv,m3u8视频都为空，则直播状态默认为【11取消】
        if(StringUtils.isBlank(dtoItem.getLiveVideoPathFlv()) && StringUtils.isBlank(dtoItem.getLiveVideoPathM3u8())){
            dtoItem.setLiveVideoPathStatus(VideoStatusEnum.NUM_11.getCode());
            log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_VIDEO_INFO_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】播控中心源赛事ID：{},传入视频地址为空！",dtoItem.getMatchId());
        }
        Response<ThirdVideoBoardCastRecord> response = Response.success();
        //编辑的对象
        ThirdVideoBoardCastRecord item = new ThirdVideoBoardCastRecord();
        BeanUtil.copyProperties(dtoItem, item);
        item.setId(dataSource.getId()+""+dtoItem.getMatchId());
        //视频赛事级别分布式锁
        String tryLockKey = RedisConfig.REDIS_KEY_DATABASE + "::ThirdVideoBoardCastRecordDTO:"+item.getId();
        boolean lockFlag = false;
        try {
            //校验球队名称长度
            if(!validateTeamNames(dtoItem,response)){
                return response;
            }
            //获取分布式锁
            lockFlag = redisService.tryLock(tryLockKey, tryLockKey, 10, 10);
            item.setSportId(sportId);
            //获取库中视频信息
            ThirdVideoBoardCastRecord oldItem = thirdVideoBoardCastRecordService.getItem(dtoItem.getMatchId(),dataSource.getCode());
            //是否需要更新标准赛事修改时间
//            Boolean upStandardMatchflag = true;
            if(null == oldItem){
                thirdVideoBoardCastRecordService.saveItem(item);
                //需求：2915 【客户端】接入L01动画源， 涉及到赛程动画源优先级，新增时需要通知一次
                thirdVideoImgInfoProducer.pushThirdVideoInfo(request.getLinkId(),item);
            }else{
                //比较本次投递信息和数据库中现有信息是否一致，一致则不入库
                if(!EntityEqualsUtils.equalsIsObjToString(item,oldItem)){
                    item.setCreateTime(oldItem.getCreateTime());
                    thirdVideoBoardCastRecordService.updateItem(item);
                    log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_VIDEO_INFO_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】源赛事ID：{} 修改成功",dtoItem.getMatchId());
                }else{
//                    upStandardMatchflag = false;
                    log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_VIDEO_INFO_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】源赛事ID：{},本次接收到播控中心数据和库中数据一致，跳过修改", dtoItem.getMatchId());
                }
                //如果视频直播状态发生改变需要通知赛程管理，优化单209
                if(!oldItem.getLiveVideoPathStatus().equals(item.getLiveVideoPathStatus()) || !oldItem.getLiveVideoOnline().equals(item.getLiveVideoOnline())){
                    thirdVideoImgInfoProducer.pushThirdVideoInfo(request.getLinkId(),item);
                }
            }
            //标准赛事ID
            Long standardMatchId = null;
            if (null != thirdMatchInfo) {
                standardMatchId = thirdMatchInfo.getReferenceId();
                if(!ONE.equals(thirdMatchInfo.getLmtMode())){
                    //打开三方赛事动画
                    ThirdMatchInfo upThirdMatchInfo = new ThirdMatchInfo();
                    upThirdMatchInfo.setId(thirdMatchInfo.getId());
                    upThirdMatchInfo.setLmtMode(ONE);
                    realtimeBaseProduecr.send(upThirdMatchInfo,request.getLinkId(),DATA_THIRD_MATCH_INFO_DB,thirdMatchInfo.getThirdMatchSourceId(),thirdMatchInfo.getDataSourceCode());
                }
                //更新标准赛事修改时间
                if (null != standardMatchId && standardMatchId != 0L) {
//                    if(upStandardMatchflag){
//                        StandardMatchInfo standardMatchInfo = new StandardMatchInfo();
//                        standardMatchInfo.setId(standardMatchId);
//                        realtimeBaseProduecr.send(standardMatchInfo,request.getLinkId(),DATA_STANDARD_MATCH_INFO_DB,thirdMatchInfo.getReferenceId()+"",thirdMatchInfo.getDataSourceCode());
//                    }
                    //通知下游赛事视频变更
                    modifyMatchInfoProducer.pushModifyMatchInfoMessage(request.getLinkId(), standardMatchId, "播控中心接收到视频", null );
                }
            }
            response.setData(item);
            log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_VIDEO_INFO_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】" +
                    "播控中心数据接收结束,源赛事ID：{}，标准赛事ID：{}" , dtoItem.getMatchId(),standardMatchId);
        }finally {
            if (lockFlag) {
                //释放redis锁
                redisService.unLock(tryLockKey, tryLockKey);
            }
            response.setDataSourceTime(System.currentTimeMillis() - beginTime);
            log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_VIDEO_INFO_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】播控中心数据接收结束,返回结果：{}", JSONObject.toJSONString(response));
        }
        return response;
    }

    @Autowired
    private ThirdVideoImgInfoProducer thirdVideoImgInfoProducer;
    /**
     * 视频截图信息
     * */
    public Response processVideoImgData(@Valid Request<ThirdVideoBoardCastRecordDTO> request) {
        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_VIDEO_IMG_INFO_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】播控中心视频截图数据接收开始");
        long beginTime = System.currentTimeMillis();
        //校验LinkId和缓存中是否重复
        validateLinkId(THIRD_VIDEO_IMG_INFO_API,request);
        Response response = Response.success();
        ThirdVideoBoardCastRecordDTO data = request.getData();
        if(StringUtils.isBlank(data.getLiveVideoImgUrl())){
            log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_VIDEO_IMG_INFO_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】视频截图地址不能为空！");
            return response;
        }
        //获取三方赛事信息
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(data.getDataSourceCode(), data.getMatchId());
        if (null != thirdMatchInfo) {
            //如果已经关联标准赛事ID
            if (null != thirdMatchInfo.getReferenceId() && thirdMatchInfo.getReferenceId() != 0L) {
                VideoAnimationBO videoAnimationBO = new VideoAnimationBO();
                videoAnimationBO.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
                videoAnimationBO.setThirdMatchId(thirdMatchInfo.getId());
                videoAnimationBO.setAniId(thirdMatchInfo.getThirdMatchSourceId());
                videoAnimationBO.setStandardMatchId(thirdMatchInfo.getReferenceId());
                videoAnimationBO.setLiveVideoImgUrl(data.getLiveVideoImgUrl());
                videoAnimationBO.setLeagueFlag(data.getLeagueFlag());
                thirdVideoImgInfoProducer.pushThirdVideoImgInfo(request.getLinkId(),videoAnimationBO,thirdMatchInfo.getDataSourceCode());
            }
        }
        response.setDataSourceTime(System.currentTimeMillis() - beginTime);
        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_VIDEO_IMG_INFO_API+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】" +
                "源赛事ID：{},播控中心视频截图数据接收结束,返回结果:{}" , data.getMatchId(),JSONObject.toJSONString(response));
        return response;
    }



    private static final int MAX_NAME_LENGTH = 50;

    private boolean validateNameLength(String name, String fieldName, Response response) {
        if (StringUtils.isNotBlank(name) && name.length() > MAX_NAME_LENGTH) {
            response.setMsg(fieldName + "长度不能超过" + MAX_NAME_LENGTH + "字符");
            return false;
        }
        return true;
    }

    public boolean validateTeamNames(ThirdVideoBoardCastRecordDTO dtoItem, Response response) {
        if (!validateNameLength(dtoItem.getHomeZn(), "主队中文名", response)) return false;
        if (!validateNameLength(dtoItem.getAwayZn(), "客队中文名", response)) return false;
        if (!validateNameLength(dtoItem.getHomeEn(), "主队英文名", response)) return false;
        if (!validateNameLength(dtoItem.getAwayEn(), "客队英文名", response)) return false;
        return true;
    }


}

