CREATE TABLE course (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255)  NOT NULL,
    description TEXT
);

CREATE TABLE instructor (
    id   SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL
);

CREATE TABLE schedule (
    id             SERIAL PRIMARY KEY,
    course_id      INTEGER NOT NULL
        REFERENCES course(id)
        ON DELETE CASCADE,
    instructor_id  INTEGER NOT NULL
        REFERENCES instructor(id)
        ON DELETE SET NULL,
    start_time     TIMESTAMP  NOT NULL,
    end_time       TIMESTAMP  NOT NULL,
    location       VARCHAR(100)
);