# Pre-Launch Checklist

## Blockers

- [x] **Release signing config** — `signingConfigs { release }` in `build.gradle.kts`, `isMinifyEnabled = true`, `isShrinkResources = true`, R8 ProGuard rules for Room entities + line numbers. Keystore credentials read from `local.properties`. ⚠️ User must generate keystore and add credentials to `local.properties` before release build.
- [x] **Lock orientation to portrait** — `android:screenOrientation="portrait"` added to `AndroidManifest.xml` Activity

## High Priority

- [x] **Mic permission on launch + denial feedback** — `RECORD_AUDIO` requested upfront in `MainActivity.onCreate()`. If denied and user taps mic in CaptureScreen, snackbar shown with "Settings" action to open app settings.
- [ ] **Test on Android 15+ device/emulator** — verify photo picker, permissions, audio recording under latest scoped-storage rules

## Medium Priority

- [x] **Storage-full error feedback** — `CaptureViewModel.saveMemory()` accepts `onError` callback. Photo copy failure surfaces snackbar. `AudioRecorder.stopRecording()` cleans up corrupt files on failure.
- [x] **Max recording duration** — 5-minute hard cap via `setMaxDuration()` on MediaRecorder + UI-side auto-stop with snackbar
- [x] **`allowBackup`** — kept `true` (user decision: diary data survives uninstall/reinstall)
- [x] **Enable schema export** — `exportSchema = true` in `@Database`, KSP schema location configured, v3 JSON generated at `app/schemas/`

## Low Priority

- [x] **Compress photos** — `Bitmap.compress(JPEG, 80)` with `inSampleSize` downsampling (max 2048px longest edge) for OOM safety
- [x] **Orphaned audio cleanup** — on app start, `MainActivity` scans `audio_memos/` and deletes files not referenced in DB
- [ ] **Test low-RAM devices** — verify scroll performance with 50+ entries on 3-4GB RAM emulator
- [x] **`applicationId` rename** — changed to `com.memory.diary`
