/**
 *
 */
package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description  :  标准玩法自动关盘
 * @author       :  aison
 * @Date:  2020年10月30日15:10:37
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class StandardCategoryAutoCloseMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 标准赛事ID
	 */
	private Long standardMatchId;

	/**
	 * 玩法集合List
	 */
	private List<Long> standardCategoryList;
}
