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

- [x] **Task 41: Waveform Recording Visualizer — Bottom-Aligned & Thin Bars.**
  - Adjust the animated waveform from Task 39 so bars are **bottom-aligned** (grow upward from a fixed baseline) rather than center-aligned (current implementation grows from center).
  - Each bar must be exactly **4dp wide** (thin lines), with equal gaps between them.
  - Keep all other waveform behavior (amplitude polling, orange→pink gradient, fade-out on stop) unchanged.

  ### Task 41 Sub-tasks: Spotify-style Full-Screen Recording Mode

  - [x] **Task 41a: Full-Screen Recording Overlay — Hide Normal UI.**
    - When `isRecording == true`, replace the entire normal CaptureScreen layout with a dedicated full-screen recording view.
    - Elements to hide: title "Add a memory", text input field, suggestion chips (starters + predictive), photo FAB, save button, audio preview row.
    - The recording view is a `Column` split into two sections: top 60% and bottom 40%.

  - [x] **Task 41b: Recording Top Bar — "● Recording" Indicator + Timer.**
    - Top row: X button on the left (stops recording, saves audio), red dot + "Recording" text centered.
    - Below, vertically centered in the top 60% area: large bold elapsed timer in `M:SS` format (e.g. `0:04`), using `displayLarge` typography.
    - Timer tracks seconds since recording started via `System.currentTimeMillis()` polled in the existing amplitude `LaunchedEffect`.

  - [x] **Task 41c: Smooth Filled Wave Shape — Replace Thin Bars.**
    - Replace the current 4dp rectangular bar waveform with a smooth filled wave shape (Spotify-style).
    - Wave occupies the full height of the bottom 40% `Box`. Drawn with a `Canvas` using a `Path` with `cubicTo` bezier curves through 7 amplitude control points.
    - Wave top edge animates fluidly: each frame lerps current heights toward random targets at factor 0.18; targets refresh every ~5 frames (~500ms) for slow organic undulation. Poll interval: 100ms.
    - Fill: subtle app gradient `#FF9966 → #FF6699` at 15% alpha (`0x26` prefix).

  - [x] **Task 41d: Red Stop Button at Bottom Center.**
    - A large 64dp red (`#E53935`) circular FAB with X icon, centered at the bottom of the wave area with 32dp bottom padding.
    - "Stop" label (`bodySmall`) below the button.
    - Tapping calls `stopRecording()`, sets `isRecording = false`, returns to normal CaptureScreen with audio preview.

- [x] **Task 42: Save Audio as Waveform Data.**
  - While recording, continuously sample `AudioRecorder.maxAmplitude()` (100ms poll) and accumulate normalised amplitude values into a `mutableStateListOf<Float>()`.
  - When `stopRecording()` is called, the list is JSON-encoded as `"[f0,f1,…]"` and passed to `saveMemory()`.
  - Schema change: added `waveformData: String?` column to `Memory` entity; `AppDatabase` bumped to version 2 with `MIGRATION_1_2` (`ALTER TABLE memories ADD COLUMN waveformData TEXT`).
  - `CaptureViewModel.saveMemory()` now accepts a `waveformData: String?` parameter and stores it on the `Memory` object.

- [x] **Task 43: Waveform Playback on Saved Audio.**
  - When a saved memory's audio is played, the stored waveform is decoded and rendered as 4dp vertical bars (orange→pink gradient, bottom-aligned) with a white playhead.
  - Playhead position is driven by `AudioPlayer.currentPosition / duration` polled at 100ms via `LaunchedEffect`; resets to 0 when playback stops or completes.
  - `AudioPlayer` exposes `currentPosition: Int` and `duration: Int` getters.
  - Implemented in both **DiaryScreen** memory card and **CaptureScreen** audio preview row.
  - Memories without `waveformData` (old entries) continue showing the existing "Voice Memo" text fallback.
  - Play/pause icon updated: shows `Pause` icon while playing instead of `Close`.

- [x] **Task 44: Bottom Sheet — Placeholder Alignment & Rounded Top Corners.**
  - Placeholder text *"What's on your mind?"* is **left-aligned** (text-start) and **vertically centered** within the sheet height using a `Box` with `contentAlignment = Alignment.CenterStart`.
  - Rounded top corners were already present (`RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)` via `.clip()`), so no shape change was needed.

- [x] **Task 45: Gradient as App Primary Color — "Save memory" Button & Global Update.**
  - Brand gradient `#FF9966 → #FF6699` (horizontal) applied as background fill via `Brush.horizontalGradient` + `containerColor = Transparent` on the "Save memory" `Button` (white text).
  - Same gradient applied to Mic FAB and Photo FAB (`CircleShape` background, transparent container, white icon).
  - Gradient strip (Task 35) and waveform bars (Task 39/41) already used matching values — confirmed, no change needed.
  - `design.md` already documented the canonical brand gradient — no change needed.

- [x] **Task 46: Memory Card — Remove Playwrite Font from Dates.**
  - Memory card date/timestamp changed from `Playwrite Österreich` to `MaterialTheme.typography.bodySmall` (the app's legible sans-serif).
  - No other card typography changes.

- [x] **Task 47: Replace Playwrite with Trocchi Font App-Wide.**
  - `trocchi_regular.ttf` added to `res/font/` (Google Fonts, OFL licensed — downloaded from google/fonts GitHub).
  - `playwriteFamily` → `trocchiFamily` in `Type.kt`; font file → `R.font.trocchi_regular`. All display/headline/titleLarge styles updated.
  - Inline `playwriteFamily` reference in `Screens.kt` (date group header in `IndexScreen`) updated to `trocchiFamily`.
  - `design.md` already documented Trocchi as Primary Font — no change needed.

- [x] **Task 48: Memory Entry Text — Center-Aligned in CaptureScreen.**
  - `textAlign = TextAlign.Center` added to `BasicTextField`'s `textStyle` in `CaptureScreen`.
  - Placeholder `"I remember..."` `Text` also receives `textAlign = Center` + `fillMaxWidth()` for visual consistency before the user starts typing.
  - No other screens affected.

- [x] **Task 49: Suggestion & Predictor Chips — White Fill with Soft Shadow.**
  - Both starter chips (Task 37) and predictor chips (Task 38) updated:
    - `containerColor = Color.White` via `SuggestionChipDefaults.suggestionChipColors`.
    - `border = null` — no outline.
    - `elevation = SuggestionChipDefaults.suggestionChipElevation(0.dp)` — M3 elevation zeroed.
    - `Modifier.appleShadow(4.dp)` — Apple-style soft diffuse shadow for depth.
  - Typography and tap behavior unchanged.

---

## Phase 13: Bento Grid Card Layout

### Grid Definition

```
Screen width = full bleed (e.g. 390dp on a standard device)
Columns      = 2
Column width = (screenWidth - horizontalPadding - gutter) / 2
               e.g. (390 - 16 - 8) / 2 ≈ 183dp per column
Gutter       = 8dp (between columns and between rows)
Outer margin = 8dp left + 8dp right

Span types:
  full  = 2 columns wide  (≈ 366dp)
  half  = 1 column wide   (≈ 183dp, square aspect ratio)
```

**Card type → span mapping (fixed, content-type driven):**

| Content type              | Condition                             | Span   | Approx size        |
|---------------------------|---------------------------------------|--------|--------------------|
| Media card                | `photoUri != null`                    | full   | 366 × auto dp      |
| Audio card                | `audioUri != null && photoUri == null`| full   | 366 × auto dp      |
| Text-only card            | no photo, no audio                    | half   | 183 × 183 dp       |

**Internal stacking order (all card types):**

```
┌─────────────────────────┐  ← full-span card
│  MEDIA or AUDIO BLOCK   │  top: photo (180dp) or colour block (120dp)
├─────────────────────────┤
│  text  •  date  •  tag  │  bottom: content row (16dp padding)
└─────────────────────────┘

┌───────────┐              ← half-span card (text-only)
│ gradient  │
│ HEADLINE  │  bold first line
│ body…     │  remaining text
│ date • tag│
└───────────┘
```

- [x] **Task 50: Bento Grid Infrastructure — Variable Spans by Content Type.**
  - Replace the current list/grid layout in `IndexScreen` with a 2-column bento grid.
  - Use `LazyVerticalStaggeredGrid` (Compose) or a custom `LazyVerticalGrid` with `GridItemSpan` logic.
  - Span calculation: inspect each `MemoryEntity` — if `photoUri != null` → span 2; if `audioUri != null && photoUri == null` → span 2; text-only → span 1.
  - Grid gutters: `8.dp` horizontal, `8.dp` vertical, consistent with existing card padding.
  - Cards retain their existing `appleShadow`, corner radius, and white fill — only their grid slot changes.
  - No changes to `CaptureScreen` or data layer.

- [ ] **Task 51: Media-First Card Layout — Photo on Top, Content Below.**
  - For cards where `photoUri != null`:
    - Top section: `AsyncImage` (Coil) fills the full card width at a fixed height of `180.dp`, `contentScale = Crop`, clipped to the card's top corners.
    - Bottom section: memory text (capped at 2 lines, ellipsized), date chip, and emotion tag sit below the image in a `Column` with `16.dp` padding.
  - If both photo and audio exist, photo takes the top slot; a small inline audio pill (mic icon + duration) appears inside the bottom content section.
  - No layout changes to text-only or audio-only cards in this task.

- [ ] **Task 52: Audio-First Card Layout — Album-Art Block on Top, Content Below.**
  - For cards where `audioUri != null && photoUri == null`:
    - Top section: a solid colour block, `height = 120.dp`, full card width, using the memory's emotional tone colour (from `EmotionTone` palette). Centred inside: a circular white play/pause `IconButton` (`48.dp`) + audio duration label beneath it in `Caption` style.
    - Bottom section: memory text (capped at 2 lines), date chip, and emotion tag — same structure as Task 51's bottom section.
  - Tapping the play button triggers audio playback inline (reuse existing audio player logic); tapping anywhere else on the card opens the memory.
  - The colour block uses the same `EmotionTone` → `Color` mapping already established in the design system.

- [ ] **Task 53: Text-Hero Card Layout — Full-Bleed Gradient with Bold Headline.**
  - For cards where `photoUri == null && audioUri == null`:
    - Card background: full-bleed `Brush.linearGradient` using the memory's emotional tone colour (light→slightly-darker tint, 2-stop), replacing the plain white fill.
    - First sentence/line of the memory text is extracted and rendered as a **headline** (`TitleMedium` or `HeadlineSmall`, bold, max 2 lines) at the top of the card.
    - Remaining text, if any, renders below in `BodySmall` (max 3 lines, ellipsized).
    - Date chip and emotion tag sit at the bottom of the card, tinted to contrast against the gradient (use `onSurface` or a darkened tone colour).
    - Card retains `appleShadow` and `1-col` square span from Task 50.

---

## Phase 14: Memory Detail Layer

### Interaction Model

```
IndexScreen (bento grid)
  │
  ├─ TAP card ──────────────────────▶ MemoryDetailScreen (read-only)
  │                                       │
  │                                       ├─ TAP edit button (top-right) ──▶ CaptureScreen (edit mode)
  │                                       │
  │                                       └─ dismiss (back / swipe-down)  ──▶ IndexScreen
  │
  └─ SWIPE LEFT on card ────────────▶ CaptureScreen (edit mode, same memory)
```

### Detail Layer Visual Spec

```
┌─────────────────────────────────────────┐
│  ← (back)               [edit ✎]  top-right
├─────────────────────────────────────────┤
│                                         │
│   PARALLAX MEDIA HERO                   │  photo → AsyncImage, contentScale=Crop
│   (shrinks as user scrolls up)          │  audio → sentiment colour block + play btn
│                                         │
├╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌┤  ← content scrolls up over hero
│  Full memory text (uncapped)            │
│  Date  •  Emotion tag                   │
│  Audio player pill (if audio present)   │
│                                         │
│  [background: subtle sentiment→appBg    │
│   gradient behind the content area]     │
└─────────────────────────────────────────┘

Text-only memory:
┌─────────────────────────────────────────┐
│  ← (back)               [edit ✎]        │
│                                         │
│  Full memory text rendered in large     │  HeadlineMedium / DisplaySmall
│  type whose colour is a vertical        │
│  gradient: sentiment colour at bottom   │
│  bleeding upward into black at top.     │
│  (Brush.linearGradient as TextStyle     │
│   brush, start=bottom, end=top)         │
│                                         │
│  Date  •  Emotion tag                   │
│                                         │
│  [full-screen sentiment→appBg gradient  │
│   background, same as other types]      │
└─────────────────────────────────────────┘
```

### Background Gradient Rule

- Always 3-stop vertical gradient: `[sentimentColor.copy(alpha=0.18f), sentimentColor.copy(alpha=0.06f), appBackground]`
- Occupies the full screen behind all content.
- `sentimentColor` = the `EmotionTone` → `Color` mapping already in the design system.

---

- [ ] **Task 54: DetailScreen Scaffold + Navigation Wiring.**
  - Create `MemoryDetailScreen(memoryId: Long)` composable in a new file.
  - Register it as a destination in `NavHost` with route `"memory/{memoryId}"`.
  - In `IndexScreen`, replace the existing card `onClick` lambda with `navController.navigate("memory/${memory.id}")`.
  - Add a `CaptureScreen` navigation route for edit mode: `"capture/{memoryId}"` — reuses existing `CaptureScreen` but pre-populates fields from the given memory entity.
  - `MemoryDetailScreen` has a top app bar with a back arrow (left) and an edit `IconButton` (pencil icon, top-right); tapping edit navigates to `"capture/{memoryId}"`.
  - No UI chrome beyond the app bar in this task — content area left as a placeholder `Box`.

- [ ] **Task 55: Card-to-Fullscreen Expand Transition.**
  - Wrap `NavHost` in `SharedTransitionLayout` (Compose `1.7+` shared-element API).
  - Apply `Modifier.sharedElement(...)` to the card container in `IndexScreen` and the root container in `MemoryDetailScreen`, keyed on `"card-${memory.id}"`.
  - Transition spec: `spring(stiffness = Spring.StiffnessMediumLow)` for bounds, `tween(300, easing = FastOutSlowInEasing)` for fade — approximates ease-in/ease-out feel.
  - If `SharedTransitionLayout` is unavailable for the project's Compose version, fall back to `AnimatedContent` with a `slideInVertically + fadeIn` / `slideOutVertically + fadeOut` pair using the same easing.
  - Ensure the card's corner radius animates from its resting value (`12.dp`) to `0.dp` as the card expands to fill the screen.

- [ ] **Task 56: Parallax Media Hero (Photo & Audio Cards).**
  - In `MemoryDetailScreen`, implement a `NestedScrollConnection` that translates the hero image/block at **0.5× the scroll velocity** (parallax factor), clamped so the hero never scrolls fully off-screen until the content column reaches its natural top.
  - **Photo card:** `AsyncImage` (Coil), `fillMaxWidth`, initial height `300.dp`, `contentScale = Crop`. Clips to a `RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)`.
  - **Audio card:** Sentiment colour block, initial height `220.dp`, centred play/pause `IconButton` (`56.dp`) + duration label. Reuse audio player logic from Task 52. Block clips to same bottom-rounded shape.
  - Content column (text, date, emotion, audio pill) sits in a `LazyColumn` beneath the hero, with `16.dp` horizontal padding and `20.dp` top padding inside the first item.
  - Memory text is rendered **uncapped** (no `maxLines` limit) in `BodyLarge`.

- [ ] **Task 57: Sentiment Gradient Background + Typography Hero (Text-Only Cards).**
  - **Background (all card types):** Draw a full-screen `Box` with `background(Brush.verticalGradient([sentimentColor.copy(0.18f), sentimentColor.copy(0.06f), appBackground]))` as the lowest layer behind the scroll content.
  - **Text-only hero:** No parallax block. Instead, render the full memory text in `DisplaySmall` (or `HeadlineLarge`) style using `TextStyle(brush = Brush.linearGradient(colors = listOf(Color.Black, sentimentColor), start = Offset(0f, 0f), end = Offset(0f, Float.POSITIVE_INFINITY)))` — black at the top, sentiment colour at the bottom.
  - Text sits in the upper portion of the screen with generous vertical padding (`top = 72.dp` to clear the app bar, `horizontal = 24.dp`).
  - Date chip and emotion tag appear below the text body with `top = 24.dp` spacing.
  - No parallax scroll connection needed for text-only; standard `LazyColumn` or `Column` with `verticalScroll`.

- [ ] **Task 58: Swipe-Left on Card → CaptureScreen (Edit Mode).**
  - In `IndexScreen`, wrap each card with a `SwipeToDismiss` (or custom `Modifier.pointerInput` horizontal drag detector) that detects a leftward swipe exceeding a threshold of `72.dp`.
  - On confirmed left swipe: navigate to `"capture/${memory.id}"` (edit mode).
  - Visual feedback during swipe: reveal a tinted edit-icon background behind the card (sentiment colour at `alpha = 0.15f`) that scales in as the card slides left; snap back with spring animation if the threshold is not met.
  - Swipe-right on the same card: no action (guard against accidental triggers).
