package com.panda.merge.mapper;

import com.panda.merge.model.MatchScoresCenterLog;
import com.panda.merge.model.MatchScoresCenterLogExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author warren
 * @since 2024/02/20 15:37:54
 */
@Repository
public interface MatchScoresCenterLogMapper {
    int insert(MatchScoresCenterLog record);

    int insertSelective(MatchScoresCenterLog record);
    List<MatchScoresCenterLog> selectByMatchManageId(String matchManageId);

    int deleteByExample(MatchScoresCenterLogExample matchScoresCenterLogExample);

    void batchInsert(@Param("list") List<MatchScoresCenterLog> records);
}
