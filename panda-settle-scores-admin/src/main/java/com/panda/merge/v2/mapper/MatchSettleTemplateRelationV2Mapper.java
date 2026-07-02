package com.panda.merge.v2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.merge.model.MatchSettleTemplateRelation;
import com.panda.merge.model.MatchSettleTemplateRelationExample;
import com.panda.merge.v2.entity.MatchSettleTemplateRelationEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MatchSettleTemplateRelationV2Mapper extends BaseMapper<MatchSettleTemplateRelationEntity> {

    long countByExample(MatchSettleTemplateRelationExample example);

    int deleteByExample(MatchSettleTemplateRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleTemplateRelationEntity record);

    int insertSelective(MatchSettleTemplateRelationEntity record);

    List<MatchSettleTemplateRelationEntity> selectByExample(MatchSettleTemplateRelationExample example);

    int updateByExampleSelective(@Param("record") MatchSettleTemplateRelationEntity record, @Param("example") MatchSettleTemplateRelationExample example);

    int updateByExample(@Param("record") MatchSettleTemplateRelationEntity record, @Param("example") MatchSettleTemplateRelationExample example);

    int updateByPrimaryKeySelective(MatchSettleTemplateRelationEntity record);

    int updateByPrimaryKey(MatchSettleTemplateRelationEntity record);

    /**
     * 批量更新到联赛等级模版 则清空配置
     * */
    void updateBatchRelationWeightIdToLevel(@Param("tournamentLevel") Integer tournamentLevel);

    /**
     * 批量更新到专用模版则绑定id
     * */
    void updateBatchRelationWeightId(@Param("tournamentLevel")Integer tournamentLevel,@Param("templateId") Long templateId);
    /**
     * 批量更新到联赛等级模版 则清空配置
     * */
    void updateBatchRelationGrayIdToLevel(@Param("tournamentLevel")Integer tournamentLevel);
    /**
     * 批量更新到专用模版则绑定id
     * */
    void updateBatchRelationGrayId(@Param("tournamentLevel")Integer tournamentLevel,@Param("templateId") Long templateId);

}
