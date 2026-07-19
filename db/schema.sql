-- Sistema de Matricula UTP
-- Ejecutar este script antes de iniciar la aplicacion

CREATE DATABASE matricula_db;
\c matricula_db;

CREATE TABLE IF NOT EXISTS enrollment_period (
    id SERIAL PRIMARY KEY,
    start_date DATE,
    end_date DATE,
    is_active BOOLEAN DEFAULT FALSE,
    current_serving_position INT DEFAULT 1
);

CREATE TABLE IF NOT EXISTS professors (
    code VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS courses (
    code VARCHAR(20) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    max_students INT NOT NULL,
    enrollment_open BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS classrooms (
    code VARCHAR(20) PRIMARY KEY,
    course_code VARCHAR(20) NOT NULL REFERENCES courses(code) ON DELETE CASCADE,
    professor_code VARCHAR(10) NOT NULL REFERENCES professors(code)
);

CREATE TABLE IF NOT EXISTS students (
    code VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    gpa DECIMAL(4,2) DEFAULT 0.00
);

CREATE TABLE IF NOT EXISTS enrollment_queue (
    student_code VARCHAR(10) PRIMARY KEY REFERENCES students(code) ON DELETE CASCADE,
    position INT NOT NULL,
    registered_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS enrollments (
    student_code VARCHAR(10) NOT NULL REFERENCES students(code),
    course_code VARCHAR(20) NOT NULL REFERENCES courses(code),
    enrolled_at TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY (student_code, course_code)
);

CREATE TABLE IF NOT EXISTS sequence_counters (
    entity VARCHAR(20) PRIMARY KEY,
    last_value BIGINT DEFAULT 0
);

INSERT INTO sequence_counters (entity, last_value) VALUES ('student', 0) ON CONFLICT DO NOTHING;
INSERT INTO sequence_counters (entity, last_value) VALUES ('professor', 0) ON CONFLICT DO NOTHING;
INSERT INTO sequence_counters (entity, last_value) VALUES ('classroom', 0) ON CONFLICT DO NOTHING;
