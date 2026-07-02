package com.panda.merge.service.settleMention.service;

import com.panda.merge.constant.SettleEventCodeEnum;

import java.util.List;
import java.util.Map;

public interface ISettleMentionProcessor<T> {

    void addSettleMention(Map<String, Object> parameters);
    void deleteSettleMention(Long matchId, List<String> keys, SettleEventCodeEnum settleEventCodeEnum);

    T querySettleMention(Long matchId);

}
