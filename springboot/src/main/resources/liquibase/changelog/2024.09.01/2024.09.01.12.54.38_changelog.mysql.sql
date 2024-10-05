-- liquibase formatted sql

-- changeset John:1725195293694-1
ALTER TABLE long_term_task_entity ADD unique_key_json_string VARCHAR(255) NOT NULL;

-- changeset John:1725195293694-2
ALTER TABLE long_term_task_entity ADD CONSTRAINT UKtd49loj9mv6wfpmmadvh80qyy UNIQUE (unique_key_json_string);

-- changeset John:1725195293694-3
ALTER TABLE long_term_task_entity DROP KEY UK3emy1dbdrrvo6uy44jvpr0b2y;

-- changeset John:1725195293694-4
ALTER TABLE long_term_task_entity DROP COLUMN unique_key;

