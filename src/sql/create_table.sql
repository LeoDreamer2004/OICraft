use homework;

drop table if exists User;
drop table if exists CheckPoint;
drop table if exists Submission;
drop table if exists IOPair;
drop table if exists Problem;

create table if not exists Problem
(
    id            int primary key auto_increment,
    title         varchar(255)                    not null,
    description   text                            not null,
    input_format  text                            not null,
    output_format text                            not null,
    difficulty    enum ('EASY', 'MEDIUM', 'HARD') not null,
    time_limit    int                             not null,
    memory_limit  int                             not null,
    submit        int default 0,
    passed        int default 0
);


create table if not exists IOPair
(
    id         int primary key auto_increment,
    input      text                    not null,
    output     text                    not null,
    type       enum ('SAMPLE', 'TEST') not null,
    problem_id int                     not null,
    foreign key (problem_id) references Problem (id)
);

create table if not exists Submission
(
    id         int primary key auto_increment,
    code       text                                 not null,
    language   enum ('C', 'CPP', 'JAVA', 'PYTHON')  not null,
    status     enum ('PASSED', 'FAILED', 'WAITING') not null,
    io_pair_id int                                  not null,
    score      int                                  not null,
    foreign key (io_pair_id) references IOPair (id)
);

create table if not exists CheckPoint
(
    submission_id int                                              not null,
    io_pair_id    int                                              not null,
    status        enum ('P', 'AC', 'WA', 'TLE', 'MLE', 'RE', 'CE') not null,
    used_time     int                                              not null,
    used_memory   int                                              not null,
    info          text                                             not null,
    foreign key (io_pair_id) references IOPair (id),
    foreign key (submission_id) references Submission (id)
);

create table if not exists User
(
    id       int primary key auto_increment,
    username varchar(255)                                            not null,
    password varchar(255)                                            not null,
    role     enum ('ADMIN', 'USER')                                  not null,
    grade    enum ('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPORT') not null
);

insert into Problem(title, description, input_format, output_format, difficulty, time_limit, memory_limit)
values ('a + b 问题', '把两个数相加', '两个整数 $a$, $b$，以空格隔开',
        '一个整数 $n = a + b$', 'EASY', 100, 256);