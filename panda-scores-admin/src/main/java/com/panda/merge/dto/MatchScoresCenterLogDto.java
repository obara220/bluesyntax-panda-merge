package com.panda.merge.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author warren
 * @since 2024/02/25 15:44:08
 */
@Data
public class MatchScoresCenterLogDto {
    private int id;

    /**
     * 操作類別編碼
     */
    private Integer operatePageCode;

    /**
     * 操作類別
     */
    private String operatePageName;

    /**
     * 操作對象ID
     */
    private String objectId;

    /**
     * 操作對象名稱
     */
    private String objectName;

    /**
     * 操作對象擴展ID
     */
    private String extObjectId;

    /**
     * 操作對象擴展名稱
     */
    private String extObjectName;

    /**
     * 操作行為
     */
    private String behavior;

    /**
     * 操作參數
     */
    private String parameterName;

    /**
     * 修改前
     */
    private String beforeVal;

    /**
     * 修改後
     */
    private String afterVal;

    /**
     * 操作人Id
     */
    private String userId;

    /**
     * 操作人
     */
    private String userName;

    /**
     * 操作時間
     */
    private Date operateTime;

    /**
     * 操作時間字串
     */
    private String operateTimeStr;

    /**
     * 球种名称
     */
    private String sportName;

    /**
     * 賽事Id
     */
    private Long matchId;

    /**
     * 球种ID
     */
    private Integer sportId;

    /**
     * 玩法Id
     */
    private Long playId;
    /**
     * 操作人ip
     */
    private String ip;
}
