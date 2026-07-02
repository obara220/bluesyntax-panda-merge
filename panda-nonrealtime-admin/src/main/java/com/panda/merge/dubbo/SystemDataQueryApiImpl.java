package com.panda.merge.dubbo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.api.ISystemDataQueryApi;
import com.panda.merge.bo.SystemDataBO;
import com.panda.merge.bo.SystemItemDictBO;
import com.panda.merge.bo.SystemTypeDictBO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.job.RocketmqConsumerJob;
import com.panda.merge.model.SystemItemDict;
import com.panda.merge.model.SystemTypeDict;
import com.panda.merge.service.SystemItemDictService;
import com.panda.merge.service.SystemTypeDictService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.panda.merge.constant.ConstantSystem.PROJECT_ID_NOREALTIME;
import static com.panda.merge.constant.ConstantSystem.QUERY_SYSTEM_DATA;

/**
 * 获取全部字典信息（字典类型+字典值）
 * @author  tell
 * @since   2020年9月9日20:24:00
 * */
@Slf4j
@Component
@DubboService
public class SystemDataQueryApiImpl implements ISystemDataQueryApi {

    @Autowired
    private SystemTypeDictService systemTypeDictService;
    @Autowired
    private SystemItemDictService systemItemDictService;


    @Override
    public Response<SystemDataBO> querySystemData() {
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_SYSTEM_DATA+"】获取全部字典信息（字典类型+字典值）开始" );
        long beginTime = System.currentTimeMillis();
        Response response = Response.success();
        //查询所有 SystemTypeDict
        List<SystemTypeDict> systemTypeDicts = systemTypeDictService.getItemAll();
       if(CollectionUtils.isEmpty(systemTypeDicts)){
            return response;
       }
        // List<SystemTypeDict>  转 List<SystemTypeDictBO>
        List<SystemTypeDictBO> resList = new ArrayList<>();
        //对象转BO
        systemTypeDicts.forEach(typeItem -> {
            SystemTypeDictBO systemTypeDictBO = new SystemTypeDictBO();
            BeanUtils.copyProperties(typeItem, systemTypeDictBO);
            List<SystemItemDict> systemItemDicts = systemItemDictService.getListByParentTypeId(typeItem.getId());
            List<SystemItemDictBO> listItem = new ArrayList<SystemItemDictBO>();
            systemItemDicts.forEach(item -> {
                SystemItemDictBO systemItemDictBO = new SystemItemDictBO();
                //对象转换BO对象
                BeanUtils.copyProperties(item, systemItemDictBO);
                //  systemItemDictBO存入systemItemDictBO集合
                listItem.add(systemItemDictBO);
            });
            systemTypeDictBO.setSystemItemDictList(listItem);
            resList.add(systemTypeDictBO);
        });
        SystemDataBO systemDataBO = new SystemDataBO();
        systemDataBO.setSystemTypeDictList(resList);
        response.setData(systemDataBO);
        response.setDataSourceTime(System.currentTimeMillis() - beginTime);
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+QUERY_SYSTEM_DATA+"】获取全部字典信息（字典类型+字典值）结束,返回结果 ：{}" ,JSON.toJSONString(response));
        return response;
    }


    @Autowired
    private RocketmqConsumerJob rocketmqConsumerJob;

    @Override
    public Response<String> processRocketmqConsumer(Request<String> request){
        try{
            log.info("【processRocketmqConsumer 手动触发MQ暂停消费（部分topic）】 处理开始,入参: {}", request);
            JSONObject jsonObject = JSON.parseObject(request.getData());
            rocketmqConsumerJob.processData(jsonObject,request.getDataSourceTime());
            log.info("【processRocketmqConsumer 手动触发MQ暂停消费（部分topic）】 处理结束");
        }catch (Exception e){
            log.error("【processRocketmqConsumer 手动触发MQ暂停消费（部分topic）执行异常】 Exception:", e);
            return Response.failed(e.getMessage());
        }
        return Response.success();
    }

}
