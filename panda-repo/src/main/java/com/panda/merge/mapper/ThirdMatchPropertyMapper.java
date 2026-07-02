package com.panda.merge.mapper;

import com.panda.merge.model.ThirdMatchProperty;
import com.panda.merge.model.ThirdMatchPropertyExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdMatchPropertyMapper {
    long countByExample(ThirdMatchPropertyExample example);

    int deleteByExample(ThirdMatchPropertyExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdMatchProperty record);

    int insertSelective(ThirdMatchProperty record);

    List<ThirdMatchProperty> selectByExample(ThirdMatchPropertyExample example);

    ThirdMatchProperty selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdMatchProperty record, @Param("example") ThirdMatchPropertyExample example);

    int updateByExample(@Param("record") ThirdMatchProperty record, @Param("example") ThirdMatchPropertyExample example);

    int updateByPrimaryKeySelective(ThirdMatchProperty record);

    int updateByPrimaryKey(ThirdMatchProperty record);
}