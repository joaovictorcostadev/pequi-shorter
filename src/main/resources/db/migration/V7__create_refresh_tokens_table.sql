CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(512) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoke_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT uk_refresh_tokens_token UNIQUE (token),

    CONSTRAINT fk_refresh_tokens_user
    FOREIGN KEY (user_id)
    REFERENCES "users" (id)
    ON DELETE CASCADE
);