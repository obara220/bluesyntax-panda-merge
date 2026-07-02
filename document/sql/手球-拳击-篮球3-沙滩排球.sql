#手球赛种
DELETE FROM standard_sport_market_category WHERE sport_id = 11 and market_category_id in (1,4,2,259,17,19,18,6,5,15,70,43,42,127,128);
INSERT INTO standard_sport_market_category (sport_id, market_category_id, name_code, desc_name_code, is_collapse, scope_id, status, order_no, template_h5, template_pc, create_time, modify_time, template_pc_client, template_h5_client) VALUES
(11, 1, 1258000527954808833, 1260115355999715330, 1, '3', 1, 1, 1, 1, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 1, 1),
(11, 2, 1258000528063860737, 1273580487320846338, 1, '3', 1, 5, 5, 5, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 5, 13),
(11, 4, 1258000528189689858, 1301344986148683777, 1, '3', 1, 3, 7, 2, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 2, 13),
(11, 5, 1258000528252604418, 1329345847147675649, 0, '3', 1, 38, 3, 3, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 3, 3),
(11, 6, 1258000528319713282, 1329327114937221122, 1, '3', 1, 37, 0, 1, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 1, 0),
(11, 15, 1258000528760115202, 1331445211802419201, 1, '3', 1, 31, 3, 3, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 3, 3),
(11, 17, 1258000528827224065, 1260484613577424898, 1, '1', 1, 2, 1, 1, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 1, 1),
(11, 18, 1258000528885944321, 1355514849771175938, 1, '1', 1, 6, 5, 5, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 5, 13),
(11, 19, 1258000528944664578, 1355514896978067457, 1, '1', 1, 4, 2, 2, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 2, 13),
(11, 42, 1258000529653501954, 1355515887123218433, 0, '1', 1, 32, 3, 3, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 3, 3),
(11, 43, 1258000529712222210, 1355516033563148290, 0, '1', 1, 57, 3, 3, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 3, 3),
(11, 70, 1258000530823712769, 1355516163804676097, 0, '1', 1, 58, 0, 1, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 1, 0),
(11, 127, 1258000534711832578, 1301352943783559170, 1, '3', 1, 50, 5, 5, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 5, 13),
(11, 128, 1258000534766358530, 1301353019029372930, 1, '3', 1, 51, 2, 2, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 2, 13),
(11, 259, 1599473580729833496, 1599473580932534243, 1, '3', 1, 160, 0, 3, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 3, 0);

#拳击
DELETE FROM standard_sport_market_category WHERE sport_id = 12 and market_category_id in (153,2,337,338,339);
INSERT INTO standard_sport_market_category (sport_id, market_category_id, name_code, desc_name_code, is_collapse, scope_id, status, order_no, template_h5, template_pc, create_time, modify_time, template_pc_client, template_h5_client) VALUES
(12, 2, 1258000528063860737, 1263549386614710274, 1, '3', 1, 3, 5, 5, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 5, 13),
/*
(12, 337, 0, 0, 1, '3', 1, 3, 5, 5, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 5, 13),
(12, 338, 0, 0, 1, '3', 1, 3, 5, 5, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 5, 13),
(12, 339, 0, 0, 1, '3', 1, 3, 5, 5, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 5, 13),
*/
(12, 153, 1258000535978512386, 1260439771690352641, 1, '3', 1, 1, 0, 3, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 3, 0);

#篮球3*3
DELETE FROM standard_sport_market_category WHERE sport_id = 2 and market_category_id = 153;
INSERT INTO standard_sport_market_category (sport_id, market_category_id, name_code, desc_name_code, is_collapse, scope_id, status, order_no, template_h5, template_pc, create_time, modify_time, template_pc_client, template_h5_client) VALUES
(2, 153, 1258000535978512386, 1260439771690352641, 1, '3', 1, 1, 0, 3, UNIX_TIMESTAMP()*1000,  UNIX_TIMESTAMP()*1000, 3, 0);

#沙滩排球
DELETE FROM standard_sport_market_category WHERE sport_id = 13 and market_category_id in (153,172,173,159,204,162,253,254,255,256);
INSERT INTO standard_sport_market_category (sport_id, market_category_id, name_code, desc_name_code, is_collapse, scope_id, status, order_no, template_h5, template_pc, create_time, modify_time, template_pc_client, template_h5_client) VALUES
(13, 153, 1258000535978512386, 1260439771690352641, 1, '3', 1, 1, 0, 3, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 3, 0),
(13, 159, 1258000536574103554, 1260439986925256705, 1, '3', 1, 7, 0, 0, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 0, 15),
(13, 162, 1258000536737681409, 1260440193444397057, 1, '14', 1, 12, 0, 3, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 3, 0),
(13, 172, 1258000537312301058, 1260447232459587585, 1, '3', 1, 2, 2, 2, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 2, 13),
(13, 173, 1258000537417158658, 1260447238839123969, 1, '3', 1, 3, 5, 5, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 5, 13),
(13, 204, 1278671933705031681, 1280842131251970050, 1, '3', 1, 21, 6, 6, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 6, 6),
(13, 253, 1599471332402642860, 1599471332195521938, 1, '17', 1, 50, 2, 2, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 2, 13),
(13, 254, 1599471332569681136, 1599471332361839895, 1, '17', 1, 60, 2, 2, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 2, 13),
(13, 255, 1599471332900156094, 1599471332615260813, 1, '17', 1, 90, 3, 3, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 3, 3),
(13, 256, 1599471332275835808, 1599471332333396628, 1, '17', 1, 100, 3, 3, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000, 3, 3);

-- -----------------------------------------------三方赛种表--------------------------------------------------------------------
#SR手球
DELETE FROM third_sport_market_category where sport_id = 11 and market_category_id in (6715, 7000, 6608, 7096, 6706, 6788, 7114, 6570, 6842, 6502, 6494);
INSERT INTO third_sport_market_category (sport_id, market_category_id, create_time, modify_time) VALUES

(11, 7192, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(11, 6395, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(11, 7279, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(11, 6629, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),

(11, 6715, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(11, 7000, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(11, 6608, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(11, 7096, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(11, 6706, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(11, 6788, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(11, 7114, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(11, 6570, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(11, 6842, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(11, 6502, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(11, 6494, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
#SR拳击
DELETE FROM third_sport_market_category where sport_id = 12 and market_category_id in (7114, 7293, 6892, 6411);
INSERT INTO third_sport_market_category (sport_id, market_category_id, create_time, modify_time) VALUES
(12, 7114, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(12, 7293, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(12, 6892, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(12, 6876, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(12, 6411, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
#SR篮球3*3
DELETE FROM third_sport_market_category where sport_id = 2 and market_category_id =6876;
INSERT INTO third_sport_market_category (sport_id, market_category_id, create_time, modify_time) VALUES
(2, 6876, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
#SR沙滩排球
DELETE FROM third_sport_market_category where sport_id = 13 and market_category_id in (6876,6855,7311,6613,6534,6828,7215,7346,6485,7373);
INSERT INTO third_sport_market_category (sport_id, market_category_id, create_time, modify_time) VALUES
(13, 6876, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(13, 6855, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(13, 7311, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(13, 6613, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(13, 6534, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(13, 6828, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(13, 7215, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(13, 7346, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(13, 6485, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(13, 7373, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);

update third_market_category set reference_id = 337 where third_source_id = 'SR:910';
update third_market_category set reference_id = 338 where third_source_id = 'SR:911';
update third_market_category set reference_id = 339 where third_source_id = 'SR:912';

update third_market_category_field set reference_id = 1052 where third_source_id = 'SR:911:74';
update third_market_category_field set reference_id = 1053 where third_source_id = 'SR:911:76';

#盘口表name_code:337,338,339
DELETE FROM i18n_market_category WHERE name_code in (1624982167930910901,1624982167523093989,1624982167528186358);
INSERT INTO i18n_market_category (name_code, flag, data_source_code, language_type, text, remark, create_time, modify_time) VALUES
/*339*/
(1624982167930910901, 2, 'SR', 'zs', '胜者 & 精确回合', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167930910901, 2, 'SR', 'zh', '優勝者和確切回合', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167930910901, 2, 'SR', 'en', 'Winner & exact rounds', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167930910901, 2, 'SR', 'es', 'Ganador y rounds exactos', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167930910901, 2, 'SR', 'it', 'Vincitore & round esatto', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167930910901, 2, 'SR', 'de', 'Winner & exact rounds', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167930910901, 2, 'SR', 'fr', 'Vainqueur et nombre exact de rounds', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167930910901, 2, 'SR', 'pb', 'Winner & exact rounds', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167930910901, 2, 'SR', 'ru', 'Победитель и кол-во раундов', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167930910901, 2, 'SR', 'ja', 'Winner & exact rounds', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167930910901, 2, 'SR', 'ko', '승자 & 정확한 라운드', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167930910901, 2, 'SR', 'th', 'Winner & exact rounds', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167930910901, 2, 'SR', 'vi', 'Winner & exact rounds', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
/*338*/
(1624982167523093989, 2, 'SR', 'zs', '战斗会进行到底吗', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167523093989, 2, 'SR', 'zh', '戰鬥會遠嗎', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167523093989, 2, 'SR', 'en', 'Will the fight go the distance', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167523093989, 2, 'SR', 'es', 'La pelea se durará hasta', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167523093989, 2, 'SR', 'it', 'L\'incontro finirà ai punti', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167523093989, 2, 'SR', 'de', 'Will the fight go the distance', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167523093989, 2, 'SR', 'fr', 'Est ce que tout les rounds vont avoir lieu ?', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167523093989, 2, 'SR', 'pb', 'Will the fight go the distance', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167523093989, 2, 'SR', 'ru', 'Будет ли бой продолжительным', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167523093989, 2, 'SR', 'ja', 'Will the fight go the distance', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167523093989, 2, 'SR', 'ko', '경기가 끝까지 갈 수 있을 가요?', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167523093989, 2, 'SR', 'th', 'Will the fight go the distance', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167523093989, 2, 'SR', 'vi', 'Will the fight go the distance', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
/*337*/
(1624982167528186358, 2, 'SR', 'zs', '获胜方法', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167528186358, 2, 'SR', 'zh', '取勝方法', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167528186358, 2, 'SR', 'en', 'Winning method', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167528186358, 2, 'SR', 'es', 'Método de victoria', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167528186358, 2, 'SR', 'it', 'Metodo Vittoria', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167528186358, 2, 'SR', 'de', 'Winning method', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167528186358, 2, 'SR', 'fr', 'Méthode de victoire', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167528186358, 2, 'SR', 'pb', 'Winning method', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167528186358, 2, 'SR', 'ru', 'Метод победы', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167528186358, 2, 'SR', 'ja', 'Winning method', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167528186358, 2, 'SR', 'ko', '우승 방법', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167528186358, 2, 'SR', 'th', 'Winning method', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624982167528186358, 2, 'SR', 'vi', 'Phương pháp thắng', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

#赛种表name_code:337,338,339
#337,338,339盘口多语言'
DELETE FROM i18n_market_category WHERE name_code in (1624983039374593105,1624983039688615881,1624983039389495443);
INSERT INTO i18n_market_category (name_code, flag, data_source_code, language_type, text, remark, create_time, modify_time) VALUES
/*339*/
(1624983039374593105, 2, 'SR', 'zs', '胜者 & 精确回合', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039374593105, 2, 'SR', 'zh', '優勝者和確切回合', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039374593105, 2, 'SR', 'en', 'Winner & exact rounds', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039374593105, 2, 'SR', 'es', 'Ganador y rounds exactos', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039374593105, 2, 'SR', 'it', 'Vincitore & round esatto', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039374593105, 2, 'SR', 'de', 'Winner & exact rounds', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039374593105, 2, 'SR', 'fr', 'Vainqueur et nombre exact de rounds', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039374593105, 2, 'SR', 'pb', 'Winner & exact rounds', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039374593105, 2, 'SR', 'ru', 'Победитель и кол-во раундов', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039374593105, 2, 'SR', 'ja', 'Winner & exact rounds', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039374593105, 2, 'SR', 'ko', '승자 & 정확한 라운드', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039374593105, 2, 'SR', 'th', 'Winner & exact rounds', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039374593105, 2, 'SR', 'vi', 'Winner & exact rounds', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
/*338*/
(1624983039688615881, 2, 'SR', 'zs', '战斗会进行到底吗', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039688615881, 2, 'SR', 'zh', '戰鬥會遠嗎', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039688615881, 2, 'SR', 'en', 'Will the fight go the distance', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039688615881, 2, 'SR', 'es', 'La pelea se durará hasta', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039688615881, 2, 'SR', 'it', 'L\'incontro finirà ai punti', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039688615881, 2, 'SR', 'de', 'Will the fight go the distance', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039688615881, 2, 'SR', 'fr', 'Est ce que tout les rounds vont avoir lieu ?', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039688615881, 2, 'SR', 'pb', 'Will the fight go the distance', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039688615881, 2, 'SR', 'ru', 'Будет ли бой продолжительным', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039688615881, 2, 'SR', 'ja', 'Will the fight go the distance', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039688615881, 2, 'SR', 'ko', '경기가 끝까지 갈 수 있을 가요?', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039688615881, 2, 'SR', 'th', 'Will the fight go the distance', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039688615881, 2, 'SR', 'vi', 'Will the fight go the distance', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
/*337*/
(1624983039389495443, 2, 'SR', 'zs', '获胜方法', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039389495443, 2, 'SR', 'zh', '取勝方法', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039389495443, 2, 'SR', 'en', 'Winning method', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039389495443, 2, 'SR', 'es', 'Método de victoria', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039389495443, 2, 'SR', 'it', 'Metodo Vittoria', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039389495443, 2, 'SR', 'de', 'Winning method', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039389495443, 2, 'SR', 'fr', 'Méthode de victoire', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039389495443, 2, 'SR', 'pb', 'Winning method', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039389495443, 2, 'SR', 'ru', 'Метод победы', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039389495443, 2, 'SR', 'ja', 'Winning method', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039389495443, 2, 'SR', 'ko', '우승 방법', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039389495443, 2, 'SR', 'th', 'Winning method', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983039389495443, 2, 'SR', 'vi', 'Phương pháp thắng', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

#赛种表desc_name_code:337,338,339'
DELETE FROM i18n_market_category WHERE name_code in (1624983746333486671,1624983746415587560,1624983746548150386);
INSERT INTO i18n_market_category (name_code, flag, data_source_code, language_type, text, remark, create_time, modify_time) VALUES
/*339*/
(1624983746333486671, 2, 'SR', 'zs', '胜者 & 精确回合', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746333486671, 2, 'SR', 'zh', '優勝者和確切回合', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746333486671, 2, 'SR', 'en', 'Winner & exact rounds', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746333486671, 2, 'SR', 'es', 'Ganador y rounds exactos', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746333486671, 2, 'SR', 'it', 'Vincitore & round esatto', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746333486671, 2, 'SR', 'de', 'Winner & exact rounds', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746333486671, 2, 'SR', 'fr', 'Vainqueur et nombre exact de rounds', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746333486671, 2, 'SR', 'pb', 'Winner & exact rounds', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746333486671, 2, 'SR', 'ru', 'Победитель и кол-во раундов', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746333486671, 2, 'SR', 'ja', 'Winner & exact rounds', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746333486671, 2, 'SR', 'ko', '승자 & 정확한 라운드', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746333486671, 2, 'SR', 'th', 'Winner & exact rounds', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746333486671, 2, 'SR', 'vi', 'Winner & exact rounds', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
/*338*/
(1624983746415587560, 2, 'SR', 'zs', '战斗会进行到底吗', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746415587560, 2, 'SR', 'zh', '戰鬥會遠嗎', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746415587560, 2, 'SR', 'en', 'Will the fight go the distance', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746415587560, 2, 'SR', 'es', 'La pelea se durará hasta', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746415587560, 2, 'SR', 'it', 'L\'incontro finirà ai punti', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746415587560, 2, 'SR', 'de', 'Will the fight go the distance', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746415587560, 2, 'SR', 'fr', 'Est ce que tout les rounds vont avoir lieu ?', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746415587560, 2, 'SR', 'pb', 'Will the fight go the distance', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746415587560, 2, 'SR', 'ru', 'Будет ли бой продолжительным', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746415587560, 2, 'SR', 'ja', 'Will the fight go the distance', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746415587560, 2, 'SR', 'ko', '경기가 끝까지 갈 수 있을 가요?', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746415587560, 2, 'SR', 'th', 'Will the fight go the distance', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746415587560, 2, 'SR', 'vi', 'Will the fight go the distance', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
/*337*/
(1624983746548150386, 2, 'SR', 'zs', '获胜方法', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746548150386, 2, 'SR', 'zh', '取勝方法', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746548150386, 2, 'SR', 'en', 'Winning method', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746548150386, 2, 'SR', 'es', 'Método de victoria', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746548150386, 2, 'SR', 'it', 'Metodo Vittoria', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746548150386, 2, 'SR', 'de', 'Winning method', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746548150386, 2, 'SR', 'fr', 'Méthode de victoire', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746548150386, 2, 'SR', 'pb', 'Winning method', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746548150386, 2, 'SR', 'ru', 'Метод победы', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746548150386, 2, 'SR', 'ja', 'Winning method', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746548150386, 2, 'SR', 'ko', '우승 방법', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746548150386, 2, 'SR', 'th', 'Winning method', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1624983746548150386, 2, 'SR', 'vi', 'Phương pháp thắng', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

DELETE FROM i18n_market_category WHERE name_code in (1624984629306225781,1624984629704884509);
INSERT INTO i18n_market_category (name_code, flag, data_source_code, language_type, text, remark, create_time, modify_time) VALUES
/*1052*/
(1624984629306225781, 2, 'SR', 'zs', '是', '', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1624984629306225781, 2, 'SR', 'zh', '是', '', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1624984629306225781, 2, 'SR', 'en', 'yes', '', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1624984629306225781, 2, 'SR', 'es', 'sí', '', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1624984629306225781, 2, 'SR', 'it', 'sì', '', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1624984629306225781, 2, 'SR', 'de', 'ja', '', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1624984629306225781, 2, 'SR', 'fr', 'oui', '', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1624984629306225781, 2, 'SR', 'pb', 'yes', '', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1624984629306225781, 2, 'SR', 'ru', 'да', '', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1624984629306225781, 2, 'SR', 'ja', 'はい', '', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1624984629306225781, 2, 'SR', 'ko', '예', '', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1624984629306225781, 2, 'SR', 'th', 'ใช่', '', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1624984629306225781, 2, 'SR', 'vi', 'có', '', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
/*1053*/
(1624984629704884509, 2, 'SR', 'zs', '否', '', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1624984629704884509, 2, 'SR', 'zh', '否', '', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1624984629704884509, 2, 'SR', 'en', 'no', '', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1624984629704884509, 2, 'SR', 'es', 'no', '', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1624984629704884509, 2, 'SR', 'it', 'no', '', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1624984629704884509, 2, 'SR', 'de', 'nein', '', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1624984629704884509, 2, 'SR', 'fr', 'non', '', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1624984629704884509, 2, 'SR', 'pb', 'no', '', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1624984629704884509, 2, 'SR', 'ru', 'нет', '', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1624984629704884509, 2, 'SR', 'ja', 'いいえ', '', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1624984629704884509, 2, 'SR', 'ko', '아니오', '', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1624984629704884509, 2, 'SR', 'th', 'ไม่', '', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(1624984629704884509, 2, 'SR', 'vi', 'không', '', UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);

update standard_market_category set modify_time = UNIX_TIMESTAMP()*1000 where id in(337,338,339);

#del panda-merge::ThirdMarketCategory:SR-SR:910
#del panda-merge::ThirdMarketCategory:SR-SR:911
#del panda-merge::ThirdMarketCategory:SR-SR:912

#del panda-merge::ThirdMarketCategoryField:15255-SR:911:74
#del panda-merge::ThirdMarketCategoryField:15287-SR:911:76
