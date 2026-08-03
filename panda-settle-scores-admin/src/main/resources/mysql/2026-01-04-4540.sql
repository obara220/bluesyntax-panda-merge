alter table match_settle_data_source_switch add COLUMN data_source_heartbeat int(11) NOT NULL DEFAULT 1 COMMENT '数据源心跳';
alter table match_settle_data_source_switch add COLUMN single_data_source_settle int(11) NOT NULL DEFAULT 0 COMMENT '单数据源是否可以';
alter table match_settle_third_event add COLUMN second_from_start int(11) NOT NULL DEFAULT 0 COMMENT '单数据源是否可以';
ALTER TABLE match_settle_event DROP PRIMARY KEY;
ALTER TABLE match_settle_event
    MODIFY COLUMN id BIGINT(20) NOT NULL AUTO_INCREMENT,
    ADD PRIMARY KEY (id);



UPDATE match_settle_template
SET template_json = REPLACE(
        REPLACE(template_json, '}', ',"heartbeatSecond":0,"singleDatasourceSettleSwitch":0}'),
        ',"heartbeatSecond":0,"singleDatasourceSettleSwitch":0},"heartbeatSecond":0,"singleDatasourceSettleSwitch":0}',
        ',"heartbeatSecond":0,"singleDatasourceSettleSwitch":0}'
                    )
WHERE sport_id = 1 and template_type = 1 and tournament_level != -1 and template_json IS NOT NULL
  AND template_json != ''
AND template_json NOT LIKE '%heartbeatSecond%';



INSERT INTO match_settle_event (
    standard_match_id,
    period_id,
    third_event_source_id,
    event_type,
    event_code,
    event_name,
    settle_num,
    event_order,
    home_away,
    status,
    data_source_code,
    sport_id,
    settle_times,
    settle_count,
    check_number,
    settle_freeze,
    create_time,
    modify_time
)
SELECT
    src.standard_match_id,
    src.period_id,
    src.third_event_source_id,
    3,
    src.event_code,
    src.event_name,
    CASE
        WHEN src.event_code IN ('fa_card','yellow_card','red_card') AND src.period_id = 6 THEN '30195'
        WHEN src.event_code IN ('fa_card','yellow_card','red_card') AND src.period_id = 7 THEN '30205'
        WHEN src.event_code IN ('goal','no goal')                   AND src.period_id = 6 THEN '10225'
        WHEN src.event_code IN ('goal','no goal')                   AND src.period_id = 7 THEN '10235'
        WHEN src.event_code = 'corner'                              AND src.period_id = 6 THEN '2045'
        WHEN src.event_code = 'corner'                              AND src.period_id = 7 THEN '2055'
        END,
    src.event_order,
    src.home_away,
    0,
    data_source_code,
    sport_id,
    0,
    0,
    1,
    0,
    FLOOR(UNIX_TIMESTAMP(NOW(3)) * 1000),
    FLOOR(UNIX_TIMESTAMP(NOW(3)) * 1000)
FROM match_settle_event src
WHERE src.event_type = 1
  AND src.period_id  IN (6, 7)
  AND src.event_code IN ('goal','no goal','corner','fa_card','yellow_card','red_card')
  AND NOT EXISTS (
    SELECT 1
    FROM match_settle_event chk
    WHERE chk.standard_match_id = src.standard_match_id
      AND chk.event_code  = src.event_code
      AND chk.period_id   = src.period_id
      AND chk.event_type  = 3
      AND chk.event_order = src.event_order
      AND chk.settle_num  = CASE
                                WHEN src.event_code IN ('fa_card','yellow_card','red_card') AND src.period_id = 6 THEN '30195'
                                WHEN src.event_code IN ('fa_card','yellow_card','red_card') AND src.period_id = 7 THEN '30205'
                                WHEN src.event_code IN ('goal','no goal')                   AND src.period_id = 6 THEN '10225'
                                WHEN src.event_code IN ('goal','no goal')                   AND src.period_id = 7 THEN '10235'
                                WHEN src.event_code = 'corner'                              AND src.period_id = 6 THEN '2045'
                                WHEN src.event_code = 'corner'                              AND src.period_id = 7 THEN '2055'
        END
);