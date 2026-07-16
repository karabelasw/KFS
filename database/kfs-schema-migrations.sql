-- ===================================================================
-- KFS - SCHEMA MIGRATIONS LOG
--
-- Append-only, chronological log of ALTER statements applied to an
-- EXISTING KFS database to bring it forward, without dropping data.
--
-- Contrast with kfs-schema-create.sql, which is destructive
-- (drop + rebuild) and represents the full current baseline.
--
-- Convention: each entry is dated, numbered, and briefly explains
-- WHY the change was made — not just what changed. Once an entry has
-- been run against a given environment (local/dev/prod), it is never
-- edited or removed; a later correction is its own new entry.
--
-- When kfs-schema-create.sql is eventually "finalized" as a new
-- baseline (e.g. at a major version bump), this file can be cleared
-- and restarted, since the baseline already reflects everything
-- below it.
-- ===================================================================

USE KFS;

-- -------------------------------------------------------------------
-- 2026-07-16 — Migration 001
-- Fix: entry.content was created as TINYTEXT (255-byte max) on the
-- local dev database due to an unqualified @Lob mapping in Entry.java
-- letting Hibernate's ddl-auto=update infer the smallest TEXT variant
-- on its own. kfs-schema-create.sql always specified TEXT correctly;
-- this migration brings a database that drifted from that baseline
-- back in line with it. Entry.java has since been corrected to pin
-- columnDefinition = "TEXT" explicitly, and ddl-auto has been switched
-- to validate so Hibernate can no longer alter schema on its own.
-- -------------------------------------------------------------------
ALTER TABLE entry
    MODIFY COLUMN content TEXT;

-- -------------------------------------------------------------------
-- Template for the next migration:
--
-- -- YYYY-MM-DD — Migration NNN
-- -- Why: <reason for the change, not just what changed>
-- ALTER TABLE <table>
--     <ADD COLUMN | MODIFY COLUMN | DROP COLUMN | ADD CONSTRAINT ...>;
-- -------------------------------------------------------------------
