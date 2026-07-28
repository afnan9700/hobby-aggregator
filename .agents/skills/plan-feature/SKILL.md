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
5. Codebase files named in `spec.md`.
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
- Split tests into their own step.
- Prefer to write a few tests that cover the most important cases rather than many tests that cover every edge case.
- Prefer integration tests over unit tests unless the unit tests are critical to the feature.
- Trust the spec and do not explore the codebase beyonf the files mentioned in the spec.
- Explore the codebase further only if the files from spec really do not provide enough information.
- Do not rely on memory for code locations when a quick lookup is needed.
- Split oversized features before implementation if the plan becomes too large.

## Output
- `apps/<app>/features/NNN-feature-name/plan.md`
