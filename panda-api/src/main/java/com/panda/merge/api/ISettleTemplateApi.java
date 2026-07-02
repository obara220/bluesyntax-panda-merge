package com.panda.merge.api;

import com.panda.merge.dto.Response;
import com.panda.merge.dto.settle.*;
import com.panda.merge.model.MatchSettleTemplate;


public interface ISettleTemplateApi {
    /**
     *  赛事联赛结算模板查询
     * 传参：
     * 返回:
     * */
    Response list(TemplateListSearchDto templateListSearchDto);
    /**
     * 数据商权重查询
     * */
    Response searchTemplate (DataSourceWeightSearchDto dataSourceWeightSearchDto);

    /**
     * 批量需改
     * */
    Response templateBatchUpdate(SettleTemplateBatchUpdateDto settleTemplateBatchUpdateDto);
    /**
     * 新增
     * */
    Response addTemplate(MatchSettleTemplateDto matchSettleTemplate);
    /**
     * 删除
     * */
    Response deleteTemplate(SettleTemplateBatchUpdateDto settleTemplateUpdateDto);
    /**
     * 编辑数据商权重
     * */
   Response editWeightTemplate(SettleWeightTemplateUpdateDto settleTemplateUpdateDto);
    /**
     * 编辑灰色区间
     * */
   Response editGrayAreaTemplate(SettleGrayTemplateUpdateDto settleGrayTemplateUpdateDto);
   /**
    * 根据赛事获取联赛结算模版
    * */
   Response getMatchTemplateByMatchId(Long standardMatchId);

    /**
     * 根据球种类型和数据源编码获取数据源的开关和各联赛级别的权重信息
     * */
    Response getDataSourceAllWeightByCode(MatchSettleDataSourceWeightAndSwitchDto matchSettleDataSourceWeightAndSwitchDto);

    /**
     * 根据数据源编码编辑该数据源的各联赛级别权重与开关
     * */
    Response editDataSourceAllWeight(DataSourceWeightUpdateDto DataSourceWeightUpdateDto);

    /**
     * 根据球种获取现有模板中有哪些数据商
     * @param sportId
     * @return
     */
    Response getDataSourceFromTemlate(Long sportId);

    /**
     * 新增数据商及初始化参数
     * @param matchSettleDataSourceWeightAndSwitchDto
     * @return
     */
    Response addNewDataSource(MatchSettleDataSourceWeightAndSwitchDto matchSettleDataSourceWeightAndSwitchDto);

    /**
     * 删除数据商
     * @param matchSettleDataSourceWeightAndSwitchDto
     * @return
     */
    Response delDataSource(MatchSettleDataSourceWeightAndSwitchDto matchSettleDataSourceWeightAndSwitchDto);

    /**
     * 编辑数据商编码
     * @param dataSourceWeightUpdateDto
     * @return
     */
    Response editDataSourceCode(DataSourceWeightUpdateDto dataSourceWeightUpdateDto);


    Response updateDataSourceWeightConfig(DataSourceWeightUpdateDto DataSourceWeightUpdateDto);
//
//    Response getDataSourceAllWeightNumByCode (DataSourceWeightUpdateDto DataSourceWeightUpdateDto);

    Response editDownTemplate(SettleDownTemplateUpdateDto settleDownTemplateUpdateDto);

}
