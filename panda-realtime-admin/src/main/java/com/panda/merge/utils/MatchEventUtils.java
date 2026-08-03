package com.panda.merge.utils;

import com.alibaba.fastjson.JSON;
import com.panda.merge.common.utils.EntityEqualsUtils;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.ThirdVideoBoardCastRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 事件工具类
 * */
public class MatchEventUtils {
    /**
     * 对事件进行取消处理
     */
    public static List<MatchEventInfo> doCancelEvent(List<MatchEventInfo> originalEvents) {
        List<MatchEventInfo> resule =new ArrayList<>();
        List<String> cancelIds =new ArrayList<>();
        for (MatchEventInfo originalEvent : originalEvents) {
            if(1==originalEvent.getCanceled()){
                cancelIds.add(originalEvent.getExtraInfo());
            }
        }
        for (MatchEventInfo originalEvent : originalEvents) {
            if(!cancelIds.contains( originalEvent.getThirdEventId())){
                resule.add(originalEvent);
            }
        }
        return resule;
    }


    public static void main(String[] args) {
        ThirdVideoBoardCastRecord item1 = new ThirdVideoBoardCastRecord();
        item1.setCreateTime(System.currentTimeMillis());
        item1.setModifyTime(System.currentTimeMillis());
        item1.setAniId("123456");
        item1.setDataSourceCode("TS");
        item1.setAwayZn("测试");
        item1.setLiveVideoOnline(1L);
        item1.setLiveVideoPathStatus(null);
        item1.setStartDate(new Date());

        ThirdVideoBoardCastRecord item2 = new ThirdVideoBoardCastRecord();
        item2.setCreateTime(item1.getCreateTime()+1000);
        item2.setModifyTime(item1.getModifyTime()+1000);
        item2.setAniId("123456");
        item2.setDataSourceCode("TS");
        item2.setAwayZn("测试");
        item2.setLiveVideoOnline(1L);
        item2.setLiveVideoPathStatus(null);
        item2.setStartDate(new Date());

        System.out.println(JSON.toJSONString(item1));
        System.out.println(JSON.toJSONString(item2));

        Boolean flag = EntityEqualsUtils.equalsIsObjToString(item1, item2);
        System.out.println("对象比较返回结果="+flag);
    }
}
