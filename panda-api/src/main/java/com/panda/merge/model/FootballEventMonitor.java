package com.panda.merge.model;

import com.panda.merge.dto.MatchEventInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 足球报球板事件监控实体类
 *
 * @author warren
 * @since 2024/11/03 13:59:17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FootballEventMonitor implements Serializable {
    /**
     * 当前登录人
     */
    private String userName;

    /**
     * 操作赛事在线状态相关信息
     */
    private List<MatchEventInfoDTO> thirdMatchInfo;
}
