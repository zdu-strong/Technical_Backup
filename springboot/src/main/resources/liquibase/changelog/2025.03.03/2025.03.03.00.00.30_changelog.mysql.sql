-- liquibase formatted sql

-- changeset john:1740960052280-1
ALTER TABLE distributed_execution_main_entity ADD is_cancel BIT(1) NOT NULL;

