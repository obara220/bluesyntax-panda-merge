package com.panda.merge.common.enums;

import com.alibaba.fastjson.JSON;

/**
 * 赛事状态枚举 对应数据表 system_item_dict parent_type_id=5的记录枚举类
 * @author   tell
 * @since    2020年9月5日13:22:13
 */
public enum MatchStatusEnum {
    Not_Started(0, "赛事未开始", 5L),
    Live(1, "滚球阶段", 5L),
    Suspended(2, "暂停", 5L),
    Ended(3, "结束", 5L),
    Closed(4, "关闭", 5L),
    Cancelled(5, "取消", 5L),
    Abandoned(6, "放弃播报", 5L),
    Delayed(7, "延迟", 5L),
    Unknown(8, "未知", 5L),
    Postponed(9, "延期", 5L),
    Interrupted(10, "比赛中断", 5L),
    UnUsable(11, "不可用", 5L),
    /** 999 赛事阶段专用，非赛事状态*/
    Ended999(999, "完赛", 5L),
    ;
    public Integer value;
    public String desc;
    public long parentTypeId;

    MatchStatusEnum(Integer value, String desc, long parentTypeId) {
        this.value = value;
        this.desc = desc;
        this.parentTypeId = parentTypeId;
    }

    /**
     * 根据状态值获取枚举对象
     * @param status_value 状态值
     * @return MatchStatusEnum 具体状态枚举对象
     */
    public static MatchStatusEnum getEnum(int status_value) {
        for (MatchStatusEnum matchOverStatusEnum : MatchStatusEnum.values()) {
            if (matchOverStatusEnum.value.equals(status_value)) {
                return matchOverStatusEnum;
            }
        }
        return null;
    }


    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
