package com.panda.merge.api;

import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.*;

/**
 * KB
 * 报球版的dubbo接口
 * */
public interface IPDBasketBallAdvertiseApi {

    /**
     * 查询报球版详情  +统计查询接口
     * */
    Response searchDetail(MatchAdvertiseQueryDto matchAdvertiseQueryDto);

    /**
     * 统计查询接口
     * */
    Response searchAllScore(PDBasketBallParseContinueDto pauseContinueDto);


    /**
     * 事件查询接口
     * */
    Response  searchEventList(PDBasketBallSearchEventDto eventDto);


    /**
     * 可能事件/确认事件/取消事件
     * */
    Response  sendEvent(PDBasketBallSendEventDto sendEventDto);
    /**
     * 未命中/取消/命中
     * */
    Response  sendBall(PDBasketBallSendBallDto sendBallDto);


    /**
     * 比赛开始
     * */
    Response gameStart(PDBaskectBallMatchStartDto pdBaskectBallMatchStartDto);


    /**
     *  删除事件
     * */
    Response deleteEvent(PDBasketBallDeleteEventDto pdBasketBallDeleteEventDto);

    /**
     *  删除事件
     * */
    Response editEvent(PDBasketBallEditEventDto editEventDto);


    /**
     * 暂停
     * */
    Response pauseAndContinue(PDBasketBallPauseDto pdBasketBallPauseDto);

    /**
     * 当前罚球查询
     * */
    Response getFreeThrow(PDBasketBallPauseDto basketBallPauseDto);

    /**
     * 罚球配置次数
     * */
    Response setFreeThrow(SetFreeThrowDto setFreeThrowDto);

    /**
     * 取消配置次数
     * */
    Response cancelFreeThrow(SetFreeThrowDto setFreeThrowDto);

    /**
     * 罚球进行中记录
     * */
    Response goFreeThrow(GoFreeThrowDto setFreeThrowDto);
    /**
     * 罚球结束触发比分最终变更
     * Send
     * */
    Response sendFreeThrow(SendFreeThrowDto setFreeThrowDto);

    /**
     * 比赛中断/重开
     * */
    Response breakOrReStart(PDBasketBallParseContinueDto parseContinueDto);

    /**
     * 6 分钟比分编辑接口
     * */
    Response editSixScore(PDBasketBallEditSixScoreDto editSixScoreDto);

}
