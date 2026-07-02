package com.panda.merge.dubbo;


import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.panda.merge.api.IStandardSportPlayerQueryApi;
import com.panda.merge.bo.StandardSportPlayerBO;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.dto.*;
import com.panda.merge.model.LanguageInternation;
import com.panda.merge.service.StandardSportPlayerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.PROJECT_ID_NOREALTIME;
import static com.panda.merge.constant.ConstantSystem.QUERY_STANDARD_SPORT_PLAYER_BY_UPDATE_TIME;


/**
 * 标准球员信息相关查询
 * @author :  tell
 * @since  :  2020年9月9日11:33:27
 */
@Slf4j
@Component
@DubboService
public class StandardSportPlayerQueryApiImpl  extends BaseProcessor implements IStandardSportPlayerQueryApi {

    @Autowired
    private StandardSportPlayerService standardSportPlayerService;

    @Override
    public Response<PageModel<List<StandardSportPlayerBO>>> queryStandardSportPlayerByUpdateTime(Request<PageModel<StandardSportPlayerDTO>> request) {
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_STANDARD_SPORT_PLAYER_BY_UPDATE_TIME+"】【::"+request.getLinkId()+"::】根据修改时间分页查询标准球员信息开始,入参：{}",JSON.toJSONString(request.getData()));
        Page<StandardSportPlayerDetail> resPage = standardSportPlayerService.getPageItemGreaterThanOrModifyTime(request.getData());
        //转换后的数据
        List<StandardSportPlayerBO> resList = new LinkedList<>();
        if(!CollectionUtils.isEmpty(resPage)){
            //查询球员多语言信息
            List<Long> nameCodes = resPage.stream().map(obj -> obj.getNameCode()).collect(Collectors.toList());
            Map<Long, List<LanguageInternation>> nameCode2Languages = languageInternationService.getItemsByNameCodes(nameCodes);
            for (StandardSportPlayerDetail standardSportPlayer: resPage) {
                StandardSportPlayerBO standardSportPlayerBO = new StandardSportPlayerBO();
                BeanUtils.copyProperties(standardSportPlayer, standardSportPlayerBO);
                List<LanguageInternation> languageList = nameCode2Languages.get(standardSportPlayer.getNameCode());
                if(!CollectionUtils.isEmpty(languageList)){
                    standardSportPlayerBO.setIl8nNameList(getI18nItemBOList(languageList));
                }
                resList.add(standardSportPlayerBO);
                log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_STANDARD_SPORT_PLAYER_BY_UPDATE_TIME+"】【::"+request.getLinkId()+"::】分页查询标准球员信息 ：{}" ,standardSportPlayerBO.getId());
            }
        }
        //转换后的分页对象
        PageModel<List<StandardSportPlayerBO>> pageModel = new PageModel(resPage.getPageSize(),resPage.getPageNum());
        pageModel.setTotal(resPage.getTotal());
        response.setData(pageModel);
        response.setDataSourceTime(System.currentTimeMillis() - beginTime);
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_STANDARD_SPORT_PLAYER_BY_UPDATE_TIME+"】【::"+request.getLinkId()+"::】根据修改时间分页查询标准球员信息结束,返回结果 ：{}" ,JSON.toJSONString(response));
        pageModel.setData(resList);
        return response;
    }
}
