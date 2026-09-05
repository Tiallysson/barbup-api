CREATE TABLE password_reset_code (
    id uuid NOT NULL,
    active boolean NOT NULL DEFAULT true,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    created_by varchar(255),
    updated_by varchar(255),
    user_id uuid NOT NULL,
    code_hash varchar(255) NOT NULL,
    expires_at timestamp NOT NULL,
    attempts integer NOT NULL DEFAULT 0,
    consumed_at timestamp,
    CONSTRAINT pk_password_reset_code PRIMARY KEY (id),
    CONSTRAINT fk_password_reset_code_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uk_password_reset_code_user UNIQUE (user_id)
);

CREATE INDEX idx_password_reset_code_user_id ON password_reset_code (user_id);
