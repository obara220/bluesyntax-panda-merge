package com.panda.merge.test;

import com.panda.merge.api.IStandardSportPlayerQueryApi;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.StandardSportPlayerDTO;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 标准球员分页查询测试
 * @author :  tell
 * @since : 2020年9月6日13:48:55
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StandardSportPlayerQueryApiTest {

    @Autowired
    IStandardSportPlayerQueryApi standardSportPlayerQueryApi;

    @Test
    public void tesPageHelper() {
        PageModel<StandardSportPlayerDTO> page = new PageModel<>(10,1);
        StandardSportPlayerDTO dto = new StandardSportPlayerDTO();
//        dto.setModifyTime(1L);
        dto.setThirdSourcePlayerId("sr:player:924754");
        dto.setDataSourceCode("SR");
        dto.setThirdSportId(2L);
        page.setData(dto);
        Request<PageModel<StandardSportPlayerDTO>> request = new Request<>();
        request.setData(page);
        request.setLinkId("tetsPageHelper"+System.currentTimeMillis());
        long beginTime = System.currentTimeMillis();
        Response response = standardSportPlayerQueryApi.queryStandardSportPlayerByUpdateTime(request);
        long endTime = System.currentTimeMillis();
        System.out.println("测试标准球员分页查询处理结束,共耗时 ：" + (endTime - beginTime) + ", response: " + response);
    }




}
