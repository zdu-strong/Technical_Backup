-- liquibase formatted sql

-- changeset john:1740906016536-1
ALTER TABLE distributed_execution_detail_entity ADD partition_num BIGINT NOT NULL;

-- changeset john:1740906016536-2
ALTER TABLE distributed_execution_main_entity ADD total_partition BIGINT NOT NULL;

