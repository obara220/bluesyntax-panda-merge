package com.panda.merge.constant;


/**
 * 项目中的全局常量定义
 */
public interface Constants {

    String WEBSOCKET_STR = "websocket";
    String UPGRADE_STR = "Upgrade";
    int OK_CODE = 200;

    String HTTP_CODEC = "http-codec";
    String AGGREGATOR = "aggregator";
    String HTTP_CHUNKED = "http-chunked";
    String HANDLER = "handler";
    int MAX_CONTENT_LENGTH = 65536;

    //ws测试端口
    int PORT = 8989;

    // http测试端口
    int HTTPPORT = 8087;

    String WEB_SOCKET_URL = "ws://localhost:"+PORT+"/ws";

    //订阅者列表
    String IM_QUEUE_CHANNLID = "im-queue-channlid";
}
