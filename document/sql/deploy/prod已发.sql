#################2020年10月17日已发隔离start##############
################################3.0match_result################################
ALTER TABLE standard_match_result
MODIFY COLUMN template_id varchar(20) NOT NULL DEFAULT '0';

ALTER TABLE standard_match_result
MODIFY COLUMN template_text varchar(255) DEFAULT NULL;

ALTER TABLE third_match_result
MODIFY COLUMN template_id varchar(20) NOT NULL DEFAULT '0';

ALTER TABLE third_match_result
MODIFY COLUMN template_text varchar(255) DEFAULT NULL;


INSERT INTO match_event_template
(id,sport_id, order_no, trigger_code, trigger_period_id, event_code, template_text, template_format, template_no, audit_time, bill_time)
VALUES (49,1, 20, 'match_status', '999', 'goal', '全场结束', '全场结束', 0, 180, 60);

INSERT INTO match_event_template
(id,sport_id, order_no, trigger_code, trigger_period_id, event_code, template_text, template_format, template_no, audit_time, bill_time)
VALUES (50,2, 8, 'match_status', '999', 'score_change', '全场结束', '全场结束', 0, 180, 60);

INSERT INTO match_event_template
(id,sport_id, order_no, trigger_code, trigger_period_id, event_code, template_text, template_format, template_no, audit_time, bill_time)
VALUES (51,5, 6, 'match_status', '999', 'tennis_score_change', '全场结束', '全场结束', 0, 180, 60);

INSERT INTO match_event_template
(id,sport_id, order_no, trigger_code, trigger_period_id, event_code, template_text, template_format, template_no, audit_time, bill_time)
VALUES (52,7, 10, 'match_status', '999', 'ball_pot', '全场结束', '全场结束', 0, 180, 60);

INSERT INTO match_event_template
(id,sport_id, order_no, trigger_code, trigger_period_id, event_code, template_text, template_format, template_no, audit_time, bill_time)
VALUES (53,8, 5, 'match_status', '999', 'table_tennis_score_change', '全场结束', '全场结束', 0, 180, 60);

INSERT INTO match_event_template
(id,sport_id, order_no, trigger_code, trigger_period_id, event_code, template_text, template_format, template_no, audit_time, bill_time)
VALUES (54,10, 5, 'match_status', '999', 'badminton_score_change', '全场结束', '全场结束', 0, 180, 60);


INSERT INTO match_event_template_period(template_id, trigger_period_id)
select id template_id,trigger_period_id
from match_event_template where trigger_period_id = '999';


INSERT INTO match_event_template_code(template_id, trigger_code)
select id template_id,trigger_code
from match_event_template where trigger_period_id = '999';



UPDATE match_event_template
SET trigger_code = 'match_status',
trigger_period_id = '800|900|1000|1100|1200', event_code = 'tennis_score_change', template_text = '第X盘第Y局-Player A/Player B获胜', template_format = '第%s盘第%s局-<span class=\'ec\'>%s</span>获胜', template_no = 1
WHERE id = 27;

UPDATE match_event_template
SET trigger_code = 'tennis_score_change',
trigger_period_id = '8|9|10|11|12',
event_code = 'tennis_score_change', template_text = '第X盘进入抢七局', template_format = '第%s盘进入抢七局', template_no = 1
WHERE id = 28;

UPDATE match_event_template
SET trigger_code = 'match_status',
trigger_period_id = '800|900|1000|1100|1200', event_code = 'tennis_score_change', template_text = '第X盘抢七局比分C:D', template_format = '第%s盘抢七局比分%s:%s', template_no = 1
WHERE id = 29;

UPDATE match_event_template
SET trigger_code = 'match_status',
trigger_period_id = '301|302|303|304', event_code = 'tennis_score_change', template_text = '第X盘比分M:N，总局数比分E:F', template_format = '第%s盘比分%s:%s，总局数比分%s:%s', template_no = 1
WHERE id = 31;

UPDATE match_event_template
SET template_text = '点球大战第X个点球-(HomeorAway 射进 M:N)',
template_format = '点球大战第%s个点球-(<span class=\'ec\'>%s</span> 射进 %s:%s)'
WHERE id = 12;

INSERT INTO match_event_template(id,
sport_id, order_no, trigger_code, trigger_period_id, event_code, template_text, template_format, template_no, audit_time, bill_time)
VALUES
(55, 1, 21, 'penalty_missed', '50', 'penalty_missed', '点球大战第X个点球-(HomeorAway 射失 M:N)', '点球大战第%s个点球-(<span class=\'ec\'>%s</span> 射失 %s:%s)', 2, 180, 60);

INSERT INTO match_event_template_period(template_id, trigger_period_id)
select id template_id,trigger_period_id
from match_event_template where trigger_period_id = '50' and trigger_code = 'penalty_missed';

INSERT INTO match_event_template_code(template_id, trigger_code)
select id template_id,trigger_code
from match_event_template where trigger_period_id = '50' and trigger_code = 'penalty_missed';


INSERT INTO market_category_template_relation
(market_category_id, template_id, dynamic_flag, sport_id)
select
133 market_category_id, id template_id, 1 dynamic_flag, 1 sport_id
from match_event_template where trigger_period_id = '50' and trigger_code = 'penalty_missed';


UPDATE match_event_code
SET add_event = 1
WHERE id = 19 limit 1;

delete from match_event_template_period where template_id in (
	select id from match_event_template where sport_id = 5
);

INSERT INTO match_event_template_period(template_id, trigger_period_id)
VALUES
 (27, 800),
 (27, 900),
 (27, 1000),
 (27, 1100),
 (27, 1200),
 (28, 8),
 (28, 9),
 (28, 10),
 (28, 11),
 (28, 12),
 (29, 800),
 (29, 900),
 (29, 1000),
 (29, 1100),
 (29, 1200),
 (30, 100),
 (31, 301),
 (31, 302),
 (31, 303),
 (31, 304),
 (51, 999);

delete from match_event_template_code where template_id in (
	select id from match_event_template where sport_id = 5
);

INSERT INTO match_event_template_code(template_id, trigger_code)
select id template_id,trigger_code from match_event_template where sport_id = 5;

INSERT INTO system_item_dict(parent_type_id, code, value, active, description, addition1, remark, create_time, modify_time)
VALUES
 ( 8, 'FIRST_SET_END', '301', 1, 'SET1 结束', '5', '1st set end', 1567771988510, 1567771988510),
 ( 8, 'SECOND_SET_END', '302', 1, 'SET2 结束', '5', '2st set end', 1567771988510, 1567771988510),
 ( 8, 'THIRD_SET_END', '303', 1, 'SET3 结束', '5', '3st set end', 1567771988510, 1567771988510),
 ( 8, 'FOURTH_SET_END', '304', 1, 'SET4 结束', '5', '4st set end', 1567771988510, 1567771988510),
 ( 8, 'FIFTH_SET_END', '305', 1, 'SET5 结束', '5', '5st set end', 1567771988510, 1567771988510),

 ( 8, 'FIRST_SET_GAME_END', '800', 1, 'SET1', '5', '1st set game end', 1567771988510, 1567771988510),
 ( 8, 'SECOND_SET_GAME_END', '900', 1, 'SET2', '5', '2st set game end', 1567771988510, 1567771988510),
 ( 8, 'THIRD_SET_GAME_END', '1000', 1, 'SET3', '5', '3st set game end', 1567771988510, 1567771988510),
 ( 8, 'FOURTH_SET_GAME_END', '1100', 1, 'SET4', '5', '4st set game end', 1567771988510, 1567771988510),
 ( 8, 'FIFTH_SET_GAME_END', '1200', 1, 'SET5', '5', '5st set game end', 1567771988510, 1567771988510);

UPDATE match_event_template
SET trigger_period_id = '301|302|303|304|100', template_no = 1 WHERE id = 34;

UPDATE match_event_template SET template_no = 1 WHERE id = 33;

UPDATE match_event_template_period SET trigger_period_id = 301 WHERE id = 85;
UPDATE match_event_template_period SET trigger_period_id = 302 WHERE id = 86;
UPDATE match_event_template_period SET trigger_period_id = 303 WHERE id = 87;
UPDATE match_event_template_period SET trigger_period_id = 304 WHERE id = 88;
UPDATE match_event_template_period SET trigger_period_id = 305 WHERE id = 89;

UPDATE match_event_code SET event_name = '比分', add_event = 1 WHERE id = 52;
UPDATE match_event_code SET event_name = '比分', add_event = 1 WHERE id = 58;
UPDATE match_event_code SET event_name = '比分', add_event = 1 WHERE id = 40;
UPDATE match_event_code SET event_name = '比分', add_event = 1 WHERE id = 44;

UPDATE match_event_template SET template_text = '第X节比分m:n', template_format = '第%s节比分%s:%s' WHERE id = 21;
UPDATE match_event_template SET template_text = '上半场比分m:n', template_format = '上半场比分%s:%s' WHERE id = 22;
UPDATE match_event_template SET template_text = '下半场比分m:n，常规时间比分 M:N', template_format = '下半场比分%s:%s，常规时间比分%s:%s' WHERE id = 23;
UPDATE match_event_template SET template_text = '全场-Home/Away获得第Y分', template_format = '全场-<span class=\'ec\'>%s</span>获得第%s分' WHERE id = 25;
UPDATE match_event_template SET template_text = '全场-Home/Away得分达到Y分', template_format = '全场-<span class=\'ec\'>%s</span>得分达到%s分' WHERE id = 26;
UPDATE match_event_template SET template_text = '第X节-Home/Away得分达到Y分', template_format = '第%s节-<span class=\'ec\'>%s</span>得分达到%s分' WHERE id = 20;
UPDATE match_event_template SET template_text = '全场比分(含加时) M:N', template_format = '全场比分(含加时) %s:%s' WHERE id = 24;
UPDATE match_event_template SET template_text = '第X局Player A/Player B获得第Y分', template_format = '第%s局<span class=\'ec\'>%s</span>获得第%s分' WHERE id = 32;
UPDATE match_event_template SET template_text = '第X局Player A/Player B获得第Y分', template_format = '第%s局<span class=\'ec\'>%s</span>获得第%s分' WHERE id = 36;
UPDATE match_event_template SET template_text = '第X局最后得分(Player A/Player B)-红球/黄球/绿球/棕球/蓝球/粉球/黑球/犯规', template_format = '第%s局最后得分（%s）- %s' WHERE id = 45;


update market_category_template_relation
set dynamic_flag = 0
where market_category_id = 205 and template_id = 30 and sport_id = 5 limit 1;

update market_category_template_relation
set dynamic_flag = 0
where market_category_id = 206 and template_id = 30 and sport_id = 5 limit 1;

update market_category_template_relation
set dynamic_flag = 1
where market_category_id = 133 and template_id = 12 and sport_id = 1 limit 1;

update market_category_template_relation
set dynamic_flag = 0
where market_category_id = 161 and template_id = 30 and sport_id = 5 limit 1;

update market_category_template_relation
set dynamic_flag = 0
where market_category_id = 207 and template_id = 30 and sport_id = 5 limit 1;

UPDATE market_category_template_relation
SET dynamic_flag = 1
WHERE market_category_id = 180 and template_id = 47 and sport_id = 7 limit 1;

UPDATE market_category_template_relation
SET dynamic_flag = 2
WHERE market_category_id = 191 and template_id = 42 and sport_id = 7 limit 1;

UPDATE market_category_template_relation
SET dynamic_flag = 2
WHERE market_category_id = 192 and template_id = 42 and sport_id = 7 limit 1;

UPDATE market_category_template_relation
SET dynamic_flag = 2
WHERE market_category_id = 193 and template_id = 42 and sport_id = 7 limit 1;


UPDATE market_category_template_relation
SET dynamic_flag = 2
WHERE market_category_id = 179 and template_id = 38 and sport_id = 8 limit 1;

UPDATE market_category_template_relation
SET dynamic_flag = 2
WHERE market_category_id = 203 and template_id = 38 and sport_id = 8 limit 1;


UPDATE system_item_dict
SET description = '第1局结束'
WHERE parent_type_id = 8 and code = 'FIRST_PAUSE' and value = '301' and addition1 = '10' limit 1;

UPDATE system_item_dict
SET description = '第2局结束'
WHERE parent_type_id = 8 and code = 'SECOND_PAUSE' and value = '302' and addition1 = '10' limit 1;

UPDATE system_item_dict
SET description = '第3局结束'
WHERE parent_type_id = 8 and code = 'THIRD_PAUSE' and value = '303' and addition1 = '10' limit 1;

UPDATE system_item_dict
SET description = '第4局结束'
WHERE parent_type_id = 8 and code = 'FOURTH_PAUSE' and value = '304' and addition1 = '10' limit 1;

UPDATE market_category_template_relation
SET dynamic_flag = 2
WHERE market_category_id = 194 and template_id = 43 and sport_id = 7 LIMIT 1;


UPDATE market_category_template_relation
SET dynamic_flag = 2
WHERE market_category_id = 180 and template_id = 47 and sport_id = 7 LIMIT 1;

UPDATE match_event_template SET template_no = 1 WHERE id = 30;
UPDATE match_event_template SET event_code = 'ball_pot' WHERE id = 41;
UPDATE match_event_template SET event_code = 'goal' WHERE id = 55;

UPDATE match_event_code SET add_event = 1 WHERE id = 48;

UPDATE match_event_template
SET trigger_period_id = '100' WHERE id = 23 LIMIT 1;
UPDATE match_event_template
SET trigger_period_id = '110' WHERE id = 24 LIMIT 1;

delete from match_event_template_period where template_id = 23 and trigger_period_id = 2 LIMIT 1;
delete from match_event_template_period where template_id = 24 and trigger_period_id = 100 LIMIT 1;

update system_item_dict set description = 'SET1' where parent_type_id = 8 and addition1 = 5 and `value` = 800 LIMIT 1;
update system_item_dict set description = 'SET2' where parent_type_id = 8 and addition1 = 5 and `value` = 900 LIMIT 1;
update system_item_dict set description = 'SET3' where parent_type_id = 8 and addition1 = 5 and `value` = 1000 LIMIT 1;
update system_item_dict set description = 'SET4' where parent_type_id = 8 and addition1 = 5 and `value` = 1100 LIMIT 1;
update system_item_dict set description = 'SET5' where parent_type_id = 8 and addition1 = 5 and `value` = 1200 LIMIT 1;

INSERT INTO
market_category_template_relation(market_category_id, template_id, dynamic_flag, sport_id)
VALUES (134, 55, 0, 1),
(237, 55, 1, 1),
(238, 55, 0, 1),
(239, 55, 0, 1),
(240, 55, 0, 1),
(241, 55, 0, 1);

-- 赛事开售表添加赛事标签字段
ALTER TABLE standard_sport_market_sell ADD label tinyint(4) DEFAULT 0  COMMENT '赛事标签';

ALTER TABLE standard_sport_tournament ADD COLUMN `hot_status` tinyint(2)  COMMENT '是否热门联赛 0:false  1:true' DEFAULT 0 AFTER season;


################################sport_market_relation################################
CREATE TABLE `sport_market_relation`
(
    `id`                  bigint(20) NOT NULL,
    `market_relation_key` varchar(100) COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
    `relation_market_id`  bigint(20)                              DEFAULT NULL,
    `create_time`         bigint(20)                              DEFAULT '0',
    `modify_time`         bigint(20)                              DEFAULT '0',
    PRIMARY KEY (`id`),
    UNIQUE KEY `market_relation_key` (`market_relation_key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_as_cs COMMENT ='记录表relationId';


################################panda-odds-admin################################

-- 创建自动手动操盘配置表
DROP TABLE config_trade_type;
CREATE TABLE `config_trade_type` (
  `id` bigint(22) NOT NULL AUTO_INCREMENT,
  `level` tinyint(2) DEFAULT '0' COMMENT '生效级别 1:玩法 3:赛事',
  `standard_match_id` varchar(100) COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '标准赛事id',
  `standard_category_id` varchar(100)  COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '标准玩法id',
  `trade_type` tinyint(2) DEFAULT NULL COMMENT '操盘类型 0:自动操盘 1:手动操盘',
  `modify_time` bigint(20) DEFAULT '0' COMMENT '配置修改时间',
  `create_time` bigint(20) DEFAULT '0' COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_match_id_category_id` (`standard_match_id`,`standard_category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='自动手动操盘配置';

-- 修改操盘级别状态和盘口状态一致（修改此处需要和业务同步）
alter table standard_match_info modify column  operate_match_status tinyint COMMENT '比赛开盘标识.-1:未开盘; 0: 开盘; 1: 封盘; 2:关盘; 11:锁盘;';
alter table standard_match_info alter column operate_match_status set default -1;
update standard_match_info set operate_match_status = -1 where operate_match_status = 0;
update standard_match_info set operate_match_status = 0 where operate_match_status = 1;
update standard_match_info set operate_match_status = 1 where operate_match_status = 3;

ALTER TABLE config_market_trade_item
ADD COLUMN link_id varchar(100) NULL AFTER min_odds_value;

ALTER TABLE config_trade_market
ADD COLUMN link_id varchar(100) NULL AFTER addition3;

ALTER TABLE third_sport_market_odds
ADD COLUMN `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '投注项名称' AFTER `name_code`;

ALTER TABLE config_trade_market
ADD COLUMN MATCH_TYPE varchar(32) NULL COMMENT '赛事类型：0.普通赛事、1.冠军赛事' AFTER TARGET_ID;


################################panda-realtime-admin################################

drop table if exists configuration_match_data_source;
CREATE TABLE `configuration_match_data_source` (
  `id` bigint(22) NOT NULL AUTO_INCREMENT,
  `standard_match_id` bigint(20) NOT NULL COMMENT '标准赛事id',
  `market_type` tinyint(4) DEFAULT NULL COMMENT '盘口类型 1早盘  0滚球',
  `sr_weight` tinyint(4) DEFAULT NULL COMMENT 'SR权重',
  `bc_weight` tinyint(4) DEFAULT NULL COMMENT 'BC权重',
  `bg_weight` tinyint(4) DEFAULT NULL COMMENT 'BG权重',
  `score_source` tinyint(4) DEFAULT NULL COMMENT '比分源1:SR(LiveData)  2:UOF, 注意：比分源还有为null的情况，需适配',
  `create_time` bigint(20) DEFAULT '0' COMMENT '创建时间',
  `modify_time` bigint(20) DEFAULT '0' COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_standard_match_id` (`standard_match_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1336 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='赛事开盘数据源配置表';


drop table if exists configuration_match_template_event;
CREATE TABLE `configuration_match_template_event` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `standard_match_id` bigint(20) NOT NULL COMMENT '标准赛事id',
  `event_code` varchar(64) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '事件编码',
  `event_audit_time` int(11) DEFAULT NULL COMMENT '自动审核时间',
  `event_settlement_time` int(11) DEFAULT NULL COMMENT '事件结算时间',
  `create_time` bigint(20) DEFAULT '0' COMMENT '创建时间',
  `modify_time` bigint(20) DEFAULT '0' COMMENT '修改时间',
  `canceled` tinyint(4) DEFAULT '0' COMMENT '是否被取消.1 被取消; 0:没有被取消',
  PRIMARY KEY (`id`),
  KEY `idx_event_standard_match_id` (`standard_match_id`,`event_code`)
) ENGINE=InnoDB AUTO_INCREMENT=1698 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='赛事开盘事件审核明细表';


ALTER  TABLE market_category_sell ADD COLUMN `is_sell` tinyint(4) DEFAULT '0' COMMENT '是否开售 1：是  0：否';
ALTER  TABLE market_category_sell ADD COLUMN link_id varchar(100) NULL AFTER is_sell;
ALTER  TABLE market_category_sell ADD COLUMN `market_count` int(11) DEFAULT '0' COMMENT '最大盘口数';
ALTER  TABLE market_category_sell ADD COLUMN `is_series` tinyint(4) DEFAULT '0' COMMENT '支持串关，1:是 0:否';
ALTER  TABLE market_category_sell ADD COLUMN `auto_close_Market` int(11) DEFAULT '0' COMMENT '足球自动关盘时间设置：6、上半场期间 41、加时赛上半场 7、下半场期间 42、加时赛下半场     篮球自动关盘时间设置：13、第1节 14、第2节 15、第3节 16、第4节 40、加时';
ALTER  TABLE market_category_sell ADD COLUMN `match_progress_time` int(11) DEFAULT '0' COMMENT '比赛进程时间';
ALTER  TABLE market_category_sell ADD COLUMN `injury_time` int(11) DEFAULT '0' COMMENT '补时时间';
ALTER  TABLE market_category_sell ADD COLUMN `market_near_diff`  decimal(20, 2) DEFAULT '0' COMMENT '相邻盘口差值';
ALTER  TABLE market_category_sell ADD COLUMN `market_near_odds_diff`  decimal(20, 2) DEFAULT '0' COMMENT '相邻盘口赔率差值';

-- 最大盘口数默认值设置
update  market_category_sell set market_count = 3 where sell_status ='Sold' and sell_time >(unix_timestamp(now()) * 1000)- (60 * 60 * 24 * 1000 * 7) ;


################################third_market_category################################

-- 先执行删除语句
delete from third_market_category where third_source_id in ('SR:534_1','SR:534_2','SR:534_3','SR:534_4','SR:534_5','SR:534_6','SR:535_1','SR:535_2',
'SR:535_3','SR:536_1','SR:536_2','SR:536_3','SR:536_4','SR:536_5','SR:536_6','SR:536_7','SR:536_8');
INSERT INTO third_market_category( name_code, fields_num, third_source_id, reference_id, data_source_code, active, create_time, modify_time) VALUES
(1304695065015926785, 0, 'SR:534_1', 10001, 'SR', 1, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304695076391874562, 0, 'SR:534_2', 10002, 'SR', 1, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304695076425428994, 0, 'SR:534_3', 10013, 'SR', 1, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304981351526219777, 0, 'SR:534_4', 10010, 'SR', 1, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304981351555579905, 0, 'SR:534_5', 10011, 'SR', 1, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304981351695089665, 0, 'SR:534_6', 10012, 'SR', 1, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304695068442673153, 0, 'SR:535_1', 10015, 'SR', 1, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304695083236978690, 0, 'SR:535_2', 10016, 'SR', 1, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304695083295698946, 0, 'SR:535_3', 10017, 'SR', 1, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304695066300481537, 0, 'SR:536_1', 10003, 'SR', 1, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304695069709352961, 0, 'SR:536_2', 10004, 'SR', 1, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304695084839202818, 0, 'SR:536_3', 10005, 'SR', 1, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304695066325647361, 0, 'SR:536_4', 10006, 'SR', 1, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304695066342424578, 0, 'SR:536_5', 10007, 'SR', 1, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304695069730324481, 0, 'SR:536_6', 10008, 'SR', 1, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304695084868562946, 0, 'SR:536_7', 10009, 'SR', 1, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304695066371784706, 0, 'SR:536_8', 10014, 'SR', 1, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);

delete from i18n_market_category where name_code in (1304695065015926785,1304695066300481537,1304695066325647361,1304695066342424578,1304695066371784706,1304695068442673153,1304695069709352961,1304695069730324481,
1304695076391874562, 1304695076425428994,1304695083236978690,1304695083295698946,1304695084839202818,1304695084868562946,1304981351526219777,1304981351555579905,1304981351695089665);
INSERT INTO i18n_market_category (name_code, `flag`, data_source_code, language_type, `text`, remark, create_time, modify_time) VALUES
(1304695065015926785, 2, 'SR', 'en', 'Championship free text market', '', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304695066300481537, 2, 'SR', 'en', 'Free text multiwinner market', '', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304695066325647361, 2, 'SR', 'en', 'Free text multiwinner market', '', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304695066342424578, 2, 'SR', 'en', 'Free text multiwinner market', '', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304695066371784706, 2, 'SR', 'en', 'Free text multiwinner market', '', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304695068442673153, 2, 'SR', 'en', 'Short term free text market', '', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304695069709352961, 2, 'SR', 'en', 'Free text multiwinner market', '', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304695069730324481, 2, 'SR', 'en', 'Free text multiwinner market', '', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304695076391874562, 2, 'SR', 'en', 'Championship free text market', '', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304695076425428994, 2, 'SR', 'en', 'Championship free text market', '', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304695083236978690, 2, 'SR', 'en', 'Short term free text market', '', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304695083295698946, 2, 'SR', 'en', 'Short term free text market', '', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304695084839202818, 2, 'SR', 'en', 'Free text multiwinner market', '', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304695084868562946, 2, 'SR', 'en', 'Free text multiwinner market', '', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304981351526219777, 2, 'SR', 'en', 'Championship free text market', '', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304981351555579905, 2, 'SR', 'en', 'Championship free text market', '', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(1304981351695089665, 2, 'SR', 'en', 'Championship free text market', '', unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);
COMMIT;

update third_market_category set reference_id = 10001,modify_time = unix_timestamp(now()) * 1000 where third_source_id = 'BC:9782';
update third_market_category set reference_id = 10016,modify_time = unix_timestamp(now()) * 1000 where third_source_id = 'BC:9508';
update third_market_category set reference_id = 10017,modify_time = unix_timestamp(now()) * 1000 where third_source_id = 'BC:9857';
update third_market_category set reference_id = 10001,modify_time = unix_timestamp(now()) * 1000 where third_source_id = 'BC:9817';
update third_market_category set reference_id = 10003,modify_time = unix_timestamp(now()) * 1000 where third_source_id = 'BC:12057';
update third_market_category set reference_id = 10004,modify_time = unix_timestamp(now()) * 1000 where third_source_id = 'BC:12027';
update third_market_category set reference_id = 10005,modify_time = unix_timestamp(now()) * 1000 where third_source_id = 'BC:12029';
update third_market_category set reference_id = 10007,modify_time = unix_timestamp(now()) * 1000 where third_source_id = 'BC:12030';
update third_market_category set reference_id = 10008,modify_time = unix_timestamp(now()) * 1000 where third_source_id = 'BC:12032';
update third_market_category set reference_id = 10009,modify_time = unix_timestamp(now()) * 1000 where third_source_id = 'BC:12033';
COMMIT;

################################standard_market_category################################
-- 新增标准冠军玩法
delete from standard_market_category where id > 10000;
INSERT INTO standard_market_category  (id, name_code, fields_num, multi_market, support_odds, template_pc, template_h5, `status`, order_no, create_time, modify_time) VALUES
(10001,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 0, 0, '2', 0, 0, 1, 265, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(10002,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 0, 0, '2', 0, 0, 1, 265, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(10003,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 0, 0, '2', 0, 0, 1, 265, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(10004,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 0, 0, '2', 0, 0, 1, 265, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(10005,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 0, 0, '2', 0, 0, 1, 265, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(10006,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 0, 0, '2', 0, 0, 1, 265, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(10007,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 0, 0, '2', 0, 0, 1, 265, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(10008,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 0, 0, '2', 0, 0, 1, 265, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(10009,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 0, 0, '2', 0, 0, 1, 265, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(10010,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 0, 0, '2', 0, 0, 1, 265, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(10011,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 0, 0, '2', 0, 0, 1, 265, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(10012,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 0, 0, '2', 0, 0, 1, 265, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(10013,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 0, 0, '2', 0, 0, 1, 265, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(10014,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 0, 0, '2', 0, 0, 1, 265, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(10015,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 0, 0, '2', 0, 0, 1, 265, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(10016,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 0, 0, '2', 0, 0, 1, 265, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000),
(10017,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 0, 0, '2', 0, 0, 1, 265, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000);
COMMIT;

################################third_sport_market_category################################
delete FROM third_sport_market_category WHERE sport_id in (18,105);
INSERT INTO third_sport_market_category (sport_id, market_category_id, create_time, modify_time)
select 18, id, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000
from third_market_category
where third_source_id in ('BC:9508','BC:9857','SR:535_1','SR:535_2','SR:535_3');

delete from  third_sport_market_category
where sport_id = 2 and market_category_id in (select id from third_market_category where third_source_id in ('BC:9782','SR:534_1','SR:534_3','SR:536_8'));
INSERT INTO third_sport_market_category (sport_id, market_category_id, create_time, modify_time)
select 2, id, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000
from third_market_category
where third_source_id in ('BC:9782','SR:534_1','SR:534_3','SR:536_8');


delete from third_sport_market_category
where sport_id = 1 and market_category_id in (select id from third_market_category where third_source_id in ('BC:9817','BC:12057','BC:12027','BC:12029','BC:12030','BC:12032','BC:12033',
'SR:534_1','SR:534_2','SR:536_1','SR:536_2','SR:536_3','SR:536_4','SR:536_5','SR:536_6','SR:536_7','SR:534_4','SR:534_5','SR:534_6'));
INSERT INTO third_sport_market_category (sport_id, market_category_id, create_time, modify_time)
select 1, id, unix_timestamp(now()) * 1000, unix_timestamp(now()) * 1000
from third_market_category
where third_source_id in ('BC:9817','BC:12057','BC:12027','BC:12029','BC:12030','BC:12032','BC:12033',
'SR:534_1','SR:534_2','SR:536_1','SR:536_2','SR:536_3','SR:536_4','SR:536_5','SR:536_6','SR:536_7','SR:534_4','SR:534_5','SR:534_6');
COMMIT;


################################standard_sport_market_category################################
-- is_collapse: 是否展开，1：“是” 代表默认展开，0：“否” 代表默认收起,  scope_id: 所属时段, status: 玩法状态. 0无效; 1有效, order_no排序
-- SELECT	* FROM	third_sport_type;
-- SELECT item_t.* FROM	system_type_dict dict_t,	system_item_dict item_t WHERE	dict_t.id = item_t.parent_type_id 	AND dict_t.`code` = 'playTimeStage';

-- 运动类型、所有时区新增
-- INSERT INTO standard_sport_type(id,name_code, introduction, remark, create_time, modify_time) VALUES (105, 105, '政治娱乐', '政治娱乐', 1599027704000, 1599027704000);
-- INSERT INTO third_sport_type(name_code, third_sport_id, data_source_code, reference_id, introduction, remark, create_time, modify_time)
-- VALUES (105, '18', 'SR', 105, '政治娱乐', '政治娱乐', unix_timestamp(now())*1000,unix_timestamp(now())* 1000);
-- INSERT INTO third_sport_type(name_code, third_sport_id, data_source_code, reference_id, introduction, remark, create_time, modify_time)
-- VALUES (106, '89', 'BC', 105, '政治娱乐', '政治娱乐', unix_timestamp(now())*1000,unix_timestamp(now())* 1000);

-- 运动类型、所有时区新增
delete from system_item_dict where parent_type_id = 7 and `value` = 20;
INSERT INTO system_item_dict (parent_type_id , `code` , `value` , active , description , addition1 , remark , create_time , modify_time )
VALUES ( 7, '冠军', '20', 1, '冠军', '0', '冠军', unix_timestamp(now())*1000, unix_timestamp(now())*1000);
COMMIT;

-- 1.足球
delete from standard_sport_market_category where sport_id = 1 and market_category_id in (10001,10002,10003,10004,10005,10006,10007,10008,10009,10010,10011,10012);
INSERT INTO  standard_sport_market_category (sport_id,market_category_id , name_code, desc_name_code, is_collapse, scope_id, `status`, order_no, create_time, modify_time ) VALUES
(1,10001,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 1, '20', 1, 1000, unix_timestamp(now())*1000,unix_timestamp(now())* 1000),
(1,10002,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 1, '20', 1, 1007, unix_timestamp(now())*1000,unix_timestamp(now())* 1000),
(1,10003,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 1, '20', 1, 1002, unix_timestamp(now())*1000,unix_timestamp(now())* 1000),
(1,10004,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 1, '20', 1, 1003, unix_timestamp(now())*1000,unix_timestamp(now())* 1000),
(1,10005,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 0, '20', 1, 1004, unix_timestamp(now())*1000,unix_timestamp(now())* 1000),
(1,10006,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 1, '20', 1, 1005, unix_timestamp(now())*1000,unix_timestamp(now())* 1000),
(1,10007,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 1, '20', 1, 1006, unix_timestamp(now())*1000,unix_timestamp(now())* 1000),
(1,10008,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 0, '20', 1, 1008, unix_timestamp(now())*1000,unix_timestamp(now())* 1000),
(1,10009,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 0, '20', 1, 1009, unix_timestamp(now())*1000,unix_timestamp(now())* 1000),
(1,10010,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 1, '20', 1, 1010, unix_timestamp(now())*1000,unix_timestamp(now())* 1000),
(1,10011,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 1, '20', 1, 1011, unix_timestamp(now())*1000,unix_timestamp(now())* 1000),
(1,10012,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 1, '20', 1, 1001, unix_timestamp(now())*1000,unix_timestamp(now())* 1000);
COMMIT;

-- 2.篮球
delete from standard_sport_market_category where sport_id = 2 and market_category_id in (10001,10013,10014);
INSERT INTO standard_sport_market_category (sport_id, market_category_id, name_code, desc_name_code, is_collapse, scope_id, `status`, order_no, create_time, modify_time ) VALUES
(2,10001,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 1, '20', 1, 1000, unix_timestamp(now())*1000,unix_timestamp(now())* 1000),
(2,10013,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 1, '20', 1, 1001, unix_timestamp(now())*1000,unix_timestamp(now())* 1000),
(2,10014,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 1, '20', 1, 1002, unix_timestamp(now())*1000,unix_timestamp(now())* 1000);
COMMIT;


-- 105.政治娱乐
delete from standard_sport_market_category where sport_id = 105 and market_category_id in (10015,10016,10017);
delete from standard_sport_market_category where sport_id = 18 and market_category_id in (10015,10016,10017);
INSERT INTO  standard_sport_market_category (sport_id, market_category_id, name_code, desc_name_code, is_collapse, scope_id, `status`, order_no, create_time, modify_time ) VALUES
(18,10015,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 1, '20', 1, 3, unix_timestamp(now())*1000,unix_timestamp(now())* 1000),
(18,10016,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 1, '20', 1, 1, unix_timestamp(now())*1000,unix_timestamp(now())* 1000),
(18,10017,CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), CONCAT(UNIX_TIMESTAMP(), CEILING(RAND()*900000000+100000000)), 0, '20', 1, 2, unix_timestamp(now())*1000,unix_timestamp(now())* 1000);
COMMIT;



################################i18n_market_category_冠军################################
-- 新增标准冠军玩法name_code
INSERT INTO i18n_market_category (name_code, flag, data_source_code, language_type, text, remark, create_time, modify_time)
SELECT
  standard_t.name_code,
	i18n_t.flag,
	i18n_t.data_source_code,
	i18n_t.language_type,i18n_t.text,i18n_t.remark,unix_timestamp(now()) * 1000 ,unix_timestamp(now()) * 1000
FROM
  i18n_market_category i18n_t,
	standard_market_category standard_t,
	third_market_category third_t
WHERE
	third_t.reference_id = standard_t.id
	and third_t.data_source_code = 'SR'
	and i18n_t.name_code = third_t.name_code
	AND standard_t.id > 10000;
COMMIT;


-- 新增标准赛种表name_code
INSERT INTO i18n_market_category (name_code, flag, data_source_code, language_type, text, remark, create_time, modify_time)
SELECT
  sport_t.name_code,
	i18n_t.flag,
	i18n_t.data_source_code,
	i18n_t.language_type,i18n_t.text,i18n_t.remark,unix_timestamp(now()) * 1000 ,unix_timestamp(now()) * 1000
FROM
  i18n_market_category i18n_t,
	standard_market_category standard_t,
	standard_sport_market_category sport_t
WHERE
	sport_t.market_category_id = standard_t.id
	and i18n_t.name_code = standard_t.name_code
	and standard_t.id > 10000;
COMMIT;

-- 新增标准赛种表desc_name_code
INSERT INTO i18n_market_category (name_code, flag, data_source_code, language_type, text, remark, create_time, modify_time)
SELECT
  sport_t.desc_name_code,
	i18n_t.flag,
	i18n_t.data_source_code,
	i18n_t.language_type,i18n_t.text,i18n_t.remark,unix_timestamp(now()) * 1000 ,unix_timestamp(now()) * 1000
FROM
  i18n_market_category i18n_t,
	standard_market_category standard_t,
	standard_sport_market_category sport_t
WHERE
	sport_t.market_category_id = standard_t.id
	and i18n_t.name_code = standard_t.name_code
	and standard_t.id > 10000;
COMMIT;

################################玩法脚本补充################################
update third_market_category_field set reference_id = 784 where third_source_id = 'BG:8258:32';
update third_market_category_field set reference_id = 783 where third_source_id = 'BG:8258:33';

update third_market_category_field set reference_id = 784 where third_source_id = 'BG:8257:32';
update third_market_category_field set reference_id = 783 where third_source_id = 'BG:8257:33';

update third_market_category_field set reference_id = 784 where third_source_id = 'BG:12540:32';
update third_market_category_field set reference_id = 783 where third_source_id = 'BG:12540:33';

update third_market_category_field set reference_id = 481 where third_source_id = 'BG:6734:3';
update third_market_category_field set reference_id = 480 where third_source_id = 'BG:6734:1';

update third_market_category_field set reference_id = 499 where third_source_id = 'BG:11478:3';
update third_market_category_field set reference_id = 498 where third_source_id = 'BG:11478:1';

update third_market_category_field set reference_id = 780 where third_source_id = 'BG:11637:5';
update third_market_category_field set reference_id = 779 where third_source_id = 'BG:11637:4';

update third_market_category_field set reference_id = 499 where third_source_id = 'BG:7013:3';
update third_market_category_field set reference_id = 498 where third_source_id = 'BG:7013:1';

update third_market_category_field set reference_id = 48 where third_source_id = 'BG:202:2';
update third_market_category_field set reference_id = 49 where third_source_id = 'BG:202:3';

update third_market_category_field set reference_id = 150 where third_source_id = 'BC:6614:7854';
update third_market_category_field set reference_id = 149 where third_source_id = 'BC:6614:7855';

update third_market_category_field set reference_id = 126 where third_source_id = 'BC:6551:6471';
update third_market_category_field set reference_id = 125 where third_source_id = 'BC:6551:6472';

update third_market_category_field set reference_id = 478 where third_source_id = 'BC:9007:10006';
update third_market_category_field set reference_id = 479 where third_source_id = 'BC:9007:10007';

update standard_market_category set modify_time = (UNIX_TIMESTAMP() * 1000 + 999999);
COMMIT;

update third_market_category set reference_id = 153, modify_time = unix_timestamp(now()) * 1000 where third_source_id = 'BG:6750';
update third_market_category set reference_id = 172, modify_time = unix_timestamp(now()) * 1000 where third_source_id = 'BG:12797';
update third_market_category_field set reference_id = 545, modify_time = UNIX_TIMESTAMP() * 1000 where third_source_id = 'BG:12797:25';
update third_market_category_field set reference_id = 546, modify_time = UNIX_TIMESTAMP() * 1000 where third_source_id = 'BG:12797:26';
update third_market_category_field set reference_id = 480, modify_time = UNIX_TIMESTAMP() * 1000 where third_source_id = 'BG:6750:25';
update third_market_category_field set reference_id = 481, modify_time = UNIX_TIMESTAMP() * 1000 where third_source_id = 'BG:6750:26';
update third_market_category set reference_id = 0, modify_time = UNIX_TIMESTAMP() * 1000 where third_source_id in ('BC:8943','BC:8942','BC:8934','BC:8945','BC:8944');

-- 政治娱乐赛种配置
delete from third_sport_type where name_code = 105 and third_sport_id = '18' and data_source_code = 'SR';
delete from third_sport_type where name_code = 105 and third_sport_id = '89' and data_source_code = 'BC';
delete from third_sport_type where name_code = 105 and third_sport_id = '91' and data_source_code = 'BC';
delete from third_sport_type where name_code = 105 and third_sport_id = '92' and data_source_code = 'BC';
delete from standard_sport_type where id = 18;
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`) VALUES (18, '18', 'SR',18, '政治娱乐', '政治娱乐', 1564997479754, 1564997479754);
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`) VALUES (18, '89', 'BC',18, '政治选举', '政治选举', 1564997479754, 1564997479754);
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`) VALUES (18, '91', 'BC',18, '娱乐', '娱乐', 1564997479754, 1564997479754);
INSERT INTO `panda`.`third_sport_type`(`name_code`, `third_sport_id`, `data_source_code`, `reference_id`, `introduction`, `remark`, `create_time`, `modify_time`) VALUES (18, '92', 'BC',18, '奥斯卡', '奥斯卡', 1564997479754, 1564997479754);
INSERT INTO `panda`.`standard_sport_type`(`id`, `name_code`, `introduction`, `remark`, `create_time`, `modify_time`) VALUES (18, 18, '政治娱乐', '政治娱乐', 1564997479754, 1564997479754);

-- 删除之前的表
drop table if exists i18n_outright_market;
drop table if exists i18nnames_outright_category_name;
drop table if exists i18nnames_outright_match_name;
drop table if exists standard_outright_match_category;
drop table if exists standard_outright_match_info;
drop table if exists third_outright_match_info;

-- 冠军赛事创建表
CREATE TABLE `i18n_outright_market` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name_code` bigint(20) DEFAULT '0' COMMENT '文字对应的编码',
  `flag` tinyint(4) DEFAULT '2' COMMENT '1 人工  2 系统',
  `data_source_code` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '数据来源编码. SR BC等',
  `language_type` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '语言类型. zh jp en 等',
  `text` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '文字内容.  ',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '',
  `create_time` bigint(20) DEFAULT '0',
  `modify_time` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_uq_source_namecode` (`data_source_code`,`language_type`,`name_code`) USING BTREE,
  KEY `idx_namecodeand` (`name_code`,`data_source_code`) USING BTREE,
  KEY `idx_namecode` (`name_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1320903176074870977 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='冠军盘口多语言';

CREATE TABLE `i18nnames_outright_match_name` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `match_category_filed` bigint(255) DEFAULT NULL COMMENT '玩法 赛事  投注项',
  `type` tinyint(4) DEFAULT NULL COMMENT '1  三方赛事  2 标准赛事  3 投注项',
  `data_source_code` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '数据源  PA  SR BG',
  `language_type` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '语言类型',
  `text` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '值',
  `flag` tinyint(4) DEFAULT NULL COMMENT ' 1 人工  2 系统',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `modfiy_time` bigint(20) DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `index_match_category_filed` (`match_category_filed`) USING BTREE,
  KEY `index_data_source_code` (`data_source_code`) USING BTREE,
  KEY `index_match_category_filed_type` (`match_category_filed`,`type`)
) ENGINE=InnoDB AUTO_INCREMENT=74823 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='冠军赛事及投注项国际化表';

CREATE TABLE `outright_match_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `operate_target_id` bigint(20) DEFAULT NULL COMMENT '操作目标id，赛事id,盘口id...',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人id',
  `operator_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '操作人名称',
  `operator_modle` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '操作模块',
  `operator_number` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '操作批次编号(uuid)',
  `operator_text` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '操作内容',
  `operator_time` bigint(20) DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='冠军玩法操作日志表';

CREATE TABLE `standard_outright_market` (
  `id` bigint(20) NOT NULL COMMENT '标准盘口id',
  `standard_match_id` bigint(20) DEFAULT NULL COMMENT '标准赛事id',
  `market_category_id` bigint(20) DEFAULT NULL COMMENT '标准玩法id',
  `market_sell_status` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT 'Unsold' COMMENT ' 盘口开售状态 Sold 开售 Unsold 未售',
  `name_code` bigint(20) DEFAULT NULL COMMENT '盘口国际信息',
  `market_status` tinyint(4) DEFAULT NULL COMMENT '标准盘口状态',
  `next_closing_time` bigint(20) DEFAULT '0' COMMENT '下次封盘时间',
  `link_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `create_time` bigint(20) DEFAULT '0' COMMENT '创建时间',
  `modfiy_time` bigint(20) DEFAULT '0' COMMENT '修改时间',
  UNIQUE KEY `standard_match_category_uq_index` (`id`,`standard_match_id`),
  KEY `index_standard_match_id` (`standard_match_id`) USING BTREE COMMENT '标准冠军赛事id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='标准冠军赛事盘口表';

CREATE TABLE `standard_outright_match_info` (
  `id` bigint(20) NOT NULL COMMENT '主键id',
  `sport_id` bigint(20) DEFAULT '0' COMMENT '赛种id',
  `region_id` bigint(20) DEFAULT NULL COMMENT '区域id',
  `standard_tournament_id` bigint(20) DEFAULT NULL COMMENT '标准联赛id',
  `standard_outright_name_en` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '冠军赛事中文名称',
  `standard_outright_name_cn` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '冠军赛事英文名称',
  `data_source_code` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '数据源',
  `match_market_status` tinyint(4) DEFAULT '-1' COMMENT '赛事开关封锁 -1 未开 0 :开、2:关、1:封、11',
  `standard_outright_manager_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '冠军赛事管理id',
  `third_outright_match_id` bigint(20) DEFAULT '0' COMMENT '三方冠军赛事id',
  `third_outright_match_source_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '三方冠军赛事源id',
  `standrd_outright_match_begion_time` bigint(20) DEFAULT '0' COMMENT '标准冠军赛事开始时间',
  `standrd_outright_match_end_time` bigint(20) DEFAULT '0' COMMENT '标准冠军赛事结束时间',
  `sell_status` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT 'Unsold' COMMENT '冠军赛事开售状态 Sold 开售 Unsold 未售',
  `auto_sell_status` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '是否自动开售新盘口 Yes  是 No 否',
  `season_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '赛季id',
  `standard_outright_year` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '标准冠军赛事赛季名称',
  `booked` tinyint(4) DEFAULT NULL COMMENT '是否订阅  0 未订阅  1已订阅',
  `remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '备注',
  `create_time` bigint(20) DEFAULT '0' COMMENT '新增时间',
  `modify_time` bigint(20) DEFAULT '0' COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_uinque_standard_champion_manager_id` (`standard_outright_manager_id`) USING BTREE COMMENT '管理id',
  UNIQUE KEY `index_uinque_third_champion_match_source_id` (`third_outright_match_source_id`) USING BTREE COMMENT '三方源id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='标准冠军赛事表';

CREATE TABLE `third_outright_match_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `sport_id` bigint(20) DEFAULT '0' COMMENT '赛种',
  `region_id` bigint(20) DEFAULT '0' COMMENT '区域id',
  `tournament_id` bigint(20) DEFAULT '0' COMMENT '三方联赛id',
  `third_match_name_en` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '三方冠军赛事英文',
  `third_match_name_cn` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '三方冠军赛事中文',
  `data_source_code` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '数据源  sr, bc ,bg',
  `third_outright_begin_time` bigint(20) DEFAULT '0' COMMENT '三方冠军赛事开始时间',
  `third_outright_end_time` bigint(20) DEFAULT '0' COMMENT '三方冠军赛事结束时间',
  `third_outright_source_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '三方冠军赛事源id',
  `standard_outright_manager_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '标准管理id',
  `reference_id` bigint(20) DEFAULT '0' COMMENT '标准冠军赛id',
  `season_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '赛季id',
  `third_outright_year` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '三方冠军赛事赛季名称',
  `booked` int(11) DEFAULT NULL COMMENT '是否订阅 0 未订阅  1 已订阅',
  `remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '备注',
  `modify_time` bigint(20) DEFAULT NULL COMMENT '修改时间',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_uinque_third_champion_source_id` (`third_outright_source_id`) USING BTREE COMMENT '三方冠军赛事源id',
  KEY `index_standard_champion_id` (`reference_id`) USING BTREE COMMENT '标准冠军赛事id',
  KEY `index_third_champion_source_id` (`tournament_id`) USING BTREE COMMENT '三方联赛id'
) ENGINE=InnoDB AUTO_INCREMENT=1321164807740674051 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='三方冠军赛事表';



-- 篮球相关
CREATE TABLE `config_market_category_head` (
  `id` bigint(22) NOT NULL AUTO_INCREMENT,
  `standard_match_info_id` bigint(20) DEFAULT NULL COMMENT '标准比赛ID   standard_match_info.id',
  `standard_category_id` bigint(22) DEFAULT NULL COMMENT '标准玩法 ID',
  `market_type` tinyint(4) DEFAULT NULL COMMENT '盘口类型. 属于赛前盘或者滚球盘. 1: 赛前盘; 0: 滚球盘.',
  `market_head_gap` double(22,2) DEFAULT NULL COMMENT '盘口差',
  `link_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '日志id',
  `create_time` bigint(20) DEFAULT '0' COMMENT '创建时间',
  `modify_time` bigint(20) DEFAULT '0' COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `config_market_head_matchid` (`standard_match_info_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='盘口玩法盘口差配置表';

CREATE TABLE `config_market_category_head_log` (
  `id` bigint(22) NOT NULL AUTO_INCREMENT,
  `standard_match_info_id` bigint(20) DEFAULT NULL COMMENT '标准比赛ID   standard_match_info.id',
  `standard_category_id` bigint(22) DEFAULT NULL COMMENT '标准玩法 ID',
  `market_type` tinyint(4) DEFAULT NULL COMMENT '盘口类型. 属于赛前盘或者滚球盘. 1: 赛前盘; 0: 滚球盘.',
  `market_head_gap` double(22,2) DEFAULT NULL COMMENT '盘口差',
  `link_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '日志id',
  `create_time` bigint(20) DEFAULT '0' COMMENT '创建时间',
  `modify_time` bigint(20) DEFAULT '0' COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `config_market_head_log_matchid` (`standard_match_info_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='盘口玩法盘口差配置日志表';

-- 最大最小值增加盘口位置字段
ALTER table  config_market_trade_item add place_num  int DEFAULT 0 COMMENT '盘口位置' after market_category_id;
ALTER table  config_market_trade_item_log add place_num  int DEFAULT 0 COMMENT '盘口位置' after market_category_id;

update third_market_category_field set reference_id  = 480, modify_time = UNIX_TIMESTAMP() * 1000 where third_source_id  = 'BG:6750:25';
update third_market_category_field set reference_id  = 481, modify_time = UNIX_TIMESTAMP() * 1000 where third_source_id  = 'BG:6750:26';


ALTER TABLE config_trade_market_log ADD COLUMN MATCH_TYPE varchar(32) NULL AFTER TARGET_ID;

ALTER TABLE sport_market_relation
MODIFY COLUMN market_relation_key varchar(225) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL AFTER `id`;


-- 给最大最小值设置表创建索引
ALTER TABLE config_market_trade_item ADD INDEX index_match_id (match_id);
ALTER TABLE config_market_trade_item ADD INDEX index_market_category_id (market_category_id);
-- 给margin配置表创建索引
ALTER TABLE config_market_category_margin ADD INDEX index_standard_match_info_id (standard_match_info_id);
ALTER TABLE config_market_category_margin ADD INDEX index_standard_category_id (standard_category_id);
-- 给水差表创建索引
ALTER TABLE config_market_auto_diff_trade ADD INDEX index_standard_match_id (standard_match_id);

drop table if exists i18n_market_odds;
CREATE TABLE `i18n_market_odds` (
  `odds_source_id` varchar(225) COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '三方投注项源id',
  `data_source_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '数据源编码',
  `language_type` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '语言类型. zh jp en 等',
  `text` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '文字内容.  ',
  `create_time` bigint(20) DEFAULT '0',
  `modify_time` bigint(20) DEFAULT '0',
  UNIQUE KEY `idx_odds_id_source_type` (`odds_source_id`,`language_type`,`data_source_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='投注项多语言';




-- 泰森排行榜单相关脚本开始
-- 三方联赛表表添加三方数据源赛季ID字段
alter table third_sport_tournament add column `third_season_source_id` varchar(50) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '三方数据源当前赛季ID';

-- ----------------------------
-- Table structure for third_sport_player_ranking
-- ----------------------------
DROP TABLE IF EXISTS `third_sport_player_ranking`;
CREATE TABLE `third_sport_player_ranking`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT 'ID(三方数据源赛季ID+榜单类型+榜单序号)',
  `third_tournament_source_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '三方数据源联赛ID',
  `sport_id` bigint(20) NOT NULL COMMENT '运动类型',
  `third_source_season_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '三方数据源赛季ID',
  `third_source_season_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '三方数据源赛季名称',
  `third_source_season_begin_time` datetime(0) NULL DEFAULT NULL COMMENT '三方数据源赛季开始时间',
  `third_source_season_end_time` datetime(0) NULL DEFAULT NULL COMMENT '三方数据源赛季结束始时间',
  `match_count` int(11) NOT NULL COMMENT '参数场数',
  `ranking_value` int(11) NOT NULL COMMENT '榜单值',
  `ranking_sort` int(11) NOT NULL COMMENT '榜单序号',
  `ranking_type` int(11) NOT NULL COMMENT '榜单类型（足球类：1 射手榜,24 助攻榜 |   篮球类：24助攻榜,59盖帽榜,60得分榜,61篮板榜,62抢断榜,63技术犯规榜,64失误榜,65投篮次数榜,66进球次数榜,67效率榜,69二分命中数榜,70二分投球次数榜,71三分命中数榜,72三分投中次数榜,73罚中次数榜,74罚球次数榜）',
  `third_team_source_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '三方数据源球队ID',
  `team_cn_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '球队中文名称',
  `team_en_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '球队英文名称',
  `team_logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '球队logo',
  `third_player_source_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '三方数据源球员ID',
  `player_cn_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '球员中文名称',
  `player_en_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '球员英文名称',
  `player_logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '球员logo',
  `modify_time` bigint(20) NOT NULL COMMENT '修改时间',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_uq_tour`(`third_tournament_source_id`, `sport_id`) USING BTREE,
  INDEX `idx_season_id`(`third_source_season_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '联赛下球员排行榜（泰森独有）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for third_sport_team_ranking
-- ----------------------------
DROP TABLE IF EXISTS `third_sport_team_ranking`;
CREATE TABLE `third_sport_team_ranking`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '三方数据源赛季ID+榜单ID+球队ID',
  `third_tournament_source_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '三方数据源联赛ID',
  `sport_id` bigint(20) NOT NULL COMMENT '运动类型',
  `third_source_season_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '三方数据源赛季ID',
  `third_source_season_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '三方数据源赛季名称',
  `third_source_season_begin_time` datetime(0) NULL DEFAULT NULL COMMENT '三方数据源赛季开始时间',
  `third_source_season_end_time` datetime(0) NULL DEFAULT NULL COMMENT '三方数据源赛季结束始时间',
  `ranking_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '榜单ID',
  `ranking_cn_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '榜单中文名称',
  `ranking_en_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '榜单英文名称',
  `match_count` int(11) NULL DEFAULT NULL COMMENT '参数场数',
  `third_team_source_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '三方数据源球队ID',
  `team_cn_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '球队中文名称',
  `team_en_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '球队英文名称',
  `team_logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '球队logo',
  `position_total` int(11) NOT NULL COMMENT '排名值',
  `win_total` int(11) NOT NULL COMMENT '胜场数',
  `draw_total` int(11) NOT NULL COMMENT '平局数',
  `loss_total` int(11) NOT NULL COMMENT '负场数',
  `points_total` int(11) NOT NULL COMMENT '积分数',
  `goals_for_total` int(11) NOT NULL COMMENT '进球数',
  `goals_against_total` int(11) NOT NULL COMMENT '失球数',
  `modify_time` bigint(20) NOT NULL COMMENT '修改时间',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_uq_tour`(`third_tournament_source_id`, `sport_id`) USING BTREE,
  INDEX `idx_season_id`(`third_source_season_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '联赛下球队积分排行榜（泰森独有）' ROW_FORMAT = Dynamic;
-- 泰森排行榜单相关脚本结束

#################2020年10月28日已发预发布end##############


-- noah增加脚本
UPDATE market_category_template_relation
SET dynamic_flag = 2
WHERE market_category_id = 180 and template_id = 47 and sport_id = 7 LIMIT 1;

UPDATE match_event_template SET template_no = 1 WHERE id = 30;

UPDATE match_event_template SET event_code = 'ball_pot' WHERE id = 41;


UPDATE system_item_dict
SET description = 'SET1'
WHERE parent_type_id = 8 and value = '800' and addition1 = '5' LIMIT 1;

UPDATE system_item_dict
SET description = 'SET2'
WHERE parent_type_id = 8 and value = '900' and addition1 = '5' LIMIT 1;

UPDATE system_item_dict
SET description = 'SET3'
WHERE parent_type_id = 8 and value = '1000' and addition1 = '5' LIMIT 1;

UPDATE system_item_dict
SET description = 'SET4'
WHERE parent_type_id = 8 and value = '1100' and addition1 = '5' LIMIT 1;

UPDATE system_item_dict
SET description = 'SET5'
WHERE parent_type_id = 8 and value = '1200' and addition1 = '5' LIMIT 1;



#################2020年10月30日已发 补充羽毛球模板基础数据修正脚本##############
UPDATE match_event_template_period
SET trigger_period_id = 100
WHERE template_id = 33 AND trigger_period_id = 301 LIMIT 1;

UPDATE match_event_template_period
SET trigger_period_id = 301
WHERE template_id = 34 AND trigger_period_id = 12 LIMIT 1;

#################2020年11月11日已发 ##############
ALTER TABLE standard_sport_market_odds ADD COLUMN standard_match_id BIGINT (20) NOT NULL DEFAULT 0 COMMENT '赛事ID,standard_match_info.id' , ALGORITHM=INSTANT;
ALTER TABLE third_sport_market_odds ADD COLUMN third_match_id BIGINT (20) NOT NULL DEFAULT 0 COMMENT '赛事ID,third_match_info.id', ALGORITHM=INSTANT;

##############2020年11月19日已发#################
UPDATE match_event_template
SET template_text = '加时赛结束，比分M:N', template_format = '加时赛结束，比分%s:%s' WHERE id = 24 LIMIT 1;

UPDATE match_event_template
SET template_text = '全场结束，全场比分(含加时)M:N', template_format = '全场结束，全场比分(含加时)%s:%s' WHERE id = 50 LIMIT 1;

######################20201203start################################
DROP TABLE IF EXISTS `config_market_status_trade`;
CREATE TABLE `config_market_status_trade` (
  `id` bigint(22) NOT NULL,
	`relation_market_id` bigint(32) DEFAULT '0' COMMENT '标准盘口id',
  `standard_match_info_id` bigint(32) DEFAULT '0' COMMENT '标准赛事id',
  `standard_category_id` bigint(16)  DEFAULT '0' COMMENT '标准玩法id',
  `market_type` tinyint(2)  COMMENT '盘口类型',
  `addtion` varchar(10) DEFAULT NULL COMMENT '盘口值',
	`market_status` tinyint(2) DEFAULT 12 COMMENT '盘口状态，12-弃用，其他-开启',
	`link_id` varchar(64) DEFAULT NULL COMMENT '操作日志id',
  `modify_time` bigint(20) DEFAULT '0' COMMENT '配置修改时间',
  `create_time` bigint(20) DEFAULT '0' COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE  INDEX  `idx_market_status_id` (`relation_market_id`,`market_type`),
  KEY `index_standard_match_id` (`standard_match_info_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='操盘后台操作盘口状态，弃用或开启';

-- bevan增加操盘配置记录操作人ID脚本
ALTER TABLE config_market_category_place ADD COLUMN `operater_id` BIGINT ( 22 ) DEFAULT '0' COMMENT '操作人ID';

ALTER TABLE config_market_trade_item ADD COLUMN `operater_id` BIGINT ( 22 ) DEFAULT '0' COMMENT '操作人ID';
ALTER TABLE config_market_trade_item_log ADD COLUMN `operater_id` BIGINT ( 22 ) DEFAULT '0' COMMENT '操作人ID';

ALTER TABLE config_market_category_margin ADD COLUMN `operater_id` BIGINT ( 22 ) DEFAULT '0' COMMENT '操作人ID';
ALTER TABLE config_market_category_margin_log ADD COLUMN `operater_id` BIGINT ( 22 ) DEFAULT '0' COMMENT '操作人ID';

ALTER TABLE config_market_auto_diff_trade ADD COLUMN `operater_id` BIGINT ( 22 ) DEFAULT '0' COMMENT '操作人ID';
ALTER TABLE config_market_auto_diff_trade_log ADD COLUMN `operater_id` BIGINT ( 22 ) DEFAULT '0' COMMENT '操作人ID';

ALTER TABLE config_market_category_head ADD COLUMN `operater_id` BIGINT ( 22 ) DEFAULT '0' COMMENT '操作人ID';
ALTER TABLE config_market_category_head_log ADD COLUMN `operater_id` BIGINT ( 22 ) DEFAULT '0' COMMENT '操作人ID';

ALTER TABLE config_market_status_trade ADD COLUMN `operater_id` BIGINT ( 22 ) DEFAULT '0' COMMENT '操作人ID';

ALTER TABLE config_trade_type ADD COLUMN `operater_id` BIGINT ( 22 ) DEFAULT '0' COMMENT '操作人ID';

ALTER TABLE match_data_source_weight ADD COLUMN `operater_id` BIGINT ( 22 ) DEFAULT '0' COMMENT '操作人ID';
##########################20201203end############################

##########################20201217 bevan add start############################
ALTER TABLE config_market_display_trade DROP INDEX idx_standard_match_id;
CREATE UNIQUE INDEX idx_standard_match_id ON config_market_display_trade ( standard_match_id );
##########################20201217end############################