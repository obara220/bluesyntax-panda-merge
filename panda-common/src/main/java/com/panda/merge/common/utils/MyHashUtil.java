package com.panda.merge.common.utils;

public class MyHashUtil {

    /**
     * FNV-1a 64位哈希
     * */
    public static long fnv1aHash64(String input) {
        final long FNV_64_PRIME = 0x100000001b3L;
        long hash = 0xcbf29ce484222325L; // FNV offset basis

        for (int i = 0; i < input.length(); i++) {
            hash ^= input.charAt(i);
            hash *= FNV_64_PRIME;
        }

        return hash & 0x7FFFFFFFFFFFFFFFL;
    }

    public static void main(String[] args) {
        System.out.println("FF133211 → " + fnv1aHash64("FF133211"));
        System.out.println("FF133222 → " + fnv1aHash64("FF133222"));
        System.out.println("VV12354566 → " + fnv1aHash64("VV12354566"));
        System.out.println("BB12558899 → " + fnv1aHash64("BB12558899"));
    }

}
