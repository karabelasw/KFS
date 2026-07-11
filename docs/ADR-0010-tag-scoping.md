# ADR-0010: Tag Scoping

## Status
Accepted

## Context

Entries currently have no lightweight, user-defined categorization
mechanism. Filing into a Node (organization) and typed Relationships
(the graph) both exist, but neither answers a simpler, more informal
question: *"what loose labels does the user want to attach to this
Entry?"* — e.g. `weeknight`, `vegetarian`, `permit-required`.

Two shapes were considered for a `tag` table:

- **Global tags** — a single tag namespace, either system-wide or
  scoped to `user_id`, reusable across every Knowledge Base a user
  owns.
- **KB-scoped tags** — a `tag` table scoped to a single
  `knowledge_base_id`, with its own independent namespace per KB.

## Decision

Tags are **Knowledge-Base-scoped**.

- **`tag`**: `id`, `knowledge_base_id` (FK), `name`, standard audit
  columns (`created_at`, `created_by`, `last_modified`, `modified_by`,
  `archived_at`, `archived_by`). `UNIQUE(knowledge_base_id, name)`.
- **`entry_tag`**: a first-class association table between `entry` and
  `tag`, following the `entry_node` pattern established in ADR-0007 —
  composite primary key (`entry_id`, `tag_id`) rather than a
  surrogate key, plus `added_at` and `created_by`.

### Why KB-scoped rather than global

This mirrors the KB-as-root philosophy already applied throughout the
schema: `knowledge_base` is the true tree root, ownership is transitive
via `knowledge_base` (no direct `USER→ENTRY` line needed), and every
other owner-authored construct (Node, Entry, Relationship) is scoped to
a single KB. Scoping tags the same way keeps the pattern consistent
rather than introducing a second, differently-scoped vocabulary
concept alongside `content_type` and `relationship_type` (which are
intentionally global, system- or user-defined at the platform level,
not per-KB).

Three concrete considerations drove the decision:

1. **Namespace collision** — the same word (e.g. `quick`) can mean
   different things in different KBs (a 30-minute recipe vs. a
   low-effort software ticket). Global tags force either avoiding
   common words or accepting a tag list that mixes unrelated domains.
2. **Relevant suggestions** — a KB's tag vocabulary should reflect
   that KB's own content when the user is tagging an Entry, not the
   user's entire cross-KB tagging history.
3. **Collaboration boundary (forward-looking)** — `knowledge_base_access`
   (ADR-0008, deferred in full) will eventually let a KB owner grant a
   collaborator access to a single KB. Global or user-scoped tags would
   let a collaborator see or reuse the tag vocabulary from KBs they
   were never granted access to, a subtle information leak. KB-scoped
   tags keep the same boundary ADR-0008 already draws around
   Comments/Attachments intact.

### Why `entry_tag` is a first-class association, not a bare join table

Consistent with ADR-0007's reasoning for `entry_node`: keeping
`entry_tag` as its own table (rather than an implicit many-to-many)
allows metadata on the association itself. Here that's kept
deliberately minimal — `added_at` and `created_by` — since ADR-0008
means tagging is owner-authored only; there is no need for a
`display_order` analog the way `entry_node` needed one for filing
position within a Node.

### Why owner-authored only

Per ADR-0008's two-tier ownership model, KB owners author Entries,
Nodes, and Relationships; contributors are limited to Comments and
Attachments. Tags organize and categorize Entries in the same
authorial sense as Nodes and Relationships do, so tagging follows the
owner-authored tier rather than the contributor tier. `entry_tag`
carries a single `created_by`, not a broader contributor-permission
check.

## Consequences

- `tag` and `entry_tag` are additive; no existing table changes.
- Both are created after `knowledge_base` and `entry` in DDL execution
  order (as their FKs require), and after `user` for audit columns.
- `ON DELETE CASCADE` on `tag.knowledge_base_id` and both
  `entry_tag` FKs means archiving/deleting a KB or an Entry cleans up
  associated tags and taggings automatically — no orphaned rows.
- Cross-KB tag search/unification (e.g. "show me everything tagged
  `vegetarian` across all my KBs") is not directly supported by this
  design; it would require an application-layer aggregation across
  KB-scoped tag names, not a schema-level join. Not a current
  requirement, but noted for awareness.
- Tag `name` normalization (case, whitespace) is left to the service
  layer, consistent with how similar concerns are handled elsewhere in
  this schema; `UNIQUE(knowledge_base_id, name)` assumes normalized
  input.
- This is additive and does not alter ADR-0007, ADR-0008, or ADR-0009.

## Open Questions (deferred)

- Should `tag` carry optional display metadata (color, description),
  mirroring the lightweight-but-extensible pattern used elsewhere? Not
  needed at current scope.
- Should cross-KB tag aggregation be revisited once real usage patterns
  emerge (e.g. a user with many KBs wanting a unified "recently tagged"
  view)? Deferred; would likely be a service-layer concern rather than
  a schema change.
