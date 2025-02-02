-- liquibase formatted sql

-- changeset John:1738489438049-9
ALTER TABLE user_black_organize_entity DROP FOREIGN KEY FKay1r2ryhqdkvs5ll7ltr1hthu;

-- changeset John:1738489438049-10
ALTER TABLE user_black_organize_closure_entity DROP FOREIGN KEY FKgeq95vaaswvbtq6g2evd4csp0;

-- changeset John:1738489438049-11
ALTER TABLE user_black_organize_entity DROP FOREIGN KEY FKhkqth37t013xa04vx2myptqtk;

-- changeset John:1738489438049-12
ALTER TABLE user_role_relation_entity DROP FOREIGN KEY FKkxrfaoowboxrmfxbn9wwsjwrw;

-- changeset John:1738489438049-13
ALTER TABLE user_black_organize_closure_entity DROP FOREIGN KEY FKlac7iem33ph9eneiyxld7v6q8;

-- changeset John:1738489438049-14
ALTER TABLE role_entity DROP FOREIGN KEY FKq16ryut3ufqib3ihyhwp7kl91;

-- changeset John:1738489438049-1
CREATE TABLE role_organize_relation_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, update_date datetime(6) NOT NULL, organize_id VARCHAR(255) NOT NULL, role_id VARCHAR(255) NOT NULL, CONSTRAINT PK_ROLE_ORGANIZE_RELATION_ENTITY PRIMARY KEY (id));

-- changeset John:1738489438049-2
ALTER TABLE role_entity ADD is_organize_role BIT(1) NOT NULL;

-- changeset John:1738489438049-3
ALTER TABLE role_organize_relation_entity ADD CONSTRAINT UKhcg7w3c9c8u5j8kmr2q9roial UNIQUE (role_id, organize_id);

-- changeset John:1738489438049-4
ALTER TABLE user_role_relation_entity ADD CONSTRAINT UKnofobu3y0sqiglf30t1jt0d9j UNIQUE (user_id, role_id);

-- changeset John:1738489438049-5
ALTER TABLE organize_relation_entity ADD CONSTRAINT UKrmlbect4368yiy62ham79oq39 UNIQUE (ancestor_id, descendant_id);

-- changeset John:1738489438049-6
CREATE INDEX FK1jmylyd0kcod3u8jw5nmmyyn8 ON role_organize_relation_entity(organize_id);

-- changeset John:1738489438049-7
ALTER TABLE role_organize_relation_entity ADD CONSTRAINT FK1jmylyd0kcod3u8jw5nmmyyn8 FOREIGN KEY (organize_id) REFERENCES organize_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1738489438049-8
ALTER TABLE role_organize_relation_entity ADD CONSTRAINT FKn2ghw3do41s32ttv67rqq3i6o FOREIGN KEY (role_id) REFERENCES role_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1738489438049-15
ALTER TABLE user_role_relation_entity DROP KEY UKeh5k564341594geifetwt2hlr;

-- changeset John:1738489438049-16
ALTER TABLE role_entity DROP KEY UKmkg4n4ypdo09623ms0r03xyxn;

-- changeset John:1738489438049-17
DROP TABLE user_black_organize_closure_entity;

-- changeset John:1738489438049-18
DROP TABLE user_black_organize_entity;

-- changeset John:1738489438049-19
ALTER TABLE role_entity DROP COLUMN organize_id;

-- changeset John:1738489438049-20
ALTER TABLE user_role_relation_entity DROP COLUMN organize_id;

-- changeset John:1738489438049-21
DROP INDEX FKgq2kbl4rrp402j4006bgg0e55 ON organize_relation_entity;

