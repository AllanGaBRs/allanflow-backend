CREATE TABLE tb_documents (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content JSONB,
    type VARCHAR(255) NOT NULL,
    workspace_id UUID NOT NULL,
    board_id UUID NOT NULL,
    parent_id UUID,

    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),

    CONSTRAINT ck_document_type
        CHECK (type IN ('FILE', 'FOLDER')),

    CONSTRAINT fk_document_workspace
        FOREIGN KEY (workspace_id) REFERENCES tb_workspace (id),

    CONSTRAINT fk_document_board
        FOREIGN KEY (board_id) REFERENCES tb_board (id),

    CONSTRAINT fk_document_parent
        FOREIGN KEY (parent_id) REFERENCES tb_documents (id)
);

CREATE INDEX idx_document_workspace
    ON tb_documents (workspace_id);

CREATE INDEX idx_document_board
    ON tb_documents (board_id);

CREATE INDEX idx_document_parent
    ON tb_documents (parent_id);