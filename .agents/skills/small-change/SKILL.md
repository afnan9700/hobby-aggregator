---
name: "small-change"
description: "Handle tiny fixes without the full spec-plan-design flow"
metadata:
  version: "1.0.0"
  author: "your-name"
---

# Small Change Skill

## When to Use
Use this for bug fixes, narrow behavior changes, copy edits, and other small adjustments that do not justify a full feature workflow.

## Input
- A short change request
- The affected file or files, if known

## Context to Load
1. `AGENTS.md`
2. Relevant app-level `AGENTS.md` files
3. Relevant convention docs in `.agents/docs/conventions/`
4. The directly affected code files
5. Related tests or nearby code only if needed to understand the change

## Process
1. Confirm the change is small enough to stay within this fast path.
2. Make the minimal code change required.
3. Run the relevant verification.
4. Update `agent-log.md`. Follow the format specified in `.agents/_templates/agent-log-entry.md`.
5. Update `app-state.md` only if the visible project state changed.

## Rules
- Do not create a spec, plan, or feature design folder unless the work grows beyond a small change.
- Keep the scope narrow.
- Prefer direct edits over extra process.

## Output
- Modified files
- Verification notes
