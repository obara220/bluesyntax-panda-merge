package com.panda.merge.dubbo;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.panda.merge.api.IOutrightMatchDataQueryApi;
import com.panda.merge.bo.OutrightMatchCateGoryInfoBO;
import com.panda.merge.bo.OutrightMatchInfoBO;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.dto.OutrightMatchInfoDTO;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.mapper.I18nnamesOutrightCategoryNameMapper;
import com.panda.merge.mapper.I18nnamesOutrightMatchNameMapper;
import com.panda.merge.mapper.StandardSportTournamentMapper;
import com.panda.merge.model.*;
import com.panda.merge.service.IOutrightMatchCategoryDataQueryService;
import com.panda.merge.service.IOutrightMatchDataQueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.panda.merge.constant.ConstantSystem.PROJECT_ID_NOREALTIME;

/**
 * @author : nonhung
 * @project Name : panda-merge
 * @package Name : com.panda.merge.dubbo
 * @description : TODO
 * @date: 2020-10-02 11:19
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Slf4j
@Component
@DubboService
public class IOutrightMatchDataQueryApiImpl implements IOutrightMatchDataQueryApi {

    @Autowired
    private IOutrightMatchCategoryDataQueryService iOutrightMatchCategoryDataQueryService;

    @Autowired
    private IOutrightMatchDataQueryService iOutrightMatchDataQueryService;

    @Autowired
    private I18nnamesOutrightMatchNameMapper i18nnamesOutrightMatchNameMapper;

    @Autowired
    private I18nnamesOutrightCategoryNameMapper i18nnamesOutrightCategoryNameMapper;

    @Autowired
    private StandardSportTournamentMapper standardSportTournamentMapper;


    @Override
    public Response<PageModel<List<OutrightMatchInfoBO>>> queryOutrihtMatch(Request<PageModel<OutrightMatchInfoDTO>> request) {
        log.info("【"+ PROJECT_ID_NOREALTIME +" ：queryOutrihtMatch】【::"+request.getLinkId()+"::】分页查询标准冠军赛事开始,入参：{}",JSON.toJSONString(request.getData()));
        List<OutrightMatchInfoBO> outrightMatchInfoBOS = new ArrayList<>();
        PageModel<OutrightMatchInfoDTO> data = request.getData();
        PageHelper.startPage(data.getCurrent(), data.getSize());
        List<StandardOutrightMatchInfo> standardOutrightMatchInfos = iOutrightMatchDataQueryService.queryOutrightMatch(request.getData().getData());
        if (CollectionUtils.isEmpty(standardOutrightMatchInfos)) {
            return Response.success();
        }
        PageInfo pageInfo = new PageInfo(standardOutrightMatchInfos);
        PageModel<List<OutrightMatchInfoBO>> pageModel = new PageModel(data.getSize(), data.getCurrent());
        pageModel.setTotal(pageInfo.getTotal());
        //查询赛事国际化
        List<Long> list = standardOutrightMatchInfos.stream().map(StandardOutrightMatchInfo::getId).collect(Collectors.toList());
        I18nnamesOutrightMatchNameExample example = new I18nnamesOutrightMatchNameExample();
        example.createCriteria().andTypeEqualTo(Constant.OUTRIGHT_TYPE.STANDARD_OUTRIGHT).andMatchCategoryFiledIn(list);
        List<I18nnamesOutrightMatchName> i18nnamesOutrightMatchNames = i18nnamesOutrightMatchNameMapper.selectByExample(example);
        Map<Long, Map<String, String>> map = i18nnamesOutrightMatchNames.stream()
                .collect(Collectors.groupingBy(I18nnamesOutrightMatchName::getMatchCategoryFiled, Collectors.toMap(I18nnamesOutrightMatchName::getLanguageType, I18nnamesOutrightMatchName::getText)));
        //查询联赛logo
        Set<Long> standardTouIdsSet = standardOutrightMatchInfos.stream().map(StandardOutrightMatchInfo::getStandardTournamentId).collect(Collectors.toSet());
        List<Long> standardTouIdsList = Stream.of(standardTouIdsSet.toArray(new Long[0])).collect(Collectors.toList());
        StandardSportTournamentExample standardSportTournamentExample = new StandardSportTournamentExample();
        standardSportTournamentExample.createCriteria().andIdIn(standardTouIdsList);
        List<StandardSportTournament> standardSportTournaments = standardSportTournamentMapper.selectByExample(standardSportTournamentExample);
        Map<Long, String> touLogo = standardSportTournaments.stream().collect(Collectors.toMap(StandardSportTournament::getId, StandardSportTournament::getLogoUrl));
        for (StandardOutrightMatchInfo matchInfo : standardOutrightMatchInfos) {
            OutrightMatchInfoBO outrightMatchInfoBO = new OutrightMatchInfoBO();
            BeanUtils.copyProperties(matchInfo, outrightMatchInfoBO);
            Map<String, String> stringStringMap = map.get(matchInfo.getId());
            outrightMatchInfoBO.setMap(MapUtils.isEmpty(stringStringMap) ? new HashMap<>() : stringStringMap);
            String logo = touLogo.get(matchInfo.getStandardTournamentId());
            outrightMatchInfoBO.setTouLogoUrl(StringUtils.isNoneBlank(logo) ? logo : "");
            outrightMatchInfoBOS.add(outrightMatchInfoBO);
            log.info("【"+ PROJECT_ID_NOREALTIME +" ：queryOutrihtMatch】【::"+request.getLinkId()+"::】分页查询标准冠军赛事信息 ：{}" ,outrightMatchInfoBO.getId());
        }
        log.info("【" + PROJECT_ID_NOREALTIME + " ：queryOutrihtMatch】【::"+request.getLinkId()+"::】分页查询标准冠军赛事返回结果 ：{}", JSON.toJSONString(pageModel));
        pageModel.setData(outrightMatchInfoBOS);
        return Response.success(pageModel);
    }

    @Override
    public Response<List<OutrightMatchCateGoryInfoBO>> queryOutrihtMatchCategory(Request<PageModel<OutrightMatchInfoDTO>> request) {
        log.info("【"+ PROJECT_ID_NOREALTIME +" ：queryOutrihtMatchCategory】【::"+request.getLinkId()+"::】查询冠军赛事玩法开始,入参：{}",JSON.toJSONString(request.getData()));
        List<OutrightMatchCateGoryInfoBO> outrightMatchInfoBOS = new ArrayList<>();
        Response<List<OutrightMatchCateGoryInfoBO>> listResponse = new Response<>();
        List<StandardOutrightMatchCategory> standardOutrightMatchCategories = iOutrightMatchCategoryDataQueryService.queryOutrihtMatchCategory(request.getData().getData());
        if (CollectionUtils.isEmpty(standardOutrightMatchCategories)) {
            return listResponse;
        }
        List<Long> list = standardOutrightMatchCategories.stream().map(StandardOutrightMatchCategory::getId).collect(Collectors.toList());
        I18nnamesOutrightCategoryNameExample example = new I18nnamesOutrightCategoryNameExample();
        example.createCriteria().andStandardOutrightCategoryIdIn(list);

        List<I18nnamesOutrightCategoryName> i18nnamesOutrightCategoryNames = i18nnamesOutrightCategoryNameMapper.selectByExample(example);
        Map<Long, Map<Long, List<I18nnamesOutrightCategoryName>>> mapMap = i18nnamesOutrightCategoryNames.stream()
                .collect(Collectors.groupingBy(I18nnamesOutrightCategoryName::getStandardOutrightCategoryId, Collectors.groupingBy(I18nnamesOutrightCategoryName::getStandardOutrightMatchId)));
        for (StandardOutrightMatchCategory category : standardOutrightMatchCategories) {
            OutrightMatchCateGoryInfoBO cateGoryInfoBO = new OutrightMatchCateGoryInfoBO();
            BeanUtils.copyProperties(category, cateGoryInfoBO);
            Map<Long, List<I18nnamesOutrightCategoryName>> standardListMap = mapMap.get(category.getId());
            if (!MapUtils.isEmpty(standardListMap)) {
                List<I18nnamesOutrightCategoryName> i18nnamesOutrightCategoryNames1 = standardListMap.get(category.getStandardMatchId());
                if (!CollectionUtils.isEmpty(i18nnamesOutrightCategoryNames1)) {
                    Map<String, String> map = i18nnamesOutrightCategoryNames1.stream().collect(Collectors.toMap(I18nnamesOutrightCategoryName::getLanguageType, I18nnamesOutrightCategoryName::getText));
                    cateGoryInfoBO.setMap(map);
                } else {
                    cateGoryInfoBO.setMap(new HashMap<>());
                }
            } else {
                cateGoryInfoBO.setMap(new HashMap<>());
            }
            outrightMatchInfoBOS.add(cateGoryInfoBO);
        }
        log.info("【" + PROJECT_ID_NOREALTIME + " ：queryOutrihtMatchCategory】【::"+request.getLinkId()+"::】查询冠军赛事玩法结束");
        listResponse.setData(outrightMatchInfoBOS);
        return listResponse;
    }
}
