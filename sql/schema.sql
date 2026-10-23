-- MySQL 8 建表脚本（studysys 与 user-service 共用 studysys 库）
-- 不使用外键，关联关系由应用层保证；关联列单独建索引
USE studysys;

DROP TABLE IF EXISTS t_user;
DROP TABLE IF EXISTS t_grade;
DROP TABLE IF EXISTS t_enrollment;
DROP TABLE IF EXISTS t_course_schedule;
DROP TABLE IF EXISTS t_course;
DROP TABLE IF EXISTS t_student;
DROP TABLE IF EXISTS t_teacher;
DROP TABLE IF EXISTS t_department;

CREATE TABLE t_department (
    department_id   INT          PRIMARY KEY,
    department_name VARCHAR(50)  NOT NULL
);

CREATE TABLE t_teacher (
    teacher_id    INT          PRIMARY KEY,
    teacher_name  VARCHAR(50)  NOT NULL,
    department_id INT,
    INDEX idx_teacher_dept (department_id)
);

CREATE TABLE t_student (
    student_id    INT          PRIMARY KEY,
    student_name  VARCHAR(50)  NOT NULL,
    department_id INT,
    INDEX idx_student_dept (department_id)
);

CREATE TABLE t_course (
    course_id   INT           PRIMARY KEY,
    course_name VARCHAR(100)  NOT NULL,
    teacher_id  INT,
    INDEX idx_course_teacher (teacher_id)
);

CREATE TABLE t_course_schedule (
    schedule_id    INT      AUTO_INCREMENT PRIMARY KEY,
    course_id      INT      NOT NULL,
    teacher_id     INT      NOT NULL,
    day_of_week    TINYINT  NOT NULL,
    start_period   TINYINT  NOT NULL,
    end_period     TINYINT  NOT NULL,
    capacity       INT      NOT NULL,
    enrolled_count INT      DEFAULT 0,
    INDEX idx_schedule_course (course_id),
    INDEX idx_schedule_teacher (teacher_id)
);

CREATE TABLE t_enrollment (
    enrollment_id INT          AUTO_INCREMENT PRIMARY KEY,
    schedule_id   INT          NOT NULL,
    student_id    INT          NOT NULL,
    enroll_time   DATETIME     NOT NULL,
    status        VARCHAR(10)  NOT NULL,
    CONSTRAINT uk_enroll_student_schedule UNIQUE (student_id, schedule_id),
    INDEX idx_enroll_schedule (schedule_id)
);

CREATE TABLE t_grade (
    grade_id    INT  AUTO_INCREMENT PRIMARY KEY,
    student_id  INT  NOT NULL,
    schedule_id INT  NOT NULL,
    grade       INT,
    CONSTRAINT uk_grade_student_schedule UNIQUE (student_id, schedule_id),
    INDEX idx_grade_schedule (schedule_id)
);

CREATE TABLE t_user (
    user_id      INT           AUTO_INCREMENT PRIMARY KEY,
    username     VARCHAR(50)   NOT NULL,
    password     VARCHAR(100)  NOT NULL,
    role         VARCHAR(20)   NOT NULL,
    reference_id INT,
    CONSTRAINT uk_user_username UNIQUE (username),
    CONSTRAINT uk_user_role_ref UNIQUE (role, reference_id)
);
