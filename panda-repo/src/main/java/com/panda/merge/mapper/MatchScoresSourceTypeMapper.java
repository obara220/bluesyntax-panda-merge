package com.panda.merge.mapper;

import com.panda.merge.model.MatchScoresSourceType;
import com.panda.merge.model.MatchScoresSourceTypeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchScoresSourceTypeMapper {
    long countByExample(MatchScoresSourceTypeExample example);

    int deleteByExample(MatchScoresSourceTypeExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchScoresSourceType record);

    int insertSelective(MatchScoresSourceType record);

    List<MatchScoresSourceType> selectByExample(MatchScoresSourceTypeExample example);

    /**
     * 查询资源类型 1.livedata  2.uof
     * @param id
     * @return
     */
    MatchScoresSourceType selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchScoresSourceType record, @Param("example") MatchScoresSourceTypeExample example);

    int updateByExample(@Param("record") MatchScoresSourceType record, @Param("example") MatchScoresSourceTypeExample example);

    int updateByPrimaryKeySelective(MatchScoresSourceType record);

    int updateByPrimaryKey(MatchScoresSourceType record);

    void batchUpdateByPrimaryKey(@Param("list")List<MatchScoresSourceType> list);
}