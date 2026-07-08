Architecture Decision Record - 0007

ADR-0007
Title: Entry–Node Association

Status
Accepted

Context

KFS is named a "file system," but it does not organize files — it organizes knowledge.

An early draft of the schema modeled the relationship between Entry and
Node as a single foreign key (Entry.node_id), implying that a Node owns
an Entry. This contradicts the core architecture, in which Nodes organize
knowledge but do not own it.

Knowledge naturally belongs in multiple contexts. This is one of the
reasons Entry and Node were separated as distinct entities in the first
place. A traditional file system asks "Where is this document?" KFS asks
"In what contexts is this knowledge relevant?" These are fundamentally
different questions, and the schema must reflect that difference.

Decision

Entries and Nodes have a many-to-many relationship.

A Node does not own an Entry; it provides one organizational context for
that Entry. Because knowledge may naturally exist in multiple
organizational contexts, KFS models the Entry–Node association explicitly
through the entry_node table. This preserves the independence of
knowledge from its organization and allows an Entry to appear under
multiple Nodes without duplication.

entry_node is not treated as an anonymous join table. It is a first-class
entity representing the statement "this Entry is organized under this
Node." Its responsibility is to answer "Where does this Entry appear?" —
not "Who owns this Entry?"

As a first-class entity, entry_node carries its own attributes,
independent of Entry and Node:

- display_order — ordering of an Entry within a given Node
- added_by — who placed the Entry in this Node
- added_at — when the Entry was placed in this Node

Consequences

Advantages
- Preserves the independence of knowledge from its organization
- An Entry may appear under multiple Nodes without duplicating content
- The association itself can carry meaning (ordering, provenance, and
  future attributes) without altering Entry or Node
- Naturally extensible to future capabilities such as favorite Nodes,
  pinned Entries, per-Node display modes, or node-specific notes on an
  Entry, since these are properties of the association, not of the
  Entry or the Node
- Reinforces the distinction between hierarchy ("where does this
  belong") and relationships ("what is this connected to") established
  elsewhere in the domain model

Disadvantages
- Slightly more complex than a single foreign key on Entry
- Requires a join to resolve an Entry's location(s), rather than a
  direct column lookup
- The integrity rule that an Entry's Knowledge Base must match the
  Knowledge Base of any Node it is organized under cannot be enforced
  by the database schema alone and must be enforced in the service
  layer

Related Schema

entry_node
----------
entry_id       FK -> entry.id
node_id        FK -> node.id
display_order
added_by
added_at

Primary key: (entry_id, node_id)
