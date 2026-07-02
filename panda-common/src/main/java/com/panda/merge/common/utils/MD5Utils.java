package com.panda.merge.common.utils;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by zijun.song on 2015/7/1.
 */
public class MD5Utils {

    public static String str;
    public static final String EMPTY_STRING = "";

    public static final String BASIC_STR = "14";//让生成的id长度保持一致

    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
    private final static char[] hexDigitsChar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * 转换字节数组为16进制字串
     *
     * @param b 字节数组
     * @return 16进制字串
     */
    public static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    private static Long getMD5(String orgString) {
        String s = null;
        try {
            byte[] source = orgString.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source);
            byte tmp[] = md.digest();
            char str[] = new char[16];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = tmp[i];
                //只取高位
                //System.out.println("getMD5 : "+hexDigitsChar[(byte0 >>> 4 & 0xf)%10]);
                str[k++] = hexDigitsChar[(byte0 >>> 4 & 0xf) % 10];
            }
            s = BASIC_STR + new String(str);  // 换后的结果转换为字符串
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Long.valueOf(s);
    }

    public static String MD5Encode(String origin) {
        String resultString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(origin
                    .getBytes("UTF-8")));
        } catch (Exception ex) {
        }
        return resultString;
    }

    public static Long getLongByMD5(String source) {
        return getMD5(source);
    }

    public static void main(String[] args) {
        /*long start = System.currentTimeMillis();
        Set<Long> list = new HashSet<>();
        for (int i=0;i<1000000;i++)
        {
            long s = Long.valueOf(getLongByMD5((i+"")));
            System.out.println("=========="+s);
            if (!list.contains(s))
            {
                list.add(s);
            }
        }
        System.out.println(list.size()+":"+(System.currentTimeMillis() - start));*/
        System.out.println(System.currentTimeMillis());
        Map<Long, String> map = new HashMap<Long, String>();
        while (true) {
            String value = randomStr();
            Long key = getLongByMD5(value);
            if (map.containsKey(key) && !map.get(key).equals(value)) {
                System.out.println(value);
                System.out.println(map.get(key));
                System.out.println(System.currentTimeMillis());
                break;
            }
            map.put(key, value);
        }
    }

    //64
    public static final char[] strChar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '_', ':'};

    private static String randomStr() {
        Random random = new Random();
        int totalLength = 5 + random.nextInt(50);
        String result = "";
        for (int i = 0; i < totalLength; i++) {
            int index = random.nextInt(strChar.length);
            result += strChar[index];
        }
        return result;
    }
}
