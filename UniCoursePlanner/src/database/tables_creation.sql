-- Drop if exists
DROP TABLE IF EXISTS enrollment, prerequisite, schedule, course, instructor, student, degree_program CASCADE;

-- Degree programs (e.g., CS, Biology, etc.)
CREATE TABLE degree_program (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- Students
CREATE TABLE student (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    program_id INTEGER REFERENCES degree_program(id)
);

-- Instructors
CREATE TABLE instructor (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- Courses
CREATE TABLE course (
    id SERIAL PRIMARY KEY,
    code VARCHAR(10) UNIQUE NOT NULL,     -- e.g., CS101
    name VARCHAR(100) NOT NULL,
    description TEXT,
    credits INTEGER NOT NULL,
    program_id INTEGER REFERENCES degree_program(id)
);

-- Prerequisites
CREATE TABLE prerequisite (
    id SERIAL PRIMARY KEY,
    course_id INTEGER REFERENCES course(id) ON DELETE CASCADE,
    prerequisite_id INTEGER REFERENCES course(id) ON DELETE CASCADE
);

-- Schedule (courses offered in a semester)
CREATE TABLE schedule (
    id SERIAL PRIMARY KEY,
    course_id INTEGER REFERENCES course(id),
    instructor_id INTEGER REFERENCES instructor(id),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    location VARCHAR(100),
    semester VARCHAR(10) NOT NULL         -- e.g., Fall2025
);

-- Enrollment
CREATE TABLE enrollment (
    id SERIAL PRIMARY KEY,
    student_id INTEGER REFERENCES student(id),
    schedule_id INTEGER REFERENCES schedule(id),
    status VARCHAR(20) DEFAULT 'enrolled' -- enrolled / waitlisted / dropped
);
