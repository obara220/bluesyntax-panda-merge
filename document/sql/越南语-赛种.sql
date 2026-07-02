delete from language_internation where language_type = 'vi' and name_code in (1,2,3,4,5,6,7,8,9,10,18,100,101,102,103,104);
INSERT INTO language_internation (name_code, flag, data_source_code, language_type, text, remark, create_time, modify_time) VALUES
(1, 2, 'PA', 'vi', 'Bóng Đá', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(2, 2, 'PA', 'vi', 'Bóng Rổ', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(3, 2, 'PA', 'vi', 'Bóng Chày', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(4, 2, 'PA', 'vi', 'Khúc Côn Cầu', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(5, 2, 'PA', 'vi', 'Quần Vợt', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(6, 2, 'PA', 'vi', 'Bóng Bầu Dục', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(7, 2, 'PA', 'vi', 'Bida', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(8, 2, 'PA', 'vi', 'Bóng Bàn', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(9, 2, 'PA', 'vi', 'Bóng Chuyền', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(10, 2, 'PA', 'vi', 'Cầu Lông', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(18, 2, 'PA', 'vi', 'Chính Trị Giải Trí', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(100, 2, 'PA', 'vi', '英雄联盟', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(101, 2, 'PA', 'vi', 'Dota2', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(102, 2, 'PA', 'vi', 'cs', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(103, 2, 'PA', 'vi', '王者荣耀', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(104, 2, 'PA', 'vi', '绝地求生', '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

update standard_sport_type set modify_time = UNIX_TIMESTAMP() * 1000;