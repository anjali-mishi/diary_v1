# Memory — Feature Plan v2

**Date:** March 2026
**Based on:** diary_v1 MVP (Phases 1–8 complete)
**Network stance:** Internet allowed (opt-in where it changes UX meaningfully)
**Branch:** `claude/plan-diary-features-EeE9n`

---

## Context

The v1 app is a local-first Android diary (Kotlin + Jetpack Compose + Room DB) with:
- Text / photo / audio capture
- Emotion auto-tagging (keyword-based, 6 tones: HAPPY, SAD, ANXIOUS, CALM, EXCITED, NEUTRAL)
- Chronological feed + date-indexed browsing
- Warm scrapbook aesthetic

These 10 features extend the app into a richer emotional companion. Grouped by theme below.

---

## Phase A — Emotional Intelligence (Foundation for everything else)

### Feature 3 · Real AI Sentiment Analysis
**What:** Replace the keyword `EmotionDetector` with Claude API inference so the app understands nuance, sarcasm, mixed feelings, and non-English diary entries.

**How it works:**
- On save, if the device has internet, call Claude (Haiku for speed/cost) with the diary text
- Prompt: classify the primary emotion + intensity (0–1 float) + secondary emotion if strong
- Return falls back gracefully to existing keyword matcher if offline or API call fails
- Store `emotionIntensity: Float` and `secondaryEmotionalTone: String?` in the Memory table

**Data model changes:**
```kotlin
val emotionIntensity: Float = 0.5f          // 0–1 confidence/intensity
val secondaryEmotionalTone: String? = null  // e.g. "CALM underneath EXCITED"
```

**Recommendation:** Use `claude-haiku-4-5-20251001` — fast, cheap, accurate enough. Send only the text content, never photos. Make the call async and non-blocking; the user should not wait for it.

**Alternative if staying offline:** Google's ML Kit `LanguageIdentifier` + `EntityExtraction` — reasonable sentiment accuracy, fully on-device, no API key.

---

### Feature 4 · Filter & Order by Feeling
**What:** Let users browse memories filtered by emotional tone or sorted by intensity.

**How it works:**
- Filter bar on `DiaryScreen`: horizontal chip row — All / Happy / Calm / Excited / Sad / Anxious
- Secondary sort: Newest first (default) | Most intense | Oldest first
- Active filter persists per session (not saved across relaunches)
- `IndexScreen` emotion dots already exist — tapping one pre-filters the diary view

**Data model changes:** None. Query layer only.

**New DAO queries:**
```kotlin
fun getMemoriesByTone(tone: String): Flow<List<Memory>>
fun getMemoriesByToneSortedByIntensity(tone: String): Flow<List<Memory>>
```

**Recommendation:** Animate the filter chips in and out with a slide-down reveal so it feels discoverable but not cluttered.

---

## Phase B — Memory Curation

### Feature 5 · Bookmark a Diary
**What:** Pin favourite memories so they surface in a dedicated "Favourites" view.

**How it works:**
- Long-press or swipe-right on a memory card → bookmark toggle
- Bookmarked memories get a subtle gold ribbon/star on their card
- New "Favourites" tab or section in `IndexScreen`
- Bookmarks feed into Feature 1 (diary collections) as seed content

**Data model changes:**
```kotlin
val isBookmarked: Boolean = false   // already have isHidden pattern to follow
val bookmarkedAt: Long? = null
```

**Recommendation:** Keep the gesture the same as the existing long-press menu — just add a "Bookmark" item alongside Edit/Delete.

---

### Feature 1 · Diary Collections with Custom Covers
**What:** The app automatically groups memories into named "diaries" based on shared themes, life events, or dominant happy/excited emotions. User can also design a cover for each.

**Scope (based on your answer):** Auto-grouped only. Only happy/excited/calm tone memories qualify. Letter-to-self entries get their own "Letters" diary.

**How auto-grouping works:**
- A background job runs weekly (WorkManager) over all memories
- Clustering logic (v1 simple, v2 ML-enhanced):
  - **Date proximity:** memories within 3 days of each other form a candidate group
  - **Emotion filter:** group only if ≥60% of memories in the cluster are HAPPY, EXCITED, or CALM
  - **Minimum size:** ≥3 memories to form a diary
  - **Title suggestion:** Claude API prompt — "Given these diary entry titles and dates, suggest a short evocative name for this chapter of someone's life." Fallback: auto-title from date range.
- A `DiaryCollection` entity is created and linked to memories via a junction table

**Cover design:**
- Default cover: auto-collage from the 3 most-viewed photos in the collection
- Custom options: pick a color theme, a single photo, or an emoji + title card
- Cover editor is a simple full-screen composable with a photo picker + color swatch row + title text field

**Data model (new entities):**
```kotlin
@Entity
data class DiaryCollection(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val subtitle: String? = null,           // e.g. "June – August 2025"
    val coverPhotoPath: String? = null,
    val coverColor: String? = null,         // hex
    val coverEmoji: String? = null,
    val type: String = "AUTO",              // AUTO | LETTER | MANUAL
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(primaryKeys = ["collectionId", "memoryId"])
data class CollectionMemoryCrossRef(
    val collectionId: String,
    val memoryId: String
)
```

**New screen:** `CollectionsScreen` — a bookshelf-style grid of diary covers (think: iOS Photos "albums" but warmer).

**Recommendation:** Do NOT show sad/anxious memories in collections — these diaries are purely celebratory. Sad memories remain accessible in the main feed but are excluded from collections by design. This matches your intent ("only good happy things").

---

## Phase C — Emotional Support

### Feature 7 · Quote or Note Matched to Mood
**What:** After saving a diary entry, surface a short quote from literature/film that resonates with the detected mood — not to fix the feeling, but to make the user feel seen.

**How it works:**
- Trigger: 2 seconds after a new memory is saved, a bottom sheet slides up
- Content: one quote (text + author/source) + a "save" button (saves to a `quotes_saved` local list)
- Quote selection:
  - **v1:** Curated local JSON — ~30 quotes per emotion tone, hand-picked (no API needed)
  - **v2:** Claude API — "Given this diary entry, suggest a short quote (under 30 words) from literature or film that would make this person feel less alone. Don't be preachy."
- The quote never appears if the emotion is NEUTRAL

**Local JSON structure (v1):**
```json
{
  "HAPPY": [{ "text": "...", "source": "Big Fish" }, ...],
  "SAD": [{ "text": "...", "source": "Anne of Green Gables" }, ...],
  ...
}
```

**Recommendation:** Start with local quotes — zero latency, works offline, and you can curate the exact vibe. The AI version is a Phase D upgrade. Avoid motivational-poster energy; pick literary, cinematic, genuine quotes.

---

### Feature 8 · Happy Memory Nudge When Sad or Stressed
**What:** When a user saves a SAD or ANXIOUS entry, the app quietly surfaces one past HAPPY or CALM memory as a "gentle reminder."

**How it works:**
- Trigger: emotionalTone == SAD || ANXIOUS on save
- Query: fetch a random HAPPY or CALM memory from >30 days ago (avoids recency bias)
- Display: a soft card overlay or separate bottom sheet — "A memory from back then…" — showing the photo + title
- User can tap to open the full memory, or swipe down to dismiss
- This only fires once per session (not on every sad entry)

**Recommendation:** The timing matters a lot. Fire this nudge 4–5 seconds after save, not immediately — give the person space to have just written the entry. Also respect a `lastNudgeAt` timestamp so it doesn't repeat if the user writes multiple sad entries in one session.

---

### Feature 2 · Mood Stickers
**What:** After saving an entry, suggest a set of small illustrated stickers based on the detected mood that the user can drop onto their memory card.

**How it works:**
- A sticker tray slides up (like an emoji keyboard but illustrated)
- Stickers are grouped by mood theme: soft/floaty for calm, sparkly for happy, cosy for sad, etc.
- User selects 0–3 stickers; positions are saved as `[stickerId, x%, y%]` relative to the card
- Stickers render as Compose `Image` overlays on the memory card at their saved positions
- No photo editing — stickers are UI overlays, not burned into the photo

**Data model changes:**
```kotlin
val stickers: String? = null   // JSON: [{"id":"sun_01","x":0.7,"y":0.2}, ...]
```

**Assets:** ~8–12 sticker SVGs per emotion tone = ~60 total. Can start with public domain or hand-drawn Lottie animations.

**Recommendation:** Keep sticker count small and each sticker charming. A few dozen beautifully crafted ones beat hundreds of mediocre ones. Lottie animated stickers (gentle loop, not jarring) would make this feel premium.

---

## Phase D — Special Entry Types

### Feature 6 · Letter to Future Self
**What:** A special capture mode where the user writes a letter sealed until a chosen future date. On that date, the app reveals it as a "letter from past you."

**How it works:**
- Entry point: a new FAB option in `CaptureScreen` — "Seal a letter"
- The compose UI looks like writing paper (different aesthetic — cream card, handwriting-style font, wax seal animation on save)
- User sets a reveal date (DatePicker — minimum 1 month out)
- Until the reveal date: the letter appears in the feed as a sealed envelope card (title hidden, contents hidden)
- On reveal date: WorkManager fires a notification — "A letter from past you just arrived." The card animates open (wax seal breaks).
- Letters land in their own `DiaryCollection` of type `LETTER` (see Feature 1)

**Data model changes:**
```kotlin
val entryType: String = "MEMORY"     // MEMORY | LETTER
val sealedUntil: Long? = null        // Unix timestamp for reveal
val isRevealed: Boolean = false
```

**Recommendation:** The reveal animation is the money moment — invest in it. A wax seal cracking open with a subtle particle effect is worth the effort. Also: letters should never surface in Feature 8 (happy nudges) until after they're revealed.

---

### Feature 9 · Sync a Song (Spotify Deep Link)
**What:** Based on the memory's mood and user's language preference, suggest a song and open it in Spotify.

**How it works:**
- Trigger: optional — user taps a music note icon on a saved memory card
- Song suggestion:
  - **v1:** Local curated map — 3–5 Spotify track URIs per emotion tone (e.g. `spotify:track:...`)
  - **v2:** Claude API — "This person just wrote a diary entry with this mood. Suggest one song title + artist that would fit. Respond in JSON only."
  - User can also set a language preference in Settings (English / Hindi / Tamil / other) — song suggestions filtered accordingly
- Tap → `Intent(Intent.ACTION_VIEW, Uri.parse("spotify:track:..."))` → opens Spotify
- Fallback: if Spotify not installed, open the web URL instead

**Recommendation:** For v1, hand-curate ~5 songs per mood per language. Quality over quantity. Consider: the user's preferred language affects not just lyrics but cultural resonance. A Tamil Carnatic piece for a calm memory hits differently than an English lo-fi track. This can be a genuinely distinctive feature if the curation is good.

---

## Phase E — Opening Ritual ("Calm Delight")

### Feature 10 · A Calm Delight Each Time the App Opens
**What:** Every time the user opens the app, one of five delightful moments plays before the diary loads. Skippable with a tap. Randomised.

**The five delights:**

| Delight | Description | Implementation |
|---|---|---|
| **Box Breathing** | 4-4-4-4 breathing guide with a gentle expanding/contracting circle | Compose Canvas animation + coroutine-based timer |
| **Sakura Petals** | Cherry blossom petals drift across the screen over a soft background | Lottie animation or custom Compose particle system |
| **Floating Horse** | A line-drawn horse is sketched on screen then floats away | Path drawing animation (PathEffect) + translationY anim |
| **Letter from the Past** | A random memory from 6–12 months ago shown as a handwritten letter card | Pull from Room DB, render in letter aesthetic |
| **Happy Memory** | A photo from a HAPPY/EXCITED memory fades in gently | Pull from Room DB filtered by tone, crossfade in |

**Flow:**
```
App launch → SplashActivity → pick random delight → play (max 8 seconds) →
tap anywhere or timer ends → slide up diary screen
```

**Skip mechanic:** Any tap on screen immediately skips to the diary. After 8 seconds it auto-advances.

**Settings toggle:** Let users disable the ritual entirely (Settings → "Opening moment: On/Off"). Default: On.

**Recommendation:**
- The horse is the most distinctive — lean into it. A path-drawn SVG horse that animates stroke-by-stroke and then gently floats off screen is charming and unusual.
- "Letter from the Past" and "Happy Memory" require enough diary history to be interesting. Show them only if the user has ≥10 memories and at least one from >30 days ago. Otherwise fall back to Sakura or Breathing.
- Don't let the ritual feel like loading time. The animation should start instantly (no DB query delay). Fetch the memory candidate in the background; show the animation first.

---

## Implementation Priority & Phasing

| Phase | Features | Why first |
|---|---|---|
| **A — Now** | #3 AI Sentiment, #4 Filter by Feeling | Everything else improves when emotion detection is accurate |
| **B — Next** | #5 Bookmark, #4 Filter | Quick wins, pure UI + small DB change |
| **C — Core v2** | #1 Collections, #6 Letter to Future Self | Signature features; need new data model |
| **D — Delight** | #10 Opening Ritual, #2 Mood Stickers | High polish, high joy — reward for engaged users |
| **E — Companion** | #7 Quotes, #8 Happy Nudge, #9 Song Sync | Emotional support layer; works best after corpus is large |

---

## Data Model Summary (all changes)

```kotlin
// Memory — additions
val emotionIntensity: Float = 0.5f
val secondaryEmotionalTone: String? = null
val isBookmarked: Boolean = false
val bookmarkedAt: Long? = null
val stickers: String? = null              // JSON string
val entryType: String = "MEMORY"          // MEMORY | LETTER
val sealedUntil: Long? = null
val isRevealed: Boolean = false

// New entities
DiaryCollection(id, title, subtitle, coverPhotoPath, coverColor, coverEmoji, type, createdAt)
CollectionMemoryCrossRef(collectionId, memoryId)
```

Room migration will be required from version 1 → 2. Use `Migration(1, 2)` — do not use `fallbackToDestructiveMigration()`.

---

## Open Questions (decide before building)

1. **Quote curation:** Who curates the local JSON quote library? If it's you, set aside ~2 hours to pick 30 quotes × 5 emotions = 150 quotes. This is high-leverage work.
2. **Spotify curation:** Same — hand-curate track URIs per mood × language. How many languages to support at launch?
3. **Horse drawing:** Is the horse a personal/cultural symbol or just whimsical? Worth knowing — it changes how to style it.
4. **AI API key management:** Claude API key — hardcoded for now (dev only), or will this ship to users? If shipping, need a backend proxy or user-provided key to avoid key exposure.
5. **Collection minimum size:** 3 memories per diary feels right but worth validating — do you want solo memories (e.g. one very meaningful day) to also get a cover?
