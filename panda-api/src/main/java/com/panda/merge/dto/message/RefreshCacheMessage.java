package com.panda.merge.dto.message;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * 赛程项目修改三方赛事或者标准赛事信息后，通知本项目刷新缓存
 * @author :  tell
 * @since 2020年12月1日17:08:02
 */
@Data
public class RefreshCacheMessage {

    /**
     * 标注赛事id
     */
    private Long standardMatchId;
    /**
     * 第三方比赛id列表
     */
    @NotNull(message = "第三方比赛id列表不能为null!")
    private List<Long> thirdMatchIds;

    /**
     * 操作类型
     */
    private String operateType;
}
