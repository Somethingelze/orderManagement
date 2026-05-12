CREATE TABLE notification_inbox
(
    message_id   UUID NOT NULL,
    processed_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_notification_inbox PRIMARY KEY (message_id)
);