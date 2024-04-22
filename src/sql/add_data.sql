insert into User(username, password, role, grade)
values ('admin', 'admin', 'ADMIN', 'EXPERT');

insert into Problem(title, description, input_format, output_format, difficulty, time_limit, memory_limit, author_id)
values ('a + b 问题', '把两个数相加', '两个整数 $a$, $b$，以空格隔开',
        '一个整数 $n = a + b$', 'EASY', 100, 256, 1);

insert into IOPair(problem_id, input, output, score, type)
values (1, '1 2', '3', 100, 'SAMPLE');

insert into IOPair(problem_id, input, output, score, type)
values (1, '3 4', '7', 100, 'SAMPLE');