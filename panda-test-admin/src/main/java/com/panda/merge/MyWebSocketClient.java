package com.panda.merge;

import org.java_websocket.client.DefaultSSLWebSocketClientFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import javax.net.ssl.*;
import java.net.URI;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class MyWebSocketClient extends WebSocketClient {
    public MyWebSocketClient(URI serverURI) {
        super(serverURI,new Draft_17());
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("====================open");
    }

    @Override
    public void onMessage(String s) {
        System.out.println("====================onMessage"+s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        System.out.println("====================onClose"+s);
    }

    @Override
    public void onError(Exception e) {
        System.out.println("====================onError"+e);
    }

    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    };
    public static void trustAllHosts(MyWebSocketClient myWebSocketClient)
    {
        TrustManager[] trustManagers = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;//new X509Certificate[]{};
            }
        }

        };
        try{
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null,trustManagers,new SecureRandom());
            myWebSocketClient.setWebSocketFactory(new DefaultSSLWebSocketClientFactory(sc));
        }catch (Exception e)
        {
            System.out.println("==============ex"+e);
        }
    }
}
