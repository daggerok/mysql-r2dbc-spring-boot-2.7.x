CREATE TABLE IF NOT EXISTS messages (
    address_to VARCHAR(255),
    address_from VARCHAR(255),
    body TEXT,
    sent_at DATETIME(6),
    id BIGINT AUTO_INCREMENT PRIMARY KEY
)
;
