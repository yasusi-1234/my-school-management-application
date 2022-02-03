-- ロール
insert into role(role_name) values('ROLE_STUDENT');
insert into role(role_name) values('ROLE_TEACHER');

-- ユーザー
-- 生徒
-- insert into app_user(user_name, password, first_name, last_name, role_id) values('taro-yamada', 'sample', '太郎', '山田', 1);
-- insert into app_user(user_name, password, first_name, last_name, role_id) values('hanako-takahashi', 'sample', '花子', '高橋', 1);
-- insert into app_user(user_name, password, first_name, last_name, role_id) values('ichiro-suzuki', 'sample', '一郎', '鈴木', 1);
-- 教師 1
insert into app_user(user_name, password, first_name, last_name, role_id) values('nobunaga-oda', '$2a$08$PtMBmcbmd/L6xkNgu.goyuRpCwHpunatpifBGK12TtI2kw8Ed3Adu', '信長', '織田', 2);
insert into app_user(user_name, password, first_name, last_name, role_id) values('ieyasu-tokugawa', '$2a$08$PtMBmcbmd/L6xkNgu.goyuRpCwHpunatpifBGK12TtI2kw8Ed3Adu', '家康', '徳川', 2);
insert into app_user(user_name, password, first_name, last_name, role_id) values('hideyoshi-toyotomi', '$2a$08$PtMBmcbmd/L6xkNgu.goyuRpCwHpunatpifBGK12TtI2kw8Ed3Adu', '秀吉', '豊臣', 2);
insert into app_user(user_name, password, first_name, last_name, role_id) values('mituhide-akechi', '$2a$08$PtMBmcbmd/L6xkNgu.goyuRpCwHpunatpifBGK12TtI2kw8Ed3Adu', '光秀', '明智', 2);
insert into app_user(user_name, password, first_name, last_name, role_id) values('shingen-takeda', '$2a$08$PtMBmcbmd/L6xkNgu.goyuRpCwHpunatpifBGK12TtI2kw8Ed3Adu', '信玄', '武田', 2);
-- 教師 2
insert into app_user(user_name, password, first_name, last_name, role_id) values('motochika-tyousokabe', '$2a$08$PtMBmcbmd/L6xkNgu.goyuRpCwHpunatpifBGK12TtI2kw8Ed3Adu', '元親', '長宗我部', 2);
insert into app_user(user_name, password, first_name, last_name, role_id) values('yoshimoto-imagawa', '$2a$08$PtMBmcbmd/L6xkNgu.goyuRpCwHpunatpifBGK12TtI2kw8Ed3Adu', '義元', '今川', 2);
insert into app_user(user_name, password, first_name, last_name, role_id) values('kenshin-uesug', '$2a$08$PtMBmcbmd/L6xkNgu.goyuRpCwHpunatpifBGK12TtI2kw8Ed3Adu', '謙信', '上杉', 2);
insert into app_user(user_name, password, first_name, last_name, role_id) values('dousan-saitou', '$2a$08$PtMBmcbmd/L6xkNgu.goyuRpCwHpunatpifBGK12TtI2kw8Ed3Adu', '道三', '斉藤', 2);
insert into app_user(user_name, password, first_name, last_name, role_id) values('motonari-mouri', '$2a$08$PtMBmcbmd/L6xkNgu.goyuRpCwHpunatpifBGK12TtI2kw8Ed3Adu', '元就', '毛利', 2);
-- 教師 3
insert into app_user(user_name, password, first_name, last_name, role_id) values('masamune-date', '$2a$08$PtMBmcbmd/L6xkNgu.goyuRpCwHpunatpifBGK12TtI2kw8Ed3Adu', '政宗', '伊達', 2);
insert into app_user(user_name, password, first_name, last_name, role_id) values('yoshikage-asakura', '$2a$08$PtMBmcbmd/L6xkNgu.goyuRpCwHpunatpifBGK12TtI2kw8Ed3Adu', '義景', '朝倉', 2);
insert into app_user(user_name, password, first_name, last_name, role_id) values('yoshihiro-shimazu', '$2a$08$PtMBmcbmd/L6xkNgu.goyuRpCwHpunatpifBGK12TtI2kw8Ed3Adu', '義弘', '島津', 2);
insert into app_user(user_name, password, first_name, last_name, role_id) values('kennyo-honganzi', '$2a$08$PtMBmcbmd/L6xkNgu.goyuRpCwHpunatpifBGK12TtI2kw8Ed3Adu', '顕如', '本願寺', 2);
insert into app_user(user_name, password, first_name, last_name, role_id) values('yoshitoki-houjou', '$2a$08$PtMBmcbmd/L6xkNgu.goyuRpCwHpunatpifBGK12TtI2kw8Ed3Adu', '義時', '北条', 2);

-- 学年クラス
insert into grade_class(grade, class_name, year) values(1, 'A', 2022);
insert into grade_class(grade, class_name, year) values(1, 'B', 2022);
insert into grade_class(grade, class_name, year) values(1, 'C', 2022);
insert into grade_class(grade, class_name, year) values(1, 'D', 2022);
insert into grade_class(grade, class_name, year) values(1, 'E', 2022);
insert into grade_class(grade, class_name, year) values(2, 'A', 2022);
insert into grade_class(grade, class_name, year) values(2, 'B', 2022);
insert into grade_class(grade, class_name, year) values(2, 'C', 2022);
insert into grade_class(grade, class_name, year) values(2, 'D', 2022);
insert into grade_class(grade, class_name, year) values(2, 'E', 2022);
insert into grade_class(grade, class_name, year) values(3, 'A', 2022);
insert into grade_class(grade, class_name, year) values(3, 'B', 2022);
insert into grade_class(grade, class_name, year) values(3, 'C', 2022);
insert into grade_class(grade, class_name, year) values(3, 'D', 2022);
insert into grade_class(grade, class_name, year) values(3, 'E', 2022);

-- insert into user_grade_class(user_id, grade_id) values(1, 1);
-- insert into user_grade_class(user_id, grade_id) values(2, 2);
-- insert into user_grade_class(user_id, grade_id) values(3, 3);

-- 教員クラスの中間テーブル
insert into user_grade_class(user_id, grade_id) values(1, 1);
insert into user_grade_class(user_id, grade_id) values(2, 2);
insert into user_grade_class(user_id, grade_id) values(3, 3);
insert into user_grade_class(user_id, grade_id) values(4, 4);
insert into user_grade_class(user_id, grade_id) values(5, 5);
insert into user_grade_class(user_id, grade_id) values(6, 6);
insert into user_grade_class(user_id, grade_id) values(7, 7);
insert into user_grade_class(user_id, grade_id) values(8, 8);
insert into user_grade_class(user_id, grade_id) values(9, 9);
insert into user_grade_class(user_id, grade_id) values(10, 10);
insert into user_grade_class(user_id, grade_id) values(11, 11);
insert into user_grade_class(user_id, grade_id) values(12, 12);
insert into user_grade_class(user_id, grade_id) values(13, 13);
insert into user_grade_class(user_id, grade_id) values(14, 14);
insert into user_grade_class(user_id, grade_id) values(15, 15);
