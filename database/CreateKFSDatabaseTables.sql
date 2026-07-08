
CREATE DATABASE IF NOT EXISTS KFS;

use KFS;

/**
Create reference tables
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
('INBOX', 'Inbox', 'Default Value!  All entries are set to an INBOX state', 1),
('ACTIVE', 'Active', 'Entry has been assigned to a Knowledge Base',  2),
('ARCHIVED', 'Archived','Entry has been moved into an unsearchable state',  3);



CREATE TABLE IF NOT EXISTS knowledge_base (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    createdBy VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS entry (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    knowledge_base_id BIGINT NOT NULL,
    title VARCHAR(255),
    content TEXT,    
    status_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,
    createdBy VARCHAR(255) NOT NULL,
    
    CONSTRAINT fk_entry_kb
        FOREIGN KEY (knowledge_base_id)
        REFERENCES knowledge_base(id)
        ON DELETE CASCADE,
        
	CONSTRAINT fk_entry_status
		FOREIGN KEY (status_id)
		REFERENCES entry_status(id)  
);

/** Node**/
CREATE TABLE IF NOT EXISTS node (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    knowledge_base_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    parent_node_id BIGINT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    createdBy VARCHAR(255) NOT NULL,
    
    CONSTRAINT fk_node_kb
        FOREIGN KEY (knowledge_base_id)
        REFERENCES knowledge_base(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_node_parent
        FOREIGN KEY (parent_node_id)
        REFERENCES node(id)
        ON DELETE SET NULL
);

/**
Key Integrity Rule (important, not enforceable purely in basic SQL)
You MUST enforce in service layer:
Entry.knowledge_base_id must match Node.knowledge_base_id for any linked Node
Otherwise you corrupt the model.
*/

CREATE TABLE IF NOT EXISTS entry_node (
    entry_id BIGINT NOT NULL,
    node_id BIGINT NOT NULL,

    PRIMARY KEY (entry_id, node_id),

    CONSTRAINT fk_en_entry
        FOREIGN KEY (entry_id)
        REFERENCES entry(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_en_node
        FOREIGN KEY (node_id)
        REFERENCES node(id)
        ON DELETE CASCADE
);






