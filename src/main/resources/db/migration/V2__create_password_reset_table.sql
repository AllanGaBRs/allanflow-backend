CREATE TABLE tb_password_reset (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    code_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP(6) NOT NULL,
    used_at TIMESTAMP(6),
    attempts INTEGER NOT NULL,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),

    CONSTRAINT uk_password_reset_user
        UNIQUE (user_id),

    CONSTRAINT fk_password_reset_user
        FOREIGN KEY (user_id) REFERENCES tb_user (id)
);

CREATE INDEX idx_password_reset_user
    ON tb_password_reset (user_id);
