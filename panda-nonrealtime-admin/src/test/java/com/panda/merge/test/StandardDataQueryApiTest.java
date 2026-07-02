package com.panda.merge.test;

import com.alibaba.fastjson.JSON;
import com.panda.merge.api.IStandardDataQueryApi;
import com.panda.merge.api.IStandardMatchInfoQueryApi;
import com.panda.merge.bo.StandardMatchInfoBO;
import com.panda.merge.bo.StandardSportTypeBO;
import com.panda.merge.dto.*;
import com.panda.merge.dto.nonrealttime.query.ThirdMatchInfoDTO;
import com.panda.merge.dubbo.StandardDataQueryApiImpl;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.service.DataSourceService;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.ThirdMarketCategoryFieldService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 查询接口测试类
 * @author :  tell
 * @since : 2020年9月6日13:48:55
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StandardDataQueryApiTest {

    @Autowired
    private ThirdMarketCategoryFieldService thirdMarketCategoryFieldService;

    @Autowired
    IStandardDataQueryApi standardDataQueryApi;

    @Autowired
    IStandardMatchInfoQueryApi standardMatchInfoQueryApi;

    @Test
    public void testQueryThirdMarketCategoryFieldDetail(){
        List<ThirdMarketCategoryFieldDetail> thirdMarketCategoryFieldDetails = thirdMarketCategoryFieldService.queryThirdMarketCategoryFieldDetail("BG", 78L);
        System.out.println(JSON.toJSON(thirdMarketCategoryFieldDetails));
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }

    @Test
    public void testQueryStandardMatchInfoPage() {
        Request<PageModel<StandardMatchInfoDTO>> request = new Request<>();
        StandardMatchInfoDTO standardSportTypeDTO = new StandardMatchInfoDTO();
        standardSportTypeDTO.setBeginTime(1614657600000L);
        standardSportTypeDTO.setEndTime(1614744000000L);
        PageModel<StandardMatchInfoDTO> pageModel = new PageModel<>();
        pageModel.setData(standardSportTypeDTO);
        request.setData(pageModel);
        Response<PageModel<List<StandardMatchInfoBO>>> response =  standardMatchInfoQueryApi.queryStandardMatchInfoPage(request);
    }

    @Test
    public void testQueryStandardSportTypePage() {
        StandardSportTypeDTO dto = new StandardSportTypeDTO();
        dto.setBeginTime(0L);
        dto.setEndTime(System.currentTimeMillis());
        Request<StandardSportTypeDTO> request = new Request<>();
        request.setData(dto);
        request.setLinkId("tets"+System.currentTimeMillis());
        long beginTime = System.currentTimeMillis();
        Response response = standardDataQueryApi.queryStandardSportTypePage(request);
        long endTime = System.currentTimeMillis();
        System.out.println("测试查询标准运动类型列表处理结束,共耗时 ：" + (endTime - beginTime) + ", response: " + response);
    }


    @Test
    public void testQuerySportTournamentPage() {
        PageModel<StandardSportTournamentDTO> page = new PageModel<>(100,1);
        StandardSportTournamentDTO dto = new StandardSportTournamentDTO();
        dto.setBeginTime(0L);
        page.setData(dto);
        Request<PageModel<StandardSportTournamentDTO>> request = new Request<>();
        request.setData(page);
        request.setLinkId("tets"+System.currentTimeMillis());
        long beginTime = System.currentTimeMillis();
        Response response = standardDataQueryApi.querySportTournamentPage(request);
        long endTime = System.currentTimeMillis();
        System.out.println("测试分页查询标准联赛列表处理结束,共耗时 ：" + (endTime - beginTime) + ", response: " + response);
    }


    @Test
    public void testQuerySportMathTeamPage() {
        PageModel<StandardMatchInfoDTO> page = new PageModel<>(100,1);
        StandardMatchInfoDTO dto = new StandardMatchInfoDTO();
        dto.setBeginTime(1602903600000L);
//        dto.setEndTime(System.currentTimeMillis());
        page.setData(dto);
        Request<PageModel<StandardMatchInfoDTO>> request = new Request<>();
        request.setData(page);
        request.setLinkId("tets"+System.currentTimeMillis());
        long beginTime = System.currentTimeMillis();
        Response response = standardDataQueryApi.querySportMathTeamPage(request);
        long endTime = System.currentTimeMillis();
        System.out.println("测试分页查询标准赛程（球队）列表处理结束,共耗时 ：" + (endTime - beginTime) + ", response: " + response);
    }

    @Test
    public void testQueryStandardSportRegionPage() {
        PageModel<StandardSportRegionDTO> page = new PageModel<>(100,1);
        StandardSportRegionDTO dto = new StandardSportRegionDTO();
        dto.setBeginTime(0L);
        dto.setEndTime(System.currentTimeMillis());
        page.setData(dto);
        Request<PageModel<StandardSportRegionDTO>> request = new Request<>();
        request.setData(page);
        request.setLinkId("tets"+System.currentTimeMillis());
        long beginTime = System.currentTimeMillis();
        Response response = standardDataQueryApi.queryStandardSportRegionPage(request);
        long endTime = System.currentTimeMillis();
        System.out.println("测试分页查询体育区域列表处理结束,共耗时 ：" + (endTime - beginTime) + ", response: " + response);
    }

    @Autowired
    StandardDataQueryApiImpl standardDataQueryApiImpl;

    @Test
    public void testQueryStandardSportMarketCategoryPage() {
        PageModel<StandardSportMarketCategoryDTO> page = new PageModel<>(100,1);
        StandardSportMarketCategoryDTO dto = new StandardSportMarketCategoryDTO();
        dto.setBeginTime(1697528172000L);
        dto.setEndTime(System.currentTimeMillis());
        page.setData(dto);
        Request<PageModel<StandardSportMarketCategoryDTO>> request = new Request<>();
        request.setData(page);
        request.setLinkId("tets"+System.currentTimeMillis());
        long beginTime = System.currentTimeMillis();
        Response response = standardDataQueryApiImpl.queryStandardSportMarketCategoryPage(request);
        long endTime = System.currentTimeMillis();
        System.out.println("测试分页查询标准玩法玩，法投注项列表处理结束,共耗时 ：" + (endTime - beginTime) + ", response: " + response);
    }

    @Test
    public void testQueryThirdSportMarketPage() {
        PageModel<ThirdSportMarketDTO> page = new PageModel<>(100,1);
        ThirdSportMarketDTO dto = new ThirdSportMarketDTO();
        dto.setBeginTime(1602208800000L);
//        dto.setEndTime(System.currentTimeMillis());
        page.setData(dto);
        Request<PageModel<ThirdSportMarketDTO>> request = new Request<>();
        request.setData(page);
        request.setLinkId("tets"+System.currentTimeMillis());
        long beginTime = System.currentTimeMillis();
        Response response = standardDataQueryApi.queryThirdSportMarketPage(request);
        long endTime = System.currentTimeMillis();
        System.out.println("测试分页查询三方盘口列表处理结束,共耗时 ：" + (endTime - beginTime) + ", response: " + response);
    }

    @Test
    public void testQueryThirdSportMarketPageForReport() {
        PageModel<ThirdSportMarketDTO> page = new PageModel<>(100,1);
        ThirdSportMarketDTO dto = new ThirdSportMarketDTO();
        dto.setBeginTime(0L);
        dto.setEndTime(System.currentTimeMillis());
        page.setData(dto);
        Request<PageModel<ThirdSportMarketDTO>> request = new Request<>();
        request.setData(page);
        request.setLinkId("tets"+System.currentTimeMillis());
        long beginTime = System.currentTimeMillis();
        Response response = standardDataQueryApi.queryThirdSportMarketPageForReport(request);
        long endTime = System.currentTimeMillis();
        System.out.println("测试分页查询三方盘口列表(统计使用)处理结束,共耗时 ：" + (endTime - beginTime) + ", response: " + response);
    }

    @Test
    public void testQueryThirdMatchInfoPage() {
        PageModel<ThirdMatchInfoDTO> page = new PageModel<>(10,1);
        ThirdMatchInfoDTO dto = new ThirdMatchInfoDTO();
        dto.setBeginTime(1602208800000L);
//        dto.setEndTime(System.currentTimeMillis());
        page.setData(dto);
        Request<PageModel<ThirdMatchInfoDTO>> request = new Request<>();
        request.setData(page);
        request.setLinkId("tets"+System.currentTimeMillis());
        long beginTime = System.currentTimeMillis();
        Response response = standardDataQueryApi.queryThirdMatchInfoPage(request);
        long endTime = System.currentTimeMillis();
        System.out.println("测试分页查询三方赛事列表处理结束,共耗时 ：" + (endTime - beginTime) + ", response: " + response);
    }

    @Test
    public void testQueryDataSourcePage() {
        DataSourceDTO dto = new DataSourceDTO();
        dto.setBeginTime(0L);
        dto.setEndTime(System.currentTimeMillis());
        Request<DataSourceDTO> request = new Request<>();
        request.setData(dto);
        request.setLinkId("tets"+System.currentTimeMillis());
        long beginTime = System.currentTimeMillis();
        Response response = standardDataQueryApi.queryDataSourcePage(request);
        long endTime = System.currentTimeMillis();
        System.out.println("测试查询数据来源列表处理结束,共耗时 ：" + (endTime - beginTime) + ", response: " + response);
    }

    @Autowired
    public DataSourceService dataSourceService;

    @Test
    public void testGetDataSourceCodes(){
        Integer commerce = 1;
        List<String> dataSourceCodess = dataSourceService.getItemList().stream().filter(obj->obj.getCommerce().equals(commerce)).map(obj -> obj.getCode()).collect(Collectors.toList());
        System.out.println(dataSourceCodess);
    }

    @Autowired
    public StandardMatchInfoService standardMatchInfoService;

    @Test
    public void testGetStandardMatchInfo(){
        StandardMatchInfo item = standardMatchInfoService.getItem(96481L);
        StandardMatchInfoDetail detailItem = standardMatchInfoService.getDetailItem(96481L);
        System.out.println(item);
        System.out.println(detailItem);
    }


}
