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

## 3. The Path Forward (Current Objective)

**Phases 1–14 and all post-phase polish are complete.** The core product — capture, playback, bento grid, memory detail with shared transitions, sentiment analysis — is fully functional and visually refined.

### Next Phase — Smarter Sentiment + Index Filters (Phase A):
*   Task 59: DB Migration 2→3 (expanded schema: emotion intensity, bookmarks, stickers, letters)
*   Task 60: HuggingFace Inference API sentiment analysis (replaces keyword detector, with fallback)
*   Task 61: IndexScreen timeline layout
*   Task 62: Emotion filter chips

> [!TIP]
> **Developer Goal**
> Phase A unlocks richer emotional context across the app. Plan collections (Phase B) and sealed letters after Phase A ships.
