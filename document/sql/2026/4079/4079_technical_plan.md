# 4079 乒乓球报球板 — 后端技术方案

> 严格基于 Confluence 需求文档（页面 ID 140845695，版本 22）和现有代码分析，无额外设计。

---

## 一、现状评估

### 1.1 已存在但能力不完整

| 文件 | 状态 | 问题 |
|------|------|------|
| `TableTennisEventConverter` | ✅ 已实现 | 仅统计 `table_tennis_score_change`/`yellow_card`/`red_card`，缺少发球、加速模式、暂停等事件 |
| `TableTennisStatisticsDto` | ✅ 已实现 | 缺少发球、暂停相关统计字段 |
| `TableTennisEventStatisticsEnum` | ✅ 已实现 | 只有 SCORE_CHANGE/YELLOW_CARD/RED_CARD/BLACK_CARD，缺少新事件 |
| `TableTennisMatchResultServiceImpl` | ✅ 已实现 | 赛果转换和总局比分计算基本完整 |
| `TableTennisCheckImpl` | ✅ 已实现 | 六字段比分校验（eventHome/Away + homeFirst/Away + homeSecond/Away） |
| `TableTennisMatchEventServiceImpl` | ⚠️ **方法体为空** | `onReceiveMatchEventInfo` 仅调 `super`，**未处理** `suspension`/`suspension_over`/`timeout`/`timeout_over` |
| `SportPeriodEnum.TABLE_TENNIS` | ✅ 已存在 | 阶段：8,301,9,302,10,303,11,304,12,305,441,306,442,307,100 |
| `MatchResultStatusConfirmEnum.TABLE_TENNIS` | ✅ 已存在 | 阶段：301~307,100,999 |

### 1.2 缺失的关键能力（对照需求文档）

根据需求文档「五、事件消息整理」，以下事件编码在现有代码中**完全没有注册**：

| 事件编码 | 需求来源 | 影响模块 |
|---------|---------|---------|
| `suspension` | match_status id:80，比赛中断，IMP 事件 | MQ 消费 → 标准赛事 `whetherStop` 字段 |
| `suspension_over` | match_status id:局id，比赛重开，IMP 事件 | 同上 |
| `timeout` | 比赛暂停，停止计时 | 同上 |
| `timeout_over` | 比赛继续，开始计时 | 同上 |
| `which_team_serves_first` | 先发球，IMP 事件 | 事件统计（发球方） |
| `current_serve_tabletennis` | 发球，IMP 事件 | 事件统计（发球方） |
| `re_serve` | 重新发球，IMP 事件 | 事件统计（发球方） |
| `expedite_mode` | 加速模式，IMP 事件 | 事件统计 |
| `yellowred_card_same_hand` | 红黄牌同手，IMP 事件 | 事件统计 + 赛果（比赛结束） |
| `delete_event` | 删除事件（红牌/黄牌/赢分） | `preDealMatchEventCommon` 已处理，无需额外代码 |

---

## 二、改动清单

### 改动 1：扩展 `TableTennisEventStatisticsEnum`

**文件：** `data-manager-common/src/main/java/com/panda/sport/manager/enums/matchevent/TableTennisEventStatisticsEnum.java`

**变更内容：** 在现有枚举值后新增 7 个事件枚举项

```java
public enum TableTennisEventStatisticsEnum {

    SCORE_CHANGE("table_tennis_score_change","比分"),
    YELLOW_CARD("yellow_card","黄牌"),
    RED_CARD("red_card","红牌"),
    BLACK_CARD("black_card","黑牌"),
    // 以下为新增
    SUSPENSION("suspension","比赛中断"),
    SUSPENSION_OVER("suspension_over","比赛重开"),
    TIMEOUT("timeout","比赛暂停"),
    TIMEOUT_OVER("timeout_over","比赛继续"),
    WHICH_TEAM_SERVES_FIRST("which_team_serves_first","先发球"),
    CURRENT_SERVE_TABLE_TENNIS("current_serve_tabletennis","发球"),
    RE_SERVE("re_serve","重新发球"),
    EXPEDITE_MODE("expedite_mode","加速模式"),
    YELLOW_RED_CARD_SAME_HAND("yellowred_card_same_hand","红黄牌同手");

    private String code;
    private String name;

    TableTennisEventStatisticsEnum(String code, String name){
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
```

---

### 改动 2：扩展 `TableTennisStatisticsDto`

**文件：** `data-manager-sport-service/src/main/java/com/panda/sport/manager/sportservice/matchevent/dto/statistics/TableTennisStatisticsDto.java`

**变更内容：** 在现有字段后新增 5 个统计字段（`suspension`/`timeout` 通过 `statisticsConfig.registerCountEvent` 注册，不需要新增 Dto 字段；需要新增 Dto 字段的是：发球相关 + 加速模式 + 红黄牌同手）

```java
@Data
public class TableTennisStatisticsDto extends BaseStatisticsDto {
    // ... 现有字段保持不变 ...
    @ApiModelProperty(value = "得分")
    @StaticsItem(keyWord = "table_tennis_score_change", chosenItem = "first")
    private Map<String, StatisticsItemInteger> scoreSet = new HashMap<>();

    @ApiModelProperty(value = "黄牌")
    @StaticsItem(keyWord = "yellow_card")
    private Map<String, StatisticsItemInteger> yellowCard = new HashMap<>();

    @ApiModelProperty(value = "红牌")
    @StaticsItem(keyWord = "red_card")
    private Map<String, StatisticsItemInteger> redCard = new HashMap<>();

    // ===== 以下为新增字段 =====
    @ApiModelProperty(value = "先发球")
    @StaticsItem(keyWord = "which_team_serves_first")
    private Map<String, StatisticsItemInteger> whichTeamServesFirst = new HashMap<>();

    @ApiModelProperty(value = "发球")
    @StaticsItem(keyWord = "current_serve_tabletennis")
    private Map<String, StatisticsItemInteger> currentServe = new HashMap<>();

    @ApiModelProperty(value = "重新发球")
    @StaticsItem(keyWord = "re_serve")
    private Map<String, StatisticsItemInteger> reServe = new HashMap<>();

    @ApiModelProperty(value = "加速模式")
    @StaticsItem(keyWord = "expedite_mode")
    private Map<String, StatisticsItemInteger> expediteMode = new HashMap<>();

    @ApiModelProperty(value = "红黄牌同手")
    @StaticsItem(keyWord = "yellowred_card_same_hand")
    private Map<String, StatisticsItemInteger> yellowRedCardSameHand = new HashMap<>();

    public TableTennisStatisticsDto(Collection<String> dataSources) {
        for (String dataSourceCode : dataSources) {
            scoreSet.put(dataSourceCode, new StatisticsItemInteger("局比分"));
            yellowCard.put(dataSourceCode, new StatisticsItemInteger(TableTennisEventStatisticsEnum.YELLOW_CARD.getName()));
            redCard.put(dataSourceCode, new StatisticsItemInteger(TableTennisEventStatisticsEnum.RED_CARD.getName()));
            // 新增字段初始化
            whichTeamServesFirst.put(dataSourceCode, new StatisticsItemInteger(TableTennisEventStatisticsEnum.WHICH_TEAM_SERVES_FIRST.getName()));
            currentServe.put(dataSourceCode, new StatisticsItemInteger(TableTennisEventStatisticsEnum.CURRENT_SERVE_TABLE_TENNIS.getName()));
            reServe.put(dataSourceCode, new StatisticsItemInteger(TableTennisEventStatisticsEnum.RE_SERVE.getName()));
            expediteMode.put(dataSourceCode, new StatisticsItemInteger(TableTennisEventStatisticsEnum.EXPEDITE_MODE.getName()));
            yellowRedCardSameHand.put(dataSourceCode, new StatisticsItemInteger(TableTennisEventStatisticsEnum.YELLOW_RED_CARD_SAME_HAND.getName()));
        }
    }
}
```

---

### 改动 3：扩展 `TableTennisEventConverter`

**文件：** `data-manager-sport-service/src/main/java/com/panda/sport/manager/sportservice/matchevent/service/impl/TableTennisEventConverter.java`

**变更内容：** 在构造函数中补充注册新事件到 `statisticsConfig`

```java
public TableTennisEventConverter() {
    sportTypeEnum = SportTypeEnum.TABLE_TENNIS;
    log.info("创建{}赛事事件处理器={}", sportTypeEnum.getSportName(), this.getClass().getSimpleName());
    Field[] fields = TableTennisStatisticsDto.class.getDeclaredFields();
    for (Field field : fields) {
        String filedName = field.getName();
        if (!field.isAnnotationPresent(StaticsItem.class)) {
            log.info("统计数据结构中,该字段({})没有统计", filedName);
            continue;
        }
        StaticsItem item = field.getAnnotation(StaticsItem.class);
        affectStaticsCodeSet.addAll(Arrays.asList(item.keyWord()));
    }
    scoreEventCode.add("table_tennis_score_change");
    affectScoreEventCodes.addAll(scoreEventCode);
    // ===== 以下为新增：注册统计事件 =====
    this.statisticsConfig.registerCountEvent("which_team_serves_first", "先发球");
    this.statisticsConfig.registerCountEvent("current_serve_tabletennis", "发球");
    this.statisticsConfig.registerCountEvent("re_serve", "重新发球");
    this.statisticsConfig.registerCountEvent("expedite_mode", "加速模式");
    this.statisticsConfig.registerCountEvent("yellowred_card_same_hand", "红黄牌同手");
    // suspension/timeout 属于暂停/继续控制事件，不需要进入 affectStaticsCodeSet，
    // 但需要在 MatchEventService 中通过 matchPauseEventCodeSet 处理

    log.info("{}事件处理器初始化过程中构造事件关注列表={}", sportTypeEnum.getSportName(), JsonUtils.objectToJson(affectStaticsCodeSet));
}
```

---

### 改动 4：修复 `TableTennisMatchEventServiceImpl`

**文件：** `data-manager-sport-service/src/main/java/com/panda/sport/manager/sportservice/matcheventservice/impl/TableTennisMatchEventServiceImpl.java`

**变更内容：** 参照 `TennisMatchEventServiceImpl` 的模式，补充 `suspension`/`suspension_over`/`timeout`/`timeout_over` 事件处理

```java
@Slf4j
@Service
public class TableTennisMatchEventServiceImpl extends AbstractMatchEventService {

    public TableTennisMatchEventServiceImpl() {
        this.sportTypeEnum = SportTypeEnum.TABLE_TENNIS;
    }

    @Override
    public String onReceiveMatchEventInfo(MatchEventInfo eventInfo) {
        String eventCode = eventInfo.getEventCode();
        if ("suspension".equals(eventCode)) {
            actionMatchEventInfoMatchPause(eventInfo);
        } else if ("suspension_over".equals(eventCode)) {
            actionMatchEventInfoMatchPauseOver(eventInfo);
        } else if ("timeout".equals(eventCode)) {
            actionMatchEventInfoMatchPause(eventInfo);
        } else if ("timeout_over".equals(eventCode)) {
            actionMatchEventInfoMatchPauseOver(eventInfo);
        }
        return null;
    }
}
```

**说明：**
- `suspension`（比赛中断）和 `timeout`（比赛暂停）都调用 `actionMatchEventInfoMatchPause`，设置 `whetherStop=true`
- `suspension_over`（比赛重开）和 `timeout_over`（比赛继续）都调用 `actionMatchEventInfoMatchPauseOver`，设置 `whetherStop=false`
- 这与需求文档一致：比赛中断和比赛暂停期间都不能操作，比赛重开和比赛继续恢复操作

---

### 改动 5：在 `AbstractMatchEventHandle` 中注册暂停事件（推荐方式）

**文件：** `data-manager-sport-service/src/main/java/com/panda/sport/manager/sportservice/matchevent/service/AbstractMatchEventHandle.java`

**变更内容：** 在 `isEventAffectMatchPause` 和 `isEventAffectMatchPauseOver` 方法中，增加对乒乓球暂停事件的判断

```java
// 在 AbstractMatchEventHandle 中增加对乒乓球暂停事件的支持
// 注意：这不是修改抽象类本身，而是在 TableTennisEventConverter 中 override 这两个方法

@Override
public boolean isEventAffectMatchPause(MatchEventCommon matchEventCommon) {
    String eventCode = matchEventCommon.getEventCode();
    return "suspension".equals(eventCode) || "timeout".equals(eventCode);
}

@Override
public boolean isEventAffectMatchPause(MatchEventInfo eventInfo) {
    String eventCode = eventInfo.getEventCode();
    return "suspension".equals(eventCode) || "timeout".equals(eventCode);
}

@Override
public boolean isEventAffectMatchPauseOver(MatchEventCommon matchEventCommon) {
    String eventCode = matchEventCommon.getEventCode();
    return "suspension_over".equals(eventCode) || "timeout_over".equals(eventCode);
}

@Override
public boolean isEventAffectMatchPauseOver(MatchEventInfo eventInfo) {
    String eventCode = eventInfo.getEventCode();
    return "suspension_over".equals(eventCode) || "timeout_over".equals(eventInfo.getEventCode());
}
```

**但注意：** `TableTennisMatchEventServiceImpl` 中已经有显式的 if-else 处理，上述 override 是双重保障。根据代码设计，`AbstractMatchEventService.onReceiveMatchEventInfo` 先调用 `sportMatchEventHandler.isEventAffectMatchPause`，再调 `actionMatchEventInfoMatchPause`；而 `TableTennisMatchEventServiceImpl` 的 `onReceiveMatchEventInfo` 覆盖了父类方法，因此**只改改动 4 即可**，不需要改 `AbstractMatchEventHandle`。

**最终方案：只改改动 4（`TableTennisMatchEventServiceImpl`），不修改 `AbstractMatchEventHandle`。**

---

### 改动 6：`Constant` 中补充新常量（可选，建议）

**文件：** `data-manager-common/src/main/java/com/panda/sport/manager/constant/Constant.java`

在 `TABLE_TENNIS_SCORE_CHANGE` 附近补充：

```java
public static final String TABLE_TENNIS_SUSPENSION = "suspension";
public static final String TABLE_TENNIS_SUSPENSION_OVER = "suspension_over";
public static final String TABLE_TENNIS_TIMEOUT = "timeout";
public static final String TABLE_TENNIS_TIMEOUT_OVER = "timeout_over";
public static final String TABLE_TENNIS_WHICH_TEAM_SERVES_FIRST = "which_team_serves_first";
public static final String TABLE_TENNIS_CURRENT_SERVE = "current_serve_tabletennis";
public static final String TABLE_TENNIS_RE_SERVE = "re_serve";
public static final String TABLE_TENNIS_EXPEDITE_MODE = "expedite_mode";
public static final String TABLE_TENNIS_YELLOW_RED_CARD_SAME_HAND = "yellowred_card_same_hand";
```

---

## 三、不动的代码

以下代码**不需要修改**，原因说明：

| 代码 | 原因 |
|------|------|
| `TableTennisMatchResultServiceImpl.convertMatchStatusCommon` | match_status 事件已通过 `serviceActionMatchStatus` 处理，局比分和总局比分逻辑完整 |
| `TableTennisCheckImpl` | 六字段校验（eventHome/Away + homeFirst/Away + homeSecond/Away）对乒乓球完全适用 |
| `SportPeriodEnum.TABLE_TENNIS` | 阶段配置已完整（含 441/442/307） |
| `MatchResultStatusConfirmEnum.TABLE_TENNIS` | 阶段配置已完整 |
| `MatchEventCommon.preDealMatchEventCommon` | 删除事件（`canceled=1`）的逻辑在基类中已处理，无需修改 |

---

## 四、影响范围

### 4.1 涉及模块

| 模块 | 改动 |
|------|------|
| `data-manager-common` | `TableTennisEventStatisticsEnum` + `Constant`（可选） |
| `data-manager-sport-service` | `TableTennisStatisticsDto` + `TableTennisEventConverter` |
| `data-manager-sport-service` | `TableTennisMatchEventServiceImpl` |

### 4.2 不需要改动的模块

- `data-manager-controller`：已有接口，前端传 sportId=8 即可走乒乓球逻辑
- `data-manager-mq-consumer`：MQ 消费已注册，`TableTennisMatchEventServiceImpl` 是 `IMessageConsumer` 的子类，会自动被框架扫描注册
- `data-manager-db`：DB 实体和 Mapper 无变更

### 4.3 前端影响

- 前端 `/main/liveOddSupportmorning` 路由对应的组件 `4e28` 是及时注单和 PA 报球板共用的
- 乒乓球报球板 UI 需要前端单独开发（不在本次后端改动范围）
- 后端数据下发范围：所有现有接口按 sportId=8 自动走乒乓球逻辑

---

## 五、验收标准（对应需求文档第七节）

| 编号 | 验收点 | 对应改动 |
|------|--------|---------|
| 1 | 比赛中断 `suspension` 事件到达后，`standard_match_info.whether_stop=true` | 改动 4 |
| 2 | 比赛重开 `suspansion_over` 事件到达后，`standard_match_info.whether_stop=false` | 改动 4 |
| 3 | 比赛暂停 `timeout` 事件到达后，计时停止 | 改动 4 |
| 4 | 比赛继续 `timeout_over` 事件到达后，计时恢复 | 改动 4 |
| 5 | 先发球/发球/重新发球事件被正确统计，赛事消息中显示对应事件 | 改动 2+3 |
| 6 | 加速模式事件被正确统计 | 改动 2+3 |
| 7 | 黄牌/红牌事件统计完整（已有） | 已有 |
| 8 | 红黄牌同手事件被统计，且确认后比赛结束（前端确认） | 改动 2+3 |
| 9 | 删除事件（红牌/黄牌/赢分）能通过 `preDealMatchEventCommon` 正确过滤 | 已有基类支持 |
| 10 | 比分校验六字段比对正常 | 已有 `TableTennisCheckImpl` |

---

## 六、实施步骤

```
Step 1: 改动 1 — 扩展 TableTennisEventStatisticsEnum（新增 7 个枚举项）
Step 2: 改动 2 — 扩展 TableTennisStatisticsDto（新增 5 个字段 + 构造函数初始化）
Step 3: 改动 3 — 扩展 TableTennisEventConverter（构造函数中 registerCountEvent 5 个新事件）
Step 4: 改动 4 — 修复 TableTennisMatchEventServiceImpl（补充 4 个事件处理）
Step 5: 改动 6 — Constant 补充常量（可选，仅用于代码可读性）
Step 6: 编译验证 + 接口测试
```

---

## 七、风险评估

| 风险 | 等级 | 说明 |
|------|------|------|
| `TableTennisStatisticsDto` 新增字段影响序列化 | 低 | 新增字段默认值为空 Map，前端不感知，兼容 |
| `TableTennisMatchEventServiceImpl` 覆盖父类方法 | 低 | 参考 `TennisMatchEventServiceImpl` 已有模式，安全 |
| `statisticsConfig.registerCountEvent` 重复注册 | 低 | `registerCountEvent` 内部有 `eventDisplayNameMap.containsKey` 判断，幂等 |
| 与现有排球逻辑冲突 | 极低 | sportId 不同，逻辑完全隔离 |

---

## 八、Commit Message 建议

```
[4079] 乒乓球报球板后端能力补全

产生原因: 现有乒乓球代码骨架仅有比分和黄红牌基础统计，缺少比赛中断/暂停事件处理
          和发球/加速模式等 IMP 事件统计，导致 PA 报球板无法正常运作
解决方案: 1. 扩展 TableTennisEventStatisticsEnum 新增 7 个事件枚举
         2. 扩展 TableTennisStatisticsDto 新增 5 个统计字段
         3. TableTennisEventConverter 注册新事件到 statisticsConfig
         4. TableTennisMatchEventServiceImpl 处理 suspension/suspension_over/timeout/timeout_over
影响范围: data-manager-common, data-manager-sport-service
```
