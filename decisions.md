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
  * Mapped all 15 M3 typography tokens explicitly to Custom Fonts (SF Pro Rounded and Trocchi) to prevent the system from substituting Roboto into unmapped elements like Dialog text or secondary labels.
* **Font Sourcing — evolution:**
  * *Original issue:* Google Fonts 'Playwrite' repository structure caused HTML files to download instead of raw TTF binaries, breaking Jetpack Compose's font loader.
  * *Interim resolution:* Swapped to **Indie Flower**, then later to **Playwrite Österreich** (bundled as `res/font/playwrite_osterreich.ttf`).
  * *Final resolution (Task 47):* Replaced Playwrite Österreich with **Trocchi** (OFL licensed, sourced from `github.com/google/fonts` raw binary). `trocchiFamily` is now the single primary display font across all display/headline/titleLarge tokens.
* **Apple-Style Soft Shadows:** 
  * Standard Material 3 `FloatingActionButtonDefaults.elevation()` yields harsh, distinctly "Android" dropshadows. 
  * Set all Material elevations to `0.dp`. Developed a custom, reusable `.appleShadow()` Compose Modifier using the underlying `Canvas.drawRoundRect` and `Paint.setShadowLayer` APIs to accurately replicate the wide, highly-blurred, low-opacity (7% alpha) dropshadows prescribed by the design spec.
* **Spacious Layouts:** Purposely elevated default Compose paddings (from `16.dp` up to `24.dp`) inside `MemoryCard` and `DiaryScreen` list arrangements to emulate the modern, airy, spacing style often found in premium iOS applications. 

## UX Handling

* **Unsaved Changes Guard:** Implemented a robust `isDirty` comparator checking current editor state against original database loads. Intercepted both software (✕ Button) and hardware (Gesture/Back Arrow) edge navigations via Android's `BackHandler` API to trigger an `AlertDialog` confirming destructive exit intent.
* **Contextual Edit vs View:** Opted for a "Long Press" modal workflow inside the index feed for triggering edits/deletions rather than cluttering the clean UI cards with static edit/trash icons.

## Phase 9: Capture Entry Point & Animation

* **Persistent Bottom Sheet over FAB:**
  * Removed the `+` FAB. Replaced with a persistent sheet anchored at the bottom 15% of `DiaryScreen` using `BoxWithConstraints` (required to compute `sheetHeight` as a `Dp` value for both the sheet and the gradient overlay).
  * Sheet is non-dismissible by design — it is always the primary memory creation entry point.
* **Smart Sheet Tap Zones:**
  * The sheet is split into three independent tap targets rather than a single global clickable, so each icon initiates its own flow without forcing the user through an extra step.
  * *Text prompt* → navigates to `CaptureScreen` with `action=text`; a `LaunchedEffect` fires `FocusRequester.requestFocus()` + `keyboardController.show()` after a 300ms delay to let the slide-up animation settle before the keyboard appears.
  * *Mic icon* → navigates with `action=voice`; `LaunchedEffect` checks `RECORD_AUDIO` permission and either starts `AudioRecorder` immediately or launches the permission request.
  * *Image icon* → navigates with `action=image`; `LaunchedEffect` fires `photoPickerLauncher.launch()` after a 200ms delay.
  * Action is threaded through the nav route as a nullable string argument (`?action={action}`), keeping the edit flow (`?memoryId={id}`) fully unchanged.
* **Bottom-to-Top Slide Animation:**
  * `CaptureScreen` uses Compose Navigation's `enterTransition`/`exitTransition` on the composable route — `slideInVertically { fullHeight }` + `fadeIn` on enter (400ms/300ms), `slideOutVertically { fullHeight }` + `fadeOut` on exit (350ms/250ms). Declared at the nav-graph level so the animation applies regardless of which sheet zone was tapped.
* **Gradient Strip Above Sheet:**
  * A 30dp `Box` with `Brush.verticalGradient` (transparent → soft orange → soft pink) is overlaid at `BottomCenter` and offset upward by `sheetHeight`, sitting flush against the sheet's top edge. Purely decorative; diary content above it is fully unaffected.

## Phase 10: CaptureScreen UI Enhancements

* **Suggestion Chips — Cursor Control (`TextFieldValue`):**
  * *Issue:* `BasicTextField` bound to a plain `String` state always resets the cursor to position 0 when the string is set programmatically (e.g., on chip tap).
  * *Resolution:* Introduced a companion `textFieldSelection: TextRange` state alongside the existing `rememberSaveable` `textContent: String`. `BasicTextField` now receives a `TextFieldValue(textContent, textFieldSelection)`; `onValueChange` updates both independently. Chip taps set `textFieldSelection = TextRange(newText.length)`, placing the cursor at the end. This avoids migrating `textContent` to a non-serialisable `TextFieldValue` and keeps the existing dirty-check and save logic unchanged.
* **Waveform Recording Visualizer (Task 39):**
  * Replaced the red dot + "Recording Voice Memo…" `Row` with an animated `Canvas` waveform.
  * Added `maxAmplitude(): Int` to `AudioRecorder` (delegates to `MediaRecorder.maxAmplitude`).
  * A `LaunchedEffect(isRecording)` coroutine polls amplitude every 60 ms while recording, computing 20 bar heights. Each bar uses a bell-curve spread (centre bars taller) plus random jitter so motion looks organic. Heights are stored in a `List<Float>` state.
  * Bar fill: `Brush.verticalGradient` from `#FFB280` (soft orange) → `#FFA8C0` (soft pink), matching the Task 35 sheet gradient.
  * Fade-out: `AnimatedVisibility(visible = isRecording, exit = fadeOut(tween(600)))` — when recording stops the coroutine cancels, heights freeze at their last values, and the canvas fades out over 600 ms, giving the "freeze briefly then disappear" feel.
  * The audio preview row (play/stop) shown after recording ends is unchanged.
* **Floating "Save memory" CTA (Task 40):**
  * Removed the top-right `TextButton("Save")` from the top bar; its conditional slot is now always a `Spacer(width=48.dp)` to keep the Close `IconButton` left-aligned.
  * Added a Material 3 filled `Button` ("Save memory") directly above the mic/photo FAB row inside the existing `Column`. This places it physically above the action bar, achieving the "above the FAB row" spec requirement without requiring a `Box` or `Scaffold` restructure.
  * Visibility matches the old condition: `textContent.isNotBlank() || selectedPhotoUri != null || recordedAudioUri != null`.
  * Modifier chain: `fillMaxWidth() → padding(bottom=12.dp) → appleShadow(8.dp)`, giving the same soft apple-style shadow used on the FABs.
  * `CaptureViewModel.saveMemory` call is unchanged.
* **Starter Chip Copy:**
  * Removed trailing ellipsis (`…`) from all four starter prompt labels so inserted text reads as a clean sentence fragment the user continues naturally.

## Phase 11: Waveform Recording & Playback (Tasks 41–43)

* **Lerp-based Wave Animation (Task 41c refinement):**
  * Random-jitter-per-frame produced jittery, noisy movement. Replaced with a two-state lerp system: `targets` (a `List<Float>` refreshed every 5 frames) and `waveBarHeights` (lerped toward targets at factor 0.18 each 100ms frame). This gives slow, fluid, organic undulation that still responds to voice amplitude.
  * Reduced control points from 20 to 7 — fewer bezier segments produce wider, rounder wave humps that feel more like Spotify/Apple Music and less like an EKG.
  * Alpha reduced to 15% (`0x26`) for a subtle ghost-wave effect; `maxWaveH` set to full box height so the wave fills the entire bottom 40%.

* **Waveform Data Format — Plain JSON string (Task 42):**
  * No external JSON library needed. Amplitude samples are encoded as `"[f0,f1,…]"` using `joinToString(",", "[", "]")` and decoded with `split(",").mapNotNull { it.toFloatOrNull() }`. Lightweight, readable, and trivially parseable in future tasks.
  * Samples are accumulated in a `mutableStateListOf<Float>` (Compose snapshot-aware) so the UI can read the live count if needed. The list is cleared at the start of each new recording.

* **Room Schema Migration (Task 42):**
  * Added `MIGRATION_1_2` (`ALTER TABLE memories ADD COLUMN waveformData TEXT`) rather than using `fallbackToDestructiveMigration()`, preserving all existing user data on upgrade.

* **AudioPlayer Progress Polling (Task 43):**
  * `MediaPlayer.currentPosition` and `duration` are exposed as simple Kotlin property getters on `AudioPlayer`. The Compose UI polls them via `LaunchedEffect(isPlaying)` at 100ms — no callbacks or Flow needed, keeping the implementation minimal.
  * `duration` returns 1 (not 0) as the default to avoid divide-by-zero in the progress ratio.

* **Pause icon — explicit import required (Task 43):**
  * `Icons.Default.Pause` does not resolve without an explicit `import androidx.compose.material.icons.filled.Pause`, even though `material-icons-extended` is on the classpath. Added the import alongside the existing `PlayArrow` and `Close` imports.

## Phase 12: Visual Polish & Branding (Tasks 44–48)

* **Bottom Sheet Placeholder Alignment (Task 44):**
  * Final state: text is **left-aligned** (text-start) and **vertically centred** within the sheet. Implemented as a `Box(contentAlignment = Alignment.CenterStart)` taking `weight(1f) + fillMaxHeight()`, replacing the earlier `Text` with `fillMaxHeight()` that pushed content to the top.

* **Brand Gradient as Primary Interactive Color (Task 45):**
  * Canonical gradient `#FF9966 → #FF6699` (horizontal) is now the fill for all primary interactive surfaces: "Save memory" button and Mic/Photo FABs.
  * Technique: set `containerColor = Color.Transparent` on M3 `Button` / `FloatingActionButton` and apply `Modifier.background(Brush.horizontalGradient(...), shape)` — the gradient shows through the transparent container. Icon color set to `Color.White` for legibility.
  * Gradient strip (Task 35) and waveform bars (Tasks 39/41) already matched `#FF9966/#FF6699` — confirmed, no change needed.

* **Trocchi Font Replacement (Task 47):**
  * See "Font Sourcing" above. `playwriteFamily` identifier fully removed from the codebase; replaced with `trocchiFamily` everywhere in `Type.kt` and `Screens.kt`.

* **Center-Aligned Entry Text (Task 48):**
  * `BasicTextField` in `CaptureScreen` now uses `textStyle.copy(textAlign = TextAlign.Center)`. The placeholder `"I remember..."` `Text` receives the same `textAlign` + `fillMaxWidth()` so it stays visually centred before typing begins. No other screens affected.

## Memory Detail Screen — Nav Button & Transition Polish

* **Floating Nav Button Restyle (MemoryDetailScreen):**
  * *Previous:* Back and edit buttons used `Color.Black.copy(alpha = 0.18f)` translucent circles with `Color.White` icons — designed for overlay on dark hero photos but felt inconsistent on light text-only screens.
  * *Resolution:* Buttons now use `Color.White` fill with `.appleShadow()` drop shadow and `Color(0xFF1C1C1E)` (near-black) icon tint. Matches the app's standard white-card + soft-shadow aesthetic (`appleShadow` is the canonical shadow helper throughout the codebase).

* **Text-Only Content Offset:**
  * *Issue:* On text-only memories (no photo or audio hero), the `LazyColumn` started from `y = 0`, placing the first line of body text directly behind the floating nav buttons.
  * *Resolution:* Applied `Modifier.statusBarsPadding().padding(top = 56.dp)` to the text-only `LazyColumn`. The 56dp covers the button row height (40dp) + row padding (8dp) + visual gap (8dp). `statusBarsPadding()` ensures correct clearance regardless of device status bar height.

* **Shared Transition Close Animation — Abrupt Fade Fix:**
  * *Root cause (4 compounding issues):*
    1. Both `boundsTransform` springs used `Spring.StiffnessMediumLow` (settling ~630ms), leaving only ~70ms margin within the 700ms nav scope window. Any frame drop caused the overlay to be torn down mid-animation.
    2. Exit animations were asymmetric: card side used `fadeOut(tween(300))`, detail side used `fadeOut(spring(StiffnessMediumLow))` — overlap in the overlay past the card's perceived end.
    3. The DiaryScreen `popEnterTransition = tween(700)` kept the `AnimatedVisibilityScope` alive for 700ms. If the user tapped the same card again before those 700ms expired, the new navigation started while the previous scope was still active, leaving `SharedContentState` claimed by two overlapping scopes — the second close animated from a corrupted state.
    4. The gradient `Box` (sibling of the Scaffold, outside `sharedBounds`) exited via `popExitTransition = ExitTransition.None`, disappearing on frame 1 of every close regardless of how well the geometry spring performed.
  * *Resolution:*
    * Replaced all springs with `tween(durationMillis = 500, easing = FastOutSlowInEasing)` for `boundsTransform` and `tween(400)` for `enter`/`exit` fades — both sides symmetric.
    * Tightened `popEnterTransition` on DiaryScreen from `tween(700)` → `tween(600)`: gives 100ms headroom over the 500ms tween, short enough that the scope expires before the user can re-tap and start a second overlapping nav.
    * Changed Detail route `popExitTransition` from `ExitTransition.None` → `fadeOut(tween(400))` so the gradient `Box` fades out smoothly over 400ms instead of snapping.
    * Removed now-unused `Spring`, `spring`, and `ExitTransition` imports from `MemoryDetailScreen.kt`.

## Observability / Debugging

* **Structured Logcat Logging:** Added `android.util.Log` calls across all layers with a consistent `Diary.<Layer>` tag convention (`Diary.MainActivity`, `Diary.Navigation`, `Diary.CaptureVM`, `Diary.DiaryVM`, `Diary.Repository`, `Diary.Database`, `Diary.AudioPlayer`, `Diary.AudioRecorder`, `Diary.EmotionDetector`, `Diary.ImageStorage`). Filter all app logs in Logcat with `tag:Diary`. Uses `Log.d` for normal flow, `Log.i` for key state changes, and `Log.e` for errors with full stack traces.
