package com.panda.merge.api;

import com.panda.merge.bo.*;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.message.StandardMatchEventResultMessage;
import com.panda.merge.dto.*;
import com.panda.merge.dto.nonrealttime.query.*;

import java.util.List;

/**
 * 查询接口
 * @author  tell
 * @since   2020年9月10日13:40:13
 * */
public interface IStandardDataQueryApi {
    /**
     * 查询标准体育类型列表(含多语言信息)
     * @param sportTypeDto
     * @return
     */
    Response<List<StandardSportTypeBO>> queryStandardSportTypePage(Request<StandardSportTypeDTO> sportTypeDto);

    /**
     * 分页查询标准联赛列表(含多语言信息)
     * @param request
     * @return
     */
    Response<PageModel<List<StandardSportTournamentBO>>> querySportTournamentPage(Request<PageModel<StandardSportTournamentDTO>> request);

    /**
     * 分页查询标准赛程（球队）列表(含多语言信息)（根据修改时间同步）
     * @param request
     * @return
     */
    Response<PageModel<List<StandardMatchInfoBO>>> querySportMathTeamPage(Request<PageModel<StandardMatchInfoDTO>> request);

    /**
     * 分页查询体育区域列表
     * @param request
     * @return
     */
    Response<PageModel<List<StandardSportRegionBO>>> queryStandardSportRegionPage(Request<PageModel<StandardSportRegionDTO>> request);

    /**
     * 分页查询标准玩法玩，法投注项列表
     * @param request
     * @return
     */
    Response<PageModel<List<StandardMarketCategoryBO>>> queryStandardSportMarketCategoryPage(Request<PageModel<StandardSportMarketCategoryDTO>> request);

    /**
     * 分页查询三方盘口列表，盘口分表后已经不支持分页查询了
     * @param request
     * @return
     */
    @Deprecated
    Response<PageModel<List<ThirdSportMarketBO>>> queryThirdSportMarketPage(Request<PageModel<ThirdSportMarketDTO>> request);

    /**
     * 分页查询第三方盘口列表(统计使用)，盘口分表后已经不支持分页查询了
     * @param request
     * @return
     */
    @Deprecated
    Response<PageModel<List<ThirdSportMarketBO>>> queryThirdSportMarketPageForReport(Request<PageModel<ThirdSportMarketDTO>> request);

    /**
     * 分页查询三方赛事列表
     * @param request
     * @return
     */
    Response<PageModel<List<ThirdMatchInfoBO>>> queryThirdMatchInfoPage(Request<PageModel<ThirdMatchInfoDTO>> request);

    /**
     * 查询数据来源列表
     * @param request
     * @return
     */
    Response<List<DataSourceBO>> queryDataSourcePage(Request<DataSourceDTO> request);

    /**
     * @Description：查询玩法模板关系
     * @CreationDate：2021年3月7日上午11:51:20
     * @return
     *
     */
    Response<List<MarketCategoryTemplateRelationBO>> queryMarketCategoryTemplateRelation(Request<Integer> request);
    /**
     * @Description：查询标准赛果阶段比分
     * @CreationDate：2021年3月7日上午11:51:20
     * @return
     *
     */
    Response<StandardMatchEventResultMessage> queryStandardMatchResult(Request<StandardMatchResultDTO> smrDto);

}

