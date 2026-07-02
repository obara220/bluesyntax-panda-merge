package com.panda.merge.dto.settle;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.model.MatchSettleTemplate;
import lombok.Data;

import java.util.List;

@Data
public class DataSourceWeightResponse extends AbstructMatchSettleDto{
   private Long  templateId;
   private Long tournamentId;
   private String templateName;
   private JSONArray templateJson;
   private List<MatchSettleTemplate> templateList;
   private MatchSettleTemplate tournamentTemplate;
   private List<Long> tournamentIdList;

}
