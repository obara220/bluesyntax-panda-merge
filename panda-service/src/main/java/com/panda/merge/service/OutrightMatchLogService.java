package com.panda.merge.service;

import com.panda.merge.model.OutrightMatchLog;

import java.util.List;
import java.util.Map;

/**
 * @Author Kepa
 * @Date 2021/7/15 12:15
 * @Version 1.0
 */
public interface OutrightMatchLogService {

    /**
     *  批量新增冠军玩法操作日志
     * @param listParams
     * @param outrightMatchLog
     * @return
     */
    boolean saveBatchOutrightMatchRecord(List<Map<String, String>> listParams, OutrightMatchLog outrightMatchLog);
}
