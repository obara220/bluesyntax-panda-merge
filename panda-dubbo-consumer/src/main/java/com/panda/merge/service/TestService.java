package com.panda.merge.service;

import com.alibaba.fastjson.JSON;
import com.panda.merge.api.IStandardDataQueryApi;
import com.panda.merge.api.ITradeMarketConfigApi;
import com.panda.merge.dto.*;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/9/12 <br>
 * @see com.panda.merge.service <br>
 */
@Service
public class TestService {
    @DubboReference
    ITradeMarketConfigApi tradeMarketConfigApi;


//    public void test(){
//        String requestData ="{\"linkId\":\"cc12b2a2202009261556469639a6d58f\",\"data\":\"T\",\"dataSourceTime\":0,\"dataSourceCode\":\"SR\"}";
//        String data="{\"active\":1,\"addition1\":\"SR\",\"addition2\":\"1\",\"configId\":\"22349457\",\"level\":3,\"marketStatus\":1,\"modifyTime\":1601119533065,\"sourceSystem\":3,\"targetId\":\"22906173\"}";
//        Request<TradeMarketConfigDTO> request = new Request<>();
//        request = JSON.parseObject(requestData, request.getClass());
//        TradeMarketConfigDTO dto = JSON.parseObject(data, TradeMarketConfigDTO.class);
//        request.setData(dto);
//        tradeMarketConfigApi.putTradeMarketConfig(request);
//    }


    @DubboReference
    IStandardDataQueryApi standardDataQueryApi;

    public void test() {
        PageModel<StandardMatchInfoDTO> page = new PageModel<>(10,1);
        StandardMatchInfoDTO dto = new StandardMatchInfoDTO();
        dto.setBeginTime(1602129600000L);
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

//    public void test(){
//        StandardSportTypeDTO dto = new StandardSportTypeDTO();
//        dto.setBeginTime(1602129600000L);
////        dto.setEndTime(System.currentTimeMillis());
//        Request<StandardSportTypeDTO> request = new Request<>();
//        request.setData(dto);
//        request.setLinkId("tets"+System.currentTimeMillis());
//        long beginTime = System.currentTimeMillis();
//        Response response = standardDataQueryApi.queryStandardSportTypePage(request);
//        long endTime = System.currentTimeMillis();
//        System.out.println("测试查询标准运动类型列表处理结束,共耗时 ：" + (endTime - beginTime) + ", response: " + response);
//    }

//    public void test() {
//        PageModel<StandardSportMarketCategoryDTO> page = new PageModel<>(50,6);
//        StandardSportMarketCategoryDTO dto = new StandardSportMarketCategoryDTO();
//        dto.setBeginTime(0L);
////        dto.setEndTime(System.currentTimeMillis());
//        page.setData(dto);
//        Request<PageModel<StandardSportMarketCategoryDTO>> request = new Request<>();
//        request.setData(page);
//        request.setLinkId("tets"+System.currentTimeMillis());
//        long beginTime = System.currentTimeMillis();
//        Response response = standardDataQueryApi.queryStandardSportMarketCategoryPage(request);
//        long endTime = System.currentTimeMillis();
//        System.out.println("测试分页查询标准玩法玩，法投注项列表处理结束,共耗时 ：" + (endTime - beginTime) + ", response: " + response);
//    }

}
