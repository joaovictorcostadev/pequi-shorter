CREATE TABLE IF NOT EXISTS url_accesses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    url_id BIGINT NOT NULL,
    ip VARCHAR(45) NOT NULL,
    country VARCHAR(100),
    state VARCHAR(100),
    city VARCHAR(100),
    device_type VARCHAR(50),
    operating_system VARCHAR(50),
    browser VARCHAR(50),
    user_agent TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ,

    CONSTRAINT fk_url_accesses_user
    FOREIGN KEY (user_id)
    REFERENCES "users" (id)
    ON DELETE SET NULL,

    CONSTRAINT fk_url_accesses_url
    FOREIGN KEY (url_id)
    REFERENCES urls (id)
    ON DELETE CASCADE
    );
