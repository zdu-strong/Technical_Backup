-- liquibase formatted sql

-- changeset John:1716382849978-6
ALTER TABLE user_message_relevance_entity DROP FOREIGN KEY FKh6rsmdli06112quwefflxqtix;

-- changeset John:1716382849978-7
ALTER TABLE user_message_relevance_entity DROP FOREIGN KEY FKifnebykp5yxf1mk2mb0qnm0t4;

-- changeset John:1716382849978-1
CREATE TABLE user_message_deactivate_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, update_date datetime(6) NOT NULL, user_id VARCHAR(255) NOT NULL, user_message_id VARCHAR(255) NOT NULL, CONSTRAINT PK_USER_MESSAGE_DEACTIVATE_ENTITY PRIMARY KEY (id));

-- changeset John:1716382849978-2
ALTER TABLE user_message_deactivate_entity ADD CONSTRAINT UKgor4r6qme9rknaoyfrqmvweh3 UNIQUE (user_message_id, user_id);

-- changeset John:1716382849978-3
CREATE INDEX FK132burgawm3e386ygcj8co5vu ON user_message_deactivate_entity(user_id);

-- changeset John:1716382849978-4
ALTER TABLE user_message_deactivate_entity ADD CONSTRAINT FK132burgawm3e386ygcj8co5vu FOREIGN KEY (user_id) REFERENCES user_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1716382849978-5
ALTER TABLE user_message_deactivate_entity ADD CONSTRAINT FKi41417apwfestnbkgv32u9amw FOREIGN KEY (user_message_id) REFERENCES user_message_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1716382849978-8
ALTER TABLE user_message_relevance_entity DROP KEY UKftojb6p6e7hpos4s78t37scwu;

-- changeset John:1716382849978-9
DROP TABLE user_message_relevance_entity;

