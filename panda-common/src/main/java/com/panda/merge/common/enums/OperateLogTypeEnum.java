package com.panda.merge.common.enums;

/**
 * @author :  idol
 * @project Name :  panda_data_service
 * @package Name :  com.panda.sports.manager.enums
 * @description :  操作类型枚举
 * @date: 2022-2-8 15:13:13
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum OperateLogTypeEnum {

    type_1(10001, "冻结 ", "Stop"),
    type_2(10002, "取消冻结 ", "Resume"),
    type_3(10003, "设置进球方式和球员 ", "player And Goal Type"),
    type_4(10004, "结算切换", "Switch mode"),
    type_5(10005, "赛事冻结 ", "Match Stop"),
    type_6(10006, "没有进球", "None"),
    type_7(10007, "结算中心", "Settlement Center"),
    type_8(10008, "预开售设置", "Match Settings"),
    type_9(10009, "赛前盘", "Pre-Match"),
    type_10(10010, "滚球盘", "In-Play"),
    type_11(10011, "未开售", "No Offer"),
    type_12(10012, "进球类", "Goal"),
    type_13(10013, "角球类", "Corner"),
    type_14(10014, "罚牌类", "Bookings"),
    type_16(100166, "其他类", "Others"),
    type_17(100167, "篮球类", "basketBall"),
    type_15(10045, "足球报球板", "PD"),
    /**
     *   2 走水   3 输  4 赢 7 取消
     * */

    RESULT_2(2,"走水", "void"),
    RESULT_3(3,"输", "lose"),
    RESULT_4(4,"赢", "win"),
    RESULT_7(7,"取消", "cancel"),

    /**
     * YesNoEnum
     * 1:编辑
     */
    EDIT(10015, "比分编辑", "Edit"),
    /**
     * 确认比分
     */
    CONFIRM_SCORE(10016, "比分确认", "Confirm"),

    /**
     * 比分结算
     */
    SCORE_SETTLE(10017, "比分结算", "Settle"),

    /**
     * 进球方式和球员
     */
    PLAYER_AND_GOAL_TYPE(10003, "设置进球方式和球员", "player And Goal Type"),

    /**
     * 程序重跑
     */
    ROLLBACK_EXECUTE(10018, "程序重跑", "Re-Send"),
    /**
     * 回滚比分
     */
    ROLLBACK_SCORES_SETTLE(10019, "回滚比分", "Rollback"),

    /**
     * 标记确认
     */
    SETTLE_MARK_CONFIRM(10181, "标记已确认", "settle mark confirm"),

    /**
     * 结算2.0
     */
    SETTLE_MODULE_2(10182, "结算2.0", "settle module 2.0"),


    SCORES_SETTLE_10020(10020, "查询成功", "Searching successfully"),
    SCORES_SETTLE_10021(10021, "暂无日志", "No Data"),
    SCORES_SETTLE_10022(10022, "日志查询出错,请联系开发人员", "Error : unable to search, please contact IT Support."),
    SCORES_SETTLE_10023(10023, "该球种暂未开放", "Disable for this sport type"),
    SCORES_SETTLE_10024(10024, "语言类型有误", "Error : language type"),
    SCORES_SETTLE_10025(10025, "参数有误", "Incorrect data"),


    SCORES_SETTLE_10027(10027, "主队", "Home"),
    SCORES_SETTLE_10028(10028, "客队", "Away"),
    SCORES_SETTLE_10029(10029, "没有角球", "No Corner"),
    SCORES_SETTLE_10030(10030, "操作成功", "Successful operation"),
    SCORES_SETTLE_10031(10031, "走水", "Return"),
    SCORES_SETTLE_10032(10032, "玩法类冻结", "Category Stop"),
    SCORES_SETTLE_10033(10033, "赛事开关", "Match switch"),
    SCORES_SETTLE_10034(10034, "系统开关", "System switch"),
    SCORES_SETTLE_10035(10035, "开", "On"),
    SCORES_SETTLE_10036(10036, "关", "Off"),
    SCORES_SETTLE_10037(10037, "冻结 s/ 分钟", "Stop /s minute"),
    SCORES_SETTLE_10038(10038, "比分中心", "Score Center"),
    SCORES_SETTLE_100381(100381, "比分中心设置", "Score Center Setting"),
    SCORES_SETTLE_10039(10039, "异常结算", "Abnormal Settle"),
    SCORES_SETTLE_10040(10040, "点球大战谁先踢球设置", "Penalty shootout who kicks the ball first"),
    SCORES_SETTLE_10041(10041, "点球大战谁先踢球单独结算", "Whoever kicks the ball first will be billed separately"),
    SCORES_SETTLE_10042(10042, "点球大战谁先踢球设置权重", "Who kicks the ball first Set the weight"),
    SCORES_SETTLE_10043(10043, "顺序开关", "Sequence Switch"),
    SCORES_SETTLE_10044(10044, "五分钟开关", "Five Switch"),

    SCORES_SETTLE_100045(100045, "主数据源比分带入", "Main data source score carried over"),
    SCORES_SETTLE_100046(100046, "单节时间限制(s)", "Single period time limit(s)"),
    SCORES_SETTLE_100047(100047, "结算设置 -联赛级设置", "Settlement Settings - League Level Settings"),
    SCORES_SETTLE_100048(100048, "结算设置-结算1.0事件反推赛果权重", "Settlement Settings - Settlement 1.0 Event Reverse Result Weight"),
    SCORES_SETTLE_100049(100049,"即时(赛中)结算","In-play Settlement"),

    //报球板日志
    SCORES_PD_10046(10046, "五分钟比分修改", "Five Min Scores Update"),
    SCORES_PD_10047(10047, "十五分钟比分修改", "Fifteen Min Scores Update"),
    SCORES_PD_10048(10048, "报球板-网球", "Tennis PD"),
    SCORES_PD_10049(10049, "比赛开始", "Match Begin"),
    SCORES_PD_10050(10050, "选择赛制", "Choose Match"),
    SCORES_PD_10051(10051, "第%s盘,第%s局", "SET %s,GAMES %s"),
    SCORES_PD_10052(10052, "比赛结束", "Match Ended"),
    SCORES_PD_10053(10053, "比赛中断", "Match Postponed"),
    SCORES_PD_10054(10054, "比赛推迟", "Match Interrupted"),
    SCORES_PD_10055(10055, "比赛取消", "Match Canceled"),
    SCORES_PD_10056(10056, "比赛恢复", "Match Recover"),
    SCORES_PD_10057(10057, "设置赛制", "Match Round Type"),
    SCORES_PD_10058(10058, "调整局制", "Match Length"),
    SCORES_PD_10059(10059, "录入局内比分", "Add Scores"),
    SCORES_PD_10060(10060, "第%s盘,第%s局开始", "SET %s,GAMES %s Begin"),
    SCORES_PD_10061(10061, "第%s盘,第%s局结束", "SET %s,GAMES %s End"),
    SCORES_PD_10062(10062, "设置盘最大局数", "Set Max Round"),
    SCORES_PD_10063(10063, "重新计算盘比分", "ReCount Set Score"),


    SCORES_PD_10064(10064, "删除事件", "Delete Event"),

    SCORES_PD_100641(100641, "点球重踢", "Retake Pen"),
    SCORES_PD_10065(10065, "点击", "Click"),
    SCORES_PD_10066(10066, "上半场", "1st Half"),
    SCORES_PD_10067(10067, "下半场", "2st Half"),
    SCORES_PD_10068(10068, "加时赛", "ET"),
    SCORES_PD_10069(10069, "点球", "Penalty"),
    SCORES_PD_10070(10070, "可能黄牌", "Possible YC"),
    SCORES_PD_10071(10071, "没有黄牌", "No YC"),
    SCORES_PD_10072(10072, "黄牌确认", "Yellow Card confirmed"),
    SCORES_PD_10073(10073, "可能红牌", "Possible RC"),
    SCORES_PD_10074(10074, "没有红牌", "No Red card"),
    SCORES_PD_10075(10075, "红牌确认", "Red card confirmed"),
    SCORES_PD_10076(10076, "可能角球", "Possible CR"),
    SCORES_PD_10077(10077, "没有角球", "No Corner"),
    SCORES_PD_10078(10078, "角球确认", "Corner confirmed"),
    SCORES_PD_10079(10079, "可能进球", "Possible G"),
    SCORES_PD_10080(10080, "没有进球", "Cancel G"),
    SCORES_PD_10082(10082, "进球确认", "Goal Confirm"),
    SCORES_PD_10083(10083, "VAR", "VAR"),
    SCORES_PD_10084(10084, "进球", "Goal (G)"),
    SCORES_PD_10085(10085, "角球", "Corner (CR)"),
    SCORES_PD_10086(10086, "黄牌", "Yellow card (YC)"),
    SCORES_PD_10087(10087, "红牌", "Red card (RC)"),
    SCORES_PD_10088(10088, "修改赛事时间", "Modify In-Play Time"),
    SCORES_PD_10089(10089, "倒计时 +/-", "Countdown +/-"),
    SCORES_PD_10090(10090, "结算冻结", "Stop Settlement"),
    SCORES_PD_10091(10091, "取消冻结", "Resume Settlement"),
    SCORES_PD_10092(10092, "冻结", "Stop"),
    SCORES_PD_10093(10093, "取消冻结", "Resume"),
    SCORES_PD_10094(10094, "得分", "Point"),

    SCORES_PD_10095(10095, "点球重踢", "Retake Pen"),

    SCORES_PD_100951(100951, "没有重踢", "No Retake Pen"),

    SCORES_PD_10096(10096, "点球进球", "Penalty Goal"),
    SCORES_PD_10097(10097, "赛事阶段", "Match Period"),
    SCORES_PD_100100(100100, "报球板-足球", "PA Live Feed - Soccer"),
    SCORES_PD_100101(100101, "报球板-篮球", "PA Live Feed - Basketball"),
    SCORES_PD_100102(100102, "报球板-冰球", "PA Live Feed - Ice Hockey"),
    SCORES_PD_100105(100105, "报球板-斯诺克", "PA Live Feed - Snooker"),
    SCORES_PD_100129(100129, "报球板-排球", "PA Live Feed - Volleyball"),
    SCORES_PD_100103(100103, "修改开赛时间", "Modify Match Time"),
    SCORES_PD_100104(100104, "修改开赛日期", "Modify Match Date"),
    SCORES_PD_100106(100106, "开球", "Kick-Off"),
    SCORES_PD_100107(100107, "主队开球", "Home KO"),
    SCORES_PD_100120(100120, "开关封锁", "Offer Status"),
    SCORES_PD_100108(100108, "开", "Offer"),
    SCORES_PD_100109(100109, "关", "Close"),
    SCORES_PD_100110(100110, "封", "Suspend"),
    SCORES_PD_100122(100122, "锁", "Lock"),
    SCORES_PD_100111(100111, "客队开球", "Away KO"),
    SCORES_PD_100112(100112, "开始赛事时间", "Match Time Starts"),
    SCORES_PD_100126(100126, "开始", "Start"),
    SCORES_PD_100113(100113, "暂停赛事时间", "Match Time Stops"),
    SCORES_PD_100127(100127, "暂停", "Stop"),
    SCORES_PD_100114(100114, "继续赛事时间", "Match Time Resume"),
    SCORES_PD_100128(100128, "继续", "Resume"),
    SCORES_PD_100115(100115, "结束赛事时间", "Match Time Ends"),
    SCORES_PD_100117(100117, "安全", "Safe"),
    SCORES_PD_100118(100118, "危险", "Danger"),
    SCORES_PD_100121(100121, "修改比分", "Modify Score"),
    SCORES_PD_100123(100123, "第%s盘开始", "SET %s Begin"),
    SCORES_PD_100124(100124, "第%s盘结束", "SET %s End"),
    SCORES_PD_100125(100125, "可能点球", "Possible P"),
    SCORES_PD_100130(100130, "确认点球", "Penalty Confirmed"),
    SCORES_PD_100131(100131, "没有点球", "No PEN"),
    SCORES_PD_100132(100132, "点球未进", "PEN Missed"),
    SCORES_PD_100133(100133, "新增", "Add"),
    SCORES_PD_100134(100134, "赛制时间", "Format Period"),
    SCORES_PD_100135(100135, "点球大战", "PEN Shootout"),
    SCORES_PD_100136(100136, "角球", "Point Corner"),
    SCORES_PD_100137(100137, "大罚", "Major"),
    SCORES_PD_100138(100138, "小罚", "Minor"),
    SCORES_PD_100139(100139, "冻结 - 日期时间", "Stop - Date Time"),
    SCORES_PD_100140(100140, "进球", "Goal"),
    SCORES_PD_100141(100141, "角球", "Corner"),
    SCORES_PD_100142(100142, "黄牌", "Yellow card"),
    SCORES_PD_100143(100143, "红牌", "Red card"),
    SCORES_PD_100144(100144, "数据商开关", "Provider"),
    SCORES_PD_100145(100145, "罚牌", "Penalty card"),
    SCORES_PD_100146(100146, "主队球权", "Home Possession"),
    SCORES_PD_100147(100147, "客队球权", "Away Possession"),
    SCORES_PD_100148(100148, "结算中心 (新)", "Settlement Center (New)"),
    SCORES_PD_100149(100149, "新增竞品链接", "Add Competitor URL"),
    SCORES_PD_100150(100150, "编辑竞品链接", "Edit Competitor URL"),
    SCORES_PD_100151(100151, "删除竞品链接", "Delete Competitor URL"),
    SCORES_PD_100152(100152, "竞品名称", "Competitor"),
    SCORES_PD_100153(100153, "链接", "URL"),
    SCORES_PD_100154(100154, "关闭删除提示", "Delete Alert Off"),
    SCORES_PD_100155(100155, "结算设置 - Critical Period", "Settlement Settings - Critical Period"),
    SCORES_PD_100156(100156, "结算设置 - 数据商权重", "Settlement Settings - Provider weight"),
    SCORES_PD_100157(100157, "添加数据商权重", "Add Provider weight"),
    SCORES_PD_100158(100158, "删除数据商权重", "Delete Provider weight"),
    SCORES_PD_100159(100159, "编辑数据商权重", "Edit Provider weight"),
    SCORES_PD_100171(100171, "数据商心跳", "Data Source Heartbeat"),
    SCORES_PD_100172(100172, "15分钟/5分钟单数据源结算", "15-minute/5-minute Single Data Source Settlement"),
    SCORES_PD_100170(100170, "Edit Event Limit", "Edit Event Limit"),
    SCORES_PD_100160(100160, "Edit Critical Feed", "Edit Critical Feed"),
    SCORES_PD_100161(100161, "结算参数设置", "Settlement Template setting"),
    SCORES_PD_100162(100162, "新增模版", "Add Template"),
    SCORES_PD_100163(100163, "修改模版", "Edit Template"),
    SCORES_PD_100164(100164, "删除模版", "Delete Template"),
    SCORES_PD_100169(100169, "批量参数设置", "Batch Edit Templates"),
    SCORES_PD_100067(100067, "数据商权重模版", "Settlement Weight Templates"),
    SCORES_PD_100068(100068, "灰色区间设置", "Critical Period Template"),
    SCORES_PD_100069(100069, "倒计时模板设置", "Setting Countdown Templates"),
    SCORES_PD_100070(100070, "设定联赛模版", "Template setting"),
    SCORES_PD_100071(100071, "参与结算开关", "PA Live Feed - Settlement Switch"),
    SCORES_PD_100072(100072, "取消比赛结束", "Cancel Match End"),
    SCORES_PD_100073(100073, "比分编辑", "Edit Score"),
    SCORES_PD_100074(100074, "取消结束", "Cancel Match End"),
    SCORES_PD_100075(100075,"界外球","throw in"),
    SCORES_PD_100076(100076,"进攻","attack"),
    SCORES_PD_100077(100077,"球门球","goal kick"),
    SCORES_PD_100078(100078,"任意球","free kick"),
    SCORES_PD_100079(100079,"可能任意球","possible free kick"),
    SCORES_PD_100080(100080,"没有任意球","canceled free kick"),
    SCORES_PD_100081(100081,"任意球确认","free kick confirm"),
    SCORES_PD_100082(100082,"越位","free kick confirm"),
    SCORES_PD_100083(100083,"射正","shot on target"),
    SCORES_PD_100084(100084,"射偏","shot off target"),
    SCORES_PD_100085(100085,"红黄牌","yellow red card"),
    SCORES_PD_100086(100086,"伤停补时","injury stop time"),
    SCORES_PD_100087(100087,"时间状态","match time reset"),
    SCORES_PD_100088(100088,"完整统计","match count"),
    SCORES_PD_100089(100089,"危险进攻","dangerous attack"),
    SCORES_PD_100090(100090,"喝水","water break"),
    SCORES_PD_100091(100091,"可能视频辅助裁判","Possible VAR Check"),
    SCORES_PD_1000911(0,"进球","Goal"),
    SCORES_PD_100092(100092,"视频辅助裁判确认","VAR Comfirm"),
    SCORES_PD_1000921(1,"点球","Penalty kick"),
    SCORES_PD_100093(100093,"视频辅助裁判取消","VAR Check Cancel"),
    SCORES_PD_1000931(2,"红牌","Red card (RC)"),
    SCORES_PD_100500(100500, "点球大战先罚", "Penalty First"),
    SCORES_PD_100501(100501, "可能VAR: 罚牌", "possible_var_red_card"),
    SCORES_PD_100502(100502, "可能VAR: 进球", "possible_var_goal"),
    SCORES_PD_100503(100503, "可能VAR: 点球", "possible_var_penalty"),
    SCORES_PD_100504(100504, "确认VAR：进球", "var_goal"),
    SCORES_PD_100505(100505, "确认VAR：点球", "var_penalty"),
    SCORES_PD_100506(100506, "确认VAR：黄牌", "var_yellow_card"),
    SCORES_PD_100507(100507, "确认VAR：罚牌", "var_red_card"),
    SCORES_PD_100508(100508, "取消VAR: 罚牌", "canceled_var_red_card"),
    SCORES_PD_100509(100509, "取消VAR: 进球", "canceled_var_goal"),
    SCORES_PD_1005010(100510, "取消VAR: 点球", "canceled_var_penalty"),
    /**
     * 篮球报球板2.0新增日志
     */
    SCORES_PD_2001(2001,"助攻","assist"),
    SCORES_PD_2002(2002,"失误","turnover"),
    SCORES_PD_2003(2003,"抢断","steal"),
    SCORES_PD_2004(2004,"盖帽","block"),
    SCORES_PD_2005(2005,"犯规","foul"),
    SCORES_PD_2006(2006,"进攻篮板","rebound attack"),
    SCORES_PD_2007(2007,"防守篮板","rebound defence"),
    SCORES_PD_2008(2008,"控球权","possession"),

    SCORES_PD_203111(203111,"1分罚球初始化","1-point free throw initialize"),
    SCORES_PD_203112(203112,"2分罚球初始化","2-point free throw initialize"),
    SCORES_PD_203113(203113,"3分罚球初始化","3-point free throw initialize"),

    SCORES_PD_203114(203114,"1分罚球取消","1-point play canceled"),
    SCORES_PD_203115(203115,"2分罚球取消","2-point play canceled"),
    SCORES_PD_203116(203116,"3分罚球取消","3-point play canceled"),

    SCORES_PD_20312(20312,"罚球增加","increase free throw"),

    SCORES_PD_20313(20313,"罚球减少","decrease free throw"),
    SCORES_PD_2031(2031,"1分罚球未命中","one-point free throw missed"),
    SCORES_PD_2032(2032,"1分罚球命中","one-point free throw"),

    SCORES_PD_203201(203201,"输入框罚球命中","free throw input box"),
    SCORES_PD_203102(203102,"2分罚球未命中","2-point free throw missed"),
    SCORES_PD_203202(203202,"2分罚球命中","2-point free throw"),
    SCORES_PD_203103(203103,"3分罚球未命中","3-point free throw missed"),
    SCORES_PD_203203(203203,"3分罚球命中","3-point free throw"),
    SCORES_PD_203301(203301,"1分罚球删除","1-point free throw delete"),
    SCORES_PD_20330101(20330101,"输入框罚球删除","free throw delete input box"),
    SCORES_PD_2033011(2033011,"1分罚球编辑","1-point free throw edit"),
    SCORES_PD_2033012(2033012,"输入框罚球编辑","free throw edit input box"),
    SCORES_PD_203302(203302,"2分球删除","2-point delete"),
    SCORES_PD_2033021(2033021,"2分球编辑","2-point edit"),
    SCORES_PD_203303(203303,"3分球删除","3-point delete"),
    SCORES_PD_2033031(2033031,"3分球编辑","3-point edit"),
    SCORES_PD_2033(2033,"1分罚球取消","one-point free throw cancel"),
    SCORES_PD_2034(2034,"2分投篮未命中","2-point shot missed"),
    SCORES_PD_2035(2035,"2分投篮命中","2-point shot"),
    SCORES_PD_2036(2036,"2分投篮取消","2-point shot cancel"),
    SCORES_PD_2037(2037,"3分投篮未命中","3-point shot missed"),
    SCORES_PD_2038(2038,"3分投篮命中","3-point shot"),
    SCORES_PD_2039(2039,"3分投篮取消","3-point shot cancel"),
    SCORES_PD_2040(2040,"跳球开始","jump ball start"),
    SCORES_PD_2041(2041,"其它开始","other start"),
    SCORES_PD_2013(2013,"第一节","first quarter"),
    SCORES_PD_201306(201306,"第一节后6分钟","6-min after first quarter"),
    SCORES_PD_201312(201312,"第一节前6分钟","6-min before first quarter"),
    SCORES_PD_2014(2014,"第二节","second quarter"),
    SCORES_PD_201406(201406,"第二节后6分钟","6-min after second quarter"),
    SCORES_PD_201412(201412,"第二节前6分钟","6-min before second quarter"),
    SCORES_PD_2015(2015,"第三节","third quarter"),
    SCORES_PD_201506(201506,"第三节后6分钟","6-min after third quarter"),
    SCORES_PD_201512(201512,"第三节前6分钟","6-min before third quarter"),
    SCORES_PD_2016(2016,"第四节","fourth quarter"),
    SCORES_PD_201606(201606,"第四节后6分钟","6-min after fourth quarter"),
    SCORES_PD_201612(201612,"第四节前6分钟","6-min before fourth quarter"),

    SPORT_RESULT_SHOW_STATUS(100094,"比分中心显示隐藏","scores center show status"),
    SCORES_CENTER_SETTLE(100095,"比分中心结算","scores center settle"),
    SCORES_CENTER_MATCH_STATUS(100096,"赛果显示","match result show"),
    SCORES_CENTER_MANUAL_SCORE(100961,"手动输入比分","manual input score"),
    SCORES_CENTER_MODIFY_DATASOURCE(100962,"修改主数据源","modify main datasource"),
    SCORES_CENTER_MODIFY_MODIFY_STATUS(100963,"修改赛果显示","modify match result show"),
    DISMISS_SETTLEMENT_ALERT_INDICATOR(100200,"催结算消红","Prompt Settlement to Clear Red Flags"),
    SCORES_CANCEL_WITH_ONE_CLICK(100300,"一键取消","One-click cancel"),
    SCORES_CENTER_MAIN_DATASOURCE(100964,"主数据源","main datasource"),
    SCORES_CENTER_OPEN(10000001,"开","ON"),
    SCORES_CENTER_CLOSE(10000000,"关","OFF"),
    SCORES_CENTER_MATCH_SETTING(100097,"赛果显示设置","match result show setting"),
    SCORES_CENTER_DEFAULT(100098,"默认值","default"),
    SCORES_CENTER_DEFAULT_SETTING(100099,"默认值设置","default setting"),

    SCORES_CENTER_SETTLE_CONV(100201,"常规比分下发","Match Scores Settle"),
    SCORES_CENTER_SETTLE_BREAK(100202,"比赛中断比分下发","Match Interruption Settle"),
    MINUTES_SCORES_CHECK_SWITCH(100203,"区间比分校验开关","Minutes scores check switch"),


    /**
     * 五分钟比分(操作日志使用)
     */
    GOAL_5minute_5(5, "0:00 - 4:59", "0:00 - 4:59"),
    GOAL_5minute_10(10, "5:00 - 9:59", "5:00 - 9:59"),
    GOAL_5minute_15(15, "10:00 - 14:59", "10:00 - 14:59"),
    GOAL_5minute_20(20, "15:00 - 19:59", "15:00 - 19:59"),
    GOAL_5minute_25(25, "20:00 - 24:59", "20:00 - 24:59"),
    GOAL_5minute_30(30, "25:00 - 29:59", "25:00 - 29:59"),
    GOAL_5minute_35(35, "30:00 - 34:59", "30:00 - 34:59"),
    GOAL_5minute_40(40, "35:00 - 39:59", "35:00 - 39:59"),
    GOAL_5minute_45(45, "40:00 - 45:00 (不含补时)", "40:00 - 45:00 (excluded injury time)"),
    GOAL_5minute_49(49, "上半场 绝杀球", "1H Last-minute Goal (Injury Time)"),

    GOAL_5minute_50(50, "2H - 49:59", "下半场 - 49:59"),
    GOAL_5minute_55(55, "50:00 - 54:59", "50:00 - 54:59"),
    GOAL_5minute_60(60, "55:00 - 59:59", "55:00 - 59:59"),
    GOAL_5minute_65(65, "60:00 - 64:59", "60:00 - 64:59"),
    GOAL_5minute_70(70, "65:00 - 69:59", "65:00 - 69:59"),
    GOAL_5minute_75(75, "70:00 - 74:59", "70:00 - 74:59"),
    GOAL_5minute_80(80, "75:00 - 79:59", "75:00 - 79:59"),
    GOAL_5minute_85(85, "80:00 - 84:59", "80:00 - 84:59"),
    GOAL_5minute_90(90, "85:00 - 90:00 (excluded injury time)", "85:00 - 90:00 (不含补时)"),
    GOAL_5minute_99(99, "下半场 绝杀球", "2H Last-minute Goal (Injury Time)"),
    GOAL_5minute_none(0, "无进球", "No Goal"),

    ONE_FIFTEENMIN(60899, "0:00-14:59","0:00-14:59"),
    TWO_FIFTEENMIN(61799, "15:00-29:59","15:00-29:59"),
    THREE_FIFTEENMIN(62699, "30:00-1HT","30:00-1HT"),
    FOUR_FIFTEENMIN(73599, "45:00-59:59","45:00-59:59"),
    FIVE_FIFTEENMIN(74499, "60:00-74:59","60:00-74:59"),
    SIX_FIFTEENMIN(75399, "75:00-FT","75:00-FT"),


    //二次结算的结算原因(不开更改)
    SCORES_SETTLE_10026(10026, "结算原因", "Reason"),
    REASON_101(101, "比赛取消", "Match cancelled"),
    REASON_102(102, "比赛延期", "Match postponed"),
    REASON_120(120, "比赛延迟", "Match delayed"),
    REASON_103(103, "比赛中断", "Match interrupted"),
    REASON_104(104, "比赛重赛", "Rematch"),
    REASON_105(105, "比赛腰斩", "Match cancellation"),
    REASON_106(106, "比赛放弃", "Match abandoned"),
    REASON_107(107, "盘口错误", "Incorrect market"),
    REASON_108(108, "赔率错误", "Incorrect odds"),
    REASON_109(109, "队伍错误", "Incorrect team"),
    REASON_110(110, "联赛错误", "Incorrect league"),
    REASON_111(111, "比分错误", "Incorrect score"),
    REASON_112(112, "电视裁判", "VAR"),
    REASON_113(113, "主客场错误", "Incorrect ground"),
    REASON_114(114, "赛制错误", "Incorrect match format"),
    REASON_115(115, "赛程错误", "Incorrect schedule"),
    REASON_116(116, "事件错误", "Incorrect event"),
    REASON_117(117, "赛事提前", "Schedule in advance"),
    REASON_119(119, "数据源取消", "Odds feed cancelled"),
    REASON_121(121, "操盘手取消", "Trader cancelled"),
    REASON_122(122, "主动弃赛", "Walkover"),
    REASON_123(123, "并列获胜", "Dead Heat"),
    REASON_124(124, "中途弃赛", "Abandoned at game"),
    REASON_125(125, "统计错误", "Incorrect stats"),
    REASON_126(126, "官网比分修改", "Official score changed"),
    REASON_127(127, "赛果错误", "Result Error"),

    REASON_118(118, "其他", "Others"),

    //比分中心编辑提示
    EDIT_TIPS_MSG_01(200101, "正规赛比分加总不相同，不可编辑加时比分", "Can't edit OT/ET data when regular time wasn't tied"),
    EDIT_TIPS_MSG_02(200102, "羽毛球单局不可编辑超30分", "Can't edit over 30"),
    EDIT_TIPS_MSG_03(200103, "比分大于局点分，编辑时双方分差不可大于2分", "Can't not edit point difference more than 2 when one side point more than set point"),
    EDIT_TIPS_MSG_04(200104, "红牌总数超过限制", "Total of Red card was out of limit"),
    EDIT_TIPS_MSG_05(200105, "黄牌总数超过限制", "Total of Yellow card was out of limit"),
    EDIT_TIPS_MSG_06(200106, "加时不相同，不可编辑点球大战比分", "Can't edit PK data when ET/OT data wasn't tied"),
    EDIT_TIPS_MSG_07(200107, "全场比分不可小于半场比分", "Can't edit HT data more than Result Aggr"),
    EDIT_TIPS_MSG_08(200108, "冰球加时采黄金进球制，故不能维护{1,0},{0,1},{0,0}以外之比分", "Ice Hockey was golden goal so can't edit other than {1,0},{0,1},{0,0}"),
    EDIT_TIPS_MSG_09(200109, "编辑时，需双方皆有比数", "Can't just edit one side data in one column"),
    EDIT_TIPS_MSG_10(200110, "网球一方为7分时，另一方不得为5、6以外比分", "Just can edit {7:6},{7:5} when one side is 7"),
    EDIT_TIPS_MSG_11(200111, "点球大战比分不可相差2分及以上", "In a penalty shootout, the score difference cannot be 2 or more goals."),
    EDIT_TIPS_MSG_12(200112, "篮球2节制赛事不能存在小节比分", "There cannot be sub-quarter scores in a 2-quarter basketball match"),
    EDIT_TIPS_MSG_13(200113, "比分未到决胜分数，请核对后重新输入", "The score has not reached the winning point. Please verify and re-enter."),
    EDIT_TIPS_MSG_14(200114, "羽毛球单局比分不能超过30分", "The score of a single badminton game cannot exceed 30 points"),
    EDIT_TIPS_MSG_15(200115, "总分不可相同，请确认后再次输入", "The total score cannot be the same, please confirm and enter again"),
    EDIT_TIPS_MSG_16(200116, "未开始阶段不可编辑比分", "Score cannot be edited during the pre-match phase"),
    EDIT_TIPS_MSG_17(200117, "赛事结束,点球大战比分不能相等", "In a penalty shootout, the score difference cannot be 2 or more goals."),
    EDIT_TIPS_MSG_18(200118, "编辑时，区间比分和常规比分需一致", "When editing, the interval score and the regular score must be consistent"),

    SCORES_CENTER_SWITCH_EDIT(100965,"与主数据源联动","Follow data source"),
    //4053二次结算原因
    SETTLE_REASON_80(9080,"官网错误","Official Error"),
    SETTLE_REASON_81(9081,"数据商错误","Source Error"),
    SETTLE_REASON_82(9082,"系统问题","System Error"),
    SETTLE_REASON_83(9083,"人为错误","Human Error"),
    SETTLE_REASON_84(9084,"赛果不变","Resettle Same score"),
    //4268斯诺克报球板
    SCORE_CHANGE(426801, "变更比分", "SCORE CHANGE"),
    ;

    private Integer code;

    private String value;

    private String name;

    OperateLogTypeEnum(Integer code, String name, String value) {
        this.code = code;
        this.value = value;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }


    public static OperateLogTypeEnum getEnum(String code) {
        for (OperateLogTypeEnum operateLogTypeEnum : OperateLogTypeEnum.values()) {
            if (operateLogTypeEnum.getCode().toString().equals(code)) {
                return operateLogTypeEnum;
            }
        }
        return null;
    }

    public static String getEnumByZs(String code) {
        for (OperateLogTypeEnum operateLogTypeEnum : OperateLogTypeEnum.values()) {
            if (operateLogTypeEnum.getCode().toString().equals(code)) {
                return operateLogTypeEnum.getName();
            }
        }
        return code;
    }

    public static String getEnumByEn(String code) {
        for (OperateLogTypeEnum operateLogTypeEnum : OperateLogTypeEnum.values()) {
            if (operateLogTypeEnum.getCode().toString().equals(code)) {
                String val = operateLogTypeEnum.getValue();
                return val != null ? val : code;
            }
        }
        return code;
    }

    public static String getCodeByValue(String lang, String name) {

        String result = name;
        if (name == null) return result;
        for (OperateLogTypeEnum operateLogTypeEnum : OperateLogTypeEnum.values()) {
            switch (lang) {
                case "cn":
                    if (operateLogTypeEnum.getName() != null && operateLogTypeEnum.getName().equals(name)) {
                        return operateLogTypeEnum.getCode().toString();
                    }
                    break;
                case "en":
                    if (operateLogTypeEnum.getValue() != null && operateLogTypeEnum.getValue().equals(name)) {
                        return operateLogTypeEnum.getCode().toString();
                    }
                    break;
            }
        }
        return result;
    }


    public static OperateLogTypeEnum getEnumByValue(String value) {
        for (OperateLogTypeEnum operateLogTypeEnum : OperateLogTypeEnum.values()) {
            if (operateLogTypeEnum.getValue().equals(value)) {
                return operateLogTypeEnum;
            }
        }
        return REASON_118;
    }
}
