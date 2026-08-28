package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * SR BC提供的比赛数据信息表
 * </p>
 *
 * @author titan
 * @since 2026-08-25
 */
@Data
public class ThirdMatchDataSourceDTO implements Serializable {
    private static final long serialVersionUID = 1L;


    @NotNull(message = "第三方赛事原始id")
    private String thirdMatchSourceId;


    @NotNull(message = "球种id")
    private Long sportId;

    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 数据源编码
     */
    private String dataSourceCode;

    /**
     * 赛事来源 1.现场2.TV3.其他
     */
    private int liveEventSource;
}
