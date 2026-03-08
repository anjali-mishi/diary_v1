# Memory - Product Specification (MVP)

**App Name:** Memory  
**Platform:** Android (Kotlin + Jetpack Compose)  
**Storage:** Local-first (device only)  
**Design:** Warm/cozy scrapbook/diary feel

---

## Product Vision

A photo diary app that captures both visible moments (photos) and invisible moments (thoughts/feelings), preserving emotional context so memories remain meaningful over time.

**Target User:** Late Millennials to Early Gen Z (born ~1990-2005)

---

## The ONE Core Thing This MVP Must Do Brilliantly

**Capture memory and show it to user when they return.**

The simplest version:
1. User types/speaks/records → optionally attaches photo → presses enter → done
2. User comes back, sees it in chronological diary format
3. Index page to jump between memories
4. Delightful, minimal, emotionally inviting & comforting interface

---

## Core User Problem

- Users have thousands of photos but memories fade
- Photos lose meaning without the story/context behind them
- Not every meaningful moment has a photo
- Current apps show random memories without emotional awareness
- Browsing old photos decreases over time, memories fade

---

## User Behaviors

- Capture every moment and have feelings about it
- Look at photos and feel happy/reminded
- Use photo apps when: feeling anxious/sad, showing friends, nostalgia trips
- Want to document but writing feels like work
- Currently do the same in Instagram but has social pressure of maintaining appearance



## MVP Features

### 1. Memory Capture (Create)

**Entry Requirements:**
- At least ONE of: text content OR photo OR audio
- Both text and photo are optional, but at least one must be present

**Input Methods:**
- ✅ Text typing
- ✅ Voice-to-text (speech recognition)
- ✅ Audio recording (saved as audio file)
- ✅ Photo attachment (camera or gallery)

**User Flow:**
- No prompts or guided questions
- User writes/speaks freely at their own will
- Add photo anytime (can attach later to text-only entries)
- Press "Save" → Memory created

**Memory Types:**
- Text-only entries (invisible moments)
- Photo-only entries 
- Text + photo entries
- Audio + photo entries
- Mixed combinations

### 2. Memory Viewing (Read)

**Diary View:**
- Chronological timeline (newest first or oldest first - TBD)
- Like flipping through a physical diary
- Each memory displays:
  - Date & time stamp
  - Text content (if any)
  - Photo (if attached)
  - Audio player (if recorded)
  - Emotional color indicator (auto-detected)

**Index View:**
- Accessible browsing mechanism to jump between memories
- Like a book's table of contents
- Quick navigation through time periods

**Visual Treatment:**
- Different moods have different colors (auto-detected from text/context)
- Warm, cozy, scrapbook aesthetic
- Emotionally inviting interface
- Override above when Figma or stitch designs are connected

### 3. Memory Management (Edit/Delete)

**Editing:**
- ✅ User can edit any past memory
- ✅ User can delete any memory
- ✅ User can add photos to text-only memories later

### 4. Emotional Safety

**Hide Memories:**
- User can manually hide specific memories
- Hidden memories don't show in main feed
- User can unhide later if desired

### 5. Privacy & Security

**Access Control:**
- No sharing features
- If device is locked → app is not accessible
- Fully private, local storage only

---

## Technical Specifications

### Tech Stack
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Architecture:** Local-first (no backend/cloud)
- **Storage:** Room Database (local SQLite) for metadata, diary logs & memory context, and local file system for photos/audio
- **Speech-to-text:** Android SpeechRecognizer API
- **Audio Recording:** MediaRecorder API

### Data Model

```kotlin
// Memory Entity
data class Memory(
    val id: String,
    val timestamp: Long,
    val title: String, // User-defined or auto-generated from first line of text
    val textContent: String? = null,
    val audioFilePath: String? = null,
    val photoFilePath: String? = null, // Reference to phone storage path
    val emotionalTone: EmotionalTone? = null, // auto-detected via keyword matching
    val isHidden: Boolean = false,
    val lastViewedAt: Long? = null, // For "pick up where you left off"
    val createdAt: Long,
    val updatedAt: Long
)

// Emotional tone (for color coding)
enum class EmotionalTone {
    HAPPY,      // Yellow/Gold
    SAD,        // Blue
    ANXIOUS,    // Purple
    CALM,       // Green
    EXCITED,    // Orange
    NEUTRAL     // Beige/Cream
}
```

### Data Organization
- **Unlimited entries per day**
- Flat chronological structure
- No albums or collections for MVP

---

## Out of Scope for MVP

### Explicitly NOT Building:
- ❌ Social features (sharing with friends)
- ❌ Collaborative journals
- ❌ Print/export options
- ❌ Advanced search functionality
- ❌ Themes/customization options
- ❌ Streaks/gamification
- ❌ Reminders/notifications
- ❌ Video support
- ❌ Integration with other apps
- ❌ Templates for entries
- ❌ Multi-photo per entry (single photo only for MVP)
- ❌ Location tagging
- ❌ Weather data
- ❌ AI/LLM features (no auto-generation, no AI suggestions)
- ❌ Manual mood tagging by user
- ❌ Pattern insights ("You were happiest when...")
- ❌ Memory resurfacing notifications ("On this day")
- ❌ Filtered views by mood/emotion

---

## User Experience Flow

### First-Time User (Empty State)
**Flow:** Drop user straight into "Add first memory" screen
- No onboarding tutorial
- No examples or prompts
- Clean slate to start journaling immediately

### Adding a Memory
1. Tap "+" primary floating actionbutton (always accessible)
2. See input options: Text field / Voice button / Audio record / Camera/Gallery
3. User adds content (at least one type required)
4. Optional: Add/edit memory title (auto-suggested from first line if text exists)
5. Optional: Attach photo
6. Tap "Save"
7. Auto-detect emotional tone from text (background keyword matching)
8. Return to memory view at this new memory
9. Match background color to emotional tone auto-detected from text

### Browsing Memories
1. Open app → Resume where you left off (last viewed memory) 
2. Swipe up/down OR use bottom slider to navigate through memories divided by themes (like Kindle)
3. Timeline scrubber at bottom shows position in memory history
4. Tap "Index" button → See date list with memory titles (Rename index button to My Diaries)
5. Tap date → Jump to first memory of that day
6. Tap specific memory title → Jump directly to that memory
7. Long-press memory → Options: Edit / Delete / Hide
8. Each memory page is full screen view with 70% of screen area to attached photo (if photo missing use animated gradient of contrasting color ball on a glass blurr with primary color picked from emotion tone) on top and text content below (Instagram style)
9. Each memory has consistent UI and text is always black

### Viewing Memory with Audio
1. Navigate to memory with audio recording
2. Photo/text content displayed at top
3. Audio player fixed at bottom (Spotify-style)
4. Play/pause, scrubber, timestamp visible
5. Can read/view content while audio plays

### Editing a Memory
1. Tap memory → See detail view
2. Tap "Edit" button
3. Modify text / add-remove photo / re-record audio
4. Save changes

---

## Design Principles

### Visual Design
- **Aesthetic:** Warm/cozy scrapbook/diary feel
- **Colors:** Soft, muted pastels with emotional color coding
- **Typography:** Handwriting-inspired but legible fonts
- **Spacing:** Generous whitespace, not cluttered
- **Interactions:** Minimal, delightful micro-interactions

### Emotional Design
- **Inviting:** Feels safe to share feelings
- **Comforting:** Like talking to a trusted friend
- **Non-judgmental:** No pressure, no streaks, no guilt
- **Personal:** Feels like "yours" not a corporate product

### Interaction Design
- **Fast:** Capture in <30 seconds possible
- **Flexible:** No time limit, user's free will
- **Forgiving:** Easy to edit/delete
- **Discoverable:** Features are obvious without tutorial

---

## Success Metrics (MVP)

**Primary Goal:** Developer (you) personally uses it for X days

**Success Indicators:**
- You return to the app regularly
- You capture different types of memories (text, photo, audio)
- You browse old memories and feel emotional connection
- Interface feels delightful and inviting
- No friction in capture flow

---

## Roadmap (Post-MVP / V2)

### Future Features:
1. **Auto-tagging emotional values** from context for easier searching
2. **Shuffle memories** - random memory browsing
3. **Auto-surface for moods** - "Show me happy memories when sad"
4. **Search functionality** - find by keyword, date, emotion
5. **Pattern insights** - "You smile most when..."
6. **Memory resurfacing** - gentle "On this day" prompts
7. **Export/backup** - save memories outside app
8. **Themes** - customize visual appearance
9. **Multi-photo support** - photo galleries per memory

---

## Technical Decisions

### 1. Chronological Navigation
**Like Kindle reading experience:**
- User picks up where they left off (remembers last viewed memory)
- Bottom slider to flip through past memories
- Swipe/gesture to move between memories
- Timeline scrubber at bottom for quick navigation

### 2. Emotional Tone Detection
**As simple as possible:**
- Keyword matching approach
- Happy: "happy", "joy", "excited", "love", "amazing", "great", "wonderful"
- Sad: "sad", "cry", "miss", "lost", "hurt", "pain"
- Anxious: "anxious", "worry", "stress", "nervous", "scared", "afraid"
- Calm: "calm", "peace", "relax", "quiet", "serene"
- Excited: "excited", "thrilled", "can't wait", "pumped"
- Neutral: default if no keywords detected

### 3. Audio Playback
**Like Spotify song playing screen:**
- Photo/notes displayed at top of screen
- Audio player fixed at bottom
- Play/pause, scrubber, timestamp
- Full-screen memory view with integrated player

### 4. Photo Storage
**Direct reference to phone storage:**
- No compression needed since locally fetched
- Store file path reference in database
- Photos remain in original quality
- Option to compress only if user runs into storage issues (future consideration)

### 5. Index Navigation
**List of dates with memory titles:**
```
📅 February 15, 2026
   └ "Morning coffee thoughts"
   └ "Call with Mom"

📅 February 14, 2026
   └ "Valentine's Day dinner"

📅 February 13, 2026
   └ "Work presentation nerves"
   └ "Evening walk"
```
- Each memory needs a "key title" (user-defined or auto-generated from first line)
- Tap date → jump to first memory of that day
- Tap specific memory title → jump directly to that memory

---

## Next Steps

1. ✅ Finalize product spec (this document)
2. ⏭️ Create UI/UX wireframes
3. ⏭️ Design visual mockups (warm/cozy aesthetic)
4. ⏭️ Set up Kotlin + Jetpack Compose project structure
5. ⏭️ Implement core features (Create → Read → Edit → Delete)
6. ⏭️ Test with real usage over X days
7. ⏭️ Iterate based on personal experience
