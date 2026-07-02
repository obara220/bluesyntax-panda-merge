package com.panda.merge.mapper;

import com.panda.merge.model.MatchTimeInfo;
import com.panda.merge.model.MatchTimeInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchTimeInfoMapper {
    long countByExample(MatchTimeInfoExample example);

    int deleteByExample(MatchTimeInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchTimeInfo record);

    int insertSelective(MatchTimeInfo record);

    List<MatchTimeInfo> selectByExample(MatchTimeInfoExample example);

    MatchTimeInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchTimeInfo record, @Param("example") MatchTimeInfoExample example);

    int updateByExample(@Param("record") MatchTimeInfo record, @Param("example") MatchTimeInfoExample example);

    int updateByPrimaryKeySelective(MatchTimeInfo record);

    int updateByPrimaryKey(MatchTimeInfo record);

    /**
     * 按主键更新伤补时间
     *
     * @param id             主键
     * @param timeOutList 伤补时间
     * @return 更新数量
     */
    int updateInjuryTimeByPrimaryKey(@Param("timeOutList") String timeOutList, @Param("id") Long id);

    void batchUpdateByPrimaryKey(@Param("list")List<MatchTimeInfo> saveList);
}