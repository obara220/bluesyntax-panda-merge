package com.panda.merge.service;

import com.github.pagehelper.Page;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.StandardSportTournamentDTO;
import com.panda.merge.dto.StandardSportTournamentDetail;
import com.panda.merge.model.StandardSportTournament;

import java.util.List;

/**
 * 标准联赛信息 <br>
 * @author   tell
 * @since    2020年9月10日10:32:26
 */
public interface StandardSportTournamentService {

        /**
         * 根据修改时间筛选，分页查询标准联赛信息
         * @param  page  分页对象信息
         * @return Page<StandardSportTournamentament>
         * */
        Page<StandardSportTournamentDetail> getItemPageByModifyTime(PageModel<StandardSportTournamentDTO> page);


        /**
         * 根据标准联赛ID列表查询标准联赛信息列表
         * @param ids   标准联赛ID列表
         * @return List<StandardSportTournament>
         * */
        List<StandardSportTournament> getItems(List<Long> ids);

        /**
         * 获取联赛列表 使用缓存
         *
         * @param ids 联赛id列表
         * @return 联赛列表
         */
        List<StandardSportTournament> getItemsCache(List<Long> ids);

        /**
         * 根据标准联赛ID查询标准联赛信息
         * @param id
         * @return
         */
        StandardSportTournament getItem(Long id);

        void evitCache(List<Long> ids);
}
