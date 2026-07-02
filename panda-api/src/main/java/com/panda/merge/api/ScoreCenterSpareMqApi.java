package com.panda.merge.api;

/**
 * @author warren
 * @since 2025/06/14 02:40:52
 */
public interface ScoreCenterSpareMqApi {
    /**
     * 备用MQ停止及启动
     *
     * @param isStop 启停状态 1停止，非1则启动
     */
    void slaveRocketMqStopResume(Integer isStop);
}
