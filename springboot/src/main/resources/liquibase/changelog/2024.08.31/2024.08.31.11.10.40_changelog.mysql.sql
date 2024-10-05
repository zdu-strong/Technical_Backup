-- liquibase formatted sql

-- changeset John:1725102654549-3
ALTER TABLE organize_move_top_entity DROP FOREIGN KEY FKbeaf63wml34x4fpcdftpi3efr;

-- changeset John:1725102654549-1
ALTER TABLE long_term_task_entity ADD unique_key VARCHAR(255) NOT NULL;

-- changeset John:1725102654549-2
ALTER TABLE long_term_task_entity ADD CONSTRAINT UK3emy1dbdrrvo6uy44jvpr0b2y UNIQUE (unique_key);

-- changeset John:1725102654549-4
ALTER TABLE organize_move_top_entity DROP KEY organize_id;

-- changeset John:1725102654549-5
DROP TABLE organize_move_top_entity;

