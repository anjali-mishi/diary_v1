# Memory — A Personal Diary App

A warm, emotion-aware diary app for Android. Capture memories as text, photos, or voice memos. No cloud, no account, no tracking — everything lives on your device.

---

## Features

- **Text, photo, and voice memo capture** — rich entries combining all three
- **Emotion detection** — automatically detects the emotional tone of your entry (happy, sad, calm, anxious, excited, neutral) from your words
- **Speech-to-text** — speak your thoughts directly into the text field
- **Bento-style diary feed** — memories displayed as polaroid-style cards
- **Index screen with dial scrubber** — navigate memories by emotional tone using a custom dial knob
- **Memory detail view** — full-screen view with parallax background tied to the emotion of the entry
- **Edit & delete** — full control over your entries
- **Local-first** — all data stored on-device using Room (SQLite). Nothing is ever uploaded.
- **Onboarding** — emotion-first welcome screen on first launch

---

## Tech Stack

| Layer | Tech |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose (Material 3) |
| Navigation | Navigation Compose |
| Database | Room (SQLite, schema v3) |
| Audio | MediaRecorder / MediaPlayer |
| Images | Coil |
| Architecture | MVVM, unidirectional data flow |
| DI | Manual `ViewModelProvider.Factory` (no Hilt) |

---

## Project Structure

```
app/src/main/java/com/example/MyApplication/
├── MainActivity.kt               # Single activity, permission handling, orphaned audio cleanup
├── data/
│   ├── dao/MemoryDao.kt          # Room DAO
│   ├── database/AppDatabase.kt   # Room DB (v3)
│   ├── model/Memory.kt           # Memory entity
│   └── repository/MemoryRepository.kt
├── ui/
│   ├── AppNavigation.kt          # NavHost, ViewModel wiring, route-aware transitions
│   ├── DiaryScreen.kt            # Home feed + BentoMemoryCard variants
│   ├── CaptureScreen.kt          # Capture + edit mode
│   ├── IndexScreen.kt            # Dial scrubber + polaroid list
│   ├── MemoryDetailScreen.kt     # Full-screen detail view
│   ├── WelcomeScreen.kt          # First-launch onboarding
│   ├── DialKnob.kt               # Custom Canvas dial component
│   └── Shared.kt                 # Shared helpers (appleShadow, emotionColor, etc.)
└── util/
    ├── AudioRecorder.kt          # MediaRecorder wrapper (5-min cap)
    ├── AudioPlayer.kt            # MediaPlayer wrapper
    ├── EmotionDetector.kt        # Keyword-based emotion detection
    ├── ImageStorage.kt           # Photo copy + JPEG compression
    ├── SpeechRecognizerManager.kt# Android SpeechRecognizer wrapper
    └── OnboardingPrefs.kt        # First-launch flag
```

---

## How to Build

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17+ (use Android Studio's bundled JBR)
- Android SDK 36

### Debug build
```bash
./gradlew assembleDebug
./gradlew installDebug   # install on connected device/emulator
```

### Release build
1. Generate a keystore (one-time):
   ```bash
   keytool -genkey -v -keystore release-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias diary-release
   ```

2. Add credentials to `local.properties` (never commit this file):
   ```
   RELEASE_STORE_FILE=../release-keystore.jks
   RELEASE_STORE_PASSWORD=<your password>
   RELEASE_KEY_ALIAS=diary-release
   RELEASE_KEY_PASSWORD=<your password>
   ```

3. Build:
   ```bash
   ./gradlew assembleRelease
   ```
   Output: `app/build/outputs/apk/release/app-release.apk`

---

## Design System

- **Primary colour:** `#000000` at 88% opacity (SoftBlack)
- **Fonts:** Trocchi (display), SF Pro Rounded (body)
- **Emotion colours:** drive card tinting, waveform colour, and screen background gradients
- **Aesthetic:** warm paper / scrapbook feel
- Full spec in [`design.md`](design.md)

---

## Distribution

The app is distributed as a sideloaded APK (not on Google Play Store).

Download the latest release from the [Releases](../../releases) page.

**Install instructions:**
1. Download `app-release.apk` on your Android phone
2. Tap the file — Android will warn "Install from unknown sources"
3. Enable installs from your browser/file manager in Settings
4. If Play Protect blocks it, tap **More details → Install anyway**

---

## Privacy

- No internet connection required
- No data leaves your device
- No analytics, no ads, no account
- Microphone used only for voice memos recorded within the app

---

## Roadmap

- [ ] iOS + web via React Native rewrite
- [ ] Export memories
- [ ] Seekable audio progress bar
- [ ] In-app update check

---

## License

MIT — see [LICENSE](LICENSE)
