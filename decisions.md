# Memory Diary App: Decision Log

This document records the key architectural, technical, and UI/UX design decisions made during the development of Memory MVP.

## Architecture & Foundational Setup

* **Architecture Pattern:** MVVM (Model-View-ViewModel). Selected as the standard, robust architecture for Android development that neatly separates UI from business logic.
* **UI Framework:** Jetpack Compose. Selected over XML for a modern, declarative approach to building Android interfaces rapidly.
* **Local Persistence:** Room Database. Used for storing memories, utilizing entities and DAOs for clean SQLite abstraction.
* **Dependency Injection:** Bypassed Hilt for MVP to increase development speed and reduce boilerplate. Instantiated ViewModels directly using custom `ViewModelProvider.Factory` combined with a shared `AppDatabase` singleton.
* **Navigation:** Jetpack Navigation Compose. Single-activity architecture using a `NavHost` to route between `DiaryScreen`, `CaptureScreen`, and `IndexScreen`. 

## Development Strategy & Scope

* **Minimum Viable Product (MVP) focus:** 
  * Prioritized core functional loops—writing, adding photo/audio, saving, and viewing over complex features like Speech-to-Text.
  * Speech-to-Text (`Task 23`) was explicitly skipped for MVP and deferred to future updates.
* **Agile implementation:** Followed a strict, incremental task list defined in `task.md`, validating each step before moving on.

## Technical Decisions & Issue Resolutions

* **Photo Storage Permanence:** 
  * *Issue:* The Android Photo Picker returns temporary `content://` URIs that break upon app restart.
  * *Resolution:* Built an `ImageStorage` utility to instantly copy selected image streams into the app’s persistent private `filesDir/photos/` directory before saving the path to Room.
* **Audio Recording & Playback:**
  * *Issue 1:* Initial recordings were saving to `cacheDir` making them vulnerable to unexpected OS cleanup.
  * *Resolution:* Shifted audio memos to save securely in `filesDir/audio_memos/` with unique timestamped filenames to prevent overwriting.
  * *Issue 2:* Exogenous playback default behavior. `MediaPlayer` defaults to the device Earpiece/Voice Call stream, causing silent playback on emulators without headphones.
  * *Resolution:* Explicitly injected `AudioAttributes` targeting `USAGE_MEDIA` and `CONTENT_TYPE_MUSIC` to forcefully route audio to external speakers.

## "Warm Paper Scrapbook" Design Engineering

Executing the specific soft, journal-like aesthetics defined in `design.md` required overriding default Material 3 (M3) behaviors.

* **Absolute Theme Enforcement:**
  * Android 12+ forces "Dynamic Colors" based on the user's wallpaper. We intentionally disabled `dynamicColor` inside `Theme.kt` to lock the palette to our custom `#FDF9F1` Warm Paper aesthetic across all devices.
  * Mapped all 15 M3 typography tokens explicitly to Custom Fonts (SF Pro and Playwrite) to prevent the system from substituting Roboto into unmapped elements like Dialog text or secondary labels.
* **Font Sourcing constraints:** 
  * *Issue:* Google Fonts 'Playwrite' repository structure caused HTML files to download instead of raw TTF binaries, breaking Jetpack Compose's font loader. 
  * *Resolution:* Swapped Playwrite for **Indie Flower** (another verified Google Font with an organic, journal cursive aesthetic) via direct binary download to restore the visual design without blocking development.
* **Apple-Style Soft Shadows:** 
  * Standard Material 3 `FloatingActionButtonDefaults.elevation()` yields harsh, distinctly "Android" dropshadows. 
  * Set all Material elevations to `0.dp`. Developed a custom, reusable `.appleShadow()` Compose Modifier using the underlying `Canvas.drawRoundRect` and `Paint.setShadowLayer` APIs to accurately replicate the wide, highly-blurred, low-opacity (7% alpha) dropshadows prescribed by the design spec.
* **Spacious Layouts:** Purposely elevated default Compose paddings (from `16.dp` up to `24.dp`) inside `MemoryCard` and `DiaryScreen` list arrangements to emulate the modern, airy, spacing style often found in premium iOS applications. 

## UX Handling

* **Unsaved Changes Guard:** Implemented a robust `isDirty` comparator checking current editor state against original database loads. Intercepted both software (✕ Button) and hardware (Gesture/Back Arrow) edge navigations via Android's `BackHandler` API to trigger an `AlertDialog` confirming destructive exit intent.
* **Contextual Edit vs View:** Opted for a "Long Press" modal workflow inside the index feed for triggering edits/deletions rather than cluttering the clean UI cards with static edit/trash icons.
