update third_market_category set reference_id = 198 where third_source_id = 'TX:26';
update third_market_category set reference_id = 199 where third_source_id = 'TX:25';
update third_market_category set reference_id = 40 where third_source_id = 'TX:6';

update third_market_category_field set reference_id = 617 where third_source_id = 'TX:26:100';
update third_market_category_field set reference_id = 618 where third_source_id = 'TX:26:101';

update third_market_category_field set reference_id = 619 where third_source_id = 'TX:25:102';
update third_market_category_field set reference_id = 620 where third_source_id = 'TX:25:103';

update third_market_category_field set reference_id = 157 where third_source_id = 'TX:6:104';
update third_market_category_field set reference_id = 158 where third_source_id = 'TX:6:105';

DELETE FROM third_market_category WHERE third_source_id = 'TX:355';
INSERT INTO third_market_category(id, name_code, fields_num, third_source_id, reference_id, data_source_code, active, create_time, modify_time)
VALUES (1447871363566137346, 1447871363566137347, 2, 'TX:355', 53, 'TX', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

DELETE FROM third_sport_market_category WHERE market_category_id = 1447871363566137346;
INSERT INTO third_sport_market_category(sport_id, market_category_id, create_time, modify_time)
VALUES (2, 1447871363566137346, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

DELETE FROM third_market_category_field WHERE third_source_id in('TX:355:3551','TX:355:3552');
INSERT INTO third_market_category_field(id, market_category_id, name_code, third_source_id, reference_id, order_no, data_source_code, create_time, modify_time)
VALUES (1447890060649295874, 1447871363566137346, 1447890060649295875, 'TX:355:3551', 187, 1, 'TX', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);
INSERT INTO third_market_category_field(id, market_category_id, name_code, third_source_id, reference_id, order_no, data_source_code, create_time, modify_time)
VALUES (1447890060649295877, 1447871363566137346, 1447890060649295878, 'TX:355:3552', 188, 2, 'TX', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

DELETE FROM i18n_market_category WHERE name_code in (1447871363566137347,1447890060649295875,1447890060649295878);
INSERT INTO i18n_market_category ( name_code, flag, data_source_code, language_type, text, remark, create_time, modify_time )
VALUES
	(1447871363566137347, 2, 'TX', 'en', '2nd Quarter Odd/Even', NULL, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000 ),
	(1447890060649295875, 2, 'TX', 'en', 'Odd', NULL, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000 ),
	(1447890060649295878, 2, 'TX', 'en', 'Even', NULL, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000 );




