-- liquibase formatted sql

-- changeset John:1716386043020-1
ALTER TABLE organize_entity ADD deactive_key VARCHAR(255) NOT NULL;

-- changeset John:1716386043020-2
ALTER TABLE user_email_entity ADD deactive_key VARCHAR(255) NOT NULL;

-- changeset John:1716386043020-3
ALTER TABLE organize_closure_entity ADD is_active BIT(1) NOT NULL;

-- changeset John:1716386043020-4
ALTER TABLE token_entity ADD is_active BIT(1) NOT NULL;

-- changeset John:1716386043020-5
ALTER TABLE organize_entity ADD is_active BIT(1) NOT NULL;

-- changeset John:1716386043020-6
ALTER TABLE organize_entity ADD CONSTRAINT UK1mhgsj2mhg2lhsa61a07lvfj0 UNIQUE (parent_id, name, deactive_key);

-- changeset John:1716386043020-7
ALTER TABLE user_email_entity ADD CONSTRAINT UKkc2p7w4m6bob6x3162493k2il UNIQUE (email, deactive_key);

-- changeset John:1716386043020-8
ALTER TABLE organize_entity DROP KEY UK5sbf5vxcdf70l5qsp050ortys;

-- changeset John:1716386043020-9
ALTER TABLE user_email_entity DROP KEY UKqk35yh86v7c9xa0scp4p795be;

-- changeset John:1716386043020-10
ALTER TABLE organize_entity DROP COLUMN deleted_key;

-- changeset John:1716386043020-11
ALTER TABLE user_email_entity DROP COLUMN deleted_key;

-- changeset John:1716386043020-12
ALTER TABLE organize_closure_entity DROP COLUMN is_deleted;

-- changeset John:1716386043020-13
ALTER TABLE organize_entity DROP COLUMN is_deleted;

-- changeset John:1716386043020-14
ALTER TABLE token_entity DROP COLUMN is_deleted;

