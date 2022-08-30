CREATE TABLE return_orders (
    id INTEGER NOT NULL AUTO_INCREMENT,
    order_id VARCHAR(128) NOT NULL,
    email_address VARCHAR(128) NOT NULL,
    status VARCHAR(128) NOT NULL,
    refund_amount DECIMAL NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE return_items (
    id INTEGER NOT NULL AUTO_INCREMENT,
    order_id VARCHAR(128) NOT NULL,
    sku VARCHAR(128) NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL NOT NULL,
    item_name VARCHAR(128) NOT NULL,
    status VARCHAR(128) NOT NULL,
    PRIMARY KEY (id)
);