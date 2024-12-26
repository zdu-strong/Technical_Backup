-- liquibase formatted sql

-- changeset John:1735216203487-5
ALTER TABLE distributed_execution_task_entity DROP FOREIGN KEY FKitx2qn2bvvdru5vibbfiwgqr1;

-- changeset John:1735216203487-1
CREATE TABLE distributed_execution_detail_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, has_error BIT(1) NOT NULL, is_done BIT(1) NOT NULL, page_num BIGINT NOT NULL, update_date datetime(6) NOT NULL, distributed_execution_main_id VARCHAR(255) NOT NULL, CONSTRAINT PK_DISTRIBUTED_EXECUTION_DETAIL_ENTITY PRIMARY KEY (id));

-- changeset John:1735216203487-2
CREATE TABLE distributed_execution_main_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, execution_type VARCHAR(255) NOT NULL, has_error BIT(1) NOT NULL, is_done BIT(1) NOT NULL, total_record BIGINT NOT NULL, update_date datetime(6) NOT NULL, CONSTRAINT PK_DISTRIBUTED_EXECUTION_MAIN_ENTITY PRIMARY KEY (id));

-- changeset John:1735216203487-3
ALTER TABLE distributed_execution_detail_entity ADD CONSTRAINT UKqf20sgk2rqf45yrwwntjbl401 UNIQUE (distributed_execution_main_id, page_num);

-- changeset John:1735216203487-4
ALTER TABLE distributed_execution_detail_entity ADD CONSTRAINT FKu3bcsmq1v20ho2nxy78a2m51 FOREIGN KEY (distributed_execution_main_id) REFERENCES distributed_execution_main_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1735216203487-6
ALTER TABLE distributed_execution_task_entity DROP KEY UKka035jadeea1vle1ttvx4o9kb;

-- changeset John:1735216203487-7
DROP TABLE distributed_execution_entity;

-- changeset John:1735216203487-8
DROP TABLE distributed_execution_task_entity;

