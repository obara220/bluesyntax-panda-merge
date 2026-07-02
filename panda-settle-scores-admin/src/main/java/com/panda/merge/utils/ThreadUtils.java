//package com.panda.merge.utils;
//
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.Random;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author :  dorich
// * @project Name :  panda_data_service
// * @package Name :  com.panda.sports.manager.utils
// * @description :  TODO
// * @date: 2019-10-31 11:41
// * @modificationHistory Who    When    What
// * --------  ---------  --------------------------
// */
//@Data
//@Slf4j
//public class ThreadUtils {
//    /**
//     * 当前服务器核数
//     */
//    private final static Integer cpuCount = Runtime.getRuntime().availableProcessors();
//
//    /**
//     * 最佳的线程数 = CPU可用核心数 / (1 - 阻塞系数)
//     */
//    private final static Integer bestPoolSize = (int) (cpuCount / (1 - 0.9));
//
//    /**
//     * 线程空闲周期
//     */
//    private final static Long keepLive = 100L;
//
//    /**
//     * 任务队列最大长度
//     */
//    private final static Integer queueTaskNum = 100;
//
//    /**
//     * 线程池       new ThreadPoolExecutor(1,bestPoolSize,);
//     */
//    private static ThreadPoolExecutor fixedThreadPool = new ThreadPoolExecutor(Math.min(2, cpuCount / 2), bestPoolSize,
//            keepLive, TimeUnit.SECONDS, new LinkedBlockingQueue<>(queueTaskNum));
//
//    /**
//     * 提交任务到线程池
//     *
//     * @param r
//     * @return boolean  成功提交返回true：提交失败返回false
//     * @description TODO
//     * @author dorich
//     * @date 2019/10/31 14:54
//     **/
//    public static boolean addTaskThreadPool(Runnable r) {
//        if (fixedThreadPool.getQueue().size() < queueTaskNum) {
//            fixedThreadPool.submit(r);
//            return true;
//        }
//        log.warn("任务队列已满,无法提交.");
//        return false;
//    }
//
//    /**
//     * 提交任务到线程池
//     *
//     * @param r
//     * @return boolean  成功提交返回true：提交失败返回false
//     * @description TODO
//     * @author dorich
//     * @date 2019/10/31 14:54
//     **/
//    public static void addTaskThreadPool(Runnable r, String taskName) {
//        while (fixedThreadPool.getQueue().size() >= queueTaskNum) {
//            try {
//                // 20%的几率 会打印日志
//                if(new Random().nextInt(100) < 50) {
//                    log.info(taskName + "----sleep");
//                }
//                Thread.sleep(50);
//            } catch (Exception e) {
//
//            }
//        }
//        if(new Random().nextInt(100) < 5) {
//            log.info("taskName:" + taskName);
//        }
//        fixedThreadPool.submit(r);
//        return;
//    }
//
//    public static void addTaskThreadPool(Runnable r, String taskName, boolean noWait) {
//        if (!noWait) {
//            addTaskThreadPool(r, taskName);
//        } else {
//            fixedThreadPool.submit(r);
//        }
//    }
//}
