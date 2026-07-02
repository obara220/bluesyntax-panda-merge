package com.panda.merge.config;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 类名映射工具类，用于处理包名迁移时的类型兼容性问题
 * 当 Redis 中存储的数据使用了旧的包名，而当前项目使用新包名时，需要进行类型映射
 * 
 * @author Auto-generated
 */
@Slf4j
public class ClassNameMappingUtil {
    
    /**
     * 类名映射表：旧类名 -> 新类名
     * 如果项目迁移后包名改变，需要在这里添加映射关系
     * 例如：如果旧包名是 com.oldpackage.service.settleMention.dto.FootballMentionStatus
     *      新包名是 com.panda.merge.service.settleMention.dto.FootballMentionStatus
     *      则添加映射：com.oldpackage.service.settleMention.dto.FootballMentionStatus -> com.panda.merge.service.settleMention.dto.FootballMentionStatus
     */
//    private static final Map<String, String> CLASS_NAME_MAPPING = new HashMap<>();
    
    /**
     * 包名前缀映射表：旧包名前缀 -> 新包名前缀
     * 用于批量替换包名前缀，更灵活
     * 例如：com.oldpackage.service -> com.panda.merge.service
     */
    private static final Map<String, String> PACKAGE_PREFIX_MAPPING = new HashMap<>();
    
    static {
        // ====================================================================
        // 包名迁移映射配置说明
        // ====================================================================
        // 场景：Redis 中的数据由存储项目写入，使用包名 com.panda.merge.service.settleMention.dto
        //      读取项目使用不同的包名，例如 com.panda.datacenter.merge.service.settleMention.dto
        //      需要在读取项目的 ClassNameMappingUtil 中配置映射关系
        //
        // 配置方式（二选一）：
        // 方式1：使用包名前缀映射（推荐，更灵活，自动处理所有相关类）
        // 方式2：使用精确类名映射（如果需要精确控制特定类）
        // ====================================================================
        
        // 方式1：包名前缀映射（推荐）
        // 将 Redis 中的包名 com.panda.merge.service.settleMention.dto 
        // 映射到读取项目的包名 com.panda.datacenter.merge.service.settleMention.dto
        // 取消下面的注释并配置正确的映射关系
//         PACKAGE_PREFIX_MAPPING.put("com.panda.merge.service.settleMention.dto",
//                                    "com.panda.datacenter.merge.service.settleMention.dto");
        PACKAGE_PREFIX_MAPPING.put("com.panda.datacenter.merge.service.settleMention.dto",
                "com.panda.merge.service.settleMention.dto");

        // 方式2：精确类名映射（如果需要精确控制）
        // CLASS_NAME_MAPPING.put("com.panda.merge.service.settleMention.dto.FootballMentionStatus", 
        //                        "com.panda.datacenter.merge.service.settleMention.dto.FootballMentionStatus");
        // CLASS_NAME_MAPPING.put("com.panda.merge.service.settleMention.dto.BasketballMentionStatus", 
        //                        "com.panda.datacenter.merge.service.settleMention.dto.BasketballMentionStatus");
        // CLASS_NAME_MAPPING.put("com.panda.merge.service.settleMention.dto.AbstractMentionStatus", 
        //                        "com.panda.datacenter.merge.service.settleMention.dto.AbstractMentionStatus");
        // CLASS_NAME_MAPPING.put("com.panda.merge.service.settleMention.dto.AbstractMentionStatus$EventStatus", 
        //                        "com.panda.datacenter.merge.service.settleMention.dto.AbstractMentionStatus$EventStatus");
    }
    
    /**
     * 替换 JSON 字符串中的类名
     * FastJSON 使用 @type 字段存储类名，格式为 "@type":"com.package.ClassName"
     * 
     * @param jsonStr 原始 JSON 字符串
     * @return 替换后的 JSON 字符串
     */
    public static String replaceClassNameInJson(String jsonStr) {
        if (jsonStr == null || jsonStr.isEmpty()) {
            return jsonStr;
        }
        
        String result = jsonStr;
        
        // 先进行精确的类名映射
//        for (Map.Entry<String, String> entry : CLASS_NAME_MAPPING.entrySet()) {
//            String oldClassName = entry.getKey();
//            String newClassName = entry.getValue();
//
//            // 替换 @type 字段中的类名
//            // 匹配格式："@type":"oldClassName" 或 "@type":"oldClassName$InnerClass"
//            // 使用 Pattern.quote 来转义特殊字符，确保精确匹配
//            String escapedOldClassName = Pattern.quote(oldClassName);
//            // 匹配 "@type":"oldClassName" 或 "@type":"oldClassName$..."
//            String pattern = "\"@type\"\\s*:\\s*\"(" + escapedOldClassName + ")(\\$[^\"]*)?\"";
//            result = result.replaceAll(pattern, "\"@type\":\"" + newClassName + "$2\"");
//        }
        
        // 再进行包名前缀映射（批量替换）
        for (Map.Entry<String, String> entry : PACKAGE_PREFIX_MAPPING.entrySet()) {
            String oldPrefix = entry.getKey();
            String newPrefix = entry.getValue();
            
            // 替换 @type 字段中的包名前缀
            // 匹配格式："@type":"oldPrefix.ClassName" 或 "@type":"oldPrefix.ClassName$InnerClass"
            String escapedOldPrefix = Pattern.quote(oldPrefix);
            // 匹配 "@type":"oldPrefix.ClassName" 或 "@type":"oldPrefix.ClassName$..."
            String pattern = "\"@type\"\\s*:\\s*\"(" + escapedOldPrefix + ")(\\.[^\"]*)?\"";
            result = result.replaceAll(pattern, "\"@type\":\"" + newPrefix + "$2\"");
        }
        
        if (!result.equals(jsonStr)) {
            log.debug("Class name mapping applied. Original length: {}, Mapped length: {}", 
                    jsonStr.length(), result.length());
            // 只在调试级别记录完整内容，避免日志过大
            if (log.isTraceEnabled()) {
                log.trace("Class name mapping details. Original: {}, Mapped: {}", jsonStr, result);
            }
        }
        
        return result;
    }
    
    /**
     * 添加类名映射（运行时动态添加）
     * 
     * @param oldClassName 旧类名
     * @param newClassName 新类名
     */
//    public static void addClassNameMapping(String oldClassName, String newClassName) {
//        CLASS_NAME_MAPPING.put(oldClassName, newClassName);
//        log.info("Added class name mapping: {} -> {}", oldClassName, newClassName);
//    }
    
    /**
     * 添加包名前缀映射（运行时动态添加）
     * 
     * @param oldPrefix 旧包名前缀
     * @param newPrefix 新包名前缀
     */
    public static void addPackagePrefixMapping(String oldPrefix, String newPrefix) {
        PACKAGE_PREFIX_MAPPING.put(oldPrefix, newPrefix);
        log.info("Added package prefix mapping: {} -> {}", oldPrefix, newPrefix);
    }
    
    /**
     * 清除所有映射（用于测试或重置）
     */
    public static void clearMappings() {
//        CLASS_NAME_MAPPING.clear();
        PACKAGE_PREFIX_MAPPING.clear();
        log.info("Cleared all class name mappings");
    }
}

