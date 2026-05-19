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
- All UI lives in one large file: `ui/Screens.kt` (~1900 lines). It contains `DiaryScreen`, `CaptureScreen`, `IndexScreen`, `BentoMemoryCard`, `IndexMemoryRow`, and card layout helpers.
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

Room DB is version 3. Migrations: v1→v2 adds `waveformData`, v2→v3 adds bookmarks/stickers/letters columns. Lives in `data/database/AppDatabase.kt`.

## Design System

Defined in `design.md` and implemented across `ui/theme/`:
- **Fonts**: Trocchi (display/headings), SF Pro Rounded (body)
- **Primary color**: `Color(0xE0000000)` — black at 88% opacity. Used for all CTAs, icon tints, primary text.
- **Aesthetic**: warm paper/scrapbook feel — avoid stark whites and flat colours
- **Emotion colours** (`ui/theme/Color.kt`) drive card tinting, waveform colour, and text-hero gradients. The `emotionColor()` helper in `ui/Shared.kt` maps emotion strings to colours — keep consistent across all usage sites.
- **Shadow helper**: `appleShadow()` modifier defined in `ui/Shared.kt` — use it instead of raw `Modifier.shadow()` for the app's signature soft-shadow look.
- Dark mode is intentionally disabled for the MVP (`forcedScheme = ColorScheme.Light` in `Theme.kt`).

## Project Documentation

- `design.md` — micro design system (colours, typography, radii, elevation, glass effects)
- `product.md` — product spec and feature requirements
- `task.md` — phased task list (Phases 1–14 + A + publish)
- `launch_checklist.md` — pre-launch verification checklist with prioritized action items
- `decisions.md` — architecture decision records
- `progress_report.md` — completed work log and current state
