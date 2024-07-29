-- liquibase formatted sql

-- changeset John:1722264830525-6
ALTER TABLE organize_closure_entity DROP FOREIGN KEY FKfrbs0stjmmje4we9n1t0cf0oh;

-- changeset John:1722264830525-7
ALTER TABLE organize_closure_entity DROP FOREIGN KEY FKrq3vl8q12mdppvdre3bcv6fce;

-- changeset John:1722264830525-1
CREATE TABLE organize_relation_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, update_date datetime(6) NOT NULL, ancestor_id VARCHAR(255) NOT NULL, descendant_id VARCHAR(255) NOT NULL, CONSTRAINT PK_ORGANIZE_RELATION_ENTITY PRIMARY KEY (id));

-- changeset John:1722264830525-2
CREATE INDEX FK1ns4j4ixqmi5gbvjslbpeprrs ON organize_relation_entity(descendant_id);

-- changeset John:1722264830525-3
CREATE INDEX FKgq2kbl4rrp402j4006bgg0e55 ON organize_relation_entity(ancestor_id);

-- changeset John:1722264830525-4
ALTER TABLE organize_relation_entity ADD CONSTRAINT FK1ns4j4ixqmi5gbvjslbpeprrs FOREIGN KEY (descendant_id) REFERENCES organize_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1722264830525-5
ALTER TABLE organize_relation_entity ADD CONSTRAINT FKgq2kbl4rrp402j4006bgg0e55 FOREIGN KEY (ancestor_id) REFERENCES organize_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1722264830525-8
DROP TABLE organize_closure_entity;

