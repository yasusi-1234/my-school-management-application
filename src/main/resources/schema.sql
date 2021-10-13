drop table if exists user_test;
drop table if exists test;
drop table if exists user_grade_class;
drop table if exists grade_class;
drop table if exists role;
drop table if exists app_user;

-- ロールテーブル
create table if not exists role(
	role_id int auto_increment primary key,
	role_name varchar(20) not null
);

-- ユーザーテーブル(学生・教師)
create table if not exists app_user(
	user_id bigint auto_increment primary key,
	user_name varchar(50) not null unique,
	password varchar(255) not null,
    first_name varchar(20) not null,
    last_name varchar(20) not null,
    role_id int not null,
    CONSTRAINT fk_role_id
		FOREIGN KEY (role_id)
		REFERENCES role (role_id)
		ON DELETE RESTRICT ON UPDATE CASCADE
);

-- 学年クラステーブル
create table if not exists grade_class(
	grade_id bigint auto_increment primary key,
    grade tinyint not null,
    class_name char(1) not null,
    year int not null
);

-- 学年クラスとユーザーの中間テーブル
create table if not exists user_grade_class(
	user_grade_id bigint auto_increment primary key,
    user_id bigint,
    grade_id bigint not null,
    CONSTRAINT fk_user_id
		FOREIGN KEY (user_id)
		REFERENCES app_user (user_id)
		ON DELETE RESTRICT ON UPDATE CASCADE,
	CONSTRAINT fk_grade_id
		FOREIGN KEY (grade_id)
		REFERENCES grade_class (grade_id)
		ON DELETE RESTRICT ON UPDATE CASCADE
);

-- テストテーブル
create table if not exists test(
	test_id bigint primary key,
    season_name varchar(10) not null,
    subject_name varchar(10) not null,
    year int not null,
    grade tinyint not null
);

-- テストとユーザーの中間テーブル
create table if not exists user_test(
	user_test_id bigint primary key,
    class_name char(1) not null,
    point int not null,
    user_id bigint,
    test_id bigint not null,
    CONSTRAINT fk_user_id
		FOREIGN KEY (user_id)
		REFERENCES app_user (user_id)
		ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_test_id
		FOREIGN KEY (test_id)
		REFERENCES test (test_id)
		ON DELETE RESTRICT ON UPDATE CASCADE
);