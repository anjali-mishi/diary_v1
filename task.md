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

- [ ] **Task 28: Audio Playback in Edit.** Use unique filenames for voice memos so they don't overwrite, and enable playback on the edit screen.
- [ ] **Task 29: Persistent Photo Storage.** Copy selected photos to internal app storage rather than storing temporary URIs.
- [ ] **Task 30: Unsaved Changes Dialog.** Intercept back/close actions to confirm discarding unsaved edits.
- [ ] **Task 31: Absolute Theme Enforcement.** Map all Material 3 typography tokens to custom fonts and replace standard M3 elevations with custom soft shadows to perfectly match `design.md`.

> **CRITICAL RULE FOR ALL FUTURE DEVELOPMENT:** Do not assume implementation details. If a dependency, UI layout, or technical approach is ambiguous, you MUST ask the user for clarity before executing code.
