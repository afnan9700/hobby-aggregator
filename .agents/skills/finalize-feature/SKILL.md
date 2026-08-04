---
name: finalize-feature
description: 'Update design docs, app-state, and session-log after implementation'
metadata:
  version: 1.0.0
---

# Finalize Feature Skill

## When to Use
Use this after a feature has been implemented and reviewed, when the code is done and the descriptive documentation needs to catch up. 

## Input
- Docs to create or update:
  - `design.md`
  - Colocated `DESIGN.md` files for submodules, if needed
  - `app-state.md`
  - `agent-log.md`

## Context to Load
* `.agents/_templates/_feature-templates/design-index.md`
* `.agents/_templates/_feature-templates/design-colocated.md`
* `.agents/_templates/_app-templates/app-state.md`
* `.agents/_templates/agent-log-entry.md`

This skill is used within the same context as the implementation plan, so do not load any additional context unless specified. 

## Process
1. Create or update the feature-level `design.md` so it describes the feature as implemented. Follow the format specified in `.agents/_templates/_feature-template/design-index.md`. 
2. Create or update colocated `DESIGN.md` files for any submodules whose responsibilities or invariants changed. Follow the format specified in `.agents/_templates/design-colocated.md`. 
3. Update `app-state.md` to reflect the new project state. Follow the format specified in `.agents/_templates/_app-templates/app-state.md`.
4. Append a summary entry to `agent-log.md`. Follow the format specified in `.agents/_templates/agent-log-entry.md`.

## Rules
- This skill updates documentation; it does not redesign the feature.
- Create or update only the doc files mentioned in the user input. Do not create new doc files unless explicitly requested.
- Keep design docs descriptive, not procedural.
- Do not create unnecessary doc files just to satisfy a checklist.

## Output
- Updated `design.md`
- Updated colocated `DESIGN.md` files, if needed
- Updated `app-state.md`
- Updated `agent-log.md`
