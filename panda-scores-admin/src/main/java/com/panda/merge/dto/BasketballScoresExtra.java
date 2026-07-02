package com.panda.merge.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.panda.merge.annotation.ScoresProperty;
import lombok.Data;

import java.util.Map;

/**
 * @author warren
 * @since 2025/08/04 16:19:40
 */
@Data
public class BasketballScoresExtra extends AbstractSportScores {
    /**
     * 暂停历史记录: key存放当前系统时间, value存放历史当前主客队暂停次数
     */
    @JSONField(serialzeFeatures = {SerializerFeature.DisableCircularReferenceDetect})
    @ScoresProperty(eventName = "暂停历史记录: key存放当前系统时间(切换OT及在当前OT增加时间), value存放历史当前主客队暂停次数")
    private Map<Long, CommonItem> historyTimeout;

    @ScoresProperty(eventName = "最新的加时赛软次")
    private Integer currentTimeout;

    public BasketballScoresExtra() {
        super.init(this);
    }
}
