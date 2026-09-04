package com.panda.merge.rocketmq.processor;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.DataSourceEncrypEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.config.A99ParamConfig;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.dto.ThirdMatchMarketDTO;
import com.panda.merge.exception.ExceptionHelper;
import com.panda.merge.model.ThirdMarketCategory;
import com.panda.merge.service.ThirdSportMarketCategoryService;
import com.panda.merge.service.ThirdSportTypeService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Validated
@Component
@Data
public class A99ThirdMatchMarketProcessor extends BaseProcessor {
    @Resource
    private A99ThirdAllBatchMarketProcessor thirdAllBatchMarketProcessor;
    @Resource
    private ThirdSportTypeService thirdSportTypeService;
    @Resource
    private ThirdSportMarketCategoryService thirdSportMarketCategoryService;

   // @Value("${calculate.standard.category.classify}")
   // private String standardCategory;
     @Autowired
     private A99ParamConfig a99ParamConfig;
    @ExceptionHelper
    public void execute(@Valid List<Request<ThirdMatchMarketDTO>> requests) {
        if (requests == null || requests.isEmpty()) {
            log.info("百家赔批量拉取开始:ThirdMatchMarketProcessor 请求列表为空或不存在");
            return;
        }
        log.info("百家赔批量拉取开始1:ThirdMatchMarketProcessor standardCategory:{},thirdMarketCategorySourceIds:{},categoryMap:{},standardMarketIds:{}"
                ,a99ParamConfig.getStandardCategory(),a99ParamConfig.getThirdMarketCategorySourceIds(),a99ParamConfig.getCategoryMap(),a99ParamConfig.getStandardMarketIds());
        List<Request<ThirdMatchMarketDTO>> lists = new ArrayList<>();
        for (Request<ThirdMatchMarketDTO> e : requests) {
            if (e.getData() != null  && e.getData().getDataSourceCode() != null && e.getData().getSportId() != null) {
                //校验是否是足球
                String thirdSportId = thirdSportTypeService.getThirdSportId(StandardSportTypeEnum.FootBall.code, e.getData().getDataSourceCode());
                if (!thirdSportId.equals(e.getData().getSportId().toString())) {
                    break;
                }
                if (e.getData().getDataSourceCode().equals(DataSourceCodeEnum.AO.code) ||
                        e.getData().getDataSourceCode().equals(DataSourceCodeEnum.OD.code)) {
                    //过滤A01赔率
                    break;
                }
                //校验是否是4480需求中的24个玩法
                List<ThirdMarketDTO> marketList = new ArrayList<>();
                for (ThirdMarketDTO thirdMatchMarketDTO : e.getData().getMarketList()) {
                    if (thirdMatchMarketDTO.getStatus() != 0) {
                        //只缓存开盘的盘口
                        continue;
                    }
                    log.info("百家赔批量拉取开始2:a99ParamConfig.getThirdMarketCategorySourceIds():{},getThirdMarketCategorySourceId:{}",a99ParamConfig.getThirdMarketCategorySourceIds(),thirdMatchMarketDTO.getThirdMarketCategorySourceId());
                    if (a99ParamConfig.getThirdMarketCategorySourceIds().contains(thirdMatchMarketDTO.getThirdMarketCategorySourceId())) {
                        marketList.add(thirdMatchMarketDTO);
                        log.info("{}::三方玩法添加缓存thirdMatchMarketDTO:{}", e.getLinkId(),thirdMatchMarketDTO.getThirdMarketCategorySourceId());
                    }
                }
                if (ObjectUtil.isNotEmpty(marketList)) {
                    log.info("{}::三方玩法校验通过,数量:{}", e.getLinkId(), marketList.size());
                    e.getData().setMarketList(marketList);
                    lists.add(e);
                } else {
                    log.info("{}::三方玩法校验不通过", e.getLinkId());
                }
            }
        }
       log.info("百家赔批量拉取开始3:ThirdMatchMarketProcessor,请求列表: {},是否为空: {}",  lists.size(),CollectionUtils.isNotEmpty(lists));
        if (CollectionUtils.isNotEmpty(lists)) {
            String linkIds = requests.stream().map(Request::getLinkId).collect(Collectors.joining("-"));
            log.info("::{}:: 百家赔批量拉取开始:ThirdMatchMarketProcessor 三方玩法: {}", linkIds, lists);
            thirdAllBatchMarketProcessor.execute(lists);
        } else {
            log.info("百家赔批量拉取开始:ThirdMatchMarketProcessor,没有符合条件的请求");
        }
    }


}
