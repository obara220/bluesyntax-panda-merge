package com.panda.merge;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import http.HttpClientUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        String url = "https://mollybet.com/v1/sessions/";
        Map<String, Object> params = new HashMap<>();
        params.put("username","obmollydemo");
        params.put("password","abc12345");
        String response = "";
        response = HttpClientUtil.getInstance().doPost(url, params);
        JSONObject json = JSONUtil.parseObj(response);
        System.out.println("===========" + response);

        // 此处的WebSocket服务端URI，上面服务端第2点有详细说明
        String serverURI = "wss://api.mollybet.com/v1/stream/?";
        URI uri = new URI(serverURI + "token=" + json.get("data"));
        MyWebSocketClient myClient = new MyWebSocketClient(uri);
        MyWebSocketClient.trustAllHosts(myClient);
        myClient.connectBlocking();
        myClient.send("此为要发送的数据内容");
    }

}
