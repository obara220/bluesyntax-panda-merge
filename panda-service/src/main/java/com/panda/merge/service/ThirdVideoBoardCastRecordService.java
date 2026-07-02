package com.panda.merge.service;

import com.panda.merge.dto.nonrealttime.query.ThirdMatchInfoDTO;
import com.panda.merge.model.ThirdVideoBoardCastRecord;

import java.util.List;

/**
 * 赛事播控平台视频记录(泰森独有)
 * @author tell
 * @since  2020年10月18日09:01:35
 */
public interface ThirdVideoBoardCastRecordService {


    /**
     * 根据修改时间筛选
     * @return List<ThirdVideoBoardCastRecord>
     */
    List<ThirdVideoBoardCastRecord> getItemByModifyTime(ThirdMatchInfoDTO item);

    /**
     * 获取视频列表
     * @param  thirdMatchSourceId  数据源赛事ID
     * @param  dataSourceCode  数据来源code
     * @return ThirdVideoBoardCastRecord
     * */
    ThirdVideoBoardCastRecord getItem(String thirdMatchSourceId,String dataSourceCode);

    /**
     * 新增
     * @param  item  对象信息
     * @return ThirdVideoBoardCastRecord
     * */
    ThirdVideoBoardCastRecord saveItem(ThirdVideoBoardCastRecord item);

    /**
     * 修改
     * @param  item  对象信息
     * @return ThirdVideoBoardCastRecord
     * */
    ThirdVideoBoardCastRecord updateItem(ThirdVideoBoardCastRecord item);

}
