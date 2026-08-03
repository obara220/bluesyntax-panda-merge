delete from third_market_category where data_source_code='LS' and third_source_id in('LS:409','LS:95','LS:11','LS:410','LS:250','LS:129','LS:30','LS:31',
                                                                                     'LS:401','LS:402','LS:170','LS:406','LS:407','LS:214','LS:408','LS:414');

INSERT INTO `panda`.`third_market_category`(`id`, `name_code`, `fields_num`, `third_source_id`, `reference_id`, `data_source_code`, `active`, `create_time`, `modify_time`) VALUES (1637321343321632776, 1637321343321632777, 2, 'LS:11', 114, 'LS', 1, 1679202802864, 1679905582991);
INSERT INTO `panda`.`third_market_category`(`id`, `name_code`, `fields_num`, `third_source_id`, `reference_id`, `data_source_code`, `active`, `create_time`, `modify_time`) VALUES (1637321343321632792, 1637321343321632793, 2, 'LS:129', 122, 'LS', 1, 1679202802864, 1679905583006);
INSERT INTO `panda`.`third_market_category`(`id`, `name_code`, `fields_num`, `third_source_id`, `reference_id`, `data_source_code`, `active`, `create_time`, `modify_time`) VALUES (1637321343321632796, 1637321343321632797, 2, 'LS:170', 118, 'LS', 1, 1679202802864, 1679905583010);
INSERT INTO `panda`.`third_market_category`(`id`, `name_code`, `fields_num`, `third_source_id`, `reference_id`, `data_source_code`, `active`, `create_time`, `modify_time`) VALUES (1640269016978812934, 1640269016978812935, 2, 'LS:214', 307, 'LS', 1, 1679905583029, 1679905583029);
INSERT INTO `panda`.`third_market_category`(`id`, `name_code`, `fields_num`, `third_source_id`, `reference_id`, `data_source_code`, `active`, `create_time`, `modify_time`) VALUES (1637321343321632788, 1637321343321632789, 2, 'LS:250', 121, 'LS', 1, 1679202802864, 1679905583003);
INSERT INTO `panda`.`third_market_category`(`id`, `name_code`, `fields_num`, `third_source_id`, `reference_id`, `data_source_code`, `active`, `create_time`, `modify_time`) VALUES (1637321343321632804, 1637321343321632805, 2, 'LS:30', 115, 'LS', 1, 1679202802864, 1679905583015);
INSERT INTO `panda`.`third_market_category`(`id`, `name_code`, `fields_num`, `third_source_id`, `reference_id`, `data_source_code`, `active`, `create_time`, `modify_time`) VALUES (1637321343321632808, 1637321343321632809, 2, 'LS:31', 116, 'LS', 1, 1679202802864, 1679905583018);
INSERT INTO `panda`.`third_market_category`(`id`, `name_code`, `fields_num`, `third_source_id`, `reference_id`, `data_source_code`, `active`, `create_time`, `modify_time`) VALUES (1637321343321632812, 1637321343321632813, 2, 'LS:401', 123, 'LS', 1, 1679202802864, 1679905583021);
INSERT INTO `panda`.`third_market_category`(`id`, `name_code`, `fields_num`, `third_source_id`, `reference_id`, `data_source_code`, `active`, `create_time`, `modify_time`) VALUES (1637321343321632816, 1637321343321632817, 2, 'LS:402', 124, 'LS', 1, 1679202802864, 1679905583024);
INSERT INTO `panda`.`third_market_category`(`id`, `name_code`, `fields_num`, `third_source_id`, `reference_id`, `data_source_code`, `active`, `create_time`, `modify_time`) VALUES (1637321343321632800, 1637321343321632801, 2, 'LS:406', 229, 'LS', 1, 1679202802864, 1679905583012);
INSERT INTO `panda`.`third_market_category`(`id`, `name_code`, `fields_num`, `third_source_id`, `reference_id`, `data_source_code`, `active`, `create_time`, `modify_time`) VALUES (1640269016978812930, 1640269016978812931, 2, 'LS:407', 306, 'LS', 1, 1679905583029, 1679905583029);
INSERT INTO `panda`.`third_market_category`(`id`, `name_code`, `fields_num`, `third_source_id`, `reference_id`, `data_source_code`, `active`, `create_time`, `modify_time`) VALUES (1640269016978812938, 1640269016978812939, 2, 'LS:408', 308, 'LS', 1, 1679905583029, 1679905583029);
INSERT INTO `panda`.`third_market_category`(`id`, `name_code`, `fields_num`, `third_source_id`, `reference_id`, `data_source_code`, `active`, `create_time`, `modify_time`) VALUES (1637321343321632780, 1637321343321632781, 3, 'LS:409', 111, 'LS', 1, 1679202802864, 1679905582994);
INSERT INTO `panda`.`third_market_category`(`id`, `name_code`, `fields_num`, `third_source_id`, `reference_id`, `data_source_code`, `active`, `create_time`, `modify_time`) VALUES (1637321343321632784, 1637321343321632785, 3, 'LS:410', 119, 'LS', 1, 1679202802864, 1679905582998);
INSERT INTO `panda`.`third_market_category`(`id`, `name_code`, `fields_num`, `third_source_id`, `reference_id`, `data_source_code`, `active`, `create_time`, `modify_time`) VALUES (1640269016978812942, 1640269016978812943, 2, 'LS:414', 309, 'LS', 1, 1679905583029, 1679905583029);
INSERT INTO `panda`.`third_market_category`(`id`, `name_code`, `fields_num`, `third_source_id`, `reference_id`, `data_source_code`, `active`, `create_time`, `modify_time`) VALUES (1637321343321632772, 1637321343321632773, 2, 'LS:95', 113, 'LS', 1, 1679202802864, 1679905582988);



DELETE FROM `panda`.`i18n_market_category` WHERE name_code in (1637321343321632777,1637321343321632793,
                                                               1637321343321632797,1640269016978812935,1637321343321632789,1637321343321632805,1637321343321632809,
                                                               1637321343321632813,1637321343321632817,1637321343321632801,1640269016978812931,1640269016978812939,
                                                               1637321343321632781,1637321343321632785,1640269016978812943,1637321343321632773);

INSERT INTO `panda`.`i18n_market_category`(`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1637321343321632773, 2, 'LS', 'en', 'Corners Handicap', NULL, 1679202581909, 1679905353519);
INSERT INTO `panda`.`i18n_market_category`(`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1637321343321632777, 2, 'LS', 'en', 'Total Corners', NULL, 1679202581909, 1679905353520);
INSERT INTO `panda`.`i18n_market_category`(`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1637321343321632781, 2, 'LS', 'en', '1X2 Corners', NULL, 1679202581909, 1679905353521);
INSERT INTO `panda`.`i18n_market_category`(`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1637321343321632785, 2, 'LS', 'en', '1X2 Corners 1st Half', NULL, 1679202581909, 1679905353521);
INSERT INTO `panda`.`i18n_market_category`(`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1637321343321632789, 2, 'LS', 'en', 'Corners Handicap - 1st Half', NULL, 1679202581909, 1679905353522);
INSERT INTO `panda`.`i18n_market_category`(`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1637321343321632793, 2, 'LS', 'en', 'Under/Over Corners - 1st Half', NULL, 1679202581909, 1679905353523);
INSERT INTO `panda`.`i18n_market_category`(`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1637321343321632797, 2, 'LS', 'en', 'Odd/Even Corners', NULL, 1679202581909, 1679905353524);
INSERT INTO `panda`.`i18n_market_category`(`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1637321343321632801, 2, 'LS', 'en', 'Odd/Even Corners  1st Half', NULL, 1679202581909, 1679905353525);
INSERT INTO `panda`.`i18n_market_category`(`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1637321343321632805, 2, 'LS', 'en', 'Under/Over Corners - Home Team', NULL, 1679202581909, 1679905353526);
INSERT INTO `panda`.`i18n_market_category`(`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1637321343321632809, 2, 'LS', 'en', 'Under/Over Corners - Away Team', NULL, 1679202581909, 1679905353526);
INSERT INTO `panda`.`i18n_market_category`(`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1637321343321632813, 2, 'LS', 'en', 'Under/Over Corners 1st Half - Home Team', NULL, 1679202581909, 1679905353527);
INSERT INTO `panda`.`i18n_market_category`(`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1637321343321632817, 2, 'LS', 'en', 'Under/Over Corners 1st Half - Away Team', NULL, 1679202581909, 1679905353528);
INSERT INTO `panda`.`i18n_market_category`(`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1640269016978812931, 2, 'LS', 'en', 'Asian Handicap Cards', NULL, 1679905353464, 1679905353464);
INSERT INTO `panda`.`i18n_market_category`(`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1640269016978812935, 2, 'LS', 'en', 'Under/Over Cards', NULL, 1679905353464, 1679905353464);
INSERT INTO `panda`.`i18n_market_category`(`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1640269016978812939, 2, 'LS', 'en', 'Asian Handicap Cards 1st Half', NULL, 1679905353464, 1679905353464);
INSERT INTO `panda`.`i18n_market_category`(`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1640269016978812943, 2, 'LS', 'en', 'Under/Over Cards 1st Half', NULL, 1679905353464, 1679905353464);



delete from `panda`.`third_market_category_field` where third_source_id in ('LS:95:1','LS:95:2','LS:11:1','LS:11:2','LS:409:1','LS:409:2','LS:409:3',
                                                                            'LS:410:1','LS:410:2','LS:410:3','LS:250:1','LS:250:2','LS:129:1','LS:129:2',
                                                                            'LS:170:1','LS:170:2','LS:406:1','LS:406:2','LS:30:1','LS:30:2','LS:31:1',
                                                                            'LS:31:2','LS:401:1','LS:401:2','LS:402:1','LS:402:2','LS:407:1','LS:407:2',
                                                                            'LS:214:1','LS:214:2','LS:408:1','LS:408:2','LS:414:1','LS:414:2');

INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1637321343321632772, 1637321378381819921, 'LS:95:1', 394, 1, 'LS', 1679202811223, 1679905586375);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1637321343321632772, 1637321378381819924, 'LS:95:2', 395, 2, 'LS', 1679202811223, 1679905586377);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1637321343321632776, 1637321378381819927, 'LS:11:1', 397, 1, 'LS', 1679202811223, 1679905586379);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1637321343321632776, 1637321378381819930, 'LS:11:2', 396, 2, 'LS', 1679202811223, 1679905586381);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1637321343321632780, 1637321378381819912, 'LS:409:1', 388, 1, 'LS', 1679202811223, 1679905586369);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1637321343321632780, 1637321378381819915, 'LS:409:2', 389, 2, 'LS', 1679202811223, 1679905586371);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1637321343321632780, 1637321378381819918, 'LS:409:3', 390, 3, 'LS', 1679202811223, 1679905586373);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1637321343321632784, 1637321378381819933, 'LS:410:1', 404, 1, 'LS', 1679202811223, 1679905586383);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1637321343321632784, 1637321378381819936, 'LS:410:2', 405, 2, 'LS', 1679202811223, 1679905586385);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1637321343321632784, 1637321378381819939, 'LS:410:3', 406, 3, 'LS', 1679202811223, 1679905586387);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1637321343321632788, 1637321378381819942, 'LS:250:1', 410, 1, 'LS', 1679202811223, 1679905586390);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1637321343321632788, 1637321378381819945, 'LS:250:2', 411, 2, 'LS', 1679202811223, 1679905586392);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1637321343321632792, 1637321378381819948, 'LS:129:1', 413, 1, 'LS', 1679202811223, 1679905586394);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1637321343321632792, 1637321378381819951, 'LS:129:2', 412, 2, 'LS', 1679202811223, 1679905586397);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1637321343321632796, 1637321378381819954, 'LS:170:1', 402, 1, 'LS', 1679202811223, 1679905586398);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1637321343321632796, 1637321378381819957, 'LS:170:2', 403, 2, 'LS', 1679202811223, 1679905586401);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1637321343321632800, 1637321378381819960, 'LS:406:1', 727, 1, 'LS', 1679202811223, 1679905586404);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1637321343321632800, 1637321378381819963, 'LS:406:2', 728, 2, 'LS', 1679202811223, 1679905586407);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1637321343321632804, 1637321378381819966, 'LS:30:1', 399, 1, 'LS', 1679202811223, 1679905586410);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1637321343321632804, 1637321378381819969, 'LS:30:2', 398, 2, 'LS', 1679202811223, 1679905586413);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1637321343321632808, 1637321378381819972, 'LS:31:1', 401, 1, 'LS', 1679202811223, 1679905586416);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1637321343321632808, 1637321378381819975, 'LS:31:2', 400, 2, 'LS', 1679202811223, 1679905586419);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1637321343321632812, 1637321378381819978, 'LS:401:1', 415, 1, 'LS', 1679202811223, 1679905586422);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1637321343321632812, 1637321378381819981, 'LS:401:2', 414, 2, 'LS', 1679202811223, 1679905586424);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1637321343321632816, 1637321378381819984, 'LS:402:1', 417, 1, 'LS', 1679202811223, 1679905586427);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1637321343321632816, 1637321378381819987, 'LS:402:2', 416, 2, 'LS', 1679202811223, 1679905586430);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1640269016978812930, 1640269031285583874, 'LS:407:1', 964, 1, 'LS', 1679905586440, 1679905586440);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1640269016978812930, 1640269031285583877, 'LS:407:2', 965, 2, 'LS', 1679905586440, 1679905586440);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1640269016978812934, 1640269031285583880, 'LS:214:1', 966, 1, 'LS', 1679905586440, 1679905586440);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1640269016978812934, 1640269031285583883, 'LS:214:2', 967, 2, 'LS', 1679905586440, 1679905586440);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1640269016978812938, 1640269031285583886, 'LS:408:1', 968, 1, 'LS', 1679905586440, 1679905586440);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1640269016978812938, 1640269031285583889, 'LS:408:2', 969, 2, 'LS', 1679905586440, 1679905586440);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1640269016978812942, 1640269031285583892, 'LS:414:1', 970, 1, 'LS', 1679905586440, 1679905586440);
INSERT INTO `panda`.`third_market_category_field`(`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES ( 1640269016978812942, 1640269031285583895, 'LS:414:2', 971, 2, 'LS', 1679905586440, 1679905586440);




DELETE FROM `panda`.`i18n_market_category` WHERE name_code in (
                                                               1637321378381819921,1637321378381819924,1637321378381819927,1637321378381819930,1637321378381819912,
                                                               1637321378381819915,1637321378381819918,1637321378381819933,1637321378381819936,1637321378381819939,
                                                               1637321378381819942,1637321378381819945,1637321378381819948,1637321378381819951,1637321378381819954,
                                                               1637321378381819957,1637321378381819960,1637321378381819963,1637321378381819966,1637321378381819969,
                                                               1637321378381819972,1637321378381819975,1637321378381819978,1637321378381819981,1637321378381819984,
                                                               1637321378381819987,1640269031285583874,1640269031285583877,1640269031285583880,1640269031285583883,
                                                               1640269031285583886,1640269031285583889,1640269031285583892,1640269031285583895);

INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1637321378381819912, 2, 'LS', 'en', '1', NULL, 1679202590435, 1679905357174);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1637321378381819915, 2, 'LS', 'en', 'X', NULL, 1679202590435, 1679905357175);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1637321378381819918, 2, 'LS', 'en', '2', NULL, 1679202590435, 1679905357176);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1637321378381819921, 2, 'LS', 'en', '1', NULL, 1679202590435, 1679905357177);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1637321378381819924, 2, 'LS', 'en', '2', NULL, 1679202590435, 1679905357178);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1637321378381819927, 2, 'LS', 'en', 'Over', NULL, 1679202590435, 1679905357179);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1637321378381819930, 2, 'LS', 'en', 'Under', NULL, 1679202590435, 1679905357181);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1637321378381819933, 2, 'LS', 'en', '1', NULL, 1679202590435, 1679905357182);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1637321378381819936, 2, 'LS', 'en', 'X', NULL, 1679202590435, 1679905357183);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1637321378381819939, 2, 'LS', 'en', '2', NULL, 1679202590435, 1679905357184);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1637321378381819942, 2, 'LS', 'en', '1', NULL, 1679202590435, 1679905357185);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1637321378381819945, 2, 'LS', 'en', '2', NULL, 1679202590435, 1679905357186);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1637321378381819948, 2, 'LS', 'en', 'Over', NULL, 1679202590435, 1679905357187);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1637321378381819951, 2, 'LS', 'en', 'Under', NULL, 1679202590435, 1679905357189);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1637321378381819954, 2, 'LS', 'en', 'Odd', NULL, 1679202590435, 1679905357190);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1637321378381819957, 2, 'LS', 'en', 'Even', NULL, 1679202590435, 1679905357191);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1637321378381819960, 2, 'LS', 'en', 'Odd', NULL, 1679202590435, 1679905357192);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1637321378381819963, 2, 'LS', 'en', 'Even', NULL, 1679202590435, 1679905357193);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1637321378381819966, 2, 'LS', 'en', 'Over', NULL, 1679202590435, 1679905357194);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1637321378381819969, 2, 'LS', 'en', 'Under', NULL, 1679202590435, 1679905357196);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1637321378381819972, 2, 'LS', 'en', 'Over', NULL, 1679202590435, 1679905357197);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1637321378381819975, 2, 'LS', 'en', 'Under', NULL, 1679202590435, 1679905357198);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1637321378381819978, 2, 'LS', 'en', 'Over', NULL, 1679202590435, 1679905357199);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1637321378381819981, 2, 'LS', 'en', 'Under', NULL, 1679202590435, 1679905357200);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1637321378381819984, 2, 'LS', 'en', 'Over', NULL, 1679202590435, 1679905357201);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1637321378381819987, 2, 'LS', 'en', 'Under', NULL, 1679202590435, 1679905357202);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1640269031285583874, 2, 'LS', 'en', '1', NULL, 1679905357031, 1679905357031);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1640269031285583877, 2, 'LS', 'en', '2', NULL, 1679905357031, 1679905357031);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1640269031285583880, 2, 'LS', 'en', 'Over', NULL, 1679905357031, 1679905357031);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1640269031285583883, 2, 'LS', 'en', 'Under', NULL, 1679905357031, 1679905357031);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1640269031285583886, 2, 'LS', 'en', '1', NULL, 1679905357031, 1679905357031);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1640269031285583889, 2, 'LS', 'en', '2', NULL, 1679905357031, 1679905357031);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1640269031285583892, 2, 'LS', 'en', 'Over', NULL, 1679905357031, 1679905357031);
INSERT INTO `panda`.`i18n_market_category`( `name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES ( 1640269031285583895, 2, 'LS', 'en', 'Under', NULL, 1679905357031, 1679905357031);


DELETE FROM `panda`.`third_sport_market_category`
WHERE  sport_id = 1 and market_category_id in (SELECT id FROM `panda`.`third_market_category`
                                               WHERE third_source_id in ('LS:409','LS:95','LS:11','LS:410','LS:250','LS:129','LS:30','LS:31',
                                                                         'LS:401','LS:402','LS:170','LS:406','LS:407','LS:214','LS:408','LS:414'));
INSERT INTO `panda`.`third_sport_market_category`(`sport_id`, `market_category_id`, `create_time`, `modify_time`) VALUES (1, 1637321343321632772, 1679202802864, 1679202802864);
INSERT INTO `panda`.`third_sport_market_category`(`sport_id`, `market_category_id`, `create_time`, `modify_time`) VALUES (1, 1637321343321632776, 1679202802864, 1679202802864);
INSERT INTO `panda`.`third_sport_market_category`(`sport_id`, `market_category_id`, `create_time`, `modify_time`) VALUES (1, 1637321343321632780, 1679202802864, 1679202802864);
INSERT INTO `panda`.`third_sport_market_category`(`sport_id`, `market_category_id`, `create_time`, `modify_time`) VALUES (1, 1637321343321632784, 1679202802864, 1679202802864);
INSERT INTO `panda`.`third_sport_market_category`(`sport_id`, `market_category_id`, `create_time`, `modify_time`) VALUES (1, 1637321343321632788, 1679202802864, 1679202802864);
INSERT INTO `panda`.`third_sport_market_category`(`sport_id`, `market_category_id`, `create_time`, `modify_time`) VALUES (1, 1637321343321632792, 1679202802864, 1679202802864);
INSERT INTO `panda`.`third_sport_market_category`(`sport_id`, `market_category_id`, `create_time`, `modify_time`) VALUES (1, 1637321343321632796, 1679202802864, 1679202802864);
INSERT INTO `panda`.`third_sport_market_category`(`sport_id`, `market_category_id`, `create_time`, `modify_time`) VALUES (1, 1637321343321632800, 1679202802864, 1679202802864);
INSERT INTO `panda`.`third_sport_market_category`(`sport_id`, `market_category_id`, `create_time`, `modify_time`) VALUES (1, 1637321343321632804, 1679202802864, 1679202802864);
INSERT INTO `panda`.`third_sport_market_category`(`sport_id`, `market_category_id`, `create_time`, `modify_time`) VALUES (1, 1637321343321632808, 1679202802864, 1679202802864);
INSERT INTO `panda`.`third_sport_market_category`(`sport_id`, `market_category_id`, `create_time`, `modify_time`) VALUES (1, 1637321343321632812, 1679202802864, 1679202802864);
INSERT INTO `panda`.`third_sport_market_category`(`sport_id`, `market_category_id`, `create_time`, `modify_time`) VALUES (1, 1637321343321632816, 1679202802864, 1679202802864);
INSERT INTO `panda`.`third_sport_market_category`(`sport_id`, `market_category_id`, `create_time`, `modify_time`) VALUES (1, 1640269016978812930, 1679905583029, 1679905583029);
INSERT INTO `panda`.`third_sport_market_category`(`sport_id`, `market_category_id`, `create_time`, `modify_time`) VALUES (1, 1640269016978812934, 1679905583029, 1679905583029);
INSERT INTO `panda`.`third_sport_market_category`(`sport_id`, `market_category_id`, `create_time`, `modify_time`) VALUES (1, 1640269016978812938, 1679905583029, 1679905583029);
INSERT INTO `panda`.`third_sport_market_category`(`sport_id`, `market_category_id`, `create_time`, `modify_time`) VALUES (1, 1640269016978812942, 1679905583029, 1679905583029);

/*修改标准玩法的时间，下游通过modify_time时间变更触发及时拉取上游变更过的玩法、球种玩法、投注项、国际化数据*/
update `panda`.`standard_market_category` set modify_time = UNIX_TIMESTAMP()*1000 where id in (114,122,118,307,121,115,116,123,124,229,306,308,111,119,309,113);




Delete from `panda`.`third_market_category` where data_source_code='LS' and third_source_id in('LS:63','LS:202','LS:203','LS:204','LS:205');

INSERT INTO `panda`.`third_market_category` (`id`, `name_code`, `fields_num`, `third_source_id`, `reference_id`, `data_source_code`, `active`, `create_time`, `modify_time`) VALUES (1558716971638714421, 1558716971638714422, 2, 'LS:202', 48, 'LS', 1, 1660462060256, 1660462060256);
INSERT INTO `panda`.`third_market_category` (`id`, `name_code`, `fields_num`, `third_source_id`, `reference_id`, `data_source_code`, `active`, `create_time`, `modify_time`) VALUES (1558716971634520110, 1558716971634520111, 2, 'LS:203', 54, 'LS', 1, 1660462060255, 1660462060255);
INSERT INTO `panda`.`third_market_category` (`id`, `name_code`, `fields_num`, `third_source_id`, `reference_id`, `data_source_code`, `active`, `create_time`, `modify_time`) VALUES (1558716971638714449, 1558716971638714450, 2, 'LS:204', 60, 'LS', 1, 1660462060256, 1660462060256);
INSERT INTO `panda`.`third_market_category` (`id`, `name_code`, `fields_num`, `third_source_id`, `reference_id`, `data_source_code`, `active`, `create_time`, `modify_time`) VALUES (1558716971634520094, 1558716971634520095, 2, 'LS:205', 66, 'LS', 1, 1660462060255, 1660462060255);
INSERT INTO `panda`.`third_market_category` (`id`, `name_code`, `fields_num`, `third_source_id`, `reference_id`, `data_source_code`, `active`, `create_time`, `modify_time`) VALUES (1558716971638714377, 1558716971638714378, 2, 'LS:63',  43, 'LS', 1, 1660462060256, 1660462060256);

DELETE FROM `panda`.`i18n_market_category` WHERE name_code in (1558716971638714422,1558716971634520111,1558716971638714450,1558716971634520095,1558716971638714378);

INSERT INTO `panda`.`i18n_market_category` (`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1558716971634520095, 2, 'LS', 'en', '4th Period Winner Home/Away', NULL, 1660462060496, 1660462060496);
INSERT INTO `panda`.`i18n_market_category` (`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1558716971634520111, 2, 'LS', 'en', '2nd Period Winner Home/Away', NULL, 1660462060496, 1660462060496);
INSERT INTO `panda`.`i18n_market_category` (`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1558716971638714378, 2, 'LS', 'en', '12 Halftime', NULL, 1660462060496, 1660462060496);
INSERT INTO `panda`.`i18n_market_category` (`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1558716971638714422, 2, 'LS', 'en', '1st Period Winner Home/Away', NULL, 1660462060496, 1660462060496);
INSERT INTO `panda`.`i18n_market_category` (`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1558716971638714450, 2, 'LS', 'en', '3rd Period Winner Home/Away', NULL, 1660462060496, 1660462060496);


delete from `panda`.`third_market_category_field` where third_source_id in ('LS:63:1','LS:63:2','LS:202:1','LS:202:2','LS:203:1','LS:203:2','LS:204:1',
                                                                            'LS:204:2','LS:205:1','LS:205:2');

INSERT INTO `panda`.`third_market_category_field` (`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES (1558716971638714421, 1558716991079313435, 'LS:202:1', 174, 1, 'LS', 1660462064891, 1660462064891);
INSERT INTO `panda`.`third_market_category_field` (`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES (1558716971638714421, 1558716991079313441, 'LS:202:2', 175, 2, 'LS', 1660462064891, 1660462064891);
INSERT INTO `panda`.`third_market_category_field` (`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES (1558716971634520110, 1558716991079313444, 'LS:203:1', 189, 1, 'LS', 1660462064891, 1660462064891);
INSERT INTO `panda`.`third_market_category_field` (`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES (1558716971634520110, 1558716991079313450, 'LS:203:2', 190, 2, 'LS', 1660462064891, 1660462064891);
INSERT INTO `panda`.`third_market_category_field` (`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES (1558716971638714449, 1558716991079313447, 'LS:204:1', 206, 1, 'LS', 1660462064891, 1660462064891);
INSERT INTO `panda`.`third_market_category_field` (`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES (1558716971638714449, 1558716991079313459, 'LS:204:2', 207, 2, 'LS', 1660462064891, 1660462064891);
INSERT INTO `panda`.`third_market_category_field` (`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES (1558716971634520094, 1558716991079313453, 'LS:205:1', 220, 1, 'LS', 1660462064891, 1660462064891);
INSERT INTO `panda`.`third_market_category_field` (`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES (1558716971634520094, 1558716991079313462, 'LS:205:2', 221, 2, 'LS', 1660462064891, 1660462064891);
INSERT INTO `panda`.`third_market_category_field` (`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES (1558716971638714377, 1558716991070924826, 'LS:63:1', 163, 1, 'LS', 1660462064889, 1660462064889);
INSERT INTO `panda`.`third_market_category_field` (`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES (1558716971638714377, 1558716991070924820, 'LS:63:2', 164, 2, 'LS', 1660462064889, 1660462064889);


DELETE FROM `panda`.`i18n_market_category` WHERE name_code in (1558716991079313435,1558716991079313441,1558716991079313444,1558716991079313450,1558716991079313447,
                                                               1558716991079313459,1558716991079313453,1558716991079313462,1558716991070924826,1558716991070924820);

INSERT INTO `panda`.`i18n_market_category` (`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1558716991070924820, 2, 'LS', 'en', '2', NULL, 1660462065400, 1660462065400);
INSERT INTO `panda`.`i18n_market_category` (`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1558716991070924826, 2, 'LS', 'en', '1', NULL, 1660462065400, 1660462065400);
INSERT INTO `panda`.`i18n_market_category` (`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1558716991079313435, 2, 'LS', 'en', '1', NULL, 1660462065400, 1660462065400);
INSERT INTO `panda`.`i18n_market_category` (`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1558716991079313441, 2, 'LS', 'en', '2', NULL, 1660462065400, 1660462065400);
INSERT INTO `panda`.`i18n_market_category` (`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1558716991079313444, 2, 'LS', 'en', '1', NULL, 1660462065400, 1660462065400);
INSERT INTO `panda`.`i18n_market_category` (`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1558716991079313447, 2, 'LS', 'en', '1', NULL, 1660462065400, 1660462065400);
INSERT INTO `panda`.`i18n_market_category` (`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1558716991079313450, 2, 'LS', 'en', '2', NULL, 1660462065400, 1660462065400);
INSERT INTO `panda`.`i18n_market_category` (`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1558716991079313453, 2, 'LS', 'en', '1', NULL, 1660462065400, 1660462065400);
INSERT INTO `panda`.`i18n_market_category` (`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1558716991079313459, 2, 'LS', 'en', '2', NULL, 1660462065400, 1660462065400);
INSERT INTO `panda`.`i18n_market_category` (`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES (1558716991079313462, 2, 'LS', 'en', '2', NULL, 1660462065400, 1660462065400);


DELETE FROM `panda`.`third_sport_market_category`
WHERE  sport_id = 2 and market_category_id in (SELECT id FROM `panda`.`third_market_category`
                                               WHERE third_source_id in ('LS:63','LS:202','LS:203','LS:204','LS:205'));


INSERT INTO `panda`.`third_sport_market_category` (`sport_id`, `market_category_id`, `create_time`, `modify_time`) VALUES (2, 1558716971638714421, 1660462060256, 1660462060256);
INSERT INTO `panda`.`third_sport_market_category` (`sport_id`, `market_category_id`, `create_time`, `modify_time`) VALUES (2, 1558716971634520110, 1660462060255, 1660462060255);
INSERT INTO `panda`.`third_sport_market_category` (`sport_id`, `market_category_id`, `create_time`, `modify_time`) VALUES (2, 1558716971638714449, 1660462060256, 1660462060256);
INSERT INTO `panda`.`third_sport_market_category` (`sport_id`, `market_category_id`, `create_time`, `modify_time`) VALUES (2, 1558716971634520094, 1660462060255, 1660462060255);
INSERT INTO `panda`.`third_sport_market_category` (`sport_id`, `market_category_id`, `create_time`, `modify_time`) VALUES (2, 1558716971638714377, 1660462060256, 1660462060256);


/*修改标准玩法的时间，下游通过modify_time时间变更触发及时拉取上游变更过的玩法、球种玩法、投注项、国际化数据*/
update `panda`.`standard_market_category` set modify_time = UNIX_TIMESTAMP()*1000 where id in (48,54,60,66,43);




Delete from `panda`.`third_market_category` where data_source_code='LS' and third_source_id in('LS:398','LS:400');
INSERT INTO `panda`.`third_market_category` (`id`, `name_code`, `fields_num`, `third_source_id`, `reference_id`, `data_source_code`, `active`, `create_time`, `modify_time`) VALUES (1645024369807323139, 1645024369807323140, 2, 'LS:400', 313, 'LS', 1, 1681039347493, 1681039347493);
INSERT INTO `panda`.`third_market_category` (`id`, `name_code`, `fields_num`, `third_source_id`, `reference_id`, `data_source_code`, `active`, `create_time`, `modify_time`) VALUES(1645024369857654787, 1645024369857654788, 2, 'LS:398', 312, 'LS', 1, 1681039347505, 1681039347505);

DELETE FROM `panda`.`i18n_market_category` WHERE name_code in (1645024369807323140,1645024369857654788);
INSERT INTO `panda`.`i18n_market_category` (`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES(1645024369807323140, 2, 'LS', 'en', 'Odd/Even Cards 1st Half', NULL, 1681039347581, 1681039347581);
INSERT INTO `panda`.`i18n_market_category` (`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES(1645024369857654788, 2, 'LS', 'en', 'Odd/Even Cards', NULL, 1681039347581, 1681039347581);


delete from `panda`.`third_market_category_field` where third_source_id in ('LS:400:1','LS:400:2','LS:398:1','LS:398:2');

INSERT INTO `panda`.`third_market_category_field` (`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES (1645024369857654787, 1645024417819521026, 'LS:398:1', 978, 1, 'LS', 1681039358940, 1681039358940);
INSERT INTO `panda`.`third_market_category_field` (`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES  (1645024369807323139, 1645024417819521029, 'LS:400:2', 981, 2, 'LS', 1681039358940, 1681039358940);
INSERT INTO `panda`.`third_market_category_field` (`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES  (1645024369807323139, 1645024417819521032, 'LS:400:1', 980, 1, 'LS', 1681039358940, 1681039358940);
INSERT INTO `panda`.`third_market_category_field` (`market_category_id`, `name_code`, `third_source_id`, `reference_id`, `order_no`, `data_source_code`, `create_time`, `modify_time`) VALUES  (1645024369857654787, 1645024417819521035, 'LS:398:2', 979, 2, 'LS', 1681039358940, 1681039358940);


DELETE FROM `panda`.`i18n_market_category` WHERE name_code in (1645024417819521026,1645024417819521029,1645024417819521032,1645024417819521035);
INSERT INTO `panda`.`i18n_market_category` (`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES(1645024417819521026, 2, 'LS', 'en', 'Odd', NULL, 1681039359078, 1681039359078);
INSERT INTO `panda`.`i18n_market_category` (`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES(1645024417819521029, 2, 'LS', 'en', 'Even', NULL, 1681039359078, 1681039359078);
INSERT INTO `panda`.`i18n_market_category` (`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES(1645024417819521032, 2, 'LS', 'en', 'Odd', NULL, 1681039359078, 1681039359078);
INSERT INTO `panda`.`i18n_market_category` (`name_code`, `flag`, `data_source_code`, `language_type`, `text`, `remark`, `create_time`, `modify_time`) VALUES(1645024417819521035, 2, 'LS', 'en', 'Even', NULL, 1681039359078, 1681039359078);

DELETE FROM `panda`.`third_sport_market_category`
WHERE  sport_id = 1 and market_category_id in (SELECT id FROM `panda`.`third_market_category`
                                               WHERE third_source_id in ('LS:398','LS:400'));

INSERT INTO `panda`.`third_sport_market_category` (`sport_id`, `market_category_id`, `create_time`, `modify_time`) VALUES (1, 1645024369807323139, 1681039347493, 1681039347493);
INSERT INTO `panda`.`third_sport_market_category` (`sport_id`, `market_category_id`, `create_time`, `modify_time`) VALUES (1, 1645024369857654787, 1681039347505, 1681039347505);

/*修改标准玩法的时间，下游通过modify_time时间变更触发及时拉取上游变更过的玩法、球种玩法、投注项、国际化数据*/
update `panda`.`standard_market_category` set modify_time = UNIX_TIMESTAMP()*1000 where id in (312,313);