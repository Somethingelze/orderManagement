CREATE TABLE public.products (
                                 id UUID PRIMARY KEY,
                                 name VARCHAR(255) NOT NULL,
                                 price DECIMAL(10, 2),
                                 sale DECIMAL(10, 2),
                                 quantity INTEGER
);

INSERT INTO products (id, name, price,  sale, quantity)
VALUES
    ('f47ac10b-58cc-4372-a567-0e02b2c3d479', 'Table',100.00,15.00, 1000),
    ('89b83b9c-7d9a-4c12-9c44-3252570b6d4c','Chair',50.00,5.00, 1000),
    ('c82b79e7-e431-4e78-8380-36e659c0715e','Pencil',1.00,0.20, 10000);
