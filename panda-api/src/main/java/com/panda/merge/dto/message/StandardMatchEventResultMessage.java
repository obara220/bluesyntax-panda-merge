package com.panda.merge.dto.message;

import java.io.Serializable;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * 标准赛果消息
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-11-13
 */
@Data
public class StandardMatchEventResultMessage implements Serializable {

    /**
	 *
	 */
	private static final long serialVersionUID = 4221928529742888751L;

	private Long id;

    /**
     * 运动ID
     */
    private Long sportId;

    /**
     * 是否被取消. 0:未没取消；1:取消;
     */
    private Integer canceled;

    /**
     * 数据源
     */
    private String dataSourceCode;

    private String eventCode;

    /**
     * 事件名称
     */
    private String eventName;

    /**
     * 修改时间
     */
    private Long eventTime;

    /**
     * 额外信息,一般用作事件的子类型
     */
    private String extraInfo;

    /**
     * 扩展字段1
     */
    private String addition1;

    /**
     * 扩展字段2
     */
    private String addition2;

    /**
     * 扩展字段3
     */
    private String addition3;

    /**
     * 扩展字段4
     */
    private String addition4;

    /**
     * 扩展字段5
     */
    private String addition5;

    /**
     * 待自动确认时长(单位:秒).  被阻塞的事件会因为前边阻塞的事件导致待确认时间延长, 此该时间必须保存
     */
    private Integer autoConfirmTime;

    private Integer autoConfirmUsedTime;

    /**
     * 可选值 home,away
     home:主队
     away:客队
     */
    private String homeAway;


    /**
     * 当前事件对应的主队数量
     */
    private Integer eventHomeNumber;

    /**
     * 当前事件对应的客队数量
     */
    private Integer eventAwayNumber;

    /**
     * 当前盘数
     * 每个赛种含义可能不一样，该字段按照网球定义命名
     * 足球：上半场表示1，下半场表示2
     * 篮球：小节数
     * 网球：网球是先有盘，才有局，所以这里网球就是代表 盘数
     * 乒乓球：乒乓球只有局,所以这里代表 局数
     * 板球：板球先有局，后面是轮，所以这里表示 局数
     * */
    private Integer firstNumber;

    /** 主队盘比分（eventHomeNumber 直属下级的比分）*/
    private Integer homeFirstNumber;

    /** 客队盘比分（eventAwayNumber 直属下级的比分）*/
    private Integer awayFirstNumber;

    /**
     * 当前局数
     * 每个赛种含义可能不一样，该字段按照网球定义命名
     * 足球：无
     * 篮球：无
     * 网球：网球是先有盘，才有局，所以这里网球就是代表 局数
     * 乒乓球：乒乓球只有局,所以这里不需要赋值
     * 板球：板球先有局，后面是轮，所以这里表示 轮数
     * */
    private Integer secondNumber;

    /** 主队局比分（homeFirstNumber 直属下级的比分）*/
    private Integer homeSecondNumber;

    /** 客队局比分（awayFirstNumber 直属下级的比分）*/
    private Integer awaySecondNumber;



    /**
     * 比较阶段value，
     对应字典：t.parent_type_id = 8 AND t.addition1 = 体育类型;
     如足球对应字典：t.parent_type_id = 8 AND t.addition1 = 1;
     */
    private Long matchPeriodId;

    /**
     * 球员1的id
     */
    private Long player1Id;

    /**
     * 球员1的名称
     */
    private String player1Name;

    /**
     * 球员2的id
     */
    private Long player2Id;

    /**
     * 球员2的名称
     */
    private String player2Name;

    /**
     * 距离比赛开始多少秒
     */
    private Integer secondsFromStart;

    /**
     * 标准赛事的id. 对应 standard_match_info.id
     */
    private Long standardMatchId;

    /**
     * 标准球队 ID. 对应 standard_sport_team.id
     */
    private Long standardTeamId;

    private String teamNameZs;


    /**
     * 第三方数据源提供的该事件id.
     */
    private String thirdEventId;

    /**
     * 第三方赛事的id. 对应third_match_info.id
     */
    private Long thirdMatchId;

    /**
     * 比赛在数据源中的ID
     */
    private String thirdMatchSourceId;

    /**
     * 第三方球队id. 对应 third_sport_team.id
     */
    private Long thirdTeamId;

    /**
     * 赛果事件状态：
     0：待确认
     1： 待自动确认
     2:   已确认
     3：已暂停
     4:   无效
     */
    private Integer status;

    /**
     * 操作类型：
     0：创建
     1：启动自动确认
     2：自动确认
     3：手动确认
     4：赛果编辑
     5：赛果修正
     6：赛果忽略
     7：数据源移除
     8：事件取消
     9：确认暂停
     */
    private Integer operateType;

    /**
     * 操作人
     */
    private String operater;
    /**
     * 手动确认次数
     */
    private Integer confirmOperationTimes;

    private Long autoConfirmStartTimestamp;

    /**
     * 预警类型：
     0：无
     1:  已确认赛果变更预警
     2：阻塞预警
     */
    private Integer alertType;

    /**
     * 数据来源类型：
     0 : UOF
     1:  Scoring Feed
     */
    private Integer sourceType;

    /**
     * 当前事件权重
     */
    private Integer eventWeight;

    /**
     * 商业数据源,格式如：SR,BC
     */
    private String commercialDatasources;

    /**
     * 竞品数据源，如188,QT
     */
    private String competitionDatasources;

    /**
     * 比分网提供的数据源. 比如:QT
     */
    private String scoreDatasources;

    /**
     * 被删除的数据源
     */
    private String deletedDataSourceCode;

    private String remark;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 修改时间
     */
    private Long modifyTime;

    /**
     * 数据表结构中体现比分类型:分别表示：0：全场比分（默认），1：盘比分，2：局比分
     */
    private Integer scoreType;

    /**
     * 扩展字段6
     */
    private String addition6;

    /**
     * 扩展字段7
     */
    private String addition7;

    /**
     * 扩展字段8
     */
    private String addition8;

    /**
     * 扩展字段9
     */
    private String addition9;
    
    /**
     * 扩展字段10
     */
    private String addition10;
    
    /**
     * 赛事事件审核序号
     */
    private Integer auditOrderNo;
    
    /**
     * 赛事事件模板号
     */
    private Integer templateNo;
    
    /**
     * 是否符合玩法显式审核：0:非显式审核1:玩法赛果显式审核
     */
    private Integer auditType;
    
    /**
     * 模板Id
     */
    private String templateId;
    
    /**
     * 模板格式
     */
    private String templateText;
    
    /**
     * 模板格式EN
     */
    private String templateTextEn;

    /**
     * 赛事事件顺序号
     */
    private Integer eventOrder;
    /**
     * 赛果确认时间
     */
    private Long confirmTime;
    /**
     * 事件可信度等级
     */
    private Integer eventLevel = 0;
    /**
     * 用户ID
     */
    private String userId;

    private String linkId;

    public String getLinkId() {
        if(StringUtils.isNotBlank(linkId)){
            return this.linkId;
        }else{
            return standardMatchId+"_"+thirdMatchSourceId+"_"+thirdEventId;
        }
    }

}
