alter table match_settle_data_source_switch add COLUMN data_source_heartbeat int(11) NOT NULL DEFAULT 1 COMMENT '数据源心跳';
alter table match_settle_data_source_switch add COLUMN single_data_source_settle int(11) NOT NULL DEFAULT 0 COMMENT '单数据源是否可以';
alter table match_settle_third_event add COLUMN second_from_start int(11) NOT NULL DEFAULT 0 COMMENT '单数据源是否可以';

UPDATE match_settle_template
SET template_json = REPLACE(
        REPLACE(template_json, '}', ',"heartbeatSecond":1800,"singleDatasourceSettleSwitch":0}'),
        ',"heartbeatSecond":1800,"singleDatasourceSettleSwitch":0},"heartbeatSecond":1800,"singleDatasourceSettleSwitch":0}',
        ',"heartbeatSecond":1800,"singleDatasourceSettleSwitch":0}'
                    )
WHERE sport_id = 1 and template_type = 1 and tournament_level != -1 and template_json IS NOT NULL
  AND template_json != ''
AND template_json NOT LIKE '%heartbeatSecond%';