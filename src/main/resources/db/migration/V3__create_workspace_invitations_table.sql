CREATE TABLE tb_workspace_invitations (
    id UUID PRIMARY KEY,
    workspace_id UUID NOT NULL,
    invited_by_user_id UUID NOT NULL,
    email VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    code_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP(6) NOT NULL,
    accepted_at TIMESTAMP(6),
    attempts INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),

    CONSTRAINT uk_workspace_invitation_workspace_email
        UNIQUE (workspace_id, email),

    CONSTRAINT ck_workspace_invitation_role
        CHECK (role IN ('ADMIN', 'MEMBER')),

    CONSTRAINT ck_workspace_invitation_attempts
        CHECK (attempts >= 0),

    CONSTRAINT fk_workspace_invitation_workspace
        FOREIGN KEY (workspace_id) REFERENCES tb_workspace (id),

    CONSTRAINT fk_workspace_invitation_invited_by
        FOREIGN KEY (invited_by_user_id) REFERENCES tb_user (id)
);

CREATE INDEX idx_workspace_invitation_invited_by
    ON tb_workspace_invitations (invited_by_user_id);
