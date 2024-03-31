-- liquibase formatted sql

-- changeset John:1711873559839-1
ALTER TABLE distributed_execution_entity ADD extra_execute_content LONGTEXT NOT NULL;

-- changeset John:1711873559839-2
ALTER TABLE distributed_execution_entity ADD has_error BIT(1) NOT NULL;

-- changeset John:1711873559839-3
ALTER TABLE distributed_execution_entity ADD is_done BIT(1) NOT NULL;

-- changeset John:1711873559839-4
ALTER TABLE distributed_execution_entity ADD is_last_of_extra_execute_content BIT(1) NOT NULL;

-- changeset John:1711873559839-5
ALTER TABLE distributed_execution_entity ADD pagination LONGTEXT NOT NULL;

-- changeset John:1711873559839-6
ALTER TABLE distributed_execution_entity ADD total_page BIGINT NOT NULL;

-- changeset John:1711873559839-7
ALTER TABLE distributed_execution_entity ADD total_record BIGINT NOT NULL;

-- changeset John:1711873559839-8
ALTER TABLE distributed_execution_entity ADD unique_code_of_extra_execute_content VARCHAR(255) NOT NULL;

-- changeset John:1711873559839-9
ALTER TABLE distributed_execution_entity ADD CONSTRAINT UKq7j5ona88grpqooi970x40ehc UNIQUE (version, page_size, page_num, unique_code_of_extra_execute_content);

-- changeset John:1711873559839-10
ALTER TABLE distributed_execution_entity DROP KEY UK61qqq6cisvchp1mxmk0x63796;

