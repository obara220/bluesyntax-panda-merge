package com.panda.merge.dto;


import com.panda.merge.annotation.ScoresProperty;
import com.panda.merge.constant.TeamTypeConstant;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Field;

@Slf4j
@Data
public class AbstractSportScores  implements Serializable{

    public  void changeHomeAwayScore() throws IllegalAccessException {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if(field.getType().equals(com.panda.merge.dto.CommonItem.class)){
                field.setAccessible(true);
                CommonItem commonItem = (com.panda.merge.dto.CommonItem)field.get(this);
                if(commonItem!=null){
                    Integer home = commonItem.getHome();
                    Integer away = commonItem.getAway();
                    commonItem.setHome(away);
                    commonItem.setAway(home);
                }
            }
        }
    }

    public static  void init(AbstractSportScores abstractSportScores){
        Field[] fields = abstractSportScores.getClass().getDeclaredFields();
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  for (Field field : fields) {
            String filedName = field.getName();
            field.setAccessible(true);
            // 被StaticsItem修饰的属性才需要做统计
            if (!field.isAnnotationPresent(ScoresProperty.class)) {
                continue;
            }
            try {
                /***  获取统计项注解上的统计数据的event_code值   ***/
                ScoresProperty item = field.getAnnotation(ScoresProperty.class);
                if(!item.isDefaultCreate()){
                    continue;
                }
                String[] strings = item.eventCode();
//                CommonItem CommonItem=new CommonItem(item.eventName(),item.eventCode()[0]);
                CommonItem commonItem=new CommonItem();
                if(field.getType().equals(commonItem.getClass())){
                    field.set(abstractSportScores,commonItem);
                }else if(field.getType().equals(CommonFItem.class)){
                    CommonFItem commonFItem=new CommonFItem();
                    field.set(abstractSportScores,commonFItem);
                }else if(field.getType().equals(CommonItemBigDecimal.class)){
                    CommonItemBigDecimal commonItemBigDecimal=new CommonItemBigDecimal();
                    field.set(abstractSportScores,commonItemBigDecimal);
                }else {
                    Integer x= new Integer(0);
                    field.set(abstractSportScores,x);
                }


            } catch (Exception e) {
                
                String msg = "FootballScoresDto" + ";" + filedName + ":统计出错";
            }
        }
//        abstractSportScores.jsonType=abstractSportScores.getClass().getSimpleName();
    }


    /**
     * 1.根据code设置参数比分
     * */
    public boolean setFieldByEventCode(Long thirdMatchId, String eventCode,Integer home ,Integer away){
        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(ScoresProperty.class)) {
                continue;
            }
            ScoresProperty item = field.getAnnotation(ScoresProperty.class);
            try {
                for (String code : item.eventCode()) {
                    if (code.equals(eventCode)) {
                        if(field.get(this)==null){
                            log.info("thirdMatchId:{} setFieldByEventCode:{} 找不到属性",thirdMatchId,eventCode);
                            return false;
                        }
                        CommonItem commonItem = (com.panda.merge.dto.CommonItem)field.get(this);
                        if(commonItem.getHome()>=home&&commonItem.getAway()>=away){
                            log.info(" thirdMatchId:{} ,eventCode:{},消费顺序有误",thirdMatchId,eventCode);
                            return  false;
                        }
                        log.info(" thirdMatchId:{} ,eventCode:{},before: home:{},away:{}",thirdMatchId,eventCode,commonItem.getHome(),commonItem.getAway());
                        commonItem.setAway(away);
                        commonItem.setHome(home);
                        log.info(" thirdMatchId:{} ,eventCode:{},after: home:{},away:{}",thirdMatchId,eventCode,commonItem.getHome(),commonItem.getAway());
                        return  true;
                    }
                }
            }catch (IllegalAccessException e) {
                
            } catch (Exception e) {
                
            }
        }
        return  false;
    }
    /**
     * 1.根据code设置参数比分
     * */
    public boolean setFieldByCancelEventCode(Long thirdMatchId, String eventCode,Integer home ,Integer away){
        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(ScoresProperty.class)) {
                continue;
            }
            ScoresProperty item = field.getAnnotation(ScoresProperty.class);
            try {
                for (String code : item.eventCode()) {
                    if (code.equals(eventCode)) {
                        if(field.get(this)==null){
                            log.error("thirdMatchId:{} setFieldByEventCode:{} 找不到属性",thirdMatchId,eventCode);
                            return false;
                        }
                        CommonItem commonItem = (com.panda.merge.dto.CommonItem)field.get(this);
                        log.info(" thirdMatchId:{} ,eventCode:{},before: home:{},away:{}",thirdMatchId,eventCode,commonItem.getHome(),commonItem.getAway());
                        commonItem.setAway(away);
                        commonItem.setHome(home);
                        log.info(" thirdMatchId:{} ,eventCode:{},after: home:{},away:{}",thirdMatchId,eventCode,commonItem.getHome(),commonItem.getAway());
                        return  true;
                    }
                }
            }catch (IllegalAccessException e) {
                log.info("事件比分处理异常：", e);
            } catch (Exception e) {
                log.info("事件比分处理异常：", e);
            }
        }
        return  false;
    }
    /**
     * 2.根据code 主客队  设置计数器累加
     * */
    public boolean addEventScores(String eventCode, String team){
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(ScoresProperty.class)) {
                continue;
            }
            ScoresProperty item = field.getAnnotation(ScoresProperty.class);
            try {
                for (String code : item.eventCode()) {
                    if (code.equals(eventCode)) {
                        CommonItem CommonItem = (com.panda.merge.dto.CommonItem)field.get(this);
                            if(team.equals(TeamTypeConstant.AWAY)){
                                CommonItem.setAway(CommonItem.getAway()+1);
                            }else if(team.equals(TeamTypeConstant.HOME)){
                                CommonItem.setHome(CommonItem.getHome()+1);
                            }
                        return  true;
                    }
                }
            }catch (IllegalAccessException e) {
                log.error("addEventScores,处理异常,Exception:", e);
            }
        }
        return  false;
    }
    /**
     * 2.根据code 主客队  设置计数器累加
     * */
    public CommonItem getEventScores(String eventCode){
        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(ScoresProperty.class)) {
                continue;
            }
            ScoresProperty item = field.getAnnotation(ScoresProperty.class);
            try {
                for (String code : item.eventCode()) {
                    if (code.equals(eventCode)) {
                        CommonItem commonItem = (CommonItem)field.get(this);
                        return  commonItem;
                    }
                }
            }catch (IllegalAccessException e) {
                
            }
        }
        return  null;
    }
    /**
     * 2.根据code 主客队  设置计数器减少
     * */
    public boolean deleteEventScores(String eventCode, String team) {
        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(ScoresProperty.class)) {
                continue;
            }
            ScoresProperty item = field.getAnnotation(ScoresProperty.class);
            try {
                for (String code : item.eventCode()) {
                    if (code.equals(eventCode)) {
                        CommonItem CommonItem = (com.panda.merge.dto.CommonItem) field.get(this);
                        if (team.equals(TeamTypeConstant.AWAY)) {
                            CommonItem.setAway(CommonItem.getAway() - 1);
                            if(CommonItem.getAway()<0){
                                CommonItem.setAway(0);
                            }
                        } else if (team.equals(TeamTypeConstant.HOME)) {
                            CommonItem.setHome(CommonItem.getHome() - 1);
                            if(CommonItem.getHome()<0){
                                CommonItem.setHome(0);
                            }
                        }
                        return true;
                    }
                }
            } catch (IllegalAccessException e) {
                
            }

        }
        return false;
    }


}
