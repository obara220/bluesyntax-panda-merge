package com.panda.merge.constant;


import com.panda.merge.common.enums.BasketBallSettleNumEnum;
import com.panda.merge.dto.SettleQueryDTO;

import java.util.LinkedList;
import java.util.List;


public class MatchSettleCheckConstant {
    /**
     * 核对数据类型1数据商2用户输入
     * */
    public static class CheckDataType{
        /**
         *
         * 数据商 1
         * */
        public static Integer DATA_SOURCE=1;
        /**
         *
         * 用户输入 2
         * */
        public static Integer USER_EDIT=2;
    }
    /**
     * 1.阶段比分 2次序事件
     * */
    public static class CheckType{
        /**
         *
         * 1 阶段比分
         * */
        public static Integer PERIOD_SCORE=1;
        /**
         *
         * 2次序事件
         * */
        public static Integer EVENT_SCORE=2;
    }
    public static class GoWaterStatus{
        /**
         * 走水
         * */
        public static Integer GO_WATER=1;
        /**
         * 不走水
         * */
        public static Integer NOT_GO_WATER=0;
    }
    /**
     * 0未编辑1已编辑2已确认待核对3已确认核对成功4已确认核对失败
     * */
    public static class CheckStatus{

        public static Integer NOT_EDIT=0;

        public static Integer EDIT=1;

        public static Integer CONFIRM=2;
        /**
         * 过期作废
         * */
        public static Integer TIME_OVER=3;
    }

    /**
     * 是否灰色区间： 1 是 0 不是
     */
    public static class IsGrey{
        public static Integer IS_NOT_GREY = 0;

        public static Integer IS_GREY = 1;
    }

    public static class GoalConfirmEventCode{
        public static String SR="play_resumes_after_goal";
        public static String RB="kick_off";
        public static String BG="kick_off";
        public static String PA="kick_off";
        public static String KO="kick_off";
        public static String F01="kick_off";
        public static String N01="kick_off";
        public static String LS="kick_off";
    }

    public static class CornerConfirmEventCode{
        public static String BG="corner_taken";
    }

    public static class GoalStatus{
        public static Integer NOT_CONFIRM=0;
        public static Integer CONFIRM=1;
    }

    public static class HasDeleteEvent{
        public static Integer YES=1;
        public static Integer NO=0;
    }
    public static class HasSettleFreeze{
        public static Integer YES=1;
        public static Integer NO=0;
    }

    /**
     * 获取篮球结算编码
     *
     * @param settleQueryDTO
     * @return
     */
    public static List<String> getBasketBallSettleNumEnumList(SettleQueryDTO settleQueryDTO) {

        if (settleQueryDTO.getPlayCategoryNum() != null && settleQueryDTO.getPlayCategoryNum() > 0) {
            settleQueryDTO.setLevelNum(2);
        } else {
            settleQueryDTO.setLevelNum(3);
        }
        List<String> settleNumList = new LinkedList<>();
        switch (settleQueryDTO.getSettleNum()) {
            case "100":
                settleQueryDTO.setLevelNum(2);
                settleNumList.add(BasketBallSettleNumEnum.BK_Q101.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_Q102.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_Q103.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_Q104.getCode());
                break;
            case "200":
                settleQueryDTO.setLevelNum(2);
                settleNumList.add(BasketBallSettleNumEnum.BK_Q201.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_Q202.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_Q203.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_Q204.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_1HT.getCode());
                break;
            case "300":
                settleQueryDTO.setLevelNum(2);
                settleNumList.add(BasketBallSettleNumEnum.BK_Q301.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_Q302.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_Q303.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_Q304.getCode());
                break;
            case "400":
                settleQueryDTO.setLevelNum(2);
                settleNumList.add(BasketBallSettleNumEnum.BK_Q401.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_Q402.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_Q403.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_Q404.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_Q404.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_FT_RG.getCode());
                break;
            case "end":
                settleQueryDTO.setLevelNum(2);
                settleNumList.add(BasketBallSettleNumEnum.BK_ET.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_2HT.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_FT_ET.getCode());
                break;
            case "s001":
                settleQueryDTO.setLevelNum(2);
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_10.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_20.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_30.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_40.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_50.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_60.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_70.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_80.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_90.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_100.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_110.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_120.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_130.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_140.getCode());
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_150.getCode());
                break;
            case "q101":
                settleNumList.add(BasketBallSettleNumEnum.BK_Q101.getCode());
                break;
            case "q102":
                settleNumList.add(BasketBallSettleNumEnum.BK_Q102.getCode());
                break;
            case "q103":
                settleNumList.add(BasketBallSettleNumEnum.BK_Q103.getCode());
                break;
            case "q104":
                settleNumList.add(BasketBallSettleNumEnum.BK_Q104.getCode());
                break;
            case "q201":
                settleNumList.add(BasketBallSettleNumEnum.BK_Q201.getCode());
                break;
            case "q202":
                settleNumList.add(BasketBallSettleNumEnum.BK_Q202.getCode());
                break;
            case "q203":
                settleNumList.add(BasketBallSettleNumEnum.BK_Q203.getCode());
                break;
            case "q204":
                settleNumList.add(BasketBallSettleNumEnum.BK_Q204.getCode());
                break;
            case "1ht": //上半场
                settleNumList.add(BasketBallSettleNumEnum.BK_1HT.getCode());
                break;
            case "q301":
                settleNumList.add(BasketBallSettleNumEnum.BK_Q301.getCode());
                break;
            case "q302":
                settleNumList.add(BasketBallSettleNumEnum.BK_Q302.getCode());
                break;
            case "q303":
                settleNumList.add(BasketBallSettleNumEnum.BK_Q303.getCode());
                break;
            case "q304":
                settleNumList.add(BasketBallSettleNumEnum.BK_Q304.getCode());
                break;
            case "q401":
                settleNumList.add(BasketBallSettleNumEnum.BK_Q401.getCode());
                break;
            case "q402":
                settleNumList.add(BasketBallSettleNumEnum.BK_Q402.getCode());
                break;
            case "q403":
                settleNumList.add(BasketBallSettleNumEnum.BK_Q403.getCode());
                break;
            case "q404":
                settleNumList.add(BasketBallSettleNumEnum.BK_Q404.getCode());
                break;
            case "2ht":
                settleNumList.add(BasketBallSettleNumEnum.BK_2HT.getCode());
                break;
            case "et":
                settleNumList.add(BasketBallSettleNumEnum.BK_FT_ET.getCode());
                break;
            case "rg":
                settleNumList.add(BasketBallSettleNumEnum.BK_FT_RG.getCode());
                break;
            case "s10":
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_10.getCode());
                break;
            case "s20":
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_20.getCode());
                break;
            case "s30":
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_30.getCode());
                break;
            case "s40":
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_40.getCode());
                break;
            case "s50":
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_50.getCode());
                break;
            case "s60":
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_60.getCode());
                break;
            case "s70":
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_70.getCode());
                break;
            case "s80":
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_80.getCode());
                break;
            case "s90":
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_90.getCode());
                break;
            case "s100":
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_100.getCode());
                break;
            case "s110":
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_110.getCode());
                break;
            case "s120":
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_120.getCode());
                break;
            case "s130":
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_130.getCode());
                break;
            case "s140":
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_140.getCode());
                break;
            case "s150":
                settleNumList.add(BasketBallSettleNumEnum.BK_1ST_150.getCode());
                break;
            case "point":
                settleNumList.add(BasketBallSettleNumEnum.BK_POINT.getCode());
                break;
            case "3pt":
                settleNumList.add(BasketBallSettleNumEnum.BK_3PT.getCode());
                break;
            case "ast":
                settleNumList.add(BasketBallSettleNumEnum.BK_AST.getCode());
                break;
            case "rbd":
                settleNumList.add(BasketBallSettleNumEnum.BK_RBD.getCode());
                break;
            case "bk_q1041":
                settleNumList.add(BasketBallSettleNumEnum.BK_Q1041.getCode());
                break;
            case "bk_q1042":
                settleNumList.add(BasketBallSettleNumEnum.BK_Q1042.getCode());
                break;
            case "bk_q2041":
                settleNumList.add(BasketBallSettleNumEnum.BK_Q2041.getCode());
                break;
            case "bk_q2042":
                settleNumList.add(BasketBallSettleNumEnum.BK_Q2042.getCode());
                break;
            case "bk_q3041":
                settleNumList.add(BasketBallSettleNumEnum.BK_Q3041.getCode());
                break;
            case "bk_q3042":
                settleNumList.add(BasketBallSettleNumEnum.BK_Q3042.getCode());
                break;
            case "bk_q4041":
                settleNumList.add(BasketBallSettleNumEnum.BK_Q4041.getCode());
                break;
            case "bk_q4042":
                settleNumList.add(BasketBallSettleNumEnum.BK_Q4042.getCode());
                break;
            case "bk_2htet":
                settleNumList.add(BasketBallSettleNumEnum.BK_2HT_OT.getCode());
                break;
            case "bk_et":
                settleNumList.add(BasketBallSettleNumEnum.BK_ET.getCode());
                break;
            case "bk_401":
                settleNumList.add(BasketBallSettleNumEnum.BK_401.getCode());
                break;
            case "bk_403":
                settleNumList.add(BasketBallSettleNumEnum.BK_403.getCode());
                break;
            default:
                break;
        }
        return settleNumList;
    }


    public static String getPeriodBySettleNum(String settleNum,Integer matchLength) {

        String period = null;
        BasketBallSettleNumEnum anEnum = BasketBallSettleNumEnum.getEnum(settleNum);
        if (anEnum == null) {
            return period;
        }
        switch (anEnum) {
                case BK_1ST_10:
                case BK_1ST_20:
                case BK_1ST_30:
                case BK_1ST_40:
                case BK_1ST_50:
                case BK_1ST_60:
                case BK_1ST_70:
                case BK_1ST_80:
                case BK_1ST_90:
                case BK_1ST_100:
                case BK_1ST_110:
                case BK_1ST_120:
                case BK_1ST_130:
                case BK_1ST_140:
                case BK_1ST_150:
                    period = BasketBallSettleNumEnum.BK_WHO_XX0.getCode();
                    break;
                default:
                    period = BasketBallSettleNumEnum.BK_IN_ALL.getCode();
                    break;
            }

        return period;
    }


}
