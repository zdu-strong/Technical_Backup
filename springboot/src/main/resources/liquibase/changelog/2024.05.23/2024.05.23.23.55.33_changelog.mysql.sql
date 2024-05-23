-- liquibase formatted sql

-- changeset John:1716508554463-3
CREATE INDEX IDXbi079mfdyigc5sqnj20d801cr ON organize_closure_entity(ancestor_id, is_active);

-- changeset John:1716508554463-4
CREATE INDEX IDXodg8kq20ypvfvf3is4473alec ON organize_entity(parent_id, is_active);

-- changeset John:1716508554463-5
DROP INDEX FKfrbs0stjmmje4we9n1t0cf0oh ON organize_closure_entity;

-- changeset John:1716508554463-1
ALTER TABLE distributed_execution_entity DROP KEY UKq7j5ona88grpqooi970x40ehc;

-- changeset John:1716508554463-2
ALTER TABLE distributed_execution_entity ADD CONSTRAINT UKq7j5ona88grpqooi970x40ehc UNIQUE (version, page_num, page_size, unique_code_of_extra_execute_content);

