//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.panda.merge.monitor;

import com.alibaba.druid.util.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.lang3.tuple.Pair;

public class DruidMonitorUtil {
    public static final int SQL_MAX_LENGTH = 2000;
    public static final int ERROR_MESSAGE_MAX_LENGTH = 2000;
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final Charset DEFAULT_CHARSET_OBJ = Charset.forName("UTF-8");

    public DruidMonitorUtil() {
    }

    public static final void setFiledValue(Object obj, String fieldName, Object fieldValue) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, fieldValue);
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }
    }

    public static final Object invokeFieldMethod(Object obj, String fieldName, String methodName, Pair<Class<?>, ?>... methodParams) {
        try {
            Class<?>[] paramTypes = new Class[methodParams.length];
            Object[] paramValues = new Object[methodParams.length];

            for(int i = 0; i < methodParams.length; ++i) {
                paramTypes[i] = (Class)methodParams[i].getLeft();
                paramValues[i] = methodParams[i].getRight();
            }

            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object fieldValue = field.get(obj);
            Method method = fieldValue.getClass().getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);
            return method.invoke(fieldValue, paramValues);
        } catch (Exception var9) {
            throw new RuntimeException(var9);
        }
    }

    public static final String encodeMessage(String message) throws IOException {
        if (StringUtils.isEmpty(message)) {
            return message;
        } else {
            byte[] data = gzip(message.getBytes(DEFAULT_CHARSET_OBJ));
            return Base64.getEncoder().encodeToString(data);
        }
    }

    public static final String decodeMessage(String message) throws IOException {
        if (StringUtils.isEmpty(message)) {
            return message;
        } else {
            byte[] dec = Base64.getDecoder().decode(message);
            byte[] data = unGZip(dec);
            return new String(data, DEFAULT_CHARSET_OBJ);
        }
    }

    public static final byte[] gzip(byte[] bytes) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        byte[] rs = gzip((InputStream)bis);
        bis.close();
        return rs;
    }

    public static final byte[] gzip(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        gzip(in, baos);
        byte[] rs = baos.toByteArray();
        baos.close();
        return rs;
    }

    public static final void gzip(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[8192];
        GZIPOutputStream gzip = new GZIPOutputStream(out);

        int r;
        while((r = in.read(buffer, 0, buffer.length)) != -1) {
            gzip.write(buffer, 0, r);
        }

        gzip.close();
    }

    public static final byte[] unGZip(byte[] bytes) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        byte[] rs = unGZip((InputStream)bis);
        bis.close();
        return rs;
    }

    public static final byte[] unGZip(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        unGZip(in, baos);
        byte[] rs = baos.toByteArray();
        baos.close();
        return rs;
    }

    public static final void unGZip(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[8192];
        GZIPInputStream gzip = new GZIPInputStream(in);

        int r;
        while((r = gzip.read(buffer, 0, buffer.length)) != -1) {
            out.write(buffer, 0, r);
        }

        gzip.close();
    }

    public static String getLinuxLocalIp() {
        String ip ="";
        try {
            ip = System.getenv("POD_ID");
            if(org.apache.commons.lang3.StringUtils.isNotBlank(ip)){
                return ip;
            }
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                String name = intf.getName();
                if (!name.contains("docker") && !name.contains("lo")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            String ipaddress = inetAddress.getHostAddress();
                            if (!ipaddress.contains("::") && !ipaddress.contains("0:0:") && !ipaddress.contains("fe80")) {
                                ip = ipaddress;
                            }
                        }
                    }
                }
            }

        } catch (SocketException ex) {
            ip = "127.0.0.1";
        }
        return ip;
    }
}
