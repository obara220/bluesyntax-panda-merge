package com.panda.merge.mapper;

import com.panda.merge.model.ThirdMatchExInfomation;
import com.panda.merge.model.ThirdMatchExInfomationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdMatchExInfomationMapper {
    long countByExample(ThirdMatchExInfomationExample example);

    int deleteByExample(ThirdMatchExInfomationExample example);

    int deleteByPrimaryKey(String id);

    int insert(ThirdMatchExInfomation record);

    int insertSelective(ThirdMatchExInfomation record);

    List<ThirdMatchExInfomation> selectByExampleWithBLOBs(ThirdMatchExInfomationExample example);

    List<ThirdMatchExInfomation> selectByExample(ThirdMatchExInfomationExample example);

    ThirdMatchExInfomation selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ThirdMatchExInfomation record, @Param("example") ThirdMatchExInfomationExample example);

    int updateByExampleWithBLOBs(@Param("record") ThirdMatchExInfomation record, @Param("example") ThirdMatchExInfomationExample example);

    int updateByExample(@Param("record") ThirdMatchExInfomation record, @Param("example") ThirdMatchExInfomationExample example);

    int updateByPrimaryKeySelective(ThirdMatchExInfomation record);

    int updateByPrimaryKeyWithBLOBs(ThirdMatchExInfomation record);

    int updateByPrimaryKey(ThirdMatchExInfomation record);
}