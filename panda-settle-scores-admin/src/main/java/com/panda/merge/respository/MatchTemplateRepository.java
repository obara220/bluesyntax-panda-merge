//package com.panda.merge.respository;
//
//import cn.hutool.crypto.digest.DigestUtil;
//import com.alibaba.fastjson.JSONObject;
//import com.panda.merge.config.RedisService;
//import com.panda.merge.mapper.MatchSettleTemplateMapper;
//import com.panda.merge.mapper.MatchSettleTemplateRelationMapper;
//import com.panda.merge.mapper.SettleTemplateExtMappper;
//import com.panda.merge.model.*;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//import static com.panda.merge.constant.RepositoryConstant.*;
//
//
//@Service
//@Slf4j
//public class MatchTemplateRepository {
//
//    @Autowired
//    RedisService redisService;
//    @Autowired
//    MatchSettleTemplateRelationMapper matchSettleTemplateRelationMapper;
//    @Autowired
//    MatchSettleTemplateMapper matchSettleTemplateMapper;
//    @Autowired
//    SettleTemplateExtMappper settleTemplateExtMappper;
//
//    public MatchSettleTemplateRelation getMatchSettleTemplateRelation(Long id) {
//        String key = TEMPLATE_RELATION+id;
//        Object o = null;
//        try{
////            o = redisService.get(DigestUtil.md5Hex(key));
//            o = redisService.get(key);
//        }catch (Exception e){
//            log.error("redis读异常TemplateRelation：key:"+ key, e);
//        }
//        MatchSettleTemplateRelation matchSettleTemplateRelation =null;
//        if (o != null) {
//            matchSettleTemplateRelation = JSONObject.toJavaObject(JSONObject.parseObject(o.toString()), MatchSettleTemplateRelation.class);
//                return matchSettleTemplateRelation;
//        }else{
//            MatchSettleTemplateRelationExample example =new MatchSettleTemplateRelationExample();
//            example.createCriteria().andStandardTournamentIdEqualTo(id);
//            List<MatchSettleTemplateRelation> list = matchSettleTemplateRelationMapper.selectByExample(example);
//            if (!list.isEmpty()){
//                matchSettleTemplateRelation = list.get(0);
//                try{
////                    redisService.set(DigestUtil.md5Hex(key),JSONObject.toJSON(matchSettleTemplateRelation),REDIS_THREE_TIME);
//                    redisService.set(key,JSONObject.toJSON(matchSettleTemplateRelation),REDIS_THREE_TIME);
//                }catch (Exception e){
//                    log.error("getMatchSettleTemplateRelation:redis写入异常TemplateRelation：key=[{}]TemplateRelation[{}]", key,JSONObject.toJSON(matchSettleTemplateRelation), e);
//                }
//
//            }
//        }
//        return matchSettleTemplateRelation;
//    }
//    public void delTemplateRelationByExample(MatchSettleTemplateRelationExample example) {
//        List<MatchSettleTemplateRelation> list = matchSettleTemplateRelationMapper.selectByExample(example);
//        if (!list.isEmpty()){
//            list.forEach(l->{
//                String key = TEMPLATE_RELATION+l.getId();
//                try {
//                    redisService.del(key);
//                }catch (Exception e){
//                    log.error("deleteTemplate:redis删除异常：key=[{}]TemplateRelation[{}]", key,JSONObject.toJSON(l), e);
//                }
//
//            });
//        }
//    }
//
//    public void insertTemplateRelationToRedis(MatchSettleTemplateRelation matchSettleTemplateRelation){
//        String key = TEMPLATE_RELATION+matchSettleTemplateRelation.getId();
//        try {
//            redisService.set(key,JSONObject.toJSON(matchSettleTemplateRelation),REDIS_THREE_TIME);
//        }catch (Exception e){
//            log.error("insertTemplateRelationToRedis:redis插入异常：key=[{}]TemplateRelation[{}]TemplateRelation[{}]", key,JSONObject.toJSON(matchSettleTemplateRelation), e);
//        }
//    }
//    public void batchInsertTemplateRelationToRedis(Integer level){
//        List<MatchSettleTemplateRelation> list = settleTemplateExtMappper.selectTemplateRelationByLevel(level);
//        if (!list.isEmpty()){
//            list.forEach(l->{
//                String key = TEMPLATE_RELATION+l.getId();
//                try {
//                    redisService.set(key,JSONObject.toJSON(l),REDIS_THREE_TIME);
//                }catch (Exception e){
//                    log.error("batchInsertTemplateRelationToRedis:redis插入异常：key=[{}]TemplateRelation[{}]TemplateRelation[{}]", key,JSONObject.toJSON(l), e);
//                }
//            });
//        }
//
//
//    }
//
//
//    public MatchSettleTemplate getMatchSettleTemplateByTypeAndLevel(Integer type,Integer level, Long sportId){
//        String key = "TEMPLATE_LEVEL"+level+"_TYPE"+type+"_SPORT_"+sportId;
//        Object o = null;
//        try{
////            o = redisService.get(DigestUtil.md5Hex(key));
//            o = redisService.get(key);
//        }catch (Exception e){
//            log.error("redis读异常TemplateByTypeAndLevel：key=[{}]", key, e);
//        }
//        MatchSettleTemplate matchSettleTemplate =null;
//        if (o != null) {
//            matchSettleTemplate = JSONObject.toJavaObject(JSONObject.parseObject(o.toString()), MatchSettleTemplate.class);
//            return matchSettleTemplate;
//        }else{
//            MatchSettleTemplateExample templateExample=new MatchSettleTemplateExample();
//            templateExample.createCriteria().andTournamentLevelEqualTo(level).andTemplateTypeEqualTo(type).andSportIdEqualTo(sportId);
//            List<MatchSettleTemplate> list = matchSettleTemplateMapper.selectByExample(templateExample);
//            if (!list.isEmpty()){
//                matchSettleTemplate = list.get(0);
//                try {
////                    redisService.set(DigestUtil.md5Hex(key),JSONObject.toJSON(matchSettleTemplate),REDIS_THREE_TIME);
//                    redisService.set(key,JSONObject.toJSON(matchSettleTemplate),REDIS_THREE_TIME);
//                }catch (Exception e){
//                    log.error("getMatchSettleTemplateByTypeAndLevel:redis写入异常TemplateByTypeAndLevel：key=[{}]MatchSettleTemplate[{}]", key,JSONObject.toJSON(matchSettleTemplate), e);
//                }
//
//            }
//        }
//        return matchSettleTemplate;
//
//    }
//
//    public  MatchSettleTemplate getMatchSettleTemplateByPrimaryKey(Long id){
//        String key = TEMPLATE_ID+id;
//        Object o = null;
//        try {
////            o = redisService.get(DigestUtil.md5Hex(key));
//            o = redisService.get(key);
//        }catch (Exception e){
//            log.error("redis读异常TemplateByPrimaryKey：key=[{}]", key, e);
//        }
//
//        MatchSettleTemplate matchSettleTemplate = null;
//        if (o != null) {
//            matchSettleTemplate = JSONObject.toJavaObject(JSONObject.parseObject(o.toString()), MatchSettleTemplate.class);
//            if (null!=matchSettleTemplate){
//                return matchSettleTemplate;
//            }
//        }else{
//            matchSettleTemplate = matchSettleTemplateMapper.selectByPrimaryKey(id);
//            if (null!=matchSettleTemplate){
//                try {
////                    redisService.set(DigestUtil.md5Hex(key),JSONObject.toJSON(matchSettleTemplate),REDIS_THREE_TIME);
//                    redisService.set(key,JSONObject.toJSON(matchSettleTemplate),REDIS_THREE_TIME);
//                }catch (Exception e){
//                    log.error("getMatchSettleTemplateByPrimaryKey:redis写入异常TemplateByPrimaryKey：key=[{}]MatchSettleTemplate[{}]", key,JSONObject.toJSON(matchSettleTemplate), e);
//                }
//
//            }
//        }
//        return matchSettleTemplate;
//
//    }
//
//    public void  insertTemplateToRedis(MatchSettleTemplate matchSettleTemplate){
//        String key = TEMPLATE_ID+matchSettleTemplate.getId();
//        try {
//            redisService.set(key,JSONObject.toJSON(matchSettleTemplate),REDIS_THREE_TIME);
//        }catch (Exception e){
//            log.error("insertTemplateToRedis:redis新增异常TemplateByPrimaryKey：key=[{}]MatchSettleTemplate[{}]", key,JSONObject.toJSON(matchSettleTemplate), e);
//        }
//    }
//    public void  insertMatchSettleTemplateByTypeAndLevel(MatchSettleTemplate matchSettleTemplate){
//        String key = "TEMPLATE_LEVEL"+matchSettleTemplate.getTournamentLevel()+"_TYPE"+matchSettleTemplate.getTemplateType()+"_SPORT_"+matchSettleTemplate.getSportId();
//
//        try {
//            redisService.set(key,JSONObject.toJSON(matchSettleTemplate),REDIS_THREE_TIME);
//        }catch (Exception e){
//            log.error("insertMatchSettleTemplateByTypeAndLevel:redis新增异常TemplateByPrimaryKey：key=[{}]MatchSettleTemplate[{}]", key,JSONObject.toJSON(matchSettleTemplate), e);
//        }
//    }
//
//    public void  delTemplateByPrimaryKey(Long id){
//        String key = TEMPLATE_ID+id;
//        try {
//            redisService.del(key);
//        }catch (Exception e){
//            log.error("deleteTemplate:redis删除异常TemplateByPrimaryKey：key=[{}]", key, e);
//        }
//    }
//
//    public void  delTemplateByByTypeAndLevel(Integer type,Integer level, Long sportId){
//        String key = "TEMPLATE_LEVEL"+level+"_TYPE"+type+"_SPORT_"+sportId;
//        try {
//            redisService.del(key);
//        }catch (Exception e){
//            log.error("delTemplateByByTypeAndLevel:redis删除异常delTemplateByByTypeAndLevel：key=[{}]", key, e);
//        }
//    }
//    public static void main(String[] args) {
//    }
//
//
//}
