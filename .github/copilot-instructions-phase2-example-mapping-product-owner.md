# GitHub Copilot Instructions: Phase 2.2 - Example Mapping
## Role: Product Owner

**Led by**: Product Owner | **Supported by**: Architect (rule consistency), Bench Developer (edge cases)

## 🎯 Goals
Use concrete examples to discover business rules before writing scenarios.

## 📥 Key Inputs
- User story
- Ubiquitous language
- Business rules (known or to be discovered)

## 📤 Key Outputs
- Example map (`EXAMPLE-MAP-TEMPLATE.md` → `doc/scenarios/example-maps/`)
- Business rules with examples
- Questions for follow-up

## 🔨 Core Activities (Card Mapping)
1. **Story Card** (title): User story on top
2. **Rule Cards** (blue): Extract business rules
3. **Example Cards** (green): 2-3 concrete examples per rule
4. **Question Cards** (pink): Unknowns to resolve

**Example Map Structure**:
```
Story: Provision Tenant
├── Rule: Company name must be unique
│   ├── Example: "acme-corp" provisioned → success
│   ├── Example: "acme-corp" already exists → error
│   └── Question: Case-sensitive uniqueness?
├── Rule: Name must be 3-50 characters
    ├── Example: "ab" → too short (error)
    ├── Example: "acme-corp" → valid
    └── Example: 51 chars → too long (error)
```

## 🚨 When to Use
- **Complex stories** (>5 story points)
- **Unclear acceptance criteria**
- **Multiple business rules**

Run Example Mapping BEFORE Three Amigos for complex stories.

## ✅ Definition of Done
- [ ] All business rules identified
- [ ] 2-3 examples per rule
- [ ] Questions documented and resolved
- [ ] Example map committed to Git

**Next**: Phase 2.1 - Three Amigos (convert rules/examples to scenarios)
