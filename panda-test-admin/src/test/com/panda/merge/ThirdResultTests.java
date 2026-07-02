package com.panda.merge;

import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisService;
import com.panda.merge.dao.StandardMatchInfoDao;
import com.panda.merge.dao.StandardSportTeamDao;
import com.panda.merge.dto.StandardMatchInfoDTO;
import com.panda.merge.dto.StandardSportTeamDetail;
import com.panda.merge.mapper.*;
import com.panda.merge.model.*;
import com.panda.merge.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
class ThirdResultTests {


    @Autowired
    private TUserMapper tUserMapper;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private ThirdMatchTeamRelationService thirdMatchTeamRelationService;
    @Autowired
    private ThirdSportMarketOddsService thirdSportMarketOddsService;
    @Autowired
    private StandardSportMarketOddsService standardSportMarketOddsService;
    @Autowired
    private StandardSportMarketService standardSportMarketService;
    @Autowired
    private ThirdSportMarketService thirdSportMarketService;
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Test
    void testMatchTeamRelation(){
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(2703329L);
        System.out.println(standardMatchInfo);
    }
    @Test
    void testSelect()
    {
        TUserExample tUserExample = new TUserExample();
        tUserMapper.deleteByExample(tUserExample);
        List<TUser> list = tUserMapper.selectByExample(tUserExample);
        for (TUser tUser : list)
        {
            System.out.println(tUser.getId()+"-"+tUser.getName());
        }

    }

    @Autowired
    ThirdMatchInfoMapper thirdMatchInfoMapper;
    @Test
    void testAdd()
    {
        /*TUser user1 = new TUser();
        user1.setId(Long.valueOf(14));
        user1.setName("BC");
        tUserMapper.insert(user1);

        TUser user2 = new TUser();
        user2.setId(Long.valueOf(15));
        user2.setName("SR");
        tUserMapper.insert(user2);

        TUser user3 = new TUser();
        user3.setId(Long.valueOf(16));
        user3.setName("BG");
        tUserMapper.insert(user3);*/
        TUserExample tUserExample = new TUserExample();
        tUserExample.createCriteria();//.andNameIn(Arrays.asList("SR","BG"));
        List<TUser> tuer = tUserMapper.selectByExample(tUserExample);
        System.out.println("============" + tuer.size());

       /*ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(1380192790631239682L);
        thirdMatchInfoMapper.deleteByPrimaryKey(1380192790631239682L);
        thirdMatchInfo.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        thirdMatchInfoMapper.insertSelective(thirdMatchInfo);
        System.out.println("================");*/
    }
    @Test
    void getOdds()
    {
        ThirdSportMarketOdds t =
                thirdSportMarketOddsService.getItem("SR","25352650_16_-1_1714",1454443070367772673L);
        System.out.println("=========");
    }

    @Autowired
    LanguageInternationMapper languageInternationMapper;
    @Autowired
    MatchAutoAssociationMapper matchAutoAssociationMapper;
    @Test
    void delOdds()
    {
       /* Integer result =  thirdSportMarketOddsService.delOdds();
        System.out.println("=================="+result);*/
        LanguageInternationExample example = new LanguageInternationExample();
        example.createCriteria().andNameCodeIn(Arrays.asList(1045952L,3L,4L,5L));
        //long result = languageInternationMapper.countByExample(example);
        System.out.println("===============");
        languageInternationMapper.selectByExample(example);

       /* MatchAutoAssociationExample matchAutoAssociationExample = new MatchAutoAssociationExample();
        matchAutoAssociationExample.createCriteria();
        List<MatchAutoAssociation> list =  matchAutoAssociationMapper.selectByExample(matchAutoAssociationExample);*/
        System.out.println("================");
    }

    @Autowired
    private ThirdSportMarketMapper thirdSportMarketMapper;
    @Test
    void thirdSportMarketService()
    {
        ThirdSportMarket thirdSportMarket = thirdSportMarketService.getItem("BG:8192226:124923316:0");
        ThirdSportMarketExample example = new ThirdSportMarketExample();
        example.createCriteria().andIdEqualTo(thirdSportMarket.getId()).andDataSourceCodeEqualTo(thirdSportMarket.getDataSourceCode());
        thirdSportMarketMapper.deleteByExample(example);
        thirdSportMarketMapper.insert(thirdSportMarket);
    }



    @Test
    void standardSport()
    {
        List<StandardSportMarketOdds> result =  standardSportMarketOddsService.getMarketOddsByMatchIdList(Arrays.asList(1437682833434390530l,1437681333429645313l));
        standardSportMarketService.getItemList(2693501L);

        standardSportMarketOddsService.getItem("SR","1000005527_246_4.5_2__1714",1437681333429645313L);
        System.out.println("=================="+0);
    }
    @Autowired
    StandardMatchInfoDao standardMatchInfoDao;

    @Test
    void standardSportDao()
    {
        StandardMatchInfoDTO standardMatchInfoDTO = new StandardMatchInfoDTO();
        standardMatchInfoDao.getItemPageByModifyTime(standardMatchInfoDTO);
        System.out.println("=================="+0);
    }
    @Autowired
    StandardSportTeamDao standardSportTeamDao;
    @Test
    void standardSportTeamDao()
    {
        List<Long> standardTeamIds  = new ArrayList<>();
        standardTeamIds.add(1467L);
        //List<StandardSportTeamDetail> standardSportTeamDetails = standardSportTeamDao.getItemByStandardTeamIds(standardTeamIds);
        List<StandardSportTeamDetail> s = standardSportTeamDao.getItemByStandardMatchId(2693558L);
        System.out.println("=================="+0);
    }
    @Autowired
    RedisService redisService;
    @Test
    void testDel()
    {
        String redisKey = "saveTheLastCategoryStatusToReidsMap:"+122+"_"+"CODE";
        redisService.set(redisKey,1);
        Object obj = redisService.get(redisKey);
        redisService.del(redisKey);

        System.out.println("========");
    }

    @Autowired
    ConfigMarketCategoryPlaceService configMarketCategoryPlaceService;
    @Test
    void testBatchInsert()
    {
        List<ConfigMarketCategoryPlace> c = configMarketCategoryPlaceService.getItemListCache(2953213L,2L);
        System.out.println("========");
    }
}
