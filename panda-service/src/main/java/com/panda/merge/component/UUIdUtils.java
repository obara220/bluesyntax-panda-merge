package com.panda.merge.component;

import com.panda.merge.common.utils.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @author :  Jimmy
 * @Project Name :  panda_data_realtime_marketodds
 * @Package Name :  com.panda.sport.data.realtime.utils
 * @Description :  TODO
 * @Date: 2020-02-08 19:01
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Component
public class UUIdUtils {
    private static long workerId = 0;
    public static Long getId() {
        return IdWorker.getId();
    }

    @Autowired
    public void setWorkerId(@Value("${main.workerId}") long workerId) {
        UUIdUtils.workerId = workerId;
        IdWorker.initSequence(workerId);
    }

    private static Long getProcessId(long nacosWorkerId) {
        try {
            Random random = new Random();//默认构造方法
            int i = random.nextInt(32);
            log.info("雪花算法初始化WorkerId:" + i);
            return (long)i;
        } catch (Exception e) {
            log.info("雪花算法初始化WorkerId失败=" + nacosWorkerId, e);
            return nacosWorkerId;
        }
    }
    public static void main(String[] args) {
//        String addr = "255.255.255.253";
//        String serverIpStr = addr.replace(".","");
//        long serverIpNum = Long.valueOf(serverIpStr);
//        long id = serverIpNum % (32 + 1);
//        System.out.println(id);
        /*for(int i=0; i < 10000; i++){
            UUIdUtils.workerId = 13;
            IdWorker.initSequence(workerId);

            System.out.println(IdWorker.getId());
        }*/
        Set<Long> list = new HashSet<>();
        for (int i=0;i<10000;i++)
        {
            String s = "1263549"+i;
            long hash = (("1263549".hashCode() & 0xffffffffL) << 6L)
                    ^ (("_"+i).hashCode() & 0xffffffffL);
            list.add(hash);
            System.out.println(hash);
        }

        System.out.println(list.size());

    }
}
