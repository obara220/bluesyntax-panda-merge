package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description  :  操盘配置-联赛配置margin分时DTO
 * @author       :  Vito
 * @Date:  2019年11月6日 下午2:22:06
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class CategoryMarginDtlDTO implements Serializable {
    private static final long serialVersionUID = -8354357114094981498L;

    /**
     * 分时，单位秒
     */
    private Long timeFrame;

    /**
     * 分时对应的margin值
     */
    private Double margin;

}
