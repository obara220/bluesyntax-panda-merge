package com.panda.merge.service;

import com.github.pagehelper.Page;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.nonrealttime.query.QueryThirdRankingInfoDTO;
import com.panda.merge.model.ThirdSportTournament;

import java.util.List;

/**
 * <Description> 三方联赛信息
 * @author      tell
 * @since       2020年9月3日15:24:52
 */
public interface ThirdSportTournamentService {

    /**
     * 分页查询含标准联赛的三方联赛
     * */
    Page<ThirdSportTournament> getItemPageByModifyTime(PageModel<QueryThirdRankingInfoDTO> page);

    /**
     * 根据 数据源+标准运动类型+三方数据源联赛ID 获取三方联赛信息
     * @param dataSourceCode   数据来源
     * @param sportId          运动类型
     * @param thirdSourceId          三方数据源联赛ID
     * @return ThirdSportTournament
     * */
    ThirdSportTournament getOneItem(String dataSourceCode, Long sportId, String thirdSourceId);

    /**
     * 根据标准联赛ID列表查询三方联赛信息列表
     * @param dataSourceCode   数据来源
     * @param sportId          运动类型
     * @param referenceIds    标准联赛ID
     * @return ThirdSportTournament
     * */
    List<ThirdSportTournament> getItems(String dataSourceCode, Long sportId, List<Long> referenceIds);

    /**
     * 根据数据源联赛ID列表查询三方联赛信息列表
     * @param dataSourceCodes   数据来源列表
     * @param thirdTournamentSourceIds    数据源联赛ID
     * @return ThirdSportTournament
     * */
    List<ThirdSportTournament> getItems(List<String> dataSourceCodes,List<String> thirdTournamentSourceIds);

    /**
     * 新增或修改
     * @param item  对象信息
     * @return ThirdSportTournament
     * */
    ThirdSportTournament saveOrupdate(ThirdSportTournament item);

    ThirdSportTournament getThirdSportTournament(Long id);
}
