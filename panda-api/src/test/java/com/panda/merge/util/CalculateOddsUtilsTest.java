//package com.panda.merge.util;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.panda.merge.dto.DiscountOddsConfigDTO;
//import com.panda.merge.dto.message.StandardMatchMarketMessage;
//import com.panda.merge.model.ConfigMarketLevel;
//import org.apache.commons.lang3.time.StopWatch;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class CalculateOddsUtilsTest {
//
//    private List<ConfigMarketLevel> configs;
//
//    private StandardMatchMarketMessage standardMatchMarketMessage;
//
//    private Map<String, DiscountOddsConfigDTO> discountOddsConfigDTOMap;
//
//    @BeforeEach
//    public void setUp() {
//        configs = new ArrayList<>();
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        try (InputStream inputStream = getClass().getClassLoader()
//                                                 .getResourceAsStream("tx.json")) {
//            standardMatchMarketMessage = objectMapper.readValue(inputStream, StandardMatchMarketMessage.class);
//            long count = standardMatchMarketMessage.getMarketList()
//                                                   .stream()
//                                                   .flatMap(market -> market.getMarketOddsList()
//                                                                            .stream())
//                                                   .count();
//            System.out.println(count);
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to load test data", e);
//        }
//
//        discountOddsConfigDTOMap = new HashMap<>();
//    }
//
//
//    @Test
//    public void testCalculateOddsByMatchLevel_NormalCategory() {
//        List<ConfigMarketLevel> configs = new ArrayList<>();
//
//        for (int i = 0; i < 5; i++) {
//            addConfigLevel(configs, i);
//        }
//
//        boolean matchTradType = false;
//        Map<String, DiscountOddsConfigDTO> discountOddsConfigDTOMap = new HashMap<>();
//
//        Map<Long, Integer> categoryMap = new HashMap<>();
//        categoryMap.put(1L, 100);
//        categoryMap.put(2L, 200);
//
//        StopWatch watch = StopWatch.createStarted();
//        for (int i = 0; i < 500; i++) {
//            CalculateOddsUtils.calculateOddsByMatchLevel(configs, standardMatchMarketMessage, categoryMap,
//                                                         matchTradType,
//                    discountOddsConfigDTOMap);
//        }
//        System.out.println(watch.getTime()/500.0);
//    }
//
//    void addConfigLevel(List<ConfigMarketLevel> configs,int level) {
//        configs.add(getConfigLevel(level,21));
//        configs.add(getConfigLevel(level,22));
//        configs.add(getConfigLevel(level,31));
//        configs.add(getConfigLevel(level,32));
//        configs.add(getConfigLevel(level,33));
//    }
//
//    ConfigMarketLevel getConfigLevel(int level,int marketTypeDetail) {
//        ConfigMarketLevel configMarketLevel = new ConfigMarketLevel();
//        configMarketLevel.setLevel(level);
//        configMarketLevel.setMarketTypeDetail(marketTypeDetail);
//        configMarketLevel.setDiffValue(0.5);
//        configMarketLevel.setSportId(1L);
//        return configMarketLevel;
//    }
//}
