package com.panda.merge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.LanguageInternationDao;
import com.panda.merge.mapper.LanguageInternationMapper;
import com.panda.merge.model.LanguageInternation;
import com.panda.merge.model.LanguageInternationExample;
import com.panda.merge.service.LanguageInternationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.THIRD_TEAM_TOURNAMENT_UPDATE_REDIS;
import static com.panda.merge.constant.ConstantSystem.TWO;

/**
 * <Description> 多语言信息
 * @author      tell
 * @since       2020年9月3日15:24:52
 */
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class LanguageInternationServiceImpl extends BaseServiceImpl<LanguageInternation> implements LanguageInternationService {

    @Autowired
    private LanguageInternationMapper languageInternationMapper;
    @Autowired
    private LanguageInternationDao languageInternationDao;
    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Override
    @Cacheable(key = "'LanguageInternationMap:' + #dataSourceCode + '-' + #nameCode",unless="#result == null || #result.size() == 0")
    public Map<String,LanguageInternation> getLanguageType2Item(String dataSourceCode,Long nameCode){
        LanguageInternationExample example = new LanguageInternationExample();
        if(StringUtils.isEmpty(dataSourceCode)){
            example.createCriteria().andNameCodeEqualTo(nameCode);
        }else{
            example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andNameCodeEqualTo(nameCode);
        }
        return languageInternationMapper.selectByExample(example).stream().collect(Collectors.toMap(LanguageInternation::getLanguageType, i -> i));
    }

    @Override
    public Map<Long,List<LanguageInternation>> getItemsByNameCodes(List<Long> nameCodes){
        Map<Long,List<LanguageInternation>> nameCode2Languages = new LinkedHashMap<>();
        if(!CollectionUtils.isEmpty(nameCodes)){
            LanguageInternationExample example = new LanguageInternationExample();
            example.createCriteria().andNameCodeIn(nameCodes);
            List<LanguageInternation> resList = languageInternationMapper.selectByExample(example);
            if(!CollectionUtils.isEmpty(resList)){
                nameCode2Languages = resList.stream().collect(Collectors.groupingBy(obj->obj.getNameCode()));
            }
        }
        return nameCode2Languages;
    }

    @Override
    public LanguageInternation saveOrupdate(LanguageInternation upItem,String linkId){
        String redisKey = RedisConfig.REDIS_KEY_DATABASE + "::LanguageInternationMap:" + upItem.getDataSourceCode() + '-' + upItem.getNameCode();
        /** 根据创建时间来区分新增或修改（创建时间不为空是为新增）*/
        if(null != upItem.getCreateTime()){
            try{
                //2S内不允许重复入库
                String lockKey = String.format(RedisConfig.REDIS_KEY_DATABASE + "::LanguageInternation:DbLock:%s_%s_%s", upItem.getDataSourceCode(), upItem.getNameCode()
                        , upItem.getLanguageType());
                //如果是新增,避免同时新增出现唯一索引异常
                if(redisService.tryLockOnce(lockKey,lockKey,TWO)){
                    int num = languageInternationMapper.insertSelective(upItem);
                    log.info("linkId={},saveOrupdate 多语言信息新增完成，nameCode:{},dataSourceCode:{},LanguageType:{},num:{}",linkId,upItem.getNameCode(),upItem.getDataSourceCode(),upItem.getLanguageType(),num);
                    refreshHashCache(redisKey,upItem.getLanguageType(),upItem);
                }else{
                    log.info("linkId={},LanguageInternation,数据源编码={}，多语言code={},LanguageType={},2S内不允许重复入库",linkId,upItem.getDataSourceCode(),upItem.getNameCode(),upItem.getLanguageType());
                }

            }catch (DataAccessException e){
                log.error("linkId="+linkId+",根据nameCode="+upItem.getNameCode()+"，dataSourceCode="+upItem.getDataSourceCode()+"，LanguageType="+upItem.getLanguageType()+" 新增多语言,唯一主键冲突");
                redisService.del(redisKey);
            }
        }else{
            if(null != upItem.getModifyTime()){
                LanguageInternation item = new LanguageInternation();
                //数据来源，多语言类型 无需修改
                BeanUtil.copyProperties(upItem,item,"dataSourceCode","languageType","createTime");
                LanguageInternationExample example = new LanguageInternationExample();
                example.createCriteria().andIdEqualTo(item.getId()).andNameCodeEqualTo(item.getNameCode());
                int num = languageInternationMapper.updateByExampleSelective(item, example);
                log.info("linkId={},saveOrupdate 多语言信息修改完成，nameCode:{},dataSourceCode:{},LanguageType:{},num:{}",linkId,upItem.getNameCode(),upItem.getDataSourceCode(),upItem.getLanguageType(),num);
                refreshHashCache(redisKey,upItem.getLanguageType(),upItem);
            }else{
                log.info("linkId={},saveOrupdate 多语言信息无需编辑，nameCode:{},dataSourceCode:{},LanguageType:{}",linkId,upItem.getNameCode(),upItem.getDataSourceCode(),upItem.getLanguageType());
            }
        }
        return upItem;
    }

    @Override
    public List<LanguageInternation> saveOrupdateList(List<LanguageInternation> list,String linkId){
        for (LanguageInternation upItem: list) {
            saveOrupdate(upItem,linkId);
        }
        //球队、球员、联赛国际化信息变更时推送赛程MQ
        if(!CollectionUtils.isEmpty(list)){
            Long nameCode = list.get(0).getNameCode();
            if(redisService.hasKey("THIRD_TEAM_TOURNAMENT_UPDATE:" + nameCode)){
                Map<String, Long> map = new HashMap<>();
                map.put("nameCode", nameCode);
                MessageBuilder<String> builder = MessageBuilder.withPayload(JSON.toJSONString(map)).setHeader(MessageConst.PROPERTY_KEYS, nameCode);
                rocketMqTemplate.send(THIRD_TEAM_TOURNAMENT_UPDATE_REDIS+":"+nameCode, builder.build());
                log.info("linkId={},球队球员联赛国际化信息变更后推送完成, topic : "+THIRD_TEAM_TOURNAMENT_UPDATE_REDIS+", nameCode={}, dataSourceCode={}", linkId, nameCode, list.get(0).getDataSourceCode());
                //通知后清理缓存
                redisService.del("THIRD_TEAM_TOURNAMENT_UPDATE:" + nameCode);
            }
        }
        return list;

        /** 根据创建时间来区分新增或修改（创建时间不为空是为新增） 因为分表原因,批量新增或修改已经不支持,直接调用单挑数据修改*/
//        List<LanguageInternation> addList = list.stream().filter(obj -> !Objects.isNull(obj.getCreateTime())).collect(Collectors.toList());
//        try{
//            if(!CollectionUtils.isEmpty(addList)){
//                //需要新增的
////                List<LanguageInternation> addNewList = new LinkedList<>();
//                for (LanguageInternation item: addList) {
//                    //获取多语言锁
//                    String languageLockKey = item.getNameCode() + item.getLanguageType();
//                    //如果是新增,避免同时新增出现唯一索引异常
//                    if(redisService.tryLockOnce(languageLockKey,languageLockKey,ONE)){
////                        addNewList.add(item);
//                        int num = languageInternationMapper.insert(item);
//                        log.info("saveOrupdateList 多语言信息新增完成，nameCode: {}  num:{},",item.getNameCode(),num);
//                        redisService.del(RedisConfig.REDIS_KEY_DATABASE + "::LanguageInternationMap:"+ item.getNameCode());
//                    }
//                }
//            }
//        }catch (DataAccessException e){
//            Set<Long> nameCodes = list.stream().map(obj -> obj.getNameCode()).collect(Collectors.toSet());
//            Set<String> dataSourceCodes = list.stream().map(obj -> obj.getDataSourceCode()).collect(Collectors.toSet());
//            log.error("saveOrupdateList 根据nameCode列表:"+ JSON.toJSONString(nameCodes) +",dataSourceCodes: "+JSON.toJSONString(dataSourceCodes)+" 批量新增或修改多语言，唯一主键冲突，Exception:",e);
//            for (Long nameCode: nameCodes) {
//                redisService.del(RedisConfig.REDIS_KEY_DATABASE + "::LanguageInternationMap:"+ nameCode);
//            }
//        }
//        //需要修改的数据
//        List<LanguageInternation> updateList = list.stream().filter(obj -> Objects.isNull(obj.getCreateTime()) && !Objects.isNull(obj.getModifyTime())).collect(Collectors.toList());
//        if(!CollectionUtils.isEmpty(updateList)){
//            //分表后不适合批量
////                languageInternationDao.updateList(updateList);
//            for (LanguageInternation item: updateList) {
//                LanguageInternationExample example = new LanguageInternationExample();
//                //分表字段，在修改的时候必须带上
//                example.createCriteria().andIdEqualTo(item.getId()).andNameCodeEqualTo(item.getNameCode());
//                int num = languageInternationMapper.updateByExampleSelective(item, example);
//                log.info("saveOrupdateList 多语言信息新增完成，nameCode: {}  num:{},",item.getNameCode(),num);
//                refreshHashCache(RedisConfig.REDIS_KEY_DATABASE + "::LanguageInternationMap:"+ item.getNameCode(),item.getLanguageType(),item);
//            }
//        }
//        return list;
    }

    @Override
    @CacheEvict(key = "'LanguageInternationMap:' + #item.dataSourceCode +  '-' + #item.nameCode")
    public void delItem(LanguageInternation item,String linkId){
        LanguageInternationExample example = new LanguageInternationExample();
        example.createCriteria().andIdEqualTo(item.getId()).andNameCodeEqualTo(item.getNameCode());
        languageInternationMapper.deleteByExample(example);
        log.info("linkId={},delItem 删除废弃的多语言完成，nameCode:{},dataSourceCode:{},LanguageType:{}",linkId,item.getNameCode(),item.getDataSourceCode(),item.getLanguageType());
    }

    @Override
    public List<LanguageInternation> getLanguageInternationByNameCode(Long nameCode) {
        LanguageInternationExample example = new LanguageInternationExample();
        example.createCriteria().andNameCodeEqualTo(nameCode);
        List<LanguageInternation> resList = languageInternationMapper.selectByExample(example);
        if ( CollectionUtils.isEmpty(resList) ) {
            return null;
        }
        return resList;
    }


}
