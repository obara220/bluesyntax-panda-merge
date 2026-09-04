package com.panda.merge;
import org.springframework.context.annotation.Lazy;
import com.alibaba.fastjson.JSON;
import com.panda.merge.api.I18nMarketCategoryApi;
import com.panda.merge.component.CommonAsyncService;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.*;
import com.panda.merge.dto.message.SoldMessage;
import com.panda.merge.dubbo.ThirdMarketStatusApiImpl;
import com.panda.merge.dubbo.TradeMarketConfigApiServiceImpl;
import com.panda.merge.dubbo.TradeMarketOddsApiServiceImpl;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarket;
import com.panda.merge.rocketmq.processor.*;
import com.panda.merge.service.StandardSportMarketService;
import com.panda.merge.service.ThirdMatchTeamRelationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.panda.merge.constant.ConstantSystem.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class OddsAdminTests {


    @Autowired
    private I18nMarketCategoryApi i18nMarketCategoryApi;



    /**
     * 使用RocketMq的生产者
     */
    @Autowired
    private DefaultMQProducer defaultMQProducer;

    @Autowired
    private StandardSportMarketService standardSportMarketService;
    @Lazy
    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;

    @Autowired
    SoldMessageToOddsProcessor soldMessageToOddsProcessor;

    @Autowired
    ThirdMarketResultProcessor thirdMarketResultProcessor;

    @Autowired
    ThirdBetCancelProcessor thirdBetCancelProcessor;

    @Autowired
    ThirdBetCancelRollbackProcessor thirdBetCancelRollbackProcessor;

    @Autowired
    ThirdBetSettlementRollbackProcessor thirdBetSettlementRollbackProcessor;

    @Autowired
    TradeMarketOddsApiServiceImpl tradeMarketOddsApiService;


    @Autowired
    TradeMarketConfigApiServiceImpl tradeMarketConfigApiService;

    @Autowired
    ThirdMarketStatusApiImpl thirdMarketStatusApi;

    @Autowired
    RedisService redisService;

    @Autowired
    private ThirdMatchTeamRelationService thirdMatchTeamRelationService;
    @Autowired
    CommonAsyncService commonAsyncService;

    @Test
    public void testGetAllThirdSportMarket()
    {

        String linkId = "11";
        StandardMatchInfo standardMatchInfo = new StandardMatchInfo();
        standardMatchInfo.setId(2715514L);
        Integer marketType = 0;
        Map<Long,String> longStringHashMap = new HashMap<>();
        longStringHashMap.put(1L,"SR");
        commonAsyncService.getAllThirdSportMarketList(linkId, standardMatchInfo, marketType, longStringHashMap );
    }
    @Test
    public void testStandardSportMarket(){
        List<String> strList = new ArrayList<>();
        strList.add("1626949939369_28_");
        strList.add("1626949939369_10_");
        strList.add("1626949939369_112_");
        List<StandardSportMarket> sr = standardSportMarketService.getItemByThirdMarketSourceIdsAndDataSourceCode(strList, "SR", 2729862L);
        System.out.println(JSON.toJSON(sr));
        System.out.println(">>>>>");
    }

    @Test
    public void testPlayer(){
        List<ThirdMatchTeamRelationDetail> list = thirdMatchTeamRelationService.getItemsByMatchId(1354951236584034305L);
        Map<String, String> map = new HashMap<>();
        for (ThirdMatchTeamRelationDetail item: list) {
            if(null == map.get(item.getThirdSourcePlayerId())){
                map.put(item.getThirdSourcePlayerId(),item.getMatchPosition());
            }
        }
        System.out.println(map.get("sr:player:608176"));
        System.out.println(map);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>");
        //Map<String, String> map = list.stream().collect(Collectors.toMap(ThirdMatchTeamRelationDetail::getThirdSourcePlayerId, ThirdMatchTeamRelationDetail::getMatchPosition,(oldValue, newValue)->newValue));

    }


    @Test
    public void testRedis() {
        for(int i=0;i<10000;i++){
            String key = RedisConfig.REDIS_KEY_LINKID + "THIRD_MATCH_MARKET_API" + FIX +"123456";
            Object res = redisService.get(key);
            if (!Objects.isNull(res)) {
                Integer num = Integer.valueOf(res.toString());
                if(num > THREE){
                    System.out.println(System.currentTimeMillis()+"序号："+i+":参数linkID重复:" + 123456);
                }else{
                    redisService.set(key,num + ONE,HOUR_1);
                }
            } else {
                redisService.set(key,TEN,HOUR_1);
            }
        }

    }

    @Test
    @Transactional
    @Rollback
    public void testInsertBatch() {
        Assert.assertEquals(5, "");
    }

    /**
     *  测试标准投注项名称多语言
     */
    @Test
    public void testInitI18nMarketCategory() {
        //i18nMarketCategoryApi.initI18nMarketCategory();
    }



    /**
     *  测试接收三方盘口数据
     */
    /*@Test
    public void testOutrightMarketOrder() {
        String data = "{\"linkId\":\"3jooiljhqwydmm5qvr7lzv_marketOrder\",\"data\":{\"marketOrderDTOList\":[{\"standardMarketId\":\"1320570808049643521\",\"marketOrderNumber\":3},{\"standardMarketId\":\"1320570808527794178\",\"marketOrderNumber\":4},{\"standardMarketId\":\"1320570808867532802\",\"marketOrderNumber\":1},{\"standardMarketId\":\"1320570813347049474\",\"marketOrderNumber\":13},{\"standardMarketId\":\"1320570813632262146\",\"marketOrderNumber\":5},{\"standardMarketId\":\"1320570813909086210\",\"marketOrderNumber\":8},{\"standardMarketId\":\"1320572401025331202\",\"marketOrderNumber\":9},{\"standardMarketId\":\"1320572401235046402\",\"marketOrderNumber\":2},{\"standardMarketId\":\"1320572401453150211\",\"marketOrderNumber\":12},{\"standardMarketId\":\"1320572401771917314\",\"marketOrderNumber\":0},{\"standardMarketId\":\"1328272967700361218\",\"marketOrderNumber\":6},{\"standardMarketId\":\"1328273310676856834\",\"marketOrderNumber\":10},{\"standardMarketId\":\"1328273602948804610\",\"marketOrderNumber\":7},{\"standardMarketId\":\"1328273915336110081\",\"marketOrderNumber\":11}],\"standardMatchId\":1320570806305005569},\"dataSourceTime\":1610693540267,\"dataSourceCode\":\"SR\",\"dataType\":null,\"operaterId\":null}";
        Request<OutrightMarketOrderMessage> request = new Request<>();
        request = JSON.parseObject(data, request.getClass());
        OutrightMarketOrderMessage marketOrderMessage = JSON.parseObject(JSON.toJSONString(request.getData()), OutrightMarketOrderMessage.class);
        request.setData(marketOrderMessage);
        outrightMarketOrderProcessor.processOutrightMarketOrder(request);

    }*/

    /**
     *  测试接收三方盘口数据
     */
    @Test
    public void testAccessMatchMarketData() {
        String data = "{\n" +
                "    \"linkId\": \"SR_ac12b2f6202109211736323026d111112\",\n" +
                "    \"data\": {\n" +
                "        \"sportId\": 1,\n" +
                "        \"thirdMatchSourceId\": \"29023556\",\n" +
                "        \"thirdTournamentSourceId\": \"sr:tournament:782\",\n" +
                "        \"dataSourceCode\": \"SR\",\n" +
                "        \"modifyTime\": 1632216994343,\n" +
                "        \"marketList\": [\n" +
                "\t\t{\n" +
                "                \"thirdMarketCategorySourceId\": \"SR:23\",\n" +
                "                \"thirdMarketSourceId\": \"29023556_23_sr:exact_goals:3+\",\n" +
                "                \"marketType\": 1,\n" +
                "                \"dataSourceCode\": \"SR\",\n" +
                "                \"status\": 0,\n" +
                "                \"oddsName\": \"Guizhou FC 准确进球\",\n" +
                "                \"addition1\": \"3\",\n" +
                "                \"modifyTime\": 1632216992330,\n" +
                "                \"numberOfWinners\": 1,\n" +
                "                \"i18nNames\": [\n" +
                "                    {\n" +
                "                        \"languageType\": \"en\",\n" +
                "                        \"text\": \"Guizhou FC exact goals\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"languageType\": \"zs\",\n" +
                "                        \"text\": \"Guizhou FC 准确进球\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"marketOddsList\": [\n" +
                "                    {\n" +
                "                        \"active\": 1,\n" +
                "                        \"oddsType\": \"0\",\n" +
                "                        \"addition1\": \"50017\",\n" +
                "                        \"thirdOddsFieldSourceId\": \"29023556_23_sr:exact_goals:3+_sr:exact_goals:3+:88\",\n" +
                "                        \"orderOdds\": 1,\n" +
                "                        \"oddsValue\": 210000,\n" +
                "                        \"originalOddsValue\": 246858,\n" +
                "                        \"thirdTempletSourceId\": \"None\",\n" +
                "                        \"dataSourceCode\": \"SR\",\n" +
                "                        \"modifyTime\": 1632216992330,\n" +
                "                        \"i18nNames\": [\n" +
                "                            {\n" +
                "                                \"languageType\": \"en\",\n" +
                "                                \"text\": \"0\"\n" +
                "                            },\n" +
                "                            {\n" +
                "                                \"languageType\": \"zs\",\n" +
                "                                \"text\": \"0\"\n" +
                "                            }\n" +
                "                        ],\n" +
                "                        \"extraInfo\": \"23Andsr:exact_goals:3+:88\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"active\": 1,\n" +
                "                        \"oddsType\": \"1\",\n" +
                "                        \"addition1\": \"50017\",\n" +
                "                        \"thirdOddsFieldSourceId\": \"29023556_23_sr:exact_goals:3+_sr:exact_goals:3+:89\",\n" +
                "                        \"orderOdds\": 2,\n" +
                "                        \"oddsValue\": 229000,\n" +
                "                        \"originalOddsValue\": 271457,\n" +
                "                        \"thirdTempletSourceId\": \"None\",\n" +
                "                        \"dataSourceCode\": \"SR\",\n" +
                "                        \"modifyTime\": 1632216992330,\n" +
                "                        \"i18nNames\": [\n" +
                "                            {\n" +
                "                                \"languageType\": \"en\",\n" +
                "                                \"text\": \"1\"\n" +
                "                            },\n" +
                "                            {\n" +
                "                                \"languageType\": \"zs\",\n" +
                "                                \"text\": \"1\"\n" +
                "                            }\n" +
                "                        ],\n" +
                "                        \"extraInfo\": \"23Andsr:exact_goals:3+:89\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"active\": 1,\n" +
                "                        \"oddsType\": \"2\",\n" +
                "                        \"addition1\": \"50017\",\n" +
                "                        \"thirdOddsFieldSourceId\": \"29023556_23_sr:exact_goals:3+_sr:exact_goals:3+:90\",\n" +
                "                        \"orderOdds\": 3,\n" +
                "                        \"oddsValue\": 480000,\n" +
                "                        \"originalOddsValue\": 608580,\n" +
                "                        \"thirdTempletSourceId\": \"None\",\n" +
                "                        \"dataSourceCode\": \"SR\",\n" +
                "                        \"modifyTime\": 1632216992330,\n" +
                "                        \"i18nNames\": [\n" +
                "                            {\n" +
                "                                \"languageType\": \"en\",\n" +
                "                                \"text\": \"2\"\n" +
                "                            },\n" +
                "                            {\n" +
                "                                \"languageType\": \"zs\",\n" +
                "                                \"text\": \"2\"\n" +
                "                            }\n" +
                "                        ],\n" +
                "                        \"extraInfo\": \"23Andsr:exact_goals:3+:90\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"active\": 1,\n" +
                "                        \"oddsType\": \"3+\",\n" +
                "                        \"addition1\": \"50017\",\n" +
                "                        \"thirdOddsFieldSourceId\": \"29023556_23_sr:exact_goals:3+_sr:exact_goals:3+:91\",\n" +
                "                        \"orderOdds\": 4,\n" +
                "                        \"oddsValue\": 1250000,\n" +
                "                        \"originalOddsValue\": 1607443,\n" +
                "                        \"thirdTempletSourceId\": \"None\",\n" +
                "                        \"dataSourceCode\": \"SR\",\n" +
                "                        \"modifyTime\": 1632216992330,\n" +
                "                        \"i18nNames\": [\n" +
                "                            {\n" +
                "                                \"languageType\": \"en\",\n" +
                "                                \"text\": \"3+\"\n" +
                "                            },\n" +
                "                            {\n" +
                "                                \"languageType\": \"zs\",\n" +
                "                                \"text\": \"3+\"\n" +
                "                            }\n" +
                "                        ],\n" +
                "                        \"extraInfo\": \"23Andsr:exact_goals:3+:91\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"extraInfo\": \"{\\\"variant\\\":\\\"sr:exact_goals:3+\\\"}\"\n" +
                "            }\n" +
                "            \n" +
                "\t\t]\n" +
                "\t\t}\n" +
                "\t\t}";
        Request<ThirdMatchMarketDTO> request = new Request<>();
        request = JSON.parseObject(data, request.getClass());
        ThirdMatchMarketDTO thirdMatchMarketDTO = JSON.parseObject(JSON.toJSONString(request.getData()), ThirdMatchMarketDTO.class);
        request.setData(thirdMatchMarketDTO);
        thirdMatchMarketProcessor.accessMatchMarketData(request);

    }

    /**
     * 操盘水差
     */
    @Test
    public void testPutTradeMarketAutoDiffConfig() {
        String data = "{\"data\":{\"diffConfigs\":[{\"diffValue\":0.2,\"marketCategoryId\":4,\"marketId\":1315918975666532354,\"oddType\":\"2\"},{\"diffValue\":0.0,\"marketCategoryId\":4,\"marketId\":1315918975666532354,\"oddType\":\"1\"},{\"diffValue\":0.0,\"marketCategoryId\":4,\"marketId\":1315918975666532354,\"oddType\":\"1\"}],\"marketConfigs\":[{\"marketCategoryId\":4,\"marketId\":1315918975666532354,\"maxOddsValue\":101.0,\"minOddsValue\":1.01}],\"matchId\":1422839},\"dataSourceTime\":1602590263519,\"linkId\":\"32e27feafb4c4d54bb1fd6f19411e612_trade2\"}";
        Request<TradeMarketAutoDiffConfigDTO> request = new Request<>();
        request = JSON.parseObject(data, request.getClass());
        TradeMarketAutoDiffConfigDTO tradeMarketAutoDiffConfigDTO = JSON.parseObject(JSON.toJSONString(request.getData()), TradeMarketAutoDiffConfigDTO.class);
        request.setData(tradeMarketAutoDiffConfigDTO);
        tradeMarketConfigApiService.putTradeMarketAutoDiffConfig(request);

    }

    @Test
    public void TestPutTradeMarketHeadGapConfig(){
        String data = "{\"data\":{\"marketHeadGap\":2.0,\"marketType\":1,\"standardCategoryId\":39,\"standardMatchInfoId\":1983577},\"dataSourceTime\":1610516195147,\"dataType\":\"putTradeMarketHeadGapConfig\",\"linkId\":\"a20d240283b84b7b847ec778cb8b9f1_trade\",\"operaterId\":10018}";
        Request<TradeMarketHeadGapConfigDTO> request = new Request<>();
        request = JSON.parseObject(data, request.getClass());
        TradeMarketHeadGapConfigDTO tradeMarketHeadGapConfigDTO =  JSON.parseObject(JSON.toJSONString(request.getData()), TradeMarketHeadGapConfigDTO.class);
        request.setData(tradeMarketHeadGapConfigDTO);
        tradeMarketConfigApiService.putTradeMarketHeadGapConfig(request);
    }
    /**
     * 操盘ui调配接口测试
     */
    @Test
    public void testPutTradeMarketUiConfig(){
        String data = "{\"data\":{\"marketType\":1,\"matchType\":\"0\",\"placeNum\":1,\"placeNumDiffConfigs\":[{\"diffValue\":0.1,\"marketCategoryId\":39,\"oddType\":\"2\",\"placeNum\":1},{\"diffValue\":0.1,\"marketCategoryId\":39,\"oddType\":\"2\",\"placeNum\":2}],\"standardCategoryId\":39,\"standardMatchInfoId\":2026749},\"dataSourceTime\":1614753258098,\"dataType\":\"putTradeMarketUiConfig\",\"linkId\":\"9ceff30ae5644e74b433b77ef8f84087_merge_trade3\",\"operaterId\":10018}";
        Request<TradeMarketUiConfigDTO> request = new Request<>();
        request = JSON.parseObject(data,request.getClass());
        TradeMarketUiConfigDTO tradeMarketUiConfigDTO = JSON.parseObject(JSON.toJSONString(request.getData()), TradeMarketUiConfigDTO.class);
        request.setData(tradeMarketUiConfigDTO);
        tradeMarketConfigApiService.putTradeMarketUiConfig(request);
    }
    @Test
    public void testputTradeMarketConfig(){
        String data = "{\"data\":{\"active\":1,\"configId\":\"match_id_1914620\",\"level\":3,\"marketStatus\":2,\"modifyTime\":1613460428873,\"operaterId\":10018,\"sourceSystem\":2,\"targetId\":\"1914620\"},\"dataSourceTime\":1613460428873,\"dataType\":\"putTradeMarketConfig\",\"linkId\":\"15a56086e00a4ff0b0a77cc45799321f_trade_status4\",\"operaterId\":10018}";
        Request<TradeMarketConfigDTO> request = new Request<>();
        request = JSON.parseObject(data,request.getClass());
        TradeMarketConfigDTO tradeMarketUiConfigDTO = JSON.parseObject(JSON.toJSONString(request.getData()), TradeMarketConfigDTO.class);
        request.setData(tradeMarketUiConfigDTO);
        Response s = tradeMarketConfigApiService.putTradeMarketConfig(request);
        System.out.println(JSON.toJSON(s));
    }
    /**
     * 操盘ui调配接口测试
     */
    @Test
    public void testPutTradeMarketPlaceConfig(){
        String data = "{\"data\":{\"marketPlaceDtlDTOList\":[{\"placeNum\":-1,\"placeNumStatus\":\"2\",\"standardCategoryId\":37},{\"placeNum\":-1,\"placeNumStatus\":\"2\",\"standardCategoryId\":38},{\"placeNum\":-1,\"placeNumStatus\":\"2\",\"standardCategoryId\":39},{\"placeNum\":-1,\"placeNumStatus\":\"2\",\"standardCategoryId\":40}],\"standardMatchInfoId\":1813838},\"dataSourceTime\":1608618563365,\"dataType\":\"putTradeMarketPlaceConfig\",\"linkId\":\"81b028b59cba4dd9ae20391107880c74_trade_status_place4\",\"operaterId\":-1}";
        Request<TradeMarketPlaceConfigDTO> request = new Request<>();
        request = JSON.parseObject(data,request.getClass());
        TradeMarketPlaceConfigDTO tradeMarketUiConfigDTO = JSON.parseObject(JSON.toJSONString(request.getData()), TradeMarketPlaceConfigDTO.class);
        request.setData(tradeMarketUiConfigDTO);
        tradeMarketConfigApiService.putTradeMarketPlaceConfig(request);
    }
    @Test
    public void testPutTranMarketMerginListConfig(){
        String data = "{\"data\":[{\"marketMarginDtlDTOList\":[{\"margin\":0.4,\"oddsType\":\"2\",\"timeFrame\":0}],\"marketType\":0,\"placeNum\":1,\"standardCategoryId\":37,\"standardMatchInfoId\":1376396},{\"marketMarginDtlDTOList\":[{\"margin\":0.3,\"oddsType\":\"Even\",\"timeFrame\":0}],\"marketType\":0,\"placeNum\":1,\"standardCategoryId\":40,\"standardMatchInfoId\":1376396}],\"dataSourceTime\":1603800936449,\"linkId\":\"1743499e609b437cbc2ce85c04a82aab_trade1\"}";
        Request<List<TradeMarketMarginConfigDTO>> request = new Request<>();
        request = JSON.parseObject(data,request.getClass());
        List<TradeMarketMarginConfigDTO> tradeMarketUiConfigDTOList = new ArrayList<>();
        List tradeMarketMarginConfigDTOs = JSON.parseObject(JSON.toJSONString(request.getData()), List.class);
        for(Object obj : tradeMarketMarginConfigDTOs){
            TradeMarketMarginConfigDTO tradeMarketUiConfigDTO = JSON.parseObject(JSON.toJSONString(obj), TradeMarketMarginConfigDTO.class);
            tradeMarketUiConfigDTOList.add(tradeMarketUiConfigDTO);
        }
        request.setData(tradeMarketUiConfigDTOList);
        tradeMarketConfigApiService.putTradeMarketMarginConfigList(request);
    }


    @Test
    public void testPutTradeMarketOdds() {
        String data = "{\"data\":{\"marketList\":[{\"addition1\":\"3\",\"addition2\":\"\",\"dataSourceCode\":\"SR\",\"marketCategoryId\":67,\"marketOddsList\":[{\"addition1\":\"0\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"dataSourceCode\":\"PA\",\"name\":\"其他\",\"nameExpressionValue\":\"3\",\"oddsFieldsTemplateId\":223,\"oddsType\":\"Other\",\"oddsValue\":330000,\"orderOdds\":3,\"originalOddsValue\":330000},{\"addition1\":\"0\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"dataSourceCode\":\"PA\",\"name\":\"吉森46人 3+\",\"nameExpressionValue\":\"3\",\"oddsFieldsTemplateId\":224,\"oddsType\":\"2And3+\",\"oddsValue\":330000,\"orderOdds\":2,\"originalOddsValue\":330000},{\"addition1\":\"0\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"dataSourceCode\":\"PA\",\"name\":\"波恩电信 3+\",\"nameExpressionValue\":\"3\",\"oddsFieldsTemplateId\":222,\"oddsType\":\"1And3+\",\"oddsValue\":204000,\"orderOdds\":1,\"originalOddsValue\":204000}],\"marketType\":1,\"oddsName\":\"第4 一刻钟 - 输赢比数\",\"placeNum\":1,\"placeNumStatus\":1,\"status\":1,\"thirdMarketSourceStatus\":0}],\"standardMatchInfoId\":1379049},\"dataSourceTime\":1608690819521,\"dataType\":\"putTradeMarketOdds\",\"linkId\":\"5853bc68238c4f70a3d66c5251eb1ee6_odds_trade1\",\"operaterId\":-1}";
        Request<StandardMatchMarketDTO> request = new Request<>();
        request = JSON.parseObject(data, request.getClass());
        StandardMatchMarketDTO standardMatchMarketDTO = JSON.parseObject(JSON.toJSONString(request.getData()), StandardMatchMarketDTO.class);
        request.setData(standardMatchMarketDTO);
        tradeMarketOddsApiService.putTradeMarketOdds(request);

    }

    @Test
    public void testSoldMessageToOdds(){

        String data = "{\"data\":{\"dataSource\":\"BG\",\"isOutRight\":\"0\",\"marketCategoryIds\":{1:\"BG\",2:\"BG\",3:\"BG\",4:\"BG\",5:\"BG\",6:\"BG\",7:\"BG\",8:\"BG\",9:\"BG\",10:\"BG\",11:\"BG\",12:\"BG\",13:\"BG\",14:\"BG\",15:\"BG\",16:\"BG\",17:\"BG\",18:\"BG\",19:\"BG\",20:\"BG\",21:\"BG\",22:\"BG\",23:\"BG\",24:\"BG\",25:\"BG\",26:\"BG\",27:\"BG\",28:\"BG\",29:\"BG\",30:\"BG\",31:\"BG\",32:\"BG\",33:\"BG\",34:\"BG\",36:\"BG\",42:\"BG\",43:\"BG\",68:\"BG\",69:\"BG\",70:\"BG\",71:\"BG\",72:\"BG\",73:\"BG\",74:\"BG\",75:\"BG\",76:\"BG\",77:\"BG\",78:\"BG\",79:\"BG\",80:\"BG\",81:\"BG\",82:\"BG\",83:\"BG\",84:\"BG\",85:\"BG\",86:\"BG\",87:\"BG\",88:\"BG\",89:\"BG\",90:\"BG\",91:\"BG\",92:\"BG\",93:\"BG\",94:\"BG\",95:\"BG\",96:\"BG\",97:\"BG\",98:\"BG\",99:\"BG\",100:\"BG\",101:\"BG\",102:\"BG\",103:\"BG\",104:\"BG\",105:\"BG\",106:\"BG\",107:\"BG\",108:\"BG\",109:\"BG\",110:\"BG\",111:\"BG\",112:\"BG\",113:\"BG\",114:\"BG\",115:\"BG\",116:\"BG\",117:\"BG\",118:\"BG\",119:\"BG\",120:\"BG\",121:\"BG\",122:\"BG\",123:\"BG\",124:\"BG\",125:\"BG\",126:\"BG\",127:\"BG\",128:\"BG\",129:\"BG\",130:\"BG\",131:\"BG\",132:\"BG\",133:\"BG\",134:\"BG\",135:\"BG\",136:\"BG\",137:\"BG\",138:\"BG\",139:\"BG\",140:\"BG\",141:\"BG\",142:\"BG\",143:\"BG\",144:\"BG\",148:\"BG\",149:\"BG\",150:\"BG\",151:\"BG\",152:\"BG\",222:\"BG\",223:\"BG\",224:\"BG\",225:\"BG\",226:\"BG\",227:\"BG\",228:\"BG\",229:\"BG\",230:\"BG\",231:\"BG\",232:\"BG\",233:\"BG\",234:\"BG\",235:\"BG\",236:\"BG\",237:\"BG\",238:\"BG\",239:\"BG\",240:\"BG\",241:\"BG\"},\"marketType\":1,\"matchId\":2005134,\"riskManagerCode\":\"PA\"},\"linkId\":\"0f8e092846774602a12e887dfef1b9d7_trade_sold\"}";
        Request<SoldMessage> request = new Request<>();
        request = JSON.parseObject(data, request.getClass());
        //org.springframework.messaging.converter.MappingJackson2MessageConverter.convertFromInternal(data);
        SoldMessage soldMessage = JSON.parseObject(JSON.toJSONString(request.getData()), SoldMessage.class);
         request.setData(soldMessage);
        soldMessageToOddsProcessor.soldMessageToOdds(request);
    }


    @Test
    public void send() throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        for (int i = 0; i < 10000; i++) {
            String msg = "demo msg test" + i;
            //log.info("开始发送消息："+msg);
            Message sendMsg = new Message("DemoTopic", "DemoTag", msg.getBytes());
            //默认3秒超时
            SendResult sendResult = defaultMQProducer.send(sendMsg);
            // log.info("消息发送响应信息："+sendResult.toString());
        }

    }

    //PA开售处理   操盘界面
    @Test
    public void soldMessageTest() {
        String data = "{\"data\":{\"marketCategoryIds\":[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,36,42,43,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,148,149,150,151,152,222,223,224,225,226,227,228,229,230,231,232,233,234,235,236,237,238,239,240,241],\"marketType\":1,\"matchId\":18046},\"linkId\":\"8c05e4446c924f5faff17d82eaeb7134_trade_sold\"}";

        Request<SoldMessage> request = new Request<>();
        request = JSON.parseObject(data, request.getClass());
        SoldMessage soldMessage = JSON.parseObject(JSON.toJSONString(request.getData()), SoldMessage.class);
        request.setData(soldMessage);
        soldMessageToOddsProcessor.soldMessageToOdds(request);


    }

    //   赛果
    @Test
    public void thirdMarketResultApiTest() {
        String data = "{\"linkId\":\"ac12b2f620200908194556992fccf4c8\",\"data\":{\"sportId\":1,\"thirdMatchSourceId\":\"23323959\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557028,\"marketList\":[{\"thirdMarketCategorySourceId\":\"SR:10\",\"thirdMarketSourceId\":\"23323959_10_\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":2,\"oddsName\":\"双胜彩\",\"modifyTime\":1599565556993,\"marketOddsList\":[],\"extraInfo\":\"{}\"},{\"thirdMarketCategorySourceId\":\"SR:19\",\"thirdMarketSourceId\":\"23323959_19_2.5\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":0,\"oddsName\":\"邦德公牛鲨 合计\",\"addition1\":\"2.5\",\"modifyTime\":1599565556993,\"marketOddsList\":[{\"active\":1,\"oddsType\":\"Under\",\"addition1\":\"556750\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_19_2.5_13\",\"orderOdds\":1,\"oddsValue\":127000,\"originalOddsValue\":134668,\"thirdTempletSourceId\":\"SR:19:13\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556993,\"extraInfo\":\"19And13\"},{\"active\":1,\"oddsType\":\"Over\",\"addition1\":\"556750\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_19_2.5_12\",\"orderOdds\":2,\"oddsValue\":330000,\"originalOddsValue\":388448,\"thirdTempletSourceId\":\"SR:19:12\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556993,\"extraInfo\":\"19And12\"}],\"extraInfo\":\"{\\\"total\\\":\\\"2.5\\\"}\"},{\"thirdMarketCategorySourceId\":\"SR:546\",\"thirdMarketSourceId\":\"23323959_546_\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":0,\"oddsName\":\"双胜彩\\u0026两队得分\",\"modifyTime\":1599565556993,\"marketOddsList\":[{\"active\":1,\"oddsType\":\"1XAndYes\",\"addition1\":\"1\",\"addition2\":\"556750\",\"addition3\":\"X\",\"addition4\":\"0\",\"addition5\":\"Yes\",\"thirdOddsFieldSourceId\":\"23323959_546__1718\",\"orderOdds\":1,\"oddsValue\":175000,\"originalOddsValue\":209472,\"thirdTempletSourceId\":\"SR:546:1718\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556993,\"extraInfo\":\"546And1718\"},{\"active\":1,\"oddsType\":\"1XAndNo\",\"addition1\":\"1\",\"addition2\":\"556750\",\"addition3\":\"X\",\"addition4\":\"0\",\"addition5\":\"No\",\"thirdOddsFieldSourceId\":\"23323959_546__1719\",\"orderOdds\":2,\"oddsValue\":170000,\"originalOddsValue\":202223,\"thirdTempletSourceId\":\"SR:546:1719\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556993,\"extraInfo\":\"546And1719\"},{\"active\":1,\"oddsType\":\"12AndYes\",\"addition1\":\"1\",\"addition2\":\"556750\",\"addition3\":\"2\",\"addition4\":\"423771\",\"addition5\":\"Yes\",\"thirdOddsFieldSourceId\":\"23323959_546__1720\",\"orderOdds\":3,\"oddsValue\":200000,\"originalOddsValue\":241875,\"thirdTempletSourceId\":\"SR:546:1720\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556993,\"extraInfo\":\"546And1720\"},{\"active\":1,\"oddsType\":\"12AndNo\",\"addition1\":\"1\",\"addition2\":\"556750\",\"addition3\":\"2\",\"addition4\":\"423771\",\"addition5\":\"No\",\"thirdOddsFieldSourceId\":\"23323959_546__1721\",\"orderOdds\":4,\"oddsValue\":170000,\"originalOddsValue\":202223,\"thirdTempletSourceId\":\"SR:546:1721\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556994,\"extraInfo\":\"546And1721\"},{\"active\":1,\"oddsType\":\"X2AndYes\",\"addition1\":\"X\",\"addition2\":\"0\",\"addition3\":\"2\",\"addition4\":\"423771\",\"addition5\":\"Yes\",\"thirdOddsFieldSourceId\":\"23323959_546__1722\",\"orderOdds\":5,\"oddsValue\":600000,\"originalOddsValue\":832163,\"thirdTempletSourceId\":\"SR:546:1722\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556994,\"extraInfo\":\"546And1722\"},{\"active\":0,\"oddsType\":\"X2AndNo\",\"addition1\":\"X\",\"addition2\":\"0\",\"addition3\":\"2\",\"addition4\":\"423771\",\"addition5\":\"No\",\"thirdOddsFieldSourceId\":\"23323959_546__1723\",\"orderOdds\":6,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"SR:546:1723\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556994,\"extraInfo\":\"546And1723\"}],\"extraInfo\":\"{}\"},{\"thirdMarketCategorySourceId\":\"SR:13\",\"thirdMarketSourceId\":\"23323959_13_\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":0,\"oddsName\":\"马斯格雷夫 下注无效\",\"modifyTime\":1599565556994,\"marketOddsList\":[{\"active\":1,\"oddsType\":\"1\",\"addition1\":\"423771\",\"addition2\":\"556750\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_13__780\",\"orderOdds\":1,\"oddsValue\":106000,\"originalOddsValue\":110464,\"thirdTempletSourceId\":\"SR:13:780\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556994,\"extraInfo\":\"13And780\"},{\"active\":1,\"oddsType\":\"X\",\"addition1\":\"423771\",\"addition2\":\"0\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_13__782\",\"orderOdds\":2,\"oddsValue\":675000,\"originalOddsValue\":1055688,\"thirdTempletSourceId\":\"SR:13:782\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556994,\"extraInfo\":\"13And782\"}],\"extraInfo\":\"{}\"},{\"thirdMarketCategorySourceId\":\"SR:18\",\"thirdMarketSourceId\":\"23323959_18_3.5\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":0,\"oddsName\":\"合计\",\"addition1\":\"3.5\",\"modifyTime\":1599565556994,\"marketOddsList\":[{\"active\":1,\"oddsType\":\"Over\",\"addition1\":\"\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_18_3.5_12\",\"orderOdds\":1,\"oddsValue\":320000,\"originalOddsValue\":382758,\"thirdTempletSourceId\":\"SR:18:12\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556995,\"extraInfo\":\"18And12\"},{\"active\":1,\"oddsType\":\"Under\",\"addition1\":\"\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_18_3.5_13\",\"orderOdds\":2,\"oddsValue\":127000,\"originalOddsValue\":135366,\"thirdTempletSourceId\":\"SR:18:13\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556995,\"extraInfo\":\"18And13\"}],\"extraInfo\":\"{\\\"total\\\":\\\"3.5\\\"}\"},{\"thirdMarketCategorySourceId\":\"SR:20\",\"thirdMarketSourceId\":\"23323959_20_0.5\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":0,\"oddsName\":\"马斯格雷夫 合计\",\"addition1\":\"0.5\",\"modifyTime\":1599565556995,\"marketOddsList\":[{\"active\":1,\"oddsType\":\"Under\",\"addition1\":\"423771\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_20_0.5_13\",\"orderOdds\":1,\"oddsValue\":185000,\"originalOddsValue\":202223,\"thirdTempletSourceId\":\"SR:20:13\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556995,\"extraInfo\":\"20And13\"},{\"active\":1,\"oddsType\":\"Over\",\"addition1\":\"423771\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_20_0.5_12\",\"orderOdds\":2,\"oddsValue\":180000,\"originalOddsValue\":197825,\"thirdTempletSourceId\":\"SR:20:12\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556995,\"extraInfo\":\"20And12\"}],\"extraInfo\":\"{\\\"total\\\":\\\"0.5\\\"}\"},{\"thirdMarketCategorySourceId\":\"SR:29\",\"thirdMarketSourceId\":\"23323959_29_\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":0,\"oddsName\":\"两队都得分\",\"modifyTime\":1599565556995,\"marketOddsList\":[{\"active\":1,\"oddsType\":\"Yes\",\"addition1\":\"\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_29__74\",\"orderOdds\":1,\"oddsValue\":180000,\"originalOddsValue\":197825,\"thirdTempletSourceId\":\"SR:29:74\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556995,\"extraInfo\":\"29And74\"},{\"active\":1,\"oddsType\":\"No\",\"addition1\":\"\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_29__76\",\"orderOdds\":2,\"oddsValue\":185000,\"originalOddsValue\":202223,\"thirdTempletSourceId\":\"SR:29:76\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556995,\"extraInfo\":\"29And76\"}],\"extraInfo\":\"{}\"},{\"thirdMarketCategorySourceId\":\"SR:14\",\"thirdMarketSourceId\":\"23323959_14_1:0\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":2,\"oddsName\":\"不利 1:0\",\"addition1\":\"1\",\"modifyTime\":1599565556995,\"marketOddsList\":[],\"extraInfo\":\"{\\\"hcp\\\":\\\"1:0\\\"}\"},{\"thirdMarketCategorySourceId\":\"SR:11\",\"thirdMarketSourceId\":\"23323959_11_\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":0,\"oddsName\":\"平局返还\",\"modifyTime\":1599565556995,\"marketOddsList\":[{\"active\":1,\"oddsType\":\"1\",\"addition1\":\"556750\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_11__4\",\"orderOdds\":1,\"oddsValue\":101000,\"originalOddsValue\":103194,\"thirdTempletSourceId\":\"SR:11:4\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556995,\"extraInfo\":\"11And4\"},{\"active\":1,\"oddsType\":\"2\",\"addition1\":\"423771\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_11__5\",\"orderOdds\":2,\"oddsValue\":925000,\"originalOddsValue\":3230390,\"thirdTempletSourceId\":\"SR:11:5\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556995,\"extraInfo\":\"11And5\"}],\"extraInfo\":\"{}\"},{\"thirdMarketCategorySourceId\":\"SR:37\",\"thirdMarketSourceId\":\"23323959_37_2.5\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":0,\"oddsName\":\"1x2 \\u0026 合计\",\"addition1\":\"2.5\",\"modifyTime\":1599565556996,\"marketOddsList\":[{\"active\":1,\"oddsType\":\"1AndUnder\",\"addition1\":\"1\",\"addition2\":\"Under\",\"addition3\":\"556750\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_37_2.5_794\",\"orderOdds\":1,\"oddsValue\":235000,\"originalOddsValue\":271781,\"thirdTempletSourceId\":\"SR:37:794\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556996,\"extraInfo\":\"37And794\"},{\"active\":0,\"oddsType\":\"XAndUnder\",\"addition1\":\"X\",\"addition2\":\"Under\",\"addition3\":\"0\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_37_2.5_798\",\"orderOdds\":2,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"SR:37:798\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556996,\"extraInfo\":\"37And798\"},{\"active\":0,\"oddsType\":\"2AndUnder\",\"addition1\":\"2\",\"addition2\":\"Under\",\"addition3\":\"423771\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_37_2.5_802\",\"orderOdds\":3,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"SR:37:802\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556996,\"extraInfo\":\"37And802\"},{\"active\":1,\"oddsType\":\"1AndOver\",\"addition1\":\"1\",\"addition2\":\"Over\",\"addition3\":\"556750\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_37_2.5_796\",\"orderOdds\":4,\"oddsValue\":175000,\"originalOddsValue\":195355,\"thirdTempletSourceId\":\"SR:37:796\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556996,\"extraInfo\":\"37And796\"},{\"active\":1,\"oddsType\":\"XAndOver\",\"addition1\":\"X\",\"addition2\":\"Over\",\"addition3\":\"0\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_37_2.5_800\",\"orderOdds\":5,\"oddsValue\":875000,\"originalOddsValue\":1086218,\"thirdTempletSourceId\":\"SR:37:800\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556996,\"extraInfo\":\"37And800\"},{\"active\":1,\"oddsType\":\"2AndOver\",\"addition1\":\"2\",\"addition2\":\"Over\",\"addition3\":\"423771\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_37_2.5_804\",\"orderOdds\":6,\"oddsValue\":2800000,\"originalOddsValue\":3557944,\"thirdTempletSourceId\":\"SR:37:804\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556996,\"extraInfo\":\"37And804\"}],\"extraInfo\":\"{\\\"total\\\":\\\"2.5\\\"}\"},{\"thirdMarketCategorySourceId\":\"SR:21\",\"thirdMarketSourceId\":\"23323959_21_sr:exact_goals:5+\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":0,\"oddsName\":\"总进球数\",\"modifyTime\":1599565556996,\"marketOddsList\":[{\"active\":0,\"oddsType\":\"0\",\"thirdOddsFieldSourceId\":\"23323959_21_sr:exact_goals:5+_sr:exact_goals:5+:1336\",\"orderOdds\":1,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556996,\"extraInfo\":\"21Andsr:exact_goals:5+:1336\"},{\"active\":0,\"oddsType\":\"1\",\"thirdOddsFieldSourceId\":\"23323959_21_sr:exact_goals:5+_sr:exact_goals:5+:1337\",\"orderOdds\":2,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556996,\"extraInfo\":\"21Andsr:exact_goals:5+:1337\"},{\"active\":1,\"oddsType\":\"2\",\"thirdOddsFieldSourceId\":\"23323959_21_sr:exact_goals:5+_sr:exact_goals:5+:1338\",\"orderOdds\":3,\"oddsValue\":240000,\"originalOddsValue\":271781,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556996,\"extraInfo\":\"21Andsr:exact_goals:5+:1338\"},{\"active\":1,\"oddsType\":\"3\",\"thirdOddsFieldSourceId\":\"23323959_21_sr:exact_goals:5+_sr:exact_goals:5+:1339\",\"orderOdds\":4,\"oddsValue\":240000,\"originalOddsValue\":269691,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556996,\"extraInfo\":\"21Andsr:exact_goals:5+:1339\"},{\"active\":1,\"oddsType\":\"4\",\"thirdOddsFieldSourceId\":\"23323959_21_sr:exact_goals:5+_sr:exact_goals:5+:1340\",\"orderOdds\":5,\"oddsValue\":470000,\"originalOddsValue\":557371,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556996,\"extraInfo\":\"21Andsr:exact_goals:5+:1340\"},{\"active\":1,\"oddsType\":\"5+\",\"thirdOddsFieldSourceId\":\"23323959_21_sr:exact_goals:5+_sr:exact_goals:5+:1341\",\"orderOdds\":6,\"oddsValue\":1000000,\"originalOddsValue\":1221775,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556997,\"extraInfo\":\"21Andsr:exact_goals:5+:1341\"}],\"extraInfo\":\"{\\\"variant\\\":\\\"sr:exact_goals:5+\\\"}\"},{\"thirdMarketCategorySourceId\":\"SR:16\",\"thirdMarketSourceId\":\"23323959_16_-1.5\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":0,\"oddsName\":\"欧洲让球盘\",\"addition1\":\"0.5\",\"addition2\":\"-1.5\",\"modifyTime\":1599565556997,\"marketOddsList\":[{\"active\":1,\"oddsType\":\"1\",\"addition1\":\"556750\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_16_-1.5_1714\",\"orderOdds\":1,\"oddsValue\":157000,\"originalOddsValue\":169709,\"thirdTempletSourceId\":\"SR:16:1714\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556997,\"extraInfo\":\"16And1714\"},{\"active\":1,\"oddsType\":\"2\",\"addition1\":\"423771\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_16_-1.5_1715\",\"orderOdds\":2,\"oddsValue\":215000,\"originalOddsValue\":243454,\"thirdTempletSourceId\":\"SR:16:1715\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556997,\"extraInfo\":\"16And1715\"}],\"extraInfo\":\"{\\\"hcp\\\":\\\"-1.5\\\"}\"},{\"thirdMarketCategorySourceId\":\"SR:26\",\"thirdMarketSourceId\":\"23323959_26_\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":0,\"oddsName\":\"总进球数单/双\",\"modifyTime\":1599565556997,\"marketOddsList\":[{\"active\":1,\"oddsType\":\"Odd\",\"addition1\":\"\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_26__70\",\"orderOdds\":1,\"oddsValue\":205000,\"originalOddsValue\":228927,\"thirdTempletSourceId\":\"SR:26:70\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556997,\"extraInfo\":\"26And70\"},{\"active\":1,\"oddsType\":\"Even\",\"addition1\":\"\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_26__72\",\"orderOdds\":2,\"oddsValue\":165000,\"originalOddsValue\":177563,\"thirdTempletSourceId\":\"SR:26:72\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556997,\"extraInfo\":\"26And72\"}],\"extraInfo\":\"{}\"},{\"thirdMarketCategorySourceId\":\"SR:18\",\"thirdMarketSourceId\":\"23323959_18_4.5\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":0,\"oddsName\":\"合计\",\"addition1\":\"4.5\",\"modifyTime\":1599565556997,\"marketOddsList\":[{\"active\":1,\"oddsType\":\"Over\",\"addition1\":\"\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_18_4.5_12\",\"orderOdds\":1,\"oddsValue\":700000,\"originalOddsValue\":1221775,\"thirdTempletSourceId\":\"SR:18:12\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556997,\"extraInfo\":\"18And12\"},{\"active\":1,\"oddsType\":\"Under\",\"addition1\":\"\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_18_4.5_13\",\"orderOdds\":2,\"oddsValue\":105000,\"originalOddsValue\":108914,\"thirdTempletSourceId\":\"SR:18:13\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556997,\"extraInfo\":\"18And13\"}],\"extraInfo\":\"{\\\"total\\\":\\\"4.5\\\"}\"},{\"thirdMarketCategorySourceId\":\"SR:8\",\"thirdMarketSourceId\":\"23323959_8_3\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":0,\"oddsName\":\"谁先进第第3球\",\"addition1\":\"3\",\"modifyTime\":1599565556998,\"marketOddsList\":[{\"active\":1,\"oddsType\":\"1\",\"addition1\":\"556750\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_8_3_6\",\"orderOdds\":1,\"oddsValue\":470000,\"originalOddsValue\":531279,\"thirdTempletSourceId\":\"SR:8:6\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556998,\"extraInfo\":\"8And6\"},{\"active\":1,\"oddsType\":\"None\",\"addition1\":\"0\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_8_3_7\",\"orderOdds\":2,\"oddsValue\":245000,\"originalOddsValue\":271781,\"thirdTempletSourceId\":\"SR:8:7\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556998,\"extraInfo\":\"8And7\"},{\"active\":1,\"oddsType\":\"2\",\"addition1\":\"423771\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_8_3_8\",\"orderOdds\":3,\"oddsValue\":205000,\"originalOddsValue\":225311,\"thirdTempletSourceId\":\"SR:8:8\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556998,\"extraInfo\":\"8And8\"}],\"extraInfo\":\"{\\\"goalnr\\\":\\\"3\\\"}\"},{\"thirdMarketCategorySourceId\":\"SR:31\",\"thirdMarketSourceId\":\"23323959_31_\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":0,\"oddsName\":\"邦德公牛鲨不失球\",\"modifyTime\":1599565556998,\"marketOddsList\":[{\"active\":1,\"oddsType\":\"Yes\",\"addition1\":\"556750\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_31__74\",\"orderOdds\":1,\"oddsValue\":185000,\"originalOddsValue\":202223,\"thirdTempletSourceId\":\"SR:31:74\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556998,\"extraInfo\":\"31And74\"},{\"active\":1,\"oddsType\":\"No\",\"addition1\":\"556750\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_31__76\",\"orderOdds\":2,\"oddsValue\":180000,\"originalOddsValue\":197825,\"thirdTempletSourceId\":\"SR:31:76\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556998,\"extraInfo\":\"31And76\"}],\"extraInfo\":\"{}\"},{\"thirdMarketCategorySourceId\":\"SR:1\",\"thirdMarketSourceId\":\"23323959_1_\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":0,\"oddsName\":\"胜平负\",\"modifyTime\":1599565556998,\"marketOddsList\":[{\"active\":1,\"oddsType\":\"1\",\"addition1\":\"556750\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"addition5\":\"\",\"thirdOddsFieldSourceId\":\"23323959_1__1\",\"orderOdds\":1,\"oddsValue\":110000,\"originalOddsValue\":113657,\"thirdTempletSourceId\":\"SR:1:1\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556998,\"extraInfo\":\"1And1\"},{\"active\":1,\"oddsType\":\"X\",\"addition1\":\"0\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_1__2\",\"orderOdds\":2,\"oddsValue\":875000,\"originalOddsValue\":1086218,\"thirdTempletSourceId\":\"SR:1:2\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556998,\"extraInfo\":\"1And2\"},{\"active\":1,\"oddsType\":\"2\",\"addition1\":\"423771\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_1__3\",\"orderOdds\":3,\"oddsValue\":2900000,\"originalOddsValue\":3557944,\"thirdTempletSourceId\":\"SR:1:3\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556999,\"extraInfo\":\"1And3\"}],\"extraInfo\":\"{}\"},{\"thirdMarketCategorySourceId\":\"SR:199\",\"thirdMarketSourceId\":\"23323959_199_sr:correct_score:below:5-5\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":0,\"oddsName\":\"正确分\",\"modifyTime\":1599565556999,\"marketOddsList\":[{\"active\":0,\"oddsType\":\"1:0\",\"addition1\":\"1\",\"addition2\":\"0\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1476\",\"orderOdds\":1,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556999,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1476\"},{\"active\":1,\"oddsType\":\"2:0\",\"addition1\":\"2\",\"addition2\":\"0\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1477\",\"orderOdds\":2,\"oddsValue\":220000,\"originalOddsValue\":271781,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556999,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1477\"},{\"active\":1,\"oddsType\":\"2:1\",\"addition1\":\"2\",\"addition2\":\"1\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1483\",\"orderOdds\":3,\"oddsValue\":300000,\"originalOddsValue\":381648,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556999,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1483\"},{\"active\":1,\"oddsType\":\"3:0\",\"addition1\":\"3\",\"addition2\":\"0\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1478\",\"orderOdds\":4,\"oddsValue\":675000,\"originalOddsValue\":919344,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556999,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1478\"},{\"active\":1,\"oddsType\":\"3:1\",\"addition1\":\"3\",\"addition2\":\"1\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1484\",\"orderOdds\":5,\"oddsValue\":950000,\"originalOddsValue\":1290986,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556999,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1484\"},{\"active\":1,\"oddsType\":\"3:2\",\"addition1\":\"3\",\"addition2\":\"2\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1490\",\"orderOdds\":6,\"oddsValue\":2600000,\"originalOddsValue\":3625726,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556999,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1490\"},{\"active\":1,\"oddsType\":\"4:0\",\"addition1\":\"4\",\"addition2\":\"0\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1479\",\"orderOdds\":7,\"oddsValue\":4500000,\"originalOddsValue\":6219667,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556999,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1479\"},{\"active\":1,\"oddsType\":\"4:1\",\"addition1\":\"4\",\"addition2\":\"1\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1485\",\"orderOdds\":8,\"oddsValue\":6000000,\"originalOddsValue\":8733946,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556999,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1485\"},{\"active\":1,\"oddsType\":\"4:2\",\"addition1\":\"4\",\"addition2\":\"2\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1491\",\"orderOdds\":9,\"oddsValue\":10000000,\"originalOddsValue\":24529230,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556999,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1491\"},{\"active\":1,\"oddsType\":\"4:3\",\"addition1\":\"4\",\"addition2\":\"3\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1497\",\"orderOdds\":10,\"oddsValue\":10000000,\"originalOddsValue\":103335269,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556999,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1497\"},{\"active\":1,\"oddsType\":\"5:0\",\"addition1\":\"5\",\"addition2\":\"0\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1480\",\"orderOdds\":11,\"oddsValue\":10000000,\"originalOddsValue\":63117139,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556999,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1480\"},{\"active\":1,\"oddsType\":\"5:1\",\"addition1\":\"5\",\"addition2\":\"1\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1486\",\"orderOdds\":12,\"oddsValue\":10000000,\"originalOddsValue\":88632037,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556999,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1486\"},{\"active\":1,\"oddsType\":\"5:2\",\"addition1\":\"5\",\"addition2\":\"2\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1492\",\"orderOdds\":13,\"oddsValue\":10000000,\"originalOddsValue\":248922476,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556999,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1492\"},{\"active\":1,\"oddsType\":\"5:3\",\"addition1\":\"5\",\"addition2\":\"3\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1498\",\"orderOdds\":14,\"oddsValue\":10000000,\"originalOddsValue\":1048645622,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556999,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1498\"},{\"active\":1,\"oddsType\":\"5:4\",\"addition1\":\"5\",\"addition2\":\"4\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1504\",\"orderOdds\":15,\"oddsValue\":10000000,\"originalOddsValue\":2147483647,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556999,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1504\"},{\"active\":0,\"oddsType\":\"0:0\",\"addition1\":\"0\",\"addition2\":\"0\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1475\",\"orderOdds\":16,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556999,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1475\"},{\"active\":0,\"oddsType\":\"1:1\",\"addition1\":\"1\",\"addition2\":\"1\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1482\",\"orderOdds\":17,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556999,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1482\"},{\"active\":1,\"oddsType\":\"2:2\",\"addition1\":\"2\",\"addition2\":\"2\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1489\",\"orderOdds\":18,\"oddsValue\":850000,\"originalOddsValue\":1164476,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556999,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1489\"},{\"active\":1,\"oddsType\":\"3:3\",\"addition1\":\"3\",\"addition2\":\"3\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1496\",\"orderOdds\":19,\"oddsValue\":10000000,\"originalOddsValue\":16594121,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556999,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1496\"},{\"active\":1,\"oddsType\":\"4:4\",\"addition1\":\"4\",\"addition2\":\"4\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1503\",\"orderOdds\":20,\"oddsValue\":10000000,\"originalOddsValue\":630589387,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556999,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1503\"},{\"active\":0,\"oddsType\":\"0:1\",\"addition1\":\"0\",\"addition2\":\"1\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1481\",\"orderOdds\":21,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556999,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1481\"},{\"active\":0,\"oddsType\":\"0:2\",\"addition1\":\"0\",\"addition2\":\"2\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1487\",\"orderOdds\":22,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556999,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1487\"},{\"active\":0,\"oddsType\":\"1:2\",\"addition1\":\"1\",\"addition2\":\"2\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1488\",\"orderOdds\":23,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565556999,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1488\"},{\"active\":0,\"oddsType\":\"0:3\",\"addition1\":\"0\",\"addition2\":\"3\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1493\",\"orderOdds\":24,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557000,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1493\"},{\"active\":0,\"oddsType\":\"1:3\",\"addition1\":\"1\",\"addition2\":\"3\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1494\",\"orderOdds\":25,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557000,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1494\"},{\"active\":1,\"oddsType\":\"2:3\",\"addition1\":\"2\",\"addition2\":\"3\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1495\",\"orderOdds\":26,\"oddsValue\":3200000,\"originalOddsValue\":4515446,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557000,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1495\"},{\"active\":0,\"oddsType\":\"0:4\",\"addition1\":\"0\",\"addition2\":\"4\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1499\",\"orderOdds\":27,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557000,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1499\"},{\"active\":0,\"oddsType\":\"1:4\",\"addition1\":\"1\",\"addition2\":\"4\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1500\",\"orderOdds\":28,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557000,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1500\"},{\"active\":1,\"oddsType\":\"2:4\",\"addition1\":\"2\",\"addition2\":\"4\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1501\",\"orderOdds\":29,\"oddsValue\":10000000,\"originalOddsValue\":25363201,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557000,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1501\"},{\"active\":1,\"oddsType\":\"3:4\",\"addition1\":\"3\",\"addition2\":\"4\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1502\",\"orderOdds\":30,\"oddsValue\":10000000,\"originalOddsValue\":85795205,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557000,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1502\"},{\"active\":0,\"oddsType\":\"0:5\",\"addition1\":\"0\",\"addition2\":\"5\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1505\",\"orderOdds\":31,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557000,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1505\"},{\"active\":0,\"oddsType\":\"1:5\",\"addition1\":\"1\",\"addition2\":\"5\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1506\",\"orderOdds\":32,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557000,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1506\"},{\"active\":1,\"oddsType\":\"2:5\",\"addition1\":\"2\",\"addition2\":\"5\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1507\",\"orderOdds\":33,\"oddsValue\":10000000,\"originalOddsValue\":178080943,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557000,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1507\"},{\"active\":1,\"oddsType\":\"3:5\",\"addition1\":\"3\",\"addition2\":\"5\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1508\",\"orderOdds\":34,\"oddsValue\":10000000,\"originalOddsValue\":602388228,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557000,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1508\"},{\"active\":1,\"oddsType\":\"4:5\",\"addition1\":\"4\",\"addition2\":\"5\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1509\",\"orderOdds\":35,\"oddsValue\":10000000,\"originalOddsValue\":2147483647,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557000,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1509\"},{\"active\":1,\"oddsType\":\"Other\",\"thirdOddsFieldSourceId\":\"23323959_199_sr:correct_score:below:5-5_sr:correct_score:below:5-5:1510\",\"orderOdds\":36,\"oddsValue\":10000000,\"originalOddsValue\":281471148,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557000,\"extraInfo\":\"199Andsr:correct_score:below:5-5:1510\"}],\"extraInfo\":\"{\\\"variant\\\":\\\"sr:correct_score:below:5-5\\\"}\"},{\"thirdMarketCategorySourceId\":\"SR:35\",\"thirdMarketSourceId\":\"23323959_35_\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":0,\"oddsName\":\"1x2 \\u0026 两队得分\",\"modifyTime\":1599565557000,\"marketOddsList\":[{\"active\":1,\"oddsType\":\"1AndYes\",\"addition1\":\"1\",\"addition2\":\"556750\",\"addition3\":\"Yes\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_35__78\",\"orderOdds\":1,\"oddsValue\":225000,\"originalOddsValue\":259519,\"thirdTempletSourceId\":\"SR:35:78\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557000,\"extraInfo\":\"35And78\"},{\"active\":1,\"oddsType\":\"1AndNo\",\"addition1\":\"1\",\"addition2\":\"556750\",\"addition3\":\"No\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_35__80\",\"orderOdds\":2,\"oddsValue\":180000,\"originalOddsValue\":202223,\"thirdTempletSourceId\":\"SR:35:80\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557000,\"extraInfo\":\"35And80\"},{\"active\":1,\"oddsType\":\"XAndYes\",\"addition1\":\"X\",\"addition2\":\"0\",\"addition3\":\"Yes\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_35__82\",\"orderOdds\":3,\"oddsValue\":875000,\"originalOddsValue\":1086218,\"thirdTempletSourceId\":\"SR:35:82\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557000,\"extraInfo\":\"35And82\"},{\"active\":0,\"oddsType\":\"XAndNo\",\"addition1\":\"X\",\"addition2\":\"0\",\"addition3\":\"No\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_35__84\",\"orderOdds\":4,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"SR:35:84\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557000,\"extraInfo\":\"35And84\"},{\"active\":1,\"oddsType\":\"2AndYes\",\"addition1\":\"2\",\"addition2\":\"423771\",\"addition3\":\"Yes\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_35__86\",\"orderOdds\":5,\"oddsValue\":2800000,\"originalOddsValue\":3557944,\"thirdTempletSourceId\":\"SR:35:86\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557000,\"extraInfo\":\"35And86\"},{\"active\":0,\"oddsType\":\"2AndNo\",\"addition1\":\"2\",\"addition2\":\"423771\",\"addition3\":\"No\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_35__88\",\"orderOdds\":6,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"SR:35:88\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557000,\"extraInfo\":\"35And88\"}],\"extraInfo\":\"{}\"},{\"thirdMarketCategorySourceId\":\"SR:45\",\"thirdMarketSourceId\":\"23323959_45_\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":0,\"oddsName\":\"正确进球\",\"modifyTime\":1599565557000,\"marketOddsList\":[{\"active\":0,\"oddsType\":\"0:0\",\"addition1\":\"0\",\"addition2\":\"0\",\"thirdOddsFieldSourceId\":\"23323959_45__274\",\"orderOdds\":1,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557000,\"extraInfo\":\"45And274\"},{\"active\":0,\"oddsType\":\"1:0\",\"addition1\":\"1\",\"addition2\":\"0\",\"thirdOddsFieldSourceId\":\"23323959_45__276\",\"orderOdds\":2,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557000,\"extraInfo\":\"45And276\"},{\"active\":1,\"oddsType\":\"2:0\",\"addition1\":\"2\",\"addition2\":\"0\",\"thirdOddsFieldSourceId\":\"23323959_45__278\",\"orderOdds\":3,\"oddsValue\":250000,\"originalOddsValue\":271781,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557000,\"extraInfo\":\"45And278\"},{\"active\":1,\"oddsType\":\"3:0\",\"addition1\":\"3\",\"addition2\":\"0\",\"thirdOddsFieldSourceId\":\"23323959_45__280\",\"orderOdds\":4,\"oddsValue\":825000,\"originalOddsValue\":919344,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557000,\"extraInfo\":\"45And280\"},{\"active\":1,\"oddsType\":\"4:0\",\"addition1\":\"4\",\"addition2\":\"0\",\"thirdOddsFieldSourceId\":\"23323959_45__282\",\"orderOdds\":5,\"oddsValue\":5500000,\"originalOddsValue\":6219667,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557000,\"extraInfo\":\"45And282\"},{\"active\":0,\"oddsType\":\"0:1\",\"addition1\":\"0\",\"addition2\":\"1\",\"thirdOddsFieldSourceId\":\"23323959_45__284\",\"orderOdds\":6,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557000,\"extraInfo\":\"45And284\"},{\"active\":0,\"oddsType\":\"1:1\",\"addition1\":\"1\",\"addition2\":\"1\",\"thirdOddsFieldSourceId\":\"23323959_45__286\",\"orderOdds\":7,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557000,\"extraInfo\":\"45And286\"},{\"active\":1,\"oddsType\":\"2:1\",\"addition1\":\"2\",\"addition2\":\"1\",\"thirdOddsFieldSourceId\":\"23323959_45__288\",\"orderOdds\":8,\"oddsValue\":350000,\"originalOddsValue\":381648,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557000,\"extraInfo\":\"45And288\"},{\"active\":1,\"oddsType\":\"3:1\",\"addition1\":\"3\",\"addition2\":\"1\",\"thirdOddsFieldSourceId\":\"23323959_45__290\",\"orderOdds\":9,\"oddsValue\":1150000,\"originalOddsValue\":1290986,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557001,\"extraInfo\":\"45And290\"},{\"active\":1,\"oddsType\":\"4:1\",\"addition1\":\"4\",\"addition2\":\"1\",\"thirdOddsFieldSourceId\":\"23323959_45__292\",\"orderOdds\":10,\"oddsValue\":7500000,\"originalOddsValue\":8733946,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557001,\"extraInfo\":\"45And292\"},{\"active\":0,\"oddsType\":\"0:2\",\"addition1\":\"0\",\"addition2\":\"2\",\"thirdOddsFieldSourceId\":\"23323959_45__294\",\"orderOdds\":11,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557001,\"extraInfo\":\"45And294\"},{\"active\":0,\"oddsType\":\"1:2\",\"addition1\":\"1\",\"addition2\":\"2\",\"thirdOddsFieldSourceId\":\"23323959_45__296\",\"orderOdds\":12,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557001,\"extraInfo\":\"45And296\"},{\"active\":1,\"oddsType\":\"2:2\",\"addition1\":\"2\",\"addition2\":\"2\",\"thirdOddsFieldSourceId\":\"23323959_45__298\",\"orderOdds\":13,\"oddsValue\":1050000,\"originalOddsValue\":1164476,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557001,\"extraInfo\":\"45And298\"},{\"active\":1,\"oddsType\":\"3:2\",\"addition1\":\"3\",\"addition2\":\"2\",\"thirdOddsFieldSourceId\":\"23323959_45__300\",\"orderOdds\":14,\"oddsValue\":3200000,\"originalOddsValue\":3625726,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557001,\"extraInfo\":\"45And300\"},{\"active\":1,\"oddsType\":\"4:2\",\"addition1\":\"4\",\"addition2\":\"2\",\"thirdOddsFieldSourceId\":\"23323959_45__302\",\"orderOdds\":15,\"oddsValue\":10000000,\"originalOddsValue\":24529230,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557001,\"extraInfo\":\"45And302\"},{\"active\":0,\"oddsType\":\"0:3\",\"addition1\":\"0\",\"addition2\":\"3\",\"thirdOddsFieldSourceId\":\"23323959_45__304\",\"orderOdds\":16,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557001,\"extraInfo\":\"45And304\"},{\"active\":0,\"oddsType\":\"1:3\",\"addition1\":\"1\",\"addition2\":\"3\",\"thirdOddsFieldSourceId\":\"23323959_45__306\",\"orderOdds\":17,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557001,\"extraInfo\":\"45And306\"},{\"active\":1,\"oddsType\":\"2:3\",\"addition1\":\"2\",\"addition2\":\"3\",\"thirdOddsFieldSourceId\":\"23323959_45__308\",\"orderOdds\":18,\"oddsValue\":4000000,\"originalOddsValue\":4515446,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557001,\"extraInfo\":\"45And308\"},{\"active\":1,\"oddsType\":\"3:3\",\"addition1\":\"3\",\"addition2\":\"3\",\"thirdOddsFieldSourceId\":\"23323959_45__310\",\"orderOdds\":19,\"oddsValue\":10000000,\"originalOddsValue\":16594121,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557001,\"extraInfo\":\"45And310\"},{\"active\":1,\"oddsType\":\"4:3\",\"addition1\":\"4\",\"addition2\":\"3\",\"thirdOddsFieldSourceId\":\"23323959_45__312\",\"orderOdds\":20,\"oddsValue\":10000000,\"originalOddsValue\":103335269,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557001,\"extraInfo\":\"45And312\"},{\"active\":0,\"oddsType\":\"0:4\",\"addition1\":\"0\",\"addition2\":\"4\",\"thirdOddsFieldSourceId\":\"23323959_45__314\",\"orderOdds\":21,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557001,\"extraInfo\":\"45And314\"},{\"active\":0,\"oddsType\":\"1:4\",\"addition1\":\"1\",\"addition2\":\"4\",\"thirdOddsFieldSourceId\":\"23323959_45__316\",\"orderOdds\":22,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557001,\"extraInfo\":\"45And316\"},{\"active\":1,\"oddsType\":\"2:4\",\"addition1\":\"2\",\"addition2\":\"4\",\"thirdOddsFieldSourceId\":\"23323959_45__318\",\"orderOdds\":23,\"oddsValue\":10000000,\"originalOddsValue\":25363201,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557001,\"extraInfo\":\"45And318\"},{\"active\":1,\"oddsType\":\"3:4\",\"addition1\":\"3\",\"addition2\":\"4\",\"thirdOddsFieldSourceId\":\"23323959_45__320\",\"orderOdds\":24,\"oddsValue\":10000000,\"originalOddsValue\":85795205,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557001,\"extraInfo\":\"45And320\"},{\"active\":1,\"oddsType\":\"4:4\",\"addition1\":\"4\",\"addition2\":\"4\",\"thirdOddsFieldSourceId\":\"23323959_45__322\",\"orderOdds\":25,\"oddsValue\":10000000,\"originalOddsValue\":630589387,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557001,\"extraInfo\":\"45And322\"},{\"active\":1,\"oddsType\":\"Other\",\"thirdOddsFieldSourceId\":\"23323959_45__324\",\"orderOdds\":26,\"oddsValue\":10000000,\"originalOddsValue\":23073118,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557001,\"extraInfo\":\"45And324\"}],\"extraInfo\":\"{}\"},{\"thirdMarketCategorySourceId\":\"SR:20\",\"thirdMarketSourceId\":\"23323959_20_2.5\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":0,\"oddsName\":\"马斯格雷夫 合计\",\"addition1\":\"2.5\",\"modifyTime\":1599565557001,\"marketOddsList\":[{\"active\":1,\"oddsType\":\"Under\",\"addition1\":\"423771\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_20_2.5_13\",\"orderOdds\":1,\"oddsValue\":101000,\"originalOddsValue\":103668,\"thirdTempletSourceId\":\"SR:20:13\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557001,\"extraInfo\":\"20And13\"},{\"active\":1,\"oddsType\":\"Over\",\"addition1\":\"423771\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_20_2.5_12\",\"orderOdds\":2,\"oddsValue\":975000,\"originalOddsValue\":2826276,\"thirdTempletSourceId\":\"SR:20:12\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557001,\"extraInfo\":\"20And12\"}],\"extraInfo\":\"{\\\"total\\\":\\\"2.5\\\"}\"},{\"thirdMarketCategorySourceId\":\"SR:23\",\"thirdMarketSourceId\":\"23323959_23_sr:exact_goals:3+\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":0,\"oddsName\":\"邦德公牛鲨 准确进球\",\"modifyTime\":1599565557001,\"marketOddsList\":[{\"active\":0,\"oddsType\":\"0\",\"addition1\":\"556750\",\"thirdOddsFieldSourceId\":\"23323959_23_sr:exact_goals:3+_sr:exact_goals:3+:88\",\"orderOdds\":1,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557001,\"extraInfo\":\"23Andsr:exact_goals:3+:88\"},{\"active\":0,\"oddsType\":\"1\",\"addition1\":\"556750\",\"thirdOddsFieldSourceId\":\"23323959_23_sr:exact_goals:3+_sr:exact_goals:3+:89\",\"orderOdds\":2,\"oddsValue\":0,\"originalOddsValue\":0,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557001,\"extraInfo\":\"23Andsr:exact_goals:3+:89\"},{\"active\":1,\"oddsType\":\"2\",\"addition1\":\"556750\",\"thirdOddsFieldSourceId\":\"23323959_23_sr:exact_goals:3+_sr:exact_goals:3+:90\",\"orderOdds\":3,\"oddsValue\":127000,\"originalOddsValue\":134668,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557001,\"extraInfo\":\"23Andsr:exact_goals:3+:90\"},{\"active\":1,\"oddsType\":\"3+\",\"addition1\":\"556750\",\"thirdOddsFieldSourceId\":\"23323959_23_sr:exact_goals:3+_sr:exact_goals:3+:91\",\"orderOdds\":4,\"oddsValue\":320000,\"originalOddsValue\":388448,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557001,\"extraInfo\":\"23Andsr:exact_goals:3+:91\"}],\"extraInfo\":\"{\\\"variant\\\":\\\"sr:exact_goals:3+\\\"}\"},{\"thirdMarketCategorySourceId\":\"SR:19\",\"thirdMarketSourceId\":\"23323959_19_3.5\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":0,\"oddsName\":\"邦德公牛鲨 合计\",\"addition1\":\"3.5\",\"modifyTime\":1599565557001,\"marketOddsList\":[{\"active\":1,\"oddsType\":\"Under\",\"addition1\":\"556750\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_19_3.5_13\",\"orderOdds\":1,\"oddsValue\":101000,\"originalOddsValue\":103760,\"thirdTempletSourceId\":\"SR:19:13\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557001,\"extraInfo\":\"19And13\"},{\"active\":1,\"oddsType\":\"Over\",\"addition1\":\"556750\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_19_3.5_12\",\"orderOdds\":2,\"oddsValue\":975000,\"originalOddsValue\":2759291,\"thirdTempletSourceId\":\"SR:19:12\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557001,\"extraInfo\":\"19And12\"}],\"extraInfo\":\"{\\\"total\\\":\\\"3.5\\\"}\"},{\"thirdMarketCategorySourceId\":\"SR:12\",\"thirdMarketSourceId\":\"23323959_12_\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":0,\"oddsName\":\"邦德公牛鲨 下注无效\",\"modifyTime\":1599565557002,\"marketOddsList\":[{\"active\":1,\"oddsType\":\"X\",\"addition1\":\"556750\",\"addition2\":\"0\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_12__776\",\"orderOdds\":1,\"oddsValue\":125000,\"originalOddsValue\":130529,\"thirdTempletSourceId\":\"SR:12:776\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557002,\"extraInfo\":\"12And776\"},{\"active\":1,\"oddsType\":\"2\",\"addition1\":\"556750\",\"addition2\":\"423771\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_12__778\",\"orderOdds\":2,\"oddsValue\":355000,\"originalOddsValue\":427553,\"thirdTempletSourceId\":\"SR:12:778\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557002,\"extraInfo\":\"12And778\"}],\"extraInfo\":\"{}\"},{\"thirdMarketCategorySourceId\":\"SR:18\",\"thirdMarketSourceId\":\"23323959_18_2.5\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":0,\"oddsName\":\"合计\",\"addition1\":\"2.5\",\"modifyTime\":1599565557002,\"marketOddsList\":[{\"active\":1,\"oddsType\":\"Over\",\"addition1\":\"\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_18_2.5_12\",\"orderOdds\":1,\"oddsValue\":147000,\"originalOddsValue\":158214,\"thirdTempletSourceId\":\"SR:18:12\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557002,\"extraInfo\":\"18And12\"},{\"active\":1,\"oddsType\":\"Under\",\"addition1\":\"\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_18_2.5_13\",\"orderOdds\":2,\"oddsValue\":240000,\"originalOddsValue\":271781,\"thirdTempletSourceId\":\"SR:18:13\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557002,\"extraInfo\":\"18And13\"}],\"extraInfo\":\"{\\\"total\\\":\\\"2.5\\\"}\"},{\"thirdMarketCategorySourceId\":\"SR:20\",\"thirdMarketSourceId\":\"23323959_20_1.5\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":0,\"oddsName\":\"马斯格雷夫 合计\",\"addition1\":\"1.5\",\"modifyTime\":1599565557002,\"marketOddsList\":[{\"active\":1,\"oddsType\":\"Under\",\"addition1\":\"423771\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_20_1.5_13\",\"orderOdds\":1,\"oddsValue\":112000,\"originalOddsValue\":118112,\"thirdTempletSourceId\":\"SR:20:13\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557002,\"extraInfo\":\"20And13\"},{\"active\":1,\"oddsType\":\"Over\",\"addition1\":\"423771\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_20_1.5_12\",\"orderOdds\":2,\"oddsValue\":490000,\"originalOddsValue\":652109,\"thirdTempletSourceId\":\"SR:20:12\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557002,\"extraInfo\":\"20And12\"}],\"extraInfo\":\"{\\\"total\\\":\\\"1.5\\\"}\"},{\"thirdMarketCategorySourceId\":\"SR:7\",\"thirdMarketSourceId\":\"23323959_7_2:0\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":0,\"oddsName\":\"剩余时间内哪队获胜\",\"addition1\":\"2\",\"addition2\":\"0\",\"modifyTime\":1599565557002,\"marketOddsList\":[{\"active\":1,\"oddsType\":\"1\",\"addition1\":\"556750\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"addition5\":\"\",\"thirdOddsFieldSourceId\":\"23323959_7_2:0_1\",\"orderOdds\":1,\"oddsValue\":625000,\"originalOddsValue\":715988,\"thirdTempletSourceId\":\"SR:7:1\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557002,\"extraInfo\":\"7And1\"},{\"active\":1,\"oddsType\":\"X\",\"addition1\":\"0\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_7_2:0_2\",\"orderOdds\":2,\"oddsValue\":205000,\"originalOddsValue\":222431,\"thirdTempletSourceId\":\"SR:7:2\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557002,\"extraInfo\":\"7And2\"},{\"active\":1,\"oddsType\":\"2\",\"addition1\":\"423771\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_7_2:0_3\",\"orderOdds\":3,\"oddsValue\":220000,\"originalOddsValue\":243454,\"thirdTempletSourceId\":\"SR:7:3\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557002,\"extraInfo\":\"7And3\"}],\"extraInfo\":\"{\\\"score\\\":\\\"2:0\\\"}\"},{\"thirdMarketCategorySourceId\":\"SR:24\",\"thirdMarketSourceId\":\"23323959_24_sr:exact_goals:3+\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":0,\"oddsName\":\"马斯格雷夫准确进球\",\"modifyTime\":1599565557002,\"marketOddsList\":[{\"active\":1,\"oddsType\":\"0\",\"addition1\":\"423771\",\"thirdOddsFieldSourceId\":\"23323959_24_sr:exact_goals:3+_sr:exact_goals:3+:88\",\"orderOdds\":1,\"oddsValue\":180000,\"originalOddsValue\":202223,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557002,\"extraInfo\":\"24Andsr:exact_goals:3+:88\"},{\"active\":1,\"oddsType\":\"1\",\"addition1\":\"423771\",\"thirdOddsFieldSourceId\":\"23323959_24_sr:exact_goals:3+_sr:exact_goals:3+:89\",\"orderOdds\":2,\"oddsValue\":245000,\"originalOddsValue\":283971,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557002,\"extraInfo\":\"24Andsr:exact_goals:3+:89\"},{\"active\":1,\"oddsType\":\"2\",\"addition1\":\"423771\",\"thirdOddsFieldSourceId\":\"23323959_24_sr:exact_goals:3+_sr:exact_goals:3+:90\",\"orderOdds\":3,\"oddsValue\":700000,\"originalOddsValue\":847700,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557002,\"extraInfo\":\"24Andsr:exact_goals:3+:90\"},{\"active\":1,\"oddsType\":\"3+\",\"addition1\":\"423771\",\"thirdOddsFieldSourceId\":\"23323959_24_sr:exact_goals:3+_sr:exact_goals:3+:91\",\"orderOdds\":4,\"oddsValue\":2300000,\"originalOddsValue\":2826276,\"thirdTempletSourceId\":\"None\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557002,\"extraInfo\":\"24Andsr:exact_goals:3+:91\"}],\"extraInfo\":\"{\\\"variant\\\":\\\"sr:exact_goals:3+\\\"}\"},{\"thirdMarketCategorySourceId\":\"SR:14\",\"thirdMarketSourceId\":\"23323959_14_0:3\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":2,\"oddsName\":\"不利 0:3\",\"addition1\":\"-3\",\"modifyTime\":1599565557002,\"marketOddsList\":[],\"extraInfo\":\"{\\\"hcp\\\":\\\"0:3\\\"}\"},{\"thirdMarketCategorySourceId\":\"SR:14\",\"thirdMarketSourceId\":\"23323959_14_0:1\",\"marketType\":0,\"dataSourceCode\":\"SR\",\"status\":0,\"oddsName\":\"不利 0:1\",\"addition1\":\"-1\",\"modifyTime\":1599565557002,\"marketOddsList\":[{\"active\":1,\"oddsType\":\"1\",\"addition1\":\"556750\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_14_0:1_1711\",\"orderOdds\":1,\"oddsValue\":157000,\"originalOddsValue\":169709,\"thirdTempletSourceId\":\"SR:14:1711\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557002,\"extraInfo\":\"14And1711\"},{\"active\":1,\"oddsType\":\"X\",\"addition1\":\"0\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_14_0:1_1712\",\"orderOdds\":2,\"oddsValue\":305000,\"originalOddsValue\":344131,\"thirdTempletSourceId\":\"SR:14:1712\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557003,\"extraInfo\":\"14And1712\"},{\"active\":1,\"oddsType\":\"2\",\"addition1\":\"423771\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"thirdOddsFieldSourceId\":\"23323959_14_0:1_1713\",\"orderOdds\":3,\"oddsValue\":725000,\"originalOddsValue\":832163,\"thirdTempletSourceId\":\"SR:14:1713\",\"dataSourceCode\":\"SR\",\"modifyTime\":1599565557003,\"extraInfo\":\"14And1713\"}],\"extraInfo\":\"{\\\"hcp\\\":\\\"0:1\\\"}\"}]},\"dataSourceTime\":1599565556988}";
        Request<ThirdMatchResultDTO> request = new Request<>();
        request = JSON.parseObject(data, request.getClass());
        ThirdMatchResultDTO dto = JSON.parseObject(JSON.toJSONString(request.getData()), ThirdMatchResultDTO.class);
        request.setData(dto);
        thirdMarketResultProcessor.thirdMarketResultApi(request);
    }

    //   盘口取消时调用
    @Test
    public void thirdBetCancelApiTest() {
        String data = "{\"linkId\":\"ac12b2f62020090812125399836bd7bb\",\"data\":{\"product\":10" +
                "" +
                "" +
                ",\"startTime\":1599534253775,\"endTime\":1599538308000,\"sourceTimestamp\":1599538373872,\"sendTimestamp\":1599538373872,\"dataSourceCode\":\"SR\",\"thirdSourceMatchId\":\"23450157\",\"markets\":[]},\"dataSourceTime\":1599538373997}";
        Request<ThirdBetCancelDTO> request = new Request<>();
        request = JSON.parseObject(data, request.getClass());
        ThirdBetCancelDTO dto = JSON.parseObject(JSON.toJSONString(request.getData()), ThirdBetCancelDTO.class);
        request.setData(dto);
        thirdBetCancelProcessor.thirdBetCancel(request);
    }

    //   回滚盘口取消时调用
    @Test
    public void thirdBetCancelRollbackApiTest() {
        String data = "{\n" +
                "    \"data\":{\n" +
                "        \"dataSourceCode\":\"SR\",\n" +
                "        \"product\":5,\n" +
                "        \"thirdSourceMatchId\":\"18741638\",\n" +
                "        \"sourceTimestamp\":1577169820647,\n" +
                "        \"sendTimestamp\":\"1579169820647\",\n" +
                "        \"startTime\":\"1579169820647\",\n" +
                "        \"endTime\":\"1579169820647\",\n" +
                "        \"markets\":[\n" +
                "            {\n" +
                "                \"thirdSourceMarketId\":\"18741638_36_2.5\",\n" +
                "                \"reason\":\"zhudong\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"thirdSourceMarketId\":\"18741638_18_3.25\",\n" +
                "                \"reason\":\"shiwu\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"linkId\":\"qc26b1t72317710564122343128b87872b4\"\n" +
                "}";
        Request<ThirdBetCancelRollbackDTO> request = new Request<>();
        request = JSON.parseObject(data, request.getClass());
        ThirdBetCancelRollbackDTO dto = JSON.parseObject(JSON.toJSONString(request.getData()), ThirdBetCancelRollbackDTO.class);
        request.setData(dto);
        thirdBetCancelRollbackProcessor.thirdBetCancelRollback(request);
    }


    //   回滚盘口结算操作
    @Test
    public void thirdBetSettlementRollbackApiTest() {
        String data = "{\"data\":{\"dataSourceCode\":\"SR\",\"product\":4,\"thirdSourceMatchId\":\"21254693\",\"sourceTimestamp\":1577169820647, \"sendTimestamp\":\"1579169820647\", \"startTime\":\"1579169820647\", \"endTime\":\"1579169820647\",\"markets\":[{\"thirdSourceMarketId\":\"21254693_19_1.5\",\"reason\":\"zhudong\"},{\"thirdSourceMarketId\":\"21254693_23_sr:exact_goals:3+\",\"reason\":\"shiwu\"}]},\"linkId\":\"aa22b1f72317812554122343128b887276\"}";
        Request<ThirdBetSettlementRollbackDTO> request = new Request<>();
        request = JSON.parseObject(data, request.getClass());
        ThirdBetSettlementRollbackDTO dto = JSON.parseObject(JSON.toJSONString(request.getData()), ThirdBetSettlementRollbackDTO.class);
        request.setData(dto);
        thirdBetSettlementRollbackProcessor.thirdBetSettlementRollback(request);
    }

    @Test
    public void thirdMarketStatusApi()
    {
        thirdMarketStatusApi.putThirdMarketStatus("22434401_68_68.5");
    }

    @Test
    public void testPutTradeMarketStatusConfig()
    {
        String data = "{\n" +
                "    \"linkId\": \"SR_ac12b2f6202109211736323026d7880f\",\n" +
                "    \"data\": {\n" +
                "        \"sportId\": 1,\n" +
                "        \"thirdMatchSourceId\": \"29023556\",\n" +
                "        \"thirdTournamentSourceId\": \"sr:tournament:782\",\n" +
                "        \"dataSourceCode\": \"SR\",\n" +
                "        \"modifyTime\": 1632216992343,\n" +
                "        \"marketList\": [\n" +
                "\t\t{\n" +
                "                \"thirdMarketCategorySourceId\": \"SR:23\",\n" +
                "                \"thirdMarketSourceId\": \"29023556_23_sr:exact_goals:3+\",\n" +
                "                \"marketType\": 1,\n" +
                "                \"dataSourceCode\": \"SR\",\n" +
                "                \"status\": 0,\n" +
                "                \"oddsName\": \"Guizhou FC 准确进球\",\n" +
                "                \"addition1\": \"3\",\n" +
                "                \"modifyTime\": 1632216992330,\n" +
                "                \"numberOfWinners\": 1,\n" +
                "                \"i18nNames\": [\n" +
                "                    {\n" +
                "                        \"languageType\": \"en\",\n" +
                "                        \"text\": \"Guizhou FC exact goals\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"languageType\": \"zs\",\n" +
                "                        \"text\": \"Guizhou FC 准确进球\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"marketOddsList\": [\n" +
                "                    {\n" +
                "                        \"active\": 1,\n" +
                "                        \"oddsType\": \"0\",\n" +
                "                        \"addition1\": \"50017\",\n" +
                "                        \"thirdOddsFieldSourceId\": \"29023556_23_sr:exact_goals:3+_sr:exact_goals:3+:88\",\n" +
                "                        \"orderOdds\": 1,\n" +
                "                        \"oddsValue\": 210000,\n" +
                "                        \"originalOddsValue\": 246858,\n" +
                "                        \"thirdTempletSourceId\": \"None\",\n" +
                "                        \"dataSourceCode\": \"SR\",\n" +
                "                        \"modifyTime\": 1632216992330,\n" +
                "                        \"i18nNames\": [\n" +
                "                            {\n" +
                "                                \"languageType\": \"en\",\n" +
                "                                \"text\": \"0\"\n" +
                "                            },\n" +
                "                            {\n" +
                "                                \"languageType\": \"zs\",\n" +
                "                                \"text\": \"0\"\n" +
                "                            }\n" +
                "                        ],\n" +
                "                        \"extraInfo\": \"23Andsr:exact_goals:3+:88\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"active\": 1,\n" +
                "                        \"oddsType\": \"1\",\n" +
                "                        \"addition1\": \"50017\",\n" +
                "                        \"thirdOddsFieldSourceId\": \"29023556_23_sr:exact_goals:3+_sr:exact_goals:3+:89\",\n" +
                "                        \"orderOdds\": 2,\n" +
                "                        \"oddsValue\": 229000,\n" +
                "                        \"originalOddsValue\": 271457,\n" +
                "                        \"thirdTempletSourceId\": \"None\",\n" +
                "                        \"dataSourceCode\": \"SR\",\n" +
                "                        \"modifyTime\": 1632216992330,\n" +
                "                        \"i18nNames\": [\n" +
                "                            {\n" +
                "                                \"languageType\": \"en\",\n" +
                "                                \"text\": \"1\"\n" +
                "                            },\n" +
                "                            {\n" +
                "                                \"languageType\": \"zs\",\n" +
                "                                \"text\": \"1\"\n" +
                "                            }\n" +
                "                        ],\n" +
                "                        \"extraInfo\": \"23Andsr:exact_goals:3+:89\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"active\": 1,\n" +
                "                        \"oddsType\": \"2\",\n" +
                "                        \"addition1\": \"50017\",\n" +
                "                        \"thirdOddsFieldSourceId\": \"29023556_23_sr:exact_goals:3+_sr:exact_goals:3+:90\",\n" +
                "                        \"orderOdds\": 3,\n" +
                "                        \"oddsValue\": 480000,\n" +
                "                        \"originalOddsValue\": 608580,\n" +
                "                        \"thirdTempletSourceId\": \"None\",\n" +
                "                        \"dataSourceCode\": \"SR\",\n" +
                "                        \"modifyTime\": 1632216992330,\n" +
                "                        \"i18nNames\": [\n" +
                "                            {\n" +
                "                                \"languageType\": \"en\",\n" +
                "                                \"text\": \"2\"\n" +
                "                            },\n" +
                "                            {\n" +
                "                                \"languageType\": \"zs\",\n" +
                "                                \"text\": \"2\"\n" +
                "                            }\n" +
                "                        ],\n" +
                "                        \"extraInfo\": \"23Andsr:exact_goals:3+:90\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"active\": 1,\n" +
                "                        \"oddsType\": \"3+\",\n" +
                "                        \"addition1\": \"50017\",\n" +
                "                        \"thirdOddsFieldSourceId\": \"29023556_23_sr:exact_goals:3+_sr:exact_goals:3+:91\",\n" +
                "                        \"orderOdds\": 4,\n" +
                "                        \"oddsValue\": 1250000,\n" +
                "                        \"originalOddsValue\": 1607443,\n" +
                "                        \"thirdTempletSourceId\": \"None\",\n" +
                "                        \"dataSourceCode\": \"SR\",\n" +
                "                        \"modifyTime\": 1632216992330,\n" +
                "                        \"i18nNames\": [\n" +
                "                            {\n" +
                "                                \"languageType\": \"en\",\n" +
                "                                \"text\": \"3+\"\n" +
                "                            },\n" +
                "                            {\n" +
                "                                \"languageType\": \"zs\",\n" +
                "                                \"text\": \"3+\"\n" +
                "                            }\n" +
                "                        ],\n" +
                "                        \"extraInfo\": \"23Andsr:exact_goals:3+:91\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"extraInfo\": \"{\\\"variant\\\":\\\"sr:exact_goals:3+\\\"}\"\n" +
                "            }\n" +
                "            \n" +
                "\t\t]\n" +
                "\t\t}\n" +
                "\t\t}";
        Request<TradeMarketStatusConfigDTO> request = new Request<>();
        request = JSON.parseObject(data, request.getClass());
        TradeMarketStatusConfigDTO tradeMarketStatusConfigDTO = JSON.parseObject(JSON.toJSONString(request.getData()), TradeMarketStatusConfigDTO.class);
        request.setData(tradeMarketStatusConfigDTO);
        Response s = tradeMarketConfigApiService.putTradeMarketStatusConfig(request);
        System.out.println(s);
    }
    @Test
    public void testPutTradePlaceNumAutoDiffConfig()
    {
        String data = "{\"data\":{\"diffConfigs\":{\"diffValue\":-0.02,\"marketCategoryId\":38,\"oddType\":\"Under\",\"placeNum\":1},\"matchId\":1815833},\"dataSourceTime\":1611196553891,\"dataType\":\"putTradePlaceNumAutoDiffConfig\",\"linkId\":\"ff4b57970d2441fdac32a25abbe79f5f_trade1\",\"operaterId\":10018}";
        Request<TradePlaceNumAutoDiffConfigDTO> request = new Request<>();
        request = JSON.parseObject(data, request.getClass());
        TradePlaceNumAutoDiffConfigDTO tradeMarketStatusConfigDTO = JSON.parseObject(JSON.toJSONString(request.getData()), TradePlaceNumAutoDiffConfigDTO.class);
        request.setData(tradeMarketStatusConfigDTO);
        Response s = tradeMarketConfigApiService.putTradePlaceNumAutoDiffConfig(request);
        System.out.println(s);
    }

    @Test
    public void updateMarketCategoryDataSourceCode()
    {
        String data = "{\"data\":[{\"dataSourceCode\":\"TX\",\"marketCategoryId\":1,\"marketType\":\"1\",\"matchId\":1967914,\"sellStatus\":\"Sold\"},{\"dataSourceCode\":\"TX\",\"marketCategoryId\":2,\"marketType\":\"1\",\"matchId\":1967914,\"sellStatus\":\"Sold\"},{\"dataSourceCode\":\"TX\",\"marketCategoryId\":4,\"marketType\":\"1\",\"matchId\":1967914,\"sellStatus\":\"Sold\"},{\"dataSourceCode\":\"TX\",\"marketCategoryId\":3,\"marketType\":\"1\",\"matchId\":1967914,\"sellStatus\":\"Sold\"},{\"dataSourceCode\":\"TX\",\"marketCategoryId\":7,\"marketType\":\"1\",\"matchId\":1967914,\"sellStatus\":\"Sold\"}],\"dataSourceTime\":1614243060283,\"linkId\":\"fd2974ceaa67483293881ec8cb7048ae_trade1\",\"operaterId\":10018}";
        Request<List<UpdateMarketCategoryDataSourceCodeDTO>> request  = new Request<>();;
        request = JSON.parseObject(data,request.getClass());
        List<UpdateMarketCategoryDataSourceCodeDTO> tradeMarketUiConfigDTOList = new ArrayList<>();
        List tradeMarketMarginConfigDTOs = JSON.parseObject(JSON.toJSONString(request.getData()), List.class);
        for(Object obj : tradeMarketMarginConfigDTOs){
            UpdateMarketCategoryDataSourceCodeDTO tradeMarketUiConfigDTO = JSON.parseObject(JSON.toJSONString(obj), UpdateMarketCategoryDataSourceCodeDTO.class);
            tradeMarketUiConfigDTOList.add(tradeMarketUiConfigDTO);
        }
        request.setData(tradeMarketUiConfigDTOList);
        Response s = tradeMarketConfigApiService.updateMarketCategoryDataSourceCode(request);
        System.out.println(s);
    }

}
