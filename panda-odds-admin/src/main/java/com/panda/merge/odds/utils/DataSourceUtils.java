package com.panda.merge.odds.utils;

import com.panda.merge.common.enums.DataSourceCodeEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * DataSourceUtils
 *
 * @description:
 * @date: 4/28/2025
 **/
public final class DataSourceUtils {

    private static final Set<String> EXCLUDED_N01_N02_SOURCES = new HashSet<>(Arrays.asList(
            DataSourceCodeEnum.N01.code,
            DataSourceCodeEnum.N02.code
    ));

    private DataSourceUtils() {
    }

    /** N01/N02 事件源编码（不可作为商业事件源，且不触发自动关盘） */
    public static boolean isN01OrN02DataSource(String dataSourceCode) {
        if (StringUtils.isBlank(dataSourceCode)) {
            return false;
        }
        String normalized = transformDataSourceCode(dataSourceCode);
        return EXCLUDED_N01_N02_SOURCES.stream().anyMatch(ds -> ds.equalsIgnoreCase(normalized));
    }

    public static String transformDataSourceCode(String originalDataSourceCode) {
        if (StringUtils.isBlank(originalDataSourceCode)) {
            return originalDataSourceCode;
        }
        String dataSourceCode = originalDataSourceCode;
        if (originalDataSourceCode.startsWith("T01")) {
            dataSourceCode = DataSourceCodeEnum.TX.getCode();
        } else if (originalDataSourceCode.startsWith("L01")) {
            dataSourceCode = DataSourceCodeEnum.LS.getCode();
        }else if (originalDataSourceCode.startsWith("L02")) {
            dataSourceCode = DataSourceCodeEnum.L02.getCode();
        }
        return dataSourceCode;
    }

    /** 判断玩法当前数据源与目标数据源是否一致（含 L01/LS 等内部站点归一） */
    public static boolean isSameDataSourceCode(String currentDataSourceCode, String targetDataSourceCode) {
        if (StringUtils.isAnyBlank(currentDataSourceCode, targetDataSourceCode)) {
            return false;
        }
        if (StringUtils.equalsIgnoreCase(currentDataSourceCode, targetDataSourceCode)) {
            return true;
        }
        return StringUtils.equalsIgnoreCase(transformDataSourceCode(currentDataSourceCode),
                                            transformDataSourceCode(targetDataSourceCode));
    }

}
