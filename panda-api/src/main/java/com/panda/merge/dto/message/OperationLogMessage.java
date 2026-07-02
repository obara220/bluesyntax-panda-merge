package com.panda.merge.dto.message;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: z9-una
 * @Date: 2023/10/03 6:54 PM
 * @Description: 操作日志
 */
@Data
public class OperationLogMessage implements Serializable {

    private static final long serialVersionUID = 3731891682494684490L;

    /**
     * 操作类别编码
     */
    private Integer operatePageCode;

    /**
     * 操作类别
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
     * 操作行为
     */
    private String behavior;

    /**
     * 操作參數
     */
    private String parameterName;

    /**
     * 修改前
     */
    @NotNull
    private String beforeVal;

    /**
     * 修改后
     */
    @NotNull
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
