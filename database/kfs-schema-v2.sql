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
System user — represents KFS itself, not an end user.
Used to attribute seeded/system-defined data (e.g. curated Content
Types) that isn't created by any actual person.
*/
INSERT INTO user (username, email)
VALUES ('system', 'system@kfs.local');

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
    owner_id BIGINT NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL,
    last_modified TIMESTAMP NULL,
    modified_by BIGINT NULL,
    archived_at TIMESTAMP NULL,
    archived_by BIGINT NULL,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_kb_owner
        FOREIGN KEY (owner_id) REFERENCES user(id),
    CONSTRAINT fk_kb_created_by
        FOREIGN KEY (created_by) REFERENCES user(id),
    CONSTRAINT fk_kb_modified_by
        FOREIGN KEY (modified_by) REFERENCES user(id),
    CONSTRAINT fk_kb_archived_by
        FOREIGN KEY (archived_by) REFERENCES user(id)
);

/**
Content type — reference table.
Governs which specialized fields (Attributes) apply to an Entry.

system_defined = TRUE:  a curated type shipped by KFS (e.g. Recipe),
                         backed by its own extension table
                         (e.g. recipe_detail), owner_id is NULL.
system_defined = FALSE: a type authored by a user at runtime, no
                         extension table; its fields are described by
                         content_type_attribute and its values are
                         stored in entry.custom_attributes (JSON).
*/
CREATE TABLE IF NOT EXISTS content_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    system_defined BOOLEAN NOT NULL DEFAULT TRUE,
    owner_id BIGINT NULL,
    display_order INT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL,
    archived_at TIMESTAMP NULL,
    archived_by BIGINT NULL,

    CONSTRAINT fk_ct_owner
        FOREIGN KEY (owner_id) REFERENCES user(id),
    CONSTRAINT fk_ct_created_by
        FOREIGN KEY (created_by) REFERENCES user(id),
    CONSTRAINT fk_ct_archived_by
        FOREIGN KEY (archived_by) REFERENCES user(id),

    CONSTRAINT ck_ct_owner_matches_scope
        CHECK (
            (system_defined = TRUE AND owner_id IS NULL)
            OR
            (system_defined = FALSE AND owner_id IS NOT NULL)
        )
);

INSERT INTO content_type (code, name, description, system_defined, display_order, created_by)
VALUES
('NOTE', 'Note', 'General-purpose note or reflection; no specialized attributes', TRUE, 1, (SELECT id FROM user WHERE username = 'system')),
('RECIPE', 'Recipe', 'A recipe, backed by recipe_detail', TRUE, 2, (SELECT id FROM user WHERE username = 'system'));

/**
Content type attribute — the field definitions for a user-defined
Content Type. Only populated when content_type.system_defined = FALSE;
curated types define their fields as real columns on their own
extension table instead (e.g. recipe_detail).
*/
CREATE TABLE IF NOT EXISTS content_type_attribute (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content_type_id BIGINT NOT NULL,
    attribute_name VARCHAR(100) NOT NULL,
    data_type VARCHAR(50) NOT NULL,
    display_order INT NOT NULL DEFAULT 0,
    required BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL,
    last_modified TIMESTAMP NULL,
    modified_by BIGINT NULL,

    CONSTRAINT fk_cta_content_type
        FOREIGN KEY (content_type_id)
        REFERENCES content_type(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_cta_created_by
        FOREIGN KEY (created_by) REFERENCES user(id),
    CONSTRAINT fk_cta_modified_by
        FOREIGN KEY (modified_by) REFERENCES user(id),

    CONSTRAINT ck_cta_data_type
        CHECK (data_type IN ('TEXT', 'NUMBER', 'DATE', 'BOOLEAN')),

    CONSTRAINT uq_cta_name_per_type
        UNIQUE (content_type_id, attribute_name)
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
    source_id BIGINT NOT NULL,
    content_type_id BIGINT NOT NULL,
    custom_attributes JSON NULL,

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

    CONSTRAINT fk_entry_content_type
        FOREIGN KEY (content_type_id)
        REFERENCES content_type(id),

    CONSTRAINT fk_entry_source
        FOREIGN KEY (source_id)
        REFERENCES source(id),

    CONSTRAINT fk_entry_created_by
        FOREIGN KEY (created_by) REFERENCES user(id),
    CONSTRAINT fk_entry_modified_by
        FOREIGN KEY (modified_by) REFERENCES user(id)
);

/**
Recipe detail — curated extension table for content_type = RECIPE.
1:1 with entry, sharing its primary key. Only present when
entry.content_type_id references RECIPE.
*/
CREATE TABLE IF NOT EXISTS recipe_detail (
    entry_id BIGINT PRIMARY KEY,
    prep_time_minutes INT,
    cook_time_minutes INT,
    servings INT,
    difficulty VARCHAR(50),

    CONSTRAINT fk_recipe_entry
        FOREIGN KEY (entry_id)
        REFERENCES entry(id)
        ON DELETE CASCADE
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

/**
Relationship type — reference table.
Defines the vocabulary of the knowledge graph (see KFS Design Session Archive).
*/
CREATE TABLE IF NOT EXISTS relationship_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    symmetric BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO relationship_type (code, name, description, symmetric, display_order)
VALUES
('RELATED_TO', 'Related To', 'General association between two entries', TRUE, 1),
('REFERENCES', 'References', 'Source entry references target entry', FALSE, 2),
('REQUIRES', 'Requires', 'Source entry depends on or requires target entry', FALSE, 3),
('PRECEDES', 'Precedes', 'Source entry comes before target entry', FALSE, 4),
('FOLLOW_UP', 'Follow Up', 'Target entry is a follow-up to source entry', FALSE, 5),
('SUPERSEDES', 'Supersedes', 'Source entry replaces or supersedes target entry', FALSE, 6),
('PART_OF', 'Part Of', 'Source entry is a component of target entry', FALSE, 7),
('CAUSED_BY', 'Caused By', 'Source entry was caused by target entry', FALSE, 8);

/**
Relationship — the knowledge graph.
The tree (Node) answers "Where does this belong?"
The graph (Relationship) answers "What is this connected to?"

Relationships connect Entries directly, independent of Node placement,
and may cross Knowledge Base boundaries (see open question below).
*/
CREATE TABLE IF NOT EXISTS relationship (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    source_entry_id BIGINT NOT NULL,
    target_entry_id BIGINT NOT NULL,
    relationship_type_id BIGINT NOT NULL,
    notes VARCHAR(500) NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL,
    last_modified TIMESTAMP NULL,
    modified_by BIGINT NULL,
    archived_at TIMESTAMP NULL,
    archived_by BIGINT NULL,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_rel_source_entry
        FOREIGN KEY (source_entry_id)
        REFERENCES entry(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_rel_target_entry
        FOREIGN KEY (target_entry_id)
        REFERENCES entry(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_rel_type
        FOREIGN KEY (relationship_type_id)
        REFERENCES relationship_type(id),

    CONSTRAINT fk_rel_created_by
        FOREIGN KEY (created_by) REFERENCES user(id),
    CONSTRAINT fk_rel_modified_by
        FOREIGN KEY (modified_by) REFERENCES user(id),
    CONSTRAINT fk_rel_archived_by
        FOREIGN KEY (archived_by) REFERENCES user(id),

    CONSTRAINT uq_rel_edge
        UNIQUE (source_entry_id, target_entry_id, relationship_type_id),

    CONSTRAINT ck_rel_no_self_link
        CHECK (source_entry_id <> target_entry_id)
);

/**
Access level — reference table.
Defines the collaborative permission vocabulary (see ADR-0008).
Owner access is implicit via knowledge_base.owner_id and never
represented as a row here.
*/
CREATE TABLE IF NOT EXISTS access_level (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    display_order INT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO access_level (code, name, description, display_order)
VALUES
('VIEWER', 'Viewer', 'May view the Knowledge Base but not add comments or attachments', 1),
('CONTRIBUTOR', 'Contributor', 'May view and add Comments and Attachments, per ADR-0008', 2);

/**
KnowledgeBaseAccess — grants a non-owner user access to a Knowledge Base.
The owner (knowledge_base.owner_id) always has full authorial access and
is never represented as a row here.
*/
CREATE TABLE IF NOT EXISTS knowledge_base_access (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    knowledge_base_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    access_level_id BIGINT NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL,
    last_modified TIMESTAMP NULL,
    modified_by BIGINT NULL,
    archived_at TIMESTAMP NULL,
    archived_by BIGINT NULL,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_kba_kb
        FOREIGN KEY (knowledge_base_id)
        REFERENCES knowledge_base(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_kba_user
        FOREIGN KEY (user_id) REFERENCES user(id),

    CONSTRAINT fk_kba_level
        FOREIGN KEY (access_level_id) REFERENCES access_level(id),

    CONSTRAINT fk_kba_created_by
        FOREIGN KEY (created_by) REFERENCES user(id),
    CONSTRAINT fk_kba_modified_by
        FOREIGN KEY (modified_by) REFERENCES user(id),
    CONSTRAINT fk_kba_archived_by
        FOREIGN KEY (archived_by) REFERENCES user(id),

    CONSTRAINT uq_kba_kb_user
        UNIQUE (knowledge_base_id, user_id)
);

/**
Comment — collaborative layer (see ADR-0008).
Attached to an Entry. May be authored by the owner or by any user
granted CONTRIBUTOR access to the Entry's Knowledge Base.
*/
CREATE TABLE IF NOT EXISTS comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    entry_id BIGINT NOT NULL,
    content TEXT NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL,
    last_modified TIMESTAMP NULL,
    modified_by BIGINT NULL,
    archived_at TIMESTAMP NULL,
    archived_by BIGINT NULL,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_comment_entry
        FOREIGN KEY (entry_id)
        REFERENCES entry(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_comment_created_by
        FOREIGN KEY (created_by) REFERENCES user(id),
    CONSTRAINT fk_comment_modified_by
        FOREIGN KEY (modified_by) REFERENCES user(id),
    CONSTRAINT fk_comment_archived_by
        FOREIGN KEY (archived_by) REFERENCES user(id)
);

/**
File — content-addressed physical file registry.
Represents a single physical file on disk, deduplicated by content_hash.
Not tied to any single Entry; multiple Attachments (across different
Entries, even across different Knowledge Bases) may reference the same
File without duplicating storage.
*/
CREATE TABLE IF NOT EXISTS file (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(1000) NOT NULL,
    mime_type VARCHAR(127) NOT NULL,
    file_size BIGINT NOT NULL,
    content_hash CHAR(64) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL,
    archived_at TIMESTAMP NULL,
    archived_by BIGINT NULL,

    CONSTRAINT fk_file_created_by
        FOREIGN KEY (created_by) REFERENCES user(id),
    CONSTRAINT fk_file_archived_by
        FOREIGN KEY (archived_by) REFERENCES user(id),

    CONSTRAINT uq_file_hash
        UNIQUE (content_hash)
);

/**
Attachment type — reference table.
FILE: attachment points at a stored File (content-addressed, on disk).
LINK: attachment points at external content (e.g. a video URL) with no
physical file stored by KFS.
*/
CREATE TABLE IF NOT EXISTS attachment_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    display_order INT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO attachment_type (code, name, description, display_order)
VALUES
('FILE', 'File', 'A stored file on disk, referenced by content hash', 1),
('LINK', 'Link', 'An external URL, e.g. a video or web page', 2);

/**
Attachment — collaborative layer (see ADR-0008).
The association between an Entry and either a stored File or an
external link. Exactly one of file_id / external_url is set.
May be authored by the owner or by any user granted CONTRIBUTOR
access to the Entry's Knowledge Base.
*/
CREATE TABLE IF NOT EXISTS attachment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    entry_id BIGINT NOT NULL,
    attachment_type_id BIGINT NOT NULL,
    file_id BIGINT NULL,
    external_url VARCHAR(1000) NULL,
    display_name VARCHAR(255) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL,
    last_modified TIMESTAMP NULL,
    modified_by BIGINT NULL,
    archived_at TIMESTAMP NULL,
    archived_by BIGINT NULL,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_attachment_entry
        FOREIGN KEY (entry_id)
        REFERENCES entry(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_attachment_type
        FOREIGN KEY (attachment_type_id) REFERENCES attachment_type(id),

    CONSTRAINT fk_attachment_file
        FOREIGN KEY (file_id) REFERENCES file(id),

    CONSTRAINT fk_attachment_created_by
        FOREIGN KEY (created_by) REFERENCES user(id),
    CONSTRAINT fk_attachment_modified_by
        FOREIGN KEY (modified_by) REFERENCES user(id),
    CONSTRAINT fk_attachment_archived_by
        FOREIGN KEY (archived_by) REFERENCES user(id),

    CONSTRAINT ck_attachment_exactly_one_target
        CHECK (
            (file_id IS NOT NULL AND external_url IS NULL)
            OR
            (file_id IS NULL AND external_url IS NOT NULL)
        ),

    CONSTRAINT uq_attachment_display_name_per_entry
        UNIQUE (entry_id, display_name)
);
