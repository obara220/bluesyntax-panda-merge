package com.panda.merge.common.enums;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;

/**
 * 球员位置类型枚举
 *
 * @author tell
 * @since 2020年12月22日14:29:42
 */
@Getter
public enum PlayerPositionTypeEnum {

    NUM_01("Half-Time", "中场", "{\"zs\":\"中场\",\"zh\":\"中場\",\"en\":\"Midfielder\",\"vi\":\"Tiền vệ trung tâm\",\"es\":\"Descanso\",\"pt\":\"Intervalo\",\"mya\":\"\",\"ru\":\"паўабарона\"}"),
    NUM_02("Forward", "前锋", "{\"zs\":\"前锋\",\"zh\":\"前鋒\",\"en\":\"Forward\",\"vi\":\"Tiền đạo trung tâm\",\"es\":\"Delantero\",\"pt\":\"Atacante\",\"mya\":\"\",\"ru\":\"Нападающий\"}"),
    NUM_03("Guard", "后卫", "{\"zs\":\"后卫\",\"zh\":\"後衛\",\"en\":\"Guard\",\"vi\":\"Hậu vệ\",\"es\":\"Defensa\",\"pt\":\"Defensor\",\"mya\":\"\",\"ru\":\"Защитник\"}"),
    NUM_04("Goalkeeper", "门将", "{\"zs\":\"门将\",\"zh\":\"門將\",\"en\":\"Goalkeeper\",\"vi\":\"Thủ môn\",\"es\":\"Portero\",\"pt\":\"Guarda-redes\",\"mya\":\"\",\"ru\":\"Вратарь\"}"),
    NUM_05("Goalie", "守门员", "{\"zs\":\"守门员\",\"zh\":\"守門員\",\"en\":\"Goalie\",\"vi\":\"Thủ môn\",\"es\":\"Portero\",\"pt\":\"Guarda-redes\",\"mya\":\"\",\"ru\":\"Вратарь\"}"),
    NUM_06("Center Striker", "中锋", "{\"zs\":\"中锋\",\"zh\":\"中鋒\",\"en\":\"Center Striker\",\"vi\":\"Trung phong\",\"es\":\"Delantero Centro\",\"pt\":\"Defesa Central\",\"mya\":\"\",\"ru\":\"Центральный нападающий\"}"),
    NUM_07("Forward Guard Swingman", "锋卫摇摆人", "{\"zs\":\"锋卫摇摆人\",\"zh\":\"鋒衛搖擺人\",\"en\":\"Forward Guard Swingman\",\"vi\":\"Hậu vệ ghi điểm\",\"es\":\"Delantero Posición Swingman\",\"pt\":\"Extremo Defensor Swingman\",\"mya\":\"\",\"ru\":\"Нападающий защитник\"}"),
    NUM_08("Forward Position Swingman", "锋位摇摆人", "{\"zs\":\"锋位摇摆人\",\"zh\":\"鋒位搖擺人\",\"en\":\"Forward Position Swingman\",\"vi\":\"tiền phong phụ\",\"es\":\"Delantero Posición Swingman\",\"pt\":\"Posição de Extremo Swingman\",\"mya\":\"\",\"ru\":\"Передовая позиция\"}"),
    NUM_09("Small Striker", "小前锋", "{\"zs\":\"小前锋\",\"zh\":\"小前鋒\",\"en\":\"Small Striker\",\"vi\":\"Tiền đạo nhỏ\",\"es\":\"Delantero medio\",\"pt\":\"Atacante Médio\",\"mya\":\"\",\"ru\":\"Маленький нападающий\"}"),
    NUM_10("Power Striker", "大前锋", "{\"zs\":\"大前锋\",\"zh\":\"大前鋒\",\"en\":\"Power Striker\",\"vi\":\"Tiền đạo trung tâm\",\"es\":\"Delantero potente\",\"pt\":\"Atacante Potente\",\"mya\":\"\",\"ru\":\"мощный нападающий\"}"),
    NUM_11("Striker / Center Striker", "前锋/中锋", "{\"zs\":\"前锋/中锋\",\"zh\":\"前鋒/中鋒\",\"en\":\"Striker / Center Striker\",\"vi\":\"Tiền đạo / Tiền đạo cắm\",\"es\":\"Delantero / Delantero Centro\",\"pt\":\"Atacante / Atacante Central\",\"mya\":\"\",\"ru\":\"Нападающий / Центральный нападающий\"}"),
    NUM_12("Striker / Center Striker", "前锋-中锋", "{\"zs\":\"前锋/中锋\",\"zh\":\"前鋒/中鋒\",\"en\":\"Striker / Center Striker\",\"vi\":\"Tiền đạo / Tiền đạo cắm\",\"es\":\"Delantero / Delantero Centro\",\"pt\":\"Atacante / Atacante Central\",\"mya\":\"\",\"ru\":\"Нападающий / Центральный нападающий\"}"),
    NUM_13("Striker-Defender", "前锋-后卫", "{\"zs\":\"前锋-后卫\",\"zh\":\"前鋒-後衛\",\"en\":\"Striker-Defender\",\"vi\":\"Tiền đạo - hậu vệ\",\"es\":\"Delantero-Defensa\",\"pt\":\"Atacante-Defensor\",\"mya\":\"\",\"ru\":\"Нападающий-Защитник\"}"),
    NUM_14("Mid Striker", "中锋", "{\"zs\":\"中锋\",\"zh\":\"中鋒\",\"en\":\"Mid Striker\",\"vi\":\"Tiền đạo trung tâm\",\"es\":\"\tDelantero Medio\",\"pt\":\"Médio Atacante\",\"mya\":\"\",\"ru\":\"Средний нападающий\"}"),
    NUM_15("Ball-Handling Striker", "前锋", "{\"zs\":\"前锋\",\"zh\":\"前鋒\",\"en\":\"Ball-Handling Striker\",\"vi\":\"Tiền đạo cánh\",\"es\":\"Delantero con manejo de balón\",\"pt\":\"Ponta de Lança com Bola\",\"mya\":\"\",\"ru\":\"Нападающий, владеющий мячом\"}"),
    NUM_16("Center Defender", "中卫", "{\"zs\":\"中卫\",\"zh\":\"中衛\",\"en\":\"Center Defender\",\"vi\":\"Trung vệ\",\"es\":\"Defensa Central\",\"pt\":\"Defesa Central\",\"mya\":\"\",\"ru\":\"Центральный защитник\"}"),
    NUM_17("Central Midfielder", "中前卫", "{\"zs\":\"中前卫\",\"zh\":\"中前衛\",\"en\":\"Central Midfielder\",\"vi\":\"Tiền vệ trung\",\"es\":\"Mediocampista Central\",\"pt\":\"Meio-campista central\",\"mya\":\"\",\"ru\":\"Центральный полузащитник\"}"),
    NUM_18("Substitute", "替补", "{\"zs\":\"替补\",\"zh\":\"替補\",\"en\":\"Substitute\",\"vi\":\"Cầu thủ dự bị\",\"es\":\"Suplente\",\"pt\":\"Suplente\",\"mya\":\"\",\"ru\":\"Замена\"}"),
    NUM_19("Defensive Midfielder", "后腰", "{\"zs\":\"后腰\",\"zh\":\"後腰\",\"en\":\"Defensive Midfielder\",\"vi\":\"Tiền vệ phòng ngự\",\"es\":\"Mediocampista Defensivo\",\"pt\":\"Meio-campo Defensivo\",\"mya\":\"\",\"ru\":\"Опорный полузащитник\"}"),
    NUM_20("Right Winger", "右边锋", "{\"zs\":\"右边锋\",\"zh\":\"右邊鋒\",\"en\":\"Right Winger\",\"vi\":\"Tiền vệ chạy cánh phải bóng\",\"es\":\"Extremo Derecho\",\"pt\":\"Extremo Direito\t\",\"mya\":\"\",\"ru\":\"Крайний нападающий\"}"),
    NUM_21("Point Guard", "控球后卫", "{\"zs\":\"控球后卫\",\"zh\":\"控球後衛\",\"en\":\"Point Guard\",\"vi\":\"Hậu vệ dẫn bóng\",\"es\":\"Punta de ataque\",\"pt\":\"Armador\",\"mya\":\"\",\"ru\":\"Разыгрывающий\"}"),
    NUM_22("Scoring Guard", "得分后卫", "{\"zs\":\"得分后卫\",\"zh\":\"得分後衛\",\"en\":\"Scoring Guard\",\"vi\":\"Hậu vệ ghi điểm\",\"es\":\"Marcador de punta\",\"pt\":\"Ala Armador\",\"mya\":\"\",\"ru\":\"Голевой защитник\"}"),
    NUM_23("Guard-Striker", "后卫-前锋", "{\"zs\":\"后卫-前锋\",\"zh\":\"後衛-前鋒\",\"en\":\"Guard-Striker\",\"vi\":\"Hậu vệ - Tiền đạo\",\"es\":\"Marcador de punta\",\"pt\":\"Médio-ofensivo\",\"mya\":\"\",\"ru\":\"Защитник-Нападающий\"}"),
    NUM_24("Position", "位置", "{\"zs\":\"位置\",\"zh\":\"位置\",\"en\":\"Position\",\"vi\":\"Vị trí\",\"es\":\"Posición\",\"pt\":\"Posição\",\"mya\":\"\",\"ru\":\"Позиция\"}"),
    NUM_25("Head Coach", "主教练", "{\"zs\":\"主教练\",\"zh\":\"主教練\",\"en\":\"Head Coach\",\"vi\":\"Huấn luyện viên\",\"es\":\"Entrenador\",\"pt\":\"Treinador Principal\",\"mya\":\"\",\"ru\":\"Главный тренер\"}"),

    NUM_26("Left-back", "左后卫", "{\"zs\":\"左后卫\",\"zh\":\"左後衛\",\"en\":\"Left-back\",\"vi\":\"Hậu vệ trái\",\"es\":\"Lateral izquierdo\",\"pt\":\"Lateral esquerdo\",\"mya\":\"ဘယ်ဘက်ကွင်းခံ\",\"ru\":\"Левый защитник\",\"th\":\"แบ็คซ้าย\",\"ms\":\"Bek kiri\",\"ad\":\"Bek kiri\",\"ko\":\"왼쪽 풀백\"}"),
    NUM_27("Right-back", "右后卫", "{\"zs\":\"右后卫\",\"zh\":\"右後衛\",\"en\":\"Right-back\",\"vi\":\"Hậu vệ phải\",\"es\":\"Lateral derecho\",\"pt\":\"Lateral direito\",\"mya\":\"ညာဘက်ကွင်းခံ\",\"ru\":\"Правый защитник\",\"th\":\"แบ็คขวา\",\"ms\":\"Bek kanan\",\"ad\":\"Bek kanan\",\"ko\":\"오른쪽 풀백\"}"),
    NUM_28("Left winger", "左边锋", "{\"zs\":\"左边锋\",\"zh\":\"左邊鋒\",\"en\":\"Left winger\",\"vi\":\"Tiền vệ trái\",\"es\":\"Extremo izquierdo\",\"pt\":\"Ponta esquerda\",\"mya\":\"ဘယ်ဘက်ကျောသူ\",\"ru\":\"Левый вингер\",\"th\":\"ปีกซ้าย\",\"ms\":\"Sayap kiri\",\"ad\":\"Sayap kiri\",\"ko\":\"왼쪽 윙어\"}"),
    NUM_29("Pitcher", "投手", "{\"zs\":\"投手\",\"zh\":\"投手\",\"en\":\"Pitcher\",\"vi\":\"Vận động viên ném bóng\",\"es\":\"Lanzador\",\"pt\":\"Arremessador\",\"mya\":\"ပစ်ခတ်သမား\",\"ru\":\"Питчер\",\"th\":\"นักขว้าง\",\"ms\":\"Pemadam\",\"ad\":\"Pemain lempar\",\"ko\":\"투수\"}"),

    ;

    public String code;
    public String msg;
    public JSONObject names;

    PlayerPositionTypeEnum(String code, String msg, String names) {
        this.code = code;
        this.msg = msg;
        this.names = JSON.parseObject(names);
    }

    /**
     * 根据位置中文名称获取英文名称
     */
    public static String getPositionEnNameByMsg(String msg) {
        msg = convertMsg(msg);
        for (PlayerPositionTypeEnum item : PlayerPositionTypeEnum.values()) {
            if (item.getMsg().equalsIgnoreCase(msg)) {
                return item.getCode();
            }
        }
        return null;
    }


    public static PlayerPositionTypeEnum getItemByMsg(String msg) {
        msg = convertMsg(msg);
        for (PlayerPositionTypeEnum item : PlayerPositionTypeEnum.values()) {
            if (item.getMsg().equalsIgnoreCase(msg)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 数据库历史数据转换为新数据
     * @param msg
     * @return
     */
    public static String convertMsg(String msg) {
        if ("中前锋".equals(msg)) {
            msg = "中锋";
        } else if ("控球前锋".equals(msg)) {
            msg = "前锋";
        }
        return msg;
    }

}
