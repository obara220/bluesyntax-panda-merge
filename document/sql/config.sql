CREATE TABLE config_outright_trade_type (
  id bigint(22) NOT NULL,
  level tinyint(2) DEFAULT '0' COMMENT '生效级别 1:玩法 3:赛事',
  standard_match_id bigint(20) DEFAULT NULL COMMENT '标准比赛ID standard_match_info.id',
  standard_market_id bigint(22) DEFAULT NULL COMMENT '统一盘口id',
  trade_type tinyint(2) DEFAULT NULL COMMENT '操盘类型 0:自动操盘 1:手动操盘',
  modify_time bigint(20) DEFAULT '0' COMMENT '配置修改时间',
  create_time bigint(20) DEFAULT '0' COMMENT '创建时间',
  operater_id bigint(22) DEFAULT '0' COMMENT '操作人ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='冠军自动手动配置表操盘';

CREATE TABLE config_outright_trade_market (
  id bigint(22) NOT NULL,
  standard_match_id bigint(20) DEFAULT NULL COMMENT '标准比赛ID standard_match_info.id',
  standard_market_id bigint(22) DEFAULT NULL COMMENT '统一盘口id',
  market_status int(4) DEFAULT '0' COMMENT '盘口状态',
  link_id varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '日志id',
  create_time bigint(20) DEFAULT '0' COMMENT '创建时间',
  modify_time bigint(20) DEFAULT '0' COMMENT '修改时间',
  operater_id bigint(22) DEFAULT '0' COMMENT '操作人ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='冠军盘口配置表';

CREATE TABLE config_outright_trade_odds (
  id bigint(22) NOT NULL,
  standard_match_id bigint(20) DEFAULT NULL COMMENT '标准比赛ID standard_match_info.id',
  standard_market_id bigint(22) DEFAULT NULL COMMENT '统一盘口id',
  standard_market_odds_id bigint(22) DEFAULT NULL COMMENT '标准投注项ID',
  odds_status int(4) DEFAULT NULL COMMENT '投注项状态',
  link_id varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '日志id',
  create_time bigint(20) DEFAULT '0' COMMENT '创建时间',
  modify_time bigint(20) DEFAULT '0' COMMENT '修改时间',
  operater_id bigint(22) DEFAULT '0' COMMENT '操作人ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='冠军投注项配置表';

CREATE TABLE config_outright_trade_probability (
  id bigint(22) NOT NULL,
  standard_match_id bigint(20) DEFAULT NULL COMMENT '标准比赛ID standard_match_info.id',
  standard_market_id bigint(22) DEFAULT NULL COMMENT '统一盘口id',
  standard_market_odds_id bigint(22) DEFAULT NULL COMMENT '标准投注项ID',
  probability double(10,2) DEFAULT '0.0' COMMENT '概率',
  link_id varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '日志id',
  create_time bigint(20) DEFAULT '0' COMMENT '创建时间',
  modify_time bigint(20) DEFAULT '0' COMMENT '修改时间',
  operater_id bigint(22) DEFAULT '0' COMMENT '操作人ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='冠军概率配置表';
