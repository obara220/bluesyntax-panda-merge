package com.panda.merge.rocketmq.processor;

import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchMarketDTO;
import com.panda.merge.exception.ExceptionHelper;
import com.panda.merge.service.ThirdSportTypeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Validated
@Component
public class ThirdMatchMarketProcessor  extends BaseProcessor {
    @Resource
    private ThirdAllBatchMarketProcessor thirdAllBatchMarketProcessor;
    @Resource
    private ThirdSportTypeService thirdSportTypeService;

    public List<Long> B01_Code_list = new ArrayList<>();

    public List<Long> N01_Code_list = new ArrayList<>();

    public List<Long> N02_Code_list = new ArrayList<>();

    public List<Long> N03_Code_list = new ArrayList<>();

    public List<Long> S01_Code_list = new ArrayList<>();

    //public List<Long> L02_Code_list = new ArrayList<>();
    @ExceptionHelper
    public void execute(@Valid List<Request<ThirdMatchMarketDTO>> requests) {
        if (requests == null || requests.isEmpty()) {
            log.info("百家赔批量拉取开始:ThirdMatchMarketProcessor 请求列表为空或不存在");
            return;
        }

        List<Request<ThirdMatchMarketDTO>> lists = new ArrayList<>();
        for (Request<ThirdMatchMarketDTO> e : requests) {
            if (e.getData() != null
                    && e.getData().getDataSourceCode() != null
                    && ((e.getData().getDataSourceCode().equalsIgnoreCase(DataSourceCodeEnum.BG.code) && B01_Code_list.contains(e.getData().getSportId()))
                    || (e.getData().getDataSourceCode().equalsIgnoreCase(DataSourceCodeEnum.N01.code) && N01_Code_list.contains(e.getData().getSportId()))
                    || (e.getData().getDataSourceCode().equalsIgnoreCase(DataSourceCodeEnum.N02.code) && N02_Code_list.contains(e.getData().getSportId()))
                    || (e.getData().getDataSourceCode().equalsIgnoreCase(DataSourceCodeEnum.SR.code) && S01_Code_list.contains(e.getData().getSportId()))
                    || (e.getData().getDataSourceCode().equalsIgnoreCase(DataSourceCodeEnum.N03.code) && N03_Code_list.contains(e.getData().getSportId()))
            )
            ) {
                lists.add(e);
            }
        }

        if (CollectionUtils.isNotEmpty(lists)) {
            String linkIds = requests.stream().map(Request::getLinkId).collect(Collectors.joining("-"));
            log.info("::{}:: 百家赔批量拉取开始:ThirdMatchMarketProcessor 请求size: {}", linkIds, requests.size());
            thirdAllBatchMarketProcessor.execute(lists);
        } else {
            log.info("百家赔批量拉取开始:ThirdMatchMarketProcessor,没有符合条件的请求");
        }
    }
    @PostConstruct
    public void init()
    {
        B01_Code_list.add(Long.valueOf(thirdSportTypeService.getThirdSportId(StandardSportTypeEnum.FootBall.code,DataSourceCodeEnum.BG.code)));
        B01_Code_list.add(Long.valueOf(thirdSportTypeService.getThirdSportId(StandardSportTypeEnum.Basketball.code,DataSourceCodeEnum.BG.code)));
        S01_Code_list.add(Long.valueOf(thirdSportTypeService.getThirdSportId(StandardSportTypeEnum.FootBall.code,DataSourceCodeEnum.SR.code)));
        S01_Code_list.add(Long.valueOf(thirdSportTypeService.getThirdSportId(StandardSportTypeEnum.Basketball.code,DataSourceCodeEnum.SR.code)));

        N01_Code_list.add(StandardSportTypeEnum.FootBall.getCode());
        N01_Code_list.add(StandardSportTypeEnum.Basketball.getCode());
        N02_Code_list.add(StandardSportTypeEnum.FootBall.getCode());
        N02_Code_list.add(StandardSportTypeEnum.Basketball.getCode());

        N03_Code_list.add(StandardSportTypeEnum.FootBall.getCode());
        N03_Code_list.add(StandardSportTypeEnum.Basketball.getCode());
    }

}
