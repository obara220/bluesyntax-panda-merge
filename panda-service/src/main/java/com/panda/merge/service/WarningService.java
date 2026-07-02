package com.panda.merge.service;

import com.panda.merge.dto.WarningEventDTO;

public interface WarningService {
    /**
     * 发送预警消息
     * @param event
     */
    void warn(WarningEventDTO event);
}
