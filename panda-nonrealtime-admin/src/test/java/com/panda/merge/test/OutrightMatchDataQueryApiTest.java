package com.panda.merge.test;

import com.panda.merge.api.IOutrightMatchDataQueryApi;
import com.panda.merge.dto.OutrightMatchInfoDTO;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 查询接口测试类
 * @author :  tell
 * @since : 2020年9月6日13:48:55
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OutrightMatchDataQueryApiTest {

    @Autowired
    IOutrightMatchDataQueryApi outrightMatchDataQueryApi;

    @Test
    public void testQueryOutrihtMatch() {
        PageModel<OutrightMatchInfoDTO> page = new PageModel<>(100,1);
        OutrightMatchInfoDTO dto = new OutrightMatchInfoDTO();
        dto.setBeginTime(1602553470001L);
        page.setData(dto);
        Request<PageModel<OutrightMatchInfoDTO>> request = new Request<>();
        request.setData(page);
        request.setLinkId("tets"+System.currentTimeMillis());
        long beginTime = System.currentTimeMillis();
        Response response = outrightMatchDataQueryApi.queryOutrihtMatch(request);
        long endTime = System.currentTimeMillis();
        System.out.println("测试分页查询冠军赛事列表处理结束,共耗时 ：" + (endTime - beginTime) + ", response: " + response);
    }




}
