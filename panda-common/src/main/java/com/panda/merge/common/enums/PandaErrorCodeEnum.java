package com.panda.merge.common.enums;

import lombok.extern.slf4j.Slf4j;

/**
 * 错误码通用枚举类
 * @author   tell
 * @since    2019-09-02 17:02
 */
@Slf4j
public enum PandaErrorCodeEnum {
    /** 公共错误码开始 编号从1000开始 **/
    REQUEST_NO_LINKID(1000,"参数linkId不能为空，请检查!"),

    REQUEST_REPEAT_LINKID(1001,"参数linkID重复!"),

    REQUEST_NO_DATA(1002,"参数Data不能为空，请检查数据是否正确！"),

    REQUEST_DATA_LIMIT(1003,"参数Data数据超过[maxSize]条，拒绝处理!"),

    DATASOURCE_IS_NOTNULL(1004,"数据来源[dataSourceCode]不能为空，请检查!"),

    DATASOURCE_LIMIT(1005,"数据来源超过[maxSize]条，拒绝处理!"),

    DATASOURCE_NO_CHECK(1006,"数据来源[dataSourceCode]未验证，请联系管理员！"),

    SPORT_ID_ILLEGAL(1007, "运动种类[sportId]非法，请检查!"),

    SPORT_ID_IS_NOTNULL(1008, "运动种类[sportId]不能为空，请检查!"),

    TOURNAMENT_ID_IS_NOTNULL(1009, "联赛[thirdTournamentSourceId]不能为空，请检查！"),

    TOURNAMENT_NAME_IS_NOTNULL(1010, "联赛[name]不能为空，请检查！"),

    REGION_ID_IS_NOTNULL(1011, "运动区域ID不能为空，请检查!"),

    REGION_NAME_IS_NOTNULL(1012, "运动区域名称不能为空，请检查!"),

    REGION_INVALID(1013, "运动区域无效，请检查!"),

    I18NS_IS_NOTNULL(1014,"国际化列表不能为空，请检查!"),

    I18N_EN(1015,"国际化数据中必须包含英语语种，请检查!"),

    I18N_LANGUAGE_TYPE(1016,"语言类型[languageType]不能为空，请检查!"),

    I18N_TEXT(1017,"文字内容[text]不能为空，请检查!"),

    SYSTEMO_EXCEPTION(9999,"系统异常!"),
    ;

    private Integer errorCode;

    private String errorMsg;

    PandaErrorCodeEnum(Integer errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

}
