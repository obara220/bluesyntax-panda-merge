package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.bo.VideoAnimationBO;
import com.panda.merge.dto.Request;
import com.panda.merge.model.ThirdVideoBoardCastRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.ONE;

/**
 * 下发视频截图图片地址到下游
 * @author tell
 * @since  2020年12月10日12:12:15
 */
@Slf4j
@Component
public class ThirdVideoImgInfoProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;


    public void pushThirdVideoInfo(String linkId, ThirdVideoBoardCastRecord item){
        Request<ThirdVideoBoardCastRecord> request = new Request<>();
        request.setLinkId(linkId);
        request.setDataSourceCode(item.getDataSourceCode());
        request.setData(item);
        MessageBuilder<Request<ThirdVideoBoardCastRecord>> requestMessageBuilder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.send("THIRD_VIDEO_INFO:" + item.getMatchId(), requestMessageBuilder.build());
        log.info("linkId=【{}】组装视频信息下发完成,topic=THIRD_VIDEO_INFO,赛事ID:{}", linkId, item.getMatchId());
    }

    public void pushThirdVideoImgInfo(String linkId, VideoAnimationBO videoAnimationBO,String dataSourceCode){
        Request<VideoAnimationBO> request = new Request<>();
        request.setLinkId(linkId);
        request.setDataSourceCode(dataSourceCode);
        request.setData(videoAnimationBO);
        MessageBuilder<Request<VideoAnimationBO>> requestMessageBuilder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.send("THIRD_VIDEO_IMG_INFO:" + videoAnimationBO.getStandardMatchId(), requestMessageBuilder.build());
        log.info("linkId=【{}】组装视频截图信息下发完成,topic=THIRD_VIDEO_IMG_INFO,源赛事ID:{}", linkId,videoAnimationBO.getThirdMatchId());
        //是重要联赛
        if(ONE.equals(videoAnimationBO.getLeagueFlag())){
            rocketMqTemplate.send("THIRD_VIDEO_IMG_INFO_CUP:" + videoAnimationBO.getStandardMatchId(), requestMessageBuilder.build());
            log.info("linkId=【{}】组装视频截图信息下发完成,topic=THIRD_VIDEO_IMG_INFO_CUP,源赛事ID:{}", linkId,videoAnimationBO.getThirdMatchId());
        }
    }
}
