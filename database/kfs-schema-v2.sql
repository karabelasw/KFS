CREATE DATABASE IF NOT EXISTS KFS;
USE KFS;

/**
Minimal user table — placeholder pending full security/User design.
Referenced by every audit column below.
*/
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

/**
Reference tables
*/
CREATE TABLE IF NOT EXISTS entry_status (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    display_order INT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO entry_status (code, name, description, display_order)
VALUES
('INBOX', 'Inbox', 'Default value. All entries are set to an INBOX state', 1),
('ACTIVE', 'Active', 'Entry has been organized under one or more Nodes', 2),
('ARCHIVED', 'Archived', 'Entry has been moved into an unsearchable state', 3);

CREATE TABLE IF NOT EXISTS source (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    display_order INT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO source (code, name, description, display_order)
VALUES
('MANUAL', 'Manual Entry', 'Created directly by the user', 1),
('IMPORT', 'Import', 'Created via bulk import', 2),
('AI_SUGGESTED', 'AI Suggested', 'Created or classified with AI assistance', 3);

/**
Knowledge Base
*/
CREATE TABLE IF NOT EXISTS knowledge_base (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL,
    last_modified TIMESTAMP NULL,
    modified_by BIGINT NULL,
    archived_at TIMESTAMP NULL,
    archived_by BIGINT NULL,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_kb_created_by
        FOREIGN KEY (created_by) REFERENCES user(id),
    CONSTRAINT fk_kb_modified_by
        FOREIGN KEY (modified_by) REFERENCES user(id),
    CONSTRAINT fk_kb_archived_by
        FOREIGN KEY (archived_by) REFERENCES user(id)
);

/**
Entry
*/
CREATE TABLE IF NOT EXISTS entry (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    knowledge_base_id BIGINT NOT NULL,
    title VARCHAR(255),
    content TEXT,
    status_id BIGINT NOT NULL,
    source_id BIGINT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL,
    last_modified TIMESTAMP NULL,
    modified_by BIGINT NULL,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_entry_kb
        FOREIGN KEY (knowledge_base_id)
        REFERENCES knowledge_base(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_entry_status
        FOREIGN KEY (status_id)
        REFERENCES entry_status(id),

    CONSTRAINT fk_entry_source
        FOREIGN KEY (source_id)
        REFERENCES source(id),

    CONSTRAINT fk_entry_created_by
        FOREIGN KEY (created_by) REFERENCES user(id),
    CONSTRAINT fk_entry_modified_by
        FOREIGN KEY (modified_by) REFERENCES user(id)
);

/**
Node
*/
CREATE TABLE IF NOT EXISTS node (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    knowledge_base_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    parent_node_id BIGINT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL,
    last_modified TIMESTAMP NULL,
    modified_by BIGINT NULL,
    archived_at TIMESTAMP NULL,
    archived_by BIGINT NULL,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_node_kb
        FOREIGN KEY (knowledge_base_id)
        REFERENCES knowledge_base(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_node_parent
        FOREIGN KEY (parent_node_id)
        REFERENCES node(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_node_created_by
        FOREIGN KEY (created_by) REFERENCES user(id),
    CONSTRAINT fk_node_modified_by
        FOREIGN KEY (modified_by) REFERENCES user(id),
    CONSTRAINT fk_node_archived_by
        FOREIGN KEY (archived_by) REFERENCES user(id)
);

/**
EntryNode — the organization table (see ADR-0007).
Answers "Where does this Entry appear?", not "Who owns this Entry?"

Key Integrity Rule (not enforceable in plain SQL — must be enforced in the service layer):
entry.knowledge_base_id must match node.knowledge_base_id for any linked node.
*/
CREATE TABLE IF NOT EXISTS entry_node (
    entry_id BIGINT NOT NULL,
    node_id BIGINT NOT NULL,
    display_order INT NOT NULL DEFAULT 0,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL,
    last_modified TIMESTAMP NULL,
    modified_by BIGINT NULL,
    archived_at TIMESTAMP NULL,
    archived_by BIGINT NULL,
    version BIGINT NOT NULL DEFAULT 0,

    PRIMARY KEY (entry_id, node_id),

    CONSTRAINT fk_en_entry
        FOREIGN KEY (entry_id)
        REFERENCES entry(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_en_node
        FOREIGN KEY (node_id)
        REFERENCES node(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_en_created_by
        FOREIGN KEY (created_by) REFERENCES user(id),
    CONSTRAINT fk_en_modified_by
        FOREIGN KEY (modified_by) REFERENCES user(id),
    CONSTRAINT fk_en_archived_by
        FOREIGN KEY (archived_by) REFERENCES user(id)
);
