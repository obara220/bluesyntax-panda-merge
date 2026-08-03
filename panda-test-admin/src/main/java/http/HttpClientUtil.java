package http;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 基于httpclient 4.5.9的版本封装成
 * 连接池模式的
 * 此类为单例模式保证当前全局只存在一个
 * 连接池的存在
 * 为了防止因为一些被动断开的连接
 * 导致服务器产生大量的CLOSE_WAIT
 * 的状态出现,从而将应用的连接池占满导致服务假死
 * 需要通过再开一个单独线程额外清理
 * 这些没有关闭的连接
 * @author kane
 * @since 2019-08-04
 * @version v1.0.0
 */
@Slf4j
public class HttpClientUtil {

    /**
     * 设置一些缺省的连接池配置
     */
    private static final int DEFAULT_POOL_MAX_TOTAL = 200;
    private static final int DEFAULT_POOL_MAX_PER_ROUTE = 200;

    private static final int DEFAULT_CONNECT_TIMEOUT = 5000;
    private static final int DEFAULT_CONNECT_REQUEST_TIMEOUT = 5000;
    private static final int DEFAULT_SOCKET_TIMEOUT = 20000;

    /**
     * 连接池的最大连接数
     */
    private final int maxTotal;
    /**
     * 连接池按route配置的最大连接数
     */
    private final int maxPerRoute;
    /**
     * tcp connect的超时时间
     */
    private final int connectTimeout;
    /**
     * 从连接池获取连接的超时时间
     */
    private final int connectRequestTimeout;
    /**
     * tcp io的读写超时时间
     */
    private final int socketTimeout;

    private static final HttpClientUtil instance = new HttpClientUtil() ;

    public static HttpClientUtil getInstance(){
        return instance;
    }

    private HttpClientUtil() {
        this(
                HttpClientUtil.DEFAULT_POOL_MAX_TOTAL,
                HttpClientUtil.DEFAULT_POOL_MAX_PER_ROUTE,
                HttpClientUtil.DEFAULT_CONNECT_TIMEOUT,
                HttpClientUtil.DEFAULT_CONNECT_REQUEST_TIMEOUT,
                HttpClientUtil.DEFAULT_SOCKET_TIMEOUT
        );
    }

    private HttpClientUtil(
            int maxTotal,
            int maxPerRoute,
            int connectTimeout,
            int connectRequestTimeout,
            int socketTimeout
    ) {
        this.maxTotal = maxTotal;
        this.maxPerRoute = maxPerRoute;
        this.connectTimeout = connectTimeout;
        this.connectRequestTimeout = connectRequestTimeout;
        this.socketTimeout = socketTimeout;

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();

        gcm = new PoolingHttpClientConnectionManager(registry);
        gcm.setMaxTotal(this.maxTotal);
        gcm.setDefaultMaxPerRoute(this.maxPerRoute);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(this.connectTimeout)                     // 设置连接超时
                .setSocketTimeout(this.socketTimeout)                       // 设置读取超时
                .setConnectionRequestTimeout(this.connectRequestTimeout)    // 设置从连接池获取连接实例的超时
                .build();

        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClient = httpClientBuilder
                .setConnectionManager(gcm)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    private PoolingHttpClientConnectionManager gcm;

    private CloseableHttpClient httpClient ;

    public String doPost(String apiUrl, Map<String, Object> params) {
        return this.doPost(apiUrl, Collections.EMPTY_MAP, params);
    }

    public String doPost(String apiUrl,
                         Map<String, String> headers,
                         Map<String, Object> params
    ) {
        HttpPost httpPost = new HttpPost(apiUrl);

        // *) 配置请求headers
        if ( headers != null && headers.size() > 0 ) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }

        // *) 配置请求参数
        if ( params != null && params.size() > 0 ) {
            HttpEntity entityReq = getUrlEncodedFormEntity(params);
            httpPost.setEntity(entityReq);
        }


        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            if (response == null || response.getStatusLine() == null) {
                return null;
            }

            int statusCode = response.getStatusLine().getStatusCode();
            if ( statusCode == HttpStatus.SC_OK ) {
                HttpEntity entityRes = response.getEntity();
                if ( entityRes != null ) {
                    return EntityUtils.toString(entityRes, "UTF-8");
                }
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                }
            }
        }
        return null;

    }

    private HttpEntity getUrlEncodedFormEntity(Map<String, Object> params) {
        List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry
                    .getValue().toString());
            pairList.add(pair);
        }
        return new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8"));
    }

}
