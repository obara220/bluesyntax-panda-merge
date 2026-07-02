package com.panda.merge.component;

public enum AlertType {
    TOO_MANY_ACTIVE_CONNECTIONS(1, "活动连接数过多"),
    CONNECTION_FAILED(2, "数据库连接失败"),
    SQL_DEADLOCK(3, "SQL死锁"),
    SQL_EXCEPTION(4, "SQL异常"),
    SQL_SLOW_QUERY(5, "SQL慢查询"),
    UNKNOWN_ERROR(-1, "其他异常");

    private int type;
    private String name;

    private AlertType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public String toString() {
        return (new StringBuilder(20)).append("(").append(this.type).append(") ").append(this.name).toString();
    }

    public int getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public static String getNameByType(int type) {
        for(AlertType alertType:AlertType.values()){
            if (type == alertType.getType()) {
                return alertType.getName();
            }
        }
        return "Mismatch";
    }
}
