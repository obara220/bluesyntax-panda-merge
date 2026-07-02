package com.panda.merge.mapper;

import com.panda.merge.model.StandardOutrightMatchInfo;
import com.panda.merge.model.StandardOutrightMatchInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardOutrightMatchInfoMapper {
    long countByExample(StandardOutrightMatchInfoExample example);

    int deleteByExample(StandardOutrightMatchInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardOutrightMatchInfo record);

    int insertSelective(StandardOutrightMatchInfo record);

    List<StandardOutrightMatchInfo> selectByExample(StandardOutrightMatchInfoExample example);

    StandardOutrightMatchInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardOutrightMatchInfo record, @Param("example") StandardOutrightMatchInfoExample example);

    int updateByExample(@Param("record") StandardOutrightMatchInfo record, @Param("example") StandardOutrightMatchInfoExample example);

    int updateByPrimaryKeySelective(StandardOutrightMatchInfo record);

    int updateByPrimaryKey(StandardOutrightMatchInfo record);
}