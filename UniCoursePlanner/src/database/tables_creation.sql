--- Drop tables if they exist (in the correct dependency order)
DROP TABLE IF EXISTS schedule, course, instructor, program CASCADE;

-- Programs (aka degree_programs)
CREATE TABLE program
(
    code VARCHAR(100) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    type VARCHAR(50)  NOT NULL -- Undergraduate, Graduate, Certificate, etc.
);

-- Instructors
CREATE TABLE instructor
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- Courses
CREATE TABLE course
(
    code              TEXT PRIMARY KEY, -- e.g., BUS101
    program_code      VARCHAR(500),
    title             VARCHAR(500) NOT NULL,
    description       TEXT,
    credits           NUMERIC(3, 1),
    -- Some courses have missing prerequisites, so we keep that nullable
    prerequisite_code TEXT              -- self-referencing
);

-- Schedule
CREATE TABLE schedule
(
    id          SERIAL PRIMARY KEY,
    course_code VARCHAR(200),
    course_name VARCHAR(100),
    section     VARCHAR(100),
    session     VARCHAR(100),
    credits     NUMERIC(3, 1),
    campus      VARCHAR(100),
    instructor  VARCHAR(100),
    times       TEXT,        -- e.g., "MON 5:30pm-7:20pm, WED 6:30pm-7:20pm"
    location    VARCHAR(200),
    semester    VARCHAR(200) -- e.g., 202324/fall
);