-- liquibase formatted sql

-- changeset John:1722072522894-1
CREATE TABLE system_default_role_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, name VARCHAR(255) NOT NULL, update_date datetime(6) NOT NULL, CONSTRAINT PK_SYSTEM_DEFAULT_ROLE_ENTITY PRIMARY KEY (id), UNIQUE (name));

-- changeset John:1722072522894-2
CREATE TABLE system_role_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, deactive_key VARCHAR(255) NOT NULL, is_active BIT(1) NOT NULL, name VARCHAR(255) NOT NULL, update_date datetime(6) NOT NULL, organize_id VARCHAR(255) NULL, CONSTRAINT PK_SYSTEM_ROLE_ENTITY PRIMARY KEY (id));

-- changeset John:1722072522894-3
CREATE TABLE system_role_relation_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, update_date datetime(6) NOT NULL, system_default_role_id VARCHAR(255) NOT NULL, system_role_id VARCHAR(255) NOT NULL, CONSTRAINT PK_SYSTEM_ROLE_RELATION_ENTITY PRIMARY KEY (id));

-- changeset John:1722072522894-4
ALTER TABLE system_role_relation_entity ADD CONSTRAINT UKml3rfsnvl4rmqj4uspx7rl0uu UNIQUE (system_default_role_id, system_role_id);

-- changeset John:1722072522894-5
CREATE INDEX FKmiiv7gynd0e029d9ceriilfwy ON system_role_entity(organize_id);

-- changeset John:1722072522894-6
CREATE INDEX FKt8w58hfw3wcq1k7ju07bgjvpo ON system_role_relation_entity(system_role_id);

-- changeset John:1722072522894-7
ALTER TABLE system_role_relation_entity ADD CONSTRAINT FK1grbbc8n57ni2yg31n9rqvg3 FOREIGN KEY (system_default_role_id) REFERENCES system_default_role_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1722072522894-8
ALTER TABLE system_role_entity ADD CONSTRAINT FKmiiv7gynd0e029d9ceriilfwy FOREIGN KEY (organize_id) REFERENCES organize_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1722072522894-9
ALTER TABLE system_role_relation_entity ADD CONSTRAINT FKt8w58hfw3wcq1k7ju07bgjvpo FOREIGN KEY (system_role_id) REFERENCES system_role_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

