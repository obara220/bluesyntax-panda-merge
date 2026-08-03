package com.panda.merge.test;

import com.panda.merge.job.VideoScoresCheckJob;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author aldrich
 * 2024/2/17 10:40
 * 动画比分不一致优化测试类
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class VideoScoresCheckJobTest {

    @Autowired
    private VideoScoresCheckJob videoScoresCheckJob;

    @Test
    public void test_VideoScoresCheckJob(){
        //String param = "{\"sportId\":\"1\",\"type\":\"0\"}";
        String param = "{\"sportId\":\"1\",\"type\":\"1\"}";
        videoScoresCheckJob.execute(param);
    }
}
