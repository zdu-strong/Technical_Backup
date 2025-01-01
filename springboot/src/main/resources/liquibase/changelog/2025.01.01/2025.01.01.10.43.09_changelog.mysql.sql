-- liquibase formatted sql

-- changeset John:1735728208363-14
ALTER TABLE user_role_relation_entity DROP FOREIGN KEY FK6aaypqmne5yv29be69crdaiu5;

-- changeset John:1735728208363-15
ALTER TABLE user_role_entity DROP FOREIGN KEY FKm2s710nqv05lpe7gynhmeldq3;

-- changeset John:1735728208363-16
ALTER TABLE system_role_relation_entity DROP FOREIGN KEY FKpw9rsmw819ptdj5v8c6b32w51;

-- changeset John:1735728208363-17
ALTER TABLE system_role_relation_entity DROP FOREIGN KEY FKt8w58hfw3wcq1k7ju07bgjvpo;

-- changeset John:1735728208363-1
CREATE TABLE permission_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, name VARCHAR(255) NOT NULL, update_date datetime(6) NOT NULL, CONSTRAINT PK_PERMISSION_ENTITY PRIMARY KEY (id), UNIQUE (name));

-- changeset John:1735728208363-2
CREATE TABLE role_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, deactive_key VARCHAR(255) NOT NULL, is_active BIT(1) NOT NULL, name VARCHAR(255) NOT NULL, update_date datetime(6) NOT NULL, organize_id VARCHAR(255) NULL, CONSTRAINT PK_ROLE_ENTITY PRIMARY KEY (id));

-- changeset John:1735728208363-3
CREATE TABLE role_permission_relation_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, update_date datetime(6) NOT NULL, permission_id VARCHAR(255) NOT NULL, role_id VARCHAR(255) NOT NULL, CONSTRAINT PK_ROLE_PERMISSION_RELATION_ENTITY PRIMARY KEY (id));

-- changeset John:1735728208363-4
ALTER TABLE user_role_relation_entity ADD role_id VARCHAR(255) NOT NULL;

-- changeset John:1735728208363-5
ALTER TABLE role_permission_relation_entity ADD CONSTRAINT UKcdovbasr36jrwjhj6tycl4v6a UNIQUE (role_id, permission_id);

-- changeset John:1735728208363-6
ALTER TABLE user_role_relation_entity ADD CONSTRAINT UKeh5k564341594geifetwt2hlr UNIQUE (user_id, role_id, organize_id);

-- changeset John:1735728208363-7
ALTER TABLE role_entity ADD CONSTRAINT UKmkg4n4ypdo09623ms0r03xyxn UNIQUE (organize_id, name, deactive_key);

-- changeset John:1735728208363-8
CREATE INDEX FK2o4r4d77pv4yuwn9uhfg05usa ON role_permission_relation_entity(permission_id);

-- changeset John:1735728208363-9
CREATE INDEX FK9m7vhteoenein0bj7qm0lf0qo ON user_role_relation_entity(role_id);

-- changeset John:1735728208363-10
ALTER TABLE role_permission_relation_entity ADD CONSTRAINT FK2o4r4d77pv4yuwn9uhfg05usa FOREIGN KEY (permission_id) REFERENCES permission_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1735728208363-11
ALTER TABLE user_role_relation_entity ADD CONSTRAINT FK9m7vhteoenein0bj7qm0lf0qo FOREIGN KEY (role_id) REFERENCES role_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1735728208363-12
ALTER TABLE role_permission_relation_entity ADD CONSTRAINT FKfkobmpfsitu0k0r20u6w34kej FOREIGN KEY (role_id) REFERENCES role_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1735728208363-13
ALTER TABLE role_entity ADD CONSTRAINT FKq16ryut3ufqib3ihyhwp7kl91 FOREIGN KEY (organize_id) REFERENCES organize_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1735728208363-18
ALTER TABLE user_role_relation_entity DROP KEY UK55dwlfodg78c4lkx5xi8e4xnn;

-- changeset John:1735728208363-19
ALTER TABLE system_role_entity DROP KEY UKe764t0bg9il2pop1w3i9aq1ec;

-- changeset John:1735728208363-20
ALTER TABLE system_role_relation_entity DROP KEY UKi3d85u6lbhlldo8nrvlk73bxv;

-- changeset John:1735728208363-21
ALTER TABLE user_role_entity DROP KEY UKqu535rlp12as9g96xdfmfc1yq;

-- changeset John:1735728208363-22
DROP TABLE system_role_entity;

-- changeset John:1735728208363-23
DROP TABLE system_role_relation_entity;

-- changeset John:1735728208363-24
DROP TABLE user_role_entity;

-- changeset John:1735728208363-25
ALTER TABLE user_role_relation_entity DROP COLUMN user_role_id;

