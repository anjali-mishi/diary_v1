# Memory App MVP: Progress & Strategy Report

> [!NOTE]
> **Purpose:** This is a living content document — written to support storytelling, retrospectives, and creator content (posts, threads, case studies) about how this app was built. Update it after each meaningful sprint so there's always a ready-to-publish snapshot of the journey.
>
> It also provides Claude with a comprehensive overview of the development journey: our core strategy, completed milestones, and immediate next steps toward a premium user experience.

## 1. Our Process & Collaborative Strategy

Our execution strategy has been rooted in disciplined, document-driven development. Rather than rushing blindly into code, we established clear architectural and product goals upfront, serving as our "North Star".

*   **Product Definition First (`product.md`):** We strictly defined the project scope to focus on a local-first, privacy-respecting photo and emotional journaling experience. By explicitly listing "anti-goals" (no social features, no cloud sync, no AI prompt generation), we successfully prevented feature creep.
*   **Design as the Foundation (`design.md`):** As a Product Designer, ensuring a "warm, cozy, and handmade scrapbook" aesthetic was paramount. We defined our typographic choices (e.g., Playwrite Österreich), cohesive soft color palettes, and Apple-style "liquid glass" elevations before touching the UI layer.
*   **Structured Phased Execution (`task.md`):** The technical lifecycle was broken down into 9 distinct phases. We decoupled visual layouts from backend logic. For instance, we built the UI input forms before wiring up the local Room Database, allowing for early visual validation. 
*   **Rigorous Iteration & Polish:** Rather than calling the core features "done", we dedicated an entire phase (Phase 8) specifically for ensuring robust edge-case handling (unsaved changes dialogs, persistent photo storage, audio playback continuity, and absolute theme enforcement).

---

## 2. All That We've Done (Completed Milestones)

We have successfully completed **Phases 1 through 9**, fully realizing the functional MVP and the premium entry-point experience.

### 🏗 Foundation & Architecture (Phases 1-2)
*   Initialized a clean Android Studio project using **Kotlin** and **Jetpack Compose**.
*   Established a scalable directory structure (`ui`, `data`, `domain`).
*   Configured Jetpack Navigation Compose, bridging placeholder screens for the `DiaryScreen`, `CaptureScreen`, and `IndexScreen`.

### 🎨 The "Capture" & "Diary" Experience (Phases 3-5)
*   Built the primary input interfaces for composing text, attaching photos, and recording voice notes.
*   Integrated the **Room Database**, converting our `Memory` data models into persistent local SQLite tables.
*   Wrote Database Access Objects (DAOs) and successfully wired the UI interactions to save, read, and load records.
*   Implemented an efficient, infinitely scrolling `LazyColumn` for the main feed, designing dynamic Memory Cards that automatically adjust based on the content available (e.g., hiding image placeholders if no photo exists).

### 🚀 Advanced Capabilities (Phase 6)
*   **Rich Media Handling:** Successfully implemented gallery/camera photo selection mapping real device URI paths to our database.
*   **Audio Integration:** Connected the native `MediaRecorder` API to capture and securely save audio voice notes internally.
*   **Emotional UI:** Shipped a localized keyword-matching algorithm that reads the user's text on save, determines their "Emotional Tone", and assigns a representative color to the memory card.

### 🧹 Polish & Edge-Cases (Phases 7-8)
*   Finalized the "My Diaries" index view, allowing users to jump directly to specific dates.
*   Implemented complete Edit, Update, and Delete lifecycles for past memories.
*   Built a custom BackHandler dialog to warn users before discarding unsaved edits.
*   Refactored temporary URI media storage into persistent internal app storage to ensure images don't break over time.
*   Overrode default Material 3 tokens to strictly adhere to our custom `design.md` typography and soft-shadow aesthetics.

### ✨ Premium Entry Point (Phase 9)
*   **Persistent Bottom Sheet:** Replaced the FAB with a persistent sheet anchored at the bottom 15% of `DiaryScreen` — always visible, never dismissible.
*   **Smart Tap Zones:** Each zone on the sheet initiates a distinct flow — tapping the text prompt auto-focuses the keyboard, tapping the mic auto-starts recording, tapping the image icon auto-opens the photo picker. Action is passed through the nav route and handled via `LaunchedEffect` in `CaptureScreen`.
*   **Bottom-to-Top Slide Animation:** `CaptureScreen` slides in from the bottom using Compose Navigation's `enterTransition`/`exitTransition` for a native sheet-like feel.
*   **Gradient Strip:** A 30dp soft orange→pink gradient overlaid immediately above the sheet for a polished visual boundary.
*   **Structured Logging:** Added `Diary.*` Logcat tags across all layers (Navigation, ViewModels, Repository, Database, AudioPlayer, AudioRecorder, EmotionDetector, ImageStorage) for full observability during debugging.

---

### 🎙 Waveform Recording & Playback (Phase 11 — Tasks 41–43)

*   **Spotify-Style Full-Screen Recording Mode (Task 41a–d):** When recording begins, the entire `CaptureScreen` is replaced by a full-screen recording overlay split 60/40. The top 60% shows a "● Recording" indicator row and a large bold `M:SS` elapsed timer. The bottom 40% hosts a smooth filled bezier wave (7 control points, lerp-animated at factor 0.18 toward targets that refresh every ~500ms) in the brand gradient at 15% alpha. A 64dp red circular FAB at the bottom stops recording.
*   **Waveform Data Persistence (Task 42):** Amplitude samples are accumulated in a `mutableStateListOf<Float>` during the 100ms poll loop. On stop, they are JSON-encoded as `"[f0,f1,…]"` and saved to a new `waveformData: String?` column on the `Memory` Room entity (schema v2, migration provided).
*   **Waveform Playback with Animated Playhead (Task 43):** Both the DiaryScreen memory card and the CaptureScreen audio preview now render the stored waveform as 4dp bottom-aligned bars (orange→pink gradient) when `waveformData` is available. A white vertical playhead advances across the bars via `AudioPlayer.currentPosition / duration` polled at 100ms. Old entries without waveform data fall back to the "Voice Memo" text row. The play/stop icon now correctly shows `Pause` while audio is playing.

---

### 🎨 Visual Polish & Branding (Phase 12 — Tasks 44–48)

*   **Bottom Sheet Placeholder (Task 44):** Placeholder text "What's on your mind?" is left-aligned (text-start) and vertically centred within the sheet using `Box(contentAlignment = Alignment.CenterStart)`. Rounded top corners were already in place.
*   **Brand Gradient App-Wide (Task 45):** `#FF9966 → #FF6699` horizontal gradient is now the fill for all primary interactive surfaces. Applied to the "Save memory" button and Mic/Photo FABs via `Modifier.background(Brush.horizontalGradient(...))` with `containerColor = Transparent` and white icons. Gradient strip and waveform bars already matched — confirmed.
*   **Memory Card Date Font (Task 46):** Card timestamps now render in `MaterialTheme.typography.bodySmall` (SF Pro Rounded) instead of Trocchi/Playwrite.
*   **Trocchi Font (Task 47):** `trocchi_regular.ttf` (OFL, Google Fonts) bundled in `res/font/`. `playwriteFamily` fully removed; `trocchiFamily` now covers all display/headline/titleLarge tokens in `Type.kt`. Inline reference in `IndexScreen` date headers also updated.
*   **Center-Aligned Entry Text (Task 48):** `BasicTextField` in `CaptureScreen` uses `textAlign = TextAlign.Center` in its `textStyle`. Placeholder `"I remember..."` matches. No other screens affected.

---

---

### 🪟 Memory Detail Screen Polish (Post Phase 14)

*   **Floating Nav Button Restyle:** Back and edit buttons on `MemoryDetailScreen` upgraded from translucent dark circles to white circles with `appleShadow()` drop shadow and near-black (`#1C1C1E`) icons — consistent with the app's premium light-surface aesthetic.
*   **Text-Only Content Offset:** The text hero `LazyColumn` on text-only memories now receives `statusBarsPadding() + padding(top = 56.dp)`, pushing body text cleanly below the floating nav buttons instead of rendering behind them.
*   **Shared Transition Abrupt Fade Fix:** Diagnosed and eliminated a recurring abrupt cut at the end of the card→detail close animation caused by four compounding issues: (1) spring settling time (~630ms) too close to the nav scope deadline (700ms), (2) asymmetric exit specs across both sharedBounds sides, (3) overlapping AnimatedVisibilityScope state when the user re-opened a card before the 700ms scope expired, (4) gradient background Box disappearing instantly on frame 1 of every close. Fixed by replacing all springs with deterministic tweens (400ms fade / 500ms bounds, `FastOutSlowInEasing`), tightening the DiaryScreen `popEnterTransition` scope to 600ms, and adding `popExitTransition = fadeOut(tween(400))` to the Detail route so the gradient fades gracefully instead of snapping.

---

---

### 🎛 IndexScreen — Sentiment Dial Redesign (Post Phase 14)

The `IndexScreen` was fully redesigned from a bento/timeline grid into a **sentiment-driven memory browser** with three custom components:

#### DialKnob (custom Canvas component)
*   Skeuomorphic radio-dial rendered entirely in `DrawScope` — no images, no XML.
*   Barrel + center pill geometry: pill height = 76% barrel height, pill width = `wH × 2.12`.
*   Multi-layer shadow simulation (5 dark layers bottom-offset, 5 light layers top-offset) since `DrawScope` has no `BlurMaskFilter`.
*   Metallic rim as a filled ring with 5-stop gradient, cut by barrel clip.
*   Horizontal ribs: sine-mask + ellipse-mask fade, `ribMaxAlpha = 0.085`, 1px stroke.
*   Center pill: semi-transparent vertical gradient (`surfaceDn → backgroundColor → surfaceUp`), inset bottom shadow, `neuDark @ 0.18α` border, white highlight line.
*   Sentiment carousel inside pill: clipped to pill, Trocchi Italic 16sp focal / 11sp side, alpha `1 − dist × 0.58`.
*   `indexDialValue: Float` hoisted to `DiaryViewModel` so dial position survives navigation and config changes.

#### PolaroidPillCard (new composable)
*   82dp tall row with a 58×68dp polaroid thumbnail (2dp radius, `appleShadow`) + text column.
*   Fixed per-card decorative tilt via `remember(index)` — deterministic, not state-driven.
*   All cards equal visual weight — no focal dimming or alpha animation.
*   Fallback (no photo): Option C — date as art (`"MMM\nd"`) in Trocchi Bold 18sp, emotion color on 25% washed background.
*   Primary text: nunitoFamily, 16sp Regular, 1 line ellipsis, `onBackground` color.
*   Date: `labelSmall`, `#8E8A86` (design secondary).
*   Shadow: `appleShadow(cornerRadius = 2.dp)` — aligns with soft-shadow design language.

#### DotRailTimeline (new composable)
*   Canvas-drawn horizontal dot rail scrubber.
*   Rail always visible (even when 0 memories match sentiment — shows empty rail without dots).
*   Focal dot: 5dp radius, full `#2C2A29`; others: 3dp, 28% alpha.
*   `detectDragGestures` drives `carouselFractIdx` → `LaunchedEffect` scrolls `LazyColumn` to focal item.

#### IndexScreen layout
*   Sentiment gradient: full-screen top-bleed via `graphicsLayer { translationY = -topBleedPx }` so gradient extends behind the transparent TopAppBar into the status bar.
*   Bottom 280dp zone: DotRailTimeline (full-width, end=60dp) + Shuffle button (brand gradient `#FF9966→#FF6699`, always `TopEnd` pinned) + DialKnob (top=40dp, height=155dp).
*   360dp fade gradient dissolves list into timeline zone.
*   List top padding 8dp — no wasted empty space.

---

### 🎹 CaptureScreen Polish Sprint (Post RTGL1.0 — May 2026)

#### Keyboard-Aware Bottom Toolbar
- Added `Modifier.imePadding()` to the CaptureScreen Column. As the keyboard slides in, the full bottom cluster (action icons row + Save button) rides up with it in sync. No manual animation needed — `imePadding()` is animated automatically by Compose.
- Removed `navigationBarsPadding()` from the bottom row (subsumed by `imePadding()`).

#### Keyboard Auto-Open Fix (Edit Mode)
- Previous approach (`keyboardController?.show()` at an arbitrary delay) was unreliable — the IME ignores `show()` calls during ongoing window transitions.
- Fixed with a reactive pattern: `onFocusChanged { if (isFocused) keyboardController?.show() }` on the `BasicTextField`. `show()` is now called only after the system confirms focus is granted, never during the transition.
- Delay kept at 400ms (≥ 280ms crossfade) to ensure focus is only requested after the transition settles.

#### Save Button Redesign
- Full-width pill: `fillMaxWidth()`, `padding(horizontal = 16dp)`, `height(52dp)`, `RoundedCornerShape(100dp)`.
- Removes the old half-width right-aligned layout; button now feels like a primary CTA across the full screen.

#### Emotion Tab Moved to Action Row
- Emotion picker moved from its own bottom row into the left side of the action icons row.
- Bare styling: emoji + label text + `↓` caret (`ExpandMore`). No pill background, no `appleShadow`. Taps still open the `ModalBottomSheet`.
- Caret changed from `KeyboardArrowRight` → `ExpandMore` (semantically correct for a bottom sheet).
- Bottom row now contains only the Save button.

#### STT / Mic Removed
- `SpeechRecognizerManager`, all STT state vars (`isSpeechListening`, `speechPartialText`, `speechError`, `sttPending`), animations (`sttPulse`, `listenPulse`), callbacks, listening indicator UI, and the mic `Box` button all removed from `CaptureScreen`.
- `recordAudioPermissionLauncher` simplified — the `sttPending` branch removed; it now only handles audio recording permission.
- `readOnly = isSpeechListening` and the conditional text color on `BasicTextField` removed — text field is always editable.
- Reason: mic was showing as an unexplained floating system toolbar on keyboard open; the feature was not part of the core product story for launch.

---

### ✏️ CaptureScreen Edit Mode Redesign + Codebase Refactor (Tag: RTGL1.0 — May 2026)

#### Codebase Refactor: Screens.kt Split (Task 58d — DONE)
- `Screens.kt` (~2200 lines) fully split into per-screen files:
  - `ui/DiaryScreen.kt` — DiaryScreen + BentoMemoryCard + bottom sheet
  - `ui/CaptureScreen.kt` — CaptureScreen (all capture + edit logic)
  - `ui/IndexScreen.kt` — IndexScreen + PolaroidPillCard + DotRailTimeline + DialKnob wiring
  - `ui/Shared.kt` — shared helpers (appleShadow, emotionColor, etc.)
  - `ui/MemoryDetailScreen.kt` — already existed as separate file
- No behaviour changes. Build verified clean post-split.

#### Edit Screen Visual Overhaul
- **Background:** Two-layer system matching MemoryDetailScreen — vertical emotion gradient + scrim image overlay. Gradient keys off `selectedEmotion` in edit mode, giving live tonal feedback as user changes emotion.
- **Nav row:** 68dp height, white-circle icon buttons with `appleShadow()`, close button right-aligned. Matches MemoryDetailScreen floating header exactly.
- **Content padding:** Body content (`44dp` horizontal), UI controls (`24dp` horizontal) — consistent with MemoryDetailScreen.
- **Spacer:** 32dp gap between nav row and first content block.
- **Emotion picker:** White pill button (bottom-left), opens `ModalBottomSheet` with 6 emotion options. Checkmark on selected. Replaces the old inline emotion row.
- **Save button:** Full-width gradient button at the bottom. Stays visible regardless of keyboard state.
- **Crossfade transition:** Detail → Edit and Edit → Detail use fade (280ms) instead of the slide-up used for new captures. Nav-aware transitions in `AppNavigation.kt` detect source/target route.
- **Auto-focus:** On entering edit mode, cursor is placed at end of existing text and device keyboard opens automatically (`delay(100)` → `focusRequester.requestFocus()` + `keyboardController.show()`).

---

## 3. The Path Forward (Current Objective)

**Phases 1–14, all post-phase polish, and the IndexScreen sentinel dial redesign are complete.**

### Next Phase — Smarter Sentiment + Index Filters (Phase A):
*   Task 59: DB Migration 2→3 (expanded schema: emotion intensity, bookmarks, stickers, letters)
*   Task 60: HuggingFace Inference API sentiment analysis (replaces keyword detector, with fallback)
*   Tasks 61–62 superseded by the new DialKnob-driven IndexScreen (already shipped)

> [!TIP]
> **Developer Goal**
> Phase A unlocks richer emotional context across the app. Plan collections (Phase B) and sealed letters after Phase A ships.

---

## 4. Publishing Strategy & Revised Roadmap

### Decision: Android First → React Native Rewrite

**Goal:** Establish credibility as an independent product builder. Ship fast, validate with real users, then scale to iOS + web.

**Phase 1 — Android Publish (~1 week):**
- App is 95% complete. Remaining: keyword enrichment + Play Store setup.
- Free app, no monetisation, no ads. "Proof of work" play.
- Privacy story: local-only, no cloud, no tracking — a feature, not a limitation.

**Phase 2 — React Native Rewrite (~3 weeks post-launch):**
- Single codebase for iOS + Android + Web.
- Port all screens, DialKnob (Canvas), emotion detection, media APIs.
- Estimated 3 weeks. Gives cross-platform credibility.

### Decision: Keyword Enrichment over HuggingFace API

**Original plan (Task 60):** HuggingFace Inference API (`j-hartmann/emotion-english-distilroberta-base`).

**Why dropped:**
- 30s cold start on free tier = broken UX on first save
- ~$0.10/month credit cap — not truly free at scale
- Not trained on Indian English or Hinglish
- CALM has no corresponding label in the model — CALM would never be detected

**New approach:** Enrich `EmotionDetector.kt` with Indian English, Hinglish, Gen Z slang, spiritual/cultural language.
- Free forever, zero latency, offline-first, better accuracy for target user (Late Millennials / Gen Z India)
- Research brief provided; user to gather UGC keyword doc; Claude to implement + validate

### Tasks Deferred (not blocking launch)
- Task 58d (Split Screens.kt) — tech debt, post-launch v1.1
- Task 59 (DB Migration 2→3) — needed only for Phase B bookmarks, not for launch

---

## 5. Latest Session Completions (2026-05-07 & 2026-05-08)

### IndexScreen Final Polish & Visual Redesign (2026-05-08)

Aligned IndexScreen visual language with the rest of the app — applied the same background/header system established in MemoryDetailScreen and CaptureScreen. Key changes:

**Background System:**
- Implemented two-layer background: vertical emotion gradient (appBackground → emotionColor.copy(0.18f)) + memory_detail_scrim image overlay
- Fade gradient (bottom of list) reduced from undefined to 20% alpha (was preventing emotion gradient visibility, now provides subtle separation)

**Header Navigation:**
- Redesigned header: back button + title "My Diaries" on left side, Plus button (capture trigger) on right side
- Floating Row layout using SpaceBetween (no fixed height constraints, uses statusBarsPadding + padding)
- Matches white-circle button style with appleShadow from MemoryDetailScreen

**Memory Card Layout (PolaroidPillCard):**
- Restructured to: text content on left (snippet + date), polaroid on right (only when photoUri exists)
- Row height compacted from 82dp to 68dp
- HorizontalDivider (0.5dp, transparent) between each card for subtle list separation
- Polaroid hidden when no media present (previously always shown)

**Shuffle Button Redesign:**
- Changed to secondary button style: white background, black text, primary-color icon (#FF9966)
- Positioned TopEnd (over dial knob area) with subtle appleShadow
- RoundedCornerShape(20dp) pill shape, lighter visual weight than primary buttons

**DialKnob Hue Shift:**
- Replaced saturation/brightness shifting with hue-only shifting from emotion color
- Implemented `Color.hue()` and `Color.withHue(hue)` helpers using `android.graphics.Color.RGBToHSV/HSVToColor`
- Applies emotion hue to: `rimAccent`, `mutedFg`, `neuDark`, metallic rim gradient stops (0.22, 0.50, 0.78)
- Font changed from Trocchi → Nunito (app default), improving readability in pill carousel

**Spacing Compaction:**
- Bottom container height: 280dp → 210dp
- List bottom padding: 280dp → 210dp
- Dial padding: top=44dp, height=140dp (from 155dp)
- List contentPadding: top=4dp, bottom=16dp, spacedBy=0dp
- Row height: 82dp → 68dp
- Removed large vertical gaps between list and dial area

**Plus Button Navigation:**
- Added onNavigateToCapture parameter to IndexScreen
- Wired to bottom nav's "Add new memory" flow (`CaptureScreen?action=text`)

### Audio Hero Component Finalization (Tasks A1-A5)

**Visual Refinement:**
- SVG waveform integration: 30 bars with professionally-designed amplitude heights extracted from wave.svg
- Background: #414141 at 0.23 alpha (subtle dark), 1dp stroke (#000000 at 0.10 alpha)
- Waveform coloring: white bars for unplayed, black bars for played portions
- Play button behavior: 60dp dark circle (#1C1C1E) with white icon, centered, **disappears during playback**

**Implementation:**
- `AudioHeroSection` made `internal` so both MemoryDetailScreen and CaptureScreen can reuse it
- AudioState enum (IDLE, PLAYING, PAUSED) used consistently across both screens
- Session-scoped pause/resume: position retained within session, resets to 0 on navigation away
- Audio focus handling: pauses on phone calls, app backgrounding, other media (AudioFocusRequest API with listener)
- Playhead progress: calculated from currentPosition / duration, updated every 100ms

**Design Decisions:**
- Removed delete button from audio hero (kept as independent action if needed elsewhere)
- Audio-only memories: hidden pill, hero is sole audio surface
- Photo + audio memories: photo is hero, audio renders as pill below text content

### UX Fixes & Navigation Polish

**Keyboard Auto-Focus on Edit (CaptureScreen):**
- Root cause: LaunchedEffect only handled action cases for sheet (text/voice/image/speech), missing null case for editing
- Fix: Added null action case with focusRequester.requestFocus() + keyboardController?.show() after 400ms crossfade delay
- Now keyboard auto-opens when entering edit mode, matching new-memory-from-sheet behavior

**Scroll Position Retention (DiaryScreen):**
- Root cause: rememberLazyListState() doesn't preserve state across back-navigation
- Fix: Added rememberSaveable state vars for scroll index + offset, implemented restore on return
- Continuously save current scroll position via LaunchedEffect
- Changed auto-scroll-to-top logic: only triggers on NEW memory additions, not on back-nav
- Users now return to their last scroll position when exiting MemoryDetailScreen

---
- Phases B, C, D, E — post-launch roadmap unchanged
