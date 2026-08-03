package com.panda.merge.common.utils;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@SuppressWarnings("all")
@Slf4j
public class Gzip {

    public static void main(String[] args) throws Exception {
        JSONObject object = new JSONObject();
        object.put("aaa",222);
        System.out.println( Gzip.compress(object.toJSONString()));
    }

    /**
     * 数据解压缩
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] decompress(byte[] data) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 解压缩
        decompress(bais, baos);
        data = baos.toByteArray();
        baos.flush();
        baos.close();
        bais.close();
        return data;
    }

    /**
     * 数据解压缩
     *
     * @param is
     * @param os
     * @throws Exception
     */
    public static void decompress(InputStream is, OutputStream os) throws Exception {
        GZIPInputStream gis = new GZIPInputStream(is);
        int count;
        byte data[] = new byte[BUFFER];
        while ((count = gis.read(data, 0, BUFFER)) != -1) {
            os.write(data, 0, count);
        }
        gis.close();
    }

    public static byte[] compress(byte[] data) throws Exception {
        if (data == null || data.length == 0) {
            return new byte[0];
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(data);
        gzip.close();
        gzip.flush();
        return out.toByteArray();
    }


    /**
     * 加密
     * @param data
     * @return
     * @throws Exception
     */
    public static String compress(String data) throws Exception {
        if (data == null || data.length() == 0) {
            return data;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(data.getBytes("UTF-8"));
            gzip.close();
            gzip.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gzip != null) {
                gzip.close();
            }
            if (out != null) {
                out.close();
            }
        }
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }

    //decompress str
    public static String decompress(String dataStr) throws Exception {
        if (dataStr == null || dataStr.length() == 0) {
            return dataStr;
        }
        byte[] dataArr = Base64.getMimeDecoder().decode(dataStr.getBytes(StandardCharsets.US_ASCII));
        ByteArrayInputStream bais = new ByteArrayInputStream(dataArr);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 解压缩
        decompress(bais, baos);
        String data = baos.toString();
        baos.flush();
        baos.close();
        bais.close();
        return data;
    }


    static final int BUFFER = 10240;

    /**
     * 数据压缩
     *
     * @param is
     * @param os
     * @throws Exception
     */
    public static void compress(InputStream is, OutputStream os) throws Exception {
        GZIPOutputStream gos = new GZIPOutputStream(os);
        int count;
        byte data[] = new byte[BUFFER];
        while ((count = is.read(data, 0, BUFFER)) != -1) {
            gos.write(data, 0, count);
        }
        gos.finish();
        gos.flush();
        gos.close();
    }
}