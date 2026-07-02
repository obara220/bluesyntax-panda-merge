package com.panda.merge.test;

import com.google.common.collect.Lists;
import com.panda.merge.api.IThirdMatchInfoQueryApi;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.StandardMatchInfoDTO;
import com.panda.merge.dto.nonrealttime.query.QueryThirdSportTournamentDTO;
import com.panda.merge.dto.nonrealttime.query.ThirdMatchInfoDTO;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 三方赛事信息测试类
 * @author :  tell
 * @since : 2020年9月6日13:48:55
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ThirdMatchInfoQueryApiTest {

    @Autowired
    IThirdMatchInfoQueryApi thirdMatchInfoQueryApi;

    
    @Test
    public void testQueryThirdMatchInfoByThirdSourceId() {
        ThirdMatchInfoDTO dto = new ThirdMatchInfoDTO();
        dto.setDataSourceCode("TS");
        dto.setThirdSportId(1L);
//        dto.setThirdMatchSourceId("-1");
        Request<ThirdMatchInfoDTO> request = new Request<>();
        request.setLinkId("tets"+System.currentTimeMillis());
        request.setData(dto);
        long beginTime = System.currentTimeMillis();
        Response response = thirdMatchInfoQueryApi.queryThirdMatchInfoByThirdSourceId(request);
        long endTime = System.currentTimeMillis();
        System.out.println("测试根据三方数据源赛事ID查询三方赛事信息结束,共耗时 ：" + (endTime - beginTime) + ", response: " + response);
    }

    @Test
    public void queryThirdSportTournamentList() {
        QueryThirdSportTournamentDTO dto = new QueryThirdSportTournamentDTO();
        dto.setDataSourceCode("TS");
        dto.setStandardTournamentIds(Lists.newArrayList(273L,1L));
        Request<QueryThirdSportTournamentDTO> request = new Request<>();
        request.setLinkId("tets"+System.currentTimeMillis());
        request.setData(dto);
        long beginTime = System.currentTimeMillis();
        Response response = thirdMatchInfoQueryApi.queryThirdSportTournamentList(request);
        long endTime = System.currentTimeMillis();
        System.out.println("测试获取三方联赛列表信息结束,共耗时 ：" + (endTime - beginTime) + ", response: " + response);
    }

    @Test
    public void queryThirdMatchHistoryStatisticsPage() {
        PageModel<StandardMatchInfoDTO> page = new PageModel<>(100,1);
        StandardMatchInfoDTO dto = new StandardMatchInfoDTO();
        dto.setBeginTime(1612951200000L);
//        dto.setEndTime(System.currentTimeMillis());
        dto.setThirdSportId(1L);
        page.setData(dto);
        Request<PageModel<StandardMatchInfoDTO>> request = new Request<>();
        request.setData(page);
        request.setLinkId("tets"+System.currentTimeMillis());
        long beginTime = System.currentTimeMillis();
        Response response = thirdMatchInfoQueryApi.queryThirdMatchHistoryStatisticsPage(request);
        long endTime = System.currentTimeMillis();
        System.out.println("测试分页三方赛事历史统计列表信息结束,共耗时 ：" + (endTime - beginTime) + ", response: " + response);
    }


}
