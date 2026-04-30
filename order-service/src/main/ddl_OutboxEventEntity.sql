CREATE TABLE outbox
(
    id            UUID NOT NULL,
    aggregatetype VARCHAR(255),
    aggregateid   UUID,
    status        SMALLINT,
    payload       JSONB,
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_outbox PRIMARY KEY (id)
);