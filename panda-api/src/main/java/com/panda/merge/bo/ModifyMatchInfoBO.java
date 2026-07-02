package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ModifyMatchInfoBO<T> implements Serializable {

    /** 链路ID **/
    private String linkId;

    /** 标准赛事ID **/
    private Long standardMatchId;

    /** 操作人ID **/
    private Long operaterId;

    /** 操作关联数据 **/
    private T data;

    /** 操作的原因 **/
    private String operateReason;

    /** 触发操作的时间 **/
    private Long operateTime;

    /** 默认当前时间为触发时间 **/
    public void setOperateTime(){
        this.operateTime = System.currentTimeMillis();
    }

}
