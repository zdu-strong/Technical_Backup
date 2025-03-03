-- liquibase formatted sql

-- changeset john:1741006103986-1
ALTER TABLE distributed_execution_main_entity ADD status VARCHAR(255) NOT NULL;

-- changeset john:1741006103986-2
CREATE INDEX IDXbbivfqxmshx9i0wf9cceai4uq ON distributed_execution_main_entity(execution_type, status);

-- changeset john:1741006103986-3
ALTER TABLE distributed_execution_main_entity DROP COLUMN has_error;

-- changeset john:1741006103986-4
ALTER TABLE distributed_execution_main_entity DROP COLUMN is_cancel;

-- changeset john:1741006103986-5
ALTER TABLE distributed_execution_main_entity DROP COLUMN is_done;

-- changeset john:1741006103986-6
DROP INDEX IDX58tey18ww9yake925dq5pd69u ON distributed_execution_main_entity;

