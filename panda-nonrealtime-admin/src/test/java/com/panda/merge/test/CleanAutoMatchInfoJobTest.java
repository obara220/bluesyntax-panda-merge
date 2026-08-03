package com.panda.merge.test;

import com.panda.merge.job.CleanAutoMatchInfoJob;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 验证自动化赛事定时清除
 * @author aldrich
 * @since 2024/10/6
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CleanAutoMatchInfoJobTest {

    @Autowired
    private CleanAutoMatchInfoJob cleanAutoMatchInfoJob;

    @Test
    public void test_VideoScoresCheckJob(){
        //String param = "{\"sportId\":\"1\",\"type\":\"0\"}";
        String param = "{\"dayNum\":2,\"matchNum\":1000,\"standardTeamId\":\"94166,96388\"}";
        cleanAutoMatchInfoJob.execute(param);
    }
}
