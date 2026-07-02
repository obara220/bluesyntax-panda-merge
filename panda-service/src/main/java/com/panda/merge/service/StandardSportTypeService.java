package com.panda.merge.service;

import com.panda.merge.dto.StandardSportTypeDTO;
import com.panda.merge.model.StandardSportType;

import java.util.List;

/**
 * <Description> 标准运动类型信息（含多语言）
 * @author      tell
 * @since       2020年9月10日14:07:09
 */
public interface StandardSportTypeService {

    /**
     * 根据修改时间筛选获取标准运动类型信息（含多语言）
     * @param parDto   参数对象
     * @return  List<StandardSportType>
     * */
    List<StandardSportType> getItemListByModifyTime(StandardSportTypeDTO parDto);
}
