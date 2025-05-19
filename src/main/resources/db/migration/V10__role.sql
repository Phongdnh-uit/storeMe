CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT
);

ALTER TABLE users ADD COLUMN IF NOT EXISTS role_id BIGINT REFERENCES roles(id);

INSERT INTO roles (name, description, created_by, updated_by) VALUES 
('admin', 'Administrator role with full access', 0, 0);

INSERT INTO roles (name, description, created_by, updated_by) VALUES
('user', 'Regular user role with limited access', 0, 0);
