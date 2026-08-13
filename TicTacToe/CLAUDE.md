# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

# Architecture
- Clean Architecture, four layers: domain, application (use cases), 
  interface adapters, infrastructure.
- Dependencies point inward only. domain has zero framework imports.
- Package by layer: com.tictactoe.domain, com.tictactoe.application, com.tictactoe.adapters, com.tictactoe.infra
- Build: no build tool — plain `javac`/`java`, Java 17. JUnit 5 (console-standalone) and AssertJ
  are vendored as jars in `lib/` (no dependency manager).
- Tests: JUnit 5 + AssertJ. Run all: `./test.sh`. Run one class: `./test.sh --select-class com.tictactoe.<pkg>.<ClassName>`.
- Run the app: `./run.sh`

# Order
- For every new piece: write the failing test first. Do not write 
  implementation until I've confirmed the test fails for the right reason.
- Build inside-out: domain model + unit tests → use case + tests → 
  adapter + integration test → wire into entry point.
