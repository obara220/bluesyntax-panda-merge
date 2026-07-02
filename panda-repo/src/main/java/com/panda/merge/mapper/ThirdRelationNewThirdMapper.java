package com.panda.merge.mapper;

import com.panda.merge.model.ThirdRelationNewThird;
import com.panda.merge.model.ThirdRelationNewThirdExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ThirdRelationNewThirdMapper {
    long countByExample(ThirdRelationNewThirdExample example);

    int deleteByExample(ThirdRelationNewThirdExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdRelationNewThird record);

    int insertSelective(ThirdRelationNewThird record);

    List<ThirdRelationNewThird> selectByExample(ThirdRelationNewThirdExample example);

    ThirdRelationNewThird selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdRelationNewThird record, @Param("example") ThirdRelationNewThirdExample example);

    int updateByExample(@Param("record") ThirdRelationNewThird record, @Param("example") ThirdRelationNewThirdExample example);

    int updateByPrimaryKeySelective(ThirdRelationNewThird record);

    int updateByPrimaryKey(ThirdRelationNewThird record);
}