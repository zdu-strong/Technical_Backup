-- liquibase formatted sql

-- changeset John:1736581574539-1
CREATE TABLE nonce_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, nonce VARCHAR(255) NOT NULL, update_date datetime(6) NOT NULL, CONSTRAINT PK_NONCE_ENTITY PRIMARY KEY (id), UNIQUE (nonce));

