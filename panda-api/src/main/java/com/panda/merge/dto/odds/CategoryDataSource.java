package com.panda.merge.dto.odds;

import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.model.MarketCategorySell;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.model.ThirdMatchInfo;

/**
 * CategoryDataSource
 *
 * @description: 玩法数据源切换信息
 * @date: 4/28/2025
 **/
public class CategoryDataSource {

    public String linkId;

    public Long categoryId;

    public String ods;

    public String internalOds;

    public String tds;

    public String internalTds;

    public ThirdMatchInfo thirdMatchInfo;

    public StandardMatchInfo standardMatchInfo;

    public MarketCategorySell marketCategorySell;

    public StandardSportMarketSell standardSportMarketSell;

    public int marketType;

    public int status;

    public String remark;



    public static CategoryDataSource createFromInternal(Long categoryId, String dataSourceCode,
                                                        int marketType,
                                                        String linkId) {
        CategoryDataSource result = new CategoryDataSource();
        result.categoryId = categoryId;
        result.linkId = linkId+ "_" + categoryId;
        result.marketType = marketType;
        result.internalOds = dataSourceCode;
        if (dataSourceCode.startsWith("T01")) {
            result.ods = DataSourceCodeEnum.TX.getCode();
        } else if (dataSourceCode.startsWith("L01")) {
            result.ods = DataSourceCodeEnum.LS.getCode();
        } else if (dataSourceCode.startsWith("L02")) {
            result.ods = DataSourceCodeEnum.L02.getCode();
        } else {
            result.ods = dataSourceCode;
        }
        return result;
    }

    public void setTdsFromInternal(String s) {
        this.internalTds = s;
        if (s.startsWith("T01")) {
            this.tds = DataSourceCodeEnum.TX.getCode();
        } else if (s.startsWith("L01")) {
            this.tds = DataSourceCodeEnum.LS.getCode();
        } else if (s.startsWith("L02")) {
            this.tds = DataSourceCodeEnum.L02.getCode();
        } else {
            this.tds = s;
        }
    }

    public void failed(String remark) {
        status = -1;
        this.remark = remark;
    }

    public MatchDataSourceDTO.CategoryDataSourceDTO createResponse() {
        return new MatchDataSourceDTO.CategoryDataSourceDTO(categoryId, internalOds, internalTds, status, remark);
    }

}
