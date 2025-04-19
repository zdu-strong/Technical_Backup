-- liquibase formatted sql

-- changeset john:1745059836590-1
ALTER TABLE storage_space_entity ADD is_deleted BIT(1) NOT NULL;

