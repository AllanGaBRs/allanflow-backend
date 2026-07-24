CREATE TABLE tb_user (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    role VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),

    CONSTRAINT ck_user_role
        CHECK (role IN ('ROLE_USER'))
);

CREATE TABLE tb_workspace (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    slug VARCHAR(120) NOT NULL UNIQUE,
    owner_id UUID NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),

    CONSTRAINT fk_workspace_owner
        FOREIGN KEY (owner_id) REFERENCES tb_user (id)
);

CREATE TABLE tb_memberships (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    role VARCHAR(255) NOT NULL,

    CONSTRAINT uk_membership_user_workspace
        UNIQUE (user_id, workspace_id),

    CONSTRAINT ck_membership_role
        CHECK (role IN ('OWNER', 'ADMIN', 'MEMBER')),

    CONSTRAINT fk_membership_user
        FOREIGN KEY (user_id) REFERENCES tb_user (id),

    CONSTRAINT fk_membership_workspace
        FOREIGN KEY (workspace_id) REFERENCES tb_workspace (id)
);

CREATE TABLE tb_board (
    id UUID PRIMARY KEY,
    workspace_id UUID NOT NULL,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),

    CONSTRAINT fk_board_workspace
        FOREIGN KEY (workspace_id) REFERENCES tb_workspace (id)
);

CREATE TABLE tb_board_members (
    board_id UUID NOT NULL,
    user_id UUID NOT NULL,

    CONSTRAINT pk_board_members
        PRIMARY KEY (board_id, user_id),

    CONSTRAINT fk_board_member_board
        FOREIGN KEY (board_id) REFERENCES tb_board (id),

    CONSTRAINT fk_board_member_user
        FOREIGN KEY (user_id) REFERENCES tb_user (id)
);

CREATE TABLE tb_column (
    id UUID PRIMARY KEY,
    workspace_id UUID NOT NULL,
    board_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    position INTEGER NOT NULL,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),

    CONSTRAINT fk_column_workspace
        FOREIGN KEY (workspace_id) REFERENCES tb_workspace (id),

    CONSTRAINT fk_column_board
        FOREIGN KEY (board_id) REFERENCES tb_board (id)
);

CREATE TABLE tb_client (
    id UUID PRIMARY KEY,
    workspace_id UUID NOT NULL,
    name VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(255),
    company VARCHAR(255),
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),

    CONSTRAINT fk_client_workspace
        FOREIGN KEY (workspace_id) REFERENCES tb_workspace (id)
);

CREATE TABLE tb_label (
    id UUID PRIMARY KEY,
    board_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    color VARCHAR(255),
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),

    CONSTRAINT uk_label_board_name
        UNIQUE (board_id, name),

    CONSTRAINT fk_label_board
        FOREIGN KEY (board_id) REFERENCES tb_board (id)
);

CREATE TABLE tb_task (
    id UUID PRIMARY KEY,
    workspace_id UUID NOT NULL,
    board_id UUID NOT NULL,
    column_id UUID NOT NULL,
    client_id UUID,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    position INTEGER NOT NULL,
    priority VARCHAR(255) NOT NULL DEFAULT 'MEDIUM',
    archived BOOLEAN NOT NULL DEFAULT FALSE,
    due_date TIMESTAMP(6),
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),

    CONSTRAINT ck_task_priority
        CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),

    CONSTRAINT fk_task_workspace
        FOREIGN KEY (workspace_id) REFERENCES tb_workspace (id),

    CONSTRAINT fk_task_board
        FOREIGN KEY (board_id) REFERENCES tb_board (id),

    CONSTRAINT fk_task_column
        FOREIGN KEY (column_id) REFERENCES tb_column (id),

    CONSTRAINT fk_task_client
        FOREIGN KEY (client_id) REFERENCES tb_client (id)
);

CREATE TABLE tb_task_assignees (
    task_id UUID NOT NULL,
    user_id UUID NOT NULL,

    CONSTRAINT pk_task_assignees
        PRIMARY KEY (task_id, user_id),

    CONSTRAINT fk_task_assignee_task
        FOREIGN KEY (task_id) REFERENCES tb_task (id),

    CONSTRAINT fk_task_assignee_user
        FOREIGN KEY (user_id) REFERENCES tb_user (id)
);

CREATE TABLE tb_task_labels (
    task_id UUID NOT NULL,
    label_id UUID NOT NULL,

    CONSTRAINT pk_task_labels
        PRIMARY KEY (task_id, label_id),

    CONSTRAINT fk_task_label_task
        FOREIGN KEY (task_id) REFERENCES tb_task (id),

    CONSTRAINT fk_task_label_label
        FOREIGN KEY (label_id) REFERENCES tb_label (id)
);

CREATE TABLE tb_checklist (
    id UUID PRIMARY KEY,
    task_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),

    CONSTRAINT fk_checklist_task
        FOREIGN KEY (task_id) REFERENCES tb_task (id)
);

CREATE TABLE tb_checklist_item (
    id UUID PRIMARY KEY,
    checklist_id UUID NOT NULL,
    content VARCHAR(255) NOT NULL,
    checked BOOLEAN NOT NULL DEFAULT FALSE,
    position INTEGER NOT NULL,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),

    CONSTRAINT fk_checklist_item_checklist
        FOREIGN KEY (checklist_id) REFERENCES tb_checklist (id)
);

CREATE TABLE tb_comments (
    id UUID PRIMARY KEY,
    task_id UUID NOT NULL,
    author_id UUID NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),

    CONSTRAINT fk_comment_task
        FOREIGN KEY (task_id) REFERENCES tb_task (id),

    CONSTRAINT fk_comment_author
        FOREIGN KEY (author_id) REFERENCES tb_user (id)
);

CREATE INDEX idx_membership_workspace
    ON tb_memberships (workspace_id);

CREATE INDEX idx_board_workspace
    ON tb_board (workspace_id);

CREATE INDEX idx_board_member_user
    ON tb_board_members (user_id);

CREATE INDEX idx_column_board_position
    ON tb_column (board_id, position);

CREATE INDEX idx_client_workspace
    ON tb_client (workspace_id);

CREATE INDEX idx_task_column_position
    ON tb_task (column_id, position);

CREATE INDEX idx_task_workspace
    ON tb_task (workspace_id);

CREATE INDEX idx_task_board
    ON tb_task (board_id);

CREATE INDEX idx_task_assignee_user
    ON tb_task_assignees (user_id);

CREATE INDEX idx_task_label_label
    ON tb_task_labels (label_id);

CREATE INDEX idx_checklist_task
    ON tb_checklist (task_id);

CREATE INDEX idx_checklist_item_position
    ON tb_checklist_item (checklist_id, position);

CREATE INDEX idx_comment_task
    ON tb_comments (task_id);