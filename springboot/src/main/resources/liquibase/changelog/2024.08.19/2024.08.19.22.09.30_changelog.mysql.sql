-- liquibase formatted sql

-- changeset John:1724105384292-1
ALTER TABLE user_role_relation_entity ADD organize_id VARCHAR(255) NULL;

-- changeset John:1724105384292-2
ALTER TABLE user_role_relation_entity ADD CONSTRAINT UKhi6i8uipe9l7xwpne4o47hf78 UNIQUE (user_id, system_role_id, organize_id);

-- changeset John:1724105384292-3
CREATE INDEX FKkxrfaoowboxrmfxbn9wwsjwrw ON user_role_relation_entity(organize_id);

-- changeset John:1724105384292-4
ALTER TABLE user_role_relation_entity ADD CONSTRAINT FKkxrfaoowboxrmfxbn9wwsjwrw FOREIGN KEY (organize_id) REFERENCES organize_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1724105384292-5
ALTER TABLE user_role_relation_entity DROP KEY UK2r7kn4ecomsubdkw64arb4myu;

