

-- 增加盘口操盘类型
ALTER TABLE config_outright_trade_market ADD COLUMN `operate_type` tinyint(2) DEFAULT 0 COMMENT '是否人工操盘; 0:自动, 1:手动, 默认0';