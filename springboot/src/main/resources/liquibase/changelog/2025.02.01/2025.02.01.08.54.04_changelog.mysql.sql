-- liquibase formatted sql

-- changeset John:1738400069805-1
ALTER TABLE organize_entity ADD is_company BIT(1) NOT NULL;

-- changeset John:1738400069805-2
CREATE INDEX IDXrehkt4bi3sm4vokj7voyk1i2f ON organize_entity(is_company, is_active);

