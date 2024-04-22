-- liquibase formatted sql

-- changeset John:1713785803062-1
CREATE TABLE user_message_relevance_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, is_deleted BIT(1) NOT NULL, update_date datetime(6) NOT NULL, user_id VARCHAR(255) NOT NULL, user_message_id VARCHAR(255) NOT NULL, CONSTRAINT PK_USER_MESSAGE_RELEVANCE_ENTITY PRIMARY KEY (id));

-- changeset John:1713785803062-2
ALTER TABLE user_message_relevance_entity ADD CONSTRAINT UKftojb6p6e7hpos4s78t37scwu UNIQUE (user_message_id, user_id);

-- changeset John:1713785803062-3
CREATE INDEX FKh6rsmdli06112quwefflxqtix ON user_message_relevance_entity(user_id);

-- changeset John:1713785803062-4
ALTER TABLE user_message_relevance_entity ADD CONSTRAINT FKh6rsmdli06112quwefflxqtix FOREIGN KEY (user_id) REFERENCES user_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1713785803062-5
ALTER TABLE user_message_relevance_entity ADD CONSTRAINT FKifnebykp5yxf1mk2mb0qnm0t4 FOREIGN KEY (user_message_id) REFERENCES user_message_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

