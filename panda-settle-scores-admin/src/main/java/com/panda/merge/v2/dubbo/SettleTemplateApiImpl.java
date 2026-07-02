package com.panda.merge.v2.dubbo;

import com.panda.merge.api.ISettleTemplateApi;
import com.panda.merge.dto.*;
import com.panda.merge.dto.settle.*;
import com.panda.merge.v2.controllerv2.MatchSettleTemplateController;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@DubboService
@Slf4j
public class SettleTemplateApiImpl implements ISettleTemplateApi {

    @Autowired
    private MatchSettleTemplateController settleTemplateController;

    @Override
    public Response list(TemplateListSearchDto templateListSearchDto) {
        return settleTemplateController.list(templateListSearchDto);
    }

    @Override
    public Response searchTemplate(DataSourceWeightSearchDto dataSourceWeightSearchDto) {
        return settleTemplateController.searchTemplate(dataSourceWeightSearchDto);
    }

    @Override
    public Response templateBatchUpdate(SettleTemplateBatchUpdateDto settleTemplateUpdateDto) {
        return settleTemplateController.templateBatchUpdate(settleTemplateUpdateDto);
    }

    @Override
    public Response addTemplate(MatchSettleTemplateDto matchSettleTemplateDto) {
        return settleTemplateController.addTemplate(matchSettleTemplateDto);
    }

    @Override
    public Response deleteTemplate(SettleTemplateBatchUpdateDto settleTemplateUpdateDto) {
        return settleTemplateController.deleteTemplate(settleTemplateUpdateDto);
    }

    @Override
    public Response editWeightTemplate(SettleWeightTemplateUpdateDto settleTemplateUpdateDto) {
        return settleTemplateController.editWeightTemplate(settleTemplateUpdateDto);
    }

    @Override
    public Response editGrayAreaTemplate(SettleGrayTemplateUpdateDto settleGrayTemplateUpdateDto) {
        return settleTemplateController.editGrayAreaTemplate(settleGrayTemplateUpdateDto);
    }

    @Override
    public Response getMatchTemplateByMatchId(Long standardMatchId) {
        return settleTemplateController.getMatchTemplateByMatchId(standardMatchId);
    }


    /**
     * 根据球种类型和数据源编码获取数据源的开关和各联赛级别的权重信息
     */
    @Override
    public Response getDataSourceAllWeightByCode(MatchSettleDataSourceWeightAndSwitchDto matchSettleDataSourceWeightAndSwitchDto) {
        return settleTemplateController.getDataSourceAllWeightByCode(matchSettleDataSourceWeightAndSwitchDto);
    }

    @Override
//public  Response getDataSourceAllWeightNumByCode(DataSourceWeightUpdateDto dataSourceWeightUpdateDto){
//    //封装各个联赛的权重上限
//    MatchSettleDataSourceWeightConfigExample weightConfigExample = new MatchSettleDataSourceWeightConfigExample();
//    weightConfigExample.createCriteria().andDataSourceCodeEqualTo(dataSourceWeightUpdateDto.getDataSourceCode()).andSportIdEqualTo(dataSourceWeightUpdateDto.getSportId());
//    List<MatchSettleDataSourceWeightConfig> weightConfigs = matchSettleDataSourceWeightConfigMapper.selectByExample(weightConfigExample);
//    List<DataScoreWeightNumDto> list = new ArrayList<>();
//    if (!CollectionUtils.isEmpty(weightConfigs)){
//        weightConfigs.forEach(d->{
//            DataScoreWeightNumDto dto = new DataScoreWeightNumDto();
//            dto.setDataScoreCode(d.getDataSourceCode());
//            dto.setWeightNum(d.getWeightNum());
//            dto.setLevel(d.getTournamentLevel());
//            list.add(dto);
//        });
//    }
//        return Response.success(list);
//}

    /**
     * 根据数据源编码编辑该数据源的各联赛级别权重与开关
     * */
    public Response editDataSourceAllWeight(DataSourceWeightUpdateDto dataSourceWeightUpdateDto) {
        return settleTemplateController.editDataSourceAllWeight(dataSourceWeightUpdateDto);
    }

    public Response updateDataSourceWeightConfig(DataSourceWeightUpdateDto dataSourceWeightUpdateDto) {
        return settleTemplateController.updateDataSourceWeightConfig(dataSourceWeightUpdateDto);
    }

    @Override
    public Response editDownTemplate(SettleDownTemplateUpdateDto settleDownTemplateUpdateDto) {
        return settleTemplateController.editDownTemplate(settleDownTemplateUpdateDto);
    }

    /**
     * 根据球种获取模板里的数据源编码
     *
     * @param sportId
     * @return
     */
    public Response getDataSourceFromTemlate(Long sportId) {
        return settleTemplateController.getDataSourceFromTemlate(sportId);
    }

    /**
     * 新增数据商及初始化参数
     *
     * @param matchSettleDataSourceWeightAndSwitchDto
     * @return
     */

    public Response addNewDataSource(MatchSettleDataSourceWeightAndSwitchDto matchSettleDataSourceWeightAndSwitchDto) {
        return settleTemplateController.addNewDataSource(matchSettleDataSourceWeightAndSwitchDto);
    }

    /**
     * 删除数据商
     *
     * @param matchSettleDataSourceWeightAndSwitchDto
     * @return
     */
    public Response delDataSource(MatchSettleDataSourceWeightAndSwitchDto matchSettleDataSourceWeightAndSwitchDto) {
        return settleTemplateController.delDataSource(matchSettleDataSourceWeightAndSwitchDto);
    }

    /**
     * 修改数据商编码
     *
     * @param dataSourceWeightUpdateDto
     * @return
     */
    public Response editDataSourceCode(DataSourceWeightUpdateDto dataSourceWeightUpdateDto) {
        return settleTemplateController.editDataSourceCode(dataSourceWeightUpdateDto);
    }




}
