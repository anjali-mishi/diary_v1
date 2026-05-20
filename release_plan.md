# Release Plan — Memory Diary v1.0

**Distribution:** Sideload via website (APK download), not Play Store.
**Target:** Android only, free, local-only data.

---

## How to use this file

Tasks are sequenced — finish one before starting the next. After completing each task, **Claude must explicitly prompt the user** with: _"Task X done. Ready to start Task Y?"_ — do NOT auto-continue to the next task. The user picks the pace.

Mark tasks `[x]` when complete. Add notes inline as needed.

---

## Phase 1: Keystore & Signing

- [x] **T1. Generate release keystore**
  - Run `keytool -genkey -v -keystore release-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias diary-release` from project root
  - Save passwords in password manager (losing them = users must uninstall to update)
  - Verify `release-keystore.jks` exists at project root and is gitignored

- [x] **T2. Configure local.properties**
  - Add `RELEASE_STORE_FILE`, `RELEASE_STORE_PASSWORD`, `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD` to `local.properties`

---

## Phase 2: Build & Verify

- [x] **T3. Build release APK**
  - Run `./gradlew assembleRelease`
  - Output: `app/build/outputs/apk/release/app-release.apk`
  - Fix any R8 / ProGuard errors that surface

- [x] **T4. Test release APK on emulator**
  - Install via `adb install` or drag-and-drop
  - Full flow: first-launch mic prompt → capture (text + photo + audio) → view → edit → delete → index → dial knob → 5-min recording cap → kill+reopen persistence

- [ ] **T5. Test release APK on real device**
  - Transfer APK to phone, enable "Install unknown apps", install
  - Repeat full flow from T4
  - Check mic quality, photo picker, scroll performance
  - Specifically test on Android 15+ if available

---

## Phase 3: Distribution Prep

- [x] **T6. Write privacy policy**
  - Cover: no data collection, mic permission purpose, no analytics/cloud, contact email
  - Host on GitHub Pages, Notion public page, or memory-site

- [x] **T7. Generate APK checksum**
  - `ba8959e5e3e4550277871fd707d039deb874cbd36264c9e033d3ee08b6a3530d`

- [ ] **T8. Take screenshots on phone**
  - 3–5 screenshots covering: welcome, diary, capture, detail, index
  - Use real phone (not emulator) for authentic frame

- [x] **T9. Decide hosting**
  - Option A: GitHub Releases only (simplest, free, trusted)
  - Option B: Memory-site hosts marketing, GitHub Releases hosts APK (recommended)
  - Option C: Self-host APK on memory-site

---

## Phase 4: Website

- [ ] **T10. Add release page to memory-site**
  - Hero + app description
  - Screenshots gallery
  - Download button → APK link
  - Install instructions with screenshots (enabling "unknown sources" is the #1 friction point)
  - Privacy policy link
  - "Android only" + "Data stored on device" + "Free, no ads, no account" disclaimers
  - Version + changelog
  - Contact email
  - SHA-256 checksum

- [ ] **T11. Deploy memory-site updates**
  - Verify download link works end-to-end

---

## Phase 5: Launch

- [ ] **T12. Upload APK to GitHub Releases**
  - Tag `v1.0.0`, attach `app-release.apk`, paste changelog

- [ ] **T13. End-to-end sanity check**
  - From your own phone, visit your website → download APK → install → use it
  - Confirms the whole user journey works

- [ ] **T14. Announce**
  - LinkedIn post (vibe-coding documentation angle)
  - Anywhere else relevant

---

## Reminders

- Don't change `applicationId` after release — it's the app's identity for all future updates
- Don't lose the keystore — back it up somewhere safe (encrypted cloud, USB, etc.)
- Sideloaded users won't get auto-updates — plan for how you'll notify them of new versions later (in-app version check is a v1.1 candidate)
