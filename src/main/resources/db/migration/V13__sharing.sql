CREATE TABLE IF NOT EXISTS sharing (
    id BIGSERIAL PRIMARY KEY,
    granted_by BIGINT NOT NULL,
    granted_to BIGINT NOT NULL,
    shared_fs_node BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    version BIGINT DEFAULT 1,
    -- Constraints 
    FOREIGN KEY (granted_by) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (granted_to) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (shared_fs_node) REFERENCES fs_nodes(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sharing_permissions (
    id BIGSERIAL PRIMARY KEY,
    sharing_id BIGINT NOT NULL,
    sharing_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    version BIGINT DEFAULT 1,
    -- Constraints
    FOREIGN KEY (sharing_id) REFERENCES sharing(id) ON DELETE CASCADE
);
