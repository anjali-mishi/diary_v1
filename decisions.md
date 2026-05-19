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
  * *Resolution:* Buttons now use `Color.White` fill with `.appleShadow()` drop shadow and `Color(0xE0000000)` (primary black at 88% opacity) icon tint. Matches the app's standard white-card + soft-shadow aesthetic (`appleShadow` is the canonical shadow helper throughout the codebase).

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

## IndexScreen — Sentiment Dial Redesign

* **Replaced bento grid with sentiment-filtered PolaroidPillCard list:**
  * The bento `LazyVerticalStaggeredGrid` and timeline `LazyColumn` (Tasks 61–62 plan) were replaced with a new paradigm: a `DialKnob` component selects one of 6 sentiments; the list shows only memories matching that sentiment. This collapses filtering + navigation into a single tactile interaction rather than separate chips + list.

* **DialKnob — pure Canvas, no blur API:**
  * `DrawScope` in Compose has no `BlurMaskFilter`. Multi-layer shadow is simulated by stacking `drawRoundRect` calls at decreasing alpha and increasing offset — 5 dark layers (bottom-offset) + 5 light layers (top-offset). Step size and layer count control apparent blur radius.
  * White rectangle above the pill artifact: `drawRect(backgroundColor)` was painting the canvas solid, making the region above the pill visible. Fixed by removing it — canvas is transparent outside the pill path.

* **Dial state persistence — ViewModel hoist:**
  * `remember` resets on config change and nav back-stack pop. Solution: `var indexDialValue: Float = 0f` on `DiaryViewModel` — survives config changes (ViewModel lifecycle) and is shared across `DiaryScreen` and `IndexScreen` via the same ViewModel instance in `AppNavigation`.

* **PolaroidPillCard — equal visual weight:**
  * Early drafts animated tilt and alpha based on `isFocal` state. Removed: focal-driven alpha makes cards feel like a carousel, not a list. All cards now have equal weight. Tilt is `remember(index)` — deterministic and decorative only.
  * Shadow: `appleShadow(cornerRadius = 2.dp)` instead of `Modifier.shadow(3.dp)`. Rationale: `appleShadow` uses `setShadowLayer(24f, 0f, 6f, argb(18, 0, 0, 0))` — high blur, low alpha — matching the design system's "5–8% black, 16–32px blur" spec. Raw `shadow()` produces harsh Material elevation shadows.

* **PolaroidPillCard no-photo fallback — Option C (date as art):**
  * Option A (initial letter) was trialled but felt generic. Option C chosen: month + day rendered as large Trocchi Bold in the emotion color on a 25% washed background inside the polaroid frame. Aligns with the design system's "washed" emotional color variant rule and keeps the polaroid format recognisable.

* **Sentiment gradient top-bleed:**
  * IndexScreen content starts below the TopAppBar (Scaffold applies `innerPadding`). TopAppBar has `containerColor = Color.Transparent`. To make the sentiment gradient appear to start from the status bar, the gradient Box uses `graphicsLayer { translationY = -topBleedPx }` (status bar height + 56dp). `graphicsLayer` shifts drawing without affecting layout bounds, so it overflows into the TopAppBar/status bar zone without disrupting the composable's allocated space.

* **DotRailTimeline — always visible, empty state rail:**
  * Early version returned early (`if (count == 0) return`) hiding the timeline entirely when no memories matched the sentiment. Changed: the rail line is always drawn; dots are conditional on `count > 0`. Rationale: the timeline chrome (rail + shuffle button) is a persistent navigation affordance and should not disappear when the content is empty — it signals to the user that filtering is active.

* **Shuffle button — brand gradient, fixed position:**
  * Moved from inline `Row` end to `Alignment.TopEnd` on the bottom container `Box`. Rationale: `weight(1f)` on the timeline inside a `Row` already pushed the button to the end, but fixing it to `TopEnd` on the `Box` makes it completely immune to timeline width changes and reinforces the "always accessible" intent. Brand gradient (`#FF9966 → #FF6699`) applied to the circle background with white icon — consistent with all primary interactive surfaces in the design system.

* **Typography hierarchy in list:**
  * Snippet (primary): `nunitoFamily`, 16sp, Regular, 1 line — Trocchi is display-only; body text in lists uses the legible sans-serif per `design.md`.
  * Date (secondary): `labelSmall`, `#8E8A86` — matches "Secondary/Hint Text" in the design system. Previous `emotCol.copy(alpha = 0.75f)` was failing accessibility contrast and violated the emotional color "full alpha for text only" rule.

## Publishing & Platform Strategy

* **Android-first, React Native second:**
  * Decision: Ship Android app to Play Store first. Once live and validated with real users, rewrite in React Native for iOS + Android + Web (single codebase).
  * Rationale: App is 95% complete. Fastest path to "proof of credibility as a product builder." React Native rewrite estimated 3 weeks post-launch.
  * React Native chosen over PWA + Capacitor for stronger portfolio signal (native compilation, App Store presence on both platforms).

* **No monetisation:**
  * App published free, no ads, no IAP. Goal is credibility — "I can ship independently" — not revenue.

* **Emotion Detection: Indian English keyword enrichment over HuggingFace API:**
  * Task 60 (HuggingFace Inference API) revised. HuggingFace free tier has cold starts up to 30s and ~$0.10/month credit limit. Not trained on Indian English or Hinglish.
  * Decision: enrich `EmotionDetector.kt` keyword sets with Indian English, Hinglish transliterations, Gen Z slang, and spiritual/cultural language instead.
  * Rationale: (1) free forever, (2) zero latency, (3) better accuracy for target user (Late Millennials / Gen Z India), (4) "100% private, no data leaves phone" is a stronger product story.
  * CALM gap confirmed: HuggingFace model has no `calm` output label. Keyword detection preserves CALM correctly.
  * Research phase: UGC research brief written for manual research (Reddit, Twitter, Quora India, Instagram, YouTube). User to provide enriched keyword doc; Claude to implement and validate against 10 test phrases before touching code.

* **Task 59 (DB Migration 2→3) deferred:**
  * New columns (emotionIntensity, bookmarks, stickers, letters) are Phase B/C/D features. Not needed for Android launch. Will be added when Phase B (bookmarks) is implemented.

* **Task 58d (Split Screens.kt) deferred:**
  * Listed as "do before Phase A" but not a hard blocker. Screens.kt ~2200 lines is tech debt, not a launch blocker. Deferred to post-launch v1.1.

## App-Wide Screen System (May 2026 — RTGL1.0)

### Background Layer Standardisation
- **Decision:** All full-screen destinations share a two-layer background system: (1) `Brush.verticalGradient` from `appBackground` → `emotionColor.copy(0.12f)` → `emotionColor.copy(0.35f)`, and (2) `Image(R.drawable.memory_detail_scrim)` over the top at full size.
- **Why:** Previously each screen had ad-hoc backgrounds. Standardising on a single spec ensures visual coherence, simplifies future screen additions, and ties every surface back to the emotional tone of the content.
- **Reference:** Established in `MemoryDetailScreen`, now canonical for the entire app. Documented in `design.md` under "App-Wide Screen System."

### Screen Header / Nav Row Standardisation
- **Decision:** Every full-screen destination uses a floating 68dp nav row with `24dp` horizontal padding, white-circle icon buttons (`appleShadow()`), and `Color(0xE0000000)` (primary) icon tint. No `TopAppBar` or Scaffold `topBar` for detail and capture screens.
- **Why:** Scaffold's `TopAppBar` injects `innerPadding.top`, which causes layout shift during shared-element transitions. Floating nav avoids this entirely. The white-circle style was already established on `MemoryDetailScreen`; extending it to `CaptureScreen` makes edit mode feel like a continuation of the detail view rather than a new context.
- **Content padding rule:** Body content = `44dp` horizontal; UI controls = `24dp` horizontal. Consistent across CaptureScreen and MemoryDetailScreen.

### CaptureScreen: Crossfade Transition (Detail ↔ Edit)
- **Decision:** When navigating from `MemoryDetailScreen` to `CaptureScreen` (edit mode), use `fadeIn/fadeOut` (280ms) instead of the slide-up used for new captures. Same fade on the return journey.
- **Why:** Slide-up implies a new sheet appearing. In edit mode, the user is staying in the same content context — a crossfade communicates "mode switch" not "new screen." Nav transitions are route-aware in `AppNavigation.kt` (`initialState.destination.route?.startsWith(Screen.Detail.name)`).

### CaptureScreen: Emotion Picker as Bottom Sheet
- **Decision:** Emotion is selected via a `ModalBottomSheet` opened by a white pill button (bottom-left of the screen). The previous inline chip row is removed.
- **Why:** Six emotion options in an inline row consumed significant vertical space and competed with the keyboard. A bottom sheet is dismissible, scannable, and keeps the writing surface clean.

## CaptureScreen Polish Sprint (Post RTGL1.0 — May 2026)

### Keyboard Auto-Open: Reactive Pattern over Proactive Delay
- **Decision:** Use `onFocusChanged { if (isFocused) keyboardController?.show() }` on `BasicTextField` instead of calling `show()` at a fixed delay.
- **Why:** `keyboardController?.show()` is fire-and-forget — it is silently ignored if the window doesn't have focus yet (e.g., during a 280ms crossfade transition). A reactive pattern tied to actual focus confirmation is guaranteed to fire at the right time.
- **Delay retained:** `delay(400)` before `requestFocus()` ensures the crossfade finishes before focus is even requested.

### imePadding() for Bottom Toolbar
- **Decision:** `Modifier.imePadding()` on the CaptureScreen Column; `navigationBarsPadding()` removed from bottom row.
- **Why:** `imePadding()` is a single modifier that makes the entire bottom cluster (action icons + Save button) animate up with the keyboard automatically. Removes any need for manual offset calculations or `WindowInsets.ime` subscriptions.

### STT / Mic Removed from CaptureScreen
- **Decision:** `SpeechRecognizerManager` and all STT UI fully removed from CaptureScreen.
- **Why:** Android's system keyboard shows a floating toolbar (clipboard + mic shortcuts) when a text field has focus but the keyboard is not visible. This toolbar was appearing on edit mode entry because the keyboard wasn't opening in time. Removing the in-app mic reduces surface area and eliminates the confusing dual-mic affordance (one from the app, one from the system keyboard). STT can be re-added post-launch as a deliberate feature with proper UX design.

### Emotion Tab: Bare in Action Row, not Pill in Bottom Row
- **Decision:** Emotion selector moved to left of action icons row; pill background/shadow removed; only emoji + label + `ExpandMore` caret.
- **Why:** The white pill in its own bottom row created visual competition with the Save button directly below it. Moving it into the action row groups all secondary controls together and reduces the number of distinct rows. Bare styling keeps it lightweight — it's a modifier, not a CTA.

### Save Button: Full-Width Pill
- **Decision:** `fillMaxWidth()` + `RoundedCornerShape(100dp)` + `padding(horizontal = 16dp)`.
- **Why:** A half-width right-aligned button underweights the primary action. Full-width pill is the standard for primary CTAs in the app (consistent with iOS-style bottom buttons). 16dp margin gives slight breathing room from screen edges.

## Observability / Debugging

* **Structured Logcat Logging:** Added `android.util.Log` calls across all layers with a consistent `Diary.<Layer>` tag convention (`Diary.MainActivity`, `Diary.Navigation`, `Diary.CaptureVM`, `Diary.DiaryVM`, `Diary.Repository`, `Diary.Database`, `Diary.AudioPlayer`, `Diary.AudioRecorder`, `Diary.EmotionDetector`, `Diary.ImageStorage`). Filter all app logs in Logcat with `tag:Diary`. Uses `Log.d` for normal flow, `Log.i` for key state changes, and `Log.e` for errors with full stack traces.

## IndexScreen Final Polish (May 2026)

### Background Standardisation Completion
- **Decision:** IndexScreen now uses the same two-layer background system as MemoryDetailScreen and CaptureScreen: emotion gradient (appBackground → emotionColor.copy(0.18f)) + memory_detail_scrim image.
- **Why:** Consolidates the entire app around a single visual language — every full-screen destination now has consistent emotional context via the same background stack.
- **Implementation:** Both empty and populated states render the same background; emotion is inherited from the current dial selection or defaults to NEUTRAL.

### DialKnob: Hue-Only Emotion Color Shift
- **Decision:** Instead of shifting saturation/brightness, extract only the hue from the emotion color and apply it to dial elements (rimAccent, mutedFg, neuDark, metallic rim gradient).
- **Why:** Hue shifts preserve the dial's tonal balance and metallic character while still responding to emotion selection. Saturation/brightness shifts would oversaturate or darken key visual elements, breaking the skeuomorphic balance.
- **Implementation:** `Color.hue()` extracts HSV hue via `android.graphics.Color.RGBToHSV`, applied to all relevant colors with `Color.withHue(hue)`.
- **Tested:** Visual comparison between Happy (gold hue ~60°) and Calm (green hue ~165°) confirmed hue shift works on all dial surfaces.

### Header Navigation: Floating Row instead of TopAppBar
- **Decision:** IndexScreen header is a floating Row (not Scaffold TopAppBar) with back button + title on left, Plus button on right.
- **Why:** Floating nav (matching MemoryDetailScreen/CaptureScreen) avoids inner padding and layout shifts. Consistent nav pattern across all screens reduces cognitive load.
- **Layout:** `statusBarsPadding().padding(horizontal=24dp, vertical=14dp)` — no fixed height constraint, content sizes naturally.
- **Plus button:** Captures the top-right slot, navigates to new memory creation flow.

### PolaroidPillCard: Right-Aligned Photo, Text-First Layout
- **Decision:** Text content (snippet + date) takes left 70%; polaroid thumbnail (58×68dp) occupies right 30% only when `photoUri != null`. Polaroid hidden entirely when no media.
- **Why:** Text-first prioritizes readability. Photo becomes a secondary scan hint. When no photo exists, the full row is available for text, eliminating whitespace.
- **Implementation:** Conditional rendering in Row layout — `if (memory.photoFilePath != null) { Box(...) }` — no reserved space when false.

### Shuffle Button: Secondary Style (White, Black Text, Primary Icon)
- **Decision:** Redesigned shuffle from primary to secondary button: white background, black text (#1C1C1E), primary-color icon (#FF9966).
- **Why:** Primary buttons (gradient fill) are for primary CTAs (Save, Plus). Shuffle is a utility action, not primary. Secondary style signals lower visual weight while remaining accessible.
- **Implementation:** `Row` with `RoundedCornerShape(20dp)`, `background(Color.White)`, `Text(..., color = Color(0xFF1C1C1E))`, `Icon(..., tint = Color(0xFFFF9966))`.

### Fade Gradient: 20% Max Alpha
- **Decision:** Reduced fade gradient alpha from undefined (full) to 20% on background color.
- **Why:** The previous fade was opaque at the bottom, completely obscuring the emotion gradient + scrim layers. 20% alpha provides visual separation between list and dial without erasing the background layers.
- **Trade-off:** Slight reduction in list/dial boundary clarity, but preserves the app's unified background aesthetic.

### Spacing Compaction
- **Decision:** Bottom container 280dp → 210dp. List padding, dial sizing, and row heights all reduced proportionally. List contentPadding changed to top=4dp, bottom=16dp, spacedBy=0dp.
- **Why:** Removes unnecessary vertical dead space. Dial and list feel more tightly integrated, improving screen real estate usage and visual density without feeling cramped.
- **Row height:** 82dp → 68dp for PolaroidPillCard, accommodating smaller photos and tighter line-height text.

### DialKnob Font: Trocchi → Nunito
- **Decision:** Carousel text in DialKnob changed from Trocchi (display font) to Nunito (app default sans-serif).
- **Why:** Trocchi at 16sp/11sp sizes became hard to read inside the small pill area. Nunito's legibility at small sizes makes the sentiment labels readable at a glance. Trocchi reserved for display/headline contexts only.

### AppNavigation: Removed Duplicate TopAppBar
- **Decision:** Changed `topBar` logic in Scaffold from `if (isIndex) { TopAppBar(...) }` to `topBar = {}` (empty).
- **Why:** IndexScreen's floating header replaces the global TopAppBar. Scaffold-level TopAppBar was creating duplicate "My Diaries" text + back buttons on screen. Empty topBar avoids this duplication and prevents inner padding that would affect layout.

### Navigation: Plus Button → Capture Flow
- **Decision:** New onNavigateToCapture callback passed to IndexScreen; Plus button navigates to `CaptureScreen?action=text`.
- **Why:** Consistency with DiaryScreen bottom sheet capture entry. Plus button is a discoverable, always-visible alternative to the bottom sheet on IndexScreen.
- **Implementation:** Wired in AppNavigation at IndexScreen call site.

## Pre-Launch Polish Sprint (2026-05-18)

### Primary Color: Black at 88% Opacity
- **Decision:** Replaced `SoftBlack = Color(0xFF2C2A29)` with `Color(0xE0000000)` (pure black at 88% opacity) as the app's primary color.
- **Why:** Cleaner, more modern look. 88% opacity softens pure black slightly while maintaining readability. All hardcoded `Color(0xFF1C1C1E)` instances across WelcomeScreen, DiaryScreen, IndexScreen, MemoryDetailScreen replaced with the new primary value.
- **Scope:** `SoftBlack` in `Color.kt` flows through to `primary`, `onBackground`, `onSurface`, `onSecondary` in the theme. All CTA backgrounds, icon tints, and text colors updated.

### CaptureScreen Save Button: Primary Color (Not Gradient)
- **Decision:** Save button fill changed from `GradientPeach → GradientPink` horizontal gradient to solid `Color(0xE0000000)` (primary color).
- **Why:** Primary CTAs should use the primary color, not the brand gradient. Gradient is reserved for decorative/accent surfaces (waveform bars, sheet gradient strip). Solid primary is more authoritative for the main action button.
- **Bottom padding:** Increased from 12dp to 28dp for breathing room.

### DiaryScreen: Single Memory Centering
- **Decision:** When only one memory exists, render it in a vertically/horizontally centered `Box` instead of the `LazyColumn`. When 2+ memories exist, use the standard stacked `LazyColumn`.
- **Why:** A single card in a reversed lazy column floats near the top with empty space below — feels unfinished. Centering gives the first memory a hero moment and makes the empty-ish state feel intentional.

### DiaryScreen: Hide Header/Pill on Empty State
- **Decision:** The header (logo + "My Diaries") and bottom capture pill are hidden when `memories.isEmpty()`, allowing the WelcomeContent empty state to render cleanly.
- **Why:** Both were rendering with high `zIndex` on top of the WelcomeContent, creating visual overlap. The welcome content has its own CTA ("Capture your first memory") so the pill bar is redundant.

### CaptureScreen: Close Button Navigation Fix
- **Decision:** `onNavigateBack` now falls back to `navController.navigate(Screen.Diary.name)` if `popBackStack()` returns false (empty back stack).
- **Why:** When coming from WelcomeScreen, `popUpTo(Welcome) { inclusive = true }` removes Welcome from the stack before navigating to Capture. The close button's `popBackStack()` had nothing to pop, so nothing happened. Fallback ensures the user always lands somewhere.
- **Bonus:** Close button now routes through `handleBack()` to show the "Discard changes?" dialog when content is dirty.

### Welcome Screen Cards: Reduced Content
- **Decision:** Shortened card headlines and body text; added `fontWeight = FontWeight.Normal` to card body text.
- **Why:** Cards were too text-heavy for a welcome screen — they should hint at content, not display it. Body text appeared bolder than intended because the SF Pro Rounded font's default weight reads heavier at small sizes.

## Welcome Screen — v1: Capture-Focused (May 2026, superseded)

* **Decision:** Show a welcome screen on first launch with 3 variant sample cards (photo, audio, text-only) previewing memory types.
* **Outcome:** Built and working, but identified a strategic gap — the welcome emphasized *capture mechanics* ("here are 3 memory types") rather than the app's actual value proposition: automatic emotion mapping and revisiting memories by emotional context.
* **Status:** Superseded by v2 below. Code still in `WelcomeScreen.kt` — will be replaced.

## Welcome Screen — v2: Emotion-First Onboarding (May 2026, planned)

* **Decision:** Replace capture-focused welcome with an interactive demo that shows the core product loop: write → emotion detected → browse by feeling.
* **Why the pivot:** The founder's vision is explicitly *not* "another journaling app." The differentiator is what happens *after* capture — automatic emotion mapping and emotional pattern browsing. The welcome should demonstrate this, not show input types.
* **Design — single screen, one tap, zero scroll:**
  1. Single sample memory card (text-only, neutral background) centered. Subtle "tap the card" hint.
  2. User taps → emotion color (Calm) blooms behind card. Label: "Calm". Tagline: "You write. The app feels."
  3. After 1s → card shrinks and slides down, morphing into a PolaroidPillCard. Section 2 auto-scrolls into view.
  4. Section 2: Static DialKnob frozen on "Calm" + 2 pre-placed PolaroidPillCards (Happy, Excited). Morphed card joins as third pill.
  5. Final tagline: "Revisit yourself by how you felt." CTA: "Capture your first moment."
* **Principle:** Still not a tutorial. One tap, one reveal, one auto-transition. The interaction *is* the product explanation.
* **Implementation:** Rewrite `WelcomeScreen.kt`. Reuse `DialKnob` (non-interactive preview), emotion colors, `appleShadow()`. Create lightweight static `PolaroidPillRow` (current `PolaroidPillCard` is private to IndexScreen and requires `Memory` object). Card-to-pill morph via `animateDpAsState`/`animateFloatAsState` + auto-scroll.
* **Empty state:** DiaryScreen empty state will show a simplified version (possibly final state with DialKnob preview + CTA, skipping the tap interaction).
