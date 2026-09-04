package com.panda.merge.odds.utils;

import com.panda.merge.common.enums.DataSourceCodeEnum;

/**
 * DataSourceUtils
 *
 * @description:
 * @date: 4/28/2025
 **/
public final class DataSourceUtils {

    public static String transformDataSourceCode(String originalDataSourceCode) {
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

}
