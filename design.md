# Memory App - Micro-Design System (MVP)

This document defines the foundational visual language for the Memory App. It serves as the single source of truth for UI execution, ensuring a warm, inviting, and emotionally resonant experience.

## Core Aesthetic
* Warm / Cozy / Scrapbook / Journal feel.
* Liquid glass elevation (Apple-style).
* Soft, diffuse shadows (Apple-style).

---

## App-Wide Screen System

These rules apply to **every full-screen destination** (DiaryScreen, CaptureScreen, IndexScreen, MemoryDetailScreen). Deviations must be explicitly noted.

### Background Layer System (Two-Layer)

Every screen uses a two-layer background stack in this order (bottom to top):

| Layer | Type | Spec |
|---|---|---|
| **1 — Gradient** | `Brush.verticalGradient` | `[appBackground, emotionColor.copy(0.12f), emotionColor.copy(0.35f)]`, full screen |
| **2 — Scrim** | `Image(R.drawable.memory_detail_scrim)` | `fillMaxSize`, `ContentScale.Crop` |

- `emotionColor` = the memory's (or current selection's) `EmotionTone` → `Color` mapping from `ui/theme/Color.kt`.
- For screens without a specific memory context (e.g., DiaryScreen), use `NEUTRAL` tone as the default gradient.
- Do **not** use a flat `background` color alone on any full-screen surface — the two-layer system is mandatory.

### Screen Header / Nav Row

Every screen has a floating header row at the top. Spec (applies to CaptureScreen and MemoryDetailScreen):

| Property | Value |
|---|---|
| Height | `68dp` |
| Horizontal padding | `24dp` |
| Top inset | `statusBarsPadding()` on the parent Box |
| Title typography | `titleMedium`, nunitoFamily (or Trocchi for display-emphasis screens) |
| Icon buttons | White circle fill, `appleShadow()`, near-black `Color(0xFF1C1C1E)` icon tint, `40dp` size |
| Close/Back button | `Icons.Default.Close` or `Icons.Default.ArrowBack`, same circle style |

- The nav row is floating (not in a `TopAppBar` / Scaffold), so `contentWindowInsets = 0` on the parent Scaffold.
- A `Spacer(Modifier.height(32.dp))` separates the nav row from the first content block beneath it.

### Content Padding

| Zone | Horizontal padding |
|---|---|
| Body content (text input, photo, audio blocks) | `44dp` |
| UI controls (nav row, chips, action rows, bottom bar) | `24dp` |

- These padding values match across CaptureScreen and MemoryDetailScreen for visual consistency.

### Keyboard-Aware Bottom Toolbar

Every screen with a text input and a bottom action row must use `Modifier.imePadding()` on the outermost Column. This makes the full bottom cluster (action icons + emotion tab + Save button) ride up in sync with the keyboard as it slides in, without any manual animation or offset math. `navigationBarsPadding()` is NOT applied separately on the bottom row — `imePadding()` subsumes it.

### Action Row Layout (CaptureScreen)

The action row sits between the text field and the Save button. Layout:

| Position | Content |
|---|---|
| Left | Emotion tab — bare: emoji + label text + `↓` caret (`ExpandMore`). No background, no shadow, no pill. Taps open the emotion bottom sheet. |
| Right | Audio recorder icon + vertical divider + Photo picker icon |

### Save Button

Full-width pill button at the bottom of every capture/edit surface:
- `fillMaxWidth()`, `padding(horizontal = 16dp)`, `height(52dp)`
- `RoundedCornerShape(100dp)` — full pill
- Gradient fill: `GradientPeach → GradientPink` (horizontal) when content exists; flat grey at 15% alpha when empty
- `appleShadow(100dp)` when active

---

## Typography
*   **Primary Font (Titles):** `Trocchi` (Google Fonts, free) - Gives the personal, handmade journal feel. Replaced `Playwrite Österreich` as of Task 47.
*   **Secondary Font (Body Text, Entries & Dates):** `Nunito` (shipped as `sf_pro_rounded` in res/font) - Ensures long journal entries and dates are easy to read.

### Type Scale
| Style | Token | Family | Weight | Size | Line Height | Usage |
|---|---|---|---|---|---|---|
| **Headline** | `titleMedium` | Nunito | Bold | 20sp | 24sp | Card headlines, memory titles |
| **Body Large** | `bodyLarge` | Nunito | Regular | 20sp | 28sp (140%) | Memory detail body text (all variants) |
| **Body** | `bodyMedium` | Nunito | Regular | 14sp | 20sp | Subtitles, inline text |
| **Caption** | `bodySmall` | Nunito | Regular | 12sp | 16sp | Secondary captions |
| **Label** | `labelSmall` | Nunito | Bold | 11sp | 16sp | Date labels, emoji row |

## Color Palette
### Base Colors
*   **App Background:** `#FDF9F1` (Warm paper)
*   **Memory Card Background:** `#FEFCF7` (Warm off-white — slightly warmer than pure white, matches the paper texture overlay)
*   **Primary Text:** `#2C2A29` (Soft black)
*   **Secondary / Hint Text:** Medium warm grey (e.g., `#8E8A86`)
*   **Accent / Primary Brand Gradient (Interactive elements):** `#FF9966` → `#FF6699` (soft orange → soft pink, left-to-right or top-to-bottom as context demands).
    - Applied to: "Save memory" button fill, FABs, waveform bars, gradient strip above bottom sheet, action chips active states, and any other primary interactive surface.
    - Replaces the previous flat `#2C2A29` accent for all interactive elements.

### Emotional Color Variants
Each emotion has two usage modes:

| Variant | Alpha | Usage |
|---|---|---|
| **Full** | None (1.0) | Text labels, icon tints, focal accent strokes, date text |
| **Washed** | 0.20 – 0.25 | Screen background gradients, card fill tints, polaroid fallback backgrounds |

Using the full color as a solid block background is forbidden (see note below). Washed variants blend with the warm paper background to stay readable and non-aggressive.

### Emotional Colors (Background Gradients)
**Crucial UI Note:** These colors should NOT be used as solid blocks. They must be implemented as an **extreme blurred, soft gradient** (custom background) behind the memory content, blending with the Apple Glass elevation.

*   **HAPPY:** `#FFD700` (Gold - Like sunshine, warmth, optimism)
*   **SAD:** `#6B9BD1` (Soft Blue - Gentle sadness, not harsh navy)
*   **ANXIOUS:** `#9B8BC6` (Lavender - Soft purple, calming despite anxiety)
*   **CALM:** `#7FB5A0` (Sage Green - Peaceful, like nature, grounded)
*   **EXCITED:** `#FF9F66` (Coral - Vibrant but warm, not aggressive red)
*   **NEUTRAL:** `#D4C5B9` (Beige - Like old diary paper, nostalgic)

## Shapes & Radii
*   **Buttons (FABs, Action Icons):** 100% radius (Perfect circles/pills)
*   **Memory Cards / Containers:** `20dp` radius
*   **Small decorative cards (e.g., polaroid thumbnails, inline media chips):** `2dp` radius — intentional exception to the 20dp rule. Smaller surfaces get proportionally smaller radii so corners don't dominate the shape.

## Elevation & Shadows
*   **Style:** Soft, diffused Apple-style drop shadows. 
*   **List Separation:** Rely entirely on Apple-style shadows and padding. Do NOT use visible hard borders or divider lines between memory cards in the index or list views.
*   **Implementation Note:** Avoid harsh Material Design default dropshadows. Focus on low y-offset, low opacity (e.g., 5-8% black), and a high blur radius (e.g., 16px-32px).
*   **Glassmorphism (Liquid Glass):** Used for elevated overlays (e.g., the bottom audio player). For MVP compatibility across older devices (API 26+), standard translucent overlays (e.g., 80% opacity) are acceptable in place of OS-level render blurs.

## Memory Cards

### Feed Layout (DiaryScreen)
*   Single-column `LazyColumn` with `reverseLayout = true` — newest memory sits at the **visual bottom**; user scrolls downward to reach older memories.
*   **Width:** Each card is **70% of screen width**, centred within **24dp horizontal margins**.
*   **Alternating alignment:** Even-index cards pin to the **left** (`CenterStart`); odd-index cards pin to the **right** (`CenterEnd`).
*   **Stacked overlap:** Cards overlap the one below by **60dp** (negative `spacedBy`). A `zIndex` ladder ensures newer cards always render on top of older ones.
*   **Tilt:** Even-index cards tilt **−3°** (left), odd-index cards tilt **+3°** (right), applied via `graphicsLayer { rotationZ }` so it is purely visual and does not affect layout bounds or shared transitions.

### Card Dimensions
*   **Height:** Fixed `300dp` for all variants.
*   **Width:** 70% of screen width.
*   **Corner radius:** `30dp`.
*   **Shadow:** None — relies on frosted blur and gradient for elevation.
*   **Texture:** None — clean card surface.
*   **Inner Stroke:** White 1px strokes on top, left, and right edges at 10% alpha (subtle inner highlight).

### Card Background
*   **Style:** Radial frosted glass gradient (two-layer system).
    - **Layer 1:** Radial gradient base (`white @ 64% alpha` → `sentiment color @ 64% alpha`)
    - **Layer 2:** Frosted overlay (white 48% alpha + 60px GPU-accelerated blur)
*   **Gradient:** Radial gradient emanating from center.
*   **Gradient Spectrum:** 24% spread (0-76% white, 76-100% sentiment transition). Creates soft, subtle color transition.
*   **Blur Effect:** 60px GPU-accelerated frosted blur on overlay layer (48% white alpha). Creates visible frosting effect while keeping images/text sharp. API 31+ implementation with graceful fallback on older devices.
*   **Sentiment Colors:** 
    - HAPPY: `#EBCC75` (golden yellow)
    - SAD: `#6B9BD1` (soft blue)
    - ANXIOUS: `#9B8BC6` (lavender)
    - CALM: `#7FB5A0` (sage green)
    - EXCITED: `#FF9F66` (coral)
    - NEUTRAL: `#D4C5B9` (beige)

### Card Padding
*   **Horizontal (left, right):** `24dp`
*   **Top:** `24dp`
*   **Bottom:** `16dp`

### Card Variants
Three variants are selected automatically based on which media is attached to the memory:

| Variant | Trigger | Layout |
|---|---|---|
| **Photo** | `photoFilePath != null` | 110dp image at top; text below (headline + body + date) |
| **Audio** | `audioFilePath != null` (no photo) | 110dp animated waveform at top; text below |
| **Text-only** | No media | Full-height text with frosted gradient background |

### Card Typography
*   **Headline:** 
    - Font size: 20sp
    - Font weight: Bold
    - Font family: SF Pro Rounded
    - Line height: 22sp
    - Letter spacing: -0.4sp
    - Color: `#000000` at 88% opacity (`#E0000000`)
    - Lines: Max 2 with ellipsis

*   **Body / Subtitle:**
    - Font size: 14sp
    - Font weight: Regular
    - Font family: SF Pro Rounded
    - Line height: 18sp
    - Letter spacing: -0.4sp
    - Color: `#000000` at 62% opacity (`#9E000000`)
    - Lines: Max 2 with ellipsis

*   **Date Label:**
    - Font size: 12sp
    - Font weight: Regular
    - Font family: SF Pro Rounded
    - Line height: 16sp
    - Letter spacing: -0.4sp
    - Color: `#000000` at 62% opacity (`#9E000000`)
    - Format: "MMM d" (e.g., "12 April")
    - Position: Bottom-left

*   **Sentiment Emoji Indicator:**
    - Position: Bottom-right
    - Size: 18sp
    - Mapping:
      - HAPPY: 😊
      - SAD: 😢
      - ANXIOUS: 😰
      - CALM: 😌
      - EXCITED: 😄
      - NEUTRAL: 😐

### Media Images
*   **Photo variant:** 110dp height, full card width, crops excess vertically
*   **Audio variant:** Waveform display in 110dp height box with sentiment color background
*   **Multiple images (future):** 
    - 1 image: Full width, 110dp height
    - 2 images: 50% width each with 2px gap, 110dp height
    - 3 images: 1 full-width on top (110dp), 2 below (50% width each with 2px gap, 110dp height)

## List Components

### Vertical Scroll List (IndexScreen / PolaroidPillList)
*   **Height:** Natural — the list height equals the sum of all item heights plus spacing. No fixed container height.
*   **Items:** `PolaroidPillCard` — 68dp tall, full width with `16dp` horizontal padding.
*   **Layout:** Text content (snippet + date) on left; polaroid thumbnail (58×68dp) on right when `photoUri != null`; polaroid hidden when no media.
*   **Spacing:** `0dp` between items (divider is part of card).
*   **Divider:** HorizontalDivider (0.5dp thick) below each card, `Color(0xFF2C2A29).copy(alpha = 0.08f)`, `16dp` horizontal padding.
*   **Fade-out:** A `360dp` vertical gradient at the bottom dissolves the list into the dial zone (`Color.Transparent → background.copy(alpha = 0.20f)`).
*   **Empty state:** When no memories match the selected sentiment, render the dot-rail timeline without dots (just the rail line) so the timeline chrome is always visible.
*   **Scroll behaviour:** Driven by `DotRailTimeline` scrubber — `animateScrollToItem(focalIdx)` on focal index change.
*   **Padding:** `statusBarsPadding().padding(bottom=210.dp, top=68.dp)` to clear header and dial area. Content padding: top=4dp, bottom=16dp.

### PolaroidPillCard (Memory Row)
*   **Structure:** Row with text column (left) and polaroid thumbnail (right).
*   **Height:** 68dp (was 82dp).
*   **Text column:** Memory snippet (nunitoFamily 16sp Regular, 1 line) + date label (labelSmall, #8E8A86).
*   **Polaroid:** 58×68dp, 2dp rounded corners, white background, appleShadow(cornerRadius=2dp). Rendered only when `photoUri != null`.
*   **Fallback (no photo):** Entire row height is text (no reserved right side).
*   **Tilt:** Fixed per-card via `remember(index)` — deterministic decorative effect.

### DialKnob (IndexScreen Sentiment Selector)
*   **Hue Shift:** Emotion color hue is extracted and applied to dial elements via `Color.withHue(emotionHue)`:
    - `rimAccent`: metallic rim highlight gradient
    - `mutedFg`: secondary text/detail elements
    - `neuDark`: border stroke
    - Metallic rim gradient: 5 stops at (0.0, 0.22, 0.50, 0.78, 1.0) — mid-tones (0.22, 0.50, 0.78) apply emotion hue
*   **Font:** Nunito (app default, not Trocchi)
*   **Bottom container:** 210dp height (was 280dp). Padding: top=44dp, height=140dp for dial, bottom padding absorbed by dial height.

### Fade Gradient (List→Dial Transition)
*   **Position:** Bottom of memory list, full screen width.
*   **Height:** 360dp.
*   **Direction:** Vertical, transparent at top → background at bottom.
*   **Alpha:** 20% max opacity on background color (was undefined/full opacity, now subtle).
*   **Purpose:** Separates list zone from dial zone without hiding emotion gradient or scrim layers.

### Waveform (Audio Cards)
*   30 bars rendered as a `Canvas` using the brand accent gradient (`#FF9966` → `#FF6699`).
*   Real amplitude data is read from `memory.waveformData` (JSON float array stored in the DB). For entries recorded before waveform capture was added, a deterministic pseudo-random pattern is generated from `audioFilePath.hashCode()` as a fallback.
