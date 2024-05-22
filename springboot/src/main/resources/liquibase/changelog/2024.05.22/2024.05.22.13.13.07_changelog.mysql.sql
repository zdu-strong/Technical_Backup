-- liquibase formatted sql

-- changeset John:1716383601045-1
ALTER TABLE user_entity ADD is_active BIT(1) NOT NULL;

-- changeset John:1716383601045-2
ALTER TABLE user_email_entity ADD is_active BIT(1) NOT NULL;

-- changeset John:1716383601045-3
ALTER TABLE user_email_entity DROP COLUMN is_deleted;

-- changeset John:1716383601045-4
ALTER TABLE user_entity DROP COLUMN is_deleted;

