<div align="center">
  <img src="art/logo.jpg" alt="Unsent Messenger Logo" width="180" style="border-radius: 28px;" />
  <h1>Unsent Messenger</h1>
  <p><strong>The Anti-Unsend Countermeasure & AI Utility for Android</strong></p>

  <p>
    <a href="https://github.com/Ambatucode/unsent-messenger/actions/workflows/build-apk.yml"><img src="https://github.com/Ambatucode/unsent-messenger/actions/workflows/build-apk.yml/badge.svg" alt="Build Status" /></a>
    <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="License: MIT" /></a>
    <a href="https://github.com/Ambatucode/unsent-messenger/releases/tag/latest"><img src="https://img.shields.io/github/v/release/Ambatucode/unsent-messenger?label=Latest%20APK&color=brightgreen" alt="Latest Release" /></a>
  </p>
</div>

---

An Android counter-tool built with **Kotlin**, **Jetpack Compose (Material 3)**, and **Room Database** that captures and logs incoming Messenger notifications locally on your device, allowing you to view messages and photos even after the sender has clicked **"Unsend for everyone"**, plus an on-demand **Gemini AI Assistant & Form Solver**!

---

## 📲 Instant Download & Install (No PC Required!)

You can download the latest compiled `.apk` directly to your Android phone from GitHub:

👉 **[Download Latest APK from GitHub Releases](https://github.com/Ambatucode/unsent-messenger/releases/tag/latest)**

*Every time new code is pushed to this repository, GitHub Actions automatically compiles and updates the `.apk`.*

---

## ✨ Features

- ⚡ **Real-time Capture**: Intercepts notifications from Facebook Messenger (`com.facebook.orca`), Messenger Lite, Instagram Direct, and WhatsApp.
- 🚫 **Unsent Message & Photo Detection**: Automatically detects and highlights messages and images retracted by the sender.
- 🤖 **On-Demand Gemini AI Assistant**: Built-in AI tool to answer forms, solve questions, and draft smart replies with your own free Google AI Studio API key.
- 🖼️ **In-App Photo Viewer & On-Demand Reveal**: Full-screen zoomable photo viewer with on-demand tap-to-reveal privacy.
- 📱 **Cross-Platform Sender Support**: Works when the sender is using **iOS (iPhone/iPad)**, Android, Mac, or Windows Web.
- 🔒 **100% Private & On-Device**: All chat logs and images are stored locally in an offline SQLite/Room database. No servers, no tracking, 0 cloud uploads.
- 🔋 **Reboot & Battery Resilient**: Auto-rebinds upon device reboot (`BOOT_COMPLETED`) with battery optimization exemption support.
- 🧪 **Built-in Diagnostic Tool**: Simulate test unsent messages and photos from Settings without needing a second phone.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 2.1
- **UI Framework**: Jetpack Compose (Material 3)
- **Database**: AndroidX Room (SQLite) with KSP
- **AI Integration**: Google Gemini 1.5 Flash REST API
- **Async & Reactive**: Kotlin Coroutines & `Flow`
- **Core Service**: Android `NotificationListenerService` (`android.permission.BIND_NOTIFICATION_LISTENER_SERVICE`)
- **CI/CD Pipeline**: GitHub Actions (`assembleDebug` on push)
- **Navigation**: Navigation Compose

---

## ⚙️ First-Time Phone Setup

When you open the app on your phone:
1. **Grant Notification Access**:
   - Tap **Enable Notification Access** -> Toggle **ON** for `Unsent Messenger`.
2. **Disable Battery Optimization**:
   - Tap **Ignore Battery Optimization** so Android won't shut down the service in the background.
3. **Activate AI Assistant (Optional)**:
   - Tap the **✨ AI icon** in the top bar -> Paste your free [Google AI Studio Gemini API Key](https://aistudio.google.com/app/apikey).
