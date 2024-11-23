-- liquibase formatted sql

-- changeset John:1732325773034-1
ALTER TABLE user_email_entity MODIFY email VARCHAR(255) NOT NULL;

-- changeset John:1732325773034-2
ALTER TABLE verification_code_email_entity MODIFY email VARCHAR(255) NOT NULL;

