---
name: "create-spec"
description: "Generate a feature specification from a high-level idea"
metadata:
  version: "1.0.0"
---

# Create Spec Skill

## When to Use
Use this when a feature idea is still broad and needs to be turned into a concrete specification.

## Input
- A feature idea or request
- Optional constraints, examples, and references

## Context to Load
1. `architecture.md`
2. Relevant app-level `AGENTS.md` and `app-state.md` files
3. Relevant convention docs in `.agents/docs/conventions/`
4. Existing feature specs, if this feature touches related behavior
5. Relevant colocated `DESIGN.md` files, if they exist
6. Codebase files only when they are needed to understand the current state
7. `.agents/_templates/_feature-templates/spec.md` for the spec template

## Process
1. Read the minimum context needed to understand the feature.
2. Identify the owning app and the feature folder location.
3. Clarify the goal, scope, non-goals, edge cases, and dependencies.
4. Draft `spec.md` from `.agents/_templates/_feature-templates/spec.md`.
5. Keep the spec precise enough for implementation without guessing.
6. Present the spec for human review.

## Output
- `apps/<app>/features/NNN-feature-name/spec.md`
