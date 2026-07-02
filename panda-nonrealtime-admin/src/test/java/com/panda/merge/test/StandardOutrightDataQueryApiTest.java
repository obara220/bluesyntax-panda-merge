package com.panda.merge.test;

import com.panda.merge.api.IOutrightMatchDataQueryApi;
import com.panda.merge.dto.OutrightMatchInfoDTO;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.Request;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author : nonhung
 * @project Name : panda-merge
 * @package Name : com.panda.merge.test
 * @description : TODO
 * @date: 2020-10-06 17:58
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StandardOutrightDataQueryApiTest {

    @Autowired
    private IOutrightMatchDataQueryApi iOutrightMatchDataQueryApi;
    Request<PageModel<OutrightMatchInfoDTO>> request = new Request<>();
    PageModel<OutrightMatchInfoDTO> pageModel = new PageModel<>();

    @Test
    public void queryOutrihtMatch() {
        pageModel.setSize(100);
        pageModel.setCurrent(1);
        pageModel.setTotal(0);
        OutrightMatchInfoDTO outrightMatchInfoDTO = new OutrightMatchInfoDTO();
        outrightMatchInfoDTO.setBeginTime(1596347619000L);
        outrightMatchInfoDTO.setEndTime(1601985885214L);
        pageModel.setData(outrightMatchInfoDTO);
        request.setData(pageModel);
        request.setLinkId("business_b368d90ba4cf424c842e137393b5c6a5");
        iOutrightMatchDataQueryApi.queryOutrihtMatchCategory(request);
    }


    @Test
    public void queryOutrihtMatchCategory() {
        //   iOutrightMatchDataQueryApi.queryOutrihtMatch()
    }
}
