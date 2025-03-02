-- liquibase formatted sql

-- changeset john:1740913309036-1
ALTER TABLE distributed_execution_detail_entity ADD CONSTRAINT UK15v7504fn2h7g3y2hba1e460w UNIQUE (distributed_execution_main_id, partition_num, page_num);

-- changeset john:1740913309036-2
CREATE INDEX IDX27pogkpy6r3tne9alm4pw5e8p ON distributed_execution_main_entity(execution_type, create_date, id);

-- changeset john:1740913309036-3
CREATE INDEX IDX58tey18ww9yake925dq5pd69u ON distributed_execution_main_entity(execution_type, is_done);

-- changeset john:1740913309036-4
CREATE INDEX IDXaba5xj8scn2308k0nahk2nbp8 ON distributed_execution_main_entity(create_date, id);

-- changeset john:1740913309036-5
CREATE INDEX IDXaeb9vxnx6c013dbq42lthb647 ON distributed_execution_detail_entity(distributed_execution_main_id, has_error);

-- changeset john:1740913309036-6
ALTER TABLE distributed_execution_detail_entity DROP COLUMN is_done;

