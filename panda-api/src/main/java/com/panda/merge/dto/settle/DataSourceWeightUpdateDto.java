package com.panda.merge.dto.settle;

import lombok.Data;

import java.util.List;

@Data
public class DataSourceWeightUpdateDto extends AbstructMatchSettleDto{

    private Long sportId;

    private String  dataSourceCode;
    private String  switchJson; //开关Json
    private String updateJson; //权重等数字Json

    //用于修改数据商编码
    private String newDataSourceCode;

    //权重上限Json
    private String weightConfigJson;
}
