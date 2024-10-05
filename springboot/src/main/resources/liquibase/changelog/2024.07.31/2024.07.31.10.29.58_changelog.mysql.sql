-- liquibase formatted sql

-- changeset John:1722421812299-1
CREATE TABLE distributed_execution_task_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, has_error BIT(1) NOT NULL, is_done BIT(1) NOT NULL, page_num BIGINT NOT NULL, update_date datetime(6) NOT NULL, distributed_execution_id VARCHAR(255) NOT NULL, CONSTRAINT PK_DISTRIBUTED_EXECUTION_TASK_ENTITY PRIMARY KEY (id));

-- changeset John:1722421812299-2
ALTER TABLE distributed_execution_entity ADD execution_type VARCHAR(255) NOT NULL;

-- changeset John:1722421812299-3
ALTER TABLE distributed_execution_task_entity ADD CONSTRAINT UKka035jadeea1vle1ttvx4o9kb UNIQUE (distributed_execution_id, page_num);

-- changeset John:1722421812299-4
ALTER TABLE distributed_execution_task_entity ADD CONSTRAINT FKitx2qn2bvvdru5vibbfiwgqr1 FOREIGN KEY (distributed_execution_id) REFERENCES distributed_execution_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset John:1722421812299-5
ALTER TABLE distributed_execution_entity DROP KEY UKq7j5ona88grpqooi970x40ehc;

-- changeset John:1722421812299-6
ALTER TABLE distributed_execution_entity DROP COLUMN extra_execute_content;

-- changeset John:1722421812299-7
ALTER TABLE distributed_execution_entity DROP COLUMN is_last_of_extra_execute_content;

-- changeset John:1722421812299-8
ALTER TABLE distributed_execution_entity DROP COLUMN name;

-- changeset John:1722421812299-9
ALTER TABLE distributed_execution_entity DROP COLUMN page_num;

-- changeset John:1722421812299-10
ALTER TABLE distributed_execution_entity DROP COLUMN page_size;

-- changeset John:1722421812299-11
ALTER TABLE distributed_execution_entity DROP COLUMN pagination;

-- changeset John:1722421812299-12
ALTER TABLE distributed_execution_entity DROP COLUMN total_page;

-- changeset John:1722421812299-13
ALTER TABLE distributed_execution_entity DROP COLUMN unique_code_of_extra_execute_content;

-- changeset John:1722421812299-14
ALTER TABLE distributed_execution_entity DROP COLUMN version;

