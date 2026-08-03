package com.panda.merge;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.panda.merge.dto.I18nItemDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchTeamDTO;
import com.panda.merge.dto.ThirdMatchTeamRelationDTO;
import com.panda.merge.dto.nonrealttime.put.ThirdMatchInfoDTO;
import com.panda.merge.rocketmq.processor.ThirdMatchInfoProcessor;
import com.panda.merge.service.ThirdMatchInfoService;
import com.panda.merge.service.VirtualBatchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class ThirdMatchInfoConsumerTest {


    @Autowired
    ThirdMatchInfoProcessor thirdMatchInfoProcessor;
    @Autowired
    VirtualBatchService virtualBatchService;
    @Autowired
    ThirdMatchInfoService thirdMatchInfoService;

    @Test
    void testThirdGlobalStatusss() {
        Request<List<ThirdMatchInfoDTO>> request = new Request<>();
        request.setLinkId("RB_ac12b2f420230215051805232378535029c6_2");
        /**
         * 赛事资料
         * {"linkId":"RB_ac12b2f420230215051805232378535029c6_3","data":
         * [{"thirdMatchSourceId":"1779076","sourceTournamentId":"158","sportId":1,"dataSourceCode":"RB","thirdRegionId":"20","beginTime":1676459100000,"matchLength":0,
         * "matchStatus":"0","homeAwayInfo":"ACS Flacara Horezu VS ACS Viitorul Pandurii Targu Jiu",
         * "neutralGround":0,"liveOddSupport":1,"active":1,"booked":1,"createTime":1676452860000}],
         * "dataSourceTime":1676452860000}
         * */
        ThirdMatchInfoDTO item = new ThirdMatchInfoDTO();
        item.setThirdMatchSourceId("1779076");
        item.setSourceTournamentId("158");
        item.setSportId(1L);
        item.setDataSourceCode("RB");
        item.setThirdRegionId("20");
        item.setBeginTime(1676462400000L);
        item.setMatchLength(0);
        item.setMatchStatus("0");
        item.setHomeAwayInfo("ACS Flacara Horezu VS ACS Viitorul Pandurii Targu Jiu");
        item.setNeutralGround(0);
        item.setLiveOddSupport(1);
        item.setActive(1);
        item.setBooked(1);
        item.setCreateTime(System.currentTimeMillis());
        /**
         * 球队资料
         * "matchTeamList":[
         *
         * {"thirdTeamId":"47948","name":"ACS Flacara Horezu","type":7,"coach":"","statium":"home",
         * "teamNameList":[
         * {"languageType":"en","text":"ACS Flacara Horezu"},
         * {"languageType":"zs","text":"ACS Flacara Horezu"}
         * ],
         * "matchTeamRelation":
         * {"matchPosition":"home","teamNameRecord":"ACS Flacara Horezu","remark":""}
         * ,"countryId":"49","countryName":"Romania","logoUrl":"","remark":""},
         *
         *
         * {"thirdTeamId":"41270","name":"ACS Viitorul Pandurii Targu Jiu","type":7,"coach":"","statium":"away",
         * "teamNameList":[
         * {"languageType":"en","text":"ACS Viitorul Pandurii Targu Jiu"},
         * {"languageType":"zs","text":"ACS Viitorul Pandurii Targu Jiu"}
         * ],
         * "matchTeamRelation":{"matchPosition":"away","teamNameRecord":"ACS Viitorul Pandurii Targu Jiu","remark":""},
         * "countryId":"49","countryName":"Romania","logoUrl":"","remark":""}
         * ]

         * */
        ThirdMatchTeamDTO homeTeam = new ThirdMatchTeamDTO();
        homeTeam.setThirdTeamId("47948");
        homeTeam.setName("ACS Flacara Horezu");
        homeTeam.setType(7);
        homeTeam.setStatium("away");
        homeTeam.setCountryId("49");
        homeTeam.setCountryName("49");

        I18nItemDTO i18n1 = new I18nItemDTO();
        i18n1.setLanguageType("en");
        i18n1.setText("ACS Flacara Horezu");
        I18nItemDTO i18n2 = new I18nItemDTO();
        i18n2.setLanguageType("zs");
        i18n2.setText("ACS Flacara Horezu");
        homeTeam.setTeamNameList(Lists.newArrayList(i18n1,i18n2));

        ThirdMatchTeamRelationDTO relation = new ThirdMatchTeamRelationDTO();
        relation.setMatchPosition("away");
        relation.setTeamNameRecord("ACS Flacara Horezu");
        homeTeam.setMatchTeamRelation(relation);

        ThirdMatchTeamDTO awayTeam = new ThirdMatchTeamDTO();
        awayTeam.setThirdTeamId("41270");
        awayTeam.setName("ACS Viitorul Pandurii Targu Jiu");
        awayTeam.setType(7);
        awayTeam.setStatium("home");
        awayTeam.setCountryId("49");
        awayTeam.setCountryName("49");

        I18nItemDTO i18n3 = new I18nItemDTO();
        i18n3.setLanguageType("en");
        i18n3.setText("ACS Viitorul Pandurii Targu Jiu");
        I18nItemDTO i18n4 = new I18nItemDTO();
        i18n4.setLanguageType("zs");
        i18n4.setText("ACS Viitorul Pandurii Targu Jiu");
        awayTeam.setTeamNameList(Lists.newArrayList(i18n3,i18n4));

        ThirdMatchTeamRelationDTO relation2 = new ThirdMatchTeamRelationDTO();
        relation2.setMatchPosition("home");
        relation2.setTeamNameRecord("ACS Viitorul Pandurii Targu Jiu");
        awayTeam.setMatchTeamRelation(relation2);

        item.setMatchTeamList(Lists.newArrayList(homeTeam,awayTeam));

        List<ThirdMatchInfoDTO>  list = new ArrayList<>();
        list.add(item);
        request.setData(list);
        thirdMatchInfoProcessor.processMatchData(request);
    }

    public static void main(String[] args) {

    }

}
