-- liquibase formatted sql

-- changeset john:1740906540598-1
ALTER TABLE distributed_execution_main_entity ADD total_page BIGINT NOT NULL;

-- changeset john:1740906540598-2
ALTER TABLE distributed_execution_main_entity DROP COLUMN total_record;

