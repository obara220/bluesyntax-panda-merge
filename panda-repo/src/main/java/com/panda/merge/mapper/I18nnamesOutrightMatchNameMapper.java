package com.panda.merge.mapper;

import com.panda.merge.model.I18nnamesOutrightMatchName;
import com.panda.merge.model.I18nnamesOutrightMatchNameExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface I18nnamesOutrightMatchNameMapper {
    long countByExample(I18nnamesOutrightMatchNameExample example);

    int deleteByExample(I18nnamesOutrightMatchNameExample example);

    int deleteByPrimaryKey(Long id);

    int insert(I18nnamesOutrightMatchName record);

    int insertSelective(I18nnamesOutrightMatchName record);

    List<I18nnamesOutrightMatchName> selectByExample(I18nnamesOutrightMatchNameExample example);

    I18nnamesOutrightMatchName selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") I18nnamesOutrightMatchName record, @Param("example") I18nnamesOutrightMatchNameExample example);

    int updateByExample(@Param("record") I18nnamesOutrightMatchName record, @Param("example") I18nnamesOutrightMatchNameExample example);

    int updateByPrimaryKeySelective(I18nnamesOutrightMatchName record);

    int updateByPrimaryKey(I18nnamesOutrightMatchName record);
}