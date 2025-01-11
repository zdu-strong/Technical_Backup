-- liquibase formatted sql

-- changeset John:1736582156549-1
CREATE INDEX IDX8840dv7xlk21fn2oxkysj6me3 ON nonce_entity(create_date, id);

-- changeset John:1736582156549-2
CREATE INDEX IDX9w6ej7fc4cg929habd6nnuxjg ON storage_space_entity(create_date, id);

-- changeset John:1736582156549-3
CREATE INDEX IDXtaxm65p0istgkmuysjcy4arkq ON organize_entity(create_date, id);

