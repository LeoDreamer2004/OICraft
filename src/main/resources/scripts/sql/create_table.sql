use homework;

drop table if exists Post;
drop table if exists Comment;
drop table if exists CheckPoint;
drop table if exists Submission;
drop table if exists IOPair;
drop table if exists Problem;
drop table if exists User;

create table if not exists User
(
    id           int primary key auto_increment,
    username     varchar(255)                                            not null,
    password     varchar(255)                                            not null,
    role         enum ('ADMIN', 'USER')                                  not null,
    grade        enum ('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT') not null,
    last_checkin date default null,
    experience   int  default 0,
    email        varchar(255),
    signature    varchar(255),
    avatar       mediumblob,
    constraint unique (username)
);

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
    passed        int default 0,
    adviceai      text,
    author_id     int,
    foreign key (author_id) references User (id)
);


create table if not exists IOPair
(
    id         int primary key auto_increment,
    input      text                    not null,
    output     text                    not null,
    type       enum ('SAMPLE', 'TEST') not null,
    problem_id int                     not null,
    score      int                     not null,
    foreign key (problem_id) references Problem (id)
);



create table if not exists Submission
(
    id                  int primary key auto_increment,
    code                text                                 not null,
    language            enum ('C', 'CPP', 'JAVA', 'PYTHON')  not null,
    status              enum ('PASSED', 'FAILED', 'WAITING') not null,
    user_id             int                                  not null,
    score               int                                  not null,
    problem_id          int                                  not null,
    ai_advice_requested boolean default false,
    foreign key (user_id) references User (id),
    foreign key (problem_id) references Problem (id)
);

create table if not exists CheckPoint
(
    id            int primary key auto_increment,
    submission_id int                                              not null,
    io_pair_id    int                                              not null,
    status        enum ('P', 'AC', 'WA', 'TLE', 'MLE', 'RE', 'CE') not null,
    used_time     int                                              not null,
    used_memory   int                                              not null,
    info          text                                             not null,
    foreign key (io_pair_id) references IOPair (id),
    foreign key (submission_id) references Submission (id)
);

create table if not exists Post
(
    id          int primary key auto_increment,
    title       varchar(255) not null,
    content     text         not null,
    user_id     int,
    create_time timestamp    not null,
    problem_id  int          not null,
    foreign key (problem_id) references Problem (id),
    foreign key (user_id) references User (id)
);

create table if not exists Comment
(
    id          int primary key auto_increment,
    content     text      not null,
    user_id     int,
    post_id     int       not null,
    create_time timestamp not null,
    foreign key (user_id) references User (id),
    foreign key (post_id) references Post (id)
);
