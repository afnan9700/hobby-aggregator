# Feature Folder Guidance

## Default Rule
Place each feature folder inside the app that owns the work.

Examples:
- `apps/web/features/001-login/`
- `apps/api/features/002-document-sync/`

## When to Use a Root Feature Folder
Use a root-level `features/NNN-feature-name/` folder only when:
- the repository is not organized as a monorepo, or
- the feature truly spans several apps and has no single owner.

## Suggested Contents
- `spec.md`
- `plan.md`
- `design.md`

## Colocated Design Docs
Create colocated `DESIGN.md` files only for submodules or components that have enough complexity to justify a persistent description.
