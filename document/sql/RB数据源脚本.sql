delete from data_source where `code` = 'RB';
INSERT INTO data_source(`id`, `code`, `full_name`, `short_name`, `priority`, `commerce`,standard,type,remark, `create_time`, `modify_time`)
VALUES (null, 'RB', 'RunningBall','RB', 0, 1,0,0,'RunningBall', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

delete from third_sport_type where  data_source_code = 'RB' and name_code in (1,2,3,4,9);
INSERT INTO third_sport_type(name_code, third_sport_id, data_source_code, reference_id, introduction, `remark`, create_time, modify_time) 
VALUES (1, '1', 'RB',1, '足球', '足球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);
INSERT INTO third_sport_type(name_code, third_sport_id, data_source_code, reference_id, introduction, `remark`, create_time, modify_time) 
VALUES (2, '2', 'RB',2, '篮球', '篮球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);
INSERT INTO third_sport_type(name_code, third_sport_id, data_source_code, reference_id, introduction, `remark`, create_time, modify_time)
VALUES (4, '8', 'RB',4, '冰球', '冰球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);
INSERT INTO third_sport_type(name_code, third_sport_id, data_source_code, reference_id, introduction, `remark`, create_time, modify_time)
VALUES (9, '9', 'RB',9, '排球', '排球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);
INSERT INTO third_sport_type(name_code, third_sport_id, data_source_code, reference_id, introduction, `remark`, create_time, modify_time)
VALUES (3, '17', 'RB',3, '棒球', '棒球', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);
