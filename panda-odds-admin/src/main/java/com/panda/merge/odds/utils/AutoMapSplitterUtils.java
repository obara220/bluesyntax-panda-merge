package com.panda.merge.odds.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Map 分割工具类：根据大小阈值自动判断是否分割
 */
public class AutoMapSplitterUtils {

    /**
     * 核心方法：自动判断并分割 Map
     * @param originalMap 原始 Map
     * @param threshold 分割阈值（当 Map.size > threshold 时才分割）
     * @param chunkSize 每个小 Map 的最大元素数（分割时生效）
     * @return 处理后的 Map 列表（未分割则列表仅含原 Map）
     */
    public static <K, V> List<Map<K, V>> autoSplitMap(
            Map<K, V> originalMap,
            int threshold,
            int chunkSize) {

        // 1. 空值/空Map处理
        if (originalMap == null || originalMap.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 参数合法性校验
        if (threshold < 0) {
            throw new IllegalArgumentException("分割阈值不能为负数");
        }
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("小Map的最大元素数必须大于0");
        }

        // 3. 判断是否需要分割
        int mapSize = originalMap.size();
        List<Map<K, V>> result = new ArrayList<>();

        if (mapSize <= threshold) {
            // 未超过阈值：不分割，直接返回原Map
            result.add(new HashMap<>(originalMap)); // 新建Map避免外部修改影响
            return result;
        } else {
            // 超过阈值：执行分割
            return splitMapByFixedSize(originalMap, chunkSize);
        }
    }

    /**
     * 辅助方法：按固定大小分割Map（复用之前的核心逻辑）
     */
    private static <K, V> List<Map<K, V>> splitMapByFixedSize(Map<K, V> originalMap, int chunkSize) {
        List<Map<K, V>> resultList = new ArrayList<>();
        List<Map.Entry<K, V>> entryList = new ArrayList<>(originalMap.entrySet());
        int totalSize = entryList.size();
        int currentIndex = 0;

        while (currentIndex < totalSize) {
            int endIndex = Math.min(currentIndex + chunkSize, totalSize);
            List<Map.Entry<K, V>> subEntryList = entryList.subList(currentIndex, endIndex);

            Map<K, V> subMap = new HashMap<>();
            for (Map.Entry<K, V> entry : subEntryList) {
                subMap.put(entry.getKey(), entry.getValue());
            }
            resultList.add(subMap);
            currentIndex = endIndex;
        }
        return resultList;
    }

/*    // 测试示例（模拟实际使用场景）
    public static void main(String[] args) {
        // 1. 构建测试Map（15个元素）
        Map<String, String> testMap = new HashMap<>();
        for (int i = 1; i <= 15; i++) {
            testMap.put("key" + i, "value" + i);
        }

        // 2. 场景1：阈值10，每个小Map最多5个元素（15>10，触发分割）
        System.out.println("=== 场景1：Map大小(15) > 阈值(10)，触发分割 ===");
        List<Map<String, String>> splitResult1 = autoSplitMap(testMap, 10, 5);
        for (int i = 0; i < splitResult1.size(); i++) {
            System.out.println("小Map" + (i+1) + "：" + splitResult1.get(i) + " (大小：" + splitResult1.get(i).size() + ")");
        }

        // 3. 场景2：阈值20，每个小Map最多5个元素（15<20，不分割）
        System.out.println("\n=== 场景2：Map大小(15) < 阈值(20)，不分割 ===");
        List<Map<String, String>> splitResult2 = autoSplitMap(testMap, 20, 5);
        for (int i = 0; i < splitResult2.size(); i++) {
            System.out.println("结果Map" + (i+1) + "：" + splitResult2.get(i) + " (大小：" + splitResult2.get(i).size() + ")");
        }
    }*/
}
