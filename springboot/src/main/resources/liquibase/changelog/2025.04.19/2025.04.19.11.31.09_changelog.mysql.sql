-- liquibase formatted sql

-- changeset john:1745062307199-3
ALTER TABLE storage_space_entity ADD CONSTRAINT UK9w6ej7fc4cg929habd6nnuxjg UNIQUE (create_date, id);

-- changeset john:1745062307199-4
ALTER TABLE storage_space_entity DROP COLUMN is_deleted;

-- changeset john:1745062307199-1
ALTER TABLE storage_space_entity DROP KEY UK9w6ej7fc4cg929habd6nnuxjg;

-- changeset john:1745062307199-2
ALTER TABLE storage_space_entity ADD CONSTRAINT UK9w6ej7fc4cg929habd6nnuxjg UNIQUE (create_date, id);

