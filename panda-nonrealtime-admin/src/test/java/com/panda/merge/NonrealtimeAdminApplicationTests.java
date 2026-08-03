package com.panda.merge;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ThirdSportTournamentDTO;
import com.panda.merge.rocketmq.processor.ThirdSportTournamentProcessor;
import com.panda.merge.service.ThirdSportTypeService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
class NonrealtimeAdminApplicationTests {

    @Autowired
    private ThirdSportTournamentProcessor thirdSportTournamentProcessor;
    @Resource
    private ThirdSportTypeService thirdSportTypeService;

    public List<Long> B01_Code_list = new ArrayList<>();

    public List<Long> N01_Code_list = new ArrayList<>();

    public List<Long> N02_Code_list = new ArrayList<>();

    @Test
    public void contextLoads() {
        Request<List<ThirdSportTournamentDTO>> request = new Request<>();
        request.setLinkId("ac12b2a22021021917455800736d91d6");
        request.setDataSourceTime(1613727958007L);
        List<ThirdSportTournamentDTO> paramsList = Lists.newLinkedList();
        String paramStr = "{\"thirdTournamentSourceId\":\"sr:simple_tournament:99788\",\"fatherTournamentId\":\"sr:tournament:21880\",\"simpleFlage\":\"1\",\"name\":\"Villena, Singles W-Itf-Esp-04A\",\"sportId\":5,\"dataSourceCode\":\"SR\",\"tournamentNameList\":[{\"languageType\":\"zs\",\"text\":\"Villena, Singles W-Itf-Esp-04A\"},{\"languageType\":\"en\",\"text\":\"Villena, Singles W-Itf-Esp-04A\"}],\"currentRoundType\":\"cup\",\"tournamentRoundName\":\"round_of_32\",\"sportRegionId\":\"213\",\"sportRegionName\":\"ITF女子\",\"createTime\":1613699158007}";
        ThirdSportTournamentDTO thirdSportTournamentDTO = JSONObject.parseObject(paramStr,ThirdSportTournamentDTO.class);
        paramsList.add(thirdSportTournamentDTO);
        request.setData(paramsList);
        Response response = thirdSportTournamentProcessor.processTournamentData(request);
        System.out.println(response);
    }


    public static void main(String[] args) {
        List<String> list = new LinkedList<>();
        System.out.println(CollectionUtils.isEmpty(list));
    }
    @PostConstruct
    public void init()
    {
        B01_Code_list.add(Long.valueOf(thirdSportTypeService.getThirdSportId(StandardSportTypeEnum.FootBall.code,DataSourceCodeEnum.BG.code)));
        B01_Code_list.add(Long.valueOf(thirdSportTypeService.getThirdSportId(StandardSportTypeEnum.Basketball.code,DataSourceCodeEnum.BG.code)));
        N01_Code_list.add(Long.valueOf(thirdSportTypeService.getThirdSportId(StandardSportTypeEnum.FootBall.code,DataSourceCodeEnum.N01.code)));
        N01_Code_list.add(Long.valueOf(thirdSportTypeService.getThirdSportId(StandardSportTypeEnum.Basketball.code,DataSourceCodeEnum.N01.code)));
        N02_Code_list.add(Long.valueOf(thirdSportTypeService.getThirdSportId(StandardSportTypeEnum.FootBall.code,DataSourceCodeEnum.N02.code)));
        N02_Code_list.add(Long.valueOf(thirdSportTypeService.getThirdSportId(StandardSportTypeEnum.Basketball.code,DataSourceCodeEnum.N02.code)));
    }
}
