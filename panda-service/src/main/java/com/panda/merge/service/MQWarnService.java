package com.panda.merge.service;

public interface MQWarnService {
    void mqBrockerWarn(Throwable e, String topic, String linkedId);
}
