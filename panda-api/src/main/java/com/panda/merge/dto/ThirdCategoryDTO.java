package com.panda.merge.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 基本玩法
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-09-03
 */
@Data
public class ThirdCategoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 第三方玩法原始ID.
     */
    private String thirdSourceId;

    /**
     * 取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source
     */
    private String dataSourceCode;

    /**
     * 标准玩法id
     */
    private Long referenceId;
    
    /**
     * 赛事Id
     */
    private Long matchId;

}
