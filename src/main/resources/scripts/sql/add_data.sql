insert into User(username, password, role, grade)
values ('admin', 'admin', 'ADMIN', 'EXPERT'),
       ('user', '111', 'USER', 'BEGINNER');

insert into Problem(title, description, input_format, output_format, difficulty, time_limit, memory_limit, author_id)
values ('a + b 问题', '把两个数相加', '两个整数 $a$, $b$，以空格隔开',
        '一个整数 $n = a + b$', 'EASY', 100, 256, 1);

insert into IOPair(problem_id, input, output, score, type)
values (1, '1 2', '3', 100, 'SAMPLE'),
       (1, '2 3', '5', 100, 'SAMPLE'),
       (1, '5 7', '12', 100, 'TEST');

insert into submission(user_id, problem_id, code, language, result, score, ai_advice_requested)
values (1, 1, 'a, b = map(input().split())\nprint(a + b)', 'PYTHON', 'PASSED', 100, false);

insert into checkpoint(submission_id, io_pair_id, status, used_time, used_memory, info)
values (1, 1, 'P', 10, 256, 'Accepted'),
       (1, 2, 'WA', 10, 256, 'Wrong Answer'),
       (1, 3, 'TLE', 1000, 256, 'Time Limit Exceeded');