-- liquibase formatted sql

-- changeset John:1715177244509-1
ALTER TABLE long_term_task_entity MODIFY result LONGTEXT NOT NULL;

