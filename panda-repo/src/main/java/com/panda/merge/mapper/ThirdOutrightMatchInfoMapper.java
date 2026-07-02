package com.panda.merge.mapper;

import com.panda.merge.model.ThirdOutrightMatchInfo;
import com.panda.merge.model.ThirdOutrightMatchInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdOutrightMatchInfoMapper {
    long countByExample(ThirdOutrightMatchInfoExample example);

    int deleteByExample(ThirdOutrightMatchInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdOutrightMatchInfo record);

    int insertSelective(ThirdOutrightMatchInfo record);

    List<ThirdOutrightMatchInfo> selectByExample(ThirdOutrightMatchInfoExample example);

    ThirdOutrightMatchInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdOutrightMatchInfo record, @Param("example") ThirdOutrightMatchInfoExample example);

    int updateByExample(@Param("record") ThirdOutrightMatchInfo record, @Param("example") ThirdOutrightMatchInfoExample example);

    int updateByPrimaryKeySelective(ThirdOutrightMatchInfo record);

    int updateByPrimaryKey(ThirdOutrightMatchInfo record);
}