package com.panda.merge.dto.message;


import lombok.Data;

import java.text.SimpleDateFormat;

/**
 * PreSoldFirstOddsMessage
 *
 * @description: 预售首次三方盘口告警信息
 * @date: 1/25/2025
 **/
@Data
public class PreSoldFirstOddsMessage {

    private String matchManageId;

    private Long matchInfoId;

    private String sourceCode;

    private String tournamentName;

    private String tournamentNameEn;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("League（联赛）：%s (%s)\n", tournamentNameEn,tournamentName));
        sb.append(String.format("Match ID (赛事id): %s\n", matchManageId));
        sb.append(String.format("Odds provider (赔率源): %s\n",sourceCode));
        sb.append(String.format("Time 发生时间：%s\n",
                                new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date())));
        return sb.toString();
    }
}
