package com.panda.merge.api;

import com.panda.merge.bo.SystemDataBO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;

/**
 * 获取全部字典信息（字典类型+字典值）
 * @author  tell
 * @since   2020年9月9日20:24:00
 * */
public interface ISystemDataQueryApi {

    /**
     * 查询融合基础数据API
     * @return
     */
    Response<SystemDataBO> querySystemData();

    /**
     * 手动触发MQ暂停消费(目前只支持事件异步入库)
     * @param request
     *   data参数  {"pandaDbIsError":""} panda数据库是否异常（0:否，1:是）
     * @return
     */
    Response<String> processRocketmqConsumer(Request<String> request);
}
