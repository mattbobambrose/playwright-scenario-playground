---
scenario_dir: scenarios
test_dir: src/test/kotlin/com/mattbobambrose/examples/scenarios
test_language: kotlin
test_framework: kotest-stringspec
---

# Playwright Scenarios — Project Config

Edit the YAML frontmatter to reconfigure, or run `/playwright-scenarios-config` to re-prompt.

## Config profiles

Kotlin (current):
```yaml
scenario_dir: scenarios
test_dir: src/test/kotlin/com/mattbobambrose/examples/scenarios
test_language: kotlin
test_framework: kotest-stringspec
```

TypeScript (switch to this when working on the TS implementation):
```yaml
scenario_dir: scenarios
test_dir: typescript/tests/scenarios
test_language: typescript
test_framework: playwright-test
```
