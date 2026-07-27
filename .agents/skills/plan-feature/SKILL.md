---
name: "plan-feature"
description: "Generate a step-by-step implementation plan from a spec"
metadata:
  version: "1.0.0"
---

# Plan Feature Skill

## When to Use
Use this after a spec is approved and you need a practical implementation plan.

## Input
- `spec.md`

## Context to Load
1. `apps/<app>/features/NNN-feature-name/spec.md`
2. `apps/<app>/app-state.md`
3. Relevant app-level `AGENTS.md` files
4. Relevant convention docs in `.agents/docs/conventions/`
5. Related feature specs and design docs when the feature depends on them
6. Codebase files as needed to identify the correct files and boundaries
7. Plan template in `.agents/_templates/_feature-templates/plan.md`

## Process
1. Read the spec and the current project state.
2. Inspect the relevant codebase area enough to locate the affected files.
3. Group work into small, verifiable steps.
4. For each step, identify files to create or modify, implementation details, and verification. 
5. Include test work as explicit steps or a dedicated testing section.
6. Identify any places where implementation risk is high or dependencies are unclear.
7. Present the plan in the format specified in `.agents/_templates/_feature-templates/plan.md` for human review. The plan should have enough details that referring to spec again is not required. 

## Rules
- Keep steps atomic.
- Be explicit about exact files and boundaries.
- Do not rely on memory for code locations when a quick lookup is needed.
- Split oversized features before implementation if the plan becomes too large.

## Output
- `apps/<app>/features/NNN-feature-name/plan.md`
