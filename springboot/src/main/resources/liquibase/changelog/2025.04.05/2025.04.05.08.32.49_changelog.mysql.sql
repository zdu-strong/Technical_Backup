-- liquibase formatted sql

-- changeset john:1743842006373-1
ALTER TABLE organize_entity ADD deactivate_key VARCHAR(255) NOT NULL;

-- changeset john:1743842006373-2
ALTER TABLE role_entity ADD deactivate_key VARCHAR(255) NOT NULL;

-- changeset john:1743842006373-3
ALTER TABLE user_email_entity ADD deactivate_key VARCHAR(255) NOT NULL;

-- changeset john:1743842006373-4
ALTER TABLE user_email_entity ADD CONSTRAINT UKh2isntv8o39q931e23s31mbi6 UNIQUE (email, deactivate_key);

-- changeset john:1743842006373-5
ALTER TABLE organize_entity ADD CONSTRAINT UKhw9iwm4bgg93qvllktse9l7x7 UNIQUE (parent_id, name, deactivate_key);

-- changeset john:1743842006373-6
ALTER TABLE organize_entity DROP KEY UK1mhgsj2mhg2lhsa61a07lvfj0;

-- changeset john:1743842006373-7
ALTER TABLE user_email_entity DROP KEY UKkc2p7w4m6bob6x3162493k2il;

-- changeset john:1743842006373-8
ALTER TABLE organize_entity DROP COLUMN deactive_key;

-- changeset john:1743842006373-9
ALTER TABLE role_entity DROP COLUMN deactive_key;

-- changeset john:1743842006373-10
ALTER TABLE user_email_entity DROP COLUMN deactive_key;

