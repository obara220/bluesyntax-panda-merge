package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * modify_match_business topic 消息体
 * 联赛/赛事编辑管理下发，用于更新标准赛事下数据源列表缓存
 */
@Data
public class ModifyMatchBusinessMessageDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String linkId;

    /**
     * 赛事列表，一般只有一条
     */
    private List<ModifyMatchBusinessDataItem> data;

    @Data
    public static class ModifyMatchBusinessDataItem implements Serializable {
        private static final long serialVersionUID = 1L;
        /** 标准赛事ID */
        private Long id;
        /** 三方赛事信息列表，用于提取 dataSourceCode */
        private List<ThirdMatchInfoListEntry> thirdMatchInfoList;
    }

    @Data
    public static class ThirdMatchInfoListEntry implements Serializable {
        private static final long serialVersionUID = 1L;
        private String dataSourceCode;
    }
}
