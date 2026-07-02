package com.panda.merge.mapper;

import com.panda.merge.model.MatchSettleAbnormal;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMatchSettleAbnormalMapper extends MatchSettleAbnormalMapper{

   void insertByList(List<MatchSettleAbnormal> list);
}