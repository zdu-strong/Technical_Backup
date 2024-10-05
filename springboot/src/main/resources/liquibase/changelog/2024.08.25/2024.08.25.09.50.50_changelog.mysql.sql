-- liquibase formatted sql

-- changeset John:1724579466657-8
ALTER TABLE user_role_relation_entity DROP FOREIGN KEY FK2gjhgy73kqr9f9xgvvp4uesm4;

-- changeset John:1724579466657-9
ALTER TABLE user_role_relation_entity DROP FOREIGN KEY FK8p59ikpmch4vw781yhmj12nyv;

-- changeset John:1724579466657-10
ALTER TABLE user_role_relation_entity DROP FOREIGN KEY FKkxrfaoowboxrmfxbn9wwsjwrw;

-- changeset John:1724579466657-1
CREATE TABLE user_system_role_relation_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, update_date datetime(6) NOT NULL, organize_id VARCHAR(255) NULL, system_role_id VARCHAR(255) NOT NULL, user_id VARCHAR(255) NOT NULL, CONSTRAINT PK_USER_SYSTEM_ROLE_RELATION_ENTITY PRIMARY KEY (id));

-- changeset John:1724579466657-2
ALTER TABLE user_system_role_relation_entity ADD CONSTRAINT UKhd2hbkcs78v9qfk5p3qb86gcg UNIQUE (user_id, system_role_id, organize_id);

-- changeset John:1724579466657-3
CREATE INDEX FKgww7oklhbd6gtawk68vn87lry ON user_system_role_relation_entity(system_role_id);

-- changeset John:1724579466657-4
CREATE INDEX FKh7n16t8ta9mfj36ro75rgbsnu ON user_system_role_relation_entity(organize_id);

-- changeset John:1724579466657-5
ALTER TABLE user_system_role_relation_entity ADD CONSTRAINT FK19q3cy5u6p31a66ws6obrfiat FOREIGN KEY (user_id) REFERENCES user_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1724579466657-6
ALTER TABLE user_system_role_relation_entity ADD CONSTRAINT FKgww7oklhbd6gtawk68vn87lry FOREIGN KEY (system_role_id) REFERENCES system_role_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1724579466657-7
ALTER TABLE user_system_role_relation_entity ADD CONSTRAINT FKh7n16t8ta9mfj36ro75rgbsnu FOREIGN KEY (organize_id) REFERENCES organize_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1724579466657-11
ALTER TABLE user_role_relation_entity DROP KEY UKhi6i8uipe9l7xwpne4o47hf78;

-- changeset John:1724579466657-12
DROP TABLE user_role_relation_entity;

