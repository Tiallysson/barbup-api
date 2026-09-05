CREATE TABLE password_reset_tokens (
    id uuid NOT NULL,
    active boolean NOT NULL DEFAULT true,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    created_by varchar(255),
    updated_by varchar(255),
    user_id uuid NOT NULL,
    token_hash varchar(64) NOT NULL,
    expires_at timestamp NOT NULL,
    used_at timestamp,
    CONSTRAINT pk_password_reset_tokens PRIMARY KEY (id),
    CONSTRAINT fk_password_reset_tokens_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uk_password_reset_tokens_hash UNIQUE (token_hash)
);

CREATE INDEX idx_prt_hash ON password_reset_tokens (token_hash);
CREATE INDEX idx_password_reset_tokens_user_id ON password_reset_tokens (user_id);
