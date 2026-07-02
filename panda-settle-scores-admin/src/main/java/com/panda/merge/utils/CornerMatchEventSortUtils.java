package com.panda.merge.utils;

import com.panda.merge.dto.settle.MatchSettleEventDto;
import com.panda.merge.model.MatchSettleEvent;

import java.util.ArrayList;
import java.util.List;

public class CornerMatchEventSortUtils {

    private static List<Long> periodList =new ArrayList<>();
    static {
        periodList.add(6l);periodList.add(31l);periodList.add(7l);periodList.add(100l);periodList.add(41l);
        periodList.add(32l);periodList.add(42l);periodList.add(110l);periodList.add(50l);periodList.add(120l);
        periodList.add(999l);
    }

    public  static  Integer compareCornerMatchEventAndScore(MatchSettleEventDto o1, MatchSettleEventDto o2){
        Integer x1= periodList.indexOf(o1.getPeriodId());
        Integer x2= periodList.indexOf(o2.getPeriodId());
        if(x1!=x2){
            return x2-x1;
        }
        if(!o1.getSettleNum().equals(o2.getSettleNum())){
            return o2.getSettleNum().compareTo(o1.getSettleNum());
        }

        return  o2.getEventOrder()-o1.getEventOrder();
    }

//    public static void main(String[]x){
//        String xx="201"; String xx2="202";
//        System.out.println(xx.compareTo(xx2));
//    }
}
