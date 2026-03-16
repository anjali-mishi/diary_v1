# Memory App MVP: Step-by-Step Task List

This is your roadmap to building the Memory App, designed specifically for you as a Product Designer to iterate on UI quickly while establishing a solid technical foundation. We will tackle these one step at a time.

## Phase 1: Environment & Project Foundation (Current)

- [x] **Task 1: Verify Installation.** Confirm Android Studio is installed and updated.
- [x] **Task 2: Verify Emulator.** Ensure your virtual device (e.g., Pixel 8) is running and accessible.
- [x] **Task 3: Initialize Project.** Create a new Android project:
    - Template: "Empty Activity" (This gives us a clean slate).
    - Language: Kotlin.
    - UI Toolkit: Jetpack Compose.
- [x] **Task 4: Understand Build Files.** Briefly review `build.gradle.kts` files to understand where we'll add external libraries later.
- [x] **Task 5: Initial Run.** Run the empty "Hello World" app on the emulator to confirm everything is connected.

## Phase 2: Core Architecture & Navigation Strategy

- [x] **Task 6: Define Folder Structure.** Set up the main folders (e.g., `ui`, `data`, `domain`) for organization.
- [x] **Task 7: Introduce Navigation.** Understand Jetpack Navigation Compose.
- [x] **Task 8: Create Placeholder Screens.** Build empty Composable functions for:
    - `DiaryScreen` (The main chronological view).
    - `CaptureScreen` (Where users add memories).
    - `IndexScreen` (The date-based jump list).
- [x] **Task 9: Wire Navigation.** Connect the placeholders so we can move between views.

## Phase 3: The "Capture" Experience (UI First)

- [x] **Task 10: Input UI.** Design the main text input area for the `CaptureScreen`.
- [x] **Task 11: Action Buttons.** Add placeholders for the Voice, Audio, and Photo action buttons.
- [x] **Task 12: Basic State Management.** Handle the temporary state of typed text holding it on the screen before saving.
- [x] **Task 13: Apply Aesthetics.** Apply initial color palettes, typography, and the "warm/cozy" visual style.

## Phase 4: Local Data Persistence (The Database)

- [x] **Task 14: Introduce Room.** Understand Room, our local database solution.
- [x] **Task 15: Create the Entity.** Translate the `Memory` data model from `product.md` into a Room Entity.
- [x] **Task 16: Define the DAO.** Create the Data Access Object (methods to insert, read, update, delete).
- [x] **Task 17: Connect Data to UI.** Wire up the "Save" button on the `CaptureScreen` to store the text in Room.

## Phase 5: The "Diary View" Experience

- [x] **Task 18: Build the List UI.** Use `LazyColumn` for an efficient scrolling list of memories.
- [x] **Task 19: Design the Memory Card.** Create the individual memory display component (text, media placeholders, emotional color).
- [x] **Task 20: Bind Data to List.** Connect the Room database to the list UI so new memories appear automatically.

## Phase 6: Advanced Capabilities (Iterating on MVP)

- [x] **Task 21: Handle Photo Storage.** Implement gallery/camera selection and saving the *path* to the database.
- [x] **Task 22: Audio Recording.** Integrate the MediaRecorder API to attach audio files.
  - *UI Refinement:* Consolidated bottom action bar from 3 buttons to 2. Mic button now handles recording (start/stop). Redundant PlayArrow record button removed.
- [~] **Task 23: Speech-to-Text.** *(Skipped for MVP — Mic button repurposed for audio recording. Speech-to-text can be added in a future iteration using Android SpeechRecognizer API.)*
- [x] **Task 24: Emotional Tone Detection.** Build the simple keyword matching logic to assign colors automatically.

## Phase 7: Polish and Remaining Navigation

- [x] **Task 25: Implement Index View.** Build the jump-list functionality for `IndexScreen`. UI label renamed to "My Diaries" per product.md.
- [x] **Task 26: Edit/Delete Logic.** Hook up long-press actions to modify existing entries.
- [x] **Task 27: Final Polish.** Review end-to-end flow and visual consistency against the product specifications.

## Phase 8: Bug Fixes & UX Polish

- [x] **Task 28: Audio Playback in Edit.** Use unique filenames for voice memos so they don't overwrite, and enable playback on the edit screen.
- [x] **Task 29: Persistent Photo Storage.** Copy selected photos to internal app storage rather than storing temporary URIs.
- [x] **Task 30: Unsaved Changes Dialog.** Intercept back/close actions to confirm discarding unsaved edits.
- [x] **Task 31: Absolute Theme Enforcement.** Map all Material 3 typography tokens to custom fonts and replace standard M3 elevations with custom soft shadows to perfectly match `design.md`.

## Phase 9: Capture Entry Point — Persistent Bottom Sheet

- [x] **Task 32: Replace FAB with Persistent Bottom Sheet.**
  - Removed the existing black `+` FAB button entirely.
  - Added a persistent bottom sheet occupying the bottom **15%** of the screen over `DiaryScreen`.
  - Sheet is always visible, never dismissible — primary entry point for creating a memory.

- [x] **Task 33: Bottom Sheet Collapsed State UI.**
  - The collapsed sheet displays:
    - Text prompt *"What's on your mind?"* left-aligned.
    - Mic icon and Photo icon grouped on the right, side by side.
  - Each zone has its own independent tap target (see Task 33a).

- [x] **Task 33a: Smart Sheet Tap Zones.**
  - Tapping the text prompt opens `CaptureScreen` with keyboard auto-focused.
  - Tapping the Mic icon opens `CaptureScreen` and immediately starts audio recording (or prompts for permission).
  - Tapping the Image icon opens `CaptureScreen` and immediately launches the photo picker.
  - Action is passed as a nav argument (`action=text|voice|image`) and handled via `LaunchedEffect` in `CaptureScreen`.

- [x] **Task 34: Bottom-to-Top Capture Screen Animation.**
  - `CaptureScreen` slides in from bottom to top (upward motion) covering 100% of the screen.
  - Use Compose `AnimatedVisibility` or `ModalBottomSheet` with a custom enter transition for the slide-up effect.

- [x] **Task 35: Soft Gradient Above the Sheet.**
  - Render a 30px tall gradient strip immediately above the sheet (between the diary list and the sheet edge).
  - Gradient colors: soft orange → pink, fully transparent at the top edge, opaque at the sheet edge.
  - This gradient does NOT blur the diary content — the 80% of screen above it remains 100% visible and unaffected.
  - Implement as a `Box` with `Brush.verticalGradient` overlaid at the bottom of the diary area.

> **CRITICAL RULE FOR ALL FUTURE DEVELOPMENT:** Do not assume implementation details. If a dependency, UI layout, or technical approach is ambiguous, you MUST ask the user for clarity before executing code.

## Phase 10: CaptureScreen UI Enhancements

- [x] **Task 36: Screen Title.**
  - Add a title **"Add a memory"** at the top of `CaptureScreen`, below the close-button row and above the text input.
  - Typography: `MaterialTheme.typography.headlineMedium`, muted/secondary color so it doesn't compete with user input.
  - Reference: top bar area in `Screens.kt:492–524`.

- [x] **Task 37: Quick Starter Suggestion Chips (Empty State).**
  - When `textContent` is blank, show a horizontal scrollable row of pre-written prompt chips to help the user get started.
  - Chip examples: *"Today I felt…"*, *"Something I'm grateful for…"*, *"A moment I want to remember…"*, *"I was surprised by…"*
  - Tapping a chip inserts its text into `textContent` and hides the chip row.
  - Chip row disappears automatically once `textContent.isNotBlank()`.
  - Implement as a `LazyRow` of `SuggestionChip` (Material 3). No backend or ML — static list only.
  - **UI Update:** Chips are bottom-aligned, positioned directly above the mic/photo FAB row (i.e., right above the keypad), not below the title.
  - **Refinement:** Removed ellipses (`…`) from all starter chip labels. Tapping a chip now places the cursor at the end of the inserted text.

- [x] **Task 38: Inline Auto-Suggestions (Predictive Text Chips).**
  - As the user types, show 2–3 short predictive continuation chips in a strip **above the keyboard area** (between the text field and the mic/photo FABs).
  - Suggestions are **rule-based only** — no ML or external API:
    - Match the last few words of `textContent` against a small curated map of diary sentence starters → likely continuations.
    - Example: "Today I felt" → suggests *"happy"*, *"overwhelmed"*, *"at peace"*.
    - Example: "I want to remember" → suggests *"this moment"*, *"how it felt"*, *"every detail"*.
  - Tapping a chip appends the suggestion to `textContent`.
  - If no pattern matches, render an empty row (no visible strip).
  - Implement as a `Row` of `FilterChip` or plain styled `Text` buttons.
  - **Refinement:** Tapping a suggestion chip places the cursor at the end of the resulting text (not the beginning).

- [x] **Task 39: Waveform Audio Recording Visualizer.**
  - Replace the current "red dot + Recording Voice Memo…" text indicator (`Screens.kt:529–542`) with an animated waveform bar visualizer.
  - Waveform: a row of vertical bars that animate (height varies) while `isRecording == true`.
  - Bar heights: driven by `MediaRecorder.maxAmplitude` if wired in `AudioRecorder.kt`; otherwise simulated with random variation on a ~60ms timer using `LaunchedEffect`.
  - Bar colors: gradient from orange → pink, matching the sheet gradient from Task 35.
  - When recording stops, bars freeze briefly then fade out.
  - Implement using `Canvas` composable with `drawRect` for bars.
  - Does not change the audio preview row (play/stop) shown after recording ends.

- [x] **Task 40: Floating Bottom-Aligned "Save memory" CTA.**
  - Move the Save action from the top-right `TextButton` (`Screens.kt:506–520`) to a **full-width floating button at the bottom of the screen**.
  - Button design:
    - Full-width with 24dp horizontal padding.
    - Filled `Button` (Material 3 primary color) with label **"Save memory"**.
    - Soft apple-style shadow matching existing FAB shadows in the codebase.
    - Positioned above the mic/photo FAB row using a `Box` with `Alignment.BottomCenter` or `Scaffold` `bottomBar`.
  - Visibility: same conditional as current Save button — only shown when any content exists (`textContent.isNotBlank() || selectedPhotoUri != null || recordedAudioUri != null`).
  - Remove the existing `TextButton` Save from the top bar; replace its slot with `Spacer` so the Close button remains left-aligned.
  - Save logic in `CaptureViewModel.kt:36–78` remains unchanged.

- [ ] **Task 41: Waveform Recording Visualizer — Bottom-Aligned & Thin Bars.**
  - Adjust the animated waveform from Task 39 so bars are **bottom-aligned** (grow upward from a fixed baseline) rather than center-aligned (current implementation grows from center).
  - Each bar must be exactly **4dp wide** (thin lines), with equal gaps between them.
  - Keep all other waveform behavior (amplitude polling, orange→pink gradient, fade-out on stop) unchanged.

- [ ] **Task 42: Save Audio as Waveform Data.**
  - While recording, continuously sample `AudioRecorder.maxAmplitude()` (same 60ms poll used by the visualizer) and accumulate the normalized amplitude values into a `List<Float>`.
  - When `stopRecording()` is called, persist this list alongside the audio file path in the `Memory` Room entity.
  - Schema change: add a `waveformData: String` column (JSON-encoded `List<Float>`) to the `Memory` entity and update the DAO/database migration.
  - The `CaptureViewModel.saveMemory()` call must accept and store the waveform data.

- [ ] **Task 43: Waveform Playback on Saved Audio.**
  - When a saved memory's audio is played (play/stop button in `DiaryScreen` or `EditScreen`), render the stored waveform instead of a static icon.
  - Waveform display: same bar style as Task 41 (4dp wide, bottom-aligned, orange→pink gradient).
  - Animate a playhead (a vertical line or highlight) that advances across the bars in sync with audio playback progress (use `MediaPlayer.currentPosition / duration` polled at ~100ms).
  - When playback stops or completes, reset the playhead to the start.
  - Does not affect memories that have no waveform data (e.g., old entries) — those continue showing the existing play button UI.

- [ ] **Task 44: Bottom Sheet — Centered Placeholder & Rounded Top Corners.**
  - Center-align the placeholder text *"What's on your mind?"* inside the persistent bottom sheet (currently left-aligned).
  - Apply **rounded top-left and top-right corners** (e.g., `16dp` radius) to the sheet's surface/background shape; bottom corners remain square (sheet is flush with screen bottom).
  - No other sheet layout or behavior changes.
