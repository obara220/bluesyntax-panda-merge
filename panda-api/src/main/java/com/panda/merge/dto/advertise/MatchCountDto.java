package com.panda.merge.dto.advertise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author warren
 * @since 2023/12/08 11:58:57
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchCountDto extends AbstructAdvertiseDto {
    // 三方赛事ID
    private Long thirdMatchId;
}
