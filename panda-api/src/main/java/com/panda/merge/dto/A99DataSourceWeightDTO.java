package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class A99DataSourceWeightDTO implements Serializable {
    /**
     * {
     *   "matchId": 123456,
     *   "templateId": 9958,
     *   "matchType": 0,
     *   "sportId": 1,
     *   "a99Configs": [
     *     {
     *       "10001": [
     *         {"name": "AO", "status": 1, "value": 70},
     *         {"name": "LS-1XBet", "status": 0, "value": 0}
     *       ],
     *       "cautionValue": 3,
     *       "oddsChangeThreshold": 0.04,
     *       "waterLimitUpper": 50,
     *       "closeMarketReject": 0,
     *       "a99Switch": 1
     *     },
     *     {
     *       "10002": [
     *         {"name": "AO", "status": 1, "value": 80}
     *       ],
     *       "cautionValue": 5,
     *       "oddsChangeThreshold": 0.05,
     *       "waterLimitUpper": 60,
     *       "closeMarketReject": 0,
     *       "a99Switch": 1
     *     }
     *   ],
     *   "linkId": "linkId值",
     *   "updateTime": 1700000000000,
     *   "operatorId": 1001
     * }
     */

    private String linkId;

    private Long updateTime;

    private Long operatorId;

    private Long matchId;

    private Long templateId;

    private int matchType;

    private Long sportId;

    private String a99ConfigValue;

}
