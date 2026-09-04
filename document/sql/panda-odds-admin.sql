
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

ALTER TABLE market_category_sell
ADD COLUMN link_id varchar(100) NULL AFTER is_sell;

ALTER TABLE third_sport_market_odds
ADD COLUMN `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '投注项名称' AFTER `name_code`;

ALTER TABLE config_trade_market
ADD COLUMN MATCH_TYPE varchar(32) NULL COMMENT '赛事类型：0.普通赛事、1.冠军赛事' AFTER TARGET_ID;

-- 测试环境，隔离环境已执行
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

################################1start##########################
DROP TABLE IF EXISTS `config_placenum_auto_diff_trade`;
CREATE TABLE `config_placenum_auto_diff_trade` (
  `id` bigint(22) NOT NULL,
  `standard_match_id` bigint(22) DEFAULT NULL COMMENT '标准赛事id',
  `standard_category_id` bigint(22) DEFAULT NULL COMMENT '标准玩法id',
  `place_num` int(4) DEFAULT NULL COMMENT '坑位值',
  `odds_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '投注项类型',
  `diff_value` double(22,4) DEFAULT NULL,
  `link_id` varchar(64) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '日志id',
  `create_time` bigint(22) DEFAULT NULL COMMENT '创建时间',
  `modify_time` bigint(22) DEFAULT NULL COMMENT '修改时间',
  `operater_id` bigint(22) DEFAULT '0' COMMENT '操作人ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_placenum_auto_diff` (`standard_match_id`,`standard_category_id`,`place_num`),
  KEY `index_standard_match_id` (`standard_match_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='坑位自动水差配置信息';

DROP TABLE IF EXISTS `config_category_auto_diff_trade`;
CREATE TABLE `config_category_auto_diff_trade` (
  `id` bigint(22) NOT NULL,
  `standard_match_id` bigint(22) DEFAULT NULL COMMENT '标准赛事id',
  `standard_category_id` bigint(22) DEFAULT NULL COMMENT '标准玩法id',
  `odds_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '投注项类型',
  `diff_value` double(22,4) DEFAULT NULL,
  `link_id` varchar(64) COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '日志id',
  `create_time` bigint(22) DEFAULT NULL COMMENT '创建时间',
  `modify_time` bigint(22) DEFAULT NULL COMMENT '修改时间',
  `operater_id` bigint(22) DEFAULT '0' COMMENT '操作人ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_placenum_auto_diff` (`standard_match_id`,`standard_category_id`),
  KEY `index_standard_match_id` (`standard_match_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='玩法水差配置信息';
################################1end##########################

ALTER TABLE standard_sport_market_odds CHANGE active active TINYINT(4) NULL DEFAULT '1' COMMENT '投注项状态： 0未激活(锁盘)、1激活、2投注项封盘';

#################玩法切换记录入库#####################
DROP TABLE IF EXISTS `category_datasourcecode_change`;
CREATE TABLE `category_datasourcecode_change`  (
  `id` bigint(22) NOT NULL AUTO_INCREMENT,
  `standard_match_id` bigint(22) NULL DEFAULT NULL COMMENT '标准赛事id',
  `standard_category_id` bigint(22) NULL DEFAULT NULL COMMENT '标准玩法id',
  `market_type` tinyint(2) NULL DEFAULT NULL COMMENT '盘口类型 0:滚球 1:赛前',
  `sell_status` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '开售 Unsold:滚球 Sold:已售',
  `data_source_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '切换后的数据源',
  `modify_time` bigint(20) NULL DEFAULT 0 COMMENT '配置修改时间',
  `create_time` bigint(20) NULL DEFAULT 0 COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_match_id`(`standard_match_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '玩法切换数据源记录' ROW_FORMAT = Dynamic;

#####################700###############################
ALTER TABLE config_market_category_place ADD child_standard_category_id  bigint(22)  COMMENT '子玩法ID';
UPDATE  config_market_category_place set `child_standard_category_id` = `standard_category_id` where `child_standard_category_id` is null;
ALTER TABLE config_market_category_place ADD INDEX index_child_standard_category_id (child_standard_category_id);

ALTER TABLE config_market_trade_item ADD child_standard_category_id  bigint(22) COMMENT '子玩法ID';
UPDATE config_market_trade_item set `child_standard_category_id` = `market_category_id` where `child_standard_category_id` is null;
ALTER TABLE config_market_trade_item ADD INDEX index_child_standard_category_id (child_standard_category_id);

ALTER TABLE config_market_trade_item_log ADD child_standard_category_id  bigint(22) COMMENT '子玩法ID';
UPDATE config_market_trade_item_log set `child_standard_category_id` = `market_category_id` where `child_standard_category_id` is null;
ALTER TABLE config_market_trade_item_log ADD INDEX index_child_standard_category_id (child_standard_category_id);

ALTER TABLE config_market_category_margin ADD child_standard_category_id  bigint(22) COMMENT '子玩法ID';
UPDATE config_market_category_margin set `child_standard_category_id` = `standard_category_id` where `child_standard_category_id` is null;
ALTER TABLE config_market_category_margin ADD INDEX index_child_standard_category_id (child_standard_category_id);

ALTER TABLE config_market_category_margin_log ADD child_standard_category_id  bigint(22) COMMENT '子玩法ID';
UPDATE config_market_category_margin_log set `child_standard_category_id` = `standard_category_id` where `child_standard_category_id` is null;
ALTER TABLE config_market_category_margin_log ADD INDEX index_child_standard_category_id (child_standard_category_id);

ALTER TABLE config_placenum_auto_diff_trade ADD child_standard_category_id  bigint(22) COMMENT '子玩法ID';
UPDATE  config_placenum_auto_diff_trade set `child_standard_category_id` = `standard_category_id` where `child_standard_category_id` is null;
ALTER TABLE `panda`.`config_placenum_auto_diff_trade` DROP INDEX `unique_placenum_auto_diff`,ADD UNIQUE `unique_placenum_auto_diff` ( `standard_match_id`, `standard_category_id`, `child_standard_category_id`, `place_num` ) USING BTREE;

ALTER TABLE config_category_auto_diff_trade ADD child_standard_category_id  bigint(22) COMMENT '子玩法ID';
UPDATE config_category_auto_diff_trade set `child_standard_category_id` = `standard_category_id` where `child_standard_category_id` is null;
ALTER TABLE config_category_auto_diff_trade ADD INDEX index_child_standard_category_id (child_standard_category_id);
ALTER TABLE `panda`.`config_category_auto_diff_trade` DROP INDEX `unique_placenum_auto_diff`, ADD UNIQUE `unique_placenum_auto_diff` (`standard_match_id`, `standard_category_id`, `child_standard_category_id`) USING BTREE;

ALTER TABLE config_market_margin_gap ADD child_standard_category_id  bigint(22) COMMENT '子玩法ID';
UPDATE config_market_margin_gap set `child_standard_category_id` = `market_category_id` where `child_standard_category_id` is null;
ALTER TABLE config_market_margin_gap ADD INDEX index_child_standard_category_id (child_standard_category_id);

ALTER TABLE config_market_margin_gap_log ADD child_standard_category_id  bigint(22) COMMENT '子玩法ID';
UPDATE config_market_margin_gap_log set `child_standard_category_id` = `market_category_id` where `child_standard_category_id` is null;
ALTER TABLE config_market_margin_gap_log ADD INDEX index_child_standard_category_id (child_standard_category_id);

-- 篮球盘口差
ALTER TABLE config_market_category_head ADD child_standard_category_id  bigint(22) COMMENT '子玩法ID';
UPDATE config_market_category_head set `child_standard_category_id` = `standard_category_id` where `child_standard_category_id` is null;

ALTER TABLE config_market_category_head_log ADD child_standard_category_id  bigint(22) COMMENT '子玩法ID';
UPDATE config_market_category_head_log set `child_standard_category_id` = `standard_category_id` where `child_standard_category_id` is null;


-- 历史表数据全部清除
TRUNCATE config_market_category_place_his;
TRUNCATE config_market_category_margin_his;
TRUNCATE config_market_category_margin_log_his;

-- 历史表字段
ALTER TABLE config_market_category_place_his ADD child_standard_category_id  bigint(22)  COMMENT '子玩法ID';
ALTER TABLE config_market_category_margin_his ADD child_standard_category_id  bigint(22)  COMMENT '子玩法ID';
ALTER TABLE config_market_category_margin_log_his ADD child_standard_category_id  bigint(22)  COMMENT '子玩法ID';

--------------三方盘口表---------------------------------
CREATE TABLE `third_sport_market_sr` (
  `id` bigint(20) NOT NULL COMMENT '数据库id, 自增',
  `tournament_id` bigint(20) DEFAULT NULL COMMENT '所属联赛ID   ',
  `match_id` bigint(20) DEFAULT NULL COMMENT '比赛ID:third_match_info.id',
  `market_category_id` bigint(20) DEFAULT NULL COMMENT '第三方玩法id   standard_sport_market_category.id',
  `third_market_source_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '第三提供的id。SR: 报文中有id字段。',
  `reference_id` bigint(20) DEFAULT NULL COMMENT '如果当前盘口与标准盘口中的B记录玩法相同且盘口显示内容相同, 则该记录的当前字段值为B.ID',
  `market_type` tinyint(4) DEFAULT NULL COMMENT '盘口类型. 属于赛前盘或者滚球盘. 1: 赛前盘; 0: 滚球盘. ',
  `data_source_code` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source',
  `status` tinyint(4) DEFAULT '1' COMMENT '盘口状态0-5. 0:active, 1:suspended, 2:deactivated, 3:settled, 4:cancelled, 5:handedOver',
  `scope_id` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '盘口阶段id. 对应 对应 system_item_dict.value',
  `name_code` bigint(20) DEFAULT NULL COMMENT '盘口名称编码. 用于多语言',
  `odds_type_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '玩法的中文名称. 仅用用于数据库操作人员使用. ',
  `third_odds_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '接收到第三方数据后, 可以通过该字段快速定位到当前的盘口. 通过玩法和具体内容确认盘口的唯一性.  SR提供的盘口数据id 生成算法: Type_Typeid_Subtypeid_Specialoddsvalue',
  `odds_value` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '该盘口具体显示的值. 例如: 大小球中, 大小界限是:  3.5',
  `order_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '排序类型',
  `odds_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '盘口名称. ',
  `odds_metric` bigint(10) DEFAULT NULL COMMENT '盘口级别，数字越小优先级越高',
  `addition1` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '附加字段1',
  `addition2` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '附加字段2',
  `addition3` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '附加字段3',
  `addition4` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '附加字段4',
  `addition5` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '附加字段5',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `create_time` bigint(20) DEFAULT NULL,
  `modify_time` bigint(20) DEFAULT NULL,
  `extra_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `third_market_source_status` tinyint(4) DEFAULT NULL COMMENT '三方盘口源状态',
  `offer_line_id` tinyint(4) DEFAULT NULL COMMENT 'TX坑位',
  `number_of_winners` int(11) DEFAULT '1' COMMENT '并列-胜出数',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_datasource_marketsourceid_unique` (`third_market_source_id`,`data_source_code`) USING BTREE,
  KEY `idx_reference_id` (`reference_id`) USING BTREE,
  KEY `idx_source` (`third_market_source_id`) USING BTREE,
  KEY `idx_third_match_id` (`match_id`),
  KEY `idx_modify_time` (`modify_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs ROW_FORMAT=DYNAMIC COMMENT='该表存放 第三方提供的盘口';

INSERT INTO third_sport_market_sr(id,tournament_id,match_id,market_category_id,third_market_source_id,reference_id,market_type,data_source_code,status,scope_id,name_code,odds_type_name,third_odds_type,odds_value,order_type,odds_name,odds_metric,addition1,addition2,addition3,addition4,addition5,remark,create_time,modify_time,extra_info,third_market_source_status,offer_line_id,number_of_winners )
select id,tournament_id,match_id,market_category_id,third_market_source_id,reference_id,market_type,data_source_code,status,scope_id,name_code,odds_type_name,third_odds_type,odds_value,order_type,odds_name,odds_metric,addition1,addition2,addition3,addition4,addition5,remark,create_time,modify_time,extra_info,third_market_source_status,offer_line_id,number_of_winners
from third_sport_market where data_source_code = 'SR';
INSERT INTO third_sport_market_bc(id,tournament_id,match_id,market_category_id,third_market_source_id,reference_id,market_type,data_source_code,status,scope_id,name_code,odds_type_name,third_odds_type,odds_value,order_type,odds_name,odds_metric,addition1,addition2,addition3,addition4,addition5,remark,create_time,modify_time,extra_info,third_market_source_status,offer_line_id,number_of_winners )
select id,tournament_id,match_id,market_category_id,third_market_source_id,reference_id,market_type,data_source_code,status,scope_id,name_code,odds_type_name,third_odds_type,odds_value,order_type,odds_name,odds_metric,addition1,addition2,addition3,addition4,addition5,remark,create_time,modify_time,extra_info,third_market_source_status,offer_line_id,number_of_winners
from third_sport_market where data_source_code = 'BC';
INSERT INTO third_sport_market_bg(id,tournament_id,match_id,market_category_id,third_market_source_id,reference_id,market_type,data_source_code,status,scope_id,name_code,odds_type_name,third_odds_type,odds_value,order_type,odds_name,odds_metric,addition1,addition2,addition3,addition4,addition5,remark,create_time,modify_time,extra_info,third_market_source_status,offer_line_id,number_of_winners )
select id,tournament_id,match_id,market_category_id,third_market_source_id,reference_id,market_type,data_source_code,status,scope_id,name_code,odds_type_name,third_odds_type,odds_value,order_type,odds_name,odds_metric,addition1,addition2,addition3,addition4,addition5,remark,create_time,modify_time,extra_info,third_market_source_status,offer_line_id,number_of_winners
from third_sport_market where data_source_code = 'BG';
INSERT INTO third_sport_market_tx(id,tournament_id,match_id,market_category_id,third_market_source_id,reference_id,market_type,data_source_code,status,scope_id,name_code,odds_type_name,third_odds_type,odds_value,order_type,odds_name,odds_metric,addition1,addition2,addition3,addition4,addition5,remark,create_time,modify_time,extra_info,third_market_source_status,offer_line_id,number_of_winners )
select id,tournament_id,match_id,market_category_id,third_market_source_id,reference_id,market_type,data_source_code,status,scope_id,name_code,odds_type_name,third_odds_type,odds_value,order_type,odds_name,odds_metric,addition1,addition2,addition3,addition4,addition5,remark,create_time,modify_time,extra_info,third_market_source_status,offer_line_id,number_of_winners
from third_sport_market where data_source_code = 'TX';
INSERT INTO third_sport_market_pa(id,tournament_id,match_id,market_category_id,third_market_source_id,reference_id,market_type,data_source_code,status,scope_id,name_code,odds_type_name,third_odds_type,odds_value,order_type,odds_name,odds_metric,addition1,addition2,addition3,addition4,addition5,remark,create_time,modify_time,extra_info,third_market_source_status,offer_line_id,number_of_winners )
select id,tournament_id,match_id,market_category_id,third_market_source_id,reference_id,market_type,data_source_code,status,scope_id,name_code,odds_type_name,third_odds_type,odds_value,order_type,odds_name,odds_metric,addition1,addition2,addition3,addition4,addition5,remark,create_time,modify_time,extra_info,third_market_source_status,offer_line_id,number_of_winners
from third_sport_market where data_source_code = 'PA';

-------------三方投注项表----------------------
DROP TABLE IF EXISTS `third_sport_market_odds_sr`;
CREATE TABLE `third_sport_market_odds_sr` (
  `id` bigint(20) NOT NULL COMMENT '表ID, 自增',
  `market_id` bigint(20) DEFAULT NULL COMMENT '盘口ID  third_sport_market.id',
  `reference_id` bigint(20) DEFAULT NULL COMMENT '如果当前盘口与标准盘口中的B记录玩法相同且盘口显示内容相同, 则该记录的当前字段值为B.ID',
  `active` tinyint(4) DEFAULT NULL COMMENT '当前投注项是否被激活.1激活; 0未激活(锁盘)',
  `settlement_result_text` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '投注项结算结果文本',
  `settlement_result` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '投注项结算结果文本',
  `bet_settlement_certainty` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '赛果已确认: Confirmed, 盘中事件确认: LiveScouted, 未知: Unknown',
  `odds_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '投注项类型',
  `addition1` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '附加字段1',
  `addition2` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '附加字段2',
  `addition3` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '附加字段3',
  `addition4` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '附加字段4',
  `addition5` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '附加字段5',
  `third_odds_field_source_id` varchar(225) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `order_odds` int(11) DEFAULT '0' COMMENT '用于排序, 大于1, 越小越靠前',
  `name_code` bigint(20) DEFAULT NULL COMMENT '名称编码. 用于多语言. 投注项可能有也可能没有该字段. 需要的时候填入',
  `name_expression_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '投注项名称中包含的表达式的值',
  `odds_value` int(11) DEFAULT NULL COMMENT '投注项赔率. 单位: 0.0001',
  `pa_odds_value` int(11) DEFAULT '0' COMMENT '投注项PA赔率. 单位: 0.0001',
  `original_odds_value` int(11) DEFAULT NULL COMMENT '投注项原始赔率. 单位: 0.0001',
  `odds_fields_template_id` bigint(20) DEFAULT '0' COMMENT '标准投注项模板id   standard_market_category_field.id',
  `third_template_source_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '三方投注项模板源ID，third_market_category_field.id',
  `target_side` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '投注给哪一方: T1主队, T2客队',
  `data_source_code` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `create_time` bigint(20) DEFAULT NULL,
  `modify_time` bigint(20) DEFAULT NULL,
  `extra_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `third_match_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '赛事ID,third_match_info.id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_ds_source_id` (`third_odds_field_source_id`,`data_source_code`,`market_id`) USING BTREE,
  KEY `idx_market` (`market_id`) USING BTREE,
  KEY `idx_data_source_code` (`data_source_code`) USING BTREE,
  KEY `idx_reference` (`reference_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs ROW_FORMAT=DYNAMIC COMMENT='第三方赛事盘口投注项表';

INSERT INTO third_sport_market_odds_sr (id,market_id,reference_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,third_odds_field_source_id,order_odds,name_code,name,name_expression_value,odds_value,pa_odds_value,original_odds_value,odds_fields_template_id,third_template_source_id,target_side,data_source_code,remark,create_time,modify_time,extra_info,third_match_id)
SELECT id,market_id,reference_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,third_odds_field_source_id,order_odds,name_code,name,name_expression_value,odds_value,pa_odds_value,original_odds_value,odds_fields_template_id,third_template_source_id,target_side,data_source_code,remark,create_time,modify_time,extra_info,third_match_id from third_sport_market_odds
where data_source_code = 'SR';
INSERT INTO third_sport_market_odds_bg (id,market_id,reference_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,third_odds_field_source_id,order_odds,name_code,name,name_expression_value,odds_value,pa_odds_value,original_odds_value,odds_fields_template_id,third_template_source_id,target_side,data_source_code,remark,create_time,modify_time,extra_info,third_match_id)
SELECT id,market_id,reference_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,third_odds_field_source_id,order_odds,name_code,name,name_expression_value,odds_value,pa_odds_value,original_odds_value,odds_fields_template_id,third_template_source_id,target_side,data_source_code,remark,create_time,modify_time,extra_info,third_match_id from third_sport_market_odds
where data_source_code = 'BG';
INSERT INTO third_sport_market_odds_bc (id,market_id,reference_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,third_odds_field_source_id,order_odds,name_code,name,name_expression_value,odds_value,pa_odds_value,original_odds_value,odds_fields_template_id,third_template_source_id,target_side,data_source_code,remark,create_time,modify_time,extra_info,third_match_id)
SELECT id,market_id,reference_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,third_odds_field_source_id,order_odds,name_code,name,name_expression_value,odds_value,pa_odds_value,original_odds_value,odds_fields_template_id,third_template_source_id,target_side,data_source_code,remark,create_time,modify_time,extra_info,third_match_id from third_sport_market_odds
where data_source_code = 'BC';
INSERT INTO third_sport_market_odds_tx (id,market_id,reference_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,third_odds_field_source_id,order_odds,name_code,name,name_expression_value,odds_value,pa_odds_value,original_odds_value,odds_fields_template_id,third_template_source_id,target_side,data_source_code,remark,create_time,modify_time,extra_info,third_match_id)
SELECT id,market_id,reference_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,third_odds_field_source_id,order_odds,name_code,name,name_expression_value,odds_value,pa_odds_value,original_odds_value,odds_fields_template_id,third_template_source_id,target_side,data_source_code,remark,create_time,modify_time,extra_info,third_match_id from third_sport_market_odds
where data_source_code = 'TX';
INSERT INTO third_sport_market_odds_pa (id,market_id,reference_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,third_odds_field_source_id,order_odds,name_code,name,name_expression_value,odds_value,pa_odds_value,original_odds_value,odds_fields_template_id,third_template_source_id,target_side,data_source_code,remark,create_time,modify_time,extra_info,third_match_id)
SELECT id,market_id,reference_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,third_odds_field_source_id,order_odds,name_code,name,name_expression_value,odds_value,pa_odds_value,original_odds_value,odds_fields_template_id,third_template_source_id,target_side,data_source_code,remark,create_time,modify_time,extra_info,third_match_id from third_sport_market_odds
where data_source_code = 'PA';



------------标准盘口表-----------------
CREATE TABLE `standard_sport_market_0` (
  `id` bigint(20) NOT NULL COMMENT '数据库id, 自增',
  `relation_market_id` bigint(20) DEFAULT NULL,
  `standard_tournament_id` bigint(20) DEFAULT NULL COMMENT '所属联赛ID    standard_sport_tournament.id',
  `standard_match_info_id` bigint(20) DEFAULT NULL COMMENT '标准比赛ID   standard_match_info.id',
  `market_category_id` bigint(20) DEFAULT NULL COMMENT '标准玩法id   standard_sport_market_category.id',
  `market_type` tinyint(4) DEFAULT NULL COMMENT '盘口类型. 属于赛前盘或者滚球盘. 1: 赛前盘; 0: 滚球盘. ',
  `trade_type` tinyint(2) DEFAULT '0' COMMENT '操盘方式：0自动操盘，1手动操盘',
  `name_code` bigint(20) DEFAULT NULL COMMENT '盘口名称编码. 用于多语言',
  `odds_value` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '该盘口具体显示的值. 例如: 大小球中, 大小界限是:  3.5',
  `odds_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '盘口名称,V1.2统一命名规则. ',
  `order_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '排序类型',
  `odds_metric` bigint(10) DEFAULT '0' COMMENT '盘口级别，数字越小优先级越高',
  `addition1` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '附加字段1',
  `addition2` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '附加字段2',
  `addition3` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '附加字段3',
  `addition4` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '附加字段4',
  `addition5` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '附加字段5',
  `data_source_code` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source',
  `status` tinyint(4) DEFAULT '1' COMMENT '盘口状态0-5. 0:active, 1:suspended, 2:deactivated, 3:settled, 4:cancelled, 5:handedOver',
  `third_market_source_status` tinyint(4) DEFAULT NULL COMMENT '三方盘口源状态',
  `scope_id` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '盘口阶段id. 对应 对应 system_item_dict.value',
  `third_odds_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '接收到第三方数据后, 可以通过该字段快速定位到当前的盘口. 通过玩法和具体内容确认盘口的唯一性.  SR提供的盘口数据id 生成算法: Type_Typeid_Subtypeid_Specialoddsvalue',
  `third_market_source_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '该字段用于做风控时，需要替换成风控服务商提供的盘口id。 如果数据源发生切换，当前字段需要更新。',
  `send_data` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '是否下发数据：Y是N否',
  `link_id` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '最近一次下发数据的Linkid',
  `extra_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `place_num` int(6) DEFAULT NULL COMMENT '盘口位置，1：表示主盘，2：表示第一副盘',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `create_time` bigint(20) DEFAULT NULL,
  `modify_time` bigint(20) DEFAULT NULL,
  `number_of_winners` int(11) DEFAULT '1' COMMENT '并列-胜出数',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_datasource_marketsourceId_unique` (`standard_match_info_id`,`third_market_source_id`,`data_source_code`) USING BTREE,
  KEY `idx_category` (`market_category_id`) USING BTREE,
  KEY `idx_matchId` (`standard_match_info_id`) USING BTREE,
  KEY `id_category_match` (`standard_match_info_id`,`market_category_id`,`data_source_code`) USING BTREE,
  KEY `index_relation_market_id_data_source_code` (`relation_market_id`,`data_source_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs ROW_FORMAT=DYNAMIC COMMENT='足球赛事盘口表. 使用盘口关联的功能存在以下假设: 同一个盘口的显示值不可变更, 如果变更需要删除2个盘口之间的关联关系. . ';

INSERT INTO standard_sport_market_0 (id,relation_market_id,standard_tournament_id,standard_match_info_id,market_category_id,market_type,trade_type,name_code,odds_value,odds_name,order_type,odds_metric,addition1,addition2,addition3,addition4,addition5,data_source_code,status,third_market_source_status,scope_id,third_odds_type,third_market_source_id,send_data,link_id,extra_info,place_num,remark,create_time,modify_time,number_of_winners)
SELECT id,relation_market_id,standard_tournament_id,standard_match_info_id,market_category_id,market_type,trade_type,name_code,odds_value,odds_name,order_type,odds_metric,addition1,addition2,addition3,addition4,addition5,data_source_code,status,third_market_source_status,scope_id,third_odds_type,third_market_source_id,send_data,link_id,extra_info,place_num,remark,create_time,modify_time,number_of_winners
  from standard_sport_market  where standard_match_info_id % 10 = 0;

INSERT INTO standard_sport_market_1 (id,relation_market_id,standard_tournament_id,standard_match_info_id,market_category_id,market_type,trade_type,name_code,odds_value,odds_name,order_type,odds_metric,addition1,addition2,addition3,addition4,addition5,data_source_code,status,third_market_source_status,scope_id,third_odds_type,third_market_source_id,send_data,link_id,extra_info,place_num,remark,create_time,modify_time,number_of_winners)
SELECT id,relation_market_id,standard_tournament_id,standard_match_info_id,market_category_id,market_type,trade_type,name_code,odds_value,odds_name,order_type,odds_metric,addition1,addition2,addition3,addition4,addition5,data_source_code,status,third_market_source_status,scope_id,third_odds_type,third_market_source_id,send_data,link_id,extra_info,place_num,remark,create_time,modify_time,number_of_winners
  from standard_sport_market  where standard_match_info_id % 10 = 1;

INSERT INTO standard_sport_market_2 (id,relation_market_id,standard_tournament_id,standard_match_info_id,market_category_id,market_type,trade_type,name_code,odds_value,odds_name,order_type,odds_metric,addition1,addition2,addition3,addition4,addition5,data_source_code,status,third_market_source_status,scope_id,third_odds_type,third_market_source_id,send_data,link_id,extra_info,place_num,remark,create_time,modify_time,number_of_winners)
SELECT id,relation_market_id,standard_tournament_id,standard_match_info_id,market_category_id,market_type,trade_type,name_code,odds_value,odds_name,order_type,odds_metric,addition1,addition2,addition3,addition4,addition5,data_source_code,status,third_market_source_status,scope_id,third_odds_type,third_market_source_id,send_data,link_id,extra_info,place_num,remark,create_time,modify_time,number_of_winners
  from standard_sport_market  where standard_match_info_id % 10 = 2;

INSERT INTO standard_sport_market_3 (id,relation_market_id,standard_tournament_id,standard_match_info_id,market_category_id,market_type,trade_type,name_code,odds_value,odds_name,order_type,odds_metric,addition1,addition2,addition3,addition4,addition5,data_source_code,status,third_market_source_status,scope_id,third_odds_type,third_market_source_id,send_data,link_id,extra_info,place_num,remark,create_time,modify_time,number_of_winners)
SELECT id,relation_market_id,standard_tournament_id,standard_match_info_id,market_category_id,market_type,trade_type,name_code,odds_value,odds_name,order_type,odds_metric,addition1,addition2,addition3,addition4,addition5,data_source_code,status,third_market_source_status,scope_id,third_odds_type,third_market_source_id,send_data,link_id,extra_info,place_num,remark,create_time,modify_time,number_of_winners
  from standard_sport_market  where standard_match_info_id % 10 = 3;

INSERT INTO standard_sport_market_4 (id,relation_market_id,standard_tournament_id,standard_match_info_id,market_category_id,market_type,trade_type,name_code,odds_value,odds_name,order_type,odds_metric,addition1,addition2,addition3,addition4,addition5,data_source_code,status,third_market_source_status,scope_id,third_odds_type,third_market_source_id,send_data,link_id,extra_info,place_num,remark,create_time,modify_time,number_of_winners)
SELECT id,relation_market_id,standard_tournament_id,standard_match_info_id,market_category_id,market_type,trade_type,name_code,odds_value,odds_name,order_type,odds_metric,addition1,addition2,addition3,addition4,addition5,data_source_code,status,third_market_source_status,scope_id,third_odds_type,third_market_source_id,send_data,link_id,extra_info,place_num,remark,create_time,modify_time,number_of_winners
  from standard_sport_market  where standard_match_info_id % 10 = 4;

INSERT INTO standard_sport_market_5 (id,relation_market_id,standard_tournament_id,standard_match_info_id,market_category_id,market_type,trade_type,name_code,odds_value,odds_name,order_type,odds_metric,addition1,addition2,addition3,addition4,addition5,data_source_code,status,third_market_source_status,scope_id,third_odds_type,third_market_source_id,send_data,link_id,extra_info,place_num,remark,create_time,modify_time,number_of_winners)
SELECT id,relation_market_id,standard_tournament_id,standard_match_info_id,market_category_id,market_type,trade_type,name_code,odds_value,odds_name,order_type,odds_metric,addition1,addition2,addition3,addition4,addition5,data_source_code,status,third_market_source_status,scope_id,third_odds_type,third_market_source_id,send_data,link_id,extra_info,place_num,remark,create_time,modify_time,number_of_winners
  from standard_sport_market  where standard_match_info_id % 10 = 5;

INSERT INTO standard_sport_market_6 (id,relation_market_id,standard_tournament_id,standard_match_info_id,market_category_id,market_type,trade_type,name_code,odds_value,odds_name,order_type,odds_metric,addition1,addition2,addition3,addition4,addition5,data_source_code,status,third_market_source_status,scope_id,third_odds_type,third_market_source_id,send_data,link_id,extra_info,place_num,remark,create_time,modify_time,number_of_winners)
SELECT id,relation_market_id,standard_tournament_id,standard_match_info_id,market_category_id,market_type,trade_type,name_code,odds_value,odds_name,order_type,odds_metric,addition1,addition2,addition3,addition4,addition5,data_source_code,status,third_market_source_status,scope_id,third_odds_type,third_market_source_id,send_data,link_id,extra_info,place_num,remark,create_time,modify_time,number_of_winners
  from standard_sport_market  where standard_match_info_id % 10 = 6;

INSERT INTO standard_sport_market_7 (id,relation_market_id,standard_tournament_id,standard_match_info_id,market_category_id,market_type,trade_type,name_code,odds_value,odds_name,order_type,odds_metric,addition1,addition2,addition3,addition4,addition5,data_source_code,status,third_market_source_status,scope_id,third_odds_type,third_market_source_id,send_data,link_id,extra_info,place_num,remark,create_time,modify_time,number_of_winners)
SELECT id,relation_market_id,standard_tournament_id,standard_match_info_id,market_category_id,market_type,trade_type,name_code,odds_value,odds_name,order_type,odds_metric,addition1,addition2,addition3,addition4,addition5,data_source_code,status,third_market_source_status,scope_id,third_odds_type,third_market_source_id,send_data,link_id,extra_info,place_num,remark,create_time,modify_time,number_of_winners
  from standard_sport_market  where standard_match_info_id % 10 = 7;

INSERT INTO standard_sport_market_8 (id,relation_market_id,standard_tournament_id,standard_match_info_id,market_category_id,market_type,trade_type,name_code,odds_value,odds_name,order_type,odds_metric,addition1,addition2,addition3,addition4,addition5,data_source_code,status,third_market_source_status,scope_id,third_odds_type,third_market_source_id,send_data,link_id,extra_info,place_num,remark,create_time,modify_time,number_of_winners)
SELECT id,relation_market_id,standard_tournament_id,standard_match_info_id,market_category_id,market_type,trade_type,name_code,odds_value,odds_name,order_type,odds_metric,addition1,addition2,addition3,addition4,addition5,data_source_code,status,third_market_source_status,scope_id,third_odds_type,third_market_source_id,send_data,link_id,extra_info,place_num,remark,create_time,modify_time,number_of_winners
  from standard_sport_market  where standard_match_info_id % 10 = 8;

INSERT INTO standard_sport_market_9 (id,relation_market_id,standard_tournament_id,standard_match_info_id,market_category_id,market_type,trade_type,name_code,odds_value,odds_name,order_type,odds_metric,addition1,addition2,addition3,addition4,addition5,data_source_code,status,third_market_source_status,scope_id,third_odds_type,third_market_source_id,send_data,link_id,extra_info,place_num,remark,create_time,modify_time,number_of_winners)
SELECT id,relation_market_id,standard_tournament_id,standard_match_info_id,market_category_id,market_type,trade_type,name_code,odds_value,odds_name,order_type,odds_metric,addition1,addition2,addition3,addition4,addition5,data_source_code,status,third_market_source_status,scope_id,third_odds_type,third_market_source_id,send_data,link_id,extra_info,place_num,remark,create_time,modify_time,number_of_winners
  from standard_sport_market  where standard_match_info_id % 10 = 9;

-------------标准投注项表---------------
DROP TABLE IF EXISTS `standard_sport_market_odds_0`;
CREATE TABLE `standard_sport_market_odds_0` (
  `id` bigint(20) NOT NULL COMMENT '表ID, 自增',
  `relation_market_odds_id` bigint(20) DEFAULT NULL,
  `market_id` bigint(20) DEFAULT NULL COMMENT '盘口ID  standard_sport_market.id',
  `relation_market_id` bigint(20) DEFAULT NULL,
  `odds_fields_template_id` bigint(20) DEFAULT '0' COMMENT '标准投注项模板id 对应standard_sport_odds_fields_templet.id',
  `third_template_source_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '三方投注项源ID',
  `active` tinyint(4) DEFAULT '1' COMMENT '投注项状态： 0未激活(锁盘)、1激活、2投注项封盘',
  `settlement_result_text` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '投注项结算结果文本',
  `settlement_result` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '投注项结算结果文本',
  `bet_settlement_certainty` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '赛果已确认: Confirmed, 盘中事件确认: LiveScouted, 未知: Unknown',
  `odds_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '投注项类型',
  `addition1` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '附加字段1',
  `addition2` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '附加字段2',
  `addition3` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '附加字段3',
  `addition4` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '附加字段4',
  `addition5` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '附加字段5',
  `name_code` bigint(20) DEFAULT NULL COMMENT '名称编码. 用于多语言. 投注项可能有也可能没有该字段. 需要的时候填入',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '投注项名称. ',
  `name_expression_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '投注项名称中包含的表达式的值',
  `malay_odds_value` double(10,2) DEFAULT NULL COMMENT '马来赔',
  `odds_value` int(11) DEFAULT NULL COMMENT '投注项赔率. 单位: 0.0001',
  `pa_odds_value` int(11) DEFAULT '0' COMMENT '投注项PA赔率. 单位: 0.0001',
  `original_odds_value` int(11) DEFAULT NULL COMMENT '投注项原始赔率. 单位: 0.0001',
  `target_side` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '投注给哪一方: T1主队, T2客队, ',
  `order_odds` int(11) DEFAULT '0' COMMENT '用于排序, 大于1, 越小越靠前',
  `data_source_code` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source',
  `third_odds_field_source_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `extra_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `create_time` bigint(20) DEFAULT NULL,
  `modify_time` bigint(20) DEFAULT NULL,
  `standard_match_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '赛事ID,standard_match_info.id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_data_source_source_odds_unique` (`data_source_code`,`third_odds_field_source_id`,`market_id`) USING BTREE,
  KEY `idx_market` (`market_id`) USING BTREE,
  KEY `idx_odds_fields_tem` (`odds_fields_template_id`) USING BTREE,
  KEY `un_market_data_source` (`market_id`,`data_source_code`) USING BTREE,
  KEY `index_name_code` (`name_code`),
  KEY `index_relation_market_id` (`relation_market_id`),
  KEY `index_radar_id` (`addition1`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs ROW_FORMAT=DYNAMIC COMMENT='赛事盘口投注项表';

INSERT INTO standard_sport_market_odds_0(id,relation_market_odds_id,market_id,relation_market_id,odds_fields_template_id,third_template_source_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,name_code,name,name_expression_value,malay_odds_value,odds_value,pa_odds_value,original_odds_value,target_side,order_odds,data_source_code,third_odds_field_source_id,extra_info,remark,create_time,modify_time,standard_match_id)
select id,relation_market_odds_id,market_id,relation_market_id,odds_fields_template_id,third_template_source_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,name_code,name,name_expression_value,malay_odds_value,odds_value,pa_odds_value,original_odds_value,target_side,order_odds,data_source_code,third_odds_field_source_id,extra_info,remark,create_time,modify_time,standard_match_id
from standard_sport_market_odds where market_id % 10 = 0;
INSERT INTO standard_sport_market_odds_1(id,relation_market_odds_id,market_id,relation_market_id,odds_fields_template_id,third_template_source_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,name_code,name,name_expression_value,malay_odds_value,odds_value,pa_odds_value,original_odds_value,target_side,order_odds,data_source_code,third_odds_field_source_id,extra_info,remark,create_time,modify_time,standard_match_id)
select id,relation_market_odds_id,market_id,relation_market_id,odds_fields_template_id,third_template_source_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,name_code,name,name_expression_value,malay_odds_value,odds_value,pa_odds_value,original_odds_value,target_side,order_odds,data_source_code,third_odds_field_source_id,extra_info,remark,create_time,modify_time,standard_match_id
from standard_sport_market_odds where market_id % 10 = 1;
INSERT INTO standard_sport_market_odds_2(id,relation_market_odds_id,market_id,relation_market_id,odds_fields_template_id,third_template_source_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,name_code,name,name_expression_value,malay_odds_value,odds_value,pa_odds_value,original_odds_value,target_side,order_odds,data_source_code,third_odds_field_source_id,extra_info,remark,create_time,modify_time,standard_match_id)
select id,relation_market_odds_id,market_id,relation_market_id,odds_fields_template_id,third_template_source_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,name_code,name,name_expression_value,malay_odds_value,odds_value,pa_odds_value,original_odds_value,target_side,order_odds,data_source_code,third_odds_field_source_id,extra_info,remark,create_time,modify_time,standard_match_id
from standard_sport_market_odds where market_id % 10 = 2;
INSERT INTO standard_sport_market_odds_3(id,relation_market_odds_id,market_id,relation_market_id,odds_fields_template_id,third_template_source_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,name_code,name,name_expression_value,malay_odds_value,odds_value,pa_odds_value,original_odds_value,target_side,order_odds,data_source_code,third_odds_field_source_id,extra_info,remark,create_time,modify_time,standard_match_id)
select id,relation_market_odds_id,market_id,relation_market_id,odds_fields_template_id,third_template_source_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,name_code,name,name_expression_value,malay_odds_value,odds_value,pa_odds_value,original_odds_value,target_side,order_odds,data_source_code,third_odds_field_source_id,extra_info,remark,create_time,modify_time,standard_match_id
from standard_sport_market_odds where market_id % 10 = 3;
INSERT INTO standard_sport_market_odds_4(id,relation_market_odds_id,market_id,relation_market_id,odds_fields_template_id,third_template_source_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,name_code,name,name_expression_value,malay_odds_value,odds_value,pa_odds_value,original_odds_value,target_side,order_odds,data_source_code,third_odds_field_source_id,extra_info,remark,create_time,modify_time,standard_match_id)
select id,relation_market_odds_id,market_id,relation_market_id,odds_fields_template_id,third_template_source_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,name_code,name,name_expression_value,malay_odds_value,odds_value,pa_odds_value,original_odds_value,target_side,order_odds,data_source_code,third_odds_field_source_id,extra_info,remark,create_time,modify_time,standard_match_id
from standard_sport_market_odds where market_id % 10 = 4;
INSERT INTO standard_sport_market_odds_5(id,relation_market_odds_id,market_id,relation_market_id,odds_fields_template_id,third_template_source_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,name_code,name,name_expression_value,malay_odds_value,odds_value,pa_odds_value,original_odds_value,target_side,order_odds,data_source_code,third_odds_field_source_id,extra_info,remark,create_time,modify_time,standard_match_id)
select id,relation_market_odds_id,market_id,relation_market_id,odds_fields_template_id,third_template_source_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,name_code,name,name_expression_value,malay_odds_value,odds_value,pa_odds_value,original_odds_value,target_side,order_odds,data_source_code,third_odds_field_source_id,extra_info,remark,create_time,modify_time,standard_match_id
from standard_sport_market_odds where market_id % 10 = 5;
INSERT INTO standard_sport_market_odds_6(id,relation_market_odds_id,market_id,relation_market_id,odds_fields_template_id,third_template_source_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,name_code,name,name_expression_value,malay_odds_value,odds_value,pa_odds_value,original_odds_value,target_side,order_odds,data_source_code,third_odds_field_source_id,extra_info,remark,create_time,modify_time,standard_match_id)
select id,relation_market_odds_id,market_id,relation_market_id,odds_fields_template_id,third_template_source_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,name_code,name,name_expression_value,malay_odds_value,odds_value,pa_odds_value,original_odds_value,target_side,order_odds,data_source_code,third_odds_field_source_id,extra_info,remark,create_time,modify_time,standard_match_id
from standard_sport_market_odds where market_id % 10 = 6;
INSERT INTO standard_sport_market_odds_7(id,relation_market_odds_id,market_id,relation_market_id,odds_fields_template_id,third_template_source_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,name_code,name,name_expression_value,malay_odds_value,odds_value,pa_odds_value,original_odds_value,target_side,order_odds,data_source_code,third_odds_field_source_id,extra_info,remark,create_time,modify_time,standard_match_id)
select id,relation_market_odds_id,market_id,relation_market_id,odds_fields_template_id,third_template_source_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,name_code,name,name_expression_value,malay_odds_value,odds_value,pa_odds_value,original_odds_value,target_side,order_odds,data_source_code,third_odds_field_source_id,extra_info,remark,create_time,modify_time,standard_match_id
from standard_sport_market_odds where market_id % 10 = 7;
INSERT INTO standard_sport_market_odds_8(id,relation_market_odds_id,market_id,relation_market_id,odds_fields_template_id,third_template_source_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,name_code,name,name_expression_value,malay_odds_value,odds_value,pa_odds_value,original_odds_value,target_side,order_odds,data_source_code,third_odds_field_source_id,extra_info,remark,create_time,modify_time,standard_match_id)
select id,relation_market_odds_id,market_id,relation_market_id,odds_fields_template_id,third_template_source_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,name_code,name,name_expression_value,malay_odds_value,odds_value,pa_odds_value,original_odds_value,target_side,order_odds,data_source_code,third_odds_field_source_id,extra_info,remark,create_time,modify_time,standard_match_id
from standard_sport_market_odds where market_id % 10 = 8;
INSERT INTO standard_sport_market_odds_9(id,relation_market_odds_id,market_id,relation_market_id,odds_fields_template_id,third_template_source_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,name_code,name,name_expression_value,malay_odds_value,odds_value,pa_odds_value,original_odds_value,target_side,order_odds,data_source_code,third_odds_field_source_id,extra_info,remark,create_time,modify_time,standard_match_id)
select id,relation_market_odds_id,market_id,relation_market_id,odds_fields_template_id,third_template_source_id,active,settlement_result_text,settlement_result,bet_settlement_certainty,odds_type,addition1,addition2,addition3,addition4,addition5,name_code,name,name_expression_value,malay_odds_value,odds_value,pa_odds_value,original_odds_value,target_side,order_odds,data_source_code,third_odds_field_source_id,extra_info,remark,create_time,modify_time,standard_match_id
from standard_sport_market_odds where market_id % 10 = 9;


INSERT INTO standard_sport_market_odds_0 select * from standard_sport_market_odds where  market_id % 10 = 0;
INSERT INTO standard_sport_market_odds_1 select * from standard_sport_market_odds where  market_id % 10 = 1;
INSERT INTO standard_sport_market_odds_2 select * from standard_sport_market_odds where  market_id % 10 = 2;
INSERT INTO standard_sport_market_odds_3 select * from standard_sport_market_odds where  market_id % 10 = 3;
INSERT INTO standard_sport_market_odds_4 select * from standard_sport_market_odds where  market_id % 10 = 4;
INSERT INTO standard_sport_market_odds_5 select * from standard_sport_market_odds where  market_id % 10 = 5;
INSERT INTO standard_sport_market_odds_6 select * from standard_sport_market_odds where  market_id % 10 = 6;
INSERT INTO standard_sport_market_odds_7 select * from standard_sport_market_odds where  market_id % 10 = 7;
INSERT INTO standard_sport_market_odds_8 select * from standard_sport_market_odds where  market_id % 10 = 8;
INSERT INTO standard_sport_market_odds_9 select * from standard_sport_market_odds where  market_id % 10 = 9;

INSERT INTO third_sport_market_odds_sr select * from third_sport_market_odds where  data_source_code = 'SR'  AND create_time >= unix_timestamp(now()) * 1000 - 60 * 60 * 24*7 * 1000;
INSERT INTO third_sport_market_odds_bg select * from third_sport_market_odds where  data_source_code = 'BG'  AND create_time >= unix_timestamp(now()) * 1000 - 60 * 60 * 24*7 * 1000;
INSERT INTO third_sport_market_odds_bc select * from third_sport_market_odds where  data_source_code = 'BC'  AND create_time >= unix_timestamp(now()) * 1000 - 60 * 60 * 24*7 * 1000;
INSERT INTO third_sport_market_odds_tx select * from third_sport_market_odds where  data_source_code = 'TX'  AND create_time >= unix_timestamp(now()) * 1000 - 60 * 60 * 24*7 * 1000;
INSERT INTO third_sport_market_odds_pa select * from third_sport_market_odds where  data_source_code = 'PA'  AND create_time >= unix_timestamp(now()) * 1000 - 60 * 60 * 24*7 * 1000;

------------需求：1839-----------------
DROP TABLE IF EXISTS `config_market_odds_status`;
CREATE TABLE `config_market_odds_status` (
  `id` bigint(22) NOT NULL  COMMENT '投注项id',
  `standard_match_info_id` bigint(32) DEFAULT '0' COMMENT '标准赛事id',
  `standard_category_id` bigint(16)  DEFAULT '0' COMMENT '标准玩法id',
  `odds_type` varchar(64) NOT NULL  COMMENT '投注项',
  `odds_value` bigint(20) NOT NULL  COMMENT '投注项赔率值',
  `status` tinyint(2) DEFAULT 12 COMMENT '投注项操盘状态，0-关闭，1-开启',
  `market_type` tinyint(2)  COMMENT '盘口类型',
	`link_id` varchar(64) DEFAULT NULL COMMENT '操作日志id',
  `modify_time` bigint(20) DEFAULT '0' COMMENT '配置修改时间',
  `create_time` bigint(20) DEFAULT '0' COMMENT '创建时间',
  `operater_id` bigint(22) DEFAULT '0' COMMENT '操作人ID',
  PRIMARY KEY (`id`),
  UNIQUE  INDEX  `idx_market_odds_unique` (`standard_match_info_id`,`standard_category_id`,`odds_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='操盘后台投注项状态以及赔率';


------------需求：1852-----------------
DROP TABLE IF EXISTS `config_match_status`;
CREATE TABLE `config_match_status` (
  `id` bigint(22) NOT NULL  ,
  `standard_match_info_id` bigint(32) DEFAULT '0' COMMENT '标准赛事id',
  `status` tinyint(2) DEFAULT 12 COMMENT '赛事接拒2.0开关，0-关，1-开',
  `link_id` varchar(64) DEFAULT NULL COMMENT '操作日志id',
  `modify_time` bigint(20) DEFAULT '0' COMMENT '配置修改时间',
  `create_time` bigint(20) DEFAULT '0' COMMENT '创建时间',
  `operater_id` bigint(22) DEFAULT '0' COMMENT '操作人ID',
  `market_type` tinyint(2) DEFAULT 0 COMMENT '盘口类别1:赛前盘;0:滚球盘',
  PRIMARY KEY (`id`),
  UNIQUE  INDEX  `idx_match_status_unique` (`standard_match_info_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='2.0接拒状态开关配置表';


--------------三方盘口表---------------------------------
CREATE TABLE `third_sport_market_be` (
  `id` bigint(20) NOT NULL COMMENT '数据库id, 自增',
  `tournament_id` bigint(20) DEFAULT NULL COMMENT '所属联赛ID   ',
  `match_id` bigint(20) DEFAULT NULL COMMENT '比赛ID:third_match_info.id',
  `market_category_id` bigint(20) DEFAULT NULL COMMENT '第三方玩法id   standard_sport_market_category.id',
  `third_market_source_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '第三提供的id。SR: 报文中有id字段。',
  `reference_id` bigint(20) DEFAULT NULL COMMENT '如果当前盘口与标准盘口中的B记录玩法相同且盘口显示内容相同, 则该记录的当前字段值为B.ID',
  `market_type` tinyint(4) DEFAULT NULL COMMENT '盘口类型. 属于赛前盘或者滚球盘. 1: 赛前盘; 0: 滚球盘. ',
  `data_source_code` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source',
  `status` tinyint(4) DEFAULT '1' COMMENT '盘口状态0-5. 0:active, 1:suspended, 2:deactivated, 3:settled, 4:cancelled, 5:handedOver',
  `scope_id` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '盘口阶段id. 对应 对应 system_item_dict.value',
  `name_code` bigint(20) DEFAULT NULL COMMENT '盘口名称编码. 用于多语言',
  `odds_type_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '玩法的中文名称. 仅用用于数据库操作人员使用. ',
  `third_odds_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '接收到第三方数据后, 可以通过该字段快速定位到当前的盘口. 通过玩法和具体内容确认盘口的唯一性.  SR提供的盘口数据id 生成算法: Type_Typeid_Subtypeid_Specialoddsvalue',
  `odds_value` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '该盘口具体显示的值. 例如: 大小球中, 大小界限是:  3.5',
  `order_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '排序类型',
  `odds_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '盘口名称. ',
  `odds_metric` bigint(10) DEFAULT NULL COMMENT '盘口级别，数字越小优先级越高',
  `addition1` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '附加字段1',
  `addition2` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '附加字段2',
  `addition3` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '附加字段3',
  `addition4` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '附加字段4',
  `addition5` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '附加字段5',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `create_time` bigint(20) DEFAULT NULL,
  `modify_time` bigint(20) DEFAULT NULL,
  `extra_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `third_market_source_status` tinyint(4) DEFAULT NULL COMMENT '三方盘口源状态',
  `offer_line_id` tinyint(4) DEFAULT NULL COMMENT 'TX坑位',
  `number_of_winners` int(11) DEFAULT '1' COMMENT '并列-胜出数',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_datasource_marketsourceid_unique` (`third_market_source_id`,`data_source_code`) USING BTREE,
  KEY `idx_reference_id` (`reference_id`) USING BTREE,
  KEY `idx_source` (`third_market_source_id`) USING BTREE,
  KEY `idx_third_match_id` (`match_id`),
  KEY `idx_modify_time` (`modify_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs ROW_FORMAT=DYNAMIC COMMENT='该表存放 第三方提供的盘口';


-------------三方投注项表----------------------
DROP TABLE IF EXISTS `third_sport_market_odds_be`;
CREATE TABLE `third_sport_market_odds_be` (
  `id` bigint(20) NOT NULL COMMENT '表ID, 自增',
  `market_id` bigint(20) DEFAULT NULL COMMENT '盘口ID  third_sport_market.id',
  `reference_id` bigint(20) DEFAULT NULL COMMENT '如果当前盘口与标准盘口中的B记录玩法相同且盘口显示内容相同, 则该记录的当前字段值为B.ID',
  `active` tinyint(4) DEFAULT NULL COMMENT '当前投注项是否被激活.1激活; 0未激活(锁盘)',
  `settlement_result_text` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '投注项结算结果文本',
  `settlement_result` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '投注项结算结果文本',
  `bet_settlement_certainty` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '赛果已确认: Confirmed, 盘中事件确认: LiveScouted, 未知: Unknown',
  `odds_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '投注项类型',
  `addition1` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '附加字段1',
  `addition2` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '附加字段2',
  `addition3` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '附加字段3',
  `addition4` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '附加字段4',
  `addition5` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT '' COMMENT '附加字段5',
  `third_odds_field_source_id` varchar(225) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `order_odds` int(11) DEFAULT '0' COMMENT '用于排序, 大于1, 越小越靠前',
  `name_code` bigint(20) DEFAULT NULL COMMENT '名称编码. 用于多语言. 投注项可能有也可能没有该字段. 需要的时候填入',
  `name_expression_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '投注项名称中包含的表达式的值',
  `odds_value` int(11) DEFAULT NULL COMMENT '投注项赔率. 单位: 0.0001',
  `pa_odds_value` int(11) DEFAULT '0' COMMENT '投注项PA赔率. 单位: 0.0001',
  `original_odds_value` int(11) DEFAULT NULL COMMENT '投注项原始赔率. 单位: 0.0001',
  `odds_fields_template_id` bigint(20) DEFAULT '0' COMMENT '标准投注项模板id   standard_market_category_field.id',
  `third_template_source_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '三方投注项模板源ID，third_market_category_field.id',
  `target_side` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '投注给哪一方: T1主队, T2客队',
  `data_source_code` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `create_time` bigint(20) DEFAULT NULL,
  `modify_time` bigint(20) DEFAULT NULL,
  `extra_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL,
  `third_match_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '赛事ID,third_match_info.id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_ds_source_id` (`third_odds_field_source_id`,`data_source_code`,`market_id`) USING BTREE,
  KEY `idx_market` (`market_id`) USING BTREE,
  KEY `idx_data_source_code` (`data_source_code`) USING BTREE,
  KEY `idx_reference` (`reference_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs ROW_FORMAT=DYNAMIC COMMENT='第三方赛事盘口投注项表';








