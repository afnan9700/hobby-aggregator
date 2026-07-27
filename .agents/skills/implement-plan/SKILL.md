---
name: "implement-plan"
description: "Execute one or more steps from an implementation plan"
metadata:
  version: "1.0.0"
---

# Implement Plan Skill

## When to Use
Use this when a plan is approved and you need to execute it step by step.

## Input
- `plan.md`
- The step number(s) to implement

## Context to Load
1. The relevant step(s) from `plan.md`
2. Relevant app-level `AGENTS.md` files
3. Relevant convention docs in `.agents/docs/conventions/`
4. Codebase files named in the plan or required by direct dependencies

## Process
1. Read the planned step(s).
2. Implement only the requested step(s).
3. Keep changes aligned with the plan.
4. Verify the result immediately after implementation.
5. Record ambiguous decisions for review if needed.
6. Present the modified files and the notes for human review.

## Rules
- Stay within the plan unless a necessary adjustment is discovered.
- If the plan is incomplete, note the gap rather than silently expanding scope.
- If a command fails because of sandbox, permission, or environment limits, do not retry in a loop. Stop, explain the blocker briefly, and ask the user how to proceed.
- Verify before proceeding to the next step.

## Output
- Modified files
- Notes on ambiguous decisions, if any
