# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew installDebug           # Build and install on connected device/emulator
./gradlew clean                  # Clean build artifacts
./gradlew test                   # Run unit tests
./gradlew connectedAndroidTest   # Run instrumented tests (requires device/emulator)
./gradlew testDebugUnitTest      # Run debug unit tests only
```

No linting tools (ktlint, Detekt) are configured. Kotlin official style is enforced via `gradle.properties`.

## Architecture

MVVM with a unidirectional data flow:

```
UI (Screens.kt) → ViewModels → MemoryRepository → MemoryDao → Room DB
```

**Key architectural decisions:**
- No Hilt — DI is done via custom `ViewModelProvider.Factory` instantiated in `AppNavigation.kt`, which is also where the database singleton and repository are created.
- Single-activity app (`MainActivity`). Navigation is entirely in `AppNavigation.kt` (NavHost with three routes: `diary`, `capture/{memoryId}/{action}`, `index`).
- All UI lives in one large file: `ui/Screens.kt` (~1700 lines). It contains `DiaryScreen`, `CaptureScreen`, `IndexScreen`, `BentoMemoryCard`, and card layout helpers.
- `DiaryScreen` uses a persistent bottom sheet overlay for quick capture — it does not navigate to `CaptureScreen` for new entries on the home screen.
- Emotion detection (`util/EmotionDetector.kt`) is purely keyword-based, returning one of: `HAPPY`, `SAD`, `ANXIOUS`, `CALM`, `EXCITED`, `NEUTRAL`.

## Data Model

`Memory` (Room entity, `data/model/Memory.kt`):
- `id`, `timestamp`, `updatedAt` — auto-managed
- `title`, `textContent` — text content
- `audioFilePath` — path to `.m4a` in internal storage
- `waveformData` — serialized `List<Float>` as JSON string (added in DB migration v1→v2)
- `photoFilePath` — path copied to internal storage via `util/ImageStorage.kt`
- `emotionalTone` — string matching `EmotionDetector` output

Room DB is version 2. The migration adds the `waveformData` column; it lives in `AppDatabase.kt`.

## Design System

Defined in `design.md` and implemented across `ui/theme/`:
- **Fonts**: Trocchi (display/headings), SF Pro Rounded (body)
- **Aesthetic**: warm paper/scrapbook feel — avoid stark whites and flat colours
- **Emotion colours** (`ui/theme/Color.kt`) drive card tinting, waveform colour, and text-hero gradients throughout `Screens.kt`. Changes to emotion colours must be consistent across all three `emotionColor` `when` blocks in `Screens.kt` (lines ~355, ~1386, ~1675) plus the gradient block (~1607).
- **Shadow helper**: `appleShadow()` modifier defined in `Screens.kt` — use it instead of raw `Modifier.shadow()` for the app's signature soft-shadow look.
- Dark mode is intentionally disabled for the MVP (`forcedScheme = ColorScheme.Light` in `Theme.kt`).

## Project Documentation

- `design.md` — micro design system (colours, typography, radii, elevation, glass effects)
- `product.md` — product spec and feature requirements
- `task.md` — phased task list (~49 tasks across 9 phases)
- `decisions.md` — architecture decision records
- `progress_report.md` — completed work log and current state
