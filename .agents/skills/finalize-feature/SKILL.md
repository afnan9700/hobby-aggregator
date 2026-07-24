---
name: "finalize-feature"
description: "Update design docs, app-state, and session-log after implementation"
metadata:
  version: "1.0.0"
  author: "your-name"
---

# Finalize Feature Skill

## When to Use
Use this after a feature has been implemented and reviewed, when the code is done and the descriptive documentation needs to catch up.

## Input
- Implemented feature files
- `spec.md`
- `plan.md`
- The implementation review result

## Context to Load
1. Existing feature design docs, especially `design.md`
2. Relevant colocated `DESIGN.md` files
3. `app-state.md`
4. `architecture.md` if specified by the user.
5. Relevant template files from `.agents/_templates/` for reference.

## Process
1. Read the implementation result.
2. Create or update the feature-level `design.md` so it describes the feature as implemented. Follow the format specified in `.agents/_templates/_feature-template/design-index.md`. 
3. Create or update colocated `DESIGN.md` files for any submodules whose responsibilities or invariants changed. Follow the format specified in `.agents/_templates/design-colocated.md`. 
4. Update `app-state.md` to reflect the new project state. Follow the format specified in `.agents/_templates/_app-templates/app-state.md`.
5. Append a summary entry to `agent-log.md`. Follow the format specified in `.agents/_templates/agent-log-entry.md`.
6. Present the documentation changes for human review.

## Rules
- This skill updates documentation; it does not redesign the feature.
- Keep design docs descriptive, not procedural.
- Only add colocated design docs where they are genuinely useful or if they are specified by the suer.
- Do not create unnecessary doc files just to satisfy a checklist.

## Output
- Updated `design.md`
- Updated colocated `DESIGN.md` files, if needed
- Updated `app-state.md`
- Updated `agent-log.md`
