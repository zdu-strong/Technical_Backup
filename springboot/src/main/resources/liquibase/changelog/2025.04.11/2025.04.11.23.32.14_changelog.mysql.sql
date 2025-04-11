-- liquibase formatted sql

-- changeset john:1744414372490-1
ALTER TABLE organize_entity ADD deletion_code VARCHAR(255) NOT NULL;

-- changeset john:1744414372490-2
ALTER TABLE role_entity ADD deletion_code VARCHAR(255) NOT NULL;

-- changeset john:1744414372490-3
ALTER TABLE user_email_entity ADD deletion_code VARCHAR(255) NOT NULL;

-- changeset john:1744414372490-4
ALTER TABLE token_entity ADD is_deleted BIT(1) NOT NULL;

-- changeset john:1744414372490-5
ALTER TABLE user_entity ADD is_deleted BIT(1) NOT NULL;

-- changeset john:1744414372490-6
ALTER TABLE role_entity ADD is_deleted BIT(1) NOT NULL;

-- changeset john:1744414372490-7
ALTER TABLE organize_entity ADD is_deleted BIT(1) NOT NULL;

-- changeset john:1744414372490-8
ALTER TABLE user_email_entity ADD is_deleted BIT(1) NOT NULL;

-- changeset john:1744414372490-9
ALTER TABLE organize_entity ADD CONSTRAINT UKb6qs7ivk9iu1vi60h04qgfeng UNIQUE (parent_id, name, deletion_code);

-- changeset john:1744414372490-10
ALTER TABLE user_email_entity ADD CONSTRAINT UKfgnoa0g2ehmplhq2oue2a7fl0 UNIQUE (email, deletion_code);

-- changeset john:1744414372490-11
CREATE INDEX IDX5w2of6p8sx2atafw8yqt31axr ON organize_entity(is_company, is_deleted);

-- changeset john:1744414372490-12
CREATE INDEX IDXift39bbd68wts6cwbbd38ybws ON organize_entity(parent_id, is_deleted);

-- changeset john:1744414372490-13
ALTER TABLE user_email_entity DROP KEY UKh2isntv8o39q931e23s31mbi6;

-- changeset john:1744414372490-14
ALTER TABLE organize_entity DROP KEY UKhw9iwm4bgg93qvllktse9l7x7;

-- changeset john:1744414372490-15
ALTER TABLE organize_entity DROP COLUMN deactivate_key;

-- changeset john:1744414372490-16
ALTER TABLE role_entity DROP COLUMN deactivate_key;

-- changeset john:1744414372490-17
ALTER TABLE user_email_entity DROP COLUMN deactivate_key;

-- changeset john:1744414372490-18
ALTER TABLE logger_entity DROP COLUMN is_active;

-- changeset john:1744414372490-19
ALTER TABLE organize_entity DROP COLUMN is_active;

-- changeset john:1744414372490-20
ALTER TABLE role_entity DROP COLUMN is_active;

-- changeset john:1744414372490-21
ALTER TABLE token_entity DROP COLUMN is_active;

-- changeset john:1744414372490-22
ALTER TABLE user_email_entity DROP COLUMN is_active;

-- changeset john:1744414372490-23
ALTER TABLE user_entity DROP COLUMN is_active;

-- changeset john:1744414372490-24
DROP INDEX IDXodg8kq20ypvfvf3is4473alec ON organize_entity;

-- changeset john:1744414372490-25
DROP INDEX IDXrehkt4bi3sm4vokj7voyk1i2f ON organize_entity;

