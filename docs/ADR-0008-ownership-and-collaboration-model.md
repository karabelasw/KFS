Architecture Decision Record - 0008

ADR-0008
Title: Ownership and Collaboration Model

Status
Accepted

Context

KFS is designed to support sharing: a user may share a Knowledge Base
with family members or a broader audience, while remaining its owner
and steward (KFS Mission Statement; KFS Philosophy).

Once sharing exists, "who created a record" and "who owns it" are no
longer guaranteed to be the same user. A shared collaborator may
interact with a Knowledge Base without owning it. The schema's
audit columns (created_by, modified_by) track authorship of a change,
but do not by themselves express authorization — who is allowed to
make that change in the first place.

This distinction became concrete while analyzing Relationships (see
ADR-0007 and the relationship table): if collaborators could edit
Entries, Nodes, or Relationships directly, it would be possible for a
Relationship to connect Entries owned by different users, and for a
Knowledge Base's core structure to be altered by someone other than
its owner. Enforcing this at the database level would require
denormalizing ownership across entry, node, and relationship, adding
schema complexity to solve what is fundamentally an authorization
question.

The KFS Philosophy states that knowledge belongs to the user, who is
its author, owner, and steward, and that final authority always
remains with the user — a principle applied there to AI assistance,
but which applies equally to other people.

Decision

Ownership of the core knowledge structure — Entry, Node, and
Relationship — is single-party and non-transferable except by the
owner. Only the owner of a Knowledge Base may create, edit, organize,
or connect the Entries, Nodes, and Relationships within it.

Collaboration is supported through a separate, secondary layer:
supporting entities such as Comments and Attachments. Users granted
access to a Knowledge Base may add and edit their own Comments and
Attachments, but may not alter the Entries, Nodes, or Relationships
that make up the owner's knowledge itself.

This establishes two distinct classes of write access:

- Authorial (owner-only): Entry, Node, Relationship
- Collaborative (shared access): Comment, Attachment, and similar
  supporting entities

knowledge_base carries an explicit owner_id, distinct from
created_by. created_by records who technically created the record;
owner_id records who has authority over it. These may diverge in the
future (for example, if ownership is ever transferred), and are
therefore modeled as separate fields even though they hold the same
value today.

Consequences

Advantages
- Resolves cross-ownership ambiguity in Relationships without
  schema-level denormalization: because only an owner can create
  Entries, Nodes, or Relationships in their own Knowledge Base, a
  Relationship can never legitimately connect Entries with different
  owners.
- Matches the Philosophy's principle that final authority over
  knowledge remains with the user, extended from AI assistance to
  human collaborators as well.
- A single, reusable authorization check (is the requesting user the
  Knowledge Base owner) governs all writes to Entry, Node, and
  Relationship, rather than a cross-entity comparison.
- Cleanly separates the core knowledge model from the collaborative
  layer, consistent with the earlier separation of Entry from its
  supporting entities (Attachments, Images, Comments, Tags).

Disadvantages
- Requires a future access/sharing mechanism (e.g. a
  knowledge_base_access table associating users with a Knowledge Base
  and a permission level) to determine who may add Comments or
  Attachments at all. This does not yet exist in the schema.
- Collaborators cannot directly improve or correct an Entry's content;
  any such change must go through the owner, which may limit
  collaborative editing use cases if they become desired later.
- Ownership transfer (e.g. inheritance, account succession) is not
  addressed by this decision and will require its own future design.

Related Schema

knowledge_base.owner_id — FK to user(id), distinct from created_by.

Not yet implemented: an access/sharing table governing which users may
view, comment on, or attach files to a Knowledge Base they do not own.
