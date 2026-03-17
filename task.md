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

## Phase 9: Premium UI — Instagram & Spotify Mechanics

- [ ] **Task 32: Instagram-Style Full-Screen Memory Detail View.**
  - Create a new `DetailScreen` composable that opens when a memory card is tapped (instead of jumping directly to edit mode).
  - Layout: photo or animated gradient fills ~70% of screen height at top; text content scrolls below.
  - For memories without a photo: show an animated blurred gradient background using the emotional tone color.
  - An Edit button in the top-right corner navigates to the existing `CaptureScreen` in edit mode.
  - ⚠️ **Requires user clarity before execution:** How should the back gesture behave — swipe down (Instagram-style) or standard back button? Should the gradient animation be subtle parallax or a slow color pulse?

- [ ] **Task 33: Spotify-Style Persistent Audio Player.**
  - Build a globally-visible floating bottom bar that appears over the Scaffold (above the FAB) whenever an audio memo is actively playing.
  - Must include: Play/Pause button, audio title/source memory name, a scrubber bar showing progress, and elapsed/total time.
  - Background: Glassmorphism effect — semi-translucent (`~85% opacity`) surface with Warm Paper tint, matching `design.md`.
  - Player persists and follows the user even if they navigate between `DiaryScreen` and `IndexScreen`.
  - ⚠️ **Requires user clarity before execution:** Should tapping the bar expand it into a full Spotify-style "Now Playing" sheet, or just control play/pause inline?

> **CRITICAL RULE FOR ALL FUTURE DEVELOPMENT:** Do not assume implementation details. If a dependency, UI layout, or technical approach is ambiguous, you MUST ask the user for clarity before executing code.

---

# Memory App v2: Post-MVP Feature Tasks

> Full feature spec lives in `features_v2.md`. All decisions documented there.
> Phases below continue task numbering from Phase 9.

---

## Phase 10: AI Emotional Foundation
*Prerequisite for phases 11–16. Upgrade emotion detection to Claude API and add mood filtering.*

- [ ] **Task 34: Claude API Infrastructure.**
  - Add `INTERNET` permission to `AndroidManifest.xml`.
  - Add API key to `local.properties` as `CLAUDE_API_KEY=...` and expose via `BuildConfig`.
  - Add OkHttp dependency to `build.gradle.kts`.
  - Create `ClaudeApiService.kt` — single suspend function that POSTs to the Messages API and returns the raw response.

- [ ] **Task 35: Upgrade Emotion Detection.**
  - Create `ClaudeEmotionResult` data class: `{ primaryTone, intensity: Float, secondaryTone? }`.
  - Update `EmotionDetector.kt` to try the Claude API first (async, non-blocking) and fall back to keyword matching if offline or API fails.
  - Prompt to use: *"Classify the emotional tone of this diary entry. Return JSON: {primary: one of HAPPY/SAD/ANXIOUS/CALM/EXCITED/NEUTRAL, intensity: 0.0–1.0, secondary: optional same enum or null}. Entry: [text]"*
  - Use `claude-haiku-4-5-20251001` — never send photo paths or audio paths, text only.

- [ ] **Task 36: Room Migration 1 → 2.**
  - Add `emotionIntensity: Float = 0.5f` and `secondaryEmotionalTone: String? = null` to `Memory` entity.
  - Write `Migration(1, 2)` — two `ALTER TABLE` statements. Never use `fallbackToDestructiveMigration()`.
  - Update `AppDatabase` version to 2.

- [ ] **Task 37: Reflect Intensity in Card UI.**
  - On `MemoryCard`, vary the emotion color bar's opacity/height based on `emotionIntensity` (e.g. 0.3 intensity = thin bar, 1.0 = bold bar).
  - Show `secondaryEmotionalTone` as a small secondary color dot beside the primary bar if present.

- [ ] **Task 38: Emotion Filter Chips on DiaryScreen.**
  - Add a horizontally scrollable chip row just below the "Memories" header: **All · Happy · Calm · Excited · Sad · Anxious**.
  - Active chip is filled; others are outlined. Animated slide-down reveal on first render.
  - Filter state lives in `DiaryViewModel` and resets to "All" on app relaunch.
  - Tapping an emotion dot in `IndexScreen` navigates to `DiaryScreen` with that filter pre-selected.

- [ ] **Task 39: DAO Queries for Filtering and Sorting.**
  - Add to `MemoryDao.kt`:
    ```kotlin
    fun getMemoriesByTone(tone: String): Flow<List<Memory>>
    fun getMemoriesByToneSortedByIntensity(tone: String): Flow<List<Memory>>
    fun getAllMemoriesSortedByIntensity(): Flow<List<Memory>>
    ```
  - Update `DiaryViewModel` to expose `activeFilter: StateFlow<String?>` and `sortOrder: StateFlow<SortOrder>`, switching the active query accordingly.
  - Add a sort menu (3-dot or long-press header): Newest first / Oldest first / Most intense.

---

## Phase 11: Bookmarks
*Quick win — pure UI plus a small DB change. No API needed.*

- [ ] **Task 40: Room Migration 2 → 3.**
  - Add `isBookmarked: Boolean = false` and `bookmarkedAt: Long? = null` to `Memory` entity.
  - Write `Migration(2, 3)` with two `ALTER TABLE` statements.

- [ ] **Task 41: Bookmark Toggle.**
  - Add **"Bookmark"** / **"Remove Bookmark"** to the existing long-press context menu on `MemoryCard` (alongside Edit and Delete).
  - Add a small gold star/ribbon overlay to the top-right corner of bookmarked `MemoryCard`s.
  - Add `toggleBookmark(memory: Memory)` to `DiaryViewModel` and `MemoryRepository`.

- [ ] **Task 42: Favourites Section in IndexScreen.**
  - Add a pinned "Favourites" section at the top of `IndexScreen` listing bookmarked memories.
  - Show the section only when at least one bookmark exists; hide it otherwise.
  - Tapping a favourite navigates to that memory (same flow as existing index rows).

---

## Phase 12: Diary Collections with Custom Covers
*Signature feature. New entities, background job, new screen, cover editor.*

- [ ] **Task 43: New Entities and Room Migration 3 → 4.**
  - Add `DiaryCollection` and `CollectionMemoryCrossRef` entities as defined in `features_v2.md`.
  - Add corresponding DAOs: `DiaryCollectionDao` — insert, delete, `getAllCollections(): Flow`, `getMemoriesForCollection(id)`.
  - Write `Migration(3, 4)` creating two new tables. Update `AppDatabase` version to 4.

- [ ] **Task 44: Auto-Grouping WorkManager Job.**
  - Create `CollectionBuilderWorker` — periodic work, runs weekly.
  - Logic: scan all memories, group by date proximity (≤3 days apart), filter groups where ≥60% of memories are HAPPY, EXCITED, or CALM.
  - Minimum group size: 1 memory (solo meaningful days qualify). Create a `DiaryCollection` for each qualifying group.
  - De-duplicate: skip groups that already have a matching collection by date range.

- [ ] **Task 45: Claude API Collection Title Generation.**
  - Within `CollectionBuilderWorker`, for each new collection call Claude API:
    - Prompt: *"Given these diary entry titles and dates, suggest a short evocative name (3–5 words) for this chapter of someone's life. Return only the name, no punctuation."*
  - Fallback title if API unavailable: `"[Month] [Year]"` derived from date range.

- [ ] **Task 46: CollectionsScreen — Bookshelf Grid.**
  - New screen `CollectionsScreen` navigable from a "Collections" button on `DiaryScreen` header.
  - Lazy grid (2 columns) of collection cover cards — title, subtitle (date range), cover photo or color.
  - Tapping a collection opens a filtered memory list (reuses `DiaryScreen` with a collection filter).
  - Empty state: "Your first collection will appear once you have a few happy memories."

- [ ] **Task 47: Cover Editor Composable.**
  - Full-screen sheet accessible from a collection's overflow menu: "Edit Cover".
  - Options: **Photo** (pick from collection's memories), **Color** (6 swatches from design system), **Emoji + title card**.
  - Preview updates live as the user changes options. Save persists to `DiaryCollection.coverPhotoPath / coverColor / coverEmoji`.

- [ ] **Task 48: Wire Collections into Navigation.**
  - Add `CollectionsScreen` to `AppNavigation`.
  - Add "Collections" entry point to `DiaryScreen` header or as a new bottom nav item.
  - ⚠️ **Requires user clarity before execution:** Header link vs. bottom nav — ask before implementing.

---

## Phase 13: Letter to Future Self
*Special entry type. Sealed until a chosen date, revealed by WorkManager notification.*

- [ ] **Task 49: Room Migration 4 → 5.**
  - Add `entryType: String = "MEMORY"`, `sealedUntil: Long? = null`, `isRevealed: Boolean = false` to `Memory` entity.
  - Write `Migration(4, 5)`.

- [ ] **Task 50: Letter Capture UI.**
  - New composable `LetterCaptureScreen` — cream background, handwriting-style font, subtle ruled-line texture.
  - Entry point: second FAB option on `DiaryScreen` (or long-press main FAB) — "Write a letter to future you".
  - Includes a `DatePickerDialog` — minimum date must be 1 month from today.
  - On save: `entryType = "LETTER"`, `sealedUntil = chosen date`, `isRevealed = false`.

- [ ] **Task 51: Sealed Envelope Card.**
  - In `DiaryScreen`, detect `entryType == "LETTER" && !isRevealed` and render a `SealedLetterCard` instead of the standard `MemoryCard`.
  - Shows: a wax seal illustration, "Sealed until [date]", no title or content preview.
  - Tapping a sealed card shows a toast/snack: "This letter opens on [date]."

- [ ] **Task 52: Reveal WorkManager Job + Notification.**
  - On letter save, schedule a one-time `LetterRevealWorker` to run at `sealedUntil` timestamp.
  - Worker sets `isRevealed = true` in the DB and fires a notification: *"A letter from past you just arrived."*
  - Tapping the notification deep-links to the revealed letter in the diary.

- [ ] **Task 53: Wax Seal Reveal Animation.**
  - When `isRevealed` flips to true (first open after reveal date), animate the letter card: wax seal cracks and fades out, envelope unfolds, content fades in.
  - Use Compose `AnimatedContent` + a Canvas-drawn seal with a crack path animation.
  - Letters are automatically added to a `DiaryCollection` of type `LETTER` titled "Letters to Myself".

---

## Phase 14: Opening Ritual — Calm Delight
*Five rotating delights on every app open. Skippable. Backed by a settings toggle.*

- [ ] **Task 54: Opening Ritual Host Screen.**
  - Create `OpeningRitualScreen` composable — full-screen, warm background, no navigation bar.
  - Set as the launch destination in `AppNavigation` (before `DiaryScreen`).
  - Skip mechanic: any tap anywhere advances to `DiaryScreen` immediately. Auto-advance after 8 seconds.
  - Random delight selection: weighted random pick from the 5 options (degrade gracefully — see Task 58/59 conditions).
  - Add **Settings screen** (gear icon on `DiaryScreen` header) with a single toggle: "Opening moment — On/Off". Default: On. Store in `SharedPreferences`.

- [ ] **Task 55: Box Breathing Delight.**
  - `BoxBreathingComposable` — a soft circle expands/contracts on a 4-4-4-4 second cycle.
  - Text label transitions: *"Breathe in… Hold… Breathe out… Hold…"*
  - Compose `Canvas` + `InfiniteTransition` for the circle. Coroutine timer for label changes.

- [ ] **Task 56: Sakura Petals Delight.**
  - `SakuraPetalsComposable` — 15–20 petal `Image` composables with randomised `animateFloat` offsets for drift (x wobble, y fall, slight rotation).
  - Use a simple petal SVG asset. Stagger launch times so they don't all start together.
  - Soft pink-white gradient background.

- [ ] **Task 57: Floating Horse Delight.**
  - `FloatingHorseComposable` — a minimal line-drawn horse SVG path is drawn stroke-by-stroke using `PathEffect.dashPathEffect` with an animated phase, then floats upward and fades out via `animateFloat`.
  - Define the horse as a single `Path` object (simplified silhouette, ~20 points).
  - Background: warm off-white, no distractions.

- [ ] **Task 58: Letter from the Past Delight.**
  - `LetterFromPastComposable` — fetch a random memory from 6–12 months ago at screen init.
  - Display it as a letter card (cream card, handwriting font, date in corner, text preview).
  - Only available if: user has ≥10 memories AND at least one memory exists in the 6–12 month window.
  - Fallback to Sakura or Breathing if condition not met.

- [ ] **Task 59: Happy Memory Delight.**
  - `HappyMemoryComposable` — fetch a random HAPPY or EXCITED memory with a photo from >30 days ago.
  - Crossfade the photo in over 1.5s, overlay the memory title in soft text.
  - Only available if: user has ≥10 memories AND a qualifying HAPPY/EXCITED memory with photo exists.
  - Fallback to Sakura or Breathing if condition not met.

---

## Phase 15: Mood Stickers
*Illustrated sticker overlays on memory cards. Assets-first feature.*

- [ ] **Task 60: Source Sticker Assets.**
  - Prepare ~8–10 Lottie JSON sticker files per emotion tone (HAPPY, SAD, ANXIOUS, CALM, EXCITED + NEUTRAL) = ~50–60 total.
  - Name convention: `sticker_happy_01.json`, `sticker_calm_03.json`, etc.
  - Place in `res/raw/`. Each sticker loops gently (2–3s loop, low energy animation).

- [ ] **Task 61: Room Migration 5 → 6.**
  - Add `stickers: String? = null` to `Memory` entity.
  - Write `Migration(5, 6)`. Sticker JSON format: `[{"id":"sticker_happy_01","x":0.72,"y":0.18}]`.

- [ ] **Task 62: Sticker Tray Composable.**
  - `StickerTrayBottomSheet` — slides up 2 seconds after a memory is saved (alongside or after the quote sheet from Phase 16).
  - Horizontal scroll of sticker thumbnails grouped by the memory's detected emotion tone.
  - User can place up to 3 stickers. Tap a sticker → it appears on the card preview at a default position; drag to reposition (track x%/y% relative to card bounds).
  - "Done" saves positions to `Memory.stickers` via `CaptureViewModel`.

- [ ] **Task 63: Sticker Overlay on MemoryCard.**
  - In `MemoryCard`, deserialize `stickers` JSON and render each as a `LottieAnimation` composable positioned using `Box` with fractional offsets.
  - Stickers render at 40dp size. Loop animation plays continuously when the card is visible.

---

## Phase 16: Emotional Companion
*Quotes, nudges, and song sync. Best experienced once the diary has meaningful history.*

- [ ] **Task 64: Generate and Bundle Quotes JSON.**
  - Use Claude API (during development, one-time run) to generate `quotes_v1.json` in `assets/`:
    - Prompt: *"Generate 30 famous, widely-loved quotes for the emotion [TONE] from world-renowned authors, poets, and films. Sources: Rumi, Maya Angelou, Tolkien, Murakami, Toni Morrison, Tagore, Coelho, classic films. Return as JSON array: [{text, source}]. No motivational-poster clichés."*
    - Run for HAPPY, SAD, ANXIOUS, CALM, EXCITED — 150 quotes total.
  - Create `QuoteRepository.kt` — loads JSON from assets, returns a random quote for a given tone.

- [ ] **Task 65: Quote Bottom Sheet.**
  - `QuoteBottomSheet` composable — slides up 2 seconds after saving a memory (only when `emotionalTone != NEUTRAL`).
  - Shows: quote text (Playwrite font, large), source/author (secondary), a bookmark icon to save it.
  - Dismisses on swipe down or tap outside.
  - If Claude API is available and the local pool has been seen (track shown quote IDs in `SharedPreferences`), fall back to a fresh API-generated quote.

- [ ] **Task 66: Saved Quotes Feature.**
  - Create `SavedQuote` Room entity: `{ id, text, source, emotionalTone, savedAt }`.
  - Add `SavedQuoteDao` with insert + `getAllSavedQuotes(): Flow`.
  - Add a "Saved Quotes" section to `IndexScreen` or as a tab in the future `SettingsScreen`.
  - Room Migration 6 → 7.

- [ ] **Task 67: Happy Memory Nudge.**
  - In `CaptureViewModel.saveMemory()`, after saving: if `emotionalTone == SAD || ANXIOUS`, query a random HAPPY or CALM memory with `timestamp < now - 30 days`.
  - Delay 4 seconds (coroutine), then show `HappyNudgeBottomSheet` — "A memory from back then…" with photo thumbnail + title + "Open memory" CTA.
  - Guard: store `lastNudgeSessionId` in `SharedPreferences`; skip if already nudged in this app session.
  - Do NOT nudge for `entryType == LETTER`.

- [ ] **Task 68: Song Sync (Spotify).**
  - Add **language preference** to `SettingsScreen`: English / Hindi / Tamil / Other (free text). Store in `SharedPreferences`.
  - Add a music note `IconButton` to `MemoryCard` overflow or detail view.
  - On tap: call Claude API — *"This diary entry has a [TONE] emotional tone. Suggest one song (title + artist) in [LANGUAGE] that fits this mood. Return JSON: {title, artist, spotifySearchQuery}."*
  - Launch `Intent(ACTION_VIEW, "https://open.spotify.com/search/${spotifySearchQuery}")`.
  - Fallback: if Spotify not installed, open in browser. Show a loading indicator while API call is in-flight.
