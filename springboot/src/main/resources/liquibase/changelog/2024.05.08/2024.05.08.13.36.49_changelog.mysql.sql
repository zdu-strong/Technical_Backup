-- liquibase formatted sql

-- changeset John:1715175423564-1
ALTER TABLE user_message_entity MODIFY file_name LONGTEXT NOT NULL;

-- changeset John:1715175423564-2
ALTER TABLE user_message_entity MODIFY folder_name VARCHAR(255) NOT NULL;

-- changeset John:1715175423564-3
ALTER TABLE user_message_entity MODIFY folder_size BIGINT NOT NULL;

