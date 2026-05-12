CREATE TABLE reserved_item
(
    id         UUID NOT NULL,
    order_id   UUID,
    product_id UUID NOT NULL,
    quantity   BIGINT,
    CONSTRAINT pk_reserveditem PRIMARY KEY (id)
);

ALTER TABLE reserved_item
    ADD CONSTRAINT FK_RESERVEDITEM_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);