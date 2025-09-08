CREATE TABLE IF NOT EXISTS card (
    id SERIAL PRIMARY KEY,
    card_number VARCHAR(255) UNIQUE NOT NULL,
    owner VARCHAR(255) NOT NULL,
    expiry_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    balance DOUBLE PRECISION NOT NULL,
    user_id INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);