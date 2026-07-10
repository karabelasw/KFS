# ADR-0009: System-Level User Roles

## Status
Accepted

## Context

The `user` table has, since its introduction, been a deliberate placeholder —
`id`, `username`, `email`, `created_at` — pending a full security/User design.
It carries no notion of platform-wide authority.

The only permission concept that exists today is `access_level`
(`VIEWER`, `CONTRIBUTOR`) paired with `knowledge_base_access`, established in
ADR-0008. That model answers a narrower question: *"what can this user do
within this specific Knowledge Base?"* It is intentionally scoped per-KB, with
Owner authority implicit via `knowledge_base.owner_id`.

Nothing in the schema currently answers a different, orthogonal question:
*"what can this user do on the platform, independent of any KB they own or
collaborate on?"* — e.g. administrative capabilities, moderation, or simply
distinguishing a standard end user from an administrator. This gap became
apparent when reviewing the schema for anything resembling roles or
permissions and finding only the KB-scoped model.

A related wrinkle: the seeded `system` user (introduced to attribute
system-defined data such as curated Content Types) is not a person and should
not be represented with the same authority label as a human administrator.

## Decision

Introduce a **system-wide role** as a single, mandatory attribute of every
user — not a many-to-many capability set.

- **`system_role`**: a reference table following the same shape as existing
  vocabulary tables in this schema (`entry_status`, `source`, `access_level`,
  `attachment_type`, `relationship_type`): `code`, `name`, `description`,
  `display_order`, `active`.
- Seeded values: `ADMIN`, `USER`, `SYSTEM`.
- **`user.system_role_id`**: a required (`NOT NULL`) foreign key to
  `system_role`. Every user holds **exactly one** role at all times.
- The seeded `system` account is assigned the `SYSTEM` role, distinguishing
  it from both `ADMIN` and `USER` — it represents KFS itself, not a person
  with elevated privileges.

### Why single-role (FK) instead of multi-role (join table)

`knowledge_base_access` already establishes the join-table pattern for
many-to-many, per-resource permissions, and that pattern was considered here.
It was rejected for system-level roles for this schema iteration: a user's
platform-wide authority is a single, exclusive classification (are they an
administrator or not?), not a composable set of capabilities. A simple
`NOT NULL` foreign key keeps the authority question answerable with a single
join, avoids the ambiguity of a user holding zero or multiple system roles
simultaneously, and mirrors the same "single source of truth" discipline
applied to `knowledge_base.owner_id` and `entry.status_id` elsewhere in this
schema (see ADR-0007, ADR-0008).

If a future need arises for composable, non-exclusive system capabilities
(e.g. a moderator who is not a full administrator, or a user holding more
than one simultaneous capability), that will warrant a new ADR and a
join-table design at that time — it is out of scope here.

### Why a new table rather than reusing `access_level`

`access_level` is explicitly scoped to KB collaboration
(`knowledge_base_access.access_level_id`) and its vocabulary (`VIEWER`,
`CONTRIBUTOR`) has no meaningful overlap with platform-wide authority.
Reusing it would conflate two unrelated permission domains — one scoped to a
single KB, the other scoped to the entire platform — under one vocabulary
table, violating the same single-source-of-truth principle this decision is
meant to uphold.

### Why `system_role` rather than `role`

`ROLE` is a reserved keyword in MySQL 8.0.19+ (`CREATE ROLE`, `GRANT ... TO
ROLE`). While not strictly disallowed as an identifier, `system_role` avoids
the collision entirely and leaves the unqualified term available if a
future, differently-scoped role concept is ever introduced.

## Consequences

- `user` is no longer purely a placeholder table — it now carries a real
  authority attribute, though it remains otherwise minimal pending the full
  security/User design.
- `system_role` must be created and seeded before `user` in DDL execution
  order, since `user.system_role_id` is `NOT NULL` from creation. The schema
  file has been reordered accordingly.
- The seeded `system` account's insert statement now resolves its role via
  `SELECT id FROM system_role WHERE code = 'SYSTEM'`, consistent with how
  other seed data in this schema resolves reference-table foreign keys.
- Service-layer authorization logic (not yet implemented) will need to treat
  `system_role_id = ADMIN` as platform-wide authority, independent of and
  additive to any KB-level `access_level` a user may separately hold via
  `knowledge_base_access`.
- This is additive and does not alter ADR-0007 or ADR-0008; KB-level
  ownership and collaboration semantics are unchanged.

## Open Questions (deferred)

- Should `ADMIN` implicitly bypass KB-level ownership restrictions (e.g. edit
  any KB), or does platform administration remain entirely separate from
  content authorship? Not addressed by this ADR.
- Should `system_role` gain a `system_defined` flag or otherwise support
  custom roles, mirroring the two-tier `content_type` pattern? Not needed at
  current scope (three fixed, code-driven roles) but noted for awareness if
  role requirements grow.
