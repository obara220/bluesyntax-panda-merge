package com.panda.merge.mapper;

import com.panda.merge.model.ThirdMatchSidelined;
import com.panda.merge.model.ThirdMatchSidelinedExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdMatchSidelinedMapper {
    long countByExample(ThirdMatchSidelinedExample example);

    int deleteByExample(ThirdMatchSidelinedExample example);

    int deleteByPrimaryKey(String id);

    int insert(ThirdMatchSidelined record);

    int insertSelective(ThirdMatchSidelined record);

    List<ThirdMatchSidelined> selectByExample(ThirdMatchSidelinedExample example);

    ThirdMatchSidelined selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ThirdMatchSidelined record, @Param("example") ThirdMatchSidelinedExample example);

    int updateByExample(@Param("record") ThirdMatchSidelined record, @Param("example") ThirdMatchSidelinedExample example);

    int updateByPrimaryKeySelective(ThirdMatchSidelined record);

    int updateByPrimaryKey(ThirdMatchSidelined record);
}