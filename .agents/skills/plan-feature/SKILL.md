---
name: plan-feature
description: Generate a step-by-step implementation plan from a spec
metadata:
  version: 1.0.0
---

# Plan Feature Skill

## When to Use
Use this after a spec is approved and you need a practical implementation plan.

## Input
- `spec.md`

## Context to Load
1. `apps/<app>/features/NNN-feature-name/spec.md`
2. Relevant convention docs in `.agents/docs/conventions/`
3. `app-state.md` to understand the current project state.
4. Files named in `spec.md`.
5. Plan template in `.agents/_templates/_feature-templates/plan.md`

### Process
1. Read the spec and identify the files it references.
2. Inspect only those files. Explore additional code only if the referenced files do not provide enough information to produce an implementation plan.
3. Break the feature into small, independently verifiable implementation steps. Split oversized features into smaller phases before planning if needed.
4. For each implementation step, specify:
   * files to create or modify,
   * implementation details,
   * verification criteria (optional),
5. Add a dedicated testing step, prioritizing a small set of high-value integration tests. Include unit tests only where they provide unique value.
6. Identify implementation risks, assumptions, open questions, or external dependencies.
7. Present the final plan using the format defined in `.agents/_templates/_feature-templates/plan.md`. The plan should be self-contained and detailed enough that the spec is not needed during implementation.

### Rules
* Keep each implementation step atomic.
* Prefer high-value integration tests over exhaustive or low-value unit tests.
* Do not inspect unrelated parts of the codebase unless required to complete the plan.

## Output
- `apps/<app>/features/NNN-feature-name/plan.md`
