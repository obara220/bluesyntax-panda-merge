update third_market_category set reference_id = 130 where third_source_id = 'TX:255';

DELETE FROM third_market_category_field WHERE id in (1441979960035241992,1441979960035241995,1441979960035241986,1441979960035241989);
INSERT INTO third_market_category_field(id, market_category_id, name_code, third_source_id, reference_id, order_no, data_source_code, create_time, modify_time) VALUES (1441979960035241992, 1337738175956774996, 1441979960035241993, 'TX:255:1', 432, 1, 'TX', 1632629787821, 1632629787821);
INSERT INTO third_market_category_field(id, market_category_id, name_code, third_source_id, reference_id, order_no, data_source_code, create_time, modify_time) VALUES (1441979960035241995, 1337738175956774996, 1441979960035241996, 'TX:255:2', 433, 2, 'TX', 1632629787821, 1632629787821);
INSERT INTO third_market_category_field(id, market_category_id, name_code, third_source_id, reference_id, order_no, data_source_code, create_time, modify_time) VALUES (1441979960035241986, 1392119733269164035, 1441979960035241987, 'TX:256:1', 1041, 1, 'TX', 1632629787821, 1632629787821);
INSERT INTO third_market_category_field(id, market_category_id, name_code, third_source_id, reference_id, order_no, data_source_code, create_time, modify_time) VALUES (1441979960035241989, 1392119733269164035, 1441979960035241990, 'TX:256:2', 1042, 2, 'TX', 1632629787821, 1632629787821);

DELETE FROM i18n_market_category WHERE name_code in (1441979960035241987,1441979960035241990,1441979960035241993,1441979960035241996);
INSERT INTO i18n_market_category(name_code, flag, data_source_code, language_type, text, remark, create_time, modify_time) VALUES (1441979960035241987, 2, 'TX', 'en', 'Over', NULL, 1632629710679, 1632629710679);
INSERT INTO i18n_market_category(name_code, flag, data_source_code, language_type, text, remark, create_time, modify_time) VALUES (1441979960035241990, 2, 'TX', 'en', 'Under', NULL, 1632629710679, 1632629710679);
INSERT INTO i18n_market_category(name_code, flag, data_source_code, language_type, text, remark, create_time, modify_time) VALUES (1441979960035241993, 2, 'TX', 'en', '1', NULL, 1632629710679, 1632629710679);
INSERT INTO i18n_market_category(name_code, flag, data_source_code, language_type, text, remark, create_time, modify_time) VALUES (1441979960035241996, 2, 'TX', 'en', '2', NULL, 1632629710679, 1632629710679);

update standard_market_category set modify_time = UNIX_TIMESTAMP() * 1000 where id = 130;
#del panda-merge::ThirdMarketCategory:TX-TX:255
#del panda-merge::ThirdMarketCategory:TX-TX:256