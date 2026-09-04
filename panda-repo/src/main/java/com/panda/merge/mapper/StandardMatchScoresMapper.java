package com.panda.merge.mapper;

import com.panda.merge.model.StandardMatchScores;
import com.panda.merge.model.StandardMatchScoresExample;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @description 标准比分表
 * @author Fymen
 * @date 2024-01-29
 */
@Repository
public interface StandardMatchScoresMapper {

    /**
     * 新增
     * @author Fymen
     * @date 2024/01/29
     **/
    int insert(StandardMatchScores standardMatchScores);

    /**
     * 更新
     * @author Fymen
     * @date 2024/01/29
     **/
    int update(StandardMatchScores standardMatchScores);

    /**
     * 查询标准比分
     * @param matchId
     * @return
     */
    StandardMatchScores loadByMatchId(Long matchId);

//    /**
//     * 查询标准比分
//     * @param thirdMatchId
//     * @return
//     */
//    StandardMatchScores queryStandardScoreByThirdMatchId(Long thirdMatchId);

    /**
     * 查询 分页查询
     *
     * @author Fymen
     * @date 2024/01/29
     **/
    List<StandardMatchScores> pageList(int offset, int pagesize);

    /**
     * 查询 分页查询 count
     * @author Fymen
     * @date 2024/01/29
     **/
    int pageListCount(int offset,int pagesize);


    int deleteByExample(StandardMatchScoresExample example);

    void batchUpdateByPrimaryKey(List<StandardMatchScores> list);

    List<StandardMatchScores> queryScoresByMatchIds(List<Long> matchIds);
}