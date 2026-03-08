# Memory 📔
### *Capturing the visible and invisible moments.*

**Memory** is a local-first, emotionally-aware journaling app designed to preserve the full context of your life. Unlike standard gallery apps, Memory helps you capture not just what happened (photos), but how it felt (thoughts and audio).

---

## **✨ Core Features**
- **📸 Multimedia Capture:** Seamlessly blend text, high-quality photos, and voice recordings into a single memory.
- **🎭 Emotion Detection:** Automatically detects the emotional tone of your entries (Happy, Sad, Anxious, Calm, Excited) using keyword analysis.
- **🎨 Dynamic Aesthetics:** The app’s "scrapbook" interface changes color and mood based on the emotion of the memory you’re viewing.
- **🔒 Privacy First:** 100% local storage. Your memories never leave your device. No cloud, no tracking, no accounts.
- **📖 Narrative Timeline:** A chronological, Kindle-style reading experience that lets you flip through your life's story.

---

## **🛠️ Tech Stack**
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Modern, declarative UI)
- **Database:** Room (SQLite) for high-performance local metadata storage
- **Architecture:** MVVM (Model-View-ViewModel) with Clean Architecture principles
- **Dependency Injection:** Hilt
- **Media:** MediaRecorder API for audio & CameraX/Gallery integration for photos

---

## **🚀 Getting Started**
### **Prerequisites**
- Android Studio Ladybug (or newer)
- Android SDK 34+
- A physical Android device or Emulator

### **Installation**
1. **Clone the repository:**
   ```bash
   git clone https://github.com/anjali-mishi/diary_v1.git
   ```
2. **Open in Android Studio:**
   File > Open > Select the `diary-v1` folder.
3. **Build & Run:**
   Select your device and press the **Run** button (green play icon).

---

## **📝 Product Vision**
We live in an age where we have thousands of photos, but the stories behind them often fade. **Memory** is built for those who want to document their "invisible moments"—the quiet thoughts, the specific feelings, and the sounds of a moment—so they remain meaningful years from now.

---

## **🛤️ Roadmap**
- [x] Multimedia entry support (Text/Photo/Audio)
- [x] Emotional tone auto-detection
- [x] Chronological Index view
- [ ] Memory search & filtering by emotion
- [ ] "On this day" memory resurfacing
- [ ] Encrypted local backups

---

*Developed with ❤️ as a "Design in Public" project.*
