package com.panda.merge.utils;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator  {

    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    // 由于JS最多识别16位长度，因此这里控制长度不超过16位，这里控制为16位
    private static int ID_LENGTH = 16;


    public static Long nextId() {
        //  生成最大4位随技术
        int i2 = ThreadLocalRandom.current().nextInt(9999);
        String timeStr = String.valueOf(System.currentTimeMillis());
        // 取出时间串前面相同的部分
        timeStr = timeStr.substring(5);
        // 递增生成最大9999的递增ID
        if (atomicInteger.get() == 9999) {
            atomicInteger.set(0);
        }
        int i1 = atomicInteger.getAndIncrement();
        String id = timeStr.concat(String.valueOf(i2)).concat(i1+"");
        // 严格控制ID长度，如果过长 从最前面截取
        if (id.length() > ID_LENGTH) {
            // 计算多了多少位
            int surplusLenth = id.length() - ID_LENGTH;
            id = id.substring(surplusLenth);
        }
        return Long.parseLong(id);
    }

//    public static void main(String[] args) {
//        CustomIdGenerator customIdGenerator=new CustomIdGenerator();
//        customIdGenerator.nextId(null).longValue();
//        final CountDownLatch latch=new CountDownLatch(1000000);
//        Set<Long> set=new ConcurrentHashSet<>();
//        for(int i=0;i<1000000;i++){
//            final int x=i;
//            Thread thread=new Thread(){
//                @Override
//                public void run() {
//                    for(int j=0;j<1;j++){
//                        Number number = customIdGenerator.nextId(new Object());
//                        long l = number.longValue();
//                        boolean add = set.add(l);
//                        System.out.println(number);
//                        if(!add) {
//                            System.out.println("重复"+l);
//                        }
//                    }
//                    latch.countDown();
//                }
//            };
//            thread.start();
//        }
//
//        try {
//            latch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
}