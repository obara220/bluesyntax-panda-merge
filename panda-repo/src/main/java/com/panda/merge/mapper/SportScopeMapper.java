package com.panda.merge.mapper;

import com.panda.merge.model.SportScope;
import com.panda.merge.model.SportScopeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SportScopeMapper {
    long countByExample(SportScopeExample example);

    int deleteByExample(SportScopeExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SportScope record);

    int insertSelective(SportScope record);

    List<SportScope> selectByExample(SportScopeExample example);

    SportScope selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") SportScope record, @Param("example") SportScopeExample example);

    int updateByExample(@Param("record") SportScope record, @Param("example") SportScopeExample example);

    int updateByPrimaryKeySelective(SportScope record);

    int updateByPrimaryKey(SportScope record);
}