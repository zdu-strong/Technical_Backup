-- liquibase formatted sql

-- changeset John:1722081917303-1
CREATE TABLE user_role_relation_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, update_date datetime(6) NOT NULL, system_role_id VARCHAR(255) NOT NULL, user_id VARCHAR(255) NOT NULL, CONSTRAINT PK_USER_ROLE_RELATION_ENTITY PRIMARY KEY (id));

-- changeset John:1722081917303-2
CREATE INDEX FK2gjhgy73kqr9f9xgvvp4uesm4 ON user_role_relation_entity(user_id);

-- changeset John:1722081917303-3
CREATE INDEX FK8p59ikpmch4vw781yhmj12nyv ON user_role_relation_entity(system_role_id);

-- changeset John:1722081917303-4
CREATE INDEX FKfrbs0stjmmje4we9n1t0cf0oh ON organize_closure_entity(ancestor_id);

-- changeset John:1722081917303-5
ALTER TABLE user_role_relation_entity ADD CONSTRAINT FK2gjhgy73kqr9f9xgvvp4uesm4 FOREIGN KEY (user_id) REFERENCES user_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1722081917303-6
ALTER TABLE user_role_relation_entity ADD CONSTRAINT FK8p59ikpmch4vw781yhmj12nyv FOREIGN KEY (system_role_id) REFERENCES system_role_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1722081917303-7
ALTER TABLE organize_closure_entity DROP COLUMN is_active;

-- changeset John:1722081917303-8
DROP INDEX IDXbi079mfdyigc5sqnj20d801cr ON organize_closure_entity;

