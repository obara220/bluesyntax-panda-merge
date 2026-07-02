package com.panda.merge.test;

import com.google.common.collect.Lists;
import com.panda.merge.api.IThirdRankingInfoQueryApi;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.nonrealttime.query.QueryThirdRankingInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * 查询三方联赛下排行榜单数据测试类
 * @author :  tell
 * @since : 2020年9月6日13:48:55
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ThirdRankingInfoQueryApiTest {

    @Autowired
    IThirdRankingInfoQueryApi thirdRankingInfoQueryApi;

    @Test
    public void testQueryThirdSportTeamRanking(){
        Request<PageModel<QueryThirdRankingInfoDTO>> request = new Request<>();
        QueryThirdRankingInfoDTO dto = new QueryThirdRankingInfoDTO();
        dto.setBeginTime(1614657600000L);

        PageModel<QueryThirdRankingInfoDTO> pageModel = new PageModel<>();
        pageModel.setData(dto);
        request.setData(pageModel);
        request.setLinkId("tets"+System.currentTimeMillis());

        long beginTime = System.currentTimeMillis();
        Response response = thirdRankingInfoQueryApi.queryThirdSportTeamRanking(request);
        long endTime = System.currentTimeMillis();
        System.out.println("测试查询三方联赛下球队榜单排行榜信息处理结束,共耗时 ：" + (endTime - beginTime) + ", response: " + response);
    }

    @Test
    public void testQueryThirdSportPlayerRanking(){
        Request<PageModel<QueryThirdRankingInfoDTO>> request = new Request<>();
        QueryThirdRankingInfoDTO dto = new QueryThirdRankingInfoDTO();
        dto.setBeginTime(1614657600000L);

        PageModel<QueryThirdRankingInfoDTO> pageModel = new PageModel<>();
        pageModel.setData(dto);
        request.setData(pageModel);
        request.setLinkId("tets"+System.currentTimeMillis());

        long beginTime = System.currentTimeMillis();
        Response response = thirdRankingInfoQueryApi.queryThirdSportPlayerRanking(request);
        long endTime = System.currentTimeMillis();
        System.out.println("测试查询三方联赛下球员榜单排行榜信息处理结束,共耗时 ：" + (endTime - beginTime) + ", response: " + response);
    }

}
