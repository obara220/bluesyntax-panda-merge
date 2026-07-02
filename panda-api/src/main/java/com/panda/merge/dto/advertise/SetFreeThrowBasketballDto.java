package com.panda.merge.dto.advertise;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.panda.merge.dto.settle.AbstructMatchSettleDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author warren
 * @since 2024/03/30 12:45:34
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetFreeThrowBasketballDto extends AbstructMatchSettleDto {
    /**
     * 第几个球
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private long id;
    /**
     * 进球状态 -1 未投  0 未进  1进了
     */
    private int status;
    /**
     * 删除状态
     */
    private boolean delete;
}
