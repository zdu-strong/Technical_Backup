-- liquibase formatted sql

-- changeset John:1731804851493-16
ALTER TABLE user_system_role_relation_entity DROP FOREIGN KEY FK19q3cy5u6p31a66ws6obrfiat;

-- changeset John:1731804851493-17
ALTER TABLE system_role_relation_entity DROP FOREIGN KEY FK1grbbc8n57ni2yg31n9rqvg3;

-- changeset John:1731804851493-18
ALTER TABLE user_system_role_relation_entity DROP FOREIGN KEY FKgww7oklhbd6gtawk68vn87lry;

-- changeset John:1731804851493-19
ALTER TABLE user_system_role_relation_entity DROP FOREIGN KEY FKh7n16t8ta9mfj36ro75rgbsnu;

-- changeset John:1731804851493-20
ALTER TABLE system_role_entity DROP FOREIGN KEY FKmiiv7gynd0e029d9ceriilfwy;

-- changeset John:1731804851493-1
CREATE TABLE user_role_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, deactive_key VARCHAR(255) NOT NULL, is_active BIT(1) NOT NULL, name VARCHAR(255) NOT NULL, update_date datetime(6) NOT NULL, organize_id VARCHAR(255) NULL, CONSTRAINT PK_USER_ROLE_ENTITY PRIMARY KEY (id));

-- changeset John:1731804851493-2
CREATE TABLE user_role_relation_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, update_date datetime(6) NOT NULL, organize_id VARCHAR(255) NULL, user_id VARCHAR(255) NOT NULL, user_role_id VARCHAR(255) NOT NULL, CONSTRAINT PK_USER_ROLE_RELATION_ENTITY PRIMARY KEY (id));

-- changeset John:1731804851493-3
ALTER TABLE system_role_relation_entity ADD user_role_id VARCHAR(255) NOT NULL;

-- changeset John:1731804851493-4
ALTER TABLE user_role_relation_entity ADD CONSTRAINT UK55dwlfodg78c4lkx5xi8e4xnn UNIQUE (user_id, user_role_id, organize_id);

-- changeset John:1731804851493-5
ALTER TABLE system_role_entity ADD CONSTRAINT UKe764t0bg9il2pop1w3i9aq1ec UNIQUE (name);

-- changeset John:1731804851493-6
ALTER TABLE system_role_relation_entity ADD CONSTRAINT UKi3d85u6lbhlldo8nrvlk73bxv UNIQUE (user_role_id, system_role_id);

-- changeset John:1731804851493-7
ALTER TABLE user_role_entity ADD CONSTRAINT UKqu535rlp12as9g96xdfmfc1yq UNIQUE (organize_id, name, deactive_key);

-- changeset John:1731804851493-8
CREATE INDEX FK6aaypqmne5yv29be69crdaiu5 ON user_role_relation_entity(user_role_id);

-- changeset John:1731804851493-9
CREATE INDEX FKkxrfaoowboxrmfxbn9wwsjwrw ON user_role_relation_entity(organize_id);

-- changeset John:1731804851493-10
CREATE INDEX FKt8w58hfw3wcq1k7ju07bgjvpo ON system_role_relation_entity(system_role_id);

-- changeset John:1731804851493-11
ALTER TABLE user_role_relation_entity ADD CONSTRAINT FK2gjhgy73kqr9f9xgvvp4uesm4 FOREIGN KEY (user_id) REFERENCES user_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1731804851493-12
ALTER TABLE user_role_relation_entity ADD CONSTRAINT FK6aaypqmne5yv29be69crdaiu5 FOREIGN KEY (user_role_id) REFERENCES user_role_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1731804851493-13
ALTER TABLE user_role_relation_entity ADD CONSTRAINT FKkxrfaoowboxrmfxbn9wwsjwrw FOREIGN KEY (organize_id) REFERENCES organize_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1731804851493-14
ALTER TABLE user_role_entity ADD CONSTRAINT FKm2s710nqv05lpe7gynhmeldq3 FOREIGN KEY (organize_id) REFERENCES organize_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1731804851493-15
ALTER TABLE system_role_relation_entity ADD CONSTRAINT FKpw9rsmw819ptdj5v8c6b32w51 FOREIGN KEY (user_role_id) REFERENCES user_role_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1731804851493-21
ALTER TABLE system_role_entity DROP KEY UKg073i0k6v6paiqt8r64hiaht0;

-- changeset John:1731804851493-22
ALTER TABLE user_system_role_relation_entity DROP KEY UKhd2hbkcs78v9qfk5p3qb86gcg;

-- changeset John:1731804851493-23
ALTER TABLE system_role_relation_entity DROP KEY UKml3rfsnvl4rmqj4uspx7rl0uu;

-- changeset John:1731804851493-24
ALTER TABLE system_default_role_entity DROP KEY name;

-- changeset John:1731804851493-25
DROP TABLE system_default_role_entity;

-- changeset John:1731804851493-26
DROP TABLE user_system_role_relation_entity;

-- changeset John:1731804851493-27
ALTER TABLE system_role_entity DROP COLUMN deactive_key;

-- changeset John:1731804851493-28
ALTER TABLE system_role_entity DROP COLUMN is_active;

-- changeset John:1731804851493-29
ALTER TABLE system_role_entity DROP COLUMN organize_id;

-- changeset John:1731804851493-30
ALTER TABLE system_role_relation_entity DROP COLUMN system_default_role_id;

