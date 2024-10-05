-- liquibase formatted sql

-- changeset John:1725703176480-1
ALTER TABLE user_entity DROP COLUMN private_key_ofrsa;

-- changeset John:1725703176480-2
ALTER TABLE user_entity DROP COLUMN public_key_ofrsa;

-- changeset John:1725703176480-3
ALTER TABLE friendship_entity DROP COLUMN secret_key_ofaes;

