/**
 *
 */
package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;


@Data
public class StandardCategoryIdsDiffDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long standardMatchId;
    private Long aoMatchId;
    private Integer sportId;
    private Set<Long> standardCategoryIds;
}
