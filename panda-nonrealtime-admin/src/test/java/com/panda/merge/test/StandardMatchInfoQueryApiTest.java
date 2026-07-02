package com.panda.merge.test;

import com.panda.merge.api.IStandardDataQueryApi;
import com.panda.merge.api.IStandardMatchInfoQueryApi;
import com.panda.merge.bo.StandardMatchInfoBO;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.StandardMatchInfoDTO;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 标准赛事信息查询接口测试类
 * @author :  tell
 * @since : 2020年9月6日13:48:55
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StandardMatchInfoQueryApiTest {

    @Autowired
    IStandardMatchInfoQueryApi standardMatchInfoQueryApi;

    @Test
    public void testQueryStandardMatchInfoPage() {
        PageModel<StandardMatchInfoDTO> page = new PageModel<>(100,1);
        StandardMatchInfoDTO dto = new StandardMatchInfoDTO();
        page.setData(dto);
        Request<PageModel<StandardMatchInfoDTO>> request = new Request<>();
        request.setData(page);
        request.setLinkId("tets"+System.currentTimeMillis());
        long beginTime = System.currentTimeMillis();
        Response<PageModel<List<StandardMatchInfoBO>>> response = standardMatchInfoQueryApi.queryStandardMatchInfoPage(request);
        long endTime = System.currentTimeMillis();
        System.out.println("测试分页查询标准赛事处理结束,共耗时 ：" + (endTime - beginTime) + ", response: " + response);
    }

    @Autowired
    IStandardDataQueryApi standardDataQueryApi;

    @Test
    public void testQuerySportMathTeamPage() {

        Request<PageModel<StandardMatchInfoDTO>> request = new Request<>();
        PageModel<StandardMatchInfoDTO> page = new PageModel<>(200,1);
        StandardMatchInfoDTO dto = new StandardMatchInfoDTO();
        dto.setEndTime(1614680940040L);            //1614676560000
        dto.setBeginTime(1614680869261L);
        page.setData(dto);
        request.setData(page);
        request.setLinkId("business_1366643307874226176");
        Response<PageModel<List<StandardMatchInfoBO>>> result = standardDataQueryApi.querySportMathTeamPage(request);
        System.out.println(result);
    }

    @Test
    public void testQueryStandardMatchInfoByThirdSourceId() {
        StandardMatchInfoDTO dto = new StandardMatchInfoDTO();
        dto.setDataSourceCode("BG");
        dto.setThirdSportId(4L);
        dto.setThirdMatchSourceId("7159416");
        Request<StandardMatchInfoDTO> request = new Request<>();
        request.setLinkId("tets"+System.currentTimeMillis());
        request.setData(dto);
        long beginTime = System.currentTimeMillis();
        Response<StandardMatchInfoBO> response = standardMatchInfoQueryApi.queryStandardMatchInfoByThirdSourceId(request);
        long endTime = System.currentTimeMillis();
        System.out.println("测试根据三方数据源赛事信息查询标准赛事处理结束,共耗时 ：" + (endTime - beginTime) + ", response: " + response);
    }

    @Test
    public void testQueryStandardMatchInfoById() {
        Request<Long> request = new Request<>();
        request.setLinkId("tets"+System.currentTimeMillis());
        Long id = 9693L;
        request.setData(id);
        long beginTime = System.currentTimeMillis();
        Response<StandardMatchInfoBO> response = standardMatchInfoQueryApi.queryStandardMatchInfoById(request);
        long endTime = System.currentTimeMillis();
        System.out.println("测试根据标准赛事ID查询标准赛事处理结束,共耗时 ：" + (endTime - beginTime) + ", response: " + response);
    }


}
