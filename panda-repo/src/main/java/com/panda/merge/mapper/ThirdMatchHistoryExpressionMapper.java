package com.panda.merge.mapper;

import com.panda.merge.model.ThirdMatchHistoryExpression;
import com.panda.merge.model.ThirdMatchHistoryExpressionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdMatchHistoryExpressionMapper {
    long countByExample(ThirdMatchHistoryExpressionExample example);

    int deleteByExample(ThirdMatchHistoryExpressionExample example);

    int deleteByPrimaryKey(String id);

    int insert(ThirdMatchHistoryExpression record);

    int insertSelective(ThirdMatchHistoryExpression record);

    List<ThirdMatchHistoryExpression> selectByExample(ThirdMatchHistoryExpressionExample example);

    ThirdMatchHistoryExpression selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ThirdMatchHistoryExpression record, @Param("example") ThirdMatchHistoryExpressionExample example);

    int updateByExample(@Param("record") ThirdMatchHistoryExpression record, @Param("example") ThirdMatchHistoryExpressionExample example);

    int updateByPrimaryKeySelective(ThirdMatchHistoryExpression record);

    int updateByPrimaryKey(ThirdMatchHistoryExpression record);
}