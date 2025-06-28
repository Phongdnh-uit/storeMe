DROP TABLE IF EXISTS folders CASCADE;
DROP TABLE IF EXISTS files CASCADE;


CREATE TABLE IF NOT EXISTS fs_nodes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    size BIGINT DEFAULT 0,
    ancestor BIGINT[], -- PostgreSQL array type
    parent_id BIGINT,
    type VARCHAR(255),
    is_hidden BOOLEAN DEFAULT FALSE,
    is_locked BOOLEAN DEFAULT FALSE,
    last_accessed TIMESTAMP,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    version BIGINT DEFAULT 1,
  -- foreign keys
    CONSTRAINT fk_fsnode_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_fsnode_parent FOREIGN KEY (parent_id) REFERENCES fs_nodes(id)
);


CREATE TABLE IF NOT EXISTS file_metadata (
    id BIGSERIAL PRIMARY KEY,
    file_id BIGINT UNIQUE, -- one-to-one
    mime_type VARCHAR(255),
    extension VARCHAR(50),
    blob_key VARCHAR(255),
    version BIGINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,

    CONSTRAINT fk_filemetadata_file FOREIGN KEY (file_id) REFERENCES fs_nodes(id)
);

