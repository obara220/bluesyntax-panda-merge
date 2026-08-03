UPDATE `panda`.`i18n_market_category` SET `text` = '无第 {X} 个进球' WHERE `name_code` = 1647586118925229464  and `data_source_code` = 'AO'  and `language_type` = 'zs';
UPDATE `panda`.`i18n_market_category` SET `text` = 'No {X} Goal' WHERE `name_code` = 1647586118925229464  and `data_source_code` = 'AO'  and `language_type` = 'en';
UPDATE `panda`.`i18n_market_category` SET `text` = 'No {X} Goal' WHERE `name_code` = 1647586118925229464 and `data_source_code` = 'PA'  and `language_type` = 'en';
UPDATE `panda`.`i18n_market_category` SET `text` = '无第 {X} 个进球' WHERE `name_code` = 1647586118925229464 and `data_source_code` = 'PA' and `language_type` = 'zs';
UPDATE `panda`.`i18n_market_category` SET `text` = '無第 {X} 個進球' WHERE `name_code` = 1647586118925229464 and `data_source_code` = 'PA'  and `language_type` = 'zh';
UPDATE `panda`.`i18n_market_category` SET `text` = 'Không có BT thứ {X}' WHERE `name_code` = 1647586118925229464 and `data_source_code` = 'PA'   and `language_type` = 'vi';
UPDATE `panda`.`i18n_market_category` SET `text` = 'ไม่มีประตูที่ {X}' WHERE `name_code` = 1647586118925229464 and `data_source_code` = 'PA'  and `language_type` = 'th';
UPDATE `panda`.`i18n_market_category` SET `text` = 'No {X} Goal' WHERE `name_code` = 1647586118925229464 and `data_source_code` = 'PA'  and `language_type` = 'ms';
UPDATE `panda`.`i18n_market_category` SET `text` = 'No Goal ke {X}' WHERE `name_code` = 1647586118925229464 and `data_source_code` = 'PA'  and `language_type` = 'ad';

UPDATE `panda`.`i18n_market_category` SET `text` = '无进球(0-0)' WHERE `name_code` = 1647586118925229444  and `data_source_code` = 'AO'  and `language_type` = 'zs';
UPDATE `panda`.`i18n_market_category` SET `text` = 'No goal(0-0)' WHERE `name_code` = 1647586118925229444  and `data_source_code` = 'AO'  and `language_type` = 'en';
UPDATE `panda`.`i18n_market_category` SET `text` = 'No goal(0-0)' WHERE `name_code` = 1647586118925229444 and `data_source_code` = 'PA'  and `language_type` = 'en';
UPDATE `panda`.`i18n_market_category` SET `text` = '无进球(0-0)' WHERE `name_code` = 1647586118925229444 and `data_source_code` = 'PA' and `language_type` = 'zs';
UPDATE `panda`.`i18n_market_category` SET `text` = '無進球(0-0)' WHERE `name_code` = 1647586118925229444 and `data_source_code` = 'PA'  and `language_type` = 'zh';
UPDATE `panda`.`i18n_market_category` SET `text` = 'Không BT(0-0)' WHERE `name_code` = 1647586118925229444 and `data_source_code` = 'PA'   and `language_type` = 'vi';
UPDATE `panda`.`i18n_market_category` SET `text` = 'ยิงพลาด(0-0)' WHERE `name_code` = 1647586118925229444 and `data_source_code` = 'PA'  and `language_type` = 'th';
UPDATE `panda`.`i18n_market_category` SET `text` = 'No goal(0-0)' WHERE `name_code` = 1647586118925229444 and `data_source_code` = 'PA'  and `language_type` = 'ms';
UPDATE `panda`.`i18n_market_category` SET `text` = 'No goal(0-0)' WHERE `name_code` = 1647586118925229444 and `data_source_code` = 'PA'  and `language_type` = 'ad';
UPDATE `panda`.`standard_market_category` set modify_time = UNIX_TIMESTAMP()*1000 where id in (361,362);

UPDATE `panda`.`language_internation_2` SET `text` = 'Bola Basket' WHERE `name_code` = 2 and `data_source_code` = 'PA'  and `language_type` = 'ad';
UPDATE `panda`.`standard_sport_type` set modify_time = UNIX_TIMESTAMP()*1000 where id = 2;
UPDATE `panda`.`language_internation_2` set modify_time = UNIX_TIMESTAMP()*1000 where `data_source_code` = 'PA' and name_code =2;


UPDATE `panda_virtual`.`virtual_language_internation` SET `text` = 'Bola Basket VR' WHERE `name_code` = 1004 and `data_source_code` = 'GR'  and `language_type` = 'ad';
UPDATE `panda_virtual`.`virtual_sport_type` set modify_time = UNIX_TIMESTAMP()*1000 where virtual_sport_id = 1004;
UPDATE `panda_virtual`.`virtual_language_internation` set modify_time = UNIX_TIMESTAMP()*1000 where `data_source_code` = 'GR' and name_code = 1004;



-------==============================================================================================



UPDATE `panda`.`i18n_market_category` SET `text` = '5分钟时段进球' WHERE `name_code` = 1647586118925229421  and `data_source_code` = 'AO'  and `language_type` = 'zs';
UPDATE `panda`.`i18n_market_category` SET `text` = '5 minutes goal' WHERE `name_code` = 1647586118925229421  and `data_source_code` = 'AO'  and `language_type` = 'en';
UPDATE `panda`.`i18n_market_category` SET `text` = '5 minutes goal' WHERE `name_code` = 1647586118925229421 and `data_source_code` = 'PA'  and `language_type` = 'en';
UPDATE `panda`.`i18n_market_category` SET `text` = '5分钟时段进球' WHERE `name_code` = 1647586118925229421 and `data_source_code` = 'PA' and `language_type` = 'zs';
UPDATE `panda`.`i18n_market_category` SET `text` = '5分鐘時段進球' WHERE `name_code` = 1647586118925229421 and `data_source_code` = 'PA'  and `language_type` = 'zh';
UPDATE `panda`.`i18n_market_category` SET `text` = 'BT 5 phút' WHERE `name_code` = 1647586118925229421 and `data_source_code` = 'PA'   and `language_type` = 'vi';
UPDATE `panda`.`i18n_market_category` SET `text` = 'ทำประตูภายใน 5 นาที' WHERE `name_code` = 1647586118925229421 and `data_source_code` = 'PA'  and `language_type` = 'th';
UPDATE `panda`.`i18n_market_category` SET `text` = 'Sesi 5 Minit' WHERE `name_code` = 1647586118925229421 and `data_source_code` = 'PA'  and `language_type` = 'ms';
UPDATE `panda`.`i18n_market_category` SET `text` = '5 min gol' WHERE `name_code` = 1647586118925229421 and `data_source_code` = 'PA'  and `language_type` = 'ad';


UPDATE `panda`.`i18n_market_category` SET `text` = '5分钟时段进球' WHERE `name_code` = 1647586118925229423  and `data_source_code` = 'AO'  and `language_type` = 'zs';
UPDATE `panda`.`i18n_market_category` SET `text` = '5 minutes goal' WHERE `name_code` = 1647586118925229423  and `data_source_code` = 'AO'  and `language_type` = 'en';
UPDATE `panda`.`i18n_market_category` SET `text` = '5 minutes goal' WHERE `name_code` = 1647586118925229423 and `data_source_code` = 'PA'  and `language_type` = 'en';
UPDATE `panda`.`i18n_market_category` SET `text` = '5分钟时段进球' WHERE `name_code` = 1647586118925229423 and `data_source_code` = 'PA' and `language_type` = 'zs';
UPDATE `panda`.`i18n_market_category` SET `text` = '5分鐘時段進球' WHERE `name_code` = 1647586118925229423 and `data_source_code` = 'PA'  and `language_type` = 'zh';

UPDATE `panda`.`i18n_market_category` SET `text` = 'BT 5 phút' WHERE `name_code` = 1647586118925229423 and `data_source_code` = 'PA'   and `language_type` = 'vi';
UPDATE `panda`.`i18n_market_category` SET `text` = 'ทำประตูภายใน 5 นาที' WHERE `name_code` = 1647586118925229423 and `data_source_code` = 'PA'  and `language_type` = 'th';

UPDATE `panda`.`i18n_market_category` SET `text` = 'Sesi 5 Minit' WHERE `name_code` = 1647586118925229423 and `data_source_code` = 'PA'  and `language_type` = 'ms';
UPDATE `panda`.`i18n_market_category` SET `text` = '5 min gol' WHERE `name_code` = 1647586118925229423 and `data_source_code` = 'PA'  and `language_type` = 'ad';


UPDATE `panda`.`i18n_market_category` SET `text` = '5分钟时段进球' WHERE `name_code` = 1647586118925229424  and `data_source_code` = 'AO'  and `language_type` = 'zs';
UPDATE `panda`.`i18n_market_category` SET `text` = '5 minutes goal' WHERE `name_code` = 1647586118925229424  and `data_source_code` = 'AO'  and `language_type` = 'en';
UPDATE `panda`.`i18n_market_category` SET `text` = 'Sesi 5 Minit' WHERE `name_code` = 1647586118925229424 and `data_source_code` = 'PA'  and `language_type` = 'ms';
UPDATE `panda`.`i18n_market_category` SET `text` = '5 min gol' WHERE `name_code` = 1647586118925229424 and `data_source_code` = 'PA'  and `language_type` = 'ad';



UPDATE `panda`.`i18n_market_category` SET `text` = '{X} Goal (5min)' WHERE `name_code` = 1647586118925229422 and `data_source_code` = 'AO' and `language_type` = 'en';
UPDATE `panda`.`i18n_market_category` SET `text` = '{X} Goal (5min)' WHERE `name_code` = 1647586118925229422 and `data_source_code` = 'PA' and `language_type` = 'en';



UPDATE `panda`.`standard_market_category` set modify_time = UNIX_TIMESTAMP()*1000 where id in(361,362);



