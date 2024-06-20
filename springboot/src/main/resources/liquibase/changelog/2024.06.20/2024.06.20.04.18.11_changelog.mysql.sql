-- liquibase formatted sql

-- changeset John:1718857108544-1
ALTER TABLE logger_entity ADD is_active BIT(1) NOT NULL;

