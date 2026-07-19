# ADR-0011: Schema Change Management — Create vs. Migrations Scripts

## Status
Accepted

## Context

Through the JPA implementation phase, `spring.jpa.hibernate.ddl-auto=update`
was used to let Hibernate reconcile the live database against `@Entity`
annotations automatically. This surfaced a real problem: Hibernate silently
altered `entry.content` from `TEXT` (as correctly specified in
`kfs-schema-v2.sql`) to `TINYTEXT`, inferred from an unqualified `@Lob`
mapping on `Entry.java`. The SQL file was never wrong; the live database
and the file quietly diverged, with no record of when or why.

This exposed two related gaps:

1. No mechanism prevented Hibernate from mutating schema on its own,
   outside the deliberate schema-design workflow already established
   (ADRs → `kfs-schema-v2.sql` → ERD).
2. A single monolithic schema file conflates two very different
   operations: standing up a database from nothing, and evolving an
   existing database that already holds data.

## Decision

`ddl-auto` is set to `validate`, not `update`. Hibernate may check the
live schema against entity mappings and fail loudly on mismatch, but
may never alter schema itself. All schema changes now happen
exclusively through hand-authored SQL, matching the project's existing
"schema file is the source of truth" principle — this closes the gap
`ddl-auto=update` had quietly opened.

The single schema file is split into two, with distinct purposes:

- **`kfs-schema-create.sql`** — destructive. `DROP DATABASE IF EXISTS
  KFS` followed by the full `CREATE DATABASE` / `CREATE TABLE` / seed
  `INSERT` set. Represents the current full baseline. Used only for
  fresh environments (new dev setup, a deliberately reset local DB) —
  never run against a database holding data worth keeping.

- **`kfs-schema-migrations.sql`** — additive, append-only, chronological.
  A log of `ALTER TABLE` statements applied to bring an *existing*
  database forward without data loss. Each entry is dated, numbered,
  and documents *why* the change was made, not just what changed.
  Entries are never edited or removed once applied to any environment;
  a correction is its own new entry.

## Why two files instead of one

A single `CREATE TABLE IF NOT EXISTS` file (the prior approach) works
for a brand-new database but says nothing about how an *existing*,
populated database should evolve — running the same file again does
nothing for a schema change, since `IF NOT EXISTS` silently no-ops on
existing tables. Separating "build from nothing" from "evolve what
exists" makes both operations explicit and prevents accidentally
reaching for the destructive option against real data.

## Why not adopt a migration framework (Flyway/Liquibase) now

Both are reasonable, more automated alternatives — Flyway in particular
integrates natively with Spring Boot and would auto-apply versioned
migrations on startup. Deferred for the current phase because the
project's existing workflow (hand-authored SQL, reviewed and pushed to
GitHub deliberately, synced each session) already provides the review
discipline a migration tool would otherwise enforce, and introducing a
new dependency/config surface isn't justified while still in early
JPA implementation. Worth revisiting once `node`, `tag`, `relationship`,
and `knowledgebase` are fully modeled and schema changes become more
frequent or team-based.

## Consequences

- `kfs-schema-v2.sql` is superseded by `kfs-schema-create.sql`
  (identical content, destructive header added) and is no longer the
  file to edit going forward.
- Any schema change now requires two actions: add the change to
  `kfs-schema-create.sql` (so the baseline stays current) AND add a
  corresponding entry to `kfs-schema-migrations.sql` (so existing
  databases, including the local dev DB, can be brought forward).
- The ERD (`kfs-erd.mermaid`) continues to be generated from the
  create script, since that always reflects the current full baseline.
- `application.properties` / `application-*.properties` `ddl-auto`
  values must be audited — `validate` in all environments going
  forward; `update` should not reappear.
- This is additive and does not alter ADR-0006 through ADR-0010.

## Open Questions (deferred)

- At what point does `kfs-schema-migrations.sql` get "rolled into" a
  new baseline and cleared? No trigger defined yet — likely a major
  milestone (e.g. JPA layer complete, or a version bump).
- Should migration entries eventually be split into one file per
  change rather than one running log, once the log grows long? Not
  needed at current scale (one entry).
