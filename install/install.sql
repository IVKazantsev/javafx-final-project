CREATE TABLE IF NOT EXISTS todos
(
    ID           int auto_increment PRIMARY KEY,
    TITLE        varchar(512) NOT NULL,
    COMPLETED    VARCHAR(1)   NOT NULL DEFAULT 'N',
    CREATED_AT   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT   DATETIME     NULL     DEFAULT NULL,
    COMPLETED_AT DATETIME     NULL     DEFAULT NULL
    );

INSERT INTO todos (title)
VALUES ('Wake up'),
       ('Build something amazing');