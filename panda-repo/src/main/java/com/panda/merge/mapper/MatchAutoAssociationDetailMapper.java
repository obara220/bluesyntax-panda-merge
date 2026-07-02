package com.panda.merge.mapper;

import com.panda.merge.model.MatchAutoAssociationDetail;
import com.panda.merge.model.MatchAutoAssociationDetailExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchAutoAssociationDetailMapper {
    long countByExample(MatchAutoAssociationDetailExample example);

    int deleteByExample(MatchAutoAssociationDetailExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchAutoAssociationDetail record);

    int insertSelective(MatchAutoAssociationDetail record);

    List<MatchAutoAssociationDetail> selectByExampleWithBLOBs(MatchAutoAssociationDetailExample example);

    List<MatchAutoAssociationDetail> selectByExample(MatchAutoAssociationDetailExample example);

    MatchAutoAssociationDetail selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchAutoAssociationDetail record, @Param("example") MatchAutoAssociationDetailExample example);

    int updateByExampleWithBLOBs(@Param("record") MatchAutoAssociationDetail record, @Param("example") MatchAutoAssociationDetailExample example);

    int updateByExample(@Param("record") MatchAutoAssociationDetail record, @Param("example") MatchAutoAssociationDetailExample example);

    int updateByPrimaryKeySelective(MatchAutoAssociationDetail record);

    int updateByPrimaryKeyWithBLOBs(MatchAutoAssociationDetail record);

    int updateByPrimaryKey(MatchAutoAssociationDetail record);
}