package com.panda.merge.utils;

import com.panda.merge.model.MatchEventInfo;

import java.util.ArrayList;
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
}
